File path: src/org/jruby/compiler/ir/IR_Builder.java
Comment: / FIXME: This is a gross way to figure it out
Initial commit id: dfa3f863
Final commit id: b6fc0556
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 922
End block index: 944
    private void addConditionalForWhen(final WhenNode whenNode, List<ArgumentsCallback> conditionals, List<CompilerCallback> bodies, CompilerCallback body) {
        bodies.add(body);

        // If it's a single-arg when but contains an array, we know it's a real literal array
        // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
        if (whenNode.getExpressionNodes() instanceof ArrayNode) {
            if (whenNode instanceof WhenOneArgNode) {
                // one arg but it's an array, treat it as a proper array
                conditionals.add(new ArgumentsCallback() {
                    public int getArity() {
                        return 1;
                    }

                    public void call(IR_BuilderContext m) {
                        build(whenNode.getExpressionNodes(), m, true);
                    }
                });
                return;
            }
        }
        // otherwise, use normal args buildr
        conditionals.add(getArgsCallback(whenNode.getExpressionNodes()));
    }
