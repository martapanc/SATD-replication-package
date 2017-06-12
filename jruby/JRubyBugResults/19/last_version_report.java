    public static CallbackFactory createFactory(Ruby runtime, Class type, ClassLoader classLoader) {
        if (reflection) {
            return new ReflectionCallbackFactory(type);
        } else {
            return new InvocationCallbackFactory(runtime, type, classLoader);
        }
    }
