File path: src/org/jruby/util/io/ModeFlags.java
Comment: / TODO: Ruby does not seem to care about invalid numeric mode values
Initial commit id: 66b024fe
Final commit id: aa4ffa1b
   Bugs between [       0]:

   Bugs after [       1]:
daa36aa25f Partially fix JRUBY-6893: fcntl(Fcntl::F_GETFL) always return 0

Start block index: 85
End block index: 94
    public ModeFlags(long flags) throws InvalidValueException {
    	// TODO: Ruby does not seem to care about invalid numeric mode values
    	// I am not sure if ruby overflows here also...
        this.flags = (int)flags;
        
        if (isReadOnly() && ((flags & APPEND) != 0)) {
            // MRI 1.8 behavior: this combination of flags is not allowed
            throw new InvalidValueException();
        }
    }
