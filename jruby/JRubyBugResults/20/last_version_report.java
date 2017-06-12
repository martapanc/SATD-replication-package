public void load(final Ruby runtime, boolean wrap) {
    RubyClass cFiber = runtime.defineClass("Fiber", runtime.getObject(), new ObjectAllocator() {
        public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
            return new Fiber(runtime, klazz);
        }
    });

    cFiber.defineAnnotatedMethods(Fiber.class);
    cFiber.defineAnnotatedMethods(FiberMeta.class);

    if (runtime.getExecutor() != null) {
        executor = runtime.getExecutor();
    } else {
        executor = Executors.newCachedThreadPool(new DaemonThreadFactory());
    }
}
