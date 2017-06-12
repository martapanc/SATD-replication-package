File path: src/org/jruby/runtime/ThreadContext.java
Comment: / TODO: wire into new exception handling mechanism
Initial commit id: 3fa144eb
Final commit id: efd8a3df
   Bugs between [      39]:
fbc85d9466 Fix JRUBY-5883: Hash Subclass#== not respected when checking equality of collections.
ff1ec4932a Fixes and support code for JRUBY-5596: Improve Dalli IO performance
527be7924f Fix JRUBY-5471: private method `verify_mode=' called with Bundler, net/https, jruby-openssl and RubyGems 1.5
67b5fb8dad Fix JRUBY-5477: Caller stacks now include AbstractScript.java
80ab30ab0b Fix JRUBY-5363: Kernel#caller behavior is different for files with .rbw extension
2225f2cb21 Fixes for JRUBY-5229: Interpreter is slower for microbenchmarks after "backtrace" merge
20ff6278cc Fix JRUBY-5322: NPE forcing to compile a script
62ecad3bed Fix JRUBY-5221: jruby.reify.classes raising error with optparse
9793404945 Fix for JRUBY-5086: jruby should output more full of a stack trace in 1.9 mode
a4d33150bc Final fixes and test for JRUBY-4264: threadContextMap leaks RubyThread on death of adopted thread
93b1257dad Revert "Fix for JRUBY-4484: jruby -rtracer doesn't trace"
26f5d63fd5 Fix for JRUBY-4531: java_send for static methods
5f3de42461 Fix for JRUBY-4484: jruby -rtracer doesn't trace
d9835f8206 Interpreter fix for JRUBY-4497: return in eigenclass definition is not working
cbe7b2b790 Fix for JRUBY-4262: Weird Proc test makes JRuby to skip the rest of the tests completely
25720ab186 Fix for JRUBY-1531: Tracing in compiler / Tracing AOT compiled code
1d1e533478 Kinda hacky fixes for exceptions thrown from toplevel; backtrace isn't great, but it isn't NullPointerException. Should at least improve JRUBY-3439 and JRUBY-3345. I will leave the latter open to continue fixing the trace.
99f983249e Rejigger how binding works with files and line numbers. It appears that we weren't tracking everything we needed in a binding. Instead of holding the current frame plus the current file+line, we held only the current frame. This led to us constantly setting and clearing the frame's line numbers in an attempt to get traces to line up. With binding carrying the original file+line, that juggling is no longer necessary. This fixes an eval spec and should improve backtraces across eval calls, which had numerous peculiarities before. This also fixes JRUBY-2328: Overriding require causes eval to give wrong __FILE__ in certain circumstances.
b585f5861b Fix for JRUBY-3397: defined? CONST is not following proper constant lookup rules
df7caff252 Fix for JRUBY-2948: Exceptions do not cut off at binding evals after change reported in JRUBY-2945
98eef6f783 Fix for JRUBY-3093: alias plus eval plus module does not appear to be reflecting correct lookup hierarchy
69da32d5a4 Fix for JRUBY-2753, wrong file in c-return trace from require or load.
8d1eef47be Fixes for JRUBY-2744, reenable c-call and c-return tracing for native methods.
4056e6829a Fix for JRUBY-2729. No test provided, because I'm a big slacker. Maybe in a moment.
1d062e2805 Fix for "leak" from frames holding on to object references after they've been popped. JRUBY-2712. The performance difference on e.g. fib benchmark appears to be marginal (0.31s to 0.32-0.33s), and certainly worth reducing memory footprint in cases where the frame holds too much.
2a610b17d3 Return to thread-local ReturnJump, but do it right this time: private final, not private static final. Stupid me. Fix for JRUBY-2573.
a368103565 Fix for JRUBY-2573, I hope. Hard one to get a test case for.
a7a787839f Fixes for JRUBY-2267: clone frames always when constructing a binding, but don't clone on push and put backref/lastline in a separate structure so they can "float". Tests coming. This may also fix other backref/lastline issues.
c1a09e3d0e Fix JRUBY-2489, handle backrefs more correctly by using frame deltas for grep.
260ac769de Fix for JRUBY-2344: move "every 256 calls" counter used for thread event polling into ThreadContext as a field.
a2d7f322e6 Partial fix for JRUBY-2328
514a2e68c3 Fix for JRUBY-2261 and an improved mechanism for cleaning up old RubyThread instances. Also a test to ensure we don't start leaking RubyThread instances in this way again in the future.
7932c1b4b7 Partial fix for JRUBY-2112: Incorrect lines in exception backtraces.
c6ba8ed8ae Complete fix for JRUBY-2004: moved the type error logic into setConstantInModule so both compiler and interpreter get the benefit and added a test.
6bb89c4d85 Fix for JRUBY-1840, make sure correct RubyClass is pushed based on the incoming method's scope, not the previous scope.
e8a4f5d489 Port fix for JRUBY-1339 (const_defined? and defined?Constant) from 1_0 branch.
95108e35e6 Fixes for JRUBY-1185, handle top-level visibility appropriately and use public visibility for module, class, and struct blocks.
b31b395a5f Fix for JRUBY-959: make ThreadContext.getLastLine return nil if null, for internal consumers of lastline.
3205259a3e Fix for JRUBY-766, and potentially for many other cases where returns weren't getting an appropriate jump target.
   Bugs after [       0]:


Start block index: 516
End block index: 529
    public IRubyObject setConstantInCurrent(String name, IRubyObject result) {
        RubyModule module;

        // FIXME: why do we check RubyClass and then use CRef?
        if (getRubyClass() == null) {
            // TODO: wire into new exception handling mechanism
            throw runtime.newTypeError("no class/module to define constant");
        }
        module = (RubyModule) peekCRef().getValue();
   
        setConstantInModule(name, module, result);
   
        return result;
    }
