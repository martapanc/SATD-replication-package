File path: core/src/main/java/org/jruby/ir/interpreter/Interpreter.java
Comment: / FIXME: This determine module is in a strange location and should somehow be in block
Initial commit id: a8c5cfe3
Final commit id: 8e706223
   Bugs between [       1]:
dd7f8b74be Fix #2205: All (except top-level) scopes have PUBLIC visibility by default
   Bugs after [       1]:
b5b730563b Fix #2301

Start block index: 749
End block index: 782

public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject self, IRubyObject src, Binding binding) {
    Ruby runtime = src.getRuntime();
    DynamicScope evalScope;

    // in 1.9, eval scopes are local to the binding
    evalScope = binding.getEvalScope(runtime);

    // FIXME:  This determine module is in a strange location and should somehow be in block
    evalScope.getStaticScope().determineModule();

    Frame lastFrame = context.preEvalWithBinding(binding);
    try {
        // Binding provided for scope, use it
        RubyString source = src.convertToString();
        Node node = runtime.parseEval(source.getByteList(), binding.getFile(), evalScope, binding.getLine());
        Block block = binding.getFrame().getBlock();

        if (runtime.getInstanceConfig().getCompileMode() == RubyInstanceConfig.CompileMode.TRUFFLE) {
            throw new UnsupportedOperationException();
        }

        // SSS FIXME: AST interpreter passed both a runtime (which comes from the source string)
        // and the thread-context rather than fetch one from the other.  Why is that?
        return Interpreter.interpretBindingEval(runtime, binding.getFile(), binding.getLine(), binding.getMethod(), node, self, block);
    } catch (JumpException.BreakJump bj) {
        throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
    } catch (JumpException.RedoJump rj) {
        throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, (IRubyObject)rj.getValue(), "unexpected redo");
    } catch (StackOverflowError soe) {
        throw runtime.newSystemStackError("stack level too deep", soe);
    } finally {
        context.postEvalWithBinding(binding, lastFrame);
    }
}
