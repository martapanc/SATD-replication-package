    private void beginInit() {
        ClassVisitor cv = getClassVisitor();

        initMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<init>", sig(Void.TYPE), null, null));
        initMethod.start();
        initMethod.aload(THIS);
        initMethod.invokespecial(p(AbstractScript.class), "<init>", sig(Void.TYPE));
        
        // JRUBY-3014: make __FILE__ dynamically determined at load time, but
        // we provide a reasonable default here
        initMethod.aload(THIS);
        initMethod.ldc(getSourcename());
        initMethod.putfield(getClassname(), "filename", ci(String.class));
    }
