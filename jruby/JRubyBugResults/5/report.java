File path: src/org/jruby/Ruby.java
Comment: / FIXME moved this here to get what's obviously a utility method out of IRubyObject
Initial commit id: 0aa32bd5
Final commit id: 47fd4acb
   Bugs between [      69]:
846be19993 Fix JRUBY-6766
6ecfcee3af Mostly fix JRUBY-6760: ArgumentError output
7ca1995a33 Fix #162
ac0aff594c Fix JRUBY-6631: Time#nsec always returns 0
f4803435ea Fix recursiveKey usage across multiple threads (JRUBY-6621)
ce2282e2f6 Fix JRUBY-6527
030284a28c Remaining fixes for JRUBY-6523.
cb777c23ff Partial fix for JRUBY-6442
8f2ba4214e Fix JRUBY-6456
a75ddbea79 Additional fixes for JRUBY-6434
ff481bf0f4 Fix JRUBY-6434
2a2b0717ce Fix JRUBY-6214: Dir#rmdir raises improper exception if directory is not empty.
51bf9d9f64 Fix JRUBY-4717: implement new digest methods.
36dd3a0424 Fix JRUBY-4339: Kernel.load with wrap=true does not protect the global namespace of calling program
1a1864332f Fix JRUBY-5674: Cannot override Fixnum operators
957e62d5a7 Fix JRUBY-5694: DATA points to wrong file
ff1ec4932a Fixes and support code for JRUBY-5596: Improve Dalli IO performance
43422df208 Fix JRUBY-5583: Profiling should not bomb out trying to find method name
15832d3b4d Fix JRUBY-5514: Errno::EBADF is sometimes raised instead of IOError when TCPSocket#readline is called after TCPSocket#close
24af14859c Fix JRUBY-4433: [windows] Writing to a pipe with closed source raises wrong exception
7c7ecc5cdb Fix JRUBY-5463: Process.getpriority should raise an error with an invalid process type
527be7924f Fix JRUBY-5471: private method `verify_mode=' called with Bundler, net/https, jruby-openssl and RubyGems 1.5
23a1f3d2c1 Fix for JRUBY-5153: File system access not checked by JVM security manager
5f8bfc2ce9 Fix JRUBY-5346: jruby 1.6.0.RC1 doesn't recognize multibyte strings in 1.9 branch
6c1d41aedf Partial fix for JRUBY-5352: Eliminate yecht.jar entirely, so that YAML can be loaded without nested jars
04ce8b7d9d Fix JRUBY-5356: --profile.graph sometimes shows wrong methods
406556bd3b Fix JRUBY-5417: Nil backtrace when overriding method_missing and calling super
f0c217b6f0 Fix JRUBY-5326: [1.9] Java error in Psych when running "gem build" command
20ff6278cc Fix JRUBY-5322: NPE forcing to compile a script
01e4d770ef Fix JRUBY-5261: RSpec >= 2.2 around hooks trigger runtime NPE
62ecad3bed Fix JRUBY-5221: jruby.reify.classes raising error with optparse
620caccb15 Fix for JRUBY-5114: Frequent internal server errors resulting from AJAX calls -- GlassFish v3 1.0.2 with JRuby 1.5.2 and newer
3f995f7371 Fix for JRUBY-4966: TCPSocket raises Errno::ECONNREFUSED instead of Errno::EHOSTUNREACH
740dcd32bd Fix JRUBY-4915: BasicSocket.do_not_reverse_lookup should default to true in 1.9.2
42b0fa9bc0 Fix JRUBY-4468: Process.kill doesn't return a correct value
71aa55d7c0 fixes JRUBY-4859: File.delete doesn't throw an error when deleting a directory
6cfde52336 Fix for JRUBY-4773: Lock contention in Ruby.allocModuleId(RubyModule)
d872d1e661 Fix for JRUBY-4747: read_nonblock error with couchrest
1e4305db26 Fix for remaining issues with JRUBY-4695: local variable has value nil when compile mode is JIT or FORCE
8234945a53 Partially fix JRUBY-4695, sharing variables when JIT/FORCE is set. Currently, sharing lvars works on JIT but not on FORCE while sharing ivars works on both JIT/FORCE.
be6f6c0e5b Fix for JRUBY-4643: it is correct that M::X does not look for X in Object
9414a764ec Better fix for JRUBY-2186 and improvement to prevent things like JRUBY-4693.
93b1257dad Revert "Fix for JRUBY-4484: jruby -rtracer doesn't trace"
726da0a3c2 Fix JRUBY-4653: [1.9] Math API changes
b1966c5695 Fix JRUBY-4640. This change adds Ruby#tearDown(boolean) method for embedding API to give a chance to catch exceptions.
26323269fc Fixes for JRUBY-4553: Scalability: JRuby suffers from Java Charset lock contention
8e51e6e32c Fixes for JRUBY-2282 and JRUBY-2475.
733fb41fc7 Fix for JRUBY-4563: jruby retains handle on files
3392006696 Fix for JRUBY-4539: Error is shown when script length is more than 64K - should be just warning
5f3de42461 Fix for JRUBY-4484: jruby -rtracer doesn't trace
f5cc09f381 Fix JRUBY-3369, contributed by Lars Westergren <lars.westergren@gmail.com>
c852b972ca Partial fix for JRUBY-4456: [1.9] Random class is not defined
3487bba2fa Fixed JRUBY-4116 and JRUBY-3950 (IRB in Applet and WebStart environments).
857cfdc345 relocate generator and prelude from shared to builtin. This fixes JRUBY-4168
a6c9a1e3ce fixes JRUBY-4091: IConv.conv ignores //IGNORE flag into the encoding parameter
48cf3fb6a5 Fix for JRUBY-3956: Can't do Enumerator#next in --1.9? Not implemented yet?
854953308a Fix for JRUBY-3647: Severe performance degradation from 3d9140fafcda9c4fe6b9d5a1fec0ae9822877e03
02a5c74cea Fix for JRUBY-3633: JRuby 1.3 Fails to Execute Embedded Ruby code in Java
c9dfe522d4 Fix for JRUBY-3449: jruby-complete-1.2RC1.jar seems to break FFI
60f3d6628f Fix for JRUBY-1079: IO.sysopen not defined
99f983249e Rejigger how binding works with files and line numbers. It appears that we weren't tracking everything we needed in a binding. Instead of holding the current frame plus the current file+line, we held only the current frame. This led to us constantly setting and clearing the frame's line numbers in an attempt to get traces to line up. With binding carrying the original file+line, that juggling is no longer necessary. This fixes an eval spec and should improve backtraces across eval calls, which had numerous peculiarities before. This also fixes JRUBY-2328: Overriding require causes eval to give wrong __FILE__ in certain circumstances.
082f232db7 Finally commit Time.local fixes for Rails' many timezone-related failures. Patch by Stephen Lewis for JRUBY-3254: Make JRuby's Time#local behave more like MRI
74deae8353 Fix for JRUBY-3049: EXCEPTION_ACCESS_VIOLATION
9d2d976bd6 Fix JRUBY-2219 by adding constant generation based on the FFI const generators.
87fc0c952b Fix for JRUBY-2905: NoMethodError does not give a useful message when thrown in BSF.
1a8b8860af Fix large memory leak in management support. JRUBY-2924.
69da32d5a4 Fix for JRUBY-2753, wrong file in c-return trace from require or load.
8f029624e1 Fix for JRUBY-2738, NPE in TCPServer#peeraddr.
9a0c82816e Partial fixes for JRUBY-2721. This may be enough though, since it guarantees that dead threads can't be added to a threadgroup and once they die they won't stay there long (only a very brief window). Seems to resolve issues with Mongrel too. I will probably mark as resolved once I write up some specs for this behavior.
   Bugs after [       5]:
db853d3697 Get EINPROGRESSWaitWritable from proper location. Fixes JRUBY-7161
0d1ff021d1 Fix #666 by implementing LoadError#path and related load logic.
40d9dabb8d Properly set up the toplevel scope under new logic. Fixes #440.
92b38c48cd Fix JRUBY-5732
3baeea6eec Fix JRUBY-6859

Start block index: 776
End block index: 791
    // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
    // perhaps security methods should find their own centralized home at some point.
    public void checkSafeString(IRubyObject object) {
        if (getSafeLevel() > 0 && object.isTaint()) {
            ThreadContext tc = getCurrentContext();
            if (tc.getFrameName() != null) {
                throw newSecurityError("Insecure operation - " + tc.getFrameName());
            }
            throw newSecurityError("Insecure operation: -r");
        }
        secure(4);
        if (!(object instanceof RubyString)) {
            throw newTypeError(
                "wrong argument type " + object.getMetaClass().getName() + " (expected String)");
        }
    }
