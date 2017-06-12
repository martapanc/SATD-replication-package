File path: src/org/jruby/util/io/ModeFlags.java
Comment: / TODO: Make this more intelligible value
Initial commit id: 66b024fe
Final commit id: e04c88bd
   Bugs between [       1]:
daa36aa25f Partially fix JRUBY-6893: fcntl(Fcntl::F_GETFL) always return 0
   Bugs after [       0]:


Start block index: 204
End block index: 207
    public String toString() {
        // TODO: Make this more intelligible value
        return ""+flags;
    }
