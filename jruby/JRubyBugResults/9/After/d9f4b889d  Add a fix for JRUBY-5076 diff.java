diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index de044aec89..58b2977db4 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,3863 +1,3866 @@
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
 
 import java.util.concurrent.atomic.AtomicInteger;
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
 
+import org.jruby.util.io.SelectorFactory;
+import java.nio.channels.spi.SelectorProvider;
+
 import static org.jruby.CompatVersion.*;
 import static org.jruby.RubyEnumerator.enumeratorize;
 
 /**
  * 
  * @author jpetersen
  */
 @JRubyClass(name="IO", include="Enumerable")
 public class RubyIO extends RubyObject {
     protected OpenFile openFile;
     protected List<RubyThread> blockingThreads;
     protected IRubyObject externalEncoding;
     protected IRubyObject internalEncoding;
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor) {
     }
 
     @Deprecated
     public void unregisterDescriptor(int aFileno) {
     }
 
     @Deprecated
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return ChannelDescriptor.getDescriptorByFileno(aFileno);
     }
 
     @Deprecated
     public static int getNewFileno() {
         return ChannelDescriptor.getNewFileno();
     }
 
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
                 IRubyObject str = readAll(null);
                 if (((RubyString)str).getByteList().length() == 0) {
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
 
                             if (n == -1) {
                                 if (!readStream.isBlocking() && (readStream instanceof ChannelStream)) {
                                     checkDescriptor(runtime, ((ChannelStream) readStream).getDescriptor());
                                     continue;
                                 } else {
                                     break;
                                 }
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
 
                 if (n == -1) {
                     if (!readStream.isBlocking() && (readStream instanceof ChannelStream)) {
                         checkDescriptor(runtime, ((ChannelStream)readStream).getDescriptor());
                         continue;
                     } else {
                         break;
                     }
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
 
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         IRubyObject pathString = args[0].convertToString();
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
 
     protected boolean waitWritable(ChannelDescriptor descriptor) throws IOException {
         Channel channel = descriptor.getChannel();
         if (channel == null || !(channel instanceof SelectableChannel)) {
             return false;
         }
 
         SelectableChannel selectable = (SelectableChannel)channel;
         Selector selector = null;
         synchronized (selectable.blockingLock()) {
             boolean oldBlocking = selectable.isBlocking();
             try {
-                selector = Selector.open();
+                selector = SelectorFactory.openWithRetryFrom(getRuntime(), SelectorProvider.provider());
 
                 selectable.configureBlocking(false);
                 int real_ops = selectable.validOps() & SelectionKey.OP_WRITE;
                 SelectionKey key = selectable.keyFor(selector);
 
                 if (key == null) {
                     selectable.register(selector, real_ops, descriptor);
                 } else {
                     key.interestOps(key.interestOps()|real_ops);
                 }
 
                 while(selector.select() == 0) {}
 
                 for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                     SelectionKey skey = (SelectionKey) i.next();
                     if ((skey.interestOps() & skey.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                         if(skey.attachment() == descriptor) {
                             return true;
                         }
                     }
                 }
                 return false;
             } finally {
                 if (selector != null) {
                     try {
                         selector.close();
                     } catch (Exception e) {
                     }
                 }
                 selectable.configureBlocking(oldBlocking);
             }
         }
     }
 
     /*
      * Throw bad file descriptor is we can not read on supplied descriptor.
      */
     private void checkDescriptor(Ruby runtime, ChannelDescriptor descriptor) throws IOException {
         if (!(waitReadable(descriptor))) throw runtime.newIOError("bad file descriptor: " + openFile.getPath());
     }
 
     protected boolean waitReadable(ChannelDescriptor descriptor) throws IOException {
         Channel channel = descriptor.getChannel();
         if (channel == null || !(channel instanceof SelectableChannel)) {
             return false;
         }
 
         SelectableChannel selectable = (SelectableChannel)channel;
         Selector selector = null;
         synchronized (selectable.blockingLock()) {
             boolean oldBlocking = selectable.isBlocking();
             try {
-                selector = Selector.open();
+                selector = SelectorFactory.openWithRetryFrom(getRuntime(), SelectorProvider.provider());
 
                 selectable.configureBlocking(false);
                 int real_ops = selectable.validOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
                 SelectionKey key = selectable.keyFor(selector);
 
                 if (key == null) {
                     selectable.register(selector, real_ops, descriptor);
                 } else {
                     key.interestOps(key.interestOps()|real_ops);
                 }
 
                 while(selector.select() == 0);
 
                 for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                     SelectionKey skey = (SelectionKey) i.next();
                     if ((skey.interestOps() & skey.readyOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0) {
                         if(skey.attachment() == descriptor) {
                             return true;
                         }
                     }
                 }
                 return false;
             } finally {
                 if (selector != null) {
                     try {
                         selector.close();
                     } catch (Exception e) {
                     }
                 }
                 selectable.configureBlocking(oldBlocking);
             }
         }
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
 
                     if(eagain && waitWritable(writeStream.getDescriptor())) {
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
     
     /**
      * @deprecated
      * @return
      */
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
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
 
     @Deprecated
     public IRubyObject gets(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? gets(context) : gets(context, args[0]);
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
         
         return runtime.newFixnum(0);
     }
     
     private static final ByteList NIL_BYTELIST = ByteList.create("nil");
     private static final ByteList RECURSIVE_BYTELIST = ByteList.create("[...]");
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(ThreadContext context, IRubyObject[] args) {
         return puts(context, this, args);
     }
 
     public static IRubyObject puts(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         if (args.length == 0) {
             return writeSeparator(context, maybeIO);
         }
 
         return putsArray(context, maybeIO, args);
     }
 
     private static IRubyObject writeSeparator(ThreadContext context, IRubyObject maybeIO) {
         Ruby runtime = context.getRuntime();
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         write(context, maybeIO, separator.getByteList());
         return runtime.getNil();
     }
 
     private static IRubyObject putsArray(ThreadContext context, IRubyObject maybeIO, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         assert runtime.getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString) runtime.getGlobalVariables().getDefaultSeparator();
 
         for (int i = 0; i < args.length; i++) {
             ByteList line;
 
             if (args[i].isNil()) {
                 line = getNilByteList(runtime);
             } else if (runtime.isInspecting(args[i])) {
                 line = RECURSIVE_BYTELIST;
             } else if (args[i] instanceof RubyArray) {
                 inspectPuts(context, maybeIO, (RubyArray) args[i]);
                 continue;
             } else {
                 line = args[i].asString().getByteList();
             }
 
             write(context, maybeIO, line);
 
             if (line.length() == 0 || !line.endsWith(separator.getByteList())) {
                 write(context, maybeIO, separator.getByteList());
             }
         }
         return runtime.getNil();
     }
 
     protected void write(ThreadContext context, ByteList byteList) {
         callMethod(context, "write", RubyString.newStringShared(context.getRuntime(), byteList));
     }
 
     protected static void write(ThreadContext context, IRubyObject maybeIO, ByteList byteList) {
         maybeIO.callMethod(context, "write", RubyString.newStringShared(context.getRuntime(), byteList));
     }
 
     private static IRubyObject inspectPuts(ThreadContext context, IRubyObject maybeIO, RubyArray array) {
         try {
             context.getRuntime().registerInspecting(array);
             return putsArray(context, maybeIO, array.toJavaArray());
         } finally {
             context.getRuntime().unregisterInspecting(array);
         }
     }
 
     @Deprecated
     public IRubyObject readline(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? readline(context) : readline(context, args[0]);
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "readline", writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context) {
         IRubyObject line = gets(context);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
         
         return line;
     }
 
     @JRubyMethod(name = "readline", writes = FrameField.LASTLINE)
     public IRubyObject readline(ThreadContext context, IRubyObject separator) {
         IRubyObject line = gets(context, separator);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
 
         return line;
     }
 
     /** Read a byte. On EOF returns nil.
      * 
      */
     @JRubyMethod(name = {"getc", "getbyte"})
     public IRubyObject getc() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
 
             myOpenFile.checkReadable(getRuntime());
             myOpenFile.setReadBuffered();
 
             Stream stream = myOpenFile.getMainStream();
             
             readCheck(stream);
             stream.clearerr();
         
             int c = myOpenFile.getMainStream().fgetc();
             
             if (c == -1) {
                 // TODO: check for ferror, clear it, and try once more up above readCheck
 //                if (ferror(f)) {
 //                    clearerr(f);
 //                    if (!rb_io_wait_readable(fileno(f)))
 //                        rb_sys_fail(fptr->path);
 //                    goto retry;
 //                }
                 return getRuntime().getNil();
             }
         
             return getRuntime().newFixnum(c);
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
     
     private void readCheck(Stream stream) {
         if (!stream.readDataBuffered()) {
             openFile.checkClosed(getRuntime());
         }
     }
     
     /** 
      * <p>Pushes char represented by int back onto IOS.</p>
      * 
      * @param number to push back
      */
     @JRubyMethod(name = "ungetc", required = 1)
     public IRubyObject ungetc(IRubyObject number) {
         int ch = RubyNumeric.fix2int(number);
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (!myOpenFile.isReadBuffered()) {
             throw getRuntime().newIOError("unread stream");
         }
         
         try {
             myOpenFile.checkReadable(getRuntime());
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStream().ungetc(ch) == -1 && ch != -1) {
                 throw getRuntime().newIOError("ungetc failed");
             }
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "read_nonblock", required = 1, optional = 1, backtrace = true)
     public IRubyObject read_nonblock(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, true);
 
         if (value.isNil()) throw context.getRuntime().newEOFError();
 
         if (value instanceof RubyString) {
             RubyString str = (RubyString) value;
             if (str.isEmpty()) {
                 Ruby ruby = context.getRuntime();
                 RaiseException eagain = ruby.newErrnoEAGAINError("");
 
                 // FIXME: *oif* 1.9 actually does this
                 if (ruby.is1_9()) {
                     eagain.getException().extend(new IRubyObject[] {ruby.getIO().getConstant("WaitReadable")});
                 }
 
                 throw eagain;
             }
         }
 
         return value;
     }
 
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
     public IRubyObject readpartial(ThreadContext context, IRubyObject[] args) {
         IRubyObject value = getPartial(context, args, false);
 
         if (value.isNil()) throw context.getRuntime().newEOFError();
 
         return value;
     }
 
     private IRubyObject getPartial(ThreadContext context, IRubyObject[] args, boolean isNonblocking) {
         Ruby runtime = context.getRuntime();
 
         // Length to read
         int length = RubyNumeric.fix2int(args[0]);
         if (length < 0) throw runtime.newArgumentError("negative length " + length + " given");
 
         // String/Buffer to read it into
         IRubyObject stringArg = args.length > 1 ? args[1] : RubyString.newEmptyString(runtime);
         RubyString string = stringArg.isNil() ? RubyString.newEmptyString(runtime) : stringArg.convertToString();
         
         openFile.checkClosed(runtime);
 
         if (!(openFile.getMainStream() instanceof ChannelStream)) { // cryptic for the uninitiated...
             throw runtime.newNotImplementedError("readpartial only works with Nio based handlers");
         }
         
         try {
             ChannelStream stream = (ChannelStream) openFile.getMainStream();
             ByteList buf = isNonblocking ? stream.readnonblock(length) : stream.readpartial(length);
             boolean empty = buf == null || buf.length() == 0;
 
             string.view(empty ? ByteList.EMPTY_BYTELIST : buf);
 
             if (stream.feof() && empty) return runtime.getNil();
 
             return string;
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             throw runtime.newEOFError(e.getMessage());
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "sysread", required = 1, optional = 1)
     public IRubyObject sysread(ThreadContext context, IRubyObject[] args) {
         int len = (int)RubyNumeric.num2long(args[0]);
         if (len < 0) throw getRuntime().newArgumentError("Negative size");
 
         try {
             RubyString str;
             ByteList buffer;
             if (args.length == 1 || args[1].isNil()) {
                 if (len == 0) {
                     return RubyString.newEmptyString(getRuntime());
                 }
                 
                 buffer = new ByteList(len);
                 str = RubyString.newString(getRuntime(), buffer);
             } else {
                 str = args[1].convertToString();
                 str.modify(len);
                 
                 if (len == 0) {
                     return str;
                 }
                 
                 buffer = str.getByteList();
                 buffer.length(0);
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 throw getRuntime().newIOError("sysread for buffered IO");
             }
             
             // TODO: Ruby locks the string here
             
             myOpenFile.checkClosed(getRuntime());
             
             // TODO: Ruby re-checks that the buffer string hasn't been modified
 
             // select until read is ready
             context.getThread().beforeBlockingCall();
             context.getThread().select(this, SelectionKey.OP_READ);
 
             int bytesRead = myOpenFile.getMainStream().getDescriptor().read(len, str.getByteList());
             
             // TODO: Ruby unlocks the string here
             
             // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?
             
             if (bytesRead == -1 || (bytesRead == 0 && len > 0)) {
                 throw getRuntime().newEOFError();
             }
             
             str.setTaint(true);
             
             return str;
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
     	} catch (IOException e) {
             synthesizeSystemCallError(e);
             return null;
     	} finally {
             context.getThread().afterBlockingCall();
         }
     }
 
     /**
      * Java does not give us enough information for specific error conditions
      * so we are reduced to divining them through string matches...
      */
     // TODO: Should ECONNABORTED get thrown earlier in the descriptor itself or is it ok to handle this late?
     // TODO: Should we include this into Errno code somewhere do we can use this from other places as well?
     private void synthesizeSystemCallError(IOException e) {
         String errorMessage = e.getMessage();
         // All errors to sysread should be SystemCallErrors, but on a closed stream
         // Ruby returns an IOError.  Java throws same exception for all errors so
         // we resort to this hack...
         if ("File not open".equals(errorMessage)) {
             throw getRuntime().newIOError(e.getMessage());
         } else if ("An established connection was aborted by the software in your host machine".equals(errorMessage)) {
             throw getRuntime().newErrnoECONNABORTEDError();
         }
 
         throw getRuntime().newSystemCallError(e.getMessage());
     }
     
     public IRubyObject read(IRubyObject[] args) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         switch (args.length) {
         case 0: return read(context);
         case 1: return read(context, args[0]);
         case 2: return read(context, args[0], args[1]);
         default: throw getRuntime().newArgumentError(args.length, 2);
         }
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             return readAll(context.getRuntime().getNil());
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (EOFException ex) {
             throw getRuntime().newEOFError();
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context, IRubyObject arg0) {
         if (arg0.isNil()) {
             return read(context);
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         int length = RubyNumeric.num2int(arg0);
         
         if (length < 0) {
             throw getRuntime().newArgumentError("negative length " + length + " given");
         }
         
         RubyString str = null;
         
         return readNotAll(context, myOpenFile, length, str);
     }
     
     @JRubyMethod(name = "read")
     public IRubyObject read(ThreadContext context, IRubyObject arg0, IRubyObject arg1) {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (arg0.isNil()) {
             try {
                 myOpenFile.checkReadable(getRuntime());
                 myOpenFile.setReadBuffered();
 
                 return readAll(arg1);
             } catch (PipeException ex) {
                 throw getRuntime().newErrnoEPIPEError();
             } catch (InvalidValueException ex) {
                 throw getRuntime().newErrnoEINVALError();
             } catch (EOFException ex) {
                 throw getRuntime().newEOFError();
             } catch (IOException ex) {
                 throw getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException ex) {
                 throw getRuntime().newErrnoEBADFError();
             }
         }
         
         int length = RubyNumeric.num2int(arg0);
         
         if (length < 0) {
             throw getRuntime().newArgumentError("negative length " + length + " given");
         }
         
         RubyString str = null;
 //        ByteList buffer = null;
         if (arg1.isNil()) {
 //            buffer = new ByteList(length);
 //            str = RubyString.newString(getRuntime(), buffer);
         } else {
             str = arg1.convertToString();
             str.modify(length);
 
             if (length == 0) {
                 return str;
             }
 
 //            buffer = str.getByteList();
         }
         
         return readNotAll(context, myOpenFile, length, str);
     }
     
     private IRubyObject readNotAll(ThreadContext context, OpenFile myOpenFile, int length, RubyString str) {
         Ruby runtime = context.getRuntime();
         
         try {
             myOpenFile.checkReadable(runtime);
             myOpenFile.setReadBuffered();
 
             if (myOpenFile.getMainStream().feof()) {
                 return runtime.getNil();
             }
 
             // TODO: Ruby locks the string here
 
             // READ_CHECK from MRI io.c
             readCheck(myOpenFile.getMainStream());
 
             // TODO: check buffer length again?
     //        if (RSTRING(str)->len != len) {
     //            rb_raise(rb_eRuntimeError, "buffer string modified");
     //        }
 
             // TODO: read into buffer using all the fread logic
     //        int read = openFile.getMainStream().fread(buffer);
             ByteList newBuffer = myOpenFile.getMainStream().fread(length);
 
             // TODO: Ruby unlocks the string here
 
             // TODO: change this to check number read into buffer once that's working
     //        if (read == 0) {
             
             if (newBuffer == null || newBuffer.length() == 0) {
                 if (myOpenFile.getMainStream() == null) {
                     return runtime.getNil();
                 }
 
                 if (myOpenFile.getMainStream().feof()) {
                     // truncate buffer string to zero, if provided
                     if (str != null) {
                         str.setValue(ByteList.EMPTY_BYTELIST.dup());
                     }
                 
                     return runtime.getNil();
                 }
 
                 // Removed while working on JRUBY-2386, since fixes for that
                 // modified EOF logic such that this check is not really valid.
                 // We expect that an EOFException will be thrown now in EOF
                 // cases.
 //                if (length > 0) {
 //                    // I think this is only partly correct; sys fail based on errno in Ruby
 //                    throw getRuntime().newEOFError();
 //                }
             }
 
 
             // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?
 
             // FIXME: I don't like the null checks here
             if (str == null) {
                 if (newBuffer == null) {
                     str = RubyString.newEmptyString(runtime);
                 } else {
                     str = RubyString.newString(runtime, newBuffer);
                 }
             } else {
                 if (newBuffer == null) {
                     str.empty();
                 } else {
                     str.setValue(newBuffer);
                 }
             }
             str.setTaint(true);
 
             return str;
         } catch (EOFException ex) {
             throw runtime.newEOFError();
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException ex) {
             throw runtime.newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
     }
     
     protected IRubyObject readAll(IRubyObject buffer) throws BadDescriptorException, EOFException, IOException {
         Ruby runtime = getRuntime();
         // TODO: handle writing into original buffer better
         
         RubyString str = null;
         if (buffer instanceof RubyString) {
             str = (RubyString)buffer;
         }
         
         // TODO: ruby locks the string here
         
         // READ_CHECK from MRI io.c
         if (openFile.getMainStream().readDataBuffered()) {
             openFile.checkClosed(runtime);
         }
         
         try {
             ByteList newBuffer = openFile.getMainStream().readall();
 
             // TODO same zero-length checks as file above
 
             if (str == null) {
                 if (newBuffer == null) {
                     str = RubyString.newEmptyString(runtime);
                 } else {
                     str = RubyString.newString(runtime, newBuffer);
                 }
             } else {
                 if (newBuffer == null) {
                     str.empty();
                 } else {
                     str.setValue(newBuffer);
                 }
             }
         } catch (NonReadableChannelException ex) {
             throw runtime.newIOError("not opened for reading");
         }
 
         str.taint(runtime.getCurrentContext());
 
         return str;
 //        long bytes = 0;
 //        long n;
 //
 //        if (siz == 0) siz = BUFSIZ;
 //        if (NIL_P(str)) {
 //            str = rb_str_new(0, siz);
 //        }
 //        else {
 //            rb_str_resize(str, siz);
 //        }
 //        for (;;) {
 //            rb_str_locktmp(str);
 //            READ_CHECK(fptr->f);
 //            n = io_fread(RSTRING(str)->ptr+bytes, siz-bytes, fptr);
 //            rb_str_unlocktmp(str);
 //            if (n == 0 && bytes == 0) {
 //                if (!fptr->f) break;
 //                if (feof(fptr->f)) break;
 //                if (!ferror(fptr->f)) break;
 //                rb_sys_fail(fptr->path);
 //            }
 //            bytes += n;
 //            if (bytes < siz) break;
 //            siz += BUFSIZ;
 //            rb_str_resize(str, siz);
 //        }
 //        if (bytes != siz) rb_str_resize(str, bytes);
 //        OBJ_TAINT(str);
 //
 //        return str;
     }
     
     // TODO: There's a lot of complexity here due to error handling and
     // nonblocking IO; much of this goes away, but for now I'm just
     // having read call ChannelStream.fread directly.
 //    protected int fread(int len, ByteList buffer) {
 //        long n = len;
 //        int c;
 //        int saved_errno;
 //
 //        while (n > 0) {
 //            c = read_buffered_data(ptr, n, fptr->f);
 //            if (c < 0) goto eof;
 //            if (c > 0) {
 //                ptr += c;
 //                if ((n -= c) <= 0) break;
 //            }
 //            rb_thread_wait_fd(fileno(fptr->f));
 //            rb_io_check_closed(fptr);
 //            clearerr(fptr->f);
 //            TRAP_BEG;
 //            c = getc(fptr->f);
 //            TRAP_END;
 //            if (c == EOF) {
 //              eof:
 //                if (ferror(fptr->f)) {
 //                    switch (errno) {
 //                      case EINTR:
 //    #if defined(ERESTART)
 //                      case ERESTART:
 //    #endif
 //                        clearerr(fptr->f);
 //                        continue;
 //                      case EAGAIN:
 //    #if defined(EWOULDBLOCK) && EWOULDBLOCK != EAGAIN
 //                      case EWOULDBLOCK:
 //    #endif
 //                        if (len > n) {
 //                            clearerr(fptr->f);
 //                        }
 //                        saved_errno = errno;
 //                        rb_warning("nonblocking IO#read is obsolete; use IO#readpartial or IO#sysread");
 //                        errno = saved_errno;
 //                    }
 //                    if (len == n) return 0;
 //                }
 //                break;
 //            }
 //            *ptr++ = c;
 //            n--;
 //        }
 //        return len - n;
 //        
 //    }
 
     /** Read a byte. On EOF throw EOFError.
      * 
      */
     @JRubyMethod(name = "readchar")
     public IRubyObject readchar() {
         IRubyObject c = getc();
         
         if (c.isNil()) throw getRuntime().newEOFError();
         
         return c;
     }
     
     @JRubyMethod
     public IRubyObject stat(ThreadContext context) {
         openFile.checkClosed(context.getRuntime());
         return context.getRuntime().newFileStat(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
     }
 
     /** 
      * <p>Invoke a block for each byte.</p>
      */
     public IRubyObject each_byte(ThreadContext context, Block block) {
         Ruby runtime = context.getRuntime();
         
     	try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             while (true) {
                 myOpenFile.checkReadable(runtime);
                 myOpenFile.setReadBuffered();
 
                 // TODO: READ_CHECK from MRI
                 
                 int c = myOpenFile.getMainStream().fgetc();
                 
                 if (c == -1) {
                     // TODO: check for error, clear it, and wait until readable before trying once more
 //                    if (ferror(f)) {
 //                        clearerr(f);
 //                        if (!rb_io_wait_readable(fileno(f)))
 //                            rb_sys_fail(fptr->path);
 //                        continue;
 //                    }
                     break;
                 }
                 
                 assert c < 256;
                 block.yield(context, getRuntime().newFixnum(c));
             }
 
             // TODO: one more check for error
 //            if (ferror(f)) rb_sys_fail(fptr->path);
             return this;
         } catch (PipeException ex) {
             throw runtime.newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (EOFException e) {
             return runtime.getNil();
     	} catch (IOException e) {
     	    throw runtime.newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "each_byte", frame = true)
     public IRubyObject each_byte19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_byte(context, block) : enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "bytes")
     public IRubyObject bytes(final ThreadContext context) {
         return enumeratorize(context.getRuntime(), this, "each_byte");
     }
 
     @JRubyMethod(name = "lines")
     public IRubyObject lines(final ThreadContext context) {
         return enumeratorize(context.getRuntime(), this, "each_line");
     }
 
     public IRubyObject each_char(final ThreadContext context, final Block block) {
         Ruby runtime = context.getRuntime();
         IRubyObject ch;
 
         while(!(ch = getc()).isNil()) {
             byte c = (byte)RubyNumeric.fix2int(ch);
             int n = runtime.getKCode().getEncoding().length(c);
             RubyString str = runtime.newString();
             str.setTaint(true);
             str.cat(c);
 
             while(--n > 0) {
                 if((ch = getc()).isNil()) {
                     block.yield(context, str);
                     return this;
                 }
                 c = (byte)RubyNumeric.fix2int(ch);
                 str.cat(c);
             }
             block.yield(context, str);
         }
         return this;
     }
 
     @JRubyMethod(name = "each_char", frame = true)
     public IRubyObject each_char19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_char(context, block) : enumeratorize(context.getRuntime(), this, "each_char");
     }
 
     @JRubyMethod(name = "chars", frame = true)
     public IRubyObject chars19(final ThreadContext context, final Block block) {
         return block.isGiven() ? each_char(context, block) : enumeratorize(context.getRuntime(), this, "chars");
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     public RubyIO each_line(ThreadContext context, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         ByteList separator = getSeparatorForGets(runtime, args);
         
         ByteListCache cache = new ByteListCache();
         for (IRubyObject line = getline(runtime, separator); !line.isNil(); 
 		line = getline(runtime, separator, cache)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
     @JRubyMethod(name = "each", optional = 1, frame = true)
     public IRubyObject each19(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_line(context, args, block) : enumeratorize(context.getRuntime(), this, "each", args);
     }
 
     @JRubyMethod(name = "each_line", optional = 1, frame = true)
     public IRubyObject each_line19(final ThreadContext context, IRubyObject[]args, final Block block) {
         return block.isGiven() ? each_line(context, args, block) : enumeratorize(context.getRuntime(), this, "each_line", args);
     }
 
     @JRubyMethod(name = "readlines", optional = 1)
     public RubyArray readlines(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject[] separatorArgs = args.length > 0 ? new IRubyObject[] { args[0] } : IRubyObject.NULL_ARRAY;
         ByteList separator = getSeparatorForGets(runtime, separatorArgs);
         RubyArray result = runtime.newArray();
         IRubyObject line;
         
         while (! (line = getline(runtime, separator)).isNil()) {
             result.append(line);
         }
         return result;
     }
     
     @JRubyMethod(name = "to_io")
     public RubyIO to_io() {
     	return this;
     }
 
     @Override
     public String toString() {
         return "RubyIO(" + openFile.getMode() + ", " + getRuntime().getFileno(openFile.getMainStream().getDescriptor()) + ")";
     }
     
     /* class methods for IO */
     
     /** rb_io_s_foreach
     *
     */
     public static IRubyObject foreach(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int count = args.length;
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
        
         ByteList separator = getSeparatorFromArgs(runtime, args, 1);
 
         RubyIO io = (RubyIO)RubyFile.open(context, runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
         
         ByteListCache cache = new ByteListCache();
         if (!io.isNil()) {
             try {
                 IRubyObject str = io.getline(runtime, separator, cache);
                 while (!str.isNil()) {
                     block.yield(context, str);
                     str = io.getline(runtime, separator, cache);
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
     
     @JRubyMethod(name = "foreach", required = 1, optional = 1, frame = true, meta = true)
     public static IRubyObject foreach19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         return block.isGiven() ? foreach(context, recv, args, block) : enumeratorize(context.getRuntime(), recv, "foreach", args);
     }
 
     private static RubyIO convertToIO(ThreadContext context, IRubyObject obj) {
         return (RubyIO)TypeConverter.convertToType(obj, context.getRuntime().getIO(), "to_io");
     }
    
     private static boolean registerSelect(ThreadContext context, Selector selector, IRubyObject obj, RubyIO ioObj, int ops) throws IOException {
        Channel channel = ioObj.getChannel();
        if (channel == null || !(channel instanceof SelectableChannel)) {
            return false;
        }
        
        ((SelectableChannel) channel).configureBlocking(false);
        int real_ops = ((SelectableChannel) channel).validOps() & ops;
        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
        if (key == null) {
            ((SelectableChannel) channel).register(selector, real_ops, obj);
        } else {
            key.interestOps(key.interestOps()|real_ops);
        }
        
        return true;
     }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return select_static(context, context.getRuntime(), args);
     }
 
     private static void checkArrayType(Ruby runtime, IRubyObject obj) {
         if (!(obj instanceof RubyArray)) {
             throw runtime.newTypeError("wrong argument type "
                     + obj.getMetaClass().getName() + " (expected Array)");
         }
     }
 
     public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         Selector selector = null;
        try {
             Set pending = new HashSet();
             Set unselectable_reads = new HashSet();
             Set unselectable_writes = new HashSet();
             Map<RubyIO, Boolean> blocking = new HashMap();
             
-            selector = Selector.open();
+            selector = SelectorFactory.openWithRetryFrom(context.getRuntime(), SelectorProvider.provider());
             if (!args[0].isNil()) {
                 // read
                 checkArrayType(runtime, args[0]);
                 for (Iterator i = ((RubyArray)args[0]).getList().iterator(); i.hasNext();) {
                     IRubyObject obj = (IRubyObject)i.next();
                     RubyIO ioObj = convertToIO(context, obj);
 
                     // save blocking state
                     if (ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
                     
                     if (registerSelect(context, selector, obj, ioObj, SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) {
                         if (ioObj.writeDataBuffered()) {
                             pending.add(obj);
                         }
                     } else {
                         if ((ioObj.openFile.getMode() & OpenFile.READABLE) != 0) {
                             unselectable_reads.add(obj);
                         }
                     }
                 }
             }
 
             if (args.length > 1 && !args[1].isNil()) {
                 // write
                 checkArrayType(runtime, args[1]);
                 for (Iterator i = ((RubyArray)args[1]).getList().iterator(); i.hasNext();) {
                     IRubyObject obj = (IRubyObject)i.next();
                     RubyIO ioObj = convertToIO(context, obj);
 
                     // save blocking state
                     if (!blocking.containsKey(ioObj) && ioObj.getChannel() instanceof SelectableChannel) blocking.put(ioObj, ((SelectableChannel)ioObj.getChannel()).isBlocking());
 
                     if (!registerSelect(context, selector, obj, ioObj, SelectionKey.OP_WRITE)) {
                         if ((ioObj.openFile.getMode() & OpenFile.WRITABLE) != 0) {
                             unselectable_writes.add(obj);
                         }
                     }
                 }
             }
 
             if (args.length > 2 && !args[2].isNil()) {
                 checkArrayType(runtime, args[2]);
             // Java's select doesn't do anything about this, so we leave it be.
             }
 
             final boolean has_timeout = (args.length > 3 && !args[3].isNil());
             long timeout = 0;
             if (has_timeout) {
                 IRubyObject timeArg = args[3];
                 if (timeArg instanceof RubyFloat) {
                     timeout = Math.round(((RubyFloat)timeArg).getDoubleValue() * 1000);
                 } else if (timeArg instanceof RubyFixnum) {
                     timeout = Math.round(((RubyFixnum)timeArg).getDoubleValue() * 1000);
                 } else { // TODO: MRI also can hadle Bignum here
                     throw runtime.newTypeError("can't convert " + timeArg.getMetaClass().getName() + " into time interval");
                 }
 
                 if (timeout < 0) {
                     throw runtime.newArgumentError("negative timeout given");
                 }
             }
 
             if (pending.isEmpty() && unselectable_reads.isEmpty() && unselectable_writes.isEmpty()) {
                 if (has_timeout) {
                     if (timeout == 0) {
                         selector.selectNow();
                     } else {
                         selector.select(timeout);
                     }
                 } else {
                     selector.select();
                 }
             } else {
                 selector.selectNow();
             }
 
             List r = new ArrayList();
             List w = new ArrayList();
             List e = new ArrayList();
             for (Iterator i = selector.selectedKeys().iterator(); i.hasNext();) {
                 SelectionKey key = (SelectionKey)i.next();
                 try {
                     int interestAndReady = key.interestOps() & key.readyOps();
                     if ((interestAndReady & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT)) != 0) {
                         r.add(key.attachment());
                         pending.remove(key.attachment());
                     }
                     if ((interestAndReady & (SelectionKey.OP_WRITE)) != 0) {
                         w.add(key.attachment());
                     }
                 } catch (CancelledKeyException cke) {
                     // TODO: is this the right thing to do?
                     pending.remove(key.attachment());
                     e.add(key.attachment());
                 }
             }
             r.addAll(pending);
             r.addAll(unselectable_reads);
             w.addAll(unselectable_writes);
 
             // make all sockets blocking as configured again
             selector.close(); // close unregisters all channels, so we can safely reset blocking modes
             for (Map.Entry blockingEntry : blocking.entrySet()) {
                 SelectableChannel channel = (SelectableChannel)((RubyIO)blockingEntry.getKey()).getChannel();
                 synchronized (channel.blockingLock()) {
                     channel.configureBlocking((Boolean)blockingEntry.getValue());
                 }
             }
 
             if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
                 return runtime.getNil();
             }
 
             List ret = new ArrayList();
 
             ret.add(RubyArray.newArray(runtime, r));
             ret.add(RubyArray.newArray(runtime, w));
             ret.add(RubyArray.newArray(runtime, e));
 
             return RubyArray.newArray(runtime, ret);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         } finally {
             if (selector != null) {
                 try {
                     selector.close();
                 } catch (Exception e) {
                 }
             }
         }
    }
    
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0: throw context.getRuntime().newArgumentError(0, 1);
         case 1: return read(context, recv, args[0], Block.NULL_BLOCK);
         case 2: return read(context, recv, args[0], args[1]);
         case 3: return read(context, recv, args[0], args[1], args[2]);
         default: throw context.getRuntime().newArgumentError(args.length, 3);
         }
    }
 
     private static RubyIO newFile(ThreadContext context, IRubyObject recv, IRubyObject... args) {
        return (RubyIO) RubyKernel.open(context, recv, args, Block.NULL_BLOCK);
     }
 
     public static void failIfDirectory(Ruby runtime, RubyString pathStr) {
         if (RubyFileTest.directory_p(runtime, pathStr).isTrue()) {
             if (Platform.IS_WINDOWS) {
                 throw runtime.newErrnoEACCESError(pathStr.asJavaString());
             } else {
                 throw runtime.newErrnoEISDirError(pathStr.asJavaString());
             }
         }
     }
    
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, Block unusedBlock) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
        try {
            return file.read(context);
        } finally {
            file.close();
        }
     }
    
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
        
         try {
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     @JRubyMethod(name = "read", meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset) {
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     /**
      *  options is a hash which can contain:
      *    encoding: string or encoding
      *    mode: string
      *    open_args: array of string
      */
     private static IRubyObject read19(ThreadContext context, IRubyObject recv, IRubyObject path, IRubyObject length, IRubyObject offset, RubyHash options) {
         // FIXME: process options
 
         RubyString pathStr = path.convertToString();
         Ruby runtime = context.getRuntime();
         failIfDirectory(runtime, pathStr);
         RubyIO file = newFile(context, recv, pathStr);
 
         try {
             if (!offset.isNil()) file.seek(context, offset);
             return !length.isNil() ? file.read(context, length) : file.read(context);
         } finally  {
             file.close();
         }
     }
 
     // Enebo: annotation processing forced me to do pangea method here...
     @JRubyMethod(name = "read", meta = true, required = 1, optional = 3, compat = CompatVersion.RUBY1_9)
     public static IRubyObject read19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         IRubyObject nil = context.getRuntime().getNil();
         IRubyObject path = args[0];
         IRubyObject length = nil;
         IRubyObject offset = nil;
         RubyHash options = null;
         if (args.length > 3) {
             if (!(args[3] instanceof RubyHash)) throw context.getRuntime().newTypeError("Must be a hash");
             options = (RubyHash) args[3];
             offset = args[2];
             length = args[1];
         } else if (args.length > 2) {
             if (args[2] instanceof RubyHash) {
                 options = (RubyHash) args[2];
             } else {
                 offset = args[2];
             }
             length = args[1];
         } else if (args.length > 1) {
             if (args[1] instanceof RubyHash) {
                 options = (RubyHash) args[1];
             } else {
                 length = args[1];
             }
         }
 
         return read19(context, recv, path, length, offset, (RubyHash) options);
     }
 
     @JRubyMethod(name = "readlines", required = 1, optional = 1, meta = true)
     public static RubyArray readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         int count = args.length;
 
         IRubyObject[] fileArguments = new IRubyObject[]{ args[0].convertToString() };
         IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
         RubyIO file = (RubyIO) RubyKernel.open(context, recv, fileArguments, Block.NULL_BLOCK);
         try {
             return file.readlines(context, separatorArguments);
         } finally {
             file.close();
         }
     }
    
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true)
     public static IRubyObject popen(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
         int mode;
 
         IRubyObject cmdObj = null;
         if (Platform.IS_WINDOWS) {
             String[] tokens = args[0].convertToString().toString().split(" ", 2);
             if (tokens.length > 1) {
                 cmdObj = new RubyString(runtime, (RubyClass) recv,
                         (tokens[0].replace('/', '\\') + ' ' + tokens[1]));
             }
             else {
                 cmdObj = new RubyString(runtime, (RubyClass) recv,
                         (tokens[0].replace('/','\\')));
             }
         }
         else {
             cmdObj = args[0].convertToString();
         }
         runtime.checkSafeString(cmdObj);
 
         if ("-".equals(cmdObj.toString())) {
             throw runtime.newNotImplementedError("popen(\"-\") is unimplemented");
         }
 
         try {
             if (args.length == 1) {
                 mode = ModeFlags.RDONLY;
             } else if (args[1] instanceof RubyFixnum) {
                 mode = RubyFixnum.num2int(args[1]);
             } else {
                 mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
             }
 
             ModeFlags modes = new ModeFlags(mode);
         
             ShellLauncher.POpenProcess process = ShellLauncher.popen(runtime, cmdObj, modes);
 
             // Yes, this is gross. java.lang.Process does not appear to be guaranteed
             // "ready" when we get it back from Runtime#exec, so we try to give it a
             // chance by waiting for 10ms before we proceed. Only doing this on 1.5
             // since Hotspot 1.6+ does not seem to exhibit the problem.
             if (System.getProperty("java.specification.version", "").equals("1.5")) {
                 synchronized (process) {
                     try {
                         process.wait(100);
                     } catch (InterruptedException ie) {}
                 }
             }
             
             RubyIO io = new RubyIO(runtime, process, modes);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, io);
                 } finally {
                     if (io.openFile.isOpen()) {
                         io.close();
                     }
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor())));
                 }
             }
             return io;
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
    
     @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
     public static IRubyObject popen3(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         try {
             POpenTuple tuple = popenSpecial(context, args);
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime,
                     tuple.output,
                     tuple.input,
                     tuple.error);
             
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     cleanupPOpen(tuple);
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (tuple.process.waitFor() * 256)));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
     public static IRubyObject popen4(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         try {
             POpenTuple tuple = popenSpecial(context, args);
 
             RubyArray yieldArgs = RubyArray.newArrayLight(runtime,
                     runtime.newFixnum(ShellLauncher.getPidFromProcess(tuple.process)),
                     tuple.output,
                     tuple.input,
                     tuple.error);
 
             if (block.isGiven()) {
                 try {
                     return block.yield(context, yieldArgs);
                 } finally {
                     cleanupPOpen(tuple);
                     context.setLastExitStatus(RubyProcess.RubyStatus.newProcessStatus(runtime, (tuple.process.waitFor() * 256)));
                 }
             }
             return yieldArgs;
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
 
     private static void cleanupPOpen(POpenTuple tuple) {
         if (tuple.input.openFile.isOpen()) {
             tuple.input.close();
         }
         if (tuple.output.openFile.isOpen()) {
             tuple.output.close();
         }
         if (tuple.error.openFile.isOpen()) {
             tuple.error.close();
         }
     }
 
     private static class POpenTuple {
         public POpenTuple(RubyIO i, RubyIO o, RubyIO e, Process p) {
             input = i; output = o; error = e; process = p;
         }
         public final RubyIO input;
         public final RubyIO output;
         public final RubyIO error;
         public final Process process;
     }
 
     public static POpenTuple popenSpecial(ThreadContext context, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
 
         try {
             ShellLauncher.POpenProcess process = ShellLauncher.popen3(runtime, args);
             RubyIO input = process.getInput() != null ?
                 new RubyIO(runtime, process.getInput()) :
                 new RubyIO(runtime, process.getInputStream());
             RubyIO output = process.getOutput() != null ?
                 new RubyIO(runtime, process.getOutput()) :
                 new RubyIO(runtime, process.getOutputStream());
             RubyIO error = process.getError() != null ?
                 new RubyIO(runtime, process.getError()) :
                 new RubyIO(runtime, process.getErrorStream());
 
             input.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
             output.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
             error.getOpenFile().getMainStream().getDescriptor().
               setCanBeSeekable(false);
 
             return new POpenTuple(input, output, error, process);
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         }
     }
 
     // NIO based pipe
     @JRubyMethod(name = "pipe", meta = true)
     public static IRubyObject pipe(ThreadContext context, IRubyObject recv) {
         // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
        Ruby runtime = context.getRuntime();
        try {
            Pipe pipe = Pipe.open();
 
            RubyIO source = new RubyIO(runtime, pipe.source());
            RubyIO sink = new RubyIO(runtime, pipe.sink());
 
            sink.openFile.getMainStream().setSync(true);
            return runtime.newArrayNoCopy(new IRubyObject[] { source, sink });
        } catch (IOException ioe) {
            throw runtime.newIOErrorFromException(ioe);
        }
    }
     
     @JRubyMethod(name = "copy_stream", meta = true, compat = RUBY1_9)
     public static IRubyObject copy_stream(ThreadContext context, IRubyObject recv, 
             IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
         RubyIO io1 = null;
         boolean close_io1 = false;
         RubyIO io2 = null;
         boolean close_io2 = false;
 
         try {
             if (arg1 instanceof RubyString) {
                 io1 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg1}, Block.NULL_BLOCK);
                 close_io1 = true;
             } else if (arg1 instanceof RubyIO) {
                 io1 = (RubyIO) arg1;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             if (arg2 instanceof RubyString) {
                 io2 = (RubyIO) RubyFile.open(context, runtime.getFile(), new IRubyObject[] {arg2, runtime.newString("w")}, Block.NULL_BLOCK);
             } else if (arg2 instanceof RubyIO) {
                 io2 = (RubyIO) arg2;
                 close_io2 = true;
             } else {
                 throw runtime.newTypeError("Should be String or IO");
             }
 
             ChannelDescriptor d1 = io1.openFile.getMainStream().getDescriptor();
             if (!d1.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
             ChannelDescriptor d2 = io2.openFile.getMainStream().getDescriptor();
             if (!d2.isSeekable()) {
                 throw context.getRuntime().newTypeError("only supports file-to-file copy");
             }
 
             FileChannel f1 = (FileChannel)d1.getChannel();
             FileChannel f2 = (FileChannel)d2.getChannel();
 
             try {
                 long size = f1.size();
 
                 f1.transferTo(f2.position(), size, f2);
 
                 return context.getRuntime().newFixnum(size);
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         } finally {
             try {
                 if (io1 != null) {
                     io1.close();
                 }
             } finally {
                 if (io2 != null) {
                     io2.close();
                 }
             }
         }
     }
 
     @JRubyMethod(name = "try_convert", meta = true, backtrace = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject tryConvert(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return arg.respondsTo("to_io") ? convertToIO(context, arg) : context.getRuntime().getNil();
     }
 
     private static ByteList getNilByteList(Ruby runtime) {
         return runtime.is1_9() ? ByteList.EMPTY_BYTELIST : NIL_BYTELIST;
     }
     
     /**
      * Add a thread to the list of blocking threads for this IO.
      * 
      * @param thread A thread blocking on this IO
      */
     public synchronized void addBlockingThread(RubyThread thread) {
         if (blockingThreads == null) {
             blockingThreads = new ArrayList<RubyThread>(1);
         }
         blockingThreads.add(thread);
     }
     
     /**
      * Remove a thread from the list of blocking threads for this IO.
      * 
      * @param thread A thread blocking on this IO
      */
     public synchronized void removeBlockingThread(RubyThread thread) {
         if (blockingThreads == null) {
             return;
         }
         for (int i = 0; i < blockingThreads.size(); i++) {
             if (blockingThreads.get(i) == thread) {
                 // not using remove(Object) here to avoid the equals() call
                 blockingThreads.remove(i);
             }
         }
     }
     
     /**
      * Fire an IOError in all threads blocking on this IO object
      */
     protected synchronized void interruptBlockingThreads() {
         if (blockingThreads == null) {
             return;
         }
         for (int i = 0; i < blockingThreads.size(); i++) {
             RubyThread thread = blockingThreads.get(i);
             
             // raise will also wake the thread from selection
             thread.raise(new IRubyObject[] {getRuntime().newIOError("stream closed").getException()}, Block.NULL_BLOCK);
         }
     }
 
     /**
      * Caching reference to allocated byte-lists, allowing for internal byte[] to be
      * reused, rather than reallocated.
      *
      * Predominately used on {@link RubyIO#getline(Ruby, ByteList)} and variants.
      *
      * @author realjenius
      */
     private static class ByteListCache {
         private byte[] buffer = new byte[0];
         public void release(ByteList l) {
             buffer = l.getUnsafeBytes();
         }
 
         public ByteList allocate(int size) {
             ByteList l = new ByteList(buffer, 0, size, false);
             return l;
         }
     }
 
     /**
      *
      *  ==== Options
      *  <code>opt</code> can have the following keys
      *  :mode ::
      *    same as <code>mode</code> parameter
      *  :external_encoding ::
      *    external encoding for the IO. "-" is a
      *    synonym for the default external encoding.
      *  :internal_encoding ::
      *    internal encoding for the IO.
      *    "-" is a synonym for the default internal encoding.
      *    If the value is nil no conversion occurs.
      *  :encoding ::
      *    specifies external and internal encodings as "extern:intern".
      *  :textmode ::
      *    If the value is truth value, same as "b" in argument <code>mode</code>.
      *  :binmode ::
      *    If the value is truth value, same as "t" in argument <code>mode</code>.
      *
      *  Also <code>opt</code> can have same keys in <code>String#encode</code> for
      *  controlling conversion between the external encoding and the internal encoding.
      *
      */
     protected ModeFlags parseOptions(ThreadContext context, IRubyObject options, ModeFlags modes) {
         Ruby runtime = context.getRuntime();
 
         RubyHash rubyOptions = (RubyHash) options;
 
         IRubyObject internalEncodingOption = rubyOptions.fastARef(runtime.newSymbol("internal_encoding"));
         IRubyObject externalEncodingOption = rubyOptions.fastARef(runtime.newSymbol("external_encoding"));
         RubyString dash = runtime.newString("-");
         if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
             if (dash.eql(externalEncodingOption)) {
                 externalEncodingOption = RubyEncoding.getDefaultExternal(runtime);
             }
             setExternalEncoding(context, externalEncodingOption);
         }
 
         if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
             if (dash.eql(internalEncodingOption)) {
                 internalEncodingOption = RubyEncoding.getDefaultInternal(runtime);
             }
             setInternalEncoding(context, internalEncodingOption);
         }
 
         IRubyObject encoding = rubyOptions.fastARef(runtime.newSymbol("encoding"));
         if (encoding != null && !encoding.isNil()) {
             if (externalEncodingOption != null && !externalEncodingOption.isNil()) {
                 context.getRuntime().getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': external_encoding is used");
             } else if (internalEncodingOption != null && !internalEncodingOption.isNil()) {
                 context.getRuntime().getWarnings().warn("Ignoring encoding parameter '"+ encoding +"': internal_encoding is used");
             } else {
                 parseEncodingFromString(context, encoding, 0);
             }
         }
 
         if (rubyOptions.containsKey(runtime.newSymbol("mode"))) {
             modes = parseModes19(context, rubyOptions.fastARef(runtime.newSymbol("mode")).asString());
         }
 
 //      FIXME: check how ruby 1.9 handles this
 
 //        if (rubyOptions.containsKey(runtime.newSymbol("textmode")) &&
 //                rubyOptions.fastARef(runtime.newSymbol("textmode")).isTrue()) {
 //            try {
 //                modes = getIOModes(runtime, "t");
 //            } catch (InvalidValueException e) {
 //                throw getRuntime().newErrnoEINVALError();
 //            }
 //        }
 //
 //        if (rubyOptions.containsKey(runtime.newSymbol("binmode")) &&
 //                rubyOptions.fastARef(runtime.newSymbol("binmode")).isTrue()) {
 //            try {
 //                modes = getIOModes(runtime, "b");
 //            } catch (InvalidValueException e) {
 //                throw getRuntime().newErrnoEINVALError();
 //            }
 //        }
 
         return modes;
     }
 
     /**
      * Try for around 1s to destroy the child process. This is to work around
      * issues on some JVMs where if you try to destroy the process too quickly
      * it may not be ready and may ignore the destroy. A subsequent waitFor
      * will then hang. This version tries to destroy and call exitValue
      * repeatedly for up to 1000 calls with 1ms delay between iterations, with
      * the intent that the target process ought to be "ready to die" fairly
      * quickly and we don't get stuck in a blocking waitFor call.
      *
      * @param runtime The Ruby runtime, for raising an error
      * @param process The process to obliterate
      */
     public static void obliterateProcess(Process process) {
         int i = 0;
         Object waitLock = new Object();
         while (true) {
             // only try 1000 times with a 1ms sleep between, so we don't hang
             // forever on processes that ignore SIGTERM
             if (i >= 1000) {
                 throw new RuntimeException("could not shut down process: " + process);
             }
 
             // attempt to destroy (SIGTERM on UNIX, TerminateProcess on Windows)
             process.destroy();
             
             try {
                 // get the exit value; succeeds if it has terminated, throws
                 // IllegalThreadStateException if not.
                 process.exitValue();
             } catch (IllegalThreadStateException itse) {
                 // increment count and try again after a 1ms sleep
                 i += 1;
                 synchronized (waitLock) {
                     try {waitLock.wait(1);} catch (InterruptedException ie) {}
                 }
                 continue;
             }
             // success!
             break;
         }
     }
 }
diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index e4ba6dc3f4..53d0d74117 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,1068 +1,1070 @@
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
+import java.nio.channels.spi.SelectorProvider;
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
 import org.jruby.runtime.Visibility;
 import org.jruby.util.io.BlockingIO;
+import org.jruby.util.io.SelectorFactory;
 
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
 
     protected RubyThread(Ruby runtime, RubyClass type) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         finalResult = runtime.getNil();
 
         // init errorInfo to nil
         errorInfo = runtime.getNil();
     }
 
     public synchronized void receiveMail(ThreadService.Event event) {
         // if we're already aborting, we can receive no further mail
         if (status == Status.ABORTING) return;
         
         mail = event;
         switch (event.type) {
         case KILL:
             status = Status.ABORTING;
         }
         
         // If this thread is sleeping or stopped, wake it
         notify();
 
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
         runtime.getThreadService().associateThread(t, rubyThread);
         
         context.preAdoptThread();
         
         // set to default thread group
         runtime.getDefaultThreadGroup().addDirectly(rubyThread);
         
         return rubyThread;
     }
     
     @JRubyMethod(name = "initialize", rest = true, frame = true, visibility = Visibility.PRIVATE)
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
 
     @JRubyMethod(name = "value", frame = true)
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
     
     @JRubyMethod(name = "kill", required = 1, frame = true, meta = true)
     public static IRubyObject kill(IRubyObject receiver, IRubyObject rubyThread, Block block) {
         if (!(rubyThread instanceof RubyThread)) throw receiver.getRuntime().newTypeError(rubyThread, receiver.getRuntime().getThread());
         return ((RubyThread)rubyThread).kill();
     }
     
     @JRubyMethod(name = "exit", frame = true, meta = true)
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
 
     @JRubyMethod(name = "raise", optional = 3, frame = true)
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
     private volatile Object currentWaitObject;
     
     @Deprecated
     public boolean selectForAccept(RubyIO io) {
         return select(io, SelectionKey.OP_ACCEPT);
     }
 
     private synchronized Selector getSelector(SelectableChannel channel) throws IOException {
-        return channel.provider().openSelector();
+        return SelectorFactory.openWithRetryFrom(getRuntime(), channel.provider());
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
         Object object = currentWaitObject;
         if (object != null) {
             synchronized (object) {
                 object.notify();
             }
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
                 try {
                     currentWaitObject = o;
                     status = Status.SLEEP;
                     o.wait(delay_ms, delay_ns_remainder);
                 } finally {
                     pollThreadEvents();
                     status = Status.RUN;
                     currentWaitObject = null;
                 }
             }
             long end_ns = System.nanoTime();
             return ( end_ns - start_ns ) <= delay_ns;
         } else {
             try {
                 currentWaitObject = o;
                 status = Status.SLEEP;
                 o.wait();
             } finally {
                 pollThreadEvents();
                 status = Status.RUN;
                 currentWaitObject = null;
             }
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
diff --git a/src/org/jruby/ext/NetProtocolBufferedIO.java b/src/org/jruby/ext/NetProtocolBufferedIO.java
index 05d64236ea..223f0ea14b 100644
--- a/src/org/jruby/ext/NetProtocolBufferedIO.java
+++ b/src/org/jruby/ext/NetProtocolBufferedIO.java
@@ -1,133 +1,136 @@
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
  * Copyright (C) 2008 Ola Bini <ola.bini@gmail.com>
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
 package org.jruby.ext;
 
 import java.io.IOException;
 
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.nio.channels.SelectableChannel;
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.RubyObject;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyIO;
 import org.jruby.Ruby;
 import org.jruby.RubyException;
 import org.jruby.RubyModule;
 import org.jruby.RubyClass;
 import org.jruby.RubyString;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.exceptions.RaiseException;
 
+import org.jruby.util.io.SelectorFactory;
+import java.nio.channels.spi.SelectorProvider;
+
 /**
  * @author <a href="mailto:ola.bini@gmail.com">Ola Bini</a>
  */
 @JRubyClass(name="Net::BufferedIO")
 public class NetProtocolBufferedIO { 
     public static void create(Ruby runtime) {
         RubyModule mNet = runtime.getModule("Net");
 
         RubyClass cBufferedIO = (RubyClass)mNet.getConstant("BufferedIO");
         cBufferedIO.defineAnnotatedMethods(NetProtocolBufferedIO.class);
 
         RubyModule mNativeImpl = cBufferedIO.defineModuleUnder("NativeImplementation");
 
         mNativeImpl.defineAnnotatedMethods(NativeImpl.class);
     }    
 
     @JRubyMethod(required = 1)
     public static IRubyObject initialize(IRubyObject recv, IRubyObject io) {
         if(io instanceof RubyIO && 
            (((RubyIO)io).getOpenFile().getMainStream() instanceof ChannelStream) &&
            (((ChannelStream)((RubyIO)io).getOpenFile().getMainStream()).getDescriptor().getChannel() instanceof SelectableChannel))  {
 
             ((RubyObject)recv).extend(new IRubyObject[]{((RubyModule)recv.getRuntime().getModule("Net").getConstant("BufferedIO")).getConstant("NativeImplementation")});
             SelectableChannel sc = (SelectableChannel)(((ChannelStream)((RubyIO)io).getOpenFile().getMainStream()).getDescriptor().getChannel());
             recv.dataWrapStruct(new NativeImpl(sc));
         }
 
         recv.getInstanceVariables().setInstanceVariable("@io", io);
         recv.getInstanceVariables().setInstanceVariable("@read_timeout", recv.getRuntime().newFixnum(60));
         recv.getInstanceVariables().setInstanceVariable("@debug_output", recv.getRuntime().getNil());
         recv.getInstanceVariables().setInstanceVariable("@rbuf", RubyString.newEmptyString(recv.getRuntime()));
 
         return recv;
     }
 
     @JRubyModule(name="Net::BufferedIO::NativeImplementation")
     public static class NativeImpl {
         private SelectableChannel channel;
         public NativeImpl(SelectableChannel channel) {
             this.channel = channel;
         }
         
         @JRubyMethod
         public static IRubyObject rbuf_fill(IRubyObject recv) {
             RubyString buf = (RubyString)recv.getInstanceVariables().getInstanceVariable("@rbuf");
             RubyIO io = (RubyIO)recv.getInstanceVariables().getInstanceVariable("@io");
 
             int timeout = RubyNumeric.fix2int(recv.getInstanceVariables().getInstanceVariable("@read_timeout")) * 1000;
             NativeImpl nim = (NativeImpl)recv.dataGetStruct();
 
             Selector selector = null;
             synchronized (nim.channel.blockingLock()) {
                 boolean oldBlocking = nim.channel.isBlocking();
 
                 try {
-                    selector = Selector.open();
+                    selector = SelectorFactory.openWithRetryFrom(recv.getRuntime(), SelectorProvider.provider());
                     nim.channel.configureBlocking(false);
                     SelectionKey key = nim.channel.register(selector, SelectionKey.OP_READ);
                     int n = selector.select(timeout);
 
                     if(n > 0) {
                         IRubyObject readItems = io.read(new IRubyObject[]{recv.getRuntime().newFixnum(1024*16)});
                         return buf.concat(readItems);
                     } else {
                         RubyClass exc = (RubyClass)(recv.getRuntime().getModule("Timeout").getConstant("Error"));
                         throw new RaiseException(RubyException.newException(recv.getRuntime(), exc, "execution expired"),false);
                     }
                 } catch(IOException exception) {
                     throw recv.getRuntime().newIOErrorFromException(exception);
                 } finally {
                     if (selector != null) {
                         try {
                             selector.close();
                         } catch (Exception e) {
                         }
                     }
                     try {nim.channel.configureBlocking(oldBlocking);} catch (IOException ioe) {}
                 }
             }
         }
     }
 }// NetProtocolBufferedIO
diff --git a/src/org/jruby/ext/socket/RubyTCPServer.java b/src/org/jruby/ext/socket/RubyTCPServer.java
index 2480aed0b2..c515107dff 100644
--- a/src/org/jruby/ext/socket/RubyTCPServer.java
+++ b/src/org/jruby/ext/socket/RubyTCPServer.java
@@ -1,281 +1,284 @@
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
 
 import java.io.FileDescriptor;
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
 import java.util.regex.Pattern;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyIO;
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
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.ModeFlags;
 
+import org.jruby.util.io.SelectorFactory;
+import java.nio.channels.spi.SelectorProvider;
+
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 @JRubyClass(name="TCPServer", parent="TCPSocket")
 public class RubyTCPServer extends RubyTCPSocket {
     static void createTCPServer(Ruby runtime) {
         RubyClass rb_cTCPServer = runtime.defineClass(
                 "TCPServer", runtime.fastGetClass("TCPSocket"), TCPSERVER_ALLOCATOR);
 
         rb_cTCPServer.defineAnnotatedMethods(RubyTCPServer.class);
 
         runtime.getObject().fastSetConstant("TCPserver",rb_cTCPServer);
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
 
     @JRubyMethod(name = "initialize", required = 1, optional = 1, visibility = Visibility.PRIVATE, backtrace = true)
     public IRubyObject initialize(ThreadContext context, IRubyObject[] args) {
         IRubyObject hostname = args[0];
         IRubyObject port = args.length > 1 ? args[1] : context.getRuntime().getNil();
 
         if(hostname.isNil()
                 || ((hostname instanceof RubyString)
                         && ((RubyString) hostname).isEmpty())) {
             hostname = context.getRuntime().newString("0.0.0.0");
         } else if (hostname instanceof RubyFixnum) {
             // numeric host, use it for port
             port = hostname;
             hostname = context.getRuntime().newString("0.0.0.0");
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
                     portInt = RubyNumeric.fix2int(RubySocket.getservbyname(
                             context, context.getRuntime().getObject(), new IRubyObject[] {portString}));
                 }
             }
 
             socket_address = new InetSocketAddress(addr, portInt);
             ssc.socket().bind(socket_address);
             initSocket(context.getRuntime(), new ChannelDescriptor(
                     ssc, new ModeFlags(ModeFlags.RDWR)));
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch(UnknownHostException e) {
             throw sockerr(context.getRuntime(), "initialize: name or service not known");
         } catch(BindException e) {
             // e.printStackTrace();
             String msg = e.getMessage();
             if (msg == null) {
                 msg = "bind";
             } else {
                 msg = "bind - " + msg;
             }
 
             // This is ugly, but what can we do, Java provides the same BindingException
             // for both EADDRNOTAVAIL and EADDRINUSE, so we differentiate the errors
             // based on BindException's message.
             if(ADDR_NOT_AVAIL_PATTERN.matcher(msg).find()) {
                 throw context.getRuntime().newErrnoEADDRNOTAVAILError(msg);
             } else {
                 throw context.getRuntime().newErrnoEADDRINUSEError(msg);
             }
         } catch(SocketException e) {
             String msg = e.getMessage();
             if(msg.indexOf("Permission denied") != -1) {
                 throw context.getRuntime().newErrnoEACCESError("bind(2)");
             } else {
                 throw sockerr(context.getRuntime(), "initialize: name or service not known");
             }
         } catch(IOException e) {
             throw sockerr(context.getRuntime(), "initialize: name or service not known");
         } catch (IllegalArgumentException iae) {
             throw sockerr(context.getRuntime(), iae.getMessage());
         }
 
         return this;
     }
     @Deprecated
     public IRubyObject accept() {
         return accept(getRuntime().getCurrentContext());
     }
     @JRubyMethod(name = "accept")
     public IRubyObject accept(ThreadContext context) {
         RubyTCPSocket socket = new RubyTCPSocket(context.getRuntime(), context.getRuntime().fastGetClass("TCPSocket"));
 
         try {
             while (true) {
                 boolean ready = context.getThread().select(this, SelectionKey.OP_ACCEPT);
                 if (!ready) {
                     // we were woken up without being selected...poll for thread events and go back to sleep
                     context.pollThreadEvents();
                 } else {
                     try {
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
                         socket.initSocket(context.getRuntime(), new ChannelDescriptor(connected, new ModeFlags(ModeFlags.RDWR)));
                     } catch (InvalidValueException ex) {
                         throw context.getRuntime().newErrnoEINVALError();
                     }
                     return socket;
                 }
             }
         } catch(IOException e) {
             throw sockerr(context.getRuntime(), "problem when accepting");
         }
     }
     @Deprecated
     public IRubyObject accept_nonblock() {
         return accept_nonblock(getRuntime().getCurrentContext());
     }
     @JRubyMethod(name = "accept_nonblock")
     public IRubyObject accept_nonblock(ThreadContext context) {
         RubyTCPSocket socket = new RubyTCPSocket(context.getRuntime(), context.getRuntime().fastGetClass("TCPSocket"));
         Selector selector = null;
         synchronized (ssc.blockingLock()) {
             boolean oldBlocking = ssc.isBlocking();
 
             try {
                 ssc.configureBlocking(false);
-                selector = Selector.open();
+                selector = SelectorFactory.openWithRetryFrom(getRuntime(), SelectorProvider.provider());
 
                 int selected = selector.selectNow();
                 if (selected == 0) {
                     // no connection immediately accepted, let them try again
                     throw context.getRuntime().newErrnoEAGAINError("Resource temporarily unavailable");
                 } else {
                     try {
                         // otherwise one key has been selected (ours) so we get the channel and hand it off
                         socket.initSocket(context.getRuntime(), new ChannelDescriptor(ssc.accept(), new ModeFlags(ModeFlags.RDWR)));
                     } catch (InvalidValueException ex) {
                         throw context.getRuntime().newErrnoEINVALError();
                     }
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
     @JRubyMethod(name = "open", rest = true, frame = true, meta = true)
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
 
     private final static Pattern ADDR_NOT_AVAIL_PATTERN = Pattern.compile("assign.*address");
 }
diff --git a/src/org/jruby/util/io/BlockingIO.java b/src/org/jruby/util/io/BlockingIO.java
index db533f6537..60a561c531 100644
--- a/src/org/jruby/util/io/BlockingIO.java
+++ b/src/org/jruby/util/io/BlockingIO.java
@@ -1,256 +1,256 @@
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
  * Copyright (C) 2008 The JRuby Community <www.jruby.org>
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
 
 import java.io.IOException;
 import java.io.InterruptedIOException;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.nio.channels.WritableByteChannel;
 import java.nio.channels.spi.SelectorProvider;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentLinkedQueue;
 import java.util.concurrent.TimeUnit;
 
 /**
  * A Utility class to emulate blocking I/O operations on non-blocking channels.
  */
 public class BlockingIO {
     public static final class Condition {
         private final IOChannel channel;
         Condition(IOChannel channel) {
             this.channel = channel;
         }
         public void cancel() {
             channel.wakeup(false);
         }
         public void interrupt() {
             channel.interrupt();
         }
         public boolean await() throws InterruptedException {
             return channel.await();
         }
         public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
             return channel.await(timeout, unit);
         }
     }
     static final class IOChannel {
         final SelectableChannel channel;
         final int ops;        
         private final Object monitor;
         private boolean woken = false;
         private boolean ready = false;
         private boolean interrupted = false;
         
         IOChannel(SelectableChannel channel, int ops, Object monitor) {
             this.channel = channel;
             this.ops = ops;
             this.monitor = monitor;
         }
         public final void wakeup(boolean ready) {
             synchronized (monitor) {
                 this.woken = true;
                 this.ready = ready;
                 monitor.notifyAll();
             }
         }
         public final void interrupt() {
             synchronized (monitor) {
                 this.woken = true;
                 this.interrupted = true;
                 monitor.notifyAll();
             }
         }
         public final boolean await() throws InterruptedException {
             return await(0, TimeUnit.MILLISECONDS);
         }
         public final boolean await(final long timeout, TimeUnit unit) throws InterruptedException {
             synchronized (monitor) {
                 if (!woken) {
                     monitor.wait(TimeUnit.MILLISECONDS.convert(timeout, unit));
                 }
                 if (interrupted) {
                     throw new InterruptedException("Interrupted");
                 }
                 return ready;
             }
         }
     }
     static final class IOSelector implements Runnable {
         private final Selector selector;
         private final ConcurrentLinkedQueue<IOChannel> registrationQueue;
 
         public IOSelector(SelectorProvider provider) throws IOException {
-            selector = provider.openSelector();
+            selector = SelectorFactory.openWithRetryFrom(null, provider);
             registrationQueue = new ConcurrentLinkedQueue<IOChannel>();
         }
         public void run() {
             for ( ; ; ) {
                 try {
                     //
                     // Wake up any channels that became unblocked
                     //
                     Set<SelectionKey> selected = new HashSet<SelectionKey>(selector.selectedKeys());
                     for (SelectionKey k : selected) {
                         List<IOChannel> waitq = (List<IOChannel>) k.attachment();
                         for (IOChannel ch : waitq) {
                             ch.wakeup(true);
                         }
                         waitq.clear();
                     }
 
                     //
                     // Register any new blocking I/O requests
                     //
                     IOChannel ch;
                     Set<SelectableChannel> added = new HashSet<SelectableChannel>();
                     while ((ch = registrationQueue.poll()) != null) {
                         SelectionKey k = ch.channel.keyFor(selector);
                         List<IOChannel> waitq = k == null
                                 ? new LinkedList<IOChannel>()
                                 : (List<IOChannel>) k.attachment();
                         ch.channel.register(selector, ch.ops, waitq);
                         waitq.add(ch);
                         added.add(ch.channel);
                     }
 
                     // Now clear out any previously selected channels
                     for (SelectionKey k : selected) {
                         if (!added.contains(k.channel())) {
                             k.cancel();
                         }
                     }
 
                     //
                     // Wait for I/O on any channel
                     //
                     selector.select();
                 } catch (IOException ex) {
 
                 }
             }
         }
         Condition add(Channel channel, int ops, Object monitor) {
             IOChannel io = new IOChannel((SelectableChannel) channel, ops, monitor);
             registrationQueue.add(io);
             selector.wakeup();
             return new Condition(io);
         }
         public void await(Channel channel, int op) throws InterruptedException {
             add(channel, op, new Object()).await();
         }
     }
     static final private Map<SelectorProvider, IOSelector> selectors
             = new ConcurrentHashMap<SelectorProvider, IOSelector>();
 
     private static IOSelector getSelector(SelectorProvider provider) throws IOException {
         IOSelector sel = selectors.get(provider);
         if (sel != null) {
             return sel;
         }
 
         //
         // Synchronize and re-check to avoid creating more than one Selector per provider
         //
         synchronized (provider) {
             sel = selectors.get(provider);
             if (sel == null) {
                 sel = new IOSelector(provider);
                 selectors.put(provider, sel);
                 Thread t = new Thread(sel);
                 t.setDaemon(true);
                 t.start();
             }
         }
         return sel;
     }
     private static IOSelector getSelector(Channel channel) throws IOException {
         if (!(channel instanceof SelectableChannel)) {
             throw new IllegalArgumentException("channel must be a SelectableChannel");
         }        
         return getSelector(((SelectableChannel) channel).provider());
     }
     public static final Condition newCondition(Channel channel, int ops, Object monitor) throws IOException {
         return getSelector(channel).add(channel, ops, monitor);
     }
     public static final Condition newCondition(Channel channel, int ops) throws IOException {
         return newCondition(channel, ops, new Object());
     }
     public static void waitForIO(Channel channel, int op) throws InterruptedException, IOException {
         getSelector(channel).await(channel, op);
     }
     public static void awaitReadable(ReadableByteChannel channel) throws InterruptedException, IOException {
         waitForIO(channel, SelectionKey.OP_READ);
     }
     public static void awaitWritable(WritableByteChannel channel) throws InterruptedException, IOException {
         waitForIO(channel, SelectionKey.OP_WRITE);
     }
     public static int read(ReadableByteChannel channel, ByteBuffer buf, boolean blocking) throws IOException {
         do {
             int n = channel.read(buf);
             if (n != 0 || !blocking || !(channel instanceof SelectableChannel) || !buf.hasRemaining()) {
                 return n;
             }
             try {
                 awaitReadable(channel);
             } catch (InterruptedException ex) {
                 throw new InterruptedIOException(ex.getMessage());
             }
         } while (true);
     }
     public static int write(WritableByteChannel channel, ByteBuffer buf, boolean blocking) throws IOException {
         do {
             int n = channel.write(buf);
             if (n != 0 || !blocking || !(channel instanceof SelectableChannel) || !buf.hasRemaining()) {
                 return n;
             }
             try {
                 awaitWritable(channel);
             } catch (InterruptedException ex) {
                 throw new InterruptedIOException(ex.getMessage());
             }
         } while (true);
     }
     public static int blockingRead(ReadableByteChannel channel, ByteBuffer buf) throws IOException {
         return read(channel, buf, true);
     }
     public static int blockingWrite(WritableByteChannel channel, ByteBuffer buf) throws IOException {
         return write(channel, buf, true);
     }
 }
diff --git a/src/org/jruby/util/io/ChannelStream.java b/src/org/jruby/util/io/ChannelStream.java
index d06a1a53be..316680fd76 100644
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
@@ -1,1606 +1,1608 @@
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
 
+import java.nio.channels.spi.SelectorProvider;
+
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
     final static int BUFSIZE = 4 * 1024;
 
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
 
     private ChannelStream(Ruby runtime, ChannelDescriptor descriptor) {
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = descriptor.getOriginalModes();
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
         runtime.addInternalFinalizer(this);
     }
 
     private ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         this(runtime, descriptor);
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
         return reading && buffer.hasRemaining();
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
         flushWrite();
 
         descriptor.close();
         buffer = EMPTY_BUFFER;
 
         if (DEBUG) getLogger("ChannelStream").info("Descriptor for fileno "
                 + descriptor.getFileno() + " closed by stream");
     }
 
     /**
      * Internal close, to safely work for finalizing.
      * Silences possible exceptions.
      */
     private void closeForFinalize() {
         try {
             close();
         } catch (BadDescriptorException ex) {
             // silence
         } catch (IOException ex) {
             // silence
         } finally {
             // clear runtime so it doesn't get stuck in memory (JRUBY-2933)
             runtime = null;
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
                     tmpDst.limit(bytesToRead);
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
     public void finalize() {
         if (closedExplicitly) return;
 
         if (DEBUG) {
             getLogger("ChannelStream").info("finalize() for not explicitly closed stream");
         }
 
         // FIXME: I got a bunch of NPEs when I didn't check for nulls here...HOW?!
         if (descriptor != null && descriptor.isOpen()) {
             closeForFinalize(); // close without removing from finalizers
         }
     }
 
     public int ready() throws IOException {
         if (descriptor.getChannel() instanceof SelectableChannel) {
             int ready_stat = 0;
-            java.nio.channels.Selector sel = java.nio.channels.Selector.open();
+            java.nio.channels.Selector sel = SelectorFactory.openWithRetryFrom(null, SelectorProvider.provider());;
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
 
     public static final Stream open(Ruby runtime, ChannelDescriptor descriptor) {
         return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor), descriptor.getOriginalModes());
     }
 
     public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, modes), modes);
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
diff --git a/src/org/jruby/util/io/SelectorFactory.java b/src/org/jruby/util/io/SelectorFactory.java
new file mode 100644
index 0000000000..c027b9be52
--- /dev/null
+++ b/src/org/jruby/util/io/SelectorFactory.java
@@ -0,0 +1,65 @@
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
+ * Copyright (C) 2010 Ola Bini <ola.bini@gmail.com>
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
+package org.jruby.util.io;
+
+import java.io.IOException;
+import java.nio.channels.Selector;
+import java.nio.channels.spi.SelectorProvider;
+
+import java.net.BindException;
+
+import org.jruby.Ruby;
+
+/**
+ * @author <a href="mailto:ola.bini@gmail.com">Ola Bini</a>
+ */
+public class SelectorFactory {
+    // If it doesn't work after 20 times it's unlikely to ever work. Bailout is only option.
+    private final static int RETRY_MAX = 20; 
+    
+    public static Selector openWithRetryFrom(Ruby runtime, SelectorProvider provider) throws IOException {
+        int retryCount = 0;
+        while(true) {
+            try {
+                return provider.openSelector();
+            } catch(IOException e) {
+                if(e.getMessage() != null && 
+                   e.getMessage().contains("Unable to establish loopback connection") && 
+                   e.getCause() instanceof BindException &&
+                   retryCount < RETRY_MAX) {
+                    retryCount++;
+                    if(runtime != null) {
+                        runtime.getWarnings().warn("try number " + retryCount + " to get a selector");
+                    }
+                } else {
+                    throw e;
+                }
+            }
+        }
+    }
+}// SelectorFactory
diff --git a/src/org/jruby/util/io/SelectorPool.java b/src/org/jruby/util/io/SelectorPool.java
index 3ad8e11f1d..daa2d0e67f 100644
--- a/src/org/jruby/util/io/SelectorPool.java
+++ b/src/org/jruby/util/io/SelectorPool.java
@@ -1,106 +1,108 @@
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
  * Copyright (C) 2010 Charles O Nutter <headius@headius.com>
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
 
 import java.io.IOException;
 import java.lang.ref.Reference;
 import java.lang.ref.ReferenceQueue;
 import java.lang.ref.SoftReference;
 import java.nio.channels.Selector;
 import java.util.ArrayList;
 import java.util.List;
 
+import java.nio.channels.spi.SelectorProvider;
+
 /**
  * This is a simple implementation of a soft-referenced java.nio.channels.Selector
  * pool. It is intended to allow us to reuse a small pool of selectors rather
  * than creating them new for each use (which causes problem for Windows and
  * its socket-per-selector impl) or saving them per-thread (which causes
  * problems when there are many not-quite-dead threads in flight.
  *
  * The selectors are kept in soft references, so that if there's memory
  * pressure and they are not in use, they'll get dereferenced and eventually
  * close and finalize. Weak references would be too transient, and there's no
  * reason to keep them open in hard references forever.
  *
  * @author headius
  */
 public class SelectorPool {
     private final List<SoftReference<Selector>> pool = new ArrayList();
     private final ReferenceQueue queue = new ReferenceQueue();
 
     /**
      * Get a selector from the pool (or create a new one). Selectors come from
      * the default selector provider on the current JVM.
      *
      * @return a java.nio.channels.Selector
      * @throws IOException if there's a problem opening a new selector
      */
     public synchronized Selector get() throws IOException{
         return retrieveFromPool();
     }
 
     /**
      * Put a selector back into the pool.
      *
      * @param selector the selector to put back
      */
     public synchronized void put(Selector selector) {
         returnToPool(selector);
     }
 
     private Selector retrieveFromPool() throws IOException {
         // scrub pool
         clean();
 
         Selector selector = null;
 
         // try to get from pool
         while (!pool.isEmpty() && selector == null) {
             Reference<Selector> ref = pool.remove(pool.size() - 1);
             selector = ref.get();
         }
 
         if (selector != null) return selector;
 
-        return Selector.open();
+        return SelectorFactory.openWithRetryFrom(null, SelectorProvider.provider());
     }
 
     private void returnToPool(Selector selector) {
         clean();
         pool.add(new SoftReference<Selector>(selector, queue));
     }
 
     private void clean() {
         Reference ref;
         while ((ref = queue.poll()) != null) {
             pool.remove((SoftReference)ref);
         }
     }
 }
