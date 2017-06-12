File path: src/org/jruby/RubyObject.java
Comment: IXME: finalizer should be dupped here
Initial commit id: fa021e63
Final commit id: fd0fa789
   Bugs between [      36]:
ef99d39db4 Fix for JRUBY-5137: [1.9] Object#hash crash when a Bignum given as a hash value
b845a7bec8 Fix ruby_test SortedSet test that does not comply with Ruby bug #118
9a20ef3186 fixes JRUBY-4016: [1.9] #equal? is incorrect and behaves more like ==
dfe4d41054 Fix JRUBY-4174, make it possible to instantiate BasicObject, add BasicObject#initialize
3f6a0c9ecc Fix JRUBY-3870: Object#singleton_methods(true) doesn't include inherited methods
c871551a49 Fix for JRUBY-3495: Array#uniq does not work with custom objects that define eql? and hash. (fix 1.8 protocol for non fixnum hash codes, also implement1.9 one).
04f9566543 Fix for JRUBY-3490: Object#instance_exec only works with blocks of 3 parameters or less?!
a85971ee14 fix for JRUBY-3386: Array#eql? rubyspec failure.
2b4606df16 Fixes for JRUBY-3362: attr_accessor, attr_reader, attr_writer, and attr not obeying visibility
d97a779fb2 Fix for JRUBY-3324: Error in string encoding conversion
bbb8441c8f Fix for JRUBY-2975: Overriding Time._dump does not behave the same as MRI
37a47a6a9f Fix for JRUBY-2327: Super in module causes NPE.
dbce9b3978 Fix for JRUBY-1526 and JRUBY-1545 based on Bill's suggestion.
b9aab6013b Fix for JRUBY-1936: Object#instance_of? should not accept non-Class or non-Module arguments (patch by Vladimir).
3cf3d8f597 Fix for JRUBY-1870, Object#extend should extend from last to first.
6c1313d5d4 Fix for JRUBY-1824
0e839f4640 Fix for JRUBY-1814.
17597b3f69 Fix for JRUBY-1813.
0030191572 Fix for JRUBY-1269, finalizers should be passed the object being collected
f16e305fcf Not-so-pretty fix for JRUBY-1381: define_method with an instance_eval'ed block from the surrounding scope doesn't get the right self.
875bdbab09 Fix for JRUBY-1210: Array#join behaves inconsistently on recursive arrays. This required some fixes to the inspection mechanism allowing the join to be efficient (e.g: not looking up a ThreadLoacal on ["foo","bar"].join)
e26fda8a1c Fix for JRUBY-1074, allow Rubinius redo_spec to run
d055a0e461 Fixes for JRUBY-1094: Kernel#Integer should raise a TypeError when passed an argument that doesn't implement to_i or to_int
8970d7e87d Fix for JRUBY-1082 (Files should flush on close), by Damian Steer.
85afceb185 Dysinger's fixes for JRUBY-980, add true/false versions of public_methods, private_methods, protected_methods.
72d615dee4 Smaller fix for JRUBY-942, by Koichiro. Test is pending.
f057b63deb Fix for JRUBY-787, and introduced more correct LocalJumpError class.
e421186fd8 Fixes and tests for JRUBY-807, correct eql?/== behavior for hashes.
a5bd2796ce Fix JRUBY-815; error messages from type conversions are mangled
b4939d6102 Another rework of finalization, much simpler now. Also fixes JRUBY-799, so finalizers will run whether ObjectSpace is enabled or not.
07db95c0e6 Partial fix for JRUBY-766, allow returns in a simple eval to propagate correctly. Still more to do.
0abef1e2f8 More coercion fixes, these solve JRUBY-740 and a few other issues I found. convertType* methods still need some cleanup though.
1c02ca0e64 Fixes for JRUBY-734, along with a first run at cleaning up IRubyObject.
22ebfe6ece Fix for JRUBY-589, Object#id should emit deprecation warning
7380355037 Fix for JRUBY-587. MRI YAML doesn't recognize floats with underscores in them, but we do, since that's in the standard. So I've removed that compliant behavior to be more like MRI. Also, the groundwork is in to have faster versions of some Array methods
6fac9238fc Fix for JRUBY-593, and partial fix for JRUBY-587
   Bugs after [       2]:
1852918db0 Fix JRUBY-5364: become_java! / RubyClass.reify regression
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException

Start block index: 318
End block index: 325
    public void initCopy(IRubyObject original) {
        assert original != null;
        assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";

        setInstanceVariables(new HashMap(original.getInstanceVariables()));
        /* FIXME: finalizer should be dupped here */
        callMethod(getRuntime().getCurrentContext(), "initialize_copy", original);
    }
