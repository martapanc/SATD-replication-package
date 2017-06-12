    public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject self, IRubyObject src, Binding binding) {
        Ruby runtime = context.runtime;
        if (runtime.getInstanceConfig().getCompileMode() == RubyInstanceConfig.CompileMode.TRUFFLE) throw new UnsupportedOperationException();

        DynamicScope evalScope = binding.getEvalScope(runtime);
        evalScope.getStaticScope().determineModule(); // FIXME: It would be nice to just set this or remove it from staticScope altogether

        Frame lastFrame = context.preEvalWithBinding(binding);
        try {
            return evalCommon(context, evalScope, self, src, binding.getFile(),
                    binding.getLine(), binding.getMethod(), binding.getFrame().getBlock(), EvalType.BINDING_EVAL);
        } finally {
            context.postEvalWithBinding(binding, lastFrame);
        }
    }
