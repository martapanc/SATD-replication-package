diff --git a/src/org/jruby/RubyProcess.java b/src/org/jruby/RubyProcess.java
index 7b0dc95d29..0057fd3292 100644
--- a/src/org/jruby/RubyProcess.java
+++ b/src/org/jruby/RubyProcess.java
@@ -1,976 +1,986 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
 
 import com.kenai.constantine.platform.Errno;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ShellLauncher;
 
 import static org.jruby.ext.JRubyPOSIXHelper.checkErrno;
 
 
 /**
  */
 
 @JRubyModule(name="Process")
 public class RubyProcess {
 
     public static RubyModule createProcessModule(Ruby runtime) {
         RubyModule process = runtime.defineModule("Process");
         runtime.setProcess(process);
         
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
         RubyClass process_status = process.defineClassUnder("Status", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setProcStatus(process_status);
         
         RubyModule process_uid = process.defineModuleUnder("UID");
         runtime.setProcUID(process_uid);
         
         RubyModule process_gid = process.defineModuleUnder("GID");
         runtime.setProcGID(process_gid);
         
         RubyModule process_sys = process.defineModuleUnder("Sys");
         runtime.setProcSys(process_sys);
         
         process.defineAnnotatedMethods(RubyProcess.class);
         process_status.defineAnnotatedMethods(RubyStatus.class);
         process_uid.defineAnnotatedMethods(UserID.class);
         process_gid.defineAnnotatedMethods(GroupID.class);
         process_sys.defineAnnotatedMethods(Sys.class);
 
         runtime.loadConstantSet(process, com.kenai.constantine.platform.PRIO.class);
         runtime.loadConstantSet(process, com.kenai.constantine.platform.RLIM.class);
         runtime.loadConstantSet(process, com.kenai.constantine.platform.RLIMIT.class);
         
         process.defineConstant("WNOHANG", runtime.newFixnum(1));
         process.defineConstant("WUNTRACED", runtime.newFixnum(2));
         
         return process;
     }
     
     @JRubyClass(name="Process::Status")
     public static class RubyStatus extends RubyObject {
         private long status = 0L;
         
         private static final long EXIT_SUCCESS = 0L;
         public RubyStatus(Ruby runtime, RubyClass metaClass, long status) {
             super(runtime, metaClass);
             this.status = status;
         }
         
         public static RubyStatus newProcessStatus(Ruby runtime, long status) {
             return new RubyStatus(runtime, runtime.getProcStatus(), status);
         }
         
         // Bunch of methods still not implemented
         @JRubyMethod(name = {"to_int", "pid", "stopped?", "stopsig", "signaled?", "termsig?", "exited?", "coredump?"}, frame = true)
         public IRubyObject not_implemented() {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod(name = {"&"}, frame = true)
         public IRubyObject not_implemented1(IRubyObject arg) {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod
         public IRubyObject exitstatus() {
             return getRuntime().newFixnum(status);
         }
         
         @Deprecated
         public IRubyObject op_rshift(IRubyObject other) {
             return op_rshift(getRuntime(), other);
         }
         @JRubyMethod(name = ">>")
         public IRubyObject op_rshift(ThreadContext context, IRubyObject other) {
             return op_rshift(context.getRuntime(), other);
         }
         public IRubyObject op_rshift(Ruby runtime, IRubyObject other) {
             long shiftValue = other.convertToInteger().getLongValue();
             return runtime.newFixnum(status >> shiftValue);
         }
 
         @Override
         @JRubyMethod(name = "==")
         public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
             return other.callMethod(context, "==", this.to_i(context.getRuntime()));
         }
         
         @Deprecated
         public IRubyObject to_i() {
             return to_i(getRuntime());
         }
         @JRubyMethod
         public IRubyObject to_i(ThreadContext context) {
             return to_i(context.getRuntime());
         }
         public IRubyObject to_i(Ruby runtime) {
             return runtime.newFixnum(shiftedValue());
         }
         
         @Override
         public IRubyObject to_s() {
             return to_s(getRuntime());
         }
         @JRubyMethod
         public IRubyObject to_s(ThreadContext context) {
             return to_s(context.getRuntime());
         }
         public IRubyObject to_s(Ruby runtime) {
             return runtime.newString(String.valueOf(shiftedValue()));
         }
         
         @Override
         public IRubyObject inspect() {
             return inspect(getRuntime());
         }
         @JRubyMethod
         public IRubyObject inspect(ThreadContext context) {
             return inspect(context.getRuntime());
         }
         public IRubyObject inspect(Ruby runtime) {
             return runtime.newString("#<Process::Status: pid=????,exited(" + String.valueOf(status) + ")>");
         }
         
         @JRubyMethod(name = "success?")
         public IRubyObject success_p(ThreadContext context) {
             return context.getRuntime().newBoolean(status == EXIT_SUCCESS);
         }
         
         private long shiftedValue() {
             return status << 8;
         }
     }
 
     @JRubyModule(name="Process::UID")
     public static class UserID {
         @JRubyMethod(name = "change_privilege", module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::change_privilege not implemented yet");
         }
         
         @Deprecated
         public static IRubyObject eid(IRubyObject self) {
             return euid(self.getRuntime());
         }
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self) {
             return euid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             return eid(self.getRuntime(), arg);
         }
         @JRubyMethod(name = "eid=", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self, IRubyObject arg) {
             return eid(context.getRuntime(), arg);
         }
         public static IRubyObject eid(Ruby runtime, IRubyObject arg) {
             return euid_set(runtime, arg);
         }
         
         @JRubyMethod(name = "grant_privilege", module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(ThreadContext context, IRubyObject self) {
             return switch_rb(context, self, Block.NULL_BLOCK);
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::re_exchangeable? not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject rid(IRubyObject self) {
             return rid(self.getRuntime());
         }
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(ThreadContext context, IRubyObject self) {
             return rid(context.getRuntime());
         }
         public static IRubyObject rid(Ruby runtime) {
             return uid(runtime);
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true, visibility = PRIVATE)
         public static IRubyObject switch_rb(ThreadContext context, IRubyObject self, Block block) {
             Ruby runtime = context.getRuntime();
             int uid = checkErrno(runtime, runtime.getPosix().getuid());
             int euid = checkErrno(runtime, runtime.getPosix().geteuid());
             
             if (block.isGiven()) {
                 try {
                     checkErrno(runtime, runtime.getPosix().seteuid(uid));
                     checkErrno(runtime, runtime.getPosix().setuid(euid));
                     
                     return block.yield(context, runtime.getNil());
                 } finally {
                     checkErrno(runtime, runtime.getPosix().seteuid(euid));
                     checkErrno(runtime, runtime.getPosix().setuid(uid));
                 }
             } else {
                 checkErrno(runtime, runtime.getPosix().seteuid(uid));
                 checkErrno(runtime, runtime.getPosix().setuid(euid));
                 
                 return RubyFixnum.zero(runtime);
             }
         }
     }
     
     @JRubyModule(name="Process::GID")
     public static class GroupID {
         @JRubyMethod(name = "change_privilege", module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::GID::change_privilege not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self) {
             return eid(self.getRuntime());
         }
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self) {
             return eid(context.getRuntime());
         }
         public static IRubyObject eid(Ruby runtime) {
             return egid(runtime);
         }
 
         @Deprecated
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             return eid(self.getRuntime(), arg);
         }
         @JRubyMethod(name = "eid=", module = true)
         public static IRubyObject eid(ThreadContext context, IRubyObject self, IRubyObject arg) {
             return eid(context.getRuntime(), arg);
         }
         public static IRubyObject eid(Ruby runtime, IRubyObject arg) {
             return RubyProcess.egid_set(runtime, arg);
         }
         
         @JRubyMethod(name = "grant_privilege", module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::GID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(ThreadContext context, IRubyObject self) {
             return switch_rb(context, self, Block.NULL_BLOCK);
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::GID::re_exchangeable? not implemented yet");
         }
 
         @Deprecated
         public static IRubyObject rid(IRubyObject self) {
             return rid(self.getRuntime());
         }
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(ThreadContext context, IRubyObject self) {
             return rid(context.getRuntime());
         }
         public static IRubyObject rid(Ruby runtime) {
             return gid(runtime);
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::GID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true, visibility = PRIVATE)
         public static IRubyObject switch_rb(ThreadContext context, IRubyObject self, Block block) {
             Ruby runtime = context.getRuntime();
             int gid = checkErrno(runtime, runtime.getPosix().getgid());
             int egid = checkErrno(runtime, runtime.getPosix().getegid());
             
             if (block.isGiven()) {
                 try {
                     checkErrno(runtime, runtime.getPosix().setegid(gid));
                     checkErrno(runtime, runtime.getPosix().setgid(egid));
                     
                     return block.yield(context, runtime.getNil());
                 } finally {
                     checkErrno(runtime, runtime.getPosix().setegid(egid));
                     checkErrno(runtime, runtime.getPosix().setgid(gid));
                 }
             } else {
                 checkErrno(runtime, runtime.getPosix().setegid(gid));
                 checkErrno(runtime, runtime.getPosix().setgid(egid));
                 
                 return RubyFixnum.zero(runtime);
             }
         }
     }
     
     @JRubyModule(name="Process::Sys")
     public static class Sys {
         @Deprecated
         public static IRubyObject getegid(IRubyObject self) {
             return egid(self.getRuntime());
         }
         @JRubyMethod(name = "getegid", module = true, visibility = PRIVATE)
         public static IRubyObject getegid(ThreadContext context, IRubyObject self) {
             return egid(context.getRuntime());
         }
         
         @Deprecated
         public static IRubyObject geteuid(IRubyObject self) {
             return euid(self.getRuntime());
         }
         @JRubyMethod(name = "geteuid", module = true, visibility = PRIVATE)
         public static IRubyObject geteuid(ThreadContext context, IRubyObject self) {
             return euid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject getgid(IRubyObject self) {
             return gid(self.getRuntime());
         }
         @JRubyMethod(name = "getgid", module = true, visibility = PRIVATE)
         public static IRubyObject getgid(ThreadContext context, IRubyObject self) {
             return gid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject getuid(IRubyObject self) {
             return uid(self.getRuntime());
         }
         @JRubyMethod(name = "getuid", module = true, visibility = PRIVATE)
         public static IRubyObject getuid(ThreadContext context, IRubyObject self) {
             return uid(context.getRuntime());
         }
 
         @Deprecated
         public static IRubyObject setegid(IRubyObject recv, IRubyObject arg) {
             return egid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setegid", module = true, visibility = PRIVATE)
         public static IRubyObject setegid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return egid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject seteuid(IRubyObject recv, IRubyObject arg) {
             return euid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "seteuid", module = true, visibility = PRIVATE)
         public static IRubyObject seteuid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return euid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject setgid(IRubyObject recv, IRubyObject arg) {
             return gid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setgid", module = true, visibility = PRIVATE)
         public static IRubyObject setgid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return gid_set(context.getRuntime(), arg);
         }
 
         @Deprecated
         public static IRubyObject setuid(IRubyObject recv, IRubyObject arg) {
             return uid_set(recv.getRuntime(), arg);
         }
         @JRubyMethod(name = "setuid", module = true, visibility = PRIVATE)
         public static IRubyObject setuid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
             return uid_set(context.getRuntime(), arg);
         }
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.abort(context, recv, args);
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.exit_bang(recv, args);
     }
 
     @JRubyMethod(name = "groups", module = true, visibility = PRIVATE)
     public static IRubyObject groups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @JRubyMethod(name = "setrlimit", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject setrlimit(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#setrlimit not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject getpgrp(IRubyObject recv) {
         return getpgrp(recv.getRuntime());
     }
     @JRubyMethod(name = "getpgrp", module = true, visibility = PRIVATE)
     public static IRubyObject getpgrp(ThreadContext context, IRubyObject recv) {
         return getpgrp(context.getRuntime());
     }
     public static IRubyObject getpgrp(Ruby runtime) {
         return runtime.newFixnum(runtime.getPosix().getpgrp());
     }
 
     @JRubyMethod(name = "groups=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject groups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject waitpid(IRubyObject recv, IRubyObject[] args) {
         return waitpid(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "waitpid", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject waitpid(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid(context.getRuntime(), args);
     }
     public static IRubyObject waitpid(Ruby runtime, IRubyObject[] args) {
         int pid = -1;
         int flags = 0;
         if (args.length > 0) {
             pid = (int)args[0].convertToInteger().getLongValue();
         }
         if (args.length > 1) {
             flags = (int)args[1].convertToInteger().getLongValue();
         }
         
         int[] status = new int[1];
-        pid = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, flags), ECHILD);
+        runtime.getPosix().errno(0);
+        pid = runtime.getPosix().waitpid(pid, status, flags);
+        raiseErrnoIfSet(runtime, ECHILD);
         
         runtime.getCurrentContext().setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
         return runtime.newFixnum(pid);
     }
 
     private interface NonNativeErrno {
         public int handle(Ruby runtime, int result);
     }
 
     private static final NonNativeErrno ECHILD = new NonNativeErrno() {
         public int handle(Ruby runtime, int result) {
             throw runtime.newErrnoECHILDError();
         }
     };
 
     @Deprecated
     public static IRubyObject wait(IRubyObject recv, IRubyObject[] args) {
         return wait(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "wait", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject wait(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return wait(context.getRuntime(), args);
     }
     public static IRubyObject wait(Ruby runtime, IRubyObject[] args) {
         
         if (args.length > 0) {
             return waitpid(runtime, args);
         }
         
         int[] status = new int[1];
-        int pid = checkErrno(runtime, runtime.getPosix().wait(status), ECHILD);
+        runtime.getPosix().errno(0);
+        int pid = runtime.getPosix().wait(status);
+        raiseErrnoIfSet(runtime, ECHILD);
         
         runtime.getCurrentContext().setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
         return runtime.newFixnum(pid);
     }
 
 
     @Deprecated
     public static IRubyObject waitall(IRubyObject recv) {
         return waitall(recv.getRuntime());
     }
     @JRubyMethod(name = "waitall", module = true, visibility = PRIVATE)
     public static IRubyObject waitall(ThreadContext context, IRubyObject recv) {
         return waitall(context.getRuntime());
     }
     public static IRubyObject waitall(Ruby runtime) {
         POSIX posix = runtime.getPosix();
         RubyArray results = runtime.newArray();
         
         int[] status = new int[1];
         int result = posix.wait(status);
         while (result != -1) {
             results.append(runtime.newArray(runtime.newFixnum(result), RubyProcess.RubyStatus.newProcessStatus(runtime, status[0])));
             result = posix.wait(status);
         }
         
         return results;
     }
 
     @Deprecated
     public static IRubyObject setsid(IRubyObject recv) {
         return setsid(recv.getRuntime());
     }
     @JRubyMethod(name = "setsid", module = true, visibility = PRIVATE)
     public static IRubyObject setsid(ThreadContext context, IRubyObject recv) {
         return setsid(context.getRuntime());
     }
     public static IRubyObject setsid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setsid()));
     }
 
     @Deprecated
     public static IRubyObject setpgrp(IRubyObject recv) {
         return setpgrp(recv.getRuntime());
     }
     @JRubyMethod(name = "setpgrp", module = true, visibility = PRIVATE)
     public static IRubyObject setpgrp(ThreadContext context, IRubyObject recv) {
         return setpgrp(context.getRuntime());
     }
     public static IRubyObject setpgrp(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setpgid(0, 0)));
     }
 
     @Deprecated
     public static IRubyObject egid_set(IRubyObject recv, IRubyObject arg) {
         return egid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "egid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject egid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return egid_set(context.getRuntime(), arg);
     }
     public static IRubyObject egid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().setegid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject euid(IRubyObject recv) {
         return euid(recv.getRuntime());
     }
     @JRubyMethod(name = "euid", module = true, visibility = PRIVATE)
     public static IRubyObject euid(ThreadContext context, IRubyObject recv) {
         return euid(context.getRuntime());
     }
     public static IRubyObject euid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().geteuid()));
     }
 
     @Deprecated
     public static IRubyObject uid_set(IRubyObject recv, IRubyObject arg) {
         return uid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "uid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject uid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return uid_set(context.getRuntime(), arg);
     }
     public static IRubyObject uid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().setuid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject gid(IRubyObject recv) {
         return gid(recv.getRuntime());
     }
     @JRubyMethod(name = "gid", module = true, visibility = PRIVATE)
     public static IRubyObject gid(ThreadContext context, IRubyObject recv) {
         return gid(context.getRuntime());
     }
     public static IRubyObject gid(Ruby runtime) {
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows
             return RubyFixnum.zero(runtime);
         }
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getgid()));
     }
 
     @JRubyMethod(name = "maxgroups", module = true, visibility = PRIVATE)
     public static IRubyObject maxgroups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject getpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return getpriority(recv.getRuntime(), arg1, arg2);
     }
     @JRubyMethod(name = "getpriority", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject getpriority(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return getpriority(context.getRuntime(), arg1, arg2);
     }
     public static IRubyObject getpriority(Ruby runtime, IRubyObject arg1, IRubyObject arg2) {
         int which = (int)arg1.convertToInteger().getLongValue();
         int who = (int)arg2.convertToInteger().getLongValue();
         int result = checkErrno(runtime, runtime.getPosix().getpriority(which, who));
         
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject uid(IRubyObject recv) {
         return uid(recv.getRuntime());
     }
     @JRubyMethod(name = "uid", module = true, visibility = PRIVATE)
     public static IRubyObject uid(ThreadContext context, IRubyObject recv) {
         return uid(context.getRuntime());
     }
     public static IRubyObject uid(Ruby runtime) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getuid()));
     }
 
     @Deprecated
     public static IRubyObject waitpid2(IRubyObject recv, IRubyObject[] args) {
         return waitpid2(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "waitpid2", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject waitpid2(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid2(context.getRuntime(), args);
     }
     public static IRubyObject waitpid2(Ruby runtime, IRubyObject[] args) {
         int pid = -1;
         int flags = 0;
         if (args.length > 0) {
             pid = (int)args[0].convertToInteger().getLongValue();
         }
         if (args.length > 1) {
             flags = (int)args[1].convertToInteger().getLongValue();
         }
         
         int[] status = new int[1];
         pid = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, flags), ECHILD);
         
         return runtime.newArray(runtime.newFixnum(pid), RubyProcess.RubyStatus.newProcessStatus(runtime, status[0]));
     }
 
     @JRubyMethod(name = "initgroups", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject initgroups(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         throw recv.getRuntime().newNotImplementedError("Process#initgroups not yet implemented");
     }
 
     @JRubyMethod(name = "maxgroups=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject maxgroups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups_set not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject ppid(IRubyObject recv) {
         return ppid(recv.getRuntime());
     }
     @JRubyMethod(name = "ppid", module = true, visibility = PRIVATE)
     public static IRubyObject ppid(ThreadContext context, IRubyObject recv) {
         return ppid(context.getRuntime());
     }
     public static IRubyObject ppid(Ruby runtime) {
         int result = checkErrno(runtime, runtime.getPosix().getppid());
 
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject gid_set(IRubyObject recv, IRubyObject arg) {
         return gid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "gid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return gid_set(context.getRuntime(), arg);
     }
     public static IRubyObject gid_set(Ruby runtime, IRubyObject arg) {
         int result = checkErrno(runtime, runtime.getPosix().setgid((int)arg.convertToInteger().getLongValue()));
 
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject wait2(IRubyObject recv, IRubyObject[] args) {
         return waitpid2(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "wait2", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject wait2(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return waitpid2(context.getRuntime(), args);
     }
 
     @Deprecated
     public static IRubyObject euid_set(IRubyObject recv, IRubyObject arg) {
         return euid_set(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "euid=", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject euid_set(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return euid_set(context.getRuntime(), arg);
     }
     public static IRubyObject euid_set(Ruby runtime, IRubyObject arg) {
         checkErrno(runtime, runtime.getPosix().seteuid((int)arg.convertToInteger().getLongValue()));
         return RubyFixnum.zero(runtime);
     }
 
     @Deprecated
     public static IRubyObject setpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return setpriority(recv.getRuntime(), arg1, arg2, arg3);
     }
     @JRubyMethod(name = "setpriority", required = 3, module = true, visibility = PRIVATE)
     public static IRubyObject setpriority(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         return setpriority(context.getRuntime(), arg1, arg2, arg3);
     }
     public static IRubyObject setpriority(Ruby runtime, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         int which = (int)arg1.convertToInteger().getLongValue();
         int who = (int)arg2.convertToInteger().getLongValue();
         int prio = (int)arg3.convertToInteger().getLongValue();
         runtime.getPosix().errno(0);
         int result = checkErrno(runtime, runtime.getPosix().setpriority(which, who, prio));
         
         return runtime.newFixnum(result);
     }
 
     @Deprecated
     public static IRubyObject setpgid(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return setpgid(recv.getRuntime(), arg1, arg2);
     }
     @JRubyMethod(name = "setpgid", required = 2, module = true, visibility = PRIVATE)
     public static IRubyObject setpgid(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         return setpgid(context.getRuntime(), arg1, arg2);
     }
     public static IRubyObject setpgid(Ruby runtime, IRubyObject arg1, IRubyObject arg2) {
         int pid = (int)arg1.convertToInteger().getLongValue();
         int gid = (int)arg2.convertToInteger().getLongValue();
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().setpgid(pid, gid)));
     }
 
     @Deprecated
     public static IRubyObject getpgid(IRubyObject recv, IRubyObject arg) {
         return getpgid(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "getpgid", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject getpgid(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return getpgid(context.getRuntime(), arg);
     }
     public static IRubyObject getpgid(Ruby runtime, IRubyObject arg) {
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getpgid((int)arg.convertToInteger().getLongValue())));
     }
 
     @Deprecated
     public static IRubyObject getrlimit(IRubyObject recv, IRubyObject arg) {
         return getrlimit(recv.getRuntime(), arg);
     }
     @JRubyMethod(name = "getrlimit", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject getrlimit(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return getrlimit(context.getRuntime(), arg);
     }
     public static IRubyObject getrlimit(Ruby runtime, IRubyObject arg) {
         throw runtime.newNotImplementedError("Process#getrlimit not yet implemented");
     }
 
     @Deprecated
     public static IRubyObject egid(IRubyObject recv) {
         return egid(recv.getRuntime());
     }
     @JRubyMethod(name = "egid", module = true, visibility = PRIVATE)
     public static IRubyObject egid(ThreadContext context, IRubyObject recv) {
         return egid(context.getRuntime());
     }
     public static IRubyObject egid(Ruby runtime) {
         if (Platform.IS_WINDOWS) {
             // MRI behavior on Windows
             return RubyFixnum.zero(runtime);
         }
         return runtime.newFixnum(checkErrno(runtime, runtime.getPosix().getegid()));
     }
     
     private static String[] signals = new String[] {"EXIT", "HUP", "INT", "QUIT", "ILL", "TRAP", 
         "ABRT", "POLL", "FPE", "KILL", "BUS", "SEGV", "SYS", "PIPE", "ALRM", "TERM", "URG", "STOP",
         "TSTP", "CONT", "CHLD", "TTIN", "TTOU", "XCPU", "XFSZ", "VTALRM", "PROF", "USR1", "USR2"};
     
     private static int parseSignalString(Ruby runtime, String value) {
         int startIndex = 0;
         boolean negative = value.startsWith("-");
         
         if (negative) startIndex++;
         
         boolean signalString = value.startsWith("SIG", startIndex);
         
         if (signalString) startIndex += 3;
        
         String signalName = value.substring(startIndex);
         
         // FIXME: This table will get moved into POSIX library so we can get all actual supported
         // signals.  This is a quick fix to support basic signals until that happens.
         for (int i = 0; i < signals.length; i++) {
             if (signals[i].equals(signalName)) return negative ? -i : i;
         }
         
         throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
     }
 
     @Deprecated
     public static IRubyObject kill(IRubyObject recv, IRubyObject[] args) {
         return kill(recv.getRuntime(), args);
     }
     @JRubyMethod(name = "kill", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject kill(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return kill(context.getRuntime(), args);
     }
     public static IRubyObject kill(Ruby runtime, IRubyObject[] args) {
         if (args.length < 2) {
             throw runtime.newArgumentError("wrong number of arguments -- kill(sig, pid...)");
         }
 
         // Windows does not support these functions, so we won't even try
         // This also matches Ruby behavior for JRUBY-2353.
         if (Platform.IS_WINDOWS) {
             return runtime.getNil();
         }
         
         int signal;
         if (args[0] instanceof RubyFixnum) {
             signal = (int) ((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubySymbol) {
             signal = parseSignalString(runtime, args[0].toString());
         } else if (args[0] instanceof RubyString) {
             signal = parseSignalString(runtime, args[0].toString());
         } else {
             signal = parseSignalString(runtime, args[0].checkStringType().toString());
         }
 
         boolean processGroupKill = signal < 0;
         
         if (processGroupKill) signal = -signal;
         
         POSIX posix = runtime.getPosix();
         for (int i = 1; i < args.length; i++) {
             int pid = RubyNumeric.num2int(args[i]);
 
             // FIXME: It may be possible to killpg on systems which support it.  POSIX library
             // needs to tell whether a particular method works or not
             if (pid == 0) pid = runtime.getPosix().getpid();
             checkErrno(runtime, posix.kill(processGroupKill ? -pid : pid, signal));
         }
         
         return runtime.newFixnum(args.length - 1);
 
     }
 
     @JRubyMethod(name = "detach", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject detach(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         final int pid = (int)arg.convertToInteger().getLongValue();
         Ruby runtime = context.getRuntime();
         
         BlockCallback callback = new BlockCallback() {
             public IRubyObject call(ThreadContext context, IRubyObject[] args, Block block) {
                 int[] status = new int[1];
                 Ruby runtime = context.runtime;
                 int result = checkErrno(runtime, runtime.getPosix().waitpid(pid, status, 0));
                 
                 return runtime.newFixnum(result);
             }
         };
         
         return RubyThread.newInstance(
                 runtime.getThread(),
                 IRubyObject.NULL_ARRAY,
                 CallBlock.newCallClosure(recv, (RubyModule)recv, Arity.NO_ARGUMENTS, callback, context));
     }
 
     @Deprecated
     public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
         return times(recv.getRuntime());
     }
     @JRubyMethod(module = true, visibility = PRIVATE)
     public static IRubyObject times(ThreadContext context, IRubyObject recv, Block unusedBlock) {
         return times(context.getRuntime());
     }
     public static IRubyObject times(Ruby runtime) {
         double currentTime = System.currentTimeMillis() / 1000.0;
         double startTime = runtime.getStartTime() / 1000.0;
         RubyFloat zero = runtime.newFloat(0.0);
         return RubyStruct.newStruct(runtime.getTmsStruct(), 
                 new IRubyObject[] { runtime.newFloat(currentTime - startTime), zero, zero, zero }, 
                 Block.NULL_BLOCK);
     }
 
     @Deprecated
     public static IRubyObject pid(IRubyObject recv) {
         return pid(recv.getRuntime());
     }
     @JRubyMethod(name = "pid", module = true, visibility = PRIVATE)
     public static IRubyObject pid(ThreadContext context, IRubyObject recv) {
         return pid(context.getRuntime());
     }
     public static IRubyObject pid(Ruby runtime) {
         return runtime.newFixnum(runtime.getPosix().getpid());
     }
     
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         return RubyKernel.fork(context, recv, block);
     }
 
     @JRubyMethod(name = "spawn", required = 1, rest = true, module = true, compat = CompatVersion.RUBY1_9)
     public static RubyFixnum spawn(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         long pid = ShellLauncher.runWithoutWait(runtime, args);
         return RubyFixnum.newFixnum(runtime, pid);
     }
     
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         return RubyKernel.exit(recv, args);
     }
     
     private static final NonNativeErrno IGNORE = new NonNativeErrno() {
         public int handle(Ruby runtime, int result) {return result;}
     };
 
     private static int checkErrno(Ruby runtime, int result) {
         return checkErrno(runtime, result, IGNORE);
     }
 
     private static int checkErrno(Ruby runtime, int result, NonNativeErrno nonNative) {
         if (result == -1) {
             if (runtime.getPosix().isNative()) {
-                throw runtime.newErrnoFromInt(runtime.getPosix().errno());
+                raiseErrnoIfSet(runtime, nonNative);
             } else {
                 nonNative.handle(runtime, result);
             }
         }
         return result;
     }
+
+    private static void raiseErrnoIfSet(Ruby runtime, NonNativeErrno nonNative) {
+        if (runtime.getPosix().errno() != 0) {
+            throw runtime.newErrnoFromInt(runtime.getPosix().errno());
+        }
+    }
 }
diff --git a/src/org/jruby/util/ShellLauncher.java b/src/org/jruby/util/ShellLauncher.java
index 2d7b7ef72d..ea257f1737 100644
--- a/src/org/jruby/util/ShellLauncher.java
+++ b/src/org/jruby/util/ShellLauncher.java
@@ -1,1360 +1,1373 @@
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
  * Copyright (C) 2007-2011 JRuby Team <team@jruby.org>
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
 
 package org.jruby.util;
 
 import com.kenai.jaffl.FFIProvider;
 import static java.lang.System.out;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.FilterOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PipedInputStream;
 import java.io.PipedOutputStream;
 import java.io.PrintStream;
 import java.lang.reflect.Field;
 import java.nio.ByteBuffer;
 import java.nio.channels.FileChannel;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Main;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyHash;
 import org.jruby.RubyIO;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.ext.posix.util.FieldAccess;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.ModeFlags;
 
 /**
  * This mess of a class is what happens when all Java gives you is
  * Runtime.getRuntime().exec(). Thanks dude, that really helped.
  * @author nicksieger
  */
 public class ShellLauncher {
     private static final boolean DEBUG = false;
 
     private static final String PATH_ENV = "PATH";
 
     // from MRI -- note the unixy file separators
     private static final String[] DEFAULT_PATH =
         { "/usr/local/bin", "/usr/ucb", "/usr/bin", "/bin" };
 
     private static final String[] WINDOWS_EXE_SUFFIXES =
         { ".exe", ".com", ".bat", ".cmd" }; // the order is important
 
     private static final String[] WINDOWS_INTERNAL_CMDS = {
         "assoc", "break", "call", "cd", "chcp",
         "chdir", "cls", "color", "copy", "ctty", "date", "del", "dir", "echo", "endlocal",
         "erase", "exit", "for", "ftype", "goto", "if", "lfnfor", "lh", "lock", "md", "mkdir",
         "move", "path", "pause", "popd", "prompt", "pushd", "rd", "rem", "ren", "rename",
         "rmdir", "set", "setlocal", "shift", "start", "time", "title", "truename", "type",
         "unlock", "ver", "verify", "vol", };
 
     // TODO: better check is needed, with quoting/escaping
     private static final Pattern SHELL_METACHARACTER_PATTERN =
         Pattern.compile("[*?{}\\[\\]<>()~&|$;'`\\\\\"\\n]");
 
     private static final Pattern WIN_ENVVAR_PATTERN = Pattern.compile("%\\w+%");
 
     private static class ScriptThreadProcess extends Process implements Runnable {
         private final String[] argArray;
         private final String[] env;
         private final File pwd;
         private final boolean pipedStreams;
         private final PipedInputStream processOutput;
         private final PipedInputStream processError;
         private final PipedOutputStream processInput;
 
         private RubyInstanceConfig config;
         private Thread processThread;
         private int result;
         private Ruby parentRuntime;
 
         public ScriptThreadProcess(Ruby parentRuntime, final String[] argArray, final String[] env, final File dir) {
             this(parentRuntime, argArray, env, dir, true);
         }
 
         public ScriptThreadProcess(Ruby parentRuntime, final String[] argArray, final String[] env, final File dir, final boolean pipedStreams) {
             this.parentRuntime = parentRuntime;
             this.argArray = argArray;
             this.env = env;
             this.pwd = dir;
             this.pipedStreams = pipedStreams;
             if (pipedStreams) {
                 processOutput = new PipedInputStream();
                 processError = new PipedInputStream();
                 processInput = new PipedOutputStream();
             } else {
                 processOutput = processError = null;
                 processInput = null;
             }
         }
         public void run() {
             try {
                 this.result = (new Main(config).run(argArray)).getStatus();
             } catch (Throwable throwable) {
                 throwable.printStackTrace(this.config.getError());
                 this.result = -1;
             } finally {
                 this.config.getOutput().close();
                 this.config.getError().close();
                 try {this.config.getInput().close();} catch (IOException ioe) {}
             }
         }
 
         private Map<String, String> environmentMap(String[] env) {
             Map<String, String> m = new HashMap<String, String>();
             for (int i = 0; i < env.length; i++) {
                 String[] kv = env[i].split("=", 2);
                 m.put(kv[0], kv[1]);
             }
             return m;
         }
 
         public void start() throws IOException {
             config = new RubyInstanceConfig(parentRuntime.getInstanceConfig()) {{
                 setEnvironment(environmentMap(env));
                 setCurrentDirectory(pwd.toString());
             }};
             if (pipedStreams) {
                 config.setInput(new PipedInputStream(processInput));
                 config.setOutput(new PrintStream(new PipedOutputStream(processOutput)));
                 config.setError(new PrintStream(new PipedOutputStream(processError)));
             }
             String procName = "piped";
             if (argArray.length > 0) {
                 procName = argArray[0];
             }
             processThread = new Thread(this, "ScriptThreadProcess: " + procName);
             processThread.setDaemon(true);
             processThread.start();
         }
 
         public OutputStream getOutputStream() {
             return processInput;
         }
 
         public InputStream getInputStream() {
             return processOutput;
         }
 
         public InputStream getErrorStream() {
             return processError;
         }
 
         public int waitFor() throws InterruptedException {
             processThread.join();
             return result;
         }
 
         public int exitValue() {
             return result;
         }
 
         public void destroy() {
             if (pipedStreams) {
                 closeStreams();
             }
             processThread.interrupt();
         }
 
         private void closeStreams() {
             try { processInput.close(); } catch (IOException io) {}
             try { processOutput.close(); } catch (IOException io) {}
             try { processError.close(); } catch (IOException io) {}
         }
     }
 
     private static String[] getCurrentEnv(Ruby runtime) {
         return getCurrentEnv(runtime, null);
     }
 
     private static String[] getCurrentEnv(Ruby runtime, Map mergeEnv) {
         RubyHash hash = (RubyHash)runtime.getObject().fastGetConstant("ENV");
         String[] ret;
         
         if (mergeEnv != null && !mergeEnv.isEmpty()) {
             ret = new String[hash.size() + mergeEnv.size()];
         } else {
             ret = new String[hash.size()];
         }
 
         int i=0;
         for(Map.Entry e : (Set<Map.Entry>)hash.directEntrySet()) {
             ret[i] = e.getKey().toString() + "=" + e.getValue().toString();
             i++;
         }
         if (mergeEnv != null) for(Map.Entry e : (Set<Map.Entry>)mergeEnv.entrySet()) {
             ret[i] = e.getKey().toString() + "=" + e.getValue().toString();
             i++;
         }
 
         return ret;
     }
 
     private static boolean filenameIsPathSearchable(String fname, boolean forExec) {
         boolean isSearchable = true;
         if (fname.startsWith("/")   ||
             fname.startsWith("./")  ||
             fname.startsWith("../") ||
             (forExec && (fname.indexOf("/") != -1))) {
             isSearchable = false;
         }
         if (Platform.IS_WINDOWS) {
             if (fname.startsWith("\\")  ||
                 fname.startsWith(".\\") ||
                 fname.startsWith("..\\") ||
                 ((fname.length() > 2) && fname.startsWith(":",1)) ||
                 (forExec && (fname.indexOf("\\") != -1))) {
                 isSearchable = false;
             }
         }
         return isSearchable;
     }
 
     private static File tryFile(Ruby runtime, String fdir, String fname) {
         File pathFile;
         if (fdir == null) {
             pathFile = new File(fname);
         } else {
             pathFile = new File(fdir, fname);
         }
 
         if (!pathFile.isAbsolute()) {
             pathFile = new File(runtime.getCurrentDirectory(), pathFile.getPath());
         }
 
         log(runtime, "Trying file " + pathFile);
         if (pathFile.exists()) {
             return pathFile;
         } else {
             return null;
         }
     }
 
     private static boolean withExeSuffix(String fname) {
         String lowerCaseFname = fname.toLowerCase();
         for (String suffix : WINDOWS_EXE_SUFFIXES) {
             if (lowerCaseFname.endsWith(suffix)) {
                 return true;
             }
         }
         return false;
     }
 
     private static File isValidFile(Ruby runtime, String fdir, String fname, boolean isExec) {
         File validFile = null;
         if (isExec && Platform.IS_WINDOWS) {
             if (withExeSuffix(fname)) {
                 validFile = tryFile(runtime, fdir, fname);
             } else {
                 for (String suffix: WINDOWS_EXE_SUFFIXES) {
                     validFile = tryFile(runtime, fdir, fname + suffix);
                     if (validFile != null) {
                         // found a valid file, no need to search further
                         break;
                     }
                 }
             }
         } else {
             File pathFile = tryFile(runtime, fdir, fname);
             if (pathFile != null) {
                 if (isExec) {
                     if (!pathFile.isDirectory()) {
                         String pathFileStr = pathFile.getAbsolutePath();
                         POSIX posix = runtime.getPosix();
                         if (posix.stat(pathFileStr).isExecutable()) {
                             validFile = pathFile;
                         }
                     }
                 } else {
                     validFile = pathFile;
                 }
             }
         }
         return validFile;
     }
 
     private static File isValidFile(Ruby runtime, String fname, boolean isExec) {
         String fdir = null;
         return isValidFile(runtime, fdir, fname, isExec);
     }
 
     private static File findPathFile(Ruby runtime, String fname, String[] path, boolean isExec) {
         File pathFile = null;
         boolean doPathSearch = filenameIsPathSearchable(fname, isExec);
         if (doPathSearch) {
             for (String fdir: path) {
                 // NOTE: Jruby's handling of tildes is more complete than
                 //       MRI's, which can't handle user names after the tilde
                 //       when searching the executable path
                 pathFile = isValidFile(runtime, fdir, fname, isExec);
                 if (pathFile != null) {
                     break;
                 }
             }
         } else {
             pathFile = isValidFile(runtime, fname, isExec);
         }
         return pathFile;
     }
 
     private static File findPathExecutable(Ruby runtime, String fname) {
         RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         IRubyObject pathObject = env.op_aref(runtime.getCurrentContext(), RubyString.newString(runtime, PATH_ENV));
         String[] pathNodes = null;
         if (pathObject == null) {
             pathNodes = DEFAULT_PATH; // ASSUME: not modified by callee
         }
         else {
             String pathSeparator = System.getProperty("path.separator");
             String path = pathObject.toString();
             if (Platform.IS_WINDOWS) {
                 // Windows-specific behavior
                 path = "." + pathSeparator + path;
             }
             pathNodes = path.split(pathSeparator);
         }
         return findPathFile(runtime, fname, pathNodes, true);
     }
 
     public static int runAndWait(Ruby runtime, IRubyObject[] rawArgs) {
         return runAndWait(runtime, rawArgs, runtime.getOutputStream());
     }
 
     public static long runWithoutWait(Ruby runtime, IRubyObject[] rawArgs) {
         return runWithoutWait(runtime, rawArgs, runtime.getOutputStream());
     }
 
     public static int execAndWait(Ruby runtime, IRubyObject[] rawArgs) {
         File pwd = new File(runtime.getCurrentDirectory());
         LaunchConfig cfg = new LaunchConfig(runtime, rawArgs, true);
 
         if (cfg.shouldRunInProcess()) {
             log(runtime, "ExecAndWait in-process");
             try {
                 // exec needs to behave differently in-process, because it's technically
                 // supposed to replace the calling process. So if we're supposed to run
                 // in-process, we allow it to use the default streams and not use
                 // pumpers at all. See JRUBY-2156 and JRUBY-2154.
                 ScriptThreadProcess ipScript = new ScriptThreadProcess(
                         runtime, cfg.getExecArgs(), getCurrentEnv(runtime), pwd, false);
                 ipScript.start();
                 return ipScript.waitFor();
             } catch (IOException e) {
                 throw runtime.newIOErrorFromException(e);
             } catch (InterruptedException e) {
                 throw runtime.newThreadError("unexpected interrupt");
             }
         } else {
             return runAndWait(runtime, rawArgs);
         }
     }
 
     public static int runAndWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
         return runAndWait(runtime, rawArgs, output, true);
     }
 
     public static int runAndWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output, boolean doExecutableSearch) {
         OutputStream error = runtime.getErrorStream();
         InputStream input = runtime.getInputStream();
         try {
             Process aProcess = run(runtime, rawArgs, doExecutableSearch);
             handleStreams(runtime, aProcess, input, output, error);
             return aProcess.waitFor();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     public static long runWithoutWait(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
+        OutputStream error = runtime.getErrorStream();
         try {
-            POpenProcess aProcess = new POpenProcess(popenShared(runtime, rawArgs));
+            Process aProcess = run(runtime, rawArgs, true);
+            handleStreamsNonblocking(runtime, aProcess, output, error);
             return getPidFromProcess(aProcess);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     public static long getPidFromProcess(Process process) {
         if (process instanceof ScriptThreadProcess) {
             return process.hashCode();
         } else if (process instanceof POpenProcess) {
             return reflectPidFromProcess(((POpenProcess)process).getChild());
         } else {
             return reflectPidFromProcess(process);
         }
     }
     
     private static final Class UNIXProcess;
     private static final Field UNIXProcess_pid;
     private static final Class ProcessImpl;
     private static final Field ProcessImpl_handle;
     private interface PidGetter { public long getPid(Process process); }
     private static final PidGetter PID_GETTER;
     
     static {
         // default PidGetter
         PidGetter pg = new PidGetter() {
             public long getPid(Process process) {
                 return process.hashCode();
             }
         };
         
         Class up = null;
         Field pid = null;
         try {
             up = Class.forName("java.lang.UNIXProcess");
             pid = up.getDeclaredField("pid");
             pid.setAccessible(true);
         } catch (Exception e) {
             // ignore and try windows version
         }
         UNIXProcess = up;
         UNIXProcess_pid = pid;
 
         Class pi = null;
         Field handle = null;
         try {
             pi = Class.forName("java.lang.ProcessImpl");
             handle = pi.getDeclaredField("handle");
             handle.setAccessible(true);
         } catch (Exception e) {
             // ignore and use hashcode
         }
         ProcessImpl = pi;
         ProcessImpl_handle = handle;
 
         if (UNIXProcess_pid != null) {
             if (ProcessImpl_handle != null) {
                 // try both
                 pg = new PidGetter() {
                     public long getPid(Process process) {
                         try {
                             if (UNIXProcess.isInstance(process)) {
                                 return (Integer)UNIXProcess_pid.get(process);
                             } else if (ProcessImpl.isInstance(process)) {
                                 Long hproc = (Long) ProcessImpl_handle.get(process);
                                 return WindowsFFI.getKernel32(FFIProvider.getProvider())
                                     .GetProcessId(new com.kenai.jaffl.NativeLong(hproc));
                             }
                         } catch (Exception e) {
                             // ignore and use hashcode
                         }
                         return process.hashCode();
                     }
                 };
             } else {
                 // just unix
                 pg = new PidGetter() {
                     public long getPid(Process process) {
                         try {
                             if (UNIXProcess.isInstance(process)) {
                                 return (Integer)UNIXProcess_pid.get(process);
                             }
                         } catch (Exception e) {
                             // ignore and use hashcode
                         }
                         return process.hashCode();
                     }
                 };
             }
         } else if (ProcessImpl_handle != null) {
             // just windows
             pg = new PidGetter() {
                 public long getPid(Process process) {
                     try {
                         if (ProcessImpl.isInstance(process)) {
                             Long hproc = (Long) ProcessImpl_handle.get(process);
                             return WindowsFFI.getKernel32(FFIProvider.getProvider())
                                 .GetProcessId(new com.kenai.jaffl.NativeLong(hproc));
                         }
 
                     } catch (Exception e) {
                         // ignore and use hashcode
                     }
                     return process.hashCode();
                 }
             };
         } else {
             // neither
             pg = new PidGetter() {
                 public long getPid(Process process) {
                     return process.hashCode();
                 }
             };
         }
         PID_GETTER = pg;
     }
 
     public static long reflectPidFromProcess(Process process) {
         return PID_GETTER.getPid(process);
     }
 
     public static Process run(Ruby runtime, IRubyObject string) throws IOException {
         return run(runtime, new IRubyObject[] {string}, false);
     }
 
     public static POpenProcess popen(Ruby runtime, IRubyObject string, ModeFlags modes) throws IOException {
         return new POpenProcess(popenShared(runtime, new IRubyObject[] {string}), runtime, modes);
     }
 
     public static POpenProcess popen(Ruby runtime, IRubyObject[] strings, Map env, ModeFlags modes) throws IOException {
         return new POpenProcess(popenShared(runtime, strings, env), runtime, modes);
     }
 
     public static POpenProcess popen3(Ruby runtime, IRubyObject[] strings) throws IOException {
         return new POpenProcess(popenShared(runtime, strings));
     }
 
     private static Process popenShared(Ruby runtime, IRubyObject[] strings) throws IOException {
         return popenShared(runtime, strings, null);
     }
 
     private static Process popenShared(Ruby runtime, IRubyObject[] strings, Map env) throws IOException {
         String shell = getShell(runtime);
         Process childProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
 
         try {
             String[] args = parseCommandLine(runtime.getCurrentContext(), runtime, strings);
             boolean useShell = false;
             for (String arg : args) useShell |= shouldUseShell(arg);
             
             // CON: popen is a case where I think we should just always shell out.
             if (strings.length == 1) {
                 if (useShell) {
                     // single string command, pass to sh to expand wildcards
                     String[] argArray = new String[3];
                     argArray[0] = shell;
                     argArray[1] = shell.endsWith("sh") ? "-c" : "/c";
                     argArray[2] = strings[0].asJavaString();
                     childProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime, env), pwd);
                 } else {
                     childProcess = Runtime.getRuntime().exec(args, getCurrentEnv(runtime, env), pwd);
                 }
             } else {
                 if (useShell) {
                     String[] argArray = new String[args.length + 2];
                     argArray[0] = shell;
                     argArray[1] = shell.endsWith("sh") ? "-c" : "/c";
                     System.arraycopy(args, 0, argArray, 2, args.length);
                     childProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime, env), pwd);
                 } else {
                     // direct invocation of the command
                     childProcess = Runtime.getRuntime().exec(args, getCurrentEnv(runtime, env), pwd);
                 }
             }
         } catch (SecurityException se) {
             throw runtime.newSecurityError(se.getLocalizedMessage());
         }
 
         return childProcess;
     }
 
     /**
      * Unwrap all filtering streams between the given stream and its actual
      * unfiltered stream. This is primarily to unwrap streams that have
      * buffers that would interfere with interactivity.
      *
      * @param filteredStream The stream to unwrap
      * @return An unwrapped stream, presumably unbuffered
      */
     public static OutputStream unwrapBufferedStream(OutputStream filteredStream) {
         if (RubyInstanceConfig.NO_UNWRAP_PROCESS_STREAMS) return filteredStream;
         while (filteredStream instanceof FilterOutputStream) {
             try {
                 filteredStream = (OutputStream)
                     FieldAccess.getProtectedFieldValue(FilterOutputStream.class,
                         "out", filteredStream);
             } catch (Exception e) {
                 break; // break out if we've dug as deep as we can
             }
         }
         return filteredStream;
     }
 
     /**
      * Unwrap all filtering streams between the given stream and its actual
      * unfiltered stream. This is primarily to unwrap streams that have
      * buffers that would interfere with interactivity.
      *
      * @param filteredStream The stream to unwrap
      * @return An unwrapped stream, presumably unbuffered
      */
     public static InputStream unwrapBufferedStream(InputStream filteredStream) {
         if (RubyInstanceConfig.NO_UNWRAP_PROCESS_STREAMS) return filteredStream;
         while (filteredStream instanceof BufferedInputStream) {
             try {
                 filteredStream = (InputStream)
                     FieldAccess.getProtectedFieldValue(BufferedInputStream.class,
                         "in", filteredStream);
             } catch (Exception e) {
                 break; // break out if we've dug as deep as we can
             }
         }
         return filteredStream;
     }
 
     public static class POpenProcess extends Process {
         private final Process child;
 
         // real stream references, to keep them from being GCed prematurely
         private InputStream realInput;
         private OutputStream realOutput;
         private InputStream realInerr;
 
         private InputStream input;
         private OutputStream output;
         private InputStream inerr;
         private FileChannel inputChannel;
         private FileChannel outputChannel;
         private FileChannel inerrChannel;
         private Pumper inputPumper;
         private Pumper inerrPumper;
         private Pumper outputPumper;
 
         public POpenProcess(Process child, Ruby runtime, ModeFlags modes) {
             this.child = child;
 
             if (modes.isWritable()) {
                 prepareOutput(child);
             } else {
                 // close process output
                 // See JRUBY-3405; hooking up to parent process stdin caused
                 // problems for IRB etc using stdin.
                 try {child.getOutputStream().close();} catch (IOException ioe) {}
             }
 
             if (modes.isReadable()) {
                 prepareInput(child);
             } else {
                 pumpInput(child, runtime);
             }
 
             pumpInerr(child, runtime);
         }
 
         public POpenProcess(Process child) {
             this.child = child;
 
             prepareOutput(child);
             prepareInput(child);
             prepareInerr(child);
         }
 
         @Override
         public OutputStream getOutputStream() {
             return output;
         }
 
         @Override
         public InputStream getInputStream() {
             return input;
         }
 
         @Override
         public InputStream getErrorStream() {
             return inerr;
         }
 
         public FileChannel getInput() {
             return inputChannel;
         }
 
         public FileChannel getOutput() {
             return outputChannel;
         }
 
         public FileChannel getError() {
             return inerrChannel;
         }
 
         public boolean hasOutput() {
             return output != null || outputChannel != null;
         }
 
         public Process getChild() {
             return child;
         }
 
         @Override
         public int waitFor() throws InterruptedException {
             if (outputPumper == null) {
                 try {
                     if (output != null) output.close();
                 } catch (IOException ioe) {
                     // ignore, we're on the way out
                 }
             } else {
                 outputPumper.quit();
             }
 
             int result = child.waitFor();
 
             return result;
         }
 
         @Override
         public int exitValue() {
             return child.exitValue();
         }
 
         @Override
         public void destroy() {
             try {
                 if (input != null) input.close();
                 if (inerr != null) inerr.close();
                 if (output != null) output.close();
                 if (inputChannel != null) inputChannel.close();
                 if (inerrChannel != null) inerrChannel.close();
                 if (outputChannel != null) outputChannel.close();
 
                 // processes seem to have some peculiar locking sequences, so we
                 // need to ensure nobody is trying to close/destroy while we are
                 synchronized (this) {
                     RubyIO.obliterateProcess(child);
                     if (inputPumper != null) synchronized(inputPumper) {inputPumper.quit();}
                     if (inerrPumper != null) synchronized(inerrPumper) {inerrPumper.quit();}
                     if (outputPumper != null) synchronized(outputPumper) {outputPumper.quit();}
                 }
             } catch (IOException ioe) {
                 throw new RuntimeException(ioe);
             }
         }
 
         private void prepareInput(Process child) {
             // popen callers wants to be able to read, provide subprocess in directly
             realInput = child.getInputStream();
             input = unwrapBufferedStream(realInput);
             if (input instanceof FileInputStream) {
                 inputChannel = ((FileInputStream) input).getChannel();
             } else {
                 inputChannel = null;
             }
             inputPumper = null;
         }
 
         private void prepareInerr(Process child) {
             // popen callers wants to be able to read, provide subprocess in directly
             realInerr = child.getErrorStream();
             inerr = unwrapBufferedStream(realInerr);
             if (inerr instanceof FileInputStream) {
                 inerrChannel = ((FileInputStream) inerr).getChannel();
             } else {
                 inerrChannel = null;
             }
             inerrPumper = null;
         }
 
         private void prepareOutput(Process child) {
             // popen caller wants to be able to write, provide subprocess out directly
             realOutput = child.getOutputStream();
             output = unwrapBufferedStream(realOutput);
             if (output instanceof FileOutputStream) {
                 outputChannel = ((FileOutputStream) output).getChannel();
             } else {
                 outputChannel = null;
             }
             outputPumper = null;
         }
 
         private void pumpInput(Process child, Ruby runtime) {
             // no read requested, hook up read to parents output
             InputStream childIn = unwrapBufferedStream(child.getInputStream());
             FileChannel childInChannel = null;
             if (childIn instanceof FileInputStream) {
                 childInChannel = ((FileInputStream) childIn).getChannel();
             }
             OutputStream parentOut = unwrapBufferedStream(runtime.getOut());
             FileChannel parentOutChannel = null;
             if (parentOut instanceof FileOutputStream) {
                 parentOutChannel = ((FileOutputStream) parentOut).getChannel();
             }
             if (childInChannel != null && parentOutChannel != null) {
                 inputPumper = new ChannelPumper(runtime, childInChannel, parentOutChannel, Pumper.Slave.IN, this);
             } else {
                 inputPumper = new StreamPumper(runtime, childIn, parentOut, false, Pumper.Slave.IN, this);
             }
             inputPumper.start();
             input = null;
             inputChannel = null;
         }
 
         private void pumpInerr(Process child, Ruby runtime) {
             // no read requested, hook up read to parents output
             InputStream childIn = unwrapBufferedStream(child.getErrorStream());
             FileChannel childInChannel = null;
             if (childIn instanceof FileInputStream) {
                 childInChannel = ((FileInputStream) childIn).getChannel();
             }
             OutputStream parentOut = unwrapBufferedStream(runtime.getOut());
             FileChannel parentOutChannel = null;
             if (parentOut instanceof FileOutputStream) {
                 parentOutChannel = ((FileOutputStream) parentOut).getChannel();
             }
             if (childInChannel != null && parentOutChannel != null) {
                 inerrPumper = new ChannelPumper(runtime, childInChannel, parentOutChannel, Pumper.Slave.IN, this);
             } else {
                 inerrPumper = new StreamPumper(runtime, childIn, parentOut, false, Pumper.Slave.IN, this);
             }
             inerrPumper.start();
             inerr = null;
             inerrChannel = null;
         }
     }
 
     private static class LaunchConfig {
         LaunchConfig(Ruby runtime, IRubyObject[] rawArgs, boolean doExecutableSearch) {
             this.runtime = runtime;
             this.rawArgs = rawArgs;
             this.doExecutableSearch = doExecutableSearch;
             shell = getShell(runtime);
             args = parseCommandLine(runtime.getCurrentContext(), runtime, rawArgs);
         }
 
         /**
          * Only run an in-process script if the script name has "ruby", ".rb",
          * or "irb" in the name.
          */
         private boolean shouldRunInProcess() {
             if (!runtime.getInstanceConfig().isRunRubyInProcess()) {
                 return false;
             }
 
             // Check for special shell characters [<>|] at the beginning
             // and end of each command word and don't run in process if we find them.
             for (int i = 0; i < args.length; i++) {
                 String c = args[i];
                 if (c.trim().length() == 0) {
                     continue;
                 }
                 char[] firstLast = new char[] {c.charAt(0), c.charAt(c.length()-1)};
                 for (int j = 0; j < firstLast.length; j++) {
                     switch (firstLast[j]) {
                     case '<': case '>': case '|': case ';':
                     case '*': case '?': case '{': case '}':
                     case '[': case ']': case '(': case ')':
                     case '~': case '&': case '$': case '"':
                     case '`': case '\n': case '\\': case '\'':
                         return false;
                     case '2':
                         if(c.length() > 1 && c.charAt(1) == '>') {
                             return false;
                         }
                     }
                 }
             }
 
             String command = args[0];
 
             if (Platform.IS_WINDOWS) {
                 command = command.toLowerCase();
             }
 
             // handle both slash types, \ and /.
             String[] slashDelimitedTokens = command.split("[/\\\\]");
             String finalToken = slashDelimitedTokens[slashDelimitedTokens.length - 1];
             boolean inProc = (finalToken.endsWith("ruby")
                     || (Platform.IS_WINDOWS && finalToken.endsWith("ruby.exe"))
                     || finalToken.endsWith(".rb")
                     || finalToken.endsWith("irb"));
 
             if (!inProc) {
                 return false;
             } else {
                 // snip off ruby or jruby command from list of arguments
                 // leave alone if the command is the name of a script
                 int startIndex = command.endsWith(".rb") ? 0 : 1;
                 if (command.trim().endsWith("irb")) {
                     startIndex = 0;
                     args[0] = runtime.getJRubyHome() + File.separator + "bin" + File.separator + "jirb";
                 }
                 execArgs = new String[args.length - startIndex];
                 System.arraycopy(args, startIndex, execArgs, 0, execArgs.length);
                 return true;
             }
         }
 
         /**
          * This hack is to work around a problem with cmd.exe on windows where it can't
          * interpret a filename with spaces in the first argument position as a command.
          * In that case it's better to try passing the bare arguments to runtime.exec.
          * On all other platforms we'll always run the command in the shell.
          */
         private boolean shouldRunInShell() {
             if (rawArgs.length != 1) {
                 // this is the case when exact executable and its parameters passed,
                 // in such cases MRI just executes it, without any shell.
                 return false;
             }
 
             // in one-arg form, we always use shell, except for Windows
             if (!Platform.IS_WINDOWS) return true;
 
             // now, deal with Windows
             if (shell == null) return false;
 
             // TODO: Better name for the method
             // Essentially, we just check for shell meta characters.
             // TODO: we use args here and rawArgs in upper method.
             for (String arg : args) {
                 if (!shouldVerifyPathExecutable(arg.trim())) {
                     return true;
                 }
             }
 
             // OK, so no shell meta-chars, now check that the command does exist
             executable = args[0].trim();
             executableFile = findPathExecutable(runtime, executable);
 
             // if the executable exists, start it directly with no shell
             if (executableFile != null) {
                 log(runtime, "Got it: " + executableFile);
                 // TODO: special processing for BAT/CMD files needed at all?
                 // if (isBatch(executableFile)) {
                 //    log(runtime, "This is a BAT/CMD file, will start in shell");
                 //    return true;
                 // }
                 return false;
             } else {
                 log(runtime, "Didn't find executable: " + executable);
             }
 
             if (isCmdBuiltin(executable)) {
                 cmdBuiltin = true;
                 return true;
             }
 
             // TODO: maybe true here?
             return false;
         }
 
         private void verifyExecutableForShell() {
             String cmdline = rawArgs[0].toString().trim();
             if (doExecutableSearch && shouldVerifyPathExecutable(cmdline) && !cmdBuiltin) {
                 verifyExecutable();
             }
 
             // now, prepare the exec args
 
             execArgs = new String[3];
             execArgs[0] = shell;
             execArgs[1] = shell.endsWith("sh") ? "-c" : "/c";
 
             if (Platform.IS_WINDOWS) {
                 // that's how MRI does it too
                 execArgs[2] = "\"" + cmdline + "\"";
             } else {
                 execArgs[2] = cmdline;
             }
         }
 
         private void verifyExecutableForDirect() {
             verifyExecutable();
             execArgs = args;
             try {
                 execArgs[0] = executableFile.getCanonicalPath();
             } catch (IOException ioe) {
                 // can't get the canonical path, will use as-is
             }
         }
 
         private void verifyExecutable() {
             if (executableFile == null) {
                 if (executable == null) {
                     executable = args[0].trim();
                 }
                 executableFile = findPathExecutable(runtime, executable);
             }
             if (executableFile == null) {
                 throw runtime.newErrnoENOENTError(executable);
             }
         }
 
         private String[] getExecArgs() {
             return execArgs;
         }
 
         private static boolean isBatch(File f) {
             String path = f.getPath();
             return (path.endsWith(".bat") || path.endsWith(".cmd"));
         }
 
         private boolean isCmdBuiltin(String cmd) {
             if (!shell.endsWith("sh")) { // assume cmd.exe
                 int idx = Arrays.binarySearch(WINDOWS_INTERNAL_CMDS, cmd.toLowerCase());
                 if (idx >= 0) {
                     log(runtime, "Found Windows shell's built-in command: " + cmd);
                     // Windows shell internal command, launch in shell then
                     return true;
                 }
             }
             return false;
         }
 
         /**
          * Checks a command string to determine if it has I/O redirection
          * characters that require it to be executed by a command interpreter.
          */
         private static boolean hasRedirection(String cmdline) {
             if (Platform.IS_WINDOWS) {
                  // Scan the string, looking for redirection characters (< or >), pipe
                  // character (|) or newline (\n) that are not in a quoted string
                  char quote = '\0';
                  for (int idx = 0; idx < cmdline.length();) {
                      char ptr = cmdline.charAt(idx);
                      switch (ptr) {
                      case '\'':
                      case '\"':
                          if (quote == '\0') {
                              quote = ptr;
                          } else if (quote == ptr) {
                              quote = '\0';
                          }
                          idx++;
                          break;
                      case '>':
                      case '<':
                      case '|':
                      case '\n':
                          if (quote == '\0') {
                              return true;
                          }
                          idx++;
                          break;
                      case '%':
                          // detect Windows environment variables: %ABC%
                          Matcher envVarMatcher = WIN_ENVVAR_PATTERN.matcher(cmdline.substring(idx));
                          if (envVarMatcher.find()) {
                              return true;
                          } else {
                              idx++;
                          }
                          break;
                      case '\\':
                          // slash serves as escape character
                          idx++;
                      default:
                          idx++;
                          break;
                      }
                  }
                  return false;
             } else {
                 // TODO: better check here needed, with quoting/escaping
                 Matcher metaMatcher = SHELL_METACHARACTER_PATTERN.matcher(cmdline);
                 return metaMatcher.find();
             }
         }
 
         // Should we try to verify the path executable, or just punt to the shell?
         private static boolean shouldVerifyPathExecutable(String cmdline) {
             boolean verifyPathExecutable = true;
             if (hasRedirection(cmdline)) {
                 return false;
             }
             return verifyPathExecutable;
         }
 
         private Ruby runtime;
         private boolean doExecutableSearch;
         private IRubyObject[] rawArgs;
         private String shell;
         private String[] args;
         private String[] execArgs;
         private boolean cmdBuiltin = false;
 
         private String executable;
         private File executableFile;
     }
 
     public static Process run(Ruby runtime, IRubyObject[] rawArgs, boolean doExecutableSearch) throws IOException {
         Process aProcess = null;
         File pwd = new File(runtime.getCurrentDirectory());
         LaunchConfig cfg = new LaunchConfig(runtime, rawArgs, doExecutableSearch);
 
         try {
             if (cfg.shouldRunInProcess()) {
                 log(runtime, "Launching in-process");
                 ScriptThreadProcess ipScript = new ScriptThreadProcess(
                         runtime, cfg.getExecArgs(), getCurrentEnv(runtime), pwd);
                 ipScript.start();
                 return ipScript;
             } else if (cfg.shouldRunInShell()) {
                 log(runtime, "Launching with shell");
                 // execute command with sh -c
                 // this does shell expansion of wildcards
                 cfg.verifyExecutableForShell();
                 aProcess = Runtime.getRuntime().exec(cfg.getExecArgs(), getCurrentEnv(runtime), pwd);
             } else {
                 log(runtime, "Launching directly (no shell)");
                 cfg.verifyExecutableForDirect();
                 aProcess = Runtime.getRuntime().exec(cfg.getExecArgs(), getCurrentEnv(runtime), pwd);
             }
         } catch (SecurityException se) {
             throw runtime.newSecurityError(se.getLocalizedMessage());
         }
         
         return aProcess;
     }
 
     private interface Pumper extends Runnable {
         public enum Slave { IN, OUT };
         public void start();
         public void quit();
     }
 
     private static class StreamPumper extends Thread implements Pumper {
         private final InputStream in;
         private final OutputStream out;
         private final boolean onlyIfAvailable;
         private final Object waitLock = new Object();
         private final Object sync;
         private final Slave slave;
         private volatile boolean quit;
         private final Ruby runtime;
 
         StreamPumper(Ruby runtime, InputStream in, OutputStream out, boolean avail, Slave slave, Object sync) {
             this.in = unwrapBufferedStream(in);
             this.out = unwrapBufferedStream(out);
             this.onlyIfAvailable = avail;
             this.slave = slave;
             this.sync = sync;
             this.runtime = runtime;
             setDaemon(true);
         }
         @Override
         public void run() {
             runtime.getCurrentContext().setEventHooksEnabled(false);
             byte[] buf = new byte[1024];
             int numRead;
             boolean hasReadSomething = false;
             try {
                 while (!quit) {
                     // The problem we trying to solve below: STDIN in Java
                     // is blocked and non-interruptible, so if we invoke read
                     // on it, we might never be able to interrupt such thread.
                     // So, we use in.available() to see if there is any input
                     // ready, and only then read it. But this approach can't
                     // tell whether the end of stream reached or not, so we
                     // might end up looping right at the end of the stream.
                     // Well, at least, we can improve the situation by checking
                     // if some input was ever available, and if so, not
                     // checking for available anymore, and just go to read.
                     if (onlyIfAvailable && !hasReadSomething) {
                         if (in.available() == 0) {
                             synchronized (waitLock) {
                                 waitLock.wait(10);
                             }
                             continue;
                         } else {
                             hasReadSomething = true;
                         }
                     }
 
                     if ((numRead = in.read(buf)) == -1) {
                         break;
                     }
                     out.write(buf, 0, numRead);
                 }
             } catch (Exception e) {
             } finally {
                 if (onlyIfAvailable) {
                     synchronized (sync) {
                         // We need to close the out, since some
                         // processes would just wait for the stream
                         // to be closed before they process its content,
                         // and produce the output. E.g.: "cat".
                         if (slave == Slave.OUT) {
                             // we only close out if it's the slave stream, to avoid
                             // closing a directly-mapped stream from parent process
                             try { out.close(); } catch (IOException ioe) {}
                         }
                     }
                 }
             }
         }
         public void quit() {
             this.quit = true;
             synchronized (waitLock) {
                 waitLock.notify();
             }
         }
     }
 
     private static class ChannelPumper extends Thread implements Pumper {
         private final FileChannel inChannel;
         private final FileChannel outChannel;
         private final Slave slave;
         private final Object sync;
         private volatile boolean quit;
         private final Ruby runtime;
 
         ChannelPumper(Ruby runtime, FileChannel inChannel, FileChannel outChannel, Slave slave, Object sync) {
             if (DEBUG) out.println("using channel pumper");
             this.inChannel = inChannel;
             this.outChannel = outChannel;
             this.slave = slave;
             this.sync = sync;
             this.runtime = runtime;
             setDaemon(true);
         }
         @Override
         public void run() {
             runtime.getCurrentContext().setEventHooksEnabled(false);
             ByteBuffer buf = ByteBuffer.allocateDirect(1024);
             buf.clear();
             try {
                 while (!quit && inChannel.isOpen() && outChannel.isOpen()) {
                     int read = inChannel.read(buf);
                     if (read == -1) break;
                     buf.flip();
                     outChannel.write(buf);
                     buf.clear();
                 }
             } catch (Exception e) {
             } finally {
                 // processes seem to have some peculiar locking sequences, so we
                 // need to ensure nobody is trying to close/destroy while we are
                 synchronized (sync) {
                     switch (slave) {
                     case OUT:
                         try { outChannel.close(); } catch (IOException ioe) {}
                         break;
                     case IN:
                         try { inChannel.close(); } catch (IOException ioe) {}
                     }
                 }
             }
         }
         public void quit() {
             interrupt();
             this.quit = true;
         }
     }
 
     private static void handleStreams(Ruby runtime, Process p, InputStream in, OutputStream out, OutputStream err) throws IOException {
         InputStream pOut = p.getInputStream();
         InputStream pErr = p.getErrorStream();
         OutputStream pIn = p.getOutputStream();
 
         StreamPumper t1 = new StreamPumper(runtime, pOut, out, false, Pumper.Slave.IN, p);
         StreamPumper t2 = new StreamPumper(runtime, pErr, err, false, Pumper.Slave.IN, p);
 
         // The assumption here is that the 'in' stream provides
         // proper available() support. If available() always
         // returns 0, we'll hang!
         StreamPumper t3 = new StreamPumper(runtime, in, pIn, true, Pumper.Slave.OUT, p);
 
         t1.start();
         t2.start();
         t3.start();
 
         try { t1.join(); } catch (InterruptedException ie) {}
         try { t2.join(); } catch (InterruptedException ie) {}
         t3.quit();
 
         try { err.flush(); } catch (IOException io) {}
         try { out.flush(); } catch (IOException io) {}
 
         try { pIn.close(); } catch (IOException io) {}
         try { pOut.close(); } catch (IOException io) {}
         try { pErr.close(); } catch (IOException io) {}
 
         // Force t3 to quit, just in case if it's stuck.
         // Note: On some platforms, even interrupt might not
         // have an effect if the thread is IO blocked.
         try { t3.interrupt(); } catch (SecurityException se) {}
     }
 
+    private static void handleStreamsNonblocking(Ruby runtime, Process p, OutputStream out, OutputStream err) throws IOException {
+        InputStream pOut = p.getInputStream();
+        InputStream pErr = p.getErrorStream();
+
+        StreamPumper t1 = new StreamPumper(runtime, pOut, out, false, Pumper.Slave.IN, p);
+        StreamPumper t2 = new StreamPumper(runtime, pErr, err, false, Pumper.Slave.IN, p);
+
+        t1.start();
+        t2.start();
+    }
+
     // TODO: move inside the LaunchConfig
     private static String[] parseCommandLine(ThreadContext context, Ruby runtime, IRubyObject[] rawArgs) {
         String[] args;
         if (rawArgs.length == 1) {
             synchronized (runtime.getLoadService()) {
                 runtime.getLoadService().require("jruby/path_helper");
             }
             RubyModule pathHelper = runtime.getClassFromPath("JRuby::PathHelper");
             RubyArray parts = (RubyArray) RuntimeHelpers.invoke(
                     context, pathHelper, "smart_split_command", rawArgs);
             args = new String[parts.getLength()];
             for (int i = 0; i < parts.getLength(); i++) {
                 args[i] = parts.entry(i).toString();
             }
         } else {
             args = new String[rawArgs.length];
             for (int i = 0; i < rawArgs.length; i++) {
                 args[i] = rawArgs[i].toString();
             }
         }
         return args;
     }
 
     private static String getShell(Ruby runtime) {
         return RbConfigLibrary.jrubyShell();
     }
 
     private static boolean shouldUseShell(String command) {
         boolean useShell = false;
         for (char c : command.toCharArray()) {
             if (c != ' ' && !Character.isLetter(c) && "*?{}[]<>()~&|\\$;'`\"\n".indexOf(c) != -1) {
                 useShell = true;
             }
         }
         return useShell;
     }
 
     static void log(Ruby runtime, String msg) {
         if (RubyInstanceConfig.DEBUG_LAUNCHING) {
             runtime.getErr().println("ShellLauncher: " + msg);
         }
     }
 }
