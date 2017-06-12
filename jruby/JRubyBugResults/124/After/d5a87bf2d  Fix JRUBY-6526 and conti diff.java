diff --git a/src/org/jruby/ext/socket/RubyBasicSocket.java b/src/org/jruby/ext/socket/RubyBasicSocket.java
index 8a7e229ef7..c18872e042 100644
--- a/src/org/jruby/ext/socket/RubyBasicSocket.java
+++ b/src/org/jruby/ext/socket/RubyBasicSocket.java
@@ -1,765 +1,789 @@
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
 
 import static jnr.constants.platform.IPProto.IPPROTO_TCP;
 import static jnr.constants.platform.IPProto.IPPROTO_IP;
 import static jnr.constants.platform.Sock.SOCK_DGRAM;
 import static jnr.constants.platform.Sock.SOCK_STREAM;
 import static jnr.constants.platform.TCP.TCP_NODELAY;
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.net.DatagramSocket;
 import java.net.InetSocketAddress;
 import java.net.ServerSocket;
 import java.net.Socket;
 import java.net.SocketAddress;
 import java.nio.channels.Channel;
 import java.nio.channels.DatagramChannel;
 import java.nio.channels.ServerSocketChannel;
 import java.nio.channels.SocketChannel;
 
+import jnr.constants.platform.Sock;
 import org.jruby.CompatVersion;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyIO;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.Pack;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.OpenFile;
 
 import jnr.constants.platform.SocketLevel;
 import jnr.constants.platform.SocketOption;
 import org.jruby.util.io.Sockaddr;
 
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="BasicSocket", parent="IO")
 public class RubyBasicSocket extends RubyIO {
     private static final ByteList FORMAT_SMALL_I = new ByteList(ByteList.plain("i"));
     protected MulticastStateManager multicastStateManager = null;
 
     private static ObjectAllocator BASICSOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyBasicSocket(runtime, klass);
         }
     };
 
     static void createBasicSocket(Ruby runtime) {
         RubyClass rb_cBasicSocket = runtime.defineClass("BasicSocket", runtime.getIO(), BASICSOCKET_ALLOCATOR);
 
         rb_cBasicSocket.defineAnnotatedMethods(RubyBasicSocket.class);
     }
 
     // By default we always reverse lookup unless do_not_reverse_lookup set.
     private boolean doNotReverseLookup = false;
 
     public RubyBasicSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
         doNotReverseLookup = runtime.is1_9();
     }
     
     protected void initSocket(Ruby runtime, ChannelDescriptor descriptor) {
         // continue with normal initialization
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, newModeFlags(runtime, ModeFlags.RDONLY)));
             openFile.setPipeStream(ChannelStream.fdopen(runtime, descriptor, newModeFlags(runtime, ModeFlags.WRONLY)));
             openFile.getPipeStream().setSync(true);
         } catch (org.jruby.util.io.InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         }
         openFile.setMode(OpenFile.READWRITE | OpenFile.SYNC);
     }
 
     @Override
     public IRubyObject close_write(ThreadContext context) {
         if (context.getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw context.getRuntime().newSecurityError("Insecure: can't close");
         }
 
         if (!openFile.isWritable()) {
             return context.getRuntime().getNil();
         }
 
         if (openFile.getPipeStream() == null && openFile.isReadable()) {
             throw context.getRuntime().newIOError("closing non-duplex IO for writing");
         }
 
         if (!openFile.isReadable()) {
             close();
         } else {
             // shutdown write
             try {
                 shutdownInternal(context, 1);
             } catch (BadDescriptorException e) {
                 throw context.runtime.newErrnoEBADFError();
             }
         }
         return context.getRuntime().getNil();
     }
 
     @Override
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (runtime.getSafeLevel() >= 4 && isTaint()) {
             throw runtime.newSecurityError("Insecure: can't close");
         }
 
         if (!openFile.isOpen()) {
             throw context.getRuntime().newIOError("not opened for reading");
         }
 
         if (!openFile.isWritable()) {
             close();
         } else {
             // shutdown read
             try {
                 shutdownInternal(context, 0);
             } catch (BadDescriptorException e) {
                 throw context.runtime.newErrnoEBADFError();
             }
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "send", rest = true)
     public IRubyObject write_send(ThreadContext context, IRubyObject[] args) {
         return syswrite(context, args[0]);
     }
 
     @Deprecated
     public IRubyObject recv(IRubyObject[] args) {
         return recv(getRuntime().getCurrentContext(), args);
     }
     @JRubyMethod(rest = true)
     public IRubyObject recv(ThreadContext context, IRubyObject[] args) {
         OpenFile openFile = getOpenFileChecked();
         try {
             context.getThread().beforeBlockingCall();
             return RubyString.newString(context.getRuntime(), openFile.getMainStreamSafe().read(RubyNumeric.fix2int(args[0])));
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             // recv returns nil on EOF
             return context.getRuntime().getNil();
         } catch (IOException e) {
             // All errors to sysread should be SystemCallErrors, but on a closed stream
             // Ruby returns an IOError.  Java throws same exception for all errors so
             // we resort to this hack...
             if ("Socket not open".equals(e.getMessage())) {
 	            throw context.getRuntime().newIOError(e.getMessage());
             }
             throw context.getRuntime().newSystemCallError(e.getMessage());
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
 
+    @Deprecated
     protected InetSocketAddress getLocalSocket(String caller) throws BadDescriptorException {
+        return getSocketAddress();
+    }
+
+    protected InetSocketAddress getSocketAddress() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if (socketChannel instanceof SocketChannel) {
             return (InetSocketAddress)((SocketChannel)socketChannel).socket().getLocalSocketAddress();
         } else if (socketChannel instanceof ServerSocketChannel) {
             return (InetSocketAddress)((ServerSocketChannel) socketChannel).socket().getLocalSocketAddress();
         } else if (socketChannel instanceof DatagramChannel) {
             return (InetSocketAddress)((DatagramChannel) socketChannel).socket().getLocalSocketAddress();
         } else {
             return null;
         }
     }
     
     protected InetSocketAddress getRemoteSocket() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             return (InetSocketAddress)((SocketChannel)socketChannel).socket().getRemoteSocketAddress();
         } else {
             return null;
         }
     }
 
     private Socket asSocket() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof SocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((SocketChannel)socketChannel).socket();
     }
 
     private ServerSocket asServerSocket() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof ServerSocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((ServerSocketChannel)socketChannel).socket();
     }
 
     private DatagramSocket asDatagramSocket() throws BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof DatagramChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((DatagramChannel)socketChannel).socket();
     }
 
     private IRubyObject getBroadcast(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime, (socketChannel instanceof DatagramChannel) ? asDatagramSocket().getBroadcast() : false);
     }
 
     private void setBroadcast(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setBroadcast(asBoolean(val));
         }
     }
 
     private void setKeepAlive(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setKeepAlive(asBoolean(val));
         }
     }
 
     private void setTcpNoDelay(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setTcpNoDelay(asBoolean(val));
         }
     }
 
     private void joinMulticastGroup(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
 
         if(socketChannel instanceof DatagramChannel) {
             if (multicastStateManager == null) {
                 multicastStateManager = new MulticastStateManager();
             }
 
             if (val instanceof RubyString) {
                 byte [] ipaddr_buf = val.convertToString().getBytes();
                 multicastStateManager.addMembership(ipaddr_buf);
             }
         }
     }
 
     private void setReuseAddr(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if (socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReuseAddress(asBoolean(val));
         } else if (socketChannel instanceof SocketChannel) {
             asSocket().setReuseAddress(asBoolean(val));
         } else if (socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReuseAddress(asBoolean(val));
         }
     }
 
     private void setRcvBuf(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReceiveBufferSize(asNumber(val));
         }
     }
 
     private void setTimeout(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setSoTimeout(asNumber(val));
         }
     }
 
     private void setSndBuf(IRubyObject val) throws IOException, BadDescriptorException {
         try {
             Channel socketChannel = getOpenChannel();
             if(socketChannel instanceof SocketChannel) {
                 asSocket().setSendBufferSize(asNumber(val));
             } else if(socketChannel instanceof DatagramChannel) {
                 asDatagramSocket().setSendBufferSize(asNumber(val));
             }
         } catch (IllegalArgumentException iae) {
             throw getRuntime().newErrnoEINVALError(iae.getMessage());
         }
     }
 
     private void setLinger(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
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
 
     private void setOOBInline(IRubyObject val) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setOOBInline(asBoolean(val));
         }
     }
 
     private int asNumber(IRubyObject val) {
         if (val instanceof RubyNumeric) {
             return RubyNumeric.fix2int(val);
         } else if (val instanceof RubyBoolean) {
             return val.isTrue() ? 1 : 0;
         }
         else {
             return stringAsNumber(val);
         }
     }
 
     private int stringAsNumber(IRubyObject val) {
         ByteList str = val.convertToString().getByteList();
         IRubyObject res = Pack.unpack(getRuntime(), str, FORMAT_SMALL_I).entry(0);
         
         if (res.isNil()) {
             throw getRuntime().newErrnoEINVALError();
         }
 
         return RubyNumeric.fix2int(res);
     }
 
     protected boolean asBoolean(IRubyObject val) {
         if (val instanceof RubyString) {
             return stringAsNumber(val) != 0;
         } else if(val instanceof RubyNumeric) {
             return RubyNumeric.fix2int(val) != 0;
         } else {
             return val.isTrue();
         }
     }
 
     private IRubyObject getKeepAlive(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime,
                          (socketChannel instanceof SocketChannel) ? asSocket().getKeepAlive() : false
                          );
     }
 
     private IRubyObject getLinger(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
 
         int linger = 0;
         if (socketChannel instanceof SocketChannel) {
             linger = asSocket().getSoLinger();
             if (linger < 0) {
                 linger = 0;
             }
         }
 
         return number(runtime, linger);
     }
 
     private IRubyObject getOOBInline(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime,
-                         (socketChannel instanceof SocketChannel) ? asSocket().getOOBInline() : false
-                         );
+                (socketChannel instanceof SocketChannel) ? asSocket().getOOBInline() : false
+        );
     }
 
     private IRubyObject getRcvBuf(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return number(runtime,
-                      (socketChannel instanceof SocketChannel) ? asSocket().getReceiveBufferSize() : 
-                      ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getReceiveBufferSize() : 
-                       asDatagramSocket().getReceiveBufferSize())
-                      );
+                (socketChannel instanceof SocketChannel) ? asSocket().getReceiveBufferSize() :
+                        ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getReceiveBufferSize() :
+                                asDatagramSocket().getReceiveBufferSize())
+        );
     }
 
     private IRubyObject getSndBuf(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getSendBufferSize() : 
                       ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSendBufferSize() : 0)
                       );
     }
 
     private IRubyObject getReuseAddr(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
 
         boolean reuse = false;
         if (socketChannel instanceof ServerSocketChannel) {
             reuse = asServerSocket().getReuseAddress();
         } else if (socketChannel instanceof SocketChannel) {
             reuse = asSocket().getReuseAddress();
         } else if (socketChannel instanceof DatagramChannel) {
             reuse = asDatagramSocket().getReuseAddress();
         }
 
         return trueFalse(runtime, reuse);
     }
 
     private IRubyObject getTimeout(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
         return number(runtime,
-                      (socketChannel instanceof SocketChannel) ? asSocket().getSoTimeout() : 
-                      ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getSoTimeout() : 
-                       ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSoTimeout() : 0))
-                      );
+                (socketChannel instanceof SocketChannel) ? asSocket().getSoTimeout() :
+                        ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getSoTimeout() :
+                                ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSoTimeout() : 0))
+        );
     }
 
+    @Deprecated
     protected int getSoTypeDefault() {
         return 0;
     }
+
+    protected Sock getDefaultSocketType() {
+        return Sock.SOCK_STREAM;
+    }
+
+    @Deprecated
     private int getChannelSoType(Channel channel) {
         if (channel instanceof SocketChannel || channel instanceof ServerSocketChannel) {
             return SOCK_STREAM.intValue();
         } else if (channel instanceof DatagramChannel) {
             return SOCK_DGRAM.intValue();
         } else {
-            return getSoTypeDefault();
+            return getDefaultSocketType().intValue();
         }
     }
+
+    private Sock getChannelSocketType(Channel channel) {
+        if (channel instanceof SocketChannel || channel instanceof ServerSocketChannel) {
+            return SOCK_STREAM;
+        } else if (channel instanceof DatagramChannel) {
+            return SOCK_DGRAM;
+        } else {
+            return getDefaultSocketType();
+        }
+    }
+
     private IRubyObject getSoType(Ruby runtime) throws IOException, BadDescriptorException {
         Channel socketChannel = getOpenChannel();
-        return number(runtime, getChannelSoType(socketChannel));
+        return number(runtime, getChannelSocketType(socketChannel).intValue());
     }
 
     private IRubyObject trueFalse(Ruby runtime, boolean val) {
         return number(runtime, val ? 1 : 0);
     }
 
     private static IRubyObject number(Ruby runtime, int s) {
         RubyArray array = runtime.newArray(runtime.newFixnum(s));
         return Pack.pack(runtime, array, FORMAT_SMALL_I);
     }
 
     @Deprecated
     public IRubyObject getsockopt(IRubyObject lev, IRubyObject optname) {
         return getsockopt(getRuntime().getCurrentContext(), lev, optname);
     }
     @JRubyMethod
     public IRubyObject getsockopt(ThreadContext context, IRubyObject lev, IRubyObject optname) {
         int level = RubyNumeric.fix2int(lev);
         int opt = RubyNumeric.fix2int(optname);
         Ruby runtime = context.getRuntime();
 
         try {
             switch(SocketLevel.valueOf(level)) {
             case SOL_IP:
             case SOL_SOCKET:
             case SOL_TCP:
             case SOL_UDP:
                 switch(SocketOption.valueOf(opt)) {
                 case SO_BROADCAST:
                     return getBroadcast(runtime);
                 case SO_KEEPALIVE:
                     return getKeepAlive(runtime);
                 case SO_LINGER:
                     return getLinger(runtime);
                 case SO_OOBINLINE:
                     return getOOBInline(runtime);
                 case SO_RCVBUF:
                     return getRcvBuf(runtime);
                 case SO_REUSEADDR:
                     return getReuseAddr(runtime);
                 case SO_SNDBUF:
                     return getSndBuf(runtime);
                 case SO_RCVTIMEO:
                 case SO_SNDTIMEO:
                     return getTimeout(runtime);
                 case SO_TYPE:
                     return getSoType(runtime);
 
                     // Can't support the rest with Java
                 case SO_RCVLOWAT:
                     return number(runtime, 1);
                 case SO_SNDLOWAT:
                     return number(runtime, 2048);
                 case SO_DEBUG:
                 case SO_ERROR:
                 case SO_DONTROUTE:
                 case SO_TIMESTAMP:
                     return trueFalse(runtime, false);
                 default:
                     throw context.getRuntime().newErrnoENOPROTOOPTError();
                 }
             default:
                 throw context.getRuntime().newErrnoENOPROTOOPTError();
             }
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch(IOException e) {
             throw context.getRuntime().newErrnoENOPROTOOPTError();
         }
     }
     @Deprecated
     public IRubyObject setsockopt(IRubyObject lev, IRubyObject optname, IRubyObject val) {
         return setsockopt(getRuntime().getCurrentContext(), lev, optname, val);
     }
     @JRubyMethod
     public IRubyObject setsockopt(ThreadContext context, IRubyObject lev, IRubyObject optname, IRubyObject val) {
         int level = RubyNumeric.fix2int(lev);
         int opt = RubyNumeric.fix2int(optname);
 
         try {
             switch(SocketLevel.valueOf(level)) {
             case SOL_IP:
             case SOL_SOCKET:
             case SOL_TCP:
             case SOL_UDP:
                 switch(SocketOption.valueOf(opt)) {
                 case SO_BROADCAST:
                     setBroadcast(val);
                     break;
                 case SO_KEEPALIVE:
                     setKeepAlive(val);
                     break;
                 case SO_LINGER:
                     setLinger(val);
                     break;
                 case SO_OOBINLINE:
                     setOOBInline(val);
                     break;
                 case SO_RCVBUF:
                     setRcvBuf(val);
                     break;
                 case SO_REUSEADDR:
                     setReuseAddr(val);
                     break;
                 case SO_SNDBUF:
                     setSndBuf(val);
                     break;
                 case SO_RCVTIMEO:
                 case SO_SNDTIMEO:
                     setTimeout(val);
                     break;
                     // Can't support the rest with Java
                 case SO_TYPE:
                 case SO_RCVLOWAT:
                 case SO_SNDLOWAT:
                 case SO_DEBUG:
                 case SO_ERROR:
                 case SO_DONTROUTE:
                 case SO_TIMESTAMP:
                     break;
                 default:
                     if (IPPROTO_TCP.intValue() == level && TCP_NODELAY.intValue() == opt) {
                         setTcpNoDelay(val);
                     }
                     else if (IPPROTO_IP.intValue() == level) {
                         if (MulticastStateManager.IP_ADD_MEMBERSHIP == opt) {
                             joinMulticastGroup(val);
                         }
                     } else {
                         throw context.getRuntime().newErrnoENOPROTOOPTError();
                     }
                 }
                 break;
             default:
                 if (IPPROTO_TCP.intValue() == level && TCP_NODELAY.intValue() == opt) {
                     setTcpNoDelay(val);
                 }
                 else if (IPPROTO_IP.intValue() == level) {
                     if (MulticastStateManager.IP_ADD_MEMBERSHIP == opt) {
                         joinMulticastGroup(val);
                     }
                 } else {
                     throw context.getRuntime().newErrnoENOPROTOOPTError();
                 }
             }
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch(IOException e) {
             throw context.getRuntime().newErrnoENOPROTOOPTError();
         }
         return context.getRuntime().newFixnum(0);
     }
 
     @Deprecated
     public IRubyObject getsockname() {
         return getsockname(getRuntime().getCurrentContext());
     }
 
     @JRubyMethod(name = "getsockname")
     public IRubyObject getsockname(ThreadContext context) {
         return getSocknameCommon(context, "getsockname");
     }
 
     @JRubyMethod(name = "__getsockname")
     public IRubyObject getsockname_u(ThreadContext context) {
         return getSocknameCommon(context, "__getsockname");
     }
 
     protected IRubyObject getSocknameCommon(ThreadContext context, String caller) {
         try {
-            InetSocketAddress sock = getLocalSocket(caller);
+            InetSocketAddress sock = getSocketAddress();
             if(null == sock) {
                 return Sockaddr.pack_sockaddr_in(context, 0, "0.0.0.0");
             } else {
                return Sockaddr.pack_sockaddr_in(context, sock);
             }
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
 
     @Deprecated
     public IRubyObject getpeername() {
         return getpeername(getRuntime().getCurrentContext());
     }
     @JRubyMethod(name = {"getpeername", "__getpeername"})
     public IRubyObject getpeername(ThreadContext context) {
         try {
             SocketAddress sock = getRemoteSocket();
             if(null == sock) {
                 throw context.getRuntime().newIOError("Not Supported");
             }
             return context.getRuntime().newString(sock.toString());
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject shutdown(ThreadContext context, IRubyObject[] args) {
         if (context.getRuntime().getSafeLevel() >= 4 && tainted_p(context).isFalse()) {
             throw context.getRuntime().newSecurityError("Insecure: can't shutdown socket");
         }
 
         int how = 2;
         if (args.length > 0) {
             how = RubyNumeric.fix2int(args[0]);
         }
         try {
             return shutdownInternal(context, how);
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
 
     private IRubyObject shutdownInternal(ThreadContext context, int how) throws BadDescriptorException {
         Channel socketChannel;
         switch (how) {
         case 0:
             socketChannel = getOpenChannel();
             try {
                 if (socketChannel instanceof SocketChannel ||
                         socketChannel instanceof DatagramChannel) {
                     asSocket().shutdownInput();
                 } else if (socketChannel instanceof Shutdownable) {
                     ((Shutdownable)socketChannel).shutdownInput();
                 }
             } catch (IOException e) {
                 throw context.getRuntime().newIOError(e.getMessage());
             }
             if(openFile.getPipeStream() != null) {
                 openFile.setMainStream(openFile.getPipeStream());
                 openFile.setPipeStream(null);
             }
             openFile.setMode(openFile.getMode() & ~OpenFile.READABLE);
             return RubyFixnum.zero(context.getRuntime());
         case 1:
             socketChannel = getOpenChannel();
             try {
                 if (socketChannel instanceof SocketChannel ||
                         socketChannel instanceof DatagramChannel) {
                     asSocket().shutdownOutput();
                 } else if (socketChannel instanceof Shutdownable) {
                     ((Shutdownable)socketChannel).shutdownOutput();
                 }
             } catch (IOException e) {
                 throw context.getRuntime().newIOError(e.getMessage());
             }
             openFile.setPipeStream(null);
             openFile.setMode(openFile.getMode() & ~OpenFile.WRITABLE);
             return RubyFixnum.zero(context.getRuntime());
         case 2:
             shutdownInternal(context, 0);
             shutdownInternal(context, 1);
             return RubyFixnum.zero(context.getRuntime());
         default:
             throw context.getRuntime().newArgumentError("`how' should be either 0, 1, 2");
         }
     }
 
     protected boolean doNotReverseLookup(ThreadContext context) {
         return context.getRuntime().isDoNotReverseLookupEnabled() || doNotReverseLookup;
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject do_not_reverse_lookup19(ThreadContext context) {
         return context.getRuntime().newBoolean(doNotReverseLookup);
     }
 
     @JRubyMethod(name = "do_not_reverse_lookup=", compat = CompatVersion.RUBY1_9)
     public IRubyObject set_do_not_reverse_lookup19(ThreadContext context, IRubyObject flag) {
         doNotReverseLookup = flag.isTrue();
         return do_not_reverse_lookup19(context);
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
     
     private Channel getOpenChannel() throws BadDescriptorException {
         return getOpenFileChecked().getMainStreamSafe().getDescriptor().getChannel();
     }
 }// RubyBasicSocket
diff --git a/src/org/jruby/ext/socket/RubyIPSocket.java b/src/org/jruby/ext/socket/RubyIPSocket.java
index 437cb5b119..1d47f9d580 100644
--- a/src/org/jruby/ext/socket/RubyIPSocket.java
+++ b/src/org/jruby/ext/socket/RubyIPSocket.java
@@ -1,218 +1,218 @@
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
  * Copyright (C) 2007-2010 JRuby Team <team@jruby.org>
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
 
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.UnknownHostException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.Sockaddr;
 
 import static org.jruby.CompatVersion.*;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="IPSocket", parent="BasicSocket")
 public class RubyIPSocket extends RubyBasicSocket {
     static void createIPSocket(Ruby runtime) {
         RubyClass rb_cIPSocket = runtime.defineClass("IPSocket", runtime.getClass("BasicSocket"), IPSOCKET_ALLOCATOR);
         
         rb_cIPSocket.defineAnnotatedMethods(RubyIPSocket.class);
 
         runtime.getObject().setConstant("IPsocket",rb_cIPSocket);
     }
     
     private static ObjectAllocator IPSOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIPSocket(runtime, klass);
         }
     };
 
     public RubyIPSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     protected static RuntimeException sockerr(Ruby runtime, String msg) {
         return new RaiseException(runtime, runtime.getClass("SocketError"), msg, true);
     }
 
     private IRubyObject addrFor(ThreadContext context, InetSocketAddress addr, boolean reverse) {
         Ruby r = context.getRuntime();
         IRubyObject[] ret = new IRubyObject[4];
         ret[0] = r.newString("AF_INET");
         ret[1] = r.newFixnum(addr.getPort());
         String hostAddress = addr.getAddress().getHostAddress();
         if (!reverse || doNotReverseLookup(context)) {
             ret[2] = r.newString(hostAddress);
         } else {
             ret[2] = r.newString(addr.getHostName());
         }
         ret[3] = r.newString(hostAddress);
         return r.newArrayNoCopy(ret);
     }
     
     @Deprecated
     public IRubyObject addr() {
         return addr(getRuntime().getCurrentContext());
     }
     
     @JRubyMethod
     public IRubyObject addr(ThreadContext context) {
         return addrCommon(context, true);
     }
     
     @JRubyMethod(name = "addr", compat = RUBY1_9)
     public IRubyObject addr19(ThreadContext context) {
         return addrCommon(context, true);
     }
     
     @JRubyMethod(name = "addr", compat = RUBY1_9)
     public IRubyObject addr19(ThreadContext context, IRubyObject reverse) {
         return addrCommon(context, reverse.isTrue());
     }
     
     private IRubyObject addrCommon(ThreadContext context, boolean reverse) {
         try {
-            InetSocketAddress address = getLocalSocket("addr");
+            InetSocketAddress address = getSocketAddress();
             if (address == null) {
                 throw context.getRuntime().newErrnoENOTSOCKError("Not socket or not connected");
             }
             return addrFor(context, address, reverse);
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
     
     @Deprecated
     public IRubyObject peeraddr() {
         return peeraddr(getRuntime().getCurrentContext());
     }
     
     @JRubyMethod
     public IRubyObject peeraddr(ThreadContext context) {
         return peeraddrCommon(context, true);
     }
     
     @JRubyMethod(name = "peeraddr", compat = RUBY1_9)
     public IRubyObject peeraddr19(ThreadContext context) {
         return peeraddrCommon(context, true);
     }
     
     @JRubyMethod(name = "peeraddr", compat = RUBY1_9)
     public IRubyObject peeraddr19(ThreadContext context, IRubyObject reverse) {
         return peeraddrCommon(context, reverse.isTrue());
     }
     
     private IRubyObject peeraddrCommon(ThreadContext context, boolean reverse) {
         try {
             InetSocketAddress address = getRemoteSocket();
             if (address == null) {
                 throw context.getRuntime().newErrnoENOTSOCKError("Not socket or not connected");
             }
             return addrFor(context, address, reverse);
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
     
     @Override
     protected IRubyObject getSocknameCommon(ThreadContext context, String caller) {
         try {
-            InetSocketAddress sock = getLocalSocket(caller);
+            InetSocketAddress sock = getSocketAddress();
             return Sockaddr.packSockaddrFromAddress(context, sock);
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
     @Override
     public IRubyObject getpeername(ThreadContext context) {
         try {
             InetSocketAddress sock = getRemoteSocket();
             return Sockaddr.packSockaddrFromAddress(context, sock);
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
     @Deprecated
     public static IRubyObject getaddress(IRubyObject recv, IRubyObject hostname) {
         return getaddress(recv.getRuntime().getCurrentContext(), recv, hostname);
     }
     @JRubyMethod(meta = true)
     public static IRubyObject getaddress(ThreadContext context, IRubyObject recv, IRubyObject hostname) {
         try {
             return context.getRuntime().newString(InetAddress.getByName(hostname.convertToString().toString()).getHostAddress());
         } catch(UnknownHostException e) {
             throw sockerr(context.getRuntime(), "getaddress: name or service not known");
         }
     }
 
     @JRubyMethod(required = 1, optional = 1)
     public IRubyObject recvfrom(ThreadContext context, IRubyObject[] args) {
         try {
             IRubyObject result = recv(context, args);
             InetSocketAddress sender = getRemoteSocket();
 
             int port;
             String hostName;
             String hostAddress;
 
             if (sender == null) {
                 port = 0;
                 hostName = hostAddress = "0.0.0.0";
             } else {
                 port = sender.getPort();
                 hostName = sender.getHostName();
                 hostAddress = sender.getAddress().getHostAddress();
             }
 
             IRubyObject addressArray = context.getRuntime().newArray(
                     new IRubyObject[] {
                             context.getRuntime().newString("AF_INET"),
                             context.getRuntime().newFixnum(port),
                             context.getRuntime().newString(hostName),
                             context.getRuntime().newString(hostAddress)
                     });
 
             return context.getRuntime().newArray(new IRubyObject[] { result, addressArray });
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
     }
 
 }// RubyIPSocket
diff --git a/src/org/jruby/ext/socket/RubyServerSocket.java b/src/org/jruby/ext/socket/RubyServerSocket.java
new file mode 100644
index 0000000000..b107437e3b
--- /dev/null
+++ b/src/org/jruby/ext/socket/RubyServerSocket.java
@@ -0,0 +1,160 @@
+/***** BEGIN LICENSE BLOCK *****
+ * Version: CPL 1.0/GPL 2.0/LGPL 2.1
+ *
+ * The contents of this file are subject to the Common Public
+ * License Version 1.0 (the "License"); you may not use this file
+ * except in compliance with the License. You may obtain a copy of
+ * the License at http://www.eclipse.org/legal/cpl-v10.html
+ *
+ * Software distributed under the License is distributed on an "AS
+ * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
+ * implied. See the License for the specific language governing
+ * rights and limitations under the License.
+ *
+ * Copyright (C) 2007 Ola Bini <ola@ologix.com>
+ *
+ * Alternatively, the contents of this file may be used under the terms of
+ * either of the GNU General Public License Version 2 or later (the "GPL"),
+ * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
+ * in which case the provisions of the GPL or the LGPL are applicable instead
+ * of those above. If you wish to allow use of your version of this file only
+ * under the terms of either the GPL or the LGPL, and not to allow others to
+ * use your version of this file under the terms of the CPL, indicate your
+ * decision by deleting the provisions above and replace them with the notice
+ * and other provisions required by the GPL or the LGPL. If you do not delete
+ * the provisions above, a recipient may use your version of this file under
+ * the terms of any one of the CPL, the GPL or the LGPL.
+ ***** END LICENSE BLOCK *****/
+package org.jruby.ext.socket;
+
+import jnr.constants.platform.AddressFamily;
+import jnr.constants.platform.Sock;
+import jnr.netdb.Service;
+import org.jruby.Ruby;
+import org.jruby.RubyArray;
+import org.jruby.RubyClass;
+import org.jruby.RubyFixnum;
+import org.jruby.RubyModule;
+import org.jruby.RubyNumeric;
+import org.jruby.RubyString;
+import org.jruby.anno.JRubyClass;
+import org.jruby.anno.JRubyMethod;
+import org.jruby.exceptions.RaiseException;
+import org.jruby.runtime.ObjectAllocator;
+import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.builtin.IRubyObject;
+import org.jruby.util.ByteList;
+import org.jruby.util.io.BadDescriptorException;
+import org.jruby.util.io.ChannelDescriptor;
+import org.jruby.util.io.ModeFlags;
+import org.jruby.util.io.Sockaddr;
+
+import java.io.IOException;
+import java.net.DatagramSocket;
+import java.net.Inet6Address;
+import java.net.InetAddress;
+import java.net.InetSocketAddress;
+import java.net.ServerSocket;
+import java.net.Socket;
+import java.net.SocketException;
+import java.net.UnknownHostException;
+import java.nio.channels.AlreadyConnectedException;
+import java.nio.channels.Channel;
+import java.nio.channels.ClosedChannelException;
+import java.nio.channels.ConnectionPendingException;
+import java.nio.channels.DatagramChannel;
+import java.nio.channels.SelectableChannel;
+import java.nio.channels.ServerSocketChannel;
+import java.nio.channels.SocketChannel;
+import java.util.ArrayList;
+import java.util.List;
+import java.util.regex.Matcher;
+import java.util.regex.Pattern;
+
+import static jnr.constants.platform.AddressFamily.AF_INET;
+import static jnr.constants.platform.AddressFamily.AF_INET6;
+import static jnr.constants.platform.IPProto.IPPROTO_TCP;
+import static jnr.constants.platform.IPProto.IPPROTO_UDP;
+import static jnr.constants.platform.NameInfo.NI_NUMERICHOST;
+import static jnr.constants.platform.NameInfo.NI_NUMERICSERV;
+import static jnr.constants.platform.ProtocolFamily.PF_INET;
+import static jnr.constants.platform.ProtocolFamily.PF_INET6;
+import static jnr.constants.platform.Sock.SOCK_DGRAM;
+import static jnr.constants.platform.Sock.SOCK_STREAM;
+
+/**
+ * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
+ */
+@JRubyClass(name="Socket", parent="BasicSocket", include="Socket::Constants")
+public class RubyServerSocket extends RubySocket {
+    static void createServerSocket(Ruby runtime) {
+        RubyClass rb_cSocket = runtime.defineClass("ServerSocket", runtime.getClass("Socket"), SERVER_SOCKET_ALLOCATOR);
+
+        rb_cSocket.defineAnnotatedMethods(RubyServerSocket.class);
+    }
+
+    private static ObjectAllocator SERVER_SOCKET_ALLOCATOR = new ObjectAllocator() {
+        public IRubyObject allocate(Ruby runtime, RubyClass klass) {
+            return new RubyServerSocket(runtime, klass);
+        }
+    };
+
+    public RubyServerSocket(Ruby runtime, RubyClass type) {
+        super(runtime, type);
+    }
+
+    @JRubyMethod(name = "listen")
+    public IRubyObject listen(ThreadContext context, IRubyObject backlog) {
+        return context.getRuntime().newFixnum(0);
+    }
+
+    @JRubyMethod()
+    public IRubyObject connect_nonblock(ThreadContext context, IRubyObject arg) {
+        throw sockerr(context.runtime, "server socket cannot connect");
+    }
+
+    @JRubyMethod()
+    public IRubyObject connect(ThreadContext context, IRubyObject arg) {
+        throw sockerr(context.runtime, "server socket cannot connect");
+    }
+
+    @JRubyMethod()
+    public IRubyObject accept(ThreadContext context) {
+        Ruby runtime = context.runtime;
+        ServerSocketChannel channel = (ServerSocketChannel)getChannel();
+
+        try {
+            SocketChannel socket = channel.accept();
+            RubySocket rubySocket = new RubySocket(runtime, runtime.getClass("Socket"));
+            rubySocket.initFromServer(runtime, this, socket);
+
+            return rubySocket;
+            
+        } catch (IOException ioe) {
+            throw sockerr(runtime, "bind(2): name or service not known");
+
+        }
+    }
+
+    protected ChannelDescriptor initChannel(Ruby runtime) {
+        Channel channel;
+
+        try {
+            if(soType == Sock.SOCK_STREAM) {
+                channel = ServerSocketChannel.open();
+
+            } else {
+                throw runtime.newArgumentError("unsupported server socket type `" + soType + "'");
+
+            }
+
+            ModeFlags modeFlags = newModeFlags(runtime, ModeFlags.RDWR);
+
+            return new ChannelDescriptor(channel, modeFlags);
+
+        } catch(IOException e) {
+            throw sockerr(runtime, "initialize: " + e.toString());
+
+        }
+    }
+}// RubySocket
diff --git a/src/org/jruby/ext/socket/RubySocket.java b/src/org/jruby/ext/socket/RubySocket.java
index dc71f41124..f54b52cf62 100644
--- a/src/org/jruby/ext/socket/RubySocket.java
+++ b/src/org/jruby/ext/socket/RubySocket.java
@@ -1,774 +1,555 @@
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
 
 import jnr.constants.platform.AddressFamily;
+import jnr.constants.platform.INAddr;
+import jnr.constants.platform.IPProto;
+import jnr.constants.platform.NameInfo;
+import jnr.constants.platform.ProtocolFamily;
+import jnr.constants.platform.Shutdown;
 import jnr.constants.platform.Sock;
-import jnr.netdb.Service;
+import jnr.constants.platform.SocketLevel;
+import jnr.constants.platform.SocketOption;
+import jnr.constants.platform.TCP;
+import org.jruby.CompatVersion;
 import org.jruby.Ruby;
-import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
+import org.jruby.RubySymbol;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.Sockaddr;
 
 import java.io.IOException;
 import java.net.DatagramSocket;
-import java.net.Inet6Address;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
+import java.net.ServerSocket;
 import java.net.Socket;
 import java.net.SocketException;
 import java.net.UnknownHostException;
 import java.nio.channels.AlreadyConnectedException;
 import java.nio.channels.Channel;
 import java.nio.channels.ClosedChannelException;
 import java.nio.channels.ConnectionPendingException;
 import java.nio.channels.DatagramChannel;
 import java.nio.channels.SelectableChannel;
+import java.nio.channels.ServerSocketChannel;
 import java.nio.channels.SocketChannel;
-import java.util.ArrayList;
-import java.util.List;
-import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
-import static jnr.constants.platform.AddressFamily.AF_INET;
-import static jnr.constants.platform.AddressFamily.AF_INET6;
-import static jnr.constants.platform.IPProto.IPPROTO_TCP;
-import static jnr.constants.platform.IPProto.IPPROTO_UDP;
-import static jnr.constants.platform.NameInfo.NI_NUMERICHOST;
-import static jnr.constants.platform.NameInfo.NI_NUMERICSERV;
-import static jnr.constants.platform.ProtocolFamily.PF_INET;
-import static jnr.constants.platform.ProtocolFamily.PF_INET6;
-import static jnr.constants.platform.Sock.SOCK_DGRAM;
-import static jnr.constants.platform.Sock.SOCK_STREAM;
-
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="Socket", parent="BasicSocket", include="Socket::Constants")
 public class RubySocket extends RubyBasicSocket {
     static void createSocket(Ruby runtime) {
         RubyClass rb_cSocket = runtime.defineClass("Socket", runtime.getClass("BasicSocket"), SOCKET_ALLOCATOR);
 
         RubyModule rb_mConstants = rb_cSocket.defineModuleUnder("Constants");
         // we don't have to define any that we don't support; see socket.c
 
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.Sock.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.SocketOption.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.SocketLevel.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.ProtocolFamily.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.AddressFamily.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.INAddr.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.IPProto.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.Shutdown.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.TCP.class);
-        runtime.loadConstantSet(rb_mConstants, jnr.constants.platform.NameInfo.class);
+        runtime.loadConstantSet(rb_mConstants, Sock.class);
+        runtime.loadConstantSet(rb_mConstants, SocketOption.class);
+        runtime.loadConstantSet(rb_mConstants, SocketLevel.class);
+        runtime.loadConstantSet(rb_mConstants, ProtocolFamily.class);
+        runtime.loadConstantSet(rb_mConstants, AddressFamily.class);
+        runtime.loadConstantSet(rb_mConstants, INAddr.class);
+        runtime.loadConstantSet(rb_mConstants, IPProto.class);
+        runtime.loadConstantSet(rb_mConstants, Shutdown.class);
+        runtime.loadConstantSet(rb_mConstants, TCP.class);
+        runtime.loadConstantSet(rb_mConstants, NameInfo.class);
 
         // mandatory constants we haven't implemented
         rb_mConstants.setConstant("MSG_OOB", runtime.newFixnum(MSG_OOB));
         rb_mConstants.setConstant("MSG_PEEK", runtime.newFixnum(MSG_PEEK));
         rb_mConstants.setConstant("MSG_DONTROUTE", runtime.newFixnum(MSG_DONTROUTE));
         rb_mConstants.setConstant("MSG_WAITALL", runtime.newFixnum(MSG_WAITALL));
 
         // constants webrick crashes without
         rb_mConstants.setConstant("AI_PASSIVE", runtime.newFixnum(1));
 
         // More constants needed by specs
         rb_mConstants.setConstant("IP_MULTICAST_TTL", runtime.newFixnum(10));
         rb_mConstants.setConstant("IP_MULTICAST_LOOP", runtime.newFixnum(11));
         rb_mConstants.setConstant("IP_ADD_MEMBERSHIP", runtime.newFixnum(12));
         rb_mConstants.setConstant("IP_MAX_MEMBERSHIPS", runtime.newFixnum(20));
         rb_mConstants.setConstant("IP_DEFAULT_MULTICAST_LOOP", runtime.newFixnum(1));
         rb_mConstants.setConstant("IP_DEFAULT_MULTICAST_TTL", runtime.newFixnum(1));
 
         rb_cSocket.includeModule(rb_mConstants);
 
         rb_cSocket.defineAnnotatedMethods(RubySocket.class);
+        rb_cSocket.defineAnnotatedMethods(SocketUtils.class);
     }
 
     private static ObjectAllocator SOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubySocket(runtime, klass);
         }
     };
 
     public RubySocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     @Override
+    @Deprecated
     protected int getSoTypeDefault() {
+        return soType.intValue();
+    }
+
+    @Override
+    protected Sock getDefaultSocketType() {
         return soType;
     }
 
     @JRubyMethod(meta = true)
     public static IRubyObject for_fd(ThreadContext context, IRubyObject socketClass, IRubyObject fd) {
         Ruby runtime = context.getRuntime();
 
         if (fd instanceof RubyFixnum) {
             int intFD = (int)((RubyFixnum)fd).getLongValue();
 
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(intFD);
 
             if (descriptor == null) {
                 throw runtime.newErrnoEBADFError();
             }
 
             RubySocket socket = (RubySocket)((RubyClass)socketClass).allocate();
 
             socket.initFieldsFromDescriptor(runtime, descriptor);
 
             socket.initSocket(runtime, descriptor);
 
             return socket;
         } else {
             throw runtime.newTypeError(fd, context.getRuntime().getFixnum());
         }
     }
 
-    @JRubyMethod
+    @JRubyMethod(compat = CompatVersion.RUBY1_8)
     public IRubyObject initialize(ThreadContext context, IRubyObject domain, IRubyObject type, IRubyObject protocol) {
         Ruby runtime = context.runtime;
 
         initFieldsFromArgs(runtime, domain, type, protocol);
 
         ChannelDescriptor descriptor = initChannel(runtime);
 
         initSocket(runtime, descriptor);
 
         return this;
     }
 
-    @JRubyMethod(meta = true)
-    public static IRubyObject gethostname(ThreadContext context, IRubyObject recv) {
+    @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_9)
+    public IRubyObject initialize19(ThreadContext context, IRubyObject domain, IRubyObject type) {
         Ruby runtime = context.runtime;
 
-        try {
-            return runtime.newString(InetAddress.getLocalHost().getHostName());
+        initFieldsFromArgs(runtime, domain, type);
 
-        } catch(UnknownHostException e) {
-
-            try {
-                return runtime.newString(InetAddress.getByAddress(new byte[]{0,0,0,0}).getHostName());
+        ChannelDescriptor descriptor = initChannel(runtime);
 
-            } catch(UnknownHostException e2) {
-                throw sockerr(runtime, "gethostname: name or service not known");
+        initSocket(runtime, descriptor);
 
-            }
-        }
+        return this;
     }
 
-    @JRubyMethod(required = 1, rest = true, meta = true)
-    public static IRubyObject gethostbyaddr(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+    @JRubyMethod(name = "initialize", compat = CompatVersion.RUBY1_9)
+    public IRubyObject initialize19(ThreadContext context, IRubyObject domain, IRubyObject type, IRubyObject protocol) {
         Ruby runtime = context.runtime;
-        IRubyObject[] ret = new IRubyObject[4];
 
-        ret[0] = runtime.newString(Sockaddr.addressFromString(runtime, args[0].convertToString().toString()).getCanonicalHostName());
-        ret[1] = runtime.newArray();
-        ret[2] = runtime.newFixnum(2); // AF_INET
-        ret[3] = args[0];
-
-        return runtime.newArrayNoCopy(ret);
-    }
-
-    @JRubyMethod(required = 1, optional = 1, meta = true)
-    public static IRubyObject getservbyname(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
-        Ruby runtime = context.getRuntime();
-        String name = args[0].convertToString().toString();
-        String proto = args.length ==  1 ? "tcp" : args[1].convertToString().toString();
-        Service service = Service.getServiceByName(name, proto);
-        int port;
-
-        if (service != null) {
-            port = service.getPort();
-
-        } else {
-
-            // MRI behavior: try to convert the name string to port directly
-            try {
-                port = Integer.parseInt(name.trim());
-
-            } catch (NumberFormatException nfe) {
-                throw sockerr(runtime, "no such service " + name + "/" + proto);
+        initFieldsFromArgs(runtime, domain, type, protocol);
 
-            }
+        ChannelDescriptor descriptor = initChannel(runtime);
 
-        }
+        initSocket(runtime, descriptor);
 
-        return runtime.newFixnum(port);
+        return this;
     }
 
     @JRubyMethod(name = "listen")
     public IRubyObject listen(ThreadContext context, IRubyObject backlog) {
         return context.getRuntime().newFixnum(0);
     }
 
-    @JRubyMethod(name = {"pack_sockaddr_un", "sockaddr_un"}, meta = true)
-    public static IRubyObject pack_sockaddr_un(ThreadContext context, IRubyObject recv, IRubyObject filename) {
-        String str = filename.convertToString().toString();
-
-        StringBuilder sb = new StringBuilder()
-                .append((char)0)
-                .append((char) 1)
-                .append(str);
-
-        for(int i=str.length();i<104;i++) {
-            sb.append((char)0);
-        }
-
-        return context.runtime.newString(sb.toString());
-    }
-
     @JRubyMethod()
     public IRubyObject connect_nonblock(ThreadContext context, IRubyObject arg) {
         InetSocketAddress iaddr = Sockaddr.addressFromSockaddr_in(context, arg);
 
         doConnectNonblock(context, getChannel(), iaddr);
 
         return RubyFixnum.zero(context.runtime);
     }
 
     @JRubyMethod()
     public IRubyObject connect(ThreadContext context, IRubyObject arg) {
         InetSocketAddress iaddr = Sockaddr.addressFromSockaddr_in(context, arg);
 
         doConnect(context, getChannel(), iaddr);
 
         return RubyFixnum.zero(context.runtime);
     }
 
     @JRubyMethod()
     public IRubyObject bind(ThreadContext context, IRubyObject arg) {
         InetSocketAddress iaddr = Sockaddr.addressFromSockaddr_in(context, arg);
 
         doBind(context, getChannel(), iaddr);
 
         return RubyFixnum.zero(context.getRuntime());
     }
 
-    @JRubyMethod(name = {"pack_sockaddr_in", "sockaddr_in"}, meta = true)
-    public static IRubyObject pack_sockaddr_in(ThreadContext context, IRubyObject recv, IRubyObject port, IRubyObject host) {
-        int portNum = port instanceof RubyString ?
-                Integer.parseInt(port.convertToString().toString()) :
-                RubyNumeric.fix2int(port);
-
-        return Sockaddr.pack_sockaddr_in(
-                context,
-                portNum,
-                host.isNil() ? null : host.convertToString().toString());
-    }
-
-    @JRubyMethod(meta = true)
-    public static IRubyObject unpack_sockaddr_in(ThreadContext context, IRubyObject recv, IRubyObject addr) {
-        return Sockaddr.unpack_sockaddr_in(context, addr);
-    }
-
-    @JRubyMethod(meta = true)
-    public static IRubyObject gethostbyname(ThreadContext context, IRubyObject recv, IRubyObject hostname) {
-        Ruby runtime = context.runtime;
-
-        try {
-            InetAddress addr = getRubyInetAddress(hostname.convertToString().getByteList());
-            IRubyObject[] ret = new IRubyObject[4];
-
-            ret[0] = runtime.newString(addr.getCanonicalHostName());
-            ret[1] = runtime.newArray();
-            ret[2] = runtime.newFixnum(2); // AF_INET
-            ret[3] = runtime.newString(new ByteList(addr.getAddress()));
-            return runtime.newArrayNoCopy(ret);
-
-        } catch(UnknownHostException e) {
-            throw sockerr(runtime, "gethostbyname: name or service not known");
-
-        }
-    }
-
-    /**
-     * Ruby definition would look like:
-     *
-     * def self.getaddrinfo(host, port, family = nil, socktype = nil, protocol = nil, flags = nil)
-     */
-    @JRubyMethod(required = 2, optional = 4, meta = true)
-    public static IRubyObject getaddrinfo(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
-        Ruby runtime = context.runtime;
-        IRubyObject host = args[0];
-        IRubyObject port = args[1];
-        boolean emptyHost = host.isNil() || host.convertToString().isEmpty();
-
-        try {
-            if(port instanceof RubyString) {
-                port = getservbyname(context, recv, new IRubyObject[]{port});
-            }
-
-            IRubyObject family = args.length > 2 ? args[2] : context.nil;
-            IRubyObject socktype = args.length > 3 ? args[3] : context.nil;
-            //IRubyObject protocol = args[4];
-            IRubyObject flags = args.length > 5 ? args[5] : context.nil;
-
-            boolean is_ipv6 = (! family.isNil()) && (RubyNumeric.fix2int(family) & AF_INET6.intValue()) == AF_INET6.intValue();
-            boolean sock_stream = true;
-            boolean sock_dgram = true;
-
-            if(!socktype.isNil()) {
-                int val = RubyNumeric.fix2int(socktype);
-
-                if(val == SOCK_STREAM.intValue()) {
-                    sock_dgram = false;
-
-                } else if(val == SOCK_DGRAM.intValue()) {
-                    sock_stream = false;
-
-                }
-            }
-
-            // When Socket::AI_PASSIVE and host is nil, return 'any' address.
-            InetAddress[] addrs = null;
-
-            if(!flags.isNil() && RubyFixnum.fix2int(flags) > 0) {
-                // The value of 1 is for Socket::AI_PASSIVE.
-                int flag = RubyNumeric.fix2int(flags);
-
-                if ((flag == 1) && emptyHost ) {
-                    // use RFC 2732 style string to ensure that we get Inet6Address
-                    addrs = InetAddress.getAllByName(is_ipv6 ? "[::]" : "0.0.0.0");
-                }
-
-            }
-
-            if (addrs == null) {
-                addrs = InetAddress.getAllByName(emptyHost ? (is_ipv6 ? "[::1]" : null) : host.convertToString().toString());
-            }
-
-            List<IRubyObject> l = new ArrayList<IRubyObject>();
-
-            for(int i = 0; i < addrs.length; i++) {
-                IRubyObject[] c;
-
-                if(sock_dgram) {
-                    c = new IRubyObject[7];
-                    c[0] = runtime.newString(is_ipv6 ? "AF_INET6" : "AF_INET");
-                    c[1] = port;
-                    c[2] = runtime.newString(getHostAddress(recv, addrs[i]));
-                    c[3] = runtime.newString(addrs[i].getHostAddress());
-                    c[4] = runtime.newFixnum(is_ipv6 ? PF_INET6 : PF_INET);
-                    c[5] = runtime.newFixnum(SOCK_DGRAM);
-                    c[6] = runtime.newFixnum(IPPROTO_UDP);
-                    l.add(runtime.newArrayNoCopy(c));
-                }
-
-                if(sock_stream) {
-                    c = new IRubyObject[7];
-                    c[0] = runtime.newString(is_ipv6 ? "AF_INET6" : "AF_INET");
-                    c[1] = port;
-                    c[2] = runtime.newString(getHostAddress(recv, addrs[i]));
-                    c[3] = runtime.newString(addrs[i].getHostAddress());
-                    c[4] = runtime.newFixnum(is_ipv6 ? PF_INET6 : PF_INET);
-                    c[5] = runtime.newFixnum(SOCK_STREAM);
-                    c[6] = runtime.newFixnum(IPPROTO_TCP);
-                    l.add(runtime.newArrayNoCopy(c));
-                }
-            }
-
-            return runtime.newArray(l);
-
-        } catch(UnknownHostException e) {
-            throw sockerr(runtime, "getaddrinfo: name or service not known");
-
-        }
-    }
-
-    @JRubyMethod(required = 1, optional = 1, meta = true)
-    public static IRubyObject getnameinfo(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
-        Ruby runtime = context.runtime;
-        int flags = args.length == 2 ? RubyNumeric.num2int(args[1]) : 0;
-        IRubyObject arg0 = args[0];
-        String host, port;
-
-        if (arg0 instanceof RubyArray) {
-            List list = ((RubyArray)arg0).getList();
-            int len = list.size();
-
-            if (len < 3 || len > 4) {
-                throw runtime.newArgumentError("array size should be 3 or 4, "+len+" given");
-            }
-
-            // if array has 4 elements, third element is ignored
-            host = list.size() == 3 ? list.get(2).toString() : list.get(3).toString();
-            port = list.get(1).toString();
-
-        } else if (arg0 instanceof RubyString) {
-            String arg = ((RubyString)arg0).toString();
-            Matcher m = STRING_IPV4_ADDRESS_PATTERN.matcher(arg);
-
-            if (!m.matches()) {
-                IRubyObject obj = unpack_sockaddr_in(context, recv, arg0);
-
-                if (obj instanceof RubyArray) {
-                    List list = ((RubyArray)obj).getList();
-                    int len = list.size();
-
-                    if (len != 2) {
-                        throw runtime.newArgumentError("invalid address representation");
-                    }
-
-                    host = list.get(1).toString();
-                    port = list.get(0).toString();
-
-                } else {
-                    throw runtime.newArgumentError("invalid address string");
-
-                }
-
-            } else if ((host = m.group(IPV4_HOST_GROUP)) == null || host.length() == 0 ||
-                    (port = m.group(IPV4_PORT_GROUP)) == null || port.length() == 0) {
-
-                throw runtime.newArgumentError("invalid address string");
-
-            } else {
-
-                // Try IPv6
-                try {
-                    InetAddress ipv6_addr = InetAddress.getByName(host);
-
-                    if (ipv6_addr instanceof Inet6Address) {
-                        host = ipv6_addr.getHostAddress();
-                    }
-
-                } catch (UnknownHostException uhe) {
-                    throw runtime.newArgumentError("invalid address string");
-
-                }
-            }
-
-        } else {
-            throw runtime.newArgumentError("invalid args");
-
-        }
-
-        InetAddress addr;
-
-        try {
-            addr = InetAddress.getByName(host);
-
-        } catch (UnknownHostException e) {
-            throw sockerr(runtime, "unknown host: "+ host);
-
-        }
-
-        if ((flags & NI_NUMERICHOST.intValue()) == 0) {
-            host = addr.getCanonicalHostName();
-
-        } else {
-            host = addr.getHostAddress();
-
-        }
-
-        jnr.netdb.Service serv = jnr.netdb.Service.getServiceByPort(Integer.parseInt(port), null);
-
-        if (serv != null) {
-
-            if ((flags & NI_NUMERICSERV.intValue()) == 0) {
-                port = serv.getName();
-
-            } else {
-                port = Integer.toString(serv.getPort());
-
-            }
-
-        }
-
-        return runtime.newArray(runtime.newString(host), runtime.newString(port));
-
-    }
-
     private void initFieldsFromDescriptor(Ruby runtime, ChannelDescriptor descriptor) {
         Channel mainChannel = descriptor.getChannel();
 
         if (mainChannel instanceof SocketChannel) {
             // ok, it's a socket...set values accordingly
             // just using AF_INET since we can't tell from SocketChannel...
-            soDomain = AddressFamily.AF_INET.intValue();
-            soType = Sock.SOCK_STREAM.intValue();
-            soProtocol = 0;
+            soDomain = AddressFamily.AF_INET;
+            soType = Sock.SOCK_STREAM;
+            soProtocol = ProtocolFamily.PF_INET;
 
         } else if (mainChannel instanceof DatagramChannel) {
             // datagram, set accordingly
             // again, AF_INET
-            soDomain = AddressFamily.AF_INET.intValue();
-            soType = Sock.SOCK_DGRAM.intValue();
-            soProtocol = 0;
+            soDomain = AddressFamily.AF_INET;
+            soType = Sock.SOCK_DGRAM;
+            soProtocol = ProtocolFamily.PF_INET;
 
         } else {
             throw runtime.newErrnoENOTSOCKError("can't Socket.new/for_fd against a non-socket");
         }
     }
 
     private void initFieldsFromArgs(Ruby runtime, IRubyObject domain, IRubyObject type, IRubyObject protocol) {
         initDomain(runtime, domain);
 
         initType(runtime, type);
 
-        initProtocol(protocol);
+        initProtocol(runtime, protocol);
+    }
+
+    private void initFieldsFromArgs(Ruby runtime, IRubyObject domain, IRubyObject type) {
+        initDomain(runtime, domain);
+
+        initType(runtime, type);
+    }
+
+    protected void initFromServer(Ruby runtime, RubyServerSocket serverSocket, SocketChannel socketChannel) {
+        soDomain = serverSocket.soDomain;
+        soType = serverSocket.soType;
+        soProtocol = serverSocket.soProtocol;
+
+        initSocket(runtime, newChannelDescriptor(runtime, socketChannel));
     }
 
-    private ChannelDescriptor initChannel(Ruby runtime) {
+    protected ChannelDescriptor initChannel(Ruby runtime) {
         Channel channel;
 
         try {
-            if(soType == Sock.SOCK_STREAM.intValue()) {
+            if(soType == Sock.SOCK_STREAM) {
                 channel = SocketChannel.open();
 
-            } else if(soType == Sock.SOCK_DGRAM.intValue()) {
+            } else if(soType == Sock.SOCK_DGRAM) {
                 channel = DatagramChannel.open();
 
             } else {
                 throw runtime.newArgumentError("unsupported socket type `" + soType + "'");
 
             }
 
-            ModeFlags modeFlags = newModeFlags(runtime, ModeFlags.RDWR);
-
-            return new ChannelDescriptor(channel, modeFlags);
+            return newChannelDescriptor(runtime, channel);
 
         } catch(IOException e) {
             throw sockerr(runtime, "initialize: " + e.toString());
 
         }
     }
 
-    private void initProtocol(IRubyObject protocol) {
-        soProtocol = RubyNumeric.fix2int(protocol);
+    protected static ChannelDescriptor newChannelDescriptor(Ruby runtime, Channel channel) {
+        ModeFlags modeFlags = newModeFlags(runtime, ModeFlags.RDWR);
+
+        return new ChannelDescriptor(channel, modeFlags);
+    }
+
+    private void initProtocol(Ruby runtime, IRubyObject protocol) {
+        ProtocolFamily protocolFamily = null;
+        
+        if(protocol instanceof RubyString || protocol instanceof RubySymbol) {
+            String protocolString = protocol.toString();
+            protocolFamily = ProtocolFamily.valueOf("PF_" + protocolString);
+        } else {
+            int protocolInt = RubyNumeric.fix2int(protocol);
+            protocolFamily = ProtocolFamily.valueOf(protocolInt);
+        }
+
+        if (protocolFamily == null) {
+            throw sockerr(runtime, "unknown socket protocol " + protocol);
+        }
+
+        soProtocol = protocolFamily;
     }
 
     private void initType(Ruby runtime, IRubyObject type) {
-        if(type instanceof RubyString) {
+        Sock sockType = null;
+
+        if(type instanceof RubyString || type instanceof RubySymbol) {
             String typeString = type.toString();
-            if(typeString.equals("SOCK_STREAM")) {
-                soType = SOCK_STREAM.intValue();
-            } else if(typeString.equals("SOCK_DGRAM")) {
-                soType = SOCK_DGRAM.intValue();
-            } else {
-                throw sockerr(runtime, "unknown socket type " + typeString);
-            }
+            sockType = Sock.valueOf("SOCK_" + typeString);
         } else {
-            soType = RubyNumeric.fix2int(type);
+            int typeInt = RubyNumeric.fix2int(type);
+            sockType = Sock.valueOf(typeInt);
+        }
+
+        if (sockType == null) {
+            throw sockerr(runtime, "unknown socket type " + type);
         }
+
+        soType = sockType;
     }
 
     private void initDomain(Ruby runtime, IRubyObject domain) {
-        if(domain instanceof RubyString) {
+        AddressFamily addressFamily = null;
+
+        if(domain instanceof RubyString || domain instanceof RubySymbol) {
             String domainString = domain.toString();
-            if(domainString.equals("AF_INET")) {
-                soDomain = AF_INET.intValue();
-            } else if(domainString.equals("PF_INET")) {
-                soDomain = PF_INET.intValue();
-            } else {
-                throw sockerr(runtime, "unknown socket domain " + domainString);
-            }
+            addressFamily = AddressFamily.valueOf("AF_" + domainString);
         } else {
-            soDomain = RubyNumeric.fix2int(domain);
+            int domainInt = RubyNumeric.fix2int(domain);
+            addressFamily = AddressFamily.valueOf(domainInt);
+        }
+
+        if (addressFamily == null) {
+            throw sockerr(runtime, "unknown socket domain " + domain);
         }
+
+        soDomain = addressFamily;
     }
 
     private void doConnectNonblock(ThreadContext context, Channel channel, InetSocketAddress iaddr) {
         try {
             if (channel instanceof SelectableChannel) {
                 SelectableChannel selectable = (SelectableChannel)channel;
                 selectable.configureBlocking(false);
 
                 doConnect(context, channel, iaddr);
             } else {
                 throw getRuntime().newErrnoENOPROTOOPTError();
 
             }
 
         } catch(ClosedChannelException e) {
             throw context.getRuntime().newErrnoECONNREFUSEDError();
 
         } catch(IOException e) {
             throw sockerr(context.getRuntime(), "connect(2): name or service not known");
 
         }
     }
 
-    private void doConnect(ThreadContext context, Channel channel, InetSocketAddress iaddr) {
+    protected void doConnect(ThreadContext context, Channel channel, InetSocketAddress iaddr) {
         Ruby runtime = context.runtime;
 
         try {
             if (channel instanceof SocketChannel) {
                 SocketChannel socket = (SocketChannel)channel;
 
                 if(!socket.connect(iaddr)) {
                     throw context.getRuntime().newErrnoEINPROGRESSError();
                 }
 
             } else if (channel instanceof DatagramChannel) {
                 ((DatagramChannel)channel).connect(iaddr);
 
             } else {
                 throw getRuntime().newErrnoENOPROTOOPTError();
 
             }
 
         } catch(AlreadyConnectedException e) {
             throw runtime.newErrnoEISCONNError();
 
         } catch(ConnectionPendingException e) {
             throw runtime.newErrnoEINPROGRESSError();
 
         } catch(UnknownHostException e) {
             throw sockerr(context.getRuntime(), "connect(2): unknown host");
 
         } catch(SocketException e) {
             handleSocketException(context.getRuntime(), "connect", e);
 
         } catch(IOException e) {
             throw sockerr(context.getRuntime(), "connect(2): name or service not known");
 
         } catch (IllegalArgumentException iae) {
             throw sockerr(context.getRuntime(), iae.getMessage());
 
         }
     }
 
-    private void doBind(ThreadContext context, Channel channel, InetSocketAddress iaddr) {
+    protected void doBind(ThreadContext context, Channel channel, InetSocketAddress iaddr) {
         Ruby runtime = context.runtime;
 
         try {
             if (channel instanceof SocketChannel) {
                 Socket socket = ((SocketChannel)channel).socket();
                 socket.bind(iaddr);
 
+            } else if (channel instanceof ServerSocketChannel) {
+                ServerSocket socket = ((ServerSocketChannel)channel).socket();
+                socket.bind(iaddr);
+
             } else if (channel instanceof DatagramChannel) {
                 DatagramSocket socket = ((DatagramChannel)channel).socket();
                 socket.bind(iaddr);
 
             } else {
                 throw runtime.newErrnoENOPROTOOPTError();
             }
 
         } catch(UnknownHostException e) {
             throw sockerr(runtime, "bind(2): unknown host");
 
         } catch(SocketException e) {
             handleSocketException(runtime, "bind", e);
 
         } catch(IOException e) {
             throw sockerr(runtime, "bind(2): name or service not known");
 
         } catch (IllegalArgumentException iae) {
             throw sockerr(runtime, iae.getMessage());
 
         }
     }
 
-    private void handleSocketException(Ruby runtime, String caller, SocketException e) {
-        // e.printStackTrace();
+    protected void handleSocketException(Ruby runtime, String caller, SocketException e) {
         String msg = formatMessage(e, "bind");
 
         // This is ugly, but what can we do, Java provides the same exception type
         // for different situations, so we differentiate the errors
         // based on the exception's message.
         if (ALREADY_BOUND_PATTERN.matcher(msg).find()) {
             throw runtime.newErrnoEINVALError(msg);
         } else if (ADDR_NOT_AVAIL_PATTERN.matcher(msg).find()) {
             throw runtime.newErrnoEADDRNOTAVAILError(msg);
         } else if (PERM_DENIED_PATTERN.matcher(msg).find()) {
             throw runtime.newErrnoEACCESError(msg);
         } else {
             throw runtime.newErrnoEADDRINUSEError(msg);
         }
     }
 
     private static String formatMessage(Throwable e, String defaultMsg) {
         String msg = e.getMessage();
         if (msg == null) {
             msg = defaultMsg;
         } else {
             msg = defaultMsg + " - " + msg;
         }
         return msg;
     }
 
-    public static InetAddress getRubyInetAddress(ByteList address) throws UnknownHostException {
-        if (address.equal(BROADCAST)) {
-            return InetAddress.getByAddress(INADDR_BROADCAST);
+    public static RuntimeException sockerr(Ruby runtime, String msg) {
+        return new RaiseException(runtime, runtime.getClass("SocketError"), msg, true);
+    }
 
-        } else if (address.equal(ANY)) {
-            return InetAddress.getByAddress(INADDR_ANY);
+    @Deprecated
+    public static IRubyObject gethostname(ThreadContext context, IRubyObject recv) {
+        return SocketUtils.gethostname(context, recv);
+    }
 
-        } else {
-            return InetAddress.getByName(address.toString());
+    @Deprecated
+    public static IRubyObject gethostbyaddr(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+        return SocketUtils.gethostbyaddr(context, recv, args);
+    }
 
-        }
+    @Deprecated
+    public static IRubyObject getservbyname(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+        return SocketUtils.getservbyname(context, recv, args);
     }
 
-    public static RuntimeException sockerr(Ruby runtime, String msg) {
-        return new RaiseException(runtime, runtime.getClass("SocketError"), msg, true);
+    @Deprecated
+    public static IRubyObject pack_sockaddr_un(ThreadContext context, IRubyObject recv, IRubyObject filename) {
+        return SocketUtils.pack_sockaddr_un(context, recv, filename);
     }
 
-    private static String getHostAddress(IRubyObject recv, InetAddress addr) {
-        return do_not_reverse_lookup(recv).isTrue() ? addr.getHostAddress() : addr.getCanonicalHostName();
+    @Deprecated
+    public static IRubyObject pack_sockaddr_in(ThreadContext context, IRubyObject recv, IRubyObject port, IRubyObject host) {
+        return SocketUtils.pack_sockaddr_in(context, recv, port, host);
     }
 
-    private static final Pattern STRING_IPV4_ADDRESS_PATTERN =
-        Pattern.compile("((.*)\\/)?([\\.0-9]+)(:([0-9]+))?");
+    @Deprecated
+    public static IRubyObject unpack_sockaddr_in(ThreadContext context, IRubyObject recv, IRubyObject addr) {
+        return SocketUtils.unpack_sockaddr_in(context, recv, addr);
+    }
+
+    @Deprecated
+    public static IRubyObject gethostbyname(ThreadContext context, IRubyObject recv, IRubyObject hostname) {
+        return SocketUtils.gethostbyname(context, recv, hostname);
+    }
+
+    @Deprecated
+    public static IRubyObject getaddrinfo(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+        return SocketUtils.getaddrinfo(context, recv, args);
+    }
+
+    @Deprecated
+    public static IRubyObject getnameinfo(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+        return SocketUtils.getnameinfo(context, recv, args);
+    }
+
+    @Deprecated
+    public static InetAddress getRubyInetAddress(ByteList address) throws UnknownHostException {
+        return SocketUtils.getRubyInetAddress(address);
+    }
 
     private static final Pattern ALREADY_BOUND_PATTERN = Pattern.compile("[Aa]lready.*bound");
     private static final Pattern ADDR_NOT_AVAIL_PATTERN = Pattern.compile("assign.*address");
     private static final Pattern PERM_DENIED_PATTERN = Pattern.compile("[Pp]ermission.*denied");
 
-    private static final int IPV4_HOST_GROUP = 3;
-    private static final int IPV4_PORT_GROUP = 5;
-
     public static final int MSG_OOB = 0x1;
     public static final int MSG_PEEK = 0x2;
     public static final int MSG_DONTROUTE = 0x4;
     public static final int MSG_WAITALL = 0x100;
 
-    private static final ByteList BROADCAST = new ByteList("<broadcast>".getBytes());
-    private static final byte[] INADDR_BROADCAST = new byte[] {-1,-1,-1,-1}; // 255.255.255.255
-    private static final ByteList ANY = new ByteList("<any>".getBytes());
-    private static final byte[] INADDR_ANY = new byte[] {0,0,0,0}; // 0.0.0.0
-
-    private int soDomain;
-    private int soType;
-    private int soProtocol;
+    protected AddressFamily soDomain;
+    protected Sock soType;
+    protected ProtocolFamily soProtocol;
 }// RubySocket
diff --git a/src/org/jruby/ext/socket/RubyTCPServer.java b/src/org/jruby/ext/socket/RubyTCPServer.java
index 8d9a8ca993..5adc7199e8 100644
--- a/src/org/jruby/ext/socket/RubyTCPServer.java
+++ b/src/org/jruby/ext/socket/RubyTCPServer.java
@@ -1,261 +1,261 @@
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
 
 import java.io.IOException;
 import java.net.BindException;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.UnknownHostException;
 import java.net.SocketException;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.nio.channels.ServerSocketChannel;
 import java.nio.channels.SocketChannel;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyInteger;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.ModeFlags;
 
 import org.jruby.util.io.SelectorFactory;
 import java.nio.channels.spi.SelectorProvider;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="TCPServer", parent="TCPSocket")
 public class RubyTCPServer extends RubyTCPSocket {
     static void createTCPServer(Ruby runtime) {
         RubyClass rb_cTCPServer = runtime.defineClass(
                 "TCPServer", runtime.getClass("TCPSocket"), TCPSERVER_ALLOCATOR);
 
         rb_cTCPServer.defineAnnotatedMethods(RubyTCPServer.class);
 
         runtime.getObject().setConstant("TCPserver",rb_cTCPServer);
     }
 
     private static ObjectAllocator TCPSERVER_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyTCPServer(runtime, klass);
         }
     };
 
     public RubyTCPServer(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     private ServerSocketChannel ssc;
     private InetSocketAddress socket_address;
 
     @JRubyMethod(name = "initialize", required = 1, optional = 1, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.runtime;
         IRubyObject hostname = args[0];
         IRubyObject port = args.length > 1 ? args[1] : context.nil;
 
         if(hostname.isNil()
                 || ((hostname instanceof RubyString)
                         && ((RubyString) hostname).isEmpty())) {
             hostname = runtime.newString("0.0.0.0");
         } else if (hostname instanceof RubyFixnum) {
             // numeric host, use it for port
             port = hostname;
             hostname = runtime.newString("0.0.0.0");
         }
 
         String shost = hostname.convertToString().toString();
         try {
             InetAddress addr = InetAddress.getByName(shost);
             ssc = ServerSocketChannel.open();
 
             int portInt;
             if (port instanceof RubyInteger) {
                 portInt = RubyNumeric.fix2int(port);
             } else {
                 IRubyObject portString = port.convertToString();
                 IRubyObject portInteger = portString.convertToInteger( "to_i");
                 portInt = RubyNumeric.fix2int(portInteger);
 
                 if (portInt <= 0) {
-                    portInt = RubyNumeric.fix2int(RubySocket.getservbyname(
+                    portInt = RubyNumeric.fix2int(SocketUtils.getservbyname(
                             context, runtime.getObject(), new IRubyObject[] {portString}));
                 }
             }
 
             socket_address = new InetSocketAddress(addr, portInt);
             ssc.socket().bind(socket_address);
             initSocket(runtime, new ChannelDescriptor(
                     ssc, newModeFlags(runtime, ModeFlags.RDWR)));
         } catch(UnknownHostException e) {
             throw sockerr(runtime, "initialize: name or service not known");
         } catch(BindException e) {
             throw runtime.newErrnoEADDRFromBindException(e);
         } catch(SocketException e) {
             String msg = e.getMessage();
             if(msg.indexOf("Permission denied") != -1) {
                 throw runtime.newErrnoEACCESError("bind(2)");
             } else {
                 throw sockerr(runtime, "initialize: name or service not known");
             }
         } catch(IOException e) {
             throw sockerr(runtime, "initialize: name or service not known");
         } catch (IllegalArgumentException iae) {
             throw sockerr(runtime, iae.getMessage());
         }
 
         return this;
     }
     @Deprecated
     public IRubyObject accept() {
         return accept(getRuntime().getCurrentContext());
     }
     @JRubyMethod(name = "accept")
     public IRubyObject accept(ThreadContext context) {
         Ruby runtime = context.runtime;
         RubyTCPSocket socket = new RubyTCPSocket(runtime, runtime.getClass("TCPSocket"));
 
         try {
             while (true) {
                 boolean ready = context.getThread().select(this, SelectionKey.OP_ACCEPT);
                 if (!ready) {
                     // we were woken up without being selected...poll for thread events and go back to sleep
                     context.pollThreadEvents();
 
                 } else {
                     SocketChannel connected = ssc.accept();
                     connected.finishConnect();
 
                     //
                     // Force the client socket to be blocking
                     //
                     synchronized (connected.blockingLock()) {
                         connected.configureBlocking(false);
                         connected.configureBlocking(true);
                     }
 
                     // otherwise one key has been selected (ours) so we get the channel and hand it off
                     socket.initSocket(context.getRuntime(), new ChannelDescriptor(connected, newModeFlags(runtime, ModeFlags.RDWR)));
 
                     return socket;
                 }
             }
         } catch(IOException e) {
             throw sockerr(runtime, "problem when accepting");
         }
     }
 
     @JRubyMethod(name = "accept_nonblock")
     public IRubyObject accept_nonblock(ThreadContext context) {
         Ruby runtime = context.runtime;
         RubyTCPSocket socket = new RubyTCPSocket(runtime, runtime.getClass("TCPSocket"));
         Selector selector = null;
 
         synchronized (ssc.blockingLock()) {
             boolean oldBlocking = ssc.isBlocking();
 
             try {
                 ssc.configureBlocking(false);
                 selector = SelectorFactory.openWithRetryFrom(runtime, SelectorProvider.provider());
 
                 boolean ready = context.getThread().select(this, SelectionKey.OP_ACCEPT, 0);
 
                 if (!ready) {
                     // no connection immediately accepted, let them try again
                     throw runtime.newErrnoEAGAINError("Resource temporarily unavailable");
 
                 } else {
                     // otherwise one key has been selected (ours) so we get the channel and hand it off
                     socket.initSocket(context.getRuntime(), new ChannelDescriptor(ssc.accept(), newModeFlags(runtime, ModeFlags.RDWR)));
 
                     return socket;
                 }
             } catch(IOException e) {
                 throw sockerr(context.getRuntime(), "problem when accepting");
 
             } finally {
                 try {
                     if (selector != null) selector.close();
                 } catch (Exception e) {
                 }
                 try {ssc.configureBlocking(oldBlocking);} catch (IOException ioe) {}
 
             }
         }
     }
     @Deprecated
     public IRubyObject listen(IRubyObject backlog) {
         return listen(getRuntime().getCurrentContext(), backlog);
     }
     @JRubyMethod(name = "listen", required = 1)
     public IRubyObject listen(ThreadContext context, IRubyObject backlog) {
         return RubyFixnum.zero(context.getRuntime());
     }
 
     @JRubyMethod(name = "peeraddr", rest = true)
     public IRubyObject peeraddr(ThreadContext context, IRubyObject[] args) {
         throw context.getRuntime().newNotImplementedError("not supported");
     }
 
     @JRubyMethod(name = "getpeername", rest = true)
     public IRubyObject getpeername(ThreadContext context, IRubyObject[] args) {
         throw context.getRuntime().newNotImplementedError("not supported");
     }
     @Deprecated
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         return open(recv.getRuntime().getCurrentContext(), recv, args, block);
     }
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject tcpServer = recv.callMethod(context, "new", args);
 
         if (!block.isGiven()) return tcpServer;
 
         try {
             return block.yield(context, tcpServer);
         } finally {
             tcpServer.callMethod(context, "close");
         }
     }
 
     @Override
     public IRubyObject gets(ThreadContext context) {
         throw context.getRuntime().newErrnoENOTCONNError();
     }
 }
diff --git a/src/org/jruby/ext/socket/RubyUDPSocket.java b/src/org/jruby/ext/socket/RubyUDPSocket.java
index 1bffd507cb..08be233a18 100644
--- a/src/org/jruby/ext/socket/RubyUDPSocket.java
+++ b/src/org/jruby/ext/socket/RubyUDPSocket.java
@@ -1,323 +1,323 @@
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
  * Copyright (C) 2007 Damian Steer <pldms@mac.com>
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
 
 import java.io.IOException;
 import java.net.ConnectException;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.PortUnreachableException;
 import java.net.SocketException;
 import java.net.MulticastSocket;
 import java.net.UnknownHostException;
 import java.net.DatagramPacket;
 import java.nio.ByteBuffer;
 import java.nio.channels.DatagramChannel;
 
 import java.nio.channels.SelectionKey;
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.io.ChannelDescriptor;
 
 /**
  * @author <a href="mailto:pldms@mac.com">Damian Steer</a>
  */
 @JRubyClass(name="UDPSocket", parent="IPSocket")
 public class RubyUDPSocket extends RubyIPSocket {
 
     static void createUDPSocket(Ruby runtime) {
         RubyClass rb_cUDPSocket = runtime.defineClass("UDPSocket", runtime.getClass("IPSocket"), UDPSOCKET_ALLOCATOR);
         
         rb_cUDPSocket.includeModule(runtime.getClass("Socket").getConstant("Constants"));
 
         rb_cUDPSocket.defineAnnotatedMethods(RubyUDPSocket.class);
 
         runtime.getObject().setConstant("UDPsocket", rb_cUDPSocket);
     }
     private static ObjectAllocator UDPSOCKET_ALLOCATOR = new ObjectAllocator() {
 
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyUDPSocket(runtime, klass);
         }
     };
 
     public RubyUDPSocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     @JRubyMethod(visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context) {
         Ruby runtime = context.runtime;
 
         try {
             DatagramChannel channel = DatagramChannel.open();
             initSocket(runtime, new ChannelDescriptor(channel, newModeFlags(runtime, ModeFlags.RDWR)));
         } catch (ConnectException e) {
             throw runtime.newErrnoECONNREFUSEDError();
         } catch (UnknownHostException e) {
             throw sockerr(runtime, "initialize: name or service not known");
         } catch (IOException e) {
             throw sockerr(runtime, "initialize: name or service not known");
         }
         return this;
     }
 
     @JRubyMethod(visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject protocol) {
         // we basically ignore protocol. let someone report it...
         return initialize(context);
     }
     
     @Deprecated
     public IRubyObject bind(IRubyObject host, IRubyObject port) {
         return bind(getRuntime().getCurrentContext(), host, port);
     }
     @JRubyMethod
     public IRubyObject bind(ThreadContext context, IRubyObject host, IRubyObject port) {
         InetSocketAddress addr = null;
         try {
             if (host.isNil()
                 || ((host instanceof RubyString)
                 && ((RubyString) host).isEmpty())) {
                 // host is nil or the empty string, bind to INADDR_ANY
                 addr = new InetSocketAddress(RubyNumeric.fix2int(port));
             } else if (host instanceof RubyFixnum) {
                 // passing in something like INADDR_ANY
                 int intAddr = RubyNumeric.fix2int(host);
                 RubyModule socketMod = context.getRuntime().getModule("Socket");
                 if (intAddr == RubyNumeric.fix2int(socketMod.getConstant("INADDR_ANY"))) {
                     addr = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), RubyNumeric.fix2int(port));
                 }
             } else {
                 // passing in something like INADDR_ANY
                 addr = new InetSocketAddress(InetAddress.getByName(host.convertToString().toString()), RubyNumeric.fix2int(port));
             }
 
             if (this.multicastStateManager == null) {
                 ((DatagramChannel) this.getChannel()).socket().bind(addr);
             } else {
                 this.multicastStateManager.rebindToPort(RubyNumeric.fix2int(port));
             }
 
             return RubyFixnum.zero(context.getRuntime());
         } catch (UnknownHostException e) {
             throw sockerr(context.getRuntime(), "bind: name or service not known");
         } catch (SocketException e) {
             throw sockerr(context.getRuntime(), "bind: name or service not known");
         } catch (IOException e) {
             throw sockerr(context.getRuntime(), "bind: name or service not known");
         } catch (Error e) {
             // Workaround for a bug in Sun's JDK 1.5.x, see
             // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6303753
             if (e.getCause() instanceof SocketException) {
                 throw sockerr(context.getRuntime(), "bind: name or service not known");
             } else {
                 throw e;
             }
         }
     }
     @Deprecated
     public IRubyObject connect(IRubyObject host, IRubyObject port) {
         return connect(getRuntime().getCurrentContext(), host, port);
     }
     @JRubyMethod
     public IRubyObject connect(ThreadContext context, IRubyObject host, IRubyObject port) {
         try {
             InetSocketAddress addr;
             addr = new InetSocketAddress(InetAddress.getByName(host.convertToString().toString()), RubyNumeric.fix2int(port));
             ((DatagramChannel) this.getChannel()).connect(addr);
             return RubyFixnum.zero(context.getRuntime());
         } catch (UnknownHostException e) {
             throw sockerr(context.getRuntime(), "connect: name or service not known");
         } catch (IOException e) {
             throw sockerr(context.getRuntime(), "connect: name or service not known");
         }
     }
 
     @Deprecated
     public IRubyObject recvfrom(IRubyObject[] args) {
         return recvfrom(getRuntime().getCurrentContext(), args);
     }
 
     @JRubyMethod(required = 1, rest = true)
     public IRubyObject recvfrom(ThreadContext context, IRubyObject[] args) {
         try {
             InetSocketAddress sender = null;
             int length = RubyNumeric.fix2int(args[0]);
             ByteBuffer buf = ByteBuffer.allocate(length);
             byte[] buf2 = new byte[length];
             DatagramPacket recv = new DatagramPacket(buf2, buf2.length);
 
             if (this.multicastStateManager == null) {
                 ((DatagramChannel) this.getChannel()).configureBlocking(false);
                 context.getThread().select(this, SelectionKey.OP_READ);
                 sender = (InetSocketAddress) ((DatagramChannel) this.getChannel()).receive(buf);
             } else {
                 MulticastSocket ms = this.multicastStateManager.getMulticastSocket();
                 ms.receive(recv);
                 sender = (InetSocketAddress) recv.getSocketAddress();
             }
 
             // see JRUBY-4678
             if (sender == null) {
                 throw context.getRuntime().newErrnoECONNRESETError();
             }
 
             IRubyObject addressArray = context.getRuntime().newArray(new IRubyObject[]{
                 context.getRuntime().newString("AF_INET"),
                 context.getRuntime().newFixnum(sender.getPort()),
                 context.getRuntime().newString(sender.getHostName()),
                 context.getRuntime().newString(sender.getAddress().getHostAddress())
             });
 
             IRubyObject result = null;
 
             if (this.multicastStateManager == null) {
                 result = context.getRuntime().newString(new ByteList(buf.array(), 0, buf.position()));
             } else {
                 result = context.getRuntime().newString(new ByteList(recv.getData(), 0, recv.getLength()));
             }
 
             return context.getRuntime().newArray(new IRubyObject[]{result, addressArray});
         } catch (UnknownHostException e) {
             throw sockerr(context.getRuntime(), "recvfrom: name or service not known");
         } catch (PortUnreachableException e) {
             throw context.getRuntime().newErrnoECONNREFUSEDError();
         } catch (IOException e) {
             throw sockerr(context.getRuntime(), "recvfrom: name or service not known");
         }
     }
 
     @Override
     public IRubyObject recv(ThreadContext context, IRubyObject[] args) {
         try {
             int length = RubyNumeric.fix2int(args[0]);
             ByteBuffer buf = ByteBuffer.allocate(length);
             ((DatagramChannel) this.getChannel()).configureBlocking(false);
             context.getThread().select(this, SelectionKey.OP_READ);
             InetSocketAddress sender = (InetSocketAddress) ((DatagramChannel) this.getChannel()).receive(buf);
 
             // see JRUBY-4678
             if (sender == null) {
                 throw context.getRuntime().newErrnoECONNRESETError();
             }
 
             return context.getRuntime().newString(new ByteList(buf.array(), 0, buf.position()));
         } catch (IOException e) {
             throw sockerr(context.getRuntime(), "recv: name or service not known");
         }
     }
 
     @Deprecated
     public IRubyObject send(IRubyObject[] args) {
         return send(getRuntime().getCurrentContext(), args);
     }
 
     @JRubyMethod(required = 1, rest = true)
     public IRubyObject send(ThreadContext context, IRubyObject[] args) {
         try {
             int written;
             if (args.length >= 3) { // host and port given
                 RubyString nameStr = args[2].convertToString();
                 RubyString data = args[0].convertToString();
                 ByteBuffer buf = ByteBuffer.wrap(data.getBytes());
 
                 byte [] buf2 = data.getBytes();
                 DatagramPacket sendDP = null;
 
                 int port;
                 if (args[3] instanceof RubyString) {
                     jnr.netdb.Service service = jnr.netdb.Service.getServiceByName(args[3].asJavaString(), "udp");
                     if (service != null) {
                         port = service.getPort();
                     } else {
                         port = (int)args[3].convertToInteger("to_i").getLongValue();
                     }
                 } else {
                     port = (int)args[3].convertToInteger().getLongValue();
                 }
 
-                InetAddress address = RubySocket.getRubyInetAddress(nameStr.getByteList());
+                InetAddress address = SocketUtils.getRubyInetAddress(nameStr.getByteList());
                 InetSocketAddress addr =
                         new InetSocketAddress(address, port);
 
                 if (this.multicastStateManager == null) {
                     written = ((DatagramChannel) this.getChannel()).send(buf, addr);
                 }
                 else {
                     sendDP = new DatagramPacket(buf2, buf2.length, address, port);
                     MulticastSocket ms = this.multicastStateManager.getMulticastSocket();
                     ms.send(sendDP);
                     written = sendDP.getLength();
                 }
             } else {
                 RubyString data = args[0].convertToString();
                 ByteBuffer buf = ByteBuffer.wrap(data.getBytes());
                 written = ((DatagramChannel) this.getChannel()).write(buf);
             }
             return context.getRuntime().newFixnum(written);
         } catch (UnknownHostException e) {
             throw sockerr(context.getRuntime(), "send: name or service not known");
         } catch (IOException e) {
             throw sockerr(context.getRuntime(), "send: name or service not known");
         }
     }
     @Deprecated
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         return open(recv.getRuntime().getCurrentContext(), recv, args, block);
     }
     @JRubyMethod(rest = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyUDPSocket sock = (RubyUDPSocket) recv.callMethod(context, "new", args);
         if (!block.isGiven()) {
             return sock;
         }
 
         try {
             return block.yield(context, sock);
         } finally {
             if (sock.openFile.isOpen()) {
                 sock.close();
             }
         }
     }
 }// RubyUDPSocket
 
diff --git a/src/org/jruby/ext/socket/SocketLibrary.java b/src/org/jruby/ext/socket/SocketLibrary.java
index c4455ebdba..25ea6cfff5 100644
--- a/src/org/jruby/ext/socket/SocketLibrary.java
+++ b/src/org/jruby/ext/socket/SocketLibrary.java
@@ -1,29 +1,30 @@
 package org.jruby.ext.socket;
 
 import java.io.IOException;
 import org.jruby.Ruby;
 import org.jruby.runtime.load.Library;
 
 /**
  *
  * @author nicksieger
  */
 public class SocketLibrary implements Library {
     public void load(final Ruby runtime, boolean wrap) throws IOException {
         runtime.defineClass("SocketError", runtime.getStandardError(), runtime.getStandardError().getAllocator());
         RubyBasicSocket.createBasicSocket(runtime);
         RubySocket.createSocket(runtime);
+        RubyServerSocket.createServerSocket(runtime);
 
         if (runtime.getInstanceConfig().isNativeEnabled() && RubyUNIXSocket.tryUnixDomainSocket()) {
             RubyUNIXSocket.createUNIXSocket(runtime);
             RubyUNIXServer.createUNIXServer(runtime);
         }
 
         RubyIPSocket.createIPSocket(runtime);
         RubyTCPSocket.createTCPSocket(runtime);
         RubyTCPServer.createTCPServer(runtime);
         RubyUDPSocket.createUDPSocket(runtime);
 
         if (runtime.is1_9()) Addrinfo.createAddrinfo(runtime);
     }
 }
diff --git a/src/org/jruby/ext/socket/SocketUtils.java b/src/org/jruby/ext/socket/SocketUtils.java
new file mode 100644
index 0000000000..edd35c297d
--- /dev/null
+++ b/src/org/jruby/ext/socket/SocketUtils.java
@@ -0,0 +1,400 @@
+/***** BEGIN LICENSE BLOCK *****
+ * Version: CPL 1.0/GPL 2.0/LGPL 2.1
+ *
+ * The contents of this file are subject to the Common Public
+ * License Version 1.0 (the "License"); you may not use this file
+ * except in compliance with the License. You may obtain a copy of
+ * the License at http://www.eclipse.org/legal/cpl-v10.html
+ *
+ * Software distributed under the License is distributed on an "AS
+ * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
+ * implied. See the License for the specific language governing
+ * rights and limitations under the License.
+ *
+ * Alternatively, the contents of this file may be used under the terms of
+ * either of the GNU General Public License Version 2 or later (the "GPL"),
+ * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
+ * in which case the provisions of the GPL or the LGPL are applicable instead
+ * of those above. If you wish to allow use of your version of this file only
+ * under the terms of either the GPL or the LGPL, and not to allow others to
+ * use your version of this file under the terms of the CPL, indicate your
+ * decision by deleting the provisions above and replace them with the notice
+ * and other provisions required by the GPL or the LGPL. If you do not delete
+ * the provisions above, a recipient may use your version of this file under
+ * the terms of any one of the CPL, the GPL or the LGPL.
+ ***** END LICENSE BLOCK *****/
+
+package org.jruby.ext.socket;
+
+import jnr.netdb.Service;
+import org.jruby.Ruby;
+import org.jruby.RubyArray;
+import org.jruby.RubyFixnum;
+import org.jruby.RubyNumeric;
+import org.jruby.RubyString;
+import org.jruby.anno.JRubyMethod;
+import org.jruby.runtime.ThreadContext;
+import org.jruby.runtime.builtin.IRubyObject;
+import org.jruby.util.ByteList;
+import org.jruby.util.io.Sockaddr;
+
+import java.net.Inet6Address;
+import java.net.InetAddress;
+import java.net.UnknownHostException;
+import java.util.ArrayList;
+import java.util.List;
+import java.util.regex.Matcher;
+import java.util.regex.Pattern;
+
+import static jnr.constants.platform.AddressFamily.AF_INET6;
+import static jnr.constants.platform.IPProto.IPPROTO_TCP;
+import static jnr.constants.platform.IPProto.IPPROTO_UDP;
+import static jnr.constants.platform.NameInfo.NI_NUMERICHOST;
+import static jnr.constants.platform.NameInfo.NI_NUMERICSERV;
+import static jnr.constants.platform.ProtocolFamily.PF_INET;
+import static jnr.constants.platform.ProtocolFamily.PF_INET6;
+import static jnr.constants.platform.Sock.SOCK_DGRAM;
+import static jnr.constants.platform.Sock.SOCK_STREAM;
+
+/**
+ * Socket class methods for addresses, structures, and so on.
+ */
+public class SocketUtils {
+    @JRubyMethod(meta = true)
+    public static IRubyObject gethostname(ThreadContext context, IRubyObject recv) {
+        Ruby runtime = context.runtime;
+
+        try {
+            return runtime.newString(InetAddress.getLocalHost().getHostName());
+
+        } catch(UnknownHostException e) {
+
+            try {
+                return runtime.newString(InetAddress.getByAddress(new byte[]{0,0,0,0}).getHostName());
+
+            } catch(UnknownHostException e2) {
+                throw RubySocket.sockerr(runtime, "gethostname: name or service not known");
+
+            }
+        }
+    }
+
+    @JRubyMethod(required = 1, rest = true, meta = true)
+    public static IRubyObject gethostbyaddr(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+        Ruby runtime = context.runtime;
+        IRubyObject[] ret = new IRubyObject[4];
+
+        ret[0] = runtime.newString(Sockaddr.addressFromString(runtime, args[0].convertToString().toString()).getCanonicalHostName());
+        ret[1] = runtime.newArray();
+        ret[2] = runtime.newFixnum(2); // AF_INET
+        ret[3] = args[0];
+
+        return runtime.newArrayNoCopy(ret);
+    }
+
+    @JRubyMethod(required = 1, optional = 1, meta = true)
+    public static IRubyObject getservbyname(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+        Ruby runtime = context.getRuntime();
+        String name = args[0].convertToString().toString();
+        String proto = args.length ==  1 ? "tcp" : args[1].convertToString().toString();
+        Service service = Service.getServiceByName(name, proto);
+        int port;
+
+        if (service != null) {
+            port = service.getPort();
+
+        } else {
+
+            // MRI behavior: try to convert the name string to port directly
+            try {
+                port = Integer.parseInt(name.trim());
+
+            } catch (NumberFormatException nfe) {
+                throw RubySocket.sockerr(runtime, "no such service " + name + "/" + proto);
+
+            }
+
+        }
+
+        return runtime.newFixnum(port);
+    }
+
+    @JRubyMethod(name = {"pack_sockaddr_in", "sockaddr_in"}, meta = true)
+    public static IRubyObject pack_sockaddr_in(ThreadContext context, IRubyObject recv, IRubyObject port, IRubyObject host) {
+        int portNum = port instanceof RubyString ?
+                Integer.parseInt(port.convertToString().toString()) :
+                RubyNumeric.fix2int(port);
+
+        return Sockaddr.pack_sockaddr_in(
+                context,
+                portNum,
+                host.isNil() ? null : host.convertToString().toString());
+    }
+
+    @JRubyMethod(meta = true)
+    public static IRubyObject unpack_sockaddr_in(ThreadContext context, IRubyObject recv, IRubyObject addr) {
+        return Sockaddr.unpack_sockaddr_in(context, addr);
+    }
+
+    @JRubyMethod(name = {"pack_sockaddr_un", "sockaddr_un"}, meta = true)
+    public static IRubyObject pack_sockaddr_un(ThreadContext context, IRubyObject recv, IRubyObject filename) {
+        String str = filename.convertToString().toString();
+
+        StringBuilder sb = new StringBuilder()
+                .append((char)0)
+                .append((char) 1)
+                .append(str);
+
+        for(int i=str.length();i<104;i++) {
+            sb.append((char)0);
+        }
+
+        return context.runtime.newString(sb.toString());
+    }
+
+    @JRubyMethod(meta = true)
+    public static IRubyObject gethostbyname(ThreadContext context, IRubyObject recv, IRubyObject hostname) {
+        Ruby runtime = context.runtime;
+
+        try {
+            InetAddress addr = getRubyInetAddress(hostname.convertToString().getByteList());
+            IRubyObject[] ret = new IRubyObject[4];
+
+            ret[0] = runtime.newString(addr.getCanonicalHostName());
+            ret[1] = runtime.newArray();
+            ret[2] = runtime.newFixnum(2); // AF_INET
+            ret[3] = runtime.newString(new ByteList(addr.getAddress()));
+            return runtime.newArrayNoCopy(ret);
+
+        } catch(UnknownHostException e) {
+            throw RubySocket.sockerr(runtime, "gethostbyname: name or service not known");
+
+        }
+    }
+
+    public static InetAddress getRubyInetAddress(ByteList address) throws UnknownHostException {
+        if (address.equal(BROADCAST)) {
+            return InetAddress.getByAddress(INADDR_BROADCAST);
+
+        } else if (address.equal(ANY)) {
+            return InetAddress.getByAddress(INADDR_ANY);
+
+        } else {
+            return InetAddress.getByName(address.toString());
+
+        }
+    }
+
+    /**
+     * Ruby definition would look like:
+     *
+     * def self.getaddrinfo(host, port, family = nil, socktype = nil, protocol = nil, flags = nil)
+     */
+    @JRubyMethod(required = 2, optional = 4, meta = true)
+    public static IRubyObject getaddrinfo(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+        Ruby runtime = context.runtime;
+        IRubyObject host = args[0];
+        IRubyObject port = args[1];
+        boolean emptyHost = host.isNil() || host.convertToString().isEmpty();
+
+        try {
+            if(port instanceof RubyString) {
+                port = getservbyname(context, recv, new IRubyObject[]{port});
+            }
+
+            IRubyObject family = args.length > 2 ? args[2] : context.nil;
+            IRubyObject socktype = args.length > 3 ? args[3] : context.nil;
+            //IRubyObject protocol = args[4];
+            IRubyObject flags = args.length > 5 ? args[5] : context.nil;
+
+            boolean is_ipv6 = (! family.isNil()) && (RubyNumeric.fix2int(family) & AF_INET6.intValue()) == AF_INET6.intValue();
+            boolean sock_stream = true;
+            boolean sock_dgram = true;
+
+            if(!socktype.isNil()) {
+                int val = RubyNumeric.fix2int(socktype);
+
+                if(val == SOCK_STREAM.intValue()) {
+                    sock_dgram = false;
+
+                } else if(val == SOCK_DGRAM.intValue()) {
+                    sock_stream = false;
+
+                }
+            }
+
+            // When Socket::AI_PASSIVE and host is nil, return 'any' address.
+            InetAddress[] addrs = null;
+
+            if(!flags.isNil() && RubyFixnum.fix2int(flags) > 0) {
+                // The value of 1 is for Socket::AI_PASSIVE.
+                int flag = RubyNumeric.fix2int(flags);
+
+                if ((flag == 1) && emptyHost ) {
+                    // use RFC 2732 style string to ensure that we get Inet6Address
+                    addrs = InetAddress.getAllByName(is_ipv6 ? "[::]" : "0.0.0.0");
+                }
+
+            }
+
+            if (addrs == null) {
+                addrs = InetAddress.getAllByName(emptyHost ? (is_ipv6 ? "[::1]" : null) : host.convertToString().toString());
+            }
+
+            List<IRubyObject> l = new ArrayList<IRubyObject>();
+
+            for(int i = 0; i < addrs.length; i++) {
+                IRubyObject[] c;
+
+                if(sock_dgram) {
+                    c = new IRubyObject[7];
+                    c[0] = runtime.newString(is_ipv6 ? "AF_INET6" : "AF_INET");
+                    c[1] = port;
+                    c[2] = runtime.newString(getHostAddress(recv, addrs[i]));
+                    c[3] = runtime.newString(addrs[i].getHostAddress());
+                    c[4] = runtime.newFixnum(is_ipv6 ? PF_INET6 : PF_INET);
+                    c[5] = runtime.newFixnum(SOCK_DGRAM);
+                    c[6] = runtime.newFixnum(IPPROTO_UDP);
+                    l.add(runtime.newArrayNoCopy(c));
+                }
+
+                if(sock_stream) {
+                    c = new IRubyObject[7];
+                    c[0] = runtime.newString(is_ipv6 ? "AF_INET6" : "AF_INET");
+                    c[1] = port;
+                    c[2] = runtime.newString(getHostAddress(recv, addrs[i]));
+                    c[3] = runtime.newString(addrs[i].getHostAddress());
+                    c[4] = runtime.newFixnum(is_ipv6 ? PF_INET6 : PF_INET);
+                    c[5] = runtime.newFixnum(SOCK_STREAM);
+                    c[6] = runtime.newFixnum(IPPROTO_TCP);
+                    l.add(runtime.newArrayNoCopy(c));
+                }
+            }
+
+            return runtime.newArray(l);
+
+        } catch(UnknownHostException e) {
+            throw RubySocket.sockerr(runtime, "getaddrinfo: name or service not known");
+
+        }
+    }
+
+    private static String getHostAddress(IRubyObject recv, InetAddress addr) {
+        return RubyBasicSocket.do_not_reverse_lookup(recv).isTrue() ? addr.getHostAddress() : addr.getCanonicalHostName();
+    }
+
+    @JRubyMethod(required = 1, optional = 1, meta = true)
+    public static IRubyObject getnameinfo(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+        Ruby runtime = context.runtime;
+        int flags = args.length == 2 ? RubyNumeric.num2int(args[1]) : 0;
+        IRubyObject arg0 = args[0];
+        String host, port;
+
+        if (arg0 instanceof RubyArray) {
+            List list = ((RubyArray)arg0).getList();
+            int len = list.size();
+
+            if (len < 3 || len > 4) {
+                throw runtime.newArgumentError("array size should be 3 or 4, "+len+" given");
+            }
+
+            // if array has 4 elements, third element is ignored
+            host = list.size() == 3 ? list.get(2).toString() : list.get(3).toString();
+            port = list.get(1).toString();
+
+        } else if (arg0 instanceof RubyString) {
+            String arg = ((RubyString)arg0).toString();
+            Matcher m = STRING_IPV4_ADDRESS_PATTERN.matcher(arg);
+
+            if (!m.matches()) {
+                IRubyObject obj = unpack_sockaddr_in(context, recv, arg0);
+
+                if (obj instanceof RubyArray) {
+                    List list = ((RubyArray)obj).getList();
+                    int len = list.size();
+
+                    if (len != 2) {
+                        throw runtime.newArgumentError("invalid address representation");
+                    }
+
+                    host = list.get(1).toString();
+                    port = list.get(0).toString();
+
+                } else {
+                    throw runtime.newArgumentError("invalid address string");
+
+                }
+
+            } else if ((host = m.group(IPV4_HOST_GROUP)) == null || host.length() == 0 ||
+                    (port = m.group(IPV4_PORT_GROUP)) == null || port.length() == 0) {
+
+                throw runtime.newArgumentError("invalid address string");
+
+            } else {
+
+                // Try IPv6
+                try {
+                    InetAddress ipv6_addr = InetAddress.getByName(host);
+
+                    if (ipv6_addr instanceof Inet6Address) {
+                        host = ipv6_addr.getHostAddress();
+                    }
+
+                } catch (UnknownHostException uhe) {
+                    throw runtime.newArgumentError("invalid address string");
+
+                }
+            }
+
+        } else {
+            throw runtime.newArgumentError("invalid args");
+
+        }
+
+        InetAddress addr;
+
+        try {
+            addr = InetAddress.getByName(host);
+
+        } catch (UnknownHostException e) {
+            throw RubySocket.sockerr(runtime, "unknown host: "+ host);
+
+        }
+
+        if ((flags & NI_NUMERICHOST.intValue()) == 0) {
+            host = addr.getCanonicalHostName();
+
+        } else {
+            host = addr.getHostAddress();
+
+        }
+
+        jnr.netdb.Service serv = jnr.netdb.Service.getServiceByPort(Integer.parseInt(port), null);
+
+        if (serv != null) {
+
+            if ((flags & NI_NUMERICSERV.intValue()) == 0) {
+                port = serv.getName();
+
+            } else {
+                port = Integer.toString(serv.getPort());
+
+            }
+
+        }
+
+        return runtime.newArray(runtime.newString(host), runtime.newString(port));
+
+    }
+
+    private static final Pattern STRING_IPV4_ADDRESS_PATTERN =
+            Pattern.compile("((.*)\\/)?([\\.0-9]+)(:([0-9]+))?");
+
+    private static final int IPV4_HOST_GROUP = 3;
+    private static final int IPV4_PORT_GROUP = 5;
+
+    private static final ByteList BROADCAST = new ByteList("<broadcast>".getBytes());
+    private static final byte[] INADDR_BROADCAST = new byte[] {-1,-1,-1,-1}; // 255.255.255.255
+    private static final ByteList ANY = new ByteList("<any>".getBytes());
+    private static final byte[] INADDR_ANY = new byte[] {0,0,0,0}; // 0.0.0.0
+}
