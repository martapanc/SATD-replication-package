diff --git a/src/org/jruby/RubyBasicObject.java b/src/org/jruby/RubyBasicObject.java
index 60e76f9507..5653fb8000 100644
--- a/src/org/jruby/RubyBasicObject.java
+++ b/src/org/jruby/RubyBasicObject.java
@@ -1,1954 +1,2970 @@
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
+import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
+import java.util.Set;
 import java.util.concurrent.atomic.AtomicBoolean;
 
 import org.jruby.anno.JRubyMethod;
+import org.jruby.common.IRubyWarnings.ID;
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
 
-        objectClass.defineAnnotatedMethods(BasicObjectMethods.class);
         objectClass.defineAnnotatedMethods(RubyBasicObject.class);
 
         runtime.setDefaultMethodMissing(objectClass.searchMethod("method_missing"));
 
         return objectClass;
     }
 
-    /**
-     * Interestingly, the Object class doesn't really have that many
-     * methods for itself. Instead almost all of the Object methods
-     * are really defined on the Kernel module. This class is a holder
-     * for all Object methods.
-     *
-     * @see RubyKernel
-     */
-    public static class BasicObjectMethods {
-        @JRubyMethod(name = "initialize", visibility = PRIVATE)
-        public static IRubyObject intialize(IRubyObject self) {
-            return self.getRuntime().getNil();
-        }
+    public IRubyObject initialize() {
+        return getRuntime().getNil();
+    }
+
+    @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
+    public IRubyObject initialize19() {
+        return getRuntime().getNil();
+    }
+
+    @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
+    public IRubyObject initialize19(IRubyObject arg0) {
+        return getRuntime().getNil();
+    }
+
+    @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
+    public IRubyObject initialize19(IRubyObject arg0, IRubyObject arg1) {
+        return getRuntime().getNil();
+    }
+
+    @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
+    public IRubyObject initialize19(IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
+        return getRuntime().getNil();
+    }
+
+    @JRubyMethod(name = "initialize", visibility = PRIVATE, rest = true, compat = RUBY1_9)
+    public IRubyObject initialize19(IRubyObject[] args) {
+        return getRuntime().getNil();
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
             // fastest path; builtin respond_to? which just does isMethodBound
             return getMetaClass().isMethodBound(name, false);
         } else if (!method.isUndefined()) {
             // medium path, invoke user's respond_to? if defined
             return method.call(getRuntime().getCurrentContext(), this, metaClass, "respond_to?", getRuntime().newSymbol(name)).isTrue();
         } else {
             // slowest path, full callMethod to hit method_missing if present, or produce error
             return callMethod(getRuntime().getCurrentContext(), "respond_to?", getRuntime().newSymbol(name)).isTrue();
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
 
         if (original.hasVariables()) clone.syncVariables(original);
         if (original instanceof RubyModule) {
             RubyModule cloneMod = (RubyModule)clone;
             cloneMod.syncConstants((RubyModule)original);
             cloneMod.syncClassVariables((RubyModule)original);
         }
 
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
-        RubyObject clone = (RubyObject)getMetaClass().getRealClass().allocate();
+        RubyBasicObject clone = (RubyBasicObject)getMetaClass().getRealClass().allocate();
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
             clone.syncVariables(klass);
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
             return id.longValue();
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
-        // Remain unimplemented due to problems with the double java hierarchy
-        return context.getRuntime().getNil();
+        return op_equal_19(context, obj);
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
     @Deprecated
     public void syncVariables(List<Variable<Object>> variables) {
         variableTableSync(variables);
     }
 
     /**
      * Sync one this object's variables with other's - this is used to make
      * rbClone work correctly.
      */
     public void syncVariables(IRubyObject other) {
         RubyClass realClass = metaClass.getRealClass();
         RubyClass otherRealClass = other.getMetaClass().getRealClass();
         boolean sameTable = otherRealClass == realClass;
         
         for (Map.Entry<String, RubyClass.VariableAccessor> entry : otherRealClass.getVariableAccessorsForRead().entrySet()) {
             RubyClass.VariableAccessor accessor = entry.getValue();
             Object value = accessor.get(other);
             
             if (value != null) {
                 if (sameTable) {
                     accessor.set(this, value);
                 } else {
                     realClass.getVariableAccessorForWrite(accessor.getName()).set(this, value);
                 }
             }
         }
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
             evalStr = (RubyString)arg0;
         } else {
             evalStr = arg0.convertToString();
         }
 
         String file = arg1.convertToString().asJavaString();
         int line = (int)(arg2.convertToInteger().getLongValue() - 1);
 
         return evalUnder(context, mod, evalStr, file, line);
     }
 
     protected RubyModule getInstanceEvalClass() {
         if (isImmediate()) {
             // Ruby uses Qnil here, we use "dummy" because we need a class
             return getRuntime().getDummy();
         } else {
             return getSingletonClass();
         }
     }
 
     /**
      * Evaluates the string src with self set to the current object,
      * using the module under as the context.
      */
     public IRubyObject evalUnder(final ThreadContext context, RubyModule under, RubyString src, String file, int line) {
         Visibility savedVisibility = context.getCurrentVisibility();
         context.setCurrentVisibility(PUBLIC);
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
      * Class that keeps track of the finalizers for the object under
      * operation.
      */
     public static class Finalizer implements Finalizable {
         private RubyFixnum id;
         private IRubyObject firstFinalizer;
         private List<IRubyObject> finalizers;
         private AtomicBoolean finalized;
 
         public Finalizer(RubyFixnum id) {
             this.id = id;
             this.finalized = new AtomicBoolean(false);
         }
 
         public void addFinalizer(IRubyObject finalizer) {
             if (firstFinalizer == null) {
                 firstFinalizer = finalizer;
             } else {
                 if (finalizers == null) finalizers = new ArrayList<IRubyObject>(4);
                 finalizers.add(finalizer);
             }
         }
 
         public void removeFinalizers() {
             firstFinalizer = null;
             finalizers = null;
         }
 
         @Override
         public void finalize() {
             if (finalized.compareAndSet(false, true)) {
                 if (firstFinalizer != null) callFinalizer(firstFinalizer);
                 if (finalizers != null) {
                     for (int i = 0; i < finalizers.size(); i++) {
                         callFinalizer(finalizers.get(i));
                     }
                 }
             }
         }
         
         private void callFinalizer(IRubyObject finalizer) {
             RuntimeHelpers.invoke(
                     finalizer.getRuntime().getCurrentContext(),
                     finalizer, "call", id);
         }
     }
+
+    // These are added to allow their being called against BasicObject and
+    // subclass instances. Because Kernel can be included into BasicObject and
+    // subclasses, there's a possibility of calling them from Ruby.
+    // See JRUBY-4871
+
+    /** rb_obj_equal
+     *
+     * Will use Java identity equality.
+     */
+    public IRubyObject equal_p(ThreadContext context, IRubyObject obj) {
+        return this == obj ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
+    }
+
+    /** rb_obj_equal
+     *
+     * Just like "==" and "equal?", "eql?" will use identity equality for Object.
+     */
+    public IRubyObject eql_p(IRubyObject obj) {
+        return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
+    }
+
+    public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
+        Ruby runtime = context.getRuntime();
+        if (this == other || this.callMethod(context, "==", other).isTrue()){
+            return RubyFixnum.zero(runtime);
+        }
+        return runtime.getNil();
+    }
+
+    /** rb_obj_init_copy
+     *
+     * Initializes this object as a copy of the original, that is the
+     * parameter to this object. Will make sure that the argument
+     * actually has the same real class as this object. It shouldn't
+     * be possible to initialize an object with something totally
+     * different.
+     */
+    public IRubyObject initialize_copy(IRubyObject original) {
+        if (this == original) {
+            return this;
+        }
+        checkFrozen();
+
+        if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
+            throw getRuntime().newTypeError("initialize_copy should take same class object");
+        }
+
+        return this;
+    }
+
+    /**
+     * The actual method that checks frozen with the default frozen message from MRI.
+     * If possible, call this instead of {@link #testFrozen}.
+     */
+    protected void checkFrozen() {
+        testFrozen();
+    }
+
+    /** obj_respond_to
+     *
+     * respond_to?( aSymbol, includePriv=false ) -> true or false
+     *
+     * Returns true if this object responds to the given method. Private
+     * methods are included in the search only if the optional second
+     * parameter evaluates to true.
+     *
+     * @return true if this responds to the given method
+     *
+     * !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
+     *
+     * Going back to splitting according to method arity. MRI is wrong
+     * about most of these anyway, and since we have arity splitting
+     * in both the compiler and the interpreter, the performance
+     * benefit is important for this method.
+     */
+    public RubyBoolean respond_to_p(IRubyObject mname) {
+        String name = mname.asJavaString();
+        return getRuntime().newBoolean(getMetaClass().isMethodBound(name, true));
+    }
+
+    public IRubyObject respond_to_p19(IRubyObject mname) {
+        String name = mname.asJavaString();
+        IRubyObject respond = getRuntime().newBoolean(getMetaClass().isMethodBound(name, true));
+        if (!respond.isTrue()) {
+            respond = callMethod("respond_to_missing?", mname, getRuntime().getFalse());
+            respond = getRuntime().newBoolean(respond.isTrue());
+        }
+        return respond;
+    }
+
+    /** obj_respond_to
+     *
+     * respond_to?( aSymbol, includePriv=false ) -> true or false
+     *
+     * Returns true if this object responds to the given method. Private
+     * methods are included in the search only if the optional second
+     * parameter evaluates to true.
+     *
+     * @return true if this responds to the given method
+     *
+     * !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
+     *
+     * Going back to splitting according to method arity. MRI is wrong
+     * about most of these anyway, and since we have arity splitting
+     * in both the compiler and the interpreter, the performance
+     * benefit is important for this method.
+     */
+    public RubyBoolean respond_to_p(IRubyObject mname, IRubyObject includePrivate) {
+        String name = mname.asJavaString();
+        return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate.isTrue()));
+    }
+
+    public IRubyObject respond_to_p19(IRubyObject mname, IRubyObject includePrivate) {
+        String name = mname.asJavaString();
+        IRubyObject respond = getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate.isTrue()));
+        if (!respond.isTrue()) {
+            respond = callMethod("respond_to_missing?", mname, includePrivate);
+            respond = getRuntime().newBoolean(respond.isTrue());
+        }
+        return respond;
+    }
+
+    /** rb_obj_id_obsolete
+     *
+     * Old id version. This one is bound to the "id" name and will emit a deprecation warning.
+     */
+    public IRubyObject id_deprecated() {
+        getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#id will be deprecated; use Object#object_id", "Object#id", "Object#object_id");
+        return id();
+    }
+
+    /** rb_obj_id
+     *
+     * Will return the hash code of this object. In comparison to MRI,
+     * this method will use the Java identity hash code instead of
+     * using rb_obj_id, since the usage of id in JRuby will incur the
+     * cost of some. ObjectSpace maintenance.
+     */
+    public RubyFixnum hash() {
+        return getRuntime().newFixnum(super.hashCode());
+    }
+
+    /** rb_obj_class
+     *
+     * Returns the real class of this object, excluding any
+     * singleton/meta class in the inheritance chain.
+     */
+    public RubyClass type() {
+        return getMetaClass().getRealClass();
+    }
+
+    /** rb_obj_type
+     *
+     * The deprecated version of type, that emits a deprecation
+     * warning.
+     */
+    public RubyClass type_deprecated() {
+        getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#type is deprecated; use Object#class", "Object#type", "Object#class");
+        return type();
+    }
+
+    /** rb_obj_display
+     *
+     *  call-seq:
+     *     obj.display(port=$>)    => nil
+     *
+     *  Prints <i>obj</i> on the given port (default <code>$></code>).
+     *  Equivalent to:
+     *
+     *     def display(port=$>)
+     *       port.write self
+     *     end
+     *
+     *  For example:
+     *
+     *     1.display
+     *     "cat".display
+     *     [ 4, 5, 6 ].display
+     *     puts
+     *
+     *  <em>produces:</em>
+     *
+     *     1cat456
+     *
+     */
+    public IRubyObject display(ThreadContext context, IRubyObject[] args) {
+        IRubyObject port = args.length == 0 ? context.getRuntime().getGlobalVariables().get("$>") : args[0];
+
+        port.callMethod(context, "write", this);
+
+        return context.getRuntime().getNil();
+    }
+
+    /** rb_obj_tainted
+     *
+     *  call-seq:
+     *     obj.tainted?    => true or false
+     *
+     *  Returns <code>true</code> if the object is tainted.
+     *
+     */
+    public RubyBoolean tainted_p(ThreadContext context) {
+        return context.getRuntime().newBoolean(isTaint());
+    }
+
+    /** rb_obj_taint
+     *
+     *  call-seq:
+     *     obj.taint -> obj
+     *
+     *  Marks <i>obj</i> as tainted---if the <code>$SAFE</code> level is
+     *  set appropriately, many method calls which might alter the running
+     *  programs environment will refuse to accept tainted strings.
+     */
+    public IRubyObject taint(ThreadContext context) {
+        taint(context.getRuntime());
+        return this;
+    }
+
+    /** rb_obj_untaint
+     *
+     *  call-seq:
+     *     obj.untaint    => obj
+     *
+     *  Removes the taint from <i>obj</i>.
+     *
+     *  Only callable in if more secure than 3.
+     */
+    public IRubyObject untaint(ThreadContext context) {
+        context.getRuntime().secure(3);
+
+        if (isTaint()) {
+            testFrozen();
+            setTaint(false);
+        }
+
+        return this;
+    }
+
+    /** rb_obj_freeze
+     *
+     *  call-seq:
+     *     obj.freeze    => obj
+     *
+     *  Prevents further modifications to <i>obj</i>. A
+     *  <code>TypeError</code> will be raised if modification is attempted.
+     *  There is no way to unfreeze a frozen object. See also
+     *  <code>Object#frozen?</code>.
+     *
+     *     a = [ "a", "b", "c" ]
+     *     a.freeze
+     *     a << "z"
+     *
+     *  <em>produces:</em>
+     *
+     *     prog.rb:3:in `<<': can't modify frozen array (TypeError)
+     *     	from prog.rb:3
+     */
+    public IRubyObject freeze(ThreadContext context) {
+        Ruby runtime = context.getRuntime();
+        if ((flags & FROZEN_F) == 0 && (runtime.is1_9() || !isImmediate())) {
+            if (runtime.getSafeLevel() >= 4 && !isTaint()) throw runtime.newSecurityError("Insecure: can't freeze object");
+            flags |= FROZEN_F;
+        }
+        return this;
+    }
+
+    /** rb_obj_frozen_p
+     *
+     *  call-seq:
+     *     obj.frozen?    => true or false
+     *
+     *  Returns the freeze status of <i>obj</i>.
+     *
+     *     a = [ "a", "b", "c" ]
+     *     a.freeze    #=> ["a", "b", "c"]
+     *     a.frozen?   #=> true
+     */
+    public RubyBoolean frozen_p(ThreadContext context) {
+        return context.getRuntime().newBoolean(isFrozen());
+    }
+
+    /** rb_obj_untrusted
+     *  call-seq:
+     *     obj.untrusted?    => true or false
+     *
+     *  Returns <code>true</code> if the object is untrusted.
+     */
+    public RubyBoolean untrusted_p(ThreadContext context) {
+        return context.getRuntime().newBoolean(isUntrusted());
+    }
+
+    /** rb_obj_untrust
+     *  call-seq:
+     *     obj.untrust -> obj
+     *
+     *  Marks <i>obj</i> as untrusted.
+     */
+    public IRubyObject untrust(ThreadContext context) {
+        if (!isUntrusted() && !isImmediate()) {
+            checkFrozen();
+            flags |= UNTRUSTED_F;
+        }
+        return this;
+    }
+
+    /** rb_obj_trust
+     *  call-seq:
+     *     obj.trust    => obj
+     *
+     *  Removes the untrusted mark from <i>obj</i>.
+     */
+    public IRubyObject trust(ThreadContext context) {
+        if (isUntrusted() && !isImmediate()) {
+            checkFrozen();
+            flags &= ~UNTRUSTED_F;
+        }
+        return this;
+    }
+
+    /** rb_obj_is_instance_of
+     *
+     *  call-seq:
+     *     obj.instance_of?(class)    => true or false
+     *
+     *  Returns <code>true</code> if <i>obj</i> is an instance of the given
+     *  class. See also <code>Object#kind_of?</code>.
+     */
+    public RubyBoolean instance_of_p(ThreadContext context, IRubyObject type) {
+        if (type() == type) {
+            return context.getRuntime().getTrue();
+        } else if (!(type instanceof RubyModule)) {
+            throw context.getRuntime().newTypeError("class or module required");
+        } else {
+            return context.getRuntime().getFalse();
+        }
+    }
+
+
+    /** rb_obj_is_kind_of
+     *
+     *  call-seq:
+     *     obj.is_a?(class)       => true or false
+     *     obj.kind_of?(class)    => true or false
+     *
+     *  Returns <code>true</code> if <i>class</i> is the class of
+     *  <i>obj</i>, or if <i>class</i> is one of the superclasses of
+     *  <i>obj</i> or modules included in <i>obj</i>.
+     *
+     *     module M;    end
+     *     class A
+     *       include M
+     *     end
+     *     class B < A; end
+     *     class C < B; end
+     *     b = B.new
+     *     b.instance_of? A   #=> false
+     *     b.instance_of? B   #=> true
+     *     b.instance_of? C   #=> false
+     *     b.instance_of? M   #=> false
+     *     b.kind_of? A       #=> true
+     *     b.kind_of? B       #=> true
+     *     b.kind_of? C       #=> false
+     *     b.kind_of? M       #=> true
+     */
+    public RubyBoolean kind_of_p(ThreadContext context, IRubyObject type) {
+        // TODO: Generalize this type-checking code into IRubyObject helper.
+        if (!(type instanceof RubyModule)) {
+            // TODO: newTypeError does not offer enough for ruby error string...
+            throw context.getRuntime().newTypeError("class or module required");
+        }
+
+        return context.getRuntime().newBoolean(((RubyModule)type).isInstance(this));
+    }
+
+    /** rb_obj_methods
+     *
+     *  call-seq:
+     *     obj.methods    => array
+     *
+     *  Returns a list of the names of methods publicly accessible in
+     *  <i>obj</i>. This will include all the methods accessible in
+     *  <i>obj</i>'s ancestors.
+     *
+     *     class Klass
+     *       def kMethod()
+     *       end
+     *     end
+     *     k = Klass.new
+     *     k.methods[0..9]    #=> ["kMethod", "freeze", "nil?", "is_a?",
+     *                             "class", "instance_variable_set",
+     *                              "methods", "extend", "__send__", "instance_eval"]
+     *     k.methods.length   #=> 42
+     */
+    public IRubyObject methods(ThreadContext context, IRubyObject[] args) {
+        return methods(context, args, false);
+    }
+    public IRubyObject methods19(ThreadContext context, IRubyObject[] args) {
+        return methods(context, args, true);
+    }
+
+    public IRubyObject methods(ThreadContext context, IRubyObject[] args, boolean useSymbols) {
+        boolean all = args.length == 1 ? args[0].isTrue() : true;
+        Ruby runtime = getRuntime();
+        RubyArray methods = runtime.newArray();
+        Set<String> seen = new HashSet<String>();
+
+        if (getMetaClass().isSingleton()) {
+            getMetaClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, false);
+            if (all) {
+                getMetaClass().getSuperClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, true);
+            }
+        } else if (all) {
+            getMetaClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, true);
+        } else {
+            // do nothing, leave empty
+        }
+
+        return methods;
+    }
+
+    /** rb_obj_public_methods
+     *
+     *  call-seq:
+     *     obj.public_methods(all=true)   => array
+     *
+     *  Returns the list of public methods accessible to <i>obj</i>. If
+     *  the <i>all</i> parameter is set to <code>false</code>, only those methods
+     *  in the receiver will be listed.
+     */
+    public IRubyObject public_methods(ThreadContext context, IRubyObject[] args) {
+        return getMetaClass().public_instance_methods(trueIfNoArgument(context, args));
+    }
+
+    public IRubyObject public_methods19(ThreadContext context, IRubyObject[] args) {
+        return getMetaClass().public_instance_methods19(trueIfNoArgument(context, args));
+    }
+
+    /** rb_obj_protected_methods
+     *
+     *  call-seq:
+     *     obj.protected_methods(all=true)   => array
+     *
+     *  Returns the list of protected methods accessible to <i>obj</i>. If
+     *  the <i>all</i> parameter is set to <code>false</code>, only those methods
+     *  in the receiver will be listed.
+     *
+     *  Internally this implementation uses the
+     *  {@link RubyModule#protected_instance_methods} method.
+     */
+    public IRubyObject protected_methods(ThreadContext context, IRubyObject[] args) {
+        return getMetaClass().protected_instance_methods(trueIfNoArgument(context, args));
+    }
+
+    public IRubyObject protected_methods19(ThreadContext context, IRubyObject[] args) {
+        return getMetaClass().protected_instance_methods19(trueIfNoArgument(context, args));
+    }
+
+    /** rb_obj_private_methods
+     *
+     *  call-seq:
+     *     obj.private_methods(all=true)   => array
+     *
+     *  Returns the list of private methods accessible to <i>obj</i>. If
+     *  the <i>all</i> parameter is set to <code>false</code>, only those methods
+     *  in the receiver will be listed.
+     *
+     *  Internally this implementation uses the
+     *  {@link RubyModule#private_instance_methods} method.
+     */
+    public IRubyObject private_methods(ThreadContext context, IRubyObject[] args) {
+        return getMetaClass().private_instance_methods(trueIfNoArgument(context, args));
+    }
+
+    public IRubyObject private_methods19(ThreadContext context, IRubyObject[] args) {
+        return getMetaClass().private_instance_methods19(trueIfNoArgument(context, args));
+    }
+
+    // FIXME: If true array is common enough we should pre-allocate and stick somewhere
+    private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
+        return args.length == 0 ? new IRubyObject[] { context.getRuntime().getTrue() } : args;
+    }
+
+    /** rb_obj_singleton_methods
+     *
+     *  call-seq:
+     *     obj.singleton_methods(all=true)    => array
+     *
+     *  Returns an array of the names of singleton methods for <i>obj</i>.
+     *  If the optional <i>all</i> parameter is true, the list will include
+     *  methods in modules included in <i>obj</i>.
+     *
+     *     module Other
+     *       def three() end
+     *     end
+     *
+     *     class Single
+     *       def Single.four() end
+     *     end
+     *
+     *     a = Single.new
+     *
+     *     def a.one()
+     *     end
+     *
+     *     class << a
+     *       include Other
+     *       def two()
+     *       end
+     *     end
+     *
+     *     Single.singleton_methods    #=> ["four"]
+     *     a.singleton_methods(false)  #=> ["two", "one"]
+     *     a.singleton_methods         #=> ["two", "one", "three"]
+     */
+    // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
+    public RubyArray singleton_methods(ThreadContext context, IRubyObject[] args) {
+        return singletonMethods(context, args, methodsCollector);
+    }
+
+    public RubyArray singleton_methods19(ThreadContext context, IRubyObject[] args) {
+        return singletonMethods(context, args, methodsCollector19);
+    }
+
+    private RubyArray singletonMethods(ThreadContext context, IRubyObject[] args, MethodsCollector collect) {
+        boolean all = true;
+        if(args.length == 1) {
+            all = args[0].isTrue();
+        }
+
+        RubyArray singletonMethods;
+        if (getMetaClass().isSingleton()) {
+            IRubyObject[] methodsArgs = new IRubyObject[]{context.getRuntime().getFalse()};
+            singletonMethods = collect.instanceMethods(getMetaClass(), methodsArgs);
+
+            if (all) {
+                RubyClass superClass = getMetaClass().getSuperClass();
+                while (superClass.isSingleton() || superClass.isIncluded()) {
+                    singletonMethods.concat(collect.instanceMethods(superClass, methodsArgs));
+                    superClass = superClass.getSuperClass();
+                }
+            }
+        } else {
+            singletonMethods = context.getRuntime().newEmptyArray();
+        }
+
+        return singletonMethods;
+    }
+
+    private abstract static class MethodsCollector {
+        public abstract RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args);
+    };
+
+    private static final MethodsCollector methodsCollector = new MethodsCollector() {
+        public RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args) {
+            return rubyClass.instance_methods(args);
+        }
+    };
+
+    private static final MethodsCollector methodsCollector19 = new MethodsCollector() {
+        public RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args) {
+            return rubyClass.instance_methods19(args);
+        }
+    };
+
+    /** rb_obj_method
+     *
+     *  call-seq:
+     *     obj.method(sym)    => method
+     *
+     *  Looks up the named method as a receiver in <i>obj</i>, returning a
+     *  <code>Method</code> object (or raising <code>NameError</code>). The
+     *  <code>Method</code> object acts as a closure in <i>obj</i>'s object
+     *  instance, so instance variables and the value of <code>self</code>
+     *  remain available.
+     *
+     *     class Demo
+     *       def initialize(n)
+     *         @iv = n
+     *       end
+     *       def hello()
+     *         "Hello, @iv = #{@iv}"
+     *       end
+     *     end
+     *
+     *     k = Demo.new(99)
+     *     m = k.method(:hello)
+     *     m.call   #=> "Hello, @iv = 99"
+     *
+     *     l = Demo.new('Fred')
+     *     m = l.method("hello")
+     *     m.call   #=> "Hello, @iv = Fred"
+     */
+    public IRubyObject method(IRubyObject symbol) {
+        return getMetaClass().newMethod(this, symbol.asJavaString(), true, null);
+    }
+
+    public IRubyObject method19(IRubyObject symbol) {
+        return getMetaClass().newMethod(this, symbol.asJavaString(), true, null, true);
+    }
+
+    /** rb_any_to_s
+     *
+     *  call-seq:
+     *     obj.to_s    => string
+     *
+     *  Returns a string representing <i>obj</i>. The default
+     *  <code>to_s</code> prints the object's class and an encoding of the
+     *  object id. As a special case, the top-level object that is the
+     *  initial execution context of Ruby programs returns ``main.''
+     */
+    public IRubyObject to_s() {
+    	return anyToString();
+    }
+
+    /** rb_any_to_a
+     *
+     *  call-seq:
+     *     obj.to_a -> anArray
+     *
+     *  Returns an array representation of <i>obj</i>. For objects of class
+     *  <code>Object</code> and others that don't explicitly override the
+     *  method, the return value is an array containing <code>self</code>.
+     *  However, this latter behavior will soon be obsolete.
+     *
+     *     self.to_a       #=> -:1: warning: default `to_a' will be obsolete
+     *     "hello".to_a    #=> ["hello"]
+     *     Time.new.to_a   #=> [39, 54, 8, 9, 4, 2003, 3, 99, true, "CDT"]
+     *
+     *  The default to_a method is deprecated.
+     */
+    public RubyArray to_a() {
+        getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "default 'to_a' will be obsolete", "to_a");
+        return getRuntime().newArray(this);
+    }
+
+    /** rb_obj_instance_eval
+     *
+     *  call-seq:
+     *     obj.instance_eval(string [, filename [, lineno]] )   => obj
+     *     obj.instance_eval {| | block }                       => obj
+     *
+     *  Evaluates a string containing Ruby source code, or the given block,
+     *  within the context of the receiver (_obj_). In order to set the
+     *  context, the variable +self+ is set to _obj_ while
+     *  the code is executing, giving the code access to _obj_'s
+     *  instance variables. In the version of <code>instance_eval</code>
+     *  that takes a +String+, the optional second and third
+     *  parameters supply a filename and starting line number that are used
+     *  when reporting compilation errors.
+     *
+     *     class Klass
+     *       def initialize
+     *         @secret = 99
+     *       end
+     *     end
+     *     k = Klass.new
+     *     k.instance_eval { @secret }   #=> 99
+     */
+    public IRubyObject instance_eval(ThreadContext context, Block block) {
+        return specificEval(context, getInstanceEvalClass(), block);
+    }
+    public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, Block block) {
+        return specificEval(context, getInstanceEvalClass(), arg0, block);
+    }
+    public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
+        return specificEval(context, getInstanceEvalClass(), arg0, arg1, block);
+    }
+    public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
+        return specificEval(context, getInstanceEvalClass(), arg0, arg1, arg2, block);
+    }
+
+    /** rb_obj_instance_exec
+     *
+     *  call-seq:
+     *     obj.instance_exec(arg...) {|var...| block }                       => obj
+     *
+     *  Executes the given block within the context of the receiver
+     *  (_obj_). In order to set the context, the variable +self+ is set
+     *  to _obj_ while the code is executing, giving the code access to
+     *  _obj_'s instance variables.  Arguments are passed as block parameters.
+     *
+     *     class Klass
+     *       def initialize
+     *         @secret = 99
+     *       end
+     *     end
+     *     k = Klass.new
+     *     k.instance_exec(5) {|x| @secret+x }   #=> 104
+     */
+    public IRubyObject instance_exec(ThreadContext context, IRubyObject[] args, Block block) {
+        if (!block.isGiven()) throw context.getRuntime().newArgumentError("block not supplied");
+
+        RubyModule klazz;
+        if (isImmediate()) {
+            // Ruby uses Qnil here, we use "dummy" because we need a class
+            klazz = context.getRuntime().getDummy();
+        } else {
+            klazz = getSingletonClass();
+        }
+
+        return yieldUnder(context, klazz, args, block);
+    }
+
+    /** rb_obj_extend
+     *
+     *  call-seq:
+     *     obj.extend(module, ...)    => obj
+     *
+     *  Adds to _obj_ the instance methods from each module given as a
+     *  parameter.
+     *
+     *     module Mod
+     *       def hello
+     *         "Hello from Mod.\n"
+     *       end
+     *     end
+     *
+     *     class Klass
+     *       def hello
+     *         "Hello from Klass.\n"
+     *       end
+     *     end
+     *
+     *     k = Klass.new
+     *     k.hello         #=> "Hello from Klass.\n"
+     *     k.extend(Mod)   #=> #<Klass:0x401b3bc8>
+     *     k.hello         #=> "Hello from Mod.\n"
+     */
+    public IRubyObject extend(IRubyObject[] args) {
+        Ruby runtime = getRuntime();
+
+        // Make sure all arguments are modules before calling the callbacks
+        for (int i = 0; i < args.length; i++) {
+            if (!args[i].isModule()) throw runtime.newTypeError(args[i], runtime.getModule());
+        }
+
+        ThreadContext context = runtime.getCurrentContext();
+
+        // MRI extends in order from last to first
+        for (int i = args.length - 1; i >= 0; i--) {
+            args[i].callMethod(context, "extend_object", this);
+            args[i].callMethod(context, "extended", this);
+        }
+        return this;
+    }
+
+    /** rb_f_send
+     *
+     * send( aSymbol  [, args  ]*   ) -> anObject
+     *
+     * Invokes the method identified by aSymbol, passing it any arguments
+     * specified. You can use __send__ if the name send clashes with an
+     * existing method in this object.
+     *
+     * <pre>
+     * class Klass
+     *   def hello(*args)
+     *     "Hello " + args.join(' ')
+     *   end
+     * end
+     *
+     * k = Klass.new
+     * k.send :hello, "gentle", "readers"
+     * </pre>
+     *
+     * @return the result of invoking the method identified by aSymbol.
+     */
+    public IRubyObject send(ThreadContext context, Block block) {
+        throw context.getRuntime().newArgumentError(0, 1);
+    }
+    public IRubyObject send(ThreadContext context, IRubyObject arg0, Block block) {
+        String name = arg0.asJavaString();
+
+        return getMetaClass().finvoke(context, this, name, block);
+    }
+    public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
+        String name = arg0.asJavaString();
+
+        return getMetaClass().finvoke(context, this, name, arg1, block);
+    }
+    public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
+        String name = arg0.asJavaString();
+
+        return getMetaClass().finvoke(context, this, name, arg1, arg2, block);
+    }
+    public IRubyObject send(ThreadContext context, IRubyObject[] args, Block block) {
+        String name = args[0].asJavaString();
+        int newArgsLength = args.length - 1;
+
+        IRubyObject[] newArgs;
+        if (newArgsLength == 0) {
+            newArgs = IRubyObject.NULL_ARRAY;
+        } else {
+            newArgs = new IRubyObject[newArgsLength];
+            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
+        }
+
+        return getMetaClass().finvoke(context, this, name, newArgs, block);
+    }
+
+    /** rb_false
+     *
+     * call_seq:
+     *   nil.nil?               => true
+     *   <anything_else>.nil?   => false
+     *
+     * Only the object <i>nil</i> responds <code>true</code> to <code>nil?</code>.
+     */
+    public IRubyObject nil_p(ThreadContext context) {
+    	return context.getRuntime().getFalse();
+    }
+
+    /** rb_obj_pattern_match
+     *
+     *  call-seq:
+     *     obj =~ other  => false
+     *
+     *  Pattern Match---Overridden by descendents (notably
+     *  <code>Regexp</code> and <code>String</code>) to provide meaningful
+     *  pattern-match semantics.
+     */
+    public IRubyObject op_match(ThreadContext context, IRubyObject arg) {
+    	return context.getRuntime().getFalse();
+    }
+
+    public IRubyObject op_match19(ThreadContext context, IRubyObject arg) {
+    	return context.getRuntime().getNil();
+    }
+
+    public IRubyObject op_not_match(ThreadContext context, IRubyObject arg) {
+        return context.getRuntime().newBoolean(! callMethod(context, "=~", arg).isTrue());
+    }
+
+
+    //
+    // INSTANCE VARIABLE RUBY METHODS
+    //
+
+    /** rb_obj_ivar_defined
+     *
+     *  call-seq:
+     *     obj.instance_variable_defined?(symbol)    => true or false
+     *
+     *  Returns <code>true</code> if the given instance variable is
+     *  defined in <i>obj</i>.
+     *
+     *     class Fred
+     *       def initialize(p1, p2)
+     *         @a, @b = p1, p2
+     *       end
+     *     end
+     *     fred = Fred.new('cat', 99)
+     *     fred.instance_variable_defined?(:@a)    #=> true
+     *     fred.instance_variable_defined?("@b")   #=> true
+     *     fred.instance_variable_defined?("@c")   #=> false
+     */
+    public IRubyObject instance_variable_defined_p(ThreadContext context, IRubyObject name) {
+        if (variableTableContains(validateInstanceVariable(name.asJavaString()))) {
+            return context.getRuntime().getTrue();
+        }
+        return context.getRuntime().getFalse();
+    }
+
+    /** rb_obj_ivar_get
+     *
+     *  call-seq:
+     *     obj.instance_variable_get(symbol)    => obj
+     *
+     *  Returns the value of the given instance variable, or nil if the
+     *  instance variable is not set. The <code>@</code> part of the
+     *  variable name should be included for regular instance
+     *  variables. Throws a <code>NameError</code> exception if the
+     *  supplied symbol is not valid as an instance variable name.
+     *
+     *     class Fred
+     *       def initialize(p1, p2)
+     *         @a, @b = p1, p2
+     *       end
+     *     end
+     *     fred = Fred.new('cat', 99)
+     *     fred.instance_variable_get(:@a)    #=> "cat"
+     *     fred.instance_variable_get("@b")   #=> 99
+     */
+    public IRubyObject instance_variable_get(ThreadContext context, IRubyObject name) {
+        Object value;
+        if ((value = variableTableFetch(validateInstanceVariable(name.asJavaString()))) != null) {
+            return (IRubyObject)value;
+        }
+        return context.getRuntime().getNil();
+    }
+
+    /** rb_obj_ivar_set
+     *
+     *  call-seq:
+     *     obj.instance_variable_set(symbol, obj)    => obj
+     *
+     *  Sets the instance variable names by <i>symbol</i> to
+     *  <i>object</i>, thereby frustrating the efforts of the class's
+     *  author to attempt to provide proper encapsulation. The variable
+     *  did not have to exist prior to this call.
+     *
+     *     class Fred
+     *       def initialize(p1, p2)
+     *         @a, @b = p1, p2
+     *       end
+     *     end
+     *     fred = Fred.new('cat', 99)
+     *     fred.instance_variable_set(:@a, 'dog')   #=> "dog"
+     *     fred.instance_variable_set(:@c, 'cat')   #=> "cat"
+     *     fred.inspect                             #=> "#<Fred:0x401b3da8 @a=\"dog\", @b=99, @c=\"cat\">"
+     */
+    public IRubyObject instance_variable_set(IRubyObject name, IRubyObject value) {
+        ensureInstanceVariablesSettable();
+        return (IRubyObject)variableTableStore(validateInstanceVariable(name.asJavaString()), value);
+    }
+
+    /** rb_obj_remove_instance_variable
+     *
+     *  call-seq:
+     *     obj.remove_instance_variable(symbol)    => obj
+     *
+     *  Removes the named instance variable from <i>obj</i>, returning that
+     *  variable's value.
+     *
+     *     class Dummy
+     *       attr_reader :var
+     *       def initialize
+     *         @var = 99
+     *       end
+     *       def remove
+     *         remove_instance_variable(:@var)
+     *       end
+     *     end
+     *     d = Dummy.new
+     *     d.var      #=> 99
+     *     d.remove   #=> 99
+     *     d.var      #=> nil
+     */
+    public IRubyObject remove_instance_variable(ThreadContext context, IRubyObject name, Block block) {
+        ensureInstanceVariablesSettable();
+        IRubyObject value;
+        if ((value = (IRubyObject)variableTableRemove(validateInstanceVariable(name.asJavaString()))) != null) {
+            return value;
+        }
+        throw context.getRuntime().newNameError("instance variable " + name.asJavaString() + " not defined", name.asJavaString());
+    }
+
+    /** rb_obj_instance_variables
+     *
+     *  call-seq:
+     *     obj.instance_variables    => array
+     *
+     *  Returns an array of instance variable names for the receiver. Note
+     *  that simply defining an accessor does not create the corresponding
+     *  instance variable.
+     *
+     *     class Fred
+     *       attr_accessor :a1
+     *       def initialize
+     *         @iv = 3
+     *       end
+     *     end
+     *     Fred.new.instance_variables   #=> ["@iv"]
+     */
+    public RubyArray instance_variables(ThreadContext context) {
+        Ruby runtime = context.getRuntime();
+        List<String> nameList = getInstanceVariableNameList();
+
+        RubyArray array = runtime.newArray(nameList.size());
+
+        for (String name : nameList) {
+            array.append(runtime.newString(name));
+        }
+
+        return array;
+    }
+
+    // In 1.9, return symbols
+    public RubyArray instance_variables19(ThreadContext context) {
+        Ruby runtime = context.getRuntime();
+        List<String> nameList = getInstanceVariableNameList();
+
+        RubyArray array = runtime.newArray(nameList.size());
+
+        for (String name : nameList) {
+            array.append(runtime.newSymbol(name));
+        }
+
+        return array;
+    }
+
+    /**
+     * Checks if the name parameter represents a legal instance variable name, and otherwise throws a Ruby NameError
+     */
+    protected String validateInstanceVariable(String name) {
+        if (IdUtil.isValidInstanceVariableName(name)) return name;
+
+        throw getRuntime().newNameError("`" + name + "' is not allowable as an instance variable name", name);
+    }
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 6277913d13..ed7e7ab69d 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1821 +1,2157 @@
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
  * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
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
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Random;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.*;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodNBlock;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  */
 @JRubyModule(name="Kernel")
 public class RubyKernel {
     public final static Class<?> IRUBY_OBJECT = IRubyObject.class;
 
     public static abstract class MethodMissingMethod extends JavaMethodNBlock {
         public MethodMissingMethod(RubyModule implementationClass) {
             super(implementationClass, Visibility.PRIVATE, CallConfiguration.FrameFullScopeNone);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             try {
                 preFrameOnly(context, self, name, block);
                 return methodMissing(context, self, clazz, name, args, block);
             } finally {
                 postFrameOnly(context);
             }
         }
 
         public abstract IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block);
 
     }
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
 
         module.defineAnnotatedMethods(RubyKernel.class);
-        module.defineAnnotatedMethods(RubyObject.class);
         
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
         
         module.setFlag(RubyObject.USER7_F, false); //Kernel is the only Module that doesn't need an implementor
 
         runtime.setPrivateMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PRIVATE, CallType.NORMAL, args, block);
             }
         });
 
         runtime.setProtectedMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PROTECTED, CallType.NORMAL, args, block);
             }
         });
 
         runtime.setVariableMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.VARIABLE, args, block);
             }
         });
 
         runtime.setSuperMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.SUPER, args, block);
             }
         });
 
         runtime.setNormalMethodMissing(new MethodMissingMethod(module) {
             @Override
             public IRubyObject methodMissing(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 return RubyKernel.methodMissing(context, self, name, PUBLIC, CallType.NORMAL, args, block);
             }
         });
 
         if (!runtime.is1_9()) { // method_missing is in BasicObject in 1.9
             runtime.setDefaultMethodMissing(module.searchMethod("method_missing"));
         }
 
         return module;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject at_exit(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().pushExitBlock(context.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject autoload_p(ThreadContext context, final IRubyObject recv, IRubyObject symbol) {
         Ruby runtime = context.getRuntime();
         final RubyModule module = getModuleForAutoload(runtime, recv);
         String name = module.getName() + "::" + symbol.asJavaString();
         
         IAutoloadMethod autoloadMethod = runtime.getLoadService().autoloadFor(name);
         if (autoloadMethod == null) return runtime.getNil();
 
         return runtime.newString(autoloadMethod.file());
     }
 
     @JRubyMethod(required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         Ruby runtime = recv.getRuntime(); 
         final LoadService loadService = runtime.getLoadService();
         String nonInternedName = symbol.asJavaString();
         
         if (!IdUtil.isValidConstantName(nonInternedName)) {
             throw runtime.newNameError("autoload must be constant name", nonInternedName);
         }
 
         if (!runtime.is1_9() && !(file instanceof RubyString)) throw runtime.newTypeError(file, runtime.getString());
 
         RubyString fileString = RubyFile.get_path(runtime.getCurrentContext(), file);
         
         if (fileString.isEmpty()) throw runtime.newArgumentError("empty file name");
         
         final String baseName = symbol.asJavaString().intern(); // interned, OK for "fast" methods
         final RubyModule module = getModuleForAutoload(runtime, recv);
         String nm = module.getName() + "::" + baseName;
         
         IRubyObject existingValue = module.fastFetchConstant(baseName); 
         if (existingValue != null && existingValue != RubyObject.UNDEF) return runtime.getNil();
 
         module.fastStoreConstant(baseName, RubyObject.UNDEF);
 
         loadService.addAutoload(nm, new IAutoloadMethod() {
             public String file() {
                 return file.toString();
             }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 boolean required = loadService.require(file());
                 
                 // File to be loaded by autoload has already been or is being loaded.
                 if (!required) return null;
                 
                 return module.fastGetConstant(baseName);
             }
         });
         return runtime.getNil();
     }
 
     private static RubyModule getModuleForAutoload(Ruby runtime, IRubyObject recv) {
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         if (module == runtime.getKernel()) {
             // special behavior if calling Kernel.autoload directly
             if (runtime.is1_9()) {
                 module = runtime.getObject().getSingletonClass();
             } else {
                 module = runtime.getObject();
             }
         }
         return module;
     }
 
     @JRubyMethod(rest = true, frame = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject method_missing(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw context.getRuntime().newArgumentError("no id given");
 
         return methodMissingDirect(context, recv, (RubySymbol)args[0], lastVis, lastCallType, args, block);
     }
 
     protected static IRubyObject methodMissingDirect(ThreadContext context, IRubyObject recv, RubySymbol symbol, Visibility lastVis, CallType lastCallType, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime,
                                                                  recv,
                                                                  symbol,
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, symbol, RubyArray.newArrayNoCopy(runtime, args, 1)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, symbol};
         }
 
         throw new RaiseException((RubyException)exc.newInstance(context, exArgs, Block.NULL_BLOCK));
     }
 
     private static IRubyObject methodMissing(ThreadContext context, IRubyObject recv, String name, Visibility lastVis, CallType lastCallType, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         // TODO: pass this in?
         RubySymbol symbol = runtime.newSymbol(name);
 
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime,
                                                                  recv,
                                                                  symbol,
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, symbol, RubyArray.newArrayNoCopy(runtime, args)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, symbol};
         }
 
         RaiseException exception = new RaiseException((RubyException)exc.newInstance(context, exArgs, Block.NULL_BLOCK));
         exception.preRaise(context);
         throw exception;
     }
 
     @JRubyMethod(required = 1, optional = 2, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
         Ruby runtime = context.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             return RubyIO.popen(context, runtime.getIO(), new IRubyObject[] {runtime.newString(command)}, block);
         } 
 
         return RubyFile.open(context, runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject open19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         if (args[0].respondsTo("to_open")) {
             args[0] = args[0].callMethod(context, "to_open");
             return RubyFile.open(context, runtime.getFile(), args, block);
         } else {
             return open(context, recv, args, block);
         }
     }
 
     @JRubyMethod(name = "getc", module = true, visibility = PRIVATE)
     public static IRubyObject getc(ThreadContext context, IRubyObject recv) {
         context.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "getc is obsolete; use STDIN.getc instead", "getc", "STDIN.getc");
         IRubyObject defin = context.getRuntime().getGlobalVariables().get("$stdin");
         return defin.callMethod(context, "getc");
     }
 
     @JRubyMethod(name = "gets", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gets(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.gets(context, context.getRuntime().getArgsFile(), args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         if(args.length == 1) {
             runtime.getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0]);
         }
         
         exit(runtime, new IRubyObject[] { runtime.getFalse() }, false);
         return runtime.getNil(); // not reached
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_array(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return RuntimeHelpers.arrayValue(context, context.getRuntime(), object);
     }
 
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert");
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg);
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg0, arg1);
     }
     
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert");
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg);
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg0, arg1);
     }
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyFloat new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return (RubyFloat)object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString) object).getByteList().getRealSize() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = (RubyFloat)TypeConverter.convertToType(object, recv.getRuntime().getFloat(), "to_f");
             if (Double.isNaN(rFloat.getDoubleValue())) throw recv.getRuntime().newArgumentError("invalid value for Float()");
             return rFloat;
         }
     }
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyFloat new_float19(IRubyObject recv, IRubyObject object) {
         Ruby runtime = recv.getRuntime();
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(runtime, ((RubyFixnum)object).getDoubleValue());
         } else if (object instanceof RubyFloat) {
             return (RubyFloat)object;
         } else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(runtime, RubyBignum.big2dbl((RubyBignum)object));
         } else if(object instanceof RubyString){
             if(((RubyString) object).getByteList().getRealSize() == 0){ // rb_cstr_to_dbl case
                 throw runtime.newArgumentError("invalid value for Float(): " + object.inspect());
             }
             RubyString arg = (RubyString)object;
             if (arg.toString().startsWith("0x")) {
                 return ConvertBytes.byteListToInum19(runtime, arg.getByteList(), 16, true).toFloat();
             }
             return RubyNumeric.str2fnum19(runtime, arg,true);
         } else if(object.isNil()){
             throw runtime.newTypeError("can't convert nil into Float");
         } else {
             return (RubyFloat)TypeConverter.convertToType19(object, runtime.getFloat(), "to_f");
         }
     }
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject new_integer(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue();
             if (val >= (double) RubyFixnum.MAX || val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,0,true);
         }
 
         IRubyObject tmp = TypeConverter.convertToType(object, context.getRuntime().getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_integer19(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,0,true);
         } else if(object instanceof RubyNil) {
             throw context.getRuntime().newTypeError("can't convert nil into Integer");
         }
         
         IRubyObject tmp = TypeConverter.convertToType(object, context.getRuntime().getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "Integer", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_integer19(ThreadContext context, IRubyObject recv, IRubyObject object, IRubyObject base) {
         int bs = RubyNumeric.num2int(base);
         if(object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,bs,true);
         } else {
             IRubyObject tmp = object.checkStringType();
             if(!tmp.isNil()) {
                 return RubyNumeric.str2inum(context.getRuntime(),(RubyString)tmp,bs,true);
             }
         }
         throw context.getRuntime().newArgumentError("base specified for non string value");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject new_string(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject new_string19(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType19(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "p", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject p(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject defout = runtime.getGlobalVariables().get("$>");
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", RubyObject.inspect(context, args[i]));
                 defout.callMethod(context, "write", runtime.newString("\n"));
             }
         }
 
         IRubyObject result = runtime.getNil();
         if (runtime.is1_9()) {
             if (args.length == 1) {
                 result = args[0];
             } else if (args.length > 1) {
                 result = runtime.newArray(args);
             }
         }
 
         if (defout instanceof RubyFile) {
             ((RubyFile)defout).flush();
         }
 
         return result;
     }
 
     @JRubyMethod(name = "public_method",required = 1, module = true, compat = RUBY1_9)
     public static IRubyObject public_method(ThreadContext context, IRubyObject recv, IRubyObject symbol) {
         return recv.getMetaClass().newMethod(recv, symbol.asJavaString(), true, PUBLIC, true, false);
     }
 
     /** rb_f_putc
      */
     @JRubyMethod(name = "putc", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject putc(ThreadContext context, IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         
         return RubyIO.putc(context, defout, ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject puts(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         return RubyIO.puts(context, defout, args);
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject print(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         return RubyIO.print(context, defout, args);
     }
 
     @JRubyMethod(name = "printf", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject printf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readline", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readline(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(context, recv, args);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.readlines(context, context.getRuntime().getArgsFile(), args);
     }
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = RUBY1_9)
     public static IRubyObject respond_to_missing_p(ThreadContext context, IRubyObject recv, IRubyObject symbol) {
         return context.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "respond_to_missing?", module = true, compat = RUBY1_9)
     public static IRubyObject respond_to_missing_p(ThreadContext context, IRubyObject recv, IRubyObject symbol, IRubyObject isPrivate) {
         return context.getRuntime().getFalse();
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(ThreadContext context, Ruby runtime) {
         IRubyObject line = context.getCurrentScope().getLastLine(runtime);
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, args, block);
     }
 
     @JRubyMethod(name = "sub!", module = true, visibility = PRIVATE, reads = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "sub!", module = true, visibility = PRIVATE, reads = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, arg1, block);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, args, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, args, block);
     }
 
     @JRubyMethod(name = "gsub!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "gsub!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, arg1, block);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, args, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, arg1, block).isNil()) {
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chop_bang(ThreadContext context, IRubyObject recv, Block block) {
         return getLastlineString(context, context.getRuntime()).chop_bang(context);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chop(ThreadContext context, IRubyObject recv, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
 
         if (str.getByteList().getRealSize() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang(context);
             context.getCurrentScope().setLastLine(str);
         }
 
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the zero or one-arg versions.
      */
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(args);
     }
 
     @JRubyMethod(name = "chomp!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context);
     }
 
     @JRubyMethod(name = "chomp!", module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context, arg0);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE, compat = RUBY1_8)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context, arg0).isNil()) {
             return str;
         } 
 
         context.getCurrentScope().setLastLine(dup);
         return dup;
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * 
      * @param context The thread context for the current thread
      * @param recv The receiver of the method (usually a class that has included Kernel)
      * @return
      * @deprecated Use the versions with zero, one, or two args.
      */
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(context, context.getRuntime()).split(context, args);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).split(context);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0, arg1);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, reads = {LASTLINE, BACKREF}, writes = {LASTLINE, BACKREF}, compat = RUBY1_8)
     public static IRubyObject scan(ThreadContext context, IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(context, context.getRuntime()).scan(context, pattern, block);
     }
 
     @JRubyMethod(required = 1, optional = 3, module = true, visibility = PRIVATE)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(context, context.getRuntime(), args);
     }
 
     @JRubyMethod(optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject sleep(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         long milliseconds;
 
         if (args.length == 0) {
             // Zero sleeps forever
             milliseconds = 0;
         } else {
             if (!(args[0] instanceof RubyNumeric)) {
                 throw context.getRuntime().newTypeError("can't convert " + args[0].getMetaClass().getName() + "into time interval");
             }
             milliseconds = (long) (args[0].convertToFloat().getDoubleValue() * 1000);
             if (milliseconds < 0) {
                 throw context.getRuntime().newArgumentError("time interval must be positive");
             } else if (milliseconds == 0) {
                 // Explicit zero in MRI returns immediately
                 return context.getRuntime().newFixnum(0);
             }
         }
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = context.getThread();
 
         // Spurious wakeup-loop
         do {
             long loopStartTime = System.currentTimeMillis();
             try {
                 // We break if we know this sleep was explicitly woken up/interrupted
                 if (!rubyThread.sleep(milliseconds)) break;
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis() - loopStartTime);
         } while (milliseconds > 0);
 
         return context.getRuntime().newFixnum(Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, false);
         return recv.getRuntime().getNil(); // not reached
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, true);
         return recv.getRuntime().getNil(); // not reached
     }
 
     private static void exit(Ruby runtime, IRubyObject[] args, boolean hard) {
         runtime.secure(4);
 
         int status = 0;
 
         if (args.length > 0) {
             RubyObject argument = (RubyObject) args[0];
             if (argument instanceof RubyBoolean) {
                 status = argument.isFalse() ? 1 : 0;
             } else {
                 status = RubyNumeric.fix2int(argument);
             }
         }
 
         if (hard) {
             if (runtime.getInstanceConfig().isHardExit()) {
                 System.exit(status);
             } else {
                 throw new MainExitException(status, true);
             }
         } else {
             throw runtime.newSystemExit(status);
         }
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE)
     public static RubyArray global_variables(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     // In 1.9, return symbols
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyArray global_variables19(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newSymbol(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE)
     public static RubyArray local_variables(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     // In 1.9, return symbols
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyArray local_variables19(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
 
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newSymbol(name));
         }
 
         return localVariables;
     }
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding(recv));
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyBinding binding_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding());
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, module = true, visibility = PRIVATE)
     public static RubyBoolean block_given_p(ThreadContext context, IRubyObject recv) {
         return context.getRuntime().newBoolean(context.getCurrentFrame().getBlock().isGiven());
     }
 
 
     @Deprecated
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         return sprintf(recv.getRuntime().getCurrentContext(), recv, args);
     }
 
     @JRubyMethod(name = {"sprintf", "format"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject sprintf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw context.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = context.getRuntime().newArrayNoCopy(args);
         newArgs.shift(context);
 
         return str.op_format(context, newArgs);
     }
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, module = true, visibility = PRIVATE, omit = true)
     public static IRubyObject raise(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Ruby runtime = context.getRuntime();
 
         RaiseException raise;
         switch (args.length) {
             case 0:
                 IRubyObject lastException = runtime.getGlobalVariables().get("$!");
                 if (lastException.isNil()) {
                     raise = new RaiseException(runtime, runtime.getRuntimeError(), "", false);
                 } else {
                     // non RubyException value is allowed to be assigned as $!.
                     raise = new RaiseException((RubyException) lastException);
                 }
                 break;
             case 1:
                 if (args[0] instanceof RubyString) {
                     raise = new RaiseException((RubyException) runtime.getRuntimeError().newInstance(context, args, block));
                 } else {
                     raise = new RaiseException(convertToException(runtime, args[0], null));
                 }
                 break;
             default:
                 raise = new RaiseException(convertToException(runtime, args[0], args[1]));
                 if (args.length > 2) {
                     raise.getException().set_backtrace(args[2]);
                 }
                 break;
         }
         
         raise.preRaise(context);
 
         if (runtime.getDebug().isTrue()) {
             printExceptionSummary(context, runtime, raise.getException());
         }
 
         throw raise;
     }
 
     private static RubyException convertToException(Ruby runtime, IRubyObject obj, IRubyObject optionalMessage) {
         if (!obj.respondsTo("exception")) {
             throw runtime.newTypeError("exception class/object expected");
         }
         IRubyObject exception;
         if (optionalMessage == null) {
             exception = obj.callMethod(runtime.getCurrentContext(), "exception");
         } else {
             exception = obj.callMethod(runtime.getCurrentContext(), "exception", optionalMessage);
         }
         try {
             return (RubyException) exception;
         } catch (ClassCastException cce) {
             throw runtime.newTypeError("exception object expected");
         }
     }
 
     private static void printExceptionSummary(ThreadContext context, Ruby runtime, RubyException rEx) {
         ThreadContext.RubyStackTraceElement[] elements = rEx.getBacktraceElements();
         ThreadContext.RubyStackTraceElement firstElement = elements[0];
         String msg = String.format("Exception `%s' at %s:%s - %s\n",
                 rEx.getMetaClass(),
                 firstElement.getFileName(), firstElement.getLineNumber(),
                 runtime.is1_9() ? TypeConverter.convertToType(rEx, runtime.getString(), "to_s") : rEx.convertToString().toString());
 
         runtime.getErrorStream().print(msg);
     }
 
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         return requireCommon(recv.getRuntime(), recv, name, block);
     }
 
     @JRubyMethod(name = "require", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject require19(ThreadContext context, IRubyObject recv, IRubyObject name, Block block) {
         Ruby runtime = context.getRuntime();
 
         IRubyObject tmp = name.checkStringType();
         if (!tmp.isNil()) {
             return requireCommon(runtime, recv, tmp, block);
         }
 
         return requireCommon(runtime, recv,
                 name.respondsTo("to_path") ? name.callMethod(context, "to_path") : name, block);
     }
 
     private static IRubyObject requireCommon(Ruby runtime, IRubyObject recv, IRubyObject name, Block block) {
         if (runtime.getLoadService().lockAndRequire(name.convertToString().toString())) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         return loadCommon(args[0], recv.getRuntime(), args, block);
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject load19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject file = args[0];
         if (!(file instanceof RubyString) && file.respondsTo("to_path")) {
             file = file.callMethod(context, "to_path");
         }
 
         return loadCommon(file, context.getRuntime(), args, block);
     }
 
     private static IRubyObject loadCommon(IRubyObject fileName, Ruby runtime, IRubyObject[] args, Block block) {
         RubyString file = fileName.convertToString();
 
         boolean wrap = args.length == 2 ? args[1].isTrue() : false;
 
         runtime.getLoadService().load(file.getByteList().toString(), wrap);
 
         return runtime.getTrue();
     }
 
     @JRubyMethod(required = 1, optional = 3, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject eval(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return evalCommon(context, recv, args, block, evalBinding18);
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject eval19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return evalCommon(context, recv, args, block, evalBinding19);
     }
 
     private static IRubyObject evalCommon(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block, EvalBinding evalBinding) {
         Ruby runtime = context.getRuntime();
         // string to eval
         RubyString src = args[0].convertToString();
         runtime.checkSafeString(src);
 
         boolean bindingGiven = args.length > 1 && !args[1].isNil();
         Binding binding = bindingGiven ? evalBinding.convertToBinding(args[1]) : context.currentBinding();
         if (args.length > 2) {
             // file given, use it and force it into binding
             binding.setFile(args[2].convertToString().toString());
         } else {
             // file not given
             if (bindingGiven) {
                 // binding given, use binding's file
             } else {
                 // no binding given, use (eval)
                 binding.setFile("(eval)");
             }
         }
         if (args.length > 3) {
             // file given, use it and force it into binding
             // -1 because parser uses zero offsets and other code compensates
             binding.setLine(((int) args[3].convertToInteger().getLongValue()) - 1);
         } else {
             if (bindingGiven) {
                 // binding given, use binding's line
             } else {
                 // no binding given, use 0 for both
                 binding.setLine(0);
             }
         }
 
         // set method to current frame's, which should be caller's
         String frameName = context.getFrameName();
         if (frameName != null) binding.setMethod(frameName);
 
         if (bindingGiven) recv = binding.getSelf();
 
         return ASTInterpreter.evalWithBinding(context, recv, src, binding);
     }
 
     private static abstract class EvalBinding {
         public abstract Binding convertToBinding(IRubyObject scope);
     }
 
     private static EvalBinding evalBinding18 = new EvalBinding() {
         public Binding convertToBinding(IRubyObject scope) {
             if (scope instanceof RubyBinding) {
                 return ((RubyBinding)scope).getBinding().clone();
             } else {
                 if (scope instanceof RubyProc) {
                     return ((RubyProc) scope).getBlock().getBinding().clone();
                 } else {
                     // bomb out, it's not a binding or a proc
                     throw scope.getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
                 }
             }
         }
     };
 
     private static EvalBinding evalBinding19 = new EvalBinding() {
         public Binding convertToBinding(IRubyObject scope) {
             if (scope instanceof RubyBinding) {
                 return ((RubyBinding)scope).getBinding().clone();
             } else {
                 throw scope.getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Binding)");
             }
         }
     };
 
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject callcc(ThreadContext context, IRubyObject recv, Block block) {
         RubyContinuation continuation = new RubyContinuation(context.getRuntime());
         return continuation.enter(context, continuation, block);
     }
 
     @JRubyMethod(optional = 1, module = true, visibility = PRIVATE, omit = true)
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level (" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE)
     public static IRubyObject rbCatch(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         Ruby runtime = context.runtime;
         RubyContinuation rbContinuation = new RubyContinuation(runtime, stringOrSymbol(tag));
         try {
             context.pushCatch(rbContinuation.getContinuation());
             return rbContinuation.enter(context, rbContinuation, block);
         } finally {
             context.popCatch();
         }
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject rbCatch19(ThreadContext context, IRubyObject recv, Block block) {
         IRubyObject tag = new RubyObject(context.runtime.getObject());
         return rbCatch19Common(context, tag, block, true);
     }
 
     @JRubyMethod(name = "catch", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject rbCatch19(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         return rbCatch19Common(context, tag, block, false);
     }
 
     private static IRubyObject rbCatch19Common(ThreadContext context, IRubyObject tag, Block block, boolean yieldTag) {
         RubyContinuation rbContinuation = new RubyContinuation(context.getRuntime(), tag);
         try {
             context.pushCatch(rbContinuation.getContinuation());
             return rbContinuation.enter(context, yieldTag ? tag : rbContinuation, block);
         } finally {
             context.popCatch();
         }
     }
 
     @JRubyMethod(name = "throw", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         return rbThrowInternal(context, stringOrSymbol(tag), IRubyObject.NULL_ARRAY, block, uncaught18);
     }
 
     @JRubyMethod(name = "throw", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject tag, IRubyObject arg, Block block) {
         return rbThrowInternal(context, stringOrSymbol(tag), new IRubyObject[] {arg}, block, uncaught18);
     }
 
     private static RubySymbol stringOrSymbol(IRubyObject obj) {
         if (obj instanceof RubySymbol) {
             return (RubySymbol)obj;
         } else {
             return RubySymbol.newSymbol(obj.getRuntime(), obj.asJavaString().intern());
         }
     }
 
     @JRubyMethod(name = "throw", frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject rbThrow19(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         return rbThrowInternal(context, tag, IRubyObject.NULL_ARRAY, block, uncaught19);
     }
 
     @JRubyMethod(name = "throw", frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject rbThrow19(ThreadContext context, IRubyObject recv, IRubyObject tag, IRubyObject arg, Block block) {
         return rbThrowInternal(context, tag, new IRubyObject[] {arg}, block, uncaught19);
     }
 
     private static IRubyObject rbThrowInternal(ThreadContext context, IRubyObject tag, IRubyObject[] args, Block block, Uncaught uncaught) {
         Ruby runtime = context.getRuntime();
 
         RubyContinuation.Continuation continuation = context.getActiveCatch(tag);
 
         if (continuation != null) {
             continuation.args = args;
             throw continuation;
         }
 
         // No catch active for this throw
         String message = "uncaught throw `" + tag + "'";
         RubyThread currentThread = context.getThread();
 
         if (currentThread == runtime.getThreadService().getMainThread()) {
             throw uncaught.uncaughtThrow(runtime, message, tag);
         } else {
             message += " in thread 0x" + Integer.toHexString(RubyInteger.fix2int(currentThread.id()));
             if (runtime.is1_9()) {
                 throw runtime.newArgumentError(message);
             } else {
                 throw runtime.newThreadError(message);
             }
         }
     }
 
     private static abstract class Uncaught {
         public abstract RaiseException uncaughtThrow(Ruby runtime, String message, IRubyObject tag);
     }
 
     private static final Uncaught uncaught18 = new Uncaught() {
         public RaiseException uncaughtThrow(Ruby runtime, String message, IRubyObject tag) {
             return runtime.newNameError(message, tag.toString());
         }
     };
 
     private static final Uncaught uncaught19 = new Uncaught() {
         public RaiseException uncaughtThrow(Ruby runtime, String message, IRubyObject tag) {
             return runtime.newArgumentError(message);
         }
     };
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject trap(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         context.getRuntime().getLoadService().require("jsignal_internal");
         return RuntimeHelpers.invoke(context, recv, "__jtrap", args, block);
     }
     
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject warn(ThreadContext context, IRubyObject recv, IRubyObject message) {
         Ruby runtime = context.getRuntime();
         
         if (runtime.warningsEnabled()) {
             IRubyObject out = runtime.getGlobalVariables().get("$stderr");
             RuntimeHelpers.invoke(context, out, "puts", message);
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject set_trace_func(ThreadContext context, IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             context.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw context.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             context.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject trace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
         RubyProc proc = null;
         String var = args.length > 1 ? args[0].toString() : null;
         // ignore if it's not a global var
         if (var.charAt(0) != '$') return context.getRuntime().getNil();
         if (args.length == 1) proc = RubyProc.newProc(context.getRuntime(), block, Block.Type.PROC);
         if (args.length == 2) {
             proc = (RubyProc)TypeConverter.convertToType(args[1], context.getRuntime().getProc(), "to_proc", true);
         }
         
         context.getRuntime().getGlobalVariables().setTraceVar(var, proc);
         
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(required = 1, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject untrace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
         String var = args.length >= 1 ? args[0].toString() : null;
 
         // ignore if it's not a global var
         if (var.charAt(0) != '$') return context.getRuntime().getNil();
         
         if (args.length > 1) {
             ArrayList<IRubyObject> success = new ArrayList<IRubyObject>();
             for (int i = 1; i < args.length; i++) {
                 if (context.getRuntime().getGlobalVariables().untraceVar(var, args[i])) {
                     success.add(args[i]);
                 }
             }
             return RubyArray.newArray(context.getRuntime(), success);
         } else {
             context.getRuntime().getGlobalVariables().untraceVar(var);
         }
         
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_added(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_removed(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject singleton_method_undefined(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(required = 1, optional = 1, compat = RUBY1_9)
     public static IRubyObject define_singleton_method(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
 
         RubyClass singleton_class = recv.getSingletonClass();
         if (args.length > 1) {
             IRubyObject arg1 = args[1];
             if (context.runtime.getUnboundMethod().isInstance(args[1])) {
                 RubyUnboundMethod method = (RubyUnboundMethod)arg1;
                 RubyModule owner = (RubyModule)method.owner(context);
                 if (owner.isSingleton() &&
                     !(recv.getMetaClass().isSingleton() && recv.getMetaClass().isKindOfModule(owner))) {
 
                     throw context.runtime.newTypeError("can't bind singleton method to a different class");
                 }
             }
             return singleton_class.define_method(context, args[0], args[1], block);
         } else {
             return singleton_class.define_method(context, args[0], block);
         }
     }
 
     @JRubyMethod(name = {"proc", "lambda"}, module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyProc proc(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
 
     @JRubyMethod(module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyProc lambda(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = "proc", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyProc proc_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.PROC, block);
     }
 
     @JRubyMethod(name = "loop", module = true, visibility = PRIVATE)
     public static IRubyObject loop(ThreadContext context, IRubyObject recv, Block block) {
         if (context.runtime.is1_9() && !block.isGiven()) {
             return RubyEnumerator.enumeratorize(context.runtime, recv, "loop");
         }
         IRubyObject nil = context.getRuntime().getNil();
         RubyClass stopIteration = context.getRuntime().getStopIteration();
         try {
             while (true) {
                 block.yieldSpecific(context);
 
                 context.pollThreadEvents();
             }
         } catch (RaiseException ex) {
             if (!stopIteration.op_eqq(context, ex.getException()).isTrue()) {
                 throw ex;
             }
         }
         return nil;
     }
 
     @JRubyMethod(name = "test", required = 2, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject test(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) throw context.getRuntime().newArgumentError("wrong number of arguments");
 
         int cmd;
         if (args[0] instanceof RubyFixnum) {
             cmd = (int)((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubyString &&
                 ((RubyString) args[0]).getByteList().length() > 0) {
             // MRI behavior: use first byte of string value if len > 0
             cmd = ((RubyString) args[0]).getByteList().charAt(0);
         } else {
             cmd = (int) args[0].convertToInteger().getLongValue();
         }
         
         // MRI behavior: raise ArgumentError for 'unknown command' before
         // checking number of args.
         switch(cmd) {
         case 'A': case 'b': case 'c': case 'C': case 'd': case 'e': case 'f': case 'g': case 'G': 
         case 'k': case 'M': case 'l': case 'o': case 'O': case 'p': case 'r': case 'R': case 's':
         case 'S': case 'u': case 'w': case 'W': case 'x': case 'X': case 'z': case '=': case '<':
         case '>': case '-':
             break;
         default:
             throw context.getRuntime().newArgumentError("unknown command ?" + (char) cmd);
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-': case '=': case '<': case '>':
             if (args.length != 3) throw context.getRuntime().newArgumentError(args.length, 3);
             break;
         default:
             if (args.length != 2) throw context.getRuntime().newArgumentError(args.length, 2);
             break;
         }
         
         switch (cmd) {
         case 'A': // ?A  | Time    | Last access time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).atime();
         case 'b': // ?b  | boolean | True if file1 is a block device
             return RubyFileTest.blockdev_p(recv, args[1]);
         case 'c': // ?c  | boolean | True if file1 is a character device
             return RubyFileTest.chardev_p(recv, args[1]);
         case 'C': // ?C  | Time    | Last change time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).ctime();
         case 'd': // ?d  | boolean | True if file1 exists and is a directory
             return RubyFileTest.directory_p(recv, args[1]);
         case 'e': // ?e  | boolean | True if file1 exists
             return RubyFileTest.exist_p(recv, args[1]);
         case 'f': // ?f  | boolean | True if file1 exists and is a regular file
             return RubyFileTest.file_p(recv, args[1]);
         case 'g': // ?g  | boolean | True if file1 has the \CF{setgid} bit
             return RubyFileTest.setgid_p(recv, args[1]);
         case 'G': // ?G  | boolean | True if file1 exists and has a group ownership equal to the caller's group
             return RubyFileTest.grpowned_p(recv, args[1]);
         case 'k': // ?k  | boolean | True if file1 exists and has the sticky bit set
             return RubyFileTest.sticky_p(recv, args[1]);
         case 'M': // ?M  | Time    | Last modification time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtime();
         case 'l': // ?l  | boolean | True if file1 exists and is a symbolic link
             return RubyFileTest.symlink_p(recv, args[1]);
         case 'o': // ?o  | boolean | True if file1 exists and is owned by the caller's effective uid
             return RubyFileTest.owned_p(recv, args[1]);
         case 'O': // ?O  | boolean | True if file1 exists and is owned by the caller's real uid 
             return RubyFileTest.rowned_p(recv, args[1]);
         case 'p': // ?p  | boolean | True if file1 exists and is a fifo
             return RubyFileTest.pipe_p(recv, args[1]);
         case 'r': // ?r  | boolean | True if file1 is readable by the effective uid/gid of the caller
             return RubyFileTest.readable_p(recv, args[1]);
         case 'R': // ?R  | boolean | True if file is readable by the real uid/gid of the caller
             // FIXME: Need to implement an readable_real_p in FileTest
             return RubyFileTest.readable_p(recv, args[1]);
         case 's': // ?s  | int/nil | If file1 has nonzero size, return the size, otherwise nil
             return RubyFileTest.size_p(recv, args[1]);
         case 'S': // ?S  | boolean | True if file1 exists and is a socket
             return RubyFileTest.socket_p(recv, args[1]);
         case 'u': // ?u  | boolean | True if file1 has the setuid bit set
             return RubyFileTest.setuid_p(recv, args[1]);
         case 'w': // ?w  | boolean | True if file1 exists and is writable by effective uid/gid
             return RubyFileTest.writable_p(recv, args[1]);
         case 'W': // ?W  | boolean | True if file1 exists and is writable by the real uid/gid
             // FIXME: Need to implement an writable_real_p in FileTest
             return RubyFileTest.writable_p(recv, args[1]);
         case 'x': // ?x  | boolean | True if file1 exists and is executable by the effective uid/gid
             return RubyFileTest.executable_p(recv, args[1]);
         case 'X': // ?X  | boolean | True if file1 exists and is executable by the real uid/gid
             return RubyFileTest.executable_real_p(recv, args[1]);
         case 'z': // ?z  | boolean | True if file1 exists and has a zero length
             return RubyFileTest.zero_p(recv, args[1]);
         case '=': // ?=  | boolean | True if the modification times of file1 and file2 are equal
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeEquals(args[2]);
         case '<': // ?<  | boolean | True if the modification time of file1 is prior to that of file2
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeLessThan(args[2]);
         case '>': // ?>  | boolean | True if the modification time of file1 is after that of file2
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeGreaterThan(args[2]);
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             return RubyFileTest.identical_p(recv, args[1], args[2]);
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     @JRubyMethod(name = "`", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject backquote(ThreadContext context, IRubyObject recv, IRubyObject aString) {
         Ruby runtime = context.getRuntime();
         RubyString string = aString.convertToString();
         IRubyObject[] args = new IRubyObject[] {string};
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         int resultCode;
 
         try {
             // NOTE: not searching executable path before invoking args
             resultCode = ShellLauncher.runAndWait(runtime, args, output, false);
         } catch (Exception e) {
             resultCode = 127;
         }
 
         context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
 
         byte[] out = output.toByteArray();
         int length = out.length;
 
         if (Platform.IS_WINDOWS) {
             // MRI behavior, replace '\r\n' by '\n'
             int newPos = 0;
             byte curr, next;
             for (int pos = 0; pos < length; pos++) {
                 curr = out[pos];
                 if (pos == length - 1) {
                     out[newPos++] = curr;
                     break;
                 }
                 next = out[pos + 1];
                 if (curr != '\r' || next != '\n') {
                     out[newPos++] = curr;
                 }
             }
 
             // trim the length
             length = newPos;
         }
 
         return RubyString.newStringNoCopy(runtime, out, 0, length);
     }
 
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
 
         // Not sure how well this works, but it works much better than
         // just currentTimeMillis by itself.
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(System.currentTimeMillis() ^
                recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
                runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
 
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
     
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         IRubyObject newRandomSeed = arg.convertToInteger("to_int");
         Ruby runtime = context.getRuntime();
 
         long seedArg = 0;
         if (newRandomSeed instanceof RubyBignum) {
             seedArg = ((RubyBignum)newRandomSeed).getValue().longValue();
         } else if (!arg.isNil()) {
             seedArg = RubyNumeric.num2long(newRandomSeed);
         }
 
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(seedArg);
 
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
 
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject srand19(ThreadContext context, IRubyObject recv) {
         return RubyRandom.srand(context, recv);
     }
 
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static IRubyObject srand19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RubyRandom.srandCommon(context, recv, arg.convertToInteger("to_int"), true);
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyFloat.newFloat(runtime, runtime.getRandom().nextDouble());
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         Random random = runtime.getRandom();
 
         return randCommon(context, runtime, random, recv, arg);
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyNumeric rand19(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyFloat.newFloat(runtime, RubyRandom.globalRandom.nextDouble());
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyNumeric rand19(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         Random random = RubyRandom.globalRandom;
 
         return randCommon(context, runtime, random, recv, arg);
     }
 
     private static RubyNumeric randCommon(ThreadContext context, Ruby runtime, Random random, IRubyObject recv, IRubyObject arg) {
         if (arg instanceof RubyBignum) {
             byte[] bigCeilBytes = ((RubyBignum) arg).getValue().toByteArray();
             BigInteger bigCeil = new BigInteger(bigCeilBytes).abs();
             byte[] randBytes = new byte[bigCeilBytes.length];
             random.nextBytes(randBytes);
             BigInteger result = new BigInteger(randBytes).abs().mod(bigCeil);
             return new RubyBignum(runtime, result);
         }
 
         RubyInteger integerCeil = (RubyInteger)RubyKernel.new_integer(context, recv, arg);
         long ceil = Math.abs(integerCeil.getLongValue());
         if (ceil == 0) return RubyFloat.newFloat(runtime, random.nextDouble());
         if (ceil > Integer.MAX_VALUE) return runtime.newFixnum(Math.abs(random.nextLong()) % ceil);
 
         return runtime.newFixnum(random.nextInt((int) ceil));
     }
 
     @JRubyMethod(name = "spawn", required = 1, rest = true, module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyFixnum spawn(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         long pid = ShellLauncher.runWithoutWait(runtime, args);
         return RubyFixnum.newFixnum(runtime, pid);
     }
 
     @JRubyMethod(name = "syscall", required = 1, optional = 9, module = true, visibility = PRIVATE)
     public static IRubyObject syscall(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         throw context.getRuntime().newNotImplementedError("Kernel#syscall is not implemented in JRuby");
     }
 
     @JRubyMethod(name = "system", required = 1, rest = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyBoolean system(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         return systemCommon(context, recv, args) == 0 ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "system", required = 1, rest = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject system19(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode = systemCommon(context, recv, args);
         switch (resultCode) {
             case 0: return runtime.getTrue();
             case 127: return runtime.getNil();
             default: return runtime.getFalse();
         }
     }
 
     private static int systemCommon(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode;
 
         try {
             if (! Platform.IS_WINDOWS && args[args.length -1].asJavaString().matches(".*[^&]&\\s*")) {
                 // looks like we need to send process to the background
                 ShellLauncher.runWithoutWait(runtime, args);
                 return 0;
             }
             resultCode = ShellLauncher.runAndWait(runtime, args);
         } catch (Exception e) {
             resultCode = 127;
         }
 
         context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return resultCode;
     }
     
     @JRubyMethod(name = {"exec"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject exec(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         // This is a fairly specific hack for empty string, but it does the job
         if (args.length == 1 && args[0].convertToString().isEmpty()) {
             throw runtime.newErrnoENOENTError(args[0].convertToString().toString());
         }
         
         int resultCode;
         try {
             // TODO: exec should replace the current process.
             // This could be possible with JNA. 
             resultCode = ShellLauncher.execAndWait(runtime, args);
         } catch (RaiseException e) {
             throw e; // no need to wrap this exception
         } catch (Exception e) {
             throw runtime.newErrnoENOENTError("cannot execute");
         }
         
         exit(runtime, new IRubyObject[] {runtime.newFixnum(resultCode)}, true);
 
         // not reached
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.getRuntime();
         
         if (!RubyInstanceConfig.FORK_ENABLED) {
             throw runtime.newNotImplementedError("fork is unsafe and disabled by default on JRuby");
         }
         
         if (block.isGiven()) {
             int pid = runtime.getPosix().fork();
             
             if (pid == 0) {
                 try {
                     block.yield(context, runtime.getNil());
                 } catch (RaiseException re) {
                     if (re.getException() instanceof RubySystemExit) {
                         throw re;
                     }
                     return exit_bang(recv, new IRubyObject[] {RubyFixnum.minus_one(runtime)});
                 } catch (Throwable t) {
                     return exit_bang(recv, new IRubyObject[] {RubyFixnum.minus_one(runtime)});
                 }
                 return exit_bang(recv, new IRubyObject[] {RubyFixnum.zero(runtime)});
             } else {
                 return runtime.newFixnum(pid);
             }
         } else {
             int result = runtime.getPosix().fork();
         
             if (result == -1) {
                 return runtime.getNil();
             }
 
             return runtime.newFixnum(result);
         }
     }
 
     @JRubyMethod(module = true)
     public static IRubyObject tap(ThreadContext context, IRubyObject recv, Block block) {
         block.yield(context, recv);
         return recv;
     }
 
     @JRubyMethod(name = {"to_enum", "enum_for"}, rest = true, compat = RUBY1_9)
     public static IRubyObject to_enum(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         switch (args.length) {
         case 0: return enumeratorize(runtime, recv, "each");
         case 1: return enumeratorize(runtime, recv, args[0].asJavaString());
         case 2: return enumeratorize(runtime, recv, args[0].asJavaString(), args[1]);
         default:
             IRubyObject enumArgs[] = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, enumArgs, 0, enumArgs.length);
             return enumeratorize(runtime, recv, args[0].asJavaString(), enumArgs);
         }
     }
 
     @JRubyMethod(name = { "__method__", "__callee__" }, module = true, visibility = PRIVATE, reads = METHODNAME, omit = true)
     public static IRubyObject __method__(ThreadContext context, IRubyObject recv) {
         String frameName = context.getFrameName();
         if (frameName == null) {
             return context.nil;
         }
         return context.runtime.newSymbol(frameName);
     }
 
     @JRubyMethod(module = true, compat = RUBY1_9)
     public static IRubyObject singleton_class(IRubyObject recv) {
         return recv.getSingletonClass();
     }
 
     @JRubyMethod(rest = true, compat = RUBY1_9)
     public static IRubyObject public_send(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         recv.getMetaClass().checkMethodBound(context, args, PUBLIC);
         return ((RubyObject)recv).send19(context, args, Block.NULL_BLOCK);
     }
+
+    // Moved binding of these methods here, since Kernel can be included into
+    // BasicObject subclasses, and these methods must still work.
+    // See JRUBY-4871
+
+    @JRubyMethod(name = "==", required = 1, compat = RUBY1_8)
+    public static IRubyObject op_equal(ThreadContext context, IRubyObject self, IRubyObject other) {
+        return ((RubyBasicObject)self).op_equal(context, other);
+    }
+
+    @JRubyMethod(name = "equal?", required = 1, compat = RUBY1_8)
+    public static IRubyObject equal_p(ThreadContext context, IRubyObject self, IRubyObject other) {
+        return ((RubyBasicObject)self).equal_p(context, other);
+    }
+
+    @JRubyMethod(name = "eql?", required = 1)
+    public static IRubyObject eql_p(IRubyObject self, IRubyObject obj) {
+        return ((RubyBasicObject)self).eql_p(obj);
+    }
+
+    @JRubyMethod(name = "===", required = 1)
+    public static IRubyObject op_eqq(ThreadContext context, IRubyObject self, IRubyObject other) {
+        return ((RubyBasicObject)self).op_eqq(context, other);
+    }
+
+    @JRubyMethod(name = "<=>", required = 1, compat = RUBY1_9)
+    public static IRubyObject op_cmp(ThreadContext context, IRubyObject self, IRubyObject other) {
+        return ((RubyBasicObject)self).op_cmp(context, other);
+    }
+
+    @JRubyMethod(name = "initialize_copy", required = 1, visibility = PRIVATE)
+    public static IRubyObject initialize_copy(IRubyObject self, IRubyObject original) {
+        return ((RubyBasicObject)self).initialize_copy(original);
+    }
+
+    @JRubyMethod(name = "respond_to?", compat = RUBY1_8)
+    public static RubyBoolean respond_to_p(IRubyObject self, IRubyObject mname) {
+        return ((RubyBasicObject)self).respond_to_p(mname);
+    }
+
+    @JRubyMethod(name = "respond_to?", compat = RUBY1_9)
+    public static IRubyObject respond_to_p19(IRubyObject self, IRubyObject mname) {
+        return ((RubyBasicObject)self).respond_to_p19(mname);
+    }
+
+    @JRubyMethod(name = "respond_to?", compat = RUBY1_8)
+    public static RubyBoolean respond_to_p(IRubyObject self, IRubyObject mname, IRubyObject includePrivate) {
+        return ((RubyBasicObject)self).respond_to_p(mname, includePrivate);
+    }
+
+    @JRubyMethod(name = "respond_to?", compat = RUBY1_9)
+    public static IRubyObject respond_to_p19(IRubyObject self, IRubyObject mname, IRubyObject includePrivate) {
+        return ((RubyBasicObject)self).respond_to_p19(mname, includePrivate);
+    }
+
+    @JRubyMethod(name = {"object_id", "__id__"})
+    public static IRubyObject id(IRubyObject self) {
+        return ((RubyBasicObject)self).id();
+    }
+
+    @JRubyMethod(name = "id", compat = RUBY1_8)
+    public static IRubyObject id_deprecated(IRubyObject self) {
+        return ((RubyBasicObject)self).id_deprecated();
+    }
+
+    @JRubyMethod(name = "hash")
+    public static RubyFixnum hash(IRubyObject self) {
+        return ((RubyBasicObject)self).hash();
+    }
+
+    @JRubyMethod(name = "class")
+    public static RubyClass type(IRubyObject self) {
+        return ((RubyBasicObject)self).type();
+    }
+
+    @JRubyMethod(name = "type")
+    public static RubyClass type_deprecated(IRubyObject self) {
+        return ((RubyBasicObject)self).type_deprecated();
+    }
+
+    @JRubyMethod(name = "clone")
+    public static IRubyObject rbClone(IRubyObject self) {
+        return ((RubyBasicObject)self).rbClone();
+    }
+
+    @JRubyMethod
+    public static IRubyObject dup(IRubyObject self) {
+        return ((RubyBasicObject)self).dup();
+    }
+
+    @JRubyMethod(name = "display", optional = 1)
+    public static IRubyObject display(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).display(context, args);
+    }
+
+    @JRubyMethod(name = "tainted?")
+    public static RubyBoolean tainted_p(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).tainted_p(context);
+    }
+
+    @JRubyMethod(name = "taint")
+    public static IRubyObject taint(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).taint(context);
+    }
+
+    @JRubyMethod(name = "untaint")
+    public static IRubyObject untaint(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).untaint(context);
+    }
+
+    @JRubyMethod(name = "freeze")
+    public static IRubyObject freeze(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).freeze(context);
+    }
+
+    @JRubyMethod(name = "frozen?")
+    public static RubyBoolean frozen_p(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).frozen_p(context);
+    }
+
+    @JRubyMethod(name = "untrusted?", compat = RUBY1_9)
+    public static RubyBoolean untrusted_p(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).untrusted_p(context);
+    }
+
+    @JRubyMethod(compat = RUBY1_9)
+    public static IRubyObject untrust(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).untrust(context);
+    }
+
+    @JRubyMethod(compat = RUBY1_9)
+    public static IRubyObject trust(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).trust(context);
+    }
+
+    @JRubyMethod(name = "inspect")
+    public static IRubyObject inspect(IRubyObject self) {
+        return ((RubyBasicObject)self).inspect();
+    }
+
+    @JRubyMethod(name = "instance_of?", required = 1)
+    public static RubyBoolean instance_of_p(ThreadContext context, IRubyObject self, IRubyObject type) {
+        return ((RubyBasicObject)self).instance_of_p(context, type);
+    }
+
+    @JRubyMethod(name = {"kind_of?", "is_a?"}, required = 1)
+    public static RubyBoolean kind_of_p(ThreadContext context, IRubyObject self, IRubyObject type) {
+        return ((RubyBasicObject)self).kind_of_p(context, type);
+    }
+
+    @JRubyMethod(name = "methods", optional = 1, compat = RUBY1_8)
+    public static IRubyObject methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).methods(context, args);
+    }
+    @JRubyMethod(name = "methods", optional = 1, compat = RUBY1_9)
+    public static IRubyObject methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).methods19(context, args);
+    }
+
+    @JRubyMethod(name = "public_methods", optional = 1, compat = RUBY1_8)
+    public static IRubyObject public_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).public_methods(context, args);
+    }
+
+    @JRubyMethod(name = "public_methods", optional = 1, compat = RUBY1_9)
+    public static IRubyObject public_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).public_methods19(context, args);
+    }
+
+    @JRubyMethod(name = "protected_methods", optional = 1, compat = RUBY1_8)
+    public static IRubyObject protected_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).protected_methods(context, args);
+    }
+
+    @JRubyMethod(name = "protected_methods", optional = 1, compat = RUBY1_9)
+    public static IRubyObject protected_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).protected_methods19(context, args);
+    }
+
+    @JRubyMethod(name = "private_methods", optional = 1, compat = RUBY1_8)
+    public static IRubyObject private_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).private_methods(context, args);
+    }
+
+    @JRubyMethod(name = "private_methods", optional = 1, compat = RUBY1_9)
+    public static IRubyObject private_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).private_methods19(context, args);
+    }
+
+    @JRubyMethod(name = "singleton_methods", optional = 1, compat = RUBY1_8)
+    public static RubyArray singleton_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).singleton_methods(context, args);
+    }
+
+    @JRubyMethod(name = "singleton_methods", optional = 1 , compat = RUBY1_9)
+    public static RubyArray singleton_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).singleton_methods19(context, args);
+    }
+
+    @JRubyMethod(name = "method", required = 1)
+    public static IRubyObject method(IRubyObject self, IRubyObject symbol) {
+        return ((RubyBasicObject)self).method(symbol);
+    }
+
+    @JRubyMethod(name = "method", required = 1, compat = RUBY1_9)
+    public static IRubyObject method19(IRubyObject self, IRubyObject symbol) {
+        return ((RubyBasicObject)self).method19(symbol);
+    }
+
+    @JRubyMethod(name = "to_s")
+    public static IRubyObject to_s(IRubyObject self) {
+        return ((RubyBasicObject)self).to_s();
+    }
+
+    @JRubyMethod(name = "to_a", visibility = PUBLIC, compat = RUBY1_8)
+    public static RubyArray to_a(IRubyObject self) {
+        return ((RubyBasicObject)self).to_a();
+    }
+
+    @JRubyMethod(compat = RUBY1_8)
+    public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, Block block) {
+        return ((RubyBasicObject)self).instance_eval(context, block);
+    }
+    @JRubyMethod(compat = RUBY1_8)
+    public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
+        return ((RubyBasicObject)self).instance_eval(context, arg0, block);
+    }
+    @JRubyMethod(compat = RUBY1_8)
+    public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
+        return ((RubyBasicObject)self).instance_eval(context, arg0, arg1, block);
+    }
+    @JRubyMethod(compat = RUBY1_8)
+    public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
+        return ((RubyBasicObject)self).instance_eval(context, arg0, arg1, arg2, block);
+    }
+
+    @JRubyMethod(optional = 3, rest = true, compat = RUBY1_8)
+    public static IRubyObject instance_exec(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
+        return ((RubyBasicObject)self).instance_exec(context, args, block);
+    }
+
+    @JRubyMethod(name = "extend", required = 1, rest = true)
+    public static IRubyObject extend(IRubyObject self, IRubyObject[] args) {
+        return ((RubyBasicObject)self).extend(args);
+    }
+
+    @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
+    public static IRubyObject send(ThreadContext context, IRubyObject self, Block block) {
+        return ((RubyBasicObject)self).send(context, block);
+    }
+    @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
+    public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
+        return ((RubyBasicObject)self).send(context, arg0, block);
+    }
+    @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
+    public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
+        return ((RubyBasicObject)self).send(context, arg0, arg1, block);
+    }
+    @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
+    public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
+        return ((RubyBasicObject)self).send(context, arg0, arg1, arg2, block);
+    }
+    @JRubyMethod(name = {"send", "__send__"}, rest = true, compat = RUBY1_8)
+    public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
+        return ((RubyBasicObject)self).send(context, args, block);
+    }
+
+    @JRubyMethod(name = {"send"}, compat = RUBY1_9)
+    public static IRubyObject send19(ThreadContext context, IRubyObject self, Block block) {
+        return ((RubyBasicObject)self).send19(context, block);
+    }
+    @JRubyMethod(name = {"send"}, compat = RUBY1_9)
+    public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
+        return ((RubyBasicObject)self).send19(context, arg0, block);
+    }
+    @JRubyMethod(name = {"send"}, compat = RUBY1_9)
+    public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
+        return ((RubyBasicObject)self).send19(context, arg0, arg1, block);
+    }
+    @JRubyMethod(name = {"send"}, compat = RUBY1_9)
+    public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
+        return ((RubyBasicObject)self).send19(context, arg0, arg1, arg2, block);
+    }
+    @JRubyMethod(name = {"send"}, rest = true, compat = RUBY1_9)
+    public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
+        return ((RubyBasicObject)self).send19(context, args, block);
+    }
+
+    @JRubyMethod(name = "nil?")
+    public static IRubyObject nil_p(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).nil_p(context);
+    }
+
+    @JRubyMethod(name = "=~", required = 1, compat = RUBY1_8)
+    public static IRubyObject op_match(ThreadContext context, IRubyObject self, IRubyObject arg) {
+        return ((RubyBasicObject)self).op_match(context, arg);
+    }
+
+    @JRubyMethod(name = "=~", required = 1, compat = RUBY1_9)
+    public static IRubyObject op_match19(ThreadContext context, IRubyObject self, IRubyObject arg) {
+        return ((RubyBasicObject)self).op_match19(context, arg);
+    }
+
+    @JRubyMethod(name = "!~", required = 1, compat = RUBY1_9)
+    public static IRubyObject op_not_match(ThreadContext context, IRubyObject self, IRubyObject arg) {
+        return ((RubyBasicObject)self).op_not_match(context, arg);
+    }
+
+    @JRubyMethod(name = "instance_variable_defined?", required = 1)
+    public static IRubyObject instance_variable_defined_p(ThreadContext context, IRubyObject self, IRubyObject name) {
+        return ((RubyBasicObject)self).instance_variable_defined_p(context, name);
+    }
+
+    @JRubyMethod(name = "instance_variable_get", required = 1)
+    public static IRubyObject instance_variable_get(ThreadContext context, IRubyObject self, IRubyObject name) {
+        return ((RubyBasicObject)self).instance_variable_get(context, name);
+    }
+
+    @JRubyMethod(name = "instance_variable_set", required = 2)
+    public static IRubyObject instance_variable_set(IRubyObject self, IRubyObject name, IRubyObject value) {
+        return ((RubyBasicObject)self).instance_variable_set(name, value);
+    }
+
+    @JRubyMethod(visibility = PRIVATE)
+    public static IRubyObject remove_instance_variable(ThreadContext context, IRubyObject self, IRubyObject name, Block block) {
+        return ((RubyBasicObject)self).remove_instance_variable(context, name, block);
+    }
+
+    @JRubyMethod(name = "instance_variables")
+    public static RubyArray instance_variables(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).instance_variables(context);
+    }
+
+    @JRubyMethod(name = "instance_variables", compat = RUBY1_9)
+    public static RubyArray instance_variables19(ThreadContext context, IRubyObject self) {
+        return ((RubyBasicObject)self).instance_variables19(context);
+    }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 2b74f652e6..ed323e994d 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1631 +1,454 @@
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
-import java.util.HashSet;
 import java.util.List;
 
-import java.util.Set;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
-import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import static org.jruby.javasupport.util.RuntimeHelpers.metaclass;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
-import org.jruby.util.IdUtil;
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
 
-        objectClass.defineAnnotatedMethods(ObjectMethods.class);
+        objectClass.defineAnnotatedMethods(RubyObject.class);
 
         return objectClass;
     }
 
     /**
-     * Interestingly, the Object class doesn't really have that many
-     * methods for itself. Instead almost all of the Object methods
-     * are really defined on the Kernel module. This class is a holder
-     * for all Object methods.
-     *
-     * @see RubyKernel
-     */
-    public static class ObjectMethods {
-        @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_8)
-        public static IRubyObject initialize(IRubyObject self) {
-            return self.getRuntime().getNil();
-        }
-
-        @JRubyMethod(name = "initialize", visibility = PRIVATE, rest = true, compat = RUBY1_9)
-        public static IRubyObject initialize19(IRubyObject self, IRubyObject[] args) {
-            return self.getRuntime().getNil();
-        }
-    }
-
-    /**
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
 
     public static final ObjectAllocator REIFYING_OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             reifyAncestors(klass);
             return klass.allocate();
         }
 
         public void reifyAncestors(RubyClass klass) {
             if (klass.getAllocator() == this) {
                 reifyAncestors(klass.getSuperClass().getRealClass());
                 klass.reify();
             }
         }
     };
 
+    @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_8)
+    public IRubyObject initialize() {
+        return getRuntime().getNil();
+    }
+
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
-     * The actual method that checks frozen with the default frozen message from MRI.
-     * If possible, call this instead of {@link #testFrozen}.
-     */
-    protected void checkFrozen() {
-        testFrozen();
-    }
-
-    /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject[] args, Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", args, block);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", block);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject arg0, Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", arg0, block);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject arg0, IRubyObject arg1, Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", arg0, arg1, block);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "initialize", arg0, arg1, arg2, block);
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
                 "wrong number of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
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
 
     // Methods of the Object class (rb_obj_*):
 
-    /** rb_obj_equal
-     *
-     * Will by default use identity equality to compare objects. This
-     * follows the Ruby semantics.
-     */
-    @JRubyMethod(name = "==", required = 1, compat = RUBY1_8)
-    @Override
-    public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
-        return super.op_equal_19(context, obj);
-    }
-
-    /** rb_obj_equal
-     *
-     * Will use Java identity equality.
-     */
-    @JRubyMethod(name = "equal?", required = 1, compat = RUBY1_8)
-    public IRubyObject equal_p(ThreadContext context, IRubyObject obj) {
-        return this == obj ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
-    }
-
-    /** rb_obj_equal
-     *
-     * Just like "==" and "equal?", "eql?" will use identity equality for Object.
-     */
-    @JRubyMethod(name = "eql?", required = 1)
-    public IRubyObject eql_p(IRubyObject obj) {
-        return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
-    }
-
     /** rb_equal
      *
      * The Ruby "===" method is used by default in case/when
      * statements. The Object implementation first checks Java identity
      * equality and then calls the "==" method too.
      */
-    @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return context.getRuntime().newBoolean(equalInternal(context, this, other));
     }
 
-    @JRubyMethod(name = "<=>", required = 1, compat = RUBY1_9)
-    public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
-        Ruby runtime = context.getRuntime();
-        if (this == other || this.callMethod(context, "==", other).isTrue()){
-            return RubyFixnum.zero(runtime);
-        }
-        return runtime.getNil();
-    }
-
     protected static DynamicMethod _op_equal(ThreadContext context, RubyClass metaclass) {
         if (metaclass.index >= ClassIndex.MAX_CLASSES) return metaclass.searchMethod("==");
         return context.runtimeCache.getMethod(context, metaclass, metaclass.index * (MethodIndex.OP_EQUAL + 1), "==");
     }
 
     protected static DynamicMethod _eql(ThreadContext context, RubyClass metaclass) {
         if (metaclass.index >= ClassIndex.MAX_CLASSES) return metaclass.searchMethod("eql?");
         return context.runtimeCache.getMethod(context, metaclass, metaclass.index * (MethodIndex.EQL + 1), "eql?");
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "==" method.
      */
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject a, final IRubyObject b){
         if (a == b) {
             return true;
         } else if (a instanceof RubySymbol) {
             return false;
         } else if (a instanceof RubyFixnum && b instanceof RubyFixnum) {
             return ((RubyFixnum)a).fastEqual((RubyFixnum)b);
         } else if (a instanceof RubyFloat && b instanceof RubyFloat) {
             return ((RubyFloat)a).fastEqual((RubyFloat)b);
         } else {
             RubyClass metaclass = metaclass(a);
             return _op_equal(context, metaclass).call(context, a, metaclass, "==", b).isTrue();
         }
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "eql?" method.
      */
     protected static boolean eqlInternal(final ThreadContext context, final IRubyObject a, final IRubyObject b){
         if (a == b) {
             return true;
         } else if (a instanceof RubySymbol) {
             return false;
         } else if (a instanceof RubyNumeric) {
             if (a.getClass() != b.getClass()) return false;
             return equalInternal(context, a, b);
         } else {
             RubyClass metaclass = metaclass(a);
             return _eql(context, metaclass).call(context, a, metaclass, "eql?", b).isTrue();
         }
     }
 
-    /** rb_obj_init_copy
-     *
-     * Initializes this object as a copy of the original, that is the
-     * parameter to this object. Will make sure that the argument
-     * actually has the same real class as this object. It shouldn't
-     * be possible to initialize an object with something totally
-     * different.
-     */
-    @JRubyMethod(name = "initialize_copy", required = 1, visibility = PRIVATE)
-    public IRubyObject initialize_copy(IRubyObject original) {
-        if (this == original) {
-            return this;
-        }
-        checkFrozen();
-
-        if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
-            throw getRuntime().newTypeError("initialize_copy should take same class object");
-        }
-
-        return this;
-    }
-
-    /** obj_respond_to
-     *
-     * respond_to?( aSymbol, includePriv=false ) -> true or false
-     *
-     * Returns true if this object responds to the given method. Private
-     * methods are included in the search only if the optional second
-     * parameter evaluates to true.
-     *
-     * @return true if this responds to the given method
-     *
-     * !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
-     *
-     * Going back to splitting according to method arity. MRI is wrong
-     * about most of these anyway, and since we have arity splitting
-     * in both the compiler and the interpreter, the performance
-     * benefit is important for this method.
-     */
-    @JRubyMethod(name = "respond_to?", compat = RUBY1_8)
-    public RubyBoolean respond_to_p(IRubyObject mname) {
-        String name = mname.asJavaString();
-        return getRuntime().newBoolean(getMetaClass().isMethodBound(name, true));
-    }
-
-    @JRubyMethod(name = "respond_to?", compat = RUBY1_9)
-    public IRubyObject respond_to_p19(IRubyObject mname) {
-        String name = mname.asJavaString();
-        IRubyObject respond = getRuntime().newBoolean(getMetaClass().isMethodBound(name, true));
-        if (!respond.isTrue()) {
-            respond = callMethod("respond_to_missing?", mname, getRuntime().getFalse());
-            respond = getRuntime().newBoolean(respond.isTrue());
-        }
-        return respond;
-    }
-
-    /** obj_respond_to
-     *
-     * respond_to?( aSymbol, includePriv=false ) -> true or false
-     *
-     * Returns true if this object responds to the given method. Private
-     * methods are included in the search only if the optional second
-     * parameter evaluates to true.
-     *
-     * @return true if this responds to the given method
-     *
-     * !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
-     *
-     * Going back to splitting according to method arity. MRI is wrong
-     * about most of these anyway, and since we have arity splitting
-     * in both the compiler and the interpreter, the performance
-     * benefit is important for this method.
-     */
-    @JRubyMethod(name = "respond_to?", compat = RUBY1_8)
-    public RubyBoolean respond_to_p(IRubyObject mname, IRubyObject includePrivate) {
-        String name = mname.asJavaString();
-        return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate.isTrue()));
-    }
-
-    @JRubyMethod(name = "respond_to?", compat = RUBY1_9)
-    public IRubyObject respond_to_p19(IRubyObject mname, IRubyObject includePrivate) {
-        String name = mname.asJavaString();
-        IRubyObject respond = getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate.isTrue()));
-        if (!respond.isTrue()) {
-            respond = callMethod("respond_to_missing?", mname, includePrivate);
-            respond = getRuntime().newBoolean(respond.isTrue());
-        }
-        return respond;
-    }
-
-    /** rb_obj_id
-     *
-     * Return the internal id of an object.
-     *
-     * FIXME: Should this be renamed to match its ruby name?
-     */
-     @JRubyMethod(name = {"object_id", "__id__"})
-     @Override
-     public IRubyObject id() {
-        return super.id();
-     }
-
-    /** rb_obj_id_obsolete
-     *
-     * Old id version. This one is bound to the "id" name and will emit a deprecation warning.
-     */
-    @JRubyMethod(name = "id", compat = RUBY1_8)
-    public IRubyObject id_deprecated() {
-        getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#id will be deprecated; use Object#object_id", "Object#id", "Object#object_id");
-        return id();
-    }
-
-    /** rb_obj_id
-     *
-     * Will return the hash code of this object. In comparison to MRI,
-     * this method will use the Java identity hash code instead of
-     * using rb_obj_id, since the usage of id in JRuby will incur the
-     * cost of some. ObjectSpace maintenance.
-     */
-    @JRubyMethod(name = "hash")
-    public RubyFixnum hash() {
-        return getRuntime().newFixnum(super.hashCode());
-    }
-
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
             RubyInteger integer = hashValue.convertToInteger();
             if (integer instanceof RubyBignum) {
                 return (int)integer.getBigIntegerValue().intValue();
             } else {
                 return (int)integer.getLongValue();
             }
         } else {
             hashValue = hashValue.callMethod(runtime.getCurrentContext(), "%", RubyFixnum.newFixnum(runtime, 536870923L));
             if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue);
             return System.identityHashCode(hashValue);
         }
     }
 
-    /** rb_obj_class
-     *
-     * Returns the real class of this object, excluding any
-     * singleton/meta class in the inheritance chain.
-     */
-    @JRubyMethod(name = "class")
-    public RubyClass type() {
-        return getMetaClass().getRealClass();
-    }
-
-    /** rb_obj_type
-     *
-     * The deprecated version of type, that emits a deprecation
-     * warning.
-     */
-    @JRubyMethod(name = "type")
-    public RubyClass type_deprecated() {
-        getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#type is deprecated; use Object#class", "Object#type", "Object#class");
-        return type();
-    }
-
-    /** rb_obj_clone
-     *
-     * This method should be overridden only by: Proc, Method,
-     * UnboundedMethod, Binding. It will use the defined allocated of
-     * the object, then clone the singleton class, taint the object,
-     * call initCopy and then copy frozen state.
-     */
-    @JRubyMethod(name = "clone")
-    @Override
-    public IRubyObject rbClone() {
-        return super.rbClone();
-    }
-
-    /** rb_obj_dup
-     *
-     * This method should be overridden only by: Proc
-     *
-     * Will allocate a new instance of the real class of this object,
-     * and then initialize that copy. It's different from {@link
-     * #rbClone} in that it doesn't copy the singleton class.
-     */
-    @JRubyMethod
-    @Override
-    public IRubyObject dup() {
-        return super.dup();
-    }
-
-    /** rb_obj_display
-     *
-     *  call-seq:
-     *     obj.display(port=$>)    => nil
-     *
-     *  Prints <i>obj</i> on the given port (default <code>$></code>).
-     *  Equivalent to:
-     *
-     *     def display(port=$>)
-     *       port.write self
-     *     end
-     *
-     *  For example:
-     *
-     *     1.display
-     *     "cat".display
-     *     [ 4, 5, 6 ].display
-     *     puts
-     *
-     *  <em>produces:</em>
-     *
-     *     1cat456
-     *
-     */
-    @JRubyMethod(name = "display", optional = 1)
-    public IRubyObject display(ThreadContext context, IRubyObject[] args) {
-        IRubyObject port = args.length == 0 ? context.getRuntime().getGlobalVariables().get("$>") : args[0];
-
-        port.callMethod(context, "write", this);
-
-        return context.getRuntime().getNil();
-    }
-
-    /** rb_obj_tainted
-     *
-     *  call-seq:
-     *     obj.tainted?    => true or false
-     *
-     *  Returns <code>true</code> if the object is tainted.
-     *
-     */
-    @JRubyMethod(name = "tainted?")
-    public RubyBoolean tainted_p(ThreadContext context) {
-        return context.getRuntime().newBoolean(isTaint());
-    }
-
-    /** rb_obj_taint
-     *
-     *  call-seq:
-     *     obj.taint -> obj
-     *
-     *  Marks <i>obj</i> as tainted---if the <code>$SAFE</code> level is
-     *  set appropriately, many method calls which might alter the running
-     *  programs environment will refuse to accept tainted strings.
-     */
-    @JRubyMethod(name = "taint")
-    public IRubyObject taint(ThreadContext context) {
-        taint(context.getRuntime());
-        return this;
-    }
-
-    /** rb_obj_untaint
-     *
-     *  call-seq:
-     *     obj.untaint    => obj
-     *
-     *  Removes the taint from <i>obj</i>.
-     *
-     *  Only callable in if more secure than 3.
-     */
-    @JRubyMethod(name = "untaint")
-    public IRubyObject untaint(ThreadContext context) {
-        context.getRuntime().secure(3);
-
-        if (isTaint()) {
-            testFrozen();
-            setTaint(false);
-        }
-
-        return this;
-    }
-
-    /** rb_obj_freeze
-     *
-     *  call-seq:
-     *     obj.freeze    => obj
-     *
-     *  Prevents further modifications to <i>obj</i>. A
-     *  <code>TypeError</code> will be raised if modification is attempted.
-     *  There is no way to unfreeze a frozen object. See also
-     *  <code>Object#frozen?</code>.
-     *
-     *     a = [ "a", "b", "c" ]
-     *     a.freeze
-     *     a << "z"
-     *
-     *  <em>produces:</em>
-     *
-     *     prog.rb:3:in `<<': can't modify frozen array (TypeError)
-     *     	from prog.rb:3
-     */
-    @JRubyMethod(name = "freeze")
-    public IRubyObject freeze(ThreadContext context) {
-        Ruby runtime = context.getRuntime();
-        if ((flags & FROZEN_F) == 0 && (runtime.is1_9() || !isImmediate())) {
-            if (runtime.getSafeLevel() >= 4 && !isTaint()) throw runtime.newSecurityError("Insecure: can't freeze object");
-            flags |= FROZEN_F;
-        }
-        return this;
-    }
-
-    /** rb_obj_frozen_p
-     *
-     *  call-seq:
-     *     obj.frozen?    => true or false
-     *
-     *  Returns the freeze status of <i>obj</i>.
-     *
-     *     a = [ "a", "b", "c" ]
-     *     a.freeze    #=> ["a", "b", "c"]
-     *     a.frozen?   #=> true
-     */
-    @JRubyMethod(name = "frozen?")
-    public RubyBoolean frozen_p(ThreadContext context) {
-        return context.getRuntime().newBoolean(isFrozen());
-    }
-
-    /** rb_obj_untrusted
-     *  call-seq:
-     *     obj.untrusted?    => true or false
-     *
-     *  Returns <code>true</code> if the object is untrusted.
-     */
-    @JRubyMethod(name = "untrusted?", compat = RUBY1_9)
-    public RubyBoolean untrusted_p(ThreadContext context) {
-        return context.getRuntime().newBoolean(isUntrusted());
-    }
-
-    /** rb_obj_untrust
-     *  call-seq:
-     *     obj.untrust -> obj
-     *
-     *  Marks <i>obj</i> as untrusted.
-     */
-    @JRubyMethod(compat = RUBY1_9)
-    public IRubyObject untrust(ThreadContext context) {
-        if (!isUntrusted() && !isImmediate()) {
-            checkFrozen();
-            flags |= UNTRUSTED_F;
-        }
-        return this;
-    }
-
-    /** rb_obj_trust
-     *  call-seq:
-     *     obj.trust    => obj
-     *
-     *  Removes the untrusted mark from <i>obj</i>.
-     */
-    @JRubyMethod(compat = RUBY1_9)
-    public IRubyObject trust(ThreadContext context) {
-        if (isUntrusted() && !isImmediate()) {
-            checkFrozen();
-            flags &= ~UNTRUSTED_F;
-        }
-        return this;
-    }
-
     /** rb_inspect
      *
      * The internal helper that ensures a RubyString instance is returned
      * so dangerous casting can be omitted
      * Prefered over callMethod(context, "inspect")
      */
     static RubyString inspect(ThreadContext context, IRubyObject object) {
         return RubyString.objAsString(context, object.callMethod(context, "inspect"));
     }
 
-    /** rb_obj_inspect
-     *
-     *  call-seq:
-     *     obj.inspect   => string
-     *
-     *  Returns a string containing a human-readable representation of
-     *  <i>obj</i>. If not overridden, uses the <code>to_s</code> method to
-     *  generate the string.
-     *
-     *     [ 1, 2, 3..4, 'five' ].inspect   #=> "[1, 2, 3..4, \"five\"]"
-     *     Time.new.inspect                 #=> "Wed Apr 09 08:54:39 CDT 2003"
-     */
-    @JRubyMethod(name = "inspect")
-    @Override
-    public IRubyObject inspect() {
-        return super.inspect();
-    }
-
-    /** rb_obj_is_instance_of
-     *
-     *  call-seq:
-     *     obj.instance_of?(class)    => true or false
-     *
-     *  Returns <code>true</code> if <i>obj</i> is an instance of the given
-     *  class. See also <code>Object#kind_of?</code>.
-     */
-    @JRubyMethod(name = "instance_of?", required = 1)
-    public RubyBoolean instance_of_p(ThreadContext context, IRubyObject type) {
-        if (type() == type) {
-            return context.getRuntime().getTrue();
-        } else if (!(type instanceof RubyModule)) {
-            throw context.getRuntime().newTypeError("class or module required");
-        } else {
-            return context.getRuntime().getFalse();
-        }
-    }
-
-
-    /** rb_obj_is_kind_of
-     *
-     *  call-seq:
-     *     obj.is_a?(class)       => true or false
-     *     obj.kind_of?(class)    => true or false
-     *
-     *  Returns <code>true</code> if <i>class</i> is the class of
-     *  <i>obj</i>, or if <i>class</i> is one of the superclasses of
-     *  <i>obj</i> or modules included in <i>obj</i>.
-     *
-     *     module M;    end
-     *     class A
-     *       include M
-     *     end
-     *     class B < A; end
-     *     class C < B; end
-     *     b = B.new
-     *     b.instance_of? A   #=> false
-     *     b.instance_of? B   #=> true
-     *     b.instance_of? C   #=> false
-     *     b.instance_of? M   #=> false
-     *     b.kind_of? A       #=> true
-     *     b.kind_of? B       #=> true
-     *     b.kind_of? C       #=> false
-     *     b.kind_of? M       #=> true
-     */
-    @JRubyMethod(name = {"kind_of?", "is_a?"}, required = 1)
-    public RubyBoolean kind_of_p(ThreadContext context, IRubyObject type) {
-        // TODO: Generalize this type-checking code into IRubyObject helper.
-        if (!(type instanceof RubyModule)) {
-            // TODO: newTypeError does not offer enough for ruby error string...
-            throw context.getRuntime().newTypeError("class or module required");
-        }
-
-        return context.getRuntime().newBoolean(((RubyModule)type).isInstance(this));
-    }
-
-    /** rb_obj_methods
-     *
-     *  call-seq:
-     *     obj.methods    => array
-     *
-     *  Returns a list of the names of methods publicly accessible in
-     *  <i>obj</i>. This will include all the methods accessible in
-     *  <i>obj</i>'s ancestors.
-     *
-     *     class Klass
-     *       def kMethod()
-     *       end
-     *     end
-     *     k = Klass.new
-     *     k.methods[0..9]    #=> ["kMethod", "freeze", "nil?", "is_a?",
-     *                             "class", "instance_variable_set",
-     *                              "methods", "extend", "__send__", "instance_eval"]
-     *     k.methods.length   #=> 42
-     */
-    @JRubyMethod(name = "methods", optional = 1, compat = RUBY1_8)
-    public IRubyObject methods(ThreadContext context, IRubyObject[] args) {
-        return methods(context, args, false);
-    }
-    @JRubyMethod(name = "methods", optional = 1, compat = RUBY1_9)
-    public IRubyObject methods19(ThreadContext context, IRubyObject[] args) {
-        return methods(context, args, true);
-    }
-
-    public IRubyObject methods(ThreadContext context, IRubyObject[] args, boolean useSymbols) {
-        boolean all = args.length == 1 ? args[0].isTrue() : true;
-        Ruby runtime = getRuntime();
-        RubyArray methods = runtime.newArray();
-        Set<String> seen = new HashSet<String>();
-
-        if (getMetaClass().isSingleton()) {
-            getMetaClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, false);
-            if (all) {
-                getMetaClass().getSuperClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, true);
-            }
-        } else if (all) {
-            getMetaClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, true);
-        } else {
-            // do nothing, leave empty
-        }
-
-        return methods;
-    }
-
-    /** rb_obj_public_methods
-     *
-     *  call-seq:
-     *     obj.public_methods(all=true)   => array
-     *
-     *  Returns the list of public methods accessible to <i>obj</i>. If
-     *  the <i>all</i> parameter is set to <code>false</code>, only those methods
-     *  in the receiver will be listed.
-     */
-    @JRubyMethod(name = "public_methods", optional = 1, compat = RUBY1_8)
-    public IRubyObject public_methods(ThreadContext context, IRubyObject[] args) {
-        return getMetaClass().public_instance_methods(trueIfNoArgument(context, args));
-    }
-
-    @JRubyMethod(name = "public_methods", optional = 1, compat = RUBY1_9)
-    public IRubyObject public_methods19(ThreadContext context, IRubyObject[] args) {
-        return getMetaClass().public_instance_methods19(trueIfNoArgument(context, args));
-    }
-
-    /** rb_obj_protected_methods
-     *
-     *  call-seq:
-     *     obj.protected_methods(all=true)   => array
-     *
-     *  Returns the list of protected methods accessible to <i>obj</i>. If
-     *  the <i>all</i> parameter is set to <code>false</code>, only those methods
-     *  in the receiver will be listed.
-     *
-     *  Internally this implementation uses the
-     *  {@link RubyModule#protected_instance_methods} method.
-     */
-    @JRubyMethod(name = "protected_methods", optional = 1, compat = RUBY1_8)
-    public IRubyObject protected_methods(ThreadContext context, IRubyObject[] args) {
-        return getMetaClass().protected_instance_methods(trueIfNoArgument(context, args));
-    }
-
-    @JRubyMethod(name = "protected_methods", optional = 1, compat = RUBY1_9)
-    public IRubyObject protected_methods19(ThreadContext context, IRubyObject[] args) {
-        return getMetaClass().protected_instance_methods19(trueIfNoArgument(context, args));
-    }
-
-    /** rb_obj_private_methods
-     *
-     *  call-seq:
-     *     obj.private_methods(all=true)   => array
-     *
-     *  Returns the list of private methods accessible to <i>obj</i>. If
-     *  the <i>all</i> parameter is set to <code>false</code>, only those methods
-     *  in the receiver will be listed.
-     *
-     *  Internally this implementation uses the
-     *  {@link RubyModule#private_instance_methods} method.
-     */
-    @JRubyMethod(name = "private_methods", optional = 1, compat = RUBY1_8)
-    public IRubyObject private_methods(ThreadContext context, IRubyObject[] args) {
-        return getMetaClass().private_instance_methods(trueIfNoArgument(context, args));
-    }
-
-    @JRubyMethod(name = "private_methods", optional = 1, compat = RUBY1_9)
-    public IRubyObject private_methods19(ThreadContext context, IRubyObject[] args) {
-        return getMetaClass().private_instance_methods19(trueIfNoArgument(context, args));
-    }
-
-    // FIXME: If true array is common enough we should pre-allocate and stick somewhere
-    private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
-        return args.length == 0 ? new IRubyObject[] { context.getRuntime().getTrue() } : args;
-    }
-
-    /** rb_obj_singleton_methods
-     *
-     *  call-seq:
-     *     obj.singleton_methods(all=true)    => array
-     *
-     *  Returns an array of the names of singleton methods for <i>obj</i>.
-     *  If the optional <i>all</i> parameter is true, the list will include
-     *  methods in modules included in <i>obj</i>.
-     *
-     *     module Other
-     *       def three() end
-     *     end
-     *
-     *     class Single
-     *       def Single.four() end
-     *     end
-     *
-     *     a = Single.new
-     *
-     *     def a.one()
-     *     end
-     *
-     *     class << a
-     *       include Other
-     *       def two()
-     *       end
-     *     end
-     *
-     *     Single.singleton_methods    #=> ["four"]
-     *     a.singleton_methods(false)  #=> ["two", "one"]
-     *     a.singleton_methods         #=> ["two", "one", "three"]
-     */
-    // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
-    @JRubyMethod(name = "singleton_methods", optional = 1, compat = RUBY1_8)
-    public RubyArray singleton_methods(ThreadContext context, IRubyObject[] args) {
-        return singletonMethods(context, args, methodsCollector);
-    }
-
-    @JRubyMethod(name = "singleton_methods", optional = 1 , compat = RUBY1_9)
-    public RubyArray singleton_methods19(ThreadContext context, IRubyObject[] args) {
-        return singletonMethods(context, args, methodsCollector19);
-    }
-
-    public RubyArray singletonMethods(ThreadContext context, IRubyObject[] args, MethodsCollector collect) {
-        boolean all = true;
-        if(args.length == 1) {
-            all = args[0].isTrue();
-        }
-
-        RubyArray singletonMethods;
-        if (getMetaClass().isSingleton()) {
-            IRubyObject[] methodsArgs = new IRubyObject[]{context.getRuntime().getFalse()};
-            singletonMethods = collect.instanceMethods(getMetaClass(), methodsArgs);
-            
-            if (all) {
-                RubyClass superClass = getMetaClass().getSuperClass();
-                while (superClass.isSingleton() || superClass.isIncluded()) {
-                    singletonMethods.concat(collect.instanceMethods(superClass, methodsArgs));
-                    superClass = superClass.getSuperClass();
-                }
-            }
-        } else {
-            singletonMethods = context.getRuntime().newEmptyArray();
-        }
-
-        return singletonMethods;
-    }
-
-    private abstract static class MethodsCollector {
-        public abstract RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args);
-    };
-
-    private static final MethodsCollector methodsCollector = new MethodsCollector() {
-        public RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args) {
-            return rubyClass.instance_methods(args);
-        }
-    };
-
-    private static final MethodsCollector methodsCollector19 = new MethodsCollector() {
-        public RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args) {
-            return rubyClass.instance_methods19(args);
-        }
-    };
-
-    /** rb_obj_method
-     *
-     *  call-seq:
-     *     obj.method(sym)    => method
-     *
-     *  Looks up the named method as a receiver in <i>obj</i>, returning a
-     *  <code>Method</code> object (or raising <code>NameError</code>). The
-     *  <code>Method</code> object acts as a closure in <i>obj</i>'s object
-     *  instance, so instance variables and the value of <code>self</code>
-     *  remain available.
-     *
-     *     class Demo
-     *       def initialize(n)
-     *         @iv = n
-     *       end
-     *       def hello()
-     *         "Hello, @iv = #{@iv}"
-     *       end
-     *     end
-     *
-     *     k = Demo.new(99)
-     *     m = k.method(:hello)
-     *     m.call   #=> "Hello, @iv = 99"
-     *
-     *     l = Demo.new('Fred')
-     *     m = l.method("hello")
-     *     m.call   #=> "Hello, @iv = Fred"
-     */
-    @JRubyMethod(name = "method", required = 1)
-    public IRubyObject method(IRubyObject symbol) {
-        return getMetaClass().newMethod(this, symbol.asJavaString(), true, null);
-    }
-
-    @JRubyMethod(name = "method", required = 1, compat = RUBY1_9)
-    public IRubyObject method19(IRubyObject symbol) {
-        return getMetaClass().newMethod(this, symbol.asJavaString(), true, null, true);
-    }
-
-    /** rb_any_to_s
-     *
-     *  call-seq:
-     *     obj.to_s    => string
-     *
-     *  Returns a string representing <i>obj</i>. The default
-     *  <code>to_s</code> prints the object's class and an encoding of the
-     *  object id. As a special case, the top-level object that is the
-     *  initial execution context of Ruby programs returns ``main.''
-     */
-    @JRubyMethod(name = "to_s")
-    public IRubyObject to_s() {
-    	return anyToString();
-    }
-
-    /** rb_any_to_a
-     *
-     *  call-seq:
-     *     obj.to_a -> anArray
-     *
-     *  Returns an array representation of <i>obj</i>. For objects of class
-     *  <code>Object</code> and others that don't explicitly override the
-     *  method, the return value is an array containing <code>self</code>.
-     *  However, this latter behavior will soon be obsolete.
-     *
-     *     self.to_a       #=> -:1: warning: default `to_a' will be obsolete
-     *     "hello".to_a    #=> ["hello"]
-     *     Time.new.to_a   #=> [39, 54, 8, 9, 4, 2003, 3, 99, true, "CDT"]
-     *
-     *  The default to_a method is deprecated.
-     */
-    @JRubyMethod(name = "to_a", visibility = PUBLIC, compat = RUBY1_8)
-    public RubyArray to_a() {
-        getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "default 'to_a' will be obsolete", "to_a");
-        return getRuntime().newArray(this);
-    }
-
-    /** rb_obj_instance_eval
-     *
-     *  call-seq:
-     *     obj.instance_eval(string [, filename [, lineno]] )   => obj
-     *     obj.instance_eval {| | block }                       => obj
-     *
-     *  Evaluates a string containing Ruby source code, or the given block,
-     *  within the context of the receiver (_obj_). In order to set the
-     *  context, the variable +self+ is set to _obj_ while
-     *  the code is executing, giving the code access to _obj_'s
-     *  instance variables. In the version of <code>instance_eval</code>
-     *  that takes a +String+, the optional second and third
-     *  parameters supply a filename and starting line number that are used
-     *  when reporting compilation errors.
-     *
-     *     class Klass
-     *       def initialize
-     *         @secret = 99
-     *       end
-     *     end
-     *     k = Klass.new
-     *     k.instance_eval { @secret }   #=> 99
-     */
-    @JRubyMethod(compat = RUBY1_8)
-    public IRubyObject instance_eval(ThreadContext context, Block block) {
-        return specificEval(context, getInstanceEvalClass(), block);
-    }
-    @JRubyMethod(compat = RUBY1_8)
-    public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, Block block) {
-        return specificEval(context, getInstanceEvalClass(), arg0, block);
-    }
-    @JRubyMethod(compat = RUBY1_8)
-    public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
-        return specificEval(context, getInstanceEvalClass(), arg0, arg1, block);
-    }
-    @JRubyMethod(compat = RUBY1_8)
-    public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
-        return specificEval(context, getInstanceEvalClass(), arg0, arg1, arg2, block);
-    }
-
-    /** rb_obj_instance_exec
-     *
-     *  call-seq:
-     *     obj.instance_exec(arg...) {|var...| block }                       => obj
-     *
-     *  Executes the given block within the context of the receiver
-     *  (_obj_). In order to set the context, the variable +self+ is set
-     *  to _obj_ while the code is executing, giving the code access to
-     *  _obj_'s instance variables.  Arguments are passed as block parameters.
-     *
-     *     class Klass
-     *       def initialize
-     *         @secret = 99
-     *       end
-     *     end
-     *     k = Klass.new
-     *     k.instance_exec(5) {|x| @secret+x }   #=> 104
-     */
-    @JRubyMethod(optional = 3, rest = true, compat = RUBY1_8)
-    public IRubyObject instance_exec(ThreadContext context, IRubyObject[] args, Block block) {
-        if (!block.isGiven()) throw context.getRuntime().newArgumentError("block not supplied");
-
-        RubyModule klazz;
-        if (isImmediate()) {
-            // Ruby uses Qnil here, we use "dummy" because we need a class
-            klazz = context.getRuntime().getDummy();
-        } else {
-            klazz = getSingletonClass();
-        }
-
-        return yieldUnder(context, klazz, args, block);
-    }
-
-    /** rb_obj_extend
-     *
-     *  call-seq:
-     *     obj.extend(module, ...)    => obj
-     *
-     *  Adds to _obj_ the instance methods from each module given as a
-     *  parameter.
-     *
-     *     module Mod
-     *       def hello
-     *         "Hello from Mod.\n"
-     *       end
-     *     end
-     *
-     *     class Klass
-     *       def hello
-     *         "Hello from Klass.\n"
-     *       end
-     *     end
-     *
-     *     k = Klass.new
-     *     k.hello         #=> "Hello from Klass.\n"
-     *     k.extend(Mod)   #=> #<Klass:0x401b3bc8>
-     *     k.hello         #=> "Hello from Mod.\n"
-     */
-    @JRubyMethod(name = "extend", required = 1, rest = true)
-    public IRubyObject extend(IRubyObject[] args) {
-        Ruby runtime = getRuntime();
-
-        // Make sure all arguments are modules before calling the callbacks
-        for (int i = 0; i < args.length; i++) {
-            if (!args[i].isModule()) throw runtime.newTypeError(args[i], runtime.getModule());
-        }
-
-        ThreadContext context = runtime.getCurrentContext();
-
-        // MRI extends in order from last to first
-        for (int i = args.length - 1; i >= 0; i--) {
-            args[i].callMethod(context, "extend_object", this);
-            args[i].callMethod(context, "extended", this);
-        }
-        return this;
-    }
-
-    /** rb_obj_dummy
-     *
-     * Default initialize method. This one gets defined in some other
-     * place as a Ruby method.
-     */
-    public IRubyObject initialize() {
-        return getRuntime().getNil();
-    }
-
-    /** rb_f_send
-     *
-     * send( aSymbol  [, args  ]*   ) -> anObject
-     *
-     * Invokes the method identified by aSymbol, passing it any arguments
-     * specified. You can use __send__ if the name send clashes with an
-     * existing method in this object.
-     *
-     * <pre>
-     * class Klass
-     *   def hello(*args)
-     *     "Hello " + args.join(' ')
-     *   end
-     * end
-     *
-     * k = Klass.new
-     * k.send :hello, "gentle", "readers"
-     * </pre>
-     *
-     * @return the result of invoking the method identified by aSymbol.
-     */
-    @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
-    public IRubyObject send(ThreadContext context, Block block) {
-        throw context.getRuntime().newArgumentError(0, 1);
-    }
-    @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
-    public IRubyObject send(ThreadContext context, IRubyObject arg0, Block block) {
-        String name = arg0.asJavaString();
-
-        return getMetaClass().finvoke(context, this, name, block);
-    }
-    @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
-    public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
-        String name = arg0.asJavaString();
-
-        return getMetaClass().finvoke(context, this, name, arg1, block);
-    }
-    @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
-    public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
-        String name = arg0.asJavaString();
-
-        return getMetaClass().finvoke(context, this, name, arg1, arg2, block);
-    }
-    @JRubyMethod(name = {"send", "__send__"}, rest = true, compat = RUBY1_8)
-    public IRubyObject send(ThreadContext context, IRubyObject[] args, Block block) {
-        String name = args[0].asJavaString();
-        int newArgsLength = args.length - 1;
-
-        IRubyObject[] newArgs;
-        if (newArgsLength == 0) {
-            newArgs = IRubyObject.NULL_ARRAY;
-        } else {
-            newArgs = new IRubyObject[newArgsLength];
-            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
-        }
-
-        return getMetaClass().finvoke(context, this, name, newArgs, block);
-    }
-
-    @JRubyMethod(name = {"send"}, compat = RUBY1_9)
-    @Override
-    public IRubyObject send19(ThreadContext context, Block block) {
-        return super.send19(context, block);
-    }
-    @JRubyMethod(name = {"send"}, compat = RUBY1_9)
-    @Override
-    public IRubyObject send19(ThreadContext context, IRubyObject arg0, Block block) {
-        return super.send19(context, arg0, block);
-    }
-    @JRubyMethod(name = {"send"}, compat = RUBY1_9)
-    @Override
-    public IRubyObject send19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
-        return super.send19(context, arg0, arg1, block);
-    }
-    @JRubyMethod(name = {"send"}, compat = RUBY1_9)
-    @Override
-    public IRubyObject send19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
-        return super.send19(context, arg0, arg1, arg2, block);
-    }
-    @JRubyMethod(name = {"send"}, rest = true, compat = RUBY1_9)
-    @Override
-    public IRubyObject send19(ThreadContext context, IRubyObject[] args, Block block) {
-        return super.send19(context, args, block);
-    }
-
-    /** rb_false
-     *
-     * call_seq:
-     *   nil.nil?               => true
-     *   <anything_else>.nil?   => false
-     *
-     * Only the object <i>nil</i> responds <code>true</code> to <code>nil?</code>.
-     */
-    @JRubyMethod(name = "nil?")
-    public IRubyObject nil_p(ThreadContext context) {
-    	return context.getRuntime().getFalse();
-    }
-
-    /** rb_obj_pattern_match
-     *
-     *  call-seq:
-     *     obj =~ other  => false
-     *
-     *  Pattern Match---Overridden by descendents (notably
-     *  <code>Regexp</code> and <code>String</code>) to provide meaningful
-     *  pattern-match semantics.
-     */
-    @JRubyMethod(name = "=~", required = 1, compat = RUBY1_8)
-    public IRubyObject op_match(ThreadContext context, IRubyObject arg) {
-    	return context.getRuntime().getFalse();
-    }
-
-    @JRubyMethod(name = "=~", required = 1, compat = RUBY1_9)
-    public IRubyObject op_match19(ThreadContext context, IRubyObject arg) {
-    	return context.getRuntime().getNil();
-    }
-
-    @JRubyMethod(name = "!~", required = 1, compat = RUBY1_9)
-    public IRubyObject op_not_match(ThreadContext context, IRubyObject arg) {
-        return context.getRuntime().newBoolean(! callMethod(context, "=~", arg).isTrue());
-    }
-
-
-    //
-    // INSTANCE VARIABLE RUBY METHODS
-    //
-
-    /** rb_obj_ivar_defined
-     *
-     *  call-seq:
-     *     obj.instance_variable_defined?(symbol)    => true or false
-     *
-     *  Returns <code>true</code> if the given instance variable is
-     *  defined in <i>obj</i>.
-     *
-     *     class Fred
-     *       def initialize(p1, p2)
-     *         @a, @b = p1, p2
-     *       end
-     *     end
-     *     fred = Fred.new('cat', 99)
-     *     fred.instance_variable_defined?(:@a)    #=> true
-     *     fred.instance_variable_defined?("@b")   #=> true
-     *     fred.instance_variable_defined?("@c")   #=> false
-     */
-    @JRubyMethod(name = "instance_variable_defined?", required = 1)
-    public IRubyObject instance_variable_defined_p(ThreadContext context, IRubyObject name) {
-        if (variableTableContains(validateInstanceVariable(name.asJavaString()))) {
-            return context.getRuntime().getTrue();
-        }
-        return context.getRuntime().getFalse();
-    }
-
-    /** rb_obj_ivar_get
-     *
-     *  call-seq:
-     *     obj.instance_variable_get(symbol)    => obj
-     *
-     *  Returns the value of the given instance variable, or nil if the
-     *  instance variable is not set. The <code>@</code> part of the
-     *  variable name should be included for regular instance
-     *  variables. Throws a <code>NameError</code> exception if the
-     *  supplied symbol is not valid as an instance variable name.
-     *
-     *     class Fred
-     *       def initialize(p1, p2)
-     *         @a, @b = p1, p2
-     *       end
-     *     end
-     *     fred = Fred.new('cat', 99)
-     *     fred.instance_variable_get(:@a)    #=> "cat"
-     *     fred.instance_variable_get("@b")   #=> 99
-     */
-    @JRubyMethod(name = "instance_variable_get", required = 1)
-    public IRubyObject instance_variable_get(ThreadContext context, IRubyObject name) {
-        Object value;
-        if ((value = variableTableFetch(validateInstanceVariable(name.asJavaString()))) != null) {
-            return (IRubyObject)value;
-        }
-        return context.getRuntime().getNil();
-    }
-
-    /** rb_obj_ivar_set
-     *
-     *  call-seq:
-     *     obj.instance_variable_set(symbol, obj)    => obj
-     *
-     *  Sets the instance variable names by <i>symbol</i> to
-     *  <i>object</i>, thereby frustrating the efforts of the class's
-     *  author to attempt to provide proper encapsulation. The variable
-     *  did not have to exist prior to this call.
-     *
-     *     class Fred
-     *       def initialize(p1, p2)
-     *         @a, @b = p1, p2
-     *       end
-     *     end
-     *     fred = Fred.new('cat', 99)
-     *     fred.instance_variable_set(:@a, 'dog')   #=> "dog"
-     *     fred.instance_variable_set(:@c, 'cat')   #=> "cat"
-     *     fred.inspect                             #=> "#<Fred:0x401b3da8 @a=\"dog\", @b=99, @c=\"cat\">"
-     */
-    @JRubyMethod(name = "instance_variable_set", required = 2)
-    public IRubyObject instance_variable_set(IRubyObject name, IRubyObject value) {
-        ensureInstanceVariablesSettable();
-        return (IRubyObject)variableTableStore(validateInstanceVariable(name.asJavaString()), value);
-    }
-
-    /** rb_obj_remove_instance_variable
-     *
-     *  call-seq:
-     *     obj.remove_instance_variable(symbol)    => obj
-     *
-     *  Removes the named instance variable from <i>obj</i>, returning that
-     *  variable's value.
-     *
-     *     class Dummy
-     *       attr_reader :var
-     *       def initialize
-     *         @var = 99
-     *       end
-     *       def remove
-     *         remove_instance_variable(:@var)
-     *       end
-     *     end
-     *     d = Dummy.new
-     *     d.var      #=> 99
-     *     d.remove   #=> 99
-     *     d.var      #=> nil
-     */
-    @JRubyMethod(visibility = PRIVATE)
-    public IRubyObject remove_instance_variable(ThreadContext context, IRubyObject name, Block block) {
-        ensureInstanceVariablesSettable();
-        IRubyObject value;
-        if ((value = (IRubyObject)variableTableRemove(validateInstanceVariable(name.asJavaString()))) != null) {
-            return value;
-        }
-        throw context.getRuntime().newNameError("instance variable " + name.asJavaString() + " not defined", name.asJavaString());
-    }
-
-    /** rb_obj_instance_variables
-     *
-     *  call-seq:
-     *     obj.instance_variables    => array
-     *
-     *  Returns an array of instance variable names for the receiver. Note
-     *  that simply defining an accessor does not create the corresponding
-     *  instance variable.
-     *
-     *     class Fred
-     *       attr_accessor :a1
-     *       def initialize
-     *         @iv = 3
-     *       end
-     *     end
-     *     Fred.new.instance_variables   #=> ["@iv"]
-     */
-    @JRubyMethod(name = "instance_variables")
-    public RubyArray instance_variables(ThreadContext context) {
-        Ruby runtime = context.getRuntime();
-        List<String> nameList = getInstanceVariableNameList();
-
-        RubyArray array = runtime.newArray(nameList.size());
-
-        for (String name : nameList) {
-            array.append(runtime.newString(name));
-        }
-
-        return array;
-    }
-
-    // In 1.9, return symbols
-    @JRubyMethod(name = "instance_variables", compat = RUBY1_9)
-    public RubyArray instance_variables19(ThreadContext context) {
-        Ruby runtime = context.getRuntime();
-        List<String> nameList = getInstanceVariableNameList();
-
-        RubyArray array = runtime.newArray(nameList.size());
-
-        for (String name : nameList) {
-            array.append(runtime.newSymbol(name));
-        }
-
-        return array;
-    }
-
-    /**
-     * Checks if the name parameter represents a legal instance variable name, and otherwise throws a Ruby NameError
-     */
-    protected String validateInstanceVariable(String name) {
-        if (IdUtil.isValidInstanceVariableName(name)) return name;
-
-        throw getRuntime().newNameError("`" + name + "' is not allowable as an instance variable name", name);
-    }
-
     /**
      * Tries to support Java serialization of Ruby objects. This is
      * still experimental and might not work.
      */
     // NOTE: Serialization is primarily supported for testing purposes, and there is no general
     // guarantee that serialization will work correctly. Specifically, instance variables pointing
     // at symbols, threads, modules, classes, and other unserializable types are not detected.
     private void writeObject(ObjectOutputStream out) throws IOException {
         out.defaultWriteObject();
         // write out ivar count followed by name/value pairs
         List<String> names = getInstanceVariableNameList();
         out.writeInt(names.size());
         for (String name : names) {
             out.writeObject(name);
             out.writeObject(getInstanceVariables().getInstanceVariable(name));
         }
     }
 
     /**
      * Tries to support Java unserialization of Ruby objects. This is
      * still experimental and might not work.
      */
     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         // rest in ivar count followed by name/value pairs
         int ivarCount = in.readInt();
         for (int i = 0; i < ivarCount; i++) {
             setInstanceVariable((String)in.readObject(), (IRubyObject)in.readObject());
         }
     }
 
 }
