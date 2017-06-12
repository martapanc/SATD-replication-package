File path: truffle/src/main/java/org/jruby/truffle/parser/Helpers.java
Comment: / TODO: this is kinda gross
Initial commit id: fa938c4c
Final commit id: 277af88f
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 286
End block index: 318
    public static Errno errnoFromException(Throwable t) {
        if (t instanceof ClosedChannelException) {
            return Errno.EBADF;
        }

        // TODO: this is kinda gross
        if(t.getMessage() != null) {
            String errorMessage = t.getMessage();

            // All errors to sysread should be SystemCallErrors, but on a closed stream
            // Ruby returns an IOError.  Java throws same exception for all errors so
            // we resort to this hack...

            if ("Bad file descriptor".equals(errorMessage)) {
                return Errno.EBADF;
            } else if ("File not open".equals(errorMessage)) {
                return null;
            } else if ("An established connection was aborted by the software in your host machine".equals(errorMessage)) {
                return Errno.ECONNABORTED;
            } else if (t.getMessage().equals("Broken pipe")) {
                return Errno.EPIPE;
            } else if ("Connection reset by peer".equals(errorMessage) ||
                       "An existing connection was forcibly closed by the remote host".equals(errorMessage) ||
                    (Platform.IS_WINDOWS && errorMessage.contains("connection was aborted"))) {
                return Errno.ECONNRESET;
            } else if (errorMessage.equals("No space left on device")) {
                return Errno.ENOSPC;
            } else if (errorMessage.equals("Too many open files")) {
                return Errno.EMFILE;
            }
        }
        return null;
    }
