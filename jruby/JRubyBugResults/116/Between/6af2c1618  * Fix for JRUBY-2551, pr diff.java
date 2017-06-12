diff --git a/src/org/jruby/java/MiniJava.java b/src/org/jruby/java/MiniJava.java
index 000652201c..d424ad1a44 100644
--- a/src/org/jruby/java/MiniJava.java
+++ b/src/org/jruby/java/MiniJava.java
@@ -1,1042 +1,1037 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.java;
 
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.compiler.util.HandleFactory;
 import org.jruby.compiler.util.HandleFactory.Handle;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import static org.jruby.util.CodegenUtils.*;
 import org.jruby.util.IdUtil;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import static org.objectweb.asm.Opcodes.*;
 
 /**
  *
  * @author headius
  */
 public class MiniJava implements Library {
     private static final boolean DEBUG = false;
     
     public void load(Ruby runtime, boolean wrap) {
         runtime.getErr().print("Warning: minijava is experimental and subject to change\n");
         
         runtime.getKernel().defineAnnotatedMethods(MiniJava.class);
 
         // load up object and add a few useful methods
         RubyModule javaObject = getMirrorForClass(runtime, Object.class);
 
         javaObject.addMethod("to_s", new JavaMethod.JavaMethodZero(javaObject, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return context.getRuntime().newString(((JavaObjectWrapper) self).object.toString());
             }
         });
 
         javaObject.addMethod("hash", new JavaMethod.JavaMethodZero(javaObject, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return self.getRuntime().newFixnum(((JavaObjectWrapper) self).object.hashCode());
             }
         });
 
         javaObject.addMethod("==", new JavaMethod.JavaMethodOne(javaObject, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                 if (arg instanceof JavaObjectWrapper) {
                     return context.getRuntime().newBoolean(((JavaObjectWrapper) self).object.equals(((JavaObjectWrapper) arg).object));
                 } else {
                     return context.getRuntime().getFalse();
                 }
             }
         });
 
         // open up the 'to_java' and 'as' coercion methods on Ruby Objects, via Kernel
         RubyModule rubyKernel = runtime.getKernel();
         rubyKernel.addModuleFunction("to_java", new JavaMethod.JavaMethodZeroOrOne(rubyKernel, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return ((RubyObject) self).to_java();
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                 return ((RubyObject) self).as(getJavaClassFromObject(arg));
             }
         });
     }
     
     @JRubyMethod(name = "new_class", rest = true, module = true)
     public static IRubyObject new_class(ThreadContext context, IRubyObject self, IRubyObject[] interfaces) {
         Class[] javaInterfaces = new Class[interfaces.length];
         for (int i = 0; i < interfaces.length; i++) {
             javaInterfaces[i] = getJavaClassFromObject(interfaces[i]);
         }
         
         return createImplClass(javaInterfaces, context.getRuntime(), "I" + System.currentTimeMillis());
     }
 
     @JRubyMethod(name = "import", module = true)
     public static IRubyObject rb_import(ThreadContext context, IRubyObject self, IRubyObject name) {
         String className = name.toString();
         try {
             Class cls = findClass(context.getRuntime().getJRubyClassLoader(), className);
 
             RubyModule namespace;
             if (self instanceof RubyModule) {
                 namespace = (RubyModule) self;
             } else {
                 namespace = self.getMetaClass().getRealClass();
             }
 
             namespace.defineConstant(cls.getSimpleName(), getMirrorForClass(context.getRuntime(), cls));
 
             return context.getRuntime().getNil();
         } catch (Exception e) {
             if (context.getRuntime().getDebug().isTrue()) {
                 e.printStackTrace();
             }
             throw context.getRuntime().newTypeError("Could not find class " + className + ", exception: " + e);
         }
     }
 
     @JRubyMethod(name = "import", module = true)
     public static IRubyObject rb_import(ThreadContext context, IRubyObject self, IRubyObject name, IRubyObject as) {
         String className = name.toString();
         try {
             Class cls = findClass(context.getRuntime().getJRubyClassLoader(), className);
 
             RubyModule namespace;
             if (self instanceof RubyModule) {
                 namespace = (RubyModule) self;
             } else {
                 namespace = self.getMetaClass().getRealClass();
             }
 
             namespace.defineConstant(as.toString(), getMirrorForClass(context.getRuntime(), cls));
 
             return context.getRuntime().getNil();
         } catch (Exception e) {
             if (context.getRuntime().getDebug().isTrue()) {
                 e.printStackTrace();
             }
             throw context.getRuntime().newTypeError("Could not find class " + className + ", exception: " + e);
         }
     }
 
     public static RubyClass createImplClass(Class[] superTypes, Ruby ruby, String name) {
         String[] superTypeNames = new String[superTypes.length];
         Map<String, List<Method>> simpleToAll = new HashMap<String, List<Method>>();
         for (int i = 0; i < superTypes.length; i++) {
             superTypeNames[i] = p(superTypes[i]);
             
             for (Method method : superTypes[i].getDeclaredMethods()) {
                 List<Method> methods = simpleToAll.get(method.getName());
                 if (methods == null) simpleToAll.put(method.getName(), methods = new ArrayList<Method>());
                 methods.add(method);
             }
         }
         
         Class newClass = defineImplClass(ruby, name, superTypeNames, simpleToAll);
         RubyClass rubyCls = populateImplClass(ruby, newClass, simpleToAll);
         
         return rubyCls;
     }
     
     public static Class defineImplClass(Ruby ruby, String name, String[] superTypeNames, Map<String, List<Method>> simpleToAll) {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         
         // construct the class, implementing all supertypes
         cw.visit(V1_5, ACC_PUBLIC | ACC_SUPER, name, null, p(Object.class), superTypeNames);
         
         // fields needed for dispatch and such
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "ruby", ci(Ruby.class), null, null).visitEnd();
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "rubyClass", ci(RubyClass.class), null, null).visitEnd();
         cw.visitField(ACC_PRIVATE | ACC_FINAL, "self", ci(IRubyObject.class), null, null).visitEnd();
         
         // create constructor
         SkinnyMethodAdapter initMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", sig(void.class), null, null));
         initMethod.aload(0);
         initMethod.invokespecial(p(Object.class), "<init>", sig(void.class));
         
         // wrap self and store the wrapper
         initMethod.aload(0);
         initMethod.getstatic(name, "ruby", ci(Ruby.class));
         initMethod.aload(0);
         initMethod.invokestatic(p(MiniJava.class), "javaToRuby", sig(IRubyObject.class, Ruby.class, Object.class));
         initMethod.putfield(name, "self", ci(IRubyObject.class));
         
         // end constructor
         initMethod.voidreturn();
         initMethod.end();
         
         // start setup method
         SkinnyMethodAdapter setupMethod = new SkinnyMethodAdapter(cw.visitMethod(ACC_STATIC | ACC_PUBLIC | ACC_SYNTHETIC, "__setup__", sig(void.class, RubyClass.class), null, null));
         setupMethod.start();
         
         // set RubyClass
         setupMethod.aload(0);
         setupMethod.dup();
         setupMethod.putstatic(name, "rubyClass", ci(RubyClass.class));
         
         // set Ruby
         setupMethod.invokevirtual(p(RubyClass.class), "getClassRuntime", sig(Ruby.class));
         setupMethod.putstatic(name, "ruby", ci(Ruby.class));
         
         // for each simple method name, implement the complex methods, calling the simple version
         for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
             String simpleName = entry.getKey();
             
             // all methods dispatch to the simple version by default, which is method_missing normally
             cw.visitField(ACC_STATIC | ACC_PUBLIC | ACC_VOLATILE, simpleName, ci(DynamicMethod.class), null, null).visitEnd();
             
             for (Method method : entry.getValue()) {
                 Class[] paramTypes = method.getParameterTypes();
                 Class returnType = method.getReturnType();
                 
                 SkinnyMethodAdapter mv = new SkinnyMethodAdapter(
                         cw.visitMethod(ACC_PUBLIC, simpleName, sig(returnType, paramTypes), null, null));
                 mv.start();
                 String fieldName = mangleMethodFieldName(simpleName, paramTypes);
                 
                 // try specific name first, falling back on simple name
                 Label dispatch = new Label();
                 cw.visitField(ACC_STATIC | ACC_PUBLIC | ACC_VOLATILE, fieldName, ci(DynamicMethod.class), null, null).visitEnd();
                 mv.getstatic(name, fieldName, ci(DynamicMethod.class));
                 mv.dup();
                 mv.ifnonnull(dispatch);
                 mv.pop();
                 mv.getstatic(name, simpleName, ci(DynamicMethod.class));
                 mv.dup();
                 mv.ifnonnull(dispatch);
                 mv.pop();
                 mv.getstatic(name, "rubyClass", ci(RubyClass.class));
                 mv.ldc("method_missing");
                 mv.invokevirtual(p(RubyClass.class), "searchMethod", sig(DynamicMethod.class, String.class));
                 mv.label(dispatch);
                 
                 // get current context
                 mv.getstatic(name, "ruby", ci(Ruby.class));
                 mv.invokevirtual(p(Ruby.class), "getCurrentContext", sig(ThreadContext.class));
                 
                 // load self, class, and name
                 mv.aload(0);
                 mv.getfield(name, "self", ci(IRubyObject.class));
                 mv.getstatic(name, "rubyClass", ci(RubyClass.class));
                 mv.ldc(simpleName);
                 
                 // load arguments into IRubyObject[] for dispatch
                 if (method.getParameterTypes().length != 0) {
                     mv.pushInt(method.getParameterTypes().length);
                     mv.anewarray(p(IRubyObject.class));
                     
                     for (int i = 0; i < paramTypes.length; i++) {
                         mv.dup();
                         mv.pushInt(i);
                         // convert to IRubyObject
                         mv.getstatic(name, "ruby", ci(Ruby.class));
                         mv.aload(i + 1);
                         mv.invokestatic(p(MiniJava.class), "javaToRuby", sig(IRubyObject.class, Ruby.class, Object.class));
                         mv.aastore();
                     }
                 } else {
                     mv.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
                 }
                 
                 // load null block
                 mv.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
                 
                 // invoke method
                 mv.invokevirtual(p(DynamicMethod.class), "call", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class));
                 
                 // if we expect a return value, unwrap it
                 if (method.getReturnType() != void.class) {
                     mv.invokestatic(p(MiniJava.class), "rubyToJava", sig(Object.class, IRubyObject.class));
                     mv.checkcast(p(returnType));
 
                     mv.areturn();
                 } else {
                     mv.voidreturn();
                 }
                 mv.end();
             }
         }
         
         // end setup method
         setupMethod.voidreturn();
         setupMethod.end();
         
         // end class
         cw.visitEnd();
         
         // create the class
         byte[] bytes = cw.toByteArray();
         Class newClass = ruby.getJRubyClassLoader().defineClass(name, cw.toByteArray());
         
         if (DEBUG) {
             FileOutputStream fos = null;
             try {
                 fos = new FileOutputStream(name + ".class");
                 fos.write(bytes);
             } catch (IOException ioe) {
                 ioe.printStackTrace();
             } finally {
                 try {fos.close();} catch (Exception e) {}
             }
         }
         
         return newClass;
     }
 
     public static RubyClass populateImplClass(Ruby ruby, Class newClass, Map<String, List<Method>> simpleToAll) {
         RubyClass rubyCls = (RubyClass)getMirrorForClass(ruby, newClass);
         
         // setup the class
         try {
             newClass.getMethod("__setup__", new Class[]{RubyClass.class}).invoke(null, new Object[]{rubyCls});
         } catch (IllegalAccessException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (IllegalArgumentException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (InvocationTargetException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         } catch (NoSuchMethodException ex) {
             throw error(ruby, ex, "Could not setup class: " + newClass);
         }
 
         // now, create a method_added that can replace the DynamicMethod fields as they're redefined
         final Map<String, Field> allFields = new HashMap<String, Field>();
         try {
             for (Map.Entry<String, List<Method>> entry : simpleToAll.entrySet()) {
                 String simpleName = entry.getKey();
 
                 Field simpleField = newClass.getField(simpleName);
                 allFields.put(simpleName, simpleField);
 
                 for (Method method : entry.getValue()) {
                     String complexName = simpleName + prettyParams(method.getParameterTypes());
                     String fieldName = mangleMethodFieldName(simpleName, method.getParameterTypes());
                     allFields.put(complexName, newClass.getField(fieldName));
                 }
             }
         } catch (IllegalArgumentException ex) {
             throw error(ruby, ex, "Could not prepare method fields: " + newClass);
         } catch (NoSuchFieldException ex) {
             throw error(ruby, ex, "Could not prepare method fields: " + newClass);
         }
 
         DynamicMethod method_added = new JavaMethod(rubyCls.getSingletonClass(), Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 RubyClass selfClass = (RubyClass)self;
                 Ruby ruby = selfClass.getClassRuntime();
                 String methodName = args[0].asJavaString();
                 Field field = allFields.get(methodName);
 
                 if (field == null) {
                     // do nothing, it's a non-impl method
                 } else {
                     try {
                         field.set(null, selfClass.searchMethod(methodName));
                     } catch (IllegalAccessException iae) {
                         throw error(ruby, iae, "Could not set new method into field: " + selfClass + "." + methodName);
                     } catch (IllegalArgumentException iae) {
                         throw error(ruby, iae, "Could not set new method into field: " + selfClass + "." + methodName);
                     }
                 }
 
                 return context.getRuntime().getNil();
             }
         };
         rubyCls.getSingletonClass().addMethod("method_added", method_added);
         
         return rubyCls;
     }
     
     protected static String mangleMethodFieldName(String baseName, Class[] paramTypes) {
         String fieldName = baseName + prettyParams(paramTypes);
         fieldName = fieldName.replace('.', '\\');
         
         return fieldName;
     }
     
     protected static Class findClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
         if (className.indexOf('.') == -1 && Character.isLowerCase(className.charAt(0))) {
             // probably a primitive
             switch (className.charAt(0)) {
             case 'b':
                 return byte.class;
             case 's':
                 return short.class;
             case 'c':
                 return char.class;
             case 'i':
                 return int.class;
             case 'l':
                 return long.class;
             case 'f':
                 return float.class;
             case 'd':
                 return double.class;
             default:
                 return classLoader.loadClass(className);
             }
         } else {
             return classLoader.loadClass(className);
         }
     }
     static Map<Class, RubyModule> classMap = new HashMap<Class, RubyModule>();
 
     public static RubyModule getMirrorForClass(Ruby ruby, Class cls) {
         if (cls == null) {
             return ruby.getObject();
         }
 
         RubyModule rubyCls = classMap.get(cls);
 
         if (rubyCls == null) {
             rubyCls = createMirrorForClass(ruby, cls);
 
             classMap.put(cls, rubyCls);
             populateMirrorForClass(rubyCls, cls);
             rubyCls = classMap.get(cls);
         }
 
         return rubyCls;
     }
 
     protected static RubyModule createMirrorForClass(Ruby ruby, Class cls) {
         if (cls.isInterface()) {
             // interfaces are handled as modules
             RubyModule rubyMod = RubyModule.newModule(ruby);
             return rubyMod;
         } else {
             // construct the mirror class and parent classes
             RubyClass rubyCls = RubyClass.newClass(ruby, (RubyClass) getMirrorForClass(ruby, cls.getSuperclass()));
             return rubyCls;
         }
     }
 
     protected static void populateMirrorForClass(RubyModule rubyMod, final Class cls) {
         Ruby ruby = rubyMod.getRuntime();
 
         // set the full name
         rubyMod.setBaseName(cls.getCanonicalName());
 
         // include all interfaces
         Class[] interfaces = cls.getInterfaces();
         for (Class ifc : interfaces) {
             rubyMod.includeModule(getMirrorForClass(ruby, ifc));
         }
 
         // if it's an inner class and it's not public, we can't access it;
         // skip population of declared elements
         if (cls.getEnclosingClass() != null && !Modifier.isPublic(cls.getModifiers())) {
             return;
         }
 
         RubyModule rubySing = rubyMod.getSingletonClass();
 
         // if it's an array, only add methods for aref, aset, and length
         if (cls.isArray()) {
             populateMirrorForArrayClass(rubyMod, cls);
         } else {
             populateDeclaredMethods(rubyMod, cls, true);
 
             populateConstructors(rubySing, cls);
 
             populateArrayConstructors(rubySing);
 
             populateFields(rubyMod, cls);
         }
 
         populateSpecialMethods(rubyMod, rubySing, cls);
     }
     private static void populateArrayConstructors(RubyModule rubySing) {
         // add array construction methods
         rubySing.addMethod("[]", new JavaMethod.JavaMethodOneOrTwoOrThree(rubySing, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                 Class javaClass = getJavaClassFromObject(self);
                 int size = RubyFixnum.fix2int(arg.convertToInteger());
                 return javaToRuby(context.getRuntime(), Array.newInstance(javaClass, size));
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
                 Class javaClass = getJavaClassFromObject(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 int y = RubyFixnum.fix2int(arg1.convertToInteger());
                 return javaToRuby(context.getRuntime(), Array.newInstance(javaClass, new int[]{x, y}));
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
                 Class javaClass = getJavaClassFromObject(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 int y = RubyFixnum.fix2int(arg1.convertToInteger());
                 int z = RubyFixnum.fix2int(arg2.convertToInteger());
                 return javaToRuby(context.getRuntime(), Array.newInstance(javaClass, new int[]{x, y, z}));
             }
         });
     }
 
     private static void populateConstructors(RubyModule rubySing, final Class cls) {
         final Ruby ruby = rubySing.getRuntime();
         
-        // add all constructors
+        // add all public constructors (note: getConstructors only returns public ones)
         Constructor[] constructors = cls.getConstructors();
         for (final Constructor constructor : constructors) {
-            // only public constructors
-            if (!Modifier.isPublic(constructor.getModifiers())) {
-                continue;
-            }
-
             DynamicMethod dynMethod;
             if (constructor.getParameterTypes().length == 0) {
                 dynMethod = new JavaMethod.JavaMethodZero(rubySing, Visibility.PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                         try {
                             return javaToRuby(context.getRuntime(), constructor.newInstance());
                         } catch (InstantiationException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (IllegalAccessException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (IllegalArgumentException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (InvocationTargetException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         }
                     }
                 };
             } else {
                 dynMethod = new JavaMethod.JavaMethodNoBlock(rubySing, Visibility.PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] rubyArgs) {
                         Object[] args = new Object[rubyArgs.length];
 
                         for (int i = 0; i < args.length; i++) {
                             args[i] = rubyToJava(rubyArgs[i]);
                         }
                         try {
                             return javaToRuby(ruby, constructor.newInstance(args));
                         } catch (InstantiationException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (IllegalAccessException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (IllegalArgumentException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         } catch (InvocationTargetException ex) {
                             if (ruby.getDebug().isTrue()) ex.printStackTrace();
                             throw ruby.newTypeError("Could not instantiate " + cls.getCanonicalName() + " using " + prettyParams(constructor.getParameterTypes()));
                         }
                     }
                 };
             }
 
             // if not already defined, we add a 'new' that guesses at which signature to use
             // TODO: just adding first one right now...add in signature-guessing logic
             if (rubySing.getMethods().get("new") == null) {
                 rubySing.addMethod("new", dynMethod);
             }
 
             // add 'new' with full signature, so it's guaranteed to be directly accessible
             // TODO: no need for this to be a full, formal JVM signature
             rubySing.addMethod("new" + prettyParams(constructor.getParameterTypes()), dynMethod);
         }
     }
 
     private static void populateDeclaredMethods(RubyModule rubyMod, final Class cls, boolean includeStatic) throws SecurityException {
         // add all instance and static methods
         Method[] methods = cls.getDeclaredMethods();
         for (final Method method : methods) {
             String name = method.getName();
             RubyModule target;
 
             // only public methods
             if (!Modifier.isPublic(method.getModifiers())) {
                 continue;
             }
 
             if (Modifier.isStatic(method.getModifiers())) {
                 if (!includeStatic) continue; // only include static methods if specified
                 
                 target = rubyMod.getSingletonClass();
             } else {
                 target = rubyMod;
             }
 
             JavaMethodFactory factory = getMethodFactory(method.getReturnType());
             DynamicMethod dynMethod = factory.createMethod(target, method);
 
             // if not overloaded, we add a method that guesses at which signature to use
             // TODO: just adding first one right now...add in signature-guessing logic
             if (target.getMethods().get(name) == null) {
                 target.addMethod(name, dynMethod);
             }
 
             // add method with full signature, so it's guaranteed to be directly accessible
             // TODO: no need for this to be a full, formal JVM signature
             name = name + prettyParams(method.getParameterTypes());
             target.addMethod(name, dynMethod);
         }
     }
     
     private static void populateSpecialMethods(RubyModule rubyMod, RubyModule rubySing, final Class cls) {
         final Ruby ruby = rubyMod.getRuntime();
         
         // add a few type-specific special methods
         rubySing.addMethod("java_class", new JavaMethod.JavaMethodZero(rubySing, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return javaToRuby(ruby, cls);
             }
         });
     }
 
     private static void populateFields(RubyModule rubyMod, final Class cls) throws RaiseException, SecurityException {
         Ruby ruby = rubyMod.getRuntime();
         
         // add all static variables
         Field[] fields = cls.getDeclaredFields();
         for (Field field : fields) {
             // only public static fields that are valid constants
             if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && IdUtil.isConstant(field.getName())) {
                 Object value = null;
                 try {
                     value = field.get(null);
                 } catch (Exception e) {
                     throw ruby.newTypeError("Could not access field " + cls.getCanonicalName() + "::" + field.getName() + " using " + ci(field.getType()));
                 }
                 rubyMod.defineConstant(field.getName(), new JavaObjectWrapper((RubyClass) getMirrorForClass(ruby, value.getClass()), value));
             }
         }
     }
 
     protected static void populateMirrorForArrayClass(RubyModule rubyMod, Class cls) {
         final Ruby ruby = rubyMod.getRuntime();
         
         rubyMod.addMethod("[]", new JavaMethod.JavaMethodOneOrTwo(rubyMod, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                 Object array = rubyToJava(self);
                 int x = RubyFixnum.fix2int(arg.convertToInteger());
                 return javaToRuby(ruby, Array.get(array, x));
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
                 Object array = rubyToJava(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 int y = RubyFixnum.fix2int(arg1.convertToInteger());
                 return javaToRuby(ruby, Array.get(Array.get(array, x), y));
             }
         });
 
         rubyMod.addMethod("[]=", new JavaMethod.JavaMethodTwoOrThree(rubyMod, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
                 Object array = rubyToJava(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 Object obj = rubyToJava(arg1);
                 Array.set(array, x, obj);
 
                 return arg1;
             }
 
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
                 Object array = rubyToJava(self);
                 int x = RubyFixnum.fix2int(arg0.convertToInteger());
                 int y = RubyFixnum.fix2int(arg1.convertToInteger());
                 Object obj = rubyToJava(arg2);
                 Array.set(Array.get(array, x), y, obj);
 
                 return arg2;
             }
         });
 
         rubyMod.addMethod("length", new JavaMethod.JavaMethodZero(rubyMod, Visibility.PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 Object array = rubyToJava(self);
                 return javaToRuby(ruby, Array.getLength(array));
             }
         });
     }
     static final Map<Class, JavaMethodFactory> methodFactories = new HashMap();
     static final JavaMethodFactory JAVA_OBJECT_METHOD_FACTORY = new JavaMethodFactory() {
         public DynamicMethod createMethod(RubyClass klazz, Method method) {
             return new JavaObjectWrapperMethod(klazz, method);
         }
     };
 
     protected static JavaMethodFactory getMethodFactory(Class returnType) {
         JavaMethodFactory factory = methodFactories.get(returnType);
 
         if (factory == null) {
             return JAVA_OBJECT_METHOD_FACTORY;
         }
 
         return factory;
     }
     
 
     static {
         methodFactories.put(void.class, new JavaMethodFactory() {
             @Override
             public DynamicMethod createMethod(RubyModule klazz, Method method) {
                 Class[] parameters = method.getParameterTypes();
                 if (parameters.length > 0) {
                     return new JavaVoidWrapperMethod(klazz, method);
                 } else {
                     return new JavaVoidWrapperMethodZero(klazz, method);
                 }
             }
         });
     }
 
     public static class JavaMethodFactory {
         public DynamicMethod createMethod(RubyModule klazz, Method method) {
             Class[] params = method.getParameterTypes();
             if (params.length > 0) {
                 return new JavaObjectWrapperMethod(klazz, method);
             } else {
                 return new JavaObjectWrapperMethodZero(klazz, method);
             }
         }
     }
 
     public static abstract class AbstractJavaWrapperMethodZero extends JavaMethod.JavaMethodZero {
         protected final Handle handle;
         protected final boolean isStatic;
         protected final String className;
         protected final String methodName;
         protected final String prettySig;
         protected final Ruby ruby;
 
         public AbstractJavaWrapperMethodZero(RubyModule klazz, Method method) {
             super(klazz, Visibility.PUBLIC);
 
             this.handle = HandleFactory.createHandle(klazz.getRuntime().getJRubyClassLoader(), method);
             this.isStatic = Modifier.isStatic(method.getModifiers());
             this.className = method.getDeclaringClass().getCanonicalName();
             this.methodName = method.getName();
             this.prettySig = prettyParams(method.getParameterTypes());
             this.ruby = klazz.getRuntime();
         }
 
         protected RaiseException error(ThreadContext context, Exception e) throws RaiseException {
             if (ruby.getDebug().isTrue()) {
                 e.printStackTrace();
             }
             throw ruby.newTypeError("Could not dispatch to " + className + "#" + methodName + " using " + prettySig);
         }
     }
 
     public static abstract class AbstractJavaWrapperMethod extends JavaMethod {
         protected final Handle handle;
         protected final boolean isStatic;
         protected final String className;
         protected final String methodName;
         protected final String prettySig;
         protected final Ruby ruby;
 
         public AbstractJavaWrapperMethod(RubyModule klazz, Method method) {
             super(klazz, Visibility.PUBLIC);
 
             this.handle = HandleFactory.createHandle(klazz.getRuntime().getJRubyClassLoader(), method);
             this.isStatic = Modifier.isStatic(method.getModifiers());
             this.className = method.getDeclaringClass().getCanonicalName();
             this.methodName = method.getName();
             this.prettySig = prettyParams(method.getParameterTypes());
             this.ruby = klazz.getRuntime();
         }
 
         protected RaiseException error(Exception e) throws RaiseException {
             return MiniJava.error(ruby, e, "Could not dispatch to " + className + "#" + methodName + " using " + prettySig);
         }
     }
     
     protected static RaiseException error(Ruby ruby, Exception e, String message) throws RaiseException {
         if (ruby.getDebug().isTrue()) {
             e.printStackTrace();
         }
         throw ruby.newTypeError(message);
     }
 
     protected static class JavaObjectWrapperMethodZero extends AbstractJavaWrapperMethodZero {
         public JavaObjectWrapperMethodZero(RubyModule klazz, Method method) {
             super(klazz, method);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return javaToRuby(ruby, result);
         }
     }
 
     protected static class JavaObjectWrapperMethod extends AbstractJavaWrapperMethod {
         public JavaObjectWrapperMethod(RubyModule klazz, Method method) {
             super(klazz, method);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             Object[] newArgs = new Object[args.length];
             for (int i = 0; i < args.length; i++) {
                 IRubyObject arg = args[i];
                 newArgs[i] = rubyToJava(arg);
             }
 
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, newArgs);
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return javaToRuby(ruby, result);
     }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1), rubyToJava(arg2));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             Object[] newArgs = new Object[args.length];
             for (int i = 0; i < args.length; i++) {
                 IRubyObject arg = args[i];
                 newArgs[i] = rubyToJava(arg);
             }
 
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, newArgs);
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1));
 
             return javaToRuby(ruby, result);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             Object result = (Object) handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1), rubyToJava(arg2));
 
             return javaToRuby(ruby, result);
         }
     }
 
     protected static class JavaVoidWrapperMethod extends AbstractJavaWrapperMethod {
         public JavaVoidWrapperMethod(RubyModule klazz, Method method) {
             super(klazz, method);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             Object[] newArgs = new Object[args.length];
             for (int i = 0; i < args.length; i++) {
                 IRubyObject arg = args[i];
                 newArgs[i] = rubyToJava(arg);
             }
 
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, newArgs);
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1), rubyToJava(arg2));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
             Object[] newArgs = new Object[args.length];
             for (int i = 0; i < args.length; i++) {
                 IRubyObject arg = args[i];
                 newArgs[i] = rubyToJava(arg);
             }
 
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, newArgs);
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1));
 
             return self;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object, rubyToJava(arg0), rubyToJava(arg1), rubyToJava(arg2));
 
             return self;
         }
     }
 
     protected static class JavaVoidWrapperMethodZero extends AbstractJavaWrapperMethodZero {
         public JavaVoidWrapperMethodZero(RubyModule klazz, Method method) {
             super(klazz, method);
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
             handle.invoke(isStatic ? null : ((JavaObjectWrapper) self).object);
 
             return self;
         }
     }
 
     public static Object rubyToJava(IRubyObject object) {
         if (object.isNil()) {
             return null;
         } else if (object instanceof JavaObjectWrapper) {
             return ((JavaObjectWrapper) object).object;
         } else {
             return object;
         }
     }
 
     public static IRubyObject javaToRuby(Ruby ruby, Object object) {
         if (object == null) {
             return ruby.getNil();
         } else if (object instanceof IRubyObject) {
             return (IRubyObject) object;
         } else {
             return new JavaObjectWrapper((RubyClass) getMirrorForClass(ruby, object.getClass()), object);
         }
     }
 
     public static class JavaObjectWrapper extends RubyObject {
         Object object;
 
         public JavaObjectWrapper(RubyClass klazz, Object object) {
             super(klazz.getRuntime(), klazz);
             this.object = object;
         }
     };
 
     public static Class getJavaClassFromObject(IRubyObject obj) {
         if (!obj.respondsTo("java_class")) {
             throw obj.getRuntime().newTypeError(obj.getMetaClass().getBaseName() + " is not a Java type");
         } else {
             return (Class) rubyToJava(obj.callMethod(obj.getRuntime().getCurrentContext(), "java_class"));
         }
     }
 }
diff --git a/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java b/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
index 104422f4de..b386e00757 100644
--- a/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
+++ b/src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
@@ -1,806 +1,806 @@
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
 
 package org.jruby.javasupport.proxy;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.UndeclaredThrowableException;
 import java.security.AccessController;
 import java.security.PrivilegedAction;
 import java.security.ProtectionDomain;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.FieldVisitor;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.commons.GeneratorAdapter;
 
 public class JavaProxyClassFactory {
 
     private static final Type JAVA_LANG_CLASS_TYPE = Type.getType(Class.class);
 
     private static final Type[] EMPTY_TYPE_ARR = new Type[0];
 
     private static final org.objectweb.asm.commons.Method HELPER_GET_PROXY_CLASS_METHOD = org.objectweb.asm.commons.Method
             .getMethod(JavaProxyClass.class.getName()
                     + " initProxyClass(java.lang.Class)");
 
     private static final org.objectweb.asm.commons.Method CLASS_FORNAME_METHOD = org.objectweb.asm.commons.Method
             .getMethod("java.lang.Class forName(java.lang.String)");
 
     private static final String INVOCATION_HANDLER_FIELD_NAME = "__handler";
 
     private static final String PROXY_CLASS_FIELD_NAME = "__proxy_class";
 
     private static final Class[] EMPTY_CLASS_ARR = new Class[0];
 
     private static final Type INVOCATION_HANDLER_TYPE = Type
             .getType(JavaProxyInvocationHandler.class);
 
     private static final Type PROXY_METHOD_TYPE = Type
             .getType(JavaProxyMethod.class);
 
     private static final Type PROXY_CLASS_TYPE = Type
             .getType(JavaProxyClass.class);
 
     private static final org.objectweb.asm.commons.Method INVOCATION_HANDLER_INVOKE_METHOD = org.objectweb.asm.commons.Method
             .getMethod("java.lang.Object invoke(java.lang.Object, "
                     + PROXY_METHOD_TYPE.getClassName()
                     + ", java.lang.Object[])");
 
     private static final Type PROXY_HELPER_TYPE = Type
             .getType(InternalJavaProxyHelper.class);
 
     private static final org.objectweb.asm.commons.Method PROXY_HELPER_GET_METHOD = org.objectweb.asm.commons.Method
             .getMethod(PROXY_METHOD_TYPE.getClassName() + " initProxyMethod("
                     + JavaProxyClass.class.getName()
                     + ",java.lang.String,java.lang.String,boolean)");
 
     private static final Type JAVA_PROXY_TYPE = Type
             .getType(InternalJavaProxy.class);
 
     private static int counter;
 
     private static Map proxies = Collections.synchronizedMap(new HashMap());
 
     private static Method defineClass_method; // statically initialized below
 
     private static synchronized int nextId() {
         return counter++;
     }
     
     // TODO: we should be able to optimize this quite a bit post-1.0.  JavaClass already
     // has all the methods organized by method name; the next version (supporting protected
     // methods/fields) will have them organized even further. So collectMethods here can
     // just lookup the overridden methods in the JavaClass map, should be much faster.
     static JavaProxyClass newProxyClass(ClassLoader loader,
             String targetClassName, Class superClass, Class[] interfaces, Set names)
             throws InvocationTargetException {
         if (loader == null) {
             loader = JavaProxyClassFactory.class.getClassLoader();
         }
 
         if (superClass == null) {
             superClass = Object.class;
         }
 
         if (interfaces == null) {
             interfaces = EMPTY_CLASS_ARR;
         }
 
         Set key = new HashSet();
         key.add(superClass);
         for (int i = 0; i < interfaces.length; i++) {
             key.add(interfaces[i]);
         }
 
         // add (potentially) overridden names to the key.
         // TODO: see note above re: optimizations
         if (names != null) {
             key.addAll(names);
         }
 
         JavaProxyClass proxyClass = (JavaProxyClass) proxies.get(key);
         if (proxyClass == null) {
 
             if (targetClassName == null) {
                 String pkg = packageName(superClass);
                 String fullName = superClass.getName();
                 int ix = fullName.lastIndexOf('.');
                 String cName = fullName;
                 if(ix != -1) {
                     cName = fullName.substring(ix+1);
                 }
                 if (pkg.startsWith("java.") || pkg.startsWith("javax.")) {
                     pkg = packageName(JavaProxyClassFactory.class) + ".gen";
                 }
                 if(ix == -1) {
                     targetClassName = cName + "$Proxy" + nextId();
                 } else {
                     targetClassName = pkg + "." + cName + "$Proxy"
                         + nextId();
                 }
             }
 
             validateArgs(targetClassName, superClass);
 
             Map methods = new HashMap();
             collectMethods(superClass, interfaces, methods, names);
 
             Type selfType = Type.getType("L"
                     + toInternalClassName(targetClassName) + ";");
             proxyClass = generate(loader, targetClassName, superClass,
                     interfaces, methods, selfType);
 
             proxies.put(key, proxyClass);
         }
 
         return proxyClass;
     }
 
     static JavaProxyClass newProxyClass(ClassLoader loader,
             String targetClassName, Class superClass, Class[] interfaces)
             throws InvocationTargetException {
         return newProxyClass(loader,targetClassName,superClass,interfaces,null);
     }
 
     private static JavaProxyClass generate(final ClassLoader loader,
             final String targetClassName, final Class superClass,
             final Class[] interfaces, final Map methods, final Type selfType) {
         ClassWriter cw = beginProxyClass(targetClassName, superClass,
                 interfaces);
 
         GeneratorAdapter clazzInit = createClassInitializer(selfType, cw);
 
         generateConstructors(superClass, selfType, cw);
 
         generateGetProxyClass(selfType, cw);
 
         generateGetInvocationHandler(selfType, cw);
 
         generateProxyMethods(superClass, methods, selfType, cw, clazzInit);
 
         // finish class initializer
         clazzInit.returnValue();
         clazzInit.endMethod();
 
         // end class
         cw.visitEnd();
 
         byte[] data = cw.toByteArray();
 
         /*
          * try { FileOutputStream o = new
          * FileOutputStream(targetClassName.replace( '/', '.') + ".class");
          * o.write(data); o.close(); } catch (IOException ex) {
          * ex.printStackTrace(); }
          */
 
         Class clazz = invokeDefineClass(loader, selfType.getClassName(), data);
 
         // trigger class initialization for the class
         try {
             Field proxy_class = clazz.getDeclaredField(PROXY_CLASS_FIELD_NAME);
             proxy_class.setAccessible(true);
             return (JavaProxyClass) proxy_class.get(clazz);
         } catch (Exception ex) {
             InternalError ie = new InternalError();
             ie.initCause(ex);
             throw ie;
         }
     }
 
     static {
         AccessController.doPrivileged(new PrivilegedAction() {
             public Object run() {
                 try {
                     defineClass_method = ClassLoader.class.getDeclaredMethod(
                             "defineClass", new Class[] { String.class,
                                     byte[].class, int.class, int.class, ProtectionDomain.class });
                 } catch (Exception e) {
                     // should not happen!
                     e.printStackTrace();
                     return null;
                 }
                 defineClass_method.setAccessible(true);
                 return null;
             }
         });
     }
 
     private static Class invokeDefineClass(ClassLoader loader,
             String className, byte[] data) {
         try {
             return (Class) defineClass_method
                     .invoke(loader, new Object[] { className, data,
                             new Integer(0), new Integer(data.length), JavaProxyClassFactory.class.getProtectionDomain() });
         } catch (IllegalArgumentException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         } catch (IllegalAccessException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         } catch (InvocationTargetException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
         }
     }
 
     private static ClassWriter beginProxyClass(final String targetClassName,
             final Class superClass, final Class[] interfaces) {
 
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
 
         int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC;
         String name = toInternalClassName(targetClassName);
         String signature = null;
         String supername = toInternalClassName(superClass);
         String[] interfaceNames = new String[interfaces.length + 1];
         for (int i = 0; i < interfaces.length; i++) {
             interfaceNames[i] = toInternalClassName(interfaces[i]);
         }
         interfaceNames[interfaces.length] = toInternalClassName(InternalJavaProxy.class);
 
         // start class
         cw.visit(Opcodes.V1_3, access, name, signature, supername,
                 interfaceNames);
 
         cw.visitField(Opcodes.ACC_PRIVATE, INVOCATION_HANDLER_FIELD_NAME,
                 INVOCATION_HANDLER_TYPE.getDescriptor(), null, null).visitEnd();
 
         cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                 PROXY_CLASS_FIELD_NAME, PROXY_CLASS_TYPE.getDescriptor(), null,
                 null).visitEnd();
 
         return cw;
     }
 
     private static void generateProxyMethods(Class superClass, Map methods,
             Type selfType, ClassVisitor cw, GeneratorAdapter clazzInit) {
         Iterator it = methods.values().iterator();
         while (it.hasNext()) {
             MethodData md = (MethodData) it.next();
             Type superClassType = Type.getType(superClass);
             generateProxyMethod(selfType, superClassType, cw, clazzInit, md);
         }
     }
 
     private static void generateGetInvocationHandler(Type selfType,
             ClassVisitor cw) {
         // make getter for handler
         GeneratorAdapter gh = new GeneratorAdapter(Opcodes.ACC_PUBLIC,
                 new org.objectweb.asm.commons.Method("___getInvocationHandler",
                         INVOCATION_HANDLER_TYPE, EMPTY_TYPE_ARR), null,
                 EMPTY_TYPE_ARR, cw);
 
         gh.loadThis();
         gh.getField(selfType, INVOCATION_HANDLER_FIELD_NAME,
                 INVOCATION_HANDLER_TYPE);
         gh.returnValue();
         gh.endMethod();
     }
 
     private static void generateGetProxyClass(Type selfType, ClassVisitor cw) {
         // make getter for proxy class
         GeneratorAdapter gpc = new GeneratorAdapter(Opcodes.ACC_PUBLIC,
                 new org.objectweb.asm.commons.Method("___getProxyClass",
                         PROXY_CLASS_TYPE, EMPTY_TYPE_ARR), null,
                 EMPTY_TYPE_ARR, cw);
         gpc.getStatic(selfType, PROXY_CLASS_FIELD_NAME, PROXY_CLASS_TYPE);
         gpc.returnValue();
         gpc.endMethod();
     }
 
     private static void generateConstructors(Class superClass, Type selfType,
             ClassVisitor cw) {
-        Constructor[] cons = superClass.getConstructors();
+        Constructor[] cons = superClass.getDeclaredConstructors();
         for (int i = 0; i < cons.length; i++) {
             Constructor constructor = cons[i];
 
             int acc = constructor.getModifiers();
             if (Modifier.isProtected(acc) || Modifier.isPublic(acc)) {
                 // ok, it's publix or protected
             } else if (!Modifier.isPrivate(acc)
                     && packageName(constructor.getDeclaringClass()).equals(
                             packageName(selfType.getClassName()))) {
                 // ok, it's package scoped and we're in the same package
             } else {
                 // it's unaccessible
                 continue;
             }
 
             generateConstructor(selfType, constructor, cw);
         }
     }
 
     private static GeneratorAdapter createClassInitializer(Type selfType,
             ClassVisitor cw) {
         GeneratorAdapter clazzInit;
         clazzInit = new GeneratorAdapter(Opcodes.ACC_PRIVATE
                 | Opcodes.ACC_STATIC, new org.objectweb.asm.commons.Method(
                 "<clinit>", Type.VOID_TYPE, EMPTY_TYPE_ARR), null,
                 EMPTY_TYPE_ARR, cw);
 
         clazzInit.visitLdcInsn(selfType.getClassName());
         clazzInit.invokeStatic(JAVA_LANG_CLASS_TYPE, CLASS_FORNAME_METHOD);
         clazzInit
                 .invokeStatic(PROXY_HELPER_TYPE, HELPER_GET_PROXY_CLASS_METHOD);
         clazzInit.dup();
         clazzInit.putStatic(selfType, PROXY_CLASS_FIELD_NAME, PROXY_CLASS_TYPE);
         return clazzInit;
     }
 
     private static void generateProxyMethod(Type selfType, Type superType,
             ClassVisitor cw, GeneratorAdapter clazzInit, MethodData md) {
         if (!md.generateProxyMethod()) {
             return;
         }
 
         org.objectweb.asm.commons.Method m = md.getMethod();
         Type[] ex = toType(md.getExceptions());
 
         String field_name = "__mth$" + md.getName() + md.scrambledSignature();
 
         // create static private method field
         FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE
                 | Opcodes.ACC_STATIC, field_name, PROXY_METHOD_TYPE
                 .getDescriptor(), null, null);
         fv.visitEnd();
 
         clazzInit.dup();
         clazzInit.push(m.getName());
         clazzInit.push(m.getDescriptor());
         clazzInit.push(md.isImplemented());
         clazzInit.invokeStatic(PROXY_HELPER_TYPE, PROXY_HELPER_GET_METHOD);
         clazzInit.putStatic(selfType, field_name, PROXY_METHOD_TYPE);
 
         org.objectweb.asm.commons.Method sm = new org.objectweb.asm.commons.Method(
                 "__super$" + m.getName(), m.getReturnType(), m
                         .getArgumentTypes());
 
         //
         // construct the proxy method
         //
         GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null,
                 ex, cw);
 
         ga.loadThis();
         ga.getField(selfType, INVOCATION_HANDLER_FIELD_NAME,
                 INVOCATION_HANDLER_TYPE);
 
         // if the method is extending something, then we have
         // to test if the handler is initialized...
 
         if (md.isImplemented()) {
             ga.dup();
             Label ok = ga.newLabel();
             ga.ifNonNull(ok);
 
             ga.loadThis();
             ga.loadArgs();
             ga.invokeConstructor(superType, m);
             ga.returnValue();
             ga.mark(ok);
         }
 
         ga.loadThis();
         ga.getStatic(selfType, field_name, PROXY_METHOD_TYPE);
 
         if (m.getArgumentTypes().length == 0) {
             // load static empty array
             ga.getStatic(JAVA_PROXY_TYPE, "NO_ARGS", Type
                     .getType(Object[].class));
         } else {
             // box arguments
             ga.loadArgArray();
         }
 
         Label before = ga.mark();
 
         ga.invokeInterface(INVOCATION_HANDLER_TYPE,
                 INVOCATION_HANDLER_INVOKE_METHOD);
 
         Label after = ga.mark();
 
         ga.unbox(m.getReturnType());
         ga.returnValue();
 
         // this is a simple rethrow handler
         Label rethrow = ga.mark();
         ga.visitInsn(Opcodes.ATHROW);
 
         for (int i = 0; i < ex.length; i++) {
             ga.visitTryCatchBlock(before, after, rethrow, ex[i]
                     .getInternalName());
         }
 
         ga.visitTryCatchBlock(before, after, rethrow, "java/lang/Error");
         ga.visitTryCatchBlock(before, after, rethrow,
                 "java/lang/RuntimeException");
 
         Type thr = Type.getType(Throwable.class);
         Label handler = ga.mark();
         Type udt = Type.getType(UndeclaredThrowableException.class);
         int loc = ga.newLocal(thr);
         ga.storeLocal(loc, thr);
         ga.newInstance(udt);
         ga.dup();
         ga.loadLocal(loc, thr);
         ga.invokeConstructor(udt, org.objectweb.asm.commons.Method
                 .getMethod("void <init>(java.lang.Throwable)"));
         ga.throwException();
 
         ga.visitTryCatchBlock(before, after, handler, "java/lang/Throwable");
 
         ga.endMethod();
 
         //
         // construct the super-proxy method
         //
         if (md.isImplemented()) {
 
             GeneratorAdapter ga2 = new GeneratorAdapter(Opcodes.ACC_PUBLIC, sm,
                     null, ex, cw);
 
             ga2.loadThis();
             ga2.loadArgs();
             ga2.invokeConstructor(superType, m);
             ga2.returnValue();
             ga2.endMethod();
         }
     }
 
     private static Class[] generateConstructor(Type selfType,
             Constructor constructor, ClassVisitor cw) {
 
         Class[] superConstructorParameterTypes = constructor
                 .getParameterTypes();
         Class[] newConstructorParameterTypes = new Class[superConstructorParameterTypes.length + 1];
         System.arraycopy(superConstructorParameterTypes, 0,
                 newConstructorParameterTypes, 0,
                 superConstructorParameterTypes.length);
         newConstructorParameterTypes[superConstructorParameterTypes.length] = JavaProxyInvocationHandler.class;
 
         int access = Opcodes.ACC_PUBLIC;
         String name1 = "<init>";
         String signature = null;
         Class[] superConstructorExceptions = constructor.getExceptionTypes();
 
         org.objectweb.asm.commons.Method super_m = new org.objectweb.asm.commons.Method(
                 name1, Type.VOID_TYPE, toType(superConstructorParameterTypes));
         org.objectweb.asm.commons.Method m = new org.objectweb.asm.commons.Method(
                 name1, Type.VOID_TYPE, toType(newConstructorParameterTypes));
 
         GeneratorAdapter ga = new GeneratorAdapter(access, m, signature,
                 toType(superConstructorExceptions), cw);
 
         ga.loadThis();
         ga.loadArgs(0, superConstructorParameterTypes.length);
         ga.invokeConstructor(Type.getType(constructor.getDeclaringClass()),
                 super_m);
 
         ga.loadThis();
         ga.loadArg(superConstructorParameterTypes.length);
         ga.putField(selfType, INVOCATION_HANDLER_FIELD_NAME,
                 INVOCATION_HANDLER_TYPE);
 
         // do a void return
         ga.returnValue();
         ga.endMethod();
         return newConstructorParameterTypes;
     }
 
     private static String toInternalClassName(Class clazz) {
         return toInternalClassName(clazz.getName());
     }
 
     private static String toInternalClassName(String name) {
         return name.replace('.', '/');
     }
 
     private static Type[] toType(Class[] parameterTypes) {
         Type[] result = new Type[parameterTypes.length];
         for (int i = 0; i < result.length; i++) {
             result[i] = Type.getType(parameterTypes[i]);
         }
         return result;
     }
 
     private static void collectMethods(Class superClass, Class[] interfaces,
             Map methods, Set names) {
         HashSet allClasses = new HashSet();
         addClass(allClasses, methods, superClass, names);
         addInterfaces(allClasses, methods, interfaces, names);
     }
 
     static class MethodData {
         Set methods = new HashSet();
 
         final Method mostSpecificMethod;
         final Class[] mostSpecificParameterTypes;
 
         boolean hasPublicDecl = false;
 
         MethodData(Method method) {
             this.mostSpecificMethod = method;
             this.mostSpecificParameterTypes = mostSpecificMethod.getParameterTypes();
             hasPublicDecl = method.getDeclaringClass().isInterface()
                     || Modifier.isPublic(method.getModifiers());
         }
 
         public String scrambledSignature() {
             StringBuffer sb = new StringBuffer();
             Class[] parms = getParameterTypes();
             for (int i = 0; i < parms.length; i++) {
                 sb.append('$');
                 String name = parms[i].getName();
                 name = name.replace('[', '1');
                 name = name.replace('.', '_');
                 name = name.replace(';', '2');
                 sb.append(name);
             }
             return sb.toString();
         }
 
         public Class getDeclaringClass() {
             return mostSpecificMethod.getDeclaringClass();
         }
 
         public org.objectweb.asm.commons.Method getMethod() {
             return new org.objectweb.asm.commons.Method(getName(), Type
                     .getType(getReturnType()), getType(getParameterTypes()));
         }
 
         private Type[] getType(Class[] parameterTypes) {
             Type[] result = new Type[parameterTypes.length];
             for (int i = 0; i < parameterTypes.length; i++) {
                 result[i] = Type.getType(parameterTypes[i]);
             }
             return result;
         }
 
         private String getName() {
             return mostSpecificMethod.getName();
         }
 
         private Class[] getParameterTypes() {
             return mostSpecificParameterTypes;
         }
 
         public Class[] getExceptions() {
 
             Set all = new HashSet();
 
             Iterator it = methods.iterator();
             while (it.hasNext()) {
                 Method m = (Method) it.next();
                 Class[] ex = m.getExceptionTypes();
                 for (int i = 0; i < ex.length; i++) {
                     Class exx = ex[i];
 
                     if (all.contains(exx)) {
                         continue;
                     }
 
                     boolean add = true;
                     Iterator it2 = all.iterator();
                     while (it2.hasNext()) {
                         Class de = (Class) it2.next();
 
                         if (de.isAssignableFrom(exx)) {
                             add = false;
                             break;
                         } else if (exx.isAssignableFrom(de)) {
                             it2.remove();
                             add = true;
                         }
 
                     }
 
                     if (add) {
                         all.add(exx);
                     }
                 }
             }
 
             return (Class[]) all.toArray(new Class[all.size()]);
         }
 
         public boolean generateProxyMethod() {
             return !isFinal() && !isPrivate();
         }
 
         public void add(Method method) {
             methods.add(method);
             hasPublicDecl |= Modifier.isPublic(method.getModifiers());
         }
 
         Class getReturnType() {
             return mostSpecificMethod.getReturnType();
         }
 
         boolean isFinal() {
             if (mostSpecificMethod.getDeclaringClass().isInterface()) {
                 return false;
             }
 
             int mod = mostSpecificMethod.getModifiers();
             return Modifier.isFinal(mod);
         }
 
         boolean isPrivate() {
             if (mostSpecificMethod.getDeclaringClass().isInterface()) {
                 return false;
             }
 
             int mod = mostSpecificMethod.getModifiers();
             return Modifier.isPrivate(mod);
         }
 
         boolean isImplemented() {
             if (mostSpecificMethod.getDeclaringClass().isInterface()) {
                 return false;
             }
 
             int mod = mostSpecificMethod.getModifiers();
             return !Modifier.isAbstract(mod);
         }
     }
 
     static class MethodKey {
         private String name;
 
         private Class[] arguments;
 
         MethodKey(Method m) {
             this.name = m.getName();
             this.arguments = m.getParameterTypes();
         }
 
         public boolean equals(Object obj) {
             if (obj instanceof MethodKey) {
                 MethodKey key = (MethodKey) obj;
 
                 return name.equals(key.name)
                         && Arrays.equals(arguments, key.arguments);
             }
 
             return false;
         }
 
         public int hashCode() {
             return name.hashCode();
         }
     }
 
     private static void addInterfaces(Set allClasses, Map methods,
             Class[] interfaces, Set names) {
         for (int i = 0; i < interfaces.length; i++) {
             addInterface(allClasses, methods, interfaces[i], names);
         }
     }
 
     private static void addInterface(Set allClasses, Map methods,
             Class interfaze, Set names) {
         if (allClasses.add(interfaze)) {
             addMethods(methods, interfaze, names);
             addInterfaces(allClasses, methods, interfaze.getInterfaces(), names);
         }
     }
 
     private static void addMethods(Map methods, Class classOrInterface, Set names) {
         Method[] mths = classOrInterface.getDeclaredMethods();
         for (int i = 0; i < mths.length; i++) {
             if (names == null || names.contains(mths[i].getName())) {
                 addMethod(methods, mths[i]);
             }
         }
     }
 
     private static void addMethod(Map methods, Method method) {
         int acc = method.getModifiers();
         
         if (Modifier.isStatic(acc) || Modifier.isPrivate(acc)) {
             return;
         }
 
         MethodKey mk = new MethodKey(method);
         MethodData md = (MethodData) methods.get(mk);
         if (md == null) {
             md = new MethodData(method);
             methods.put(mk, md);
         }
         md.add(method);
     }
 
     private static void addClass(Set allClasses, Map methods, Class clazz, Set names) {
         if (allClasses.add(clazz)) {
             addMethods(methods, clazz, names);
             Class superClass = clazz.getSuperclass();
             if (superClass != null) {
                 addClass(allClasses, methods, superClass, names);
             }
 
             addInterfaces(allClasses, methods, clazz.getInterfaces(), names);
         }
     }
 
     private static void validateArgs(String targetClassName, Class superClass) {
 
         if (Modifier.isFinal(superClass.getModifiers())) {
             throw new IllegalArgumentException("cannot extend final class");
         }
 
         String targetPackage = packageName(targetClassName);
 
         String pkg = targetPackage.replace('.', '/');
         if (pkg.startsWith("java")) {
             throw new IllegalArgumentException("cannor add classes to package "
                     + pkg);
         }
 
         Package p = Package.getPackage(pkg);
         if (p != null) {
             if (p.isSealed()) {
                 throw new IllegalArgumentException("package " + p
                         + " is sealed");
             }
         }
     }
 
     private static String packageName(Class clazz) {
         String clazzName = clazz.getName();
         return packageName(clazzName);
     }
 
     private static String packageName(String clazzName) {
         int idx = clazzName.lastIndexOf('.');
         if (idx == -1) {
             return "";
         } else {
             return clazzName.substring(0, idx);
         }
     }
 
 }
