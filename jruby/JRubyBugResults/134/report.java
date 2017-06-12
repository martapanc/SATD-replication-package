File path: src/org/jruby/RubyObject.java
Comment: IXME: Should this be renamed to match its ruby name?
Initial commit id: 7404d16e
Final commit id: 6eb8df5b
   Bugs between [      20]:
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
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
   Bugs after [       1]:
1852918db0 Fix JRUBY-5364: become_java! / RubyClass.reify regression

Start block index: 968
End block index: 973
     * FIXME: Should this be renamed to match its ruby name?
     */
    @JRubyMethod(name = "object_id", name2 = "__id__", module = true)
    public synchronized IRubyObject id() {
        return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
    }