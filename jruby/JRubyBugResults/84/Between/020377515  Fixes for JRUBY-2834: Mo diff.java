diff --git a/spec/tags/ruby/1.8/core/argf/close_tags.txt b/spec/tags/ruby/1.8/core/argf/close_tags.txt
deleted file mode 100644
index 14885b5279..0000000000
--- a/spec/tags/ruby/1.8/core/argf/close_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.close reads one line from the first file, closes it and read the next one
diff --git a/spec/tags/ruby/1.8/core/argf/closed_tags.txt b/spec/tags/ruby/1.8/core/argf/closed_tags.txt
deleted file mode 100644
index d86f996c36..0000000000
--- a/spec/tags/ruby/1.8/core/argf/closed_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.closed? says it is closed 
diff --git a/spec/tags/ruby/1.8/core/argf/each_byte_tags.txt b/spec/tags/ruby/1.8/core/argf/each_byte_tags.txt
deleted file mode 100644
index 7e19b104e6..0000000000
--- a/spec/tags/ruby/1.8/core/argf/each_byte_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.each_byte reads each byte of files
diff --git a/spec/tags/ruby/1.8/core/argf/each_line_tags.txt b/spec/tags/ruby/1.8/core/argf/each_line_tags.txt
deleted file mode 100644
index 6f0fb680d5..0000000000
--- a/spec/tags/ruby/1.8/core/argf/each_line_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.each_line reads each line of files
diff --git a/spec/tags/ruby/1.8/core/argf/each_tags.txt b/spec/tags/ruby/1.8/core/argf/each_tags.txt
deleted file mode 100644
index 0ecefbe141..0000000000
--- a/spec/tags/ruby/1.8/core/argf/each_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.each reads each line of files
diff --git a/spec/tags/ruby/1.8/core/argf/eof_tags.txt b/spec/tags/ruby/1.8/core/argf/eof_tags.txt
deleted file mode 100644
index f5825c3c6e..0000000000
--- a/spec/tags/ruby/1.8/core/argf/eof_tags.txt
+++ /dev/null
@@ -1,4 +0,0 @@
-fails(JRUBY-2834):ARGF.eof returns true when reaching the end of a file
-fails(JRUBY-2834):ARGF.eof raises IOError when called on a closed stream
-fails(JRUBY-2834):ARGF.eof? returns true when reaching the end of a file
-fails(JRUBY-2834):ARGF.eof? raises IOError when called on a closed stream
diff --git a/spec/tags/ruby/1.8/core/argf/file_tags.txt b/spec/tags/ruby/1.8/core/argf/file_tags.txt
deleted file mode 100644
index 0141832d39..0000000000
--- a/spec/tags/ruby/1.8/core/argf/file_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.file returns the current file object on each file
diff --git a/spec/tags/ruby/1.8/core/argf/filename_tags.txt b/spec/tags/ruby/1.8/core/argf/filename_tags.txt
deleted file mode 100644
index 935fc19d51..0000000000
--- a/spec/tags/ruby/1.8/core/argf/filename_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails(JRUBY-2834):ARGF.filename returns the current file name on each file
-fails(JRUBY-2834):ARGF.filename it sets the $FILENAME global variable with the current file name on each file
diff --git a/spec/tags/ruby/1.8/core/argf/fileno_tags.txt b/spec/tags/ruby/1.8/core/argf/fileno_tags.txt
deleted file mode 100644
index e773755844..0000000000
--- a/spec/tags/ruby/1.8/core/argf/fileno_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.fileno returns the current file number on each file
diff --git a/spec/tags/ruby/1.8/core/argf/getc_tags.txt b/spec/tags/ruby/1.8/core/argf/getc_tags.txt
deleted file mode 100644
index d9cce1eb09..0000000000
--- a/spec/tags/ruby/1.8/core/argf/getc_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.getc reads each char of files
diff --git a/spec/tags/ruby/1.8/core/argf/gets_tags.txt b/spec/tags/ruby/1.8/core/argf/gets_tags.txt
deleted file mode 100644
index 9b33d51aa9..0000000000
--- a/spec/tags/ruby/1.8/core/argf/gets_tags.txt
+++ /dev/null
@@ -1,4 +0,0 @@
-fails(JRUBY-2834):ARGF.gets reads one line of a file
-fails(JRUBY-2834):ARGF.gets reads all lines of a file
-fails(JRUBY-2834):ARGF.gets reads all lines of two files
-fails(JRUBY-2834):ARGF.gets sets $_ global variable with each line read
diff --git a/spec/tags/ruby/1.8/core/argf/lineno_tags.txt b/spec/tags/ruby/1.8/core/argf/lineno_tags.txt
deleted file mode 100644
index bf5e129f90..0000000000
--- a/spec/tags/ruby/1.8/core/argf/lineno_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.lineno returns the current line number on each file
diff --git a/spec/tags/ruby/1.8/core/argf/path_tags.txt b/spec/tags/ruby/1.8/core/argf/path_tags.txt
deleted file mode 100644
index 721e46567d..0000000000
--- a/spec/tags/ruby/1.8/core/argf/path_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails(JRUBY-2834):ARGF.path returns the current file name on each file
-fails(JRUBY-2834):ARGF.path it sets the $FILENAME global variable with the current file name on each file
diff --git a/spec/tags/ruby/1.8/core/argf/pos_tags.txt b/spec/tags/ruby/1.8/core/argf/pos_tags.txt
deleted file mode 100644
index 4d991f9ad0..0000000000
--- a/spec/tags/ruby/1.8/core/argf/pos_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails(JRUBY-2834):ARGF.pos gives the correct position for each read operation
-fails(JRUBY-2834):ARGF.pos= sets the correct position in files
diff --git a/spec/tags/ruby/1.8/core/argf/read_tags.txt b/spec/tags/ruby/1.8/core/argf/read_tags.txt
deleted file mode 100644
index d159427ee0..0000000000
--- a/spec/tags/ruby/1.8/core/argf/read_tags.txt
+++ /dev/null
@@ -1,11 +0,0 @@
-fails(JRUBY-2834):ARGF.read reads the contents of a file
-fails(JRUBY-2834):ARGF.read treats first nil argument as no length limit
-fails(JRUBY-2834):ARGF.read treats second nil argument as no output buffer
-fails(JRUBY-2834):ARGF.read treats second argument as an output buffer
-fails(JRUBY-2834):ARGF.read reads a number of bytes from the first file
-fails(JRUBY-2834):ARGF.read reads from a single file consecutively
-fails(JRUBY-2834):ARGF.read reads the contents of two files
-fails(JRUBY-2834):ARGF.read reads the contents of one file and some characters from the second
-fails(JRUBY-2834):ARGF.read reads across two files consecutively
-fails(JRUBY-2834):ARGF.read reads the contents of the same file twice
-fails(JRUBY-2834):ARGF.read reads the contents of a special device file
diff --git a/spec/tags/ruby/1.8/core/argf/readchar_tags.txt b/spec/tags/ruby/1.8/core/argf/readchar_tags.txt
deleted file mode 100644
index b1e3515397..0000000000
--- a/spec/tags/ruby/1.8/core/argf/readchar_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.readchar reads each char of files
diff --git a/spec/tags/ruby/1.8/core/argf/readline_tags.txt b/spec/tags/ruby/1.8/core/argf/readline_tags.txt
deleted file mode 100644
index c7e38228f2..0000000000
--- a/spec/tags/ruby/1.8/core/argf/readline_tags.txt
+++ /dev/null
@@ -1,4 +0,0 @@
-fails(JRUBY-2834):ARGF.readline reads one line of a file
-fails(JRUBY-2834):ARGF.readline reads all lines of a file
-fails(JRUBY-2834):ARGF.readline reads all lines of stdin
-fails(JRUBY-2834):ARGF.readline reads all lines of two files
diff --git a/spec/tags/ruby/1.8/core/argf/readlines_tags.txt b/spec/tags/ruby/1.8/core/argf/readlines_tags.txt
deleted file mode 100644
index 586cdddb61..0000000000
--- a/spec/tags/ruby/1.8/core/argf/readlines_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails(JRUBY-2834):ARGF.readlines reads all lines of all files
-fails(JRUBY-2834):ARGF.readlines returns nil when end of stream reached
diff --git a/spec/tags/ruby/1.8/core/argf/rewind_tags.txt b/spec/tags/ruby/1.8/core/argf/rewind_tags.txt
deleted file mode 100644
index 1f8ac318be..0000000000
--- a/spec/tags/ruby/1.8/core/argf/rewind_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.rewind goes back to beginning of current file
diff --git a/spec/tags/ruby/1.8/core/argf/seek_tags.txt b/spec/tags/ruby/1.8/core/argf/seek_tags.txt
deleted file mode 100644
index 1f9f0a2357..0000000000
--- a/spec/tags/ruby/1.8/core/argf/seek_tags.txt
+++ /dev/null
@@ -1,3 +0,0 @@
-fails(JRUBY-2834):ARGF.seek sets the correct absolute position relative to beginning of file
-fails(JRUBY-2834):ARGF.seek sets the correct position relative to current position in file
-fails(JRUBY-2834):ARGF.seek sets the correct absolute position relative to end of file
diff --git a/spec/tags/ruby/1.8/core/argf/skip_tags.txt b/spec/tags/ruby/1.8/core/argf/skip_tags.txt
deleted file mode 100644
index bc52f8a51a..0000000000
--- a/spec/tags/ruby/1.8/core/argf/skip_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails(JRUBY-2834):ARGF.skip skips the current file
-fails(JRUBY-2834):ARGF.skip has no effect when called twice in a row
diff --git a/spec/tags/ruby/1.8/core/argf/tell_tags.txt b/spec/tags/ruby/1.8/core/argf/tell_tags.txt
deleted file mode 100644
index dfc61d86b5..0000000000
--- a/spec/tags/ruby/1.8/core/argf/tell_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.tell gives the correct position for each read operation
diff --git a/spec/tags/ruby/1.8/core/argf/to_a_tags.txt b/spec/tags/ruby/1.8/core/argf/to_a_tags.txt
deleted file mode 100644
index bd63a5f114..0000000000
--- a/spec/tags/ruby/1.8/core/argf/to_a_tags.txt
+++ /dev/null
@@ -1,2 +0,0 @@
-fails(JRUBY-2834):ARGF.to_a reads all lines of all files
-fails(JRUBY-2834):ARGF.to_a returns nil when end of stream reached
diff --git a/spec/tags/ruby/1.8/core/argf/to_i_tags.txt b/spec/tags/ruby/1.8/core/argf/to_i_tags.txt
deleted file mode 100644
index 3949628fa7..0000000000
--- a/spec/tags/ruby/1.8/core/argf/to_i_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.to_i returns the current file number on each file
diff --git a/spec/tags/ruby/1.8/core/argf/to_io_tags.txt b/spec/tags/ruby/1.8/core/argf/to_io_tags.txt
deleted file mode 100644
index 01b93af993..0000000000
--- a/spec/tags/ruby/1.8/core/argf/to_io_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2834):ARGF.to_io returns the IO of the current file
diff --git a/src/org/jruby/RubyArgsFile.java b/src/org/jruby/RubyArgsFile.java
index 3235c8bc4f..bbec39a72a 100644
--- a/src/org/jruby/RubyArgsFile.java
+++ b/src/org/jruby/RubyArgsFile.java
@@ -1,451 +1,471 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
+ * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
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
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 
-import org.jruby.anno.FrameField;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 public class RubyArgsFile {
     private static final class ArgsFileData {
         private final Ruby runtime;
         public ArgsFileData(Ruby runtime) {
             this.runtime = runtime;
         }
 
         public IRubyObject currentFile;
         public int currentLineNumber;
+        public int minLineNumber;
         public boolean startedProcessing = false; 
-        public boolean finishedProcessing = false;
 
         public boolean nextArgsFile(ThreadContext context) {
-            if (finishedProcessing) {
-                return false;
-            }
 
             RubyArray args = (RubyArray)runtime.getGlobalVariables().get("$*");
             if (args.getLength() == 0) {
                 if (!startedProcessing) { 
                     currentFile = runtime.getGlobalVariables().get("$stdin");
-                    ((RubyString) runtime.getGlobalVariables().get("$FILENAME")).setValue(new ByteList(new byte[]{'-'}));
-                    currentLineNumber = 0;
+                    if(!runtime.getGlobalVariables().get("$FILENAME").asJavaString().equals("-")) {
+                        runtime.defineReadonlyVariable("$FILENAME", runtime.newString("-"));
+                    }
                     startedProcessing = true;
                     return true;
                 } else {
-                    finishedProcessing = true;
                     return false;
                 }
             }
 
             IRubyObject arg = args.shift(context);
             RubyString filename = (RubyString)((RubyObject)arg).to_s();
             ByteList filenameBytes = filename.getByteList();
-            ((RubyString) runtime.getGlobalVariables().get("$FILENAME")).setValue(filenameBytes);
+            if(!filename.op_equal(context, (RubyString) runtime.getGlobalVariables().get("$FILENAME")).isTrue()) {
+                runtime.defineReadonlyVariable("$FILENAME", filename);
+            }
 
             if (filenameBytes.length() == 1 && filenameBytes.get(0) == '-') {
                 currentFile = runtime.getGlobalVariables().get("$stdin");
             } else {
                 currentFile = RubyFile.open(context, runtime.getFile(), 
                         new IRubyObject[] {filename.strDup(context.getRuntime())}, Block.NULL_BLOCK); 
+                minLineNumber = currentLineNumber;
+                currentFile.callMethod(context, "lineno=", context.getRuntime().newFixnum(currentLineNumber));
             }
             
             startedProcessing = true;
             return true;
         }
 
         public static ArgsFileData getDataFrom(IRubyObject recv) {
             ArgsFileData data = (ArgsFileData)recv.dataGetStruct();
             if(data == null) {
                 data = new ArgsFileData(recv.getRuntime());
                 recv.dataWrapStruct(data);
             }
             return data;
         }
     }    
     
-    public static void setCurrentLineNumber(IRubyObject recv, int newLineNumber) {
-        ArgsFileData.getDataFrom(recv).currentLineNumber = newLineNumber;
+    public static void setCurrentLineNumber(IRubyObject recv, IRubyObject newLineNumber) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+
+        if(data != null) {
+            int lineno = RubyNumeric.fix2int(newLineNumber);
+            data.currentLineNumber = lineno;
+            if (data.currentFile != null) {
+                data.currentFile.callMethod(recv.getRuntime().getCurrentContext(), "lineno=", newLineNumber);
+    }
+        }
     }
 
     public static void initArgsFile(Ruby runtime) {
         RubyObject argsFile = new RubyObject(runtime, runtime.getObject());
 
         runtime.getEnumerable().extend_object(argsFile);
         
         runtime.defineReadonlyVariable("$<", argsFile);
         runtime.defineGlobalConstant("ARGF", argsFile);
         
         RubyClass argfClass = argsFile.getMetaClass();
         argfClass.defineAnnotatedMethods(RubyArgsFile.class);
         runtime.defineReadonlyVariable("$FILENAME", runtime.newString("-"));
     }
 
     @JRubyMethod(name = {"fileno", "to_i"})
     public static IRubyObject fileno(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
 
         if (data.currentFile == null && !data.nextArgsFile(context)) {
             throw context.getRuntime().newArgumentError("no stream");
         }
         return ((RubyIO) data.currentFile).fileno(context);
     }
 
     @JRubyMethod(name = "to_io")
     public static IRubyObject to_io(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
 
         if (data.currentFile == null && !data.nextArgsFile(context)) {
             throw context.getRuntime().newArgumentError("no stream");
         }
         return data.currentFile;
     }
 
     public static IRubyObject internalGets(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
 
         if(data.currentFile == null && !data.nextArgsFile(context)) {
             return context.getRuntime().getNil();
         }
         
         IRubyObject line = data.currentFile.callMethod(context, "gets", args);
         
         while (line instanceof RubyNil) {
             data.currentFile.callMethod(context, "close");
             if (!data.nextArgsFile(context)) {
-                data.currentFile = null;
                 return line;
         	}
             line = data.currentFile.callMethod(context, "gets", args);
         }
         
-        data.currentLineNumber++;
         context.getRuntime().getGlobalVariables().set("$.", context.getRuntime().newFixnum(data.currentLineNumber));
         
         return line;
     }
     
     // ARGF methods
 
     /** Read a line.
      * 
      */
-    @JRubyMethod(name = "gets", optional = 1, frame = true, writes = FrameField.LASTLINE)
+    @JRubyMethod(name = "gets", optional = 1)
     public static IRubyObject gets(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject result = internalGets(context, recv, args);
-
         if (!result.isNil()) {
             context.getCurrentFrame().setLastLine(result);
+            context.getRuntime().getGlobalVariables().set("$_", result);
         }
 
         return result;
     }
     
     /** Read a line.
      * 
      */
-    @JRubyMethod(name = "readline", optional = 1, frame = true, writes = FrameField.LASTLINE)
+    @JRubyMethod(name = "readline", optional = 1)
     public static IRubyObject readline(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(context, recv, args);
 
         if (line.isNil()) {
             throw context.getRuntime().newEOFError();
         }
         
         return line;
     }
 
-    @JRubyMethod(name = "readlines", optional = 1, frame = true)
-    public static RubyArray readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+    @JRubyMethod(name = {"readlines", "to_a"}, optional = 1, frame = true)
+    public static IRubyObject readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject[] separatorArgument;
         if (args.length > 0) {
             if (!context.getRuntime().getNilClass().isInstance(args[0]) &&
                 !context.getRuntime().getString().isInstance(args[0])) {
                 throw context.getRuntime().newTypeError(args[0], context.getRuntime().getString());
             } 
             separatorArgument = new IRubyObject[] { args[0] };
         } else {
             separatorArgument = IRubyObject.NULL_ARRAY;
         }
 
         RubyArray result = context.getRuntime().newArray();
         IRubyObject line;
         while (! (line = internalGets(context, recv, separatorArgument)).isNil()) {
             result.append(line);
         }
+        if(result.empty_p().isTrue()) {
+            return context.getRuntime().getNil();
+        }
         return result;
     }
     
     @JRubyMethod(name = "each_byte", frame = true)
     public static IRubyObject each_byte(ThreadContext context, IRubyObject recv, Block block) {
         IRubyObject bt;
 
         while(!(bt = getc(context, recv)).isNil()) {
             block.yield(context, bt);
         }
 
         return recv;
     }
 
     @JRubyMethod(name = "each_byte", optional = 1, frame = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject each_byte19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         return block.isGiven() ? each_byte(context, recv, block) : enumeratorize(context.getRuntime(), recv, "each_byte");
     }
 
     /** Invoke a block for each line.
      *
      */
     @JRubyMethod(name = {"each_line", "each"}, optional = 1, frame = true)
     public static IRubyObject each_line(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject nextLine = internalGets(context, recv, args);
         
         while (!nextLine.isNil()) {
         	block.yield(context, nextLine);
         	nextLine = internalGets(context, recv, args);
         }
         
         return recv;
     }
 
     @JRubyMethod(name = "each_line", optional = 1, frame = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject each_line19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         return block.isGiven() ? each_line(context, recv, args, block) : enumeratorize(context.getRuntime(), recv, "each_line", args);
     }
 
     @JRubyMethod(name = "each", optional = 1, frame = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject each19(final ThreadContext context, IRubyObject recv, IRubyObject[] args, final Block block) {
         return block.isGiven() ? each_line(context, recv, args, block) : enumeratorize(context.getRuntime(), recv, "each", args);
     }
 
     @JRubyMethod(name = "file")
     public static IRubyObject file(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
 
         if(data.currentFile == null && !data.nextArgsFile(context)) {
             return context.getRuntime().getNil();
         }
         return data.currentFile;
     }
 
     @JRubyMethod(name = "skip")
     public static IRubyObject skip(IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         data.currentFile = null;
         return recv;
     }
 
     @JRubyMethod(name = "close")
     public static IRubyObject close(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         if(data.currentFile == null && !data.nextArgsFile(context)) {
             return recv;
         }
         data.currentFile = null;
         data.currentLineNumber = 0;
         return recv;
     }
 
     @JRubyMethod(name = "closed?")
     public static IRubyObject closed_p(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         if(data.currentFile == null && !data.nextArgsFile(context)) {
-            return recv;
+            return recv.getRuntime().getTrue();
         }
         return ((RubyIO)data.currentFile).closed_p(context);
     }
 
     @JRubyMethod(name = "binmode")
     public static IRubyObject binmode(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         if(data.currentFile == null && !data.nextArgsFile(context)) {
             throw context.getRuntime().newArgumentError("no stream");
         }
         
         return ((RubyIO)data.currentFile).binmode();
     }
 
     @JRubyMethod(name = "lineno")
     public static IRubyObject lineno(ThreadContext context, IRubyObject recv) {
-        return context.getRuntime().newFixnum(ArgsFileData.getDataFrom(recv).currentLineNumber);
+        return recv.getRuntime().newFixnum(ArgsFileData.getDataFrom(recv).currentLineNumber);
+    }
+
+    @JRubyMethod(name = "lineno=")
+    public static IRubyObject lineno_set(ThreadContext context, IRubyObject recv, IRubyObject line) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        data.currentLineNumber = RubyNumeric.fix2int(line);
+        data.currentFile.callMethod(context, "lineno=", line);
+        context.getRuntime().getGlobalVariables().set("$.", line);
+        return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "tell", alias = {"pos"})
     public static IRubyObject tell(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         if(data.currentFile == null && !data.nextArgsFile(context)) {
             throw context.getRuntime().newArgumentError("no stream to tell");
         }
         return ((RubyIO)data.currentFile).pos(context);
     }
 
     @JRubyMethod(name = "rewind")
     public static IRubyObject rewind(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         if(data.currentFile == null && !data.nextArgsFile(context)) {
             throw context.getRuntime().newArgumentError("no stream to rewind");
         }
-        return ((RubyIO)data.currentFile).rewind(context);
+        RubyFixnum retVal = ((RubyIO)data.currentFile).rewind(context);
+        ((RubyIO)data.currentFile).lineno_set(context, context.getRuntime().newFixnum(data.minLineNumber));
+        return retVal;
     }
 
     @JRubyMethod(name = {"eof", "eof?"})
     public static IRubyObject eof(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         if (data.currentFile == null && !data.nextArgsFile(context)) {
-            return context.getRuntime().getTrue();
+            throw context.getRuntime().newIOError("stream is closed");
         }
 
         return ((RubyIO) data.currentFile).eof_p(context);
     }
 
     @JRubyMethod(name = "pos=", required = 1)
     public static IRubyObject set_pos(ThreadContext context, IRubyObject recv, IRubyObject offset) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         if(data.currentFile == null && !data.nextArgsFile(context)) {
             throw context.getRuntime().newArgumentError("no stream to set position");
         }
         return ((RubyIO)data.currentFile).pos_set(context, offset);
     }
 
     @JRubyMethod(name = "seek", required = 1, optional = 1)
     public static IRubyObject seek(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         if(data.currentFile == null && !data.nextArgsFile(context)) {
             throw context.getRuntime().newArgumentError("no stream to seek");
         }
         return ((RubyIO)data.currentFile).seek(context, args);
     }
 
-    @JRubyMethod(name = "lineno=", required = 1)
-    public static IRubyObject set_lineno(ThreadContext context, IRubyObject recv, IRubyObject line) {
-        ArgsFileData data = ArgsFileData.getDataFrom(recv);
-        data.currentLineNumber = RubyNumeric.fix2int(line);
-        return context.getRuntime().getNil();
-    }
-
     @JRubyMethod(name = "readchar")
     public static IRubyObject readchar(ThreadContext context, IRubyObject recv) {
         IRubyObject c = getc(context, recv);
         
         if(c.isNil()) throw context.getRuntime().newEOFError();
 
         return c;
     }
 
     @JRubyMethod(name = "getc")
     public static IRubyObject getc(ThreadContext context, IRubyObject recv) {
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         IRubyObject bt;
         while(true) {
             if(data.currentFile == null && !data.nextArgsFile(context)) {
                 return context.getRuntime().getNil();
             }
             if(!(data.currentFile instanceof RubyFile)) {
                 bt = data.currentFile.callMethod(context,"getc");
             } else {
                 bt = ((RubyIO)data.currentFile).getc();
             }
             if(bt.isNil()) {
                 data.currentFile = null;
                 continue;
             }
             return bt;
         }
     }
 
     @JRubyMethod(name = "read", optional = 2)
     public static IRubyObject read(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         ArgsFileData data = ArgsFileData.getDataFrom(recv);
         IRubyObject tmp, str, length;
         long len = 0;
         if(args.length > 0) {
             length = args[0];
             if(args.length > 1) {
                 str = args[1];
             } else {
                 str = runtime.getNil();
             }
         } else {
             length = str = runtime.getNil();
         }
 
         if(!length.isNil()) {
             len = RubyNumeric.num2long(length);
         }
         if(!str.isNil()) {
             str = str.convertToString();
             ((RubyString)str).modify();
             ((RubyString)str).getByteList().length(0);
             args[1] = runtime.getNil();
         }
         while(true) {
             if(data.currentFile == null && !data.nextArgsFile(context)) {
                 return str;
             }
             if(!(data.currentFile instanceof RubyIO)) {
                 tmp = data.currentFile.callMethod(context, "read", args);
             } else {
                 tmp = ((RubyIO)data.currentFile).read(args);
             }
             if(str.isNil()) {
                 str = tmp;
             } else if(!tmp.isNil()) {
                 ((RubyString)str).append(tmp);
             }
             if(tmp.isNil() || length.isNil()) {
                 data.currentFile = null;
                 continue;
             } else if(args.length >= 1) {
                 if(((RubyString)str).getByteList().length() < len) {
                     len -= ((RubyString)str).getByteList().length();
                     args[0] = runtime.newFixnum(len);
                     continue;
                 }
             }
             return str;
         }
     }
 
     @JRubyMethod(name = "filename", alias = {"path"})
-    public static RubyString filename(ThreadContext context, IRubyObject recv) {
-        return (RubyString) context.getRuntime().getGlobalVariables().get("$FILENAME");
+    public static IRubyObject filename(ThreadContext context, IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+
+        if(data.currentFile == null && !data.nextArgsFile(context)) {
+            return context.getRuntime().getNil();
+    }
+
+        return context.getRuntime().getGlobalVariables().get("$FILENAME");
     }
 
     @JRubyMethod(name = "to_s") 
     public static IRubyObject to_s(IRubyObject recv) {
         return recv.getRuntime().newString("ARGF");
     }
 }
diff --git a/src/org/jruby/RubyGlobal.java b/src/org/jruby/RubyGlobal.java
index a057c6a1b1..72a6eac4e9 100644
--- a/src/org/jruby/RubyGlobal.java
+++ b/src/org/jruby/RubyGlobal.java
@@ -1,627 +1,628 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
+ * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
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
 
 import org.jruby.util.io.STDIO;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.environment.OSEnvironmentReaderExcepton;
 import org.jruby.environment.OSEnvironment;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ReadonlyGlobalVariable;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.KCode;
 
 /** This class initializes global variables and constants.
  * 
  * @author jpetersen
  */
 public class RubyGlobal {
     
     /**
      * Obligate string-keyed and string-valued hash, used for ENV and ENV_JAVA
      * 
      */
     public static class StringOnlyRubyHash extends RubyHash {
         
         public StringOnlyRubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
             super(runtime, valueMap, defaultValue);
         }
 
         @Override
         public RubyHash to_hash() {
             Ruby runtime = getRuntime();
             RubyHash hash = RubyHash.newHash(runtime);
             hash.replace(runtime.getCurrentContext(), this);
             return hash;
         }
 
         @Override
         public IRubyObject op_aref(ThreadContext context, IRubyObject key) {
             return super.op_aref(context, key.convertToString());
         }
 
         @Override
         public IRubyObject op_aset(ThreadContext context, IRubyObject key, IRubyObject value) {
             if (!key.respondsTo("to_str")) {
                 throw getRuntime().newTypeError("can't convert " + key.getMetaClass() + " into String");
             }
             if (!value.respondsTo("to_str") && !value.isNil()) {
                 throw getRuntime().newTypeError("can't convert " + value.getMetaClass() + " into String");
             }
 
             if (value.isNil()) {
                 return super.delete(context, key, org.jruby.runtime.Block.NULL_BLOCK);
             }
             
             //return super.aset(getRuntime().newString("sadfasdF"), getRuntime().newString("sadfasdF"));
             return super.op_aset(context, RuntimeHelpers.invoke(context, key, "to_str"),
                     value.isNil() ? getRuntime().getNil() : RuntimeHelpers.invoke(context, value, "to_str"));
         }
         
         @JRubyMethod
         @Override
         public IRubyObject to_s(){
             return getRuntime().newString("ENV");
         }
     }
     
     public static void createGlobals(ThreadContext context, Ruby runtime) {
         runtime.defineGlobalConstant("TOPLEVEL_BINDING", runtime.newBinding());
         
         runtime.defineGlobalConstant("TRUE", runtime.getTrue());
         runtime.defineGlobalConstant("FALSE", runtime.getFalse());
         runtime.defineGlobalConstant("NIL", runtime.getNil());
         
         // define ARGV and $* for this runtime
         RubyArray argvArray = runtime.newArray();
         String[] argv = runtime.getInstanceConfig().getArgv();
         for (int i = 0; i < argv.length; i++) {
             argvArray.append(RubyString.newStringShared(runtime, argv[i].getBytes()));
         }
         runtime.defineGlobalConstant("ARGV", argvArray);
         runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(argvArray));
 
         IAccessor d = new ValueAccessor(runtime.newString(
                 runtime.getInstanceConfig().displayedFileName()));
         runtime.getGlobalVariables().define("$PROGRAM_NAME", d);
         runtime.getGlobalVariables().define("$0", d);
 
         // Version information:
         IRubyObject version = runtime.newString(Constants.RUBY_VERSION).freeze(context);
         IRubyObject release = runtime.newString(Constants.COMPILE_DATE).freeze(context);
         IRubyObject platform = runtime.newString(Constants.PLATFORM).freeze(context);
         IRubyObject engine = runtime.newString(Constants.ENGINE).freeze(context);
 
         runtime.defineGlobalConstant("RUBY_VERSION", version);
         runtime.defineGlobalConstant("RUBY_PATCHLEVEL", runtime.newString(Constants.RUBY_PATCHLEVEL).freeze(context));
         runtime.defineGlobalConstant("RUBY_RELEASE_DATE", release);
         runtime.defineGlobalConstant("RUBY_PLATFORM", platform);
         runtime.defineGlobalConstant("RUBY_ENGINE", engine);
 
         runtime.defineGlobalConstant("VERSION", version);
         runtime.defineGlobalConstant("RELEASE_DATE", release);
         runtime.defineGlobalConstant("PLATFORM", platform);
         
         IRubyObject jrubyVersion = runtime.newString(Constants.VERSION).freeze(context);
         runtime.defineGlobalConstant("JRUBY_VERSION", jrubyVersion);
 		
         GlobalVariable kcodeGV = new KCodeGlobalVariable(runtime, "$KCODE", runtime.newString("NONE"));
         runtime.defineVariable(kcodeGV);
         runtime.defineVariable(new GlobalVariable.Copy(runtime, "$-K", kcodeGV));
         IRubyObject defaultRS = runtime.newString(runtime.getInstanceConfig().getRecordSeparator()).freeze(context);
         GlobalVariable rs = new StringGlobalVariable(runtime, "$/", defaultRS);
         runtime.defineVariable(rs);
         runtime.setRecordSeparatorVar(rs);
         runtime.getGlobalVariables().setDefaultSeparator(defaultRS);
         runtime.defineVariable(new StringGlobalVariable(runtime, "$\\", runtime.getNil()));
         runtime.defineVariable(new StringGlobalVariable(runtime, "$,", runtime.getNil()));
 
         runtime.defineVariable(new LineNumberGlobalVariable(runtime, "$.", RubyFixnum.one(runtime)));
         runtime.defineVariable(new LastlineGlobalVariable(runtime, "$_"));
         runtime.defineVariable(new LastExitStatusVariable(runtime, "$?"));
 
         runtime.defineVariable(new ErrorInfoGlobalVariable(runtime, "$!", runtime.getNil()));
         runtime.defineVariable(new NonEffectiveGlobalVariable(runtime, "$=", runtime.getFalse()));
 
         if(runtime.getInstanceConfig().getInputFieldSeparator() == null) {
             runtime.defineVariable(new GlobalVariable(runtime, "$;", runtime.getNil()));
         } else {
             runtime.defineVariable(new GlobalVariable(runtime, "$;", RubyRegexp.newRegexp(runtime, runtime.getInstanceConfig().getInputFieldSeparator(), 0)));
         }
         
         Boolean verbose = runtime.getInstanceConfig().getVerbose();
         IRubyObject verboseValue = null;
         if (verbose == null) {
             verboseValue = runtime.getNil();
         } else if(verbose == Boolean.TRUE) {
             verboseValue = runtime.getTrue();
         } else {
             verboseValue = runtime.getFalse();
         }
         runtime.defineVariable(new VerboseGlobalVariable(runtime, "$VERBOSE", verboseValue));
         
         IRubyObject debug = runtime.newBoolean(runtime.getInstanceConfig().isDebug());
         runtime.defineVariable(new DebugGlobalVariable(runtime, "$DEBUG", debug));
         runtime.defineVariable(new DebugGlobalVariable(runtime, "$-d", debug));
 
         runtime.defineVariable(new SafeGlobalVariable(runtime, "$SAFE"));
 
         runtime.defineVariable(new BacktraceGlobalVariable(runtime, "$@"));
 
         IRubyObject stdin = new RubyIO(runtime, STDIO.IN);
         IRubyObject stdout = new RubyIO(runtime, STDIO.OUT);
         IRubyObject stderr = new RubyIO(runtime, STDIO.ERR);
 
         runtime.defineVariable(new InputGlobalVariable(runtime, "$stdin", stdin));
 
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stdout", stdout));
         runtime.getGlobalVariables().alias("$>", "$stdout");
         runtime.getGlobalVariables().alias("$defout", "$stdout");
 
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stderr", stderr));
         runtime.getGlobalVariables().alias("$deferr", "$stderr");
 
         runtime.defineGlobalConstant("STDIN", stdin);
         runtime.defineGlobalConstant("STDOUT", stdout);
         runtime.defineGlobalConstant("STDERR", stderr);
 
         runtime.defineVariable(new LoadedFeatures(runtime, "$\""));
         runtime.defineVariable(new LoadedFeatures(runtime, "$LOADED_FEATURES"));
 
         runtime.defineVariable(new LoadPath(runtime, "$:"));
         runtime.defineVariable(new LoadPath(runtime, "$-I"));
         runtime.defineVariable(new LoadPath(runtime, "$LOAD_PATH"));
         
         runtime.defineVariable(new MatchMatchGlobalVariable(runtime, "$&"));
         runtime.defineVariable(new PreMatchGlobalVariable(runtime, "$`"));
         runtime.defineVariable(new PostMatchGlobalVariable(runtime, "$'"));
         runtime.defineVariable(new LastMatchGlobalVariable(runtime, "$+"));
         runtime.defineVariable(new BackRefGlobalVariable(runtime, "$~"));
 
         // On platforms without a c-library accessable through JNA, getpid will return hashCode 
         // as $$ used to. Using $$ to kill processes could take down many runtimes, but by basing
         // $$ on getpid() where available, we have the same semantics as MRI.
         runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(runtime.getPosix().getpid())));
 
         // after defn of $stderr as the call may produce warnings
         defineGlobalEnvConstants(runtime);
         
         // Fixme: Do we need the check or does Main.java not call this...they should consolidate 
         if (runtime.getGlobalVariables().get("$*").isNil()) {
             runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(runtime.newArray()));
         }
         
         runtime.getGlobalVariables().defineReadonly("$-p", 
                 new ValueAccessor(runtime.getInstanceConfig().isAssumePrinting() ? runtime.getTrue() : runtime.getNil()));
         runtime.getGlobalVariables().defineReadonly("$-n", 
                 new ValueAccessor(runtime.getInstanceConfig().isAssumeLoop() ? runtime.getTrue() : runtime.getNil()));
         runtime.getGlobalVariables().defineReadonly("$-a", 
                 new ValueAccessor(runtime.getInstanceConfig().isSplit() ? runtime.getTrue() : runtime.getNil()));
         runtime.getGlobalVariables().defineReadonly("$-l", 
                 new ValueAccessor(runtime.getInstanceConfig().isProcessLineEnds() ? runtime.getTrue() : runtime.getNil()));
 
         // ARGF, $< object
         RubyArgsFile.initArgsFile(runtime);
     }
 
     private static void defineGlobalEnvConstants(Ruby runtime) {
 
     	Map environmentVariableMap = null;
     	OSEnvironment environment = new OSEnvironment();
     	try {
     		environmentVariableMap = environment.getEnvironmentVariableMap(runtime);
     	} catch (OSEnvironmentReaderExcepton e) {
     		// If the environment variables are not accessible shouldn't terminate 
     		runtime.getWarnings().warn(ID.MISCELLANEOUS, e.getMessage());
     	}
 		
     	if (environmentVariableMap == null) {
             // if the environment variables can't be obtained, define an empty ENV
     		environmentVariableMap = new HashMap();
     	}
 
         StringOnlyRubyHash h1 = new StringOnlyRubyHash(runtime,
                                                        environmentVariableMap, runtime.getNil());
         h1.getSingletonClass().defineAnnotatedMethods(StringOnlyRubyHash.class);
         runtime.defineGlobalConstant("ENV", h1);
 
         // Define System.getProperties() in ENV_JAVA
         Map systemProps = environment.getSystemPropertiesMap(runtime);
         runtime.defineGlobalConstant("ENV_JAVA", new StringOnlyRubyHash(
                 runtime, systemProps, runtime.getNil()));
         
     }
 
     private static class NonEffectiveGlobalVariable extends GlobalVariable {
         public NonEffectiveGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             runtime.getWarnings().warn(ID.INEFFECTIVE_GLOBAL, "warning: variable " + name + " is no longer effective; ignored", name);
             return value;
         }
 
         @Override
         public IRubyObject get() {
             runtime.getWarnings().warn(ID.INEFFECTIVE_GLOBAL, "warning: variable " + name + " is no longer effective", name);
             return runtime.getFalse();
         }
     }
 
     private static class LastExitStatusVariable extends GlobalVariable {
         public LastExitStatusVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             IRubyObject lastExitStatus = runtime.getCurrentContext().getLastExitStatus();
             return lastExitStatus == null ? runtime.getNil() : lastExitStatus;
         }
         
         @Override
         public IRubyObject set(IRubyObject lastExitStatus) {
             runtime.getCurrentContext().setLastExitStatus(lastExitStatus);
             
             return lastExitStatus;
         }
     }
 
     private static class MatchMatchGlobalVariable extends GlobalVariable {
         public MatchMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.last_match(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class PreMatchGlobalVariable extends GlobalVariable {
         public PreMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_pre(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class PostMatchGlobalVariable extends GlobalVariable {
         public PostMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_post(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class LastMatchGlobalVariable extends GlobalVariable {
         public LastMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RubyRegexp.match_last(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class BackRefGlobalVariable extends GlobalVariable {
         public BackRefGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         @Override
         public IRubyObject get() {
             return RuntimeHelpers.getBackref(runtime, runtime.getCurrentContext());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             RuntimeHelpers.setBackref(runtime, runtime.getCurrentContext(), value);
             return value;
         }
     }
 
     // Accessor methods.
 
     private static class LineNumberGlobalVariable extends GlobalVariable {
         public LineNumberGlobalVariable(Ruby runtime, String name, RubyFixnum value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
-            RubyArgsFile.setCurrentLineNumber(runtime.getGlobalVariables().get("$<"),RubyNumeric.fix2int(value));
+            RubyArgsFile.setCurrentLineNumber(runtime.getGlobalVariables().get("$<"), value);
             return super.set(value);
         }
     }
 
     private static class ErrorInfoGlobalVariable extends GlobalVariable {
         public ErrorInfoGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, null);
             set(value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() &&
                     !runtime.getException().isInstance(value) &&
                     !(JavaUtil.isJavaObject(value) && JavaUtil.unwrapJavaObject(value) instanceof Exception)) {
                 throw runtime.newTypeError("assigning non-exception to $!");
             }
             
             return runtime.getCurrentContext().setErrorInfo(value);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getCurrentContext().getErrorInfo();
         }
     }
 
     // FIXME: move out of this class!
     public static class StringGlobalVariable extends GlobalVariable {
         public StringGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() && ! (value instanceof RubyString)) {
                 throw runtime.newTypeError("value of " + name() + " must be a String");
             }
             return super.set(value);
         }
     }
 
     public static class KCodeGlobalVariable extends GlobalVariable {
         public KCodeGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getKCode().kcode(runtime);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             runtime.setKCode(KCode.create(runtime, value.convertToString().toString()));
             return value;
         }
     }
 
     private static class SafeGlobalVariable extends GlobalVariable {
         public SafeGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.newFixnum(runtime.getSafeLevel());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
 //            int level = RubyNumeric.fix2int(value);
 //            if (level < runtime.getSafeLevel()) {
 //            	throw runtime.newSecurityError("tried to downgrade safe level from " + 
 //            			runtime.getSafeLevel() + " to " + level);
 //            }
 //            runtime.setSafeLevel(level);
             // thread.setSafeLevel(level);
             runtime.getWarnings().warn(ID.SAFE_NOT_SUPPORTED, "SAFE levels are not supported in JRuby");
             return RubyFixnum.newFixnum(runtime, runtime.getSafeLevel());
         }
     }
 
     private static class VerboseGlobalVariable extends GlobalVariable {
         public VerboseGlobalVariable(Ruby runtime, String name, IRubyObject initialValue) {
             super(runtime, name, initialValue);
             set(initialValue);
         }
         
         @Override
         public IRubyObject get() {
             return runtime.getVerbose();
         }
 
         @Override
         public IRubyObject set(IRubyObject newValue) {
             if (newValue.isNil()) {
                 runtime.setVerbose(newValue);
             } else {
                 runtime.setVerbose(runtime.newBoolean(newValue.isTrue()));
             }
 
             return newValue;
         }
     }
 
     private static class DebugGlobalVariable extends GlobalVariable {
         public DebugGlobalVariable(Ruby runtime, String name, IRubyObject initialValue) {
             super(runtime, name, initialValue);
             set(initialValue);
         }
 
         @Override
         public IRubyObject get() {
             return runtime.getDebug();
         }
 
         @Override
         public IRubyObject set(IRubyObject newValue) {
             if (newValue.isNil()) {
                 runtime.setDebug(newValue);
             } else {
                 runtime.setDebug(runtime.newBoolean(newValue.isTrue()));
             }
 
             return newValue;
         }
     }
 
     private static class BacktraceGlobalVariable extends GlobalVariable {
         public BacktraceGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             IRubyObject errorInfo = runtime.getGlobalVariables().get("$!");
             IRubyObject backtrace = errorInfo.isNil() ? runtime.getNil() : errorInfo.callMethod(errorInfo.getRuntime().getCurrentContext(), "backtrace");
             //$@ returns nil if $!.backtrace is not an array
             if (!(backtrace instanceof RubyArray)) {
                 backtrace = runtime.getNil();
             }
             return backtrace;
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (runtime.getGlobalVariables().get("$!").isNil()) {
                 throw runtime.newArgumentError("$! not set.");
             }
             runtime.getGlobalVariables().get("$!").callMethod(value.getRuntime().getCurrentContext(), "set_backtrace", value);
             return value;
         }
     }
 
     private static class LastlineGlobalVariable extends GlobalVariable {
         public LastlineGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         @Override
         public IRubyObject get() {
             return RuntimeHelpers.getLastLine(runtime, runtime.getCurrentContext());
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             RuntimeHelpers.setLastLine(runtime, runtime.getCurrentContext(), value);
             return value;
         }
     }
 
     private static class InputGlobalVariable extends GlobalVariable {
         public InputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (value == get()) {
                 return value;
             }
             
             return super.set(value);
         }
     }
 
     private static class OutputGlobalVariable extends GlobalVariable {
         public OutputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         @Override
         public IRubyObject set(IRubyObject value) {
             if (value == get()) {
                 return value;
             }
             if (value instanceof RubyIO) {
                 RubyIO io = (RubyIO)value;
                 
                 // HACK: in order to have stdout/err act like ttys and flush always,
                 // we set anything assigned to stdout/stderr to sync
                 io.getHandler().setSync(true);
             }
 
             if (!value.respondsTo("write")) {
                 throw runtime.newTypeError(name() + " must have write method, " +
                                     value.getType().getName() + " given");
             }
 
             return super.set(value);
         }
     }
 
     private static class LoadPath extends ReadonlyGlobalVariable {
         public LoadPath(Ruby runtime, String name) {
             super(runtime, name, null);
         }
         
         /**
          * @see org.jruby.runtime.GlobalVariable#get()
          */
         @Override
         public IRubyObject get() {
             return runtime.getLoadService().getLoadPath();
         }
     }
 
     private static class LoadedFeatures extends ReadonlyGlobalVariable {
         public LoadedFeatures(Ruby runtime, String name) {
             super(runtime, name, null);
         }
         
         /**
          * @see org.jruby.runtime.GlobalVariable#get()
          */
         @Override
         public IRubyObject get() {
             return runtime.getLoadService().getLoadedFeatures();
         }
     }
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 3fe301145c..058d64f1ff 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1331 +1,1332 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
+ * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
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
 
 import java.io.ByteArrayOutputStream;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Random;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.*;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  */
 @JRubyModule(name="Kernel")
 public class RubyKernel {
     public final static Class<?> IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
 
         module.defineAnnotatedMethods(RubyKernel.class);
         module.defineAnnotatedMethods(RubyObject.class);
         
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
         
         module.setFlag(RubyObject.USER7_F, false); //Kernel is the only Module that doesn't need an implementor
 
         return module;
     }
 
     @JRubyMethod(name = "at_exit", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject at_exit(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().pushExitBlock(context.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject autoload_p(ThreadContext context, final IRubyObject recv, IRubyObject symbol) {
         Ruby runtime = context.getRuntime();
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         String name = module.getName() + "::" + symbol.asJavaString();
         
         IAutoloadMethod autoloadMethod = runtime.getLoadService().autoloadFor(name);
         if (autoloadMethod == null) return runtime.getNil();
 
         return runtime.newString(autoloadMethod.file());
     }
 
     @JRubyMethod(name = "autoload", required = 2, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         Ruby runtime = recv.getRuntime(); 
         final LoadService loadService = runtime.getLoadService();
         String nonInternedName = symbol.asJavaString();
         
         if (!IdUtil.isValidConstantName(nonInternedName)) {
             throw runtime.newNameError("autoload must be constant name", nonInternedName);
         }
         
         RubyString fileString = file.convertToString();
         
         if (fileString.isEmpty()) {
             throw runtime.newArgumentError("empty file name");
         }
         
         final String baseName = symbol.asJavaString().intern(); // interned, OK for "fast" methods
         final RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         String nm = module.getName() + "::" + baseName;
         
         IRubyObject existingValue = module.fastFetchConstant(baseName); 
         if (existingValue != null && existingValue != RubyObject.UNDEF) return runtime.getNil();
         
         module.fastStoreConstant(baseName, RubyObject.UNDEF);
         
         loadService.addAutoload(nm, new IAutoloadMethod() {
             public String file() {
                 return file.toString();
             }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 boolean required = loadService.require(file());
                 
                 // File to be loaded by autoload has already been or is being loaded.
                 if (!required) return null;
                 
                 return module.fastGetConstant(baseName);
             }
         });
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "method_missing", rest = true, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject method_missing(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw runtime.newArgumentError("no id given");
 
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime, 
                                                                  recv,
                                                                  args[0],
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, args[0], RubyArray.newArrayNoCopy(runtime, args, 1)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, args[0]};
         }
         
         throw new RaiseException((RubyException)exc.newInstance(context, exArgs, Block.NULL_BLOCK));
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
         Ruby runtime = context.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             return RubyIO.popen(context, runtime.getIO(), new IRubyObject[] {runtime.newString(command)}, block);
         } 
 
         return RubyFile.open(context, runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "getc", module = true, visibility = PRIVATE)
     public static IRubyObject getc(ThreadContext context, IRubyObject recv) {
         context.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "getc is obsolete; use STDIN.getc instead", "getc", "STDIN.getc");
         IRubyObject defin = context.getRuntime().getGlobalVariables().get("$stdin");
         return defin.callMethod(context, "getc");
     }
 
     @JRubyMethod(name = "gets", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gets(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.gets(context, context.getRuntime().getGlobalVariables().get("$<"), args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if(args.length == 1) {
             context.getRuntime().getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_array(ThreadContext context, IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.checkArrayType();
 
         if (value.isNil()) {
             if (object.getMetaClass().searchMethod("to_a").getImplementationClass() != context.getRuntime().getKernel()) {
                 value = object.callMethod(context, "to_a");
                 if (!(value instanceof RubyArray)) throw context.getRuntime().newTypeError("`to_a' did not return Array");
                 return value;
             } else {
                 return context.getRuntime().newArray(object);
             }
         }
         return value;
     }
 
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert");
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg);
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg0, arg1);
     }
     
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert");
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg);
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg0, arg1);
     }
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE)
     public static RubyFloat new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return (RubyFloat)object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getByteList().realSize == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = (RubyFloat)TypeConverter.convertToType(object, recv.getRuntime().getFloat(), "to_f");
             if (Double.isNaN(rFloat.getDoubleValue())) throw recv.getRuntime().newArgumentError("invalid value for Float()");
             return rFloat;
         }
     }
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_integer(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,0,true);
         }
         
         IRubyObject tmp = TypeConverter.convertToType(object, context.getRuntime().getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_string(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "p", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject p(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject defout = runtime.getGlobalVariables().get("$>");
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", RubyObject.inspect(context, args[i]));
                 defout.callMethod(context, "write", runtime.newString("\n"));
             }
         }
 
         IRubyObject result = runtime.getNil();
         if (runtime.getInstanceConfig().getCompatVersion() == CompatVersion.RUBY1_9) {
             if (args.length == 1) {
                 result = args[0];
             } else if (args.length > 1) {
                 result = runtime.newArray(args);
             }
         }
 
         if (defout instanceof RubyFile) {
             ((RubyFile)defout).flush();
         }
 
         return result;
     }
 
     /** rb_f_putc
      */
     @JRubyMethod(name = "putc", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject putc(ThreadContext context, IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(context, "putc", ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject puts(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         
         defout.callMethod(context, "puts", args);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject print(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         defout.callMethod(context, "print", args);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject printf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readline", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readline(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(context, recv, args);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, module = true, visibility = PRIVATE)
-    public static RubyArray readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+    public static IRubyObject readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.readlines(context, context.getRuntime().getGlobalVariables().get("$<"), args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(ThreadContext context, Ruby runtime) {
         IRubyObject line = context.getPreviousFrame().getLastLine();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, args, block);
     }
 
     @JRubyMethod(name = "sub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "sub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, arg1, block);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, args, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, arg1, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, args, block);
     }
 
     @JRubyMethod(name = "gsub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "gsub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, arg1, block);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, args, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, arg1, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chop_bang(ThreadContext context, IRubyObject recv, Block block) {
         return getLastlineString(context, context.getRuntime()).chop_bang();
     }
 
     @JRubyMethod(name = "chop", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chop(ThreadContext context, IRubyObject recv, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
 
         if (str.getByteList().realSize > 0) {
             str = (RubyString) str.dup();
             str.chop_bang();
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the zero or one-arg versions.
      */
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(args);
     }
 
     @JRubyMethod(name = "chomp!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).chomp_bang();
     }
 
     @JRubyMethod(name = "chomp!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(arg0);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the zero or one-arg versions.
      */
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "chomp", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang().isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "chomp", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(arg0).isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * 
      * @param context The thread context for the current thread
      * @param recv The receiver of the method (usually a class that has included Kernel)
      * @return
      * @deprecated Use the versions with zero, one, or two args.
      */
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(context, context.getRuntime()).split(context, args);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).split(context);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0, arg1);
     }
 
     @JRubyMethod(name = "scan", required = 1, frame = true, module = true, visibility = PRIVATE, reads = {LASTLINE, BACKREF}, writes = {LASTLINE, BACKREF})
     public static IRubyObject scan(ThreadContext context, IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(context, context.getRuntime()).scan(context, pattern, block);
     }
 
     @JRubyMethod(name = "select", required = 1, optional = 3, module = true, visibility = PRIVATE)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(context, context.getRuntime(), args);
     }
 
     @JRubyMethod(name = "sleep", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject sleep(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         long milliseconds;
 
         if (args.length == 0) {
             // Zero sleeps forever
             milliseconds = 0;
         } else {
             if (!(args[0] instanceof RubyNumeric)) {
                 throw context.getRuntime().newTypeError("can't convert " + args[0].getMetaClass().getName() + "into time interval");
             }
             milliseconds = (long) (args[0].convertToFloat().getDoubleValue() * 1000);
             if (milliseconds < 0) {
                 throw context.getRuntime().newArgumentError("time interval must be positive");
             } else if (milliseconds == 0) {
                 // Explicit zero in MRI returns immediately
                 return context.getRuntime().newFixnum(0);
             }
         }
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = context.getThread();
 
         // Spurious wakeup-loop
         do {
             long loopStartTime = System.currentTimeMillis();
             try {
                 // We break if we know this sleep was explicitly woken up/interrupted
                 if (!rubyThread.sleep(milliseconds)) break;
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis() - loopStartTime);
         } while (milliseconds > 0);
 
         return context.getRuntime().newFixnum(Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, false);
         return recv.getRuntime().getNil(); // not reached
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, true);
         return recv.getRuntime().getNil(); // not reached
     }
     
     private static void exit(Ruby runtime, IRubyObject[] args, boolean hard) {
         runtime.secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         if (hard) {
             throw new MainExitException(status, true);
         } else {
             throw runtime.newSystemExit(status);
         }
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE)
     public static RubyArray global_variables(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE)
     public static RubyArray local_variables(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime(), recv);
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyBinding binding_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return RubyBinding.newBinding(context.getRuntime());
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, frame = true, module = true, visibility = PRIVATE)
     public static RubyBoolean block_given_p(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newBoolean(context.getPreviousFrame().getBlock().isGiven());
     }
 
 
     @Deprecated
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         return sprintf(recv.getRuntime().getCurrentContext(), recv, args);
     }
 
     @JRubyMethod(name = {"sprintf", "format"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject sprintf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw context.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = context.getRuntime().newArrayNoCopy(args);
         newArgs.shift(context);
 
         return str.op_format(context, newArgs);
     }
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject raise(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Ruby runtime = context.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getRuntimeError(), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.getRuntimeError().newInstance(context, args, block));
             }
             
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
 
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!runtime.fastGetClass("Exception").isInstance(exception)) {
             throw runtime.newTypeError("exception object expected");
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
 
         if (runtime.getDebug().isTrue()) {
             printExceptionSummary(context, runtime, (RubyException) exception);
         }
 
         throw new RaiseException((RubyException) exception);
     }
 
     private static void printExceptionSummary(ThreadContext context, Ruby runtime, RubyException rEx) {
         Frame currentFrame = context.getCurrentFrame();
 
         String msg = String.format("Exception `%s' at %s:%s - %s\n",
                 rEx.getMetaClass(),
                 currentFrame.getFile(), currentFrame.getLine() + 1,
                 rEx.to_s());
 
         IRubyObject errorStream = runtime.getGlobalVariables().get("$stderr");
         errorStream.callMethod(context, "write", runtime.newString(msg));
     }
 
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(name = "require", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         Ruby runtime = recv.getRuntime();
         
         if (runtime.getLoadService().require(name.convertToString().toString())) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyString file = args[0].convertToString();
         boolean wrap = args.length == 2 ? args[1].isTrue() : false;
 
         runtime.getLoadService().load(file.getByteList().toString(), wrap);
         
         return runtime.getTrue();
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject eval(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
             
         // string to eval
         RubyString src = args[0].convertToString();
         runtime.checkSafeString(src);
         
         IRubyObject scope = args.length > 1 && !args[1].isNil() ? args[1] : null;
         String file;
         if (args.length > 2) {
             file = args[2].convertToString().toString();
         } else if (scope == null) {
             file = "(eval)";
         } else {
             file = null;
         }
         int line;
         if (args.length > 3) {
             line = (int) args[3].convertToInteger().getLongValue();
         } else if (scope == null) {
             line = 0;
         } else {
             line = -1;
         }
         if (scope == null) scope = RubyBinding.newBindingForEval(context);
         
         return ASTInterpreter.evalWithBinding(context, src, scope, file, line);
     }
 
     @JRubyMethod(name = "callcc", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject callcc(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.getRuntime();
         runtime.getWarnings().warn(ID.EMPTY_IMPLEMENTATION, "Kernel#callcc: Continuations are not implemented in JRuby and will not work", "Kernel#callcc");
         IRubyObject cc = runtime.getContinuation().callMethod(context, "new");
         cc.dataWrapStruct(block);
         return block.yield(context, cc);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level(" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "catch", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject rbCatch(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         CatchTarget target = new CatchTarget(tag.asJavaString());
         try {
             context.pushCatch(target);
             return block.yield(context, tag);
         } catch (JumpException.ThrowJump tj) {
             if (tj.getTarget() == target) return (IRubyObject) tj.getValue();
             
             throw tj;
         } finally {
             context.popCatch();
         }
     }
     
     public static class CatchTarget implements JumpTarget {
         private final String tag;
         public CatchTarget(String tag) { this.tag = tag; }
         public String getTag() { return tag; }
     }
 
     @JRubyMethod(name = "throw", required = 1, frame = true, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         String tag = args[0].asJavaString();
         CatchTarget[] catches = context.getActiveCatches();
 
         String message = "uncaught throw `" + tag + "'";
 
         // Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i].getTag())) {
                 //Catch active, throw for catch to handle
                 throw new JumpException.ThrowJump(catches[i], args.length > 1 ? args[1] : runtime.getNil());
             }
         }
 
         // No catch active for this throw
         RubyThread currentThread = context.getThread();
         if (currentThread == runtime.getThreadService().getMainThread()) {
             throw runtime.newNameError(message, tag);
         } else {
             throw runtime.newThreadError(message + " in thread 0x" + Integer.toHexString(RubyInteger.fix2int(currentThread.id())));
         }
     }
 
     @JRubyMethod(name = "trap", required = 1, frame = true, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject trap(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         context.getRuntime().getLoadService().require("jsignal");
         return RuntimeHelpers.invoke(context, recv, "__jtrap", args, block);
     }
     
     @JRubyMethod(name = "warn", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject warn(ThreadContext context, IRubyObject recv, IRubyObject message) {
         Ruby runtime = context.getRuntime();
         
         if (!runtime.getVerbose().isNil()) {
             IRubyObject out = runtime.getGlobalVariables().get("$stderr");
             RuntimeHelpers.invoke(context, out, "puts", message);
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "set_trace_func", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject set_trace_func(ThreadContext context, IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             context.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw context.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             context.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     @JRubyMethod(name = "trace_var", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject trace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
         RubyProc proc = null;
         String var = args.length > 1 ? args[0].toString() : null;
         // ignore if it's not a global var
         if (var.charAt(0) != '$') return context.getRuntime().getNil();
         if (args.length == 1) proc = RubyProc.newProc(context.getRuntime(), block, Block.Type.PROC);
         if (args.length == 2) {
             proc = (RubyProc)TypeConverter.convertToType(args[1], context.getRuntime().getProc(), "to_proc", true);
         }
         
         context.getRuntime().getGlobalVariables().setTraceVar(var, proc);
         
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "untrace_var", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject untrace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
         String var = args.length >= 1 ? args[0].toString() : null;
 
         // ignore if it's not a global var
         if (var.charAt(0) != '$') return context.getRuntime().getNil();
         
         if (args.length > 1) {
             ArrayList<IRubyObject> success = new ArrayList<IRubyObject>();
             for (int i = 1; i < args.length; i++) {
                 if (context.getRuntime().getGlobalVariables().untraceVar(var, args[i])) {
                     success.add(args[i]);
                 }
             }
             return RubyArray.newArray(context.getRuntime(), success);
         } else {
             context.getRuntime().getGlobalVariables().untraceVar(var);
         }
         
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_added", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_added(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_removed", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_removed(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_undefined", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_undefined(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = {"proc", "lambda"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyProc proc(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @Deprecated
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"lambda"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc lambda(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"proc"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc proc_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.PROC, block);
     }
 
     @JRubyMethod(name = "loop", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject loop(ThreadContext context, IRubyObject recv, Block block) {
         while (true) {
             block.yield(context, context.getRuntime().getNil());
 
             context.pollThreadEvents();
         }
     }
     
     @JRubyMethod(name = "test", required = 2, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject test(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) throw context.getRuntime().newArgumentError("wrong number of arguments");
 
         int cmd;
         if (args[0] instanceof RubyFixnum) {
             cmd = (int)((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubyString &&
                 ((RubyString) args[0]).getByteList().length() > 0) {
             // MRI behavior: use first byte of string value if len > 0
             cmd = ((RubyString) args[0]).getByteList().charAt(0);
         } else {
             cmd = (int) args[0].convertToInteger().getLongValue();
         }
         
         // MRI behavior: raise ArgumentError for 'unknown command' before
         // checking number of args.
         switch(cmd) {
         case 'A': case 'b': case 'c': case 'C': case 'd': case 'e': case 'f': case 'g': case 'G': 
         case 'k': case 'M': case 'l': case 'o': case 'O': case 'p': case 'r': case 'R': case 's':
         case 'S': case 'u': case 'w': case 'W': case 'x': case 'X': case 'z': case '=': case '<':
         case '>': case '-':
             break;
         default:
             throw context.getRuntime().newArgumentError("unknown command ?" + (char) cmd);
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-': case '=': case '<': case '>':
             if (args.length != 3) throw context.getRuntime().newArgumentError(args.length, 3);
             break;
         default:
             if (args.length != 2) throw context.getRuntime().newArgumentError(args.length, 2);
             break;
         }
         
         switch (cmd) {
         case 'A': // ?A  | Time    | Last access time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).atime();
         case 'b': // ?b  | boolean | True if file1 is a block device
             return RubyFileTest.blockdev_p(recv, args[1]);
         case 'c': // ?c  | boolean | True if file1 is a character device
             return RubyFileTest.chardev_p(recv, args[1]);
         case 'C': // ?C  | Time    | Last change time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).ctime();
         case 'd': // ?d  | boolean | True if file1 exists and is a directory
             return RubyFileTest.directory_p(recv, args[1]);
         case 'e': // ?e  | boolean | True if file1 exists
             return RubyFileTest.exist_p(recv, args[1]);
         case 'f': // ?f  | boolean | True if file1 exists and is a regular file
             return RubyFileTest.file_p(recv, args[1]);
         case 'g': // ?g  | boolean | True if file1 has the \CF{setgid} bit
             return RubyFileTest.setgid_p(recv, args[1]);
         case 'G': // ?G  | boolean | True if file1 exists and has a group ownership equal to the caller's group
             return RubyFileTest.grpowned_p(recv, args[1]);
         case 'k': // ?k  | boolean | True if file1 exists and has the sticky bit set
             return RubyFileTest.sticky_p(recv, args[1]);
         case 'M': // ?M  | Time    | Last modification time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtime();
         case 'l': // ?l  | boolean | True if file1 exists and is a symbolic link
             return RubyFileTest.symlink_p(recv, args[1]);
         case 'o': // ?o  | boolean | True if file1 exists and is owned by the caller's effective uid
             return RubyFileTest.owned_p(recv, args[1]);
         case 'O': // ?O  | boolean | True if file1 exists and is owned by the caller's real uid 
             return RubyFileTest.rowned_p(recv, args[1]);
         case 'p': // ?p  | boolean | True if file1 exists and is a fifo
             return RubyFileTest.pipe_p(recv, args[1]);
         case 'r': // ?r  | boolean | True if file1 is readable by the effective uid/gid of the caller
             return RubyFileTest.readable_p(recv, args[1]);
         case 'R': // ?R  | boolean | True if file is readable by the real uid/gid of the caller
             // FIXME: Need to implement an readable_real_p in FileTest
             return RubyFileTest.readable_p(recv, args[1]);
         case 's': // ?s  | int/nil | If file1 has nonzero size, return the size, otherwise nil
             return RubyFileTest.size_p(recv, args[1]);
         case 'S': // ?S  | boolean | True if file1 exists and is a socket
             return RubyFileTest.socket_p(recv, args[1]);
         case 'u': // ?u  | boolean | True if file1 has the setuid bit set
             return RubyFileTest.setuid_p(recv, args[1]);
         case 'w': // ?w  | boolean | True if file1 exists and is writable by effective uid/gid
             return RubyFileTest.writable_p(recv, args[1]);
         case 'W': // ?W  | boolean | True if file1 exists and is writable by the real uid/gid
             // FIXME: Need to implement an writable_real_p in FileTest
             return RubyFileTest.writable_p(recv, args[1]);
         case 'x': // ?x  | boolean | True if file1 exists and is executable by the effective uid/gid
             return RubyFileTest.executable_p(recv, args[1]);
         case 'X': // ?X  | boolean | True if file1 exists and is executable by the real uid/gid
             return RubyFileTest.executable_real_p(recv, args[1]);
         case 'z': // ?z  | boolean | True if file1 exists and has a zero length
             return RubyFileTest.zero_p(recv, args[1]);
         case '=': // ?=  | boolean | True if the modification times of file1 and file2 are equal
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeEquals(args[2]);
         case '<': // ?<  | boolean | True if the modification time of file1 is prior to that of file2
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeLessThan(args[2]);
         case '>': // ?>  | boolean | True if the modification time of file1 is after that of file2
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeGreaterThan(args[2]);
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             return RubyFileTest.identical_p(recv, args[1], args[2]);
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     @JRubyMethod(name = "`", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject backquote(ThreadContext context, IRubyObject recv, IRubyObject aString) {
         Ruby runtime = context.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         RubyString string = aString.convertToString();
         int resultCode = ShellLauncher.runAndWait(runtime, new IRubyObject[] {string}, output);
         
         runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newStringNoCopy(runtime, output.toByteArray());
     }
     
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
 
         // Not sure how well this works, but it works much better than
         // just currentTimeMillis by itself.
         runtime.setRandomSeed(System.currentTimeMillis() ^
                recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
                runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
 
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
     
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyInteger integerSeed = arg.convertToInteger("to_int");
         Ruby runtime = context.getRuntime();
 
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(integerSeed.getLongValue());
 
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyFloat.newFloat(runtime, runtime.getRandom().nextDouble());
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         Random random = runtime.getRandom();
 
         if (arg instanceof RubyBignum) {
             byte[] bigCeilBytes = ((RubyBignum) arg).getValue().toByteArray();
             BigInteger bigCeil = new BigInteger(bigCeilBytes).abs();
             byte[] randBytes = new byte[bigCeilBytes.length];
             random.nextBytes(randBytes);
             BigInteger result = new BigInteger(randBytes).abs().mod(bigCeil);
             return new RubyBignum(runtime, result); 
         }
 
         RubyInteger integerCeil = (RubyInteger)RubyKernel.new_integer(context, recv, arg); 
         long ceil = Math.abs(integerCeil.getLongValue());
         if (ceil == 0) return RubyFloat.newFloat(runtime, random.nextDouble());
         if (ceil > Integer.MAX_VALUE) return runtime.newFixnum(Math.abs(random.nextLong()) % ceil);
 
         return runtime.newFixnum(random.nextInt((int) ceil));
     }
 
     @JRubyMethod(name = "syscall", required = 1, optional = 9, module = true, visibility = PRIVATE)
     public static IRubyObject syscall(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         throw context.getRuntime().newNotImplementedError("Kernel#syscall is not implemented in JRuby");
     }
 
     @JRubyMethod(name = {"system"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static RubyBoolean system(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode;
         try {
             resultCode = ShellLauncher.runAndWait(runtime, args);
         } catch (Exception e) {
             resultCode = 127;
         }
         runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     @JRubyMethod(name = {"exec"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject exec(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode;
         try {
             // TODO: exec should replace the current process.
             // This could be possible with JNA. 
             resultCode = ShellLauncher.execAndWait(runtime, args);
         } catch (Exception e) {
             throw runtime.newErrnoENOENTError("cannot execute");
         }
         
         return exit(recv, new IRubyObject[] {runtime.newFixnum(resultCode)});
     }
 
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.getRuntime();
         
         if (!RubyInstanceConfig.FORK_ENABLED) {
             throw runtime.newNotImplementedError("fork is unsafe and disabled by default on JRuby");
         }
         
         if (block.isGiven()) {
             int pid = runtime.getPosix().fork();
             
             if (pid == 0) {
                 try {
                     block.yield(context, runtime.getNil());
                 } catch (RaiseException re) {
                     if (re.getException() instanceof RubySystemExit) {
                         throw re;
                     }
                     return exit_bang(recv, new IRubyObject[] {RubyFixnum.minus_one(runtime)});
                 } catch (Throwable t) {
                     return exit_bang(recv, new IRubyObject[] {RubyFixnum.minus_one(runtime)});
                 }
                 return exit_bang(recv, new IRubyObject[] {RubyFixnum.zero(runtime)});
             } else {
                 return runtime.newFixnum(pid);
             }
         } else {
             int result = runtime.getPosix().fork();
         
             if (result == -1) {
                 return runtime.getNil();
             }
 
             return runtime.newFixnum(result);
         }
     }
 
     @JRubyMethod(frame = true, module = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject tap(ThreadContext context, IRubyObject recv, Block block) {
         block.yield(context, recv);
         return recv;
     }
 
     @JRubyMethod(name = {"to_enum", "enum_for"}, rest = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject to_enum(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         switch (args.length) {
         case 0: return enumeratorize(runtime, recv, "each");
         case 1: return enumeratorize(runtime, recv, args[0].asJavaString());
         case 2: return enumeratorize(runtime, recv, args[0].asJavaString(), args[1]);
         default:
             IRubyObject enumArgs[] = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, enumArgs, 0, enumArgs.length);
             return enumeratorize(runtime, recv, args[0].asJavaString(), enumArgs);
         }
     }
 
     @JRubyMethod(name = { "__method__", "__callee__" }, module = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject __method__(ThreadContext context, IRubyObject recv) {
         Frame f = context.getCurrentFrame();
         String name = f != null ? f.getName() : null;
         return name != null ? context.getRuntime().newSymbol(name) : context.getRuntime().getNil();
     }
 }
