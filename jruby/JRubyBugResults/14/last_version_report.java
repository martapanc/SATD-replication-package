public static RubyClass createClass(Ruby runtime, RubyClass baseClass) {
    RubyClass exceptionClass = runtime.defineClass(CLASS_NAME, baseClass, NATIVE_EXCEPTION_ALLOCATOR);

    exceptionClass.defineAnnotatedMethods(NativeException.class);

    return exceptionClass;
}
