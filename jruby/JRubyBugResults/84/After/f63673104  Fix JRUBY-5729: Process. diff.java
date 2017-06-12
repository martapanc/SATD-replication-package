diff --git a/src/org/jruby/RubyBasicObject.java b/src/org/jruby/RubyBasicObject.java
index 6ad3b998b4..ed3a400d52 100644
--- a/src/org/jruby/RubyBasicObject.java
+++ b/src/org/jruby/RubyBasicObject.java
@@ -1054,1919 +1054,1919 @@ public class RubyBasicObject implements Cloneable, IRubyObject, Serializable, Co
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
         return context.getRuntime().newBoolean(!invokedynamic(context, this, OP_EQUAL, other).isTrue());
     }
 
     public int compareTo(IRubyObject other) {
         return (int)invokedynamic(getRuntime().getCurrentContext(), this, OP_CMP, other).convertToInteger().getLongValue();
     }
 
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
         return that == other || invokedynamic(context, that, OP_EQUAL, other).isTrue();
     }
 
     /** method used for Hash key comparison (specialized for String, Symbol and Fixnum)
      *
      * Will by default just call the Ruby method "eql?"
      */
     public boolean eql(IRubyObject other) {
         return invokedynamic(getRuntime().getCurrentContext(), this, EQL, other).isTrue();
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
 
     // These are added to allow their being called against BasicObject and
     // subclass instances. Because Kernel can be included into BasicObject and
     // subclasses, there's a possibility of calling them from Ruby.
     // See JRUBY-4871
 
     /** rb_obj_equal
      *
      * Will use Java identity equality.
      */
     public IRubyObject equal_p(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
     }
 
     /** rb_obj_equal
      *
      * Just like "==" and "equal?", "eql?" will use identity equality for Object.
      */
     public IRubyObject eql_p(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
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
     protected void checkFrozen() {
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
-        IRubyObject respond = getRuntime().newBoolean(getMetaClass().isMethodBound(name, true));
+        IRubyObject respond = getRuntime().newBoolean(getMetaClass().isMethodBound(name, true, true));
         if (!respond.isTrue()) {
             respond = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "respond_to_missing?", mname, getRuntime().getFalse());
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
             respond = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "respond_to_missing?", mname, includePrivate);
             respond = getRuntime().newBoolean(respond.isTrue());
         }
         return respond;
     }
 
     /** rb_obj_id_obsolete
      *
      * Old id version. This one is bound to the "id" name and will emit a deprecation warning.
      */
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
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#type is deprecated; use Object#class", "Object#type", "Object#class");
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
     public RubyBoolean frozen_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_untrusted
      *  call-seq:
      *     obj.untrusted?    => true or false
      *
      *  Returns <code>true</code> if the object is untrusted.
      */
     public RubyBoolean untrusted_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isUntrusted());
     }
 
     /** rb_obj_untrust
      *  call-seq:
      *     obj.untrust -> obj
      *
      *  Marks <i>obj</i> as untrusted.
      */
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
     public IRubyObject trust(ThreadContext context) {
         if (isUntrusted() && !isImmediate()) {
             checkFrozen();
             flags &= ~UNTRUSTED_F;
         }
         return this;
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
 
         RubyArray singletonMethods;
         if (getMetaClass().isSingleton()) {
             IRubyObject[] methodsArgs = new IRubyObject[]{context.getRuntime().getFalse()};
             singletonMethods = collect.instanceMethods(getMetaClass(), methodsArgs);
 
             if (all) {
                 RubyClass superClass = getMetaClass().getSuperClass();
                 while (superClass.isSingleton() || superClass.isIncluded()) {
                     singletonMethods.concat(collect.instanceMethods(superClass, methodsArgs));
                     superClass = superClass.getSuperClass();
                 }
             }
         } else {
             singletonMethods = context.getRuntime().newEmptyArray();
         }
 
         return singletonMethods;
     }
 
     private abstract static class MethodsCollector {
         public abstract RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args);
     };
 
     private static final MethodsCollector methodsCollector = new MethodsCollector() {
         public RubyArray instanceMethods(RubyClass rubyClass, IRubyObject[] args) {
             return rubyClass.instance_methods(args);
         }
     };
 
     private static final MethodsCollector methodsCollector19 = new MethodsCollector() {
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
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "default 'to_a' will be obsolete", "to_a");
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
         return specificEval(context, getInstanceEvalClass(), block);
     }
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, block);
     }
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, block);
     }
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, arg2, block);
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
         if (!block.isGiven()) throw context.getRuntime().newArgumentError("block not supplied");
 
         RubyModule klazz;
         if (isImmediate()) {
             // Ruby uses Qnil here, we use "dummy" because we need a class
             klazz = context.getRuntime().getDummy();
         } else {
             klazz = getSingletonClass();
         }
 
         return yieldUnder(context, klazz, args, block);
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
         throw context.getRuntime().newArgumentError(0, 1);
     }
     public IRubyObject send(ThreadContext context, IRubyObject arg0, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, block);
     }
     public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, arg1, block);
     }
     public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, arg1, arg2, block);
     }
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
     	return context.getRuntime().getFalse();
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
     	return context.getRuntime().getFalse();
     }
 
     public IRubyObject op_match19(ThreadContext context, IRubyObject arg) {
     	return context.getRuntime().getNil();
     }
 
     public IRubyObject op_not_match(ThreadContext context, IRubyObject arg) {
         return context.getRuntime().newBoolean(! callMethod(context, "=~", arg).isTrue());
     }
 
 
     //
     // INSTANCE VARIABLE RUBY METHODS
     //
 
     /** rb_obj_ivar_defined
      *
      *  call-seq:
      *     obj.instance_variable_defined?(symbol)    => true or false
      *
      *  Returns <code>true</code> if the given instance variable is
      *  defined in <i>obj</i>.
      *
      *     class Fred
      *       def initialize(p1, p2)
      *         @a, @b = p1, p2
      *       end
      *     end
      *     fred = Fred.new('cat', 99)
      *     fred.instance_variable_defined?(:@a)    #=> true
      *     fred.instance_variable_defined?("@b")   #=> true
      *     fred.instance_variable_defined?("@c")   #=> false
      */
     public IRubyObject instance_variable_defined_p(ThreadContext context, IRubyObject name) {
         if (variableTableContains(validateInstanceVariable(name.asJavaString()))) {
             return context.getRuntime().getTrue();
         }
         return context.getRuntime().getFalse();
     }
 
     /** rb_obj_ivar_get
      *
      *  call-seq:
      *     obj.instance_variable_get(symbol)    => obj
      *
      *  Returns the value of the given instance variable, or nil if the
      *  instance variable is not set. The <code>@</code> part of the
      *  variable name should be included for regular instance
      *  variables. Throws a <code>NameError</code> exception if the
      *  supplied symbol is not valid as an instance variable name.
      *
      *     class Fred
      *       def initialize(p1, p2)
      *         @a, @b = p1, p2
      *       end
      *     end
      *     fred = Fred.new('cat', 99)
      *     fred.instance_variable_get(:@a)    #=> "cat"
      *     fred.instance_variable_get("@b")   #=> 99
      */
     public IRubyObject instance_variable_get(ThreadContext context, IRubyObject name) {
         Object value;
         if ((value = variableTableFetch(validateInstanceVariable(name.asJavaString()))) != null) {
             return (IRubyObject)value;
         }
         return context.getRuntime().getNil();
     }
 
     /** rb_obj_ivar_set
      *
      *  call-seq:
      *     obj.instance_variable_set(symbol, obj)    => obj
      *
      *  Sets the instance variable names by <i>symbol</i> to
      *  <i>object</i>, thereby frustrating the efforts of the class's
      *  author to attempt to provide proper encapsulation. The variable
      *  did not have to exist prior to this call.
      *
      *     class Fred
      *       def initialize(p1, p2)
      *         @a, @b = p1, p2
      *       end
      *     end
      *     fred = Fred.new('cat', 99)
      *     fred.instance_variable_set(:@a, 'dog')   #=> "dog"
      *     fred.instance_variable_set(:@c, 'cat')   #=> "cat"
      *     fred.inspect                             #=> "#<Fred:0x401b3da8 @a=\"dog\", @b=99, @c=\"cat\">"
      */
     public IRubyObject instance_variable_set(IRubyObject name, IRubyObject value) {
         ensureInstanceVariablesSettable();
         return (IRubyObject)variableTableStore(validateInstanceVariable(name.asJavaString()), value);
     }
 
     /** rb_obj_remove_instance_variable
      *
      *  call-seq:
      *     obj.remove_instance_variable(symbol)    => obj
      *
      *  Removes the named instance variable from <i>obj</i>, returning that
      *  variable's value.
      *
      *     class Dummy
      *       attr_reader :var
      *       def initialize
      *         @var = 99
      *       end
      *       def remove
      *         remove_instance_variable(:@var)
      *       end
      *     end
      *     d = Dummy.new
      *     d.var      #=> 99
      *     d.remove   #=> 99
      *     d.var      #=> nil
      */
     public IRubyObject remove_instance_variable(ThreadContext context, IRubyObject name, Block block) {
         ensureInstanceVariablesSettable();
         IRubyObject value;
         if ((value = (IRubyObject)variableTableRemove(validateInstanceVariable(name.asJavaString()))) != null) {
             return value;
         }
         throw context.getRuntime().newNameError("instance variable " + name.asJavaString() + " not defined", name.asJavaString());
     }
 
     /** rb_obj_instance_variables
      *
      *  call-seq:
      *     obj.instance_variables    => array
      *
      *  Returns an array of instance variable names for the receiver. Note
      *  that simply defining an accessor does not create the corresponding
      *  instance variable.
      *
      *     class Fred
      *       attr_accessor :a1
      *       def initialize
      *         @iv = 3
      *       end
      *     end
      *     Fred.new.instance_variables   #=> ["@iv"]
      */
     public RubyArray instance_variables(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         List<String> nameList = getInstanceVariableNameList();
 
         RubyArray array = runtime.newArray(nameList.size());
 
         for (String name : nameList) {
             array.append(runtime.newString(name));
         }
 
         return array;
     }
 
     // In 1.9, return symbols
     public RubyArray instance_variables19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         List<String> nameList = getInstanceVariableNameList();
 
         RubyArray array = runtime.newArray(nameList.size());
 
         for (String name : nameList) {
             array.append(runtime.newSymbol(name));
         }
 
         return array;
     }
 
     /**
      * Checks if the name parameter represents a legal instance variable name, and otherwise throws a Ruby NameError
      */
     protected String validateInstanceVariable(String name) {
         if (IdUtil.isValidInstanceVariableName(name)) return name;
 
         throw getRuntime().newNameError("`" + name + "' is not allowable as an instance variable name", name);
     }
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 13bed0dea8..7f99238633 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -755,1383 +755,1389 @@ public class RubyKernel {
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
 
         int status = hard ? 1 : 0;
 
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
     
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), context.currentBinding(recv));
     }
     
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = RUBY1_9)
     public static RubyBinding binding19(ThreadContext context, IRubyObject recv, Block block) {
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
             case 2:
                 raise = new RaiseException(convertToException(runtime, args[0], args[1]));
                 break;
             default:
                 raise = new RaiseException(convertToException(runtime, args[0], args[1]), args[2]);
                 break;
         }
 
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
         RubyStackTraceElement[] elements = rEx.getBacktraceElements();
         RubyStackTraceElement firstElement = elements[0];
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
 
         runtime.getLoadService().load(file.toString(), wrap);
 
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
         long[] tuple;
 
         try {
             // NOTE: not searching executable path before invoking args
             tuple = ShellLauncher.runAndWaitPid(runtime, args, output, false);
         } catch (Exception e) {
             tuple = new long[] {127, -1};
         }
 
         context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple[0], tuple[1]));
 
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
         long pid = ShellLauncher.runExternalWithoutWait(runtime, args);
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
         long[] tuple;
 
         try {
             if (! Platform.IS_WINDOWS && args[args.length -1].asJavaString().matches(".*[^&]&\\s*")) {
                 // looks like we need to send process to the background
                 ShellLauncher.runWithoutWait(runtime, args);
                 return 0;
             }
             tuple = ShellLauncher.runAndWaitPid(runtime, args);
         } catch (Exception e) {
             tuple = new long[] {127, -1};
         }
 
         context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, tuple[0], tuple[1]));
         return (int)tuple[0];
     }
     
     @JRubyMethod(name = {"exec"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject exec(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         // This is a fairly specific hack for empty string, but it does the job
         if (args.length == 1 && args[0].convertToString().isEmpty()) {
             throw runtime.newErrnoENOENTError(args[0].convertToString().toString());
         }
         
         int resultCode;
         boolean nativeFailed = false;
         try {
             try {
                 String[] argv = new String[args.length];
                 for (int i = 0; i < args.length; i++) {
                     argv[i] = args[i].asJavaString();
                 }
                 resultCode = runtime.getPosix().exec(null, argv);
                 // Only here because native exec could not exec (always -1)
                 nativeFailed = true;
             } catch (RaiseException e) {  // Not implemented error
                 // Fall back onto our existing code if native not available
                 // FIXME: Make jnr-posix Pure-Java backend do this as well
                 resultCode = ShellLauncher.execAndWait(runtime, args);
             }
         } catch (RaiseException e) {
             throw e; // no need to wrap this exception
         } catch (Exception e) {
             throw runtime.newErrnoENOENTError("cannot execute");
         }
         
         if (nativeFailed) throw runtime.newErrnoENOENTError("cannot execute");
         
         exit(runtime, new IRubyObject[] {runtime.newFixnum(resultCode)}, true);
 
         // not reached
         return runtime.getNil();
     }
 
-    @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
+    @JRubyMethod(name = "fork", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.getRuntime();
         throw runtime.newNotImplementedError("fork is not available on this platform");
     }
 
+    @JRubyMethod(name = "fork", module = true, visibility = PRIVATE, compat = RUBY1_9, notImplemented = true)
+    public static IRubyObject fork19(ThreadContext context, IRubyObject recv, Block block) {
+        Ruby runtime = context.getRuntime();
+        throw runtime.newNotImplementedError("fork is not available on this platform");
+    }
+
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
 
     // Moved binding of these methods here, since Kernel can be included into
     // BasicObject subclasses, and these methods must still work.
     // See JRUBY-4871
 
     @JRubyMethod(name = "==", required = 1, compat = RUBY1_8)
     public static IRubyObject op_equal(ThreadContext context, IRubyObject self, IRubyObject other) {
         return ((RubyBasicObject)self).op_equal(context, other);
     }
 
     @JRubyMethod(name = "equal?", required = 1, compat = RUBY1_8)
     public static IRubyObject equal_p(ThreadContext context, IRubyObject self, IRubyObject other) {
         return ((RubyBasicObject)self).equal_p(context, other);
     }
 
     @JRubyMethod(name = "eql?", required = 1)
     public static IRubyObject eql_p(IRubyObject self, IRubyObject obj) {
         return ((RubyBasicObject)self).eql_p(obj);
     }
 
     @JRubyMethod(name = "===", required = 1)
     public static IRubyObject op_eqq(ThreadContext context, IRubyObject self, IRubyObject other) {
         return ((RubyBasicObject)self).op_eqq(context, other);
     }
 
     @JRubyMethod(name = "<=>", required = 1, compat = RUBY1_9)
     public static IRubyObject op_cmp(ThreadContext context, IRubyObject self, IRubyObject other) {
         return ((RubyBasicObject)self).op_cmp(context, other);
     }
 
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = PRIVATE)
     public static IRubyObject initialize_copy(IRubyObject self, IRubyObject original) {
         return ((RubyBasicObject)self).initialize_copy(original);
     }
 
     @JRubyMethod(name = "respond_to?", compat = RUBY1_8)
     public static RubyBoolean respond_to_p(IRubyObject self, IRubyObject mname) {
         return ((RubyBasicObject)self).respond_to_p(mname);
     }
 
     @JRubyMethod(name = "respond_to?", compat = RUBY1_9)
     public static IRubyObject respond_to_p19(IRubyObject self, IRubyObject mname) {
         return ((RubyBasicObject)self).respond_to_p19(mname);
     }
 
     @JRubyMethod(name = "respond_to?", compat = RUBY1_8)
     public static RubyBoolean respond_to_p(IRubyObject self, IRubyObject mname, IRubyObject includePrivate) {
         return ((RubyBasicObject)self).respond_to_p(mname, includePrivate);
     }
 
     @JRubyMethod(name = "respond_to?", compat = RUBY1_9)
     public static IRubyObject respond_to_p19(IRubyObject self, IRubyObject mname, IRubyObject includePrivate) {
         return ((RubyBasicObject)self).respond_to_p19(mname, includePrivate);
     }
 
     @JRubyMethod(name = {"object_id", "__id__"})
     public static IRubyObject id(IRubyObject self) {
         return ((RubyBasicObject)self).id();
     }
 
     @JRubyMethod(name = "id", compat = RUBY1_8)
     public static IRubyObject id_deprecated(IRubyObject self) {
         return ((RubyBasicObject)self).id_deprecated();
     }
 
     @JRubyMethod(name = "hash")
     public static RubyFixnum hash(IRubyObject self) {
         return ((RubyBasicObject)self).hash();
     }
 
     @JRubyMethod(name = "class")
     public static RubyClass type(IRubyObject self) {
         return ((RubyBasicObject)self).type();
     }
 
     @JRubyMethod(name = "type")
     public static RubyClass type_deprecated(IRubyObject self) {
         return ((RubyBasicObject)self).type_deprecated();
     }
 
     @JRubyMethod(name = "clone")
     public static IRubyObject rbClone(IRubyObject self) {
         return ((RubyBasicObject)self).rbClone();
     }
 
     @JRubyMethod
     public static IRubyObject dup(IRubyObject self) {
         return ((RubyBasicObject)self).dup();
     }
 
     @JRubyMethod(name = "display", optional = 1)
     public static IRubyObject display(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).display(context, args);
     }
 
     @JRubyMethod(name = "tainted?")
     public static RubyBoolean tainted_p(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).tainted_p(context);
     }
 
     @JRubyMethod(name = "taint")
     public static IRubyObject taint(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).taint(context);
     }
 
     @JRubyMethod(name = "untaint")
     public static IRubyObject untaint(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).untaint(context);
     }
 
     @JRubyMethod(name = "freeze")
     public static IRubyObject freeze(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).freeze(context);
     }
 
     @JRubyMethod(name = "frozen?")
     public static RubyBoolean frozen_p(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).frozen_p(context);
     }
 
     @JRubyMethod(name = "untrusted?", compat = RUBY1_9)
     public static RubyBoolean untrusted_p(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).untrusted_p(context);
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public static IRubyObject untrust(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).untrust(context);
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public static IRubyObject trust(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).trust(context);
     }
 
     @JRubyMethod(name = "inspect")
     public static IRubyObject inspect(IRubyObject self) {
         return ((RubyBasicObject)self).inspect();
     }
 
     @JRubyMethod(name = "instance_of?", required = 1)
     public static RubyBoolean instance_of_p(ThreadContext context, IRubyObject self, IRubyObject type) {
         return ((RubyBasicObject)self).instance_of_p(context, type);
     }
 
     @JRubyMethod(name = {"kind_of?", "is_a?"}, required = 1)
     public static RubyBoolean kind_of_p(ThreadContext context, IRubyObject self, IRubyObject type) {
         return ((RubyBasicObject)self).kind_of_p(context, type);
     }
 
     @JRubyMethod(name = "methods", optional = 1, compat = RUBY1_8)
     public static IRubyObject methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).methods(context, args);
     }
     @JRubyMethod(name = "methods", optional = 1, compat = RUBY1_9)
     public static IRubyObject methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).methods19(context, args);
     }
 
     @JRubyMethod(name = "public_methods", optional = 1, compat = RUBY1_8)
     public static IRubyObject public_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).public_methods(context, args);
     }
 
     @JRubyMethod(name = "public_methods", optional = 1, compat = RUBY1_9)
     public static IRubyObject public_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).public_methods19(context, args);
     }
 
     @JRubyMethod(name = "protected_methods", optional = 1, compat = RUBY1_8)
     public static IRubyObject protected_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).protected_methods(context, args);
     }
 
     @JRubyMethod(name = "protected_methods", optional = 1, compat = RUBY1_9)
     public static IRubyObject protected_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).protected_methods19(context, args);
     }
 
     @JRubyMethod(name = "private_methods", optional = 1, compat = RUBY1_8)
     public static IRubyObject private_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).private_methods(context, args);
     }
 
     @JRubyMethod(name = "private_methods", optional = 1, compat = RUBY1_9)
     public static IRubyObject private_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).private_methods19(context, args);
     }
 
     @JRubyMethod(name = "singleton_methods", optional = 1, compat = RUBY1_8)
     public static RubyArray singleton_methods(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).singleton_methods(context, args);
     }
 
     @JRubyMethod(name = "singleton_methods", optional = 1 , compat = RUBY1_9)
     public static RubyArray singleton_methods19(ThreadContext context, IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).singleton_methods19(context, args);
     }
 
     @JRubyMethod(name = "method", required = 1)
     public static IRubyObject method(IRubyObject self, IRubyObject symbol) {
         return ((RubyBasicObject)self).method(symbol);
     }
 
     @JRubyMethod(name = "method", required = 1, compat = RUBY1_9)
     public static IRubyObject method19(IRubyObject self, IRubyObject symbol) {
         return ((RubyBasicObject)self).method19(symbol);
     }
 
     @JRubyMethod(name = "to_s")
     public static IRubyObject to_s(IRubyObject self) {
         return ((RubyBasicObject)self).to_s();
     }
 
     @JRubyMethod(name = "to_a", visibility = PUBLIC, compat = RUBY1_8)
     public static RubyArray to_a(IRubyObject self) {
         return ((RubyBasicObject)self).to_a();
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, Block block) {
         return ((RubyBasicObject)self).instance_eval(context, block);
     }
     @JRubyMethod(compat = RUBY1_8)
     public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         return ((RubyBasicObject)self).instance_eval(context, arg0, block);
     }
     @JRubyMethod(compat = RUBY1_8)
     public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         return ((RubyBasicObject)self).instance_eval(context, arg0, arg1, block);
     }
     @JRubyMethod(compat = RUBY1_8)
     public static IRubyObject instance_eval(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return ((RubyBasicObject)self).instance_eval(context, arg0, arg1, arg2, block);
     }
 
     @JRubyMethod(optional = 3, rest = true, compat = RUBY1_8)
     public static IRubyObject instance_exec(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return ((RubyBasicObject)self).instance_exec(context, args, block);
     }
 
     @JRubyMethod(name = "extend", required = 1, rest = true)
     public static IRubyObject extend(IRubyObject self, IRubyObject[] args) {
         return ((RubyBasicObject)self).extend(args);
     }
 
     @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, Block block) {
         return ((RubyBasicObject)self).send(context, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         return ((RubyBasicObject)self).send(context, arg0, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         return ((RubyBasicObject)self).send(context, arg0, arg1, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return ((RubyBasicObject)self).send(context, arg0, arg1, arg2, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, rest = true, compat = RUBY1_8)
     public static IRubyObject send(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return ((RubyBasicObject)self).send(context, args, block);
     }
 
     @JRubyMethod(name = {"send"}, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, Block block) {
         return ((RubyBasicObject)self).send19(context, block);
     }
     @JRubyMethod(name = {"send"}, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         return ((RubyBasicObject)self).send19(context, arg0, block);
     }
     @JRubyMethod(name = {"send"}, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         return ((RubyBasicObject)self).send19(context, arg0, arg1, block);
     }
     @JRubyMethod(name = {"send"}, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return ((RubyBasicObject)self).send19(context, arg0, arg1, arg2, block);
     }
     @JRubyMethod(name = {"send"}, rest = true, compat = RUBY1_9)
     public static IRubyObject send19(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         return ((RubyBasicObject)self).send19(context, args, block);
     }
 
     @JRubyMethod(name = "nil?")
     public static IRubyObject nil_p(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).nil_p(context);
     }
 
     @JRubyMethod(name = "=~", required = 1, compat = RUBY1_8)
     public static IRubyObject op_match(ThreadContext context, IRubyObject self, IRubyObject arg) {
         return ((RubyBasicObject)self).op_match(context, arg);
     }
 
     @JRubyMethod(name = "=~", required = 1, compat = RUBY1_9)
     public static IRubyObject op_match19(ThreadContext context, IRubyObject self, IRubyObject arg) {
         return ((RubyBasicObject)self).op_match19(context, arg);
     }
 
     @JRubyMethod(name = "!~", required = 1, compat = RUBY1_9)
     public static IRubyObject op_not_match(ThreadContext context, IRubyObject self, IRubyObject arg) {
         return ((RubyBasicObject)self).op_not_match(context, arg);
     }
 
     @JRubyMethod(name = "instance_variable_defined?", required = 1)
     public static IRubyObject instance_variable_defined_p(ThreadContext context, IRubyObject self, IRubyObject name) {
         return ((RubyBasicObject)self).instance_variable_defined_p(context, name);
     }
 
     @JRubyMethod(name = "instance_variable_get", required = 1)
     public static IRubyObject instance_variable_get(ThreadContext context, IRubyObject self, IRubyObject name) {
         return ((RubyBasicObject)self).instance_variable_get(context, name);
     }
 
     @JRubyMethod(name = "instance_variable_set", required = 2)
     public static IRubyObject instance_variable_set(IRubyObject self, IRubyObject name, IRubyObject value) {
         return ((RubyBasicObject)self).instance_variable_set(name, value);
     }
 
     @JRubyMethod(visibility = PRIVATE)
     public static IRubyObject remove_instance_variable(ThreadContext context, IRubyObject self, IRubyObject name, Block block) {
         return ((RubyBasicObject)self).remove_instance_variable(context, name, block);
     }
 
     @JRubyMethod(name = "instance_variables")
     public static RubyArray instance_variables(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).instance_variables(context);
     }
 
     @JRubyMethod(name = "instance_variables", compat = RUBY1_9)
     public static RubyArray instance_variables19(ThreadContext context, IRubyObject self) {
         return ((RubyBasicObject)self).instance_variables19(context);
     }
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index a63a2b2d8c..c4888e74a2 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -359,2000 +359,2009 @@ public class RubyModule extends RubyObject {
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (fullName == null) {
             calculateName();
         }
         return fullName;
     }
 
     private void calculateName() {
         fullName = calculateFullName();
     }
 
     private String calculateFullName() {
         if (getBaseName() == null) {
             if (bareName == null) {
                 if (isClass()) {
                     bareName = "#<" + "Class"  + ":0x1" + String.format("%08x", System.identityHashCode(this)) + ">";
                 } else {
                     bareName = "#<" + "Module" + ":0x1" + String.format("%08x", System.identityHashCode(this)) + ">";
                 }
             }
 
             return bareName;
         }
 
         String result = getBaseName();
         RubyClass objectClass = getRuntime().getObject();
 
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
             String pName = p.getBaseName();
             // This is needed when the enclosing class or module is a singleton.
             // In that case, we generated a name such as null::Foo, which broke 
             // Marshalling, among others. The correct thing to do in this situation 
             // is to insert the generate the name of form #<Class:01xasdfasd> if 
             // it's a singleton module/class, which this code accomplishes.
             if(pName == null) {
                 pName = p.getName();
             }
             result = pName + "::" + result;
         }
 
         return result;
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     @Deprecated
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module;
         if ((module = getConstantAt(name)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
     }
 
     public RubyClass fastGetClass(String internedName) {
         IRubyObject module;
         if ((module = fastGetConstantAt(internedName)) instanceof RubyClass) {
             return (RubyClass)module;
         }
         return null;
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
         checkForCyclicInclude(module);
 
         infectBy(module);
 
         doIncludeModule(module);
         invalidateConstantCache();
         invalidateCoreClasses();
         invalidateCacheDescendants();
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 PRIVATE : PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
     
     public void defineAnnotatedMethod(Class clazz, String name) {
         // FIXME: This is probably not very efficient, since it loads all methods for each call
         boolean foundMethod = false;
         for (Method method : clazz.getDeclaredMethods()) {
             if (method.getName().equals(name) && defineAnnotatedMethod(method, MethodFactory.createFactory(getRuntime().getJRubyClassLoader()))) {
                 foundMethod = true;
             }
         }
 
         if (!foundMethod) {
             throw new RuntimeException("No JRubyMethod present for method " + name + "on class " + clazz.getName());
         }
     }
     
     public void defineAnnotatedConstants(Class clazz) {
         Field[] declaredFields = clazz.getDeclaredFields();
         for (Field field : declaredFields) {
             if (Modifier.isStatic(field.getModifiers())) {
                 defineAnnotatedConstant(field);
             }
         }
     }
 
     public boolean defineAnnotatedConstant(Field field) {
         JRubyConstant jrubyConstant = field.getAnnotation(JRubyConstant.class);
 
         if (jrubyConstant == null) return false;
 
         String[] names = jrubyConstant.value();
         if(names.length == 0) {
             names = new String[]{field.getName()};
         }
 
         Class tp = field.getType();
         IRubyObject realVal;
 
         try {
             if(tp == Integer.class || tp == Integer.TYPE || tp == Short.class || tp == Short.TYPE || tp == Byte.class || tp == Byte.TYPE) {
                 realVal = RubyNumeric.int2fix(getRuntime(), field.getInt(null));
             } else if(tp == Boolean.class || tp == Boolean.TYPE) {
                 realVal = field.getBoolean(null) ? getRuntime().getTrue() : getRuntime().getFalse();
             } else {
                 realVal = getRuntime().getNil();
             }
         } catch(Exception e) {
             realVal = getRuntime().getNil();
         }
 
         
         for(String name : names) {
             this.fastSetConstant(name, realVal);
         }
 
         return true;
     }
 
     public void defineAnnotatedMethods(Class clazz) {
         defineAnnotatedMethodsIndividually(clazz);
     }
     
     public static class MethodClumper {
         Map<String, List<JavaMethodDescriptor>> annotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_8 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> annotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> staticAnnotatedMethods1_9 = new HashMap<String, List<JavaMethodDescriptor>>();
         Map<String, List<JavaMethodDescriptor>> allAnnotatedMethods = new HashMap<String, List<JavaMethodDescriptor>>();
         
         public void clump(Class cls) {
             Method[] declaredMethods = cls.getDeclaredMethods();
             for (Method method: declaredMethods) {
                 JRubyMethod anno = method.getAnnotation(JRubyMethod.class);
                 if (anno == null) continue;
                 
                 JavaMethodDescriptor desc = new JavaMethodDescriptor(method);
                 
                 String name = anno.name().length == 0 ? method.getName() : anno.name()[0];
                 
                 List<JavaMethodDescriptor> methodDescs;
                 Map<String, List<JavaMethodDescriptor>> methodsHash = null;
                 if (desc.isStatic) {
                     if (anno.compat() == RUBY1_8) {
                         methodsHash = staticAnnotatedMethods1_8;
                     } else if (anno.compat() == RUBY1_9) {
                         methodsHash = staticAnnotatedMethods1_9;
                     } else {
                         methodsHash = staticAnnotatedMethods;
                     }
                 } else {
                     if (anno.compat() == RUBY1_8) {
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
             invalidateCoreClasses();
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
 
             invalidateCoreClasses();
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
         invalidateCacheDescendantsInner();
         // update all hierarchies into which this module has been included
         synchronized (getRuntime().getHierarchyLock()) {
             for (RubyClass includingHierarchy : includingHierarchies) {
                 includingHierarchy.invalidateCacheDescendants();
             }
         }
     }
     
     protected void invalidateCoreClasses() {
         if (!getRuntime().isBooting()) {
             if (this == getRuntime().getFixnum()) {
                 getRuntime().setFixnumReopened(true);
             } else if (this == getRuntime().getFloat()) {
                 getRuntime().setFloatReopened(true);
             }
         }
     }
 
     protected void invalidateCacheDescendantsInner() {
         generation = getRuntime().getNextModuleGeneration();
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
 
         invalidateCoreClasses();
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
         invalidateCoreClasses();
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
                 private RubyClass.VariableAccessor accessor = RubyClass.VariableAccessor.DUMMY_ACCESSOR;
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                     IRubyObject variable = (IRubyObject)verifyAccessor(self.getMetaClass().getRealClass()).get(self);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 private RubyClass.VariableAccessor verifyAccessor(RubyClass cls) {
                     RubyClass.VariableAccessor localAccessor = accessor;
                     if (localAccessor.getClassId() != cls.id) {
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
                 private RubyClass.VariableAccessor accessor = RubyClass.VariableAccessor.DUMMY_ACCESSOR;
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg1) {
                     verifyAccessor(self.getMetaClass().getRealClass()).set(self, arg1);
                     return arg1;
                 }
 
                 private RubyClass.VariableAccessor verifyAccessor(RubyClass cls) {
                     RubyClass.VariableAccessor localAccessor = accessor;
                     if (localAccessor.getClassId() != cls.id) {
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
 
         DynamicMethod method = deepMethodSearch(name, runtime);
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
                 addMethod(name, new WrapperMethod(this, method, visibility));
             }
 
             invalidateCoreClasses();
             invalidateCacheDescendants();
         }
     }
 
     private DynamicMethod deepMethodSearch(String name, Ruby runtime) {
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined() && isModule()) {
             method = runtime.getObject().searchMethod(name);
         }
 
         if (method.isUndefined()) {
             throw runtime.newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
         return method;
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
+    
+    public boolean isMethodBound(String name, boolean checkVisibility, boolean checkRespondTo) {
+        if (!checkRespondTo) return isMethodBound(name, checkVisibility);
+        DynamicMethod method = searchMethod(name);
+        if (!method.isUndefined() && !method.isNotImplemented()) {
+            return !(checkVisibility && method.getVisibility() == PRIVATE);
+        }
+        return false;
+    }
 
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
         Visibility visibility = PUBLIC;
 
         RubyProc proc = runtime.newProc(Block.Type.LAMBDA, block);
 
         // a normal block passed to define_method changes to do arity checking; make it a lambda
         proc.getBlock().type = Block.Type.LAMBDA;
         
         newMethod = createProcMethod(name, visibility, proc);
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
         return proc;
     }
     
     @JRubyMethod(name = "define_method", visibility = PRIVATE, reads = VISIBILITY)
     public IRubyObject define_method(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject body;
         String name = arg0.asJavaString().intern();
         DynamicMethod newMethod = null;
         Visibility visibility = PUBLIC;
 
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
         
         RuntimeHelpers.addInstanceMethod(this, name, newMethod, visibility, context, runtime);
 
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
         scope.makeArgumentScope();
 
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
         if (originalModule.hasVariables()) syncVariables(originalModule);
         syncConstants(originalModule);
 
         originalModule.cloneMethods(this);
 
         return this;
     }
 
     public void syncConstants(RubyModule other) {
         if (other.getConstantMap() != Collections.EMPTY_MAP) {
             getConstantMapForWrite().putAll(other.getConstantMap());
         }
     }
 
     public void syncClassVariables(RubyModule other) {
         if (other.getClassVariablesForRead() != Collections.EMPTY_MAP) {
             getClassVariables().putAll(other.getClassVariablesForRead());
         }
     }
 
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
 
     /** rb_mod_initialize
      *
      */
     @JRubyMethod(name = "initialize", frame = true, compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, Block block) {
         if (block.isGiven()) {
             // class and module bodies default to public, so make the block's visibility public. JRUBY-1185.
             block.getBinding().setVisibility(PUBLIC);
             module_exec(context, block);
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
 
             for (int i = 0; i < args.length; i++) {
                 String name = args[i].asJavaString().intern();
                 DynamicMethod method = deepMethodSearch(name, runtime);
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
diff --git a/src/org/jruby/RubyProcess.java b/src/org/jruby/RubyProcess.java
index e24c5f2f86..191dd3efb3 100644
--- a/src/org/jruby/RubyProcess.java
+++ b/src/org/jruby/RubyProcess.java
@@ -1,989 +1,995 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 
 import com.kenai.constantine.platform.Signal;
 import java.util.EnumSet;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ShellLauncher;
+import static org.jruby.CompatVersion.*;
 
 import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 import static org.jruby.runtime.MethodIndex.OP_EQUAL;
 
 
 /**
  */
 
 @JRubyModule(name="Process")
 public class RubyProcess {
 
     public static RubyModule createProcessModule(Ruby runtime) {
         RubyModule process = runtime.defineModule("Process");
         runtime.setProcess(process);
         
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
         RubyClass process_status = process.defineClassUnder("Status", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setProcStatus(process_status);
         
         RubyModule process_uid = process.defineModuleUnder("UID");
         runtime.setProcUID(process_uid);
         
         RubyModule process_gid = process.defineModuleUnder("GID");
         runtime.setProcGID(process_gid);
         
         RubyModule process_sys = process.defineModuleUnder("Sys");
         runtime.setProcSys(process_sys);
         
         process.defineAnnotatedMethods(RubyProcess.class);
         process_status.defineAnnotatedMethods(RubyStatus.class);
         process_uid.defineAnnotatedMethods(UserID.class);
         process_gid.defineAnnotatedMethods(GroupID.class);
         process_sys.defineAnnotatedMethods(Sys.class);
 
         runtime.loadConstantSet(process, com.kenai.constantine.platform.PRIO.class);
         runtime.loadConstantSet(process, com.kenai.constantine.platform.RLIM.class);
         runtime.loadConstantSet(process, com.kenai.constantine.platform.RLIMIT.class);
         
         process.defineConstant("WNOHANG", runtime.newFixnum(1));
         process.defineConstant("WUNTRACED", runtime.newFixnum(2));
         
         return process;
     }
     
     @JRubyClass(name="Process::Status")
     public static class RubyStatus extends RubyObject {
         private final long status;
         private final long pid;
         
         private static final long EXIT_SUCCESS = 0L;
         public RubyStatus(Ruby runtime, RubyClass metaClass, long status, long pid) {
             super(runtime, metaClass);
             this.status = status;
             this.pid = pid;
         }
         
         public static RubyStatus newProcessStatus(Ruby runtime, long status, long pid) {
             return new RubyStatus(runtime, runtime.getProcStatus(), status, pid);
         }
         
         // Bunch of methods still not implemented
         @JRubyMethod(name = {"to_int", "stopped?", "stopsig", "signaled?", "termsig?", "exited?", "coredump?"}, frame = true)
         public IRubyObject not_implemented() {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod(name = {"&"}, frame = true)
         public IRubyObject not_implemented1(IRubyObject arg) {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod
         public IRubyObject exitstatus() {
             return getRuntime().newFixnum(status);
         }
         
         @Deprecated
         public IRubyObject op_rshift(IRubyObject other) {
             return op_rshift(getRuntime(), other);
         }
         @JRubyMethod(name = ">>")
         public IRubyObject op_rshift(ThreadContext context, IRubyObject other) {
             return op_rshift(context.getRuntime(), other);
         }
         public IRubyObject op_rshift(Ruby runtime, IRubyObject other) {
             long shiftValue = other.convertToInteger().getLongValue();
             return runtime.newFixnum(status >> shiftValue);
         }
 
         @Override
         @JRubyMethod(name = "==")
         public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
             return invokedynamic(context, other, OP_EQUAL, this.to_i(context.getRuntime()));
         }
         
         @Deprecated
         public IRubyObject to_i() {
             return to_i(getRuntime());
         }
         @JRubyMethod
         public IRubyObject to_i(ThreadContext context) {
             return to_i(context.getRuntime());
         }
         public IRubyObject to_i(Ruby runtime) {
             return runtime.newFixnum(shiftedValue());
         }
         
         @Override
         public IRubyObject to_s() {
             return to_s(getRuntime());
         }
         @JRubyMethod
         public IRubyObject to_s(ThreadContext context) {
             return to_s(context.getRuntime());
         }
         public IRubyObject to_s(Ruby runtime) {
             return runtime.newString(String.valueOf(shiftedValue()));
         }
         
         @Override
         public IRubyObject inspect() {
             return inspect(getRuntime());
         }
         @JRubyMethod
         public IRubyObject inspect(ThreadContext context) {
             return inspect(context.getRuntime());
         }
         public IRubyObject inspect(Ruby runtime) {
             return runtime.newString("#<Process::Status: pid=" + pid + ",exited(" + String.valueOf(status) + ")>");
         }
         
         @JRubyMethod(name = "success?")
         public IRubyObject success_p(ThreadContext context) {
             return context.getRuntime().newBoolean(status == EXIT_SUCCESS);
         }
         
         @JRubyMethod
         public IRubyObject pid(ThreadContext context) {
             return context.getRuntime().newFixnum(pid);
         }
         
         private long shiftedValue() {
             return status << 8;
         }
     }
 
     @JRubyModule(name="Process::UID")
     public static class UserID {
         @JRubyMethod(name = "change_privilege", module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::change_privilege not implemented yet");
         }
         
         @Deprecated
         public static IRubyObject eid(IRubyObject self) {
             return euid(self.getRuntime());
         }
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self) {
             return euid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             return eid(self.getRuntime(), arg);
         }
         @JRubyMethod(name = "eid=", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self, IRubyObject arg) {
             return eid(context.getRuntime(), arg);
         }
         public static IRubyObject eid(Ruby runtime, IRubyObject arg) {
             return euid_set(runtime, arg);
         }
         
         @JRubyMethod(name = "grant_privilege", module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(ThreadContext context, IRubyObject self) {
             return switch_rb(context, self, Block.NULL_BLOCK);
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::re_exchangeable? not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject rid(IRubyObject self) {
             return rid(self.getRuntime());
         }
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(ThreadContext context, IRubyObject self) {
             return rid(context.getRuntime());
         }
         public static IRubyObject rid(Ruby runtime) {
             return uid(runtime);
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true, visibility = PRIVATE)
         public static IRubyObject switch_rb(ThreadContext context, IRubyObject self, Block block) {
             Ruby runtime = context.getRuntime();
             int uid = checkErrno(runtime, runtime.getPosix().getuid());
             int euid = checkErrno(runtime, runtime.getPosix().geteuid());
             
             if (block.isGiven()) {
                 try {
                     checkErrno(runtime, runtime.getPosix().seteuid(uid));
                     checkErrno(runtime, runtime.getPosix().setuid(euid));
                     
                     return block.yield(context, runtime.getNil());
                 } finally {
                     checkErrno(runtime, runtime.getPosix().seteuid(euid));
                     checkErrno(runtime, runtime.getPosix().setuid(uid));
                 }
             } else {
                 checkErrno(runtime, runtime.getPosix().seteuid(uid));
                 checkErrno(runtime, runtime.getPosix().setuid(euid));
                 
                 return RubyFixnum.zero(runtime);
             }
         }
     }
     
     @JRubyModule(name="Process::GID")
     public static class GroupID {
         @JRubyMethod(name = "change_privilege", module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::GID::change_privilege not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self) {
             return eid(self.getRuntime());
         }
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self) {
             return eid(context.getRuntime());
         }
         public static IRubyObject eid(Ruby runtime) {
             return egid(runtime);
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             return eid(self.getRuntime(), arg);
         }
         @JRubyMethod(name = "eid=", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self, IRubyObject arg) {
             return eid(context.getRuntime(), arg);
         }
         public static IRubyObject eid(Ruby runtime, IRubyObject arg) {
             return RubyProcess.egid_set(runtime, arg);
         }
         
         @JRubyMethod(name = "grant_privilege", module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::GID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(ThreadContext context, IRubyObject self) {
             return switch_rb(context, self, Block.NULL_BLOCK);
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::GID::re_exchangeable? not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject rid(IRubyObject self) {
             return rid(self.getRuntime());
         }
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(ThreadContext context, IRubyObject self) {
             return rid(context.getRuntime());
         }
         public static IRubyObject rid(Ruby runtime) {
             return gid(runtime);
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::GID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true, visibility = PRIVATE)
         public static IRubyObject switch_rb(ThreadContext context, IRubyObject self, Block block) {
             Ruby runtime = context.getRuntime();
             int gid = checkErrno(runtime, runtime.getPosix().getgid());
             int egid = checkErrno(runtime, runtime.getPosix().getegid());
             
             if (block.isGiven()) {
                 try {
                     checkErrno(runtime, runtime.getPosix().setegid(gid));
                     checkErrno(runtime, runtime.getPosix().setgid(egid));
                     
                     return block.yield(context, runtime.getNil());
                 } finally {
                     checkErrno(runtime, runtime.getPosix().setegid(egid));
                     checkErrno(runtime, runtime.getPosix().setgid(gid));
                 }
             } else {
                 checkErrno(runtime, runtime.getPosix().setegid(gid));
                 checkErrno(runtime, runtime.getPosix().setgid(egid));
                 
                 return RubyFixnum.zero(runtime);
             }
         }
     }
     
     @JRubyModule(name="Process::Sys")
     public static class Sys {
         @Deprecated
         public static IRubyObject getegid(IRubyObject self) {
             return egid(self.getRuntime());
         }
         @JRubyMethod(name = "getegid", module = true, visibility = PRIVATE)
         public static IRubyObject getegid(ThreadContext context, IRubyObject self) {
             return egid(context.getRuntime());
         }
         
         @Deprecated
         public static IRubyObject geteuid(IRubyObject self) {
             return euid(self.getRuntime());
         }
         @JRubyMethod(name = "geteuid", module = true, visibility = PRIVATE)
         public static IRubyObject geteuid(ThreadContext context, IRubyObject self) {
             return euid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject getgid(IRubyObject self) {
             return gid(self.getRuntime());
         }
         @JRubyMethod(name = "getgid", module = true, visibility = PRIVATE)
         public static IRubyObject getgid(ThreadContext context, IRubyObject self) {
             return gid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject getuid(IRubyObject self) {
             return uid(self.getRuntime());
         }
         @JRubyMethod(name = "getuid", module = true, visibility = PRIVATE)
         public static IRubyObject getuid(ThreadContext context, IRubyObject self) {
             return uid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject setegid(IRubyObject recv, IRubyObject arg) {
             return egid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setegid", module = true, visibility = PRIVATE)
         public static IRubyObject setegid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return egid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject seteuid(IRubyObject recv, IRubyObject arg) {
             return euid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "seteuid", module = true, visibility = PRIVATE)
         public static IRubyObject seteuid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return euid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject setgid(IRubyObject recv, IRubyObject arg) {
             return gid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setgid", module = true, visibility = PRIVATE)
         public static IRubyObject setgid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return gid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject setuid(IRubyObject recv, IRubyObject arg) {
             return uid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setuid", module = true, visibility = PRIVATE)
         public static IRubyObject setuid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return uid_set(context.getRuntime(), arg);
         }
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.abort(context, recv, args);
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.exit_bang(recv, args);
     }
 
     @JRubyMethod(name = "groups", module = true, visibility = PRIVATE)
     public static IRubyObject groups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @JRubyMethod(name = "setrlimit", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject setrlimit(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#setrlimit not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject getpgrp(IRubyObject recv) {
         return getpgrp(recv.getRuntime());
     }
     @JRubyMethod(name = "getpgrp", module = true, visibility = PRIVATE)
     public static IRubyObject getpgrp(ThreadContext context, IRubyObject recv) {
         return getpgrp(context.getRuntime());
     }
     public static IRubyObject getpgrp(Ruby runtime) {
         return runtime.newFixnum(runtime.getPosix().getpgrp());
     }
 
     @JRubyMethod(name = "groups=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject groups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject waitpid(IRubyObject recv, IRubyObject[] args) {
         return waitpid(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "waitpid", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject waitpid(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid(context.getRuntime(), args);
     }
     public static IRubyObject waitpid(Ruby runtime, IRubyObject[] args) {
         int pid = -1;
         int flags = 0;
         if (args.length > 0) {
             pid = (int)args[0].convertToInteger().getLongValue();
         }
         if (args.length > 1) {
             flags = (int)args[1].convertToInteger().getLongValue();
         }
         
         int[] status = new int[1];
         runtime.getPosix().errno(0);
         pid = runtime.getPosix().waitpid(pid, status, flags);
         raiseErrnoIfSet(runtime, ECHILD);
         
         runtime.getCurrentContext().setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (status[0] >> 8) & 0xff, pid));
         return runtime.newFixnum(pid);
     }
 
     private interface NonNativeErrno {
         public int handle(Ruby runtime, int result);
     }
 
     private static final NonNativeErrno ECHILD = new NonNativeErrno() {
         public int handle(Ruby runtime, int result) {
             throw runtime.newErrnoECHILDError();
         }
     };
 
     @Deprecated
     public static IRubyObject wait(IRubyObject recv, IRubyObject[] args) {
         return wait(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "wait", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject wait(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return wait(context.getRuntime(), args);
     }
     public static IRubyObject wait(Ruby runtime, IRubyObject[] args) {
         
         if (args.length > 0) {
             return waitpid(runtime, args);
         }
         
         int[] status = new int[1];
         runtime.getPosix().errno(0);
         int pid = runtime.getPosix().wait(status);
         raiseErrnoIfSet(runtime, ECHILD);
         
         runtime.getCurrentContext().setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (status[0] >> 8) & 0xff, pid));
         return runtime.newFixnum(pid);
     }
 
 
     @Deprecated
     public static IRubyObject waitall(IRubyObject recv) {
         return waitall(recv.getRuntime());
     }
     @JRubyMethod(name = "waitall", module = true, visibility = PRIVATE)
     public static IRubyObject waitall(ThreadContext context, IRubyObject recv) {
         return waitall(context.getRuntime());
     }
     public static IRubyObject waitall(Ruby runtime) {
         POSIX posix = runtime.getPosix();
         RubyArray results = runtime.newArray();
         
         int[] status = new int[1];
         int result = posix.wait(status);
         while (result != -1) {
             results.append(runtime.newArray(runtime.newFixnum(result), RubyProcess.RubyStatus.newProcessStatus(runtime, (status[0] >> 8) & 0xff, result)));
             result = posix.wait(status);
         }
         
         return results;
     }
 
     @Deprecated
     public static IRubyObject setsid(IRubyObject recv) {
         return setsid(recv.getRuntime());
     }
     @JRubyMethod(name = "setsid", module = true, visibility = PRIVATE)
     public static IRubyObject setsid(ThreadContext context, IRubyObject recv) {
         return setsid(context.getRuntime());
     }
     public static IRubyObject setsid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setsid()));
     }
 
     @Deprecated
     public static IRubyObject setpgrp(IRubyObject recv) {
         return setpgrp(recv.getRuntime());
     }
     @JRubyMethod(name = "setpgrp", module = true, visibility = PRIVATE)
     public static IRubyObject setpgrp(ThreadContext context, IRubyObject recv) {
         return setpgrp(context.getRuntime());
     }
     public static IRubyObject setpgrp(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setpgid(0, 0)));
     }
 
     @Deprecated
     public static IRubyObject egid_set(IRubyObject recv, IRubyObject arg) {
         return egid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "egid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject egid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return egid_set(context.getRuntime(), arg);
     }
     public static IRubyObject egid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().setegid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject euid(IRubyObject recv) {
         return euid(recv.getRuntime());
     }
     @JRubyMethod(name = "euid", module = true, visibility = PRIVATE)
     public static IRubyObject euid(ThreadContext context, IRubyObject recv) {
         return euid(context.getRuntime());
     }
     public static IRubyObject euid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().geteuid()));
     }
 
     @Deprecated
     public static IRubyObject uid_set(IRubyObject recv, IRubyObject arg) {
         return uid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "uid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject uid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return uid_set(context.getRuntime(), arg);
     }
     public static IRubyObject uid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().setuid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject gid(IRubyObject recv) {
         return gid(recv.getRuntime());
     }
     @JRubyMethod(name = "gid", module = true, visibility = PRIVATE)
     public static IRubyObject gid(ThreadContext context, IRubyObject recv) {
         return gid(context.getRuntime());
     }
     public static IRubyObject gid(Ruby runtime) {
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows
             return RubyFixnum.zero(runtime);
         }
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getgid()));
     }
 
     @JRubyMethod(name = "maxgroups", module = true, visibility = PRIVATE)
     public static IRubyObject maxgroups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject getpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return getpriority(recv.getRuntime(), arg1, arg2);
     }
     @JRubyMethod(name = "getpriority", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject getpriority(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return getpriority(context.getRuntime(), arg1, arg2);
     }
     public static IRubyObject getpriority(Ruby runtime, IRubyObject arg1, IRubyObject arg2) {
         int which = (int)arg1.convertToInteger().getLongValue();
         int who = (int)arg2.convertToInteger().getLongValue();
         int result = checkErrno(runtime, runtime.getPosix().getpriority(which, who));
         
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject uid(IRubyObject recv) {
         return uid(recv.getRuntime());
     }
     @JRubyMethod(name = "uid", module = true, visibility = PRIVATE)
     public static IRubyObject uid(ThreadContext context, IRubyObject recv) {
         return uid(context.getRuntime());
     }
     public static IRubyObject uid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getuid()));
     }
 
     @Deprecated
     public static IRubyObject waitpid2(IRubyObject recv, IRubyObject[] args) {
         return waitpid2(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "waitpid2", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject waitpid2(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid2(context.getRuntime(), args);
     }
     public static IRubyObject waitpid2(Ruby runtime, IRubyObject[] args) {
         int pid = -1;
         int flags = 0;
         if (args.length > 0) {
             pid = (int)args[0].convertToInteger().getLongValue();
         }
         if (args.length > 1) {
             flags = (int)args[1].convertToInteger().getLongValue();
         }
         
         int[] status = new int[1];
         pid = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, flags), ECHILD);
         
         return runtime.newArray(runtime.newFixnum(pid), RubyProcess.RubyStatus.newProcessStatus(runtime, (status[0] >> 8) & 0xff, pid));
     }
 
     @JRubyMethod(name = "initgroups", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject initgroups(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         throw recv.getRuntime().newNotImplementedError("Process#initgroups not yet implemented");
     }
 
     @JRubyMethod(name = "maxgroups=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject maxgroups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups_set not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject ppid(IRubyObject recv) {
         return ppid(recv.getRuntime());
     }
     @JRubyMethod(name = "ppid", module = true, visibility = PRIVATE)
     public static IRubyObject ppid(ThreadContext context, IRubyObject recv) {
         return ppid(context.getRuntime());
     }
     public static IRubyObject ppid(Ruby runtime) {
         int result = checkErrno(runtime, runtime.getPosix().getppid());
 
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject gid_set(IRubyObject recv, IRubyObject arg) {
         return gid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "gid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return gid_set(context.getRuntime(), arg);
     }
     public static IRubyObject gid_set(Ruby runtime, IRubyObject arg) {
         int result = checkErrno(runtime, runtime.getPosix().setgid((int)arg.convertToInteger().getLongValue()));
 
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject wait2(IRubyObject recv, IRubyObject[] args) {
         return waitpid2(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "wait2", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject wait2(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid2(context.getRuntime(), args);
     }
 
     @Deprecated
     public static IRubyObject euid_set(IRubyObject recv, IRubyObject arg) {
         return euid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "euid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject euid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return euid_set(context.getRuntime(), arg);
     }
     public static IRubyObject euid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().seteuid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject setpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return setpriority(recv.getRuntime(), arg1, arg2, arg3);
     }
     @JRubyMethod(name = "setpriority", required = 3, module = true, visibility = PRIVATE)
     public static IRubyObject setpriority(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return setpriority(context.getRuntime(), arg1, arg2, arg3);
     }
     public static IRubyObject setpriority(Ruby runtime, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         int which = (int)arg1.convertToInteger().getLongValue();
         int who = (int)arg2.convertToInteger().getLongValue();
         int prio = (int)arg3.convertToInteger().getLongValue();
         runtime.getPosix().errno(0);
         int result = checkErrno(runtime, runtime.getPosix().setpriority(which, who, prio));
         
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject setpgid(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return setpgid(recv.getRuntime(), arg1, arg2);
     }
     @JRubyMethod(name = "setpgid", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject setpgid(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return setpgid(context.getRuntime(), arg1, arg2);
     }
     public static IRubyObject setpgid(Ruby runtime, IRubyObject arg1, IRubyObject arg2) {
         int pid = (int)arg1.convertToInteger().getLongValue();
         int gid = (int)arg2.convertToInteger().getLongValue();
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setpgid(pid, gid)));
     }
 
     @Deprecated
     public static IRubyObject getpgid(IRubyObject recv, IRubyObject arg) {
         return getpgid(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "getpgid", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject getpgid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return getpgid(context.getRuntime(), arg);
     }
     public static IRubyObject getpgid(Ruby runtime, IRubyObject arg) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getpgid((int)arg.convertToInteger().getLongValue())));
     }
 
     @Deprecated
     public static IRubyObject getrlimit(IRubyObject recv, IRubyObject arg) {
         return getrlimit(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "getrlimit", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject getrlimit(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return getrlimit(context.getRuntime(), arg);
     }
     public static IRubyObject getrlimit(Ruby runtime, IRubyObject arg) {
         throw runtime.newNotImplementedError("Process#getrlimit not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject egid(IRubyObject recv) {
         return egid(recv.getRuntime());
     }
     @JRubyMethod(name = "egid", module = true, visibility = PRIVATE)
     public static IRubyObject egid(ThreadContext context, IRubyObject recv) {
         return egid(context.getRuntime());
     }
     public static IRubyObject egid(Ruby runtime) {
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows
             return RubyFixnum.zero(runtime);
         }
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getegid()));
     }
     
     private static int parseSignalString(Ruby runtime, String value) {
         int startIndex = 0;
         boolean negative = value.startsWith("-");
         
         if (value.startsWith("-")) startIndex++;
         String signalName = value.startsWith("SIG", startIndex)
                 ? value 
                 : "SIG" + value.substring(startIndex);
         
         try {
             int signalValue = Signal.valueOf(signalName).value();
             return negative ? -signalValue : signalValue;
 
         } catch (IllegalArgumentException ex) {
             throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
         }
         
     }
 
     @Deprecated
     public static IRubyObject kill(IRubyObject recv, IRubyObject[] args) {
         return kill(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "kill", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject kill(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return kill(context.getRuntime(), args);
     }
     public static IRubyObject kill(Ruby runtime, IRubyObject[] args) {
         if (args.length < 2) {
             throw runtime.newArgumentError("wrong number of arguments -- kill(sig, pid...)");
         }
 
         // Windows does not support these functions, so we won't even try
         // This also matches Ruby behavior for JRUBY-2353.
         if (Platform.IS_WINDOWS) {
             return runtime.getNil();
         }
         
         int signal;
         if (args[0] instanceof RubyFixnum) {
             signal = (int) ((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubySymbol) {
             signal = parseSignalString(runtime, args[0].toString());
         } else if (args[0] instanceof RubyString) {
             signal = parseSignalString(runtime, args[0].toString());
         } else {
             signal = parseSignalString(runtime, args[0].checkStringType().toString());
         }
 
         boolean processGroupKill = signal < 0;
         
         if (processGroupKill) signal = -signal;
         
         POSIX posix = runtime.getPosix();
         for (int i = 1; i < args.length; i++) {
             int pid = RubyNumeric.num2int(args[i]);
 
             // FIXME: It may be possible to killpg on systems which support it.  POSIX library
             // needs to tell whether a particular method works or not
             if (pid == 0) pid = runtime.getPosix().getpid();
             checkErrno(runtime, posix.kill(processGroupKill ? -pid : pid, signal));
         }
         
         return runtime.newFixnum(args.length - 1);
 
     }
 
     @JRubyMethod(name = "detach", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject detach(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         final int pid = (int)arg.convertToInteger().getLongValue();
         Ruby runtime = context.getRuntime();
         
         BlockCallback callback = new BlockCallback() {
             public IRubyObject call(ThreadContext context, IRubyObject[] args, Block block) {
                 int[] status = new int[1];
                 Ruby runtime = context.runtime;
                 int result = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, 0));
                 
                 return runtime.newFixnum(result);
             }
         };
         
         return RubyThread.newInstance(
                 runtime.getThread(),
                 IRubyObject.NULL_ARRAY,
                 CallBlock.newCallClosure(recv, (RubyModule)recv, Arity.NO_ARGUMENTS, callback, context));
     }
 
     @Deprecated
     public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
         return times(recv.getRuntime());
     }
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject times(ThreadContext context, IRubyObject recv, Block unusedBlock) {
         return times(context.getRuntime());
     }
     public static IRubyObject times(Ruby runtime) {
         double currentTime = System.currentTimeMillis() / 1000.0;
         double startTime = runtime.getStartTime() / 1000.0;
         RubyFloat zero = runtime.newFloat(0.0);
         return RubyStruct.newStruct(runtime.getTmsStruct(), 
                 new IRubyObject[] { runtime.newFloat(currentTime - startTime), zero, zero, zero }, 
                 Block.NULL_BLOCK);
     }
 
     @Deprecated
     public static IRubyObject pid(IRubyObject recv) {
         return pid(recv.getRuntime());
     }
     @JRubyMethod(name = "pid", module = true, visibility = PRIVATE)
     public static IRubyObject pid(ThreadContext context, IRubyObject recv) {
         return pid(context.getRuntime());
     }
     public static IRubyObject pid(Ruby runtime) {
         return runtime.newFixnum(runtime.getPosix().getpid());
     }
     
-    @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
+    @JRubyMethod(name = "fork", module = true, visibility = PRIVATE, compat = RUBY1_8)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         return RubyKernel.fork(context, recv, block);
     }
+    
+    @JRubyMethod(name = "fork", module = true, visibility = PRIVATE, compat = RUBY1_9, notImplemented = true)
+    public static IRubyObject fork19(ThreadContext context, IRubyObject recv, Block block) {
+        return RubyKernel.fork(context, recv, block);
+    }
 
     @JRubyMethod(name = "spawn", required = 1, rest = true, module = true, compat = CompatVersion.RUBY1_9)
     public static RubyFixnum spawn(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         long pid = ShellLauncher.runExternalWithoutWait(runtime, args);
         return RubyFixnum.newFixnum(runtime, pid);
     }
     
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.exit(recv, args);
     }
     
     private static final NonNativeErrno IGNORE = new NonNativeErrno() {
         public int handle(Ruby runtime, int result) {return result;}
     };
 
     private static int checkErrno(Ruby runtime, int result) {
         return checkErrno(runtime, result, IGNORE);
     }
 
     private static int checkErrno(Ruby runtime, int result, NonNativeErrno nonNative) {
         if (result == -1) {
             if (runtime.getPosix().isNative()) {
                 raiseErrnoIfSet(runtime, nonNative);
             } else {
                 nonNative.handle(runtime, result);
             }
         }
         return result;
     }
 
     private static void raiseErrnoIfSet(Ruby runtime, NonNativeErrno nonNative) {
         if (runtime.getPosix().errno() != 0) {
             throw runtime.newErrnoFromInt(runtime.getPosix().errno());
         }
     }
 }
diff --git a/src/org/jruby/anno/AnnotationBinder.java b/src/org/jruby/anno/AnnotationBinder.java
index 131ddb848f..128cd46a92 100644
--- a/src/org/jruby/anno/AnnotationBinder.java
+++ b/src/org/jruby/anno/AnnotationBinder.java
@@ -1,571 +1,573 @@
 package org.jruby.anno;
 
 import com.sun.mirror.apt.*;
 import com.sun.mirror.declaration.*;
 import com.sun.mirror.type.ReferenceType;
 import com.sun.mirror.util.*;
 
 import java.io.ByteArrayOutputStream;
 import java.io.FileOutputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Set;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 
 import org.jruby.CompatVersion;
 import org.jruby.util.CodegenUtils;
 import static java.util.Collections.*;
 import static com.sun.mirror.util.DeclarationVisitors.*;
 
 /*
  * This class is used to run an annotation processor that lists class
  * names.  The functionality of the processor is analogous to the
  * ListClass doclet in the Doclet Overview.
  */
 public class AnnotationBinder implements AnnotationProcessorFactory {
     // Process any set of annotations
     private static final Collection<String> supportedAnnotations = unmodifiableCollection(Arrays.asList("org.jruby.anno.JRubyMethod", "org.jruby.anno.JRubyClass"));    // No supported options
     private static final Collection<String> supportedOptions = emptySet();
 
     public Collection<String> supportedAnnotationTypes() {
         return supportedAnnotations;
     }
 
     public Collection<String> supportedOptions() {
         return supportedOptions;
     }
 
     public AnnotationProcessor getProcessorFor(
             Set<AnnotationTypeDeclaration> atds,
             AnnotationProcessorEnvironment env) {
         return new AnnotationBindingProcessor(env);
     }
 
     private static class AnnotationBindingProcessor implements AnnotationProcessor {
 
         private final AnnotationProcessorEnvironment env;
         private final List<String> classNames = new ArrayList<String>();
 
         AnnotationBindingProcessor(AnnotationProcessorEnvironment env) {
             this.env = env;
         }
 
         public void process() {
             for (TypeDeclaration typeDecl : env.getSpecifiedTypeDeclarations()) {
                 typeDecl.accept(getDeclarationScanner(new RubyClassVisitor(),
                         NO_OP));
             }
             try {
                 FileWriter fw = new FileWriter("src_gen/annotated_classes.txt");
                 for (String name : classNames) {
                     fw.write(name);
                     fw.write('\n');
                 }
                 fw.close();
             } catch (Exception e) {
                 throw new RuntimeException(e);
             }
         }
 
         private class RubyClassVisitor extends SimpleDeclarationVisitor {
 
             private PrintStream out;
             private static final boolean DEBUG = false;
 
             @Override
             public void visitClassDeclaration(ClassDeclaration cd) {
                 try {
                     String qualifiedName = cd.getQualifiedName().replace('.', '$');
 
                     // skip anything not related to jruby
                     if (!qualifiedName.contains("org$jruby")) {
                         return;
                     }
                     ByteArrayOutputStream bytes = new ByteArrayOutputStream(1024);
                     out = new PrintStream(bytes);
 
                     // start a new populator
                     out.println("/* THIS FILE IS GENERATED. DO NOT EDIT */");
                     out.println("package org.jruby.gen;");
 
                     out.println("import org.jruby.Ruby;");
                     out.println("import org.jruby.RubyModule;");
                     out.println("import org.jruby.RubyClass;");
                     out.println("import org.jruby.CompatVersion;");
                     out.println("import org.jruby.anno.TypePopulator;");
                     out.println("import org.jruby.internal.runtime.methods.CallConfiguration;");
                     out.println("import org.jruby.internal.runtime.methods.JavaMethod;");
                     out.println("import org.jruby.internal.runtime.methods.DynamicMethod;");
                     out.println("import org.jruby.runtime.Arity;");
                     out.println("import org.jruby.runtime.Visibility;");
                     out.println("import org.jruby.compiler.ASTInspector;");
                     out.println("import java.util.Arrays;");
                     out.println("import java.util.List;");
 
                     out.println("public class " + qualifiedName + "$Populator extends TypePopulator {");
                     out.println("    public void populate(RubyModule cls, Class clazz) {");
                     if (DEBUG) {
                         out.println("        System.out.println(\"Using pregenerated populator: \" + \"" + cd.getSimpleName() + "Populator\");");
                     }
 
                     // scan for meta, compat, etc to reduce findbugs complaints about "dead assignments"
                     boolean hasMeta = false;
                     boolean hasModule = false;
                     boolean hasCompat = false;
                     for (MethodDeclaration md : cd.getMethods()) {
                         JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                         if (anno == null) {
                             continue;
                         }
                         hasMeta |= anno.meta();
                         hasModule |= anno.module();
                         hasCompat |= anno.compat() != CompatVersion.BOTH;
                     }
 
                     out.println("        JavaMethod javaMethod;");
                     out.println("        DynamicMethod moduleMethod;");
                     if (hasMeta || hasModule) out.println("        RubyClass singletonClass = cls.getSingletonClass();");
                     if (hasCompat) out.println("        CompatVersion compatVersion = cls.getRuntime().getInstanceConfig().getCompatVersion();");
                     out.println("        Ruby runtime = cls.getRuntime();");
 
                     Map<String, List<MethodDeclaration>> annotatedMethods = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> annotatedMethods1_8 = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods1_8 = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> annotatedMethods1_9 = new HashMap<String, List<MethodDeclaration>>();
                     Map<String, List<MethodDeclaration>> staticAnnotatedMethods1_9 = new HashMap<String, List<MethodDeclaration>>();
 
                     Set<String> frameAwareMethods = new HashSet<String>();
                     Set<String> scopeAwareMethods = new HashSet<String>();
 
                     int methodCount = 0;
                     for (MethodDeclaration md : cd.getMethods()) {
                         JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                         if (anno == null) {
                             continue;
                         }
                         methodCount++;
 
                         // warn if the method raises any exceptions (JRUBY-4494)
                         if (md.getThrownTypes().size() != 0) {
                             System.err.print("Method " + cd.toString() + "." + md.toString() + " should not throw exceptions: ");
                             boolean comma = false;
                             for (ReferenceType thrownType : md.getThrownTypes()) {
                                 if (comma) System.err.print(", ");
                                 System.err.print(thrownType);
                                 comma = true;
                             }
                             System.err.print("\n");
                         }
 
                         String name = anno.name().length == 0 ? md.getSimpleName() : anno.name()[0];
 
                         List<MethodDeclaration> methodDescs;
                         Map<String, List<MethodDeclaration>> methodsHash = null;
                         if (md.getModifiers().contains(Modifier.STATIC)) {
                             if (anno.compat() == CompatVersion.RUBY1_8) {
                                 methodsHash = staticAnnotatedMethods1_8;
                             } else if (anno.compat() == CompatVersion.RUBY1_9) {
                                 methodsHash = staticAnnotatedMethods1_9;
                             } else {
                                 methodsHash = staticAnnotatedMethods;
                             }
                         } else {
                             if (anno.compat() == CompatVersion.RUBY1_8) {
                                 methodsHash = annotatedMethods1_8;
                             } else if (anno.compat() == CompatVersion.RUBY1_9) {
                                 methodsHash = annotatedMethods1_9;
                             } else {
                                 methodsHash = annotatedMethods;
                             }
                         }
 
                         methodDescs = methodsHash.get(name);
                         if (methodDescs == null) {
                             methodDescs = new ArrayList<MethodDeclaration>();
                             methodsHash.put(name, methodDescs);
                         }
 
                         methodDescs.add(md);
 
                         // check for frame field reads or writes
                         boolean frame = false;
                         boolean scope = false;
                         if (anno.frame()) {
                             if (DEBUG) System.out.println("Method has frame = true: " + methodDescs.get(0).getDeclaringType() + ": " + methodDescs);
                             frame = true;
                         }
                         if (anno.reads() != null) for (FrameField read : anno.reads()) {
                             switch (read) {
                             case BACKREF:
                             case LASTLINE:
                                 if (DEBUG) System.out.println("Method reads scope field " + read + ": " + methodDescs.get(0).getDeclaringType() + ": " + methodDescs);
                                 scope = true;
                                 break;
                             default:
                                 if (DEBUG) System.out.println("Method reads frame field " + read + ": " + methodDescs.get(0).getDeclaringType() + ": " + methodDescs);
                                 frame = true;
                             }
                         }
                         if (anno.writes() != null) for (FrameField write : anno.writes()) {
                             switch (write) {
                             case BACKREF:
                             case LASTLINE:
                                 if (DEBUG) System.out.println("Method writes scope field " + write + ": " + methodDescs.get(0).getDeclaringType() + ": " + methodDescs);
                                 scope = true;
                                 break;
                             default:
                                 if (DEBUG) System.out.println("Method writes frame field " + write + ": " + methodDescs.get(0).getDeclaringType() + ": " + methodDescs);
                                 frame = true;
                             }
                         }
                         if (frame) frameAwareMethods.addAll(Arrays.asList(anno.name()));
                         if (scope) scopeAwareMethods.addAll(Arrays.asList(anno.name()));
                     }
 
                     if (methodCount == 0) {
                         // no annotated methods found, skip
                         return;
                     }
 
                     classNames.add(getActualQualifiedName(cd));
 
                     processMethodDeclarations(staticAnnotatedMethods);
                     for (Map.Entry<String, List<MethodDeclaration>> entry : staticAnnotatedMethods.entrySet()) {
                         MethodDeclaration decl = entry.getValue().get(0);
                         if (!decl.getAnnotation(JRubyMethod.class).omit()) addCoreMethodMapping(entry.getKey(), decl, out);
                     }
 
                     if (!staticAnnotatedMethods1_8.isEmpty()) {
                         out.println("        if (compatVersion == CompatVersion.RUBY1_8 || compatVersion == CompatVersion.BOTH) {");
                         processMethodDeclarations(staticAnnotatedMethods1_8);
                         for (Map.Entry<String, List<MethodDeclaration>> entry : staticAnnotatedMethods1_8.entrySet()) {
                             MethodDeclaration decl = entry.getValue().get(0);
                             if (!decl.getAnnotation(JRubyMethod.class).omit()) addCoreMethodMapping(entry.getKey(), decl, out);
                         }
                         out.println("        }");
                     }
 
                     if (!staticAnnotatedMethods1_9.isEmpty()) {
                         out.println("        if (compatVersion == CompatVersion.RUBY1_9 || compatVersion == CompatVersion.BOTH) {");
                         processMethodDeclarations(staticAnnotatedMethods1_9);
                         for (Map.Entry<String, List<MethodDeclaration>> entry : staticAnnotatedMethods1_9.entrySet()) {
                             MethodDeclaration decl = entry.getValue().get(0);
                             if (!decl.getAnnotation(JRubyMethod.class).omit()) addCoreMethodMapping(entry.getKey(), decl, out);
                         }
                         out.println("        }");
                     }
 
                     processMethodDeclarations(annotatedMethods);
                     for (Map.Entry<String, List<MethodDeclaration>> entry : annotatedMethods.entrySet()) {
                         MethodDeclaration decl = entry.getValue().get(0);
                         if (!decl.getAnnotation(JRubyMethod.class).omit()) addCoreMethodMapping(entry.getKey(), decl, out);
                     }
 
                     if (!annotatedMethods1_8.isEmpty()) {
                         out.println("        if (compatVersion == CompatVersion.RUBY1_8 || compatVersion == CompatVersion.BOTH) {");
                         processMethodDeclarations(annotatedMethods1_8);
                         for (Map.Entry<String, List<MethodDeclaration>> entry : annotatedMethods1_8.entrySet()) {
                             MethodDeclaration decl = entry.getValue().get(0);
                             if (!decl.getAnnotation(JRubyMethod.class).omit()) addCoreMethodMapping(entry.getKey(), decl, out);
                         }
                         out.println("        }");
                     }
 
                     if (!annotatedMethods1_9.isEmpty()) {
                         out.println("        if (compatVersion == CompatVersion.RUBY1_9 || compatVersion == CompatVersion.BOTH) {");
                         processMethodDeclarations(annotatedMethods1_9);
                         for (Map.Entry<String, List<MethodDeclaration>> entry : annotatedMethods1_9.entrySet()) {
                             MethodDeclaration decl = entry.getValue().get(0);
                             if (!decl.getAnnotation(JRubyMethod.class).omit()) addCoreMethodMapping(entry.getKey(), decl, out);
                         }
                         out.println("        }");
                     }
 
                     out.println("    }");
 
                     // write out a static initializer for frame names, so it only fires once
                     out.println("    static {");
                     if (!frameAwareMethods.isEmpty()) {
                         StringBuffer frameMethodsString = new StringBuffer();
                         boolean first = true;
                         for (String name : frameAwareMethods) {
                             if (!first) frameMethodsString.append(',');
                             first = false;
                             frameMethodsString.append('"').append(name).append('"');
                         }
                         out.println("        ASTInspector.addFrameAwareMethods(" + frameMethodsString + ");");
                     }
                     if (!scopeAwareMethods.isEmpty()) {
                         StringBuffer scopeMethodsString = new StringBuffer();
                         boolean first = true;
                         for (String name : scopeAwareMethods) {
                             if (!first) scopeMethodsString.append(',');
                             first = false;
                             scopeMethodsString.append('"').append(name).append('"');
                         }
                         out.println("        ASTInspector.addScopeAwareMethods(" + scopeMethodsString + ");");
                     }
                     out.println("     }");
 
                     out.println("}");
                     out.close();
                     out = null;
 
                     FileOutputStream fos = new FileOutputStream("src_gen/" + qualifiedName + "$Populator.java");
                     fos.write(bytes.toByteArray());
                     fos.close();
                 } catch (IOException ioe) {
                     System.err.println("FAILED TO GENERATE:");
                     ioe.printStackTrace();
                     System.exit(1);
                 }
             }
 
             public void processMethodDeclarations(Map<String, List<MethodDeclaration>> declarations) {
                 for (Map.Entry<String, List<MethodDeclaration>> entry : declarations.entrySet()) {
                     List<MethodDeclaration> list = entry.getValue();
 
                     if (list.size() == 1) {
                         // single method, use normal logic
                         processMethodDeclaration(list.get(0));
                     } else {
                         // multimethod, new logic
                         processMethodDeclarationMulti(list.get(0));
                     }
                 }
             }
 
             public void processMethodDeclaration(MethodDeclaration md) {
                 JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                 if (anno != null && out != null) {
                     boolean isStatic = md.getModifiers().contains(Modifier.STATIC);
                     String qualifiedName = getActualQualifiedName(md.getDeclaringType());
 
                     boolean hasContext = false;
                     boolean hasBlock = false;
 
                     StringBuffer buffer = new StringBuffer();
                     boolean first = true;
                     for (ParameterDeclaration pd : md.getParameters()) {
                         if (!first) buffer.append(", ");
                         first = false;
                         buffer.append(pd.getType().toString());
                         buffer.append(".class");
                         hasContext |= pd.getType().toString().equals("org.jruby.runtime.ThreadContext");
                         hasBlock |= pd.getType().toString().equals("org.jruby.runtime.Block");
                     }
 
                     int actualRequired = calculateActualRequired(md, md.getParameters().size(), anno.optional(), anno.rest(), isStatic, hasContext, hasBlock);
 
                     String annotatedBindingName = CodegenUtils.getAnnotatedBindingClassName(
                             md.getSimpleName(),
                             qualifiedName,
                             isStatic,
                             actualRequired,
                             anno.optional(),
                             false,
                             anno.frame());
                     String implClass = anno.meta() ? "singletonClass" : "cls";
 
                     out.println("        javaMethod = new " + annotatedBindingName + "(" + implClass + ", Visibility." + anno.visibility() + ");");
                     out.println("        populateMethod(javaMethod, " +
                             getArityValue(anno, actualRequired) + ", \"" +
                             md.getSimpleName() + "\", " +
                             isStatic + ", " +
-                            "CallConfiguration." + getCallConfigNameByAnno(anno) + ");");
+                            "CallConfiguration." + getCallConfigNameByAnno(anno) + ", " +
+                            anno.notImplemented() + ");");
                     out.println("        javaMethod.setNativeCall("
                             + md.getDeclaringType().getQualifiedName() + ".class, "
                             + "\"" + md.getSimpleName() + "\", "
                             + md.getReturnType().toString() + ".class, "
                             + "new Class[] {" + buffer.toString() + "}, "
                             + md.getModifiers().contains(Modifier.STATIC) + ");");
                     generateMethodAddCalls(md, anno);
                 }
             }
 
             public void processMethodDeclarationMulti(MethodDeclaration md) {
                 JRubyMethod anno = md.getAnnotation(JRubyMethod.class);
                 if (anno != null && out != null) {
                     boolean isStatic = md.getModifiers().contains(Modifier.STATIC);
                     String qualifiedName = getActualQualifiedName(md.getDeclaringType());
 
                     boolean hasContext = false;
                     boolean hasBlock = false;
 
                     for (ParameterDeclaration pd : md.getParameters()) {
                         hasContext |= pd.getType().toString().equals("org.jruby.runtime.ThreadContext");
                         hasBlock |= pd.getType().toString().equals("org.jruby.runtime.Block");
                     }
 
                     int actualRequired = calculateActualRequired(md, md.getParameters().size(), anno.optional(), anno.rest(), isStatic, hasContext, hasBlock);
 
                     String annotatedBindingName = CodegenUtils.getAnnotatedBindingClassName(
                             md.getSimpleName(),
                             qualifiedName,
                             isStatic,
                             actualRequired,
                             anno.optional(),
                             true,
                             anno.frame());
                     String implClass = anno.meta() ? "singletonClass" : "cls";
 
                     out.println("        javaMethod = new " + annotatedBindingName + "(" + implClass + ", Visibility." + anno.visibility() + ");");
                     out.println("        populateMethod(javaMethod, " +
                             "-1, \"" +
                             md.getSimpleName() + "\", " +
                             isStatic + ", " +
-                            "CallConfiguration." + getCallConfigNameByAnno(anno) + ");");
+                            "CallConfiguration." + getCallConfigNameByAnno(anno) + ", " +
+                            anno.notImplemented() + ");");
                     generateMethodAddCalls(md, anno);
                 }
             }
 
             private void addCoreMethodMapping(String rubyName, MethodDeclaration decl, PrintStream out) {
                 out.println(
                         "        runtime.addBoundMethod(\""
                         + decl.getDeclaringType().getQualifiedName()
                         + "."
                         + decl.getSimpleName()
                         + "\", \""
                         + rubyName
                         + "\");");
             }
 
             private String getActualQualifiedName(TypeDeclaration td) {
                 // declared type returns the qualified name without $ for inner classes!!!
                 String qualifiedName;
                 if (td.getDeclaringType() != null) {
                     // inner class, use $ to delimit
                     if (td.getDeclaringType().getDeclaringType() != null) {
                         qualifiedName = td.getDeclaringType().getDeclaringType().getQualifiedName() + "$" + td.getDeclaringType().getSimpleName() + "$" + td.getSimpleName();
                     } else {
                         qualifiedName = td.getDeclaringType().getQualifiedName() + "$" + td.getSimpleName();
                     }
                 } else {
                     qualifiedName = td.getQualifiedName();
                 }
 
                 return qualifiedName;
             }
 
             private int calculateActualRequired(MethodDeclaration md, int paramsLength, int optional, boolean rest, boolean isStatic, boolean hasContext, boolean hasBlock) {
                 int actualRequired;
                 if (optional == 0 && !rest) {
                     int args = paramsLength;
                     if (args == 0) {
                         actualRequired = 0;
                     } else {
                         if (isStatic) {
                             args--;
                         }
                         if (hasContext) {
                             args--;
                         }
                         if (hasBlock) {
                             args--;                        // TODO: confirm expected args are IRubyObject (or similar)
                         }
                         actualRequired = args;
                     }
                 } else {
                     // optional args, so we have IRubyObject[]
                     // TODO: confirm
                     int args = paramsLength;
                     if (args == 0) {
                         actualRequired = 0;
                     } else {
                         if (isStatic) {
                             args--;
                         }
                         if (hasContext) {
                             args--;
                         }
                         if (hasBlock) {
                             args--;                        // minus one more for IRubyObject[]
                         }
                         args--;
 
                         // TODO: confirm expected args are IRubyObject (or similar)
                         actualRequired = args;
                     }
 
                     if (actualRequired != 0) {
                         throw new RuntimeException("Combining specific args with IRubyObject[] is not yet supported: "
                                 + md.getDeclaringType().getQualifiedName() + "." + md.toString());
                     }
                 }
 
                 return actualRequired;
             }
 
             public void generateMethodAddCalls(MethodDeclaration md, JRubyMethod jrubyMethod) {
                 if (jrubyMethod.meta()) {
                     defineMethodOnClass("javaMethod", "singletonClass", jrubyMethod, md);
                 } else {
                     defineMethodOnClass("javaMethod", "cls", jrubyMethod, md);
                     if (jrubyMethod.module()) {
                         out.println("        moduleMethod = populateModuleMethod(cls, javaMethod);");
                         defineMethodOnClass("moduleMethod", "singletonClass", jrubyMethod, md);
                     }
                 }
             //                }
             }
 
             private void defineMethodOnClass(String methodVar, String classVar, JRubyMethod jrubyMethod, MethodDeclaration md) {
                 String baseName;
                 if (jrubyMethod.name().length == 0) {
                     baseName = md.getSimpleName();
                     out.println("        " + classVar + ".addMethodAtBootTimeOnly(\"" + baseName + "\", " + methodVar + ");");
                 } else {
                     baseName = jrubyMethod.name()[0];
                     for (String name : jrubyMethod.name()) {
                         out.println("        " + classVar + ".addMethodAtBootTimeOnly(\"" + name + "\", " + methodVar + ");");
                     }
                 }
 
                 if (jrubyMethod.alias().length > 0) {
                     for (String alias : jrubyMethod.alias()) {
                         out.println("        " + classVar + ".defineAlias(\"" + alias + "\", \"" + baseName + "\");");
                     }
                 }
             }
         }
 
         public static int getArityValue(JRubyMethod anno, int actualRequired) {
             if (anno.optional() > 0 || anno.rest()) {
                 return -(actualRequired + 1);
             }
             return actualRequired;
         }
     
         public static String getCallConfigNameByAnno(JRubyMethod anno) {
             return getCallConfigName(anno.frame(), anno.scope(), anno.backtrace());
         }
 
         public static String getCallConfigName(boolean frame, boolean scope, boolean backtrace) {
             if (frame) {
                 if (scope) {
                     return "FrameFullScopeFull";
                 } else {
                     return "FrameFullScopeNone";
                 }
             } else if (scope) {
                 if (backtrace) {
                     return "FrameBacktraceScopeFull";
                 } else {
                     return "FrameNoneScopeFull";
                 }
             } else if (backtrace) {
                 return "FrameBacktraceScopeNone";
             } else {
                 return "FrameNoneScopeNone";
             }
         }
     }
 }
diff --git a/src/org/jruby/anno/JRubyMethod.java b/src/org/jruby/anno/JRubyMethod.java
index 635e18abb8..19bee72f8c 100644
--- a/src/org/jruby/anno/JRubyMethod.java
+++ b/src/org/jruby/anno/JRubyMethod.java
@@ -1,90 +1,94 @@
 /*
  * JRubyMethod.java
  * 
  * Created on Aug 4, 2007, 3:07:36 PM
  * 
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.anno;
 
 import java.lang.annotation.ElementType;
 import java.lang.annotation.Retention;
 import java.lang.annotation.RetentionPolicy;
 import java.lang.annotation.Target;
 import org.jruby.CompatVersion;
 import org.jruby.runtime.Visibility;
 
 /**
  *
  * @author headius
  */
 @Retention(RetentionPolicy.RUNTIME)
 @Target(ElementType.METHOD)
 public @interface JRubyMethod {
     /**
      * The name or names of this method in Ruby-land.
      */
     String[] name() default {};
     /**
      * The number of required arguments.
      */
     int required() default 0;
     /**
      * The number of optional arguments.
      */
     int optional() default 0;
     /**
      * Whether this method has a "rest" argument.
      */
     boolean rest() default false;
     /**
      * Any alias or aliases for this method.
      */
     String[] alias() default {};
     /**
      * Whether this method should be defined on the metaclass.
      */
     boolean meta() default false;
     /**
      * Whether this method should be a module function, defined on metaclass and private on class.
      */
     boolean module() default false;
     /**
      * Whether this method expects to have a call frame allocated for it.
      */
     boolean frame() default false;
     /**
      * Whether this method expects to have a heap-based variable scope allocated for it.
      */
     boolean scope() default false;
     /**
      * Whether this method is specific to Ruby 1.9
      */
     CompatVersion compat() default CompatVersion.BOTH;
     /**
      * The visibility of this method.
      */
     Visibility visibility() default Visibility.PUBLIC;
     /**
      * Whether to use a frame slot for backtrace information
      */
     boolean backtrace() default false;
     /**
      * What, if anything, method reads from caller's frame
      */
     FrameField[] reads() default {};
     /**
      * What, if anything, method writes to caller's frame
      */
     FrameField[] writes() default {};
     /**
      * Argument types to coerce to before calling
      */
     Class[] argTypes() default {};
     /**
      * Whether to use a frame slot for backtrace information
      */
     boolean omit() default false;
+    /**
+     * Whether this method should show up as defined in response to respond_to? calls
+     */
+    boolean notImplemented() default false;
 }
diff --git a/src/org/jruby/anno/TypePopulator.java b/src/org/jruby/anno/TypePopulator.java
index fdd40a59d5..dd41a7c8fd 100644
--- a/src/org/jruby/anno/TypePopulator.java
+++ b/src/org/jruby/anno/TypePopulator.java
@@ -1,109 +1,110 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.anno;
 
 import java.util.List;
 import java.util.Map;
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.Visibility;
 
 /**
  *
  * @author headius
  */
 public abstract class TypePopulator {
-    public void populateMethod(JavaMethod javaMethod, int arity, String simpleName, boolean isStatic, CallConfiguration callConfig) {
+    public static void populateMethod(JavaMethod javaMethod, int arity, String simpleName, boolean isStatic, CallConfiguration callConfig, boolean notImplemented) {
         javaMethod.setIsBuiltin(true);
         javaMethod.setArity(Arity.createArity(arity));
         javaMethod.setJavaName(simpleName);
         javaMethod.setSingleton(isStatic);
         javaMethod.setCallConfig(callConfig);
+        javaMethod.setNotImplemented(notImplemented);
     }
     
-    public DynamicMethod populateModuleMethod(RubyModule cls, JavaMethod javaMethod) {
+    public static DynamicMethod populateModuleMethod(RubyModule cls, JavaMethod javaMethod) {
         DynamicMethod moduleMethod = javaMethod.dup();
         moduleMethod.setImplementationClass(cls.getSingletonClass());
         moduleMethod.setVisibility(Visibility.PUBLIC);
         return moduleMethod;
     }
     
     public abstract void populate(RubyModule clsmod, Class clazz);
     
     public static final TypePopulator DEFAULT = new DefaultTypePopulator();
     public static class DefaultTypePopulator extends TypePopulator {
         public void populate(RubyModule clsmod, Class clazz) {
             // fallback on non-pregenerated logic
             MethodFactory methodFactory = MethodFactory.createFactory(clsmod.getRuntime().getJRubyClassLoader());
             Ruby runtime = clsmod.getRuntime();
             
             RubyModule.MethodClumper clumper = new RubyModule.MethodClumper();
             clumper.clump(clazz);
 
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAllAnnotatedMethods().entrySet()) {
                 for (JavaMethodDescriptor desc : entry.getValue()) {
                     JRubyMethod anno = desc.anno;
                     
                     // check for frame field reads or writes
                     if (anno.frame() || (anno.reads() != null && anno.reads().length >= 1) || (anno.writes() != null && anno.writes().length >= 1)) {
                         // add all names for this annotation
                         ASTInspector.addFrameAwareMethods(anno.name());
                         // TODO: separate scope-aware and frame-aware
                         ASTInspector.addScopeAwareMethods(anno.name());
                     }
                 }
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods().entrySet()) {
                 clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
                 for (JavaMethodDescriptor desc : entry.getValue()) {
                     if (!desc.anno.omit()) runtime.addBoundMethod(desc.declaringClassName + "." + desc.name, entry.getKey());
                 }
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods().entrySet()) {
                 clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
                 for (JavaMethodDescriptor desc : entry.getValue()) {
                     if (!desc.anno.omit()) runtime.addBoundMethod(desc.declaringClassName + "." + desc.name, entry.getKey());
                 }
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_8().entrySet()) {
                 clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
                 for (JavaMethodDescriptor desc : entry.getValue()) {
                     if (!desc.anno.omit()) runtime.addBoundMethod(desc.declaringClassName + "." + desc.name, entry.getKey());
                 }
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_8().entrySet()) {
                 clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
                 for (JavaMethodDescriptor desc : entry.getValue()) {
                     if (!desc.anno.omit()) runtime.addBoundMethod(desc.declaringClassName + "." + desc.name, entry.getKey());
                 }
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getStaticAnnotatedMethods1_9().entrySet()) {
                 clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
                 for (JavaMethodDescriptor desc : entry.getValue()) {
                     if (!desc.anno.omit()) runtime.addBoundMethod(desc.declaringClassName + "." + desc.name, entry.getKey());
                 }
             }
             
             for (Map.Entry<String, List<JavaMethodDescriptor>> entry : clumper.getAnnotatedMethods1_9().entrySet()) {
                 clsmod.defineAnnotatedMethod(entry.getKey(), entry.getValue(), methodFactory);
                 for (JavaMethodDescriptor desc : entry.getValue()) {
                     if (!desc.anno.omit()) runtime.addBoundMethod(desc.declaringClassName + "." + desc.name, entry.getKey());
                 }
             }
         }
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/DynamicMethod.java b/src/org/jruby/internal/runtime/methods/DynamicMethod.java
index e28c9cb5e7..1a891661bc 100644
--- a/src/org/jruby/internal/runtime/methods/DynamicMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DynamicMethod.java
@@ -1,541 +1,559 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
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
 
 
 ////////////////////////////////////////////////////////////////////////////////
 // NOTE: THIS FILE IS GENERATED! DO NOT EDIT THIS FILE!
 // generated from: src/org/jruby/internal/runtime/methods/DynamicMethod.erb
 // using arities: src/org/jruby/internal/runtime/methods/DynamicMethod.arities.erb
 ////////////////////////////////////////////////////////////////////////////////
 
 
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 
 /**
  * DynamicMethod represents a method handle in JRuby, to provide both entry
  * points into AST and bytecode interpreters, but also to provide handles to
  * JIT-compiled and hand-implemented Java methods. All methods invokable from
  * Ruby code are referenced by method handles, either directly or through
  * delegation or callback mechanisms.
  */
 public abstract class DynamicMethod {
     /** The Ruby module or class in which this method is immediately defined. */
     protected RubyModule implementationClass;
     /** The "protected class" used for calculating protected access. */
     protected RubyModule protectedClass;
     /** The visibility of this method. */
     protected Visibility visibility;
     /** The "call configuration" to use for pre/post call logic. */
     protected CallConfiguration callConfig;
     /** The serial number for this method object, to globally identify it */
     protected long serialNumber;
     /** Is this a builtin core method or not */
     protected boolean builtin = false;
     /** Data on what native call will eventually be made */
     protected NativeCall nativeCall;
     /** The simple, base name this method was defined under. May be null.*/
     protected String name;
+    /** Whether this method is "not implemented". */
+    protected boolean notImplemented = false;
 
     /**
      * Base constructor for dynamic method handles.
      *
      * @param implementationClass The class to which this method will be
      * immediately bound
      * @param visibility The visibility assigned to this method
      * @param callConfig The CallConfiguration to use for this method's
      * pre/post invocation logic.
      */
     protected DynamicMethod(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
         assert implementationClass != null;
         init(implementationClass, visibility, callConfig);
     }
 
     /**
      * Base constructor for dynamic method handles with names.
      *
      * @param implementationClass The class to which this method will be
      * immediately bound
      * @param visibility The visibility assigned to this method
      * @param callConfig The CallConfiguration to use for this method's
      * pre/post invocation logic.
      */
     protected DynamicMethod(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig, String name) {
         this(implementationClass, visibility, callConfig);
         this.name = name;
     }
 
     /**
      * A no-arg constructor used only by the UndefinedMethod subclass and
      * CompiledMethod handles. instanceof assertions make sure this is so.
      */
     protected DynamicMethod() {
 //        assert (this instanceof UndefinedMethod ||
 //                this instanceof CompiledMethod ||
 //                this instanceof );
     }
 
     protected void init(RubyModule implementationClass, Visibility visibility, CallConfiguration callConfig) {
         this.visibility = visibility;
         this.implementationClass = implementationClass;
         // TODO: Determine whether we should perhaps store non-singleton class
         // in the implementationClass
         this.protectedClass = calculateProtectedClass(implementationClass);
         this.callConfig = callConfig;
         this.serialNumber = implementationClass.getRuntime().getNextDynamicMethodSerial();
     }
 
     /**
      * Get the global serial number for this method object
      *
      * @return This method object's serial number
      */
     public long getSerialNumber() {
         return serialNumber;
     }
 
     public boolean isBuiltin() {
         return builtin;
     }
 
     public void setIsBuiltin(boolean isBuiltin) {
         this.builtin = isBuiltin;
     }
 
     /**
      * The minimum 'call' method required for a dynamic method handle.
      * Subclasses must impleemnt this method, but may implement the other
      * signatures to provide faster, non-boxing call paths. Typically
      * subclasses will implement this method to check variable arity calls,
      * then performing a specific-arity invocation to the appropriate method
      * or performing variable-arity logic in-line.
      *
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param args The argument list to this invocation
      * @param block The block passed to this invocation
      * @return The result of the call
      */
     public abstract IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz,
             String name, IRubyObject[] args, Block block);
 
     /**
      * A default implementation of n-arity, non-block 'call' method,
      * which simply calls the n-arity, block-receiving version with
      * the arg list and Block.NULL_BLOCK.
      *
      * @param context The thread context for the currently executing thread
      * @param self The 'self' or 'receiver' object to use for this call
      * @param klazz The Ruby class against which this method is binding
      * @param name The incoming name used to invoke this method
      * @param arg1 The first argument to this invocation
      * @param arg2 The second argument to this invocation
      * @return The result of the call
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz,
             String name, IRubyObject[] args) {
         return call(context, self, clazz, name, args, Block.NULL_BLOCK);
     }
 
     ////////////////////////////////////////////////////////////////////////////
     // Now we provide default impls of a number of signatures. For each arity,
     // we first generate a non-block version of the method, which just adds
     // NULL_BLOCK and re-calls, allowing e.g. compiled code, which always can
     // potentially take a block, to only generate the block-receiving signature
     // and still avoid arg boxing.
     //
     // We then provide default implementations of each block-accepting method
     // that in turn call the IRubyObject[]+Block version of call. This then
     // finally falls back on the minimum implementation requirement for
     // dynamic method handles.
     ////////////////////////////////////////////////////////////////////////////
 
     /** Arity 0, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name) {
         return call(context, self, klazz, name, Block.NULL_BLOCK);
     }
     /** Arity 0, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, Block block) {
         return call(context, self, klazz, name, IRubyObject.NULL_ARRAY, block);
     }
     /** Arity 1, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0) {
         return call(context, self, klazz, name, arg0, Block.NULL_BLOCK);
     }
     /** Arity 1, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0}, block);
     }
     /** Arity 2, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1) {
         return call(context, self, klazz, name, arg0, arg1, Block.NULL_BLOCK);
     }
     /** Arity 2, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1}, block);
     }
     /** Arity 3, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         return call(context, self, klazz, name, arg0, arg1, arg2, Block.NULL_BLOCK);
     }
     /** Arity 3, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1, arg2}, block);
     }
     /** Arity 4, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return call(context, self, klazz, name, arg0, arg1, arg2, arg3, Block.NULL_BLOCK);
     }
     /** Arity 4, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1, arg2, arg3}, block);
     }
     /** Arity 5, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4) {
         return call(context, self, klazz, name, arg0, arg1, arg2, arg3, arg4, Block.NULL_BLOCK);
     }
     /** Arity 5, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4}, block);
     }
     /** Arity 6, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5) {
         return call(context, self, klazz, name, arg0, arg1, arg2, arg3, arg4, arg5, Block.NULL_BLOCK);
     }
     /** Arity 6, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5}, block);
     }
     /** Arity 7, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6) {
         return call(context, self, klazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, Block.NULL_BLOCK);
     }
     /** Arity 7, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5, arg6}, block);
     }
     /** Arity 8, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7) {
         return call(context, self, klazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, Block.NULL_BLOCK);
     }
     /** Arity 8, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7}, block);
     }
     /** Arity 9, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8) {
         return call(context, self, klazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, Block.NULL_BLOCK);
     }
     /** Arity 9, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8}, block);
     }
     /** Arity 10, no block */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8, IRubyObject arg9) {
         return call(context, self, klazz, name, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, Block.NULL_BLOCK);
     }
     /** Arity 10, with block; calls through IRubyObject[] path */
     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule klazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject arg4, IRubyObject arg5, IRubyObject arg6, IRubyObject arg7, IRubyObject arg8, IRubyObject arg9, Block block) {
         return call(context, self, klazz, name, new IRubyObject[] {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9}, block);
     }
 
 
 
     /**
      * Duplicate this method, returning DynamicMethod referencing the same code
      * and with the same attributes.
      *
      * It is not required that this method produce a new object if the
      * semantics of the DynamicMethod subtype do not require such.
      *
      * @return An identical DynamicMethod object to the target.
      */
     public abstract DynamicMethod dup();
 
     /**
      * Determine whether this method is callable from the given object using
      * the given call type.
      *
      * @param caller The calling object
      * @param callType The type of call
      * @return true if the call would not violate visibility; false otherwise
      */
     public boolean isCallableFrom(IRubyObject caller, CallType callType) {
         switch (visibility) {
         case PUBLIC:
             return true;
         case PRIVATE:
             return callType != CallType.NORMAL;
         case PROTECTED:
             return protectedAccessOk(caller);
         }
 
         return true;
     }
 
     /**
      * Determine whether the given object can safely invoke protected methods on
      * the class this method is bound to.
      *
      * @param caller The calling object
      * @return true if the calling object can call protected methods; false
      * otherwise
      */
     private boolean protectedAccessOk(IRubyObject caller) {
         return getProtectedClass().isInstance(caller);
     }
 
     /**
      * Calculate, based on given RubyModule, which class in its hierarchy
      * should be used to determine protected access.
      *
      * @param cls The class from which to calculate
      * @return The class to be used for protected access checking.
      */
     protected static RubyModule calculateProtectedClass(RubyModule cls) {
         // singleton classes don't get their own visibility domain
         if (cls.isSingleton()) cls = cls.getSuperClass();
 
         while (cls.isIncluded()) cls = cls.getMetaClass();
 
         // For visibility we need real meta class and not anonymous one from class << self
         if (cls instanceof MetaClass) cls = ((MetaClass) cls).getRealClass();
 
         return cls;
     }
 
     /**
      * Retrieve the pre-calculated "protected class" used for access checks.
      *
      * @return The "protected class" for access checks.
      */
     protected RubyModule getProtectedClass() {
         return protectedClass;
     }
 
     /**
      * Retrieve the class or module on which this method is implemented, used
      * for 'super' logic among others.
      *
      * @return The class on which this method is implemented
      */
     public RubyModule getImplementationClass() {
         return implementationClass;
     }
 
     /**
      * Set the class on which this method is implemented, used for 'super'
      * logic, among others.
      *
      * @param implClass The class on which this method is implemented
      */
     public void setImplementationClass(RubyModule implClass) {
         implementationClass = implClass;
         protectedClass = calculateProtectedClass(implClass);
     }
 
     /**
      * Get the visibility of this method.
      *
      * @return The visibility of this method
      */
     public Visibility getVisibility() {
         return visibility;
     }
 
     /**
      * Set the visibility of this method.
      *
      * @param visibility The visibility of this method
      */
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
 
     /**
      * Whether this method is the "undefined" method, used to represent a
      * missing or undef'ed method. Only returns true for UndefinedMethod
      * instances, of which there should be only one (a singleton).
      *
      * @return true if this method is the undefined method; false otherwise
      */
     public final boolean isUndefined() {
         return this == UndefinedMethod.INSTANCE;
     }
 
     /**
      * Retrieve the arity of this method, used for reporting arity to Ruby
      * code. This arity may or may not reflect the actual specific or variable
      * arities of the referenced method.
      *
      * @return The arity of the method, as reported to Ruby consumers.
      */
     public Arity getArity() {
         return Arity.optional();
     }
 
     /**
      * Get the "real" method contained within this method. This simply returns
      * self except in cases where a method is wrapped to give it a new
      * name or new implementation class (AliasMethod, WrapperMethod, ...).
      *
      * @return The "real" method associated with this one
      */
     public DynamicMethod getRealMethod() {
         return this;
     }
 
     /**
      * Get the CallConfiguration used for pre/post logic for this method handle.
      *
      * @return The CallConfiguration for this method handle
      */
     public CallConfiguration getCallConfig() {
         return callConfig;
     }
 
     /**
      * Set the CallConfiguration used for pre/post logic for this method handle.
      *
      * @param callConfig The CallConfiguration for this method handle
      */
     public void setCallConfig(CallConfiguration callConfig) {
         this.callConfig = callConfig;
     }
     
     public static class NativeCall {
         private final Class nativeTarget;
         private final String nativeName;
         private final Class nativeReturn;
         private final Class[] nativeSignature;
         private final boolean statik;
 
         public NativeCall(Class nativeTarget, String nativeName, Class nativeReturn, Class[] nativeSignature, boolean statik) {
             this.nativeTarget = nativeTarget;
             this.nativeName = nativeName;
             this.nativeReturn = nativeReturn;
             this.nativeSignature = nativeSignature;
             this.statik = statik;
         }
 
         public Class getNativeTarget() {
             return nativeTarget;
         }
 
         public String getNativeName() {
             return nativeName;
         }
 
         public Class getNativeReturn() {
             return nativeReturn;
         }
 
         public Class[] getNativeSignature() {
             return nativeSignature;
         }
 
         public boolean isStatic() {
             return statik;
         }
 
         @Override
         public String toString() {
             return "" + (statik?"static ":"") + nativeReturn.getName() + " " + nativeTarget.getName() + "." + nativeName + CodegenUtils.prettyParams(nativeSignature);
         }
     }
 
     public void setNativeCall(Class nativeTarget, String nativeName, Class nativeReturn, Class[] nativeSignature, boolean statik) {
         this.nativeCall = new NativeCall(nativeTarget, nativeName, nativeReturn, nativeSignature, statik);
     }
 
     public NativeCall getNativeCall() {
         return nativeCall;
     }
 
     /**
      * Returns true if this method is backed by native (i.e. Java) code.
      *
      * @return true If backed by Java code or JVM bytecode; false otherwise
      */
     public boolean isNative() {
         return false;
     }
 
     /**
      * Get the base name this method was defined as.
      *
      * @return the base name for the method
      */
     public String getName() {
         return name;
     }
 
     /**
      * Set the base name for this method.
      *
      * @param name the name to set
      */
     public void setName(String name) {
         this.name = name;
     }
+    
+    /**
+     * Whether this method is "not implemented". This is
+     * primarily to support Ruby 1.9's behavior of respond_to? yielding false if
+     * the feature in question is unsupported (but still having the method defined).
+     */
+    public boolean isNotImplemented() {
+        return notImplemented;
+    }
+    
+    /**
+     * Set whether this method is "not implemented".
+     */
+    public void setNotImplemented(boolean setNotImplemented) {
+        this.notImplemented = setNotImplemented;
+    }
 
     protected IRubyObject handleRedo(Ruby runtime) throws RaiseException {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, runtime.getNil(), "unexpected redo");
     }
 
     protected IRubyObject handleReturn(ThreadContext context, JumpException.ReturnJump rj, int callNumber) {
         if (rj.getTarget() == callNumber) {
             return (IRubyObject) rj.getValue();
         }
         throw rj;
     }
 
     protected IRubyObject handleBreak(ThreadContext context, Ruby runtime, JumpException.BreakJump bj, int callNumber) {
         if (bj.getTarget() == callNumber) {
             throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, runtime.getNil(), "unexpected break");
         }
         throw bj;
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java b/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
index 9e81448ce6..9caa7e839e 100644
--- a/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
+++ b/src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
@@ -1,1557 +1,1564 @@
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
  * Copyright (C) 2006 The JRuby Community <www.jruby.org>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 package org.jruby.internal.runtime.methods;
 
 import java.io.PrintWriter;
 import java.lang.reflect.Modifier;
 import java.util.Arrays;
 import java.util.List;
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyKernel;
 import org.jruby.parser.StaticScope;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Opcodes;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JavaMethodDescriptor;
+import org.jruby.anno.TypePopulator;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockCallback19;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import static org.jruby.util.CodegenUtils.*;
 import static java.lang.System.*;
 import org.jruby.util.JRubyClassLoader;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.util.CheckClassAdapter;
 
 /**
  * In order to avoid the overhead with reflection-based method handles, this
  * MethodFactory uses ASM to generate tiny invoker classes. This allows for
  * better performance and more specialization per-handle than can be supported
  * via reflection. It also allows optimizing away many conditionals that can
  * be determined once ahead of time.
  * 
  * When running in secured environments, this factory may not function. When
  * this can be detected, MethodFactory will fall back on the reflection-based
  * factory instead.
  * 
  * @see org.jruby.internal.runtime.methods.MethodFactory
  */
 public class InvocationMethodFactory extends MethodFactory implements Opcodes {
     private static final boolean DEBUG = false;
     
     /** The pathname of the super class for compiled Ruby method handles. */ 
     private final static String COMPILED_SUPER_CLASS = p(CompiledMethod.class);
     
     /** The outward call signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class));
     
     /** The outward call signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ZERO_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ZERO = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ONE_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_ONE = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_TWO_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_TWO = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_THREE_BLOCK = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, IRubyObject.class, Block.class));
     
     /** The outward arity-zero call-with-block signature for compiled Ruby method handles. */
     private final static String COMPILED_CALL_SIG_THREE = sig(IRubyObject.class,
             params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject.class, IRubyObject.class, IRubyObject.class));
 
     private final static String BLOCK_CALL_SIG = sig(RubyKernel.IRUBY_OBJECT, params(
             ThreadContext.class, RubyKernel.IRUBY_OBJECT, IRubyObject.class, Block.class));
     private final static String BLOCK_CALL_SIG19 = sig(RubyKernel.IRUBY_OBJECT, params(
             ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class));
     
     /** The super constructor signature for Java-based method handles. */
     private final static String JAVA_SUPER_SIG = sig(Void.TYPE, params(RubyModule.class, Visibility.class));
     
     /** The lvar index of "this" */
     public static final int THIS_INDEX = 0;
     
     /** The lvar index of the passed-in ThreadContext */
     public static final int THREADCONTEXT_INDEX = 1;
     
     /** The lvar index of the method-receiving object */
     public static final int RECEIVER_INDEX = 2;
     
     /** The lvar index of the RubyClass being invoked against */
     public static final int CLASS_INDEX = 3;
     
     /** The lvar index method name being invoked */
     public static final int NAME_INDEX = 4;
     
     /** The lvar index of the method args on the call */
     public static final int ARGS_INDEX = 5;
     
     /** The lvar index of the passed-in Block on the call */
     public static final int BLOCK_INDEX = 6;
 
     /** The classloader to use for code loading */
     protected final JRubyClassLoader classLoader;
     
     /**
      * Whether this factory has seen undefined methods already. This is used to
      * detect likely method handle collisions when we expect to create a new
      * handle for each call.
      */
     private boolean seenUndefinedClasses = false;
 
     /**
      * Whether we've informed the user that we've seen undefined methods; this
      * is to avoid a flood of repetitive information.
      */
     private boolean haveWarnedUser = false;
     
     /**
      * Construct a new InvocationMethodFactory using the specified classloader
      * to load code. If the target classloader is not an instance of
      * JRubyClassLoader, it will be wrapped with one.
      * 
      * @param classLoader The classloader to use, or to wrap if it is not a
      * JRubyClassLoader instance.
      */
     public InvocationMethodFactory(ClassLoader classLoader) {
         if (classLoader instanceof JRubyClassLoader) {
             this.classLoader = (JRubyClassLoader)classLoader;
         } else {
            this.classLoader = new JRubyClassLoader(classLoader);
         }
     }
 
     /**
      * Use code generation to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethodLazily(
             RubyModule implementationClass,
             String method,
             Arity arity,
             Visibility visibility,
             StaticScope scope,
             Object scriptObject,
             CallConfiguration callConfig,
             ISourcePosition position,
             String parameterDesc) {
 
         return new CompiledMethod.LazyCompiledMethod(
                 implementationClass,
                 method,
                 arity,
                 visibility,
                 scope,
                 scriptObject,
                 callConfig,
                 position,
                 parameterDesc,
                 new InvocationMethodFactory(classLoader));
     }
 
     private static String getCompiledCallbackName(String typePath, String method) {
         return (typePath + "$" + method).replaceAll("/", "\\$");
     }
 
     /**
      * Use code generation to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethod(
             RubyModule implementationClass,
             String method,
             Arity arity,
             Visibility visibility,
             StaticScope scope,
             Object scriptObject,
             CallConfiguration callConfig,
             ISourcePosition position,
             String parameterDesc) {
         
         Class scriptClass = scriptObject.getClass();
         String typePath = p(scriptClass);
         String invokerPath = getCompiledCallbackName(typePath, method);
         synchronized (classLoader) {
             Class generatedClass = tryClass(invokerPath, scriptClass);
 
             try {
                 if (generatedClass == null) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("no generated handle in classloader for: " + invokerPath);
                     }
                     byte[] invokerBytes = getCompiledMethodOffline(
                             method,
                             typePath,
                             invokerPath,
                             arity,
                             scope,
                             callConfig,
                             position.getFile(),
                             position.getStartLine());
                     generatedClass = endCallWithBytes(invokerBytes, invokerPath);
                 } else if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                     System.err.println("found generated handle in classloader: " + invokerPath);
                 }
 
                 CompiledMethod compiledMethod = (CompiledMethod)generatedClass.newInstance();
                 compiledMethod.init(implementationClass, arity, visibility, scope, scriptObject, callConfig, position, parameterDesc);
                 
                 if (arity.isFixed() && arity.required() <= 3) {
                     Class[] params = StandardASMCompiler.getStaticMethodParams(scriptClass, scope.getRequiredArgs());
                     compiledMethod.setNativeCall(scriptClass, method, IRubyObject.class, params, true);
                 }
                 return compiledMethod;
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Use code generation to provide a method handle for a compiled Ruby method.
      *
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     @Override
     public byte[] getCompiledMethodOffline(
             String method, String className, String invokerPath, Arity arity,
             StaticScope scope, CallConfiguration callConfig, String filename, int line) {
         String sup = COMPILED_SUPER_CLASS;
         ClassWriter cw;
         cw = createCompiledCtor(invokerPath, invokerPath, sup);
         SkinnyMethodAdapter mv = null;
         String signature = null;
         boolean specificArity = false;
 
         // if trace, need to at least populate a backtrace frame
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             switch (callConfig) {
             case FrameNoneScopeDummy:
                 callConfig = CallConfiguration.FrameBacktraceScopeDummy;
                 break;
             case FrameNoneScopeFull:
                 callConfig = CallConfiguration.FrameBacktraceScopeFull;
                 break;
             case FrameNoneScopeNone:
                 callConfig = CallConfiguration.FrameBacktraceScopeNone;
                 break;
             }
         }
 
         if (scope.getRestArg() >= 0 || scope.getOptionalArgs() > 0 || scope.getRequiredArgs() > 3) {
             signature = COMPILED_CALL_SIG_BLOCK;
             mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "call", signature, null, null);
         } else {
             specificArity = true;
 
             mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "call", COMPILED_CALL_SIG_BLOCK, null, null);
             mv.start();
             mv.line(-1);
 
             // check arity
             mv.aloadMany(0, 1, 4, 5); // method, context, name, args, required
             mv.pushInt(scope.getRequiredArgs());
             mv.invokestatic(p(JavaMethod.class), "checkArgumentCount", sig(void.class, JavaMethod.class, ThreadContext.class, String.class, IRubyObject[].class, int.class));
 
             mv.aloadMany(0, 1, 2, 3, 4);
             for (int i = 0; i < scope.getRequiredArgs(); i++) {
                 mv.aload(5);
                 mv.ldc(i);
                 mv.arrayload();
             }
             mv.aload(6);
 
             switch (scope.getRequiredArgs()) {
             case 0:
                 signature = COMPILED_CALL_SIG_ZERO_BLOCK;
                 break;
             case 1:
                 signature = COMPILED_CALL_SIG_ONE_BLOCK;
                 break;
             case 2:
                 signature = COMPILED_CALL_SIG_TWO_BLOCK;
                 break;
             case 3:
                 signature = COMPILED_CALL_SIG_THREE_BLOCK;
                 break;
             }
 
             mv.invokevirtual(invokerPath, "call", signature);
             mv.areturn();
             mv.end();
 
             // Define a second version that doesn't take a block, so we have unique code paths for both cases.
             switch (scope.getRequiredArgs()) {
             case 0:
                 signature = COMPILED_CALL_SIG_ZERO;
                 break;
             case 1:
                 signature = COMPILED_CALL_SIG_ONE;
                 break;
             case 2:
                 signature = COMPILED_CALL_SIG_TWO;
                 break;
             case 3:
                 signature = COMPILED_CALL_SIG_THREE;
                 break;
             }
             mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "call", signature, null, null);
             mv.start();
             mv.line(-1);
 
             mv.aloadMany(0, 1, 2, 3, 4);
             for (int i = 1; i <= scope.getRequiredArgs(); i++) {
                 mv.aload(4 + i);
             }
             mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
             switch (scope.getRequiredArgs()) {
             case 0:
                 signature = COMPILED_CALL_SIG_ZERO_BLOCK;
                 break;
             case 1:
                 signature = COMPILED_CALL_SIG_ONE_BLOCK;
                 break;
             case 2:
                 signature = COMPILED_CALL_SIG_TWO_BLOCK;
                 break;
             case 3:
                 signature = COMPILED_CALL_SIG_THREE_BLOCK;
                 break;
             }
 
             mv.invokevirtual(invokerPath, "call", signature);
             mv.areturn();
             mv.end();
 
             mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "call", signature, null, null);
         }
 
         mv.visitCode();
         mv.line(-1);
 
         // save off callNumber
         mv.aload(1);
         mv.getfield(p(ThreadContext.class), "callNumber", ci(int.class));
         int callNumberIndex = -1;
         if (specificArity) {
             switch (scope.getRequiredArgs()) {
             case -1:
                 callNumberIndex = ARGS_INDEX + 1/*args*/ + 1/*block*/ + 1;
                 break;
             case 0:
                 callNumberIndex = ARGS_INDEX + 1/*block*/ + 1;
                 break;
             default:
                 callNumberIndex = ARGS_INDEX + scope.getRequiredArgs() + 1/*block*/ + 1;
             }
         } else {
             callNumberIndex = ARGS_INDEX + 1/*block*/ + 1;
         }
         mv.istore(callNumberIndex);
 
         // invoke pre method stuff
         if (!callConfig.isNoop() || RubyInstanceConfig.FULL_TRACE_ENABLED) {
             if (specificArity) {
                 invokeCallConfigPre(mv, COMPILED_SUPER_CLASS, scope.getRequiredArgs(), true, callConfig);
             } else {
                 invokeCallConfigPre(mv, COMPILED_SUPER_CLASS, -1, true, callConfig);
             }
         }
 
         // pre-call trace
         int traceBoolIndex = -1;
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // load and store trace enabled flag
             if (specificArity) {
                 switch (scope.getRequiredArgs()) {
                 case -1:
                     traceBoolIndex = ARGS_INDEX + 1/*args*/ + 1/*block*/ + 2;
                     break;
                 case 0:
                     traceBoolIndex = ARGS_INDEX + 1/*block*/ + 2;
                     break;
                 default:
                     traceBoolIndex = ARGS_INDEX + scope.getRequiredArgs() + 1/*block*/ + 2;
                 }
             } else {
                 traceBoolIndex = ARGS_INDEX + 1/*block*/ + 2;
             }
 
             mv.aload(1);
             mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             mv.invokevirtual(p(Ruby.class), "hasEventHooks", sig(boolean.class));
             mv.istore(traceBoolIndex);
             // tracing pre
             invokeTraceCompiledPre(mv, COMPILED_SUPER_CLASS, traceBoolIndex, filename, line);
         }
 
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label doFinally = new Label();
         Label doReturnFinally = new Label();
         Label doRedoFinally = new Label();
         Label catchReturnJump = new Label();
         Label catchRedoJump = new Label();
 
         boolean heapScoped = callConfig.scoping() != Scoping.None;
         boolean framed = callConfig.framing() != Framing.None;
 
         if (framed || heapScoped)   mv.trycatch(tryBegin, tryEnd, catchReturnJump, p(JumpException.ReturnJump.class));
         if (framed)                 mv.trycatch(tryBegin, tryEnd, catchRedoJump, p(JumpException.RedoJump.class));
         if (framed || heapScoped)   mv.trycatch(tryBegin, tryEnd, doFinally, null);
         if (framed || heapScoped)   mv.trycatch(catchReturnJump, doReturnFinally, doFinally, null);
         if (framed)                 mv.trycatch(catchRedoJump, doRedoFinally, doFinally, null);
         if (framed || heapScoped)   mv.label(tryBegin);
 
         // main body
         {
             mv.aload(0);
             // FIXME we want to eliminate these type casts when possible
             mv.getfield(invokerPath, "$scriptObject", ci(Object.class));
             mv.checkcast(className);
             mv.aloadMany(THREADCONTEXT_INDEX, RECEIVER_INDEX);
             if (specificArity) {
                 for (int i = 0; i < scope.getRequiredArgs(); i++) {
                     mv.aload(ARGS_INDEX + i);
                 }
                 mv.aload(ARGS_INDEX + scope.getRequiredArgs());
                 mv.invokestatic(className, method, StandardASMCompiler.getStaticMethodSignature(className, scope.getRequiredArgs()));
             } else {
                 mv.aloadMany(ARGS_INDEX, BLOCK_INDEX);
                 mv.invokestatic(className, method, StandardASMCompiler.getStaticMethodSignature(className, 4));
             }
         }
         if (framed || heapScoped) {
             mv.label(tryEnd);
         }
 
         // normal exit, perform finally and return
         {
             if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                 invokeTraceCompiledPost(mv, COMPILED_SUPER_CLASS, traceBoolIndex);
             }
             if (!callConfig.isNoop()) {
                 invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
             }
             mv.visitInsn(ARETURN);
         }
 
         // return jump handling
         if (framed || heapScoped) {
             mv.label(catchReturnJump);
             {
                 mv.aload(0);
                 mv.swap();
                 mv.aload(1);
                 mv.swap();
                 mv.iload(callNumberIndex);
                 mv.invokevirtual(COMPILED_SUPER_CLASS, "handleReturn", sig(IRubyObject.class, ThreadContext.class, JumpException.ReturnJump.class, int.class));
                 mv.label(doReturnFinally);
 
                 // finally
                 if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                     invokeTraceCompiledPost(mv, COMPILED_SUPER_CLASS, traceBoolIndex);
                 }
                 if (!callConfig.isNoop()) {
                     invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                 }
 
                 // return result if we're still good
                 mv.areturn();
             }
         }
 
         if (framed) {
             // redo jump handling
             mv.label(catchRedoJump);
             {
                 // clear the redo
                 mv.pop();
 
                 // get runtime, create jump error, and throw it
                 mv.aload(1);
                 mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                 mv.invokevirtual(p(Ruby.class), "newRedoLocalJumpError", sig(RaiseException.class));
                 mv.label(doRedoFinally);
 
                 // finally
                 if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                     invokeTraceCompiledPost(mv, COMPILED_SUPER_CLASS, traceBoolIndex);
                 }
                 if (!callConfig.isNoop()) {
                     invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
                 }
 
                 // throw redo error if we're still good
                 mv.athrow();
             }
         }
 
         // finally handling for abnormal exit
         if (framed || heapScoped) {
             mv.label(doFinally);
 
             //call post method stuff (exception raised)
             if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                 invokeTraceCompiledPost(mv, COMPILED_SUPER_CLASS, traceBoolIndex);
             }
             if (!callConfig.isNoop()) {
                 invokeCallConfigPost(mv, COMPILED_SUPER_CLASS, callConfig);
             }
 
             // rethrow exception
             mv.athrow(); // rethrow it
         }
         mv.end();
 
         return endCallOffline(cw);
     }
     
     private static class DescriptorInfo {
         private int min;
         private int max;
         private boolean frame;
         private boolean scope;
         private boolean backtrace;
         private boolean rest;
         private boolean block;
         
         public DescriptorInfo(List<JavaMethodDescriptor> descs) {
             min = Integer.MAX_VALUE;
             max = 0;
             frame = false;
             scope = false;
             backtrace = false;
             rest = false;
             block = false;
             boolean first = true;
             boolean lastBlock = false;
 
             for (JavaMethodDescriptor desc: descs) {
                 // make sure we don't have some methods with blocks and others without
                 // the handle generation logic can't handle such cases yet
                 if (first) {
                     first = false;
                 } else {
                     if (lastBlock != desc.hasBlock) {
                         throw new RuntimeException("Mismatched block parameters for method " + desc.declaringClassName + "." + desc.name);
                     }
                 }
                 lastBlock = desc.hasBlock;
                 
                 int specificArity = -1;
                 if (desc.hasVarArgs) {
                     if (desc.optional == 0 && !desc.rest && desc.required == 0) {
                         throw new RuntimeException("IRubyObject[] args but neither of optional or rest specified for method " + desc.declaringClassName + "." + desc.name);
                     }
                     rest = true;
                     if (descs.size() == 1) {
                         min = -1;
                     }
                 } else {
                     if (desc.optional == 0 && !desc.rest) {
                         if (desc.required == 0) {
                             // No required specified, check actual number of required args
                             if (desc.actualRequired <= 3) {
                                 // actual required is less than 3, so we use specific arity
                                 specificArity = desc.actualRequired;
                             } else {
                                 // actual required is greater than 3, raise error (we don't support actual required > 3)
                                 throw new RuntimeException("Invalid specific-arity number of arguments (" + desc.actualRequired + ") on method " + desc.declaringClassName + "." + desc.name);
                             }
                         } else if (desc.required >= 0 && desc.required <= 3) {
                             if (desc.actualRequired != desc.required) {
                                 throw new RuntimeException("Specified required args does not match actual on method " + desc.declaringClassName + "." + desc.name);
                             }
                             specificArity = desc.required;
                         }
                     }
 
                     if (specificArity < min) {
                         min = specificArity;
                     }
 
                     if (specificArity > max) {
                         max = specificArity;
                     }
                 }
 
                 frame |= desc.anno.frame();
                 scope |= desc.anno.scope();
                 backtrace |= desc.anno.backtrace();
                 block |= desc.hasBlock;
             }
         }
         
         public boolean isBacktrace() {
             return backtrace;
         }
 
         public boolean isFrame() {
             return frame;
         }
 
         public int getMax() {
             return max;
         }
 
         public int getMin() {
             return min;
         }
 
         public boolean isScope() {
             return scope;
         }
         
         public boolean isRest() {
             return rest;
         }
         
         public boolean isBlock() {
             return block;
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, List<JavaMethodDescriptor> descs) {
         JavaMethodDescriptor desc1 = descs.get(0);
         String javaMethodName = desc1.name;
         
         if (DEBUG) out.println("Binding multiple: " + desc1.declaringClassName + "." + javaMethodName);
         
         synchronized (classLoader) {
             try {
                 Class c = getAnnotatedMethodClass(descs);
                 
                 DescriptorInfo info = new DescriptorInfo(descs);
                 if (DEBUG) out.println(" min: " + info.getMin() + ", max: " + info.getMax());
 
                 JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class}).newInstance(new Object[]{implementationClass, desc1.anno.visibility()});
 
-                ic.setArity(Arity.OPTIONAL);
-                ic.setJavaName(javaMethodName);
-                ic.setSingleton(desc1.isStatic);
-                ic.setCallConfig(CallConfiguration.getCallConfig(info.isFrame(), info.isScope(), info.isBacktrace()));
+                TypePopulator.populateMethod(
+                        ic,
+                        Arity.optional().getValue(),
+                        javaMethodName,
+                        desc1.isStatic,
+                        CallConfiguration.getCallConfig(info.isFrame(), info.isScope(), info.isBacktrace()),
+                        desc1.anno.notImplemented());
                 return ic;
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method. Return the resulting generated or loaded class.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public Class getAnnotatedMethodClass(List<JavaMethodDescriptor> descs) throws Exception {
         JavaMethodDescriptor desc1 = descs.get(0);
 
         if (!Modifier.isPublic(desc1.getDeclaringClass().getModifiers())) {
             System.err.println("warning: binding non-public class" + desc1.declaringClassName + "; reflected handles won't work");
         }
         
         String javaMethodName = desc1.name;
         
         if (DEBUG) {
             if (descs.size() > 1) {
                 out.println("Binding multiple: " + desc1.declaringClassName + "." + javaMethodName);
             } else {
                 out.println("Binding single: " + desc1.declaringClassName + "." + javaMethodName);
             }
         }
         
         String generatedClassName = CodegenUtils.getAnnotatedBindingClassName(javaMethodName, desc1.declaringClassName, desc1.isStatic, desc1.actualRequired, desc1.optional, descs.size() > 1, desc1.anno.frame());
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // in debug mode we append _DBG to class name to force it to regenerate (or use pre-generated debug version)
             generatedClassName += "_DBG";
         }
         String generatedClassPath = generatedClassName.replace('.', '/');
         
         synchronized (classLoader) {
             Class c = tryClass(generatedClassName, desc1.getDeclaringClass());
 
             DescriptorInfo info = new DescriptorInfo(descs);
             if (DEBUG) out.println(" min: " + info.getMin() + ", max: " + info.getMax() + ", hasBlock: " + info.isBlock() + ", rest: " + info.isRest());
 
             if (c == null) {
                 Class superClass = null;
                 if (info.getMin() == -1) {
                     // normal all-rest method
                     if (info.isBlock()) {
                         superClass = JavaMethod.JavaMethodNBlock.class;
                     } else {
                         superClass = JavaMethod.JavaMethodN.class;
                     }
                 } else {
                     if (info.isRest()) {
                         if (info.isBlock()) {
                             superClass = JavaMethod.BLOCK_REST_METHODS[info.getMin()][info.getMax()];
                         } else {
                             superClass = JavaMethod.REST_METHODS[info.getMin()][info.getMax()];
                         }
                     } else {
                         if (info.isBlock()) {
                             superClass = JavaMethod.BLOCK_METHODS[info.getMin()][info.getMax()];
                         } else {
                             superClass = JavaMethod.METHODS[info.getMin()][info.getMax()];
                         }
                     }
                 }
                 
                 if (superClass == null) throw new RuntimeException("invalid multi combination");
                 String superClassString = p(superClass);
                 int dotIndex = desc1.declaringClassName.lastIndexOf('.');
                 ClassWriter cw = createJavaMethodCtor(generatedClassPath, desc1.declaringClassName.substring(dotIndex + 1) + "$" + desc1.name, superClassString);
 
                 addAnnotatedMethodInvoker(cw, "call", superClassString, descs);
 
                 c = endClass(cw, generatedClassName);
             }
 
             return c;
         }
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, JavaMethodDescriptor desc) {
         String javaMethodName = desc.name;
         
         synchronized (classLoader) {
             try {
                 Class c = getAnnotatedMethodClass(Arrays.asList(desc));
 
                 JavaMethod ic = (JavaMethod)c.getConstructor(new Class[]{RubyModule.class, Visibility.class}).newInstance(new Object[]{implementationClass, desc.anno.visibility()});
 
-                ic.setArity(Arity.fromAnnotation(desc.anno, desc.actualRequired));
-                ic.setJavaName(javaMethodName);
-                ic.setSingleton(desc.isStatic);
-                ic.setCallConfig(CallConfiguration.getCallConfigByAnno(desc.anno));
+                TypePopulator.populateMethod(
+                        ic,
+                        Arity.fromAnnotation(desc.anno, desc.actualRequired).getValue(),
+                        javaMethodName,
+                        desc.isStatic,
+                        CallConfiguration.getCallConfigByAnno(desc.anno),
+                        desc.anno.notImplemented());
                 return ic;
             } catch(Exception e) {
                 e.printStackTrace();
                 throw implementationClass.getRuntime().newLoadError(e.getMessage());
             }
         }
     }
 
     private static String getBlockCallbackName(String typePathString, String method) {
         return (typePathString + "$" + method).replaceAll("/", "\\$");
     }
 
     public CompiledBlockCallback getBlockCallback(String method, String file, int line, Object scriptObject) {
         Class typeClass = scriptObject.getClass();
         String typePathString = p(typeClass);
         String mname = getBlockCallbackName(typePathString, method);
         synchronized (classLoader) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("no generated handle in classloader for: " + mname);
                     }
                     byte[] bytes = getBlockCallbackOffline(method, file, line, typePathString);
                     c = endCallWithBytes(bytes, mname);
                 } else {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("found generated handle in classloader for: " + mname);
                     }
                 }
                 
                 CompiledBlockCallback ic = (CompiledBlockCallback) c.getConstructor(Object.class).newInstance(scriptObject);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 e.printStackTrace();
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     @Override
     public byte[] getBlockCallbackOffline(String method, String file, int line, String classname) {
         String mname = getBlockCallbackName(classname, method);
         ClassWriter cw = createBlockCtor(mname, classname);
         SkinnyMethodAdapter mv = startBlockCall(cw);
         mv.line(-1);
         mv.aload(0);
         mv.getfield(mname, "$scriptObject", "L" + classname + ";");
         mv.aloadMany(1, 2, 3, 4);
         mv.invokestatic(classname, method, sig(
                 IRubyObject.class, "L" + classname + ";", ThreadContext.class,
                         IRubyObject.class, IRubyObject.class, Block.class));
         mv.areturn();
         mv.end();
 
         mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "getFile", sig(String.class), null, null);
         mv.start();
         mv.ldc(file);
         mv.areturn();
         mv.end();
 
         mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "getLine", sig(int.class), null, null);
         mv.start();
         mv.ldc(line);
         mv.ireturn();
         mv.end();
 
         return endCallOffline(cw);
     }
 
     public CompiledBlockCallback19 getBlockCallback19(String method, String file, int line, Object scriptObject) {
         Class typeClass = scriptObject.getClass();
         String typePathString = p(typeClass);
         String mname = getBlockCallbackName(typePathString, method);
         synchronized (classLoader) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("no generated handle in classloader for: " + mname);
                     }
                     byte[] bytes = getBlockCallback19Offline(method, file, line, typePathString);
                     c = endClassWithBytes(bytes, mname);
                 } else {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("found generated handle in classloader for: " + mname);
                     }
                 }
                 
                 CompiledBlockCallback19 ic = (CompiledBlockCallback19) c.getConstructor(Object.class).newInstance(scriptObject);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 e.printStackTrace();
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     @Override
     public byte[] getBlockCallback19Offline(String method, String file, int line, String classname) {
         String mnamePath = getBlockCallbackName(classname, method);
         ClassWriter cw = createBlockCtor19(mnamePath, classname);
         SkinnyMethodAdapter mv = startBlockCall19(cw);
         mv.line(-1);
         mv.aload(0);
         mv.getfield(mnamePath, "$scriptObject", "L" + classname + ";");
         mv.aloadMany(1, 2, 3, 4);
         mv.invokestatic(classname, method, sig(
                 IRubyObject.class, "L" + classname + ";", ThreadContext.class,
                         IRubyObject.class, IRubyObject[].class, Block.class));
         mv.areturn();
         mv.end();
 
         mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "getFile", sig(String.class), null, null);
         mv.start();
         mv.ldc(file);
         mv.areturn();
         mv.end();
 
         mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "getLine", sig(int.class), null, null);
         mv.start();
         mv.ldc(line);
         mv.ireturn();
         mv.end();
         
         return endCallOffline(cw);
     }
 
     private SkinnyMethodAdapter startBlockCall(ClassWriter cw) {
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_SYNTHETIC | ACC_FINAL, "call", BLOCK_CALL_SIG, null, null);
 
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         return mv;
     }
 
     private SkinnyMethodAdapter startBlockCall19(ClassWriter cw) {
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_SYNTHETIC | ACC_FINAL, "call", BLOCK_CALL_SIG19, null, null);
 
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         return mv;
     }
 
     private ClassWriter createBlockCtor(String namePath, String classname) {
         String ciClassname = "L" + classname + ";";
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, p(CompiledBlockCallback.class), null);
         cw.visitSource(namePath, null);
         cw.visitField(ACC_PRIVATE | ACC_FINAL, "$scriptObject", ciClassname, null, null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "<init>", sig(Void.TYPE, params(Object.class)), null, null);
         mv.start();
         mv.line(-1);
         mv.aload(0);
         mv.invokespecial(p(CompiledBlockCallback.class), "<init>", sig(void.class));
         mv.aloadMany(0, 1);
         mv.checkcast(classname);
         mv.putfield(namePath, "$scriptObject", ciClassname);
         mv.voidreturn();
         mv.end();
 
         return cw;
     }
 
     private ClassWriter createBlockCtor19(String namePath, String classname) {
         String ciClassname = "L" + classname + ";";
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, p(Object.class), new String[] {p(CompiledBlockCallback19.class)});
         cw.visitSource(namePath, null);
         cw.visitField(ACC_PRIVATE | ACC_FINAL, "$scriptObject", ciClassname, null, null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "<init>", sig(Void.TYPE, params(Object.class)), null, null);
         mv.start();
         mv.line(-1);
         mv.aload(0);
         mv.invokespecial(p(Object.class), "<init>", sig(void.class));
         mv.aloadMany(0, 1);
         mv.checkcast(classname);
         mv.putfield(namePath, "$scriptObject", ciClassname);
         mv.voidreturn();
         mv.end();
 
         return cw;
     }
 
     /**
      * Use code generation to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public void prepareAnnotatedMethod(RubyModule implementationClass, JavaMethod javaMethod, JavaMethodDescriptor desc) {
         String javaMethodName = desc.name;
         
         javaMethod.setArity(Arity.fromAnnotation(desc.anno, desc.actualRequired));
         javaMethod.setJavaName(javaMethodName);
         javaMethod.setSingleton(desc.isStatic);
         javaMethod.setCallConfig(CallConfiguration.getCallConfigByAnno(desc.anno));
     }
 
     /**
      * Emit code to check the arity of a call to a Java-based method.
      * 
      * @param jrubyMethod The annotation of the called method
      * @param method The code generator for the handle being created
      */
     private void checkArity(JRubyMethod jrubyMethod, SkinnyMethodAdapter method, int specificArity) {
         Label arityError = new Label();
         Label noArityError = new Label();
         
         switch (specificArity) {
         case 0:
         case 1:
         case 2:
         case 3:
             // for zero, one, two, three arities, JavaMethod.JavaMethod*.call(...IRubyObject[] args...) will check
             return;
         default:
             boolean checkArity = false;
             if (jrubyMethod.rest()) {
                 if (jrubyMethod.required() > 0) {
                     // just confirm minimum args provided
                     method.aload(ARGS_INDEX);
                     method.arraylength();
                     method.ldc(jrubyMethod.required());
                     method.if_icmplt(arityError);
                     checkArity = true;
                 }
             } else if (jrubyMethod.optional() > 0) {
                 if (jrubyMethod.required() > 0) {
                     // confirm minimum args provided
                     method.aload(ARGS_INDEX);
                     method.arraylength();
                     method.ldc(jrubyMethod.required());
                     method.if_icmplt(arityError);
                 }
 
                 // confirm maximum not greater than optional
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(jrubyMethod.required() + jrubyMethod.optional());
                 method.if_icmpgt(arityError);
                 checkArity = true;
             } else {
                 // just confirm args length == required
                 method.aload(ARGS_INDEX);
                 method.arraylength();
                 method.ldc(jrubyMethod.required());
                 method.if_icmpne(arityError);
                 checkArity = true;
             }
 
             if (checkArity) {
                 method.go_to(noArityError);
 
                 // Raise an error if arity does not match requirements
                 method.label(arityError);
                 method.aload(THREADCONTEXT_INDEX);
                 method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                 method.aload(ARGS_INDEX);
                 method.ldc(jrubyMethod.required());
                 method.ldc(jrubyMethod.required() + jrubyMethod.optional());
                 method.invokestatic(p(Arity.class), "checkArgumentCount", sig(int.class, Ruby.class, IRubyObject[].class, int.class, int.class));
                 method.pop();
 
                 method.label(noArityError);
             }
         }
     }
 
     private ClassWriter createCompiledCtor(String namePath, String shortPath, String sup) {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         cw.visitSource(shortPath, null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "<init>", "()V", null, null);
         mv.visitCode();
         mv.line(-1);
         mv.aload(0);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", "()V");
         mv.visitLineNumber(-1, new Label());
         mv.voidreturn();
         mv.end();
 
         return cw;
     }
 
     private ClassWriter createJavaMethodCtor(String namePath, String shortPath, String sup) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
         String sourceFile = namePath.substring(namePath.lastIndexOf('/') + 1) + ".gen";
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, namePath, null, sup, null);
         cw.visitSource(sourceFile, null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "<init>", JAVA_SUPER_SIG, null, null);
         mv.start();
         mv.line(-1);
         mv.aloadMany(0, 1, 2);
         mv.visitMethodInsn(INVOKESPECIAL, sup, "<init>", JAVA_SUPER_SIG);
         mv.visitLineNumber(-1, new Label());
         mv.voidreturn();
         mv.end();
         
         return cw;
     }
 
     private void invokeCallConfigPre(SkinnyMethodAdapter mv, String superClass, int specificArity, boolean block, CallConfiguration callConfig) {
         // invoke pre method stuff
         if (callConfig.isNoop()) return;
 
         prepareForPre(mv, specificArity, block, callConfig);
         mv.invokevirtual(superClass, getPreMethod(callConfig), getPreSignature(callConfig));
     }
 
     private void invokeCallConfigPost(SkinnyMethodAdapter mv, String superClass, CallConfiguration callConfig) {
         if (callConfig.isNoop()) return;
 
         mv.aload(1);
         mv.invokestatic(superClass, getPostMethod(callConfig), sig(void.class, params(ThreadContext.class)));
     }
 
     private void prepareForPre(SkinnyMethodAdapter mv, int specificArity, boolean block, CallConfiguration callConfig) {
         if (callConfig.isNoop()) return;
         
         mv.aloadMany(0, THREADCONTEXT_INDEX);
         
         switch (callConfig.framing()) {
         case Full:
             mv.aloadMany(RECEIVER_INDEX, NAME_INDEX); // self, name
             loadBlockForPre(mv, specificArity, block);
             break;
         case Backtrace:
             mv.aload(NAME_INDEX); // name
             break;
         case None:
             break;
         default: throw new RuntimeException("Unknown call configuration");
         }
     }
 
     private String getPreMethod(CallConfiguration callConfig) {
         switch (callConfig) {
         case FrameFullScopeFull: return "preFrameAndScope";
         case FrameFullScopeDummy: return "preFrameAndDummyScope";
         case FrameFullScopeNone: return "preFrameOnly";
         case FrameBacktraceScopeFull: return "preBacktraceAndScope";
         case FrameBacktraceScopeDummy: return "preBacktraceDummyScope";
         case FrameBacktraceScopeNone:  return "preBacktraceOnly";
         case FrameNoneScopeFull: return "preScopeOnly";
         case FrameNoneScopeDummy: return "preNoFrameDummyScope";
         case FrameNoneScopeNone: return "preNoop";
         default: throw new RuntimeException("Unknown call configuration");
         }
     }
 
     private String getPreSignature(CallConfiguration callConfig) {
         switch (callConfig) {
         case FrameFullScopeFull: return sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class));
         case FrameFullScopeDummy: return sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class));
         case FrameFullScopeNone: return sig(void.class, params(ThreadContext.class, IRubyObject.class, String.class, Block.class));
         case FrameBacktraceScopeFull: return sig(void.class, params(ThreadContext.class, String.class));
         case FrameBacktraceScopeDummy: return sig(void.class, params(ThreadContext.class, String.class));
         case FrameBacktraceScopeNone:  return sig(void.class, params(ThreadContext.class, String.class));
         case FrameNoneScopeFull: return sig(void.class, params(ThreadContext.class));
         case FrameNoneScopeDummy: return sig(void.class, params(ThreadContext.class));
         case FrameNoneScopeNone: return sig(void.class);
         default: throw new RuntimeException("Unknown call configuration");
         }
     }
 
     public static String getPostMethod(CallConfiguration callConfig) {
         switch (callConfig) {
         case FrameFullScopeFull: return "postFrameAndScope";
         case FrameFullScopeDummy: return "postFrameAndScope";
         case FrameFullScopeNone: return "postFrameOnly";
         case FrameBacktraceScopeFull: return "postBacktraceAndScope";
         case FrameBacktraceScopeDummy: return "postBacktraceDummyScope";
         case FrameBacktraceScopeNone:  return "postBacktraceOnly";
         case FrameNoneScopeFull: return "postScopeOnly";
         case FrameNoneScopeDummy: return "postNoFrameDummyScope";
         case FrameNoneScopeNone: return "postNoop";
         default: throw new RuntimeException("Unknown call configuration");
         }
     }
 
     private void loadArguments(SkinnyMethodAdapter mv, JavaMethodDescriptor desc, int specificArity) {
         switch (specificArity) {
         default:
         case -1:
             mv.aload(ARGS_INDEX);
             break;
         case 0:
             // no args
             break;
         case 1:
             loadArgumentWithCast(mv, 1, desc.argumentTypes[0]);
             break;
         case 2:
             loadArgumentWithCast(mv, 1, desc.argumentTypes[0]);
             loadArgumentWithCast(mv, 2, desc.argumentTypes[1]);
             break;
         case 3:
             loadArgumentWithCast(mv, 1, desc.argumentTypes[0]);
             loadArgumentWithCast(mv, 2, desc.argumentTypes[1]);
             loadArgumentWithCast(mv, 3, desc.argumentTypes[2]);
             break;
         }
     }
 
     private void loadArgumentWithCast(SkinnyMethodAdapter mv, int argNumber, Class coerceType) {
         mv.aload(ARGS_INDEX + (argNumber - 1));
         if (coerceType != IRubyObject.class && coerceType != IRubyObject[].class) {
             if (coerceType == RubyString.class) {
                 mv.invokeinterface(p(IRubyObject.class), "convertToString", sig(RubyString.class));
             } else {
                 throw new RuntimeException("Unknown coercion target: " + coerceType);
             }
         }
     }
 
     /** load block argument for pre() call.  Since we have fixed-arity call
      * paths we need calculate where the last var holding the block is.
      *
      * is we don't have a block we setup NULL_BLOCK as part of our null pattern
      * strategy (we do not allow null in any field which accepts block).
      */
     private void loadBlockForPre(SkinnyMethodAdapter mv, int specificArity, boolean getsBlock) {
         if (!getsBlock) {            // No block so load null block instance
             mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
             return;
         }
 
         loadBlock(mv, specificArity, getsBlock);
     }
 
     /** load the block argument from the correct position.  Since we have fixed-
      * arity call paths we need to calculate where the last var holding the
      * block is.
      * 
      * If we don't have a block then this does nothing.
      */
     private void loadBlock(SkinnyMethodAdapter mv, int specificArity, boolean getsBlock) {
         if (!getsBlock) return;         // No block so nothing more to do
         
         switch (specificArity) {        // load block since it accepts a block
         case 0: case 1: case 2: case 3: // Fixed arities signatures
             mv.aload(BLOCK_INDEX - 1 + specificArity);
             break;
         default: case -1:
             mv.aload(BLOCK_INDEX);      // Generic arity signature
             break;
         }
     }
 
     private void loadReceiver(String typePath, JavaMethodDescriptor desc, SkinnyMethodAdapter mv) {
         // load target for invocations
         if (Modifier.isStatic(desc.modifiers)) {
             if (desc.hasContext) {
                 mv.aload(THREADCONTEXT_INDEX);
             }
             
             // load self object as IRubyObject, for recv param
             mv.aload(RECEIVER_INDEX);
         } else {
             // load receiver as original type for virtual invocation
             mv.aload(RECEIVER_INDEX);
             mv.checkcast(typePath);
             
             if (desc.hasContext) {
                 mv.aload(THREADCONTEXT_INDEX);
             }
         }
     }
     private Class tryClass(String name, Class targetClass) {
         Class c = null;
         try {
             if (classLoader == null) {
                 c = Class.forName(name, true, classLoader);
             } else {
                 c = classLoader.loadClass(name);
             }
         } catch(Exception e) {
             seenUndefinedClasses = true;
             return null;
         }
 
         try {
             // For JRUBY-5038, try getting call method to ensure classloaders are lining up right
             // If this fails, it usually means we loaded an invoker from a higher-up classloader
             c.getMethod("call", new Class[]{ThreadContext.class, IRubyObject.class, RubyModule.class,
             String.class, IRubyObject[].class, Block.class});
             
             if (c != null && seenUndefinedClasses && !haveWarnedUser) {
                 haveWarnedUser = true;
                 System.err.println("WARNING: while creating new bindings for " + targetClass + ",\n" +
                         "found an existing binding; you may want to run a clean build.");
             }
             
             return c;
         } catch(Exception e) {
             e.printStackTrace();
             seenUndefinedClasses = true;
             return null;
         }
     }
 
     private Class tryClass(String name) {
         try {
             Class c = classLoader.loadClass(name);
 
             // For JRUBY-5038, try getting a method to ensure classloaders are lining up right
             // If this fails, it usually means we loaded an invoker from a higher-up classloader
             c.getMethod("call", ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
 
             return c;
         } catch (Exception e) {
             return null;
         }
     }
 
     protected Class endCall(ClassWriter cw, String name) {
         return endClass(cw, name);
     }
 
     protected Class endCallWithBytes(byte[] classBytes, String name) {
         return endClassWithBytes(classBytes, name);
     }
 
     protected byte[] endCallOffline(ClassWriter cw) {
         return endClassOffline(cw);
     }
 
     protected Class endClass(ClassWriter cw, String name) {
         cw.visitEnd();
         byte[] code = cw.toByteArray();
         if (DEBUG) CheckClassAdapter.verify(new ClassReader(code), false, new PrintWriter(System.err));
          
         return classLoader.defineClass(name, code);
     }
 
     protected Class endClassWithBytes(byte[] code, String name) {
         return classLoader.defineClass(name, code);
     }
 
     protected byte[] endClassOffline(ClassWriter cw) {
         cw.visitEnd();
         byte[] code = cw.toByteArray();
         if (DEBUG) CheckClassAdapter.verify(new ClassReader(code), false, new PrintWriter(System.err));
 
         return code;
     }
     
     private SkinnyMethodAdapter beginMethod(ClassWriter cw, String methodName, int specificArity, boolean block) {
         switch (specificArity) {
         default:
         case -1:
             if (block) {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_BLOCK, null, null);
             } else {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG, null, null);
             }
         case 0:
             if (block) {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ZERO_BLOCK, null, null);
             } else {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ZERO, null, null);
             }
         case 1:
             if (block) {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ONE_BLOCK, null, null);
             } else {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_ONE, null, null);
             }
         case 2:
             if (block) {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_TWO_BLOCK, null, null);
             } else {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_TWO, null, null);
             }
         case 3:
             if (block) {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_THREE_BLOCK, null, null);
             } else {
                 return new SkinnyMethodAdapter(cw, ACC_PUBLIC, methodName, COMPILED_CALL_SIG_THREE, null, null);
             }
         }
     }
 
     private void addAnnotatedMethodInvoker(ClassWriter cw, String callName, String superClass, List<JavaMethodDescriptor> descs) {
         for (JavaMethodDescriptor desc: descs) {
             int specificArity = -1;
             if (desc.optional == 0 && !desc.rest) {
                 if (desc.required == 0) {
                     if (desc.actualRequired <= 3) {
                         specificArity = desc.actualRequired;
                     } else {
                         specificArity = -1;
                     }
                 } else if (desc.required >= 0 && desc.required <= 3) {
                     specificArity = desc.required;
                 }
             }
 
             boolean hasBlock = desc.hasBlock;
             SkinnyMethodAdapter mv = null;
 
             mv = beginMethod(cw, callName, specificArity, hasBlock);
             mv.visitCode();
             mv.line(-1);
 
             createAnnotatedMethodInvocation(desc, mv, superClass, specificArity, hasBlock);
 
             mv.end();
         }
     }
 
     private void createAnnotatedMethodInvocation(JavaMethodDescriptor desc, SkinnyMethodAdapter method, String superClass, int specificArity, boolean block) {
         String typePath = desc.declaringClassPath;
         String javaMethodName = desc.name;
 
         checkArity(desc.anno, method, specificArity);
         
         CallConfiguration callConfig = CallConfiguration.getCallConfigByAnno(desc.anno);
         if (!callConfig.isNoop()) {
             invokeCallConfigPre(method, superClass, specificArity, block, callConfig);
         }
 
         int traceBoolIndex = -1;
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // load and store trace enabled flag
             switch (specificArity) {
             case -1:
                 traceBoolIndex = ARGS_INDEX + (block ? 1 : 0) + 1;
                 break;
             case 0:
                 traceBoolIndex = ARGS_INDEX + (block ? 1 : 0);
                 break;
             default:
                 traceBoolIndex = ARGS_INDEX + specificArity + (block ? 1 : 0) + 1;
             }
 
             method.aload(1);
             method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
             method.invokevirtual(p(Ruby.class), "hasEventHooks", sig(boolean.class));
             method.istore(traceBoolIndex);
 
             // call trace
             invokeCCallTrace(method, traceBoolIndex);
         }
 
         Label tryBegin = new Label();
         Label tryEnd = new Label();
         Label doFinally = new Label();
 
         if (!callConfig.isNoop()) {
             method.trycatch(tryBegin, tryEnd, doFinally, null);
         }
         
         method.label(tryBegin);
         {
             loadReceiver(typePath, desc, method);
             
             loadArguments(method, desc, specificArity);
             
             loadBlock(method, specificArity, block);
 
             if (Modifier.isStatic(desc.modifiers)) {
                 // static invocation
                 method.invokestatic(typePath, javaMethodName, desc.signature);
             } else {
                 // virtual invocation
                 method.invokevirtual(typePath, javaMethodName, desc.signature);
             }
 
             if (desc.getReturnClass() == void.class) {
                 // void return type, so we need to load a nil for returning below
                 method.aload(THREADCONTEXT_INDEX);
                 method.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                 method.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
             }
         }
         method.label(tryEnd);
         
         // normal finally and exit
         {
             if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                 invokeCReturnTrace(method, traceBoolIndex);
             }
             
             if (!callConfig.isNoop()) {
                 invokeCallConfigPost(method, superClass, callConfig);
             }
 
             // return
             method.visitInsn(ARETURN);
         }
         
         // these are only needed if we have a non-noop call config
         if (!callConfig.isNoop()) {
             // finally handling for abnormal exit
             {
                 method.label(doFinally);
                 
                 if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                     invokeCReturnTrace(method, traceBoolIndex);
                 }
 
                 //call post method stuff (exception raised)
                 if (!callConfig.isNoop()) {
                     invokeCallConfigPost(method, superClass, callConfig);
                 }
 
                 // rethrow exception
                 method.athrow(); // rethrow it
             }
         }
     }
 
     private void invokeCCallTrace(SkinnyMethodAdapter method, int traceBoolIndex) {
         method.aloadMany(0, 1); // method, threadContext
         method.iload(traceBoolIndex); // traceEnable
         method.aload(4); // invokedName
         method.invokevirtual(p(JavaMethod.class), "callTrace", sig(void.class, ThreadContext.class, boolean.class, String.class));
     }
     
     private void invokeCReturnTrace(SkinnyMethodAdapter method, int traceBoolIndex) {
         method.aloadMany(0, 1); // method, threadContext
         method.iload(traceBoolIndex); // traceEnable
         method.aload(4); // invokedName
         method.invokevirtual(p(JavaMethod.class), "returnTrace", sig(void.class, ThreadContext.class, boolean.class, String.class));
     }
 
     private void invokeTraceCompiledPre(SkinnyMethodAdapter mv, String superClass, int traceBoolIndex, String filename, int line) {
         mv.aloadMany(0, 1); // method, threadContext
         mv.iload(traceBoolIndex); // traceEnable
         mv.aload(4); // invokedName
         mv.ldc(filename);
         mv.ldc(line);
         mv.invokevirtual(superClass, "callTraceCompiled", sig(void.class, ThreadContext.class, boolean.class, String.class, String.class, int.class));
     }
 
     private void invokeTraceCompiledPost(SkinnyMethodAdapter mv, String superClass, int traceBoolIndex) {
         mv.aloadMany(0, 1); // method, threadContext
         mv.iload(traceBoolIndex); // traceEnable
         mv.aload(4); // invokedName
         mv.invokevirtual(superClass, "returnTraceCompiled", sig(void.class, ThreadContext.class, boolean.class, String.class));
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/ReflectionMethodFactory.java b/src/org/jruby/internal/runtime/methods/ReflectionMethodFactory.java
index ad107a7065..1ce3ee01b2 100644
--- a/src/org/jruby/internal/runtime/methods/ReflectionMethodFactory.java
+++ b/src/org/jruby/internal/runtime/methods/ReflectionMethodFactory.java
@@ -1,241 +1,249 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (c) 2007 Peter Brant <peter.brant@gmail.com>
  * Copyright (C) 2008 The JRuby Community <www.headius.com>
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
 package org.jruby.internal.runtime.methods;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 
 import java.util.ArrayList;
 import java.util.List;
 import org.jruby.RubyModule;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JavaMethodDescriptor;
+import org.jruby.anno.TypePopulator;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockCallback19;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * This MethodFactory uses reflection to provide method handles. Reflection is
  * typically slower than code-generated handles, but it does provide a simple
  * mechanism for binding in environments where code-generation isn't supported.
  * 
  * @see org.jruby.internal.runtime.methods.MethodFactory
  */
 public class ReflectionMethodFactory extends MethodFactory {
     /**
      * Use reflection to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethodLazily(RubyModule implementationClass,
             String methodName, Arity arity, Visibility visibility, 
             StaticScope scope, Object scriptObject, CallConfiguration callConfig,
             ISourcePosition position, String parameterDesc) {
 
         return getCompiledMethod(
                 implementationClass,
                 methodName,
                 arity,
                 visibility,
                 scope,
                 scriptObject,
                 callConfig,
                 position,
                 parameterDesc);
     }
     
     /**
      * Use reflection to provide a method handle for a compiled Ruby method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getCompiledMethod
      */
     public DynamicMethod getCompiledMethod(RubyModule implementationClass,
             String methodName, Arity arity, Visibility visibility, 
             StaticScope scope, Object scriptObject, CallConfiguration callConfig,
             ISourcePosition position, String parameterDesc) {
         try {
             Class scriptClass = scriptObject.getClass();
             Method method = scriptClass.getMethod(methodName, scriptClass, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
             return new ReflectedCompiledMethod(
                     implementationClass,
                     arity,
                     visibility,
                     scope,
                     scriptObject,
                     method,
                     callConfig,
                     position,
                     parameterDesc);
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException("No method with name " + methodName + " found in " + scriptObject.getClass());
         }
     }
     
     /**
      * Use reflection to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, JavaMethodDescriptor desc) {
         try {
             if (!Modifier.isPublic(desc.getDeclaringClass().getModifiers())) {
                 System.err.println("warning: binding non-public class" + desc.declaringClassName + "; reflected handles won't work");
             }
 
             Method method = desc.getDeclaringClass().getDeclaredMethod(desc.name, desc.getParameterClasses());
             JavaMethod ic = new ReflectedJavaMethod(implementationClass, method, desc.anno);
 
-            ic.setIsBuiltin(true);
-            ic.setJavaName(method.getName());
-            ic.setSingleton(Modifier.isStatic(method.getModifiers()));
-            ic.setCallConfig(CallConfiguration.getCallConfigByAnno(desc.anno));
+            TypePopulator.populateMethod(
+                    ic,
+                    ic.getArity().getValue(),
+                    method.getName(),
+                    Modifier.isStatic(method.getModifiers()),
+                    CallConfiguration.getCallConfigByAnno(desc.anno),
+                    desc.anno.notImplemented());
+                
             return ic;
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
     
     /**
      * Use reflection to provide a method handle based on an annotated Java
      * method.
      * 
      * @see org.jruby.internal.runtime.methods.MethodFactory#getAnnotatedMethod
      */
     public DynamicMethod getAnnotatedMethod(RubyModule implementationClass, List<JavaMethodDescriptor> descs) {
         try {
             if (!Modifier.isPublic(descs.get(0).getDeclaringClass().getModifiers())) {
                 System.err.println("warning: binding non-public class" + descs.get(0).declaringClassName + "; reflected handles won't work");
             }
             
             List<Method> methods = new ArrayList();
             List<JRubyMethod> annotations = new ArrayList();
             
             for (JavaMethodDescriptor desc: descs) {
                 methods.add(desc.getDeclaringClass().getDeclaredMethod(desc.name, desc.getParameterClasses()));
                 annotations.add(desc.anno);
             }
             Method method0 = methods.get(0);
             JRubyMethod anno0 = annotations.get(0);
             
             JavaMethod ic = new ReflectedJavaMultiMethod(implementationClass, methods, annotations);
 
-            ic.setIsBuiltin(true);
-            ic.setJavaName(method0.getName());
-            ic.setSingleton(Modifier.isStatic(method0.getModifiers()));
-            ic.setCallConfig(CallConfiguration.getCallConfigByAnno(anno0));
+            TypePopulator.populateMethod(
+                    ic,
+                    ic.getArity().getValue(),
+                    method0.getName(),
+                    Modifier.isStatic(method0.getModifiers()),
+                    CallConfiguration.getCallConfigByAnno(anno0),
+                    anno0.notImplemented());
             return ic;
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
 
     public CompiledBlockCallback getBlockCallback(String method, final String file, final int line, final Object scriptObject) {
         try {
             Class scriptClass = scriptObject.getClass();
             final Method blockMethod = scriptClass.getMethod(method, scriptClass, ThreadContext.class, IRubyObject.class, IRubyObject.class, Block.class);
             return new CompiledBlockCallback() {
                 public IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject args, Block block) {
                     try {
                         return (IRubyObject)blockMethod.invoke(null, scriptObject, context, self, args, block);
                     } catch (IllegalAccessException ex) {
                         throw new RuntimeException(ex);
                     } catch (IllegalArgumentException ex) {
                         throw new RuntimeException(ex);
                     } catch (InvocationTargetException ex) {
                         Throwable cause = ex.getCause();
                         if (cause instanceof RuntimeException) {
                             throw (RuntimeException) cause;
                         } else if (cause instanceof Error) {
                             throw (Error) cause;
                         } else {
                             throw new RuntimeException(ex);
                         }
                     }
                 }
 
                 public String getFile() {
                     return file;
                 }
 
                 public int getLine() {
                     return line;
                 }
             };
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     public CompiledBlockCallback19 getBlockCallback19(String method, final String file, final int line, final Object scriptObject) {
         try {
             Class scriptClass = scriptObject.getClass();
             final Method blockMethod = scriptClass.getMethod(method, scriptClass, ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class);
             return new CompiledBlockCallback19() {
                 public IRubyObject call(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
                     try {
                         return (IRubyObject)blockMethod.invoke(null, scriptObject, context, self, args, block);
                     } catch (IllegalAccessException ex) {
                         throw new RuntimeException(ex);
                     } catch (IllegalArgumentException ex) {
                         throw new RuntimeException(ex);
                     } catch (InvocationTargetException ex) {
                         Throwable cause = ex.getCause();
                         if (cause instanceof RuntimeException) {
                             throw (RuntimeException) cause;
                         } else if (cause instanceof Error) {
                             throw (Error) cause;
                         } else {
                             throw new RuntimeException(ex);
                         }
                     }
                 }
 
                 public String getFile() {
                     return file;
                 }
 
                 public int getLine() {
                     return line;
                 }
             };
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 }
diff --git a/src/org/jruby/runtime/callsite/RespondToCallSite.java b/src/org/jruby/runtime/callsite/RespondToCallSite.java
index f3a98c1a22..ed101ab2ab 100644
--- a/src/org/jruby/runtime/callsite/RespondToCallSite.java
+++ b/src/org/jruby/runtime/callsite/RespondToCallSite.java
@@ -1,135 +1,135 @@
 package org.jruby.runtime.callsite;
 
 import org.jruby.Ruby;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.RubyClass;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Visibility;
 
 public class RespondToCallSite extends NormalCachingCallSite {
     private volatile RespondToTuple respondToTuple = RespondToTuple.NULL_CACHE;
 
     private static class RespondToTuple {
         static final RespondToTuple NULL_CACHE = new RespondToTuple("", true, CacheEntry.NULL_CACHE, CacheEntry.NULL_CACHE, null);
         public final String name;
         public final boolean checkVisibility;
         public final CacheEntry respondToMethod;
         public final CacheEntry entry;
         public final IRubyObject respondsTo;
         
         public RespondToTuple(String name, boolean checkVisibility, CacheEntry respondToMethod, CacheEntry entry, IRubyObject respondsTo) {
             this.name = name;
             this.checkVisibility = checkVisibility;
             this.respondToMethod = respondToMethod;
             this.entry = entry;
             this.respondsTo = respondsTo;
         }
 
         public boolean cacheOk(RubyClass klass) {
             return respondToMethod.typeOk(klass) && entry.typeOk(klass);
         }
     }
 
     public RespondToCallSite() {
         super("respond_to?");
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject name) { 
         RubyClass klass = self.getMetaClass();
         RespondToTuple tuple = respondToTuple;
         if (tuple.cacheOk(klass)) {
             String strName = name.asJavaString();
             if (strName.equals(tuple.name) && tuple.checkVisibility) return tuple.respondsTo;
         }
         // go through normal call logic, which will hit overridden cacheAndCall
         IRubyObject respond = super.call(context, caller, self, name);
 
         if (!respond.isTrue() && context.getRuntime().is1_9()) {
             respond = self.callMethod(context, "respond_to_missing?", new IRubyObject[]{name, context.getRuntime().getFalse()});
             respond = context.getRuntime().newBoolean(respond.isTrue());
         }
         return respond;
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject caller, IRubyObject self, IRubyObject name, IRubyObject bool) {
         RubyClass klass = self.getMetaClass();
         RespondToTuple tuple = respondToTuple;
         if (tuple.cacheOk(klass)) {
             String strName = name.asJavaString();
             if (strName.equals(tuple.name) && !bool.isTrue() == tuple.checkVisibility) return tuple.respondsTo;
         }
         // go through normal call logic, which will hit overridden cacheAndCall
         IRubyObject respond = super.call(context, caller, self, name, bool);
 
         if (!respond.isTrue() && context.getRuntime().is1_9()) {
             respond = self.callMethod(context, "respond_to_missing?", new IRubyObject[]{name, bool});
             respond = context.getRuntime().newBoolean(respond.isTrue());
         }
         return respond;
     }
 
     @Override
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, ThreadContext context, IRubyObject self, IRubyObject arg) {
         CacheEntry entry = selfType.searchWithCache(methodName);
         DynamicMethod method = entry.method;
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg);
         }
 
         // alternate logic to cache the result of respond_to if it's the standard one
         if (entry.method == context.getRuntime().getRespondToMethod()) {
             String name = arg.asJavaString();
             RespondToTuple tuple = recacheRespondsTo(entry, name, selfType, true, context);
             respondToTuple = tuple;
             return tuple.respondsTo;
         }
 
         // normal logic if it's not the builtin respond_to? method
         cache = entry;
         return method.call(context, self, selfType, methodName, arg);
     }
 
     @Override
     protected IRubyObject cacheAndCall(IRubyObject caller, RubyClass selfType, ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1) {
         CacheEntry entry = selfType.searchWithCache(methodName);
         DynamicMethod method = entry.method;
         if (methodMissing(method, caller)) {
             return callMethodMissing(context, self, method, arg0, arg1);
         }
 
         // alternate logic to cache the result of respond_to if it's the standard one
         if (entry.method == context.getRuntime().getRespondToMethod()) {
             String name = arg0.asJavaString();
             RespondToTuple tuple = recacheRespondsTo(entry, name, selfType, !arg1.isTrue(), context);
             respondToTuple = tuple;
             return tuple.respondsTo;
         }
 
         // normal logic if it's not the builtin respond_to? method
         cache = entry;
         return method.call(context, self, selfType, methodName, arg0, arg1);
     }
 
     private static RespondToTuple recacheRespondsTo(CacheEntry respondToMethod, String newString, RubyClass klass, boolean checkVisibility, ThreadContext context) {
         Ruby runtime = context.getRuntime();
         CacheEntry respondToLookupResult = klass.searchWithCache(newString);
         IRubyObject respondsTo;
-        if (!respondToLookupResult.method.isUndefined()) {
+        if (!respondToLookupResult.method.isUndefined() && !respondToLookupResult.method.isNotImplemented()) {
             respondsTo = checkVisibilityAndCache(respondToLookupResult, checkVisibility, runtime);
         } else {
             respondsTo = runtime.getFalse();
         }
         return new RespondToTuple(newString, checkVisibility, respondToMethod, respondToLookupResult, respondsTo);
     }
 
     private static IRubyObject checkVisibilityAndCache(CacheEntry respondEntry, boolean checkVisibility, Ruby runtime) {
         if (!checkVisibility || respondEntry.method.getVisibility() != Visibility.PRIVATE) {
             return runtime.getTrue();
         } else {
             return runtime.getFalse();
         }
     }
 }
\ No newline at end of file
