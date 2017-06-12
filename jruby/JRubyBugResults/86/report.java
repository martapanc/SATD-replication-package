File path: src/org/jruby/runtime/builtin/meta/IOMetaClass.java
Comment: / TODO: Implement tty? and isatty. We have no real capability to
Initial commit id: ae3178fb
Final commit id: bac69eda
   Bugs between [       6]:
3cd5f13072 Fix for JRUBY-791, and also inspect on Process::Status
1c02ca0e64 Fixes for JRUBY-734, along with a first run at cleaning up IRubyObject.
49cf139366 Fix for JRUBY-455: It was using the wrong argument for write selects, and if arg[0] was nil and arg[1] was not, ClassCastEx would result.
57504d74a1 Initial fixes for JRUBY-408. Added ObjectAllocator and code to consume it throughout the MetaClass hierarchy.
ebb36c83a5 Fix for JRUBY-249, by Damian Steer. A first version of IO#pipe
9994820016 Fix for JRUBY-321: remove CONNECT from interested events on reads, since it is always "ready" for open sockets.
   Bugs after [       0]:

Start block index: 52
End block index: 108

protected void initializeClass() {
    includeModule(getRuntime().getClasses().getEnumerableModule());

    // TODO: Implement tty? and isatty.  We have no real capability to
    // determine this from java, but if we could set tty status, then
    // we could invoke jruby differently to allow stdin to return true
    // on this.  This would allow things like cgi.rb to work properly.

    defineSingletonMethod("foreach", Arity.optional(), "foreach");
    defineSingletonMethod("readlines", Arity.optional(), "readlines");

    CallbackFactory callbackFactory = getRuntime().callbackFactory(RubyIO.class);
    defineMethod("<<", callbackFactory.getMethod("addString", IRubyObject.class));
    defineMethod("clone", callbackFactory.getMethod("clone_IO"));
    defineMethod("close", callbackFactory.getMethod("close"));
    defineMethod("closed?", callbackFactory.getMethod("closed"));
    defineMethod("each", callbackFactory.getOptMethod("each_line"));
    defineMethod("each_byte", callbackFactory.getMethod("each_byte"));
    defineMethod("each_line", callbackFactory.getOptMethod("each_line"));
    defineMethod("eof", callbackFactory.getMethod("eof"));
    defineMethod("eof?", callbackFactory.getMethod("eof"));
    defineMethod("fileno", callbackFactory.getMethod("fileno"));
    defineMethod("flush", callbackFactory.getMethod("flush"));
    defineMethod("fsync", callbackFactory.getMethod("fsync"));
    defineMethod("getc", callbackFactory.getMethod("getc"));
    defineMethod("gets", callbackFactory.getOptMethod("gets"));
    defineMethod("initialize", Arity.optional(), "initialize");
    defineMethod("lineno", callbackFactory.getMethod("lineno"));
    defineMethod("lineno=", callbackFactory.getMethod("lineno_set", RubyFixnum.class));
    defineMethod("pid", callbackFactory.getMethod("pid"));
    defineMethod("pos", callbackFactory.getMethod("pos"));
    defineMethod("pos=", callbackFactory.getMethod("pos_set", RubyFixnum.class));
    defineMethod("print", callbackFactory.getOptSingletonMethod("print"));
    defineMethod("printf", callbackFactory.getOptSingletonMethod("printf"));
    defineMethod("putc", callbackFactory.getMethod("putc", IRubyObject.class));
    defineMethod("puts", callbackFactory.getOptSingletonMethod("puts"));
    defineMethod("read", callbackFactory.getOptMethod("read"));
    defineMethod("readchar", callbackFactory.getMethod("readchar"));
    defineMethod("readline", callbackFactory.getOptMethod("readline"));
    defineMethod("readlines", callbackFactory.getOptMethod("readlines"));
    defineMethod("reopen", callbackFactory.getOptMethod("reopen", IRubyObject.class));
    defineMethod("rewind", callbackFactory.getMethod("rewind"));
    defineMethod("seek", callbackFactory.getOptMethod("seek"));
    defineMethod("sync", callbackFactory.getMethod("sync"));
    defineMethod("sync=", callbackFactory.getMethod("sync_set", RubyBoolean.class));
    defineMethod("sysread", callbackFactory.getMethod("sysread", RubyFixnum.class));
    defineMethod("syswrite", callbackFactory.getMethod("syswrite", IRubyObject.class));
    defineMethod("tell", callbackFactory.getMethod("pos"));
    defineMethod("to_i", callbackFactory.getMethod("fileno"));
    defineMethod("ungetc", callbackFactory.getMethod("ungetc", RubyFixnum.class));
    defineMethod("write", callbackFactory.getMethod("write", IRubyObject.class));

    // Constants for seek
    setConstant("SEEK_SET", getRuntime().newFixnum(IOHandler.SEEK_SET));
    setConstant("SEEK_CUR", getRuntime().newFixnum(IOHandler.SEEK_CUR));
    setConstant("SEEK_END", getRuntime().newFixnum(IOHandler.SEEK_END));
}
