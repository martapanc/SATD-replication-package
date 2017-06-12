diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 76922a56da..2a63701a9a 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1423 +1,1425 @@
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
     public Ruby getRuntime() {
         return getMetaClass().getRuntime();
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public final RubyClass getMetaClass() {
         if (Ruby.RUNTIME_THREADLOCAL && metaClass == null && metaClassName != null) {
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
 
+       ((MetaClass)clone).setAttached(((MetaClass)klass).getAttached());
+
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
         
         assert superClass != null : "Superclass should always be something for " + klazz.getBaseName();
 
         return RuntimeHelpers.invokeAs(context, superClass, this, context.getFrameName(), args, CallType.SUPER, block);
     }    
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return RuntimeHelpers.invoke(context, this, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, this, name, new IRubyObject[] { arg }, CallType.FUNCTIONAL, Block.NULL_BLOCK);
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
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, new IRubyObject[] { this }, block);
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
         args[0] = args[0].convertToString();
 
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
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // Line numbers are zero-based so we subtract one
                 int lineNumber = (int) (args[3].convertToInteger().getLongValue() - 1);
 
                 return ASTInterpreter.evalSimple(source.getRuntime().getCurrentContext(),
                                   args[0], source, filename.convertToString().toString(), lineNumber);
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
     }
 
     private IRubyObject yieldUnder(RubyModule under, IRubyObject[] args, Block block) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
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
     public IRubyObject op_equal(IRubyObject obj) {
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
     public IRubyObject op_eqq(IRubyObject other) {
         if(this == other || callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==",other).isTrue()){
             return getRuntime().getTrue();
         }
  
         return getRuntime().getFalse();
     }
     
     protected static IRubyObject equalInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         if (that == other) return that.getRuntime().getTrue();
         return that.callMethod(context, MethodIndex.EQUALEQUAL, "==", other);
     }
 
     protected static boolean eqlInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         if (that == other) return true;
         return that.callMethod(context, MethodIndex.EQL_P, "eql?", other).isTrue();
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
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
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
             throw getRuntime().newTypeError(type, getRuntime().getModule());
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
     public IRubyObject instance_eval(IRubyObject[] args, Block block) {
         RubyModule klazz;
         if (isImmediate()) {
             klazz = getRuntime().getCurrentContext().getPreviousFrame().getKlazz();
             if (klazz == null) klazz = getRuntime().getObject();
         } else {
             klazz = getSingletonClass();
         }
         return specificEval(klazz, args, block);
     }
 
     @JRubyMethod(name = "instance_exec", optional = 3, frame = true)
     public IRubyObject instance_exec(IRubyObject[] args, Block block) {
         if (!block.isGiven()) {
             throw getRuntime().newArgumentError("block not supplied");
         }
 
         RubyModule klazz;
         if (isImmediate()) {
             klazz = getRuntime().getCurrentContext().getPreviousFrame().getKlazz();
             if (klazz == null) klazz = getRuntime().getObject();            
         } else {
             klazz = getSingletonClass();
         }
 
         return yieldUnder(klazz, args, block);
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
     public IRubyObject send(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asJavaString();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         ThreadContext context = getRuntime().getCurrentContext();
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyClass = getMetaClass();
         method = rubyClass.searchMethod(name);
 
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
     public IRubyObject op_match(IRubyObject arg) {
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
 
diff --git a/test/test_higher_javasupport.rb b/test/test_higher_javasupport.rb
index bda04a2156..d3994261f2 100644
--- a/test/test_higher_javasupport.rb
+++ b/test/test_higher_javasupport.rb
@@ -1,651 +1,661 @@
 require 'java'
 require 'test/unit'
 
 TopLevelConstantExistsProc = Proc.new do
   include_class 'java.lang.String'
 end
 
 class TestHigherJavasupport < Test::Unit::TestCase
   TestHelper = org.jruby.test.TestHelper
   JArray = ArrayList = java.util.ArrayList
   FinalMethodBaseTest = org.jruby.test.FinalMethodBaseTest
 
   def test_java_passing_class
     assert_equal("java.util.ArrayList", TestHelper.getClassName(ArrayList))
   end
 
   @@include_java_lang = Proc.new {
       include_package "java.lang"
       java_alias :JavaInteger, :Integer
   }
 
   def test_java_class_loading_and_class_name_collisions
     assert_raises(NameError) { System }
     @@include_java_lang.call
     assert_nothing_raised { System }
     assert_equal(10, JavaInteger.new(10).intValue)
     assert_raises(NoMethodError) { Integer.new(10) }
   end
 
   Random = java.util.Random
   Double = java.lang.Double
   def test_constructors_and_instance_methods
     r = Random.new
     assert_equal(Random, r.class)
     r = Random.new(1001)
     assert_equal(10.0, Double.new(10).doubleValue())
     assert_equal(10.0, Double.new("10").doubleValue())
 
     assert_equal(Random, r.class)
     assert_equal(Fixnum, r.nextInt.class)
     assert_equal(Fixnum, r.nextInt(10).class)
   end
 
   Long = java.lang.Long
   def test_instance_methods_differing_only_on_argument_type
     l1 = Long.new(1234)
     l2 = Long.new(1000)
     assert(l1.compareTo(l2) > 0)
   end
 
   def test_dispatching_on_nil
     sb = TestHelper.getInterfacedInstance()
     assert_equal(nil , sb.dispatchObject(nil))
   end
 
   def test_class_methods
     result = java.lang.System.currentTimeMillis()
     assert_equal(Fixnum, result.class)
   end
 
   Boolean = java.lang.Boolean
   def test_class_methods_differing_only_on_argument_type
     assert_equal(true, Boolean.valueOf("true"))
     assert_equal(false, Boolean.valueOf(false))
   end
 
   Character = java.lang.Character
   def test_constants
     assert_equal(9223372036854775807, Long::MAX_VALUE)
     assert(! defined? Character::Y_DATA)  # Known private field in Character
     # class definition with "_" constant causes error
     assert_nothing_raised { org.jruby.javasupport.test.ConstantHolder }
   end
 
   def test_using_arrays
     list = JArray.new
     list.add(10)
     list.add(20)
     array = list.toArray
     assert_equal(10, array[0])
     assert_equal(20, array[1])
     assert_equal(2, array.length)
     array[1] = 1234
     assert_equal(10, array[0])
     assert_equal(1234, array[1])
     assert_equal([10, 1234], array.entries)
     assert_equal(10, array.min)
   end
 
   def test_creating_arrays
     array = Double[3].new
     assert_equal(3, array.length)
     array[0] = 3.14
     array[2] = 17.0
     assert_equal(3.14, array[0])
     assert_equal(17.0, array[2])
   end
 
   Pipe = java.nio.channels.Pipe
   def test_inner_classes
     assert_equal("java.nio.channels.Pipe$SinkChannel",
                  Pipe::SinkChannel.java_class.name)
     assert(Pipe::SinkChannel.instance_methods.include?("keyFor"))
   end
 
   def test_subclasses_and_their_return_types
     l = ArrayList.new
     r = Random.new
     l.add(10)
     assert_equal(10, l.get(0))
     l.add(r)
     r_returned = l.get(1)
     # Since Random is a public class we should get the value casted as that
     assert_equal("java.util.Random", r_returned.java_class.name)
     assert(r_returned.nextInt.kind_of?(Fixnum))
   end
 
   HashMap = java.util.HashMap
   def test_private_classes_interfaces_and_return_types
     h = HashMap.new
     assert_equal(HashMap, h.class)
     h.put("a", 1)
     iter = h.entrySet.iterator
     inner_instance_entry = iter.next
     # The class implements a public interface, MapEntry, so the methods
     # on that should be available, even though the instance is of a
     # private class.
     assert_equal("a", inner_instance_entry.getKey)
   end
 
   class FooArrayList < ArrayList
     $ensureCapacity = false
     def foo
       size
     end
     def ensureCapacity(howmuch)
       $ensureCapacity = true
       super
     end
   end
 
   def test_extending_java_classes
     l = FooArrayList.new
     assert_equal(0, l.foo)
     l.add(100)
     assert_equal(1, l.foo)
     assert_equal(true, $ensureCapacity)
   end
 
   def test_extending_java_interfaces
     if java.lang.Comparable.instance_of?(Module)
       anonymous = Class.new(Object)
       anonymous.send :include, java.lang.Comparable
       anonymous.send :include, java.lang.Runnable
       assert anonymous < java.lang.Comparable
       assert anonymous < java.lang.Runnable
       assert anonymous.new.kind_of?(java.lang.Runnable)
       assert anonymous.new.kind_of?(java.lang.Comparable)
     else
       assert Class.new(java.lang.Comparable)
     end
   end
 
   def test_support_of_other_class_loaders
     assert_helper_class = Java::JavaClass.for_name("org.jruby.test.TestHelper")
     assert_helper_class2 = Java::JavaClass.for_name("org.jruby.test.TestHelper")
     assert(assert_helper_class.java_class == assert_helper_class2.java_class, "Successive calls return the same class")
     method = assert_helper_class.java_method('loadAlternateClass')
     alt_assert_helper_class = method.invoke_static()
 
     constructor = alt_assert_helper_class.constructor();
     alt_assert_helper = constructor.new_instance();
     identityMethod = alt_assert_helper_class.java_method('identityTest')
     identity = Java.java_to_primitive(identityMethod.invoke(alt_assert_helper))
     assert_equal("ABCDEFGH",  identity)
   end
 
   module Foo
     include_class("java.util.ArrayList")
   end
 
   include_class("java.lang.String") {|package,name| "J#{name}" }
   include_class ["java.util.Hashtable", "java.util.Vector"]
 
   def test_class_constants_defined_under_correct_modules
     assert_equal(0, Foo::ArrayList.new.size)
     assert_equal("a", JString.new("a").to_s)
     assert_equal(0, Vector.new.size)
     assert_equal(0, Hashtable.new.size)
   end
 
   def test_high_level_java_should_only_deal_with_proxies_and_not_low_level_java_class
     a = JString.new
     assert(a.getClass().class != "Java::JavaClass")
   end
 
   # We had a problem with accessing singleton class versus class earlier. Sanity check
   # to make sure we are not writing class methods to the same place.
   include_class 'org.jruby.test.AlphaSingleton'
   include_class 'org.jruby.test.BetaSingleton'
 
   def test_make_sure_we_are_not_writing_class_methods_to_the_same_place
     assert_nothing_raised { AlphaSingleton.getInstance.alpha }
   end
 
   include_class 'org.jruby.javasupport.test.Color'
   def test_lazy_proxy_method_tests_for_alias_and_respond_to
     color = Color.new('green')
     assert_equal(true, color.respond_to?(:setColor))
     assert_equal(false, color.respond_to?(:setColorBogus))
   end
 
   class MyColor < Color
     alias_method :foo, :getColor
     def alias_test
       alias_method :foo2, :setColorReallyBogus
     end
   end
 
   def test_accessor_methods
     my_color = MyColor.new('blue')
     assert_equal('blue', my_color.foo)
     assert_raises(NoMethodError) { my_color.alias_test }
     my_color.color = 'red'
     assert_equal('red', my_color.color)
     my_color.setDark(true)
     assert_equal(true, my_color.dark?)
     my_color.dark = false
     assert_equal(false, my_color.dark?)
   end
 
   # No explicit test, but implicitly EMPTY_LIST.each should not blow up interpreter
   # Old error was EMPTY_LIST is a private class implementing a public interface with public methods
   include_class 'java.util.Collections'
   def test_empty_list_each_should_not_blow_up_interpreter
     assert_nothing_raised { Collections::EMPTY_LIST.each {|element| } }
   end
 
   def test_already_loaded_proxies_should_still_see_extend_proxy
     JavaUtilities.extend_proxy('java.util.List') do
       def foo
         true
       end
     end
     assert_equal(true, Foo::ArrayList.new.foo)
   end
     
   def test_same_proxy_does_not_raise
     # JString already included and it is the same proxy, so do not throw an error
     # (e.g. intent of include_class already satisfied)
     assert_nothing_raised do
       begin
         old_stream = $stderr.dup
         $stderr.reopen(RUBY_PLATFORM =~ /mswin/ ? 'NUL:' : '/dev/null')
         $stderr.sync = true
         class << self
           include_class("java.lang.String") {|package,name| "J#{name}" }
         end
       ensure
         $stderr.reopen(old_stream)
       end
     end
   end
 
   include_class 'java.util.Calendar'
   def test_date_time_conversion
     # Test java.util.Date <=> Time implicit conversion
     calendar = Calendar.getInstance
     calendar.setTime(Time.at(0))
     java_date = calendar.getTime
 
     assert_equal(java_date.getTime, Time.at(0).to_i)
   end
 
   def test_expected_java_string_methods_exist
     # test that the list of JString methods contains selected methods from Java
     jstring_methods = %w[bytes charAt char_at compareTo compareToIgnoreCase compare_to
       compare_to_ignore_case concat contentEquals content_equals endsWith
       ends_with equals equalsIgnoreCase equals_ignore_case getBytes getChars
       getClass get_bytes get_chars get_class hashCode hash_code indexOf
       index_of intern java_class java_object java_object= lastIndexOf last_index_of
       length matches notify notifyAll notify_all regionMatches region_matches replace
       replaceAll replaceFirst replace_all replace_first split startsWith starts_with
       subSequence sub_sequence substring taint tainted? toCharArray toLowerCase
       toString toUpperCase to_char_array to_java_object to_lower_case to_string
       to_upper_case trim wait]
 
     jstring_methods.each { |method| assert(JString.public_instance_methods.include?(method), "#{method} is missing from JString") }
   end
 
   def test_direct_package_access
     a = java.util.ArrayList.new
     assert_equal(0, a.size)
   end
 
   Properties = Java::java.util.Properties
   def test_declare_constant
     p = Properties.new
     p.setProperty("a", "b")
     assert_equal("b", p.getProperty("a"))
   end
 
   if java.awt.event.ActionListener.instance_of?(Module)
     class MyBadActionListener
       include java.awt.event.ActionListener
     end
   else
     class MyBadActionListener < java.awt.event.ActionListener
     end
   end
 
   def test_expected_missing_interface_method
     assert_raises(NoMethodError) { MyBadActionListener.new.actionPerformed }
   end
 
   def test_that_misspelt_fq_class_names_dont_stop_future_fq_class_names_with_same_inner_most_package
     assert_raises(NameError) { Java::java.til.zip.ZipFile }
     assert_nothing_raised { Java::java.util.zip.ZipFile }
   end
 
   def test_that_subpackages_havent_leaked_into_other_packages
     assert_equal(false, Java::java.respond_to?(:zip))
     assert_equal(false, Java::com.respond_to?(:util))
   end
 
   def test_that_sub_packages_called_java_javax_com_org_arent_short_circuited
     #to their top-level conterparts
     assert(!com.equal?(java.flirble.com))
   end
 
   def test_that_we_get_the_same_package_instance_on_subsequent_calls
     assert(com.flirble.equal?(com.flirble))
   end
 
   @@include_proc = Proc.new do
     Thread.stop
     include_class "java.lang.System"
     include_class "java.lang.Runtime"
     Thread.current[:time] = System.currentTimeMillis
     Thread.current[:mem] = Runtime.getRuntime.freeMemory
   end
 
   # Disabled temporarily...keeps failing for no obvious reason
 =begin
   def test_that_multiple_threads_including_classes_dont_step_on_each_other
     # we swallow the output to $stderr, so testers don't have to see the
     # warnings about redefining constants over and over again.
     threads = []
 
     begin
       old_stream = $stderr.dup
       $stderr.reopen(RUBY_PLATFORM =~ /mswin/ ? 'NUL:' : '/dev/null')
       $stderr.sync = true
 
       50.times do
         threads << Thread.new(&@@include_proc)
       end
 
       # wait for threads to all stop, then wake them up
       threads.each {|t| Thread.pass until t.stop?}
       threads.each {|t| t.run}
       # join each to let them run
       threads.each {|t| t.join }
       # confirm they all successfully called currentTimeMillis and freeMemory
     ensure
       $stderr.reopen(old_stream)
     end
 
     threads.each do |t|
       assert(t[:time])
       assert(t[:mem])
     end
   end
 =end
 
   unless (java.lang.System.getProperty("java.specification.version") == "1.4")
     if javax.xml.namespace.NamespaceContext.instance_of?(Module)
       class NSCT
         include javax.xml.namespace.NamespaceContext
         # JRUBY-66: No super here...make sure we still work.
         def initialize(arg)
         end
         def getNamespaceURI(prefix)
           'ape:sex'
         end
       end
     else
       class NSCT < javax.xml.namespace.NamespaceContext
         # JRUBY-66: No super here...make sure we still work.
         def initialize(arg)
         end
         def getNamespaceURI(prefix)
           'ape:sex'
         end
       end
     end
     def test_no_need_to_call_super_in_initialize_when_implementing_java_interfaces
       # No error is a pass here for JRUBY-66
       assert_nothing_raised do
         javax.xml.xpath.XPathFactory.newInstance.newXPath.setNamespaceContext(NSCT.new(1))
       end
     end
   end
 
   def test_can_see_inner_class_constants_with_same_name_as_top_level
     # JRUBY-425: make sure we can reference inner class names that match
     # the names of toplevel constants
     ell = java.awt.geom.Ellipse2D
     assert_nothing_raised { ell::Float.new }
   end
 
   def test_that_class_methods_are_being_camel_cased
     assert(java.lang.System.respond_to?("current_time_millis"))
   end
 
   if Java::java.lang.Runnable.instance_of?(Module)
     class TestInitBlock
       include Java::java.lang.Runnable
       def initialize(&block)
         raise if !block
         @bar = block.call
       end
       def bar; @bar; end
     end
   else
     class TestInitBlock < Java::java.lang.Runnable
       def initialize(&block)
         raise if !block
         @bar = block.call
       end
       def bar; @bar; end
     end
   end
 
   def test_that_blocks_are_passed_through_to_the_constructor_for_an_interface_impl
     assert_nothing_raised {
       assert_equal("foo", TestInitBlock.new { "foo" }.bar)
     }
   end
 
   def test_no_collision_with_ruby_allocate_and_java_allocate
     # JRUBY-232
     assert_nothing_raised { java.nio.ByteBuffer.allocate(1) }
   end
 
   # JRUBY-636 and other "extending Java classes"-issues
   class BigInt < java.math.BigInteger
     def initialize(val)
       super(val)
     end
     def test
       "Bit count = #{bitCount}"
     end
   end
 
   def test_extend_java_class
     assert_equal 2, BigInt.new("10").bitCount
     assert_equal "Bit count = 2", BigInt.new("10").test
   end
 
   class TestOS < java.io.OutputStream
     attr_reader :written
     def write(p)
       @written = true
     end
   end
 
   def test_extend_output_stream
     _anos = TestOS.new
     bos = java.io.BufferedOutputStream.new _anos
     bos.write 32
     bos.flush
     assert _anos.written
   end
 
   def test_impl_shortcut
     has_run = false
     java.lang.Runnable.impl do
       has_run = true
     end.run
 
     assert has_run
   end
 
   # JRUBY-674
   OuterClass = org.jruby.javasupport.test.OuterClass
   def test_inner_class_proxies
     assert defined?(OuterClass::PublicStaticInnerClass)
     assert OuterClass::PublicStaticInnerClass.instance_methods.include?("a")
 
     assert !defined?(OuterClass::ProtectedStaticInnerClass)
     assert !defined?(OuterClass::DefaultStaticInnerClass)
     assert !defined?(OuterClass::PrivateStaticInnerClass)
 
     assert defined?(OuterClass::PublicInstanceInnerClass)
     assert OuterClass::PublicInstanceInnerClass.instance_methods.include?("a")
 
     assert !defined?(OuterClass::ProtectedInstanceInnerClass)
     assert !defined?(OuterClass::DefaultInstanceInnerClass)
     assert !defined?(OuterClass::PrivateInstanceInnerClass)
   end
   
   # Test the new "import" syntax
   def test_import
     
     assert_nothing_raised { 
       import java.nio.ByteBuffer
       ByteBuffer.allocate(10)
     }
   end
 
   def test_java_exception_handling
     list = ArrayList.new
     begin
       list.get(5)
       assert(false)
     rescue java.lang.IndexOutOfBoundsException => e
       assert_equal("java.lang.IndexOutOfBoundsException: Index: 5, Size: 0", e.message)
     end
   end
 
   # test for JRUBY-698
   def test_java_method_returns_null
     include_class 'org.jruby.test.ReturnsNull'
     rn = ReturnsNull.new
 
     assert_equal("", rn.returnNull.to_s)
   end
   
   # test for JRUBY-664
   class FinalMethodChildClass < FinalMethodBaseTest
   end
 
   def test_calling_base_class_final_method
     assert_equal("In foo", FinalMethodBaseTest.new.foo)
     assert_equal("In foo", FinalMethodChildClass.new.foo)
   end
 
   # test case for JRUBY-679
   # class Weather < java.util.Observable
   #   def initialize(temp)
   #     super()
   #     @temp = temp
   #   end
   # end
   # class Meteorologist < java.util.Observer
   #   attr_reader :updated
   #   def initialize(weather)
   #     weather.addObserver(self)
   #   end
   #   def update(obs, arg)
   #     @updated = true
   #   end
   # end
   # def test_should_be_able_to_have_different_ctor_arity_between_ruby_subclass_and_java_superclass
   #   assert_nothing_raised do
   #     w = Weather.new(32)
   #     m = Meteorologist.new(w)
   #     w.notifyObservers
   #     assert(m.updated)
   #   end
   # end
   
   class A < java.lang.Object
     include org.jruby.javasupport.test.Interface1
     
     def method1
     end
   end
   A.new
   
   class B < A
   	include org.jruby.javasupport.test.Interface2
   	
   	def method2
   	end
   end
   B.new
   
   class C < B
   end
   C.new
  
   def test_interface_methods_seen
      ci = org.jruby.javasupport.test.ConsumeInterfaces.new
      ci.addInterface1(A.new)
      ci.addInterface1(B.new)
      ci.addInterface2(B.new)
      ci.addInterface1(C.new)
      ci.addInterface2(C.new)
   	
   end
   
   class LCTestA < java::lang::Object
     include org::jruby::javasupport::test::Interface1
 
     def method1
     end
   end
   LCTestA.new
   
   class LCTestB < LCTestA
   	include org::jruby::javasupport::test::Interface2
   	
   	def method2
   	end
   end
   LCTestB.new
   
   class java::lang::Object
     def boo
       'boo!'
     end
   end
    
   def test_lowercase_colon_package_syntax
     assert_equal(java::lang::String, java.lang.String)
     assert_equal('boo!', java.lang.String.new('xxx').boo)
     ci = org::jruby::javasupport::test::ConsumeInterfaces.new
     assert_equal('boo!', ci.boo)
     assert_equal('boo!', LCTestA.new.boo)
     assert_equal('boo!', LCTestB.new.boo)
     ci.addInterface1(LCTestA.new)
     ci.addInterface1(LCTestB.new)
     ci.addInterface2(LCTestB.new)
   end
   
   def test_marsal_java_object_fails
     assert_raises(TypeError) { Marshal.dump(java::lang::Object.new) }
   end
 
   def test_string_from_bytes
     assert_equal('foo', String.from_java_bytes('foo'.to_java_bytes))
   end
   
   # JRUBY-2088
   def test_package_notation_with_arguments
     assert_raises(NoMethodError) do 
       java.lang("ABC").String
     end
 
     assert_raises(NoMethodError) do 
       java.lang.String(123)
     end
     
     assert_raises(NoMethodError) do 
       Java::se("foobar").com.Foobar
     end
   end
+  
+  # JRUBY-1545
+  def test_creating_subclass_to_java_interface_raises_type_error 
+    assert_raises(TypeError) do 
+      eval(<<CLASSDEF)
+class FooXBarBarBar < Java::JavaLang::Runnable
+end
+CLASSDEF
+    end
+  end
 end
 
