public RubyClass defineClassUnder(final RubyModule pkg, final String name, final RubyClass superClazz) {
    // shouldn't happen, but if a superclass is specified, it's not ours
    if (superClazz != null) {
        return null;
    }
    final IRubyObject packageName;
    // again, shouldn't happen. TODO: might want to throw exception instead.
    if ((packageName = pkg.getInstanceVariable("@package_name")) == null) return null;

    final Ruby runtime = pkg.getRuntime();
    return (RubyClass)get_proxy_class(
            runtime.getJavaSupport().getJavaUtilitiesModule(),
            JavaClass.forName(runtime, packageName.asSymbol() + name));
}
