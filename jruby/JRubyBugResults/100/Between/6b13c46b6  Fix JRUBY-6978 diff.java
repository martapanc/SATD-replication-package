diff --git a/spec/java_integration/exceptions/rescue_spec.rb b/spec/java_integration/exceptions/rescue_spec.rb
index 4b0570fe23..17b5c74c8e 100644
--- a/spec/java_integration/exceptions/rescue_spec.rb
+++ b/spec/java_integration/exceptions/rescue_spec.rb
@@ -1,119 +1,152 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 
 java_import java.lang.OutOfMemoryError
 java_import "java_integration.fixtures.ThrowExceptionInInitializer"
 java_import "java_integration.fixtures.ExceptionRunner"
 
-describe "Non-wrapped Java throwables" do
-  it "can be rescued" do
+describe "A non-wrapped Java throwable" do
+  it "can be rescued using the Java type" do
     exception = OutOfMemoryError.new
     begin
       raise exception
     rescue OutOfMemoryError => oome
     end
     
     oome.should_not == nil
     oome.should == exception
   end
+
+  it "can be rescued using Object" do
+    begin
+      raise java.lang.NullPointerException.new
+    rescue Object => e
+      e.should be_kind_of(java.lang.NullPointerException)
+    end
+  end
+
+  it "can be rescued using Exception" do
+    begin
+      raise java.lang.NullPointerException.new
+    rescue Exception => e
+      e.should be_kind_of(java.lang.NullPointerException)
+    end
+  end
+
+  it "can be rescued using StandardError" do
+    begin
+      raise java.lang.NullPointerException.new
+    rescue StandardError => e
+      e.should be_kind_of(java.lang.NullPointerException)
+    end
+  end
+
+  it "can be rescued inline" do
+    obj = Object.new
+    def obj.go
+      raise java.lang.NullPointerException.new
+    end
+
+    (obj.go rescue 'foo').should == 'foo'
+  end
 end
 
 describe "A Ruby-level exception" do
   it "carries its message along to the Java exception" do
     java_ex = JRuby.runtime.new_runtime_error("error message");
     java_ex.message.should == "(RuntimeError) error message"
 
     java_ex = JRuby.runtime.new_name_error("error message", "name");
     java_ex.message.should == "(NameError) error message"
   end
 end
 
 describe "A native exception wrapped by another" do  
   it "gets the first available message from the causes' chain" do
     begin
       ThrowExceptionInInitializer.new.test
     rescue NativeException => e
       e.message.should =~ /lets cause an init exception$/
     end
   end
 
   it "can be re-raised" do
     lambda {
       begin
         ThrowExceptionInInitializer.new.test
       rescue NativeException => e
         raise e.exception("re-raised")
       end
     }.should raise_error(NativeException)
   end
 end
 
 describe "A Ruby subclass of a Java exception" do
   before :all do
     @ex_class = Class.new(java.lang.RuntimeException)
   end
 
   it "is rescuable with all Java superclasses" do
     exception = @ex_class.new
 
     begin
       raise exception
       fail
     rescue java.lang.Throwable
       $!.should == exception
     end
 
     begin
       raise exception
       fail
     rescue java.lang.Exception
       $!.should == exception
     end
 
     begin
       raise exception
       fail
     rescue java.lang.RuntimeException
       $!.should == exception
     end
   end
 
   it "presents its Ruby nature when rescued" do
     exception = @ex_class.new
 
     begin
       raise exception
       fail
     rescue java.lang.Throwable => t
       t.class.should == @ex_class
       t.should equal(exception)
     end
   end
 end
 
 describe "A ruby exception raised through java and back to ruby" do
   it "its class and message is preserved" do
     begin
       ExceptionRunner.new.do_it_now do
         raise "it comes from ruby"
       end
       fail
     rescue RuntimeError => e
       e.message.should == "it comes from ruby"
     end
   end
 end
 
 describe "A ruby exception raised through java and back " +
          "to ruby via a different thread" do
   it "its class and message is preserved" do
     begin
       ExceptionRunner.new.do_it_threaded do
         raise "it comes from ruby"
       end
       fail
     rescue RuntimeError => e
       e.message.should == "it comes from ruby"
     end
   end
 end
 
diff --git a/src/org/jruby/RubyException.java b/src/org/jruby/RubyException.java
index b957850990..8301af0a18 100644
--- a/src/org/jruby/RubyException.java
+++ b/src/org/jruby/RubyException.java
@@ -1,376 +1,378 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sf.net>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
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
 
 import org.jruby.runtime.backtrace.BacktraceData;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.util.List;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.JumpException.FlowControlException;
 import org.jruby.java.proxies.ConcreteJavaProxy;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.backtrace.RubyStackTraceElement;
 import org.jruby.runtime.backtrace.TraceType;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Exception")
 public class RubyException extends RubyObject {
     protected RubyException(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, null);
     }
 
     public RubyException(Ruby runtime, RubyClass rubyClass, String message) {
         super(runtime, rubyClass);
         
         this.message = message == null ? runtime.getNil() : runtime.newString(message);
     }
 
     @JRubyMethod(optional = 2, visibility = PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length == 1) message = args[0];
         return this;
     }
 
     @JRubyMethod
     public IRubyObject backtrace() {
         IRubyObject bt = getBacktrace();
         return bt;
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject set_backtrace(IRubyObject obj) {
         if (obj.isNil()) {
             backtrace = null;
         } else if (!isArrayOfStrings(obj)) {
             throw getRuntime().newTypeError("backtrace must be Array of String");
         } else {
             backtrace = obj;
         }
         return backtrace();
     }
 
     @JRubyMethod(name = "exception", optional = 1, rest = true, meta = true)
     public static IRubyObject exception(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return ((RubyClass) recv).newInstance(context, args, block);
     }
 
     @JRubyMethod(optional = 1)
     public RubyException exception(IRubyObject[] args) {
         switch (args.length) {
             case 0 :
                 return this;
             case 1 :
                 if(args[0] == this) {
                     return this;
                 }
                 RubyException ret = (RubyException)rbClone();
                 ret.initialize(args, Block.NULL_BLOCK); // This looks wrong, but it's the way MRI does it.
                 return ret;
             default :
                 throw getRuntime().newArgumentError("Wrong argument count");
         }
     }
 
     @JRubyMethod(name = "to_s", compat = CompatVersion.RUBY1_8)
     public IRubyObject to_s(ThreadContext context) {
         if (message.isNil()) {
             return context.runtime.newString(getMetaClass().getRealClass().getName());
         }
         message.setTaint(isTaint());
         return message;
     }
 
     @JRubyMethod(name = "to_s", compat = CompatVersion.RUBY1_9)
     public IRubyObject to_s19(ThreadContext context) {
         if (message.isNil()) {
             return context.runtime.newString(getMetaClass().getRealClass().getName());
         }
         message.setTaint(isTaint());
         return message.asString();
     }
 
     @JRubyMethod(name = "to_str", compat = CompatVersion.RUBY1_8)
     public IRubyObject to_str(ThreadContext context) {
         return callMethod(context, "to_s");
     }
 
     @JRubyMethod(name = "message")
     public IRubyObject message(ThreadContext context) {
         return callMethod(context, "to_s");
     }
 
     /** inspects an object and return a kind of debug information
      *
      *@return A RubyString containing the debug information.
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect(ThreadContext context) {
         // rb_class_name skips intermediate classes (JRUBY-6786)
         RubyModule rubyClass = getMetaClass().getRealClass();
         RubyString exception = RubyString.objAsString(context, this);
 
         if (exception.size() == 0) return getRuntime().newString(rubyClass.getName());
         StringBuilder sb = new StringBuilder("#<");
         sb.append(rubyClass.getName()).append(": ").append(exception.getByteList()).append(">");
         return getRuntime().newString(sb.toString());
     }
 
     @JRubyMethod(name = "==", compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         if (this == other) return context.runtime.getTrue();
 
         boolean equal =
                 getMetaClass().getRealClass() == other.getMetaClass().getRealClass() &&
                         context.runtime.getException().isInstance(other) &&
                         callMethod(context, "message").equals(other.callMethod(context, "message")) &&
                         callMethod(context, "backtrace").equals(other.callMethod(context, "backtrace"));
         return context.runtime.newBoolean(equal);
     }
 
     @JRubyMethod(name = "===", meta = true)
     public static IRubyObject op_eqq(ThreadContext context, IRubyObject recv, IRubyObject other) {
         Ruby runtime = context.runtime;
         // special case non-FlowControlException Java exceptions so they'll be caught by rescue Exception
-        if (recv == runtime.getException() && other instanceof ConcreteJavaProxy) {
+        if (other instanceof ConcreteJavaProxy &&
+                (recv == runtime.getException() || recv == runtime.getStandardError())) {
+
             Object object = ((ConcreteJavaProxy)other).getObject();
             if (object instanceof Throwable && !(object instanceof FlowControlException)) {
                 return context.runtime.getTrue();
             }
         }
         // fall back on default logic
         return ((RubyClass)recv).op_eqq(context, other);
     }
 
     public void setBacktraceData(BacktraceData backtraceData) {
         this.backtraceData = backtraceData;
     }
 
     public BacktraceData getBacktraceData() {
         return backtraceData;
     }
 
     public RubyStackTraceElement[] getBacktraceElements() {
         if (backtraceData == null) {
             return RubyStackTraceElement.EMPTY_ARRAY;
         }
         return backtraceData.getBacktrace(getRuntime());
     }
 
     public void prepareBacktrace(ThreadContext context, boolean nativeException) {
         // if it's null, build a backtrace
         if (backtraceData == null) {
             backtraceData = context.runtime.getInstanceConfig().getTraceType().getBacktrace(context, nativeException);
         }
     }
 
     /**
      * Prepare an "integrated" backtrace that includes the normal Ruby trace plus non-filtered Java frames. Used by
      * Java integration to show the Java frames for a JI-called method.
      *
      * @param context
      * @param javaTrace
      */
     public void prepareIntegratedBacktrace(ThreadContext context, StackTraceElement[] javaTrace) {
         // if it's null, build a backtrace
         if (backtraceData == null) {
             backtraceData = context.runtime.getInstanceConfig().getTraceType().getIntegratedBacktrace(context, javaTrace);
         }
     }
 
     public void forceBacktrace(IRubyObject backtrace) {
         backtraceData = BacktraceData.EMPTY;
         set_backtrace(backtrace);
     }
     
     public IRubyObject getBacktrace() {
         if (backtrace == null) {
             initBacktrace();
         }
         return backtrace;
     }
     
     public void initBacktrace() {
         Ruby runtime = getRuntime();
         if (backtraceData == null) {
             backtrace = runtime.getNil();
         } else {
             backtrace = TraceType.generateMRIBacktrace(runtime, backtraceData.getBacktrace(runtime));
         }
     }
 
     @Override
     public void copySpecialInstanceVariables(IRubyObject clone) {
         RubyException exception = (RubyException)clone;
         exception.backtraceData = backtraceData;
         exception.backtrace = backtrace;
         exception.message = message;
     }
 
     /**
      * Print the Ruby exception's backtrace to the given PrintStream.
      *
      * @param errorStream the PrintStream to which backtrace should be printed
      */
     public void printBacktrace(PrintStream errorStream) {
         printBacktrace(errorStream, 0);
     }
 
     /**
      * Print the Ruby exception's backtrace to the given PrintStream. This
      * version accepts a number of lines to skip and is primarily used
      * internally for exception printing where the first line is treated specially.
      *
      * @param errorStream the PrintStream to which backtrace should be printed
      */
     public void printBacktrace(PrintStream errorStream, int skip) {
         IRubyObject backtrace = callMethod(getRuntime().getCurrentContext(), "backtrace");
         if (!backtrace.isNil() && backtrace instanceof RubyArray) {
             IRubyObject[] elements = backtrace.convertToArray().toJavaArray();
 
             for (int i = skip; i < elements.length; i++) {
                 IRubyObject stackTraceLine = elements[i];
                 if (stackTraceLine instanceof RubyString) {
                     printStackTraceLine(errorStream, stackTraceLine);
                 }
             }
         }
     }
 
     private void printStackTraceLine(PrintStream errorStream, IRubyObject stackTraceLine) {
             errorStream.print("\tfrom " + stackTraceLine + '\n');
     }
 	
     private boolean isArrayOfStrings(IRubyObject backtrace) {
         if (!(backtrace instanceof RubyArray)) return false; 
             
         IRubyObject[] elements = ((RubyArray) backtrace).toJavaArray();
         
         for (int i = 0 ; i < elements.length ; i++) {
             if (!(elements[i] instanceof RubyString)) return false;
         }
             
         return true;
     }
 
     public static ObjectAllocator EXCEPTION_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyException instance = new RubyException(runtime, klass);
 
             // for future compatibility as constructors move toward not accepting metaclass?
             instance.setMetaClass(klass);
 
             return instance;
         }
     };
 
     private static final ObjectMarshal EXCEPTION_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             RubyException exc = (RubyException)obj;
 
             marshalStream.registerLinkTarget(exc);
             List<Variable<Object>> attrs = exc.getVariableList();
             attrs.add(new VariableEntry<Object>(
                     "mesg", exc.message == null ? runtime.getNil() : exc.message));
             attrs.add(new VariableEntry<Object>("bt", exc.getBacktrace()));
             marshalStream.dumpVariables(attrs);
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             RubyException exc = (RubyException)type.allocate();
 
             unmarshalStream.registerLinkTarget(exc);
             // FIXME: Can't just pull these off the wire directly? Or maybe we should
             // just use real vars all the time for these?
             unmarshalStream.defaultVariablesUnmarshal(exc);
 
             exc.message = (IRubyObject)exc.removeInternalVariable("mesg");
             exc.set_backtrace((IRubyObject)exc.removeInternalVariable("bt"));
 
             return exc;
         }
     };
 
     public static RubyClass createExceptionClass(Ruby runtime) {
         RubyClass exceptionClass = runtime.defineClass("Exception", runtime.getObject(), EXCEPTION_ALLOCATOR);
         runtime.setException(exceptionClass);
 
         exceptionClass.index = ClassIndex.EXCEPTION;
         exceptionClass.setReifiedClass(RubyException.class);
 
         exceptionClass.setMarshal(EXCEPTION_MARSHAL);
         exceptionClass.defineAnnotatedMethods(RubyException.class);
 
         return exceptionClass;
     }
 
     public static RubyException newException(Ruby runtime, RubyClass excptnClass, String msg) {
         return new RubyException(runtime, excptnClass, msg);
     }
 
     // rb_exc_new3
     public static IRubyObject newException(ThreadContext context, RubyClass exceptionClass, IRubyObject message) {
         return exceptionClass.callMethod(context, "new", message.convertToString());
     }
 
     private BacktraceData backtraceData;
     private IRubyObject backtrace;
     public IRubyObject message;
 
     public static final int TRACE_HEAD = 8;
     public static final int TRACE_TAIL = 4;
     public static final int TRACE_MAX = TRACE_HEAD + TRACE_TAIL + 6;
 
 }
diff --git a/src/org/jruby/ast/RescueNode.java b/src/org/jruby/ast/RescueNode.java
index 22c955d6e1..c9e135e17d 100644
--- a/src/org/jruby/ast/RescueNode.java
+++ b/src/org/jruby/ast/RescueNode.java
@@ -1,242 +1,238 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.ast;
 
 import java.util.Arrays;
 import java.util.List;
 
 import org.jruby.NativeException;
 import org.jruby.Ruby;
 import org.jruby.RubyException;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.unsafe.UnsafeFactory;
 
 /**
  * Represents a rescue statement
  */
 public class RescueNode extends Node {
     private final Node bodyNode;
     private final RescueBodyNode rescueNode;
     private final Node elseNode;
     
     public RescueNode(ISourcePosition position, Node bodyNode, RescueBodyNode rescueNode, Node elseNode) {
         super(position);
         this.bodyNode = bodyNode;
         this.rescueNode = rescueNode;
         this.elseNode = elseNode;
     }
 
     public NodeType getNodeType() {
         return NodeType.RESCUENODE;
     }
 
     /**
      * Accept for the visitor pattern.
      * @param iVisitor the visitor
      **/
     public Object accept(NodeVisitor iVisitor) {
         return iVisitor.visitRescueNode(this);
     }
 
     /**
      * Gets the bodyNode.
      * @return Returns a Node
      */
     public Node getBodyNode() {
         return bodyNode;
     }
 
     /**
      * Gets the elseNode.
      * @return Returns a Node
      */
     public Node getElseNode() {
         return elseNode;
     }
 
     /**
      * Gets the first rescueNode.
      * @return Returns a Node
      */
     public RescueBodyNode getRescueNode() {
         return rescueNode;
     }
     
     public List<Node> childNodes() {
         return Node.createList(rescueNode, bodyNode, elseNode);
     }
     
     @Override
     public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
-        return interpretWithJavaExceptions(runtime, context, self, aBlock);
-    }
-
-    private IRubyObject interpretWithJavaExceptions(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         IRubyObject result = null;
         boolean exceptionRaised = false;
         RescuedBlock : while (true) {
             IRubyObject globalExceptionState = runtime.getGlobalVariables().get("$!");
             boolean anotherExceptionRaised = false;
             try {
                 result = executeBody(runtime, context, self, aBlock);
                 break;
             } catch (RaiseException raiseJump) {
                 try {
                     result = handleException(runtime, context, self, aBlock, raiseJump);
                     exceptionRaised = true;
                     break;
                 } catch (JumpException.RetryJump rj) {
                     // let RescuedBlock continue
                 } catch (RaiseException je) {
                     anotherExceptionRaised = true;
                     throw je;
                 }
             } catch (JumpException.FlowControlException flow) {
                 // just rethrow
                 throw flow;
             } catch (Throwable t) {
                 if (t instanceof Unrescuable) {
                     UnsafeFactory.getUnsafe().throwException(t);
                 }
                 try {
                     result = handleJavaException(runtime, context, self, aBlock, t);
                     exceptionRaised = true;
                     break;
                 } catch (JumpException.RetryJump rj) {
                     continue RescuedBlock;
                 } catch (RaiseException je) {
                     anotherExceptionRaised = true;
                     throw je;
                 }
             } finally {
                 // clear exception when handled or retried
                 if (!anotherExceptionRaised) {
                     runtime.getGlobalVariables().set("$!", globalExceptionState);
                 }
             }
         }
 
         // If no exception is thrown execute else block
         if (elseNode != null && !exceptionRaised) {
             if (rescueNode == null) {
                 runtime.getWarnings().warn(ID.ELSE_WITHOUT_RESCUE, elseNode.getPosition(), "else without rescue is useless");
             }
             result = elseNode.interpret(runtime, context, self, aBlock);
         }
         return result;
     }
 
     private IRubyObject handleException(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock, RaiseException raiseJump) {
         RubyException raisedException = raiseJump.getException();
         RescueBodyNode cRescueNode = rescueNode;
 
         while (cRescueNode != null) {
             IRubyObject[] exceptions = getExceptions(cRescueNode, runtime, context, self, aBlock);
 
             if (RuntimeHelpers.isExceptionHandled(raisedException, exceptions, context).isTrue()) {
                 runtime.getGlobalVariables().set("$!", raisedException);
 
                 return cRescueNode.interpret(runtime,context, self, aBlock);
             }
 
             cRescueNode = cRescueNode.getOptRescueNode();
         }
 
         // no takers; bubble up
         throw raiseJump;
     }
     
     private IRubyObject handleJavaException(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock, Throwable throwable) {
         RescueBodyNode cRescueNode = rescueNode;
 
         while (cRescueNode != null) {
             IRubyObject[] exceptions = getExceptions(cRescueNode, runtime, context, self, aBlock);
 
             if (RuntimeHelpers.isJavaExceptionHandled(throwable, exceptions, context).isTrue()) {
                 IRubyObject exceptionObj;
 
                 if (exceptions.length == 1 && exceptions[0] == runtime.getNativeException()) {
                     // wrap Throwable in a NativeException object
                     exceptionObj = new NativeException(runtime, runtime.getNativeException(), throwable);
                     ((NativeException)exceptionObj).prepareIntegratedBacktrace(context, throwable.getStackTrace());
                 } else {
                     // wrap as normal JI object
                     exceptionObj = JavaUtil.convertJavaToUsableRubyObject(runtime, throwable);
                 }
 
                 runtime.getGlobalVariables().set("$!", exceptionObj);
 
                 return cRescueNode.interpret(runtime, context, self, aBlock);
             }
 
             cRescueNode = cRescueNode.getOptRescueNode();
         }
 
         // no takers; bubble up
         UnsafeFactory.getUnsafe().throwException(throwable);
         throw new RuntimeException("Unsafe.throwException failed");
     }
 
     private IRubyObject executeBody(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         // FIXME: Make bodyNode non-null in parser
         if (bodyNode == null) {
             return runtime.getNil();
         }
         // Execute rescue block
         return bodyNode.interpret(runtime, context, self, aBlock);
     }
 
     private IRubyObject[] getExceptions(RescueBodyNode cRescueNode, Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
         Node exceptionNodes = cRescueNode.getExceptionNodes();
         IRubyObject[] exceptions;
 
         if (exceptionNodes == null) {
             exceptions = new IRubyObject[]{runtime.getStandardError()};
         } else {
             exceptions = ASTInterpreter.setupArgs(runtime, context, exceptionNodes, self, aBlock);
         }
         return exceptions;
     }
 }
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index 2e953726f1..569ca7da78 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -6,2038 +6,2046 @@ import org.jruby.ast.ArgumentNode;
 import org.jruby.ast.DAsgnNode;
 import org.jruby.ast.DSymbolNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.LiteralNode;
 import org.jruby.ast.LocalAsgnNode;
 import org.jruby.ast.MultipleAsgn19Node;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.OptArgNode;
 import org.jruby.ast.UnnamedRestArgNode;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.CompiledIRMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.ir.operands.UndefinedValue;
 import org.jruby.javasupport.JavaClass;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SimpleSourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlock19;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockCallback19;
 import org.jruby.runtime.CompiledBlockLight;
 import org.jruby.runtime.CompiledBlockLight19;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Interpreted19Block;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.invokedynamic.MethodNames;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.unsafe.UnsafeFactory;
 
 import java.util.ArrayList;
 import java.util.StringTokenizer;
 
 import static org.jruby.runtime.invokedynamic.MethodNames.OP_EQUAL;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class RuntimeHelpers {
     public static CallSite selectAttrAsgnCallSite(IRubyObject receiver, IRubyObject self, CallSite normalSite, CallSite variableSite) {
         if (receiver == self) return variableSite;
         return normalSite;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, arg1, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, IRubyObject value, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, arg0, arg1, arg2, value);
         return value;
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, ThreadContext context, IRubyObject caller) {
         callSite.call(context, caller, receiver, args);
         return args[args.length - 1];
     }
     public static IRubyObject doAttrAsgn(IRubyObject receiver, CallSite callSite, IRubyObject[] args, IRubyObject value, ThreadContext context, IRubyObject caller) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         callSite.call(context, caller, receiver, newArgs);
         return value;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject[] receivers) {
         for (int i = 0; i < receivers.length; i++) {
             IRubyObject receiver = receivers[i];
             if (invokeEqqForCaseWhen(callSite, context, caller, arg, receiver)) return true;
         }
         return false;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver) {
         IRubyObject result = callSite.call(context, caller, receiver, arg);
         if (result.isTrue()) return true;
         return false;
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver0, IRubyObject receiver1) {
         IRubyObject result = callSite.call(context, caller, receiver0, arg);
         if (result.isTrue()) return true;
         return invokeEqqForCaseWhen(callSite, context, caller, arg, receiver1);
     }
 
     public static boolean invokeEqqForCaseWhen(CallSite callSite, ThreadContext context, IRubyObject caller, IRubyObject arg, IRubyObject receiver0, IRubyObject receiver1, IRubyObject receiver2) {
         IRubyObject result = callSite.call(context, caller, receiver0, arg);
         if (result.isTrue()) return true;
         return invokeEqqForCaseWhen(callSite, context, caller, arg, receiver1, receiver2);
     }
 
     public static boolean areAnyTrueForCaselessWhen(IRubyObject[] receivers) {
         for (int i = 0; i < receivers.length; i++) {
             if (receivers[i].isTrue()) return true;
         }
         return false;
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver) {
         return receiver.isTrue();
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver0, IRubyObject receiver1) {
         return receiver0.isTrue() || receiver1.isTrue();
     }
 
     public static boolean invokeEqqForCaselessWhen(IRubyObject receiver0, IRubyObject receiver1, IRubyObject receiver2) {
         return receiver0.isTrue() || receiver1.isTrue() || receiver2.isTrue();
     }
     
     public static CompiledBlockCallback createBlockCallback(Object scriptObject, String closureMethod, String file, int line) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         MethodFactory factory = MethodFactory.createFactory(scriptClassLoader);
         
         return factory.getBlockCallback(closureMethod, file, line, scriptObject);
     }
 
     public static CompiledBlockCallback19 createBlockCallback19(Object scriptObject, String closureMethod, String file, int line) {
         Class scriptClass = scriptObject.getClass();
         ClassLoader scriptClassLoader = scriptClass.getClassLoader();
         MethodFactory factory = MethodFactory.createFactory(scriptClassLoader);
 
         return factory.getBlockCallback19(closureMethod, file, line, scriptObject);
     }
 
     public static byte[] createBlockCallbackOffline(String classPath, String closureMethod, String file, int line) {
         MethodFactory factory = MethodFactory.createFactory(RuntimeHelpers.class.getClassLoader());
 
         return factory.getBlockCallbackOffline(closureMethod, file, line, classPath);
     }
 
     public static byte[] createBlockCallback19Offline(String classPath, String closureMethod, String file, int line) {
         MethodFactory factory = MethodFactory.createFactory(RuntimeHelpers.class.getClassLoader());
 
         return factory.getBlockCallback19Offline(closureMethod, file, line, classPath);
     }
 
     public static String buildBlockDescriptor19(
             String closureMethod,
             int arity,
             StaticScope scope,
             String file,
             int line,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             String parameterList,
             ASTInspector inspector) {
         return buildBlockDescriptor(closureMethod, arity, scope, file, line, hasMultipleArgsHead, argsNodeId, inspector) +
                 "," + parameterList;
     }
 
     public static String buildBlockDescriptor(
             String closureMethod,
             int arity,
             StaticScope scope,
             String file,
             int line,
             boolean hasMultipleArgsHead,
             NodeType argsNodeId,
             ASTInspector inspector) {
         
         // build scope names string
         StringBuffer scopeNames = new StringBuffer();
         for (int i = 0; i < scope.getVariables().length; i++) {
             if (i != 0) scopeNames.append(';');
             scopeNames.append(scope.getVariables()[i]);
         }
 
         // build descriptor string
         String descriptor =
                 closureMethod + ',' +
                 arity + ',' +
                 scopeNames + ',' +
                 hasMultipleArgsHead + ',' +
                 BlockBody.asArgumentType(argsNodeId) + ',' +
                 file + ',' +
                 line + ',' +
                 !(inspector.hasClosure() || inspector.hasScopeAwareMethods());
 
         return descriptor;
     }
     
     public static String[][] parseBlockDescriptor(String descriptor) {
         String[] firstSplit = descriptor.split(",");
         String[] secondSplit;
         if (firstSplit[2].length() == 0) {
             secondSplit = new String[0];
         } else {
             secondSplit = firstSplit[2].split(";");
             // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
             for (int i = 0; i < secondSplit.length; i++) {
                 secondSplit[i] = secondSplit[i].intern();
             }
         }
         return new String[][] {firstSplit, secondSplit};
     }
 
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String descriptor) {
         String[][] splitDesc = parseBlockDescriptor(descriptor);
         String[] firstSplit = splitDesc[0];
         String[] secondSplit = splitDesc[1];
         return createCompiledBlockBody(context, scriptObject, firstSplit[0], Integer.parseInt(firstSplit[1]), secondSplit, Boolean.valueOf(firstSplit[3]), Integer.parseInt(firstSplit[4]), firstSplit[5], Integer.parseInt(firstSplit[6]), Boolean.valueOf(firstSplit[7]));
     }
     
     public static BlockBody createCompiledBlockBody(ThreadContext context, Object scriptObject, String closureMethod, int arity, 
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, String file, int line, boolean light) {
         StaticScope staticScope = context.runtime.getStaticScopeFactory().newBlockScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         if (light) {
             return CompiledBlockLight.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType);
         } else {
             return CompiledBlock.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType);
         }
     }
 
     public static BlockBody createCompiledBlockBody19(ThreadContext context, Object scriptObject, String descriptor) {
         String[][] splitDesc = parseBlockDescriptor(descriptor);
         String[] firstSplit = splitDesc[0];
         String[] secondSplit = splitDesc[1];
         return createCompiledBlockBody19(context, scriptObject, firstSplit[0], Integer.parseInt(firstSplit[1]), secondSplit, Boolean.valueOf(firstSplit[3]), Integer.parseInt(firstSplit[4]), firstSplit[5], Integer.parseInt(firstSplit[6]), Boolean.valueOf(firstSplit[7]), firstSplit[8]);
     }
 
     public static BlockBody createCompiledBlockBody19(ThreadContext context, Object scriptObject, String closureMethod, int arity,
             String[] staticScopeNames, boolean hasMultipleArgsHead, int argsNodeType, String file, int line, boolean light, String parameterList) {
         StaticScope staticScope = context.runtime.getStaticScopeFactory().newBlockScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
 
         if (light) {
             return CompiledBlockLight19.newCompiledBlockLight(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType, parameterList.split(";"));
         } else {
             return CompiledBlock19.newCompiledBlock(
                     Arity.createArity(arity), staticScope,
                     createBlockCallback19(scriptObject, closureMethod, file, line),
                     hasMultipleArgsHead, argsNodeType, parameterList.split(";"));
         }
     }
     
     public static Block createBlock(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock.newCompiledClosure(
                 context,
                 self,
                 body);
     }
 
     public static Block createBlock19(ThreadContext context, IRubyObject self, BlockBody body) {
         return CompiledBlock19.newCompiledClosure(
                 context,
                 self,
                 body);
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String scopeString, CompiledBlockCallback callback) {
         StaticScope staticScope = decodeBlockScope(context, scopeString);
         staticScope.determineModule();
         
         context.preScopedBody(DynamicScope.newDynamicScope(staticScope, context.getCurrentScope()));
         
         Block block = CompiledBlock.newCompiledClosure(context, self, Arity.createArity(0), staticScope, callback, false, BlockBody.ZERO_ARGS);
         
         try {
             block.yield(context, null);
         } finally {
             context.postScopedBody();
         }
 
         return context.runtime.getNil();
     }
     
     public static Block createSharedScopeBlock(ThreadContext context, IRubyObject self, int arity, 
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         
         return CompiledSharedScopeBlock.newCompiledSharedScopeClosure(context, self, Arity.createArity(arity), 
                 context.getCurrentScope(), callback, hasMultipleArgsHead, argsNodeType);
     }
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, StaticScope scope,
             int arity, String filename, int line, CallConfiguration callConfig, String parameterDesc) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.runtime;
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         performNormalMethodChecks(containingClass, runtime, name);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructNormalMethod(
                 factory, javaName,
                 name, containingClass, new SimpleSourcePosition(filename, line), arity, scope, visibility, scriptObject,
                 callConfig,
                 parameterDesc);
         
         addInstanceMethod(containingClass, name, method, visibility,context, runtime);
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, StaticScope scope,
             int arity, String filename, int line, CallConfiguration callConfig, String parameterDesc) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.runtime;
 
         RubyClass rubyClass = performSingletonMethodChecks(runtime, receiver, name);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method = constructSingletonMethod(
                 factory, javaName, rubyClass,
                 new SimpleSourcePosition(filename, line), arity, scope,
                 scriptObject, callConfig, parameterDesc);
         
         rubyClass.addMethod(name, method);
         
         callSingletonMethodHook(receiver,context, runtime.fastNewSymbol(name));
         
         return runtime.getNil();
     }
 
     public static byte[] defOffline(String name, String classPath, String invokerName, Arity arity, StaticScope scope, CallConfiguration callConfig, String filename, int line) {
         MethodFactory factory = MethodFactory.createFactory(RuntimeHelpers.class.getClassLoader());
         byte[] methodBytes = factory.getCompiledMethodOffline(name, classPath, invokerName, arity, scope, callConfig, filename, line);
 
         return methodBytes;
     }
     
     public static RubyClass getSingletonClass(Ruby runtime, IRubyObject receiver) {
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError(runtime.is1_9() ? "can't define singleton" : ("no virtual class for " + receiver.getMetaClass().getBaseName()));
         } else {
             return receiver.getSingletonClass();
         }
     }
 
     // TODO: Only used by interface implementation; eliminate it
     public static IRubyObject invokeMethodMissing(IRubyObject receiver, String name, IRubyObject[] args) {
         ThreadContext context = receiver.getRuntime().getCurrentContext();
 
         // store call information so method_missing impl can use it
         context.setLastCallStatusAndVisibility(CallType.FUNCTIONAL, Visibility.PUBLIC);
 
         if (name.equals("method_missing")) {
             return RubyKernel.method_missing(context, receiver, args, Block.NULL_BLOCK);
         }
 
         IRubyObject[] newArgs = prepareMethodMissingArgs(args, context, name);
 
         return invoke(context, receiver, "method_missing", newArgs, Block.NULL_BLOCK);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject[] args, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, args, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, IRubyObject arg1, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, arg1, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, arg0, arg1, arg2, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType, Block block) {
         return selectMethodMissing(context, receiver, visibility, name, callType).call(context, receiver, receiver.getMetaClass(), name, block);
     }
 
     public static DynamicMethod selectMethodMissing(ThreadContext context, IRubyObject receiver, Visibility visibility, String name, CallType callType) {
         Ruby runtime = context.runtime;
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = receiver.getMetaClass().searchMethod("method_missing");
         if (methodMissing.isUndefined() || methodMissing.equals(runtime.getDefaultMethodMissing())) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     public static DynamicMethod selectMethodMissing(ThreadContext context, RubyClass selfClass, Visibility visibility, String name, CallType callType) {
         Ruby runtime = context.runtime;
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = selfClass.searchMethod("method_missing");
         if (methodMissing.isUndefined() || methodMissing.equals(runtime.getDefaultMethodMissing())) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     public static DynamicMethod selectMethodMissing(RubyClass selfClass, Visibility visibility, String name, CallType callType) {
         Ruby runtime = selfClass.getClassRuntime();
 
         if (name.equals("method_missing")) {
             return selectInternalMM(runtime, visibility, callType);
         }
 
         DynamicMethod methodMissing = selfClass.searchMethod("method_missing");
         if (methodMissing.isUndefined() || methodMissing.equals(runtime.getDefaultMethodMissing())) {
             return selectInternalMM(runtime, visibility, callType);
         }
         return new MethodMissingMethod(methodMissing, callType);
     }
 
     private static class MethodMissingMethod extends DynamicMethod {
         private final DynamicMethod delegate;
         private final CallType lastCallStatus;
 
         public MethodMissingMethod(DynamicMethod delegate, CallType lastCallStatus) {
             this.delegate = delegate;
             this.lastCallStatus = lastCallStatus;
         }
 
         @Override
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             context.setLastCallStatus(lastCallStatus);
             return this.delegate.call(context, self, clazz, "method_missing", prepareMethodMissingArgs(args, context, name), block);
         }
 
         @Override
         public DynamicMethod dup() {
             return this;
         }
     }
 
     private static DynamicMethod selectInternalMM(Ruby runtime, Visibility visibility, CallType callType) {
         if (visibility == Visibility.PRIVATE) {
             return runtime.getPrivateMethodMissing();
         } else if (visibility == Visibility.PROTECTED) {
             return runtime.getProtectedMethodMissing();
         } else if (callType == CallType.VARIABLE) {
             return runtime.getVariableMethodMissing();
         } else if (callType == CallType.SUPER) {
             return runtime.getSuperMethodMissing();
         } else {
             return runtime.getNormalMethodMissing();
         }
     }
 
     private static IRubyObject[] prepareMethodMissingArgs(IRubyObject[] args, ThreadContext context, String name) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = context.runtime.newSymbol(name);
 
         return newArgs;
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, Block block) {
         return self.getMetaClass().finvoke(context, self, name, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, arg2, block);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return self.getMetaClass().finvoke(context, self, name, args, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name) {
         return self.getMetaClass().finvoke(context, self, name);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0) {
         return self.getMetaClass().finvoke(context, self, name, arg0);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         return self.getMetaClass().finvoke(context, self, name, arg0, arg1, arg2);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject... args) {
         return self.getMetaClass().finvoke(context, self, name, args);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, CallType callType) {
         return RuntimeHelpers.invoke(context, self, name, IRubyObject.NULL_ARRAY, callType, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, args, callType, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, arg, callType, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return asClass.finvoke(context, self, name, args, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, Block block) {
         return asClass.finvoke(context, self, name, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, Block block) {
         return asClass.finvoke(context, self, name, arg0, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
         return asClass.finvoke(context, self, name, arg0, arg1, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return asClass.finvoke(context, self, name, arg0, arg1, arg2, block);
     }
 
     public static IRubyObject invokeChecked(ThreadContext context, IRubyObject self, String name) {
         return self.getMetaClass().finvokeChecked(context, self, name);
     }
 
     /**
      * The protocol for super method invocation is a bit complicated
      * in Ruby. In real terms it involves first finding the real
      * implementation class (the super class), getting the name of the
      * method to call from the frame, and then invoke that on the
      * super class with the current self as the actual object
      * invoking.
      */
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
         
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, args, block);
         }
         return method.call(context, self, superClass, name, args, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, block);
         }
         return method.call(context, self, superClass, name, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, block);
         }
         return method.call(context, self, superClass, name, arg0, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, arg1, block);
         }
         return method.call(context, self, superClass, name, arg0, arg1, block);
     }
     
     public static IRubyObject invokeSuper(ThreadContext context, IRubyObject self, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         checkSuperDisabledOrOutOfMethod(context);
         RubyModule klazz = context.getFrameKlazz();
         String name = context.getFrameName();
 
         RubyClass superClass = findImplementerIfNecessary(self.getMetaClass(), klazz).getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(name) : UndefinedMethod.INSTANCE;
 
         if (method.isUndefined()) {
             return callMethodMissing(context, self, method.getVisibility(), name, CallType.SUPER, arg0, arg1, arg2, block);
         }
         return method.call(context, self, superClass, name, arg0, arg1, arg2, block);
     }
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         return ensureRubyArray(value.getRuntime(), value);
     }
 
     public static RubyArray ensureRubyArray(Ruby runtime, IRubyObject value) {
         return value instanceof RubyArray ? (RubyArray)value : RubyArray.newArray(runtime, value);
     }
 
     public static RubyArray ensureMultipleAssignableRubyArray(IRubyObject value, Ruby runtime, boolean masgnHasHead) {
         if (!(value instanceof RubyArray)) {
             if (runtime.is1_9()) {
                 value = ArgsUtil.convertToRubyArray19(runtime, value, masgnHasHead);
             } else {
                 value = ArgsUtil.convertToRubyArray(runtime, value, masgnHasHead);                
             }
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
     }
     
     @Deprecated
     public static IRubyObject fastFetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName) {
         return fetchClassVariable(context, runtime, self, internedName);
     }
     
     public static IRubyObject getConstant(ThreadContext context, String internedName) {
         Ruby runtime = context.runtime;
 
         return context.getCurrentScope().getStaticScope().getConstantWithConstMissing(runtime, internedName, runtime.getObject());
     }
 
     public static IRubyObject nullToNil(IRubyObject value, ThreadContext context) {
         return value != null ? value : context.nil;
     }
     
     public static IRubyObject nullToNil(IRubyObject value, Ruby runtime) {
         return value != null ? value : runtime.getNil();
     }
     
     public static IRubyObject nullToNil(IRubyObject value, IRubyObject nil) {
         return value != null ? value : nil;
     }
     
     public static RubyClass prepareSuperClass(Ruby runtime, IRubyObject rubyClass) {
         RubyClass.checkInheritable(rubyClass); // use the same logic as in EvaluationState
         return (RubyClass)rubyClass;
     }
     
     public static RubyModule prepareClassNamespace(ThreadContext context, IRubyObject rubyModule) {
         if (rubyModule == null || rubyModule.isNil()) {
             rubyModule = context.getCurrentScope().getStaticScope().getModule();
 
             if (rubyModule == null) {
                 throw context.runtime.newTypeError("no outer class/module");
             }
         }
 
         if (rubyModule instanceof RubyModule) {
             return (RubyModule)rubyModule;
         } else {
             throw context.runtime.newTypeError(rubyModule + " is not a class/module");
         }
     }
     
     public static IRubyObject setClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     @Deprecated
     public static IRubyObject fastSetClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName, IRubyObject value) {
         return setClassVariable(context, runtime, self, internedName, value);
     }
     
     public static IRubyObject declareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String name, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     @Deprecated
     public static IRubyObject fastDeclareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String internedName, IRubyObject value) {
         return declareClassVariable(context, runtime, self, internedName, value);
     }
     
     public static void handleArgumentSizes(ThreadContext context, Ruby runtime, int given, int required, int opt, int rest) {
         if (opt == 0) {
             if (rest < 0) {
                 // no opt, no rest, exact match
                 if (given != required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 }
             } else {
                 // only rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 }
             }
         } else {
             if (rest < 0) {
                 // opt but no rest, must be at least required and no more than required + opt
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 } else if (given > (required + opt)) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + (required + opt) + ")");
                 }
             } else {
                 // opt and rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong number of arguments (" + given + " for " + required + ")");
                 }
             }
         }
     }
     
     /**
      * If it's Redo, Next, or Break, rethrow it as a normal exception for while to handle
      * @param re
      * @param context
      */
     public static Throwable unwrapRedoNextBreakOrJustLocalJump(RaiseException re, ThreadContext context) {
         RubyException exception = re.getException();
         if (context.runtime.getLocalJumpError().isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             switch (jumpError.getReason()) {
             case REDO:
                 return JumpException.REDO_JUMP;
             case NEXT:
                 return new JumpException.NextJump(jumpError.exit_value());
             case BREAK:
                 return new JumpException.BreakJump(context.getFrameJumpTarget(), jumpError.exit_value());
             }
         }
         return re;
     }
     
     public static String getLocalJumpTypeOrRethrow(RaiseException re) {
         RubyException exception = re.getException();
         Ruby runtime = exception.getRuntime();
         if (runtime.getLocalJumpError().isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             IRubyObject reason = jumpError.reason();
 
             return reason.asJavaString();
         }
 
         throw re;
     }
     
     public static IRubyObject unwrapLocalJumpErrorValue(RaiseException re) {
         return ((RubyLocalJumpError)re.getException()).exit_value();
     }
     
     public static IRubyObject processBlockArgument(Ruby runtime, Block block) {
         if (!block.isGiven()) {
             return runtime.getNil();
         }
         
         return processGivenBlock(block, runtime);
     }
 
     private static IRubyObject processGivenBlock(Block block, Ruby runtime) {
         RubyProc blockArg = block.getProcObject();
 
         if (blockArg == null) {
             blockArg = runtime.newBlockPassProc(Block.Type.PROC, block);
             blockArg.getBlock().type = Block.Type.PROC;
         }
 
         return blockArg;
     }
     
     public static Block getBlockFromBlockPassBody(Ruby runtime, IRubyObject proc, Block currentBlock) {
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = coerceProc(proc, runtime);
         }
 
         return getBlockFromProc(currentBlock, proc);
     }
 
     private static IRubyObject coerceProc(IRubyObject proc, Ruby runtime) throws RaiseException {
         proc = TypeConverter.convertToType(proc, runtime.getProc(), "to_proc", false);
 
         if (!(proc instanceof RubyProc)) {
             throw runtime.newTypeError("wrong argument type " + proc.getMetaClass().getName() + " (expected Proc)");
         }
         return proc;
     }
 
     private static Block getBlockFromProc(Block currentBlock, IRubyObject proc) {
         // TODO: Add safety check for taintedness
         if (currentBlock != null && currentBlock.isGiven()) {
             RubyProc procObject = currentBlock.getProcObject();
             // The current block is already associated with proc.  No need to create a new one
             if (procObject != null && procObject == proc) {
                 return currentBlock;
             }
         }
 
         return ((RubyProc) proc).getBlock();       
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         return getBlockFromBlockPassBody(proc.getRuntime(), proc, currentBlock);
 
     }
     
     public static IRubyObject backref(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(context.runtime);
         
         return RubyRegexp.match_last(backref);
     }
 
     public static IRubyObject[] getArgValues(ThreadContext context) {
         return context.getCurrentScope().getArgValues();
     }
     
     public static IRubyObject callZSuper(Ruby runtime, ThreadContext context, Block block, IRubyObject self) {
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         return RuntimeHelpers.invokeSuper(context, self, context.getCurrentScope().getArgValues(), block);
     }
     
     public static IRubyObject[] appendToObjectArray(IRubyObject[] array, IRubyObject add) {
         IRubyObject[] newArray = new IRubyObject[array.length + 1];
         System.arraycopy(array, 0, newArray, 0, array.length);
         newArray[array.length] = add;
         return newArray;
     }
     
     public static JumpException.ReturnJump returnJump(IRubyObject result, ThreadContext context) {
         return context.returnJump(result);
     }
     
     public static IRubyObject throwReturnJump(IRubyObject result, ThreadContext context) {
         throw context.returnJump(result);
     }
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, ThreadContext context) {
         // JRUBY-530, while case
         if (bj.getTarget() == context.getFrameJumpTarget()) {
             return (IRubyObject) bj.getValue();
         }
 
         throw bj;
     }
     
     public static IRubyObject breakJump(ThreadContext context, IRubyObject value) {
         throw new JumpException.BreakJump(context.getFrameJumpTarget(), value);
     }
     
     public static IRubyObject breakLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, value, "unexpected break");
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, ThreadContext context) {
         for (int i = 0; i < exceptions.length; i++) {
             IRubyObject result = isExceptionHandled(currentException, exceptions[i], context);
             if (result.isTrue()) return result;
         }
         return context.runtime.getFalse();
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception, ThreadContext context) {
         return isExceptionHandled((IRubyObject)currentException, exception, context);
     }
 
     public static IRubyObject isExceptionHandled(IRubyObject currentException, IRubyObject exception, ThreadContext context) {
         Ruby runtime = context.runtime;
         if (!runtime.getModule().isInstance(exception)) {
             throw runtime.newTypeError("class or module required for rescue clause");
         }
         IRubyObject result = invoke(context, exception, "===", currentException);
         if (result.isTrue()) return result;
         return runtime.getFalse();
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception0, IRubyObject exception1, ThreadContext context) {
         IRubyObject result = isExceptionHandled(currentException, exception0, context);
         if (result.isTrue()) return result;
         return isExceptionHandled(currentException, exception1, context);
     }
 
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject exception0, IRubyObject exception1, IRubyObject exception2, ThreadContext context) {
         IRubyObject result = isExceptionHandled(currentException, exception0, context);
         if (result.isTrue()) return result;
         return isExceptionHandled(currentException, exception1, exception2, context);
     }
 
     private static boolean checkJavaException(Throwable throwable, IRubyObject catchable, ThreadContext context) {
         Ruby runtime = context.runtime;
         if (
                 // rescue exception needs to catch Java exceptions
                 runtime.getException() == catchable ||
 
                 // rescue Object needs to catch Java exceptions
-                runtime.getObject() == catchable) {
+                runtime.getObject() == catchable ||
+
+                // rescue StandardError needs t= catch Java exceptions
+                runtime.getStandardError() == catchable) {
 
             if (throwable instanceof RaiseException) {
                 return isExceptionHandled(((RaiseException)throwable).getException(), catchable, context).isTrue();
             }
 
             // let Ruby exceptions decide if they handle it
             return isExceptionHandled(JavaUtil.convertJavaToUsableRubyObject(runtime, throwable), catchable, context).isTrue();
 
         } else if (runtime.getNativeException() == catchable) {
             // NativeException catches Java exceptions, lazily creating the wrapper
             return true;
 
         } else if (catchable instanceof RubyClass && catchable.getInstanceVariables().hasInstanceVariable("@java_class")) {
             RubyClass rubyClass = (RubyClass)catchable;
             JavaClass javaClass = (JavaClass)rubyClass.getInstanceVariable("@java_class");
             if (javaClass != null) {
                 Class cls = javaClass.javaClass();
                 if (cls.isInstance(throwable)) {
                     return true;
                 }
             }
         }
 
         return false;
     }
     
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject[] throwables, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwables, context);
         } else {
-            for (int i = 0; i < throwables.length; i++) {
-                if (checkJavaException(currentThrowable, throwables[i], context)) {
-                    return context.runtime.getTrue();
+            if (throwables.length == 0) {
+                // no rescue means StandardError, which rescues Java exceptions
+                return context.runtime.getTrue();
+            } else {
+                for (int i = 0; i < throwables.length; i++) {
+                    if (checkJavaException(currentThrowable, throwables[i], context)) {
+                        return context.runtime.getTrue();
+                    }
                 }
             }
 
             return context.runtime.getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable, context);
         } else {
             if (checkJavaException(currentThrowable, throwable, context)) {
                 return context.runtime.getTrue();
             }
 
             return context.runtime.getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
 
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0, context)) {
                 return context.runtime.getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1, context)) {
                 return context.runtime.getTrue();
             }
 
             return context.runtime.getFalse();
         }
     }
 
     public static IRubyObject isJavaExceptionHandled(Throwable currentThrowable, IRubyObject throwable0, IRubyObject throwable1, IRubyObject throwable2, ThreadContext context) {
         if (currentThrowable instanceof Unrescuable) {
             UnsafeFactory.getUnsafe().throwException(currentThrowable);
         }
         
         if (currentThrowable instanceof RaiseException) {
             return isExceptionHandled(((RaiseException)currentThrowable).getException(), throwable0, throwable1, throwable2, context);
         } else {
             if (checkJavaException(currentThrowable, throwable0, context)) {
                 return context.runtime.getTrue();
             }
             if (checkJavaException(currentThrowable, throwable1, context)) {
                 return context.runtime.getTrue();
             }
             if (checkJavaException(currentThrowable, throwable2, context)) {
                 return context.runtime.getTrue();
             }
 
             return context.runtime.getFalse();
         }
     }
 
     public static void storeExceptionInErrorInfo(Throwable currentThrowable, ThreadContext context) {
         IRubyObject exception = null;
         if (currentThrowable instanceof RaiseException) {
             exception = ((RaiseException)currentThrowable).getException();
         } else {
             exception = JavaUtil.convertJavaToUsableRubyObject(context.runtime, currentThrowable);
         }
         context.setErrorInfo(exception);
     }
 
     public static void storeNativeExceptionInErrorInfo(Throwable currentThrowable, ThreadContext context) {
         IRubyObject exception = null;
         if (currentThrowable instanceof RaiseException) {
             exception = ((RaiseException)currentThrowable).getException();
         } else {
             Ruby runtime = context.runtime;
 
             // wrap Throwable in a NativeException object
             exception = new NativeException(runtime, runtime.getNativeException(), currentThrowable);
             ((NativeException)exception).prepareIntegratedBacktrace(context, currentThrowable.getStackTrace());
         }
         context.setErrorInfo(exception);
     }
 
     public static void clearErrorInfo(ThreadContext context) {
         context.setErrorInfo(context.runtime.getNil());
     }
     
     public static void checkSuperDisabledOrOutOfMethod(ThreadContext context) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             if (name != null) {
                 throw context.runtime.newNameError("superclass method '" + name + "' disabled", name);
             } else {
                 throw context.runtime.newNoMethodError("super called outside of method", null, context.runtime.getNil());
             }
         }
     }
     
     public static Block ensureSuperBlock(Block given, Block parent) {
         if (!given.isGiven()) {
             return parent;
         }
         return given;
     }
     
     public static RubyModule findImplementerIfNecessary(RubyModule clazz, RubyModule implementationClass) {
         if (implementationClass != null && implementationClass.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             return clazz.findImplementer(implementationClass);
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             return implementationClass;
         }
     }
     
     public static RubyArray createSubarray(RubyArray input, int start) {
         return (RubyArray)input.subseqLight(start, input.size() - start);
     }
 
     public static RubyArray createSubarray(RubyArray input, int start, int post) {
         return (RubyArray)input.subseqLight(start, input.size() - post - start);
     }
     
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start) {
         if (start >= input.length) {
             return RubyArray.newEmptyArray(runtime);
         } else {
             return RubyArray.newArrayNoCopy(runtime, input, start);
         }
     }
 
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start, int exclude) {
         int length = input.length - exclude - start;
         if (length <= 0) {
             return RubyArray.newEmptyArray(runtime);
         } else {
             return RubyArray.newArrayNoCopy(runtime, input, start, length);
         }
     }
 
     public static IRubyObject elementOrNull(IRubyObject[] input, int element) {
         if (element >= input.length) {
             return null;
         } else {
             return input[element];
         }
     }
 
     public static IRubyObject optElementOrNull(IRubyObject[] input, int element, int postCount) {
         if (element + postCount >= input.length) {
             return null;
         } else {
             return input[element];
         }
     }
 
     public static IRubyObject elementOrNil(IRubyObject[] input, int element, IRubyObject nil) {
         if (element >= input.length) {
             return nil;
         } else {
             return input[element];
         }
     }
 
     public static IRubyObject postElementOrNil(IRubyObject[] input, int postCount, int postIndex, IRubyObject nil) {
         int aryIndex = input.length - postCount + postIndex;
         if (aryIndex >= input.length || aryIndex < 0) {
             return nil;
         } else {
             return input[aryIndex];
         }
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = RuntimeHelpers.splatValue(expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, "===", expression).isTrue()) ||
                     (expression == null && condition.isTrue())) {
                 return context.runtime.getTrue();
             }
         }
 
         return context.runtime.getFalse();
     }
     
     public static IRubyObject setConstantInModule(IRubyObject value, IRubyObject module, String name, ThreadContext context) {
         return context.setConstantInModule(name, module, value);
     }
 
     public static IRubyObject setConstantInCurrent(IRubyObject value, ThreadContext context, String name) {
         return context.setConstantInCurrent(name, value);
     }
     
     public static IRubyObject retryJump() {
         throw JumpException.RETRY_JUMP;
     }
     
     public static IRubyObject redoJump() {
         throw JumpException.REDO_JUMP;
     }
     
     public static IRubyObject redoLocalJumpError(Ruby runtime) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, runtime.getNil(), "unexpected redo");
     }
     
     public static IRubyObject nextJump(IRubyObject value) {
         throw new JumpException.NextJump(value);
     }
     
     public static IRubyObject nextLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.NEXT, value, "unexpected next");
     }
     
     public static final int MAX_SPECIFIC_ARITY_OBJECT_ARRAY = 10;
     
     public static IRubyObject[] anewarrayIRubyObjects(int size) {
         return new IRubyObject[size];
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, int start) {
         ary[start] = one;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, int start) {
         ary[start] = one;
         ary[start+1] = two;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         ary[start+8] = nine;
         return ary;
     }
     
     public static IRubyObject[] aastoreIRubyObjects(IRubyObject[] ary, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten, int start) {
         ary[start] = one;
         ary[start+1] = two;
         ary[start+2] = three;
         ary[start+3] = four;
         ary[start+4] = five;
         ary[start+5] = six;
         ary[start+6] = seven;
         ary[start+7] = eight;
         ary[start+8] = nine;
         ary[start+9] = ten;
         return ary;
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one) {
         return new IRubyObject[] {one};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two) {
         return new IRubyObject[] {one, two};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three) {
         return new IRubyObject[] {one, two, three};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return new IRubyObject[] {one, two, three, four};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return new IRubyObject[] {one, two, three, four, five};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six) {
         return new IRubyObject[] {one, two, three, four, five, six};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven) {
         return new IRubyObject[] {one, two, three, four, five, six, seven};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight, nine};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten) {
         return new IRubyObject[] {one, two, three, four, five, six, seven, eight, nine, ten};
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one) {
         return RubyArray.newArrayLight(runtime, one);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two) {
         return RubyArray.newArrayLight(runtime, one, two);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three) {
         return RubyArray.newArrayLight(runtime, one, two, three);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return RubyArray.newArrayLight(runtime, one, two, three, four);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight, nine);
     }
 
     public static RubyArray constructRubyArray(Ruby runtime, IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five, IRubyObject six, IRubyObject seven, IRubyObject eight, IRubyObject nine, IRubyObject ten) {
         return RubyArray.newArrayLight(runtime, one, two, three, four, five, six, seven, eight, nine, ten);
     }
     
     public static String[] constructStringArray(String one) {
         return new String[] {one};
     }
     
     public static String[] constructStringArray(String one, String two) {
         return new String[] {one, two};
     }
     
     public static String[] constructStringArray(String one, String two, String three) {
         return new String[] {one, two, three};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four) {
         return new String[] {one, two, three, four};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five) {
         return new String[] {one, two, three, four, five};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six) {
         return new String[] {one, two, three, four, five, six};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven) {
         return new String[] {one, two, three, four, five, six, seven};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight) {
         return new String[] {one, two, three, four, five, six, seven, eight};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine, String ten) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine, ten};
     }
     
     public static final int MAX_SPECIFIC_ARITY_HASH = 3;
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         hash.fastASetCheckString(runtime, key3, value3);
         return hash;
     }
 
     public static RubyHash constructSmallHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString(runtime, key1, value1);
         return hash;
     }
 
     public static RubyHash constructSmallHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString(runtime, key1, value1);
         hash.fastASetSmallCheckString(runtime, key2, value2);
         return hash;
     }
 
     public static RubyHash constructSmallHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetSmallCheckString(runtime, key1, value1);
         hash.fastASetSmallCheckString(runtime, key2, value2);
         hash.fastASetSmallCheckString(runtime, key3, value3);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         hash.fastASetCheckString19(runtime, key3, value3);
         return hash;
     }
 
     public static RubyHash constructSmallHash19(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString19(runtime, key1, value1);
         return hash;
     }
 
     public static RubyHash constructSmallHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString19(runtime, key1, value1);
         hash.fastASetSmallCheckString19(runtime, key2, value2);
         return hash;
     }
 
     public static RubyHash constructSmallHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newSmallHash(runtime);
         hash.fastASetSmallCheckString19(runtime, key1, value1);
         hash.fastASetSmallCheckString19(runtime, key2, value2);
         hash.fastASetSmallCheckString19(runtime, key3, value3);
         return hash;
     }
 
     public static IRubyObject undefMethod(ThreadContext context, Object nameArg) {
         RubyModule module = context.getRubyClass();
 
         String name = (nameArg instanceof String) ?
             (String) nameArg : nameArg.toString();
 
         if (module == null) {
             throw context.runtime.newTypeError("No class to undef method '" + name + "'.");
         }
 
         module.undef(context, name);
 
         return context.runtime.getNil();
     }
     
     public static IRubyObject defineAlias(ThreadContext context, IRubyObject self, Object newNameArg, Object oldNameArg) {
         Ruby runtime = context.runtime;
         RubyModule module = context.getRubyClass();
    
         if (module == null || self instanceof RubyFixnum || self instanceof RubySymbol){
             throw runtime.newTypeError("no class to make alias");
         }
 
         String newName = (newNameArg instanceof String) ?
             (String) newNameArg : newNameArg.toString();
         String oldName = (oldNameArg instanceof String) ? 
             (String) oldNameArg : oldNameArg.toString();
 
         module.defineAlias(newName, oldName);
         module.callMethod(context, "method_added", runtime.newSymbol(newName));
    
         return runtime.getNil();
     }
     
     public static IRubyObject negate(IRubyObject value, Ruby runtime) {
         if (value.isTrue()) return runtime.getFalse();
         return runtime.getTrue();
     }
     
     public static IRubyObject stringOrNil(ByteList value, ThreadContext context) {
         if (value == null) return context.nil;
         return RubyString.newStringShared(context.runtime, value);
     }
     
     public static void preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = context.runtime.getStaticScopeFactory().newLocalScope(null, varNames);
         preLoadCommon(context, staticScope, false);
     }
 
     public static void preLoad(ThreadContext context, String scopeString, boolean wrap) {
         StaticScope staticScope = decodeRootScope(context, scopeString);
         preLoadCommon(context, staticScope, wrap);
     }
 
     public static void preLoadCommon(ThreadContext context, StaticScope staticScope, boolean wrap) {
         RubyClass objectClass = context.runtime.getObject();
         IRubyObject topLevel = context.runtime.getTopSelf();
         if (wrap) {
             staticScope.setModule(RubyModule.newModule(context.runtime));
         } else {
             staticScope.setModule(objectClass);
         }
         DynamicScope scope = DynamicScope.newDynamicScope(staticScope);
 
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
         context.preNodeEval(objectClass, topLevel);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postNodeEval();
         context.postScopedBody();
     }
     
     public static void registerEndBlock(Block block, Ruby runtime) {
         runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
     }
     
     public static IRubyObject match3(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match(context, value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
 
     public static IRubyObject match3_19(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match19(context, value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
     
     public static IRubyObject getErrorInfo(Ruby runtime) {
         return runtime.getGlobalVariables().get("$!");
     }
     
     public static void setErrorInfo(Ruby runtime, IRubyObject error) {
         runtime.getGlobalVariables().set("$!", error);
     }
 
     public static IRubyObject setLastLine(Ruby runtime, ThreadContext context, IRubyObject value) {
         return context.getCurrentScope().setLastLine(value);
     }
 
     public static IRubyObject getLastLine(Ruby runtime, ThreadContext context) {
         return context.getCurrentScope().getLastLine(runtime);
     }
 
     public static IRubyObject setBackref(Ruby runtime, ThreadContext context, IRubyObject value) {
         if (!value.isNil() && !(value instanceof RubyMatchData)) throw runtime.newTypeError(value, runtime.getMatchData());
         return context.getCurrentScope().setBackRef(value);
     }
 
     public static IRubyObject getBackref(Ruby runtime, ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
         return backref;
     }
     
     public static IRubyObject preOpAsgnWithOrAnd(IRubyObject receiver, ThreadContext context, IRubyObject self, CallSite varSite) {
         return varSite.call(context, self, receiver);
     }
     
     public static IRubyObject postOpAsgnWithOrAnd(IRubyObject receiver, IRubyObject value, ThreadContext context, IRubyObject self, CallSite varAsgnSite) {
         varAsgnSite.call(context, self, receiver, value);
         return value;
     }
     
     public static IRubyObject opAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, CallSite varSite, CallSite opSite, CallSite opAsgnSite) {
         IRubyObject var = varSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, arg);
         opAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg1, arg2, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2, arg3);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, new IRubyObject[] {arg1, arg2, arg3, result});
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, appendToObjectArray(args, result));
 
         return result;
     }
 
     
     public static IRubyObject opElementAsgnWithOrPartTwoOneArg(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, arg, value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoTwoArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, args[0], args[1], value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoThreeArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, new IRubyObject[] {args[0], args[1], args[2], value});
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoNArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         asetSite.call(context, self, receiver, newArgs);
         return value;
     }
 
     public static RubyArray arrayValue(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         return arrayValue(runtime.getCurrentContext(), runtime, value);
     }
     
     public static RubyArray arrayValue(ThreadContext context, Ruby runtime, IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
             // remove this hack too.
 
             if (value.respondsTo("to_a") && value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 IRubyObject avalue = value.callMethod(context, "to_a");
                 if (!(avalue instanceof RubyArray)) {
                     if (runtime.is1_9() && avalue.isNil()) {
                         return runtime.newArray(value);
                     } else {
                         throw runtime.newTypeError("`to_a' did not return Array");
                     }
                 }
                 return (RubyArray)avalue;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     public static IRubyObject aryToAry(IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, value.getRuntime().getArray(), "to_ary", false);
         }
 
         return value.getRuntime().newArray(value);
     }
 
     public static IRubyObject aValueSplat(IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return value.getRuntime().getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first() : array;
     }
 
     public static IRubyObject aValueSplat19(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             return value.getRuntime().getNil();
         }
 
         return (RubyArray) value;
     }
 
     public static RubyArray splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
     }
 
     public static RubyArray splatValue19(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newEmptyArray();
         }
 
         return arrayValue(value);
     }
     
     public static IRubyObject unsplatValue19(IRubyObject argsResult) {
         if (argsResult instanceof RubyArray) {
             RubyArray array = (RubyArray) argsResult;
                     
             if (array.size() == 1) {
                 IRubyObject newResult = array.eltInternal(0);
                 if (!((newResult instanceof RubyArray) && ((RubyArray) newResult).size() == 0)) {
                     argsResult = newResult;
                 }
             }
         }        
         return argsResult;
     }
 
     public static IRubyObject unsplatValue19IfArityOne(IRubyObject argsResult, Block block) {
         if (block.isGiven() && block.arity().getValue() > 1) argsResult = RuntimeHelpers.unsplatValue19(argsResult);
         return argsResult;
     }
         
     public static IRubyObject[] splatToArguments(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     public static IRubyObject[] splatToArguments19(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return IRubyObject.NULL_ARRAY;
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     private static IRubyObject[] splatToArgumentsCommon(Ruby runtime, IRubyObject value) {
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             return convertSplatToJavaArray(runtime, value);
         }
         return ((RubyArray)tmp).toJavaArrayMaybeUnsafe();
     }
     
     private static IRubyObject[] convertSplatToJavaArray(Ruby runtime, IRubyObject value) {
         // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
         // remove this hack too.
 
         RubyClass metaClass = value.getMetaClass();
         DynamicMethod method = metaClass.searchMethod("to_a");
         if (method.isUndefined() || method.getImplementationClass() == runtime.getKernel()) {
             return new IRubyObject[] {value};
         }
 
         IRubyObject avalue = method.call(runtime.getCurrentContext(), value, metaClass, "to_a");
         if (!(avalue instanceof RubyArray)) {
             if (runtime.is1_9() && avalue.isNil()) {
                 return new IRubyObject[] {value};
             } else {
                 throw runtime.newTypeError("`to_a' did not return Array");
             }
         }
         return ((RubyArray)avalue).toJavaArray();
     }
     
     public static IRubyObject[] argsCatToArguments(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     public static IRubyObject[] argsCatToArguments19(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments19(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     private static IRubyObject[] argsCatToArgumentsCommon(IRubyObject[] args, IRubyObject[] ary, IRubyObject cat) {
         if (ary.length > 0) {
             IRubyObject[] newArgs = new IRubyObject[args.length + ary.length];
             System.arraycopy(args, 0, newArgs, 0, args.length);
             System.arraycopy(ary, 0, newArgs, args.length, ary.length);
             args = newArgs;
         }
         
         return args;
     }
 
     public static void addInstanceMethod(RubyModule containingClass, String name, DynamicMethod method, Visibility visibility, ThreadContext context, Ruby runtime) {
         containingClass.addMethod(name, method);
 
         RubySymbol sym = runtime.fastNewSymbol(name);
         if (visibility == Visibility.MODULE_FUNCTION) {
             addModuleMethod(containingClass, name, method, context, sym);
         }
 
         callNormalMethodHook(containingClass, context, sym);
     }
 
     private static void addModuleMethod(RubyModule containingClass, String name, DynamicMethod method, ThreadContext context, RubySymbol sym) {
         containingClass.getSingletonClass().addMethod(name, new WrapperMethod(containingClass.getSingletonClass(), method, Visibility.PUBLIC));
         containingClass.callMethod(context, "singleton_method_added", sym);
     }
 
     private static void callNormalMethodHook(RubyModule containingClass, ThreadContext context, RubySymbol name) {
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             callSingletonMethodHook(((MetaClass) containingClass).getAttached(), context, name);
         } else {
             containingClass.callMethod(context, "method_added", name);
         }
     }
 
     private static void callSingletonMethodHook(IRubyObject receiver, ThreadContext context, RubySymbol name) {
         receiver.callMethod(context, "singleton_method_added", name);
     }
 
     private static DynamicMethod constructNormalMethod(
             MethodFactory factory,
             String javaName,
             String name,
             RubyModule containingClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Visibility visibility,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         DynamicMethod method;
 
         if (name.equals("initialize") || name.equals("initialize_copy") || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             method = factory.getCompiledMethodLazily(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             method = factory.getCompiledMethod(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
 
         return method;
     }
 
     private static DynamicMethod constructSingletonMethod(
             MethodFactory factory,
             String javaName,
             RubyClass rubyClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             return factory.getCompiledMethodLazily(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             return factory.getCompiledMethod(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
