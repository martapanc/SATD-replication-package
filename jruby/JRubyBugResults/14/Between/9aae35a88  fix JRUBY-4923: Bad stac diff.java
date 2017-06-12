diff --git a/spec/java_integration/exceptions/rescue_spec.rb b/spec/java_integration/exceptions/rescue_spec.rb
index f0680dabe7..5a0ed0a06a 100644
--- a/spec/java_integration/exceptions/rescue_spec.rb
+++ b/spec/java_integration/exceptions/rescue_spec.rb
@@ -1,26 +1,37 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 import java.lang.OutOfMemoryError
+import "java_integration.fixtures.ThrowExceptionInInitializer"
 
 describe "Non-wrapped Java throwables" do
   it "can be rescued" do
     exception = OutOfMemoryError.new
     begin
       raise exception
     rescue OutOfMemoryError => oome
     end
     
     oome.should_not == nil
     oome.should == exception
   end
 end
 
 describe "A Ruby-level exception" do
   it "carries its message along to the Java exception" do
     java_ex = JRuby.runtime.new_runtime_error("error message");
     java_ex.message.should == "error message"
 
     java_ex = JRuby.runtime.new_name_error("error message", "name");
     java_ex.message.should == "error message"
   end
+end
+
+describe "A native exception wrapped by another" do  
+  it "gets the first available message from the causes' chain" do
+    begin
+      ThrowExceptionInInitializer.new.test
+    rescue NativeException => e
+      e.message.should =~ /lets cause an init exception$/
+    end
+  end
 end
\ No newline at end of file
diff --git a/spec/java_integration/fixtures/ThrowExceptionInInitializer.java b/spec/java_integration/fixtures/ThrowExceptionInInitializer.java
new file mode 100644
index 0000000000..092a7f6a6c
--- /dev/null
+++ b/spec/java_integration/fixtures/ThrowExceptionInInitializer.java
@@ -0,0 +1,15 @@
+package java_integration.fixtures;
+
+public class ThrowExceptionInInitializer {
+    public void test() {
+        new ThrowExceptionInStatic();
+    }
+}
+
+class ThrowExceptionInStatic  {
+    static {
+        if (1 == (1 + 0)) {
+          throw new RuntimeException("lets cause an init exception");
+        }
+    }
+}
\ No newline at end of file
diff --git a/src/org/jruby/NativeException.java b/src/org/jruby/NativeException.java
index dd0ad4a720..d3bf6fd9c9 100644
--- a/src/org/jruby/NativeException.java
+++ b/src/org/jruby/NativeException.java
@@ -1,150 +1,160 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.PrintStream;
 
 import java.lang.reflect.Member;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.Java;
-import org.jruby.javasupport.JavaObject;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
-import org.jruby.util.SafePropertyAccessor;
 
 @JRubyClass(name = "NativeException", parent = "RuntimeError")
 public class NativeException extends RubyException {
 
     private final Throwable cause;
     public static final String CLASS_NAME = "NativeException";
     private final Ruby runtime;
 
     public NativeException(Ruby runtime, RubyClass rubyClass, Throwable cause) {
-        super(runtime, rubyClass, cause.getClass().getName() + ": " + cause.getMessage());
+        super(runtime, rubyClass);
         this.runtime = runtime;
         this.cause = cause;
+        this.message = runtime.newString(cause.getClass().getName() + ": " + searchStackMessage(cause));
     }
 
     public static RubyClass createClass(Ruby runtime, RubyClass baseClass) {
         // FIXME: If NativeException is expected to be used from Ruby code, it should provide
         // a real allocator to be used. Otherwise Class.new will fail, as will marshalling. JRUBY-415
         RubyClass exceptionClass = runtime.defineClass(CLASS_NAME, baseClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         exceptionClass.defineAnnotatedMethods(NativeException.class);
 
         return exceptionClass;
     }
 
     @JRubyMethod(frame = true)
     public IRubyObject cause(Block unusedBlock) {
         return Java.getInstance(getRuntime(), cause);
     }
 
     public IRubyObject backtrace() {
         IRubyObject rubyTrace = super.backtrace();
         if (rubyTrace.isNil()) {
             return rubyTrace;
         }
         RubyArray array = (RubyArray) rubyTrace.dup();
         StackTraceElement[] stackTrace = cause.getStackTrace();
         for (int i = stackTrace.length - 1; i >= 0; i--) {
             StackTraceElement element = stackTrace[i];
             String className = element.getClassName();
             String line = null;
             if (element.getFileName() == null) {
                 line = className + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'";
             } else {
                 int index = className.lastIndexOf(".");
                 String packageName = null;
                 if (index == -1) {
                     packageName = "";
                 } else {
                     packageName = className.substring(0, index) + "/";
                 }
                 line = packageName.replace(".", "/") + element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'";
             }
             RubyString string = runtime.newString(line);
             array.unshift(string);
         }
         return array;
     }
 
     public void trimStackTrace(Member target) {
         Throwable t = new Throwable();
         StackTraceElement[] origStackTrace = cause.getStackTrace();
         StackTraceElement[] currentStackTrace = t.getStackTrace();
         int skip = 0;
         for (int i = 1;
                 i <= origStackTrace.length && i <= currentStackTrace.length;
                 ++i) {
             StackTraceElement a = origStackTrace[origStackTrace.length - i];
             StackTraceElement b = currentStackTrace[currentStackTrace.length - i];
             if (a.equals(b)) {
                 skip += 1;
             } else {
                 break;
             }
         }
         // If we know what method was being called, strip everything
         // before the call. This hides the JRuby and reflection internals.
         if (target != null) {
             String className = target.getDeclaringClass().getName();
             String methodName = target.getName();
             for (int i = origStackTrace.length - skip - 1; i >= 0; --i) {
                 StackTraceElement frame = origStackTrace[i];
                 if (frame.getClassName().equals(className) &&
                     frame.getMethodName().equals(methodName)) {
                     skip = origStackTrace.length - i - 1;
                     break;
                 }
             }
         }
         if (skip > 0) {
             StackTraceElement[] newStackTrace =
                     new StackTraceElement[origStackTrace.length - skip];
             for (int i = 0; i < newStackTrace.length; ++i) {
                 newStackTrace[i] = origStackTrace[i];
             }
             cause.setStackTrace(newStackTrace);
         }
     }
 
     public void printBacktrace(PrintStream errorStream) {
         super.printBacktrace(errorStream);
         if (getRuntime().getDebug().isTrue()) {
             errorStream.println("Complete Java stackTrace");
             cause.printStackTrace(errorStream);
         }
     }
 
     public Throwable getCause() {
         return cause;
     }
+
+    private String searchStackMessage(Throwable cause) {
+        String message = null;
+
+        do {
+            message = cause.getMessage();
+            cause = cause.getCause();
+        } while (message == null && cause != null);
+
+        return message;
+    }
 }
