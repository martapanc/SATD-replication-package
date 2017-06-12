diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 54f18436f2..9b7829fa72 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1463 +1,1465 @@
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
  * Copyright (C) 2007 MenTaLguY <mental@rydia.net>
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
 package org.jruby;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.concurrent.atomic.AtomicBoolean;
 
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.util.IdUtil;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.runtime.builtin.InternalVariables;
 import org.jruby.runtime.marshal.CoreObjectType;
 import org.jruby.util.TypeConverter;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Object", include="Kernel")
 public class RubyObject implements Cloneable, IRubyObject, Serializable, CoreObjectType, InstanceVariables, InternalVariables {
     
     private RubyObject(){};
     // An instance that never equals any other instance
     public static final IRubyObject NEVER = new RubyObject();
     
     // The class of this object
     protected transient RubyClass metaClass;
     protected String metaClassName;
 
     /**
      * The variableTable contains variables for an object, defined as:
      * <ul>
      * <li> instance variables
      * <li> class variables (for classes/modules)
      * <li> internal variables (such as those used when marshaling RubyRange and RubyException)
      * </ul>
      * 
      * Constants are stored separately, see {@link RubyModule}. 
      * 
      */
     protected transient volatile VariableTableEntry[] variableTable;
     protected transient int variableTableSize;
     protected transient int variableTableThreshold;
 
     private transient Object dataStruct;
 
     protected int flags; // zeroed by jvm
     public static final int ALL_F = -1;
     public static final int FALSE_F = 1 << 0;
     public static final int NIL_F = 1 << 1;
     public static final int FROZEN_F = 1 << 2;
     public static final int TAINTED_F = 1 << 3;
 
     public static final int FL_USHIFT = 4;
     
     public static final int USER0_F = (1<<(FL_USHIFT+0));
     public static final int USER1_F = (1<<(FL_USHIFT+1));
     public static final int USER2_F = (1<<(FL_USHIFT+2));
     public static final int USER3_F = (1<<(FL_USHIFT+3));
     public static final int USER4_F = (1<<(FL_USHIFT+4));
     public static final int USER5_F = (1<<(FL_USHIFT+5));
     public static final int USER6_F = (1<<(FL_USHIFT+6));
     public static final int USER7_F = (1<<(FL_USHIFT+7));
 
     public final void setFlag(int flag, boolean set) {
         if (set) {
             flags |= flag;
         } else {
             flags &= ~flag;
         }
     }
     
     public final boolean getFlag(int flag) { 
         return (flags & flag) != 0;
     }
     
     private transient Finalizer finalizer;
     
     public class Finalizer implements Finalizable {
         private long id;
         private List<IRubyObject> finalizers;
         private AtomicBoolean finalized;
         
         public Finalizer(long id) {
             this.id = id;
             this.finalized = new AtomicBoolean(false);
         }
         
         public void addFinalizer(IRubyObject finalizer) {
             if (finalizers == null) {
                 finalizers = new ArrayList<IRubyObject>();
             }
             finalizers.add(finalizer);
         }
 
         public void removeFinalizers() {
             finalizers = null;
         }
     
         public void finalize() {
             if (finalized.compareAndSet(false, true)) {
                 if (finalizers != null) {
                     for (int i = 0; i < finalizers.size(); i++) {
                         IRubyObject finalizer = finalizers.get(i);                        
                         RuntimeHelpers.invoke(
                                 finalizer.getRuntime().getCurrentContext(),
                                 finalizer, "call", RubyObject.this.id());
                     }
                 }
             }
         }
     }
 
     /** standard path for object creation 
      * 
      */
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     /** path for objects who want to decide whether they want to be in ObjectSpace
      *  regardless of it being turned on or off
      *  (notably used by objects being considered immediate, they'll always pass false here)
      */
     protected RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
         if (Ruby.RUNTIME_THREADLOCAL && metaClass != null) {
             metaClassName = metaClass.classId;
         }
 
         if (useObjectSpace) {
             assert runtime.isObjectSpaceEnabled();
             runtime.getObjectSpace().add(this);
         }
 
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         if (runtime.getSafeLevel() >= 3) flags |= TAINTED_F;
     }
     
     public static RubyClass createObjectClass(Ruby runtime, RubyClass objectClass) {
         objectClass.index = ClassIndex.OBJECT;
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyObject.class);
         objectClass.defineFastPrivateMethod("initialize", callbackFactory.getFastMethod("initialize"));
 
         return objectClass;
     }
     
     public static final ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyObject(runtime, klass);
         }
     };
 
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
 
     public boolean isModule() {
         return false;
     }
     
     public boolean isClass() {
         return false;
     }
 
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /** rb_make_metaclass
      *
      */
     public RubyClass makeMetaClass(RubyClass superClass) {
         MetaClass klass = new MetaClass(getRuntime(), superClass); // rb_class_boot
         setMetaClass(klass);
 
         klass.setAttached(this);
         klass.setMetaClass(superClass.getRealClass().getMetaClass());
 
         return klass;
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
         return other == this || 
                 other instanceof IRubyObject && 
                 callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY).toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public final Ruby getRuntime() {
         return getMetaClass().getClassRuntime();
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public final RubyClass getMetaClass() {
         RubyClass mc;
         if ((mc = metaClass) != null) return mc;
         if (Ruby.RUNTIME_THREADLOCAL && metaClassName != null) {
             // this should only happen when we're persisting objects, so go after getCurrentInstance directly
             metaClass = Ruby.getCurrentInstance().getClass(metaClassName);
         }
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
         if (Ruby.RUNTIME_THREADLOCAL && metaClass != null) {
             metaClassName = metaClass.classId;
         }
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return (flags & FROZEN_F) != 0;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         if (frozen) {
             flags |= FROZEN_F;
         } else {
             flags &= ~FROZEN_F;
         }
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message + " " + getMetaClass().getName());
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
         return (flags & TAINTED_F) != 0; 
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         if (taint) {
             flags |= TAINTED_F;
         } else {
             flags &= ~TAINTED_F;
     }
     }
 
     public final boolean isNil() {
         return (flags & NIL_F) != 0;
     }
 
     public final boolean isTrue() {
         return (flags & FALSE_F) == 0;
     }
 
     public final boolean isFalse() {
         return (flags & FALSE_F) != 0;
     }
 
     public boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     /** rb_singleton_class
      *  Note: this method is specialized for RubyFixnum, RubySymbol, RubyNil and RubyBoolean
      */    
     public RubyClass getSingletonClass() {
         RubyClass klass;
         
         if (getMetaClass().isSingleton() && ((MetaClass)getMetaClass()).getAttached() == this) {
             klass = getMetaClass();            
         } else {
             klass = makeMetaClass(getMetaClass());
         }
         
         klass.setTaint(isTaint());
         if (isFrozen()) klass.setFrozen(true);
         
         return klass;
     }
     
     /** rb_singleton_class_clone
      *
      */
     protected RubyClass getSingletonClassClone() {
        RubyClass klass = getMetaClass();
 
        if (!klass.isSingleton()) return klass;
 
        MetaClass clone = new MetaClass(getRuntime());
        clone.flags = flags;
 
        if (this instanceof RubyClass) {
            clone.setMetaClass(clone);
        } else {
            clone.setMetaClass(klass.getSingletonClassClone());
        }
 
        clone.setSuperClass(klass.getSuperClass());
 
        if (klass.hasVariables()) {
            clone.syncVariables(klass.getVariableList());
        }
 
        klass.cloneMethods(clone);
 
        ((MetaClass)clone.getMetaClass()).setAttached(clone);
 
        ((MetaClass)clone).setAttached(((MetaClass)klass).getAttached());
 
        return clone;
     }
 
     /** init_copy
      * 
      */
     private static void initCopy(IRubyObject clone, RubyObject original) {
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         original.copySpecialInstanceVariables(clone);
 
         if (original.hasVariables()) {
             clone.syncVariables(original.getVariableList());
         }
 
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         if (obj.isTaint()) setTaint(true);
         return this;
     }
 
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = RuntimeHelpers.findImplementerIfNecessary(getMetaClass(), klazz).getSuperClass();
         
-        assert superClass != null : "Superclass should always be something for " + klazz.getBaseName();
-
+        if (superClass == null) {
+            String name = context.getFrameName(); 
+            return RuntimeHelpers.callMethodMissing(context, this, klazz.searchMethod(name), name, args, this, CallType.SUPER, block);
+        }
         return RuntimeHelpers.invokeAs(context, superClass, this, context.getFrameName(), args, CallType.SUPER, block);
     }    
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return RuntimeHelpers.invoke(context, this, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, this, name, arg, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, this, name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invoke(context, this, name, args, CallType.FUNCTIONAL, block);
     }
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name) {
         return RuntimeHelpers.invoke(context, this, methodIndex, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, this, methodIndex,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /** rb_to_id
      *
      */
     public String asJavaString() {
         IRubyObject asString = checkStringType();
         if(!asString.isNil()) return ((RubyString)asString).asJavaString();
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) TypeConverter.convertToType(this, getRuntime().getArray(), MethodIndex.TO_ARY, "to_ary");
     }
 
     public RubyHash convertToHash() {
         return (RubyHash)TypeConverter.convertToType(this, getRuntime().getHash(), MethodIndex.TO_HASH, "to_hash");
     }
     
     public RubyFloat convertToFloat() {
         return (RubyFloat) TypeConverter.convertToType(this, getRuntime().getFloat(), MethodIndex.TO_F, "to_f");
     }
 
     public RubyInteger convertToInteger() {
         return convertToInteger(MethodIndex.TO_INT, "to_int");
     }
 
     public RubyInteger convertToInteger(int convertMethodIndex, String convertMethod) {
         IRubyObject val = TypeConverter.convertToType(this, getRuntime().getInteger(), convertMethodIndex, convertMethod, true);
         if (!(val instanceof RubyInteger)) throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod + " should return Integer");
         return (RubyInteger)val;
     }
 
     public RubyString convertToString() {
         return (RubyString) TypeConverter.convertToType(this, getRuntime().getString(), MethodIndex.TO_STR, "to_str");
     }
     
     public final IRubyObject convertToType(RubyClass target, int convertMethodIndex) {
         return TypeConverter.convertToType(this, target, convertMethodIndex, (String)MethodIndex.NAMES.get(convertMethodIndex));
     }
 
     /** rb_obj_as_string
      */
     public RubyString asString() {
         IRubyObject str = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
         
         if (!(str instanceof RubyString)) return (RubyString)anyToString();
         if (isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = TypeConverter.convertToTypeWithCheck(this, getRuntime().getString(), MethodIndex.TO_STR, "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return TypeConverter.convertToTypeWithCheck(this, getRuntime().getArray(), MethodIndex.TO_ARY, "to_ary");
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(context, mod, new IRubyObject[] { this }, block);
         }
 
         if (args.length == 0) {
             throw getRuntime().newArgumentError("block not supplied");
         } else if (args.length > 3) {
             String lastFuncName = context.getFrameName();
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
         args[0] = args[0].convertToString();
 
         IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
         IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
         Visibility savedVisibility = context.getCurrentVisibility();
         context.setCurrentVisibility(Visibility.PUBLIC);
         try {
             return evalUnder(context, mod, args[0], file, line);
         } finally {
             context.setCurrentVisibility(savedVisibility);
         }
     }
 
     public IRubyObject evalUnder(final ThreadContext context, RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         return under.executeUnder(context, new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // Line numbers are zero-based so we subtract one
                 int lineNumber = (int) (args[3].convertToInteger().getLongValue() - 1);
 
                 return ASTInterpreter.evalSimple(context, args[0], source, 
                         filename.convertToString().toString(), lineNumber);
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
     }
 
     private IRubyObject yieldUnder(final ThreadContext context, RubyModule under, IRubyObject[] args, Block block) {
         return under.executeUnder(context, new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 Visibility savedVisibility = block.getBinding().getVisibility();
 
                 block.getBinding().setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield;
                     boolean aValue;
                     if (args.length == 1) {
                         valueInYield = args[0];
                         aValue = false;
                     } else {
                         valueInYield = RubyArray.newArray(getRuntime(), args);
                         aValue = true;
                     }
                     
                     // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
                     block = block.cloneBlock();
                     block.getBinding().setSelf(RubyObject.this);
                     block.getBinding().getFrame().setSelf(RubyObject.this);
                     // end hack
                     
                     return block.yield(context, valueInYield, RubyObject.this, context.getRubyClass(), aValue);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException.BreakJump bj) {
                         return (IRubyObject) bj.getValue();
                 } finally {
                     block.getBinding().setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, args, block);
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "equal?", required = 1)
     public IRubyObject equal_p(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** method used for Hash key comparison (specialized for String, Symbol and Fixnum)
      * 
      */
     public boolean eql(IRubyObject other) {
         return callMethod(getRuntime().getCurrentContext(), MethodIndex.EQL_P, "eql?", other).isTrue();
     }
     
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
     
     /** rb_equal
      * 
      */
     @JRubyMethod(name = "===", required = 1)
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return getRuntime().newBoolean(this == other || callMethod(context, MethodIndex.EQUALEQUAL, "==",other).isTrue());
     }
 
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that == other || that.callMethod(context, MethodIndex.EQUALEQUAL, "==", other).isTrue();
     }
 
     protected static boolean eqlInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that == other || that.callMethod(context, MethodIndex.EQL_P, "eql?", other).isTrue();
     }
 
     /** rb_obj_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
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
      * FIXME: !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
      */
     @JRubyMethod(name = "respond_to?", rest = true)
     public RubyBoolean respond_to_p(IRubyObject[] args) {
         String name = args[0].asJavaString();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      * FIXME: Should this be renamed to match its ruby name?
      */
     @JRubyMethod(name = {"object_id", "__id__"})
     public synchronized IRubyObject id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
 
     /** rb_obj_id_obsolete
      * 
      */
     @JRubyMethod(name = "id")
     public synchronized IRubyObject id_deprecated() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#id will be deprecated; use Object#object_id", "Object#id", "Object#object_id");
         return id();
     }
     
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(super.hashCode());
     }
 
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), MethodIndex.HASH, "hash");
         
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue); 
         
         return super.hashCode();
     }
 
     /** rb_obj_type
      *
      */
     @JRubyMethod(name = "class")
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     @JRubyMethod(name = "type")
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#type is deprecated; use Object#class", "Object#type", "Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     @JRubyMethod(name = "clone", frame = true)
     public IRubyObject rbClone() {
         if (isImmediate()) throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         
         // We're cloning ourselves, so we know the result should be a RubyObject
         RubyObject clone = (RubyObject)getMetaClass().getRealClass().allocate();
         clone.setMetaClass(getSingletonClassClone());
         if (isTaint()) clone.setTaint(true);
 
         initCopy(clone, this);
 
         if (isFrozen()) clone.setFrozen(true);
         return clone;
     }
 
     /** rb_obj_dup
      *  should be overriden only by: Proc
      */
     @JRubyMethod(name = "dup")
     public IRubyObject dup() {
         if (isImmediate()) throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
 
         IRubyObject dup = getMetaClass().getRealClass().allocate();
         if (isTaint()) dup.setTaint(true);
 
         initCopy(dup, this);
 
         return dup;
     }
     
     /** Lots of MRI objects keep their state in non-lookupable ivars (e:g. Range, Struct, etc)
      *  This method is responsible for dupping our java field equivalents 
      * 
      */
     protected void copySpecialInstanceVariables(IRubyObject clone) {
     }    
 
     @JRubyMethod(name = "display", optional = 1)
     public IRubyObject display(ThreadContext context, IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(context, "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_tainted
      *
      */
     @JRubyMethod(name = "tainted?")
     public RubyBoolean tainted_p() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     @JRubyMethod(name = "taint")
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
     @JRubyMethod(name = "untaint")
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
     @JRubyMethod(name = "freeze")
     public IRubyObject freeze() {
         if ((flags & FROZEN_F) == 0) {
             if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw getRuntime().newSecurityError("Insecure: can't freeze object");
             }
             flags |= FROZEN_F;
         }
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     
     @JRubyMethod(name = "frozen?")
     public RubyBoolean frozen_p() {
         return getRuntime().newBoolean(isFrozen());
     }
 
     /** inspect_obj
      * 
      */
     private StringBuffer inspectObj(StringBuffer part) {
         String sep = "";
         for (Variable<IRubyObject> ivar : getInstanceVariableList()) {
             part.append(sep);
             part.append(" ");
             part.append(ivar.getName());
             part.append("=");
             part.append(ivar.getValue().callMethod(getRuntime().getCurrentContext(), "inspect"));
             sep = ",";
         }
         part.append(">");
         return part;
     }
 
     /** rb_obj_inspect
      *
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         if ((!isImmediate()) &&
                 // TYPE(obj) == T_OBJECT
                 !(this instanceof RubyClass) &&
                 this != runtime.getObject() &&
                 this != runtime.getModule() &&
                 !(this instanceof RubyModule) &&
                 // TODO: should have #hasInstanceVariables method, though
                 // this will work here:
                 hasVariables()) {
 
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
 
             if (runtime.isInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return runtime.newString(part.toString());
             }
             try {
                 runtime.registerInspecting(this);
                 return runtime.newString(inspectObj(part).toString());
             } finally {
                 runtime.unregisterInspecting(this);
             }
         }
 
         if (isNil()) return RubyNil.inspect(this);
         return RuntimeHelpers.invoke(runtime.getCurrentContext(), this, MethodIndex.TO_S, "to_s", IRubyObject.NULL_ARRAY);
     }
 
     /** rb_obj_is_instance_of
      *
      */
     @JRubyMethod(name = "instance_of?", required = 1)
     public RubyBoolean instance_of_p(IRubyObject type) {
         if (type() == type) {
             return getRuntime().getTrue();
         } else {
             if (!(type instanceof RubyModule)) {
                 throw getRuntime().newTypeError("class or module required");
             }
             return getRuntime().getFalse();
         }
     }
 
 
     /** rb_obj_is_kind_of
      *
      */
     @JRubyMethod(name = {"kind_of?", "is_a?"}, required = 1)
     public RubyBoolean kind_of_p(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!(type instanceof RubyModule)) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError("class or module required");
         }
 
         return getRuntime().newBoolean(((RubyModule)type).isInstance(this));
     }
 
     /** rb_obj_methods
      *
      */
     @JRubyMethod(name = "methods", optional = 1)
     public IRubyObject methods(IRubyObject[] args) {
         boolean all = true;
         if (args.length == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray singletonMethods = null;
         if (getMetaClass().isSingleton()) {
             singletonMethods =
                     getMetaClass().instance_methods(new IRubyObject[] {getRuntime().getFalse()});
             if (all) {
                 singletonMethods.concat(getMetaClass().getSuperClass().instance_methods(new IRubyObject[] {getRuntime().getTrue()}));
             }
         } else {
             if (all) {
                 singletonMethods = getMetaClass().instance_methods(new IRubyObject[] {getRuntime().getTrue()});
             } else {
                 singletonMethods = getRuntime().newEmptyArray();
             }
         }
         
         return singletonMethods;
     }
 
     @JRubyMethod(name = "public_methods", optional = 1)
     public IRubyObject public_methods(IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().public_instance_methods(args);
     }
 
     /** rb_obj_protected_methods
      *
      */
     @JRubyMethod(name = "protected_methods", optional = 1)
     public IRubyObject protected_methods(IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().protected_instance_methods(args);
     }
 
     /** rb_obj_private_methods
      *
      */
     @JRubyMethod(name = "private_methods", optional = 1)
     public IRubyObject private_methods(IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getTrue() };
         }
 
         return getMetaClass().private_instance_methods(args);
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     @JRubyMethod(name = "singleton_methods", optional = 1)
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(args.length == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray singletonMethods = null;
         if (getMetaClass().isSingleton()) {
             singletonMethods =
                     getMetaClass().instance_methods(new IRubyObject[] {getRuntime().getFalse()});
             if (all) {
                 RubyClass superClass = getMetaClass().getSuperClass();
                 while (superClass.isIncluded()) {
                     singletonMethods.concat(superClass.instance_methods(new IRubyObject[] {getRuntime().getFalse()}));
                     superClass = superClass.getSuperClass();
                 }
             }
         } else {
             singletonMethods = getRuntime().newEmptyArray();
         }
         
         return singletonMethods;
     }
 
     @JRubyMethod(name = "method", required = 1)
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asJavaString(), true);
     }
 
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
     	return anyToString();
     }
     
     @JRubyMethod(name = "to_a", visibility = Visibility.PUBLIC)
     public RubyArray to_a() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "default 'to_a' will be obsolete", "to_a");
         return getRuntime().newArray(this);
     }
 
     @JRubyMethod(name = "instance_eval", optional = 3, frame = true)
     public IRubyObject instance_eval(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz;
         if (isImmediate()) {
             klazz = context.getPreviousFrame().getKlazz();
             if (klazz == null) klazz = getRuntime().getObject();
         } else {
             klazz = getSingletonClass();
         }
         return specificEval(context, klazz, args, block);
     }
 
     @JRubyMethod(name = "instance_exec", optional = 3, frame = true)
     public IRubyObject instance_exec(ThreadContext context, IRubyObject[] args, Block block) {
         if (!block.isGiven()) {
             throw getRuntime().newArgumentError("block not supplied");
         }
 
         RubyModule klazz;
         if (isImmediate()) {
             klazz = context.getPreviousFrame().getKlazz();
             if (klazz == null) klazz = getRuntime().getObject();            
         } else {
             klazz = getSingletonClass();
         }
 
         return yieldUnder(context, klazz, args, block);
     }
 
     @JRubyMethod(name = "extend", required = 1, rest = true)
     public IRubyObject extend(IRubyObject[] args) {
         // Make sure all arguments are modules before calling the callbacks
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isModule()) throw getRuntime().newTypeError(args[i], getRuntime().getModule()); 
         }
 
         // MRI extends in order from last to first
         for (int i = args.length - 1; i >= 0; i--) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
     public IRubyObject initialize() {
         return getRuntime().getNil();
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
     @JRubyMethod(name = {"send", "__send__"}, required = 1, rest = true)
     public IRubyObject send(ThreadContext context, IRubyObject[] args, Block block) {
         String name = args[0].asJavaString();
         int newArgsLength = args.length - 1;
 
         IRubyObject[] newArgs;
         if (newArgsLength == 0) {
             newArgs = IRubyObject.NULL_ARRAY;
         } else {
             newArgs = new IRubyObject[newArgsLength];
             System.arraycopy(args, 1, newArgs, 0, newArgs.length);
         }
 
         RubyModule rubyClass = getMetaClass();
         DynamicMethod method = rubyClass.searchMethod(name);
 
         // send doesn't check visibility
         if (method.isUndefined()) {
             return RuntimeHelpers.callMethodMissing(context, this, method, name, newArgs, context.getFrameSelf(), CallType.FUNCTIONAL, block);
         }
 
         return method.call(context, this, rubyClass, name, newArgs, block);
     }
     
     @JRubyMethod(name = "nil?")
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "=~", required = 1)
     public IRubyObject op_match(ThreadContext context, IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
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
  
     public void addFinalizer(IRubyObject finalizer) {
         if (this.finalizer == null) {
             this.finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
             getRuntime().addFinalizer(this.finalizer);
         }
         this.finalizer.addFinalizer(finalizer);
     }
 
     public void removeFinalizers() {
         if (finalizer != null) {
             finalizer.removeFinalizers();
             finalizer = null;
             getRuntime().removeFinalizer(this.finalizer);
         }
     }
 
 
     //
     // INSTANCE VARIABLE RUBY METHODS
     //
     
     @JRubyMethod(name = "instance_variable_defined?", required = 1)
     public IRubyObject instance_variable_defined_p(IRubyObject name) {
         if (variableTableContains(validateInstanceVariable(name.asJavaString()))) {
             return getRuntime().getTrue();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "instance_variable_get", required = 1)
     public IRubyObject instance_variable_get(IRubyObject name) {
         IRubyObject value;
         if ((value = variableTableFetch(validateInstanceVariable(name.asJavaString()))) != null) {
             return value;
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "instance_variable_set", required = 2)
     public IRubyObject instance_variable_set(IRubyObject name, IRubyObject value) {
         ensureInstanceVariablesSettable();
         return variableTableStore(validateInstanceVariable(name.asJavaString()), value);
     }
 
     @JRubyMethod(name = "remove_instance_variable", required = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject remove_instance_variable(IRubyObject name, Block block) {
         ensureInstanceVariablesSettable();
         IRubyObject value;
         if ((value = variableTableRemove(validateInstanceVariable(name.asJavaString()))) != null) {
             return value;
         }
         throw getRuntime().newNameError("instance variable " + name.asJavaString() + " not defined", name.asJavaString());
     }
     
     @JRubyMethod(name = "instance_variables")
     public RubyArray instance_variables() {
         Ruby runtime = getRuntime();
         List<String> nameList = getInstanceVariableNameList();
 
         RubyArray array = runtime.newArray(nameList.size());
 
         for (String name : nameList) {
             array.append(runtime.newString(name));
         }
 
         return array;
     }
 
     //
     // INSTANCE VARIABLE API METHODS
     //
     
     public InstanceVariables getInstanceVariables() {
         return this;
     }
     
     public boolean hasInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return variableTableContains(name);
     }
     
     public boolean fastHasInstanceVariable(String internedName) {
         assert IdUtil.isInstanceVariable(internedName);
         return variableTableFastContains(internedName);
     }
     
     public IRubyObject getInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return variableTableFetch(name);
     }
     
     public IRubyObject fastGetInstanceVariable(String internedName) {
         assert IdUtil.isInstanceVariable(internedName);
         return variableTableFastFetch(internedName);
     }
 
     /** rb_iv_set / rb_ivar_set
     *
     */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         assert IdUtil.isInstanceVariable(name) && value != null;
         ensureInstanceVariablesSettable();
         return variableTableStore(name, value);
     }
     
     public IRubyObject fastSetInstanceVariable(String internedName, IRubyObject value) {
         assert IdUtil.isInstanceVariable(internedName) && value != null;
         ensureInstanceVariablesSettable();
         return variableTableFastStore(internedName, value);
      }
 
     public IRubyObject removeInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         ensureInstanceVariablesSettable();
         return variableTableRemove(name);
     }
 
     public List<Variable<IRubyObject>> getInstanceVariableList() {
         VariableTableEntry[] table = variableTableGetTable();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         IRubyObject readValue;
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if (IdUtil.isInstanceVariable(e.name)) {
                     if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                     list.add(new VariableEntry<IRubyObject>(e.name, readValue));
                 }
             }
         }
         return list;
     }
 
     public List<String> getInstanceVariableNameList() {
         VariableTableEntry[] table = variableTableGetTable();
         ArrayList<String> list = new ArrayList<String>();
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if (IdUtil.isInstanceVariable(e.name)) {
                     list.add(e.name);
                 }
             }
         }
         return list;
     }
 
     protected static final String ERR_INSECURE_SET_INST_VAR  = "Insecure: can't modify instance variable";
 
     protected String validateInstanceVariable(String name) {
         if (IdUtil.isValidInstanceVariableName(name)) {
             return name;
         }
         throw getRuntime().newNameError("`" + name + "' is not allowable as an instance variable name", name);
     }
 
     protected void ensureInstanceVariablesSettable() {
         if (!isFrozen() && (getRuntime().getSafeLevel() < 4 || isTaint())) {
             return;
         }
         
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError(ERR_INSECURE_SET_INST_VAR);
         }
         if (isFrozen()) {
             if (this instanceof RubyModule) {
                 throw getRuntime().newFrozenError("class/module ");
             } else {
                 throw getRuntime().newFrozenError("");
             }
         }
     }
 
     //
     // INTERNAL VARIABLE METHODS
     //
     
     public InternalVariables getInternalVariables() {
         return this;
     }
     
     public boolean hasInternalVariable(String name) {
         assert !isRubyVariable(name);
         return variableTableContains(name);
     }
 
     public boolean fastHasInternalVariable(String internedName) {
         assert !isRubyVariable(internedName);
         return variableTableFastContains(internedName);
     }
 
     public IRubyObject getInternalVariable(String name) {
         assert !isRubyVariable(name);
         return variableTableFetch(name);
     }
 
     public IRubyObject fastGetInternalVariable(String internedName) {
         assert !isRubyVariable(internedName);
         return variableTableFastFetch(internedName);
     }
 
     public void setInternalVariable(String name, IRubyObject value) {
         assert !isRubyVariable(name);
         variableTableStore(name, value);
     }
 
     public void fastSetInternalVariable(String internedName, IRubyObject value) {
         assert !isRubyVariable(internedName);
         variableTableFastStore(internedName, value);
     }
 
     public IRubyObject removeInternalVariable(String name) {
         assert !isRubyVariable(name);
         return variableTableRemove(name);
     }
 
     public void syncVariables(List<Variable<IRubyObject>> variables) {
         variableTableSync(variables);
     }
 
     public List<Variable<IRubyObject>> getInternalVariableList() {
         VariableTableEntry[] table = variableTableGetTable();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         IRubyObject readValue;
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if (!isRubyVariable(e.name)) {
                     if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                     list.add(new VariableEntry<IRubyObject>(e.name, readValue));
                 }
             }
         }
         return list;
     }
 
     
     //
     // COMMON VARIABLE METHODS
     //
 
     /**
      * Returns true if object has any variables, defined as:
      * <ul>
      * <li> instance variables
      * <li> class variables
      * <li> constants
      * <li> internal variables, such as those used when marshaling Ranges and Exceptions
      * </ul>
      * @return true if object has any variables, else false
      */
     public boolean hasVariables() {
         return variableTableGetSize() > 0;
     }
 
     public int getVariableCount() {
         return variableTableGetSize();
     }
     
     // TODO: must override in RubyModule to pick up constants
diff --git a/test/test_method_missing.rb b/test/test_method_missing.rb
index ff41357a07..c0d977e2b1 100644
--- a/test/test_method_missing.rb
+++ b/test/test_method_missing.rb
@@ -1,88 +1,97 @@
 require 'test/unit'
 
 class TestMethodMissing < Test::Unit::TestCase
 
     class AParent
         def method_missing *args
         end
     end
 
     class AChild < AParent
         def foo
             super
         end
     end
     
     class AMethodMissingClass
         def method_missing name, *args
             1
         end
     end
     
     class AClassWithProtectedAndPrivateMethod
         private
         def a_private_method
         end
 
         protected
         def a_protected_method
         end
     end
 
 
     def test_super_method_missing
         assert_nothing_raised{AChild.new.foo}
     end
 
     def test_private_method_missing
         assert_raise(NoMethodError){AClassWithProtectedAndPrivateMethod.new.a_private_method}
         begin
             AClassWithProtectedAndPrivateMethod.new.a_private_method
         rescue Exception => e
             assert(e.message =~ /private method/)
         end
     end
 
     def test_protected_method_missing
         assert_raise(NoMethodError){AClassWithProtectedAndPrivateMethod.new.a_protected_method}
         begin
             AClassWithProtectedAndPrivateMethod.new.a_protected_method
         rescue Exception => e
             assert(e.message =~ /protected method/)
         end
     end
     
     def test_undefined_method_missing
         assert_raise(NoMethodError){AClassWithProtectedAndPrivateMethod.new.a_missing_method}
         begin
             AClassWithProtectedAndPrivateMethod.new.a_missing_method
         rescue Exception => e
             assert(e.message =~ /undefined method/)
         end
     end
     
     def test_no_method_error_args
         begin
             some_method "a", 1
         rescue Exception => e
             assert_equal(e.args, ["a",1])
             assert_equal(e.class, NoMethodError)
         end
     end
 
     def test_undef_method_and_clone_singleton_class
         s = "a string"
         s.instance_eval do
             (class << self;self;end).class_eval do
                 undef_method :length
             end
         end
         assert_raise(NoMethodError){s.clone.length}
         assert_nothing_raised{s.dup.length}
     end
     
     def test_attr_assign_missing_returns_rhs
         assert_equal(AMethodMissingClass.new.foo=2, 2)
         assert_equal(eval("AMethodMissingClass.new.foo=2"), 2)
     end
+
+    def test_no_super_method_in_module
+        Kernel.module_eval do
+            def a_module_method arg
+                super
+            end
+        end
+        assert_raise(NoMethodError){a_module_method 'foo'}
+    end
 end