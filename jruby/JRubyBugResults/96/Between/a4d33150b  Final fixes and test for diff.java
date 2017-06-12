diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index 34b3cc277a..8138e86ef3 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,1036 +1,1048 @@
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
  * Copyright (C) 2002 Jason Voegele <jason@jvoegele.com>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
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
 package org.jruby;
 
 import java.io.IOException;
+import java.lang.ref.WeakReference;
 import java.nio.channels.Channel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.WeakHashMap;
 import java.util.HashMap;
 import java.util.Map;
 
 import java.util.Set;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.internal.runtime.FutureThread;
 import org.jruby.internal.runtime.NativeThread;
 import org.jruby.internal.runtime.RubyRunnable;
 import org.jruby.internal.runtime.ThreadLike;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.ExecutionContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import java.util.concurrent.ExecutionException;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.Visibility;
 import org.jruby.util.io.BlockingIO;
 
 /**
  * Implementation of Ruby's <code>Thread</code> class.  Each Ruby thread is
  * mapped to an underlying Java Virtual Machine thread.
  * <p>
  * Thread encapsulates the behavior of a thread of execution, including the main
  * thread of the Ruby script.  In the descriptions that follow, the parameter
  * <code>aSymbol</code> refers to a symbol, which is either a quoted string or a
  * <code>Symbol</code> (such as <code>:name</code>).
  * 
  * Note: For CVS history, see ThreadClass.java.
  */
 @JRubyClass(name="Thread")
 public class RubyThread extends RubyObject implements ExecutionContext {
     private ThreadLike threadImpl;
     private RubyFixnum priority;
     private transient Map<IRubyObject, IRubyObject> threadLocalVariables;
     private final Map<Object, IRubyObject> contextVariables = new WeakHashMap<Object, IRubyObject>();
     private boolean abortOnException;
     private IRubyObject finalResult;
     private RaiseException exitingException;
     private RubyThreadGroup threadGroup;
 
     private final ThreadService threadService;
 
     // Error info is per-thread
     private IRubyObject errorInfo;
+
+    // weak reference to associated ThreadContext
+    private volatile WeakReference<ThreadContext> contextRef;
     
     private static final boolean DEBUG = false;
 
     public static enum Status { RUN, SLEEP, ABORTING, DEAD }
 
     private volatile ThreadService.Event mail;
     private volatile Status status = Status.RUN;
 
     protected RubyThread(Ruby runtime, RubyClass type) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         finalResult = runtime.getNil();
 
         // init errorInfo to nil
         errorInfo = runtime.getNil();
     }
 
     public synchronized void receiveMail(ThreadService.Event event) {
         // if we're already aborting, we can receive no further mail
         if (status == Status.ABORTING) return;
         
         mail = event;
         switch (event.type) {
         case KILL:
             status = Status.ABORTING;
         }
         
         // If this thread is sleeping or stopped, wake it
         notify();
 
         // interrupt the target thread in case it's blocking or waiting
         // WARNING: We no longer interrupt the target thread, since this usually means
         // interrupting IO and with NIO that means the channel is no longer usable.
         // We either need a new way to handle waking a target thread that's waiting
         // on IO, or we need to accept that we can't wake such threads and must wait
         // for them to complete their operation.
         //threadImpl.interrupt();
 
         // new interrupt, to hopefully wake it out of any blocking IO
         this.interrupt();
 
     }
 
     public synchronized void checkMail(ThreadContext context) {
         ThreadService.Event myEvent = mail;
         mail = null;
         if (myEvent != null) {
             switch (myEvent.type) {
             case RAISE:
                 receivedAnException(context, myEvent.exception);
             case KILL:
                 throwThreadKill();
             }
         }
     }
 
     public IRubyObject getErrorInfo() {
         return errorInfo;
     }
 
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         this.errorInfo = errorInfo;
         return errorInfo;
     }
+
+    public void setContext(ThreadContext context) {
+        this.contextRef = new WeakReference<ThreadContext>(context);
+    }
+
+    public ThreadContext getContext() {
+        return contextRef.get();
+    }
     
     /**
      * Dispose of the current thread by removing it from its parent ThreadGroup.
      */
     public void dispose() {
         threadGroup.remove(this);
     }
    
     public static RubyClass createThreadClass(Ruby runtime) {
         // FIXME: In order for Thread to play well with the standard 'new' behavior,
         // it must provide an allocator that can create empty object instances which
         // initialize then fills with appropriate data.
         RubyClass threadClass = runtime.defineClass("Thread", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setThread(threadClass);
 
         threadClass.index = ClassIndex.THREAD;
         threadClass.setReifiedClass(RubyThread.class);
 
         threadClass.defineAnnotatedMethods(RubyThread.class);
 
         RubyThread rubyThread = new RubyThread(runtime, threadClass);
         // TODO: need to isolate the "current" thread from class creation
         rubyThread.threadImpl = new NativeThread(rubyThread, Thread.currentThread());
         runtime.getThreadService().setMainThread(Thread.currentThread(), rubyThread);
         
         // set to default thread group
         runtime.getDefaultThreadGroup().addDirectly(rubyThread);
         
         threadClass.setMarshal(ObjectMarshal.NOT_MARSHALABLE_MARSHAL);
         
         return threadClass;
     }
 
     /**
      * <code>Thread.new</code>
      * <p>
      * Thread.new( <i>[ arg ]*</i> ) {| args | block } -> aThread
      * <p>
      * Creates a new thread to execute the instructions given in block, and
      * begins running it. Any arguments passed to Thread.new are passed into the
      * block.
      * <pre>
      * x = Thread.new { sleep .1; print "x"; print "y"; print "z" }
      * a = Thread.new { print "a"; print "b"; sleep .2; print "c" }
      * x.join # Let the threads finish before
      * a.join # main thread exits...
      * </pre>
      * <i>produces:</i> abxyzc
      */
     @JRubyMethod(name = {"new", "fork"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, true, block);
     }
 
     /**
      * Basically the same as Thread.new . However, if class Thread is
      * subclassed, then calling start in that subclass will not invoke the
      * subclass's initialize method.
      */
     @JRubyMethod(name = "start", rest = true, frame = true, meta = true)
     public static RubyThread start(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, false, block);
     }
     
     public static RubyThread adopt(IRubyObject recv, Thread t) {
         return adoptThread(recv, t, Block.NULL_BLOCK);
     }
 
     private static RubyThread adoptThread(final IRubyObject recv, Thread t, Block block) {
         final Ruby runtime = recv.getRuntime();
         final RubyThread rubyThread = new RubyThread(runtime, (RubyClass) recv);
         
         rubyThread.threadImpl = new NativeThread(rubyThread, t);
         ThreadContext context = runtime.getThreadService().registerNewThread(rubyThread);
         runtime.getThreadService().associateThread(t, rubyThread);
         
         context.preAdoptThread();
         
         // set to default thread group
         runtime.getDefaultThreadGroup().addDirectly(rubyThread);
         
         return rubyThread;
     }
     
     @JRubyMethod(name = "initialize", rest = true, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = getRuntime();
         if (!block.isGiven()) throw runtime.newThreadError("must be called with a block");
 
         try {
             RubyRunnable runnable = new RubyRunnable(this, args, context.getFrames(0), block);
             if (RubyInstanceConfig.POOLING_ENABLED) {
                 FutureThread futureThread = new FutureThread(this, runnable);
                 threadImpl = futureThread;
 
                 addToCorrectThreadGroup(context);
 
                 threadImpl.start();
 
                 // JRUBY-2380, associate future early so it shows up in Thread.list right away, in case it doesn't run immediately
                 runtime.getThreadService().associateThread(futureThread.getFuture(), this);
             } else {
                 Thread thread = new Thread(runnable);
                 thread.setDaemon(true);
                 threadImpl = new NativeThread(this, thread);
             
                 addToCorrectThreadGroup(context);
 
                 threadImpl.start();
 
                 // JRUBY-2380, associate thread early so it shows up in Thread.list right away, in case it doesn't run immediately
                 runtime.getThreadService().associateThread(thread, this);
             }
 
             // We yield here to hopefully permit the target thread to schedule
             // MRI immediately schedules it, so this is close but not exact
             Thread.yield();
         
             return this;
         } catch (SecurityException ex) {
           throw runtime.newThreadError(ex.getMessage());
         }
     }
     
     private static RubyThread startThread(final IRubyObject recv, final IRubyObject[] args, boolean callInit, Block block) {
         RubyThread rubyThread = new RubyThread(recv.getRuntime(), (RubyClass) recv);
         
         if (callInit) {
             rubyThread.callInit(args, block);
         } else {
             // for Thread::start, which does not call the subclass's initialize
             rubyThread.initialize(recv.getRuntime().getCurrentContext(), args, block);
         }
         
         return rubyThread;
     }
     
     public synchronized void cleanTerminate(IRubyObject result) {
         finalResult = result;
     }
 
     public synchronized void beDead() {
         status = status.DEAD;
         try {
             if (selector != null) selector.close();
         } catch (IOException ioe) {
         }
     }
 
     public void pollThreadEvents() {
         pollThreadEvents(getRuntime().getCurrentContext());
     }
     
     public void pollThreadEvents(ThreadContext context) {
         if (mail != null) checkMail(context);
     }
     
     private static void throwThreadKill() {
         throw new ThreadKill();
     }
 
     /**
      * Returns the status of the global ``abort on exception'' condition. The
      * default is false. When set to true, will cause all threads to abort (the
      * process will exit(0)) if an exception is raised in any thread. See also
      * Thread.abort_on_exception= .
      */
     @JRubyMethod(name = "abort_on_exception", meta = true)
     public static RubyBoolean abort_on_exception_x(IRubyObject recv) {
     	Ruby runtime = recv.getRuntime();
         return runtime.isGlobalAbortOnExceptionEnabled() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "abort_on_exception=", required = 1, meta = true)
     public static IRubyObject abort_on_exception_set_x(IRubyObject recv, IRubyObject value) {
         recv.getRuntime().setGlobalAbortOnExceptionEnabled(value.isTrue());
         return value;
     }
 
     @JRubyMethod(name = "current", meta = true)
     public static RubyThread current(IRubyObject recv) {
         return recv.getRuntime().getCurrentContext().getThread();
     }
 
     @JRubyMethod(name = "main", meta = true)
     public static RubyThread main(IRubyObject recv) {
         return recv.getRuntime().getThreadService().getMainThread();
     }
 
     @JRubyMethod(name = "pass", meta = true)
     public static IRubyObject pass(IRubyObject recv) {
         Ruby runtime = recv.getRuntime();
         ThreadService ts = runtime.getThreadService();
         boolean critical = ts.getCritical();
         
         ts.setCritical(false);
         
         Thread.yield();
         
         ts.setCritical(critical);
         
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "list", meta = true)
     public static RubyArray list(IRubyObject recv) {
     	RubyThread[] activeThreads = recv.getRuntime().getThreadService().getActiveRubyThreads();
         
         return recv.getRuntime().newArrayNoCopy(activeThreads);
     }
 
     private void addToCorrectThreadGroup(ThreadContext context) {
         // JRUBY-3568, inherit threadgroup or use default
         IRubyObject group = context.getThread().group();
         if (!group.isNil()) {
             ((RubyThreadGroup) group).addDirectly(this);
         } else {
             context.getRuntime().getDefaultThreadGroup().addDirectly(this);
         }
     }
     
     private IRubyObject getSymbolKey(IRubyObject originalKey) {
         if (originalKey instanceof RubySymbol) {
             return originalKey;
         } else if (originalKey instanceof RubyString) {
             return getRuntime().newSymbol(originalKey.asJavaString());
         } else if (originalKey instanceof RubyFixnum) {
             getRuntime().getWarnings().warn(ID.FIXNUMS_NOT_SYMBOLS, "Do not use Fixnums as Symbols");
             throw getRuntime().newArgumentError(originalKey + " is not a symbol");
         } else {
             throw getRuntime().newTypeError(originalKey + " is not a symbol");
         }
     }
     
     private synchronized Map<IRubyObject, IRubyObject> getThreadLocals() {
         if (threadLocalVariables == null) {
             threadLocalVariables = new HashMap<IRubyObject, IRubyObject>();
         }
         return threadLocalVariables;
     }
 
     public final Map<Object, IRubyObject> getContextVariables() {
         return contextVariables;
     }
 
     public boolean isAlive(){
         return threadImpl.isAlive() && status != Status.ABORTING;
     }
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject op_aref(IRubyObject key) {
         IRubyObject value;
         if ((value = getThreadLocals().get(getSymbolKey(key))) != null) {
             return value;
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
         key = getSymbolKey(key);
         
         getThreadLocals().put(key, value);
         return value;
     }
 
     @JRubyMethod(name = "abort_on_exception")
     public RubyBoolean abort_on_exception() {
         return abortOnException ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "abort_on_exception=", required = 1)
     public IRubyObject abort_on_exception_set(IRubyObject val) {
         abortOnException = val.isTrue();
         return val;
     }
 
     @JRubyMethod(name = "alive?")
     public RubyBoolean alive_p() {
         return isAlive() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "join", optional = 1, backtrace = true)
     public IRubyObject join(IRubyObject[] args) {
         long timeoutMillis = Long.MAX_VALUE;
         if (args.length > 0) {
             if (args.length > 1) {
                 throw getRuntime().newArgumentError(args.length,1);
             }
             // MRI behavior: value given in seconds; converted to Float; less
             // than or equal to zero returns immediately; returns nil
             timeoutMillis = (long)(1000.0D * args[0].convertToFloat().getValue());
             if (timeoutMillis <= 0) {
 	        // TODO: not sure that we should skip calling join() altogether.
 		// Thread.join() has some implications for Java Memory Model, etc.
 	        if (threadImpl.isAlive()) {
 		   return getRuntime().getNil();
 		} else {   
                    return this;
 		}
             }
         }
         if (isCurrent()) {
             throw getRuntime().newThreadError("thread tried to join itself");
         }
         try {
             if (threadService.getCritical()) {
                 // If the target thread is sleeping or stopped, wake it
                 synchronized (this) {
                     notify();
                 }
                 
                 // interrupt the target thread in case it's blocking or waiting
                 // WARNING: We no longer interrupt the target thread, since this usually means
                 // interrupting IO and with NIO that means the channel is no longer usable.
                 // We either need a new way to handle waking a target thread that's waiting
                 // on IO, or we need to accept that we can't wake such threads and must wait
                 // for them to complete their operation.
                 //threadImpl.interrupt();
             }
 
             RubyThread currentThread = getRuntime().getCurrentContext().getThread();
             final long timeToWait = Math.min(timeoutMillis, 200);
 
             // We need this loop in order to be able to "unblock" the
             // join call without actually calling interrupt.
             long start = System.currentTimeMillis();
             while(true) {
                 currentThread.pollThreadEvents();
                 threadImpl.join(timeToWait);
                 if (!threadImpl.isAlive()) {
                     break;
                 }
                 if (System.currentTimeMillis() - start > timeoutMillis) {
                     break;
                 }
             }
         } catch (InterruptedException ie) {
             ie.printStackTrace();
             assert false : ie;
         } catch (ExecutionException ie) {
             ie.printStackTrace();
             assert false : ie;
         }
 
         if (exitingException != null) {
             throw exitingException;
         }
 
         if (threadImpl.isAlive()) {
             return getRuntime().getNil();
         } else {
             return this;
 	}
     }
 
     @JRubyMethod(name = "value", frame = true)
     public IRubyObject value() {
         join(new IRubyObject[0]);
         synchronized (this) {
             return finalResult;
         }
     }
 
     @JRubyMethod(name = "group")
     public IRubyObject group() {
         if (threadGroup == null) {
         	return getRuntime().getNil();
         }
         
         return threadGroup;
     }
     
     void setThreadGroup(RubyThreadGroup rubyThreadGroup) {
     	threadGroup = rubyThreadGroup;
     }
     
     @JRubyMethod(name = "inspect")
     @Override
     public synchronized IRubyObject inspect() {
         // FIXME: There's some code duplication here with RubyObject#inspect
         StringBuilder part = new StringBuilder();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(System.identityHashCode(this)));
         part.append(status.toString().toLowerCase());
         part.append(">");
         return getRuntime().newString(part.toString());
     }
 
     @JRubyMethod(name = "key?", required = 1)
     public RubyBoolean key_p(IRubyObject key) {
         key = getSymbolKey(key);
         
         return getRuntime().newBoolean(getThreadLocals().containsKey(key));
     }
 
     @JRubyMethod(name = "keys")
     public RubyArray keys() {
         IRubyObject[] keys = new IRubyObject[getThreadLocals().size()];
         
         return RubyArray.newArrayNoCopy(getRuntime(), getThreadLocals().keySet().toArray(keys));
     }
     
     @JRubyMethod(name = "critical=", required = 1, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject critical_set(IRubyObject receiver, IRubyObject value) {
     	receiver.getRuntime().getThreadService().setCritical(value.isTrue());
     	
     	return value;
     }
 
     @JRubyMethod(name = "critical", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject critical(IRubyObject receiver) {
     	return receiver.getRuntime().newBoolean(receiver.getRuntime().getThreadService().getCritical());
     }
     
     @JRubyMethod(name = "stop", meta = true)
     public static IRubyObject stop(ThreadContext context, IRubyObject receiver) {
         RubyThread rubyThread = context.getThread();
         
         synchronized (rubyThread) {
             rubyThread.checkMail(context);
             try {
                 // attempt to decriticalize all if we're the critical thread
                 receiver.getRuntime().getThreadService().setCritical(false);
 
                 rubyThread.status = Status.SLEEP;
                 rubyThread.wait();
             } catch (InterruptedException ie) {
                 rubyThread.checkMail(context);
                 rubyThread.status = Status.RUN;
             }
         }
         
         return receiver.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "kill", required = 1, frame = true, meta = true)
     public static IRubyObject kill(IRubyObject receiver, IRubyObject rubyThread, Block block) {
         if (!(rubyThread instanceof RubyThread)) throw receiver.getRuntime().newTypeError(rubyThread, receiver.getRuntime().getThread());
         return ((RubyThread)rubyThread).kill();
     }
     
     @JRubyMethod(name = "exit", frame = true, meta = true)
     public static IRubyObject s_exit(IRubyObject receiver, Block block) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
 
         synchronized (rubyThread) {
             rubyThread.status = Status.ABORTING;
             rubyThread.mail = null;
             receiver.getRuntime().getThreadService().setCritical(false);
             throw new ThreadKill();
         }
     }
 
     @JRubyMethod(name = "stop?")
     public RubyBoolean stop_p() {
     	// not valid for "dead" state
     	return getRuntime().newBoolean(status == Status.SLEEP || status == Status.DEAD);
     }
     
     @JRubyMethod(name = "wakeup")
     public synchronized RubyThread wakeup() {
         if(!threadImpl.isAlive() && status == Status.DEAD) {
             throw getRuntime().newThreadError("killed thread");
         }
 
         status = Status.RUN;
         notifyAll();
     	
     	return this;
     }
     
     @JRubyMethod(name = "priority")
     public RubyFixnum priority() {
         return priority;
     }
 
     @JRubyMethod(name = "priority=", required = 1)
     public IRubyObject priority_set(IRubyObject priority) {
         // FIXME: This should probably do some translation from Ruby priority levels to Java priority levels (until we have green threads)
         int iPriority = RubyNumeric.fix2int(priority);
         
         if (iPriority < Thread.MIN_PRIORITY) {
             iPriority = Thread.MIN_PRIORITY;
         } else if (iPriority > Thread.MAX_PRIORITY) {
             iPriority = Thread.MAX_PRIORITY;
         }
         
         this.priority = RubyFixnum.newFixnum(getRuntime(), iPriority);
         
         if (threadImpl.isAlive()) {
             threadImpl.setPriority(iPriority);
         }
         return this.priority;
     }
 
     @JRubyMethod(name = "raise", optional = 3, frame = true)
     public IRubyObject raise(IRubyObject[] args, Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         if (this == context.getThread()) {
             return RubyKernel.raise(context, runtime.getKernel(), args, block);
         }
         
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before raising");
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " raising");
         IRubyObject exception = prepareRaiseException(runtime, args, block);
 
         runtime.getThreadService().deliverEvent(new ThreadService.Event(currentThread, this, ThreadService.Event.Type.RAISE, exception));
 
         return this;
     }
 
     /**
      * This is intended to be used to raise exceptions in Ruby threads from non-
      * Ruby threads like Timeout's thread.
      * 
      * @param args Same args as for Thread#raise
      * @param block Same as for Thread#raise
      */
     public void internalRaise(IRubyObject[] args) {
         Ruby runtime = getRuntime();
 
         IRubyObject exception = prepareRaiseException(runtime, args, Block.NULL_BLOCK);
 
         receiveMail(new ThreadService.Event(this, this, ThreadService.Event.Type.RAISE, exception));
     }
 
     private IRubyObject prepareRaiseException(Ruby runtime, IRubyObject[] args, Block block) {
         if(args.length == 0) {
             IRubyObject lastException = errorInfo;
             if(lastException.isNil()) {
                 return new RaiseException(runtime, runtime.getRuntimeError(), "", false).getException();
             } 
             return lastException;
         }
 
         IRubyObject exception;
         ThreadContext context = getRuntime().getCurrentContext();
         
         if(args.length == 1) {
             if(args[0] instanceof RubyString) {
                 return runtime.getRuntimeError().newInstance(context, args, block);
             }
             
             if(!args[0].respondsTo("exception")) {
                 return runtime.newTypeError("exception class/object expected").getException();
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 return runtime.newTypeError("exception class/object expected").getException();
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!runtime.getException().isInstance(exception)) {
             return runtime.newTypeError("exception object expected").getException();
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         return exception;
     }
     
     @JRubyMethod(name = "run")
     public synchronized IRubyObject run() {
         return wakeup();
     }
 
     /**
      * We can never be sure if a wait will finish because of a Java "spurious wakeup".  So if we
      * explicitly wakeup and we wait less than requested amount we will return false.  We will
      * return true if we sleep right amount or less than right amount via spurious wakeup.
      */
     public synchronized boolean sleep(long millis) throws InterruptedException {
         assert this == getRuntime().getCurrentContext().getThread();
         boolean result = true;
 
         synchronized (this) {
             pollThreadEvents();
             try {
                 status = Status.SLEEP;
                 wait(millis);
             } finally {
                 result = (status != Status.RUN);
                 pollThreadEvents();
                 status = Status.RUN;
             }
         }
 
         return result;
     }
 
     @JRubyMethod(name = "status")
     public synchronized IRubyObject status() {
         if (threadImpl.isAlive()) {
             // TODO: no java stringity
             return getRuntime().newString(status.toString().toLowerCase());
         } else if (exitingException != null) {
             return getRuntime().getNil();
         } else {
             return getRuntime().getFalse();
         }
     }
 
     public void enterSleep() {
         status = Status.SLEEP;
     }
 
     public void exitSleep() {
         status = Status.RUN;
     }
 
     @JRubyMethod(name = {"kill", "exit", "terminate"})
     public IRubyObject kill() {
     	// need to reexamine this
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         
         // If the killee thread is the same as the killer thread, just die
         if (currentThread == this) throwThreadKill();
         
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " trying to kill");
 
         currentThread.pollThreadEvents();
 
         getRuntime().getThreadService().deliverEvent(new ThreadService.Event(currentThread, this, ThreadService.Event.Type.KILL));
         
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " succeeded with kill");
         
         return this;
     }
     
     @JRubyMethod(name = {"kill!", "exit!", "terminate!"})
     public IRubyObject kill_bang() {
         throw getRuntime().newNotImplementedError("Thread#kill!, exit!, and terminate! are not safe and not supported");
     }
     
     @JRubyMethod(name = "safe_level")
     public IRubyObject safe_level() {
         throw getRuntime().newNotImplementedError("Thread-specific SAFE levels are not supported");
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject backtrace(ThreadContext context) {
         return context.createCallerBacktrace(context.getRuntime(), 0);
     }
 
     private boolean isCurrent() {
         return threadImpl.isCurrent();
     }
 
     public void exceptionRaised(RaiseException exception) {
         assert isCurrent();
 
         RubyException rubyException = exception.getException();
         Ruby runtime = rubyException.getRuntime();
         if (runtime.getSystemExit().isInstance(rubyException)) {
             threadService.getMainThread().raise(new IRubyObject[] {rubyException}, Block.NULL_BLOCK);
         } else if (abortOnException(runtime)) {
             runtime.printError(rubyException);
             RubyException systemExit = RubySystemExit.newInstance(runtime, 1);
             systemExit.message = rubyException.message;
             systemExit.set_backtrace(rubyException.backtrace());
             threadService.getMainThread().raise(new IRubyObject[] {systemExit}, Block.NULL_BLOCK);
             return;
         } else if (runtime.getDebug().isTrue()) {
             runtime.printError(exception.getException());
         }
         exitingException = exception;
     }
 
     private boolean abortOnException(Ruby runtime) {
         return (runtime.isGlobalAbortOnExceptionEnabled() || abortOnException);
     }
 
     public static RubyThread mainThread(IRubyObject receiver) {
         return receiver.getRuntime().getThreadService().getMainThread();
     }
     
     private volatile Selector currentSelector;
     private volatile Object currentWaitObject;
     
     @Deprecated
     public boolean selectForAccept(RubyIO io) {
         return select(io, SelectionKey.OP_ACCEPT);
     }
 
     private volatile Selector selector;
 
     private synchronized Selector getSelector(SelectableChannel channel) throws IOException {
         if (selector == null) selector = Selector.open();
         return selector;
     }
     
     public boolean select(RubyIO io, int ops) {
         return select(io.getChannel(), io, ops);
     }
 
     public boolean select(Channel channel, RubyIO io, int ops) {
         if (channel instanceof SelectableChannel) {
             SelectableChannel selectable = (SelectableChannel)channel;
             
             synchronized (selectable.blockingLock()) {
                 boolean oldBlocking = selectable.isBlocking();
 
                 SelectionKey key = null;
                 try {
                     selectable.configureBlocking(false);
                     
                     if (io != null) io.addBlockingThread(this);
                     currentSelector = getSelector(selectable);
 
                     key = selectable.register(currentSelector, ops);
 
                     beforeBlockingCall();
                     int result = currentSelector.select();
 
                     // check for thread events, in case we've been woken up to die
                     pollThreadEvents();
 
                     if (result == 1) {
                         Set<SelectionKey> keySet = currentSelector.selectedKeys();
 
                         if (keySet.iterator().next() == key) {
                             return true;
                         }
                     }
 
                     return false;
                 } catch (IOException ioe) {
                     throw getRuntime().newRuntimeError("Error with selector: " + ioe);
                 } finally {
                     try {
                         if (key != null) {
                             key.cancel();
                             currentSelector.selectNow();
                         }
                         afterBlockingCall();
                         currentSelector = null;
                         if (io != null) io.removeBlockingThread(this);
                         selectable.configureBlocking(oldBlocking);
                     } catch (IOException ioe) {
                         // ignore; I don't like doing it, but it seems like we
                         // really just need to make all channels non-blocking by
                         // default and use select when implementing blocking ops,
                         // so if this remains set non-blocking, perhaps it's not
                         // such a big deal...
                     }
                 }
             }
         } else {
             // can't select, just have to do a blocking call
             return true;
         }
     }
     
     public void interrupt() {
         Selector activeSelector = currentSelector;
         if (activeSelector != null) {
             activeSelector.wakeup();
         }
         BlockingIO.Condition iowait = blockingIO;
         if (iowait != null) {
             iowait.cancel();
         }
         Object object = currentWaitObject;
         if (object != null) {
             synchronized (object) {
                 object.notify();
             }
         }
     }
     private volatile BlockingIO.Condition blockingIO = null;
     public boolean waitForIO(ThreadContext context, RubyIO io, int ops) {
         Channel channel = io.getChannel();
 
         if (!(channel instanceof SelectableChannel)) {
             return true;
         }
         try {
             io.addBlockingThread(this);
             blockingIO = BlockingIO.newCondition(channel, ops);
             boolean ready = blockingIO.await();
             
             // check for thread events, in case we've been woken up to die
             pollThreadEvents();
             return ready;
         } catch (IOException ioe) {
             throw context.getRuntime().newRuntimeError("Error with selector: " + ioe);
         } catch (InterruptedException ex) {
             // FIXME: not correct exception
             throw context.getRuntime().newRuntimeError("Interrupted");
         } finally {
             blockingIO = null;
             io.removeBlockingThread(this);
         }
     }
     public void beforeBlockingCall() {
         enterSleep();
     }
     
     public void afterBlockingCall() {
         exitSleep();
     }
 
     private void receivedAnException(ThreadContext context, IRubyObject exception) {
         RubyModule kernelModule = getRuntime().getKernel();
         if (DEBUG) {
             System.out.println("thread " + Thread.currentThread() + " before propagating exception: " + status);
         }
         kernelModule.callMethod(context, "raise", exception);
     }
 
     public boolean wait_timeout(IRubyObject o, Double timeout) throws InterruptedException {
         if ( timeout != null ) {
             long delay_ns = (long)(timeout * 1000000000.0);
             long start_ns = System.nanoTime();
             if (delay_ns > 0) {
                 long delay_ms = delay_ns / 1000000;
                 int delay_ns_remainder = (int)( delay_ns % 1000000 );
                 try {
                     currentWaitObject = o;
                     status = Status.SLEEP;
                     o.wait(delay_ms, delay_ns_remainder);
                 } finally {
                     pollThreadEvents();
                     status = Status.RUN;
                     currentWaitObject = null;
                 }
             }
             long end_ns = System.nanoTime();
             return ( end_ns - start_ns ) <= delay_ns;
         } else {
             try {
                 currentWaitObject = o;
                 status = Status.SLEEP;
                 o.wait();
             } finally {
                 pollThreadEvents();
                 status = Status.RUN;
                 currentWaitObject = null;
             }
             return true;
         }
     }
 
     @Override
     public boolean equals(Object obj) {
         if (obj == null) {
             return false;
         }
         if (getClass() != obj.getClass()) {
             return false;
         }
         final RubyThread other = (RubyThread)obj;
         if (this.threadImpl != other.threadImpl && (this.threadImpl == null || !this.threadImpl.equals(other.threadImpl))) {
             return false;
         }
         return true;
     }
 
     @Override
     public int hashCode() {
         int hash = 3;
         hash = 97 * hash + (this.threadImpl != null ? this.threadImpl.hashCode() : 0);
         return hash;
     }
 
     @Override
     public String toString() {
         return threadImpl.toString();
     }
 }
diff --git a/src/org/jruby/internal/runtime/RubyThreadMap.java b/src/org/jruby/internal/runtime/RubyThreadMap.java
deleted file mode 100644
index ea152c6d60..0000000000
--- a/src/org/jruby/internal/runtime/RubyThreadMap.java
+++ /dev/null
@@ -1,124 +0,0 @@
-/***** BEGIN LICENSE BLOCK *****
- * Version: CPL 1.0/GPL 2.0/LGPL 2.1
- *
- * The contents of this file are subject to the Common Public
- * License Version 1.0 (the "License"); you may not use this file
- * except in compliance with the License. You may obtain a copy of
- * the License at http://www.eclipse.org/legal/cpl-v10.html
- *
- * Software distributed under the License is distributed on an "AS
- * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
- * implied. See the License for the specific language governing
- * rights and limitations under the License.
- *
- * Copyright (C) 2010 Charles O Nutter <headius@headius.com>
- *
- * Alternatively, the contents of this file may be used under the terms of
- * either of the GNU General Public License Version 2 or later (the "GPL"),
- * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
- * in which case the provisions of the GPL or the LGPL are applicable instead
- * of those above. If you wish to allow use of your version of this file only
- * under the terms of either the GPL or the LGPL, and not to allow others to
- * use your version of this file under the terms of the CPL, indicate your
- * decision by deleting the provisions above and replace them with the notice
- * and other provisions required by the GPL or the LGPL. If you do not delete
- * the provisions above, a recipient may use your version of this file under
- * the terms of any one of the CPL, the GPL or the LGPL.
- ***** END LICENSE BLOCK *****/
-package org.jruby.internal.runtime;
-
-import java.lang.ref.ReferenceQueue;
-import java.lang.ref.WeakReference;
-import java.util.Hashtable;
-import java.util.Map;
-import java.util.Set;
-import org.jruby.RubyThread;
-import org.jruby.runtime.ThreadContext;
-
-public class RubyThreadMap {
-    /** For null keys, to mimic WeakHashMap */
-    private static final Object NULL_KEY = new Object();
-
-    private final Map<RubyThreadWeakReference<Object>, RubyThread> map = new Hashtable();
-    private final ReferenceQueue<Object> queue = new ReferenceQueue();
-    private final Map<RubyThread, ThreadContext> mapToClean;
-
-    public RubyThreadMap(Map<RubyThread, ThreadContext> mapToClean) {
-        this.mapToClean = mapToClean;
-    }
-
-    public static class RubyThreadWeakReference<T> extends WeakReference<T> {
-        private final RubyThread thread;
-        public int hashCode;
-        public RubyThreadWeakReference(T referrent, RubyThread thread) {
-            super(referrent);
-            hashCode = referrent.hashCode();
-            this.thread = thread;
-        }
-        public RubyThreadWeakReference(T referrent, ReferenceQueue<? super T> queue, RubyThread thread) {
-            super(referrent, queue);
-            hashCode = referrent.hashCode();
-            this.thread = thread;
-        }
-        public RubyThread getThread() {
-            return thread;
-        }
-        @Override
-        public int hashCode() {
-            return hashCode;
-        }
-        @Override
-        public boolean equals(Object other) {
-            Object myKey = get();
-            if (other instanceof RubyThreadWeakReference) {
-                Object otherKey = ((RubyThreadWeakReference)other).get();
-                if (myKey != otherKey) return false;
-                return true;
-            } else if (other instanceof Thread) {
-                return myKey == other;
-            } else {
-                return false;
-            }
-        }
-    }
-
-    private void cleanup() {
-        RubyThreadWeakReference<Object> ref;
-        while ((ref = (RubyThreadWeakReference<Object>) queue.poll()) != null) {
-            map.remove(ref);
-            mapToClean.remove(ref.getThread());
-        }
-    }
-
-    public int size() {
-        cleanup();
-        return map.size();
-    }
-
-    public Set<Map.Entry<RubyThreadWeakReference<Object>, RubyThread>> entrySet() {
-        return map.entrySet();
-    }
-
-    public RubyThread get(Object key) {
-        cleanup();
-        key = nullKey(key);
-        return map.get(key);
-    }
-
-    public RubyThread put(Object key, RubyThread value) {
-        cleanup();
-        key = nullKey(key);
-        return map.put(new RubyThreadWeakReference(key, queue, value), value);
-    }
-
-    public RubyThread remove(Object key) {
-        cleanup();
-        key = nullKey(key);
-        RubyThread t = map.remove(key);
-        return t;
-    }
-
-    private Object nullKey(Object key) {
-        return key == null ? NULL_KEY : key;
-    }
-}
diff --git a/src/org/jruby/internal/runtime/ThreadService.java b/src/org/jruby/internal/runtime/ThreadService.java
index fdf8e64fc4..75a389dd3d 100644
--- a/src/org/jruby/internal/runtime/ThreadService.java
+++ b/src/org/jruby/internal/runtime/ThreadService.java
@@ -1,277 +1,339 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby.internal.runtime;
 
 import java.lang.ref.SoftReference;
 import java.util.concurrent.locks.ReentrantLock;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 
 import java.util.WeakHashMap;
 import java.util.concurrent.Future;
 import org.jruby.Ruby;
 import org.jruby.RubyThread;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.ThreadContext;
 
+/**
+ * ThreadService maintains lists ofall the JRuby-specific thread data structures
+ * needed for Ruby's threading API and for JRuby's execution. The main
+ * structures are:
+ *
+ * <ul>
+ * <li>ThreadContext, which contains frames, scopes, etc needed for Ruby execution</li>
+ * <li>RubyThread, the Ruby object representation of a thread's state</li>
+ * <li>RubyThreadGroup, which represents a group of Ruby threads</li>
+ * </ul>
+ *
+ * In order to ensure these structures do not linger after the thread has terminated,
+ * most of them are either weakly or softly referenced. The references associated
+ * with these structures are:
+ *
+ * <ul>
+ * <li>ThreadService has a hard reference to a ThreadLocal, which holds a soft reference
+ * to a ThreadContext. So the thread's locals softly reference ThreadContext.
+ * We use a soft reference to keep ThreadContext instances from going away too
+ * quickly when a Java thread leaves Ruby space completely, which would otherwise
+ * result in a lot of ThreadContext object churn.</li>
+ * <li>ThreadService maintains a weak map from the actual java.lang.Thread (or
+ * java.util.concurrent.Future) instance to the associated RubyThread. @see org.jruby.internal.runtime.RubyThreadMap.
+ * Because this map is weak-valued rather than weak-keyed, it may hold Thread/Future
+ * references beyond their useful lifetime, but they will get cleaned when the
+ * map is accessed again.</li>
+ * <li>RubyThread has a weak reference to its to ThreadContext.</li>
+ * <li>ThreadContext has a hard reference to its associated RubyThread. Ignoring other
+ * resources, this will usually mean RubyThread is softly reachable via the
+ * soft threadlocal reference to ThreadContext in ThreadService, but weakly
+ * referenced otherwise.</li>
+ * <li>RubyThreadGroup has hard references to threads it owns. The thread removes
+ * itself on termination (if it's a Ruby thread) or when the ThreadContext is
+ * collected (as in the case of "adopted" Java threads.</li>
+ * </ul>
+ *
+ * These data structures can come to life in one of two ways:
+ *
+ * <ul>
+ * <li>A Ruby thread is started. This constructs a new RubyThread object, which
+ * calls to ThreadService to initialize a ThreadContext and appropriate mappings
+ * in all ThreadService's structures. The body of the thread is wrapped with a
+ * finally block that will forcibly unregister the thread and all related
+ * structures from ThreadService.</li>
+ * <li>A Java thread enters Ruby by doing a call. The thread is "adopted", and
+ * gains a RubyThread instance, a ThreadContext instance, and all associated
+ * mappings in ThreadService. Since we don't know when the thread has "left"
+ * Ruby permanently, no forcible unregistration is attempted for the various
+ * structures and maps. However, they should not be hard-rooted; the
+ * ThreadContext is only softly reachable at best if no calls are in-flight,
+ * so it will collect. Its collection will release the reference to RubyThread,
+ * and its finalizer will unregister that RubyThread from its RubyThreadGroup.
+ * With the RubyThread gone, the Thread-to-RubyThread map will eventually clear,
+ * releasing the hard reference to the Thread itself.</li>
+ * <ul>
+ */
 public class ThreadService {
     private Ruby runtime;
+    /**
+     * A hard reference to the "main" context, so we always have one waiting for
+     * "main" thread execution.
+     */
     private ThreadContext mainContext;
+
+    /**
+     * A thread-local soft reference to the current thread's ThreadContext. We
+     * use a soft reference so that the ThreadContext is still collectible but
+     * will not immediately disappear once dereferenced, to avoid churning
+     * through ThreadContext instances every time a Java thread enters and exits
+     * Ruby space.
+     */
     private ThreadLocal<SoftReference<ThreadContext>> localContext;
+
+    /**
+     * The Java thread group into which we register all Ruby threads. This is
+     * distinct from the RubyThreadGroup, which is simply a mutable collection
+     * of threads.
+     */
     private ThreadGroup rubyThreadGroup;
 
-    private RubyThreadMap rubyThreadMap;
-    private Map<RubyThread,ThreadContext> threadContextMap;
+    /**
+     * A map from a Java Thread or Future to its RubyThread instance. This map
+     * is weak-valued, so it will not prevent collection of the RubyThread. It
+     * lazily cleans out dead entries, so it may hold references to Threads or
+     * Futures longer then their useful lifetime.
+     */
+    private final Map<Object, RubyThread> rubyThreadMap;
     
-    private ReentrantLock criticalLock = new ReentrantLock();
+    private final ReentrantLock criticalLock = new ReentrantLock();
 
     public ThreadService(Ruby runtime) {
         this.runtime = runtime;
         this.mainContext = ThreadContext.newContext(runtime);
         this.localContext = new ThreadLocal<SoftReference<ThreadContext>>();
 
         try {
             this.rubyThreadGroup = new ThreadGroup("Ruby Threads#" + runtime.hashCode());
         } catch(SecurityException e) {
             this.rubyThreadGroup = Thread.currentThread().getThreadGroup();
         }
 
-        this.threadContextMap = Collections.synchronizedMap(new WeakHashMap<RubyThread, ThreadContext>());
-        this.rubyThreadMap = new RubyThreadMap(threadContextMap);
+        this.rubyThreadMap = Collections.synchronizedMap(new WeakHashMap<Object, RubyThread>());
         
         // Must be called from main thread (it is currently, but this bothers me)
         localContext.set(new SoftReference<ThreadContext>(mainContext));
     }
 
     public void disposeCurrentThread() {
-        RubyThread thread = getCurrentContext().getThread();
-        threadContextMap.remove(thread);
         localContext.set(null);
         rubyThreadMap.remove(Thread.currentThread());
     }
 
     /**
      * In order to provide an appropriate execution context for a given thread,
      * we store ThreadContext instances in a threadlocal. This method is a utility
      * to get at that threadlocal context from anywhere in the program it may
      * not be immediately available. This method should be used sparingly, and
      * if it is possible to pass ThreadContext on the argument list, it is
      * preferable.
      * 
      * <b>Description of behavior</b>
      * 
      * The threadlocal does not actually contain the ThreadContext directly;
      * instead, it contains a SoftReference that holds the ThreadContext. This
      * is to allow new threads to enter the system and execute Ruby code with
      * a valid context, but still allow that context to garbage collect if the
      * thread stays alive much longer. We use SoftReference here because
      * WeakReference is collected too quickly, resulting in very expensive
      * ThreadContext churn (and this originally lead to JRUBY-2261's leak of
      * adopted RubyThread instances).
      * 
      * @return The ThreadContext instance for the current thread, or a new one
      * if none has previously been created or the old ThreadContext has been
      * collected.
      */
     public ThreadContext getCurrentContext() {
         SoftReference sr = null;
         ThreadContext context = null;
         
         while (context == null) {
             // loop until a context is available, to clean up softrefs that might have been collected
             if ((sr = (SoftReference)localContext.get()) == null) {
                 sr = adoptCurrentThread();
                 context = (ThreadContext)sr.get();
             } else {
                 context = (ThreadContext)sr.get();
             }
             
             // context is null, wipe out the SoftReference (this could be done with a reference queue)
             if (context == null) {
                 localContext.set(null);
             }
         }
 
         return context;
     }
     
     private SoftReference adoptCurrentThread() {
         Thread current = Thread.currentThread();
         
         RubyThread.adopt(runtime.getThread(), current);
         
         return (SoftReference) localContext.get();
     }
 
     public RubyThread getMainThread() {
         return mainContext.getThread();
     }
 
     public void setMainThread(Thread thread, RubyThread rubyThread) {
         mainContext.setThread(rubyThread);
-        threadContextMap.put(rubyThread, mainContext);
         rubyThreadMap.put(thread, rubyThread);
     }
     
     public synchronized RubyThread[] getActiveRubyThreads() {
     	// all threads in ruby thread group plus main thread
 
         synchronized(rubyThreadMap) {
             List<RubyThread> rtList = new ArrayList<RubyThread>(rubyThreadMap.size());
         
-            for (Map.Entry<RubyThreadMap.RubyThreadWeakReference<Object>, RubyThread> entry : rubyThreadMap.entrySet()) {
-                Object key = entry.getKey().get();
+            for (Map.Entry<Object, RubyThread> entry : rubyThreadMap.entrySet()) {
+                Object key = entry.getKey();
                 if (key == null) continue;
                 
                 if (key instanceof Thread) {
                     Thread t = (Thread)key;
 
                     // thread is not alive, skip it
                     if (!t.isAlive()) continue;
                 } else if (key instanceof Future) {
                     Future f = (Future)key;
 
                     // future is done or cancelled, skip it
                     if (f.isDone() || f.isCancelled()) continue;
                 }
             
                 rtList.add(entry.getValue());
             }
 
             RubyThread[] rubyThreads = new RubyThread[rtList.size()];
             rtList.toArray(rubyThreads);
     	
             return rubyThreads;
         }
     }
 
     public ThreadGroup getRubyThreadGroup() {
     	return rubyThreadGroup;
     }
 
     public ThreadContext getThreadContextForThread(RubyThread thread) {
-        return threadContextMap.get(thread);
+        return thread.getContext();
     }
 
     public synchronized ThreadContext registerNewThread(RubyThread thread) {
         ThreadContext context = ThreadContext.newContext(runtime);
         localContext.set(new SoftReference(context));
-        threadContextMap.put(thread, context);
         context.setThread(thread);
         return context;
     }
 
     public synchronized void associateThread(Object threadOrFuture, RubyThread rubyThread) {
         rubyThreadMap.put(threadOrFuture, rubyThread);
     }
 
     public synchronized void dissociateThread(Object threadOrFuture) {
         rubyThreadMap.remove(threadOrFuture);
     }
     
     public synchronized void unregisterThread(RubyThread thread) {
         rubyThreadMap.remove(Thread.currentThread());
-        threadContextMap.remove(thread);
         getCurrentContext().setThread(null);
         localContext.set(null);
     }
     
     public void setCritical(boolean critical) {
         if (critical && !criticalLock.isHeldByCurrentThread()) {
             acquireCritical();
         } else if (!critical && criticalLock.isHeldByCurrentThread()) {
             releaseCritical();
         }
     }
 
     private void acquireCritical() {
         criticalLock.lock();
     }
 
     private void releaseCritical() {
         criticalLock.unlock();
     }
     
     public boolean getCritical() {
         return criticalLock.isHeldByCurrentThread();
     }
     
     public static class Event {
         public enum Type { KILL, RAISE, WAKEUP }
         public final RubyThread sender;
         public final RubyThread target;
         public final Type type;
         public final IRubyObject exception;
 
         public Event(RubyThread sender, RubyThread target, Type type) {
             this(sender, target, type, null);
         }
 
         public Event(RubyThread sender, RubyThread target, Type type, IRubyObject exception) {
             this.sender = sender;
             this.target = target;
             this.type = type;
             this.exception = exception;
         }
     }
 
     public synchronized void deliverEvent(Event event) {
         // first, check if the sender has unreceived mail
         event.sender.checkMail(getCurrentContext());
 
         // then deliver mail to the target
         event.target.receiveMail(event);
     }
 
     /**
      * Get the map from threadlike objects to RubyThread instances. Used mainly
      * for testing purposes.
      *
      * @return The ruby thread map
      */
-    public RubyThreadMap getRubyThreadMap() {
+    public Map<Object, RubyThread> getRubyThreadMap() {
         return rubyThreadMap;
     }
-
-    /**
-     * Get the map from RubyThread objects to ThreadContext objects. Used for
-     * testing purposes.
-     *
-     * @return The thread context map
-     * @return
-     */
-    public Map getThreadContextMap() {
-        return threadContextMap;
-    }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 052c5345a9..92bdb842eb 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1257 +1,1262 @@
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
     
     /** The number of calls after which to do a thread event poll */
     private final static int CALL_POLL_COUNT = 0xFFF;
     private final static String UNKNOWN_NAME = "(unknown)";
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
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
+
+        // associate the thread with this context, unless we're clearing the reference
+        if (thread != null) {
+            thread.setContext(this);
+        }
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
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
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
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
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
         INTERPRETED_FRAMES.put(DefaultMethod.class.getName() + ".interpretedCall", FrameType.METHOD);
         
         INTERPRETED_FRAMES.put(InterpretedBlock.class.getName() + ".evalBlockBody", FrameType.BLOCK);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalWithBinding", FrameType.EVAL);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalSimple", FrameType.EVAL);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalClassDefinitionBody", FrameType.CLASS);
         
         INTERPRETED_FRAMES.put(Ruby.class.getName() + ".runInterpreter", FrameType.ROOT);
     }
     
     public static IRubyObject createRubyHybridBacktrace(Ruby runtime, RubyStackTraceElement[] backtraceFrames, RubyStackTraceElement[] stackTrace, boolean debug) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         ThreadContext context = runtime.getCurrentContext();
         
         int rubyFrameIndex = backtraceFrames.length - 1;
         for (int i = 0; i < stackTrace.length; i++) {
             RubyStackTraceElement element = stackTrace[i];
             
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
                     RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element.getElement()));
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
diff --git a/test/jruby_index b/test/jruby_index
index a4142e7aa8..1390649b24 100644
--- a/test/jruby_index
+++ b/test/jruby_index
@@ -1,124 +1,125 @@
 # Our own test/unit-based tests
 # NOTE: test_globals comes first here because it has tests that $? be nil
 test_globals
 # NOTE: test_object_class_default_methods comes early because it depends
 #       on no additional methods existing on the Object class
 test_object_class_default_methods
 test_argf
 test_array
 test_array_subclass_behavior
 test_autoload
 test_backquote
 test_backtraces
 test_big_decimal
 test_bignum
 test_binding_eval_yield
 test_block
 test_block_arg_processing
 test_cache_map_leak
 test_caller
 test_case
 test_class
 test_command_line_switches
 test_comparable
 test_core_arities
 test_crazy_blocks
 test_custom_enumerable
 test_cvars_in_odd_scopes
 test_date_time
 test_defined
 test_default_constants
 test_delegated_array_equals
 test_dir
 test_digest_lazy_load
 test_digest_extend
 test_digest2
 test_dup_clone_taint_freeze
 test_env
 test_etc
 test_eval
 test_eval_with_binding
 test_file
 test_flip
 test_frame_self
 test_hash
 test_higher_javasupport
 test_iconv
 test_included_in_object_space
 test_ivar_table_integrity
 test_io
 test_line_endings
 test_load
 test_math
 test_method
 test_method_cache
 test_method_override_and_caching
 test_nkf
 test_java_accessible_object
 test_java_extension
 test_java_wrapper_deadlock
 test_jruby_internals
 test_jruby_4084
 compiler/test_jrubyc
 test_launching_by_shell_script
 #test_local_jump_error
 test_marshal_with_instance_variables
 test_marshal_gemspec
 test_method_missing
 test_methods
 test_no_stack_trace_stomp
 test_pack
 test_primitive_to_java
 test_process
 test_proc_visibility
 test_parsing
 test_random
 test_rbconfig
 test_require_once
 test_respond_to_concurrency
 test_ri
 test_socket
 test_string_java_bytes
 test_string_printf
 test_string_sub
 test_string_to_number
 test_super_call_site_caching
 test_symbol
 test_tb_yaml
 test_timeout
 test_thread
 test_thread_backtrace
 test_threaded_nonlocal_return
 test_time_nil_ops
 test_unicode_paths
 test_unmarshal
 test_variables
 test_vietnamese_charset
 #test_trace_func
 test_win32
 test_zlib
 test_yaml
 test_system_error
 
 # these tests are last because they pull in libraries that can affect others
 test_base64_strangeness
 test_loading_builtin_libraries
 test_load_compiled_ruby_class_from_classpath
 test_null_channel
 test_irubyobject_java_passing
 test_jruby_object_input_stream
 test_jar_on_load_path
 test_jruby_ext
 test_jruby_core_ext
 test_thread_context_frame_dereferences_unreachable_variables
 test_context_classloader
 test_rexml_document
 test_load_compiled_ruby
 test_openssl_stub
 test_missing_jruby_home
 test_ast_inspector
 test_jarred_gems_with_spaces_in_directory
 test_weak_drb_id_conv
 test_kernel
 test_dir_with_plusses
 test_jar_file
+test_thread_service
diff --git a/test/test_thread_service.rb b/test/test_thread_service.rb
new file mode 100644
index 0000000000..bd1e2a540a
--- /dev/null
+++ b/test/test_thread_service.rb
@@ -0,0 +1,70 @@
+require 'test/unit'
+require 'jruby'
+
+class TestThreadService < Test::Unit::TestCase
+  GC_COUNT = 10
+  
+  def test_ruby_thread_leaks
+    svc = JRuby.runtime.thread_service
+    start_rt = svc.ruby_thread_map.size
+    
+    # spin up 100 threads and join them
+    (1..10).to_a.map {Thread.new {}}.map(&:join)
+    
+    # access maps and GC a couple times to flush things out
+    svc.ruby_thread_map.size
+    GC_COUNT.times {JRuby.gc}
+    
+    # confirm the size goes back to the same
+    assert_equal start_rt, svc.ruby_thread_map.size
+  end
+  
+  def test_java_thread_leaks
+    svc = JRuby.runtime.thread_service
+    start_rt = svc.ruby_thread_map.size
+
+    # spin up 100 Java threads and join them
+    (1..10).to_a.map {t = java.lang.Thread.new {}; t.start; t}.map(&:join)
+    
+    # access maps and GC a couple times to flush things out
+    svc.ruby_thread_map.size
+    GC_COUNT.times {JRuby.gc}
+
+    # confirm the size goes back to the same
+    assert_equal start_rt, svc.ruby_thread_map.size
+  end
+  
+  def test_java_threads_in_thread_list
+    svc = JRuby.runtime.thread_service
+    start_list = Thread.list
+    
+    # spin up 100 Java threads and wait for them all to be ready
+    state_ary = [false] * 10
+    threads = (0..9).to_a.map do |i|
+      t = java.lang.Thread.new do
+        state_ary[i] = true
+        Thread.pass while state_ary[i]
+      end
+      t.start
+      t
+    end
+    
+    # wait for them all to be running
+    Thread.pass until state_ary.all?
+    
+    # check that Thread.list contains 100 more threads
+    assert_equal start_list.size + 10, Thread.list.size
+    
+    # shut down all threads and wait for them to terminate
+    0.upto(9) {|i| state_ary[i] = false}
+    threads.map(&:join)
+    threads = nil
+    
+    # access maps and GC a couple times to flush things out
+    svc.ruby_thread_map.size
+    GC_COUNT.times {JRuby.gc}
+    
+    # confirm the thread list is back to what it was
+    assert_equal start_list, Thread.list
+  end
+end
\ No newline at end of file
