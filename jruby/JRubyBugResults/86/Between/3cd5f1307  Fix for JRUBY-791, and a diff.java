diff --git a/src/org/jruby/RubyProcess.java b/src/org/jruby/RubyProcess.java
index f8537c1c21..2ff19bed40 100644
--- a/src/org/jruby/RubyProcess.java
+++ b/src/org/jruby/RubyProcess.java
@@ -1,182 +1,182 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 
 /**
  *
  * @author  enebo
  */
 public class RubyProcess {
 
     public static RubyModule createProcessModule(Ruby runtime) {
         RubyModule process = runtime.defineModule("Process");
         
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
         RubyModule process_status = process.defineClassUnder("Status", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR); 
 
         CallbackFactory processCallbackFactory = runtime.callbackFactory(RubyProcess.class);
         CallbackFactory process_statusCallbackFactory = runtime.callbackFactory(RubyProcess.RubyStatus.class);
 
 //        process.defineModuleFunction("fork", processCallbackFactory.getSingletonMethod("fork"));
 //        process.defineModuleFunction("exit!", processCallbackFactory.getOptSingletonMethod("exit_bang"));
 //        process.defineModuleFunction("exit", processCallbackFactory.getOptSingletonMethod("exit"));
 //        process.defineModuleFunction("abort", processCallbackFactory.getOptSingletonMethod("abort"));
 //        process.defineModuleFunction("kill", processCallbackFactory.getOptSingletonMethod("kill"));
 //        process.defineModuleFunction("wait", processCallbackFactory.getOptSingletonMethod("wait"));
 //        process.defineModuleFunction("wait2", processCallbackFactory.getOptSingletonMethod("wait2"));
 //        process.defineModuleFunction("waitpid", processCallbackFactory.getOptSingletonMethod("waitpid"));
 //        process.defineModuleFunction("waitpid2", processCallbackFactory.getOptSingletonMethod("waitpid2"));
 //        process.defineModuleFunction("waitall", processCallbackFactory.getSingletonMethod("waitall"));
 //        process.defineModuleFunction("detach", processCallbackFactory.getSingletonMethod("detach", RubyKernel.IRUBY_OBJECT));
         process.defineModuleFunction("pid", processCallbackFactory.getFastSingletonMethod("pid"));
 //        process.defineModuleFunction("ppid", processCallbackFactory.getSingletonMethod("ppid"));
 //
 //        process.defineModuleFunction("getpgrp", processCallbackFactory.getSingletonMethod("getprgrp"));
 //        process.defineModuleFunction("setpgrp", processCallbackFactory.getSingletonMethod("setpgrp"));
 //        process.defineModuleFunction("getpgid", processCallbackFactory.getSingletonMethod("getpgid", RubyKernel.IRUBY_OBJECT));
 //        process.defineModuleFunction("setpgid", processCallbackFactory.getSingletonMethod("setpgid", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
 //
 //        process.defineModuleFunction("setsid", processCallbackFactory.getSingletonMethod("setsid"));
 //
 //        process.defineModuleFunction("getpriority", processCallbackFactory.getSingletonMethod("getpriority", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
 //        process.defineModuleFunction("setpriority", processCallbackFactory.getSingletonMethod("setpriority", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
 
 //    #ifdef HAVE_GETPRIORITY
 //        rb_define_const(rb_mProcess, "PRIO_PROCESS", INT2FIX(PRIO_PROCESS));
 //        rb_define_const(rb_mProcess, "PRIO_PGRP", INT2FIX(PRIO_PGRP));
 //        rb_define_const(rb_mProcess, "PRIO_USER", INT2FIX(PRIO_USER));
 //    #endif
 
 //        process.defineModuleFunction("uid", processCallbackFactory.getSingletonMethod("uid"));
 //        process.defineModuleFunction("uid=", processCallbackFactory.getSingletonMethod("uid_set", RubyKernel.IRUBY_OBJECT));
 //        process.defineModuleFunction("gid", processCallbackFactory.getSingletonMethod("gid"));
 //        process.defineModuleFunction("gid=", processCallbackFactory.getSingletonMethod("gid_set", RubyKernel.IRUBY_OBJECT));
 //        process.defineModuleFunction("euid", processCallbackFactory.getSingletonMethod("euid"));
 //        process.defineModuleFunction("euid=", processCallbackFactory.getSingletonMethod("euid_set", RubyKernel.IRUBY_OBJECT));
 //        process.defineModuleFunction("egid", processCallbackFactory.getSingletonMethod("egid"));
 //        process.defineModuleFunction("egid=", processCallbackFactory.getSingletonMethod("egid_set", RubyKernel.IRUBY_OBJECT));
 //        process.defineModuleFunction("initgroups", processCallbackFactory.getSingletonMethod("initgroups", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
 //        process.defineModuleFunction("groups", processCallbackFactory.getSingletonMethod("groups"));
 //        process.defineModuleFunction("groups=", processCallbackFactory.getSingletonMethod("groups_set", RubyKernel.IRUBY_OBJECT));
 //        process.defineModuleFunction("maxgroups", processCallbackFactory.getSingletonMethod("maxgroups"));
 //        process.defineModuleFunction("maxgroups=", processCallbackFactory.getSingletonMethod("maxgroups_set", RubyKernel.IRUBY_OBJECT));
         process.defineModuleFunction("times", processCallbackFactory.getSingletonMethod("times"));
         
         // Process::Status methods  
         process_status.defineMethod("==", process_statusCallbackFactory.getMethod("op_eq", RubyKernel.IRUBY_OBJECT));
 //        process_status.defineMethod("&", process_statusCallbackFactory.getMethod("op_and"));
         process_status.defineMethod(">>", process_statusCallbackFactory.getMethod("rightshift_op", RubyKernel.IRUBY_OBJECT));
         process_status.defineMethod("to_i", process_statusCallbackFactory.getMethod("to_i"));
 //        process_status.defineMethod("to_int", process_statusCallbackFactory.getMethod("to_int"));
         process_status.defineMethod("to_s", process_statusCallbackFactory.getMethod("to_s"));
         process_status.defineMethod("inspect", process_statusCallbackFactory.getMethod("inspect"));
 //        process_status.defineMethod("pid", process_statusCallbackFactory.getMethod("pid"));
 //        process_status.defineMethod("stopped?", process_statusCallbackFactory.getMethod("stopped_p"));
 //        process_status.defineMethod("stopsig", process_statusCallbackFactory.getMethod("stopsig"));
 //        process_status.defineMethod("signaled?", process_statusCallbackFactory.getMethod("signaled_p"));
 //        process_status.defineMethod("termsig", process_statusCallbackFactory.getMethod("termsig"));
 //        process_status.defineMethod("exited?", process_statusCallbackFactory.getMethod("exited_p"));
         process_status.defineMethod("exitstatus", process_statusCallbackFactory.getMethod("exitstatus"));
         process_status.defineMethod("success?", process_statusCallbackFactory.getMethod("success_p"));
 //        process_status.defineMethod("coredump?", process_statusCallbackFactory.getMethod("coredump_p"));
         
         return process;
     }
     
     public static class RubyStatus extends RubyObject {
         private long status = 0L;
         
         private static final long EXIT_SUCCESS = 0L;
         public RubyStatus(Ruby runtime, RubyClass metaClass, long status) {
             super(runtime, metaClass);
             
             this.status = status;
         }
         
         public static RubyStatus newProcessStatus(Ruby runtime, long status) {
             return new RubyStatus(runtime, runtime.getModule("Process").getClass("Status"), status);
         }
         
         public IRubyObject exitstatus(Block block) {
             return getRuntime().newFixnum(status);
         }
         
         public IRubyObject rightshift_op(IRubyObject other, Block block) {
             long shiftValue = other.convertToInteger().getLongValue();
             
             
             return getRuntime().newFixnum(status >> shiftValue);
         }
         
         public IRubyObject op_eq(IRubyObject other, Block block) {
             return other.callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==", this.to_i(block));
         }
 
         public IRubyObject to_i(Block unusedBlock) {
             return exitstatus(null);
         }
         
         public IRubyObject to_s(Block unusedBlock) {
             return getRuntime().newString(String.valueOf(status));
         }
         
         public IRubyObject inspect(Block unusedBlock) {
-            return to_s();
+            return getRuntime().newString("#<Process::Status: pid=????,exited(" + String.valueOf(status) + ")>");
         }
         
         public IRubyObject success_p(Block unusedBlock) {
             return getRuntime().newBoolean(status == EXIT_SUCCESS);
         }
     }
     
     public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
         Ruby runtime = recv.getRuntime();
         double currentTime = System.currentTimeMillis() / 1000.0;
         double startTime = runtime.getStartTime() / 1000.0;
         RubyFloat zero = runtime.newFloat(0.0);
         return RubyStruct.newStruct(runtime.getTmsStruct(), 
                 new IRubyObject[] { runtime.newFloat(currentTime - startTime), zero, zero, zero }, 
                 Block.NULL_BLOCK);
     }
 
     public static IRubyObject pid(IRubyObject recv) {
         return recv.getRuntime().newFixnum(System.identityHashCode(recv.getRuntime()));
     }
 
     public static IRubyObject kill(IRubyObject recv, IRubyObject[] args) throws Exception {
         return recv.getRuntime().getNil();
     }
 }
diff --git a/src/org/jruby/runtime/builtin/meta/IOMetaClass.java b/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
index eb77b1e54f..6c2b4de032 100644
--- a/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
+++ b/src/org/jruby/runtime/builtin/meta/IOMetaClass.java
@@ -1,389 +1,390 @@
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
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
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
 package org.jruby.runtime.builtin.meta;
 
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.Selector;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Channel;
 import java.nio.channels.Pipe;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyClass;
 import org.jruby.RubyIO;
 import org.jruby.RubyKernel;
+import org.jruby.RubyProcess;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IOHandler;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.collections.SinglyLinkedList;
 
 public class IOMetaClass extends ObjectMetaClass {
     
     public IOMetaClass(Ruby runtime) {
         this("IO", RubyIO.class, runtime.getObject(), IO_ALLOCATOR);
     }
     
     public IOMetaClass(String name, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         this(name, RubyIO.class, superClass, allocator, parentCRef);
     }
     
     public IOMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator) {
         super(name, clazz, superClass, allocator);
     }
     
     public IOMetaClass(String name, Class clazz, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         super(name, clazz, superClass, allocator, parentCRef);
     }
     
     protected class IOMeta extends Meta {
         protected void initializeClass() {
             includeModule(getRuntime().getModule("Enumerable"));
             
             // TODO: Implement tty? and isatty.  We have no real capability to
             // determine this from java, but if we could set tty status, then
             // we could invoke jruby differently to allow stdin to return true
             // on this.  This would allow things like cgi.rb to work properly.
             
             defineSingletonMethod("foreach", Arity.optional());
             defineSingletonMethod("read", Arity.optional());
             defineSingletonMethod("readlines", Arity.optional());
             defineSingletonMethod("popen", Arity.optional());
             defineFastSingletonMethod("select", Arity.optional());
             defineFastSingletonMethod("pipe", Arity.noArguments());
             
             defineFastMethod("<<", Arity.singleArgument(), "addString");
             defineFastMethod("binmode", Arity.noArguments());
             defineFastMethod("close", Arity.noArguments());
             defineFastMethod("closed?", Arity.noArguments(), "closed");
             defineMethod("each", Arity.optional(), "each_line");
             defineMethod("each_byte", Arity.noArguments());
             defineMethod("each_line", Arity.optional());
             defineFastMethod("eof", Arity.noArguments());
             defineAlias("eof?", "eof");
             defineFastMethod("fcntl", Arity.twoArguments());
             defineFastMethod("fileno", Arity.noArguments());
             defineFastMethod("flush", Arity.noArguments());
             defineFastMethod("fsync", Arity.noArguments());
             defineFastMethod("getc", Arity.noArguments());
             defineFastMethod("gets", Arity.optional());
             defineMethod("initialize", Arity.optional());
             defineFastMethod("initialize_copy", Arity.singleArgument());
             defineFastMethod("lineno", Arity.noArguments());
             defineFastMethod("lineno=", Arity.singleArgument(), "lineno_set");
             defineFastMethod("pid", Arity.noArguments());
             defineFastMethod("pos", Arity.noArguments());
             defineFastMethod("pos=", Arity.singleArgument(), "pos_set");
             defineFastMethod("print", Arity.optional());
             defineFastMethod("printf", Arity.optional());
             defineFastMethod("putc", Arity.singleArgument());
             defineFastMethod("puts", Arity.optional());
             defineFastMethod("readpartial", Arity.optional());
             defineFastMethod("read", Arity.optional());
             defineFastMethod("readchar", Arity.noArguments());
             defineFastMethod("readline", Arity.optional());
             defineFastMethod("readlines", Arity.optional());
             defineFastMethod("reopen", Arity.optional());
             defineFastMethod("rewind", Arity.noArguments());
             defineFastMethod("seek", Arity.optional());
             defineFastMethod("sync", Arity.noArguments());
             defineFastMethod("sync=", Arity.singleArgument(), "sync_set");
             defineFastMethod("sysread", Arity.singleArgument());
             defineFastMethod("syswrite", Arity.singleArgument());
             defineAlias("tell", "pos");
             defineAlias("to_i", "fileno");
             defineFastMethod("to_io", Arity.noArguments());
             defineFastMethod("ungetc", Arity.singleArgument());
             defineFastMethod("write", Arity.singleArgument());
             defineFastMethod("tty?", Arity.noArguments(), "tty");
             defineAlias("isatty", "tty?");
             
             // Constants for seek
             setConstant("SEEK_SET", getRuntime().newFixnum(IOHandler.SEEK_SET));
             setConstant("SEEK_CUR", getRuntime().newFixnum(IOHandler.SEEK_CUR));
             setConstant("SEEK_END", getRuntime().newFixnum(IOHandler.SEEK_END));
         }
     };
     
     protected Meta getMeta() {
         return new IOMeta();
     }
     
     public RubyClass newSubClass(String name, SinglyLinkedList parentCRef) {
         return new IOMetaClass(name, this, IO_ALLOCATOR, parentCRef);
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
     
     /** rb_io_s_foreach
      *
      */
     public IRubyObject foreach(IRubyObject[] args, Block block) {
         int count = Arity.checkArgumentCount(getRuntime(), args, 1, -1);
         IRubyObject filename = args[0].convertToString();
         getRuntime().checkSafeString(filename);
         RubyIO io = (RubyIO) ((FileMetaClass) getRuntime().getClass("File")).open(new IRubyObject[] { filename }, false, block);
         
         if (!io.isNil() && io.isOpen()) {
             try {
                 IRubyObject[] newArgs = new IRubyObject[count - 1];
                 System.arraycopy(args, 1, newArgs, 0, count - 1);
                 
                 IRubyObject nextLine = io.internalGets(newArgs);
                 while (!nextLine.isNil()) {
                     block.yield(getRuntime().getCurrentContext(), nextLine);
                     nextLine = io.internalGets(newArgs);
                 }
             } finally {
                 io.close();
             }
         }
         
         return getRuntime().getNil();
     }
     
     private static void registerSelect(Selector selector, IRubyObject obj, int ops) throws IOException {
         RubyIO ioObj;
         
         if(!(obj instanceof RubyIO)) {
             // invoke to_io
             if(!obj.respondsTo("to_io")) {
                 return;
             }
             ioObj = (RubyIO) obj.callMethod(obj.getRuntime().getCurrentContext(), "to_io");
         } else {
             ioObj = (RubyIO) obj;
         }
         
         Channel channel = ioObj.getChannel();
         if(channel == null || !(channel instanceof SelectableChannel)) {
             return;
         }
         
         ((SelectableChannel) channel).configureBlocking(false);
         int real_ops = ((SelectableChannel) channel).validOps() & ops;
         SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
         
         if(key == null) {
             ((SelectableChannel) channel).register(selector, real_ops, obj);
         } else {
             key.interestOps(key.interestOps()|real_ops);
         }
     }
     
     public IRubyObject select(IRubyObject[] args) {
         return select_static(getRuntime(), args);
     }
     
     public static IRubyObject select_static(Ruby runtime, IRubyObject[] args) {
         try {
             boolean atLeastOneDescriptor = false;
             
             Selector selector = Selector.open();
             if (!args[0].isNil()) {
                 atLeastOneDescriptor = true;
                 
                 // read
                 for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                     IRubyObject obj = (IRubyObject) i.next();
                     registerSelect(selector, obj, SelectionKey.OP_READ|SelectionKey.OP_ACCEPT);
                 }
             }
             if (args.length > 1 && !args[1].isNil()) {
                 atLeastOneDescriptor = true;
                 // write
                 for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                     IRubyObject obj = (IRubyObject) i.next();
                     registerSelect(selector, obj, SelectionKey.OP_WRITE);
                 }
             }
             if (args.length > 2 && !args[2].isNil()) {
                 atLeastOneDescriptor = true;
                 // Java's select doesn't do anything about this, so we leave it be.
             }
             
             long timeout = 0;
             if(args.length > 3 && !args[3].isNil()) {
                 if (args[3] instanceof RubyFloat) {
                     timeout = Math.round(((RubyFloat) args[3]).getDoubleValue() * 1000);
                 } else {
                     timeout = Math.round(((RubyFixnum) args[3]).getDoubleValue() * 1000);
                 }
                 
                 if (timeout < 0) {
                     throw runtime.newArgumentError("negative timeout given");
                 }
             }
             
             if (!atLeastOneDescriptor) {
                 return runtime.getNil();
             }
             
             if(args.length > 3) {
                 selector.select(timeout);
             } else {
                 selector.select();
             }
             
             List r = new ArrayList();
             List w = new ArrayList();
             List e = new ArrayList();
             for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                 SelectionKey key = (SelectionKey) i.next();
                 if ((key.interestOps() & key.readyOps()
                         & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
                     r.add(key.attachment());
                 }
                 if ((key.interestOps() & key.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                     w.add(key.attachment());
                 }
             }
             
             // make all sockets blocking as configured again
             for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
                 SelectionKey key = (SelectionKey) i.next();
                 SelectableChannel channel = key.channel();
                 synchronized(channel.blockingLock()) {
                     boolean blocking = ((RubyIO) key.attachment()).getBlocking();
                     key.cancel();
                     channel.configureBlocking(blocking);
                 }
             }
             selector.close();
             
             if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
                 return runtime.getNil();
             }
             
             List ret = new ArrayList();
             
             ret.add(RubyArray.newArray(runtime, r));
             ret.add(RubyArray.newArray(runtime, w));
             ret.add(RubyArray.newArray(runtime, e));
             
             return RubyArray.newArray(runtime, ret);
         } catch(IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
     
     public IRubyObject read(IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 3);
         IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
         RubyIO file = (RubyIO) RubyKernel.open(this, fileArguments, block);
         IRubyObject[] readArguments;
         
         if (args.length >= 2) {
             readArguments = new IRubyObject[] {args[1].convertToType(getRuntime().getFixnum(), MethodIndex.TO_INT, "to_int", true)};
         } else {
             readArguments = new IRubyObject[] {};
         }
         
         try {
             
             if (args.length == 3) {
                 file.seek(new IRubyObject[] {args[2].convertToType(getRuntime().getFixnum(), MethodIndex.TO_INT, "to_int", true)});
             }
             
             return file.read(readArguments);
         } finally {
             file.close();
         }
     }
     
     public RubyArray readlines(IRubyObject[] args, Block block) {
         int count = Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         
         IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
         IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
         RubyIO file = (RubyIO) RubyKernel.open(this, fileArguments, block);
         try {
             return file.readlines(separatorArguments);
         } finally {
             file.close();
         }
     }
     
     //XXX Hacked incomplete popen implementation to make
     public IRubyObject popen(IRubyObject[] args, Block block) {
         Ruby runtime = getRuntime();
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         IRubyObject cmdObj = args[0].convertToString();
         getRuntime().checkSafeString(cmdObj);
         ThreadContext tc = runtime.getCurrentContext();
         
         try {
             Process process = new ShellLauncher(runtime).run(cmdObj);            
             RubyIO io = new RubyIO(runtime, process);
             
             if (block.isGiven()) {
                 try {
                     block.yield(tc, io);
                     return runtime.getNil();
                 } finally {
                     io.close();
-                    runtime.getGlobalVariables().set("$?", runtime.newFixnum(process.waitFor() * 256));
+                    runtime.getGlobalVariables().set("$?",  RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor() * 256)));
                 }
             }
             return io;
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
     
     // NIO based pipe
     public IRubyObject pipe() throws Exception {
         Ruby runtime = getRuntime();
         Pipe pipe = Pipe.open();
         return runtime.newArrayNoCopy(new IRubyObject[]{
             new RubyIO(runtime, pipe.source()),
             new RubyIO(runtime, pipe.sink())
         });
     }
 }
