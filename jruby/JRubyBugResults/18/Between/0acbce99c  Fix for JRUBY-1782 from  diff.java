diff --git a/src/builtin/jsignal.rb b/src/builtin/jsignal.rb
index fa242c2d46..53e601eb44 100644
--- a/src/builtin/jsignal.rb
+++ b/src/builtin/jsignal.rb
@@ -1,31 +1,31 @@
 module ::Kernel
   SIGNALS = {
     1  => "SIGHUP", 2  => "SIGINT", 3  => "SIGQUIT", 4  => "SIGILL", 5  => "SIGTRAP", 6  => "SIGABRT",
     7  => "SIGPOLL", 8  => "SIGFPE", 9  => "SIGKILL", 10 => "SIGBUS", 11 => "SIGSEGV", 12 => "SIGSYS",
     13 => "SIGPIPE", 14 => "SIGALRM", 15 => "SIGTERM", 16 => "SIGURG", 17 => "SIGSTOP", 18 => "SIGTSTP",
     19 => "SIGCONT", 20 => "SIGCHLD", 21 => "SIGTTIN", 22 => "SIGTTOU", 24 => "SIGXCPU", 25 => "SIGXFSZ",
     26 => "SIGVTALRM", 27 => "SIGPROF", 30 => "SIGUSR1", 31 => "SIGUSR2"
   }
   
   begin
     JavaSignal = Java::sun.misc.Signal
     def __jtrap(*args, &block)
       sig = args.shift
       sig = SIGNALS[sig] if sig.kind_of?(Fixnum)
       sig = sig.to_s.sub(/^SIG(.+)/,'\1')
 
       block = args.shift unless args.empty?
 
       signal_object = JavaSignal.new(sig) rescue nil
       return unless signal_object
       
       Signal::__jtrap_kernel(block, signal_object, sig)
     rescue java.lang.IllegalArgumentException
       warn "The signal #{sig} is in use by the JVM and will not work correctly on this platform"
     end
   rescue NameError
     def __jtrap(*args, &block)
-      warn "trap not supported by this VM"
+      warn "trap not supported or not allowed by this VM"
     end
   end
 end
diff --git a/src/org/jruby/javasupport/JavaSupport.java b/src/org/jruby/javasupport/JavaSupport.java
index 7db31495dd..a82e81939b 100644
--- a/src/org/jruby/javasupport/JavaSupport.java
+++ b/src/org/jruby/javasupport/JavaSupport.java
@@ -1,263 +1,262 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 package org.jruby.javasupport;
 
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.util.ObjectProxyCache;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 public class JavaSupport {
     private final Ruby runtime;
 
     private final Map exceptionHandlers = new HashMap();
     
     private final ObjectProxyCache<IRubyObject,RubyClass> objectProxyCache = 
         // TODO: specifying soft refs, may want to compare memory consumption,
         // behavior with weak refs (specify WEAK in place of SOFT below)
         new ObjectProxyCache<IRubyObject,RubyClass>(ObjectProxyCache.ReferenceType.SOFT) {
 
         public IRubyObject allocateProxy(Object javaObject, RubyClass clazz) {
             IRubyObject proxy = clazz.allocate();
             proxy.getInstanceVariables().fastSetInstanceVariable("@java_object",
                     JavaObject.wrap(clazz.getRuntime(), javaObject));
             return proxy;
         }
 
     };
 
     
     // There's not a compelling reason to keep JavaClass instances in a weak map
     // (any proxies created are [were] kept in a non-weak map, so in most cases they will
     // stick around anyway), and some good reasons not to (JavaClass creation is
     // expensive, for one; many lookups are performed when passing parameters to/from
     // methods; etc.).
     // TODO: faster custom concurrent map
     private final ConcurrentHashMap<Class,JavaClass> javaClassCache =
         new ConcurrentHashMap<Class,JavaClass>(128);
     
     // FIXME: needs to be rethought
     private final Map matchCache = Collections.synchronizedMap(new HashMap(128));
 
     private Callback concreteProxyCallback;
 
     private RubyModule javaModule;
     private RubyModule javaUtilitiesModule;
     private RubyClass javaObjectClass;
     private RubyClass javaClassClass;
     private RubyClass javaArrayClass;
     private RubyClass javaProxyClass;
     private RubyModule javaInterfaceTemplate;
     private RubyModule packageModuleTemplate;
     private RubyClass arrayProxyClass;
     private RubyClass concreteProxyClass;
     
     
     public JavaSupport(Ruby ruby) {
         this.runtime = ruby;
     }
 
     final synchronized void setConcreteProxyCallback(Callback concreteProxyCallback) {
         if (this.concreteProxyCallback == null) {
             this.concreteProxyCallback = concreteProxyCallback;
         }
     }
     
     final Callback getConcreteProxyCallback() {
         return concreteProxyCallback;
     }
     
     final Map getMatchCache() {
         return matchCache;
     }
 
     
     public Class loadJavaClass(String className) {
-        if (Ruby.isSecurityRestricted() && className.startsWith("sun.misc.")) {
-            throw runtime.newNameError("security: cannot load class " + className, className);
-        }
         try {
             Class result = primitiveClass(className);
             if(result == null) {
                 return (Ruby.isSecurityRestricted()) ? Class.forName(className) :
                    Class.forName(className, true, runtime.getJRubyClassLoader());
             }
             return result;
         } catch (ClassNotFoundException cnfExcptn) {
             throw runtime.newNameError("cannot load Java class " + className, className);
+        } catch (SecurityException se) {
+            throw runtime.newNameError("security: cannot load Java class " + className, className);
         }
     }
 
     public JavaClass getJavaClassFromCache(Class clazz) {
         return javaClassCache.get(clazz);
     }
     
     public void putJavaClassIntoCache(JavaClass clazz) {
         javaClassCache.put(clazz.javaClass(), clazz);
     }
     
     public void defineExceptionHandler(String exceptionClass, RubyProc handler) {
         exceptionHandlers.put(exceptionClass, handler);
     }
 
     public void handleNativeException(Throwable exception) {
         if (exception instanceof RaiseException) {
             throw (RaiseException) exception;
         }
         Class excptnClass = exception.getClass();
         RubyProc handler = (RubyProc)exceptionHandlers.get(excptnClass.getName());
         while (handler == null &&
                excptnClass != Throwable.class) {
             excptnClass = excptnClass.getSuperclass();
         }
         if (handler != null) {
             handler.call(new IRubyObject[]{JavaUtil.convertJavaToRuby(runtime, exception)});
         } else {
             throw createRaiseException(exception);
         }
     }
 
     private RaiseException createRaiseException(Throwable exception) {
         RaiseException re = RaiseException.createNativeRaiseException(runtime, exception);
         
         return re;
     }
 
     private static Class primitiveClass(String name) {
         if (name.equals("long")) {
             return Long.TYPE;
         } else if (name.equals("int")) {
             return Integer.TYPE;
         } else if (name.equals("boolean")) {
             return Boolean.TYPE;
         } else if (name.equals("char")) {
             return Character.TYPE;
         } else if (name.equals("short")) {
             return Short.TYPE;
         } else if (name.equals("byte")) {
             return Byte.TYPE;
         } else if (name.equals("float")) {
             return Float.TYPE;
         } else if (name.equals("double")) {
             return Double.TYPE;
         }
         return null;
     }
     
     public ObjectProxyCache<IRubyObject,RubyClass> getObjectProxyCache() {
         return objectProxyCache;
     }
 
     // not synchronizing these methods, no harm if these values get set twice...
     
     public RubyModule getJavaModule() {
         if (javaModule == null) {
             javaModule = runtime.fastGetModule("Java");
         }
         return javaModule;
     }
     
     public RubyModule getJavaUtilitiesModule() {
         if (javaUtilitiesModule == null) {
             javaUtilitiesModule = runtime.fastGetModule("JavaUtilities");
         }
         return javaUtilitiesModule;
     }
     
     public RubyClass getJavaObjectClass() {
         if (javaObjectClass == null) {
             javaObjectClass = getJavaModule().fastGetClass("JavaObject");
         }
         return javaObjectClass;
     }
 
     public RubyClass getJavaArrayClass() {
         if (javaArrayClass == null) {
             javaArrayClass = getJavaModule().fastGetClass("JavaArray");
         }
         return javaArrayClass;
     }
     
     public RubyClass getJavaClassClass() {
         if(javaClassClass == null) {
             javaClassClass = getJavaModule().fastGetClass("JavaClass");
         }
         return javaClassClass;
     }
     
     public RubyModule getJavaInterfaceTemplate() {
         if (javaInterfaceTemplate == null) {
             javaInterfaceTemplate = runtime.fastGetModule("JavaInterfaceTemplate");
         }
         return javaInterfaceTemplate;
     }
     
     public RubyModule getPackageModuleTemplate() {
         if (packageModuleTemplate == null) {
             packageModuleTemplate = runtime.fastGetModule("JavaPackageModuleTemplate");
         }
         return packageModuleTemplate;
     }
     
     public RubyClass getJavaProxyClass() {
         if (javaProxyClass == null) {
             javaProxyClass = runtime.fastGetClass("JavaProxy");
         }
         return javaProxyClass;
     }
     
     public RubyClass getConcreteProxyClass() {
         if (concreteProxyClass == null) {
             concreteProxyClass = runtime.fastGetClass("ConcreteJavaProxy");
         }
         return concreteProxyClass;
     }
     
     public RubyClass getArrayProxyClass() {
         if (arrayProxyClass == null) {
             arrayProxyClass = runtime.fastGetClass("ArrayJavaProxy");
         }
         return arrayProxyClass;
     }
 
 }
