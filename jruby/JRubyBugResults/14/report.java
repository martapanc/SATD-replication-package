File path: src/org/jruby/NativeException.java
Comment: / FIXME: If NativeException is expected to be used from Ruby code
Initial commit id: b390103c
Final commit id: 725be643
   Bugs between [       3]:
725be6439d Fix JRUBY-6103: allocator undefined for NativeException by providing a simple allocator for NativeException.
9aae35a881 fix JRUBY-4923: Bad stacktraces when a ExceptionInInitializerError occurs in a java class
b1aa3d456f Simple fix for NativeException to avoid a continually growing backtrace. JRUBY-940.
   Bugs after [       0]:


Start block index: 49
End block index: 59
    public static RubyClass createClass(IRuby runtime, RubyClass baseClass) {
        // FIXME: If NativeException is expected to be used from Ruby code, it should provide
        // a real allocator to be used. Otherwise Class.new will fail, as will marshalling. JRUBY-415
    	RubyClass exceptionClass = runtime.defineClass(CLASS_NAME, baseClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
    	
		CallbackFactory callbackFactory = runtime.callbackFactory(NativeException.class);
		exceptionClass.defineMethod("cause", 
				callbackFactory.getMethod("cause"));		

		return exceptionClass;
    }
