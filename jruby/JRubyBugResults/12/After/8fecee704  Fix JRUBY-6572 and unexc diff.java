diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 56eb8ef76b..6085019c2b 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,1662 +1,1663 @@
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
 
 import org.jcodings.specific.UTF16BEEncoding;
 import org.jcodings.specific.UTF16LEEncoding;
 import org.jcodings.specific.UTF32BEEncoding;
 import org.jcodings.specific.UTF32LEEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.jruby.util.StringSupport;
 import org.jruby.util.io.EncodingOption;
 import org.jruby.util.io.ModeFlags;
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
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.WritableByteChannel;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
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
 import org.jruby.util.io.IOOptions;
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
     
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         this(runtime, outputStream, true);
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream, boolean autoclose) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newRuntimeError("Opening null stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(ChannelStream.open(runtime, new ChannelDescriptor(Channels.newChannel(outputStream)), autoclose));
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
 
     public RubyIO(Ruby runtime, ShellLauncher.POpenProcess process, IOOptions ioOptions) {
         this(runtime, runtime.getIO(), process, null, ioOptions);
     }
 
     public RubyIO(Ruby runtime, RubyClass cls, ShellLauncher.POpenProcess process, RubyHash options, IOOptions ioOptions) {
         super(runtime, cls);
 
         ioOptions = updateIOOptionsFromOptions(runtime.getCurrentContext(), (RubyHash) options, ioOptions);
         setEncodingFromOptions(ioOptions.getEncodingOption());
 
         openFile = new OpenFile();
         
         openFile.setMode(ioOptions.getModeFlags().getOpenFileFlags() | OpenFile.SYNC);
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
 
         switch (stdio) {
         case IN:
             // special constructor that accepts stream, not channel
             descriptor = new ChannelDescriptor(runtime.getIn(), newModeFlags(runtime, ModeFlags.RDONLY), FileDescriptor.in);
             runtime.putFilenoMap(0, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             break;
         case OUT:
             descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getOut()), newModeFlags(runtime, ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out);
             runtime.putFilenoMap(1, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             openFile.getMainStream().setSync(true);
             break;
         case ERR:
             descriptor = new ChannelDescriptor(Channels.newChannel(runtime.getErr()), newModeFlags(runtime, ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.err);
             runtime.putFilenoMap(2, descriptor.getFileno());
             mainStream = ChannelStream.open(runtime, descriptor);
             openFile.setMainStream(mainStream);
             openFile.getMainStream().setSync(true);
             break;
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
             IOOptions modes;
             if (args.length > 1) {
                 IRubyObject modeString = args[1].convertToString();
                 modes = newIOOptions(runtime, modeString.toString());
 
                 openFile.setMode(modes.getModeFlags().getOpenFileFlags());
             } else {
                 modes = newIOOptions(runtime, "r");
             }
 
             String path = pathString.toString();
 
             // Ruby code frequently uses a platform check to choose "NUL:" on windows
             // but since that check doesn't work well on JRuby, we help it out
 
             openFile.setPath(path);
 
             if (openFile.getMainStream() == null) {
                 try {
                     openFile.setMainStream(ChannelStream.fopen(runtime, path, modes.getModeFlags()));
                 } catch (FileExistsException fee) {
                     throw runtime.newErrnoEEXISTError(path);
                 }
 
                 if (openFile.getPipeStream() != null) {
                     openFile.getPipeStream().fclose();
                     openFile.setPipeStream(null);
                 }
             } else {
                 // TODO: This is an freopen in MRI, this is close, but not quite the same
                 openFile.getMainStreamSafe().freopen(runtime, path, newIOOptions(runtime, openFile.getModeAsString(runtime)).getModeFlags());
                 
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
 
             OpenFile origFile = ios.getOpenFileChecked();
             OpenFile selfFile = getOpenFileChecked();
 
             long pos = 0;
             Stream origStream = origFile.getMainStreamSafe();
             ChannelDescriptor origDescriptor = origStream.getDescriptor();
             boolean origIsSeekable = origDescriptor.isSeekable();
 
             if (origFile.isReadable() && origIsSeekable) {
                 pos = origFile.getMainStreamSafe().fgetpos();
             }
 
             if (origFile.getPipeStream() != null) {
                 origFile.getPipeStream().fflush();
             } else if (origFile.isWritable()) {
                 origStream.fflush();
             }
 
             if (selfFile.isWritable()) {
                 selfFile.getWriteStreamSafe().fflush();
             }
 
             selfFile.setMode(origFile.getMode());
             selfFile.setProcess(origFile.getProcess());
             selfFile.setLineNumber(origFile.getLineNumber());
             selfFile.setPath(origFile.getPath());
             selfFile.setFinalizer(origFile.getFinalizer());
 
             Stream selfStream = selfFile.getMainStreamSafe();
             ChannelDescriptor selfDescriptor = selfFile.getMainStreamSafe().getDescriptor();
             boolean selfIsSeekable = selfDescriptor.isSeekable();
 
             // confirm we're not reopening self's channel
             if (selfDescriptor.getChannel() != origDescriptor.getChannel()) {
                 // check if we're a stdio IO, and ensure we're not badly mutilated
                 if (runtime.getFileno(selfDescriptor) >= 0 && runtime.getFileno(selfDescriptor) <= 2) {
                     selfFile.getMainStreamSafe().clearerr();
 
                     // dup2 new fd into self to preserve fileno and references to it
                     origDescriptor.dup2Into(selfDescriptor);
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
                         selfFile.setMainStream(ChannelStream.fdopen(runtime, origDescriptor, origDescriptor.getOriginalModes()));
                         selfFile.setPipeStream(pipeFile);
                     } else {
                         // only use internal fileno here, stdio is handled above
                         selfFile.setMainStream(
                                 ChannelStream.open(
                                 runtime,
                                 origDescriptor.dup2(selfDescriptor.getFileno())));
 
                         // since we're not actually duping the incoming channel into our handler, we need to
                         // copy the original sync behavior from the other handler
                         selfFile.getMainStreamSafe().setSync(selfFile.getMainStreamSafe().isSync());
                     }
                     selfFile.setMode(mode);
                 }
 
                 // TODO: anything threads attached to original fd are notified of the close...
                 // see rb_thread_fd_close
 
                 if (origFile.isReadable() && pos >= 0) {
                     if (selfIsSeekable) {
                         selfFile.seek(pos, Stream.SEEK_SET);
                     }
 
                     if (origIsSeekable) {
                         origFile.seek(pos, Stream.SEEK_SET);
                     }
                 }
             }
 
             // only use internal fileno here, stdio is handled above
             if (selfFile.getPipeStream() != null && selfDescriptor.getFileno() != selfFile.getPipeStream().getDescriptor().getFileno()) {
                 int fd = selfFile.getPipeStream().getDescriptor().getFileno();
 
                 if (origFile.getPipeStream() == null) {
                     selfFile.getPipeStream().fclose();
                     selfFile.setPipeStream(null);
                 } else if (fd != origFile.getPipeStream().getDescriptor().getFileno()) {
                     selfFile.getPipeStream().fclose();
                     ChannelDescriptor newFD2 = origFile.getPipeStream().getDescriptor().dup2(fd);
                     selfFile.setPipeStream(ChannelStream.fdopen(runtime, newFD2, newIOOptions(runtime, "w").getModeFlags()));
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
             ex.printStackTrace();
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
 
     @Deprecated
     public static ModeFlags getIOModes(Ruby runtime, String modesString) {
         return newModeFlags(runtime, modesString);
     }
 
     @Deprecated
     public static int getIOModesIntFromString(Ruby runtime, String modesString) {
         try {
             return ModeFlags.getOFlagsFromString(modesString);
         } catch (InvalidValueException ive) {
             throw runtime.newArgumentError("illegal access mode");
         }
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
-            Encoding internal = getInternalEncoding(runtime);
+            Encoding internal = internalEncoding;
+            Encoding external = externalEncoding;
 
-            if (internal != null) {
+            if (internal != external) {
                 result = RubyString.newStringNoCopy(runtime,
                         RubyString.transcode(runtime.getCurrentContext(),
-                        ((RubyString) result).getByteList(), getExternalEncoding(runtime), internal,
+                        ((RubyString) result).getByteList(), external, internal,
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
-                    return RubyString.newEmptyString(runtime, getExternalEncoding(runtime));
+                    return RubyString.newEmptyString(runtime, externalEncoding);
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
 
     private IRubyObject initializeCommon19(ThreadContext context, int fileno, IRubyObject options, IOOptions ioOptions) {
         Ruby runtime = context.runtime;
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(runtime.getFilenoExtMap(fileno));
 
             if (descriptor == null) throw runtime.newErrnoEBADFError();
 
             descriptor.checkOpen();
 
             if (options != null && !(options instanceof RubyHash)) {
                 throw context.runtime.newTypeError(options, runtime.getHash());
             }
 
             if (ioOptions == null) {
                 ioOptions = newIOOptions(runtime, descriptor.getOriginalModes());
             }
 
             ioOptions = updateIOOptionsFromOptions(context, (RubyHash) options, ioOptions);
             setEncodingFromOptions(ioOptions.getEncodingOption());
 
             if (ioOptions == null) ioOptions = newIOOptions(runtime, descriptor.getOriginalModes());
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(runtime, false);
             }
 
             openFile.setMode(ioOptions.getModeFlags().getOpenFileFlags());
             openFile.setMainStream(fdopen(descriptor, ioOptions.getModeFlags()));
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         }
 
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, Block unused) {
         return initializeCommon19(context, RubyNumeric.fix2int(fileNumber), null, null);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject second, Block unused) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         IOOptions ioOptions = null;
         RubyHash options = null;
         if (second instanceof RubyHash) {
             options = (RubyHash)second;
         } else {
             ioOptions = parseIOOptions19(second);
         }
 
         return initializeCommon19(context, fileno, options, ioOptions);
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject fileNumber, IRubyObject modeValue, IRubyObject options, Block unused) {
         int fileno = RubyNumeric.fix2int(fileNumber);
         IOOptions ioOptions = parseIOOptions19(modeValue);
 
         return initializeCommon19(context, fileno, options, ioOptions);
     }
 
     // No encoding processing
     protected IOOptions parseIOOptions(IRubyObject arg) {
         Ruby runtime = getRuntime();
 
         if (arg instanceof RubyFixnum) return newIOOptions(runtime, (int) RubyFixnum.fix2long(arg));
 
         return newIOOptions(runtime, newModeFlags(runtime, arg.convertToString().toString()));
     }
 
     // Encoding processing
     protected IOOptions parseIOOptions19(IRubyObject arg) {
         Ruby runtime = getRuntime();
 
         if (arg instanceof RubyFixnum) return newIOOptions(runtime, (int) RubyFixnum.fix2long(arg));
 
         String modeString = arg.convertToString().toString();
         try {
             return new IOOptions(runtime, modeString);
         } catch (InvalidValueException ive) {
             throw runtime.newArgumentError("invalid access mode " + modeString);
         }
     }
 
     @JRubyMethod(required = 1, optional = 1, visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         Ruby runtime = getRuntime();
         int argCount = args.length;
         IOOptions ioOptions;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = ChannelDescriptor.getDescriptorByFileno(runtime.getFilenoExtMap(fileno));
             
             if (descriptor == null) {
                 throw runtime.newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     ioOptions = newIOOptions(runtime, RubyFixnum.fix2long(args[1]));
                 } else {
                     ioOptions = newIOOptions(runtime, args[1].convertToString().toString());
                 }
             } else {
                 // use original modes
                 ioOptions = newIOOptions(runtime, descriptor.getOriginalModes());
             }
 
             if (openFile.isOpen()) {
                 // JRUBY-4650: Make sure we clean up the old data,
                 // if it's present.
                 openFile.cleanup(runtime, false);
             }
 
             openFile.setMode(ioOptions.getModeFlags().getOpenFileFlags());
         
             openFile.setMainStream(fdopen(descriptor, ioOptions.getModeFlags()));
         } catch (BadDescriptorException ex) {
             throw runtime.newErrnoEBADFError();
         }
         
         return this;
     }
     
     protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) {
         Ruby runtime = getRuntime();
 
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         
         if (existingDescriptor == null) {
             // redundant, done above as well
             
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
             
             // IN FACT, we should probably raise an error, yes?
             throw runtime.newErrnoEBADFError();
             
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
             try {
                 return ChannelStream.fdopen(runtime, existingDescriptor, modes);
             } catch (InvalidValueException ive) {
                 throw runtime.newErrnoEINVALError();
             }
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
     public IRubyObject set_encoding(ThreadContext context, IRubyObject encodingObj) {
         if (encodingObj instanceof RubyString) {
             EncodingOption encodingOption = EncodingOption.getEncodingOptionFromString(context.runtime, encodingObj.convertToString().toString());
             setEncodingFromOptions(encodingOption);
         } else {
             setExternalEncoding(context, encodingObj);
         }
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
 
     private static Encoding getEncodingCommon(ThreadContext context, IRubyObject encoding) {
         if (encoding instanceof RubyEncoding) return ((RubyEncoding) encoding).getEncoding();
         
         return context.getRuntime().getEncodingService().getEncodingFromObject(encoding);
     }
 
     @JRubyMethod(required = 1, rest = true, meta = true)
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
 
     @JRubyMethod(required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_8)
     public static IRubyObject sysopen(IRubyObject recv, IRubyObject[] args, Block block) {
         StringSupport.checkStringSafety(recv.getRuntime(), args[0]);
         IRubyObject pathString = args[0].convertToString();
         return sysopenCommon(recv, args, block, pathString);
     }
     
     @JRubyMethod(name = "sysopen", required = 1, optional = 2, meta = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject sysopen19(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         StringSupport.checkStringSafety(context.getRuntime(), args[0]);
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
 
         IOOptions modes = null;
         int perms = -1; // -1 == don't set permissions
 
         if (args.length > 1) {
             IRubyObject modeString = args[1].convertToString();
             modes = newIOOptions(runtime, modeString.toString());
         } else {
             modes = newIOOptions(runtime, "r");
         }
         if (args.length > 2) {
             RubyInteger permsInt =
                 args.length >= 3 ? args[2].convertToInteger() : null;
             perms = RubyNumeric.fix2int(permsInt);
         }
 
         int fileno = -1;
         try {
             ChannelDescriptor descriptor =
                 ChannelDescriptor.open(runtime.getCurrentDirectory(),
                                        path, modes.getModeFlags(), perms, runtime.getPosix(),
                                        runtime.getJRubyClassLoader());
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
 
     public boolean isAutoclose() {
         return openFile.isAutoclose();
     }
 
     public void setAutoclose(boolean autoclose) {
         openFile.setAutoclose(autoclose);
     }
 
     @JRubyMethod
     public IRubyObject autoclose(ThreadContext context) {
         return context.runtime.newBoolean(isAutoclose());
     }
 
     @JRubyMethod(name = "autoclose=")
     public IRubyObject autoclose_set(ThreadContext context, IRubyObject autoclose) {
         setAutoclose(autoclose.isTrue());
         return context.nil;
     }
 
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
         if (isClosed()) throw getRuntime().newIOError("closed stream");
 
         Ruby runtime = getRuntime();
         if (getExternalEncoding(runtime) == USASCIIEncoding.INSTANCE) {
             externalEncoding = ASCIIEncoding.INSTANCE;
         }
         openFile.setBinmode();
         return this;
     }
 
     @JRubyMethod(name = "binmode?", compat = RUBY1_9)
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
             
             context.getThread().beforeBlockingCall();
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return runtime.newFixnum(read);
         } catch (InvalidValueException ex) {
             throw runtime.newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         } catch (IOException e) {
             if (e.getMessage().equals("Broken pipe")) {
                 throw runtime.newErrnoEPIPEError();
             }
             if (e.getMessage().equals("Connection reset by peer")) {
                 throw runtime.newErrnoEPIPEError();
             }
             throw runtime.newSystemCallError(e.getMessage());
         } finally {
             context.getThread().afterBlockingCall();
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(ThreadContext context, IRubyObject obj) {
         Ruby runtime = context.runtime;
 
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
 
             ChannelStream stream = (ChannelStream)myOpenFile.getWriteStream();
 
             int written = stream.writenonblock(str.getByteList());
             if (written == 0) {
                 if (runtime.is1_9()) {
                     throw runtime.newErrnoEAGAINWritableError("");
                 } else {
                     throw runtime.newErrnoEWOULDBLOCKError();
                 }
             }
 
             return context.getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw context.getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
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
 
             context.getThread().beforeBlockingCall();
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
         } finally {
             context.getThread().afterBlockingCall();
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
         try {
             return runtime.newFixnum(runtime.getFileno(getOpenFileChecked().getMainStreamSafe().getDescriptor()));
         } catch (BadDescriptorException e) {
             throw runtime.newErrnoEBADFError();
         }
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
         try {
             return context.getRuntime().newBoolean(getOpenFileChecked().getMainStreamSafe().isSync());
         } catch (BadDescriptorException e) {
             throw context.runtime.newErrnoEBADFError();
         }
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
             return context.getRuntime().newFixnum(getOpenFileChecked().getMainStreamSafe().fgetpos());
         } catch (InvalidValueException ex) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOErrorFromException(e);
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
             myOpenFile.getMainStreamSafe().lseek(offset, Stream.SEEK_SET);
         
             myOpenFile.getMainStreamSafe().clearerr();
         } catch (BadDescriptorException e) {
             throw context.getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw context.getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw context.getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw context.getRuntime().newIOErrorFromException(e);
         }
         
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
 
     @JRubyMethod(name = "putc", required = 1)
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
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index 85a2533305..88bf1bce13 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,1637 +1,1639 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
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
 
 import static org.jruby.CompatVersion.RUBY1_8;
 import static org.jruby.CompatVersion.RUBY1_9;
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.BACKREF;
 import static org.jruby.javasupport.util.RuntimeHelpers.invokedynamic;
 import static org.jruby.runtime.MethodIndex.OP_CMP;
 import static org.jruby.runtime.MethodIndex.OP_EQUAL;
 import static org.jruby.runtime.Visibility.PRIVATE;
 import static org.jruby.util.StringSupport.CR_7BIT;
 import static org.jruby.util.StringSupport.CR_BROKEN;
 import static org.jruby.util.StringSupport.CR_MASK;
 import static org.jruby.util.StringSupport.CR_UNKNOWN;
 import static org.jruby.util.StringSupport.CR_VALID;
 import static org.jruby.util.StringSupport.codeLength;
 import static org.jruby.util.StringSupport.codePoint;
 import static org.jruby.util.StringSupport.codeRangeScan;
 import static org.jruby.util.StringSupport.searchNonAscii;
 import static org.jruby.util.StringSupport.strLengthWithCodeRange;
 import static org.jruby.util.StringSupport.toLower;
 import static org.jruby.util.StringSupport.toUpper;
 import static org.jruby.util.StringSupport.unpackArg;
 import static org.jruby.util.StringSupport.unpackResult;
 
 import java.io.UnsupportedEncodingException;
 import java.nio.ByteBuffer;
 import java.nio.CharBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.CodingErrorAction;
 import java.util.HashSet;
 import java.util.Locale;
 import java.util.Set;
 
 import org.jcodings.Encoding;
 import org.jcodings.EncodingDB;
 import org.jcodings.ascii.AsciiTables;
 import org.jcodings.constants.CharacterType;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jcodings.specific.UTF8Encoding;
 import org.jcodings.util.CaseInsensitiveBytesHash;
 import org.jcodings.util.IntHash;
 import org.joni.Matcher;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingCapable;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.ConvertBytes;
 import org.jruby.util.MurmurHash;
 import org.jruby.util.Numeric;
 import org.jruby.util.Pack;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.Sprintf;
 import org.jruby.util.StringSupport;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.jruby.util.string.JavaCrypt;
 
 /**
  * Implementation of Ruby String class
  * 
  * Concurrency: no synchronization is required among readers, but
  * all users must synchronize externally with writers.
  *
  */
 @JRubyClass(name="String", include={"Enumerable", "Comparable"})
 public class RubyString extends RubyObject implements EncodingCapable {
 
     private static final Logger LOG = LoggerFactory.getLogger("RubyString");
 
     private static final ASCIIEncoding ASCII = ASCIIEncoding.INSTANCE;
     private static final UTF8Encoding UTF8 = UTF8Encoding.INSTANCE;
     private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
 
     // string doesn't share any resources
     private static final int SHARE_LEVEL_NONE = 0;
     // string has it's own ByteList, but it's pointing to a shared buffer (byte[])
     private static final int SHARE_LEVEL_BUFFER = 1;
     // string doesn't have it's own ByteList (values)
     private static final int SHARE_LEVEL_BYTELIST = 2;
 
     private volatile int shareLevel = SHARE_LEVEL_NONE;
 
     private ByteList value;
 
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         runtime.setString(stringClass);
         stringClass.index = ClassIndex.STRING;
         stringClass.setReifiedClass(RubyString.class);
         stringClass.kindOf = new RubyModule.KindOf() {
             @Override
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyString;
                 }
             };
 
         stringClass.includeModule(runtime.getComparable());
         if (!runtime.is1_9()) stringClass.includeModule(runtime.getEnumerable());
         stringClass.defineAnnotatedMethods(RubyString.class);
 
         return stringClass;
     }
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return RubyString.newEmptyString(runtime, klass);
         }
     };
 
     public Encoding getEncoding() {
         return value.getEncoding();
     }
 
     public void setEncoding(Encoding encoding) {
         value.setEncoding(encoding);
     }
 
     public void associateEncoding(Encoding enc) {
         if (value.getEncoding() != enc) {
             if (!isCodeRangeAsciiOnly() || !enc.isAsciiCompatible()) clearCodeRange();
             value.setEncoding(enc);
         }
     }
 
     public final void setEncodingAndCodeRange(Encoding enc, int cr) {
         value.setEncoding(enc);
         setCodeRange(cr);
     }
 
     public final Encoding toEncoding(Ruby runtime) {
         return runtime.getEncodingService().findEncoding(this);
     }
 
     public final int getCodeRange() {
         return flags & CR_MASK;
     }
 
     public final void setCodeRange(int codeRange) {
         clearCodeRange();
         flags |= codeRange & CR_MASK;
     }
 
     public final void clearCodeRange() {
         flags &= ~CR_MASK;
     }
 
     private void keepCodeRange() {
         if (getCodeRange() == CR_BROKEN) clearCodeRange();
     }
 
     // ENC_CODERANGE_ASCIIONLY
     public final boolean isCodeRangeAsciiOnly() {
         return getCodeRange() == CR_7BIT;
     }
 
     // rb_enc_str_asciionly_p
     public final boolean isAsciiOnly() {
         return value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT;
     }
 
     public final boolean isCodeRangeValid() {
         return (flags & CR_VALID) != 0;
     }
 
     public final boolean isCodeRangeBroken() {
         return (flags & CR_BROKEN) != 0;
     }
 
     static int codeRangeAnd(int cr1, int cr2) {
         if (cr1 == CR_7BIT) return cr2;
         if (cr1 == CR_VALID) return cr2 == CR_7BIT ? CR_VALID : cr2;
         return CR_UNKNOWN;
     }
 
     private void copyCodeRangeForSubstr(RubyString from, Encoding enc) {
         int fromCr = from.getCodeRange();
         if (fromCr == CR_7BIT) {
             setCodeRange(fromCr);
         } else if (fromCr == CR_VALID) {
             if (!enc.isAsciiCompatible() || searchNonAscii(value) != -1) {
                 setCodeRange(CR_VALID);
             } else {
                 setCodeRange(CR_7BIT);
             }
         } else{ 
             if (value.getRealSize() == 0) {
                 setCodeRange(!enc.isAsciiCompatible() ? CR_VALID : CR_7BIT);
             }
         }
     }
 
     private void copyCodeRange(RubyString from) {
         value.setEncoding(from.value.getEncoding());
         setCodeRange(from.getCodeRange());
     }
 
     // rb_enc_str_coderange
     final int scanForCodeRange() {
         int cr = getCodeRange();
         if (cr == CR_UNKNOWN) {
             cr = codeRangeScan(value.getEncoding(), value);
             setCodeRange(cr);
         }
         return cr;
     }
 
     final boolean singleByteOptimizable() {
         return getCodeRange() == CR_7BIT || value.getEncoding().isSingleByte();
     }
 
     final boolean singleByteOptimizable(Encoding enc) {
         return getCodeRange() == CR_7BIT || enc.isSingleByte();
     }
 
     private Encoding isCompatibleWith(RubyString other) { 
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.value.getEncoding();
 
         if (enc1 == enc2) return enc1;
 
         if (other.value.getRealSize() == 0) return enc1;
         if (value.getRealSize() == 0) return enc2;
 
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
 
         return RubyEncoding.areCompatible(enc1, scanForCodeRange(), enc2, other.scanForCodeRange());
     }
 
     final Encoding isCompatibleWith(EncodingCapable other) {
         if (other instanceof RubyString) return checkEncoding((RubyString)other);
         Encoding enc1 = value.getEncoding();
         Encoding enc2 = other.getEncoding();
 
         if (enc1 == enc2) return enc1;
         if (value.getRealSize() == 0) return enc2;
         if (!enc1.isAsciiCompatible() || !enc2.isAsciiCompatible()) return null;
         if (enc2 instanceof USASCIIEncoding) return enc1;
         if (scanForCodeRange() == CR_7BIT) return enc2;
         return null;
     }
 
     public final Encoding checkEncoding(RubyString other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.value.getEncoding());
         return enc;
     }
 
     final Encoding checkEncoding(EncodingCapable other) {
         Encoding enc = isCompatibleWith(other);
         if (enc == null) throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + 
                                 value.getEncoding() + " and " + other.getEncoding());
         return enc;
     }
 
     private Encoding checkDummyEncoding() {
         Encoding enc = value.getEncoding();
         if (enc.isDummy()) throw getRuntime().newEncodingCompatibilityError(
                 "incompatible encoding with this operation: " + enc);
         return enc;
     }
 
     private boolean isComparableWith(RubyString other) {
         ByteList otherValue = other.value;
         if (value.getEncoding() == otherValue.getEncoding() ||
             value.getRealSize() == 0 || otherValue.getRealSize() == 0) return true;
         return isComparableViaCodeRangeWith(other);
     }
 
     private boolean isComparableViaCodeRangeWith(RubyString other) {
         int cr1 = scanForCodeRange();
         int cr2 = other.scanForCodeRange();
 
         if (cr1 == CR_7BIT && (cr2 == CR_7BIT || other.value.getEncoding().isAsciiCompatible())) return true;
         if (cr2 == CR_7BIT && value.getEncoding().isAsciiCompatible()) return true;
         return false;
     }
 
     private int strLength(Encoding enc) {
         if (singleByteOptimizable(enc)) return value.getRealSize();
         return strLength(value, enc);
     }
 
     public final int strLength() {
         if (singleByteOptimizable()) return value.getRealSize();
         return strLength(value);
     }
 
     private int strLength(ByteList bytes) {
         return strLength(bytes, bytes.getEncoding());
     }
 
     private int strLength(ByteList bytes, Encoding enc) {
         if (isCodeRangeValid() && enc instanceof UTF8Encoding) return StringSupport.utf8Length(value);
 
         long lencr = strLengthWithCodeRange(bytes, enc);
         int cr = unpackArg(lencr);
         if (cr != 0) setCodeRange(cr);
         return unpackResult(lencr);
     }
 
     final int subLength(int pos) {
         if (singleByteOptimizable() || pos < 0) return pos;
         return StringSupport.strLength(value.getEncoding(), value.getUnsafeBytes(), value.getBegin(), value.getBegin() + pos);
     }
 
     /** short circuit for String key comparison
      * 
      */
     @Override
     public final boolean eql(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (getMetaClass() != runtime.getString() || getMetaClass() != other.getMetaClass()) return super.eql(other);
         return runtime.is1_9() ? eql19(runtime, other) : eql18(runtime, other);
     }
 
     private boolean eql18(Ruby runtime, IRubyObject other) {
         return value.equal(((RubyString)other).value);
     }
 
     // rb_str_hash_cmp
     private boolean eql19(Ruby runtime, IRubyObject other) {
         RubyString otherString = (RubyString)other;
         return isComparableWith(otherString) && value.equal(((RubyString)other).value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass) {
         this(runtime, rubyClass, EMPTY_BYTE_ARRAY);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
         assert value != null;
         Charset charset = null;
         Encoding defaultEncoding = runtime.getEncodingService().getLocaleEncoding();
         if (defaultEncoding == null) defaultEncoding = UTF8;
 
         charset = defaultEncoding.getCharset();
         byte[] bytes = RubyEncoding.encode(value, charset);
 
         this.value = new ByteList(bytes, defaultEncoding, false);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(value);
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = value;
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, boolean objectSpace) {
         super(runtime, rubyClass, objectSpace);
         assert value != null;
         this.value = value;
     }
 
     public RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding encoding, boolean objectSpace) {
         this(runtime, rubyClass, value, objectSpace);
         value.setEncoding(encoding);
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc, int cr) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
         flags |= cr;
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, Encoding enc) {
         this(runtime, rubyClass, value);
         value.setEncoding(enc);
     }
 
     protected RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, int cr) {
         this(runtime, rubyClass, value);
         flags |= cr;
     }
 
     // Deprecated String construction routines
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated  
      */
     @Deprecated
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *  @deprecated
      */
     @Deprecated
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getMetaClass(), s);
     }
 
     @Deprecated
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newStringLight(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes, false);
     }
 
     public static RubyString newStringLight(Ruby runtime, int size) {
         return new RubyString(runtime, runtime.getString(), new ByteList(size), false);
     }
 
     public static RubyString newStringLight(Ruby runtime, int size, Encoding encoding) {
         return new RubyString(runtime, runtime.getString(), new ByteList(size), encoding, false);
     }
   
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
 
     public static RubyString newString(Ruby runtime, String str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
     
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return new RubyString(runtime, runtime.getString(), new ByteList(copy, false));
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes, Encoding encoding) {
         return new RubyString(runtime, runtime.getString(), bytes, encoding);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, String str) {
         ByteList byteList = new ByteList(RubyEncoding.encodeUTF8(str), UTF8Encoding.INSTANCE, false);
         return new RubyString(runtime, runtime.getString(), byteList);
     }
     
     public static RubyString newUnicodeString(Ruby runtime, CharSequence str) {
         ByteList byteList = new ByteList(RubyEncoding.encodeUTF8(str), UTF8Encoding.INSTANCE, false);
         return new RubyString(runtime, runtime.getString(), byteList);
     }
 
     /**
      * Return a new Ruby String encoded as the default internal encoding given a Java String that
      * has come from an external source. If there is no default internal encoding set, the Ruby
      * String will be encoded using Java's default external encoding. If an internal encoding is
      * set, that encoding will be used for the Ruby String.
      *
      * @param runtime
      * @param str
      * @return
      */
     public static RubyString newInternalFromJavaExternal(Ruby runtime, String str) {
         // Ruby internal
         Encoding internal = runtime.getDefaultInternalEncoding();
         Charset rubyInt = null;
         if (internal != null && internal.getCharset() != null) rubyInt = internal.getCharset();
 
         // Java external, used if no internal
         Charset javaExt = Charset.defaultCharset();
         Encoding javaExtEncoding = runtime.getEncodingService().getJavaDefault();
 
         if (rubyInt == null) {
             return RubyString.newString(
                     runtime,
                     new ByteList(str.getBytes(), javaExtEncoding));
         } else {
             return RubyString.newString(
                     runtime,
                     new ByteList(RubyEncoding.encode(str, rubyInt), internal));
         }
     }
 
     // String construction routines by NOT byte[] buffer and making the target String shared 
     public static RubyString newStringShared(Ruby runtime, RubyString orig) {
         orig.shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString str = new RubyString(runtime, runtime.getString(), orig.value);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }       
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes) {
         return newStringShared(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, Encoding encoding) {
         return newStringShared(runtime, runtime.getString(), bytes, encoding);
     }
 
 
     public static RubyString newStringShared(Ruby runtime, ByteList bytes, int codeRange) {
         RubyString str = new RubyString(runtime, runtime.getString(), bytes, codeRange);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes) {
         RubyString str = new RubyString(runtime, clazz, bytes);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding encoding) {
         RubyString str = new RubyString(runtime, clazz, bytes, encoding);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes) {
         return newStringShared(runtime, new ByteList(bytes, false));
     }
 
     public static RubyString newStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringShared(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newEmptyString(Ruby runtime) {
         return newEmptyString(runtime, runtime.getString());
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, ByteList.EMPTY_BYTELIST);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     // String construction routines by NOT byte[] buffer and NOT making the target String shared 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, runtime.getString(), bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes) {
         return new RubyString(runtime, clazz, bytes);
     }    
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes, int start, int length) {
         return newStringNoCopy(runtime, new ByteList(bytes, start, length, false));
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, byte[] bytes) {
         return newStringNoCopy(runtime, new ByteList(bytes, false));
     }
 
     /** Encoding aware String construction routines for 1.9
      * 
      */
     private static final class EmptyByteListHolder {
         final ByteList bytes;
         final int cr;
         EmptyByteListHolder(Encoding enc) {
             this.bytes = new ByteList(ByteList.NULL_ARRAY, enc);
             this.cr = bytes.getEncoding().isAsciiCompatible() ? CR_7BIT : CR_VALID;
         }
     }
 
     private static EmptyByteListHolder EMPTY_BYTELISTS[] = new EmptyByteListHolder[4];
 
     static EmptyByteListHolder getEmptyByteList(Encoding enc) {
+        if (enc == null) enc = ASCIIEncoding.INSTANCE;
         int index = enc.getIndex();
         EmptyByteListHolder bytes;
         if (index < EMPTY_BYTELISTS.length && (bytes = EMPTY_BYTELISTS[index]) != null) {
             return bytes;
         }
         return prepareEmptyByteList(enc);
     }
 
     private static EmptyByteListHolder prepareEmptyByteList(Encoding enc) {
+        if (enc == null) enc = ASCIIEncoding.INSTANCE;
         int index = enc.getIndex();
         if (index >= EMPTY_BYTELISTS.length) {
             EmptyByteListHolder tmp[] = new EmptyByteListHolder[index + 4];
             System.arraycopy(EMPTY_BYTELISTS,0, tmp, 0, EMPTY_BYTELISTS.length);
             EMPTY_BYTELISTS = tmp;
         }
         return EMPTY_BYTELISTS[index] = new EmptyByteListHolder(enc);
     }
 
     public static RubyString newEmptyString(Ruby runtime, RubyClass metaClass, Encoding enc) {
         EmptyByteListHolder holder = getEmptyByteList(enc);
         RubyString empty = new RubyString(runtime, metaClass, holder.bytes, holder.cr);
         empty.shareLevel = SHARE_LEVEL_BYTELIST;
         return empty;
     }
 
     public static RubyString newEmptyString(Ruby runtime, Encoding enc) {
         return newEmptyString(runtime, runtime.getString(), enc);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, RubyClass clazz, ByteList bytes, Encoding enc, int cr) {
         return new RubyString(runtime, clazz, bytes, enc, cr);
     }
 
     public static RubyString newStringNoCopy(Ruby runtime, ByteList bytes, Encoding enc, int cr) {
         return newStringNoCopy(runtime, runtime.getString(), bytes, enc, cr);
     }
 
     public static RubyString newUsAsciiStringNoCopy(Ruby runtime, ByteList bytes) {
         return newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
     }
 
     public static RubyString newUsAsciiStringShared(Ruby runtime, ByteList bytes) {
         RubyString str = newStringNoCopy(runtime, bytes, USASCIIEncoding.INSTANCE, CR_7BIT);
         str.shareLevel = SHARE_LEVEL_BYTELIST;
         return str;
     }
     
     public static RubyString newUsAsciiStringShared(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] copy = new byte[length];
         System.arraycopy(bytes, start, copy, 0, length);
         return newUsAsciiStringShared(runtime, new ByteList(copy, false));
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     @Override
     public Class getJavaClass() {
         return String.class;
     }
 
     @Override
     public RubyString convertToString() {
         return this;
     }
 
     @Override
     public String toString() {
         return decodeString();
     }
 
     /**
      * Convert this Ruby string to a Java String. This version is encoding-aware.
      *
      * @return A decoded Java String, based on this Ruby string's encoding.
      */
     public String decodeString() {
         Ruby runtime = getRuntime();
         // Note: we always choose UTF-8 for outbound strings in 1.8 mode.  This is clearly undesirable
         // but we do not mark any incoming Strings from JI with their real encoding so we just pick utf-8.
         
         if (runtime.is1_9()) {
             Encoding encoding = getEncoding();
             
             if (encoding == UTF8) {
                 // faster UTF8 decoding
                 return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
             }
             
             Charset charset = runtime.getEncodingService().charsetForEncoding(encoding);
 
             encoding.getCharset();
 
             // charset is not defined for this encoding in jcodings db.  Try letting Java resolve this.
             if (charset == null) {
                 try {
                     return new String(value.getUnsafeBytes(), value.begin(), value.length(), encoding.toString());
                 } catch (UnsupportedEncodingException uee) {
                     return value.toString();
                 }
             }
             
             return RubyEncoding.decode(value.getUnsafeBytes(), value.begin(), value.length(), charset);
         } else {
             // fast UTF8 decoding
             return RubyEncoding.decodeUTF8(value.getUnsafeBytes(), value.begin(), value.length());
         }
     }
 
     /**
      * Overridden dup for fast-path logic.
      *
      * @return A new RubyString sharing the original backing store.
      */
     @Override
     public IRubyObject dup() {
         RubyClass mc = metaClass.getRealClass();
         if (mc.index != ClassIndex.STRING) return super.dup();
 
         return strDup(mc.getClassRuntime(), mc.getRealClass());
     }
 
     /** rb_str_dup
      * 
      */
     @Deprecated
     public final RubyString strDup() {
         return strDup(getRuntime(), getMetaClass());
     }
     
     public final RubyString strDup(Ruby runtime) {
         return strDup(runtime, getMetaClass());
     }
     
     @Deprecated
     final RubyString strDup(RubyClass clazz) {
         return strDup(getRuntime(), getMetaClass());
     }
 
     final RubyString strDup(Ruby runtime, RubyClass clazz) {
         shareLevel = SHARE_LEVEL_BYTELIST;
         RubyString dup = new RubyString(runtime, clazz, value);
         dup.shareLevel = SHARE_LEVEL_BYTELIST;
         dup.flags |= flags & (CR_MASK | TAINTED_F | UNTRUSTED_F);
         
         return dup;
     }
     
     /* rb_str_subseq */
     public final RubyString makeSharedString(Ruby runtime, int index, int len) {
         return makeShared(runtime, runtime.getString(), index, len);
     }
     
     public RubyString makeSharedString19(Ruby runtime, int index, int len) {
         return makeShared19(runtime, runtime.getString(), value, index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, int index, int len) {
         return makeShared(runtime, getType(), index, len);
     }
 
     public final RubyString makeShared(Ruby runtime, RubyClass meta, int index, int len) {
         final RubyString shared;
         if (len == 0) {
             shared = newEmptyString(runtime, meta);
         } else if (len == 1) {
             shared = newStringShared(runtime, meta, 
                     RubyInteger.SINGLE_CHAR_BYTELISTS[value.getUnsafeBytes()[value.getBegin() + index] & 0xff]);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         shared.infectBy(this);
         return shared;
     }
 
     public final RubyString makeShared19(Ruby runtime, int index, int len) {
         return makeShared19(runtime, value, index, len);
     }
 
     private RubyString makeShared19(Ruby runtime, ByteList value, int index, int len) {
         return makeShared19(runtime, getType(), value, index, len);
     }
     
     private RubyString makeShared19(Ruby runtime, RubyClass meta, ByteList value, int index, int len) {
         final RubyString shared;
         Encoding enc = value.getEncoding();
 
         if (len == 0) {
             shared = newEmptyString(runtime, meta, enc);
         } else {
             if (shareLevel == SHARE_LEVEL_NONE) shareLevel = SHARE_LEVEL_BUFFER;
             shared = new RubyString(runtime, meta, value.makeShared(index, len));
             shared.shareLevel = SHARE_LEVEL_BUFFER;
             shared.copyCodeRangeForSubstr(this, enc); // no need to assign encoding, same bytelist shared
         }
         shared.infectBy(this);
         return shared;
     }
 
     public final void setByteListShared() {
         if (shareLevel != SHARE_LEVEL_BYTELIST) shareLevel = SHARE_LEVEL_BYTELIST;
     }
 
     /**
      * Check that the string can be modified, raising error otherwise.
      *
      * If you plan to modify a string with shared backing store, this
      * method is not sufficient; you will need to call modify() instead.
      */
     public final void modifyCheck() {
         frozenCheck();
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
     }
 
     private void modifyCheck(byte[] b, int len) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len) throw getRuntime().newRuntimeError("string modified");
     }
 
     private void modifyCheck(byte[] b, int len, Encoding enc) {
         if (value.getUnsafeBytes() != b || value.getRealSize() != len || value.getEncoding() != enc) throw getRuntime().newRuntimeError("string modified");
     }
 
     private void frozenCheck() {
         frozenCheck(false);
     }
 
     private void frozenCheck(boolean runtimeError) {
         if (isFrozen()) throw getRuntime().newFrozenError("string", runtimeError);
     }
 
     /** rb_str_modify
      *
      */
     public final void modify() {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup();
             } else {
                 value.unshare();
             }
             shareLevel = SHARE_LEVEL_NONE;
         }
 
         value.invalidate();
     }
 
     public final void modify19() {
         modify();
         clearCodeRange();
     }
 
     private void modifyAndKeepCodeRange() {
         modify();
         keepCodeRange();
     }
 
     /** rb_str_modify (with length bytes ensured)
      *
      */
     public final void modify(int length) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 value = value.dup(length);
             } else {
                 value.unshare(length);
             }
             shareLevel = SHARE_LEVEL_NONE;
         } else {
             value.ensure(length);
         }
 
         value.invalidate();
     }
 
     public final void modify19(int length) {
         modify(length);
         clearCodeRange();
     }
 
     /** rb_str_resize
      */
     public final void resize(int length) {
         modify();
         if (value.getRealSize() > length) {
             value.setRealSize(length);
         } else if (value.length() < length) {
             value.length(length);
         }
     }
 
     public final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         shareLevel = SHARE_LEVEL_NONE;
     }
 
     private void view(byte[]bytes) {
         modifyCheck();
 
         value = new ByteList(bytes);
         shareLevel = SHARE_LEVEL_NONE;
 
         value.invalidate();
     }
 
     private void view(int index, int len) {
         modifyCheck();
 
         if (shareLevel != SHARE_LEVEL_NONE) {
             if (shareLevel == SHARE_LEVEL_BYTELIST) {
                 // if len == 0 then shared empty
                 value = value.makeShared(index, len);
                 shareLevel = SHARE_LEVEL_BUFFER;
             } else {
                 value.view(index, len);
             }
         } else {
             value.view(index, len);
             // FIXME this below is temporary, but its much safer for COW (it prevents not shared Strings with begin != 0)
             // this allows now e.g.: ByteList#set not to be begin aware
             shareLevel = SHARE_LEVEL_BUFFER;
         }
 
         value.invalidate();
     }
 
     public static String bytesToString(byte[] bytes, int beg, int len) {
         return new String(ByteList.plain(bytes, beg, len));
     }
 
     public static String byteListToString(ByteList bytes) {
         return bytesToString(bytes.getUnsafeBytes(), bytes.begin(), bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes, 0, bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     @Override
     public RubyString asString() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType() {
         return this;
     }
 
     @Override
     public IRubyObject checkStringType19() {
         return this;
     }
 
     @JRubyMethod(name = "try_convert", meta = true, compat = RUBY1_9)
     public static IRubyObject try_convert(ThreadContext context, IRubyObject recv, IRubyObject str) {
         return str.checkStringType();
     }
 
     @JRubyMethod(name = {"to_s", "to_str"})
     @Override
     public IRubyObject to_s() {
         Ruby runtime = getRuntime();
         if (getMetaClass().getRealClass() != runtime.getString()) {
             return strDup(runtime, runtime.getString());
         }
         return this;
     }
 
     @Override
     public final int compareTo(IRubyObject other) {
         Ruby runtime = getRuntime();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return runtime.is1_9() ? op_cmp19(otherString) : op_cmp(otherString);
         }
         return (int)op_cmpCommon(runtime.getCurrentContext(), other).convertToInteger().getLongValue();
     }
 
     /* rb_str_cmp_m */
     @JRubyMethod(name = "<=>", compat = RUBY1_8)
     @Override
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     @JRubyMethod(name = "<=>", compat = RUBY1_9)
     public IRubyObject op_cmp19(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyString) {
             return context.getRuntime().newFixnum(op_cmp19((RubyString)other));
         }
         return op_cmpCommon(context, other);
     }
 
     private IRubyObject op_cmpCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         // deal with case when "other" is not a string
         if (other.respondsTo("to_str") && other.respondsTo("<=>")) {
             IRubyObject result = invokedynamic(context, other, OP_CMP, this);
             if (result.isNil()) return result;
             if (result instanceof RubyFixnum) {
                 return RubyFixnum.newFixnum(runtime, -((RubyFixnum)result).getLongValue());
             } else {
                 return RubyFixnum.zero(runtime).callMethod(context, "-", result);
             }
         }
         return runtime.getNil();
     }
 
     /** rb_str_equal
      *
      */
     @JRubyMethod(name = "==", compat = RUBY1_8)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             return value.equal(((RubyString)other).value) ? runtime.getTrue() : runtime.getFalse();
         }
         return op_equalCommon(context, other);
     }
 
     @JRubyMethod(name = {"==", "==="}, compat = RUBY1_9)
     public IRubyObject op_equal19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (this == other) return runtime.getTrue();
         if (other instanceof RubyString) {
             RubyString otherString = (RubyString)other;
             return isComparableWith(otherString) && value.equal(otherString.value) ? runtime.getTrue() : runtime.getFalse();
         }
         return op_equalCommon(context, other);
     }
 
     private IRubyObject op_equalCommon(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         if (!other.respondsTo("to_str")) return runtime.getFalse();
         return invokedynamic(context, other, OP_EQUAL, this).isTrue() ? runtime.getTrue() : runtime.getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_8)
     public IRubyObject op_plus(ThreadContext context, IRubyObject _str) {
         RubyString str = _str.convertToString();
         RubyString resultStr = newString(context.getRuntime(), addByteLists(value, str.value));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
 
     @JRubyMethod(name = "+", required = 1, compat = RUBY1_9)
     public IRubyObject op_plus19(ThreadContext context, IRubyObject _str) {
         RubyString str = _str.convertToString();
         Encoding enc = checkEncoding(str);
         RubyString resultStr = newStringNoCopy(context.getRuntime(), addByteLists(value, str.value),
                                     enc, codeRangeAnd(getCodeRange(), str.getCodeRange()));
         resultStr.infectBy(flags | str.flags);
         return resultStr;
     }
 
     private ByteList addByteLists(ByteList value1, ByteList value2) {
         ByteList result = new ByteList(value1.getRealSize() + value2.getRealSize());
         result.setRealSize(value1.getRealSize() + value2.getRealSize());
         System.arraycopy(value1.getUnsafeBytes(), value1.getBegin(), result.getUnsafeBytes(), 0, value1.getRealSize());
         System.arraycopy(value2.getUnsafeBytes(), value2.getBegin(), result.getUnsafeBytes(), value1.getRealSize(), value2.getRealSize());
         return result;
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_8)
     public IRubyObject op_mul(ThreadContext context, IRubyObject other) {
         return multiplyByteList(context, other);
     }
 
     @JRubyMethod(name = "*", required = 1, compat = RUBY1_9)
     public IRubyObject op_mul19(ThreadContext context, IRubyObject other) {
         RubyString result = multiplyByteList(context, other);
         result.value.setEncoding(value.getEncoding());
         result.copyCodeRange(this);
         return result;
     }
 
     private RubyString multiplyByteList(ThreadContext context, IRubyObject arg) {
         int len = RubyNumeric.num2int(arg);
         if (len < 0) throw context.getRuntime().newArgumentError("negative argument");
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.getRealSize()) {
             throw context.getRuntime().newArgumentError("argument too big");
         }
 
         ByteList bytes = new ByteList(len *= value.getRealSize());
         if (len > 0) {
             bytes.setRealSize(len);
             int n = value.getRealSize();
             System.arraycopy(value.getUnsafeBytes(), value.getBegin(), bytes.getUnsafeBytes(), 0, n);
             while (n <= len >> 1) {
                 System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, n);
                 n <<= 1;
             }
             System.arraycopy(bytes.getUnsafeBytes(), 0, bytes.getUnsafeBytes(), n, len - n);
         }
         RubyString result = new RubyString(context.getRuntime(), getMetaClass(), bytes);
         result.infectBy(this);
         return result;
     }
 
     @JRubyMethod(name = "%", required = 1)
     public IRubyObject op_format(ThreadContext context, IRubyObject arg) {
         return opFormatCommon(context, arg, context.getRuntime().getInstanceConfig().getCompatVersion());
     }
 
     private IRubyObject opFormatCommon(ThreadContext context, IRubyObject arg, CompatVersion compat) {
         IRubyObject tmp;
         if (context.runtime.is1_9() && arg instanceof RubyHash) {
             tmp = arg;
         } else {
             tmp = arg.checkArrayType();
             if (tmp.isNil()) tmp = arg;
         }
 
         ByteList out = new ByteList(value.getRealSize());
         out.setEncoding(value.getEncoding());
 
         boolean tainted;
 
         // FIXME: Should we make this work with platform's locale,
         // or continue hardcoding US?
         switch (compat) {
         case RUBY1_8:
             tainted = Sprintf.sprintf(out, Locale.US, value, tmp);
             break;
         case RUBY1_9:
         case RUBY2_0:
             tainted = Sprintf.sprintf1_9(out, Locale.US, value, tmp);
             break;
         default:
             throw new RuntimeException("invalid compat version for sprintf: " + compat);
         }
         RubyString str = newString(context.getRuntime(), out);
 
         str.setTaint(tainted || isTaint());
         return str;
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
         Ruby runtime = getRuntime();
         return RubyFixnum.newFixnum(runtime, strHashCode(runtime));
     }
 
     @Override
     public int hashCode() {
         return strHashCode(getRuntime());
     }
 
     /**
      * Generate a murmurhash for the String, using its associated Ruby instance's hash seed.
      *
      * @param runtime
      * @return
      */
     public int strHashCode(Ruby runtime) {
         int hash = MurmurHash.hash32(value.getUnsafeBytes(), value.getBegin(), value.getRealSize(), runtime.getHashSeed());
         if (runtime.is1_9()) {
             hash ^= (value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT ? 0 : value.getEncoding().getIndex());
         }
         return hash;
     }
 
     /**
      * Generate a murmurhash for the String, without a seed.
      *
      * @param runtime
      * @return
      */
     public int unseededStrHashCode(Ruby runtime) {
         int hash = MurmurHash.hash32(value.getUnsafeBytes(), value.getBegin(), value.getRealSize(), 0);
         if (runtime.is1_9()) {
             hash ^= (value.getEncoding().isAsciiCompatible() && scanForCodeRange() == CR_7BIT ? 0 : value.getEncoding().getIndex());
         }
         return hash;
     }
 
     @Override
     public boolean equals(Object other) {
         if (this == other) return true;
 
         if (other instanceof RubyString) {
             if (((RubyString) other).value.equal(value)) return true;
         }
 
         return false;
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(ThreadContext context, IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
         IRubyObject str = obj.callMethod(context, "to_s");
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
         if (obj.isTaint()) str.setTaint(true);
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public final int op_cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     public final int op_cmp19(RubyString other) {
         int ret = value.cmp(other.value);
         if (ret == 0 && !isComparableWith(other)) {
             return value.getEncoding().getIndex() > other.value.getEncoding().getIndex() ? 1 : -1;
         }
         return ret;
     }
 
     /** rb_to_id
      *
      */
     @Override
     public String asJavaString() {
         return toString();
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), value.dup());
     }
 
     public final RubyString cat(byte[] str) {
         modify(value.getRealSize() + str.length);
         System.arraycopy(str, 0, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.length);
         value.setRealSize(value.getRealSize() + str.length);
         return this;
     }
 
     public final RubyString cat(byte[] str, int beg, int len) {
         modify(value.getRealSize() + len);
         System.arraycopy(str, beg, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         return this;
     }
 
     // // rb_str_buf_append
     public final RubyString cat19(RubyString str) {
         ByteList other = str.value;
         int otherCr = cat(other.getUnsafeBytes(), other.getBegin(), other.getRealSize(),
                 other.getEncoding(), str.getCodeRange());
         infectBy(str);
         str.setCodeRange(otherCr);
         return this;
     }
 
     public final RubyString cat(RubyString str) {
         return cat(str.getByteList());
     }
 
     public final RubyString cat(ByteList str) {
         modify(value.getRealSize() + str.getRealSize());
         System.arraycopy(str.getUnsafeBytes(), str.getBegin(), value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), str.getRealSize());
         value.setRealSize(value.getRealSize() + str.getRealSize());
         return this;
     }
 
     public final RubyString cat(byte ch) {
         modify(value.getRealSize() + 1);
         value.getUnsafeBytes()[value.getBegin() + value.getRealSize()] = ch;
         value.setRealSize(value.getRealSize() + 1);
         return this;
     }
 
     public final RubyString cat(int ch) {
         return cat((byte)ch);
     }
 
     public final RubyString cat(int code, Encoding enc) {
         int n = codeLength(getRuntime(), enc, code);
         modify(value.getRealSize() + n);
         enc.codeToMbc(code, value.getUnsafeBytes(), value.getBegin() + value.getRealSize());
         value.setRealSize(value.getRealSize() + n);
         return this;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc, int cr) {
         modify(value.getRealSize() + len);
         int toCr = getCodeRange();
         Encoding toEnc = value.getEncoding();
         int cr2 = cr;
 
         if (toEnc == enc) {
             if (toCr == CR_UNKNOWN || (toEnc == ASCIIEncoding.INSTANCE && toCr != CR_7BIT)) {
                 cr = CR_UNKNOWN;
             } else if (cr == CR_UNKNOWN) {
                 cr = codeRangeScan(enc, bytes, p, len);
             }
         } else {
             if (!toEnc.isAsciiCompatible() || !enc.isAsciiCompatible()) {
                 if (len == 0) return toCr;
                 if (value.getRealSize() == 0) {
                     System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
                     value.setRealSize(value.getRealSize() + len);
                     setEncodingAndCodeRange(enc, cr);
                     return cr;
                 }
                 throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + toEnc + " and " + enc);
             }
             if (cr == CR_UNKNOWN) cr = codeRangeScan(enc, bytes, p, len);
             if (toCr == CR_UNKNOWN) {
                 if (toEnc == ASCIIEncoding.INSTANCE || cr != CR_7BIT) toCr = scanForCodeRange();
             }
         }
         if (cr2 != 0) cr2 = cr;
 
         if (toEnc != enc && toCr != CR_7BIT && cr != CR_7BIT) {
             throw getRuntime().newEncodingCompatibilityError("incompatible character encodings: " + toEnc + " and " + enc);
         }
 
         final int resCr;
         final Encoding resEnc;
         if (toCr == CR_UNKNOWN) {
             resEnc = toEnc;
             resCr = CR_UNKNOWN;
         } else if (toCr == CR_7BIT) {
             if (cr == CR_7BIT) {
                 resEnc = toEnc != ASCIIEncoding.INSTANCE ? toEnc : enc;
                 resCr = CR_7BIT;
             } else {
                 resEnc = enc;
                 resCr = cr;
             }
         } else if (toCr == CR_VALID) {
             resEnc = toEnc;
             if (cr == CR_7BIT || cr == CR_VALID) {
                 resCr = toCr;
             } else {
                 resCr = cr;
             }
         } else {
             resEnc = toEnc;
             resCr = len > 0 ? CR_UNKNOWN : toCr;
         }
 
         if (len < 0) throw getRuntime().newArgumentError("negative string size (or size too big)");
 
         System.arraycopy(bytes, p, value.getUnsafeBytes(), value.getBegin() + value.getRealSize(), len);
         value.setRealSize(value.getRealSize() + len);
         setEncodingAndCodeRange(resEnc, resCr);
 
         return cr2;
     }
 
     public final int cat(byte[]bytes, int p, int len, Encoding enc) {
         return cat(bytes, p, len, enc, CR_UNKNOWN);
     }
 
     public final RubyString catAscii(byte[]bytes, int p, int len) {
         Encoding enc = value.getEncoding();
         if (enc.isAsciiCompatible()) {
             cat(bytes, p, len, enc, CR_7BIT);
         } else {
             byte buf[] = new byte[enc.maxLength()];
             int end = p + len;
             while (p < end) {
                 int c = bytes[p];
                 int cl = codeLength(getRuntime(), enc, c);
                 enc.codeToMbc(c, buf, 0);
                 cat(buf, 0, cl, enc, CR_VALID);
                 p++;
             }
         }
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = RUBY1_8)
     public IRubyObject replace(IRubyObject other) {
         if (this == other) return this;
         replaceCommon(other);
         return this;
     }
 
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1, compat = RUBY1_9)
     public RubyString replace19(IRubyObject other) {
         modifyCheck();
         if (this == other) return this;
         setCodeRange(replaceCommon(other).getCodeRange()); // encoding doesn't have to be copied.
         return this;
     }
 
     private RubyString replaceCommon(IRubyObject other) {
         modifyCheck();
         RubyString otherStr = other.convertToString();
         otherStr.shareLevel = shareLevel = SHARE_LEVEL_BYTELIST;
         value = otherStr.value;
         infectBy(otherStr);
         return otherStr;
     }
 
     @JRubyMethod(name = "clear", compat = RUBY1_9)
     public RubyString clear() {
         modifyCheck();
         Encoding enc = value.getEncoding();
 
         EmptyByteListHolder holder = getEmptyByteList(enc);
         value = holder.bytes;
         shareLevel = SHARE_LEVEL_BYTELIST;
         setCodeRange(holder.cr);
         return this;
     }
 
     @JRubyMethod(name = "reverse", compat = RUBY1_8)
     public IRubyObject reverse(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() <= 1) return strDup(context.getRuntime());
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         byte[]obytes = new byte[len];
 
         for (int i = 0; i <= len >> 1; i++) {
             obytes[i] = bytes[p + len - i - 1];
             obytes[len - i - 1] = bytes[p + i];
         }
 
         return new RubyString(runtime, getMetaClass(), new ByteList(obytes, false)).infectBy(this);
     }
 
     @JRubyMethod(name = "reverse", compat = RUBY1_9)
     public IRubyObject reverse19(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         if (value.getRealSize() <= 1) return strDup(context.getRuntime());
 
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int len = value.getRealSize();
         byte[]obytes = new byte[len];
 
         boolean single = true;
         Encoding enc = value.getEncoding();
         // this really needs to be inlined here
         if (singleByteOptimizable(enc)) {
             for (int i = 0; i <= len >> 1; i++) {
                 obytes[i] = bytes[p + len - i - 1];
                 obytes[len - i - 1] = bytes[p + i];
             }
         } else {
             int end = p + len;
             int op = len;
             while (p < end) {
                 int cl = StringSupport.length(enc, bytes, p, end);
                 if (cl > 1 || (bytes[p] & 0x80) != 0) {
                     single = false;
                     op -= cl;
                     System.arraycopy(bytes, p, obytes, op, cl);
                     p += cl;
                 } else {
                     obytes[--op] = bytes[p++];
                 }
             }
         }
 
         RubyString result = new RubyString(runtime, getMetaClass(), new ByteList(obytes, false));
 
         if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
         Encoding encoding = value.getEncoding();
         result.value.setEncoding(encoding);
         result.copyCodeRangeForSubstr(this, encoding);
         return result.infectBy(this);
     }
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_8)
     public RubyString reverse_bang(ThreadContext context) {
         if (value.getRealSize() > 1) {
             modify();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
             for (int i = 0; i < len >> 1; i++) {
                 byte b = bytes[p + i];
                 bytes[p + i] = bytes[p + len - i - 1];
                 bytes[p + len - i - 1] = b;
             }
         }
 
         return this;
     }
 
     @JRubyMethod(name = "reverse!", compat = RUBY1_9)
     public RubyString reverse_bang19(ThreadContext context) {
         modifyCheck();
         if (value.getRealSize() > 1) {
             modifyAndKeepCodeRange();
             byte[]bytes = value.getUnsafeBytes();
             int p = value.getBegin();
             int len = value.getRealSize();
 
             Encoding enc = value.getEncoding();
             // this really needs to be inlined here
             if (singleByteOptimizable(enc)) {
                 for (int i = 0; i < len >> 1; i++) {
                     byte b = bytes[p + i];
                     bytes[p + i] = bytes[p + len - i - 1];
                     bytes[p + len - i - 1] = b;
                 }
             } else {
                 int end = p + len;
                 int op = len;
                 byte[]obytes = new byte[len];
                 boolean single = true;
                 while (p < end) {
                     int cl = StringSupport.length(enc, bytes, p, end);
                     if (cl > 1 || (bytes[p] & 0x80) != 0) {
                         single = false;
                         op -= cl;
                         System.arraycopy(bytes, p, obytes, op, cl);
                         p += cl;
                     } else {
                         obytes[--op] = bytes[p++];
                     }
                 }
                 value.setUnsafeBytes(obytes);
                 if (getCodeRange() == CR_UNKNOWN) setCodeRange(single ? CR_7BIT : CR_VALID);
             }
         }
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newStringShared(recv.getRuntime(), ByteList.EMPTY_BYTELIST);
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     @Override
     public IRubyObject initialize(ThreadContext context) {
         return this;
     }
 
     @JRubyMethod(visibility = PRIVATE, compat = RUBY1_8)
     public IRubyObject initialize(ThreadContext context, IRubyObject arg0) {
         replace(arg0);
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     @Override
     public IRubyObject initialize19(ThreadContext context) {
         return this;
     }
 
     @JRubyMethod(name = "initialize", visibility = PRIVATE, compat = RUBY1_9)
     public IRubyObject initialize19(ThreadContext context, IRubyObject arg0) {
         replace19(arg0);
         return this;
     }
 
     @JRubyMethod(compat = RUBY1_8)
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         return RubyFixnum.newFixnum(context.getRuntime(), value.caseInsensitiveCmp(other.convertToString().value));
     }
 
     @JRubyMethod(name = "casecmp", compat = RUBY1_9)
     public IRubyObject casecmp19(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.getRuntime();
         RubyString otherStr = other.convertToString();
         Encoding enc = isCompatibleWith(otherStr);
         if (enc == null) return runtime.getNil();
 
         if (singleByteOptimizable() && otherStr.singleByteOptimizable()) {
             return RubyFixnum.newFixnum(runtime, value.caseInsensitiveCmp(otherStr.value));
         } else {
             return multiByteCasecmp(runtime, enc, value, otherStr.value);
         }
     }
 
     private IRubyObject multiByteCasecmp(Ruby runtime, Encoding enc, ByteList value, ByteList otherValue) {
         byte[]bytes = value.getUnsafeBytes();
         int p = value.getBegin();
         int end = p + value.getRealSize();
 
diff --git a/src/org/jruby/util/io/EncodingOption.java b/src/org/jruby/util/io/EncodingOption.java
index 7d97978d04..78e7a42249 100644
--- a/src/org/jruby/util/io/EncodingOption.java
+++ b/src/org/jruby/util/io/EncodingOption.java
@@ -1,152 +1,168 @@
 package org.jruby.util.io;
 
 import org.jcodings.Encoding;
 import org.jcodings.specific.ASCIIEncoding;
 import org.jruby.Ruby;
 import org.jruby.RubyHash;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.encoding.EncodingService;
 
 public class EncodingOption {
     private Encoding externalEncoding;
     private Encoding internalEncoding;
     private boolean bom;
 
     public EncodingOption(Encoding externalEncoding, Encoding internalEncoding, boolean bom) {
         this.externalEncoding = externalEncoding;
         this.internalEncoding = internalEncoding;
         this.bom = bom;
     }
 
     public Encoding getExternalEncoding() {
         return externalEncoding;
     }
 
     public Encoding getInternalEncoding() {
         return internalEncoding;
     }
 
     public boolean hasBom() {
         return bom;
     }
 
     // c: rb_io_extract_encoding_option
     public static EncodingOption getEncodingOptionFromObject(IRubyObject options) {
         if (options == null || options.isNil() || !(options instanceof RubyHash)) return null;
 
         RubyHash opts = (RubyHash) options;
 
         Ruby runtime = options.getRuntime();
         IRubyObject encOption = opts.fastARef(runtime.newSymbol("encoding"));
         IRubyObject extOption = opts.fastARef(runtime.newSymbol("external_encoding"));
         IRubyObject intOption = opts.fastARef(runtime.newSymbol("internal_encoding"));
         if (encOption != null && !encOption.isNil()) {
             if (extOption != null) {
                 runtime.getWarnings().warn(
                         "Ignoring encoding parameter '" + encOption
                                 + "': external_encoding is used");
                 encOption = runtime.getNil();
             } else if (intOption != null) {
                 runtime.getWarnings().warn(
                         "Ignoring encoding parameter '" + encOption
                                 + "': internal_encoding is used");
                 encOption = runtime.getNil();
             } else {
                 IRubyObject tmp = encOption.checkStringType19();
                 if (!tmp.isNil()) {
                     return getEncodingOptionFromString(runtime, tmp.convertToString().toString());
                 }
                 return createEncodingOption(runtime, runtime.getEncodingService()
                         .getEncodingFromObject(encOption), null, false);
             }
         }
         boolean set = false;
         Encoding extEncoding = null;
         Encoding intEncoding = null;
 
         if (extOption != null) {
             set = true;
             if (!extOption.isNil()) {
                 extEncoding = runtime.getEncodingService().getEncodingFromObject(extOption);
             }
         }
         if (intOption != null) {
             set = true;
             if (intOption.isNil()) {
                 // null;
             } else if (intOption.asString().toString().equals("-")) {
                 // null;
             } else {
                 intEncoding = runtime.getEncodingService().getEncodingFromObject(intOption);
             }
         }
         if (!set)
             return null;
 
         return createEncodingOption(runtime, extEncoding, intEncoding, false);
     }
 
+    // c: rb_io_ext_int_to_encs
     private static EncodingOption createEncodingOption(Ruby runtime, Encoding extEncoding,
                                                        Encoding intEncoding, boolean isBom) {
+        boolean defaultExt = false;
         if (extEncoding == null) {
             extEncoding = runtime.getDefaultExternalEncoding();
+            defaultExt = true;
         }
-        if (intEncoding == null) {
+        if (intEncoding == null && extEncoding != ASCIIEncoding.INSTANCE) {
+            /* If external is ASCII-8BIT, no default transcoding */
             intEncoding = runtime.getDefaultInternalEncoding();
         }
-        // NOTE: This logic used to do checks for int == ext, etc, like in rb_io_ext_int_to_encs,
-        // but that logic seems specific to how MRI's IO sets up "enc" and "enc2". We explicitly separate
-        // external and internal, so consumers should decide how to deal with int == ext.
-        return new EncodingOption(extEncoding, intEncoding, isBom);
+        if (intEncoding == null || intEncoding == extEncoding) {
+            /* No internal encoding => use external + no transcoding */
+            return new EncodingOption(
+                    null,
+                    (defaultExt && intEncoding != extEncoding) ? null : extEncoding,
+                    isBom);
+        } else {
+            return new EncodingOption(
+                    extEncoding,
+                    intEncoding,
+                    isBom);
+        }
     }
 
     // c: parse_mode_enc
     public static EncodingOption getEncodingOptionFromString(Ruby runtime, String option) {
         EncodingService service = runtime.getEncodingService();
         Encoding extEncoding = null;
         Encoding intEncoding = null;
         boolean isBom = false;
 
         String[] encs = option.split(":", 2);
 
         if (encs[0].toLowerCase().startsWith("bom|utf-")) {
             isBom = true;
             encs[0] = encs[0].substring(4);
         }
 
         extEncoding = service.getEncodingFromString(encs[0]);
 
         if (encs.length > 1) {
             if (encs[1].equals("-")) {
                 // null;
             } else {
                 intEncoding = service.getEncodingFromString(encs[1]);
             }
         }
 
         if (extEncoding == null) {
             extEncoding = runtime.getDefaultExternalEncoding();
         }
 
         if (intEncoding == null) {
             intEncoding = runtime.getDefaultInternalEncoding();
         }
         // NOTE: This logic used to do checks for int == ext, etc, like in rb_io_ext_int_to_encs,
         // but that logic seems specific to how MRI's IO sets up "enc" and "enc2". We explicitly separate
         // external and internal, so consumers should decide how to deal with int == ext.
         return new EncodingOption(extEncoding, intEncoding, isBom);
     }
 
     // c: parse_mode_enc
     public static EncodingOption getEncodingNoOption(Ruby runtime, ModeFlags modeFlags) {
         if (modeFlags.isBinary()) {
             return new EncodingOption(
                     ASCIIEncoding.INSTANCE,
-                    runtime.getDefaultInternalEncoding(), false);
+                    null, false);
         } else {
             return new EncodingOption(
                     runtime.getDefaultExternalEncoding(),
                     runtime.getDefaultInternalEncoding(), false);
         }
     }
+
+    public String toString() {
+        return "EncodingOption(int:" + internalEncoding + ", ext:" + externalEncoding + ", bom:" + bom + ")";
+    }
 }
diff --git a/test/externals/ruby1.9/excludes/TestIO_M17N.rb b/test/externals/ruby1.9/excludes/TestIO_M17N.rb
index 601989031d..5b00d8ef09 100644
--- a/test/externals/ruby1.9/excludes/TestIO_M17N.rb
+++ b/test/externals/ruby1.9/excludes/TestIO_M17N.rb
@@ -1,134 +1,88 @@
-exclude :test_binary, "needs investigation"
 exclude :test_binmode, "needs investigation"
 exclude :test_binmode2, "needs investigation"
 exclude :test_binmode3, "needs investigation"
 exclude :test_binmode_paragraph_nonasciicompat, "needs investigation"
 exclude :test_binmode_write_ascii_incompat_internal, "needs investigation"
 exclude :test_both_textmode_binmode, "needs investigation"
 exclude :test_cbuf, "needs investigation"
-exclude :test_cbuf_select, "needs investigation"
 exclude :test_cr_decorator_on_stdout, "needs investigation"
 exclude :test_crlf_decorator_on_stdout, "needs investigation"
 exclude :test_dup, "needs investigation"
 exclude :test_dup_undef, "needs investigation"
-exclude :test_file_foreach, "needs investigation"
-exclude :test_getc_ascii_only, "needs investigation"
+exclude :test_error_nonascii, "needs investigation"
 exclude :test_getc_invalid, "needs investigation"
 exclude :test_getc_invalid2, "needs investigation"
 exclude :test_getc_invalid3, "needs investigation"
-exclude :test_getc_newlineconv, "needs investigation"
 exclude :test_getc_newlineconv_invalid, "needs investigation"
 exclude :test_getc_stateful_conversion, "needs investigation"
 exclude :test_gets_invalid, "needs investigation"
 exclude :test_gets_limit, "needs investigation"
-exclude :test_gets_nil, "needs investigation"
+exclude :test_inspect_nonascii, "needs investigation"
 exclude :test_invalid_r, "needs investigation"
 exclude :test_invalid_w, "needs investigation"
-exclude :test_io_new_enc, "needs investigation"
 exclude :test_lf_decorator_on_stdout, "needs investigation"
-exclude :test_marshal, "needs investigation"
 exclude :test_nonascii_terminator, "needs investigation"
-exclude :test_open_ascii, "needs investigation"
-exclude :test_open_nonascii, "needs investigation"
 exclude :test_open_pipe_r_enc, "needs investigation"
 exclude :test_open_pipe_r_enc2, "needs investigation"
-exclude :test_open_r, "needs investigation"
-exclude :test_open_r_enc, "needs investigation"
-exclude :test_open_r_enc_enc, "needs investigation"
-exclude :test_open_r_enc_enc_in_opt, "needs investigation"
-exclude :test_open_r_enc_in_opt, "needs investigation"
-exclude :test_open_r_encname_encname, "needs investigation"
-exclude :test_open_r_encname_encname_in_opt, "needs investigation"
 exclude :test_open_r_encname_in_opt, "needs investigation"
 exclude :test_open_r_ext_enc_in_opt, "needs investigation"
 exclude :test_open_r_ext_encname_in_opt, "needs investigation"
-exclude :test_open_r_externalencname_internalencname_in_opt, "needs investigation"
-exclude :test_open_rb, "needs investigation"
 exclude :test_open_w, "needs investigation"
-exclude :test_open_w_enc, "needs investigation"
-exclude :test_open_w_enc_enc, "needs investigation"
-exclude :test_open_w_enc_enc_in_opt, "needs investigation"
-exclude :test_open_w_enc_enc_in_opt2, "needs investigation"
-exclude :test_open_w_enc_enc_perm, "needs investigation"
-exclude :test_open_w_enc_in_opt, "needs investigation"
 exclude :test_open_w_enc_in_opt2, "needs investigation"
-exclude :test_open_wb, "needs investigation"
 exclude :test_pipe, "needs investigation"
-exclude :test_pipe_conversion, "needs investigation"
-exclude :test_pipe_convert_partial_read, "needs investigation"
-exclude :test_pipe_terminator_conversion, "needs investigation"
-exclude :test_popen_r_enc, "needs investigation"
 exclude :test_popen_r_enc_enc, "needs investigation"
 exclude :test_popen_r_enc_enc_in_opt, "needs investigation"
 exclude :test_popen_r_enc_enc_in_opt2, "needs investigation"
 exclude :test_popen_r_enc_in_opt, "needs investigation"
 exclude :test_popen_r_enc_in_opt2, "needs investigation"
 exclude :test_popenv_r_enc_enc_in_opt2, "needs investigation"
 exclude :test_puts_widechar, "needs investigation"
 exclude :test_read_all, "needs investigation"
 exclude :test_read_all_invalid, "needs investigation"
-exclude :test_read_encoding, "needs investigation"
 exclude :test_read_mode, "needs investigation"
 exclude :test_read_newline_conversion_error, "needs investigation"
 exclude :test_read_newline_conversion_with_encoding_conversion, "needs investigation"
-exclude :test_read_newline_conversion_without_encoding_conversion, "needs investigation"
 exclude :test_read_stateful, "needs investigation"
-exclude :test_s_foreach_enc, "needs investigation"
-exclude :test_s_foreach_enc_enc, "needs investigation"
-exclude :test_s_foreach_enc_enc_in_opt, "needs investigation"
-exclude :test_s_foreach_enc_enc_in_opt2, "needs investigation"
-exclude :test_s_foreach_enc_in_opt, "needs investigation"
 exclude :test_s_foreach_enc_in_opt2, "needs investigation"
 exclude :test_s_foreach_open_args_enc, "needs investigation"
 exclude :test_s_foreach_open_args_enc_enc, "needs investigation"
 exclude :test_s_foreach_open_args_enc_enc_in_opt, "needs investigation"
 exclude :test_s_foreach_open_args_enc_enc_in_opt2, "needs investigation"
 exclude :test_s_foreach_open_args_enc_in_opt, "needs investigation"
 exclude :test_s_foreach_open_args_enc_in_opt2, "needs investigation"
 exclude :test_s_pipe_invalid, "needs investigation"
 exclude :test_s_pipe_undef, "needs investigation"
 exclude :test_s_pipe_undef_replace_string, "needs investigation"
-exclude :test_set_encoding, "needs investigation"
-exclude :test_set_encoding2, "needs investigation"
 exclude :test_set_encoding_binmode, "needs investigation"
 exclude :test_set_encoding_enc, "needs investigation"
 exclude :test_set_encoding_invalid, "needs investigation"
-exclude :test_set_encoding_nil, "needs investigation"
 exclude :test_set_encoding_undef, "needs investigation"
 exclude :test_set_encoding_undef_replace, "needs investigation"
 exclude :test_stdin, "needs investigation"
 exclude :test_stdin_external_encoding_with_reopen, "needs investigation"
-exclude :test_strip_bom, "needs investigation"
-exclude :test_terminator_conversion, "needs investigation"
 exclude :test_terminator_conversion2, "needs investigation"
 exclude :test_terminator_stateful_conversion, "needs investigation"
 exclude :test_text_mode, "needs investigation"
 exclude :test_text_mode_ungetc_eof, "needs investigation"
 exclude :test_textmode_decode_universal_newline_getc, "needs investigation"
 exclude :test_textmode_decode_universal_newline_gets, "needs investigation"
 exclude :test_textmode_decode_universal_newline_read, "needs investigation"
 exclude :test_textmode_decode_universal_newline_utf16, "needs investigation"
-exclude :test_textmode_encode_newline, "needs investigation"
 exclude :test_textmode_encode_newline_enc, "needs investigation"
 exclude :test_textmode_paragraph_binaryread, "needs investigation"
 exclude :test_textmode_paragraph_nonasciicompat, "needs investigation"
 exclude :test_textmode_paragraphmode, "needs investigation"
 exclude :test_undef_r, "needs investigation"
 exclude :test_undef_w_stateful, "needs investigation"
 exclude :test_undef_w_stateless, "needs investigation"
 exclude :test_ungetc_int, "needs investigation"
 exclude :test_ungetc_stateful_conversion, "needs investigation"
 exclude :test_ungetc_stateful_conversion2, "needs investigation"
 exclude :test_ungetc_str, "needs investigation"
 exclude :test_w_xml_attr, "needs investigation"
-exclude :test_write_ascii_incompat, "needs investigation"
 exclude :test_write_conversion, "needs investigation"
 exclude :test_write_conversion_anyenc_stateful, "needs investigation"
 exclude :test_write_conversion_anyenc_stateful_nosync, "needs investigation"
 exclude :test_write_conversion_anyenc_stateless, "needs investigation"
 exclude :test_write_conversion_fixenc, "needs investigation"
 exclude :test_write_mode, "needs investigation"
-exclude :test_write_mode_fail, "needs investigation"
-exclude :test_write_noenc, "needs investigation"
-exclude :test_error_nonascii, "needs investigation"
-exclude :test_inspect_nonascii, "needs investigation"
