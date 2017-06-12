File path: src/org/jruby/java/MiniJava.java
Comment: / TODO: make this do specific-arity calling
Initial commit id: 70cf5821
Final commit id: 02b8e01b
   Bugs between [      10]:
ba40f9ccdc Fix for JRUBY-3158: Wrong ruby methods called on object of same class from Java code.
27d262b3c5 Spec for already-fixed JRUBY-3262: JRuby 1.1.6 regression: ClassFormatError if Ruby superclass of Ruby class implements super-interface of Java interface
761eabb2fb Fix and specs for JRUBY-2999: Regression: Inheriting method with same name from two Java interfaces causes Java classloader error
a4387186d8 Fix for JRUBY-2993: implementing java class causes exception
5d89d09d77 Fix for JRUBY-2965: Sparodic Linkage Error On Startup
a36d3e4175 Fix JRUBY-2928, the same fix as for equals applied to hashCode and toString
133abfcab6 Fix JRUBY-2927 and enable the test case for it.
343af19a25 Fix for JRUBY-2926. Make equals short circuit to Object.equals for interfaces
0828bab485 Fix for JRUBY-2903, allow interface methods to be implemented with underscore-cased names.
14759c6a52 Fixes and specs for JRUBY-2863, inherited interfaces not getting superinterface methods implemented in Ruby.
   Bugs after [       0]:


Start block index: 501
End block index: 539
    private static void coerceArgumentsToRuby(SkinnyMethodAdapter mv, Class[] paramTypes, String name) {
        // load arguments into IRubyObject[] for dispatch
        if (paramTypes.length != 0) {
            mv.pushInt(paramTypes.length);
            mv.anewarray(p(IRubyObject.class));

            // TODO: make this do specific-arity calling
            for (int i = 0; i < paramTypes.length; i++) {
                mv.dup();
                mv.pushInt(i);
                // convert to IRubyObject
                mv.getstatic(name, "ruby", ci(Ruby.class));
                if (paramTypes[i].isPrimitive()) {
                    if (paramTypes[i] == byte.class || paramTypes[i] == short.class || paramTypes[i] == char.class || paramTypes[i] == int.class) {
                        mv.iload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, int.class));
                    } else if (paramTypes[i] == long.class) {
                        mv.lload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, long.class));
                    } else if (paramTypes[i] == float.class) {
                        mv.fload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, float.class));
                    } else if (paramTypes[i] == double.class) {
                        mv.dload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, double.class));
                    } else if (paramTypes[i] == boolean.class) {
                        mv.iload(i + 1);
                        mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, boolean.class));
                    }
                } else {
                    mv.aload(i + 1);
                    mv.invokestatic(p(JavaUtil.class), "convertJavaToRuby", sig(IRubyObject.class, Ruby.class, Object.class));
                }
                mv.aastore();
            }
        } else {
            mv.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
        }
    }
