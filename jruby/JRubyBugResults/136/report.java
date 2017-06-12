File path: src/org/jruby/RubyArray.java
Comment: FIXME: Whis is this named "push_m"?
Initial commit id: e1a1b474
Final commit id: 719bc20a
   Bugs between [      16]:
56eeae1a85 Fix for JRUBY-4515: RubyArray doesn't have toJava method which makes JavaEmbedUtils.rubyToJava fail
0505fb1fc7 [1.9] Fix JRUBY-4508, add Array#rotate and Array#rotate!
aeef3e6d1d fixes JRUBY-4181: [1.8.7] Failures on 1.8.7 HEAD test/ruby/test_array. Failure 4.
ac54677333 fixes JRUBY-4175: RubySpec: Array#<=> returns nil when the argument is not array-like
4d034fafe1 Fix for JRUBY-4157: fannkuch and chameneosredux benchmarks timing out on The Benchmarks Game
6266374bc8 Fix for JRUBY-4053: ActiveRecord AssociationCollection#== method is returning false on equal results
7b201461f1 Fixes for JRUBY-3816: Objects returned from Java library call not fully unwrapped
2b6aedfc5d Fix for JRUBY-3878: String representation of arrays and symbols does not match ruby 1.9
397ae2d50e Fix JRUBY-3148
c6aebe391c Fix JRUBY-3612
219e0308de Fix for JRUBY-3387: Array#== rubyspec failure.
b3332e8a4b Fix for JRUBY-3251: ConcurrencyError bug when installing Merb on JRuby. Test pending.
04ce842cc4 Fixes for JRUBY-2883: Many Array methods don't handle recursive arrays properly, JRUBY-2878: Array#hash with recursive array crashes JRuby. Affected methods also match 1.9 behavior.
993f8c99ef A bunch of findbugs fixes from JRUBY-1173. Also renamed generated callbacks to be Callback in their name instead of Invoker, to distinguish from actual invokers.
a2854314c1 Fix for JRUBY-2065: Array#assoc and Array#rassoc are not COW aware.
bad1f67887 Fixes for JRUBY-1409, only alias the methods that MRI does; rebind others.
   Bugs after [      17]:
bcc26747f5 Use RubyModule.JavaClassKindOf fixes jruby/jruby#614
812f24cc2d Array #take and #drop are the same between 1.8 and 1.9. Fixes JRUBY-7151
6203f30b52 Fix #529.
39cbc8292e Fix JRUBY-6776
ed738357bd Fix JRUBY-6706
b48cfe5a52 Fix JRUBY-6497
8f2ba4214e Fix JRUBY-6456
d3b828d6df Fix JRUBY-6382
cf9b95dbad Fix JRUBY-6359
20c3217aae Fix JRUBY-5643: [1.9] Array#map and Array#collect produce the same enumerator; should be specific to the method called
c8f186cbda minor fix for reported bug in JRUBY-6264. Perhaps bigger patch will be suggested.
5512c59a73 Encoding fixes for JRUBY-6033:
fbc85d9466 Fix JRUBY-5883: Hash Subclass#== not respected when checking equality of collections.
d205a0c0c8 Fix JRUBY-5239: ArrayIndexOutOfBoundsException in threadsafe mode
4a3c6fe08b Second attempt to fix JRUBY-5275. Even smaller than the last commit.
d649a499eb Fix JRUBY-5275: each_slice enumerator methods fail
105217d348 RubyArray now obeys List.remove contract by removing only one element matching Object. Fix JRUBY-4661

Start block index: 876
End block index: 866

/** rb_ary_push_m
 * FIXME: Whis is this named "push_m"?
 */
@JRubyMethod(name = "push", required = 1, rest = true)
public RubyArray push_m(IRubyObject[] items) {
    for (int i = 0; i < items.length; i++) {
        append(items[i]);
    }

    return this;
}