diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index a260d3cece..7173c16045 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -66,1497 +66,1520 @@ import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.ClassProvider;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodNoBlock;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodZero;
 import org.jruby.java.MiniJava;
 import org.jruby.java.addons.ArrayJavaAddons;
 import org.jruby.java.addons.StringJavaAddons;
 import org.jruby.java.proxies.ArrayJavaProxy;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.java.proxies.InterfaceJavaProxy;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.runtime.callback.Callback;
 
 @JRubyModule(name = "Java")
 public class Java implements Library {
 
     public void load(Ruby runtime, boolean wrap) throws IOException {
         createJavaModule(runtime);
         runtime.getLoadService().smartLoad("builtin/javasupport");
         RubyClassPathVariable.createClassPathVariable(runtime);
     }
 
     public static RubyModule createJavaModule(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         RubyModule javaModule = runtime.defineModule("Java");
         
         javaModule.defineAnnotatedMethods(Java.class);
 
         JavaObject.createJavaObjectClass(runtime, javaModule);
         JavaArray.createJavaArrayClass(runtime, javaModule);
         JavaClass.createJavaClassClass(runtime, javaModule);
         JavaMethod.createJavaMethodClass(runtime, javaModule);
         JavaConstructor.createJavaConstructorClass(runtime, javaModule);
         JavaField.createJavaFieldClass(runtime, javaModule);
         
         // set of utility methods for Java-based proxy objects
         JavaProxyMethods.createJavaProxyMethods(context);
         
         // the proxy (wrapper) type hierarchy
         JavaProxy.createJavaProxy(context);
         ConcreteJavaProxy.createConcreteJavaProxy(context);
         //InterfaceJavaProxy.createInterfaceJavaProxy(context);
         ArrayJavaProxy.createArrayJavaProxy(context);
 
         // also create the JavaProxy* classes
         JavaProxyClass.createJavaProxyModule(runtime);
 
         // The template for interface modules
         JavaInterfaceTemplate.createJavaInterfaceTemplateModule(context);
 
         RubyModule javaUtils = runtime.defineModule("JavaUtilities");
         
         javaUtils.defineAnnotatedMethods(JavaUtilities.class);
 
         runtime.getJavaSupport().setConcreteProxyCallback(new Callback() {
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                 Arity.checkArgumentCount(recv.getRuntime(), args, 1, 1);
                 
                 return Java.concrete_proxy_inherited(recv, args[0]);
             }
 
             public Arity getArity() {
                 return Arity.ONE_ARGUMENT;
             }
         });
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
 
         RubyClass javaProxy = runtime.defineClass("JavaProxy", runtime.getObject(), runtime.getObject().getAllocator());
         javaProxy.defineAnnotatedMethods(JavaProxy.class);
         
         // Now attach Java-related extras to core classes
         runtime.getArray().defineAnnotatedMethods(ArrayJavaAddons.class);
         runtime.getKernel().defineAnnotatedMethods(KernelJavaAddons.class);
         runtime.getString().defineAnnotatedMethods(StringJavaAddons.class);
         
         // add all name-to-class mappings
         addNameClassMappings(runtime, runtime.getJavaSupport().getNameClassMap());
         
         // add some base Java classes everyone will need
         runtime.getJavaSupport().setObjectJavaClass(JavaClass.get(runtime, Object.class));
         
         // finally, set JavaSupport.isEnabled to true
         runtime.getJavaSupport().setActive(true);
 
         return javaModule;
     }
     
     /**
      * This populates the master map from short-cut names to JavaClass instances for
      * a number of core Java types.
      * 
      * @param runtime
      * @param nameClassMap
      */
     private static void addNameClassMappings(Ruby runtime, Map<String, JavaClass> nameClassMap) {
         JavaClass booleanPrimClass = JavaClass.get(runtime, Boolean.TYPE);
         JavaClass booleanClass = JavaClass.get(runtime, Boolean.class);
         nameClassMap.put("boolean", booleanPrimClass);
         nameClassMap.put("Boolean", booleanClass);
         nameClassMap.put("java.lang.Boolean", booleanClass);
         
         JavaClass bytePrimClass = JavaClass.get(runtime, Byte.TYPE);
         JavaClass byteClass = JavaClass.get(runtime, Byte.class);
         nameClassMap.put("byte", bytePrimClass);
         nameClassMap.put("Byte", byteClass);
         nameClassMap.put("java.lang.Byte", byteClass);
         
         JavaClass shortPrimClass = JavaClass.get(runtime, Short.TYPE);
         JavaClass shortClass = JavaClass.get(runtime, Short.class);
         nameClassMap.put("short", shortPrimClass);
         nameClassMap.put("Short", shortClass);
         nameClassMap.put("java.lang.Short", shortClass);
         
         JavaClass charPrimClass = JavaClass.get(runtime, Character.TYPE);
         JavaClass charClass = JavaClass.get(runtime, Character.class);
         nameClassMap.put("char", charPrimClass);
         nameClassMap.put("Character", charClass);
         nameClassMap.put("Char", charClass);
         nameClassMap.put("java.lang.Character", charClass);
         
         JavaClass intPrimClass = JavaClass.get(runtime, Integer.TYPE);
         JavaClass intClass = JavaClass.get(runtime, Integer.class);
         nameClassMap.put("int", intPrimClass);
         nameClassMap.put("Integer", intClass);
         nameClassMap.put("Int", intClass);
         nameClassMap.put("java.lang.Integer", intClass);
         
         JavaClass longPrimClass = JavaClass.get(runtime, Long.TYPE);
         JavaClass longClass = JavaClass.get(runtime, Long.class);
         nameClassMap.put("long", longPrimClass);
         nameClassMap.put("Long", longClass);
         nameClassMap.put("java.lang.Long", longClass);
         
         JavaClass floatPrimClass = JavaClass.get(runtime, Float.TYPE);
         JavaClass floatClass = JavaClass.get(runtime, Float.class);
         nameClassMap.put("float", floatPrimClass);
         nameClassMap.put("Float", floatClass);
         nameClassMap.put("java.lang.Float", floatClass);
         
         JavaClass doublePrimClass = JavaClass.get(runtime, Double.TYPE);
         JavaClass doubleClass = JavaClass.get(runtime, Double.class);
         nameClassMap.put("double", doublePrimClass);
         nameClassMap.put("Double", doubleClass);
         nameClassMap.put("java.lang.Double", doubleClass);
         
         JavaClass bigintClass = JavaClass.get(runtime, BigInteger.class);
         nameClassMap.put("big_int", bigintClass);
         nameClassMap.put("big_integer", bigintClass);
         nameClassMap.put("BigInteger", bigintClass);
         nameClassMap.put("java.math.BigInteger", bigintClass);
         
         JavaClass bigdecimalClass = JavaClass.get(runtime, BigDecimal.class);
         nameClassMap.put("big_decimal", bigdecimalClass);
         nameClassMap.put("BigDecimal", bigdecimalClass);
         nameClassMap.put("java.math.BigDecimal", bigdecimalClass);
         
         JavaClass objectClass = JavaClass.get(runtime, Object.class);
         nameClassMap.put("object", objectClass);
         nameClassMap.put("Object", objectClass);
         nameClassMap.put("java.lang.Object", objectClass);
         
         JavaClass stringClass = JavaClass.get(runtime, String.class);
         nameClassMap.put("string", stringClass);
         nameClassMap.put("String", stringClass);
         nameClassMap.put("java.lang.String", stringClass);
     }
 
     @JRubyModule(name = "JavaUtilities")
     public static class JavaUtilities {
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject wrap(IRubyObject recv, IRubyObject arg0) {
             return Java.wrap(recv, arg0);
         }
         
         @JRubyMethod(name = "valid_constant_name?", module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject valid_constant_name_p(IRubyObject recv, IRubyObject arg0) {
             return Java.valid_constant_name_p(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject primitive_match(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.primitive_match(recv, arg0, arg1);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject access(IRubyObject recv, IRubyObject arg0) {
             return Java.access(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject matching_method(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.matching_method(recv, arg0, arg1);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject set_java_object(IRubyObject recv, IRubyObject self, IRubyObject java_object) {
             self.getInstanceVariables().fastSetInstanceVariable("@java_object", java_object);
             self.dataWrapStruct(java_object);
             return java_object;
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_deprecated_interface_proxy(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
             return Java.get_deprecated_interface_proxy(context, recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject arg0) {
             return Java.get_interface_module(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_package_module(IRubyObject recv, IRubyObject arg0) {
             return Java.get_package_module(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject arg0) {
             return Java.get_package_module_dot_format(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject arg0) {
             return Java.get_proxy_class(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject is_primitive_type(IRubyObject recv, IRubyObject arg0) {
             return Java.is_primitive_type(recv, arg0);
         }
 
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject create_proxy_class(IRubyObject recv, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             return Java.create_proxy_class(recv, arg0, arg1, arg2);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_java_class(IRubyObject recv, IRubyObject arg0) {
             return Java.get_java_class(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_top_level_proxy_or_package(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
             return Java.get_top_level_proxy_or_package(context, recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_proxy_or_package_under_package(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.get_proxy_or_package_under_package(context, recv, arg0, arg1);
         }
         
         @Deprecated
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject add_proxy_extender(IRubyObject recv, IRubyObject arg0) {
             return Java.add_proxy_extender(recv, arg0);
         }
     }
 
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new ClassProvider() {
 
         public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyClass) get_proxy_class(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
 
         public RubyModule defineModuleUnder(RubyModule pkg, String name) {
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyModule) get_interface_module(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
     };
     private static final Map<String, Boolean> JAVA_PRIMITIVES = new HashMap<String, Boolean>();
     
 
     static {
         String[] primitives = {"boolean", "byte", "char", "short", "int", "long", "float", "double"};
         for (String primitive : primitives) {
             JAVA_PRIMITIVES.put(primitive, Boolean.TRUE);
         }
     }
 
     public static IRubyObject is_primitive_type(IRubyObject recv, IRubyObject sym) {
         return recv.getRuntime().newBoolean(JAVA_PRIMITIVES.containsKey(sym.asJavaString()));
     }
 
     public static IRubyObject create_proxy_class(
             IRubyObject recv,
             IRubyObject constant,
             IRubyObject javaClass,
             IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             throw recv.getRuntime().newTypeError(module, recv.getRuntime().getModule());
         }
         return ((RubyModule) module).const_set(constant, get_proxy_class(recv, javaClass));
     }
 
     public static IRubyObject get_java_class(IRubyObject recv, IRubyObject name) {
         try {
             return JavaClass.for_name(recv, name);
         } catch (Exception e) {
             return recv.getRuntime().getNil();
         }
     }
 
     /**
      * Returns a new proxy instance of type (RubyClass)recv for the wrapped java_object,
      * or the cached proxy if we've already seen this object.
      * 
      * @param recv the class for this object
      * @param java_object the java object wrapped in a JavaObject wrapper
      * @return the new or cached proxy for the specified Java object
      */
     public static IRubyObject new_instance_for(IRubyObject recv, IRubyObject java_object) {
         // FIXME: note temporary double-allocation of JavaObject as we move to cleaner interface
         if (java_object instanceof JavaObject) {
             return getInstance(((JavaObject) java_object).getValue(), (RubyClass) recv);
         }
         // in theory we should never get here, keeping around temporarily
         IRubyObject new_instance = ((RubyClass) recv).allocate();
         new_instance.getInstanceVariables().fastSetInstanceVariable("@java_object", java_object);
         new_instance.dataWrapStruct(java_object);
         return new_instance;
     }
 
     /**
      * Returns a new proxy instance of type clazz for rawJavaObject, or the cached
      * proxy if we've already seen this object.
      * 
      * @param rawJavaObject
      * @param clazz
      * @return the new or cached proxy for the specified Java object
      */
     public static IRubyObject getInstance(Object rawJavaObject, RubyClass clazz) {
         return clazz.getRuntime().getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject, clazz);
     }
 
     /**
      * Returns a new proxy instance of a type corresponding to rawJavaObject's class,
      * or the cached proxy if we've already seen this object.  Note that primitives
      * and strings are <em>not</em> coerced to corresponding Ruby types; use
      * JavaUtil.convertJavaToUsableRubyObject to get coerced types or proxies as
      * appropriate.
      * 
      * @param runtime
      * @param rawJavaObject
      * @return the new or cached proxy for the specified Java object
      * @see JavaUtil.convertJavaToUsableRubyObject
      */
     public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject) {
         if (rawJavaObject != null) {
             return runtime.getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject,
                     (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, rawJavaObject.getClass())));
         }
         return runtime.getNil();
     }
 
     // If the proxy class itself is passed as a parameter this will be called by Java#ruby_to_java    
     public static IRubyObject to_java_object(IRubyObject recv) {
         return recv.getInstanceVariables().fastGetInstanceVariable("@java_class");
     }
 
     // JavaUtilities
     /**
      * Add a new proxy extender. This is used by JavaUtilities to allow adding methods
      * to a given type's proxy and all types descending from that proxy's Java class.
      */
     @Deprecated
     public static IRubyObject add_proxy_extender(IRubyObject recv, IRubyObject extender) {
         // hacky workaround in case any users call this directly.
         // most will have called JavaUtilities.extend_proxy instead.
         recv.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "JavaUtilities.add_proxy_extender is deprecated - use JavaUtilities.extend_proxy instead", "add_proxy_extender", "JavaUtilities.extend_proxy");
         IRubyObject javaClassVar = extender.getInstanceVariables().fastGetInstanceVariable("@java_class");
         if (!(javaClassVar instanceof JavaClass)) {
             throw recv.getRuntime().newArgumentError("extender does not have a valid @java_class");
         }
         ((JavaClass) javaClassVar).addProxyExtender(extender);
         return recv.getRuntime().getNil();
     }
 
     public static RubyModule getInterfaceModule(Ruby runtime, JavaClass javaClass) {
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError(javaClass.toString() + " is not an interface");
         }
         RubyModule interfaceModule;
         if ((interfaceModule = javaClass.getProxyModule()) != null) {
             return interfaceModule;
         }
         javaClass.lockProxy();
         try {
             if ((interfaceModule = javaClass.getProxyModule()) == null) {
                 interfaceModule = (RubyModule) runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.fastSetInstanceVariable("@java_class", javaClass);
                 addToJavaPackageModule(interfaceModule, javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 Class<?>[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0;) {
                     JavaClass extendedClass = JavaClass.get(runtime, extended[i]);
                     RubyModule extModule = getInterfaceModule(runtime, extendedClass);
                     interfaceModule.includeModule(extModule);
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return interfaceModule;
     }
 
     public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject javaClassObject) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         return getInterfaceModule(runtime, javaClass);
     }
 
     // Note: this isn't really all that deprecated, as it is used for
     // internal purposes, at least for now. But users should be discouraged
     // from calling this directly; eventually it will go away.
     public static IRubyObject get_deprecated_interface_proxy(ThreadContext context, IRubyObject recv, IRubyObject javaClassObject) {
         Ruby runtime = context.getRuntime();
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError("expected Java interface class, got " + javaClassObject);
         }
         RubyClass proxyClass;
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if ((proxyClass = javaClass.getProxyClass()) == null) {
                 RubyModule interfaceModule = getInterfaceModule(runtime, javaClass);
                 RubyClass interfaceJavaProxy = runtime.fastGetClass("InterfaceJavaProxy");
                 proxyClass = RubyClass.newClass(runtime, interfaceJavaProxy);
                 proxyClass.setAllocator(interfaceJavaProxy.getAllocator());
                 proxyClass.makeMetaClass(interfaceJavaProxy.getMetaClass());
                 // parent.setConstant(name, proxyClass); // where the name should come from ?
                 proxyClass.inherit(interfaceJavaProxy);
                 proxyClass.callMethod(context, "java_class=", javaClass);
                 // including interface module so old-style interface "subclasses" will
                 // respond correctly to #kind_of?, etc.
                 proxyClass.includeModule(interfaceModule);
                 javaClass.setupProxy(proxyClass);
                 // add reference to interface module
                 if (proxyClass.fastGetConstantAt("Includable") == null) {
                     proxyClass.fastSetConstant("Includable", interfaceModule);
                 }
 
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static RubyModule getProxyClass(Ruby runtime, JavaClass javaClass) {
         RubyClass proxyClass;
         Class<?> c;
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         if ((c = javaClass.javaClass()).isInterface()) {
             return getInterfaceModule(runtime, javaClass);
         }
         javaClass.lockProxy();
         try {
             if ((proxyClass = javaClass.getProxyClass()) == null) {
 
                 if (c.isArray()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getArrayProxyClass(),
                             javaClass, true);
 
                 } else if (c.isPrimitive()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
 
                 } else if (c == Object.class) {
                     // java.lang.Object is added at root of java proxy classes
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
                     proxyClass.getMetaClass().defineFastMethod("inherited",
                             runtime.getJavaSupport().getConcreteProxyCallback());
                     addToJavaPackageModule(proxyClass, javaClass);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
                     proxyClass = createProxyClass(runtime,
                             (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, c.getSuperclass())),
                             javaClass, false);
 
                     // include interface modules into the proxy class
                     Class<?>[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0;) {
                         JavaClass ifc = JavaClass.get(runtime, interfaces[i]);
                         proxyClass.includeModule(getInterfaceModule(runtime, ifc));
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxyClass, javaClass);
                     }
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject java_class_object) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (java_class_object instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             javaClass = (JavaClass) java_class_object;
         } else {
             throw runtime.newTypeError(java_class_object, runtime.getJavaSupport().getJavaClassClass());
         }
         return getProxyClass(runtime, javaClass);
     }
 
     private static RubyClass createProxyClass(Ruby runtime, RubyClass baseType,
             JavaClass javaClass, boolean invokeInherited) {
 	// JRUBY-2938 the proxy class might already exist
 	RubyClass proxyClass = javaClass.getProxyClass();
 	if (proxyClass != null)
 	    return proxyClass;
 
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass) baseType;
         proxyClass = RubyClass.newClass(runtime, superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         proxyClass.setAllocator(superClass.getAllocator());
         if (invokeInherited) {
             proxyClass.inherit(superClass);
         }
         proxyClass.callMethod(runtime.getCurrentContext(), "java_class=", javaClass);
         javaClass.setupProxy(proxyClass);
         return proxyClass;
     }
 
     public static IRubyObject concrete_proxy_inherited(IRubyObject recv, IRubyObject subclass) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         JavaSupport javaSupport = runtime.getJavaSupport();
         RubyClass javaProxyClass = javaSupport.getJavaProxyClass().getMetaClass();
         RuntimeHelpers.invokeAs(tc, javaProxyClass, recv, "inherited", new IRubyObject[]{subclass},
                 org.jruby.runtime.CallType.SUPER, Block.NULL_BLOCK);
         return setupJavaSubclass(tc, subclass, recv.callMethod(tc, "java_class"));
     }
 
     private static IRubyObject setupJavaSubclass(ThreadContext context, IRubyObject subclass, IRubyObject java_class) {
         Ruby runtime = context.getRuntime();
 
         if (!(subclass instanceof RubyClass)) {
             throw runtime.newTypeError(subclass, runtime.getClassClass());
         }
         RubyClass rubySubclass = (RubyClass)subclass;
         rubySubclass.getInstanceVariables().fastSetInstanceVariable("@java_proxy_class", runtime.getNil());
 
         RubyClass subclassSingleton = rubySubclass.getSingletonClass();
         subclassSingleton.addReadWriteAttribute(context, "java_proxy_class");
         subclassSingleton.addMethod("java_interfaces", new JavaMethodZero(subclassSingleton, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 IRubyObject javaInterfaces = self.getInstanceVariables().fastGetInstanceVariable("@java_interfaces");
                 if (javaInterfaces != null) return javaInterfaces.dup();
                 return context.getRuntime().getNil();
             }
         });
 
         rubySubclass.addMethod("__jcreate!", new JavaMethodNoBlock(subclassSingleton, Visibility.PUBLIC) {
             private final Map<Integer, ParameterTypes> methodCache = new HashMap<Integer, ParameterTypes>();
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
                 IRubyObject proxyClass = self.getMetaClass().getInstanceVariables().fastGetInstanceVariable("@java_proxy_class");
                 if (proxyClass == null || proxyClass.isNil()) {
                     proxyClass = JavaProxyClass.get_with_class(self, self.getMetaClass());
                     self.getMetaClass().getInstanceVariables().fastSetInstanceVariable("@java_proxy_class", proxyClass);
                 }
                 JavaProxyClass realProxyClass = (JavaProxyClass)proxyClass;
                 RubyArray constructors = realProxyClass.constructors();
                 ArrayList<JavaProxyConstructor> forArity = new ArrayList<JavaProxyConstructor>();
                 for (int i = 0; i < constructors.size(); i++) {
                     JavaProxyConstructor constructor = (JavaProxyConstructor)constructors.eltInternal(i);
                     if (constructor.getParameterTypes().length == args.length) {
                         forArity.add(constructor);
                     }
                 }
                 if (forArity.size() == 0) {
                     throw context.getRuntime().newArgumentError("wrong # of arguments for constructor");
                 }
                 JavaProxyConstructor matching = (JavaProxyConstructor)matchingCallableArityN(
                         self,
                         methodCache,
                         forArity.toArray(new JavaProxyConstructor[forArity.size()]), args, args.length);
                 Object[] newArgs = new Object[args.length];
                 Class[] parameterTypes = matching.getParameterTypes();
                 for (int i = 0; i < args.length; i++) {
                     newArgs[i] = JavaUtil.convertArgumentToType(context, args[i], parameterTypes[i]);
                 }
                 JavaObject newObject = matching.newInstance(self, newArgs);
                 return JavaUtilities.set_java_object(self, self, newObject);
             }
         });
 
         return runtime.getNil();
     }
 
     // package scheme 2: separate module for each full package name, constructed 
     // from the camel-cased package segments: Java::JavaLang::Object, 
     private static void addToJavaPackageModule(RubyModule proxyClass, JavaClass javaClass) {
         Class<?> clazz = javaClass.javaClass();
         String fullName;
         if ((fullName = clazz.getName()) == null) {
             return;
         }
         int endPackage = fullName.lastIndexOf('.');
         // we'll only map conventional class names to modules 
         if (fullName.indexOf('$') != -1 || !Character.isUpperCase(fullName.charAt(endPackage + 1))) {
             return;
         }
         Ruby runtime = proxyClass.getRuntime();
         String packageString = endPackage < 0 ? "" : fullName.substring(0, endPackage);
         RubyModule packageModule = getJavaPackageModule(runtime, packageString);
         if (packageModule != null) {
             String className = fullName.substring(endPackage + 1);
             if (packageModule.getConstantAt(className) == null) {
                 packageModule.const_set(runtime.newSymbol(className), proxyClass);
             }
         }
     }
 
     private static RubyModule getJavaPackageModule(Ruby runtime, String packageString) {
         String packageName;
         int length = packageString.length();
         if (length == 0) {
             packageName = "Default";
         } else {
             StringBuilder buf = new StringBuilder();
             for (int start = 0, offset = 0; start < length; start = offset + 1) {
                 if ((offset = packageString.indexOf('.', start)) == -1) {
                     offset = length;
                 }
                 buf.append(Character.toUpperCase(packageString.charAt(start))).append(packageString.substring(start + 1, offset));
             }
             packageName = buf.toString();
         }
 
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject packageModule = javaModule.getConstantAt(packageName);
         if (packageModule == null) {
             return createPackageModule(javaModule, packageName, packageString);
         } else if (packageModule instanceof RubyModule) {
             return (RubyModule) packageModule;
         } else {
             return null;
         }
     }
 
     private static RubyModule createPackageModule(RubyModule parent, String name, String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule) runtime.getJavaSupport().getPackageModuleTemplate().dup();
         packageModule.fastSetInstanceVariable("@package_name", runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         MetaClass metaClass = (MetaClass) packageModule.getMetaClass();
         metaClass.setAttached(packageModule);
         return packageModule;
     }
     private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     public static RubyModule getPackageModule(Ruby runtime, String name) {
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.getConstantAt(name)) instanceof RubyModule) {
             return (RubyModule) value;
         }
         String packageName;
         if ("Default".equals(name)) {
             packageName = "";
         } else {
             Matcher m = CAMEL_CASE_PACKAGE_SPLITTER.matcher(name);
             packageName = m.replaceAll("$1.$2").toLowerCase();
         }
         return createPackageModule(javaModule, name, packageName);
     }
 
     public static IRubyObject get_package_module(IRubyObject recv, IRubyObject symObject) {
         return getPackageModule(recv.getRuntime(), symObject.asJavaString());
     }
 
     public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject dottedName) {
         Ruby runtime = recv.getRuntime();
         RubyModule module = getJavaPackageModule(runtime, dottedName.asJavaString());
         return module == null ? runtime.getNil() : module;
     }
 
     public static RubyModule getProxyOrPackageUnderPackage(ThreadContext context, final Ruby runtime, 
             RubyModule parentPackage, String sym) {
         IRubyObject packageNameObj = parentPackage.fastGetInstanceVariable("@package_name");
         if (packageNameObj == null) {
             throw runtime.newArgumentError("invalid package module");
         }
         String packageName = packageNameObj.asJavaString();
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         String fullName = packageName + name;
         if (Character.isLowerCase(name.charAt(0))) {
             // TODO: should check against all Java reserved names here, not just primitives
             if (JAVA_PRIMITIVES.containsKey(name)) {
                 throw runtime.newArgumentError("illegal package name component: " + name);
             // this covers the rare case of lower-case class names (and thus will
             // fail 99.999% of the time). fortunately, we'll only do this once per
             // package name. (and seriously, folks, look into best practices...)
             }
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, fullName));
             } catch (RaiseException re) { /* expected */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* expected */ }
 
             RubyModule packageModule;
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if ((packageModule = getJavaPackageModule(runtime, fullName)) == null) {
                 return null;
             // save package module as ivar in parent, and add method to parent so
             // we don't have to come back here.
             }
             final String ivarName = ("@__pkg__" + name).intern();
             parentPackage.fastSetInstanceVariable(ivarName, packageModule);
             RubyClass singleton = parentPackage.getSingletonClass();
             singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
 
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) {
                         Arity.raiseArgumentError(runtime, args.length, 0, 0);
                     }
                     IRubyObject variable;
                     if ((variable = ((RubyModule) self).fastGetInstanceVariable(ivarName)) != null) {
                         return variable;
                     }
                     return runtime.getNil();
                 }
 
                 @Override
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             return packageModule;
         } else {
             // upper case name, so most likely a class
             return getProxyClass(runtime, JavaClass.forNameVerbose(runtime, fullName));
 
         // FIXME: we should also support orgs that use capitalized package
         // names (including, embarrassingly, the one I work for), but this
         // should be enabled by a system property, as the expected default
         // behavior for an upper-case value should be (and is) to treat it
         // as a class name, and raise an exception if it's not found 
 
 //            try {
 //                return getProxyClass(runtime, JavaClass.forName(runtime, fullName));
 //            } catch (Exception e) {
 //                // but for those not hip to conventions and best practices,
 //                // we'll try as a package
 //                return getJavaPackageModule(runtime, fullName);
 //            }
         }
     }
 
     public static IRubyObject get_proxy_or_package_under_package(
             ThreadContext context,
             IRubyObject recv,
             IRubyObject parentPackage,
             IRubyObject sym) {
         Ruby runtime = recv.getRuntime();
         if (!(parentPackage instanceof RubyModule)) {
             throw runtime.newTypeError(parentPackage, runtime.getModule());
         }
         RubyModule result;
         if ((result = getProxyOrPackageUnderPackage(context, runtime,
                 (RubyModule) parentPackage, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
 
     public static RubyModule getTopLevelProxyOrPackage(ThreadContext context, final Ruby runtime, String sym) {
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         if (Character.isLowerCase(name.charAt(0))) {
             // this covers primitives and (unlikely) lower-case class names
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not primitive or lc class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not primitive or lc class */ }
 
             // TODO: check for Java reserved names and raise exception if encountered
 
             RubyModule packageModule;
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if ((packageModule = getJavaPackageModule(runtime, name)) == null) {
                 return null;
             }
             RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
             if (javaModule.getMetaClass().isMethodBound(name, false)) {
                 return packageModule;
             // save package module as ivar in parent, and add method to parent so
             // we don't have to come back here.
             }
             final String ivarName = ("@__pkg__" + name).intern();
             javaModule.fastSetInstanceVariable(ivarName, packageModule);
             RubyClass singleton = javaModule.getSingletonClass();
             singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
 
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) {
                         Arity.raiseArgumentError(runtime, args.length, 0, 0);
                     }
                     IRubyObject variable;
                     if ((variable = ((RubyModule) self).fastGetInstanceVariable(ivarName)) != null) {
                         return variable;
                     }
                     return runtime.getNil();
                 }
 
                 @Override
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             return packageModule;
         } else {
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not a class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not a class */ }
 
             // upper-case package name
             // TODO: top-level upper-case package was supported in the previous (Ruby-based)
             // implementation, so leaving as is.  see note at #getProxyOrPackageUnderPackage
             // re: future approach below the top-level.
             return getPackageModule(runtime, name);
         }
     }
 
     public static IRubyObject get_top_level_proxy_or_package(ThreadContext context, IRubyObject recv, IRubyObject sym) {
         Ruby runtime = context.getRuntime();
         RubyModule result;
         if ((result = getTopLevelProxyOrPackage(context, runtime, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
 
     public static IRubyObject matching_method(IRubyObject recv, IRubyObject methods, IRubyObject args) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
         List<Class<?>> arg_types = new ArrayList<Class<?>>();
         int alen = ((RubyArray) args).getLength();
         IRubyObject[] aargs = ((RubyArray) args).toJavaArrayMaybeUnsafe();
         for (int i = 0; i < alen; i++) {
             if (aargs[i] instanceof JavaObject) {
                 arg_types.add(((JavaClass) ((JavaObject) aargs[i]).java_class()).javaClass());
             } else {
                 arg_types.add(aargs[i].getClass());
             }
         }
 
         Map ms = (Map) matchCache.get(methods);
         if (ms == null) {
             ms = new HashMap();
             matchCache.put(methods, ms);
         } else {
             IRubyObject method = (IRubyObject) ms.get(arg_types);
             if (method != null) {
                 return method;
             }
         }
 
         int mlen = ((RubyArray) methods).getLength();
         IRubyObject[] margs = ((RubyArray) methods).toJavaArrayMaybeUnsafe();
 
         for (int i = 0; i < 2; i++) {
             for (int k = 0; k < mlen; k++) {
                 IRubyObject method = margs[k];
                 List<Class<?>> types = Arrays.asList(((ParameterTypes) method).getParameterTypes());
 
                 // Compatible (by inheritance)
                 if (arg_types.size() == types.size()) {
                     // Exact match
                     if (types.equals(arg_types)) {
                         ms.put(arg_types, method);
                         return method;
                     }
 
                     boolean match = true;
                     for (int j = 0; j < types.size(); j++) {
                         if (!(JavaClass.assignable(types.get(j), arg_types.get(j)) &&
                                 (i > 0 || primitive_match(types.get(j), arg_types.get(j)))) && !JavaUtil.isDuckTypeConvertable(arg_types.get(j), types.get(j))) {
                             match = false;
                             break;
                         }
                     }
                     if (match) {
                         ms.put(arg_types, method);
                         return method;
                     }
                 } // Could check for varargs here?
 
             }
         }
 
         Object o1 = margs[0];
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         } else {
             throw recv.getRuntime().newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         }
     }
     
     public static int argsHashCode(Object[] a) {
         if (a == null)
             return 0;
  
         int result = 1;
  
         for (Object element : a)
             result = 31 * result + (element == null ? 0 : element.getClass().hashCode());
  
         return result;
     }
     
+    public static int argsHashCode(Class[] a) {
+        if (a == null)
+            return 0;
+ 
+        int result = 1;
+ 
+        for (Class element : a)
+            result = 31 * result + (element == null ? 0 : element.hashCode());
+ 
+        return result;
+    }
+    
     public static int argsHashCode(IRubyObject a0) {
         return 31 + classHashCode(a0);
     }
     
     public static int argsHashCode(IRubyObject a0, IRubyObject a1) {
         return 31 * (31 + classHashCode(a0)) + classHashCode(a1);
     }
     
     public static int argsHashCode(IRubyObject a0, IRubyObject a1, IRubyObject a2) {
         return 31 * (31 * (31 + classHashCode(a0)) + classHashCode(a1)) + classHashCode(a2);
     }
     
     public static int argsHashCode(IRubyObject a0, IRubyObject a1, IRubyObject a2, IRubyObject a3) {
         return 31 * (31 * (31 * (31 + classHashCode(a0)) + classHashCode(a1)) + classHashCode(a2)) + classHashCode(a3);
     }
     
     private static int classHashCode(IRubyObject o) {
         return o == null ? 0 : o.getJavaClass().hashCode();
     }
     
     public static int argsHashCode(IRubyObject[] a) {
         if (a == null)
             return 0;
  
         int result = 1;
  
         for (IRubyObject element : a)
             result = 31 * result + classHashCode(element);
  
         return result;
     }
     
     public static Class argClass(Object a) {
         if (a == null) return void.class;
         
         
         return a.getClass();
     }
     
     public static Class argClass(IRubyObject a) {
         if (a == null) return void.class;
         
         return a.getJavaClass();
     }
 
     public static JavaCallable matching_method_internal(IRubyObject recv, Map cache, JavaCallable[] methods, Object[] args, int len) {
         int signatureCode = argsHashCode(args);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method != null) {
             return method;
         }
 
         int mlen = methods.length;
 
         mfor:
         for (int k = 0; k < mlen; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             // Compatible (by inheritance)
             if (len == types.length) {
                 // Exact match
                 boolean same = true;
                 for (int x = 0, y = len; x < y; x++) {
                     if (!types[x].equals(argClass(args[x]))) {
                         same = false;
                         break;
                     }
                 }
                 if (same) {
                     cache.put(signatureCode, method);
                     return method;
                 }
 
                 for (int j = 0, m = len; j < m; j++) {
                     if (!(JavaClass.assignable(types[j], argClass(args[j])) &&
                             primitive_match(types[j], argClass(args[j])))) {
                         continue mfor;
                     }
                 }
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         mfor:
         for (int k = 0; k < mlen; k++) {
             method = methods[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             // Compatible (by inheritance)
             if (len == types.length) {
                 for (int j = 0, m = len; j < m; j++) {
                     if (!JavaClass.assignable(types[j], argClass(args[j])) && !JavaUtil.isDuckTypeConvertable(argClass(args[j]), types[j])) {
                         continue mfor;
                     }
                 }
                 cache.put(signatureCode, method);
                 return method;
             }
         }
 
         // We've fallen and can't get up...prepare for error message
         Object o1 = methods[0];
         ArrayList argTypes = new ArrayList(args.length);
         for (Object o : args) argTypes.add(argClass(o));
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + argTypes + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         } else {
             Thread.dumpStack();
             throw recv.getRuntime().newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + argTypes + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         }
     }
     
     private static JavaCallable findMatchingCallableForArgs(IRubyObject recv, Map cache, int signatureCode, JavaCallable[] methods, IRubyObject... args) {
         JavaCallable method = findCallable(methods, Exact, args);
         if (method == null) method = findCallable(methods, AssignableAndPrimitivable, args);
         if (method == null) method = findCallable(methods, AssignableOrDuckable, args);
         if (method == null) {
             throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, args);
         } else {
             cache.put(signatureCode, method);
             return method;
         }
     }
 
     // A version that just requires ParameterTypes; they will all move toward
     // something similar soon
     public static ParameterTypes matchingCallableArityN(IRubyObject recv, Map cache, ParameterTypes[] methods, IRubyObject[] args, int argsLength) {
         int signatureCode = argsHashCode(args);
         ParameterTypes method = (ParameterTypes)cache.get(signatureCode);
         if (method == null) method = findMatchingCallableForArgs(recv, cache, signatureCode, methods, args);
         return method;
     }
     private static ParameterTypes findMatchingCallableForArgs(IRubyObject recv, Map cache, int signatureCode, ParameterTypes[] methods, IRubyObject... args) {
         ParameterTypes method = findCallable(methods, Exact, args);
         if (method == null) method = findCallable(methods, AssignableAndPrimitivable, args);
         if (method == null) method = findCallable(methods, AssignableOrDuckable, args);
         if (method == null) {
             throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, args);
         } else {
             cache.put(signatureCode, method);
             return method;
         }
     }
     private static ParameterTypes findCallable(ParameterTypes[] callables, CallableAcceptor acceptor, IRubyObject... args) {
         for (int k = 0; k < callables.length; k++) {
             ParameterTypes callable = callables[k];
             Class<?>[] types = callable.getParameterTypes();
             
             if (acceptor.accept(types, args)) return callable;
         }
         return null;
     }
     private static RaiseException argTypesDoNotMatch(Ruby runtime, IRubyObject receiver, ParameterTypes[] methods, IRubyObject... args) {
         Object o1 = methods[0];
         ArrayList<Class> argTypes = new ArrayList<Class>(args.length);
         for (Object o : args) argTypes.add(argClass(o));
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw runtime.newNameError("no constructor with arguments matching " + argTypes + " on object " + receiver.callMethod(runtime.getCurrentContext(), "inspect"), null);
         } else {
             throw runtime.newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + argTypes + " on object " + receiver.callMethod(runtime.getCurrentContext(), "inspect"), null);
         }
     }
     
     // NOTE: The five match methods are arity-split to avoid the cost of boxing arguments
     // when there's already a cached match. Do not condense them into a single
     // method.
     public static JavaCallable matchingCallableArityN(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject[] args, int argsLength) {
         int signatureCode = argsHashCode(args);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = findMatchingCallableForArgs(recv, cache, signatureCode, methods, args);
         return method;
     }
     
     public static JavaCallable matchingCallableArityOne(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0) {
         int signatureCode = argsHashCode(arg0);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = findMatchingCallableForArgs(recv, cache, signatureCode, methods, arg0);
         return method;
     }
     
     public static JavaCallable matchingCallableArityTwo(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1) {
         int signatureCode = argsHashCode(arg0, arg1);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = findMatchingCallableForArgs(recv, cache, signatureCode, methods, arg0, arg1);
         return method;
     }
     
     public static JavaCallable matchingCallableArityThree(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         int signatureCode = argsHashCode(arg0, arg1, arg2);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = findMatchingCallableForArgs(recv, cache, signatureCode, methods, arg0, arg1, arg2);
         return method;
     }
     
     public static JavaCallable matchingCallableArityFour(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         int signatureCode = argsHashCode(arg0, arg1, arg2, arg3);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = findMatchingCallableForArgs(recv, cache, signatureCode, methods, arg0, arg1, arg2, arg3);
         return method;
     }
     
     private static interface CallableAcceptor {
         public boolean accept(Class<?>[] types, IRubyObject[] args);
     }
     
     private static final CallableAcceptor Exact = new CallableAcceptor() {
         public boolean accept(Class<?>[] types, IRubyObject[] args) {
             return exactMatch(types, args);
         }
     };
     
     private static final CallableAcceptor AssignableAndPrimitivable = new CallableAcceptor() {
         public boolean accept(Class<?>[] types, IRubyObject[] args) {
             return assignableAndPrimitivable(types, args);
         }
     };
     
     private static final CallableAcceptor AssignableOrDuckable = new CallableAcceptor() {
         public boolean accept(Class<?>[] types, IRubyObject[] args) {
             return assignableOrDuckable(types, args);
         }
     };
     
     private static JavaCallable findCallable(JavaCallable[] callables, CallableAcceptor acceptor, IRubyObject... args) {
         for (int k = 0; k < callables.length; k++) {
             JavaCallable callable = callables[k];
             Class<?>[] types = ((ParameterTypes) callable).getParameterTypes();
             
             if (acceptor.accept(types, args)) return callable;
         }
         return null;
     }
     
     private static boolean exactMatch(Class[] types, IRubyObject... args) {
         for (int i = 0; i < types.length; i++) {
             if (!types[i].equals(argClass(args[i]))) return false;
         }
         return true;
     }
     
     private static boolean assignableAndPrimitivable(Class[] types, IRubyObject... args) {
         for (int i = 0; i < types.length; i++) {
             if (!(assignable(types[i], args[i]) && primitivable(types[i], args[i]))) return false;
         }
         return true;
     }
     
     private static boolean assignableOrDuckable(Class[] types, IRubyObject... args) {
         for (int i = 0; i < types.length; i++) {
             if (!(assignable(types[i], args[i]) || duckable(types[i], args[i]))) return false;
         }
         return true;
     }
     
     private static boolean assignable(Class type, IRubyObject arg) {
         return JavaClass.assignable(type, argClass(arg));
     }
     
     /**
      * This method checks whether an argument can be *directly* converted into
      * the target primitive, i.e. without changing from integral to floating-point.
      * 
      * @param type The target type
      * @param arg The argument to convert
      * @return Whether the argument can be directly converted to the target primitive type
      */
     private static boolean primitivable(Class type, IRubyObject arg) {
         Class argClass = argClass(arg);
         if (type.isPrimitive()) {
             // TODO: This is where we would want to do precision checks to see
             // if it's non-destructive to coerce a given type into the target
             // integral primitive
             if (type == Integer.TYPE || type == Long.TYPE || type == Short.TYPE || type == Character.TYPE) {
                 return argClass == long.class || // long first because it's what Fixnum claims to be
                         argClass == byte.class ||
                         argClass == short.class ||
                         argClass == char.class ||
                         argClass == int.class ||
                         argClass == Long.class ||
                         argClass == Byte.class ||
                         argClass == Short.class ||
                         argClass == Character.class ||
                         argClass == Integer.class;
             } else if (type == Float.TYPE || type == Double.TYPE) {
                 return argClass == double.class || // double first because it's what float claims to be
                         argClass == float.class ||
                         argClass == Float.class ||
                         argClass == Double.class;
             } else if (type == Boolean.TYPE) {
                 return argClass == boolean.class ||
                         argClass == Boolean.class;
             }
         }
         return false;
     }
     
     private static boolean duckable(Class type, IRubyObject arg) {
         return JavaUtil.isDuckTypeConvertable(argClass(arg), type);
     }
     
     private static RaiseException argTypesDoNotMatch(Ruby runtime, IRubyObject receiver, JavaCallable[] methods, IRubyObject... args) {
         Object o1 = methods[0];
         ArrayList<Class> argTypes = new ArrayList<Class>(args.length);
         for (Object o : args) argTypes.add(argClass(o));
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw runtime.newNameError("no constructor with arguments matching " + argTypes + " on object " + receiver.callMethod(runtime.getCurrentContext(), "inspect"), null);
         } else {
             throw runtime.newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + argTypes + " on object " + receiver.callMethod(runtime.getCurrentContext(), "inspect"), null);
         }
     }
     
     public static IRubyObject access(IRubyObject recv, IRubyObject java_type) {
         int modifiers = ((JavaClass) java_type).javaClass().getModifiers();
         return recv.getRuntime().newString(Modifier.isPublic(modifiers) ? "public" : (Modifier.isProtected(modifiers) ? "protected" : "private"));
     }
 
     public static IRubyObject valid_constant_name_p(IRubyObject recv, IRubyObject name) {
         RubyString sname = name.convertToString();
         if (sname.getByteList().length() == 0) {
             return recv.getRuntime().getFalse();
         }
         return Character.isUpperCase(sname.getByteList().charAt(0)) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 
     public static boolean primitive_match(Object v1, Object v2) {
         if (((Class) v1).isPrimitive()) {
             if (v1 == Integer.TYPE || v1 == Long.TYPE || v1 == Short.TYPE || v1 == Character.TYPE) {
                 return v2 == Integer.class ||
                         v2 == Long.class ||
                         v2 == Short.class ||
                         v2 == Character.class;
             } else if (v1 == Float.TYPE || v1 == Double.TYPE) {
                 return v2 == Float.class ||
                         v2 == Double.class;
             } else if (v1 == Boolean.TYPE) {
                 return v2 == Boolean.class;
             }
             return false;
         }
         return true;
     }
 
     public static IRubyObject primitive_match(IRubyObject recv, IRubyObject t1, IRubyObject t2) {
         if (((JavaClass) t1).primitive_p().isTrue()) {
             Object v1 = ((JavaObject) t1).getValue();
             Object v2 = ((JavaObject) t2).getValue();
             return primitive_match(v1, v2) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
         }
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject wrap(IRubyObject recv, IRubyObject java_object) {
         return getInstance(recv.getRuntime(), ((JavaObject) java_object).getValue());
     }
 
     public static IRubyObject wrap(Ruby runtime, IRubyObject java_object) {
         return getInstance(runtime, ((JavaObject) java_object).getValue());
     }
 
     // Java methods
     @JRubyMethod(required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject define_exception_handler(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = args[0].toString();
         RubyProc handler = null;
         if (args.length > 1) {
             handler = (RubyProc) args[1];
         } else {
             handler = recv.getRuntime().newProc(Block.Type.PROC, block);
         }
         recv.getRuntime().getJavaSupport().defineExceptionHandler(name, handler);
 
         return recv;
     }
 
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject primitive_to_java(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.primitive_to_java(recv, object, unusedBlock);
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version 
      */
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject java_to_ruby(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.java_to_ruby(recv.getRuntime(), object);
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.ruby_to_java(recv, object, unusedBlock);
     }
 
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.java_to_primitive(recv, object, unusedBlock);
     }
 
     @JRubyMethod(required = 1, rest = true, frame = true, module = true, visibility = Visibility.PRIVATE)
     @Deprecated
     public static IRubyObject new_proxy_instance(final IRubyObject recv, IRubyObject[] args, Block block) {
         int size = Arity.checkArgumentCount(recv.getRuntime(), args, 1, -1) - 1;
         final RubyProc proc;
 
         // Is there a supplied proc argument or do we assume a block was supplied
         if (args[size] instanceof RubyProc) {
             proc = (RubyProc) args[size];
         } else {
             proc = recv.getRuntime().newProc(Block.Type.PROC, block);
             size++;
         }
 
         // Create list of interfaces to proxy (and make sure they really are interfaces)
         Class[] interfaces = new Class[size];
         for (int i = 0; i < size; i++) {
             if (!(args[i] instanceof JavaClass) || !((JavaClass) args[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + args[i]);
             }
             interfaces[i] = ((JavaClass) args[i]).javaClass();
         }
 
         return JavaObject.wrap(recv.getRuntime(), Proxy.newProxyInstance(recv.getRuntime().getJRubyClassLoader(), interfaces, new InvocationHandler() {
 
             private Map parameterTypeCache = new ConcurrentHashMap();
 
             public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                 Class[] parameterTypes = (Class[]) parameterTypeCache.get(method);
                 if (parameterTypes == null) {
                     parameterTypes = method.getParameterTypes();
                     parameterTypeCache.put(method, parameterTypes);
                 }
                 int methodArgsLength = parameterTypes.length;
                 String methodName = method.getName();
 
                 if (methodName.equals("toString") && methodArgsLength == 0) {
                     return proxy.getClass().getName();
                 } else if (methodName.equals("hashCode") && methodArgsLength == 0) {
                     return new Integer(proxy.getClass().hashCode());
                 } else if (methodName.equals("equals") && methodArgsLength == 1 && parameterTypes[0].equals(Object.class)) {
                     return Boolean.valueOf(proxy == nargs[0]);
                 }
                 Ruby runtime = recv.getRuntime();
                 int length = nargs == null ? 0 : nargs.length;
                 IRubyObject[] rubyArgs = new IRubyObject[length + 2];
                 rubyArgs[0] = JavaObject.wrap(runtime, proxy);
                 rubyArgs[1] = new JavaMethod(runtime, method);
                 for (int i = 0; i < length; i++) {
                     rubyArgs[i + 2] = JavaObject.wrap(runtime, nargs[i]);
                 }
                 return JavaUtil.convertArgument(runtime, proc.call(runtime.getCurrentContext(), rubyArgs), method.getReturnType());
             }
         }));
     }
 
     @JRubyMethod(required = 2, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_proxy_instance2(IRubyObject recv, final IRubyObject wrapper, IRubyObject ifcs, Block block) {
         IRubyObject[] javaClasses = ((RubyArray)ifcs).toJavaArray();
         final Ruby runtime = recv.getRuntime();
 
         // Create list of interface names to proxy (and make sure they really are interfaces)
+        // Also build a hashcode from all classes to use for retrieving previously-created impl
         Class[] interfaces = new Class[javaClasses.length];
         for (int i = 0; i < javaClasses.length; i++) {
             if (!(javaClasses[i] instanceof JavaClass) || !((JavaClass) javaClasses[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + javaClasses[i]);
             }
             interfaces[i] = ((JavaClass) javaClasses[i]).javaClass();
         }
-        
-        // TODO: cache this class!
-        String implClassName = "InterfaceImpl" + wrapper.getMetaClass().hashCode();
+
+        // hashcode is a combination of the interfaces and the Ruby class we're using
+        // to implement them
+        int interfacesHashCode = argsHashCode(interfaces);
+        // if it's a singleton class and the real class is proc, we're doing closure conversion
+        // so just use Proc's hashcode
+        if (wrapper.getMetaClass().isSingleton() && wrapper.getMetaClass().getRealClass() == runtime.getProc()) {
+            interfacesHashCode = 31 * interfacesHashCode + runtime.getProc().hashCode();
+        } else {
+            // normal new class implementing interfaces
+            interfacesHashCode = 31 * interfacesHashCode + wrapper.getMetaClass().hashCode();
+        }
+        String implClassName = "InterfaceImpl" + interfacesHashCode;
         Class proxyImplClass;
         try {
             proxyImplClass = Class.forName(implClassName, true, runtime.getJRubyClassLoader());
         } catch (ClassNotFoundException cnfe) {
             proxyImplClass = MiniJava.createOldStyleImplClass(interfaces, wrapper.getMetaClass(), runtime, implClassName);
         }
         
         try {
             Constructor proxyConstructor = proxyImplClass.getConstructor(IRubyObject.class);
             return JavaObject.wrap(recv.getRuntime(), proxyConstructor.newInstance(wrapper));
         } catch (NoSuchMethodException nsme) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + nsme);
         } catch (InvocationTargetException ite) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ite);
         } catch (InstantiationException ie) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ie);
         } catch (IllegalAccessException iae) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + iae);
         }
     }
 }
diff --git a/src/org/jruby/javasupport/JavaUtil.java b/src/org/jruby/javasupport/JavaUtil.java
index b81ea3187b..35980bbccd 100644
--- a/src/org/jruby/javasupport/JavaUtil.java
+++ b/src/org/jruby/javasupport/JavaUtil.java
@@ -1,1229 +1,1232 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Don Schwartz <schwardo@users.sourceforge.net>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 package org.jruby.javasupport;
 
 import java.io.UnsupportedEncodingException;
 import java.lang.reflect.Method;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.jruby.Ruby;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyNil;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.ByteList;
 
 public class JavaUtil {
 
     public static Object convertRubyToJava(IRubyObject rubyObject) {
         return convertRubyToJava(rubyObject, Object.class);
     }
     
     public interface RubyConverter {
         public Object convert(ThreadContext context, IRubyObject rubyObject);
     }
     
     public static final RubyConverter RUBY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return Boolean.valueOf(rubyObject.isTrue());
         }
     };
     
     public static final RubyConverter RUBY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Byte((byte) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Byte((byte) 0);
         }
     };
     
     public static final RubyConverter RUBY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Short((short) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Short((short) 0);
         }
     };
     
     public static final RubyConverter RUBY_CHAR_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Character((char) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Character((char) 0);
         }
     };
     
     public static final RubyConverter RUBY_INTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Integer((int) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Integer(0);
         }
     };
     
     public static final RubyConverter RUBY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Long(((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Long(0);
         }
     };
     
     public static final RubyConverter RUBY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Float((float) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_F, "to_f")).getDoubleValue());
             }
             return new Float(0.0);
         }
     };
     
     public static final RubyConverter RUBY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Double(((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_F, "to_f")).getDoubleValue());
             }
             return new Double(0.0);
         }
     };
     
     public static final RubyConverter ARRAY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject == context.getRuntime().getFalse() || rubyObject.isNil()) {
                 return Boolean.FALSE;
             } else if (rubyObject == context.getRuntime().getTrue()) {
                 return Boolean.TRUE;
             } else if (rubyObject instanceof RubyNumeric) {
                 return ((RubyNumeric)rubyObject).getLongValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
             } else if (rubyObject instanceof RubyString) {
                 return Boolean.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return integer.getLongValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Byte.valueOf((byte)((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Byte.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Byte.valueOf((byte)integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Short.valueOf((short)((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Short.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Short.valueOf((short)integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_CHAR_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Character.valueOf((char)((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Character.valueOf(rubyObject.asJavaString().charAt(0));
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Character.valueOf((char)integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_INT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Integer.valueOf((int)((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Integer.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Integer.valueOf((int)integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Long.valueOf(((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return Long.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Long.valueOf(integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Float.valueOf((float)((RubyNumeric)rubyObject).getDoubleValue());
             } else if (rubyObject instanceof RubyString) {
                 return Float.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Float.valueOf((float)integer.getDoubleValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return Double.valueOf(((RubyNumeric)rubyObject).getDoubleValue());
             } else if (rubyObject instanceof RubyString) {
                 return Double.valueOf(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return Double.valueOf(integer.getDoubleValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_OBJECT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyInteger) {
                 long value = ((RubyInteger)rubyObject).getLongValue();
                 if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                     return Integer.valueOf((int)value);
                 } else if (value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) {
                     return Long.valueOf(value);
                 } else {
                     return new BigInteger(rubyObject.toString());
                 }
             } else if (rubyObject instanceof RubyFloat) {
                 return Double.valueOf(((RubyFloat)rubyObject).getDoubleValue());
             } else if (rubyObject instanceof JavaProxy) {
                 return ((JavaProxy)rubyObject).unwrap();
             } else {
                 return java_to_ruby(context.getRuntime(), rubyObject);
             }
         }
     };
     
     public static final RubyConverter ARRAY_CLASS_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof JavaClass) {
                 return ((JavaClass)rubyObject).javaClass();
             } else {
                 return java_to_ruby(context.getRuntime(), rubyObject);
             }
         }
     };
 
     public static final RubyConverter ARRAY_STRING_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyString) {
                 return ((RubyString)rubyObject).getUnicodeValue();
             } else {
                 return rubyObject.toString();
             }
         }
     };
     
     public static final RubyConverter ARRAY_BIGINTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return BigInteger.valueOf(((RubyNumeric)rubyObject).getLongValue());
             } else if (rubyObject instanceof RubyString) {
                 return new BigDecimal(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return BigInteger.valueOf(integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final RubyConverter ARRAY_BIGDECIMAL_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject instanceof RubyNumeric) {
                 return BigDecimal.valueOf(((RubyNumeric)rubyObject).getDoubleValue());
             } else if (rubyObject instanceof RubyString) {
                 return new BigDecimal(rubyObject.asJavaString());
             } else if (rubyObject instanceof JavaProxy) {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             } else if (rubyObject.respondsTo("to_f")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_f");
                 return BigDecimal.valueOf(integer.getDoubleValue());
             } else if (rubyObject.respondsTo("to_i")) {
                 RubyInteger integer = (RubyInteger)RuntimeHelpers.invoke(context, rubyObject, "to_i");
                 return BigDecimal.valueOf(integer.getLongValue());
             } else {
                 return ruby_to_java(rubyObject, rubyObject, Block.NULL_BLOCK);
             }
         }
     };
     
     public static final Map<Class, RubyConverter> RUBY_CONVERTERS = new HashMap<Class, RubyConverter>();
     public static final Map<Class, RubyConverter> ARRAY_CONVERTERS = new HashMap<Class, RubyConverter>();
     
     static {
         RUBY_CONVERTERS.put(Boolean.class, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Boolean.TYPE, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Byte.class, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Byte.TYPE, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Short.class, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Short.TYPE, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Integer.class, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Integer.TYPE, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Long.class, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Long.TYPE, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Float.class, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Float.TYPE, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Double.class, RUBY_DOUBLE_CONVERTER);
         RUBY_CONVERTERS.put(Double.TYPE, RUBY_DOUBLE_CONVERTER);
         
         ARRAY_CONVERTERS.put(Boolean.class, ARRAY_BOOLEAN_CONVERTER);
         ARRAY_CONVERTERS.put(Boolean.TYPE, ARRAY_BOOLEAN_CONVERTER);
         ARRAY_CONVERTERS.put(Byte.class, ARRAY_BYTE_CONVERTER);
         ARRAY_CONVERTERS.put(Byte.TYPE, ARRAY_BYTE_CONVERTER);
         ARRAY_CONVERTERS.put(Short.class, ARRAY_SHORT_CONVERTER);
         ARRAY_CONVERTERS.put(Short.TYPE, ARRAY_SHORT_CONVERTER);
         ARRAY_CONVERTERS.put(Character.class, ARRAY_CHAR_CONVERTER);
         ARRAY_CONVERTERS.put(Character.TYPE, ARRAY_CHAR_CONVERTER);
         ARRAY_CONVERTERS.put(Integer.class, ARRAY_INT_CONVERTER);
         ARRAY_CONVERTERS.put(Integer.TYPE, ARRAY_INT_CONVERTER);
         ARRAY_CONVERTERS.put(Long.class, ARRAY_LONG_CONVERTER);
         ARRAY_CONVERTERS.put(Long.TYPE, ARRAY_LONG_CONVERTER);
         ARRAY_CONVERTERS.put(Float.class, ARRAY_FLOAT_CONVERTER);
         ARRAY_CONVERTERS.put(Float.TYPE, ARRAY_FLOAT_CONVERTER);
         ARRAY_CONVERTERS.put(Double.class, ARRAY_DOUBLE_CONVERTER);
         ARRAY_CONVERTERS.put(Double.TYPE, ARRAY_DOUBLE_CONVERTER);
         ARRAY_CONVERTERS.put(String.class, ARRAY_STRING_CONVERTER);
         ARRAY_CONVERTERS.put(Class.class, ARRAY_CLASS_CONVERTER);
         ARRAY_CONVERTERS.put(BigInteger.class, ARRAY_BIGINTEGER_CONVERTER);
         ARRAY_CONVERTERS.put(BigDecimal.class, ARRAY_BIGDECIMAL_CONVERTER);
     }
     
     public static RubyConverter getArrayConverter(Class type) {
         RubyConverter converter = ARRAY_CONVERTERS.get(type);
         if (converter == null) {
             return ARRAY_OBJECT_CONVERTER;
         }
         return converter;
     }
     
     public static byte convertRubyToJavaByte(IRubyObject rubyObject) {
         return ((Byte)convertRubyToJava(rubyObject, byte.class)).byteValue();
     }
     
     public static short convertRubyToJavaShort(IRubyObject rubyObject) {
         return ((Short)convertRubyToJava(rubyObject, short.class)).shortValue();
     }
     
     public static char convertRubyToJavaChar(IRubyObject rubyObject) {
         return ((Character)convertRubyToJava(rubyObject, char.class)).charValue();
     }
     
     public static int convertRubyToJavaInt(IRubyObject rubyObject) {
         return ((Integer)convertRubyToJava(rubyObject, int.class)).intValue();
     }
     
     public static long convertRubyToJavaLong(IRubyObject rubyObject) {
         return ((Long)convertRubyToJava(rubyObject, long.class)).longValue();
     }
     
     public static float convertRubyToJavaFloat(IRubyObject rubyObject) {
         return ((Float)convertRubyToJava(rubyObject, float.class)).floatValue();
     }
     
     public static double convertRubyToJavaDouble(IRubyObject rubyObject) {
         return ((Double)convertRubyToJava(rubyObject, double.class)).doubleValue();
     }
     
     public static boolean convertRubyToJavaBoolean(IRubyObject rubyObject) {
         return ((Boolean)convertRubyToJava(rubyObject, boolean.class)).booleanValue();
     }
 
     public static Object convertRubyToJava(IRubyObject rubyObject, Class javaClass) {
         if (javaClass == void.class || rubyObject == null || rubyObject.isNil()) {
             return null;
         }
         
         ThreadContext context = rubyObject.getRuntime().getCurrentContext();
         
         if (rubyObject.respondsTo("java_object")) {
         	rubyObject = rubyObject.callMethod(context, "java_object");
         }
 
         if (rubyObject.respondsTo("to_java_object")) {
         	rubyObject = rubyObject.callMethod(context, "to_java_object");
         }
 
         if (rubyObject instanceof JavaObject) {
             Object value =  ((JavaObject) rubyObject).getValue();
             
             return convertArgument(rubyObject.getRuntime(), value, value.getClass());
             
         } else if (javaClass == Object.class || javaClass == null) {
             /* The Java method doesn't care what class it is, but we need to
                know what to convert it to, so we use the object's own class.
                If that doesn't help, we use String to force a call to the
                object's "to_s" method. */
             javaClass = rubyObject.getJavaClass();
         }
 
         if (javaClass.isInstance(rubyObject)) {
             // rubyObject is already of the required jruby class (or subclass)
             return rubyObject;
         }
 
         if (javaClass.isPrimitive()) {
             RubyConverter converter = RUBY_CONVERTERS.get(javaClass);
             if (converter != null) {
                 return converter.convert(context, rubyObject);
             }
 
             // XXX this probably isn't good enough -AM
             String s = ((RubyString) rubyObject.callMethod(context, MethodIndex.TO_S, "to_s")).toString();
             if (s.length() > 0) {
                 return new Character(s.charAt(0));
             }
 			return new Character('\0');
         } else if (javaClass == String.class) {
             RubyString rubyString = (RubyString) rubyObject.callMethod(context, MethodIndex.TO_S, "to_s");
             ByteList bytes = rubyString.getByteList();
             try {
                 return new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
             } catch (UnsupportedEncodingException uee) {
                 return new String(bytes.unsafeBytes(), bytes.begin(), bytes.length());
             }
         } else if (javaClass == ByteList.class) {
             return rubyObject.convertToString().getByteList();
         } else if (javaClass == BigInteger.class) {
          	if (rubyObject instanceof RubyBignum) {
          		return ((RubyBignum)rubyObject).getValue();
          	} else if (rubyObject instanceof RubyNumeric) {
  				return  BigInteger.valueOf (((RubyNumeric)rubyObject).getLongValue());
          	} else if (rubyObject.respondsTo("to_i")) {
          		RubyNumeric rubyNumeric = ((RubyNumeric)rubyObject.callMethod(context,MethodIndex.TO_F, "to_f"));
  				return  BigInteger.valueOf (rubyNumeric.getLongValue());
          	}
         } else if (javaClass == BigDecimal.class && !(rubyObject instanceof JavaObject)) {
          	if (rubyObject.respondsTo("to_f")) {
              	double double_value = ((RubyNumeric)rubyObject.callMethod(context,MethodIndex.TO_F, "to_f")).getDoubleValue();
              	return new BigDecimal(double_value);
          	}
         }
         try {
             return ((JavaObject) rubyObject).getValue();
         } catch (ClassCastException ex) {
             if (rubyObject.getRuntime().getDebug().isTrue()) ex.printStackTrace();
             return null;
         }
     }
 
     public static IRubyObject[] convertJavaArrayToRuby(Ruby runtime, Object[] objects) {
         if (objects == null) return IRubyObject.NULL_ARRAY;
         
         IRubyObject[] rubyObjects = new IRubyObject[objects.length];
         for (int i = 0; i < objects.length; i++) {
             rubyObjects[i] = convertJavaToRuby(runtime, objects[i]);
         }
         return rubyObjects;
     }
     
     public interface JavaConverter {
         public IRubyObject convert(Ruby runtime, Object object);
     }
     
     public static final JavaConverter JAVA_DEFAULT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) {
                 return runtime.getNil();
             }
 
             if (object instanceof IRubyObject) {
                 return (IRubyObject) object;
             }
  
             // Note: returns JavaObject instance, which is not
             // directly usable. probably too late to change this now,
             // supplying alternate method convertJavaToUsableRubyObject
             return JavaObject.wrap(runtime, object);
         }
     };
     
     public static final JavaConverter JAVA_BOOLEAN_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBoolean.newBoolean(runtime, ((Boolean)object).booleanValue());
         }
     };
     
     public static final JavaConverter JAVA_FLOAT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Float)object).doubleValue());
         }
     };
     
     public static final JavaConverter JAVA_DOUBLE_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Double)object).doubleValue());
         }
     };
     
     public static final JavaConverter JAVA_CHAR_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Character)object).charValue());
         }
     };
     
     public static final JavaConverter JAVA_BYTE_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Byte)object).byteValue());
         }
     };
     
     public static final JavaConverter JAVA_SHORT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Short)object).shortValue());
         }
     };
     
     public static final JavaConverter JAVA_INT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Integer)object).intValue());
         }
     };
     
     public static final JavaConverter JAVA_LONG_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Long)object).longValue());
         }
     };
     
     public static final JavaConverter JAVA_STRING_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newUnicodeString(runtime, (String)object);
         }
     };
     
     public static final JavaConverter BYTELIST_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newString(runtime, (ByteList)object);
         }
     };
     
     public static final JavaConverter JAVA_BIGINTEGER_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBignum.newBignum(runtime, (BigInteger)object);
         }
     };
     
     private static final Map<Class,JavaConverter> JAVA_CONVERTERS =
         new HashMap<Class,JavaConverter>();
     
     static {
         JAVA_CONVERTERS.put(Byte.class, JAVA_BYTE_CONVERTER);
         JAVA_CONVERTERS.put(Byte.TYPE, JAVA_BYTE_CONVERTER);
         JAVA_CONVERTERS.put(Short.class, JAVA_SHORT_CONVERTER);
         JAVA_CONVERTERS.put(Short.TYPE, JAVA_SHORT_CONVERTER);
         JAVA_CONVERTERS.put(Character.class, JAVA_CHAR_CONVERTER);
         JAVA_CONVERTERS.put(Character.TYPE, JAVA_CHAR_CONVERTER);
         JAVA_CONVERTERS.put(Integer.class, JAVA_INT_CONVERTER);
         JAVA_CONVERTERS.put(Integer.TYPE, JAVA_INT_CONVERTER);
         JAVA_CONVERTERS.put(Long.class, JAVA_LONG_CONVERTER);
         JAVA_CONVERTERS.put(Long.TYPE, JAVA_LONG_CONVERTER);
         JAVA_CONVERTERS.put(Float.class, JAVA_FLOAT_CONVERTER);
         JAVA_CONVERTERS.put(Float.TYPE, JAVA_FLOAT_CONVERTER);
         JAVA_CONVERTERS.put(Double.class, JAVA_DOUBLE_CONVERTER);
         JAVA_CONVERTERS.put(Double.TYPE, JAVA_DOUBLE_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.class, JAVA_BOOLEAN_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.TYPE, JAVA_BOOLEAN_CONVERTER);
         
         JAVA_CONVERTERS.put(String.class, JAVA_STRING_CONVERTER);
         
         JAVA_CONVERTERS.put(ByteList.class, BYTELIST_CONVERTER);
         
         JAVA_CONVERTERS.put(BigInteger.class, JAVA_BIGINTEGER_CONVERTER);
 
     }
     
     public static JavaConverter getJavaConverter(Class clazz) {
         JavaConverter converter = JAVA_CONVERTERS.get(clazz);
         
         if (converter == null) {
             converter = JAVA_DEFAULT_CONVERTER;
         }
         
         return converter;
     }
 
     /**
      * Converts object to the corresponding Ruby type; however, for non-primitives,
      * a JavaObject instance is returned. This must be subsequently wrapped by
      * calling one of Java.wrap, Java.java_to_ruby, Java.new_instance_for, or
      * Java.getInstance, depending on context.
      * 
      * @param runtime
      * @param object 
      * @return corresponding Ruby type, or a JavaObject instance
      */
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object) {
         if (object == null) {
             return runtime.getNil();
         } else if (object instanceof IRubyObject) {
             return (IRubyObject)object;
         }
         return convertJavaToRuby(runtime, object, object.getClass());
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, int i) {
         return runtime.newFixnum(i);
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, long l) {
         return runtime.newFixnum(l);
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, float f) {
         return runtime.newFloat(f);
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, double d) {
         return runtime.newFloat(d);
     }
     
     public static IRubyObject convertJavaToRuby(Ruby runtime, boolean b) {
         return runtime.newBoolean(b);
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object, Class javaClass) {
         return getJavaConverter(javaClass).convert(runtime, object);
     }
     
     /**
      * Returns a usable RubyObject; for types that are not converted to Ruby native
      * types, a Java proxy will be returned. 
      * 
      * @param runtime
      * @param object
      * @return corresponding Ruby type, or a functional Java proxy
      */
     public static IRubyObject convertJavaToUsableRubyObject(Ruby runtime, Object object) {
         if (object == null) return runtime.getNil();
         
         // if it's already IRubyObject, don't re-wrap (JRUBY-2480)
         if (object instanceof IRubyObject) {
             return (IRubyObject)object;
         }
         
         JavaConverter converter = JAVA_CONVERTERS.get(object.getClass());
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     public static Class<?> primitiveToWrapper(Class<?> type) {
         if (type.isPrimitive()) {
             if (type == Integer.TYPE) {
                 return Integer.class;
             } else if (type == Double.TYPE) {
                 return Double.class;
             } else if (type == Boolean.TYPE) {
                 return Boolean.class;
             } else if (type == Byte.TYPE) {
                 return Byte.class;
             } else if (type == Character.TYPE) {
                 return Character.class;
             } else if (type == Float.TYPE) {
                 return Float.class;
             } else if (type == Long.TYPE) {
                 return Long.class;
             } else if (type == Void.TYPE) {
                 return Void.class;
             } else if (type == Short.TYPE) {
                 return Short.class;
             }
         }
         return type;
     }
 
     public static Object convertArgument(Ruby runtime, Object argument, Class<?> parameterType) {
         if (argument == null) {
           if(parameterType.isPrimitive()) {
             throw runtime.newTypeError("primitives do not accept null");
           } else {
             return null;
           }
         }
         
         if (argument instanceof JavaObject) {
             argument = ((JavaObject) argument).getValue();
             if (argument == null) {
                 return null;
             }
         }
         Class<?> type = primitiveToWrapper(parameterType);
         if (type == Void.class) {
             return null;
         }
         if (argument instanceof Number) {
             final Number number = (Number) argument;
             if (type == Long.class) {
                 return new Long(number.longValue());
             } else if (type == Integer.class) {
                 return new Integer(number.intValue());
             } else if (type == Byte.class) {
                 return new Byte(number.byteValue());
             } else if (type == Character.class) {
                 return new Character((char) number.intValue());
             } else if (type == Double.class) {
                 return new Double(number.doubleValue());
             } else if (type == Float.class) {
                 return new Float(number.floatValue());
             } else if (type == Short.class) {
                 return new Short(number.shortValue());
             }
         }
         if (isDuckTypeConvertable(argument.getClass(), parameterType)) {
             RubyObject rubyObject = (RubyObject) argument;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(runtime.getCurrentContext(), rubyObject, parameterType);
             }
         }
         return argument;
     }
     
     public static boolean isDuckTypeConvertable(Class providedArgumentType, Class parameterType) {
         return parameterType.isInterface() && !parameterType.isAssignableFrom(providedArgumentType) 
             && RubyObject.class.isAssignableFrom(providedArgumentType);
     }
     
     public static Object convertProcToInterface(ThreadContext context, RubyObject rubyObject, Class target) {
         Ruby runtime = context.getRuntime();
         IRubyObject javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
         IRubyObject javaInterfaceModule = Java.get_interface_module(javaUtilities, JavaClass.get(runtime, target));
         if (!((RubyModule) javaInterfaceModule).isInstance(rubyObject)) {
             rubyObject.extend(new IRubyObject[]{javaInterfaceModule});
         }
 
         if (rubyObject instanceof RubyProc) {
             // Proc implementing an interface, pull in the catch-all code that lets the proc get invoked
             // no matter what method is called on the interface
             RubyClass singletonClass = rubyObject.getSingletonClass();
-            final RubyProc proc = (RubyProc) rubyObject;
 
             singletonClass.addMethod("method_missing", new DynamicMethod(singletonClass, Visibility.PUBLIC, CallConfiguration.NO_FRAME_NO_SCOPE) {
 
                 @Override
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
+                    if (!(self instanceof RubyProc)) {
+                        throw context.getRuntime().newTypeError("interface impl method_missing for block used with non-Proc object");
+                    }
+                    RubyProc proc = (RubyProc)self;
                     IRubyObject[] newArgs;
                     if (args.length == 1) {
                         newArgs = IRubyObject.NULL_ARRAY;
                     } else {
                         newArgs = new IRubyObject[args.length - 1];
                         System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                     }
                     return proc.call(context, newArgs);
                 }
 
                 @Override
                 public DynamicMethod dup() {
                     return this;
                 }
             });
         }
         JavaObject jo = (JavaObject) RuntimeHelpers.invoke(context, rubyObject, "__jcreate_meta!");
         return jo.getValue();
     }
 
     public static Object convertArgumentToType(ThreadContext context, IRubyObject arg, Class target) {
         if (arg instanceof JavaObject) {
             return coerceJavaObjectToType(context, ((JavaObject)arg).getValue(), target);
         } else if (arg.dataGetStruct() instanceof JavaObject) {
             JavaObject innerWrapper = (JavaObject)arg.dataGetStruct();
             
             // ensure the object is associated with the wrapper we found it in,
             // so that if it comes back we don't re-wrap it
             context.getRuntime().getJavaSupport().getObjectProxyCache().put(innerWrapper.getValue(), arg);
             
             return innerWrapper.getValue();
         } else {
             switch (arg.getMetaClass().index) {
             case ClassIndex.NIL:
                 return coerceNilToType((RubyNil)arg, target);
             case ClassIndex.FIXNUM:
                 return coerceFixnumToType((RubyFixnum)arg, target);
             case ClassIndex.BIGNUM:
                 return coerceBignumToType((RubyBignum)arg, target);
             case ClassIndex.FLOAT:
                 return coerceFloatToType((RubyFloat)arg, target);
             case ClassIndex.STRING:
                 return coerceStringToType((RubyString)arg, target);
             case ClassIndex.TRUE:
                 return Boolean.TRUE;
             case ClassIndex.FALSE:
                 return Boolean.FALSE;
             case ClassIndex.TIME:
                 return ((RubyTime) arg).getJavaDate();
             default:
                 return coerceOtherToType(context, arg, target);
             }
         }
     }
     
     public static Object coerceJavaObjectToType(ThreadContext context, Object javaObject, Class target) {
         if (isDuckTypeConvertable(javaObject.getClass(), target)) {
             RubyObject rubyObject = (RubyObject) javaObject;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(context, rubyObject, target);
             }
 
             // can't be converted any more, return it
             return javaObject;
         } else {
             return javaObject;
         }
     }
     
     public static Object coerceNilToType(RubyNil nil, Class target) {
         if(target.isPrimitive()) {
             throw nil.getRuntime().newTypeError("primitives do not accept null");
         } else {
             return null;
         }
     }
     
     public static Object coerceFixnumToType(RubyFixnum fixnum, Class target) {
         if (target.isPrimitive()) {
             if (target == Integer.TYPE) {
                 return Integer.valueOf((int)fixnum.getLongValue());
             } else if (target == Double.TYPE) {
                 return Double.valueOf(fixnum.getLongValue());
             } else if (target == Byte.TYPE) {
                 return Byte.valueOf((byte)fixnum.getLongValue());
             } else if (target == Character.TYPE) {
                 return Character.valueOf((char)fixnum.getLongValue());
             } else if (target == Float.TYPE) {
                 return Float.valueOf((float)fixnum.getLongValue());
             } else if (target == Long.TYPE) {
                 return Long.valueOf(fixnum.getLongValue());
             } else if (target == Short.TYPE) {
                 return Short.valueOf((short)fixnum.getLongValue());
             }
         }
         return Long.valueOf(fixnum.getLongValue());
     }
         
     public static Object coerceBignumToType(RubyBignum bignum, Class target) {
         if (target.isPrimitive()) {
             if (target == Integer.TYPE) {
                 return Integer.valueOf((int)bignum.getLongValue());
             } else if (target == Double.TYPE) {
                 return Double.valueOf(bignum.getLongValue());
             } else if (target == Byte.TYPE) {
                 return Byte.valueOf((byte)bignum.getLongValue());
             } else if (target == Character.TYPE) {
                 return Character.valueOf((char)bignum.getLongValue());
             } else if (target == Float.TYPE) {
                 return Float.valueOf((float)bignum.getLongValue());
             } else if (target == Long.TYPE) {
                 return Long.valueOf(bignum.getLongValue());
             } else if (target == Short.TYPE) {
                 return Short.valueOf((short)bignum.getLongValue());
             }
         }
         return bignum.getValue();
     }
     
     public static Object coerceFloatToType(RubyFloat flote, Class target) {
         if (target.isPrimitive()) {
             if (target == Integer.TYPE) {
                 return Integer.valueOf((int)flote.getLongValue());
             } else if (target == Double.TYPE) {
                 return Double.valueOf(flote.getDoubleValue());
             } else if (target == Byte.TYPE) {
                 return Byte.valueOf((byte)flote.getLongValue());
             } else if (target == Character.TYPE) {
                 return Character.valueOf((char)flote.getLongValue());
             } else if (target == Float.TYPE) {
                 return Float.valueOf((float)flote.getDoubleValue());
             } else if (target == Long.TYPE) {
                 return Long.valueOf(flote.getLongValue());
             } else if (target == Short.TYPE) {
                 return Short.valueOf((short)flote.getLongValue());
             }
         }
         return Double.valueOf(flote.getDoubleValue());
     }
     
     public static Object coerceStringToType(RubyString string, Class target) {
         try {
             ByteList bytes = string.getByteList();
             return new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
         } catch (UnsupportedEncodingException uee) {
             return string.toString();
         }
     }
     
     public static Object coerceOtherToType(ThreadContext context, IRubyObject arg, Class target) {
         Ruby runtime = context.getRuntime();
         
         if (isDuckTypeConvertable(arg.getClass(), target)) {
             RubyObject rubyObject = (RubyObject) arg;
             if (!rubyObject.respondsTo("java_object")) {
                 return convertProcToInterface(context, rubyObject, target);
             }
         } else if (arg.respondsTo("to_java_object")) {
             Object javaObject = arg.callMethod(context, "to_java_object");
             if (javaObject instanceof JavaObject) {
                 runtime.getJavaSupport().getObjectProxyCache().put(((JavaObject) javaObject).getValue(), arg);
                 javaObject = ((JavaObject)javaObject).getValue();
             }
             return javaObject;
         }
 
         // it's either as converted as we can make it via above logic or it's
         // not one of the types we convert, so just pass it out as-is without wrapping
         return arg;
     }
 
     public static IRubyObject primitive_to_java(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return object;
         }
         Ruby runtime = recv.getRuntime();
         Object javaObject;
         switch (object.getMetaClass().index) {
         case ClassIndex.NIL:
             javaObject = null;
             break;
         case ClassIndex.FIXNUM:
             javaObject = new Long(((RubyFixnum) object).getLongValue());
             break;
         case ClassIndex.BIGNUM:
             javaObject = ((RubyBignum) object).getValue();
             break;
         case ClassIndex.FLOAT:
             javaObject = new Double(((RubyFloat) object).getValue());
             break;
         case ClassIndex.STRING:
             try {
                 ByteList bytes = ((RubyString) object).getByteList();
                 javaObject = new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
             } catch (UnsupportedEncodingException uee) {
                 javaObject = object.toString();
             }
             break;
         case ClassIndex.TRUE:
             javaObject = Boolean.TRUE;
             break;
         case ClassIndex.FALSE:
             javaObject = Boolean.FALSE;
             break;
         case ClassIndex.TIME:
             javaObject = ((RubyTime) object).getJavaDate();
             break;
         default:
             // it's not one of the types we convert, so just pass it out as-is without wrapping
             return object;
         }
 
         // we've found a Java type to which we've coerced the Ruby value, wrap it
         return JavaObject.wrap(runtime, javaObject);
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version 
      */
     public static IRubyObject java_to_ruby(Ruby runtime, IRubyObject object) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToUsableRubyObject(runtime, ((JavaObject) object).getValue());
         }
         return object;
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object.respondsTo("to_java_object")) {
             IRubyObject result = (JavaObject)object.dataGetStruct();
             if (result == null) {
                 result = object.callMethod(recv.getRuntime().getCurrentContext(), "to_java_object");
             }
             if (result instanceof JavaObject) {
                 recv.getRuntime().getJavaSupport().getObjectProxyCache().put(((JavaObject) result).getValue(), object);
             }
             return result;
         }
 
         return primitive_to_java(recv, object, unusedBlock);
     }
 
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
         }
 
         return object;
     }
     
     public static boolean isJavaObject(IRubyObject candidate) {
         return candidate.dataGetStruct() instanceof JavaObject;
     }
     
     public static Object unwrapJavaObject(IRubyObject object) {
         return ((JavaObject)object.dataGetStruct()).getValue();
     }
 
     private static final Pattern JAVA_PROPERTY_CHOPPER = Pattern.compile("(get|set|is)([A-Z0-9])(.*)");
     public static String getJavaPropertyName(String beanMethodName) {
         Matcher m = JAVA_PROPERTY_CHOPPER.matcher(beanMethodName);
 
         if (!m.find()) return null;
         String javaPropertyName = m.group(2).toLowerCase() + m.group(3);
         return javaPropertyName;
     }
 
     private static final Pattern CAMEL_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");    
     public static String getRubyCasedName(String javaCasedName) {
         Matcher m = CAMEL_CASE_SPLITTER.matcher(javaCasedName);
         return m.replaceAll("$1_$2").toLowerCase();
     }
 
     private static final Pattern RUBY_CASE_SPLITTER = Pattern.compile("([a-z][0-9]*)_([a-z])");    
     public static String getJavaCasedName(String javaCasedName) {
         Matcher m = RUBY_CASE_SPLITTER.matcher(javaCasedName);
         StringBuffer newName = new StringBuffer();
         if (!m.find()) {
             return null;
         }
         m.reset();
 
         while (m.find()) {
             m.appendReplacement(newName, m.group(1) + Character.toUpperCase(m.group(2).charAt(0)));
         }
 
         m.appendTail(newName);
 
         return newName.toString();
     }
     
     /**
      * Given a simple Java method name and the Java Method objects that represent
      * all its overloads, add to the given nameSet all possible Ruby names that would
      * be valid.
      * 
      * @param simpleName
      * @param nameSet
      * @param methods
      */
     public static Set<String> getRubyNamesForJavaName(String javaName, List<Method> methods) {
         String javaPropertyName = JavaUtil.getJavaPropertyName(javaName);
         String rubyName = JavaUtil.getRubyCasedName(javaName);
         Set<String> nameSet = new LinkedHashSet<String>();
         nameSet.add(javaName);
         nameSet.add(rubyName);
         String rubyPropertyName = null;
         for (Method method: methods) {
             Class<?>[] argTypes = method.getParameterTypes();
             Class<?> resultType = method.getReturnType();
             int argCount = argTypes.length;
 
             // Add property name aliases
             if (javaPropertyName != null) {
                 if (rubyName.startsWith("get_")) {
                     rubyPropertyName = rubyName.substring(4);
                     if (argCount == 0 ||                                // getFoo      => foo
                         argCount == 1 && argTypes[0] == int.class) {    // getFoo(int) => foo(int)
 
                         nameSet.add(javaPropertyName);
                         nameSet.add(rubyPropertyName);
                         if (resultType == boolean.class) {              // getFooBar() => fooBar?, foo_bar?(*)
                             nameSet.add(javaPropertyName + '?');
                             nameSet.add(rubyPropertyName + '?');
                         }
                     }
                 } else if (rubyName.startsWith("set_")) {
                     rubyPropertyName = rubyName.substring(4);
                     if (argCount == 1 && resultType == void.class) {    // setFoo(Foo) => foo=(Foo)
                         nameSet.add(javaPropertyName + '=');
                         nameSet.add(rubyPropertyName + '=');
                     }
                 } else if (rubyName.startsWith("is_")) {
                     rubyPropertyName = rubyName.substring(3);
                     if (resultType == boolean.class) {                  // isFoo() => foo, isFoo(*) => foo(*)
                         nameSet.add(javaPropertyName);
                         nameSet.add(rubyPropertyName);
                         nameSet.add(javaPropertyName + '?');
                         nameSet.add(rubyPropertyName + '?');
                     }
                 }
             } else {
                 // If not a property, but is boolean add ?-postfixed aliases.
                 if (resultType == boolean.class) {
                     // is_something?, contains_thing?
                     nameSet.add(javaName + '?');
                     nameSet.add(rubyName + '?');
                 }
             }
         }
         
         return nameSet;
     }
 
     public static JavaObject unwrapJavaObject(Ruby runtime, IRubyObject convertee, String errorMessage) {
         IRubyObject obj = convertee;
         if(!(obj instanceof JavaObject)) {
             if (obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof JavaObject)) {
                 obj = (JavaObject)obj.dataGetStruct();
             } else {
                 throw runtime.newTypeError(errorMessage);
             }
         }
         return (JavaObject)obj;
     }
 
     public static Object unwrapJavaValue(Ruby runtime, IRubyObject obj, String errorMessage) {
         if(obj instanceof JavaMethod) {
             return ((JavaMethod)obj).getValue();
         } else if(obj instanceof JavaConstructor) {
             return ((JavaConstructor)obj).getValue();
         } else if(obj instanceof JavaField) {
             return ((JavaField)obj).getValue();
         } else if(obj instanceof JavaObject) {
             return ((JavaObject)obj).getValue();
         } else if(obj.dataGetStruct() != null && (obj.dataGetStruct() instanceof IRubyObject)) {
             return unwrapJavaValue(runtime, ((IRubyObject)obj.dataGetStruct()), errorMessage);
         } else {
             throw runtime.newTypeError(errorMessage);
         }
     }
 }
