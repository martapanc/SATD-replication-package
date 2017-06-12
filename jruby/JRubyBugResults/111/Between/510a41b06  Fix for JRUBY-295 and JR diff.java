diff --git a/src/builtin/zlib.rb b/src/builtin/zlib.rb
deleted file mode 100644
index 5da3058554..0000000000
--- a/src/builtin/zlib.rb
+++ /dev/null
@@ -1,460 +0,0 @@
-#/***** BEGIN LICENSE BLOCK *****
-# * Version: CPL 1.0/GPL 2.0/LGPL 2.1
-# *
-# * The contents of this file are subject to the Common Public
-# * License Version 1.0 (the "License"); you may not use this file
-# * except in compliance with the License. You may obtain a copy of
-# * the License at http://www.eclipse.org/legal/cpl-v10.html
-# *
-# * Software distributed under the License is distributed on an "AS
-# * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-# * implied. See the License for the specific language governing
-# * rights and limitations under the License.
-# * 
-# * Alternatively, the contents of this file may be used under the terms of
-# * either of the GNU General Public License Version 2 or later (the "GPL"),
-# * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
-# * in which case the provisions of the GPL or the LGPL are applicable instead
-# * of those above. If you wish to allow use of your version of this file only
-# * under the terms of either the GPL or the LGPL, and not to allow others to
-# * use your version of this file under the terms of the CPL, indicate your
-# * decision by deleting the provisions above and replace them with the notice
-# * and other provisions required by the GPL or the LGPL. If you do not delete
-# * the provisions above, a recipient may use your version of this file under
-# * the terms of any one of the CPL, the GPL or the LGPL.
-# ***** END LICENSE BLOCK *****/
-require "java"
-require "stringio"
-
-include_class("java.lang.String"){ |p,name| "J#{name}"}
-
-include_class "java.io.BufferedReader"
-include_class "java.io.ByteArrayOutputStream"
-include_class "java.io.InputStreamReader"
-include_class "java.io.PipedInputStream"
-include_class "java.io.PipedOutputStream"
-include_class "java.io.StringBufferInputStream"
-include_class "java.lang.StringBuffer"
-
-include_class "java.util.zip.Deflater"
-include_class "java.util.zip.DeflaterOutputStream"
-include_class "java.util.zip.Inflater"
-include_class "java.util.zip.InflaterInputStream"
-include_class "java.util.zip.GZIPInputStream"
-include_class "java.util.zip.GZIPOutputStream"
-
-include_class "org.jruby.util.Adler32Ext"
-include_class "org.jruby.util.CRC32Ext"
-include_class "org.jruby.util.IOInputStream"
-include_class "org.jruby.util.IOOutputStream"
-include_class "org.jruby.util.ZlibInflate"
-include_class "org.jruby.util.ZlibDeflate"
-
-#
-# Implementation of the Zlib library with the help of Java classes
-# Does not provide all functionality, especially in Gzip, since
-# Java's implementation kindof suck.
-#
-module Zlib
-  
-    # Constants
-    # Most of these are based on the constants in either java.util.zip or Zlib
-    ZLIB_VERSION = "1.2.1"
-    VERSION = "0.6.0"
-
-    BINARY = 0
-    ASCII = 1
-    UNKNOWN = 2
-
-    DEF_MEM_LEVEL = 8
-    MAX_MEM_LEVEL = 9
-
-    OS_UNIX = 3
-    OS_UNKNOWN = 255
-    OS_CODE = 11
-    OS_ZSYSTEM = 8
-    OS_VMCMS = 4
-    OS_VMS = 2
-    OS_RISCOS = 13
-    OS_MACOS = 7
-    OS_OS2 = 6
-    OS_AMIGA = 1
-    OS_QDOS = 12
-    OS_WIN32 = 11
-    OS_ATARI = 5
-    OS_MSDOS = 0
-    OS_CPM = 9
-    OS_TOPS20 = 10
-
-    DEFAULT_STRATEGY = 0
-    FILTERED = 1
-    HUFFMAN_ONLY = 2
-
-    NO_FLUSH = 0
-    SYNC_FLUSH = 2
-    FULL_FLUSH = 3
-    FINISH = 4
-
-    NO_COMPRESSION = 0
-    BEST_SPEED = 1
-    DEFAULT_COMPRESSION = -1
-    BEST_COMPRESSION = 9
-
-    MAX_WBITS = 15
-
-    #
-    # Returns the string which represents the version of zlib library.
-    #
-    def Zlib.zlib_version
-      ZLIB_VERSION
-    end
-
-    #
-    # Returns the string which represents the version of this library
-    #
-    def Zlib.version
-      VERSION
-    end
-
-    #
-    # Calculates Adler-32 checksum for string, and returns updated value of adler. 
-    # If string is omitted, it returns the Adler-32 initial value. 
-    # If adler is omitted, it assumes that the initial value is given to adler.
-    #
-    def Zlib.adler32(string=nil, adler=1)
-      ext = Adler32Ext.new adler
-      if string
-        string.each_byte {|b| ext.update(b) }
-      end
-      ext.getValue
-    end
-
-    #
-    # Calculates CRC-32 checksum for string, and returns updated value of crc. 
-    # If string is omitted, it returns the CRC-32 initial value. 
-    # If crc is omitted, it assumes that the initial value is given to crc.
-    #
-    def Zlib.crc32(string=nil, crc=0)
-      ext = CRC32Ext.new crc
-      if string
-        string.each_byte {|b| ext.update(b) }
-      end
-      ext.getValue
-    end
-
-    #
-    # Returns the table for calculating CRC checksum as an array.
-    #
-    def Zlib.crc_table
-      [0, 1996959894, 3993919788, 2567524794, 124634137, 1886057615, 3915621685, 2657392035, 249268274, 2044508324, 3772115230, 2547177864, 162941995, 
-        2125561021, 3887607047, 2428444049, 498536548, 1789927666, 4089016648, 2227061214, 450548861, 1843258603, 4107580753, 2211677639, 325883990, 
-        1684777152, 4251122042, 2321926636, 335633487, 1661365465, 4195302755, 2366115317, 997073096, 1281953886, 3579855332, 2724688242, 1006888145, 
-        1258607687, 3524101629, 2768942443, 901097722, 1119000684, 3686517206, 2898065728, 853044451, 1172266101, 3705015759, 2882616665, 651767980, 
-        1373503546, 3369554304, 3218104598, 565507253, 1454621731, 3485111705, 3099436303, 671266974, 1594198024, 3322730930, 2970347812, 795835527, 
-        1483230225, 3244367275, 3060149565, 1994146192, 31158534, 2563907772, 4023717930, 1907459465, 112637215, 2680153253, 3904427059, 2013776290, 
-        251722036, 2517215374, 3775830040, 2137656763, 141376813, 2439277719, 3865271297, 1802195444, 476864866, 2238001368, 4066508878, 1812370925, 
-        453092731, 2181625025, 4111451223, 1706088902, 314042704, 2344532202, 4240017532, 1658658271, 366619977, 2362670323, 4224994405, 1303535960, 
-        984961486, 2747007092, 3569037538, 1256170817, 1037604311, 2765210733, 3554079995, 1131014506, 879679996, 2909243462, 3663771856, 1141124467, 
-        855842277, 2852801631, 3708648649, 1342533948, 654459306, 3188396048, 3373015174, 1466479909, 544179635, 3110523913, 3462522015, 1591671054, 
-        702138776, 2966460450, 3352799412, 1504918807, 783551873, 3082640443, 3233442989, 3988292384, 2596254646, 62317068, 1957810842, 3939845945, 
-        2647816111, 81470997, 1943803523, 3814918930, 2489596804, 225274430, 2053790376, 3826175755, 2466906013, 167816743, 2097651377, 4027552580, 
-        2265490386, 503444072, 1762050814, 4150417245, 2154129355, 426522225, 1852507879, 4275313526, 2312317920, 282753626, 1742555852, 4189708143, 
-        2394877945, 397917763, 1622183637, 3604390888, 2714866558, 953729732, 1340076626, 3518719985, 2797360999, 1068828381, 1219638859, 3624741850, 
-        2936675148, 906185462, 1090812512, 3747672003, 2825379669, 829329135, 1181335161, 3412177804, 3160834842, 628085408, 1382605366, 3423369109, 
-        3138078467, 570562233, 1426400815, 3317316542, 2998733608, 733239954, 1555261956, 3268935591, 3050360625, 752459403, 1541320221, 2607071920, 
-        3965973030, 1969922972, 40735498, 2617837225, 3943577151, 1913087877, 83908371, 2512341634, 3803740692, 2075208622, 213261112, 2463272603, 
-        3855990285, 2094854071, 198958881, 2262029012, 4057260610, 1759359992, 534414190, 2176718541, 4139329115, 1873836001, 414664567, 2282248934, 
-        4279200368, 1711684554, 285281116, 2405801727, 4167216745, 1634467795, 376229701, 2685067896, 3608007406, 1308918612, 956543938, 2808555105, 
-        3495958263, 1231636301, 1047427035, 2932959818, 3654703836, 1088359270, 936918000, 2847714899, 3736837829, 1202900863, 817233897, 3183342108, 
-        3401237130, 1404277552, 615818150, 3134207493, 3453421203, 1423857449, 601450431, 3009837614, 3294710456, 1567103746, 711928724, 3020668471, 
-        3272380065, 1510334235, 755167117]
-    end
-end
-
-class Zlib::Error < StandardError
-end
-
-class Zlib::StreamEnd < Zlib::Error
-end
-
-class Zlib::StreamError < Zlib::Error
-end
-
-class Zlib::BufError < Zlib::Error
-end
-
-class Zlib::NeedDict < Zlib::Error
-end
-
-class Zlib::MemError < Zlib::Error
-end
-
-class Zlib::VersionError < Zlib::Error
-end
-
-class Zlib::DataError < Zlib::Error
-end
-
-#
-# The abstract base class for Deflater and Inflater. 
-# This implementation don't really do so much, except for provide som common
-# functionality between Deflater and Inflater.
-# Some of that functionality is also dubious, because of the Java
-# implementation.
-#
-class Zlib::ZStream < Object
-    def initialize
-      @closed = false
-      @ended = false
-    end
-
-    #
-    # Not implemented.
-    #
-    def flush_next_out
-    end
-    
-    def total_out
-      @flater.getTotalOut
-    end
-
-    def stream_end?
-      @flater.finished
-    end
-
-    #
-    # Constant implementation, we can not know this.
-    #
-    def data_type
-      Zlib::UNKNOWN
-    end
-
-    def closed?
-      @closed
-    end
-
-    def ended?
-      @ended
-    end
-
-    def end
-      @flater.end unless @ended
-      @ended = true
-    end
-
-    def reset
-      @flater.reset
-    end
-
-    #
-    # Constant implementation, we can not know this.
-    #
-    def avail_out
-      0
-    end
-
-    #
-    # Not implemented, no support for this.
-    #
-    def avail_out=(p1)
-    end
-
-    def adler
-      @flater.adler
-    end
-
-    def finish
-      # FIXME: this doesn't appear to work
-      #@stream.finish
-    end
-
-    #
-    # Constant implementation, we can not know this.
-    #
-    def avail_in
-      0
-    end
-
-    #
-    # Not implemented.
-    #
-    def flush_next_in
-    end
-
-    def total_in
-      @flater.getTotalIn
-    end
-
-    def finished?
-      @flater.finished
-    end
-
-    def close
-      @stream.close unless @closed
-      @closed = true
-    end
-end
-
-#
-# Zlib::Inflate is the class for decompressing compressed data.
-# The implementation is patchy, due to bad underlying support
-# for certain functions.
-#
-class Zlib::Inflate < Zlib::ZStream
-    #
-    # Decompresses string. Raises a Zlib::NeedDict exception if a preset dictionary is needed for decompression.
-    #
-    def self.inflate(string)
-      ZlibInflate.s_inflate(self,string)
-    end
-
-    #
-    # Creates a new inflate stream for decompression. See zlib.h for details of the argument. If window_bits is nil, the default value is used.
-    #
-    def initialize(window_bits=nil)
-      @infl = ZlibInflate.new(self)
-    end
-
-    #
-    # Adds p1 to stream and returns self
-    #
-    def <<(p1)
-      @infl.append(p1)
-      self
-    end
-
-    #
-    # No idea, no implementation
-    #
-    def sync_point?
-      @infl.sync_point
-    end
-
-    #
-    # Sets the preset dictionary and returns string. This method is available just only after a Zlib::NeedDict exception was raised. See zlib.h for details.
-    #
-    def set_dictionary(p1)
-      @infl.set_dictionary(p1)
-    end
-
-    #
-    # Inputs string into the inflate stream and returns the output from the stream. 
-    # Calling this method, both the input and the output buffer of the stream are flushed. 
-    # If string is nil, this method finishes the stream, just like Zlib::ZStream#finish.
-    #
-    # Raises a Zlib::NeedDict exception if a preset dictionary is needed to decompress. 
-    # Set the dictionary by Zlib::Inflate#set_dictionary and then call this method again with an empty string.
-    #
-    def inflate(string)
-      @infl.inflate(string)
-    end
-
-    #
-    # This implementation is not correct
-    #
-    def sync(string)
-      @infl.sync(string)
-    end
-end
-
-class Zlib::GzipFile::Error < Zlib::Error
-end
-
-class Zlib::GzipFile::CRCError < Zlib::GzipFile::Error
-end
-
-class Zlib::GzipFile::NoFooter < Zlib::GzipFile::Error
-end
-
-class Zlib::GzipFile::LengthError < Zlib::GzipFile::Error
-end
-
-#
-# Zlib::Deflate is the class for compressing data.
-# The implementation is patchy, due to bad underlying support
-# for certain functions.
-#
-class Zlib::Deflate < Zlib::ZStream
-    #
-    # Compresses the given string. Valid values of level are Zlib::NO_COMPRESSION, Zlib::BEST_SPEED, 
-    # Zlib::BEST_COMPRESSION, Zlib::DEFAULT_COMPRESSION, and an integer from 0 to 9.
-    #
-    def self.deflate(string, level=Zlib::DEFAULT_COMPRESSION)
-      ZlibDeflate.s_deflate(self,string,level)
-    end
-
-    # 
-    # Creates a new deflate stream for compression. See zlib.h for details of each argument. 
-    # If an argument is nil, the default value of that argument is used.
-    #
-    def initialize(level=nil,window_bits=nil, memlevel=nil,strategy=nil)
-      if level.nil?
-        level = Zlib::DEFAULT_COMPRESSION
-      end
-      if strategy.nil?
-        strategy = Zlib::DEFAULT_STRATEGY
-      end
-      if window_bits.nil?
-        window_bits = Zlib::MAX_WBITS
-      end
-      if memlevel.nil?
-        memlevel = Zlib::DEF_MEM_LEVEL
-      end
-      @defl = ZlibDeflate.new(self,level,window_bits,memlevel,strategy)
-    end
-
-    #
-    # String output - Writes p1 to stream. p1 will be converted to a string using to_s.
-    # Returns self
-    #
-    def <<(p1)
-      @defl.append(p1)
-      self
-    end
-
-    #
-    # Changes the parameters of the deflate stream. See zlib.h for details. The output from the stream by changing the params is preserved in output buffer.
-    #
-    def params(level,strategy)
-      @defl.params(level,strategy)
-    end
-
-    #
-    # Sets the preset dictionary and returns string. This method is available just only after Zlib::Deflate.new or 
-    # Zlib::ZStream#reset method was called. See zlib.h for details.
-    # 
-    def set_dictionary(string)
-      @defl.set_dictionary(string)
-    end
-
-    #
-    # This method is equivalent to deflate(, flush). If flush is omitted, Zlib::SYNC_FLUSH is used as flush. This 
-    # method is just provided to improve the readability of your Ruby program.
-    #
-    def flush(flush=Zlib::SYNC_FLUSH)
-      @defl.flush(flush)
-    end
-
-    # 
-    # Inputs string into the deflate stream and returns the output from the stream. On calling this method, 
-    # both the input and the output buffers of the stream are flushed. 
-    # If string is nil, this method finishes the stream, just like Zlib::ZStream#finish.
-    #
-    # The value of flush should be either Zlib::NO_FLUSH, Zlib::SYNC_FLUSH, Zlib::FULL_FLUSH, or Zlib::FINISH. See zlib.h for details. 
-    #
-    def deflate(string,flush=nil)
-      @defl.deflate(string,flush)
-    end
-end
-
-class Zlib::GzipFile::CRCError < Zlib::GzipFile::Error
-end
-
-class Zlib::GzipFile::Error < Zlib::Error
-end
-
-class Zlib::GzipFile::NoFooter < Zlib::GzipFile::Error
-end
-
-class Zlib::GzipFile::LengthError < Zlib::GzipFile::Error
-end
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 56f9911c40..a38932bd43 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -205,1011 +205,1011 @@ public class RubyObject implements Cloneable, IRubyObject {
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         this.frozen = frozen;
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message);
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen " + getMetaClass().getName());
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
      */
     public boolean isTaint() {
         return taint;
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         this.taint = taint;
     }
 
     public boolean isNil() {
         return false;
     }
 
     public boolean isTrue() {
         return !isNil();
     }
 
     public boolean isFalse() {
         return isNil();
     }
 
     public boolean respondsTo(String name) {
         return getMetaClass().isMethodBound(name, false);
     }
 
     // Some helper functions:
 
     public int checkArgumentCount(IRubyObject[] args, int min, int max) {
         if (args.length < min) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + min + ")");
         }
         if (max > -1 && args.length > max) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for " + max + ")");
         }
         return args.length;
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().hasModuleInHierarchy(type);
     }
 
     /** rb_singleton_class
      *
      */
     public MetaClass getSingletonClass() {
         RubyClass type = getMetaClass();
         if (!type.isSingleton()) {
             type = makeMetaClass(type, type.getCRef());
         }
 
         assert type instanceof MetaClass;
 
 		if (!isNil()) {
 			type.setTaint(isTaint());
 			type.setFrozen(isFrozen());
 		}
 
         return (MetaClass)type;
     }
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     public void addSingletonMethod(String name, ICallable method) {
         getSingletonClass().addMethod(name, method);
     }
 
     /* rb_init_ccopy */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
 
         callMethod(getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType) {
         assert args != null;
         ICallable method = null;
 
         method = rubyclass.searchMethod(name);
 
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(context.getFrameSelf(), callType))) {
             if (callType == CallType.SUPER) {
                 throw getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(method.getVisibility(), callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(this, args);
             }
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(getRuntime(), name);
 
             return callMethod(context, "method_missing", newArgs);
         }
 
         RubyModule implementer = null;
         if (method.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             implementer = rubyclass.findImplementer(method.getImplementationClass());
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             implementer = method.getImplementationClass();
         }
 
         String originalName = method.getOriginalName();
         if (originalName != null) {
             name = originalName;
         }
 
         IRubyObject result = method.call(context, this, implementer, name, args, false);
 
         return result;
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY);
     }
 
     /**
      * rb_funcall
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return callMethod(context, name, new IRubyObject[] { arg });
     }
 
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!varName.startsWith("@")) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	IRubyObject variable = getInstanceVariable(varName);
 
     	// Pickaxe v2 says no var should show NameError, but ruby only sends back nil..
     	return variable == null ? getRuntime().getNil() : variable;
     }
 
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().get(name);
     }
 
     public IRubyObject instance_variable_set(IRubyObject var, IRubyObject value) {
     	String varName = var.asSymbol();
 
     	if (!varName.startsWith("@")) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	return setInstanceVariable(var.asSymbol(), value);
     }
 
     public IRubyObject setInstanceVariable(String name, IRubyObject value,
             String taintError, String freezeError) {
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError(taintError);
         }
         testFrozen(freezeError);
 
         getInstanceVariables().put(name, value);
 
         return value;
     }
 
     /** rb_iv_set / rb_ivar_set
      *
      */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         return setInstanceVariable(name, value,
                 "Insecure: can't modify instance variable", "");
     }
 
     public Iterator instanceVariableNames() {
         return getInstanceVariables().keySet().iterator();
     }
 
     /** rb_eval
      *
      */
     public IRubyObject eval(Node n) {
         //return new EvaluationState(getRuntime(), this).begin(n);
         // need to continue evaluation with a new self, so save the old one (should be a stack?)
         return EvaluationState.eval(getRuntime().getCurrentContext(), n, this);
     }
 
     public void callInit(IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         tc.setIfBlockAvailable();
         try {
             callMethod(getRuntime().getCurrentContext(), "initialize", args);
         } finally {
             tc.clearIfBlockAvailable();
         }
     }
 
     public void extendObject(RubyModule module) {
         getSingletonClass().includeModule(module);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
     public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
 
         IRubyObject value = convertToType(targetType, convertMethod, false);
         if (value.isNil()) {
             return value;
         }
 
         if (!targetType.equals(value.getMetaClass().getName())) {
             throw getRuntime().newTypeError(value.getMetaClass().getName() + "#" + convertMethod +
                     "should return " + targetType);
         }
 
         return value;
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(String targetType, String convertMethod, boolean raise) {
         // No need to convert something already of the correct type.
         // XXXEnebo - Could this pass actual class reference instead of String?
         if (targetType.equals(getMetaClass().getName())) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
             if (raise) {
                 throw getRuntime().newTypeError(
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
         return callMethod(getRuntime().getCurrentContext(), convertMethod);
     }
 
     private String trueFalseNil(String v) {
         if("TrueClass".equals(v)) {
             return "true";
         } else if("FalseClass".equals(v)) {
             return "false";
         } else if("NilClass".equals(v)) {
             return "nil";
         }
         return v;
     }
 
     public RubyArray convertToArray() {
         return (RubyArray) convertToType("Array", "to_ary", true);
     }
 
     public RubyFloat convertToFloat() {
         return (RubyFloat) convertToType("Float", "to_f", true);
     }
 
     public RubyInteger convertToInteger() {
         return (RubyInteger) convertToType("Integer", "to_int", true);
     }
 
     public RubyString convertToString() {
         return (RubyString) convertToType("String", "to_str", true);
     }
 
     /** rb_convert_type
      *
      */
     public IRubyObject convertType(Class type, String targetType, String convertMethod) {
         if (type.isAssignableFrom(getClass())) {
             return this;
         }
 
         IRubyObject result = convertToType(targetType, convertMethod, true);
 
         if (!type.isAssignableFrom(result.getClass())) {
             throw getRuntime().newTypeError(
                 getMetaClass().getName() + "#" + convertMethod + " should return " + targetType + ".");
         }
 
         return result;
     }
 
     public void checkSafeString() {
         if (getRuntime().getSafeLevel() > 0 && isTaint()) {
             ThreadContext tc = getRuntime().getCurrentContext();
             if (tc.getFrameLastFunc() != null) {
                 throw getRuntime().newSecurityError("Insecure operation - " + tc.getFrameLastFunc());
             }
             throw getRuntime().newSecurityError("Insecure operation: -r");
         }
         getRuntime().secure(4);
         if (!(this instanceof RubyString)) {
             throw getRuntime().newTypeError(
                 "wrong argument type " + getMetaClass().getName() + " (expected String)");
         }
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (tc.isBlockGiven()) {
             if (args.length > 0) {
                 throw getRuntime().newArgumentError(args.length, 0);
             }
             return yieldUnder(mod);
         }
 		if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameLastFunc();
 		    throw getRuntime().newArgumentError(
 		        "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
 		}
 		/*
 		if (ruby.getSecurityLevel() >= 4) {
 			Check_Type(argv[0], T_STRING);
 		} else {
 			Check_SafeStr(argv[0]);
 		}
 		*/
 		IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
 		IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
 		Visibility savedVisibility = tc.getCurrentVisibility();
         tc.setCurrentVisibility(Visibility.PUBLIC);
 		try {
 		    return evalUnder(mod, args[0], file, line);
 		} finally {
             tc.setCurrentVisibility(savedVisibility);
 		}
     }
 
     public IRubyObject evalUnder(RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         /*
         if (ruby_safe_level >= 4) {
         	Check_Type(src, T_STRING);
         } else {
         	Check_SafeStr(src);
         	}
         */
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
 
                 return args[0].evalSimple(source.getRuntime().getCurrentContext(),
                                   source, ((RubyString) filename).toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line });
     }
 
     private IRubyObject yieldUnder(RubyModule under) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Block block = (Block) context.getCurrentBlock();
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return context.yieldCurrentBlock(valueInYield, selfInYield, context.getRubyClass(), false);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException je) {
                 	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 		IRubyObject breakValue = (IRubyObject)je.getPrimaryData();
 
                 		return breakValue == null ? getRuntime().getNil() : breakValue;
                 	} else {
                 		throw je;
                 	}
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this });
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, String file) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
 
         ISourcePosition savedPosition = threadContext.getPosition();
         IRubyObject result = getRuntime().getNil();
 
         IRubyObject newSelf = null;
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding(blockOfBinding);
             newSelf = threadContext.getFrameSelf();
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf);
         } finally {
             threadContext.postEvalWithBinding(blockOfBinding);
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
         return result;
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(ThreadContext context, IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
 
         ISourcePosition savedPosition = threadContext.getPosition();
         // no binding, just eval in "current" frame (caller's frame)
         Iter iter = threadContext.getFrameIter();
         IRubyObject result = getRuntime().getNil();
 
         try {
             // hack to avoid using previous frame if we're the first frame, since this eval is used to start execution too
             if (threadContext.getPreviousFrame() != null) {
                 threadContext.setFrameIter(threadContext.getPreviousFrameIter());
             }
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, threadContext.getCurrentScope()), this);
         } finally {
             // FIXME: this is broken for Proc, see above
             threadContext.setFrameIter(iter);
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
 
         return result;
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject equal(IRubyObject obj) {
         if (isNil()) {
             return getRuntime().newBoolean(obj.isNil());
         }
         return getRuntime().newBoolean(this == obj);
     }
 
 	public IRubyObject same(IRubyObject other) {
 		return this == other ? getRuntime().getTrue() : getRuntime().getFalse();
 	}
 
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this != original) {
 	        checkFrozen();
 	        if (!getClass().equals(original.getClass())) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	        }
 	    }
 
 	    return this;
 	}
 
     /**
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      */
     public RubyBoolean respond_to(IRubyObject[] args) {
         checkArgumentCount(args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         if (id == 0) {
             id = getRuntime().getObjectSpace().createId(this);
         }
         return getRuntime().newFixnum(id);
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public int hashCode() {
     	return (int) RubyNumeric.fix2long(callMethod(getRuntime().getCurrentContext(), "hash"));
     }
 
     /** rb_obj_type
      *
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn("Object#type is deprecated; use Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *
      */
     public IRubyObject rbClone() {
         IRubyObject clone = doClone();
         clone.setMetaClass(getMetaClass().getSingletonClassClone());
         clone.setTaint(this.isTaint());
         clone.initCopy(this);
         clone.setFrozen(isFrozen());
         return clone;
     }
 
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
     	return getMetaClass().getRealClass().allocate();
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *
      */
     public IRubyObject dup() {
         IRubyObject dup = callMethod(getRuntime().getCurrentContext(), "clone");
         if (!dup.getClass().equals(getClass())) {
             throw getRuntime().newTypeError("duplicated object must be same type");
         }
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         return dup;
     }
 
     /** rb_obj_tainted
      *
      */
     public RubyBoolean tainted() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     public IRubyObject taint() {
         getRuntime().secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
         return this;
     }
 
     /** rb_obj_untaint
      *
      */
     public IRubyObject untaint() {
         getRuntime().secure(3);
         if (isTaint()) {
         	testFrozen("object");
             setTaint(false);
         }
         return this;
     }
 
     /** Freeze an object.
      *
      * rb_obj_freeze
      *
      */
     public IRubyObject freeze() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't freeze object");
         }
         setFrozen(true);
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_inspect
      *
      */
     public IRubyObject inspect() {
         if(getInstanceVariables().size() > 0) {
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
             part.append(" ");
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append("...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     part.append(sep);
                     part.append(name);
                     part.append("=");
                     part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                     sep = ", ";
                 }
                 part.append(">");
                 return getRuntime().newString(part.toString());
             } finally {
                 getRuntime().unregisterInspecting(this);
             }
         }
         return callMethod(getRuntime().getCurrentContext(), "to_s");
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
         for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
             String name = (String) iter.next();
 
             // Do not include constants which also get stored in instance var list in classes.
             if (!Character.isUpperCase(name.charAt(0))) {
                 names.add(getRuntime().newString(name));
             }
         }
         return getRuntime().newArray(names);
     }
 
     /** rb_obj_is_kind_of
      *
      */
     public RubyBoolean kind_of(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!type.isKindOf(getRuntime().getClass("Module"))) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
         }
 
         return getRuntime().newBoolean(isKindOf((RubyModule)type));
     }
 
     /** rb_obj_methods
      *
      */
     public IRubyObject methods(IRubyObject[] args) {
     	checkArgumentCount(args, 0, 1);
 
     	if (args.length == 0) {
     		args = new IRubyObject[] { getRuntime().getTrue() };
     	}
 
         return getMetaClass().instance_methods(args);
     }
 
 	public IRubyObject public_methods(IRubyObject[] args) {
         return getMetaClass().public_instance_methods(args);
 	}
 
     /** rb_obj_protected_methods
      *
      */
     public IRubyObject protected_methods() {
         return getMetaClass().protected_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_private_methods
      *
      */
     public IRubyObject private_methods() {
         return getMetaClass().private_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     public RubyArray singleton_methods() {
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && type instanceof MetaClass;
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 ICallable method = (ICallable) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type) {
                 	continue;
                 }
 
                 RubyString methodName = getRuntime().newString((String) entry.getKey());
                 if (method.getVisibility().isPublic() && ! result.includes(methodName)) {
                     result.append(methodName);
                 }
             }
         }
 
         return result;
     }
 
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asSymbol(), true);
     }
 
     protected IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args) {
         return specificEval(getSingletonClass(), args);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         checkArgumentCount(args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         RubyClass module = getRuntime().getClass("Module");
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(module)) {
                 throw getRuntime().newTypeError(args[i], module);
             }
         }
 
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     public IRubyObject inherited(IRubyObject arg) {
     	return getRuntime().getNil();
     }
     public IRubyObject initialize(IRubyObject[] args) {
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = callMethod(getRuntime().getCurrentContext(), "inspect").toString();
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description,
             noClass ? "" : ":", noClass ? "" : getType().getName()});
 
         if (lastCallType == CallType.VARIABLE) {
         	throw getRuntime().newNameError(msg, name);
         }
         throw getRuntime().newNoMethodError(msg, name);
     }
 
     /**
      * send( aSymbol  [, args  ]*   ) -> anObject
      *
      * Invokes the method identified by aSymbol, passing it any arguments
      * specified. You can use __send__ if the name send clashes with an
      * existing method in this object.
      *
      * <pre>
      * class Klass
      *   def hello(*args)
      *     "Hello " + args.join(' ')
      *   end
      * end
      *
      * k = Klass.new
      * k.send :hello, "gentle", "readers"
      * </pre>
      *
      * @return the result of invoking the method identified by aSymbol.
      */
     public IRubyObject send(IRubyObject[] args) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         ThreadContext tc = getRuntime().getCurrentContext();
 
         tc.setIfBlockAvailable();
         try {
             return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL);
         } finally {
             tc.clearIfBlockAvailable();
         }
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined", id);
    }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('o');
         RubySymbol classname = RubySymbol.newSymbol(getRuntime(), getMetaClass().getName());
         output.dumpObject(classname);
         Map iVars = getInstanceVariablesSnapshot();
         output.dumpInt(iVars.size());
         for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
             String name = (String) iter.next();
             IRubyObject value = (IRubyObject)iVars.get(name);
             
             output.dumpObject(RubySymbol.newSymbol(getRuntime(), name));
             output.dumpObject(value);
         }
     }
    
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#scanArgs()
      */
-    public IRubyObject[] scanArgs(IRuby runtime, IRubyObject[] args, int required, int optional) {
+    public IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional) {
         int total = required+optional;
         int real = checkArgumentCount(args,required,total);
         IRubyObject[] narr = new IRubyObject[total];
         System.arraycopy(args,0,narr,0,real);
         for(int i=real; i<total; i++) {
-            narr[i] = runtime.getNil();
+            narr[i] = getRuntime().getNil();
         }
         return narr;
     }
 }
diff --git a/src/org/jruby/RubyZlib.java b/src/org/jruby/RubyZlib.java
index 4baa6a5083..ae9c107fd1 100644
--- a/src/org/jruby/RubyZlib.java
+++ b/src/org/jruby/RubyZlib.java
@@ -1,559 +1,1041 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 
 import java.io.InputStream;
 import java.io.IOException;
 
 import java.util.List;
 import java.util.ArrayList;
 
 import java.util.zip.GZIPInputStream;
 import java.util.zip.GZIPOutputStream;
 
 import org.jruby.exceptions.RaiseException;
 
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
+import org.jruby.util.CRC32Ext;
+import org.jruby.util.Adler32Ext;
+import org.jruby.util.ZlibInflate;
+import org.jruby.util.ZlibDeflate;
 
 public class RubyZlib {
     /** Create the Zlib module and add it to the Ruby runtime.
      * 
      */
     public static RubyModule createZlibModule(IRuby runtime) {
         RubyModule result = runtime.defineModule("Zlib");
 
         RubyClass gzfile = result.defineClassUnder("GzipFile", runtime.getObject());
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyGzipFile.class);
         gzfile.defineSingletonMethod("wrap", callbackFactory.getSingletonMethod("wrap", RubyGzipFile.class, IRubyObject.class));
         gzfile.defineSingletonMethod("new", callbackFactory.getSingletonMethod("newCreate"));
         gzfile.defineMethod("os_code", callbackFactory.getMethod("os_code"));
         gzfile.defineMethod("closed?", callbackFactory.getMethod("closed_p"));
         gzfile.defineMethod("orig_name", callbackFactory.getMethod("orig_name"));
         gzfile.defineMethod("to_io", callbackFactory.getMethod("to_io"));
         gzfile.defineMethod("finish", callbackFactory.getMethod("finish"));
         gzfile.defineMethod("comment", callbackFactory.getMethod("comment"));
         gzfile.defineMethod("crc", callbackFactory.getMethod("crc"));
         gzfile.defineMethod("mtime", callbackFactory.getMethod("mtime"));
         gzfile.defineMethod("sync", callbackFactory.getMethod("sync"));
         gzfile.defineMethod("close", callbackFactory.getMethod("close"));
         gzfile.defineMethod("level", callbackFactory.getMethod("level"));
         gzfile.defineMethod("sync=", callbackFactory.getMethod("set_sync", IRubyObject.class));
         
         RubyClass gzreader = result.defineClassUnder("GzipReader", gzfile);
         gzreader.includeModule(runtime.getModule("Enumerable"));
         CallbackFactory callbackFactory2 = runtime.callbackFactory(RubyGzipReader.class);
         gzreader.defineSingletonMethod("open", callbackFactory2.getSingletonMethod("open", RubyString.class));
         gzreader.defineSingletonMethod("new", callbackFactory2.getOptSingletonMethod("newCreate"));
         gzreader.defineMethod("initialize", callbackFactory2.getMethod("initialize", IRubyObject.class));
         gzreader.defineMethod("rewind", callbackFactory2.getMethod("rewind"));
         gzreader.defineMethod("lineno", callbackFactory2.getMethod("lineno"));
         gzreader.defineMethod("readline", callbackFactory2.getMethod("readline"));
         gzreader.defineMethod("read", callbackFactory2.getOptMethod("read"));
         gzreader.defineMethod("lineno=", callbackFactory2.getMethod("set_lineno", RubyNumeric.class));
         gzreader.defineMethod("pos", callbackFactory2.getMethod("pos"));
         gzreader.defineMethod("readchar", callbackFactory2.getMethod("readchar"));
         gzreader.defineMethod("readlines", callbackFactory2.getOptMethod("readlines"));
         gzreader.defineMethod("each_byte", callbackFactory2.getMethod("each_byte"));
         gzreader.defineMethod("getc", callbackFactory2.getMethod("getc"));
         gzreader.defineMethod("eof", callbackFactory2.getMethod("eof"));
         gzreader.defineMethod("ungetc", callbackFactory2.getMethod("ungetc", RubyNumeric.class));
         gzreader.defineMethod("each", callbackFactory2.getOptMethod("each"));
         gzreader.defineMethod("unused", callbackFactory2.getMethod("unused"));
         gzreader.defineMethod("eof?", callbackFactory2.getMethod("eof_p"));
         gzreader.defineMethod("gets", callbackFactory2.getOptMethod("gets"));
         gzreader.defineMethod("tell", callbackFactory2.getMethod("tell"));
         
         RubyClass zlibError = result.defineClassUnder("Error", runtime.getClass("StandardError"));
         gzreader.defineClassUnder("Error", zlibError);
 
         RubyClass gzwriter = result.defineClassUnder("GzipWriter", gzfile);
         CallbackFactory callbackFactory3 = runtime.callbackFactory(RubyGzipWriter.class);
         gzwriter.defineSingletonMethod("open", callbackFactory3.getOptSingletonMethod("open"));
         gzwriter.defineSingletonMethod("new", callbackFactory3.getOptSingletonMethod("newCreate"));
         gzwriter.defineMethod("initialize", callbackFactory3.getOptMethod("initialize2"));
         gzwriter.defineMethod("<<", callbackFactory3.getMethod("append", IRubyObject.class));
         gzwriter.defineMethod("printf", callbackFactory3.getOptMethod("printf"));
         gzwriter.defineMethod("pos", callbackFactory3.getMethod("pos"));
         gzwriter.defineMethod("orig_name=", callbackFactory3.getMethod("set_orig_name", RubyString.class));
         gzwriter.defineMethod("putc", callbackFactory3.getMethod("putc", RubyNumeric.class));
         gzwriter.defineMethod("comment=", callbackFactory3.getMethod("set_comment", RubyString.class));
         gzwriter.defineMethod("puts", callbackFactory3.getOptMethod("puts"));
         gzwriter.defineMethod("flush", callbackFactory3.getOptMethod("flush"));
         gzwriter.defineMethod("mtime=", callbackFactory3.getMethod("set_mtime", IRubyObject.class));
         gzwriter.defineMethod("tell", callbackFactory3.getMethod("tell"));
         gzwriter.defineMethod("write", callbackFactory3.getMethod("write", IRubyObject.class));
 
+        result.defineConstant("ZLIB_VERSION",runtime.newString("1.2.1"));
+        result.defineConstant("VERSION",runtime.newString("0.6.0"));
+
+        result.defineConstant("BINARY",runtime.newFixnum(0));
+        result.defineConstant("ASCII",runtime.newFixnum(1));
+        result.defineConstant("UNKNOWN",runtime.newFixnum(2));
+
+        result.defineConstant("DEF_MEM_LEVEL",runtime.newFixnum(8));
+        result.defineConstant("MAX_MEM_LEVEL",runtime.newFixnum(9));
+
+        result.defineConstant("OS_UNIX",runtime.newFixnum(3));
+        result.defineConstant("OS_UNKNOWN",runtime.newFixnum(255));
+        result.defineConstant("OS_CODE",runtime.newFixnum(11));
+        result.defineConstant("OS_ZSYSTEM",runtime.newFixnum(8));
+        result.defineConstant("OS_VMCMS",runtime.newFixnum(4));
+        result.defineConstant("OS_VMS",runtime.newFixnum(2));
+        result.defineConstant("OS_RISCOS",runtime.newFixnum(13));
+        result.defineConstant("OS_MACOS",runtime.newFixnum(7));
+        result.defineConstant("OS_OS2",runtime.newFixnum(6));
+        result.defineConstant("OS_AMIGA",runtime.newFixnum(1));
+        result.defineConstant("OS_QDOS",runtime.newFixnum(12));
+        result.defineConstant("OS_WIN32",runtime.newFixnum(11));
+        result.defineConstant("OS_ATARI",runtime.newFixnum(5));
+        result.defineConstant("OS_MSDOS",runtime.newFixnum(0));
+        result.defineConstant("OS_CPM",runtime.newFixnum(9));
+        result.defineConstant("OS_TOPS20",runtime.newFixnum(10));
+
+        result.defineConstant("DEFAULT_STRATEGY",runtime.newFixnum(0));
+        result.defineConstant("FILTERED",runtime.newFixnum(1));
+        result.defineConstant("HUFFMAN_ONLY",runtime.newFixnum(2));
+
+        result.defineConstant("NO_FLUSH",runtime.newFixnum(0));
+        result.defineConstant("SYNC_FLUSH",runtime.newFixnum(2));
+        result.defineConstant("FULL_FLUSH",runtime.newFixnum(3));
+        result.defineConstant("FINISH",runtime.newFixnum(4));
+
+        result.defineConstant("NO_COMPRESSION",runtime.newFixnum(0));
+        result.defineConstant("BEST_SPEED",runtime.newFixnum(1));
+        result.defineConstant("DEFAULT_COMPRESSION",runtime.newFixnum(-1));
+        result.defineConstant("BEST_COMPRESSION",runtime.newFixnum(9));
+
+        result.defineConstant("MAX_WBITS",runtime.newFixnum(15));
+
+        CallbackFactory cf = runtime.callbackFactory(RubyZlib.class);
+        result.defineModuleFunction("zlib_version",cf.getSingletonMethod("zlib_version"));
+        result.defineModuleFunction("version",cf.getSingletonMethod("version"));
+        result.defineModuleFunction("adler32",cf.getOptSingletonMethod("adler32"));
+        result.defineModuleFunction("crc32",cf.getOptSingletonMethod("crc32"));
+        result.defineModuleFunction("crc_table",cf.getSingletonMethod("crc_table"));
+
+        result.defineClassUnder("StreamEnd",zlibError);
+        result.defineClassUnder("StreamError",zlibError);
+        result.defineClassUnder("BufError",zlibError);
+        result.defineClassUnder("NeedDict",zlibError);
+        result.defineClassUnder("MemError",zlibError);
+        result.defineClassUnder("VersionError",zlibError);
+        result.defineClassUnder("DataError",zlibError);
+
+        RubyClass gzError = gzfile.defineClassUnder("Error",zlibError);
+        gzfile.defineClassUnder("CRCError",gzError);
+        gzfile.defineClassUnder("NoFooter",gzError);
+        gzfile.defineClassUnder("LengthError",gzError);
+
+        RubyClass zstream = result.defineClassUnder("ZStream", runtime.getObject());
+        CallbackFactory zstreamcb = runtime.callbackFactory(ZStream.class);
+        zstream.defineMethod("initialize",zstreamcb.getMethod("initialize"));
+        zstream.defineMethod("flush_next_out",zstreamcb.getMethod("flush_next_out"));
+        zstream.defineMethod("total_out",zstreamcb.getMethod("total_out"));
+        zstream.defineMethod("stream_end?",zstreamcb.getMethod("stream_end_p"));
+        zstream.defineMethod("data_type",zstreamcb.getMethod("data_type"));
+        zstream.defineMethod("closed?",zstreamcb.getMethod("closed_p"));
+        zstream.defineMethod("ended?",zstreamcb.getMethod("ended_p"));
+        zstream.defineMethod("end",zstreamcb.getMethod("end"));
+        zstream.defineMethod("reset",zstreamcb.getMethod("reset"));
+        zstream.defineMethod("avail_out",zstreamcb.getMethod("avail_out"));
+        zstream.defineMethod("avail_out=",zstreamcb.getMethod("set_avail_out",IRubyObject.class));
+        zstream.defineMethod("adler",zstreamcb.getMethod("adler"));
+        zstream.defineMethod("finish",zstreamcb.getMethod("finish"));
+        zstream.defineMethod("avail_in",zstreamcb.getMethod("avail_in"));
+        zstream.defineMethod("flush_next_in",zstreamcb.getMethod("flush_next_in"));
+        zstream.defineMethod("total_in",zstreamcb.getMethod("total_in"));
+        zstream.defineMethod("finished?",zstreamcb.getMethod("finished_p"));
+        zstream.defineMethod("close",zstreamcb.getMethod("close"));
+
+        RubyClass infl = result.defineClassUnder("Inflate", zstream);
+        CallbackFactory inflcb = runtime.callbackFactory(Inflate.class);
+        infl.defineSingletonMethod("new",inflcb.getOptSingletonMethod("newInstance"));
+        infl.defineSingletonMethod("inflate",inflcb.getSingletonMethod("s_inflate",IRubyObject.class));
+        infl.defineMethod("initialize",inflcb.getOptMethod("_initialize"));
+        infl.defineMethod("<<",inflcb.getMethod("append",IRubyObject.class));
+        infl.defineMethod("sync_point?",inflcb.getMethod("sync_point_p"));
+        infl.defineMethod("set_dictionary",inflcb.getMethod("set_dictionary",IRubyObject.class));
+        infl.defineMethod("inflate",inflcb.getMethod("inflate",IRubyObject.class));
+        infl.defineMethod("sync",inflcb.getMethod("sync",IRubyObject.class));
+
+        RubyClass defl = result.defineClassUnder("Deflate", zstream);
+        CallbackFactory deflcb = runtime.callbackFactory(Deflate.class);
+        defl.defineSingletonMethod("new",deflcb.getOptSingletonMethod("newInstance"));
+        defl.defineSingletonMethod("deflate",deflcb.getOptSingletonMethod("s_deflate"));
+        defl.defineMethod("initialize",deflcb.getOptMethod("_initialize"));
+        defl.defineMethod("<<",deflcb.getMethod("append",IRubyObject.class));
+        defl.defineMethod("params",deflcb.getMethod("params",IRubyObject.class,IRubyObject.class));
+        defl.defineMethod("set_dictionary",deflcb.getMethod("set_dictionary",IRubyObject.class));
+        defl.defineMethod("flush",deflcb.getOptMethod("flush"));
+        defl.defineMethod("deflate",deflcb.getOptMethod("deflate"));
+
+        runtime.getModule("Kernel").callMethod(runtime.getCurrentContext(),"require",runtime.newString("stringio"));
+
         return result;
     }
 
+    public static IRubyObject zlib_version(IRubyObject recv) {
+        return ((RubyModule)recv).getConstant("ZLIB_VERSION");
+    }
+
+    public static IRubyObject version(IRubyObject recv) {
+        return ((RubyModule)recv).getConstant("VERSION");
+    }
+
+    public static IRubyObject crc32(IRubyObject recv, IRubyObject[] args) throws Exception {
+        args = recv.scanArgs(args,0,2);
+        int crc = 0;
+        String str = null;
+        if(!args[0].isNil()) {
+            str = args[0].toString();
+        }
+        if(!args[1].isNil()) {
+            crc = RubyNumeric.fix2int(args[1]);
+        }
+        CRC32Ext ext = new CRC32Ext(crc);
+        if(str != null) {
+            ext.update(str.getBytes("PLAIN"));
+        }
+        return recv.getRuntime().newFixnum(ext.getValue());
+    }
+
+    public static IRubyObject adler32(IRubyObject recv, IRubyObject[] args) throws Exception {
+        args = recv.scanArgs(args,0,2);
+        int adler = 1;
+        String str = null;
+        if(!args[0].isNil()) {
+            str = args[0].toString();
+        }
+        if(!args[1].isNil()) {
+            adler = RubyNumeric.fix2int(args[1]);
+        }
+        Adler32Ext ext = new Adler32Ext(adler);
+        if(str != null) {
+            ext.update(str.getBytes("PLAIN"));
+        }
+        return recv.getRuntime().newFixnum(ext.getValue());
+    }
+
+    private final static long[] crctab = new long[]{
+        0L, 1996959894L, 3993919788L, 2567524794L, 124634137L, 1886057615L, 3915621685L, 2657392035L, 249268274L, 2044508324L, 3772115230L, 2547177864L, 162941995L, 
+        2125561021L, 3887607047L, 2428444049L, 498536548L, 1789927666L, 4089016648L, 2227061214L, 450548861L, 1843258603L, 4107580753L, 2211677639L, 325883990L, 
+        1684777152L, 4251122042L, 2321926636L, 335633487L, 1661365465L, 4195302755L, 2366115317L, 997073096L, 1281953886L, 3579855332L, 2724688242L, 1006888145L, 
+        1258607687L, 3524101629L, 2768942443L, 901097722L, 1119000684L, 3686517206L, 2898065728L, 853044451L, 1172266101L, 3705015759L, 2882616665L, 651767980L, 
+        1373503546L, 3369554304L, 3218104598L, 565507253L, 1454621731L, 3485111705L, 3099436303L, 671266974L, 1594198024L, 3322730930L, 2970347812L, 795835527L, 
+        1483230225L, 3244367275L, 3060149565L, 1994146192L, 31158534L, 2563907772L, 4023717930L, 1907459465L, 112637215L, 2680153253L, 3904427059L, 2013776290L, 
+        251722036L, 2517215374L, 3775830040L, 2137656763L, 141376813L, 2439277719L, 3865271297L, 1802195444L, 476864866L, 2238001368L, 4066508878L, 1812370925L, 
+        453092731L, 2181625025L, 4111451223L, 1706088902L, 314042704L, 2344532202L, 4240017532L, 1658658271L, 366619977L, 2362670323L, 4224994405L, 1303535960L, 
+        984961486L, 2747007092L, 3569037538L, 1256170817L, 1037604311L, 2765210733L, 3554079995L, 1131014506L, 879679996L, 2909243462L, 3663771856L, 1141124467L, 
+        855842277L, 2852801631L, 3708648649L, 1342533948L, 654459306L, 3188396048L, 3373015174L, 1466479909L, 544179635L, 3110523913L, 3462522015L, 1591671054L, 
+        702138776L, 2966460450L, 3352799412L, 1504918807L, 783551873L, 3082640443L, 3233442989L, 3988292384L, 2596254646L, 62317068L, 1957810842L, 3939845945L, 
+        2647816111L, 81470997L, 1943803523L, 3814918930L, 2489596804L, 225274430L, 2053790376L, 3826175755L, 2466906013L, 167816743L, 2097651377L, 4027552580L, 
+        2265490386L, 503444072L, 1762050814L, 4150417245L, 2154129355L, 426522225L, 1852507879L, 4275313526L, 2312317920L, 282753626L, 1742555852L, 4189708143L, 
+        2394877945L, 397917763L, 1622183637L, 3604390888L, 2714866558L, 953729732L, 1340076626L, 3518719985L, 2797360999L, 1068828381L, 1219638859L, 3624741850L, 
+        2936675148L, 906185462L, 1090812512L, 3747672003L, 2825379669L, 829329135L, 1181335161L, 3412177804L, 3160834842L, 628085408L, 1382605366L, 3423369109L, 
+        3138078467L, 570562233L, 1426400815L, 3317316542L, 2998733608L, 733239954L, 1555261956L, 3268935591L, 3050360625L, 752459403L, 1541320221L, 2607071920L, 
+        3965973030L, 1969922972L, 40735498L, 2617837225L, 3943577151L, 1913087877L, 83908371L, 2512341634L, 3803740692L, 2075208622L, 213261112L, 2463272603L, 
+        3855990285L, 2094854071L, 198958881L, 2262029012L, 4057260610L, 1759359992L, 534414190L, 2176718541L, 4139329115L, 1873836001L, 414664567L, 2282248934L, 
+        4279200368L, 1711684554L, 285281116L, 2405801727L, 4167216745L, 1634467795L, 376229701L, 2685067896L, 3608007406L, 1308918612L, 956543938L, 2808555105L, 
+        3495958263L, 1231636301L, 1047427035L, 2932959818L, 3654703836L, 1088359270L, 936918000L, 2847714899L, 3736837829L, 1202900863L, 817233897L, 3183342108L, 
+        3401237130L, 1404277552L, 615818150L, 3134207493L, 3453421203L, 1423857449L, 601450431L, 3009837614L, 3294710456L, 1567103746L, 711928724L, 3020668471L, 
+        3272380065L, 1510334235L, 755167117};
+
+    public static IRubyObject crc_table(IRubyObject recv) {
+        List ll = new ArrayList(crctab.length);
+        for(int i=0;i<crctab.length;i++) {
+            ll.add(recv.getRuntime().newFixnum(crctab[i]));
+        }
+        return recv.getRuntime().newArray(ll);
+    }
+
+
+    public static abstract class ZStream extends RubyObject {
+        protected boolean closed = false;
+        protected boolean ended = false;
+        protected boolean finished = false;
+
+        protected abstract int internalTotalOut();
+        protected abstract boolean internalStreamEndP();
+        protected abstract void internalEnd();
+        protected abstract void internalReset();
+        protected abstract int internalAdler();
+        protected abstract IRubyObject internalFinish() throws Exception;
+        protected abstract int internalTotalIn();
+        protected abstract void internalClose();
+
+        public ZStream(IRuby runtime, RubyClass type) {
+            super(runtime, type);
+        }
+
+        public IRubyObject initialize() {
+            return this;
+        }
+
+        public IRubyObject flush_next_out() {
+            return getRuntime().getNil();
+        }
+
+        public IRubyObject total_out() {
+            return getRuntime().newFixnum(internalTotalOut());
+        }
+
+        public IRubyObject stream_end_p() {
+            return internalStreamEndP() ? getRuntime().getTrue() : getRuntime().getFalse();
+        }
+
+        public IRubyObject data_type() {
+            return getRuntime().getModule("Zlib").getConstant("UNKNOWN");
+        }
+
+        public IRubyObject closed_p() {
+            return closed ? getRuntime().getTrue() : getRuntime().getFalse();
+        }
+
+        public IRubyObject ended_p() {
+            return ended ? getRuntime().getTrue() : getRuntime().getFalse();
+        }
+
+        public IRubyObject end() {
+            if(!ended) {
+                internalEnd();
+                ended = true;
+            }
+            return getRuntime().getNil();
+        }
+
+        public IRubyObject reset() {
+            internalReset();
+            return getRuntime().getNil();
+        }
+
+        public IRubyObject avail_out() {
+            return RubyFixnum.zero(getRuntime());
+        }
+
+        public IRubyObject set_avail_out(IRubyObject p1) {
+            return p1;
+        }
+
+        public IRubyObject adler() {
+            return getRuntime().newFixnum(internalAdler());
+        }
+
+        public IRubyObject finish() throws Exception {
+            if(!finished) {
+                finished = true;
+                return internalFinish();
+            }
+            return getRuntime().newString("");
+        }
+
+        public IRubyObject avail_in() {
+            return RubyFixnum.zero(getRuntime());
+        }
+
+        public IRubyObject flush_next_in() {
+            return getRuntime().getNil();
+        }
+
+        public IRubyObject total_in() {
+            return getRuntime().newFixnum(internalTotalIn());
+        }
+
+        public IRubyObject finished_p() {
+            return finished ? getRuntime().getTrue() : getRuntime().getFalse();
+        }
+
+        public IRubyObject close() {
+            if(!closed) {
+                internalClose();
+                closed = true;
+            }
+            return getRuntime().getNil();
+        }
+    }
+
+    public static class Inflate extends ZStream {
+        public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args) {
+            IRubyObject result = new Inflate(recv.getRuntime(), (RubyClass)recv);
+            result.callInit(args);
+            return result;
+        }
+
+        public static IRubyObject s_inflate(IRubyObject recv, IRubyObject string) throws Exception {
+            return ZlibInflate.s_inflate(recv,string.toString());
+        }
+
+        public Inflate(IRuby runtime, RubyClass type) {
+            super(runtime, type);
+        }
+
+        private ZlibInflate infl;
+
+        public IRubyObject _initialize(IRubyObject[] args) throws Exception {
+            infl = new ZlibInflate(this);
+            return this;
+        }
+
+        public IRubyObject append(IRubyObject arg) {
+            infl.append(arg);
+            return this;
+        }
+
+        public IRubyObject sync_point_p() {
+            return infl.sync_point();
+        }
+
+        public IRubyObject set_dictionary(IRubyObject arg) throws Exception {
+            return infl.set_dictionary(arg);
+        }
+
+        public IRubyObject inflate(IRubyObject string) throws Exception {
+            return infl.inflate(string.toString());
+        }
+
+        public IRubyObject sync(IRubyObject string) {
+            return infl.sync(string);
+        }
+
+        protected int internalTotalOut() {
+            return infl.getInflater().getTotalOut();
+        }
+
+        protected boolean internalStreamEndP() {
+            return infl.getInflater().finished();
+        }
+
+        protected void internalEnd() {
+            infl.getInflater().end();
+        }
+
+        protected void internalReset() {
+            infl.getInflater().reset();
+        }
+
+        protected int internalAdler() {
+            return infl.getInflater().getAdler();
+        }
+
+        protected IRubyObject internalFinish() throws Exception {
+            infl.finish();
+            return getRuntime().getNil();
+        }
+
+        public IRubyObject finished_p() {
+            return infl.getInflater().finished() ? getRuntime().getTrue() : getRuntime().getFalse();
+        }
+
+        protected int internalTotalIn() {
+            return infl.getInflater().getTotalIn();
+        }
+
+        protected void internalClose() {
+            infl.close();
+        }
+    }
+
+    public static class Deflate extends ZStream {
+        public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args) {
+            IRubyObject result = new Deflate(recv.getRuntime(), (RubyClass)recv);
+            result.callInit(args);
+            return result;
+        }
+
+        public static IRubyObject s_deflate(IRubyObject recv, IRubyObject[] args) throws Exception {
+            args = recv.scanArgs(args,1,1);
+            int level = -1;
+            if(!args[1].isNil()) {
+                level = RubyNumeric.fix2int(args[1]);
+            }
+            return ZlibDeflate.s_deflate(recv,args[0].toString(),level);
+        }
+
+        public Deflate(IRuby runtime, RubyClass type) {
+            super(runtime, type);
+        }
+
+        private ZlibDeflate defl;
+
+        public IRubyObject _initialize(IRubyObject[] args) throws Exception {
+            args = scanArgs(args,0,4);
+            int level = -1;
+            int window_bits = 15;
+            int memlevel = 8;
+            int strategy = 0;
+            if(!args[0].isNil()) {
+                level = RubyNumeric.fix2int(args[0]);
+            }
+            if(!args[1].isNil()) {
+                window_bits = RubyNumeric.fix2int(args[1]);
+            }
+            if(!args[2].isNil()) {
+                memlevel = RubyNumeric.fix2int(args[2]);
+            }
+            if(!args[3].isNil()) {
+                strategy = RubyNumeric.fix2int(args[3]);
+            }
+            defl = new ZlibDeflate(this,level,window_bits,memlevel,strategy);
+            return this;
+        }
+
+        public IRubyObject append(IRubyObject arg) throws Exception {
+            defl.append(arg);
+            return this;
+        }
+
+        public IRubyObject params(IRubyObject level, IRubyObject strategy) {
+            defl.params(RubyNumeric.fix2int(level),RubyNumeric.fix2int(strategy));
+            return getRuntime().getNil();
+        }
+
+        public IRubyObject set_dictionary(IRubyObject arg) throws Exception {
+            return defl.set_dictionary(arg);
+        }
+        
+        public IRubyObject flush(IRubyObject[] args) throws Exception {
+            int flush = 2; // SYNC_FLUSH
+            if(checkArgumentCount(args,0,1) == 1) {
+                if(!args[0].isNil()) {
+                    flush = RubyNumeric.fix2int(args[0]);
+                }
+            }
+            return defl.flush(flush);
+        }
+
+        public IRubyObject deflate(IRubyObject[] args) throws Exception {
+            args = scanArgs(args,1,1);
+            int flush = 0; // NO_FLUSH
+            if(!args[1].isNil()) {
+                flush = RubyNumeric.fix2int(args[1]);
+            }
+            return defl.deflate(args[0].toString(),flush);
+        }
+
+        protected int internalTotalOut() {
+            return defl.getDeflater().getTotalOut();
+        }
+
+        protected boolean internalStreamEndP() {
+            return defl.getDeflater().finished();
+        }
+
+        protected void internalEnd() {
+            defl.getDeflater().end();
+        }
+
+        protected void internalReset() {
+            defl.getDeflater().reset();
+        }
+
+        protected int internalAdler() {
+            return defl.getDeflater().getAdler();
+        }
+
+        protected IRubyObject internalFinish() throws Exception {
+            return defl.finish();
+        }
+
+        protected int internalTotalIn() {
+            return defl.getDeflater().getTotalIn();
+        }
+
+        protected void internalClose() {
+            defl.close();
+        }
+    }
+
     public static class RubyGzipFile extends RubyObject {
         public static IRubyObject wrap(IRubyObject recv, RubyGzipFile io, IRubyObject proc) throws IOException {
             if (!proc.isNil()) {
                 try {
                     ((RubyProc)proc).call(new IRubyObject[]{io});
                 } finally {
                     if (!io.isClosed()) {
                         io.close();
                     }
                 }
                 return recv.getRuntime().getNil();
             }
 
             return io;
         }
 
         public static RubyGzipFile newCreate(IRubyObject recv) {
             RubyGzipFile result = new RubyGzipFile(recv.getRuntime(), (RubyClass) recv);
             result.callInit(new IRubyObject[0]);
             return result;
         }
 
         protected boolean closed = false;
         protected boolean finished = false;
         private int os_code = 255;
         private int level = -1;
         private String orig_name;
         private String comment;
         protected IRubyObject realIo;
         private IRubyObject mtime;
 
         public RubyGzipFile(IRuby runtime, RubyClass type) {
             super(runtime, type);
             mtime = runtime.getNil();
         }
         
         public IRubyObject os_code() {
             return getRuntime().newFixnum(os_code);
         }
         
         public IRubyObject closed_p() {
             return closed ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         
         protected boolean isClosed() {
             return closed;
         }
         
         public IRubyObject orig_name() {
             return orig_name == null ? getRuntime().getNil() : getRuntime().newString(orig_name);
         }
         
         public Object to_io() {
             return realIo;
         }
         
         public IRubyObject comment() {
             return comment == null ? getRuntime().getNil() : getRuntime().newString(comment);
         }
         
         public IRubyObject crc() {
             return RubyFixnum.zero(getRuntime());
         }
         
         public IRubyObject mtime() {
             return mtime;
         }
         
         public IRubyObject sync() {
             return getRuntime().getNil();
         }
         
         public IRubyObject finish() throws IOException {
             if (!finished) {
                 //io.finish();
             }
             finished = true;
             return realIo;
         }
 
         public IRubyObject close() throws IOException {
             return null;
         }
         
         public IRubyObject level() {
             return getRuntime().newFixnum(level);
         }
         
         public IRubyObject set_sync(IRubyObject ignored) {
             return getRuntime().getNil();
         }
     }
 
     public static class RubyGzipReader extends RubyGzipFile {
         private static RubyGzipReader newInstance(IRubyObject recv, IRubyObject[] args) {
             RubyGzipReader result = new RubyGzipReader(recv.getRuntime(), recv.getRuntime().getModule("Zlib").getClass("GzipReader"));
             result.callInit(args);
             return result;
         }
 
         public static RubyGzipReader newCreate(IRubyObject recv, IRubyObject[] args) {
             RubyGzipReader result = new RubyGzipReader(recv.getRuntime(), (RubyClass)recv);
             result.callInit(args);
             return result;
         }
 
         public static IRubyObject open(IRubyObject recv, RubyString filename) throws IOException {
             RubyObject proc = (recv.getRuntime().getCurrentContext().isBlockGiven()) ? (RubyObject)recv.getRuntime().newProc() : (RubyObject)recv.getRuntime().getNil();
             RubyGzipReader io = newInstance(recv,new IRubyObject[]{recv.getRuntime().getClass("File").callMethod(recv.getRuntime().getCurrentContext(),"open", new IRubyObject[]{filename,recv.getRuntime().newString("rb")})});
             
             return RubyGzipFile.wrap(recv, io, proc);
         }
 
         public RubyGzipReader(IRuby runtime, RubyClass type) {
             super(runtime, type);
         }
         
         private int line;
         private InputStream io;
         
         public IRubyObject initialize(IRubyObject io) {
             realIo = io;
             try {
                 this.io = new GZIPInputStream(new IOInputStream(io));
             } catch (IOException e) {
                 IRuby runtime = io.getRuntime();
                 RubyClass errorClass = runtime.getModule("Zlib").getClass("GzipReader").getClass("Error");
                 throw new RaiseException(RubyException.newException(runtime, errorClass, e.getMessage()));
             }
 
             line = 1;
             
             return this;
         }
         
         public IRubyObject rewind() {
             return getRuntime().getNil();
         }
         
         public IRubyObject lineno() {
             return getRuntime().newFixnum(line);
         }
 
         public IRubyObject readline() throws IOException {
             IRubyObject dst = gets(new IRubyObject[0]);
             if (dst.isNil()) {
                 throw getRuntime().newEOFError();
             }
             return dst;
         }
 
         public IRubyObject internalGets(IRubyObject[] args) throws IOException {
             String sep = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getValue().toString();
             if (args.length > 0) {
                 sep = args[0].toString();
             }
             return internalSepGets(sep);
         }
 
         private IRubyObject internalSepGets(String sep) throws IOException {
             StringBuffer result = new StringBuffer();
             char ce = (char) io.read();
             while (ce != -1 && sep.indexOf(ce) == -1) {
                 result.append((char) ce);
                 ce = (char) io.read();
             }
             line++;
             return getRuntime().newString(result.append(sep).toString());
         }
 
         public IRubyObject gets(IRubyObject[] args) throws IOException {
             IRubyObject result = internalGets(args);
             if (!result.isNil()) {
                 getRuntime().getCurrentContext().setLastline(result);
             }
             return result;
         }
 
         private final static int BUFF_SIZE = 4096;
         public IRubyObject read(IRubyObject[] args) throws IOException {
             if (args.length == 0 || args[0].isNil()) {
                 StringBuffer val = new StringBuffer();
                 byte[] buffer = new byte[BUFF_SIZE];
                 int read = io.read(buffer);
                 while (read != -1) {
                     val.append(new String(buffer,0,read));
                     read = io.read(buffer);
                 }
                 return getRuntime().newString(val.toString());
             } 
 
             int len = RubyNumeric.fix2int(args[0]);
             if (len < 0) {
             	throw getRuntime().newArgumentError("negative length " + len + " given");
             } else if (len > 0) {
             	byte[] buffer = new byte[len];
             	int toRead = len;
             	int offset = 0;
             	int read = 0;
             	while (toRead > 0) {
             		read = io.read(buffer,offset,toRead);
             		if (read == -1) {
             			break;
             		}
             		toRead -= read;
             		offset += read;
             	}
             	return getRuntime().newString(new String(buffer,0,len-toRead, "PLAIN"));
             }
                 
             return getRuntime().newString("");
         }
 
         public IRubyObject set_lineno(RubyNumeric lineArg) {
             line = RubyNumeric.fix2int(lineArg);
             return lineArg;
         }
 
         public IRubyObject pos() {
             return RubyFixnum.zero(getRuntime());
         }
         
         public IRubyObject readchar() throws IOException {
             int value = io.read();
             if (value == -1) {
                 throw getRuntime().newEOFError();
             }
             return getRuntime().newFixnum(value);
         }
 
         public IRubyObject getc() throws IOException {
             int value = io.read();
             return value == -1 ? getRuntime().getNil() : getRuntime().newFixnum(value);
         }
 
         private boolean isEof() throws IOException {
             return ((GZIPInputStream)io).available() != 1;
         }
 
         public IRubyObject close() throws IOException {
             if (!closed) {
                 io.close();
             }
             this.closed = true;
             return getRuntime().getNil();
         }
         
         public IRubyObject eof() throws IOException {
             return isEof() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         public IRubyObject eof_p() throws IOException {
             return eof();
         }
 
         public IRubyObject unused() {
             return getRuntime().getNil();
         }
 
         public IRubyObject tell() {
             return getRuntime().getNil();
         }
 
         public IRubyObject each(IRubyObject[] args) throws IOException {
             String sep = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getValue().toString();
             
             if (args.length > 0 && !args[0].isNil()) {
                 sep = args[0].toString();
             }
 
             ThreadContext context = getRuntime().getCurrentContext();
             while (!isEof()) {
                 context.yield(internalSepGets(sep));
             }
             
             return getRuntime().getNil();
         }
     
         public IRubyObject ungetc(RubyNumeric arg) {
             return getRuntime().getNil();
         }
 
         public IRubyObject readlines(IRubyObject[] args) throws IOException {
             List array = new ArrayList();
             
             if (args.length != 0 && args[0].isNil()) {
                 array.add(read(new IRubyObject[0]));
             } else {
                 String seperator = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getValue().toString();
                 if (args.length > 0) {
                     seperator = args[0].toString();
                 }
                 while (!isEof()) {
                     array.add(internalSepGets(seperator));
                 }
             }
             return getRuntime().newArray(array);
         }
 
         public IRubyObject each_byte() throws IOException {
             int value = io.read();
 
             ThreadContext context = getRuntime().getCurrentContext();
             while (value != -1) {
                 context.yield(getRuntime().newFixnum(value));
                 value = io.read();
             }
             
             return getRuntime().getNil();
         }
     }
 
     public static class RubyGzipWriter extends RubyGzipFile {
         private static RubyGzipWriter newInstance(IRubyObject recv, IRubyObject[] args) {
             RubyGzipWriter result = new RubyGzipWriter(recv.getRuntime(), recv.getRuntime().getModule("Zlib").getClass("GzipWriter"));
             result.callInit(args);
             return result;
         }
 
         public static RubyGzipWriter newCreate(IRubyObject recv, IRubyObject[] args) {
             RubyGzipWriter result = new RubyGzipWriter(recv.getRuntime(), (RubyClass)recv);
             result.callInit(args);
             return result;
         }
         public static IRubyObject open(IRubyObject recv, IRubyObject[] args) throws IOException {
             IRubyObject level = recv.getRuntime().getNil();
             IRubyObject strategy = recv.getRuntime().getNil();
             ThreadContext context = recv.getRuntime().getCurrentContext();
             if (args.length>1) {
                 level = args[1];
                 if (args.length>2) {
                     strategy = args[2];
                 }
             }
 
             RubyObject proc = (context.isBlockGiven()) ? (RubyObject)recv.getRuntime().newProc() : (RubyObject)recv.getRuntime().getNil();
             RubyGzipWriter io = newInstance(recv,new IRubyObject[]{recv.getRuntime().getClass("File").callMethod(context,"open", new IRubyObject[]{args[0],recv.getRuntime().newString("wb")}),level,strategy});
             return RubyGzipFile.wrap(recv, io, proc);
         }
 
         public RubyGzipWriter(IRuby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         private GZIPOutputStream io;
         public IRubyObject initialize2(IRubyObject[] args) throws IOException {
             realIo = (RubyObject)args[0];
             this.io = new GZIPOutputStream(new IOOutputStream(args[0]));
             
             return this;
         }
 
         public IRubyObject close() throws IOException {
             if (!closed) {
                 io.close();
             }
             this.closed = true;
             
             return getRuntime().getNil();
         }
 
         public IRubyObject append(IRubyObject p1) throws IOException {
             this.write(p1);
             return this;
         }
 
         public IRubyObject printf(IRubyObject[] args) throws IOException {
             write(RubyKernel.sprintf(this, args));
             return getRuntime().getNil();
         }
 
         public IRubyObject print(IRubyObject[] args) throws IOException {
             if (args.length != 0) {
                 for (int i = 0, j = args.length; i < j; i++) {
                     write(args[i]);
                 }
             }
             
             IRubyObject sep = getRuntime().getGlobalVariables().get("$\\");
             if (!sep.isNil()) {
                 write(sep);
             }
             
             return getRuntime().getNil();
         }
 
         public IRubyObject pos() {
             return getRuntime().getNil();
         }
 
         public IRubyObject set_orig_name(RubyString ignored) {
             return getRuntime().getNil();
         }
 
         public IRubyObject set_comment(RubyString ignored) {
             return getRuntime().getNil();
         }
 
         public IRubyObject putc(RubyNumeric p1) throws IOException {
             io.write(RubyNumeric.fix2int(p1));
             return p1;
         }
         
         public IRubyObject puts(IRubyObject[] args) throws IOException {
             RubyStringIO sio = (RubyStringIO)RubyStringIO.newInstance(this, new IRubyObject[0]);
             sio.puts(args);
             write(sio.string());
             
             return getRuntime().getNil();
         }
 
         public IRubyObject finish() throws IOException {
             if (!finished) {
                 io.finish();
             }
             finished = true;
             return realIo;
         }
 
         public IRubyObject flush(IRubyObject[] args) throws IOException {
             if (args.length == 0 || args[0].isNil() || RubyNumeric.fix2int(args[0]) != 0) { // Zlib::NO_FLUSH
                 io.flush();
             }
             return getRuntime().getNil();
         }
 
         public IRubyObject set_mtime(IRubyObject ignored) {
             return getRuntime().getNil();
         }
 
         public IRubyObject tell() {
             return getRuntime().getNil();
         }
 
         public IRubyObject write(IRubyObject p1) throws IOException {
             String str = p1.toString();
             io.write(str.getBytes("ISO8859_1"));
             return getRuntime().newFixnum(str.length());
         }
     }
 }
diff --git a/src/org/jruby/libraries/ZlibLibrary.java b/src/org/jruby/libraries/ZlibLibrary.java
index 32dc135b0b..b9b7dabac1 100644
--- a/src/org/jruby/libraries/ZlibLibrary.java
+++ b/src/org/jruby/libraries/ZlibLibrary.java
@@ -1,42 +1,41 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 package org.jruby.libraries;
 
 import java.io.IOException;
 
 import org.jruby.RubyZlib;
 import org.jruby.IRuby;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.BuiltinScript;
 
 public class ZlibLibrary implements Library {
     public void load(final IRuby runtime) throws IOException {
         RubyZlib.createZlibModule(runtime);
-        new BuiltinScript("zlib").load(runtime);
     }
 }
diff --git a/src/org/jruby/runtime/builtin/IRubyObject.java b/src/org/jruby/runtime/builtin/IRubyObject.java
index adee77c14e..c3bcbbb5da 100644
--- a/src/org/jruby/runtime/builtin/IRubyObject.java
+++ b/src/org/jruby/runtime/builtin/IRubyObject.java
@@ -1,337 +1,336 @@
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
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 package org.jruby.runtime.builtin;
 
 import java.io.IOException;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.MetaClass;
 import org.jruby.IRuby;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.ast.Node;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 
 /** Object is the parent class of all classes in Ruby. Its methods are
  * therefore available to all objects unless explicitly overridden.
  *
  * @author  jpetersen
  */
 public interface IRubyObject {
     public static final IRubyObject[] NULL_ARRAY = new IRubyObject[0];
     
     /**
      * RubyMethod getInstanceVar.
      * @param string
      * @return RubyObject
      */
     IRubyObject getInstanceVariable(String string);
 
     /**
      * RubyMethod setInstanceVar.
      * @param string
      * @param rubyObject
      * @return RubyObject
      */
     IRubyObject setInstanceVariable(String string, IRubyObject rubyObject);
     
     Map getInstanceVariables();
     Map getInstanceVariablesSnapshot();
 
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name, IRubyObject[] args, CallType callType);
     
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType);
     
     /**
      * RubyMethod funcall.
      * @param context TODO
      * @param string
      * @return RubyObject
      */
     IRubyObject callMethod(ThreadContext context, String string);
 
     /**
      * RubyMethod isNil.
      * @return boolean
      */
     boolean isNil();
 
     boolean isTrue();
 
     /**
      * RubyMethod isTaint.
      * @return boolean
      */
     boolean isTaint();
 
     /**
      * RubyMethod isFrozen.
      * @return boolean
      */
     boolean isFrozen();
 
     /**
      * RubyMethod funcall.
      * @param context TODO
      * @param string
      * @param arg
      * @return RubyObject
      */
     IRubyObject callMethod(ThreadContext context, String string, IRubyObject arg);
 
     /**
      * RubyMethod getRubyClass.
      */
     RubyClass getMetaClass();
 
     void setMetaClass(RubyClass metaClass);
 
     /**
      * RubyMethod getSingletonClass.
      * @return RubyClass
      */
     MetaClass getSingletonClass();
 
     /**
      * RubyMethod getType.
      * @return RubyClass
      */
     RubyClass getType();
 
     /**
      * RubyMethod isKindOf.
      * @param rubyClass
      * @return boolean
      */
     boolean isKindOf(RubyModule rubyClass);
 
     /**
      * RubyMethod respondsTo.
      * @param string
      * @return boolean
      */
     boolean respondsTo(String string);
 
     /**
      * RubyMethod getRuntime.
      */
     IRuby getRuntime();
 
     /**
      * RubyMethod getJavaClass.
      * @return Class
      */
     Class getJavaClass();
 
     /**
      * RubyMethod callMethod.
      * @param context TODO
      * @param method
      * @param rubyArgs
      * @return IRubyObject
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs);
 
     /**
      * RubyMethod eval.
      * @param iNode
      * @return IRubyObject
      */
     IRubyObject eval(Node iNode);
 
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalWithBinding(ThreadContext context, IRubyObject evalString, IRubyObject binding, String file);
 
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @param binding The binding object under which to perform the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalSimple(ThreadContext context, IRubyObject evalString, String file);
 
     /**
      * RubyMethod extendObject.
      * @param rubyModule
      */
     void extendObject(RubyModule rubyModule);
 
     /**
      * Convert the object into a symbol name if possible.
      * 
      * @return String the symbol name
      */
     String asSymbol();
 
     /**
      * Methods which perform to_xxx if the object has such a method
      */
     RubyArray convertToArray();
     RubyFloat convertToFloat();
     RubyInteger convertToInteger();
     RubyString convertToString();
 
     /**
      * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
      * 
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @param raiseOnError will throw an Error if conversion does not work
      * @return the converted value
      */
     IRubyObject convertToType(String targetType, String convertMethod, boolean raiseOnError);
 
     /**
      * Higher level conversion utility similiar to convertToType but it can throw an
      * additional TypeError during conversion (MRI: rb_check_convert_type).
      * 
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @return the converted value
      */
     IRubyObject convertToTypeWithCheck(String targetType, String convertMethod);
 
 
     /**
      * RubyMethod setTaint.
      * @param b
      */
     void setTaint(boolean b);
 
     /**
      * RubyMethod checkSafeString.
      */
     void checkSafeString();
 
     /**
      * RubyMethod marshalTo.
      * @param marshalStream
      */
     void marshalTo(MarshalStream marshalStream) throws IOException;
 
     /**
      * RubyMethod convertType.
      * @param type
      * @param string
      * @param string1
      */
     IRubyObject convertType(Class type, String string, String string1);
 
     /**
      * RubyMethod dup.
      */
     IRubyObject dup();
 
     /**
      * RubyMethod setupClone.
      * @param original
      */
     void initCopy(IRubyObject original);
 
     /**
      * RubyMethod setFrozen.
      * @param b
      */
     void setFrozen(boolean b);
 
     /**
      * RubyMethod inspect.
      * @return String
      */
     IRubyObject inspect();
 
     /**
      * Make sure the arguments fit the range specified by minimum and maximum.  On
      * a failure, The Ruby runtime will generate an ArgumentError.
      * 
      * @param arguments to check
      * @param minimum number of args
      * @param maximum number of args (-1 for any number of args)
      * @return the number of arguments in args
      */
     int checkArgumentCount(IRubyObject[] arguments, int minimum, int maximum);
 
     /**
      * RubyMethod rbClone.
      * @return IRubyObject
      */
     IRubyObject rbClone();
 
 
     public void callInit(IRubyObject[] args);
 
     /**
      * RubyMethod defineSingletonMethod.
      * @param name
      * @param callback
      */
     void defineSingletonMethod(String name, Callback callback);
 
     boolean singletonMethodsAllowed();
 
 	Iterator instanceVariableNames();
 
     /**
      * rb_scan_args
      *
      * This method will take the arguments specified, fill in an array and return it filled
      * with nils for every argument not provided. It's guaranteed to always return a new array.
      * 
-     * @param runtime the JRuby runtime
      * @param args the arguments to check
      * @param required the amount of required arguments
      * @param optional the amount of optional arguments
      * @return a new array containing all arguments provided, and nils in those spots not provided.
      * 
      */
-    IRubyObject[] scanArgs(IRuby runtime, IRubyObject[] args, int required, int optional);
+    IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional);
 }
diff --git a/src/org/jruby/util/ZlibDeflate.java b/src/org/jruby/util/ZlibDeflate.java
index 32e91220ba..d2e0cfa07f 100644
--- a/src/org/jruby/util/ZlibDeflate.java
+++ b/src/org/jruby/util/ZlibDeflate.java
@@ -1,140 +1,153 @@
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
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Dave Brosius <dbrosius@mebigfatguy.com>
  * Copyright (C) 2006 Peter K Chan <peter@oaktop.com>
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
 package org.jruby.util;
 
 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 import java.util.zip.DataFormatException;
 import java.util.zip.Deflater;
 
 import org.jruby.IRuby;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class ZlibDeflate {
     private Deflater flater;
     private StringBuffer collected;
     private IRuby runtime;
 
     public final static int DEF_MEM_LEVEL = 8;
     public final static int MAX_MEM_LEVEL = 9;
 
     public final static int MAX_WBITS = 15;
 
     public final static int NO_FLUSH = 0;
     public final static int SYNC_FLUSH = 2;
     public final static int FULL_FLUSH = 3;
     public final static int FINISH = 4;
 
     public ZlibDeflate(IRubyObject caller, int level, int win_bits, int memlevel, int strategy) {
         super();
-        flater = new Deflater(level);
+        flater = new Deflater(level,true);
         flater.setStrategy(strategy);
         collected = new StringBuffer();
         runtime = caller.getRuntime();
     }
 
     public static IRubyObject s_deflate(IRubyObject caller, String str, int level) 
     	throws UnsupportedEncodingException, DataFormatException, IOException {
         ZlibDeflate zstream = new ZlibDeflate(caller, level, MAX_WBITS, DEF_MEM_LEVEL, Deflater.DEFAULT_STRATEGY);
-        IRubyObject result = zstream.deflate(str, new Long(FINISH));
+        IRubyObject result = zstream.deflate(str, FINISH);
         zstream.close();
         
         return result;
     }
 
+    public Deflater getDeflater() {
+        return flater;
+    }
+
     public void append(IRubyObject obj) throws IOException, UnsupportedEncodingException {
         append(obj.convertToString().toString());
     }
 
     public void append(String obj) throws IOException, UnsupportedEncodingException {
         collected.append(obj);
     }
 
     public void params(int level, int strategy) {
         flater.setLevel(level);
         flater.setStrategy(strategy);
     }
 
     public IRubyObject set_dictionary(IRubyObject str) throws UnsupportedEncodingException {
         flater.setDictionary(str.convertToString().toString().getBytes("ISO8859_1"));
         
         return str;
     }
 
-    public IRubyObject flush(Long flush) throws IOException {
+    public IRubyObject flush(int flush) throws IOException {
         return deflate("", flush);
     }
 
-    public IRubyObject deflate(String str, Long flush_x) throws IOException {
-        int flush = flush_x.intValue();
-        
+    public IRubyObject deflate(String str, int flush) throws IOException {
         if (null == str) {
             StringBuffer result = new StringBuffer();
             byte[] outp = new byte[1024];
             byte[] buf = collected.toString().getBytes("ISO8859_1");
             collected = new StringBuffer();
             flater.setInput(buf);
             flater.finish();
             int resultLength = -1;
             while (!flater.finished() && resultLength != 0) {
                 resultLength = flater.deflate(outp);
                 result.append(new String(outp, 0, resultLength,"ISO-8859-1"));
             }
             
             return runtime.newString(result.toString());       
         } else {
             append(str);
             if (flush == FINISH) {
                 StringBuffer result = new StringBuffer();
                 byte[] outp = new byte[1024];
                 byte[] buf = collected.toString().getBytes("ISO8859_1");
                 collected = new StringBuffer();
                 flater.setInput(buf);
                 flater.finish();
                 int resultLength = -1;
                 while (!flater.finished() && resultLength != 0) {
                     resultLength = flater.deflate(outp);
                     result.append(new String(outp, 0, resultLength,"ISO-8859-1"));
                 }
                 
                 return runtime.newString(result.toString());
             }
             
-            return runtime.getNil();
+            return runtime.newString("");
         }
     }
     
-    public void finish() {
+    public IRubyObject finish() throws Exception {
+        StringBuffer result = new StringBuffer();
+        byte[] outp = new byte[1024];
+        byte[] buf = collected.toString().getBytes("ISO8859_1");
+        collected = new StringBuffer();
+        flater.setInput(buf);
         flater.finish();
+        int resultLength = -1;
+        while (!flater.finished() && resultLength != 0) {
+            resultLength = flater.deflate(outp);
+            result.append(new String(outp, 0, resultLength,"ISO-8859-1"));
+        }
+        return runtime.newString(result.toString());
     }
     
     public void close() {
     }
 }
diff --git a/src/org/jruby/util/ZlibInflate.java b/src/org/jruby/util/ZlibInflate.java
index 3e3f670eaf..95ea7bdadf 100644
--- a/src/org/jruby/util/ZlibInflate.java
+++ b/src/org/jruby/util/ZlibInflate.java
@@ -1,105 +1,110 @@
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
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Dave Brosius <dbrosius@mebigfatguy.com>
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
 package org.jruby.util;
 
 import java.io.UnsupportedEncodingException;
 
 import java.util.zip.DataFormatException;
 import java.util.zip.Inflater;
 
 import org.jruby.IRuby;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class ZlibInflate {
     private Inflater flater;
     private StringBuffer collected;
     private IRuby runtime;
 
     public ZlibInflate(IRubyObject caller) {
         super();
-        flater = new Inflater();
+        flater = new Inflater(true);
         collected = new StringBuffer();
         runtime = caller.getRuntime();
     }
 
     public static IRubyObject s_inflate(IRubyObject caller, String str) 
     	throws UnsupportedEncodingException, DataFormatException {
         ZlibInflate zstream = new ZlibInflate(caller);
         IRubyObject result = zstream.inflate(str);
         zstream.finish();
         zstream.close();
         return result;
     }
 
+    public Inflater getInflater() {
+        return flater;
+    }
+
     public void append(IRubyObject obj) {
         append(obj.convertToString().toString());
     }
 
     public void append(String obj) {
         collected.append(obj);
     }
 
     public IRubyObject sync_point() {
         return runtime.getFalse();
     }
 
     public IRubyObject set_dictionary(IRubyObject str) throws UnsupportedEncodingException {
         flater.setDictionary(str.convertToString().toString().getBytes("ISO8859_1"));
         
         return str;
     }
 
     public IRubyObject inflate(String str) throws UnsupportedEncodingException, DataFormatException {
         if (null != str) {
             append(str);
         }
         StringBuffer result = new StringBuffer();
         byte[] outp = new byte[1024];
         byte[] buf = collected.toString().getBytes("ISO8859_1");
         collected = new StringBuffer();
         flater.setInput(buf);
         int resultLength = -1;
         while (!flater.finished() && resultLength != 0) {
             resultLength = flater.inflate(outp);
             result.append(new String(outp, 0, resultLength, "ISO8859_1"));
         }
         return runtime.newString(result.toString());       
     }
 
     public IRubyObject sync(IRubyObject str) {
         append(str);
         return runtime.getFalse();
     }
 
     public void finish() {
+        flater.end();
     }
     
     public void close() {
     }
 }
