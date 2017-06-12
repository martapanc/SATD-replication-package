diff --git a/spec/tags/1.8/ruby/core/module/remove_class_variable_tags.txt b/spec/tags/1.8/ruby/core/module/remove_class_variable_tags.txt
deleted file mode 100644
index 97fef7775d..0000000000
--- a/spec/tags/1.8/ruby/core/module/remove_class_variable_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails(JRUBY-4812):Module#remove_class_variable removes class variable
-fails(JRUBY-4812):Module#remove_class_variable returns the value of removing class variable
diff --git a/spec/tags/1.9/ruby/core/module/remove_class_variable_tags.txt b/spec/tags/1.9/ruby/core/module/remove_class_variable_tags.txt
deleted file mode 100644
index 056b8b3f06..0000000000
--- a/spec/tags/1.9/ruby/core/module/remove_class_variable_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails:Module#remove_class_variable removes class variable
-fails:Module#remove_class_variable returns the value of removing class variable
diff --git a/src/org/jruby/RubyBasicObject.java b/src/org/jruby/RubyBasicObject.java
index ded836c8ec..432ea374fa 100644
--- a/src/org/jruby/RubyBasicObject.java
+++ b/src/org/jruby/RubyBasicObject.java
@@ -1,1835 +1,1839 @@
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
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
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
     private static final Object[] NULL_OBJECT_ARRAY = new Object[0];
     
     // The class of this object
     protected transient RubyClass metaClass;
 
     // zeroed by jvm
     protected int flags;
 
     // variable table, lazily allocated as needed (if needed)
     private volatile Object[] varTable = NULL_OBJECT_ARRAY;
 
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
     public static final int USER8_F = (1<<(FL_USHIFT+8));
 
     public static final int COMPARE_BY_IDENTITY_F = USER8_F;
 
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
     public static final ObjectAllocator BASICOBJECT_ALLOCATOR = new ObjectAllocator() {
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
 
         objectClass.defineAnnotatedMethods(BasicObjectMethods.class);
         objectClass.defineAnnotatedMethods(RubyBasicObject.class);
 
         runtime.setDefaultMethodMissing(objectClass.searchMethod("method_missing"));
 
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
         @JRubyMethod(name = "initialize", visibility = PRIVATE)
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
      * Will invoke a named method with no arguments and no block if that method or a custom
      * method missing exists. Otherwise returns null. 1.9: rb_check_funcall
      */
     public final IRubyObject checkCallMethod(ThreadContext context, String name) {
         return RuntimeHelpers.invokeChecked(context, this, name);
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
         DynamicMethod method = getMetaClass().searchMethod("respond_to?");
         if(method == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return method.call(getRuntime().getCurrentContext(), this, metaClass, "respond_to?", getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     /**
      * Does this object respond to the specified message via "method_missing?"
      */
     public final boolean respondsToMissing(String name) {
         return respondsToMissing(name, true);
     }
 
     /**
      * Does this object respond to the specified message via "method_missing?"
      */
     public final boolean respondsToMissing(String name, boolean priv) {
         DynamicMethod method = getMetaClass().searchMethod("respond_to_missing?");
         // perhaps should try a smart version as for respondsTo above?
         if(method.isUndefined()) {
             return false;
         } else {
             return method.call(
                     getRuntime().getCurrentContext(),
                     this,
                     metaClass,
                     "respond_to_missing?",
                     getRuntime().newSymbol(name),
                     getRuntime().newBoolean(priv)).isTrue();
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
 
     /** rb_check_string_type
      *
      * Tries to return a coerced string representation of this object,
      * using "to_str". If that returns something other than a String
      * or nil, an empty String will be returned.
      *
      */
     public IRubyObject checkStringType19() {
         IRubyObject str = TypeConverter.convertToTypeWithCheck19(this, getRuntime().getString(), "to_str");
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
         // for callers that unconditionally pass null retval type (JRUBY-4737)
         if (target == void.class) return null;
 
         if (dataGetStruct() instanceof JavaObject) {
             // for interface impls
 
             JavaObject innerWrapper = (JavaObject)dataGetStruct();
 
             // ensure the object is associated with the wrapper we found it in,
             // so that if it comes back we don't re-wrap it
             if (target.isAssignableFrom(innerWrapper.getValue().getClass())) {
                 getRuntime().getJavaSupport().getObjectProxyCache().put(innerWrapper.getValue(), this);
 
                 return innerWrapper.getValue();
             }
         } else if (JavaUtil.isDuckTypeConvertable(getClass(), target)) {
             if (!respondsTo("java_object")) {
                 return JavaUtil.convertProcToInterface(getRuntime().getCurrentContext(), this, target);
             }
         } else if (target.isAssignableFrom(getClass())) {
             return this;
         }
         
         throw getRuntime().newTypeError("cannot convert instance of " + getClass() + " to " + target);
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
-        if (original instanceof RubyModule) ((RubyModule) clone).syncConstants((RubyModule) original);
+        if (original instanceof RubyModule) {
+            RubyModule cloneMod = (RubyModule)clone;
+            cloneMod.syncConstants((RubyModule)original);
+            cloneMod.syncClassVariables((RubyModule)original);
+        }
 
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
         return getRuntime().newFixnum(getObjectId());
     }
 
     /**
      * The logic here is to use the special objectId accessor slot from the
      * parent as a lazy store for an object ID. IDs are generated atomically,
      * in serial, and guaranteed unique for up to 2^63 objects. The special
      * objectId slot is managed separately from the "normal" vars so it
      * does not marshal, clone/dup, or refuse to be initially set when the
      */
     protected synchronized long getObjectId() {
         // object is frozen.
         synchronized (this) {
             RubyClass.VariableAccessor objectIdAccessor = getMetaClass().getRealClass().getObjectIdAccessorForWrite();
             Long id = (Long)objectIdAccessor.get(this);
             if (id == null) {
                 return initObjectId(objectIdAccessor);
             }
             return id;
         }
     }
 
     /**
      * We lazily stand up the object ID since it forces us to stand up
      * per-object state for a given object. We also check for ObjectSpace here,
      * and normally we do not register a given object ID into ObjectSpace due
      * to the high cost associated with constructing the related weakref. Most
      * uses of id/object_id will only ever need it to be a unique identifier,
      * and the id2ref behavior provided by ObjectSpace is considered internal
      * and not generally supported.
      * 
      * @param objectIdAccessor The variable accessor to use for storing the
      * generated object ID
      * @return The generated object ID
      */
     protected synchronized long initObjectId(RubyClass.VariableAccessor objectIdAccessor) {
         Ruby runtime = getRuntime();
         long id;
         
         if (runtime.isObjectSpaceEnabled()) {
             id = runtime.getObjectSpace().createAndRegisterObjectId(this);
         } else {
             id = ObjectSpace.calculateObjectId(this);
         }
         
         // we use a direct path here to avoid frozen checks
         setObjectId(objectIdAccessor.getIndex(), id);
 
         return id;
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
             return hashyInspect();
         }
 
         if (isNil()) return RubyNil.inspect(this);
         return RuntimeHelpers.invoke(runtime.getCurrentContext(), this, "to_s");
     }
 
     public IRubyObject hashyInspect() {
         Ruby runtime = getRuntime();
         StringBuilder part = new StringBuilder();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(inspectHashCode()));
 
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
 
     /**
      * For most objects, the hash used in the default #inspect is just the
      * identity hashcode of the actual object.
      *
      * See org.jruby.java.proxies.JavaProxy for a divergent case.
      *
      * @return The identity hashcode of this object
      */
     protected int inspectHashCode() {
         return System.identityHashCode(this);
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
 
 
     @JRubyMethod(name = "!", compat = RUBY1_9)
     public IRubyObject op_not(ThreadContext context) {
         return context.getRuntime().newBoolean(!this.isTrue());
     }
 
     @JRubyMethod(name = "!=", required = 1, compat = RUBY1_9)
     public IRubyObject op_not_equal(ThreadContext context, IRubyObject other) {
         return context.getRuntime().newBoolean(!callMethod("==", other).isTrue());
     }
 
     public int compareTo(IRubyObject other) {
         return (int)callMethod(getRuntime().getCurrentContext(), "<=>", other).convertToInteger().getLongValue();
     }
 
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         // Remain unimplemented due to problems with the double java hierarchy
         return context.getRuntime().getNil();
     }
 
     /** rb_obj_equal
      *
      * Will by default use identity equality to compare objects. This
      * follows the Ruby semantics.
      *
      * The name of this method doesn't follow the convention because hierarchy problems
      */
     @JRubyMethod(name = "==", required = 1, compat = RUBY1_9)
     public IRubyObject op_equal_19(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
     }
 
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         // Remain unimplemented due to problems with the double java hierarchy
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "equal?", required = 1, compat = RUBY1_9)
     public IRubyObject equal_p19(ThreadContext context, IRubyObject other) {
         return op_equal_19(context, other);
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
             // since this is the first time we're registering a finalizer, we
             // must also register this object in ObjectSpace, so that future
             // calls to undefine_finalizer, which takes an object ID, can
             // locate the object properly. See JRUBY-4839.
             long id = getObjectId();
             RubyFixnum fixnumId = (RubyFixnum)id();
 
             getRuntime().getObjectSpace().registerObjectId(id, this);
 
             finalizer = new Finalizer(fixnumId);
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
 
     private Object[] getVariableTableForRead() {
         return varTable;
     }
 
     private synchronized Object[] getVariableTableForWrite(int index) {
         if (varTable == NULL_OBJECT_ARRAY) {
             if (DEBUG) System.out.println("resizing from " + varTable.length + " to " + getMetaClass().getRealClass().getVariableTableSizeWithObjectId());
             varTable = new Object[getMetaClass().getRealClass().getVariableTableSizeWithObjectId()];
         } else if (varTable.length <= index) {
             if (DEBUG) System.out.println("resizing from " + varTable.length + " to " + getMetaClass().getRealClass().getVariableTableSizeWithObjectId());
             Object[] newTable = new Object[getMetaClass().getRealClass().getVariableTableSizeWithObjectId()];
             System.arraycopy(varTable, 0, newTable, 0, varTable.length);
             varTable = newTable;
         }
         return varTable;
     }
 
     public Object getVariable(int index) {
         if (index < 0) return null;
         Object[] ivarTable = getVariableTableForRead();
         if (ivarTable.length > index) return ivarTable[index];
         return null;
     }
 
     public void setVariable(int index, Object value) {
         ensureInstanceVariablesSettable();
         if (index < 0) return;
         Object[] ivarTable = getVariableTableForWrite(index);
         ivarTable[index] = value;
     }
 
     private void setObjectId(int index, long value) {
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
         // we check both to exclude object_id
         return getMetaClass().getRealClass().getVariableTableSize() > 0 && varTable.length > 0;
     }
 
     /**
      * Returns the amount of instance variables, class variables,
      * constants and internal variables this object has.
      */
     @Deprecated
     public int getVariableCount() {
         // we use min to exclude object_id
         return Math.min(varTable.length, getMetaClass().getRealClass().getVariableTableSize());
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
 
     /**
      * Fetch an object from the variable table based on the name,
      * where the name must be an interned Java String.
      *
      * @return the object or null if not found
      */
     protected Object variableTableFastFetch(String internedName) {
         return variableTableFetch(internedName);
     }
 
     /**
      * Store a value in the variable store under the specific name.
      */
     protected Object variableTableStore(String name, Object value) {
         getMetaClass().getRealClass().getVariableAccessorForWrite(name).set(this, value);
         return value;
     }
 
     /**
      * Will store the value under the specified name, where the name
      * needs to be an interned Java String.
      */
     protected Object variableTableFastStore(String internedName, Object value) {
         return variableTableStore(internedName, value);
     }
 
     /**
      * Removes the entry with the specified name from the variable
      * table, and returning the removed value.
      */
     protected Object variableTableRemove(String name) {
         synchronized(this) {
             Object value = getMetaClass().getRealClass().getVariableAccessorForRead(name).get(this);
             getMetaClass().getRealClass().getVariableAccessorForWrite(name).set(this, null);
             return value;
         }
     }
 
     /**
      * Synchronize the variable table with the argument. In real terms
      * this means copy all entries into a newly allocated table.
      */
     protected void variableTableSync(List<Variable<Object>> vars) {
         synchronized(this) {
             for (Variable<Object> var : vars) {
                 variableTableStore(var.getName(), var.getValue());
             }
         }
     }
 
     //
     // INTERNAL VARIABLE METHODS
     //
 
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */
     public InternalVariables getInternalVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#hasInternalVariable
      */
     public boolean hasInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastHasInternalVariable
      */
     public boolean fastHasInternalVariable(String internedName) {
         assert !IdUtil.isRubyVariable(internedName);
         return variableTableFastContains(internedName);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#getInternalVariable
      */
     public Object getInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableFetch(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastGetInternalVariable
      */
     public Object fastGetInternalVariable(String internedName) {
         assert !IdUtil.isRubyVariable(internedName);
         return variableTableFastFetch(internedName);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#setInternalVariable
      */
     public void setInternalVariable(String name, Object value) {
         assert !IdUtil.isRubyVariable(name);
         variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastSetInternalVariable
      */
     public void fastSetInternalVariable(String internedName, Object value) {
         assert !IdUtil.isRubyVariable(internedName);
         variableTableFastStore(internedName, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#removeInternalVariable
      */
     public Object removeInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableRemove(name);
     }
 
     /**
      * Sync one variable table with another - this is used to make
      * rbClone work correctly.
      */
     public void syncVariables(List<Variable<Object>> variables) {
         variableTableSync(variables);
     }
 
     //
     // INSTANCE VARIABLE API METHODS
     //
 
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */
     public InstanceVariables getInstanceVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#hasInstanceVariable
      */
     public boolean hasInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastHasInstanceVariable
      */
     public boolean fastHasInstanceVariable(String internedName) {
         assert IdUtil.isInstanceVariable(internedName);
         return variableTableFastContains(internedName);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariable
      */
     public IRubyObject getInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return (IRubyObject)variableTableFetch(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastGetInstanceVariable
      */
     public IRubyObject fastGetInstanceVariable(String internedName) {
         assert IdUtil.isInstanceVariable(internedName);
         return (IRubyObject)variableTableFastFetch(internedName);
     }
 
     /** rb_iv_set / rb_ivar_set
     *
     * @see org.jruby.runtime.builtin.InstanceVariables#setInstanceVariable
     */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         assert IdUtil.isInstanceVariable(name) && value != null;
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastSetInstanceVariable
      */
     public IRubyObject fastSetInstanceVariable(String internedName, IRubyObject value) {
         assert IdUtil.isInstanceVariable(internedName) && value != null;
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableFastStore(internedName, value);
      }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#removeInstanceVariable
      */
     public IRubyObject removeInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableRemove(name);
     }
 
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     public List<Variable<IRubyObject>> getInstanceVariableList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getVariableAccessorsForRead();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             list.add(new VariableEntry<IRubyObject>(entry.getKey(), (IRubyObject)value));
         }
         return list;
     }
 
     /**
      * Gets a name list of all variables in this object.
      */
    // TODO: must override in RubyModule to pick up constants
    public List<String> getInstanceVariableNameList() {
         Map<String, RubyClass.VariableAccessor> ivarAccessors = getMetaClass().getRealClass().getVariableAccessorsForRead();
         ArrayList<String> list = new ArrayList<String>();
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             list.add(entry.getKey());
         }
         return list;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariableNameList
      */
     public void copyInstanceVariablesInto(final InstanceVariables other) {
         for (Variable<IRubyObject> var : getInstanceVariableList()) {
             synchronized (this) {
                 other.setInstanceVariable(var.getName(), var.getValue());
             }
         }
     }
 
     /**
      * Makes sure that instance variables can be set on this object,
      * including information about whether this object is frozen, or
      * tainted. Will throw a suitable exception in that case.
      */
     protected final void ensureInstanceVariablesSettable() {
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
 
     public int getNativeTypeIndex() {
         throw new UnsupportedOperationException("Not supported yet.");
     }
 
     /**
      * A method to determine whether the method named by methodName is a builtin
      * method.  This means a method with a JRubyMethod annotation written in
      * Java.
      *
      * @param methodName to look for.
      * @return true if so
      */
     public boolean isBuiltin(String methodName) {
         DynamicMethod method = getMetaClass().searchMethodInner(methodName);
 
         return method != null && method.isBuiltin();
     }
 
     @JRubyMethod(name = "singleton_method_added", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject singleton_method_added19(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_removed", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject singleton_method_removed19(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_undefined", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject singleton_method_undefined19(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "method_missing", rest = true, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject method_missing19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw context.getRuntime().newArgumentError("no id given");
 
         return RubyKernel.methodMissingDirect(context, recv, (RubySymbol)args[0], lastVis, lastCallType, args, block);
     }
 
     @JRubyMethod(name = "__send__", compat = RUBY1_9)
     public IRubyObject send19(ThreadContext context, Block block) {
         throw context.getRuntime().newArgumentError(0, 1);
     }
     @JRubyMethod(name = "__send__", compat = RUBY1_9)
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, block);
     }
     @JRubyMethod(name = "__send__", compat = RUBY1_9)
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, arg1, block);
     }
     @JRubyMethod(name = "__send__", compat = RUBY1_9)
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, arg1, arg2, block);
     }
     @JRubyMethod(name = "__send__", rest = true, compat = RUBY1_9)
     public IRubyObject send19(ThreadContext context, IRubyObject[] args, Block block) {
         String name = args[0].asJavaString();
         int newArgsLength = args.length - 1;
 
         IRubyObject[] newArgs;
         if (newArgsLength == 0) {
             newArgs = IRubyObject.NULL_ARRAY;
         } else {
             newArgs = new IRubyObject[newArgsLength];
             System.arraycopy(args, 1, newArgs, 0, newArgs.length);
         }
 
         return getMetaClass().finvoke(context, this, name, newArgs, block);
     }
     
     @JRubyMethod(name = "instance_eval", compat = RUBY1_9)
     public IRubyObject instance_eval19(ThreadContext context, Block block) {
         return specificEval(context, getInstanceEvalClass(), block);
     }
     @JRubyMethod(name = "instance_eval", compat = RUBY1_9)
     public IRubyObject instance_eval19(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, block);
     }
     @JRubyMethod(name = "instance_eval", compat = RUBY1_9)
     public IRubyObject instance_eval19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, block);
     }
     @JRubyMethod(name = "instance_eval", compat = RUBY1_9)
     public IRubyObject instance_eval19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, arg2, block);
     }
 
     @JRubyMethod(name = "instance_exec", optional = 3, rest = true, compat = RUBY1_9)
     public IRubyObject instance_exec19(ThreadContext context, IRubyObject[] args, Block block) {
         if (!block.isGiven()) throw context.getRuntime().newLocalJumpErrorNoBlock();
 
         RubyModule klazz;
         if (isImmediate()) {
             // Ruby uses Qnil here, we use "dummy" because we need a class
             klazz = context.getRuntime().getDummy();
         } else {
             klazz = getSingletonClass();
         }
 
         return yieldUnder(context, klazz, args, block);
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
     protected IRubyObject yieldUnder(final ThreadContext context, RubyModule under, IRubyObject[] args, Block block) {
         context.preExecuteUnder(under, block);
 
         Visibility savedVisibility = block.getBinding().getVisibility();
         block.getBinding().setVisibility(PUBLIC);
 
         try {
             if (args.length == 1) {
                 IRubyObject valueInYield = args[0];
                 return setupBlock(block).yieldNonArray(context, valueInYield, this, context.getRubyClass());
             } else {
                 IRubyObject valueInYield = RubyArray.newArrayNoCopy(context.getRuntime(), args);
                 return block.yieldArray(context, valueInYield, this, context.getRubyClass());
             }
             //TODO: Should next and return also catch here?
         } catch (JumpException.BreakJump bj) {
             return (IRubyObject) bj.getValue();
         } finally {
             block.getBinding().setVisibility(savedVisibility);
 
             context.postExecuteUnder();
         }
     }
 
     private Block setupBlock(Block block) {
         // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
         block = block.cloneBlock();
         block.getBinding().setSelf(this);
         block.getBinding().getFrame().setSelf(this);
 
         return block;
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
     protected IRubyObject yieldUnder(final ThreadContext context, RubyModule under, Block block) {
         context.preExecuteUnder(under, block);
 
         Visibility savedVisibility = block.getBinding().getVisibility();
         block.getBinding().setVisibility(PUBLIC);
 
         try {
             return setupBlock(block).yieldNonArray(context, this, this, context.getRubyClass());
             //TODO: Should next and return also catch here?
         } catch (JumpException.BreakJump bj) {
             return (IRubyObject) bj.getValue();
         } finally {
             block.getBinding().setVisibility(savedVisibility);
 
             context.postExecuteUnder();
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
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 946d83e0ea..bda71d657d 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -576,2000 +576,2006 @@ public class RubyModule extends RubyObject {
                         methodsHash = annotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = annotatedMethods1_9;
                     } else {
                         methodsHash = annotatedMethods;
                     }
                 }
 
                 // add to specific
                 methodDescs = methodsHash.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     methodsHash.put(name, methodDescs);
                 }
                 
                 methodDescs.add(desc);
 
                 // add to general
                 methodDescs = allAnnotatedMethods.get(name);
                 if (methodDescs == null) {
                     methodDescs = new ArrayList<JavaMethodDescriptor>();
                     allAnnotatedMethods.put(name, methodDescs);
                 }
 
                 methodDescs.add(desc);
             }
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAllAnnotatedMethods() {
             return allAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods() {
             return annotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_8() {
             return annotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getAnnotatedMethods1_9() {
             return annotatedMethods1_9;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods() {
             return staticAnnotatedMethods;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_8() {
             return staticAnnotatedMethods1_8;
         }
 
         public Map<String, List<JavaMethodDescriptor>> getStaticAnnotatedMethods1_9() {
             return staticAnnotatedMethods1_9;
         }
     }
     
     public void defineAnnotatedMethodsIndividually(Class clazz) {
         TypePopulator populator;
         
         if (RubyInstanceConfig.FULL_TRACE_ENABLED || RubyInstanceConfig.REFLECTED_HANDLES) {
             // we want reflected invokers or need full traces, use default (slow) populator
             if (DEBUG) System.out.println("trace mode, using default populator");
             populator = TypePopulator.DEFAULT;
         } else {
             try {
                 String qualifiedName = "org.jruby.gen." + clazz.getCanonicalName().replace('.', '$');
 
                 if (DEBUG) System.out.println("looking for " + qualifiedName + "$Populator");
 
                 Class populatorClass = Class.forName(qualifiedName + "$Populator");
                 populator = (TypePopulator)populatorClass.newInstance();
             } catch (Throwable t) {
                 if (DEBUG) System.out.println("Could not find it, using default populator");
                 populator = TypePopulator.DEFAULT;
             }
         }
         
         populator.populate(this, clazz);
     }
     
     public boolean defineAnnotatedMethod(String name, List<JavaMethodDescriptor> methods, MethodFactory methodFactory) {
         JavaMethodDescriptor desc = methods.get(0);
         if (methods.size() == 1) {
             return defineAnnotatedMethod(desc, methodFactory);
         } else {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, methods);
             define(this, desc, dynamicMethod);
             
             return true;
         }
     }
     
     public boolean defineAnnotatedMethod(Method method, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = method.getAnnotation(JRubyMethod.class);
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
     
     public boolean defineAnnotatedMethod(JavaMethodDescriptor desc, MethodFactory methodFactory) { 
         JRubyMethod jrubyMethod = desc.anno;
 
         if (jrubyMethod == null) return false;
 
             if(jrubyMethod.compat() == BOTH ||
                     getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             DynamicMethod dynamicMethod = methodFactory.getAnnotatedMethod(this, desc);
             define(this, desc, dynamicMethod);
 
             return true;
         }
         return false;
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 PRIVATE : PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method, Visibility visibility) {
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, PRIVATE));
     }
 
     public void defineFastProtectedMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, PROTECTED));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(ThreadContext context, String name) {
         Ruby runtime = context.getRuntime();
         
         if (this == runtime.getObject()) runtime.secure(4);
 
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.UNDEFINING_BAD, "undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = ((MetaClass)c).getAttached();
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw runtime.newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_undefined", runtime.newSymbol(name));
         } else {
             callMethod(context, "method_undefined", runtime.newSymbol(name));
         }
     }
 
     @JRubyMethod(name = "include?", required = 1)
     public IRubyObject include_p(ThreadContext context, IRubyObject arg) {
         if (!arg.isModule()) throw context.getRuntime().newTypeError(arg, context.getRuntime().getModule());
         RubyModule moduleToCompare = (RubyModule) arg;
 
         // See if module is in chain...Cannot match against itself so start at superClass.
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isSame(moduleToCompare)) return context.getRuntime().getTrue();
         }
         
         return context.getRuntime().getFalse();
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         Ruby runtime = getRuntime();
         
         if (this == runtime.getObject()) runtime.secure(4);
 
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw runtime.newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         addMethodInternal(name, method);
     }
 
     public void addMethodInternal(String name, DynamicMethod method) {
         synchronized(getMethodsForWrite()) {
             addMethodAtBootTimeOnly(name, method);
             invalidateCacheDescendants();
         }
     }
 
     /**
      * This method is not intended for use by normal users; it is a fast-path
      * method that skips synchronization and hierarchy invalidation to speed
      * boot-time method definition.
      *
      * @param name The name to which to bind the method
      * @param method The method to bind
      */
     public void addMethodAtBootTimeOnly(String name, DynamicMethod method) {
         getMethodsForWrite().put(name, method);
 
         getRuntime().addProfiledMethod(name, method);
     }
 
     public void removeMethod(ThreadContext context, String name) {
         Ruby runtime = context.getRuntime();
         
         if (this == runtime.getObject()) runtime.secure(4);
 
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw runtime.newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethodsForWrite()) {
             DynamicMethod method = (DynamicMethod) getMethodsForWrite().remove(name);
             if (method == null) {
                 throw runtime.newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             invalidateCacheDescendants();
         }
         
         if (isSingleton()) {
             IRubyObject singleton = ((MetaClass)this).getAttached(); 
             singleton.callMethod(context, "singleton_method_removed", runtime.newSymbol(name));
         } else {
             callMethod(context, "method_removed", runtime.newSymbol(name));
         }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */    
     public DynamicMethod searchMethod(String name) {
         return searchWithCache(name).method;
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */    
     public CacheEntry searchWithCache(String name) {
         CacheEntry entry = cacheHit(name);
 
         if (entry != null) return entry;
 
         // we grab serial number first; the worst that will happen is we cache a later
         // update with an earlier serial number, which would just flush anyway
         int token = getCacheToken();
         DynamicMethod method = searchMethodInner(name);
 
         if (method instanceof DefaultMethod) {
             method = ((DefaultMethod)method).getMethodForCaching();
         }
 
         return method != null ? addToCache(name, method, token) : addToCache(name, UndefinedMethod.getInstance(), token);
     }
     
     public final int getCacheToken() {
         return generation;
     }
 
     private final Map<String, CacheEntry> getCachedMethods() {
         return this.cachedMethods;
     }
 
     private final Map<String, CacheEntry> getCachedMethodsForWrite() {
         Map<String, CacheEntry> myCachedMethods = this.cachedMethods;
         return myCachedMethods == Collections.EMPTY_MAP ?
             this.cachedMethods = new ConcurrentHashMap<String, CacheEntry>(0, 0.75f, 1) :
             myCachedMethods;
     }
     
     private CacheEntry cacheHit(String name) {
         CacheEntry cacheEntry = getCachedMethods().get(name);
 
         if (cacheEntry != null) {
             if (cacheEntry.token == getCacheToken()) {
                 return cacheEntry;
             }
         }
         
         return null;
     }
     
     protected static abstract class CacheEntryFactory {
         public abstract CacheEntry newCacheEntry(DynamicMethod method, int token);
 
         /**
          * Test all WrapperCacheEntryFactory instances in the chain for assignability
          * from the given class.
          *
          * @param cacheEntryFactoryClass the class from which to test assignability
          * @return whether the given class is assignable from any factory in the chain
          */
         public boolean hasCacheEntryFactory(Class cacheEntryFactoryClass) {
             CacheEntryFactory current = this;
             while (current instanceof WrapperCacheEntryFactory) {
                 if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
                     return true;
                 }
                 current = ((WrapperCacheEntryFactory)current).getPrevious();
             }
             if (cacheEntryFactoryClass.isAssignableFrom(current.getClass())) {
                 return true;
             }
             return false;
         }
     }
 
     /**
      * A wrapper CacheEntryFactory, for delegating cache entry creation along a chain.
      */
     protected static abstract class WrapperCacheEntryFactory extends CacheEntryFactory {
         /** The CacheEntryFactory being wrapped. */
         protected final CacheEntryFactory previous;
 
         /**
          * Construct a new WrapperCacheEntryFactory using the given CacheEntryFactory as
          * the "previous" wrapped factory.
          *
          * @param previous the wrapped factory
          */
         public WrapperCacheEntryFactory(CacheEntryFactory previous) {
             this.previous = previous;
         }
 
         public CacheEntryFactory getPrevious() {
             return previous;
         }
     }
 
     protected static final CacheEntryFactory NormalCacheEntryFactory = new CacheEntryFactory() {
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             return new CacheEntry(method, token);
         }
     };
 
     protected static class SynchronizedCacheEntryFactory extends WrapperCacheEntryFactory {
         public SynchronizedCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
             // delegate up the chain
             CacheEntry delegated = previous.newCacheEntry(method, token);
             return new CacheEntry(new SynchronizedDynamicMethod(delegated.method), delegated.token);
         }
     }
 
     protected static class ProfilingCacheEntryFactory extends WrapperCacheEntryFactory {
         public ProfilingCacheEntryFactory(CacheEntryFactory previous) {
             super(previous);
         }
         @Override
         public CacheEntry newCacheEntry(DynamicMethod method, int token) {
             if (method.isUndefined()) {
                 return new CacheEntry(method, token);
             }
             CacheEntry delegated = previous.newCacheEntry(method, token);
             return new CacheEntry(new ProfilingDynamicMethod(delegated.method), delegated.token);
         }
     }
 
     private volatile CacheEntryFactory cacheEntryFactory;
 
     // modifies this class only; used to make the Synchronized module synchronized
     public void becomeSynchronized() {
         cacheEntryFactory = new SynchronizedCacheEntryFactory(cacheEntryFactory);
     }
 
     public boolean isSynchronized() {
         return cacheEntryFactory.hasCacheEntryFactory(SynchronizedCacheEntryFactory.class);
     }
 
     private CacheEntry addToCache(String name, DynamicMethod method, int token) {
         CacheEntry entry = cacheEntryFactory.newCacheEntry(method, token);
         getCachedMethodsForWrite().put(name, entry);
 
         return entry;
     }
     
     protected DynamicMethod searchMethodInner(String name) {
         DynamicMethod method = getMethods().get(name);
         
         if (method != null) return method;
         
         return superClass == null ? null : superClass.searchMethodInner(name);
     }
 
     public void invalidateCacheDescendants() {
         if (DEBUG) System.out.println("invalidating descendants: " + classId);
         generation = getRuntime().getNextModuleGeneration();
         // update all hierarchies into which this module has been included
         synchronized (getRuntime().getHierarchyLock()) {
             for (RubyClass includingHierarchy : includingHierarchies) {
                 includingHierarchy.invalidateCacheDescendants();
             }
         }
     }
     
     protected void invalidateConstantCache() {
         getRuntime().incrementConstantGeneration();
     }    
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.isSame(clazz)) return module;
         }
 
         return null;
     }
 
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
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
 
         // JRUBY-2435: Aliasing eval and other "special" methods should display a warning
         // We warn because we treat certain method names as "special" for purposes of
         // optimization. Hopefully this will be enough to convince people not to alias
         // them.
         if (SCOPE_CAPTURING_METHODS.contains(oldName)) {
             runtime.getWarnings().warn("`" + oldName + "' should not be aliased");
         }
 
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
 
         invalidateCacheDescendants();
         putMethod(name, new AliasMethod(this, method, oldName));
     }
 
     public synchronized void defineAliases(List<String> aliases, String oldName) {
         testFrozen("module");
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = runtime.getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw runtime.newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
 
         for (String name: aliases) {
             if (oldName.equals(name)) continue;
 
             putMethod(name, new AliasMethod(this, method, oldName));
         }
         invalidateCacheDescendants();
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
 
         Ruby runtime = getRuntime();
         IRubyObject classObj = getConstantAtSpecial(name);
         RubyClass clazz;
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw runtime.newTypeError(name + " is not a class");
             clazz = (RubyClass)classObj;
 
             if (superClazz != null) {
                 RubyClass tmp = clazz.getSuperClass();
                 while (tmp != null && tmp.isIncluded()) tmp = tmp.getSuperClass(); // need to skip IncludedModuleWrappers
                 if (tmp != null) tmp = tmp.getRealClass();
                 if (tmp != superClazz) throw runtime.newTypeError("superclass mismatch for class " + name);
                 // superClazz = null;
             }
 
             if (runtime.getSafeLevel() >= 4) throw runtime.newTypeError("extending class prohibited");
         } else if (classProviders != null && (clazz = searchProvidersForClass(name, superClazz)) != null) {
             // reopen a java class
         } else {
             if (superClazz == null) superClazz = runtime.getObject();
             if (superClazz == runtime.getObject() && RubyInstanceConfig.REIFY_RUBY_CLASSES) {
                 clazz = RubyClass.newClass(runtime, superClazz, name, REIFYING_OBJECT_ALLOCATOR, this, true);
             } else {
                 clazz = RubyClass.newClass(runtime, superClazz, name, superClazz.getAllocator(), this, true);
             }
         }
 
         return clazz;
     }
 
     /** this method should be used only by interpreter or compiler 
      * 
      */
     public RubyModule defineOrGetModuleUnder(String name) {
         // This method is intended only for defining new modules in Ruby code
         Ruby runtime = getRuntime();
         IRubyObject moduleObj = getConstantAtSpecial(name);
         RubyModule module;
         if (moduleObj != null) {
             if (!moduleObj.isModule()) throw runtime.newTypeError(name + " is not a module");
             if (runtime.getSafeLevel() >= 4) throw runtime.newSecurityError("extending module prohibited");
             module = (RubyModule)moduleObj;
         } else if (classProviders != null && (module = searchProvidersForModule(name)) != null) {
             // reopen a java module
         } else {
             module = RubyModule.newModule(runtime, name, this, true); 
         }
         return module;
     }
 
     /** rb_define_class_under
      *  this method should be used only as an API to define/open nested classes 
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator) {
         return getRuntime().defineClassUnder(name, superClass, allocator, this);
     }
 
     /** rb_define_module_under
      *  this method should be used only as an API to define/open nested module
      */
     public RubyModule defineModuleUnder(String name) {
         return getRuntime().defineModuleUnder(name, this);
     }
 
     private void addAccessor(ThreadContext context, String internedName, Visibility visibility, boolean readable, boolean writeable) {
         assert internedName == internedName.intern() : internedName + " is not interned";
 
         final Ruby runtime = context.getRuntime();
 
         if (visibility == PRIVATE) {
             //FIXME warning
         } else if (visibility == MODULE_FUNCTION) {
             visibility = PRIVATE;
             // FIXME warning
         }
         final String variableName = ("@" + internedName).intern();
         if (readable) {
             addMethod(internedName, new JavaMethodZero(this, visibility, CallConfiguration.FrameNoneScopeNone) {
                 // volatile to ensure visibility across threads
                 private volatile RubyClass.VariableAccessor accessor = RubyClass.VariableAccessor.DUMMY_ACCESSOR;
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                     IRubyObject variable = (IRubyObject)verifyAccessor(self.getMetaClass().getRealClass()).get(self);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 private RubyClass.VariableAccessor verifyAccessor(RubyClass cls) {
                     RubyClass.VariableAccessor localAccessor = accessor;
                     if (localAccessor.getClassId() != cls.hashCode()) {
                         localAccessor = cls.getVariableAccessorForRead(variableName);
                         accessor = localAccessor;
                     }
                     return localAccessor;
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
         }
         if (writeable) {
             internedName = (internedName + "=").intern();
             addMethod(internedName, new JavaMethodOne(this, visibility, CallConfiguration.FrameNoneScopeNone) {
                 // volatile to ensure visibility across threads
                 private volatile RubyClass.VariableAccessor accessor = RubyClass.VariableAccessor.DUMMY_ACCESSOR;
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1) {
                     verifyAccessor(self.getMetaClass().getRealClass()).set(self, arg1);
                     return arg1;
                 }
 
                 private RubyClass.VariableAccessor verifyAccessor(RubyClass cls) {
                     RubyClass.VariableAccessor localAccessor = accessor;
                     if (localAccessor.getClassId() != cls.hashCode()) {
                         localAccessor = cls.getVariableAccessorForWrite(variableName);
                         accessor = localAccessor;
                     }
                     return localAccessor;
                 }
             });
             callMethod(context, "method_added", runtime.fastNewSymbol(internedName));
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
             exportMethod(methods[i].asJavaString(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined() && isModule()) {
             method = runtime.getObject().searchMethod(name);
         }
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
             }
 
             invalidateCacheDescendants();
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility() == PRIVATE);
         }
         return false;
     }
 
     public void checkMethodBound(ThreadContext context, IRubyObject[] args, Visibility visibility) {
         if (args.length == 0) {
             throw context.getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asJavaString();
 
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined() && method.getVisibility() != visibility) {
             Ruby runtime = context.getRuntime();
             RubyNameError.RubyNameErrorMessage message = new RubyNameError.RubyNameErrorMessage(runtime, this,
                     runtime.newString(name), method.getVisibility(), CallType.NORMAL);
 
             throw runtime.newNoMethodError(message.to_str(context).asJavaString(), name, NEVER);
         }
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String methodName, boolean bound, Visibility visibility) {
         return newMethod(receiver, methodName, bound, visibility, false, true);
     }
 
     public IRubyObject newMethod(IRubyObject receiver, final String methodName, boolean bound, Visibility visibility, boolean respondToMissing) {
         return newMethod(receiver, methodName, bound, visibility, respondToMissing, true);
     }
 
     public static class RespondToMissingMethod extends JavaMethod.JavaMethodNBlock {
         final CallSite site;
         public RespondToMissingMethod(RubyModule implClass, Visibility vis, String methodName) {
             super(implClass, vis);
 
             site = new FunctionalCachingCallSite(methodName);
         }
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             return site.call(context, self, self, args, block);
         }
 
         public boolean equals(Object other) {
             if (!(other instanceof RespondToMissingMethod)) return false;
 
             RespondToMissingMethod rtmm = (RespondToMissingMethod)other;
 
             return this.site.methodName.equals(rtmm.site.methodName) &&
                     getImplementationClass() == rtmm.getImplementationClass();
         }
     }
 
     public IRubyObject newMethod(IRubyObject receiver, final String methodName, boolean bound, Visibility visibility, boolean respondToMissing, boolean priv) {
         DynamicMethod method = searchMethod(methodName);
 
         if (method.isUndefined() ||
             (visibility != null && method.getVisibility() != visibility)) {
             if (respondToMissing) { // 1.9 behavior
                 if (receiver.respondsToMissing(methodName, priv)) {
                     method = new RespondToMissingMethod(this, PUBLIC, methodName);
                 } else {
                     throw getRuntime().newNameError("undefined method `" + methodName +
                         "' for class `" + this.getName() + "'", methodName);
                 }
             } else {
                 throw getRuntime().newNameError("undefined method `" + methodName +
                     "' for class `" + this.getName() + "'", methodName);
             }
         }
 
         RubyModule implementationModule = method.getImplementationClass();
         RubyModule originModule = this;
         while (originModule != implementationModule && originModule.isSingleton()) {
             originModule = ((MetaClass)originModule).getRealClass();
         }
 
         RubyMethod newMethod;
         if (bound) {
             newMethod = RubyMethod.newMethod(implementationModule, methodName, originModule, methodName, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(implementationModule, methodName, originModule, methodName, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     @JRubyMethod(name = "define_method", visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, Block block) {
         Ruby runtime = context.getRuntime();
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = context.getCurrentVisibility();
 
         if (visibility == MODULE_FUNCTION) visibility = PRIVATE;
         RubyProc proc = runtime.newProc(Block.Type.LAMBDA, block);
 
         // a normal block passed to define_method changes to do arity checking; make it a lambda
         proc.getBlock().type = Block.Type.LAMBDA;
         
         newMethod = createProcMethod(name, visibility, proc);
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, context.getCurrentVisibility(), context, runtime);
 
         return proc;
     }
     
     @JRubyMethod(name = "define_method", visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject body;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = context.getCurrentVisibility();
 
         if (visibility == MODULE_FUNCTION) visibility = PRIVATE;
         if (runtime.getProc().isInstance(arg1)) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (RubyProc)arg1;
             body = proc;
 
             newMethod = createProcMethod(name, visibility, proc);
         } else if (runtime.getMethod().isInstance(arg1)) {
             RubyMethod method = (RubyMethod)arg1;
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(), visibility);
         } else {
             throw runtime.newTypeError("wrong argument type " + arg1.getType().getName() + " (expected Proc/Method)");
         }
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, context.getCurrentVisibility(), context, runtime);
 
         return body;
     }
     @Deprecated
     public IRubyObject define_method(ThreadContext context, IRubyObject[] args, Block block) {
         switch (args.length) {
         case 1:
             return define_method(context, args[0], block);
         case 2:
             return define_method(context, args[0], args[1], block);
         default:
             throw context.getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 2)");
         }
     }
     
     private DynamicMethod createProcMethod(String name, Visibility visibility, RubyProc proc) {
         Block block = proc.getBlock();
         block.getBinding().getFrame().setKlazz(this);
         block.getBinding().getFrame().setName(name);
         block.getBinding().setMethod(name);
         
         StaticScope scope = block.getBody().getStaticScope();
 
         // for zsupers in define_method (blech!) we tell the proc scope to act as the "argument" scope
         scope.setArgumentScope(true);
 
         Arity arity = block.arity();
         // just using required is broken...but no more broken than before zsuper refactoring
         scope.setRequiredArgs(arity.required());
 
         if(!arity.isFixed()) {
             scope.setRestArg(arity.required());
         }
 
         return new ProcMethod(this, proc, visibility);
     }
 
     @Deprecated
     public IRubyObject executeUnder(ThreadContext context, Callback method, IRubyObject[] args, Block block) {
         context.preExecuteUnder(this, block);
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     @JRubyMethod(name = "name")
     public IRubyObject name() {
         Ruby runtime = getRuntime();
         if (getBaseName() == null) {
             return RubyString.newEmptyString(runtime);
         } else {
             return runtime.newString(getName());
         }
     }
 
     @JRubyMethod(name = "name", compat = RUBY1_9)
     public IRubyObject name19() {
         Ruby runtime = getRuntime();
         if (getBaseName() == null) {
             return runtime.getNil();
         } else {
             return runtime.newString(getName());
         }
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Map.Entry<String, DynamicMethod> entry : getMethods().entrySet()) {
             DynamicMethod method = entry.getValue();
             // Do not clone cached methods
             // FIXME: MRI copies all methods here
             if (method.getImplementationClass() == realType || method.isUndefined()) {
                 
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     /** rb_mod_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         super.initialize_copy(original);
 
         RubyModule originalModule = (RubyModule)original;
 
         if (!getMetaClass().isSingleton()) setMetaClass(originalModule.getSingletonClassClone());
         setSuperClass(originalModule.getSuperClass());
         if (originalModule.hasVariables()) syncVariables(originalModule.getVariableList());
         syncConstants(originalModule);
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     public void syncConstants(RubyModule other) {
         if (other.getConstantMap() != Collections.EMPTY_MAP) {
             getConstantMapForWrite().putAll(other.getConstantMap());
         }
     }
 
+    public void syncClassVariables(RubyModule other) {
+        if (other.getClassVariablesForRead() != Collections.EMPTY_MAP) {
+            getClassVariables().putAll(other.getClassVariablesForRead());
+        }
+    }
+
     /** rb_mod_included_modules
      *
      */
     @JRubyMethod(name = "included_modules")
     public RubyArray included_modules(ThreadContext context) {
         RubyArray ary = context.getRuntime().newArray();
 
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
     @JRubyMethod(name = "ancestors")
     public RubyArray ancestors(ThreadContext context) {
         return context.getRuntime().newArray(getAncestorList());
     }
     
     @Deprecated
     public RubyArray ancestors() {
         return getRuntime().newArray(getAncestorList());
     }
 
     public List<IRubyObject> getAncestorList() {
         ArrayList<IRubyObject> list = new ArrayList<IRubyObject>();
 
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if(!module.isSingleton()) list.add(module.getNonIncludedClass());
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     @Override
     public int hashCode() {
         return id;
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     @Override
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = ((MetaClass)this).getAttached();
             StringBuilder buffer = new StringBuilder("#<Class:");
             if (attached != null) { // FIXME: figure out why we get null sometimes
                 if(attached instanceof RubyClass || attached instanceof RubyModule){
                     buffer.append(attached.inspect());
                 }else{
                     buffer.append(attached.anyToString());
                 }
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     @JRubyMethod(name = "===", required = 1)
     @Override
     public RubyBoolean op_eqq(ThreadContext context, IRubyObject obj) {
         return context.getRuntime().newBoolean(isInstance(obj));
     }
 
     /**
      * We override equals here to provide a faster path, since equality for modules
      * is pretty cut and dried.
      * @param other The object to check for equality
      * @return true if reference equality, false otherwise
      */
     @Override
     public boolean equals(Object other) {
         return this == other;
     }
 
     @JRubyMethod(name = "==", required = 1)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     /** rb_mod_freeze
      *
      */
     @JRubyMethod(name = "freeze")
     @Override
     public final IRubyObject freeze(ThreadContext context) {
         to_s();
         return super.freeze(context);
     }
 
     /** rb_mod_le
     *
     */
     @JRubyMethod(name = "<=", required = 1)
    public IRubyObject op_le(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         if (isKindOfModule((RubyModule) obj)) return getRuntime().getTrue();
         if (((RubyModule) obj).isKindOfModule(this)) return getRuntime().getFalse();
 
         return getRuntime().getNil();
     }
 
     /** rb_mod_lt
     *
     */
     @JRubyMethod(name = "<", required = 1)
    public IRubyObject op_lt(IRubyObject obj) {
         return obj == this ? getRuntime().getFalse() : op_le(obj);
     }
 
     /** rb_mod_ge
     *
     */
     @JRubyMethod(name = ">=", required = 1)
    public IRubyObject op_ge(IRubyObject obj) {
         if (!(obj instanceof RubyModule)) {
             throw getRuntime().newTypeError("compared with non class/module");
         }
 
         return ((RubyModule) obj).op_le(this);
     }
 
     /** rb_mod_gt
     *
     */
     @JRubyMethod(name = ">", required = 1)
    public IRubyObject op_gt(IRubyObject obj) {
         return this == obj ? getRuntime().getFalse() : op_ge(obj);
     }
 
     /** rb_mod_cmp
     *
     */
     @JRubyMethod(name = "<=>", required = 1)
    public IRubyObject op_cmp(IRubyObject obj) {
         if (this == obj) return getRuntime().newFixnum(0);
         if (!(obj instanceof RubyModule)) return getRuntime().getNil();
 
         RubyModule module = (RubyModule) obj;
 
         if (module.isKindOfModule(this)) return getRuntime().newFixnum(1);
         if (this.isKindOfModule(module)) return getRuntime().newFixnum(-1);
 
         return getRuntime().getNil();
     }
 
     public boolean isKindOfModule(RubyModule type) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.isSame(type)) return true;
         }
 
         return false;
     }
 
     protected boolean isSame(RubyModule module) {
         return this == module;
     }
 
     /** rb_mod_initialize
      *
      */
     @JRubyMethod(name = "initialize", frame = true, visibility = PRIVATE)
     public IRubyObject initialize(Block block) {
         if (block.isGiven()) {
             // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
             block.getBinding().setVisibility(PUBLIC);
             block.yieldNonArray(getRuntime().getCurrentContext(), this, this, this);
         }
 
         return getRuntime().getNil();
     }
     
     public void addReadWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, true, true);
     }
     
     public void addReadAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, true, false);
     }
     
     public void addWriteAttribute(ThreadContext context, String name) {
         addAccessor(context, name.intern(), PUBLIC, false, true);
     }
 
     /** rb_mod_attr
      *
      */
     @JRubyMethod(name = "attr", required = 1, optional = 1, visibility = PRIVATE, reads = VISIBILITY, compat = RUBY1_8)
     public IRubyObject attr(ThreadContext context, IRubyObject[] args) {
         boolean writeable = args.length > 1 ? args[1].isTrue() : false;
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
         
         addAccessor(context, args[0].asJavaString().intern(), visibility, true, writeable);
 
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "attr", rest = true, visibility = PRIVATE, reads = VISIBILITY, compat = RUBY1_9)
     public IRubyObject attr19(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         if (args.length == 2 && (args[1] == runtime.getTrue() || args[1] == runtime.getFalse())) {
             runtime.getWarnings().warn(ID.OBSOLETE_ARGUMENT, "optional boolean argument is obsoleted");
             addAccessor(context, args[0].asJavaString().intern(), context.getCurrentVisibility(), args[0].isTrue(), true);
             return runtime.getNil();
         }
 
         return attr_reader(context, args);
     }
 
     @Deprecated
     public IRubyObject attr_reader(IRubyObject[] args) {
         return attr_reader(getRuntime().getCurrentContext(), args);
     }
     
     /** rb_mod_attr_reader
      *
      */
     @JRubyMethod(name = "attr_reader", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_reader(ThreadContext context, IRubyObject[] args) {
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), visibility, true, false);
         }
 
         return context.getRuntime().getNil();
     }
 
     /** rb_mod_attr_writer
      *
      */
     @JRubyMethod(name = "attr_writer", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_writer(ThreadContext context, IRubyObject[] args) {
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             addAccessor(context, args[i].asJavaString().intern(), visibility, false, true);
         }
 
         return context.getRuntime().getNil();
     }
 
 
     @Deprecated
     public IRubyObject attr_accessor(IRubyObject[] args) {
         return attr_accessor(getRuntime().getCurrentContext(), args);
     }
 
     /** rb_mod_attr_accessor
      *
      */
     @JRubyMethod(name = "attr_accessor", rest = true, visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject attr_accessor(ThreadContext context, IRubyObject[] args) {
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility visibility = context.getCurrentVisibility();
 
         for (int i = 0; i < args.length; i++) {
             // This is almost always already interned, since it will be called with a symbol in most cases
             // but when created from Java code, we might get an argument that needs to be interned.
             // addAccessor has as a precondition that the string MUST be interned
             addAccessor(context, args[i].asJavaString().intern(), visibility, true, true);
         }
 
         return context.getRuntime().getNil();
     }
 
     /**
      * Get a list of all instance methods names of the provided visibility unless not is true, then 
      * get all methods which are not the provided 
      * 
      * @param args passed into one of the Ruby instance_method methods
      * @param visibility to find matching instance methods against
      * @param not if true only find methods not matching supplied visibility
      * @return a RubyArray of instance method names
      */
     private RubyArray instance_methods(IRubyObject[] args, final Visibility visibility, boolean not, boolean useSymbols) {
         boolean includeSuper = args.length > 0 ? args[0].isTrue() : true;
         Ruby runtime = getRuntime();
         RubyArray ary = runtime.newArray();
         Set<String> seen = new HashSet<String>();
 
         populateInstanceMethodNames(seen, ary, visibility, not, useSymbols, includeSuper);
 
         return ary;
     }
 
     public void populateInstanceMethodNames(Set<String> seen, RubyArray ary, final Visibility visibility, boolean not, boolean useSymbols, boolean includeSuper) {
         Ruby runtime = getRuntime();
 
         for (RubyModule type = this; type != null; type = type.getSuperClass()) {
             RubyModule realType = type.getNonIncludedClass();
             for (Map.Entry entry : type.getMethods().entrySet()) {
                 String methodName = (String) entry.getKey();
 
                 if (! seen.contains(methodName)) {
                     seen.add(methodName);
 
                     DynamicMethod method = (DynamicMethod) entry.getValue();
                     if (method.getImplementationClass() == realType &&
                         (!not && method.getVisibility() == visibility || (not && method.getVisibility() != visibility)) &&
                         ! method.isUndefined()) {
 
                         ary.append(useSymbols ? runtime.newSymbol(methodName) : runtime.newString(methodName));
                     }
                 }
             }
 
             if (!includeSuper) {
                 break;
             }
         }
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray instance_methods(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, true, false);
     }
 
     @JRubyMethod(name = "instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, true, true);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray public_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PUBLIC, false, false);
     }
 
     @JRubyMethod(name = "public_instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray public_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PUBLIC, false, true);
     }
 
     @JRubyMethod(name = "instance_method", required = 1)
     public IRubyObject instance_method(IRubyObject symbol) {
         return newMethod(null, symbol.asJavaString(), false, null);
     }
 
     /** rb_class_protected_instance_methods
      *
      */
     @JRubyMethod(name = "protected_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray protected_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PROTECTED, false, false);
     }
 
     @JRubyMethod(name = "protected_instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray protected_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PROTECTED, false, true);
     }
 
     /** rb_class_private_instance_methods
      *
      */
     @JRubyMethod(name = "private_instance_methods", optional = 1, compat = RUBY1_8)
     public RubyArray private_instance_methods(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, false, false);
     }
 
     @JRubyMethod(name = "private_instance_methods", optional = 1, compat = RUBY1_9)
     public RubyArray private_instance_methods19(IRubyObject[] args) {
         return instance_methods(args, PRIVATE, false, true);
     }
 
     /** rb_mod_append_features
      *
      */
     @JRubyMethod(name = "append_features", required = 1, visibility = PRIVATE)
     public RubyModule append_features(IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             // MRI error message says Class, even though Module is ok 
             throw getRuntime().newTypeError(module,getRuntime().getClassClass());
         }
         ((RubyModule) module).includeModule(this);
         return this;
     }
 
     /** rb_mod_extend_object
      *
      */
     @JRubyMethod(name = "extend_object", required = 1, visibility = PRIVATE)
     public IRubyObject extend_object(IRubyObject obj) {
         obj.getSingletonClass().includeModule(this);
         return obj;
     }
 
     /** rb_mod_include
      *
      */
     @JRubyMethod(name = "include", rest = true, visibility = PRIVATE)
     public RubyModule include(IRubyObject[] modules) {
         ThreadContext context = getRuntime().getCurrentContext();
         // MRI checks all types first:
         for (int i = modules.length; --i >= 0; ) {
             IRubyObject obj = modules[i];
             if (!obj.isModule()) throw context.getRuntime().newTypeError(obj, context.getRuntime().getModule());
         }
         for (int i = modules.length - 1; i >= 0; i--) {
             modules[i].callMethod(context, "append_features", this);
             modules[i].callMethod(context, "included", this);
         }
 
         return this;
     }
 
     @JRubyMethod(name = "included", required = 1, visibility = PRIVATE)
     public IRubyObject included(ThreadContext context, IRubyObject other) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "extended", required = 1, frame = true, visibility = PRIVATE)
     public IRubyObject extended(ThreadContext context, IRubyObject other, Block block) {
         return context.getRuntime().getNil();
     }
 
     private void setVisibility(ThreadContext context, IRubyObject[] args, Visibility visibility) {
         if (context.getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw context.getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             // Note: we change current frames visibility here because the methods which call
             // this method are all "fast" (e.g. they do not created their own frame).
             context.setCurrentVisibility(visibility);
         } else {
             setMethodVisibility(args, visibility);
         }
     }
 
     /** rb_mod_public
      *
      */
     @JRubyMethod(name = "public", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbPublic(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PUBLIC);
         return this;
     }
 
     /** rb_mod_protected
      *
      */
     @JRubyMethod(name = "protected", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbProtected(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PROTECTED);
         return this;
     }
 
     /** rb_mod_private
      *
      */
     @JRubyMethod(name = "private", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule rbPrivate(ThreadContext context, IRubyObject[] args) {
         setVisibility(context, args, PRIVATE);
         return this;
     }
 
     /** rb_mod_modfunc
      *
      */
     @JRubyMethod(name = "module_function", rest = true, visibility = PRIVATE, writes = VISIBILITY)
     public RubyModule module_function(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw runtime.newSecurityError("Insecure: can't change method visibility");
         }
 
         if (args.length == 0) {
             context.setCurrentVisibility(MODULE_FUNCTION);
         } else {
             setMethodVisibility(args, PRIVATE);
 
             // FIXME: This is duplicated in exportMethod; should be unified.
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asJavaString().intern();
                 DynamicMethod method = searchMethod(name);
                 if (method.isUndefined() && isModule()) {
                     method = runtime.getObject().searchMethod(name);
                 }
                 if (method.isUndefined()) {
                     throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
                 }
                 getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), method, PUBLIC));
                 callMethod(context, "singleton_method_added", context.getRuntime().fastNewSymbol(name));
             }
         }
         return this;
     }
 
     @JRubyMethod(name = "method_added", required = 1, visibility = PRIVATE)
     public IRubyObject method_added(ThreadContext context, IRubyObject nothing) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "method_removed", required = 1, visibility = PRIVATE)
     public IRubyObject method_removed(ThreadContext context, IRubyObject nothing) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "method_undefined", required = 1, visibility = PRIVATE)
     public IRubyObject method_undefined(ThreadContext context, IRubyObject nothing) {
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "method_defined?", required = 1)
     public RubyBoolean method_defined_p(ThreadContext context, IRubyObject symbol) {
         return isMethodBound(symbol.asJavaString(), true) ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "public_method_defined?", required = 1)
     public IRubyObject public_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
         
         return context.getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == PUBLIC);
     }
 
     @JRubyMethod(name = "protected_method_defined?", required = 1)
     public IRubyObject protected_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 	    
         return context.getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == PROTECTED);
     }
 	
     @JRubyMethod(name = "private_method_defined?", required = 1)
     public IRubyObject private_method_defined(ThreadContext context, IRubyObject symbol) {
         DynamicMethod method = searchMethod(symbol.asJavaString());
 	    
         return context.getRuntime().newBoolean(!method.isUndefined() && method.getVisibility() == PRIVATE);
     }
 
     @JRubyMethod(name = "public_class_method", rest = true)
     public RubyModule public_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, PUBLIC);
         return this;
     }
 
     @JRubyMethod(name = "private_class_method", rest = true)
     public RubyModule private_class_method(IRubyObject[] args) {
         getMetaClass().setMethodVisibility(args, PRIVATE);
         return this;
     }
 
     @JRubyMethod(name = "alias_method", required = 2, visibility = PRIVATE)
     public RubyModule alias_method(ThreadContext context, IRubyObject newId, IRubyObject oldId) {
         String newName = newId.asJavaString();
         defineAlias(newName, oldId.asJavaString());
         RubySymbol newSym = newId instanceof RubySymbol ? (RubySymbol)newId :
             context.getRuntime().newSymbol(newName);
         if (isSingleton()) {
             ((MetaClass)this).getAttached().callMethod(context, "singleton_method_added", newSym);
         } else {
             callMethod(context, "method_added", newSym);
         }
         return this;
     }
 
     @JRubyMethod(name = "undef_method", required = 1, rest = true, visibility = PRIVATE)
     public RubyModule undef_method(ThreadContext context, IRubyObject[] args) {
         for (int i=0; i<args.length; i++) {
             undef(context, args[i].asJavaString());
         }
         return this;
     }
 
     @JRubyMethod(name = {"module_eval", "class_eval"}, frame = true)
     public IRubyObject module_eval(ThreadContext context, Block block) {
         return specificEval(context, this, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"}, frame = true)
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, this, arg0, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"}, frame = true)
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, this, arg0, arg1, block);
     }
     @JRubyMethod(name = {"module_eval", "class_eval"}, frame = true)
     public IRubyObject module_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, this, arg0, arg1, arg2, block);
     }
     @Deprecated
     public IRubyObject module_eval(ThreadContext context, IRubyObject[] args, Block block) {
         return specificEval(context, this, args, block);
     }
 
     @JRubyMethod(name = {"module_exec", "class_exec"}, frame = true)
     public IRubyObject module_exec(ThreadContext context, Block block) {
         if (block.isGiven()) {
             return yieldUnder(context, this, block);
         } else {
             throw context.getRuntime().newLocalJumpErrorNoBlock();
         }
     }
     @JRubyMethod(name = {"module_exec", "class_exec"}, rest = true, frame = true)
     public IRubyObject module_exec(ThreadContext context, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             return yieldUnder(context, this, args, block);
         } else {
             throw context.getRuntime().newLocalJumpErrorNoBlock();
         }
     }
 
     @JRubyMethod(name = "remove_method", required = 1, rest = true, visibility = PRIVATE)
     public RubyModule remove_method(ThreadContext context, IRubyObject[] args) {
         for(int i=0;i<args.length;i++) {
             removeMethod(context, args[i].asJavaString());
         }
         return this;
     }
 
     public static void marshalTo(RubyModule module, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(module);
         output.writeString(MarshalStream.getPathFromClass(module));
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyModule result = UnmarshalStream.getModuleFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     /* Module class methods */
     
     /** 
      * Return an array of nested modules or classes.
      */
     @JRubyMethod(name = "nesting", frame = true, meta = true)
     public static RubyArray nesting(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.getRuntime();
         RubyModule object = runtime.getObject();
         StaticScope scope = context.getCurrentScope().getStaticScope();
         RubyArray result = runtime.newArray();
         
         for (StaticScope current = scope; current.getModule() != object; current = current.getPreviousCRefScope()) {
             result.append(current.getModule());
         }
         
         return result;
     }
 
     /**
      * Include the given module and all related modules into the hierarchy above
      * this module/class. Inspects the hierarchy to ensure the same module isn't
      * included twice, and selects an appropriate insertion point for each incoming
      * module.
      * 
      * @param baseModule The module to include, along with any modules it itself includes
      */
     private void doIncludeModule(RubyModule baseModule) {
         List<RubyModule> modulesToInclude = gatherModules(baseModule);
         
         RubyModule currentInclusionPoint = this;
         ModuleLoop: for (RubyModule nextModule : modulesToInclude) {
             checkForCyclicInclude(nextModule);
 
             boolean superclassSeen = false;
 
             // scan class hierarchy for module
             for (RubyClass nextClass = this.getSuperClass(); nextClass != null; nextClass = nextClass.getSuperClass()) {
                 if (doesTheClassWrapTheModule(nextClass, nextModule)) {
                     // next in hierarchy is an included version of the module we're attempting,
                     // so we skip including it
                     
                     // if we haven't encountered a real superclass, use the found module as the new inclusion point
                     if (!superclassSeen) currentInclusionPoint = nextClass;
                     
                     continue ModuleLoop;
                 } else {
                     superclassSeen = true;
                 }
             }
 
             currentInclusionPoint = proceedWithInclude(currentInclusionPoint, nextModule);
         }
     }
     
     /**
      * Is the given class a wrapper for the specified module?
      * 
      * @param theClass The class to inspect
      * @param theModule The module we're looking for
      * @return true if the class is a wrapper for the module, false otherwise
      */
     private boolean doesTheClassWrapTheModule(RubyClass theClass, RubyModule theModule) {
         return theClass.isIncluded() &&
                 theClass.getNonIncludedClass() == theModule.getNonIncludedClass();
     }
 
     /**
      * Gather all modules that would be included by including the given module.
      * The resulting list contains the given module and its (zero or more)
      * module-wrapping superclasses.
      * 
      * @param baseModule The base module from which to aggregate modules
      * @return A list of all modules that would be included by including the given module
      */
     private List<RubyModule> gatherModules(RubyModule baseModule) {
         // build a list of all modules to consider for inclusion
         List<RubyModule> modulesToInclude = new ArrayList<RubyModule>();
         while (baseModule != null) {
             modulesToInclude.add(baseModule);
             baseModule = baseModule.getSuperClass();
         }
 
         return modulesToInclude;
     }
 
     /**
      * Actually proceed with including the specified module above the given target
      * in a hierarchy. Return the new module wrapper.
      * 
      * @param insertAbove The hierarchy target above which to include the wrapped module
      * @param moduleToInclude The module to wrap and include
      * @return The new module wrapper resulting from this include
      */
     private RubyModule proceedWithInclude(RubyModule insertAbove, RubyModule moduleToInclude) {
         // In the current logic, if we get here we know that module is not an
         // IncludedModuleWrapper, so there's no need to fish out the delegate. But just
         // in case the logic should change later, let's do it anyway
         RubyClass wrapper = new IncludedModuleWrapper(getRuntime(), insertAbove.getSuperClass(), moduleToInclude.getNonIncludedClass());
         
         // if the insertion point is a class, update subclass lists
         if (insertAbove instanceof RubyClass) {
             RubyClass insertAboveClass = (RubyClass)insertAbove;
             
             // if there's a non-null superclass, we're including into a normal class hierarchy;
             // update subclass relationships to avoid stale parent/child relationships
             if (insertAboveClass.getSuperClass() != null) {
                 insertAboveClass.getSuperClass().replaceSubclass(insertAboveClass, wrapper);
             }
             
             wrapper.addSubclass(insertAboveClass);
         }
         
         insertAbove.setSuperClass(wrapper);
         insertAbove = insertAbove.getSuperClass();
         return insertAbove;
     }
 
 
     //
     ////////////////// CLASS VARIABLE RUBY METHODS ////////////////
     //
 
     @JRubyMethod(name = "class_variable_defined?", required = 1)
     public IRubyObject class_variable_defined_p(ThreadContext context, IRubyObject var) {
         String internedName = validateClassVariable(var.asJavaString().intern());
         RubyModule module = this;
         do {
             if (module.fastHasClassVariable(internedName)) {
                 return context.getRuntime().getTrue();
             }
         } while ((module = module.getSuperClass()) != null);
 
         return context.getRuntime().getFalse();
     }
 
     /** rb_mod_cvar_get
      *
      */
     @JRubyMethod(name = "class_variable_get", required = 1, visibility = PRIVATE)
     public IRubyObject class_variable_get(IRubyObject var) {
         return fastGetClassVar(validateClassVariable(var.asJavaString()).intern());
     }
 
     /** rb_mod_cvar_set
      *
      */
     @JRubyMethod(name = "class_variable_set", required = 2, visibility = PRIVATE)
     public IRubyObject class_variable_set(IRubyObject var, IRubyObject value) {
         return fastSetClassVar(validateClassVariable(var.asJavaString()).intern(), value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     @JRubyMethod(name = "remove_class_variable", required = 1, visibility = PRIVATE)
     public IRubyObject remove_class_variable(ThreadContext context, IRubyObject name) {
         String javaName = validateClassVariable(name.asJavaString());
         IRubyObject value;
 
         if ((value = deleteClassVariable(javaName)) != null) {
             return value;
         }
 
         if (fastIsClassVarDefined(javaName)) {
             throw cannotRemoveError(javaName);
         }
 
         throw context.getRuntime().newNameError("class variable " + javaName + " not defined for " + getName(), javaName);
     }
 
     /** rb_mod_class_variables
      *
      */
     @JRubyMethod(name = "class_variables", compat = RUBY1_8)
     public RubyArray class_variables(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyArray ary = runtime.newArray();
         
         Collection<String> names = classVariablesCommon();
         ary.addAll(names);
         return ary;
     }
 
     @JRubyMethod(name = "class_variables", compat = RUBY1_9)
     public RubyArray class_variables19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyArray ary = runtime.newArray();
         
         Collection<String> names = classVariablesCommon();
         for (String name : names) {
             ary.add(runtime.newSymbol(name));
         }
         return ary;
     }
 
     private Collection<String> classVariablesCommon() {
         Set<String> names = new HashSet<String>();
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             names.addAll(p.getClassVariableNameList());
         }
         return names;
     }
 
 
     //
     ////////////////// CONSTANT RUBY METHODS ////////////////
     //
 
     /** rb_mod_const_defined
      *
      */
     @JRubyMethod(name = "const_defined?", required = 1, compat = RUBY1_8)
     public RubyBoolean const_defined_p(ThreadContext context, IRubyObject symbol) {
         // Note: includes part of fix for JRUBY-1339
         return context.getRuntime().newBoolean(fastIsConstantDefined(validateConstant(symbol.asJavaString()).intern()));
     }
 
     @JRubyMethod(name = "const_defined?", required = 1, compat = RUBY1_9)
     public RubyBoolean const_defined_p19(ThreadContext context, IRubyObject symbol) {
         // Note: includes part of fix for JRUBY-1339
         return context.getRuntime().newBoolean(fastIsConstantDefined19(validateConstant(symbol.asJavaString()).intern()));
     }
 
     /** rb_mod_const_get
      *
      */
     @JRubyMethod(name = "const_get", required = 1)
     public IRubyObject const_get(IRubyObject symbol) {
         return getConstant(validateConstant(symbol.asJavaString()));
     }
 
     /** rb_mod_const_set
      *
      */
     @JRubyMethod(name = "const_set", required = 2)
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         IRubyObject constant = fastSetConstant(validateConstant(symbol.asJavaString()).intern(), value);
 
         if (constant instanceof RubyModule) {
             ((RubyModule)constant).calculateName();
         }
         return constant;
     }
 
     @JRubyMethod(name = "remove_const", required = 1, visibility = PRIVATE)
     public IRubyObject remove_const(ThreadContext context, IRubyObject rubyName) {
         String name = validateConstant(rubyName.asJavaString());
         IRubyObject value;
         if ((value = deleteConstant(name)) != null) {
             invalidateConstantCache();
             if (value != UNDEF) {
                 return value;
             }
             context.getRuntime().getLoadService().removeAutoLoadFor(getName() + "::" + name);
             // FIXME: I'm not sure this is right, but the old code returned
             // the undef, which definitely isn't right...
             return context.getRuntime().getNil();
         }
 
         if (hasConstantInHierarchy(name)) {
             throw cannotRemoveError(name);
         }
 
         throw context.getRuntime().newNameError("constant " + name + " not defined for " + getName(), name);
     }
 
     private boolean hasConstantInHierarchy(final String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.hasConstant(name)) {
                 return true;
         }
         }
         return false;
     }
     
     /**
      * Base implementation of Module#const_missing, throws NameError for specific missing constant.
      * 
      * @param name The constant name which was found to be missing
      * @return Nothing! Absolutely nothing! (though subclasses might choose to return something)
      */
     @JRubyMethod(name = "const_missing", required = 1, frame = true)
     public IRubyObject const_missing(ThreadContext context, IRubyObject rubyName, Block block) {
         Ruby runtime = context.getRuntime();
         String name;
         
         if (this != runtime.getObject()) {
             name = getName() + "::" + rubyName.asJavaString();
         } else {
             name = rubyName.asJavaString();
         }
 
         throw runtime.newNameError("uninitialized constant " + name, name);
     }
 
     @JRubyMethod(name = "constants", compat = RUBY1_8)
     public RubyArray constants(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         RubyArray array = runtime.newArray();
         Collection<String> constantNames = constantsCommon(runtime, true, true);
         array.addAll(constantNames);
         
         return array;
     }
 
     @JRubyMethod(name = "constants", compat = RUBY1_9)
     public RubyArray constants19(ThreadContext context) {
         return constantsCommon19(context, true, true);
     }
 
     @JRubyMethod(name = "constants", compat = RUBY1_9)
     public RubyArray constants19(ThreadContext context, IRubyObject allConstants) {
         return constantsCommon19(context, false, allConstants.isTrue());
     }
     
     public RubyArray constantsCommon19(ThreadContext context, boolean replaceModule, boolean allConstants) {
         Ruby runtime = context.getRuntime();
         RubyArray array = runtime.newArray();
         
         Collection<String> constantNames = constantsCommon(runtime, replaceModule, allConstants);
         
         for (String name : constantNames) {
             array.add(runtime.newSymbol(name));
         }
         return array;
     }
 
     /** rb_mod_constants
      *
      */
     public Collection<String> constantsCommon(Ruby runtime, boolean replaceModule, boolean allConstants) {
         RubyModule objectClass = runtime.getObject();
 
         Collection<String> constantNames = new HashSet<String>();
         if (allConstants) {
             if ((replaceModule && runtime.getModule() == this) || objectClass == this) {
                 constantNames = objectClass.getConstantNames();
             } else {
                 Set<String> names = new HashSet<String>();
