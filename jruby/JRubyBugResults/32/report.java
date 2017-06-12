File path: src/org/jruby/compiler/impl/StandardASMCompiler.java
Comment: / FIXME: this really ought to be in clinit
Initial commit id: 362877f0
Final commit id: 3849128d
   Bugs between [      13]:
9d6e9ed703 Fix for JRUBY-2734. NPE in defined?(Something.does_not_exist).
993f8c99ef A bunch of findbugs fixes from JRUBY-1173. Also renamed generated callbacks to be Callback in their name instead of Invoker, to distinguish from actual invokers.
87342fb6ef Fix for JRUBY-2246, chained methods require heap-based scopes.
7d02f3be36 Fix for JRUBY-2179: cache BigInteger instances to speed up literal bignum creation.
35eb534bae Remaining fixes for JRUBY-2033, poor performance for attr assignment. Extends "fast" specific-arity attr assignment to all cases.
fa64d08dd9 Fix for JRUBY-1963
833606c98d Fix for JRUBY-1823, plus refactoring
6dd78351b4 Fix for instance variable assignment being broken in the compiler. See JRUBY-1666 for a big bug that will address the absence of tests.
ba188df2dc Fix for JRUBY-1486, pass Java arguments when running a compile script on to the script as ARGV Ruby arguments.
1702968064 Fix for JRUBY-1286.
47fc3bf1eb Fix for JRUBY-1468, fixes the original issue, adds a whole raft of tests, and fixes a few additional issues discovered.
880dbd21f9 Remaining fixes in compiler for JRUBY-1446, and enabled a couple tests for it.
5d43a13fe3 Fix for JRUBY-1388, plus an additional fix where it wasn't scoping constants in the right module.
   Bugs after [       3]:
36dd3a0424 Fix JRUBY-4339: Kernel.load with wrap=true does not protect the global namespace of calling program
ffe5c350d2 Fix JRUBY-5225: New method-name-mangling may not be JVM specification compliant
2b166c98a4 Fix for JRUBY-4825: __FILE__ is not expanded when it is used from within a a compiled ruby script used as the main class of an executable jar

Start block index: 246
End block index: 261
    private void beginInit() {
        ClassVisitor cv = getClassVisitor();

        initMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<init>", cg.sig(Void.TYPE), null, null));
        initMethod.start();
        initMethod.aload(THIS);
        initMethod.invokespecial(cg.p(Object.class), "<init>", cg.sig(Void.TYPE));
        
        cv.visitField(ACC_PRIVATE | ACC_FINAL, "$class", cg.ci(Class.class), null, null);
        
        // FIXME: this really ought to be in clinit, but it doesn't matter much
        initMethod.aload(THIS);
        initMethod.ldc(cg.c(classname));
        initMethod.invokestatic(cg.p(Class.class), "forName", cg.sig(Class.class, cg.params(String.class)));
        initMethod.putfield(classname, "$class", cg.ci(Class.class));
    }
