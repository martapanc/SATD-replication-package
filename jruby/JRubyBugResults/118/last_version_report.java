@Deprecated
public IRubyObject setConstantInCurrent(String internedName, IRubyObject result) {
    return getCurrentStaticScope().setConstant(internedName, result);
}
