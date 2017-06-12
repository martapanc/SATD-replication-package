    public ModeFlags(long flags) throws InvalidValueException {
    	// TODO: Ruby does not seem to care about invalid numeric mode values
    	// I am not sure if ruby overflows here also...
        this.flags = (int)flags;
        
        if (isReadOnly() && ((flags & APPEND) != 0)) {
            // MRI 1.8 behavior: this combination of flags is not allowed
            throw new InvalidValueException();
        }
    }
