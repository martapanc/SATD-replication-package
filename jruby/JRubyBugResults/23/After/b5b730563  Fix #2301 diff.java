diff --git a/core/src/main/java/org/jruby/RubyBasicObject.java b/core/src/main/java/org/jruby/RubyBasicObject.java
index 9472504ade..fa432cc854 100644
--- a/core/src/main/java/org/jruby/RubyBasicObject.java
+++ b/core/src/main/java/org/jruby/RubyBasicObject.java
@@ -691,2043 +691,2043 @@ public class RubyBasicObject implements Cloneable, IRubyObject, Serializable, Co
 
         if (!(str instanceof RubyString)) return (RubyString)anyToString();
         if (isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
  /**
      * Tries to convert this object to a Ruby Array using the "to_ary"
      * method.
      */
     @Override
     public RubyArray convertToArray() {
         return (RubyArray) TypeConverter.convertToType(this, getRuntime().getArray(), "to_ary");
     }
 
     /**
      * Tries to convert this object to a Ruby Hash using the "to_hash"
      * method.
      */
     @Override
     public RubyHash convertToHash() {
         return (RubyHash)TypeConverter.convertToType(this, getRuntime().getHash(), "to_hash");
     }
 
     /**
      * Tries to convert this object to a Ruby Float using the "to_f"
      * method.
      */
     @Override
     public RubyFloat convertToFloat() {
         return (RubyFloat) TypeConverter.convertToType(this, getRuntime().getFloat(), "to_f");
     }
 
     /**
      * Tries to convert this object to a Ruby Integer using the "to_int"
      * method.
      */
     @Override
     public RubyInteger convertToInteger() {
         return convertToInteger("to_int");
     }
 
     /**
      * Tries to convert this object to a Ruby Integer using the
      * supplied conversion method.
      */
     @Override
     public RubyInteger convertToInteger(String convertMethod) {
         IRubyObject val = TypeConverter.convertToType(this, getRuntime().getInteger(), convertMethod, true);
         if (!(val instanceof RubyInteger)) throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod + " should return Integer");
         return (RubyInteger)val;
     }
 
     /**
      * Tries to convert this object to a Ruby String using the
      * "to_str" method.
      */
     @Override
     public RubyString convertToString() {
         return (RubyString) TypeConverter.convertToType(this, getRuntime().getString(), "to_str");
     }
 
     /**
      * Internal method that helps to convert any object into the
      * format of a class name and a hex string inside of #<>.
      */
     @Override
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
     @Override
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
     @Override
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
     @Override
     public IRubyObject checkArrayType() {
         return TypeConverter.convertToTypeWithCheck(this, getRuntime().getArray(), "to_ary");
     }
 
     /**
      * @see IRubyObject#toJava
      */
     @Override
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
 
     @Override
     public IRubyObject dup() {
         Ruby runtime = getRuntime();
 
         if (isImmediate()) throw runtime.newTypeError("can't dup " + getMetaClass().getName());
 
         IRubyObject dup = getMetaClass().getRealClass().allocate();
         if (isTaint()) dup.setTaint(true);
 
         initCopy(dup, this, "initialize_dup");
 
         return dup;
     }
 
     /** init_copy
      *
      * Initializes a copy with variable and special instance variable
      * information, and then call the initialize_copy Ruby method.
      */
     private static void initCopy(IRubyObject clone, IRubyObject original, String method) {
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         original.copySpecialInstanceVariables(clone);
 
         if (original.hasVariables()) clone.syncVariables(original);
         if (original instanceof RubyModule) {
             RubyModule cloneMod = (RubyModule)clone;
             cloneMod.syncConstants((RubyModule)original);
             cloneMod.syncClassVariables((RubyModule)original);
         }
 
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), method, original);
     }
 
     protected static boolean OBJ_INIT_COPY(IRubyObject obj, IRubyObject orig) {
         if (obj == orig) return false;
 
         objInitCopy(obj, orig);
         return true;
     }
 
     protected static void objInitCopy(IRubyObject obj, IRubyObject orig) {
         if (obj == orig) return;
         // FIXME: booooo!
         ((RubyBasicObject)obj).checkFrozen();
         // Not implemented
 //        checkTrusted();
         if (obj.getClass() != orig.getClass() || obj.getMetaClass().getRealClass() != orig.getMetaClass().getRealClass()) {
             throw obj.getRuntime().newTypeError("initialize_copy should take same class object");
         }
     }
 
     /**
      * Lots of MRI objects keep their state in non-lookupable ivars
      * (e:g. Range, Struct, etc). This method is responsible for
      * dupping our java field equivalents
      */
     @Override
     public void copySpecialInstanceVariables(IRubyObject clone) {
     }
 
     /** rb_inspect
      *
      * The internal helper that ensures a RubyString instance is returned
      * so dangerous casting can be omitted
      * Prefered over callMethod(context, "inspect")
      */
     static RubyString inspect(ThreadContext context, IRubyObject object) {
         return RubyString.objAsString(context, invokedynamic(context, object, INSPECT));
     }
 
     @Override
     public IRubyObject rbClone() {
         Ruby runtime = getRuntime();
 
         if (isImmediate()) throw runtime.newTypeError("can't clone " + getMetaClass().getName());
 
         // We're cloning ourselves, so we know the result should be a RubyObject
         RubyBasicObject clone = (RubyBasicObject)getMetaClass().getRealClass().allocate();
         clone.setMetaClass(getSingletonClassClone());
         if (isTaint()) clone.setTaint(true);
 
         initCopy(clone, this, "initialize_clone");
 
         if (isFrozen()) clone.setFrozen(true);
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
     @Override
     public boolean isModule() {
         return false;
     }
 
     /**
      * Specifically polymorphic method that are meant to be overridden
      * by classes to specify that they are classes in an easy way.
      */
     @Override
     public boolean isClass() {
         return false;
     }
 
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct(Object)
      */
     @Override
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
     @Override
     public synchronized Object dataGetStruct() {
         return getInternalVariable("__wrap_struct__");
     }
 
     // Equivalent of Data_Get_Struct
     // This will first check that the object in question is actually a T_DATA equivalent.
     @Override
     public synchronized Object dataGetStructChecked() {
         TypeConverter.checkData(this);
         return getInternalVariable("__wrap_struct__");
     }
 
     /** rb_obj_id
      *
      * Return the internal id of an object.
      */
     @JRubyMethod(name = {"object_id", "__id__"})
     @Override
     public IRubyObject id() {
         return getRuntime().newFixnum(getObjectId());
     }
 
     /** rb_obj_itself
      *
      * Identity method for the object.
      */
     @JRubyMethod
     public IRubyObject itself() {
         return this;
     }
 
     /**
      * The logic here is to use the special objectId accessor slot from the
      * parent as a lazy store for an object ID. IDs are generated atomically,
      * in serial, and guaranteed unique for up to 2^63 objects. The special
      * objectId slot is managed separately from the "normal" vars so it
      * does not marshal, clone/dup, or refuse to be initially set when the
      * object is frozen.
      */
     protected long getObjectId() {
         return metaClass.getRealClass().getVariableTableManager().getObjectId(this);
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
     @Override
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         if ((!isImmediate()) && !(this instanceof RubyModule) && hasVariables()) {
             return hashyInspect();
         } else {
             if (isNil()) return RubyNil.inspect(runtime.getCurrentContext(), this);
             return to_s();
         }
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
 
     // MRI: rb_inspect, which does dispatch
     public static IRubyObject rbInspect(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.runtime;
         RubyString str = obj.callMethod(context, "inspect").asString();
         Encoding ext = EncodingUtils.defaultExternalEncoding(runtime);
         if (!ext.isAsciiCompatible()) {
             if (!str.isAsciiOnly())
                 throw runtime.newEncodingCompatibilityError("inspected result must be ASCII only if default external encoding is ASCII incompatible");
             return str;
         }
         if (str.getEncoding() != ext && !str.isAsciiOnly())
             throw runtime.newEncodingCompatibilityError("inspected result must be ASCII only or use the default external encoding");
         return str;
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
 
         for (Map.Entry<String, VariableAccessor> entry : metaClass.getVariableTableManager().getVariableAccessorsForRead().entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             
             part.append(sep).append(" ").append(entry.getKey()).append("=");
             part.append(invokedynamic(context, (IRubyObject)value, INSPECT));
             sep = ",";
         }
         part.append(">");
         return part;
     }
 
     // Methods of the Object class (rb_obj_*):
 
 
     @JRubyMethod(name = "!")
     public IRubyObject op_not(ThreadContext context) {
         return context.runtime.newBoolean(!this.isTrue());
     }
 
     @JRubyMethod(name = "!=", required = 1)
     public IRubyObject op_not_equal(ThreadContext context, IRubyObject other) {
         return context.runtime.newBoolean(!invokedynamic(context, this, OP_EQUAL, other).isTrue());
     }
 
     /**
      * Compares this Ruby object with another.
      *
      * @param other another IRubyObject
      * @return 0 if equal,
      *         &lt; 0 if this is less than other,
      *         &gt; 0 if this is greater than other
      * @throws IllegalArgumentException if the objects cannot be compared.
      */
     @Override
     public int compareTo(IRubyObject other) {
         // SSS FIXME: How do we get access to the runtime here?
         // IRubyObject oldExc = runtime.getGlobalVariables().get("$!");
         try {
             IRubyObject cmp = invokedynamic(getRuntime().getCurrentContext(),
                     this, OP_CMP, other);
             
             // if RubyBasicObject#op_cmp is used, the result may be nil
             if (!cmp.isNil()) {
                 return (int) cmp.convertToInteger().getLongValue();
             }
         } catch (RaiseException ex) {
             // runtime.getGlobalVariables().set("$!", oldExc);
         }
         
         /* We used to raise an error if two IRubyObject were not comparable, but
          * in order to support the new ConcurrentHashMapV8 and other libraries
          * and containers that arbitrarily call compareTo expecting it to always
          * succeed, we have opted to return 0 here. This will allow all
          * RubyBasicObject subclasses to be compared, but if the comparison is
          * not valid we they will appear the same for sorting purposes.
          * 
          * See https://jira.codehaus.org/browse/JRUBY-7013
          */
         return 0;
     }
 
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         return op_equal_19(context, obj);
     }
 
     /** rb_obj_equal
      *
      * Will by default use identity equality to compare objects. This
      * follows the Ruby semantics.
      *
      * The name of this method doesn't follow the convention because hierarchy problems
      */
     @JRubyMethod(name = "==")
     public IRubyObject op_equal_19(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.runtime.getTrue() : context.runtime.getFalse();
     }
 
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         // Remain unimplemented due to problems with the double java hierarchy
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "equal?", required = 1)
     public IRubyObject equal_p19(ThreadContext context, IRubyObject other) {
         return op_equal_19(context, other);
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "==" method.
      */
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that == other || invokedynamic(context, that, OP_EQUAL, other).isTrue();
     }
 
     /** method used for Hash key comparison (specialized for String, Symbol and Fixnum)
      *
      * Will by default just call the Ruby method "eql?"
      */
     @Override
     public boolean eql(IRubyObject other) {
         return invokedynamic(getRuntime().getCurrentContext(), this, EQL, other).isTrue();
     }
 
     /**
      * Adds the specified object as a finalizer for this object.
      */
     @Override
     public void addFinalizer(IRubyObject f) {
         Finalizer finalizer = (Finalizer)getInternalVariable("__finalizer__");
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
     @Override
     public void removeFinalizers() {
         Finalizer finalizer = (Finalizer)getInternalVariable("__finalizer__");
         if (finalizer != null) {
             finalizer.removeFinalizers();
             removeInternalVariable("__finalizer__");
             getRuntime().removeFinalizer(finalizer);
         }
     }
 
     @Override
     public Object getVariable(int index) {
         return VariableAccessor.getVariable(this, index);
     }
     
     @Override
     public void setVariable(int index, Object value) {
         ensureInstanceVariablesSettable();
         if (index < 0) return;
         metaClass.getVariableTableManager().setVariableInternal(this, index, value);
     }
 
     public final Object getFFIHandle() {
         return metaClass.getVariableTableManager().getFFIHandle(this);
     }
 
     public final void setFFIHandle(Object value) {
         metaClass.getVariableTableManager().setFFIHandle(this, value);
     }
 
     //
     // COMMON VARIABLE METHODS
     //
 
     /**
      * Returns true if object has any variables
      * 
      * @see VariableTableManager#hasVariables(org.jruby.RubyBasicObject) 
      */
     @Override
     public boolean hasVariables() {
         return metaClass.getVariableTableManager().hasVariables(this);
     }
 
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     @Override
     public List<Variable<Object>> getVariableList() {
         Map<String, VariableAccessor> ivarAccessors = metaClass.getVariableAccessorsForRead();
         ArrayList<Variable<Object>> list = new ArrayList<Variable<Object>>();
         for (Map.Entry<String, VariableAccessor> entry : ivarAccessors.entrySet()) {
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
     @Override
    public List<String> getVariableNameList() {
         Map<String, VariableAccessor> ivarAccessors = metaClass.getVariableAccessorsForRead();
         ArrayList<String> list = new ArrayList<String>();
         for (Map.Entry<String, VariableAccessor> entry : ivarAccessors.entrySet()) {
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
         return metaClass.getVariableAccessorForRead(name).get(this) != null;
     }
 
     /**
      * Fetch an object from the variable table based on the name.
      *
      * @return the object or null if not found
      */
     protected Object variableTableFetch(String name) {
         return metaClass.getVariableAccessorForRead(name).get(this);
     }
 
     /**
      * Store a value in the variable store under the specific name.
      */
     protected Object variableTableStore(String name, Object value) {
         metaClass.getVariableAccessorForWrite(name).set(this, value);
         return value;
     }
 
     /**
      * Removes the entry with the specified name from the variable
      * table, and returning the removed value.
      */
     protected Object variableTableRemove(String name) {
         return metaClass.getVariableTableManager().clearVariable(this, name);
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
     @Override
     public InternalVariables getInternalVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#hasInternalVariable
      */
     @Override
     public boolean hasInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#getInternalVariable
      */
     @Override
     public Object getInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableFetch(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#setInternalVariable
      */
     @Override
     public void setInternalVariable(String name, Object value) {
         assert !IdUtil.isRubyVariable(name);
         variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#removeInternalVariable
      */
     @Override
     public Object removeInternalVariable(String name) {
         assert !IdUtil.isRubyVariable(name);
         return variableTableRemove(name);
     }
     
     /**
      * Sync one this object's variables with other's - this is used to make
      * rbClone work correctly.
      */
     @Override
     public void syncVariables(IRubyObject other) {
         metaClass.getVariableTableManager().syncVariables(this, other);
     }
     
     //
     // INSTANCE VARIABLE API METHODS
     //
 
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */
     @Override
     public InstanceVariables getInstanceVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#hasInstanceVariable
      */
     @Override
     public boolean hasInstanceVariable(String name) {
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariable
      */
     @Override
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject)variableTableFetch(name);
     }
 
     /** rb_iv_set / rb_ivar_set
     *
     * @see org.jruby.runtime.builtin.InstanceVariables#setInstanceVariable
     */
     @Override
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         assert value != null;
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#removeInstanceVariable
      */
     @Override
     public IRubyObject removeInstanceVariable(String name) {
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableRemove(name);
     }
 
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     @Override
     public List<Variable<IRubyObject>> getInstanceVariableList() {
         Map<String, VariableAccessor> ivarAccessors = metaClass.getVariableAccessorsForRead();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         for (Map.Entry<String, VariableAccessor> entry : ivarAccessors.entrySet()) {
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
     @Override
    public List<String> getInstanceVariableNameList() {
         Map<String, VariableAccessor> ivarAccessors = metaClass.getVariableAccessorsForRead();
         ArrayList<String> list = new ArrayList<String>();
         for (Map.Entry<String, VariableAccessor> entry : ivarAccessors.entrySet()) {
             Object value = entry.getValue().get(this);
             if (value == null || !(value instanceof IRubyObject) || !IdUtil.isInstanceVariable(entry.getKey())) continue;
             list.add(entry.getKey());
         }
         return list;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariableNameList
      */
     @Override
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
     public final void ensureInstanceVariablesSettable() {
         if (!isFrozen() || isImmediate()) {
             return;
         }
         raiseFrozenError();
     }
 
     private void raiseFrozenError() throws RaiseException {
         if (this instanceof RubyModule) {
             throw getRuntime().newFrozenError("class/module ");
         } else {
             throw getRuntime().newFrozenError(getMetaClass().toString());
         }
     }
 
     @Deprecated
     @Override
     public final int getNativeTypeIndex() {
         return getNativeClassIndex().ordinal();
     }
     
     @Override
     public ClassIndex getNativeClassIndex() {
         return ClassIndex.BASICOBJECT;
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
         return getMetaClass().isMethodBuiltin(methodName);
     }
 
     @JRubyMethod(name = "singleton_method_added", module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_added19(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "singleton_method_removed", module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_removed19(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "singleton_method_undefined", module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_undefined19(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.runtime.getNil();
     }
 
     @JRubyMethod(name = "method_missing", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject method_missing19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) {
             throw context.runtime.newArgumentError("no id given");
         }
 
         return RubyKernel.methodMissingDirect(context, recv, (RubySymbol)args[0], lastVis, lastCallType, args, block);
     }
 
     @JRubyMethod(name = "__send__", omit = true)
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, Block block) {
         String name = RubySymbol.objectToSymbolString(arg0);
 
         return getMetaClass().finvoke(context, this, name, block);
     }
     @JRubyMethod(name = "__send__", omit = true)
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         String name = RubySymbol.objectToSymbolString(arg0);
 
         return getMetaClass().finvoke(context, this, name, arg1, block);
     }
     @JRubyMethod(name = "__send__", omit = true)
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         String name = RubySymbol.objectToSymbolString(arg0);
 
         return getMetaClass().finvoke(context, this, name, arg1, arg2, block);
     }
     @JRubyMethod(name = "__send__", required = 1, rest = true, omit = true)
     public IRubyObject send19(ThreadContext context, IRubyObject[] args, Block block) {
         String name = RubySymbol.objectToSymbolString(args[0]);
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
     
     @JRubyMethod(name = "instance_eval",
             reads = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE},
             writes = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE})
     public IRubyObject instance_eval19(ThreadContext context, Block block) {
         return specificEval(context, getInstanceEvalClass(), block, EvalType.INSTANCE_EVAL);
     }
     @JRubyMethod(name = "instance_eval",
             reads = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE},
             writes = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE})
     public IRubyObject instance_eval19(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, block, EvalType.INSTANCE_EVAL);
     }
     @JRubyMethod(name = "instance_eval",
             reads = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE},
             writes = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE})
     public IRubyObject instance_eval19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, block, EvalType.INSTANCE_EVAL);
     }
     @JRubyMethod(name = "instance_eval",
             reads = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE},
             writes = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE})
     public IRubyObject instance_eval19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, arg2, block, EvalType.INSTANCE_EVAL);
     }
 
     @JRubyMethod(name = "instance_exec", optional = 3, rest = true,
             reads = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE},
             writes = {LASTLINE, BACKREF, VISIBILITY, BLOCK, SELF, METHODNAME, LINE, JUMPTARGET, CLASS, FILENAME, SCOPE})
     public IRubyObject instance_exec19(ThreadContext context, IRubyObject[] args, Block block) {
         if (!block.isGiven()) {
             throw context.runtime.newLocalJumpErrorNoBlock();
         }
 
         RubyModule klazz;
         if (isImmediate()) {
             // Ruby uses Qnil here, we use "dummy" because we need a class
             klazz = context.runtime.getDummy();
         } else {
             klazz = getSingletonClass();
         }
 
         return yieldUnder(context, klazz, args, block, EvalType.INSTANCE_EVAL);
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
     protected IRubyObject yieldUnder(final ThreadContext context, RubyModule under, IRubyObject[] args, Block block, EvalType evalType) {
-        context.preExecuteUnder(under, block);
+        context.preExecuteUnder(this, under, block);
 
         IRubyObject savedBindingSelf = block.getBinding().getSelf();
         IRubyObject savedFrameSelf = block.getBinding().getFrame().getSelf();
         Visibility savedVisibility = block.getBinding().getVisibility();
         block.getBinding().setVisibility(PUBLIC);
 
         try {
             if (args.length == 1) {
                 IRubyObject valueInYield = args[0];
                 return setupBlock(block, evalType).yieldNonArray(context, valueInYield, this); // context.getRubyClass());
             } else {
                 IRubyObject valueInYield = RubyArray.newArrayNoCopy(context.runtime, args);
                 return setupBlock(block, evalType).yieldArray(context, valueInYield, this);  // context.getRubyClass());
             }
             //TODO: Should next and return also catch here?
         } catch (JumpException.BreakJump bj) {
             return (IRubyObject) bj.getValue();
         } finally {
             block.getBinding().setVisibility(savedVisibility);
             block.getBinding().setSelf(savedBindingSelf);
             block.getBinding().getFrame().setSelf(savedFrameSelf);
 
             context.postExecuteUnder();
         }
     }
 
     private Block setupBlock(Block block, EvalType evalType) {
         // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
         return block.cloneBlockForEval(this, evalType);
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
     protected IRubyObject yieldUnder(final ThreadContext context, RubyModule under, Block block, EvalType evalType) {
-        context.preExecuteUnder(under, block);
+        context.preExecuteUnder(this, under, block);
 
         IRubyObject savedBindingSelf = block.getBinding().getSelf();
         IRubyObject savedFrameSelf = block.getBinding().getFrame().getSelf();
         Visibility savedVisibility = block.getBinding().getVisibility();
         block.getBinding().setVisibility(PUBLIC);
 
         try {
             return setupBlock(block, evalType).yieldNonArray(context, this, this); //, context.getRubyClass());
             //TODO: Should next and return also catch here?
         } catch (JumpException.BreakJump bj) {
             return (IRubyObject) bj.getValue();
         } finally {
             block.getBinding().setVisibility(savedVisibility);
             block.getBinding().setSelf(savedBindingSelf);
             block.getBinding().getFrame().setSelf(savedFrameSelf);
 
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
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, Block block, EvalType evalType) {
         if (block.isGiven()) {
             return yieldUnder(context, mod, block, evalType);
         } else {
             throw context.runtime.newArgumentError("block not supplied");
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
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject arg, Block block, EvalType evalType) {
         if (block.isGiven()) {
             throw context.runtime.newArgumentError(1, 0);
         }
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (arg instanceof RubyString) {
             evalStr = (RubyString)arg;
         } else {
             evalStr = arg.convertToString();
         }
 
         String file = "(eval)";
         int line = 0;
 
         return evalUnder(context, mod, evalStr, file, line, evalType);
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
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject arg0, IRubyObject arg1, Block block, EvalType evalType) {
         if (block.isGiven()) {
             throw context.runtime.newArgumentError(2, 0);
         }
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (arg0 instanceof RubyString) {
             evalStr = (RubyString)arg0;
         } else {
             evalStr = arg0.convertToString();
         }
 
         String file = arg1.convertToString().asJavaString();
         int line = 0;
 
         return evalUnder(context, mod, evalStr, file, line, evalType);
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
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block, EvalType evalType) {
         if (block.isGiven()) {
             throw context.runtime.newArgumentError(2, 0);
         }
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (arg0 instanceof RubyString) {
             evalStr = (RubyString)arg0;
         } else {
             evalStr = arg0.convertToString();
         }
 
         String file = arg1.convertToString().asJavaString();
         int line = (int)(arg2.convertToInteger().getLongValue() - 1);
 
         return evalUnder(context, mod, evalStr, file, line, evalType);
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
     public IRubyObject evalUnder(final ThreadContext context, RubyModule under, RubyString src, String file, int line, EvalType evalType) {
         return Interpreter.evalSimple(context, under, this, src, file, line, evalType);
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
             Helpers.invoke(
                     finalizer.getRuntime().getCurrentContext(),
                     finalizer, "call", id);
         }
     }
 
     // These are added to allow their being called against BasicObject and
     // subclass instances. Because Kernel can be included into BasicObject and
     // subclasses, there's a possibility of calling them from Ruby.
     // See JRUBY-4871
 
     /** rb_obj_equal
      *
      * Will use Java identity equality.
      */
     public IRubyObject equal_p(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.runtime.getTrue() : context.runtime.getFalse();
     }
 
     /** rb_obj_equal
      *
      * Just like "==" and "equal?", "eql?" will use identity equality for Object.
      */
     public IRubyObject eql_p(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         if (this == other || invokedynamic(context, this, OP_EQUAL, other).isTrue()){
             return RubyFixnum.zero(runtime);
         }
         return runtime.getNil();
     }
 
     /** rb_obj_init_copy
      *
      * Initializes this object as a copy of the original, that is the
      * parameter to this object. Will make sure that the argument
      * actually has the same real class as this object. It shouldn't
      * be possible to initialize an object with something totally
      * different.
      */
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
 
     /**
      * The actual method that checks frozen with the default frozen message from MRI.
      * If possible, call this instead of {@link #testFrozen}.
      */
     public void checkFrozen() {
         testFrozen();
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
     public RubyBoolean respond_to_p(IRubyObject mname) {
         String name = mname.asJavaString();
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, true));
     }
 
     public IRubyObject respond_to_p19(IRubyObject mname) {
         String name = mname.asJavaString();
         IRubyObject respond = getRuntime().newBoolean(getMetaClass().isMethodBound(name, true, true));
         if (!respond.isTrue()) {
             respond = Helpers.invoke(getRuntime().getCurrentContext(), this, "respond_to_missing?", mname, getRuntime().getFalse());
             respond = getRuntime().newBoolean(respond.isTrue());
         }
         return respond;
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
     public RubyBoolean respond_to_p(IRubyObject mname, IRubyObject includePrivate) {
         String name = mname.asJavaString();
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate.isTrue()));
     }
 
     public IRubyObject respond_to_p19(IRubyObject mname, IRubyObject includePrivate) {
         String name = mname.asJavaString();
         IRubyObject respond = getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate.isTrue()));
         if (!respond.isTrue()) {
             respond = Helpers.invoke(getRuntime().getCurrentContext(), this, "respond_to_missing?", mname, includePrivate);
             respond = getRuntime().newBoolean(respond.isTrue());
         }
         return respond;
     }
 
     /** rb_obj_id_obsolete
      *
      * Old id version. This one is bound to the "id" name and will emit a deprecation warning.
      */
     public IRubyObject id_deprecated() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#id will be deprecated; use Object#object_id");
         return id();
     }
 
     /** rb_obj_id
      *
      * Will return the hash code of this object. In comparison to MRI,
      * this method will use the Java identity hash code instead of
      * using rb_obj_id, since the usage of id in JRuby will incur the
      * cost of some. ObjectSpace maintenance.
      */
     public RubyFixnum hash() {
         return getRuntime().newFixnum(super.hashCode());
     }
 
     /** rb_obj_class
      *
      * Returns the real class of this object, excluding any
      * singleton/meta class in the inheritance chain.
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     /** rb_obj_type
      *
      * The deprecated version of type, that emits a deprecation
      * warning.
      */
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#type is deprecated; use Object#class");
         return type();
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
     public IRubyObject display(ThreadContext context, IRubyObject[] args) {
         IRubyObject port = args.length == 0 ? context.runtime.getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(context, "write", this);
 
         return context.runtime.getNil();
     }
 
     /** rb_obj_tainted
      *
      *  call-seq:
      *     obj.tainted?    => true or false
      *
      *  Returns <code>true</code> if the object is tainted.
      *
      */
     public RubyBoolean tainted_p(ThreadContext context) {
         return context.runtime.newBoolean(isTaint());
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
     public IRubyObject taint(ThreadContext context) {
         taint(context.runtime);
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
     public IRubyObject untaint(ThreadContext context) {
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
     public IRubyObject freeze(ThreadContext context) {
         Ruby runtime = context.runtime;
         if ((flags & FROZEN_F) == 0) {
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
     public RubyBoolean frozen_p(ThreadContext context) {
         return context.runtime.newBoolean(isFrozen());
     }
 
     /** rb_obj_is_instance_of
      *
      *  call-seq:
      *     obj.instance_of?(class)    => true or false
      *
      *  Returns <code>true</code> if <i>obj</i> is an instance of the given
      *  class. See also <code>Object#kind_of?</code>.
      */
     public RubyBoolean instance_of_p(ThreadContext context, IRubyObject type) {
         if (type() == type) {
             return context.runtime.getTrue();
         } else if (!(type instanceof RubyModule)) {
             throw context.runtime.newTypeError("class or module required");
         } else {
             return context.runtime.getFalse();
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
     public RubyBoolean kind_of_p(ThreadContext context, IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!(type instanceof RubyModule)) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw context.runtime.newTypeError("class or module required");
         }
 
         return context.runtime.newBoolean(((RubyModule) type).isInstance(this));
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
     public IRubyObject methods(ThreadContext context, IRubyObject[] args) {
         return methods(context, args, false);
     }
     public IRubyObject methods19(ThreadContext context, IRubyObject[] args) {
         return methods(context, args, true);
     }
 
     public IRubyObject methods(ThreadContext context, IRubyObject[] args, boolean useSymbols) {
         boolean all = args.length == 1 ? args[0].isTrue() : true;
         Ruby runtime = getRuntime();
         RubyArray methods = runtime.newArray();
         Set<String> seen = new HashSet<String>();
 
         if (getMetaClass().isSingleton()) {
             getMetaClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, false);
             if (all) {
                 getMetaClass().getSuperClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, true);
             }
         } else if (all) {
             getMetaClass().populateInstanceMethodNames(seen, methods, PRIVATE, true, useSymbols, true);
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
     public IRubyObject public_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().public_instance_methods(trueIfNoArgument(context, args));
     }
 
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
     public IRubyObject protected_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().protected_instance_methods(trueIfNoArgument(context, args));
     }
 
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
     public IRubyObject private_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().private_instance_methods(trueIfNoArgument(context, args));
     }
 
     public IRubyObject private_methods19(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().private_instance_methods19(trueIfNoArgument(context, args));
     }
 
     // FIXME: If true array is common enough we should pre-allocate and stick somewhere
     private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? new IRubyObject[] { context.runtime.getTrue() } : args;
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
     public RubyArray singleton_methods(ThreadContext context, IRubyObject[] args) {
         return singletonMethods(context, args, methodsCollector);
     }
 
     public RubyArray singleton_methods19(ThreadContext context, IRubyObject[] args) {
         return singletonMethods(context, args, methodsCollector19);
     }
 
     private RubyArray singletonMethods(ThreadContext context, IRubyObject[] args, MethodsCollector collect) {
         boolean all = true;
         if(args.length == 1) {
             all = args[0].isTrue();
         }
 
         if (getMetaClass().isSingleton()) {
             IRubyObject[] methodsArgs = new IRubyObject[]{context.runtime.getFalse()};
             RubyArray singletonMethods = collect.instanceMethods(getMetaClass(), methodsArgs);
 
             if (all) {
                 RubyClass superClass = getMetaClass().getSuperClass();
                 while (superClass.isSingleton() || superClass.isIncluded()) {
                     singletonMethods.concat(collect.instanceMethods(superClass, methodsArgs));
                     superClass = superClass.getSuperClass();
                 }
             }
 
             singletonMethods.uniq_bang(context);
             return singletonMethods;
         }
 
         return context.runtime.newEmptyArray();
     }
 
     private abstract static class MethodsCollector {
         public abstract RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args);
     };
 
     private static final MethodsCollector methodsCollector = new MethodsCollector() {
         @Override
         public RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args) {
             return rubyClass.instance_methods(args);
         }
     };
 
     private static final MethodsCollector methodsCollector19 = new MethodsCollector() {
         @Override
         public RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args) {
             return rubyClass.instance_methods19(args);
         }
     };
 
     /** rb_obj_method
      *
      *  call-seq:
      *     obj.method(sym)    => method
      *
      *  Looks up the named method as a receiver in <i>obj</i>, returning a
      *  <code>Method</code> object (or raising <code>NameError</code>). The
      *  <code>Method</code> object acts as a closure in <i>obj</i>'s object
      *  instance, so instance variables and the value of <code>self</code>
      *  remain available.
      *
      *     class Demo
      *       def initialize(n)
      *         @iv = n
      *       end
      *       def hello()
      *         "Hello, @iv = #{@iv}"
      *       end
      *     end
      *
      *     k = Demo.new(99)
      *     m = k.method(:hello)
      *     m.call   #=> "Hello, @iv = 99"
      *
      *     l = Demo.new('Fred')
      *     m = l.method("hello")
      *     m.call   #=> "Hello, @iv = Fred"
      */
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asJavaString(), true, null);
     }
 
     public IRubyObject method19(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asJavaString(), true, null, true);
     }
 
     /** rb_any_to_s
      *
      *  call-seq:
      *     obj.to_s    => string
      *
      *  Returns a string representing <i>obj</i>. The default
      *  <code>to_s</code> prints the object's class and an encoding of the
      *  object id. As a special case, the top-level object that is the
      *  initial execution context of Ruby programs returns ``main.''
      */
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     /** rb_any_to_a
      *
      *  call-seq:
      *     obj.to_a -> anArray
      *
      *  Returns an array representation of <i>obj</i>. For objects of class
      *  <code>Object</code> and others that don't explicitly override the
      *  method, the return value is an array containing <code>self</code>.
      *  However, this latter behavior will soon be obsolete.
      *
      *     self.to_a       #=> -:1: warning: default `to_a' will be obsolete
      *     "hello".to_a    #=> ["hello"]
      *     Time.new.to_a   #=> [39, 54, 8, 9, 4, 2003, 3, 99, true, "CDT"]
      *
      *  The default to_a method is deprecated.
      */
     public RubyArray to_a() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "default 'to_a' will be obsolete");
         return getRuntime().newArray(this);
     }
 
     /** rb_obj_instance_eval
      *
      *  call-seq:
      *     obj.instance_eval(string [, filename [, lineno]] )   => obj
      *     obj.instance_eval {| | block }                       => obj
      *
      *  Evaluates a string containing Ruby source code, or the given block,
      *  within the context of the receiver (_obj_). In order to set the
      *  context, the variable +self+ is set to _obj_ while
      *  the code is executing, giving the code access to _obj_'s
      *  instance variables. In the version of <code>instance_eval</code>
      *  that takes a +String+, the optional second and third
      *  parameters supply a filename and starting line number that are used
      *  when reporting compilation errors.
      *
      *     class Klass
      *       def initialize
      *         @secret = 99
      *       end
      *     end
      *     k = Klass.new
      *     k.instance_eval { @secret }   #=> 99
      */
     public IRubyObject instance_eval(ThreadContext context, Block block) {
         return specificEval(context, getInstanceEvalClass(), block, EvalType.INSTANCE_EVAL);
     }
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, block, EvalType.INSTANCE_EVAL);
     }
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, block, EvalType.INSTANCE_EVAL);
     }
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, arg2, block, EvalType.INSTANCE_EVAL);
     }
 
     /** rb_obj_instance_exec
      *
      *  call-seq:
      *     obj.instance_exec(arg...) {|var...| block }                       => obj
      *
      *  Executes the given block within the context of the receiver
      *  (_obj_). In order to set the context, the variable +self+ is set
      *  to _obj_ while the code is executing, giving the code access to
      *  _obj_'s instance variables.  Arguments are passed as block parameters.
      *
      *     class Klass
      *       def initialize
      *         @secret = 99
      *       end
      *     end
      *     k = Klass.new
      *     k.instance_exec(5) {|x| @secret+x }   #=> 104
      */
     public IRubyObject instance_exec(ThreadContext context, IRubyObject[] args, Block block) {
         if (!block.isGiven()) {
             throw context.runtime.newArgumentError("block not supplied");
         }
 
         RubyModule klazz;
         if (isImmediate()) {
             // Ruby uses Qnil here, we use "dummy" because we need a class
             klazz = context.runtime.getDummy();
         } else {
             klazz = getSingletonClass();
         }
 
         return yieldUnder(context, klazz, args, block, EvalType.INSTANCE_EVAL);
     }
 
     /** rb_obj_extend
      *
      *  call-seq:
      *     obj.extend(module, ...)    => obj
      *
      *  Adds to _obj_ the instance methods from each module given as a
      *  parameter.
      *
      *     module Mod
      *       def hello
      *         "Hello from Mod.\n"
      *       end
      *     end
      *
      *     class Klass
      *       def hello
      *         "Hello from Klass.\n"
      *       end
      *     end
      *
      *     k = Klass.new
      *     k.hello         #=> "Hello from Klass.\n"
      *     k.extend(Mod)   #=> #<Klass:0x401b3bc8>
      *     k.hello         #=> "Hello from Mod.\n"
      */
     public IRubyObject extend(IRubyObject[] args) {
         Ruby runtime = getRuntime();
 
         // Make sure all arguments are modules before calling the callbacks
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isModule()) throw runtime.newTypeError(args[i], runtime.getModule());
         }
 
         ThreadContext context = runtime.getCurrentContext();
 
         // MRI extends in order from last to first
         for (int i = args.length - 1; i >= 0; i--) {
             args[i].callMethod(context, "extend_object", this);
             args[i].callMethod(context, "extended", this);
         }
         return this;
     }
 
     /** rb_f_send
      *
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
     public IRubyObject send(ThreadContext context, Block block) {
         throw context.runtime.newArgumentError(0, 1);
     }
     public IRubyObject send(ThreadContext context, IRubyObject arg0, Block block) {
         String name = RubySymbol.objectToSymbolString(arg0);
 
         return getMetaClass().finvoke(context, this, name, block);
     }
     public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         String name = RubySymbol.objectToSymbolString(arg0);
 
         return getMetaClass().finvoke(context, this, name, arg1, block);
     }
     public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         String name = RubySymbol.objectToSymbolString(arg0);
 
         return getMetaClass().finvoke(context, this, name, arg1, arg2, block);
     }
     public IRubyObject send(ThreadContext context, IRubyObject[] args, Block block) {
         if (args.length == 0) return send(context, block);
         
         String name = RubySymbol.objectToSymbolString(args[0]);
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
 
     /** rb_false
      *
      * call_seq:
      *   nil.nil?               => true
      *   <anything_else>.nil?   => false
      *
      * Only the object <i>nil</i> responds <code>true</code> to <code>nil?</code>.
      */
     public IRubyObject nil_p(ThreadContext context) {
         return context.runtime.getFalse();
     }
 
     /** rb_obj_pattern_match
      *
      *  call-seq:
      *     obj =~ other  => false
      *
      *  Pattern Match---Overridden by descendents (notably
      *  <code>Regexp</code> and <code>String</code>) to provide meaningful
      *  pattern-match semantics.
      */
     public IRubyObject op_match(ThreadContext context, IRubyObject arg) {
         return context.runtime.getFalse();
     }
 
     public IRubyObject op_match19(ThreadContext context, IRubyObject arg) {
         return context.runtime.getNil();
     }
 
     public IRubyObject op_not_match(ThreadContext context, IRubyObject arg) {
         return context.runtime.newBoolean(!callMethod(context, "=~", arg).isTrue());
     }
 
 
     //
     // INSTANCE VARIABLE RUBY METHODS
     //
 
     /** rb_obj_ivar_defined
      *
      *  call-seq:
      *     obj.instance_variable_defined?(symbol)    => true or false
      *
diff --git a/core/src/main/java/org/jruby/ir/interpreter/Interpreter.java b/core/src/main/java/org/jruby/ir/interpreter/Interpreter.java
index 0c0dffa349..cfaea5415f 100644
--- a/core/src/main/java/org/jruby/ir/interpreter/Interpreter.java
+++ b/core/src/main/java/org/jruby/ir/interpreter/Interpreter.java
@@ -1,743 +1,743 @@
 package org.jruby.ir.interpreter;
 
 import org.jruby.*;
 import org.jruby.ast.RootNode;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.internal.runtime.methods.InterpretedIRMethod;
 import org.jruby.ir.*;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.instructions.boxing.*;
 import org.jruby.ir.instructions.specialized.*;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Float;
 import org.jruby.ir.runtime.IRBreakJump;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.ivars.VariableAccessor;
 import org.jruby.runtime.opto.ConstantCache;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 import java.util.List;
 
 public class Interpreter extends IRTranslator<IRubyObject, IRubyObject> {
 
     private static final Logger LOG = LoggerFactory.getLogger("Interpreter");
     private static final IRubyObject[] EMPTY_ARGS = new IRubyObject[]{};
     private static int interpInstrsCount = 0;
 
     // we do not need instances of Interpreter
     // FIXME: Should we make it real singleton and get rid of static methods?
     private Interpreter() { }
 
     private static class InterpreterHolder {
         // FIXME: Remove static reference unless lifus does later
         public static final Interpreter instance = new Interpreter();
     }
 
     public static Interpreter getInstance() {
         return InterpreterHolder.instance;
     }
 
     public static void dumpStats() {
         if ((IRRuntimeHelpers.isDebug() || IRRuntimeHelpers.inProfileMode()) && interpInstrsCount > 10000) {
             LOG.info("-- Interpreted instructions: {}", interpInstrsCount);
             /*
             for (Operation o: opStats.keySet()) {
                 System.out.println(o + " = " + opStats.get(o).count);
             }
             */
         }
     }
 
     public static void runBeginEndBlocks(List<IRClosure> beBlocks, ThreadContext context, IRubyObject self, StaticScope currScope, Object[] temp) {
         if (beBlocks == null) return;
 
         for (IRClosure b: beBlocks) {
             // SSS FIXME: Should I piggyback on WrappedIRClosure.retrieve or just copy that code here?
             b.prepareForInterpretation();
             Block blk = (Block)(new WrappedIRClosure(b.getSelf(), b)).retrieve(context, self, currScope, context.getCurrentScope(), temp);
             blk.yield(context, null);
         }
     }
 
     public static void runEndBlocks(List<WrappedIRClosure> blocks, ThreadContext context, IRubyObject self, StaticScope currScope, Object[] temp) {
         if (blocks == null) return;
 
         for (WrappedIRClosure block: blocks) {
             ((Block) block.retrieve(context, self, currScope, context.getCurrentScope(), temp)).yield(context, null);
         }
     }
 
     @Override
     protected IRubyObject execute(Ruby runtime, IRScriptBody irScope, IRubyObject self) {
         BeginEndInterpreterContext ic = (BeginEndInterpreterContext) irScope.prepareForInterpretation();
         ThreadContext context = runtime.getCurrentContext();
         String name = "(root)";
 
         if (IRRuntimeHelpers.isDebug()) LOG.info("Executing " + ic);
 
         // We get the live object ball rolling here.
         // This give a valid value for the top of this lexical tree.
         // All new scopes can then retrieve and set based on lexical parent.
         StaticScope scope = ic.getStaticScope();
         RubyModule currModule = scope.getModule();
         if (currModule == null) {
             // SSS FIXME: Looks like this has to do with Kernel#load
             // and the wrap parameter. Figure it out and document it here.
             currModule = context.getRuntime().getObject();
         }
 
         scope.setModule(currModule);
         DynamicScope tlbScope = irScope.getToplevelScope();
         if (tlbScope == null) {
             context.preMethodScopeOnly(scope);
         } else {
             context.preScopedBody(tlbScope);
             tlbScope.growIfNeeded();
         }
         context.setCurrentVisibility(Visibility.PRIVATE);
 
         try {
             runBeginEndBlocks(ic.getBeginBlocks(), context, self, scope, null);
             return INTERPRET_ROOT(context, self, ic, currModule, name);
         } catch (IRBreakJump bj) {
             throw IRException.BREAK_LocalJumpError.getException(context.runtime);
         } finally {
             runEndBlocks(ic.getEndBlocks(), context, self, scope, null);
             dumpStats();
             context.popScope();
         }
     }
 
     private static void setResult(Object[] temp, DynamicScope currDynScope, Variable resultVar, Object result) {
         if (resultVar instanceof TemporaryVariable) {
             // Unboxed Java primitives (float/double/int/long) don't come here because result is an Object
             // So, it is safe to use offset directly without any correction as long as IRScope uses
             // three different allocators (each with its own 'offset' counter)
             // * one for LOCAL, BOOLEAN, CURRENT_SCOPE, CURRENT_MODULE, CLOSURE tmpvars
             // * one for FIXNUM
             // * one for FLOAT
             temp[((TemporaryLocalVariable)resultVar).offset] = result;
         } else {
             LocalVariable lv = (LocalVariable)resultVar;
             currDynScope.setValue((IRubyObject)result, lv.getLocation(), lv.getScopeDepth());
         }
     }
 
     private static void setResult(Object[] temp, DynamicScope currDynScope, Instr instr, Object result) {
         if (instr instanceof ResultInstr) {
             setResult(temp, currDynScope, ((ResultInstr) instr).getResult(), result);
         }
     }
 
     private static Object retrieveOp(Operand r, ThreadContext context, IRubyObject self, DynamicScope currDynScope, StaticScope currScope, Object[] temp) {
         Object res;
         if (r instanceof Self) {
             return self;
         } else if (r instanceof TemporaryLocalVariable) {
             res = temp[((TemporaryLocalVariable)r).offset];
             return res == null ? context.nil : res;
         } else if (r instanceof LocalVariable) {
             LocalVariable lv = (LocalVariable)r;
             res = currDynScope.getValue(lv.getLocation(), lv.getScopeDepth());
             return res == null ? context.nil : res;
         } else {
             return r.retrieve(context, self, currScope, currDynScope, temp);
         }
 
     }
 
     private static double getFloatArg(double[] floats, Operand arg) {
         if (arg instanceof Float) {
             return ((Float)arg).value;
         } else if (arg instanceof Fixnum) {
             return (double)((Fixnum)arg).value;
         } else if (arg instanceof Bignum) {
             return ((Bignum)arg).value.doubleValue();
         } else if (arg instanceof TemporaryLocalVariable) {
             return floats[((TemporaryLocalVariable)arg).offset];
         } else {
             throw new RuntimeException("invalid float operand: " + arg);
         }
     }
 
     private static long getFixnumArg(long[] fixnums, Operand arg) {
         if (arg instanceof Float) {
             return (long)((Float)arg).value;
         } else if (arg instanceof Fixnum) {
             return ((Fixnum)arg).value;
         } else if (arg instanceof Bignum) {
             return ((Bignum)arg).value.longValue();
         } else if (arg instanceof TemporaryLocalVariable) {
             return fixnums[((TemporaryLocalVariable)arg).offset];
         } else {
             throw new RuntimeException("invalid fixnum operand: " + arg);
         }
     }
 
     private static boolean getBooleanArg(boolean[] booleans, Operand arg) {
         if (arg instanceof UnboxedBoolean) {
             return ((UnboxedBoolean)arg).isTrue();
         } else if (arg instanceof TemporaryLocalVariable) {
             return booleans[((TemporaryLocalVariable)arg).offset];
         } else {
             throw new RuntimeException("invalid fixnum operand: " + arg);
         }
     }
 
     private static void setFloatVar(double[] floats, TemporaryLocalVariable var, double val) {
         floats[var.offset] = val;
     }
 
     private static void setFixnumVar(long[] fixnums, TemporaryLocalVariable var, long val) {
         fixnums[var.offset] = val;
     }
 
     private static void setBooleanVar(boolean[] booleans, TemporaryLocalVariable var, boolean val) {
         booleans[var.offset] = val;
     }
 
     private static void interpretIntOp(AluInstr instr, Operation op, long[] fixnums, boolean[] booleans) {
         TemporaryLocalVariable dst = (TemporaryLocalVariable)instr.getResult();
         long i1 = getFixnumArg(fixnums, instr.getArg1());
         long i2 = getFixnumArg(fixnums, instr.getArg2());
         switch (op) {
             case IADD: setFixnumVar(fixnums, dst, i1 + i2); break;
             case ISUB: setFixnumVar(fixnums, dst, i1 - i2); break;
             case IMUL: setFixnumVar(fixnums, dst, i1 * i2); break;
             case IDIV: setFixnumVar(fixnums, dst, i1 / i2); break;
             case IOR : setFixnumVar(fixnums, dst, i1 | i2); break;
             case IAND: setFixnumVar(fixnums, dst, i1 & i2); break;
             case IXOR: setFixnumVar(fixnums, dst, i1 ^ i2); break;
             case ISHL: setFixnumVar(fixnums, dst, i1 << i2); break;
             case ISHR: setFixnumVar(fixnums, dst, i1 >> i2); break;
             case ILT : setBooleanVar(booleans, dst, i1 < i2); break;
             case IGT : setBooleanVar(booleans, dst, i1 > i2); break;
             case IEQ : setBooleanVar(booleans, dst, i1 == i2); break;
             default: throw new RuntimeException("Unhandled int op: " + op + " for instr " + instr);
         }
     }
 
     private static void interpretFloatOp(AluInstr instr, Operation op, double[] floats, boolean[] booleans) {
         TemporaryLocalVariable dst = (TemporaryLocalVariable)instr.getResult();
         double a1 = getFloatArg(floats, instr.getArg1());
         double a2 = getFloatArg(floats, instr.getArg2());
         switch (op) {
             case FADD: setFloatVar(floats, dst, a1 + a2); break;
             case FSUB: setFloatVar(floats, dst, a1 - a2); break;
             case FMUL: setFloatVar(floats, dst, a1 * a2); break;
             case FDIV: setFloatVar(floats, dst, a1 / a2); break;
             case FLT : setBooleanVar(booleans, dst, a1 < a2); break;
             case FGT : setBooleanVar(booleans, dst, a1 > a2); break;
             case FEQ : setBooleanVar(booleans, dst, a1 == a2); break;
             default: throw new RuntimeException("Unhandled float op: " + op + " for instr " + instr);
         }
     }
 
     private static void receiveArg(ThreadContext context, Instr i, Operation operation, IRubyObject[] args, boolean acceptsKeywordArgument, DynamicScope currDynScope, Object[] temp, Object exception, Block block) {
         Object result;
         ResultInstr instr = (ResultInstr)i;
 
         switch(operation) {
         case RECV_PRE_REQD_ARG:
             int argIndex = ((ReceivePreReqdArgInstr)instr).getArgIndex();
             result = IRRuntimeHelpers.getPreArgSafe(context, args, argIndex);
             setResult(temp, currDynScope, instr.getResult(), result);
             return;
         case RECV_CLOSURE:
             result = IRRuntimeHelpers.newProc(context.runtime, block);
             setResult(temp, currDynScope, instr.getResult(), result);
             return;
         case RECV_POST_REQD_ARG:
             result = ((ReceivePostReqdArgInstr)instr).receivePostReqdArg(args, acceptsKeywordArgument);
             // For blocks, missing arg translates to nil
             setResult(temp, currDynScope, instr.getResult(), result == null ? context.nil : result);
             return;
         case RECV_RUBY_EXC:
             setResult(temp, currDynScope, instr.getResult(), IRRuntimeHelpers.unwrapRubyException(exception));
             return;
         case RECV_JRUBY_EXC:
             setResult(temp, currDynScope, instr.getResult(), exception);
             return;
         default:
             result = ((ReceiveArgBase)instr).receiveArg(context, args, acceptsKeywordArgument);
             setResult(temp, currDynScope, instr.getResult(), result);
         }
     }
 
     private static void processCall(ThreadContext context, Instr instr, Operation operation, DynamicScope currDynScope, StaticScope currScope, Object[] temp, IRubyObject self) {
         Object result;
 
         switch(operation) {
         case CALL_1F: {
             OneFixnumArgNoBlockCallInstr call = (OneFixnumArgNoBlockCallInstr)instr;
             IRubyObject r = (IRubyObject)retrieveOp(call.getReceiver(), context, self, currDynScope, currScope, temp);
             result = call.getCallSite().call(context, self, r, call.getFixnumArg());
             setResult(temp, currDynScope, call.getResult(), result);
             break;
         }
         case CALL_1O: {
             OneOperandArgNoBlockCallInstr call = (OneOperandArgNoBlockCallInstr)instr;
             IRubyObject r = (IRubyObject)retrieveOp(call.getReceiver(), context, self, currDynScope, currScope, temp);
             IRubyObject o = (IRubyObject)call.getArg1().retrieve(context, self, currScope, currDynScope, temp);
             result = call.getCallSite().call(context, self, r, o);
             setResult(temp, currDynScope, call.getResult(), result);
             break;
         }
         case CALL_1OB: {
             OneOperandArgBlockCallInstr call = (OneOperandArgBlockCallInstr)instr;
             IRubyObject r = (IRubyObject)retrieveOp(call.getReceiver(), context, self, currDynScope, currScope, temp);
             IRubyObject o = (IRubyObject)call.getArg1().retrieve(context, self, currScope, currDynScope, temp);
             Block preparedBlock = call.prepareBlock(context, self, currScope, currDynScope, temp);
             result = call.getCallSite().call(context, self, r, o, preparedBlock);
             setResult(temp, currDynScope, call.getResult(), result);
             break;
         }
         case CALL_0O: {
             ZeroOperandArgNoBlockCallInstr call = (ZeroOperandArgNoBlockCallInstr)instr;
             IRubyObject r = (IRubyObject)retrieveOp(call.getReceiver(), context, self, currDynScope, currScope, temp);
             result = call.getCallSite().call(context, self, r);
             setResult(temp, currDynScope, call.getResult(), result);
             break;
         }
         case NORESULT_CALL_1O: {
             OneOperandArgNoBlockNoResultCallInstr call = (OneOperandArgNoBlockNoResultCallInstr)instr;
             IRubyObject r = (IRubyObject)retrieveOp(call.getReceiver(), context, self, currDynScope, currScope, temp);
             IRubyObject o = (IRubyObject)call.getArg1().retrieve(context, self, currScope, currDynScope, temp);
             call.getCallSite().call(context, self, r, o);
             break;
         }
         case NORESULT_CALL:
             instr.interpret(context, currScope, currDynScope, self, temp);
             break;
         case CALL:
         default:
             result = instr.interpret(context, currScope, currDynScope, self, temp);
             setResult(temp, currDynScope, instr, result);
             break;
         }
     }
 
     private static void processBookKeepingOp(ThreadContext context, Instr instr, Operation operation,
                                              String name, IRubyObject[] args, IRubyObject self, Block block,
                                              RubyModule implClass) {
         switch(operation) {
         case PUSH_FRAME:
             context.preMethodFrameOnly(implClass, name, self, block);
             // Only the top-level script scope has PRIVATE visibility.
             // This is already handled as part of Interpreter.execute above.
             // Everything else is PUBLIC by default.
             context.setCurrentVisibility(Visibility.PUBLIC);
             break;
         case POP_FRAME:
             context.popFrame();
             break;
         case POP_BINDING:
             context.popScope();
             break;
         case THREAD_POLL:
             if (IRRuntimeHelpers.inProfileMode()) Profiler.clockTick();
             context.callThreadPoll();
             break;
         case CHECK_ARITY:
             ((CheckArityInstr)instr).checkArity(context, args);
             break;
         case LINE_NUM:
             context.setLine(((LineNumberInstr)instr).lineNumber);
             break;
         case RECORD_END_BLOCK:
             ((RecordEndBlockInstr)instr).interpret();
             break;
         case TRACE: {
             if (context.runtime.hasEventHooks()) {
                 TraceInstr trace = (TraceInstr) instr;
                 // FIXME: Try and statically generate END linenumber instead of hacking it.
                 int linenumber = trace.getLinenumber() == -1 ? context.getLine()+1 : trace.getLinenumber();
 
                 context.trace(trace.getEvent(), trace.getName(), context.getFrameKlazz(),
                         trace.getFilename(), linenumber);
             }
             break;
         }
         }
     }
 
     private static IRubyObject processReturnOp(ThreadContext context, Instr instr, Operation operation, DynamicScope currDynScope, Object[] temp, IRubyObject self, Block.Type blockType, StaticScope currScope)
     {
         switch(operation) {
         // --------- Return flavored instructions --------
         case RETURN: {
             return (IRubyObject)retrieveOp(((ReturnBase)instr).getReturnValue(), context, self, currDynScope, currScope, temp);
         }
         case BREAK: {
             BreakInstr bi = (BreakInstr)instr;
             IRubyObject rv = (IRubyObject)bi.getReturnValue().retrieve(context, self, currScope, currDynScope, temp);
             // This also handles breaks in lambdas -- by converting them to a return
             //
             // This assumes that scopes with break instr. have a frame / dynamic scope
             // pushed so that we can get to its static scope. For-loops now always have
             // a dyn-scope pushed onto stack which makes this work in all scenarios.
             return IRRuntimeHelpers.initiateBreak(context, currDynScope, rv, blockType);
         }
         case NONLOCAL_RETURN: {
             NonlocalReturnInstr ri = (NonlocalReturnInstr)instr;
             IRubyObject rv = (IRubyObject)retrieveOp(ri.getReturnValue(), context, self, currDynScope, currScope, temp);
             return IRRuntimeHelpers.initiateNonLocalReturn(context, currDynScope, blockType, rv);
         }
         }
         return null;
     }
 
     private static void processOtherOp(ThreadContext context, Instr instr, Operation operation, DynamicScope currDynScope, StaticScope currScope, Object[] temp, IRubyObject self, Block.Type blockType, double[] floats, long[] fixnums, boolean[] booleans)
     {
         Object result;
         switch(operation) {
         case COPY: {
             CopyInstr c = (CopyInstr)instr;
             Operand  src = c.getSource();
             Variable res = c.getResult();
             if (res instanceof TemporaryFloatVariable) {
                 setFloatVar(floats, (TemporaryFloatVariable)res, getFloatArg(floats, src));
             } else if (res instanceof TemporaryFixnumVariable) {
                 setFixnumVar(fixnums, (TemporaryFixnumVariable)res, getFixnumArg(fixnums, src));
             } else {
                 setResult(temp, currDynScope, res, retrieveOp(src, context, self, currDynScope, currScope, temp));
             }
             break;
         }
 
         case GET_FIELD: {
             GetFieldInstr gfi = (GetFieldInstr)instr;
             IRubyObject object = (IRubyObject)gfi.getSource().retrieve(context, self, currScope, currDynScope, temp);
             VariableAccessor a = gfi.getAccessor(object);
             result = a == null ? null : (IRubyObject)a.get(object);
             if (result == null) {
                 if (context.runtime.isVerbose()) {
                     context.runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + gfi.getRef() + " not initialized");
                 }
                 result = context.nil;
             }
             setResult(temp, currDynScope, gfi.getResult(), result);
             break;
         }
 
         case SEARCH_CONST: {
             SearchConstInstr sci = (SearchConstInstr)instr;
             ConstantCache cache = sci.getConstantCache();
             if (!ConstantCache.isCached(cache)) {
                 result = sci.cache(context, currScope, currDynScope, self, temp);
             } else {
                 result = cache.value;
             }
             setResult(temp, currDynScope, sci.getResult(), result);
             break;
         }
 
         case RUNTIME_HELPER: {
             RuntimeHelperCall rhc = (RuntimeHelperCall)instr;
             result = rhc.callHelper(context, currScope, currDynScope, self, temp, blockType);
             if (rhc.getResult() != null) {
                 setResult(temp, currDynScope, rhc.getResult(), result);
             }
             break;
         }
 
         case BOX_FLOAT: {
             RubyFloat f = context.runtime.newFloat(getFloatArg(floats, ((BoxFloatInstr)instr).getValue()));
             setResult(temp, currDynScope, ((BoxInstr)instr).getResult(), f);
             break;
         }
 
         case BOX_FIXNUM: {
             RubyFixnum f = context.runtime.newFixnum(getFixnumArg(fixnums, ((BoxFixnumInstr) instr).getValue()));
             setResult(temp, currDynScope, ((BoxInstr)instr).getResult(), f);
             break;
         }
 
         case BOX_BOOLEAN: {
             RubyBoolean f = context.runtime.newBoolean(getBooleanArg(booleans, ((BoxBooleanInstr) instr).getValue()));
             setResult(temp, currDynScope, ((BoxInstr)instr).getResult(), f);
             break;
         }
 
         case UNBOX_FLOAT: {
             UnboxInstr ui = (UnboxInstr)instr;
             Object val = retrieveOp(ui.getValue(), context, self, currDynScope, currScope, temp);
             if (val instanceof RubyFloat) {
                 floats[((TemporaryLocalVariable)ui.getResult()).offset] = ((RubyFloat)val).getValue();
             } else {
                 floats[((TemporaryLocalVariable)ui.getResult()).offset] = ((RubyFixnum)val).getDoubleValue();
             }
             break;
         }
 
         case UNBOX_FIXNUM: {
             UnboxInstr ui = (UnboxInstr)instr;
             Object val = retrieveOp(ui.getValue(), context, self, currDynScope, currScope, temp);
             if (val instanceof RubyFloat) {
                 fixnums[((TemporaryLocalVariable)ui.getResult()).offset] = ((RubyFloat)val).getLongValue();
             } else {
                 fixnums[((TemporaryLocalVariable)ui.getResult()).offset] = ((RubyFixnum)val).getLongValue();
             }
             break;
         }
 
         // ---------- All the rest ---------
         default:
             result = instr.interpret(context, currScope, currDynScope, self, temp);
             setResult(temp, currDynScope, instr, result);
             break;
         }
     }
 
     private static IRubyObject interpret(ThreadContext context, IRubyObject self,
             InterpreterContext interpreterContext, RubyModule implClass,
             String name, IRubyObject[] args, Block block, Block.Type blockType) {
         Instr[] instrs = interpreterContext.getInstructions();
         Object[] temp           = interpreterContext.allocateTemporaryVariables();
         double[] floats         = interpreterContext.allocateTemporaryFloatVariables();
         long[]   fixnums        = interpreterContext.allocateTemporaryFixnumVariables();
         boolean[]   booleans    = interpreterContext.allocateTemporaryBooleanVariables();
         int      n              = instrs.length;
         int      ipc            = 0;
         Object   exception      = null;
         DynamicScope currDynScope = context.getCurrentScope();
         StaticScope currScope = interpreterContext.getStaticScope();
         IRScope scope = currScope.getIRScope();
         boolean acceptsKeywordArgument = interpreterContext.receivesKeywordArguments();
 
         // Init profiling this scope
         boolean debug   = IRRuntimeHelpers.isDebug();
         boolean profile = IRRuntimeHelpers.inProfileMode();
         Integer scopeVersion = profile ? Profiler.initProfiling(scope) : 0;
 
         // Enter the looooop!
         while (ipc < n) {
             Instr instr = instrs[ipc];
             ipc++;
             Operation operation = instr.getOperation();
             if (debug) {
                 LOG.info("I: {}", instr);
                 interpInstrsCount++;
             } else if (profile) {
                 Profiler.instrTick(operation);
                 interpInstrsCount++;
             }
 
             try {
                 switch (operation.opClass) {
                 case INT_OP:
                     interpretIntOp((AluInstr) instr, operation, fixnums, booleans);
                     break;
                 case FLOAT_OP:
                     interpretFloatOp((AluInstr) instr, operation, floats, booleans);
                     break;
                 case ARG_OP:
                     receiveArg(context, instr, operation, args, acceptsKeywordArgument, currDynScope, temp, exception, block);
                     break;
                 case CALL_OP:
                     if (profile) Profiler.updateCallSite(instr, scope, scopeVersion);
                     processCall(context, instr, operation, currDynScope, currScope, temp, self);
                     break;
                 case RET_OP:
                     return processReturnOp(context, instr, operation, currDynScope, temp, self, blockType, currScope);
                 case BRANCH_OP:
                     switch (operation) {
                     case JUMP: ipc = ((JumpInstr)instr).getJumpTarget().getTargetPC(); break;
                     default: ipc = instr.interpretAndGetNewIPC(context, currDynScope, currScope, self, temp, ipc); break;
                     }
                     break;
                 case BOOK_KEEPING_OP:
                     if (operation == Operation.PUSH_BINDING) {
                         // IMPORTANT: Preserve this update of currDynScope.
                         // This affects execution of all instructions in this scope
                         // which will now use the updated value of currDynScope.
                         currDynScope = interpreterContext.newDynamicScope(context);
                         context.pushScope(currDynScope);
                     } else {
                         processBookKeepingOp(context, instr, operation, name, args, self, block, implClass);
                     }
                     break;
                 case OTHER_OP:
                     processOtherOp(context, instr, operation, currDynScope, currScope, temp, self, blockType, floats, fixnums, booleans);
                     break;
                 }
             } catch (Throwable t) {
                 extractToMethodToAvoidC2Crash(context, instr, t);
 
                 if (debug) LOG.info("in : " + interpreterContext.getStaticScope().getIRScope() + ", caught Java throwable: " + t + "; excepting instr: " + instr);
                 ipc = instr.getRPC();
                 if (debug) LOG.info("ipc for rescuer: " + ipc);
 
                 if (ipc == -1) {
                     Helpers.throwException(t);
                 } else {
                     exception = t;
                 }
             }
         }
 
         // Control should never get here!
         // SSS FIXME: But looks like BEGIN/END blocks get here -- needs fixing
         return null;
     }
 
     /*
      * If you put this code into the method above it will hard crash some production builds of C2 in Java 8. We aren't
      * sure exactly which builds, but it seems to appear more often in Linux builds than Mac. - Chris Seaton
      */
 
     private static void extractToMethodToAvoidC2Crash(ThreadContext context, Instr instr, Throwable t) {
         if (!(t instanceof Unrescuable)) {
             if (!instr.canRaiseException()) {
                 System.err.println("ERROR: Got exception " + t + " but instr " + instr + " is not supposed to be raising exceptions!");
             }
             if ((t instanceof RaiseException) && context.runtime.getGlobalVariables().get("$!") != IRRuntimeHelpers.unwrapRubyException(t)) {
                 System.err.println("ERROR: $! and exception are not matching up.");
                 System.err.println("$!: " + context.runtime.getGlobalVariables().get("$!"));
                 System.err.println("t : " + t);
             }
         }
     }
 
     public static IRubyObject INTERPRET_ROOT(ThreadContext context, IRubyObject self,
            InterpreterContext ic, RubyModule clazz, String name) {
         try {
             ThreadContext.pushBacktrace(context, name, ic.getFileName(), context.getLine());
             return interpret(context, self, ic, clazz, name, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK, null);
         } finally {
             ThreadContext.popBacktrace(context);
         }
     }
 
     public static IRubyObject INTERPRET_EVAL(ThreadContext context, IRubyObject self,
            InterpreterContext ic, RubyModule clazz, IRubyObject[] args, String name, Block block, Block.Type blockType) {
         try {
             ThreadContext.pushBacktrace(context, name, ic.getFileName(), context.getLine());
             return interpret(context, self, ic, clazz, name, args, block, blockType);
         } finally {
             ThreadContext.popBacktrace(context);
         }
     }
 
     public static IRubyObject INTERPRET_BLOCK(ThreadContext context, IRubyObject self,
             InterpreterContext ic, IRubyObject[] args, String name, Block block, Block.Type blockType) {
         try {
             ThreadContext.pushBacktrace(context, name, ic.getFileName(), context.getLine());
             return interpret(context, self, ic, null, name, args, block, blockType);
         } finally {
             ThreadContext.popBacktrace(context);
         }
     }
 
     public static IRubyObject INTERPRET_METHOD(ThreadContext context, InterpretedIRMethod method,
         IRubyObject self, String name, IRubyObject[] args, Block block) {
         InterpreterContext ic = method.ensureInstrsReady();
         // FIXME: Consider synthetic methods/module/class bodies to use different method type to eliminate this check
         boolean isSynthetic = method.isSynthetic();
 
         try {
             if (!isSynthetic) ThreadContext.pushBacktrace(context, name, ic.getFileName(), context.getLine());
 
             return interpret(context, self, ic, method.getImplementationClass().getMethodLocation(), name, args, block, null);
         } finally {
             if (!isSynthetic) ThreadContext.popBacktrace(context);
         }
     }
 
     /**
      * Evaluate the given string.
      * @param context the current thread's context
      * @param self the self to evaluate under
      * @param src The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber that the eval supposedly starts from
      * @return An IRubyObject result from the evaluation
      */
     public static IRubyObject evalSimple(ThreadContext context, RubyModule under, IRubyObject self, RubyString src, String file, int lineNumber, EvalType evalType) {
         Ruby runtime = context.runtime;
         if (runtime.getInstanceConfig().getCompileMode() == RubyInstanceConfig.CompileMode.TRUFFLE) throw new UnsupportedOperationException();
 
         Visibility savedVisibility = context.getCurrentVisibility();
         context.setCurrentVisibility(Visibility.PUBLIC);
         // no binding, just eval in "current" frame (caller's frame)
         DynamicScope parentScope = context.getCurrentScope();
         DynamicScope evalScope = new ManyVarsDynamicScope(runtime.getStaticScopeFactory().newEvalScope(parentScope.getStaticScope()), parentScope);
 
         evalScope.getStaticScope().setModule(under);
-        context.pushEvalFrame();
+        context.pushEvalSimpleFrame(self);
 
         try {
             return evalCommon(context, evalScope, self, src, file, lineNumber, "(eval)", Block.NULL_BLOCK, evalType);
         } finally {
             context.popFrame();
             context.setCurrentVisibility(savedVisibility);
         }
     }
 
     private static IRubyObject evalCommon(ThreadContext context, DynamicScope evalScope, IRubyObject self, IRubyObject src,
                                           String file, int lineNumber, String name, Block block, EvalType evalType) {
         Ruby runtime = context.runtime;
         StaticScope ss = evalScope.getStaticScope();
         BeginEndInterpreterContext ic = prepareIC(runtime, evalScope, src, file, lineNumber, evalType);
 
         evalScope.setEvalType(evalType);
         context.pushScope(evalScope);
         try {
             evalScope.growIfNeeded();
 
             runBeginEndBlocks(ic.getBeginBlocks(), context, self, ss, null);
 
             return Interpreter.INTERPRET_EVAL(context, self, ic, ic.getStaticScope().getModule(), EMPTY_ARGS, name, block, null);
         } finally {
             runEndBlocks(ic.getEndBlocks(), context, self, ss, null);
             evalScope.clearEvalType();
             context.popScope();
         }
     }
 
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param context the thread context for the current thread
      * @param self the self against which eval was called; used as self in the eval in 1.9 mode
      * @param src The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @return An IRubyObject result from the evaluation
      */
     public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject self, IRubyObject src, Binding binding) {
         Ruby runtime = context.runtime;
         if (runtime.getInstanceConfig().getCompileMode() == RubyInstanceConfig.CompileMode.TRUFFLE) throw new UnsupportedOperationException();
 
         DynamicScope evalScope = binding.getEvalScope(runtime);
         evalScope.getStaticScope().determineModule(); // FIXME: It would be nice to just set this or remove it from staticScope altogether
 
         Frame lastFrame = context.preEvalWithBinding(binding);
         try {
             return evalCommon(context, evalScope, self, src, binding.getFile(),
                     binding.getLine(), binding.getMethod(), binding.getFrame().getBlock(), EvalType.BINDING_EVAL);
         } finally {
             context.postEvalWithBinding(binding, lastFrame);
         }
     }
 
     private static BeginEndInterpreterContext prepareIC(Ruby runtime, DynamicScope evalScope, IRubyObject src,
                                                         String file, int lineNumber, EvalType evalType) {
         IRScope containingIRScope = evalScope.getStaticScope().getEnclosingScope().getIRScope();
         RootNode rootNode = (RootNode) runtime.parseEval(src.convertToString().getByteList(), file, evalScope, lineNumber);
         IREvalScript evalScript = IRBuilder.createIRBuilder(runtime, runtime.getIRManager()).buildEvalRoot(evalScope.getStaticScope(), containingIRScope, file, lineNumber, rootNode, evalType);
 
         if (IRRuntimeHelpers.isDebug()) {
             LOG.info("Graph:\n" + evalScript.cfg().toStringGraph());
             LOG.info("CFG:\n" + evalScript.cfg().toStringInstrs());
         }
 
         return (BeginEndInterpreterContext) evalScript.prepareForInterpretation();
     }
 }
diff --git a/core/src/main/java/org/jruby/runtime/ThreadContext.java b/core/src/main/java/org/jruby/runtime/ThreadContext.java
index 1d3d47bbdf..52e518b2b3 100644
--- a/core/src/main/java/org/jruby/runtime/ThreadContext.java
+++ b/core/src/main/java/org/jruby/runtime/ThreadContext.java
@@ -1,1134 +1,1134 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyContinuation.Continuation;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.RubyThread;
 import org.jruby.ast.executable.RuntimeCache;
 import org.jruby.ext.fiber.ThreadFiber;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.backtrace.BacktraceElement;
 import org.jruby.runtime.backtrace.RubyStackTraceElement;
 import org.jruby.runtime.backtrace.TraceType;
 import org.jruby.runtime.backtrace.TraceType.Gather;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.profile.ProfileCollection;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 import org.jruby.util.RecursiveComparator;
 import org.jruby.util.RubyDateFormatter;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 import java.lang.ref.WeakReference;
 import java.security.SecureRandom;
 import java.util.Arrays;
 import java.util.Locale;
 import java.util.Set;
 
 public final class ThreadContext {
 
     private static final Logger LOG = LoggerFactory.getLogger("ThreadContext");
 
     public static ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         return context;
     }
     
     private final static int INITIAL_SIZE = 10;
     private final static int INITIAL_FRAMES_SIZE = 10;
     
     /** The number of calls after which to do a thread event poll */
     private final static int CALL_POLL_COUNT = 0xFFF;
 
     // runtime, nil, and runtimeCache cached here for speed of access from any thread
     public final Ruby runtime;
     public final IRubyObject nil;
     public final RuntimeCache runtimeCache;
 
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     private RubyThread thread;
     private RubyThread rootThread; // thread for fiber purposes
     private static final WeakReference<ThreadFiber> NULL_FIBER_REF = new WeakReference<ThreadFiber>(null);
     private WeakReference<ThreadFiber> fiber = NULL_FIBER_REF;
     private ThreadFiber rootFiber; // hard anchor for root threads' fibers
     // Cache format string because it is expensive to create on demand
     private RubyDateFormatter dateFormatter;
 
     private Frame[] frameStack = new Frame[INITIAL_FRAMES_SIZE];
     private int frameIndex = -1;
 
     private BacktraceElement[] backtrace = new BacktraceElement[INITIAL_FRAMES_SIZE];
     private int backtraceIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
 
     private static final Continuation[] EMPTY_CATCHTARGET_STACK = new Continuation[0];
     private Continuation[] catchStack = EMPTY_CATCHTARGET_STACK;
     private int catchIndex = -1;
     
     private boolean isProfiling = false;
 
     // The flat profile data for this thread
 	// private ProfileData profileData;
 
     private ProfileCollection profileCollection;
 	
     private boolean eventHooksEnabled = true;
 
     CallType lastCallType;
 
     Visibility lastVisibility;
 
     IRubyObject lastExitStatus;
 
     public final SecureRandom secureRandom;
 
     private static boolean trySHA1PRNG = true;
 
     {
         SecureRandom sr;
         try {
             sr = trySHA1PRNG ?
                     SecureRandom.getInstance("SHA1PRNG") :
                     new SecureRandom();
         } catch (Exception e) {
             trySHA1PRNG = false;
             sr = new SecureRandom();
         }
         secureRandom = sr;
     }
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         this.nil = runtime.getNil();
 
         if (runtime.getInstanceConfig().isProfilingEntireRun()) {
             startProfiling();
         }
 
         this.runtimeCache = runtime.getRuntimeCache();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = runtime.getStaticScopeFactory().newLocalScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
 
         Frame[] stack = frameStack;
         int length = stack.length;
         for (int i = 0; i < length; i++) {
             stack[i] = new Frame();
         }
         BacktraceElement[] stack2 = backtrace;
         int length2 = stack2.length;
         for (int i = 0; i < length2; i++) {
             stack2[i] = new BacktraceElement();
         }
         ThreadContext.pushBacktrace(this, "", "", 0);
         ThreadContext.pushBacktrace(this, "", "", 0);
     }
 
     @Override
     protected void finalize() throws Throwable {
         if (thread != null) {
             thread.dispose();
         }
     }
 
     /**
      * Retrieve the runtime associated with this context.
      *
      * Note that there's no reason to call this method rather than accessing the
      * runtime field directly.
      *
      * @see ThreadContext#runtime
      *
      * @return the runtime associated with this context
      */
     public final Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return thread.getErrorInfo();
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         thread.setErrorInfo(errorInfo);
         return errorInfo;
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
 
     public CallType getLastCallType() {
         return lastCallType;
     }
 
     public Visibility getLastVisibility() {
         return lastVisibility;
     }
     
     public void setLastCallStatusAndVisibility(CallType callType, Visibility visibility) {
         lastCallType = callType;
         lastVisibility = visibility;
     }
     
     public IRubyObject getLastExitStatus() {
         return lastExitStatus;
     }
     
     public void setLastExitStatus(IRubyObject lastExitStatus) {
         this.lastExitStatus = lastExitStatus;
     }
 
     public void printScope() {
         LOG.debug("SCOPE STACK:");
         for (int i = 0; i <= scopeIndex; i++) {
             LOG.debug("{}", scopeStack[i]);
         }
     }
 
     public DynamicScope getCurrentScope() {
         return scopeStack[scopeIndex];
     }
 
     public StaticScope getCurrentStaticScope() {
         return scopeStack[scopeIndex].getStaticScope();
     }
 
     private void expandFrameStack() {
         int newSize = frameStack.length * 2;
         frameStack = fillNewFrameStack(new Frame[newSize], newSize);
     }
 
     private Frame[] fillNewFrameStack(Frame[] newFrameStack, int newSize) {
         System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
 
         for (int i = frameStack.length; i < newSize; i++) {
             newFrameStack[i] = new Frame();
         }
         
         return newFrameStack;
     }
 
     public void pushScope(DynamicScope scope) {
         int index = ++scopeIndex;
         DynamicScope[] stack = scopeStack;
         stack[index] = scope;
         if (index + 1 == stack.length) {
             expandScopeStack();
         }
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
     
     private void expandScopeStack() {
         int newSize = scopeStack.length * 2;
         DynamicScope[] newScopeStack = new DynamicScope[newSize];
 
         System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
 
         scopeStack = newScopeStack;
     }
     
     public RubyThread getThread() {
         return thread;
     }
     
     public RubyThread getFiberCurrentThread() {
         if (rootThread != null) return rootThread;
         return thread;
     }
     
     public RubyDateFormatter getRubyDateFormatter() {
         if (dateFormatter == null)
             dateFormatter = new RubyDateFormatter(this);
         return dateFormatter;
     }
     
     public void setThread(RubyThread thread) {
         this.thread = thread;
         this.rootThread = thread; // may be reset by fiber
 
         // associate the thread with this context, unless we're clearing the reference
         if (thread != null) {
             thread.setContext(this);
         }
     }
     
     public ThreadFiber getFiber() {
         ThreadFiber f = fiber.get();
         
         if (f == null) return rootFiber;
         
         return f;
     }
     
     public void setFiber(ThreadFiber fiber) {
         this.fiber = new WeakReference(fiber);
     }
     
     public void setRootFiber(ThreadFiber rootFiber) {
         this.rootFiber = rootFiber;
     }
     
     public void setRootThread(RubyThread rootThread) {
         this.rootThread = rootThread;
     }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchStack() {
         int newSize = catchStack.length * 2;
         if (newSize == 0) newSize = 1;
         Continuation[] newCatchStack = new Continuation[newSize];
 
         System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
         catchStack = newCatchStack;
     }
     
     public void pushCatch(Continuation catchTarget) {
         int index = ++catchIndex;
         if (index == catchStack.length) {
             expandCatchStack();
         }
         catchStack[index] = catchTarget;
     }
     
     public void popCatch() {
         catchIndex--;
     }
 
     /**
      * Find the active Continuation for the given tag. Must be called with an
      * interned string.
      *
      * @param tag The interned string to search for
      * @return The continuation associated with this tag
      */
     public Continuation getActiveCatch(Object tag) {
         for (int i = catchIndex; i >= 0; i--) {
             Continuation c = catchStack[i];
             if (c.tag == tag) return c;
         }
 
         // if this is a fiber, search prev for tag
         ThreadFiber fiber = getFiber();
         ThreadFiber prev;
         if (fiber != null && (prev = fiber.getData().getPrev()) != null) {
             return prev.getThread().getContext().getActiveCatch(tag);
         }
 
         return null;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         Frame currentFrame = stack[index - 1];
         stack[index].updateFrame(currentFrame);
         if (index + 1 == stack.length) {
             expandFrameStack();
         }
     }
     
     private Frame pushFrame(Frame frame) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index] = frame;
         if (index + 1 == stack.length) {
             expandFrameStack();
         }
         return frame;
     }
 
-    public void pushEvalFrame() {
+    public void pushEvalSimpleFrame(IRubyObject executeObject) {
         Frame frame = getCurrentFrame();
-        pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), Block.NULL_BLOCK);
+        pushCallFrame(frame.getKlazz(), frame.getName(), executeObject, Block.NULL_BLOCK);
         frame = getCurrentFrame();
         frame.setVisibility(Visibility.PUBLIC);
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(clazz, self, name, block, callNumber);
         if (index + 1 == stack.length) {
             expandFrameStack();
         }
     }
     
     private void pushEvalFrame(IRubyObject self) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrameForEval(self, callNumber);
         if (index + 1 == stack.length) {
             expandFrameStack();
         }
     }
     
     public void pushFrame() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFrameStack();
         }
     }
     
     public void popFrame() {
         Frame[] stack = frameStack;
         int index = frameIndex--;
         Frame frame = stack[index];
         
         // if the frame was captured, we must replace it but not clear
         if (frame.isCaptured()) {
             stack[index] = new Frame();
         } else {
             frame.clear();
         }
     }
         
     private void popFrameReal(Frame oldFrame) {
         frameStack[frameIndex--] = oldFrame;
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
     
     public Frame getNextFrame() {
         int index = frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFrameStack();
         }
         return stack[index + 1];
     }
     
     public Frame getPreviousFrame() {
         int index = frameIndex;
         return index < 1 ? null : frameStack[index - 1];
     }
     
     /**
      * Set the $~ (backref) "global" to the given value.
      * 
      * @param match the value to set
      * @return the value passed in
      */
     public IRubyObject setBackRef(IRubyObject match) {
         return getCurrentFrame().setBackRef(match);
     }
     
     /**
      * Get the value of the $~ (backref) "global".
      * 
      * @return the value of $~
      */
     public IRubyObject getBackRef() {
         return getCurrentFrame().getBackRef(nil);
     }
     
     /**
      * Set the $_ (lastlne) "global" to the given value.
      * 
      * @param last the value to set
      * @return the value passed in
      */
     public IRubyObject setLastLine(IRubyObject last) {
         return getCurrentFrame().setLastLine(last);
     }
     
     /**
      * Get the value of the $_ (lastline) "global".
      * 
      * @return the value of $_
      */
     public IRubyObject getLastLine() {
         return getCurrentFrame().getLastLine(nil);
     }
 
     /////////////////// BACKTRACE ////////////////////
 
     private static void expandBacktraceStack(ThreadContext context) {
         int newSize = context.backtrace.length * 2;
         context.backtrace = fillNewBacktrace(context, new BacktraceElement[newSize], newSize);
     }
 
     private static BacktraceElement[] fillNewBacktrace(ThreadContext context, BacktraceElement[] newBacktrace, int newSize) {
         System.arraycopy(context.backtrace, 0, newBacktrace, 0, context.backtrace.length);
 
         for (int i = context.backtrace.length; i < newSize; i++) {
             newBacktrace[i] = new BacktraceElement();
         }
 
         return newBacktrace;
     }
 
     public static void pushBacktrace(ThreadContext context, String method, String file, int line) {
         int index = ++context.backtraceIndex;
         BacktraceElement[] stack = context.backtrace;
         BacktraceElement.update(stack[index], method, file, line);
         if (index + 1 == stack.length) {
             ThreadContext.expandBacktraceStack(context);
         }
     }
 
     public static void popBacktrace(ThreadContext context) {
         context.backtraceIndex--;
     }
 
     /**
      * Check if a static scope is present on the call stack.
      * This is the IR equivalent of isJumpTargetAlive
      *
      * @param scope the static scope to look for
      * @return true if it exists
      *         false if not
      **/
     public boolean scopeExistsOnCallStack(DynamicScope scope) {
         DynamicScope[] stack = scopeStack;
         for (int i = scopeIndex; i >= 0; i--) {
            if (stack[i] == scope) return true;
         }
         return false;
     }
 
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return backtrace[backtraceIndex].filename;
     }
     
     public int getLine() {
         return backtrace[backtraceIndex].line;
     }
     
     public void setLine(int line) {
         backtrace[backtraceIndex].line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         BacktraceElement b = backtrace[backtraceIndex];
         b.filename = file;
         b.line = line;
     }
 
     public void setFileAndLine(ISourcePosition position) {
         BacktraceElement b = backtrace[backtraceIndex];
         b.filename = position.getFile();
         b.line = position.getLine();
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
       public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
     }
     
     public void pollThreadEvents() {
         thread.pollThreadEvents(this);
     }
     
     public int callNumber = 0;
 
     public int getCurrentTarget() {
         return callNumber;
     }
     
     public void callThreadPoll() {
         if ((callNumber++ & CALL_POLL_COUNT) == 0) pollThreadEvents();
     }
 
     public static void callThreadPoll(ThreadContext context) {
         if ((context.callNumber++ & CALL_POLL_COUNT) == 0) context.pollThreadEvents();
     }
     
     public void trace(RubyEvent event, String name, RubyModule implClass) {
         trace(event, name, implClass, backtrace[backtraceIndex].filename, backtrace[backtraceIndex].line);
     }
 
     public void trace(RubyEvent event, String name, RubyModule implClass, String file, int line) {
         runtime.callEventHooks(this, event, file, line, name, implClass);
     }
 
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     @Deprecated
     public IRubyObject getConstant(String internedName) {
         return getCurrentStaticScope().getConstant(internedName);
     }
 
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, RubyStackTraceElement element) {
         RubyString str = RubyString.newString(runtime, element.mriStyleString());
         backtrace.append(str);
     }
     
     /**
      * Create an Array with backtrace information for Kernel#caller
      * @param level
      * @param length
      * @return an Array with the backtrace
      */
     public IRubyObject createCallerBacktrace(int level, Integer length, StackTraceElement[] stacktrace) {
         runtime.incrementCallerCount();
         
         RubyStackTraceElement[] trace = getTraceSubset(level, length, stacktrace);
         
         if (trace == null) return nil;
         
         RubyArray newTrace = runtime.newArray(trace.length);
 
         for (int i = level; i - level < trace.length; i++) {
             addBackTraceElement(runtime, newTrace, trace[i - level]);
         }
         
         if (RubyInstanceConfig.LOG_CALLERS) TraceType.dumpCaller(newTrace);
         
         return newTrace;
     }
 
     /**
      * Create an array containing Thread::Backtrace::Location objects for the
      * requested caller trace level and length.
      * 
      * @param level the level at which the trace should start
      * @param length the length of the trace
      * @return an Array with the backtrace locations
      */
     public IRubyObject createCallerLocations(int level, Integer length, StackTraceElement[] stacktrace) {
         RubyStackTraceElement[] trace = getTraceSubset(level, length, stacktrace);
         
         if (trace == null) return nil;
         
         return RubyThread.Location.newLocationArray(runtime, trace);
     }
     
     private RubyStackTraceElement[] getTraceSubset(int level, Integer length, StackTraceElement[] stacktrace) {
         runtime.incrementCallerCount();
         
         if (length != null && length == 0) return new RubyStackTraceElement[0];
         
         RubyStackTraceElement[] trace =
                 TraceType.Gather.CALLER.getBacktraceData(this, stacktrace, false).getBacktrace(runtime);
         
         int traceLength = safeLength(level, length, trace);
         
         if (traceLength < 0) return null;
         
         trace = Arrays.copyOfRange(trace, level, level + traceLength);
         
         if (RubyInstanceConfig.LOG_CALLERS) TraceType.dumpCaller(trace);
         
         return trace;
     }
     
     private static int safeLength(int level, Integer length, RubyStackTraceElement[] trace) {
         int baseLength = trace.length - level;
         return length != null ? Math.min(length, baseLength) : baseLength;
     }
 
     /**
      * Create an Array with backtrace information for a built-in warning
      * @param runtime
      * @return an Array with the backtrace
      */
     public RubyStackTraceElement[] createWarningBacktrace(Ruby runtime) {
         runtime.incrementWarningCount();
 
         RubyStackTraceElement[] trace = gatherCallerBacktrace();
 
         if (RubyInstanceConfig.LOG_WARNINGS) TraceType.dumpWarning(trace);
 
         return trace;
     }
     
     public RubyStackTraceElement[] gatherCallerBacktrace() {
         return Gather.CALLER.getBacktraceData(this, false).getBacktrace(runtime);
     }
 
     public boolean isEventHooksEnabled() {
         return eventHooksEnabled;
     }
 
     public void setEventHooksEnabled(boolean flag) {
         eventHooksEnabled = flag;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public BacktraceElement[] createBacktrace2(int level, boolean nativeException) {
         BacktraceElement[] backtraceClone = backtrace.clone();
         int backtraceIndex = this.backtraceIndex;
         BacktraceElement[] newTrace = new BacktraceElement[backtraceIndex + 1];
         for (int i = 0; i <= backtraceIndex; i++) {
             newTrace[i] = backtraceClone[i];
         }
         return newTrace;
     }
     
     private static String createRubyBacktraceString(StackTraceElement element) {
         return element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'";
     }
     
     public static String createRawBacktraceStringFromThrowable(Throwable t) {
         StackTraceElement[] javaStackTrace = t.getStackTrace();
         
         StringBuilder buffer = new StringBuilder();
         if (javaStackTrace != null && javaStackTrace.length > 0) {
             StackTraceElement element = javaStackTrace[0];
 
             buffer
                     .append(createRubyBacktraceString(element))
                     .append(": ")
                     .append(t.toString())
                     .append("\n");
             for (int i = 1; i < javaStackTrace.length; i++) {
                 element = javaStackTrace[i];
                 
                 buffer
                         .append("\tfrom ")
                         .append(createRubyBacktraceString(element));
                 if (i + 1 < javaStackTrace.length) buffer.append("\n");
             }
         }
         
         return buffer.toString();
     }
 
     private Frame pushFrameForBlock(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setVisibility(binding.getVisibility());
         
         return lastFrame;
     }
 
     private Frame pushFrameForEval(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setVisibility(binding.getVisibility());
         return lastFrame;
     }
     
     public void preAdoptThread() {
         pushFrame();
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
 
     public void preExtensionLoad(IRubyObject self) {
         pushFrame();
         getCurrentFrame().setSelf(self);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
     }
 
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         StaticScope staticScope = runtime.getStaticScopeFactory().newLocalScope(null);
         staticScope.setVariables(names);
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
 
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, Block block,
             StaticScope staticScope) {
         pushCallFrame(clazz, name, self, block);
         pushScope(DynamicScope.newDynamicScope(staticScope));
     }
     
     public void preMethodFrameAndDummyScope(RubyModule clazz, String name, IRubyObject self, Block block,
             StaticScope staticScope) {
         pushCallFrame(clazz, name, self, block);
         pushScope(staticScope.getDummyScope());
     }
 
     public void preMethodNoFrameAndDummyScope(StaticScope staticScope) {
         pushScope(staticScope.getDummyScope());
     }
     
     public void postMethodFrameAndScope() {
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, Block block) {
         pushCallFrame(clazz, name, self, block);
     }
     
     public void postMethodFrameOnly() {
         popFrame();
     }
     
     public void preMethodScopeOnly(StaticScope staticScope) {
         pushScope(DynamicScope.newDynamicScope(staticScope));
     }
     
     public void postMethodScopeOnly() {
         popScope();
     }
     
     public void preMethodBacktraceAndScope(String name, StaticScope staticScope) {
         preMethodScopeOnly(staticScope);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
     }
     
     public void preMethodBacktraceOnly(String name) {
     }
 
     public void preMethodBacktraceDummyScope(String name, StaticScope staticScope) {
         pushScope(staticScope.getDummyScope());
     }
     
     public void postMethodBacktraceOnly() {
     }
 
     public void postMethodBacktraceDummyScope() {
         popScope();
     }
     
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
     
     public void preNodeEval(IRubyObject self) {
         pushEvalFrame(self);
     }
     
     public void postNodeEval() {
         popFrame();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
-    public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
+    public void preExecuteUnder(IRubyObject executeUnderObj, RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         DynamicScope scope = getCurrentScope();
         StaticScope sScope = runtime.getStaticScopeFactory().newBlockScope(scope.getStaticScope());
         sScope.setModule(executeUnderClass);
         pushScope(DynamicScope.newDynamicScope(sScope, scope));
-        pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block);
+        pushCallFrame(frame.getKlazz(), frame.getName(), executeUnderObj, block);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popScope();
     }
 
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public Frame preForBlock(Binding binding) {
         Frame lastFrame = preYieldNoScope(binding);
         pushScope(binding.getDynamicScope());
         return lastFrame;
     }
     
     public Frame preYieldSpecificBlock(Binding binding, StaticScope scope) {
         Frame lastFrame = preYieldNoScope(binding);
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         return lastFrame;
     }
     
     public Frame preYieldLightBlock(Binding binding, DynamicScope emptyScope) {
         Frame lastFrame = preYieldNoScope(binding);
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         return lastFrame;
     }
     
     public Frame preYieldNoScope(Binding binding) {
         return pushFrameForBlock(binding);
     }
     
     public void preEvalScriptlet(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postEvalScriptlet() {
         popScope();
     }
     
     public Frame preEvalWithBinding(Binding binding) {
         return pushFrameForEval(binding);
     }
     
     public void postEvalWithBinding(Binding binding, Frame lastFrame) {
         popFrameReal(lastFrame);
     }
     
     public void postYield(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
     }
     
     public void postYieldLight(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
     }
     
     public void postYieldNoScope(Frame lastFrame) {
         popFrameReal(lastFrame);
     }
     
     public void preScopedBody(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postScopedBody() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callEventHooks(ThreadContext, RubyEvent, String, int, String, org.jruby.runtime.builtin.IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callEventHooks(ThreadContext, RubyEvent, String, int, String, org.jruby.runtime.builtin.IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Return a binding representing the current call's state
      * @return the current binding
      */
     public Binding currentBinding() {
         Frame frame = getCurrentFrame().capture();
         return new Binding(frame, getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding currentBinding(IRubyObject self) {
         Frame frame = getCurrentFrame().capture();
         return new Binding(self, frame, frame.getVisibility(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility and self.
      * @param self the self object to use
      * @param visibility the visibility to use
      * @return the current binding using the specified self and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility) {
         Frame frame = getCurrentFrame().capture();
         return new Binding(self, frame, visibility, getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified scope and self.
      * @param self the self object to use
      * @param scope the scope to use
      * @return the current binding using the specified self and scope
      */
     public Binding currentBinding(IRubyObject self, DynamicScope scope) {
         Frame frame = getCurrentFrame().capture();
         return new Binding(self, frame, frame.getVisibility(), scope, backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility, scope, and self. For shared-scope binding
      * consumers like for loops.
      * 
      * @param self the self object to use
      * @param visibility the visibility to use
      * @param scope the scope to use
      * @return the current binding using the specified self, scope, and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility, DynamicScope scope) {
         Frame frame = getCurrentFrame().capture();
         return new Binding(self, frame, visibility, scope, backtrace[backtraceIndex].clone());
     }
 
     /**
      * Get the profile collection for this thread (ThreadContext).
      *
      * @return the thread's profile collection
      *
      */
     public ProfileCollection getProfileCollection() {
         return profileCollection;
     }
 
     public void startProfiling() {
         isProfiling = true;
         // use new profiling data every time profiling is started, useful in
         // case users keep a reference to previous data after profiling stop
         profileCollection = getRuntime().getProfilingService().newProfileCollection( this );
     }
     
     public void stopProfiling() {
         isProfiling = false;
     }
     
     public boolean isProfiling() {
         return isProfiling;
     }
     
     private int currentMethodSerial = 0;
     
     public int profileEnter(int nextMethod) {
         int previousMethodSerial = currentMethodSerial;
         currentMethodSerial = nextMethod;
         if (isProfiling()) {
             getProfileCollection().profileEnter(nextMethod);
         }
         return previousMethodSerial;
     }
 
     public int profileEnter(String name, DynamicMethod nextMethod) {
         if (isProfiling()) {
             // TODO This can be removed, because the profiled method will be added in the MethodEnhancer if necessary
             getRuntime().getProfiledMethods().addProfiledMethod( name, nextMethod );
         }
         return profileEnter((int) nextMethod.getSerialNumber());
     }
     
     public int profileExit(int nextMethod, long startTime) {
         int previousMethodSerial = currentMethodSerial;
         currentMethodSerial = nextMethod;
         if (isProfiling()) {
             getProfileCollection().profileExit(nextMethod, startTime);
         }
         return previousMethodSerial;
     }
     
     public Set<RecursiveComparator.Pair> getRecursiveSet() {
         return recursiveSet;
     }
     
     public void setRecursiveSet(Set<RecursiveComparator.Pair> recursiveSet) {
         this.recursiveSet = recursiveSet;
     }
 
     @Deprecated
     public void setFile(String file) {
         backtrace[backtraceIndex].filename = file;
     }
     
     private Set<RecursiveComparator.Pair> recursiveSet;
     
     @Deprecated
     private org.jruby.util.RubyDateFormat dateFormat;
     
     @Deprecated
     public org.jruby.util.RubyDateFormat getRubyDateFormat() {
         if (dateFormat == null) dateFormat = new org.jruby.util.RubyDateFormat("-", Locale.US, true);
         
         return dateFormat;
     }
 }
