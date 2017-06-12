diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index e56fb9bb3c..002f5dcfe2 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,1121 +1,1125 @@
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
 
 import org.jruby.util.StringSupport;
 import org.jruby.util.io.SelectBlob;
 import org.jcodings.exception.EncodingException;
 import jnr.constants.platform.Fcntl;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.FileNotFoundException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.NonReadableChannelException;
 import java.nio.channels.Pipe;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jcodings.Encoding;
 import org.jruby.anno.FrameField;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.ext.fcntl.FcntlLibrary;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import static org.jruby.runtime.Visibility.*;
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
 
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 
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
-
+    
     public RubyIO(Ruby runtime, OutputStream outputStream) {
+        this(runtime, outputStream, true);
+    }
+
+    public RubyIO(Ruby runtime, OutputStream outputStream, boolean autoclose) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
-            openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(outputStream))));
+            openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(outputStream)), autoclose));
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
         Stream mainStream;
 
         try {
             switch (stdio) {
             case IN:
                 // special constructor that accepts stream, not channel
                 descriptor = new ChannelDescriptor(runtime.getIn(), new ModeFlags(ModeFlags.RDONLY), FileDescriptor.in);
                 runtime.putFilenoMap(0, descriptor.getFileno());
                 mainStream = ChannelStream.open(runtime, descriptor);
                 openFile.setMainStream(mainStream);
                 break;
             case OUT:
                 descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getOut()), new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out);
                 runtime.putFilenoMap(1, descriptor.getFileno());
                 mainStream = ChannelStream.open(runtime, descriptor);
                 openFile.setMainStream(mainStream);
                 openFile.getMainStream().setSync(true);
                 break;
             case ERR:
                 descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getErr()), new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.err);
                 runtime.putFilenoMap(2, descriptor.getFileno());
                 mainStream = ChannelStream.open(runtime, descriptor);
                 openFile.setMainStream(mainStream);
                 openFile.getMainStream().setSync(true);
                 break;
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         // never autoclose stdio streams
         openFile.setAutoclose(false);
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
         ioClass.setConstant("SEEK_SET", runtime.newFixnum(Stream.SEEK_SET));
         ioClass.setConstant("SEEK_CUR", runtime.newFixnum(Stream.SEEK_CUR));
         ioClass.setConstant("SEEK_END", runtime.newFixnum(Stream.SEEK_END));
 
         if (runtime.is1_9()) {
             ioClass.defineModuleUnder("WaitReadable");
             ioClass.defineModuleUnder("WaitWritable");
         }
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
         try {
             return getOpenFileChecked().getMainStreamSafe().newOutputStream();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     public InputStream getInStream() {
         try {
             return getOpenFileChecked().getMainStreamSafe().newInputStream();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     public Channel getChannel() {
         try {
             return getOpenFileChecked().getMainStreamSafe().getChannel();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
 
     @Deprecated
     public Stream getHandler() throws BadDescriptorException {
         return getOpenFileChecked().getMainStreamSafe();
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
                 openFile.getMainStreamSafe().freopen(runtime, path, getIOModes(runtime, openFile.getModeAsString(runtime)));
                 
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
                 pos = originalFile.getMainStreamSafe().fgetpos();
             }
 
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStreamSafe().fflush();
             }
 
             if (selfFile.isWritable()) {
                 selfFile.getWriteStreamSafe().fflush();
             }
 
             selfFile.setMode(originalFile.getMode());
             selfFile.setProcess(originalFile.getProcess());
             selfFile.setLineNumber(originalFile.getLineNumber());
             selfFile.setPath(originalFile.getPath());
             selfFile.setFinalizer(originalFile.getFinalizer());
 
             ChannelDescriptor selfDescriptor = selfFile.getMainStreamSafe().getDescriptor();
             ChannelDescriptor originalDescriptor = originalFile.getMainStreamSafe().getDescriptor();
 
             // confirm we're not reopening self's channel
             if (selfDescriptor.getChannel() != originalDescriptor.getChannel()) {
                 // check if we're a stdio IO, and ensure we're not badly mutilated
                 if (runtime.getFileno(selfDescriptor) >= 0 && runtime.getFileno(selfDescriptor) <= 2) {
                     selfFile.getMainStreamSafe().clearerr();
 
                     // dup2 new fd into self to preserve fileno and references to it
                     originalDescriptor.dup2Into(selfDescriptor);
                 } else {
                     Stream pipeFile = selfFile.getPipeStream();
                     int mode = selfFile.getMode();
                     selfFile.getMainStreamSafe().fclose();
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
                         selfFile.getMainStreamSafe().setSync(selfFile.getMainStreamSafe().isSync());
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
             throw runtime.newIOErrorFromException(ex);
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
     private ByteList separator(Ruby runtime) {
         return separator(runtime, runtime.getRecordSeparatorVar().get());
     }
 
     private ByteList separator(Ruby runtime, IRubyObject separatorValue) {
         ByteList separator = separatorValue.isNil() ? null :
             separatorValue.convertToString().getByteList();
 
         if (separator != null) {
             if (separator.getRealSize() == 0) separator = Stream.PARAGRAPH_DELIMETER;
 
             if (runtime.is1_9()) {
                 Encoding internal = getInternalEncoding(runtime);
 
                 if (internal != null) {
                     separator = RubyString.transcode(runtime.getCurrentContext(), separator,
                             internal, getExternalEncoding(runtime), runtime.getNil());
                 }
             }
         }
 
         return separator;
     }
 
     private ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
         return separator(runtime, args.length > idx ? args[idx] : runtime.getRecordSeparatorVar().get());
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
 
     private IRubyObject getline(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
         IRubyObject result = getlineInner(runtime, separator, limit, cache);
 
         if (runtime.is1_9() && !result.isNil()) {
             Encoding internal = getInternalEncoding(runtime);
 
             if (internal != null) {
                 result = RubyString.newStringNoCopy(runtime,
                         RubyString.transcode(runtime.getCurrentContext(),
                         ((RubyString) result).getByteList(), getExternalEncoding(runtime), internal,
                         runtime.getNil()));
             }
         }
 
         return result;
     }
     /**
      * getline using logic of gets.  If limit is -1 then read unlimited amount.
      *
      */
     private IRubyObject getlineInner(Ruby runtime, ByteList separator, long limit, ByteListCache cache) {
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
                 if (runtime.is1_9()) {
                     return RubyString.newEmptyString(runtime, getExternalEncoding(runtime));
                 } else {
                     return RubyString.newEmptyString(runtime);
                 }
             } else if (separator != null && separator.length() == 1 && limit < 0) {
                 return getlineFast(runtime, separator.get(0) & 0xFF, cache);
             } else {
                 Stream readStream = myOpenFile.getMainStreamSafe();
                 int c = -1;
                 int n = -1;
                 int newline = (separator != null) ? (separator.get(separator.length() - 1) & 0xFF) : -1;
 
                 ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
                 try {
                     boolean update = false;
                     boolean limitReached = false;
 
                     while (true) {
                         do {
                             readCheck(readStream);
                             readStream.clearerr();
 
                             try {
                                 runtime.getCurrentContext().getThread().beforeBlockingCall();
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
                             } finally {
                                 runtime.getCurrentContext().getThread().afterBlockingCall();
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
                         if (c == newline && separator != null && buf.length() >= separator.length() &&
                                 0 == ByteList.memcmp(buf.getUnsafeBytes(), buf.getBegin() + buf.getRealSize() - separator.length(), separator.getUnsafeBytes(), separator.getBegin(), separator.getRealSize())) {
                             break;
                         }
                     }
                     
                     if (isParagraph && c != -1) swallow('\n');
                     if (!update) return runtime.getNil();
 
                     incrementLineno(runtime, myOpenFile);
 
                     return makeString(runtime, buf, cache != null);
                 }
                 finally {
                     if (cache != null) cache.release(buf);
                 }
             }
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (EOFException e) {
             return runtime.getNil();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     private Encoding getExternalEncoding(Ruby runtime) {
         return externalEncoding != null ? externalEncoding : runtime.getDefaultExternalEncoding();
     }
 
 
     private Encoding getInternalEncoding(Ruby runtime) {
         return internalEncoding != null ? internalEncoding : runtime.getDefaultInternalEncoding();
     }
 
     private RubyString makeString(Ruby runtime, ByteList buffer, boolean isCached) {
         ByteList newBuf = isCached ? new ByteList(buffer) : buffer;
 
         if (runtime.is1_9()) newBuf.setEncoding(getExternalEncoding(runtime));
 
         RubyString str = RubyString.newString(runtime, newBuf);
         str.setTaint(true);
 
         return str;
     }
 
     private void incrementLineno(Ruby runtime, OpenFile myOpenFile) {
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.setCurrentLine(lineno);
         RubyArgsFile.setCurrentLineNumber(runtime.getArgsFile(), lineno);
     }
 
     protected boolean swallow(int term) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStreamSafe();
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
         Stream readStream = openFile.getMainStreamSafe();
         int c = -1;
 
         ByteList buf = cache != null ? cache.allocate(0) : new ByteList(0);
         try {
             boolean update = false;
             do {
                 readCheck(readStream);
                 readStream.clearerr();
                 int n;
                 try {
                     runtime.getCurrentContext().getThread().beforeBlockingCall();
                     n = readStream.getline(buf, (byte) delim);
                     c = buf.length() > 0 ? buf.get(buf.length() - 1) & 0xff : -1;
                 } catch (EOFException e) {
                     n = -1;
                 } finally {
                     runtime.getCurrentContext().getThread().afterBlockingCall();
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
 
             if (!update) return runtime.getNil();
                 
             incrementLineno(runtime, openFile);
 
             return makeString(runtime, buf, cache != null);
         } finally {
             if (cache != null) cache.release(buf);
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             context.getRuntime().getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead");
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
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unusedBlock) {
         return initializeCommon19(RubyNumeric.fix2int(fileNumber), null);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
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
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
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
             Ruby runtime = context.getRuntime();
 
             IRubyObject[] fullEncoding = modes19.split(context, RubyString.newString(runtime, ":")).toJavaArray();
 
             IRubyObject externalEncodingOption = fullEncoding[initialPosition];
             RubyString dash = runtime.newString("-");
             if (dash.eql(externalEncodingOption)) {
                 externalEncodingOption = runtime.getEncodingService().getDefaultExternal();
             }
 
             if (fullEncoding.length > (initialPosition + 1)) {
                 IRubyObject internalEncodingOption = fullEncoding[initialPosition + 1];
                 if (dash.eql(internalEncodingOption)) {
                     internalEncodingOption = runtime.getEncodingService().getDefaultInternal();
                 }
                 set_encoding(context, externalEncodingOption, internalEncodingOption);
             } else {
                 set_encoding(context, externalEncodingOption);
             }
         }
     }
 
     @JRubyMethod(required = 1, optional = 1, visibility = PRIVATE, compat = RUBY1_8)
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
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject external_encoding(ThreadContext context) {
         return externalEncoding != null ?
             context.getRuntime().getEncodingService().getEncoding(externalEncoding) :
             context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject internal_encoding(ThreadContext context) {
         return internalEncoding != null ?
             context.getRuntime().getEncodingService().getEncoding(internalEncoding) :
             context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString) {
         setExternalEncoding(context, encodingString);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat=RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(compat = RUBY1_9)
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingString, IRubyObject internalEncoding, IRubyObject options) {
         setExternalEncoding(context, encodingString);
         setInternalEncoding(context, internalEncoding);
         return context.getRuntime().getNil();
     }
 
     private void setExternalEncoding(ThreadContext context, IRubyObject encoding) {
         externalEncoding = getEncodingCommon(context, encoding);
     }
 
 
     private void setInternalEncoding(ThreadContext context, IRubyObject encoding) {
         Encoding internalEncodingOption = getEncodingCommon(context, encoding);
 
         if (internalEncodingOption == externalEncoding) {
             context.getRuntime().getWarnings().warn("Ignoring internal encoding " + encoding
                     + ": it is identical to external encoding " + external_encoding(context));
         } else {
             internalEncoding = internalEncodingOption;
         }
     }
 
     private Encoding getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         if (encoding instanceof RubyEncoding) return ((RubyEncoding) encoding).getEncoding();
         
         return context.getRuntime().getEncodingService().getEncodingFromObject(encoding);
     }
 
     @JRubyMethod(required = 1, optional = 2, meta = true)
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
diff --git a/src/org/jruby/embed/ScriptingContainer.java b/src/org/jruby/embed/ScriptingContainer.java
index 0ec6d6828a..4c24dbcc63 100644
--- a/src/org/jruby/embed/ScriptingContainer.java
+++ b/src/org/jruby/embed/ScriptingContainer.java
@@ -614,1128 +614,1128 @@ public class ScriptingContainer implements EmbedRubyInstanceConfigAdapter {
     /**
      * Changes a JRuby home directory to a directory of a given name.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * the given directory will be used.
      *
      * @since JRuby 1.5.0.
      *
      * @param home a name of new JRuby home directory.
      */
     public void setHomeDirectory(String home) {
         provider.getRubyInstanceConfig().setJRubyHome(home);
     }
 
     /**
      * Returns a ClassCache object that is tied to a class loader. The default ClassCache
      * object is tied to a current thread' context loader if it exists. Otherwise, it is
      * tied to the class loader that loaded RubyInstanceConfig.
      *
      * @since JRuby 1.5.0.
      *
      * @return a ClassCache object.
      */
     public ClassCache getClassCache() {
         return provider.getRubyInstanceConfig().getClassCache();
     }
 
     /**
      * Changes a ClassCache object to a given one.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * the given class cache will be used.
      *
      * @since JRuby 1.5.0.
      *
      * @param cache a new ClassCache object to be set.
      */
     public void setClassCache(ClassCache cache) {
         provider.getRubyInstanceConfig().setClassCache(cache);
     }
 
     /**
      * Returns a class loader object that is currently used. This loader loads
      * Ruby files and libraries.
      *
      * @since JRuby 1.5.0.
      *
      * @return a class loader object that is currently used.
      */
     public ClassLoader getClassLoader() {
         return provider.getRubyInstanceConfig().getLoader();
     }
 
     /**
      * Changes a class loader to a given loader.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * the given class loader will be used.
      *
      * @since JRuby 1.5.0.
      *
      * @param loader a new class loader to be set.
      */
     public void setClassLoader(ClassLoader loader) {
         provider.getRubyInstanceConfig().setLoader(loader);
     }
 
     /**
      * Returns a Profile currently used. The default value is Profile.DEFAULT,
      * which has the same behavior to Profile.ALL.
      * Profile allows you to define a restricted subset of code to be loaded during
      * the runtime initialization. When you use JRuby in restricted environment
      * such as Google App Engine, Profile is a helpful option.
      *
      * @since JRuby 1.5.0.
      *
      * @return a current profiler.
      */
     public Profile getProfile() {
         return provider.getRubyInstanceConfig().getProfile();
     }
 
     /**
      * Changes a Profile to a given one. The default value is Profile.DEFAULT,
      * which has the same behavior to Profile.ALL.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * Profile allows you to define a restricted subset of code to be loaded during
      * the runtime initialization. When you use JRuby in restricted environment
      * such as Google App Engine, Profile is a helpful option. For example,
      * Profile.NO_FILE_CLASS doesn't load File class.
      *
      * @since JRuby 1.5.0.
      *
      * @param profile a new profiler to be set.
      */
     public void setProfile(Profile profile) {
         provider.getRubyInstanceConfig().setProfile(profile);
     }
 
     /**
      * Returns a LoadServiceCreator currently used.
      *
      * @since JRuby 1.5.0.
      *
      * @return a current LoadServiceCreator.
      */
     public LoadServiceCreator getLoadServiceCreator() {
         return provider.getRubyInstanceConfig().getLoadServiceCreator();
     }
 
     /**
      * Changes a LoadServiceCreator to a given one.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param creator a new LoadServiceCreator
      */
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         provider.getRubyInstanceConfig().setLoadServiceCreator(creator);
     }
 
     /**
      * Returns a list of argument.
      *
      * @since JRuby 1.5.0.
      *
      * @return an arguments' list.
      */
     public String[] getArgv() {
         return provider.getRubyInstanceConfig().getArgv();
     }
 
     /**
      * Changes values of the arguments' list.
      *
      * @since JRuby 1.5.0.
      *
      * @param argv a new arguments' list.
      */
     public void setArgv(String[] argv) {
         provider.getRubyInstanceConfig().setArgv(argv);
     }
 
     /**
      * Returns a script filename to run. The default value is "&lt;script&gt;".
      *
      * @since JRuby 1.5.0.
      *
      * @return a script filename.
      */
     public String getScriptFilename() {
         return provider.getRubyInstanceConfig().getScriptFileName();
     }
 
     /**
      * Changes a script filename to run. The default value is "&lt;script&gt;".
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param filename a new script filename.
      */
     public void setScriptFilename(String filename) {
         provider.getRubyInstanceConfig().setScriptFileName(filename);
     }
 
     /**
      * Returns a record separator. The default value is "\n".
      *
      * @since JRuby 1.5.0.
      *
      * @return a record separator.
      */
     public String getRecordSeparator() {
         return provider.getRubyInstanceConfig().getRecordSeparator();
     }
 
     /**
      * Changes a record separator to a given value. If "0" is given, the record
      * separator goes to "\n\n", "777" goes to "\uFFFF", otherwise, an octal value
      * of the given number.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param separator a new record separator value, "0" or "777"
      */
     public void setRecordSeparator(String separator) {
         provider.getRubyInstanceConfig().setRecordSeparator(separator);
     }
 
     /**
      * Returns a value of KCode currently used. The default value is KCode.NONE.
      *
      * @since JRuby 1.5.0.
      *
      * @return a KCode value.
      */
     public KCode getKCode() {
         return provider.getRubyInstanceConfig().getKCode();
     }
 
     /**
      * Changes a value of KCode to a given value. The value should be one of
      * KCode.NONE, KCode.UTF8, KCode.SJIS, or KCode.EUC. The default value is KCode.NONE.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * the given value will be used.
      *
      * @since JRuby 1.5.0.
      *
      * @param kcode a new KCode value.
      */
     public void setKCode(KCode kcode) {
         provider.getRubyInstanceConfig().setKCode(kcode);
     }
 
     /**
      * Returns the value of n, which means that jitted methods are logged in
      * every n methods. The default value is 0.
      *
      * @since JRuby 1.5.0.
      *
      * @return a value that determines how often jitted methods are logged.
      */
     public int getJitLogEvery() {
         return provider.getRubyInstanceConfig().getJitLogEvery();
     }
 
     /**
      * Changes a value of n, so that jitted methods are logged in every n methods.
      * The default value is 0. This value can be set by the jruby.jit.logEvery System
      * property.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * the configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param logEvery a new number of methods.
      */
     public void setJitLogEvery(int logEvery) {
         provider.getRubyInstanceConfig().setJitLogEvery(logEvery);
     }
 
     /**
      * Returns a value of the threshold that determines whether jitted methods'
      * call reached to the limit or not. The default value is -1 when security
      * restriction is applied, or 50 when no security restriction exists.
      *
      * @since JRuby 1.5.0.
      *
      * @return a value of the threshold.
      */
     public int getJitThreshold() {
         return provider.getRubyInstanceConfig().getJitThreshold();
     }
 
     /**
      * Changes a value of the threshold that determines whether jitted methods'
      * call reached to the limit or not. The default value is -1 when security
      * restriction is applied, or 50 when no security restriction exists. This
      * value can be set by jruby.jit.threshold System property.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * the configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param threshold a new value of the threshold.
      */
     public void setJitThreshold(int threshold) {
         provider.getRubyInstanceConfig().setJitThreshold(threshold);
     }
 
     /**
      * Returns a value of a max class cache size. The default value is 0 when
      * security restriction is applied, or 4096 when no security restriction exists.
      *
      * @since JRuby 1.5.0.
      *
      * @return a value of a max class cache size.
      */
     public int getJitMax() {
         return provider.getRubyInstanceConfig().getJitMax();
     }
 
     /**
      * Changes a value of a max class cache size. The default value is 0 when
      * security restriction is applied, or 4096 when no security restriction exists.
      * This value can be set by jruby.jit.max System property.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * the configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param max a new value of a max class cache size.
      */
     public void setJitMax(int max) {
         provider.getRubyInstanceConfig().setJitMax(max);
     }
 
     /**
      * Returns a value of a max size of the bytecode generated by compiler. The
      * default value is -1 when security restriction is applied, or 10000 when
      * no security restriction exists.
      *
      * @since JRuby 1.5.0.
      *
      * @return a value of a max size of the bytecode.
      */
     public int getJitMaxSize() {
         return provider.getRubyInstanceConfig().getJitMaxSize();
     }
 
     /**
      * Changes a value of a max size of the bytecode generated by compiler. The
      * default value is -1 when security restriction is applied, or 10000 when
      * no security restriction exists. This value can be set by jruby.jit.maxsize
      * System property.
      * Call this method before you use put/get, runScriptlet, and parse methods so that
      * the configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param maxSize a new value of a max size of the bytecode.
      */
     public void setJitMaxSize(int maxSize) {
         provider.getRubyInstanceConfig().setJitMaxSize(maxSize);
     }
 
     /**
      * Returns version information about JRuby and Ruby supported by this platform.
      *
      * @return version information.
      */
     public String getSupportedRubyVersion() {
         return OutputStrings.getVersionString(provider.getRubyInstanceConfig().getCompatVersion()).trim();
     }
 
     /**
      * Returns an array of values associated to a key.
      *
      * @param key is a key in a property file
      * @return values associated to the key
      */
     public String[] getProperty(String key) {
         if (basicProperties.containsKey(key)) {
             return (String[]) basicProperties.get(key);
         } else {
             return null;
         }
     }
 
     /**
      * Returns a provider instance of {@link LocalContextProvider}. When users 
      * want to configure Ruby runtime, they can do by setting class loading paths,
      * {@link org.jruby.RubyInstanceConfig} or {@link org.jruby.util.ClassCache}
      * to the provider before they get Ruby runtime.
      * 
      * @return a provider of {@link LocalContextProvider}
      */
     public LocalContextProvider getProvider() {
         return provider;
     }
 
     /**
      * Returns a Ruby runtime in one of {@link LocalContextScope}.
      *
      * @deprecated As of JRuby 1.5.0. Use getProvider().getRuntime() method instead.
      *
      * @return Ruby runtime of a specified local context
      */
     @Deprecated
     public Ruby getRuntime() {
         return provider.getRuntime();
     }
 
     /**
      * Returns a variable map in one of {@link LocalContextScope}. Variables
      * in this map is used to share between Java and Ruby. Map keys are Ruby's
      * variable names, thus they must be valid Ruby names.
      * 
      * @return a variable map specific to the current thread
      */
     public BiVariableMap getVarMap() {
         return provider.getVarMap();
     }
 
     /**
      * Returns a attribute map in one of {@link LocalContextScope}. Attributes
      * in this map accept any key value pair, types of which are java.lang.Object.
      * Ruby scripts do not look up this map.
      * 
      * @return an attribute map specific to the current thread
      */
     public Map getAttributeMap() {
         return provider.getAttributeMap();
     }
 
     /**
      * Returns an attribute value associated with the specified key in
      * a attribute map. This is a short cut method of
      * ScriptingContainer#getAttributeMap().get(key).
      * 
      * @param key is the attribute key
      * @return value is a value associated to the specified key
      */
     public Object getAttribute(Object key) {
         return provider.getAttributeMap().get(key);
     }
 
     /**
      * Associates the specified value with the specified key in a
      * attribute map. If the map previously contained a mapping for the key,
      * the old value is replaced. This is a short cut method of
      * ScriptingContainer#getAttributeMap().put(key, value).
      * 
      * @param key is a key that the specified value is to be associated with
      * @param value is a value to be associated with the specified key
      * @return the previous value associated with key, or null if there was no mapping for key. 
      */
     public Object setAttribute(Object key, Object value) {
         return provider.getAttributeMap().put(key, value);
     }
 
     /**
      * Removes the specified value with the specified key in a
      * attribute map. If the map previously contained a mapping for the key,
      * the old value is returned. This is a short cut method of
      * ScriptingContainer#getAttributeMap().remove(key).
      *
      * @param key is a key that the specified value is to be removed from
      * @return the previous value associated with key, or null if there was no mapping for key.
      */
     public Object removeAttribute(Object key) {
         return provider.getAttributeMap().remove(key);
     }
 
     /**
      * Returns a value of the specified key in a top level of runtime or null
      * if this map doesn't have a mapping for the key. The key
      * must be a valid Ruby variable or constant name.
      * 
      * @param key is a key whose associated value is to be returned
      * @return a value to which the specified key is mapped, or null if this
      *         map contains no mapping for the key
      */
     public Object get(String key) {
         return provider.getVarMap().get(provider.getRuntime().getTopSelf(), key);
     }
 
     /**
      * Returns a value of a specified key in a specified receiver or null if
      * a variable map doesn't have a mapping for the key in a given
      * receiver. The key must be a valid Ruby variable or constant name. A global
      * variable doesn't depend on the receiver.
      *
      * @param receiver a receiver to get the value from
      * @param key is a key whose associated value is to be returned
      * @return a value to which the specified key is mapped, or null if this
      *         map contains no mapping for the key
      */
     public Object get(Object receiver, String key) {
         return provider.getVarMap().get(receiver, key);
     }
 
     /**
      * Associates the specified value with the specified key in a
      * variable map. This key-value pair is injected to a top level of runtime
      * during evaluation. If the map previously contained a mapping for the key,
      * the old value is replaced. The key must be a valid Ruby variable or 
      * constant name. It will be a top level variable or constant. 
      * 
      * @param key is a key that the specified value is to be associated with
      * @param value is a value to be associated with the specified key
      * @return a previous value associated with a key, or null if there was
      *         no mapping for this key.
      */
     public Object put(String key, Object value) {
         return provider.getVarMap().put(provider.getRuntime().getTopSelf(), key, value);
     }
 
     /**
      * Associates the specified value with the specified key in a variable map.
      * This key-value pair is injected to a given receiver during evaluation.
      * If the map previously contained a mapping for the key,
      * the old value is replaced. The key must be a valid Ruby variable or 
      * constant name. A given receiver limits the scope of a variable or constant.
      * However, a global variable is accessible globally always.
      *
      * @param receiver a receiver to put the value in
      * @param key is a key that the specified value is to be associated with
      * @param value is a value to be associated with the specified key
      * @return a previous value associated with a key, or null if there was
      *         no mapping for this key.
      */
     public Object put(Object receiver, String key, Object value) {
         return provider.getVarMap().put(receiver, key, value);
     }
 
     /**
      * Removes the specified Ruby variable with the specified variable name from a
      * variable map and runtime top level. If the map previously contained a
      * mapping for the key, the old value is returned. The key must be a valid
      * Ruby variable name.
      *
      * @param key is a key that the specified value is to be associated with
      * @return a previous value associated with a key, or null if there was
      *         no mapping for this key.
      */
     public Object remove(String key) {
         return remove(provider.getRuntime().getTopSelf(), key);
     }
 
     /**
      * Removes the specified Ruby variable with the specified variable name in a
      * variable map and given receiver. If the map previously contained a mapping for the key,
      * the old value is returned. The key must be a valid Ruby variable name.
      * This is a short cut method of ScriptingContainer#getVarMap().remove(key).
      *
      * @param receiver a receiver to remove the value from
      * @param key is a key that the specified value is to be associated with
      * @return a previous value associated with a key, or null if there was
      *         no mapping for this key.
      */
     public Object remove(Object receiver, String key) {
         return provider.getVarMap().remove(receiver, key);
     }
 
     /**
      * Removes all of the mappings from this map.
      * The map will be empty after this call returns. Ruby variables are also
      * removed from Ruby instance. However, Ruby instance keep having global variable
      * names with null value.
      * This is a short cut method of ScriptingContainer#getVarMap().clear().
      */
     public void clear() {
         provider.getVarMap().clear();
     }
 
     /**
      * Parses a script and return an object which can be run(). This allows
      * the script to be parsed once and evaluated many times.
      * 
      * @param script is a Ruby script to be parsed
      * @param lines are linenumbers to display for parse errors and backtraces.
      *        This field is optional. Only the first argument is used for parsing.
      *        When no line number is specified, 0 is applied to.
      * @return an object which can be run
      */
     public EmbedEvalUnit parse(String script, int... lines) {
         return runtimeAdapter.parse(script, lines);
     }
 
     /**
      * Parses a script given by a reader and return an object which can be run().
      * This allows the script to be parsed once and evaluated many times.
      * 
      * @param reader is used to read a script from
      * @param filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @param lines are linenumbers to display for parse errors and backtraces.
      *        This field is optional. Only the first argument is used for parsing.
      *        When no line number is specified, 0 is applied to.
      * @return an object which can be run
      */
     public EmbedEvalUnit parse(Reader reader, String filename, int... lines) {
         return runtimeAdapter.parse(reader, filename, lines);
     }
 
     /**
      * Parses a script read from a specified path and return an object which can be run().
      * This allows the script to be parsed once and evaluated many times.
      * 
      * @param type is one of the types {@link PathType} defines
      * @param filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @param lines are linenumbers to display for parse errors and backtraces.
      *        This field is optional. Only the first argument is used for parsing.
      *        When no line number is specified, 0 is applied to.
      * @return an object which can be run
      */
     public EmbedEvalUnit parse(PathType type, String filename, int... lines) {
         return runtimeAdapter.parse(type, filename, lines);
     }
 
     /**
      * Parses a script given by a input stream and return an object which can be run().
      * This allows the script to be parsed once and evaluated many times.
      * 
      * @param istream is an input stream to get a script from
      * @param filename filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @param lines are linenumbers to display for parse errors and backtraces.
      *        This field is optional. Only the first argument is used for parsing.
      *        When no line number is specified, 0 is applied to.
      * @return an object which can be run
      */
     public EmbedEvalUnit parse(InputStream istream, String filename, int... lines) {
         return runtimeAdapter.parse(istream, filename, lines);
     }
 
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns a result only if a script returns a value.
      * Right after the parsing, the script is evaluated once.
      *
      * @param script is a Ruby script to get run
      * @return an evaluated result converted to a Java object
      */
     public Object runScriptlet(String script) {
         EmbedEvalUnit unit = parse(script);
         return runUnit(unit);
     }
 
     private Object runUnit(EmbedEvalUnit unit) {
         if (unit == null) {
             return null;
         }
         IRubyObject ret = unit.run();
         return JavaEmbedUtils.rubyToJava(ret);
     }
 
     /**
      * Evaluates a script read from a reader under the current scope
      * (perhaps the top-level scope) and returns a result only if a script
      * returns a value. Right after the parsing, the script is evaluated once.
      * 
      * @param reader is used to read a script from
      * @param filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @return an evaluated result converted to a Java object
      */
     public Object runScriptlet(Reader reader, String filename) {
         EmbedEvalUnit unit = parse(reader, filename);
         return runUnit(unit);
     }
 
     /**
      * Evaluates a script read from a input stream under the current scope
      * (perhaps the top-level scope) and returns a result only if a script
      * returns a value. Right after the parsing, the script is evaluated once.
      *
      * @param istream is used to input a script from
      * @param filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @return an evaluated result converted to a Java object
      */
     public Object runScriptlet(InputStream istream, String filename) {
         EmbedEvalUnit unit = parse(istream, filename);
         return runUnit(unit);
     }
 
     /**
      * Reads a script file from specified path and evaluates it under the current
      * scope (perhaps the top-level scope) and returns a result only if a script
      * returns a value. Right after the parsing, the script is evaluated once.
      * 
      * @param type is one of the types {@link PathType} defines
      * @param filename is used to read the script from and an information
      * @return an evaluated result converted to a Java object
      */
     public Object runScriptlet(PathType type, String filename) {
         EmbedEvalUnit unit = parse(type, filename);
         return runUnit(unit);
     }
 
     /**
      * Returns an instance of {@link EmbedRubyRuntimeAdapter} for embedders to parse
      * scripts.
      * 
      * @return an instance of {@link EmbedRubyRuntimeAdapter}.
      */
     public EmbedRubyRuntimeAdapter newRuntimeAdapter() {
         return runtimeAdapter;
     }
 
     /**
      * Returns an instance of {@link EmbedRubyObjectAdapter} for embedders to invoke
      * methods defined by Ruby. The script must be evaluated prior to a method call.
      * In most cases, users don't need to use this method. ScriptingContainer's
      * callMethods are the shortcut and work in the same way.
      *
      * <pre>Example
      *         # calendar.rb
      *         require 'date'
      *         class Calendar
      *           def initialize;@today = DateTime.now;end
      *           def next_year;@today.year + 1;end
      *         end
      *         Calendar.new
      *
      *
      *         ScriptingContainer container = new ScriptingContainer();
      *         String filename =  "ruby/calendar.rb";
      *         Object receiver = instance.runScriptlet(PathType.CLASSPATH, filename);
      *         EmbedRubyObjectAdapter adapter = instance.newObjectAdapter();
      *         Integer result =
      *             (Integer) adapter.callMethod(receiver, "next_year", Integer.class);
      *         System.out.println("next year: " + result);
      *         System.out.println(instance.get("@today"));
      *
      * Outputs:
      *     next year: 2010
      *     2009-05-19T17:46:44-04:00</pre>
      * 
      * @return an instance of {@link EmbedRubyObjectAdapter}
      */
     public EmbedRubyObjectAdapter newObjectAdapter() {
         return objectAdapter;
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method does not have any argument.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments
      * @return an instance of requested Java type
      */
     public Object callMethod(Object receiver, String methodName, Object... args) {
         return objectAdapter.callMethod(receiver, methodName, args);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method does not have any argument.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param block is a block to be executed in this method
      * @param args is an array of method arguments
      * @return an instance of requested Java type
      */
     public Object callMethod(Object receiver, String methodName, Block block, Object... args) {
         return objectAdapter.callMethod(receiver, methodName, block, args);
     }
     
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method does not have any argument.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Class<T> returnType) {
         return objectAdapter.callMethod(receiver, methodName, returnType);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have only one argument.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param singleArg is an method argument
      * @param returnType returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Object singleArg, Class<T> returnType) {
         return objectAdapter.callMethod(receiver, methodName, singleArg, returnType);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have multiple arguments.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments
      * @param returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Object[] args, Class<T> returnType) {
         return objectAdapter.callMethod(receiver, methodName, args, returnType);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have multiple arguments, one of which is a block.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments except a block
      * @param block is a block to be executed in this method
      * @param returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Object[] args, Block block, Class<T> returnType) {
         return objectAdapter.callMethod(receiver, methodName, args, block, returnType);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method does not have any argument, and users want to inject Ruby's local
      * variables' values from Java.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param returnType is the type we want it to convert to
      * @param unit is parsed unit
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Class<T> returnType, EmbedEvalUnit unit) {
         return objectAdapter.callMethod(receiver, methodName, returnType, unit);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have multiple arguments, and users want to inject Ruby's local
      * variables' values from Java.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments
      * @param returnType is the type we want it to convert to
      * @param unit is parsed unit
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Object[] args, Class<T> returnType, EmbedEvalUnit unit) {
         return objectAdapter.callMethod(receiver, methodName, args, returnType, unit);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have multiple arguments, one of which is a block, and users want to
      * inject Ruby's local variables' values from Java.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments except a block
      * @param block is a block to be executed in this method
      * @param returnType is the type we want it to convert to
      * @param unit is parsed unit
      * @return is the type we want it to convert to
      */
     public <T> T callMethod(Object receiver, String methodName, Object[] args, Block block, Class<T> returnType, EmbedEvalUnit unit) {
         return objectAdapter.callMethod(receiver, methodName, args, block, returnType, unit);
     }
 
     /**
      *
      * @param receiver is an instance that will receive this method call
      * @param args is an array of method arguments
      * @param returnType is the type we want it to convert to
      * @return is the type we want it to convert to
      */
     public <T> T callSuper(Object receiver, Object[] args, Class<T> returnType) {
         return objectAdapter.callSuper(receiver, args, returnType);
     }
 
     /**
      *
      * @param receiver is an instance that will receive this method call
      * @param args is an array of method arguments except a block
      * @param block is a block to be executed in this method
      * @param returnType is the type we want it to convert to
      * @return is the type we want it to convert to
      */
     public <T> T callSuper(Object receiver, Object[] args, Block block, Class<T> returnType) {
         return objectAdapter.callSuper(receiver, args, block, returnType);
     }
 
     /**
      * Returns an instance of a requested interface type. An implementation of
      * the requested interface is done by a Ruby script, which has been evaluated
      * before getting the instance.
      * In most cases, users don't need to use this method. ScriptingContainer's
      * runScriptlet method returns an instance of the interface type that is
      * implemented by Ruby.
      *
      * <pre>Example
      * Interface
      *     //QuadraticFormula.java
      *     package org.jruby.embed;
      *     import java.util.List;
      *     public interface QuadraticFormula {
      *         List solve(int a, int b, int c) throws Exception;
      *     }
      *
      * Implementation
      *     #quadratic_formula.rb
      *     def solve(a, b, c)
      *       v = b ** 2 - 4 * a * c
      *       if v < 0: raise RangeError end
      *       s0 = ((-1)*b - Math.sqrt(v))/(2*a)
      *       s1 = ((-1)*b + Math.sqrt(v))/(2*a)
      *       return s0, s1
      *     end
      *
      * Usage
      *     ScriptingContainer container = new ScriptingContainer();
      *     String filename = "ruby/quadratic_formula_class.rb";
      *     Object receiver = container.runScriptlet(PathType.CLASSPATH, filename);
      *     QuadraticFormula qf = container.getInstance(receiver, QuadraticFormula.class);
      *     try {
      *          List<Double> solutions = qf.solve(1, -2, -13);
      *          printSolutions(solutions);
      *          solutions = qf.solve(1, -2, 13);
      *          for (double s : solutions) {
      *              System.out.print(s + ", ");
      *          }
      *     } catch (Exception e) {
      *          e.printStackTrace();
      *     }
      *
      * Output
      *     -2.7416573867739413, 4.741657386773941, 
      * </pre>
      *
      * 
      * @param receiver is an instance that implements the interface
      * @param clazz is a requested interface
      * @return an instance of a requested interface type
      */
     public <T> T getInstance(Object receiver, Class<T> clazz) {
         return interfaceAdapter.getInstance(receiver, clazz);
     }
 
     /**
      * Replaces a standard input by a specified reader
      * 
      * @param reader is a reader to be set
      */
     public void setReader(Reader reader) {
         if (reader == null) {
             return;
         }
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.READER)) {
             Reader old = (Reader) map.get(AttributeName.READER);
             if (old == reader) {
                 return;
             }
         }
         map.put(AttributeName.READER, reader);
         InputStream istream = new ReaderInputStream(reader);
         Ruby runtime = provider.getRuntime();
         RubyIO io = new RubyIO(runtime, istream);
         io.getOpenFile().getMainStream().setSync(true);
         runtime.defineVariable(new InputGlobalVariable(runtime, "$stdin", io));
         runtime.getObject().getConstantMapForWrite().put("STDIN", io);
     }
 
     /**
      * Returns a reader set in an attribute map.
      *
      * @return a reader in an attribute map
      */
     public Reader getReader() {
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.READER)) {
             return (Reader) getAttributeMap().get(AttributeName.READER);
         }
         return null;
     }
 
     /**
      * Returns an input stream that Ruby runtime has. The stream is set when
      * Ruby runtime is initialized.
      *
      * @deprecated As of JRuby 1.5.0, replaced by getInput().
      * 
      * @return an input stream that Ruby runtime has.
      */
     @Deprecated
     public InputStream getIn() {
         return getInput();
     }
 
     /**
      * Replaces a standard output by a specified writer.
      *
      * @param writer is a writer to be set
      */
     public void setWriter(Writer writer) {
         if (writer == null) {
             return;
         }
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.WRITER)) {
             Writer old = (Writer) map.get(AttributeName.WRITER);
             if (old == writer) {
                 return;
             }
         }
         map.put(AttributeName.WRITER, writer);
         PrintStream pstream = new PrintStream(new WriterOutputStream(writer));
         setOutputStream(pstream);
     }
 
     private void setOutputStream(PrintStream pstream) {
         if (pstream == null) {
             return;
         }
         Ruby runtime = provider.getRuntime();
-        RubyIO io = new RubyIO(runtime, pstream);
+        RubyIO io = new RubyIO(runtime, pstream, false);
         io.getOpenFile().getMainStream().setSync(true);
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stdout", io));
         runtime.getObject().getConstantMapForWrite().put("STDOUT", io);
         runtime.getGlobalVariables().alias("$>", "$stdout");
         runtime.getGlobalVariables().alias("$defout", "$stdout");
     }
 
     public void resetWriter() {
         PrintStream pstream = provider.getRubyInstanceConfig().getOutput();
         setOutputStream(pstream);
     }
 
     /**
      * Returns a writer set in an attribute map.
      * 
      * @return a writer in a attribute map
      */
     public Writer getWriter() {
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.WRITER)) {
             return (Writer) getAttributeMap().get(AttributeName.WRITER);
         }
         return null;
     }
 
     /**
      * Returns an output stream that Ruby runtime has. The stream is set when
      * Ruby runtime is initialized.
      *
      * @deprecated As of JRuby 1.5.0, replaced by getOutput().
      * 
      * @return an output stream that Ruby runtime has
      */
     @Deprecated
     public PrintStream getOut() {
         return getOutput();
     }
 
     /**
      * Replaces a standard error by a specified writer.
      * 
      * @param errorWriter is a writer to be set
      */
     public void setErrorWriter(Writer errorWriter) {
         if (errorWriter == null) {
             return;
         }
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.ERROR_WRITER)) {
             Writer old = (Writer) map.get(AttributeName.ERROR_WRITER);
             if (old == errorWriter) {
                 return;
             }
         }
         map.put(AttributeName.ERROR_WRITER, errorWriter);
         PrintStream pstream = new PrintStream(new WriterOutputStream(errorWriter));
         setErrorStream(pstream);
     }
 
     private void setErrorStream(PrintStream error) {
         if (error == null) {
             return;
         }
         Ruby runtime = provider.getRuntime();
-        RubyIO io = new RubyIO(runtime, error);
+        RubyIO io = new RubyIO(runtime, error, false);
         io.getOpenFile().getMainStream().setSync(true);
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stderr", io));
         runtime.getObject().getConstantMapForWrite().put("STDERR", io);
         runtime.getGlobalVariables().alias("$deferr", "$stderr");
     }
 
     public void resetErrorWriter() {
         PrintStream error = provider.getRubyInstanceConfig().getError();
         setErrorStream(error);
     }
 
     /**
      * Returns an error writer set in an attribute map.
      *
      * @return an error writer in a attribute map
      */
     public Writer getErrorWriter() {
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.ERROR_WRITER)) {
             return (Writer) getAttributeMap().get(AttributeName.ERROR_WRITER);
         }
         return null;
     }
 
     /**
      * Returns an error output stream that Ruby runtime has. The stream is set when
      * Ruby runtime is initialized.
      *
      * @deprecated As of JRuby 1.5.0, Replaced by getError()
      * 
      * @return an error output stream that Ruby runtime has
      */
     @Deprecated
     public PrintStream getErr() {
         return getError();
     }
 
     /**
      * Cleanly shut down this ScriptingContainer and any JRuby resources it holds.
      * All ScriptingContainer instances should be terminated when you are done with
      * them, rather then leaving them for GC to finalize.
      *
      * @since JRuby 1.5.0
      */
     public void terminate() {
         getProvider().getRuntime().tearDown(false);
         getProvider().terminate();
     }
 
     /**
      * Ensure this ScriptingContainer instance is terminated when nobody holds any
      * references to it (and GC wants to reclaim it).
      * 
      * @throws Throwable
      * 
      * @since JRuby 1.6.0
      */
     public void finalize() throws Throwable {
         super.finalize();
         terminate();
     }
 }
\ No newline at end of file
diff --git a/src/org/jruby/embed/jsr223/Utils.java b/src/org/jruby/embed/jsr223/Utils.java
index f044e3868f..a7dc2301d7 100644
--- a/src/org/jruby/embed/jsr223/Utils.java
+++ b/src/org/jruby/embed/jsr223/Utils.java
@@ -1,194 +1,195 @@
 /**
  * **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2009-2011 Yoko Harada <yokolet@gmail.com>
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
  * **** END LICENSE BLOCK *****
  */
 package org.jruby.embed.jsr223;
 
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import javax.script.Bindings;
 import javax.script.ScriptContext;
 import javax.script.ScriptEngine;
 import org.jruby.embed.AttributeName;
 import org.jruby.embed.LocalVariableBehavior;
 import org.jruby.embed.ScriptingContainer;
 import org.jruby.embed.variable.TransientLocalVariable;
 import org.jruby.embed.variable.VariableInterceptor;
 
 /**
  * A collection of JSR223 specific utility methods.
  *
  * @author Yoko Harada <yokolet@gmail.com>
  */
 public class Utils {
     /**
      * Gets line number value from engine's attribute map.
      *
      * @param context ScriptContext to be used to the evaluation
      * @return line number
      */
     static int getLineNumber(ScriptContext context) {
         Object obj = context.getAttribute(AttributeName.LINENUMBER.toString(), ScriptContext.ENGINE_SCOPE);
         if (obj instanceof Integer) {
             return (Integer)obj;
         }
         return 0;
     }
 
     /**
      * Gets a receiver object from engine's attribute map.
      *
      * @param context ScriptContext to be used to the evaluation
      * @return receiver object or null if the attribute doesn't exist
      */
     static Object getReceiver(ScriptContext context) {
         return context.getAttribute(AttributeName.RECEIVER.toString(), ScriptContext.ENGINE_SCOPE);
     }
 
     static String getFilename(ScriptContext context) {
         Object filename = context.getAttribute(ScriptEngine.FILENAME);
         return filename != null ? (String)filename : "<script>";
     }
 
     static boolean isTerminationOn(ScriptContext context) {
         Object obj = context.getAttribute(AttributeName.TERMINATION.toString());
         if (obj != null && obj instanceof Boolean && ((Boolean) obj) == true) {
             return true;
         }
         return false;
     }
 
     static void preEval(ScriptingContainer container, ScriptContext context) {
         Object receiver = Utils.getReceiverObject(context);
+
         Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
         for (Map.Entry<String, Object> entry : bindings.entrySet()) {
             Utils.put(container, receiver, entry.getKey(), entry.getValue(), context);
         }
-        
+
         //container.setReader(context.getReader());
         container.setWriter(context.getWriter());
         container.setErrorWriter(context.getErrorWriter());
-        
+
         // if key of globalMap exists in engineMap, this key-value pair should be skipped.
         bindings = context.getBindings(ScriptContext.GLOBAL_SCOPE);
         if (bindings == null) return;
         for (Map.Entry<String, Object> entry : bindings.entrySet()) {
             if (container.getVarMap().containsKey(entry.getKey())) continue;
             Utils.put(container, receiver, entry.getKey(), entry.getValue(), context);
         }
     }
 
     private static Object getReceiverObject(ScriptContext context) {
         if (context == null) return null;
         return context.getAttribute(AttributeName.RECEIVER.toString(), ScriptContext.ENGINE_SCOPE);
     }
 
     static void postEval(ScriptingContainer container, ScriptContext context) {
         if (context == null) return;
         Object receiver = Utils.getReceiverObject(context);
-        
+
         Bindings engineMap = context.getBindings(ScriptContext.ENGINE_SCOPE);
         int size = engineMap.keySet().size();
         String[] names = engineMap.keySet().toArray(new String[size]);
         Iterator<Map.Entry<String, Object>> iter = engineMap.entrySet().iterator();
         for (;iter.hasNext();) {
             Map.Entry<String, Object> entry = iter.next();
             if (Utils.shouldLVarBeDeleted(container, entry.getKey())) {
                 iter.remove();
             }
         }
         Set<String> keys = container.getVarMap().keySet();
         if (keys != null && keys.size() > 0) {
             for (String key : keys) {
                 Object value = container.getVarMap().get(key);
                 engineMap.put(Utils.adjustKey(key), value);
             }
         }
 
         Bindings globalMap = context.getBindings(ScriptContext.GLOBAL_SCOPE);
         if (globalMap == null) return;
         keys = globalMap.keySet();
         if (keys != null && keys.size() > 0) {
             for (String key : keys) {
                 if (engineMap.containsKey(key)) continue;
                 Object value = container.getVarMap().get(receiver, key);
                 globalMap.put(key, value);
             }
         }
     }
 
     private static Object put(ScriptingContainer container, Object receiver, String key, Object value, ScriptContext context) {
         Object oldValue = null;
         String adjustedKey = Utils.adjustKey(key);
         if (Utils.isRubyVariable(container, adjustedKey)) {
             boolean sharing_variables = true;
             Object obj = context.getAttribute(AttributeName.SHARING_VARIABLES.toString(), ScriptContext.ENGINE_SCOPE);
             if (obj != null && obj instanceof Boolean && ((Boolean) obj) == false) {
                 sharing_variables = false;
             }
             if (sharing_variables || "ARGV".equals(adjustedKey)) {
                 oldValue = container.put(receiver, adjustedKey, value);
             }
         } else {
             if (adjustedKey.equals(AttributeName.SHARING_VARIABLES.toString())) {
                 oldValue = container.setAttribute(AttributeName.SHARING_VARIABLES, value);
             } else {
                 oldValue = container.setAttribute(adjustedKey, value);
             }
             /* Maybe no need anymore?
             if (container.getAttributeMap().containsKey(BACKED_BINDING)) {
                 Bindings b = (Bindings) container.getAttribute(BACKED_BINDING);
                 b.put(key, value);
             }
              *
              */
         }
         return oldValue;
     }
 
     static boolean isRubyVariable(ScriptingContainer container, String name) {
         return VariableInterceptor.isKindOfRubyVariable(container.getProvider().getLocalVariableBehavior(), name);
     }
 
     private static String adjustKey(String key) {
         if (key.equals(ScriptEngine.ARGV)) {
             return "ARGV";
         } if ("ARGV".equals(key)) {
             return ScriptEngine.ARGV;
         } else {
             return key;
         }
     }
     
     private static boolean shouldLVarBeDeleted(ScriptingContainer container, String key) {
         LocalVariableBehavior behavior = container.getProvider().getLocalVariableBehavior();
         if (behavior != LocalVariableBehavior.TRANSIENT) return false;
         return TransientLocalVariable.isValidName(key);
     }
 }
