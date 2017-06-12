diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index 916c781a11..f423d8773a 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,1107 +1,1108 @@
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
 import java.lang.ref.WeakReference;
 import java.nio.channels.Channel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.nio.channels.spi.SelectorProvider;
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
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.util.io.BlockingIO;
 import org.jruby.util.io.SelectorFactory;
 
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
 
     // weak reference to associated ThreadContext
     private volatile WeakReference<ThreadContext> contextRef;
     
     private static final boolean DEBUG = false;
 
     public static enum Status { RUN, SLEEP, ABORTING, DEAD }
 
     private volatile ThreadService.Event mail;
     private volatile Status status = Status.RUN;
     private volatile BlockingTask currentBlockingTask;
 
     protected RubyThread(Ruby runtime, RubyClass type) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         finalResult = runtime.getNil();
 
+        this.priority = RubyFixnum.one(runtime);
         // init errorInfo to nil
         errorInfo = runtime.getNil();
     }
 
     public void receiveMail(ThreadService.Event event) {
         synchronized (this) {
             // if we're already aborting, we can receive no further mail
             if (status == Status.ABORTING) return;
 
             mail = event;
             switch (event.type) {
             case KILL:
                 status = Status.ABORTING;
             }
 
             // If this thread is sleeping or stopped, wake it
             notify();
         }
 
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
 
     public void setContext(ThreadContext context) {
         this.contextRef = new WeakReference<ThreadContext>(context);
     }
 
     public ThreadContext getContext() {
         return contextRef.get();
     }
     
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
     @JRubyMethod(name = {"new", "fork"}, rest = true, meta = true)
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, true, block);
     }
 
     /**
      * Basically the same as Thread.new . However, if class Thread is
      * subclassed, then calling start in that subclass will not invoke the
      * subclass's initialize method.
      */
     @JRubyMethod(rest = true, meta = true)
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
     
     @JRubyMethod(rest = true, visibility = PRIVATE)
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
                 thread.setName("Ruby" + thread.getName() + ": " + context.getFile() + ":" + (context.getLine() + 1));
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
             throw getRuntime().newThreadError("thread " + identityString() + " tried to join itself");
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
 
     @JRubyMethod
     public IRubyObject value() {
         join(new IRubyObject[0]);
         synchronized (this) {
             return finalResult;
         }
     }
 
     @JRubyMethod
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
         part.append("#<").append(cname).append(":");
         part.append(identityString());
         part.append(' ');
         part.append(status.toString().toLowerCase());
         part.append('>');
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
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject kill(IRubyObject receiver, IRubyObject rubyThread, Block block) {
         if (!(rubyThread instanceof RubyThread)) throw receiver.getRuntime().newTypeError(rubyThread, receiver.getRuntime().getThread());
         return ((RubyThread)rubyThread).kill();
     }
     
     @JRubyMethod(meta = true)
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
 
     @JRubyMethod(optional = 3)
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
 
     public static interface BlockingTask {
         public void run() throws InterruptedException;
         public void wakeup();
     }
 
     public static final class SleepTask implements BlockingTask {
         private final Object object;
         private final long millis;
         private final int nanos;
 
         public SleepTask(Object object, long millis, int nanos) {
             this.object = object;
             this.millis = millis;
             this.nanos = nanos;
         }
 
         public void run() throws InterruptedException {
             synchronized (object) {
                 object.wait(millis, nanos);
             }
         }
 
         public void wakeup() {
             synchronized (object) {
                 object.notify();
             }
         }
     }
 
     public void executeBlockingTask(BlockingTask task) throws InterruptedException {
         enterSleep();
         try {
             currentBlockingTask = task;
             pollThreadEvents();
             task.run();
         } finally {
             exitSleep();
             currentBlockingTask = null;
             pollThreadEvents();
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
 
     public StackTraceElement[] javaBacktrace() {
         if (threadImpl instanceof NativeThread) {
             return ((NativeThread)threadImpl).getThread().getStackTrace();
         }
 
         // Future-based threads can't get a Java trace
         return new StackTraceElement[0];
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
     
     @Deprecated
     public boolean selectForAccept(RubyIO io) {
         return select(io, SelectionKey.OP_ACCEPT);
     }
 
     private synchronized Selector getSelector(SelectableChannel channel) throws IOException {
         return SelectorFactory.openWithRetryFrom(getRuntime(), channel.provider());
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
                     currentSelector = getRuntime().getSelectorPool().get();
 
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
                     // Note: I don't like ignoring these exceptions, but it's
                     // unclear how likely they are to happen or what damage we
                     // might do by ignoring them. Note that the pieces are separate
                     // so that we can ensure one failing does not affect the others
                     // running.
 
                     // clean up the key in the selector
                     try {
                         if (key != null) key.cancel();
                         if (currentSelector != null) currentSelector.selectNow();
                     } catch (Exception e) {
                         // ignore
                     }
 
                     // shut down and null out the selector
                     try {
                         if (currentSelector != null) {
                             getRuntime().getSelectorPool().put(currentSelector);
                         }
                     } catch (Exception e) {
                         // ignore
                     } finally {
                         currentSelector = null;
                     }
 
                     // remove this thread as a blocker against the given IO
                     if (io != null) io.removeBlockingThread(this);
 
                     // go back to previous blocking state on the selectable
                     try {
                         selectable.configureBlocking(oldBlocking);
                     } catch (Exception e) {
                         // ignore
                     }
 
                     // clear thread state from blocking call
                     afterBlockingCall();
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
         
         BlockingTask task = currentBlockingTask;
         if (task != null) {
             task.wakeup();
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
         pollThreadEvents();
         enterSleep();
     }
     
     public void afterBlockingCall() {
         exitSleep();
         pollThreadEvents();
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
                 executeBlockingTask(new SleepTask(o, delay_ms, delay_ns_remainder));
             }
             long end_ns = System.nanoTime();
             return ( end_ns - start_ns ) <= delay_ns;
         } else {
             executeBlockingTask(new SleepTask(o, 0, 0));
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
 
     public String toString() {
         return threadImpl.toString();
     }
 
     private String identityString() {
         return "0x" + Integer.toHexString(System.identityHashCode(this));
     }
 }
diff --git a/test/test_thread.rb b/test/test_thread.rb
index 608162f07b..4806126057 100644
--- a/test/test_thread.rb
+++ b/test/test_thread.rb
@@ -1,332 +1,339 @@
 require 'test/unit'
 require 'thread'
 
 class TestThread < Test::Unit::TestCase
   def test_running_and_finishing
     thread = Thread.new {
       $toto = 1
     }
     thread.join
     assert_equal(1, $toto)
     assert_equal(false, thread.status)
   end
 
   def test_local_variables
     v = nil
     t = Thread.new { v = 1 }
     t.join
     assert_equal(1, v)
   end
 
   def test_taking_arguments
     v = nil
     t = Thread.new(10) {|argument| v = argument }
     t.join
     assert_equal(10, v)
   end
 
   def test_thread_current
     t = Thread.current
     assert_equal(t, Thread.current)
   end
 
   def test_thread_local_variables
     v = nil
     t = Thread.new {
       Thread.current[:x] = 1234
       assert_equal(1234, Thread.current[:x])
       assert_equal(nil, Thread.current[:y])
       assert(Thread.current.key?(:x))
       assert(! Thread.current.key?(:y))
     }
     t.join
     assert(! Thread.current.key?(:x))
     Thread.current[:x] = 1
     assert(Thread.current.key?(:x))
     Thread.current["y"] = 2
     assert(Thread.current.key?("y"))
     assert([:x, :y], Thread.current.keys.sort {|x, y| x.to_s <=> y.to_s})
     assert_raises(TypeError) { Thread.current[Object.new] }
     assert_raises(TypeError) { Thread.current[Object.new] = 1 }
     assert_raises(ArgumentError) { Thread.current[1] }
     assert_raises(ArgumentError) { Thread.current[1]  = 1}
   end
 
   def test_status
     t = Thread.new { Thread.current.status }
     t.join
     v = t.value
     assert_equal("run", v)
     assert_equal(false, t.status)
     
     # check that "run", sleep", and "dead" appear in inspected output
     q = Queue.new
     t = Thread.new { q << Thread.current.inspect; sleep }
     Thread.pass until t.status == "sleep" || !t.alive?
     assert(q.shift(true)["run"])
     assert(t.inspect["sleep"])
     t.kill
     t.join rescue nil
     assert(t.inspect["dead"])
   end
 
   def test_inspect
     t = Thread.new {}.join
     assert_match /#<Thread:0x[0-9a-z]+ \w+>/, t.inspect
   end
 
   def thread_foo()
     raise "hello"
   end
   def test_error_handling
     e = nil
     t = Thread.new {
       thread_foo()
     }
     begin
       t.join
     rescue RuntimeError => error
       e = error
     end
     assert(! e.nil?)
     assert_equal(nil, t.status)
   end
 
   def test_joining_itself
     e = nil
     begin
       Thread.current.join
     rescue ThreadError => error
       e = error
     end
     assert(! e.nil?)
     assert_match /thread [0-9a-z]+ tried to join itself/, e.message
   end
 
   def test_raise
     e = nil
     t = Thread.new {
       while true
         Thread.pass
       end
     }
     t.raise("Die")
     begin
       t.join
     rescue RuntimeError => error
       e = error
     end
     assert(e.kind_of?(RuntimeError))
     
     # test raising in a sleeping thread
     e = 1
     set = false
     begin
       t = Thread.new { e = 2; set = true; sleep(100); e = 3 }
       while !set
         sleep(1)
       end
       t.raise("Die")
     rescue; end
     
     assert_equal(2, e)
     assert_raise(RuntimeError) { t.value }
   end
 
   def test_thread_value
     assert_raise(ArgumentError) { Thread.new { }.value(100) }
     assert_equal(2, Thread.new { 2 }.value)
     assert_raise(RuntimeError) { Thread.new { raise "foo" }.value }
   end
   
   class MyThread < Thread
     def initialize
       super do; 1; end
     end
   end
   
   def test_thread_subclass_zsuper
     x = MyThread.new
     x.join
     assert_equal(1, x.value)
     x = MyThread.start { 2 }
     x.join
     assert_equal(2, x.value)
   end
   
   def test_dead_thread_priority
     x = Thread.new {}
     1 while x.alive?
     x.priority = 5
     assert_equal(5, x.priority)
   end
   
   def test_join_returns_thread
     x = Thread.new {}
     assert_nothing_raised { x.join.to_s }
   end
   
   def test_abort_on_exception_does_not_blow_up
     # CON: I had an issue where annotated methods weren't binding right
     # where there was both a static and instance method of the same name.
     # This caused abort_on_exception to fail to bind right; a temporary fix
     # was put in place by appending _x but it shouldn't stay. This test confirms
     # the method stays callable.
     assert_nothing_raised { Thread.abort_on_exception }
     assert_nothing_raised { Thread.abort_on_exception = Thread.abort_on_exception}
   end
 
   # JRUBY-2021
   def test_multithreaded_method_definition
     def run_me
       sleep 0.1
       def do_stuff
         sleep 0.1
       end
     end
 
     threads = []
     100.times {
       threads << Thread.new { run_me }
     }
     threads.each { |t| t.join }
   end
 
   def test_socket_accept_can_be_interrupted
     require 'socket'
     tcps = nil
     100.times{|i|
       begin
         tcps = TCPServer.new("0.0.0.0", 10000+i)
         break
       rescue Errno::EADDRINUSE
         next
       end
     }
 
     flunk "unable to find open port" unless tcps
 
     t = Thread.new {
       tcps.accept
     }
 
     Thread.pass until t.status == "sleep"
     ex = Exception.new
     t.raise ex
     assert_raises(Exception) { t.join }
   end
   
   # JRUBY-2315
   def test_exit_from_within_thread
     begin
       a = Thread.new do
         loop do
           sleep 0.1
         end
       end
 
       b = Thread.new do
         sleep 0.5
         Kernel.exit(1)
       end
  
       a.join
       fail
       b.join
     rescue SystemExit
       # rescued!
       assert(true)
     end
   end
 
   def call_to_s(a)
     a.to_s
   end
   
   # JRUBY-2477 - polymorphic calls are not thread-safe
   def test_poly_calls_thread_safe
     # Note this isn't a perfect test, but it's not possible to test perfectly
     # This might only fail on multicore machines
     results = [false, false, false, false, false, false, false, false, false, false]
     threads = []
     sym = :foo
     str = "foo"
     
     10.times {|i| threads << Thread.new { 10_000.times { call_to_s(sym); call_to_s(str) }; results[i] = true }}
     
     threads.pop.join until threads.empty?
     assert_equal [true, true, true, true, true, true, true, true, true, true], results
   end
 
   def test_thread_exit_does_not_deadlock
     100.times do
       t = Thread.new { Thread.stop; Thread.current.exit }
       Thread.pass until t.status == "sleep"
       t.wakeup; t.join
     end
   end
 
   # JRUBY-2380: Thread.list has a race condition
   # Fix is to make sure the thread is added to the global list before returning from Thread#new
   def test_new_thread_in_list
     count = 10
     live = Thread.list.size
 
     100.times do
       threads = []
       count.times do
         threads << Thread.new do
           sleep
         end
       end
 
       if (size = Thread.list.size) != count + live
         raise "wrong! (expected #{count + live} but was #{size})"
       end
 
       threads.each do |t|
         Thread.pass until t.status == 'sleep'
         t.wakeup
         t.join
       end
     end
   end
   
   # JRUBY-3568: thread group is inherited from parent
   def test_inherits_thread_group
     tg = ThreadGroup.new
     og = nil
     tg.add(Thread.current)
     Thread.new { og = Thread.current.group }.join
     assert_equal(tg, og)
   end
 
   # JRUBY-3740: Thread#wakeup not working
   def test_wakeup_wakes_sleeping_thread
     awoke = false
     t = Thread.new { sleep; awoke = true }
     Thread.pass until t.status == "sleep"
     t.wakeup.join
     assert awoke
 
     awoke = false
     start_time = Time.now
     done = false
     t = Thread.new { sleep 100; done = true }
     Thread.pass until t.status == "sleep"
     t.wakeup
     loop {
       break if done || Time.now - start_time > 10
       Thread.pass
     }
     assert done
   end
 
   # JRUBY-4154
   if RUBY_VERSION < "1.9"
     def test_thread_exclusive
       out = Thread.exclusive { :result }
       assert_equal(:result, out)
     end
   end
+
+  # JRUBY-5290
+  def test_default_priority
+    t = Thread.new { sleep 1 while true }
+    assert_equal 1, t.priority
+    t.exit
+  end
 end
