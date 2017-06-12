diff --git a/src/org/jruby/NativeException.java b/src/org/jruby/NativeException.java
index ac8d1bfce7..4d7980381c 100644
--- a/src/org/jruby/NativeException.java
+++ b/src/org/jruby/NativeException.java
@@ -1,89 +1,89 @@
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
 
 import org.jruby.javasupport.JavaObject;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 
 public class NativeException extends RubyException {
 
     private final Throwable cause;
     public static final String CLASS_NAME = "NativeException";
 	private final Ruby runtime;
 
     public NativeException(Ruby runtime, RubyClass rubyClass, Throwable cause) {
         super(runtime, rubyClass, cause.getClass().getName()+": "+cause.getMessage());
 		this.runtime = runtime;
         this.cause = cause;
     }
     
     public static RubyClass createClass(Ruby runtime, RubyClass baseClass) {
         // FIXME: If NativeException is expected to be used from Ruby code, it should provide
         // a real allocator to be used. Otherwise Class.new will fail, as will marshalling. JRUBY-415
     	RubyClass exceptionClass = runtime.defineClass(CLASS_NAME, baseClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
     	
 		CallbackFactory callbackFactory = runtime.callbackFactory(NativeException.class);
 		exceptionClass.defineMethod("cause", 
 				callbackFactory.getMethod("cause"));		
 
 		return exceptionClass;
     }
     
     public IRubyObject cause(Block unusedBlock) {
         return getRuntime().getModule("JavaUtilities").callMethod(getRuntime().getCurrentContext(),
             "wrap",
             JavaObject.wrap(getRuntime(), cause));
     }
     
     public IRubyObject backtrace() {
         IRubyObject rubyTrace = super.backtrace();
         if (rubyTrace.isNil())
             return rubyTrace;
-        RubyArray array = (RubyArray) rubyTrace;
+        RubyArray array = (RubyArray) rubyTrace.dup();
         StackTraceElement[] stackTrace = cause.getStackTrace();
         for (int i=stackTrace.length-1; i>=0; i--) {
             StackTraceElement element = stackTrace[i];
             String line = element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getClassName() + "." + element.getMethodName() + "'";
             RubyString string = runtime.newString(line);
             array.unshift(string);
         }
-        return rubyTrace;
+        return array;
     }
     
     public void printBacktrace(PrintStream errorStream) {
     	super.printBacktrace(errorStream);
     	errorStream.println("Complete Java stackTrace");
     	cause.printStackTrace(errorStream);
     }
 }
diff --git a/test/testJavaExtensions.rb b/test/testJavaExtensions.rb
index 14311b7e69..53b8b081bb 100644
--- a/test/testJavaExtensions.rb
+++ b/test/testJavaExtensions.rb
@@ -1,115 +1,116 @@
 require 'test/minirunit'
 test_check "Java Extensions Support"
 
 if defined? Java
 
   require 'java'
 
   module JavaCollections
     include_class 'java.util.HashMap'
     include_class "java.util.ArrayList"
     include_class "java.util.HashSet"
     include_class "java.lang.Short"
 
 
     def self.testSet
       set = HashSet.new
       set.add(1)
       set.add(2)
       
       newSet = []
       set.each {|x| newSet << x }
       
       test_ok newSet.include?(1)
       test_ok newSet.include?(2)
     end    
 
     def self.testComparable
       one = Short.new(1)
       two = Short.new(2)
       three = Short.new(3)
       list = [ three, two, one]
       list = list.sort
       test_equal([one, two, three], list)
     end    
     
     def self.testMap
       map = HashMap.new
       map.put('A','1');
       map.put('C','3');
       
       hash = Hash.new
       map.each {|key, value| 
         hash[key] = value
       }
       test_equal('1', hash['A'])
       test_equal('3', hash['C'])
     end
     
     def self.testList  
       a = ArrayList.new
       
       a << 3
       a << 1
       a << 2
       
       test_ok([1, 2, 3], a.sort)
       test_ok([1], a.select {|e| e >= 1 })
     end
     
     testSet
     testMap
     testList
     testComparable
   end
   
   module JavaExceptions
     include_class 'org.jruby.test.TestHelper' 
     include_class 'java.lang.RuntimeException' 
     include_class 'java.lang.NullPointerException' 
     include_class 'org.jruby.test.TestHelper$TestHelperException' do |p,c| "THException"; end
     begin
       TestHelper.throwTestHelperException
     rescue THException => e
     end  
   
     begin
       TestHelper.throwTestHelperException
     rescue NullPointerException => e
       test_fail("Should not rescue")
     rescue THException => e
     end  
   
     begin
       TestHelper.throwTestHelperException
     rescue RuntimeException => e
     end  
   
     begin
       TestHelper.throwTestHelperException
     rescue NativeException => e
+      test_equal e.backtrace.size, e.backtrace.size
     end  
   end
   
   module JavaBeans
     BLUE = "blue"
     GREEN = "green"
     
     include_class 'org.jruby.javasupport.test.Color'
       # Java bean convention properties as attributes
     color = Color.new(GREEN)
 
     test_ok !color.isDark
     color.dark = true
     test_ok color.dark
     test_ok color.dark?
 
 
     test_equal GREEN, color.color
 
     color.color = BLUE
     test_equal BLUE, color.color
     
   
   end
 end
