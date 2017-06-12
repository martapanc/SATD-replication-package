diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index 3081dbb458..7e7b01b0f3 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,815 +1,822 @@
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
 import java.nio.channels.Channel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.HashMap;
 import java.util.Map;
 
 import java.util.Set;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.internal.runtime.FutureThread;
 import org.jruby.internal.runtime.NativeThread;
 import org.jruby.internal.runtime.RubyNativeThread;
 import org.jruby.internal.runtime.RubyRunnable;
 import org.jruby.internal.runtime.ThreadLike;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.TimeoutException;
 import java.util.concurrent.locks.ReentrantLock;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.Visibility;
 
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
 public class RubyThread extends RubyObject {
     private ThreadLike threadImpl;
     private RubyFixnum priority;
     private final Map<IRubyObject, IRubyObject> threadLocalVariables = new HashMap<IRubyObject, IRubyObject>();
     private boolean abortOnException;
     private IRubyObject finalResult;
     private RaiseException exitingException;
     private IRubyObject receivedException;
     private RubyThreadGroup threadGroup;
 
     private final ThreadService threadService;
     private volatile boolean isStopped = false;
     public Object stopLock = new Object();
     
     private volatile boolean killed = false;
     public Object killLock = new Object();
     
     public final ReentrantLock lock = new ReentrantLock();
     
     private static final boolean DEBUG = false;
+
+    protected RubyThread(Ruby runtime, RubyClass type) {
+        super(runtime, type);
+        this.threadService = runtime.getThreadService();
+        // set to default thread group
+        RubyThreadGroup defaultThreadGroup = (RubyThreadGroup)runtime.getThreadGroup().fastGetConstant("Default");
+        defaultThreadGroup.add(this, Block.NULL_BLOCK);
+        finalResult = runtime.getNil();
+    }
+    
+    /**
+     * Dispose of the current thread by removing it from its parent ThreadGroup.
+     */
+    public void dispose() {
+        threadGroup.remove(this);
+    }
    
     public static RubyClass createThreadClass(Ruby runtime) {
         // FIXME: In order for Thread to play well with the standard 'new' behavior,
         // it must provide an allocator that can create empty object instances which
         // initialize then fills with appropriate data.
         RubyClass threadClass = runtime.defineClass("Thread", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setThread(threadClass);
 
         threadClass.defineAnnotatedMethods(RubyThread.class);
 
         RubyThread rubyThread = new RubyThread(runtime, threadClass);
         // TODO: need to isolate the "current" thread from class creation
         rubyThread.threadImpl = new NativeThread(rubyThread, Thread.currentThread());
         runtime.getThreadService().setMainThread(rubyThread);
         
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
         
         context.preAdoptThread();
         
         return rubyThread;
     }
     
     @JRubyMethod(name = "initialize", rest = true, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (!block.isGiven()) throw getRuntime().newThreadError("must be called with a block");
 
         if (RubyInstanceConfig.POOLING_ENABLED) {
             threadImpl = new FutureThread(this, new RubyRunnable(this, args, block));
         } else {
             threadImpl = new NativeThread(this, new RubyNativeThread(this, args, block));
         }
         threadImpl.start();
         
         return this;
     }
     
     private static RubyThread startThread(final IRubyObject recv, final IRubyObject[] args, boolean callInit, Block block) {
         RubyThread rubyThread = new RubyThread(recv.getRuntime(), (RubyClass) recv);
         
         if (callInit) {
             rubyThread.callInit(args, block);
         } else {
             // for Thread::start, which does not call the subclass's initialize
             rubyThread.initialize(args, block);
         }
         
         return rubyThread;
     }
     
     private void ensureCurrent(ThreadContext context) {
         if (this != context.getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     private void ensureNotCurrent() {
         if (this == getRuntime().getCurrentContext().getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     public void cleanTerminate(IRubyObject result) {
         finalResult = result;
         isStopped = true;
     }
 
     public void pollThreadEvents() {
         pollThreadEvents(getRuntime().getCurrentContext());
     }
     
     public void pollThreadEvents(ThreadContext context) {
         // check for criticalization *before* locking ourselves
         threadService.waitForCritical();
 
         ensureCurrent(context);
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before");
         if (killed) throw new ThreadKill();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " after");
         if (receivedException != null) {
             // clear this so we don't keep re-throwing
             IRubyObject raiseException = receivedException;
             receivedException = null;
             RubyModule kernelModule = getRuntime().getKernel();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before propagating exception: " + killed);
             kernelModule.callMethod(context, "raise", raiseException);
         }
     }
 
-    protected RubyThread(Ruby runtime, RubyClass type) {
-        super(runtime, type);
-        this.threadService = runtime.getThreadService();
-        // set to default thread group
-        RubyThreadGroup defaultThreadGroup = (RubyThreadGroup)runtime.getThreadGroup().fastGetConstant("Default");
-        defaultThreadGroup.add(this, Block.NULL_BLOCK);
-        finalResult = runtime.getNil();
-    }
-
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
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject op_aref(IRubyObject key) {
         IRubyObject value;
         if ((value = threadLocalVariables.get(getSymbolKey(key))) != null) {
             return value;
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
         key = getSymbolKey(key);
         
         threadLocalVariables.put(key, value);
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
         return threadImpl.isAlive() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "join", optional = 1)
     public IRubyObject join(IRubyObject[] args) {
         long timeoutMillis = 0;
         if (args.length > 0) {
             if (args.length > 1) {
                 throw getRuntime().newArgumentError(args.length,1);
             }
             // MRI behavior: value given in seconds; converted to Float; less
             // than or equal to zero returns immediately; returns nil
             timeoutMillis = (long)(1000.0D * args[0].convertToFloat().getValue());
             if (timeoutMillis <= 0) {
 	        // TODO: not sure that we should skip caling join() altogether.
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
                 synchronized (stopLock) {
                     stopLock.notify();
                 }
                 
                 // interrupt the target thread in case it's blocking or waiting
                 // WARNING: We no longer interrupt the target thread, since this usually means
                 // interrupting IO and with NIO that means the channel is no longer usable.
                 // We either need a new way to handle waking a target thread that's waiting
                 // on IO, or we need to accept that we can't wake such threads and must wait
                 // for them to complete their operation.
                 //threadImpl.interrupt();
             }
             threadImpl.join(timeoutMillis);
         } catch (InterruptedException ie) {
             ie.printStackTrace();
             assert false : ie;
         } catch (TimeoutException ie) {
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
 
     @JRubyMethod(name = "value")
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
     public IRubyObject inspect() {
         // FIXME: There's some code duplication here with RubyObject#inspect
         StringBuffer part = new StringBuffer();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(System.identityHashCode(this)));
         
         if (threadImpl.isAlive()) {
             if (isStopped) {
                 part.append(getRuntime().newString(" sleep"));
             } else if (killed) {
                 part.append(getRuntime().newString(" aborting"));
             } else {
                 part.append(getRuntime().newString(" run"));
             }
         } else {
             part.append(" dead");
         }
         
         part.append(">");
         return getRuntime().newString(part.toString());
     }
 
     @JRubyMethod(name = "key?", required = 1)
     public RubyBoolean key_p(IRubyObject key) {
         key = getSymbolKey(key);
         
         return getRuntime().newBoolean(threadLocalVariables.containsKey(key));
     }
 
     @JRubyMethod(name = "keys")
     public RubyArray keys() {
         IRubyObject[] keys = new IRubyObject[threadLocalVariables.size()];
         
         return RubyArray.newArrayNoCopy(getRuntime(), (IRubyObject[])threadLocalVariables.keySet().toArray(keys));
     }
     
     @JRubyMethod(name = "critical=", required = 1, meta = true)
     public static IRubyObject critical_set(IRubyObject receiver, IRubyObject value) {
     	receiver.getRuntime().getThreadService().setCritical(value.isTrue());
     	
     	return value;
     }
 
     @JRubyMethod(name = "critical", meta = true)
     public static IRubyObject critical(IRubyObject receiver) {
     	return receiver.getRuntime().newBoolean(receiver.getRuntime().getThreadService().getCritical());
     }
     
     @JRubyMethod(name = "stop", meta = true)
     public static IRubyObject stop(IRubyObject receiver) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
         Object stopLock = rubyThread.stopLock;
         
         synchronized (stopLock) {
             rubyThread.pollThreadEvents();
             try {
                 rubyThread.isStopped = true;
                 // attempt to decriticalize all if we're the critical thread
                 receiver.getRuntime().getThreadService().setCritical(false);
                 
                 stopLock.wait();
             } catch (InterruptedException ie) {
                 rubyThread.pollThreadEvents();
             }
             rubyThread.isStopped = false;
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
         
         rubyThread.killed = true;
         // attempt to decriticalize all if we're the critical thread
         receiver.getRuntime().getThreadService().setCritical(false);
         
         throw new ThreadKill();
     }
 
     @JRubyMethod(name = "stop?")
     public RubyBoolean stop_p() {
     	// not valid for "dead" state
     	return getRuntime().newBoolean(isStopped);
     }
     
     @JRubyMethod(name = "wakeup")
     public RubyThread wakeup() {
     	synchronized (stopLock) {
     		stopLock.notifyAll();
     	}
     	
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
 
     @JRubyMethod(name = "raise", optional = 2, frame = true)
     public IRubyObject raise(IRubyObject[] args, Block block) {
         ensureNotCurrent();
         Ruby runtime = getRuntime();
         
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before raising");
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         try {
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " raising");
             receivedException = prepareRaiseException(runtime, args, block);
             
             // If the target thread is sleeping or stopped, wake it
             synchronized (stopLock) {
                 stopLock.notify();
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
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
         }
 
         return this;
     }
 
     private IRubyObject prepareRaiseException(Ruby runtime, IRubyObject[] args, Block block) {
         if(args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if(lastException.isNil()) {
                 return new RaiseException(runtime, runtime.fastGetClass("RuntimeError"), "", false).getException();
             } 
             return lastException;
         }
 
         IRubyObject exception;
         ThreadContext context = getRuntime().getCurrentContext();
         
         if(args.length == 1) {
             if(args[0] instanceof RubyString) {
                 return runtime.fastGetClass("RuntimeError").newInstance(context, args, block);
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
     public IRubyObject run() {
         // if stopped, unstop
         synchronized (stopLock) {
             if (isStopped) {
                 isStopped = false;
                 stopLock.notifyAll();
             }
         }
     	
     	return this;
     }
     
     public void sleep(long millis) throws InterruptedException {
         ensureCurrent(getRuntime().getCurrentContext());
         synchronized (stopLock) {
             pollThreadEvents();
             try {
                 isStopped = true;
                 stopLock.wait(millis);
             } finally {
                 isStopped = false;
                 pollThreadEvents();
             }
         }
     }
 
     @JRubyMethod(name = "status")
     public IRubyObject status() {
         if (threadImpl.isAlive()) {
             if (isStopped || currentSelector != null && currentSelector.isOpen()) {
             	return getRuntime().newString("sleep");
             } else if (killed) {
                 return getRuntime().newString("aborting");
             }
         	
             return getRuntime().newString("run");
         } else if (exitingException != null) {
             return getRuntime().getNil();
         } else {
             return getRuntime().newBoolean(false);
         }
     }
 
     @JRubyMethod(name = {"kill", "exit", "terminate"})
     public IRubyObject kill() {
     	// need to reexamine this
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         
         try {
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " trying to kill");
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
 
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " succeeded with kill");
             killed = true;
             
             // If the target thread is sleeping or stopped, wake it
             synchronized (stopLock) {
                 stopLock.notify();
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
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
         }
         
         try {
             threadImpl.join();
         } catch (InterruptedException ie) {
             // we were interrupted, check thread events again
             currentThread.pollThreadEvents();
         } catch (ExecutionException ie) {
             // we were interrupted, check thread events again
             currentThread.pollThreadEvents();
         }
         
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
 
     private boolean isCurrent() {
         return threadImpl.isCurrent();
     }
 
     public void exceptionRaised(RaiseException exception) {
         assert isCurrent();
 
         RubyException rubyException = exception.getException();
         Ruby runtime = rubyException.getRuntime();
         if (runtime.fastGetClass("SystemExit").isInstance(rubyException)) {
             threadService.getMainThread().raise(new IRubyObject[] {rubyException}, Block.NULL_BLOCK);
         } else if (abortOnException(runtime)) {
             // FIXME: printError explodes on some nullpointer
             //getRuntime().getRuntime().printError(exception.getException());
             RubyException systemExit = RubySystemExit.newInstance(runtime, 1);
             systemExit.message = rubyException.message;
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
     
     private Selector currentSelector;
     
     public boolean selectForAccept(RubyIO io) {
         Channel channel = io.getChannel();
         
         if (channel instanceof SelectableChannel) {
             SelectableChannel selectable = (SelectableChannel)channel;
             
             try {
                 currentSelector = selectable.provider().openSelector();
             
                 SelectionKey key = selectable.register(currentSelector, SelectionKey.OP_ACCEPT);
 
                 int result = currentSelector.select();
 
                 if (result == 1) {
                     Set<SelectionKey> keySet = currentSelector.selectedKeys();
 
                     if (keySet.iterator().next() == key) {
                         return true;
                     }
                 }
 
                 return false;
             } catch (IOException ioe) {
                 throw io.getRuntime().newRuntimeError("Error with selector: " + ioe);
             } finally {
                 if (currentSelector != null) {
                     try {
                         currentSelector.close();
                     } catch (IOException ioe) {
                         throw io.getRuntime().newRuntimeError("Could not close selector");
                     }
                 }
                 currentSelector = null;
             }
         } else {
             // can't select, just have to do a blocking call
             return true;
         }
     }
     
     public void interrupt() {
         if (currentSelector != null) {
             currentSelector.wakeup();
         }
     }
     
     public void beforeBlockingCall() {
         isStopped = true;
     }
     
     public void afterBlockingCall() {
         isStopped = false;
     }
 }
diff --git a/src/org/jruby/internal/runtime/ThreadService.java b/src/org/jruby/internal/runtime/ThreadService.java
index cacb47024f..e152778683 100644
--- a/src/org/jruby/internal/runtime/ThreadService.java
+++ b/src/org/jruby/internal/runtime/ThreadService.java
@@ -1,190 +1,215 @@
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
 
+import java.lang.ref.SoftReference;
 import java.util.concurrent.locks.ReentrantLock;
-import java.lang.ref.WeakReference;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.jruby.Ruby;
 import org.jruby.RubyThread;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.util.collections.WeakHashSet;
 
 public class ThreadService {
     private Ruby runtime;
     private ThreadContext mainContext;
-    private ThreadLocal localContext;
+    private ThreadLocal<SoftReference> localContext;
     private ThreadGroup rubyThreadGroup;
     private Set rubyThreadList;
     private Thread mainThread;
     
     private ReentrantLock criticalLock = new ReentrantLock();
 
     public ThreadService(Ruby runtime) {
         this.runtime = runtime;
         this.mainContext = ThreadContext.newContext(runtime);
         this.localContext = new ThreadLocal();
         this.rubyThreadGroup = new ThreadGroup("Ruby Threads#" + runtime.hashCode());
         this.rubyThreadList = Collections.synchronizedSet(new WeakHashSet());
         
         // Must be called from main thread (it is currently, but this bothers me)
         mainThread = Thread.currentThread();
-        localContext.set(new WeakReference(mainContext));
+        localContext.set(new SoftReference(mainContext));
         rubyThreadList.add(mainThread);
     }
 
     public void disposeCurrentThread() {
         localContext.set(null);
     }
 
+    /**
+     * In order to provide an appropriate execution context for a given thread,
+     * we store ThreadContext instances in a threadlocal. This method is a utility
+     * to get at that threadlocal context from anywhere in the program it may
+     * not be immediately available. This method should be used sparingly, and
+     * if it is possible to pass ThreadContext on the argument list, it is
+     * preferable.
+     * 
+     * <b>Description of behavior</b>
+     * 
+     * The threadlocal does not actually contain the ThreadContext directly;
+     * instead, it contains a SoftReference that holds the ThreadContext. This
+     * is to allow new threads to enter the system and execute Ruby code with
+     * a valid context, but still allow that context to garbage collect if the
+     * thread stays alive much longer. We use SoftReference here because
+     * WeakReference is collected too quickly, resulting in very expensive
+     * ThreadContext churn (and this originally lead to JRUBY-2261's leak of
+     * adopted RubyThread instances).
+     * 
+     * @return The ThreadContext instance for the current thread, or a new one
+     * if none has previously been created or the old ThreadContext has been
+     * collected.
+     */
     public ThreadContext getCurrentContext() {
-        WeakReference wr = null;
+        SoftReference sr = null;
         ThreadContext context = null;
         
         while (context == null) {
-            // loop until a context is available, to clean up weakrefs that might get collected
-            if ((wr = (WeakReference)localContext.get()) == null) {
-                wr = adoptCurrentThread();
-                context = (ThreadContext)wr.get();
+            // loop until a context is available, to clean up softrefs that might have been collected
+            if ((sr = (SoftReference)localContext.get()) == null) {
+                sr = adoptCurrentThread();
+                context = (ThreadContext)sr.get();
             } else {
-                context = (ThreadContext)wr.get();
+                context = (ThreadContext)sr.get();
             }
+            
+            // context is null, wipe out the SoftReference (this could be done with a reference queue)
             if (context == null) {
                 localContext.set(null);
             }
         }
 
         return context;
     }
     
-    private WeakReference adoptCurrentThread() {
+    private SoftReference adoptCurrentThread() {
         Thread current = Thread.currentThread();
         
         RubyThread.adopt(runtime.getThread(), current);
         
-        return (WeakReference) localContext.get();
+        return (SoftReference) localContext.get();
     }
 
     public RubyThread getMainThread() {
         return mainContext.getThread();
     }
 
     public void setMainThread(RubyThread thread) {
         mainContext.setThread(thread);
     }
     
     public synchronized RubyThread[] getActiveRubyThreads() {
     	// all threads in ruby thread group plus main thread
 
         synchronized(rubyThreadList) {
             List rtList = new ArrayList(rubyThreadList.size());
         
             for (Iterator iter = rubyThreadList.iterator(); iter.hasNext();) {
                 Thread t = (Thread)iter.next();
             
                 if (!t.isAlive()) continue;
             
                 RubyThread rt = getRubyThreadFromThread(t);
                 rtList.add(rt);
             }
         
             RubyThread[] rubyThreads = new RubyThread[rtList.size()];
             rtList.toArray(rubyThreads);
     	
             return rubyThreads;
         }
     }
     
     public ThreadGroup getRubyThreadGroup() {
     	return rubyThreadGroup;
     }
 
     public synchronized ThreadContext registerNewThread(RubyThread thread) {
         ThreadContext context = ThreadContext.newContext(runtime);
-        localContext.set(new WeakReference(context));
+        localContext.set(new SoftReference(context));
         getCurrentContext().setThread(thread);
         // This requires register to be called from within the registree thread
         rubyThreadList.add(Thread.currentThread());
         return context;
     }
     
     public synchronized void unregisterThread(RubyThread thread) {
         rubyThreadList.remove(Thread.currentThread());
         getCurrentContext().setThread(null);
         localContext.set(null);
     }
     
     private RubyThread getRubyThreadFromThread(Thread activeThread) {
         RubyThread rubyThread;
         if (activeThread instanceof RubyNativeThread) {
             RubyNativeThread rubyNativeThread = (RubyNativeThread)activeThread;
             rubyThread = rubyNativeThread.getRubyThread();
         } else {
             // main thread
             rubyThread = mainContext.getThread();
         }
         return rubyThread;
     }
     
     public void setCritical(boolean critical) {
         if (criticalLock.isHeldByCurrentThread()) {
             if (critical) {
                 // do nothing
             } else {
                 criticalLock.unlock();
             }
         } else {
             if (critical) {
                 criticalLock.lock();
             } else {
                 // do nothing
             }
         }
     }
     
     public boolean getCritical() {
         return criticalLock.isHeldByCurrentThread();
     }
     
     public void waitForCritical() {
         if (criticalLock.isLocked()) {
             criticalLock.lock();
             criticalLock.unlock();
         }
     }
 
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index e3719fff77..fad252f18d 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,904 +1,909 @@
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
 
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyKernel.CatchTarget;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author jpetersen
  */
 public final class ThreadContext {
     public static synchronized ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         //        if(runtime.getInstanceConfig().isSamplingEnabled()) {
         //    org.jruby.util.SimpleSampler.registerThreadContext(context);
         //}
 
         return context;
     }
     
     private final static int INITIAL_SIZE = 50;
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     // Error info is per-thread
     private IRubyObject errorInfo;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private CatchTarget[] catchStack = new CatchTarget[INITIAL_SIZE];
     private int catchIndex = -1;
     
     // File where current executing unit is being evaluated
     private String file = "";
     
     // Line where current executing unit is being evaluated
     private int line = 0;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
         // init errorInfo to nil
         errorInfo = runtime.getNil();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
             
         for (int i = 0; i < frameStack.length; i++) {
             frameStack[i] = new Frame();
         }
     }
+
+    @Override
+    protected void finalize() throws Throwable {
+        thread.dispose();
+    }
     
     CallType lastCallType;
     
     Visibility lastVisibility;
     
     IRubyObject lastExitStatus;
     
     public Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return errorInfo;
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         this.errorInfo = errorInfo;
         return errorInfo;
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
     
     private void expandFramesIfNecessary(int newMax) {
         if (newMax == frameStack.length) {
             int newSize = frameStack.length * 2;
             Frame[] newFrameStack = new Frame[newSize];
             
             System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
             
             for (int i = frameStack.length; i < newSize; i++) {
                 newFrameStack[i] = new Frame();
             }
             
             frameStack = newFrameStack;
         }
     }
     
     private void expandParentsIfNecessary() {
         if (parentIndex + 1 == parentStack.length) {
             int newSize = parentStack.length * 2;
             RubyModule[] newParentStack = new RubyModule[newSize];
             
             System.arraycopy(parentStack, 0, newParentStack, 0, parentStack.length);
             
             parentStack = newParentStack;
         }
     }
     
     public void pushScope(DynamicScope scope) {
         scopeStack[++scopeIndex] = scope;
         expandScopesIfNecessary();
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
     
     private void expandScopesIfNecessary() {
         if (scopeIndex + 1 == scopeStack.length) {
             int newSize = scopeStack.length * 2;
             DynamicScope[] newScopeStack = new DynamicScope[newSize];
             
             System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
             
             scopeStack = newScopeStack;
         }
     }
     
     public RubyThread getThread() {
         return thread;
     }
     
     public void setThread(RubyThread thread) {
         this.thread = thread;
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
 //    public IRubyObject getLastline() {
 //        IRubyObject value = getCurrentScope().getLastLine();
 //        
 //        // DynamicScope does not preinitialize these values since they are virtually never used.
 //        return value == null ? runtime.getNil() : value;
 //    }
 //    
 //    public void setLastline(IRubyObject value) {
 //        getCurrentScope().setLastLine(value);
 //    }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         if (catchIndex + 1 == catchStack.length) {
             int newSize = catchStack.length * 2;
             CatchTarget[] newCatchStack = new CatchTarget[newSize];
             
             System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
             catchStack = newCatchStack;
         }
     }
     
     public void pushCatch(CatchTarget catchTarget) {
         catchStack[++catchIndex] = catchTarget;
         expandCatchIfNecessary();
     }
     
     public void popCatch() {
         catchIndex--;
     }
     
     public CatchTarget[] getActiveCatches() {
         if (catchIndex < 0) return new CatchTarget[0];
         
         CatchTarget[] activeCatches = new CatchTarget[catchIndex + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, catchIndex + 1);
         return activeCatches;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         Frame currentFrame = getCurrentFrame();
         frameStack[++frameIndex].updateFrame(currentFrame);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private Frame pushFrameCopy(Frame frame) {
         frameStack[++frameIndex].updateFrame(frame);
         expandFramesIfNecessary(frameIndex + 1);
         return frameStack[frameIndex];
     }
     
     private void pushFrame(Frame frame) {
         frameStack[++frameIndex] = frame;
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block, JumpTarget jumpTarget) {
         pushFrame(clazz, name, self, block, jumpTarget);        
     }
     
     private void pushBacktraceFrame(String name) {
         pushFrame(name);        
     }
     
     private void pushFrame(String name) {
         frameStack[++frameIndex].updateFrame(name, file, line);
         expandFramesIfNecessary(frameIndex + 1);
     }
 
     private void pushFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block, JumpTarget jumpTarget) {
         frameStack[++frameIndex].updateFrame(clazz, self, name, block, file, line, jumpTarget);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void pushFrame() {
         frameStack[++frameIndex].updateFrame(file, line);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void popFrame() {
         Frame frame = frameStack[frameIndex];
         frameIndex--;
         setFile(frame.getFile());
         setLine(frame.getLine());
     }
         
     private void popFrameReal() {
         Frame frame = frameStack[frameIndex];
         frameStack[frameIndex] = new Frame();
         frameIndex--;
         setFile(frame.getFile());
         setLine(frame.getLine());
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
     
     public Frame getNextFrame() {
         expandFramesIfNecessary(frameIndex + 1);
         return frameStack[frameIndex + 1];
     }
     
     public Frame getPreviousFrame() {
         int size = frameIndex + 1;
         return size <= 1 ? null : frameStack[size - 2];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public JumpTarget getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public void setFrameJumpTarget(JumpTarget target) {
         getCurrentFrame().setJumpTarget(target);
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
         getThread().pollThreadEvents(this);
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         
         parentStack[++parentIndex] = currentModule;
         expandParentsIfNecessary();
     }
     
     public RubyModule popRubyClass() {
         RubyModule ret = parentStack[parentIndex];
         parentStack[parentIndex--] = null;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = parentStack[parentIndex];
         
         return parentModule.getNonIncludedClass();
     }
     
     public RubyModule getBindingRubyClass() {
         RubyModule parentModule = null;
         if(parentIndex == 0) {
             parentModule = parentStack[parentIndex];
         } else {
             parentModule = parentStack[parentIndex-1];
             
         }
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject result;
         IRubyObject undef = runtime.getUndef();
         
         // flipped from while to do to search current class first
         for (StaticScope scope = getCurrentScope().getStaticScope(); scope != null; scope = scope.getPreviousCRefScope()) {
             RubyModule module = scope.getModule();
             if ((result = module.fastFetchConstant(internedName)) != null) {
                 if (result != undef) return true;
                 return runtime.getLoadService().autoloadFor(module.getName() + "::" + internedName) != null;
             }
         }
         
         return getCurrentScope().getStaticScope().getModule().fastIsConstantDefined(internedName);
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         StaticScope scope = getCurrentScope().getStaticScope();
         RubyClass object = runtime.getObject();
         IRubyObject undef = runtime.getUndef();
         IRubyObject result;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = scope.getModule();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             if ((result = klass.fastFetchConstant(internedName)) != null) {
                 if (result != undef) {
                     return result;
                 }
                 klass.deleteConstant(internedName);
                 if (runtime.getLoadService().autoload(klass.getName() + "::" + internedName) == null) break;
                 continue;
             }
             scope = scope.getPreviousCRefScope();
         } while (scope != null && scope.getModule() != object);
         
         return getCurrentScope().getStaticScope().getModule().fastGetConstant(internedName);
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
     
     private static void addBackTraceElement(RubyArray backtrace, Frame frame, Frame previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getName() != null && 
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getFile().equals(previousFrame.getFile()) &&
                 frame.getLine() == previousFrame.getLine()) {
             return;
         }
         
         StringBuffer buf = new StringBuffer(60);
         buf.append(frame.getFile()).append(':').append(frame.getLine() + 1);
         
         if (previousFrame.getName() != null) {
             buf.append(":in `").append(previousFrame.getName()).append('\'');
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
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, Frame[] backtraceFrames) {
         RubyArray backtrace = runtime.newArray();
         
         if (backtraceFrames == null || backtraceFrames.length <= 0) return backtrace;
         
         int traceSize = backtraceFrames.length;
 
         for (int i = traceSize - 1; i > 0; i--) {
             Frame frame = backtraceFrames[i];
             // We are in eval with binding break out early
             if (frame.isBindingFrame()) break;
 
             addBackTraceElement(backtrace, frame, backtraceFrames[i - 1]);
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
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type, String[] scopeNames) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         StaticScope staticScope = new LocalStaticScope(getCurrentScope().getStaticScope(), scopeNames);
         staticScope.setModule(type);
         pushScope(new ManyVarsDynamicScope(staticScope, null));
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
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, IRubyObject[] args, int req, Block block, 
             StaticScope staticScope, JumpTarget jumpTarget) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block, jumpTarget);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, IRubyObject[] args, int req, Block block,
             JumpTarget jumpTarget) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block, jumpTarget);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
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
     
     public void postMethodBacktraceOnly() {
         popFrame();
     }
     
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         
         pushRubyClass(objectClass);
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushCallFrame(null, null, self, Block.NULL_BLOCK, null);
         // set visibility to private, since toplevel of scripts always started out private
         setCurrentVisibility(Visibility.PRIVATE);
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
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block, frame.getJumpTarget());
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
     
     public void preRunThread(Frame currentFrame) {
         pushFrame(currentFrame);
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public void preForBlock(Binding binding, RubyModule klass) {
         Frame f = binding.getFrame();
         f.setFile(file);
         f.setLine(line);
         pushFrame(f);
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushScope(binding.getDynamicScope());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         Frame f = pushFrameCopy(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         Frame f = pushFrameCopy(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldNoScope(Binding binding, RubyModule klass) {
         Frame f = pushFrameCopy(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preEvalWithBinding(Binding binding) {
         Frame frame = binding.getFrame();
         frame.setIsBindingFrame(true);
         pushFrame(frame);
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushRubyClass(binding.getKlass());
     }
     
     public void postEvalWithBinding(Binding binding) {
         binding.getFrame().setIsBindingFrame(false);
         popFrameReal();
         popRubyClass();
     }
     
     public void postYield(Binding binding) {
         popScope();
         popFrameReal();
         popRubyClass();
     }
     
     public void postYieldLight(Binding binding) {
         popScope();
         popFrameReal();
         popRubyClass();
     }
     
     public void postYieldNoScope() {
         popFrameReal();
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
 }
diff --git a/test/org/jruby/test/TestAdoptedThreading.java b/test/org/jruby/test/TestAdoptedThreading.java
index 43379244d6..519280e9d1 100644
--- a/test/org/jruby/test/TestAdoptedThreading.java
+++ b/test/org/jruby/test/TestAdoptedThreading.java
@@ -1,169 +1,243 @@
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
 
 import java.util.logging.Level;
 import java.util.logging.Logger;
 
 import org.apache.bsf.BSFManager;
 
 import junit.framework.Test;
 import junit.framework.TestCase;
 import junit.framework.TestSuite;
+import org.apache.bsf.BSFException;
+import org.jruby.RubyArray;
+import org.jruby.RubyThreadGroup;
+import org.jruby.runtime.Block;
 
 /**
  * A simple "adopted thread" concurrency test case.
  */
 public class TestAdoptedThreading extends TestCase {
     private static Logger LOGGER = Logger.getLogger(TestAdoptedThreading.class
             .getName());
 
     // Uncomment the "puts" lines if you want to see more detail
     private static final String SCRIPT = "require 'java'\n"
             + "include_class 'org.jruby.test.ITest'\n"
             + "if ITest.instance_of?(Module)\n"
             + "  class TestImpl; include ITest; end\n"
             + "else\n"
             + "  class TestImpl < ITest; end\n"
             + "end\n"
             + "class TestImpl\n"
             + "    def exec(_value)\n"
             + "        #puts \"start executing!\"\n"
             + "        100.times do | item |\n"
             + "           value = \"#{item}\"\n"
             + "        end\n"
             + "        #puts \"end executing1!\"\n"
             + "        exec2(_value)\n"
             + "    end\n" + "    def exec2(_value)\n"
             + "        #puts \"start executing2!\"\n"
             + "        500.times do | item |\n"
             + "           value = \"#{item}\"\n"
             + "        end\n"
             + "        #puts \"end executing2!\"\n"
             + "        \"VALUE: #{_value}\"\n"
             + "    end\n"
             + "end";
 
     public TestAdoptedThreading(String _name) {
         super(_name);
     }
 
     public static Test suite() {
         TestSuite suite;
         suite = new TestSuite(TestAdoptedThreading.class);
 
         return suite;
     }
 
     private BSFManager manager_;
 
     protected void setUp() throws Exception {
         LOGGER.log(Level.FINEST, SCRIPT);
         BSFManager.registerScriptingEngine("ruby",
                 "org.jruby.javasupport.bsf.JRubyEngine",
                 new String[] { "rb" });
         manager_ = new BSFManager();
         manager_.exec("ruby", "(java)", 1, 1, SCRIPT);
     }
     
     private static final int RUNNER_COUNT = 10;
     private static final int RUNNER_LOOP_COUNT = 10;
 
     public void testThreading() {
         Runner[] runners = new Runner[RUNNER_COUNT];
         
         for (int i = 0; i < RUNNER_COUNT; i++) runners[i] = new Runner("R" + i, RUNNER_LOOP_COUNT);
         for (int i = 0; i < RUNNER_COUNT; i++) runners[i].start();
 
         try {
             for (int i = 0; i < RUNNER_COUNT; i++) runners[i].join();
         } catch (InterruptedException ie) {
             // XXX: do something?
         }
         
         for (int i = 0; i < RUNNER_COUNT; i++) {
             if (runners[i].isFailed()) {
                 if (runners[i].getFailureException() != null) {
                     throw runners[i].getFailureException();
                 }
             }
         }
     }
+    
+    public void testThreadsStayAdopted() throws Exception {
+        final Object start = new Object();
+        final Exception[] fail = {null};
+        
+        RubyThreadGroup rtg = (RubyThreadGroup)manager_.eval("ruby", "(java)", 1, 1, "ThreadGroup::Default");
+        
+        int initialCount = ((RubyArray)rtg.list(Block.NULL_BLOCK)).getLength();
+        
+        synchronized (start) {
+            Thread pausyThread = new Thread() {
+                public void run() {
+                    synchronized (this) {
+                        // Notify the calling thread that we're about to go to sleep the first time
+                        synchronized(start) {
+                            start.notify();
+                        }
+                
+                        // wait for the go signal
+                        try {
+                            this.wait();
+                        } catch (InterruptedException ie) {
+                            fail[0] = ie;
+                            return;
+                        }
+                    }
+                    
+                    // run ten separate calls into Ruby, with delay and explicit GC
+                    for (int i = 0; i < 10; i++) {
+                        try {
+                            manager_.exec("ruby", "(java)", 1, 1, "a = 0; while a < 1000; a += 1; end");
+                        } catch (BSFException bsfe) {
+                            fail[0] = bsfe;
+                        }
+                        System.gc();
+
+                        try {
+                            sleep(1000);
+                        } catch (InterruptedException ie) {
+                            fail[0] = ie;
+                            break;
+                        }
+                    }
+                    
+                    synchronized (start) {
+                        start.notify();
+                    }
+                }
+            };
+            
+            pausyThread.start();
+            
+            // wait until thread has initialized
+            start.wait();
+            
+            // notify thread to proceed
+            synchronized(pausyThread) {
+                pausyThread.notify();
+            }
+            
+            // wait until thread has completed
+            start.wait();
+        }
+        
+        // if any exceptions were raised, we fail
+        assertNull(fail[0]);
+        
+        // there should only be one more thread in thread group than before we started
+        assertEquals(initialCount + 1, ((RubyArray)rtg.list(Block.NULL_BLOCK)).getLength());
+    }
 
     class Runner extends Thread {
         private int count_;
         private boolean failed;
         private RuntimeException failureException;
 
         public Runner(String _name, int _count) {
             count_ = _count;
         }
         
         public boolean isFailed() {
             return failed;
         }
         
         public RuntimeException getFailureException() {
             return failureException;
         }
 
         public void run() {
             for (int i = 0; i < count_; i++) {
                 ITest test = getTest();
                 if (test != null) {
                     try {
                         Object result = test.exec("foo");
                         if (!result.toString().equals("VALUE: 5000")) {
                             failed = true;
                         }
                     } catch (RuntimeException re) {
                         failed = true;
                         failureException = re;
                         break;
                     }
                 }
             }
         }
 
         private ITest getTest() {
             ITest result = null;
             synchronized (manager_) {
                 try {
                     result = (ITest) manager_.eval("ruby", "(java)", 1, 1,
                             "TestImpl.new");
                 } catch (Exception ex) {
                     ex.printStackTrace();
                 }
             }
             return result;
         }
     }
 
     public static void main(String[] _args) {
         junit.textui.TestRunner.run(suite());
     }
 
 }
