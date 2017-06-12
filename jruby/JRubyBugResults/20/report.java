File path: src/org/jruby/libraries/FiberLibrary.java
Comment: / FIXME: Not sure what the semantics of transfer are
Initial commit id: c187d01e
Final commit id: c2f126a4
   Bugs between [       2]:
bf56a4c459 Fix for JRUBY-3615: Fiber.yield crahes if called without arguments
d5edd038a6 Fix for JRUBY-1812, load should accept a "wrap" parameter that causes it to load in a new top self.
   Bugs after [       0]:


Start block index: 97
End block index: 107
        public static void setup(Ruby runtime) {
            RubyClass cFiber = runtime.defineClass("Fiber", runtime.getClass("Object"), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
            CallbackFactory cb = runtime.callbackFactory(Fiber.class);
            cFiber.getMetaClass().defineMethod("new", cb.getOptSingletonMethod("newInstance"));
            cFiber.defineFastMethod("resume", cb.getFastOptMethod("resume"));
            // FIXME: Not sure what the semantics of transfer are
            //cFiber.defineFastMethod("transfer", cb.getFastOptMethod("transfer"));
            cFiber.defineFastMethod("alive?", cb.getFastMethod("alive_p"));
            cFiber.getMetaClass().defineFastMethod("yield", cb.getFastSingletonMethod("yield", IRubyObject.class));
            cFiber.getMetaClass().defineFastMethod("current", cb.getFastSingletonMethod("current"));
        }
