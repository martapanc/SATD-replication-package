public static RubyClass createStructClass(Ruby runtime) {
    RubyClass structClass = runtime.defineClass("Struct", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
    runtime.setStructClass(structClass);
    structClass.index = ClassIndex.STRUCT;
    structClass.includeModule(runtime.getEnumerable());
    structClass.defineAnnotatedMethods(RubyStruct.class);

    return structClass;
}
