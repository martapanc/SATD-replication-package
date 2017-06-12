diff --git a/src/org/jruby/RubyException.java b/src/org/jruby/RubyException.java
index 61e704c144..46de561b52 100644
--- a/src/org/jruby/RubyException.java
+++ b/src/org/jruby/RubyException.java
@@ -1,366 +1,360 @@
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
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.SafePropertyAccessor;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Exception")
 public class RubyException extends RubyObject {
     private ThreadContext.RubyStackTraceElement[] backtraceFrames;
     private StackTraceElement[] javaStackTrace;
     private IRubyObject backtrace;
     public IRubyObject message;
     public static final int TRACE_HEAD = 8;
     public static final int TRACE_TAIL = 4;
     public static final int TRACE_MAX = TRACE_HEAD + TRACE_TAIL + 6;
 
     protected RubyException(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, null);
     }
 
     public RubyException(Ruby runtime, RubyClass rubyClass, String message) {
         super(runtime, rubyClass);
         
         this.message = message == null ? runtime.getNil() : runtime.newString(message);
         this.javaStackTrace = Thread.currentThread().getStackTrace();
     }
     
     private static ObjectAllocator EXCEPTION_ALLOCATOR = new ObjectAllocator() {
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
     
     public void setBacktraceFrames(ThreadContext.RubyStackTraceElement[] backtraceFrames) {
         this.backtraceFrames = backtraceFrames;
     }
     
     public ThreadContext.RubyStackTraceElement[] getBacktraceFrames() {
         return backtraceFrames;
     }
 
     public void prepareBacktrace(ThreadContext context, boolean nativeException) {
         ThreadContext.RubyStackTraceElement[] stackTrace = getBacktraceFrames();
 
         // if it's null, build a backtrace
         if (stackTrace == null) {
             stackTrace = context.createBacktrace2(0, nativeException);
 
             // if it's still null, just use an empty trace
             if (stackTrace == null) stackTrace = new ThreadContext.RubyStackTraceElement[0];
 
             setBacktraceFrames(stackTrace);
         }
     }
     
     public static final int RAW = 0;
     public static final int RAW_FILTERED = 1;
     public static final int RUBY_FRAMED = 2;
     public static final int RUBY_COMPILED = 3;
     public static final int RUBY_HYBRID = 4;
     public static final int RUBINIUS = 5;
 
     public static final int RAW_FRAME_CROP_COUNT = 10;
     
     public static int TRACE_TYPE;
     
     static {
         String style = SafePropertyAccessor.getProperty("jruby.backtrace.style", "ruby_framed").toLowerCase();
         
         if (style.equalsIgnoreCase("raw")) TRACE_TYPE = RAW;
         else if (style.equalsIgnoreCase("raw_filtered")) TRACE_TYPE = RAW_FILTERED;
         else if (style.equalsIgnoreCase("ruby_framed")) TRACE_TYPE = RUBY_FRAMED;
         else if (style.equalsIgnoreCase("ruby_compiled")) TRACE_TYPE = RUBY_COMPILED;
         else if (style.equalsIgnoreCase("ruby_hybrid")) TRACE_TYPE = RUBY_HYBRID;
         else if (style.equalsIgnoreCase("rubinius")) TRACE_TYPE = RUBINIUS;
         else TRACE_TYPE = RUBY_FRAMED;
     }
     
     public IRubyObject getBacktrace() {
         if (backtrace == null) {
             initBacktrace();
         }
         return backtrace;
     }
     
     public void initBacktrace() {
         switch (TRACE_TYPE) {
         case RAW:
             backtrace = ThreadContext.createRawBacktrace(getRuntime(), javaStackTrace, false);
             break;
         case RAW_FILTERED:
             backtrace = ThreadContext.createRawBacktrace(getRuntime(), javaStackTrace, true);
             break;
         case RUBY_FRAMED:
         case RUBINIUS:
             backtrace = backtraceFrames == null ? getRuntime().getNil() : ThreadContext.createBacktraceFromFrames(getRuntime(), backtraceFrames);
             break;
         case RUBY_COMPILED:
             backtrace = ThreadContext.createRubyCompiledBacktrace(getRuntime(), javaStackTrace);
             break;
         case RUBY_HYBRID:
             backtrace = ThreadContext.createRubyHybridBacktrace(getRuntime(), backtraceFrames, javaStackTrace, getRuntime().getDebug().isTrue());
             break;
         }
     }
 
     @JRubyMethod(optional = 2, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length == 1) message = args[0];
         return this;
     }
 
     @JRubyMethod
     public IRubyObject backtrace() {
         return getBacktrace(); 
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject set_backtrace(IRubyObject obj) {
         if (obj.isNil()) {
             backtrace = null;
         } else if (!isArrayOfStrings(obj)) {
             throw getRuntime().newTypeError("backtrace must be Array of String");
         } else {
             backtrace = (RubyArray) obj;
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
 
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s(ThreadContext context) {
         if (message.isNil()) return context.getRuntime().newString(getMetaClass().getName());
         message.setTaint(isTaint());
         return message;
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
         RubyModule rubyClass = getMetaClass();
         RubyString exception = RubyString.objAsString(context, this);
 
         if (exception.getByteList().getRealSize() == 0) return getRuntime().newString(rubyClass.getName());
         StringBuilder sb = new StringBuilder("#<");
         sb.append(rubyClass.getName()).append(": ").append(exception.getByteList()).append(">");
         return getRuntime().newString(sb.toString());
     }
 
     @JRubyMethod(name = "==", compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         boolean equal =
                 context.getRuntime().getException().isInstance(other) &&
                 callMethod(context, "message").equals(other.callMethod(context, "message")) &&
                 callMethod(context, "backtrace").equals(other.callMethod(context, "backtrace"));
         return context.getRuntime().newBoolean(equal);
     }
 
     @JRubyMethod(name = "===", meta = true)
     public static IRubyObject op_eqq(ThreadContext context, IRubyObject recv, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         // special case non-FlowControlException Java exceptions so they'll be caught by rescue Exception
         if (recv == runtime.getException() && other instanceof ConcreteJavaProxy) {
             Object object = ((ConcreteJavaProxy)other).getObject();
             if (object instanceof Throwable && !(object instanceof FlowControlException)) {
                 return context.getRuntime().getTrue();
             }
         }
         // fall back on default logic
         return ((RubyClass)recv).op_eqq(context, other);
     }
 
     @Override
     public void copySpecialInstanceVariables(IRubyObject clone) {
         RubyException exception = (RubyException)clone;
         exception.backtraceFrames = backtraceFrames;
         exception.javaStackTrace = javaStackTrace;
         exception.backtrace = backtrace;
         exception.message = message;
     }
 
     public void printBacktrace(PrintStream errorStream) {
         IRubyObject backtrace = callMethod(getRuntime().getCurrentContext(), "backtrace");
         boolean debug = getRuntime().getDebug().isTrue();
         if (!backtrace.isNil() && backtrace instanceof RubyArray) {
             IRubyObject[] elements = backtrace.convertToArray().toJavaArray();
 
             for (int i = 1; i < elements.length; i++) {
                 IRubyObject stackTraceLine = elements[i];
-                    if (stackTraceLine instanceof RubyString) {
+                if (stackTraceLine instanceof RubyString) {
                     printStackTraceLine(errorStream, stackTraceLine);
                 }
-
-                if (!debug && TRACE_TYPE != RAW && i == RubyException.TRACE_HEAD && elements.length > RubyException.TRACE_MAX) {
-                    int hiddenLevels = elements.length - RubyException.TRACE_HEAD - RubyException.TRACE_TAIL;
-                            errorStream.print("\t ... " + hiddenLevels + " levels...\n");
-                    i = elements.length - RubyException.TRACE_TAIL;
-                }
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
 
     // rb_exc_new3
     public static IRubyObject newException(ThreadContext context, RubyClass exceptionClass, IRubyObject message) {
         return exceptionClass.callMethod(context, "new", message.convertToString());
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 2382bb1283..af55ea093c 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1493 +1,1496 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 package org.jruby.runtime;
 
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyContinuation.Continuation;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.RubyThread;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException.ReturnJump;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.InterpretedMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public final class ThreadContext {
     public static ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
 
         return context;
     }
     
     private final static int INITIAL_SIZE = 10;
     private final static int INITIAL_FRAMES_SIZE = 10;
     
     /** The number of calls after which to do a thread event poll */
     private final static int CALL_POLL_COUNT = 0xFFF;
     private final static String UNKNOWN_NAME = "(unknown)";
 
     // runtime and nil cached here for speed of access from any thread
     public final Ruby runtime;
     public final IRubyObject nil;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     private Frame[] frameStack = new Frame[INITIAL_FRAMES_SIZE];
     private int frameIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
 
     private static final Continuation[] EMPTY_CATCHTARGET_STACK = new Continuation[0];
     private Continuation[] catchStack = EMPTY_CATCHTARGET_STACK;
     private int catchIndex = -1;
     
     // File where current executing unit is being evaluated
     private String file = "";
     
     // Line where current executing unit is being evaluated
     private int line = 0;
 
     // In certain places, like grep, we don't use real frames for the
     // call blocks. This has the effect of not setting the backref in
     // the correct frame - this delta is activated to the place where
     // the grep is running in so that the backref will be set in an
     // appropriate place.
     private int rubyFrameDelta = 0;
     private boolean eventHooksEnabled = true;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         this.nil = runtime.getNil();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
 
         Frame[] stack = frameStack;
         int length = stack.length;
         for (int i = 0; i < length; i++) {
             stack[i] = new Frame();
         }
     }
 
     @Override
     protected void finalize() throws Throwable {
         thread.dispose();
     }
     
     CallType lastCallType;
     
     Visibility lastVisibility;
     
     IRubyObject lastExitStatus;
     
     public final Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return thread.getErrorInfo();
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         thread.setErrorInfo(errorInfo);
         return errorInfo;
     }
     
     public ReturnJump returnJump(IRubyObject value) {
         return new ReturnJump(getFrameJumpTarget(), value);
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
 
     public CallType getLastCallType() {
         return lastCallType;
     }
 
     public void setLastVisibility(Visibility visibility) {
         lastVisibility = visibility;
     }
 
     public Visibility getLastVisibility() {
         return lastVisibility;
     }
     
     public void setLastCallStatusAndVisibility(CallType callType, Visibility visibility) {
         lastCallType = callType;
         lastVisibility = visibility;
     }
     
     public IRubyObject getLastExitStatus() {
         return lastExitStatus;
     }
     
     public void setLastExitStatus(IRubyObject lastExitStatus) {
         this.lastExitStatus = lastExitStatus;
     }
 
     public void printScope() {
         System.out.println("SCOPE STACK:");
         for (int i = 0; i <= scopeIndex; i++) {
             System.out.println(scopeStack[i]);
         }
     }
 
     public DynamicScope getCurrentScope() {
         return scopeStack[scopeIndex];
     }
     
     public DynamicScope getPreviousScope() {
         return scopeStack[scopeIndex - 1];
     }
     
     private void expandFramesIfNecessary() {
         int newSize = frameStack.length * 2;
         frameStack = fillNewFrameStack(new Frame[newSize], newSize);
     }
 
     private Frame[] fillNewFrameStack(Frame[] newFrameStack, int newSize) {
         System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
 
         for (int i = frameStack.length; i < newSize; i++) {
             newFrameStack[i] = new Frame();
         }
         
         return newFrameStack;
     }
     
     private void expandParentsIfNecessary() {
         int newSize = parentStack.length * 2;
         RubyModule[] newParentStack = new RubyModule[newSize];
 
         System.arraycopy(parentStack, 0, newParentStack, 0, parentStack.length);
 
         parentStack = newParentStack;
     }
     
     public void pushScope(DynamicScope scope) {
         int index = ++scopeIndex;
         DynamicScope[] stack = scopeStack;
         stack[index] = scope;
         if (index + 1 == stack.length) {
             expandScopesIfNecessary();
         }
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
     
     private void expandScopesIfNecessary() {
         int newSize = scopeStack.length * 2;
         DynamicScope[] newScopeStack = new DynamicScope[newSize];
 
         System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
 
         scopeStack = newScopeStack;
     }
     
     public RubyThread getThread() {
         return thread;
     }
     
     public void setThread(RubyThread thread) {
         this.thread = thread;
 
         // associate the thread with this context, unless we're clearing the reference
         if (thread != null) {
             thread.setContext(this);
         }
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         int newSize = catchStack.length * 2;
         if (newSize == 0) newSize = 1;
         Continuation[] newCatchStack = new Continuation[newSize];
 
         System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
         catchStack = newCatchStack;
     }
     
     public void pushCatch(Continuation catchTarget) {
         int index = ++catchIndex;
         if (index == catchStack.length) {
             expandCatchIfNecessary();
         }
         catchStack[index] = catchTarget;
     }
     
     public void popCatch() {
         catchIndex--;
     }
 
     /**
      * Find the active Continuation for the given tag. Must be called with an
      * interned string.
      *
      * @param tag The interned string to search for
      * @return The continuation associated with this tag
      */
     public Continuation getActiveCatch(String tag) {
         for (int i = catchIndex; i >= 0; i--) {
             Continuation c = catchStack[i];
             if (c.tag.equals(tag)) return c;
         }
         return null;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         Frame currentFrame = stack[index - 1];
         stack[index].updateFrame(currentFrame);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private Frame pushFrame(Frame frame) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index] = frame;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return frame;
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(clazz, self, name, block, file, line, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushEvalFrame(IRubyObject self) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrameForEval(self, file, line, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushBacktraceFrame(String name) {
         pushFrame(name);        
     }
     
     private void pushFrame(String name) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(name, file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushFrame() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void popFrame() {
         Frame frame = frameStack[frameIndex--];
         setFileAndLine(frame);
         
         frame.clear();
     }
         
     private void popFrameReal(Frame oldFrame) {
         int index = frameIndex;
         Frame frame = frameStack[index];
         frameStack[index] = oldFrame;
         frameIndex = index - 1;
         setFileAndLine(frame);
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
 
     public int getRubyFrameDelta() {
         return this.rubyFrameDelta;
     }
     
     public void setRubyFrameDelta(int newDelta) {
         this.rubyFrameDelta = newDelta;
     }
 
     public Frame getCurrentRubyFrame() {
         return frameStack[frameIndex-rubyFrameDelta];
     }
     
     public Frame getNextFrame() {
         int index = frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return stack[index + 1];
     }
     
     public Frame getPreviousFrame() {
         int index = frameIndex;
         return index < 1 ? null : frameStack[index - 1];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
 
     public Frame[] getFrames(int delta) {
         int top = frameIndex + delta;
         Frame[] frames = new Frame[top + 1];
         for (int i = 0; i <= top; i++) {
             frames[i] = frameStack[i].duplicateForBacktrace();
         }
         return frames;
     }
 
     /**
      * Search the frame stack for the given JumpTarget. Return true if it is
      * found and false otherwise. Skip the given number of frames before
      * beginning the search.
      * 
      * @param target The JumpTarget to search for
      * @param skipFrames The number of frames to skip before searching
      * @return
      */
     public boolean isJumpTargetAlive(int target, int skipFrames) {
         for (int i = frameIndex - skipFrames; i >= 0; i--) {
             if (frameStack[i].getJumpTarget() == target) return true;
         }
         return false;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public int getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return file;
     }
     
     public int getLine() {
         return line;
     }
     
     public void setFile(String file) {
         this.file = file;
     }
     
     public void setLine(int line) {
         this.line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         this.file = file;
         this.line = line;
     }
     
     public void setFileAndLine(Frame frame) {
         this.file = frame.getFile();
         this.line = frame.getLine();
     }
 
     public void setFileAndLine(ISourcePosition position) {
         this.file = position.getFile();
         this.line = position.getStartLine();
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
     }
     
     public void pollThreadEvents() {
         thread.pollThreadEvents(this);
     }
     
     public int callNumber = 0;
 
     public int getCurrentTarget() {
         return callNumber;
     }
     
     public void callThreadPoll() {
         if ((callNumber++ & CALL_POLL_COUNT) == 0) pollThreadEvents();
     }
 
     public static void callThreadPoll(ThreadContext context) {
         if ((context.callNumber++ & CALL_POLL_COUNT) == 0) context.pollThreadEvents();
     }
     
     public void trace(RubyEvent event, String name, RubyModule implClass) {
         trace(event, name, implClass, file, line);
     }
 
     public void trace(RubyEvent event, String name, RubyModule implClass, String file, int line) {
         runtime.callEventHooks(this, event, file, line, name, implClass);
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         
         int index = ++parentIndex;
         RubyModule[] stack = parentStack;
         stack[index] = currentModule;
         if (index + 1 == stack.length) {
             expandParentsIfNecessary();
         }
     }
     
     public RubyModule popRubyClass() {
         int index = parentIndex;
         RubyModule[] stack = parentStack;
         RubyModule ret = stack[index];
         stack[index] = null;
         parentIndex = index - 1;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert parentIndex != -1 : "Trying to get RubyClass from empty stack";
         RubyModule parentModule = parentStack[parentIndex];
         return parentModule.getNonIncludedClass();
     }
 
     public RubyModule getPreviousRubyClass() {
         assert parentIndex != 0 : "Trying to get RubyClass from too-shallow stack";
         RubyModule parentModule = parentStack[parentIndex - 1];
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject value = getConstant(internedName);
 
         return value != null;
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         return getCurrentScope().getStaticScope().getConstant(runtime, internedName, runtime.getObject());
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String internedName, IRubyObject result) {
         RubyModule module;
 
         if ((module = getCurrentScope().getStaticScope().getModule()) != null) {
             module.fastSetConstant(internedName, result);
             return result;
         }
 
         // TODO: wire into new exception handling mechanism
         throw runtime.newTypeError("no class/module to define constant");
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInModule(String internedName, IRubyObject target, IRubyObject result) {
         if (!(target instanceof RubyModule)) {
             throw runtime.newTypeError(target.toString() + " is not a class/module");
         }
         RubyModule module = (RubyModule)target;
         module.fastSetConstant(internedName, result);
         
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String internedName, IRubyObject result) {
         runtime.getObject().fastSetConstant(internedName, result);
         
         return result;
     }
     
     @Deprecated
     private static void addBackTraceElement(RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame) {
         addBackTraceElement(backtrace.getRuntime(), backtrace, frame, previousFrame);
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, Frame frame, Frame previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getLine() == previousFrame.getLine() &&
                 frame.getName() != null && 
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getFile().equals(previousFrame.getFile())) {
             return;
         }
         
         RubyString traceLine;
         if (previousFrame.getName() != null) {
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `" + previousFrame.getName() + '\'');
         } else if (runtime.is1_9()) {
             // TODO: This probably isn't the best hack, but it works until we can have different
             // root frame setup for 1.9 easily.
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `<main>'");
         } else {
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1));
         }
         
         backtrace.append(traceLine);
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame) {
-        if (frame != previousFrame && // happens with native exceptions, should not filter those out
+        if (!backtrace.getRuntime().is1_9() &&
+                frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getLineNumber() == previousFrame.getLineNumber() &&
                 frame.getMethodName() != null &&
                 frame.getMethodName().equals(previousFrame.getMethodName()) &&
                 frame.getFileName() != null &&
                 frame.getFileName().equals(previousFrame.getFileName())) {
             return;
         }
         
         RubyString traceLine;
         String fileName = frame.getFileName();
         if (fileName == null) fileName = "";
         if (previousFrame.getMethodName().equals(UNKNOWN_NAME)) {
             traceLine = RubyString.newString(runtime, fileName + ':' + (frame.getLineNumber()));
         } else {
             traceLine = RubyString.newString(runtime, fileName + ':' + (frame.getLineNumber()) + ":in `" + previousFrame.getMethodName() + '\'');
         }
         
         backtrace.append(traceLine);
     }
     
     private static void addBackTraceElement(RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame, FrameType frameType) {
-        if (frame != previousFrame && // happens with native exceptions, should not filter those out
+        // filter out recursive calls only in 1.8 mode
+        if (!backtrace.getRuntime().is1_9() &&
+                frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getMethodName() != null && 
                 frame.getMethodName().equals(previousFrame.getMethodName()) &&
                 frame.getFileName().equals(previousFrame.getFileName()) &&
                 frame.getLineNumber() == previousFrame.getLineNumber()) {
             return;
         }
         
         StringBuilder buf = new StringBuilder(60);
         buf.append(frame.getFileName()).append(':').append(frame.getLineNumber());
         
         if (previousFrame.getMethodName() != null) {
             switch (frameType) {
             case METHOD:
                 buf.append(":in `");
                 buf.append(previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case BLOCK:
                 buf.append(":in `");
                 buf.append("block in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case EVAL:
                 buf.append(":in `");
                 buf.append("eval in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case CLASS:
                 buf.append(":in `");
                 buf.append("class in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case ROOT:
                 buf.append(":in `<toplevel>'");
                 break;
             }
         }
         
         backtrace.append(backtrace.getRuntime().newString(buf.toString()));
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, RubyStackTraceElement[] backtraceFrames) {
         return createBacktraceFromFrames(runtime, backtraceFrames, true);
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createCallerBacktrace(Ruby runtime, int level) {
         int traceSize = frameIndex - level + 1;
         RubyArray backtrace = runtime.newArray(traceSize);
 
         for (int i = traceSize - 1; i > 0; i--) {
             addBackTraceElement(runtime, backtrace, frameStack[i], frameStack[i - 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, RubyStackTraceElement[] backtraceFrames, boolean cropAtEval) {
         RubyArray backtrace = runtime.newArray();
         
         if (backtraceFrames == null || backtraceFrames.length <= 0) return backtrace;
         
         int traceSize = backtraceFrames.length;
 
         for (int i = 0; i < traceSize - 1; i++) {
             RubyStackTraceElement frame = backtraceFrames[i];
             // We are in eval with binding break out early
             // FIXME: This is broken with the new backtrace stuff
             if (cropAtEval && frame.isBinding()) break;
 
             addBackTraceElement(runtime, backtrace, frame, backtraceFrames[i + 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Frame[] createBacktrace(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         Frame[] traceFrames;
         
         if (traceSize <= 0) return null;
         
         if (nativeException) {
             // assert level == 0;
             traceFrames = new Frame[traceSize + 1];
             traceFrames[traceSize] = frameStack[frameIndex];
         } else {
             traceFrames = new Frame[traceSize];
         }
         
         System.arraycopy(frameStack, 0, traceFrames, 0, traceSize);
         
         return traceFrames;
     }
 
     public boolean isEventHooksEnabled() {
         return eventHooksEnabled;
     }
 
     public void setEventHooksEnabled(boolean flag) {
         eventHooksEnabled = flag;
     }
 
     public static class RubyStackTraceElement {
         private StackTraceElement element;
         private boolean binding;
 
         public RubyStackTraceElement(String cls, String method, String file, int line, boolean binding) {
             element = new StackTraceElement(cls, method, file, line);
             this.binding = binding;
         }
 
         public StackTraceElement getElement() {
             return element;
         }
 
         public boolean isBinding() {
             return binding;
         }
 
         public String getClassName() {
             return element.getClassName();
         }
 
         public String getFileName() {
             return element.getFileName();
         }
 
         public int getLineNumber() {
             return element.getLineNumber();
         }
 
         public String getMethodName() {
             return element.getMethodName();
         }
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public RubyStackTraceElement[] createBacktrace2(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         RubyStackTraceElement[] newTrace;
         
         if (traceSize <= 0) return null;
 
         int totalSize = traceSize;
         if (nativeException) {
             // assert level == 0;
             totalSize = traceSize + 1;
         }
         newTrace = new RubyStackTraceElement[totalSize];
 
         return buildTrace(newTrace);
     }
 
     private RubyStackTraceElement[] buildTrace(RubyStackTraceElement[] newTrace) {
         for (int i = 0; i < newTrace.length; i++) {
             Frame current = frameStack[i];
             String klazzName = getClassNameFromFrame(current);
             String methodName = getMethodNameFromFrame(current);
             newTrace[newTrace.length - 1 - i] = 
                     new RubyStackTraceElement(klazzName, methodName, current.getFile(), current.getLine() + 1, current.isBindingFrame());
         }
         
         return newTrace;
     }
 
     private String getClassNameFromFrame(Frame current) {
         String klazzName;
         if (current.getKlazz() == null) {
             klazzName = UNKNOWN_NAME;
         } else {
             klazzName = current.getKlazz().getName();
         }
         return klazzName;
     }
     
     private String getMethodNameFromFrame(Frame current) {
         String methodName = current.getName();
         if (current.getName() == null) {
             methodName = UNKNOWN_NAME;
         }
         return methodName;
     }
     
     private static String createRubyBacktraceString(StackTraceElement element) {
         return element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'";
     }
     
     public static String createRawBacktraceStringFromThrowable(Throwable t) {
         StackTraceElement[] javaStackTrace = t.getStackTrace();
         
         StringBuffer buffer = new StringBuffer();
         if (javaStackTrace != null && javaStackTrace.length > 0) {
             StackTraceElement element = javaStackTrace[0];
 
             buffer
                     .append(createRubyBacktraceString(element))
                     .append(": ")
                     .append(t.toString())
                     .append("\n");
             for (int i = 1; i < javaStackTrace.length; i++) {
                 element = javaStackTrace[i];
                 
                 buffer
                         .append("\tfrom ")
                         .append(createRubyBacktraceString(element));
                 if (i + 1 < javaStackTrace.length) buffer.append("\n");
             }
         }
         
         return buffer.toString();
     }
     
     public static IRubyObject createRawBacktrace(Ruby runtime, StackTraceElement[] stackTrace, boolean filter) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             
             if (filter) {
                 if (element.getClassName().startsWith("org.jruby") ||
                         element.getLineNumber() < 0) {
                     continue;
                 }
             }
             RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element));
             traceArray.append(str);
         }
         
         return traceArray;
     }
     
     public static IRubyObject createRubyCompiledBacktrace(Ruby runtime, StackTraceElement[] stackTrace) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             int index = element.getMethodName().indexOf("$RUBY$");
             if (index < 0) continue;
             String unmangledMethod = element.getMethodName().substring(index + 6);
             RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + unmangledMethod + "'");
             traceArray.append(str);
         }
         
         return traceArray;
     }
 
     private Frame pushFrameForBlock(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
 
         // set the binding's frame's "previous" file and line to current, so
         // trace will show who called the block
         f.setFileAndLine(file, line);
         
         setFileAndLine(binding.getFile(), binding.getLine());
         f.setVisibility(binding.getVisibility());
         
         return lastFrame;
     }
 
     private Frame pushFrameForEval(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         setFileAndLine(binding.getFile(), binding.getLine());
         f.setVisibility(binding.getVisibility());
         return lastFrame;
     }
     
     public enum FrameType { METHOD, BLOCK, EVAL, CLASS, ROOT }
     public static final Map<String, FrameType> INTERPRETED_FRAMES = new HashMap<String, FrameType>();
     
     static {
         INTERPRETED_FRAMES.put(InterpretedMethod.class.getName() + ".call", FrameType.METHOD);
         
         INTERPRETED_FRAMES.put(InterpretedBlock.class.getName() + ".evalBlockBody", FrameType.BLOCK);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalWithBinding", FrameType.EVAL);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalSimple", FrameType.EVAL);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalClassDefinitionBody", FrameType.CLASS);
         
         INTERPRETED_FRAMES.put(Ruby.class.getName() + ".runInterpreter", FrameType.ROOT);
     }
     
     public static IRubyObject createRubyHybridBacktrace(Ruby runtime, RubyStackTraceElement[] backtraceFrames, StackTraceElement[] stackTrace, boolean debug) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         ThreadContext context = runtime.getCurrentContext();
         
         int rubyFrameIndex = backtraceFrames.length - 1;
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             
             // look for mangling markers for compiled Ruby in method name
             int index = element.getMethodName().indexOf("$RUBY$");
             if (index >= 0) {
                 String unmangledMethod = element.getMethodName().substring(index + 6);
                 RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + unmangledMethod + "'");
                 traceArray.append(str);
                 
                 // if it's not a rescue or ensure, there's a frame associated, so decrement
                 if (!(element.getMethodName().contains("__rescue__") || element.getMethodName().contains("__ensure__"))) {
                     rubyFrameIndex--;
                 }
                 continue;
             }
             
             // look for __file__ method name for compiled roots
             if (element.getMethodName().equals("__file__")) {
                 RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ": `<toplevel>'");
                 traceArray.append(str);
                 rubyFrameIndex--;
                 continue;
             }
             
             // look for mangling markers for bound, unframed methods in class name
             index = element.getClassName().indexOf("$RUBYINVOKER$");
             if (index >= 0) {
                 // unframed invokers have no Ruby frames, so pull from class name
                 // but use current frame as file and line
                 String unmangledMethod = element.getClassName().substring(index + 13);
                 Frame current = context.frameStack[rubyFrameIndex];
                 RubyString str = RubyString.newString(runtime, current.getFile() + ":" + (current.getLine() + 1) + ":in `" + unmangledMethod + "'");
                 traceArray.append(str);
                 continue;
             }
             
             // look for mangling markers for bound, framed methods in class name
             index = element.getClassName().indexOf("$RUBYFRAMEDINVOKER$");
             if (index >= 0) {
                 // framed invokers will have Ruby frames associated with them
                 addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex - 1], FrameType.METHOD);
                 rubyFrameIndex--;
                 continue;
             }
             
             // try to mine out a Ruby frame using our list of interpreter entry-point markers
             String classMethod = element.getClassName() + "." + element.getMethodName();
             FrameType frameType = INTERPRETED_FRAMES.get(classMethod);
             if (frameType != null) {
                 // Frame matches one of our markers for "interpreted" calls
                 if (rubyFrameIndex == 0) {
                     addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex], frameType);
                 } else {
                     addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex - 1], frameType);
                     rubyFrameIndex--;
                 }
                 continue;
             } else {
                 // Frame is extraneous runtime information, skip it unless debug
                 if (debug) {
                     RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element));
                     traceArray.append(str);
                 }
                 continue;
             }
         }
         
         return traceArray;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(DynamicScope.newDynamicScope(staticScope));
     }
 
     public void preCompiledClassDummyScope(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(staticScope.getDummyScope());
     }
 
     public void postCompiledClass() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preScopeNode(StaticScope staticScope) {
         pushScope(DynamicScope.newDynamicScope(staticScope, getCurrentScope()));
     }
 
     public void postScopeNode() {
         popScope();
     }
 
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
 
         pushScope(DynamicScope.newDynamicScope(staticScope, null));
     }
     
     public void postClassEval() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         LocalStaticScope staticScope = new LocalStaticScope(null);
         staticScope.setVariables(names);
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void preMethodFrameAndDummyScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
 
     public void preMethodNoFrameAndDummyScope(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, Block block) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block);
     }
     
     public void postMethodFrameOnly() {
         popFrame();
         popRubyClass();
     }
     
     public void preMethodScopeOnly(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodScopeOnly() {
         popRubyClass();
         popScope();
     }
     
     public void preMethodBacktraceAndScope(String name, RubyModule clazz, StaticScope staticScope) {
         preMethodScopeOnly(clazz, staticScope);
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
         popFrame();
     }
     
     public void preMethodBacktraceOnly(String name) {
         pushBacktraceFrame(name);
     }
 
     public void preMethodBacktraceDummyScope(RubyModule clazz, String name, StaticScope staticScope) {
         pushBacktraceFrame(name);
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodBacktraceOnly() {
         popFrame();
     }
 
     public void postMethodBacktraceDummyScope() {
         popFrame();
         popRubyClass();
         popScope();
     }
     
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         
         pushRubyClass(objectClass);
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self, String name) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
 
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         DynamicScope scope = getCurrentScope();
         StaticScope sScope = new BlockStaticScope(scope.getStaticScope());
         sScope.setModule(executeUnderClass);
         pushScope(DynamicScope.newDynamicScope(sScope, scope));
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popScope();
         popRubyClass();
     }
     
     public void preMproc() {
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
     }
     
     public void preRunThread(Frame[] currentFrames) {
         for (Frame frame : currentFrames) {
             pushFrame(frame);
         }
         setFileAndLine(getCurrentFrame());
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public Frame preForBlock(Binding binding, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         pushScope(binding.getDynamicScope());
         return lastFrame;
     }
     
     public Frame preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         return lastFrame;
     }
     
     public Frame preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         return lastFrame;
     }
     
     public Frame preYieldNoScope(Binding binding, RubyModule klass) {
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return pushFrameForBlock(binding);
     }
     
     public void preEvalScriptlet(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postEvalScriptlet() {
         popScope();
     }
     
     public Frame preEvalWithBinding(Binding binding) {
         binding.getFrame().setIsBindingFrame(true);
         Frame lastFrame = pushFrameForEval(binding);
         pushRubyClass(binding.getKlass());
         return lastFrame;
     }
     
     public void postEvalWithBinding(Binding binding, Frame lastFrame) {
         binding.getFrame().setIsBindingFrame(false);
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYield(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldLight(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldNoScope(Frame lastFrame) {
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void preScopedBody(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postScopedBody() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Is this thread actively in defined? at the moment.
      *
      * @return true if within defined?
      */
     public boolean isWithinDefined() {
         return isWithinDefined;
     }
     
     /**
      * Set whether we are actively within defined? or not.
      *
      * @param isWithinDefined true if so
      */
     public void setWithinDefined(boolean isWithinDefined) {
         this.isWithinDefined = isWithinDefined;
     }
 
     /**
      * Return a binding representing the current call's state
      * @return the current binding
      */
     public Binding currentBinding() {
         Frame frame = getCurrentFrame();
         return new Binding(frame, getRubyClass(), getCurrentScope(), file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding currentBinding(IRubyObject self) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), getCurrentScope(), file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility and self.
      * @param self the self object to use
      * @param visibility the visibility to use
      * @return the current binding using the specified self and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), getCurrentScope(), file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified scope and self.
      * @param self the self object to use
      * @param visibility the scope to use
      * @return the current binding using the specified self and scope
      */
     public Binding currentBinding(IRubyObject self, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), scope, file, line);
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility, scope, and self. For shared-scope binding
      * consumers like for loops.
      * 
      * @param self the self object to use
      * @param visibility the visibility to use
      * @param scope the scope to use
      * @return the current binding using the specified self, scope, and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), scope, file, line);
     }
 
     /**
      * Return a binding representing the previous call's state
      * @return the current binding
      */
     public Binding previousBinding() {
         Frame frame = getPreviousFrame();
         Frame current = getCurrentFrame();
         return new Binding(frame, getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
     }
 
     /**
      * Return a binding representing the previous call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding previousBinding(IRubyObject self) {
         Frame frame = getPreviousFrame();
         Frame current = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
     }
 }
diff --git a/test/org/jruby/test/TestRubyException.java b/test/org/jruby/test/TestRubyException.java
index b98691f461..3617a4f34d 100644
--- a/test/org/jruby/test/TestRubyException.java
+++ b/test/org/jruby/test/TestRubyException.java
@@ -1,105 +1,95 @@
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
  * Copyright (C) 2005 David Corbin <dcorbin@users.sf.net>
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
 package org.jruby.test;
 
 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.util.ArrayList;
 import java.util.List;
 
 import junit.framework.TestCase;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyException;
 import org.jruby.RubyString;
 
 public class TestRubyException extends TestCase {
 
 	private Ruby interpreter;
 	private RubyException exception;
 
 	public void setUp() {
 		interpreter = Ruby.newInstance();
 		exception = new RubyException(interpreter, interpreter.getClass("StandardError"), "test");
 	}
-
-	public void testPrintBacktraceWithHiddenLevels() throws Exception {
-		setBackTrace(19);
-		
-		String[] lines = printError();
-        
-		assertEquals(expectedTraceLine(1), lines[0]);
-		assertEquals("\t ... 7 levels...", lines[RubyException.TRACE_HEAD]);
-		assertEquals(expectedTraceLine(16), lines[RubyException.TRACE_HEAD + 1]);
-	}
 	
 	public void testPrintBacktrace() throws Exception {
 		setBackTrace(18);
 		
 		String[] lines = printError();
 		
 		assertEquals(expectedTraceLine(1), lines[0]);
 		assertEquals(expectedTraceLine(RubyException.TRACE_HEAD + 1), lines[RubyException.TRACE_HEAD]);
 	}
 
 	public void testPrintNilBacktrace() throws Exception {
 	    exception.set_backtrace(interpreter.getNil());
 		
 		String[] lines = printError();
 		
 		assertEquals(0, lines.length);
 	}
 
 	private String[] printError() {
 		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
 		PrintStream stream = new PrintStream(byteArrayOutputStream);
 		exception.printBacktrace(stream);
 		String output = new String(byteArrayOutputStream.toByteArray());
 		if (output.trim().length() == 0) {
 		    return new String[0];
 	    } else {
 		    return output.split("\n");
         }
 	}
 
 	private void setBackTrace(int lineCount) {
 		List traceLines = new ArrayList();
 		for (int i=0; i<lineCount; i++)
 			traceLines.add(RubyString.newString(interpreter, testLine(i)));
 		exception.set_backtrace(RubyArray.newArray(interpreter, traceLines));
 	}
 	
 	private String expectedTraceLine(int index) {
 		return "\tfrom " + testLine(index);
 	}
 
 	private String testLine(int i) {
 		return "Line " + i;
 	}
 }
