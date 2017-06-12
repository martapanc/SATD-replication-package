    public IRubyObject bind(ThreadContext context, IRubyObject arg) {
        InetSocketAddress iaddr = Sockaddr.addressFromSockaddr_in(context, arg);

        doBind(context, getChannel(), iaddr);

        return RubyFixnum.zero(context.getRuntime());
    }
