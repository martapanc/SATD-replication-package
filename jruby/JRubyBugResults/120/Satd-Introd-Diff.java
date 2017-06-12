diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index ba81cfd06a..f5bc07afe8 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,1933 +1,2574 @@
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
 
+import static java.lang.System.out;
+import org.jruby.util.io.FileExistsException;
+import org.jruby.util.io.STDIO;
+import org.jruby.util.io.OpenFile;
+import org.jruby.util.io.ChannelDescriptor;
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.lang.ref.Reference;
 import java.lang.ref.WeakReference;
 import java.nio.channels.Channel;
 import java.nio.channels.Channels;
-import java.nio.channels.FileChannel;
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
-import org.jruby.common.IRubyWarnings.ID;
 
+import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
-import org.jruby.util.Stream;
-import org.jruby.util.Stream.InvalidValueException;
-import org.jruby.util.Stream.PipeException;
-import org.jruby.util.IOModes;
+import org.jruby.util.io.Stream;
+import org.jruby.util.io.ModeFlags;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
-import org.jruby.util.Stream.BadDescriptorException;
-import org.jruby.util.ChannelStream;
-import org.jruby.util.io.SplitChannel;
+import org.jruby.util.io.BadDescriptorException;
+import org.jruby.util.io.ChannelStream;
+import org.jruby.util.io.InvalidValueException;
+import org.jruby.util.io.PipeException;
 
 /**
  * 
  * @author jpetersen
  */
 public class RubyIO extends RubyObject {
-    public enum STDIO {
-        IN, OUT, ERR;
-        
-        public int fileno() {
-            switch (this) {
-            case IN: return 0;
-            case OUT: return 1;
-            case ERR: return 2;
-            default: throw new RuntimeException();
-            }
-        }
-        
-        public static boolean isSTDIO(int fileno) {
-            if (fileno >= 0 && fileno <= 2) return true;
-            
-            return false;
-        }
-    }
-    
-    public static class OpenFile {
-        private Stream mainStream;
-        private Stream pipeStream;
-        private IOModes modes;
-        private int pid = -1; // probably not useful in JRuby; for fork and pipes and all
-        private int lineNumber = 0;
-        private String path;
-        private Finalizer finalizer;
-
-        public Stream getMainStream() {
-            return mainStream;
-        }
-
-        public void setMainStream(Stream mainStream) {
-            this.mainStream = mainStream;
-        }
-
-        public Stream getPipeStream() {
-            return pipeStream;
-        }
-
-        public void setPipeStream(Stream pipeStream) {
-            this.pipeStream = pipeStream;
-        }
-
-        public IOModes getModes() {
-            return modes;
-        }
-
-        public void setModes(IOModes modes) {
-            this.modes = modes;
-        }
-
-        public int getPid() {
-            return pid;
-        }
-
-        public void setPid(int pid) {
-            this.pid = pid;
-        }
-
-        public int getLineNumber() {
-            return lineNumber;
-        }
-
-        public void setLineNumber(int lineNumber) {
-            this.lineNumber = lineNumber;
-        }
-
-        public String getPath() {
-            return path;
-        }
-
-        public void setPath(String path) {
-            this.path = path;
-        }
-
-        public Finalizer getFinalizer() {
-            return finalizer;
-        }
-
-        public void setFinalizer(Finalizer finalizer) {
-            this.finalizer = finalizer;
-        }
-    }
-    
-    public static class ChannelDescriptor {
-        private Channel channel;
-        private int fileno;
-        
-        public ChannelDescriptor(Channel channel, int fileno) {
-            this.channel = channel;
-            this.fileno = fileno;
-        }
-
-        public int getFileno() {
-            return fileno;
-        }
-
-        public void setFileno(int fileno) {
-            this.fileno = fileno;
-        }
-
-        public Channel getChannel() {
-            return channel;
-        }
-
-        public void setChannel(Channel channel) {
-            this.channel = channel;
-        }
-        
-        public boolean isSeekable() {
-            return channel instanceof FileChannel;
-        }
-    }
-    
     protected OpenFile openFile;
     
-    // Does THIS IO object think it is still open
-    // as opposed to the IO Handler which knows the
-    // actual truth.  If two IO objects share the
-    // same IO Handler, then it is possible for
-    // one object to think that the handler is open
-    // when it really isn't.  Keeping track of this yields
-    // the right errors.
-    protected boolean isOpen = true;
-    private boolean atEOF = false;
-
-    /*
-     * Random notes:
-     *  
-     * 1. When a second IO object is created with the same fileno odd
-     * concurrency issues happen when the underlying implementation
-     * commits data.   So:
-     * 
-     * f = File.new("some file", "w")
-     * f.puts("heh")
-     * g = IO.new(f.fileno)
-     * g.puts("hoh")
-     * ... more operations of g and f ...
-     * 
-     * Will generate a mess in "some file".  The problem is that most
-     * operations are buffered.  When those buffers flush and get
-     * written to the physical file depends on the implementation
-     * (semantically I would think that it should be last op wins -- but 
-     * it isn't).  I doubt java could mimic ruby in this way.  I also 
-     * doubt many people are taking advantage of this.  How about 
-     * syswrite/sysread though?  I think the fact that sysread/syswrite 
-     * are defined to be a low-level system calls, allows implementations 
-     * to be somewhat different?
-     * 
-     * 2. In the case of:
-     * f = File.new("some file", "w")
-     * f.puts("heh")
-     * print f.pos
-     * g = IO.new(f.fileno)
-     * print g.pos
-     * Both printed positions will be the same.  But:
-     * f = File.new("some file", "w")
-     * f.puts("heh")
-     * g = IO.new(f.fileno)
-     * print f.pos, g.pos
-     * won't be the same position.  Seem peculiar enough not to touch
-     * (this involves pos() actually causing a seek?)
-     * 
-     * 3. All IO objects reference a IOHandler.  If multiple IO objects
-     * have the same fileno, then they also share the same IOHandler.
-     * It is possible that some IO objects may share the same IOHandler
-     * but not have the same permissions.  However, all subsequent IO
-     * objects created after the first must be a subset of the original
-     * IO Object (see below for an example). 
-     *
-     * The idea that two or more IO objects can have different access
-     * modes means that IO objects must keep track of their own
-     * permissions.  In addition the IOHandler itself must know what
-     * access modes it has.
-     * 
-     * The above sharing situation only occurs in a situation like:
-     * f = File.new("some file", "r+")
-     * g = IO.new(f.fileno, "r")
-     * Where g has reduced (subset) permissions.
-     * 
-     * On reopen, the fileno's IOHandler gets replaced by a new handler. 
-     */
-    
-    /*
-     * I considered making all callers of this be moved into IOHandlers
-     * constructors (since it would be less error prone to forget there).
-     * However, reopen() makes doing this a little funky. 
-     */
-    public void registerIOHandler(Stream newHandler) {
-        getRuntime().getIoHandlers().put(new Integer(newHandler.getDescriptor().getFileno()), new WeakReference<Stream>(newHandler));
+    public void registerDescriptor(ChannelDescriptor descriptor) {
+        getRuntime().getDescriptors().put(new Integer(descriptor.getFileno()), new WeakReference<ChannelDescriptor>(descriptor));
     }
     
-    public void unregisterIOHandler(int aFileno) {
-        getRuntime().getIoHandlers().remove(new Integer(aFileno));
+    public void unregisterDescriptor(int aFileno) {
+        getRuntime().getDescriptors().remove(new Integer(aFileno));
     }
     
-    public Stream getIOHandlerByFileno(int aFileno) {
-        Reference<Stream> reference = getRuntime().getIoHandlers().get(new Integer(aFileno));
+    public ChannelDescriptor getDescriptorByFileno(int aFileno) {
+        Reference<ChannelDescriptor> reference = getRuntime().getDescriptors().get(new Integer(aFileno));
         if (reference == null) {
             return null;
         }
-        return (Stream) reference.get();
+        return (ChannelDescriptor) reference.get();
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
-        openFile.setModes(new IOModes(getRuntime()));
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
-            openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(outputStream), getNewFileno())));
-        } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(outputStream), getNewFileno(), new FileDescriptor())));
+        } catch (InvalidValueException e) {
+            throw getRuntime().newErrnoEINVALError();
         }
-        openFile.setModes(openFile.getMainStream().getModes());
         
-        registerIOHandler(openFile.getMainStream());
+        openFile.setMode(OpenFile.WRITABLE | OpenFile.APPEND);
+        
+        registerDescriptor(openFile.getMainStream().getDescriptor());
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
-            openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(inputStream), getNewFileno())));
-        } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(inputStream), getNewFileno(), new FileDescriptor())));
+        } catch (InvalidValueException e) {
+            throw getRuntime().newErrnoEINVALError();
         }
         
-        openFile.setModes(openFile.getMainStream().getModes());
+        openFile.setMode(OpenFile.READABLE);
         
-        registerIOHandler(openFile.getMainStream());
+        registerDescriptor(openFile.getMainStream().getDescriptor());
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         openFile = new OpenFile();
         
         try {
-            openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(channel, getNewFileno())));
-        } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(channel, getNewFileno(), new FileDescriptor())));
+        } catch (InvalidValueException e) {
+            throw getRuntime().newErrnoEINVALError();
         }
-        openFile.setModes(openFile.getMainStream().getModes());
         
-        registerIOHandler(openFile.getMainStream());
+        openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
+        
+        registerDescriptor(openFile.getMainStream().getDescriptor());
     }
 
-    public RubyIO(Ruby runtime, Process process) {
+    public RubyIO(Ruby runtime, Process process, ModeFlags modes) {
     	super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
         
-        openFile.setModes(new IOModes(runtime, "w+"));
+        openFile.setMode(modes.getOpenFileFlags() | OpenFile.SYNC);
+        openFile.setProcess(process);
 
         try {
-            SplitChannel channel = new SplitChannel(
-                    Channels.newChannel(process.getInputStream()),
-                    Channels.newChannel(process.getOutputStream()));
-    	    openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(channel, getNewFileno()),openFile.getModes()));
-        } catch (IOException e) {
-            throw runtime.newIOError(e.getMessage());
+            InputStream pipeIn = process.getInputStream();
+            ChannelDescriptor main = new ChannelDescriptor(
+                    Channels.newChannel(pipeIn),
+                    getNewFileno(),
+                    new FileDescriptor());
+            
+            OutputStream pipeOut = process.getOutputStream();
+            ChannelDescriptor pipe = new ChannelDescriptor(
+                    Channels.newChannel(pipeOut),
+                    getNewFileno(),
+                    new FileDescriptor());
+            
+            if (!openFile.isReadable()) {
+                main.close();
+                pipeIn.close();
+            } else {
+                openFile.setMainStream(new ChannelStream(getRuntime(), main));
+            }
+            
+            if (!openFile.isWritable()) {
+                pipe.close();
+                pipeOut.close();
+            } else {
+                if (openFile.getMainStream() != null) {
+                    openFile.setPipeStream(new ChannelStream(getRuntime(), pipe));
+                } else {
+                    openFile.setMainStream(new ChannelStream(getRuntime(), pipe));
+                }
+            }
+            
+            registerDescriptor(main);
+            registerDescriptor(pipe);
+        } catch (BadDescriptorException ex) {
+            throw getRuntime().newErrnoEBADFError();
+        } catch (IOException ex) {
+            throw getRuntime().newIOErrorFromException(ex);
+        } catch (InvalidValueException e) {
+            throw getRuntime().newErrnoEINVALError();
         }
-    	openFile.setModes(openFile.getMainStream().getModes());
-    	
-    	registerIOHandler(openFile.getMainStream());
     }
     
     public RubyIO(Ruby runtime, STDIO stdio) {
         super(runtime, runtime.getIO());
         
         openFile = new OpenFile();
 
         try {
             switch (stdio) {
             case IN:
-                openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(runtime.getIn()), 0), new IOModes(runtime, IOModes.RDONLY), FileDescriptor.in));
+                openFile.setMainStream(
+                        new ChannelStream(
+                            runtime, 
+                            new ChannelDescriptor(Channels.newChannel(runtime.getIn()), 0, new ModeFlags(ModeFlags.RDONLY), FileDescriptor.in),
+                            FileDescriptor.in));
                 break;
             case OUT:
-                openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(runtime.getOut()), 1), new IOModes(runtime, IOModes.WRONLY | IOModes.APPEND), FileDescriptor.out));
+                openFile.setMainStream(
+                        new ChannelStream(
+                            runtime, 
+                            new ChannelDescriptor(Channels.newChannel(runtime.getOut()), 1, new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out),
+                            FileDescriptor.out));
                 openFile.getMainStream().setSync(true);
                 break;
             case ERR:
-                openFile.setMainStream(new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(runtime.getErr()), 2), new IOModes(runtime, IOModes.WRONLY | IOModes.APPEND), FileDescriptor.err));
+                openFile.setMainStream(
+                        new ChannelStream(
+                            runtime, 
+                            new ChannelDescriptor(Channels.newChannel(runtime.getErr()), 2, new ModeFlags(ModeFlags.WRONLY | ModeFlags.APPEND), FileDescriptor.out), 
+                            FileDescriptor.err));
                 openFile.getMainStream().setSync(true);
                 break;
             }
-        } catch (IOException e) {
-            throw runtime.newErrnoEBADFError();
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
         }
         
-        openFile.setModes(openFile.getMainStream().getModes());
+        openFile.setMode(openFile.getMainStream().getModes().getOpenFileFlags());
         
-        registerIOHandler(openFile.getMainStream());        
+        registerDescriptor(openFile.getMainStream().getDescriptor());        
     }
     
-    public static Stream handlerForFileno(Ruby runtime, int fileno) throws BadDescriptorException, IOException {
-        switch (fileno) {
-        case 0:
-            return new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(runtime.getIn()), fileno), FileDescriptor.in);
-        case 1:
-            return new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(runtime.getOut()), fileno), FileDescriptor.out);
-        case 2:
-            return new ChannelStream(runtime, new ChannelDescriptor(Channels.newChannel(runtime.getErr()), fileno), FileDescriptor.err);
-        default:
-            throw new BadDescriptorException();
-        }
+    public OpenFile getOpenFile() {
+        return openFile;
+    }
+    
+    protected OpenFile getOpenFileChecked() {
+        openFile.checkClosed(getRuntime());
+        return openFile;
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
-                public boolean isKindOf(IRubyObject obj, RubyModule type) {
-                    return obj instanceof RubyIO;
-                }
-            };
+            @Override
+            public boolean isKindOf(IRubyObject obj, RubyModule type) {
+                return obj instanceof RubyIO;
+            }
+        };
 
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
 
-    /*
-     * See checkReadable for commentary.
-     */
-    protected void checkWriteable() {
-        if (!isOpen() || !openFile.modes.isWritable()) {
-            throw getRuntime().newIOError("not opened for writing");
-        }
-    }
-
-    /*
-     * What the IO object "thinks" it can do.  If two IO objects share
-     * the same fileno (IOHandler), then it is possible for one to pull
-     * the rug out from the other.  This will make the second object still
-     * "think" that the file is open.  Secondly, if two IO objects share
-     * the same fileno, but the second one only has a subset of the access
-     * permissions, then it will "think" that it cannot do certain 
-     * operations.
-     */
-    protected void checkReadable() {
-        if (!isOpen() || !openFile.modes.isReadable()) {
-            throw getRuntime().newIOError("not opened for reading");            
-        }
-    }
-    
-    public boolean isOpen() {
-        return isOpen;
-    }
-
     public OutputStream getOutStream() {
-        return openFile.getMainStream().newOutputStream();
+        return getOpenFileChecked().getMainStream().newOutputStream();
     }
 
     public InputStream getInStream() {
-        return openFile.getMainStream().newInputStream();
+        return getOpenFileChecked().getMainStream().newInputStream();
     }
 
     public Channel getChannel() {
-        if (openFile.getMainStream() instanceof ChannelStream) {
+        if (getOpenFileChecked().getMainStream() instanceof ChannelStream) {
             return ((ChannelStream) openFile.getMainStream()).getDescriptor().getChannel();
         } else {
             return null;
         }
     }
     
     public Stream getHandler() {
-        return openFile.getMainStream();
+        return getOpenFileChecked().getMainStream();
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
-    public IRubyObject reopen(IRubyObject[] args) {
+    public IRubyObject reopen(IRubyObject[] args) throws InvalidValueException {
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
 
-                OpenFile originalFile = ios.openFile;
-                OpenFile selfFile = openFile;
+                OpenFile originalFile = ios.getOpenFileChecked();
+                OpenFile selfFile = getOpenFileChecked();
 
-                long position = 0;
-                if (originalFile.getModes().isReadable()) {
-                    position = originalFile.getMainStream().fgetpos();
+                long pos = 0;
+                if (originalFile.isReadable()) {
+                    pos = originalFile.getMainStream().fgetpos();
                 }
 
                 if (originalFile.getPipeStream() != null) {
                     originalFile.getPipeStream().fflush();
-                } else if (originalFile.getModes().isWritable()) {
+                } else if (originalFile.isWritable()) {
                     originalFile.getMainStream().fflush();
                 }
 
-                if (selfFile.getModes().isWritable()) {
-                    if (selfFile.getPipeStream() != null) {
-                        selfFile.getPipeStream().fflush();
-                    } else {
-                        selfFile.getMainStream().fflush();
-                    }
+                if (selfFile.isWritable()) {
+                    selfFile.getWriteStream().fflush();
                 }
 
-                selfFile.setModes(originalFile.getModes());
-                selfFile.setPid(originalFile.getPid());
+                selfFile.setMode(originalFile.getMode());
+                selfFile.setProcess(originalFile.getProcess());
                 selfFile.setLineNumber(originalFile.getLineNumber());
                 selfFile.setPath(originalFile.getPath());
                 selfFile.setFinalizer(originalFile.getFinalizer());
 
                 ChannelDescriptor selfDescriptor = selfFile.getMainStream().getDescriptor();
                 ChannelDescriptor originalDescriptor = originalFile.getMainStream().getDescriptor();
 
                 // confirm we're not reopening self's channel
                 if (selfDescriptor.getChannel() != originalDescriptor.getChannel()) {
-                    // this is our poor-man's check for stdio; Ruby appears to save stdio streams
-                    // for posterity to handle the stdio streams differently
+                    // check if we're a stdio IO, and ensure we're not badly mutilated
                     if (selfDescriptor.getFileno() >=0 && selfDescriptor.getFileno() <= 2) {
-                        // TODO: this should turn self's channel (backed by stdio) into target using same fileno
-                        // and dup the target channel. This saves fileno, but doesn't actually dup channel
-                        selfDescriptor.setChannel(originalDescriptor.getChannel());
+                        selfFile.getMainStream().clearerr();
+                        
+                        // dup2 new fd into self to preserve fileno and references to it
+                        originalDescriptor.dup2(selfDescriptor);
+                        
+                        // re-register, since fileno points at something new now
+                        registerDescriptor(selfFile.getMainStream().getDescriptor());
                     } else {
                         Stream pipeFile = selfFile.getPipeStream();
-                        IOModes modes = selfFile.getModes();
+                        int mode = selfFile.getMode();
                         selfFile.getMainStream().fclose();
                         selfFile.setPipeStream(null);
 
                         // TODO: turn off readable? am I reading this right?
                         // This only seems to be used while duping below, since modes gets
                         // reset to actual modes afterward
                         //fptr->mode &= (m & FMODE_READABLE) ? ~FMODE_READABLE : ~FMODE_WRITABLE;
 
-                        // TODO: dup the original channel into self's channel with same fileno
                         if (pipeFile != null) {
-                            // TODO: new main handler is original handler opened in 'r' mode
-                            //selfFile.handler = new IOHandlerNio(getRuntime(), originalFile.handler.getChannel(), originalFile.handler.getFileno(), IOModes.RDONLY);
+                            selfFile.setMainStream(ChannelStream.fdopen(getRuntime(), originalDescriptor, new ModeFlags()));
                             selfFile.setPipeStream(pipeFile);
                         } else {
-                            selfFile.setMainStream(new ChannelStream(getRuntime(), new ChannelDescriptor(originalDescriptor.getChannel(), selfDescriptor.getFileno())));
+                            selfFile.setMainStream(
+                                    new ChannelStream(
+                                        getRuntime(),
+                                        originalDescriptor.dup2(selfDescriptor.getFileno())));
+                            
+                            // re-register the descriptor
+                            registerDescriptor(selfFile.getMainStream().getDescriptor());
                             
                             // since we're not actually duping the incoming channel into our handler, we need to
                             // copy the original sync behavior from the other handler
                             selfFile.getMainStream().setSync(selfFile.getMainStream().isSync());
                         }
-                        selfFile.setModes(modes);
+                        selfFile.setMode(mode);
                     }
                     // TODO: anything threads attached to original fd are notified of the close...
                     // see rb_thread_fd_close
+                    
+                    if (originalFile.isReadable() && pos >= 0) {
+                        selfFile.seek(pos, Stream.SEEK_SET);
+                        originalFile.seek(pos, Stream.SEEK_SET);
+                    }
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
-
-                // When we reopen, we want our fileno to be preserved even
-                // though we have a new IOHandler.
-                // Note: When we clone we get a new fileno...then we replace it.
-                // This ends up incrementing our fileno index up, which makes the
-                // fileno we choose different from ruby.  Since this seems a bit
-                // too implementation specific, I did not bother trying to get
-                // these to agree (what scary code would depend on fileno generating
-                // a particular way?)
                 
-                // TODO: set our metaclass to target's class (i.e. scary!)
-
-                // Update fileno list with our new handler
-                registerIOHandler(selfFile.getMainStream());
-
                 // TODO: restore binary mode
     //            if (fptr->mode & FMODE_BINMODE) {
     //                rb_io_binmode(io);
     //            }
+                
+                // TODO: set our metaclass to target's class (i.e. scary!)
+
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
-                openFile.setModes(new IOModes(getRuntime()));
             }
             
-            if (args.length > 1) {
-                IRubyObject modeString = args[1].convertToString();
+            try {
+                ModeFlags modes;
+                if (args.length > 1) {
+                    IRubyObject modeString = args[1].convertToString();
+                    modes = getIOModes(getRuntime(), modeString.toString());
+
+                    openFile.setMode(modes.getOpenFileFlags());
+                } else {
+                    modes = getIOModes(getRuntime(), "r");
+                }
+
+                String path = pathString.toString();
                 
-                openFile.setModes(new IOModes(getRuntime(), modeString.toString()));
-            }
-            
-            String path = pathString.toString();
-            openFile.setPath(path);
+                // Ruby code frequently uses a platform check to choose "NUL:" on windows
+                // but since that check doesn't work well on JRuby, we help it out
+                if ("/dev/null".equals(path) || System.getProperty("os.name").contains("Windows")) {
+                    path = "NUL:";
+                }
+                
+                openFile.setPath(path);
             
-            try {
                 if (openFile.getMainStream() == null) {
-                    if ("/dev/null".equals(path) && System.getProperty("os.name").contains("Windows")) {
-                        path = "NUL:";
+                    try {
+                        openFile.setMainStream(ChannelStream.fopen(getRuntime(), path, modes));
+                    } catch (FileExistsException fee) {
+                        throw getRuntime().newErrnoEEXISTError(path);
                     }
-                    openFile.setMainStream(ChannelStream.fopen(getRuntime(), path, openFile.getModes()));
-                    isOpen = true;
                     
-                    registerIOHandler(openFile.getMainStream());
+                    registerDescriptor(openFile.getMainStream().getDescriptor());
                     if (openFile.getPipeStream() != null) {
                         openFile.getPipeStream().fclose();
-                        unregisterIOHandler(openFile.getPipeStream().getDescriptor().getFileno());
+                        unregisterDescriptor(openFile.getPipeStream().getDescriptor().getFileno());
                         openFile.setPipeStream(null);
                     }
                     return this;
                 } else {
-                    // Ruby code frequently uses a platform check to choose "NUL:" on windows
-                    // but since that check doesn't work well on JRuby, we help it out
-                    if ("/dev/null".equals(path)) {
-                        if (System.getProperty("os.name").contains("Windows")) {
-                            path = "NUL:";
-                        }
-                    }
-                    
                     // TODO: This is an freopen in MRI, this is close, but not quite the same
-                    openFile.getMainStream().freopen(path,openFile.getModes());
+                    openFile.getMainStream().freopen(path, getIOModes(getRuntime(), openFile.getModeAsString(getRuntime())));
 
                     // re-register
-                    registerIOHandler(openFile.getMainStream());
+                    registerDescriptor(openFile.getMainStream().getDescriptor());
 
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
-            } catch (Stream.InvalidValueException e) {
+            } catch (InvalidValueException e) {
             	throw getRuntime().newErrnoEINVALError();
             }
         }
         
         // A potentially previously close IO is being 'reopened'.
-        isOpen = true;
         return this;
     }
     
+    public static ModeFlags getIOModes(Ruby runtime, String modesString) throws InvalidValueException {
+        return new ModeFlags(getIOModesIntFromString(runtime, modesString));
+    }
+        
+    public static int getIOModesIntFromString(Ruby runtime, String modesString) {
+        int modes = 0;
+        int length = modesString.length();
+
+        if (length == 0) {
+            throw runtime.newArgumentError("illegal access mode");
+        }
+
+        switch (modesString.charAt(0)) {
+        case 'r' :
+            modes |= ModeFlags.RDONLY;
+            break;
+        case 'a' :
+            modes |= ModeFlags.APPEND | ModeFlags.WRONLY | ModeFlags.CREAT;
+            break;
+        case 'w' :
+            modes |= ModeFlags.WRONLY | ModeFlags.TRUNC | ModeFlags.CREAT;
+            break;
+        default :
+            throw runtime.newArgumentError("illegal access mode " + modes);
+        }
+
+        for (int n = 1; n < length; n++) {
+            switch (modesString.charAt(n)) {
+            case 'b':
+                modes |= ModeFlags.BINARY;
+                break;
+            case '+':
+                modes = (modes & ~ModeFlags.ACCMODE) | ModeFlags.RDWR;
+                break;
+            default:
+                throw runtime.newArgumentError("illegal access mode " + modes);
+            }
+        }
+
+        return modes;
+    }
+    
     private ByteList getSeparatorForGets(IRubyObject[] args) {
         IRubyObject sepVal;
 
         if (args.length > 0) {
             sepVal = args[0];
         } else {
             sepVal = getRuntime().getRecordSeparatorVar().get();
         }
         
         ByteList separator = sepVal.isNil() ? null : ((RubyString) sepVal).getByteList();
 
         if (separator != null && separator.realSize == 0) {
             separator = Stream.PARAGRAPH_DELIMETER;
         }
         
         return separator;
     }
 
-    /** Read a line.
-     * 
-     */
-    // TODO: Most things loop over this and always pass it the same arguments
-    // meaning they are an invariant of the loop.  Think about fixing this.
-    public IRubyObject internalGets(IRubyObject[] args) {
-        return internalGets(getSeparatorForGets(args));
-    }
+    public IRubyObject getline(ByteList separator) {
+        try {
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            myOpenFile.checkReadable(getRuntime());
+            boolean isParagraph = separator == Stream.PARAGRAPH_DELIMETER;
+            separator = (separator == Stream.PARAGRAPH_DELIMETER) ?
+                    Stream.PARAGRAPH_SEPARATOR : separator;
+            
+            if (isParagraph) {
+                swallow('\n');
+            }
+            
+            if (separator == null) {
+                IRubyObject str = readAll(null);
+                if (((RubyString)str).getByteList().length() == 0) {
+                    return getRuntime().getNil();
+                }
+                return str;
+            } else if (separator.length() == 1) {
+                return getlineFast(separator.get(0));
+            } else {
+                Stream readStream = myOpenFile.getMainStream();
+                int c = -1;
+                int newline = separator.get(separator.length() - 1);
 
-    public IRubyObject internalGets(ByteList separator) {
-        checkReadable();
-        
-        try {		
-            ByteList newLine = openFile.getMainStream().fgets(separator);
+                ByteList buf = new ByteList(1024);
+                boolean update = false;
+                
+                while (true) {
+                    do {
+                        readCheck(readStream);
+                        readStream.clearerr();
+
+                        try {
+                            c = readStream.fgetc();
+                        } catch (EOFException e) {
+                            c = -1;
+                        }
 
-            if (newLine != null) {
-                openFile.setLineNumber(openFile.getLineNumber() + 1);
-                getRuntime().getGlobalVariables().set("$.", getRuntime().newFixnum(openFile.getLineNumber()));
-                RubyString result = RubyString.newString(getRuntime(), newLine);
-                result.taint();
+                        if (c == -1) {
+                            // TODO: clear error, wait for it to become readable
+                            if (c == -1) {
+                                if (update) {
+                                    return RubyString.newString(getRuntime(), buf);
+                                }
+                                break;
+                            }
+                        }
+                        
+                        buf.append(c);
+            
+                        update = true;
+                    } while (c != newline); // loop until we see the nth separator char
+                    
+                    // if we hit EOF, we're done
+                    if (c == -1) {
+                        break;
+                    }
+                    
+                    // if we've found the last char of the separator,
+                    // and we've found at least as many characters as separator length,
+                    // and the last n characters of our buffer match the separator, we're done
+                    if (c == newline && buf.length() >= separator.length() &&
+                            0 == ByteList.memcmp(buf.unsafeBytes(), buf.begin + buf.realSize - separator.length(), separator.unsafeBytes(), separator.begin, separator.realSize)) {
+                        break;
+                    }
+                }
+                
+                if (isParagraph) {
+                    if (c != -1) {
+                        swallow('\n');
+                    }
+                }
+                
+                if (!update) {
+                    return getRuntime().getNil();
+                } else {
+                    myOpenFile.setLineNumber(myOpenFile.getLineNumber() + 1);
+                    // this is for a range check, near as I can tell
+                    RubyNumeric.int2fix(getRuntime(), myOpenFile.getLineNumber());
+                    RubyString str = RubyString.newString(getRuntime(), buf);
+                    str.setTaint(true);
 
-                return result;
+                    return str;
+                }
             }
-		    
-            return getRuntime().getNil();
+        } catch (PipeException ex) {
+            throw getRuntime().newErrnoEPIPEError();
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
         } catch (EOFException e) {
             return getRuntime().getNil();
-        } catch (Stream.BadDescriptorException e) {
+        } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
+    
+    protected boolean swallow(int term) throws IOException, BadDescriptorException {
+        Stream readStream = openFile.getMainStream();
+        int c;
+        
+        do {
+            readCheck(readStream);
+            
+            try {
+                c = readStream.fgetc();
+            } catch (EOFException e) {
+                c = -1;
+            }
+            
+            if (c != term) {
+                readStream.ungetc(c);
+                return true;
+            }
+        } while (c != -1);
+        
+        return false;
+    }
+    
+    public IRubyObject getlineFast(int delim) throws IOException, BadDescriptorException {
+        Stream readStream = openFile.getMainStream();
+        int c = -1;
+
+        ByteList buf = new ByteList(1024);
+        boolean update = false;
+        do {
+            readCheck(readStream);
+            readStream.clearerr();
+
+            try {
+                c = readStream.fgetc();
+            } catch (EOFException e) {
+                c = -1;
+            }
+
+            if (c == -1) {
+                // TODO: clear error, wait for it to become readable
+                if (c == -1) {
+                    if (update) {
+                        return RubyString.newString(getRuntime(), buf);
+                    }
+                    break;
+                }
+            }
+            
+            buf.append(c);
+            
+            update = true;
+        } while (c != delim);
+
+        if (!update) {
+            return getRuntime().getNil();
+        } else {
+            openFile.setLineNumber(openFile.getLineNumber() + 1);
+            // this is for a range check, near as I can tell
+            RubyNumeric.int2fix(getRuntime(), openFile.getLineNumber());
+            RubyString str = RubyString.newString(getRuntime(), buf);
+            str.setTaint(true);
+
+            return str;
+        }
+    }
     // IO class methods.
 
+    @JRubyMethod(name = {"new"}, rest = true, frame = true, meta = true)
+    public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
+        RubyClass klass = (RubyClass)recv;
+        
+        if (block.isGiven()) {
+            String className = klass.getName();
+            recv.getRuntime().getWarnings().warn(
+                    ID.BLOCK_NOT_ACCEPTED,
+                    className + "::new() does not take block; use " + className + "::open() instead",
+                    className + "::open()");
+        }
+        
+        return klass.newInstance(args, block);
+    }
+
     @JRubyMethod(name = "initialize", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
-        if (unusedBlock.isGiven()) {
-            getRuntime().getWarnings().warn(ID.BLOCK_NOT_ACCEPTED,
-                    "IO::new() does not take block; use IO::open() instead", "IO::new()");
-        }
+        int argCount = args.length;
+        ModeFlags modes;
+        
+        int fileno = RubyNumeric.fix2int(args[0]);
+        
+        try {
+            ChannelDescriptor descriptor = getDescriptorByFileno(fileno);
+            
+            if (descriptor == null) {
+                throw getRuntime().newErrnoEBADFError();
+            }
+            
+            descriptor.checkOpen();
+            
+            if (argCount == 2) {
+                if (args[1] instanceof RubyFixnum) {
+                    modes = new ModeFlags(RubyFixnum.fix2long(args[1]));
+                } else {
+                    modes = getIOModes(getRuntime(), (args[1].convertToString().toString()));
+                }
+            } else {
+                // use original modes
+                modes = descriptor.getOriginalModes();
+            }
 
-        int count = args.length;
-        int newFileno = RubyNumeric.fix2int(args[0]);
-        String mode = null;
+            openFile.setMode(modes.getOpenFileFlags());
         
-        if (count > 1) {
-            mode = args[1].convertToString().toString();
+            openFile.setMainStream(fdopen(descriptor, modes));
+        } catch (BadDescriptorException ex) {
+            throw getRuntime().newErrnoEBADFError();
+        } catch (InvalidValueException ive) {
+            throw getRuntime().newErrnoEINVALError();
         }
-
+        
+        return this;
+    }
+    
+    protected Stream fdopen(ChannelDescriptor existingDescriptor, ModeFlags modes) throws InvalidValueException {
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
-        Stream existingIOHandler = getIOHandlerByFileno(newFileno);
         
-        if (existingIOHandler == null) {
+        if (existingDescriptor == null) {
+            // redundant, done above as well
+            
             // this seems unlikely to happen unless it's a totally bogus fileno
             // ...so do we even need to bother trying to create one?
-            if (mode == null) {
-                mode = "r";
-            }
             
-            try {
-                openFile.setMainStream(handlerForFileno(getRuntime(), newFileno));
-            } catch (BadDescriptorException e) {
-                throw getRuntime().newErrnoEBADFError();
-            } catch (IOException e) {
-                throw getRuntime().newErrnoEBADFError();
-            }
-            //modes = new IOModes(getRuntime(), mode);
+            // IN FACT, we should probably raise an error, yes?
+            throw getRuntime().newErrnoEBADFError();
             
-            registerIOHandler(openFile.getMainStream());
+//            if (mode == null) {
+//                mode = "r";
+//            }
+//            
+//            try {
+//                openFile.setMainStream(streamForFileno(getRuntime(), fileno));
+//            } catch (BadDescriptorException e) {
+//                throw getRuntime().newErrnoEBADFError();
+//            } catch (IOException e) {
+//                throw getRuntime().newErrnoEBADFError();
+//            }
+//            //modes = new IOModes(getRuntime(), mode);
+//            
+//            registerStream(openFile.getMainStream());
         } else {
             // We are creating a new IO object that shares the same
-            // IOHandler (and fileno).  
-            openFile.setMainStream(existingIOHandler);
-            
-            // Inherit if no mode specified otherwise create new one
-            openFile.setModes(mode == null ? openFile.mainStream.getModes() : new IOModes(getRuntime(), mode));
-
-            // Reset file based on modes.
-            try {
-                openFile.getMainStream().reset(openFile.getModes());
-            } catch (PipeException pe) {
-                throw getRuntime().newErrnoEPIPEError();
-            } catch (BadDescriptorException bde) {
-                throw getRuntime().newErrnoEBADFError();
-            } catch (Stream.InvalidValueException e) {
-            	throw getRuntime().newErrnoEINVALError();
-            } catch (IOException e) {
-                throw getRuntime().newIOError(e.getMessage());
-            }
+            // IOHandler (and fileno).
+            return ChannelStream.fdopen(getRuntime(), existingDescriptor, modes);
         }
-        
-        return this;
     }
 
-    @JRubyMethod(name = "open", required = 1, optional = 1, frame = true, meta = true)
+    @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
-        RubyIO io = new RubyIO(runtime, (RubyClass) recv);
-        io.initialize(args, block);
+        RubyClass klass = (RubyClass)recv;
+        
+        RubyIO io = (RubyIO)klass.newInstance(args, block);
 
         if (block.isGiven()) {
             try {
                 return block.yield(runtime.getCurrentContext(), io);
             } finally {
-                if (io.isOpen()) {
+                if (io.openFile.isOpen()) {
                     io.close();
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
-
+    
+    protected void checkInitialized() {
+        if (openFile == null) {
+            throw getRuntime().newIOError("uninitialized stream");
+        }
+    }
+    
+    protected void checkClosed() {
+        if (openFile.getMainStream() == null && openFile.getPipeStream() == null) {
+            throw getRuntime().newIOError("closed stream");
+        }
+    }
+    
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(IRubyObject obj) {
         try {
-            if (obj instanceof RubyString) {
-                return getRuntime().newFixnum(openFile.getMainStream().write(((RubyString)obj).getByteList()));
-            } else {
-                // FIXME: unlikely to be efficient, but probably correct
-                return getRuntime().newFixnum(
-                        openFile.getMainStream().write(
-                        ((RubyString)obj.callMethod(
-                            obj.getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s")).getByteList()));
+            RubyString string = obj.convertToString();
+            
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            myOpenFile.checkWritable(getRuntime());
+            
+            Stream writeStream = myOpenFile.getWriteStream();
+            
+            if (myOpenFile.isWriteBuffered()) {
+                getRuntime().getWarnings().warn(
+                        ID.SYSWRITE_BUFFERED_IO,
+                        "syswrite for buffered IO");
+            }
+            
+            if (!writeStream.getDescriptor().isWritable()) {
+                myOpenFile.checkClosed(getRuntime());
             }
-        } catch (Stream.BadDescriptorException e) {
+            
+            int read = writeStream.getDescriptor().write(string.getByteList());
+            
+            if (read == -1) {
+                // TODO? I think this ends up propagating from normal Java exceptions
+                // sys_fail(openFile.getPath())
+            }
+            
+            return getRuntime().newFixnum(read);
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (PipeException ex) {
+            throw getRuntime().newErrnoEPIPEError();
+        } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
+            e.printStackTrace();
             throw getRuntime().newSystemCallError(e.getMessage());
         }
     }
     
+    @JRubyMethod(name = "write_nonblock", required = 1)
+    public IRubyObject write_nonblock(IRubyObject obj) {
+        // TODO: Obviously, we're not doing a non-blocking write here
+        return write(obj);
+    }
+    
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(IRubyObject obj) {
+        getRuntime().secure(4);
+        
+        RubyString str;
+        if (obj instanceof RubyString) {
+            str = (RubyString)obj;
+        } else {
+            str = (RubyString)obj.asString();
+        }
+        
+        // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
+        
+        if (str.getByteList().length() == 0) {
+            return getRuntime().newFixnum(0);
+        }
+
         try {
-            ByteList objBytes = obj.asString().getByteList();
-            if (objBytes.length() == 0) {
-                // nothing to write, just return
-                return RubyFixnum.zero(getRuntime());
-            } else {
-                checkWriteable();
-                return getRuntime().newFixnum(openFile.getMainStream().fwrite(objBytes));
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            myOpenFile.checkWritable(getRuntime());
+
+            int written = fwrite(str.getByteList());
+
+            if (written == -1) {
+                // TODO: sys fail
             }
-        } catch (Stream.BadDescriptorException e) {
-            return RubyFixnum.zero(getRuntime());
-        } catch (IOException e) {
-            String message = e.getMessage();
-            if(message != null) {
-                if(message.equals("Broken pipe")) {
-                    throw getRuntime().newErrnoEPIPEError();
-                } else if(message.equals("not opened for writing")) {
-                    throw getRuntime().newIOError(message);
-                }
+
+            // if not sync, we switch to write buffered mode
+            if (!myOpenFile.isSync()) {
+                myOpenFile.setWriteBuffered();
             }
 
-            if(getRuntime().getDebug().isTrue()) {
-                getRuntime().getWarnings().warn(ID.SWALLOWED_IO_EXCEPTION, "swallowed IO exception: " + e.toString());
-                e.printStackTrace();
+            return getRuntime().newFixnum(written);
+        } catch (IOException ex) {
+            throw getRuntime().newIOErrorFromException(ex);
+        } catch (BadDescriptorException ex) {
+            throw getRuntime().newErrnoEBADFError();
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (PipeException ex) {
+            throw getRuntime().newErrnoEPIPEError();
+        }
+    }
+    
+    protected int fwrite(ByteList buffer) {
+        int n, r, l, offset = 0;
+        Stream writeStream = openFile.getWriteStream();
+
+        int len = buffer.length();
+        
+//        if ((n = len) <= 0) return n;
+        if (len == 0) return 0;
+        
+        try {
+            if (openFile.isSync()) {
+                openFile.fflush(writeStream);
+
+                // TODO: why is this guarded?
+    //            if (!rb_thread_fd_writable(fileno(f))) {
+    //                rb_io_check_closed(fptr);
+    //            }
+                // TODO: loop until it's all written
+                //while (offset < len) {
+                    writeStream.getDescriptor().write(buffer);
+                //}
+                return len;
+
+                // TODO: all this stuff...some pipe logic, some async thread stuff
+    //          retry:
+    //            l = n;
+    //            if (PIPE_BUF < l &&
+    //                !rb_thread_critical &&
+    //                !rb_thread_alone() &&
+    //                wsplit_p(fptr)) {
+    //                l = PIPE_BUF;
+    //            }
+    //            TRAP_BEG;
+    //            r = write(fileno(f), RSTRING(str)->ptr+offset, l);
+    //            TRAP_END;
+    //            if (r == n) return len;
+    //            if (0 <= r) {
+    //                offset += r;
+    //                n -= r;
+    //                errno = EAGAIN;
+    //            }
+    //            if (rb_io_wait_writable(fileno(f))) {
+    //                rb_io_check_closed(fptr);
+    //                if (offset < RSTRING(str)->len)
+    //                    goto retry;
+    //            }
+    //            return -1L;
             }
 
-            return RubyFixnum.zero(getRuntime());
+            // TODO: handle errors in buffered write by retrying until finished or file is closed
+            return writeStream.fwrite(buffer);
+    //        while (errno = 0, offset += (r = fwrite(RSTRING(str)->ptr+offset, 1, n, f)), (n -= r) > 0) {
+    //            if (ferror(f)
+    //            ) {
+    //                if (rb_io_wait_writable(fileno(f))) {
+    //                    rb_io_check_closed(fptr);
+    //                    clearerr(f);
+    //                    if (offset < RSTRING(str)->len)
+    //                        continue;
+    //                }
+    //                return -1L;
+    //            }
+    //        }
+
+//            return len - n;
+        } catch (IOException ex) {
+            throw getRuntime().newIOErrorFromException(ex);
+        } catch (BadDescriptorException ex) {
+            throw getRuntime().newErrnoEBADFError();
         }
     }
 
     /** rb_io_addstr
      * 
      */
     @JRubyMethod(name = "<<", required = 1)
-    public IRubyObject op_concat(IRubyObject anObject) {
+    public IRubyObject op_append(IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
-        IRubyObject strObject = anObject.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s");
-
-        write(strObject);
+        callMethod(getRuntime().getCurrentContext(), "write", anObject);
         
         return this; 
     }
 
     @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno() {
-        return getRuntime().newFixnum(openFile.getMainStream().getDescriptor().getFileno());
+        return getRuntime().newFixnum(getOpenFileChecked().getMainStream().getDescriptor().getFileno());
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno() {
-        return getRuntime().newFixnum(openFile.getLineNumber());
+        return getRuntime().newFixnum(getOpenFileChecked().getLineNumber());
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(IRubyObject newLineNumber) {
-        openFile.setLineNumber(RubyNumeric.fix2int(newLineNumber));
+        getOpenFileChecked().setLineNumber(RubyNumeric.fix2int(newLineNumber));
 
         return (RubyFixnum) newLineNumber;
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync() {
-        return getRuntime().newBoolean(openFile.getMainStream().isSync());
+        return getRuntime().newBoolean(getOpenFileChecked().getMainStream().isSync());
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
-        int pid = openFile.getPid();
+        OpenFile myOpenFile = getOpenFileChecked();
+        
+        if (myOpenFile.getProcess() == null) {
+            return getRuntime().getNil();
+        }
+        
+        // Of course this isn't particularly useful.
+        int pid = myOpenFile.getProcess().hashCode();
         
-        return pid == -1 ? getRuntime().getNil() : getRuntime().newFixnum(pid); 
+        return getRuntime().newFixnum(pid); 
     }
     
-    public boolean hasPendingBuffered() {
-        return openFile.getMainStream().hasPendingBuffered();
+    /**
+     * @deprecated
+     * @return
+     */
+    public boolean writeDataBuffered() {
+        return openFile.getMainStream().writeDataBuffered();
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos() {
         try {
-            return getRuntime().newFixnum(openFile.getMainStream().fgetpos());
+            return getRuntime().newFixnum(getOpenFileChecked().getMainStream().fgetpos());
         } catch (BadDescriptorException bde) {
             throw getRuntime().newErrnoEBADFError();
-        } catch (Stream.PipeException e) {
+        } catch (PipeException e) {
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
         
+        OpenFile myOpenFile = getOpenFileChecked();
+        
         try {
-            openFile.getMainStream().fseek(offset, Stream.SEEK_SET);
-        } catch (BadDescriptorException bde) {
-            throw getRuntime().newErrnoEBADFError();
-        } catch (Stream.InvalidValueException e) {
+            myOpenFile.getMainStream().fseek(offset, Stream.SEEK_SET);
+        } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
-        } catch (Stream.PipeException e) {
+        } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
+        myOpenFile.getMainStream().clearerr();
+        
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
-            openFile.getMainStream().fputc(c);
-        } catch (Stream.BadDescriptorException e) {
+            getOpenFileChecked().getMainStream().fputc(c);
+        } catch (BadDescriptorException e) {
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
-        if (args.length == 0) {
-            throw getRuntime().newArgumentError("wrong number of arguments");
+        long offset = RubyNumeric.num2long(args[0]);
+        int whence = Stream.SEEK_SET;
+        
+        if (args.length > 1) {
+            whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
+        OpenFile myOpenFile = getOpenFileChecked();
+        
+        try {
+            myOpenFile.seek(offset, whence);
+        } catch (BadDescriptorException ex) {
+            throw getRuntime().newErrnoEBADFError();
+        } catch (InvalidValueException e) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (PipeException e) {
+            throw getRuntime().newErrnoESPIPEError();
+        } catch (IOException e) {
+            throw getRuntime().newIOError(e.getMessage());
+        }
+        
+        myOpenFile.getMainStream().clearerr();
+        
+        return RubyFixnum.zero(getRuntime());
+    }
+    
+    // This was a getOpt with one mandatory arg, but it did not work
+    // so I am parsing it for now.
+    @JRubyMethod(name = "sysseek", required = 1, optional = 1)
+    public RubyFixnum sysseek(IRubyObject[] args) {
         long offset = RubyNumeric.num2long(args[0]);
-        int type = Stream.SEEK_SET;
+        long pos;
+        int whence = Stream.SEEK_SET;
         
         if (args.length > 1) {
-            type = RubyNumeric.fix2int(args[1].convertToInteger());
+            whence = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
+        OpenFile myOpenFile = getOpenFileChecked();
+        
         try {
-            openFile.getMainStream().fseek(offset, type);
-        } catch (BadDescriptorException bde) {
+            
+            if (myOpenFile.isReadable() && myOpenFile.isReadBuffered()) {
+                throw getRuntime().newIOError("sysseek for buffered IO");
+            }
+            if (myOpenFile.isWritable() && myOpenFile.isWriteBuffered()) {
+                getRuntime().getWarnings().warn(
+                        ID.SYSSEEK_BUFFERED_IO,
+                        "sysseek for buffered IO");
+            }
+            
+            pos = myOpenFile.getMainStream().getDescriptor().lseek(offset, whence);
+        } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
-        } catch (Stream.InvalidValueException e) {
-        	throw getRuntime().newErrnoEINVALError();
-        } catch (Stream.PipeException e) {
-        	throw getRuntime().newErrnoESPIPEError();
+        } catch (InvalidValueException e) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (PipeException e) {
+            throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
-        return RubyFixnum.zero(getRuntime());
+        myOpenFile.getMainStream().clearerr();
+        
+        return getRuntime().newFixnum(pos);
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind() {
+        OpenFile myOpenfile = getOpenFileChecked();
+        
         try {
-            openFile.getMainStream().rewind();
-        } catch (BadDescriptorException bde) {
-            throw getRuntime().newErrnoEBADFError();
-        } catch (Stream.InvalidValueException e) {
+            myOpenfile.getMainStream().fseek(0L, Stream.SEEK_SET);
+            myOpenfile.getMainStream().clearerr();
+            
+            // TODO: This is some goofy global file value from MRI..what to do?
+//            if (io == current_file) {
+//                gets_lineno -= fptr->lineno;
+//            }
+        } catch (InvalidValueException e) {
             throw getRuntime().newErrnoEINVALError();
-        } catch (Stream.PipeException e) {
+        } catch (PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         // Must be back on first line on rewind.
-        openFile.setLineNumber(0);
+        myOpenfile.setLineNumber(0);
         
         return RubyFixnum.zero(getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync() {
-        checkWriteable();
-
         try {
-            openFile.getMainStream().sync();
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            myOpenFile.checkWritable(getRuntime());
+        
+            myOpenFile.getWriteStream().sync();
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (PipeException ex) {
+            throw getRuntime().newErrnoEPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
-        } catch (Stream.BadDescriptorException e) {
+        } catch (BadDescriptorException e) {
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
-        openFile.getMainStream().setSync(newSync.isTrue());
+        getOpenFileChecked().getMainStream().setSync(newSync.isTrue());
 
         return this;
     }
 
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p() {
         try {
-            boolean isEOF = openFile.getMainStream().feof(); 
-            return isEOF ? getRuntime().getTrue() : getRuntime().getFalse();
-        } catch (Stream.BadDescriptorException e) {
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            myOpenFile.checkReadable(getRuntime());
+            
+            if (myOpenFile.getMainStream().feof()) {
+                return getRuntime().getTrue();
+            }
+            
+            if (myOpenFile.getMainStream().readDataBuffered()) {
+                return getRuntime().getFalse();
+            }
+            
+            readCheck(myOpenFile.getMainStream());
+            
+            myOpenFile.getMainStream().clearerr();
+            
+            int c = myOpenFile.getMainStream().fgetc();
+            
+            if (c != -1) {
+                myOpenFile.getMainStream().ungetc(c);
+                return getRuntime().getFalse();
+            }
+            
+            myOpenFile.checkClosed(getRuntime());
+            
+            myOpenFile.getMainStream().clearerr();
+            
+            return getRuntime().getTrue();
+        } catch (PipeException ex) {
+            throw getRuntime().newErrnoEPIPEError();
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty"})
     public RubyBoolean tty_p() {
-        return getRuntime().newBoolean(getRuntime().getPosix().isatty(openFile.getMainStream().getFD()));
+        return getRuntime().newBoolean(getRuntime().getPosix().isatty(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor()));
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
+    @Override
     public IRubyObject initialize_copy(IRubyObject original){
         if (this == original) return this;
 
-        RubyIO originalIO = (RubyIO) original;
+        RubyIO originalIO = (RubyIO) TypeConverter.convertToTypeWithCheck(original, getRuntime().getIO(), MethodIndex.TO_IO, "to_io");
         
-        OpenFile originalFile = originalIO.openFile;
+        OpenFile originalFile = originalIO.getOpenFileChecked();
         OpenFile newFile = openFile;
         
         try {
+            // TODO: I didn't see where MRI has this check, but it seems to be the right place
+            originalFile.checkClosed(getRuntime());
+            
             if (originalFile.getPipeStream() != null) {
                 originalFile.getPipeStream().fflush();
                 originalFile.getMainStream().fseek(0, Stream.SEEK_CUR);
-            } else if (originalFile.getModes().isWritable()) {
+            } else if (originalFile.isWritable()) {
                 originalFile.getMainStream().fflush();
             } else {
                 originalFile.getMainStream().fseek(0, Stream.SEEK_CUR);
             }
 
-            newFile.setModes(originalFile.getModes());
-            newFile.setPid(originalFile.getPid());
+            newFile.setMode(originalFile.getMode());
+            newFile.setProcess(originalFile.getProcess());
             newFile.setLineNumber(originalFile.getLineNumber());
             newFile.setPath(originalFile.getPath());
-
-            String fdMode = null;
-            if (newFile.getModes().isReadable()) {
-                if (newFile.getModes().isWritable()) {
-                    if (originalFile.getPipeStream() != null) {
-                        fdMode = "r";
+            newFile.setFinalizer(originalFile.getFinalizer());
+            
+            ModeFlags modes;
+            if (newFile.isReadable()) {
+                if (newFile.isWritable()) {
+                    if (newFile.getPipeStream() != null) {
+                        modes = new ModeFlags(ModeFlags.RDONLY);
                     } else {
-                        fdMode = "r+";
+                        modes = new ModeFlags(ModeFlags.RDWR);
                     }
                 } else {
-                    fdMode = "r";
+                    modes = new ModeFlags(ModeFlags.RDONLY);
                 }
-            } else if (newFile.getModes().isWritable()) {
-                fdMode = "w";
             } else {
-                fdMode = "r";
+                if (newFile.isWritable()) {
+                    modes = new ModeFlags(ModeFlags.WRONLY);
+                } else {
+                    modes = originalFile.getMainStream().getModes();
+                }
             }
+            
+            ChannelDescriptor descriptor = originalFile.getMainStream().getDescriptor().dup();
 
-            // FIXME: use fdMode for the cloned handler's channel (which is sortof our fd)
-
-            // Two pos pointers?  
-            // http://blade.nagaokaut.ac.jp/ruby/ruby-talk/81513
-            // So if I understand this correctly, the descriptor level stuff
-            // shares things like position, but the higher level stuff uses
-            // a different set of libc functions (FILE*), which does not share
-            // position.  Our current implementation implements our higher 
-            // level operations on top of our 'sys' versions.  So we could in
-            // fact share everything.  Unfortunately, we want to clone ruby's
-            // behavior (i.e. show how this interface bleeds their 
-            // implementation). So our best bet, is to just create a yet another
-            // copy of the handler.  In fact, ruby 1.8 must do this as the cloned
-            // resource is in fact a different fileno.  What is clone for again?        
-
-            // FIXME: Missing step here is to clone the channel, which we can't do at present.
-            // Ruby dup's the original file descriptor and then opens it into a new FILE structure
-            // This would be roughly equivalent to us duping the original channel and inserting it
-            // into a new IOHandler. We may be able to get away with just preserving the shared
-            // channel (keeping it open until all referencing handlers have closed it) since
-            // at least one platform appears to share position between the two
-            
-            openFile.setMainStream(new ChannelStream(getRuntime(), new ChannelDescriptor(originalFile.getMainStream().getDescriptor().getChannel(), getNewFileno()), openFile.getModes()));
-            registerIOHandler(openFile.getMainStream());
+            newFile.setMainStream(ChannelStream.fdopen(getRuntime(), descriptor, modes));
+            
+            // TODO: the rest of this...seeking to same position is unnecessary since we share a channel
+            // but some of this may be needed?
+            
+//    fseeko(fptr->f, ftello(orig->f), SEEK_SET);
+//    if (orig->f2) {
+//	if (fileno(orig->f) != fileno(orig->f2)) {
+//	    fd = ruby_dup(fileno(orig->f2));
+//	}
+//	fptr->f2 = rb_fdopen(fd, "w");
+//	fseeko(fptr->f2, ftello(orig->f2), SEEK_SET);
+//    }
+//    if (fptr->mode & FMODE_BINMODE) {
+//	rb_io_binmode(dest);
+//    }
+            
+            // Register the new descriptor
+            registerDescriptor(newFile.getMainStream().getDescriptor());
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
-        return isOpen() ? getRuntime().getFalse() : getRuntime().getTrue();
+        if (openFile.getMainStream() == null && openFile.getPipeStream() == null) {
+            return getRuntime().getTrue();
+        } else {
+            return getRuntime().getFalse();
+        }
     }
 
     /** 
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
-        isOpen = false;
+        if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
+            throw getRuntime().newSecurityError("Insecure: can't close");
+        }
         
-        try {
-            openFile.getMainStream().fclose();
-        } catch (Stream.BadDescriptorException e) {
-            throw getRuntime().newErrnoEBADFError();
-        } catch (IOException e) {
-            throw getRuntime().newIOError(e.getMessage());
+        openFile.checkClosed(getRuntime());
+        return close2();
+    }
+        
+    protected IRubyObject close2() {
+        if (openFile == null) {
+            return getRuntime().getNil();
+        }
+        
+        // These would be used when we notify threads...if we notify threads
+        ChannelDescriptor main, pipe;
+        if (openFile.getPipeStream() != null) {
+            pipe = openFile.getPipeStream().getDescriptor();
+        } else {
+            if (openFile.getMainStream() == null) {
+                return getRuntime().getNil();
+            }
+            pipe = null;
         }
         
-        unregisterIOHandler(openFile.getMainStream().getDescriptor().getFileno());
+        main = openFile.getMainStream().getDescriptor();
         
-        return this;
+        // cleanup, raising errors if any
+        openFile.cleanup(getRuntime(), true);
+        
+        // TODO: notify threads waiting on descriptors/IO? probably not...
+        
+        if (openFile.getProcess() != null) {
+            try {
+                IRubyObject processResult = RubyProcess.RubyStatus.newProcessStatus(getRuntime(), openFile.getProcess().waitFor());
+                getRuntime().getGlobalVariables().set("$?", processResult);
+            } catch (InterruptedException ie) {
+                // TODO: do something here?
+            }
+        }
+        
+        return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "close_write")
-    public IRubyObject close_write() {
+    public IRubyObject close_write() throws BadDescriptorException {
         try {
-            openFile.getMainStream().closeWrite();
-        } catch (BadDescriptorException bde) {
-            throw getRuntime().newErrnoEBADFError();
+            if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
+                throw getRuntime().newSecurityError("Insecure: can't close");
+            }
+            
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            if (myOpenFile.getPipeStream() == null && myOpenFile.isReadable()) {
+                throw getRuntime().newIOError("closing non-duplex IO for writing");
+            }
+            
+            if (myOpenFile.getPipeStream() == null) {
+                close();
+            } else{
+                myOpenFile.getPipeStream().fclose();
+                myOpenFile.setPipeStream(null);
+                myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.WRITABLE);
+                // TODO
+                // n is result of fclose; but perhaps having a SysError below is enough?
+                // if (n != 0) rb_sys_fail(fptr->path);
+            }
         } catch (IOException ioe) {
             // hmmmm
         }
         return this;
     }
 
+    @JRubyMethod(name = "close_read")
+    public IRubyObject close_read() throws BadDescriptorException {
+        try {
+            if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
+                throw getRuntime().newSecurityError("Insecure: can't close");
+            }
+            
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            if (myOpenFile.getPipeStream() == null && myOpenFile.isWritable()) {
+                throw getRuntime().newIOError("closing non-duplex IO for reading");
+            }
+            
+            if (myOpenFile.getPipeStream() == null) {
+                close();
+            } else{
+                myOpenFile.getMainStream().fclose();
+                myOpenFile.setMode(myOpenFile.getMode() & ~OpenFile.READABLE);
+                myOpenFile.setMainStream(myOpenFile.getPipeStream());
+                myOpenFile.setPipeStream(null);
+                // TODO
+                // n is result of fclose; but perhaps having a SysError below is enough?
+                // if (n != 0) rb_sys_fail(fptr->path);
+            }
+        } catch (IOException ioe) {
+            // I believe Ruby bails out with a "bug" if closing fails
+            throw getRuntime().newIOErrorFromException(ioe);
+        }
+        return this;
+    }
+
     /** Flushes the IO output stream.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "flush")
     public RubyIO flush() {
         try { 
-            openFile.getMainStream().fflush();
-        } catch (Stream.BadDescriptorException e) {
+            getOpenFileChecked().getWriteStream().fflush();
+        } catch (BadDescriptorException e) {
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
-        IRubyObject result = internalGets(args);
+        ByteList separator = getSeparatorForGets(args);
+        
+        IRubyObject result = getline(separator);
 
         if (!result.isNil()) getRuntime().getCurrentContext().getCurrentFrame().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
         return ((ChannelStream) openFile.getMainStream()).isBlocking();
-     }
+    }
 
     @JRubyMethod(name = "fcntl", required = 2)
-    public IRubyObject fcntl(IRubyObject cmd, IRubyObject arg) throws IOException {
+    public IRubyObject fcntl(IRubyObject cmd, IRubyObject arg) {
+        // TODO: This version differs from ioctl by checking whether fcntl exists
+        // and raising notimplemented if it doesn't; perhaps no difference for us?
+        return ctl(cmd, arg);
+    }
+
+    @JRubyMethod(name = "ioctl", required = 1, optional = 1)
+    public IRubyObject ioctl(IRubyObject[] args) {
+        IRubyObject cmd = args[0];
+        IRubyObject arg;
+        
+        if (args.length == 2) {
+            arg = args[1];
+        } else {
+            arg = getRuntime().getNil();
+        }
+        
+        return ctl(cmd, arg);
+    }
+
+    public IRubyObject ctl(IRubyObject cmd, IRubyObject arg) {
         long realCmd = cmd.convertToInteger().getLongValue();
+        long nArg = 0;
         
         // FIXME: Arg may also be true, false, and nil and still be valid.  Strangely enough, 
         // protocol conversion is not happening in Ruby on this arg?
-        if (!(arg instanceof RubyNumeric)) return getRuntime().newFixnum(0);
+        if (arg.isNil() || arg == getRuntime().getFalse()) {
+            nArg = 0;
+        } else if (arg instanceof RubyFixnum) {
+            nArg = RubyFixnum.fix2long(arg);
+        } else if (arg == getRuntime().getTrue()) {
+            nArg = 1;
+        } else {
+            throw getRuntime().newNotImplementedError("JRuby does not support string for second fcntl/ioctl argument yet");
+        }
         
-        long realArg = ((RubyNumeric)arg).getLongValue();
+        OpenFile myOpenFile = getOpenFileChecked();
 
         // Fixme: Only F_SETFL is current supported
         if (realCmd == 1L) {  // cmd is F_SETFL
             boolean block = true;
             
-            if ((realArg & IOModes.NONBLOCK) == IOModes.NONBLOCK) {
+            if ((nArg & ModeFlags.NONBLOCK) == ModeFlags.NONBLOCK) {
                 block = false;
             }
 
             try {
-                openFile.getMainStream().setBlocking(block);
+                myOpenFile.getMainStream().setBlocking(block);
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
+        } else {
+            throw getRuntime().newNotImplementedError("JRuby only supports F_SETFL for fcntl/ioctl currently");
         }
         
         return getRuntime().newFixnum(0);
     }
+    
+    private static final ByteList NIL_BYTELIST = ByteList.create("nil");
+    private static final ByteList RECURSIVE_BYTELIST = ByteList.create("[...]");
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(IRubyObject[] args) {
     	ThreadContext context = getRuntime().getCurrentContext();
         
+        assert getRuntime().getGlobalVariables().getDefaultSeparator() instanceof RubyString;
+        RubyString separator = (RubyString)getRuntime().getGlobalVariables().getDefaultSeparator();
+        
         if (args.length == 0) {
-            callMethod(context, "write", getRuntime().newString("\n"));
+            write(context, separator.getByteList());
             return getRuntime().getNil();
         }
 
         for (int i = 0; i < args.length; i++) {
-            String line;
+            ByteList line;
             
             if (args[i].isNil()) {
-                line = "nil";
+                line = NIL_BYTELIST;
             } else if (getRuntime().isInspecting(args[i])) {
-                line = "[...]";
+                line = RECURSIVE_BYTELIST;
             } else if (args[i] instanceof RubyArray) {
                 inspectPuts((RubyArray) args[i]);
                 continue;
             } else {
-                line = args[i].toString();
+                line = args[i].asString().getByteList();
             }
             
-            callMethod(context, "write", getRuntime().newString(line));
+            write(context, line);
             
-            if (!line.endsWith("\n")) {
-                callMethod(context, "write", getRuntime().newString("\n"));
+            if (line.length() == 0 || !line.endsWith(separator.getByteList())) {
+                write(context, separator.getByteList());
             }
         }
         return getRuntime().getNil();
     }
     
+    protected void write(ThreadContext context, ByteList byteList) {
+        callMethod(context, "write", getRuntime().newStringShared(byteList));
+    }
+    
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
-        checkReadable();
-        
         try {
-            int c = openFile.getMainStream().fgetc();
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            myOpenFile.checkReadable(getRuntime());
+
+            Stream stream = myOpenFile.getMainStream();
+            
+            readCheck(stream);
+            stream.clearerr();
+        
+            int c = myOpenFile.getMainStream().fgetc();
+            
+            if (c == -1) {
+                // TODO: check for ferror, clear it, and try once more up above readCheck
+//                if (ferror(f)) {
+//                    clearerr(f);
+//                    if (!rb_io_wait_readable(fileno(f)))
+//                        rb_sys_fail(fptr->path);
+//                    goto retry;
+//                }
+                return getRuntime().getNil();
+            }
         
-            return c == -1 ? getRuntime().getNil() : getRuntime().newFixnum(c);
-        } catch (Stream.BadDescriptorException e) {
+            return getRuntime().newFixnum(c);
+        } catch (PipeException ex) {
+            throw getRuntime().newErrnoEPIPEError();
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
     
+    private void readCheck(Stream stream) {
+        if (!stream.readDataBuffered()) {
+            openFile.checkClosed(getRuntime());
+        }
+    }
+    
     /** 
      * <p>Pushes char represented by int back onto IOS.</p>
      * 
      * @param number to push back
      */
     @JRubyMethod(name = "ungetc", required = 1)
     public IRubyObject ungetc(IRubyObject number) {
-        openFile.getMainStream().ungetc(RubyNumeric.fix2int(number));
+        getOpenFileChecked().getMainStream().ungetc(RubyNumeric.fix2int(number));
 
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
     public IRubyObject readpartial(IRubyObject[] args) {
-        if(!(openFile.mainStream instanceof ChannelStream)) {
+        if(!(openFile.getMainStream() instanceof ChannelStream)) {
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
-        } catch (Stream.BadDescriptorException e) {
+        } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             return getRuntime().getNil();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
-    // FIXME: according to MRI's RI, sysread only takes one arg
     @JRubyMethod(name = "sysread", required = 1, optional = 1)
     public IRubyObject sysread(IRubyObject[] args) {
         int len = (int)RubyNumeric.num2long(args[0]);
         if (len < 0) throw getRuntime().newArgumentError("Negative size");
 
         try {
             RubyString str;
+            ByteList buffer;
             if (args.length == 1 || args[1].isNil()) {
-                if (len == 0) return RubyString.newString(getRuntime(), "");
-                str = RubyString.newString(getRuntime(), openFile.getMainStream().read(len));
+                if (len == 0) {
+                    return RubyString.newStringShared(getRuntime(), ByteList.EMPTY_BYTELIST);
+                }
+                
+                buffer = new ByteList(len);
+                str = RubyString.newString(getRuntime(), buffer);
             } else {
                 str = args[1].convertToString();
+                str.modify(len);
+                
                 if (len == 0) {
-                    str.setValue(new ByteList());
                     return str;
                 }
-                str.setValue(openFile.getMainStream().read(len)); // should preserve same instance
+                
+                buffer = str.getByteList();
+            }
+            
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            myOpenFile.checkReadable(getRuntime());
+            
+            if (myOpenFile.getMainStream().readDataBuffered()) {
+                throw getRuntime().newIOError("sysread for buffered IO");
             }
+            
+            // TODO: Ruby locks the string here
+            
+            getRuntime().getCurrentContext().getThread().beforeBlockingCall();
+            myOpenFile.checkClosed(getRuntime());
+            
+            // TODO: Ruby re-checks that the buffer string hasn't been modified
+            
+            int bytesRead = myOpenFile.getMainStream().getDescriptor().read(len, str.getByteList());
+            
+            // TODO: Ruby unlocks the string here
+            
+            // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?
+            
+            if (bytesRead == -1 || (bytesRead == 0 && len > 0)) {
+                throw getRuntime().newEOFError();
+            }
+            
             str.setTaint(true);
-            return str;
             
-        } catch (Stream.BadDescriptorException e) {
-            throw getRuntime().newIOError("bad descriptor");
+            return str;
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        } catch (InvalidValueException e) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (PipeException e) {
+            throw getRuntime().newErrnoEPIPEError();
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
-    	}
+    	} finally {
+            getRuntime().getCurrentContext().getThread().afterBlockingCall();
+        }
+    }
+    
+    @JRubyMethod(name = "read_nonblock", required = 1, optional = 1)
+    public IRubyObject read_nonblock(IRubyObject[] args) {
+        // TODO: Obviously, we're not doing a nonblocking read here...
+        return read(args);
     }
     
-    @JRubyMethod(name = "read", rest = true)
+    @JRubyMethod(name = "read", optional = 2)
     public IRubyObject read(IRubyObject[] args) {
-        checkReadable();
-
         int argCount = args.length;
-        RubyString callerBuffer = null;
-        boolean readEntireStream = (argCount == 0 || args[0].isNil());
+        
+        OpenFile myOpenFile = getOpenFileChecked();
+        
+        if (argCount == 0 || args[0].isNil()) {
+            try {
+                myOpenFile.checkReadable(getRuntime());
+
+                if (args.length == 2) {
+                    return readAll(args[1]);
+                } else {
+                    return readAll(getRuntime().getNil());
+                }
+            } catch (PipeException ex) {
+                throw getRuntime().newErrnoEPIPEError();
+            } catch (InvalidValueException ex) {
+                throw getRuntime().newErrnoEINVALError();
+            } catch (EOFException ex) {
+                throw getRuntime().newEOFError();
+            } catch (IOException ex) {
+                throw getRuntime().newIOErrorFromException(ex);
+            } catch (BadDescriptorException ex) {
+                throw getRuntime().newErrnoEBADFError();
+            }
+        }
+        
+        int length = RubyNumeric.num2int(args[0]);
+        
+        if (length < 0) {
+            throw getRuntime().newArgumentError("negative length " + length + " given");
+        }
+        
+        RubyString str = null;
+//        ByteList buffer = null;
+        if (args.length == 1 || args[1].isNil()) {
+//            buffer = new ByteList(length);
+//            str = RubyString.newString(getRuntime(), buffer);
+        } else {
+            str = args[1].convertToString();
+            str.modify(length);
+
+            if (length == 0) {
+                return str;
+            }
+
+//            buffer = str.getByteList();
+        }
 
         try {
-            // Reads when already at EOF keep us at EOF
-            // We do retain the possibility of un-EOFing if the handler
-            // gets new data
-            if (atEOF && openFile.getMainStream().feof()) throw new EOFException();
+            myOpenFile.checkReadable(getRuntime());
 
-            if (argCount == 2) {
-                callerBuffer = !args[1].isNil() ? args[1].convertToString() : getRuntime().newString(); 
+            if (myOpenFile.getMainStream().feof()) {
+                return getRuntime().getNil();
             }
 
-            ByteList buf;
-            if (readEntireStream) {
-                buf = openFile.getMainStream().readall();
-            } else {
-                long len = RubyNumeric.num2long(args[0]);
-                if (len < 0) throw getRuntime().newArgumentError("negative length " + len + " given");
-                if (len == 0) return getRuntime().newString("");
-                buf = openFile.getMainStream().fread((int)len);
+            // TODO: Ruby locks the string here
+
+            // READ_CHECK from MRI io.c
+            readCheck(myOpenFile.getMainStream());
+
+            // TODO: check buffer length again?
+    //        if (RSTRING(str)->len != len) {
+    //            rb_raise(rb_eRuntimeError, "buffer string modified");
+    //        }
+
+            // TODO: read into buffer using all the fread logic
+    //        int read = openFile.getMainStream().fread(buffer);
+            ByteList newBuffer = myOpenFile.getMainStream().fread(length);
+
+            // TODO: Ruby unlocks the string here
+
+            // TODO: change this to check number read into buffer once that's working
+    //        if (read == 0) {
+            
+            if (newBuffer == null || newBuffer.length() == 0) {
+                if (myOpenFile.getMainStream() == null) {
+                    return getRuntime().getNil();
+                }
+
+                if (myOpenFile.getMainStream().feof()) {
+                    // truncate buffer string to zero, if provided
+                    if (str != null) {
+                        str.setValue(ByteList.EMPTY_BYTELIST.dup());
+                    }
+                
+                    return getRuntime().getNil();
+                }
+
+                if (length > 0) {
+                    // I think this is only partly correct; sys fail based on errno in Ruby
+                    throw getRuntime().newEOFError();
+                }
             }
 
-            if (buf == null) throw new EOFException();
 
-            // If we get here then no EOFException was thrown in the handler.  We
-            // might still need to set our atEOF flag back to true depending on
-            // whether we were reading the entire stream (see the finally block below)
-            atEOF = false;
-            if (callerBuffer != null) {
-                callerBuffer.setValue(buf);
-                return callerBuffer;
+            // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?
+
+            // FIXME: I don't like the null checks here
+            if (str == null) {
+                if (newBuffer == null) {
+                    str = getRuntime().newString();
+                } else {
+                    str = RubyString.newString(getRuntime(), newBuffer);
+                }
+            } else {
+                if (newBuffer == null) {
+                    str.setValue(ByteList.EMPTY_BYTELIST.dup());
+                } else {
+                    str.setValue(newBuffer);
+                }
             }
-            
-            return RubyString.newString(getRuntime(), buf);
-        } catch (Stream.BadDescriptorException e) {
-            e.printStackTrace();
+            str.setTaint(true);
+
+            return str;
+        } catch (EOFException ex) {
+            throw getRuntime().newEOFError();
+        } catch (PipeException ex) {
+            throw getRuntime().newErrnoEPIPEError();
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (IOException ex) {
+            throw getRuntime().newIOErrorFromException(ex);
+        } catch (BadDescriptorException ex) {
             throw getRuntime().newErrnoEBADFError();
-        } catch (EOFException e) {
-            // on EOF, IO#read():
-            // with no args or a nil first arg will return an empty string
-            // with a non-nil first arg will return nil
-            atEOF = true;
-            if (callerBuffer != null) {
-                callerBuffer.setValue("");
-                return readEntireStream ? callerBuffer : getRuntime().getNil();
-            }
+        }
+    }
+    
+    protected IRubyObject readAll(IRubyObject buffer) throws BadDescriptorException, EOFException, IOException {
+        // TODO: handle writing into original buffer better
+        
+        RubyString str = null;
+        if (buffer instanceof RubyString) {
+            str = (RubyString)buffer;
+        }
+        
+        // TODO: ruby locks the string here
+        
+        // READ_CHECK from MRI io.c
+        if (openFile.getMainStream().readDataBuffered()) {
+            openFile.checkClosed(getRuntime());
+        }
+        
+        ByteList newBuffer = openFile.getMainStream().readall();
 
-            return readEntireStream ? getRuntime().newString("") : getRuntime().getNil();
-        } catch (IOException e) {
-            throw getRuntime().newIOError(e.getMessage());
-        } finally {
-            // reading the entire stream always puts us at EOF
-            if (readEntireStream) {
-                atEOF = true;
+        // TODO same zero-length checks as file above
+
+        if (str == null) {
+            if (newBuffer == null) {
+                str = RubyString.newStringShared(getRuntime(), ByteList.EMPTY_BYTELIST);
+            } else {
+                str = RubyString.newString(getRuntime(), newBuffer);
+            }
+        } else {
+            if (newBuffer == null) {
+                str.setValue(ByteList.EMPTY_BYTELIST.dup());
+            } else {
+                str.setValue(newBuffer);
             }
         }
+
+        str.taint();
+
+        return str;
+//        long bytes = 0;
+//        long n;
+//
+//        if (siz == 0) siz = BUFSIZ;
+//        if (NIL_P(str)) {
+//            str = rb_str_new(0, siz);
+//        }
+//        else {
+//            rb_str_resize(str, siz);
+//        }
+//        for (;;) {
+//            rb_str_locktmp(str);
+//            READ_CHECK(fptr->f);
+//            n = io_fread(RSTRING(str)->ptr+bytes, siz-bytes, fptr);
+//            rb_str_unlocktmp(str);
+//            if (n == 0 && bytes == 0) {
+//                if (!fptr->f) break;
+//                if (feof(fptr->f)) break;
+//                if (!ferror(fptr->f)) break;
+//                rb_sys_fail(fptr->path);
+//            }
+//            bytes += n;
+//            if (bytes < siz) break;
+//            siz += BUFSIZ;
+//            rb_str_resize(str, siz);
+//        }
+//        if (bytes != siz) rb_str_resize(str, bytes);
+//        OBJ_TAINT(str);
+//
+//        return str;
     }
+    
+    // TODO: There's a lot of complexity here due to error handling and
+    // nonblocking IO; much of this goes away, but for now I'm just
+    // having read call ChannelStream.fread directly.
+//    protected int fread(int len, ByteList buffer) {
+//        long n = len;
+//        int c;
+//        int saved_errno;
+//
+//        while (n > 0) {
+//            c = read_buffered_data(ptr, n, fptr->f);
+//            if (c < 0) goto eof;
+//            if (c > 0) {
+//                ptr += c;
+//                if ((n -= c) <= 0) break;
+//            }
+//            rb_thread_wait_fd(fileno(fptr->f));
+//            rb_io_check_closed(fptr);
+//            clearerr(fptr->f);
+//            TRAP_BEG;
+//            c = getc(fptr->f);
+//            TRAP_END;
+//            if (c == EOF) {
+//              eof:
+//                if (ferror(fptr->f)) {
+//                    switch (errno) {
+//                      case EINTR:
+//    #if defined(ERESTART)
+//                      case ERESTART:
+//    #endif
+//                        clearerr(fptr->f);
+//                        continue;
+//                      case EAGAIN:
+//    #if defined(EWOULDBLOCK) && EWOULDBLOCK != EAGAIN
+//                      case EWOULDBLOCK:
+//    #endif
+//                        if (len > n) {
+//                            clearerr(fptr->f);
+//                        }
+//                        saved_errno = errno;
+//                        rb_warning("nonblocking IO#read is obsolete; use IO#readpartial or IO#sysread");
+//                        errno = saved_errno;
+//                    }
+//                    if (len == n) return 0;
+//                }
+//                break;
+//            }
+//            *ptr++ = c;
+//            n--;
+//        }
+//        return len - n;
+//        
+//    }
 
     /** Read a byte. On EOF throw EOFError.
      * 
      */
     @JRubyMethod(name = "readchar")
     public IRubyObject readchar() {
-        checkReadable();
-        
-        try {
-            int c = openFile.getMainStream().fgetc();
+        IRubyObject c = getc();
         
-            if (c == -1) throw getRuntime().newEOFError();
+        if (c.isNil()) throw getRuntime().newEOFError();
         
-            return getRuntime().newFixnum(c);
-        } catch (Stream.BadDescriptorException e) {
-            throw getRuntime().newErrnoEBADFError();
-        } catch (EOFException e) {
-            throw getRuntime().newEOFError();
-        } catch (IOException e) {
-            throw getRuntime().newIOError(e.getMessage());
-        }
+        return c;
     }
     
     @JRubyMethod
     public IRubyObject stat() {
-        return getRuntime().newFileStat(openFile.getMainStream().getFD());
+        return getRuntime().newFileStat(getOpenFileChecked().getMainStream().getDescriptor().getFileDescriptor());
     }
 
     /** 
      * <p>Invoke a block for each byte.</p>
      */
     @JRubyMethod(name = "each_byte", frame = true)
     public IRubyObject each_byte(Block block) {
     	try {
-            ThreadContext context = getRuntime().getCurrentContext();
-            for (int c = openFile.getMainStream().fgetc(); c != -1; c = openFile.getMainStream().fgetc()) {
+            Ruby runtime = getRuntime();
+            ThreadContext context = runtime.getCurrentContext();
+            OpenFile myOpenFile = getOpenFileChecked();
+            
+            while (true) {
+                myOpenFile.checkReadable(runtime);
+                
+                // TODO: READ_CHECK from MRI
+                
+                int c = myOpenFile.getMainStream().fgetc();
+                
+                if (c == -1) {
+                    // TODO: check for error, clear it, and wait until readable before trying once more
+//                    if (ferror(f)) {
+//                        clearerr(f);
+//                        if (!rb_io_wait_readable(fileno(f)))
+//                            rb_sys_fail(fptr->path);
+//                        continue;
+//                    }
+                    break;
+                }
+                
                 assert c < 256;
                 block.yield(context, getRuntime().newFixnum(c));
             }
 
-            return getRuntime().getNil();
-        } catch (Stream.BadDescriptorException e) {
+            // TODO: one more check for error
+//            if (ferror(f)) rb_sys_fail(fptr->path);
+            return this;
+        } catch (PipeException ex) {
+            throw getRuntime().newErrnoEPIPEError();
+        } catch (InvalidValueException ex) {
+            throw getRuntime().newErrnoEINVALError();
+        } catch (BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             return getRuntime().getNil();
     	} catch (IOException e) {
     	    throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     @JRubyMethod(name = {"each_line", "each"}, optional = 1, frame = true)
     public RubyIO each_line(IRubyObject[] args, Block block) {
         ThreadContext context = getRuntime().getCurrentContext(); 
         ByteList separator = getSeparatorForGets(args);
         
-        for (IRubyObject line = internalGets(separator); !line.isNil(); 
-        	line = internalGets(separator)) {
+        for (IRubyObject line = getline(separator); !line.isNil(); 
+        	line = getline(separator)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
 
     @JRubyMethod(name = "readlines", optional = 1)
     public RubyArray readlines(IRubyObject[] args) {
         ByteList separator;
         if (args.length > 0) {
             if (!getRuntime().getNilClass().isInstance(args[0]) &&
                 !getRuntime().getString().isInstance(args[0])) {
                 throw getRuntime().newTypeError(args[0], 
                         getRuntime().getString());
             } 
             separator = getSeparatorForGets(new IRubyObject[] { args[0] });
         } else {
             separator = getSeparatorForGets(IRubyObject.NULL_ARRAY);
         }
 
         RubyArray result = getRuntime().newArray();
         IRubyObject line;
-        while (! (line = internalGets(separator)).isNil()) {
+        while (! (line = getline(separator)).isNil()) {
             result.append(line);
         }
         return result;
     }
     
     @JRubyMethod(name = "to_io")
     public RubyIO to_io() {
     	return this;
     }
 
+    @Override
     public String toString() {
-        return "RubyIO(" + openFile.getModes() + ", " + openFile.getMainStream().getDescriptor().getFileno() + ")";
+        return "RubyIO(" + openFile.getMode() + ", " + openFile.getMainStream().getDescriptor().getFileno() + ")";
     }
     
     /* class methods for IO */
     
     /** rb_io_s_foreach
     *
     */
-    @JRubyMethod(name = "foreach", required = 1, rest = true, frame = true, meta = true)
+    @JRubyMethod(name = "foreach", required = 1, optional = 1, frame = true, meta = true)
     public static IRubyObject foreach(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         int count = args.length;
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
-        RubyIO io = (RubyIO) RubyFile.open(recv, new IRubyObject[] { filename }, false, block);
        
-        if (!io.isNil() && io.isOpen()) {
+        RubyString separator;
+        if (count == 2) {
+            separator = args[1].convertToString();
+        } else {
+            separator = runtime.getGlobalVariables().get("$/").convertToString();
+        }
+        
+        RubyIO io = (RubyIO)RubyFile.open(runtime.getFile(), new IRubyObject[] { filename }, Block.NULL_BLOCK);
+        
+        if (!io.isNil()) {
             try {
-                IRubyObject[] newArgs = new IRubyObject[count - 1];
-                System.arraycopy(args, 1, newArgs, 0, count - 1);
-               
-                IRubyObject nextLine = io.internalGets(newArgs);
-                while (!nextLine.isNil()) {
-                    block.yield(runtime.getCurrentContext(), nextLine);
-                    nextLine = io.internalGets(newArgs);
+                ByteList sep = separator.getByteList();
+                IRubyObject str = io.getline(sep);
+                ThreadContext context = runtime.getCurrentContext();
+                while (!str.isNil()) {
+                    block.yield(context, str);
+                    str = io.getline(sep);
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
    
     private static RubyIO registerSelect(Selector selector, IRubyObject obj, int ops) throws IOException {
        RubyIO ioObj;
        
        if (!(obj instanceof RubyIO)) {
            // invoke to_io
            if (!obj.respondsTo("to_io")) return null;
 
            ioObj = (RubyIO) obj.callMethod(obj.getRuntime().getCurrentContext(), "to_io");
        } else {
            ioObj = (RubyIO) obj;
        }
        
        Channel channel = ioObj.getChannel();
        if (channel == null || !(channel instanceof SelectableChannel)) {
            return null;
        }
        
        ((SelectableChannel) channel).configureBlocking(false);
        int real_ops = ((SelectableChannel) channel).validOps() & ops;
        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
        if (key == null) {
            ((SelectableChannel) channel).register(selector, real_ops, obj);
        } else {
            key.interestOps(key.interestOps()|real_ops);
        }
        
        return ioObj;
    }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return select_static(recv.getRuntime(), args);
     }
    
     public static IRubyObject select_static(Ruby runtime, IRubyObject[] args) {
        try {
+           // FIXME: This needs to be ported
            boolean atLeastOneDescriptor = false;
            
            Set pending = new HashSet();
            Selector selector = Selector.open();
            if (!args[0].isNil()) {
                atLeastOneDescriptor = true;
                
                // read
                for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                    IRubyObject obj = (IRubyObject) i.next();
                    RubyIO ioObj = registerSelect(selector, obj, 
                            SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
                    
-                   if (ioObj!=null && ioObj.hasPendingBuffered()) pending.add(obj);
+                   if (ioObj!=null && ioObj.writeDataBuffered()) pending.add(obj);
                }
            }
            if (args.length > 1 && !args[1].isNil()) {
                atLeastOneDescriptor = true;
                // write
                for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                    IRubyObject obj = (IRubyObject) i.next();
                    registerSelect(selector, obj, SelectionKey.OP_WRITE);
                }
            }
            if (args.length > 2 && !args[2].isNil()) {
                atLeastOneDescriptor = true;
                // Java's select doesn't do anything about this, so we leave it be.
            }
            
            long timeout = 0;
            if(args.length > 3 && !args[3].isNil()) {
                if (args[3] instanceof RubyFloat) {
                    timeout = Math.round(((RubyFloat) args[3]).getDoubleValue() * 1000);
                } else {
                    timeout = Math.round(((RubyFixnum) args[3]).getDoubleValue() * 1000);
                }
                
                if (timeout < 0) {
                    throw runtime.newArgumentError("negative timeout given");
                }
            }
            
            if (!atLeastOneDescriptor) {
                return runtime.getNil();
            }
            
            if (pending.isEmpty()) {
                if (args.length > 3) {
                    if (timeout==0) {
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
            for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                SelectionKey key = (SelectionKey) i.next();
                if ((key.interestOps() & key.readyOps()
                        & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
                    r.add(key.attachment());
                    pending.remove(key.attachment());
                }
                if ((key.interestOps() & key.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                    w.add(key.attachment());
                }
            }
            r.addAll(pending);
            
            // make all sockets blocking as configured again
            for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
                SelectionKey key = (SelectionKey) i.next();
                SelectableChannel channel = key.channel();
                synchronized(channel.blockingLock()) {
                    boolean blocking = ((RubyIO) key.attachment()).getBlocking();
                    key.cancel();
                    channel.configureBlocking(blocking);
                }
            }
            selector.close();
            
            if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
                return runtime.getNil();
            }
            
            List ret = new ArrayList();
            
            ret.add(RubyArray.newArray(runtime, r));
            ret.add(RubyArray.newArray(runtime, w));
            ret.add(RubyArray.newArray(runtime, e));
            
            return RubyArray.newArray(runtime, ret);
        } catch(IOException e) {
            throw runtime.newIOError(e.getMessage());
        }
    }
    
     @JRubyMethod(name = "read", required = 1, optional = 2, meta = true)
     public static IRubyObject read(IRubyObject recv, IRubyObject[] args, Block block) {
        IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
        RubyIO file = (RubyIO) RubyKernel.open(recv, fileArguments, block);
        IRubyObject[] readArguments;
        
        if (args.length >= 2) {
            readArguments = new IRubyObject[] {args[1].convertToInteger()};
        } else {
            readArguments = new IRubyObject[] {};
        }
        
        try {
            
            if (args.length == 3) {
                file.seek(new IRubyObject[] {args[2].convertToInteger()});
            }
            
            return file.read(readArguments);
        } finally {
            file.close();
        }
    }
    
     @JRubyMethod(name = "readlines", required = 1, optional = 1, meta = true)
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args, Block block) {
-       int count = args.length;
-       
-       IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
-       IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
-       RubyIO file = (RubyIO) RubyKernel.open(recv, fileArguments, block);
-       try {
-           return file.readlines(separatorArguments);
-       } finally {
-           file.close();
-       }
-   }
+        int count = args.length;
+
+        IRubyObject[] fileArguments = new IRubyObject[]{args[0]};
+        IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
+        RubyIO file = (RubyIO) RubyKernel.open(recv, fileArguments, block);
+        try {
+            return file.readlines(separatorArguments);
+        } finally {
+            file.close();
+        }
+    }
    
-    //XXX Hacked incomplete popen implementation to make
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true)
     public static IRubyObject popen(IRubyObject recv, IRubyObject[] args, Block block) {
-       Ruby runtime = recv.getRuntime();
-       IRubyObject cmdObj = args[0].convertToString();
-       runtime.checkSafeString(cmdObj);
-       
-       try {
-           Process process = new ShellLauncher(runtime).run(cmdObj);            
-           RubyIO io = new RubyIO(runtime, process);
-           
-           if (block.isGiven()) {
-               try {
-                   return block.yield(runtime.getCurrentContext(), io);
-               } finally {
-                   if (io.isOpen()) {
-                       io.close();
-                   }
-                   runtime.getGlobalVariables().set("$?",  RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor() * 256)));
-               }
-           }
-           return io;
-       } catch (IOException e) {
-           throw runtime.newIOErrorFromException(e);
-       } catch (InterruptedException e) {
-           throw runtime.newThreadError("unexpected interrupt");
-       }
-   }
-   
+        Ruby runtime = recv.getRuntime();
+        int mode;
+
+        try {
+            if (args.length == 1) {
+                mode = ModeFlags.RDONLY;
+            } else if (args[1] instanceof RubyFixnum) {
+                mode = RubyFixnum.num2int(args[1]);
+            } else {
+                mode = getIOModesIntFromString(runtime, args[1].convertToString().toString());
+            }
+
+            ModeFlags modes = new ModeFlags(mode);
+            IRubyObject cmdObj = args[0].convertToString();
+            runtime.checkSafeString(cmdObj);
+        
+            Process process = new ShellLauncher(runtime).run(cmdObj);
+            RubyIO io = new RubyIO(runtime, process, modes);
+
+            if (block.isGiven()) {
+                try {
+                    return block.yield(runtime.getCurrentContext(), io);
+                } finally {
+                    if (io.openFile.isOpen()) {
+                        io.close();
+                    }
+                    runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor() * 256)));
+                }
+            }
+            return io;
+        } catch (InvalidValueException ex) {
+            throw runtime.newErrnoEINVALError();
+        } catch (IOException e) {
+            throw runtime.newIOErrorFromException(e);
+        } catch (InterruptedException e) {
+            throw runtime.newThreadError("unexpected interrupt");
+        }
+    }
+
     // NIO based pipe
     @JRubyMethod(name = "pipe", meta = true)
     public static IRubyObject pipe(IRubyObject recv) throws Exception {
+        // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
        Ruby runtime = recv.getRuntime();
        Pipe pipe = Pipe.open();
+       
+       RubyIO source = new RubyIO(runtime, pipe.source());
+       RubyIO sink = new RubyIO(runtime, pipe.sink());
+       
+       sink.openFile.getMainStream().setSync(true);
        return runtime.newArrayNoCopy(new IRubyObject[]{
-           new RubyIO(runtime, pipe.source()),
-           new RubyIO(runtime, pipe.sink())
+           source,
+           sink
        });
    }
-   
-    /**
-     * returns non-nil if input available without blocking, false if EOF or not open/readable, otherwise nil.
-     */
-    public IRubyObject ready() {
-       try {
-           if (!openFile.mainStream.isOpen() || !openFile.mainStream.isReadable() || openFile.getMainStream().feof()) {
-               return getRuntime().getFalse();
-           }
-
-           int avail = openFile.getMainStream().ready();
-           if (avail > 0) {
-               return getRuntime().newFixnum(avail);
-           } 
-       } catch (Exception anyEx) {
-           return getRuntime().getFalse();
-       }
-       return getRuntime().getNil();
-   }
-   
-    /**
-     * waits until input available or timed out and returns self, or nil when EOF reached.
-     */
-    public IRubyObject io_wait() {
-       try {
-           if (openFile.getMainStream().feof()) {
-               return getRuntime().getNil();
-           }
-            openFile.getMainStream().waitUntilReady();
-       } catch (Exception anyEx) {
-           return getRuntime().getNil();
-       }
-       return this;
-   }
 }
