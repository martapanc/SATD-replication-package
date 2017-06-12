private synchronized IRubyObject status(Ruby runtime) {
    if (threadImpl.isAlive()) {
        return RubyString.newStringShared(runtime, status.get().bytes);
    } else if (exitingException != null) {
        return runtime.getNil();
    } else {
        return runtime.getFalse();
    }
}
