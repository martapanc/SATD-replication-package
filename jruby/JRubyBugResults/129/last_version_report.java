@Deprecated
public IRubyObject removeCvar(IRubyObject name) {
    return removeClassVariable(name.asJavaString());
}
