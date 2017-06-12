File path: src/org/jruby/runtime/Interpreted19Block.java
Comment: / We clone dynamic scope because this will be a new instance of a block. Any previously
Initial commit id: bf8db0bc
Final commit id: 0e0ffe2e
   Bugs between [       2]:
99f983249e Rejigger how binding works with files and line numbers. It appears that we weren't tracking everything we needed in a binding. Instead of holding the current frame plus the current file+line, we held only the current frame. This led to us constantly setting and clearing the frames line numbers in an attempt to get traces to line up. With binding carrying the original file+line, that juggling is no longer necessary. This fixes an eval spec and should improve backtraces across eval calls, which had numerous peculiarities before. This also fixes JRUBY-2328: Overriding require causes eval to give wrong __FILE__ in certain circumstances.
ff96ef927f Fix for JRUBY-1872: next statement should return the argument passed, not nil
   Bugs after [       2]:
c3354867f3 Compiler fix for JRUBY-6377 and refactoring.
2225f2cb21 Fixes for JRUBY-5229: Interpreter is slower for microbenchmarks after "backtrace" merge

Start block index: 210
End block index: 223

public Block cloneBlock(Binding binding) {
    // We clone dynamic scope because this will be a new instance of a block.  Any previously
    // captured instances of this block may still be around and we do not want to start
    // overwriting those values when we create a new one.
    // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
    binding = new Binding(
            binding.getSelf(),
            binding.getFrame().duplicate(),
            binding.getVisibility(),
            binding.getKlass(),
            binding.getDynamicScope());

    return new Block(this, binding);
}
