File path: src/org/jruby/RubyObject.java
Comment: / FIXME: If true array is common enough we should pre-allocate and stick somewhere
Initial commit id: 8868926c
Final commit id: 6eb8df5b
   Bugs between [       6]:
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
ef99d39db4 Fix for JRUBY-5137: [1.9] Object#hash crash when a Bignum given as a hash value
b845a7bec8 Fix ruby_test SortedSet test that does not comply with Ruby bug #118
9a20ef3186 fixes JRUBY-4016: [1.9] #equal? is incorrect and behaves more like ==
dfe4d41054 Fix JRUBY-4174, make it possible to instantiate BasicObject, add BasicObject#initialize
3f6a0c9ecc Fix JRUBY-3870: Object#singleton_methods(true) doesn't include inherited methods
   Bugs after [       1]:
1852918db0 Fix JRUBY-5364: become_java! / RubyClass.reify regression

Start block index: 1100
End block index: 1103
    // FIXME: If true array is common enough we should pre-allocate and stick somewhere
    private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
        return args.length == 0 ? new IRubyObject[] { context.getRuntime().getTrue() } : args;
    }
