File path: src/org/jruby/RubyProcess.java
Comment: / FIXME: This table will get moved into POSIX library so we can get all actual supported
Initial commit id: c3af7f12
Final commit id: 2f1445af
   Bugs between [      11]:
2f1445af01 Use jnr-constants for signal values.  Fixes JRUBY-5696.
5d094dbf5c Fix JRUBY-5688: Process::Status#pid is missing
78835628f2 Fix JRUBY-5687: Process::Status#exitstatus does not return correct exit status
35a1935d2d Fix JRUBY-5531: Process.spawn("ruby") gives garbage PID
d76f6369e7 Fix JRUBY-4469: Process.spawn seems to be completely broken
7c7ecc5cdb Fix JRUBY-5463: Process.getpriority should raise an error with an invalid process type
3f5f461e62 Improve on the previous fix for JRUBY-4468, so that we can handle the cases where 'kill' sets errno to something other than ESRCH.
42b0fa9bc0 Fix JRUBY-4468: Process.kill doesn't return a correct value
b643acda37 Fix for JRUBY-2795: Process.setpriority failure in rubyspecs on Mac OS X, Soylatte
2ebc903a20 Fix for JRUBY-2353: Process.kill breaks up JRuby, while works in MRI on Windows
bc381827af Fix JRUBY-2796 Missing constant in Process::Constants on Mac OS X (darwin)
   Bugs after [       2]:
4bba125ed4 Fix JRUBY-6906: error message for Process.kill 'EXIT' is wrong
f636731047 Fix JRUBY-5729: Process.respond_to?(:fork) must return false

Start block index: 342
End block index: 361
    private static int parseSignalString(Ruby runtime, String value) {
        int startIndex = 0;
        boolean negative = value.startsWith("-");
        
        if (negative) startIndex++;
        
        boolean signalString = value.startsWith("SIG", startIndex);
        
        if (signalString) startIndex += 3;
       
        String signalName = value.substring(startIndex);
        
        // FIXME: This table will get moved into POSIX library so we can get all actual supported
        // signals.  This is a quick fix to support basic signals until that happens.
        for (int i = 0; i < signals.length; i++) {
            if (signals[i].equals(signalName)) return negative ? -i : i;
        }
        
        throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
    }
