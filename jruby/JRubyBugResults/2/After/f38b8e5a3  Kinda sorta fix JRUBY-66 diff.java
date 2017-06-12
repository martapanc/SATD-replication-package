diff --git a/src/org/jruby/java/dispatch/CallableSelector.java b/src/org/jruby/java/dispatch/CallableSelector.java
index 41f3dd1a9a..4d5c92102f 100644
--- a/src/org/jruby/java/dispatch/CallableSelector.java
+++ b/src/org/jruby/java/dispatch/CallableSelector.java
@@ -1,494 +1,518 @@
 package org.jruby.java.dispatch;
 
+import java.lang.reflect.Member;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.javasupport.JavaCallable;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaConstructor;
 import org.jruby.javasupport.JavaMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.ParameterTypes;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 
 /**
  * Method selection logic for calling from Ruby to Java.
  */
 public class CallableSelector {
-    public static ParameterTypes matchingCallableArityN(Map cache, ParameterTypes[] methods, IRubyObject[] args, int argsLength) {
+    public static ParameterTypes matchingCallableArityN(Ruby runtime, Map cache, ParameterTypes[] methods, IRubyObject[] args, int argsLength) {
         int signatureCode = argsHashCode(args);
         ParameterTypes method = (ParameterTypes)cache.get(signatureCode);
         if (method == null) {
-            method = findMatchingCallableForArgs(cache, signatureCode, methods, args);
+            method = findMatchingCallableForArgs(runtime, cache, signatureCode, methods, args);
         }
         return method;
     }
 
     // NOTE: The five match methods are arity-split to avoid the cost of boxing arguments
     // when there's already a cached match. Do not condense them into a single
     // method.
-    public static JavaCallable matchingCallableArityN(Map cache, JavaCallable[] methods, IRubyObject[] args, int argsLength) {
+    public static JavaCallable matchingCallableArityN(Ruby runtime, Map cache, JavaCallable[] methods, IRubyObject[] args, int argsLength) {
         int signatureCode = argsHashCode(args);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) {
-            method = (JavaCallable)findMatchingCallableForArgs(cache, signatureCode, methods, args);
+            method = (JavaCallable)findMatchingCallableForArgs(runtime, cache, signatureCode, methods, args);
         }
         return method;
     }
 
-    public static JavaCallable matchingCallableArityOne(Map cache, JavaCallable[] methods, IRubyObject arg0) {
+    public static JavaCallable matchingCallableArityOne(Ruby runtime, Map cache, JavaCallable[] methods, IRubyObject arg0) {
         int signatureCode = argsHashCode(arg0);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) {
-            method = (JavaCallable)findMatchingCallableForArgs(cache, signatureCode, methods, arg0);
+            method = (JavaCallable)findMatchingCallableForArgs(runtime, cache, signatureCode, methods, arg0);
         }
         return method;
     }
 
-    public static JavaCallable matchingCallableArityTwo(Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1) {
+    public static JavaCallable matchingCallableArityTwo(Ruby runtime, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1) {
         int signatureCode = argsHashCode(arg0, arg1);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) {
-            method = (JavaCallable)findMatchingCallableForArgs(cache, signatureCode, methods, arg0, arg1);
+            method = (JavaCallable)findMatchingCallableForArgs(runtime, cache, signatureCode, methods, arg0, arg1);
         }
         return method;
     }
 
-    public static JavaCallable matchingCallableArityThree(Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
+    public static JavaCallable matchingCallableArityThree(Ruby runtime, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         int signatureCode = argsHashCode(arg0, arg1, arg2);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) {
-            method = (JavaCallable)findMatchingCallableForArgs(cache, signatureCode, methods, arg0, arg1, arg2);
+            method = (JavaCallable)findMatchingCallableForArgs(runtime, cache, signatureCode, methods, arg0, arg1, arg2);
         }
         return method;
     }
 
-    public static JavaCallable matchingCallableArityFour(Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
+    public static JavaCallable matchingCallableArityFour(Ruby runtime, Map cache, JavaCallable[] methods, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         int signatureCode = argsHashCode(arg0, arg1, arg2, arg3);
         JavaCallable method = (JavaCallable)cache.get(signatureCode);
         if (method == null) {
-            method = (JavaCallable)findMatchingCallableForArgs(cache, signatureCode, methods, arg0, arg1, arg2, arg3);
+            method = (JavaCallable)findMatchingCallableForArgs(runtime, cache, signatureCode, methods, arg0, arg1, arg2, arg3);
         }
         return method;
     }
 
-    private static ParameterTypes findMatchingCallableForArgs(Map cache, int signatureCode, ParameterTypes[] methods, IRubyObject... args) {
+    private static final boolean DEBUG = true;
+
+    private static ParameterTypes findMatchingCallableForArgs(Ruby runtime, Map cache, int signatureCode, ParameterTypes[] methods, IRubyObject... args) {
         ParameterTypes method = null;
 
         // try the new way first
         List<ParameterTypes> newFinds = findCallable(methods, args);
         if (newFinds.size() > 0) {
             // new way found one, so let's go with that
             if (newFinds.size() == 1) {
                 method = newFinds.get(0);
             } else {
                 // narrow to most specific version (or first version, if none are more specific
                 ParameterTypes mostSpecific = null;
-                for (ParameterTypes candidate : newFinds) {
-                    if (mostSpecific == null) mostSpecific = candidate;
+                Class[] msTypes = null;
+                boolean ambiguous = false;
+                OUTER: for (ParameterTypes candidate : newFinds) {
+                    if (mostSpecific == null) {
+                        mostSpecific = candidate;
+                        msTypes = mostSpecific.getParameterTypes();
+                        continue;
+                    }
 
-                    Class[] msTypes = mostSpecific.getParameterTypes();
                     Class[] cTypes = candidate.getParameterTypes();
 
                     for (int i = 0; i < msTypes.length; i++) {
                         if (msTypes[i] != cTypes[i] && msTypes[i].isAssignableFrom(cTypes[i])) {
                             mostSpecific = candidate;
-                            continue;
+                            msTypes = cTypes;
+                            ambiguous = false;
+                            continue OUTER;
+                        }
+                    }
+
+                    // none more specific; check for ambiguities
+                    for (int i = 0; i < msTypes.length; i++) {
+                        if (msTypes[i] != cTypes[i] && !msTypes[i].isAssignableFrom(cTypes[i]) && !cTypes[i].isAssignableFrom(msTypes[i])) {
+                            ambiguous = true;
+                        } else {
+                            ambiguous = false;
+                            continue OUTER;
                         }
                     }
                 }
                 method = mostSpecific;
+
+                if (ambiguous) {
+                    runtime.getWarnings().warn("ambiguous Java methods found, using " + ((Member) ((JavaCallable) method).accessibleObject()).getName() + CodegenUtils.prettyParams(msTypes));
+                }
             }
         }
 
         // fall back on old ways
         if (method == null) {
             method = findCallable(methods, Exact, args);
         }
         if (method == null) {
             method = findCallable(methods, AssignableAndPrimitivable, args);
         }
         if (method == null) {
             method = findCallable(methods, AssignableOrDuckable, args);
         }
         if (method == null) {
             method = findCallable(methods, AssignableAndPrimitivableWithVarargs, args);
         }
         
         // cache found result
         if (method != null) cache.put(signatureCode, method);
         
         return method;
     }
 
     private static void warnMultipleMatches(IRubyObject[] args, List<ParameterTypes> newFinds) {
         RubyClass[] argTypes = new RubyClass[args.length];
         for (int i = 0; i < argTypes.length; i++) {
             argTypes[i] = args[i].getMetaClass();
         }
         StringBuilder builder = new StringBuilder("multiple Java methods for arguments (");
         boolean first = true;
         for (RubyClass argType : argTypes) {
             if (!first) {
                 builder.append(",");
             }
             first = false;
             builder.append(argType);
         }
         builder.append("), using first:");
         for (ParameterTypes types : newFinds) {
             builder.append("\n  ").append(types);
         }
         args[0].getRuntime().getWarnings().warn(builder.toString());
     }
 
     private static ParameterTypes findCallable(ParameterTypes[] callables, CallableAcceptor acceptor, IRubyObject... args) {
         ParameterTypes bestCallable = null;
         int bestScore = -1;
         for (int k = 0; k < callables.length; k++) {
             ParameterTypes callable = callables[k];
 
             if (acceptor.accept(callable, args)) {
                 int currentScore = getExactnessScore(callable, args);
                 if (currentScore > bestScore) {
                     bestCallable = callable;
                     bestScore = currentScore;
                 }
             }
         }
         return bestCallable;
     }
 
     private static List<ParameterTypes> findCallable(ParameterTypes[] callables, IRubyObject... args) {
         List<ParameterTypes> retainedCallables = new ArrayList<ParameterTypes>(callables.length);
         List<ParameterTypes> incomingCallables = new ArrayList<ParameterTypes>(Arrays.asList(callables));
         
         for (int currentArg = 0; currentArg < args.length; currentArg++) {
             retainedCallables.clear();
             for (Matcher matcher : MATCH_SEQUENCE) {
                 for (Iterator<ParameterTypes> callableIter = incomingCallables.iterator(); callableIter.hasNext();) {
                     ParameterTypes callable = callableIter.next();
                     Class[] types = callable.getParameterTypes();
 
                     if (matcher.match(types[currentArg], args[currentArg])) {
                         callableIter.remove();
                         retainedCallables.add(callable);
                     }
                 }
             }
             incomingCallables.clear();
             incomingCallables.addAll(retainedCallables);
         }
 
         return retainedCallables;
     }
 
     private static int getExactnessScore(ParameterTypes paramTypes, IRubyObject[] args) {
         Class[] types = paramTypes.getParameterTypes();
         int count = 0;
 
         if (paramTypes.isVarArgs()) {
             // varargs exactness gives the last N args as +1 since they'll already
             // have been determined to fit
 
             // dig out as many trailing args as possible that match varargs type
             int nonVarargs = types.length - 1;
             
             // add one for vararg
             count += 1;
 
             // check remaining args
             for (int i = 0; i < nonVarargs && i < args.length; i++) {
                 if (types[i].equals(argClass(args[i]))) {
                     count++;
                 }
             }
         } else {
             for (int i = 0; i < args.length; i++) {
                 if (types[i].equals(argClass(args[i]))) {
                     count++;
                 }
             }
         }
         return count;
     }
 
     private static interface CallableAcceptor {
 
         public boolean accept(ParameterTypes types, IRubyObject[] args);
     }
     private static final CallableAcceptor Exact = new CallableAcceptor() {
 
         public boolean accept(ParameterTypes types, IRubyObject[] args) {
             return exactMatch(types, args);
         }
     };
     private static final CallableAcceptor AssignableAndPrimitivable = new CallableAcceptor() {
 
         public boolean accept(ParameterTypes types, IRubyObject[] args) {
             return assignableAndPrimitivable(types, args);
         }
     };
     private static final CallableAcceptor AssignableOrDuckable = new CallableAcceptor() {
 
         public boolean accept(ParameterTypes types, IRubyObject[] args) {
             return assignableOrDuckable(types, args);
         }
     };
     private static final CallableAcceptor AssignableAndPrimitivableWithVarargs = new CallableAcceptor() {
 
         public boolean accept(ParameterTypes types, IRubyObject[] args) {
             return assignableAndPrimitivableWithVarargs(types, args);
         }
     };
 
     private interface Matcher {
         public boolean match(Class type, IRubyObject arg);
     }
 
     private static boolean exactMatch(ParameterTypes paramTypes, IRubyObject... args) {
         Class[] types = paramTypes.getParameterTypes();
         
         if (args.length != types.length) return false;
         
         for (int i = 0; i < types.length; i++) {
             if (!EXACT.match(types[i], args[i])) {
                 return false;
             }
         }
         return true;
     }
 
     private static Matcher EXACT = new Matcher() {
         public boolean match(Class type, IRubyObject arg) {
             return type.equals(argClass(arg))
                     || (type.isPrimitive() && CodegenUtils.getBoxType(type) == argClass(arg));
         }
     };
 
     private static Matcher ASSIGNABLE = new Matcher() {
         public boolean match(Class type, IRubyObject arg) {
             return assignable(type, arg);
         }
     };
 
     private static Matcher PRIMITIVABLE = new Matcher() {
         public boolean match(Class type, IRubyObject arg) {
             return primitivable(type, arg);
         }
     };
 
     private static Matcher DUCKABLE = new Matcher() {
         public boolean match(Class type, IRubyObject arg) {
             return duckable(type, arg);
         }
     };
 
     private static final Matcher[] MATCH_SEQUENCE = new Matcher[] {EXACT, PRIMITIVABLE, ASSIGNABLE, DUCKABLE};
 
     private static boolean assignableAndPrimitivable(ParameterTypes paramTypes, IRubyObject... args) {
         Class[] types = paramTypes.getParameterTypes();
         
         if (args.length != types.length) return false;
         
         for (int i = 0; i < types.length; i++) {
             if (!(ASSIGNABLE.match(types[i], args[i]) && PRIMITIVABLE.match(types[i], args[i]))) {
                 return false;
             }
         }
         return true;
     }
 
     private static boolean assignableOrDuckable(ParameterTypes paramTypes, IRubyObject... args) {
         Class[] types = paramTypes.getParameterTypes();
         
         if (args.length != types.length) return false;
         
         for (int i = 0; i < types.length; i++) {
             if (!(ASSIGNABLE.match(types[i], args[i]) || DUCKABLE.match(types[i], args[i]))) {
                 return false;
             }
         }
         return true;
     }
 
     private static boolean assignableAndPrimitivableWithVarargs(ParameterTypes paramTypes, IRubyObject... args) {
         // bail out if this is not a varargs method
         if (!paramTypes.isVarArgs()) return false;
         
         Class[] types = paramTypes.getParameterTypes();
 
         Class varArgArrayType = types[types.length - 1];
         Class varArgType = varArgArrayType.getComponentType();
         
         // if there's no args, we only match when there's just varargs
         if (args.length == 0) {
             return types.length <= 1;
         }
 
         // dig out as many trailing args as will fit, ensuring they match varargs type
         int nonVarargs = types.length - 1;
         for (int i = args.length - 1; i >= nonVarargs; i--) {
             if (!(ASSIGNABLE.match(varArgType, args[i]) || PRIMITIVABLE.match(varArgType, args[i]))) {
                 return false;
             }
         }
 
         // check remaining args
         for (int i = 0; i < nonVarargs; i++) {
             if (!(ASSIGNABLE.match(types[i], args[i]) || PRIMITIVABLE.match(types[i], args[i]))) {
                 return false;
             }
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
 
     private static int argsHashCode(IRubyObject a0) {
         return 31 + classHashCode(a0);
     }
 
     private static int argsHashCode(IRubyObject a0, IRubyObject a1) {
         return 31 * argsHashCode(a0) + classHashCode(a1);
     }
 
     private static int argsHashCode(IRubyObject a0, IRubyObject a1, IRubyObject a2) {
         return 31 * argsHashCode(a0, a1) + classHashCode(a2);
     }
 
     private static int argsHashCode(IRubyObject a0, IRubyObject a1, IRubyObject a2, IRubyObject a3) {
         return 31 * argsHashCode(a0, a1, a2) + classHashCode(a3);
     }
 
     private static int argsHashCode(IRubyObject[] a) {
         if (a == null) {
             return 0;
         }
 
         int result = 1;
 
         for (IRubyObject element : a) {
             result = 31 * result + classHashCode(element);
         }
 
         return result;
     }
 
     private static int classHashCode(IRubyObject o) {
         return o == null ? 0 : o.getJavaClass().hashCode();
     }
 
     private static Class argClass(IRubyObject a) {
         if (a == null) {
             return void.class;
         }
 
         return a.getJavaClass();
     }
 
     public static RaiseException argTypesDoNotMatch(Ruby runtime, IRubyObject receiver, JavaCallable[] methods, Object... args) {
         Class[] argTypes = new Class[args.length];
         for (int i = 0; i < args.length; i++) {
             argTypes[i] = argClassTypeError(args[i]);
         }
 
         return argumentError(runtime.getCurrentContext(), methods, receiver, argTypes);
     }
 
     private static Class argClassTypeError(Object object) {
         if (object == null) {
             return void.class;
         }
         if (object instanceof ConcreteJavaProxy) {
             return ((ConcreteJavaProxy)object).getJavaClass();
         }
 
         return object.getClass();
     }
 
     private static RaiseException argumentError(ThreadContext context, ParameterTypes[] methods, IRubyObject receiver, Class[] argTypes) {
         boolean constructor = methods[0] instanceof JavaConstructor || methods[0] instanceof JavaProxyConstructor;
         
         StringBuffer fullError = new StringBuffer();
         fullError.append("no ");
         if (constructor) {
             fullError.append("constructor");
         } else {
             fullError.append("method '")
                     .append(((JavaMethod)methods[0]).name().toString())
                     .append("' ");
         }
         fullError.append("for arguments ")
                 .append(CodegenUtils.prettyParams(argTypes))
                 .append(" on ");
         if (receiver instanceof RubyModule) {
             fullError.append(((RubyModule)receiver).getName());
         } else {
             fullError.append(receiver.getMetaClass().getRealClass().getName());
         }
         
         if (methods.length > 1) {
             fullError.append("\n  available overloads:");
             for (ParameterTypes method : methods) {
                 fullError.append("\n    " + CodegenUtils.prettyParams(method.getParameterTypes()));
             }
         }
         
         return context.runtime.newNameError(fullError.toString(), null);
     }
 }
diff --git a/src/org/jruby/java/invokers/RubyToJavaInvoker.java b/src/org/jruby/java/invokers/RubyToJavaInvoker.java
index 2850d2052d..8c69b16772 100644
--- a/src/org/jruby/java/invokers/RubyToJavaInvoker.java
+++ b/src/org/jruby/java/invokers/RubyToJavaInvoker.java
@@ -1,326 +1,328 @@
 package org.jruby.java.invokers;
 
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Array;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.java.dispatch.CallableSelector;
 import org.jruby.java.proxies.ArrayJavaProxy;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.javasupport.JavaCallable;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public abstract class RubyToJavaInvoker extends JavaMethod {
     protected static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
     protected final JavaCallable javaCallable;
     protected final JavaCallable[][] javaCallables;
     protected final JavaCallable[] javaVarargsCallables;
     protected final int minVarargsArity;
     protected final Map cache;
+    protected final Ruby runtime;
     private Member[] members;
     
     RubyToJavaInvoker(RubyModule host, Member[] members) {
         super(host, Visibility.PUBLIC, CallConfiguration.FrameNoneScopeNone);
         this.members = members;
+        this.runtime = host.getRuntime();
         // we set all Java methods to optional, since many/most have overloads
         setArity(Arity.OPTIONAL);
 
         Ruby runtime = host.getRuntime();
 
         // initialize all the callables for this method
         JavaCallable callable = null;
         JavaCallable[][] callables = null;
         JavaCallable[] varargsCallables = null;
         int varargsArity = Integer.MAX_VALUE;
         
         if (members.length == 1) {
             callable = createCallable(runtime, members[0]);
             if (callable.isVarArgs()) {
                 varargsCallables = createCallableArray(callable);
             }
         } else {
             Map<Integer, List<JavaCallable>> methodsMap = new HashMap<Integer, List<JavaCallable>>();
             List<JavaCallable> varargsMethods = new ArrayList();
             int maxArity = 0;
             for (Member method: members) {
                 int currentArity = getMemberParameterTypes(method).length;
                 maxArity = Math.max(currentArity, maxArity);
                 List<JavaCallable> methodsForArity = methodsMap.get(currentArity);
                 if (methodsForArity == null) {
                     methodsForArity = new ArrayList<JavaCallable>();
                     methodsMap.put(currentArity,methodsForArity);
                 }
                 JavaCallable javaMethod = createCallable(runtime,method);
                 methodsForArity.add(javaMethod);
 
                 if (isMemberVarArgs(method)) {
                     varargsArity = Math.min(currentArity - 1, varargsArity);
                     varargsMethods.add(javaMethod);
                 }
             }
 
             callables = createCallableArrayArray(maxArity + 1);
             for (Map.Entry<Integer,List<JavaCallable>> entry : methodsMap.entrySet()) {
                 List<JavaCallable> methodsForArity = (List<JavaCallable>)entry.getValue();
 
                 JavaCallable[] methodsArray = methodsForArity.toArray(createCallableArray(methodsForArity.size()));
                 callables[((Integer)entry.getKey()).intValue()] = methodsArray;
             }
 
             if (varargsMethods.size() > 0) {
                 // have at least one varargs, build that map too
                 varargsCallables = createCallableArray(varargsMethods.size());
                 varargsMethods.toArray(varargsCallables);
             }
         }
         members = null;
 
         // initialize cache of parameter types to method
         // FIXME: No real reason to use CHM, is there?
         cache = new ConcurrentHashMap(0, 0.75f, 1);
 
         this.javaCallable = callable;
         this.javaCallables = callables;
         this.javaVarargsCallables = varargsCallables;
         this.minVarargsArity = varargsArity;
         
         // if it's not overloaded, set up a NativeCall
         if (javaCallable != null) {
             // no constructor support yet
             if (javaCallable instanceof org.jruby.javasupport.JavaMethod) {
                 org.jruby.javasupport.JavaMethod javaMethod = (org.jruby.javasupport.JavaMethod)javaCallable;
                 Method method = (Method)javaMethod.getValue();
                 // only public, since non-public don't bind
                 if (Modifier.isPublic(method.getModifiers()) && Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                     setNativeCall(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes(), Modifier.isStatic(method.getModifiers()), true);
                 }
             }
         } else {
             // use the lowest-arity non-overload
             for (JavaCallable[] callablesForArity : javaCallables) {
                 if (callablesForArity != null
                         && callablesForArity.length == 1
                         && callablesForArity[0] instanceof org.jruby.javasupport.JavaMethod) {
                     Method method = (Method)((org.jruby.javasupport.JavaMethod)callablesForArity[0]).getValue();
                     // only public, since non-public don't bind
                     if (Modifier.isPublic(method.getModifiers()) && Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                         setNativeCall(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes(), Modifier.isStatic(method.getModifiers()), true);
                     }
                 }
             }
         }
     }
 
     protected Member[] getMembers() {
         return members;
     }
 
     protected AccessibleObject[] getAccessibleObjects() {
         return (AccessibleObject[])getMembers();
     }
 
     protected abstract JavaCallable createCallable(Ruby ruby, Member member);
 
     protected abstract JavaCallable[] createCallableArray(JavaCallable callable);
 
     protected abstract JavaCallable[] createCallableArray(int size);
 
     protected abstract JavaCallable[][] createCallableArrayArray(int size);
 
     protected abstract Class[] getMemberParameterTypes(Member member);
 
     protected abstract boolean isMemberVarArgs(Member member);
 
     static Object convertArg(IRubyObject arg, JavaCallable method, int index) {
         return arg.toJava(method.getParameterTypes()[index]);
     }
 
     static Object convertVarargs(IRubyObject[] args, JavaCallable method) {
         Class[] types = method.getParameterTypes();
         Class varargArrayType = types[types.length - 1];
         Class varargType = varargArrayType.getComponentType();
         int varargsStart = types.length - 1;
         int varargsCount = args.length - varargsStart;
 
         Object varargs;
         if (args.length == 0) {
             return Array.newInstance(varargType, 0);
         } else if (varargsCount == 1 && args[varargsStart] instanceof ArrayJavaProxy) {
             // we may have a pre-created array to pass; try that first
             varargs = args[varargsStart].toJava(varargArrayType);
         } else {
             varargs = Array.newInstance(varargType, varargsCount);
 
             for (int i = 0; i < varargsCount; i++) {
                 Array.set(varargs, i, args[varargsStart + i].toJava(varargType));
             }
         }
         return varargs;
     }
 
     static JavaProxy castJavaProxy(IRubyObject self) {
         assert self instanceof JavaProxy : "Java methods can only be invoked on Java objects";
         return (JavaProxy)self;
     }
 
     static void trySetAccessible(AccessibleObject[] accObjs) {
         if (!Ruby.isSecurityRestricted()) {
             try {
                 AccessibleObject.setAccessible(accObjs, true);
             } catch(SecurityException e) {}
         }
     }
 
     void raiseNoMatchingCallableError(String name, IRubyObject proxy, Object... args) {
         int len = args.length;
         Class[] argTypes = new Class[args.length];
         for (int i = 0; i < len; i++) {
             argTypes[i] = args[i].getClass();
         }
         throw proxy.getRuntime().newArgumentError("no " + name + " with arguments matching " + Arrays.toString(argTypes) + " on object " + proxy.getMetaClass());
     }
 
     protected JavaCallable findCallable(IRubyObject self, String name, IRubyObject[] args, int arity) {
         JavaCallable callable;
         if ((callable = javaCallable) == null) {
             JavaCallable[] callablesForArity = null;
             if (arity >= javaCallables.length || (callablesForArity = javaCallables[arity]) == null) {
                 if (javaVarargsCallables != null) {
-                    callable = CallableSelector.matchingCallableArityN(cache, javaVarargsCallables, args, arity);
+                    callable = CallableSelector.matchingCallableArityN(runtime, cache, javaVarargsCallables, args, arity);
                     if (callable == null) {
                         throw CallableSelector.argTypesDoNotMatch(self.getRuntime(), self, javaVarargsCallables, (Object[])args);
                     }
                     return callable;
                 } else {
                     throw self.getRuntime().newArgumentError(args.length, javaCallables.length - 1);
                 }
             }
-            callable = CallableSelector.matchingCallableArityN(cache, callablesForArity, args, arity);
+            callable = CallableSelector.matchingCallableArityN(runtime, cache, callablesForArity, args, arity);
             if (callable == null && javaVarargsCallables != null) {
-                callable = CallableSelector.matchingCallableArityN(cache, javaVarargsCallables, args, arity);
+                callable = CallableSelector.matchingCallableArityN(runtime, cache, javaVarargsCallables, args, arity);
                 if (callable == null) {
                     throw CallableSelector.argTypesDoNotMatch(self.getRuntime(), self, javaVarargsCallables, (Object[])args);
                 }
                 return callable;
             }
             if (callable == null) {
                 throw CallableSelector.argTypesDoNotMatch(self.getRuntime(), self, callablesForArity, (Object[])args);
             }
         } else {
             if (!callable.isVarArgs() && callable.getParameterTypes().length != args.length) {
                 throw self.getRuntime().newArgumentError(args.length, callable.getParameterTypes().length);
             }
         }
         return callable;
     }
 
     protected JavaCallable findCallableArityZero(IRubyObject self, String name) {
         JavaCallable callable;
         if ((callable = javaCallable) == null) {
             // TODO: varargs?
             JavaCallable[] callablesForArity = null;
             if (javaCallables.length == 0 || (callablesForArity = javaCallables[0]) == null) {
                 raiseNoMatchingCallableError(name, self, EMPTY_OBJECT_ARRAY);
             }
             callable = callablesForArity[0];
         } else {
             if (callable.getParameterTypes().length != 0) {
                 throw self.getRuntime().newArgumentError(0, callable.getParameterTypes().length);
             }
         }
         return callable;
     }
 
     protected JavaCallable findCallableArityOne(IRubyObject self, String name, IRubyObject arg0) {
         JavaCallable callable;
         if ((callable = javaCallable) == null) {
             // TODO: varargs?
             JavaCallable[] callablesForArity = null;
             if (javaCallables.length <= 1 || (callablesForArity = javaCallables[1]) == null) {
                 throw self.getRuntime().newArgumentError(1, javaCallables.length - 1);
             }
-            callable = CallableSelector.matchingCallableArityOne(cache, callablesForArity, arg0);
+            callable = CallableSelector.matchingCallableArityOne(runtime, cache, callablesForArity, arg0);
             if (callable == null) {
                 throw CallableSelector.argTypesDoNotMatch(self.getRuntime(), self, callablesForArity, arg0);
             }
         } else {
             if (callable.getParameterTypes().length != 1) {
                 throw self.getRuntime().newArgumentError(1, callable.getParameterTypes().length);
             }
         }
         return callable;
     }
 
     protected JavaCallable findCallableArityTwo(IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1) {
         JavaCallable callable;
         if ((callable = javaCallable) == null) {
             // TODO: varargs?
             JavaCallable[] callablesForArity = null;
             if (javaCallables.length <= 2 || (callablesForArity = javaCallables[2]) == null) {
                 throw self.getRuntime().newArgumentError(2, javaCallables.length - 1);
             }
-            callable = CallableSelector.matchingCallableArityTwo(cache, callablesForArity, arg0, arg1);
+            callable = CallableSelector.matchingCallableArityTwo(runtime, cache, callablesForArity, arg0, arg1);
             if (callable == null) {
                 throw CallableSelector.argTypesDoNotMatch(self.getRuntime(), self, callablesForArity, arg0, arg1);
             }
         } else {
             if (callable.getParameterTypes().length != 2) {
                 throw self.getRuntime().newArgumentError(2, callable.getParameterTypes().length);
             }
         }
         return callable;
     }
 
     protected JavaCallable findCallableArityThree(IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         JavaCallable callable;
         if ((callable = javaCallable) == null) {
             // TODO: varargs?
             JavaCallable[] callablesForArity = null;
             if (javaCallables.length <= 3 || (callablesForArity = javaCallables[3]) == null) {
                 throw self.getRuntime().newArgumentError(3, javaCallables.length - 1);
             }
-            callable = CallableSelector.matchingCallableArityThree(cache, callablesForArity, arg0, arg1, arg2);
+            callable = CallableSelector.matchingCallableArityThree(runtime, cache, callablesForArity, arg0, arg1, arg2);
             if (callable == null) {
                 throw CallableSelector.argTypesDoNotMatch(self.getRuntime(), self, callablesForArity, arg0, arg1, arg2);
             }
         } else {
             if (callable.getParameterTypes().length != 3) {
                 throw self.getRuntime().newArgumentError(3, callable.getParameterTypes().length);
             }
         }
         return callable;
     }
 
     protected JavaCallable findCallableArityFour(IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         JavaCallable callable;
         if ((callable = javaCallable) == null) {
             // TODO: varargs?
             JavaCallable[] callablesForArity = null;
             if (javaCallables.length <= 4 || (callablesForArity = javaCallables[4]) == null) {
                 throw self.getRuntime().newArgumentError(4, javaCallables.length - 1);
             }
-            callable = CallableSelector.matchingCallableArityFour(cache, callablesForArity, arg0, arg1, arg2, arg3);
+            callable = CallableSelector.matchingCallableArityFour(runtime, cache, callablesForArity, arg0, arg1, arg2, arg3);
             if (callable == null) {
                 throw CallableSelector.argTypesDoNotMatch(self.getRuntime(), self, callablesForArity, arg0, arg1, arg2, arg3);
             }
         } else {
             if (callable.getParameterTypes().length != 4) {
                 throw self.getRuntime().newArgumentError(4, callable.getParameterTypes().length);
             }
         }
         return callable;
     }
 }
\ No newline at end of file
diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index 6a56d82114..efced1cc11 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -1,1314 +1,1313 @@
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
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 package org.jruby.javasupport;
 
 import org.jruby.java.util.BlankSlateWrapper;
 import org.jruby.java.util.SystemPropertiesMap;
 import org.jruby.java.proxies.JavaInterfaceTemplate;
 import org.jruby.java.addons.KernelJavaAddons;
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Proxy;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBasicObject;
 import org.jruby.RubyClass;
 import org.jruby.RubyClassPathVariable;
 import org.jruby.RubyException;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.RubyUnboundMethod;
 import org.jruby.exceptions.RaiseException;
-import org.jruby.java.util.SystemPropertiesMap;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.*;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodN;
 import org.jruby.internal.runtime.methods.JavaMethod.JavaMethodZero;
 import org.jruby.java.addons.ArrayJavaAddons;
 import org.jruby.java.addons.IOJavaAddons;
 import org.jruby.java.addons.StringJavaAddons;
 import org.jruby.java.codegen.RealClassGenerator;
 import org.jruby.java.dispatch.CallableSelector;
 import org.jruby.java.invokers.InstanceMethodInvoker;
 import org.jruby.java.invokers.MethodInvoker;
 import org.jruby.java.invokers.StaticMethodInvoker;
 import org.jruby.java.proxies.ArrayJavaProxy;
 import org.jruby.java.proxies.ArrayJavaProxyCreator;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.java.proxies.MapJavaProxy;
 import org.jruby.java.proxies.InterfaceJavaProxy;
 import org.jruby.java.proxies.JavaProxy;
 import org.jruby.java.proxies.RubyObjectHolderProxy;
 import org.jruby.util.ClassCache.OneShotClassLoader;
 import org.jruby.util.cli.Options;
 
 @JRubyModule(name = "Java")
 public class Java implements Library {
     public static final boolean NEW_STYLE_EXTENSION = Options.JI_NEWSTYLEEXTENSION.load();
     public static final boolean OBJECT_PROXY_CACHE = Options.JI_OBJECTPROXYCACHE.load();
 
     public void load(Ruby runtime, boolean wrap) throws IOException {
         createJavaModule(runtime);
 
         RubyModule jpmt = runtime.defineModule("JavaPackageModuleTemplate");
         jpmt.getSingletonClass().setSuperClass(new BlankSlateWrapper(runtime, jpmt.getMetaClass().getSuperClass(), runtime.getKernel()));
 
         runtime.getLoadService().require("jruby/java");
         
         // rewite ArrayJavaProxy superclass to point at Object, so it inherits Object behaviors
         RubyClass ajp = runtime.getClass("ArrayJavaProxy");
         ajp.setSuperClass(runtime.getJavaSupport().getObjectJavaClass().getProxyClass());
         ajp.includeModule(runtime.getEnumerable());
         
         RubyClassPathVariable.createClassPathVariable(runtime);
 
         // modify ENV_JAVA to be a read/write version
         Map systemProps = new SystemPropertiesMap();
         runtime.getObject().setConstantQuiet(
                 "ENV_JAVA",
                 new MapJavaProxy(
                         runtime,
                         (RubyClass)Java.getProxyClass(runtime, JavaClass.get(runtime, SystemPropertiesMap.class)),
                         systemProps));
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
         ArrayJavaProxyCreator.createArrayJavaProxyCreator(context);
         ConcreteJavaProxy.createConcreteJavaProxy(context);
         InterfaceJavaProxy.createInterfaceJavaProxy(context);
         ArrayJavaProxy.createArrayJavaProxy(context);
 
         // creates ruby's hash methods' proxy for Map interface
         MapJavaProxy.createMapJavaProxy(context);
 
         // also create the JavaProxy* classes
         JavaProxyClass.createJavaProxyModule(runtime);
 
         // The template for interface modules
         JavaInterfaceTemplate.createJavaInterfaceTemplateModule(context);
 
         RubyModule javaUtils = runtime.defineModule("JavaUtilities");
         
         javaUtils.defineAnnotatedMethods(JavaUtilities.class);
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
         
         // Now attach Java-related extras to core classes
         runtime.getArray().defineAnnotatedMethods(ArrayJavaAddons.class);
         runtime.getKernel().defineAnnotatedMethods(KernelJavaAddons.class);
         runtime.getString().defineAnnotatedMethods(StringJavaAddons.class);
         runtime.getIO().defineAnnotatedMethods(IOJavaAddons.class);
 
         if (runtime.getObject().isConstantDefined("StringIO")) {
             ((RubyClass)runtime.getObject().getConstant("StringIO")).defineAnnotatedMethods(IOJavaAddons.AnyIO.class);
         }
         
         // add all name-to-class mappings
         addNameClassMappings(runtime, runtime.getJavaSupport().getNameClassMap());
         
         // add some base Java classes everyone will need
         runtime.getJavaSupport().setObjectJavaClass(JavaClass.get(runtime, Object.class));
 
         return javaModule;
     }
 
     public static class OldStyleExtensionInherited {
         @JRubyMethod
         public static IRubyObject inherited(IRubyObject recv, IRubyObject arg0) {
             return Java.concrete_proxy_inherited(recv, arg0);
         }
     };
 
     public static class NewStyleExtensionInherited {
         @JRubyMethod
         public static IRubyObject inherited(IRubyObject recv, IRubyObject arg0) {
             if (!(arg0 instanceof RubyClass)) {
                 throw recv.getRuntime().newTypeError(arg0, recv.getRuntime().getClassClass());
             }
             
             JavaInterfaceTemplate.addRealImplClassNew((RubyClass)arg0);
             return recv.getRuntime().getNil();
         }
     };
     
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
 
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new ClassProvider() {
 
         public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().getInstanceVariable("@package_name")) == null) {
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
             if ((packageName = pkg.getInstanceVariables().getInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyModule) get_interface_module(
                     runtime,
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
 
     public static IRubyObject create_proxy_class(
             IRubyObject recv,
             IRubyObject constant,
             IRubyObject javaClass,
             IRubyObject module) {
         Ruby runtime = recv.getRuntime();
 
         if (!(module instanceof RubyModule)) {
             throw runtime.newTypeError(module, runtime.getModule());
         }
         IRubyObject proxyClass = get_proxy_class(recv, javaClass);
         RubyModule m = (RubyModule)module;
         String constName = constant.asJavaString();
         IRubyObject existing = m.getConstantNoConstMissing(constName);
 
         if (existing != null
                 && existing != RubyBasicObject.UNDEF
                 && existing != proxyClass) {
             runtime.getWarnings().warn("replacing " + existing + " with " + proxyClass + " in constant '" + constName + " on class/module " + m);
         }
         
         return ((RubyModule) module).setConstantQuiet(constant.asJavaString(), get_proxy_class(recv, javaClass));
     }
 
     public static IRubyObject get_java_class(IRubyObject recv, IRubyObject name) {
         try {
             return JavaClass.for_name(recv, name);
         } catch (Exception e) {
             recv.getRuntime().getJavaSupport().handleNativeException(e, null);
             return recv.getRuntime().getNil();
         }
     }
 
     /**
      * Same as Java#getInstance(runtime, rawJavaObject, false).
      */
     public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject) {
         return getInstance(runtime, rawJavaObject, false);
     }
     
     /**
      * Returns a new proxy instance of a type corresponding to rawJavaObject's class,
      * or the cached proxy if we've already seen this object.  Note that primitives
      * and strings are <em>not</em> coerced to corresponding Ruby types; use
      * JavaUtil.convertJavaToUsableRubyObject to get coerced types or proxies as
      * appropriate.
      * 
      * @param runtime the JRuby runtime
      * @param rawJavaObject the object to get a wrapper for
      * @param forceCache whether to force the use of the proxy cache
      * @return the new (or cached) proxy for the specified Java object
      * @see JavaUtil#convertJavaToUsableRubyObject
      */
     public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject, boolean forceCache) {
         if (rawJavaObject != null) {
             RubyClass proxyClass = (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, rawJavaObject.getClass()));
 
             if (OBJECT_PROXY_CACHE || forceCache || proxyClass.getCacheProxy()) {
                 return runtime.getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject, proxyClass);
             } else {
                 return allocateProxy(rawJavaObject, proxyClass);
             }
         }
         return runtime.getNil();
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
                 interfaceModule.setInstanceVariable("@java_class", javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 Class<?>[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0;) {
                     JavaClass extendedClass = JavaClass.get(runtime, extended[i]);
                     RubyModule extModule = getInterfaceModule(runtime, extendedClass);
                     interfaceModule.includeModule(extModule);
                 }
                 addToJavaPackageModule(interfaceModule, javaClass);
             }
         } finally {
             javaClass.unlockProxy();
         }
         return interfaceModule;
     }
 
     public static IRubyObject get_interface_module(Ruby runtime, IRubyObject javaClassObject) {
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.forNameVerbose(runtime, javaClassObject.asJavaString());
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         return getInterfaceModule(runtime, javaClass);
     }
 
     public static RubyClass getProxyClassForObject(Ruby runtime, Object object) {
         return (RubyClass)getProxyClass(runtime, JavaClass.get(runtime, object.getClass()));
     }
 
     public static RubyModule getProxyClass(Ruby runtime, JavaClass javaClass) {
         RubyClass proxyClass;
         final Class<?> c = javaClass.javaClass();
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         if (c.isInterface()) {
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
                     if (NEW_STYLE_EXTENSION) {
                         proxyClass.getMetaClass().defineAnnotatedMethods(NewStyleExtensionInherited.class);
                     } else {
                         proxyClass.getMetaClass().defineAnnotatedMethods(OldStyleExtensionInherited.class);
                     }
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
                         // java.util.Map type object has its own proxy, but following
                         // is needed. Unless kind_of?(is_a?) test will fail.
                         //if (interfaces[i] != java.util.Map.class) {
                             proxyClass.includeModule(getInterfaceModule(runtime, ifc));
                         //}
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxyClass, javaClass);
                     }
                 }
 
                 // JRUBY-1000, fail early when attempting to subclass a final Java class;
                 // solved here by adding an exception-throwing "inherited"
                 if (Modifier.isFinal(c.getModifiers())) {
                     proxyClass.getMetaClass().addMethod("inherited", new org.jruby.internal.runtime.methods.JavaMethod(proxyClass, PUBLIC) {
                         @Override
                         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                             throw context.getRuntime().newTypeError("can not extend final Java class: " + c.getCanonicalName());
                         }
                     });
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
         if (proxyClass != null) return proxyClass;
 
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass) baseType;
         proxyClass = RubyClass.newClass(runtime, superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         try {
             javaClass.javaClass().asSubclass(java.util.Map.class);
             proxyClass.setAllocator(runtime.getJavaSupport().getMapJavaProxyClass().getAllocator());
             proxyClass.defineAnnotatedMethods(MapJavaProxy.class);
             proxyClass.includeModule(runtime.getEnumerable());
         } catch (ClassCastException e) {
             proxyClass.setAllocator(superClass.getAllocator());
         }
         if (invokeInherited) {
             proxyClass.inherit(superClass);
         }
         proxyClass.callMethod(runtime.getCurrentContext(), "java_class=", javaClass);
         javaClass.setupProxy(proxyClass);
 
         // add java_method for unbound use
         proxyClass.defineAnnotatedMethods(JavaProxyClassMethods.class);
         return proxyClass;
     }
 
     public static class JavaProxyClassMethods {
         @JRubyMethod(meta = true)
         public static IRubyObject java_method(ThreadContext context, IRubyObject proxyClass, IRubyObject rubyName) {
             String name = rubyName.asJavaString();
 
             return getRubyMethod(context, proxyClass, name);
         }
 
         @JRubyMethod(meta = true)
         public static IRubyObject java_method(ThreadContext context, IRubyObject proxyClass, IRubyObject rubyName, IRubyObject argTypes) {
             String name = rubyName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
 
             return getRubyMethod(context, proxyClass, name, argTypesClasses);
         }
 
         @JRubyMethod(meta = true)
         public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName) {
             String name = rubyName.asJavaString();
             Ruby runtime = context.getRuntime();
 
             JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name));
             return method.invokeStaticDirect();
         }
 
         @JRubyMethod(meta = true)
         public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName, IRubyObject argTypes) {
             String name = rubyName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Ruby runtime = context.getRuntime();
 
             if (argTypesAry.size() != 0) {
                 Class[] argTypesClasses = (Class[]) argTypesAry.toArray(new Class[argTypesAry.size()]);
                 throw JavaMethod.newArgSizeMismatchError(runtime, argTypesClasses);
             }
 
             JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name));
             return method.invokeStaticDirect();
         }
 
         @JRubyMethod(meta = true)
         public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject rubyName, IRubyObject argTypes, IRubyObject arg0) {
             String name = rubyName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Ruby runtime = context.getRuntime();
 
             if (argTypesAry.size() != 1) {
                 throw JavaMethod.newArgSizeMismatchError(runtime, (Class) argTypesAry.eltInternal(0).toJava(Class.class));
             }
 
             Class argTypeClass = (Class) argTypesAry.eltInternal(0).toJava(Class.class);
 
             JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name, argTypeClass));
             return method.invokeStaticDirect(arg0.toJava(argTypeClass));
         }
 
         @JRubyMethod(required = 4, rest = true, meta = true)
         public static IRubyObject java_send(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
             Ruby runtime = context.getRuntime();
 
             String name = args[0].asJavaString();
             RubyArray argTypesAry = args[1].convertToArray();
             int argsLen = args.length - 2;
 
             if (argTypesAry.size() != argsLen) {
                 throw JavaMethod.newArgSizeMismatchError(runtime, (Class[]) argTypesAry.toArray(new Class[argTypesAry.size()]));
             }
 
             Class[] argTypesClasses = (Class[]) argTypesAry.toArray(new Class[argsLen]);
 
             Object[] argsAry = new Object[argsLen];
             for (int i = 0; i < argsLen; i++) {
                 argsAry[i] = args[i + 2].toJava(argTypesClasses[i]);
             }
 
             JavaMethod method = new JavaMethod(runtime, getMethodFromClass(runtime, recv, name, argTypesClasses));
             return method.invokeStaticDirect(argsAry);
         }
 
         @JRubyMethod(meta = true, visibility = PRIVATE)
         public static IRubyObject java_alias(ThreadContext context, IRubyObject proxyClass, IRubyObject newName, IRubyObject rubyName) {
             return java_alias(context, proxyClass, newName, rubyName, context.getRuntime().newEmptyArray());
         }
 
         @JRubyMethod(meta = true, visibility = PRIVATE)
         public static IRubyObject java_alias(ThreadContext context, IRubyObject proxyClass, IRubyObject newName, IRubyObject rubyName, IRubyObject argTypes) {
             String name = rubyName.asJavaString();
             String newNameStr = newName.asJavaString();
             RubyArray argTypesAry = argTypes.convertToArray();
             Class[] argTypesClasses = (Class[])argTypesAry.toArray(new Class[argTypesAry.size()]);
             Ruby runtime = context.getRuntime();
             RubyClass rubyClass;
 
             if (proxyClass instanceof RubyClass) {
                 rubyClass = (RubyClass)proxyClass;
             } else {
                 throw runtime.newTypeError(proxyClass, runtime.getModule());
             }
 
             Method method = getMethodFromClass(runtime, proxyClass, name, argTypesClasses);
             MethodInvoker invoker = getMethodInvokerForMethod(rubyClass, method);
 
             if (Modifier.isStatic(method.getModifiers())) {
                 // add alias to meta
                 rubyClass.getSingletonClass().addMethod(newNameStr, invoker);
             } else {
                 rubyClass.addMethod(newNameStr, invoker);
             }
 
             return runtime.getNil();
         }
     }
 
     private static IRubyObject getRubyMethod(ThreadContext context, IRubyObject proxyClass, String name, Class... argTypesClasses) {
         Ruby runtime = context.getRuntime();
         RubyClass rubyClass;
         
         if (proxyClass instanceof RubyClass) {
             rubyClass = (RubyClass)proxyClass;
         } else {
             throw runtime.newTypeError(proxyClass, runtime.getModule());
         }
 
         Method jmethod = getMethodFromClass(runtime, proxyClass, name, argTypesClasses);
         String prettyName = name + CodegenUtils.prettyParams(argTypesClasses);
         
         if (Modifier.isStatic(jmethod.getModifiers())) {
             MethodInvoker invoker = new StaticMethodInvoker(rubyClass, jmethod);
             return RubyMethod.newMethod(rubyClass, prettyName, rubyClass, name, invoker, proxyClass);
         } else {
             MethodInvoker invoker = new InstanceMethodInvoker(rubyClass, jmethod);
             return RubyUnboundMethod.newUnboundMethod(rubyClass, prettyName, rubyClass, name, invoker);
         }
     }
 
     public static Method getMethodFromClass(Ruby runtime, IRubyObject proxyClass, String name, Class... argTypes) {
         Class jclass = (Class)((JavaClass)proxyClass.callMethod(runtime.getCurrentContext(), "java_class")).getValue();
 
         try {
             return jclass.getMethod(name, argTypes);
         } catch (NoSuchMethodException nsme) {
             String prettyName = name + CodegenUtils.prettyParams(argTypes);
             String errorName = jclass.getName() + "." + prettyName;
             throw runtime.newNameError("Java method not found: " + errorName, name);
         }
     }
 
     private static MethodInvoker getMethodInvokerForMethod(RubyClass metaClass, Method method) {
         if (Modifier.isStatic(method.getModifiers())) {
             return new StaticMethodInvoker(metaClass.getMetaClass(), method);
         } else {
             return new InstanceMethodInvoker(metaClass, method);
         }
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
-        Ruby runtime = context.getRuntime();
+        final Ruby runtime = context.getRuntime();
 
         if (!(subclass instanceof RubyClass)) {
             throw runtime.newTypeError(subclass, runtime.getClassClass());
         }
         RubyClass rubySubclass = (RubyClass)subclass;
         rubySubclass.getInstanceVariables().setInstanceVariable("@java_proxy_class", runtime.getNil());
 
         // Subclasses of Java classes can safely use ivars, so we set this to silence warnings
         rubySubclass.setCacheProxy(true);
 
         RubyClass subclassSingleton = rubySubclass.getSingletonClass();
         subclassSingleton.addReadWriteAttribute(context, "java_proxy_class");
         subclassSingleton.addMethod("java_interfaces", new JavaMethodZero(subclassSingleton, PUBLIC) {
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 IRubyObject javaInterfaces = self.getInstanceVariables().getInstanceVariable("@java_interfaces");
                 if (javaInterfaces != null) return javaInterfaces.dup();
                 return context.getRuntime().getNil();
             }
         });
 
         rubySubclass.addMethod("__jcreate!", new JavaMethodN(subclassSingleton, PUBLIC) {
             private final Map<Integer, ParameterTypes> methodCache = new HashMap<Integer, ParameterTypes>();
             @Override
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args) {
                 IRubyObject proxyClass = self.getMetaClass().getInstanceVariables().getInstanceVariable("@java_proxy_class");
                 if (proxyClass == null || proxyClass.isNil()) {
                     proxyClass = JavaProxyClass.get_with_class(self, self.getMetaClass());
                     self.getMetaClass().getInstanceVariables().setInstanceVariable("@java_proxy_class", proxyClass);
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
-                    throw context.getRuntime().newArgumentError("wrong number of arguments for constructor");
+                    throw runtime.newArgumentError("wrong number of arguments for constructor");
                 }
 
                 JavaProxyConstructor matching = (JavaProxyConstructor)CallableSelector.matchingCallableArityN(
-                        methodCache,
+                        runtime, methodCache,
                         forArity.toArray(new JavaProxyConstructor[forArity.size()]), args, args.length);
 
                 if (matching == null) {
-                    throw context.getRuntime().newArgumentError("wrong number of arguments for constructor");
+                    throw runtime.newArgumentError("wrong number of arguments for constructor");
                 }
 
                 Object[] newArgs = new Object[args.length];
                 Class[] parameterTypes = matching.getParameterTypes();
                 for (int i = 0; i < args.length; i++) {
                     newArgs[i] = args[i].toJava(parameterTypes[i]);
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
         Ruby runtime = proxyClass.getRuntime();
         Class<?> clazz = javaClass.javaClass();
         String fullName;
         if ((fullName = clazz.getName()) == null) {
             return;
         }
         int endPackage = fullName.lastIndexOf('.');
         RubyModule parentModule;
         String className;
 
         // inner classes must be nested
         if (fullName.indexOf('$') != -1) {
             IRubyObject declClass = javaClass.declaring_class();
             if (declClass.isNil()) {
                 // no containing class for a $ class; treat it as internal and don't define a constant
                 return;
             }
             parentModule = getProxyClass(runtime, (JavaClass)declClass);
             className = clazz.getSimpleName();
         } else {
             String packageString = endPackage < 0 ? "" : fullName.substring(0, endPackage);
             parentModule = getJavaPackageModule(runtime, packageString);
             className = parentModule == null ? fullName : fullName.substring(endPackage + 1);
         }
         
         if (parentModule != null && IdUtil.isConstant(className)) {
             if (parentModule.getConstantAt(className) == null) {
                 parentModule.setConstant(className, proxyClass);
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
         packageModule.setInstanceVariable("@package_name", runtime.newString(
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
 
     private static RubyModule getPackageModule(Ruby runtime, String name) {
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
 
     private static RubyModule getProxyOrPackageUnderPackage(ThreadContext context, final Ruby runtime,
             RubyModule parentPackage, String sym) {
         IRubyObject packageNameObj = parentPackage.getInstanceVariable("@package_name");
         if (packageNameObj == null) {
             throw runtime.newArgumentError("invalid package module");
         }
         String packageName = packageNameObj.asJavaString();
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         String fullName = packageName + name;
         if (!Character.isUpperCase(name.charAt(0))) {
             // filter out any Java primitive names
             // TODO: should check against all Java reserved names here, not just primitives
             if (JAVA_PRIMITIVES.containsKey(name)) {
                 throw runtime.newArgumentError("illegal package name component: " + name);
             }
             
             // this covers the rare case of lower-case class names (and thus will
             // fail 99.999% of the time). fortunately, we'll only do this once per
             // package name. (and seriously, folks, look into best practices...)
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, fullName));
             } catch (RaiseException re) { /* expected */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* expected */ }
 
             // Haven't found a class, continue on as though it were a package
             final RubyModule packageModule = getJavaPackageModule(runtime, fullName);
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if (packageModule == null) {
                 return null;
             }
 
             // save package in singletonized parent, so we don't come back here
             memoizePackageOrClass(parentPackage, name, packageModule);
             
             return packageModule;
         } else {
             try {
                 // First char is upper case, so assume it's a class name
                 final RubyModule javaModule = getProxyClass(runtime, JavaClass.forNameVerbose(runtime, fullName));
                 
                 // save class in singletonized parent, so we don't come back here
                 memoizePackageOrClass(parentPackage, name, javaModule);
 
                 return javaModule;
             } catch (Exception e) {
                 if (RubyInstanceConfig.UPPER_CASE_PACKAGE_NAME_ALLOWED) {
                     // but for those not hip to conventions and best practices,
                     // we'll try as a package
                     return getJavaPackageModule(runtime, fullName);
                 } else {
                     throw runtime.newNameError("uppercase package names not accessible this way (`" + fullName + "')", fullName);
                 }
             }
 
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
 
     private static RubyModule getTopLevelProxyOrPackage(ThreadContext context, final Ruby runtime, String sym) {
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
 
             final RubyModule packageModule = getJavaPackageModule(runtime, name);
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if (packageModule == null) {
                 return null;
             }
             RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
             if (javaModule.getMetaClass().isMethodBound(name, false)) {
                 return packageModule;
             }
 
             memoizePackageOrClass(javaModule, name, packageModule);
             
             return packageModule;
         } else {
             RubyModule javaModule = null;
             try {
                 // we do loadJavaClass here to handle things like LinkageError through
                 Class cls = runtime.getJavaSupport().loadJavaClass(name);
                 javaModule = getProxyClass(runtime, JavaClass.get(runtime, cls));
             } catch (ExceptionInInitializerError eiie) {
                 throw runtime.newNameError("cannot initialize Java class " + name, name, eiie, false);
             } catch (LinkageError le) {
                 throw runtime.newNameError("cannot link Java class " + name, name, le, false);
             } catch (SecurityException se) {
                 throw runtime.newSecurityError(se.getLocalizedMessage());
             } catch (ClassNotFoundException e) { /* not a class */ }
 
             // upper-case package name
             // TODO: top-level upper-case package was supported in the previous (Ruby-based)
             // implementation, so leaving as is.  see note at #getProxyOrPackageUnderPackage
             // re: future approach below the top-level.
             if (javaModule == null) {
                 javaModule = getPackageModule(runtime, name);
             }
 
             memoizePackageOrClass(runtime.getJavaSupport().getJavaModule(), name, javaModule);
 
             return javaModule;
         }
     }
 
     private static void memoizePackageOrClass(final RubyModule parentPackage, final String name, final IRubyObject value) {
         RubyClass singleton = parentPackage.getSingletonClass();
         singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, PUBLIC) {
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                 if (args.length != 0) {
                     throw context.getRuntime().newArgumentError(
                             "Java package `"
                             + parentPackage.callMethod("package_name")
                             + "' does not have a method `"
                             + name
                             + "'");
                 }
                 return call(context, self, clazz, name);
             }
 
             public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                 return value;
             }
 
             @Override
             public Arity getArity() {
                 return Arity.noArguments();
             }
         });
     }
 
     public static IRubyObject get_top_level_proxy_or_package(ThreadContext context, IRubyObject recv, IRubyObject sym) {
         Ruby runtime = context.getRuntime();
         RubyModule result = getTopLevelProxyOrPackage(context, runtime, sym.asJavaString());
 
         return result != null ? result : runtime.getNil();
     }
 
     public static IRubyObject wrap(Ruby runtime, IRubyObject java_object) {
         return getInstance(runtime, ((JavaObject) java_object).getValue());
     }
     
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version
      */
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject java_to_ruby(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         try {
             return JavaUtil.java_to_ruby(recv.getRuntime(), object);
         } catch (RuntimeException e) {
             recv.getRuntime().getJavaSupport().handleNativeException(e, null);
             // This point is only reached if there was an exception handler installed.
             return recv.getRuntime().getNil();
         }
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility.
      */
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.ruby_to_java(recv, object, unusedBlock);
     }
 
     @Deprecated
     @JRubyMethod(frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         return JavaUtil.java_to_primitive(recv, object, unusedBlock);
     }
 
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     @JRubyMethod(required = 2, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject new_proxy_instance2(IRubyObject recv, final IRubyObject wrapper, IRubyObject ifcs, Block block) {
         IRubyObject[] javaClasses = ((RubyArray)ifcs).toJavaArray();
 
         // Create list of interface names to proxy (and make sure they really are interfaces)
         // Also build a hashcode from all classes to use for retrieving previously-created impl
         Class[] interfaces = new Class[javaClasses.length];
         for (int i = 0; i < javaClasses.length; i++) {
             if (!(javaClasses[i] instanceof JavaClass) || !((JavaClass) javaClasses[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + javaClasses[i]);
             }
             interfaces[i] = ((JavaClass) javaClasses[i]).javaClass();
         }
 
         return newInterfaceImpl(wrapper, interfaces);
     }
 
     public static IRubyObject newInterfaceImpl(final IRubyObject wrapper, Class[] interfaces) {
         final Ruby runtime = wrapper.getRuntime();
         ClassDefiningClassLoader classLoader;
 
         Class[] tmp_interfaces = interfaces;
         interfaces = new Class[tmp_interfaces.length + 1];
         System.arraycopy(tmp_interfaces, 0, interfaces, 0, tmp_interfaces.length);
         interfaces[tmp_interfaces.length] = RubyObjectHolderProxy.class;
 
         // hashcode is a combination of the interfaces and the Ruby class we're using
         // to implement them
         if (!RubyInstanceConfig.INTERFACES_USE_PROXY) {
             int interfacesHashCode = interfacesHashCode(interfaces);
             // if it's a singleton class and the real class is proc, we're doing closure conversion
             // so just use Proc's hashcode
             if (wrapper.getMetaClass().isSingleton() && wrapper.getMetaClass().getRealClass() == runtime.getProc()) {
                 interfacesHashCode = 31 * interfacesHashCode + runtime.getProc().hashCode();
                 classLoader = runtime.getJRubyClassLoader();
             } else {
                 // normal new class implementing interfaces
                 interfacesHashCode = 31 * interfacesHashCode + wrapper.getMetaClass().getRealClass().hashCode();
                 classLoader = new OneShotClassLoader(runtime.getJRubyClassLoader());
             }
             String implClassName = "org.jruby.gen.InterfaceImpl" + Math.abs(interfacesHashCode);
             Class proxyImplClass;
             try {
                 proxyImplClass = Class.forName(implClassName, true, runtime.getJRubyClassLoader());
             } catch (ClassNotFoundException cnfe) {
                 proxyImplClass = RealClassGenerator.createOldStyleImplClass(interfaces, wrapper.getMetaClass(), runtime, implClassName, classLoader);
             }
 
             try {
                 Constructor proxyConstructor = proxyImplClass.getConstructor(IRubyObject.class);
                 return JavaObject.wrap(runtime, proxyConstructor.newInstance(wrapper));
             } catch (NoSuchMethodException nsme) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + nsme);
             } catch (InvocationTargetException ite) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ite);
             } catch (InstantiationException ie) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ie);
             } catch (IllegalAccessException iae) {
                 throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + iae);
             }
         } else {
             Object proxyObject = Proxy.newProxyInstance(runtime.getJRubyClassLoader(), interfaces, new InvocationHandler() {
                 private Map parameterTypeCache = new ConcurrentHashMap();
 
                 public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                     String methodName = method.getName();
                     int length = nargs == null ? 0 : nargs.length;
 
                     // FIXME: wtf is this? Why would these use the class?
                     if (methodName == "toString" && length == 0) {
                         return proxy.getClass().getName();
                     } else if (methodName == "hashCode" && length == 0) {
                         return Integer.valueOf(proxy.getClass().hashCode());
                     } else if (methodName == "equals" && length == 1) {
                         Class[] parameterTypes = (Class[]) parameterTypeCache.get(method);
                         if (parameterTypes == null) {
                             parameterTypes = method.getParameterTypes();
                             parameterTypeCache.put(method, parameterTypes);
                         }
                         if (parameterTypes[0].equals(Object.class)) {
                             return Boolean.valueOf(proxy == nargs[0]);
                         }
                     } else if (methodName == "__ruby_object" && length == 0) {
                         return wrapper;
                     }
 
                     IRubyObject[] rubyArgs = JavaUtil.convertJavaArrayToRuby(runtime, nargs);
                     try {
                         return RuntimeHelpers.invoke(runtime.getCurrentContext(), wrapper, methodName, rubyArgs).toJava(method.getReturnType());
                     } catch (RuntimeException e) { e.printStackTrace(); throw e; }
                 }
             });
             return JavaObject.wrap(runtime, proxyObject);
         }
     }
 
     public static Class generateRealClass(final RubyClass clazz) {
         final Ruby runtime = clazz.getRuntime();
         final Class[] interfaces = getInterfacesFromRubyClass(clazz);
 
         // hashcode is a combination of the interfaces and the Ruby class we're using
         // to implement them
         int interfacesHashCode = interfacesHashCode(interfaces);
         // normal new class implementing interfaces
         interfacesHashCode = 31 * interfacesHashCode + clazz.hashCode();
         
         String implClassName;
         if (clazz.getBaseName() == null) {
             // no-name class, generate a bogus name for it
             implClassName = "anon_class" + Math.abs(System.identityHashCode(clazz)) + "_" + Math.abs(interfacesHashCode);
         } else {
             implClassName = clazz.getName().replaceAll("::", "\\$\\$") + "_" + Math.abs(interfacesHashCode);
         }
         Class proxyImplClass;
         try {
             proxyImplClass = Class.forName(implClassName, true, runtime.getJRubyClassLoader());
         } catch (ClassNotFoundException cnfe) {
             // try to use super's reified class; otherwise, RubyObject (for now)
             Class superClass = clazz.getSuperClass().getRealClass().getReifiedClass();
             if (superClass == null) {
                 superClass = RubyObject.class;
             }
             proxyImplClass = RealClassGenerator.createRealImplClass(superClass, interfaces, clazz, runtime, implClassName);
             
             // add a default initialize if one does not already exist and this is a Java-hierarchy class
             if (NEW_STYLE_EXTENSION &&
                     !(RubyBasicObject.class.isAssignableFrom(proxyImplClass) || clazz.getMethods().containsKey("initialize"))
                     ) {
                 clazz.addMethod("initialize", new JavaMethodZero(clazz, PUBLIC) {
                     @Override
                     public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name) {
                         return context.getRuntime().getNil();
                     }
                 });
             }
         }
         clazz.setReifiedClass(proxyImplClass);
         clazz.setRubyClassAllocator(proxyImplClass);
 
         return proxyImplClass;
     }
 
     public static Constructor getRealClassConstructor(Ruby runtime, Class proxyImplClass) {
         try {
             return proxyImplClass.getConstructor(Ruby.class, RubyClass.class);
         } catch (NoSuchMethodException nsme) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + nsme);
         }
     }
 
     public static IRubyObject constructProxy(Ruby runtime, Constructor proxyConstructor, RubyClass clazz) {
         try {
             return (IRubyObject)proxyConstructor.newInstance(runtime, clazz);
         } catch (InvocationTargetException ite) {
             ite.printStackTrace();
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ite);
         } catch (InstantiationException ie) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + ie);
         } catch (IllegalAccessException iae) {
             throw runtime.newTypeError("Exception instantiating generated interface impl:\n" + iae);
         }
     }
     
     public static IRubyObject allocateProxy(Object javaObject, RubyClass clazz) {
         IRubyObject proxy = clazz.allocate();
         if (proxy instanceof JavaProxy) {
             ((JavaProxy)proxy).setObject(javaObject);
         } else {
             JavaObject wrappedObject = JavaObject.wrap(clazz.getRuntime(), javaObject);
             proxy.dataWrapStruct(wrappedObject);
         }
 
         return proxy;
     }
 
     public static IRubyObject wrapJavaObject(Ruby runtime, Object object) {
         return allocateProxy(object, getProxyClassForObject(runtime, object));
     }
 
     public static Class[] getInterfacesFromRubyClass(RubyClass klass) {
         Set<Class> interfaces = new HashSet<Class>();
         // walk all superclasses aggregating interfaces
         while (klass != null) {
             IRubyObject maybeInterfaces = klass.getInstanceVariables().getInstanceVariable("@java_interfaces");
             if (maybeInterfaces instanceof RubyArray) {
                 RubyArray moreInterfaces = (RubyArray)maybeInterfaces;
                 if (!moreInterfaces.isFrozen()) moreInterfaces.setFrozen(true);
 
                 interfaces.addAll(moreInterfaces);
             }
             klass = klass.getSuperClass();
         }
 
         return interfaces.toArray(new Class[interfaces.size()]);
     }
     
     private static int interfacesHashCode(Class[] a) {
         if (a == null) {
             return 0;
         }
 
         int result = 1;
 
         for (Class element : a)
             result = 31 * result + (element == null ? 0 : element.hashCode());
 
         return result;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaAccessibleObject.java b/src/org/jruby/javasupport/JavaAccessibleObject.java
index 6f9effa2cd..99c6dfa62a 100644
--- a/src/org/jruby/javasupport/JavaAccessibleObject.java
+++ b/src/org/jruby/javasupport/JavaAccessibleObject.java
@@ -1,157 +1,157 @@
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
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
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
 
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Member;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyObject;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public abstract class JavaAccessibleObject extends RubyObject {
 
     protected JavaAccessibleObject(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
 
     public static void registerRubyMethods(Ruby runtime, RubyClass result) {
         result.defineAnnotatedMethods(JavaAccessibleObject.class);
     }
 
-    protected abstract AccessibleObject accessibleObject();
+    public abstract AccessibleObject accessibleObject();
 
     public boolean equals(Object other) {
         return other instanceof JavaAccessibleObject &&
                 this.accessibleObject() == ((JavaAccessibleObject) other).accessibleObject();
     }
 
     public int hashCode() {
         return this.accessibleObject().hashCode();
     }
 
     @JRubyMethod
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     @JRubyMethod(name = {"==", "eql?"})
     public IRubyObject op_equal(IRubyObject other) {
         return other instanceof JavaAccessibleObject && accessibleObject().equals(((JavaAccessibleObject) other).accessibleObject()) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "equal?")
     public IRubyObject same(IRubyObject other) {
         return getRuntime().newBoolean(equals(other));
     }
 
     @JRubyMethod(name = "accessible?")
     public RubyBoolean isAccessible() {
         return RubyBoolean.newBoolean(getRuntime(), accessibleObject().isAccessible());
     }
 
     @JRubyMethod(name = "accessible=")
     public IRubyObject setAccessible(IRubyObject object) {
         accessibleObject().setAccessible(object.isTrue());
         return object;
     }
 
     @SuppressWarnings("unchecked")
     @JRubyMethod
     public IRubyObject annotation(IRubyObject annoClass) {
         if (!(annoClass instanceof JavaClass)) {
             throw getRuntime().newTypeError(annoClass, getRuntime().getJavaSupport().getJavaClassClass());
         }
         return Java.getInstance(getRuntime(), accessibleObject().getAnnotation(((JavaClass) annoClass).javaClass()));
     }
 
     @JRubyMethod
     public IRubyObject annotations() {
         return Java.getInstance(getRuntime(), accessibleObject().getAnnotations());
     }
 
     @JRubyMethod(name = "annotations?")
     public RubyBoolean annotations_p() {
         return getRuntime().newBoolean(accessibleObject().getAnnotations().length > 0);
     }
 
     @JRubyMethod
     public IRubyObject declared_annotations() {
         return Java.getInstance(getRuntime(), accessibleObject().getDeclaredAnnotations());
     }
 
     @JRubyMethod(name = "declared_annotations?")
     public RubyBoolean declared_annotations_p() {
         return getRuntime().newBoolean(accessibleObject().getDeclaredAnnotations().length > 0);
     }
 
     @JRubyMethod(name = "annotation_present?")
     public IRubyObject annotation_present_p(IRubyObject annoClass) {
         if (!(annoClass instanceof JavaClass)) {
             throw getRuntime().newTypeError(annoClass, getRuntime().getJavaSupport().getJavaClassClass());
         }
         return getRuntime().newBoolean(this.accessibleObject().isAnnotationPresent(((JavaClass) annoClass).javaClass()));
     }
 
     // for our purposes, Accessibles are also Members, and vice-versa,
     // so we'll include Member methods here.
     @JRubyMethod
     public IRubyObject declaring_class() {
         Class<?> clazz = ((Member) accessibleObject()).getDeclaringClass();
         if (clazz != null) {
             return JavaClass.get(getRuntime(), clazz);
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod
     public IRubyObject modifiers() {
         return getRuntime().newFixnum(((Member) accessibleObject()).getModifiers());
     }
 
     @JRubyMethod
     public IRubyObject name() {
         return getRuntime().newString(((Member) accessibleObject()).getName());
     }
 
     @JRubyMethod(name = "synthetic?")
     public IRubyObject synthetic_p() {
         return getRuntime().newBoolean(((Member) accessibleObject()).isSynthetic());
     }
 
     @JRubyMethod(name = {"to_s", "to_string"})
     public RubyString to_string() {
         return getRuntime().newString(accessibleObject().toString());
     }
 }
diff --git a/src/org/jruby/javasupport/JavaConstructor.java b/src/org/jruby/javasupport/JavaConstructor.java
index b27fc0088a..c0f35df97d 100644
--- a/src/org/jruby/javasupport/JavaConstructor.java
+++ b/src/org/jruby/javasupport/JavaConstructor.java
@@ -1,350 +1,350 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
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
 
 import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Type;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="Java::JavaConstructor")
 public class JavaConstructor extends JavaCallable {
     private final Constructor<?> constructor;
     private final JavaUtil.JavaConverter objectConverter;
 
     public Object getValue() {
         return constructor;
     }
 
     public static RubyClass createJavaConstructorClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result =
                 javaModule.defineClassUnder("JavaConstructor", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         JavaCallable.registerRubyMethods(runtime, result);
         
         result.defineAnnotatedMethods(JavaConstructor.class);
         
         return result;
     }
 
     public JavaConstructor(Ruby runtime, Constructor<?> constructor) {
         super(runtime, runtime.getJavaSupport().getJavaConstructorClass(), constructor.getParameterTypes());
         this.constructor = constructor;
         
         this.objectConverter = JavaUtil.getJavaConverter(constructor.getDeclaringClass());
     }
 
     public static JavaConstructor create(Ruby runtime, Constructor<?> constructor) {
         return new JavaConstructor(runtime, constructor);
     }
     
     public static JavaConstructor getMatchingConstructor(Ruby runtime, Class<?> javaClass, Class<?>[] argumentTypes) {
         try {
             return create(runtime, javaClass.getConstructor(argumentTypes));
         } catch (NoSuchMethodException e) {
             // Java reflection does not allow retrieving constructors like methods
             CtorSearch: for (Constructor<?> ctor : javaClass.getConstructors()) {
                 Class<?>[] targetTypes = ctor.getParameterTypes();
                 
                 // for zero args case we can stop searching
                 if (targetTypes.length != argumentTypes.length) {
                     continue CtorSearch;
                 } else if (targetTypes.length == 0 && argumentTypes.length == 0) {
                     return create(runtime, ctor);
                 } else {
                     boolean found = true;
                     
                     TypeScan: for (int i = 0; i < argumentTypes.length; i++) {
                         if (i >= targetTypes.length) found = false;
                         
                         if (targetTypes[i].isAssignableFrom(argumentTypes[i])) {
                             found = true;
                             continue TypeScan;
                         } else {
                             found = false;
                             continue CtorSearch;
                         }
                     }
 
                     // if we get here, we found a matching method, use it
                     // TODO: choose narrowest method by continuing to search
                     if (found) {
                         return create(runtime, ctor);
                     }
                 }
             }
         }
         // no matching ctor found
         return null;
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaConstructor &&
             this.constructor == ((JavaConstructor)other).constructor;
     }
     
     public int hashCode() {
         return constructor.hashCode();
     }
 
     public int getArity() {
         return parameterTypes.length;
     }
     
     protected String nameOnInspection() {
         return getType().toString();
     }
 
     public Class<?>[] getParameterTypes() {
         return parameterTypes;
     }
 
     public Class<?>[] getExceptionTypes() {
         return constructor.getExceptionTypes();
     }
 
     public Type[] getGenericParameterTypes() {
         return constructor.getGenericParameterTypes();
     }
 
     public Type[] getGenericExceptionTypes() {
         return constructor.getGenericExceptionTypes();
     }
 
     public Annotation[][] getParameterAnnotations() {
         return constructor.getParameterAnnotations();
     }
     
     public boolean isVarArgs() {
         return constructor.isVarArgs();
     }
 
     public int getModifiers() {
         return constructor.getModifiers();
     }
     
     public String toGenericString() {
         return constructor.toGenericString();
     }
 
-    protected AccessibleObject accessibleObject() {
+    public AccessibleObject accessibleObject() {
         return constructor;
     }
     
     @JRubyMethod
     public IRubyObject type_parameters() {
         return Java.getInstance(getRuntime(), constructor.getTypeParameters());
     }
 
     @JRubyMethod
     public IRubyObject return_type() {
         return getRuntime().getNil();
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject new_instance(IRubyObject[] args) {
         int length = args.length;
         Class<?>[] types = parameterTypes;
         if (length != types.length) {
             throw getRuntime().newArgumentError(length, types.length);
         }
         Object[] constructorArguments = new Object[length];
         for (int i = length; --i >= 0; ) {
             constructorArguments[i] = args[i].toJava(types[i]);
         }
         try {
             Object result = constructor.newInstance(constructorArguments);
             return JavaObject.wrap(getRuntime(), result);
 
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError("expected " + argument_types().inspect() +
                                               ", got [" + constructorArguments[0].getClass().getName() + ", ...]");
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access");
         } catch (InvocationTargetException ite) {
             getRuntime().getJavaSupport().handleNativeException(ite.getTargetException(), constructor);
             // not reached
             assert false;
             return null;
         } catch (InstantiationException ie) {
             throw getRuntime().newTypeError("can't make instance of " + constructor.getDeclaringClass().getName());
         }
     }
 
     public IRubyObject new_instance(Object[] arguments) {
         checkArity(arguments.length);
 
         try {
             Object result = constructor.newInstance(arguments);
             return JavaObject.wrap(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError("expected " + argument_types().inspect() +
                                               ", got [" + arguments[0].getClass().getName() + ", ...]");
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access");
         } catch (InvocationTargetException ite) {
             getRuntime().getJavaSupport().handleNativeException(ite.getTargetException(), constructor);
             // not reached
             assert false;
             return null;
         } catch (InstantiationException ie) {
             throw getRuntime().newTypeError("can't make instance of " + constructor.getDeclaringClass().getName());
         }
     }
 
     public Object newInstanceDirect(Object... arguments) {
         checkArity(arguments.length);
 
         try {
             return constructor.newInstance(arguments);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, constructor);
         } catch (Throwable t) {
             return handleThrowable(t, constructor);
         }
     }
 
     public Object newInstanceDirect() {
         checkArity(0);
 
         try {
             return constructor.newInstance();
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(iae);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, constructor);
         } catch (Throwable t) {
             return handleThrowable(t, constructor);
         }
     }
 
     public Object newInstanceDirect(Object arg0) {
         checkArity(1);
 
         try {
             return constructor.newInstance(arg0);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(iae, arg0);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, constructor);
         } catch (Throwable t) {
             return handleThrowable(t, constructor);
         }
     }
 
     public Object newInstanceDirect(Object arg0, Object arg1) {
         checkArity(2);
 
         try {
             return constructor.newInstance(arg0, arg1);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(iae, arg0, arg1);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, constructor);
         } catch (Throwable t) {
             return handleThrowable(t, constructor);
         }
     }
 
     public Object newInstanceDirect(Object arg0, Object arg1, Object arg2) {
         checkArity(3);
 
         try {
             return constructor.newInstance(arg0, arg1, arg2);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(iae, arg0, arg1, arg2);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, constructor);
         } catch (Throwable t) {
             return handleThrowable(t, constructor);
         }
     }
 
     public Object newInstanceDirect(Object arg0, Object arg1, Object arg2, Object arg3) {
         checkArity(4);
 
         try {
             return constructor.newInstance(arg0, arg1, arg2, arg3);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(iae, arg0, arg1, arg2, arg3);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, constructor);
         } catch (Throwable t) {
             return handleThrowable(t, constructor);
         }
     }
 
     private IRubyObject handleIllegalAccessEx(IllegalAccessException iae) {
         throw getRuntime().newTypeError("illegal access on constructor for type " + constructor.getDeclaringClass().getSimpleName() + ": " + iae.getMessage());
     }
 
     private IRubyObject handlelIllegalArgumentEx(IllegalArgumentException iae, Object... arguments) {
         throw getRuntime().newTypeError(
                 "for constructor of type " +
                 constructor.getDeclaringClass().getSimpleName() +
                 " expected " +
                 argument_types().inspect() +
                 "; got: " +
                 dumpArgTypes(arguments) +
                 "; error: " +
                 iae.getMessage());
     }
 }
diff --git a/src/org/jruby/javasupport/JavaField.java b/src/org/jruby/javasupport/JavaField.java
index 575f1a20e1..7ad39cdf93 100644
--- a/src/org/jruby/javasupport/JavaField.java
+++ b/src/org/jruby/javasupport/JavaField.java
@@ -1,219 +1,219 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 package org.jruby.javasupport;
 
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Field;
 import java.lang.reflect.Modifier;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="Java::JavaField")
 public class JavaField extends JavaAccessibleObject {
     private Field field;
 
     public Object getValue() {
         return field;
     }
 
     public static RubyClass createJavaFieldClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = javaModule.defineClassUnder("JavaField", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         
         result.defineAnnotatedMethods(JavaField.class);
 
         return result;
     }
 
     public JavaField(Ruby runtime, Field field) {
         super(runtime, runtime.getJavaSupport().getJavaFieldClass());
         this.field = field;
     }
 
     public boolean equals(Object other) {
         return other instanceof JavaField &&
             this.field == ((JavaField)other).field;
     }
     
     public int hashCode() {
         return field.hashCode();
     }
 
     @JRubyMethod
     public RubyString value_type() {
         return getRuntime().newString(field.getType().getName());
     }
 
     @JRubyMethod(name = {"==", "==="})
     public IRubyObject op_equal(IRubyObject other) {
     	if (!(other instanceof JavaField)) {
     		return getRuntime().getFalse();
     	}
     	
         return getRuntime().newBoolean(field.equals(((JavaField) other).field));
     }
 
     @JRubyMethod(name = "public?")
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(field.getModifiers()));
     }
 
     @JRubyMethod(name = "static?")
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(Modifier.isStatic(field.getModifiers()));
     }
     
     @JRubyMethod(name = "enum_constant?")
     public RubyBoolean enum_constant_p() {
         return getRuntime().newBoolean(field.isEnumConstant());
     }
 
     @JRubyMethod
     public RubyString to_generic_string() {
         return getRuntime().newString(field.toGenericString());
     }
     
     @JRubyMethod(name = "type")
     public IRubyObject field_type() {
         return JavaClass.get(getRuntime(), field.getType());
     }
 
     @JRubyMethod
     public IRubyObject value(ThreadContext context, IRubyObject object) {
         Ruby runtime = context.getRuntime();
 
         Object javaObject = null;
         if (!Modifier.isStatic(field.getModifiers())) {
             javaObject = JavaUtil.unwrapJavaValue(runtime, object, "not a java object");
         }
         try {
             return JavaUtil.convertJavaToUsableRubyObject(runtime, field.get(javaObject));
         } catch (IllegalAccessException iae) {
             throw runtime.newTypeError("illegal access");
         }
     }
 
     @JRubyMethod
     public IRubyObject set_value(IRubyObject object, IRubyObject value) {
         Object javaObject = null;
         if (!Modifier.isStatic(field.getModifiers())) {
             javaObject  = JavaUtil.unwrapJavaValue(getRuntime(), object, "not a java object: " + object);
         }
         IRubyObject val = value;
         if(val.dataGetStruct() instanceof JavaObject) {
             val = (IRubyObject)val.dataGetStruct();
         }
         try {
             Object convertedValue = val.toJava(field.getType());
 
             field.set(javaObject, convertedValue);
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError(
                                 "illegal access on setting variable: " + iae.getMessage());
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError(
                                 "wrong type for " + field.getType().getName() + ": " +
                                 val.getClass().getName());
         }
         return val;
     }
 
     @JRubyMethod(name = "final?")
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(field.getModifiers()));
     }
 
     @JRubyMethod
     public JavaObject static_value() {
         try {
             // TODO: Only setAccessible to account for pattern found by
             // accessing constants included from a non-public interface.
             // (aka java.util.zip.ZipConstants being implemented by many
             // classes)
             if (!Ruby.isSecurityRestricted()) {
                 field.setAccessible(true);
             }
             return JavaObject.wrap(getRuntime(), field.get(null));
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal static value access: " + iae.getMessage());
         }
     }
 
     @JRubyMethod
     public JavaObject set_static_value(IRubyObject value) {
         if (! (value instanceof JavaObject)) {
             throw getRuntime().newTypeError("not a java object:" + value);
         }
         try {
             Object convertedValue = value.toJava(field.getType());
             // TODO: Only setAccessible to account for pattern found by
             // accessing constants included from a non-public interface.
             // (aka java.util.zip.ZipConstants being implemented by many
             // classes)
             // TODO: not sure we need this at all, since we only expose
             // public fields.
             //field.setAccessible(true);
             field.set(null, convertedValue);
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError(
                                 "illegal access on setting static variable: " + iae.getMessage());
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError(
                                 "wrong type for " + field.getType().getName() + ": " +
                                 ((JavaObject) value).getValue().getClass().getName());
         }
         return (JavaObject) value;
     }
     
     @JRubyMethod
     public RubyString name() {
         return getRuntime().newString(field.getName());
     }
     
-    protected AccessibleObject accessibleObject() {
+    public AccessibleObject accessibleObject() {
         return field;
     }
 }
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index 3084e6c834..aefe6e6854 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,672 +1,672 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 
 import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Type;
 
 @JRubyClass(name="Java::JavaMethod")
 public class JavaMethod extends JavaCallable {
 
     private static final Logger LOG = LoggerFactory.getLogger("JavaMethod");
 
     private final static boolean USE_HANDLES = RubyInstanceConfig.USE_GENERATED_HANDLES;
     private final static boolean HANDLE_DEBUG = false;
     private final Method method;
     private final Class boxedReturnType;
     private final boolean isFinal;
     private final JavaUtil.JavaConverter returnConverter;
 
     public Object getValue() {
         return method;
     }
 
     public static RubyClass createJavaMethodClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = 
             javaModule.defineClassUnder("JavaMethod", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         JavaCallable.registerRubyMethods(runtime, result);
         
         result.defineAnnotatedMethods(JavaMethod.class);
 
         return result;
     }
 
     public JavaMethod(Ruby runtime, Method method) {
         super(runtime, runtime.getJavaSupport().getJavaMethodClass(), method.getParameterTypes());
         this.method = method;
         this.isFinal = Modifier.isFinal(method.getModifiers());
         if (method.getReturnType().isPrimitive() && method.getReturnType() != void.class) {
             this.boxedReturnType = CodegenUtils.getBoxType(method.getReturnType());
         } else {
             this.boxedReturnType = method.getReturnType();
         }
 
         boolean methodIsPublic = Modifier.isPublic(method.getModifiers());
         boolean classIsPublic = Modifier.isPublic(method.getDeclaringClass().getModifiers());
 
         // Special classes like Collections.EMPTY_LIST are inner classes that are private but 
         // implement public interfaces.  Their methods are all public methods for the public 
         // interface.  Let these public methods execute via setAccessible(true).
         if (JavaClass.CAN_SET_ACCESSIBLE) {
             // we should be able to setAccessible ok...
             try {
                 if (methodIsPublic &&
                     !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                     accessibleObject().setAccessible(true);
                 }
             } catch (SecurityException se) {
                 // we shouldn't get here if JavaClass.CAN_SET_ACCESSIBLE is doing
                 // what it should, so we warn.
                runtime.getWarnings().warn("failed to setAccessible: " + accessibleObject() + ", exception follows: " + se.getMessage());
             }
         }
         
         returnConverter = JavaUtil.getJavaConverter(method.getReturnType());
     }
 
     public static JavaMethod create(Ruby runtime, Method method) {
         return new JavaMethod(runtime, method);
     }
 
     public static JavaMethod create(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             Method method = javaClass.getMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod createDeclared(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             return create(runtime, javaClass.getDeclaredMethod(methodName, argumentTypes));
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod getMatchingDeclaredMethod(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
         // include superclass methods.  also, the getDeclared calls may throw SecurityException if
         // we're running under a restrictive security policy.
         try {
             return create(runtime, javaClass.getDeclaredMethod(methodName, argumentTypes));
         } catch (NoSuchMethodException e) {
             // search through all declared methods to find a closest match
             MethodSearch: for (Method method : javaClass.getDeclaredMethods()) {
                 if (method.getName().equals(methodName)) {
                     Class<?>[] targetTypes = method.getParameterTypes();
                 
                     // for zero args case we can stop searching
                     if (targetTypes.length == 0 && argumentTypes.length == 0) {
                         return create(runtime, method);
                     }
                     
                     TypeScan: for (int i = 0; i < argumentTypes.length; i++) {
                         if (i >= targetTypes.length) continue MethodSearch;
 
                         if (targetTypes[i].isAssignableFrom(argumentTypes[i])) {
                             continue TypeScan;
                         } else {
                             continue MethodSearch;
                         }
                     }
 
                     // if we get here, we found a matching method, use it
                     // TODO: choose narrowest method by continuing to search
                     return create(runtime, method);
                 }
             }
         }
         // no matching method found
         return null;
     }
     
     @Override
     public boolean equals(Object other) {
         return other instanceof JavaMethod &&
             this.method == ((JavaMethod)other).method;
     }
     
     @Override
     public int hashCode() {
         return method.hashCode();
     }
 
     @JRubyMethod
     @Override
     public RubyString name() {
         return getRuntime().newString(method.getName());
     }
 
     public int getArity() {
         return parameterTypes.length;
     }
 
     @JRubyMethod(name = "public?")
     @Override
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(method.getModifiers()));
     }
 
     @JRubyMethod(name = "final?")
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(method.getModifiers()));
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke(IRubyObject[] args) {
         checkArity(args.length - 1);
         Object[] arguments = new Object[args.length - 1];
         convertArguments(args, arguments, 1);
 
         IRubyObject invokee = args[0];
         if(invokee.isNil()) {
             return invokeWithExceptionHandling(method, null, arguments);
         }
 
         Object javaInvokee = null;
 
         if (!isStatic()) {
             javaInvokee = JavaUtil.unwrapJavaValue(getRuntime(), invokee, "invokee not a java object");
 
             if (! method.getDeclaringClass().isInstance(javaInvokee)) {
                 throw getRuntime().newTypeError("invokee not instance of method's class (" +
                                                   "got" + javaInvokee.getClass().getName() + " wanted " +
                                                   method.getDeclaringClass().getName() + ")");
             }
 
             //
             // this test really means, that this is a ruby-defined subclass of a java class
             //
             if (javaInvokee instanceof InternalJavaProxy &&
                     // don't bother to check if final method, it won't
                     // be there (not generated, can't be!)
                     !Modifier.isFinal(method.getModifiers())) {
                 JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
                         .___getProxyClass();
                 JavaProxyMethod jpm;
                 if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null &&
                         jpm.hasSuperImplementation()) {
                     return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arguments);
                 }
             }
         }
         return invokeWithExceptionHandling(method, javaInvokee, arguments);
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke_static(IRubyObject[] args) {
         checkArity(args.length);
         Object[] arguments = new Object[args.length];
         System.arraycopy(args, 0, arguments, 0, arguments.length);
         convertArguments(args, arguments, 0);
         return invokeWithExceptionHandling(method, null, arguments);
     }
 
     @JRubyMethod
     public IRubyObject return_type() {
         Class<?> klass = method.getReturnType();
         
         if (klass.equals(void.class)) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), klass);
     }
 
     @JRubyMethod
     public IRubyObject type_parameters() {
         return Java.getInstance(getRuntime(), method.getTypeParameters());
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object[] args) {
         checkArity(args.length);
         checkInstanceof(javaInvokee);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, args);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, args);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(0);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(1);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(2);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(3);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1, arg2);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2);
     }
 
     public IRubyObject invokeDirect(Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         assert method.getDeclaringClass().isInstance(javaInvokee);
 
         checkArity(4);
 
         if (mightBeProxy(javaInvokee)) {
             return tryProxyInvocation(javaInvokee, arg0, arg1, arg2, arg3);
         }
 
         return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2, arg3);
     }
 
     public IRubyObject invokeStaticDirect(Object[] args) {
         checkArity(args.length);
         return invokeDirectWithExceptionHandling(method, null, args);
     }
 
     public IRubyObject invokeStaticDirect() {
         checkArity(0);
         return invokeDirectWithExceptionHandling(method, null);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0) {
         checkArity(1);
         return invokeDirectWithExceptionHandling(method, null, arg0);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1) {
         checkArity(2);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1, Object arg2) {
         checkArity(3);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1, arg2);
     }
 
     public IRubyObject invokeStaticDirect(Object arg0, Object arg1, Object arg2, Object arg3) {
         checkArity(4);
         return invokeDirectWithExceptionHandling(method, null, arg0, arg1, arg2, arg3);
     }
 
     private void checkInstanceof(Object javaInvokee) throws RaiseException {
         if (!method.getDeclaringClass().isInstance(javaInvokee)) {
             throw getRuntime().newTypeError("invokee not instance of method's class (" + "got" + javaInvokee.getClass().getName() + " wanted " + method.getDeclaringClass().getName() + ")");
         }
     }
 
     private IRubyObject invokeWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return returnConverter.convert(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectSuperWithExceptionHandling(Method method, Object javaInvokee, Object... arguments) {
         // super calls from proxies must use reflected method
         // FIXME: possible to make handles do the superclass call?
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arguments);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee) {
         try {
             Object result = method.invoke(javaInvokee);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0) {
         try {
             Object result = method.invoke(javaInvokee, arg0);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1) {
         try {
             Object result = method.invoke(javaInvokee, arg0, arg1);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         try {
             Object result = method.invoke(javaInvokee, arg0, arg1, arg2);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1, arg2);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject invokeDirectWithExceptionHandling(Method method, Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         try {
             Object result = method.invoke(javaInvokee, arg0, arg1, arg2, arg3);
             return convertReturn(result);
         } catch (IllegalArgumentException iae) {
             return handlelIllegalArgumentEx(method, iae, arg0, arg1, arg2, arg3);
         } catch (IllegalAccessException iae) {
             return handleIllegalAccessEx(method, iae);
         } catch (InvocationTargetException ite) {
             return handleInvocationTargetEx(ite, method);
         } catch (Throwable t) {
             return handleThrowable(t, method);
         }
     }
 
     private IRubyObject convertReturn(Object result) {
         if (result != null && result.getClass() != boxedReturnType) {
             // actual type does not exactly match method return type, re-get converter
             // FIXME: when the only autoconversions are primitives, this won't be needed
             return JavaUtil.convertJavaToUsableRubyObject(getRuntime(), result);
         }
         return JavaUtil.convertJavaToUsableRubyObjectWithConverter(getRuntime(), result, returnConverter);
     }
 
     private IRubyObject handleIllegalAccessEx(Method method, IllegalAccessException iae) throws RaiseException {
         throw getRuntime().newTypeError("illegal access on '" + method.getName() + "': " + iae.getMessage());
     }
 
     private IRubyObject handlelIllegalArgumentEx(Method method, IllegalArgumentException iae, Object... arguments) throws RaiseException {
         throw getRuntime().newTypeError(
                 "for method " +
                 method.getDeclaringClass().getSimpleName() +
                 "." +
                 method.getName() +
                 " expected " +
                 argument_types().inspect() +
                 "; got: " +
                 dumpArgTypes(arguments) +
                 "; error: " +
                 iae.getMessage());
     }
 
     private void convertArguments(IRubyObject[] argsIn, Object[] argsOut, int from) {
         Class<?>[] types = parameterTypes;
         for (int i = argsOut.length; --i >= 0; ) {
             argsOut[i] = argsIn[i+from].toJava(types[i]);
         }
     }
 
     public Class<?>[] getParameterTypes() {
         return parameterTypes;
     }
 
     public Class<?>[] getExceptionTypes() {
         return method.getExceptionTypes();
     }
 
     public Type[] getGenericParameterTypes() {
         return method.getGenericParameterTypes();
     }
 
     public Type[] getGenericExceptionTypes() {
         return method.getGenericExceptionTypes();
     }
     
     public Annotation[][] getParameterAnnotations() {
         return method.getParameterAnnotations();
     }
 
     public boolean isVarArgs() {
         return method.isVarArgs();
     }
 
     protected String nameOnInspection() {
         return "#<" + getType().toString() + "/" + method.getName() + "(";
     }
 
     @JRubyMethod(name = "static?")
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(isStatic());
     }
     
     public RubyBoolean bridge_p() {
         return getRuntime().newBoolean(method.isBridge());
     }
 
     private boolean isStatic() {
         return Modifier.isStatic(method.getModifiers());
     }
 
     public int getModifiers() {
         return method.getModifiers();
     }
 
     public String toGenericString() {
         return method.toGenericString();
     }
 
-    protected AccessibleObject accessibleObject() {
+    public AccessibleObject accessibleObject() {
         return method;
     }
 
     private boolean mightBeProxy(Object javaInvokee) {
         // this test really means, that this is a ruby-defined subclass of a java class
         return javaInvokee instanceof InternalJavaProxy && !isFinal;
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object... args) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, args);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, args);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1, Object arg2) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1, arg2);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2);
         }
     }
 
     private IRubyObject tryProxyInvocation(Object javaInvokee, Object arg0, Object arg1, Object arg2, Object arg3) {
         JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee).___getProxyClass();
         JavaProxyMethod jpm;
         if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null && jpm.hasSuperImplementation()) {
             return invokeDirectSuperWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arg0, arg1, arg2, arg3);
         } else {
             return invokeDirectWithExceptionHandling(method, javaInvokee, arg0, arg1, arg2, arg3);
         }
     }
 
     public static RaiseException newMethodNotFoundError(Ruby runtime, Class target, String prettyName, String simpleName) {
         return runtime.newNameError("java method not found: " + target.getName() + "." + prettyName, simpleName);
     }
 
     public static RaiseException newArgSizeMismatchError(Ruby runtime, Class ... argTypes) {
         return runtime.newArgumentError("argument count mismatch for method signature " + CodegenUtils.prettyParams(argTypes));
     }
 }
diff --git a/test/org/jruby/javasupport/test/TestNativeException.java b/test/org/jruby/javasupport/test/TestNativeException.java
index 9bc611e13d..d1fb2c1966 100644
--- a/test/org/jruby/javasupport/test/TestNativeException.java
+++ b/test/org/jruby/javasupport/test/TestNativeException.java
@@ -1,57 +1,57 @@
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
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 package org.jruby.javasupport.test; 
  
 import org.jruby.Ruby;
 import org.jruby.test.TestRubyBase;
  
 public class TestNativeException extends TestRubyBase {
 
     public TestNativeException(String name) {
         super(name);
     }
 
     @Override
     protected void setUp() throws Exception {
         super.setUp();
         runtime = Ruby.newInstance();
     }
 
     public void testCauseIsProxied() throws Exception {
-        String result = eval("require 'java'\n" +
+        String result = eval("$-w = nil; require 'java'\n" +
                 "java_import('java.io.File') { 'JFile' }\n" +
                 "begin\n" +
                 "  JFile.new(nil)\n" +
                 "rescue NativeException => e\n" +
                 "end\n" +
                 "p e.cause.respond_to?(:print_stack_trace)");
         assertEquals("Bug: [ JRUBY-106 ]", "true", result);
     }
 }
