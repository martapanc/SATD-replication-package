File path: src/org/jruby/ext/socket/RubySocket.java
Comment: / Workaround for a bug in Sun's JDK 1.5.x
Initial commit id: eb1f44e4
Final commit id: 606bd2e1
   Bugs between [       2]:
56c1fb34da Fix JRUBY-5594: Seeing ConnectionPendingException in my code
2eb09cdec6 Fix intermittent failure in JRUBY-5232 regression test.
   Bugs after [       3]:
97409f4889 Fix JRUBY-6761
ce2282e2f6 Fix JRUBY-6527
d5a87bf2d6 Fix JRUBY-6526 and continue socket refactoring.

Start block index: 349
End block index: 388
    public IRubyObject bind(ThreadContext context, IRubyObject arg) {
        RubyArray sockaddr = (RubyArray) unpack_sockaddr_in(context, this, arg);

        try {
            IRubyObject addr = sockaddr.pop(context);
            IRubyObject port = sockaddr.pop(context);
            InetSocketAddress iaddr = new InetSocketAddress(
                    addr.convertToString().toString(), RubyNumeric.fix2int(port));

            Channel socketChannel = getChannel();
            if (socketChannel instanceof SocketChannel) {
                Socket socket = ((SocketChannel)socketChannel).socket();
                socket.bind(iaddr);
            } else if (socketChannel instanceof DatagramChannel) {
                DatagramSocket socket = ((DatagramChannel)socketChannel).socket();
                socket.bind(iaddr);
            } else {
                throw getRuntime().newErrnoENOPROTOOPTError();
            }
        } catch(UnknownHostException e) {
            throw sockerr(context.getRuntime(), "bind(2): unknown host");
        } catch(SocketException e) {
            handleSocketException(context.getRuntime(), e);
        } catch(IOException e) {
            e.printStackTrace();
            throw sockerr(context.getRuntime(), "bind(2): name or service not known");
        } catch (IllegalArgumentException iae) {
            throw sockerr(context.getRuntime(), iae.getMessage());
        } catch (Error e) {
            // Workaround for a bug in Sun's JDK 1.5.x, see
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6303753
            Throwable cause = e.getCause();
            if (cause instanceof SocketException) {
                handleSocketException(context.getRuntime(), (SocketException)cause);
            } else {
                throw e;
            }
        }
        return RubyFixnum.zero(context.getRuntime());
    }
