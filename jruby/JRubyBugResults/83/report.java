File path: core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
Comment: / TODO: don't bother passing when fcall or vcall
Initial commit id: 25fdaa3b
Final commit id: 96034ed2
   Bugs between [       0]:

   Bugs after [       9]:
5f83b908f7 Fixes #4328. Literal rational syntax does not support Bignum.
18cde0df7f Separate varargs and specific-arity names in JIT. Fixes #4482
4e4935ed6f Fixes #4319.  JRuby can not interpret keyword argument when placed after positional argument in block Fixes #2485. proc with extra args incorrectly binds wrong post args
cf2df89ba6 Disable the AddLocalVarLoadStore pass to fix #3891.
5861b1b9c6 Fix __FILE__ in JIT. Fixes #3410.
c9c2390744 Fixes #3046. Shellescaped utf-8 string misbehaving in backticks
0825e699fb fix GH-2591 on master. keeps encoding of symbol name.
d211f19382 Fix #2409: Unbreak JIT
29371d9354 Fix #2409: Splat --> BuildSplatInstr + Splat (for use in call args)

Start block index: 1289
End block index: 1324
    public void OneFixnumArgNoBlockCallInstr(OneFixnumArgNoBlockCallInstr oneFixnumArgNoBlockCallInstr) {
        if (MethodIndex.getFastFixnumOpsMethod(oneFixnumArgNoBlockCallInstr.getName()) == null) {
            CallInstr(oneFixnumArgNoBlockCallInstr);
            return;
        }
        IRBytecodeAdapter m = jvmMethod();
        String name = oneFixnumArgNoBlockCallInstr.getName();
        long fixnum = oneFixnumArgNoBlockCallInstr.getFixnumArg();
        Operand receiver = oneFixnumArgNoBlockCallInstr.getReceiver();
        Variable result = oneFixnumArgNoBlockCallInstr.getResult();

        m.loadContext();

        // for visibility checking without requiring frame self
        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
        m.loadSelf(); // caller

        visit(receiver);

        String signature = sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, IRubyObject.class));

        m.adapter.invokedynamic(
                "fixnumOperator:" + JavaNameMangler.mangleMethodName(name),
                signature,
                InvokeDynamicSupport.getFixnumOperatorHandle(),
                fixnum,
                "",
                0);

        if (result != null) {
            jvmStoreLocal(result);
        } else {
            // still need to drop, since all dyncalls return something (FIXME)
            m.adapter.pop();
        }
    }
