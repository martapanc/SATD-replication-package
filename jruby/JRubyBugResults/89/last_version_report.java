    public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
        return new SelectBlob().goForIt(context, runtime, args);
    }
