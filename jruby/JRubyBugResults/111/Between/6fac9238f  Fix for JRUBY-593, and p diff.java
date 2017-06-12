diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index c78a202794..ff1deb325e 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1160 +1,1160 @@
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
-        return ((RubyString) callMethod(getRuntime().getCurrentContext(), "to_s")).toString();
+        return callMethod(getRuntime().getCurrentContext(), "to_s").toString();
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
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
         /* FIXME: finalizer should be dupped here */
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
         return callMethod(context, rubyclass, name, args, callType, Block.NULL_BLOCK);
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
 
     /** rb_eval
      *
      */
     public IRubyObject eval(Node n) {
         //return new EvaluationState(getRuntime(), this).begin(n);
         // need to continue evaluation with a new self, so save the old one (should be a stack?)
         return EvaluationState.eval(getRuntime().getCurrentContext(), n, this, Block.NULL_BLOCK);
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
 
     /** rb_obj_as_string
      */
     public RubyString objAsString() {
         IRubyObject str;
         if(this instanceof RubyString) {
             return (RubyString)this;
         }
         str = this.callMethod(getRuntime().getCurrentContext(),"to_s");
         if(!(str instanceof RubyString)) {
             str = anyToString();
         }
         return (RubyString)str;
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
         if (block.isGiven()) {
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
 
         ISourcePosition savedPosition = context.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
             return EvaluationState.eval(context, getRuntime().parse(src.toString(), file, context.getCurrentScope()), this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
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
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     public IRubyObject rbClone() {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getSingletonClassClone());
         clone.setTaint(isTaint());
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
         
         dup.initCopy(this);
 
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
diff --git a/src/org/jruby/evaluator/EvaluationState.java b/src/org/jruby/evaluator/EvaluationState.java
index e9f5c9faa4..503c5d8162 100644
--- a/src/org/jruby/evaluator/EvaluationState.java
+++ b/src/org/jruby/evaluator/EvaluationState.java
@@ -1,1680 +1,1680 @@
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
  * Copyright (C) 2006 Charles Oliver Nutter <headius@headius.com>
  * Copytight (C) 2006-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.IRuby;
 import org.jruby.MetaClass;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyKernel;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.ast.AliasNode;
 import org.jruby.ast.ArgsCatNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ArgsPushNode;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.AttrAssignNode;
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BeginNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.BinaryOperatorNode;
 import org.jruby.ast.BlockNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.BreakNode;
 import org.jruby.ast.CallNode;
 import org.jruby.ast.CaseNode;
 import org.jruby.ast.ClassNode;
 import org.jruby.ast.ClassVarAsgnNode;
 import org.jruby.ast.ClassVarDeclNode;
 import org.jruby.ast.ClassVarNode;
 import org.jruby.ast.Colon2Node;
 import org.jruby.ast.Colon3Node;
 import org.jruby.ast.ConstDeclNode;
 import org.jruby.ast.ConstNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DRegexpNode;
 import org.jruby.ast.DStrNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.DVarNode;
 import org.jruby.ast.DXStrNode;
 import org.jruby.ast.DefinedNode;
 import org.jruby.ast.DefnNode;
 import org.jruby.ast.DefsNode;
 import org.jruby.ast.DotNode;
 import org.jruby.ast.EnsureNode;
 import org.jruby.ast.EvStrNode;
 import org.jruby.ast.FCallNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FlipNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.ForNode;
 import org.jruby.ast.GlobalAsgnNode;
 import org.jruby.ast.GlobalVarNode;
 import org.jruby.ast.HashNode;
 import org.jruby.ast.IfNode;
 import org.jruby.ast.InstAsgnNode;
 import org.jruby.ast.InstVarNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.LocalVarNode;
 import org.jruby.ast.Match2Node;
 import org.jruby.ast.Match3Node;
 import org.jruby.ast.MatchNode;
 import org.jruby.ast.ModuleNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NewlineNode;
 import org.jruby.ast.NextNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeTypes;
 import org.jruby.ast.NotNode;
 import org.jruby.ast.NthRefNode;
 import org.jruby.ast.OpAsgnNode;
 import org.jruby.ast.OpAsgnOrNode;
 import org.jruby.ast.OpElementAsgnNode;
 import org.jruby.ast.OptNNode;
 import org.jruby.ast.OrNode;
 import org.jruby.ast.RegexpNode;
 import org.jruby.ast.RescueBodyNode;
 import org.jruby.ast.RescueNode;
 import org.jruby.ast.ReturnNode;
 import org.jruby.ast.RootNode;
 import org.jruby.ast.SClassNode;
 import org.jruby.ast.SValueNode;
 import org.jruby.ast.SplatNode;
 import org.jruby.ast.StrNode;
 import org.jruby.ast.SuperNode;
 import org.jruby.ast.SymbolNode;
 import org.jruby.ast.ToAryNode;
 import org.jruby.ast.UndefNode;
 import org.jruby.ast.UntilNode;
 import org.jruby.ast.VAliasNode;
 import org.jruby.ast.VCallNode;
 import org.jruby.ast.WhenNode;
 import org.jruby.ast.WhileNode;
 import org.jruby.ast.XStrNode;
 import org.jruby.ast.YieldNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.JumpException.JumpType;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ForBlock;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.KCode;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class EvaluationState {
     public static IRubyObject eval(ThreadContext context, Node node, IRubyObject self, Block block) {
         try {
             return evalInternal(context, node, self, block);
         } catch (StackOverflowError sfe) {
             throw context.getRuntime().newSystemStackError("stack level too deep");
         }
     }
     
     /* Something like cvar_cbase() from eval.c, factored out for the benefit
      * of all the classvar-related node evaluations */
     private static RubyModule getClassVariableBase(ThreadContext context, IRuby runtime) {
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
 
     private static IRubyObject evalInternal(ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         IRuby runtime = context.getRuntime();
         
         bigloop: do {
             if (node == null) return runtime.getNil();
 
             switch (node.nodeId) {
             case NodeTypes.ALIASNODE: {
                 AliasNode iVisited = (AliasNode) node;
     
                 if (context.getRubyClass() == null) {
                     throw runtime.newTypeError("no class to make alias");
                 }
     
                 context.getRubyClass().defineAlias(iVisited.getNewName(), iVisited.getOldName());
                 context.getRubyClass().callMethod(context, "method_added", runtime.newSymbol(iVisited.getNewName()));
     
                 return runtime.getNil();
             }
             case NodeTypes.ANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return result;
                 node = iVisited.getSecondNode();
                 continue bigloop;
             }
             case NodeTypes.ARGSCATNODE: {
                 ArgsCatNode iVisited = (ArgsCatNode) node;
     
                 IRubyObject args = evalInternal(context, iVisited.getFirstNode(), self, aBlock);
                 IRubyObject secondArgs = splatValue(evalInternal(context, iVisited.getSecondNode(), self, aBlock));
                 RubyArray list = args instanceof RubyArray ? (RubyArray) args : runtime.newArray(args);
     
                 return list.concat(secondArgs);
             }
             case NodeTypes.ARGSPUSHNODE: {
                 ArgsPushNode iVisited = (ArgsPushNode) node;
                 
                 RubyArray args = (RubyArray) evalInternal(context, iVisited.getFirstNode(), self, aBlock).dup();
                 return args.append(evalInternal(context, iVisited.getSecondNode(), self, aBlock));
             }
                 //                case NodeTypes.ARGSNODE:
                 //                EvaluateVisitor.argsNodeVisitor.execute(this, node);
                 //                break;
                 //                case NodeTypes.ARGUMENTNODE:
                 //                EvaluateVisitor.argumentNodeVisitor.execute(this, node);
                 //                break;
             case NodeTypes.ARRAYNODE: {
                 ArrayNode iVisited = (ArrayNode) node;
                 IRubyObject[] array = new IRubyObject[iVisited.size()];
                 int i = 0;
                 for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
                     Node next = (Node) iterator.next();
     
                     array[i++] = evalInternal(context, next, self, aBlock);
                 }
     
                 return runtime.newArrayNoCopy(array);
             }
                 //                case NodeTypes.ASSIGNABLENODE:
                 //                EvaluateVisitor.assignableNodeVisitor.execute(this, node);
                 //                break;
             case NodeTypes.ATTRASSIGNNODE: {
                 AttrAssignNode iVisited = (AttrAssignNode) node;
     
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self, aBlock);
                 IRubyObject[] args = setupArgs(context, iVisited.getArgsNode(), self);
                 
                 assert receiver.getMetaClass() != null : receiver.getClass().getName();
                 
                 // If reciever is self then we do the call the same way as vcall
                 CallType callType = (receiver == self ? CallType.VARIABLE : CallType.NORMAL);
     
                 receiver.callMethod(context, iVisited.getName(), args, callType);
                 
                 return args[args.length - 1]; 
             }
             case NodeTypes.BACKREFNODE: {
                 BackRefNode iVisited = (BackRefNode) node;
                 IRubyObject backref = context.getBackref();
                 switch (iVisited.getType()) {
                 case '~':
                     return backref;
                 case '&':
                     return RubyRegexp.last_match(backref);
                 case '`':
                     return RubyRegexp.match_pre(backref);
                 case '\'':
                     return RubyRegexp.match_post(backref);
                 case '+':
                     return RubyRegexp.match_last(backref);
                 }
                 break;
             }
             case NodeTypes.BEGINNODE: {
                 BeginNode iVisited = (BeginNode) node;
     
                 node = iVisited.getBodyNode();
                 continue bigloop;
             }
             case NodeTypes.BIGNUMNODE: {
                 BignumNode iVisited = (BignumNode) node;
                 return RubyBignum.newBignum(runtime, iVisited.getValue());
             }
                 //                case NodeTypes.BINARYOPERATORNODE:
                 //                EvaluateVisitor.binaryOperatorNodeVisitor.execute(this, node);
                 //                break;
                 //                case NodeTypes.BLOCKARGNODE:
                 //                EvaluateVisitor.blockArgNodeVisitor.execute(this, node);
                 //                break;
             case NodeTypes.BLOCKNODE: {
                 BlockNode iVisited = (BlockNode) node;
     
                 IRubyObject result = runtime.getNil();
                 for (Iterator iter = iVisited.iterator(); iter.hasNext();) {
                     result = evalInternal(context, (Node) iter.next(), self, aBlock);
                 }
     
                 return result;
             }
             case NodeTypes.BLOCKPASSNODE:
                 assert false: "Call nodes and friends deal with this";
             case NodeTypes.BREAKNODE: {
                 BreakNode iVisited = (BreakNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
     
                 JumpException je = new JumpException(JumpException.JumpType.BreakJump);
     
                 je.setValue(result);
     
                 throw je;
             }
             case NodeTypes.CALLNODE: {
                 CallNode iVisited = (CallNode) node;
     
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self, aBlock);
                 IRubyObject[] args = setupArgs(context, iVisited.getArgsNode(), self);
                 
                 assert receiver.getMetaClass() != null : receiver.getClass().getName();
 
                 Block block = getBlock(context, self, aBlock, iVisited.getIterNode());
                 
                 // No block provided lets look at fast path for STI dispatch.
                 if (!block.isGiven()) {
                     RubyModule module = receiver.getMetaClass();
                     if (module.index != 0) {
                          return receiver.callMethod(context, module,
                                 runtime.getSelectorTable().table[module.index][iVisited.index],
                                 iVisited.getName(), args, CallType.NORMAL);
                     }                    
                 }
                     
                 try {
                     while (true) {
                         try {
                             return receiver.callMethod(context, iVisited.getName(), args,
                                     CallType.NORMAL, block);
                         } catch (JumpException je) {
                             switch (je.getJumpType().getTypeId()) {
                             case JumpType.RETRY:
                                 // allow loop to retry
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
             case NodeTypes.CASENODE: {
                 CaseNode iVisited = (CaseNode) node;
                 IRubyObject expression = null;
                 if (iVisited.getCaseNode() != null) {
                     expression = evalInternal(context, iVisited.getCaseNode(), self, aBlock);
                 }
     
                 context.pollThreadEvents();
     
                 IRubyObject result = runtime.getNil();
     
                 Node firstWhenNode = iVisited.getFirstWhenNode();
                 while (firstWhenNode != null) {
                     if (!(firstWhenNode instanceof WhenNode)) {
                         node = firstWhenNode;
                         continue bigloop;
                     }
     
                     WhenNode whenNode = (WhenNode) firstWhenNode;
     
                     if (whenNode.getExpressionNodes() instanceof ArrayNode) {
                         for (Iterator iter = ((ArrayNode) whenNode.getExpressionNodes()).iterator(); iter
                                 .hasNext();) {
                             Node tag = (Node) iter.next();
     
                             context.setPosition(tag.getPosition());
                             if (isTrace(runtime)) {
                                 callTraceFunction(context, "line", self);
                             }
     
                             // Ruby grammar has nested whens in a case body because of
                             // productions case_body and when_args.
                             if (tag instanceof WhenNode) {
                                 RubyArray expressions = (RubyArray) evalInternal(context, ((WhenNode) tag)
                                                 .getExpressionNodes(), self, aBlock);
     
                                 for (int j = 0; j < expressions.getLength(); j++) {
                                     IRubyObject condition = expressions.entry(j);
     
                                     if ((expression != null && condition.callMethod(context, "===", expression)
                                             .isTrue())
                                             || (expression == null && condition.isTrue())) {
                                         node = ((WhenNode) firstWhenNode).getBodyNode();
                                         continue bigloop;
                                     }
                                 }
                                 continue;
                             }
     
                             result = evalInternal(context, tag, self, aBlock);
     
                             if ((expression != null && result.callMethod(context, "===", expression).isTrue())
                                     || (expression == null && result.isTrue())) {
                                 node = whenNode.getBodyNode();
                                 continue bigloop;
                             }
                         }
                     } else {
                         result = evalInternal(context, whenNode.getExpressionNodes(), self, aBlock);
     
                         if ((expression != null && result.callMethod(context, "===", expression).isTrue())
                                 || (expression == null && result.isTrue())) {
                             node = ((WhenNode) firstWhenNode).getBodyNode();
                             continue bigloop;
                         }
                     }
     
                     context.pollThreadEvents();
     
                     firstWhenNode = whenNode.getNextCase();
                 }
     
                 return runtime.getNil();
             }
             case NodeTypes.CLASSNODE: {
                 ClassNode iVisited = (ClassNode) node;
                 Node superNode = iVisited.getSuperNode();
                 RubyClass superClass = superNode == null ? null : (RubyClass) evalInternal(context, superNode, self, aBlock);
                 Node classNameNode = iVisited.getCPath();
                 String name = ((INameNode) classNameNode).getName();
                 RubyModule enclosingClass = getEnclosingModule(context, classNameNode, self, aBlock);
                 RubyClass rubyClass = enclosingClass.defineOrGetClassUnder(name, superClass);
     
                 if (context.getWrapper() != null) {
                     rubyClass.extendObject(context.getWrapper());
                     rubyClass.includeModule(context.getWrapper());
                 }
                 return evalClassDefinitionBody(context, iVisited.getScope(), iVisited.getBodyNode(), rubyClass, self, aBlock);
             }
             case NodeTypes.CLASSVARASGNNODE: {
                 ClassVarAsgnNode iVisited = (ClassVarAsgnNode) node;
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 RubyModule rubyClass = getClassVariableBase(context, runtime);
     
                 if (rubyClass == null) {
                     rubyClass = self.getMetaClass();
                 }     
                 rubyClass.setClassVar(iVisited.getName(), result);
     
                 return result;
             }
             case NodeTypes.CLASSVARDECLNODE: {
     
                 ClassVarDeclNode iVisited = (ClassVarDeclNode) node;
     
                 RubyModule rubyClass = getClassVariableBase(context, runtime);                
                 if (rubyClass == null) {
                     throw runtime.newTypeError("no class/module to define class variable");
                 }
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 rubyClass.setClassVar(iVisited.getName(), result);
     
                 return result;
             }
             case NodeTypes.CLASSVARNODE: {
                 ClassVarNode iVisited = (ClassVarNode) node;
                 RubyModule rubyClass = getClassVariableBase(context, runtime);
     
                 if (rubyClass == null) {
                     rubyClass = self.getMetaClass();
                 }
 
                 return rubyClass.getClassVar(iVisited.getName());
             }
             case NodeTypes.COLON2NODE: {
                 Colon2Node iVisited = (Colon2Node) node;
                 Node leftNode = iVisited.getLeftNode();
     
                 // TODO: Made this more colon3 friendly because of cpath production
                 // rule in grammar (it is convenient to think of them as the same thing
                 // at a grammar level even though evaluation is).
                 if (leftNode == null) {
                     return runtime.getObject().getConstantFrom(iVisited.getName());
                 } else {
                     IRubyObject result = evalInternal(context, iVisited.getLeftNode(), self, aBlock);
                     if (result instanceof RubyModule) {
                         return ((RubyModule) result).getConstantFrom(iVisited.getName());
                     } else {
                         return result.callMethod(context, iVisited.getName(), aBlock);
                     }
                 }
             }
             case NodeTypes.COLON3NODE: {
                 Colon3Node iVisited = (Colon3Node) node;
                 return runtime.getObject().getConstantFrom(iVisited.getName());
             }
             case NodeTypes.CONSTDECLNODE: {
                 ConstDeclNode iVisited = (ConstDeclNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 IRubyObject module;
     
                 if (iVisited.getPathNode() != null) {
                     module = evalInternal(context, iVisited.getPathNode(), self, aBlock);
                 } else {
                     
     
                     // FIXME: why do we check RubyClass and then use CRef?
                     if (context.getRubyClass() == null) {
                         // TODO: wire into new exception handling mechanism
                         throw runtime.newTypeError("no class/module to define constant");
                     }
                     module = (RubyModule) context.peekCRef().getValue();
                 }
     
                 // FIXME: shouldn't we use the result of this set in setResult?
                 ((RubyModule) module).setConstant(iVisited.getName(), result);
     
                 return result;
             }
             case NodeTypes.CONSTNODE: {
                 ConstNode iVisited = (ConstNode) node;
                 return context.getConstant(iVisited.getName());
             }
             case NodeTypes.DASGNNODE: {
                 DAsgnNode iVisited = (DAsgnNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
 
                 // System.out.println("DSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
                 context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
     
                 return result;
             }
             case NodeTypes.DEFINEDNODE: {
                 DefinedNode iVisited = (DefinedNode) node;
                 String definition = getDefinition(context, iVisited.getExpressionNode(), self, aBlock);
                 if (definition != null) {
                     return runtime.newString(definition);
                 } else {
                     return runtime.getNil();
                 }
             }
             case NodeTypes.DEFNNODE: {
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
                 
                 if (containingClass.isSingleton()) {
                     IRubyObject attachedObject = ((MetaClass) containingClass).getAttachedObject();
                     
                     if (!attachedObject.singletonMethodsAllowed()) {
                         throw runtime.newTypeError("can't define singleton method \"" + 
                                 iVisited.getName() + "\" for " + attachedObject.getType());
                     }
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
             case NodeTypes.DEFSNODE: {
                 DefsNode iVisited = (DefsNode) node;
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self, aBlock);
     
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
                     if (!receiver.singletonMethodsAllowed()) {
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
             case NodeTypes.DOTNODE: {
                 DotNode iVisited = (DotNode) node;
                 return RubyRange.newRange(runtime, 
                         evalInternal(context, iVisited.getBeginNode(), self, aBlock), 
                         evalInternal(context, iVisited.getEndNode(), self, aBlock), 
                         iVisited.isExclusive());
             }
             case NodeTypes.DREGEXPNODE: {
                 DRegexpNode iVisited = (DRegexpNode) node;
     
                 StringBuffer sb = new StringBuffer();
                 for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
                     Node iterNode = (Node) iterator.next();
     
-                    sb.append(evalInternal(context, iterNode, self, aBlock).toString());
+                    sb.append(evalInternal(context, iterNode, self, aBlock).objAsString().toString());
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
 
                 return RubyRegexp.newRegexp(runtime, sb.toString(), iVisited.getOptions(), lang);
             }
             case NodeTypes.DSTRNODE: {
                 DStrNode iVisited = (DStrNode) node;
     
                 StringBuffer sb = new StringBuffer();
                 for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
                     Node iterNode = (Node) iterator.next();
     
-                    sb.append(evalInternal(context, iterNode, self, aBlock).toString());
+                    sb.append(evalInternal(context, iterNode, self, aBlock).objAsString().toString());
                 }
     
                 return runtime.newString(sb.toString());
             }
             case NodeTypes.DSYMBOLNODE: {
                 DSymbolNode iVisited = (DSymbolNode) node;
     
                 StringBuffer sb = new StringBuffer();
                 for (Iterator iterator = iVisited.getNode().iterator(); iterator.hasNext();) {
                     Node iterNode = (Node) iterator.next();
     
                     sb.append(evalInternal(context, iterNode, self, aBlock).toString());
                 }
     
                 return runtime.newSymbol(sb.toString());
             }
             case NodeTypes.DVARNODE: {
                 DVarNode iVisited = (DVarNode) node;
 
                 // System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
                 IRubyObject obj = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
                 // FIXME: null check is removable once we figure out how to assign to unset named block args
                 return obj == null ? runtime.getNil() : obj;
             }
             case NodeTypes.DXSTRNODE: {
                 DXStrNode iVisited = (DXStrNode) node;
     
                 StringBuffer sb = new StringBuffer();
                 for (Iterator iterator = iVisited.iterator(); iterator.hasNext();) {
                     Node iterNode = (Node) iterator.next();
     
                     sb.append(evalInternal(context, iterNode, self, aBlock).toString());
                 }
     
                 return self.callMethod(context, "`", runtime.newString(sb.toString()));
             }
             case NodeTypes.ENSURENODE: {
                 EnsureNode iVisited = (EnsureNode) node;
     
                 // save entering the try if there's nothing to ensure
                 if (iVisited.getEnsureNode() != null) {
                     IRubyObject result = runtime.getNil();
     
                     try {
                         result = evalInternal(context, iVisited.getBodyNode(), self, aBlock);
                     } finally {
                         evalInternal(context, iVisited.getEnsureNode(), self, aBlock);
                     }
     
                     return result;
                 }
     
                 node = iVisited.getBodyNode();
                 continue bigloop;
             }
             case NodeTypes.EVSTRNODE: {
                 EvStrNode iVisited = (EvStrNode) node;
     
                 node = iVisited.getBody();
                 continue bigloop;
             }
             case NodeTypes.FALSENODE: {
                 context.pollThreadEvents();
                 return runtime.getFalse();
             }
             case NodeTypes.FCALLNODE: {
                 FCallNode iVisited = (FCallNode) node;
                 
                 IRubyObject[] args = setupArgs(context, iVisited.getArgsNode(), self);
                 Block block = getBlock(context, self, aBlock, iVisited.getIterNode());
 
                 try {
                     while (true) {
                         try {
                             IRubyObject result = self.callMethod(context, iVisited.getName(), args,
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
                             default:
                                 throw je;
                             }
                         }
                     }
                 } catch (JumpException je) {
                     switch (je.getJumpType().getTypeId()) {
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
             case NodeTypes.FIXNUMNODE: {
                 FixnumNode iVisited = (FixnumNode) node;
                 return iVisited.getFixnum(runtime);
             }
             case NodeTypes.FLIPNODE: {
                 FlipNode iVisited = (FlipNode) node;
                 IRubyObject result = runtime.getNil();
     
                 if (iVisited.isExclusive()) {
                     if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                         result = evalInternal(context, iVisited.getBeginNode(), self, aBlock).isTrue() ? runtime.getFalse()
                                 : runtime.getTrue();
                         context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
                         return result;
                     } else {
                         if (evalInternal(context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                             context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                         }
                         return runtime.getTrue();
                     }
                 } else {
                     if (!context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth()).isTrue()) {
                         if (evalInternal(context, iVisited.getBeginNode(), self, aBlock).isTrue()) {
                             context.getCurrentScope().setValue(
                                     iVisited.getIndex(),
                                     evalInternal(context, iVisited.getEndNode(), self, aBlock).isTrue() ? runtime.getFalse()
                                             : runtime.getTrue(), iVisited.getDepth());
                             return runtime.getTrue();
                         } else {
                             return runtime.getFalse();
                         }
                     } else {
                         if (evalInternal(context, iVisited.getEndNode(), self, aBlock).isTrue()) {
                             context.getCurrentScope().setValue(iVisited.getIndex(), runtime.getFalse(), iVisited.getDepth());
                         }
                         return runtime.getTrue();
                     }
                 }
             }
             case NodeTypes.FLOATNODE: {
                 FloatNode iVisited = (FloatNode) node;
                 return RubyFloat.newFloat(runtime, iVisited.getValue());
             }
             case NodeTypes.FORNODE: {
                 ForNode iVisited = (ForNode) node;
                 
                 Block block = ForBlock.createBlock(context, iVisited.getVarNode(), 
                         context.getCurrentScope(), iVisited.getCallable(), self);
     
                 try {
                     while (true) {
                         try {
                             ISourcePosition position = context.getPosition();
     
                             IRubyObject recv = null;
                             try {
                                 recv = evalInternal(context, iVisited.getIterNode(), self, aBlock);
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
             case NodeTypes.GLOBALASGNNODE: {
                 GlobalAsgnNode iVisited = (GlobalAsgnNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
     
                 runtime.getGlobalVariables().set(iVisited.getName(), result);
     
                 // FIXME: this should be encapsulated along with the set above
                 if (iVisited.getName() == "$KCODE") {
                     runtime.setKCode(KCode.create(runtime, result.toString()));
                 }
     
                 return result;
             }
             case NodeTypes.GLOBALVARNODE: {
                 GlobalVarNode iVisited = (GlobalVarNode) node;
                 return runtime.getGlobalVariables().get(iVisited.getName());
             }
             case NodeTypes.HASHNODE: {
                 HashNode iVisited = (HashNode) node;
     
                 Map hash = null;
                 if (iVisited.getListNode() != null) {
                     hash = new HashMap(iVisited.getListNode().size() / 2);
     
                     for (Iterator iterator = iVisited.getListNode().iterator(); iterator.hasNext();) {
                         // insert all nodes in sequence, hash them in the final instruction
                         // KEY
                         IRubyObject key = evalInternal(context, (Node) iterator.next(), self, aBlock);
                         IRubyObject value = evalInternal(context, (Node) iterator.next(), self, aBlock);
     
                         hash.put(key, value);
                     }
                 }
     
                 if (hash == null) {
                     return RubyHash.newHash(runtime);
                 }
     
                 return RubyHash.newHash(runtime, hash, runtime.getNil());
             }
             case NodeTypes.IFNODE: {
                 IfNode iVisited = (IfNode) node;
                 IRubyObject result = evalInternal(context, iVisited.getCondition(), self, aBlock);
     
                 if (result.isTrue()) {
                     node = iVisited.getThenBody();
                     continue bigloop;
                 } else {
                     node = iVisited.getElseBody();
                     continue bigloop;
                 }
             }
             case NodeTypes.INSTASGNNODE: {
                 InstAsgnNode iVisited = (InstAsgnNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 self.setInstanceVariable(iVisited.getName(), result);
     
                 return result;
             }
             case NodeTypes.INSTVARNODE: {
                 InstVarNode iVisited = (InstVarNode) node;
                 IRubyObject variable = self.getInstanceVariable(iVisited.getName());
     
                 return variable == null ? runtime.getNil() : variable;
             }
                 //                case NodeTypes.ISCOPINGNODE:
                 //                EvaluateVisitor.iScopingNodeVisitor.execute(this, node);
                 //                break;
             case NodeTypes.ITERNODE: 
                 assert false: "Call nodes deal with these directly";
             case NodeTypes.LOCALASGNNODE: {
                 LocalAsgnNode iVisited = (LocalAsgnNode) node;
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 
                 // System.out.println("LSetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth() + " and set " + result);
                 context.getCurrentScope().setValue(iVisited.getIndex(), result, iVisited.getDepth());
 
                 return result;
             }
             case NodeTypes.LOCALVARNODE: {
                 LocalVarNode iVisited = (LocalVarNode) node;
 
                 //System.out.println("DGetting: " + iVisited.getName() + " at index " + iVisited.getIndex() + " and at depth " + iVisited.getDepth());
                 IRubyObject result = context.getCurrentScope().getValue(iVisited.getIndex(), iVisited.getDepth());
 
                 return result == null ? runtime.getNil() : result;
             }
             case NodeTypes.MATCH2NODE: {
                 Match2Node iVisited = (Match2Node) node;
                 IRubyObject recv = evalInternal(context, iVisited.getReceiverNode(), self, aBlock);
                 IRubyObject value = evalInternal(context, iVisited.getValueNode(), self, aBlock);
     
                 return ((RubyRegexp) recv).match(value);
             }
             case NodeTypes.MATCH3NODE: {
                 Match3Node iVisited = (Match3Node) node;
                 IRubyObject recv = evalInternal(context, iVisited.getReceiverNode(), self, aBlock);
                 IRubyObject value = evalInternal(context, iVisited.getValueNode(), self, aBlock);
     
                 if (value instanceof RubyString) {
                     return ((RubyRegexp) recv).match(value);
                 } else {
                     return value.callMethod(context, "=~", recv);
                 }
             }
             case NodeTypes.MATCHNODE: {
                 MatchNode iVisited = (MatchNode) node;
                 return ((RubyRegexp) evalInternal(context, iVisited.getRegexpNode(), self, aBlock)).match2();
             }
             case NodeTypes.MODULENODE: {
                 ModuleNode iVisited = (ModuleNode) node;
                 Node classNameNode = iVisited.getCPath();
                 String name = ((INameNode) classNameNode).getName();
                 RubyModule enclosingModule = getEnclosingModule(context, classNameNode, self, aBlock);
     
                 if (enclosingModule == null) {
                     throw runtime.newTypeError("no outer class/module");
                 }
     
                 RubyModule module;
                 if (enclosingModule == runtime.getObject()) {
                     module = runtime.getOrCreateModule(name);
                 } else {
                     module = enclosingModule.defineModuleUnder(name);
                 }
                 return evalClassDefinitionBody(context, iVisited.getScope(), iVisited.getBodyNode(), module, self, aBlock);
             }
             case NodeTypes.MULTIPLEASGNNODE: {
                 MultipleAsgnNode iVisited = (MultipleAsgnNode) node;
                 return AssignmentVisitor.assign(context, self, iVisited, 
                         evalInternal(context, iVisited.getValueNode(), self, aBlock), 
                         aBlock, false);
             }
             case NodeTypes.NEWLINENODE: {
                 NewlineNode iVisited = (NewlineNode) node;
     
                 // something in here is used to build up ruby stack trace...
                 context.setPosition(iVisited.getPosition());
     
                 if (isTrace(runtime)) {
                     callTraceFunction(context, "line", self);
                 }
     
                 // TODO: do above but not below for additional newline nodes
                 node = iVisited.getNextNode();
                 continue bigloop;
             }
             case NodeTypes.NEXTNODE: {
                 NextNode iVisited = (NextNode) node;
     
                 context.pollThreadEvents();
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
     
                 // now used as an interpreter event
                 JumpException je = new JumpException(JumpException.JumpType.NextJump);
     
                 je.setTarget(iVisited);
                 je.setValue(result);
     
                 throw je;
             }
             case NodeTypes.NILNODE:
                 return runtime.getNil();
             case NodeTypes.NOTNODE: {
                 NotNode iVisited = (NotNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getConditionNode(), self, aBlock);
                 return result.isTrue() ? runtime.getFalse() : runtime.getTrue();
             }
             case NodeTypes.NTHREFNODE: {
                 NthRefNode iVisited = (NthRefNode) node;
                 return RubyRegexp.nth_match(iVisited.getMatchNumber(), context.getBackref());
             }
             case NodeTypes.OPASGNANDNODE: {
                 BinaryOperatorNode iVisited = (BinaryOperatorNode) node;
     
                 // add in reverse order
                 IRubyObject result = evalInternal(context, iVisited.getFirstNode(), self, aBlock);
                 if (!result.isTrue()) return result;
                 node = iVisited.getSecondNode();
                 continue bigloop;
             }
             case NodeTypes.OPASGNNODE: {
                 OpAsgnNode iVisited = (OpAsgnNode) node;
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self, aBlock);
                 IRubyObject value = receiver.callMethod(context, iVisited.getVariableName());
     
                 if (iVisited.getOperatorName() == "||") {
                     if (value.isTrue()) {
                         return value;
                     }
                     value = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 } else if (iVisited.getOperatorName() == "&&") {
                     if (!value.isTrue()) {
                         return value;
                     }
                     value = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 } else {
                     value = value.callMethod(context, iVisited.getOperatorName(), evalInternal(context,
                             iVisited.getValueNode(), self, aBlock));
                 }
     
                 receiver.callMethod(context, iVisited.getVariableNameAsgn(), value);
     
                 context.pollThreadEvents();
     
                 return value;
             }
             case NodeTypes.OPASGNORNODE: {
                 OpAsgnOrNode iVisited = (OpAsgnOrNode) node;
                 String def = getDefinition(context, iVisited.getFirstNode(), self, aBlock);
     
                 IRubyObject result = runtime.getNil();
                 if (def != null) {
                     result = evalInternal(context, iVisited.getFirstNode(), self, aBlock);
                 }
                 if (!result.isTrue()) {
                     result = evalInternal(context, iVisited.getSecondNode(), self, aBlock);
                 }
     
                 return result;
             }
             case NodeTypes.OPELEMENTASGNNODE: {
                 OpElementAsgnNode iVisited = (OpElementAsgnNode) node;
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self, aBlock);
     
                 IRubyObject[] args = setupArgs(context, iVisited.getArgsNode(), self);
     
                 IRubyObject firstValue = receiver.callMethod(context, "[]", args);
     
                 if (iVisited.getOperatorName() == "||") {
                     if (firstValue.isTrue()) {
                         return firstValue;
                     }
                     firstValue = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 } else if (iVisited.getOperatorName() == "&&") {
                     if (!firstValue.isTrue()) {
                         return firstValue;
                     }
                     firstValue = evalInternal(context, iVisited.getValueNode(), self, aBlock);
                 } else {
                     firstValue = firstValue.callMethod(context, iVisited.getOperatorName(), evalInternal(context, iVisited
                                     .getValueNode(), self, aBlock));
                 }
     
                 IRubyObject[] expandedArgs = new IRubyObject[args.length + 1];
                 System.arraycopy(args, 0, expandedArgs, 0, args.length);
                 expandedArgs[expandedArgs.length - 1] = firstValue;
                 return receiver.callMethod(context, "[]=", expandedArgs);
             }
             case NodeTypes.OPTNNODE: {
                 OptNNode iVisited = (OptNNode) node;
     
                 IRubyObject result = runtime.getNil();
                 while (RubyKernel.gets(runtime.getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
                     loop: while (true) { // Used for the 'redo' command
                         try {
                             result = evalInternal(context, iVisited.getBodyNode(), self, aBlock);
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
             case NodeTypes.ORNODE: {
                 OrNode iVisited = (OrNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getFirstNode(), self, aBlock);
     
                 if (!result.isTrue()) {
                     result = evalInternal(context, iVisited.getSecondNode(), self, aBlock);
                 }
     
                 return result;
             }
                 //                case NodeTypes.POSTEXENODE:
                 //                EvaluateVisitor.postExeNodeVisitor.execute(this, node);
                 //                break;
             case NodeTypes.REDONODE: {
                 context.pollThreadEvents();
     
                 // now used as an interpreter event
                 JumpException je = new JumpException(JumpException.JumpType.RedoJump);
     
                 je.setValue(node);
     
                 throw je;
             }
             case NodeTypes.REGEXPNODE: {
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
                     return RubyRegexp.newRegexp(runtime, iVisited.getPattern(), lang);
                 } catch(java.util.regex.PatternSyntaxException e) {
                     throw runtime.newSyntaxError(e.getMessage());
                 }
             }
             case NodeTypes.RESCUEBODYNODE: {
                 RescueBodyNode iVisited = (RescueBodyNode) node;
                 node = iVisited.getBodyNode();
                 continue bigloop;
             }
             case NodeTypes.RESCUENODE: {
                 RescueNode iVisited = (RescueNode)node;
                 RescuedBlock : while (true) {
                     IRubyObject globalExceptionState = runtime.getGlobalVariables().get("$!");
                     boolean anotherExceptionRaised = false;
                     try {
                         // Execute rescue block
                         IRubyObject result = evalInternal(context, iVisited.getBodyNode(), self, aBlock);
 
                         // If no exception is thrown execute else block
                         if (iVisited.getElseNode() != null) {
                             if (iVisited.getRescueNode() == null) {
                                 runtime.getWarnings().warn(iVisited.getElseNode().getPosition(), "else without rescue is useless");
                             }
                             result = evalInternal(context, iVisited.getElseNode(), self, aBlock);
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
                                 exceptionNodesList = (ListNode) evalInternal(context, exceptionNodes, self, aBlock);
                             } else {
                                 exceptionNodesList = (ListNode) exceptionNodes;
                             }
                             
                             if (isRescueHandled(context, raisedException, exceptionNodesList, self)) {
                                 try {
                                     return evalInternal(context, rescueNode, self, aBlock);
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
             case NodeTypes.RETRYNODE: {
                 context.pollThreadEvents();
     
                 JumpException je = new JumpException(JumpException.JumpType.RetryJump);
     
                 throw je;
             }
             case NodeTypes.RETURNNODE: {
                 ReturnNode iVisited = (ReturnNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getValueNode(), self, aBlock);
     
                 JumpException je = new JumpException(JumpException.JumpType.ReturnJump);
     
                 je.setTarget(iVisited.getTarget());
                 je.setValue(result);
     
                 throw je;
             }
             case NodeTypes.ROOTNODE: {
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
                     return eval(context, iVisited.getBodyNode(), self, aBlock);
                 } finally {
                     context.postRootNode();
                 }
             }
             case NodeTypes.SCLASSNODE: {
                 SClassNode iVisited = (SClassNode) node;
                 IRubyObject receiver = evalInternal(context, iVisited.getReceiverNode(), self, aBlock);
     
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
                     singletonClass.extendObject(context.getWrapper());
                     singletonClass.includeModule(context.getWrapper());
                 }
     
                 return evalClassDefinitionBody(context, iVisited.getScope(), iVisited.getBodyNode(), singletonClass, self, aBlock);
             }
             case NodeTypes.SELFNODE:
                 return self;
             case NodeTypes.SPLATNODE: {
                 SplatNode iVisited = (SplatNode) node;
                 return splatValue(evalInternal(context, iVisited.getValue(), self, aBlock));
             }
                 ////                case NodeTypes.STARNODE:
                 ////                EvaluateVisitor.starNodeVisitor.execute(this, node);
                 ////                break;
             case NodeTypes.STRNODE: {
                 StrNode iVisited = (StrNode) node;
                 return runtime.newString(iVisited.getValue());
             }
             case NodeTypes.SUPERNODE: {
                 SuperNode iVisited = (SuperNode) node;
                 
     
                 if (context.getFrameLastClass() == null) {
                     String name = context.getFrameLastFunc();
                     throw runtime.newNameError("Superclass method '" + name
                             + "' disabled.", name);
                 }
                 IRubyObject[] args = setupArgs(context, iVisited.getArgsNode(), self);
                 Block block = getBlock(context, self, aBlock, iVisited.getIterNode());
                 
                 // If no explicit block passed to super, then use the one passed in.
                 if (!block.isGiven()) block = aBlock;
                 
                 return context.callSuper(args, block);
             }
             case NodeTypes.SVALUENODE: {
                 SValueNode iVisited = (SValueNode) node;
                 return aValueSplat(evalInternal(context, iVisited.getValue(), self, aBlock));
             }
             case NodeTypes.SYMBOLNODE: {
                 SymbolNode iVisited = (SymbolNode) node;
                 return runtime.newSymbol(iVisited.getName());
             }
             case NodeTypes.TOARYNODE: {
                 ToAryNode iVisited = (ToAryNode) node;
                 return aryToAry(evalInternal(context, iVisited.getValue(), self, aBlock));
             }
             case NodeTypes.TRUENODE: {
                 context.pollThreadEvents();
                 return runtime.getTrue();
             }
             case NodeTypes.UNDEFNODE: {
                 UndefNode iVisited = (UndefNode) node;
                 
     
                 if (context.getRubyClass() == null) {
                     throw runtime
                             .newTypeError("No class to undef method '" + iVisited.getName() + "'.");
                 }
                 context.getRubyClass().undef(iVisited.getName());
     
                 return runtime.getNil();
             }
             case NodeTypes.UNTILNODE: {
                 UntilNode iVisited = (UntilNode) node;
     
                 IRubyObject result = runtime.getNil();
                 
                 while (!(result = evalInternal(context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
                     loop: while (true) { // Used for the 'redo' command
                         try {
                             result = evalInternal(context, iVisited.getBodyNode(), self, aBlock);
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
             case NodeTypes.VALIASNODE: {
                 VAliasNode iVisited = (VAliasNode) node;
                 runtime.getGlobalVariables().alias(iVisited.getNewName(), iVisited.getOldName());
     
                 return runtime.getNil();
             }
             case NodeTypes.VCALLNODE: {
                 VCallNode iVisited = (VCallNode) node;
                 return self.callMethod(context, iVisited.getName(), 
                         IRubyObject.NULL_ARRAY, CallType.VARIABLE);
             }
             case NodeTypes.WHENNODE:
                 assert false;
                 return null;
             case NodeTypes.WHILENODE: {
                 WhileNode iVisited = (WhileNode) node;
     
                 IRubyObject result = runtime.getNil();
                 boolean firstTest = iVisited.evaluateAtStart();
                 
                 while (!firstTest || (result = evalInternal(context, iVisited.getConditionNode(), self, aBlock)).isTrue()) {
                     firstTest = true;
                     loop: while (true) { // Used for the 'redo' command
                         try {
                             evalInternal(context, iVisited.getBodyNode(), self, aBlock);
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
             case NodeTypes.XSTRNODE: {
                 XStrNode iVisited = (XStrNode) node;
                 return self.callMethod(context, "`", runtime.newString(iVisited.getValue()));
             }
             case NodeTypes.YIELDNODE: {
                 YieldNode iVisited = (YieldNode) node;
     
                 IRubyObject result = evalInternal(context, iVisited.getArgsNode(), self, aBlock);
                 if (iVisited.getArgsNode() == null) {
                     result = null;
                 }
 
                 Block block = context.getCurrentFrame().getBlock();
 
                 return block.yield(context, result, null, null, iVisited.getCheckState());
                                 
             }
             case NodeTypes.ZARRAYNODE: {
                 return runtime.newArray();
             }
             case NodeTypes.ZSUPERNODE: {
                 
     
                 if (context.getFrameLastClass() == null) {
                     String name = context.getFrameLastFunc();
                     throw runtime.newNameError("superclass method '" + name
                             + "' disabled", name);
                 }
                 
                 // Has the method that is calling super received a block argument
                 Block block = context.getCurrentFrame().getBlock();
                 
                 return context.callSuper(context.getFrameArgs(), block);
             }
             default:
                 throw new RuntimeException("Invalid node encountered in interpreter: \"" + node.getClass().getName() + "\", please report this at www.jruby.org");
             }
         } while (true);
     }
     
     private static String getArgumentDefinition(ThreadContext context, Node node, String type, IRubyObject self, Block block) {
         if (node == null) return type;
             
         if (node instanceof ArrayNode) {
             for (Iterator iter = ((ArrayNode)node).iterator(); iter.hasNext(); ) {
                 if (getDefinitionInner(context, (Node)iter.next(), self, block) == null) return null;
             }
         } else if (getDefinitionInner(context, node, self, block) == null) {
             return null;
         }
 
         return type;
     }
     
     private static String getDefinition(ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         try {
             context.setWithinDefined(true);
             return getDefinitionInner(context, node, self, aBlock);
         } finally {
             context.setWithinDefined(false);
         }
     }
     
     private static String getDefinitionInner(ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (node == null) return "expression";
         
         switch(node.nodeId) {
         case NodeTypes.ATTRASSIGNNODE: {
             AttrAssignNode iVisited = (AttrAssignNode) node;
             
             if (getDefinitionInner(context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (!visibility.isPrivate() && 
                             (!visibility.isProtected() || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(context, iVisited.getArgsNode(), "assignment", self, aBlock);
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
             
             if (getDefinitionInner(context, iVisited.getReceiverNode(), self, aBlock) != null) {
                 try {
                     IRubyObject receiver = eval(context, iVisited.getReceiverNode(), self, aBlock);
                     RubyClass metaClass = receiver.getMetaClass();
                     DynamicMethod method = metaClass.searchMethod(iVisited.getName());
                     Visibility visibility = method.getVisibility();
 
                     if (!visibility.isPrivate() && 
                             (!visibility.isProtected() || self.isKindOf(metaClass.getRealClass()))) {
                         if (metaClass.isMethodBound(iVisited.getName(), false)) {
                             return getArgumentDefinition(context, iVisited.getArgsNode(), "method", self, aBlock);
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
                 IRubyObject left = EvaluationState.eval(context, iVisited.getLeftNode(), self, aBlock);
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
                 return getArgumentDefinition(context, iVisited.getArgsNode(), "method", self, aBlock);
             }
             
             return null;
         }
         case NodeTypes.GLOBALVARNODE:
             if (context.getRuntime().getGlobalVariables().isDefined(((GlobalVarNode) node).getName())) {
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
             String lastMethod = context.getFrameLastFunc();
             RubyModule lastClass = context.getFrameLastClass();
             if (lastMethod != null && lastClass != null && 
                     lastClass.getSuperClass().isMethodBound(lastMethod, false)) {
                 return getArgumentDefinition(context, iVisited.getArgsNode(), "super", self, aBlock);
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
             
diff --git a/test/org/jvyamlb/TestBean2.java b/test/org/jvyamlb/TestBean2.java
new file mode 100644
index 0000000000..276e4558d3
--- /dev/null
+++ b/test/org/jvyamlb/TestBean2.java
@@ -0,0 +1,84 @@
+/***** BEGIN LICENSE BLOCK *****
+ * Version: CPL 1.0/GPL 2.0/LGPL 2.1
+ *
+ * The contents of this file are subject to the Common Public
+ * License Version 1.0 (the "License"); you may not use this file
+ * except in compliance with the License. You may obtain a copy of
+ * the License at http://www.eclipse.org/legal/cpl-v10.html
+ *
+ * Software distributed under the License is distributed on an "AS
+ * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
+ * implied. See the License for the specific language governing
+ * rights and limitations under the License.
+ *
+ * Copyright (C) 2007 Ola Bini <ola@ologix.com>
+ * 
+ * Alternatively, the contents of this file may be used under the terms of
+ * either of the GNU General Public License Version 2 or later (the "GPL"),
+ * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
+ * in which case the provisions of the GPL or the LGPL are applicable instead
+ * of those above. If you wish to allow use of your version of this file only
+ * under the terms of either the GPL or the LGPL, and not to allow others to
+ * use your version of this file under the terms of the CPL, indicate your
+ * decision by deleting the provisions above and replace them with the notice
+ * and other provisions required by the GPL or the LGPL. If you do not delete
+ * the provisions above, a recipient may use your version of this file under
+ * the terms of any one of the CPL, the GPL or the LGPL.
+ ***** END LICENSE BLOCK *****/
+package org.jvyamlb;
+
+import org.jruby.util.ByteList;
+
+/**
+ * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
+ */
+public class TestBean2 {
+    private ByteList name;
+    private int age;
+
+    public TestBean2() {
+    }
+
+    public TestBean2(final ByteList name, final int age) {
+        this.name = name;
+        this.age = age;
+    }
+    
+    public ByteList getName() {
+        return this.name;
+    }
+
+    public int getAge() {
+        return age;
+    }
+
+    public void setName(final ByteList name) {
+        this.name = name;
+    }
+
+    public void setAge(final int age) {
+        this.age = age;
+    }
+
+    public boolean equals(final Object other) {
+        boolean ret = this == other;
+        if(!ret && other instanceof TestBean2) {
+            TestBean2 o = (TestBean2)other;
+            ret = 
+                this.name == null ? o.name == null : this.name.equals(o.name) &&
+                this.age == o.age;
+        }
+        return ret;
+    }
+
+    public int hashCode() {
+        int val = 3;
+        val += 3 * (name == null ? 0 : name.hashCode());
+        val += 3 * age;
+        return val;
+    }
+
+    public String toString() {
+        return "#<org.jvyamlb.TestBean2 name=\"" + name + "\" age=" + age + ">";
+    }
+}// TestBean2
diff --git a/test/org/jvyamlb/YAMLDumpTest.java b/test/org/jvyamlb/YAMLDumpTest.java
index 25566d72ae..714ada6657 100644
--- a/test/org/jvyamlb/YAMLDumpTest.java
+++ b/test/org/jvyamlb/YAMLDumpTest.java
@@ -1,89 +1,82 @@
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
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
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
 package org.jvyamlb;
 
 import java.util.Map;
 import java.util.List;
 import java.util.HashMap;
 import java.util.ArrayList;
 
 import junit.framework.TestCase;
 
 import org.jruby.util.ByteList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class YAMLDumpTest extends TestCase {
     public YAMLDumpTest(final String name) {
         super(name);
     }
 
     public void testBasicStringDump() {
         assertEquals(ByteList.create("--- str\n"), YAML.dump(ByteList.create("str")));
     }
 
     public void testBasicHashDump() {
         Map ex = new HashMap();
         ex.put(ByteList.create("a"),ByteList.create("b"));
         assertEquals(ByteList.create("--- \na: b\n"), YAML.dump(ex));
     }
 
     public void testBasicListDump() {
         List ex = new ArrayList();
         ex.add(ByteList.create("a"));
         ex.add(ByteList.create("b"));
         ex.add(ByteList.create("c"));
         assertEquals(ByteList.create("--- \n- a\n- b\n- c\n"), YAML.dump(ex));
     }
 
     public void testVersionDumps() {
         assertEquals(ByteList.create("--- !!int 1\n"), YAML.dump(new Integer(1),YAML.config().explicitTypes(true)));
         assertEquals(ByteList.create("--- !int 1\n"), YAML.dump(new Integer(1),YAML.config().version("1.0").explicitTypes(true)));
     }
 
     public void testMoreScalars() {
         assertEquals(ByteList.create("--- !!str 1.0\n"), YAML.dump(ByteList.create("1.0")));
     }
 
     public void testDumpJavaBean() {
-        final java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Sweden"));
-        cal.clear();
-        cal.set(1982,5-1,3); // Java's months are zero-based...
-        final TestBean toDump = new TestBean(ByteList.create("Ola Bini"), 24, cal.getTime());
+        final TestBean2 toDump = new TestBean2(ByteList.create("Ola Bini"), 24);
         Object v = YAML.dump(toDump);
-        assertTrue(
-ByteList.create("--- !java/object:org.jvyamlb.TestBean\nname: Ola Bini\nage: 24\nborn: 1982-05-03 02:00:00 +02:00\n").equals(v) ||
-ByteList.create("--- !java/object:org.jvyamlb.TestBean\nname: Ola Bini\nborn: 1982-05-03 02:00:00 +02:00\nage: 24\n").equals(v) ||
-ByteList.create("--- !java/object:org.jvyamlb.TestBean\nage: 24\nname: Ola Bini\nborn: 1982-05-03 02:00:00 +02:00\n").equals(v) ||
-ByteList.create("--- !java/object:org.jvyamlb.TestBean\nage: 24\nborn: 1982-05-03 02:00:00 +02:00\nname: Ola Bini\n").equals(v) ||
-ByteList.create("--- !java/object:org.jvyamlb.TestBean\nborn: 1982-05-03 02:00:00 +02:00\nage: 24\nname: Ola Bini\n").equals(v) ||
-ByteList.create("--- !java/object:org.jvyamlb.TestBean\nborn: 1982-05-03 02:00:00 +02:00\nname: Ola Bini\nage: 24\n").equals(v)
+        assertTrue("something is wrong with: \"" + v + "\"",
+ByteList.create("--- !java/object:org.jvyamlb.TestBean2\nname: Ola Bini\nage: 24\n").equals(v) ||
+ByteList.create("--- !java/object:org.jvyamlb.TestBean2\nage: 24\nname: Ola Bini\n").equals(v)
                    );
     }
 }// YAMLDumpTest
