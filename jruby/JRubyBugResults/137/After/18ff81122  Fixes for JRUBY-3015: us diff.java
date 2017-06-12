diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index 3d9e07a8e0..38c7bc910a 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -542,1022 +542,1022 @@ public class Java implements Library {
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
         RuntimeHelpers.invokeAs(tc, javaProxyClass, recv, "inherited", subclass,
                 Block.NULL_BLOCK);
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
         RubyModule result = getTopLevelProxyOrPackage(context, runtime, sym.asJavaString());
 
         return result != null ? result : runtime.getNil();
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
 
         throw argumentError(recv.getRuntime().getCurrentContext(), margs[0], recv, arg_types);
     }
     
     public static int argsHashCode(Object[] a) {
         if (a == null)
             return 0;
  
         int result = 1;
  
         for (Object element : a)
             result = 31 * result + (element == null ? 0 : element.getClass().hashCode());
  
         return result;
     }
     
     public static int argsHashCode(Class[] a) {
         if (a == null)
             return 0;
  
         int result = 1;
  
         for (Class element : a)
             result = 31 * result + (element == null ? 0 : element.hashCode());
  
         return result;
     }
     
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
 
         throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, args);
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
             throw argTypesDoNotMatch(recv.getRuntime(), recv, methods, (Object[]) args);
         } else {
             cache.put(signatureCode, method);
             return method;
         }
     }
     private static ParameterTypes findCallable(ParameterTypes[] callables, CallableAcceptor acceptor, IRubyObject... args) {
         ParameterTypes bestCallable = null;
         int bestScore = -1;
         for (int k = 0; k < callables.length; k++) {
             ParameterTypes callable = callables[k];
             Class<?>[] types = callable.getParameterTypes();
 
             if (acceptor.accept(types, args)) {
                 int currentScore = getExactnessScore(types, args);
                 if (currentScore > bestScore) {
                     bestCallable = callable;
                     bestScore = currentScore;
                 }
             }
         }
         return bestCallable;
     }
     
     private static int getExactnessScore(Class<?>[] types, IRubyObject[] args) {
         int count = 0;
         for (int i = 0; i < args.length; i++) {
             if (types[i].equals(argClass(args[i]))) count++;
         }
         return count;
     }
 
     
     // NOTE: The five match methods are arity-split to avoid the cost of boxing arguments
     // when there's already a cached match. Do not condense them into a single
     // method.
     public static JavaCallable matchingCallableArityN(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject[] args, int argsLength) {
         int signatureCode = argsHashCode(args);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = (JavaCallable)findMatchingCallableForArgs(recv, cache, signatureCode, methods, args);
         return method;
     }
     
     public static JavaCallable matchingCallableArityOne(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0) {
         int signatureCode = argsHashCode(arg0);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = (JavaCallable)findMatchingCallableForArgs(recv, cache, signatureCode, methods, arg0);
         return method;
     }
     
     public static JavaCallable matchingCallableArityTwo(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1) {
         int signatureCode = argsHashCode(arg0, arg1);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = (JavaCallable)findMatchingCallableForArgs(recv, cache, signatureCode, methods, arg0, arg1);
         return method;
     }
     
     public static JavaCallable matchingCallableArityThree(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         int signatureCode = argsHashCode(arg0, arg1, arg2);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = (JavaCallable)findMatchingCallableForArgs(recv, cache, signatureCode, methods, arg0, arg1, arg2);
         return method;
     }
     
     public static JavaCallable matchingCallableArityFour(IRubyObject recv, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         int signatureCode = argsHashCode(arg0, arg1, arg2, arg3);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) method = (JavaCallable)findMatchingCallableForArgs(recv, cache, signatureCode, methods, arg0, arg1, arg2, arg3);
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
     
     private static RaiseException argTypesDoNotMatch(Ruby runtime, IRubyObject receiver, Object[] methods, Object... args) {
         ArrayList<Class<?>> argTypes = new ArrayList<Class<?>>(args.length);
         
         for (Object o : args) argTypes.add(argClassTypeError(o));
 
         return argumentError(runtime.getCurrentContext(), methods[0], receiver, argTypes);
     }
 
     private static Class argClassTypeError(Object object) {
         if (object == null) return void.class;
         if (object instanceof ConcreteJavaProxy) return ((ConcreteJavaProxy) object).getJavaClass();
 
         return object.getClass();
     }
     
     private static RaiseException argumentError(ThreadContext context, Object method, IRubyObject receiver, List<Class<?>> argTypes) {
         String methodName = (method instanceof JavaConstructor || method instanceof JavaProxyConstructor) ?
             "constructor" : ((JavaMethod) method).name().toString();
 
         return context.getRuntime().newNameError("no " + methodName + " with arguments matching " +
                 argTypes + " on object " + receiver.callMethod(context, "inspect"), null);
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
         // Also build a hashcode from all classes to use for retrieving previously-created impl
         Class[] interfaces = new Class[javaClasses.length];
         for (int i = 0; i < javaClasses.length; i++) {
             if (!(javaClasses[i] instanceof JavaClass) || !((JavaClass) javaClasses[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + javaClasses[i]);
             }
             interfaces[i] = ((JavaClass) javaClasses[i]).javaClass();
         }
 
         // hashcode is a combination of the interfaces and the Ruby class we're using
         // to implement them
         int interfacesHashCode = argsHashCode(interfaces);
         // if it's a singleton class and the real class is proc, we're doing closure conversion
         // so just use Proc's hashcode
         if (wrapper.getMetaClass().isSingleton() && wrapper.getMetaClass().getRealClass() == runtime.getProc()) {
             interfacesHashCode = 31 * interfacesHashCode + runtime.getProc().hashCode();
         } else {
             // normal new class implementing interfaces
             interfacesHashCode = 31 * interfacesHashCode + wrapper.getMetaClass().hashCode();
         }
-        String implClassName = "InterfaceImpl" + interfacesHashCode;
+        String implClassName = "InterfaceImpl" + Math.abs(interfacesHashCode);
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
diff --git a/test/testYAML.rb b/test/testYAML.rb
index cd5923d1c8..de6383d066 100644
--- a/test/testYAML.rb
+++ b/test/testYAML.rb
@@ -1,519 +1,509 @@
 require 'test/minirunit'
 require 'yaml'
 
 test_equal("str", YAML.load("!str str"))
 test_equal("str", YAML.load("--- str"))
 test_equal("str", YAML.load("---\nstr"))
 test_equal("str", YAML.load("--- \nstr"))
 test_equal("str", YAML.load("--- \n str"))
 test_equal("str", YAML.load("str"))
 test_equal("str", YAML.load(" str"))
 test_equal("str", YAML.load("\nstr"))
 test_equal("str", YAML.load("\n str"))
 test_equal("str", YAML.load('"str"'))
 test_equal("str", YAML.load("'str'"))
 test_equal("str", YAML.load(" --- 'str'"))
 test_equal("1.0", YAML.load("!str 1.0"))
 test_equal(:str, YAML.load(":str"))
 
 test_equal(47, YAML.load("47"))
 test_equal(0, YAML.load("0"))
 test_equal(-1, YAML.load("-1"))
 
 test_equal({'a' => 'b', 'c' => 'd' }, YAML.load("a: b\nc: d"))
 test_equal({'a' => 'b', 'c' => 'd' }, YAML.load("c: d\na: b\n"))
 
 test_equal({'a' => 'b', 'c' => 'd' }, YAML.load("{a: b, c: d}"))
 test_equal({'a' => 'b', 'c' => 'd' }, YAML.load("{c: d,\na: b}"))
 
 test_equal(%w(a b c), YAML.load("--- \n- a\n- b\n- c\n"))
 test_equal(%w(a b c), YAML.load("--- [a, b, c]"))
 test_equal(%w(a b c), YAML.load("[a, b, c]"))
 
 test_equal("--- str\n", "str".to_yaml)
 test_equal("--- \na: b\n", {'a'=>'b'}.to_yaml)
 test_equal("--- \n- a\n- b\n- c\n", %w(a b c).to_yaml)
 
 test_equal("--- \"1.0\"\n", "1.0".to_yaml)
 
 class TestBean
   attr_accessor :value, :key
   def initialize(v,k)
     @value=v
     @key=k
   end
   
   def ==(other)
     self.class == other.class && self.value == other.value && self.key == other.key
   end
 end
 
 test_ok(["--- !ruby/object:TestBean \nvalue: 13\nkey: 42\n",
          "--- !ruby/object:TestBean \nkey: 42\nvalue: 13\n"].include?(TestBean.new(13,42).to_yaml))
 test_equal(TestBean.new(13,42),YAML.load("--- !ruby/object:TestBean \nvalue: 13\nkey: 42\n"))
 
 TestStruct = Struct.new(:foo,:bar)
 test_ok(["--- !ruby/struct:TestStruct \nfoo: 13\nbar: 42\n","--- !ruby/struct:TestStruct \nbar: 42\nfoo: 13\n"].include?(TestStruct.new(13,42).to_yaml))
 test_equal("--- !ruby/exception:StandardError \nmessage: foobar\n", StandardError.new("foobar").to_yaml)
 
 test_equal("--- :foo\n", :foo.to_yaml)
 
-test_ok(["--- !ruby/range \nbegin: 1\nend: 3\nexcl: false\n",
-         "--- !ruby/range \nbegin: 1\nexcl: false\nend: 3\n",
-         "--- !ruby/range \nend: 3\nbegin: 1\nexcl: false\n",
-         "--- !ruby/range \nend: 3\nexcl: false\nbegin: 1\n",
-         "--- !ruby/range \nexcl: false\nbegin: 1\nend: 3\n",
-         "--- !ruby/range \nexcl: false\nend: 3\nbegin: 1\n"].include?((1..3).to_yaml))
-test_ok(["--- !ruby/range \nbegin: 1\nend: 3\nexcl: true\n",
-         "--- !ruby/range \nbegin: 1\nexcl: true\nend: 3\n",
-         "--- !ruby/range \nend: 3\nbegin: 1\nexcl: true\n",
-         "--- !ruby/range \nend: 3\ntrue: false\nbegin: 1\n",
-         "--- !ruby/range \nexcl: true\nbegin: 1\nend: 3\n",
-         "--- !ruby/range \nexcl: true\nend: 3\nbegin: 1\n"].include?((1...3).to_yaml))
+test_equal(["--- !ruby/range ", "begin: 1", "end: 3", "excl: false"], (1..3).to_yaml.split("\n").sort)
+test_equal(["--- !ruby/range ", "begin: 1", "end: 3", "excl: true"], (1...3).to_yaml.split("\n").sort)
 
 test_equal("--- !ruby/regexp /^abc/\n", /^abc/.to_yaml)
 
 test_equal("--- 1982-05-03 15:32:44 Z\n",Time.utc(1982,05,03,15,32,44).to_yaml)
 test_equal("--- 2005-05-03\n",Date.new(2005,5,3).to_yaml)
 
 test_equal("--- .NaN\n",(0.0/0.0).to_yaml)
 test_equal("--- .Inf\n",(1.0/0.0).to_yaml)
 test_equal("--- -.Inf\n",(-1.0/0.0).to_yaml)
 test_equal("--- 0.0\n", (0.0).to_yaml)
 test_equal("--- 0\n", 0.to_yaml)
 
 test_equal("--- true\n", true.to_yaml)
 test_equal("--- false\n", false.to_yaml)
 
 test_equal("--- \n", nil.to_yaml)
 
 test_equal("--- :foo\n", :foo.to_yaml)
 
 # JRUBY-718
 test_equal("--- \"\"\n", ''.to_yaml)
 test_equal('', YAML.load("---\n!str"))
 
 # JRUBY-719
 test_equal('---', YAML.load("--- ---\n"))
 test_equal('---', YAML.load("---"))
 
 astr = "abcde"
 shared = astr[2..-1]
 test_equal('cde', YAML.load(shared))
 test_equal("--- cde\n", shared.to_yaml)
 
 # JRUBY-1026
 a = "one0.1"
 b = a[3..-1]
 test_equal("--- \"0.1\"\n", YAML.dump(b))
 
 # JRUBY-1169
 class HashWithIndifferentAccess < Hash
 end
 
 hash = HashWithIndifferentAccess.new
 hash['kind'] = 'human'
 need_to_be_serialized = {:first => 'something', :second_params => hash}
 a = {:x => need_to_be_serialized.to_yaml}
 test_equal need_to_be_serialized, YAML.load(YAML.load(a.to_yaml)[:x])
 
 # JRUBY-1220 - make sure all three variations work
 bad_text = " A\nR"
 dump = YAML.dump({'text' => bad_text})
 loaded = YAML.load(dump)
 test_equal bad_text, loaded['text']
 
 bad_text = %{
  A
 R}
 dump = YAML.dump({'text' => bad_text})
 loaded = YAML.load(dump)
 test_equal bad_text, loaded['text']
 
 bad_text = %{
  ActiveRecord::StatementInvalid in ProjectsController#confirm_delete
 RuntimeError: ERROR	C23503	Mupdate or delete on "projects" violates foreign 
     }
 dump = YAML.dump({'text' => bad_text})
 loaded = YAML.load(dump)
 test_equal bad_text, loaded['text']
 
 string = <<-YAML
 outer
   property1: value1
   additional:
   - property2: value2
     color: green
     data: SELECT 'xxxxxxxxxxxxxxxxxxx', COUNT(*) WHERE xyzabc = 'unk'
     combine: overlay-bottom
 YAML
 test_equal string, YAML.load(YAML.dump(string))
 
 ## TODO: implement real fuzz testing of YAML round tripping here
 
 text = " "*80 + "\n" + " "*30
 test_equal text, YAML.load(YAML.dump(text))
 
 text = <<-YAML
   - label: New
     color: green
     data: SELECT 'Iteration Scheduled', COUNT(*) WHERE Status = 'New'
     combine: overlay-bottom
   - label: Open
     color: pink
     data: SELECT 'Iteration Scheduled', COUNT(*) WHERE Status = 'Open'
     combine: overlay-bottom
   - label: Ready for Development
     color: yellow
     data: SELECT 'Iteration Scheduled', COUNT(*) WHERE Status = 'Ready for Development'
     combine: overlay-bottom
     color: blue
     data: SELECT 'Iteration Scheduled', COUNT(*) WHERE Status = 'Complete'
     combine: overlay-bottom
   - label: Other statuses
     color: red
     data: SELECT 'Iteration Scheduled', COUNT(*)
                     combine: total
 YAML
 
 test_equal text, YAML.load(YAML.dump(text))
 
 text = <<-YAML
 stack-bar-chart
   conditions: 'Release' in (R1) and not 'Iteration Scheduled' = null
   labels: SELECT DISTINCT 'Iteration Scheduled' ORDER BY 'Iteration Scheduled'
   cumulative: true
   series:
   - label: New
     color: green
     data: SELECT 'Iteration Scheduled', COUNT(*) WHERE Status = 'New'
     combine: overlay-bottom
   - label: Open
     color: pink
     data: SELECT 'Iteration Scheduled', COUNT(*) WHERE Status = 'Open'
     combine: overlay-bottom
   - label: Ready for Development
     color: yellow
     data: SELECT 'Iteration Scheduled', COUNT(*) WHERE Status = 'Ready for Development'
     combine: overlay-bottom
   - label: Complete
     color: blue
     data: SELECT 'Iteration Scheduled', COUNT(*) WHERE Status = 'Complete'
     combine: overlay-bottom
   - label: Other statuses
     color: red
     data: SELECT 'Iteration Scheduled', COUNT(*)
     combine: total
 YAML
 
 test_equal text, YAML.load(YAML.dump(text))
 
 text = <<YAML
 valid_key:
 key1: value
 invalid_key
 akey: blah
 YAML
 
 test_exception(ArgumentError) do 
   YAML.load(text)
 end
 
 def roundtrip(text)
   test_equal text, YAML.load(YAML.dump(text))
 end
 
 roundtrip("C VW\205\v\321XU\346")
 roundtrip("\n8 xwKmjHG")
 roundtrip("1jq[\205qIB\ns")
 roundtrip("\rj\230fso\304\nEE")
 roundtrip("ks]qkYM\2073Un\317\nL\346Yp\204 CKMfFcRDFZ\vMNk\302fQDR<R\v \314QUa\234P\237s aLJnAu \345\262Wqm_W\241\277J\256ILKpPNsMPuok")
 
 def fuzz_roundtrip(str)
   out = YAML.load(YAML.dump(str))
   test_equal str, out
 end
 
 values = (1..255).to_a
 more = ('a'..'z').to_a + ('A'..'Z').to_a
 blanks = [' ', "\t", "\n"]
 
 types = [more*10 + blanks*2, values + more*10 + blanks*2, values + more*10 + blanks*20]
 sizes = [10, 81, 214]
 
 errors = []
 types.each do |t|
   sizes.each do |s|
     1000.times do |vv|
       val = ""
       s.times do 
         val << t[rand(t.length)]
       end
       fuzz_roundtrip(val)
     end      
   end
 end
 
 test_no_exception do 
   YAML.load_file("test/yaml/does_not_work.yml")
 end
 
 roundtrip :"1"
 
 
 # Fix for JRUBY-1471
 class YamlTest
   def initialize
     @test = Hash.new
     @test["hello"] = "foo"
   end
 end
 
 list = [YamlTest.new, YamlTest.new, YamlTest.new]
 test_equal 3, list.map{ |ll| ll.object_id }.uniq.length
 list2 = YAML.load(YAML.dump(list))
 test_equal 3, list2.map{ |ll| ll.object_id }.uniq.length
 
 # JRUBY-1659
 YAML.load("{a: 2007-01-01 01:12:34}")
 
 # JRUBY-1765
 test_equal Date.new(-1,1,1), YAML.load(Date.new(-1,1,1).to_yaml)
 
 # JRUBY-1766
 test_ok YAML.load(Time.now.to_yaml).instance_of?(Time)
 test_ok YAML.load("2007-01-01 01:12:34").instance_of?(String)
 test_ok YAML.load("2007-01-01 01:12:34.0").instance_of?(String)
 test_ok YAML.load("2007-01-01 01:12:34 +00:00").instance_of?(Time)
 test_ok YAML.load("2007-01-01 01:12:34.0 +00:00").instance_of?(Time)
 test_ok YAML.load("{a: 2007-01-01 01:12:34}")["a"].instance_of?(String)
 
 # JRUBY-1898
 val = YAML.load(<<YAML)
 ---
 - foo
 - foo
 - [foo]
 - [foo]
 - {foo: foo}
 - {foo: foo}
 YAML
 
 test_ok val[0].object_id != val[1].object_id
 test_ok val[2].object_id != val[3].object_id
 test_ok val[4].object_id != val[5].object_id
 
 # JRUBY-1911
 val = YAML.load(<<YAML)
 ---
 foo: { bar }
 YAML
 
 test_equal({"foo" => {"bar" => nil}}, val)
 
 # JRUBY-1756
 # This is almost certainly invalid YAML. but MRI handles it...
 val = YAML.load(<<YAML)
 ---
 default: –
 - a
 YAML
 
 test_equal({"default" => ['a']}, val)
 
 if defined?(JRUBY_VERSION)
   # JRUBY-1903
   test_equal(<<YAML_OUT, YAML::JvYAML::Scalar.new("tag:yaml.org,2002:str","foobar",'').to_str)
 --- foobar
 YAML_OUT
 
   test_equal(<<YAML_OUT, YAML::JvYAML::Scalar.new("tag:yaml.org,2002:str","foobar",'').to_s)
 --- foobar
 YAML_OUT
 
   test_equal(<<YAML_OUT, YAML::JvYAML::Seq.new("tag:yaml.org,2002:seq",[YAML::JvYAML::Scalar.new("tag:yaml.org,2002:str","foobar",'')],'').to_str)
 --- [foobar]
 
 YAML_OUT
 
   test_equal(<<YAML_OUT, YAML::JvYAML::Seq.new("tag:yaml.org,2002:seq",[YAML::JvYAML::Scalar.new("tag:yaml.org,2002:str","foobar",'')],'').to_s)
 --- [foobar]
 
 YAML_OUT
 
   test_equal(<<YAML_OUT, YAML::JvYAML::Map.new("tag:yaml.org,2002:map",{YAML::JvYAML::Scalar.new("tag:yaml.org,2002:str","a",'') => YAML::JvYAML::Scalar.new("tag:yaml.org,2002:str","b",'')},'').to_str)
 --- {a: b}
 
 YAML_OUT
 
   test_equal(<<YAML_OUT, YAML::JvYAML::Map.new("tag:yaml.org,2002:map",{YAML::JvYAML::Scalar.new("tag:yaml.org,2002:str","a",'') => YAML::JvYAML::Scalar.new("tag:yaml.org,2002:str","b",'')},'').to_s)
 --- {a: b}
 
 YAML_OUT
 end
 
 # JRUBY-1978, scalars can start with , if it's not ambigous
 test_equal(",a", YAML.load("--- \n,a"))
 
 # Make sure that overriding to_yaml always throws an exception unless it returns the correct thing
 
 class TestYamlFoo
   def to_yaml(*args)
     "foo"
   end
 end
 
 test_exception(TypeError) do 
   { :foo => TestYamlFoo.new }.to_yaml
 end
 
 # JRUBY-2019, handle tagged_classes, yaml_as and so on a bit better
 
 test_equal({
              "tag:yaml.org,2002:omap"=>YAML::Omap, 
              "tag:yaml.org,2002:pairs"=>YAML::Pairs, 
              "tag:yaml.org,2002:set"=>YAML::Set, 
              "tag:yaml.org,2002:timestamp#ymd"=>Date, 
              "tag:yaml.org,2002:bool#yes"=>TrueClass, 
              "tag:yaml.org,2002:int"=>Integer, 
              "tag:yaml.org,2002:timestamp"=>Time, 
              "tag:yaml.org,2002:binary"=>String, 
              "tag:yaml.org,2002:str"=>String, 
              "tag:yaml.org,2002:map"=>Hash, 
              "tag:yaml.org,2002:null"=>NilClass, 
              "tag:yaml.org,2002:bool#no"=>FalseClass, 
              "tag:yaml.org,2002:seq"=>Array, 
              "tag:yaml.org,2002:float"=>Float,
              "tag:ruby.yaml.org,2002:sym"=>Symbol, 
              "tag:ruby.yaml.org,2002:object"=>Object, 
              "tag:ruby.yaml.org,2002:hash"=>Hash, 
              "tag:ruby.yaml.org,2002:time"=>Time, 
              "tag:ruby.yaml.org,2002:symbol"=>Symbol, 
              "tag:ruby.yaml.org,2002:string"=>String, 
              "tag:ruby.yaml.org,2002:regexp"=>Regexp, 
              "tag:ruby.yaml.org,2002:range"=>Range, 
              "tag:ruby.yaml.org,2002:array"=>Array, 
              "tag:ruby.yaml.org,2002:exception"=>Exception, 
              "tag:ruby.yaml.org,2002:struct"=>Struct, 
            },
            YAML::tagged_classes)
 
 
 # JRUBY-2083
 
 test_equal({'foobar' => '>= 123'}, YAML.load("foobar: >= 123"))
 
 # JRUBY-2135
 test_equal({'foo' => 'bar'}, YAML.load("---\nfoo: \tbar"))
 
 # JRUBY-1911
 test_equal({'foo' => {'bar' => nil, 'qux' => nil}}, YAML.load("---\nfoo: {bar, qux}"))
 
 # JRUBY-2323
 class YAMLTestException < Exception;end
 class YAMLTestString < String; end
 test_equal('--- !str:YAMLTestString', YAMLTestString.new.to_yaml.strip)
 test_equal(YAMLTestString.new, YAML::load('--- !str:YAMLTestString'))
 
 test_equal(<<EXCEPTION_OUT, YAMLTestException.new.to_yaml) 
 --- !ruby/exception:YAMLTestException 
 message: YAMLTestException
 EXCEPTION_OUT
 
 test_equal(YAMLTestException.new.inspect, YAML::load(YAMLTestException.new.to_yaml).inspect)
 
 # JRUBY-2409
 test_equal("*.rb", YAML::load("---\n*.rb"))
 test_equal("&.rb", YAML::load("---\n&.rb"))
 
 # JRUBY-2443
 a_str = "foo"
 a_str.instance_variable_set :@bar, "baz"
 
 test_equal("--- !str \nstr: foo\n'@bar': baz\n", a_str.to_yaml)
 test_equal "baz", YAML.load(a_str.to_yaml).instance_variable_get(:@bar)
 
 test_equal :"abc\"flo", YAML.load("---\n:\"abc\\\"flo\"")
 
 # JRUBY-2579
 test_equal [:year], YAML.load("---\n[:year]")
 
 test_equal({
              'date_select' => { 'order' => [:year, :month, :day] }, 
              'some' => { 
                'id' => 1, 
                'name' => 'some', 
                'age' => 16}}, YAML.load(<<YAML))
 date_select: 
   order: [:year, :month, :day]
 some:
     id: 1
     name: some
     age: 16 
 YAML
 
 
 # Test Scanner exception
 old_debug, $DEBUG = $DEBUG, true
 begin
   YAML.load("!<abc")
   test_ok false
 rescue Exception => e
   test_ok e.to_s =~ /0:5\(5\)/
 ensure
   $DEBUG = old_debug
 end
 
 # Test Parser exception
 old_debug, $DEBUG = $DEBUG, true
 begin
   YAML.load("%YAML 2.0")
   test_ok false
 rescue Exception => e
   test_ok e.to_s =~ /0:0\(0\)/ && e.to_s =~ /0:9\(9\)/
 ensure
   $DEBUG = old_debug
 end
 
 # Test Composer exception
 old_debug, $DEBUG = $DEBUG, true
 begin
   YAML.load("*foobar")
   test_ok false
 rescue Exception => e
   test_ok e.to_s =~ /0:0\(0\)/ && e.to_s =~ /0:7\(7\)/
 ensure
   $DEBUG = old_debug
 end
 
 
 # JRUBY-2754
 obj = Object.new
 objects1 = [obj, obj]
 test_ok objects1[0].object_id == objects1[1].object_id
 
 objects2 = YAML::load objects1.to_yaml
 test_ok objects2[0].object_id == objects2[1].object_id
 
 # JRUBY-2192
 
 class FooYSmith < Array; end
 
 obj = YAML.load(<<YAMLSTR)
 --- !ruby/array:FooYSmith
 - val
 - val2
 YAMLSTR
 
 test_equal FooYSmith, obj.class
 
 
 class FooXSmith < Hash; end
 
 obj = YAML.load(<<YAMLSTR)
 --- !ruby/hash:FooXSmith
 key: value
 otherkey: othervalue
 YAMLSTR
 
 test_equal FooXSmith, obj.class
