File path: src/org/jruby/runtime/MethodBlock.java
Comment: / TODO: This is actually now returning the scope of whoever called Method#to_proc
Initial commit id: cdb1c7f5
Final commit id: fd0fa789
   Bugs between [       1]:
99f983249e Rejigger how binding works with files and line numbers. It appears that we weren't tracking everything we needed in a binding. Instead of holding the current frame plus the current file+line, we held only the current frame. This led to us constantly setting and clearing the frame's line numbers in an attempt to get traces to line up. With binding carrying the original file+line, that juggling is no longer necessary. This fixes an eval spec and should improve backtraces across eval calls, which had numerous peculiarities before. This also fixes JRUBY-2328: Overriding require causes eval to give wrong __FILE__ in certain circumstances.
   Bugs after [       1]:
13da9cb5b5 Fix JRUBY-6367.

Start block index: 130
End block index: 135
    public StaticScope getStaticScope() {
        // TODO: This is actually now returning the scope of whoever called Method#to_proc
        // which is obviously wrong; but there's no scope to provide for many methods.
        // It fixes JRUBY-2237, but needs a better solution.
        return staticScope;
    }
