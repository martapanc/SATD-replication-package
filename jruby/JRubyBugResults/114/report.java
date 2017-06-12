File path: src/org/jruby/runtime/callback/InvocationCallbackFactory.java
Comment: / TODO: This is probably BAD...
Initial commit id: 9251dc20
Final commit id: ed0a495c
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 521
End block index: 525
    public Callback getBlockMethod(String method) {
        // TODO: This is probably BAD...
        return new ReflectionCallback(type, method, new Class[] { RubyKernel.IRUBY_OBJECT,
                RubyKernel.IRUBY_OBJECT }, false, true, Arity.fixed(2), false);
    }
