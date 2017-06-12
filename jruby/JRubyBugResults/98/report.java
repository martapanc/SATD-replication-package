File path: src/org/jruby/RubyStruct.java
Comment: / TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here
Initial commit id: b390103c
Final commit id: 87404f0e
   Bugs between [      11]:
87404f0e15 Fix JRUBY-6647
b9eefb9653 Fix JRUBY-5875: extending Struct and override initialize would make Marshal fail
c8522e573b Fix for JRUBY-2490, move allocation-related struct code into struct constructor.
f7bf2e0b56 Almost certainly a fix for JRUBY-1674, some other related issues, and some assertions to make sure it doesn't happen again.
368f8bf103 Fix for JRUBY-1598
95108e35e6 Fixes for JRUBY-1185, handle top-level visibility appropriately and use public visibility for module, class, and struct blocks.
a51846feee Fixes for JRUBY-1179, allow Struct.new(nil, ...) to create anonymous structs.
eb05ebdeb3 JRUBY-858: Eval within instance_eval seems to have the wrong self. JRUBY-866: struct and its use of metaclasses is not quite right (for inspect and ==) Previous two issues fix last test case in JRUBY-531
e421186fd8 Fixes and tests for JRUBY-807, correct eql?/== behavior for hashes.
6805fad9d5 Fix the remaining marshalling issues (for JRUBY-531). Two different failures about singleton classes, first one because we didn't properly generate a singleton-name for cases where the enclosing class/module was singleton. The second problem is that ENV should really be a singleton, and can't be dumped. The real fix for ENV is to generate it the same way as ARGF, but that means custom implementation of a whole lot of hash-methods, which doesn't seem to tempting right now. The quick fix is to make sure ENV is actually a singleton object anyway, done by adding the to_s method as a singleton method to it.
8c55ee5201 Fix for JRUBY-463. Make Struct.new on an existing struct name produce a warning, but also a new struct.
   Bugs after [       1]:
6ecfcee3af Mostly fix JRUBY-6760: ArgumentError output

Start block index: 60
End block index: 88
    public static RubyClass createStructClass(IRuby runtime) {
        // TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here, but it's unclear how Structs
        // work with marshalling. Confirm behavior and ensure we're doing this correctly. JRUBY-415
        RubyClass structClass = runtime.defineClass("Struct", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
        CallbackFactory callbackFactory = runtime.callbackFactory(RubyStruct.class);
        structClass.includeModule(runtime.getModule("Enumerable"));

        structClass.defineSingletonMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));

        structClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
        structClass.defineMethod("clone", callbackFactory.getMethod("rbClone"));

        structClass.defineFastMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));

        structClass.defineFastMethod("to_s", callbackFactory.getMethod("to_s"));
        structClass.defineFastMethod("inspect", callbackFactory.getMethod("inspect"));
        structClass.defineFastMethod("to_a", callbackFactory.getMethod("to_a"));
        structClass.defineFastMethod("values", callbackFactory.getMethod("to_a"));
        structClass.defineFastMethod("size", callbackFactory.getMethod("size"));
        structClass.defineFastMethod("length", callbackFactory.getMethod("size"));

        structClass.defineMethod("each", callbackFactory.getMethod("each"));
        structClass.defineFastMethod("[]", callbackFactory.getMethod("aref", IRubyObject.class));
        structClass.defineFastMethod("[]=", callbackFactory.getMethod("aset", IRubyObject.class, IRubyObject.class));

        structClass.defineFastMethod("members", callbackFactory.getMethod("members"));

        return structClass;
    }
