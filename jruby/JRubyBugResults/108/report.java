File path: src/org/jruby/runtime/callback/InvocationCallbackFactory.java
Comment: / TODO: this could be further optimized
Initial commit id: 9251dc20
Final commit id: ed0a495c
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 993
End block index: 1039
    public void callMethodMissingIfNecessary(SkinnyMethodAdapter mv, Label afterCall, Label okCall) {
        Label methodMissing = new Label();

        // if undefined, branch to method_missing
        mv.dup();
        mv.invokevirtual(p(DynamicMethod.class), "isUndefined", sig(boolean.class));
        mv.ifne(methodMissing);

        // if we're not attempting to invoke method_missing and method is not visible, branch to method_missing
        mv.aload(DISPATCHER_NAME_INDEX);
        mv.ldc("method_missing");
        // if it's method_missing, just invoke it
        mv.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
        mv.ifne(okCall);
        // check visibility
        mv.dup(); // dup method
        mv.aload(DISPATCHER_THREADCONTEXT_INDEX);
        mv.invokevirtual(p(ThreadContext.class), "getFrameSelf", sig(IRubyObject.class));
        mv.aload(DISPATCHER_CALLTYPE_INDEX);
        mv.invokevirtual(p(DynamicMethod.class), "isCallableFrom", sig(boolean.class, params(IRubyObject.class, CallType.class)));
        mv.ifne(okCall);

        // invoke callMethodMissing
        mv.label(methodMissing);

        mv.aload(DISPATCHER_THREADCONTEXT_INDEX); // tc
        mv.swap(); // under method
        mv.aload(DISPATCHER_SELF_INDEX); // self
        mv.swap(); // under method
        mv.aload(DISPATCHER_NAME_INDEX); // name
        mv.aload(DISPATCHER_ARGS_INDEX); // args

        // caller
        mv.aload(DISPATCHER_THREADCONTEXT_INDEX);
        mv.invokevirtual(p(ThreadContext.class), "getFrameSelf", sig(IRubyObject.class));

        mv.aload(DISPATCHER_CALLTYPE_INDEX); // calltype
        mv.aload(DISPATCHER_BLOCK_INDEX); // block

        // invoke callMethodMissing method directly
        // TODO: this could be further optimized, since some DSLs hit method_missing pretty hard...
        mv.invokestatic(p(RuntimeHelpers.class), "callMethodMissing", sig(IRubyObject.class, 
                params(ThreadContext.class, IRubyObject.class, DynamicMethod.class, String.class, 
                                    IRubyObject[].class, IRubyObject.class, CallType.class, Block.class)));
        // if no exception raised, jump to end to leave result on stack for return
        mv.go_to(afterCall);
    }
