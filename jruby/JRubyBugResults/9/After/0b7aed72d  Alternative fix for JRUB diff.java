diff --git a/lib/ruby/site_ruby/shared/builtin/java/java.io.rb b/lib/ruby/site_ruby/shared/builtin/java/java.io.rb
index 4d13fdf586..8858936316 100644
--- a/lib/ruby/site_ruby/shared/builtin/java/java.io.rb
+++ b/lib/ruby/site_ruby/shared/builtin/java/java.io.rb
@@ -1,19 +1,31 @@
 require 'jruby'
 
 class java::io::InputStream
-  def to_io
-    JRuby.dereference(org.jruby.RubyIO.new(JRuby.runtime, self))
+  def to_io(opts = nil)
+    ruby_io = org.jruby.RubyIO.new(JRuby.runtime, self)
+    if opts && !opts[:autoclose]
+      ruby_io.setAutoclose(false)
+    end
+    JRuby.dereference(ruby_io)
   end
 end
 
 class java::io::OutputStream
-  def to_io
-    JRuby.dereference(org.jruby.RubyIO.new(JRuby.runtime, self))
+  def to_io(opts = nil)
+    ruby_io = org.jruby.RubyIO.new(JRuby.runtime, self)
+    if opts && !opts[:autoclose]
+      ruby_io.setAutoclose(false)
+    end
+    JRuby.dereference(ruby_io)
   end
 end
 
 module java::nio::channels::Channel
-  def to_io
-    JRuby.dereference(org.jruby.RubyIO.new(JRuby.runtime, self))
+  def to_io(opts = nil)
+    ruby_io = org.jruby.RubyIO.new(JRuby.runtime, self)
+    if opts && !opts[:autoclose]
+      ruby_io.setAutoclose(false)
+    end
+    JRuby.dereference(ruby_io)
   end
 end
diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index c81799ef7a..ba729fe6eb 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,2132 +1,2153 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import com.kenai.constantine.platform.Fcntl;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.FileNotFoundException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.CancelledKeyException;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.NonReadableChannelException;
 import java.nio.channels.Pipe;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jcodings.Encoding;
 import org.jruby.anno.FrameField;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.libraries.FcntlLibrary;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.DirectoryAsFileException;
 import org.jruby.util.io.STDIO;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 
 import org.jruby.util.io.SelectorFactory;
 import java.nio.channels.spi.SelectorProvider;
 
 import static org.jruby.CompatVersion.*;
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 /**
  * 
  * @author jpetersen
  */
 @JRubyClass(name="IO", include="Enumerable")
 public class RubyIO extends RubyObject {
     // This should only be called by this and RubyFile.
     // It allows this object to be created without a IOHandler.
     public RubyIO(Ruby runtime, RubyClass type) {
         super(runtime, type);
         
         openFile = new OpenFile();
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(outputStream))));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(inputStream))));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.READABLE);
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newRuntimeError("Opening null channelpo");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(channel)));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
     }
 
     public RubyIO(Ruby runtime, ShellLauncher.POpenProcess process, ModeFlags modes) {
     	super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         
         openFile.setMode(modes.getOpenFileFlags() | OpenFile.SYNC);
         openFile.setProcess(process);
 
         try {
             if (openFile.isReadable()) {
                 Channel inChannel;
                 if (process.getInput() != null) {
                     // NIO-based
                     inChannel = process.getInput();
                 } else {
                     // Stream-based
                     inChannel = Channels.newChannel(process.getInputStream());
                 }
                 
                 ChannelDescriptor main = new ChannelDescriptor(
                         inChannel);
                 main.setCanBeSeekable(false);
                 
                 openFile.setMainStream(ChannelStream.open(getRuntime(), main));
             }
             
             if (openFile.isWritable() && process.hasOutput()) {
                 Channel outChannel;
                 if (process.getOutput() != null) {
                     // NIO-based
                     outChannel = process.getOutput();
                 } else {
                     outChannel = Channels.newChannel(process.getOutputStream());
                 }
 
                 ChannelDescriptor pipe = new ChannelDescriptor(
                         outChannel);
                 pipe.setCanBeSeekable(false);
                 
                 if (openFile.getMainStream() != null) {
                     openFile.setPipeStream(ChannelStream.open(getRuntime(), pipe));
                 } else {
                     openFile.setMainStream(ChannelStream.open(getRuntime(), pipe));
                 }
             }
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         ChannelDescriptor descriptor;
 
         try {
             switch (stdio) {
             case IN:
                 // special constructor that accepts stream, not channel
                 descriptor = new ChannelDescriptor(runtime.getIn(), new ModeFlags(ModeFlags.RDONLY), FileDescriptor.in);
                 runtime.putFilenoMap(0, descriptor.getFileno());
                 openFile.setMainStream(
                         ChannelStream.open(
                             runtime, 
                             descriptor));
                 break;
             case OUT:
                 descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getOut()), new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out);
                 runtime.putFilenoMap(1, descriptor.getFileno());
                 openFile.setMainStream(
                         ChannelStream.open(
                             runtime, 
                             descriptor));
                 openFile.getMainStream().setSync(true);
                 break;
             case ERR:
                 descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getErr()), new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.err);
                 runtime.putFilenoMap(2, descriptor.getFileno());
                 openFile.setMainStream(
                         ChannelStream.open(
                             runtime, 
                             descriptor));
                 openFile.getMainStream().setSync(true);
                 break;
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
+        // never autoclose stdio streams
+        openFile.setAutoclose(false);
     }
     
     public static RubyIO newIO(Ruby runtime, Channel channel) {
         return new RubyIO(runtime, channel);
     }
     
     public OpenFile getOpenFile() {
         return openFile;
     }
     
     protected OpenFile getOpenFileChecked() {
         openFile.checkClosed(getRuntime());
         return openFile;
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
 
     /*
      * We use FILE versus IO to match T_FILE in MRI.
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.FILE;
     }
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
 
         ioClass.index = ClassIndex.IO;
         ioClass.setReifiedClass(RubyIO.class);
 
         ioClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyIO;
             }
         };
 
         ioClass.includeModule(runtime.getEnumerable());
         
         // TODO: Implement tty? and isatty.  We have no real capability to
         // determine this from java, but if we could set tty status, then
         // we could invoke jruby differently to allow stdin to return true
         // on this.  This would allow things like cgi.rb to work properly.
         
         ioClass.defineAnnotatedMethods(RubyIO.class);
 
         // Constants for seek
         ioClass.fastSetConstant("SEEK_SET", runtime.newFixnum(Stream.SEEK_SET));
         ioClass.fastSetConstant("SEEK_CUR", runtime.newFixnum(Stream.SEEK_CUR));
         ioClass.fastSetConstant("SEEK_END", runtime.newFixnum(Stream.SEEK_END));
 
         if (runtime.is1_9()) {
             ioClass.defineModuleUnder("WaitReadable");
             ioClass.defineModuleUnder("WaitWritable");
         }
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
         return getHandler().newOutputStream();
     }
 
     public InputStream getInStream() {
         return getHandler().newInputStream();
     }
 
     public Channel getChannel() {
         return getHandler().getChannel();
     }
     
     public Stream getHandler() {
         return getOpenFileChecked().getMainStream();
     }
 
     protected void reopenPath(Ruby runtime, IRubyObject[] args) {
         if (runtime.is1_9() && !(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             args[0] = args[0].callMethod(runtime.getCurrentContext(), "to_path");
         }
         IRubyObject pathString = args[0].convertToString();
 
         // TODO: check safe, taint on incoming string
 
         try {
             ModeFlags modes;
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = getIOModes(runtime, modeString.toString());
 
                 openFile.setMode(modes.getOpenFileFlags());
             } else {
                 modes = getIOModes(runtime, "r");
             }
 
             String path = pathString.toString();
 
             // Ruby code frequently uses a platform check to choose "NUL:" on windows
             // but since that check doesn't work well on JRuby, we help it out
 
             openFile.setPath(path);
 
             if (openFile.getMainStream() == null) {
                 try {
                     openFile.setMainStream(ChannelStream.fopen(runtime, path, modes));
                 } catch (FileExistsException fee) {
                     throw runtime.newErrnoEEXISTError(path);
                 }
 
                 if (openFile.getPipeStream() != null) {
                     openFile.getPipeStream().fclose();
                     openFile.setPipeStream(null);
                 }
             } else {
                 // TODO: This is an freopen in MRI, this is close, but not quite the same
                 openFile.getMainStream().freopen(runtime, path, getIOModes(runtime, openFile.getModeAsString(runtime)));
                 
                 if (openFile.getPipeStream() != null) {
                     // TODO: pipe handler to be reopened with path and "w" mode
                 }
             }
         } catch (PipeException pe) {
             throw runtime.newErrnoEPIPEError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     protected void reopenIO(Ruby runtime, RubyIO ios) {
         try {
             if (ios.openFile == this.openFile) return;
 
             OpenFile originalFile = ios.getOpenFileChecked();
             OpenFile selfFile = getOpenFileChecked();
 
             long pos = 0;
             if (originalFile.isReadable()) {
                 pos = originalFile.getMainStream().fgetpos();
             }
 
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStream().fflush();
             }
 
             if (selfFile.isWritable()) {
                 selfFile.getWriteStream().fflush();
             }
 
             selfFile.setMode(originalFile.getMode());
             selfFile.setProcess(originalFile.getProcess());
             selfFile.setLineNumber(originalFile.getLineNumber());
             selfFile.setPath(originalFile.getPath());
             selfFile.setFinalizer(originalFile.getFinalizer());
 
             ChannelDescriptor selfDescriptor = selfFile.getMainStream().getDescriptor();
             ChannelDescriptor originalDescriptor = originalFile.getMainStream().getDescriptor();
 
             // confirm we're not reopening self's channel
             if (selfDescriptor.getChannel() != originalDescriptor.getChannel()) {
                 // check if we're a stdio IO, and ensure we're not badly mutilated
                 if (runtime.getFileno(selfDescriptor) >= 0 && runtime.getFileno(selfDescriptor) <= 2) {
                     selfFile.getMainStream().clearerr();
 
                     // dup2 new fd into self to preserve fileno and references to it
                     originalDescriptor.dup2Into(selfDescriptor);
                 } else {
                     Stream pipeFile = selfFile.getPipeStream();
                     int mode = selfFile.getMode();
                     selfFile.getMainStream().fclose();
                     selfFile.setPipeStream(null);
 
                     // TODO: turn off readable? am I reading this right?
                     // This only seems to be used while duping below, since modes gets
                     // reset to actual modes afterward
                     //fptr->mode &= (m & FMODE_READABLE) ? ~FMODE_READABLE : ~FMODE_WRITABLE;
 
                     if (pipeFile != null) {
                         selfFile.setMainStream(ChannelStream.fdopen(runtime, originalDescriptor, new ModeFlags()));
                         selfFile.setPipeStream(pipeFile);
                     } else {
                         // only use internal fileno here, stdio is handled above
                         selfFile.setMainStream(
                                 ChannelStream.open(
                                 runtime,
                                 originalDescriptor.dup2(selfDescriptor.getFileno())));
 
                         // since we're not actually duping the incoming channel into our handler, we need to
                         // copy the original sync behavior from the other handler
                         selfFile.getMainStream().setSync(selfFile.getMainStream().isSync());
                     }
                     selfFile.setMode(mode);
                 }
 
                 // TODO: anything threads attached to original fd are notified of the close...
                 // see rb_thread_fd_close
 
                 if (originalFile.isReadable() && pos >= 0) {
                     selfFile.seek(pos, Stream.SEEK_SET);
                     originalFile.seek(pos, Stream.SEEK_SET);
                 }
             }
 
             // only use internal fileno here, stdio is handled above
             if (selfFile.getPipeStream() != null && selfDescriptor.getFileno() != selfFile.getPipeStream().getDescriptor().getFileno()) {
                 int fd = selfFile.getPipeStream().getDescriptor().getFileno();
 
                 if (originalFile.getPipeStream() == null) {
                     selfFile.getPipeStream().fclose();
                     selfFile.setPipeStream(null);
                 } else if (fd != originalFile.getPipeStream().getDescriptor().getFileno()) {
                     selfFile.getPipeStream().fclose();
                     ChannelDescriptor newFD2 = originalFile.getPipeStream().getDescriptor().dup2(fd);
                     selfFile.setPipeStream(ChannelStream.fdopen(runtime, newFD2, getIOModes(runtime, "w")));
                 }
             }
 
             // TODO: restore binary mode
             //            if (fptr->mode & FMODE_BINMODE) {
             //                rb_io_binmode(io);
             //            }
 
             // TODO: set our metaclass to target's class (i.e. scary!)
 
         } catch (IOException ex) { // TODO: better error handling
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (PipeException ex) {
             throw runtime.newIOError("could not reopen: " + ex.getMessage());
         } catch (InvalidValueException ive) {
             throw runtime.newErrnoEINVALError();
         }
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
     	IRubyObject tmp = TypeConverter.convertToTypeWithCheck(args[0], runtime.getIO(), "to_io");
         
     	if (!tmp.isNil()) {
             reopenIO(runtime, (RubyIO) tmp);
         } else {
             reopenPath(runtime, args);
         }
         
         return this;
     }
     
     public static ModeFlags getIOModes(Ruby runtime, String modesString) throws InvalidValueException {
         return new ModeFlags(getIOModesIntFromString(runtime, modesString));
     }
         
     public static int getIOModesIntFromString(Ruby runtime, String modesString) {
         int modes = 0;
         int length = modesString.length();
 
         if (length == 0) {
             throw runtime.newArgumentError("illegal access mode");
         }
 
         switch (modesString.charAt(0)) {
         case 'r' :
             modes |= ModeFlags.RDONLY;
             break;
         case 'a' :
             modes |= ModeFlags.APPEND | ModeFlags.WRONLY | ModeFlags.CREAT;
             break;
         case 'w' :
             modes |= ModeFlags.WRONLY | ModeFlags.TRUNC | ModeFlags.CREAT;
             break;
         default :
             throw runtime.newArgumentError("illegal access mode " + modesString);
         }
 
         ModifierLoop: for (int n = 1; n < length; n++) {
             switch (modesString.charAt(n)) {
             case 'b':
                 modes |= ModeFlags.BINARY;
                 break;
             case '+':
                 modes = (modes & ~ModeFlags.ACCMODE) | ModeFlags.RDWR;
                 break;
             case 't' :
                 // FIXME: add text mode to mode flags
                 break;
             case ':':
                 break ModifierLoop;
             default:
                 throw runtime.newArgumentError("illegal access mode " + modesString);
             }
         }
 
         return modes;
     }
 
     /*
      * Ensure that separator is valid otherwise give it the default paragraph separator.
      */
     private static ByteList separator(Ruby runtime) {
         return separator(runtime.getRecordSeparatorVar().get());
     }
 
     private static ByteList separator(IRubyObject separatorValue) {
         ByteList separator = separatorValue.isNil() ? null :
             separatorValue.convertToString().getByteList();
 
         if (separator != null && separator.getRealSize() == 0) separator = Stream.PARAGRAPH_DELIMETER;
 
         return separator;
     }
 
     private static ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
         return separator(args.length > idx ? args[idx] : runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList getSeparatorForGets(Ruby runtime, IRubyObject[] args) {
         return getSeparatorFromArgs(runtime, args, 0);
     }
 
     private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {
         return getline(runtime, separator, -1, cache);
     }
 
     public IRubyObject getline(Ruby runtime, ByteList separator) {
         return getline(runtime, separator, -1, null);
     }
 
 
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     public IRubyObject getline(Ruby runtime, ByteList separator, long limit) {
         return getline(runtime, separator, limit, null);
     }
     
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     private IRubyObject getline(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
             separator = isParagraph ? Stream.PARAGRAPH_SEPARATOR : separator;
             
             if (isParagraph) swallow('\n');
             
             if (separator == null && limit < 0) {
                 RubyString str = readAll();
                 if (str.getByteList().length() == 0) {
                     return runtime.getNil();
                 }
                 incrementLineno(runtime, myOpenFile);
                 return str;
             } else if (limit == 0) {
                 return RubyString.newEmptyString(runtime);
             } else if (separator.length() == 1 && limit < 0) {
                 return getlineFast(runtime, separator.get(0) & 0xFF, cache);
             } else {
                 Stream readStream = myOpenFile.getMainStream();
                 int c = -1;
                 int n = -1;
                 int newline = separator.get(separator.length() - 1) & 0xFF;
 
                 ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
                 try {
                     boolean update = false;
                     boolean limitReached = false;
 
                     while (true) {
                         do {
                             readCheck(readStream);
                             readStream.clearerr();
 
                             try {
                                 if (limit == -1) {
                                     n = readStream.getline(buf, (byte) newline);
                                 } else {
                                     n = readStream.getline(buf, (byte) newline, limit);
                                     limit -= n;
                                     if (limit <= 0) {
                                         update = limitReached = true;
                                         break;
                                     }
                                 }
 
                                 c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                             } catch (EOFException e) {
                                 n = -1;
                             }
                             
                             // CRuby checks ferror(f) and retry getc for
                             // non-blocking IO.
                             if (n == 0) {
                                 waitReadable(readStream);
                                 continue;
                             } else if (n == -1) {
                                 break;
                             }
 
                             update = true;
                         } while (c != newline); // loop until we see the nth separator char
 
                         // if we hit EOF or reached limit then we're done
                         if (n == -1 || limitReached) break;
 
                         // if we've found the last char of the separator,
                         // and we've found at least as many characters as separator length,
                         // and the last n characters of our buffer match the separator, we're done
                         if (c == newline && buf.length() >= separator.length() &&
                                 0 == ByteList.memcmp(buf.getUnsafeBytes(), buf.getBegin() + buf.getRealSize() - separator.length(), separator.getUnsafeBytes(), separator.getBegin(), separator.getRealSize())) {
                             break;
                         }
                     }
                     
                     if (isParagraph && c != -1) swallow('\n');
                     
                     if (!update) {
                         return runtime.getNil();
                     } else {
                         incrementLineno(runtime, myOpenFile);
                         RubyString str = RubyString.newString(runtime, cache != null ? new ByteList(buf) : buf);
                         str.setTaint(true);
 
                         return str;
                     }
                 }
                 finally {
                     if(cache != null) {
                         cache.release(buf);
                     }
                 }
             }
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (EOFException e) {
             return runtime.getNil();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     private void incrementLineno(Ruby runtime, OpenFile myOpenFile) {
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.setCurrentLine(lineno);
         RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), lineno);
     }
 
     protected boolean swallow(int term) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
         int c;
         
         do {
             readCheck(readStream);
             
             try {
                 c = readStream.fgetc();
             } catch (EOFException e) {
                 c = -1;
             }
             
             if (c != term) {
                 readStream.ungetc(c);
                 return true;
             }
         } while (c != -1);
         
         return false;
     }
     
     private static String vendor;
     static { String v = SafePropertyAccessor.getProperty("java.vendor") ; vendor = (v == null) ? "" : v; };
     private static String msgEINTR = "Interrupted system call";
 
     public static boolean restartSystemCall(Exception e) {
         return vendor.startsWith("Apple") && e.getMessage().equals(msgEINTR);
     }
     
     private IRubyObject getlineFast(Ruby runtime, int delim, ByteListCache cache) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
         int c = -1;
 
         ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
         try {
             boolean update = false;
             do {
                 readCheck(readStream);
                 readStream.clearerr();
                 int n;
                 try {
                     n = readStream.getline(buf, (byte) delim);
                     c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                 } catch (EOFException e) {
                     n = -1;
                 }
 
                 // CRuby checks ferror(f) and retry getc for non-blocking IO.
                 if (n == 0) {
                     waitReadable(readStream);
                     continue;
                 } else if (n == -1) {
                     break;
                 }
                 
                 update = true;
             } while (c != delim);
 
             if (!update) {
                 return runtime.getNil();
             } else {
                 incrementLineno(runtime, openFile);
                 RubyString str = RubyString.newString(runtime, cache != null ? new ByteList(buf) : buf);
                 str.setTaint(true);
                 return str;
             }
         }
         finally {
             if(cache != null) {
                 cache.release(buf);
             }
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             context.getRuntime().getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead",
                     className + "::open()");
         }
         
         return klass.newInstance(context, args, block);
     }
 
     private IRubyObject initializeCommon19(int fileno, ModeFlags modes) {
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(getRuntime().getFilenoExtMap(fileno));
 
             if (descriptor == null) throw getRuntime().newErrnoEBADFError();
 
             descriptor.checkOpen();
 
             if (modes == null) modes = descriptor.getOriginalModes();
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(getRuntime(), false);
             }
 
             openFile.setMode(modes.getOpenFileFlags());
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unusedBlock) {
         return initializeCommon19(RubyNumeric.fix2int(fileNumber), null);
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes;
         if (second instanceof RubyHash) {
             modes = parseOptions(context, second, null);
         } else {
             modes = parseModes19(context, second);
         }
 
         return initializeCommon19(fileno, modes);
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unusedBlock) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         ModeFlags modes = parseModes19(context, modeValue);
 
         modes = parseOptions(context, options, modes);
         return initializeCommon19(fileno, modes);
     }
 
     protected ModeFlags parseModes(IRubyObject arg) {
         try {
             if (arg instanceof RubyFixnum) return new ModeFlags(RubyFixnum.fix2long(arg));
 
             return getIOModes(getRuntime(), arg.convertToString().toString());
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
 
     protected ModeFlags parseModes19(ThreadContext context, IRubyObject arg) {
         ModeFlags modes = parseModes(arg);
 
         if (arg instanceof RubyString) {
             parseEncodingFromString(context, arg, 1);
         }
 
         return modes;
     }
 
     private void parseEncodingFromString(ThreadContext context, IRubyObject arg, int initialPosition) {
         RubyString modes19 = arg.convertToString();
         if (modes19.toString().contains(":")) {
             IRubyObject[] fullEncoding = modes19.split(context, RubyString.newString(context.getRuntime(), ":")).toJavaArray();
 
             IRubyObject externalEncodingOption = fullEncoding[initialPosition];
             IRubyObject internalEncodingOption = null;
             if (fullEncoding.length > (initialPosition + 1)) {
                 internalEncodingOption = fullEncoding[initialPosition + 1];
                 set_encoding(context, externalEncodingOption, internalEncodingOption);
             } else {
                 set_encoding(context, externalEncodingOption);
             }
         }
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int argCount = args.length;
         ModeFlags modes;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(getRuntime().getFilenoExtMap(fileno));
             
             if (descriptor == null) {
                 throw getRuntime().newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     modes = new ModeFlags(RubyFixnum.fix2long(args[1]));
                 } else {
                     modes = getIOModes(getRuntime(), args[1].convertToString().toString());
                 }
             } else {
                 // use original modes
                 modes = descriptor.getOriginalModes();
             }
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(getRuntime(), false);
             }
 
             openFile.setMode(modes.getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, modes));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ive) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) throws InvalidValueException {
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw getRuntime().newErrnoEBADFError();
             
 //            if (mode == null) {
 //                mode = "r";
 //            }
 //            
 //            try {
 //                openFile.setMainStream(streamForFileno(getRuntime(), fileno));
 //            } catch (BadDescriptorException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            } catch (IOException e) {
 //                throw getRuntime().newErrnoEBADFError();
 //            }
 //            //modes = new IOModes(getRuntime(), mode);
 //            
 //            registerStream(openFile.getMainStream());
         } else {
             // We are creating a new IO object that shares the same
             // IOHandler (and fileno).
             return ChannelStream.fdopen(getRuntime(), existingDescriptor, modes);
         }
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         return externalEncoding != null ? externalEncoding : RubyEncoding.getDefaultExternal(context.getRuntime());
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         return internalEncoding != null ? internalEncoding : context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString) {
         setExternalEncoding(context, encodingString);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     private void setExternalEncoding(ThreadContext context, IRubyObject encoding) {
         externalEncoding = getEncodingCommon(context, encoding);
     }
 
 
     private void setInternalEncoding(ThreadContext context, IRubyObject encoding) {
         IRubyObject internalEncodingOption = getEncodingCommon(context, encoding);
 
         if (internalEncodingOption.toString().equals(external_encoding(context).toString())) {
             context.getRuntime().getWarnings().warn("Ignoring internal encoding " + encoding
                     + ": it is identical to external encoding " + external_encoding(context));
         } else {
             internalEncoding = internalEncodingOption;
         }
     }
 
     private IRubyObject getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         IRubyObject rubyEncoding = null;
         if (encoding instanceof RubyEncoding) {
             rubyEncoding = encoding;
         } else {
             Encoding encodingObj = RubyEncoding.getEncodingFromObject(context.getRuntime(), encoding);
             rubyEncoding = RubyEncoding.convertEncodingToRubyEncoding(context.getRuntime(), encodingObj);            
         }
         return rubyEncoding;
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         RubyClass klass = (RubyClass)recv;
         
         RubyIO io = (RubyIO)klass.newInstance(context, args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(context, io);
             } finally {
                 try {
                     io.getMetaClass().finvoke(context, io, "close", IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
                 } catch (RaiseException re) {
                     RubyException rubyEx = re.getException();
                     if (rubyEx.kind_of_p(context, runtime.getStandardError()).isTrue()) {
                         // MRI behavior: swallow StandardErorrs
                         runtime.getGlobalVariables().clear("$!");
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, frame = true, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject pathString = args[0].convertToString();
         return sysopenCommon(recv, args, block, pathString);
     }
     
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, frame = true, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject sysopen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject pathString;
         if (!(args[0] instanceof RubyString) && args[0].respondsTo("to_path")) {
             pathString = args[0].callMethod(context, "to_path");
         } else {
             pathString = args[0].convertToString();
         }
         return sysopenCommon(recv, args, block, pathString);
     }
 
     private static IRubyObject sysopenCommon(IRubyObject recv, IRubyObject[] args, Block block, IRubyObject pathString) {
         Ruby runtime = recv.getRuntime();
         runtime.checkSafeString(pathString);
         String path = pathString.toString();
 
         ModeFlags modes = null;
         int perms = -1; // -1 == don't set permissions
         try {
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = getIOModes(runtime, modeString.toString());
             } else {
                 modes = getIOModes(runtime, "r");
             }
             if (args.length > 2) {
                 RubyInteger permsInt =
                     args.length >= 3 ? args[2].convertToInteger() : null;
                 perms = RubyNumeric.fix2int(permsInt);
             }
         } catch (InvalidValueException e) {
             throw runtime.newErrnoEINVALError();
         }
 
         int fileno = -1;
         try {
             ChannelDescriptor descriptor =
                 ChannelDescriptor.open(runtime.getCurrentDirectory(),
                                        path, modes, perms, runtime.getPosix());
             // always a new fileno, so ok to use internal only
             fileno = descriptor.getFileno();
         }
         catch (FileNotFoundException fnfe) {
             throw runtime.newErrnoENOENTError(path);
         } catch (DirectoryAsFileException dafe) {
             throw runtime.newErrnoEISDirError(path);
         } catch (FileExistsException fee) {
             throw runtime.newErrnoEEXISTError(path);
         } catch (IOException ioe) {
             throw runtime.newIOErrorFromException(ioe);
         }
         return runtime.newFixnum(fileno);
     }
 
+    public boolean isAutoclose() {
+        return openFile.isAutoclose();
+    }
+
+    public void setAutoclose(boolean autoclose) {
+        openFile.setAutoclose(autoclose);
+    }
+
+    @JRubyMethod(compat = RUBY1_9)
+    public IRubyObject autoclose(ThreadContext context) {
+        return context.runtime.newBoolean(openFile.isAutoclose());
+    }
+
+    @JRubyMethod(name = "autoclose=", compat = RUBY1_9)
+    public IRubyObject autoclose_set(ThreadContext context) {
+        openFile.setAutoclose(true);
+        return context.nil;
+    }
+
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         if (isClosed()) {
             throw getRuntime().newIOError("closed stream");
         }
         openFile.setBinmode();
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = CompatVersion.RUBY1_9)
     public IRubyObject op_binmode(ThreadContext context) {
         return RubyBoolean.newBoolean(context.getRuntime(), openFile.isBinmode());
     }
 
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         try {
             RubyString string = obj.asString();
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
             
             Stream writeStream = myOpenFile.getWriteStream();
             
             if (myOpenFile.isWriteBuffered()) {
                 runtime.getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "syswrite for buffered IO");
             }
             
             if (!writeStream.getDescriptor().isWritable()) {
                 myOpenFile.checkClosed(runtime);
             }
             
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return runtime.newFixnum(read);
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newSystemCallError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(ThreadContext context, IRubyObject obj) {
         // MRI behavior: always check whether the file is writable
         // or not, even if we are to write 0 bytes.
         OpenFile myOpenFile = getOpenFileChecked();
 
         try {
             myOpenFile.checkWritable(context.getRuntime());
             RubyString str = obj.asString();
             if (str.getByteList().length() == 0) {
                 return context.getRuntime().newFixnum(0);
             }
 
             if (myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSWRITE_BUFFERED_IO, "write_nonblock for buffered IO");
             }
             int written = myOpenFile.getWriteStream().getDescriptor().write(str.getByteList());
             return context.getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw context.getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         }  catch (PipeException ex) {
             throw context.getRuntime().newErrnoEPIPEError();
         }
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.getRuntime();
         
         runtime.secure(4);
         
         RubyString str = obj.asString();
 
         // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
         
         if (str.getByteList().length() == 0) {
             return runtime.newFixnum(0);
         }
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
 
             int written = fwrite(str.getByteList());
 
             if (written == -1) {
                 // TODO: sys fail
             }
 
             // if not sync, we switch to write buffered mode
             if (!myOpenFile.isSync()) {
                 myOpenFile.setWriteBuffered();
             }
 
             return runtime.newFixnum(written);
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         }
     }
     
     private boolean waitWritable(Stream stream) {
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_WRITE);
             return true;
         }
         return false;
     }
 
     private boolean waitReadable(Stream stream) {
         if (stream.readDataBuffered()) {
             return true;
         }
         Channel ch = stream.getChannel();
         if (ch instanceof SelectableChannel) {
             getRuntime().getCurrentContext().getThread().select(ch, this, SelectionKey.OP_READ);
             return true;
         }
         return false;
     }
 
     protected int fwrite(ByteList buffer) {
         int n, r, l, offset = 0;
         boolean eagain = false;
         Stream writeStream = openFile.getWriteStream();
 
         int len = buffer.length();
         
         if ((n = len) <= 0) return n;
 
         try {
             if (openFile.isSync()) {
                 openFile.fflush(writeStream);
 
                 // TODO: why is this guarded?
     //            if (!rb_thread_fd_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //            }
                
                 while(offset<len) {
                     l = n;
 
                     // TODO: Something about pipe buffer length here
 
                     r = writeStream.getDescriptor().write(buffer,offset,l);
 
                     if(r == len) {
                         return len; //Everything written
                     }
 
                     if (0 <= r) {
                         offset += r;
                         n -= r;
                         eagain = true;
                     }
 
                     if(eagain && waitWritable(writeStream)) {
                         openFile.checkClosed(getRuntime());
                         if(offset >= buffer.length()) {
                             return -1;
                         }
                         eagain = false;
                     } else {
                         return -1;
                     }
                 }
 
 
                 // TODO: all this stuff...some pipe logic, some async thread stuff
     //          retry:
     //            l = n;
     //            if (PIPE_BUF < l &&
     //                !rb_thread_critical &&
     //                !rb_thread_alone() &&
     //                wsplit_p(fptr)) {
     //                l = PIPE_BUF;
     //            }
     //            TRAP_BEG;
     //            r = write(fileno(f), RSTRING(str)->ptr+offset, l);
     //            TRAP_END;
     //            if (r == n) return len;
     //            if (0 <= r) {
     //                offset += r;
     //                n -= r;
     //                errno = EAGAIN;
     //            }
     //            if (rb_io_wait_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //                if (offset < RSTRING(str)->len)
     //                    goto retry;
     //            }
     //            return -1L;
             }
 
             // TODO: handle errors in buffered write by retrying until finished or file is closed
             return writeStream.fwrite(buffer);
     //        while (errno = 0, offset += (r = fwrite(RSTRING(str)->ptr+offset, 1, n, f)), (n -= r) > 0) {
     //            if (ferror(f)
     //            ) {
     //                if (rb_io_wait_writable(fileno(f))) {
     //                    rb_io_check_closed(fptr);
     //                    clearerr(f);
     //                    if (offset < RSTRING(str)->len)
     //                        continue;
     //                }
     //                return -1L;
     //            }
     //        }
 
 //            return len - n;
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     /** rb_io_addstr
      * 
      */
     @JRubyMethod(name = "<<", required = 1)
     public IRubyObject op_append(ThreadContext context, IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
         callMethod(context, "write", anObject);
         
         return this; 
     }
 
     @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         // map to external fileno
         return runtime.newFixnum(runtime.getFileno(getOpenFileChecked().getMainStream().getDescriptor()));
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno(ThreadContext context) {
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(ThreadContext context, IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return context.getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync(ThreadContext context) {
         return context.getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
     }
     
     /**
      * <p>Return the process id (pid) of the process this IO object
      * spawned.  If no process exists (popen was not called), then
      * nil is returned.  This is not how it appears to be defined
      * but ruby 1.8 works this way.</p>
      * 
      * @return the pid or nil
      */
     @JRubyMethod(name = "pid")
     public IRubyObject pid(ThreadContext context) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (myOpenFile.getProcess() == null) {
             return context.getRuntime().getNil();
         }
         
         // Of course this isn't particularly useful.
         long pid = myOpenFile.getPid();
         
         return context.getRuntime().newFixnum(pid); 
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos(ThreadContext context) {
         try {
             return context.getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(ThreadContext context, IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw context.getRuntime().newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.getMainStream().lseek(offset, Stream.SEEK_SET);
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return context.getRuntime().newFixnum(offset);
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true, reads = FrameField.LASTLINE)
     public IRubyObject print(ThreadContext context, IRubyObject[] args) {
         return print(context, this, args);
     }
 
     /** Print some objects to the stream.
      *
      */
     public static IRubyObject print(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getCurrentScope().getLastLine(context.getRuntime()) };
         }
 
         Ruby runtime = context.getRuntime();
         IRubyObject fs = runtime.getGlobalVariables().get("$,");
         IRubyObject rs = runtime.getGlobalVariables().get("$\\");
 
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 maybeIO.callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 maybeIO.callMethod(context, "write", runtime.newString("nil"));
             } else {
                 maybeIO.callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             maybeIO.callMethod(context, "write", rs);
         }
 
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(ThreadContext context, IRubyObject[] args) {
         callMethod(context, "write", RubyKernel.sprintf(context, this, args));
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "putc", required = 1, backtrace = true)
     public IRubyObject putc(ThreadContext context, IRubyObject object) {
         return putc(context, this, object);
     }
 
     public static IRubyObject putc(ThreadContext context, IRubyObject maybeIO, IRubyObject object) {
         int c = RubyNumeric.num2chr(object);
         if (maybeIO instanceof RubyIO) {
             // FIXME we should probably still be dyncalling 'write' here
             RubyIO io = (RubyIO)maybeIO;
             try {
                 OpenFile myOpenFile = io.getOpenFileChecked();
                 myOpenFile.checkWritable(context.getRuntime());
                 Stream writeStream = myOpenFile.getWriteStream();
                 writeStream.fputc(c);
                 if (myOpenFile.isSync()) myOpenFile.fflush(writeStream);
             } catch (IOException ex) {
                 throw context.getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException e) {
                 throw context.getRuntime().newErrnoEBADFError();
             } catch (InvalidValueException ex) {
                 throw context.getRuntime().newErrnoEINVALError();
             } catch (PipeException ex) {
                 throw context.getRuntime().newErrnoEPIPEError();
             }
         } else {
             maybeIO.callMethod(context, "write",
                     RubyString.newStringNoCopy(context.getRuntime(), new byte[] {(byte)c}));
         }
 
         return object;
     }
 
     public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = Stream.SEEK_SET;
         
         return doSeek(context, offset, whence);
     }
 
     @JRubyMethod(name = "seek")
     public RubyFixnum seek(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         long offset = RubyNumeric.num2long(arg0);
         int whence = RubyNumeric.fix2int(arg1.convertToInteger());
         
         return doSeek(context, offset, whence);
     }
     
     private RubyFixnum doSeek(ThreadContext context, long offset, int whence) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.seek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return RubyFixnum.zero(context.getRuntime());
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "sysseek", required = 1, optional = 1)
     public RubyFixnum sysseek(ThreadContext context, IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         long pos;
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             
             if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
                 throw context.getRuntime().newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 context.getRuntime().getWarnings().warn(ID.SYSSEEK_BUFFERED_IO, "sysseek for buffered IO");
             }
             
             pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return context.getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind(ThreadContext context) {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
             myOpenfile.getMainStream().lseek(0L, Stream.SEEK_SET);
             myOpenfile.getMainStream().clearerr();
             
             // TODO: This is some goofy global file value from MRI..what to do?
 //            if (io == current_file) {
 //                gets_lineno -= fptr->lineno;
 //            }
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOError(e.getMessage());
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(context.getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(runtime);
         
             Stream writeStream = myOpenFile.getWriteStream();
 
             writeStream.fflush();
             writeStream.sync();
 
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
 
         return RubyFixnum.zero(runtime);
     }
 
     /** Sets the current sync mode.
      * 
      * @param newSync The new sync mode.
      */
     @JRubyMethod(name = "sync=", required = 1)
     public IRubyObject sync_set(IRubyObject newSync) {
         getOpenFileChecked().setSync(newSync.isTrue());
         getOpenFileChecked().getMainStream().setSync(newSync.isTrue());
 
         return this;
     }
 
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStream().feof()) {
                 return runtime.getTrue();
             }
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 return runtime.getFalse();
             }
             
             readCheck(myOpenFile.getMainStream());
             waitReadable(myOpenFile.getMainStream());
             
             myOpenFile.getMainStream().clearerr();
             
             int c = myOpenFile.getMainStream().fgetc();
             
             if (c != -1) {
                 myOpenFile.getMainStream().ungetc(c);
                 return runtime.getFalse();
             }
             
             myOpenFile.checkClosed(runtime);
             
             myOpenFile.getMainStream().clearerr();
             
             return runtime.getTrue();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p(ThreadContext context) {
         return context.getRuntime().newBoolean(context.getRuntime().getPosix().isatty(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor()));
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         Ruby runtime = getRuntime();
         
         if (this == original) return this;
 
         RubyIO originalIO = (RubyIO) TypeConverter.convertToTypeWithCheck(original, runtime.getIO(), "to_io");
         
         OpenFile originalFile = originalIO.getOpenFileChecked();
         OpenFile newFile = openFile;
         
         try {
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
                 originalFile.getMainStream().lseek(0, Stream.SEEK_CUR);
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStream().fflush();
             } else {
                 originalFile.getMainStream().lseek(0, Stream.SEEK_CUR);
             }
 
             newFile.setMode(originalFile.getMode());
             newFile.setProcess(originalFile.getProcess());
             newFile.setLineNumber(originalFile.getLineNumber());
             newFile.setPath(originalFile.getPath());
             newFile.setFinalizer(originalFile.getFinalizer());
             
             ModeFlags modes;
             if (newFile.isReadable()) {
                 if (newFile.isWritable()) {
                     if (newFile.getPipeStream() != null) {
                         modes = new ModeFlags(ModeFlags.RDONLY);
                     } else {
                         modes = new ModeFlags(ModeFlags.RDWR);
                     }
                 } else {
                     modes = new ModeFlags(ModeFlags.RDONLY);
                 }
             } else {
                 if (newFile.isWritable()) {
                     modes = new ModeFlags(ModeFlags.WRONLY);
                 } else {
                     modes = originalFile.getMainStream().getModes();
                 }
             }
             
             ChannelDescriptor descriptor = originalFile.getMainStream().getDescriptor().dup();
 
             newFile.setMainStream(ChannelStream.fdopen(runtime, descriptor, modes));
 
             newFile.getMainStream().setSync(originalFile.getMainStream().isSync());
             
             // TODO: the rest of this...seeking to same position is unnecessary since we share a channel
             // but some of this may be needed?
             
 //    fseeko(fptr->f, ftello(orig->f), SEEK_SET);
 //    if (orig->f2) {
 //	if (fileno(orig->f) != fileno(orig->f2)) {
 //	    fd = ruby_dup(fileno(orig->f2));
 //	}
 //	fptr->f2 = rb_fdopen(fd, "w");
 //	fseeko(fptr->f2, ftello(orig->f2), SEEK_SET);
 //    }
 //    if (fptr->mode & FMODE_BINMODE) {
 //	rb_io_binmode(dest);
 //    }
         } catch (IOException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (PipeException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         } catch (InvalidValueException ex) {
             throw runtime.newIOError("could not init copy: " + ex);
         }
         
         return this;
     }
     
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isClosed());
     }
 
     /**
      * Is this IO closed
      * 
      * @return true if closed
      */
     public boolean isClosed() {
         return openFile.getMainStream() == null && openFile.getPipeStream() == null;
     }
 
     /** 
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         Ruby runtime = getRuntime();
         
         if (runtime.getSafeLevel() >= 4 && isTaint()) {
             throw runtime.newSecurityError("Insecure: can't close");
         }
         
         openFile.checkClosed(runtime);
         return close2(runtime);
     }
         
     protected IRubyObject close2(Ruby runtime) {
         if (openFile == null) return runtime.getNil();
         
         interruptBlockingThreads();
 
         /* FIXME: Why did we go to this trouble and not use these descriptors?
         ChannelDescriptor main, pipe;
         if (openFile.getPipeStream() != null) {
             pipe = openFile.getPipeStream().getDescriptor();
         } else {
             if (openFile.getMainStream() == null) {
                 return runtime.getNil();
             }
             pipe = null;
         }
         
         main = openFile.getMainStream().getDescriptor(); */
         
         // cleanup, raising errors if any
         openFile.cleanup(runtime, true);
         
         // TODO: notify threads waiting on descriptors/IO? probably not...
         
         if (openFile.getProcess() != null) {
             obliterateProcess(openFile.getProcess());
             IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(runtime, openFile.getProcess().exitValue());
             runtime.getCurrentContext().setLastExitStatus(processResult);
         }
         
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write(ThreadContext context) {
         try {
             if (context.getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw context.getRuntime().newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isReadable()) {
                 throw context.getRuntime().newIOError("closing non-duplex IO for writing");
             }
             
             if (myOpenFile.getPipeStream() == null) {
                 close();
             } else{
                 myOpenFile.getPipeStream().fclose();
                 myOpenFile.setPipeStream(null);
                 myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.WRITABLE);
                 // TODO
                 // n is result of fclose; but perhaps having a SysError below is enough?
                 // if (n != 0) rb_sys_fail(fptr->path);
             }
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (IOException ioe) {
             // hmmmm
         }
         return this;
     }
 
     @JRubyMethod(name = "close_read")
     public IRubyObject close_read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         
         try {
             if (runtime.getSafeLevel() >= 4 && isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isWritable()) {
                 throw runtime.newIOError("closing non-duplex IO for reading");
             }
             
             if (myOpenFile.getPipeStream() == null) {
                 close();
             } else{
                 myOpenFile.getMainStream().fclose();
                 myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.READABLE);
                 myOpenFile.setMainStream(myOpenFile.getPipeStream());
                 myOpenFile.setPipeStream(null);
                 // TODO
                 // n is result of fclose; but perhaps having a SysError below is enough?
                 // if (n != 0) rb_sys_fail(fptr->path);
             }
         } catch (BadDescriptorException bde) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException ioe) {
             // I believe Ruby bails out with a "bug" if closing fails
             throw runtime.newIOErrorFromException(ioe);
         }
         return this;
     }
 
     /** Flushes the IO output stream.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "flush")
     public RubyIO flush() {
         try { 
             getOpenFileChecked().getWriteStream().fflush();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         return this;
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime.getRecordSeparatorVar().get()));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_8)
     public IRubyObject gets(ThreadContext context, IRubyObject separatorArg) {
         IRubyObject result = getline(context.getRuntime(), separator(separatorArg));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         IRubyObject result = getline(runtime, separator(runtime));
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context, IRubyObject arg) {
         ByteList separator;
         long limit = -1;
         if (arg instanceof RubyInteger) {
             limit = RubyInteger.fix2long(arg);
             separator = separator(context.getRuntime());
         } else {
             separator = separator(arg);
         }
 
         IRubyObject result = getline(context.getRuntime(), separator, limit);
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     @JRubyMethod(name = "gets", writes = FrameField.LASTLINE, compat = RUBY1_9)
     public IRubyObject gets19(ThreadContext context, IRubyObject separator, IRubyObject limit_arg) {
         long limit = limit_arg.isNil() ? -1 : RubyNumeric.fix2long(limit_arg);
         IRubyObject result = getline(context.getRuntime(), separator(separator), limit);
 
         if (!result.isNil()) context.getCurrentScope().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
         return ((ChannelStream) openFile.getMainStream()).isBlocking();
     }
 
     @JRubyMethod(name = "fcntl")
     public IRubyObject fcntl(ThreadContext context, IRubyObject cmd) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(context.getRuntime(), cmd, null);
     }
 
     @JRubyMethod(name = "fcntl")
     public IRubyObject fcntl(ThreadContext context, IRubyObject cmd, IRubyObject arg) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(context.getRuntime(), cmd, arg);
     }
 
     @JRubyMethod(name = "ioctl", required = 1, optional = 1)
     public IRubyObject ioctl(ThreadContext context, IRubyObject[] args) {
         IRubyObject cmd = args[0];
         IRubyObject arg;
         
         if (args.length == 2) {
             arg = args[1];
         } else {
             arg = context.getRuntime().getNil();
         }
         
         return ctl(context.getRuntime(), cmd, arg);
     }
 
     public IRubyObject ctl(Ruby runtime, IRubyObject cmd, IRubyObject arg) {
         long realCmd = cmd.convertToInteger().getLongValue();
         long nArg = 0;
         
         // FIXME: Arg may also be true, false, and nil and still be valid.  Strangely enough, 
         // protocol conversion is not happening in Ruby on this arg?
         if (arg == null || arg.isNil() || arg == runtime.getFalse()) {
             nArg = 0;
         } else if (arg instanceof RubyFixnum) {
             nArg = RubyFixnum.fix2long(arg);
         } else if (arg == runtime.getTrue()) {
             nArg = 1;
         } else {
             throw runtime.newNotImplementedError("JRuby does not support string for second fcntl/ioctl argument yet");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
 
         // Fixme: Only F_SETFL and F_GETFL is current supported
         // FIXME: Only NONBLOCK flag is supported
         // FIXME: F_SETFL and F_SETFD are treated as the same thing here.  For the case of dup(fd) we
         //   should actually have F_SETFL only affect one (it is unclear how well we do, but this TODO
         //   is here to at least document that we might need to do more work here.  Mostly SETFL is
         //   for mode changes which should persist across fork() boundaries.  Since JVM has no fork
         //   this is not a problem for us.
         if (realCmd == FcntlLibrary.FD_CLOEXEC) {
             // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
             // And why the hell does webrick pass this in as a first argument!!!!!
         } else if (realCmd == Fcntl.F_SETFL.value() || realCmd == Fcntl.F_SETFD.value()) {
             if ((nArg & FcntlLibrary.FD_CLOEXEC) == FcntlLibrary.FD_CLOEXEC) {
                 // Do nothing.  FD_CLOEXEC has no meaning in JVM since we cannot really exec.
             } else {
                 try {
                     boolean block = (nArg & ModeFlags.NONBLOCK) != ModeFlags.NONBLOCK;
 
                     myOpenFile.getMainStream().setBlocking(block);
                 } catch (IOException e) {
                     throw runtime.newIOError(e.getMessage());
                 }
             }
         } else if (realCmd == Fcntl.F_GETFL.value()) {
             return myOpenFile.getMainStream().isBlocking() ? RubyFixnum.zero(runtime) : RubyFixnum.newFixnum(runtime, ModeFlags.NONBLOCK);
         } else {
             throw runtime.newNotImplementedError("JRuby only supports F_SETFL and F_GETFL with NONBLOCK for fcntl/ioctl");
         }
         
diff --git a/src/org/jruby/util/io/CRLFStreamWrapper.java b/src/org/jruby/util/io/CRLFStreamWrapper.java
index c36d9857da..23c3c57905 100644
--- a/src/org/jruby/util/io/CRLFStreamWrapper.java
+++ b/src/org/jruby/util/io/CRLFStreamWrapper.java
@@ -1,246 +1,254 @@
 package org.jruby.util.io;
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.Channel;
 import org.jruby.Ruby;
 import org.jruby.util.ByteList;
 
 /**
  * Wrapper around Stream that packs and unpacks LF <=> CRLF.
  * @author nicksieger
  */
 public class CRLFStreamWrapper implements Stream {
     private final Stream stream;
     private boolean binmode = false;
     private static final int CR = 13;
     private static final int LF = 10;
 
     public CRLFStreamWrapper(Stream stream) {
         this.stream = stream;
     }
 
     public ChannelDescriptor getDescriptor() {
         return stream.getDescriptor();
     }
 
     public void clearerr() {
         stream.clearerr();
     }
 
     public ModeFlags getModes() {
         return stream.getModes();
     }
 
     public boolean isSync() {
         return stream.isSync();
     }
 
     public void setSync(boolean sync) {
         stream.setSync(sync);
     }
 
     public void setBinmode() {
         binmode = true;
         stream.setBinmode();
     }
 
+    public boolean isAutoclose() {
+        return stream.isAutoclose();
+    }
+
+    public void setAutoclose(boolean autoclose) {
+        stream.setAutoclose(autoclose);
+    }
+
     public ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException, EOFException {
         return convertCRLFToLF(stream.fgets(separatorString));
     }
 
     public ByteList readall() throws IOException, BadDescriptorException, EOFException {
         return convertCRLFToLF(stream.readall());
     }
 
     public int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {
         if (binmode) {
             return stream.getline(dst, terminator);
         }
 
         ByteList intermediate = new ByteList();
         int result = stream.getline(intermediate, terminator);
         convertCRLFToLF(intermediate, dst);
         return result;
     }
 
     public int getline(ByteList dst, byte terminator, long limit) throws IOException, BadDescriptorException {
         if (binmode) {
             return stream.getline(dst, terminator, limit);
         }
 
         ByteList intermediate = new ByteList();
         int result = stream.getline(intermediate, terminator, limit);
         convertCRLFToLF(intermediate, dst);
         return result;
     }
 
     public ByteList fread(int number) throws IOException, BadDescriptorException, EOFException {
         if (number == 0) {
             if (stream.feof()) {
                 return null;
             } else {
                 return new ByteList(0);
             }
         }
         boolean eof = false;
         ByteList bl = new ByteList(number > ChannelStream.BUFSIZE ? ChannelStream.BUFSIZE : number);
         for (int i = 0; i < number; i++) {
             int c = fgetc();
             if (c == -1) {
                 eof = true;
                 break;
             }
             bl.append(c);
         }
         if (eof && bl.length() == 0) {
             return null;
         }
         return bl;
     }
 
     public int fwrite(ByteList string) throws IOException, BadDescriptorException {
         return stream.fwrite(convertLFToCRLF(string));
     }
 
     public int fgetc() throws IOException, BadDescriptorException, EOFException {
         int c = stream.fgetc();
         if (!binmode && c == CR) {
             c = stream.fgetc();
             if (c != LF) {
                 stream.ungetc(c);
                 return CR;
             }
         }
         return c;
     }
 
     public int ungetc(int c) {
         return stream.ungetc(c);
     }
 
     public void fputc(int c) throws IOException, BadDescriptorException {
         if (!binmode && c == LF) {
             stream.fputc(CR);
         }
         stream.fputc(c);
     }
 
     public ByteList read(int number) throws IOException, BadDescriptorException, EOFException {
         return convertCRLFToLF(stream.read(number));
     }
 
     public void fclose() throws IOException, BadDescriptorException {
         stream.fclose();
     }
 
     public int fflush() throws IOException, BadDescriptorException {
         return stream.fflush();
     }
 
     public void sync() throws IOException, BadDescriptorException {
         stream.sync();
     }
 
     public boolean feof() throws IOException, BadDescriptorException {
         return stream.feof();
     }
 
     public long fgetpos() throws IOException, PipeException, BadDescriptorException, InvalidValueException {
         return stream.fgetpos();
     }
 
     public void lseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         stream.lseek(offset, type);
     }
 
     public void ftruncate(long newLength) throws IOException, PipeException, InvalidValueException, BadDescriptorException {
         stream.ftruncate(newLength);
     }
 
     public int ready() throws IOException {
         return stream.ready();
     }
 
     public void waitUntilReady() throws IOException, InterruptedException {
         stream.waitUntilReady();
     }
 
     public boolean readDataBuffered() {
         return stream.readDataBuffered();
     }
 
     public boolean writeDataBuffered() {
         return stream.writeDataBuffered();
     }
 
     public InputStream newInputStream() {
         return stream.newInputStream();
     }
 
     public OutputStream newOutputStream() {
         return stream.newOutputStream();
     }
 
     public boolean isBlocking() {
         return stream.isBlocking();
     }
 
     public void setBlocking(boolean blocking) throws IOException {
         stream.setBlocking(blocking);
     }
 
     public void freopen(Ruby runtime, String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         stream.freopen(runtime, path, modes);
     }
 
     private ByteList convertCRLFToLF(ByteList input) {
         if (input == null) {
             return null;
         }
 
         if (binmode) {
             return input;
         }
 
         ByteList result = new ByteList();
         convertCRLFToLF(input, result);
         return result;
     }
 
     private void convertCRLFToLF(ByteList src, ByteList dst) {
         for (int i = 0; i < src.length(); i++) {
             int b = src.get(i);
             if (b == CR && i + 1 < src.length() && src.get(i + 1) == LF) {
                 continue;
             }
             dst.append(b);
         }
     }
 
     private ByteList convertLFToCRLF(ByteList input) {
         if (input == null) {
             return null;
         }
 
         if (binmode) {
             return input;
         }
 
         ByteList result = new ByteList();
         for (int i = 0; i < input.length(); i++) {
             int b = input.get(i);
             if (b == LF) {
                 result.append(CR);
             }
             result.append(b);
         }
         return result;
     }
 
     public Channel getChannel() {
         return stream.getChannel();
     }
 }
diff --git a/src/org/jruby/util/io/ChannelStream.java b/src/org/jruby/util/io/ChannelStream.java
index 4e25a16876..40e54357ef 100644
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
@@ -1,1611 +1,1629 @@
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
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 package org.jruby.util.io;
 
 import static java.util.logging.Logger.getLogger;
 
 import java.io.EOFException;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.RandomAccessFile;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.FileChannel;
 import java.nio.channels.IllegalBlockingModeException;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 
 import org.jruby.Finalizable;
 import org.jruby.Ruby;
 import org.jruby.platform.Platform;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 
 import java.nio.channels.spi.SelectorProvider;
 
 /**
  * This file implements a seekable IO file.
  */
 public class ChannelStream implements Stream, Finalizable {
     private final static boolean DEBUG = false;
 
     /**
      * The size of the read/write buffer allocated for this stream.
      *
      * This size has been scaled back from its original 16k because although
      * the larger buffer size results in raw File.open times being rather slow
      * (due to the cost of instantiating a relatively large buffer). We should
      * try to find a happy medium, or potentially pool buffers, or perhaps even
      * choose a value based on platform(??), but for now I am reducing it along
      * with changes for the "large read" patch from JRUBY-2657.
      */
     public final static int BUFSIZE = 4 * 1024;
 
     /**
      * The size at which a single read should turn into a chunkier bulk read.
      * Currently, this size is about 4x a normal buffer size.
      *
      * This size was not really arrived at experimentally, and could potentially
      * be increased. However, it seems like a "good size" and we should
      * probably only adjust it if it turns out we would perform better with a
      * larger buffer for large bulk reads.
      */
     private final static int BULK_READ_SIZE = 16 * 1024;
     private final static ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
 
     private volatile Ruby runtime;
     protected ModeFlags modes;
     protected boolean sync = false;
 
     protected volatile ByteBuffer buffer; // r/w buffer
     protected boolean reading; // are we reading or writing?
     private ChannelDescriptor descriptor;
     private boolean blocking = true;
     protected int ungotc = -1;
     private volatile boolean closedExplicitly = false;
 
     private volatile boolean eof = false;
+    private volatile boolean autoclose = true;
 
-    private ChannelStream(Ruby runtime, ChannelDescriptor descriptor) {
+    private ChannelStream(Ruby runtime, ChannelDescriptor descriptor, boolean autoclose) {
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = descriptor.getOriginalModes();
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
+        this.autoclose = autoclose;
         runtime.addInternalFinalizer(this);
     }
 
-    private ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
-        this(runtime, descriptor);
+    private ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes, boolean autoclose) throws InvalidValueException {
+        this(runtime, descriptor, autoclose);
         descriptor.checkNewModes(modes);
         this.modes = modes;
     }
 
     public Ruby getRuntime() {
         return runtime;
     }
 
     public void checkReadable() throws IOException {
         if (!modes.isReadable()) throw new IOException("not opened for reading");
     }
 
     public void checkWritable() throws IOException {
         if (!modes.isWritable()) throw new IOException("not opened for writing");
     }
 
     public void checkPermissionsSubsetOf(ModeFlags subsetModes) {
         subsetModes.isSubsetOf(modes);
     }
 
     public ModeFlags getModes() {
     	return modes;
     }
 
     public boolean isSync() {
         return sync;
     }
 
     public void setSync(boolean sync) {
         this.sync = sync;
     }
 
     public void setBinmode() {
         // No-op here, no binmode handling needed.
     }
 
+    public boolean isAutoclose() {
+        return autoclose;
+    }
+
+    public void setAutoclose(boolean autoclose) {
+        this.autoclose = autoclose;
+    }
+
     /**
      * Implement IO#wait as per io/wait in MRI.
      * waits until input available or timed out and returns self, or nil when EOF reached.
      *
      * The default implementation loops while ready returns 0.
      */
     public void waitUntilReady() throws IOException, InterruptedException {
         while (ready() == 0) {
             Thread.sleep(10);
         }
     }
 
     public boolean readDataBuffered() {
         return reading && (ungotc != -1 || buffer.hasRemaining());
     }
 
     public boolean writeDataBuffered() {
         return !reading && buffer.position() > 0;
     }
     private final int refillBuffer() throws IOException {
         buffer.clear();
         int n = ((ReadableByteChannel) descriptor.getChannel()).read(buffer);
         buffer.flip();
         return n;
     }
     public synchronized ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         if (separatorString == null) {
             return readall();
         }
 
         final ByteList separator = (separatorString == PARAGRAPH_DELIMETER) ?
             PARAGRAPH_SEPARATOR : separatorString;
 
         descriptor.checkOpen();
 
         if (feof()) {
             return null;
         }
 
         int c = read();
 
         if (c == -1) {
             return null;
         }
 
         // unread back
         buffer.position(buffer.position() - 1);
 
         ByteList buf = new ByteList(40);
 
         byte first = separator.getUnsafeBytes()[separator.getBegin()];
 
         LineLoop : while (true) {
             ReadLoop: while (true) {
                 byte[] bytes = buffer.array();
                 int offset = buffer.position();
                 int max = buffer.limit();
 
                 // iterate over remainder of buffer until we find a match
                 for (int i = offset; i < max; i++) {
                     c = bytes[i];
                     if (c == first) {
                         // terminate and advance buffer when we find our char
                         buf.append(bytes, offset, i - offset);
                         if (i >= max) {
                             buffer.clear();
                         } else {
                             buffer.position(i + 1);
                         }
                         break ReadLoop;
                     }
                 }
 
                 // no match, append remainder of buffer and continue with next block
                 buf.append(bytes, offset, buffer.remaining());
                 int read = refillBuffer();
                 if (read == -1) break LineLoop;
             }
 
             // found a match above, check if remaining separator characters match, appending as we go
             for (int i = 0; i < separator.getRealSize(); i++) {
                 if (c == -1) {
                     break LineLoop;
                 } else if (c != separator.getUnsafeBytes()[separator.getBegin() + i]) {
                     buf.append(c);
                     continue LineLoop;
                 }
                 buf.append(c);
                 if (i < separator.getRealSize() - 1) {
                     c = read();
                 }
             }
             break;
         }
 
         if (separatorString == PARAGRAPH_DELIMETER) {
             while (c == separator.getUnsafeBytes()[separator.getBegin()]) {
                 c = read();
             }
             ungetc(c);
         }
 
         return buf;
     }
 
     public synchronized int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         descriptor.checkOpen();
 
         int totalRead = 0;
         boolean found = false;
         if (ungotc != -1) {
             dst.append((byte) ungotc);
             found = ungotc == terminator;
             ungotc = -1;
             ++totalRead;
         }
         while (!found) {
             final byte[] bytes = buffer.array();
             final int begin = buffer.arrayOffset() + buffer.position();
             final int end = begin + buffer.remaining();
             int len = 0;
             for (int i = begin; i < end && !found; ++i) {
                 found = bytes[i] == terminator;
                 ++len;
             }
             if (len > 0) {
                 dst.append(buffer, len);
                 totalRead += len;
             }
             if (!found) {
                 int n = refillBuffer();
                 if (n <= 0) {
                     if (n < 0 && totalRead < 1) {
                         return -1;
                     }
                     break;
                 }
             }
         }
         return totalRead;
     }
 
     public synchronized int getline(ByteList dst, byte terminator, long limit) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         descriptor.checkOpen();
 
         int totalRead = 0;
         boolean found = false;
         if (ungotc != -1) {
             dst.append((byte) ungotc);
             found = ungotc == terminator;
             ungotc = -1;
             limit--;
             ++totalRead;
         }
         while (!found) {
             final byte[] bytes = buffer.array();
             final int begin = buffer.arrayOffset() + buffer.position();
             final int end = begin + buffer.remaining();
             int len = 0;
             for (int i = begin; i < end && limit-- > 0 && !found; ++i) {
                 found = bytes[i] == terminator;
                 ++len;
             }
             if (limit < 1) found = true;
 
             if (len > 0) {
                 dst.append(buffer, len);
                 totalRead += len;
             }
             if (!found) {
                 int n = refillBuffer();
                 if (n <= 0) {
                     if (n < 0 && totalRead < 1) {
                         return -1;
                     }
                     break;
                 }
             }
         }
         return totalRead;
     }
 
     /**
      * @deprecated readall do busy loop for the IO which has NONBLOCK bit. You
      *             should implement the logic by yourself with fread().
      */
     @Deprecated
     public synchronized ByteList readall() throws IOException, BadDescriptorException {
         final long fileSize = descriptor.isSeekable() && descriptor.getChannel() instanceof FileChannel
                 ? ((FileChannel) descriptor.getChannel()).size() : 0;
         //
         // Check file size - special files in /proc have zero size and need to be
         // handled by the generic read path.
         //
         if (fileSize > 0) {
             ensureRead();
 
             FileChannel channel = (FileChannel)descriptor.getChannel();
             final long left = fileSize - channel.position() + bufferedInputBytesRemaining();
             if (left <= 0) {
                 eof = true;
                 return null;
             }
 
             if (left > Integer.MAX_VALUE) {
                 if (getRuntime() != null) {
                     throw getRuntime().newIOError("File too large");
                 } else {
                     throw new IOException("File too large");
                 }
             }
 
             ByteList result = new ByteList((int) left);
             ByteBuffer buf = ByteBuffer.wrap(result.getUnsafeBytes(),
                     result.begin(), (int) left);
 
             //
             // Copy any buffered data (including ungetc byte)
             //
             copyBufferedBytes(buf);
 
             //
             // Now read unbuffered directly from the file
             //
             while (buf.hasRemaining()) {
                 final int MAX_READ_CHUNK = 1 * 1024 * 1024;
                 //
                 // When reading into a heap buffer, the jvm allocates a temporary
                 // direct ByteBuffer of the requested size.  To avoid allocating
                 // a huge direct buffer when doing ludicrous reads (e.g. 1G or more)
                 // we split the read up into chunks of no more than 1M
                 //
                 ByteBuffer tmp = buf.duplicate();
                 if (tmp.remaining() > MAX_READ_CHUNK) {
                     tmp.limit(tmp.position() + MAX_READ_CHUNK);
                 }
                 int n = channel.read(tmp);
                 if (n <= 0) {
                     break;
                 }
                 buf.position(tmp.position());
             }
             eof = true;
             result.length(buf.position());
             return result;
         } else if (descriptor.isNull()) {
             return new ByteList(0);
         } else {
             checkReadable();
 
             ByteList byteList = new ByteList();
             ByteList read = fread(BUFSIZE);
 
             if (read == null) {
                 eof = true;
                 return byteList;
             }
 
             while (read != null) {
                 byteList.append(read);
                 read = fread(BUFSIZE);
             }
 
             return byteList;
         }
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteBuffer</tt> to place the data in.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(ByteBuffer dst) {
         final int bytesToCopy = dst.remaining();
 
         if (ungotc != -1 && dst.hasRemaining()) {
             dst.put((byte) ungotc);
             ungotc = -1;
         }
 
         if (buffer.hasRemaining() && dst.hasRemaining()) {
 
             if (dst.remaining() >= buffer.remaining()) {
                 //
                 // Copy out any buffered bytes
                 //
                 dst.put(buffer);
 
             } else {
                 //
                 // Need to clamp source (buffer) size to avoid overrun
                 //
                 ByteBuffer tmp = buffer.duplicate();
                 tmp.limit(tmp.position() + dst.remaining());
                 dst.put(tmp);
                 buffer.position(tmp.position());
             }
         }
 
         return bytesToCopy - dst.remaining();
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteBuffer</tt> to place the data in.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(byte[] dst, int off, int len) {
         int bytesCopied = 0;
 
         if (ungotc != -1 && len > 0) {
             dst[off++] = (byte) ungotc;
             ungotc = -1;
             ++bytesCopied;
         }
 
         final int n = Math.min(len - bytesCopied, buffer.remaining());
         buffer.get(dst, off, n);
         bytesCopied += n;
 
         return bytesCopied;
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteList</tt> to place the data in.
      * @param len The maximum number of bytes to copy.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(ByteList dst, int len) {
         int bytesCopied = 0;
 
         dst.ensure(Math.min(len, bufferedInputBytesRemaining()));
 
         if (bytesCopied < len && ungotc != -1) {
             ++bytesCopied;
             dst.append((byte) ungotc);
             ungotc = -1;
         }
 
         //
         // Copy out any buffered bytes
         //
         if (bytesCopied < len && buffer.hasRemaining()) {
             int n = Math.min(buffer.remaining(), len - bytesCopied);
             dst.append(buffer, n);
             bytesCopied += n;
         }
 
         return bytesCopied;
     }
 
     /**
      * Returns a count of how many bytes are available in the read buffer
      *
      * @return The number of bytes that can be read without reading the underlying stream.
      */
     private final int bufferedInputBytesRemaining() {
         return reading ? (buffer.remaining() + (ungotc != -1 ? 1 : 0)) : 0;
     }
 
     /**
      * Tests if there are bytes remaining in the read buffer.
      *
      * @return <tt>true</tt> if there are bytes available in the read buffer.
      */
     private final boolean hasBufferedInputBytes() {
         return reading && (buffer.hasRemaining() || ungotc != -1);
     }
 
     /**
      * Returns a count of how many bytes of space is available in the write buffer.
      *
      * @return The number of bytes that can be written to the buffer without flushing
      * to the underlying stream.
      */
     private final int bufferedOutputSpaceRemaining() {
         return !reading ? buffer.remaining() : 0;
     }
 
     /**
      * Tests if there is space available in the write buffer.
      *
      * @return <tt>true</tt> if there are bytes available in the write buffer.
      */
     private final boolean hasBufferedOutputSpace() {
         return !reading && buffer.hasRemaining();
     }
 
     /**
      * Closes IO handler resources.
      *
      * @throws IOException
      * @throws BadDescriptorException
      */
     public void fclose() throws IOException, BadDescriptorException {
         try {
             synchronized (this) {
                 closedExplicitly = true;
                 close(); // not closing from finalize
             }
         } finally {
             Ruby localRuntime = getRuntime();
 
             // Make sure we remove finalizers while not holding self lock,
             // otherwise there is a possibility for a deadlock!
             if (localRuntime != null) localRuntime.removeInternalFinalizer(this);
 
             // clear runtime so it doesn't get stuck in memory (JRUBY-2933)
             runtime = null;
         }
     }
 
     /**
      * Internal close.
      *
      * @throws IOException
      * @throws BadDescriptorException
      */
     private void close() throws IOException, BadDescriptorException {
         // finish and close ourselves
         finish(true);
     }
 
     private void finish(boolean close) throws BadDescriptorException, IOException {
         try {
             flushWrite();
 
             if (DEBUG) getLogger("ChannelStream").info("Descriptor for fileno "
                     + descriptor.getFileno() + " closed by stream");
         } finally {
             buffer = EMPTY_BUFFER;
 
             // clear runtime so it doesn't get stuck in memory (JRUBY-2933)
             runtime = null;
 
             // finish descriptor
             descriptor.finish(close);
         }
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      */
     public synchronized int fflush() throws IOException, BadDescriptorException {
         checkWritable();
         try {
             flushWrite();
         } catch (EOFException eofe) {
             return -1;
         }
         return 0;
     }
 
     /**
      * Flush the write buffer to the channel (if needed)
      * @throws IOException
      */
     private void flushWrite() throws IOException, BadDescriptorException {
         if (reading || !modes.isWritable() || buffer.position() == 0) return; // Don't bother
 
         int len = buffer.position();
         buffer.flip();
         int n = descriptor.write(buffer);
 
         if(n != len) {
             // TODO: check the return value here
         }
         buffer.clear();
     }
 
     /**
      * Flush the write buffer to the channel (if needed)
      * @throws IOException
      */
     private boolean flushWrite(final boolean block) throws IOException, BadDescriptorException {
         if (reading || !modes.isWritable() || buffer.position() == 0) return false; // Don't bother
         int len = buffer.position();
         int nWritten = 0;
         buffer.flip();
 
         // For Sockets, only write as much as will fit.
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     if (oldBlocking != block) {
                         selectableChannel.configureBlocking(block);
                     }
                     nWritten = descriptor.write(buffer);
                 } finally {
                     if (oldBlocking != block) {
                         selectableChannel.configureBlocking(oldBlocking);
                     }
                 }
             }
         } else {
             nWritten = descriptor.write(buffer);
         }
         if (nWritten != len) {
             buffer.compact();
             return false;
         }
         buffer.clear();
         return true;
     }
 
     /**
      * @see org.jruby.util.IOHandler#getInputStream()
      */
     public InputStream newInputStream() {
         InputStream in = descriptor.getBaseInputStream();
         return in == null ? new InputStreamAdapter(this) : in;
     }
 
     /**
      * @see org.jruby.util.IOHandler#getOutputStream()
      */
     public OutputStream newOutputStream() {
         return new OutputStreamAdapter(this);
     }
 
     public void clearerr() {
         eof = false;
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#isEOF()
      */
     public boolean feof() throws IOException, BadDescriptorException {
         checkReadable();
 
         if (eof) {
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * @throws IOException
      * @see org.jruby.util.IOHandler#pos()
      */
     public synchronized long fgetpos() throws IOException, PipeException, InvalidValueException, BadDescriptorException {
         // Correct position for read / write buffering (we could invalidate, but expensive)
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             long pos = fileChannel.position();
             // Adjust for buffered data
             if (reading) {
                 pos -= buffer.remaining();
                 return pos - (pos > 0 && ungotc != -1 ? 1 : 0);
             } else {
                 return pos + buffer.position();
             }
         } else if (descriptor.isNull()) {
             return 0;
         } else {
             throw new PipeException();
         }
     }
 
     /**
      * Implementation of libc "lseek", which seeks on seekable streams, raises
      * EPIPE if the fd is assocated with a pipe, socket, or FIFO, and doesn't
      * do anything for other cases (like stdio).
      *
      * @throws IOException
      * @throws InvalidValueException
      * @see org.jruby.util.IOHandler#seek(long, int)
      */
     public synchronized void lseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             ungotc = -1;
             int adj = 0;
             if (reading) {
                 // for SEEK_CUR, need to adjust for buffered data
                 adj = buffer.remaining();
                 buffer.clear();
                 buffer.flip();
             } else {
                 flushWrite();
             }
             try {
                 switch (type) {
                 case SEEK_SET:
                     fileChannel.position(offset);
                     break;
                 case SEEK_CUR:
                     fileChannel.position(fileChannel.position() - adj + offset);
                     break;
                 case SEEK_END:
                     fileChannel.position(fileChannel.size() + offset);
                     break;
                 }
             } catch (IllegalArgumentException e) {
                 throw new InvalidValueException();
             } catch (IOException ioe) {
                 throw ioe;
             }
         } else if (descriptor.getChannel() instanceof SelectableChannel) {
             // TODO: It's perhaps just a coincidence that all the channels for
             // which we should raise are instanceof SelectableChannel, since
             // stdio is not...so this bothers me slightly. -CON
             throw new PipeException();
         } else {
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#sync()
      */
     public synchronized void sync() throws IOException, BadDescriptorException {
         flushWrite();
     }
 
     /**
      * Ensure buffer is ready for reading, flushing remaining writes if required
      * @throws IOException
      */
     private void ensureRead() throws IOException, BadDescriptorException {
         if (reading) return;
         flushWrite();
         buffer.clear();
         buffer.flip();
         reading = true;
     }
 
     /**
      * Ensure buffer is ready for reading, flushing remaining writes if required
      * @throws IOException
      */
     private void ensureReadNonBuffered() throws IOException, BadDescriptorException {
         if (reading) {
             if (buffer.hasRemaining()) {
                 Ruby localRuntime = getRuntime();
                 if (localRuntime != null) {
                     throw localRuntime.newIOError("sysread for buffered IO");
                 } else {
                     throw new IOException("sysread for buffered IO");
                 }
             }
         } else {
             // libc flushes writes on any read from the actual file, so we flush here
             flushWrite();
             buffer.clear();
             buffer.flip();
             reading = true;
         }
     }
 
     private void resetForWrite() throws IOException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (buffer.hasRemaining()) { // we have read ahead, and need to back up
                 fileChannel.position(fileChannel.position() - buffer.remaining());
             }
         }
         // FIXME: Clearing read buffer here...is this appropriate?
         buffer.clear();
         reading = false;
     }
 
     /**
      * Ensure buffer is ready for writing.
      * @throws IOException
      */
     private void ensureWrite() throws IOException {
         if (!reading) return;
         resetForWrite();
     }
 
     public synchronized ByteList read(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureReadNonBuffered();
 
         ByteList byteList = new ByteList(number);
 
         // TODO this should entry into error handling somewhere
         int bytesRead = descriptor.read(number, byteList);
 
         if (bytesRead == -1) {
             eof = true;
         }
 
         return byteList;
     }
 
     private ByteList bufferedRead(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         int resultSize = 0;
 
         // 128K seems to be the minimum at which the stat+seek is faster than reallocation
         final int BULK_THRESHOLD = 128 * 1024;
         if (number >= BULK_THRESHOLD && descriptor.isSeekable() && descriptor.getChannel() instanceof FileChannel) {
             //
             // If it is a file channel, then we can pre-allocate the output buffer
             // to the total size of buffered + remaining bytes in file
             //
             FileChannel fileChannel = (FileChannel) descriptor.getChannel();
             resultSize = (int) Math.min(fileChannel.size() - fileChannel.position() + bufferedInputBytesRemaining(), number);
         } else {
             //
             // Cannot discern the total read length - allocate at least enough for the buffered data
             //
             resultSize = Math.min(bufferedInputBytesRemaining(), number);
         }
 
         ByteList result = new ByteList(resultSize);
         bufferedRead(result, number);
         return result;
     }
 
     private int bufferedRead(ByteList dst, int number) throws IOException, BadDescriptorException {
 
         int bytesRead = 0;
 
         //
         // Copy what is in the buffer, if there is some buffered data
         //
         bytesRead += copyBufferedBytes(dst, number);
 
         boolean done = false;
         //
         // Avoid double-copying for reads that are larger than the buffer size
         //
         while ((number - bytesRead) >= BUFSIZE) {
             //
             // limit each iteration to a max of BULK_READ_SIZE to avoid over-size allocations
             //
             final int bytesToRead = Math.min(BULK_READ_SIZE, number - bytesRead);
             final int n = descriptor.read(bytesToRead, dst);
             if (n == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (n == 0) {
                 done = true;
                 break;
             }
             bytesRead += n;
         }
 
         //
         // Complete the request by filling the read buffer first
         //
         while (!done && bytesRead < number) {
             int read = refillBuffer();
 
             if (read == -1) {
                 eof = true;
                 break;
             } else if (read == 0) {
                 break;
             }
 
             // append what we read into our buffer and allow the loop to continue
             final int len = Math.min(buffer.remaining(), number - bytesRead);
             dst.append(buffer, len);
             bytesRead += len;
         }
 
         if (bytesRead == 0 && number != 0) {
             if (eof) {
                 throw new EOFException();
             }
         }
 
         return bytesRead;
     }
 
     private int bufferedRead(ByteBuffer dst, boolean partial) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         boolean done = false;
         int bytesRead = 0;
 
         //
         // Copy what is in the buffer, if there is some buffered data
         //
         bytesRead += copyBufferedBytes(dst);
 
         //
         // Avoid double-copying for reads that are larger than the buffer size, or
         // the destination is a direct buffer.
         //
         while ((bytesRead < 1 || !partial) && (dst.remaining() >= BUFSIZE || dst.isDirect())) {
             ByteBuffer tmpDst = dst;
             if (!dst.isDirect()) {
                 //
                 // We limit reads to BULK_READ_SIZED chunks to avoid NIO allocating
                 // a huge temporary native buffer, when doing reads into a heap buffer
                 // If the dst buffer is direct, then no need to limit.
                 //
                 int bytesToRead = Math.min(BULK_READ_SIZE, dst.remaining());
                 if (bytesToRead < dst.remaining()) {
                     tmpDst = dst.duplicate();
                     tmpDst.limit(tmpDst.position() + bytesToRead);
                 }
             }
             int n = descriptor.read(tmpDst);
             if (n == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (n == 0) {
                 done = true;
                 break;
             } else {
                 bytesRead += n;
             }
         }
 
         //
         // Complete the request by filling the read buffer first
         //
         while (!done && dst.hasRemaining() && (bytesRead < 1 || !partial)) {
             int read = refillBuffer();
 
             if (read == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (read == 0) {
                 done = true;
                 break;
             } else {
                 // append what we read into our buffer and allow the loop to continue
                 bytesRead += copyBufferedBytes(dst);
             }
         }
 
         if (eof && bytesRead == 0 && dst.remaining() != 0) {
             throw new EOFException();
         }
 
         return bytesRead;
     }
 
     private int bufferedRead() throws IOException, BadDescriptorException {
         ensureRead();
 
         if (!buffer.hasRemaining()) {
             int len = refillBuffer();
             if (len == -1) {
                 eof = true;
                 return -1;
             } else if (len == 0) {
                 return -1;
             }
         }
         return buffer.get() & 0xFF;
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(ByteList buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
 
         if (buf.length() > buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
 
 
             int n = descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
             if(n != buf.length()) {
                 // TODO: check the return value here
             }
         } else {
             if (buf.length() > buffer.remaining()) flushWrite();
 
             buffer.put(buf.getUnsafeBytes(), buf.begin(), buf.length());
         }
 
         if (isSync()) flushWrite();
 
         return buf.getRealSize();
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(ByteBuffer buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || !buf.hasRemaining()) return 0;
 
         final int nbytes = buf.remaining();
         if (nbytes >= buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
 
             descriptor.write(buf);
             // TODO: check the return value here
         } else {
             if (nbytes > buffer.remaining()) flushWrite();
 
             buffer.put(buf);
         }
 
         if (isSync()) flushWrite();
 
         return nbytes - buf.remaining();
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(int c) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         if (!buffer.hasRemaining()) flushWrite();
 
         buffer.put((byte) c);
 
         if (isSync()) flushWrite();
 
         return 1;
     }
 
     public synchronized void ftruncate(long newLength) throws IOException,
             BadDescriptorException, InvalidValueException {
         Channel ch = descriptor.getChannel();
         if (!(ch instanceof FileChannel)) {
             throw new InvalidValueException();
         }
         invalidateBuffer();
         FileChannel fileChannel = (FileChannel)ch;
         if (newLength > fileChannel.size()) {
             // truncate can't lengthen files, so we save position, seek/write, and go back
             long position = fileChannel.position();
             int difference = (int)(newLength - fileChannel.size());
 
             fileChannel.position(fileChannel.size());
             // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
             fileChannel.write(ByteBuffer.allocate(difference));
             fileChannel.position(position);
         } else {
             fileChannel.truncate(newLength);
         }
     }
 
     /**
      * Invalidate buffer before a position change has occurred (e.g. seek),
      * flushing writes if required, and correcting file position if reading
      * @throws IOException
      */
     private void invalidateBuffer() throws IOException, BadDescriptorException {
         if (!reading) flushWrite();
         int posOverrun = buffer.remaining(); // how far ahead we are when reading
         buffer.clear();
         if (reading) {
             buffer.flip();
             // if the read buffer is ahead, back up
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (posOverrun != 0) fileChannel.position(fileChannel.position() - posOverrun);
         }
     }
 
     /**
      * Ensure close (especially flush) when we're finished with.
      */
     @Override
     public void finalize() throws Throwable {
         super.finalize();
         
         if (closedExplicitly) return;
 
         if (DEBUG) {
             getLogger("ChannelStream").info("finalize() for not explicitly closed stream");
         }
 
         // FIXME: I got a bunch of NPEs when I didn't check for nulls here...HOW?!
         if (descriptor != null && descriptor.isOpen()) {
             // tidy up
-            finish(false);
+            finish(autoclose);
         }
     }
 
     public int ready() throws IOException {
         if (descriptor.getChannel() instanceof SelectableChannel) {
             int ready_stat = 0;
             java.nio.channels.Selector sel = SelectorFactory.openWithRetryFrom(null, SelectorProvider.provider());;
             SelectableChannel selchan = (SelectableChannel)descriptor.getChannel();
             synchronized (selchan.blockingLock()) {
                 boolean is_block = selchan.isBlocking();
                 try {
                     selchan.configureBlocking(false);
                     selchan.register(sel, java.nio.channels.SelectionKey.OP_READ);
                     ready_stat = sel.selectNow();
                     sel.close();
                 } catch (Throwable ex) {
                 } finally {
                     if (sel != null) {
                         try {
                             sel.close();
                         } catch (Exception e) {
                         }
                     }
                     selchan.configureBlocking(is_block);
                 }
             }
             return ready_stat;
         } else {
             return newInputStream().available();
         }
     }
 
     public synchronized void fputc(int c) throws IOException, BadDescriptorException {
         bufferedWrite(c);
     }
 
     public int ungetc(int c) {
         if (c == -1) {
             return -1;
         }
 
         // putting a bit back, so we're not at EOF anymore
         eof = false;
 
         // save the ungot
         ungotc = c;
 
         return c;
     }
 
     public synchronized int fgetc() throws IOException, BadDescriptorException {
         if (eof) {
             return -1;
         }
 
         checkReadable();
 
         int c = read();
 
         if (c == -1) {
             eof = true;
             return c;
         }
 
         return c & 0xff;
     }
 
     public synchronized int fwrite(ByteList string) throws IOException, BadDescriptorException {
         return bufferedWrite(string);
     }
 
     public synchronized int write(ByteBuffer buf) throws IOException, BadDescriptorException {
         return bufferedWrite(buf);
     }
 
     public synchronized int writenonblock(ByteList buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
 
         if (buffer.position() != 0 && !flushWrite(false)) return 0;
 
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     if (oldBlocking) {
                         selectableChannel.configureBlocking(false);
                     }
                     return descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
                 } finally {
                     if (oldBlocking) {
                         selectableChannel.configureBlocking(oldBlocking);
                     }
                 }
             }
         } else {
             return descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
         }
     }
 
     public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {
         try {
             if (number == 0) {
                 if (eof) {
                     return null;
                 } else {
                     return new ByteList(0);
                 }
             }
 
             return bufferedRead(number);
         } catch (EOFException e) {
             eof = true;
             return null;
         }
     }
 
     public synchronized ByteList readnonblock(int number) throws IOException, BadDescriptorException, EOFException {
         assert number >= 0;
 
         if (number == 0) {
             return null;
         }
 
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     selectableChannel.configureBlocking(false);
                     return readpartial(number);
                 } finally {
                     selectableChannel.configureBlocking(oldBlocking);
                 }
             }
         } else if (descriptor.getChannel() instanceof FileChannel) {
             return fread(number);
         } else {
             return null;
         }
     }
 
     public synchronized ByteList readpartial(int number) throws IOException, BadDescriptorException, EOFException {
         assert number >= 0;
 
         if (number == 0) {
             return null;
         }
         if (descriptor.getChannel() instanceof FileChannel) {
             return fread(number);
         }
 
         if (hasBufferedInputBytes()) {
             // already have some bytes buffered, just return those
             return bufferedRead(Math.min(bufferedInputBytesRemaining(), number));
         } else {
             // otherwise, we try an unbuffered read to get whatever's available
             return read(number);
         }
     }
 
     public synchronized int read(ByteBuffer dst) throws IOException, BadDescriptorException, EOFException {
         return read(dst, !(descriptor.getChannel() instanceof FileChannel));
     }
 
     public synchronized int read(ByteBuffer dst, boolean partial) throws IOException, BadDescriptorException, EOFException {
         assert dst.hasRemaining();
 
         return bufferedRead(dst, partial);
     }
 
     public synchronized int read() throws IOException, BadDescriptorException {
         try {
             descriptor.checkOpen();
 
             if (ungotc >= 0) {
                 int c = ungotc;
                 ungotc = -1;
                 return c;
             }
 
             return bufferedRead();
         } catch (EOFException e) {
             eof = true;
             return -1;
         }
     }
 
     public ChannelDescriptor getDescriptor() {
         return descriptor;
     }
 
     public void setBlocking(boolean block) throws IOException {
         if (!(descriptor.getChannel() instanceof SelectableChannel)) {
             return;
         }
         synchronized (((SelectableChannel) descriptor.getChannel()).blockingLock()) {
             blocking = block;
             try {
                 ((SelectableChannel) descriptor.getChannel()).configureBlocking(block);
             } catch (IllegalBlockingModeException e) {
                 // ignore this; select() will set the correct mode when it is finished
             }
         }
     }
 
     public boolean isBlocking() {
         return blocking;
     }
 
     public synchronized void freopen(Ruby runtime, String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         // flush first
         flushWrite();
 
         // reset buffer
         buffer.clear();
         if (reading) {
             buffer.flip();
         }
 
         this.modes = modes;
 
         if (descriptor.isOpen()) {
             descriptor.close();
         }
 
         if (path.equals("/dev/null") || path.equalsIgnoreCase("nul:") || path.equalsIgnoreCase("nul")) {
             descriptor = descriptor.reopen(new NullChannel(), modes);
         } else {
             String cwd = runtime.getCurrentDirectory();
             JRubyFile theFile = JRubyFile.create(cwd,path);
 
             if (theFile.isDirectory() && modes.isWritable()) throw new DirectoryAsFileException();
 
             if (modes.isCreate()) {
                 if (theFile.exists() && modes.isExclusive()) {
                     throw runtime.newErrnoEEXISTError("File exists - " + path);
                 }
                 theFile.createNewFile();
             } else {
                 if (!theFile.exists()) {
                     throw runtime.newErrnoENOENTError("file not found - " + path);
                 }
             }
 
             // We always open this rw since we can only open it r or rw.
             RandomAccessFile file = new RandomAccessFile(theFile, modes.toJavaModeString());
 
             if (modes.isTruncate()) file.setLength(0L);
 
             descriptor = descriptor.reopen(file, modes);
 
             if (modes.isAppendable()) lseek(0, SEEK_END);
         }
     }
 
-    public static final Stream open(Ruby runtime, ChannelDescriptor descriptor) {
-        return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor), descriptor.getOriginalModes());
+    public static Stream open(Ruby runtime, ChannelDescriptor descriptor) {
+        return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, true), descriptor.getOriginalModes());
     }
 
     public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
-        return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, modes), modes);
+        return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, modes, true), modes);
+    }
+
+    public static Stream open(Ruby runtime, ChannelDescriptor descriptor, boolean autoclose) {
+        return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, autoclose), descriptor.getOriginalModes());
+    }
+
+    public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes, boolean autoclose) throws InvalidValueException {
+        return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, modes, autoclose), modes);
     }
 
     private static Stream maybeWrapWithLineEndingWrapper(Stream stream, ModeFlags modes) {
         if (Platform.IS_WINDOWS && stream.getDescriptor().getChannel() instanceof FileChannel && !modes.isBinary()) {
             return new CRLFStreamWrapper(stream);
         }
         return stream;
     }
 
     public static Stream fopen(Ruby runtime, String path, ModeFlags modes) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         ChannelDescriptor descriptor = ChannelDescriptor.open(runtime.getCurrentDirectory(), path, modes);
         Stream stream = fdopen(runtime, descriptor, modes);
 
         if (modes.isAppendable()) stream.lseek(0, Stream.SEEK_END);
 
         return stream;
     }
 
     public Channel getChannel() {
         return getDescriptor().getChannel();
     }
 
     private static final class InputStreamAdapter extends java.io.InputStream {
         private final ChannelStream stream;
 
         public InputStreamAdapter(ChannelStream stream) {
             this.stream = stream;
         }
 
         @Override
         public int read() throws IOException {
             synchronized (stream) {
                 // If it can be pulled direct from the buffer, don't go via the slow path
                 if (stream.hasBufferedInputBytes()) {
                     try {
                         return stream.read();
                     } catch (BadDescriptorException ex) {
                         throw new IOException(ex.getMessage());
                     }
                 }
             }
 
             byte[] b = new byte[1];
             // java.io.InputStream#read must return an unsigned value;
             return read(b, 0, 1) == 1 ? b[0] & 0xff: -1;
         }
 
         @Override
         public int read(byte[] bytes, int off, int len) throws IOException {
             if (bytes == null) {
                 throw new NullPointerException("null destination buffer");
             }
             if ((len | off | (off + len) | (bytes.length - (off + len))) < 0) {
                 throw new IndexOutOfBoundsException();
             }
             if (len == 0) {
                 return 0;
             }
 
             try {
                 synchronized(stream) {
                     final int available = stream.bufferedInputBytesRemaining();
                      if (available >= len) {
                         return stream.copyBufferedBytes(bytes, off, len);
                     } else if (stream.getDescriptor().getChannel() instanceof SelectableChannel) {
                         SelectableChannel ch = (SelectableChannel) stream.getDescriptor().getChannel();
                         synchronized (ch.blockingLock()) {
                             boolean oldBlocking = ch.isBlocking();
                             try {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(true);
                                 }
                                 return stream.bufferedRead(ByteBuffer.wrap(bytes, off, len), true);
                             } finally {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(oldBlocking);
                                 }
                             }
                         }
                     } else {
                         return stream.bufferedRead(ByteBuffer.wrap(bytes, off, len), true);
                     }
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             } catch (EOFException ex) {
                 return -1;
             }
         }
 
         @Override
         public int available() throws IOException {
             synchronized (stream) {
                 return !stream.eof ? stream.bufferedInputBytesRemaining() : 0;
             }
         }
 
         @Override
         public void close() throws IOException {
             try {
                 synchronized (stream) {
                     stream.fclose();
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
     }
 
     private static final class OutputStreamAdapter extends java.io.OutputStream {
         private final ChannelStream stream;
 
         public OutputStreamAdapter(ChannelStream stream) {
             this.stream = stream;
         }
 
         @Override
         public void write(int i) throws IOException {
             synchronized (stream) {
                 if (!stream.isSync() && stream.hasBufferedOutputSpace()) {
                     stream.buffer.put((byte) i);
                     return;
                 }
             }
             byte[] b = { (byte) i };
             write(b, 0, 1);
         }
 
         @Override
         public void write(byte[] bytes, int off, int len) throws IOException {
             if (bytes == null) {
                 throw new NullPointerException("null source buffer");
             }
             if ((len | off | (off + len) | (bytes.length - (off + len))) < 0) {
                 throw new IndexOutOfBoundsException();
             }
 
             try {
                 synchronized(stream) {
                     if (!stream.isSync() && stream.bufferedOutputSpaceRemaining() >= len) {
                         stream.buffer.put(bytes, off, len);
 
                     } else if (stream.getDescriptor().getChannel() instanceof SelectableChannel) {
                         SelectableChannel ch = (SelectableChannel) stream.getDescriptor().getChannel();
                         synchronized (ch.blockingLock()) {
                             boolean oldBlocking = ch.isBlocking();
                             try {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(true);
                                 }
                                 stream.bufferedWrite(ByteBuffer.wrap(bytes, off, len));
                             } finally {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(oldBlocking);
                                 }
                             }
                         }
                     } else {
                         stream.bufferedWrite(ByteBuffer.wrap(bytes, off, len));
                     }
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
 
 
         @Override
         public void close() throws IOException {
             try {
                 synchronized (stream) {
                     stream.fclose();
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
 
         @Override
         public void flush() throws IOException {
             try {
                 synchronized (stream) {
                     stream.flushWrite(true);
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
     }
 }
diff --git a/src/org/jruby/util/io/OpenFile.java b/src/org/jruby/util/io/OpenFile.java
index 7616f67843..a8088de3d9 100644
--- a/src/org/jruby/util/io/OpenFile.java
+++ b/src/org/jruby/util/io/OpenFile.java
@@ -1,309 +1,323 @@
 package org.jruby.util.io;
 
 import java.io.IOException;
 import org.jruby.Ruby;
 import org.jruby.util.ShellLauncher;
 
 public class OpenFile {
 
     public static final int READABLE = 1;
     public static final int WRITABLE = 2;
     public static final int READWRITE = 3;
     public static final int APPEND = 64;
     public static final int CREATE = 128;
     public static final int BINMODE = 4;
     public static final int SYNC = 8;
     public static final int WBUF = 16;
     public static final int RBUF = 32;
     public static final int WSPLIT = 512;
     public static final int WSPLIT_INITIALIZED = 1024;
     public static final int SYNCWRITE = SYNC | WRITABLE;
 
     public static interface Finalizer {
 
         public void finalize(Ruby runtime, boolean raise);
     }
     private Stream mainStream;
     private Stream pipeStream;
     private int mode;
     private Process process;
     private int lineNumber = 0;
     private String path;
     private Finalizer finalizer;
 
     public Stream getMainStream() {
         return mainStream;
     }
 
     public void setMainStream(Stream mainStream) {
         this.mainStream = mainStream;
     }
 
     public Stream getPipeStream() {
         return pipeStream;
     }
 
     public void setPipeStream(Stream pipeStream) {
         this.pipeStream = pipeStream;
     }
 
     public Stream getWriteStream() {
         return pipeStream == null ? mainStream : pipeStream;
     }
 
     public int getMode() {
         return mode;
     }
 
     public String getModeAsString(Ruby runtime) {
         String modeString = getStringFromMode(mode);
 
         if (modeString == null) {
             throw runtime.newArgumentError("Illegal access modenum " + Integer.toOctalString(mode));
         }
 
         return modeString;
     }
 
     public static String getStringFromMode(int mode) {
         if ((mode & APPEND) != 0) {
             if ((mode & READWRITE) != 0) {
                 return "ab+";
             }
             return "ab";
         }
         switch (mode & READWRITE) {
         case READABLE:
             return "rb";
         case WRITABLE:
             return "wb";
         case READWRITE:
             if ((mode & CREATE) != 0) {
                 return "wb+";
             }
             return "rb+";
         }
         return null;
     }
 
     public void checkReadable(Ruby runtime) throws IOException, BadDescriptorException, PipeException, InvalidValueException {
         checkClosed(runtime);
 
         if ((mode & READABLE) == 0) {
             throw runtime.newIOError("not opened for reading");
         }
 
         if (((mode & WBUF) != 0 || (mode & (SYNCWRITE | RBUF)) == SYNCWRITE) &&
                 !mainStream.feof() && pipeStream == null) {
             try {
                 // seek to force underlying buffer to flush
                 seek(0, Stream.SEEK_CUR);
             } catch (IOException ioe) {
                 // MRI ignores seek errors, presumably for unseekable files like
                 // serial ports (JRUBY-2979), so we shall too.
             }
         }
     }
 
     public void seek(long offset, int whence) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         flushBeforeSeek();
 
         getWriteStream().lseek(offset, whence);
     }
 
     private void flushBeforeSeek() throws BadDescriptorException, IOException {
         if ((mode & WBUF) != 0) {
             fflush(getWriteStream());
         }
     }
 
     public void fflush(Stream stream) throws IOException, BadDescriptorException {
         while (true) {
             int n = stream.fflush();
             if (n != -1) {
                 break;
             }
         }
         mode &= ~WBUF;
     }
 
     public void checkWritable(Ruby runtime) throws IOException, BadDescriptorException, InvalidValueException, PipeException {
         checkClosed(runtime);
         if ((mode & WRITABLE) == 0) {
             throw runtime.newIOError("not opened for writing");
         }
         if ((mode & RBUF) != 0 && !mainStream.feof() && pipeStream == null) {
             try {
                 // seek to force read buffer to invalidate
                 seek(0, Stream.SEEK_CUR);
             } catch (IOException ioe) {
                 // MRI ignores seek errors, presumably for unseekable files like
                 // serial ports (JRUBY-2979), so we shall too.
             }
         }
         if (pipeStream == null) {
             mode &= ~RBUF;
         }
     }
 
     public void checkClosed(Ruby runtime) {
         if (mainStream == null && pipeStream == null) {
             throw runtime.newIOError("closed stream");
         }
     }
 
     public boolean isBinmode() {
         return (mode & BINMODE) != 0;
     }
 
     public void setBinmode() {
         mode |= BINMODE;
         if (mainStream != null) {
             mainStream.setBinmode();
         }
     }
 
     public boolean isOpen() {
         return mainStream != null || pipeStream != null;
     }
 
     public boolean isReadable() {
         return (mode & READABLE) != 0;
     }
 
     public boolean isWritable() {
         return (mode & WRITABLE) != 0;
     }
 
     public boolean isReadBuffered() {
         return (mode & RBUF) != 0;
     }
 
     public void setReadBuffered() {
         mode |= RBUF;
     }
 
     public boolean isWriteBuffered() {
         return (mode & WBUF) != 0;
     }
 
     public void setWriteBuffered() {
         mode |= WBUF;
     }
 
     public void setSync(boolean sync) {
         if(sync) {
             mode = mode | SYNC;
         } else {
             mode = mode & ~SYNC;
         }
     }
 
     public boolean isSync() {
         return (mode & SYNC) != 0;
     }
 
     public boolean areBothEOF() throws IOException, BadDescriptorException {
         return mainStream.feof() && (pipeStream != null ? pipeStream.feof() : true);
     }
 
     public void setMode(int modes) {
         this.mode = modes;
     }
 
     public Process getProcess() {
         return process;
     }
 
     public void setProcess(Process process) {
         this.process = process;
     }
 
     public long getPid() {
         return ShellLauncher.getPidFromProcess(process);
     }
 
     public int getLineNumber() {
         return lineNumber;
     }
 
     public void setLineNumber(int lineNumber) {
         this.lineNumber = lineNumber;
     }
 
     public String getPath() {
         return path;
     }
 
     public void setPath(String path) {
         this.path = path;
     }
 
+    public boolean isAutoclose() {
+        boolean autoclose = true;
+        Stream myMain, myPipe;
+        if ((myMain = mainStream) != null) autoclose &= myMain.isAutoclose();
+        if ((myPipe = pipeStream) != null) autoclose &= myPipe.isAutoclose();
+        return autoclose;
+    }
+
+    public void setAutoclose(boolean autoclose) {
+        Stream myMain, myPipe;
+        if ((myMain = mainStream) != null) myMain.setAutoclose(autoclose);
+        if ((myPipe = pipeStream) != null) myPipe.setAutoclose(autoclose);
+    }
+
     public Finalizer getFinalizer() {
         return finalizer;
     }
 
     public void setFinalizer(Finalizer finalizer) {
         this.finalizer = finalizer;
     }
 
     public void cleanup(Ruby runtime, boolean raise) {
         if (finalizer != null) {
             finalizer.finalize(runtime, raise);
         } else {
             finalize(runtime, raise);
         }
     }
 
     public void finalize(Ruby runtime, boolean raise) {
         try {
             ChannelDescriptor main = null;
             ChannelDescriptor pipe = null;
 
             synchronized (this) {
                 Stream ps = pipeStream;
                 if (ps != null) {
                     pipe = ps.getDescriptor();
 
                     // TODO: Ruby logic is somewhat more complicated here, see comments after
                     try {
                         ps.fflush();
                         ps.fclose();
                     } finally {
                         // make sure the pipe stream is set to null
                         pipeStream = null;
                     }
                 }
                 Stream ms = mainStream;
                 if (ms != null) {
                     // TODO: Ruby logic is somewhat more complicated here, see comments after
                     main = ms.getDescriptor();
                     try {
                         if (pipe == null && isWriteBuffered()) {
                             ms.fflush();
                         }
                         ms.fclose();
                     } catch (BadDescriptorException bde) {
                         if (main == pipe) {
                         } else {
                             throw bde;
                         }
                     } finally {
                         // make sure the main stream is set to null
                         mainStream = null;
                     }
                 }
             }
         } catch (IOException ex) {
             if (raise) {
                 throw runtime.newIOErrorFromException(ex);
             }
         } catch (BadDescriptorException ex) {
             if (raise) {
                 throw runtime.newErrnoEBADFError();
             }
         } catch (Throwable t) {
             t.printStackTrace();
         }
     }
 }
diff --git a/src/org/jruby/util/io/Stream.java b/src/org/jruby/util/io/Stream.java
index a714382475..2f1566d832 100644
--- a/src/org/jruby/util/io/Stream.java
+++ b/src/org/jruby/util/io/Stream.java
@@ -1,175 +1,178 @@
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
  * Copyright (C) 2008-2009 The JRuby Community <www.jruby.org>
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
 package org.jruby.util.io;
 
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.Channel;
 import org.jruby.util.ByteList;
 import org.jruby.Ruby;
 
 /**
  */
 public interface Stream {
     static final int SEEK_SET = 0;
     static final int SEEK_CUR = 1;
     static final int SEEK_END = 2;
 
     // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it)
     static final ByteList PARAGRAPH_DELIMETER = ByteList.create("PARAGRPH_DELIM_MRK_ER");
 
     static final ByteList PARAGRAPH_SEPARATOR = ByteList.create("\n\n");
 
     ChannelDescriptor getDescriptor();
 
     void clearerr();
 
     ModeFlags getModes();
 
     boolean isSync();
 
     void setSync(boolean sync);
 
     ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException, EOFException;
     ByteList readall() throws IOException, BadDescriptorException, EOFException;
 
 
     /**
      * Read all bytes up to and including a terminator byte.
      *
      * <p>If the terminator byte is found, it will be the last byte in the output buffer.</p>
      *
      * @param dst The output buffer.
      * @param terminator The byte to terminate reading.
      * @return The number of bytes read, or -1 if EOF is reached.
      *
      * @throws java.io.IOException
      * @throws org.jruby.util.io.BadDescriptorException
      */
     int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException;
 
     /**
      * Reads all bytes up to and including a terminator byte or until limit is reached.
      *
      * <p>If the terminator byte is found, it will be the last byte in the output buffer.</p>
      *
      * @param dst The output buffer.
      * @param terminator The byte to terminate reading.
      * @param limit the number of bytes to read unless EOF or terminator is found
      * @return The number of bytes read, or -1 if EOF is reached.
      *
      * @throws java.io.IOException
      * @throws org.jruby.util.io.BadDescriptorException
      */
     int getline(ByteList dst, byte terminator, long limit) throws IOException, BadDescriptorException;
 
     // TODO: We overflow on large files...We could increase to long to limit
     // this, but then the impl gets more involved since java io APIs based on
     // int (means we have to chunk up a long into a series of int ops).
 
     ByteList fread(int number) throws IOException, BadDescriptorException, EOFException;
     int fwrite(ByteList string) throws IOException, BadDescriptorException;
 
     int fgetc() throws IOException, BadDescriptorException, EOFException;
     int ungetc(int c);
     void fputc(int c) throws IOException, BadDescriptorException;
 
     ByteList read(int number) throws IOException, BadDescriptorException, EOFException;
 
     void fclose() throws IOException, BadDescriptorException;
     int fflush() throws IOException, BadDescriptorException;
 
     /**
      * <p>Flush and sync all writes to the filesystem.</p>
      *
      * @throws IOException if the sync does not work
      */
     void sync() throws IOException, BadDescriptorException;
 
     /**
      * <p>Return true when at end of file (EOF).</p>
      *
      * @return true if at EOF; false otherwise
      * @throws IOException
      * @throws BadDescriptorException
      */
     boolean feof() throws IOException, BadDescriptorException;
 
     /**
      * <p>Get the current position within the file associated with this
      * handler.</p>
      *
      * @return the current position in the file.
      * @throws IOException
      * @throws PipeException ESPIPE (illegal seek) when not a file
      *
      */
     long fgetpos() throws IOException, PipeException, BadDescriptorException, InvalidValueException;
 
     /**
      * <p>Perform a seek based on pos().  </p>
      * @throws IOException
      * @throws PipeException
      * @throws InvalidValueException
      */
     void lseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException;
     void ftruncate(long newLength) throws IOException, PipeException,
             InvalidValueException, BadDescriptorException;
 
     /**
      * Implement IO#ready? as per io/wait in MRI.
      * returns non-nil if input available without blocking, or nil.
      */
     int ready() throws IOException;
 
     /**
      * Implement IO#wait as per io/wait in MRI.
      * waits until input available or timed out and returns self, or nil when EOF reached.
      *
      * The default implementation loops while ready returns 0.
      */
     void waitUntilReady() throws IOException, InterruptedException;
 
     boolean readDataBuffered();
     boolean writeDataBuffered();
 
     InputStream newInputStream();
 
     OutputStream newOutputStream();
 
     boolean isBlocking();
 
     void setBlocking(boolean blocking) throws IOException;
 
     void freopen(Ruby runtime, String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException;
 
     void setBinmode();
     Channel getChannel();
+
+    boolean isAutoclose();
+    void setAutoclose(boolean autoclose);
 }
diff --git a/test/test_io.rb b/test/test_io.rb
index 78840ea799..72d5a2a98d 100644
--- a/test/test_io.rb
+++ b/test/test_io.rb
@@ -1,517 +1,517 @@
 require 'test/unit'
 require 'rbconfig'
 require 'java'
 
 class TestIO < Test::Unit::TestCase
   WINDOWS = Config::CONFIG['host_os'] =~ /Windows|mswin/
   def setup
     @to_close = []
     @file = "TestIO_tmp"
     @file2 = "Test2IO_tmp"
     @file3 = "Test3IO_tmp"
     if (WINDOWS)
       @devnull = 'NUL:'
     else
       @devnull = '/dev/null'
     end
   end
 
   def teardown
     @to_close.each {|io| io.close rescue nil }
     File.unlink @file rescue nil
     File.unlink @file2 rescue nil
     File.unlink @file3 rescue nil
   end
 
   def test_puts_on_a_recursive_array
     # Puts a recursive array
     x = []
     x << 2 << x
     f = File.new(@file, "w")
     @to_close << f
     g = IO.new(f.fileno)
     @to_close << g
     g.puts x
     g.close
 
     f = File.new(@file, "r")
     @to_close << f
     g = IO.new(f.fileno)
     @to_close << g
     a = f.gets
     b = f.gets
     assert_equal("2\n", a)
     assert_equal("[...]\n", b)
   end
 
   def test_premature_close_raises_appropriate_errors
     ensure_files @file
     # In this case we will have f close (which will pull the rug
     # out from under g) and thus make g try the ops and fail
     f = File.open(@file)
     g = IO.new(f.fileno)
     @to_close << g
     f.close
     assert_raises(Errno::EBADF) { g.readchar }
     assert_raises(Errno::EBADF) { g.readline }
     assert_raises(Errno::EBADF) { g.gets }
     assert_raises(Errno::EBADF) { g.close }
     assert_raises(IOError) { g.getc }
     assert_raises(IOError) { g.readchar }
     assert_raises(IOError) { g.read }
     assert_raises(IOError) { g.sysread 1 }
 
     f = File.open(@file, "w")
     g = IO.new(f.fileno)
     @to_close << g
     f.close
     assert_nothing_raised { g.print "" }
     assert_nothing_raised { g.write "" }
     assert_nothing_raised { g.puts "" }
     assert_nothing_raised { g.putc 'c' }
     assert_raises(Errno::EBADF) { g.syswrite "" }
   end
 
   def test_ios_with_incompatible_flags
     ensure_files @file, @file2
     # Cannot open an IO which does not have compatible permission with
     # original IO
     f = File.new(@file2, "w")
     @to_close << f
     assert_raises(Errno::EINVAL) { g = IO.new(f.fileno, "r") }
     f.close
 
     f = File.new(@file, "r")
     @to_close << f
     assert_raises(Errno::EINVAL) { g = IO.new(f.fileno, "w") }
     f.close
   end
 
   def test_ios_with_compatible_flags
     ensure_files @file
     # However, you can open a second with less permissions
     f = File.new(@file, "r+")
     @to_close << f
     g = IO.new(f.fileno, "r")
     @to_close << g
     g.gets
     f.puts "HEH"
     assert_raises(IOError) { g.write "HOH" }
     assert_equal(f.fileno, g.fileno)
     f.close
   end
 
   def test_empty_write_does_not_complain
     # empty write...writes nothing and does not complain
     f = File.new(@file, "w")
     @to_close << f
     i = f.syswrite("")
     assert_equal(i, 0)
     i = f.syswrite("heh")
     assert_equal(i, 3)
     f.close
   end
 
   def test_enoent
     assert_raises(Errno::ENOENT) { File.foreach("nonexistent_file") {} }
   end
 
   def test_reopen
     ensure_files @file, @file2
     file = File.open(@file)
     @to_close << file
     file.gets
     file2 = File.open(@file2)
     @to_close << file2
     file2_fileno = file2.fileno;
     file2 = file2.reopen(file)
     assert_equal(file.pos, file2.pos)
     assert_equal(file2_fileno, file2.fileno);
     assert(file.fileno != file2.fileno);
     file2.close
     file.close
 
     # reopen of a filename after a close should succeed (JRUBY-1885)
     assert_nothing_raised { file.reopen(@file) }
   end
 
   def test_file_read
     ensure_files @file
     # test that read returns correct values
     f = File.open(@file)
     @to_close << f
     f.read # read all
     assert_equal("", f.read)
     assert_equal(nil, f.read(1))
     f.close
   end
 
   # MRI 1.8.5 and 1.8.6 permit nil buffers with reads.
   def test_file_read_with_nil_buffer
      ensure_files @file
 
      f = File.open(@file)
      @to_close << f
      assert_equal " ", f.read(1, nil)
   end
 
   def test_open
     ensure_files @file
 
     assert_raises(ArgumentError) { io = IO.open }
 
     f = File.open(@file)
     @to_close << f
     assert_raises(ArgumentError) { io = IO.open(f.fileno, "r", :gratuitous) }
     io = IO.open(f.fileno, "r")
     @to_close << io
     assert_equal(f.fileno, io.fileno)
     assert(!io.closed?)
     io.close
     assert(io.closed?)
 
     assert(!f.closed?)
     assert_raises(Errno::EBADF) { f.close }
   end
 
   def test_open_with_block
     ensure_files @file
 
     f = File.open(@file)
     @to_close << f
     IO.open(f.fileno, "r") do |io|
       assert_equal(f.fileno, io.fileno)
       assert(!io.closed?)
     end
 
     assert(!f.closed?)
     assert_raises(Errno::EBADF) { f.close }
   end
 
   unless WINDOWS # Windows doesn't take kindly to perm mode tests
     def test_sysopen
       ensure_files @file
 
       fno = IO::sysopen(@file, "r", 0124) # not creating, mode is ignored
       assert_instance_of(Fixnum, fno)
       assert_raises(Errno::EINVAL) { IO.open(fno, "w") } # not writable
       IO.open(fno, "r") do |io|
         assert_equal(fno, io.fileno)
         assert(!io.closed?)
       end
       assert_raises(Errno::EBADF) { IO.open(fno, "r") } # fd is closed
       File.open(@file) do |f|
         mode = (f.stat.mode & 0777) # only comparing lower 9 bits
         assert(mode > 0124)
       end
 
       File.delete(@file)
       fno = IO::sysopen(@file, "w", 0611) # creating, mode is enforced
       File.open(@file) do |f|
         mode = (f.stat.mode & 0777)
         assert_equal(0611, mode)
       end
     end
   end
 
   def test_delete
     ensure_files @file, @file2, @file3
     # Test deleting files
     assert(File.delete(@file, @file2, @file3))
   end
 
   def test_select
     ##### select #####
     assert_equal(nil, select(nil, nil, nil, 0))
     assert_raises(ArgumentError) { select(nil, nil, nil, -1) }
   end
 
   class FakeStream
     attr_accessor :data
     def initialize(stream, passthrough = false)
       @stream = stream
       @passthrough = passthrough
     end
     def write(content)
       @data = content
       @stream.write(content) if @passthrough
     end
   end
 
   def test_puts_and_warn_redirection
     require 'stringio'
     begin
       $stdout = StringIO.new
       $stderr = StringIO.new
       $stdout.print ":"
       $stderr.print ":"
       puts "hi"
       warn "hello"
       assert_equal ":hi\n", $stdout.string
       assert_equal ":hello\n", $stderr.string
     ensure
       $stderr = STDERR
       $stdout = STDOUT
     end
   end
 
   # JRUBY-1894
   def test_getc_255
      File.open(@file, "wb") do |file|
        file.putc(255)
      end
      File.open(@file, "rb") do |file|
        assert_equal(255, file.getc)
      end
   end
 
   # JRUBY-2202
   def test_ungetc_empty_file
     File.open(@file, "w+") {}
     File.open(@file) do |file|
       assert_nil(file.getc)
       assert_equal(0, file.pos)
       file.ungetc(100)
 
       # The following line is an intentional regression tests,
       # it checks that JRuby doesn't break.
       assert_equal(0, file.pos)
 
       assert_equal(100, file.getc)
      end
   end
 
   # JRUBY-2202
   def test_ungetc_nonempty_file
     File.open(@file, "w+") { |file| file.puts("HELLO") }
     File.open(@file) do |file|
       assert_equal(72, file.getc)
       assert_equal(1, file.pos)
       file.ungetc(100)
       assert_equal(0, file.pos)
       assert_equal(100, file.getc)
       assert_equal(1, file.pos)
      end
   end
 
   # JRUBY-2202
   def test_ungetc_position_change
     File.open(@file, "w+") { |file| file.puts("HELLO") }
 
     # getc/ungetc the same char
     File.open(@file) do |f|
       f.ungetc(f.getc)
       assert_equal(0, f.pos)
       assert_equal("HELLO", f.read(5))
       assert_equal(5, f.pos)
     end
 
     # getc/ungetc different char
     File.open(@file) do |f|
       f.getc
       f.ungetc(100)
       assert_equal(0, f.pos)
       assert_equal("dELLO", f.read(5))
       assert_equal(5, f.pos)
      end
   end
 
   # JRUBY-2203
   # unget char should be discarded after position changing calls
   def test_unget_before_position_change
     File.open(@file, "wb+") { |file| file.puts("HELLO") }
     File.open(@file) do |f|
       f.read(3)
       f.ungetc(100)
       f.pos = 2
       assert_equal("LLO", f.read(3))
 
       f.ungetc(100)
       f.seek(2)
       assert_equal("LLO", f.read(3))
 
       f.ungetc(100)
       f.rewind
       assert_equal("HELLO", f.read(5))
 
       f.ungetc(100)
       f.seek(-3, IO::SEEK_END)
       assert_equal("LO", f.read(2))
     end
   end
 
   # JRUBY-1987
   def test_reopen_doesnt_close_same_handler
     f = File.open(@file, "w")
     @to_close << f
     x = IO.new(f.fileno)
     @to_close << x
     f.print "."
     y = x.dup
     @to_close << y
     x.reopen(y)
     f.print "."
     f.close
     out = File.read(@file)
     assert_equal "..", out
   end
 
   # JRUBY-1698
   def test_very_big_read
     # See JRUBY-1686: this caused OOM
     ensure_files @file
     f = File.open(@file)
     @to_close << f
     assert_nothing_raised { f.read(1000000000) }
   end
 
   # JRUBY-2023, multithreaded writes
   def test_multithreaded_writes
     f = File.open("__temp1", "w")
     @to_close << f
     threads = []
     100.times {
       threads << Thread.new { 100.times { f.print('.') } }
     }
     threads.each {|thread| thread.join}
     f.close
     assert File.size("__temp1") == 100*100
   ensure
     File.unlink("__temp1")
   end
 
   #JRUBY-2145
   def test_eof_on_dev_null
     File.open(@devnull, 'rb') { |f|
       assert(f.eof?)
     }
   end
 
   #JRUBY-2145
   def test_read_dev_null
     File.open(@devnull, 'rb') { |f|
       assert_equal("", f.read)
       assert_equal(nil, f.read(1))
       assert_equal([], f.readlines)
       assert_raise EOFError do
         f.readline
       end
     }
   end
 
   def test_read_ignores_blocks
     a = true
     File.read(@devnull) { a = false }
     assert(a)
   end
 
   #JRUBY-2145
   if (!WINDOWS)
     def test_copy_dev_null
       require 'fileutils'
       begin
         FileUtils.cp(@devnull, 'somefile')
         assert(File.exists?('somefile'))
         assert_equal(0, File.size('somefile'))
       ensure
         File.delete('somefile') rescue nil
       end
     end
   end
 
   if (WINDOWS)
     #JRUBY-2158
     def test_null_open_windows
       null_names = ['NUL', 'NUL:', 'nul', 'nul:']
       null_names.each { |name|
         File.open(name) { |f|
           assert_equal("", f.read)
           assert(f.eof?)
         }
         File.open(name, 'r+') { |f|
           assert_nil(f.puts("test"))
         }
       }
     end
   end
 
   def test_file_constants_included
     assert IO.include?(File::Constants)
     ["APPEND", "BINARY", "CREAT", "EXCL", "FNM_CASEFOLD",
                    "FNM_DOTMATCH", "FNM_NOESCAPE", "FNM_PATHNAME", "FNM_SYSCASE",
                    "LOCK_EX", "LOCK_NB", "LOCK_SH", "LOCK_UN", "NOCTTY", "NONBLOCK",
                    "RDONLY", "RDWR", "SEEK_CUR", "SEEK_END", "SEEK_SET", "SYNC", "TRUNC",
                    "WRONLY"].each { |c| assert(IO.constants.include?(c), "#{c} is not included") }
   end
 
   #JRUBY-3012
   def test_io_reopen
     quiet_script = File.dirname(__FILE__) + '/quiet.rb'
     result = `jruby #{quiet_script}`.chomp
     assert_equal("foo", result)
   end
 
   # JRUBY-4152
   if $stdin.tty? # in Ant that might be false
     def test_tty_leak
       assert $stdin.tty?
       10_000.times {
         $stdin.tty?
       }
       assert $stdin.tty?
     end
   end
 
   # JRUBY-4821
   def test_clear_dollar_bang_after_open_block
     open(__FILE__) do |io|
       io.close
     end
     assert_nil $!
   end
 
   # JRUBY-4932
   #  def test_popen4_read_error
   #  p, o, i, e = IO.popen4(__FILE__)
   #  assert_raise(IOError) { i.read }
   #end
 
   def ensure_files(*files)
     files.each {|f| File.open(f, "w") {|g| g << " " } }
   end
   private :ensure_files
   
   # JRUBY-4908
   unless WINDOWS
     def test_sh_used_appropriately
       # should not use sh
       p, o, i, e = IO.popen4("/bin/ps -a -x -f")
       assert_match p.to_s, i.read.lines.grep(/\/bin\/ps -a -x -f/).first
       
       # should use sh
       p, o, i, e = IO.popen4("/bin/ps -a -x -f | grep [/]bin/ps'")
       assert_no_match Regexp.new(p.to_s), i.read.grep(/\/bin\/ps/).first
     end
   end
   
   # JRUBY-5114
-  def test_finalization_leaves_channels_open
+  def test_autoclose_false_leaves_channels_open
     channel = java.io.FileInputStream.new(__FILE__).channel
     
     # sanity check
-    io1 = channel.to_io
+    io1 = channel.to_io(:autoclose => false)
     assert_equal "r", io1.sysread(1)
-    io2 = channel.to_io
+    io2 = channel.to_io(:autoclose => false)
     assert_equal "e", io2.sysread(1)
     
     # dereference and force GC a few times to finalize
     io1 = nil
     5.times { java.lang.System.gc }
     
     # io2 and original channel should still be open and usable
     assert_equal "q", io2.sysread(1)
     assert !io2.closed?
     
     assert channel.open?
   end
 end
