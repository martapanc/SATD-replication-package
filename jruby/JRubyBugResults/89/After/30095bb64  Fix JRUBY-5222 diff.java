diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 9a9dbe9c2d..049e4c3cb3 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,1521 +1,1532 @@
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
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
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
 
-            OpenFile originalFile = ios.getOpenFileChecked();
+            OpenFile origFile = ios.getOpenFileChecked();
             OpenFile selfFile = getOpenFileChecked();
 
             long pos = 0;
-            if (originalFile.isReadable()) {
-                pos = originalFile.getMainStreamSafe().fgetpos();
+            Stream origStream = origFile.getMainStreamSafe();
+            ChannelDescriptor origDescriptor = origStream.getDescriptor();
+            boolean origIsSeekable = origDescriptor.isSeekable();
+
+            if (origFile.isReadable() && origIsSeekable) {
+                pos = origFile.getMainStreamSafe().fgetpos();
             }
 
-            if (originalFile.getPipeStream() != null) {
-                originalFile.getPipeStream().fflush();
-            } else if (originalFile.isWritable()) {
-                originalFile.getMainStreamSafe().fflush();
+            if (origFile.getPipeStream() != null) {
+                origFile.getPipeStream().fflush();
+            } else if (origFile.isWritable()) {
+                origStream.fflush();
             }
 
             if (selfFile.isWritable()) {
                 selfFile.getWriteStreamSafe().fflush();
             }
 
-            selfFile.setMode(originalFile.getMode());
-            selfFile.setProcess(originalFile.getProcess());
-            selfFile.setLineNumber(originalFile.getLineNumber());
-            selfFile.setPath(originalFile.getPath());
-            selfFile.setFinalizer(originalFile.getFinalizer());
+            selfFile.setMode(origFile.getMode());
+            selfFile.setProcess(origFile.getProcess());
+            selfFile.setLineNumber(origFile.getLineNumber());
+            selfFile.setPath(origFile.getPath());
+            selfFile.setFinalizer(origFile.getFinalizer());
 
+            Stream selfStream = selfFile.getMainStreamSafe();
             ChannelDescriptor selfDescriptor = selfFile.getMainStreamSafe().getDescriptor();
-            ChannelDescriptor originalDescriptor = originalFile.getMainStreamSafe().getDescriptor();
+            boolean selfIsSeekable = selfDescriptor.isSeekable();
 
             // confirm we're not reopening self's channel
-            if (selfDescriptor.getChannel() != originalDescriptor.getChannel()) {
+            if (selfDescriptor.getChannel() != origDescriptor.getChannel()) {
                 // check if we're a stdio IO, and ensure we're not badly mutilated
                 if (runtime.getFileno(selfDescriptor) >= 0 && runtime.getFileno(selfDescriptor) <= 2) {
                     selfFile.getMainStreamSafe().clearerr();
 
                     // dup2 new fd into self to preserve fileno and references to it
-                    originalDescriptor.dup2Into(selfDescriptor);
+                    origDescriptor.dup2Into(selfDescriptor);
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
-                        selfFile.setMainStream(ChannelStream.fdopen(runtime, originalDescriptor, originalDescriptor.getOriginalModes()));
+                        selfFile.setMainStream(ChannelStream.fdopen(runtime, origDescriptor, origDescriptor.getOriginalModes()));
                         selfFile.setPipeStream(pipeFile);
                     } else {
                         // only use internal fileno here, stdio is handled above
                         selfFile.setMainStream(
                                 ChannelStream.open(
                                 runtime,
-                                originalDescriptor.dup2(selfDescriptor.getFileno())));
+                                origDescriptor.dup2(selfDescriptor.getFileno())));
 
                         // since we're not actually duping the incoming channel into our handler, we need to
                         // copy the original sync behavior from the other handler
                         selfFile.getMainStreamSafe().setSync(selfFile.getMainStreamSafe().isSync());
                     }
                     selfFile.setMode(mode);
                 }
 
                 // TODO: anything threads attached to original fd are notified of the close...
                 // see rb_thread_fd_close
 
-                if (originalFile.isReadable() && pos >= 0) {
-                    selfFile.seek(pos, Stream.SEEK_SET);
-                    originalFile.seek(pos, Stream.SEEK_SET);
+                if (origFile.isReadable() && pos >= 0) {
+                    if (selfIsSeekable) {
+                        selfFile.seek(pos, Stream.SEEK_SET);
+                    }
+
+                    if (origIsSeekable) {
+                        origFile.seek(pos, Stream.SEEK_SET);
+                    }
                 }
             }
 
             // only use internal fileno here, stdio is handled above
             if (selfFile.getPipeStream() != null && selfDescriptor.getFileno() != selfFile.getPipeStream().getDescriptor().getFileno()) {
                 int fd = selfFile.getPipeStream().getDescriptor().getFileno();
 
-                if (originalFile.getPipeStream() == null) {
+                if (origFile.getPipeStream() == null) {
                     selfFile.getPipeStream().fclose();
                     selfFile.setPipeStream(null);
-                } else if (fd != originalFile.getPipeStream().getDescriptor().getFileno()) {
+                } else if (fd != origFile.getPipeStream().getDescriptor().getFileno()) {
                     selfFile.getPipeStream().fclose();
-                    ChannelDescriptor newFD2 = originalFile.getPipeStream().getDescriptor().dup2(fd);
+                    ChannelDescriptor newFD2 = origFile.getPipeStream().getDescriptor().dup2(fd);
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
+            ex.printStackTrace();
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
         
