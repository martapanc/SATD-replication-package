File path: src/org/jruby/RubyKernel.java
Comment: / TODO: exec should replace the current process
Initial commit id: 8548cc7b
Final commit id: 0bf23316
   Bugs between [      39]:
5d094dbf5c Fix JRUBY-5688: Process::Status#pid is missing
943194f81f Fix JRUBY-5624: Class.new { p eval("self", binding) }
35a1935d2d Fix JRUBY-5531: Process.spawn("ruby") gives garbage PID
89d5d8cfe0 fix JRUBY-5431: Kernel#exit! without arg causes wrong process exit code. Patch provided by Douglas Campos with some modifications to apply
1b5fcadd47 fix #5431
528e8d8a12 Fix JRUBY-5433: Process.abort should only accept string arguments
028579935f Fix JRUBY-5388: Requiring a filename with accented characters fails
406556bd3b Fix JRUBY-5417: Nil backtrace when overriding method_missing and calling super
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
f3b4b99133 Fix JRUBY-5341: [1.9] Rescuing a specific error throws up a Java Exception when $DEBUG is true
12237879aa Fix for JRUBY-5163: Rails 3 fails to start in 1.9 mode
d0f5663d09 Fix for JRUBY-5079: Kernel#select() prevents exit during a signal handler
5d90f1b219 fix JRUBY-5022: [1.9] Kernel.Float can handle hexadecimal numbers as strings. All specs fixed for String#% in 1.9
9dad670773 A naïve approach to fixing JRUBY-2375: namely, use runWithoutWait() if the command ends with a single ampersand.
41da7ab669 fixes JRUBY-4713: [1.9.2] New Random library is almost completely unimplemented
3ac26d2d4c fixes JRUBY-4713: [1.9.2] New Random library is almost completely unimplemented
be6f6c0e5b Fix for JRUBY-4643: it is correct that M::X does not look for X in Object
a44514e674 fixes JRUBY-4543: Multiple RubySpec failures for Kernel's load() and require()
a55b043acf Fix JRUBY-4449 - make all 1.9 specs for Kernel.Integer run correctly
207cc9155a Fix JRUBY-3306: add define_singleton_method in 1.9 mode
2e4958cdb9 Fix for JRUBY-3868: Exception backtraces have missing entries if __send__ is in callstack
48ae162b97 Fix for JRUBY-3343: Modify require logic to block when two threads require the same file at the same time
434b818193 Fix for JRUBY-3483: Redirecting $stdout to an object blows up the stack
fe7d745ee8 Fix by Robert Dober for JRUBY-3599: loop does not rescue StopIteration in --1.9
99f983249e Rejigger how binding works with files and line numbers. It appears that we weren't tracking everything we needed in a binding. Instead of holding the current frame plus the current file+line, we held only the current frame. This led to us constantly setting and clearing the frame's line numbers in an attempt to get traces to line up. With binding carrying the original file+line, that juggling is no longer necessary. This fixes an eval spec and should improve backtraces across eval calls, which had numerous peculiarities before. This also fixes JRUBY-2328: Overriding require causes eval to give wrong __FILE__ in certain circumstances.
68ed5ba440 Fix for JRUBY-3011: java classes with non-public constructors are incorrectly instantiated
271c6d639b Fix for JRUBY-2252: Kernel#exec should not raise SystemExit
c7b80cf6bd Fix by Joseph LaFata for JRUBY-3298: Kernel.srand calls #to_i on number
0203775151 Fixes for JRUBY-2834: More than 50 RubySpec failures for ARGF
fad3700407 Fix for JRUBY-3131, make "self" in a binding from "send :binding" be the object sent to. Also split 1.8 and 1.9 behavior for now; my vote is that this should be unspecified behavior.
c6090c807d Partial fix for JRUBY-1551; original reported case passes but one case in the provided unit test still fails.
8829a85eec Fix JRUBY-3066 by implementing __method__ and __callee__ for Ruby1.9 compatibility.
4056e6829a Fix for JRUBY-2729. No test provided, because I'm a big slacker. Maybe in a moment.
793296a577 Fix for JRUBY-2605, make explicit bindings use the same logic as implicit eval bindings, so they get frame's block correctly.
2a9dbe56c4 Fixing JRUBY-2041 by removing the old "loop break" logic and doing a simpler jump target test. This passes all tests and everything runs, so I believe the "loop break" logic was probably a workaround for an incorrectly-implemented loop. Optimistically going ahead with this change.
07c9985d07 Modify Kernel#exit! to throw a Java MainExitException instead of a Ruby-catchable SystemExitException. Fixes JRUBY-2363 (I hope).
982486857d Fixes to allow exec'ed in-process subprocesses work more cleanly wrt stdio, by allowing the "parent" processes's normal Java IO stdio streams to be used directly in the "child" process. JRUBY-2156 and JRUBY-2154.
a1ed6bb05e Fix for JRUBY-1455 - reimplement ARGF to be a regular singleton Object instance with singleton methods. This allows cloning to work correctly.
8a81813fcf Fix for JRUBY-1988
   Bugs after [      19]:
3efc72f47b Add Kernel#__dir__. Fixes #651.
0d22cef263 Fix #391.
6de4987a92 Rework the previous logic (calling #to_path) for #393. Also, fix #399.
cf3a2b0da1 Fix #393. For #open, there is an implicit call to #to_path if #to_open fails. See, for example: https://github.com/ruby/ruby/blob/de794ae/io.c#L6170
648df615b1 Fix JRUBY-6994: exec() in chdir block doesn't inherit cwd
a5e3a4ccfe Fix JRUBY-6949
0931687127 Fix JRUBY-6885
4246d96f63 Fix JRUBY-6832
846be19993 Fix JRUBY-6766
7cf0e2e95f Fix JRUBY-6788: Missing prompt in rails console
8ec1150d8e Fixes JRUBY-6677
195b90243b Fix JRUBY-6570: autoload :Time, 'time' doesn't work
677cf1bd46 Implement Kernel#Hash. Fixes JRUBY-6496.
a75ddbea79 Additional fixes for JRUBY-6434
f8408dac26 Fix JRUBY-5348
24031a9cca Fix JRUBY-6234: Kernel.system doesn't work with environement parameters
636d625c43 Applies @knu's patch, https://gist.github.com/1160870 . This patch fixes errors of pure Java Nokogiri tests. A discussion is done at https://github.com/tenderlove/nokogiri/commit/8ee889b81a173a00bc652c68ed8ed512125ee418#commitcomment-542636
f636731047 Fix JRUBY-5729: Process.respond_to?(:fork) must return false
4bd7f02608 Fix JRUBY-5680: eval("self", Kernel.binding)

Start block index: 1150
End block index: 1166
    public static RubyBoolean exec(IRubyObject recv, IRubyObject[] args) {
        Ruby runtime = recv.getRuntime();
        int resultCode;
        try {
            // TODO: exec should replace the current process.
            // This could be possible with JNA. 
            resultCode = new ShellLauncher(runtime).runAndWait(args);
        } catch (Exception e) {
            resultCode = 127;
        }

        if (resultCode != 0) {
            throw runtime.newErrnoENOENTError("cannot execute");
        }

        return runtime.newBoolean(true);
    }
