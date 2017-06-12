File path: src/org/jruby/RubyModule.java
Comment: IXME: any good reason to have two identical methods? (same as remove_class_variable)
Initial commit id: 95c346df
Final commit id: 632441f3
   Bugs between [      30]:
42d8f82cdc Fix for JRUBY-4812: RubySpec: Module#remove_class_variable failures
055dd156a5 Fix for JRUBY-5168: Can't include interfaces in a synchronized class
0c1914058d Fix for JRUBY-5148: Module reopen doesn't work for included module
911d18d15f Really fix JRUBY-4977.
2b32739876 Fix JRUBY-4977: Class#to_s doesn't match MRI (01x... versus 0x...)
3c271f5d8e fixes JRUBY-4815: RubyClass doesn't include the base name in the class name after setting a constant with const_set
6cfde52336 Fix for JRUBY-4773: Lock contention in Ruby.allocModuleId(RubyModule)
9414a764ec Better fix for JRUBY-2186 and improvement to prevent things like JRUBY-4693.
067a95aa14 Fix for JRUBY-4214: module_function fails on some private methods
289e84870a fixes JRUBY-4197: [1.9] Module hierarchy issues in const_defined? method
3518b844ef Fixes for JRUBY-3717 and JRUBY-3715
854953308a Fix for JRUBY-3647: Severe performance degradation from 3d9140fafcda9c4fe6b9d5a1fec0ae9822877e03
3d9140fafc Fix for JRUBY-3551 and related failure potential in included module hierarchies. I have a test in process.
ed03b981de Fix for fix for JRUBY-2435: Aliasing eval and other "special" methods should display a warning
2281338745 Fix for JRUBY-2435: Aliasing eval and other "special" methods should display a warning
afa6cd5878 Fix for JRUBY-3398: Attributes defined from Java tried to use current frame for visibilityattr definition from Java. This was causing java_class attr to be private in some cases, including after my upcoming defined? constant scoping fix.
2fe04b7b7f Fix for the fix for JRUBY-3375. Thanks again :)
2b4606df16 Fixes for JRUBY-3362: attr_accessor, attr_reader, attr_writer, and attr not obeying visibility
28a459f069 Fix for JRUBY-3308: Deadlock between RubyModule.includeModule() and RubyClass.invalidateCacheDescendants()
8d1eef47be Fixes for JRUBY-2744, reenable c-call and c-return tracing for native methods.
1adf8d5b4a Fix for JRUBY-2469 - make sure that the meta-attribute always defines the method on the singleton class.
15ea91cf1c Fixes for JRUBY-2402, and improvements to bring the total build time back to where it was before recent annotation work.
dd5674482c Fix for JRUBY-2330. Make sure that rest arguments in define_method blocks with zsuper is handled correctly.
792a75ebc6 Optimized fix for JRUBY-2277: if supplied newId was RubySymbol, no need to do lookup.
8d3fa45fe8 Fix for JRUBY-2277: fix alias_method to a) pass coerced name to (singleton_)method_added; b) call correct method_added method.
5ddeaaa8a8 Fix related to JRUBY-2181: Change RubyModule#addMethod to put/replace methods atomically, since searchMethod does not synchronize on methods. (Should also be a tiny perf improvement.) Also includes some minor cleanup of signatures to support generics and prevent adding methods to IncludedModuleWrapper.
a78db1a702 Fix for JRUBY-1641 from Vladimir; safely check privileged system properties, since blowing up there kills JRuby.
f7bf2e0b56 Almost certainly a fix for JRUBY-1674, some other related issues, and some assertions to make sure it doesn't happen again.
47fc3bf1eb Fix for JRUBY-1468, fixes the original issue, adds a whole raft of tests, and fixes a few additional issues discovered.
d419a52688 Fix for JRUBY-1419, call method_added in alias_method.
   Bugs after [      16]:
bcc26747f5 Use RubyModule.JavaClassKindOf fixes jruby/jruby#614
6480caa597 Fix #497 by using nearest class's name for anon inspect prefix.
7152c1834a Passing no args to Module#undef_method and Module#remove_method should be noop. Fixes #327.
0931687127 Fix JRUBY-6885
bef3b7040c Fix JRUBY-6865
22cd6f9ac7 Fixes JRUBY-6658
cc6e09f4b3 Fix JRUBY-6753
eee38a6d48 Fix JRUBY-6238
404c280fc7 Fix JRUBY-6224
c6e2447cdb Fix JRUBY-6201: File reading performance regression
2c6781ef4e Fix JRUBY-928: Java arrays don't inherit from java.lang.Object in Rubified Java class hierarchy
f636731047 Fix JRUBY-5729: Process.respond_to?(:fork) must return false
1a1864332f Fix JRUBY-5674: Cannot override Fixnum operators
209a77e3f2 Re-fix JRUBY-5624: Class.new { p eval("self", binding) }
86ce4a82af Fix JRUBY-5376: singleton_class.define_method creates private methods
94913c828e fix JRUBY-5304: [1.9] Module.const_defined? and Module.const_get accept a second parameter to look up into the ancestors

Start block index: 2011
End block index: 2026
     * FIXME: any good reason to have two identical methods? (same as remove_class_variable)
     */
    public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
        String internedName = validateClassVariable(name.asSymbol());
        IRubyObject value;

        if ((value = deleteClassVariable(internedName)) != null) {
            return value;
        }

        if (fastIsClassVarDefined(internedName)) {
            throw cannotRemoveError(internedName);
        }

        throw getRuntime().newNameError("class variable " + internedName + " not defined for " + getName(), internedName);
    }
