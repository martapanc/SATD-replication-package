File path: src/org/jruby/compiler/impl/StandardASMCompiler.java
Comment: / TODO need to abstract this setup behind another compiler interface
Initial commit id: 4934ed56
Final commit id: 4fa52b39
   Bugs between [       2]:
ffe5c350d2 Fix JRUBY-5225: New method-name-mangling may not be JVM specification compliant
2b166c98a4 Fix for JRUBY-4825: __FILE__ is not expanded when it is used from within a a compiled ruby script used as the main class of an executable jar
   Bugs after [       1]:
36dd3a0424 Fix JRUBY-4339: Kernel.load with wrap=true does not protect the global namespace of calling program

Start block index: 424
End block index: 443
    private void beginClassInit() {
        ClassVisitor cv = getClassVisitor();

        clinitMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", sig(Void.TYPE), null, null));
        clinitMethod.start();

        if (invDynSupportInstaller != null) {
            // install invokedynamic bootstrapper
            // TODO need to abstract this setup behind another compiler interface
            try {
                invDynSupportInstaller.invoke(null, clinitMethod, classname);
            } catch (IllegalAccessException ex) {
                // ignore; we won't use invokedynamic
            } catch (IllegalArgumentException ex) {
                // ignore; we won't use invokedynamic
            } catch (InvocationTargetException ex) {
                // ignore; we won't use invokedynamic
            }
        }
    }
