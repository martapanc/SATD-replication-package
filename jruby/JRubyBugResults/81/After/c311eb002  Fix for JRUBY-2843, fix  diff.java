diff --git a/src/org/jruby/ext/socket/RubyBasicSocket.java b/src/org/jruby/ext/socket/RubyBasicSocket.java
index 3170ed9f0b..46c7377636 100644
--- a/src/org/jruby/ext/socket/RubyBasicSocket.java
+++ b/src/org/jruby/ext/socket/RubyBasicSocket.java
@@ -1,568 +1,570 @@
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
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
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
 package org.jruby.ext.socket;
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.nio.channels.Channel;
 import java.nio.channels.SocketChannel;
 import java.nio.channels.ServerSocketChannel;
 import java.nio.channels.DatagramChannel;
 import java.net.Socket;
 import java.net.ServerSocket;
 import java.net.DatagramSocket;
 import java.net.SocketAddress;
 import java.net.InetSocketAddress;
 import org.jruby.util.io.OpenFile;
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyIO;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.ChannelDescriptor;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="BasicSocket", parent="IO")
 public class RubyBasicSocket extends RubyIO {
     private static ObjectAllocator BASICSOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyBasicSocket(runtime, klass);
         }
     };
 
     static void createBasicSocket(Ruby runtime) {
         RubyClass rb_cBasicSocket = runtime.defineClass("BasicSocket", runtime.getIO(), BASICSOCKET_ALLOCATOR);
 
         rb_cBasicSocket.defineAnnotatedMethods(RubyBasicSocket.class);
     }
 
     public RubyBasicSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     protected void initSocket(ChannelDescriptor descriptor) {
         // make sure descriptor is registered
         registerDescriptor(descriptor);
         
         // continue with normal initialization
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.fdopen(getRuntime(), descriptor, new ModeFlags(ModeFlags.RDONLY)));
             openFile.setPipeStream(ChannelStream.fdopen(getRuntime(), descriptor, new ModeFlags(ModeFlags.WRONLY)));
             openFile.getPipeStream().setSync(true);
         } catch (org.jruby.util.io.InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         }
         openFile.setMode(OpenFile.READWRITE | OpenFile.SYNC);
     }
 
     @Override
     public IRubyObject close_write(ThreadContext context) {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't close");
         }
         
         if (openFile.getPipeStream() == null && openFile.isReadable()) {
             throw getRuntime().newIOError("closing non-duplex IO for writing");
         }
         
         if (!openFile.isReadable()) {
             close();
         } else {
             Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
             if (socketChannel instanceof SocketChannel
                     || socketChannel instanceof DatagramChannel) {
                 try {
                     asSocket().shutdownOutput();
                 } catch (IOException e) {
                     throw getRuntime().newIOError(e.getMessage());
                 }
             }
             openFile.setPipeStream(null);
             openFile.setMode(openFile.getMode() & ~OpenFile.WRITABLE);
         }
         return getRuntime().getNil();
     }
     
     @Override
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (runtime.getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't close");
         }
-        
+
         if (!openFile.isWritable()) {
             close();
         } else {
-            Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
-            if (socketChannel instanceof SocketChannel
+            if(openFile.getPipeStream() != null) {
+                Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
+                if (socketChannel instanceof SocketChannel
                     || socketChannel instanceof DatagramChannel) {
-                try {
-                    asSocket().shutdownInput();
-                } catch (IOException e) {
-                    throw runtime.newIOError(e.getMessage());
+                    try {
+                        asSocket().shutdownInput();
+                    } catch (IOException e) {
+                        throw runtime.newIOError(e.getMessage());
+                    }
                 }
+                openFile.setMainStream(openFile.getPipeStream());
+                openFile.setPipeStream(null);
+                openFile.setMode(openFile.getMode() & ~OpenFile.READABLE);
             }
-            openFile.setMainStream(openFile.getPipeStream());
-            openFile.setPipeStream(null);
-            openFile.setMode(openFile.getMode() & ~OpenFile.READABLE);
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "send", rest = true)
     public IRubyObject write_send(ThreadContext context, IRubyObject[] args) {
         return syswrite(context, args[0]);
     }
     
     @JRubyMethod(rest = true)
     public IRubyObject recv(IRubyObject[] args) {
         OpenFile openFile = getOpenFileChecked();
         try {
             return RubyString.newString(getRuntime(), openFile.getMainStream().read(RubyNumeric.fix2int(args[0])));
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             // recv returns nil on EOF
             return getRuntime().getNil();
     	} catch (IOException e) {
             // All errors to sysread should be SystemCallErrors, but on a closed stream
             // Ruby returns an IOError.  Java throws same exception for all errors so
             // we resort to this hack...
             if ("Socket not open".equals(e.getMessage())) {
 	            throw getRuntime().newIOError(e.getMessage());
             }
     	    throw getRuntime().newSystemCallError(e.getMessage());
     	}
     }
 
     protected InetSocketAddress getLocalSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if (socketChannel instanceof SocketChannel) {
             return (InetSocketAddress)((SocketChannel)socketChannel).socket().getLocalSocketAddress();
         } else if (socketChannel instanceof ServerSocketChannel) {
             return (InetSocketAddress)((ServerSocketChannel) socketChannel).socket().getLocalSocketAddress();
         } else {
             return null;
         }
     }
 
     protected InetSocketAddress getRemoteSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             return (InetSocketAddress)((SocketChannel)socketChannel).socket().getRemoteSocketAddress();
         } else {
             return null;
         }
     }
 
     private Socket asSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(!(socketChannel instanceof SocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((SocketChannel)socketChannel).socket();
     }
 
     private ServerSocket asServerSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(!(socketChannel instanceof ServerSocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((ServerSocketChannel)socketChannel).socket();
     }
 
     private DatagramSocket asDatagramSocket() {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(!(socketChannel instanceof DatagramChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((DatagramChannel)socketChannel).socket();
     }
 
     private IRubyObject getBroadcast() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return trueFalse((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getBroadcast() : false);
     }
 
     private void setBroadcast(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setBroadcast(asBoolean(val));
         }
     }
 
     private void setKeepAlive(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setKeepAlive(asBoolean(val));
         }
     }
 
     private void setReuseAddr(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReuseAddress(asBoolean(val));
         }
     }
 
     private void setRcvBuf(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReceiveBufferSize(asNumber(val));
         }
     }
 
     private void setTimeout(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setSoTimeout(asNumber(val));
         }
     }
 
     private void setSndBuf(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setSendBufferSize(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setSendBufferSize(asNumber(val));
         }
     }
 
     private void setLinger(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             if(val instanceof RubyBoolean && !val.isTrue()) {
                 asSocket().setSoLinger(false, 0);
             } else {
                 int num = asNumber(val);
                 if(num == -1) {
                     asSocket().setSoLinger(false, 0);
                 } else {
                     asSocket().setSoLinger(true, num);
                 }
             }
         }
     }
 
     private void setOOBInline(IRubyObject val) throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setOOBInline(asBoolean(val));
         }
     }
 
     private int asNumber(IRubyObject val) {
         if(val instanceof RubyNumeric) {
             return RubyNumeric.fix2int(val);
         } else {
             return stringAsNumber(val);
         }
     }
 
     private int stringAsNumber(IRubyObject val) {
         String str = val.convertToString().toString();
         int res = 0;
         res += (str.charAt(0)<<24);
         res += (str.charAt(1)<<16);
         res += (str.charAt(2)<<8);
         res += (str.charAt(3));
         return res;
     }
 
     protected boolean asBoolean(IRubyObject val) {
         if(val instanceof RubyString) {
             return stringAsNumber(val) != 0;
         } else if(val instanceof RubyNumeric) {
             return RubyNumeric.fix2int(val) != 0;
         } else {
             return val.isTrue();
         }
     }
 
     private IRubyObject getKeepAlive() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return trueFalse(
                          (socketChannel instanceof SocketChannel) ? asSocket().getKeepAlive() : false
                          );
     }
 
     private IRubyObject getLinger() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(
                       (socketChannel instanceof SocketChannel) ? asSocket().getSoLinger() : 0
                       );
     }
 
     private IRubyObject getOOBInline() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return trueFalse(
                          (socketChannel instanceof SocketChannel) ? asSocket().getOOBInline() : false
                          );
     }
 
     private IRubyObject getRcvBuf() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(
                       (socketChannel instanceof SocketChannel) ? asSocket().getReceiveBufferSize() : 
                       ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getReceiveBufferSize() : 
                        asDatagramSocket().getReceiveBufferSize())
                       );
     }
 
     private IRubyObject getSndBuf() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(
                       (socketChannel instanceof SocketChannel) ? asSocket().getSendBufferSize() : 
                       ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSendBufferSize() : 0)
                       );
     }
 
     private IRubyObject getReuseAddr() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return trueFalse(
                          (socketChannel instanceof ServerSocketChannel) ? asServerSocket().getReuseAddress() : false
                          );
     }
 
     private IRubyObject getTimeout() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(
                       (socketChannel instanceof SocketChannel) ? asSocket().getSoTimeout() : 
                       ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getSoTimeout() : 
                        ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSoTimeout() : 0))
                       );
     }
 
     protected int getSoTypeDefault() {
         return 0;
     }
 
     private IRubyObject getSoType() throws IOException {
         Channel socketChannel = openFile.getMainStream().getDescriptor().getChannel();
         return number(
                       (socketChannel instanceof SocketChannel) ? RubySocket.SOCK_STREAM : 
                       ((socketChannel instanceof ServerSocketChannel) ? RubySocket.SOCK_STREAM : 
                        ((socketChannel instanceof DatagramChannel) ? RubySocket.SOCK_DGRAM : getSoTypeDefault()))
                       );
     }
 
     private IRubyObject trueFalse(boolean val) {
         return getRuntime().newString( val ? " \u0000\u0000\u0000" : "\u0000\u0000\u0000\u0000" );
     }
 
     private IRubyObject number(long s) {
         StringBuilder result = new StringBuilder();
         result.append((char) ((s>>24) &0xff)).append((char) ((s>>16) &0xff));
         result.append((char) ((s >> 8) & 0xff)).append((char) (s & 0xff));
         return getRuntime().newString(result.toString());
     }
 
     @JRubyMethod
     public IRubyObject getsockopt(IRubyObject lev, IRubyObject optname) {
         int level = RubyNumeric.fix2int(lev);
         int opt = RubyNumeric.fix2int(optname);
 
         try {
             switch(level) {
             case RubySocket.SOL_IP:
             case RubySocket.SOL_SOCKET:
             case RubySocket.SOL_TCP:
             case RubySocket.SOL_UDP:
                 switch(opt) {
                 case RubySocket.SO_BROADCAST:
                     return getBroadcast();
                 case RubySocket.SO_KEEPALIVE:
                     return getKeepAlive();
                 case RubySocket.SO_LINGER:
                     return getLinger();
                 case RubySocket.SO_OOBINLINE:
                     return getOOBInline();
                 case RubySocket.SO_RCVBUF:
                     return getRcvBuf();
                 case RubySocket.SO_REUSEADDR:
                     return getReuseAddr();
                 case RubySocket.SO_SNDBUF:
                     return getSndBuf();
                 case RubySocket.SO_RCVTIMEO:
                 case RubySocket.SO_SNDTIMEO:
                     return getTimeout();
                 case RubySocket.SO_TYPE:
                     return getSoType();
 
                     // Can't support the rest with Java
                 case RubySocket.SO_RCVLOWAT:
                     return number(1);
                 case RubySocket.SO_SNDLOWAT:
                     return number(2048);
                 case RubySocket.SO_DEBUG:
                 case RubySocket.SO_ERROR:
                 case RubySocket.SO_DONTROUTE:
                 case RubySocket.SO_TIMESTAMP:
                     return trueFalse(false);
                 default:
                     throw getRuntime().newErrnoENOPROTOOPTError();
                 }
             default:
                 throw getRuntime().newErrnoENOPROTOOPTError();
             }
         } catch(IOException e) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
     }
 
     @JRubyMethod
     public IRubyObject setsockopt(IRubyObject lev, IRubyObject optname, IRubyObject val) {
         int level = RubyNumeric.fix2int(lev);
         int opt = RubyNumeric.fix2int(optname);
 
         try {
             switch(level) {
             case RubySocket.SOL_IP:
             case RubySocket.SOL_SOCKET:
             case RubySocket.SOL_TCP:
             case RubySocket.SOL_UDP:
                 switch(opt) {
                 case RubySocket.SO_BROADCAST:
                     setBroadcast(val);
                     break;
                 case RubySocket.SO_KEEPALIVE:
                     setKeepAlive(val);
                     break;
                 case RubySocket.SO_LINGER:
                     setLinger(val);
                     break;
                 case RubySocket.SO_OOBINLINE:
                     setOOBInline(val);
                     break;
                 case RubySocket.SO_RCVBUF:
                     setRcvBuf(val);
                     break;
                 case RubySocket.SO_REUSEADDR:
                     setReuseAddr(val);
                     break;
                 case RubySocket.SO_SNDBUF:
                     setSndBuf(val);
                     break;
                 case RubySocket.SO_RCVTIMEO:
                 case RubySocket.SO_SNDTIMEO:
                     setTimeout(val);
                     break;
                     // Can't support the rest with Java
                 case RubySocket.SO_TYPE:
                 case RubySocket.SO_RCVLOWAT:
                 case RubySocket.SO_SNDLOWAT:
                 case RubySocket.SO_DEBUG:
                 case RubySocket.SO_ERROR:
                 case RubySocket.SO_DONTROUTE:
                 case RubySocket.SO_TIMESTAMP:
                     break;
                 default:
                     throw getRuntime().newErrnoENOPROTOOPTError();
                 }
                 break;
             default:
                 throw getRuntime().newErrnoENOPROTOOPTError();
             }
         } catch(IOException e) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
         return getRuntime().newFixnum(0);
     }
 
     @JRubyMethod(name = {"getsockname", "__getsockname"})
     public IRubyObject getsockname() {
         SocketAddress sock = getLocalSocket();
         if(null == sock) {
             throw getRuntime().newIOError("Not Supported");
         }
         return getRuntime().newString(sock.toString());
     }
 
     @JRubyMethod(name = {"getpeername", "__getpeername"})
     public IRubyObject getpeername() {
         SocketAddress sock = getRemoteSocket();
         if(null == sock) {
             throw getRuntime().newIOError("Not Supported");
         }
         return getRuntime().newString(sock.toString());
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject shutdown(ThreadContext context, IRubyObject[] args) {
         if (getRuntime().getSafeLevel() >= 4 && tainted_p(context).isFalse()) {
             throw getRuntime().newSecurityError("Insecure: can't shutdown socket");
         }
         
         int how = 2;
         if (args.length > 0) {
             how = RubyNumeric.fix2int(args[0]);
         }
         if (how < 0 || 2 < how) {
             throw getRuntime().newArgumentError("`how' should be either 0, 1, 2");
         }
         if (how != 2) {
             throw getRuntime().newNotImplementedError("Shutdown currently only works with how=2");
         }
         return close();
     }
 
     @JRubyMethod(meta = true)
     public static IRubyObject do_not_reverse_lookup(IRubyObject recv) {
         return recv.getRuntime().isDoNotReverseLookupEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
     
     @JRubyMethod(name = "do_not_reverse_lookup=", meta = true)
     public static IRubyObject set_do_not_reverse_lookup(IRubyObject recv, IRubyObject flag) {
         recv.getRuntime().setDoNotReverseLookupEnabled(flag.isTrue());
         return recv.getRuntime().isDoNotReverseLookupEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 }// RubyBasicSocket
diff --git a/src/org/jruby/javasupport/JavaMethod.java b/src/org/jruby/javasupport/JavaMethod.java
index 6594aad47f..f89fe57f90 100644
--- a/src/org/jruby/javasupport/JavaMethod.java
+++ b/src/org/jruby/javasupport/JavaMethod.java
@@ -1,383 +1,383 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
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
 
 import java.lang.annotation.Annotation;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.lang.reflect.Type;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.proxy.InternalJavaProxy;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 @JRubyClass(name="Java::JavaMethod")
 public class JavaMethod extends JavaCallable {
     private final Method method;
     private final Class<?>[] parameterTypes;
     private final JavaUtil.JavaConverter returnConverter;
 
     public static RubyClass createJavaMethodClass(Ruby runtime, RubyModule javaModule) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
         // this type and it can't be marshalled. Confirm. JRUBY-415
         RubyClass result = 
             javaModule.defineClassUnder("JavaMethod", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
 
         JavaAccessibleObject.registerRubyMethods(runtime, result);
         JavaCallable.registerRubyMethods(runtime, result);
         
         result.defineAnnotatedMethods(JavaMethod.class);
 
         return result;
     }
 
     public JavaMethod(Ruby runtime, Method method) {
         super(runtime, runtime.getJavaSupport().getJavaMethodClass());
         this.method = method;
         this.parameterTypes = method.getParameterTypes();
 
         // Special classes like Collections.EMPTY_LIST are inner classes that are private but 
         // implement public interfaces.  Their methods are all public methods for the public 
         // interface.  Let these public methods execute via setAccessible(true). 
         if (Modifier.isPublic(method.getModifiers()) &&
             Modifier.isPublic(method.getClass().getModifiers()) &&
             !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
             accessibleObject().setAccessible(true);
         }
         
         returnConverter = JavaUtil.getJavaConverter(method.getReturnType());
     }
 
     public static JavaMethod create(Ruby runtime, Method method) {
         return new JavaMethod(runtime, method);
     }
 
     public static JavaMethod create(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             Method method = javaClass.getMethod(methodName, argumentTypes);
             return create(runtime, method);
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod createDeclared(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         try {
             return create(runtime, javaClass.getDeclaredMethod(methodName, argumentTypes));
         } catch (NoSuchMethodException e) {
             throw runtime.newNameError("undefined method '" + methodName + "' for class '" + javaClass.getName() + "'",
                     methodName);
         }
     }
 
     public static JavaMethod getMatchingDeclaredMethod(Ruby runtime, Class<?> javaClass, String methodName, Class<?>[] argumentTypes) {
         // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
         // include superclass methods.  also, the getDeclared calls may throw SecurityException if
         // we're running under a restrictive security policy.
         try {
             return create(runtime, javaClass.getDeclaredMethod(methodName, argumentTypes));
         } catch (NoSuchMethodException e) {
             // search through all declared methods to find a closest match
             MethodSearch: for (Method method : javaClass.getDeclaredMethods()) {
                 if (method.getName().equals(methodName)) {
                     Class<?>[] targetTypes = method.getParameterTypes();
                 
                     // for zero args case we can stop searching
                     if (targetTypes.length == 0 && argumentTypes.length == 0) {
                         return create(runtime, method);
                     }
                     
                     TypeScan: for (int i = 0; i < argumentTypes.length; i++) {
                         if (i >= targetTypes.length) continue MethodSearch;
 
                         if (targetTypes[i].isAssignableFrom(argumentTypes[i])) {
                             continue TypeScan;
                         } else {
                             continue MethodSearch;
                         }
                     }
 
                     // if we get here, we found a matching method, use it
                     // TODO: choose narrowest method by continuing to search
                     return create(runtime, method);
                 }
             }
         }
         // no matching method found
         return null;
     }
     
     public boolean equals(Object other) {
         return other instanceof JavaMethod &&
             this.method == ((JavaMethod)other).method;
     }
     
     public int hashCode() {
         return method.hashCode();
     }
 
     @JRubyMethod
     public RubyString name() {
         return getRuntime().newString(method.getName());
     }
 
     public int getArity() {
         return parameterTypes.length;
     }
 
     @JRubyMethod(name = "public?")
     public RubyBoolean public_p() {
         return getRuntime().newBoolean(Modifier.isPublic(method.getModifiers()));
     }
 
     @JRubyMethod(name = "final?")
     public RubyBoolean final_p() {
         return getRuntime().newBoolean(Modifier.isFinal(method.getModifiers()));
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke(IRubyObject[] args) {
         if (args.length != 1 + getArity()) {
             throw getRuntime().newArgumentError(args.length, 1 + getArity());
         }
 
         Object[] arguments = new Object[args.length - 1];
         convertArguments(getRuntime(), arguments, args, 1);
 
         IRubyObject invokee = args[0];
         if(invokee.isNil()) {
             return invokeWithExceptionHandling(method, null, arguments);
         }
 
         Object javaInvokee = JavaUtil.unwrapJavaObject(getRuntime(), invokee, "invokee not a java object").getValue();
 
         if (! method.getDeclaringClass().isInstance(javaInvokee)) {
             throw getRuntime().newTypeError("invokee not instance of method's class (" +
                                               "got" + javaInvokee.getClass().getName() + " wanted " +
                                               method.getDeclaringClass().getName() + ")");
         }
         
         //
         // this test really means, that this is a ruby-defined subclass of a java class
         //
         if (javaInvokee instanceof InternalJavaProxy &&
                 // don't bother to check if final method, it won't
                 // be there (not generated, can't be!)
                 !Modifier.isFinal(method.getModifiers())) {
             JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
                     .___getProxyClass();
             JavaProxyMethod jpm;
             if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null &&
                     jpm.hasSuperImplementation()) {
                 return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, arguments);
             }
         }
         return invokeWithExceptionHandling(method, javaInvokee, arguments);
     }
 
     public IRubyObject invoke(IRubyObject self, Object[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
 
         if (! (self instanceof JavaObject)) {
             throw getRuntime().newTypeError("invokee not a java object");
         }
         Object javaInvokee = ((JavaObject) self).getValue();
 
         if (! method.getDeclaringClass().isInstance(javaInvokee)) {
             throw getRuntime().newTypeError("invokee not instance of method's class (" +
                                               "got" + javaInvokee.getClass().getName() + " wanted " +
                                               method.getDeclaringClass().getName() + ")");
         }
         
         //
         // this test really means, that this is a ruby-defined subclass of a java class
         //
         if (javaInvokee instanceof InternalJavaProxy &&
                 // don't bother to check if final method, it won't
                 // be there (not generated, can't be!)
                 !Modifier.isFinal(method.getModifiers())) {
             JavaProxyClass jpc = ((InternalJavaProxy) javaInvokee)
                     .___getProxyClass();
             JavaProxyMethod jpm;
             if ((jpm = jpc.getMethod(method.getName(), parameterTypes)) != null &&
                     jpm.hasSuperImplementation()) {
                 return invokeWithExceptionHandling(jpm.getSuperMethod(), javaInvokee, args);
             }
         }
         return invokeWithExceptionHandling(method, javaInvokee, args);
     }
 
     @JRubyMethod(rest = true)
     public IRubyObject invoke_static(IRubyObject[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
         Object[] arguments = new Object[args.length];
         System.arraycopy(args, 0, arguments, 0, arguments.length);
         convertArguments(getRuntime(), arguments, args, 0);
         return invokeWithExceptionHandling(method, null, arguments);
     }
 
     public IRubyObject invoke_static(Object[] args) {
         if (args.length != getArity()) {
             throw getRuntime().newArgumentError(args.length, getArity());
         }
-        
+
         return invokeWithExceptionHandling(method, null, args);
     }
 
     @JRubyMethod
     public IRubyObject return_type() {
         Class<?> klass = method.getReturnType();
         
         if (klass.equals(void.class)) {
             return getRuntime().getNil();
         }
         return JavaClass.get(getRuntime(), klass);
     }
 
     @JRubyMethod
     public IRubyObject type_parameters() {
         return Java.getInstance(getRuntime(), method.getTypeParameters());
     }
 
     private IRubyObject invokeWithExceptionHandling(Method method, Object javaInvokee, Object[] arguments) {
         try {
             Object result = method.invoke(javaInvokee, arguments);
             return returnConverter.convert(getRuntime(), result);
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newTypeError("for method " + method.getName() + " expected " + argument_types().inspect() + "; got: "
                         + dumpArgTypes(arguments)
                         + "; error: " + iae.getMessage());
         } catch (IllegalAccessException iae) {
             throw getRuntime().newTypeError("illegal access on '" + method.getName() + "': " + iae.getMessage());
         } catch (InvocationTargetException ite) {
             getRuntime().getJavaSupport().handleNativeException(ite.getTargetException());
             // This point is only reached if there was an exception handler installed.
             return getRuntime().getNil();
         }
     }
 
     private String dumpArgTypes(Object[] arguments) {
         StringBuilder str = new StringBuilder("[");
         for (int i = 0; i < arguments.length; i++) {
             if (i > 0) {
                 str.append(",");
             }
             if (arguments[i] == null) {
                 str.append("null");
             } else {
                 str.append(arguments[i].getClass().getName());
             }
         }
         str.append("]");
         return str.toString();
     }
 
     private void convertArguments(Ruby runtime, Object[] arguments, Object[] args, int from) {
         Class<?>[] types = parameterTypes;
         for (int i = arguments.length; --i >= 0; ) {
             arguments[i] = JavaUtil.convertArgument(runtime, args[i+from], types[i]);
         }
     }
 
     public Class<?>[] getParameterTypes() {
         return parameterTypes;
     }
 
     public Class<?>[] getExceptionTypes() {
         return method.getExceptionTypes();
     }
 
     public Type[] getGenericParameterTypes() {
         return method.getGenericParameterTypes();
     }
 
     public Type[] getGenericExceptionTypes() {
         return method.getGenericExceptionTypes();
     }
     
     public Annotation[][] getParameterAnnotations() {
         return method.getParameterAnnotations();
     }
 
     public boolean isVarArgs() {
         return method.isVarArgs();
     }
 
     protected String nameOnInspection() {
         return "#<" + getType().toString() + "/" + method.getName() + "(";
     }
 
     public RubyBoolean static_p() {
         return getRuntime().newBoolean(isStatic());
     }
     
     public RubyBoolean bridge_p() {
         return getRuntime().newBoolean(method.isBridge());
     }
 
     private boolean isStatic() {
         return Modifier.isStatic(method.getModifiers());
     }
 
     public int getModifiers() {
         return method.getModifiers();
     }
 
     public String toGenericString() {
         return method.toGenericString();
     }
 
     protected AccessibleObject accessibleObject() {
         return method;
     }
 }
