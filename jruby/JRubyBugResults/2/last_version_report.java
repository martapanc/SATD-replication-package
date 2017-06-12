    public IRubyObject invoke(IRubyObject[] args) {
        checkArity(args.length - 1);
        Object[] arguments = new Object[args.length - 1];
        convertArguments(getRuntime(), arguments, args, 1);

        IRubyObject invokee = args[0];
        if(invokee.isNil()) {
            return invokeWithExceptionHandling(method, null, arguments);
        }

        Object javaInvokee = JavaUtil.unwrapJavaObject(getRuntime(), invokee, "invokee not a java object").getValue();

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
