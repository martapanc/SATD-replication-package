File path: src/org/jruby/javasupport/util/RuntimeHelpers.java
Comment: / FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
Initial commit id: 7af96b98
Final commit id: bb98e4b8
   Bugs between [      21]:
40d9dabb8d Properly set up the toplevel scope under new logic. Fixes #440.
6b13c46b68 Fix JRUBY-6978
47a88ba5a7 Fix #366
da2a9dd478 Additional fix to go with #276 and its fix #293.
a2c656195f Fix for issue #276
846be19993 Fix JRUBY-6766
799757739f Fix for JRUBY-6728. This fixes Nokogiri's 1.9 test error of PrettyPrint as well as https://github.com/sparklemotion/nokogiri/issues/657.
0723b7d750 Fix JRUBY-5863: Named captures cause crash when there is no match
c3354867f3 Compiler fix for JRUBY-6377 and refactoring.
193b1e5b06 Fix JRUBY-4925: Evaluation of constant happens before rhs of assignment (different to mri)
36dd3a0424 Fix JRUBY-4339: Kernel.load with wrap=true does not protect the global namespace of calling program
a0bd87b707 Fix JRUBY-5632: [19] Incompatible behaviour of splat operator (*) with objects that implement #to_a (w.r.t. MRI)
0fbb8e22be fixes JRUBY-4807: Method missing behavior error
9ebf876d8d Add specs for rescuing Java exceptions with "Exception" or "Object", add duck-typed methods from Ruby Exception to Java Throwable plus specs, and fix a few gaps in the logic for JRUBY-4677.
323ba1e629 Fix for JRUBY-4677: Java exceptions can't be rescued with "rescue Exception"
25720ab186 Fix for JRUBY-1531: Tracing in compiler / Tracing AOT compiled code
6266374bc8 Fix for JRUBY-4053: ActiveRecord AssociationCollection#== method is returning false on equal results
e85018a30c Fix by Leonardo Borges for JRUBY-2349: Bug in constant lookup on non Module or Class
4b8ccfae5a Fix for JRUBY-3517: Incorrect self in a multiple assignment
1d1e533478 Kinda hacky fixes for exceptions thrown from toplevel; backtrace isn't great, but it isn't NullPointerException. Should at least improve JRUBY-3439 and JRUBY-3345. I will leave the latter open to continue fixing the trace.
6251ec2ebb Fix for JRUBY-3207: super error message is inaccurate when superclass doesn't implement method.
   Bugs after [       0]:


Start block index: 1541
End block index: 1546
        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
        if (iter.getBlockBody() instanceof InterpretedBlock) {
            return InterpretedBlock.newInterpretedClosure(context, iter.getBlockBody(), self);
        } else {
            return Interpreted19Block.newInterpretedClosure(context, iter.getBlockBody(), self);
        }
