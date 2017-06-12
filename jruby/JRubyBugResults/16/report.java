File path: src/org/jruby/IncludedModuleWrapper.java
Comment: / FIXME: legal here? may want UnsupportedOperationException
Initial commit id: 95c346df
Final commit id: 6f159fe5
   Bugs between [       2]:
8dc882706f Fix for JRUBY-3661: Incorrect super call site caching with multiple included modules
5ddeaaa8a8 Fix related to JRUBY-2181: Change RubyModule#addMethod to put/replace methods atomically, since searchMethod does not synchronize on methods. (Should also be a tiny perf improvement.) Also includes some minor cleanup of signatures to support generics and prevent adding methods to IncludedModuleWrapper.
   Bugs after [       0]:


Start block index: 250
End block index: 253
    protected IRubyObject constantTableStore(String name, IRubyObject value) {
        // FIXME: legal here? may want UnsupportedOperationException
        return delegate.constantTableStore(name, value);
    }
