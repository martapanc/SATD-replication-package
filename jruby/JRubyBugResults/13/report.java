File path: core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
Comment: / FIXME: I don't like this
Initial commit id: 2877eda4
Final commit id: 389b7657
   Bugs between [       0]:

   Bugs after [      12]:
5f83b908f7 Fixes #4328. Literal rational syntax does not support Bignum.
18cde0df7f Separate varargs and specific-arity names in JIT. Fixes #4482
4e4935ed6f Fixes #4319.  JRuby can not interpret keyword argument when placed after positional argument in block Fixes #2485. proc with extra args incorrectly binds wrong post args
cf2df89ba6 Disable the AddLocalVarLoadStore pass to fix #3891.
5861b1b9c6 Fix __FILE__ in JIT. Fixes #3410.
c9c2390744 Fixes #3046. Shellescaped utf-8 string misbehaving in backticks
0825e699fb fix GH-2591 on master. keeps encoding of symbol name.
d211f19382 Fix #2409: Unbreak JIT
29371d9354 Fix #2409: Splat --> BuildSplatInstr + Splat (for use in call args)
89f548ea9b Fixes #2172: Symbols need to support UTF-8 names
6da2fed17a Revert to uncached super logic to fix #2092.
c3b80aa0f9 Fix defined?(::Object) logic in JIT. Partial fix for #2090.

Start block index: 1869
End block index: 2032
    public void Regexp(Regexp regexp) {
        if (!regexp.hasKnownValue() && !regexp.options.isOnce()) {
            if (regexp.getRegexp() instanceof CompoundString) {
                // FIXME: I don't like this custom logic for building CompoundString bits a different way :-\
                jvm.method().loadRuntime();
                { // negotiate RubyString pattern from parts
                    jvm.method().loadRuntime();
                    { // build RubyString[]
                        List<Operand> operands = ((CompoundString)regexp.getRegexp()).getPieces();
                        jvm.method().adapter.ldc(operands.size());
                        jvm.method().adapter.anewarray(p(RubyString.class));
                        for (int i = 0; i < operands.size(); i++) {
                            Operand operand = operands.get(i);
                            jvm.method().adapter.dup();
                            jvm.method().adapter.ldc(i);
                            visit(operand);
                            jvm.method().adapter.aastore();
                        }
                    }
                    jvm.method().adapter.ldc(regexp.options.toEmbeddedOptions());
                    jvm.method().adapter.invokestatic(p(RubyRegexp.class), "preprocessDRegexp", sig(RubyString.class, Ruby.class, RubyString[].class, int.class));
                }
                jvm.method().adapter.ldc(regexp.options.toEmbeddedOptions());
                jvm.method().adapter.invokestatic(p(RubyRegexp.class), "newDRegexp", sig(RubyRegexp.class, Ruby.class, RubyString.class, int.class));
            } else {
                jvm.method().loadRuntime();
                visit(regexp.getRegexp());
                jvm.method().adapter.invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
                jvm.method().adapter.ldc(regexp.options.toEmbeddedOptions());
                jvm.method().adapter.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, Ruby.class, RubyString.class, int.class));
            }
            jvm.method().adapter.dup();
            jvm.method().adapter.invokevirtual(p(RubyRegexp.class), "setLiteral", sig(void.class));
        } else {
            // FIXME: need to check this on cached path
            // context.runtime.getKCode() != rubyRegexp.getKCode()) {
            jvm.method().loadContext();
            visit(regexp.getRegexp());
            jvm.method().pushRegexp(regexp.options.toEmbeddedOptions());
        }
    }

    @Override
    public void ScopeModule(ScopeModule scopemodule) {
        jvm.method().adapter.aload(1);
        jvm.method().adapter.invokevirtual(p(StaticScope.class), "getModule", sig(RubyModule.class));
    }

    @Override
    public void Self(Self self) {
        // %self is in JVM-local-2 always
        jvm.method().loadLocal(2);
    }

    @Override
    public void Splat(Splat splat) {
        jvm.method().loadContext();
        visit(splat.getArray());
        jvm.method().invokeHelper("irSplat", RubyArray.class, ThreadContext.class, IRubyObject.class);
    }

    @Override
    public void StandardError(StandardError standarderror) {
        jvm.method().loadRuntime();
        jvm.method().adapter.invokevirtual(p(Ruby.class), "getStandardError", sig(RubyClass.class));
    }

    @Override
    public void StringLiteral(StringLiteral stringliteral) {
        jvm.method().pushString(stringliteral.getByteList());
    }

    @Override
    public void SValue(SValue svalue) {
        super.SValue(svalue);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void Symbol(Symbol symbol) {
        jvm.method().pushSymbol(symbol.getName());
    }

    @Override
    public void TemporaryVariable(TemporaryVariable temporaryvariable) {
        jvmLoadLocal(temporaryvariable);
    }

    @Override
    public void TemporaryLocalVariable(TemporaryLocalVariable temporarylocalvariable) {
        jvmLoadLocal(temporarylocalvariable);
    }

    @Override
    public void TemporaryFloatVariable(TemporaryFloatVariable temporaryfloatvariable) {
        jvmLoadLocal(temporaryfloatvariable);
    }

    @Override
    public void TemporaryFixnumVariable(TemporaryFixnumVariable temporaryfixnumvariable) {
        jvmLoadLocal(temporaryfixnumvariable);
    }

    @Override
    public void TemporaryBooleanVariable(TemporaryBooleanVariable temporarybooleanvariable) {
        jvmLoadLocal(temporarybooleanvariable);
    }

    @Override
    public void UndefinedValue(UndefinedValue undefinedvalue) {
        jvm.method().pushUndefined();
    }

    @Override
    public void UnexecutableNil(UnexecutableNil unexecutablenil) {
        throw new RuntimeException(this.getClass().getSimpleName() + " should never be directly executed!");
    }

    @Override
    public void WrappedIRClosure(WrappedIRClosure wrappedirclosure) {
        IRClosure closure = wrappedirclosure.getClosure();

        jvm.method().adapter.newobj(p(Block.class));
        jvm.method().adapter.dup();

        { // prepare block body (should be cached
            jvm.method().adapter.newobj(p(CompiledIRBlockBody.class));
            jvm.method().adapter.dup();

            // FIXME: This is inefficient because it's creating a new StaticScope every time
            String encodedScope = Helpers.encodeScope(closure.getStaticScope());
            jvm.method().loadContext();
            jvm.method().loadStaticScope();
            jvm.method().adapter.ldc(encodedScope);
            jvm.method().adapter.invokestatic(p(Helpers.class), "decodeScopeAndDetermineModule", sig(StaticScope.class, ThreadContext.class, StaticScope.class, String.class));

            jvm.method().adapter.ldc(Helpers.stringJoin(",", closure.getParameterList()));

            jvm.method().adapter.ldc(closure.getFileName());

            jvm.method().adapter.ldc(closure.getLineNumber());

            jvm.method().adapter.ldc(closure.isForLoopBody() || closure.isBeginEndBlock());

            jvm.method().adapter.ldc(closure.getHandle());

            jvm.method().adapter.ldc(closure.getArity().getValue());

            jvm.method().adapter.invokespecial(p(CompiledIRBlockBody.class), "<init>", sig(void.class, StaticScope.class, String.class, String.class, int.class, boolean.class, java.lang.invoke.MethodHandle.class, int.class));
        }

        { // prepare binding
            jvm.method().loadContext();
            visit(closure.getSelf());
            jvmLoadLocal(DYNAMIC_SCOPE);
            jvm.method().adapter.invokevirtual(p(ThreadContext.class), "currentBinding", sig(Binding.class, IRubyObject.class, DynamicScope.class));
        }

        jvm.method().adapter.invokespecial(p(Block.class), "<init>", sig(void.class, BlockBody.class, Binding.class));
    }

    private final JVM jvm;
    private IRScope currentScope;
    private int methodIndex = 0;
}
