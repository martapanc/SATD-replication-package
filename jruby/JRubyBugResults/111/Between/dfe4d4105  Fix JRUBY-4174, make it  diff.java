diff --git a/src/org/jruby/RubyBasicObject.java b/src/org/jruby/RubyBasicObject.java
index ee56f6b3c0..b6d21bf9cf 100644
--- a/src/org/jruby/RubyBasicObject.java
+++ b/src/org/jruby/RubyBasicObject.java
@@ -1,1147 +1,1147 @@
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
  * Copyright (C) 2008 Thomas E Enebo <enebo@acm.org>
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
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicBoolean;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.runtime.builtin.InternalVariables;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.CoreObjectType;
 import org.jruby.util.IdUtil;
 import org.jruby.util.TypeConverter;
 
 /**
  *
  * @author enebo
  */
 public class RubyBasicObject implements Cloneable, IRubyObject, Serializable, Comparable<IRubyObject>, CoreObjectType, InstanceVariables, InternalVariables {
     private static final boolean DEBUG = false;
     
     // The class of this object
     protected transient RubyClass metaClass;
 
     protected int flags; // zeroed by jvm
 
     /**
      * The error message used when some one tries to modify an
      * instance variable in a high security setting.
      */
     protected static final String ERR_INSECURE_SET_INST_VAR  = "Insecure: can't modify instance variable";
 
     public static final int ALL_F = -1;
     public static final int FALSE_F = 1 << 0;
     /**
      * This flag is a bit funny. It's used to denote that this value
      * is nil. It's a bit counterintuitive for a Java programmer to
      * not use subclassing to handle this case, since we have a
      * RubyNil subclass anyway. Well, the reason for it being a flag
      * is that the {@link #isNil()} method is called extremely often. So often
      * that it gives a good speed boost to make it monomorphic and
      * final. It turns out using a flag for this actually gives us
      * better performance than having a polymorphic {@link #isNil()} method.
      */
     public static final int NIL_F = 1 << 1;
     public static final int FROZEN_F = 1 << 2;
     public static final int TAINTED_F = 1 << 3;
     public static final int UNTRUSTED_F = 1 << 4;
 
     public static final int FL_USHIFT = 5;
 
     public static final int USER0_F = (1<<(FL_USHIFT+0));
     public static final int USER1_F = (1<<(FL_USHIFT+1));
     public static final int USER2_F = (1<<(FL_USHIFT+2));
     public static final int USER3_F = (1<<(FL_USHIFT+3));
     public static final int USER4_F = (1<<(FL_USHIFT+4));
     public static final int USER5_F = (1<<(FL_USHIFT+5));
     public static final int USER6_F = (1<<(FL_USHIFT+6));
     public static final int USER7_F = (1<<(FL_USHIFT+7));
 
     public static final int COMPARE_BY_IDENTITY_F = (1<<(FL_USHIFT+8));
 
     /**
      *  A value that is used as a null sentinel in among other places
      *  the RubyArray implementation. It will cause large problems to
      *  call any methods on this object.
      */
     public static final IRubyObject NEVER = new RubyBasicObject();
 
     /**
      * A value that specifies an undefined value. This value is used
      * as a sentinel for undefined constant values, and other places
      * where neither null nor NEVER makes sense.
      */
     public static final IRubyObject UNDEF = new RubyBasicObject();
 
     /**
      * It's not valid to create a totally empty RubyObject. Since the
      * RubyObject is always defined in relation to a runtime, that
      * means that creating RubyObjects from outside the class might
      * cause problems.
      */
     private RubyBasicObject(){};
 
     /**
      * Default allocator instance for all Ruby objects. The only
      * reason to not use this allocator is if you actually need to
      * have all instances of something be a subclass of RubyObject.
      *
      * @see org.jruby.runtime.ObjectAllocator
      */
     public static final ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyBasicObject(runtime, klass);
         }
     };
 
     /**
      * Will create the Ruby class Object in the runtime
      * specified. This method needs to take the actual class as an
      * argument because of the Object class' central part in runtime
      * initialization.
      */
     public static RubyClass createBasicObjectClass(Ruby runtime, RubyClass objectClass) {
         objectClass.index = ClassIndex.OBJECT;
 
-        //objectClass.defineAnnotatedMethods(BasicObjectMethods.class);
+        objectClass.defineAnnotatedMethods(BasicObjectMethods.class);
         objectClass.defineAnnotatedMethods(RubyBasicObject.class);
 
         return objectClass;
     }
 
     /**
      * Interestingly, the Object class doesn't really have that many
      * methods for itself. Instead almost all of the Object methods
      * are really defined on the Kernel module. This class is a holder
      * for all Object methods.
      *
      * @see RubyKernel
      */
     public static class BasicObjectMethods {
         @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
         public static IRubyObject intialize(IRubyObject self) {
             return self.getRuntime().getNil();
         }
     }
 
     /**
      * Standard path for object creation. Objects are entered into ObjectSpace
      * only if ObjectSpace is enabled.
      */
     public RubyBasicObject(Ruby runtime, RubyClass metaClass) {
         assert metaClass != null: "NULL Metaclass!!?!?!";
 
         this.metaClass = metaClass;
 
         if (runtime.isObjectSpaceEnabled()) addToObjectSpace(runtime);
         if (runtime.getSafeLevel() >= 3) taint(runtime);
     }
 
     /**
      * Path for objects that don't taint and don't enter objectspace.
      */
     public RubyBasicObject(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Path for objects who want to decide whether they don't want to be in
      * ObjectSpace even when it is on. (notably used by objects being
      * considered immediate, they'll always pass false here)
      */
     protected RubyBasicObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace, boolean canBeTainted) {
         this.metaClass = metaClass;
 
         if (useObjectSpace) addToObjectSpace(runtime);
         if (canBeTainted && runtime.getSafeLevel() >= 3) taint(runtime);
     }
 
     protected RubyBasicObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
 
         if (useObjectSpace) addToObjectSpace(runtime);
         if (runtime.getSafeLevel() >= 3) taint(runtime);
     }
 
     private void addToObjectSpace(Ruby runtime) {
         assert runtime.isObjectSpaceEnabled();
         runtime.getObjectSpace().add(this);
     }
 
     protected void taint(Ruby runtime) {
         runtime.secure(4);
         if (!isTaint()) {
         	testFrozen();
             setTaint(true);
         }
     }
 
     /** rb_frozen_class_p
      *
      * Helper to test whether this object is frozen, and if it is will
      * throw an exception based on the message.
      */
    protected final void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message);
        }
    }
 
     /** rb_frozen_class_p
      *
      * Helper to test whether this object is frozen, and if it is will
      * throw an exception based on the message.
      */
    protected final void testFrozen() {
        if (isFrozen()) {
            throw getRuntime().newFrozenError("object");
        }
    }
 
     /**
      * Sets or unsets a flag on this object. The only flags that are
      * guaranteed to be valid to use as the first argument is:
      *
      * <ul>
      *  <li>{@link #FALSE_F}</li>
      *  <li>{@link NIL_F}</li>
      *  <li>{@link FROZEN_F}</li>
      *  <li>{@link TAINTED_F}</li>
      *  <li>{@link USER0_F}</li>
      *  <li>{@link USER1_F}</li>
      *  <li>{@link USER2_F}</li>
      *  <li>{@link USER3_F}</li>
      *  <li>{@link USER4_F}</li>
      *  <li>{@link USER5_F}</li>
      *  <li>{@link USER6_F}</li>
      *  <li>{@link USER7_F}</li>
      * </ul>
      *
      * @param flag the actual flag to set or unset.
      * @param set if true, the flag will be set, if false, the flag will be unset.
      */
     public final void setFlag(int flag, boolean set) {
         if (set) {
             flags |= flag;
         } else {
             flags &= ~flag;
         }
     }
 
     /**
      * Get the value of a custom flag on this object. The only
      * guaranteed flags that can be sent in to this method is:
      *
      * <ul>
      *  <li>{@link #FALSE_F}</li>
      *  <li>{@link NIL_F}</li>
      *  <li>{@link FROZEN_F}</li>
      *  <li>{@link TAINTED_F}</li>
      *  <li>{@link USER0_F}</li>
      *  <li>{@link USER1_F}</li>
      *  <li>{@link USER2_F}</li>
      *  <li>{@link USER3_F}</li>
      *  <li>{@link USER4_F}</li>
      *  <li>{@link USER5_F}</li>
      *  <li>{@link USER6_F}</li>
      *  <li>{@link USER7_F}</li>
      * </ul>
      *
      * @param flag the flag to get
      * @return true if the flag is set, false otherwise
      */
     public final boolean getFlag(int flag) {
         return (flags & flag) != 0;
     }
 
     /**
      * See org.jruby.javasupport.util.RuntimeHelpers#invokeSuper
      */
     @Deprecated
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invokeSuper(context, this, args, block);
     }
 
     /**
      * Will invoke a named method with no arguments and no block.
      */
     public final IRubyObject callMethod(ThreadContext context, String name) {
         return RuntimeHelpers.invoke(context, this, name);
     }
 
     /**
      * Will invoke a named method with one argument and no block with
      * functional invocation.
      */
      public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, this, name, arg);
     }
 
     /**
      * Will invoke a named method with the supplied arguments and no
      * block with functional invocation.
      */
     public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, this, name, args);
     }
 
     public final IRubyObject callMethod(String name, IRubyObject... args) {
         return RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, name, args);
     }
 
     public final IRubyObject callMethod(String name) {
         return RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, name);
     }
 
     /**
      * Will invoke a named method with the supplied arguments and
      * supplied block with functional invocation.
      */
     public final IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invoke(context, this, name, args, block);
     }
 
     /**
      * Will invoke an indexed method with the no arguments and no
      * block.
      */
     @Deprecated
     public final IRubyObject callMethod(ThreadContext context, int methodIndex, String name) {
         return RuntimeHelpers.invoke(context, this, name);
     }
 
     /**
      * Will invoke an indexed method with the one argument and no
      * block with a functional invocation.
      */
     @Deprecated
     public final IRubyObject callMethod(ThreadContext context, int methodIndex, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, this, name, arg, Block.NULL_BLOCK);
     }
 
 
     /**
      * Does this object represent nil? See the docs for the {@link
      * #NIL_F} flag for more information.
      */
     public final boolean isNil() {
         return (flags & NIL_F) != 0;
     }
 
     /**
      * Is this value a true value or not? Based on the {@link #FALSE_F} flag.
      */
     public final boolean isTrue() {
         return (flags & FALSE_F) == 0;
     }
 
     /**
      * Is this value a false value or not? Based on the {@link #FALSE_F} flag.
      */
     public final boolean isFalse() {
         return (flags & FALSE_F) != 0;
     }
 
     /**
      * Gets the taint. Shortcut for getFlag(TAINTED_F).
      *
      * @return true if this object is tainted
      */
     public boolean isTaint() {
         return (flags & TAINTED_F) != 0;
     }
 
     /**
      * Sets the taint flag. Shortcut for setFlag(TAINTED_F, taint)
      *
      * @param taint should this object be tainted or not?
      */
     public void setTaint(boolean taint) {
         // JRUBY-4113: callers should not call setTaint on immediate objects
         if (isImmediate()) return;
         
         if (taint) {
             flags |= TAINTED_F;
         } else {
             flags &= ~TAINTED_F;
         }
     }
 
 
     /** OBJ_INFECT
      *
      * Infects this object with traits from the argument obj. In real
      * terms this currently means that if obj is tainted, this object
      * will get tainted too. It's possible to hijack this method to do
      * other infections if that would be interesting.
      */
     public IRubyObject infectBy(IRubyObject obj) {
         if (obj.isTaint()) setTaint(true);
         if (obj.isUntrusted()) setUntrusted(true);
         return this;
     }
 
     final RubyBasicObject infectBy(RubyBasicObject obj) {
         flags |= (obj.flags & (TAINTED_F | UNTRUSTED_F));
         return this;
     }
 
     final RubyBasicObject infectBy(int tuFlags) {
         flags |= (tuFlags & (TAINTED_F | UNTRUSTED_F));
         return this;
     }
 
     /**
      * Is this value frozen or not? Shortcut for doing
      * getFlag(FROZEN_F).
      *
      * @return true if this object is frozen, false otherwise
      */
     public boolean isFrozen() {
         return (flags & FROZEN_F) != 0;
     }
 
     /**
      * Sets whether this object is frozen or not. Shortcut for doing
      * setFlag(FROZEN_F, frozen).
      *
      * @param frozen should this object be frozen?
      */
     public void setFrozen(boolean frozen) {
         if (frozen) {
             flags |= FROZEN_F;
         } else {
             flags &= ~FROZEN_F;
         }
     }
 
 
     /**
      * Is this value untrusted or not? Shortcut for doing
      * getFlag(UNTRUSTED_F).
      *
      * @return true if this object is frozen, false otherwise
      */
     public boolean isUntrusted() {
         return (flags & UNTRUSTED_F) != 0;
     }
 
     /**
      * Sets whether this object is frozen or not. Shortcut for doing
      * setFlag(FROZEN_F, frozen).
      *
      * @param frozen should this object be frozen?
      */
     public void setUntrusted(boolean untrusted) {
         if (untrusted) {
             flags |= UNTRUSTED_F;
         } else {
             flags &= ~UNTRUSTED_F;
         }
     }
 
     /**
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public final RubyClass getMetaClass() {
         return metaClass;
     }
 
     /** rb_singleton_class
      *
      * Note: this method is specialized for RubyFixnum, RubySymbol,
      * RubyNil and RubyBoolean
      *
      * Will either return the existing singleton class for this
      * object, or create a new one and return that.
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
 
     /** rb_make_metaclass
      *
      * Will create a new meta class, insert this in the chain of
      * classes for this specific object, and return the generated meta
      * class.
      */
     public RubyClass makeMetaClass(RubyClass superClass) {
         MetaClass klass = new MetaClass(getRuntime(), superClass, this); // rb_class_boot
         setMetaClass(klass);
 
         klass.setMetaClass(superClass.getRealClass().getMetaClass());
 
         superClass.addSubclass(klass);
 
         return klass;
     }
 
     /**
      * Makes it possible to change the metaclass of an object. In
      * practice, this is a simple version of Smalltalks Become, except
      * that it doesn't work when we're dealing with subclasses. In
      * practice it's used to change the singleton/meta class used,
      * without changing the "real" inheritance chain.
      */
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return getMetaClass().getRealClass();
     }
 
     /**
      * Does this object respond to the specified message? Uses a
      * shortcut if it can be proved that respond_to? haven't been
      * overridden.
      */
     public final boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     /**
      * Will return the runtime that this object is associated with.
      *
      * @return current runtime
      */
     public final Ruby getRuntime() {
         return getMetaClass().getClassRuntime();
     }
 
     /**
      * Will return the Java interface that most closely can represent
      * this object, when working through JAva integration
      * translations.
      */
     public Class getJavaClass() {
         Object obj = dataGetStruct();
         if (obj instanceof JavaObject) {
             return ((JavaObject)obj).getValue().getClass();
         }
         return getClass();
     }
 
     /** rb_to_id
      *
      * Will try to convert this object to a String using the Ruby
      * "to_str" if the object isn't already a String. If this still
      * doesn't work, will throw a Ruby TypeError.
      *
      */
     public String asJavaString() {
         IRubyObject asString = checkStringType();
         if(!asString.isNil()) return ((RubyString)asString).asJavaString();
         throw getRuntime().newTypeError(inspect().toString() + " is not a string");
     }
 
     /** rb_obj_as_string
      *
      * First converts this object into a String using the "to_s"
      * method, infects it with the current taint and returns it. If
      * to_s doesn't return a Ruby String, {@link #anyToString} is used
      * instead.
      */
     public RubyString asString() {
         IRubyObject str = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "to_s");
 
         if (!(str instanceof RubyString)) return (RubyString)anyToString();
         if (isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
  /**
      * Tries to convert this object to a Ruby Array using the "to_ary"
      * method.
      */
     public RubyArray convertToArray() {
         return (RubyArray) TypeConverter.convertToType(this, getRuntime().getArray(), "to_ary");
     }
 
     /**
      * Tries to convert this object to a Ruby Hash using the "to_hash"
      * method.
      */
     public RubyHash convertToHash() {
         return (RubyHash)TypeConverter.convertToType(this, getRuntime().getHash(), "to_hash");
     }
 
     /**
      * Tries to convert this object to a Ruby Float using the "to_f"
      * method.
      */
     public RubyFloat convertToFloat() {
         return (RubyFloat) TypeConverter.convertToType(this, getRuntime().getFloat(), "to_f");
     }
 
     /**
      * Tries to convert this object to a Ruby Integer using the "to_int"
      * method.
      */
     public RubyInteger convertToInteger() {
         return convertToInteger("to_int");
     }
 
     @Deprecated
     public RubyInteger convertToInteger(int methodIndex, String convertMethod) {
         return convertToInteger(convertMethod);
     }
 
     /**
      * Tries to convert this object to a Ruby Integer using the
      * supplied conversion method.
      */
     public RubyInteger convertToInteger(String convertMethod) {
         IRubyObject val = TypeConverter.convertToType(this, getRuntime().getInteger(), convertMethod, true);
         if (!(val instanceof RubyInteger)) throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod + " should return Integer");
         return (RubyInteger)val;
     }
 
     /**
      * Tries to convert this object to a Ruby String using the
      * "to_str" method.
      */
     public RubyString convertToString() {
         return (RubyString) TypeConverter.convertToType(this, getRuntime().getString(), "to_str");
     }
 
     /**
      * Internal method that helps to convert any object into the
      * format of a class name and a hex string inside of #<>.
      */
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     /** rb_check_string_type
      *
      * Tries to return a coerced string representation of this object,
      * using "to_str". If that returns something other than a String
      * or nil, an empty String will be returned.
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = TypeConverter.convertToTypeWithCheck(this, getRuntime().getString(), "to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = RubyString.newEmptyString(getRuntime());
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     * Returns the result of trying to convert this object to an Array
     * with "to_ary".
     */
     public IRubyObject checkArrayType() {
         return TypeConverter.convertToTypeWithCheck(this, getRuntime().getArray(), "to_ary");
     }
 
     // 1.9 rb_check_to_integer
     IRubyObject checkIntegerType(Ruby runtime, IRubyObject obj, String method) {
         if (obj instanceof RubyFixnum) return obj;
         IRubyObject conv = TypeConverter.convertToType(obj, getRuntime().getInteger(), method, false);
         return conv instanceof RubyInteger ? conv : obj.getRuntime().getNil();
     }
 
     /**
      * @see IRubyObject.toJava
      */
     public Object toJava(Class target) {
         if (dataGetStruct() instanceof JavaObject) {
             // for interface impls
 
             JavaObject innerWrapper = (JavaObject)dataGetStruct();
 
             // ensure the object is associated with the wrapper we found it in,
             // so that if it comes back we don't re-wrap it
             getRuntime().getJavaSupport().getObjectProxyCache().put(innerWrapper.getValue(), this);
 
             return innerWrapper.getValue();
         } else {
             return JavaUtil.coerceOtherToType(getRuntime().getCurrentContext(), this, target);
         }
     }
 
     public IRubyObject dup() {
         if (isImmediate()) throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
 
         IRubyObject dup = getMetaClass().getRealClass().allocate();
         if (isTaint()) dup.setTaint(true);
         if (isUntrusted()) dup.setUntrusted(true);
 
         initCopy(dup, this);
 
         return dup;
     }
 
     /** init_copy
      *
      * Initializes a copy with variable and special instance variable
      * information, and then call the initialize_copy Ruby method.
      */
     private static void initCopy(IRubyObject clone, IRubyObject original) {
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         original.copySpecialInstanceVariables(clone);
 
         if (original.hasVariables()) clone.syncVariables(original.getVariableList());
         if (original instanceof RubyModule) ((RubyModule) clone).syncConstants((RubyModule) original);
 
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /**
      * Lots of MRI objects keep their state in non-lookupable ivars
      * (e:g. Range, Struct, etc). This method is responsible for
      * dupping our java field equivalents
      */
     public void copySpecialInstanceVariables(IRubyObject clone) {
     }
 
     /** rb_inspect
      *
      * The internal helper that ensures a RubyString instance is returned
      * so dangerous casting can be omitted
      * Prefered over callMethod(context, "inspect")
      */
     static RubyString inspect(ThreadContext context, IRubyObject object) {
         return RubyString.objAsString(context, object.callMethod(context, "inspect"));
     }
 
     public IRubyObject rbClone() {
         if (isImmediate()) throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
 
         // We're cloning ourselves, so we know the result should be a RubyObject
         RubyObject clone = (RubyObject)getMetaClass().getRealClass().allocate();
         clone.setMetaClass(getSingletonClassClone());
         if (isTaint()) clone.setTaint(true);
 
         initCopy(clone, this);
 
         if (isFrozen()) clone.setFrozen(true);
         if (isUntrusted()) clone.setUntrusted(true);
         return clone;
     }
 
     /** rb_singleton_class_clone
      *
      * Will make sure that if the current objects class is a
      * singleton, it will get cloned.
      *
      * @return either a real class, or a clone of the current singleton class
      */
     protected RubyClass getSingletonClassClone() {
         RubyClass klass = getMetaClass();
 
         if (!klass.isSingleton()) {
             return klass;
         }
 
         MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), ((MetaClass) klass).getAttached());
         clone.flags = flags;
 
         if (this instanceof RubyClass) {
             clone.setMetaClass(clone);
         } else {
             clone.setMetaClass(klass.getSingletonClassClone());
         }
 
         if (klass.hasVariables()) {
             clone.syncVariables(klass.getVariableList());
         }
         clone.syncConstants(klass);
 
         klass.cloneMethods(clone);
 
         ((MetaClass) clone.getMetaClass()).setAttached(clone);
 
         return clone;
     }
 
     /**
      * Specifically polymorphic method that are meant to be overridden
      * by modules to specify that they are modules in an easy way.
      */
     public boolean isModule() {
         return false;
     }
 
     /**
      * Specifically polymorphic method that are meant to be overridden
      * by classes to specify that they are classes in an easy way.
      */
     public boolean isClass() {
         return false;
     }
 
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         if (obj == null) {
             removeInternalVariable("__wrap_struct__");
         } else {
             fastSetInternalVariable("__wrap_struct__", obj);
         }
     }
 
     // The dataStruct is a place where custom information can be
     // contained for core implementations that doesn't necessarily
     // want to go to the trouble of creating a subclass of
     // RubyObject. The OpenSSL implementation uses this heavily to
     // save holder objects containing Java cryptography objects.
     // Java integration uses this to store the Java object ref.
     //protected transient Object dataStruct;
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return fastGetInternalVariable("__wrap_struct__");
     }
 
     // Equivalent of Data_Get_Struct
     // This will first check that the object in question is actually a T_DATA equivalent.
     public synchronized Object dataGetStructChecked() {
         TypeConverter.checkData(this);
         return this.fastGetInternalVariable("__wrap_struct__");
     }
 
     /** rb_obj_id
      *
      * Return the internal id of an object.
      */
     public IRubyObject id() {
         Ruby runtime = getRuntime();
         if (runtime.isObjectSpaceEnabled()) {
             synchronized (this) {
                 return runtime.newFixnum(runtime.getObjectSpace().idOf(this));
             }
         } else {
             return runtime.newFixnum(System.identityHashCode(this));
         }
     }
 
     /** rb_obj_inspect
      *
      *  call-seq:
      *     obj.inspect   => string
      *
      *  Returns a string containing a human-readable representation of
      *  <i>obj</i>. If not overridden, uses the <code>to_s</code> method to
      *  generate the string.
      *
      *     [ 1, 2, 3..4, 'five' ].inspect   #=> "[1, 2, 3..4, \"five\"]"
      *     Time.new.inspect                 #=> "Wed Apr 09 08:54:39 CDT 2003"
      */
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         if ((!isImmediate()) && !(this instanceof RubyModule) && hasVariables()) {
             StringBuilder part = new StringBuilder();
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
         return RuntimeHelpers.invoke(runtime.getCurrentContext(), this, "to_s");
     }
 
 
     /** inspect_obj
      *
      * The internal helper method that takes care of the part of the
      * inspection that inspects instance variables.
      */
     private StringBuilder inspectObj(StringBuilder part) {
         ThreadContext context = getRuntime().getCurrentContext();
         String sep = "";
 
         for (Variable<IRubyObject> ivar : getInstanceVariableList()) {
             part.append(sep).append(" ").append(ivar.getName()).append("=");
             part.append(ivar.getValue().callMethod(context, "inspect"));
             sep = ",";
         }
         part.append(">");
         return part;
     }
 
     // Methods of the Object class (rb_obj_*):
 
 
     @JRubyMethod(name = "!", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_not(ThreadContext context) {
         return context.getRuntime().newBoolean(!this.isTrue());
     }
 
     @JRubyMethod(name = "!=", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_not_equal(ThreadContext context, IRubyObject other) {
         return context.getRuntime().newBoolean(!op_equal(context, other).isTrue());
     }
 
     public int compareTo(IRubyObject other) {
         return (int)callMethod(getRuntime().getCurrentContext(), "<=>", other).convertToInteger().getLongValue();
     }
 
     /** rb_obj_equal
      *
      * Will by default use identity equality to compare objects. This
      * follows the Ruby semantics.
      */
     @JRubyMethod(name = "==", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
     }
 
     /** rb_equal
      *
      * The Ruby "===" method is used by default in case/when
      * statements. The Object implementation first checks Java identity
      * equality and then calls the "==" method too.
      */
     @JRubyMethod(name = "equal?", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return op_equal(context, other);
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "==" method.
      */
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that == other || that.callMethod(context, "==", other).isTrue();
     }
 
     /** method used for Hash key comparison (specialized for String, Symbol and Fixnum)
      *
      * Will by default just call the Ruby method "eql?"
      */
     public boolean eql(IRubyObject other) {
         return callMethod(getRuntime().getCurrentContext(), "eql?", other).isTrue();
     }
 
     /**
      * Adds the specified object as a finalizer for this object.
      */
     public void addFinalizer(IRubyObject f) {
         Finalizer finalizer = (Finalizer)fastGetInternalVariable("__finalizer__");
         if (finalizer == null) {
             finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
             fastSetInternalVariable("__finalizer__", finalizer);
             getRuntime().addFinalizer(finalizer);
         }
         finalizer.addFinalizer(f);
     }
 
     /**
      * Remove all the finalizers for this object.
      */
     public void removeFinalizers() {
         Finalizer finalizer = (Finalizer)fastGetInternalVariable("__finalizer__");
         if (finalizer != null) {
             finalizer.removeFinalizers();
             removeInternalVariable("__finalizer__");
             getRuntime().removeFinalizer(finalizer);
         }
     }
 
     private static final Object[] NULL_OBJECT_ARRAY = new Object[0];
     private volatile Object[] varTable = NULL_OBJECT_ARRAY;
 
     private Object[] getIvarTableForRead() {
         return varTable;
     }
 
     private synchronized Object[] getVariableTableForWrite(int index) {
         if (varTable == NULL_OBJECT_ARRAY) {
             varTable = new Object[getMetaClass().getRealClass().getVariableTableSize()];
         } else if (varTable.length <= index) {
             if (DEBUG) System.out.println("resizing from " + varTable.length + " to " + getMetaClass().getRealClass().getVariableTableSize());
             Object[] newTable = new Object[getMetaClass().getRealClass().getVariableTableSize()];
             System.arraycopy(varTable, 0, newTable, 0, varTable.length);
             varTable = newTable;
         }
         return varTable;
     }
 
     public Object getVariable(int index) {
         if (index < 0) return null;
         Object[] ivarTable = getIvarTableForRead();
         if (ivarTable.length > index) return ivarTable[index];
         return null;
     }
 
     public synchronized void setVariable(int index, Object value) {
         ensureInstanceVariablesSettable();
         if (index < 0) return;
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
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
         return varTable.length > 0;
     }
 
     /**
      * Returns the amount of instance variables, class variables,
      * constants and internal variables this object has.
      */
     public int getVariableCount() {
         return varTable.length;
     }
 
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     public List<Variable<Object>> getVariableList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<Variable<Object>> list = new ArrayList<Variable<Object>>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null) continue;
             list.add(new VariableEntry<Object>(entry.getKey(), value));
         }
         return list;
     }
 
     /**
      * Gets a name list of all variables in this object.
      */
    // TODO: must override in RubyModule to pick up constants
    public List<String> getVariableNameList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<String> list = new ArrayList<String>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null) continue;
             list.add(entry.getKey());
         }
         return list;
     }
 
     /**
      * Checks if the variable table contains a variable of the
      * specified name.
      */
     protected boolean variableTableContains(String name) {
         return getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this) != null;
     }
 
     /**
      * Checks if the variable table contains the the variable of the
      * specified name, where the precondition is that the name must be
      * an interned Java String.
      */
     protected boolean variableTableFastContains(String internedName) {
         return variableTableContains(internedName);
     }
 
     /**
      * Fetch an object from the variable table based on the name.
      *
      * @return the object or null if not found
      */
     protected Object variableTableFetch(String name) {
         return getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this);
     }
 
diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index 49f0739529..9152363573 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,1167 +1,1171 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.java.codegen.RealClassGenerator;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JRubyClassLoader;
 import static org.jruby.util.CodegenUtils.*;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.collections.WeakHashSet;
 import org.objectweb.asm.AnnotationVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.MethodVisitor;
 
 import static org.objectweb.asm.Opcodes.*;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Class", parent="Module")
 public class RubyClass extends RubyModule {
     public static void createClassClass(Ruby runtime, RubyClass classClass) {
         classClass.index = ClassIndex.CLASS;
         classClass.setReifiedClass(RubyClass.class);
         classClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyClass;
             }
         };
         
         classClass.undefineMethod("module_function");
         classClass.undefineMethod("append_features");
         classClass.undefineMethod("extend_object");
         
         classClass.defineAnnotatedMethods(RubyClass.class);
         
         classClass.addMethod("new", new SpecificArityNew(classClass, Visibility.PUBLIC));
         
         // This is a non-standard method; have we decided to start extending Ruby?
         //classClass.defineFastMethod("subclasses", callbackFactory.getFastOptMethod("subclasses"));
         
         // FIXME: for some reason this dispatcher causes a VerifyError...
         //classClass.dispatcher = callbackFactory.createDispatcher(classClass);
     }
     
     public static final ObjectAllocator CLASS_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyClass clazz = new RubyClass(runtime);
             clazz.allocator = ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR; // Class.allocate object is not allocatable before it is initialized
             return clazz;
         }
     };
 
     public ObjectAllocator getAllocator() {
         return allocator;
     }
 
     public void setAllocator(ObjectAllocator allocator) {
         this.allocator = allocator;
     }
 
     /**
      * Set an allocator that calls the default constructor for a given class.
      *
      * @param cls The class on which to call the default constructor to allocate
      */
     public void setClassAllocator(final Class cls) {
         this.allocator = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                 try {
                     return (IRubyObject)cls.newInstance();
                 } catch (InstantiationException ie) {
                     throw runtime.newTypeError("could not allocate " + cls + " with default constructor:\n" + ie);
                 } catch (IllegalAccessException iae) {
                     throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible default constructor:\n" + iae);
                 }
             }
         };
     }
 
     public void setRubyClassAllocator(final Class cls) {
         try {
             final Constructor constructor = cls.getConstructor(Ruby.class, RubyClass.class);
             
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)constructor.newInstance(runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (InstantiationException ie) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ie);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     @JRubyMethod(name = "allocate")
     public IRubyObject allocate() {
-        if (superClass == null) throw runtime.newTypeError("can't instantiate uninitialized class");
+        if (superClass == null) {
+            if(!(runtime.is1_9() && this == runtime.getBasicObject())) {
+                throw runtime.newTypeError("can't instantiate uninitialized class");
+            }
+        }
         IRubyObject obj = allocator.allocate(runtime, this);
         if (obj.getMetaClass().getRealClass() != getRealClass()) throw runtime.newTypeError("wrong instance allocation");
         return obj;
     }
 
     public CallSite[] getBaseCallSites() {
         return baseCallSites;
     }
     
     public CallSite[] getExtraCallSites() {
         return extraCallSites;
     }
 
     public static class VariableAccessor {
         private int index;
         private final int classId;
         public VariableAccessor(int index, int classId) {
             this.index = index;
             this.classId = classId;
         }
         public int getClassId() {
             return classId;
         }
         public int getIndex() {
             return index;
         }
         public Object get(Object object) {
             return ((IRubyObject)object).getVariable(index);
         }
         public void set(Object object, Object value) {
             ((IRubyObject)object).setVariable(index, value);
         }
         public static final VariableAccessor DUMMY_ACCESSOR = new VariableAccessor(-1, -1);
     }
 
     public Map<String, VariableAccessor> getVariableAccessorsForRead() {
         return variableAccessors;
     }
 
     public synchronized Map<String, VariableAccessor> getVariableAccessorsForWrite() {
         if (variableAccessors == Collections.EMPTY_MAP) variableAccessors = new Hashtable<String, VariableAccessor>(1);
         return variableAccessors;
     }
 
     public synchronized VariableAccessor getVariableAccessorForWrite(String name) {
         Map<String, VariableAccessor> myVariableAccessors = getVariableAccessorsForWrite();
         VariableAccessor ivarAccessor = myVariableAccessors.get(name);
         if (ivarAccessor == null) {
             ivarAccessor = new VariableAccessor(myVariableAccessors.size(), this.id);
             myVariableAccessors.put(name, ivarAccessor);
         }
         return ivarAccessor;
     }
 
     public VariableAccessor getVariableAccessorForRead(String name) {
         VariableAccessor accessor = getVariableAccessorsForRead().get(name);
         if (accessor == null) accessor = VariableAccessor.DUMMY_ACCESSOR;
         return accessor;
     }
 
     public int getVariableTableSize() {
         return variableAccessors.size();
     }
 
     public Map<String, VariableAccessor> getVariableTableCopy() {
         return new HashMap<String, VariableAccessor>(getVariableAccessorsForRead());
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.CLASS;
     }
     
     @Override
     public boolean isModule() {
         return false;
     }
 
     @Override
     public boolean isClass() {
         return true;
     }
 
     @Override
     public boolean isSingleton() {
         return false;
     }
 
     /** boot_defclass
      * Create an initial Object meta class before Module and Kernel dependencies have
      * squirreled themselves together.
      * 
      * @param runtime we need it
      * @return a half-baked meta class for object
      */
     public static RubyClass createBootstrapClass(Ruby runtime, String name, RubyClass superClass, ObjectAllocator allocator) {
         RubyClass obj;
 
         if (superClass == null ) {  // boot the Object class 
             obj = new RubyClass(runtime);
             obj.marshal = DEFAULT_OBJECT_MARSHAL;
         } else {                    // boot the Module and Class classes
             obj = new RubyClass(runtime, superClass);
         }
         obj.setAllocator(allocator);
         obj.setBaseName(name);
         return obj;
     }
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), objectSpace);
         this.runtime = runtime;
         this.superClass = superClass; // this is the only case it might be null here (in MetaClass construction)
     }
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, Generation generation, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), generation, objectSpace);
         this.runtime = runtime;
         this.superClass = superClass; // this is the only case it might be null here (in MetaClass construction)
     }
     
     /** used by CLASS_ALLOCATOR (any Class' class will be a Class!)
      *  also used to bootstrap Object class
      */
     protected RubyClass(Ruby runtime) {
         super(runtime, runtime.getClassClass());
         this.runtime = runtime;
         index = ClassIndex.CLASS;
     }
     
     /** rb_class_boot (for plain Classes)
      *  also used to bootstrap Module and Class classes 
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz) {
         this(runtime);
         superClass = superClazz;
         marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         allocator = superClazz.allocator;
         
         infectBy(superClass);        
     }
     
     /** 
      * A constructor which allows passing in an array of supplementary call sites.
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz, CallSite[] extraCallSites) {
         this(runtime);
         this.superClass = superClazz;
         this.marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         
         this.extraCallSites = extraCallSites;
         
         infectBy(superClass);        
     }
 
     /** 
      * Construct a new class with the given name scoped under Object (global)
      * and with Object as its immediate superclass.
      * Corresponds to rb_class_new in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass);        
     }
 
     /** 
      * A variation on newClass that allow passing in an array of supplementary
      * call sites to improve dynamic invocation.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, CallSite[] extraCallSites) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass, extraCallSites);        
     }
 
     /** 
      * Construct a new class with the given name, allocator, parent class,
      * and containing class. If setParent is true, the class's parent will be
      * explicitly set to the provided parent (rather than the new class just
      * being assigned to a constant in that parent).
      * Corresponds to rb_class_new/rb_define_class_id/rb_name_class/rb_set_class_path
      * in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent) {
         RubyClass clazz = newClass(runtime, superClass);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** 
      * A variation on newClass that allows passing in an array of supplementary
      * call sites to improve dynamic invocation performance.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent, CallSite[] extraCallSites) {
         RubyClass clazz = newClass(runtime, superClass, extraCallSites);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** rb_make_metaclass
      *
      */
     @Override
     public RubyClass makeMetaClass(RubyClass superClass) {
         if (isSingleton()) { // could be pulled down to RubyClass in future
             MetaClass klass = new MetaClass(runtime, superClass, this); // rb_class_boot
             setMetaClass(klass);
 
             klass.setMetaClass(klass);
             klass.setSuperClass(getSuperClass().getRealClass().getMetaClass());
             
             return klass;
         } else {
             return super.makeMetaClass(superClass);
         }
     }
     
     @Deprecated
     public IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block) {
         return invoke(context, self, name, args, callType, block);
     }
     
     public boolean notVisibleAndNotMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return !method.isCallableFrom(caller, callType) && !name.equals("method_missing");
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
 
     private void dumpReifiedClass(String dumpDir, String javaPath, byte[] classBytes) {
         if (dumpDir != null) {
             if (dumpDir.equals("")) {
                 dumpDir = ".";
             }
             java.io.FileOutputStream classStream = null;
             try {
                 java.io.File classFile = new java.io.File(dumpDir, javaPath + ".class");
                 classFile.getParentFile().mkdirs();
                 classStream = new java.io.FileOutputStream(classFile);
                 classStream.write(classBytes);
             } catch (IOException io) {
                 getRuntime().getWarnings().warn("unable to dump class file: " + io.getMessage());
             } finally {
                 if (classStream != null) {
                     try {
                         classStream.close();
                     } catch (IOException ignored) {
                     }
                 }
             }
         }
     }
 
     private void generateMethodAnnotations(Map<Class, Map<String, Object>> methodAnnos, SkinnyMethodAdapter m, List<Map<Class, Map<String, Object>>> parameterAnnos) {
         if (methodAnnos != null && methodAnnos.size() != 0) {
             for (Map.Entry<Class, Map<String, Object>> entry : methodAnnos.entrySet()) {
                 m.visitAnnotationWithFields(ci(entry.getKey()), true, entry.getValue());
             }
         }
         if (parameterAnnos != null && parameterAnnos.size() != 0) {
             for (int i = 0; i < parameterAnnos.size(); i++) {
                 Map<Class, Map<String, Object>> annos = parameterAnnos.get(i);
                 if (annos != null && annos.size() != 0) {
                     for (Iterator<Map.Entry<Class, Map<String, Object>>> it = annos.entrySet().iterator(); it.hasNext();) {
                         Map.Entry<Class, Map<String, Object>> entry = it.next();
                         m.visitParameterAnnotationWithFields(i, ci(entry.getKey()), true, entry.getValue());
                     }
                 }
             }
         }
     }
     
     private boolean shouldCallMethodMissing(DynamicMethod method) {
         return method.isUndefined();
     }
     private boolean shouldCallMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return method.isUndefined() || notVisibleAndNotMethodMissing(method, name, caller, callType);
     }
     
     public IRubyObject invokeInherited(ThreadContext context, IRubyObject self, IRubyObject subclass) {
         DynamicMethod method = getMetaClass().searchMethod("inherited");
 
         if (method.isUndefined()) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), "inherited", CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
 
         return method.call(context, self, getMetaClass(), "inherited", subclass, Block.NULL_BLOCK);
     }
 
     /** rb_class_new_instance
     *
     */
     public IRubyObject newInstance(ThreadContext context, IRubyObject[] args, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, this, obj, args, block);
         return obj;
     }
     
     public static class SpecificArityNew extends JavaMethod {
         public SpecificArityNew(RubyModule implClass, Visibility visibility) {
             super(implClass, visibility);
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, args, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, arg2, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
     }
 
     /** rb_class_initialize
      * 
      */
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_8, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_8, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_9, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize19(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, true);
     }
         
     @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_9, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize19(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, true);
     }
 
     private IRubyObject initializeCommon(RubyClass superClazz, Block block, boolean callInheritBeforeSuper) {
         superClass = superClazz;
         allocator = superClazz.allocator;
         makeMetaClass(superClazz.getMetaClass());
 
         marshal = superClazz.marshal;
 
         superClazz.addSubclass(this);
 
         if (callInheritBeforeSuper) {
             inherit(superClazz);
             super.initialize(block);
         } else {
             super.initialize(block);
             inherit(superClazz);
         }
 
         return this;
     }
 
     /** rb_class_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         if (superClass != null) throw runtime.newTypeError("already initialized class");
         if (original instanceof MetaClass) throw runtime.newTypeError("can't copy singleton class");        
         
         super.initialize_copy(original);
         allocator = ((RubyClass)original).allocator; 
         return this;        
     }
 
     protected void setModuleSuperClass(RubyClass superClass) {
         // remove us from old superclass's child classes
         if (this.superClass != null) this.superClass.removeSubclass(this);
         // add us to new superclass's child classes
         superClass.addSubclass(this);
         // update superclass reference
         this.superClass = superClass;
     }
     
     public Collection subclasses(boolean includeDescendants) {
         Set<RubyClass> mySubclasses = subclasses;
         if (mySubclasses != null) {
             Collection<RubyClass> mine = new ArrayList<RubyClass>(mySubclasses);
             if (includeDescendants) {
                 for (RubyClass i: mySubclasses) {
                     mine.addAll(i.subclasses(includeDescendants));
                 }
             }
 
             return mine;
         } else {
             return Collections.EMPTY_LIST;
         }
     }
 
     /**
      * Add a new subclass to the weak set of subclasses.
      *
      * This version always constructs a new set to avoid having to synchronize
      * against the set when iterating it for invalidation in
      * invalidateCacheDescendants.
      *
      * @param subclass The subclass to add
      */
     public synchronized void addSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) subclasses = oldSubclasses = new WeakHashSet<RubyClass>(4);
             oldSubclasses.add(subclass);
         }
     }
     
     /**
      * Remove a subclass from the weak set of subclasses.
      *
      * This version always constructs a new set to avoid having to synchronize
      * against the set when iterating it for invalidation in
      * invalidateCacheDescendants.
      *
      * @param subclass The subclass to remove
      */
     public synchronized void removeSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
         }
     }
 
     /**
      * Replace an existing subclass with a new one.
      *
      * This version always constructs a new set to avoid having to synchronize
      * against the set when iterating it for invalidation in
      * invalidateCacheDescendants.
      *
      * @param subclass The subclass to remove
      * @param newSubclass The subclass to replace it with
      */
     public synchronized void replaceSubclass(RubyClass subclass, RubyClass newSubclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
             oldSubclasses.add(newSubclass);
         }
     }
 
     /**
      * Invalidate all subclasses of this class by walking the set of all
      * subclasses and asking them to invalidate themselves.
      *
      * Note that this version works against a reference to the current set of
      * subclasses, which could be replaced by the time this iteration is
      * complete. In theory, there may be a path by which invalidation would
      * miss a class added during the invalidation process, but the exposure is
      * minimal if it exists at all. The only way to prevent it would be to
      * synchronize both invalidation and subclass set modification against a
      * global lock, which we would like to avoid.
      */
     @Override
     public void invalidateCacheDescendants() {
         super.invalidateCacheDescendants();
         // update all subclasses
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.invalidateCacheDescendants();
             }
         }
     }
     
     public Ruby getClassRuntime() {
         return runtime;
     }
 
     public RubyClass getRealClass() {
         return this;
     }    
 
     @JRubyMethod(name = "inherited", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject inherited(ThreadContext context, IRubyObject arg) {
         return runtime.getNil();
     }
 
     /** rb_class_inherited (reversed semantics!)
      * 
      */
     public void inherit(RubyClass superClazz) {
         if (superClazz == null) superClazz = runtime.getObject();
 
         superClazz.invokeInherited(runtime.getCurrentContext(), superClazz, this);
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */
     @JRubyMethod(name = "superclass", compat = CompatVersion.RUBY1_8)
     public IRubyObject superclass(ThreadContext context) {
         RubyClass superClazz = superClass;
 
         if (superClazz == null) throw runtime.newTypeError("uninitialized class");
 
         if (isSingleton()) superClazz = metaClass;
         while (superClazz != null && superClazz.isIncluded()) superClazz = superClazz.superClass;
 
         return superClazz != null ? superClazz : runtime.getNil();
     }
     
     @JRubyMethod(name = "superclass", compat = CompatVersion.RUBY1_9)
     public IRubyObject superclass19(ThreadContext context) {
         RubyClass superClazz = superClass;
         if (superClazz == null) {
             if (metaClass == runtime.getBasicObject().getMetaClass()) return runtime.getNil();
             throw runtime.newTypeError("uninitialized class");
         }
 
         while (superClazz != null && superClazz.isIncluded()) superClazz = superClazz.superClass;
 
         return superClazz != null ? superClazz : runtime.getNil();
     }
 
     private void checkNotInitialized() {
         if (superClass != null) throw runtime.newTypeError("already initialized class");
     }
     /** rb_check_inheritable
      * 
      */
     public static void checkInheritable(IRubyObject superClass) {
         if (!(superClass instanceof RubyClass)) {
             throw superClass.getRuntime().newTypeError("superclass must be a Class (" + superClass.getMetaClass() + " given)"); 
         }
         if (((RubyClass)superClass).isSingleton()) {
             throw superClass.getRuntime().newTypeError("can't make subclass of virtual class");
         }        
     }
 
     public final ObjectMarshal getMarshal() {
         return marshal;
     }
     
     public final void setMarshal(ObjectMarshal marshal) {
         this.marshal = marshal;
     }
     
     public final void marshal(Object obj, MarshalStream marshalStream) throws IOException {
         getMarshal().marshalTo(runtime, obj, this, marshalStream);
     }
     
     public final Object unmarshal(UnmarshalStream unmarshalStream) throws IOException {
         return getMarshal().unmarshalFrom(runtime, this, unmarshalStream);
     }
     
     public static void marshalTo(RubyClass clazz, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(clazz);
         output.writeString(MarshalStream.getPathFromClass(clazz));
     }
 
     public static RubyClass unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyClass result = UnmarshalStream.getClassFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     protected static final ObjectMarshal DEFAULT_OBJECT_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             IRubyObject object = (IRubyObject)obj;
             
             marshalStream.registerLinkTarget(object);
             marshalStream.dumpVariables(object.getVariableList());
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             IRubyObject result = type.allocate();
             
             unmarshalStream.registerLinkTarget(result);
 
             unmarshalStream.defaultVariablesUnmarshal(result);
 
             return result;
         }
     };
 
     public synchronized void reify() {
         reify(null);
     }
 
     /**
      * Stand up a real Java class for the backing store of this object
      * @param classDumpDir Directory to save reified java class
      */
     public synchronized void reify(String classDumpDir) {
         Class reifiedParent = RubyObject.class;
         String javaName = "ruby." + getBaseName();
         String javaPath = "ruby/" + getBaseName();
         JRubyClassLoader parentCL = runtime.getJRubyClassLoader();
 
         if (superClass.reifiedClass != null) {
             reifiedParent = superClass.reifiedClass;
         }
 
         Class[] interfaces = Java.getInterfacesFromRubyClass(this);
         String[] interfaceNames = new String[interfaces.length];
         for (int i = 0; i < interfaces.length; i++) {
             interfaceNames[i] = p(interfaces[i]);
         }
 
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, javaPath, null, p(reifiedParent),
                 interfaceNames);
 
         if (classAnnotations != null && classAnnotations.size() != 0) {
             for (Map.Entry<Class,Map<String,Object>> entry : classAnnotations.entrySet()) {
                 Class annoType = entry.getKey();
                 Map<String,Object> fields = entry.getValue();
 
                 AnnotationVisitor av = cw.visitAnnotation(ci(annoType), true);
                 CodegenUtils.visitAnnotationFields(av, fields);
                 av.visitEnd();
             }
         }
 
         // fields to hold Ruby and RubyClass references
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "ruby", ci(Ruby.class), null, null);
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "rubyClass", ci(RubyClass.class), null, null);
 
         // static initializing method
         SkinnyMethodAdapter m = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "clinit", sig(void.class, Ruby.class, RubyClass.class), null, null));
         m.start();
         m.aload(0);
         m.putstatic(javaPath, "ruby", ci(Ruby.class));
         m.aload(1);
         m.putstatic(javaPath, "rubyClass", ci(RubyClass.class));
         m.voidreturn();
         m.end();
 
         // standard constructor that accepts Ruby, RubyClass
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", sig(void.class, Ruby.class, RubyClass.class), null, null);
         m = new SkinnyMethodAdapter(mv);
         m.aload(0);
         m.aload(1);
         m.aload(2);
         m.invokespecial(p(reifiedParent), "<init>", sig(void.class, Ruby.class, RubyClass.class));
         m.voidreturn();
         m.end();
 
         // no-arg constructor using static references to Ruby and RubyClass
         mv = cw.visitMethod(ACC_PUBLIC, "<init>", CodegenUtils.sig(void.class), null, null);
         m = new SkinnyMethodAdapter(mv);
         m.aload(0);
         m.getstatic(javaPath, "ruby", ci(Ruby.class));
         m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
         m.invokespecial(p(reifiedParent), "<init>", sig(void.class, Ruby.class, RubyClass.class));
         m.voidreturn();
         m.end();
 
         // toJava method to always pass the actual object
         mv = cw.visitMethod(ACC_PUBLIC, "toJava", CodegenUtils.sig(Object.class, Class.class), null, null);
         m = new SkinnyMethodAdapter(mv);
         m.aload(0);
         m.areturn();
         m.end();
 
         for (Map.Entry<String,DynamicMethod> methodEntry : getMethods().entrySet()) {
             String methodName = methodEntry.getKey();
             String javaMethodName = JavaNameMangler.mangleStringForCleanJavaIdentifier(methodName);
             Map<Class,Map<String,Object>> methodAnnos = getMethodAnnotations().get(methodName);
             List<Map<Class,Map<String,Object>>> parameterAnnos = getParameterAnnotations().get(methodName);
             Class[] methodSignature = getMethodSignatures().get(methodName);
 
             if (methodSignature == null) {
                 // non-signature signature with just IRubyObject
                 switch (methodEntry.getValue().getArity().getValue()) {
                 case 0:
                     mv = cw.visitMethod(ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(IRubyObject.class), null, null);
                     m = new SkinnyMethodAdapter(mv);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.aload(0);
                     m.ldc(methodName);
                     m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class));
                     break;
                 default:
                     mv = cw.visitMethod(ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(IRubyObject.class, IRubyObject[].class), null, null);
                     m = new SkinnyMethodAdapter(mv);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.aload(0);
                     m.ldc(methodName);
                     m.aload(1);
                     m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
                 }
                 m.areturn();
             } else {
                 // generate a real method signature for the method, with to/from coercions
 
                 // indices for temp values
                 Class[] params = new Class[methodSignature.length - 1];
                 System.arraycopy(methodSignature, 1, params, 0, params.length);
                 int baseIndex = 1;
                 for (Class paramType : params) {
                     if (paramType == double.class || paramType == long.class) {
                         baseIndex += 2;
                     } else {
                         baseIndex += 1;
                     }
                 }
                 int rubyIndex = baseIndex;
 
                 mv = cw.visitMethod(ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(methodSignature[0], params), null, null);
                 m = new SkinnyMethodAdapter(mv);
                 generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                 m.getstatic(javaPath, "ruby", ci(Ruby.class));
                 m.astore(rubyIndex);
 
                 m.aload(0); // self
                 m.ldc(methodName); // method name
                 RealClassGenerator.coerceArgumentsToRuby(m, params, rubyIndex);
                 m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
 
                 RealClassGenerator.coerceResultAndReturn(m, methodSignature[0]);
             }
 
             m.end();
         }
 
         cw.visitEnd();
         byte[] classBytes = cw.toByteArray();
         dumpReifiedClass(classDumpDir, javaPath, classBytes);
         Class result = parentCL.defineClass(javaName, classBytes);
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 973fb20023..a97ac9a3b5 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1156 +1,1156 @@
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
 import java.util.HashSet;
 import java.util.List;
 
 import java.util.Set;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IdUtil;
 import org.jruby.runtime.marshal.DataType;
 
 /**
  * RubyObject is the only implementation of the
  * {@link org.jruby.runtime.builtin.IRubyObject}. Every Ruby object in JRuby
  * is represented by something that is an instance of RubyObject. In
  * some of the core class implementations, this means doing a subclass
  * that extends RubyObject, in other cases it means using a simple
  * RubyObject instance and the data field to store specific
  * information about the Ruby object.
  *
  * Some care has been taken to make the implementation be as
  * monomorphic as possible, so that the Java Hotspot engine can
  * improve performance of it. That is the reason for several patterns
  * that might seem odd in this class.
  *
  * The IRubyObject interface used to have lots of methods for
  * different things, but these have now mostly been refactored into
  * several interfaces that gives access to that specific part of the
  * object. This gives us the possibility to switch out that subsystem
  * without changing interfaces again. For example, instance variable
  * and internal variables are handled this way, but the implementation
  * in RubyObject only returns "this" in {@link #getInstanceVariables()} and
  * {@link #getInternalVariables()}.
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Object", include="Kernel")
 public class RubyObject extends RubyBasicObject {
     // Equivalent of T_DATA
     public static class Data extends RubyObject implements DataType {
         public Data(Ruby runtime, RubyClass metaClass, Object data) {
             super(runtime, metaClass);
             dataWrapStruct(data);
         }
 
         public Data(RubyClass metaClass, Object data) {
             super(metaClass);
             dataWrapStruct(data);
         }
     }
 
     /**
      * Standard path for object creation. Objects are entered into ObjectSpace
      * only if ObjectSpace is enabled.
      */
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         super(runtime, metaClass);
     }
 
     /**
      * Path for objects that don't taint and don't enter objectspace.
      */
     public RubyObject(RubyClass metaClass) {
         super(metaClass);
     }
 
     /**
      * Path for objects who want to decide whether they don't want to be in
      * ObjectSpace even when it is on. (notably used by objects being
      * considered immediate, they'll always pass false here)
      */
     protected RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace, boolean canBeTainted) {
         super(runtime, metaClass, useObjectSpace, canBeTainted);
     }
 
     protected RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         super(runtime, metaClass, useObjectSpace);
     }
 
     /**
      * Will create the Ruby class Object in the runtime
      * specified. This method needs to take the actual class as an
      * argument because of the Object class' central part in runtime
      * initialization.
      */
     public static RubyClass createObjectClass(Ruby runtime, RubyClass objectClass) {
         objectClass.index = ClassIndex.OBJECT;
         objectClass.setReifiedClass(RubyObject.class);
 
         objectClass.defineAnnotatedMethods(ObjectMethods.class);
 
         return objectClass;
     }
 
     /**
      * Interestingly, the Object class doesn't really have that many
      * methods for itself. Instead almost all of the Object methods
      * are really defined on the Kernel module. This class is a holder
      * for all Object methods.
      *
      * @see RubyKernel
      */
     public static class ObjectMethods {
-        @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
+        @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
         public static IRubyObject intialize(IRubyObject self) {
             return self.getRuntime().getNil();
         }
     }
 
     /**
      * Default allocator instance for all Ruby objects. The only
      * reason to not use this allocator is if you actually need to
      * have all instances of something be a subclass of RubyObject.
      *
      * @see org.jruby.runtime.ObjectAllocator
      */
     public static final ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyObject(runtime, klass);
         }
     };
 
     /**
      * Will make sure that this object is added to the current object
      * space.
      *
      * @see org.jruby.runtime.ObjectSpace
      */
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
 
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      *
      * Will generally return a value from org.jruby.runtime.ClassIndex
      *
      * @see org.jruby.runtime.ClassInde
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
 
     /**
      * Simple helper to print any objects.
      */
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     @Override
     public boolean equals(Object other) {
         return other == this ||
                 other instanceof IRubyObject &&
                 callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     /**
      * The default toString method is just a wrapper that calls the
      * Ruby "to_s" method.
      */
     @Override
     public String toString() {
         RubyString rubyString = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "to_s").convertToString();
         return rubyString.getUnicodeValue();
     }
 
     /**
      * The actual method that checks frozen with the default frozen message from MRI.
      * If possible, call this instead of {@link #testFrozen}.
      */
    protected void checkFrozen() {
        testFrozen();
    }
 
     /** init_copy
      *
      * Initializes a copy with variable and special instance variable
      * information, and then call the initialize_copy Ruby method.
      */
     private static void initCopy(IRubyObject clone, RubyObject original) {
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         original.copySpecialInstanceVariables(clone);
 
         if (original.hasVariables()) clone.syncVariables(original.getVariableList());
         if (original instanceof RubyModule) ((RubyModule) clone).syncConstants((RubyModule) original);
 
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /**
      * Tries to convert this object to the specified Ruby type, using
      * a specific conversion method.
      */
     @Deprecated
     public final IRubyObject convertToType(RubyClass target, int convertMethodIndex) {
         throw new RuntimeException("Not supported; use the String versions");
     }
 
     /** specific_eval
      *
      * Evaluates the block or string inside of the context of this
      * object, using the supplied arguments. If a block is given, this
      * will be yielded in the specific context of this object. If no
      * block is given then a String-like object needs to be the first
      * argument, and this string will be evaluated. Second and third
      * arguments in the args-array is optional, but can contain the
      * filename and line of the string under evaluation.
      */
     @Deprecated
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(context, mod, block);
         }
 
         if (args.length == 0) {
             throw getRuntime().newArgumentError("block not supplied");
         } else if (args.length > 3) {
             String lastFuncName = context.getFrameName();
             throw getRuntime().newArgumentError(
                 "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
         }
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (args[0] instanceof RubyString) {
             evalStr = (RubyString)args[0];
         } else {
             evalStr = args[0].convertToString();
         }
 
         String file;
         int line;
         if (args.length > 1) {
             file = args[1].convertToString().asJavaString();
             if (args.length > 2) {
                 line = (int)(args[2].convertToInteger().getLongValue() - 1);
             } else {
                 line = 0;
             }
         } else {
             file = "(eval)";
             line = 0;
         }
 
         return evalUnder(context, mod, evalStr, file, line);
     }
 
     /** specific_eval
      *
      * Evaluates the block or string inside of the context of this
      * object, using the supplied arguments. If a block is given, this
      * will be yielded in the specific context of this object. If no
      * block is given then a String-like object needs to be the first
      * argument, and this string will be evaluated. Second and third
      * arguments in the args-array is optional, but can contain the
      * filename and line of the string under evaluation.
      */
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, Block block) {
         if (block.isGiven()) {
             return yieldUnder(context, mod, block);
         } else {
             throw context.getRuntime().newArgumentError("block not supplied");
         }
     }
 
     /** specific_eval
      *
      * Evaluates the block or string inside of the context of this
      * object, using the supplied arguments. If a block is given, this
      * will be yielded in the specific context of this object. If no
      * block is given then a String-like object needs to be the first
      * argument, and this string will be evaluated. Second and third
      * arguments in the args-array is optional, but can contain the
      * filename and line of the string under evaluation.
      */
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject arg, Block block) {
         if (block.isGiven()) throw context.getRuntime().newArgumentError(1, 0);
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (arg instanceof RubyString) {
             evalStr = (RubyString)arg;
         } else {
             evalStr = arg.convertToString();
         }
 
         String file = "(eval)";
         int line = 0;
 
         return evalUnder(context, mod, evalStr, file, line);
     }
 
     /** specific_eval
      *
      * Evaluates the block or string inside of the context of this
      * object, using the supplied arguments. If a block is given, this
      * will be yielded in the specific context of this object. If no
      * block is given then a String-like object needs to be the first
      * argument, and this string will be evaluated. Second and third
      * arguments in the args-array is optional, but can contain the
      * filename and line of the string under evaluation.
      */
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject arg0, IRubyObject arg1, Block block) {
         if (block.isGiven()) throw context.getRuntime().newArgumentError(2, 0);
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (arg0 instanceof RubyString) {
             evalStr = (RubyString)arg0;
         } else {
             evalStr = arg0.convertToString();
         }
 
         String file = arg1.convertToString().asJavaString();
         int line = 0;
 
         return evalUnder(context, mod, evalStr, file, line);
     }
 
     /** specific_eval
      *
      * Evaluates the block or string inside of the context of this
      * object, using the supplied arguments. If a block is given, this
      * will be yielded in the specific context of this object. If no
      * block is given then a String-like object needs to be the first
      * argument, and this string will be evaluated. Second and third
      * arguments in the args-array is optional, but can contain the
      * filename and line of the string under evaluation.
      */
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         if (block.isGiven()) throw context.getRuntime().newArgumentError(2, 0);
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (arg0 instanceof RubyString) {
             evalStr = (RubyString)arg0;
         } else {
             evalStr = arg0.convertToString();
         }
 
         String file = arg1.convertToString().asJavaString();
         int line = (int)(arg2.convertToInteger().getLongValue() - 1);
 
         return evalUnder(context, mod, evalStr, file, line);
     }
 
     /**
      * Evaluates the string src with self set to the current object,
      * using the module under as the context.
      * @deprecated Call with an int line number and String file
      */
     public IRubyObject evalUnder(final ThreadContext context, RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         return evalUnder(context, under, src.convertToString(), file.convertToString().toString(), (int) (line.convertToInteger().getLongValue() - 1));
     }
 
     /**
      * Evaluates the string src with self set to the current object,
      * using the module under as the context.
      */
     public IRubyObject evalUnder(final ThreadContext context, RubyModule under, RubyString src, String file, int line) {
         Visibility savedVisibility = context.getCurrentVisibility();
         context.setCurrentVisibility(Visibility.PUBLIC);
         context.preExecuteUnder(under, Block.NULL_BLOCK);
         try {
             return ASTInterpreter.evalSimple(context, this, src,
                     file, line);
         } finally {
             context.postExecuteUnder();
             context.setCurrentVisibility(savedVisibility);
         }
     }
 
     /**
      * Will yield to the specific block changing the self to be the
      * current object instead of the self that is part of the frame
      * saved in the block frame. This method is the basis for the Ruby
      * instance_eval and module_eval methods. The arguments sent in to
      * it in the args array will be yielded to the block. This makes
      * it possible to emulate both instance_eval and instance_exec
      * with this implementation.
      */
     private IRubyObject yieldUnder(final ThreadContext context, RubyModule under, IRubyObject[] args, Block block) {
         context.preExecuteUnder(under, block);
 
         Visibility savedVisibility = block.getBinding().getVisibility();
         block.getBinding().setVisibility(Visibility.PUBLIC);
 
         try {
             IRubyObject valueInYield;
             boolean aValue;
 
             // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
             block = block.cloneBlock();
             block.getBinding().setSelf(RubyObject.this);
             block.getBinding().getFrame().setSelf(RubyObject.this);
             // end hack
 
             if (args.length == 1) {
                 valueInYield = args[0];
                 return block.yieldNonArray(context, valueInYield, RubyObject.this, context.getRubyClass());
             } else {
                 valueInYield = RubyArray.newArrayNoCopy(context.getRuntime(), args);
                 return block.yieldArray(context, valueInYield, RubyObject.this, context.getRubyClass());
             }
             //TODO: Should next and return also catch here?
         } catch (JumpException.BreakJump bj) {
             return (IRubyObject) bj.getValue();
         } finally {
             block.getBinding().setVisibility(savedVisibility);
 
             context.postExecuteUnder();
         }
     }
 
     /**
      * Will yield to the specific block changing the self to be the
      * current object instead of the self that is part of the frame
      * saved in the block frame. This method is the basis for the Ruby
      * instance_eval and module_eval methods. The arguments sent in to
      * it in the args array will be yielded to the block. This makes
      * it possible to emulate both instance_eval and instance_exec
      * with this implementation.
      */
     private IRubyObject yieldUnder(final ThreadContext context, RubyModule under, Block block) {
         context.preExecuteUnder(under, block);
 
         Visibility savedVisibility = block.getBinding().getVisibility();
         block.getBinding().setVisibility(Visibility.PUBLIC);
 
         try {
             // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
             block = block.cloneBlock();
             block.getBinding().setSelf(RubyObject.this);
             block.getBinding().getFrame().setSelf(RubyObject.this);
             // end hack
 
             return block.yieldNonArray(context, this, this, context.getRubyClass());
             //TODO: Should next and return also catch here?
         } catch (JumpException.BreakJump bj) {
             return (IRubyObject) bj.getValue();
         } finally {
             block.getBinding().setVisibility(savedVisibility);
 
             context.postExecuteUnder();
         }
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      * Will by default use identity equality to compare objects. This
      * follows the Ruby semantics.
      */
     @JRubyMethod(name = "==", required = 1, compat = CompatVersion.RUBY1_8)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         return super.op_equal(context, obj);
     }
 
     /** rb_obj_equal
      *
      * Will use Java identity equality.
      */
     @JRubyMethod(name = "equal?", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject equal_p(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
     }
 
     /** rb_obj_equal
      *
      * Just like "==" and "equal?", "eql?" will use identity equality for Object.
      */
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_equal
      *
      * The Ruby "===" method is used by default in case/when
      * statements. The Object implementation first checks Java identity
      * equality and then calls the "==" method too.
      */
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return context.getRuntime().newBoolean(equalInternal(context, this, other));
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "==" method.
      */
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that == other || that.callMethod(context, "==", other).isTrue();
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "eql?" method.
      */
     protected static boolean eqlInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that.callMethod(context, "eql?", other).isTrue();
     }
 
     /** rb_obj_init_copy
      *
      * Initializes this object as a copy of the original, that is the
      * parameter to this object. Will make sure that the argument
      * actually has the same real class as this object. It shouldn't
      * be possible to initialize an object with something totally
      * different.
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject initialize_copy(IRubyObject original) {
         if (this == original) {
             return this;
         }
         checkFrozen();
 
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
             throw getRuntime().newTypeError("initialize_copy should take same class object");
         }
 
         return this;
     }
 
     /** obj_respond_to
      *
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      *
      * !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
      *
      * Going back to splitting according to method arity. MRI is wrong
      * about most of these anyway, and since we have arity splitting
      * in both the compiler and the interpreter, the performance
      * benefit is important for this method.
      */
     @JRubyMethod(name = "respond_to?")
     public RubyBoolean respond_to_p(IRubyObject mname) {
         String name = mname.asJavaString();
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, true));
     }
 
     /** obj_respond_to
      *
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      *
      * !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
      *
      * Going back to splitting according to method arity. MRI is wrong
      * about most of these anyway, and since we have arity splitting
      * in both the compiler and the interpreter, the performance
      * benefit is important for this method.
      */
     @JRubyMethod(name = "respond_to?")
     public RubyBoolean respond_to_p(IRubyObject mname, IRubyObject includePrivate) {
         String name = mname.asJavaString();
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate.isTrue()));
     }
 
     /** rb_obj_id
      *
      * Return the internal id of an object.
      *
      * FIXME: Should this be renamed to match its ruby name?
      */
      @JRubyMethod(name = {"object_id", "__id__"})
      @Override
      public IRubyObject id() {
         return super.id();
      }
 
     /** rb_obj_id_obsolete
      *
      * Old id version. This one is bound to the "id" name and will emit a deprecation warning.
      */
     @JRubyMethod(name = "id")
     public IRubyObject id_deprecated() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#id will be deprecated; use Object#object_id", "Object#id", "Object#object_id");
         return id();
     }
 
     /** rb_obj_id
      *
      * Will return the hash code of this object. In comparison to MRI,
      * this method will use the Java identity hash code instead of
      * using rb_obj_id, since the usage of id in JRuby will incur the
      * cost of some. ObjectSpace maintenance.
      */
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(super.hashCode());
     }
 
     /**
      * Override the Object#hashCode method to make sure that the Ruby
      * hash is actually used as the hashcode for Ruby objects. If the
      * Ruby "hash" method doesn't return a number, the Object#hashCode
      * implementation will be used instead.
      */
     @Override
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), "hash");
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue);
         return nonFixnumHashCode(hashValue);
     }
 
     private int nonFixnumHashCode(IRubyObject hashValue) {
         Ruby runtime = getRuntime();
         if (runtime.is1_9()) {
             return (int)hashValue.convertToInteger().getLongValue();
         } else {
             hashValue = hashValue.callMethod(runtime.getCurrentContext(), "%", RubyFixnum.newFixnum(runtime, 536870923L));
             if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue);
             return System.identityHashCode(hashValue);
         }
     }
 
     /** rb_obj_class
      *
      * Returns the real class of this object, excluding any
      * singleton/meta class in the inheritance chain.
      */
     @JRubyMethod(name = "class")
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     /** rb_obj_type
      *
      * The deprecated version of type, that emits a deprecation
      * warning.
      */
     @JRubyMethod(name = "type")
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#type is deprecated; use Object#class", "Object#type", "Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *
      * This method should be overridden only by: Proc, Method,
      * UnboundedMethod, Binding. It will use the defined allocated of
      * the object, then clone the singleton class, taint the object,
      * call initCopy and then copy frozen state.
      */
     @JRubyMethod(name = "clone", frame = true)
     @Override
     public IRubyObject rbClone() {
         return super.rbClone();
     }
 
     /** rb_obj_dup
      *
      * This method should be overridden only by: Proc
      *
      * Will allocate a new instance of the real class of this object,
      * and then initialize that copy. It's different from {@link
      * #rbClone} in that it doesn't copy the singleton class.
      */
     @JRubyMethod(name = "dup")
     @Override
     public IRubyObject dup() {
         return super.dup();
     }
 
     /** rb_obj_display
      *
      *  call-seq:
      *     obj.display(port=$>)    => nil
      *
      *  Prints <i>obj</i> on the given port (default <code>$></code>).
      *  Equivalent to:
      *
      *     def display(port=$>)
      *       port.write self
      *     end
      *
      *  For example:
      *
      *     1.display
      *     "cat".display
      *     [ 4, 5, 6 ].display
      *     puts
      *
      *  <em>produces:</em>
      *
      *     1cat456
      *
      */
     @JRubyMethod(name = "display", optional = 1)
     public IRubyObject display(ThreadContext context, IRubyObject[] args) {
         IRubyObject port = args.length == 0 ? context.getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(context, "write", this);
 
         return context.getRuntime().getNil();
     }
 
     /** rb_obj_tainted
      *
      *  call-seq:
      *     obj.tainted?    => true or false
      *
      *  Returns <code>true</code> if the object is tainted.
      *
      */
     @JRubyMethod(name = "tainted?")
     public RubyBoolean tainted_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      *  call-seq:
      *     obj.taint -> obj
      *
      *  Marks <i>obj</i> as tainted---if the <code>$SAFE</code> level is
      *  set appropriately, many method calls which might alter the running
      *  programs environment will refuse to accept tainted strings.
      */
     @JRubyMethod(name = "taint")
     public IRubyObject taint(ThreadContext context) {
         taint(context.getRuntime());
         return this;
     }
 
     /** rb_obj_untaint
      *
      *  call-seq:
      *     obj.untaint    => obj
      *
      *  Removes the taint from <i>obj</i>.
      *
      *  Only callable in if more secure than 3.
      */
     @JRubyMethod(name = "untaint")
     public IRubyObject untaint(ThreadContext context) {
         context.getRuntime().secure(3);
 
         if (isTaint()) {
             testFrozen();
             setTaint(false);
         }
 
         return this;
     }
 
     /** rb_obj_freeze
      *
      *  call-seq:
      *     obj.freeze    => obj
      *
      *  Prevents further modifications to <i>obj</i>. A
      *  <code>TypeError</code> will be raised if modification is attempted.
      *  There is no way to unfreeze a frozen object. See also
      *  <code>Object#frozen?</code>.
      *
      *     a = [ "a", "b", "c" ]
      *     a.freeze
      *     a << "z"
      *
      *  <em>produces:</em>
      *
      *     prog.rb:3:in `<<': can't modify frozen array (TypeError)
      *     	from prog.rb:3
      */
     @JRubyMethod(name = "freeze")
     public IRubyObject freeze(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if ((flags & FROZEN_F) == 0 && (runtime.is1_9() || !isImmediate())) {
             if (runtime.getSafeLevel() >= 4 && !isTaint()) throw runtime.newSecurityError("Insecure: can't freeze object");
             flags |= FROZEN_F;
         }
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      *  call-seq:
      *     obj.frozen?    => true or false
      *
      *  Returns the freeze status of <i>obj</i>.
      *
      *     a = [ "a", "b", "c" ]
      *     a.freeze    #=> ["a", "b", "c"]
      *     a.frozen?   #=> true
      */
     @JRubyMethod(name = "frozen?")
     public RubyBoolean frozen_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_untrusted
      *  call-seq:
      *     obj.untrusted?    => true or false
      *
      *  Returns <code>true</code> if the object is untrusted.
      */
     @JRubyMethod(name = "untrusted?", compat = CompatVersion.RUBY1_9)
     public RubyBoolean untrusted_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isUntrusted());
     }
 
     /** rb_obj_untrust
      *  call-seq:
      *     obj.untrust -> obj
      *
      *  Marks <i>obj</i> as untrusted.
      */
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject untrust(ThreadContext context) {
         if (!isUntrusted() && !isImmediate()) {
             checkFrozen();
             flags |= UNTRUSTED_F;
         }
         return this;
     }
 
     /** rb_obj_trust
      *  call-seq:
      *     obj.trust    => obj
      *
      *  Removes the untrusted mark from <i>obj</i>.
      */
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject trust(ThreadContext context) {
         if (isUntrusted() && !isImmediate()) {
             checkFrozen();
             flags &= ~UNTRUSTED_F;
         }
         return this;
     }
 
     /** rb_inspect
      *
      * The internal helper that ensures a RubyString instance is returned
      * so dangerous casting can be omitted
      * Prefered over callMethod(context, "inspect")
      */
     static RubyString inspect(ThreadContext context, IRubyObject object) {
         return RubyString.objAsString(context, object.callMethod(context, "inspect"));
     }
 
     /** rb_obj_inspect
      *
      *  call-seq:
      *     obj.inspect   => string
      *
      *  Returns a string containing a human-readable representation of
      *  <i>obj</i>. If not overridden, uses the <code>to_s</code> method to
      *  generate the string.
      *
      *     [ 1, 2, 3..4, 'five' ].inspect   #=> "[1, 2, 3..4, \"five\"]"
      *     Time.new.inspect                 #=> "Wed Apr 09 08:54:39 CDT 2003"
      */
     @JRubyMethod(name = "inspect")
     @Override
     public IRubyObject inspect() {
         return super.inspect();
     }
 
     /** rb_obj_is_instance_of
      *
      *  call-seq:
      *     obj.instance_of?(class)    => true or false
      *
      *  Returns <code>true</code> if <i>obj</i> is an instance of the given
      *  class. See also <code>Object#kind_of?</code>.
      */
     @JRubyMethod(name = "instance_of?", required = 1)
     public RubyBoolean instance_of_p(ThreadContext context, IRubyObject type) {
         if (type() == type) {
             return context.getRuntime().getTrue();
         } else if (!(type instanceof RubyModule)) {
             throw context.getRuntime().newTypeError("class or module required");
         } else {
             return context.getRuntime().getFalse();
         }
     }
 
 
     /** rb_obj_is_kind_of
      *
      *  call-seq:
      *     obj.is_a?(class)       => true or false
      *     obj.kind_of?(class)    => true or false
      *
      *  Returns <code>true</code> if <i>class</i> is the class of
      *  <i>obj</i>, or if <i>class</i> is one of the superclasses of
      *  <i>obj</i> or modules included in <i>obj</i>.
      *
      *     module M;    end
      *     class A
      *       include M
      *     end
      *     class B < A; end
      *     class C < B; end
      *     b = B.new
      *     b.instance_of? A   #=> false
      *     b.instance_of? B   #=> true
      *     b.instance_of? C   #=> false
      *     b.instance_of? M   #=> false
      *     b.kind_of? A       #=> true
      *     b.kind_of? B       #=> true
      *     b.kind_of? C       #=> false
      *     b.kind_of? M       #=> true
      */
     @JRubyMethod(name = {"kind_of?", "is_a?"}, required = 1)
     public RubyBoolean kind_of_p(ThreadContext context, IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!(type instanceof RubyModule)) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw context.getRuntime().newTypeError("class or module required");
         }
 
         return context.getRuntime().newBoolean(((RubyModule)type).isInstance(this));
     }
 
     /** rb_obj_methods
      *
      *  call-seq:
      *     obj.methods    => array
      *
      *  Returns a list of the names of methods publicly accessible in
      *  <i>obj</i>. This will include all the methods accessible in
      *  <i>obj</i>'s ancestors.
      *
      *     class Klass
      *       def kMethod()
      *       end
      *     end
      *     k = Klass.new
      *     k.methods[0..9]    #=> ["kMethod", "freeze", "nil?", "is_a?",
      *                             "class", "instance_variable_set",
      *                              "methods", "extend", "__send__", "instance_eval"]
      *     k.methods.length   #=> 42
      */
     @JRubyMethod(name = "methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject methods(ThreadContext context, IRubyObject[] args) {
         return methods(context, args, false);
     }
     @JRubyMethod(name = "methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject methods19(ThreadContext context, IRubyObject[] args) {
         return methods(context, args, true);
     }
 
     public IRubyObject methods(ThreadContext context, IRubyObject[] args, boolean useSymbols) {
         boolean all = args.length == 1 ? args[0].isTrue() : true;
         Ruby runtime = getRuntime();
         RubyArray methods = runtime.newArray();
         Set<String> seen = new HashSet<String>();
 
         if (getMetaClass().isSingleton()) {
             getMetaClass().populateInstanceMethodNames(seen, methods, Visibility.PRIVATE, true, useSymbols, false);
             if (all) {
                 getMetaClass().getSuperClass().populateInstanceMethodNames(seen, methods, Visibility.PRIVATE, true, useSymbols, true);
             }
         } else if (all) {
             getMetaClass().populateInstanceMethodNames(seen, methods, Visibility.PRIVATE, true, useSymbols, true);
         } else {
             // do nothing, leave empty
         }
 
         return methods;
     }
 
     /** rb_obj_public_methods
      *
      *  call-seq:
      *     obj.public_methods(all=true)   => array
      *
      *  Returns the list of public methods accessible to <i>obj</i>. If
      *  the <i>all</i> parameter is set to <code>false</code>, only those methods
      *  in the receiver will be listed.
      */
     @JRubyMethod(name = "public_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject public_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().public_instance_methods(trueIfNoArgument(context, args));
     }
 
     @JRubyMethod(name = "public_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject public_methods19(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().public_instance_methods19(trueIfNoArgument(context, args));
     }
 
     /** rb_obj_protected_methods
      *
      *  call-seq:
      *     obj.protected_methods(all=true)   => array
      *
      *  Returns the list of protected methods accessible to <i>obj</i>. If
      *  the <i>all</i> parameter is set to <code>false</code>, only those methods
      *  in the receiver will be listed.
      *
      *  Internally this implementation uses the
      *  {@link RubyModule#protected_instance_methods} method.
      */
     @JRubyMethod(name = "protected_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject protected_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().protected_instance_methods(trueIfNoArgument(context, args));
     }
 
     @JRubyMethod(name = "protected_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject protected_methods19(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().protected_instance_methods19(trueIfNoArgument(context, args));
     }
 
     /** rb_obj_private_methods
      *
      *  call-seq:
      *     obj.private_methods(all=true)   => array
      *
      *  Returns the list of private methods accessible to <i>obj</i>. If
      *  the <i>all</i> parameter is set to <code>false</code>, only those methods
      *  in the receiver will be listed.
      *
      *  Internally this implementation uses the
      *  {@link RubyModule#private_instance_methods} method.
      */
     @JRubyMethod(name = "private_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject private_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().private_instance_methods(trueIfNoArgument(context, args));
     }
 
     @JRubyMethod(name = "private_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject private_methods19(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().private_instance_methods19(trueIfNoArgument(context, args));
     }
 
     // FIXME: If true array is common enough we should pre-allocate and stick somewhere
     private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? new IRubyObject[] { context.getRuntime().getTrue() } : args;
     }
 
     /** rb_obj_singleton_methods
      *
      *  call-seq:
      *     obj.singleton_methods(all=true)    => array
      *
      *  Returns an array of the names of singleton methods for <i>obj</i>.
      *  If the optional <i>all</i> parameter is true, the list will include
      *  methods in modules included in <i>obj</i>.
      *
      *     module Other
      *       def three() end
      *     end
      *
      *     class Single
      *       def Single.four() end
      *     end
      *
      *     a = Single.new
      *
      *     def a.one()
      *     end
      *
      *     class << a
      *       include Other
      *       def two()
      *       end
      *     end
      *
      *     Single.singleton_methods    #=> ["four"]
      *     a.singleton_methods(false)  #=> ["two", "one"]
      *     a.singleton_methods         #=> ["two", "one", "three"]
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     @JRubyMethod(name = "singleton_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public RubyArray singleton_methods(ThreadContext context, IRubyObject[] args) {
         return singletonMethods(context, args, false);
