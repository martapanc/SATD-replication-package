private JavaClass(Ruby runtime, Class<?> javaClass) {
    super(runtime, (RubyClass) runtime.getJavaSupport().getJavaClassClass(), javaClass);
    if (javaClass.isInterface()) {
        initializer = new InterfaceInitializer(javaClass);
    } else if (!(javaClass.isArray() || javaClass.isPrimitive())) {
        initializer = new ClassInitializer(javaClass);
    } else {
        initializer = DUMMY_INITIALIZER;
    }
}
