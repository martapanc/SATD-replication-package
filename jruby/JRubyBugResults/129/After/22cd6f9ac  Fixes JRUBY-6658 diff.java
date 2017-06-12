diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index 51d4a913c4..a236fac9fe 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -2540,1331 +2540,1334 @@ public class RubyModule extends RubyObject {
         return class_variable_set(var, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     @JRubyMethod(name = "remove_class_variable", visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject remove_class_variable(ThreadContext context, IRubyObject name) {
         return removeClassVariable(name.asJavaString());
     }
 
     @JRubyMethod(name = "remove_class_variable", compat = RUBY1_9)
     public IRubyObject remove_class_variable19(ThreadContext context, IRubyObject name) {
         return remove_class_variable(context, name);
     }
 
     /** rb_mod_class_variables
      *
      */
     @JRubyMethod(name = "class_variables", compat = RUBY1_8)
     public RubyArray class_variables(ThreadContext context) {
         Ruby runtime = context.runtime;
         RubyArray ary = runtime.newArray();
         
         Collection<String> names = classVariablesCommon();
         ary.addAll(names);
         return ary;
     }
 
     @JRubyMethod(name = "class_variables", compat = RUBY1_9)
     public RubyArray class_variables19(ThreadContext context) {
         Ruby runtime = context.runtime;
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
         return context.runtime.newBoolean(fastIsConstantDefined(validateConstant(symbol.asJavaString()).intern()));
     }
 
     @JRubyMethod(name = "const_defined?", required = 1, optional = 1, compat = RUBY1_9)
     public RubyBoolean const_defined_p19(ThreadContext context, IRubyObject[] args) {
         IRubyObject symbol = args[0];
         boolean inherit = args.length == 1 || (!args[1].isNil() && args[1].isTrue());
 
         return context.runtime.newBoolean(fastIsConstantDefined19(validateConstant(symbol.asJavaString()).intern(), inherit));
     }
 
     /** rb_mod_const_get
      *
      */
     @JRubyMethod(name = "const_get", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject const_get(IRubyObject symbol) {
         return getConstant(validateConstant(symbol.asJavaString()));
     }
 
     @JRubyMethod(name = "const_get", required = 1, optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject const_get(ThreadContext context, IRubyObject[] args) {
         IRubyObject symbol = args[0];
         boolean inherit = args.length == 1 || (!args[1].isNil() && args[1].isTrue());
 
         // 1.9 only includes Object when inherit = true or unspecified (JRUBY-6224)
         return getConstant(validateConstant(symbol.asJavaString()), inherit, inherit);
     }
 
     /** rb_mod_const_set
      *
      */
     @JRubyMethod(name = "const_set", required = 2)
     public IRubyObject const_set(IRubyObject symbol, IRubyObject value) {
         IRubyObject constant = setConstant(validateConstant(symbol.asJavaString()).intern(), value);
 
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
             removeAutoload(name);
             // FIXME: I'm not sure this is right, but the old code returned
             // the undef, which definitely isn't right...
             return context.runtime.getNil();
         }
 
         if (hasConstantInHierarchy(name)) {
             throw cannotRemoveError(name);
         }
 
         throw context.runtime.newNameError("constant " + name + " not defined for " + getName(), name);
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
         Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
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
         Ruby runtime = context.runtime;
         RubyArray array = runtime.newArray();
         
         Collection<String> constantNames = constantsCommon(runtime, replaceModule, allConstants, false);
         
         for (String name : constantNames) {
             array.add(runtime.newSymbol(name));
         }
         return array;
     }
 
     /** rb_mod_constants
      *
      */
     public Collection<String> constantsCommon(Ruby runtime, boolean replaceModule, boolean allConstants) {
         return constantsCommon(runtime, replaceModule, allConstants, true);
     }
 
 
     public Collection<String> constantsCommon(Ruby runtime, boolean replaceModule, boolean allConstants, boolean includePrivate) {
         RubyModule objectClass = runtime.getObject();
 
         Collection<String> constantNames = new HashSet<String>();
         if (allConstants) {
             if ((replaceModule && runtime.getModule() == this) || objectClass == this) {
                 constantNames = objectClass.getConstantNames(includePrivate);
             } else {
                 Set<String> names = new HashSet<String>();
                 for (RubyModule module = this; module != null && module != objectClass; module = module.getSuperClass()) {
                     names.addAll(module.getConstantNames(includePrivate));
                 }
                 constantNames = names;
             }
         } else {
             if ((replaceModule && runtime.getModule() == this) || objectClass == this) {
                 constantNames = objectClass.getConstantNames(includePrivate);
             } else {
                 constantNames = getConstantNames(includePrivate);
             }
         }
 
         return constantNames;
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject private_constant(ThreadContext context, IRubyObject name) {
         setConstantVisibility(context, validateConstant(name.asJavaString()), true);
         invalidateConstantCache();
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_9, required = 1, rest = true)
     public IRubyObject private_constant(ThreadContext context, IRubyObject[] names) {
         for (IRubyObject name : names) {
             setConstantVisibility(context, validateConstant(name.asJavaString()), true);
         }
         invalidateConstantCache();
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject public_constant(ThreadContext context, IRubyObject name) {
         setConstantVisibility(context, validateConstant(name.asJavaString()), false);
         invalidateConstantCache();
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_9, required = 1, rest = true)
     public IRubyObject public_constant(ThreadContext context, IRubyObject[] names) {
         for (IRubyObject name : names) {
             setConstantVisibility(context, validateConstant(name.asJavaString()), false);
         }
         invalidateConstantCache();
         return this;
     }
 
     private void setConstantVisibility(ThreadContext context, String name, boolean hidden) {
         ConstantEntry entry = getConstantMap().get(name);
 
         if (entry == null) {
             throw context.runtime.newNameError("constant " + getName() + "::" + name + " not defined", name);
         }
 
         getConstantMapForWrite().put(name, new ConstantEntry(entry.value, hidden));
     }
 
     //
     ////////////////// CLASS VARIABLE API METHODS ////////////////
     //
 
     /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = this;
         do {
             if (module.hasClassVariable(name)) {
                 return module.storeClassVariable(name, value);
             }
         } while ((module = module.getSuperClass()) != null);
         
         return storeClassVariable(name, value);
     }
 
     @Deprecated
     public IRubyObject fastSetClassVar(final String internedName, final IRubyObject value) {
         return setClassVar(internedName, value);
     }
 
     /**
      * Retrieve the specified class variable, searching through this module, included modules, and supermodules.
      * 
      * Ruby C equivalent = "rb_cvar_get"
      * 
      * @param name The name of the variable to retrieve
      * @return The variable's value, or throws NameError if not found
      */
     public IRubyObject getClassVar(String name) {
         assert IdUtil.isClassVariable(name);
         Object value;
         RubyModule module = this;
 
         do {
             if ((value = module.fetchClassVariable(name)) != null) return (IRubyObject)value;
         } while ((module = module.getSuperClass()) != null);
 
         throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
     }
 
     @Deprecated
     public IRubyObject fastGetClassVar(String internedName) {
         return getClassVar(internedName);
     }
 
     /**
      * Is class var defined?
      * 
      * Ruby C equivalent = "rb_cvar_defined"
      * 
      * @param name The class var to determine "is defined?"
      * @return true if true, false if false
      */
     public boolean isClassVarDefined(String name) {
         RubyModule module = this;
         do {
             if (module.hasClassVariable(name)) return true;
         } while ((module = module.getSuperClass()) != null);
 
         return false;
     }
 
     @Deprecated
     public boolean fastIsClassVarDefined(String internedName) {
         return isClassVarDefined(internedName);
     }
     
     /** rb_mod_remove_cvar
      *
      * @deprecated - use {@link #removeClassVariable(String)}
      */
     @Deprecated
     public IRubyObject removeCvar(IRubyObject name) {
         return removeClassVariable(name.asJavaString());
     }
 
     public IRubyObject removeClassVariable(String name) {
         String javaName = validateClassVariable(name);
         IRubyObject value;
 
         if ((value = deleteClassVariable(javaName)) != null) {
             return value;
         }
 
         if (isClassVarDefined(javaName)) {
             throw cannotRemoveError(javaName);
         }
 
         throw getRuntime().newNameError("class variable " + javaName + " not defined for " + getName(), javaName);
     }
 
 
     //
     ////////////////// CONSTANT API METHODS ////////////////
     //
 
     /**
      * This version searches superclasses if we're starting with Object. This
      * corresponds to logic in rb_const_defined_0 that recurses for Object only.
      *
      * @param name the constant name to find
      * @return the constant, or null if it was not found
      */
     public IRubyObject getConstantAtSpecial(String name) {
         IRubyObject value;
         if (this == getRuntime().getObject()) {
             value = getConstantNoConstMissing(name);
         } else {
             value = fetchConstant(name);
         }
         
         return value == UNDEF ? resolveUndefConstant(getRuntime(), name) : value;
     }
 
     public IRubyObject getConstantAt(String name) {
         return getConstantAt(name, true);
     }
     
     public IRubyObject getConstantAt(String name, boolean includePrivate) {
         IRubyObject value = fetchConstant(name, includePrivate);
 
         return value == UNDEF ? resolveUndefConstant(getRuntime(), name) : value;
     }
 
     @Deprecated
     public IRubyObject fastGetConstantAt(String internedName) {
         return getConstantAt(internedName);
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return getConstant(name, true);
     }
 
     public IRubyObject getConstant(String name, boolean inherit) {
         return getConstant(name, inherit, true);
     }
 
     public IRubyObject getConstant(String name, boolean inherit, boolean includeObject) {
         IRubyObject value = getConstantNoConstMissing(name, inherit, includeObject);
         Ruby runtime = getRuntime();
 
         return value == null ? callMethod(runtime.getCurrentContext(), "const_missing",
                 runtime.newSymbol(name)) : value;
     }
 
     @Deprecated
     public IRubyObject fastGetConstant(String internedName) {
         return getConstant(internedName);
     }
 
     @Deprecated
     public IRubyObject fastGetConstant(String internedName, boolean inherit) {
         return getConstant(internedName, inherit);
     }
 
     public IRubyObject getConstantNoConstMissing(String name) {
         return getConstantNoConstMissing(name, true);
     }
 
     public IRubyObject getConstantNoConstMissing(String name, boolean inherit) {
         return getConstantNoConstMissing(name, inherit, true);
     }
 
     public IRubyObject getConstantNoConstMissing(String name, boolean inherit, boolean includeObject) {
         assert IdUtil.isConstant(name);
 
         IRubyObject constant = iterateConstantNoConstMissing(name, this, inherit);
 
         if (constant == null && !isClass() && includeObject) {
             constant = iterateConstantNoConstMissing(name, getRuntime().getObject(), inherit);
         }
 
         return constant;
     }
 
     private IRubyObject iterateConstantNoConstMissing(String name, RubyModule init, boolean inherit) {
         for (RubyModule p = init; p != null; p = p.getSuperClass()) {
             IRubyObject value = p.getConstantAt(name);
 
             if (value != null) return value == UNDEF ? null : value;
             if (!inherit) break;
         }
         return null;
     }
 
     // not actually called anywhere (all known uses call the fast version)
     public IRubyObject getConstantFrom(String name) {
         IRubyObject value = getConstantFromNoConstMissing(name);
 
         return value != null ? value : getConstantFromConstMissing(name);
     }
     
     @Deprecated
     public IRubyObject fastGetConstantFrom(String internedName) {
         return getConstantFrom(internedName);
     }
 
     public IRubyObject getConstantFromNoConstMissing(String name) {
         return getConstantFromNoConstMissing(name, true);
     }
 
     public IRubyObject getConstantFromNoConstMissing(String name, boolean includePrivate) {
         assert name == name.intern() : name + " is not interned";
         assert IdUtil.isConstant(name);
         Ruby runtime = getRuntime();
         RubyClass objectClass = runtime.getObject();
         IRubyObject value;
 
         RubyModule p = this;
 
         while (p != null) {
             if ((value = p.fetchConstant(name, false)) != null) {
                 if (value == UNDEF) {
                     return p.resolveUndefConstant(runtime, name);
                 }
 
                 if (p == objectClass && this != objectClass) {
                     String badCName = getName() + "::" + name;
                     runtime.getWarnings().warn(ID.CONSTANT_BAD_REFERENCE, "toplevel constant " +
                             name + " referenced by " + badCName);
                 }
 
                 return value;
             }
             p = p.getSuperClass();
         }
         return null;
     }
     
     @Deprecated
     public IRubyObject fastGetConstantFromNoConstMissing(String internedName) {
         return getConstantFromNoConstMissing(internedName);
     }
 
     public IRubyObject getConstantFromConstMissing(String name) {
         return callMethod(getRuntime().getCurrentContext(),
                 "const_missing", getRuntime().fastNewSymbol(name));
     }
     
     @Deprecated
     public IRubyObject fastGetConstantFromConstMissing(String internedName) {
         return getConstantFromConstMissing(internedName);
     }
     
     public IRubyObject resolveUndefConstant(Ruby runtime, String name) {
         return getAutoloadConstant(runtime, name);
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name. This version does not
      * warn if the constant has already been set.
      *
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      */
     public IRubyObject setConstantQuiet(String name, IRubyObject value) {
         return setConstantCommon(name, value, false);
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      *
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      */
     public IRubyObject setConstant(String name, IRubyObject value) {
         return setConstantCommon(name, value, true);
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      *
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      */
     private IRubyObject setConstantCommon(String name, IRubyObject value, boolean warn) {
         IRubyObject oldValue = fetchConstant(name);
         if (oldValue != null) {
             if (oldValue == UNDEF) {
                 setAutoloadConstant(name, value);
             } else {
                 if (warn) {
                     getRuntime().getWarnings().warn(ID.CONSTANT_ALREADY_INITIALIZED, "already initialized constant " + name);
                 }
                 storeConstant(name, value);
             }
         } else {
             storeConstant(name, value);
         }
 
         invalidateConstantCache();
         
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module != this && module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
         }
         return value;
     }
 
     @Deprecated
     public IRubyObject fastSetConstant(String internedName, IRubyObject value) {
         return setConstant(internedName, value);
     }
     
     /** rb_define_const
      *
      */
     public void defineConstant(String name, IRubyObject value) {
         assert value != null;
 
         if (!IdUtil.isValidConstantName(name)) {
             throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     // Fix for JRUBY-1339 - search hierarchy for constant
     /** rb_const_defined_at
      * 
      */
     public boolean isConstantDefined(String name) {
         assert IdUtil.isConstant(name);
         boolean isObject = this == getRuntime().getObject();
 
         RubyModule module = this;
 
         do {
             Object value;
             if ((value = module.constantTableFetch(name)) != null) {
                 if (value != UNDEF) return true;
                 return getAutoloadMap().get(name) != null;
             }
 
         } while (isObject && (module = module.getSuperClass()) != null );
 
         return false;
     }
 
     public boolean fastIsConstantDefined(String internedName) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
         boolean isObject = this == getRuntime().getObject();
 
         RubyModule module = this;
 
         do {
             Object value;
             if ((value = module.constantTableFetch(internedName)) != null) {
                 if (value != UNDEF) return true;
                 return getAutoloadMap().get(internedName) != null;
             }
 
         } while (isObject && (module = module.getSuperClass()) != null );
 
         return false;
     }
 
     public boolean fastIsConstantDefined19(String internedName) {
         return fastIsConstantDefined19(internedName, true);
     }
 
     public boolean fastIsConstantDefined19(String internedName, boolean inherit) {
         assert internedName == internedName.intern() : internedName + " is not interned";
         assert IdUtil.isConstant(internedName);
 
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             Object value;
             if ((value = module.constantTableFetch(internedName)) != null) {
                 if (value != UNDEF) return true;
                 return getAutoloadMap().get(internedName) != null;
             }
             if (!inherit) {
                 break;
             }
         }
 
         return false;
     }
 
     //
     ////////////////// COMMON CONSTANT / CVAR METHODS ////////////////
     //
 
     private RaiseException cannotRemoveError(String id) {
         return getRuntime().newNameError("cannot remove " + id + " for " + getName(), id);
     }
 
 
     //
     ////////////////// INTERNAL MODULE VARIABLE API METHODS ////////////////
     //
     
     /**
      * Behaves similarly to {@link #getClassVar(String)}. Searches this
      * class/module <em>and its ancestors</em> for the specified internal
      * variable.
      * 
      * @param name the internal variable name
      * @return the value of the specified internal variable if found, else null
      * @see #setInternalModuleVariable(String, IRubyObject)
      */
     public boolean hasInternalModuleVariable(final String name) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.hasInternalVariable(name)) return true;
         }
 
         return false;
     }
     /**
      * Behaves similarly to {@link #getClassVar(String)}. Searches this
      * class/module <em>and its ancestors</em> for the specified internal
      * variable.
      * 
      * @param name the internal variable name
      * @return the value of the specified internal variable if found, else null
      * @see #setInternalModuleVariable(String, IRubyObject)
      */
     public IRubyObject searchInternalModuleVariable(final String name) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             IRubyObject value = (IRubyObject)module.getInternalVariable(name);
             if (value != null) return value;
         }
 
         return null;
     }
 
     /**
      * Behaves similarly to {@link #setClassVar(String, IRubyObject)}. If the
      * specified internal variable is found in this class/module <em>or an ancestor</em>,
      * it is set where found.  Otherwise it is set in this module. 
      * 
      * @param name the internal variable name
      * @param value the internal variable value
      * @see #searchInternalModuleVariable(String)
      */
     public void setInternalModuleVariable(final String name, final IRubyObject value) {
         for (RubyModule module = this; module != null; module = module.getSuperClass()) {
             if (module.hasInternalVariable(name)) {
                 module.setInternalVariable(name, value);
                 return;
             }
         }
 
         setInternalVariable(name, value);
     }
 
     //
     ////////////////// LOW-LEVEL CLASS VARIABLE INTERFACE ////////////////
     //
     // fetch/store/list class variables for this module
     //
 
     protected Map<String, IRubyObject> getClassVariables() {
         if (CLASSVARS_UPDATER == null) {
             return getClassVariablesForWriteSynchronized();
         } else {
             return getClassVariablesForWriteAtomic();
         }
     }
 
     /**
      * Get the class variables for write. If it is not set or not of the right size,
      * synchronize against the object and prepare it accordingly.
      *
      * @return the class vars map, ready for assignment
      */
     private Map<String,IRubyObject> getClassVariablesForWriteSynchronized() {
         Map myClassVars = classVariables;
         if (myClassVars == Collections.EMPTY_MAP) {
             synchronized (this) {
                 myClassVars = classVariables;
 
                 if (myClassVars == Collections.EMPTY_MAP) {
                     return classVariables = new ConcurrentHashMap<String, IRubyObject>(4, 0.75f, 2);
                 } else {
                     return myClassVars;
                 }
             }
         }
 
         return myClassVars;
     }
 
 
     /**
      * Get the class variables for write. If it is not set or not of the right size,
      * atomically update it with an appropriate value.
      *
      * @return the class vars map, ready for assignment
      */
     private Map<String,IRubyObject> getClassVariablesForWriteAtomic() {
         while (true) {
             Map myClassVars = classVariables;
             Map newClassVars;
 
             if (myClassVars == Collections.EMPTY_MAP) {
                 newClassVars = new ConcurrentHashMap<String, IRubyObject>(4, 0.75f, 2);
             } else {
                 return myClassVars;
             }
 
             // proceed with atomic update of table, or retry
             if (CLASSVARS_UPDATER.compareAndSet(this, myClassVars, newClassVars)) {
                 return newClassVars;
             }
         }
     }
 
     protected Map<String, IRubyObject> getClassVariablesForRead() {
         return classVariables;
     }
     
     public boolean hasClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         return getClassVariablesForRead().containsKey(name);
     }
 
     @Deprecated
     public boolean fastHasClassVariable(String internedName) {
         return hasClassVariable(internedName);
     }
 
     public IRubyObject fetchClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         return getClassVariablesForRead().get(name);
     }
 
     @Deprecated
     public IRubyObject fastFetchClassVariable(String internedName) {
         return fetchClassVariable(internedName);
     }
 
     public IRubyObject storeClassVariable(String name, IRubyObject value) {
         assert IdUtil.isClassVariable(name) && value != null;
         ensureClassVariablesSettable();
         getClassVariables().put(name, value);
         return value;
     }
 
     @Deprecated
     public IRubyObject fastStoreClassVariable(String internedName, IRubyObject value) {
         return storeClassVariable(internedName, value);
     }
 
     public IRubyObject deleteClassVariable(String name) {
         assert IdUtil.isClassVariable(name);
         ensureClassVariablesSettable();
         return getClassVariablesForRead().remove(name);
     }
 
     public List<String> getClassVariableNameList() {
         return new ArrayList<String>(getClassVariablesForRead().keySet());
     }
 
     protected static final String ERR_INSECURE_SET_CLASS_VAR = "Insecure: can't modify class variable";
     protected static final String ERR_FROZEN_CVAR_TYPE = "class/module ";
    
     protected final String validateClassVariable(String name) {
         if (IdUtil.isValidClassVariableName(name)) {
             return name;
         }
         throw getRuntime().newNameError("`" + name + "' is not allowed as a class variable name", name);
     }
 
     protected final void ensureClassVariablesSettable() {
         Ruby runtime = getRuntime();
         
         if (!isFrozen()) {
             return;
         }
 
         if (this instanceof RubyModule) {
             throw runtime.newFrozenError(ERR_FROZEN_CONST_TYPE);
         } else {
             throw runtime.newFrozenError("");
         }
     }
 
     //
     ////////////////// LOW-LEVEL CONSTANT INTERFACE ////////////////
     //
     // fetch/store/list constants for this module
     //
 
     public boolean hasConstant(String name) {
         assert IdUtil.isConstant(name);
         return constantTableContains(name);
     }
 
     @Deprecated
     public boolean fastHasConstant(String internedName) {
         return hasConstant(internedName);
     }
 
     // returns the stored value without processing undefs (autoloads)
     public IRubyObject fetchConstant(String name) {
         return fetchConstant(name, true);
     }
 
     public IRubyObject fetchConstant(String name, boolean includePrivate) {
         assert IdUtil.isConstant(name);
         ConstantEntry entry = constantEntryFetch(name);
 
         if (entry == null) return null;
 
         if (entry.hidden && !includePrivate) {
             throw getRuntime().newNameError("private constant " + getName() + "::" + name + " referenced", name);
         }
 
         return entry.value;
     }
 
     @Deprecated
     public IRubyObject fastFetchConstant(String internedName) {
         return fetchConstant(internedName);
     }
 
     public IRubyObject storeConstant(String name, IRubyObject value) {
         assert IdUtil.isConstant(name) && value != null;
         ensureConstantsSettable();
         return constantTableStore(name, value);
     }
 
     @Deprecated
     public IRubyObject fastStoreConstant(String internedName, IRubyObject value) {
         return storeConstant(internedName, value);
     }
 
     // removes and returns the stored value without processing undefs (autoloads)
     public IRubyObject deleteConstant(String name) {
         assert IdUtil.isConstant(name);
         ensureConstantsSettable();
         return constantTableRemove(name);
     }
     
     @Deprecated
     public List<Variable<IRubyObject>> getStoredConstantList() {
         return null;
     }
 
     @Deprecated
     public List<String> getStoredConstantNameList() {
         return new ArrayList<String>(getConstantMap().keySet());
     }
 
     /**
      * @return a list of constant names that exists at time this was called
      */
     public Collection<String> getConstantNames() {
         return getConstantMap().keySet();
     }
 
     public Collection<String> getConstantNames(boolean includePrivate) {
         if (includePrivate) return getConstantNames();
 
         if (getConstantMap().size() == 0) {
             return Collections.EMPTY_SET;
         }
 
         HashSet<String> publicNames = new HashSet<String>(getConstantMap().size());
         
         for (Map.Entry<String, ConstantEntry> entry : getConstantMap().entrySet()) {
             if (entry.getValue().hidden) continue;
             publicNames.add(entry.getKey());
         }
         return publicNames;
     }
    
     protected final String validateConstant(String name) {
         if (getRuntime().is1_9() ?
                 IdUtil.isValidConstantName19(name) :
                 IdUtil.isValidConstantName(name)) {
             return name;
         }
         throw getRuntime().newNameError("wrong constant name " + name, name);
     }
 
     protected final void ensureConstantsSettable() {
         if (isFrozen()) throw getRuntime().newFrozenError(ERR_FROZEN_CONST_TYPE);
     }
 
     protected boolean constantTableContains(String name) {
         return getConstantMap().containsKey(name);
     }
     
     protected IRubyObject constantTableFetch(String name) {
         ConstantEntry entry = getConstantMap().get(name);
         if (entry == null) return null;
         return entry.value;
     }
 
     protected ConstantEntry constantEntryFetch(String name) {
         return getConstantMap().get(name);
     }
     
     protected IRubyObject constantTableStore(String name, IRubyObject value) {
         Map<String, ConstantEntry> constMap = getConstantMapForWrite();
         boolean hidden = false;
 
         ConstantEntry entry = constMap.get(name);
         if (entry != null) hidden = entry.hidden;
 
         constMap.put(name, new ConstantEntry(value, hidden));
         return value;
     }
     
     protected IRubyObject constantTableRemove(String name) {
         ConstantEntry entry = getConstantMapForWrite().remove(name);
         if (entry == null) return null;
         return entry.value;
     }
     
     /**
      * Define an autoload. ConstantMap holds UNDEF for the name as an autoload marker.
      */
     protected void defineAutoload(String name, IAutoloadMethod loadMethod) {
-        storeConstant(name, RubyObject.UNDEF);
-        getAutoloadMapForWrite().put(name, new Autoload(loadMethod));
+        Autoload existingAutoload = getAutoloadMap().get(name);
+        if (existingAutoload == null || existingAutoload.getValue() == null) {
+            storeConstant(name, RubyObject.UNDEF);
+            getAutoloadMapForWrite().put(name, new Autoload(loadMethod));
+        }
     }
     
     /**
      * Extract an Object which is defined by autoload thread from autoloadMap and define it as a constant.
      */
     protected IRubyObject finishAutoload(String name) {
         Autoload autoload = getAutoloadMap().get(name);
         if (autoload != null) {
             IRubyObject value = autoload.getValue();
             if (value != null) {
                 storeConstant(name, value);
             }
             removeAutoload(name);
             return value;
         }
         return null;
     }
     
     /**
      * Get autoload constant.
      * If it's first resolution for the constant, it tries to require the defined feature and returns the defined value.
      * Multi-threaded accesses are blocked and processed sequentially except if the caller is the autoloading thread.
      */
     public IRubyObject getAutoloadConstant(Ruby runtime, String name) {
         Autoload autoload = getAutoloadMap().get(name);
         if (autoload == null) {
             return null;
         }
         return autoload.getConstant(runtime.getCurrentContext());
     }
     
     /**
      * Set an Object as a defined constant in autoloading.
      */
     private void setAutoloadConstant(String name, IRubyObject value) {
         Autoload autoload = getAutoloadMap().get(name);
         if (autoload != null) {
             if (!autoload.setConstant(getRuntime().getCurrentContext(), value)) {
                 storeConstant(name, value);
                 removeAutoload(name);
             }
         } else {
             storeConstant(name, value);
         }
     }
     
     /**
      * Removes an Autoload object from autoloadMap. ConstantMap must be updated before calling this.
      */
     private void removeAutoload(String name) {
         getAutoloadMapForWrite().remove(name);
     }
     
     protected String getAutoloadFile(String name) {
         Autoload autoload = getAutoloadMap().get(name);
         if (autoload != null) {
             return autoload.getFile();
         }
         return null;
     }
 
     private static void define(RubyModule module, JavaMethodDescriptor desc, DynamicMethod dynamicMethod) {
         JRubyMethod jrubyMethod = desc.anno;
         if (jrubyMethod.frame()) {
             for (String name : jrubyMethod.name()) {
                 ASTInspector.FRAME_AWARE_METHODS.add(name);
             }
         }
         if(jrubyMethod.compat() == BOTH ||
                 module.getRuntime().getInstanceConfig().getCompatVersion() == jrubyMethod.compat()) {
             RubyModule singletonClass;
 
             if (jrubyMethod.meta()) {
                 singletonClass = module.getSingletonClass();
                 dynamicMethod.setImplementationClass(singletonClass);
 
                 String baseName;
                 if (jrubyMethod.name().length == 0) {
                     baseName = desc.name;
                     singletonClass.addMethod(baseName, dynamicMethod);
                 } else {
                     baseName = jrubyMethod.name()[0];
                     for (String name : jrubyMethod.name()) {
                         singletonClass.addMethod(name, dynamicMethod);
                     }
                 }
 
                 if (jrubyMethod.alias().length > 0) {
                     for (String alias : jrubyMethod.alias()) {
                         singletonClass.defineAlias(alias, baseName);
                     }
                 }
             } else {
                 String baseName;
                 if (jrubyMethod.name().length == 0) {
                     baseName = desc.name;
                     module.addMethod(baseName, dynamicMethod);
                 } else {
                     baseName = jrubyMethod.name()[0];
                     for (String name : jrubyMethod.name()) {
                         module.addMethod(name, dynamicMethod);
                     }
                 }
 
                 if (jrubyMethod.alias().length > 0) {
                     for (String alias : jrubyMethod.alias()) {
                         module.defineAlias(alias, baseName);
                     }
                 }
 
                 if (jrubyMethod.module()) {
                     singletonClass = module.getSingletonClass();
                     // module/singleton methods are all defined public
                     DynamicMethod moduleMethod = dynamicMethod.dup();
                     moduleMethod.setVisibility(PUBLIC);
 
                     if (jrubyMethod.name().length == 0) {
                         baseName = desc.name;
                         singletonClass.addMethod(desc.name, moduleMethod);
                     } else {
                         baseName = jrubyMethod.name()[0];
                         for (String name : jrubyMethod.name()) {
                             singletonClass.addMethod(name, moduleMethod);
                         }
                     }
 
                     if (jrubyMethod.alias().length > 0) {
                         for (String alias : jrubyMethod.alias()) {
                             singletonClass.defineAlias(alias, baseName);
                         }
                     }
                 }
             }
         }
     }
     
     @Deprecated
     public IRubyObject initialize(Block block) {
         return initialize(getRuntime().getCurrentContext());
     }
 
     public KindOf kindOf = KindOf.DEFAULT_KIND_OF;
 
     public final int id;
 
     /**
      * The class/module within whose namespace this class/module resides.
      */
     public RubyModule parent;
 
     /**
      * The base name of this class/module, excluding nesting. If null, this is
      * an anonymous class.
      */
     protected String baseName;
     
     /**
      * The cached anonymous class name, since it never changes and has a nonzero
      * cost to calculate.
      */
     private String anonymousName;
 
     /**
      * The cached name, only cached once this class and all containing classes are non-anonymous
      */
     private String cachedName;
 
     private volatile Map<String, ConstantEntry> constants = Collections.EMPTY_MAP;
 
     /**
      * Represents a constant value, possibly hidden (private).
      */
     public static class ConstantEntry {
         public final IRubyObject value;
         public final boolean hidden;
 
         public ConstantEntry(IRubyObject value, boolean hidden) {
             this.value = value;
             this.hidden = hidden;
         }
         
         public ConstantEntry dup() {
             return new ConstantEntry(value, hidden);
         }
     }
     
     /**
      * Objects for holding autoload state for the defined constant.
      * 
      * 'Module#autoload' creates this object and stores it in autoloadMap.
      * This object can be shared with multiple threads so take care to change volatile and synchronized definitions.
      */
     private class Autoload {
         // A ThreadContext which is executing autoload.
         private volatile ThreadContext ctx;
         // The lock for test-and-set the ctx.
         private final Object ctxLock = new Object();
         // An object defined for the constant while autoloading.
         private volatile IRubyObject value;
         // A method which actually requires a defined feature.
         private final IAutoloadMethod loadMethod;
 
         Autoload(IAutoloadMethod loadMethod) {
             this.ctx = null;
             this.value = null;
             this.loadMethod = loadMethod;
         }
 
         // Returns an object for the constant if the caller is the autoloading thread.
         // Otherwise, try to start autoloading and returns the defined object by autoload.
         IRubyObject getConstant(ThreadContext ctx) {
             synchronized (ctxLock) {
                 if (this.ctx == null) {
                     this.ctx = ctx;
                 } else if (isSelf(ctx)) {
                     return getValue();
                 }
                 // This method needs to be synchronized for removing Autoload
                 // from autoloadMap when it's loaded. 
                 getLoadMethod().load(ctx.runtime);
             }
             return getValue();
         }
         
         // Update an object for the constant if the caller is the autoloading thread.
         boolean setConstant(ThreadContext ctx, IRubyObject value) {
             synchronized(ctxLock) {
                 if (this.ctx == null) {
                     return false;
                 } else if (isSelf(ctx)) {
                     this.value = value;
                     return true;
                 }
                 return false;
             }
         }
         
         // Returns an object for the constant defined by autoload.
         IRubyObject getValue() {
             return value;
         }
         
         // Returns the assigned feature.
         String getFile() {
             return getLoadMethod().file();
         }
 
         private IAutoloadMethod getLoadMethod() {
             return loadMethod;
         }
 
         private boolean isSelf(ThreadContext rhs) {
             return ctx != null && ctx.getThread() == rhs.getThread();
         }
     }
     
     /**
      * Set whether this class is associated with (i.e. a proxy for) a normal
      * Java class or interface.
      */
     public void setJavaProxy(boolean javaProxy) {
         this.javaProxy = javaProxy;
     }
     
     /**
      * Get whether this class is associated with (i.e. a proxy for) a normal
      * Java class or interface.
      */
     public boolean getJavaProxy() {
         return javaProxy;
     }
 
     /**
      * Get whether this Java proxy class should try to keep its instances idempotent
      * and alive using the ObjectProxyCache.
      */
     public boolean getCacheProxy() {
         return getFlag(USER0_F);
     }
 
     /**
      * Set whether this Java proxy class should try to keep its instances idempotent
      * and alive using the ObjectProxyCache.
      */
     public void setCacheProxy(boolean cacheProxy) {
         setFlag(USER0_F, cacheProxy);
     }
     
     private volatile Map<String, Autoload> autoloads = Collections.EMPTY_MAP;
     private volatile Map<String, DynamicMethod> methods = Collections.EMPTY_MAP;
     protected Map<String, CacheEntry> cachedMethods = Collections.EMPTY_MAP;
     protected int generation;
     protected Integer generationObject;
 
     protected volatile Set<RubyClass> includingHierarchies = Collections.EMPTY_SET;
 
     // ClassProviders return Java class/module (in #defineOrGetClassUnder and
     // #defineOrGetModuleUnder) when class/module is opened using colon syntax.
     private transient volatile Set<ClassProvider> classProviders = Collections.EMPTY_SET;
 
     // superClass may be null.
     protected RubyClass superClass;
 
     public int index;
 
     private volatile Map<String, IRubyObject> classVariables = Collections.EMPTY_MAP;
 
     private static final AtomicReferenceFieldUpdater CLASSVARS_UPDATER;
 
     static {
         AtomicReferenceFieldUpdater updater = null;
         try {
             updater = AtomicReferenceFieldUpdater.newUpdater(RubyModule.class, Map.class, "classVariables");
         } catch (RuntimeException re) {
             if (re.getCause() instanceof AccessControlException) {
                 // security prevented creation; fall back on synchronized assignment
             } else {
                 throw re;
             }
         }
         CLASSVARS_UPDATER = updater;
     }
     
     // Invalidator used for method caches
     protected final Invalidator methodInvalidator;
     
     /** Whether this class proxies a normal Java class */
     private boolean javaProxy = false;
 }
