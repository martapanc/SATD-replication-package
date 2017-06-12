File path: core/src/main/java/org/jruby/compiler/ASTCompiler.java
Comment: / FIXME: This is temporary since the variable compilers assume we want
Initial commit id: 4e87bc6e
Final commit id: ed54aab1
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 2613
End block index: 2630
            public void call(BodyCompiler context) {
                // FIXME: This is temporary since the variable compilers assume we want
                // args already on stack for assignment. We just pop and continue with
                // 1.9 args logic.
                context.consumeCurrentValue(); // args value
                context.consumeCurrentValue(); // passed block
                if (iterNode.getVarNode() != null) {
                    if (iterNode instanceof LambdaNode) {
                        final int required = argsNode.getRequiredArgsCount();
                        final int opt = argsNode.getOptionalArgsCount();
                        final int rest = argsNode.getRestArg();
                        context.getVariableCompiler().checkMethodArity(required, opt, rest);
                        compileMethodArgs(argsNode, context, true);
                    } else {
                        compileMethodArgs(argsNode, context, true);
                    }
                }
            }
