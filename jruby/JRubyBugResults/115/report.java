File path: src/org/jruby/runtime/ThreadContext.java
Comment: / TODO: This probably isn't the best hack
Initial commit id: d5917ab0
Final commit id: fd0fa789
   Bugs between [      11]:
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
   Bugs after [       8]:
fbc85d9466 Fix JRUBY-5883: Hash Subclass#== not respected when checking equality of collections.
ff1ec4932a Fixes and support code for JRUBY-5596: Improve Dalli IO performance
527be7924f Fix JRUBY-5471: private method `verify_mode=' called with Bundler, net/https, jruby-openssl and RubyGems 1.5
67b5fb8dad Fix JRUBY-5477: Caller stacks now include AbstractScript.java
80ab30ab0b Fix JRUBY-5363: Kernel#caller behavior is different for files with .rbw extension
2225f2cb21 Fixes for JRUBY-5229: Interpreter is slower for microbenchmarks after "backtrace" merge
20ff6278cc Fix JRUBY-5322: NPE forcing to compile a script
62ecad3bed Fix JRUBY-5221: jruby.reify.classes raising error with optparse

Start block index: 617
End block index: 638
    private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, Frame frame, Frame previousFrame) {
        if (frame != previousFrame && // happens with native exceptions, should not filter those out
                frame.getLine() == previousFrame.getLine() &&
                frame.getName() != null && 
                frame.getName().equals(previousFrame.getName()) &&
                frame.getFile().equals(previousFrame.getFile())) {
            return;
        }
        
        RubyString traceLine;
        if (previousFrame.getName() != null) {
            traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `" + previousFrame.getName() + '\'');
        } else if (runtime.is1_9()) {
            // TODO: This probably isn't the best hack, but it works until we can have different
            // root frame setup for 1.9 easily.
            traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `<main>'");
        } else {
            traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1));
        }
        
        backtrace.append(traceLine);
    }
