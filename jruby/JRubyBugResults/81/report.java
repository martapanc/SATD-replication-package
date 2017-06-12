File path: src/org/jruby/javasupport/JavaMethod.java
Comment: / TODO: choose narrowest method by continuing to search
Initial commit id: 05c37a91
Final commit id: c4e6f18a
   Bugs between [       0]:

   Bugs after [       8]:
f38b8e5a35 Kinda sorta fix JRUBY-6674
545da6c46c Fix JRUBY-6619: NoMethodError 'upcase' on Java string
6fdc42dcca Partial fix for JRUBY-4295. This fix just adds @JRubyMethod annotation.
ec8d280eb6 Fix for JRUBY-4799: Uncaught AccessibleObject.setAccessible fails on App Engine
7a8e661135 Fixes for JRUBY-4732: Clean up anything that calls dataGetStruct on a wrapped Java object to get the object
7458b0531a fixes JRUBY-4599: Invoking private/protected java no-args method with null argument leads to NPE
d493ef889d Fix JRUBY-1976, make Java Fields coerce things that aren't JavaObjects correctly, such as JavaConstructor, JavaMethod and JavaField.
c311eb0020 Fix for JRUBY-2843, fix basicsocket#close_read

Start block index: 127
End block index: 152
    public static JavaMethod createDeclaredSmart(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
        try {
            Method method = javaClass.getDeclaredMethod(methodName, argumentTypes);
            return create(runtime, method);
        } catch (NoSuchMethodException e) {
            // search through all declared methods to find a closest match
            MethodSearch: for (Method method : javaClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    Class<?>[] targetTypes = method.getParameterTypes();
                    TypeScan: for (int i = 0; i < argumentTypes.length; i++) {
                        if (i >= targetTypes.length) continue MethodSearch;
                        
                        if (targetTypes[i].isAssignableFrom(argumentTypes[i])) {
                            continue TypeScan;
                        }
                    }
                    
                    // if we get here, we found a matching method, use it
                    // TODO: choose narrowest method by continuing to search
                    return create(runtime, method);
                }
            }
            throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                    methodName);
        }
    }
