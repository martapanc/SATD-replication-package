public void Regexp(Regexp regexp) {
    if (!regexp.hasKnownValue() && !regexp.options.isOnce()) {
        jvmMethod().loadRuntime();
        visit(regexp.getRegexp());
        jvmAdapter().invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
        jvmAdapter().ldc(regexp.options.toEmbeddedOptions());
        jvmAdapter().invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, Ruby.class, RubyString.class, int.class));
        jvmAdapter().dup();
        jvmAdapter().invokevirtual(p(RubyRegexp.class), "setLiteral", sig(void.class));
    } else {
        // FIXME: need to check this on cached path
        // context.runtime.getKCode() != rubyRegexp.getKCode()) {
        jvmMethod().loadContext();
        visit(regexp.getRegexp());
        jvmMethod().pushRegexp(regexp.options.toEmbeddedOptions());
    }
}
