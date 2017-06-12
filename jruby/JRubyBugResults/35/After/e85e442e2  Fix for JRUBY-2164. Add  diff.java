diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index d7e607f2ae..a2f060d826 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,2004 +1,2101 @@
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
 
 import static java.lang.System.out;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import org.jruby.util.io.FileExistsException;
 import org.jruby.util.io.STDIO;
 import org.jruby.util.io.OpenFile;
 import org.jruby.util.io.ChannelDescriptor;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.lang.ref.Reference;
 import java.lang.ref.WeakReference;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.Pipe;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.io.Stream;
 import org.jruby.util.io.ModeFlags;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.io.BadDescriptorException;
 import org.jruby.util.io.ChannelStream;
 import org.jruby.util.io.InvalidValueException;
 import org.jruby.util.io.PipeException;
 
 /**
  * 
  * @author jpetersen
  */
 public class RubyIO extends RubyObject {
     protected OpenFile openFile;
     
     public void registerDescriptor(ChannelDescriptor descriptor) {
         getRuntime().getDescriptors().put(new Integer(descriptor.getFileno()), new WeakReference<ChannelDescriptor>(descriptor));
     }
     
     public void unregisterDescriptor(int aFileno) {
         getRuntime().getDescriptors().remove(new Integer(aFileno));
     }
     
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         Reference<ChannelDescriptor> reference = getRuntime().getDescriptors().get(new Integer(aFileno));
         if (reference == null) {
             return null;
         }
         return (ChannelDescriptor) reference.get();
     }
     
     // FIXME can't use static; would interfere with other runtimes in the same JVM
     protected static int filenoIndex = 2;
     
     public static int getNewFileno() {
         filenoIndex++;
         
         return filenoIndex;
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
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(outputStream), getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(inputStream), getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(OpenFile.READABLE);
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
             openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(channel, getNewFileno(), new FileDescriptor())));
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         
         registerDescriptor(openFile.getMainStream().getDescriptor());
     }
 
     public RubyIO(Ruby runtime, Process process, ModeFlags modes) {
     	super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         
         openFile.setMode(modes.getOpenFileFlags() | OpenFile.SYNC);
         openFile.setProcess(process);
 
         try {
             InputStream pipeIn = process.getInputStream();
             ChannelDescriptor main = new ChannelDescriptor(
                     Channels.newChannel(pipeIn),
                     getNewFileno(),
                     new FileDescriptor());
             
             OutputStream pipeOut = process.getOutputStream();
             ChannelDescriptor pipe = new ChannelDescriptor(
                     Channels.newChannel(pipeOut),
                     getNewFileno(),
                     new FileDescriptor());
             
             if (!openFile.isReadable()) {
                 main.close();
                 pipeIn.close();
             } else {
                 openFile.setMainStream(new ChannelStream(getRuntime(), main));
             }
             
             if (!openFile.isWritable()) {
                 pipe.close();
                 pipeOut.close();
             } else {
                 if (openFile.getMainStream() != null) {
                     openFile.setPipeStream(new ChannelStream(getRuntime(), pipe));
                 } else {
                     openFile.setMainStream(new ChannelStream(getRuntime(), pipe));
                 }
             }
             
             registerDescriptor(main);
             registerDescriptor(pipe);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         }
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
 
         try {
             switch (stdio) {
             case IN:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             // special constructor that accepts stream, not channel
                             new ChannelDescriptor(runtime.getIn(), 0, new ModeFlags(ModeFlags.RDONLY), FileDescriptor.in),
                             FileDescriptor.in));
                 break;
             case OUT:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             new ChannelDescriptor(Channels.newChannel(runtime.getOut()), 1, new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out),
                             FileDescriptor.out));
                 openFile.getMainStream().setSync(true);
                 break;
             case ERR:
                 openFile.setMainStream(
                         new ChannelStream(
                             runtime, 
                             new ChannelDescriptor(Channels.newChannel(runtime.getErr()), 2, new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out), 
                             FileDescriptor.err));
                 openFile.getMainStream().setSync(true);
                 break;
             }
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         }
         
         openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         
         registerDescriptor(openFile.getMainStream().getDescriptor());        
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
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyIO.class);   
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
         
         ioClass.dispatcher = callbackFactory.createDispatcher(ioClass);
 
         return ioClass;
     }
 
     public OutputStream getOutStream() {
         return getOpenFileChecked().getMainStream().newOutputStream();
     }
 
     public InputStream getInStream() {
         return getOpenFileChecked().getMainStream().newInputStream();
     }
 
     public Channel getChannel() {
         if (getOpenFileChecked().getMainStream() instanceof ChannelStream) {
             return ((ChannelStream) openFile.getMainStream()).getDescriptor().getChannel();
         } else {
             return null;
         }
     }
     
     public Stream getHandler() {
         return getOpenFileChecked().getMainStream();
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(IRubyObject[] args) throws InvalidValueException {
     	if (args.length < 1) {
             throw getRuntime().newArgumentError("wrong number of arguments");
     	}
     	
     	IRubyObject tmp = TypeConverter.convertToTypeWithCheck(args[0],
     	        getRuntime().getIO(), MethodIndex.getIndex("to_io"), "to_io");
     	if (!tmp.isNil()) {
             try {
                 RubyIO ios = (RubyIO) tmp;
 
                 if (ios.openFile == this.openFile) {
                     return this;
                 }
 
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
                     if (selfDescriptor.getFileno() >=0 && selfDescriptor.getFileno() <= 2) {
                         selfFile.getMainStream().clearerr();
                         
                         // dup2 new fd into self to preserve fileno and references to it
                         originalDescriptor.dup2(selfDescriptor);
                         
                         // re-register, since fileno points at something new now
                         registerDescriptor(selfFile.getMainStream().getDescriptor());
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
                             selfFile.setMainStream(ChannelStream.fdopen(getRuntime(), originalDescriptor, new ModeFlags()));
                             selfFile.setPipeStream(pipeFile);
                         } else {
                             selfFile.setMainStream(
                                     new ChannelStream(
                                         getRuntime(),
                                         originalDescriptor.dup2(selfDescriptor.getFileno())));
                             
                             // re-register the descriptor
                             registerDescriptor(selfFile.getMainStream().getDescriptor());
                             
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
 
                 // TODO: more pipe logic
     //            if (fptr->f2 && fd != fileno(fptr->f2)) {
     //                fd = fileno(fptr->f2);
     //                if (!orig->f2) {
     //                    fclose(fptr->f2);
     //                    rb_thread_fd_close(fd);
     //                    fptr->f2 = 0;
     //                }
     //                else if (fd != (fd2 = fileno(orig->f2))) {
     //                    fclose(fptr->f2);
     //                    rb_thread_fd_close(fd);
     //                    if (dup2(fd2, fd) < 0)
     //                        rb_sys_fail(orig->path);
     //                    fptr->f2 = rb_fdopen(fd, "w");
     //                }
     //            }
                 
                 // TODO: restore binary mode
     //            if (fptr->mode & FMODE_BINMODE) {
     //                rb_io_binmode(io);
     //            }
                 
                 // TODO: set our metaclass to target's class (i.e. scary!)
 
             } catch (IOException ex) { // TODO: better error handling
                 throw getRuntime().newIOError("could not reopen: " + ex.getMessage());
             } catch (BadDescriptorException ex) {
                 throw getRuntime().newIOError("could not reopen: " + ex.getMessage());
             } catch (PipeException ex) {
                 throw getRuntime().newIOError("could not reopen: " + ex.getMessage());
             }
         } else {
             IRubyObject pathString = args[0].convertToString();
             
             // TODO: check safe, taint on incoming string
             
             if (openFile == null) {
                 openFile = new OpenFile();
             }
             
             try {
                 ModeFlags modes;
                 if (args.length > 1) {
                     IRubyObject modeString = args[1].convertToString();
                     modes = getIOModes(getRuntime(), modeString.toString());
 
                     openFile.setMode(modes.getOpenFileFlags());
                 } else {
                     modes = getIOModes(getRuntime(), "r");
                 }
 
                 String path = pathString.toString();
                 
                 // Ruby code frequently uses a platform check to choose "NUL:" on windows
                 // but since that check doesn't work well on JRuby, we help it out
                 
                 openFile.setPath(path);
             
                 if (openFile.getMainStream() == null) {
                     try {
                         openFile.setMainStream(ChannelStream.fopen(getRuntime(), path, modes));
                     } catch (FileExistsException fee) {
                         throw getRuntime().newErrnoEEXISTError(path);
                     }
                     
                     registerDescriptor(openFile.getMainStream().getDescriptor());
                     if (openFile.getPipeStream() != null) {
                         openFile.getPipeStream().fclose();
                         unregisterDescriptor(openFile.getPipeStream().getDescriptor().getFileno());
                         openFile.setPipeStream(null);
                     }
                     return this;
                 } else {
                     // TODO: This is an freopen in MRI, this is close, but not quite the same
                     openFile.getMainStream().freopen(path, getIOModes(getRuntime(), openFile.getModeAsString(getRuntime())));
 
                     // re-register
                     registerDescriptor(openFile.getMainStream().getDescriptor());
 
                     if (openFile.getPipeStream() != null) {
                         // TODO: pipe handler to be reopened with path and "w" mode
                     }
                 }
             } catch (PipeException pe) {
                 throw getRuntime().newErrnoEPIPEError();
             } catch (IOException ex) {
                 throw getRuntime().newIOErrorFromException(ex);
             } catch (BadDescriptorException ex) {
                 throw getRuntime().newErrnoEBADFError();
             } catch (InvalidValueException e) {
             	throw getRuntime().newErrnoEINVALError();
             }
         }
         
         // A potentially previously close IO is being 'reopened'.
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
             throw runtime.newArgumentError("illegal access mode " + modes);
         }
 
         for (int n = 1; n < length; n++) {
             switch (modesString.charAt(n)) {
             case 'b':
                 modes |= ModeFlags.BINARY;
                 break;
             case '+':
                 modes = (modes & ~ModeFlags.ACCMODE) | ModeFlags.RDWR;
                 break;
             default:
                 throw runtime.newArgumentError("illegal access mode " + modes);
             }
         }
 
         return modes;
     }
 
     private static ByteList getSeparatorFromArgs(Ruby runtime, IRubyObject[] args, int idx) {
         IRubyObject sepVal;
 
         if (args.length > idx) {
             sepVal = args[idx];
         } else {
             sepVal = runtime.getRecordSeparatorVar().get();
         }
 
         ByteList separator = sepVal.isNil() ? null : sepVal.convertToString().getByteList();
 
         if (separator != null && separator.realSize == 0) {
             separator = Stream.PARAGRAPH_DELIMETER;
         }
 
         return separator;
     }
 
     private ByteList getSeparatorForGets(IRubyObject[] args) {
         return getSeparatorFromArgs(getRuntime(), args, 0);
     }
 
     public IRubyObject getline(ByteList separator) {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
             separator = (separator == Stream.PARAGRAPH_DELIMETER) ?
                     Stream.PARAGRAPH_SEPARATOR : separator;
             
             if (isParagraph) {
                 swallow('\n');
             }
             
             if (separator == null) {
                 IRubyObject str = readAll(null);
                 if (((RubyString)str).getByteList().length() == 0) {
                     return getRuntime().getNil();
                 }
                 incrementLineno(myOpenFile);
                 return str;
             } else if (separator.length() == 1) {
                 return getlineFast(separator.get(0));
             } else {
                 Stream readStream = myOpenFile.getMainStream();
                 int c = -1;
                 int newline = separator.get(separator.length() - 1) & 0xFF;
 
                 ByteList buf = new ByteList(1024);
                 boolean update = false;
                 
                 while (true) {
                     do {
                         readCheck(readStream);
                         readStream.clearerr();
 
                         try {
                             c = readStream.fgetc();
                         } catch (EOFException e) {
                             c = -1;
                         }
 
-                        if (c == -1) {
-                            // TODO: clear error, wait for it to become readable
-                            break;
+                        if(c == -1) {
+                            if (!readStream.isBlocking() && (readStream instanceof ChannelStream)) {
+                                if(!(waitReadable(((ChannelStream)readStream).getDescriptor()))) {
+                                    throw getRuntime().newIOError("bad file descriptor: " + openFile.getPath());
+                                }
+
+                                continue;
+                            } else {
+                                break;
+                            }
                         }
                         
                         buf.append(c);
             
                         update = true;
                     } while (c != newline); // loop until we see the nth separator char
                     
                     // if we hit EOF, we're done
                     if (c == -1) {
                         break;
                     }
                     
                     // if we've found the last char of the separator,
                     // and we've found at least as many characters as separator length,
                     // and the last n characters of our buffer match the separator, we're done
                     if (c == newline && buf.length() >= separator.length() &&
                             0 == ByteList.memcmp(buf.unsafeBytes(), buf.begin + buf.realSize - separator.length(), separator.unsafeBytes(), separator.begin, separator.realSize)) {
                         break;
                     }
                 }
                 
                 if (isParagraph) {
                     if (c != -1) {
                         swallow('\n');
                     }
                 }
                 
                 if (!update) {
                     return getRuntime().getNil();
                 } else {
                     incrementLineno(myOpenFile);
                     RubyString str = RubyString.newString(getRuntime(), buf);
                     str.setTaint(true);
 
                     return str;
                 }
             }
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (EOFException e) {
             return getRuntime().getNil();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     private void incrementLineno(OpenFile myOpenFile) {
         Ruby runtime = getRuntime();
         int lineno = myOpenFile.getLineNumber() + 1;
         myOpenFile.setLineNumber(lineno);
         runtime.getGlobalVariables().set("$.", runtime.newFixnum(lineno));
         // this is for a range check, near as I can tell
         RubyNumeric.int2fix(getRuntime(), myOpenFile.getLineNumber());
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
     
     public IRubyObject getlineFast(int delim) throws IOException, BadDescriptorException {
         Stream readStream = openFile.getMainStream();
         int c = -1;
 
         ByteList buf = new ByteList(1024);
         boolean update = false;
         do {
             readCheck(readStream);
             readStream.clearerr();
 
             try {
                 c = readStream.fgetc();
             } catch (EOFException e) {
                 c = -1;
             }
 
-            if (c == -1) {
-                // TODO: clear error, wait for it to become readable
-                break;
+            if(c == -1) {
+                if (!readStream.isBlocking() && (readStream instanceof ChannelStream)) {
+                    if(!(waitReadable(((ChannelStream)readStream).getDescriptor()))) {
+                        throw getRuntime().newIOError("bad file descriptor: " + openFile.getPath());
+                    }
+                    continue;
+                } else {
+                    break;
+                }
             }
             
             buf.append(c);
             
             update = true;
         } while (c != delim);
 
         if (!update) {
             return getRuntime().getNil();
         } else {
             incrementLineno(openFile);
             RubyString str = RubyString.newString(getRuntime(), buf);
             str.setTaint(true);
-
             return str;
         }
     }
     // IO class methods.
 
     @JRubyMethod(name = {"new", "for_fd"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass)recv;
         
         if (block.isGiven()) {
             String className = klass.getName();
             recv.getRuntime().getWarnings().warn(
                     ID.BLOCK_NOT_ACCEPTED,
                     className + "::new() does not take block; use " + className + "::open() instead",
                     className + "::open()");
         }
         
         return klass.newInstance(args, block);
     }
 
     @JRubyMethod(name = "initialize", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int argCount = args.length;
         ModeFlags modes;
         
         int fileno = RubyNumeric.fix2int(args[0]);
         
         try {
             ChannelDescriptor descriptor = getDescriptorByFileno(fileno);
             
             if (descriptor == null) {
                 throw getRuntime().newErrnoEBADFError();
             }
             
             descriptor.checkOpen();
             
             if (argCount == 2) {
                 if (args[1] instanceof RubyFixnum) {
                     modes = new ModeFlags(RubyFixnum.fix2long(args[1]));
                 } else {
                     modes = getIOModes(getRuntime(), (args[1].convertToString().toString()));
                 }
             } else {
                 // use original modes
                 modes = descriptor.getOriginalModes();
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
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyClass klass = (RubyClass)recv;
         
         RubyIO io = (RubyIO)klass.newInstance(args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(runtime.getCurrentContext(), io);
             } finally {
                 try {
                     io.getMetaClass().invoke(runtime.getCurrentContext(), io, "close", IRubyObject.NULL_ARRAY, CallType.FUNCTIONAL, Block.NULL_BLOCK);
                 } catch (RaiseException re) {
                     RubyException rubyEx = re.getException();
                     if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
                         // MRI behavior: swallow StandardErorrs
                     } else {
                         throw re;
                     }
                 }
             }
         }
 
         return io;
     }
 
     // This appears to be some windows-only mode.  On a java platform this is a no-op
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
             return this;
     }
     
     protected void checkInitialized() {
         if (openFile == null) {
             throw getRuntime().newIOError("uninitialized stream");
         }
     }
     
     protected void checkClosed() {
         if (openFile.getMainStream() == null && openFile.getPipeStream() == null) {
             throw getRuntime().newIOError("closed stream");
         }
     }
     
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(IRubyObject obj) {
         try {
             RubyString string = obj.asString();
 
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(getRuntime());
             
             Stream writeStream = myOpenFile.getWriteStream();
             
             if (myOpenFile.isWriteBuffered()) {
                 getRuntime().getWarnings().warn(
                         ID.SYSWRITE_BUFFERED_IO,
                         "syswrite for buffered IO");
             }
             
             if (!writeStream.getDescriptor().isWritable()) {
                 myOpenFile.checkClosed(getRuntime());
             }
             
             int read = writeStream.getDescriptor().write(string.getByteList());
             
             if (read == -1) {
                 // TODO? I think this ends up propagating from normal Java exceptions
                 // sys_fail(openFile.getPath())
             }
             
             return getRuntime().newFixnum(read);
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             e.printStackTrace();
             throw getRuntime().newSystemCallError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "write_nonblock", required = 1)
     public IRubyObject write_nonblock(IRubyObject obj) {
         // TODO: Obviously, we're not doing a non-blocking write here
         return write(obj);
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(IRubyObject obj) {
         getRuntime().secure(4);
         
         RubyString str = obj.asString();
 
         // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
         
         if (str.getByteList().length() == 0) {
             return getRuntime().newFixnum(0);
         }
 
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(getRuntime());
 
             int written = fwrite(str.getByteList());
 
             if (written == -1) {
                 // TODO: sys fail
             }
 
             // if not sync, we switch to write buffered mode
             if (!myOpenFile.isSync()) {
                 myOpenFile.setWriteBuffered();
             }
 
             return getRuntime().newFixnum(written);
         } catch (IOException ex) {
             throw getRuntime().newIOErrorFromException(ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         }
     }
+
+    protected boolean waitWritable(ChannelDescriptor descriptor) throws IOException {
+        Channel channel = descriptor.getChannel();
+        if (channel == null || !(channel instanceof SelectableChannel)) {
+            return false;
+        }
+       
+        Selector selector = Selector.open();
+
+        ((SelectableChannel) channel).configureBlocking(false);
+        int real_ops = ((SelectableChannel) channel).validOps() & SelectionKey.OP_WRITE;
+        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
+       
+        if (key == null) {
+            ((SelectableChannel) channel).register(selector, real_ops, descriptor);
+        } else {
+            key.interestOps(key.interestOps()|real_ops);
+        }
+
+        while(selector.select() == 0);
+
+        for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
+            SelectionKey skey = (SelectionKey) i.next();
+            if ((skey.interestOps() & skey.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
+                if(skey.attachment() == descriptor) {
+                    return true;
+                }
+            }
+        }
+        return false;
+    }
+
+    protected boolean waitReadable(ChannelDescriptor descriptor) throws IOException {
+        Channel channel = descriptor.getChannel();
+        if (channel == null || !(channel instanceof SelectableChannel)) {
+            return false;
+        }
+       
+        Selector selector = Selector.open();
+
+        ((SelectableChannel) channel).configureBlocking(false);
+        int real_ops = ((SelectableChannel) channel).validOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
+        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
+       
+        if (key == null) {
+            ((SelectableChannel) channel).register(selector, real_ops, descriptor);
+        } else {
+            key.interestOps(key.interestOps()|real_ops);
+        }
+
+        while(selector.select() == 0);
+
+        for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
+            SelectionKey skey = (SelectionKey) i.next();
+            if ((skey.interestOps() & skey.readyOps() & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0) {
+                if(skey.attachment() == descriptor) {
+                    return true;
+                }
+            }
+        }
+        return false;
+    }
     
     protected int fwrite(ByteList buffer) {
         int n, r, l, offset = 0;
+        boolean eagain = false;
         Stream writeStream = openFile.getWriteStream();
 
         int len = buffer.length();
         
-//        if ((n = len) <= 0) return n;
-        if (len == 0) return 0;
-        
+        if ((n = len) <= 0) return n;
+
         try {
             if (openFile.isSync()) {
                 openFile.fflush(writeStream);
 
                 // TODO: why is this guarded?
     //            if (!rb_thread_fd_writable(fileno(f))) {
     //                rb_io_check_closed(fptr);
     //            }
-                // TODO: loop until it's all written
-                //while (offset < len) {
-                    writeStream.getDescriptor().write(buffer);
-                //}
-                return len;
+               
+                while(offset<len) {
+                    l = n;
+
+                    // TODO: Something about pipe buffer length here
+
+                    r = writeStream.getDescriptor().write(buffer,offset,l);
+
+                    if(r == len) {
+                        return len; //Everything written
+                    }
+
+                    if (0 <= r) {
+                        offset += r;
+                        n -= r;
+                        eagain = true;
+                    }
+
+                    if(eagain && waitWritable(writeStream.getDescriptor())) {
+                        openFile.checkClosed(getRuntime());
+                        if(offset >= buffer.length()) {
+                            return -1;
+                        }
+                    } else {
+                        return -1;
+                    }
+                }
+
 
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
     public IRubyObject op_append(IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
         callMethod(getRuntime().getCurrentContext(), "write", anObject);
         
         return this; 
     }
 
     @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno() {
         return getRuntime().newFixnum(getOpenFileChecked().getMainStream().getDescriptor().getFileno());
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno() {
         return getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(IRubyObject newLineNumber) {
         getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync() {
         return getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
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
     public IRubyObject pid() {
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (myOpenFile.getProcess() == null) {
             return getRuntime().getNil();
         }
         
         // Of course this isn't particularly useful.
         int pid = myOpenFile.getProcess().hashCode();
         
         return getRuntime().newFixnum(pid); 
     }
     
     /**
      * @deprecated
      * @return
      */
     public boolean writeDataBuffered() {
         return openFile.getMainStream().writeDataBuffered();
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos() {
         try {
             return getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException bde) {
             throw getRuntime().newErrnoEBADFError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(IRubyObject newPosition) {
         long offset = RubyNumeric.num2long(newPosition);
 
         if (offset < 0) {
             throw getRuntime().newSystemCallError("Negative seek offset");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.getMainStream().fseek(offset, Stream.SEEK_SET);
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return (RubyFixnum) newPosition;
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true)
     public IRubyObject print(IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getCurrentContext().getCurrentFrame().getLastLine() };
         }
 
         IRubyObject fs = getRuntime().getGlobalVariables().get("$,");
         IRubyObject rs = getRuntime().getGlobalVariables().get("$\\");
         ThreadContext context = getRuntime().getCurrentContext();
         
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 callMethod(context, "write", getRuntime().newString("nil"));
             } else {
                 callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             callMethod(context, "write", rs);
         }
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(IRubyObject[] args) {
         callMethod(getRuntime().getCurrentContext(), "write", RubyKernel.sprintf(this, args));
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "putc", required = 1)
     public IRubyObject putc(IRubyObject object) {
         int c;
         
         if (getRuntime().getString().isInstance(object)) {
             String value = ((RubyString) object).toString();
             
             if (value.length() > 0) {
                 c = value.charAt(0);
             } else {
                 throw getRuntime().newTypeError("Cannot convert String to Integer");
             }
         } else if (getRuntime().getFixnum().isInstance(object)){
             c = RubyNumeric.fix2int(object);
         } else { // What case will this work for?
             c = RubyNumeric.fix2int(object.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_I, "to_i"));
         }
 
         try {
             getOpenFileChecked().getMainStream().fputc(c);
         } catch (BadDescriptorException e) {
             return RubyFixnum.zero(getRuntime());
         } catch (IOException e) {
             return RubyFixnum.zero(getRuntime());
         }
         
         return object;
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "seek", required = 1, optional = 1)
     public RubyFixnum seek(IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             myOpenFile.seek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return RubyFixnum.zero(getRuntime());
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "sysseek", required = 1, optional = 1)
     public RubyFixnum sysseek(IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
         long pos;
         int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
             whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         try {
             
             if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
                 throw getRuntime().newIOError("sysseek for buffered IO");
             }
             if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
                 getRuntime().getWarnings().warn(
                         ID.SYSSEEK_BUFFERED_IO,
                         "sysseek for buffered IO");
             }
             
             pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         myOpenFile.getMainStream().clearerr();
         
         return getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind() {
         OpenFile myOpenfile = getOpenFileChecked();
         
         try {
             myOpenfile.getMainStream().fseek(0L, Stream.SEEK_SET);
             myOpenfile.getMainStream().clearerr();
             
             // TODO: This is some goofy global file value from MRI..what to do?
 //            if (io == current_file) {
 //                gets_lineno -= fptr->lineno;
 //            }
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         // Must be back on first line on rewind.
         myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkWritable(getRuntime());
         
             myOpenFile.getWriteStream().sync();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
 
         return RubyFixnum.zero(getRuntime());
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
     public RubyBoolean eof_p() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             
             if (myOpenFile.getMainStream().feof()) {
                 return getRuntime().getTrue();
             }
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 return getRuntime().getFalse();
             }
             
             readCheck(myOpenFile.getMainStream());
             
             myOpenFile.getMainStream().clearerr();
             
             int c = myOpenFile.getMainStream().fgetc();
             
             if (c != -1) {
                 myOpenFile.getMainStream().ungetc(c);
                 return getRuntime().getFalse();
             }
             
             myOpenFile.checkClosed(getRuntime());
             
             myOpenFile.getMainStream().clearerr();
             
             return getRuntime().getTrue();
         } catch (PipeException ex) {
             throw getRuntime().newErrnoEPIPEError();
         } catch (InvalidValueException ex) {
             throw getRuntime().newErrnoEINVALError();
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p() {
         return getRuntime().newBoolean(getRuntime().getPosix().isatty(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor()));
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         if (this == original) return this;
 
         RubyIO originalIO = (RubyIO) TypeConverter.convertToTypeWithCheck(original, getRuntime().getIO(), MethodIndex.TO_IO, "to_io");
         
         OpenFile originalFile = originalIO.getOpenFileChecked();
         OpenFile newFile = openFile;
         
         try {
             // TODO: I didn't see where MRI has this check, but it seems to be the right place
             originalFile.checkClosed(getRuntime());
             
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
                 originalFile.getMainStream().fseek(0, Stream.SEEK_CUR);
             } else if (originalFile.isWritable()) {
                 originalFile.getMainStream().fflush();
             } else {
                 originalFile.getMainStream().fseek(0, Stream.SEEK_CUR);
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
 
             newFile.setMainStream(ChannelStream.fdopen(getRuntime(), descriptor, modes));
             
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
             
             // Register the new descriptor
             registerDescriptor(newFile.getMainStream().getDescriptor());
         } catch (IOException ex) {
             throw getRuntime().newIOError("could not init copy: " + ex);
         } catch (BadDescriptorException ex) {
             throw getRuntime().newIOError("could not init copy: " + ex);
         } catch (PipeException ex) {
             throw getRuntime().newIOError("could not init copy: " + ex);
         } catch (InvalidValueException ex) {
             throw getRuntime().newIOError("could not init copy: " + ex);
         }
         
         return this;
     }
     
     /** Closes the IO.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p() {
         if (openFile.getMainStream() == null && openFile.getPipeStream() == null) {
             return getRuntime().getTrue();
         } else {
             return getRuntime().getFalse();
         }
     }
 
     /** 
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't close");
         }
         
         openFile.checkClosed(getRuntime());
         return close2();
     }
         
     protected IRubyObject close2() {
         if (openFile == null) {
             return getRuntime().getNil();
         }
         
         // These would be used when we notify threads...if we notify threads
         ChannelDescriptor main, pipe;
         if (openFile.getPipeStream() != null) {
             pipe = openFile.getPipeStream().getDescriptor();
         } else {
             if (openFile.getMainStream() == null) {
                 return getRuntime().getNil();
             }
             pipe = null;
         }
         
         main = openFile.getMainStream().getDescriptor();
         
         // cleanup, raising errors if any
         openFile.cleanup(getRuntime(), true);
         
         // TODO: notify threads waiting on descriptors/IO? probably not...
         
         if (openFile.getProcess() != null) {
             try {
                 IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(getRuntime(), openFile.getProcess().waitFor());
                 getRuntime().getGlobalVariables().set("$?", processResult);
             } catch (InterruptedException ie) {
                 // TODO: do something here?
             }
         }
         
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write() throws BadDescriptorException {
         try {
             if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw getRuntime().newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isReadable()) {
                 throw getRuntime().newIOError("closing non-duplex IO for writing");
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
         } catch (IOException ioe) {
             // hmmmm
         }
         return this;
     }
 
     @JRubyMethod(name = "close_read")
     public IRubyObject close_read() throws BadDescriptorException {
         try {
             if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw getRuntime().newSecurityError("Insecure: can't close");
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             if (myOpenFile.getPipeStream() == null && myOpenFile.isWritable()) {
                 throw getRuntime().newIOError("closing non-duplex IO for reading");
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
         } catch (IOException ioe) {
             // I believe Ruby bails out with a "bug" if closing fails
             throw getRuntime().newIOErrorFromException(ioe);
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
     @JRubyMethod(name = "gets", optional = 1)
     public IRubyObject gets(IRubyObject[] args) {
         ByteList separator = getSeparatorForGets(args);
         
         IRubyObject result = getline(separator);
 
         if (!result.isNil()) getRuntime().getCurrentContext().getCurrentFrame().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
         return ((ChannelStream) openFile.getMainStream()).isBlocking();
     }
 
     @JRubyMethod(name = "fcntl", required = 2)
     public IRubyObject fcntl(IRubyObject cmd, IRubyObject arg) {
         // TODO: This version differs from ioctl by checking whether fcntl exists
         // and raising notimplemented if it doesn't; perhaps no difference for us?
         return ctl(cmd, arg);
     }
 
     @JRubyMethod(name = "ioctl", required = 1, optional = 1)
     public IRubyObject ioctl(IRubyObject[] args) {
         IRubyObject cmd = args[0];
         IRubyObject arg;
         
         if (args.length == 2) {
             arg = args[1];
         } else {
             arg = getRuntime().getNil();
         }
         
         return ctl(cmd, arg);
     }
 
     public IRubyObject ctl(IRubyObject cmd, IRubyObject arg) {
         long realCmd = cmd.convertToInteger().getLongValue();
         long nArg = 0;
         
         // FIXME: Arg may also be true, false, and nil and still be valid.  Strangely enough, 
         // protocol conversion is not happening in Ruby on this arg?
         if (arg.isNil() || arg == getRuntime().getFalse()) {
             nArg = 0;
         } else if (arg instanceof RubyFixnum) {
             nArg = RubyFixnum.fix2long(arg);
         } else if (arg == getRuntime().getTrue()) {
             nArg = 1;
         } else {
             throw getRuntime().newNotImplementedError("JRuby does not support string for second fcntl/ioctl argument yet");
         }
         
         OpenFile myOpenFile = getOpenFileChecked();
 
         // Fixme: Only F_SETFL is current supported
         if (realCmd == 1L) {  // cmd is F_SETFL
             boolean block = true;
             
             if ((nArg & ModeFlags.NONBLOCK) == ModeFlags.NONBLOCK) {
                 block = false;
             }
 
             try {
                 myOpenFile.getMainStream().setBlocking(block);
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         } else {
             throw getRuntime().newNotImplementedError("JRuby only supports F_SETFL for fcntl/ioctl currently");
         }
         
         return getRuntime().newFixnum(0);
     }
     
     private static final ByteList NIL_BYTELIST = ByteList.create("nil");
     private static final ByteList RECURSIVE_BYTELIST = ByteList.create("[...]");
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(IRubyObject[] args) {
     	ThreadContext context = getRuntime().getCurrentContext();
         
         assert getRuntime().getGlobalVariables().getDefaultSeparator() instanceof RubyString;
         RubyString separator = (RubyString)getRuntime().getGlobalVariables().getDefaultSeparator();
         
         if (args.length == 0) {
             write(context, separator.getByteList());
             return getRuntime().getNil();
         }
 
         for (int i = 0; i < args.length; i++) {
             ByteList line;
             
             if (args[i].isNil()) {
                 line = NIL_BYTELIST;
             } else if (getRuntime().isInspecting(args[i])) {
                 line = RECURSIVE_BYTELIST;
             } else if (args[i] instanceof RubyArray) {
                 inspectPuts((RubyArray) args[i]);
                 continue;
             } else {
                 line = args[i].asString().getByteList();
             }
             
             write(context, line);
             
             if (line.length() == 0 || !line.endsWith(separator.getByteList())) {
                 write(context, separator.getByteList());
             }
         }
         return getRuntime().getNil();
     }
     
     protected void write(ThreadContext context, ByteList byteList) {
         callMethod(context, "write", getRuntime().newStringShared(byteList));
     }
     
     private IRubyObject inspectPuts(RubyArray array) {
         try {
             getRuntime().registerInspecting(array);
             return puts(array.toJavaArray());
         } finally {
             getRuntime().unregisterInspecting(array);
         }
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "readline", optional = 1)
     public IRubyObject readline(IRubyObject[] args) {
         IRubyObject line = gets(args);
 
         if (line.isNil()) {
             throw getRuntime().newEOFError();
         }
         
         return line;
     }
 
     /** Read a byte. On EOF returns nil.
      * 
      */
     @JRubyMethod(name = "getc")
     public IRubyObject getc() {
         try {
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
 
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
     
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
     public IRubyObject readpartial(IRubyObject[] args) {
         if(!(openFile.getMainStream() instanceof ChannelStream)) {
             // cryptic for the uninitiated...
             throw getRuntime().newNotImplementedError("readpartial only works with Nio based handlers");
         }
     	try {
             ByteList buf = ((ChannelStream)openFile.getMainStream()).readpartial(RubyNumeric.fix2int(args[0]));
             IRubyObject strbuf = RubyString.newString(getRuntime(), buf == null ? new ByteList(ByteList.NULL_ARRAY) : buf);
             if(args.length > 1) {
                 args[1].callMethod(getRuntime().getCurrentContext(),MethodIndex.OP_LSHIFT, "<<", strbuf);
                 return args[1];
             } 
 
             return strbuf;
         } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             return getRuntime().getNil();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = "sysread", required = 1, optional = 1)
     public IRubyObject sysread(IRubyObject[] args) {
         int len = (int)RubyNumeric.num2long(args[0]);
         if (len < 0) throw getRuntime().newArgumentError("Negative size");
 
         try {
             RubyString str;
             ByteList buffer;
             if (args.length == 1 || args[1].isNil()) {
                 if (len == 0) {
                     return RubyString.newStringShared(getRuntime(), ByteList.EMPTY_BYTELIST);
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
             }
             
             OpenFile myOpenFile = getOpenFileChecked();
             
             myOpenFile.checkReadable(getRuntime());
             
             if (myOpenFile.getMainStream().readDataBuffered()) {
                 throw getRuntime().newIOError("sysread for buffered IO");
             }
             
             // TODO: Ruby locks the string here
             
             getRuntime().getCurrentContext().getThread().beforeBlockingCall();
             myOpenFile.checkClosed(getRuntime());
             
             // TODO: Ruby re-checks that the buffer string hasn't been modified
             
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
             // All errors to sysread should be SystemCallErrors, but on a closed stream
             // Ruby returns an IOError.  Java throws same exception for all errors so
             // we resort to this hack...
             if ("File not open".equals(e.getMessage())) {
                     throw getRuntime().newIOError(e.getMessage());
             }
     	    throw getRuntime().newSystemCallError(e.getMessage());
     	} finally {
             getRuntime().getCurrentContext().getThread().afterBlockingCall();
         }
     }
     
     @JRubyMethod(name = "read_nonblock", required = 1, optional = 1)
     public IRubyObject read_nonblock(IRubyObject[] args) {
         // TODO: Obviously, we're not doing a nonblocking read here...
         return read(args);
     }
     
     @JRubyMethod(name = "read", optional = 2)
     public IRubyObject read(IRubyObject[] args) {
         int argCount = args.length;
         
         OpenFile myOpenFile = getOpenFileChecked();
         
         if (argCount == 0 || args[0].isNil()) {
             try {
                 myOpenFile.checkReadable(getRuntime());
 
                 if (args.length == 2) {
                     return readAll(args[1]);
                 } else {
                     return readAll(getRuntime().getNil());
                 }
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
         
         int length = RubyNumeric.num2int(args[0]);
         
         if (length < 0) {
             throw getRuntime().newArgumentError("negative length " + length + " given");
         }
         
         RubyString str = null;
diff --git a/src/org/jruby/util/io/ChannelDescriptor.java b/src/org/jruby/util/io/ChannelDescriptor.java
index f68034fd23..09fa7e6479 100644
--- a/src/org/jruby/util/io/ChannelDescriptor.java
+++ b/src/org/jruby/util/io/ChannelDescriptor.java
@@ -1,669 +1,686 @@
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
  * Copyright (C) 2008 Charles O Nutter <headius@headius.com>
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
 
 import java.io.File;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.RandomAccessFile;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.WritableByteChannel;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.jar.JarFile;
 import static java.util.logging.Logger.getLogger;
 import java.util.zip.ZipEntry;
 import org.jruby.RubyIO;
 import org.jruby.ext.posix.POSIX;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 import static org.jruby.util.io.ModeFlags.*;
 
 /**
  * ChannelDescriptor provides an abstraction similar to the concept of a
  * "file descriptor" on any POSIX system. In our case, it's a numbered object
  * (fileno) enclosing a Channel (@see java.nio.channels.Channel), FileDescriptor
  * (@see java.io.FileDescriptor), and flags under which the original open occured
  * (@see org.jruby.util.io.ModeFlags). Several operations you would normally
  * expect to use with a POSIX file descriptor are implemented here and used by
  * higher-level classes to implement higher-level IO behavior.
  * 
  * Note that the channel specified when constructing a ChannelDescriptor will
  * be reference-counted; that is, until all known references to it through this
  * class have gone away, it will be left open. This is to support operations
  * like "dup" which must produce two independent ChannelDescriptor instances
  * that can be closed separately without affecting the other.
  * 
  * At present there's no way to simulate the behavior on some platforms where
  * POSIX dup also allows independent positioning information.
  */
 public class ChannelDescriptor {
     /** Whether to log debugging information */
     private static final boolean DEBUG = false;
     
     /** The java.nio.channels.Channel this descriptor wraps. */
     private Channel channel;
     /**
      * The file number (equivalent to the int file descriptor value in POSIX)
      * for this descriptor.
      */
     private final int fileno;
     /** The java.io.FileDescriptor object for this descriptor. */
     private final FileDescriptor fileDescriptor;
     /**
      * The original org.jruby.util.io.ModeFlags with which the specified
      * channel was opened.
      */
     private final ModeFlags originalModes;
     /** 
      * The reference count for the provided channel.
      * Only counts references through ChannelDescriptor instances.
      */
     private final AtomicInteger refCounter;
 
     /**
      * Used to work-around blocking problems with STDIN. In most cases <code>null</code>.
      * See {@link #ChannelDescriptor(InputStream, int, ModeFlags, FileDescriptor)}
      * for more details. You probably should not use it.
      */
     private InputStream baseInputStream;
     
     /**
      * Construct a new ChannelDescriptor with the specified channel, file number,
      * mode flags, file descriptor object and reference counter. This constructor
      * is only used when constructing a new copy of an existing ChannelDescriptor
      * with an existing reference count, to allow the two instances to safely
      * share and appropriately close  a given channel.
      * 
      * @param channel The channel for the new descriptor, which will be shared with another
      * @param fileno The new file number for the new descriptor
      * @param originalModes The mode flags to use as the "origina" set for this descriptor
      * @param fileDescriptor The java.io.FileDescriptor object to associate with this ChannelDescriptor
      * @param refCounter The reference counter from another ChannelDescriptor being duped.
      */
     private ChannelDescriptor(Channel channel, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor, AtomicInteger refCounter) {
         this.refCounter = refCounter;
         this.channel = channel;
         this.fileno = fileno;
         this.originalModes = originalModes;
         this.fileDescriptor = fileDescriptor;
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number, mode flags,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed.
      * 
      * @param channel The channel for the new descriptor
      * @param fileno The file number for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(Channel channel, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor) {
         this(channel, fileno, originalModes, fileDescriptor, new AtomicInteger(1));
     }
 
     /**
      * Special constructor to create the ChannelDescriptor out of the stream, file number,
      * mode flags, and file descriptor object. The channel will be created from the
      * provided stream. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed. <b>Note:</b> in most cases, you should not
      * use this constructor, it's reserved mostly for STDIN.
      *
      * @param baseInputStream The stream to create the channel for the new descriptor
      * @param fileno The file number for the new descriptor
      * @param originalModes The mode flags for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(InputStream baseInputStream, int fileno, ModeFlags originalModes, FileDescriptor fileDescriptor) {
         // The reason why we need the stream is to be able to invoke available() on it.
         // STDIN in Java is non-interruptible, non-selectable, and attempt to read
         // on such stream might lead to thread being blocked without *any* way to unblock it.
         // That's where available() comes it, so at least we could check whether
         // anything is available to be read without blocking.
         this(Channels.newChannel(baseInputStream), fileno, originalModes, fileDescriptor, new AtomicInteger(1));
         this.baseInputStream = baseInputStream;
     }
 
     /**
      * Construct a new ChannelDescriptor with the given channel, file number,
      * and file descriptor object. The channel will be kept open until all ChannelDescriptor
      * references to it have been closed. The channel's capabilities will be used
      * to determine the "original" set of mode flags.
      * 
      * @param channel The channel for the new descriptor
      * @param fileno The file number for the new descriptor
      * @param fileDescriptor The java.io.FileDescriptor object for the new descriptor
      */
     public ChannelDescriptor(Channel channel, int fileno, FileDescriptor fileDescriptor) throws InvalidValueException {
         this(channel, fileno, getModesFromChannel(channel), fileDescriptor);
     }
 
     /**
      * Get this descriptor's file number.
      * 
      * @return the fileno for this descriptor
      */
     public int getFileno() {
         return fileno;
     }
     
     /**
      * Get the FileDescriptor object associated with this descriptor. This is
      * not guaranteed to be a "valid" descriptor in the terms of the Java
      * implementation, but is provided for completeness and for cases where it
      * is possible to get a valid FileDescriptor for a given channel.
      * 
      * @return the java.io.FileDescriptor object associated with this descriptor
      */
     public FileDescriptor getFileDescriptor() {
         return fileDescriptor;
     }
 
     /**
      * The channel associated with this descriptor. The channel will be reference
      * counted through ChannelDescriptor and kept open until all ChannelDescriptor
      * objects have been closed. References that leave ChannelDescriptor through
      * this method will not be counted.
      * 
      * @return the java.nio.channels.Channel associated with this descriptor
      */
     public Channel getChannel() {
         return channel;
     }
 
     /**
      * This is intentionally non-public, since it should not be really
      * used outside of very limited use case (handling of STDIN).
      * See {@link #ChannelDescriptor(InputStream, int, ModeFlags, FileDescriptor)}
      * for more info.
      */
     /*package-protected*/ InputStream getBaseInputStream() {
         return baseInputStream;
     }
 
     /**
      * Whether the channel associated with this descriptor is seekable (i.e.
      * whether it is instanceof FileChannel).
      * 
      * @return true if the associated channel is seekable, false otherwise
      */
     public boolean isSeekable() {
         return channel instanceof FileChannel;
     }
     
     /**
      * Whether the channel associated with this descriptor is a NullChannel,
      * for which many operations are simply noops.
      */
     public boolean isNull() {
         return channel instanceof NullChannel;
     }
 
     /**
      * Whether the channel associated with this descriptor is writable (i.e.
      * whether it is instanceof WritableByteChannel).
      * 
      * @return true if the associated channel is writable, false otherwise
      */
     public boolean isWritable() {
         return channel instanceof WritableByteChannel;
     }
     
     /**
      * Whether the channel associated with this descriptor is open.
      * 
      * @return true if the associated channel is open, false otherwise
      */
     public boolean isOpen() {
         return channel.isOpen();
     }
     
     /**
      * Check whether the isOpen returns true, raising a BadDescriptorException if
      * it returns false.
      * 
      * @throws org.jruby.util.io.BadDescriptorException if isOpen returns false
      */
     public void checkOpen() throws BadDescriptorException {
         if (!isOpen()) {
             throw new BadDescriptorException();
         }
     }
     
     /**
      * Get the original mode flags for the descriptor.
      * 
      * @return the original mode flags for the descriptor
      */
     public ModeFlags getOriginalModes() {
         return originalModes;
     }
     
     /**
      * Check whether a specified set of mode flags is a superset of this
      * descriptor's original set of mode flags.
      * 
      * @param newModes The modes to confirm as superset
      * @throws org.jruby.util.io.InvalidValueException if the modes are not a superset
      */
     public void checkNewModes(ModeFlags newModes) throws InvalidValueException {
         if (!newModes.isSubsetOf(originalModes)) {
             throw new InvalidValueException();
         }
     }
     
     /**
      * Mimics the POSIX dup(2) function, returning a new descriptor that references
      * the same open channel.
      * 
      * @return A duplicate ChannelDescriptor based on this one
      */
     public ChannelDescriptor dup() {
         synchronized (refCounter) {
             refCounter.incrementAndGet();
 
             int newFileno = RubyIO.getNewFileno();
             
             if (DEBUG) getLogger("ChannelDescriptor").info("Reopen fileno " + newFileno + ", refs now: " + refCounter.get());
 
             return new ChannelDescriptor(channel, newFileno, originalModes, fileDescriptor, refCounter);
         }
     }
     
     /**
      * Mimics the POSIX dup2(2) function for a "file descriptor" that's already
      * open.
      * 
      * @param other The descriptor into which we should dup this one
      */
     public void dup2(ChannelDescriptor other) {
         other.channel = channel;
     }
     
     /**
      * Mimics the POSIX dup2(2) function, returning a new descriptor that references
      * the same open channel but with a specified fileno.
      * 
      * @param fileno The fileno to use for the new descriptor
      * @return A duplicate ChannelDescriptor based on this one
      */
     public ChannelDescriptor dup2(int fileno) {
         synchronized (refCounter) {
             refCounter.incrementAndGet();
 
             if (DEBUG) getLogger("ChannelDescriptor").info("Reopen fileno " + fileno + ", refs now: " + refCounter.get());
 
             return new ChannelDescriptor(channel, fileno, originalModes, fileDescriptor, refCounter);
         }
     }
     
     /**
      * Perform a low-level seek operation on the associated channel if it is
      * instanceof FileChannel, or raise PipeException if it is not a FileChannel.
      * Calls checkOpen to confirm the target channel is open. This is equivalent
      * to the lseek(2) POSIX function, and like that function it bypasses any
      * buffer flushing or invalidation as in ChannelStream.fseek.
      * 
      * @param offset the offset value to use
      * @param whence whence to seek
      * @throws java.io.IOException If there is an exception while seeking
      * @throws org.jruby.util.io.InvalidValueException If the value specified for
      * offset or whence is invalid
      * @throws org.jruby.util.io.PipeException If the target channel is not seekable
      * @throws org.jruby.util.io.BadDescriptorException If the target channel is
      * already closed.
      * @return the new offset into the FileChannel.
      */
     public long lseek(long offset, int whence) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         if (channel instanceof FileChannel) {
             checkOpen();
             
             FileChannel fileChannel = (FileChannel)channel;
             try {
                 long pos;
                 switch (whence) {
                 case Stream.SEEK_SET:
                     pos = offset;
                     fileChannel.position(pos);
                     break;
                 case Stream.SEEK_CUR:
                     pos = fileChannel.position() + offset;
                     fileChannel.position(pos);
                     break;
                 case Stream.SEEK_END:
                     pos = fileChannel.size() + offset;
                     fileChannel.position(pos);
                     break;
                 default:
                     throw new InvalidValueException();
                 }
                 return pos;
             } catch (IllegalArgumentException e) {
                 throw new InvalidValueException();
             }
         } else {
             throw new PipeException();
         }
     }
 
     /**
      * Perform a low-level read of the specified number of bytes into the specified
      * byte list. The incoming bytes will be appended to the byte list. This is
      * equivalent to the read(2) POSIX function, and like that function it
      * ignores read and write buffers defined elsewhere.
      * 
      * @param number the number of bytes to read
      * @param byteList the byte list on which to append the incoming bytes
      * @return the number of bytes actually read
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed.
      * @see java.util.ByteList
      */
     public int read(int number, ByteList byteList) throws IOException, BadDescriptorException {
         checkOpen();
         
         ByteBuffer buffer = ByteBuffer.allocate(number);
         int bytesRead = read(buffer);
 
         byte[] ret;
         if (buffer.hasRemaining()) {
             buffer.flip();
             ret = new byte[buffer.remaining()];
             buffer.get(ret);
         } else {
             ret = buffer.array();
         }
         byteList.append(ret);
 
         return bytesRead;
     }
     
     /**
      * Perform a low-level read of the remaining number of bytes into the specified
      * byte buffer. The incoming bytes will be used to fill the remaining space in
      * the target byte buffer. This is equivalent to the read(2) POSIX function,
      * and like that function it ignores read and write buffers defined elsewhere.
      * 
      * @param buffer the java.nio.ByteBuffer in which to put the incoming bytes
      * @return the number of bytes actually read
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      * @see java.nio.ByteBuffer
      */
     public int read(ByteBuffer buffer) throws IOException, BadDescriptorException {
         checkOpen();
         
         ReadableByteChannel readChannel = (ReadableByteChannel) channel;
         int bytesRead = 0;
         bytesRead = readChannel.read(buffer);
 
         return bytesRead;
     }
     
     /**
      * Write the bytes in the specified byte list to the associated channel.
      * 
      * @param buf the byte list containing the bytes to be written
      * @return the number of bytes actually written
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int internalWrite(ByteBuffer buffer) throws IOException, BadDescriptorException {
         checkOpen();
         
         WritableByteChannel writeChannel = (WritableByteChannel)channel;
         
         if (isSeekable() && originalModes.isAppendable()) {
             FileChannel fileChannel = (FileChannel)channel;
             fileChannel.position(fileChannel.size());
         }
         
         return writeChannel.write(buffer);
     }
     
     /**
      * Write the bytes in the specified byte list to the associated channel.
      * 
      * @param buf the byte list containing the bytes to be written
      * @return the number of bytes actually written
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int write(ByteBuffer buffer) throws IOException, BadDescriptorException {
         checkOpen();
         
         return internalWrite(buffer);
     }
     
     /**
      * Write the bytes in the specified byte list to the associated channel.
      * 
      * @param buf the byte list containing the bytes to be written
      * @return the number of bytes actually written
      * @throws java.io.IOException if there is an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int write(ByteList buf) throws IOException, BadDescriptorException {
         checkOpen();
         
         return internalWrite(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
     }
+
+    /**
+     * Write the bytes in the specified byte list to the associated channel.
+     * 
+     * @param buf the byte list containing the bytes to be written
+     * @param offset the offset to start at. this is relative to the begin variable in the but
+     * @param len the amount of bytes to write. this should not be longer than the buffer
+     * @return the number of bytes actually written
+     * @throws java.io.IOException if there is an exception during IO
+     * @throws org.jruby.util.io.BadDescriptorException if the associated
+     * channel is already closed
+     */
+    public int write(ByteList buf, int offset, int len) throws IOException, BadDescriptorException {
+        checkOpen();
+        
+        return internalWrite(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin()+offset, len));
+    }
     
     /**
      * Write the byte represented by the specified int to the associated channel.
      * 
      * @param c The byte to write
      * @return 1 if the byte was written, 0 if not and -1 if there was an error
      * (@see java.nio.channels.WritableByteChannel.write(java.nio.ByteBuffer))
      * @throws java.io.IOException If there was an exception during IO
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      */
     public int write(int c) throws IOException, BadDescriptorException {
         checkOpen();
         
         ByteBuffer buf = ByteBuffer.allocate(1);
         buf.put((byte)c);
         buf.flip();
         
         return internalWrite(buf);
     }
     
     /**
      * Open a new descriptor using the given working directory, file path,
      * mode flags, and file permission. This is equivalent to the open(2)
      * POSIX function. See org.jruby.util.io.ChannelDescriptor.open(String, String, ModeFlags, int, POSIX)
      * for the version that also sets file permissions.
      * 
      * @param cwd the "current working directory" to use when opening the file
      * @param path the file path to open
      * @param flags the mode flags to use for opening the file
      * @return a new ChannelDescriptor based on the specified parameters
      * @throws java.io.FileNotFoundException if the target file could not be found
      * and the create flag was not specified
      * @throws org.jruby.util.io.DirectoryAsFileException if the target file is
      * a directory being opened as a file
      * @throws org.jruby.util.io.FileExistsException if the target file should
      * be created anew, but already exists
      * @throws java.io.IOException if there is an exception during IO
      */
     public static ChannelDescriptor open(String cwd, String path, ModeFlags flags) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException {
         return open(cwd, path, flags, 0, null);
     }
     
     /**
      * Open a new descriptor using the given working directory, file path,
      * mode flags, and file permission. This is equivalent to the open(2)
      * POSIX function.
      * 
      * @param cwd the "current working directory" to use when opening the file
      * @param path the file path to open
      * @param flags the mode flags to use for opening the file
      * @param perm the file permissions to use when creating a new file (currently
      * unobserved)
      * @param posix a POSIX api implementation, used for setting permissions; if null, permissions are ignored
      * @return a new ChannelDescriptor based on the specified parameters
      * @throws java.io.FileNotFoundException if the target file could not be found
      * and the create flag was not specified
      * @throws org.jruby.util.io.DirectoryAsFileException if the target file is
      * a directory being opened as a file
      * @throws org.jruby.util.io.FileExistsException if the target file should
      * be created anew, but already exists
      * @throws java.io.IOException if there is an exception during IO
      */
     public static ChannelDescriptor open(String cwd, String path, ModeFlags flags, int perm, POSIX posix) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException {
         boolean fileCreated = false;
         if (path.equals("/dev/null") || path.equalsIgnoreCase("nul:") || path.equalsIgnoreCase("nul")) {
             Channel nullChannel = new NullChannel();
             // FIXME: don't use RubyIO for this
             return new ChannelDescriptor(nullChannel, RubyIO.getNewFileno(), flags, new FileDescriptor());
         } else if(path.startsWith("file:")) {
             String filePath = path.substring(5, path.indexOf("!"));
             String internalPath = path.substring(path.indexOf("!") + 2);
 
             if (!new File(filePath).exists()) {
                 throw new FileNotFoundException(path);
             }
             
             JarFile jf = new JarFile(filePath);
             ZipEntry zf = jf.getEntry(internalPath);
 
             if(zf == null) {
                 throw new FileNotFoundException(path);
             }
 
             InputStream is = jf.getInputStream(zf);
             // FIXME: don't use RubyIO for this
             return new ChannelDescriptor(Channels.newChannel(is), RubyIO.getNewFileno(), flags, new FileDescriptor());
         } else {
             JRubyFile theFile = JRubyFile.create(cwd,path);
 
             if (theFile.isDirectory() && flags.isWritable()) {
                 throw new DirectoryAsFileException();
             }
 
             if (flags.isCreate()) {
                 if (theFile.exists() && flags.isExclusive()) {
                     throw new FileExistsException(path);
                 }
                 fileCreated = theFile.createNewFile();
             } else {
                 if (!theFile.exists()) {
                     throw new FileNotFoundException(path);
                 }
             }
 
             // We always open this rw since we can only open it r or rw.
             RandomAccessFile file = new RandomAccessFile(theFile, flags.toJavaModeString());
 
             // call chmod after we created the RandomAccesFile
             // because otherwise, the file could be read-only
             if (fileCreated) {
                 // attempt to set the permissions, if we have been passed a POSIX instance,
                 // and only if the file was created in this call.
                 if (posix != null && perm != -1) {
                     posix.chmod(theFile.getPath(), perm);
                 }
             }
 
             if (flags.isTruncate()) file.setLength(0L);
 
             // TODO: append should set the FD to end, no? But there is no seek(int) in libc!
             //if (modes.isAppendable()) seek(0, Stream.SEEK_END);
 
             return new ChannelDescriptor(file.getChannel(), RubyIO.getNewFileno(), flags, file.getFD());
         }
     }
     
     /**
      * Close this descriptor. If in closing the last ChannelDescriptor reference
      * to the associate channel is closed, the channel itself will be closed.
      * 
      * @throws org.jruby.util.io.BadDescriptorException if the associated
      * channel is already closed
      * @throws java.io.IOException if there is an exception during IO
      */
     public void close() throws BadDescriptorException, IOException {
         synchronized (refCounter) {
             // if refcount is at or below zero, we're no longer valid
             if (refCounter.get() <= 0) {
                 throw new BadDescriptorException();
             }
 
             // if channel is already closed, we're no longer valid
             if (!channel.isOpen()) {
                 throw new BadDescriptorException();
             }
 
             // otherwise decrement and possibly close as normal
             int count = refCounter.decrementAndGet();
 
             if (DEBUG) getLogger("ChannelDescriptor").info("Descriptor for fileno " + fileno + " refs: " + count);
 
             if (count <= 0) {
                 channel.close();
             }
         }
     }
     
     /**
      * Build a set of mode flags using the specified channel's actual capabilities.
      * 
      * @param channel the channel to examine for capabilities
      * @return the mode flags 
      * @throws org.jruby.util.io.InvalidValueException
      */
     private static ModeFlags getModesFromChannel(Channel channel) throws InvalidValueException {
         ModeFlags modes;
         if (channel instanceof ReadableByteChannel) {
             if (channel instanceof WritableByteChannel) {
                 modes = new ModeFlags(RDWR);
             }
             modes = new ModeFlags(RDONLY);
         } else if (channel instanceof WritableByteChannel) {
             modes = new ModeFlags(WRONLY);
         } else {
             // FIXME: I don't like this
             modes = new ModeFlags(RDWR);
         }
         
         return modes;
     }
 }
diff --git a/src/org/jruby/util/io/ChannelStream.java b/src/org/jruby/util/io/ChannelStream.java
index 6782040166..96cfb4aa51 100644
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
@@ -1,873 +1,883 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.RandomAccessFile;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channels;
 import java.nio.channels.FileChannel;
 
 import java.nio.channels.Channel;
 import java.nio.channels.IllegalBlockingModeException;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.WritableByteChannel;
 import static java.util.logging.Logger.getLogger;
 import org.jruby.Finalizable;
 import org.jruby.Ruby;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 
 /**
  * <p>This file implements a seekable IO file.</p>
  */
 public class ChannelStream implements Stream, Finalizable {
     private final static boolean DEBUG = false;
     private final static int BUFSIZE = 16 * 1024;
     
     private Ruby runtime;
     protected ModeFlags modes;
     protected boolean sync = false;
     
     protected ByteBuffer buffer; // r/w buffer
     protected boolean reading; // are we reading or writing?
     private ChannelDescriptor descriptor;
     private boolean blocking = true;
     protected int ungotc = -1;
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes, FileDescriptor fileDescriptor) throws InvalidValueException {
         descriptor.checkNewModes(modes);
         
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = modes;
         this.buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
         
         // this constructor is used by fdopen, so we don't increment descriptor ref count
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor) {
         this(runtime, descriptor, descriptor.getFileDescriptor());
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, FileDescriptor fileDescriptor) {
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = descriptor.getOriginalModes();
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
     }
 
     public ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         descriptor.checkNewModes(modes);
         
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = modes;
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
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
         
         byte first = separator.bytes[separator.begin];
 
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
                 buffer.clear();
                 int read = ((ReadableByteChannel)descriptor.getChannel()).read(buffer);
                 buffer.flip();
                 if (read == -1) break LineLoop;
             }
             
             // found a match above, check if remaining separator characters match, appending as we go
             for (int i = 0; i < separator.realSize; i++) {
                 if (c == -1) {
                     break LineLoop;
                 } else if (c != separator.bytes[separator.begin + i]) {
                     buf.append(c);
                     continue LineLoop;
                 }
                 buf.append(c);
                 if (i < separator.realSize - 1) {
                     c = read();
                 }
             }
             break;
         }
 
         if (separatorString == PARAGRAPH_DELIMETER) {
             while (c == separator.bytes[separator.begin]) {
                 c = read();
             }
             ungetc(c);
         }
 
         return buf;
     }
     
     public synchronized ByteList readall() throws IOException, BadDescriptorException {
         if (descriptor.isSeekable()) {
             invalidateBuffer();
             FileChannel channel = (FileChannel)descriptor.getChannel();
             long left = channel.size() - channel.position();
             if (left == 0) {
                 eof = true;
                 return null;
             }
 
             return fread((int) left);
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
      * <p>Close IO handler resources.</p>
      * @throws IOException 
      * @throws BadDescriptorException 
      * 
      * @see org.jruby.util.IOHandler#close()
      */
     public synchronized void fclose() throws IOException, BadDescriptorException {
         close(false); // not closing from finalise
     }
     
     /**
      * Internal close, to safely work for finalizing.
      * @param finalizing true if this is in a finalizing context
      * @throws IOException 
      * @throws BadDescriptorException
      */
     private void close(boolean finalizing) throws IOException, BadDescriptorException {
         try {
             flushWrite();
 
             descriptor.close();
 
             if (DEBUG) getLogger("ChannelStream").info("Descriptor for fileno " + descriptor.getFileno() + " closed by stream");
         } finally {
             if (!finalizing) getRuntime().removeInternalFinalizer(this);
         }
     }
     
     /**
      * Internal close, to safely work for finalizing.
      * @param finalizing true if this is in a finalizing context
      * @throws IOException 
      * @throws BadDescriptorException
      */
     private void closeForFinalize() {
         try {
             close(true);
         } catch (BadDescriptorException ex) {
             // silence
         } catch (IOException ex) {
             // silence
         }
     }
 
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#flush()
      */
     public synchronized int fflush() throws IOException, BadDescriptorException {
         checkWritable();
         try {
             flushWrite();
         } catch (EOFException eof) {
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
             
+        int len = buffer.position();
         buffer.flip();
-        descriptor.write(buffer);
+        int n = descriptor.write(buffer);
+
+        if(n != len) {
+            // TODO: check the return value here
+        }
+
         buffer.clear();
     }
 
     /**
      * @see org.jruby.util.IOHandler#getInputStream()
      */
     public InputStream newInputStream() {
         InputStream in = descriptor.getBaseInputStream();
         if (in == null) {
             return new BufferedInputStream(Channels.newInputStream((ReadableByteChannel)descriptor.getChannel()));
         } else {
             return in;
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#getOutputStream()
      */
     public OutputStream newOutputStream() {
         return new BufferedOutputStream(Channels.newOutputStream((WritableByteChannel)descriptor.getChannel()));
     }
     
     private boolean eof = false;
     
     public void clearerr() {
         eof = false;
     }
     
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#isEOF()
      */
     public synchronized boolean feof() throws IOException, BadDescriptorException {
         checkReadable();
         
         if (eof) {
             return true;
         } else {
             return false;
         }
 //        
 //        if (reading && buffer.hasRemaining()) return false;
 //        
 //        if (descriptor.isSeekable()) {
 //            FileChannel fileChannel = (FileChannel)descriptor.getChannel();
 //            return (fileChannel.size() == fileChannel.position());
 //        } else if (descriptor.getChannel() instanceof SocketChannel) {
 //            return false;
 //        } else {
 //            checkReadable();
 //            ensureRead();
 //
 //            if (ungotc > 0) {
 //                return false;
 //            }
 //            // TODO: this is new to replace what's below
 //            ungotc = read();
 //            if (ungotc == -1) {
 //                eof = true;
 //                return true;
 //            }
 //            // FIXME: this was here before; need a better way?
 ////            if (fillInBuffer() < 0) {
 ////                return true;
 ////            }
 //            return false;
 //        }
     }
     
     /**
      * @throws IOException 
      * @see org.jruby.util.IOHandler#pos()
      */
     public synchronized long fgetpos() throws IOException, PipeException, InvalidValueException, BadDescriptorException {
         // Correct position for read / write buffering (we could invalidate, but expensive)
         if (descriptor.isSeekable()) {
             int ungotcCopy = ungotc;
             fseek(0, SEEK_CUR);
             ungotc = ungotcCopy;
 
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (ungotc != -1 && fileChannel.position() > 0) {
                 return fileChannel.position() - 1;
             } else {
                 return fileChannel.position();
             }
         } else if (descriptor.isNull()) {
             return 0;
         } else {
             throw new PipeException();
         }
     }
     
     /**
      * @throws IOException 
      * @throws InvalidValueException 
      * @see org.jruby.util.IOHandler#seek(long, int)
      */
     public synchronized void fseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             ungotc = -1;
             if (reading) {
                 if (buffer.remaining() > 0) {
                     fileChannel.position(fileChannel.position() - buffer.remaining());
                 }
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
                     fileChannel.position(fileChannel.position() + offset);
                     break;
                 case SEEK_END:
                     fileChannel.position(fileChannel.size() + offset);
                     break;
                 }
             } catch (IllegalArgumentException e) {
                 throw new InvalidValueException();
             }
         } else if (descriptor.isNull()) {
             return;
         } else {
             throw new PipeException();
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#sync()
      */
     public void sync() throws IOException, BadDescriptorException {
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
                 throw getRuntime().newIOError("sysread for buffered IO");
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
         
         return byteList;
     }
 
     private ByteList bufferedRead(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         
         ByteList result = new ByteList();
         int len = -1;
         if (buffer.hasRemaining()) { // already have some bytes buffered
             len = (number <= buffer.remaining()) ? number : buffer.remaining();
             result.append(buffer, len);
         }
         
         ReadableByteChannel readChannel = (ReadableByteChannel)descriptor.getChannel();
         int read = BUFSIZE;
         while (read == BUFSIZE && result.length() != number) { // not complete. try to read more
             buffer.clear(); 
             read = readChannel.read(buffer);
             buffer.flip();
             if (read == -1) break;
             int desired = number - result.length();
             len = (desired < read) ? desired : read;
             result.append(buffer, len);
         }
         
         if (result.length() == 0 && number != 0) throw new java.io.EOFException();
         return result;
     }
     
     private int bufferedRead() throws IOException, BadDescriptorException {
         ensureRead();
         
         if (!buffer.hasRemaining()) {
             buffer.clear();
             int read = descriptor.read(buffer);
             buffer.flip();
             
             if (read <= 0) {
                 eof = true;
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
         getRuntime().secure(4);
         checkWritable();
         ensureWrite();
         
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
         
         if (buf.length() > buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
             
-            descriptor.write(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
+
+            int n = descriptor.write(ByteBuffer.wrap(buf.unsafeBytes(), buf.begin(), buf.length()));
+            if(n != buf.length()) {
+                // TODO: check the return value here
+            }
         } else {
             if (buf.length() > buffer.remaining()) flushWrite();
             
             buffer.put(buf.unsafeBytes(), buf.begin(), buf.length());
         }
         
         if (isSync()) sync();
         
         return buf.realSize;
     }
     
     /**
      * @throws IOException 
      * @throws BadDescriptorException 
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(int c) throws IOException, BadDescriptorException {
         getRuntime().secure(4);
         checkWritable();
         ensureWrite();
 
         if (!buffer.hasRemaining()) flushWrite();
         
         buffer.put((byte) c);
             
         if (isSync()) sync();
             
         return 1;
     }
     
     public synchronized void ftruncate(long newLength) throws IOException, BadDescriptorException {
         invalidateBuffer();
         FileChannel fileChannel = (FileChannel)descriptor.getChannel();
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
      * Ensure close (especially flush) when we're finished with
      */
     public void finalize() {
         // FIXME: I got a bunch of NPEs when I didn't check for nulls here...HOW?!
         if (descriptor != null && descriptor.isSeekable() && descriptor.isOpen()) closeForFinalize(); // close without removing from finalizers
     }
     
     public int ready() throws IOException {
         return newInputStream().available();
     }
 
     public synchronized void fputc(int c) throws IOException, BadDescriptorException {
         try {
             bufferedWrite(c);
             fflush();
         } catch (IOException e) {
         }
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
 
     public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {
         try {
             if (number == 0) {
                 if (eof) {
                     return null;
                 } else {
                     return new ByteList(0);
                 }
             }
 
             if (ungotc >= 0) {
                 ByteList buf2 = bufferedRead(number - 1);
                 buf2.prepend((byte)ungotc);
                 ungotc = -1;
                 return buf2;
             }
 
             return bufferedRead(number);
         } catch (EOFException e) {
             eof = true;
             return null;
         }
     }
 
     public synchronized ByteList readpartial(int number) throws IOException, BadDescriptorException, EOFException {
         if (descriptor.getChannel() instanceof SelectableChannel) {
             if (ungotc >= 0) {
                 ByteList buf2 = bufferedRead(number - 1);
                 buf2.prepend((byte)ungotc);
                 ungotc = -1;
                 return buf2;
             } else {
                 return bufferedRead(number);
             }
         } else {
             return null;
         }
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
 
     public synchronized void freopen(String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException {
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
             descriptor = new ChannelDescriptor(new NullChannel(), descriptor.getFileno(), modes, new FileDescriptor());
         } else {
             String cwd = getRuntime().getCurrentDirectory();
             JRubyFile theFile = JRubyFile.create(cwd,path);
 
             if (theFile.isDirectory() && modes.isWritable()) throw new DirectoryAsFileException();
 
             if (modes.isCreate()) {
                 if (theFile.exists() && modes.isExclusive()) {
                     throw getRuntime().newErrnoEEXISTError("File exists - " + path);
                 }
                 theFile.createNewFile();
             } else {
                 if (!theFile.exists()) {
                     throw getRuntime().newErrnoENOENTError("file not found - " + path);
                 }
             }
 
             // We always open this rw since we can only open it r or rw.
             RandomAccessFile file = new RandomAccessFile(theFile, modes.toJavaModeString());
 
             if (modes.isTruncate()) file.setLength(0L);
             
             descriptor = new ChannelDescriptor(file.getChannel(), descriptor.getFileno(), modes, file.getFD());
         
             if (modes.isAppendable()) fseek(0, SEEK_END);
         }
     }
     
     public static Stream fopen(Ruby runtime, String path, ModeFlags modes) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         String cwd = runtime.getCurrentDirectory();
         
         ChannelDescriptor descriptor = ChannelDescriptor.open(cwd, path, modes);
         
         Stream stream = fdopen(runtime, descriptor, modes);
         
         if (modes.isAppendable()) stream.fseek(0, Stream.SEEK_END);
         
         return stream;
     }
     
     public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         Stream handler = new ChannelStream(runtime, descriptor, modes, descriptor.getFileDescriptor());
         
         return handler;
     }
 }
