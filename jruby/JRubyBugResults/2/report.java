File path: src/org/jruby/javasupport/JavaMethod.java
Comment: / don't bother to check if final method
Initial commit id: 52343aa0
Final commit id: fe8302dc
   Bugs between [       2]:
d493ef889d Fix JRUBY-1976, make Java Fields coerce things that aren't JavaObjects correctly, such as JavaConstructor, JavaMethod and JavaField.
c311eb0020 Fix for JRUBY-2843, fix basicsocket#close_read
   Bugs after [       6]:
f38b8e5a35 Kinda sorta fix JRUBY-6674
545da6c46c Fix JRUBY-6619: NoMethodError 'upcase' on Java string
6fdc42dcca Partial fix for JRUBY-4295. This fix just adds @JRubyMethod annotation.
ec8d280eb6 Fix for JRUBY-4799: Uncaught AccessibleObject.setAccessible fails on App Engine
7a8e661135 Fixes for JRUBY-4732: Clean up anything that calls dataGetStruct on a wrapped Java object to get the object
7458b0531a fixes JRUBY-4599: Invoking private/protected java no-args method with null argument leads to NPE

Start block index: 184
End block index: 219
    public IRubyObject invoke(IRubyObject[] args) {
        if (args.length != 1 + getArity()) {
            throw getRuntime().newArgumentError(args.length, 1 + getArity());
        }

        IRubyObject invokee = args[0];
        if (! (invokee instanceof JavaObject)) {
            throw getRuntime().newTypeError("invokee not a java object");
        }
        Object javaInvokee = ((JavaObject) invokee).getValue();
        Object[] arguments = new Object[args.length - 1];
        convertArguments(getRuntime(), arguments, args, 1);

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
        return invokeWithExceptionHandling(method, javaInvokee, arguments);
    }
