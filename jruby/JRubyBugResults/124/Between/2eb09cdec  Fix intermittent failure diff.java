diff --git a/src/org/jruby/ext/socket/RubyBasicSocket.java b/src/org/jruby/ext/socket/RubyBasicSocket.java
index 36f44ddcb0..547584702a 100644
--- a/src/org/jruby/ext/socket/RubyBasicSocket.java
+++ b/src/org/jruby/ext/socket/RubyBasicSocket.java
@@ -1,740 +1,740 @@
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
 
 import static com.kenai.constantine.platform.IPProto.IPPROTO_TCP;
 import static com.kenai.constantine.platform.IPProto.IPPROTO_IP;
 import static com.kenai.constantine.platform.Sock.SOCK_DGRAM;
 import static com.kenai.constantine.platform.Sock.SOCK_STREAM;
 import static com.kenai.constantine.platform.TCP.TCP_NODELAY;
 
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
 
 import com.kenai.constantine.platform.SocketLevel;
 import com.kenai.constantine.platform.SocketOption;
 
 
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
             openFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, new ModeFlags(ModeFlags.RDONLY)));
             openFile.setPipeStream(ChannelStream.fdopen(runtime, descriptor, new ModeFlags(ModeFlags.WRONLY)));
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
             shutdownInternal(context, 1);
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
             shutdownInternal(context, 0);
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
             return RubyString.newString(context.getRuntime(), openFile.getMainStream().read(RubyNumeric.fix2int(args[0])));
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
 
     protected InetSocketAddress getLocalSocket(String caller) {
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
     
     protected InetSocketAddress getRemoteSocket() {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             return (InetSocketAddress)((SocketChannel)socketChannel).socket().getRemoteSocketAddress();
         } else {
             return null;
         }
     }
 
     private Socket asSocket() {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof SocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((SocketChannel)socketChannel).socket();
     }
 
     private ServerSocket asServerSocket() {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof ServerSocketChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((ServerSocketChannel)socketChannel).socket();
     }
 
     private DatagramSocket asDatagramSocket() {
         Channel socketChannel = getOpenChannel();
         if(!(socketChannel instanceof DatagramChannel)) {
             throw getRuntime().newErrnoENOPROTOOPTError();
         }
 
         return ((DatagramChannel)socketChannel).socket();
     }
 
     private IRubyObject getBroadcast(Ruby runtime) throws IOException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime, (socketChannel instanceof DatagramChannel) ? asDatagramSocket().getBroadcast() : false);
     }
 
     private void setBroadcast(IRubyObject val) throws IOException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setBroadcast(asBoolean(val));
         }
     }
 
     private void setKeepAlive(IRubyObject val) throws IOException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setKeepAlive(asBoolean(val));
         }
     }
 
     private void setTcpNoDelay(IRubyObject val) throws IOException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setTcpNoDelay(asBoolean(val));
         }
     }
 
     private void joinMulticastGroup(IRubyObject val) throws IOException {
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
 
     private void setReuseAddr(IRubyObject val) throws IOException {
         Channel socketChannel = getOpenChannel();
         if (socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReuseAddress(asBoolean(val));
         } else if (socketChannel instanceof SocketChannel) {
             asSocket().setReuseAddress(asBoolean(val));
         } else if (socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReuseAddress(asBoolean(val));
         }
     }
 
     private void setRcvBuf(IRubyObject val) throws IOException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setReceiveBufferSize(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setReceiveBufferSize(asNumber(val));
         }
     }
 
     private void setTimeout(IRubyObject val) throws IOException {
         Channel socketChannel = getOpenChannel();
         if(socketChannel instanceof SocketChannel) {
             asSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof ServerSocketChannel) {
             asServerSocket().setSoTimeout(asNumber(val));
         } else if(socketChannel instanceof DatagramChannel) {
             asDatagramSocket().setSoTimeout(asNumber(val));
         }
     }
 
     private void setSndBuf(IRubyObject val) throws IOException {
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
 
     private void setLinger(IRubyObject val) throws IOException {
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
 
     private void setOOBInline(IRubyObject val) throws IOException {
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
 
     private IRubyObject getKeepAlive(Ruby runtime) throws IOException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime,
                          (socketChannel instanceof SocketChannel) ? asSocket().getKeepAlive() : false
                          );
     }
 
     private IRubyObject getLinger(Ruby runtime) throws IOException {
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
 
     private IRubyObject getOOBInline(Ruby runtime) throws IOException {
         Channel socketChannel = getOpenChannel();
         return trueFalse(runtime,
                          (socketChannel instanceof SocketChannel) ? asSocket().getOOBInline() : false
                          );
     }
 
     private IRubyObject getRcvBuf(Ruby runtime) throws IOException {
         Channel socketChannel = getOpenChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getReceiveBufferSize() : 
                       ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getReceiveBufferSize() : 
                        asDatagramSocket().getReceiveBufferSize())
                       );
     }
 
     private IRubyObject getSndBuf(Ruby runtime) throws IOException {
         Channel socketChannel = getOpenChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getSendBufferSize() : 
                       ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSendBufferSize() : 0)
                       );
     }
 
     private IRubyObject getReuseAddr(Ruby runtime) throws IOException {
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
 
     private IRubyObject getTimeout(Ruby runtime) throws IOException {
         Channel socketChannel = getOpenChannel();
         return number(runtime,
                       (socketChannel instanceof SocketChannel) ? asSocket().getSoTimeout() : 
                       ((socketChannel instanceof ServerSocketChannel) ? asServerSocket().getSoTimeout() : 
                        ((socketChannel instanceof DatagramChannel) ? asDatagramSocket().getSoTimeout() : 0))
                       );
     }
 
     protected int getSoTypeDefault() {
         return 0;
     }
     private int getChannelSoType(Channel channel) {
         if (channel instanceof SocketChannel || channel instanceof ServerSocketChannel) {
             return SOCK_STREAM.value();
         } else if (channel instanceof DatagramChannel) {
             return SOCK_DGRAM.value();
         } else {
             return getSoTypeDefault();
         }
     }
     private IRubyObject getSoType(Ruby runtime) throws IOException {
         Channel socketChannel = getOpenChannel();
         return number(runtime, getChannelSoType(socketChannel));
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
                     if (IPPROTO_TCP.value() == level && TCP_NODELAY.value() == opt) {
                         setTcpNoDelay(val);
                     }
                     else if (IPPROTO_IP.value() == level) {
                         if (MulticastStateManager.IP_ADD_MEMBERSHIP == opt) {
                             joinMulticastGroup(val);
                         }
                     } else {
                         throw context.getRuntime().newErrnoENOPROTOOPTError();
                     }
                 }
                 break;
             default:
                 if (IPPROTO_TCP.value() == level && TCP_NODELAY.value() == opt) {
                     setTcpNoDelay(val);
                 }
                 else if (IPPROTO_IP.value() == level) {
                     if (MulticastStateManager.IP_ADD_MEMBERSHIP == opt) {
                         joinMulticastGroup(val);
                     }
                 } else {
                     throw context.getRuntime().newErrnoENOPROTOOPTError();
                 }
             }
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
         InetSocketAddress sock = getLocalSocket(caller);
         if(null == sock) {
             return RubySocket.pack_sockaddr_in(context, null, 0, "0.0.0.0");
         } else {
-            return RubySocket.pack_sockaddr_in(context, null, sock.getPort(), sock.getAddress().getHostAddress());
+            return RubySocket.pack_sockaddr_in(context, sock);
         }
     }
 
     @Deprecated
     public IRubyObject getpeername() {
         return getpeername(getRuntime().getCurrentContext());
     }
     @JRubyMethod(name = {"getpeername", "__getpeername"})
     public IRubyObject getpeername(ThreadContext context) {
         SocketAddress sock = getRemoteSocket();
         if(null == sock) {
             throw context.getRuntime().newIOError("Not Supported");
         }
         return context.getRuntime().newString(sock.toString());
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
         return shutdownInternal(context, how);
     }
 
     private IRubyObject shutdownInternal(ThreadContext context, int how) {
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
     
     private Channel getOpenChannel() {
         return getOpenFileChecked().getMainStream().getDescriptor().getChannel();
     }
 }// RubyBasicSocket
diff --git a/src/org/jruby/ext/socket/RubyIPSocket.java b/src/org/jruby/ext/socket/RubyIPSocket.java
index bbac1d03a8..0a280d55dc 100644
--- a/src/org/jruby/ext/socket/RubyIPSocket.java
+++ b/src/org/jruby/ext/socket/RubyIPSocket.java
@@ -1,175 +1,169 @@
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
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="IPSocket", parent="BasicSocket")
 public class RubyIPSocket extends RubyBasicSocket {
     static void createIPSocket(Ruby runtime) {
         RubyClass rb_cIPSocket = runtime.defineClass("IPSocket", runtime.fastGetClass("BasicSocket"), IPSOCKET_ALLOCATOR);
         
         rb_cIPSocket.defineAnnotatedMethods(RubyIPSocket.class);
 
         runtime.getObject().fastSetConstant("IPsocket",rb_cIPSocket);
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
         return new RaiseException(runtime, runtime.fastGetClass("SocketError"), msg, true);
     }
 
+    public IRubyObject packSockaddrFromAddress(InetSocketAddress sock, ThreadContext context) {
+        if (sock == null) {
+            return RubySocket.pack_sockaddr_in(context, this, 0, "");
+        } else {
+            return RubySocket.pack_sockaddr_in(context, sock);
+        }
+    }
+
     private IRubyObject addrFor(ThreadContext context, InetSocketAddress addr) {
         Ruby r = context.getRuntime();
         IRubyObject[] ret = new IRubyObject[4];
         ret[0] = r.newString("AF_INET");
         ret[1] = r.newFixnum(addr.getPort());
         String hostAddress = addr.getAddress().getHostAddress();
         if (doNotReverseLookup(context)) {
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
         InetSocketAddress address = getLocalSocket("addr");
         if (address == null) {
             throw context.getRuntime().newErrnoENOTSOCKError("Not socket or not connected");
         }
         return addrFor(context, address);
     }
     @Deprecated
     public IRubyObject peeraddr() {
         return peeraddr(getRuntime().getCurrentContext());
     }
     @JRubyMethod
     public IRubyObject peeraddr(ThreadContext context) {
         InetSocketAddress address = getRemoteSocket();
         if (address == null) {
             throw context.getRuntime().newErrnoENOTSOCKError("Not socket or not connected");
         }
         return addrFor(context, address);
     }
     @Override
     protected IRubyObject getSocknameCommon(ThreadContext context, String caller) {
         InetSocketAddress sock = getLocalSocket(caller);
-        if (sock == null) {
-            return RubySocket.pack_sockaddr_in(context, this, 0, "");
-        } else {
-            String addr = sock.getAddress().getHostAddress();
-            return RubySocket.pack_sockaddr_in(context, this,
-                               sock.getPort(),
-                               addr);
-        }
+        return packSockaddrFromAddress(sock, context);
     }
     @Override
     public IRubyObject getpeername(ThreadContext context) {
         InetSocketAddress sock = getRemoteSocket();
-        if (sock == null) {
-            return RubySocket.pack_sockaddr_in(context, this, 0, "");
-	} else {
-	    String addr = sock.getAddress().getHostAddress();
-	    return RubySocket.pack_sockaddr_in(context, this,
-					       sock.getPort(),
-					       addr);
-	}
+        return packSockaddrFromAddress(sock, context);
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
     }
 
 }// RubyIPSocket
diff --git a/src/org/jruby/ext/socket/RubySocket.java b/src/org/jruby/ext/socket/RubySocket.java
index 0c11d8c983..ddd01109ad 100644
--- a/src/org/jruby/ext/socket/RubySocket.java
+++ b/src/org/jruby/ext/socket/RubySocket.java
@@ -1,787 +1,828 @@
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
 
 import static com.kenai.constantine.platform.AddressFamily.AF_INET;
 import static com.kenai.constantine.platform.AddressFamily.AF_INET6;
 import static com.kenai.constantine.platform.IPProto.IPPROTO_TCP;
 import static com.kenai.constantine.platform.IPProto.IPPROTO_UDP;
 import static com.kenai.constantine.platform.NameInfo.NI_NUMERICHOST;
 import static com.kenai.constantine.platform.NameInfo.NI_NUMERICSERV;
 import static com.kenai.constantine.platform.ProtocolFamily.PF_INET;
 import static com.kenai.constantine.platform.ProtocolFamily.PF_INET6;
 import static com.kenai.constantine.platform.Sock.SOCK_DGRAM;
 import static com.kenai.constantine.platform.Sock.SOCK_STREAM;
 
 import java.io.ByteArrayOutputStream;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.net.DatagramSocket;
 import java.net.InetAddress;
 import java.net.InetSocketAddress;
 import java.net.Socket;
 import java.net.SocketException;
 import java.net.UnknownHostException;
 import java.nio.channels.Channel;
 import java.nio.channels.DatagramChannel;
 import java.nio.channels.SocketChannel;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyString;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.ChannelDescriptor;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.ModeFlags;
 
 import com.kenai.constantine.platform.AddressFamily;
 import com.kenai.constantine.platform.Sock;
 import java.net.Inet6Address;
 import java.nio.channels.AlreadyConnectedException;
 import java.nio.channels.ClosedChannelException;
 import java.nio.channels.ConnectionPendingException;
 import java.nio.channels.spi.AbstractSelectableChannel;
+import java.util.Arrays;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="Socket", parent="BasicSocket", include="Socket::Constants")
 public class RubySocket extends RubyBasicSocket {
     @JRubyClass(name="SocketError", parent="StandardError")
     public static class SocketError {}
 
     private static ObjectAllocator SOCKET_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubySocket(runtime, klass);
         }
     };
 
     public static final int MSG_OOB = 0x1;
     public static final int MSG_PEEK = 0x2;
     public static final int MSG_DONTROUTE = 0x4;
 
     @JRubyModule(name="Socket::Constants")
     public static class Constants {}
 
     static void createSocket(Ruby runtime) {
         RubyClass rb_cSocket = runtime.defineClass("Socket", runtime.fastGetClass("BasicSocket"), SOCKET_ALLOCATOR);
 
         RubyModule rb_mConstants = rb_cSocket.defineModuleUnder("Constants");
         // we don't have to define any that we don't support; see socket.c
 
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.Sock.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.SocketOption.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.SocketLevel.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.ProtocolFamily.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.AddressFamily.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.INAddr.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.IPProto.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.Shutdown.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.TCP.class);
         runtime.loadConstantSet(rb_mConstants, com.kenai.constantine.platform.NameInfo.class);
 
         // mandatory constants we haven't implemented
         rb_mConstants.fastSetConstant("MSG_OOB", runtime.newFixnum(MSG_OOB));
         rb_mConstants.fastSetConstant("MSG_PEEK", runtime.newFixnum(MSG_PEEK));
         rb_mConstants.fastSetConstant("MSG_DONTROUTE", runtime.newFixnum(MSG_DONTROUTE));
 
         // constants webrick crashes without
         rb_mConstants.fastSetConstant("AI_PASSIVE", runtime.newFixnum(1));
 
         // More constants needed by specs
         rb_mConstants.fastSetConstant("IP_MULTICAST_TTL", runtime.newFixnum(10));
         rb_mConstants.fastSetConstant("IP_MULTICAST_LOOP", runtime.newFixnum(11));
         rb_mConstants.fastSetConstant("IP_ADD_MEMBERSHIP", runtime.newFixnum(12));
         rb_mConstants.fastSetConstant("IP_MAX_MEMBERSHIPS", runtime.newFixnum(20));
         rb_mConstants.fastSetConstant("IP_DEFAULT_MULTICAST_LOOP", runtime.newFixnum(1));
         rb_mConstants.fastSetConstant("IP_DEFAULT_MULTICAST_TTL", runtime.newFixnum(1));
 
         rb_cSocket.includeModule(rb_mConstants);
 
         rb_cSocket.defineAnnotatedMethods(RubySocket.class);
     }
 
     public RubySocket(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     protected int getSoTypeDefault() {
         return soType;
     }
 
     private int soDomain;
     private int soType;
     private int soProtocol;
     @Deprecated
     public static IRubyObject for_fd(IRubyObject socketClass, IRubyObject fd) {
         return for_fd(socketClass.getRuntime().getCurrentContext(), socketClass, fd);
     }
     @JRubyMethod(meta = true)
     public static IRubyObject for_fd(ThreadContext context, IRubyObject socketClass, IRubyObject fd) {
         Ruby ruby = context.getRuntime();
         if (fd instanceof RubyFixnum) {
             RubySocket socket = (RubySocket)((RubyClass)socketClass).allocate();
 
             // normal file descriptor..try to work with it
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno((int)((RubyFixnum)fd).getLongValue());
 
             if (descriptor == null) {
                 throw ruby.newErrnoEBADFError();
             }
 
             Channel mainChannel = descriptor.getChannel();
 
             if (mainChannel instanceof SocketChannel) {
                 // ok, it's a socket...set values accordingly
                 // just using AF_INET since we can't tell from SocketChannel...
                 socket.soDomain = AddressFamily.AF_INET.value();
                 socket.soType = Sock.SOCK_STREAM.value();
                 socket.soProtocol = 0;
             } else if (mainChannel instanceof DatagramChannel) {
                 // datagram, set accordingly
                 // again, AF_INET
                 socket.soDomain = AddressFamily.AF_INET.value();
                 socket.soType = Sock.SOCK_DGRAM.value();
                 socket.soProtocol = 0;
             } else {
                 throw context.getRuntime().newErrnoENOTSOCKError("can't Socket.new/for_fd against a non-socket");
             }
 
             socket.initSocket(ruby, descriptor);
 
             return socket;
         } else {
             throw context.getRuntime().newTypeError(fd, context.getRuntime().getFixnum());
         }
     }
 
     @JRubyMethod
     public IRubyObject initialize(ThreadContext context, IRubyObject domain, IRubyObject type, IRubyObject protocol) {
         try {
             if(domain instanceof RubyString) {
                 String domainString = domain.toString();
                 if(domainString.equals("AF_INET")) {
                     soDomain = AF_INET.value();
                 } else if(domainString.equals("PF_INET")) {
                     soDomain = PF_INET.value();
                 } else {
                     throw sockerr(context.getRuntime(), "unknown socket domain " + domainString);
                 }
             } else {
                 soDomain = RubyNumeric.fix2int(domain);
             }
 
             if(type instanceof RubyString) {
                 String typeString = type.toString();
                 if(typeString.equals("SOCK_STREAM")) {
                     soType = SOCK_STREAM.value();
                 } else if(typeString.equals("SOCK_DGRAM")) {
                     soType = SOCK_DGRAM.value();
                 } else {
                     throw sockerr(context.getRuntime(), "unknown socket type " + typeString);
                 }
             } else {
                 soType = RubyNumeric.fix2int(type);
             }
 
             soProtocol = RubyNumeric.fix2int(protocol);
 
             Channel channel = null;
             if(soType == Sock.SOCK_STREAM.value()) {
                 channel = SocketChannel.open();
             } else if(soType == Sock.SOCK_DGRAM.value()) {
                 channel = DatagramChannel.open();
             }
 
             initSocket(context.getRuntime(), new ChannelDescriptor(channel, new ModeFlags(ModeFlags.RDWR)));
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch(IOException e) {
             throw sockerr(context.getRuntime(), "initialize: " + e.toString());
         }
 
         return this;
     }
 
     private static RuntimeException sockerr(Ruby runtime, String msg) {
         return new RaiseException(runtime, runtime.fastGetClass("SocketError"), msg, true);
     }
 
     @Deprecated
     public static IRubyObject gethostname(IRubyObject recv) {
         return gethostname(recv.getRuntime().getCurrentContext(), recv);
     }
     @JRubyMethod(meta = true)
     public static IRubyObject gethostname(ThreadContext context, IRubyObject recv) {
         try {
             return context.getRuntime().newString(InetAddress.getLocalHost().getHostName());
         } catch(UnknownHostException e) {
             try {
                 return context.getRuntime().newString(InetAddress.getByAddress(new byte[]{0,0,0,0}).getHostName());
             } catch(UnknownHostException e2) {
                 throw sockerr(context.getRuntime(), "gethostname: name or service not known");
             }
         }
     }
 
     private static InetAddress intoAddress(Ruby runtime, String s) {
         try {
             byte[] bs = ByteList.plain(s);
             return InetAddress.getByAddress(bs);
         } catch(Exception e) {
             throw sockerr(runtime, "strtoaddr: " + e.toString());
         }
     }
 
     private static String intoString(Ruby runtime, InetAddress as) {
         try {
             return new String(ByteList.plain(as.getAddress()));
         } catch(Exception e) {
             throw sockerr(runtime, "addrtostr: " + e.toString());
         }
     }
     @Deprecated
     public static IRubyObject gethostbyaddr(IRubyObject recv, IRubyObject[] args) {
         return gethostbyaddr(recv.getRuntime().getCurrentContext(), recv, args);
     }
     @JRubyMethod(required = 1, rest = true, meta = true)
     public static IRubyObject gethostbyaddr(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject[] ret = new IRubyObject[4];
         ret[0] = runtime.newString(intoAddress(runtime,args[0].convertToString().toString()).getCanonicalHostName());
         ret[1] = runtime.newArray();
         ret[2] = runtime.newFixnum(2); // AF_INET
         ret[3] = args[0];
         return runtime.newArrayNoCopy(ret);
     }
 
     @Deprecated
     public static IRubyObject getservbyname(IRubyObject recv, IRubyObject[] args) {
         return getservbyname(recv.getRuntime().getCurrentContext(), recv, args);
     }
 
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject getservbyname(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int argc = Arity.checkArgumentCount(runtime, args, 1, 2);
         String name = args[0].convertToString().toString();
         String proto = argc == 1 ? "tcp" : args[1].convertToString().toString();
 
         jnr.netdb.Service service = jnr.netdb.Service.getServiceByName(name, proto);
 
         int port;
         if (service != null) {
             port = service.getPort();
         } else {
             // MRI behavior: try to convert the name string to port directly
             try {
                 port = Integer.parseInt(name.trim());
             } catch (NumberFormatException nfe) {
                 throw sockerr(runtime, "no such service " + name + "/" + proto);
             }
         }
 
         return runtime.newFixnum(port);
     }
 
     @Deprecated
     public static IRubyObject pack_sockaddr_un(IRubyObject recv, IRubyObject filename) {
         return pack_sockaddr_un(recv.getRuntime().getCurrentContext(), recv, filename);
     }
     @JRubyMethod(name = {"pack_sockaddr_un", "sockaddr_un"}, meta = true)
     public static IRubyObject pack_sockaddr_un(ThreadContext context, IRubyObject recv, IRubyObject filename) {
         StringBuilder sb = new StringBuilder();
         sb.append((char)0);
         sb.append((char)1);
         String str = filename.convertToString().toString();
         sb.append(str);
         for(int i=str.length();i<104;i++) {
             sb.append((char)0);
         }
         return context.getRuntime().newString(sb.toString());
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject connect_nonblock(ThreadContext context, IRubyObject arg) {
         Channel socketChannel = getChannel();
         try {
             if (socketChannel instanceof AbstractSelectableChannel) {
                 ((AbstractSelectableChannel) socketChannel).configureBlocking(false);
                 connect(context, arg);
             } else {
                 throw getRuntime().newErrnoENOPROTOOPTError();
             }
         } catch(ClosedChannelException e) {
             throw context.getRuntime().newErrnoECONNREFUSEDError();
         } catch(IOException e) {
             e.printStackTrace();
             throw sockerr(context.getRuntime(), "connect(2): name or service not known");
         } catch (Error e) {
             // Workaround for a bug in Sun's JDK 1.5.x, see
             // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6303753
             Throwable cause = e.getCause();
             if (cause instanceof SocketException) {
                 handleSocketException(context.getRuntime(), "connect", (SocketException)cause);
             } else {
                 throw e;
             }
         }
 
         return RubyFixnum.zero(context.getRuntime());
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject connect(ThreadContext context, IRubyObject arg) {
         RubyArray sockaddr = (RubyArray) unpack_sockaddr_in(context, this, arg);
 
         try {
             IRubyObject addr = sockaddr.pop(context);
             IRubyObject port = sockaddr.pop(context);
             InetSocketAddress iaddr = new InetSocketAddress(
                     addr.convertToString().toString(), RubyNumeric.fix2int(port));
 
             Channel socketChannel = getChannel();
             if (socketChannel instanceof SocketChannel) {
                 if(!((SocketChannel) socketChannel).connect(iaddr)) {
                     throw context.getRuntime().newErrnoEINPROGRESSError();
                 }
             } else if (socketChannel instanceof DatagramChannel) {
                 ((DatagramChannel)socketChannel).connect(iaddr);
             } else {
                 throw getRuntime().newErrnoENOPROTOOPTError();
             }
         } catch(AlreadyConnectedException e) {
             throw context.getRuntime().newErrnoEISCONNError();
         } catch(ConnectionPendingException e) {
             Channel socketChannel = getChannel();
             if (socketChannel instanceof SocketChannel) {
                 try {
                     if (((SocketChannel) socketChannel).finishConnect()) {
                         throw context.getRuntime().newErrnoEISCONNError();
                     }
                     throw context.getRuntime().newErrnoEINPROGRESSError();
                 } catch (IOException ex) {
                     e.printStackTrace();
                     throw sockerr(context.getRuntime(), "connect(2): name or service not known");
                 }
             }
             throw context.getRuntime().newErrnoEINPROGRESSError();
         } catch(UnknownHostException e) {
             throw sockerr(context.getRuntime(), "connect(2): unknown host");
         } catch(SocketException e) {
             handleSocketException(context.getRuntime(), "connect", e);
         } catch(IOException e) {
             e.printStackTrace();
             throw sockerr(context.getRuntime(), "connect(2): name or service not known");
         } catch (IllegalArgumentException iae) {
             throw sockerr(context.getRuntime(), iae.getMessage());
         } catch (Error e) {
             // Workaround for a bug in Sun's JDK 1.5.x, see
             // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6303753
             Throwable cause = e.getCause();
             if (cause instanceof SocketException) {
                 handleSocketException(context.getRuntime(), "connect", (SocketException)cause);
             } else {
                 throw e;
             }
         }
         return RubyFixnum.zero(context.getRuntime());
     }
 
     @JRubyMethod(backtrace = true)
     public IRubyObject bind(ThreadContext context, IRubyObject arg) {
         RubyArray sockaddr = (RubyArray) unpack_sockaddr_in(context, this, arg);
 
         try {
             IRubyObject addr = sockaddr.pop(context);
             IRubyObject port = sockaddr.pop(context);
             InetSocketAddress iaddr = new InetSocketAddress(
                     addr.convertToString().toString(), RubyNumeric.fix2int(port));
 
             Channel socketChannel = getChannel();
             if (socketChannel instanceof SocketChannel) {
                 Socket socket = ((SocketChannel)socketChannel).socket();
                 socket.bind(iaddr);
             } else if (socketChannel instanceof DatagramChannel) {
                 DatagramSocket socket = ((DatagramChannel)socketChannel).socket();
                 socket.bind(iaddr);
             } else {
                 throw getRuntime().newErrnoENOPROTOOPTError();
             }
         } catch(UnknownHostException e) {
             throw sockerr(context.getRuntime(), "bind(2): unknown host");
         } catch(SocketException e) {
             handleSocketException(context.getRuntime(), "bind", e);
         } catch(IOException e) {
             e.printStackTrace();
             throw sockerr(context.getRuntime(), "bind(2): name or service not known");
         } catch (IllegalArgumentException iae) {
             throw sockerr(context.getRuntime(), iae.getMessage());
         } catch (Error e) {
             // Workaround for a bug in Sun's JDK 1.5.x, see
             // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6303753
             Throwable cause = e.getCause();
             if (cause instanceof SocketException) {
                 handleSocketException(context.getRuntime(), "bind", (SocketException)cause);
             } else {
                 throw e;
             }
         }
         return RubyFixnum.zero(context.getRuntime());
     }
 
     private void handleSocketException(Ruby runtime, String caller, SocketException e) {
         // e.printStackTrace();
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
 
     @Deprecated
     public static IRubyObject pack_sockaddr_in(IRubyObject recv, IRubyObject port, IRubyObject host) {
         return pack_sockaddr_in(recv.getRuntime().getCurrentContext(), recv, port, host);
     }
     @JRubyMethod(name = {"pack_sockaddr_in", "sockaddr_in"}, meta = true)
     public static IRubyObject pack_sockaddr_in(ThreadContext context, IRubyObject recv, IRubyObject port, IRubyObject host) {
         return pack_sockaddr_in(context, recv,
                 RubyNumeric.fix2int(port),
                 host.isNil() ? null : host.convertToString().toString());
     }
     public static IRubyObject pack_sockaddr_in(ThreadContext context, IRubyObject recv, int iport, String host) {
         ByteArrayOutputStream bufS = new ByteArrayOutputStream();
         try {
             DataOutputStream ds = new DataOutputStream(bufS);
-            if(Platform.IS_BSD) {
-                ds.write(16);
-                ds.write(2);
-            } else {
-                ds.write(2);
-                ds.write(0);
-            }
 
-            ds.write(iport >> 8);
-            ds.write(iport);
+            writeSockaddrHeader(ds);
+            writeSockaddrPort(ds, iport);
 
             try {
                 if(host != null && "".equals(host)) {
                     ds.writeInt(0);
                 } else {
                     InetAddress[] addrs = InetAddress.getAllByName(host);
                     byte[] addr = addrs[0].getAddress();
                     ds.write(addr, 0, addr.length);
                 }
             } catch (UnknownHostException e) {
                 throw sockerr(context.getRuntime(), "getaddrinfo: No address associated with nodename");
             }
 
-            ds.writeInt(0);
-            ds.writeInt(0);
+            writeSockaddrFooter(ds);
+        } catch (IOException e) {
+            throw sockerr(context.getRuntime(), "pack_sockaddr_in: internal error");
+        }
+
+        return context.getRuntime().newString(new ByteList(bufS.toByteArray(),
+                false));
+    }
+    static IRubyObject pack_sockaddr_in(ThreadContext context, InetSocketAddress sock) {
+        ByteArrayOutputStream bufS = new ByteArrayOutputStream();
+        try {
+            DataOutputStream ds = new DataOutputStream(bufS);
+
+            writeSockaddrHeader(ds);
+            writeSockaddrPort(ds, sock);
+
+            String host = sock.getAddress().getHostAddress();
+            if(host != null && "".equals(host)) {
+                ds.writeInt(0);
+            } else {
+                byte[] addr = sock.getAddress().getAddress();
+                ds.write(addr, 0, addr.length);
+            }
+
+            writeSockaddrFooter(ds);
         } catch (IOException e) {
             throw sockerr(context.getRuntime(), "pack_sockaddr_in: internal error");
         }
 
         return context.getRuntime().newString(new ByteList(bufS.toByteArray(),
                 false));
     }
     @Deprecated
     public static IRubyObject unpack_sockaddr_in(IRubyObject recv, IRubyObject addr) {
         return unpack_sockaddr_in(recv.getRuntime().getCurrentContext(), recv, addr);
     }
     @JRubyMethod(meta = true)
     public static IRubyObject unpack_sockaddr_in(ThreadContext context, IRubyObject recv, IRubyObject addr) {
-        String val = addr.convertToString().toString();
-        if((Platform.IS_BSD && val.charAt(0) != 16 && val.charAt(1) != 2) || (!Platform.IS_BSD && val.charAt(0) != 2)) {
+        ByteList val = addr.convertToString().getByteList();
+        if((Platform.IS_BSD && val.get(0) != 16 && val.get(1) != 2) || (!Platform.IS_BSD && val.get(0) != 2)) {
             throw context.getRuntime().newArgumentError("can't resolve socket address of wrong type");
         }
 
-        int port = (val.charAt(2) << 8) + (val.charAt(3));
+        int port = ((val.get(2)&0xff) << 8) + (val.get(3)&0xff);
         StringBuilder sb = new StringBuilder();
-        sb.append((int)val.charAt(4));
+        sb.append(val.get(4)&0xff);
         sb.append(".");
-        sb.append((int)val.charAt(5));
+        sb.append(val.get(5)&0xff);
         sb.append(".");
-        sb.append((int)val.charAt(6));
+        sb.append(val.get(6)&0xff);
         sb.append(".");
-        sb.append((int)val.charAt(7));
+        sb.append(val.get(7)&0xff);
 
         IRubyObject[] result = new IRubyObject[]{
                 context.getRuntime().newFixnum(port),
                 context.getRuntime().newString(sb.toString())};
 
         return context.getRuntime().newArrayNoCopy(result);
     }
 
     private static final ByteList BROADCAST = new ByteList("<broadcast>".getBytes());
     private static final byte[] INADDR_BROADCAST = new byte[] {-1,-1,-1,-1}; // 255.255.255.255
     private static final ByteList ANY = new ByteList("<any>".getBytes());
     private static final byte[] INADDR_ANY = new byte[] {0,0,0,0}; // 0.0.0.0
 
     public static InetAddress getRubyInetAddress(ByteList address) throws UnknownHostException {
         if (address.equal(BROADCAST)) {
             return InetAddress.getByAddress(INADDR_BROADCAST);
         } else if (address.equal(ANY)) {
             return InetAddress.getByAddress(INADDR_ANY);
         } else {
             return InetAddress.getByName(address.toString());
         }
     }
 
     @Deprecated
     public static IRubyObject gethostbyname(IRubyObject recv, IRubyObject hostname) {
         return gethostbyname(recv.getRuntime().getCurrentContext(), recv, hostname);
     }
     @JRubyMethod(meta = true)
     public static IRubyObject gethostbyname(ThreadContext context, IRubyObject recv, IRubyObject hostname) {
         try {
             InetAddress addr = getRubyInetAddress(hostname.convertToString().getByteList());
             Ruby runtime = context.getRuntime();
             IRubyObject[] ret = new IRubyObject[4];
             ret[0] = runtime.newString(addr.getHostAddress());
             ret[1] = runtime.newArray();
             ret[2] = runtime.newFixnum(2); // AF_INET
             ret[3] = runtime.newString(new ByteList(addr.getAddress()));
             return runtime.newArrayNoCopy(ret);
         } catch(UnknownHostException e) {
             throw sockerr(context.getRuntime(), "gethostbyname: name or service not known");
         }
     }
 
     @Deprecated
     public static IRubyObject getaddrinfo(IRubyObject recv, IRubyObject[] args) {
         return getaddrinfo(recv.getRuntime().getCurrentContext(), recv, args);
     }
     //def self.getaddrinfo(host, port, family = nil, socktype = nil, protocol = nil, flags = nil)
     @JRubyMethod(required = 2, optional = 4, meta = true)
     public static IRubyObject getaddrinfo(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         args = Arity.scanArgs(context.getRuntime(),args,2,4);
         try {
             Ruby r = context.getRuntime();
             IRubyObject host = args[0];
             IRubyObject port = args[1];
             boolean emptyHost = host.isNil() || host.convertToString().isEmpty();
 
             if(port instanceof RubyString) {
                 port = getservbyname(context, recv, new IRubyObject[]{port});
             }
 
             IRubyObject family = args[2];
             IRubyObject socktype = args[3];
             //IRubyObject protocol = args[4];
             IRubyObject flags = args[5];
 
             boolean is_ipv6 = (! family.isNil()) && (RubyNumeric.fix2int(family) & AF_INET6.value()) == AF_INET6.value();
             boolean sock_stream = true;
             boolean sock_dgram = true;
             if(!socktype.isNil()) {
                 int val = RubyNumeric.fix2int(socktype);
                 if(val == SOCK_STREAM.value()) {
                     sock_dgram = false;
                 } else if(val == SOCK_DGRAM.value()) {
                     sock_stream = false;
                 }
             }
 
             // When Socket::AI_PASSIVE and host is nil, return 'any' address.
             InetAddress[] addrs = null;
             if(!flags.isNil() && RubyFixnum.fix2int(flags) > 0) {
                 // The value of 1 is for Socket::AI_PASSIVE.
                 int flag = RubyNumeric.fix2int(flags);
                 if ((flag == 1) && emptyHost ) {
                     // use RFC 2732 style string to ensure that we get Inet6Address
                     addrs = InetAddress.getAllByName(is_ipv6 ? "[::]" : "0.0.0.0");
                 }
 
             }
 
             if (addrs == null) {
                 addrs = InetAddress.getAllByName(emptyHost ? (is_ipv6 ? "[::1]" : null) : host.convertToString().toString());
             }
 
             List<IRubyObject> l = new ArrayList<IRubyObject>();
             for(int i = 0; i < addrs.length; i++) {
                 IRubyObject[] c;
                 if(sock_dgram) {
                     c = new IRubyObject[7];
                     c[0] = r.newString(is_ipv6 ? "AF_INET6" : "AF_INET");
                     c[1] = port;
                     c[2] = r.newString(getHostAddress(recv, addrs[i]));
                     c[3] = r.newString(addrs[i].getHostAddress());
                     c[4] = r.newFixnum(is_ipv6 ? PF_INET6 : PF_INET);
                     c[5] = r.newFixnum(SOCK_DGRAM);
                     c[6] = r.newFixnum(IPPROTO_UDP);
                     l.add(r.newArrayNoCopy(c));
                 }
                 if(sock_stream) {
                     c = new IRubyObject[7];
                     c[0] = r.newString(is_ipv6 ? "AF_INET6" : "AF_INET");
                     c[1] = port;
                     c[2] = r.newString(getHostAddress(recv, addrs[i]));
                     c[3] = r.newString(addrs[i].getHostAddress());
                     c[4] = r.newFixnum(is_ipv6 ? PF_INET6 : PF_INET);
                     c[5] = r.newFixnum(SOCK_STREAM);
                     c[6] = r.newFixnum(IPPROTO_TCP);
                     l.add(r.newArrayNoCopy(c));
                 }
             }
             return r.newArray(l);
         } catch(UnknownHostException e) {
             throw sockerr(context.getRuntime(), "getaddrinfo: name or service not known");
         }
     }
 
     @Deprecated
     public static IRubyObject getnameinfo(IRubyObject recv, IRubyObject[] args) {
         return getnameinfo(recv.getRuntime().getCurrentContext(), recv, args);
     }
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject getnameinfo(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int argc = Arity.checkArgumentCount(runtime, args, 1, 2);
         int flags = argc == 2 ? RubyNumeric.num2int(args[1]) : 0;
         IRubyObject arg0 = args[0];
 
         String host, port;
         if (arg0 instanceof RubyArray) {
             List list = ((RubyArray)arg0).getList();
             int len = list.size();
             if (len < 3 || len > 4) {
                 throw runtime.newArgumentError("array size should be 3 or 4, "+len+" given");
             }
             // if array has 4 elements, third element is ignored
             host = list.size() == 3 ? list.get(2).toString() : list.get(3).toString();
             port = list.get(1).toString();
         } else if (arg0 instanceof RubyString) {
             String arg = ((RubyString)arg0).toString();
             Matcher m = STRING_IPV4_ADDRESS_PATTERN.matcher(arg);
             if (!m.matches()) {
                 IRubyObject obj = unpack_sockaddr_in(context, recv, arg0);
                 if (obj instanceof RubyArray) {
                     List list = ((RubyArray)obj).getList();
                     int len = list.size();
                     if (len != 2) {
                         throw runtime.newArgumentError("invalid address representation");
                     }
                     host = list.get(1).toString();
                     port = list.get(0).toString();
                 }
                 else {
                     throw runtime.newArgumentError("invalid address string");
                 }
             } else if ((host = m.group(IPV4_HOST_GROUP)) == null || host.length() == 0 ||
                     (port = m.group(IPV4_PORT_GROUP)) == null || port.length() == 0) {
                 throw runtime.newArgumentError("invalid address string");
             } else {
                 // Try IPv6
                 try {
                     InetAddress ipv6_addr = InetAddress.getByName(host);
                     if (ipv6_addr instanceof Inet6Address) {
                         host = ipv6_addr.getHostAddress();
                     }
                 } catch (UnknownHostException uhe) {
                     throw runtime.newArgumentError("invalid address string");
                 }
             }
         } else {
             throw runtime.newArgumentError("invalid args");
         }
 
         InetAddress addr;
         try {
             addr = InetAddress.getByName(host);
         } catch (UnknownHostException e) {
             throw sockerr(runtime, "unknown host: "+ host);
         }
         if ((flags & NI_NUMERICHOST.value()) == 0) {
             host = addr.getCanonicalHostName();
         } else {
             host = addr.getHostAddress();
         }
         jnr.netdb.Service serv = jnr.netdb.Service.getServiceByPort(Integer.parseInt(port), null);
         if (serv != null) {
             if ((flags & NI_NUMERICSERV.value()) == 0) {
                 port = serv.getName();
             } else {
                 port = Integer.toString(serv.getPort());
             }
         }
         return runtime.newArray(runtime.newString(host), runtime.newString(port));
 
     }
 
     private static String getHostAddress(IRubyObject recv, InetAddress addr) {
         return do_not_reverse_lookup(recv).isTrue() ? addr.getHostAddress() : addr.getCanonicalHostName();
     }
 
+    private static void writeSockaddrHeader(DataOutputStream ds) throws IOException {
+        if (Platform.IS_BSD) {
+            ds.write(16);
+            ds.write(2);
+        } else {
+            ds.write(2);
+            ds.write(0);
+        }
+    }
+
+    private static void writeSockaddrFooter(DataOutputStream ds) throws IOException {
+        ds.writeInt(0);
+        ds.writeInt(0);
+    }
+
+    private static void writeSockaddrPort(DataOutputStream ds, InetSocketAddress sockaddr) throws IOException {
+        writeSockaddrPort(ds, sockaddr.getPort());
+    }
+
+    private static void writeSockaddrPort(DataOutputStream ds, int port) throws IOException {
+        ds.write(port >> 8);
+        ds.write(port);
+    }
+
     private static final Pattern STRING_IPV4_ADDRESS_PATTERN =
         Pattern.compile("((.*)\\/)?([\\.0-9]+)(:([0-9]+))?");
 
     private final static Pattern ALREADY_BOUND_PATTERN = Pattern.compile("[Aa]lready.*bound");
     private final static Pattern ADDR_NOT_AVAIL_PATTERN = Pattern.compile("assign.*address");
     private final static Pattern PERM_DENIED_PATTERN = Pattern.compile("[Pp]ermission.*denied");
 
     private static final int IPV4_HOST_GROUP = 3;
     private static final int IPV4_PORT_GROUP = 5;
 }// RubySocket
