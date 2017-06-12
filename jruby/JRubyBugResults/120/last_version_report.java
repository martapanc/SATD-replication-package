@JRubyMethod(name = "syswrite", required = 1)
public IRubyObject syswrite(ThreadContext context, IRubyObject obj) {
    return doWriteNonblock(context, obj, true);
}
