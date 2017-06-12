diff --git a/src/org/jruby/RubyArgsFile.java b/src/org/jruby/RubyArgsFile.java
index 8463a81342..b52bbd287f 100644
--- a/src/org/jruby/RubyArgsFile.java
+++ b/src/org/jruby/RubyArgsFile.java
@@ -1,402 +1,437 @@
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
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
-public class RubyArgsFile extends RubyObject {
+public class RubyArgsFile {
+    private static final class ArgsFileData {
+        private Ruby runtime;
+        public ArgsFileData(Ruby runtime) {
+            this.runtime = runtime;
+        }
 
-    public RubyArgsFile(Ruby runtime) {
-        super(runtime, runtime.getObject());
-    }
+        public IRubyObject currentFile;
+        public int currentLineNumber;
+        public boolean startedProcessing = false; 
+        public boolean finishedProcessing = false;
 
-    private IRubyObject currentFile;
-    private int currentLineNumber;
-    private boolean startedProcessing = false; 
-    private boolean finishedProcessing = false;
-    
-    public void setCurrentLineNumber(int newLineNumber) {
-        this.currentLineNumber = newLineNumber;
-    }
-    
-    public void initArgsFile() {
-        getRuntime().getEnumerable().extend_object(this);
-        
-        getRuntime().defineReadonlyVariable("$<", this);
-        getRuntime().defineGlobalConstant("ARGF", this);
-        
-        RubyClass argfClass = getMetaClass();
-        argfClass.defineAnnotatedMethods(RubyArgsFile.class);
-        getRuntime().defineReadonlyVariable("$FILENAME", getRuntime().newString("-"));
-    }
+        public boolean nextArgsFile() {
+            if (finishedProcessing) {
+                return false;
+            }
 
-    protected boolean nextArgsFile() {
-        if (finishedProcessing) {
-            return false;
-        }
+            RubyArray args = (RubyArray)runtime.getGlobalVariables().get("$*");
+
+            if (args.getLength() == 0) {
+                if (!startedProcessing) { 
+                    currentFile = runtime.getGlobalVariables().get("$stdin");
+                    ((RubyString) runtime.getGlobalVariables().get("$FILENAME")).setValue(new StringBuffer("-"));
+                    currentLineNumber = 0;
+                    startedProcessing = true;
+                    return true;
+                } else {
+                    finishedProcessing = true;
+                    return false;
+                }
+            }
 
-        RubyArray args = (RubyArray)getRuntime().getGlobalVariables().get("$*");
+            String filename = args.shift().toString();
+            ((RubyString) runtime.getGlobalVariables().get("$FILENAME")).setValue(new StringBuffer(filename));
 
-        if (args.getLength() == 0) {
-            if (!startedProcessing) { 
-                currentFile = getRuntime().getGlobalVariables().get("$stdin");
-                ((RubyString) getRuntime().getGlobalVariables().get("$FILENAME")).setValue(new StringBuffer("-"));
-                currentLineNumber = 0;
-                startedProcessing = true;
-                return true;
+            if (filename.equals("-")) {
+                currentFile = runtime.getGlobalVariables().get("$stdin");
             } else {
-                finishedProcessing = true;
-                return false;
+                currentFile = RubyFile.open(runtime.getFile(), new IRubyObject[] {runtime.newString(filename)}, Block.NULL_BLOCK); 
             }
+            
+            startedProcessing = true;
+            return true;
         }
 
-        String filename = args.shift().toString();
-        ((RubyString) getRuntime().getGlobalVariables().get("$FILENAME")).setValue(new StringBuffer(filename));
-
-        if (filename.equals("-")) {
-            currentFile = getRuntime().getGlobalVariables().get("$stdin");
-        } else {
-            currentFile = RubyFile.open(getRuntime().getFile(), new IRubyObject[] {getRuntime().newString(filename)}, Block.NULL_BLOCK); 
+        public static ArgsFileData getDataFrom(IRubyObject recv) {
+            ArgsFileData data = (ArgsFileData)recv.dataGetStruct();
+            if(data == null) {
+                data = new ArgsFileData(recv.getRuntime());
+                recv.dataWrapStruct(data);
+            }
+            return data;
         }
+    }    
+    
+    public static void setCurrentLineNumber(IRubyObject recv, int newLineNumber) {
+        ArgsFileData.getDataFrom(recv).currentLineNumber = newLineNumber;
+    }
+
+    public static void initArgsFile(Ruby runtime) {
+        RubyObject argsFile = new RubyObject(runtime, runtime.getObject());
 
-        startedProcessing = true;
-        return true;
+        runtime.getEnumerable().extend_object(argsFile);
+        
+        runtime.defineReadonlyVariable("$<", argsFile);
+        runtime.defineGlobalConstant("ARGF", argsFile);
+        
+        RubyClass argfClass = argsFile.getMetaClass();
+        argfClass.defineAnnotatedMethods(RubyArgsFile.class);
+        runtime.defineReadonlyVariable("$FILENAME", runtime.newString("-"));
     }
 
+
+
     @JRubyMethod(name = {"fileno", "to_i"})
-    public IRubyObject fileno() {
-        if(!startedProcessing && !nextArgsFile()) {
-            throw getRuntime().newArgumentError("no stream");
+    public static IRubyObject fileno(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+
+        if(!data.startedProcessing && !data.nextArgsFile()) {
+            throw recv.getRuntime().newArgumentError("no stream");
         }
-        return ((RubyIO)currentFile).fileno();
+        return ((RubyIO)data.currentFile).fileno();
     }
 
     @JRubyMethod(name = "to_io")
-    public IRubyObject to_io() {
-        if(currentFile == null && !nextArgsFile()) {
-            throw getRuntime().newArgumentError("no stream");
+    public static IRubyObject to_io(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+
+        if(!data.startedProcessing && !data.nextArgsFile()) {
+            throw recv.getRuntime().newArgumentError("no stream");
         }
-        return currentFile;
+        return data.currentFile;
     }
     
-    public IRubyObject internalGets(IRubyObject[] args) {
-        if (currentFile == null && !nextArgsFile()) {
-            return getRuntime().getNil();
+    public static IRubyObject internalGets(IRubyObject recv, IRubyObject[] args) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            return recv.getRuntime().getNil();
         }
         
-        ThreadContext context = getRuntime().getCurrentContext();
+        ThreadContext context = recv.getRuntime().getCurrentContext();
         
-        IRubyObject line = currentFile.callMethod(context, "gets", args);
+        IRubyObject line = data.currentFile.callMethod(context, "gets", args);
         
         while (line instanceof RubyNil) {
-            currentFile.callMethod(context, "close");
-            if (!nextArgsFile()) {
-                currentFile = null;
+            data.currentFile.callMethod(context, "close");
+            if (!data.nextArgsFile()) {
+                data.currentFile = null;
                 return line;
         	}
-            line = currentFile.callMethod(context, "gets", args);
+            line = data.currentFile.callMethod(context, "gets", args);
         }
         
-        currentLineNumber++;
-        getRuntime().getGlobalVariables().set("$.", getRuntime().newFixnum(currentLineNumber));
+        data.currentLineNumber++;
+        recv.getRuntime().getGlobalVariables().set("$.", recv.getRuntime().newFixnum(data.currentLineNumber));
         
         return line;
     }
     
     // ARGF methods
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "gets", optional = 1, frame = true)
-    public IRubyObject gets(IRubyObject[] args) {
-        IRubyObject result = internalGets(args);
+    public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
+        IRubyObject result = internalGets(recv, args);
 
         if (!result.isNil()) {
-            getRuntime().getCurrentContext().getCurrentFrame().setLastLine(result);
+            recv.getRuntime().getCurrentContext().getCurrentFrame().setLastLine(result);
         }
 
         return result;
     }
     
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "readline", optional = 1, frame = true)
-    public IRubyObject readline(IRubyObject[] args) {
-        IRubyObject line = gets(args);
+    public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
+        IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
-            throw getRuntime().newEOFError();
+            throw recv.getRuntime().newEOFError();
         }
         
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, frame = true)
-    public RubyArray readlines(IRubyObject[] args) {
+    public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
         IRubyObject[] separatorArgument;
         if (args.length > 0) {
-            if (!getRuntime().getNilClass().isInstance(args[0]) &&
-                !getRuntime().getString().isInstance(args[0])) {
-                throw getRuntime().newTypeError(args[0], 
-                        getRuntime().getString());
+            if (!recv.getRuntime().getNilClass().isInstance(args[0]) &&
+                !recv.getRuntime().getString().isInstance(args[0])) {
+                throw recv.getRuntime().newTypeError(args[0], 
+                        recv.getRuntime().getString());
             } 
             separatorArgument = new IRubyObject[] { args[0] };
         } else {
             separatorArgument = IRubyObject.NULL_ARRAY;
         }
 
-        RubyArray result = getRuntime().newArray();
+        RubyArray result = recv.getRuntime().newArray();
         IRubyObject line;
-        while (! (line = internalGets(separatorArgument)).isNil()) {
+        while (! (line = internalGets(recv, separatorArgument)).isNil()) {
             result.append(line);
         }
         return result;
     }
     
     @JRubyMethod(name = "each_byte", frame = true)
-    public IRubyObject each_byte(Block block) {
+    public static IRubyObject each_byte(IRubyObject recv, Block block) {
         IRubyObject bt;
-        ThreadContext ctx = getRuntime().getCurrentContext();
+        ThreadContext ctx = recv.getRuntime().getCurrentContext();
 
-        while(!(bt = getc()).isNil()) {
+        while(!(bt = getc(recv)).isNil()) {
             block.yield(ctx, bt);
         }
 
-        return this;
+        return recv;
     }
 
     /** Invoke a block for each line.
      *
      */
     @JRubyMethod(name = "each_line", alias = {"each"}, optional = 1, frame = true)
-    public IRubyObject each_line(IRubyObject[] args, Block block) {
-        IRubyObject nextLine = internalGets(args);
+    public static IRubyObject each_line(IRubyObject recv, IRubyObject[] args, Block block) {
+        IRubyObject nextLine = internalGets(recv, args);
         
         while (!nextLine.isNil()) {
-        	block.yield(getRuntime().getCurrentContext(), nextLine);
-        	nextLine = internalGets(args);
+        	block.yield(recv.getRuntime().getCurrentContext(), nextLine);
+        	nextLine = internalGets(recv, args);
         }
         
-        return this;
+        return recv;
     }
 
     @JRubyMethod(name = "file")
-    public IRubyObject file() {
-        if(currentFile == null && !nextArgsFile()) {
-            return getRuntime().getNil();
+    public static IRubyObject file(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            return recv.getRuntime().getNil();
         }
-        return currentFile;
+        return data.currentFile;
     }
 
     @JRubyMethod(name = "skip")
-    public IRubyObject skip() {
-        currentFile = null;
-        return this;
+    public static IRubyObject skip(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        data.currentFile = null;
+        return recv;
     }
 
     @JRubyMethod(name = "close")
-    public IRubyObject close() {
-        if(currentFile == null && !nextArgsFile()) {
-            return this;
+    public static IRubyObject close(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            return recv;
         }
-        currentFile = null;
-        currentLineNumber = 0;
-        return this;
+        data.currentFile = null;
+        data.currentLineNumber = 0;
+        return recv;
     }
 
     @JRubyMethod(name = "closed?")
-    public IRubyObject closed_p() {
-        if(currentFile == null && !nextArgsFile()) {
-            return this;
+    public static IRubyObject closed_p(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            return recv;
         }
-        return ((RubyIO)currentFile).closed_p();
+        return ((RubyIO)data.currentFile).closed_p();
     }
 
     @JRubyMethod(name = "binmode")
-    public IRubyObject binmode() {
-        if(currentFile == null && !nextArgsFile()) {
-            throw getRuntime().newArgumentError("no stream");
+    public static IRubyObject binmode(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            throw recv.getRuntime().newArgumentError("no stream");
         }
         
-        return ((RubyIO)currentFile).binmode();
+        return ((RubyIO)data.currentFile).binmode();
     }
 
     @JRubyMethod(name = "lineno")
-    public IRubyObject lineno() {
-        return getRuntime().newFixnum(currentLineNumber);
+    public static IRubyObject lineno(IRubyObject recv) {
+        return recv.getRuntime().newFixnum(ArgsFileData.getDataFrom(recv).currentLineNumber);
     }
 
     @JRubyMethod(name = "tell", alias = {"pos"})
-    public IRubyObject tell() {
-        if(currentFile == null && !nextArgsFile()) {
-            throw getRuntime().newArgumentError("no stream to tell");
+    public static IRubyObject tell(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            throw recv.getRuntime().newArgumentError("no stream to tell");
         }
-        return ((RubyIO)currentFile).pos();
+        return ((RubyIO)data.currentFile).pos();
     }
 
     @JRubyMethod(name = "rewind")
-    public IRubyObject rewind() {
-        if(currentFile == null && !nextArgsFile()) {
-            throw getRuntime().newArgumentError("no stream to rewind");
+    public static IRubyObject rewind(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            throw recv.getRuntime().newArgumentError("no stream to rewind");
         }
-        return ((RubyIO)currentFile).rewind();
+        return ((RubyIO)data.currentFile).rewind();
     }
 
     @JRubyMethod(name = {"eof", "eof?"})
-    public IRubyObject eof() {
-        if(currentFile != null && !nextArgsFile()) {
-            return getRuntime().getTrue();
+    public static IRubyObject eof(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        if(data.currentFile != null && !data.nextArgsFile()) {
+            return recv.getRuntime().getTrue();
         }
 
-        return ((RubyIO)currentFile).eof_p();
+        return ((RubyIO)data.currentFile).eof_p();
     }
 
     @JRubyMethod(name = "pos=", required = 1)
-    public IRubyObject set_pos(IRubyObject offset) {
-        if(currentFile == null && !nextArgsFile()) {
-            throw getRuntime().newArgumentError("no stream to set position");
+    public static IRubyObject set_pos(IRubyObject recv, IRubyObject offset) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            throw recv.getRuntime().newArgumentError("no stream to set position");
         }
-        return ((RubyIO)currentFile).pos_set(offset);
+        return ((RubyIO)data.currentFile).pos_set(offset);
     }
 
     @JRubyMethod(name = "seek", required = 1, optional = 1)
-    public IRubyObject seek(IRubyObject[] args) {
-        if(currentFile == null && !nextArgsFile()) {
-            throw getRuntime().newArgumentError("no stream to seek");
+    public static IRubyObject seek(IRubyObject recv, IRubyObject[] args) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        if(data.currentFile == null && !data.nextArgsFile()) {
+            throw recv.getRuntime().newArgumentError("no stream to seek");
         }
-        return ((RubyIO)currentFile).seek(args);
+        return ((RubyIO)data.currentFile).seek(args);
     }
 
     @JRubyMethod(name = "lineno=", required = 1)
-    public IRubyObject set_lineno(IRubyObject line) {
-        currentLineNumber = RubyNumeric.fix2int(line);
-        return getRuntime().getNil();
+    public static IRubyObject set_lineno(IRubyObject recv, IRubyObject line) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
+        data.currentLineNumber = RubyNumeric.fix2int(line);
+        return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readchar")
-    public IRubyObject readchar() {
-        IRubyObject c = getc();
+    public static IRubyObject readchar(IRubyObject recv) {
+        IRubyObject c = getc(recv);
         if(c.isNil()) {
-            throw getRuntime().newEOFError();
+            throw recv.getRuntime().newEOFError();
         }
         return c;
     }
 
     @JRubyMethod(name = "getc")
-    public IRubyObject getc() {
+    public static IRubyObject getc(IRubyObject recv) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
         IRubyObject bt;
         while(true) {
-            if(currentFile == null && !nextArgsFile()) {
-                return getRuntime().getNil();
+            if(data.currentFile == null && !data.nextArgsFile()) {
+                return recv.getRuntime().getNil();
             }
-            if(!(currentFile instanceof RubyFile)) {
-                bt = currentFile.callMethod(getRuntime().getCurrentContext(),"getc");
+            if(!(data.currentFile instanceof RubyFile)) {
+                bt = data.currentFile.callMethod(recv.getRuntime().getCurrentContext(),"getc");
             } else {
-                bt = ((RubyIO)currentFile).getc();
+                bt = ((RubyIO)data.currentFile).getc();
             }
             if(bt.isNil()) {
-                currentFile = null;
+                data.currentFile = null;
                 continue;
             }
             return bt;
         }
     }
 
     @JRubyMethod(name = "read", optional = 2)
-    public IRubyObject read(IRubyObject[] args) {
+    public static IRubyObject read(IRubyObject recv, IRubyObject[] args) {
+        ArgsFileData data = ArgsFileData.getDataFrom(recv);
         IRubyObject tmp, str, length;
         long len = 0;
         if(args.length > 0) {
             length = args[0];
             if(args.length > 1) {
                 str = args[1];
             } else {
-                str = getRuntime().getNil();
+                str = recv.getRuntime().getNil();
             }
         } else {
-            length = getRuntime().getNil();
-            str = getRuntime().getNil();
+            length = recv.getRuntime().getNil();
+            str = recv.getRuntime().getNil();
         }
 
         if(!length.isNil()) {
             len = RubyNumeric.num2long(length);
         }
         if(!str.isNil()) {
             str = str.convertToString();
             ((RubyString)str).modify();
             ((RubyString)str).getByteList().length(0);
-            args[1] = getRuntime().getNil();
+            args[1] = recv.getRuntime().getNil();
         }
         while(true) {
-            if(currentFile == null && !nextArgsFile()) {
+            if(data.currentFile == null && !data.nextArgsFile()) {
                 return str;
             }
-            if(!(currentFile instanceof RubyIO)) {
-                tmp = currentFile.callMethod(getRuntime().getCurrentContext(),"read",args);
+            if(!(data.currentFile instanceof RubyIO)) {
+                tmp = data.currentFile.callMethod(recv.getRuntime().getCurrentContext(),"read",args);
             } else {
-                tmp = ((RubyIO)currentFile).read(args);
+                tmp = ((RubyIO)data.currentFile).read(args);
             }
             if(str.isNil()) {
                 str = tmp;
             } else if(!tmp.isNil()) {
                 ((RubyString)str).append(tmp);
             }
             if(tmp.isNil() || length.isNil()) {
-                currentFile = null;
+                data.currentFile = null;
                 continue;
             } else if(args.length >= 1) {
                 if(((RubyString)str).getByteList().length() < len) {
                     len -= ((RubyString)str).getByteList().length();
-                    args[0] = getRuntime().newFixnum(len);
+                    args[0] = recv.getRuntime().newFixnum(len);
                     continue;
                 }
             }
             return str;
         }
     }
 
     @JRubyMethod(name = "filename", alias = {"path"})
-    public RubyString filename() {
-        return (RubyString)getRuntime().getGlobalVariables().get("$FILENAME");
+    public static RubyString filename(IRubyObject recv) {
+        return (RubyString)recv.getRuntime().getGlobalVariables().get("$FILENAME");
     }
 
-    @JRubyMethod(name = "to_s")
-    public IRubyObject to_s() {
-        return getRuntime().newString("ARGF");
+    @JRubyMethod(name = "to_s") 
+    public static IRubyObject to_s(IRubyObject recv) {
+        return recv.getRuntime().newString("ARGF");
     }
 }
diff --git a/src/org/jruby/RubyGlobal.java b/src/org/jruby/RubyGlobal.java
index 6717948a90..8d43e85bb5 100644
--- a/src/org/jruby/RubyGlobal.java
+++ b/src/org/jruby/RubyGlobal.java
@@ -1,585 +1,585 @@
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
 
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.environment.OSEnvironmentReaderExcepton;
 import org.jruby.environment.OSEnvironment;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.MethodIndex;
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
 
         public IRubyObject op_aref(IRubyObject key) {
             if (!key.respondsTo("to_str")) {
                 throw getRuntime().newTypeError("can't convert " + key.getMetaClass() + " into String");
             }
 
             return super.op_aref(RuntimeHelpers.invoke(getRuntime().getCurrentContext(), key, MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY));
         }
 
         public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
             if (!key.respondsTo("to_str")) {
                 throw getRuntime().newTypeError("can't convert " + key.getMetaClass() + " into String");
             }
             if (!value.respondsTo("to_str") && !value.isNil()) {
                 throw getRuntime().newTypeError("can't convert " + value.getMetaClass() + " into String");
             }
 
             if (value.isNil()) {
                 return super.delete(key, org.jruby.runtime.Block.NULL_BLOCK);
             }
             
             ThreadContext context = getRuntime().getCurrentContext();
             //return super.aset(getRuntime().newString("sadfasdF"), getRuntime().newString("sadfasdF"));
             return super.op_aset(RuntimeHelpers.invoke(context, key, MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY),
                     value.isNil() ? getRuntime().getNil() : RuntimeHelpers.invoke(context, value, MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY));
         }
         
         public IRubyObject to_s(){
             return getRuntime().newString("ENV");
         }
     }
     
     public static void createGlobals(Ruby runtime) {
         runtime.defineGlobalConstant("TOPLEVEL_BINDING", runtime.newBinding());
         
         runtime.defineGlobalConstant("TRUE", runtime.getTrue());
         runtime.defineGlobalConstant("FALSE", runtime.getFalse());
         runtime.defineGlobalConstant("NIL", runtime.getNil());
         
         // define ARGV and $* for this runtime
         RubyArray argvArray = runtime.newArray();
         String[] argv = runtime.getInstanceConfig().getArgv();
         for (int i = 0; i < argv.length; i++) {
             argvArray.add(runtime.newString(argv[i]));
         }
         runtime.defineGlobalConstant("ARGV", argvArray);
         runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(argvArray));
         
         // Version information:
         IRubyObject version = runtime.newString(Constants.RUBY_VERSION).freeze();
         IRubyObject release = runtime.newString(Constants.COMPILE_DATE).freeze();
         IRubyObject platform = runtime.newString(Constants.PLATFORM).freeze();
 
         runtime.defineGlobalConstant("RUBY_VERSION", version);
         runtime.defineGlobalConstant("RUBY_PATCHLEVEL", runtime.newString(Constants.RUBY_PATCHLEVEL).freeze());
         runtime.defineGlobalConstant("RUBY_RELEASE_DATE", release);
         runtime.defineGlobalConstant("RUBY_PLATFORM", platform);
 
         runtime.defineGlobalConstant("VERSION", version);
         runtime.defineGlobalConstant("RELEASE_DATE", release);
         runtime.defineGlobalConstant("PLATFORM", platform);
         
         IRubyObject jrubyVersion = runtime.newString(Constants.VERSION).freeze();
         runtime.defineGlobalConstant("JRUBY_VERSION", jrubyVersion);
 		
         GlobalVariable kcodeGV = new KCodeGlobalVariable(runtime, "$KCODE", runtime.newString("NONE"));
         runtime.defineVariable(kcodeGV);
         runtime.defineVariable(new GlobalVariable.Copy(runtime, "$-K", kcodeGV));
         IRubyObject defaultRS = runtime.newString(runtime.getInstanceConfig().getRecordSeparator()).freeze();
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
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stderr", stderr));
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$>", stdout));
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$defout", stdout));
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$deferr", stderr));
 
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
-        new RubyArgsFile(runtime).initArgsFile();
+        RubyArgsFile.initArgsFile(runtime);
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
         org.jruby.runtime.CallbackFactory cf = org.jruby.runtime.CallbackFactory.createFactory(runtime, StringOnlyRubyHash.class);
         h1.getSingletonClass().defineFastMethod("to_s", cf.getFastMethod("to_s"));
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
 
         public IRubyObject set(IRubyObject value) {
             runtime.getWarnings().warn(ID.INEFFECTIVE_GLOBAL, "warning: variable " + name + " is no longer effective; ignored", name);
             return value;
         }
 
         public IRubyObject get() {
             runtime.getWarnings().warn(ID.INEFFECTIVE_GLOBAL, "warning: variable " + name + " is no longer effective", name);
             return runtime.getFalse();
         }
     }
 
     private static class LastExitStatusVariable extends GlobalVariable {
         public LastExitStatusVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         public IRubyObject get() {
             IRubyObject lastExitStatus = runtime.getCurrentContext().getLastExitStatus();
             return lastExitStatus == null ? runtime.getNil() : lastExitStatus;
         }
         
         public IRubyObject set(IRubyObject lastExitStatus) {
             runtime.getCurrentContext().setLastExitStatus(lastExitStatus);
             
             return lastExitStatus;
         }
     }
 
     private static class MatchMatchGlobalVariable extends GlobalVariable {
         public MatchMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         public IRubyObject get() {
             return RubyRegexp.last_match(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class PreMatchGlobalVariable extends GlobalVariable {
         public PreMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         public IRubyObject get() {
             return RubyRegexp.match_pre(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class PostMatchGlobalVariable extends GlobalVariable {
         public PostMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         public IRubyObject get() {
             return RubyRegexp.match_post(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class LastMatchGlobalVariable extends GlobalVariable {
         public LastMatchGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         public IRubyObject get() {
             return RubyRegexp.match_last(runtime.getCurrentContext().getCurrentFrame().getBackRef());
         }
     }
 
     private static class BackRefGlobalVariable extends GlobalVariable {
         public BackRefGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, runtime.getNil());
         }
         
         public IRubyObject get() {
             IRubyObject ret = runtime.getCurrentContext().getCurrentFrame().getBackRef();
             if(ret instanceof RubyMatchData) {
                 ((RubyMatchData)ret).use();
             }
             return ret;
         }
     }
 
     // Accessor methods.
 
     private static class LineNumberGlobalVariable extends GlobalVariable {
         public LineNumberGlobalVariable(Ruby runtime, String name, RubyFixnum value) {
             super(runtime, name, value);
         }
 
         public IRubyObject set(IRubyObject value) {
-            ((RubyArgsFile) runtime.getGlobalVariables().get("$<")).setCurrentLineNumber(RubyNumeric.fix2int(value));
+            RubyArgsFile.setCurrentLineNumber(runtime.getGlobalVariables().get("$<"),RubyNumeric.fix2int(value));
             return super.set(value);
         }
     }
 
     private static class ErrorInfoGlobalVariable extends GlobalVariable {
         public ErrorInfoGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, null);
             set(value);
         }
 
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() && ! runtime.getException().isInstance(value)) {
                 throw runtime.newTypeError("assigning non-exception to $!");
             }
             
             return runtime.getCurrentContext().setErrorInfo(value);
         }
 
         public IRubyObject get() {
             return runtime.getCurrentContext().getErrorInfo();
         }
     }
 
     // FIXME: move out of this class!
     public static class StringGlobalVariable extends GlobalVariable {
         public StringGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
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
 
         public IRubyObject get() {
             return runtime.getKCode().kcode(runtime);
         }
 
         public IRubyObject set(IRubyObject value) {
             runtime.setKCode(KCode.create(runtime, value.convertToString().toString()));
             return value;
         }
     }
 
     private static class SafeGlobalVariable extends GlobalVariable {
         public SafeGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         public IRubyObject get() {
             return runtime.newFixnum(runtime.getSafeLevel());
         }
 
         public IRubyObject set(IRubyObject value) {
             int level = RubyNumeric.fix2int(value);
             if (level < runtime.getSafeLevel()) {
             	throw runtime.newSecurityError("tried to downgrade safe level from " + 
             			runtime.getSafeLevel() + " to " + level);
             }
             runtime.setSafeLevel(level);
             // thread.setSafeLevel(level);
             return value;
         }
     }
 
     private static class VerboseGlobalVariable extends GlobalVariable {
         public VerboseGlobalVariable(Ruby runtime, String name, IRubyObject initialValue) {
             super(runtime, name, initialValue);
             set(initialValue);
         }
         
         public IRubyObject get() {
             return runtime.getVerbose();
         }
 
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
 
         public IRubyObject get() {
             return runtime.getDebug();
         }
 
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
 
         public IRubyObject get() {
             IRubyObject errorInfo = runtime.getGlobalVariables().get("$!");
             IRubyObject backtrace = errorInfo.isNil() ? runtime.getNil() : errorInfo.callMethod(errorInfo.getRuntime().getCurrentContext(), "backtrace");
             //$@ returns nil if $!.backtrace is not an array
             if (!(backtrace instanceof RubyArray)) {
                 backtrace = runtime.getNil();
             }
             return backtrace;
         }
 
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
 
         public IRubyObject get() {
             return runtime.getCurrentContext().getCurrentFrame().getLastLine();
         }
 
         public IRubyObject set(IRubyObject value) {
             runtime.getCurrentContext().getCurrentFrame().setLastLine(value);
             return value;
         }
     }
 
     private static class InputGlobalVariable extends GlobalVariable {
         public InputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
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
             if (! value.respondsTo("write")) {
                 throw runtime.newTypeError(name() + " must have write method, " +
                                     value.getType().getName() + " given");
             }
             
             if ("$stderr".equals(name())) {
                 runtime.defineVariable(new OutputGlobalVariable(runtime, "$deferr", value));
             }
             
             if ("$stdout".equals(name())) {
                 runtime.defineVariable(new OutputGlobalVariable(runtime, "$defout", value));
                 runtime.defineVariable(new OutputGlobalVariable(runtime, "$>", value));
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
         public IRubyObject get() {
             return runtime.getLoadService().getLoadedFeatures();
         }
     }
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index e4935a3625..1d179bca35 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1172 +1,1172 @@
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
 import java.io.IOException;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import org.jruby.anno.JRubyMethod;
 
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
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.Sprintf;
 import org.jruby.util.TypeConverter;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  */
 public class RubyKernel {
     public final static Class<?> IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineAnnotatedMethods(RubyKernel.class);
         module.defineAnnotatedMethods(RubyObject.class);
         
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
         
         runtime.getObject().dispatcher = objectCallbackFactory.createDispatcher(runtime.getObject());
         module.setFlag(RubyObject.USER7_F, false); //Kernel is the only Module that doesn't need an implementor
 
         return module;
     }
 
     @JRubyMethod(name = "at_exit", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject at_exit(IRubyObject recv, Block block) {
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : recv.getRuntime().getObject();
         String name = module.getName() + "::" + symbol.asJavaString();
         
         IAutoloadMethod autoloadMethod = recv.getRuntime().getLoadService().autoloadFor(name);
         if (autoloadMethod == null) return recv.getRuntime().getNil();
 
         return recv.getRuntime().newString(autoloadMethod.file());
     }
 
     @JRubyMethod(name = "autoload", required = 2, frame = true, module = true, visibility = Visibility.PRIVATE)
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
         
         IRubyObject undef = runtime.getUndef();
         IRubyObject existingValue = module.fastFetchConstant(baseName); 
         if (existingValue != null && existingValue != undef) return runtime.getNil();
         
         module.fastStoreConstant(baseName, undef);
         
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
 
     @JRubyMethod(name = "method_missing", rest = true, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject method_missing(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw runtime.newArgumentError("no id given");
         
         String name = args[0].asJavaString();
         ThreadContext context = runtime.getCurrentContext();
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         String format = null;
 
         boolean noMethod = true; // NoMethodError
 
         if (lastVis == Visibility.PRIVATE) {
             format = "private method `%s' called for %s";
         } else if (lastVis == Visibility.PROTECTED) {
             format = "protected method `%s' called for %s";
         } else if (lastCallType == CallType.VARIABLE) {
             format = "undefined local variable or method `%s' for %s";
             noMethod = false; // NameError
         } else if (lastCallType == CallType.SUPER) {
             format = "super: no superclass method `%s'";
         }
 
         if (format == null) format = "undefined method `%s' for %s";
 
         String description = null;
         
         if (recv.isNil()) {
             description = "nil";
         } else if (recv instanceof RubyBoolean && recv.isTrue()) {
             description = "true";
         } else if (recv instanceof RubyBoolean && !recv.isTrue()) {
             description = "false";
         } else {
             if (name.equals("inspect") || name.equals("to_s")) {
                 description = recv.anyToString().toString();
             } else {
                 IRubyObject d;
                 try {
                     d = recv.callMethod(context, "inspect");
                     if (d.getMetaClass() == recv.getMetaClass() || (d instanceof RubyString && ((RubyString)d).length().getLongValue() > 65)) {
                         d = recv.anyToString();
                     }
                 } catch (JumpException je) {
                     d = recv.anyToString();
                 }
                 description = d.toString();
             }
         }
         if (description.length() == 0 || (description.length() > 0 && description.charAt(0) != '#')) {
             description = description + ":" + recv.getMetaClass().getRealClass().getName();            
         }
         
         IRubyObject[]exArgs = new IRubyObject[noMethod ? 3 : 2];
 
         RubyArray arr = runtime.newArray(args[0], runtime.newString(description));
         RubyString msg = runtime.newString(Sprintf.sprintf(runtime.newString(format), arr).toString());
         
         if (recv.isTaint()) msg.setTaint(true);
 
         exArgs[0] = msg;
         exArgs[1] = args[0];
 
         RubyClass exc;
         if (noMethod) {
             IRubyObject[]NMEArgs = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, NMEArgs, 0, NMEArgs.length);
             exArgs[2] = runtime.newArrayNoCopy(NMEArgs);
             exc = runtime.fastGetClass("NoMethodError");
         } else {
             exc = runtime.fastGetClass("NameError");
         }
         
         throw new RaiseException((RubyException)exc.newInstance(exArgs, Block.NULL_BLOCK));
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
         Ruby runtime = recv.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             return RubyIO.popen(runtime.getIO(), new IRubyObject[] {runtime.newString(command)}, block);
         } 
 
         return RubyFile.open(runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "getc", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject getc(IRubyObject recv) {
         recv.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "getc is obsolete; use STDIN.getc instead", "getc", "STDIN.getc");
         IRubyObject defin = recv.getRuntime().getGlobalVariables().get("$stdin");
         return defin.callMethod(recv.getRuntime().getCurrentContext(), "getc");
     }
 
     @JRubyMethod(name = "gets", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
-        return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).gets(args);
+        return RubyArgsFile.gets(recv.getRuntime().getGlobalVariables().get("$<"), args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject abort(IRubyObject recv, IRubyObject[] args) {
         if(args.length == 1) {
             recv.getRuntime().getGlobalVariables().get("$stderr").callMethod(recv.getRuntime().getCurrentContext(),"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.checkArrayType();
 
         if (value.isNil()) {
             if (object.getMetaClass().searchMethod("to_a").getImplementationClass() != recv.getRuntime().getKernel()) {
                 value = object.callMethod(recv.getRuntime().getCurrentContext(), MethodIndex.TO_A, "to_a");
                 if (!(value instanceof RubyArray)) throw recv.getRuntime().newTypeError("`to_a' did not return Array");
                 return value;
             } else {
                 return recv.getRuntime().newArray(object);
             }
         }
         return value;
     }
 
     @JRubyMethod(name = "Float", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return object;
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
             RubyFloat rFloat = (RubyFloat)TypeConverter.convertToType(object, recv.getRuntime().getFloat(), MethodIndex.TO_F, "to_f");
             if (Double.isNaN(rFloat.getDoubleValue())) throw recv.getRuntime().newArgumentError("invalid value for Float()");
             return rFloat;
         }
     }
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(recv.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,0,true);
         }
         
         IRubyObject tmp = TypeConverter.convertToType(object, recv.getRuntime().getInteger(), MethodIndex.TO_INT, "to_int", false);
         if (tmp.isNil()) return object.convertToInteger(MethodIndex.TO_I, "to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, recv.getRuntime().getString(), MethodIndex.TO_S, "to_s");
     }
 
     @JRubyMethod(name = "p", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject p(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", args[i].callMethod(context, "inspect"));
                 defout.callMethod(context, "write", recv.getRuntime().newString("\n"));
             }
         }
         
         if (defout instanceof RubyFile) {
             ((RubyFile)defout).flush();
         }
         
         return recv.getRuntime().getNil();
     }
 
     /** rb_f_putc
      */
     @JRubyMethod(name = "putc", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject putc(IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(recv.getRuntime().getCurrentContext(), "putc", ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject puts(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         defout.callMethod(context, "puts", args);
 
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject print(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         defout.callMethod(context, "print", args);
 
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject printf(IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             ThreadContext context = recv.getRuntime().getCurrentContext();
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readline", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
             throw recv.getRuntime().newEOFError();
         }
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
-        return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).readlines(args);
+        return RubyArgsFile.readlines(recv.getRuntime().getGlobalVariables().get("$<"), args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(Ruby runtime) {
         IRubyObject line = runtime.getCurrentContext().getPreviousFrame().getLastLine();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     @JRubyMethod(name = "sub!", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject sub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).sub_bang(args, block);
     }
 
     @JRubyMethod(name = "sub", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject sub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.sub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub!", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject gsub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).gsub_bang(args, block);
     }
 
     @JRubyMethod(name = "gsub", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject gsub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.gsub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject chop_bang(IRubyObject recv, Block block) {
         return getLastlineString(recv.getRuntime()).chop_bang();
     }
 
     @JRubyMethod(name = "chop", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject chop(IRubyObject recv, Block block) {
         RubyString str = getLastlineString(recv.getRuntime());
 
         if (str.getByteList().realSize > 0) {
             str = (RubyString) str.dup();
             str.chop_bang();
             recv.getRuntime().getCurrentContext().getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chomp!", optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject chomp_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).chomp_bang(args);
     }
 
     @JRubyMethod(name = "chomp", optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject chomp(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = getLastlineString(recv.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         recv.getRuntime().getCurrentContext().getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "split", optional = 2, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject split(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).split(args);
     }
 
     @JRubyMethod(name = "scan", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject scan(IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(recv.getRuntime()).scan(pattern, block);
     }
 
     @JRubyMethod(name = "select", required = 1, optional = 3, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(recv.getRuntime(), args);
     }
 
     @JRubyMethod(name = "sleep", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject sleep(IRubyObject recv, IRubyObject[] args) {
         long milliseconds;
 
         if (args.length == 0) {
             // Zero sleeps forever
             milliseconds = 0;
         } else {
             if (!(args[0] instanceof RubyNumeric)) {
                 throw recv.getRuntime().newTypeError("can't convert " + args[0].getMetaClass().getName() + "into time interval");
             }
             milliseconds = (long) (args[0].convertToFloat().getDoubleValue() * 1000);
             if (milliseconds < 0) {
                 throw recv.getRuntime().newArgumentError("time interval must be positive");
             } else if (milliseconds == 0) {
                 // Explicit zero in MRI returns immediately
                 return recv.getRuntime().newFixnum(0);
             }
         }
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = recv.getRuntime().getThreadService().getCurrentContext().getThread();
         
         do {
             long loopStartTime = System.currentTimeMillis();
             try {
                 rubyThread.sleep(milliseconds);
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis() - loopStartTime);
         } while (milliseconds > 0);
 
         return recv.getRuntime().newFixnum(
                 Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         recv.getRuntime().secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         throw recv.getRuntime().newSystemExit(status);
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         // This calls normal exit() on purpose because we should probably not expose System.exit(0)
         return exit(recv, args);
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     @JRubyMethod(name = "global_variables", module = true, visibility = Visibility.PRIVATE)
     public static RubyArray global_variables(IRubyObject recv) {
         RubyArray globalVariables = recv.getRuntime().newArray();
 
         for (String globalVariableName : recv.getRuntime().getGlobalVariables().getNames()) {
             globalVariables.append(recv.getRuntime().newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     @JRubyMethod(name = "local_variables", module = true, visibility = Visibility.PRIVATE)
     public static RubyArray local_variables(IRubyObject recv) {
         final Ruby runtime = recv.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         for (String name: runtime.getCurrentContext().getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     @JRubyMethod(name = "binding", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static RubyBinding binding(IRubyObject recv, Block block) {
         // FIXME: Pass block into binding
         return recv.getRuntime().newBinding();
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static RubyBoolean block_given_p(IRubyObject recv, Block block) {
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().getPreviousFrame().getBlock().isGiven());
     }
 
     @JRubyMethod(name = {"sprintf", "format"}, required = 1, rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArrayNoCopy(args);
         newArgs.shift();
 
         return str.op_format(newArgs);
     }
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.fastGetClass("RuntimeError"), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.fastGetClass("RuntimeError").newInstance(args, block));
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
         
         throw new RaiseException((RubyException) exception);
     }
     
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(name = "require", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         if (recv.getRuntime().getLoadService().require(name.convertToString().toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString file = args[0].convertToString();
         boolean wrap = false;
         if (args.length == 2) {
             wrap = args[1].isTrue();
         }
         recv.getRuntime().getLoadService().load(file.getByteList().toString(), wrap);
         return recv.getRuntime().getTrue();
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
             
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
         if (scope == null) scope = RubyBinding.newBindingForEval(runtime);
         
         return ASTInterpreter.evalWithBinding(runtime.getCurrentContext(), src, scope, file, line);
     }
 
     @JRubyMethod(name = "callcc", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject callcc(IRubyObject recv, Block block) {
         Ruby runtime = recv.getRuntime();
         runtime.getWarnings().warn(ID.EMPTY_IMPLEMENTATION, "Kernel#callcc: Continuations are not implemented in JRuby and will not work", "Kernel#callcc");
         IRubyObject cc = runtime.getContinuation().callMethod(runtime.getCurrentContext(),"new");
         cc.dataWrapStruct(block);
         return block.yield(runtime.getCurrentContext(),cc);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return ThreadContext.createBacktraceFromFrames(recv.getRuntime(), recv.getRuntime().getCurrentContext().createBacktrace(level, false));
     }
 
     @JRubyMethod(name = "catch", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
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
 
     @JRubyMethod(name = "throw", required = 1, frame = true, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         String tag = args[0].asJavaString();
         ThreadContext context = runtime.getCurrentContext();
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
 
     @JRubyMethod(name = "trap", required = 1, frame = true, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args, Block block) {
         recv.getRuntime().getLoadService().require("jsignal");
         return RuntimeHelpers.invoke(recv.getRuntime().getCurrentContext(), recv, "__jtrap", args, CallType.FUNCTIONAL, block);
     }
     
     @JRubyMethod(name = "warn", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject warn(IRubyObject recv, IRubyObject message) {
         Ruby runtime = recv.getRuntime();
         if (!runtime.getVerbose().isNil()) {
             IRubyObject out = runtime.getGlobalVariables().get("$stderr");
             out.callMethod(runtime.getCurrentContext(), "puts", new IRubyObject[] { message });
         }
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "set_trace_func", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject set_trace_func(IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             recv.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw recv.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             recv.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     @JRubyMethod(name = "trace_var", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject trace_var(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw recv.getRuntime().newArgumentError(0, 1);
         RubyProc proc = null;
         String var = null;
         
         if (args.length > 1) {
             var = args[0].toString();
         }
         
         if (var.charAt(0) != '$') {
             // ignore if it's not a global var
             return recv.getRuntime().getNil();
         }
         
         if (args.length == 1) {
             proc = RubyProc.newProc(recv.getRuntime(), block, Block.Type.PROC);
         }
         if (args.length == 2) {
             proc = (RubyProc)TypeConverter.convertToType(args[1], recv.getRuntime().getProc(), 0, "to_proc", true);
         }
         
         recv.getRuntime().getGlobalVariables().setTraceVar(var, proc);
         
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "untrace_var", required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject untrace_var(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw recv.getRuntime().newArgumentError(0, 1);
         String var = null;
         
         if (args.length >= 1) {
             var = args[0].toString();
         }
         
         if (var.charAt(0) != '$') {
             // ignore if it's not a global var
             return recv.getRuntime().getNil();
         }
         
         if (args.length > 1) {
             ArrayList<IRubyObject> success = new ArrayList<IRubyObject>();
             for (int i = 1; i < args.length; i++) {
                 if (recv.getRuntime().getGlobalVariables().untraceVar(var, args[i])) {
                     success.add(args[i]);
                 }
             }
             return RubyArray.newArray(recv.getRuntime(), success);
         } else {
             recv.getRuntime().getGlobalVariables().untraceVar(var);
         }
         
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_added", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject singleton_method_added(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_removed", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject singleton_method_removed(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_undefined", required = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject singleton_method_undefined(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
     
     @JRubyMethod(name = {"proc", "lambda"}, frame = true, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"lambda"}, frame = true, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc lambda(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"proc"}, frame = true, module = true, visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc proc_1_9(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.PROC, block);
     }
 
     @JRubyMethod(name = "loop", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject loop(IRubyObject recv, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             try {
                 block.yield(context, recv.getRuntime().getNil());
                 
                 context.pollThreadEvents();
             } catch (JumpException.BreakJump bj) {
                 // JRUBY-530, specifically the Kernel#loop case:
                 // Kernel#loop always takes a block.  But what we're looking
                 // for here is breaking an iteration where the block is one 
                 // used inside loop's block, not loop's block itself.  Set the 
                 // appropriate flag on the JumpException if this is the case
                 // (the FCALLNODE case in EvaluationState will deal with it)
                 if (bj.getTarget() != null && bj.getTarget() != block.getBody()) {
                     bj.setBreakInKernelLoop(true);
                 }
                  
                 throw bj;
             }
         }
     }
     
     @JRubyMethod(name = "test", required = 2, optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject test(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) throw recv.getRuntime().newArgumentError("wrong number of arguments");
 
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
             throw recv.getRuntime().newArgumentError("unknown command ?" + (char) cmd);
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-': case '=': case '<': case '>':
             if (args.length != 3) throw recv.getRuntime().newArgumentError(args.length, 3);
             break;
         default:
             if (args.length != 2) throw recv.getRuntime().newArgumentError(args.length, 2);
             break;
         }
         
         switch (cmd) {
         case 'A': // ?A  | Time    | Last access time for file1
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).atime();
         case 'b': // ?b  | boolean | True if file1 is a block device
             return RubyFileTest.blockdev_p(recv, args[1]);
         case 'c': // ?c  | boolean | True if file1 is a character device
             return RubyFileTest.chardev_p(recv, args[1]);
         case 'C': // ?C  | Time    | Last change time for file1
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).ctime();
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
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtime();
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
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeEquals(args[2]);
         case '<': // ?<  | boolean | True if the modification time of file1 is prior to that of file2
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeLessThan(args[2]);
         case '>': // ?>  | boolean | True if the modification time of file1 is after that of file2
             return recv.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeGreaterThan(args[2]);
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             return RubyFileTest.identical_p(recv, args[1], args[2]);
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     @JRubyMethod(name = "`", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         Ruby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         RubyString string = aString.convertToString();
         int resultCode = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {string}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newString(recv.getRuntime(), output.toByteArray());
     }
     
     @JRubyMethod(name = "srand", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static RubyInteger srand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long oldRandomSeed = runtime.getRandomSeed();
 
         if (args.length > 0) {
             RubyInteger integerSeed = args[0].convertToInteger(MethodIndex.TO_INT, "to_int");
             runtime.setRandomSeed(integerSeed.getLongValue());
         } else {
             // Not sure how well this works, but it works much better than
             // just currentTimeMillis by itself.
             runtime.setRandomSeed(System.currentTimeMillis() ^
               recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
               runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
         }
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     @JRubyMethod(name = "rand", optional = 1, module = true, visibility = Visibility.PRIVATE)
     public static RubyNumeric rand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long ceil;
         if (args.length == 0) {
             ceil = 0;
         } else if (args.length == 1) {
             if (args[0] instanceof RubyBignum) {
                 byte[] bigCeilBytes = ((RubyBignum) args[0]).getValue().toByteArray();
                 BigInteger bigCeil = new BigInteger(bigCeilBytes).abs();
                 
                 byte[] randBytes = new byte[bigCeilBytes.length];
                 runtime.getRandom().nextBytes(randBytes);
                 
                 BigInteger result = new BigInteger(randBytes).abs().mod(bigCeil);
                 
                 return new RubyBignum(runtime, result); 
             }
              
             RubyInteger integerCeil = (RubyInteger)RubyKernel.new_integer(recv, args[0]); 
             ceil = Math.abs(integerCeil.getLongValue());
         } else {
             throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         if (ceil == 0) {
             return RubyFloat.newFloat(runtime, runtime.getRandom().nextDouble()); 
         }
         if (ceil > Integer.MAX_VALUE) {
             return runtime.newFixnum(Math.abs(runtime.getRandom().nextLong()) % ceil);
         }
             
         return runtime.newFixnum(runtime.getRandom().nextInt((int) ceil));
     }
     
     @JRubyMethod(name = "syscall", required = 1, optional = 9, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject syscall(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Kernel#syscall is not implemented in JRuby");
     }
 
     @JRubyMethod(name = {"system"}, required = 1, rest = true, module = true, visibility = Visibility.PRIVATE)
     public static RubyBoolean system(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int resultCode;
         try {
             resultCode = new ShellLauncher(runtime).runAndWait(args);
         } catch (Exception e) {
             resultCode = 127;
         }
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     @JRubyMethod(name = {"exec"}, required = 1, rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject exec(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int resultCode;
         try {
             // TODO: exec should replace the current process.
             // This could be possible with JNA. 
             resultCode = new ShellLauncher(runtime).runAndWait(args);
         } catch (Exception e) {
             throw runtime.newErrnoENOENTError("cannot execute");
         }
         
         return exit(recv, new IRubyObject[] {runtime.newFixnum(resultCode)});
     }
 
     @JRubyMethod(name = "fork", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject fork(IRubyObject recv, Block block) {
         Ruby runtime = recv.getRuntime();
         
         if (!RubyInstanceConfig.FORK_ENABLED) {
             throw runtime.newNotImplementedError("fork is unsafe and disabled by default on JRuby");
         }
         
         if (block.isGiven()) {
             int pid = runtime.getPosix().fork();
             
             if (pid == 0) {
                 try {
                     block.yield(runtime.getCurrentContext(), runtime.getNil());
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
 }
diff --git a/test/test_argf.rb b/test/test_argf.rb
index 6c796dfcdd..09c69fda33 100644
--- a/test/test_argf.rb
+++ b/test/test_argf.rb
@@ -1,58 +1,61 @@
 require 'test/unit'
 require 'rbconfig'
 
 # Since we haven't found a good way to instantiate multiple ARGF instances for testing,
 # the approach here is to fork to another process, run few things, and be done with it.
 # Given JRuby startup time, ARGF is not a terribly valuable feature, so this level of testing is enough.
 
 SCRIPT = <<END_OF_SCRIPT
 
 def test_equal(expected, actual)
   raise "Expected: \#{expected.inspect}, got: \#{actual.inspect}" unless expected == actual
 end
 
 def test_to_io
   raise "Could not coerce ARGF to IO" unless ARGF.to_io.is_a? IO
 end
 
 test_to_io
 
 # should not raise anything
 test_equal "1:1\\n", ARGF.gets
 
 test_to_io
 
 ARGF.each_with_index do |line, index|
   case index
   when 0 then test_equal "1:2", line
   when 1 then test_equal "2:1\\n", line
   when 2
     test_equal "2:2\\n", line
     break
   else raise 'Should never get here'
   end
 end
 
 test_equal nil, ARGF.gets
 
 END_OF_SCRIPT
 
 class TestArgf < Test::Unit::TestCase
 
   def test_argf_sanity
     begin
       File.open('__argf_script.rb', 'w') { |f| f.write SCRIPT }
       File.open('__argf_input_1', 'w') { |f| f.write "1:1\n1:2" }
       File.open('__argf_input_2', 'w') { |f| f.write "2:1\n2:2\n" }
 
       assert system("#{Config::CONFIG['ruby_install_name']} __argf_script.rb __argf_input_1 __argf_input_2"),
              "Smoke test script for ARGF failed"
     ensure
       File.unlink '__argf_script.rb' rescue nil
       File.unlink '__argf_input_1' rescue nil
       File.unlink '__argf_input_2' rescue nil
       File.unlink '__argf_input_3' rescue nil
     end
   end
 
+  def test_argf_cloning
+    assert_equal "ARGF", ARGF.clone.to_s
+  end
 end
