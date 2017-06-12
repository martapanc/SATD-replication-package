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
