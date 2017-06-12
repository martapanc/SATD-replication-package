diff --git a/core/src/main/java/org/jruby/javasupport/binding/ConstantField.java b/core/src/main/java/org/jruby/javasupport/binding/ConstantField.java
deleted file mode 100644
index e3f8e351cf..0000000000
--- a/core/src/main/java/org/jruby/javasupport/binding/ConstantField.java
+++ /dev/null
@@ -1,33 +0,0 @@
-package org.jruby.javasupport.binding;
-
-import org.jruby.RubyModule;
-import org.jruby.javasupport.JavaUtil;
-
-import java.lang.reflect.Field;
-import java.lang.reflect.Modifier;
-
-/**
-* Created by headius on 2/26/15.
-*/
-public class ConstantField {
-    static final int CONSTANT = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;
-    final Field field;
-    public ConstantField(Field field) {
-        this.field = field;
-    }
-    void install(final RubyModule proxy) {
-        if (proxy.getConstantAt(field.getName()) == null) {
-            // TODO: catch exception if constant is already set by other
-            // thread
-            try {
-                proxy.setConstant(field.getName(), JavaUtil.convertJavaToUsableRubyObject(proxy.getRuntime(), field.get(null)));
-            } catch (IllegalAccessException iae) {
-                // if we can't read it, we don't set it
-            }
-        }
-    }
-    static boolean isConstant(final Field field) {
-        return (field.getModifiers() & CONSTANT) == CONSTANT &&
-            Character.isUpperCase(field.getName().charAt(0));
-    }
-}
