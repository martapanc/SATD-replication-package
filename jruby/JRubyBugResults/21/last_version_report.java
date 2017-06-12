    public void beginMethod(CompilerCallback argsCallback, StaticScope scope) {
        // fill in all vars with nil so compiler is happy about future accesses
        if (scope.getNumberOfVariables() > 0) {
            // if we don't have opt args, start after args (they will be assigned later)
            // this is for crap like def foo(a = (b = true; 1)) which numbers b before a
            // FIXME: only starting after required args, since opt args may access others
            // and rest args conflicts with compileRoot using "0" to indicate [] signature.
            int start = scope.getRequiredArgs();
            for (int i = start; i < scope.getNumberOfVariables(); i++) {
                methodCompiler.loadNil();
                assignLocalVariable(i, false);
            }

            // temp locals must start after last real local
            tempVariableIndex += scope.getNumberOfVariables();
        }

        if (argsCallback != null) {
            argsCallback.call(methodCompiler);
        }
    }
