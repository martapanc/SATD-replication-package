    public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
        return runtime.getEncodingService().getEncoding(encoding);
    }
