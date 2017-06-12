diff --git a/core/src/main/java/org/jruby/RubySymbol.java b/core/src/main/java/org/jruby/RubySymbol.java
index bb354b4d17..f892f8791b 100644
--- a/core/src/main/java/org/jruby/RubySymbol.java
+++ b/core/src/main/java/org/jruby/RubySymbol.java
@@ -1,889 +1,909 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Derek Berner <derek.berner@state.nm.us>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jcodings.Encoding;
 import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.compiler.Constantizable;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Block.Type;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ContextAwareBlockBody;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.FunctionalCachingCallSite;
 import org.jruby.runtime.encoding.MarshalEncoding;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.runtime.opto.OptoFactory;
 import org.jruby.util.ByteList;
 import org.jruby.util.PerlHash;
 import org.jruby.util.SipHashInline;
 
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.ReentrantLock;
 
 import static org.jruby.util.StringSupport.codeLength;
 import static org.jruby.util.StringSupport.codePoint;
 
 /**
  * Represents a Ruby symbol (e.g. :bar)
  */
 @JRubyClass(name="Symbol")
 public class RubySymbol extends RubyObject implements MarshalEncoding, Constantizable {
     public static final long symbolHashSeedK0 = 5238926673095087190l;
 
     private final String symbol;
     private final int id;
     private final ByteList symbolBytes;
     private final int hashCode;
     private Object constant;
     
     /**
      * 
      * @param runtime
      * @param internedSymbol the String value of the new Symbol. This <em>must</em>
      *                       have been previously interned
      */
     private RubySymbol(Ruby runtime, String internedSymbol, ByteList symbolBytes) {
         super(runtime, runtime.getSymbol(), false);
         // symbol string *must* be interned
 
         //        assert internedSymbol == internedSymbol.intern() : internedSymbol + " is not interned";
 
         this.symbol = internedSymbol;
         this.symbolBytes = symbolBytes;
         this.id = runtime.allocSymbolId();
 
         long hash = runtime.isSiphashEnabled() ? SipHashInline.hash24(
                 symbolHashSeedK0, 0, symbolBytes.getUnsafeBytes(),
                 symbolBytes.getBegin(), symbolBytes.getRealSize()) :
                 PerlHash.hash(symbolHashSeedK0, symbolBytes.getUnsafeBytes(),
                 symbolBytes.getBegin(), symbolBytes.getRealSize());
         this.hashCode = (int) hash;
         setFrozen(true);
     }
 
     private RubySymbol(Ruby runtime, String internedSymbol) {
         this(runtime, internedSymbol, symbolBytesFromString(runtime, internedSymbol));
     }
 
     public static RubyClass createSymbolClass(Ruby runtime) {
         RubyClass symbolClass = runtime.defineClass("Symbol", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setSymbol(symbolClass);
         RubyClass symbolMetaClass = symbolClass.getMetaClass();
         symbolClass.setClassIndex(ClassIndex.SYMBOL);
         symbolClass.setReifiedClass(RubySymbol.class);
         symbolClass.kindOf = new RubyModule.JavaClassKindOf(RubySymbol.class);
 
         symbolClass.defineAnnotatedMethods(RubySymbol.class);
         symbolMetaClass.undefineMethod("new");
 
         symbolClass.includeModule(runtime.getComparable());
         
         return symbolClass;
     }
     
     @Override
     public ClassIndex getNativeClassIndex() {
         return ClassIndex.SYMBOL;
     }
 
     /** rb_to_id
      * 
      * @return a String representation of the symbol 
      */
     @Override
     public String asJavaString() {
         return symbol;
     }
 
     @Override
     public String toString() {
         return symbol;
     }
 
     final ByteList getBytes() {
         return symbolBytes;
     }
 
+    /**
+     * RubySymbol is created by passing in a String and bytes are extracted from that.  We will
+     * pass in encoding of that string after construction but before use so it does not forget
+     * what it is.
+     */
+    public void associateEncoding(Encoding encoding) {
+        symbolBytes.setEncoding(encoding);
+    }
+
     /** short circuit for Symbol key comparison
      * 
      */
     @Override
     public final boolean eql(IRubyObject other) {
         return other == this;
     }
 
     @Override
     public boolean isImmediate() {
     	return true;
     }
 
     @Override
     public RubyClass getSingletonClass() {
         throw getRuntime().newTypeError("can't define singleton");
     }
 
     public static RubySymbol getSymbolLong(Ruby runtime, long id) {
         return runtime.getSymbolTable().lookup(id);
     }
     
     /* Symbol class methods.
      * 
      */
     
     public static RubySymbol newSymbol(Ruby runtime, IRubyObject name) {
         if (!(name instanceof RubyString)) return newSymbol(runtime, name.asJavaString());
         
         return runtime.getSymbolTable().getSymbol(((RubyString) name).getByteList());
     }
 
     public static RubySymbol newSymbol(Ruby runtime, String name) {
         return runtime.getSymbolTable().getSymbol(name);
     }
 
+    // FIXME: same bytesequences will fight over encoding of the symbol once cached.  I think largely
+    // this will only happen in some ISO_8859_?? encodings making symbols at the same time so it should
+    // be pretty rare.
+    public static RubySymbol newSymbol(Ruby runtime, String name, Encoding encoding) {
+        RubySymbol newSymbol = newSymbol(runtime, name);
+
+        newSymbol.associateEncoding(encoding);
+
+        return newSymbol;
+    }
+
     /**
      * @see org.jruby.compiler.Constantizable
      */
     @Override
     public Object constant() {
         return constant == null ?
                 constant = OptoFactory.newConstantWrapper(IRubyObject.class, this) :
                 constant;
     }
 
     @Deprecated
     @Override
     public IRubyObject inspect() {
         return inspect19(getRuntime().getCurrentContext());
     }
 
     public IRubyObject inspect(ThreadContext context) {
         return inspect19(context.runtime);
     }
 
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect19(ThreadContext context) {
         return inspect19(context.runtime);
     }
 
     private final IRubyObject inspect19(Ruby runtime) {
         ByteList result = new ByteList(symbolBytes.getRealSize() + 1);
         result.setEncoding(symbolBytes.getEncoding());
         result.append((byte)':');
         result.append(symbolBytes);
 
         RubyString str = RubyString.newString(runtime, result); 
         // TODO: 1.9 rb_enc_symname_p
         if (isPrintable() && isSymbolName19(symbol)) return str;
             
         str = (RubyString)str.inspect19();
         ByteList bytes = str.getByteList();
         bytes.set(0, ':');
         bytes.set(1, '"');
         
         return str;
     }
 
     @Override
     public IRubyObject to_s() {
         return to_s(getRuntime());
     }
     
     @JRubyMethod
     public IRubyObject to_s(ThreadContext context) {
         return to_s(context.runtime);
     }
     
     private final IRubyObject to_s(Ruby runtime) {
         return RubyString.newStringShared(runtime, symbolBytes);
     }
 
     public IRubyObject id2name() {
         return to_s(getRuntime());
     }
     
     @JRubyMethod
     public IRubyObject id2name(ThreadContext context) {
         return to_s(context);
     }
 
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return super.op_equal(context, other);
     }
 
     @Deprecated
     @Override
     public RubyFixnum hash() {
         return getRuntime().newFixnum(hashCode());
     }
 
     @JRubyMethod
     public RubyFixnum hash(ThreadContext context) {
         return context.runtime.newFixnum(hashCode());
     }
     
     @Override
     public int hashCode() {
         return hashCode;
     }
 
     public int getId() {
         return id;
     }
     
     @Override
     public boolean equals(Object other) {
         return other == this;
     }
     
     @JRubyMethod(name = "to_sym")
     public IRubyObject to_sym() {
         return this;
     }
 
     @JRubyMethod(name = "intern")
     public IRubyObject to_sym19() {
         return this;
     }
 
     @Override
     public IRubyObject taint(ThreadContext context) {
         return this;
     }
 
     private RubyString newShared(Ruby runtime) {
         return RubyString.newStringShared(runtime, symbolBytes);
     }
 
     private RubyString rubyStringFromString(Ruby runtime) {
         return RubyString.newString(runtime, symbol);
     }
 
     @JRubyMethod(name = {"succ", "next"})
     public IRubyObject succ(ThreadContext context) {
         Ruby runtime = context.runtime;
         return newSymbol(runtime, newShared(runtime).succ19(context).toString());
     }
 
     @JRubyMethod(name = "<=>")
     @Override
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         
         return !(other instanceof RubySymbol) ? runtime.getNil() :
                 newShared(runtime).op_cmp(context, ((RubySymbol)other).newShared(runtime));
     }
 
     @JRubyMethod
     public IRubyObject casecmp(ThreadContext context, IRubyObject other) {
         Ruby runtime = context.runtime;
         
         return !(other instanceof RubySymbol) ? runtime.getNil() :
                 newShared(runtime).casecmp19(context, ((RubySymbol) other).newShared(runtime));
     }
 
     @JRubyMethod(name = {"=~", "match"})
     @Override
     public IRubyObject op_match19(ThreadContext context, IRubyObject other) {
         return newShared(context.runtime).op_match19(context, other);
     }
 
     @JRubyMethod(name = {"[]", "slice"})
     public IRubyObject op_aref(ThreadContext context, IRubyObject arg) {
         return newShared(context.runtime).op_aref19(context, arg);
     }
 
     @JRubyMethod(name = {"[]", "slice"})
     public IRubyObject op_aref(ThreadContext context, IRubyObject arg1, IRubyObject arg2) {
         return newShared(context.runtime).op_aref19(context, arg1, arg2);
     }
 
     @JRubyMethod(name = {"length", "size"})
     public IRubyObject length() {
         return newShared(getRuntime()).length19();
     }
 
     @JRubyMethod(name = "empty?")
     public IRubyObject empty_p(ThreadContext context) {
         return newShared(context.runtime).empty_p(context);
     }
 
     @JRubyMethod
     public IRubyObject upcase(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         return newSymbol(runtime, rubyStringFromString(runtime).upcase19(context).toString());
     }
 
     @JRubyMethod
     public IRubyObject downcase(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         return newSymbol(runtime, rubyStringFromString(runtime).downcase19(context).toString());
     }
 
     @JRubyMethod
     public IRubyObject capitalize(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         return newSymbol(runtime, rubyStringFromString(runtime).capitalize19(context).toString());
     }
 
     @JRubyMethod
     public IRubyObject swapcase(ThreadContext context) {
         Ruby runtime = context.runtime;
         
         return newSymbol(runtime, rubyStringFromString(runtime).swapcase19(context).toString());
     }
 
     @JRubyMethod
     public IRubyObject encoding(ThreadContext context) {
         return context.runtime.getEncodingService().getEncoding(symbolBytes.getEncoding());
     }
     
     @JRubyMethod
     public IRubyObject to_proc(ThreadContext context) {
         StaticScope scope = context.runtime.getStaticScopeFactory().getDummyScope();
         final CallSite site = new FunctionalCachingCallSite(symbol);
         BlockBody body = new ContextAwareBlockBody(scope, Arity.OPTIONAL, BlockBody.SINGLE_RESTARG) {
             private IRubyObject yieldInner(ThreadContext context, RubyArray array, Block block) {
                 if (array.isEmpty()) {
                     throw context.runtime.newArgumentError("no receiver given");
                 }
 
                 IRubyObject self = array.shift(context);
 
                 return site.call(context, self, self, array.toJavaArray(), block);
             }
 
             @Override
             public IRubyObject yield(ThreadContext context, IRubyObject[] args, IRubyObject self,
                                      Binding binding, Type type, Block block) {
                 RubyProc.prepareArgs(context, type, block.arity(), args);
                 return yieldInner(context, context.runtime.newArrayNoCopyLight(args), block);
             }
 
             @Override
             public IRubyObject yield(ThreadContext context, IRubyObject value,
                     Binding binding, Block.Type type, Block block) {
                 return yieldInner(context, ArgsUtil.convertToRubyArray(context.runtime, value, false), block);
             }
             
             @Override
             protected IRubyObject doYield(ThreadContext context, IRubyObject value, Binding binding, Type type) {
                 return yieldInner(context, ArgsUtil.convertToRubyArray(context.runtime, value, false), Block.NULL_BLOCK);
             }
 
             @Override
             protected IRubyObject doYield(ThreadContext context, IRubyObject[] args, IRubyObject self, Binding binding, Type type) {
                 return yieldInner(context, context.runtime.newArrayNoCopyLight(args), Block.NULL_BLOCK);
             }
 
             @Override
             public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
                 return site.call(context, arg0, arg0);
             }
 
             @Override
             public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
                 return site.call(context, arg0, arg0, arg1);
             }
 
             @Override
             public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
                 return site.call(context, arg0, arg0, arg1, arg2);
             }
 
             @Override
             public String getFile() {
                 return symbol;
             }
 
             @Override
             public int getLine() {
                 return -1;
             }
         };
 
         return RubyProc.newProc(context.runtime,
                                 new Block(body, context.currentBinding()),
                                 Block.Type.PROC);
     }
     
     private static boolean isIdentStart(char c) {
         return ((c >= 'a' && c <= 'z')|| (c >= 'A' && c <= 'Z') || c == '_');
     }
     
     private static boolean isIdentChar(char c) {
         return ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_');
     }
     
     private static boolean isIdentifier(String s) {
         if (s == null || s.length() <= 0 || !isIdentStart(s.charAt(0))) return false;
 
         for (int i = 1; i < s.length(); i++) {
             if (!isIdentChar(s.charAt(i))) return false;
         }
         
         return true;
     }
     
     /**
      * is_special_global_name from parse.c.  
      * @param s
      * @return
      */
     private static boolean isSpecialGlobalName(String s) {
         if (s == null || s.length() <= 0) return false;
 
         int length = s.length();
            
         switch (s.charAt(0)) {        
         case '~': case '*': case '$': case '?': case '!': case '@': case '/': case '\\':        
         case ';': case ',': case '.': case '=': case ':': case '<': case '>': case '\"':        
         case '&': case '`': case '\'': case '+': case '0':
             return length == 1;            
         case '-':
             return (length == 1 || (length == 2 && isIdentChar(s.charAt(1))));
             
         default:
             for (int i = 0; i < length; i++) {
                 if (!Character.isDigit(s.charAt(i))) return false;
             }
         }
         
         return true;
     }
 
     private boolean isPrintable() {
         Ruby runtime = getRuntime();
         int p = symbolBytes.getBegin();
         int end = p + symbolBytes.getRealSize();
         byte[]bytes = symbolBytes.getUnsafeBytes();
         Encoding enc = symbolBytes.getEncoding();
 
         while (p < end) {
             int c = codePoint(runtime, enc, bytes, p, end);
             
             if (!enc.isPrint(c)) return false;
             
             p += codeLength(runtime, enc, c);
         }
         
         return true;
     }
 
     private static boolean isSymbolName19(String s) {
         if (s == null || s.length() < 1) return false;
 
         int length = s.length();
         char c = s.charAt(0);
         
         return isSymbolNameCommon(s, c, length) || 
                 (c == '!' && (length == 1 ||
                              (length == 2 && (s.charAt(1) == '~' || s.charAt(1) == '=')))) ||
                 isSymbolLocal(s, c, length);
     }
 
     private static boolean isSymbolNameCommon(String s, char c, int length) {        
         switch (c) {
         case '$':
             if (length > 1 && isSpecialGlobalName(s.substring(1))) return true;
 
             return isIdentifier(s.substring(1));
         case '@':
             int offset = 1;
             if (length >= 2 && s.charAt(1) == '@') offset++;
 
             return isIdentifier(s.substring(offset));
         case '<':
             return (length == 1 || (length == 2 && (s.equals("<<") || s.equals("<="))) ||
                     (length == 3 && s.equals("<=>")));
         case '>':
             return (length == 1) || (length == 2 && (s.equals(">>") || s.equals(">=")));
         case '=':
             return ((length == 2 && (s.equals("==") || s.equals("=~"))) ||
                     (length == 3 && s.equals("===")));
         case '*':
             return (length == 1 || (length == 2 && s.equals("**")));
         case '+':
             return (length == 1 || (length == 2 && s.equals("+@")));
         case '-':
             return (length == 1 || (length == 2 && s.equals("-@")));
         case '|': case '^': case '&': case '/': case '%': case '~': case '`':
             return length == 1;
         case '[':
             return s.equals("[]") || s.equals("[]=");
         }
         return false;
     }
 
     private static boolean isSymbolLocal(String s, char c, int length) {
         if (!isIdentStart(c)) return false;
 
         boolean localID = (c >= 'a' && c <= 'z');
         int last = 1;
 
         for (; last < length; last++) {
             char d = s.charAt(last);
 
             if (!isIdentChar(d)) break;
         }
 
         if (last == length) return true;
         if (localID && last == length - 1) {
             char d = s.charAt(last);
 
             return d == '!' || d == '?' || d == '=';
         }
 
         return false;
     }
     
     @JRubyMethod(meta = true)
     public static IRubyObject all_symbols(ThreadContext context, IRubyObject recv) {
         return context.runtime.getSymbolTable().all_symbols();
     }
     @Deprecated
     public static IRubyObject all_symbols(IRubyObject recv) {
         return recv.getRuntime().getSymbolTable().all_symbols();
     }
 
     public static RubySymbol unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubySymbol result = newSymbol(input.getRuntime(), RubyString.byteListToString(input.unmarshalString()));
         
         input.registerLinkTarget(result);
         
         return result;
     }
 
     @Override
     public Object toJava(Class target) {
         if (target == String.class || target == CharSequence.class) return symbol;
 
         return super.toJava(target);
     }
 
     public static ByteList symbolBytesFromString(Ruby runtime, String internedSymbol) {
         return new ByteList(ByteList.plain(internedSymbol), USASCIIEncoding.INSTANCE, false);
     }
 
     public static final class SymbolTable {
         static final int DEFAULT_INITIAL_CAPACITY = 2048; // *must* be power of 2!
         static final int MAXIMUM_CAPACITY = 1 << 30;
         static final float DEFAULT_LOAD_FACTOR = 0.75f;
         
         private final ReentrantLock tableLock = new ReentrantLock();
         private volatile SymbolEntry[] symbolTable;
         private final ConcurrentHashMap<ByteList, RubySymbol> bytelistTable = new ConcurrentHashMap<ByteList, RubySymbol>(100, 0.75f, Runtime.getRuntime().availableProcessors());
         private int size;
         private int threshold;
         private final float loadFactor;
         private final Ruby runtime;
         
         public SymbolTable(Ruby runtime) {
             this.runtime = runtime;
             this.loadFactor = DEFAULT_LOAD_FACTOR;
             this.threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
             this.symbolTable = new SymbolEntry[DEFAULT_INITIAL_CAPACITY];
         }
         
         // note all fields are final -- rehash creates new entries when necessary.
         // as documented in java.util.concurrent.ConcurrentHashMap.java, that will
         // statistically affect only a small percentage (< 20%) of entries for a given rehash.
         static class SymbolEntry {
             final int hash;
             final String name;
             final RubySymbol symbol;
             final SymbolEntry next;
             
             SymbolEntry(int hash, String name, RubySymbol symbol, SymbolEntry next) {
                 this.hash = hash;
                 this.name = name;
                 this.symbol = symbol;
                 this.next = next;
             }
         }
 
         public RubySymbol getSymbol(String name) {
             int hash = name.hashCode();
             SymbolEntry[] table = symbolTable;
             
             for (SymbolEntry e = getEntryFromTable(table, hash); e != null; e = e.next) {
                 if (isSymbolMatch(name, hash, e)) return e.symbol;
             }
             
             return createSymbol(name, symbolBytesFromString(runtime, name), hash, table);
         }
 
         public RubySymbol getSymbol(ByteList bytes) {
             RubySymbol symbol = bytelistTable.get(bytes);
             if (symbol != null) return symbol;
 
             String name = bytes.toString();
             int hash = name.hashCode();
             SymbolEntry[] table = symbolTable;
             
             for (SymbolEntry e = getEntryFromTable(table, hash); e != null; e = e.next) {
                 if (isSymbolMatch(name, hash, e)) {
                     symbol = e.symbol;
                     break;
                 }
             }
 
             if (symbol == null) {
                 symbol = createSymbol(name, bytes, hash, table);
             }
             
             bytelistTable.put(bytes, symbol);
 
             return symbol;
         }
 
         public RubySymbol fastGetSymbol(String internedName) {
             SymbolEntry[] table = symbolTable;
             
             for (SymbolEntry e = getEntryFromTable(symbolTable, internedName.hashCode()); e != null; e = e.next) {
                 if (isSymbolMatch(internedName, e)) return e.symbol;
             }
             
             return fastCreateSymbol(internedName, table);
         }
 
         private static SymbolEntry getEntryFromTable(SymbolEntry[] table, int hash) {
             return table[hash & (table.length - 1)];
         }
 
         private static boolean isSymbolMatch(String name, int hash, SymbolEntry entry) {
             return hash == entry.hash && name.equals(entry.name);
         }
 
         private static boolean isSymbolMatch(String internedName, SymbolEntry entry) {
             return internedName == entry.name;
         }
 
         private RubySymbol createSymbol(String name, ByteList value, int hash, SymbolEntry[] table) {
             ReentrantLock lock;
             (lock = tableLock).lock();
             try {
                 int index;                
                 int potentialNewSize = size + 1;
                 
                 table = potentialNewSize > threshold ? rehash() : symbolTable;
 
                 // try lookup again under lock
                 for (SymbolEntry e = table[index = hash & (table.length - 1)]; e != null; e = e.next) {
                     if (hash == e.hash && name.equals(e.name)) return e.symbol;
                 }
                 String internedName = name.intern();
                 RubySymbol symbol = new RubySymbol(runtime, internedName, value);
                 table[index] = new SymbolEntry(hash, internedName, symbol, table[index]);
                 size = potentialNewSize;
                 // write-volatile
                 symbolTable = table;
                 return symbol;
             } finally {
                 lock.unlock();
             }
         }
 
         private RubySymbol fastCreateSymbol(String internedName, SymbolEntry[] table) {
             ReentrantLock lock;
             (lock = tableLock).lock();
             try {
                 int index;
                 int hash;
                 int potentialNewSize = size + 1;
                 
                 table = potentialNewSize > threshold ? rehash() : symbolTable;
 
                 // try lookup again under lock
                 for (SymbolEntry e = table[index = (hash = internedName.hashCode()) & (table.length - 1)]; e != null; e = e.next) {
                     if (internedName == e.name) return e.symbol;
                 }
                 RubySymbol symbol = new RubySymbol(runtime, internedName);
                 table[index] = new SymbolEntry(hash, internedName, symbol, table[index]);
                 size = potentialNewSize;
                 // write-volatile
                 symbolTable = table;
                 return symbol;
             } finally {
                 lock.unlock();
             }
         }
         
         // backwards-compatibility, but threadsafe now
         public RubySymbol lookup(String name) {
             int hash = name.hashCode();
             SymbolEntry[] table;
             
             for (SymbolEntry e = (table = symbolTable)[hash & (table.length - 1)]; e != null; e = e.next) {
                 if (hash == e.hash && name.equals(e.name)) return e.symbol;
             }
 
             return null;
         }
         
         public RubySymbol lookup(long id) {
             SymbolEntry[] table = symbolTable;
             
             for (int i = table.length; --i >= 0; ) {
                 for (SymbolEntry e = table[i]; e != null; e = e.next) {
                     if (id == e.symbol.id) return e.symbol;
                 }
             }
 
             return null;
         }
         
         public RubyArray all_symbols() {
             SymbolEntry[] table = this.symbolTable;
             RubyArray array = runtime.newArray(this.size);
             
             for (int i = table.length; --i >= 0; ) {
                 for (SymbolEntry e = table[i]; e != null; e = e.next) {
                     array.append(e.symbol);
                 }
             }
             return array;
         }
         
         // not so backwards-compatible here, but no one should have been
         // calling this anyway.
         @Deprecated
         public void store(RubySymbol symbol) {
             throw new UnsupportedOperationException();
         }
         
         private SymbolEntry[] rehash() {
             SymbolEntry[] oldTable = symbolTable;
             int oldCapacity = oldTable.length;
             
             if (oldCapacity >= MAXIMUM_CAPACITY) return oldTable;
             
             int newCapacity = oldCapacity << 1;
             SymbolEntry[] newTable = new SymbolEntry[newCapacity];
             threshold = (int)(newCapacity * loadFactor);
             int sizeMask = newCapacity - 1;
             SymbolEntry e;
             for (int i = oldCapacity; --i >= 0; ) {
                 // We need to guarantee that any existing reads of old Map can
                 //  proceed. So we cannot yet null out each bin.
                 e = oldTable[i];
 
                 if (e != null) {
                     SymbolEntry next = e.next;
                     int idx = e.hash & sizeMask;
 
                     //  Single node on list
                     if (next == null) {
                         newTable[idx] = e;
                     } else {
                         // Reuse trailing consecutive sequence at same slot
                         SymbolEntry lastRun = e;
                         int lastIdx = idx;
                         for (SymbolEntry last = next;
                              last != null;
                              last = last.next) {
                             int k = last.hash & sizeMask;
                             if (k != lastIdx) {
                                 lastIdx = k;
                                 lastRun = last;
                             }
                         }
                         newTable[lastIdx] = lastRun;
 
                         // Clone all remaining nodes
                         for (SymbolEntry p = e; p != lastRun; p = p.next) {
                             int k = p.hash & sizeMask;
                             SymbolEntry n = newTable[k];
                             newTable[k] = new SymbolEntry(p.hash, p.name, p.symbol, n);
                         }
                     }
                 }
             }
             symbolTable = newTable;
             return newTable;
         }
     }
 
     @Override
     public boolean shouldMarshalEncoding() {
         return getMarshalEncoding() != USASCIIEncoding.INSTANCE;
     }
 
     @Override
     public Encoding getMarshalEncoding() {
         return symbolBytes.getEncoding();
     }
     
     /**
      * Properly stringify an object for the current "raw bytes" representation
      * of a symbol.
      * 
      * Symbols are represented internally as a Java string, but decoded using
      * raw bytes in ISO-8859-1 representation. This means they do not in their
      * normal String form represent a readable Java string, but it does allow
      * differently-encoded strings to map to different symbol objects.
      * 
      * See #736
      * 
      * @param object the object to symbolify
      * @return the symbol string associated with the object's string representation
      */
     public static String objectToSymbolString(IRubyObject object) {
         if (object instanceof RubySymbol) {
             return ((RubySymbol)object).toString();
         } else if (object instanceof RubyString) {
             return ((RubyString)object).getByteList().toString();
         } else {
             return object.convertToString().getByteList().toString();
         }
     }
 }
diff --git a/core/src/main/java/org/jruby/ast/SymbolNode.java b/core/src/main/java/org/jruby/ast/SymbolNode.java
index 95f900f86a..6bef154e1d 100644
--- a/core/src/main/java/org/jruby/ast/SymbolNode.java
+++ b/core/src/main/java/org/jruby/ast/SymbolNode.java
@@ -1,71 +1,80 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.ast;
 
 import java.util.List;
+import org.jcodings.Encoding;
+import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.ast.visitor.NodeVisitor;
 import org.jruby.lexer.yacc.ISourcePosition;
+import org.jruby.util.ByteList;
 
 /** 
  * Represents a symbol (:symbol_name).
  */
 public class SymbolNode extends Node implements ILiteralNode, INameNode {
-    private String name;
+    private final String name;
+    private final Encoding encoding;
 
-    public SymbolNode(ISourcePosition position, String name) {
+    public SymbolNode(ISourcePosition position, ByteList value) {
 	    super(position);
-	    this.name = name;
+	    this.name = value.toString().intern();
+        encoding = value.lengthEnc() != value.length() ? value.getEncoding() : USASCIIEncoding.INSTANCE;
     }
 
     public NodeType getNodeType() {
         return NodeType.SYMBOLNODE;
     }
 
     public <T> T accept(NodeVisitor<T> iVisitor) {
         return iVisitor.visitSymbolNode(this);
     }
 
     /**
      * Gets the name.
      * @return Returns a String
      */
     public String getName() {
         return name;
     }
+
+    public Encoding getEncoding() {
+        return encoding;
+    }
     
     public List<Node> childNodes() {
         return EMPTY_LIST;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/IRBuilder.java b/core/src/main/java/org/jruby/ir/IRBuilder.java
index 0e403435d9..991f985e6f 100644
--- a/core/src/main/java/org/jruby/ir/IRBuilder.java
+++ b/core/src/main/java/org/jruby/ir/IRBuilder.java
@@ -1,3534 +1,3537 @@
 package org.jruby.ir;
 
+import org.jcodings.specific.ASCIIEncoding;
 import org.jruby.EvalType;
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.*;
 import org.jruby.ast.types.INameNode;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.internal.runtime.methods.IRMethodArgs;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.instructions.defined.GetErrorInfoInstr;
 import org.jruby.ir.instructions.defined.RestoreErrorInfoInstr;
 import org.jruby.ir.listeners.IRScopeListener;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 import org.jruby.ir.operands.Float;
 import org.jruby.ir.transformations.inlining.SimpleCloneInfo;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.util.ByteList;
 import org.jruby.util.KeyValuePair;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.util.*;
 
 import static org.jruby.ir.instructions.RuntimeHelperCall.Methods.*;
 
 // This class converts an AST into a bunch of IR instructions
 
 // IR Building Notes
 // -----------------
 //
 // 1. More copy instructions added than necessary
 // ----------------------------------------------
 // Note that in general, there will be lots of a = b kind of copies
 // introduced in the IR because the translation is entirely single-node focused.
 // An example will make this clear
 //
 // RUBY:
 //     v = @f
 // will translate to
 //
 // AST:
 //     LocalAsgnNode v
 //       InstrVarNode f
 // will translate to
 //
 // IR:
 //     tmp = self.f [ GET_FIELD(tmp,self,f) ]
 //     v = tmp      [ COPY(v, tmp) ]
 //
 // instead of
 //     v = self.f   [ GET_FIELD(v, self, f) ]
 //
 // We could get smarter and pass in the variable into which this expression is going to get evaluated
 // and use that to store the value of the expression (or not build the expression if the variable is null).
 //
 // But, that makes the code more complicated, and in any case, all this will get fixed in a single pass of
 // copy propagation and dead-code elimination.
 //
 // Something to pay attention to and if this extra pass becomes a concern (not convinced that it is yet),
 // this smart can be built in here.  Right now, the goal is to do something simple and straightforward that is going to be correct.
 //
 // 2. Returning null vs manager.getNil()
 // ----------------------------
 // - We should be returning null from the build methods where it is a normal "error" condition
 // - We should be returning manager.getNil() where the actual return value of a build is the ruby nil operand
 //   Look in buildIf for an example of this
 //
 // 3. Temporary variable reuse
 // ---------------------------
 // I am reusing variables a lot in places in this code.  Should I instead always get a new variable when I need it
 // This introduces artificial data dependencies, but fewer variables.  But, if we are going to implement SSA pass
 // this is not a big deal.  Think this through!
 
 public class IRBuilder {
     static final Operand[] NO_ARGS = new Operand[]{};
     static final UnexecutableNil U_NIL = UnexecutableNil.U_NIL;
 
     public static IRBuilder createIRBuilder(Ruby runtime, IRManager manager) {
         return new IRBuilder(manager);
     }
 
     public static Node buildAST(boolean isCommandLineScript, String arg) {
         Ruby ruby = Ruby.getGlobalRuntime();
 
         // inline script
         if (isCommandLineScript) return ruby.parse(ByteList.create(arg), "-e", null, 0, false);
 
         // from file
         FileInputStream fis = null;
         try {
             File file = new File(arg);
             fis = new FileInputStream(file);
             long size = file.length();
             byte[] bytes = new byte[(int)size];
             fis.read(bytes);
             System.out.println("-- processing " + arg + " --");
             return ruby.parse(new ByteList(bytes), arg, null, 0, false);
         } catch (IOException ioe) {
             throw new RuntimeException(ioe);
         } finally {
             try { if (fis != null) fis.close(); } catch(Exception ignored) { }
         }
     }
 
     private static class IRLoop {
         public final IRScope  container;
         public final IRLoop   parentLoop;
         public final Label    loopStartLabel;
         public final Label    loopEndLabel;
         public final Label    iterStartLabel;
         public final Label    iterEndLabel;
         public final Variable loopResult;
 
         public IRLoop(IRScope s, IRLoop outerLoop) {
             container = s;
             parentLoop = outerLoop;
             loopStartLabel = s.getNewLabel("_LOOP_BEGIN");
             loopEndLabel   = s.getNewLabel("_LOOP_END");
             iterStartLabel = s.getNewLabel("_ITER_BEGIN");
             iterEndLabel   = s.getNewLabel("_ITER_END");
             loopResult     = s.createTemporaryVariable();
             s.setHasLoopsFlag();
         }
     }
 
     private static class RescueBlockInfo {
         RescueNode rescueNode;             // Rescue node for which we are tracking info
         Label      entryLabel;             // Entry of the rescue block
         Variable   savedExceptionVariable; // Variable that contains the saved $! variable
         IRLoop     innermostLoop;          // Innermost loop within which this rescue block is nested, if any
 
         public RescueBlockInfo(RescueNode n, Label l, Variable v, IRLoop loop) {
             rescueNode = n;
             entryLabel = l;
             savedExceptionVariable = v;
             innermostLoop = loop;
         }
 
         public void restoreException(IRBuilder b, IRScope s, IRLoop currLoop) {
             if (currLoop == innermostLoop) b.addInstr(s, new PutGlobalVarInstr("$!", savedExceptionVariable));
         }
     }
 
     /* -----------------------------------------------------------------------------------
      * Every ensure block has a start label and end label
      *
      * This ruby code will translate to the IR shown below
      * -----------------
      *   begin
      *       ... protected body ...
      *   ensure
      *       ... ensure block to run
      *   end
      * -----------------
      *  L_region_start
      *     IR instructions for the protected body
      *     .. copy of ensure block IR ..
      *  L_dummy_rescue:
      *     e = recv_exc
      *  L_start:
      *     .. ensure block IR ..
      *     throw e
      *  L_end:
      * -----------------
      *
      * If N is a node in the protected body that might exit this scope (exception rethrows
      * and returns), N has to first run the ensure block before exiting.
      *
      * Since we can have a nesting of ensure blocks, we are maintaining a stack of these
      * well-nested ensure blocks.  Every node N that will exit this scope will have to
      * run the stack of ensure blocks in the right order.
      * ----------------------------------------------------------------------------------- */
     private static class EnsureBlockInfo {
         Label    regionStart;
         Label    start;
         Label    end;
         Label    dummyRescueBlockLabel;
         Variable savedGlobalException;
 
         // Label of block that will rescue exceptions raised by ensure code
         Label    bodyRescuer;
 
         // Innermost loop within which this ensure block is nested, if any
         IRLoop   innermostLoop;
 
         // AST node for any associated rescue node in the case of begin-rescue-ensure-end block
         // Will be null in the case of begin-ensure-end block
         RescueNode matchingRescueNode;
 
         // This ensure block's instructions
         List<Instr> instrs;
 
         public EnsureBlockInfo(IRScope s, RescueNode n, IRLoop l, Label bodyRescuer) {
             regionStart = s.getNewLabel();
             start       = s.getNewLabel();
             end         = s.getNewLabel();
             dummyRescueBlockLabel = s.getNewLabel();
             instrs = new ArrayList<>();
             savedGlobalException = null;
             innermostLoop = l;
             matchingRescueNode = n;
             this.bodyRescuer = bodyRescuer;
         }
 
         public void addInstr(Instr i) {
             instrs.add(i);
         }
 
         public void addInstrAtBeginning(Instr i) {
             instrs.add(0, i);
         }
 
         public void emitBody(IRBuilder b, IRScope s) {
             b.addInstr(s, new LabelInstr(start));
             for (Instr i: instrs) {
                 b.addInstr(s, i);
             }
         }
 
         public void cloneIntoHostScope(IRBuilder b, IRScope s) {
             SimpleCloneInfo ii = new SimpleCloneInfo(s, true);
 
             // Clone required labels.
             // During normal cloning below, labels not found in the rename map
             // are not cloned.
             ii.renameLabel(start);
             for (Instr i: instrs) {
                 if (i instanceof LabelInstr) {
                     ii.renameLabel(((LabelInstr)i).label);
                 }
             }
 
             // Clone instructions now
             b.addInstr(s, new LabelInstr(ii.getRenamedLabel(start)));
             b.addInstr(s, new ExceptionRegionStartMarkerInstr(bodyRescuer));
             for (Instr i: instrs) {
                 Instr clonedInstr = i.clone(ii);
                 if (clonedInstr instanceof CallBase) {
                     CallBase call = (CallBase)clonedInstr;
                     Operand block = call.getClosureArg(null);
                     if (block instanceof WrappedIRClosure) s.addClosure(((WrappedIRClosure)block).getClosure());
                 }
                 b.addInstr(s, clonedInstr);
             }
             b.addInstr(s, new ExceptionRegionEndMarkerInstr());
         }
     }
 
     // Stack of nested rescue blocks -- this just tracks the start label of the blocks
     private Stack<RescueBlockInfo> activeRescueBlockStack = new Stack<>();
 
     // Stack of ensure blocks that are currently active
     private Stack<EnsureBlockInfo> activeEnsureBlockStack = new Stack<>();
 
     // Stack of ensure blocks whose bodies are being constructed
     private Stack<EnsureBlockInfo> ensureBodyBuildStack   = new Stack<>();
 
     // Combined stack of active rescue/ensure nestings -- required to properly set up
     // rescuers for ensure block bodies cloned into other regions -- those bodies are
     // rescued by the active rescuers at the point of definition rather than the point
     // of cloning.
     private Stack<Label> activeRescuers = new Stack<>();
 
     private int _lastProcessedLineNum = -1;
 
     // Since we are processing ASTs, loop bodies are processed in depth-first manner
     // with outer loops encountered before inner loops, and inner loops finished before outer ones.
     //
     // So, we can keep track of loops in a loop stack which  keeps track of loops as they are encountered.
     // This lets us implement next/redo/break/retry easily for the non-closure cases
     private Stack<IRLoop> loopStack = new Stack<>();
 
     public IRLoop getCurrentLoop() {
         return loopStack.isEmpty() ? null : loopStack.peek();
     }
 
     protected IRManager manager;
 
     public IRBuilder(IRManager manager) {
         this.manager = manager;
         this.activeRescuers.push(Label.UNRESCUED_REGION_LABEL);
     }
 
     public void addInstr(IRScope s, Instr i) {
         // If we are building an ensure body, stash the instruction
         // in the ensure body's list. If not, add it to the scope directly.
         if (ensureBodyBuildStack.empty()) {
             s.addInstr(i);
         } else {
             ensureBodyBuildStack.peek().addInstr(i);
         }
     }
 
     public void addInstrAtBeginning(IRScope s, Instr i) {
         // If we are building an ensure body, stash the instruction
         // in the ensure body's list. If not, add it to the scope directly.
         if (ensureBodyBuildStack.empty()) {
             s.addInstrAtBeginning(i);
         } else {
             ensureBodyBuildStack.peek().addInstrAtBeginning(i);
         }
     }
 
     private Operand getImplicitBlockArg(IRScope s) {
         int n = 0;
         while (s != null && s instanceof IRClosure) {
             // We have this oddity of an extra inserted scope for instance/class/module evals
             if (s instanceof IREvalScript && ((IREvalScript)s).isModuleOrInstanceEval()) {
                 n++;
             }
             n++;
             s = s.getLexicalParent();
         }
 
         if (s != null) {
             LocalVariable v = null;
             if (s instanceof IRMethod || s instanceof IRMetaClassBody) {
                 v = s.getLocalVariable(Variable.BLOCK, 0);
             }
 
             if (v != null) {
                 return n == 0 ? v : v.cloneForDepth(n);
             }
         }
 
         return manager.getNil();
     }
 
     // Emit cloned ensure bodies by walking up the ensure block stack.
     // If we have been passed a loop value, only emit bodies that are nested within that loop.
     private void emitEnsureBlocks(IRScope s, IRLoop loop) {
         int n = activeEnsureBlockStack.size();
         EnsureBlockInfo[] ebArray = activeEnsureBlockStack.toArray(new EnsureBlockInfo[n]);
         for (int i = n-1; i >= 0; i--) {
             EnsureBlockInfo ebi = ebArray[i];
 
             // For "break" and "next" instructions, we only want to run
             // ensure blocks from the loops they are present in.
             if (loop != null && ebi.innermostLoop != loop) break;
 
             // SSS FIXME: Should $! be restored before or after the ensure block is run?
             if (ebi.savedGlobalException != null) {
                 addInstr(s, new PutGlobalVarInstr("$!", ebi.savedGlobalException));
             }
 
             // Clone into host scope
             ebi.cloneIntoHostScope(this, s);
         }
     }
 
     private Operand buildOperand(Node node, IRScope s) throws NotCompilableException {
         switch (node.getNodeType()) {
             case ALIASNODE: return buildAlias((AliasNode) node, s);
             case ANDNODE: return buildAnd((AndNode) node, s);
             case ARGSCATNODE: return buildArgsCat((ArgsCatNode) node, s);
             case ARGSPUSHNODE: return buildArgsPush((ArgsPushNode) node, s);
             case ARRAYNODE: return buildArray(node, s);
             case ATTRASSIGNNODE: return buildAttrAssign((AttrAssignNode) node, s);
             case BACKREFNODE: return buildBackref((BackRefNode) node, s);
             case BEGINNODE: return buildBegin((BeginNode) node, s);
             case BIGNUMNODE: return buildBignum((BignumNode) node);
             case BLOCKNODE: return buildBlock((BlockNode) node, s);
             case BREAKNODE: return buildBreak((BreakNode) node, s);
             case CALLNODE: return buildCall((CallNode) node, s);
             case CASENODE: return buildCase((CaseNode) node, s);
             case CLASSNODE: return buildClass((ClassNode) node, s);
             case CLASSVARNODE: return buildClassVar((ClassVarNode) node, s);
             case CLASSVARASGNNODE: return buildClassVarAsgn((ClassVarAsgnNode) node, s);
             case CLASSVARDECLNODE: return buildClassVarDecl((ClassVarDeclNode) node, s);
             case COLON2NODE: return buildColon2((Colon2Node) node, s);
             case COLON3NODE: return buildColon3((Colon3Node) node, s);
             case COMPLEXNODE: return buildComplex((ComplexNode) node, s);
             case CONSTDECLNODE: return buildConstDecl((ConstDeclNode) node, s);
             case CONSTNODE: return searchConst(s, ((ConstNode) node).getName());
             case DASGNNODE: return buildDAsgn((DAsgnNode) node, s);
             case DEFINEDNODE: return buildGetDefinition(((DefinedNode) node).getExpressionNode(), s);
             case DEFNNODE: return buildDefn((MethodDefNode) node, s);
             case DEFSNODE: return buildDefs((DefsNode) node, s);
             case DOTNODE: return buildDot((DotNode) node, s);
             case DREGEXPNODE: return buildDRegexp((DRegexpNode) node, s);
             case DSTRNODE: return buildDStr((DStrNode) node, s);
             case DSYMBOLNODE: return buildDSymbol((DSymbolNode) node, s);
             case DVARNODE: return buildDVar((DVarNode) node, s);
             case DXSTRNODE: return buildDXStr((DXStrNode) node, s);
             case ENCODINGNODE: return buildEncoding((EncodingNode)node, s);
             case ENSURENODE: return buildEnsureNode((EnsureNode) node, s);
             case EVSTRNODE: return buildEvStr((EvStrNode) node, s);
             case FALSENODE: return buildFalse();
             case FCALLNODE: return buildFCall((FCallNode) node, s);
             case FIXNUMNODE: return buildFixnum((FixnumNode) node);
             case FLIPNODE: return buildFlip((FlipNode) node, s);
             case FLOATNODE: return buildFloat((FloatNode) node);
             case FORNODE: return buildFor((ForNode) node, s);
             case GLOBALASGNNODE: return buildGlobalAsgn((GlobalAsgnNode) node, s);
             case GLOBALVARNODE: return buildGlobalVar((GlobalVarNode) node, s);
             case HASHNODE: return buildHash((HashNode) node, s);
             case IFNODE: return buildIf((IfNode) node, s);
             case INSTASGNNODE: return buildInstAsgn((InstAsgnNode) node, s);
             case INSTVARNODE: return buildInstVar((InstVarNode) node, s);
             case ITERNODE: return buildIter((IterNode) node, s);
             case LAMBDANODE: return buildLambda((LambdaNode)node, s);
             case LITERALNODE: return buildLiteral((LiteralNode) node, s);
             case LOCALASGNNODE: return buildLocalAsgn((LocalAsgnNode) node, s);
             case LOCALVARNODE: return buildLocalVar((LocalVarNode) node, s);
             case MATCH2NODE: return buildMatch2((Match2Node) node, s);
             case MATCH3NODE: return buildMatch3((Match3Node) node, s);
             case MATCHNODE: return buildMatch((MatchNode) node, s);
             case MODULENODE: return buildModule((ModuleNode) node, s);
             case MULTIPLEASGNNODE: return buildMultipleAsgn((MultipleAsgnNode) node, s); // Only for 1.8
             case MULTIPLEASGN19NODE: return buildMultipleAsgn19((MultipleAsgn19Node) node, s);
             case NEWLINENODE: return buildNewline((NewlineNode) node, s);
             case NEXTNODE: return buildNext((NextNode) node, s);
             case NTHREFNODE: return buildNthRef((NthRefNode) node, s);
             case NILNODE: return buildNil();
             case OPASGNANDNODE: return buildOpAsgnAnd((OpAsgnAndNode) node, s);
             case OPASGNNODE: return buildOpAsgn((OpAsgnNode) node, s);
             case OPASGNORNODE: return buildOpAsgnOr((OpAsgnOrNode) node, s);
             case OPELEMENTASGNNODE: return buildOpElementAsgn((OpElementAsgnNode) node, s);
             case ORNODE: return buildOr((OrNode) node, s);
             case PREEXENODE: return buildPreExe((PreExeNode) node, s);
             case POSTEXENODE: return buildPostExe((PostExeNode) node, s);
             case RATIONALNODE: return buildRational((RationalNode) node);
             case REDONODE: return buildRedo(s);
             case REGEXPNODE: return buildRegexp((RegexpNode) node, s);
             case RESCUEBODYNODE:
                 throw new NotCompilableException("rescue body is handled by rescue compilation at: " + node.getPosition());
             case RESCUENODE: return buildRescue((RescueNode) node, s);
             case RETRYNODE: return buildRetry(s);
             case RETURNNODE: return buildReturn((ReturnNode) node, s);
             case ROOTNODE:
                 throw new NotCompilableException("Use buildRoot(); Root node at: " + node.getPosition());
             case SCLASSNODE: return buildSClass((SClassNode) node, s);
             case SELFNODE: return buildSelf(s);
             case SPLATNODE: return buildSplat((SplatNode) node, s);
             case STRNODE: return buildStr((StrNode) node, s);
             case SUPERNODE: return buildSuper((SuperNode) node, s);
             case SVALUENODE: return buildSValue((SValueNode) node, s);
             case SYMBOLNODE: return buildSymbol((SymbolNode) node);
             case TRUENODE: return buildTrue();
             case UNDEFNODE: return buildUndef(node, s);
             case UNTILNODE: return buildUntil((UntilNode) node, s);
             case VALIASNODE: return buildVAlias((VAliasNode) node, s);
             case VCALLNODE: return buildVCall((VCallNode) node, s);
             case WHILENODE: return buildWhile((WhileNode) node, s);
             case WHENNODE: assert false : "When nodes are handled by case node compilation."; return null;
             case XSTRNODE: return buildXStr((XStrNode) node, s);
             case YIELDNODE: return buildYield((YieldNode) node, s);
             case ZARRAYNODE: return buildZArray(s);
             case ZSUPERNODE: return buildZSuper((ZSuperNode) node, s);
             default: throw new NotCompilableException("Unknown node encountered in builder: " + node.getClass());
         }
     }
 
     private boolean hasListener() {
         return manager.getIRScopeListener() != null;
     }
 
     public IRBuilder newIRBuilder(IRManager manager) {
         return new IRBuilder(manager);
     }
 
     public Node skipOverNewlines(IRScope s, Node n) {
         if (n.getNodeType() == NodeType.NEWLINENODE) {
             // Do not emit multiple line number instrs for the same line
             int currLineNum = n.getPosition().getLine();
             if (currLineNum != _lastProcessedLineNum) {
                 if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
                     addInstr(s, new TraceInstr(RubyEvent.LINE, methodNameFor(s), s.getFileName(), currLineNum));
                 }
                addInstr(s, new LineNumberInstr(s, currLineNum));
                _lastProcessedLineNum = currLineNum;
             }
         }
 
         while (n.getNodeType() == NodeType.NEWLINENODE) {
             n = ((NewlineNode) n).getNextNode();
         }
 
         return n;
     }
 
     public Operand build(Node node, IRScope s) {
         if (node == null) return null;
 
         if (s == null) {
             System.out.println("Got a null scope!");
             throw new NotCompilableException("Unknown node encountered in builder: " + node);
         }
         if (hasListener()) {
             IRScopeListener listener = manager.getIRScopeListener();
             listener.startBuildOperand(node, s);
         }
         Operand operand = buildOperand(node, s);
         if (hasListener()) {
             IRScopeListener listener = manager.getIRScopeListener();
             listener.endBuildOperand(node, s, operand);
         }
         return operand;
     }
 
     public Operand buildLambda(LambdaNode node, IRScope s) {
         IRClosure closure = new IRClosure(manager, s, node.getPosition().getLine(), node.getScope(), Arity.procArityOf(node.getArgs()), node.getArgumentType());
 
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder closureBuilder = newIRBuilder(manager);
 
         // Receive self
         closureBuilder.addInstr(closure, new ReceiveSelfInstr(closure.getSelf()));
 
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         closureBuilder.addInstr(closure, new CopyInstr(closure.getCurrentScopeVariable(), new CurrentScope(0)));
         closureBuilder.addInstr(closure, new CopyInstr(closure.getCurrentModuleVariable(), new ScopeModule(0)));
 
         // args
         closureBuilder.receiveBlockArgs(node, closure);
 
         Operand closureRetVal = node.getBody() == null ? manager.getNil() : closureBuilder.build(node.getBody(), closure);
 
         // can be U_NIL if the node is an if node with returns in both branches.
         if (closureRetVal != U_NIL) closureBuilder.addInstr(closure, new ReturnInstr(closureRetVal));
 
         closureBuilder.handleBreakAndReturnsInLambdas(closure);
 
         Variable lambda = s.createTemporaryVariable();
         WrappedIRClosure lambdaBody = new WrappedIRClosure(closure.getSelf(), closure);
         addInstr(s, new BuildLambdaInstr(lambda, lambdaBody, node.getPosition()));
         return lambda;
     }
 
     public Operand buildEncoding(EncodingNode node, IRScope s) {
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new GetEncodingInstr(ret, node.getEncoding()));
         return ret;
     }
 
     // Non-arg masgn
     public Operand buildMultipleAsgn19(MultipleAsgn19Node multipleAsgnNode, IRScope s) {
         Operand  values = build(multipleAsgnNode.getValueNode(), s);
         Variable ret = getValueInTemporaryVariable(s, values);
         Variable tmp = s.createTemporaryVariable();
         addInstr(s, new ToAryInstr(tmp, ret));
         buildMultipleAsgn19Assignment(multipleAsgnNode, s, null, tmp);
         return ret;
     }
 
     protected Variable copyAndReturnValue(IRScope s, Operand val) {
         return addResultInstr(s, new CopyInstr(s.createTemporaryVariable(), val));
     }
 
     protected Variable getValueInTemporaryVariable(IRScope s, Operand val) {
         if (val != null && val instanceof TemporaryVariable) return (Variable) val;
 
         return copyAndReturnValue(s, val);
     }
 
     // Return the last argument in the list -- AttrAssign needs it
     protected Operand buildCallArgs(List<Operand> argsList, Node args, IRScope s) {
         // unwrap newline nodes to get their actual type
         args = skipOverNewlines(s, args);
         switch (args.getNodeType()) {
             case ARGSCATNODE: {
                 ArgsCatNode argsCatNode = (ArgsCatNode)args;
                 Operand v1 = build(argsCatNode.getFirstNode(), s);
                 Operand v2 = build(argsCatNode.getSecondNode(), s);
                 Variable res = s.createTemporaryVariable();
                 addInstr(s, new BuildCompoundArrayInstr(res, v1, v2, false));
                 argsList.add(new Splat(res, true));
                 return v2;
             }
             case ARGSPUSHNODE:  {
                 ArgsPushNode argsPushNode = (ArgsPushNode)args;
                 Operand v1 = build(argsPushNode.getFirstNode(), s);
                 Operand v2 = build(argsPushNode.getSecondNode(), s);
                 Variable res = s.createTemporaryVariable();
                 addInstr(s, new BuildCompoundArrayInstr(res, v1, v2, true));
                 argsList.add(new Splat(res, true));
                 return v2;
             }
             case ARRAYNODE: {
                 ArrayNode arrayNode = (ArrayNode)args;
                 if (arrayNode.isLightweight()) {
                     List<Node> children = arrayNode.childNodes();
                     // explode array, it's an internal "args" array
                     for (Node n: children) {
                         argsList.add(build(n, s));
                     }
                 } else {
                     // use array as-is, it's a literal array
                     argsList.add(build(arrayNode, s));
                 }
                 break;
             }
             case SPLATNODE: {
                 Splat splat = new Splat(build(((SplatNode)args).getValue(), s), true);
                 argsList.add(splat);
                 break;
             }
             default: {
                 argsList.add(build(args, s));
                 break;
             }
         }
 
         return argsList.isEmpty() ? manager.getNil() : argsList.get(argsList.size() - 1);
     }
 
     public List<Operand> setupCallArgs(Node args, IRScope s) {
         List<Operand> argsList = new ArrayList<>();
         if (args != null) buildCallArgs(argsList, args, s);
         return argsList;
     }
 
     // Non-arg masgn (actually a nested masgn)
     public void buildVersionSpecificAssignment(Node node, IRScope s, Variable v) {
         switch (node.getNodeType()) {
         case MULTIPLEASGN19NODE: {
             Variable tmp = s.createTemporaryVariable();
             addInstr(s, new ToAryInstr(tmp, v));
             buildMultipleAsgn19Assignment((MultipleAsgn19Node)node, s, null, tmp);
             break;
         }
         default:
             throw new NotCompilableException("Can't build assignment node: " + node);
         }
     }
 
     // This method is called to build assignments for a multiple-assignment instruction
     public void buildAssignment(Node node, IRScope s, Variable rhsVal) {
         switch (node.getNodeType()) {
             case ATTRASSIGNNODE:
                 buildAttrAssignAssignment(node, s, rhsVal);
                 break;
             case CLASSVARASGNNODE:
                 addInstr(s, new PutClassVariableInstr(classVarDefinitionContainer(s), ((ClassVarAsgnNode)node).getName(), rhsVal));
                 break;
             case CLASSVARDECLNODE:
                 addInstr(s, new PutClassVariableInstr(classVarDeclarationContainer(s), ((ClassVarDeclNode)node).getName(), rhsVal));
                 break;
             case CONSTDECLNODE:
                 buildConstDeclAssignment((ConstDeclNode) node, s, rhsVal);
                 break;
             case DASGNNODE: {
                 DAsgnNode variable = (DAsgnNode) node;
                 int depth = variable.getDepth();
                 addInstr(s, new CopyInstr(s.getLocalVariable(variable.getName(), depth), rhsVal));
                 break;
             }
             case GLOBALASGNNODE:
                 addInstr(s, new PutGlobalVarInstr(((GlobalAsgnNode)node).getName(), rhsVal));
                 break;
             case INSTASGNNODE:
                 // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
                 addInstr(s, new PutFieldInstr(s.getSelf(), ((InstAsgnNode)node).getName(), rhsVal));
                 break;
             case LOCALASGNNODE: {
                 LocalAsgnNode localVariable = (LocalAsgnNode) node;
                 int depth = localVariable.getDepth();
                 addInstr(s, new CopyInstr(s.getLocalVariable(localVariable.getName(), depth), rhsVal));
                 break;
             }
             case ZEROARGNODE:
                 throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
             default:
                 buildVersionSpecificAssignment(node, s, rhsVal);
         }
     }
 
     protected LocalVariable getBlockArgVariable(IRScope s, String name, int depth) {
         if (!(s instanceof IRFor)) throw new NotCompilableException("Cannot ask for block-arg variable in 1.9 mode");
 
         return s.getLocalVariable(name, depth);
     }
 
     protected void receiveBlockArg(IRScope s, Variable v, Operand argsArray, int argIndex, boolean isSplat) {
         if (argsArray != null) {
             // We are in a nested receive situation -- when we are not at the root of a masgn tree
             // Ex: We are trying to receive (b,c) in this example: "|a, (b,c), d| = ..."
             if (isSplat) addInstr(s, new RestArgMultipleAsgnInstr(v, argsArray, argIndex));
             else addInstr(s, new ReqdArgMultipleAsgnInstr(v, argsArray, argIndex));
         } else {
             // argsArray can be null when the first node in the args-node-ast is a multiple-assignment
             // For example, for-nodes
             addInstr(s, isSplat ? new ReceiveRestArgInstr(v, argIndex, argIndex) : new ReceivePreReqdArgInstr(v, argIndex));
         }
     }
 
     public void buildVersionSpecificBlockArgsAssignment(Node node, IRScope s) {
         if (!(s instanceof IRFor)) throw new NotCompilableException("Should not have come here for block args assignment in 1.9 mode: " + node);
 
         // Argh!  For-loop bodies and regular iterators are different in terms of block-args!
         switch (node.getNodeType()) {
             case MULTIPLEASGN19NODE: {
                 ListNode sourceArray = ((MultipleAsgn19Node) node).getPre();
                 int i = 0;
                 for (Node an: sourceArray.childNodes()) {
                     // Use 1.8 mode version for this
                     buildBlockArgsAssignment(an, s, null, i, false);
                     i++;
                 }
                 break;
             }
             default:
                 throw new NotCompilableException("Can't build assignment node: " + node);
         }
     }
 
     // This method is called to build arguments for a block!
     public void buildBlockArgsAssignment(Node node, IRScope s, Operand argsArray, int argIndex, boolean isSplat) {
         Variable v;
         switch (node.getNodeType()) {
             case ATTRASSIGNNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 buildAttrAssignAssignment(node, s, v);
                 break;
             case DASGNNODE: {
                 DAsgnNode dynamicAsgn = (DAsgnNode) node;
                 v = getBlockArgVariable(s, dynamicAsgn.getName(), dynamicAsgn.getDepth());
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 break;
             }
             case CLASSVARASGNNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 addInstr(s, new PutClassVariableInstr(classVarDefinitionContainer(s), ((ClassVarAsgnNode)node).getName(), v));
                 break;
             case CLASSVARDECLNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 addInstr(s, new PutClassVariableInstr(classVarDeclarationContainer(s), ((ClassVarDeclNode)node).getName(), v));
                 break;
             case CONSTDECLNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 buildConstDeclAssignment((ConstDeclNode) node, s, v);
                 break;
             case GLOBALASGNNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 addInstr(s, new PutGlobalVarInstr(((GlobalAsgnNode)node).getName(), v));
                 break;
             case INSTASGNNODE:
                 v = s.createTemporaryVariable();
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
                 addInstr(s, new PutFieldInstr(s.getSelf(), ((InstAsgnNode)node).getName(), v));
                 break;
             case LOCALASGNNODE: {
                 LocalAsgnNode localVariable = (LocalAsgnNode) node;
                 int depth = localVariable.getDepth();
                 v = getBlockArgVariable(s, localVariable.getName(), depth);
                 receiveBlockArg(s, v, argsArray, argIndex, isSplat);
                 break;
             }
             case ZEROARGNODE:
                 throw new NotCompilableException("Shouldn't get here; zeroarg does not do assignment: " + node);
             default:
                 buildVersionSpecificBlockArgsAssignment(node, s);
         }
     }
 
     public Operand buildAlias(final AliasNode alias, IRScope s) {
         Operand newName = build(alias.getNewName(), s);
         Operand oldName = build(alias.getOldName(), s);
         addInstr(s, new AliasInstr(newName, oldName));
 
         return manager.getNil();
     }
 
     // Translate "ret = (a && b)" --> "ret = (a ? b : false)" -->
     //
     //    v1 = -- build(a) --
     //       OPT: ret can be set to v1, but effectively v1 is false if we take the branch to L.
     //            while this info can be inferred by using attributes, why bother if we can do this?
     //    ret = v1
     //    beq(v1, false, L)
     //    v2 = -- build(b) --
     //    ret = v2
     // L:
     //
     public Operand buildAnd(final AndNode andNode, IRScope s) {
         if (andNode.getFirstNode().getNodeType().alwaysTrue()) {
             // build first node (and ignore its result) and then second node
             build(andNode.getFirstNode(), s);
             return build(andNode.getSecondNode(), s);
         } else if (andNode.getFirstNode().getNodeType().alwaysFalse()) {
             // build first node only and return its value
             return build(andNode.getFirstNode(), s);
         } else {
             Label    l   = s.getNewLabel();
             Operand  v1  = build(andNode.getFirstNode(), s);
             Variable ret = getValueInTemporaryVariable(s, v1);
             addInstr(s, BEQInstr.create(v1, manager.getFalse(), l));
             Operand  v2  = build(andNode.getSecondNode(), s);
             addInstr(s, new CopyInstr(ret, v2));
             addInstr(s, new LabelInstr(l));
             return ret;
         }
     }
 
     public Operand buildArray(Node node, IRScope s) {
         List<Operand> elts = new ArrayList<>();
         for (Node e: node.childNodes())
             elts.add(build(e, s));
 
         return copyAndReturnValue(s, new Array(elts));
     }
 
     public Operand buildArgsCat(final ArgsCatNode argsCatNode, IRScope s) {
         Operand v1 = build(argsCatNode.getFirstNode(), s);
         Operand v2 = build(argsCatNode.getSecondNode(), s);
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BuildCompoundArrayInstr(res, v1, v2, false));
         return res;
     }
 
     public Operand buildArgsPush(final ArgsPushNode node, IRScope s) {
         Operand v1 = build(node.getFirstNode(), s);
         Operand v2 = build(node.getSecondNode(), s);
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BuildCompoundArrayInstr(res, v1, v2, true));
         return res;
     }
 
     private Operand buildAttrAssign(final AttrAssignNode attrAssignNode, IRScope s) {
         Operand obj = build(attrAssignNode.getReceiverNode(), s);
         List<Operand> args = new ArrayList<>();
         Node argsNode = attrAssignNode.getArgsNode();
         Operand lastArg = (argsNode == null) ? manager.getNil() : buildCallArgs(args, argsNode, s);
         addInstr(s, new AttrAssignInstr(obj, new MethAddr(attrAssignNode.getName()), args.toArray(new Operand[args.size()])));
         return lastArg;
     }
 
     public Operand buildAttrAssignAssignment(Node node, IRScope s, Operand value) {
         final AttrAssignNode attrAssignNode = (AttrAssignNode) node;
         Operand obj = build(attrAssignNode.getReceiverNode(), s);
         List<Operand> args = setupCallArgs(attrAssignNode.getArgsNode(), s);
         args.add(value);
         addInstr(s, new AttrAssignInstr(obj, new MethAddr(attrAssignNode.getName()), args.toArray(new Operand[args.size()])));
         return value;
     }
 
     public Operand buildBackref(BackRefNode node, IRScope s) {
         // SSS FIXME: Required? Verify with Tom/Charlie
         return copyAndReturnValue(s, new Backref(node.getType()));
     }
 
     public Operand buildBegin(BeginNode beginNode, IRScope s) {
         return build(beginNode.getBodyNode(), s);
     }
 
     public Operand buildBignum(BignumNode node) {
         // SSS: Since bignum literals are effectively interned objects, no need to copyAndReturnValue(...)
         // Or is this a premature optimization?
         return new Bignum(node.getValue());
     }
 
     public Operand buildBlock(BlockNode node, IRScope s) {
         Operand retVal = null;
         for (Node child : node.childNodes()) {
             retVal = build(child, s);
         }
 
         // Value of the last expression in the block
         return retVal;
     }
 
     public Operand buildBreak(BreakNode breakNode, IRScope s) {
         IRLoop currLoop = getCurrentLoop();
 
         Operand rv = build(breakNode.getValueNode(), s);
         // If we have ensure blocks, have to run those first!
         if (!activeEnsureBlockStack.empty()) emitEnsureBlocks(s, currLoop);
         else if (!activeRescueBlockStack.empty()) activeRescueBlockStack.peek().restoreException(this, s, currLoop);
 
         if (currLoop != null) {
             addInstr(s, new CopyInstr(currLoop.loopResult, rv));
             addInstr(s, new JumpInstr(currLoop.loopEndLabel));
         } else {
             if (s instanceof IRClosure) {
                 // This lexical scope value is only used (and valid) in regular block contexts.
                 // If this instruction is executed in a Proc or Lambda context, the lexical scope value is useless.
                 IRScope returnScope = s.getLexicalParent();
                 // In 1.9 and later modes, no breaks from evals
                 if (s instanceof IREvalScript || returnScope == null) addInstr(s, new ThrowExceptionInstr(IRException.BREAK_LocalJumpError));
                 else addInstr(s, new BreakInstr(rv, returnScope.getName()));
             } else {
                 // We are not in a closure or a loop => bad break instr!
                 addInstr(s, new ThrowExceptionInstr(IRException.BREAK_LocalJumpError));
             }
         }
 
         // Once the break instruction executes, control exits this scope
         return U_NIL;
     }
 
     private void handleNonlocalReturnInMethod(IRScope s) {
         Label rBeginLabel = s.getNewLabel();
         Label rEndLabel   = s.getNewLabel();
         Label gebLabel    = s.getNewLabel();
 
         // Protect the entire body as it exists now with the global ensure block
         //
         // Add label and marker instruction in reverse order to the beginning
         // so that the label ends up being the first instr.
         addInstrAtBeginning(s, new ExceptionRegionStartMarkerInstr(gebLabel));
         addInstrAtBeginning(s, new LabelInstr(rBeginLabel));
         addInstr(s, new ExceptionRegionEndMarkerInstr());
 
         // Receive exceptions (could be anything, but the handler only processes IRReturnJumps)
         addInstr(s, new LabelInstr(gebLabel));
         Variable exc = s.createTemporaryVariable();
         addInstr(s, new ReceiveJRubyExceptionInstr(exc));
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             addInstr(s, new TraceInstr(RubyEvent.RETURN, s.getName(), s.getFileName(), -1));
         }
 
         // Handle break using runtime helper
         // --> IRRuntimeHelpers.handleNonlocalReturn(scope, bj, blockType)
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new RuntimeHelperCall(ret, HANDLE_NONLOCAL_RETURN, new Operand[]{exc} ));
         addInstr(s, new ReturnInstr(ret));
 
         // End
         addInstr(s, new LabelInstr(rEndLabel));
     }
 
     private Operand receiveBreakException(IRScope s, Operand block, CodeBlock codeBlock) {
         // Check if we have to handle a break
         if (block == null ||
             !(block instanceof WrappedIRClosure) ||
             !(((WrappedIRClosure)block).getClosure()).flags.contains(IRFlags.HAS_BREAK_INSTRS)) {
             // No protection needed -- add the call and return
             return codeBlock.run();
         }
 
         Label rBeginLabel = s.getNewLabel();
         Label rEndLabel   = s.getNewLabel();
         Label rescueLabel = s.getNewLabel();
 
         // Protected region
         addInstr(s, new LabelInstr(rBeginLabel));
         addInstr(s, new ExceptionRegionStartMarkerInstr(rescueLabel));
         Variable callResult = (Variable)codeBlock.run();
         addInstr(s, new JumpInstr(rEndLabel));
         addInstr(s, new ExceptionRegionEndMarkerInstr());
 
         // Receive exceptions (could be anything, but the handler only processes IRBreakJumps)
         addInstr(s, new LabelInstr(rescueLabel));
         Variable exc = s.createTemporaryVariable();
         addInstr(s, new ReceiveJRubyExceptionInstr(exc));
 
         // Handle break using runtime helper
         // --> IRRuntimeHelpers.handlePropagatedBreak(context, scope, bj, blockType)
         addInstr(s, new RuntimeHelperCall(callResult, HANDLE_PROPAGATE_BREAK, new Operand[]{exc} ));
 
         // End
         addInstr(s, new LabelInstr(rEndLabel));
 
         return callResult;
     }
 
     // Wrap call in a rescue handler that catches the IRBreakJump
     private void receiveBreakException(final IRScope s, Operand block, final CallInstr callInstr) {
         receiveBreakException(s, block, new CodeBlock() { public Operand run() { addInstr(s, callInstr); return callInstr.getResult(); } });
     }
 
     public Operand buildCall(CallNode callNode, IRScope s) {
         Node          callArgsNode = callNode.getArgsNode();
         Node          receiverNode = callNode.getReceiverNode();
 
         // check for "string".freeze
         if (receiverNode instanceof StrNode && callNode.getName().equals("freeze")) {
             // frozen string optimization
             return new FrozenString(((StrNode)receiverNode).getValue());
         }
 
         // Though you might be tempted to move this build into the CallInstr as:
         //    new Callinstr( ... , build(receiverNode, s), ...)
         // that is incorrect IR because the receiver has to be built *before* call arguments are built
         // to preserve expected code execution order
         Operand       receiver     = build(receiverNode, s);
         List<Operand> args         = setupCallArgs(callArgsNode, s);
         Operand       block        = setupCallClosure(callNode.getIterNode(), s);
         Variable      callResult   = s.createTemporaryVariable();
         CallInstr     callInstr    = CallInstr.create(callResult, new MethAddr(callNode.getName()), receiver, args.toArray(new Operand[args.size()]), block);
 
         // This is to support the ugly Proc.new with no block, which must see caller's frame
         if (
                 callNode.getName().equals("new") &&
                 receiverNode instanceof ConstNode &&
                 ((ConstNode)receiverNode).getName().equals("Proc")) {
             callInstr.setProcNew(true);
         }
 
         receiveBreakException(s, block, callInstr);
         return callResult;
     }
 
     public Operand buildCase(CaseNode caseNode, IRScope s) {
         // get the incoming case value
         Operand value = build(caseNode.getCaseNode(), s);
 
         // This is for handling case statements without a value (see example below)
         //   case
         //     when true <blah>
         //     when false <blah>
         //   end
         if (value == null) value = UndefinedValue.UNDEFINED;
 
         Label     endLabel  = s.getNewLabel();
         boolean   hasElse   = (caseNode.getElseNode() != null);
         Label     elseLabel = s.getNewLabel();
         Variable  result    = s.createTemporaryVariable();
 
         List<Label> labels = new ArrayList<>();
         Map<Label, Node> bodies = new HashMap<>();
 
         // build each "when"
         for (Node aCase : caseNode.getCases().childNodes()) {
             WhenNode whenNode = (WhenNode)aCase;
             Label bodyLabel = s.getNewLabel();
 
             Variable eqqResult = s.createTemporaryVariable();
             labels.add(bodyLabel);
             Operand v1, v2;
             if (whenNode.getExpressionNodes() instanceof ListNode) {
                 // Note about refactoring:
                 // - BEQInstr has a quick implementation when the second operand is a boolean literal
                 //   If it can be fixed to do this even on the first operand, we can switch around
                 //   v1 and v2 in the UndefinedValue scenario and DRY out this code.
                 // - Even with this asymmetric implementation of BEQInstr, you might be tempted to
                 //   switch around v1 and v2 in the else case.  But, that is equivalent to this Ruby code change:
                 //      (v1 == value) instead of (value == v1)
                 //   It seems that they should be identical, but the first one is v1.==(value) and the second one is
                 //   value.==(v1).  This is just fine *if* the Ruby programmer has implemented an algebraically
                 //   symmetric "==" method on those objects.  If not, then, the results might be unexpected where the
                 //   code (intentionally or otherwise) relies on this asymmetry of "==".  While it could be argued
                 //   that this a Ruby code bug, we will just try to preserve the order of the == check as it appears
                 //   in the Ruby code.
                 if (value == UndefinedValue.UNDEFINED)  {
                     v1 = build(whenNode.getExpressionNodes(), s);
                     v2 = manager.getTrue();
                 } else {
                     v1 = value;
                     v2 = build(whenNode.getExpressionNodes(), s);
                 }
             } else {
                 addInstr(s, new EQQInstr(eqqResult, build(whenNode.getExpressionNodes(), s), value));
                 v1 = eqqResult;
                 v2 = manager.getTrue();
             }
             addInstr(s, BEQInstr.create(v1, v2, bodyLabel));
 
             // SSS FIXME: This doesn't preserve original order of when clauses.  We could consider
             // preserving the order (or maybe not, since we would have to sort the constants first
             // in any case) for outputting jump tables in certain situations.
             //
             // add body to map for emitting later
             bodies.put(bodyLabel, whenNode.getBodyNode());
         }
 
         // Jump to else in case nothing matches!
         addInstr(s, new JumpInstr(elseLabel));
 
         // Build "else" if it exists
         if (hasElse) {
             labels.add(elseLabel);
             bodies.put(elseLabel, caseNode.getElseNode());
         }
 
         // Now, emit bodies while preserving when clauses order
         for (Label whenLabel: labels) {
             addInstr(s, new LabelInstr(whenLabel));
             Operand bodyValue = build(bodies.get(whenLabel), s);
             // bodyValue can be null if the body ends with a return!
             if (bodyValue != null) {
                 // SSS FIXME: Do local optimization of break results (followed by a copy & jump) to short-circuit the jump right away
                 // rather than wait to do it during an optimization pass when a dead jump needs to be removed.  For this, you have
                 // to look at what the last generated instruction was.
                 addInstr(s, new CopyInstr(result, bodyValue));
                 addInstr(s, new JumpInstr(endLabel));
             }
         }
 
         if (!hasElse) {
             addInstr(s, new LabelInstr(elseLabel));
             addInstr(s, new CopyInstr(result, manager.getNil()));
             addInstr(s, new JumpInstr(endLabel));
         }
 
         // Close it out
         addInstr(s, new LabelInstr(endLabel));
 
         return result;
     }
 
     /**
      * Build a new class and add it to the current scope (s).
      */
     public Operand buildClass(ClassNode classNode, IRScope s) {
         Node superNode = classNode.getSuperNode();
         Colon3Node cpath = classNode.getCPath();
         Operand superClass = (superNode == null) ? null : build(superNode, s);
         String className = cpath.getName();
         Operand container = getContainerFromCPath(cpath, s);
         IRClassBody body = new IRClassBody(manager, s, className, classNode.getPosition().getLine(), classNode.getScope());
         Variable tmpVar = addResultInstr(s, new DefineClassInstr(s.createTemporaryVariable(), body, container, superClass));
 
         return buildModuleOrClassBody(s, tmpVar, body, classNode.getBodyNode(), classNode.getPosition().getLine());
     }
 
     // class Foo; class << self; end; end
     // Here, the class << self declaration is in Foo's body.
     // Foo is the class in whose context this is being defined.
     public Operand buildSClass(SClassNode sclassNode, IRScope s) {
         Operand receiver = build(sclassNode.getReceiverNode(), s);
         IRModuleBody body = new IRMetaClassBody(manager, s, manager.getMetaClassName(), sclassNode.getPosition().getLine(), sclassNode.getScope());
         Variable tmpVar = addResultInstr(s, new DefineMetaClassInstr(s.createTemporaryVariable(), receiver, body));
 
         return buildModuleOrClassBody(s, tmpVar, body, sclassNode.getBodyNode(), sclassNode.getPosition().getLine());
     }
 
     // @@c
     public Operand buildClassVar(ClassVarNode node, IRScope s) {
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new GetClassVariableInstr(ret, classVarDefinitionContainer(s), node.getName()));
         return ret;
     }
 
     // Add the specified result instruction to the scope and return its result variable.
     private Variable addResultInstr(IRScope s, ResultInstr instr) {
         addInstr(s, (Instr) instr);
 
         return instr.getResult();
     }
 
     // ClassVarAsgn node is assignment within a method/closure scope
     //
     // def foo
     //   @@c = 1
     // end
     public Operand buildClassVarAsgn(final ClassVarAsgnNode classVarAsgnNode, IRScope s) {
         Operand val = build(classVarAsgnNode.getValueNode(), s);
         addInstr(s, new PutClassVariableInstr(classVarDefinitionContainer(s), classVarAsgnNode.getName(), val));
         return val;
     }
 
     // ClassVarDecl node is assignment outside method/closure scope (top-level, class, module)
     //
     // class C
     //   @@c = 1
     // end
     public Operand buildClassVarDecl(final ClassVarDeclNode classVarDeclNode, IRScope s) {
         Operand val = build(classVarDeclNode.getValueNode(), s);
         addInstr(s, new PutClassVariableInstr(classVarDeclarationContainer(s), classVarDeclNode.getName(), val));
         return val;
     }
 
     public Operand classVarDeclarationContainer(IRScope s) {
         return classVarContainer(s, true);
     }
 
     public Operand classVarDefinitionContainer(IRScope s) {
         return classVarContainer(s, false);
     }
 
     // SSS FIXME: This feels a little ugly.  Is there a better way of representing this?
     public Operand classVarContainer(IRScope s, boolean declContext) {
         /* -------------------------------------------------------------------------------
          * We are looking for the nearest enclosing scope that is a non-singleton class body
          * without running into an eval-scope in between.
          *
          * Stop lexical scope walking at an eval script boundary.  Evals are essentially
          * a way for a programmer to splice an entire tree of lexical scopes at the point
          * where the eval happens.  So, when we hit an eval-script boundary at compile-time,
          * defer scope traversal to when we know where this scope has been spliced in.
          * ------------------------------------------------------------------------------- */
         int n = 0;
         IRScope cvarScope = s;
         while (cvarScope != null && !(cvarScope instanceof IREvalScript) && !cvarScope.isNonSingletonClassBody()) {
             cvarScope = cvarScope.getLexicalParent();
             n++;
         }
 
         if ((cvarScope != null) && cvarScope.isNonSingletonClassBody()) {
             return new ScopeModule(n);
         } else {
             return addResultInstr(s, new GetClassVarContainerModuleInstr(s.createTemporaryVariable(),
                     s.getCurrentScopeVariable(), declContext ? null : s.getSelf()));
         }
     }
 
     public Operand buildConstDecl(ConstDeclNode node, IRScope s) {
         return buildConstDeclAssignment(node, s, build(node.getValueNode(), s));
     }
 
     private Operand findContainerModule(IRScope s) {
         int nearestModuleBodyDepth = s.getNearestModuleReferencingScopeDepth();
         return (nearestModuleBodyDepth == -1) ? s.getCurrentModuleVariable() : new ScopeModule(nearestModuleBodyDepth);
     }
 
     private Operand startingSearchScope(IRScope s) {
         int nearestModuleBodyDepth = s.getNearestModuleReferencingScopeDepth();
         return nearestModuleBodyDepth == -1 ? s.getCurrentScopeVariable() : new CurrentScope(nearestModuleBodyDepth);
     }
 
     public Operand buildConstDeclAssignment(ConstDeclNode constDeclNode, IRScope s, Operand val) {
         Node constNode = constDeclNode.getConstNode();
 
         if (constNode == null) {
             addInstr(s, new PutConstInstr(findContainerModule(s), constDeclNode.getName(), val));
         } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
             Operand module = build(((Colon2Node) constNode).getLeftNode(), s);
             addInstr(s, new PutConstInstr(module, constDeclNode.getName(), val));
         } else { // colon3, assign in Object
             addInstr(s, new PutConstInstr(new ObjectClass(), constDeclNode.getName(), val));
         }
 
         return val;
     }
 
     private void genInheritanceSearchInstrs(IRScope s, Operand startingModule, Variable constVal, Label foundLabel, boolean noPrivateConstants, String name) {
         addInstr(s, new InheritanceSearchConstInstr(constVal, startingModule, name, noPrivateConstants));
         addInstr(s, BNEInstr.create(constVal, UndefinedValue.UNDEFINED, foundLabel));
         addInstr(s, new ConstMissingInstr(constVal, startingModule, name));
         addInstr(s, new LabelInstr(foundLabel));
     }
 
     private Operand searchConstInInheritanceHierarchy(IRScope s, Operand startingModule, String name) {
         Variable constVal = s.createTemporaryVariable();
         genInheritanceSearchInstrs(s, startingModule, constVal, s.getNewLabel(), true, name);
         return constVal;
     }
 
     private Operand searchConst(IRScope s, String name) {
         final boolean noPrivateConstants = false;
         Variable v = s.createTemporaryVariable();
 /**
  * SSS FIXME: Went back to a single instruction for now.
  *
  * Do not split search into lexical-search, inheritance-search, and const-missing instrs.
  *
         Label foundLabel = s.getNewLabel();
         addInstr(s, new LexicalSearchConstInstr(v, startingSearchScope(s), name));
         addInstr(s, BNEInstr.create(v, UndefinedValue.UNDEFINED, foundLabel));
         genInheritanceSearchInstrs(s, findContainerModule(startingScope), v, foundLabel, noPrivateConstants, name);
 **/
         addInstr(s, new SearchConstInstr(v, name, startingSearchScope(s), noPrivateConstants));
         return v;
     }
 
     public Operand buildColon2(final Colon2Node iVisited, IRScope s) {
         Node leftNode = iVisited.getLeftNode();
         final String name = iVisited.getName();
 
         // Colon2ImplicitNode
         if (leftNode == null) return searchConst(s, name);
 
         // Colon2ConstNode
         // 1. Load the module first (lhs of node)
         // 2. Then load the constant from the module
         Operand module = build(leftNode, s);
         return searchConstInInheritanceHierarchy(s, module, name);
     }
 
     public Operand buildColon3(Colon3Node node, IRScope s) {
         return searchConstInInheritanceHierarchy(s, new ObjectClass(), node.getName());
     }
 
     public Operand buildComplex(ComplexNode node, IRScope s) {
         return new Complex((ImmutableLiteral) build(node.getNumber(), s));
     }
 
     interface CodeBlock {
         public Operand run();
     }
 
     private Operand protectCodeWithRescue(IRScope s, CodeBlock protectedCode, CodeBlock rescueBlock) {
         // This effectively mimics a begin-rescue-end code block
         // Except this catches all exceptions raised by the protected code
 
         Variable rv = s.createTemporaryVariable();
         Label rBeginLabel = s.getNewLabel();
         Label rEndLabel   = s.getNewLabel();
         Label rescueLabel = s.getNewLabel();
 
         // Protected region code
         addInstr(s, new LabelInstr(rBeginLabel));
         addInstr(s, new ExceptionRegionStartMarkerInstr(rescueLabel));
         Object v1 = protectedCode.run(); // YIELD: Run the protected code block
         addInstr(s, new CopyInstr(rv, (Operand)v1));
         addInstr(s, new JumpInstr(rEndLabel));
         addInstr(s, new ExceptionRegionEndMarkerInstr());
 
         // SSS FIXME: Create an 'Exception' operand type to eliminate the constant lookup below
         // We could preload a set of constant objects that are preloaded at boot time and use them
         // directly in IR when we know there is no lookup involved.
         //
         // new Operand type: CachedClass(String name)?
         //
         // Some candidates: Exception, StandardError, Fixnum, Object, Boolean, etc.
         // So, when they are referenced, they are fetched directly from the runtime object
         // which probably already has cached references to these constants.
         //
         // But, unsure if this caching is safe ... so, just an idea here for now.
 
         // Rescue code
         Label caughtLabel = s.getNewLabel();
         Variable exc = s.createTemporaryVariable();
         Variable excType = s.createTemporaryVariable();
 
         // Receive 'exc' and verify that 'exc' is of ruby-type 'Exception'
         addInstr(s, new LabelInstr(rescueLabel));
         addInstr(s, new ReceiveRubyExceptionInstr(exc));
         addInstr(s, new InheritanceSearchConstInstr(excType, new ObjectClass(), "Exception", false));
         outputExceptionCheck(s, excType, exc, caughtLabel);
 
         // Fall-through when the exc !== Exception; rethrow 'exc'
         addInstr(s, new ThrowExceptionInstr(exc));
 
         // exc === Exception; Run the rescue block
         addInstr(s, new LabelInstr(caughtLabel));
         Object v2 = rescueBlock.run(); // YIELD: Run the protected code block
         if (v2 != null) addInstr(s, new CopyInstr(rv, manager.getNil()));
 
         // End
         addInstr(s, new LabelInstr(rEndLabel));
 
         return rv;
     }
 
     public Operand buildGetDefinition(Node node, final IRScope scope) {
         node = skipOverNewlines(scope, node);
 
         // FIXME: Do we still have MASGN and MASGN19?
         switch (node.getNodeType()) {
         case CLASSVARASGNNODE: case CLASSVARDECLNODE: case CONSTDECLNODE:
         case DASGNNODE: case GLOBALASGNNODE: case LOCALASGNNODE: case MULTIPLEASGNNODE:
         case MULTIPLEASGN19NODE: case OPASGNNODE: case OPASGNANDNODE: case OPASGNORNODE:
         case OPELEMENTASGNNODE: case INSTASGNNODE:
             return new ConstantStringLiteral("assignment");
         case ORNODE: case ANDNODE:
             return new ConstantStringLiteral("expression");
         case FALSENODE:
             return new ConstantStringLiteral("false");
         case LOCALVARNODE: case DVARNODE:
             return new ConstantStringLiteral("local-variable");
         case MATCH2NODE: case MATCH3NODE:
             return new ConstantStringLiteral("method");
         case NILNODE:
             return new ConstantStringLiteral("nil");
         case SELFNODE:
             return new ConstantStringLiteral("self");
         case TRUENODE:
             return new ConstantStringLiteral("true");
         case DREGEXPNODE: case DSTRNODE: {
             final Node dNode = node;
 
             // protected code
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     build(dNode, scope);
                     // always an expression as long as we get through here without an exception!
                     return new ConstantStringLiteral("expression");
                 }
             };
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                 public Operand run() { return manager.getNil(); } // Nothing to do if we got an exception
             };
 
             // Try verifying definition, and if we get an JumpException exception, process it with the rescue block above
             Operand v = protectCodeWithRescue(scope, protectedCode, rescueBlock);
             Label doneLabel = scope.getNewLabel();
             Variable tmpVar = getValueInTemporaryVariable(scope, v);
             addInstr(scope, BNEInstr.create(tmpVar, manager.getNil(), doneLabel));
             addInstr(scope, new CopyInstr(tmpVar, new ConstantStringLiteral("expression")));
             addInstr(scope, new LabelInstr(doneLabel));
 
             return tmpVar;
         }
         case ARRAYNODE: { // If all elts of array are defined the array is as well
             ArrayNode array = (ArrayNode) node;
             Label undefLabel = scope.getNewLabel();
             Label doneLabel = scope.getNewLabel();
 
             Variable tmpVar = scope.createTemporaryVariable();
             for (Node elt: array.childNodes()) {
                 Operand result = buildGetDefinition(elt, scope);
 
                 addInstr(scope, BEQInstr.create(result, manager.getNil(), undefLabel));
             }
 
             addInstr(scope, new CopyInstr(tmpVar, new ConstantStringLiteral("expression")));
             addInstr(scope, new JumpInstr(doneLabel));
             addInstr(scope, new LabelInstr(undefLabel));
             addInstr(scope, new CopyInstr(tmpVar, manager.getNil()));
             addInstr(scope, new LabelInstr(doneLabel));
 
             return tmpVar;
         }
         case BACKREFNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_BACKREF,
                     Operand.EMPTY_ARRAY));
         case GLOBALVARNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_GLOBAL,
                     new Operand[] { new StringLiteral(((GlobalVarNode) node).getName()) }));
         case NTHREFNODE: {
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_NTH_REF,
                     new Operand[] { new Fixnum(((NthRefNode) node).getMatchNumber()) }));
         }
         case INSTVARNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_INSTANCE_VAR,
                     new Operand[] { scope.getSelf(), new StringLiteral(((InstVarNode) node).getName()) }));
         case CLASSVARNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_CLASS_VAR,
                     new Operand[]{classVarDefinitionContainer(scope), new StringLiteral(((ClassVarNode) node).getName())}));
         case SUPERNODE: {
             Label undefLabel = scope.getNewLabel();
             Variable tmpVar  = addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_SUPER,
                     new Operand[] { scope.getSelf() }));
             addInstr(scope, BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
             Operand superDefnVal = buildGetArgumentDefinition(((SuperNode) node).getArgsNode(), scope, "super");
             return buildDefnCheckIfThenPaths(scope, undefLabel, superDefnVal);
         }
         case VCALLNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_METHOD,
                     new Operand[] { scope.getSelf(), new StringLiteral(((VCallNode) node).getName()), Boolean.FALSE}));
         case YIELDNODE:
             return buildDefinitionCheck(scope, new BlockGivenInstr(scope.createTemporaryVariable(), getImplicitBlockArg(scope)), "yield");
         case ZSUPERNODE:
             return addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_SUPER,
                     new Operand[] { scope.getSelf() } ));
         case CONSTNODE: {
             Label defLabel = scope.getNewLabel();
             Label doneLabel = scope.getNewLabel();
             Variable tmpVar  = scope.createTemporaryVariable();
             String constName = ((ConstNode) node).getName();
             addInstr(scope, new LexicalSearchConstInstr(tmpVar, startingSearchScope(scope), constName));
             addInstr(scope, BNEInstr.create(tmpVar, UndefinedValue.UNDEFINED, defLabel));
             addInstr(scope, new InheritanceSearchConstInstr(tmpVar, findContainerModule(scope), constName, false)); // SSS FIXME: should this be the current-module var or something else?
             addInstr(scope, BNEInstr.create(tmpVar, UndefinedValue.UNDEFINED, defLabel));
             addInstr(scope, new CopyInstr(tmpVar, manager.getNil()));
             addInstr(scope, new JumpInstr(doneLabel));
             addInstr(scope, new LabelInstr(defLabel));
             addInstr(scope, new CopyInstr(tmpVar, new ConstantStringLiteral("constant")));
             addInstr(scope, new LabelInstr(doneLabel));
             return tmpVar;
         }
         case COLON3NODE: case COLON2NODE: {
             // SSS FIXME: Is there a reason to do this all with low-level IR?
             // Can't this all be folded into a Java method that would be part
             // of the runtime library, which then can be used by buildDefinitionCheck method above?
             // This runtime library would be used both by the interpreter & the compiled code!
 
             final Colon3Node colon = (Colon3Node) node;
             final String name = colon.getName();
             final Variable errInfo = scope.createTemporaryVariable();
 
             // store previous exception for restoration if we rescue something
             addInstr(scope, new GetErrorInfoInstr(errInfo));
 
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     Operand v = colon instanceof Colon2Node ?
                             build(((Colon2Node)colon).getLeftNode(), scope) : new ObjectClass();
 
                     Variable tmpVar = scope.createTemporaryVariable();
                     addInstr(scope, new RuntimeHelperCall(tmpVar, IS_DEFINED_CONSTANT_OR_METHOD, new Operand[] {v, new ConstantStringLiteral(name)}));
                     return tmpVar;
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                  public Operand run() {
                  // Nothing to do -- ignore the exception, and restore stashed error info!
                  addInstr(scope, new RestoreErrorInfoInstr(errInfo));
                  return manager.getNil();
                  }
             };
 
                 // Try verifying definition, and if we get an JumpException exception, process it with the rescue block above
             return protectCodeWithRescue(scope, protectedCode, rescueBlock);
         }
         case FCALLNODE: {
             /* ------------------------------------------------------------------
              * Generate IR for:
              *    r = self/receiver
              *    mc = r.metaclass
              *    return mc.methodBound(meth) ? buildGetArgumentDefn(..) : false
              * ----------------------------------------------------------------- */
             Label undefLabel = scope.getNewLabel();
             Variable tmpVar = addResultInstr(scope, new RuntimeHelperCall(scope.createTemporaryVariable(), IS_DEFINED_METHOD,
                     new Operand[]{scope.getSelf(), new StringLiteral(((FCallNode) node).getName()), Boolean.FALSE}));
             addInstr(scope, BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
             Operand argsCheckDefn = buildGetArgumentDefinition(((FCallNode) node).getArgsNode(), scope, "method");
             return buildDefnCheckIfThenPaths(scope, undefLabel, argsCheckDefn);
         }
         case CALLNODE: {
             final Label undefLabel = scope.getNewLabel();
             final CallNode callNode = (CallNode) node;
             Operand  receiverDefn = buildGetDefinition(callNode.getReceiverNode(), scope);
             addInstr(scope, BEQInstr.create(receiverDefn, manager.getNil(), undefLabel));
 
             // protected main block
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     Variable tmpVar = scope.createTemporaryVariable();
                     addInstr(scope, new RuntimeHelperCall(tmpVar, IS_DEFINED_CALL,
                             new Operand[]{build(callNode.getReceiverNode(), scope), new StringLiteral(callNode.getName())}));
                     return buildDefnCheckIfThenPaths(scope, undefLabel, tmpVar);
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                 public Operand run() { return manager.getNil(); } // Nothing to do if we got an exception
             };
 
             // Try verifying definition, and if we get an exception, throw it out, and return nil
             return protectCodeWithRescue(scope, protectedCode, rescueBlock);
         }
         case ATTRASSIGNNODE: {
             final Label  undefLabel = scope.getNewLabel();
             final AttrAssignNode attrAssign = (AttrAssignNode) node;
             Operand receiverDefn = buildGetDefinition(attrAssign.getReceiverNode(), scope);
             addInstr(scope, BEQInstr.create(receiverDefn, manager.getNil(), undefLabel));
 
             // protected main block
             CodeBlock protectedCode = new CodeBlock() {
                 public Operand run() {
                     /* --------------------------------------------------------------------------
                      * This basically combines checks from CALLNODE and FCALLNODE
                      *
                      * Generate IR for this sequence
                      *
                      *    1. r  = receiver
                      *    2. mc = r.metaClass
                      *    3. v  = mc.getVisibility(methodName)
                      *    4. f  = !v || v.isPrivate? || (v.isProtected? && receiver/self?.kindof(mc.getRealClass))
                      *    5. return !f && mc.methodBound(attrmethod) ? buildGetArgumentDefn(..) : false
                      *
                      * Hide the complexity of instrs 2-4 into a verifyMethodIsPublicAccessible call
                      * which can executely entirely in Java-land.  No reason to expose the guts in IR.
                      * ------------------------------------------------------------------------------ */
                     Variable tmpVar     = scope.createTemporaryVariable();
                     Operand  receiver   = build(attrAssign.getReceiverNode(), scope);
                     addInstr(scope, new RuntimeHelperCall(tmpVar, IS_DEFINED_METHOD,
                             new Operand[] { receiver, new StringLiteral(attrAssign.getName()), Boolean.TRUE }));
                     addInstr(scope, BEQInstr.create(tmpVar, manager.getNil(), undefLabel));
                     Operand argsCheckDefn = buildGetArgumentDefinition(attrAssign.getArgsNode(), scope, "assignment");
                     return buildDefnCheckIfThenPaths(scope, undefLabel, argsCheckDefn);
                 }
             };
 
             // rescue block
             CodeBlock rescueBlock = new CodeBlock() {
                 public Operand run() { return manager.getNil(); } // Nothing to do if we got an exception
             };
 
             // Try verifying definition, and if we get an JumpException exception, process it with the rescue block above
             return protectCodeWithRescue(scope, protectedCode, rescueBlock);
         }
         default:
             return new ConstantStringLiteral("expression");
         }
     }
 
     protected Variable buildDefnCheckIfThenPaths(IRScope s, Label undefLabel, Operand defVal) {
         Label defLabel = s.getNewLabel();
         Variable tmpVar = getValueInTemporaryVariable(s, defVal);
         addInstr(s, new JumpInstr(defLabel));
         addInstr(s, new LabelInstr(undefLabel));
         addInstr(s, new CopyInstr(tmpVar, manager.getNil()));
         addInstr(s, new LabelInstr(defLabel));
         return tmpVar;
     }
 
     protected Variable buildDefinitionCheck(IRScope s, ResultInstr definedInstr, String definedReturnValue) {
         Label undefLabel = s.getNewLabel();
         addInstr(s, (Instr) definedInstr);
         addInstr(s, BEQInstr.create(definedInstr.getResult(), manager.getFalse(), undefLabel));
         return buildDefnCheckIfThenPaths(s, undefLabel, new ConstantStringLiteral(definedReturnValue));
     }
 
     public Operand buildGetArgumentDefinition(final Node node, IRScope s, String type) {
         if (node == null) return new StringLiteral(type);
 
         Operand rv = new ConstantStringLiteral(type);
         boolean failPathReqd = false;
         Label failLabel = s.getNewLabel();
         if (node instanceof ArrayNode) {
             for (int i = 0; i < ((ArrayNode) node).size(); i++) {
                 Node iterNode = ((ArrayNode) node).get(i);
                 Operand def = buildGetDefinition(iterNode, s);
                 if (def == manager.getNil()) { // Optimization!
                     rv = manager.getNil();
                     break;
                 } else if (!def.hasKnownValue()) { // Optimization!
                     failPathReqd = true;
                     addInstr(s, BEQInstr.create(def, manager.getNil(), failLabel));
                 }
             }
         } else {
             Operand def = buildGetDefinition(node, s);
             if (def == manager.getNil()) { // Optimization!
                 rv = manager.getNil();
             } else if (!def.hasKnownValue()) { // Optimization!
                 failPathReqd = true;
                 addInstr(s, BEQInstr.create(def, manager.getNil(), failLabel));
             }
         }
 
         // Optimization!
         return failPathReqd ? buildDefnCheckIfThenPaths(s, failLabel, rv) : rv;
 
     }
 
     public Operand buildDAsgn(final DAsgnNode dasgnNode, IRScope s) {
         // SSS: Looks like we receive the arg in buildBlockArgsAssignment via the IterNode
         // We won't get here for argument receives!  So, buildDasgn is called for
         // assignments to block variables within a block.  As far as the IR is concerned,
         // this is just a simple copy
         int depth = dasgnNode.getDepth();
         Variable arg = s.getLocalVariable(dasgnNode.getName(), depth);
         Operand  value = build(dasgnNode.getValueNode(), s);
         addInstr(s, new CopyInstr(arg, value));
         return value;
 
         // IMPORTANT: The return value of this method is value, not arg!
         //
         // Consider this Ruby code: foo((a = 1), (a = 2))
         //
         // If we return 'value' this will get translated to:
         //    a = 1
         //    a = 2
         //    call("foo", [1,2]) <---- CORRECT
         //
         // If we return 'arg' this will get translated to:
         //    a = 1
         //    a = 2
         //    call("foo", [a,a]) <---- BUGGY
         //
         // This technique only works if 'value' is an immutable value (ex: fixnum) or a variable
         // So, for Ruby code like this:
         //     def foo(x); x << 5; end;
         //     foo(a=[1,2]);
         //     p a
         // we are guaranteed that the value passed into foo and 'a' point to the same object
         // because of the use of copyAndReturnValue method for literal objects.
     }
 
     // Called by defineMethod but called on a new builder so things like ensure block info recording
     // do not get confused.
     private IRMethod defineMethodInner(MethodDefNode defNode, IRMethod method, IRScope parent) {
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             addInstr(method, new TraceInstr(RubyEvent.CALL, method.getName(), method.getFileName(), method.getLineNumber()));
         }
 
         addInstr(method, new ReceiveSelfInstr(method.getSelf()));
 
 
         // These instructions need to be toward the top of the method because they may both be needed for
         // processing optional arguments as in def foo(a = Object).
         // Set %current_scope = <current-scope>
         // Set %current_module = isInstanceMethod ? %self.metaclass : %self
         int nearestScopeDepth = parent.getNearestModuleReferencingScopeDepth();
         addInstr(method, new CopyInstr(method.getCurrentScopeVariable(), new CurrentScope(nearestScopeDepth == -1 ? 1 : nearestScopeDepth)));
         addInstr(method, new CopyInstr(method.getCurrentModuleVariable(), new ScopeModule(nearestScopeDepth == -1 ? 1 : nearestScopeDepth)));
 
         // Build IR for arguments (including the block arg)
         receiveMethodArgs(defNode.getArgsNode(), method);
 
         // Thread poll on entry to method
         addInstr(method, new ThreadPollInstr());
 
         // Build IR for body
         Operand rv = newIRBuilder(manager).build(defNode.getBodyNode(), method);
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             addInstr(method, new TraceInstr(RubyEvent.RETURN, method.getName(), method.getFileName(), -1));
         }
 
         if (rv != null) addInstr(method, new ReturnInstr(rv));
 
         // If the method can receive non-local returns
         if (method.canReceiveNonlocalReturns()) handleNonlocalReturnInMethod(method);
 
         return method;
     }
 
     private IRMethod defineNewMethod(MethodDefNode defNode, IRScope parent, boolean isInstanceMethod) {
         IRMethod method = new IRMethod(manager, parent, defNode.getName(), isInstanceMethod, defNode.getPosition().getLine(), defNode.getScope());
 
         return newIRBuilder(manager).defineMethodInner(defNode, method, parent);
     }
 
     public Operand buildDefn(MethodDefNode node, IRScope s) { // Instance method
         IRMethod method = defineNewMethod(node, s, true);
         addInstr(s, new DefineInstanceMethodInstr(method));
-        return new Symbol(method.getName());
+        // FIXME: Method name should save encoding
+        return new Symbol(method.getName(), ASCIIEncoding.INSTANCE);
     }
 
     public Operand buildDefs(DefsNode node, IRScope s) { // Class method
         Operand container =  build(node.getReceiverNode(), s);
         IRMethod method = defineNewMethod(node, s, false);
         addInstr(s, new DefineClassMethodInstr(container, method));
-        return new Symbol(method.getName());
+        // FIXME: Method name should save encoding
+        return new Symbol(method.getName(), ASCIIEncoding.INSTANCE);
     }
 
     protected LocalVariable getArgVariable(IRScope s, String name, int depth) {
         // For non-loops, this name will override any name that exists in outer scopes
         return s instanceof IRFor ? s.getLocalVariable(name, depth) : s.getNewLocalVariable(name, 0);
     }
 
     private void addArgReceiveInstr(IRScope s, Variable v, int argIndex, boolean post, int numPreReqd, int numPostRead) {
         if (post) addInstr(s, new ReceivePostReqdArgInstr(v, argIndex, numPreReqd, numPostRead));
         else addInstr(s, new ReceivePreReqdArgInstr(v, argIndex));
     }
 
     public void receiveRequiredArg(Node node, IRScope s, int argIndex, boolean post, int numPreReqd, int numPostRead) {
         switch (node.getNodeType()) {
             case ARGUMENTNODE: {
                 ArgumentNode a = (ArgumentNode)node;
                 String argName = a.getName();
                 if (s instanceof IRMethod) ((IRMethod)s).addArgDesc(IRMethodArgs.ArgType.req, argName);
                 // Ignore duplicate "_" args in blocks
                 // (duplicate _ args are named "_$0")
                 if (!argName.equals("_$0")) {
                     addArgReceiveInstr(s, s.getNewLocalVariable(argName, 0), argIndex, post, numPreReqd, numPostRead);
                 }
                 break;
             }
             case MULTIPLEASGN19NODE: {
                 MultipleAsgn19Node childNode = (MultipleAsgn19Node) node;
                 Variable v = s.createTemporaryVariable();
                 addArgReceiveInstr(s, v, argIndex, post, numPreReqd, numPostRead);
                 if (s instanceof IRMethod) ((IRMethod)s).addArgDesc(IRMethodArgs.ArgType.req, "");
                 Variable tmp = s.createTemporaryVariable();
                 addInstr(s, new ToAryInstr(tmp, v));
                 buildMultipleAsgn19Assignment(childNode, s, tmp, null);
                 break;
             }
             default: throw new NotCompilableException("Can't build assignment node: " + node);
         }
     }
 
     private void receiveClosureArg(BlockArgNode blockVarNode, IRScope s) {
         Variable blockVar = null;
         if (blockVarNode != null) {
             String blockArgName = blockVarNode.getName();
             blockVar = s.getNewLocalVariable(blockArgName, 0);
             if (s instanceof IRMethod) ((IRMethod)s).addArgDesc(IRMethodArgs.ArgType.block, blockArgName);
             addInstr(s, new ReceiveClosureInstr(blockVar));
         }
 
         // In addition, store the block argument in an implicit block variable
         if (s instanceof IRMethod) {
             Variable implicitBlockArg = (Variable)getImplicitBlockArg(s);
             if (blockVar == null) addInstr(s, new ReceiveClosureInstr(implicitBlockArg));
             else addInstr(s, new CopyInstr(implicitBlockArg, blockVar));
         }
     }
 
     protected void receiveNonBlockArgs(final ArgsNode argsNode, IRScope s) {
         final int numPreReqd = argsNode.getPreCount();
         final int numPostReqd = argsNode.getPostCount();
         final int required = argsNode.getRequiredArgsCount(); // numPreReqd + numPostReqd
         int opt = argsNode.getOptionalArgsCount();
         int rest = argsNode.getRestArg();
 
         s.getStaticScope().setArities(required, opt, rest);
         KeywordRestArgNode keyRest = argsNode.getKeyRest();
 
         // For closures, we don't need the check arity call
         if (s instanceof IRMethod) {
             // Expensive to do this explicitly?  But, two advantages:
             // (a) on inlining, we'll be able to get rid of these checks in almost every case.
             // (b) compiler to bytecode will anyway generate this and this is explicit.
             // For now, we are going explicit instruction route.
             // But later, perhaps can make this implicit in the method setup preamble?
 
             addInstr(s, new CheckArityInstr(required, opt, rest, argsNode.hasKwargs(),
                     keyRest == null ? -1 : keyRest.getIndex()));
         } else if (s instanceof IRClosure && argsNode.hasKwargs()) {
             // FIXME: This is added to check for kwargs correctness but bypass regular correctness.
             // Any other arity checking currently happens within Java code somewhere (RubyProc.call?)
             addInstr(s, new CheckArityInstr(required, opt, rest, argsNode.hasKwargs(),
                     keyRest == null ? -1 : keyRest.getIndex()));
         }
 
         // Other args begin at index 0
         int argIndex = 0;
 
         // Pre(-opt and rest) required args
         ListNode preArgs = argsNode.getPre();
         for (int i = 0; i < numPreReqd; i++, argIndex++) {
             receiveRequiredArg(preArgs.get(i), s, argIndex, false, -1, -1);
         }
 
         // Fixup opt/rest
         opt = opt > 0 ? opt : 0;
         rest = rest > -1 ? 1 : 0;
 
         // Now for opt args
         if (opt > 0) {
             ListNode optArgs = argsNode.getOptArgs();
             for (int j = 0; j < opt; j++, argIndex++) {
                 // Jump to 'l' if this arg is not null.  If null, fall through and build the default value!
                 Label l = s.getNewLabel();
                 OptArgNode n = (OptArgNode)optArgs.get(j);
                 String argName = n.getName();
                 Variable av = s.getNewLocalVariable(argName, 0);
                 if (s instanceof IRMethod) ((IRMethod)s).addArgDesc(IRMethodArgs.ArgType.opt, argName);
                 // You need at least required+j+1 incoming args for this opt arg to get an arg at all
                 addInstr(s, new ReceiveOptArgInstr(av, required, numPreReqd, j));
                 addInstr(s, BNEInstr.create(av, UndefinedValue.UNDEFINED, l)); // if 'av' is not undefined, go to default
                 build(n.getValue(), s);
                 addInstr(s, new LabelInstr(l));
             }
         }
 
         // Rest arg
         if (rest > 0) {
             // Consider: def foo(*); .. ; end
             // For this code, there is no argument name available from the ruby code.
             // So, we generate an implicit arg name
             String argName = argsNode.getRestArgNode().getName();
             if (s instanceof IRMethod) ((IRMethod)s).addArgDesc(IRMethodArgs.ArgType.rest, argName == null ? "" : argName);
             argName = (argName == null || argName.equals("")) ? "*" : argName;
 
             // You need at least required+opt+1 incoming args for the rest arg to get any args at all
             // If it is going to get something, then it should ignore required+opt args from the beginning
             // because they have been accounted for already.
             addInstr(s, new ReceiveRestArgInstr(s.getNewLocalVariable(argName, 0), required + opt, argIndex));
         }
 
         // Post(-opt and rest) required args
         ListNode postArgs = argsNode.getPost();
         for (int i = 0; i < numPostReqd; i++) {
             receiveRequiredArg(postArgs.get(i), s, i, true, numPreReqd, numPostReqd);
         }
     }
 
     protected void receiveBlockArg(final ArgsNode argsNode, IRScope s) {
         // For methods, we always receive it (implicitly, if the block arg is not explicit)
         // For closures, only if it is explicitly present
         BlockArgNode blockArg = argsNode.getBlock();
         if (s instanceof IRMethod || blockArg != null) receiveClosureArg(blockArg, s);
     }
 
     public void receiveArgs(final ArgsNode argsNode, IRScope s) {
         // 1.9 pre, opt, rest, post args
         receiveNonBlockArgs(argsNode, s);
 
         // 2.0 keyword args
         ListNode keywords = argsNode.getKeywords();
         int required = argsNode.getRequiredArgsCount();
         if (keywords != null) {
             for (Node knode : keywords.childNodes()) {
                 KeywordArgNode kwarg = (KeywordArgNode)knode;
                 AssignableNode kasgn = kwarg.getAssignable();
                 String argName = ((INameNode) kasgn).getName();
                 Variable av = s.getNewLocalVariable(argName, 0);
                 Label l = s.getNewLabel();
                 if (s instanceof IRMethod) ((IRMethod)s).addArgDesc(IRMethodArgs.ArgType.key, argName);
                 addInstr(s, new ReceiveKeywordArgInstr(av, argName, required));
                 addInstr(s, BNEInstr.create(av, UndefinedValue.UNDEFINED, l)); // if 'av' is not undefined, we are done
 
                 // Required kwargs have no value and check_arity will throw if they are not provided.
                 if (kasgn.getValueNode().getNodeType() != NodeType.REQUIRED_KEYWORD_ARGUMENT_VALUE) {
                     build(kasgn, s);
                 } else {
                     addInstr(s, new RaiseRequiredKeywordArgumentError(argName));
                 }
                 addInstr(s, new LabelInstr(l));
             }
         }
 
         // 2.0 keyword rest arg
         KeywordRestArgNode keyRest = argsNode.getKeyRest();
         if (keyRest != null) {
             String argName = keyRest.getName();
             Variable av = s.getNewLocalVariable(argName, 0);
             if (s instanceof IRMethod) ((IRMethod)s).addArgDesc(IRMethodArgs.ArgType.keyrest, argName);
             addInstr(s, new ReceiveKeywordRestArgInstr(av, required));
         }
 
         // Block arg
         receiveBlockArg(argsNode, s);
     }
 
     // This method is called to build arguments
     public void buildArgsMasgn(Node node, IRScope s, Operand argsArray, boolean isMasgnRoot, int preArgsCount, int postArgsCount, int index, boolean isSplat) {
         Variable v;
         switch (node.getNodeType()) {
             case DASGNNODE: {
                 DAsgnNode dynamicAsgn = (DAsgnNode) node;
                 v = getArgVariable(s, dynamicAsgn.getName(), dynamicAsgn.getDepth());
                 if (isSplat) addInstr(s, new RestArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                 else addInstr(s, new ReqdArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                 break;
             }
             case LOCALASGNNODE: {
                 LocalAsgnNode localVariable = (LocalAsgnNode) node;
                 v = getArgVariable(s, localVariable.getName(), localVariable.getDepth());
                 if (isSplat) addInstr(s, new RestArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                 else addInstr(s, new ReqdArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                 break;
             }
             case MULTIPLEASGN19NODE: {
                 MultipleAsgn19Node childNode = (MultipleAsgn19Node) node;
                 if (!isMasgnRoot) {
                     v = s.createTemporaryVariable();
                     if (isSplat) addInstr(s, new RestArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                     else addInstr(s, new ReqdArgMultipleAsgnInstr(v, argsArray, preArgsCount, postArgsCount, index));
                     Variable tmp = s.createTemporaryVariable();
                     addInstr(s, new ToAryInstr(tmp, v));
                     argsArray = tmp;
                 }
                 // Build
                 buildMultipleAsgn19Assignment(childNode, s, argsArray, null);
                 break;
             }
             default:
                 throw new NotCompilableException("Shouldn't get here: " + node);
         }
     }
 
     // SSS: This method is called both for regular multiple assignment as well as argument passing
     //
     // Ex: a,b,*c=v  is a regular assignment and in this case, the "values" operand will be non-null
     // Ex: { |a,b,*c| ..} is the argument passing case
     public void buildMultipleAsgn19Assignment(final MultipleAsgn19Node multipleAsgnNode, IRScope s, Operand argsArray, Operand values) {
         final ListNode masgnPre = multipleAsgnNode.getPre();
 
         // Build assignments for specific named arguments
         int i = 0;
         if (masgnPre != null) {
             for (Node an: masgnPre.childNodes()) {
                 if (values == null) {
                     buildArgsMasgn(an, s, argsArray, false, -1, -1, i, false);
                 } else {
                     Variable rhsVal = s.createTemporaryVariable();
                     addInstr(s, new ReqdArgMultipleAsgnInstr(rhsVal, values, i));
                     buildAssignment(an, s, rhsVal);
                 }
                 i++;
             }
         }
 
         // Build an assignment for a splat, if any, with the rest of the operands!
         Node restNode = multipleAsgnNode.getRest();
         int postArgsCount = multipleAsgnNode.getPostCount();
         if (restNode != null && !(restNode instanceof StarNode)) {
             if (values == null) {
                 buildArgsMasgn(restNode, s, argsArray, false, i, postArgsCount, 0, true); // rest of the argument array!
             } else {
                 Variable rhsVal = s.createTemporaryVariable();
                 addInstr(s, new RestArgMultipleAsgnInstr(rhsVal, values, i, postArgsCount, 0));
                 buildAssignment(restNode, s, rhsVal); // rest of the argument array!
             }
         }
 
         // Build assignments for rest of the operands
         final ListNode masgnPost = multipleAsgnNode.getPost();
         if (masgnPost != null) {
             int j = 0;
             for (Node an: masgnPost.childNodes()) {
                 if (values == null) {
                     buildArgsMasgn(an, s, argsArray, false, i, postArgsCount, j, false);
                 } else {
                     Variable rhsVal = s.createTemporaryVariable();
                     addInstr(s, new ReqdArgMultipleAsgnInstr(rhsVal, values, i, postArgsCount, j));  // Fetch from the end
                     buildAssignment(an, s, rhsVal);
                 }
                 j++;
             }
         }
     }
 
     private void handleBreakAndReturnsInLambdas(IRClosure s) {
         Label rEndLabel   = s.getNewLabel();
         Label rescueLabel = Label.getGlobalEnsureBlockLabel();
 
         // protect the entire body as it exists now with the global ensure block
         addInstrAtBeginning(s, new ExceptionRegionStartMarkerInstr(rescueLabel));
         addInstr(s, new ExceptionRegionEndMarkerInstr());
 
         // Receive exceptions (could be anything, but the handler only processes IRBreakJumps)
         addInstr(s, new LabelInstr(rescueLabel));
         Variable exc = s.createTemporaryVariable();
         addInstr(s, new ReceiveJRubyExceptionInstr(exc));
 
         // Handle break using runtime helper
         // --> IRRuntimeHelpers.handleBreakAndReturnsInLambdas(context, scope, bj, blockType)
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new RuntimeHelperCall(ret, RuntimeHelperCall.Methods.HANDLE_BREAK_AND_RETURNS_IN_LAMBDA, new Operand[]{exc} ));
         addInstr(s, new ReturnInstr(ret));
 
         // End
         addInstr(s, new LabelInstr(rEndLabel));
     }
 
     public void receiveMethodArgs(final ArgsNode argsNode, IRScope s) {
         receiveArgs(argsNode, s);
     }
 
     public void receiveBlockArgs(final IterNode node, IRScope s) {
         Node args = node.getVarNode();
         if (args instanceof ArgsNode) { // regular blocks
             ((IRClosure)s).setParameterList(Helpers.encodeParameterList((ArgsNode) args).split(";"));
             receiveArgs((ArgsNode)args, s);
         } else  {
             // for loops -- reuse code in IRBuilder:buildBlockArgsAssignment
             buildBlockArgsAssignment(args, s, null, 0, false);
         }
     }
 
     public Operand buildDot(final DotNode dotNode, IRScope s) {
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BuildRangeInstr(res, build(dotNode.getBeginNode(), s), build(dotNode.getEndNode(), s), dotNode.isExclusive()));
         return res;
     }
 
     private Operand dynamicPiece(Node pieceNode, IRScope s) {
         Operand piece = build(pieceNode, s);
 
         return piece == null ? manager.getNil() : piece;
     }
 
     public Operand buildDRegexp(DRegexpNode dregexpNode, IRScope s) {
         List<Operand> strPieces = new ArrayList<>();
         for (Node n : dregexpNode.childNodes()) {
             strPieces.add(dynamicPiece(n, s));
         }
 
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BuildDynRegExpInstr(res, strPieces, dregexpNode.getOptions()));
         return res;
     }
 
     public Operand buildDStr(DStrNode dstrNode, IRScope s) {
         List<Operand> strPieces = new ArrayList<>();
         for (Node n : dstrNode.childNodes()) {
             strPieces.add(dynamicPiece(n, s));
         }
 
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BuildCompoundStringInstr(res, strPieces, dstrNode.getEncoding()));
         return copyAndReturnValue(s, res);
     }
 
     public Operand buildDSymbol(DSymbolNode node, IRScope s) {
         List<Operand> strPieces = new ArrayList<>();
         for (Node n : node.childNodes()) {
             strPieces.add(dynamicPiece(n, s));
         }
 
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BuildCompoundStringInstr(res, strPieces, node.getEncoding()));
         return copyAndReturnValue(s, new DynamicSymbol(res));
     }
 
     public Operand buildDVar(DVarNode node, IRScope s) {
         return s.getLocalVariable(node.getName(), node.getDepth());
     }
 
     public Operand buildDXStr(final DXStrNode dstrNode, IRScope s) {
         List<Operand> strPieces = new ArrayList<>();
         for (Node nextNode : dstrNode.childNodes()) {
             strPieces.add(dynamicPiece(nextNode, s));
         }
 
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BacktickInstr(res, strPieces));
         return res;
     }
 
     /* ****************************************************************
      * Consider the ensure-protected ruby code below:
 
            begin
              .. protected body ..
            ensure
              .. eb code
            end
 
        This ruby code is effectively rewritten into the following ruby code
 
           begin
             .. protected body ..
             .. copy of ensure body code ..
           rescue <any-exception-or-error> => e
             .. ensure body code ..
             raise e
           end
 
       which in IR looks like this:
 
           L1:
             Exception region start marker_1 (protected by L10)
             ... IR for protected body ...
             Exception region end marker_1
           L2:
             Exception region start marker_2 (protected by whichever block handles exceptions for ensure body)
             .. copy of IR for ensure block ..
             Exception region end marker_2
             jump L3
           L10:          <----- dummy rescue block
             e = recv_exception
             .. IR for ensure block ..
             throw e
           L3:
 
      * ****************************************************************/
     public Operand buildEnsureNode(EnsureNode ensureNode, IRScope s) {
         Node bodyNode = ensureNode.getBodyNode();
 
         // ------------ Build the body of the ensure block ------------
         //
         // The ensure code is built first so that when the protected body is being built,
         // the ensure code can be cloned at break/next/return sites in the protected body.
 
         // Push a new ensure block node onto the stack of ensure bodies being built
         // The body's instructions are stashed and emitted later.
         EnsureBlockInfo ebi = new EnsureBlockInfo(s,
             (bodyNode instanceof RescueNode) ? (RescueNode)bodyNode : null,
             getCurrentLoop(),
             activeRescuers.peek());
 
         ensureBodyBuildStack.push(ebi);
         Operand ensureRetVal = (ensureNode.getEnsureNode() == null) ? manager.getNil() : build(ensureNode.getEnsureNode(), s);
         ensureBodyBuildStack.pop();
 
         // ------------ Build the protected region ------------
         activeEnsureBlockStack.push(ebi);
 
         // Start of protected region
         addInstr(s, new LabelInstr(ebi.regionStart));
         addInstr(s, new ExceptionRegionStartMarkerInstr(ebi.dummyRescueBlockLabel));
         activeRescuers.push(ebi.dummyRescueBlockLabel);
 
         // Generate IR for code being protected
         Operand rv = bodyNode instanceof RescueNode ? buildRescueInternal((RescueNode) bodyNode, s, ebi) : build(bodyNode, s);
 
         // end of protected region
         addInstr(s, new ExceptionRegionEndMarkerInstr());
         activeRescuers.pop();
 
         // Clone the ensure body and jump to the end.
         // Dont bother if the protected body ended in a return.
         if (rv != U_NIL && !(bodyNode instanceof RescueNode)) {
             ebi.cloneIntoHostScope(this, s);
             addInstr(s, new JumpInstr(ebi.end));
         }
 
         // Pop the current ensure block info node
         activeEnsureBlockStack.pop();
 
         // ------------ Emit the ensure body alongwith dummy rescue block ------------
         // Now build the dummy rescue block that:
         // * catches all exceptions thrown by the body
         Variable exc = s.createTemporaryVariable();
         addInstr(s, new LabelInstr(ebi.dummyRescueBlockLabel));
         addInstr(s, new ReceiveJRubyExceptionInstr(exc));
 
         // Now emit the ensure body's stashed instructions
         ebi.emitBody(this, s);
 
         // 1. Ensure block has no explicit return => the result of the entire ensure expression is the result of the protected body.
         // 2. Ensure block has an explicit return => the result of the protected body is ignored.
         // U_NIL => there was a return from within the ensure block!
         if (ensureRetVal == U_NIL) rv = U_NIL;
 
         // Return (rethrow exception/end)
         // rethrows the caught exception from the dummy ensure block
         addInstr(s, new ThrowExceptionInstr(exc));
 
         // End label for the exception region
         addInstr(s, new LabelInstr(ebi.end));
 
         return rv;
     }
 
     public Operand buildEvStr(EvStrNode node, IRScope s) {
         return new AsString(build(node.getBody(), s));
     }
 
     public Operand buildFalse() {
         return manager.getFalse();
     }
 
     public Operand buildFCall(FCallNode fcallNode, IRScope s) {
         Node          callArgsNode = fcallNode.getArgsNode();
         List<Operand> args         = setupCallArgs(callArgsNode, s);
         Operand       block        = setupCallClosure(fcallNode.getIterNode(), s);
         Variable      callResult   = s.createTemporaryVariable();
         CallInstr     callInstr    = CallInstr.create(CallType.FUNCTIONAL, callResult, new MethAddr(fcallNode.getName()), s.getSelf(), args.toArray(new Operand[args.size()]), block);
         receiveBreakException(s, block, callInstr);
         return callResult;
     }
 
     private Operand setupCallClosure(Node node, IRScope s) {
         if (node == null) return null;
 
         switch (node.getNodeType()) {
             case ITERNODE:
                 return build(node, s);
             case BLOCKPASSNODE:
                 return build(((BlockPassNode)node).getBodyNode(), s);
             default:
                 throw new NotCompilableException("ERROR: Encountered a method with a non-block, non-blockpass iter node at: " + node);
         }
     }
 
     public Operand buildFixnum(FixnumNode node) {
         return new Fixnum(node.getValue());
     }
 
     public Operand buildFlip(FlipNode flipNode, IRScope s) {
         /* ----------------------------------------------------------------------
          * Consider a simple 2-state (s1, s2) FSM with the following transitions:
          *
          *     new_state(s1, F) = s1
          *     new_state(s1, T) = s2
          *     new_state(s2, F) = s2
          *     new_state(s2, T) = s1
          *
          * Here is the pseudo-code for evaluating the flip-node.
          * Let 'v' holds the value of the current state.
          *
          *    1. if (v == 's1') f1 = eval_condition(s1-condition); v = new_state(v, f1); ret = f1
          *    2. if (v == 's2') f2 = eval_condition(s2-condition); v = new_state(v, f2); ret = true
          *    3. return ret
          *
          * For exclusive flip conditions, line 2 changes to:
          *    2. if (!f1 && (v == 's2')) f2 = eval_condition(s2-condition); v = new_state(v, f2)
          *
          * In IR code below, we are representing the two states as 1 and 2.  Any
          * two values are good enough (even true and false), but 1 and 2 is simple
          * enough and also makes the IR output readable
          * ---------------------------------------------------------------------- */
 
         Fixnum s1 = new Fixnum((long)1);
         Fixnum s2 = new Fixnum((long)2);
 
         // Create a variable to hold the flip state
         IRScope nearestNonClosure = s.getNearestFlipVariableScope();
         Variable flipState = nearestNonClosure.getNewFlipStateVariable();
         nearestNonClosure.initFlipStateVariable(flipState, s1);
         if (s instanceof IRClosure) {
             // Clone the flip variable to be usable at the proper-depth.
             int n = 0;
             IRScope x = s;
             while (!x.isFlipScope()) {
                 n++;
                 x = x.getLexicalParent();
             }
             if (n > 0) flipState = ((LocalVariable)flipState).cloneForDepth(n);
         }
 
         // Variables and labels needed for the code
         Variable returnVal = s.createTemporaryVariable();
         Label    s2Label   = s.getNewLabel();
         Label    doneLabel = s.getNewLabel();
 
         // Init
         addInstr(s, new CopyInstr(returnVal, manager.getFalse()));
 
         // Are we in state 1?
         addInstr(s, BNEInstr.create(flipState, s1, s2Label));
 
         // ----- Code for when we are in state 1 -----
         Operand s1Val = build(flipNode.getBeginNode(), s);
         addInstr(s, BNEInstr.create(s1Val, manager.getTrue(), s2Label));
 
         // s1 condition is true => set returnVal to true & move to state 2
         addInstr(s, new CopyInstr(returnVal, manager.getTrue()));
         addInstr(s, new CopyInstr(flipState, s2));
 
         // Check for state 2
         addInstr(s, new LabelInstr(s2Label));
 
         // For exclusive ranges/flips, we dont evaluate s2's condition if s1's condition was satisfied
         if (flipNode.isExclusive()) addInstr(s, BEQInstr.create(returnVal, manager.getTrue(), doneLabel));
 
         // Are we in state 2?
         addInstr(s, BNEInstr.create(flipState, s2, doneLabel));
 
         // ----- Code for when we are in state 2 -----
         Operand s2Val = build(flipNode.getEndNode(), s);
         addInstr(s, new CopyInstr(returnVal, manager.getTrue()));
         addInstr(s, BNEInstr.create(s2Val, manager.getTrue(), doneLabel));
 
         // s2 condition is true => move to state 1
         addInstr(s, new CopyInstr(flipState, s1));
 
         // Done testing for s1's and s2's conditions.
         // returnVal will have the result of the flip condition
         addInstr(s, new LabelInstr(doneLabel));
 
         return returnVal;
     }
 
     public Operand buildFloat(FloatNode node) {
         // SSS: Since flaot literals are effectively interned objects, no need to copyAndReturnValue(...)
         // Or is this a premature optimization?
         return new Float(node.getValue());
     }
 
     public Operand buildFor(ForNode forNode, IRScope s) {
         Variable result = s.createTemporaryVariable();
         Operand  receiver = build(forNode.getIterNode(), s);
         Operand  forBlock = buildForIter(forNode, s);
         CallInstr callInstr = new CallInstr(CallType.NORMAL, result, new MethAddr("each"), receiver, NO_ARGS, forBlock);
         receiveBreakException(s, forBlock, callInstr);
 
         return result;
     }
 
     public Operand buildForIter(final ForNode forNode, IRScope s) {
             // Create a new closure context
         IRClosure closure = new IRFor(manager, s, forNode.getPosition().getLine(), forNode.getScope(), Arity.procArityOf(forNode.getVarNode()), forNode.getArgumentType());
 
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder forBuilder = newIRBuilder(manager);
 
             // Receive self
         forBuilder.addInstr(closure, new ReceiveSelfInstr(closure.getSelf()));
 
             // Build args
         Node varNode = forNode.getVarNode();
         if (varNode != null && varNode.getNodeType() != null) forBuilder.receiveBlockArgs(forNode, closure);
 
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         forBuilder.addInstr(closure, new CopyInstr(closure.getCurrentScopeVariable(), new CurrentScope(0)));
         forBuilder.addInstr(closure, new CopyInstr(closure.getCurrentModuleVariable(), new ScopeModule(0)));
 
         // Thread poll on entry of closure
         forBuilder.addInstr(closure, new ThreadPollInstr());
 
             // Start label -- used by redo!
         forBuilder.addInstr(closure, new LabelInstr(closure.startLabel));
 
             // Build closure body and return the result of the closure
         Operand closureRetVal = forNode.getBodyNode() == null ? manager.getNil() : forBuilder.build(forNode.getBodyNode(), closure);
         if (closureRetVal != U_NIL) { // can be null if the node is an if node with returns in both branches.
             forBuilder.addInstr(closure, new ReturnInstr(closureRetVal));
         }
 
         return new WrappedIRClosure(s.getSelf(), closure);
     }
 
     public Operand buildGlobalAsgn(GlobalAsgnNode globalAsgnNode, IRScope s) {
         Operand value = build(globalAsgnNode.getValueNode(), s);
         addInstr(s, new PutGlobalVarInstr(globalAsgnNode.getName(), value));
         return value;
     }
 
     public Operand buildGlobalVar(GlobalVarNode node, IRScope s) {
         return addResultInstr(s, new GetGlobalVariableInstr(s.createTemporaryVariable(), node.getName()));
     }
 
     public Operand buildHash(HashNode hashNode, IRScope s) {
         List<KeyValuePair<Operand, Operand>> args = new ArrayList<>();
         Operand splatKeywordArgument = null;
 
         for (KeyValuePair<Node, Node> pair: hashNode.getPairs()) {
             Node key = pair.getKey();
             Operand keyOperand;
 
             if (key == null) { // splat kwargs [e.g. foo(a: 1, **splat)] key is null and will be in last pair of hash
                 splatKeywordArgument = build(pair.getValue(), s);
                 break;
             } else {
                keyOperand = build(key, s);
             }
 
             args.add(new KeyValuePair<>(keyOperand, build(pair.getValue(), s)));
         }
 
         if (splatKeywordArgument != null) { // splat kwargs merge with any explicit kwargs
             Variable tmp = s.createTemporaryVariable();
             s.addInstr(new RuntimeHelperCall(tmp, MERGE_KWARGS, new Operand[] { splatKeywordArgument, new Hash(args)}));
             return tmp;
         } else {
             return copyAndReturnValue(s, new Hash(args));
         }
     }
 
     // Translate "r = if (cond); .. thenbody ..; else; .. elsebody ..; end" to
     //
     //     v = -- build(cond) --
     //     BEQ(v, FALSE, L1)
     //     r = -- build(thenbody) --
     //     jump L2
     // L1:
     //     r = -- build(elsebody) --
     // L2:
     //     --- r is the result of the if expression --
     //
     public Operand buildIf(final IfNode ifNode, IRScope s) {
         Node actualCondition = skipOverNewlines(s, ifNode.getCondition());
 
         Variable result;
         Label    falseLabel = s.getNewLabel();
         Label    doneLabel  = s.getNewLabel();
         Operand  thenResult;
         addInstr(s, BEQInstr.create(build(actualCondition, s), manager.getFalse(), falseLabel));
 
         boolean thenNull = false;
         boolean elseNull = false;
         boolean thenUnil = false;
         boolean elseUnil = false;
 
         // Build the then part of the if-statement
         if (ifNode.getThenBody() != null) {
             thenResult = build(ifNode.getThenBody(), s);
             if (thenResult != U_NIL) { // thenResult can be U_NIL if then-body ended with a return!
                 // SSS FIXME: Can look at the last instr and short-circuit this jump if it is a break rather
                 // than wait for dead code elimination to do it
                 result = getValueInTemporaryVariable(s, thenResult);
                 addInstr(s, new JumpInstr(doneLabel));
             } else {
                 result = s.createTemporaryVariable();
                 thenUnil = true;
             }
         } else {
             thenNull = true;
             result = addResultInstr(s, new CopyInstr(s.createTemporaryVariable(), manager.getNil()));
             addInstr(s, new JumpInstr(doneLabel));
         }
 
         // Build the else part of the if-statement
         addInstr(s, new LabelInstr(falseLabel));
         if (ifNode.getElseBody() != null) {
             Operand elseResult = build(ifNode.getElseBody(), s);
             // elseResult can be U_NIL if then-body ended with a return!
             if (elseResult != U_NIL) {
                 addInstr(s, new CopyInstr(result, elseResult));
             } else {
                 elseUnil = true;
             }
         } else {
             elseNull = true;
             addInstr(s, new CopyInstr(result, manager.getNil()));
         }
 
         if (thenNull && elseNull) {
             addInstr(s, new LabelInstr(doneLabel));
             return manager.getNil();
         } else if (thenUnil && elseUnil) {
             return U_NIL;
         } else {
             addInstr(s, new LabelInstr(doneLabel));
             return result;
         }
     }
 
     public Operand buildInstAsgn(final InstAsgnNode instAsgnNode, IRScope s) {
         Operand val = build(instAsgnNode.getValueNode(), s);
         // NOTE: if 's' happens to the a class, this is effectively an assignment of a class instance variable
         addInstr(s, new PutFieldInstr(s.getSelf(), instAsgnNode.getName(), val));
         return val;
     }
 
     public Operand buildInstVar(InstVarNode node, IRScope s) {
         return addResultInstr(s, new GetFieldInstr(s.createTemporaryVariable(), s.getSelf(), node.getName()));
     }
 
     public Operand buildIter(final IterNode iterNode, IRScope s) {
         IRClosure closure = new IRClosure(manager, s, iterNode.getPosition().getLine(), iterNode.getScope(), Arity.procArityOf(iterNode.getVarNode()), iterNode.getArgumentType());
 
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder closureBuilder = newIRBuilder(manager);
 
         // Receive self
         closureBuilder.addInstr(closure, new ReceiveSelfInstr(closure.getSelf()));
 
         // Build args
         if (iterNode.getVarNode().getNodeType() != null) closureBuilder.receiveBlockArgs(iterNode, closure);
 
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         closureBuilder.addInstr(closure, new CopyInstr(closure.getCurrentScopeVariable(), new CurrentScope(0)));
         closureBuilder.addInstr(closure, new CopyInstr(closure.getCurrentModuleVariable(), new ScopeModule(0)));
 
         // Thread poll on entry of closure
         closureBuilder.addInstr(closure, new ThreadPollInstr());
 
         // start label -- used by redo!
         closureBuilder.addInstr(closure, new LabelInstr(closure.startLabel));
 
         // Build closure body and return the result of the closure
         Operand closureRetVal = iterNode.getBodyNode() == null ? manager.getNil() : closureBuilder.build(iterNode.getBodyNode(), closure);
         if (closureRetVal != U_NIL) { // can be U_NIL if the node is an if node with returns in both branches.
             closureBuilder.addInstr(closure, new ReturnInstr(closureRetVal));
         }
 
         // Always add break/return handling even though this
         // is only required for lambdas, but we don't know at this time,
         // if this is a lambda or not.
         //
         // SSS FIXME: At a later time, see if we can optimize this and
         // do this on demand.
         closureBuilder.handleBreakAndReturnsInLambdas(closure);
 
         return new WrappedIRClosure(s.getSelf(), closure);
     }
 
     public Operand buildLiteral(LiteralNode literalNode, IRScope s) {
         return copyAndReturnValue(s, new StringLiteral(literalNode.getName()));
     }
 
     public Operand buildLocalAsgn(LocalAsgnNode localAsgnNode, IRScope s) {
         Variable var  = s.getLocalVariable(localAsgnNode.getName(), localAsgnNode.getDepth());
         Operand value = build(localAsgnNode.getValueNode(), s);
         addInstr(s, new CopyInstr(var, value));
         return value;
 
         // IMPORTANT: The return value of this method is value, not var!
         //
         // Consider this Ruby code: foo((a = 1), (a = 2))
         //
         // If we return 'value' this will get translated to:
         //    a = 1
         //    a = 2
         //    call("foo", [1,2]) <---- CORRECT
         //
         // If we return 'var' this will get translated to:
         //    a = 1
         //    a = 2
         //    call("foo", [a,a]) <---- BUGGY
         //
         // This technique only works if 'value' is an immutable value (ex: fixnum) or a variable
         // So, for Ruby code like this:
         //     def foo(x); x << 5; end;
         //     foo(a=[1,2]);
         //     p a
         // we are guaranteed that the value passed into foo and 'a' point to the same object
         // because of the use of copyAndReturnValue method for literal objects.
     }
 
     public Operand buildLocalVar(LocalVarNode node, IRScope s) {
         return s.getLocalVariable(node.getName(), node.getDepth());
     }
 
     public Operand buildMatch(MatchNode matchNode, IRScope s) {
         Operand regexp = build(matchNode.getRegexpNode(), s);
 
         return addResultInstr(s, new MatchInstr(s.createTemporaryVariable(), regexp));
     }
 
     public Operand buildMatch2(Match2Node matchNode, IRScope s) {
         Operand receiver = build(matchNode.getReceiverNode(), s);
         Operand value    = build(matchNode.getValueNode(), s);
         Variable result  = s.createTemporaryVariable();
         addInstr(s, new Match2Instr(result, receiver, value));
         if (matchNode instanceof Match2CaptureNode) {
             Match2CaptureNode m2c = (Match2CaptureNode)matchNode;
             for (int slot:  m2c.getScopeOffsets()) {
                 // Static scope scope offsets store both depth and offset
                 int depth = slot >> 16;
                 int offset = slot & 0xffff;
 
                 // For now, we'll continue to implicitly reference "$~"
                 String var = getVarNameFromScopeTree(s, depth, offset);
                 addInstr(s, new SetCapturedVarInstr(s.getLocalVariable(var, depth), result, var));
             }
         }
         return result;
     }
 
     private String getVarNameFromScopeTree(IRScope scope, int depth, int offset) {
         if (depth == 0) {
             return scope.getStaticScope().getVariables()[offset];
         }
         return getVarNameFromScopeTree(scope.getLexicalParent(), depth - 1, offset);
     }
 
     public Operand buildMatch3(Match3Node matchNode, IRScope s) {
         Operand receiver = build(matchNode.getReceiverNode(), s);
         Operand value    = build(matchNode.getValueNode(), s);
 
         return addResultInstr(s, new Match3Instr(s.createTemporaryVariable(), receiver, value));
     }
 
     private Operand getContainerFromCPath(Colon3Node cpath, IRScope s) {
         Operand container;
 
         if (cpath instanceof Colon2Node) {
             Node leftNode = ((Colon2Node) cpath).getLeftNode();
 
             if (leftNode != null) { // Foo::Bar
                 container = build(leftNode, s);
             } else { // Only name with no left-side Bar <- Note no :: on left
                 container = findContainerModule(s);
             }
         } else { //::Bar
             container = new ObjectClass();
         }
 
         return container;
     }
 
     public Operand buildModule(ModuleNode moduleNode, IRScope s) {
         Colon3Node cpath = moduleNode.getCPath();
         String moduleName = cpath.getName();
         Operand container = getContainerFromCPath(cpath, s);
         IRModuleBody body = new IRModuleBody(manager, s, moduleName, moduleNode.getPosition().getLine(), moduleNode.getScope());
         Variable tmpVar = addResultInstr(s, new DefineModuleInstr(s.createTemporaryVariable(), body, container));
 
         return buildModuleOrClassBody(s, tmpVar, body, moduleNode.getBodyNode(), moduleNode.getPosition().getLine());
     }
 
     public Operand buildMultipleAsgn(MultipleAsgnNode multipleAsgnNode, IRScope s) {
         Operand  values = build(multipleAsgnNode.getValueNode(), s);
         Variable ret = getValueInTemporaryVariable(s, values);
         buildMultipleAsgnAssignment(multipleAsgnNode, s, null, ret);
         return ret;
     }
 
     // SSS: This method is called both for regular multiple assignment as well as argument passing
     //
     // Ex: a,b,*c=v  is a regular assignment and in this case, the "values" operand will be non-null
     // Ex: { |a,b,*c| ..} is the argument passing case
     public void buildMultipleAsgnAssignment(final MultipleAsgnNode multipleAsgnNode, IRScope s, Operand argsArray, Operand values) {
         final ListNode sourceArray = multipleAsgnNode.getHeadNode();
 
         // First, build assignments for specific named arguments
         int i = 0;
         if (sourceArray != null) {
             for (Node an: sourceArray.childNodes()) {
                 if (values == null) {
                     buildBlockArgsAssignment(an, s, argsArray, i, false);
                 } else {
                     Variable rhsVal = addResultInstr(s, new ReqdArgMultipleAsgnInstr(s.createTemporaryVariable(), values, i));
                     buildAssignment(an, s, rhsVal);
                 }
                 i++;
             }
         }
 
         // First, build an assignment for a splat, if any, with the rest of the args!
         Node argsNode = multipleAsgnNode.getArgsNode();
         if (argsNode == null) {
             if (sourceArray == null) {
                 throw new NotCompilableException("Something's wrong, multiple assignment with no head or args at: " + multipleAsgnNode.getPosition());
             }
         } else if (!(argsNode instanceof StarNode)) {
             if (values != null) {
                 buildAssignment(argsNode, s,    // rest of the argument array!
                         addResultInstr(s, new RestArgMultipleAsgnInstr(s.createTemporaryVariable(), values, i)));
             } else {
                 buildBlockArgsAssignment(argsNode, s, argsArray, i, true); // rest of the argument array!
             }
         }
     }
 
     public Operand buildNewline(NewlineNode node, IRScope s) {
         return build(skipOverNewlines(s, node), s);
     }
 
     public Operand buildNext(final NextNode nextNode, IRScope s) {
         IRLoop currLoop = getCurrentLoop();
 
         Operand rv = (nextNode.getValueNode() == null) ? manager.getNil() : build(nextNode.getValueNode(), s);
 
         // If we have ensure blocks, have to run those first!
         if (!activeEnsureBlockStack.empty()) emitEnsureBlocks(s, currLoop);
         else if (!activeRescueBlockStack.empty()) activeRescueBlockStack.peek().restoreException(this, s, currLoop);
 
         if (currLoop != null) {
             // If a regular loop, the next is simply a jump to the end of the iteration
             addInstr(s, new JumpInstr(currLoop.iterEndLabel));
         } else {
             addInstr(s, new ThreadPollInstr(true));
             // If a closure, the next is simply a return from the closure!
             if (s instanceof IRClosure) addInstr(s, new ReturnInstr(rv));
             else addInstr(s, new ThrowExceptionInstr(IRException.NEXT_LocalJumpError));
         }
 
         // Once the "next instruction" (closure-return) executes, control exits this scope
         return U_NIL;
     }
 
     public Operand buildNthRef(NthRefNode nthRefNode, IRScope s) {
         return copyAndReturnValue(s, new NthRef(nthRefNode.getMatchNumber()));
     }
 
     public Operand buildNil() {
         return manager.getNil();
     }
 
     public Operand buildOpAsgn(OpAsgnNode opAsgnNode, IRScope s) {
         Label l;
         Variable readerValue = s.createTemporaryVariable();
         Variable writerValue = s.createTemporaryVariable();
 
         // get attr
         Operand  v1 = build(opAsgnNode.getReceiverNode(), s);
         addInstr(s, CallInstr.create(readerValue, new MethAddr(opAsgnNode.getVariableName()), v1, NO_ARGS, null));
 
         // Ex: e.val ||= n
         //     e.val &&= n
         String opName = opAsgnNode.getOperatorName();
         if (opName.equals("||") || opName.equals("&&")) {
             l = s.getNewLabel();
             addInstr(s, BEQInstr.create(readerValue, opName.equals("||") ? manager.getTrue() : manager.getFalse(), l));
 
             // compute value and set it
             Operand  v2 = build(opAsgnNode.getValueNode(), s);
             addInstr(s, CallInstr.create(writerValue, new MethAddr(opAsgnNode.getVariableNameAsgn()), v1, new Operand[] {v2}, null));
             // It is readerValue = v2.
             // readerValue = writerValue is incorrect because the assignment method
             // might return something else other than the value being set!
             addInstr(s, new CopyInstr(readerValue, v2));
             addInstr(s, new LabelInstr(l));
 
             return readerValue;
         }
         // Ex: e.val = e.val.f(n)
         else {
             // call operator
             Operand  v2 = build(opAsgnNode.getValueNode(), s);
             Variable setValue = s.createTemporaryVariable();
             addInstr(s, CallInstr.create(setValue, new MethAddr(opAsgnNode.getOperatorName()), readerValue, new Operand[]{v2}, null));
 
             // set attr
             addInstr(s, CallInstr.create(writerValue, new MethAddr(opAsgnNode.getVariableNameAsgn()), v1, new Operand[] {setValue}, null));
             // Returning writerValue is incorrect becuase the assignment method
             // might return something else other than the value being set!
             return setValue;
         }
     }
 
     // Translate "x &&= y" --> "x = y if is_true(x)" -->
     //
     //    x = -- build(x) should return a variable! --
     //    f = is_true(x)
     //    beq(f, false, L)
     //    x = -- build(y) --
     // L:
     //
     public Operand buildOpAsgnAnd(OpAsgnAndNode andNode, IRScope s) {
         Label    l  = s.getNewLabel();
         Operand  v1 = build(andNode.getFirstNode(), s);
         Variable result = getValueInTemporaryVariable(s, v1);
         addInstr(s, BEQInstr.create(v1, manager.getFalse(), l));
         Operand v2 = build(andNode.getSecondNode(), s);  // This does the assignment!
         addInstr(s, new CopyInstr(result, v2));
         addInstr(s, new LabelInstr(l));
         return result;
     }
 
     // "x ||= y"
     // --> "x = (is_defined(x) && is_true(x) ? x : y)"
     // --> v = -- build(x) should return a variable! --
     //     f = is_true(v)
     //     beq(f, true, L)
     //     -- build(x = y) --
     //   L:
     //
     public Operand buildOpAsgnOr(final OpAsgnOrNode orNode, IRScope s) {
         Label    l1 = s.getNewLabel();
         Label    l2 = null;
         Variable flag = s.createTemporaryVariable();
         Operand  v1;
         boolean  needsDefnCheck = orNode.getFirstNode().needsDefinitionCheck();
         if (needsDefnCheck) {
             l2 = s.getNewLabel();
             v1 = buildGetDefinition(orNode.getFirstNode(), s);
             addInstr(s, new CopyInstr(flag, v1));
             addInstr(s, BEQInstr.create(flag, manager.getNil(), l2)); // if v1 is undefined, go to v2's computation
         }
         v1 = build(orNode.getFirstNode(), s); // build of 'x'
         addInstr(s, new CopyInstr(flag, v1));
         Variable result = getValueInTemporaryVariable(s, v1);
         if (needsDefnCheck) {
             addInstr(s, new LabelInstr(l2));
         }
         addInstr(s, BEQInstr.create(flag, manager.getTrue(), l1));  // if v1 is defined and true, we are done!
         Operand v2 = build(orNode.getSecondNode(), s); // This is an AST node that sets x = y, so nothing special to do here.
         addInstr(s, new CopyInstr(result, v2));
         addInstr(s, new LabelInstr(l1));
 
         // Return value of x ||= y is always 'x'
         return result;
     }
 
     public Operand buildOpElementAsgn(OpElementAsgnNode node, IRScope s) {
         if (node.isOr()) return buildOpElementAsgnWithOr(node, s);
         if (node.isAnd()) return buildOpElementAsgnWithAnd(node, s);
 
         return buildOpElementAsgnWithMethod(node, s);
     }
 
     // Translate "a[x] ||= n" --> "a[x] = n if !is_true(a[x])"
     //
     //    tmp = build(a) <-- receiver
     //    arg = build(x) <-- args
     //    val = buildCall([], tmp, arg)
     //    f = is_true(val)
     //    beq(f, true, L)
     //    val = build(n) <-- val
     //    buildCall([]= tmp, arg, val)
     // L:
     //
     public Operand buildOpElementAsgnWithOr(OpElementAsgnNode opElementAsgnNode, IRScope s) {
         Operand array = build(opElementAsgnNode.getReceiverNode(), s);
         Label    l     = s.getNewLabel();
         Variable elt   = s.createTemporaryVariable();
         List<Operand> argList = setupCallArgs(opElementAsgnNode.getArgsNode(), s);
         addInstr(s, CallInstr.create(elt, new MethAddr("[]"), array, argList.toArray(new Operand[argList.size()]), null));
         addInstr(s, BEQInstr.create(elt, manager.getTrue(), l));
         Operand value = build(opElementAsgnNode.getValueNode(), s);
         argList.add(value);
         addInstr(s, CallInstr.create(elt, new MethAddr("[]="), array, argList.toArray(new Operand[argList.size()]), null));
         addInstr(s, new CopyInstr(elt, value));
         addInstr(s, new LabelInstr(l));
         return elt;
     }
 
     // Translate "a[x] &&= n" --> "a[x] = n if is_true(a[x])"
     public Operand buildOpElementAsgnWithAnd(OpElementAsgnNode opElementAsgnNode, IRScope s) {
         Operand array = build(opElementAsgnNode.getReceiverNode(), s);
         Label    l     = s.getNewLabel();
         Variable elt   = s.createTemporaryVariable();
         List<Operand> argList = setupCallArgs(opElementAsgnNode.getArgsNode(), s);
         addInstr(s, CallInstr.create(elt, new MethAddr("[]"), array, argList.toArray(new Operand[argList.size()]), null));
         addInstr(s, BEQInstr.create(elt, manager.getFalse(), l));
         Operand value = build(opElementAsgnNode.getValueNode(), s);
         argList.add(value);
         addInstr(s, CallInstr.create(elt, new MethAddr("[]="), array, argList.toArray(new Operand[argList.size()]), null));
         addInstr(s, new CopyInstr(elt, value));
         addInstr(s, new LabelInstr(l));
         return elt;
     }
 
     // a[i] *= n, etc.  anything that is not "a[i] &&= .. or a[i] ||= .."
     //    arr = build(a) <-- receiver
     //    arg = build(x) <-- args
     //    elt = buildCall([], arr, arg)
     //    val = build(n) <-- val
     //    val = buildCall(METH, elt, val)
     //    val = buildCall([]=, arr, arg, val)
     public Operand buildOpElementAsgnWithMethod(OpElementAsgnNode opElementAsgnNode, IRScope s) {
         Operand array = build(opElementAsgnNode.getReceiverNode(), s);
         List<Operand> argList = setupCallArgs(opElementAsgnNode.getArgsNode(), s);
         Variable elt = s.createTemporaryVariable();
         addInstr(s, CallInstr.create(elt, new MethAddr("[]"), array, argList.toArray(new Operand[argList.size()]), null)); // elt = a[args]
         Operand value = build(opElementAsgnNode.getValueNode(), s);                                       // Load 'value'
         String  operation = opElementAsgnNode.getOperatorName();
         addInstr(s, CallInstr.create(elt, new MethAddr(operation), elt, new Operand[] { value }, null)); // elt = elt.OPERATION(value)
         // SSS: do not load the call result into 'elt' to eliminate the RAW dependency on the call
         // We already know what the result is going be .. we are just storing it back into the array
         Variable tmp = s.createTemporaryVariable();
         argList.add(elt);
         addInstr(s, CallInstr.create(tmp, new MethAddr("[]="), array, argList.toArray(new Operand[argList.size()]), null));   // a[args] = elt
         return elt;
     }
 
     // Translate ret = (a || b) to ret = (a ? true : b) as follows
     //
     //    v1 = -- build(a) --
     //       OPT: ret can be set to v1, but effectively v1 is true if we take the branch to L.
     //            while this info can be inferred by using attributes, why bother if we can do this?
     //    ret = v1
     //    beq(v1, true, L)
     //    v2 = -- build(b) --
     //    ret = v2
     // L:
     //
     public Operand buildOr(final OrNode orNode, IRScope s) {
         if (orNode.getFirstNode().getNodeType().alwaysTrue()) {
             // build first node only and return true
             return build(orNode.getFirstNode(), s);
         } else if (orNode.getFirstNode().getNodeType().alwaysFalse()) {
             // build first node as non-expr and build second node
             build(orNode.getFirstNode(), s);
             return build(orNode.getSecondNode(), s);
         } else {
             Label    l   = s.getNewLabel();
             Operand  v1  = build(orNode.getFirstNode(), s);
             Variable ret = getValueInTemporaryVariable(s, v1);
             addInstr(s, BEQInstr.create(v1, manager.getTrue(), l));
             Operand  v2  = build(orNode.getSecondNode(), s);
             addInstr(s, new CopyInstr(ret, v2));
             addInstr(s, new LabelInstr(l));
             return ret;
         }
     }
 
     public Operand buildPostExe(PostExeNode postExeNode, IRScope s) {
         IRScope topLevel = s.getTopLevelScope();
         IRScope nearestLVarScope = s.getNearestTopLocalVariableScope();
 
         IRClosure endClosure = new IRClosure(manager, s, postExeNode.getPosition().getLine(), nearestLVarScope.getStaticScope(), Arity.procArityOf(postExeNode.getVarNode()), postExeNode.getArgumentType(), "_END_", true);
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder closureBuilder = newIRBuilder(manager);
 
         // Set up %current_scope and %current_module
         closureBuilder.addInstr(endClosure, new CopyInstr(endClosure.getCurrentScopeVariable(), new CurrentScope(0)));
         closureBuilder.addInstr(endClosure, new CopyInstr(endClosure.getCurrentModuleVariable(), new ScopeModule(0)));
         closureBuilder.build(postExeNode.getBodyNode(), endClosure);
 
         // Record to IRScope so JIT can pre-compile all potentially activated END blocks.
         topLevel.recordEndBlock(endClosure);
 
         // Add an instruction in 's' to record the end block in the 'topLevel' scope.
         // SSS FIXME: IR support for end-blocks that access vars in non-toplevel-scopes
         // might be broken currently. We could either fix it or consider dropping support
         // for END blocks altogether or only support them in the toplevel. Not worth the pain.
         addInstr(s, new RecordEndBlockInstr(topLevel, new WrappedIRClosure(s.getSelf(), endClosure)));
         return manager.getNil();
     }
 
     public Operand buildPreExe(PreExeNode preExeNode, IRScope s) {
         IRClosure beginClosure = new IRFor(manager, s, preExeNode.getPosition().getLine(), s.getTopLevelScope().getStaticScope(), Arity.procArityOf(preExeNode.getVarNode()), preExeNode.getArgumentType(), "_BEGIN_");
         // Create a new nested builder to ensure this gets its own IR builder state
         // like the ensure block stack
         IRBuilder closureBuilder = newIRBuilder(manager);
 
         // Set up %current_scope and %current_module
         closureBuilder.addInstr(beginClosure, new CopyInstr(beginClosure.getCurrentScopeVariable(), new CurrentScope(0)));
         closureBuilder.addInstr(beginClosure, new CopyInstr(beginClosure.getCurrentModuleVariable(), new ScopeModule(0)));
         closureBuilder.build(preExeNode.getBodyNode(), beginClosure);
 
         // Record the begin block at IR build time
         s.getTopLevelScope().recordBeginBlock(beginClosure);
         return manager.getNil();
     }
 
     public Operand buildRational(RationalNode rationalNode) {
         return new Rational(rationalNode.getNumerator(), rationalNode.getDenominator());
     }
 
     public Operand buildRedo(IRScope s) {
         // If in a loop, a redo is a jump to the beginning of the loop.
         // If not, for closures, a redo is a jump to the beginning of the closure.
         // If not in a loop or a closure, it is a local jump error
         IRLoop currLoop = getCurrentLoop();
         if (currLoop != null) {
              addInstr(s, new JumpInstr(currLoop.iterStartLabel));
         } else {
             if (s instanceof IRClosure) {
                 addInstr(s, new ThreadPollInstr(true));
                 addInstr(s, new JumpInstr(((IRClosure)s).startLabel));
             } else {
                 addInstr(s, new ThrowExceptionInstr(IRException.REDO_LocalJumpError));
             }
         }
         return manager.getNil();
     }
 
     public Operand buildRegexp(RegexpNode reNode, IRScope s) {
         // SSS FIXME: Rather than throw syntax error at runtime, we should detect
         // regexp syntax errors at build time and add an exception-throwing instruction instead
         return copyAndReturnValue(s, new Regexp(new StringLiteral(reNode.getValue()), reNode.getOptions()));
     }
 
     public Operand buildRescue(RescueNode node, IRScope s) {
         return buildRescueInternal(node, s, null);
     }
 
     private Operand buildRescueInternal(RescueNode rescueNode, IRScope s, EnsureBlockInfo ensure) {
         // Labels marking start, else, end of the begin-rescue(-ensure)-end block
         Label rBeginLabel = ensure == null ? s.getNewLabel() : ensure.regionStart;
         Label rEndLabel   = ensure == null ? s.getNewLabel() : ensure.end;
         Label rescueLabel = s.getNewLabel(); // Label marking start of the first rescue code.
 
         // Save $! in a temp var so it can be restored when the exception gets handled.
         Variable savedGlobalException = s.createTemporaryVariable();
         addInstr(s, new GetGlobalVariableInstr(savedGlobalException, "$!"));
         if (ensure != null) ensure.savedGlobalException = savedGlobalException;
 
         addInstr(s, new LabelInstr(rBeginLabel));
 
         // Placeholder rescue instruction that tells rest of the compiler passes the boundaries of the rescue block.
         addInstr(s, new ExceptionRegionStartMarkerInstr(rescueLabel));
         activeRescuers.push(rescueLabel);
 
         // Body
         Operand tmp = manager.getNil();  // default return value if for some strange reason, we neither have the body node or the else node!
         Variable rv = s.createTemporaryVariable();
         if (rescueNode.getBodyNode() != null) tmp = build(rescueNode.getBodyNode(), s);
 
         // Push rescue block *after* body has been built.
         // If not, this messes up generation of retry in these scenarios like this:
         //
         //     begin    -- 1
         //       ...
         //     rescue
         //       begin  -- 2
         //         ...
         //         retry
         //       rescue
         //         ...
         //       end
         //     end
         //
         // The retry should jump to 1, not 2.
         // If we push the rescue block before building the body, we will jump to 2.
         RescueBlockInfo rbi = new RescueBlockInfo(rescueNode, rBeginLabel, savedGlobalException, getCurrentLoop());
         activeRescueBlockStack.push(rbi);
 
         // Since rescued regions are well nested within Ruby, this bare marker is sufficient to
         // let us discover the edge of the region during linear traversal of instructions during cfg construction.
         addInstr(s, new ExceptionRegionEndMarkerInstr());
         activeRescuers.pop();
 
         // Else part of the body -- we simply fall through from the main body if there were no exceptions
         Label elseLabel = rescueNode.getElseNode() == null ? null : s.getNewLabel();
         if (elseLabel != null) {
             addInstr(s, new LabelInstr(elseLabel));
             tmp = build(rescueNode.getElseNode(), s);
         }
 
         if (tmp != U_NIL) {
             addInstr(s, new CopyInstr(rv, tmp));
 
             // No explicit return from the protected body
             // - If we dont have any ensure blocks, simply jump to the end of the rescue block
             // - If we do, execute the ensure code.
             if (ensure != null) {
                 ensure.cloneIntoHostScope(this, s);
             }
             addInstr(s, new JumpInstr(rEndLabel));
         }   //else {
             // If the body had an explicit return, the return instruction IR build takes care of setting
             // up execution of all necessary ensure blocks.  So, nothing to do here!
             //
             // Additionally, the value in 'rv' will never be used, so need to set it to any specific value.
             // So, we can leave it undefined.  If on the other hand, there was an exception in that block,
             // 'rv' will get set in the rescue handler -- see the 'rv' being passed into
             // buildRescueBodyInternal below.  So, in either case, we are good!
             //}
 
         // Start of rescue logic
         addInstr(s, new LabelInstr(rescueLabel));
 
         // Save off exception & exception comparison type
         Variable exc = addResultInstr(s, new ReceiveRubyExceptionInstr(s.createTemporaryVariable()));
 
         // Build the actual rescue block(s)
         buildRescueBodyInternal(s, rescueNode.getRescueNode(), rv, exc, rEndLabel);
 
         // End label -- only if there is no ensure block!  With an ensure block, you end at ensureEndLabel.
         if (ensure == null) addInstr(s, new LabelInstr(rEndLabel));
 
         activeRescueBlockStack.pop();
         return rv;
     }
 
     private void outputExceptionCheck(IRScope s, Operand excType, Operand excObj, Label caughtLabel) {
         Variable eqqResult = addResultInstr(s, new RescueEQQInstr(s.createTemporaryVariable(), excType, excObj));
         addInstr(s, BEQInstr.create(eqqResult, manager.getTrue(), caughtLabel));
     }
 
     private void buildRescueBodyInternal(IRScope s, RescueBodyNode rescueBodyNode, Variable rv, Variable exc, Label endLabel) {
         final Node exceptionList = rescueBodyNode.getExceptionNodes();
 
         // Compare and branch as necessary!
         Label uncaughtLabel = s.getNewLabel();
         Label caughtLabel = s.getNewLabel();
         if (exceptionList != null) {
             if (exceptionList instanceof ListNode) {
                 List<Operand> excTypes = new ArrayList<>();
                 for (Node excType : exceptionList.childNodes()) {
                     excTypes.add(build(excType, s));
                 }
                 outputExceptionCheck(s, new Array(excTypes), exc, caughtLabel);
             } else if (exceptionList instanceof SplatNode) { // splatnode, catch
                 outputExceptionCheck(s, build(((SplatNode)exceptionList).getValue(), s), exc, caughtLabel);
             } else { // argscat/argspush
                 outputExceptionCheck(s, build(exceptionList, s), exc, caughtLabel);
             }
         } else {
             // SSS FIXME:
             // rescue => e AND rescue implicitly EQQ the exception object with StandardError
             // We generate explicit IR for this test here.  But, this can lead to inconsistent
             // behavior (when compared to MRI) in certain scenarios.  See example:
             //
             //   self.class.const_set(:StandardError, 1)
             //   begin; raise TypeError.new; rescue; puts "AHA"; end
             //
             // MRI rescues the error, but we will raise an exception because of reassignment
             // of StandardError.  I am ignoring this for now and treating this as undefined behavior.
             //
             // Solution: Create a 'StandardError' operand type to eliminate this.
             Variable v = addResultInstr(s, new InheritanceSearchConstInstr(s.createTemporaryVariable(), s.getCurrentModuleVariable(), "StandardError", false));
             outputExceptionCheck(s, v, exc, caughtLabel);
         }
 
         // Uncaught exception -- build other rescue nodes or rethrow!
         addInstr(s, new LabelInstr(uncaughtLabel));
         if (rescueBodyNode.getOptRescueNode() != null) {
             buildRescueBodyInternal(s, rescueBodyNode.getOptRescueNode(), rv, exc, endLabel);
         } else {
             addInstr(s, new ThrowExceptionInstr(exc));
         }
 
         // Caught exception case -- build rescue body
         addInstr(s, new LabelInstr(caughtLabel));
         Node realBody = skipOverNewlines(s, rescueBodyNode.getBodyNode());
         Operand x = build(realBody, s);
         if (x != U_NIL) { // can be U_NIL if the rescue block has an explicit return
             // Restore "$!"
             RescueBlockInfo rbi = activeRescueBlockStack.peek();
             addInstr(s, new PutGlobalVarInstr("$!", rbi.savedExceptionVariable));
 
             // Set up node return value 'rv'
             addInstr(s, new CopyInstr(rv, x));
 
             // If we have a matching ensure block, clone it so ensure block runs here
             if (!activeEnsureBlockStack.empty() && rbi.rescueNode == activeEnsureBlockStack.peek().matchingRescueNode) {
                 activeEnsureBlockStack.peek().cloneIntoHostScope(this, s);
             }
             addInstr(s, new JumpInstr(endLabel));
         }
     }
 
     public Operand buildRetry(IRScope s) {
         // JRuby only supports retry when present in rescue blocks!
         // 1.9 doesn't support retry anywhere else.
 
         // Jump back to the innermost rescue block
         // We either find it, or we add code to throw a runtime exception
         if (activeRescueBlockStack.empty()) {
             addInstr(s, new ThrowExceptionInstr(IRException.RETRY_LocalJumpError));
         } else {
             addInstr(s, new ThreadPollInstr(true));
             // Restore $! and jump back to the entry of the rescue block
             RescueBlockInfo rbi = activeRescueBlockStack.peek();
             addInstr(s, new PutGlobalVarInstr("$!", rbi.savedExceptionVariable));
             addInstr(s, new JumpInstr(rbi.entryLabel));
             // Retries effectively create a loop
             s.setHasLoopsFlag();
         }
         return manager.getNil();
     }
 
     private Operand processEnsureRescueBlocks(IRScope s, Operand retVal) { 
         // Before we return,
         // - have to go execute all the ensure blocks if there are any.
         //   this code also takes care of resetting "$!"
         // - if we have a rescue block, reset "$!".
         if (!activeEnsureBlockStack.empty()) {
             retVal = addResultInstr(s, new CopyInstr(s.createTemporaryVariable(), retVal));
             emitEnsureBlocks(s, null);
         } else if (!activeRescueBlockStack.empty()) {
             // Restore $!
             RescueBlockInfo rbi = activeRescueBlockStack.peek();
             addInstr(s, new PutGlobalVarInstr("$!", rbi.savedExceptionVariable));
         }
        return retVal;
     }
 
     public Operand buildReturn(ReturnNode returnNode, IRScope s) {
         Operand retVal = (returnNode.getValueNode() == null) ? manager.getNil() : build(returnNode.getValueNode(), s);
 
         if (s instanceof IRClosure) {
             // If 'm' is a block scope, a return returns from the closest enclosing method.
             // If this happens to be a module body, the runtime throws a local jump error if the
             // closure is a proc. If the closure is a lambda, then this becomes a normal return.
             IRMethod m = s.getNearestMethod();
             addInstr(s, new RuntimeHelperCall(null, CHECK_FOR_LJE, new Operand[] { new Boolean(m == null) }));
             retVal = processEnsureRescueBlocks(s, retVal);
             addInstr(s, new NonlocalReturnInstr(retVal, m == null ? "--none--" : m.getName()));
         } else if (s.isModuleBody()) {
             IRMethod sm = s.getNearestMethod();
 
             // Cannot return from top-level module bodies!
             if (sm == null) addInstr(s, new ThrowExceptionInstr(IRException.RETURN_LocalJumpError));
             retVal = processEnsureRescueBlocks(s, retVal);
             if (sm != null) addInstr(s, new NonlocalReturnInstr(retVal, sm.getName()));
         } else {
             retVal = processEnsureRescueBlocks(s, retVal);
             addInstr(s, new ReturnInstr(retVal));
         }
 
         // The value of the return itself in the containing expression can never be used because of control-flow reasons.
         // The expression that uses this result can never be executed beyond the return and hence the value itself is just
         // a placeholder operand.
         return U_NIL;
     }
 
     public IREvalScript buildEvalRoot(StaticScope staticScope, IRScope containingScope, String file, int lineNumber, RootNode rootNode, EvalType evalType) {
         // Top-level script!
         IREvalScript script = new IREvalScript(manager, containingScope, file, lineNumber, staticScope, evalType);
 
         // Debug info: record line number
         addInstr(script, new LineNumberInstr(script, lineNumber));
 
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         addInstr(script, new CopyInstr(script.getCurrentScopeVariable(), new CurrentScope(0)));
         addInstr(script, new CopyInstr(script.getCurrentModuleVariable(), new ScopeModule(0)));
         // Build IR for the tree and return the result of the expression tree
         Operand rval = rootNode.getBodyNode() == null ? manager.getNil() : build(rootNode.getBodyNode(), script);
         addInstr(script, new ReturnInstr(rval));
 
         return script;
     }
 
     public IRScriptBody buildRoot(RootNode rootNode) {
         String file = rootNode.getPosition().getFile();
         StaticScope staticScope = rootNode.getStaticScope();
 
         // Top-level script!
         IRScriptBody script = new IRScriptBody(manager, file, staticScope);
         addInstr(script, new ReceiveSelfInstr(script.getSelf()));
         // Set %current_scope = <current-scope>
         // Set %current_module = <current-module>
         addInstr(script, new CopyInstr(script.getCurrentScopeVariable(), new CurrentScope(0)));
         addInstr(script, new CopyInstr(script.getCurrentModuleVariable(), new ScopeModule(0)));
 
         // Build IR for the tree and return the result of the expression tree
         addInstr(script, new ReturnInstr(build(rootNode.getBodyNode(), script)));
 
         return script;
     }
 
     public Operand buildSelf(IRScope s) {
         return s.getSelf();
     }
 
     public Operand buildSplat(SplatNode splatNode, IRScope s) {
         // SSS: Since splats can only occur in call argument lists, no need to copyAndReturnValue(...)
         // Verify with Tom / Charlie
         return new Splat(build(splatNode.getValue(), s));
     }
 
     public Operand buildStr(StrNode strNode, IRScope s) {
         return copyAndReturnValue(s, new StringLiteral(strNode.getValue(), strNode.getCodeRange()));
     }
 
     private Operand buildSuperInstr(IRScope s, Operand block, Operand[] args) {
         CallInstr superInstr;
         Variable ret = s.createTemporaryVariable();
         if ((s instanceof IRMethod) && (s.getLexicalParent() instanceof IRClassBody)) {
             IRMethod m = (IRMethod)s;
             if (m.isInstanceMethod) {
                 superInstr = new InstanceSuperInstr(ret, s.getCurrentModuleVariable(), new MethAddr(s.getName()), args, block);
             } else {
                 superInstr = new ClassSuperInstr(ret, s.getCurrentModuleVariable(), new MethAddr(s.getName()), args, block);
             }
         } else {
             // We dont always know the method name we are going to be invoking if the super occurs in a closure.
             // This is because the super can be part of a block that will be used by 'define_method' to define
             // a new method.  In that case, the method called by super will be determined by the 'name' argument
             // to 'define_method'.
             superInstr = new UnresolvedSuperInstr(ret, s.getSelf(), args, block);
         }
         receiveBreakException(s, block, superInstr);
         return ret;
     }
 
     public Operand buildSuper(SuperNode superNode, IRScope s) {
         if (s.isModuleBody()) return buildSuperInScriptBody(s);
 
         List<Operand> args = setupCallArgs(superNode.getArgsNode(), s);
         Operand block = setupCallClosure(superNode.getIterNode(), s);
         if (block == null) block = getImplicitBlockArg(s);
         return buildSuperInstr(s, block, args.toArray(new Operand[args.size()]));
     }
 
     private Operand buildSuperInScriptBody(IRScope s) {
         return addResultInstr(s, new UnresolvedSuperInstr(s.createTemporaryVariable(), s.getSelf(), NO_ARGS, null));
     }
 
     public Operand buildSValue(SValueNode node, IRScope s) {
         // SSS FIXME: Required? Verify with Tom/Charlie
         return copyAndReturnValue(s, new SValue(build(node.getValue(), s)));
     }
 
     public Operand buildSymbol(SymbolNode node) {
         // SSS: Since symbols are interned objects, no need to copyAndReturnValue(...)
-        return new Symbol(node.getName());
+        return new Symbol(node.getName(), node.getEncoding());
     }
 
     public Operand buildTrue() {
         return manager.getTrue();
     }
 
     public Operand buildUndef(Node node, IRScope s) {
         Operand methName = build(((UndefNode) node).getName(), s);
         return addResultInstr(s, new UndefMethodInstr(s.createTemporaryVariable(), methName));
     }
 
     private Operand buildConditionalLoop(IRScope s, Node conditionNode,
             Node bodyNode, boolean isWhile, boolean isLoopHeadCondition) {
         if (isLoopHeadCondition &&
                 ((isWhile && conditionNode.getNodeType().alwaysFalse()) ||
                 (!isWhile && conditionNode.getNodeType().alwaysTrue()))) {
             // we won't enter the loop -- just build the condition node
             build(conditionNode, s);
             return manager.getNil();
         } else {
             IRLoop loop = new IRLoop(s, getCurrentLoop());
             Variable loopResult = loop.loopResult;
             Label setupResultLabel = s.getNewLabel();
 
             // Push new loop
             loopStack.push(loop);
 
             // End of iteration jumps here
             addInstr(s, new LabelInstr(loop.loopStartLabel));
             if (isLoopHeadCondition) {
                 Operand cv = build(conditionNode, s);
                 addInstr(s, BEQInstr.create(cv, isWhile ? manager.getFalse() : manager.getTrue(), setupResultLabel));
             }
 
             // Redo jumps here
             addInstr(s, new LabelInstr(loop.iterStartLabel));
 
             // Thread poll at start of iteration -- ensures that redos and nexts run one thread-poll per iteration
             addInstr(s, new ThreadPollInstr(true));
 
             // Build body
             if (bodyNode != null) build(bodyNode, s);
 
             // Next jumps here
             addInstr(s, new LabelInstr(loop.iterEndLabel));
             if (isLoopHeadCondition) {
                 addInstr(s, new JumpInstr(loop.loopStartLabel));
             } else {
                 Operand cv = build(conditionNode, s);
                 addInstr(s, BEQInstr.create(cv, isWhile ? manager.getTrue() : manager.getFalse(), loop.iterStartLabel));
             }
 
             // Loop result -- nil always
             addInstr(s, new LabelInstr(setupResultLabel));
             addInstr(s, new CopyInstr(loopResult, manager.getNil()));
 
             // Loop end -- breaks jump here bypassing the result set up above
             addInstr(s, new LabelInstr(loop.loopEndLabel));
 
             // Done with loop
             loopStack.pop();
 
             return loopResult;
         }
     }
 
     public Operand buildUntil(final UntilNode untilNode, IRScope s) {
         return buildConditionalLoop(s, untilNode.getConditionNode(), untilNode.getBodyNode(), false, untilNode.evaluateAtStart());
     }
 
     public Operand buildVAlias(VAliasNode valiasNode, IRScope s) {
         addInstr(s, new GVarAliasInstr(new StringLiteral(valiasNode.getNewName()), new StringLiteral(valiasNode.getOldName())));
 
         return manager.getNil();
     }
 
     public Operand buildVCall(VCallNode node, IRScope s) {
         Variable callResult = s.createTemporaryVariable();
         Instr    callInstr  = CallInstr.create(CallType.VARIABLE, callResult, new MethAddr(node.getName()), s.getSelf(), NO_ARGS, null);
         addInstr(s, callInstr);
         return callResult;
     }
 
     public Operand buildWhile(final WhileNode whileNode, IRScope s) {
         return buildConditionalLoop(s, whileNode.getConditionNode(), whileNode.getBodyNode(), true, whileNode.evaluateAtStart());
     }
 
     public Operand buildXStr(XStrNode node, IRScope s) {
         Variable res = s.createTemporaryVariable();
         addInstr(s, new BacktickInstr(res, new StringLiteral(node.getValue())));
         return res;
     }
 
     public Operand buildYield(YieldNode node, IRScope s) {
         boolean unwrap = true;
         Node argNode = node.getArgsNode();
         // Get rid of one level of array wrapping
         if (argNode != null && (argNode instanceof ArrayNode) && ((ArrayNode)argNode).size() == 1) {
             argNode = ((ArrayNode)argNode).getLast();
             unwrap = false;
         }
 
         Variable ret = s.createTemporaryVariable();
         addInstr(s, new YieldInstr(ret, getImplicitBlockArg(s), build(argNode, s), unwrap));
         return ret;
     }
 
     public Operand buildZArray(IRScope s) {
        return copyAndReturnValue(s, new Array());
     }
 
     private Operand buildZSuperIfNest(final IRScope s, final Operand block) {
         // If we are in a block, we cannot make any assumptions about what args
         // the super instr is going to get -- if there were no 'define_method'
         // for defining methods, we could guarantee that the super is going to
         // receive args from the nearest method the block is embedded in.  But,
         // in the presence of 'define_method' (and eval and aliasing), all bets
         // are off because, any of the intervening block scopes could be a method
         // via a define_method call.
         //
         // Instead, we can actually collect all arguments of all scopes from here
         // till the nearest method scope and select the right set at runtime based
         // on which one happened to be a method scope. This has the additional
         // advantage of making explicit all used arguments.
         CodeBlock zsuperBuilder = new CodeBlock() {
             public Operand run() {
                 Variable scopeDepth = s.createTemporaryVariable();
                 addInstr(s, new ArgScopeDepthInstr(scopeDepth));
 
                 Label allDoneLabel = s.getNewLabel();
 
                 IRScope superScope = s;
                 int depthFromSuper = 0;
                 Label next = null;
 
                 // Loop and generate a block for each possible value of depthFromSuper
                 Variable zsuperResult = s.createTemporaryVariable();
                 while (superScope instanceof IRClosure) {
                     // Generate the next set of instructions
                     if (next != null) addInstr(s, new LabelInstr(next));
                     next = s.getNewLabel();
                     addInstr(s, BNEInstr.create(new Fixnum(depthFromSuper), scopeDepth, next));
                     Operand[] args = adjustVariableDepth(((IRClosure)superScope).getBlockArgs(), depthFromSuper);
                     addInstr(s, new ZSuperInstr(zsuperResult, s.getSelf(), args,  block));
                     addInstr(s, new JumpInstr(allDoneLabel));
 
                     // Move on
                     superScope = superScope.getLexicalParent();
                     depthFromSuper++;
                 }
 
                 addInstr(s, new LabelInstr(next));
 
                 // If we hit a method, this is known to always succeed
                 if (superScope instanceof IRMethod) {
                     Operand[] args = adjustVariableDepth(((IRMethod)superScope).getCallArgs(), depthFromSuper);
                     addInstr(s, new ZSuperInstr(zsuperResult, s.getSelf(), args, block));
                 } //else {
                 // FIXME: Do or don't ... there is no try
                     /* Control should never get here in the runtime */
                     /* Should we add an exception throw here just in case? */
                 //}
 
                 addInstr(s, new LabelInstr(allDoneLabel));
                 return zsuperResult;
             }
         };
 
         return receiveBreakException(s, block, zsuperBuilder);
     }
 
     public Operand buildZSuper(ZSuperNode zsuperNode, IRScope s) {
         if (s.isModuleBody()) return buildSuperInScriptBody(s);
 
         Operand block = setupCallClosure(zsuperNode.getIterNode(), s);
         if (block == null) block = getImplicitBlockArg(s);
 
         // Enebo:ZSuper in for (or nested for) can be statically resolved like method but it needs to fixup depth.
         if (s instanceof IRMethod) {
             return buildSuperInstr(s, block, ((IRMethod)s).getCallArgs());
         } else {
             return buildZSuperIfNest(s, block);
         }
     }
 
     /*
      * Adjust all argument operands by changing their depths to reflect how far they are from
      * super.  This fixup is only currently happening in supers nested in closures.
      */
     private Operand[] adjustVariableDepth(Operand[] args, int depthFromSuper) {
         Operand[] newArgs = new Operand[args.length];
 
         for (int i = 0; i < args.length; i++) {
             // Because of keyword args, we can have a keyword-arg hash in the call args.
             if (args[i] instanceof Hash) {
                 newArgs[i] = ((Hash) args[i]).cloneForLVarDepth(depthFromSuper);
             } else {
                 newArgs[i] = ((DepthCloneable) args[i]).cloneForDepth(depthFromSuper);
             }
         }
 
         return newArgs;
     }
 
     private Operand buildModuleOrClassBody(IRScope parent, Variable tmpVar, IRModuleBody body, Node bodyNode, int linenumber) {
         Variable processBodyResult = addResultInstr(parent, new ProcessModuleBodyInstr(parent.createTemporaryVariable(), tmpVar, getImplicitBlockArg(parent)));
         IRBuilder bodyBuilder = newIRBuilder(manager);
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             bodyBuilder.addInstr(body, new TraceInstr(RubyEvent.CLASS, null, body.getFileName(), linenumber));
         }
 
         bodyBuilder.addInstr(body, new ReceiveSelfInstr(body.getSelf()));                                  // %self
 
         if (body instanceof IRMetaClassBody) {
             bodyBuilder.addInstr(body, new ReceiveClosureInstr((Variable)getImplicitBlockArg(body)));      // %closure - SClass
         }
 
         bodyBuilder.addInstr(body, new CopyInstr(body.getCurrentScopeVariable(), new CurrentScope(0))); // %scope
         bodyBuilder.addInstr(body, new CopyInstr(body.getCurrentModuleVariable(), new ScopeModule(0))); // %module
         // Create a new nested builder to ensure this gets its own IR builder state
         Operand bodyReturnValue = bodyBuilder.build(bodyNode, body);
 
         if (RubyInstanceConfig.FULL_TRACE_ENABLED) {
             bodyBuilder.addInstr(body, new TraceInstr(RubyEvent.END, null, body.getFileName(), -1));
         }
 
         bodyBuilder.addInstr(body, new ReturnInstr(bodyReturnValue));
 
         return processBodyResult;
     }
 
     private String methodNameFor(IRScope s) {
         IRScope method = s.getNearestMethod();
 
         return method == null ? null : method.getName();
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/IRClosure.java b/core/src/main/java/org/jruby/ir/IRClosure.java
index 7024e556a3..633a547e93 100644
--- a/core/src/main/java/org/jruby/ir/IRClosure.java
+++ b/core/src/main/java/org/jruby/ir/IRClosure.java
@@ -1,377 +1,379 @@
 package org.jruby.ir;
 
+import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.interpreter.ClosureInterpreterContext;
 import org.jruby.ir.interpreter.InterpreterContext;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.representations.CFG;
 import org.jruby.ir.transformations.inlining.CloneInfo;
 import org.jruby.ir.transformations.inlining.SimpleCloneInfo;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.InterpretedIRBlockBody;
 import org.jruby.util.KeyValuePair;
 import org.objectweb.asm.Handle;
 
 import java.util.ArrayList;
 import java.util.List;
 
 // Closures are contexts/scopes for the purpose of IR building.  They are self-contained and accumulate instructions
 // that don't merge into the flow of the containing scope.  They are manipulated as an unit.
 // Their parents are always execution scopes.
 
 public class IRClosure extends IRScope {
     public final Label startLabel; // Label for the start of the closure (used to implement redo)
     public final Label endLabel;   // Label for the end of the closure (used to implement retry)
     public final int closureId;    // Unique id for this closure within the nearest ancestor method.
 
     private int nestingDepth;      // How many nesting levels within a method is this closure nested in?
 
     private boolean isBeginEndBlock;
 
     // Block parameters
     private List<Operand> blockArgs;
     private List<KeyValuePair<Operand, Operand>> keywordArgs;
 
     /** The parameter names, for Proc#parameters */
     private String[] parameterList;
 
     private Arity arity;
     private int argumentType;
 
     /** Added for interp/JIT purposes */
     private BlockBody body;
 
     /** Added for JIT purposes */
     private Handle handle;
 
     // Used by other constructions and by IREvalScript as well
     protected IRClosure(IRManager manager, IRScope lexicalParent, String fileName, int lineNumber, StaticScope staticScope, String prefix) {
         super(manager, lexicalParent, null, fileName, lineNumber, staticScope);
 
         this.startLabel = getNewLabel(prefix + "START");
         this.endLabel = getNewLabel(prefix + "END");
         this.closureId = lexicalParent.getNextClosureId();
         setName(prefix + closureId);
         this.body = null;
         this.parameterList = new String[] {};
 
         // set nesting depth
         int n = 0;
         IRScope s = this.getLexicalParent();
         while (s instanceof IRClosure) {
             n++;
             s = s.getLexicalParent();
         }
         this.nestingDepth = n;
     }
 
     /** Used by cloning code */
     /* Inlining generates a new name and id and basic cloning will reuse the originals name */
     protected IRClosure(IRClosure c, IRScope lexicalParent, int closureId, String fullName) {
         super(c, lexicalParent);
         this.closureId = closureId;
         super.setName(fullName);
         this.startLabel = getNewLabel(getName() + "_START");
         this.endLabel = getNewLabel(getName() + "_END");
         if (getManager().isDryRun()) {
             this.body = null;
         } else {
             this.body = new InterpretedIRBlockBody(this, c.body.arity());
         }
         this.blockArgs = new ArrayList<>();
         this.keywordArgs = new ArrayList<>();
         this.arity = c.arity;
     }
 
     public IRClosure(IRManager manager, IRScope lexicalParent, int lineNumber, StaticScope staticScope, Arity arity, int argumentType) {
         this(manager, lexicalParent, lineNumber, staticScope, arity, argumentType, "_CLOSURE_");
     }
 
     public IRClosure(IRManager manager, IRScope lexicalParent, int lineNumber, StaticScope staticScope, Arity arity, int argumentType, String prefix) {
         this(manager, lexicalParent, lineNumber, staticScope, arity, argumentType, prefix, false);
     }
 
     public IRClosure(IRManager manager, IRScope lexicalParent, int lineNumber, StaticScope staticScope, Arity arity, int argumentType, String prefix, boolean isBeginEndBlock) {
         this(manager, lexicalParent, lexicalParent.getFileName(), lineNumber, staticScope, prefix);
         this.blockArgs = new ArrayList<>();
         this.keywordArgs = new ArrayList<>();
         this.argumentType = argumentType;
         this.arity = arity;
         lexicalParent.addClosure(this);
 
         if (getManager().isDryRun()) {
             this.body = null;
         } else {
             this.body = new InterpretedIRBlockBody(this, arity);
             if (staticScope != null && !isBeginEndBlock) {
                 staticScope.setIRScope(this);
                 staticScope.setScopeType(this.getScopeType());
             }
         }
 
         this.nestingDepth++;
     }
 
     @Override
     public InterpreterContext allocateInterpreterContext(Instr[] instructionList) {
         return new ClosureInterpreterContext(this, instructionList);
     }
 
     public void setBeginEndBlock() {
         this.isBeginEndBlock = true;
     }
 
     public boolean isBeginEndBlock() {
         return isBeginEndBlock;
     }
 
     public void setParameterList(String[] parameterList) {
         this.parameterList = parameterList;
         if (!getManager().isDryRun()) {
             ((InterpretedIRBlockBody)this.body).setParameterList(parameterList);
         }
     }
 
     public String[] getParameterList() {
         return this.parameterList;
     }
 
     @Override
     public int getNextClosureId() {
         return getLexicalParent().getNextClosureId();
     }
 
     @Override
     public LocalVariable getNewFlipStateVariable() {
         throw new RuntimeException("Cannot get flip variables from closures.");
     }
 
     @Override
     public TemporaryLocalVariable createTemporaryVariable() {
         return getNewTemporaryVariable(TemporaryVariableType.CLOSURE);
     }
 
     @Override
     public TemporaryLocalVariable getNewTemporaryVariable(TemporaryVariableType type) {
         if (type == TemporaryVariableType.CLOSURE) {
             temporaryVariableIndex++;
             return new TemporaryClosureVariable(closureId, temporaryVariableIndex);
         }
 
         return super.getNewTemporaryVariable(type);
     }
 
     @Override
     public Label getNewLabel() {
         return getNewLabel("CL" + closureId + "_LBL");
     }
 
     @Override
     public IRScopeType getScopeType() {
         return IRScopeType.CLOSURE;
     }
 
     @Override
     public boolean isTopLocalVariableScope() {
         return false;
     }
 
     @Override
     public boolean isFlipScope() {
         return false;
     }
 
     @Override
     public void addInstr(Instr i) {
         // Accumulate block arguments
         if (i instanceof ReceiveKeywordRestArgInstr) {
             // Always add the keyword rest arg to the beginning
             keywordArgs.add(0, new KeyValuePair<Operand, Operand>(Symbol.KW_REST_ARG_DUMMY, ((ReceiveArgBase) i).getResult()));
         } else if (i instanceof ReceiveKeywordArgInstr) {
             ReceiveKeywordArgInstr rkai = (ReceiveKeywordArgInstr)i;
-            keywordArgs.add(new KeyValuePair<Operand, Operand>(new Symbol(rkai.argName), rkai.getResult()));
+            // FIXME: This lost encoding information when name was converted to string earlier in IRBuilder
+            keywordArgs.add(new KeyValuePair<Operand, Operand>(new Symbol(rkai.argName, USASCIIEncoding.INSTANCE), rkai.getResult()));
         } else if (i instanceof ReceiveRestArgInstr) {
             blockArgs.add(new Splat(((ReceiveRestArgInstr)i).getResult()));
         } else if (i instanceof ReceiveArgBase) {
             blockArgs.add(((ReceiveArgBase) i).getResult());
         }
 
         super.addInstr(i);
     }
 
     public Operand[] getBlockArgs() {
         if (receivesKeywordArgs()) {
             int i = 0;
             Operand[] args = new Operand[blockArgs.size() + 1];
             for (Operand arg: blockArgs) {
                 args[i++] = arg;
             }
             args[i] = new Hash(keywordArgs, true);
             return args;
         } else {
             return blockArgs.toArray(new Operand[blockArgs.size()]);
         }
     }
 
     public String toStringBody() {
         StringBuilder buf = new StringBuilder();
         buf.append(getName()).append(" = { \n");
 
         CFG c = getCFG();
         if (c != null) {
             buf.append("\nCFG:\n").append(c.toStringGraph()).append("\nInstructions:\n").append(c.toStringInstrs());
         } else {
             buf.append(toStringInstrs());
         }
         buf.append("\n}\n\n");
         return buf.toString();
     }
 
     public BlockBody getBlockBody() {
         return body;
     }
 
     @Override
     protected LocalVariable findExistingLocalVariable(String name, int scopeDepth) {
         LocalVariable lvar = lookupExistingLVar(name);
         if (lvar != null) return lvar;
 
         int newDepth = scopeDepth - 1;
 
         return newDepth >= 0 ? getLexicalParent().findExistingLocalVariable(name, newDepth) : null;
     }
 
     public LocalVariable getNewLocalVariable(String name, int depth) {
         if (depth == 0 && !(this instanceof IRFor)) {
             LocalVariable lvar = new ClosureLocalVariable(this, name, 0, getStaticScope().addVariableThisScope(name));
             localVars.put(name, lvar);
             return lvar;
         } else {
             IRScope s = this;
             int     d = depth;
             do {
                 // account for for-loops
                 while (s instanceof IRFor) {
                     depth++;
                     s = s.getLexicalParent();
                 }
 
                 // walk up
                 d--;
                 if (d >= 0) s = s.getLexicalParent();
             } while (d >= 0);
 
             return s.getNewLocalVariable(name, 0).cloneForDepth(depth);
         }
     }
 
     @Override
     public LocalVariable getLocalVariable(String name, int depth) {
         // AST doesn't seem to be implementing shadowing properly and sometimes
         // has the wrong depths which screws up variable access. So, we implement
         // shadowing here by searching for an existing local var from depth 0 and upwards.
         //
         // Check scope depths for 'a' in the closure in the following snippet:
         //
         //   "a = 1; foo(1) { |(a)| a }"
         //
         // In "(a)", it is 0 (correct), but in the body, it is 1 (incorrect)
 
         LocalVariable lvar;
         IRScope s = this;
         int d = depth;
         do {
             // account for for-loops
             while (s instanceof IRFor) {
                 depth++;
                 s = s.getLexicalParent();
             }
 
             // lookup
             lvar = s.lookupExistingLVar(name);
 
             // walk up
             d--;
             if (d >= 0) s = s.getLexicalParent();
         } while (lvar == null && d >= 0);
 
         if (lvar == null) {
             // Create a new var at requested/adjusted depth
             lvar = s.getNewLocalVariable(name, 0).cloneForDepth(depth);
         } else {
             // Find # of lexical scopes we walked up to find 'lvar'.
             // We need a copy of 'lvar' usable at that depth
             int lvarDepth = depth - (d + 1);
             if (lvar.getScopeDepth() != lvarDepth) lvar = lvar.cloneForDepth(lvarDepth);
         }
 
         return lvar;
     }
 
     public int getNestingDepth() {
         return nestingDepth;
     }
 
     protected IRClosure cloneForInlining(CloneInfo ii, IRClosure clone) {
         clone.nestingDepth  = this.nestingDepth;
         // SSS FIXME: This is fragile. Untangle this state.
         // Why is this being copied over to InterpretedIRBlockBody?
         clone.setParameterList(this.parameterList);
         clone.isBeginEndBlock = this.isBeginEndBlock;
 
         SimpleCloneInfo clonedII = ii.cloneForCloningClosure(clone);
 
         if (getCFG() != null) {
             clone.setCFG(getCFG().clone(clonedII, clone));
         } else {
             for (Instr i: getInstrs()) {
                 clone.addInstr(i.clone(clonedII));
             }
         }
 
         return clone;
     }
 
     public IRClosure cloneForInlining(CloneInfo ii) {
         IRClosure clonedClosure;
         IRScope lexicalParent = ii.getScope();
 
         if (ii instanceof SimpleCloneInfo && !((SimpleCloneInfo)ii).isEnsureBlockCloneMode()) {
             clonedClosure = new IRClosure(this, lexicalParent, closureId, getName());
         } else {
             int id = lexicalParent.getNextClosureId();
             String fullName = lexicalParent.getName() + "_CLOSURE_CLONE_" + id;
             clonedClosure = new IRClosure(this, lexicalParent, id, fullName);
         }
 
         // WrappedIRClosure should always have a single unique IRClosure in them so we should
         // not end up adding n copies of the same closure as distinct clones...
         lexicalParent.addClosure(clonedClosure);
 
         return cloneForInlining(ii, clonedClosure);
     }
 
     @Override
     public void setName(String name) {
         // We can distinguish closures only with parent scope name
         super.setName(getLexicalParent().getName() + name);
     }
 
     public Arity getArity() {
         return arity;
     }
 
     public int getArgumentType() {
         return argumentType;
     }
 
     public void setHandle(Handle handle) {
         this.handle = handle;
     }
 
     public Handle getHandle() {
         return handle;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/IRMethod.java b/core/src/main/java/org/jruby/ir/IRMethod.java
index 2a0780656a..e683e36279 100644
--- a/core/src/main/java/org/jruby/ir/IRMethod.java
+++ b/core/src/main/java/org/jruby/ir/IRMethod.java
@@ -1,136 +1,138 @@
 package org.jruby.ir;
 
+import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.internal.runtime.methods.IRMethodArgs;
 import org.jruby.ir.instructions.Instr;
 import org.jruby.ir.instructions.ReceiveArgBase;
 import org.jruby.ir.instructions.ReceiveKeywordArgInstr;
 import org.jruby.ir.instructions.ReceiveKeywordRestArgInstr;
 import org.jruby.ir.instructions.ReceiveRestArgInstr;
 import org.jruby.ir.operands.LocalVariable;
 import org.jruby.ir.operands.Operand;
 import org.jruby.ir.operands.Symbol;
 import org.jruby.ir.operands.Hash;
 import org.jruby.ir.operands.Splat;
 import org.jruby.util.KeyValuePair;
 import org.jruby.parser.StaticScope;
 
 import java.lang.invoke.MethodType;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 public class IRMethod extends IRScope {
     public final boolean isInstanceMethod;
 
     // Note that if operands from the method are modified,
     // callArgs would have to be updated as well
     //
     // Call parameters
     private List<Operand> callArgs;
     private List<KeyValuePair<Operand, Operand>> keywordArgs;
 
     // Argument description of the form [:req, "a"], [:opt, "b"] ..
     private List<String[]> argDesc;
 
     // Signatures to the jitted versions of this method
     private Map<Integer, MethodType> signatures;
 
     // Method name in the jitted version of this method
     private String jittedName;
 
     public IRMethod(IRManager manager, IRScope lexicalParent, String name,
             boolean isInstanceMethod, int lineNumber, StaticScope staticScope) {
         super(manager, lexicalParent, name, lexicalParent.getFileName(), lineNumber, staticScope);
 
         this.isInstanceMethod = isInstanceMethod;
         this.callArgs = new ArrayList<>();
         this.keywordArgs = new ArrayList<>();
         this.argDesc = new ArrayList<>();
         this.signatures = new HashMap<>();
 
         if (!getManager().isDryRun() && staticScope != null) {
             staticScope.setIRScope(this);
             staticScope.setScopeType(this.getScopeType());
         }
     }
 
     @Override
     public IRScopeType getScopeType() {
         return isInstanceMethod ? IRScopeType.INSTANCE_METHOD : IRScopeType.CLASS_METHOD;
     }
 
     @Override
     public void addInstr(Instr i) {
         // Accumulate call arguments
         if (i instanceof ReceiveKeywordRestArgInstr) {
             // Always add the keyword rest arg to the beginning
             keywordArgs.add(0, new KeyValuePair<Operand, Operand>(Symbol.KW_REST_ARG_DUMMY, ((ReceiveArgBase) i).getResult()));
         } else if (i instanceof ReceiveKeywordArgInstr) {
             ReceiveKeywordArgInstr rkai = (ReceiveKeywordArgInstr)i;
-            keywordArgs.add(new KeyValuePair<Operand, Operand>(new Symbol(rkai.argName), rkai.getResult()));
+            // FIXME: This lost encoding information when name was converted to string earlier in IRBuilder
+            keywordArgs.add(new KeyValuePair<Operand, Operand>(new Symbol(rkai.argName, USASCIIEncoding.INSTANCE), rkai.getResult()));
         } else if (i instanceof ReceiveRestArgInstr) {
             callArgs.add(new Splat(((ReceiveRestArgInstr)i).getResult(), true));
         } else if (i instanceof ReceiveArgBase) {
             callArgs.add(((ReceiveArgBase) i).getResult());
         }
 
         super.addInstr(i);
     }
 
     public void addArgDesc(IRMethodArgs.ArgType type, String argName) {
         argDesc.add(new String[]{type.name(), argName});
     }
 
     public List<String[]> getArgDesc() {
         return argDesc;
     }
 
     public Operand[] getCallArgs() {
         if (receivesKeywordArgs()) {
             int i = 0;
             Operand[] args = new Operand[callArgs.size() + 1];
             for (Operand arg: callArgs) {
                 args[i++] = arg;
             }
             args[i] = new Hash(keywordArgs, true);
             return args;
         } else {
             return callArgs.toArray(new Operand[callArgs.size()]);
         }
     }
 
     @Override
     protected LocalVariable findExistingLocalVariable(String name, int scopeDepth) {
         assert scopeDepth == 0: "Local variable depth in IRMethod should always be zero (" + name + " had depth of " + scopeDepth + ")";
         return localVars.get(name);
     }
 
     @Override
     public LocalVariable getLocalVariable(String name, int scopeDepth) {
         LocalVariable lvar = findExistingLocalVariable(name, scopeDepth);
         if (lvar == null) lvar = getNewLocalVariable(name, scopeDepth);
         return lvar;
     }
 
     public void addNativeSignature(int arity, MethodType signature) {
         signatures.put(arity, signature);
     }
 
     public MethodType getNativeSignature(int arity) {
         return signatures.get(arity);
     }
 
     public Map<Integer, MethodType> getNativeSignatures() {
         return Collections.unmodifiableMap(signatures);
     }
 
     public String getJittedName() {
         return jittedName;
     }
 
     public void setJittedName(String jittedName) {
         this.jittedName = jittedName;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/instructions/ConstMissingInstr.java b/core/src/main/java/org/jruby/ir/instructions/ConstMissingInstr.java
index f7d855d1f4..a3de0e0f2a 100644
--- a/core/src/main/java/org/jruby/ir/instructions/ConstMissingInstr.java
+++ b/core/src/main/java/org/jruby/ir/instructions/ConstMissingInstr.java
@@ -1,73 +1,75 @@
 package org.jruby.ir.instructions;
 
+import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.RubyModule;
 import org.jruby.ir.IRVisitor;
 import org.jruby.ir.Operation;
 import org.jruby.ir.operands.MethAddr;
 import org.jruby.ir.operands.Operand;
 import org.jruby.ir.operands.Symbol;
 import org.jruby.ir.operands.Variable;
 import org.jruby.ir.transformations.inlining.CloneInfo;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import java.util.Map;
 
 public class ConstMissingInstr extends CallInstr implements ResultInstr, FixedArityInstr {
     private final String missingConst;
 
     public ConstMissingInstr(Variable result, Operand currentModule, String missingConst) {
-        super(Operation.CONST_MISSING, CallType.FUNCTIONAL, result, new MethAddr("const_missing"), currentModule, new Operand[]{new Symbol(missingConst)}, null);
+        // FIXME: Missing encoding knowledge of the constant name.
+        super(Operation.CONST_MISSING, CallType.FUNCTIONAL, result, new MethAddr("const_missing"), currentModule, new Operand[]{new Symbol(missingConst, USASCIIEncoding.INSTANCE)}, null);
 
         this.missingConst = missingConst;
     }
 
     public String getMissingConst() {
         return missingConst;
     }
 
     @Override
     public void simplifyOperands(Map<Operand, Operand> valueMap, boolean force) {
         receiver = receiver.getSimplifiedOperand(valueMap, force);
     }
 
     @Override
     public Variable getResult() {
         return result;
     }
 
     @Override
     public void updateResult(Variable v) {
         this.result = v;
     }
 
     // we don't want to convert const_missing to an actual call
     @Override
     public CallBase specializeForInterpretation() {
         return this;
     }
 
     @Override
     public Instr clone(CloneInfo ii) {
         return new ConstMissingInstr(ii.getRenamedVariable(result), receiver.cloneForInlining(ii), missingConst);
     }
 
     @Override
     public String toString() {
         return super.toString() + "(" + receiver + "," + missingConst  + ")";
     }
 
     @Override
     public Object interpret(ThreadContext context, StaticScope currScope, DynamicScope currDynScope, IRubyObject self, Object[] temp) {
         RubyModule module = (RubyModule) receiver.retrieve(context, self, currScope, currDynScope, temp);
         return module.callMethod(context, "const_missing", context.runtime.fastNewSymbol(missingConst));
     }
 
     @Override
     public void visit(IRVisitor visitor) {
         visitor.ConstMissingInstr(this);
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/operands/Symbol.java b/core/src/main/java/org/jruby/ir/operands/Symbol.java
index 592dd1eeac..0fb8d649f0 100644
--- a/core/src/main/java/org/jruby/ir/operands/Symbol.java
+++ b/core/src/main/java/org/jruby/ir/operands/Symbol.java
@@ -1,35 +1,46 @@
 package org.jruby.ir.operands;
 
+import org.jcodings.Encoding;
+import org.jcodings.specific.ASCIIEncoding;
+import org.jruby.RubySymbol;
 import org.jruby.ir.IRVisitor;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class Symbol extends Reference {
-    public static final Symbol KW_REST_ARG_DUMMY = new Symbol("");
+    public static final Symbol KW_REST_ARG_DUMMY = new Symbol("", ASCIIEncoding.INSTANCE);
 
-    public Symbol(String name) {
+    private final Encoding encoding;
+
+    public Symbol(String name, Encoding encoding) {
         super(OperandType.SYMBOL, name);
+
+        this.encoding = encoding;
     }
 
     @Override
     public boolean canCopyPropagate() {
         return true;
     }
 
+    public Encoding getEncoding() {
+        return encoding;
+    }
+
     @Override
     public Object retrieve(ThreadContext context, IRubyObject self, StaticScope currScope, DynamicScope currDynScope, Object[] temp) {
-        return context.runtime.newSymbol(getName());
+        return RubySymbol.newSymbol(context.runtime, getName(), encoding);
     }
 
     @Override
     public String toString() {
         return ":'" + getName() + "'";
     }
 
     @Override
     public void visit(IRVisitor visitor) {
         visitor.Symbol(this);
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/persistence/OperandDecoderMap.java b/core/src/main/java/org/jruby/ir/persistence/OperandDecoderMap.java
index 068f30c774..60e2809fd3 100644
--- a/core/src/main/java/org/jruby/ir/persistence/OperandDecoderMap.java
+++ b/core/src/main/java/org/jruby/ir/persistence/OperandDecoderMap.java
@@ -1,135 +1,137 @@
 package org.jruby.ir.persistence;
 
 import org.jcodings.specific.ASCIIEncoding;
+import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ir.IRClosure;
 import org.jruby.ir.IRManager;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.persistence.read.parser.NonIRObjectFactory;
 import org.jruby.util.KeyValuePair;
 import org.jruby.util.RegexpOptions;
 
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.List;
 
 import static org.jruby.ir.operands.UnexecutableNil.U_NIL;
 
 /**
  *
  */
 class OperandDecoderMap {
     private final IRReaderDecoder d;
     private final IRManager manager;
 
     public OperandDecoderMap(IRManager manager, IRReaderDecoder decoder) {
         this.manager = manager;
         this.d = decoder;
     }
 
     public Operand decode(OperandType type) {
         if (RubyInstanceConfig.IR_READING_DEBUG) System.out.println("Decoding operand " + type);
 
         switch (type) {
             case ARRAY: return new Array(d.decodeOperandList());
             case AS_STRING: return new AsString(d.decodeOperand());
             case BACKREF: return new Backref(d.decodeChar());
             case BIGNUM: return new Bignum(new BigInteger(d.decodeString()));
             case BOOLEAN: return new UnboxedBoolean(d.decodeBoolean());
             case CURRENT_SCOPE: return new CurrentScope(d.decodeInt());
             case DYNAMIC_SYMBOL: return new DynamicSymbol(d.decodeOperand());
             case FIXNUM: return new Fixnum(d.decodeLong());
             case FLOAT: return new org.jruby.ir.operands.Float(d.decodeDouble());
             case GLOBAL_VARIABLE: return new GlobalVariable(d.decodeString());
             case HASH: return decodeHash();
             case IR_EXCEPTION: return IRException.getExceptionFromOrdinal(d.decodeByte());
             case LABEL: return decodeLabel();
             case LOCAL_VARIABLE: return d.getCurrentScope().getLocalVariable(d.decodeString(), d.decodeInt());
             case METHOD_HANDLE: return new MethodHandle(d.decodeOperand(), d.decodeOperand());
             case METH_ADDR: return new MethAddr(d.decodeString());
             case NIL: return manager.getNil();
             case NTH_REF: return new NthRef(d.decodeInt());
             case OBJECT_CLASS: return new ObjectClass();
             case REGEXP: return decodeRegexp();
             case SCOPE_MODULE: return new ScopeModule(d.decodeInt());
             case SELF: return Self.SELF;
             case SPLAT: return new Splat(d.decodeOperand(), d.decodeBoolean());
             case STANDARD_ERROR: return new StandardError();
             case STRING_LITERAL: return new StringLiteral(d.decodeString());
             case SVALUE: return new SValue(d.decodeOperand());
-            case SYMBOL: return new Symbol(d.decodeString());
+            // FIXME: This is broken since there is no encode/decode for encoding
+            case SYMBOL: return new Symbol(d.decodeString(), USASCIIEncoding.INSTANCE);
             case TEMPORARY_VARIABLE: return decodeTemporaryVariable();
             case UNBOXED_BOOLEAN: return new UnboxedBoolean(d.decodeBoolean());
             case UNBOXED_FIXNUM: return new UnboxedFixnum(d.decodeLong());
             case UNBOXED_FLOAT: return new UnboxedFloat(d.decodeDouble());
             case UNDEFINED_VALUE: return UndefinedValue.UNDEFINED;
             case UNEXECUTABLE_NIL: return U_NIL;
             case WRAPPED_IR_CLOSURE: return new WrappedIRClosure(d.decodeVariable(), (IRClosure) d.decodeScope());
         }
 
         return null;
     }
 
     private Operand decodeHash() {
         int size = d.decodeInt();
         List<KeyValuePair<Operand, Operand>> pairs = new ArrayList<KeyValuePair<Operand, Operand>>(size);
 
         for (int i = 0; i < size; i++) {
             pairs.add(new KeyValuePair(d.decodeOperand(), d.decodeOperand()));
         }
 
         return new Hash(pairs);
     }
 
     private Operand decodeLabel() {
         String prefix = d.decodeString();
         int id = d.decodeInt();
 
         // Special case of label
         if ("_GLOBAL_ENSURE_BLOCK".equals(prefix)) return new Label("_GLOBAL_ENSURE_BLOCK", 0);
 
         // Check if this label was already created
         // Important! Program would not be interpreted correctly
         // if new name will be created every time
         String fullLabel = prefix + "_" + id;
         if (d.getVars().containsKey(fullLabel)) {
             return d.getVars().get(fullLabel);
         }
 
         Label newLabel = new Label(prefix, id);
 
         // Add to context for future reuse
         d.getVars().put(fullLabel, newLabel);
 
         return newLabel;
     }
 
     private Regexp decodeRegexp() {
         Operand regex = d.decodeOperand();
         boolean isNone = d.decodeBoolean();
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(d.decodeInt());
         options.setEncodingNone(isNone);
         return new Regexp(regex, options);
     }
 
     private Operand decodeTemporaryVariable() {
         TemporaryVariableType type = d.decodeTemporaryVariableType();
 
         switch(type) {
             case CLOSURE:
                 return new TemporaryClosureVariable(d.decodeInt(), d.decodeInt());
             case CURRENT_MODULE:
                 return new TemporaryCurrentModuleVariable(d.decodeInt());
             case CURRENT_SCOPE:
                 return new TemporaryCurrentScopeVariable(d.decodeInt());
             case FLOAT:
                 return new TemporaryFloatVariable(d.decodeInt());
             case FIXNUM:
                 return new TemporaryFixnumVariable(d.decodeInt());
             case LOCAL:
                 return new TemporaryLocalVariable(d.decodeInt());
         }
 
         return null;
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/runtime/IRRuntimeHelpers.java b/core/src/main/java/org/jruby/ir/runtime/IRRuntimeHelpers.java
index 9411ae92a6..a4da459e2f 100644
--- a/core/src/main/java/org/jruby/ir/runtime/IRRuntimeHelpers.java
+++ b/core/src/main/java/org/jruby/ir/runtime/IRRuntimeHelpers.java
@@ -1,1286 +1,1290 @@
 package org.jruby.ir.runtime;
 
 import com.headius.invokebinder.Signature;
 import org.jcodings.Encoding;
 import org.jruby.*;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.Unrescuable;
 import org.jruby.internal.runtime.methods.CompiledIRMetaClassBody;
 import org.jruby.internal.runtime.methods.CompiledIRMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.InterpretedIRMetaClassBody;
 import org.jruby.internal.runtime.methods.InterpretedIRMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.ir.IRClassBody;
 import org.jruby.ir.IRMetaClassBody;
 import org.jruby.ir.IRModuleBody;
 import org.jruby.ir.IRScope;
 import org.jruby.ir.IRScopeType;
 import org.jruby.ir.Interp;
 import org.jruby.ir.JIT;
 import org.jruby.ir.operands.IRException;
 import org.jruby.ir.operands.Operand;
 import org.jruby.ir.operands.Splat;
 import org.jruby.ir.operands.UndefinedValue;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.parser.StaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.DefinedMessage;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.TypeConverter;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.Type;
 
 import java.lang.invoke.MethodHandle;
 import java.util.Arrays;
 
 public class IRRuntimeHelpers {
     private static final Logger LOG = LoggerFactory.getLogger("IRRuntimeHelpers");
 
     public static boolean inProfileMode() {
         return RubyInstanceConfig.IR_PROFILE;
     }
 
     public static boolean isDebug() {
         return RubyInstanceConfig.IR_DEBUG;
     }
 
     public static boolean inNonMethodBodyLambda(StaticScope scope, Block.Type blockType) {
         // SSS FIXME: Hack! AST interpreter and JIT compiler marks a proc's static scope as
         // an argument scope if it is used to define a method's body via :define_method.
         // Since that is exactly what we want to figure out here, am just using that flag here.
         // But, this is ugly (as is the original hack in the current runtime).  What is really
         // needed is a new block type -- a block that is used to define a method body.
         return blockType == Block.Type.LAMBDA && !scope.isArgumentScope();
     }
 
     public static boolean inLambda(Block.Type blockType) {
         return blockType == Block.Type.LAMBDA;
     }
 
     public static boolean inProc(Block.Type blockType) {
         return blockType == Block.Type.PROC;
     }
 
     public static void checkForLJE(ThreadContext context, DynamicScope dynScope, boolean maybeLambda, Block.Type blockType) {
         if (IRRuntimeHelpers.inLambda(blockType)) return;
 
         StaticScope scope = dynScope.getStaticScope();
         IRScopeType scopeType = scope.getScopeType();
         boolean inDefineMethod = false;
         while (dynScope != null) {
             StaticScope ss = dynScope.getStaticScope();
             // SSS FIXME: Why is scopeType empty? Looks like this static-scope
             // was not associated with the AST scope that got converted to IR.
             //
             // Ruby code: lambda { Thread.new { return }.join }.call
             //
             // To be investigated.
             IRScopeType ssType = ss.getScopeType();
             if (ssType != null) {
                 if (ssType.isMethodType()) {
                     break;
                 } else if (ss.isArgumentScope() && ssType.isClosureType() && ssType != IRScopeType.EVAL_SCRIPT) {
                     inDefineMethod = true;
                     break;
                 }
             }
             dynScope = dynScope.getParentScope();
         }
 
         // SSS FIXME: Why is scopeType empty? Looks like this static-scope
         // was not associated with the AST scope that got converted to IR.
         //
         // Ruby code: lambda { Thread.new { return }.join }.call
         //
         // To be investigated.
         if (   (scopeType == null || (!inDefineMethod && scopeType.isClosureType() && scopeType != IRScopeType.EVAL_SCRIPT))
             && (maybeLambda || !context.scopeExistsOnCallStack(dynScope)))
         {
             // Cannot return from the call that we have long since exited.
             throw IRException.RETURN_LocalJumpError.getException(context.runtime);
         }
     }
 
     /*
      * Handle non-local returns (ex: when nested in closures, root scopes of module/class/sclass bodies)
      */
     public static IRubyObject initiateNonLocalReturn(ThreadContext context, DynamicScope dynScope, Block.Type blockType, IRubyObject returnValue) {
         // If not in a lambda, check if this was a non-local return
         if (IRRuntimeHelpers.inLambda(blockType)) return returnValue;
 
         IRScopeType scopeType = dynScope.getStaticScope().getScopeType();
         while (dynScope != null) {
             StaticScope ss = dynScope.getStaticScope();
             // SSS FIXME: Why is scopeType empty? Looks like this static-scope
             // was not associated with the AST scope that got converted to IR.
             //
             // Ruby code: lambda { Thread.new { return }.join }.call
             //
             // To be investigated.
             IRScopeType ssType = ss.getScopeType();
             if (ssType != null) {
                 if (ssType.isMethodType() || (ss.isArgumentScope() && ssType.isClosureType() && ssType != IRScopeType.EVAL_SCRIPT)) {
                     break;
                 }
             }
             dynScope = dynScope.getParentScope();
         }
 
         // methodtoReturnFrom will not be -1 for explicit returns from class/module/sclass bodies
         throw IRReturnJump.create(dynScope, returnValue);
     }
 
     @JIT
     public static IRubyObject handleNonlocalReturn(StaticScope scope, DynamicScope dynScope, Object rjExc, Block.Type blockType) throws RuntimeException {
         if (!(rjExc instanceof IRReturnJump)) {
             Helpers.throwException((Throwable)rjExc);
             return null;
         } else {
             IRReturnJump rj = (IRReturnJump)rjExc;
 
             // If we are in a lambda or if we are in the method scope we are supposed to return from, stop propagating.
             if (inNonMethodBodyLambda(scope, blockType) || (rj.methodToReturnFrom == dynScope)) {
                 if (isDebug()) System.out.println("---> Non-local Return reached target in scope: " + dynScope + " matching dynscope? " + (rj.methodToReturnFrom == dynScope));
                 return (IRubyObject) rj.returnValue;
             }
 
             // If not, Just pass it along!
             throw rj;
         }
     }
 
     public static IRubyObject initiateBreak(ThreadContext context, DynamicScope dynScope, IRubyObject breakValue, Block.Type blockType) throws RuntimeException {
         if (inLambda(blockType)) {
             // Ensures would already have been run since the IR builder makes
             // sure that ensure code has run before we hit the break.  Treat
             // the break as a regular return from the closure.
             return breakValue;
         } else {
             StaticScope scope = dynScope.getStaticScope();
             IRScopeType scopeType = scope.getScopeType();
             if (!scopeType.isClosureType()) {
                 // Error -- breaks can only be initiated in closures
                 throw IRException.BREAK_LocalJumpError.getException(context.runtime);
             }
 
             IRBreakJump bj = IRBreakJump.create(dynScope.getParentScope(), breakValue);
             if (scopeType == IRScopeType.EVAL_SCRIPT) {
                 // If we are in an eval, record it so we can account for it
                 bj.breakInEval = true;
             }
 
             // Start the process of breaking through the intermediate scopes
             throw bj;
         }
     }
 
     @JIT
     public static IRubyObject handleBreakAndReturnsInLambdas(ThreadContext context, StaticScope scope, DynamicScope dynScope, Object exc, Block.Type blockType) throws RuntimeException {
         if ((exc instanceof IRBreakJump) && inNonMethodBodyLambda(scope, blockType)) {
             // We just unwound all the way up because of a non-local break
             throw IRException.BREAK_LocalJumpError.getException(context.getRuntime());
         } else if (exc instanceof IRReturnJump && (blockType == null || inLambda(blockType))) {
             // Ignore non-local return processing in non-lambda blocks.
             // Methods have a null blocktype
             return handleNonlocalReturn(scope, dynScope, exc, blockType);
         } else {
             // Propagate
             Helpers.throwException((Throwable)exc);
             // should not get here
             return null;
         }
     }
 
     @JIT
     public static IRubyObject handlePropagatedBreak(ThreadContext context, DynamicScope dynScope, Object bjExc, Block.Type blockType) {
         if (!(bjExc instanceof IRBreakJump)) {
             Helpers.throwException((Throwable)bjExc);
             return null;
         }
 
         IRBreakJump bj = (IRBreakJump)bjExc;
         if (bj.breakInEval) {
             // If the break was in an eval, we pretend as if it was in the containing scope
             StaticScope scope = dynScope.getStaticScope();
             IRScopeType scopeType = scope.getScopeType();
             if (!scopeType.isClosureType()) {
                 // Error -- breaks can only be initiated in closures
                 throw IRException.BREAK_LocalJumpError.getException(context.getRuntime());
             } else {
                 bj.breakInEval = false;
                 throw bj;
             }
         } else if (bj.scopeToReturnTo == dynScope) {
             // Done!! Hurray!
             if (isDebug()) System.out.println("---> Break reached target in scope: " + dynScope);
             return bj.breakValue;
 /* ---------------------------------------------------------------
  * SSS FIXME: Puzzled .. Why is this not needed?
         } else if (!context.scopeExistsOnCallStack(bj.scopeToReturnTo.getStaticScope())) {
             throw IRException.BREAK_LocalJumpError.getException(context.runtime);
  * --------------------------------------------------------------- */
         } else {
             // Propagate
             throw bj;
         }
     }
 
     @JIT
     public static void defCompiledIRMethod(ThreadContext context, MethodHandle handle, String rubyName, DynamicScope currDynScope, IRubyObject self, IRScope irScope) {
         Ruby runtime = context.runtime;
 
         RubyModule containingClass = IRRuntimeHelpers.findInstanceMethodContainer(context, currDynScope, self);
         Visibility currVisibility = context.getCurrentVisibility();
         Visibility newVisibility = Helpers.performNormalMethodChecksAndDetermineVisibility(runtime, containingClass, rubyName, currVisibility);
 
         DynamicMethod method = new CompiledIRMethod(handle, irScope, newVisibility, containingClass);
 
         Helpers.addInstanceMethod(containingClass, rubyName, method, currVisibility, context, runtime);
     }
 
     @JIT
     public static void defCompiledIRClassMethod(ThreadContext context, IRubyObject obj, MethodHandle handle, String rubyName, IRScope irScope) {
         Ruby runtime = context.runtime;
 
         if (obj instanceof RubyFixnum || obj instanceof RubySymbol) {
             throw runtime.newTypeError("can't define singleton method \"" + rubyName + "\" for " + obj.getMetaClass().getBaseName());
         }
 
         if (obj.isFrozen()) throw runtime.newFrozenError("object");
 
         RubyClass containingClass = obj.getSingletonClass();
 
         DynamicMethod method = new CompiledIRMethod(handle, irScope, Visibility.PUBLIC, containingClass);
 
         containingClass.addMethod(rubyName, method);
 
         obj.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(rubyName));
     }
 
     // Used by JIT
     public static IRubyObject undefMethod(ThreadContext context, Object nameArg, DynamicScope currDynScope, IRubyObject self) {
         RubyModule module = IRRuntimeHelpers.findInstanceMethodContainer(context, currDynScope, self);
         String name = (nameArg instanceof String) ?
                 (String) nameArg : nameArg.toString();
 
         if (module == null) {
             throw context.runtime.newTypeError("No class to undef method '" + name + "'.");
         }
 
         module.undef(context, name);
 
         return context.runtime.getNil();
     }
 
     public static double unboxFloat(IRubyObject val) {
         if (val instanceof RubyFloat) {
             return ((RubyFloat)val).getValue();
         } else {
             return ((RubyFixnum)val).getDoubleValue();
         }
     }
 
     public static long unboxFixnum(IRubyObject val) {
         if (val instanceof RubyFloat) {
             return (long)((RubyFloat)val).getValue();
         } else {
             return ((RubyFixnum)val).getLongValue();
         }
     }
 
     public static boolean flt(double v1, double v2) {
         return v1 < v2;
     }
 
     public static boolean fgt(double v1, double v2) {
         return v1 > v2;
     }
 
     public static boolean feq(double v1, double v2) {
         return v1 == v2;
     }
 
     public static boolean ilt(long v1, long v2) {
         return v1 < v2;
     }
 
     public static boolean igt(long v1, long v2) {
         return v1 > v2;
     }
 
     public static boolean ieq(long v1, long v2) {
         return v1 == v2;
     }
 
     public static Object unwrapRubyException(Object excObj) {
         // Unrescuable:
         //   IRBreakJump, IRReturnJump, ThreadKill, RubyContinuation, MainExitException, etc.
         //   These cannot be rescued -- only run ensure blocks
         if (excObj instanceof Unrescuable) {
             Helpers.throwException((Throwable)excObj);
         }
         // Ruby exceptions, errors, and other java exceptions.
         // These can be rescued -- run rescue blocks
         return (excObj instanceof RaiseException) ? ((RaiseException)excObj).getException() : excObj;
     }
 
     private static boolean isJavaExceptionHandled(ThreadContext context, IRubyObject excType, Object excObj, boolean arrayCheck) {
         if (!(excObj instanceof Throwable)) {
             return false;
         }
 
         Ruby runtime = context.runtime;
         Throwable throwable = (Throwable)excObj;
 
         if (excType instanceof RubyArray) {
             RubyArray testTypes = (RubyArray)excType;
             for (int i = 0, n = testTypes.getLength(); i < n; i++) {
                 IRubyObject testType = testTypes.eltInternal(i);
                 if (IRRuntimeHelpers.isJavaExceptionHandled(context, testType, throwable, true)) {
                     IRubyObject exceptionObj;
                     if (n == 1 && testType == runtime.getNativeException()) {
                         // wrap Throwable in a NativeException object
                         exceptionObj = new NativeException(runtime, runtime.getNativeException(), throwable);
                         ((NativeException)exceptionObj).prepareIntegratedBacktrace(context, throwable.getStackTrace());
                     } else {
                         // wrap as normal JI object
                         exceptionObj = JavaUtil.convertJavaToUsableRubyObject(runtime, throwable);
                     }
 
                     runtime.getGlobalVariables().set("$!", exceptionObj);
                     return true;
                 }
             }
         } else if (Helpers.checkJavaException(throwable, excType, context)) {
             IRubyObject exceptionObj;
             if (excType == runtime.getNativeException()) {
                 // wrap Throwable in a NativeException object
                 exceptionObj = new NativeException(runtime, runtime.getNativeException(), throwable);
                 ((NativeException)exceptionObj).prepareIntegratedBacktrace(context, throwable.getStackTrace());
             } else {
                 // wrap as normal JI object
                 exceptionObj = JavaUtil.convertJavaToUsableRubyObject(runtime, throwable);
             }
 
             runtime.getGlobalVariables().set("$!", exceptionObj);
             return true;
         }
 
         return false;
     }
 
     private static boolean isRubyExceptionHandled(ThreadContext context, IRubyObject excType, Object excObj) {
         if (excType instanceof RubyArray) {
             RubyArray testTypes = (RubyArray)excType;
             for (int i = 0, n = testTypes.getLength(); i < n; i++) {
                 IRubyObject testType = testTypes.eltInternal(i);
                 if (IRRuntimeHelpers.isRubyExceptionHandled(context, testType, excObj)) {
                     context.runtime.getGlobalVariables().set("$!", (IRubyObject)excObj);
                     return true;
                 }
             }
         } else if (excObj instanceof IRubyObject) {
             // SSS FIXME: Should this check be "runtime.getModule().isInstance(excType)"??
             if (!(excType instanceof RubyModule)) {
                 throw context.runtime.newTypeError("class or module required for rescue clause. Found: " + excType);
             }
 
             if (excType.callMethod(context, "===", (IRubyObject)excObj).isTrue()) {
                 context.runtime.getGlobalVariables().set("$!", (IRubyObject)excObj);
                 return true;
             }
         }
         return false;
     }
 
     public static IRubyObject isExceptionHandled(ThreadContext context, IRubyObject excType, Object excObj) {
         // SSS FIXME: JIT should do an explicit unwrap in code just like in interpreter mode.
         // This is called once for each RescueEQQ instr and unwrapping each time is unnecessary.
         // This is not a performance issue, but more a question of where this belongs.
         // It seems more logical to (a) recv-exc (b) unwrap-exc (c) do all the rescue-eqq checks.
         //
         // Unwrap Ruby exceptions
         excObj = unwrapRubyException(excObj);
 
         boolean ret = IRRuntimeHelpers.isRubyExceptionHandled(context, excType, excObj)
             || IRRuntimeHelpers.isJavaExceptionHandled(context, excType, excObj, false);
 
         return context.runtime.newBoolean(ret);
     }
 
     public static IRubyObject isEQQ(ThreadContext context, IRubyObject receiver, IRubyObject value) {
         boolean isUndefValue = value == UndefinedValue.UNDEFINED;
         if (receiver instanceof RubyArray) {
             RubyArray testVals = (RubyArray)receiver;
             for (int i = 0, n = testVals.getLength(); i < n; i++) {
                 IRubyObject v = testVals.eltInternal(i);
                 IRubyObject eqqVal = isUndefValue ? v : v.callMethod(context, "===", value);
                 if (eqqVal.isTrue()) return eqqVal;
             }
             return context.runtime.newBoolean(false);
         } else {
             return isUndefValue ? receiver : receiver.callMethod(context, "===", value);
         }
     }
 
     public static IRubyObject newProc(Ruby runtime, Block block) {
         return (block == Block.NULL_BLOCK) ? runtime.getNil() : runtime.newProc(Block.Type.PROC, block);
     }
 
     public static IRubyObject yield(ThreadContext context, Object blk, Object yieldArg, boolean unwrapArray) {
         if (blk instanceof RubyProc) blk = ((RubyProc)blk).getBlock();
         if (blk instanceof RubyNil) blk = Block.NULL_BLOCK;
         Block b = (Block)blk;
         IRubyObject yieldVal = (IRubyObject)yieldArg;
         return (unwrapArray && (yieldVal instanceof RubyArray)) ? b.yieldArray(context, yieldVal, null) : b.yield(context, yieldVal);
     }
 
     public static IRubyObject yieldSpecific(ThreadContext context, Object blk) {
         if (blk instanceof RubyProc) blk = ((RubyProc)blk).getBlock();
         if (blk instanceof RubyNil) blk = Block.NULL_BLOCK;
         Block b = (Block)blk;
         return b.yieldSpecific(context);
     }
 
     public static IRubyObject[] convertValueIntoArgArray(ThreadContext context, IRubyObject value, Arity arity, boolean argIsArray) {
         // SSS FIXME: This should not really happen -- so, some places in the runtime library are breaking this contract.
         if (argIsArray && !(value instanceof RubyArray)) argIsArray = false;
 
         int blockArity = arity.getValue();
         switch (blockArity) {
             case -1 : return argIsArray ? ((RubyArray)value).toJavaArray() : new IRubyObject[] { value };
             case  0 : return new IRubyObject[] { value };
             case  1 : {
                if (argIsArray) {
                    RubyArray valArray = ((RubyArray)value);
                    if (valArray.size() == 0) {
                        value = RubyArray.newEmptyArray(context.runtime);
                    }
                }
                return new IRubyObject[] { value };
             }
             default :
                 if (argIsArray) {
                     RubyArray valArray = (RubyArray)value;
                     if (valArray.size() == 1) value = valArray.eltInternal(0);
                     value = Helpers.aryToAry(value);
                     return (value instanceof RubyArray) ? ((RubyArray)value).toJavaArray() : new IRubyObject[] { value };
                 } else {
                     IRubyObject val0 = Helpers.aryToAry(value);
                     if (!(val0 instanceof RubyArray)) {
                         throw context.runtime.newTypeError(value.getType().getName() + "#to_ary should return Array");
                     }
                     return ((RubyArray)val0).toJavaArray();
                 }
         }
     }
 
     public static Block getBlockFromObject(ThreadContext context, Object value) {
         Block block;
         if (value instanceof Block) {
             block = (Block) value;
         } else if (value instanceof RubyProc) {
             block = ((RubyProc) value).getBlock();
         } else if (value instanceof RubyMethod) {
             block = ((RubyProc)((RubyMethod)value).to_proc(context, null)).getBlock();
         } else if ((value instanceof IRubyObject) && ((IRubyObject)value).isNil()) {
             block = Block.NULL_BLOCK;
         } else if (value instanceof IRubyObject) {
             block = ((RubyProc) TypeConverter.convertToType((IRubyObject) value, context.runtime.getProc(), "to_proc", true)).getBlock();
         } else {
             throw new RuntimeException("Unhandled case in CallInstr:prepareBlock.  Got block arg: " + value);
         }
         return block;
     }
 
     public static void checkArity(ThreadContext context, Object[] args, int required, int opt, int rest,
                                   boolean receivesKwargs, int restKey) {
         int argsLength = args.length;
         RubyHash keywordArgs = (RubyHash) extractKwargsHash(args, required, receivesKwargs);
 
         if (restKey == -1 && keywordArgs != null) checkForExtraUnwantedKeywordArgs(context, keywordArgs);
 
         // keyword arguments value is not used for arity checking.
         if (keywordArgs != null) argsLength -= 1;
 
         if (argsLength < required || (rest == -1 && argsLength > (required + opt))) {
 //            System.out.println("NUMARGS: " + argsLength + ", REQUIRED: " + required + ", OPT: " + opt + ", AL: " + args.length + ",RKW: " + receivesKwargs );
 //            System.out.println("ARGS[0]: " + args[0]);
 
             Arity.raiseArgumentError(context.runtime, argsLength, required, required + opt);
         }
     }
 
     public static RubyHash extractKwargsHash(Object[] args, int requiredArgsCount, boolean receivesKwargs) {
         if (!receivesKwargs) return null;
         if (args.length <= requiredArgsCount) return null; // No kwarg because required args slurp them up.
 
         Object lastArg = args[args.length - 1];
 
         return !(lastArg instanceof RubyHash) ? null : (RubyHash) lastArg;
     }
 
     public static void checkForExtraUnwantedKeywordArgs(final ThreadContext context, RubyHash keywordArgs) {
         final StaticScope scope = context.getCurrentStaticScope();
 
         keywordArgs.visitAll(new RubyHash.Visitor() {
             @Override
             public void visit(IRubyObject key, IRubyObject value) {
                 String keyAsString = key.asJavaString();
                 int slot = scope.isDefined((keyAsString));
 
                 // Found name in higher variable scope.  Therefore non for this block/method def.
                 if ((slot >> 16) > 0) throw context.runtime.newArgumentError("unknown keyword: " + keyAsString);
                 // Could not find it anywhere.
                 if (((short) (slot & 0xffff)) < 0) throw context.runtime.newArgumentError("unknown keyword: " + keyAsString);
             }
         });
     }
 
     public static IRubyObject match3(ThreadContext context, RubyRegexp regexp, IRubyObject argValue) {
         if (argValue instanceof RubyString) {
             return regexp.op_match19(context, argValue);
         } else {
             return argValue.callMethod(context, "=~", regexp);
         }
     }
 
     public static IRubyObject extractOptionalArgument(RubyArray rubyArray, int minArgsLength, int index) {
         int n = rubyArray.getLength();
         return minArgsLength < n ? rubyArray.entry(index) : UndefinedValue.UNDEFINED;
     }
 
     @JIT
     public static IRubyObject isDefinedBackref(ThreadContext context) {
         return RubyMatchData.class.isInstance(context.getBackRef()) ?
                 context.runtime.getDefinedMessage(DefinedMessage.GLOBAL_VARIABLE) : context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedGlobal(ThreadContext context, String name) {
         return context.runtime.getGlobalVariables().isDefined(name) ?
                 context.runtime.getDefinedMessage(DefinedMessage.GLOBAL_VARIABLE) : context.nil;
     }
 
     // FIXME: This checks for match data differently than isDefinedBackref.  Seems like they should use same mechanism?
     @JIT
     public static IRubyObject isDefinedNthRef(ThreadContext context, int matchNumber) {
         IRubyObject backref = context.getBackRef();
 
         if (backref instanceof RubyMatchData) {
             if (!((RubyMatchData) backref).group(matchNumber).isNil()) {
                 return context.runtime.getDefinedMessage(DefinedMessage.GLOBAL_VARIABLE);
             }
         }
 
         return context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedClassVar(ThreadContext context, RubyModule receiver, String name) {
         boolean defined = receiver.isClassVarDefined(name);
 
         if (!defined && receiver.isSingleton()) { // Look for class var in singleton if it is one.
             IRubyObject attached = ((MetaClass) receiver).getAttached();
 
             if (attached instanceof RubyModule) defined = ((RubyModule) attached).isClassVarDefined(name);
         }
 
         return defined ? context.runtime.getDefinedMessage(DefinedMessage.CLASS_VARIABLE) : context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedInstanceVar(ThreadContext context, IRubyObject receiver, String name) {
         return receiver.getInstanceVariables().hasInstanceVariable(name) ?
                 context.runtime.getDefinedMessage(DefinedMessage.INSTANCE_VARIABLE) : context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedCall(ThreadContext context, IRubyObject self, IRubyObject receiver, String name) {
         RubyString boundValue = Helpers.getDefinedCall(context, self, receiver, name);
 
         return boundValue == null ? context.nil : boundValue;
     }
 
     @JIT
     public static IRubyObject isDefinedConstantOrMethod(ThreadContext context, IRubyObject receiver, String name) {
         RubyString definedType = Helpers.getDefinedConstantOrBoundMethod(receiver, name);
 
         return definedType == null ? context.nil : definedType;
     }
 
     @JIT
     public static IRubyObject isDefinedMethod(ThreadContext context, IRubyObject receiver, String name, boolean checkIfPublic) {
         DynamicMethod method = receiver.getMetaClass().searchMethod(name);
 
         // If we find the method we optionally check if it is public before returning "method".
         if (!method.isUndefined() &&  (!checkIfPublic || method.getVisibility() == Visibility.PUBLIC)) {
             return context.runtime.getDefinedMessage(DefinedMessage.METHOD);
         }
 
         return context.nil;
     }
 
     @JIT
     public static IRubyObject isDefinedSuper(ThreadContext context, IRubyObject receiver) {
         boolean flag = false;
         String frameName = context.getFrameName();
 
         if (frameName != null) {
             RubyModule frameClass = context.getFrameKlazz();
             if (frameClass != null) {
                 flag = Helpers.findImplementerIfNecessary(receiver.getMetaClass(), frameClass).getSuperClass().isMethodBound(frameName, false);
             }
         }
         return flag ? context.runtime.getDefinedMessage(DefinedMessage.SUPER) : context.nil;
     }
 
     protected static void checkSuperDisabledOrOutOfMethod(ThreadContext context, RubyModule frameClass, String methodName) {
         // FIXME: super/zsuper in top-level script still seems to have a frameClass so it will not make it into this if
         if (frameClass == null) {
             if (methodName == null || !methodName.equals("")) {
                 throw context.runtime.newNameError("superclass method '" + methodName + "' disabled", methodName);
             } else {
                 throw context.runtime.newNoMethodError("super called outside of method", null, context.runtime.getNil());
             }
         }
     }
 
     public static IRubyObject nthMatch(ThreadContext context, int matchNumber) {
         return RubyRegexp.nth_match(matchNumber, context.getBackRef());
     }
 
     public static void defineAlias(ThreadContext context, IRubyObject self, DynamicScope currDynScope, String newNameString, String oldNameString) {
         if (self == null || self instanceof RubyFixnum || self instanceof RubySymbol) {
             throw context.runtime.newTypeError("no class to make alias");
         }
 
         RubyModule module = findInstanceMethodContainer(context, currDynScope, self);
         module.defineAlias(newNameString, oldNameString);
         module.callMethod(context, "method_added", context.runtime.newSymbol(newNameString));
     }
 
     public static RubyModule getModuleFromScope(ThreadContext context, StaticScope scope, IRubyObject arg) {
         Ruby runtime = context.runtime;
         RubyModule rubyClass = scope.getModule();
 
         // SSS FIXME: Copied from ASTInterpreter.getClassVariableBase and adapted
         while (scope != null && (rubyClass.isSingleton() || rubyClass == runtime.getDummy())) {
             scope = scope.getPreviousCRefScope();
             rubyClass = scope.getModule();
             if (scope.getPreviousCRefScope() == null) {
                 runtime.getWarnings().warn(IRubyWarnings.ID.CVAR_FROM_TOPLEVEL_SINGLETON_METHOD, "class variable access from toplevel singleton method");
             }
         }
 
         if ((scope == null) && (arg != null)) {
             // We ran out of scopes to check -- look in arg's metaclass
             rubyClass = arg.getMetaClass();
         }
 
         if (rubyClass == null) {
             throw context.runtime.newTypeError("no class/module to define class variable");
         }
 
         return rubyClass;
     }
 
     @JIT
     public static IRubyObject mergeKeywordArguments(ThreadContext context, IRubyObject restKwarg, IRubyObject explcitKwarg) {
         return ((RubyHash) TypeConverter.checkHashType(context.runtime, restKwarg)).merge(context, explcitKwarg, Block.NULL_BLOCK);
     }
 
     public static RubyModule findInstanceMethodContainer(ThreadContext context, DynamicScope currDynScope, IRubyObject self) {
         boolean inBindingEval = currDynScope.inBindingEval();
 
         // Top-level-scripts are special but, not if binding-evals are in force!
         if (!inBindingEval && self == context.runtime.getTopSelf()) return self.getType();
 
         for (DynamicScope ds = currDynScope; ds != null; ) {
             IRScopeType scopeType = ds.getStaticScope().getScopeType();
             switch (ds.getEvalType()) {
                 case MODULE_EVAL  : return (RubyModule) self;
                 case INSTANCE_EVAL: return self.getSingletonClass();
                 case BINDING_EVAL : ds = ds.getParentScope(); break;
                 case NONE:
                     if (scopeType == null || scopeType.isClosureType()) {
                         ds = ds.getParentScope();
                     } else if (inBindingEval) {
                         // Binding evals are special!
                         return ds.getStaticScope().getModule();
                     } else if (scopeType == IRScopeType.CLASS_METHOD) {
                         return (RubyModule) self;
                     } else if (scopeType == IRScopeType.INSTANCE_METHOD) {
                         return self.getMetaClass();
                     } else {
                         switch (scopeType) {
                             case MODULE_BODY:
                             case CLASS_BODY:
                                 return (RubyModule)self;
                             case METACLASS_BODY:
                                 return (RubyModule) self;
 
                             default:
                                 throw new RuntimeException("Should not get here! scopeType is " + scopeType);
                         }
                     }
                     break;
             }
         }
 
         throw new RuntimeException("Should not get here!");
     }
 
     public static RubyBoolean isBlockGiven(ThreadContext context, Object blk) {
         if (blk instanceof RubyProc) blk = ((RubyProc)blk).getBlock();
         if (blk instanceof RubyNil) blk = Block.NULL_BLOCK;
         Block b = (Block)blk;
         return context.runtime.newBoolean(b.isGiven());
     }
 
     public static IRubyObject receiveRestArg(ThreadContext context, Object[] args, int required, int argIndex, boolean acceptsKeywordArguments) {
         RubyHash keywordArguments = extractKwargsHash(args, required, acceptsKeywordArguments);
         return constructRestArg(context, args, keywordArguments, required, argIndex);
     }
 
     public static IRubyObject constructRestArg(ThreadContext context, Object[] args, RubyHash keywordArguments, int required, int argIndex) {
         int argsLength = keywordArguments != null ? args.length - 1 : args.length;
         int remainingArguments = argsLength - required;
 
         if (remainingArguments <= 0) return context.runtime.newArray(IRubyObject.NULL_ARRAY);
 
         IRubyObject[] restArgs = new IRubyObject[remainingArguments];
         System.arraycopy(args, argIndex, restArgs, 0, remainingArguments);
 
         return context.runtime.newArray(restArgs);
     }
 
     public static IRubyObject receivePostReqdArg(IRubyObject[] args, int preReqdArgsCount, int postReqdArgsCount, int argIndex, boolean acceptsKeywordArgument) {
         boolean kwargs = extractKwargsHash(args, preReqdArgsCount + postReqdArgsCount, acceptsKeywordArgument) != null;
         int n = kwargs ? args.length - 1 : args.length;
         int remaining = n - preReqdArgsCount;
         if (remaining <= argIndex) return null;  // For blocks!
 
         return (remaining > postReqdArgsCount) ? args[n - postReqdArgsCount + argIndex] : args[preReqdArgsCount + argIndex];
     }
 
     public static IRubyObject receiveOptArg(IRubyObject[] args, int requiredArgs, int preArgs, int argIndex, boolean acceptsKeywordArgument) {
         int optArgIndex = argIndex;  // which opt arg we are processing? (first one has index 0, second 1, ...).
         RubyHash keywordArguments = extractKwargsHash(args, requiredArgs, acceptsKeywordArgument);
         int argsLength = keywordArguments != null ? args.length - 1 : args.length;
 
         if (requiredArgs + optArgIndex >= argsLength) return UndefinedValue.UNDEFINED; // No more args left
 
         return args[preArgs + optArgIndex];
     }
 
     public static IRubyObject getPreArgSafe(ThreadContext context, IRubyObject[] args, int argIndex) {
         IRubyObject result;
         result = argIndex < args.length ? args[argIndex] : context.nil; // SSS FIXME: This check is only required for closures, not methods
         return result;
     }
 
     public static IRubyObject receiveKeywordArg(ThreadContext context, IRubyObject[] args, int required, String argName, boolean acceptsKeywordArgument) {
         RubyHash keywordArguments = extractKwargsHash(args, required, acceptsKeywordArgument);
 
         if (keywordArguments == null) return UndefinedValue.UNDEFINED;
 
         RubySymbol keywordName = context.getRuntime().newSymbol(argName);
 
         if (keywordArguments.fastARef(keywordName) == null) return UndefinedValue.UNDEFINED;
 
         // SSS FIXME: Can we use an internal delete here?
         // Enebo FIXME: Delete seems wrong if we are doing this for duplication purposes.
         return keywordArguments.delete(context, keywordName, Block.NULL_BLOCK);
     }
 
     public static IRubyObject receiveKeywordRestArg(ThreadContext context, IRubyObject[] args, int required, boolean keywordArgumentSupplied) {
         RubyHash keywordArguments = extractKwargsHash(args, required, keywordArgumentSupplied);
 
         return keywordArguments == null ? RubyHash.newSmallHash(context.getRuntime()) : keywordArguments;
     }
 
     public static IRubyObject setCapturedVar(ThreadContext context, IRubyObject matchRes, String varName) {
         IRubyObject val;
         if (matchRes.isNil()) {
             val = context.nil;
         } else {
             IRubyObject backref = context.getBackRef();
             int n = ((RubyMatchData)backref).getNameToBackrefNumber(varName);
             val = RubyRegexp.nth_match(n, backref);
         }
 
         return val;
     }
 
     @JIT // for JVM6
     public static IRubyObject instanceSuperSplatArgs(ThreadContext context, IRubyObject self, String methodName, RubyModule definingModule, IRubyObject[] args, Block block, boolean[] splatMap) {
         return instanceSuper(context, self, methodName, definingModule, splatArguments(args, splatMap), block);
     }
 
     @Interp
     public static IRubyObject instanceSuper(ThreadContext context, IRubyObject self, String methodName, RubyModule definingModule, IRubyObject[] args, Block block) {
         RubyClass superClass = definingModule.getMethodLocation().getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(methodName) : UndefinedMethod.INSTANCE;
         IRubyObject rVal = method.isUndefined() ? Helpers.callMethodMissing(context, self, method.getVisibility(), methodName, CallType.SUPER, args, block)
                 : method.call(context, self, superClass, methodName, args, block);
         return rVal;
     }
 
     @JIT // for JVM6
     public static IRubyObject classSuperSplatArgs(ThreadContext context, IRubyObject self, String methodName, RubyModule definingModule, IRubyObject[] args, Block block, boolean[] splatMap) {
         return classSuper(context, self, methodName, definingModule, splatArguments(args, splatMap), block);
     }
 
     @Interp
     public static IRubyObject classSuper(ThreadContext context, IRubyObject self, String methodName, RubyModule definingModule, IRubyObject[] args, Block block) {
         RubyClass superClass = definingModule.getMetaClass().getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(methodName) : UndefinedMethod.INSTANCE;
         IRubyObject rVal = method.isUndefined() ? Helpers.callMethodMissing(context, self, method.getVisibility(), methodName, CallType.SUPER, args, block)
                 : method.call(context, self, superClass, methodName, args, block);
         return rVal;
     }
 
     public static IRubyObject unresolvedSuperSplatArgs(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, boolean[] splatMap) {
         return unresolvedSuper(context, self, splatArguments(args, splatMap), block);
     }
 
     public static IRubyObject unresolvedSuper(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
 
         // We have to rely on the frame stack to find the implementation class
         RubyModule klazz = context.getFrameKlazz();
         String methodName = context.getCurrentFrame().getName();
 
         checkSuperDisabledOrOutOfMethod(context, klazz, methodName);
         RubyModule implMod = Helpers.findImplementerIfNecessary(self.getMetaClass(), klazz);
         RubyClass superClass = implMod.getSuperClass();
         DynamicMethod method = superClass != null ? superClass.searchMethod(methodName) : UndefinedMethod.INSTANCE;
 
         IRubyObject rVal = null;
         if (method.isUndefined()|| (superClass.isPrepended() && (method.isImplementedBy(self.getType())))) {
             rVal = Helpers.callMethodMissing(context, self, method.getVisibility(), methodName, CallType.SUPER, args, block);
         } else {
             rVal = method.call(context, self, superClass, methodName, args, block);
         }
 
         return rVal;
     }
 
     public static IRubyObject zSuperSplatArgs(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block, boolean[] splatMap) {
         if (block == null || !block.isGiven()) block = context.getFrameBlock();
         return unresolvedSuperSplatArgs(context, self, args, block, splatMap);
     }
 
     public static IRubyObject[] splatArguments(IRubyObject[] args, boolean[] splatMap) {
         if (splatMap != null && splatMap.length > 0) {
             int count = 0;
             for (int i = 0; i < splatMap.length; i++) {
                 count += splatMap[i] ? ((RubyArray)args[i]).size() : 1;
             }
 
             IRubyObject[] newArgs = new IRubyObject[count];
             int actualOffset = 0;
             for (int i = 0; i < splatMap.length; i++) {
                 if (splatMap[i]) {
                     RubyArray ary = (RubyArray) args[i];
                     for (int j = 0; j < ary.size(); j++) {
                         newArgs[actualOffset++] = ary.eltOk(j);
                     }
                 } else {
                     newArgs[actualOffset++] = args[i];
                 }
             }
 
             args = newArgs;
         }
         return args;
     }
 
     public static String encodeSplatmap(boolean[] splatmap) {
         if (splatmap == null) return "";
         StringBuilder builder = new StringBuilder();
         for (boolean b : splatmap) {
             builder.append(b ? '1' : '0');
         }
         return builder.toString();
     }
 
     public static boolean[] decodeSplatmap(String splatmapString) {
         boolean[] splatMap;
         if (splatmapString.length() > 0) {
             splatMap = new boolean[splatmapString.length()];
 
             for (int i = 0; i < splatmapString.length(); i++) {
                 if (splatmapString.charAt(i) == '1') {
                     splatMap[i] = true;
                 }
             }
         } else {
             splatMap = new boolean[0];
         }
         return splatMap;
     }
 
     public static boolean[] buildSplatMap(Operand[] args, boolean containsArgSplat) {
         boolean[] splatMap = new boolean[args.length];
 
         if (containsArgSplat) {
             for (int i = 0; i < args.length; i++) {
                 Operand operand = args[i];
                 if (operand instanceof Splat) {
                     splatMap[i] = true;
                 }
             }
         }
 
         return splatMap;
     }
 
     public static final Type[] typesFromSignature(Signature signature) {
         Type[] types = new Type[signature.argCount()];
         for (int i = 0; i < signature.argCount(); i++) {
             types[i] = Type.getType(signature.argType(i));
         }
         return types;
     }
 
     // Used by JIT
     public static RubyString newFrozenStringFromRaw(Ruby runtime, String str, String encoding) {
         return runtime.freezeAndDedupString(new RubyString(runtime, runtime.getString(), newByteListFromRaw(runtime, str, encoding)));
     }
 
     // Used by JIT
     public static final ByteList newByteListFromRaw(Ruby runtime, String str, String encoding) {
         return new ByteList(str.getBytes(RubyEncoding.ISO), runtime.getEncodingService().getEncodingFromString(encoding), false);
     }
 
     public static RubyRegexp constructRubyRegexp(ThreadContext context, RubyString pattern, RegexpOptions options) {
         return RubyRegexp.newRegexp(context.runtime, pattern.getByteList(), options);
     }
 
     // Used by JIT
     public static RubyRegexp constructRubyRegexp(ThreadContext context, RubyString pattern, int options) {
         return RubyRegexp.newRegexp(context.runtime, pattern.getByteList(), RegexpOptions.fromEmbeddedOptions(options));
     }
 
     // Used by JIT
     public static RubyEncoding retrieveEncoding(ThreadContext context, String name) {
-        Encoding encoding = context.runtime.getEncodingService().findEncodingOrAliasEntry(name.getBytes()).getEncoding();
-        return context.runtime.getEncodingService().getEncoding(encoding);
+        return context.runtime.getEncodingService().getEncoding(retrieveJCodingsEncoding(context, name));
+    }
+
+    // Used by JIT
+    public static Encoding retrieveJCodingsEncoding(ThreadContext context, String name) {
+        return context.runtime.getEncodingService().findEncodingOrAliasEntry(name.getBytes()).getEncoding();
     }
 
     // Used by JIT
     public static RubyHash constructHashFromArray(Ruby runtime, IRubyObject[] pairs) {
         RubyHash hash = RubyHash.newHash(runtime);
         for (int i = 0; i < pairs.length;) {
             hash.fastASet(runtime, pairs[i++], pairs[i++], true);
         }
         return hash;
     }
 
     // Used by JIT
     public static RubyHash dupKwargsHashAndPopulateFromArray(ThreadContext context, RubyHash dupHash, IRubyObject[] pairs) {
         Ruby runtime = context.runtime;
         RubyHash hash = dupHash.dupFast(context);
         for (int i = 0; i < pairs.length;) {
             hash.fastASet(runtime, pairs[i++], pairs[i++], true);
         }
         return hash;
     }
 
     // Used by JIT
     public static IRubyObject searchConst(ThreadContext context, StaticScope staticScope, String constName, boolean noPrivateConsts) {
         Ruby runtime = context.getRuntime();
         RubyModule object = runtime.getObject();
         IRubyObject constant = (staticScope == null) ? object.getConstant(constName) : staticScope.getConstantInner(constName);
 
         // Inheritance lookup
         RubyModule module = null;
         if (constant == null) {
             // SSS FIXME: Is this null check case correct?
             module = staticScope == null ? object : staticScope.getModule();
             constant = noPrivateConsts ? module.getConstantFromNoConstMissing(constName, false) : module.getConstantNoConstMissing(constName);
         }
 
         // Call const_missing or cache
         if (constant == null) {
             return module.callMethod(context, "const_missing", context.runtime.fastNewSymbol(constName));
         }
 
         return constant;
     }
 
     // Used by JIT
     public static IRubyObject inheritedSearchConst(ThreadContext context, IRubyObject cmVal, String constName, boolean noPrivateConsts) {
         Ruby runtime = context.runtime;
         RubyModule module;
         if (cmVal instanceof RubyModule) {
             module = (RubyModule) cmVal;
         } else {
             throw runtime.newTypeError(cmVal + " is not a type/class");
         }
 
         IRubyObject constant = noPrivateConsts ? module.getConstantFromNoConstMissing(constName, false) : module.getConstantNoConstMissing(constName);
 
         if (constant == null) {
             constant = UndefinedValue.UNDEFINED;
         }
 
         return constant;
     }
 
     // Used by JIT
     public static IRubyObject lexicalSearchConst(ThreadContext context, StaticScope staticScope, String constName) {
         IRubyObject constant = staticScope.getConstantInner(constName);
 
         if (constant == null) {
             constant = UndefinedValue.UNDEFINED;
         }
 
         return constant;
     }
 
     public static IRubyObject setInstanceVariable(IRubyObject self, IRubyObject value, String name) {
         return self.getInstanceVariables().setInstanceVariable(name, value);
     }
 
     /**
      * Construct a new DynamicMethod to wrap the given IRModuleBody and singletonizable object. Used by interpreter.
      */
     @Interp
     public static DynamicMethod newInterpretedMetaClass(Ruby runtime, IRScope metaClassBody, IRubyObject obj) {
         RubyClass singletonClass = newMetaClassFromIR(runtime, metaClassBody, obj);
 
         return new InterpretedIRMetaClassBody(metaClassBody, singletonClass);
     }
 
     /**
      * Construct a new DynamicMethod to wrap the given IRModuleBody and singletonizable object. Used by JIT.
      */
     @JIT
     public static DynamicMethod newCompiledMetaClass(ThreadContext context, MethodHandle handle, IRScope metaClassBody, IRubyObject obj) {
         RubyClass singletonClass = newMetaClassFromIR(context.runtime, metaClassBody, obj);
 
         return new CompiledIRMetaClassBody(handle, metaClassBody, singletonClass);
     }
 
     private static RubyClass newMetaClassFromIR(Ruby runtime, IRScope metaClassBody, IRubyObject obj) {
         RubyClass singletonClass = Helpers.getSingletonClass(runtime, obj);
 
         StaticScope metaClassScope = metaClassBody.getStaticScope();
 
         metaClassScope.setModule(singletonClass);
         return singletonClass;
     }
 
     /**
      * Construct a new DynamicMethod to wrap the given IRModuleBody and singletonizable object. Used by interpreter.
      */
     @Interp
     public static DynamicMethod newInterpretedModuleBody(ThreadContext context, IRScope irModule, Object rubyContainer) {
         RubyModule newRubyModule = newRubyModuleFromIR(context, irModule, rubyContainer);
         return new InterpretedIRMethod(irModule, Visibility.PUBLIC, newRubyModule);
     }
 
     @JIT
     public static DynamicMethod newCompiledModuleBody(ThreadContext context, MethodHandle handle, IRScope irModule, Object rubyContainer) {
         RubyModule newRubyModule = newRubyModuleFromIR(context, irModule, rubyContainer);
         return new CompiledIRMethod(handle, irModule, Visibility.PUBLIC, newRubyModule);
     }
 
     private static RubyModule newRubyModuleFromIR(ThreadContext context, IRScope irModule, Object rubyContainer) {
         if (!(rubyContainer instanceof RubyModule)) {
             throw context.runtime.newTypeError("no outer class/module");
         }
 
         RubyModule newRubyModule = ((RubyModule) rubyContainer).defineOrGetModuleUnder(irModule.getName());
         irModule.getStaticScope().setModule(newRubyModule);
         return newRubyModule;
     }
 
     @Interp
     public static DynamicMethod newInterpretedClassBody(ThreadContext context, IRScope irClassBody, Object container, Object superClass) {
         RubyModule newRubyClass = newRubyClassFromIR(context.runtime, irClassBody, superClass, container);
 
         return new InterpretedIRMethod(irClassBody, Visibility.PUBLIC, newRubyClass);
     }
 
     @JIT
     public static DynamicMethod newCompiledClassBody(ThreadContext context, MethodHandle handle, IRScope irClassBody, Object container, Object superClass) {
         RubyModule newRubyClass = newRubyClassFromIR(context.runtime, irClassBody, superClass, container);
 
         return new CompiledIRMethod(handle, irClassBody, Visibility.PUBLIC, newRubyClass);
     }
 
     public static RubyModule newRubyClassFromIR(Ruby runtime, IRScope irClassBody, Object superClass, Object container) {
         if (!(container instanceof RubyModule)) {
             throw runtime.newTypeError("no outer class/module");
         }
 
         RubyModule newRubyClass;
 
         if (irClassBody instanceof IRMetaClassBody) {
             newRubyClass = ((RubyModule)container).getMetaClass();
         } else {
             RubyClass sc;
             if (superClass == UndefinedValue.UNDEFINED) {
                 sc = null;
             } else {
                 RubyClass.checkInheritable((IRubyObject) superClass);
 
                 sc = (RubyClass) superClass;
             }
 
             newRubyClass = ((RubyModule)container).defineOrGetClassUnder(irClassBody.getName(), sc);
         }
 
         irClassBody.getStaticScope().setModule(newRubyClass);
         return newRubyClass;
     }
 
     @Interp
     public static void defInterpretedClassMethod(ThreadContext context, IRScope method, IRubyObject obj) {
         RubyClass rubyClass = checkClassForDef(context, method, obj);
 
         rubyClass.addMethod(method.getName(), new InterpretedIRMethod(method, Visibility.PUBLIC, rubyClass));
         obj.callMethod(context, "singleton_method_added", context.runtime.fastNewSymbol(method.getName()));
     }
 
     @JIT
     public static void defCompiledClassMethod(ThreadContext context, MethodHandle handle, IRScope method, IRubyObject obj) {
         RubyClass rubyClass = checkClassForDef(context, method, obj);
 
         rubyClass.addMethod(method.getName(), new CompiledIRMethod(handle, method, Visibility.PUBLIC, rubyClass));
         obj.callMethod(context, "singleton_method_added", context.runtime.fastNewSymbol(method.getName()));
     }
 
     @JIT
     public static void defCompiledClassMethod(ThreadContext context, MethodHandle variable, MethodHandle specific, int specificArity, IRScope method, IRubyObject obj) {
         RubyClass rubyClass = checkClassForDef(context, method, obj);
 
         rubyClass.addMethod(method.getName(), new CompiledIRMethod(variable, specific, specificArity, method, Visibility.PUBLIC, rubyClass));
         obj.callMethod(context, "singleton_method_added", context.runtime.fastNewSymbol(method.getName()));
     }
 
     private static RubyClass checkClassForDef(ThreadContext context, IRScope method, IRubyObject obj) {
         if (obj instanceof RubyFixnum || obj instanceof RubySymbol || obj instanceof RubyFloat) {
             throw context.runtime.newTypeError("can't define singleton method \"" + method.getName() + "\" for " + obj.getMetaClass().getBaseName());
         }
 
         // if (obj.isFrozen()) throw context.runtime.newFrozenError("object");
 
         return obj.getSingletonClass();
     }
 
     @Interp
     public static void defInterpretedInstanceMethod(ThreadContext context, IRScope method, DynamicScope currDynScope, IRubyObject self) {
         Ruby runtime = context.runtime;
         RubyModule clazz = findInstanceMethodContainer(context, currDynScope, self);
 
         Visibility currVisibility = context.getCurrentVisibility();
         Visibility newVisibility = Helpers.performNormalMethodChecksAndDetermineVisibility(runtime, clazz, method.getName(), currVisibility);
 
         DynamicMethod newMethod = new InterpretedIRMethod(method, newVisibility, clazz);
 
         Helpers.addInstanceMethod(clazz, method.getName(), newMethod, currVisibility, context, runtime);
     }
 
     @JIT
     public static void defCompiledInstanceMethod(ThreadContext context, MethodHandle handle, IRScope method, DynamicScope currDynScope, IRubyObject self) {
         Ruby runtime = context.runtime;
         RubyModule clazz = findInstanceMethodContainer(context, currDynScope, self);
 
         Visibility currVisibility = context.getCurrentVisibility();
         Visibility newVisibility = Helpers.performNormalMethodChecksAndDetermineVisibility(runtime, clazz, method.getName(), currVisibility);
 
         DynamicMethod newMethod = new CompiledIRMethod(handle, method, newVisibility, clazz);
 
         Helpers.addInstanceMethod(clazz, method.getName(), newMethod, currVisibility, context, runtime);
     }
 
     @JIT
     public static void defCompiledInstanceMethod(ThreadContext context, MethodHandle variable, MethodHandle specific, int specificArity, IRScope method, DynamicScope currDynScope, IRubyObject self) {
         Ruby runtime = context.runtime;
         RubyModule clazz = findInstanceMethodContainer(context, currDynScope, self);
 
         Visibility currVisibility = context.getCurrentVisibility();
         Visibility newVisibility = Helpers.performNormalMethodChecksAndDetermineVisibility(runtime, clazz, method.getName(), currVisibility);
 
         DynamicMethod newMethod = new CompiledIRMethod(variable, specific, specificArity, method, newVisibility, clazz);
 
         Helpers.addInstanceMethod(clazz, method.getName(), newMethod, currVisibility, context, runtime);
     }
 
     @JIT
     public static IRubyObject invokeModuleBody(ThreadContext context, DynamicMethod method) {
         RubyModule implClass = method.getImplementationClass();
 
         return method.call(context, implClass, implClass, "");
     }
 
     @JIT
     public static RubyRegexp newDynamicRegexp(ThreadContext context, IRubyObject[] pieces, int embeddedOptions) {
         RegexpOptions options = RegexpOptions.fromEmbeddedOptions(embeddedOptions);
         RubyString pattern = RubyRegexp.preprocessDRegexp(context.runtime, pieces, options);
         RubyRegexp re = RubyRegexp.newDRegexp(context.runtime, pattern, options);
         re.setLiteral();
 
         return re;
     }
 
     @JIT
     public static RubyArray irSplat(ThreadContext context, IRubyObject maybeAry) {
         return Helpers.splatValue19(maybeAry);
     }
 
     public static IRubyObject irToAry(ThreadContext context, IRubyObject value) {
         if (value instanceof RubyArray) {
             return value;
         } else {
             IRubyObject newValue = TypeConverter.convertToType19(value, context.runtime.getArray(), "to_ary", false);
             if (newValue.isNil()) {
                 return RubyArray.newArrayLight(context.runtime, value);
             }
 
             // must be array by now, or error
             if (!(newValue instanceof RubyArray)) {
                 throw context.runtime.newTypeError(newValue.getMetaClass() + "#" + "to_ary" + " should return Array");
             }
 
             return newValue;
         }
     }
 
     public static int irReqdArgMultipleAsgnIndex(int n,  int preArgsCount, int index, int postArgsCount) {
         if (preArgsCount == -1) {
             return index < n ? index : -1;
         } else {
             int remaining = n - preArgsCount;
             if (remaining <= index) {
                 return -1;
             } else {
                 return (remaining > postArgsCount) ? n - postArgsCount + index : preArgsCount + index;
             }
         }
     }
 
     public static IRubyObject irReqdArgMultipleAsgn(ThreadContext context, RubyArray rubyArray, int preArgsCount, int index, int postArgsCount) {
         int i = irReqdArgMultipleAsgnIndex(rubyArray.getLength(), preArgsCount, index, postArgsCount);
         return i == -1 ? context.nil : rubyArray.entry(i);
     }
 
     public static IRubyObject irNot(ThreadContext context, IRubyObject obj) {
         return context.runtime.newBoolean(!(obj.isTrue()));
     }
 
     @JIT
     public static RaiseException newRequiredKeywordArgumentError(ThreadContext context, String name) {
         return context.runtime.newArgumentError("missing keyword: " + name);
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter.java b/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter.java
index e2ead0628e..59107c2235 100644
--- a/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter.java
+++ b/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter.java
@@ -1,455 +1,452 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.ir.targets;
 
 import com.headius.invokebinder.Signature;
 import org.jcodings.Encoding;
-import org.jruby.*;
+import org.jruby.Ruby;
+import org.jruby.RubyClass;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.ir.operands.UndefinedValue;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
-import org.jruby.parser.StaticScope;
-import org.jruby.runtime.Block;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
-import org.jruby.util.JavaNameMangler;
 import org.jruby.util.RegexpOptions;
 import org.objectweb.asm.Handle;
-import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.commons.Method;
 
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.Map;
 
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  *
  * @author headius
  */
 public abstract class IRBytecodeAdapter {
     public static final int MAX_ARGUMENTS = 250;
 
     public IRBytecodeAdapter(SkinnyMethodAdapter adapter, Signature signature, ClassData classData) {
         this.adapter = adapter;
         this.signature = signature;
         this.classData = classData;
     }
 
     public ClassData getClassData() {
         return classData;
     }
 
     public void startMethod() {
         adapter.start();
     }
 
     public void endMethod() {
         adapter.end(new Runnable() {
             public void run() {
                 for (Map.Entry<Integer, Type> entry : variableTypes.entrySet()) {
                     int i = entry.getKey();
                     String name = variableNames.get(i);
                     adapter.local(i, name, entry.getValue());
                 }
             }
         });
     }
 
     public void loadLocal(int i) {
         adapter.aload(i);
     }
 
     public void loadContext() {
         adapter.aload(signature.argOffset("context"));
     }
 
     public void loadStaticScope() {
         adapter.aload(signature.argOffset("scope"));
     }
 
     public void loadSelf() {
         adapter.aload(signature.argOffset("self"));
     }
 
     public void loadArgs() {
         adapter.aload(signature.argOffset("args"));
     }
 
     public void loadBlock() {
         adapter.aload(signature.argOffset("block"));
     }
 
     public void loadFrameClass() {
         // when present, should be last element in signature
         adapter.aload(signature.argCount() - 1);
     }
 
     public void loadSuperName() {
         adapter.aload(5);
     }
 
     public void loadBlockType() {
         if (signature.argOffset("type") == -1) {
             adapter.aconst_null();
         } else {
             adapter.aload(signature.argOffset("type"));
         }
     }
 
     public void storeLocal(int i) {
         adapter.astore(i);
     }
 
     public void invokeVirtual(Type type, Method method) {
         adapter.invokevirtual(type.getInternalName(), method.getName(), method.getDescriptor());
     }
 
     public void invokeStatic(Type type, Method method) {
         adapter.invokestatic(type.getInternalName(), method.getName(), method.getDescriptor());
     }
 
     public void invokeHelper(String name, String sig) {
         adapter.invokestatic(p(Helpers.class), name, sig);
     }
 
     public void invokeHelper(String name, Class... x) {
         adapter.invokestatic(p(Helpers.class), name, sig(x));
     }
 
     public void invokeIRHelper(String name, String sig) {
         adapter.invokestatic(p(IRRuntimeHelpers.class), name, sig);
     }
 
     public void goTo(org.objectweb.asm.Label label) {
         adapter.go_to(label);
     }
 
     public void isTrue() {
         adapter.invokeinterface(p(IRubyObject.class), "isTrue", sig(boolean.class));
     }
 
     public void isNil() {
         adapter.invokeinterface(p(IRubyObject.class), "isNil", sig(boolean.class));
     }
 
     public void bfalse(org.objectweb.asm.Label label) {
         adapter.iffalse(label);
     }
 
     public void btrue(org.objectweb.asm.Label label) {
         adapter.iftrue(label);
     }
 
     public void poll() {
         loadContext();
         adapter.invokevirtual(p(ThreadContext.class), "pollThreadEvents", sig(void.class));
     }
 
     public void pushObjectClass() {
         loadRuntime();
         adapter.invokevirtual(p(Ruby.class), "getObject", sig(RubyClass.class));
     }
 
     public void pushUndefined() {
         adapter.getstatic(p(UndefinedValue.class), "UNDEFINED", ci(UndefinedValue.class));
     }
 
     public void pushHandle(Handle handle) {
         adapter.getMethodVisitor().visitLdcInsn(handle);
     }
 
     public void mark(org.objectweb.asm.Label label) {
         adapter.label(label);
     }
 
     public void returnValue() {
         adapter.areturn();
     }
 
     public int newLocal(String name, Type type) {
         int index = variableCount++;
         if (type == Type.DOUBLE_TYPE || type == Type.LONG_TYPE) {
             variableCount++;
         }
         variableTypes.put(index, type);
         variableNames.put(index, name);
         return index;
     }
 
     public org.objectweb.asm.Label newLabel() {
         return new org.objectweb.asm.Label();
     }
 
     /**
      * Stack required: none
      *
      * @param l long value to push as a Fixnum
      */
     public abstract void pushFixnum(long l);
 
     /**
      * Stack required: none
      *
      * @param d double value to push as a Float
      */
     public abstract void pushFloat(double d);
 
     /**
      * Stack required: none
      *
      * @param bl ByteList for the String to push
      */
     public abstract void pushString(ByteList bl);
 
     /**
      * Stack required: none
      *
      * @param bl ByteList for the String to push
      */
     public abstract void pushFrozenString(ByteList bl);
 
     /**
      * Stack required: none
      *
      * @param bl ByteList to push
      */
     public abstract void pushByteList(ByteList bl);
 
     /**
      * Build and save a literal regular expression.
      *
      * Stack required: ThreadContext, RubyString.
      *
      * @param options options for the regexp
      */
     public abstract void pushRegexp(int options);
 
     /**
      * Build a dynamic regexp.
      *
      * No stack requirement. The callback must push onto this method's stack the ThreadContext and all arguments for
      * building the dregexp, matching the given arity.
      *
      * @param options options for the regexp
      * @param arity number of Strings passed in
      */
     public abstract void pushDRegexp(Runnable callback, RegexpOptions options, int arity);
 
     /**
      * Push a symbol on the stack.
      *
      * Stack required: none
      *
      * @param sym the symbol's string identifier
      */
-    public abstract void pushSymbol(String sym);
+    public abstract void pushSymbol(String sym, Encoding encoding);
 
     /**
      * Push the JRuby runtime on the stack.
      *
      * Stack required: none
      */
     public abstract void loadRuntime();
 
     /**
      * Push an encoding on the stack.
      *
      * Stack required: none
      *
      * @param encoding the encoding to push
      */
     public abstract void pushEncoding(Encoding encoding);
 
     /**
      * Invoke a method on an object other than self.
      *
      * Stack required: context, self, all arguments, optional block
      *
      * @param name name of the method to invoke
      * @param arity arity of the call
      * @param hasClosure whether a closure will be on the stack for passing
      */
     public abstract void invokeOther(String name, int arity, boolean hasClosure);
 
 
     /**
      * Invoke a method on self.
      *
      * Stack required: context, caller, self, all arguments, optional block
      *
      * @param name name of the method to invoke
      * @param arity arity of the call
      * @param hasClosure whether a closure will be on the stack for passing
      */
     public abstract void invokeSelf(String name, int arity, boolean hasClosure);
 
     /**
      * Invoke a superclass method from an instance context.
      *
      * Stack required: context, caller, self, start class, arguments[, block]
      *
      * @param name name of the method to invoke
      * @param arity arity of the arguments on the stack
      * @param hasClosure whether a block is passed
      * @param splatmap a map of arguments to be splatted back into arg list
      */
     public abstract void invokeInstanceSuper(String name, int arity, boolean hasClosure, boolean[] splatmap);
 
     /**
      * Invoke a superclass method from a class context.
      *
      * Stack required: context, caller, self, start class, arguments[, block]
      *
      * @param name name of the method to invoke
      * @param arity arity of the arguments on the stack
      * @param hasClosure whether a block is passed
      * @param splatmap a map of arguments to be splatted back into arg list
      */
     public abstract void invokeClassSuper(String name, int arity, boolean hasClosure, boolean[] splatmap);
 
     /**
      * Invoke a superclass method from an unresolved context.
      *
      * Stack required: context, caller, self, arguments[, block]
      *
      * @param name name of the method to invoke
      * @param arity arity of the arguments on the stack
      * @param hasClosure whether a block is passed
      * @param splatmap a map of arguments to be splatted back into arg list
      */
     public abstract void invokeUnresolvedSuper(String name, int arity, boolean hasClosure, boolean[] splatmap);
 
     /**
      * Invoke a superclass method from a zsuper in a block.
      *
      * Stack required: context, caller, self, arguments[, block]
      *
      * @param name name of the method to invoke
      * @param arity arity of the arguments on the stack
      * @param hasClosure whether a block is passed
      * @param splatmap a map of arguments to be splatted back into arg list
      */
     public abstract void invokeZSuper(String name, int arity, boolean hasClosure, boolean[] splatmap);
 
     /**
      * Lookup a constant from current context.
      *
      * Stack required: context, static scope
      *
      * @param name name of the constant
      * @param noPrivateConsts whether to ignore private constants
      */
     public abstract void searchConst(String name, boolean noPrivateConsts);
 
     /**
      * Lookup a constant from a given class or module.
      *
      * Stack required: context, module
      *
      * @param name name of the constant
      * @param noPrivateConsts whether to ignore private constants
      */
     public abstract void inheritanceSearchConst(String name, boolean noPrivateConsts);
 
     /**
      * Lookup a constant from a lexical scope.
      *
      * Stack required: context, static scope
      *
      * @param name name of the constant
      */
     public abstract void lexicalSearchConst(String name);
 
     /**
      * Load nil onto the stack.
      *
      * Stack required: none
      */
     public abstract void pushNil();
 
     /**
      * Load a boolean onto the stack.
      *
      * Stack required: none
      *
      * @param b the boolean to push
      */
     public abstract void pushBoolean(boolean b);
 
     /**
      * Load a Bignum onto the stack.
      *
      * Stack required: none
      *
      * @param bigint the value of the Bignum to push
      */
     public abstract void pushBignum(BigInteger bigint);
 
     /**
      * Store instance variable into self.
      *
      * Stack required: self, value
      * Stack result: empty
      *
      * @param name name of variable to store
      */
     public abstract void putField(String name);
 
     /**
      * Load instance variable from self.
      *
      * Stack required: self
      * Stack result: value from self
      *
      * @param name name of variable to load
      */
     public abstract void getField(String name);
 
     /**
      * Construct an Array from elements on stack.
      *
      * Stack required: all elements of array
      *
      * @param length number of elements
      */
     public abstract void array(int length);
 
     /**
      * Construct a Hash from elements on stack.
      *
      * Stack required: context, all elements of hash
      *
      * @param length number of element pairs
      */
     public abstract void hash(int length);
 
     /**
      * Construct a Hash based on keyword arguments pasesd to this method, for use in zsuper
      *
      * Stack required: context, kwargs hash to dup, remaining elements of hash
      *
      * @param length number of element pairs
      */
     public abstract void kwargsHash(int length);
 
     /**
      * Perform a thread event checkpoint.
      *
      * Stack required: none
      */
     public abstract void checkpoint();
 
     public SkinnyMethodAdapter adapter;
     private int variableCount = 0;
     private Map<Integer, Type> variableTypes = new HashMap<Integer, Type>();
     private Map<Integer, String> variableNames = new HashMap<Integer, String>();
     private final Signature signature;
     private final ClassData classData;
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter6.java b/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter6.java
index 68555ed911..99ec476d41 100644
--- a/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter6.java
+++ b/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter6.java
@@ -1,556 +1,559 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.ir.targets;
 
 import com.headius.invokebinder.Signature;
+import java.math.BigInteger;
 import org.jcodings.Encoding;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyEncoding;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyModule;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.Helpers;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CachingCallSite;
 import org.jruby.runtime.callsite.FunctionalCachingCallSite;
-import org.jruby.runtime.invokedynamic.InvokeDynamicSupport;
 import org.jruby.util.ByteList;
-import org.jruby.util.CodegenUtils;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.RegexpOptions;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 
-import java.math.BigInteger;
-
 import static org.jruby.util.CodegenUtils.ci;
 import static org.jruby.util.CodegenUtils.p;
 import static org.jruby.util.CodegenUtils.params;
 import static org.jruby.util.CodegenUtils.sig;
 
 /**
  * Java 6 and lower-compatible version of bytecode adapter for IR JIT.
  *
  * CON FIXME: These are all dirt-stupid impls that will not be as efficient.
  */
 public class IRBytecodeAdapter6 extends IRBytecodeAdapter{
     public IRBytecodeAdapter6(SkinnyMethodAdapter adapter, Signature signature, ClassData classData) {
         super(adapter, signature, classData);
     }
 
     public void pushFixnum(long l) {
         loadRuntime();
         adapter.ldc(l);
         adapter.invokevirtual(p(Ruby.class), "newFixnum", sig(RubyFixnum.class, long.class));
     }
 
     public void pushFloat(double d) {
         loadRuntime();
         adapter.ldc(d);
         adapter.invokevirtual(p(Ruby.class), "newFloat", sig(RubyFloat.class, double.class));
     }
 
     public void pushString(ByteList bl) {
         loadRuntime();
         pushByteList(bl);
         adapter.invokestatic(p(RubyString.class), "newStringShared", sig(RubyString.class, Ruby.class, ByteList.class));
     }
 
     /**
      * Stack required: none
      *
      * @param bl ByteList for the String to push
      */
     public void pushFrozenString(ByteList bl) {
         // FIXME: too much bytecode
         String cacheField = "frozenString" + getClassData().callSiteCount.getAndIncrement();
         Label done = new Label();
         adapter.getClassVisitor().visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, cacheField, ci(RubyString.class), null, null).visitEnd();
         adapter.getstatic(getClassData().clsName, cacheField, ci(RubyString.class));
         adapter.dup();
         adapter.ifnonnull(done);
         adapter.pop();
         loadRuntime();
         adapter.ldc(bl.toString());
         adapter.ldc(bl.getEncoding().toString());
         invokeIRHelper("newFrozenStringFromRaw", sig(RubyString.class, Ruby.class, String.class, String.class));
         adapter.dup();
         adapter.putstatic(getClassData().clsName, cacheField, ci(RubyString.class));
         adapter.label(done);
     }
 
     public void pushByteList(ByteList bl) {
         // FIXME: too much bytecode
         String cacheField = "byteList" + getClassData().callSiteCount.getAndIncrement();
         Label done = new Label();
         adapter.getClassVisitor().visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, cacheField, ci(ByteList.class), null, null).visitEnd();
         adapter.getstatic(getClassData().clsName, cacheField, ci(ByteList.class));
         adapter.dup();
         adapter.ifnonnull(done);
         adapter.pop();
         loadRuntime();
         adapter.ldc(bl.toString());
         adapter.ldc(bl.getEncoding().toString());
         invokeIRHelper("newByteListFromRaw", sig(ByteList.class, Ruby.class, String.class, String.class));
         adapter.dup();
         adapter.putstatic(getClassData().clsName, cacheField, ci(ByteList.class));
         adapter.label(done);
     }
 
     public void pushRegexp(int options) {
         adapter.pushInt(options);
         invokeIRHelper("constructRubyRegexp", sig(RubyRegexp.class, ThreadContext.class, RubyString.class, int.class));
     }
 
     public void pushDRegexp(Runnable callback, RegexpOptions options, int arity) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("dynamic regexp has more than " + MAX_ARGUMENTS + " elements");
 
         SkinnyMethodAdapter adapter2;
         String incomingSig = sig(RubyRegexp.class, params(ThreadContext.class, RubyString.class, arity, int.class));
 
         if (!getClassData().dregexpMethodsDefined.contains(arity)) {
             adapter2 = new SkinnyMethodAdapter(
                     adapter.getClassVisitor(),
                     Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                     "dregexp:" + arity,
                     incomingSig,
                     null,
                     null);
 
             adapter2.aload(0);
             buildArrayFromLocals(adapter2, 1, arity);
             adapter2.iload(1 + arity);
 
             adapter2.invokestatic(p(IRRuntimeHelpers.class), "newDynamicRegexp", sig(RubyRegexp.class, ThreadContext.class, IRubyObject[].class, int.class));
             adapter2.areturn();
             adapter2.end();
 
             getClassData().dregexpMethodsDefined.add(arity);
         }
 
         String cacheField = null;
         Label done = null;
 
         if (options.isOnce()) {
             // need to cache result forever
             cacheField = "dregexp" + getClassData().callSiteCount.getAndIncrement();
             done = new Label();
             adapter.getClassVisitor().visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, cacheField, ci(RubyRegexp.class), null, null).visitEnd();
             adapter.getstatic(getClassData().clsName, cacheField, ci(RubyRegexp.class));
             adapter.dup();
             adapter.ifnonnull(done);
             adapter.pop();
         }
 
         // call synthetic method if we still need to build dregexp
         callback.run();
         adapter.ldc(options.toEmbeddedOptions());
         adapter.invokestatic(getClassData().clsName, "dregexp:" + arity, incomingSig);
 
         if (done != null) {
             adapter.dup();
             adapter.putstatic(getClassData().clsName, cacheField, ci(RubyRegexp.class));
             adapter.label(done);
         }
     }
 
-    public void pushSymbol(String sym) {
+    public void pushSymbol(String sym, Encoding encoding) {
         loadRuntime();
         adapter.ldc(sym);
-        adapter.invokevirtual(p(Ruby.class), "newSymbol", sig(RubySymbol.class, String.class));
+
+        // FIXME: Should be a helper somewhere?  Load Encoding
+        loadContext();
+        adapter.ldc(encoding.toString());
+        invokeIRHelper("retrieveJCodingsEncoding", sig(Encoding.class, ThreadContext.class, String.class));
+
+        adapter.invokestatic(p(RubySymbol.class), "newSymbol", sig(RubySymbol.class, Ruby.class, String.class, Encoding.class));
     }
 
     public void loadRuntime() {
         loadContext();
         adapter.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
     }
 
     public void pushEncoding(Encoding encoding) {
         loadContext();
         adapter.ldc(encoding.toString());
         invokeIRHelper("retrieveEncoding", sig(RubyEncoding.class, ThreadContext.class, String.class));
     }
 
     public void invokeOther(String name, int arity, boolean hasClosure) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to `" + name + "' has more than " + MAX_ARGUMENTS + " arguments");
 
         SkinnyMethodAdapter adapter2;
         String incomingSig;
         String outgoingSig;
 
         if (hasClosure) {
             switch (arity) {
                 case -1:
                     incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY, Block.class));
                     outgoingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY, Block.class));
                     break;
                 case 0:
                 case 1:
                 case 2:
                 case 3:
                     incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT, arity, Block.class));
                     outgoingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT, arity, Block.class));
                     break;
                 default:
                     incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT, arity, Block.class));
                     outgoingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY, Block.class));
                     break;
             }
         } else {
             switch (arity) {
                 case -1:
                     incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY));
                     outgoingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY));
                     break;
                 case 0:
                 case 1:
                 case 2:
                 case 3:
                     incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT, arity));
                     outgoingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT, arity));
                     break;
                 default:
                     incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT, arity));
                     outgoingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY));
                     break;
             }
         }
 
         String methodName = "invokeOther" + getClassData().callSiteCount.getAndIncrement() + ":" + JavaNameMangler.mangleMethodName(name);
 
         adapter2 = new SkinnyMethodAdapter(
                 adapter.getClassVisitor(),
                 Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                 methodName,
                 incomingSig,
                 null,
                 null);
 
         // call site object field
         adapter.getClassVisitor().visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, methodName, ci(CachingCallSite.class), null, null).visitEnd();
 
         // lazily construct it
         adapter2.getstatic(getClassData().clsName, methodName, ci(CachingCallSite.class));
         adapter2.dup();
         Label doCall = new Label();
         adapter2.ifnonnull(doCall);
         adapter2.pop();
         adapter2.newobj(p(FunctionalCachingCallSite.class));
         adapter2.dup();
         adapter2.ldc(name);
         adapter2.invokespecial(p(FunctionalCachingCallSite.class), "<init>", sig(void.class, String.class));
         adapter2.dup();
         adapter2.putstatic(getClassData().clsName, methodName, ci(CachingCallSite.class));
 
         // use call site to invoke
         adapter2.label(doCall);
         adapter2.aload(0); // context
         adapter2.aload(1); // caller
         adapter2.aload(2); // self
 
         switch (arity) {
             case -1:
             case 1:
                 adapter2.aload(3);
                 if (hasClosure) adapter2.aload(4);
                 break;
             case 0:
                 if (hasClosure) adapter2.aload(3);
                 break;
             case 2:
                 adapter2.aload(3);
                 adapter2.aload(4);
                 if (hasClosure) adapter2.aload(5);
                 break;
             case 3:
                 adapter2.aload(3);
                 adapter2.aload(4);
                 adapter2.aload(5);
                 if (hasClosure) adapter2.aload(6);
                 break;
             default:
                 buildArrayFromLocals(adapter2, 3, arity);
                 if (hasClosure) adapter2.aload(3 + arity);
                 break;
         }
 
         adapter2.invokevirtual(p(CachingCallSite.class), "call", outgoingSig);
         adapter2.areturn();
         adapter2.end();
 
         // now call it
         adapter.invokestatic(getClassData().clsName, methodName, incomingSig);
     }
 
     public static void buildArrayFromLocals(SkinnyMethodAdapter adapter2, int base, int arity) {
         if (arity == 0) {
             adapter2.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             return;
         }
 
         adapter2.pushInt(arity);
         adapter2.invokestatic(p(Helpers.class), "anewarrayIRubyObjects", sig(IRubyObject[].class, int.class));
 
         for (int i = 0; i < arity;) {
             int j = 0;
             while (i + j < arity && j < Helpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
                 adapter2.aload(base + i + j);
                 j++;
             }
             adapter2.pushInt(i);
             adapter2.invokestatic(p(Helpers.class), "aastoreIRubyObjects", sig(IRubyObject[].class, params(IRubyObject[].class, IRubyObject.class, j, int.class)));
             i += j;
         }
     }
 
     public void invokeSelf(String name, int arity, boolean hasClosure) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to `" + name + "' has more than " + MAX_ARGUMENTS + " arguments");
 
         invokeOther(name, arity, hasClosure);
     }
 
     public void invokeInstanceSuper(String name, int arity, boolean hasClosure, boolean[] splatmap) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to instance super has more than " + MAX_ARGUMENTS + " arguments");
 
         performSuper(name, arity, hasClosure, splatmap, "instanceSuperSplatArgs", false);
     }
 
     public void invokeClassSuper(String name, int arity, boolean hasClosure, boolean[] splatmap) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to class super has more than " + MAX_ARGUMENTS + " arguments");
 
         performSuper(name, arity, hasClosure, splatmap, "classSuperSplatArgs", false);
     }
 
     public void invokeUnresolvedSuper(String name, int arity, boolean hasClosure, boolean[] splatmap) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to unresolved super has more than " + MAX_ARGUMENTS + " arguments");
 
         performSuper(name, arity, hasClosure, splatmap, "unresolvedSuperSplatArgs", true);
     }
 
     public void invokeZSuper(String name, int arity, boolean hasClosure, boolean[] splatmap) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to zsuper has more than " + MAX_ARGUMENTS + " arguments");
 
         performSuper(name, arity, hasClosure, splatmap, "zSuperSplatArgs", true);
     }
 
     private void performSuper(String name, int arity, boolean hasClosure, boolean[] splatmap, String helperName, boolean unresolved) {
         SkinnyMethodAdapter adapter2;
         String incomingSig;
         String outgoingSig;
 
         if (hasClosure) {
             incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity, Block.class));
             outgoingSig = unresolved ?
                     sig(JVM.OBJECT, params(ThreadContext.class, IRubyObject.class, JVM.OBJECT_ARRAY, Block.class, boolean[].class)) :
                     sig(JVM.OBJECT, params(ThreadContext.class, IRubyObject.class, String.class, RubyModule.class, JVM.OBJECT_ARRAY, Block.class, boolean[].class));
         } else {
             incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity));
             outgoingSig = unresolved ?
                     sig(JVM.OBJECT, params(ThreadContext.class, IRubyObject.class, JVM.OBJECT_ARRAY, Block.class, boolean[].class)) :
                     sig(JVM.OBJECT, params(ThreadContext.class, IRubyObject.class, String.class, RubyModule.class, JVM.OBJECT_ARRAY, Block.class, boolean[].class));
         }
 
         String methodName = "invokeSuper" + getClassData().callSiteCount.getAndIncrement() + ":" + JavaNameMangler.mangleMethodName(name);
         adapter2 = new SkinnyMethodAdapter(
                 adapter.getClassVisitor(),
                 Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                 methodName,
                 incomingSig,
                 null,
                 null);
 
         // CON FIXME: make these offsets programmatically determined
         adapter2.aload(0);
         adapter2.aload(2);
         if (!unresolved) adapter2.ldc(name);
         if (!unresolved) adapter2.aload(3);
 
         buildArrayFromLocals(adapter2, 4, arity);
 
         if (hasClosure) {
             adapter2.aload(4 + arity);
         } else {
             adapter2.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
         }
 
         if (splatmap != null || splatmap.length > 0 || anyTrue(splatmap)) {
             String splatmapString = IRRuntimeHelpers.encodeSplatmap(splatmap);
             adapter2.ldc(splatmapString);
             adapter2.invokestatic(p(IRRuntimeHelpers.class), "decodeSplatmap", sig(boolean[].class, String.class));
         } else {
             adapter2.getstatic(p(IRRuntimeHelpers.class), "EMPTY_BOOLEAN_ARRAY", ci(boolean[].class));
         }
 
         adapter2.invokestatic(p(IRRuntimeHelpers.class), helperName, outgoingSig);
         adapter2.areturn();
         adapter2.end();
 
         // now call it
         adapter.invokestatic(getClassData().clsName, methodName, incomingSig);
     }
 
     private static boolean anyTrue(boolean[] booleans) {
         for (boolean b : booleans) if (b) return true;
         return false;
     }
 
     public void searchConst(String name, boolean noPrivateConsts) {
         adapter.ldc(name);
         adapter.ldc(noPrivateConsts);
         invokeIRHelper("searchConst", sig(IRubyObject.class, ThreadContext.class, StaticScope.class, String.class, boolean.class));
     }
 
     public void inheritanceSearchConst(String name, boolean noPrivateConsts) {
         adapter.ldc(name);
         adapter.ldc(noPrivateConsts);
         invokeIRHelper("inheritedSearchConst", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class, boolean.class));
     }
 
     public void lexicalSearchConst(String name) {
         adapter.ldc(name);
         invokeIRHelper("lexicalSearchConst", sig(IRubyObject.class, ThreadContext.class, StaticScope.class, String.class));}
 
     public void pushNil() {
         loadContext();
         adapter.getfield(p(ThreadContext.class), "nil", ci(IRubyObject.class));
     }
 
     public void pushBoolean(boolean b) {
         loadRuntime();
         adapter.invokevirtual(p(Ruby.class), b ? "getTrue" : "getFalse", sig(RubyBoolean.class));
     }
 
     public void pushBignum(BigInteger bigint) {
         String bigintStr = bigint.toString();
 
         loadRuntime();
         adapter.ldc(bigintStr);
         adapter.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, Ruby.class, String.class));
     }
 
     public void putField(String name) {
         adapter.ldc(name);
         invokeIRHelper("setInstanceVariable", sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class));
         adapter.pop();
     }
 
     public void getField(String name) {
         loadRuntime();
         adapter.ldc(name);
         invokeHelper("getInstanceVariable", sig(IRubyObject.class, IRubyObject.class, Ruby.class, String.class));
     }
 
     public void array(int length) {
         if (length > MAX_ARGUMENTS) throw new NotCompilableException("literal array has more than " + MAX_ARGUMENTS + " elements");
 
         SkinnyMethodAdapter adapter2;
         String incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, length));
 
         if (!getClassData().arrayMethodsDefined.contains(length)) {
             adapter2 = new SkinnyMethodAdapter(
                     adapter.getClassVisitor(),
                     Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                     "array:" + length,
                     incomingSig,
                     null,
                     null);
 
             adapter2.aload(0);
             adapter2.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
             buildArrayFromLocals(adapter2, 1, length);
 
             adapter2.invokevirtual(p(Ruby.class), "newArrayNoCopy", sig(RubyArray.class, IRubyObject[].class));
             adapter2.areturn();
             adapter2.end();
 
             getClassData().arrayMethodsDefined.add(length);
         }
 
         // now call it
         adapter.invokestatic(getClassData().clsName, "array:" + length, incomingSig);
     }
 
     public void hash(int length) {
         if (length > MAX_ARGUMENTS / 2) throw new NotCompilableException("literal hash has more than " + (MAX_ARGUMENTS / 2) + " pairs");
 
         SkinnyMethodAdapter adapter2;
         String incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, length * 2));
 
         if (!getClassData().hashMethodsDefined.contains(length)) {
             adapter2 = new SkinnyMethodAdapter(
                     adapter.getClassVisitor(),
                     Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                     "hash:" + length,
                     incomingSig,
                     null,
                     null);
 
             adapter2.aload(0);
             adapter2.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
             buildArrayFromLocals(adapter2, 1, length * 2);
 
             adapter2.invokestatic(p(IRRuntimeHelpers.class), "constructHashFromArray", sig(RubyHash.class, Ruby.class, IRubyObject[].class));
             adapter2.areturn();
             adapter2.end();
 
             getClassData().hashMethodsDefined.add(length);
         }
 
         // now call it
         adapter.invokestatic(getClassData().clsName, "hash:" + length, incomingSig);
     }
 
     public void kwargsHash(int length) {
         if (length > MAX_ARGUMENTS / 2) throw new NotCompilableException("kwargs hash has more than " + (MAX_ARGUMENTS / 2) + " pairs");
 
         SkinnyMethodAdapter adapter2;
         String incomingSig = sig(JVM.OBJECT, params(ThreadContext.class, RubyHash.class, IRubyObject.class, length * 2));
 
         if (!getClassData().kwargsHashMethodsDefined.contains(length)) {
             adapter2 = new SkinnyMethodAdapter(
                     adapter.getClassVisitor(),
                     Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                     "kwargsHash:" + length,
                     incomingSig,
                     null,
                     null);
 
             adapter2.aload(0);
             adapter2.aload(1);
             buildArrayFromLocals(adapter2, 2, length * 2);
 
             adapter2.invokestatic(p(IRRuntimeHelpers.class), "dupKwargsHashAndPopulateFromArray", sig(RubyHash.class, ThreadContext.class, RubyHash.class, IRubyObject[].class));
             adapter2.areturn();
             adapter2.end();
 
             getClassData().hashMethodsDefined.add(length);
         }
 
         // now call it
         adapter.invokestatic(getClassData().clsName, "kwargsHash:" + length, incomingSig);
     }
 
     public void checkpoint() {
         loadContext();
         adapter.invokevirtual(
                 p(ThreadContext.class),
                 "callThreadPoll",
                 sig(void.class));
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter7.java b/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter7.java
index a77b5ab45e..46d8742bf7 100644
--- a/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter7.java
+++ b/core/src/main/java/org/jruby/ir/targets/IRBytecodeAdapter7.java
@@ -1,264 +1,264 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 package org.jruby.ir.targets;
 
 import com.headius.invokebinder.Signature;
 import org.jcodings.Encoding;
 import org.jruby.Ruby;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyEncoding;
 import org.jruby.RubyHash;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.invokedynamic.InvokeDynamicSupport;
 import org.jruby.util.ByteList;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.RegexpOptions;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 
 import java.math.BigInteger;
 
 import static org.jruby.util.CodegenUtils.ci;
 import static org.jruby.util.CodegenUtils.params;
 import static org.jruby.util.CodegenUtils.sig;
 
 /**
  *
  * @author headius
  */
 public class IRBytecodeAdapter7 extends IRBytecodeAdapter {
 
     public IRBytecodeAdapter7(SkinnyMethodAdapter adapter, Signature signature, ClassData classData) {
         super(adapter, signature, classData);
     }
 
     public void pushFixnum(long l) {
         loadContext();
         adapter.invokedynamic("fixnum", sig(JVM.OBJECT, ThreadContext.class), FixnumObjectSite.BOOTSTRAP, l);
     }
 
     public void pushFloat(double d) {
         loadContext();
         adapter.invokedynamic("flote", sig(JVM.OBJECT, ThreadContext.class), FloatObjectSite.BOOTSTRAP, d);
     }
 
     public void pushString(ByteList bl) {
         loadContext();
         adapter.invokedynamic("string", sig(RubyString.class, ThreadContext.class), Bootstrap.string(), new String(bl.bytes(), RubyEncoding.ISO), bl.getEncoding().toString());
     }
 
     public void pushFrozenString(ByteList bl) {
         loadContext();
         adapter.invokedynamic("frozen", sig(RubyString.class, ThreadContext.class), Bootstrap.string(), new String(bl.bytes(), RubyEncoding.ISO), bl.getEncoding().toString());
     }
 
     public void pushByteList(ByteList bl) {
         adapter.invokedynamic("bytelist", sig(ByteList.class), Bootstrap.bytelist(), new String(bl.bytes(), RubyEncoding.ISO), bl.getEncoding().toString());
     }
 
     public void pushRegexp(int options) {
         adapter.invokedynamic("regexp", sig(RubyRegexp.class, ThreadContext.class, RubyString.class), RegexpObjectSite.BOOTSTRAP, options);
     }
 
     public void pushDRegexp(Runnable callback, RegexpOptions options, int arity) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("dynamic regexp has more than " + MAX_ARGUMENTS + " elements");
 
         String cacheField = null;
         Label done = null;
 
         if (options.isOnce()) {
             // need to cache result forever
             cacheField = "dregexp" + getClassData().callSiteCount.getAndIncrement();
             done = new Label();
             adapter.getClassVisitor().visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, cacheField, ci(RubyRegexp.class), null, null).visitEnd();
             adapter.getstatic(getClassData().clsName, cacheField, ci(RubyRegexp.class));
             adapter.dup();
             adapter.ifnonnull(done);
             adapter.pop();
         }
 
         // call synthetic method if we still need to build dregexp
         callback.run();
         adapter.invokedynamic("dregexp", sig(RubyRegexp.class, params(ThreadContext.class, RubyString.class, arity)), DRegexpObjectSite.BOOTSTRAP, options.toEmbeddedOptions());
 
         if (done != null) {
             adapter.dup();
             adapter.putstatic(getClassData().clsName, cacheField, ci(RubyRegexp.class));
             adapter.label(done);
         }
     }
 
     /**
      * Push a symbol on the stack
      * @param sym the symbol's string identifier
      */
-    public void pushSymbol(String sym) {
+    public void pushSymbol(String sym, Encoding encoding) {
         loadContext();
-        adapter.invokedynamic("symbol", sig(JVM.OBJECT, ThreadContext.class), SymbolObjectSite.BOOTSTRAP, sym);
+        adapter.invokedynamic("symbol", sig(JVM.OBJECT, ThreadContext.class), SymbolObjectSite.BOOTSTRAP, sym, new String(encoding.getName()));
     }
 
     public void loadRuntime() {
         loadContext();
         adapter.invokedynamic("runtime", sig(Ruby.class, ThreadContext.class), Bootstrap.contextValue());
     }
 
     public void pushEncoding(Encoding encoding) {
         loadContext();
         adapter.invokedynamic("encoding", sig(RubyEncoding.class, ThreadContext.class), Bootstrap.contextValueString(), new String(encoding.getName()));
     }
 
     public void invokeOther(String name, int arity, boolean hasClosure) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to `" + name + "' has more than " + MAX_ARGUMENTS + " arguments");
 
         if (hasClosure) {
             if (arity == -1) {
                 adapter.invokedynamic("invoke:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY, Block.class)), NormalInvokeSite.BOOTSTRAP);
             } else {
                 adapter.invokedynamic("invoke:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, arity + 2, Block.class)), NormalInvokeSite.BOOTSTRAP);
             }
         } else {
             if (arity == -1) {
                 adapter.invokedynamic("invoke:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY)), NormalInvokeSite.BOOTSTRAP);
             } else {
                 adapter.invokedynamic("invoke:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT, arity)), NormalInvokeSite.BOOTSTRAP);
             }
         }
     }
 
     public void invokeSelf(String name, int arity, boolean hasClosure) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to `" + name + "' has more than " + MAX_ARGUMENTS + " arguments");
 
         if (hasClosure) {
             if (arity == -1) {
                 adapter.invokedynamic("invokeSelf:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY, Block.class)), SelfInvokeSite.BOOTSTRAP);
             } else {
                 adapter.invokedynamic("invokeSelf:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, arity + 2, Block.class)), SelfInvokeSite.BOOTSTRAP);
             }
         } else {
             if (arity == -1) {
                 adapter.invokedynamic("invokeSelf:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT_ARRAY)), SelfInvokeSite.BOOTSTRAP);
             } else {
                 adapter.invokedynamic("invokeSelf:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, JVM.OBJECT, arity)), SelfInvokeSite.BOOTSTRAP);
             }
         }
     }
 
     public void invokeInstanceSuper(String name, int arity, boolean hasClosure, boolean[] splatmap) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to instance super has more than " + MAX_ARGUMENTS + " arguments");
 
         String splatmapString = IRRuntimeHelpers.encodeSplatmap(splatmap);
         if (hasClosure) {
             adapter.invokedynamic("invokeInstanceSuper:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity, Block.class)), Bootstrap.invokeSuper(), splatmapString);
         } else {
             adapter.invokedynamic("invokeInstanceSuper:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity)), Bootstrap.invokeSuper(), splatmapString);
         }
     }
 
     public void invokeClassSuper(String name, int arity, boolean hasClosure, boolean[] splatmap) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to class super has more than " + MAX_ARGUMENTS + " arguments");
 
         String splatmapString = IRRuntimeHelpers.encodeSplatmap(splatmap);
         if (hasClosure) {
             adapter.invokedynamic("invokeClassSuper:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity, Block.class)), Bootstrap.invokeSuper(), splatmapString);
         } else {
             adapter.invokedynamic("invokeClassSuper:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity)), Bootstrap.invokeSuper(), splatmapString);
         }
     }
 
     public void invokeUnresolvedSuper(String name, int arity, boolean hasClosure, boolean[] splatmap) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to unresolved super has more than " + MAX_ARGUMENTS + " arguments");
 
         String splatmapString = IRRuntimeHelpers.encodeSplatmap(splatmap);
         if (hasClosure) {
             adapter.invokedynamic("invokeUnresolvedSuper:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity, Block.class)), Bootstrap.invokeSuper(), splatmapString);
         } else {
             adapter.invokedynamic("invokeUnresolvedSuper:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity)), Bootstrap.invokeSuper(), splatmapString);
         }
     }
 
     public void invokeZSuper(String name, int arity, boolean hasClosure, boolean[] splatmap) {
         if (arity > MAX_ARGUMENTS) throw new NotCompilableException("call to zsuper has more than " + MAX_ARGUMENTS + " arguments");
 
         String splatmapString = IRRuntimeHelpers.encodeSplatmap(splatmap);
         if (hasClosure) {
             adapter.invokedynamic("invokeUnresolvedSuper:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity, Block.class)), Bootstrap.invokeSuper(), splatmapString);
         } else {
             adapter.invokedynamic("invokeUnresolvedSuper:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, JVM.OBJECT, RubyClass.class, JVM.OBJECT, arity)), Bootstrap.invokeSuper(), splatmapString);
         }
     }
 
     public void searchConst(String name, boolean noPrivateConsts) {
         adapter.invokedynamic("searchConst:" + name, sig(JVM.OBJECT, params(ThreadContext.class, StaticScope.class)), Bootstrap.searchConst(), noPrivateConsts?1:0);
     }
 
     public void inheritanceSearchConst(String name, boolean noPrivateConsts) {
         adapter.invokedynamic("inheritanceSearchConst:" + name, sig(JVM.OBJECT, params(ThreadContext.class, IRubyObject.class)), Bootstrap.searchConst(), noPrivateConsts?1:0);
     }
 
     public void lexicalSearchConst(String name) {
         adapter.invokedynamic("lexicalSearchConst:" + name, sig(JVM.OBJECT, params(ThreadContext.class, StaticScope.class)), Bootstrap.searchConst(), 0);
     }
 
     public void pushNil() {
         loadContext();
         adapter.invokedynamic("nil", sig(IRubyObject.class, ThreadContext.class), Bootstrap.contextValue());
     }
 
     public void pushBoolean(boolean b) {
         loadContext();
         adapter.invokedynamic(b ? "True" : "False", sig(IRubyObject.class, ThreadContext.class), Bootstrap.contextValue());
     }
 
     public void pushBignum(BigInteger bigint) {
         String bigintStr = bigint.toString();
 
         loadContext();
 
         adapter.invokedynamic("bignum", sig(RubyBignum.class, ThreadContext.class), BignumObjectSite.BOOTSTRAP, bigintStr);
     }
 
     public void putField(String name) {
         adapter.invokedynamic("ivarSet:" + JavaNameMangler.mangleMethodName(name), sig(void.class, IRubyObject.class, IRubyObject.class), Bootstrap.ivar());
     }
 
     public void getField(String name) {
         adapter.invokedynamic("ivarGet:" + JavaNameMangler.mangleMethodName(name), sig(JVM.OBJECT, IRubyObject.class), Bootstrap.ivar());
     }
 
     public void array(int length) {
         if (length > MAX_ARGUMENTS) throw new NotCompilableException("literal array has more than " + MAX_ARGUMENTS + " elements");
 
         adapter.invokedynamic("array", sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, length)), Bootstrap.array());
     }
 
     public void hash(int length) {
         if (length > MAX_ARGUMENTS / 2) throw new NotCompilableException("literal hash has more than " + (MAX_ARGUMENTS / 2) + " pairs");
 
         adapter.invokedynamic("hash", sig(JVM.OBJECT, params(ThreadContext.class, JVM.OBJECT, length * 2)), Bootstrap.hash());
     }
 
     public void kwargsHash(int length) {
         if (length > MAX_ARGUMENTS / 2) throw new NotCompilableException("kwargs hash has more than " + (MAX_ARGUMENTS / 2) + " pairs");
 
         adapter.invokedynamic("kwargsHash", sig(JVM.OBJECT, params(ThreadContext.class, RubyHash.class, JVM.OBJECT, length * 2)), Bootstrap.kwargsHash());
     }
 
     public void checkpoint() {
         loadContext();
         adapter.invokedynamic(
                 "checkpoint",
                 sig(void.class, ThreadContext.class),
                 InvokeDynamicSupport.checkpointHandle());
     }
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
index 8d0d2e06df..2f12e7cf49 100644
--- a/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+++ b/core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
@@ -1,2155 +1,2157 @@
 package org.jruby.ir.targets;
 
 import com.headius.invokebinder.Signature;
+import org.jcodings.specific.USASCIIEncoding;
 import org.jruby.*;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.ir.*;
 import org.jruby.ir.instructions.*;
 import org.jruby.ir.instructions.boxing.*;
 import org.jruby.ir.instructions.defined.GetErrorInfoInstr;
 import org.jruby.ir.instructions.defined.RestoreErrorInfoInstr;
 import org.jruby.ir.operands.*;
 import org.jruby.ir.operands.Boolean;
 import org.jruby.ir.operands.Float;
 import org.jruby.ir.operands.GlobalVariable;
 import org.jruby.ir.operands.Label;
 import org.jruby.ir.operands.MethodHandle;
 import org.jruby.ir.representations.BasicBlock;
 import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassDefiningClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.KeyValuePair;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.cli.Options;
 import org.jruby.util.log.Logger;
 import org.jruby.util.log.LoggerFactory;
 import org.objectweb.asm.Handle;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.Type;
 import org.objectweb.asm.commons.Method;
 
 import java.lang.invoke.MethodType;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import static org.jruby.util.CodegenUtils.*;
 
 /**
  * Implementation of IRCompiler for the JVM.
  */
 public class JVMVisitor extends IRVisitor {
 
     private static final Logger LOG = LoggerFactory.getLogger("JVMVisitor");
     public static final String DYNAMIC_SCOPE = "$dynamicScope";
     private static final boolean DEBUG = false;
 
     public JVMVisitor() {
         this.jvm = Options.COMPILE_INVOKEDYNAMIC.load() ? new JVM7() : new JVM6();
         this.methodIndex = 0;
         this.scopeMap = new HashMap();
     }
 
     public Class compile(IRScope scope, ClassDefiningClassLoader jrubyClassLoader) {
         return defineFromBytecode(scope, compileToBytecode(scope), jrubyClassLoader);
     }
 
     public byte[] compileToBytecode(IRScope scope) {
         codegenScope(scope);
 
 //        try {
 //            FileOutputStream fos = new FileOutputStream("tmp.class");
 //            fos.write(target.code());
 //            fos.close();
 //        } catch (Exception e) {
 //            e.printStackTrace();
 //        }
 
         return code();
     }
 
     public Class defineFromBytecode(IRScope scope, byte[] code, ClassDefiningClassLoader jrubyClassLoader) {
         Class result = jrubyClassLoader.defineClass(c(JVM.scriptToClass(scope.getFileName())), code);
 
         for (Map.Entry<String, IRScope> entry : scopeMap.entrySet()) {
             try {
                 result.getField(entry.getKey()).set(null, entry.getValue());
             } catch (Exception e) {
                 throw new NotCompilableException(e);
             }
         }
 
         return result;
     }
 
     public byte[] code() {
         return jvm.code();
     }
 
     public void codegenScope(IRScope scope) {
         if (scope instanceof IRScriptBody) {
             codegenScriptBody((IRScriptBody)scope);
         } else if (scope instanceof IRMethod) {
             emitMethodJIT((IRMethod)scope);
         } else if (scope instanceof IRModuleBody) {
             emitModuleBodyJIT((IRModuleBody)scope);
         } else {
             throw new NotCompilableException("don't know how to JIT: " + scope);
         }
     }
 
     public void codegenScriptBody(IRScriptBody script) {
         emitScriptBody(script);
     }
 
     private void logScope(IRScope scope) {
         StringBuilder b = new StringBuilder();
 
         b.append("\n\nLinearized instructions for JIT:\n");
 
         int i = 0;
         for (BasicBlock bb : scope.buildLinearization()) {
             for (Instr instr : bb.getInstrsArray()) {
                 if (i > 0) b.append("\n");
 
                 b.append("  ").append(i).append('\t').append(instr);
 
                 i++;
             }
         }
 
         LOG.info("Starting JVM compilation on scope " + scope);
         LOG.info(b.toString());
     }
 
     public void emitScope(IRScope scope, String name, Signature signature, boolean specificArity) {
         List <BasicBlock> bbs = scope.prepareForCompilation();
 
         Map <BasicBlock, Label> exceptionTable = scope.buildJVMExceptionTable();
 
         if (Options.IR_COMPILER_DEBUG.load()) logScope(scope);
 
         emitClosures(scope);
 
         jvm.pushmethod(name, scope, signature, specificArity);
 
         // store IRScope in map for insertion into class later
         String scopeField = name + "_IRScope";
         if (scopeMap.get(scopeField) == null) {
             scopeMap.put(scopeField, scope);
             jvm.cls().visitField(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC | Opcodes.ACC_VOLATILE, scopeField, ci(IRScope.class), null, null).visitEnd();
         }
 
         // Some scopes (closures, module/class bodies) do not have explicit call protocol yet.
         // Unconditionally load current dynamic scope for those bodies.
         // FIXME: If I don't load this there are some scopes that end up trying to use it before it is there
         // Try uncommenting and running compiler specs.
 //        if (!scope.hasExplicitCallProtocol()) {
             jvmMethod().loadContext();
             jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("org.jruby.runtime.DynamicScope getCurrentScope()"));
             jvmStoreLocal(DYNAMIC_SCOPE);
 //        }
 
         IRBytecodeAdapter m = jvmMethod();
 
         int numberOfLabels = bbs.size();
         int ipc = 0; // synthetic, used for debug traces that show which instr failed
         for (int i = 0; i < numberOfLabels; i++) {
             BasicBlock bb = bbs.get(i);
             org.objectweb.asm.Label start = jvm.methodData().getLabel(bb.getLabel());
             Label rescueLabel = exceptionTable.get(bb);
             org.objectweb.asm.Label end = null;
 
             m.mark(start);
 
             boolean newEnd = false;
             if (rescueLabel != null) {
                 if (i+1 < numberOfLabels) {
                     end = jvm.methodData().getLabel(bbs.get(i+1).getLabel());
                 } else {
                     newEnd = true;
                     end = new org.objectweb.asm.Label();
                 }
 
                 org.objectweb.asm.Label rescue = jvm.methodData().getLabel(rescueLabel);
                 jvmAdapter().trycatch(start, end, rescue, p(Throwable.class));
             }
 
             // ensure there's at least one instr per block
             m.adapter.nop();
 
             // visit remaining instrs
             for (Instr instr : bb.getInstrs()) {
                 if (DEBUG) instr.setIPC(ipc++); // debug mode uses instr offset for backtrace
                 visit(instr);
             }
 
             if (newEnd) {
                 m.mark(end);
             }
         }
 
         jvm.popmethod();
     }
 
     private static final Signature METHOD_SIGNATURE_BASE = Signature
             .returning(IRubyObject.class)
             .appendArgs(new String[]{"context", "scope", "self", "block", "class"}, ThreadContext.class, StaticScope.class, IRubyObject.class, Block.class, RubyModule.class);
 
     public static final Signature signatureFor(IRScope method, boolean aritySplit) {
         if (aritySplit) {
             StaticScope argScope = method.getStaticScope();
             if (argScope.isArgumentScope() &&
                     argScope.getOptionalArgs() == 0 &&
                     argScope.getRestArg() == -1 &&
                     !method.receivesKeywordArgs()) {
                 // we have only required arguments...emit a signature appropriate to that arity
                 String[] args = new String[argScope.getRequiredArgs()];
                 Class[] types = Helpers.arrayOf(Class.class, args.length, IRubyObject.class);
                 for (int i = 0; i < args.length; i++) {
                     args[i] = "arg" + i;
                 }
                 return METHOD_SIGNATURE_BASE.insertArgs(3, args, types);
             }
             // we can't do an specific-arity signature
             return null;
         }
 
         // normal boxed arg list signature
         return METHOD_SIGNATURE_BASE.insertArgs(3, new String[]{"args"}, IRubyObject[].class);
     }
 
     private static final Signature CLOSURE_SIGNATURE = Signature
             .returning(IRubyObject.class)
             .appendArgs(new String[]{"context", "scope", "self", "args", "block", "superName", "type"}, ThreadContext.class, StaticScope.class, IRubyObject.class, IRubyObject[].class, Block.class, String.class, Block.Type.class);
 
     public void emitScriptBody(IRScriptBody script) {
         String clsName = jvm.scriptToClass(script.getFileName());
         jvm.pushscript(clsName, script.getFileName());
 
         emitScope(script, "__script__", signatureFor(script, false), false);
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     public void emitMethod(IRMethod method) {
         String name = JavaNameMangler.mangleMethodName(method.getName() + "_" + methodIndex++);
 
         emitWithSignatures(method, name);
     }
 
     public void  emitMethodJIT(IRMethod method) {
         String clsName = jvm.scriptToClass(method.getFileName());
         jvm.pushscript(clsName, method.getFileName());
 
         emitWithSignatures(method, "__script__");
 
         jvm.cls().visitEnd();
         jvm.popclass();
     }
 
     private void emitWithSignatures(IRMethod method, String name) {
         method.setJittedName(name);
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false);
         method.addNativeSignature(-1, signature.type());
 
         Signature specificSig = signatureFor(method, true);
         if (specificSig != null) {
             emitScope(method, name, specificSig, true);
             method.addNativeSignature(method.getStaticScope().getRequiredArgs(), specificSig.type());
         }
     }
 
     public Handle emitModuleBodyJIT(IRModuleBody method) {
         String baseName = method.getName() + "_" + methodIndex++;
         String name;
 
         if (baseName.indexOf("DUMMY_MC") != -1) {
             name = "METACLASS_" + methodIndex++;
         } else {
             name = baseName + "_" + methodIndex++;
         }
         String clsName = jvm.scriptToClass(method.getFileName());
         jvm.pushscript(clsName, method.getFileName());
 
         Signature signature = signatureFor(method, false);
         emitScope(method, "__script__", signature, false);
 
         Handle handle = new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(signature.type().returnType(), signature.type().parameterArray()));
 
         jvm.cls().visitEnd();
         jvm.popclass();
 
         return handle;
     }
 
     private void emitClosures(IRScope s) {
         // Emit code for all nested closures
         for (IRClosure c: s.getClosures()) {
             c.setHandle(emitClosure(c));
         }
     }
 
     public Handle emitClosure(IRClosure closure) {
         /* Compile the closure like a method */
         String name = JavaNameMangler.mangleMethodName(closure.getName() + "__" + closure.getLexicalParent().getName() + "_" + methodIndex++);
 
         emitScope(closure, name, CLOSURE_SIGNATURE, false);
 
         return new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(CLOSURE_SIGNATURE.type().returnType(), CLOSURE_SIGNATURE.type().parameterArray()));
     }
 
     public Handle emitModuleBody(IRModuleBody method) {
         String baseName = method.getName() + "_" + methodIndex++;
         String name;
 
         if (baseName.indexOf("DUMMY_MC") != -1) {
             name = "METACLASS_" + methodIndex++;
         } else {
             name = baseName + "_" + methodIndex++;
         }
 
         Signature signature = signatureFor(method, false);
         emitScope(method, name, signature, false);
 
         return new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(signature.type().returnType(), signature.type().parameterArray()));
     }
 
     public void visit(Instr instr) {
         if (DEBUG) { // debug will skip emitting actual file line numbers
             jvmAdapter().line(instr.getIPC());
         }
         instr.visit(this);
     }
 
     public void visit(Operand operand) {
         operand.visit(this);
     }
 
     private int getJVMLocalVarIndex(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: return jvm.methodData().local(variable, JVM.DOUBLE_TYPE);
             case FIXNUM: return jvm.methodData().local(variable, JVM.LONG_TYPE);
             case BOOLEAN: return jvm.methodData().local(variable, JVM.BOOLEAN_TYPE);
             default: return jvm.methodData().local(variable);
             }
         } else {
             return jvm.methodData().local(variable);
         }
     }
 
     private int getJVMLocalVarIndex(String specialVar) {
         return jvm.methodData().local(specialVar);
     }
 
     private org.objectweb.asm.Label getJVMLabel(Label label) {
         return jvm.methodData().getLabel(label);
     }
 
     private void jvmStoreLocal(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: jvmAdapter().dstore(getJVMLocalVarIndex(variable)); break;
             case FIXNUM: jvmAdapter().lstore(getJVMLocalVarIndex(variable)); break;
             case BOOLEAN: jvmAdapter().istore(getJVMLocalVarIndex(variable)); break;
             default: jvmMethod().storeLocal(getJVMLocalVarIndex(variable)); break;
             }
         } else {
             jvmMethod().storeLocal(getJVMLocalVarIndex(variable));
         }
     }
 
     private void jvmStoreLocal(String specialVar) {
         jvmMethod().storeLocal(getJVMLocalVarIndex(specialVar));
     }
 
     private void jvmLoadLocal(Variable variable) {
         if (variable instanceof TemporaryLocalVariable) {
             switch (((TemporaryLocalVariable)variable).getType()) {
             case FLOAT: jvmAdapter().dload(getJVMLocalVarIndex(variable)); break;
             case FIXNUM: jvmAdapter().lload(getJVMLocalVarIndex(variable)); break;
             case BOOLEAN: jvmAdapter().iload(getJVMLocalVarIndex(variable)); break;
             default: jvmMethod().loadLocal(getJVMLocalVarIndex(variable)); break;
             }
         } else {
             jvmMethod().loadLocal(getJVMLocalVarIndex(variable));
         }
     }
 
     private void jvmLoadLocal(String specialVar) {
         jvmMethod().loadLocal(getJVMLocalVarIndex(specialVar));
     }
 
     // JVM maintains a stack of ClassData (for nested classes being compiled)
     // Each class maintains a stack of MethodData (for methods being compiled in the class)
     // MethodData wraps a IRBytecodeAdapter which wraps a SkinnyMethodAdapter which has a ASM MethodVisitor which emits bytecode
     // A long chain of indirection: JVM -> MethodData -> IRBytecodeAdapter -> SkinnyMethodAdapter -> ASM.MethodVisitor
     // In some places, methods reference JVM -> MethodData -> IRBytecodeAdapter (via jvm.method()) and ask it to walk the last 2 links
     // In other places, methods reference JVM -> MethodData -> IRBytecodeAdapter -> SkinnyMethodAdapter (via jvm.method().adapter) and ask it to walk the last link
     // Can this be cleaned up to either (a) get rid of IRBytecodeAdapter OR (b) implement passthru' methods for SkinnyMethodAdapter methods (like the others it implements)?
 
     // SSS FIXME: Needs an update to reflect instr. change
     @Override
     public void AliasInstr(AliasInstr aliasInstr) {
         IRBytecodeAdapter m = jvm.method();
         m.loadContext();
         m.loadSelf();
         jvmLoadLocal(DYNAMIC_SCOPE);
         // CON FIXME: Ideally this would not have to pass through RubyString and toString
         visit(aliasInstr.getNewName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         visit(aliasInstr.getOldName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         m.invokeIRHelper("defineAlias", sig(void.class, ThreadContext.class, IRubyObject.class, DynamicScope.class, String.class, String.class));
     }
 
     @Override
     public void AttrAssignInstr(AttrAssignInstr attrAssignInstr) {
         compileCallCommon(
                 jvmMethod(),
                 attrAssignInstr.getMethodAddr().getName(),
                 attrAssignInstr.getCallArgs(),
                 attrAssignInstr.getReceiver(),
                 attrAssignInstr.getCallArgs().length,
                 null,
                 false,
                 attrAssignInstr.getReceiver() instanceof Self ? CallType.FUNCTIONAL : CallType.NORMAL,
                 null);
     }
 
     @Override
     public void BEQInstr(BEQInstr beqInstr) {
         jvmMethod().loadContext();
         visit(beqInstr.getArg1());
         visit(beqInstr.getArg2());
         jvmMethod().invokeHelper("BEQ", boolean.class, ThreadContext.class, IRubyObject.class, IRubyObject.class);
         jvmAdapter().iftrue(getJVMLabel(beqInstr.getJumpTarget()));
     }
 
     @Override
     public void BFalseInstr(BFalseInstr bFalseInstr) {
         Operand arg1 = bFalseInstr.getArg1();
         visit(arg1);
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (!(arg1 instanceof TemporaryBooleanVariable) && !(arg1 instanceof UnboxedBoolean)) {
             // unbox
             jvmAdapter().invokeinterface(p(IRubyObject.class), "isTrue", sig(boolean.class));
         }
         jvmMethod().bfalse(getJVMLabel(bFalseInstr.getJumpTarget()));
     }
 
     @Override
     public void BlockGivenInstr(BlockGivenInstr blockGivenInstr) {
         jvmMethod().loadContext();
         visit(blockGivenInstr.getBlockArg());
         jvmMethod().invokeIRHelper("isBlockGiven", sig(RubyBoolean.class, ThreadContext.class, Object.class));
         jvmStoreLocal(blockGivenInstr.getResult());
     }
 
     private void loadFloatArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             double val;
             if (arg instanceof Float) {
                 val = ((Float)arg).value;
             } else if (arg instanceof Fixnum) {
                 val = (double)((Fixnum)arg).value;
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFloatArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     private void loadFixnumArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             long val;
             if (arg instanceof Float) {
                 val = (long)((Float)arg).value;
             } else if (arg instanceof Fixnum) {
                 val = ((Fixnum)arg).value;
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFixnumArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     private void loadBooleanArg(Operand arg) {
         if (arg instanceof Variable) {
             visit(arg);
         } else {
             boolean val;
             if (arg instanceof UnboxedBoolean) {
                 val = ((UnboxedBoolean)arg).isTrue();
             } else {
                 // Should not happen -- so, forcing an exception.
                 throw new NotCompilableException("Non-float/fixnum in loadFixnumArg!" + arg);
             }
             jvmAdapter().ldc(val);
         }
     }
 
     @Override
     public void BoxFloatInstr(BoxFloatInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed float
         loadFloatArg(instr.getValue());
 
         // Box the float
         a.invokevirtual(p(Ruby.class), "newFloat", sig(RubyFloat.class, double.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BoxFixnumInstr(BoxFixnumInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed fixnum
         loadFixnumArg(instr.getValue());
 
         // Box the fixnum
         a.invokevirtual(p(Ruby.class), "newFixnum", sig(RubyFixnum.class, long.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BoxBooleanInstr(BoxBooleanInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load runtime
         m.loadContext();
         a.getfield(p(ThreadContext.class), "runtime", ci(Ruby.class));
 
         // Get unboxed boolean
         loadBooleanArg(instr.getValue());
 
         // Box the fixnum
         a.invokevirtual(p(Ruby.class), "newBoolean", sig(RubyBoolean.class, boolean.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxFloatInstr(UnboxFloatInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxFloat", sig(double.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxFixnumInstr(UnboxFixnumInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxFixnum", sig(long.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void UnboxBooleanInstr(UnboxBooleanInstr instr) {
         // Load boxed value
         visit(instr.getValue());
 
         // Unbox it
         jvmMethod().invokeIRHelper("unboxBoolean", sig(boolean.class, IRubyObject.class));
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     public void AluInstr(AluInstr instr) {
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         // Load args
         visit(instr.getArg1());
         visit(instr.getArg2());
 
         // Compute result
         switch (instr.getOperation()) {
             case FADD: a.dadd(); break;
             case FSUB: a.dsub(); break;
             case FMUL: a.dmul(); break;
             case FDIV: a.ddiv(); break;
             case FLT: m.invokeIRHelper("flt", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case FGT: m.invokeIRHelper("fgt", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case FEQ: m.invokeIRHelper("feq", sig(boolean.class, double.class, double.class)); break; // annoying to have to do it in a method
             case IADD: a.ladd(); break;
             case ISUB: a.lsub(); break;
             case IMUL: a.lmul(); break;
             case IDIV: a.ldiv(); break;
             case ILT: m.invokeIRHelper("ilt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             case IGT: m.invokeIRHelper("igt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             case IOR: a.lor(); break;
             case IAND: a.land(); break;
             case IXOR: a.lxor(); break;
             case ISHL: a.lshl(); break;
             case ISHR: a.lshr(); break;
             case IEQ: m.invokeIRHelper("ilt", sig(boolean.class, long.class, long.class)); break; // annoying to have to do it in a method
             default: throw new NotCompilableException("UNHANDLED!");
         }
 
         // Store it
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BacktickInstr(BacktickInstr instr) {
         // prepare for call to "`" below
         jvmMethod().loadContext();
         jvmMethod().loadSelf(); // TODO: remove caller
         jvmMethod().loadSelf();
 
         ByteList csByteList = new ByteList();
         jvmMethod().pushString(csByteList);
 
         for (Operand p : instr.getPieces()) {
             // visit piece and ensure it's a string
             visit(p);
             jvmAdapter().dup();
             org.objectweb.asm.Label after = new org.objectweb.asm.Label();
             jvmAdapter().instance_of(p(RubyString.class));
             jvmAdapter().iftrue(after);
             jvmAdapter().invokevirtual(p(IRubyObject.class), "anyToString", sig(IRubyObject.class));
 
             jvmAdapter().label(after);
             jvmAdapter().invokevirtual(p(RubyString.class), "append", sig(RubyString.class, IRubyObject.class));
         }
 
         // freeze the string
         jvmAdapter().dup();
         jvmAdapter().ldc(true);
         jvmAdapter().invokeinterface(p(IRubyObject.class), "setFrozen", sig(void.class, boolean.class));
 
         // invoke the "`" method on self
         jvmMethod().invokeSelf("`", 1, false);
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BNEInstr(BNEInstr bneinstr) {
         jvmMethod().loadContext();
         visit(bneinstr.getArg1());
         visit(bneinstr.getArg2());
         jvmMethod().invokeHelper("BNE", boolean.class, ThreadContext.class, IRubyObject.class, IRubyObject.class);
         jvmAdapter().iftrue(getJVMLabel(bneinstr.getJumpTarget()));
     }
 
     @Override
     public void BNilInstr(BNilInstr bnilinstr) {
         visit(bnilinstr.getArg1());
         jvmMethod().isNil();
         jvmMethod().btrue(getJVMLabel(bnilinstr.getJumpTarget()));
     }
 
     @Override
     public void BreakInstr(BreakInstr breakInstr) {
         jvmMethod().loadContext();
         jvmLoadLocal(DYNAMIC_SCOPE);
         visit(breakInstr.getReturnValue());
         jvmMethod().loadBlockType();
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "initiateBreak", sig(IRubyObject.class, ThreadContext.class, DynamicScope.class, IRubyObject.class, Block.Type.class));
         jvmMethod().returnValue();
 
     }
 
     @Override
     public void BTrueInstr(BTrueInstr btrueinstr) {
         Operand arg1 = btrueinstr.getArg1();
         visit(arg1);
         // this is a gross hack because we don't have distinction in boolean instrs between boxed and unboxed
         if (!(arg1 instanceof TemporaryBooleanVariable) && !(arg1 instanceof UnboxedBoolean)) {
             jvmMethod().isTrue();
         }
         jvmMethod().btrue(getJVMLabel(btrueinstr.getJumpTarget()));
     }
 
     @Override
     public void BUndefInstr(BUndefInstr bundefinstr) {
         visit(bundefinstr.getArg1());
         jvmMethod().pushUndefined();
         jvmAdapter().if_acmpeq(getJVMLabel(bundefinstr.getJumpTarget()));
     }
 
     @Override
     public void BuildCompoundArrayInstr(BuildCompoundArrayInstr instr) {
         visit(instr.getAppendingArg());
         if (instr.isArgsPush()) jvmAdapter().checkcast("org/jruby/RubyArray");
         visit(instr.getAppendedArg());
         if (instr.isArgsPush()) {
             jvmMethod().invokeHelper("argsPush", RubyArray.class, RubyArray.class, IRubyObject.class);
         } else {
             jvmMethod().invokeHelper("argsCat", RubyArray.class, IRubyObject.class, IRubyObject.class);
         }
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildCompoundStringInstr(BuildCompoundStringInstr compoundstring) {
         ByteList csByteList = new ByteList();
         csByteList.setEncoding(compoundstring.getEncoding());
         jvmMethod().pushString(csByteList);
         for (Operand p : compoundstring.getPieces()) {
 //            if ((p instanceof StringLiteral) && (compoundstring.isSameEncodingAndCodeRange((StringLiteral)p))) {
 //                jvmMethod().pushByteList(((StringLiteral)p).bytelist);
 //                jvmAdapter().invokevirtual(p(RubyString.class), "cat", sig(RubyString.class, ByteList.class));
 //            } else {
                 visit(p);
                 jvmAdapter().invokevirtual(p(RubyString.class), "append19", sig(RubyString.class, IRubyObject.class));
 //            }
         }
         jvmStoreLocal(compoundstring.getResult());
     }
 
     @Override
     public void BuildDynRegExpInstr(BuildDynRegExpInstr instr) {
         final IRBytecodeAdapter m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         RegexpOptions options = instr.getOptions();
         final List<Operand> operands = instr.getPieces();
 
         Runnable r = new Runnable() {
             @Override
             public void run() {
                 m.loadContext();
                 for (int i = 0; i < operands.size(); i++) {
                     Operand operand = operands.get(i);
                     visit(operand);
                 }
             }
         };
 
         m.pushDRegexp(r, options, operands.size());
 
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void BuildRangeInstr(BuildRangeInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getBegin());
         visit(instr.getEnd());
         jvmAdapter().ldc(instr.isExclusive());
         jvmAdapter().invokestatic(p(RubyRange.class), "newRange", sig(RubyRange.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void CallInstr(CallInstr callInstr) {
         IRBytecodeAdapter m = jvmMethod();
         String name = callInstr.getMethodAddr().getName();
         Operand[] args = callInstr.getCallArgs();
         Operand receiver = callInstr.getReceiver();
         int numArgs = args.length;
         Operand closure = callInstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         CallType callType = callInstr.getCallType();
         Variable result = callInstr.getResult();
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, result);
     }
 
     private void compileCallCommon(IRBytecodeAdapter m, String name, Operand[] args, Operand receiver, int numArgs, Operand closure, boolean hasClosure, CallType callType, Variable result) {
         m.loadContext();
         m.loadSelf(); // caller
         visit(receiver);
         int arity = numArgs;
 
         if (numArgs == 1 && args[0] instanceof Splat) {
             visit(args[0]);
             m.adapter.invokevirtual(p(RubyArray.class), "toJavaArray", sig(IRubyObject[].class));
             arity = -1;
         } else if (CallBase.containsArgSplat(args)) {
             throw new NotCompilableException("splat in non-initial argument for normal call is unsupported in JIT");
         } else {
             for (Operand operand : args) {
                 visit(operand);
             }
         }
 
         if (hasClosure) {
             m.loadContext();
             visit(closure);
             m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
         }
 
         switch (callType) {
             case FUNCTIONAL:
             case VARIABLE:
                 m.invokeSelf(name, arity, hasClosure);
                 break;
             case NORMAL:
                 m.invokeOther(name, arity, hasClosure);
                 break;
         }
 
         if (result != null) {
             jvmStoreLocal(result);
         } else {
             // still need to drop, since all dyncalls return something (FIXME)
             m.adapter.pop();
         }
     }
 
     @Override
     public void CheckArgsArrayArityInstr(CheckArgsArrayArityInstr checkargsarrayarityinstr) {
         jvmMethod().loadContext();
         visit(checkargsarrayarityinstr.getArgsArray());
         jvmAdapter().pushInt(checkargsarrayarityinstr.required);
         jvmAdapter().pushInt(checkargsarrayarityinstr.opt);
         jvmAdapter().pushInt(checkargsarrayarityinstr.rest);
         jvmMethod().invokeStatic(Type.getType(Helpers.class), Method.getMethod("void irCheckArgsArrayArity(org.jruby.runtime.ThreadContext, org.jruby.RubyArray, int, int, int)"));
     }
 
     @Override
     public void CheckArityInstr(CheckArityInstr checkarityinstr) {
         if (jvm.methodData().specificArity >= 0) {
             // no arity check in specific arity path
         } else {
             jvmMethod().loadContext();
             jvmMethod().loadArgs();
             jvmAdapter().ldc(checkarityinstr.required);
             jvmAdapter().ldc(checkarityinstr.opt);
             jvmAdapter().ldc(checkarityinstr.rest);
             jvmAdapter().ldc(checkarityinstr.receivesKeywords);
             jvmAdapter().ldc(checkarityinstr.restKey);
             jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkArity", sig(void.class, ThreadContext.class, Object[].class, int.class, int.class, int.class, boolean.class, int.class));
         }
     }
 
     @Override
     public void ClassSuperInstr(ClassSuperInstr classsuperinstr) {
         String name = classsuperinstr.getMethodAddr().getName();
         Operand[] args = classsuperinstr.getCallArgs();
         Operand definingModule = classsuperinstr.getDefiningModule();
         boolean containsArgSplat = classsuperinstr.containsArgSplat();
         Operand closure = classsuperinstr.getClosureArg(null);
 
         superCommon(name, classsuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     @Override
     public void ConstMissingInstr(ConstMissingInstr constmissinginstr) {
         visit(constmissinginstr.getReceiver());
         jvmAdapter().checkcast("org/jruby/RubyModule");
         jvmMethod().loadContext();
         jvmAdapter().ldc("const_missing");
-        jvmMethod().pushSymbol(constmissinginstr.getMissingConst());
+        // FIXME: This has lost it's encoding info by this point
+        jvmMethod().pushSymbol(constmissinginstr.getMissingConst(), USASCIIEncoding.INSTANCE);
         jvmMethod().invokeVirtual(Type.getType(RubyModule.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject callMethod(org.jruby.runtime.ThreadContext, java.lang.String, org.jruby.runtime.builtin.IRubyObject)"));
         jvmStoreLocal(constmissinginstr.getResult());
     }
 
     @Override
     public void CopyInstr(CopyInstr copyinstr) {
         Operand  src = copyinstr.getSource();
         Variable res = copyinstr.getResult();
         if (res instanceof TemporaryFloatVariable) {
             loadFloatArg(src);
         } else if (res instanceof TemporaryFixnumVariable) {
             loadFixnumArg(src);
         } else {
             visit(src);
         }
         jvmStoreLocal(res);
     }
 
     @Override
     public void DefineClassInstr(DefineClassInstr defineclassinstr) {
         IRClassBody newIRClassBody = defineclassinstr.getNewIRClassBody();
 
         jvmMethod().loadContext();
         Handle handle = emitModuleBody(newIRClassBody);
         jvmMethod().pushHandle(handle);
         jvmAdapter().getstatic(jvm.clsData().clsName, handle.getName() + "_IRScope", ci(IRScope.class));
         visit(defineclassinstr.getContainer());
         visit(defineclassinstr.getSuperClass());
 
         jvmMethod().invokeIRHelper("newCompiledClassBody", sig(DynamicMethod.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, Object.class, Object.class));
 
         jvmStoreLocal(defineclassinstr.getResult());
     }
 
     @Override
     public void DefineClassMethodInstr(DefineClassMethodInstr defineclassmethodinstr) {
         IRMethod method = defineclassmethodinstr.getMethod();
 
         jvmMethod().loadContext();
 
         emitMethod(method);
 
         Map<Integer, MethodType> signatures = method.getNativeSignatures();
 
         MethodType signature = signatures.get(-1);
 
         String defSignature = pushHandlesForDef(
                 method.getJittedName(),
                 signatures,
                 signature,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, IRubyObject.class));
 
         jvmAdapter().getstatic(jvm.clsData().clsName, method.getJittedName() + "_IRScope", ci(IRScope.class));
         visit(defineclassmethodinstr.getContainer());
 
         // add method
         jvmMethod().adapter.invokestatic(p(IRRuntimeHelpers.class), "defCompiledClassMethod", defSignature);
     }
 
     // SSS FIXME: Needs an update to reflect instr. change
     @Override
     public void DefineInstanceMethodInstr(DefineInstanceMethodInstr defineinstancemethodinstr) {
         IRMethod method = defineinstancemethodinstr.getMethod();
 
         IRBytecodeAdapter   m = jvmMethod();
         SkinnyMethodAdapter a = m.adapter;
 
         m.loadContext();
 
         emitMethod(method);
         Map<Integer, MethodType> signatures = method.getNativeSignatures();
 
         MethodType variable = signatures.get(-1); // always a variable arity handle
 
         String defSignature = pushHandlesForDef(
                 method.getJittedName(),
                 signatures,
                 variable,
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, DynamicScope.class, IRubyObject.class),
                 sig(void.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, java.lang.invoke.MethodHandle.class, int.class, IRScope.class, DynamicScope.class, IRubyObject.class));
 
         a.getstatic(jvm.clsData().clsName, method.getJittedName() + "_IRScope", ci(IRScope.class));
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadSelf();
 
         // add method
         a.invokestatic(p(IRRuntimeHelpers.class), "defCompiledInstanceMethod", defSignature);
     }
 
     public String pushHandlesForDef(String name, Map<Integer, MethodType> signatures, MethodType variable, String variableOnly, String variableAndSpecific) {
         String defSignature;
 
         jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(variable.returnType(), variable.parameterArray())));
 
         if (signatures.size() == 1) {
             defSignature = variableOnly;
         } else {
             defSignature = variableAndSpecific;
 
             // FIXME: only supports one arity
             for (Map.Entry<Integer, MethodType> entry : signatures.entrySet()) {
                 if (entry.getKey() == -1) continue; // variable arity signature pushed above
                 jvmMethod().pushHandle(new Handle(Opcodes.H_INVOKESTATIC, jvm.clsData().clsName, name, sig(entry.getValue().returnType(), entry.getValue().parameterArray())));
                 jvmAdapter().pushInt(entry.getKey());
                 break;
             }
         }
         return defSignature;
     }
 
     @Override
     public void DefineMetaClassInstr(DefineMetaClassInstr definemetaclassinstr) {
         IRModuleBody metaClassBody = definemetaclassinstr.getMetaClassBody();
 
         jvmMethod().loadContext();
         Handle handle = emitModuleBody(metaClassBody);
         jvmMethod().pushHandle(handle);
         jvmAdapter().getstatic(jvm.clsData().clsName, handle.getName() + "_IRScope", ci(IRScope.class));
         visit(definemetaclassinstr.getObject());
 
         jvmMethod().invokeIRHelper("newCompiledMetaClass", sig(DynamicMethod.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, IRubyObject.class));
 
         jvmStoreLocal(definemetaclassinstr.getResult());
     }
 
     @Override
     public void DefineModuleInstr(DefineModuleInstr definemoduleinstr) {
         IRModuleBody newIRModuleBody = definemoduleinstr.getNewIRModuleBody();
 
         jvmMethod().loadContext();
         Handle handle = emitModuleBody(newIRModuleBody);
         jvmMethod().pushHandle(handle);
         jvmAdapter().getstatic(jvm.clsData().clsName, handle.getName() + "_IRScope", ci(IRScope.class));
         visit(definemoduleinstr.getContainer());
 
         jvmMethod().invokeIRHelper("newCompiledModuleBody", sig(DynamicMethod.class, ThreadContext.class, java.lang.invoke.MethodHandle.class, IRScope.class, Object.class));
 
         jvmStoreLocal(definemoduleinstr.getResult());
     }
 
     @Override
     public void EQQInstr(EQQInstr eqqinstr) {
         jvmMethod().loadContext();
         visit(eqqinstr.getArg1());
         visit(eqqinstr.getArg2());
         jvmMethod().invokeIRHelper("isEQQ", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class));
         jvmStoreLocal(eqqinstr.getResult());
     }
 
     @Override
     public void ExceptionRegionEndMarkerInstr(ExceptionRegionEndMarkerInstr exceptionregionendmarkerinstr) {
         throw new NotCompilableException("Marker instructions shouldn't reach compiler: " + exceptionregionendmarkerinstr);
     }
 
     @Override
     public void ExceptionRegionStartMarkerInstr(ExceptionRegionStartMarkerInstr exceptionregionstartmarkerinstr) {
         throw new NotCompilableException("Marker instructions shouldn't reach compiler: " + exceptionregionstartmarkerinstr);
     }
 
     @Override
     public void GetClassVarContainerModuleInstr(GetClassVarContainerModuleInstr getclassvarcontainermoduleinstr) {
         jvmMethod().loadContext();
         visit(getclassvarcontainermoduleinstr.getStartingScope());
         if (getclassvarcontainermoduleinstr.getObject() != null) {
             visit(getclassvarcontainermoduleinstr.getObject());
         } else {
             jvmAdapter().aconst_null();
         }
         jvmMethod().invokeIRHelper("getModuleFromScope", sig(RubyModule.class, ThreadContext.class, StaticScope.class, IRubyObject.class));
         jvmStoreLocal(getclassvarcontainermoduleinstr.getResult());
     }
 
     @Override
     public void GetClassVariableInstr(GetClassVariableInstr getclassvariableinstr) {
         visit(getclassvariableinstr.getSource());
         jvmAdapter().checkcast(p(RubyModule.class));
         jvmAdapter().ldc(getclassvariableinstr.getRef());
         jvmAdapter().invokevirtual(p(RubyModule.class), "getClassVar", sig(IRubyObject.class, String.class));
         jvmStoreLocal(getclassvariableinstr.getResult());
     }
 
     @Override
     public void GetFieldInstr(GetFieldInstr getfieldinstr) {
         visit(getfieldinstr.getSource());
         jvmMethod().getField(getfieldinstr.getRef());
         jvmStoreLocal(getfieldinstr.getResult());
     }
 
     @Override
     public void GetGlobalVariableInstr(GetGlobalVariableInstr getglobalvariableinstr) {
         Operand source = getglobalvariableinstr.getSource();
         GlobalVariable gvar = (GlobalVariable)source;
         String name = gvar.getName();
         jvmMethod().loadRuntime();
         jvmMethod().invokeVirtual(Type.getType(Ruby.class), Method.getMethod("org.jruby.internal.runtime.GlobalVariables getGlobalVariables()"));
         jvmAdapter().ldc(name);
         jvmMethod().invokeVirtual(Type.getType(GlobalVariables.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject get(String)"));
         jvmStoreLocal(getglobalvariableinstr.getResult());
     }
 
     @Override
     public void GVarAliasInstr(GVarAliasInstr gvaraliasinstr) {
         jvmMethod().loadRuntime();
         jvmAdapter().invokevirtual(p(Ruby.class), "getGlobalVariables", sig(GlobalVariables.class));
         visit(gvaraliasinstr.getNewName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         visit(gvaraliasinstr.getOldName());
         jvmAdapter().invokevirtual(p(Object.class), "toString", sig(String.class));
         jvmAdapter().invokevirtual(p(GlobalVariables.class), "alias", sig(void.class, String.class, String.class));
     }
 
     @Override
     public void InheritanceSearchConstInstr(InheritanceSearchConstInstr inheritancesearchconstinstr) {
         jvmMethod().loadContext();
         visit(inheritancesearchconstinstr.getCurrentModule());
 
         jvmMethod().inheritanceSearchConst(inheritancesearchconstinstr.getConstName(), inheritancesearchconstinstr.isNoPrivateConsts());
         jvmStoreLocal(inheritancesearchconstinstr.getResult());
     }
 
     @Override
     public void InstanceSuperInstr(InstanceSuperInstr instancesuperinstr) {
         String name = instancesuperinstr.getMethodAddr().getName();
         Operand[] args = instancesuperinstr.getCallArgs();
         Operand definingModule = instancesuperinstr.getDefiningModule();
         boolean containsArgSplat = instancesuperinstr.containsArgSplat();
         Operand closure = instancesuperinstr.getClosureArg(null);
 
         superCommon(name, instancesuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     private void superCommon(String name, CallInstr instr, Operand[] args, Operand definingModule, boolean containsArgSplat, Operand closure) {
         IRBytecodeAdapter m = jvmMethod();
         Operation operation = instr.getOperation();
 
         m.loadContext();
         m.loadSelf(); // TODO: get rid of caller
         m.loadSelf();
         if (definingModule == UndefinedValue.UNDEFINED) {
             jvmAdapter().aconst_null();
         } else {
             visit(definingModule);
         }
 
         // TODO: CON: is this safe?
         jvmAdapter().checkcast(p(RubyClass.class));
 
         // process args
         for (int i = 0; i < args.length; i++) {
             Operand operand = args[i];
             visit(operand);
         }
 
         // if there's splats, provide a map and let the call site sort it out
         boolean[] splatMap = IRRuntimeHelpers.buildSplatMap(args, containsArgSplat);
 
         boolean hasClosure = closure != null;
         if (hasClosure) {
             m.loadContext();
             visit(closure);
             m.invokeIRHelper("getBlockFromObject", sig(Block.class, ThreadContext.class, Object.class));
         }
 
         switch (operation) {
             case INSTANCE_SUPER:
                 m.invokeInstanceSuper(name, args.length, hasClosure, splatMap);
                 break;
             case CLASS_SUPER:
                 m.invokeClassSuper(name, args.length, hasClosure, splatMap);
                 break;
             case UNRESOLVED_SUPER:
                 m.invokeUnresolvedSuper(name, args.length, hasClosure, splatMap);
                 break;
             case ZSUPER:
                 m.invokeZSuper(name, args.length, hasClosure, splatMap);
                 break;
             default:
                 throw new NotCompilableException("unknown super type " + operation + " in " + instr);
         }
 
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void JumpInstr(JumpInstr jumpinstr) {
         jvmMethod().goTo(getJVMLabel(jumpinstr.getJumpTarget()));
     }
 
     @Override
     public void LabelInstr(LabelInstr labelinstr) {
     }
 
     @Override
     public void LexicalSearchConstInstr(LexicalSearchConstInstr lexicalsearchconstinstr) {
         jvmMethod().loadContext();
         visit(lexicalsearchconstinstr.getDefiningScope());
 
         jvmMethod().lexicalSearchConst(lexicalsearchconstinstr.getConstName());
 
         jvmStoreLocal(lexicalsearchconstinstr.getResult());
     }
 
     @Override
     public void LineNumberInstr(LineNumberInstr linenumberinstr) {
         if (DEBUG) return; // debug mode uses IPC for line numbers
 
         jvmAdapter().line(linenumberinstr.getLineNumber() + 1);
     }
 
     @Override
     public void LoadLocalVarInstr(LoadLocalVarInstr loadlocalvarinstr) {
         IRBytecodeAdapter m = jvmMethod();
         jvmLoadLocal(DYNAMIC_SCOPE);
         int depth = loadlocalvarinstr.getLocalVar().getScopeDepth();
         int location = loadlocalvarinstr.getLocalVar().getLocation();
         // TODO if we can avoid loading nil unnecessarily, it could be a big win
         OUTER: switch (depth) {
             case 0:
                 switch (location) {
                     case 0:
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueZeroDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
                         break OUTER;
                     case 1:
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueOneDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
                         break OUTER;
                     case 2:
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueTwoDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
                         break OUTER;
                     case 3:
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueThreeDepthZeroOrNil", sig(IRubyObject.class, IRubyObject.class));
                         break OUTER;
                     default:
                         m.adapter.pushInt(location);
                         m.pushNil();
                         m.adapter.invokevirtual(p(DynamicScope.class), "getValueDepthZeroOrNil", sig(IRubyObject.class, int.class, IRubyObject.class));
                         break OUTER;
                 }
             default:
                 m.adapter.pushInt(location);
                 m.adapter.pushInt(depth);
                 m.pushNil();
                 m.adapter.invokevirtual(p(DynamicScope.class), "getValueOrNil", sig(IRubyObject.class, int.class, int.class, IRubyObject.class));
         }
         jvmStoreLocal(loadlocalvarinstr.getResult());
     }
 
     @Override
     public void Match2Instr(Match2Instr match2instr) {
         visit(match2instr.getReceiver());
         jvmMethod().loadContext();
         visit(match2instr.getArg());
         jvmAdapter().invokevirtual(p(RubyRegexp.class), "op_match19", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class));
         jvmStoreLocal(match2instr.getResult());
     }
 
     @Override
     public void Match3Instr(Match3Instr match3instr) {
         jvmMethod().loadContext();
         visit(match3instr.getReceiver());
         visit(match3instr.getArg());
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "match3", sig(IRubyObject.class, ThreadContext.class, RubyRegexp.class, IRubyObject.class));
         jvmStoreLocal(match3instr.getResult());
     }
 
     @Override
     public void MatchInstr(MatchInstr matchinstr) {
         visit(matchinstr.getReceiver());
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(RubyRegexp.class), "op_match2_19", sig(IRubyObject.class, ThreadContext.class));
         jvmStoreLocal(matchinstr.getResult());
     }
 
     @Override
     public void MethodLookupInstr(MethodLookupInstr methodlookupinstr) {
         // SSS FIXME: Unused at this time
         throw new NotCompilableException("Unsupported instruction: " + methodlookupinstr);
     }
 
     @Override
     public void ModuleVersionGuardInstr(ModuleVersionGuardInstr moduleversionguardinstr) {
         // SSS FIXME: Unused at this time
         throw new NotCompilableException("Unsupported instruction: " + moduleversionguardinstr);
     }
 
     @Override
     public void NopInstr(NopInstr nopinstr) {
         // do nothing
     }
 
     @Override
     public void NoResultCallInstr(NoResultCallInstr noResultCallInstr) {
         IRBytecodeAdapter m = jvmMethod();
         String name = noResultCallInstr.getMethodAddr().getName();
         Operand[] args = noResultCallInstr.getCallArgs();
         Operand receiver = noResultCallInstr.getReceiver();
         int numArgs = args.length;
         Operand closure = noResultCallInstr.getClosureArg(null);
         boolean hasClosure = closure != null;
         CallType callType = noResultCallInstr.getCallType();
 
         compileCallCommon(m, name, args, receiver, numArgs, closure, hasClosure, callType, null);
     }
 
     @Override
     public void OptArgMultipleAsgnInstr(OptArgMultipleAsgnInstr optargmultipleasgninstr) {
         visit(optargmultipleasgninstr.getArrayArg());
         jvmAdapter().checkcast(p(RubyArray.class));
         jvmAdapter().ldc(optargmultipleasgninstr.getMinArgsLength());
         jvmAdapter().ldc(optargmultipleasgninstr.getIndex());
         jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "extractOptionalArgument", sig(IRubyObject.class, RubyArray.class, int.class, int.class));
         jvmStoreLocal(optargmultipleasgninstr.getResult());
     }
 
     @Override
     public void PopBindingInstr(PopBindingInstr popbindinginstr) {
         jvmMethod().loadContext();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void popScope()"));
     }
 
     @Override
     public void PopFrameInstr(PopFrameInstr popframeinstr) {
         jvmMethod().loadContext();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void postMethodFrameOnly()"));
     }
 
     @Override
     public void ProcessModuleBodyInstr(ProcessModuleBodyInstr processmodulebodyinstr) {
         jvmMethod().loadContext();
         visit(processmodulebodyinstr.getModuleBody());
         jvmMethod().invokeIRHelper("invokeModuleBody", sig(IRubyObject.class, ThreadContext.class, DynamicMethod.class));
         jvmStoreLocal(processmodulebodyinstr.getResult());
     }
 
     @Override
     public void PushBindingInstr(PushBindingInstr pushbindinginstr) {
         jvmMethod().loadContext();
         jvmMethod().loadStaticScope();
         jvmAdapter().invokestatic(p(DynamicScope.class), "newDynamicScope", sig(DynamicScope.class, StaticScope.class));
         jvmAdapter().dup();
         jvmStoreLocal(DYNAMIC_SCOPE);
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void pushScope(org.jruby.runtime.DynamicScope)"));
     }
 
     @Override
     public void RaiseRequiredKeywordArgumentErrorInstr(RaiseRequiredKeywordArgumentError instr) {
         jvmMethod().loadContext();
         jvmAdapter().ldc(instr.getName());
         jvmMethod().invokeIRHelper("newRequiredKeywordArgumentError", sig(RaiseException.class, ThreadContext.class, String.class));
         jvmAdapter().athrow();
     }
 
     @Override
     public void PushFrameInstr(PushFrameInstr pushframeinstr) {
         jvmMethod().loadContext();
         jvmMethod().loadFrameClass();
         jvmAdapter().ldc(pushframeinstr.getFrameName().getName());
         jvmMethod().loadSelf();
         jvmMethod().loadBlock();
         jvmMethod().invokeVirtual(Type.getType(ThreadContext.class), Method.getMethod("void preMethodFrameOnly(org.jruby.RubyModule, String, org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.Block)"));
 
         // FIXME: this should be part of explicit call protocol only when needed, optimizable, and correct for the scope
         // See also CompiledIRMethod.call
         jvmMethod().loadContext();
         jvmAdapter().invokestatic(p(Visibility.class), "values", sig(Visibility[].class));
         jvmAdapter().ldc(Visibility.PUBLIC.ordinal());
         jvmAdapter().aaload();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "setCurrentVisibility", sig(void.class, Visibility.class));
     }
 
     @Override
     public void PutClassVariableInstr(PutClassVariableInstr putclassvariableinstr) {
         visit(putclassvariableinstr.getValue());
         visit(putclassvariableinstr.getTarget());
 
         // don't understand this logic; duplicated from interpreter
         if (putclassvariableinstr.getValue() instanceof CurrentScope) {
             jvmAdapter().pop2();
             return;
         }
 
         // hmm.
         jvmAdapter().checkcast(p(RubyModule.class));
         jvmAdapter().swap();
         jvmAdapter().ldc(putclassvariableinstr.getRef());
         jvmAdapter().swap();
         jvmAdapter().invokevirtual(p(RubyModule.class), "setClassVar", sig(IRubyObject.class, String.class, IRubyObject.class));
         jvmAdapter().pop();
     }
 
     @Override
     public void PutConstInstr(PutConstInstr putconstinstr) {
         IRBytecodeAdapter m = jvmMethod();
         visit(putconstinstr.getTarget());
         m.adapter.checkcast(p(RubyModule.class));
         m.adapter.ldc(putconstinstr.getRef());
         visit(putconstinstr.getValue());
         m.adapter.invokevirtual(p(RubyModule.class), "setConstant", sig(IRubyObject.class, String.class, IRubyObject.class));
         m.adapter.pop();
     }
 
     @Override
     public void PutFieldInstr(PutFieldInstr putfieldinstr) {
         visit(putfieldinstr.getTarget());
         visit(putfieldinstr.getValue());
         jvmMethod().putField(putfieldinstr.getRef());
     }
 
     @Override
     public void PutGlobalVarInstr(PutGlobalVarInstr putglobalvarinstr) {
         GlobalVariable target = (GlobalVariable)putglobalvarinstr.getTarget();
         String name = target.getName();
         jvmMethod().loadRuntime();
         jvmMethod().invokeVirtual(Type.getType(Ruby.class), Method.getMethod("org.jruby.internal.runtime.GlobalVariables getGlobalVariables()"));
         jvmAdapter().ldc(name);
         visit(putglobalvarinstr.getValue());
         jvmMethod().invokeVirtual(Type.getType(GlobalVariables.class), Method.getMethod("org.jruby.runtime.builtin.IRubyObject set(String, org.jruby.runtime.builtin.IRubyObject)"));
         // leaves copy of value on stack
         jvmAdapter().pop();
     }
 
     @Override
     public void ReceiveClosureInstr(ReceiveClosureInstr receiveclosureinstr) {
         jvmMethod().loadRuntime();
         jvmLoadLocal("$block");
         jvmMethod().invokeIRHelper("newProc", sig(IRubyObject.class, Ruby.class, Block.class));
         jvmStoreLocal(receiveclosureinstr.getResult());
     }
 
     @Override
     public void ReceiveRubyExceptionInstr(ReceiveRubyExceptionInstr receiveexceptioninstr) {
         // exception should be on stack from try/catch, so unwrap and store it
         jvmStoreLocal(receiveexceptioninstr.getResult());
     }
 
     @Override
     public void ReceiveJRubyExceptionInstr(ReceiveJRubyExceptionInstr receiveexceptioninstr) {
         // exception should be on stack from try/catch, so just store it
         jvmStoreLocal(receiveexceptioninstr.getResult());
     }
 
     @Override
     public void ReceiveKeywordArgInstr(ReceiveKeywordArgInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.required);
         jvmAdapter().ldc(instr.argName);
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receiveKeywordArg", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class, String.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceiveKeywordRestArgInstr(ReceiveKeywordRestArgInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.required);
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receiveKeywordRestArg", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceiveOptArgInstr(ReceiveOptArgInstr instr) {
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.requiredArgs);
         jvmAdapter().pushInt(instr.preArgs);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receiveOptArg", sig(IRubyObject.class, IRubyObject[].class, int.class, int.class, int.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceivePreReqdArgInstr(ReceivePreReqdArgInstr instr) {
         if (jvm.methodData().specificArity >= 0 &&
                 instr.getArgIndex() < jvm.methodData().specificArity) {
             jvmAdapter().aload(jvm.methodData().signature.argOffset("arg" + instr.getArgIndex()));
         } else {
             jvmMethod().loadContext();
             jvmMethod().loadArgs();
             jvmAdapter().pushInt(instr.getArgIndex());
             jvmMethod().invokeIRHelper("getPreArgSafe", sig(IRubyObject.class, ThreadContext.class, IRubyObject[].class, int.class));
         }
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceivePostReqdArgInstr(ReceivePostReqdArgInstr instr) {
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.preReqdArgsCount);
         jvmAdapter().pushInt(instr.postReqdArgsCount);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receivePostReqdArg", sig(IRubyObject.class, IRubyObject[].class, int.class, int.class, int.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceiveRestArgInstr(ReceiveRestArgInstr instr) {
         jvmMethod().loadContext();
         jvmMethod().loadArgs();
         jvmAdapter().pushInt(instr.required);
         jvmAdapter().pushInt(instr.getArgIndex());
         jvmAdapter().ldc(jvm.methodData().scope.receivesKeywordArgs());
         jvmMethod().invokeIRHelper("receiveRestArg", sig(IRubyObject.class, ThreadContext.class, Object[].class, int.class, int.class, boolean.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void ReceiveSelfInstr(ReceiveSelfInstr receiveselfinstr) {
         jvmMethod().loadSelf();
         jvmStoreLocal(receiveselfinstr.getResult());
     }
 
     @Override
     public void ReqdArgMultipleAsgnInstr(ReqdArgMultipleAsgnInstr reqdargmultipleasgninstr) {
         jvmMethod().loadContext();
         visit(reqdargmultipleasgninstr.getArrayArg());
         jvmAdapter().checkcast(p(RubyArray.class));
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getPreArgsCount());
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getIndex());
         jvmAdapter().pushInt(reqdargmultipleasgninstr.getPostArgsCount());
         jvmMethod().invokeIRHelper("irReqdArgMultipleAsgn", sig(IRubyObject.class, ThreadContext.class, RubyArray.class, int.class, int.class, int.class));
         jvmStoreLocal(reqdargmultipleasgninstr.getResult());
     }
 
     @Override
     public void RescueEQQInstr(RescueEQQInstr rescueeqqinstr) {
         jvmMethod().loadContext();
         visit(rescueeqqinstr.getArg1());
         visit(rescueeqqinstr.getArg2());
         jvmMethod().invokeIRHelper("isExceptionHandled", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, Object.class));
         jvmStoreLocal(rescueeqqinstr.getResult());
     }
 
     @Override
     public void RestArgMultipleAsgnInstr(RestArgMultipleAsgnInstr restargmultipleasgninstr) {
         jvmMethod().loadContext();
         visit(restargmultipleasgninstr.getArrayArg());
         jvmAdapter().checkcast(p(RubyArray.class));
         jvmAdapter().pushInt(restargmultipleasgninstr.getPreArgsCount());
         jvmAdapter().pushInt(restargmultipleasgninstr.getPostArgsCount());
         jvmAdapter().invokestatic(p(Helpers.class), "viewArgsArray", sig(RubyArray.class, ThreadContext.class, RubyArray.class, int.class, int.class));
         jvmStoreLocal(restargmultipleasgninstr.getResult());
     }
 
     @Override
     public void RuntimeHelperCall(RuntimeHelperCall runtimehelpercall) {
         switch (runtimehelpercall.getHelperMethod()) {
             case HANDLE_PROPAGATE_BREAK:
                 jvmMethod().loadContext();
                 jvmLoadLocal(DYNAMIC_SCOPE);
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmMethod().loadBlockType();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "handlePropagatedBreak", sig(IRubyObject.class, ThreadContext.class, DynamicScope.class, Object.class, Block.Type.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case HANDLE_NONLOCAL_RETURN:
                 jvmMethod().loadStaticScope();
                 jvmLoadLocal(DYNAMIC_SCOPE);
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmMethod().loadBlockType();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "handleNonlocalReturn", sig(IRubyObject.class, StaticScope.class, DynamicScope.class, Object.class, Block.Type.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case HANDLE_BREAK_AND_RETURNS_IN_LAMBDA:
                 jvmMethod().loadContext();
                 jvmMethod().loadStaticScope();
                 jvmLoadLocal(DYNAMIC_SCOPE);
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmMethod().loadBlockType();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "handleBreakAndReturnsInLambdas", sig(IRubyObject.class, ThreadContext.class, StaticScope.class, DynamicScope.class, Object.class, Block.Type.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_BACKREF:
                 jvmMethod().loadContext();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedBackref", sig(IRubyObject.class, ThreadContext.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CALL:
                 jvmMethod().loadContext();
                 jvmMethod().loadSelf();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral) runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedCall", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CONSTANT_OR_METHOD:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedConstantOrMethod", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_NTH_REF:
                 jvmMethod().loadContext();
                 jvmAdapter().ldc((int)((Fixnum)runtimehelpercall.getArgs()[0]).getValue());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedNthRef", sig(IRubyObject.class, ThreadContext.class, int.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_GLOBAL:
                 jvmMethod().loadContext();
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[0]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedGlobal", sig(IRubyObject.class, ThreadContext.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_INSTANCE_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedInstanceVar", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_CLASS_VAR:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().checkcast(p(RubyModule.class));
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedClassVar", sig(IRubyObject.class, ThreadContext.class, RubyModule.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_SUPER:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral)runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedSuper", sig(IRubyObject.class, ThreadContext.class, String.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case IS_DEFINED_METHOD:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 jvmAdapter().ldc(((StringLiteral) runtimehelpercall.getArgs()[1]).getString());
                 jvmAdapter().ldc(((Boolean)runtimehelpercall.getArgs()[2]).isTrue());
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "isDefinedMethod", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class, boolean.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case MERGE_KWARGS:
                 jvmMethod().loadContext();
                 visit(runtimehelpercall.getArgs()[0]);
                 visit(runtimehelpercall.getArgs()[1]);
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "mergeKeywordArguments", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, IRubyObject.class));
                 jvmStoreLocal(runtimehelpercall.getResult());
                 break;
             case CHECK_FOR_LJE:
                 jvmMethod().loadContext();
                 jvmLoadLocal(DYNAMIC_SCOPE);
                 jvmAdapter().ldc(((Boolean)runtimehelpercall.getArgs()[0]).isTrue());
                 jvmMethod().loadBlockType();
                 jvmAdapter().invokestatic(p(IRRuntimeHelpers.class), "checkForLJE", sig(void.class, ThreadContext.class, DynamicScope.class, boolean.class, Block.Type.class));
                 break;
             default:
                 throw new NotCompilableException("Unknown IR runtime helper method: " + runtimehelpercall.getHelperMethod() + "; INSTR: " + this);
         }
     }
 
     @Override
     public void NonlocalReturnInstr(NonlocalReturnInstr returninstr) {
         jvmMethod().loadContext();
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadBlockType();
         visit(returninstr.getReturnValue());
 
         jvmMethod().invokeIRHelper("initiateNonLocalReturn", sig(IRubyObject.class, ThreadContext.class, DynamicScope.class, Block.Type.class, IRubyObject.class));
         jvmMethod().returnValue();
     }
 
     @Override
     public void ReturnInstr(ReturnInstr returninstr) {
         visit(returninstr.getReturnValue());
         jvmMethod().returnValue();
     }
 
     @Override
     public void SearchConstInstr(SearchConstInstr searchconstinstr) {
         jvmMethod().loadContext();
         visit(searchconstinstr.getStartingScope());
         jvmMethod().searchConst(searchconstinstr.getConstName(), searchconstinstr.isNoPrivateConsts());
         jvmStoreLocal(searchconstinstr.getResult());
     }
 
     @Override
     public void SetCapturedVarInstr(SetCapturedVarInstr instr) {
         jvmMethod().loadContext();
         visit(instr.getMatch2Result());
         jvmAdapter().ldc(instr.getVarName());
         jvmMethod().invokeIRHelper("setCapturedVar", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class, String.class));
         jvmStoreLocal(instr.getResult());
     }
 
     @Override
     public void StoreLocalVarInstr(StoreLocalVarInstr storelocalvarinstr) {
         IRBytecodeAdapter m = jvmMethod();
         jvmLoadLocal(DYNAMIC_SCOPE);
         int depth = storelocalvarinstr.getLocalVar().getScopeDepth();
         int location = storelocalvarinstr.getLocalVar().getLocation();
         Operand storeValue = storelocalvarinstr.getValue();
         switch (depth) {
             case 0:
                 switch (location) {
                     case 0:
                         storeValue.visit(this);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueZeroDepthZero", sig(IRubyObject.class, IRubyObject.class));
                         m.adapter.pop();
                         return;
                     case 1:
                         storeValue.visit(this);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueOneDepthZero", sig(IRubyObject.class, IRubyObject.class));
                         m.adapter.pop();
                         return;
                     case 2:
                         storeValue.visit(this);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueTwoDepthZero", sig(IRubyObject.class, IRubyObject.class));
                         m.adapter.pop();
                         return;
                     case 3:
                         storeValue.visit(this);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueThreeDepthZero", sig(IRubyObject.class, IRubyObject.class));
                         m.adapter.pop();
                         return;
                     default:
                         storeValue.visit(this);
                         m.adapter.pushInt(location);
                         m.adapter.invokevirtual(p(DynamicScope.class), "setValueDepthZero", sig(IRubyObject.class, IRubyObject.class, int.class));
                         m.adapter.pop();
                         return;
                 }
             default:
                 m.adapter.pushInt(location);
                 storeValue.visit(this);
                 m.adapter.pushInt(depth);
                 m.adapter.invokevirtual(p(DynamicScope.class), "setValue", sig(IRubyObject.class, int.class, IRubyObject.class, int.class));
                 m.adapter.pop();
         }
     }
 
     @Override
     public void ThreadPollInstr(ThreadPollInstr threadpollinstr) {
         jvmMethod().checkpoint();
     }
 
     @Override
     public void ThrowExceptionInstr(ThrowExceptionInstr throwexceptioninstr) {
         visit(throwexceptioninstr.getExceptionArg());
         jvmAdapter().athrow();
     }
 
     @Override
     public void ToAryInstr(ToAryInstr toaryinstr) {
         jvmMethod().loadContext();
         visit(toaryinstr.getArrayArg());
         jvmMethod().invokeIRHelper("irToAry", sig(IRubyObject.class, ThreadContext.class, IRubyObject.class));
         jvmStoreLocal(toaryinstr.getResult());
     }
 
     @Override
     public void UndefMethodInstr(UndefMethodInstr undefmethodinstr) {
         jvmMethod().loadContext();
         visit(undefmethodinstr.getMethodName());
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmMethod().loadSelf();
         jvmMethod().invokeIRHelper("undefMethod", sig(IRubyObject.class, ThreadContext.class, Object.class, DynamicScope.class, IRubyObject.class));
         jvmStoreLocal(undefmethodinstr.getResult());
     }
 
     @Override
     public void UnresolvedSuperInstr(UnresolvedSuperInstr unresolvedsuperinstr) {
         String name = unresolvedsuperinstr.getMethodAddr().getName();
         Operand[] args = unresolvedsuperinstr.getCallArgs();
         // this would be getDefiningModule but that is not used for unresolved super
         Operand definingModule = UndefinedValue.UNDEFINED;
         boolean containsArgSplat = unresolvedsuperinstr.containsArgSplat();
         Operand closure = unresolvedsuperinstr.getClosureArg(null);
 
         superCommon(name, unresolvedsuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     @Override
     public void YieldInstr(YieldInstr yieldinstr) {
         jvmMethod().loadContext();
         visit(yieldinstr.getBlockArg());
 
         if (yieldinstr.getYieldArg() == UndefinedValue.UNDEFINED) {
             jvmMethod().invokeIRHelper("yieldSpecific", sig(IRubyObject.class, ThreadContext.class, Object.class));
         } else {
             visit(yieldinstr.getYieldArg());
             jvmAdapter().ldc(yieldinstr.isUnwrapArray());
             jvmMethod().invokeIRHelper("yield", sig(IRubyObject.class, ThreadContext.class, Object.class, Object.class, boolean.class));
         }
 
         jvmStoreLocal(yieldinstr.getResult());
     }
 
     @Override
     public void ZSuperInstr(ZSuperInstr zsuperinstr) {
         String name = zsuperinstr.getMethodAddr().getName();
         Operand[] args = zsuperinstr.getCallArgs();
         // this would be getDefiningModule but that is not used for unresolved super
         Operand definingModule = UndefinedValue.UNDEFINED;
         boolean containsArgSplat = zsuperinstr.containsArgSplat();
         Operand closure = zsuperinstr.getClosureArg(null);
 
         superCommon(name, zsuperinstr, args, definingModule, containsArgSplat, closure);
     }
 
     @Override
     public void GetErrorInfoInstr(GetErrorInfoInstr geterrorinfoinstr) {
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "getErrorInfo", sig(IRubyObject.class));
         jvmStoreLocal(geterrorinfoinstr.getResult());
     }
 
     @Override
     public void RestoreErrorInfoInstr(RestoreErrorInfoInstr restoreerrorinfoinstr) {
         jvmMethod().loadContext();
         visit(restoreerrorinfoinstr.getArg());
         jvmAdapter().invokevirtual(p(ThreadContext.class), "setErrorInfo", sig(IRubyObject.class, IRubyObject.class));
         jvmAdapter().pop();
     }
 
     // ruby 1.9 specific
     @Override
     public void BuildLambdaInstr(BuildLambdaInstr buildlambdainstr) {
         jvmMethod().loadRuntime();
 
         IRClosure body = ((WrappedIRClosure)buildlambdainstr.getLambdaBody()).getClosure();
         if (body == null) {
             jvmMethod().pushNil();
         } else {
             visit(buildlambdainstr.getLambdaBody());
         }
 
         jvmAdapter().getstatic(p(Block.Type.class), "LAMBDA", ci(Block.Type.class));
         jvmAdapter().ldc(buildlambdainstr.getPosition().getFile());
         jvmAdapter().pushInt(buildlambdainstr.getPosition().getLine());
 
         jvmAdapter().invokestatic(p(RubyProc.class), "newProc", sig(RubyProc.class, Ruby.class, Block.class, Block.Type.class, String.class, int.class));
 
         jvmStoreLocal(buildlambdainstr.getResult());
     }
 
     @Override
     public void GetEncodingInstr(GetEncodingInstr getencodinginstr) {
         jvmMethod().loadContext();
         jvmMethod().pushEncoding(getencodinginstr.getEncoding());
         jvmStoreLocal(getencodinginstr.getResult());
     }
 
     // operands
     @Override
     public void Array(Array array) {
         jvmMethod().loadContext();
 
         for (Operand operand : array.getElts()) {
             visit(operand);
         }
 
         jvmMethod().array(array.getElts().length);
     }
 
     @Override
     public void AsString(AsString asstring) {
         visit(asstring.getSource());
         jvmAdapter().invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class));
     }
 
     @Override
     public void Backref(Backref backref) {
         jvmMethod().loadContext();
         jvmAdapter().invokevirtual(p(ThreadContext.class), "getBackRef", sig(IRubyObject.class));
 
         switch (backref.type) {
             case '&':
                 jvmAdapter().invokestatic(p(RubyRegexp.class), "last_match", sig(IRubyObject.class, IRubyObject.class));
                 break;
             case '`':
                 jvmAdapter().invokestatic(p(RubyRegexp.class), "match_pre", sig(IRubyObject.class, IRubyObject.class));
                 break;
             case '\'':
                 jvmAdapter().invokestatic(p(RubyRegexp.class), "match_post", sig(IRubyObject.class, IRubyObject.class));
                 break;
             case '+':
                 jvmAdapter().invokestatic(p(RubyRegexp.class), "match_last", sig(IRubyObject.class, IRubyObject.class));
                 break;
             default:
                 assert false: "backref with invalid type";
         }
     }
 
     @Override
     public void Bignum(Bignum bignum) {
         jvmMethod().pushBignum(bignum.value);
     }
 
     @Override
     public void Boolean(org.jruby.ir.operands.Boolean booleanliteral) {
         jvmMethod().pushBoolean(booleanliteral.isTrue());
     }
 
     @Override
     public void UnboxedBoolean(org.jruby.ir.operands.UnboxedBoolean bool) {
         jvmAdapter().ldc(bool.isTrue());
     }
 
     @Override
     public void ClosureLocalVariable(ClosureLocalVariable closurelocalvariable) {
         LocalVariable(closurelocalvariable);
     }
 
     @Override
     public void Complex(Complex complex) {
         jvmMethod().loadRuntime();
         jvmMethod().pushFixnum(0);
         visit(complex.getNumber());
         jvmAdapter().invokestatic(p(RubyComplex.class), "newComplexRaw", sig(RubyComplex.class, Ruby.class, IRubyObject.class, IRubyObject.class));
     }
 
     @Override
     public void CurrentScope(CurrentScope currentscope) {
         jvmMethod().loadStaticScope();
     }
 
     @Override
     public void DynamicSymbol(DynamicSymbol dynamicsymbol) {
         jvmMethod().loadRuntime();
         visit(dynamicsymbol.getSymbolName());
         jvmAdapter().invokeinterface(p(IRubyObject.class), "asJavaString", sig(String.class));
         jvmAdapter().invokevirtual(p(Ruby.class), "newSymbol", sig(RubySymbol.class, String.class));
     }
 
     @Override
     public void Fixnum(Fixnum fixnum) {
         jvmMethod().pushFixnum(fixnum.getValue());
     }
 
     @Override
     public void FrozenString(FrozenString frozen) {
         jvmMethod().pushFrozenString(frozen.getByteList());
     }
 
     @Override
     public void UnboxedFixnum(UnboxedFixnum fixnum) {
         jvmAdapter().ldc(fixnum.getValue());
     }
 
     @Override
     public void Float(org.jruby.ir.operands.Float flote) {
         jvmMethod().pushFloat(flote.getValue());
     }
 
     @Override
     public void UnboxedFloat(org.jruby.ir.operands.UnboxedFloat flote) {
         jvmAdapter().ldc(flote.getValue());
     }
 
     @Override
     public void Hash(Hash hash) {
         List<KeyValuePair<Operand, Operand>> pairs = hash.getPairs();
         Iterator<KeyValuePair<Operand, Operand>> iter = pairs.iterator();
         boolean kwargs = hash.isKWArgsHash && pairs.get(0).getKey() == Symbol.KW_REST_ARG_DUMMY;
 
         jvmMethod().loadContext();
         if (kwargs) {
             visit(pairs.get(0).getValue());
             jvmAdapter().checkcast(p(RubyHash.class));
 
             iter.next();
         }
 
         for (; iter.hasNext() ;) {
             KeyValuePair<Operand, Operand> pair = iter.next();
             visit(pair.getKey());
             visit(pair.getValue());
         }
 
         if (kwargs) {
             jvmMethod().kwargsHash(pairs.size() - 1);
         } else {
             jvmMethod().hash(pairs.size());
         }
     }
 
     @Override
     public void LocalVariable(LocalVariable localvariable) {
         // CON FIXME: This isn't as efficient as it could be, but we should not see these in optimized JIT scopes
         jvmLoadLocal(DYNAMIC_SCOPE);
         jvmAdapter().ldc(localvariable.getOffset());
         jvmAdapter().ldc(localvariable.getScopeDepth());
         jvmMethod().pushNil();
         jvmAdapter().invokevirtual(p(DynamicScope.class), "getValueOrNil", sig(IRubyObject.class, int.class, int.class, IRubyObject.class));
     }
 
     @Override
     public void MethAddr(MethAddr methaddr) {
         jvmAdapter().ldc(methaddr.getName());
     }
 
     @Override
     public void MethodHandle(MethodHandle methodhandle) {
         // SSS FIXME: Unused at this time
         throw new NotCompilableException("Unsupported operand: " + methodhandle);
     }
 
     @Override
     public void Nil(Nil nil) {
         jvmMethod().pushNil();
     }
 
     @Override
     public void NthRef(NthRef nthref) {
         jvmMethod().loadContext();
         jvmAdapter().pushInt(nthref.matchNumber);
         jvmMethod().invokeIRHelper("nthMatch", sig(IRubyObject.class, ThreadContext.class, int.class));
     }
 
     @Override
     public void ObjectClass(ObjectClass objectclass) {
         jvmMethod().pushObjectClass();
     }
 
     @Override
     public void Rational(Rational rational) {
         jvmMethod().loadRuntime();
         jvmAdapter().ldc(rational.getNumerator());
         jvmAdapter().ldc(rational.getDenominator());
         jvmAdapter().invokevirtual(p(Ruby.class), "newRational", sig(RubyRational.class, long.class, long.class));
     }
 
     @Override
     public void Regexp(Regexp regexp) {
         if (!regexp.hasKnownValue() && !regexp.options.isOnce()) {
             jvmMethod().loadRuntime();
             visit(regexp.getRegexp());
             jvmAdapter().invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
             jvmAdapter().ldc(regexp.options.toEmbeddedOptions());
             jvmAdapter().invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, Ruby.class, RubyString.class, int.class));
             jvmAdapter().dup();
             jvmAdapter().invokevirtual(p(RubyRegexp.class), "setLiteral", sig(void.class));
         } else {
             // FIXME: need to check this on cached path
             // context.runtime.getKCode() != rubyRegexp.getKCode()) {
             jvmMethod().loadContext();
             visit(regexp.getRegexp());
             jvmMethod().pushRegexp(regexp.options.toEmbeddedOptions());
         }
     }
 
     @Override
     public void ScopeModule(ScopeModule scopemodule) {
         jvmMethod().loadStaticScope();
         jvmAdapter().pushInt(scopemodule.getScopeModuleDepth());
         jvmAdapter().invokestatic(p(Helpers.class), "getNthScopeModule", sig(RubyModule.class, StaticScope.class, int.class));
     }
 
     @Override
     public void Self(Self self) {
         jvmMethod().loadSelf();
     }
 
     @Override
     public void Splat(Splat splat) {
         jvmMethod().loadContext();
         visit(splat.getArray());
         jvmMethod().invokeIRHelper("irSplat", sig(RubyArray.class, ThreadContext.class, IRubyObject.class));
     }
 
     @Override
     public void StandardError(StandardError standarderror) {
         jvmMethod().loadRuntime();
         jvmAdapter().invokevirtual(p(Ruby.class), "getStandardError", sig(RubyClass.class));
     }
 
     @Override
     public void StringLiteral(StringLiteral stringliteral) {
         jvmMethod().pushString(stringliteral.getByteList());
     }
 
     @Override
     public void SValue(SValue svalue) {
         visit(svalue.getArray());
         jvmAdapter().dup();
         jvmAdapter().instance_of(p(RubyArray.class));
         org.objectweb.asm.Label after = new org.objectweb.asm.Label();
         jvmAdapter().iftrue(after);
         jvmAdapter().pop();
         jvmMethod().pushNil();
         jvmAdapter().label(after);
     }
 
     @Override
     public void Symbol(Symbol symbol) {
-        jvmMethod().pushSymbol(symbol.getName());
+        jvmMethod().pushSymbol(symbol.getName(), symbol.getEncoding());
     }
 
     @Override
     public void TemporaryVariable(TemporaryVariable temporaryvariable) {
         jvmLoadLocal(temporaryvariable);
     }
 
     @Override
     public void TemporaryLocalVariable(TemporaryLocalVariable temporarylocalvariable) {
         jvmLoadLocal(temporarylocalvariable);
     }
 
     @Override
     public void TemporaryFloatVariable(TemporaryFloatVariable temporaryfloatvariable) {
         jvmLoadLocal(temporaryfloatvariable);
     }
 
     @Override
     public void TemporaryFixnumVariable(TemporaryFixnumVariable temporaryfixnumvariable) {
         jvmLoadLocal(temporaryfixnumvariable);
     }
 
     @Override
     public void TemporaryBooleanVariable(TemporaryBooleanVariable temporarybooleanvariable) {
         jvmLoadLocal(temporarybooleanvariable);
     }
 
     @Override
     public void UndefinedValue(UndefinedValue undefinedvalue) {
         jvmMethod().pushUndefined();
     }
 
     @Override
     public void UnexecutableNil(UnexecutableNil unexecutablenil) {
         throw new NotCompilableException(this.getClass().getSimpleName() + " should never be directly executed!");
     }
 
     @Override
     public void WrappedIRClosure(WrappedIRClosure wrappedirclosure) {
         IRClosure closure = wrappedirclosure.getClosure();
 
         jvmAdapter().newobj(p(Block.class));
         jvmAdapter().dup();
 
         { // FIXME: block body should be cached
             jvmAdapter().newobj(p(CompiledIRBlockBody.class));
             jvmAdapter().dup();
 
             jvmAdapter().ldc(closure.getHandle());
             jvmAdapter().getstatic(jvm.clsData().clsName, closure.getHandle().getName() + "_IRScope", ci(IRScope.class));
             jvmAdapter().ldc(closure.getArity().getValue());
 
             jvmAdapter().invokespecial(p(CompiledIRBlockBody.class), "<init>", sig(void.class, java.lang.invoke.MethodHandle.class, IRScope.class, int.class));
         }
 
         { // prepare binding
             jvmMethod().loadContext();
             visit(closure.getSelf());
             jvmLoadLocal(DYNAMIC_SCOPE);
             jvmAdapter().invokevirtual(p(ThreadContext.class), "currentBinding", sig(Binding.class, IRubyObject.class, DynamicScope.class));
         }
 
         jvmAdapter().invokespecial(p(Block.class), "<init>", sig(void.class, BlockBody.class, Binding.class));
     }
 
     private SkinnyMethodAdapter jvmAdapter() {
         return jvmMethod().adapter;
     }
 
     private IRBytecodeAdapter jvmMethod() {
         return jvm.method();
     }
 
     private JVM jvm;
     private int methodIndex;
     private Map<String, IRScope> scopeMap;
 }
diff --git a/core/src/main/java/org/jruby/ir/targets/SymbolObjectSite.java b/core/src/main/java/org/jruby/ir/targets/SymbolObjectSite.java
index efa6268d3a..b85d19dd65 100644
--- a/core/src/main/java/org/jruby/ir/targets/SymbolObjectSite.java
+++ b/core/src/main/java/org/jruby/ir/targets/SymbolObjectSite.java
@@ -1,36 +1,43 @@
 package org.jruby.ir.targets;
 
+import org.jcodings.Encoding;
+import org.jruby.RubySymbol;
+import org.jruby.ir.runtime.IRRuntimeHelpers;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.objectweb.asm.Handle;
 import org.objectweb.asm.Opcodes;
 
 import java.lang.invoke.CallSite;
 import java.lang.invoke.MethodHandles;
 import java.lang.invoke.MethodType;
 
 import static org.jruby.util.CodegenUtils.p;
 import static org.jruby.util.CodegenUtils.sig;
 
 /**
 * Created by headius on 10/23/14.
 */
 public class SymbolObjectSite extends LazyObjectSite {
     private final String value;
+    private final String encoding;
 
-    public SymbolObjectSite(MethodType type, String value) {
+    public SymbolObjectSite(MethodType type, String value, String encoding) {
         super(type);
 
         this.value = value;
+        this.encoding = encoding;
     }
 
-    public static final Handle BOOTSTRAP = new Handle(Opcodes.H_INVOKESTATIC, p(SymbolObjectSite.class), "bootstrap", sig(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class));
+    public static final Handle BOOTSTRAP = new Handle(Opcodes.H_INVOKESTATIC, p(SymbolObjectSite.class), "bootstrap",
+            sig(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, String.class));
 
-    public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type, String value) {
-        return new SymbolObjectSite(type, value).bootstrap(lookup);
+    public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type, String value, String encoding) {
+        return new SymbolObjectSite(type, value, encoding).bootstrap(lookup);
     }
 
     public IRubyObject construct(ThreadContext context) {
-        return context.runtime.newSymbol(value);
+        return RubySymbol.newSymbol(context.runtime, value,
+                IRRuntimeHelpers.retrieveJCodingsEncoding(context, encoding));
     }
 }
diff --git a/core/src/main/java/org/jruby/parser/ParserSupport.java b/core/src/main/java/org/jruby/parser/ParserSupport.java
index 644c5013ec..8a22bd1243 100644
--- a/core/src/main/java/org/jruby/parser/ParserSupport.java
+++ b/core/src/main/java/org/jruby/parser/ParserSupport.java
@@ -1,1453 +1,1451 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
  * Version: EPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Eclipse Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/epl-v10.html
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
  * Copyright (C) 2006-2007 Mirko Stocker <me@misto.ch>
  * Copyright (C) 2006 Thomas Corbat <tcorbat@hsr.ch>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the EPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the EPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.parser;
 
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.List;
 import org.jcodings.Encoding;
 import org.jruby.RubyBignum;
 import org.jruby.RubyRegexp;
 import org.jruby.ast.*;
 import org.jruby.ast.types.ILiteralNode;
 import org.jruby.ast.types.INameNode;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.ISourcePositionHolder;
 import org.jruby.lexer.yacc.RubyLexer;
 import org.jruby.lexer.yacc.SyntaxException;
 import org.jruby.lexer.yacc.SyntaxException.PID;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.util.ByteList;
 import org.jruby.util.RegexpOptions;
 import org.jruby.util.StringSupport;
 import org.jruby.util.cli.Options;
 
 /** 
  *
  */
 public class ParserSupport {
     // Parser states:
     protected StaticScope currentScope;
 
     protected RubyLexer lexer;
     
     // Is the parser current within a singleton (value is number of nested singletons)
     private int inSingleton;
     
     // Is the parser currently within a method definition
     private boolean inDefinition;
 
     protected IRubyWarnings warnings;
 
     protected ParserConfiguration configuration;
     private RubyParserResult result;
 
     public void reset() {
         inSingleton = 0;
         inDefinition = false;
     }
     
     public StaticScope getCurrentScope() {
         return currentScope;
     }
     
     public ParserConfiguration getConfiguration() {
         return configuration;
     }
     
     public void popCurrentScope() {
         currentScope = currentScope.getEnclosingScope();
     }
     
     public void pushBlockScope() {
         currentScope = configuration.getRuntime().getStaticScopeFactory().newBlockScope(currentScope);
     }
     
     public void pushLocalScope() {
         currentScope = configuration.getRuntime().getStaticScopeFactory().newLocalScope(currentScope);
     }
     
     public Node arg_concat(ISourcePosition position, Node node1, Node node2) {
         return node2 == null ? node1 : new ArgsCatNode(position, node1, node2);
     }
 
     public Node arg_blk_pass(Node firstNode, BlockPassNode secondNode) {
         if (secondNode != null) {
             secondNode.setArgsNode(firstNode);
             return secondNode;
         }
         return firstNode;
     }
 
     /**
      * We know for callers of this that it cannot be any of the specials checked in gettable.
      * 
      * @param node to check its variable type
      * @return an AST node representing this new variable
      */
     public Node gettable2(Node node) {
         switch (node.getNodeType()) {
         case DASGNNODE: // LOCALVAR
         case LOCALASGNNODE:
             return currentScope.declare(node.getPosition(), ((INameNode) node).getName());
         case CONSTDECLNODE: // CONSTANT
             return new ConstNode(node.getPosition(), ((INameNode) node).getName());
         case INSTASGNNODE: // INSTANCE VARIABLE
             return new InstVarNode(node.getPosition(), ((INameNode) node).getName());
         case CLASSVARDECLNODE:
         case CLASSVARASGNNODE:
             return new ClassVarNode(node.getPosition(), ((INameNode) node).getName());
         case GLOBALASGNNODE:
             return new GlobalVarNode(node.getPosition(), ((INameNode) node).getName());
         }
 
         getterIdentifierError(node.getPosition(), ((INameNode) node).getName());
         return null;
     }
 
     public Node declareIdentifier(String name) {
         return currentScope.declare(lexer.tokline, name);
     }
 
     // We know it has to be tLABEL or tIDENTIFIER so none of the other assignable logic is needed
     public AssignableNode assignableLabelOrIdentifier(String name, Node value) {
         return currentScope.assign(lexer.getPosition(), name, makeNullNil(value));
     }
 
     // Only calls via f_kw so we know it has to be tLABEL
     public AssignableNode assignableLabel(String name, Node value) {
         return currentScope.assign(lexer.getPosition(), name, makeNullNil(value));
     }
     
     protected void getterIdentifierError(ISourcePosition position, String identifier) {
         throw new SyntaxException(PID.BAD_IDENTIFIER, position, "identifier " +
                 identifier + " is not valid to get", identifier);
     }
 
     /**
      *  Wraps node with NEWLINE node.
      *
      *@param node
      *@return a NewlineNode or null if node is null.
      */
     public Node newline_node(Node node, ISourcePosition position) {
         if (node == null) return null;
 
         configuration.coverLine(position.getLine());
         
         return node instanceof NewlineNode ? node : new NewlineNode(position, node); 
     }
     
     public Node addRootNode(Node topOfAST) {
         if (result.getBeginNodes().isEmpty()) {
             ISourcePosition position;
             if (topOfAST == null) {
                 topOfAST = NilImplicitNode.NIL;
                 position = lexer.getPosition();
             } else {
                 position = topOfAST.getPosition();
             }
             
             return new RootNode(position, result.getScope(), topOfAST);
         }
 
         ISourcePosition position = topOfAST != null ? topOfAST.getPosition() : result.getBeginNodes().get(0).getPosition();
         BlockNode newTopOfAST = new BlockNode(position);
         for (Node beginNode: result.getBeginNodes()) {
             appendToBlock(newTopOfAST, beginNode);
         }
         
         // Add real top to new top (unless this top is empty [only begin/end nodes or truly empty])
         if (topOfAST != null) newTopOfAST.add(topOfAST);
         
         return new RootNode(position, result.getScope(), newTopOfAST);
     }
     
     /* MRI: block_append */
     public Node appendToBlock(Node head, Node tail) {
         if (tail == null) return head;
         if (head == null) return tail;
 
         // Reduces overhead in interp by not set position every single line we encounter.
         head = compactNewlines(head);
 
         if (!(head instanceof BlockNode)) {
             head = new BlockNode(head.getPosition()).add(head);
         }
 
         if (warnings.isVerbose() && isBreakStatement(((ListNode) head).getLast()) && Options.PARSER_WARN_NOT_REACHED.load()) {
             warnings.warning(ID.STATEMENT_NOT_REACHED, tail.getPosition(), "Statement not reached.");
         }
 
         // Assumption: tail is never a list node
         ((ListNode) head).addAll(tail);
         return head;
     }
 
     public Node getOperatorCallNode(Node firstNode, String operator) {
         checkExpression(firstNode);
 
         return new CallNode(firstNode.getPosition(), firstNode, operator, null, null);
     }
     
     public Node getOperatorCallNode(Node firstNode, String operator, Node secondNode) {
         return getOperatorCallNode(firstNode, operator, secondNode, null);
     }
 
     public Node getOperatorCallNode(Node firstNode, String operator, Node secondNode, ISourcePosition defaultPosition) {
         if (defaultPosition != null) {
             firstNode = checkForNilNode(firstNode, defaultPosition);
             secondNode = checkForNilNode(secondNode, defaultPosition);
         }
         
         checkExpression(firstNode);
         checkExpression(secondNode);
 
         return new CallNode(firstNode.getPosition(), firstNode, operator, new ArrayNode(secondNode.getPosition(), secondNode), null);
     }
 
     public Node getMatchNode(Node firstNode, Node secondNode) {
         if (firstNode instanceof DRegexpNode) {
             return new Match2Node(firstNode.getPosition(), firstNode, secondNode);
         } else if (firstNode instanceof RegexpNode) {
             List<Integer> locals = allocateNamedLocals((RegexpNode) firstNode);
 
             if (locals.size() > 0) {
                 int[] primitiveLocals = new int[locals.size()];
                 for (int i = 0; i < primitiveLocals.length; i++) {
                     primitiveLocals[i] = locals.get(i);
                 }
                 return new Match2CaptureNode(firstNode.getPosition(), firstNode, secondNode, primitiveLocals);
             } else {
                 return new Match2Node(firstNode.getPosition(), firstNode, secondNode);
             }
         } else if (secondNode instanceof DRegexpNode || secondNode instanceof RegexpNode) {
             return new Match3Node(firstNode.getPosition(), secondNode, firstNode);
         }
 
         return getOperatorCallNode(firstNode, "=~", secondNode);
     }
 
     /**
      * Define an array set condition so we can return lhs
      * 
      * @param receiver array being set
      * @param index node which should evalute to index of array set
      * @return an AttrAssignNode
      */
     public Node aryset(Node receiver, Node index) {
         checkExpression(receiver);
 
         return new_attrassign(receiver.getPosition(), receiver, "[]=", index);
     }
 
     /**
      * Define an attribute set condition so we can return lhs
      * 
      * @param receiver object which contains attribute
      * @param name of the attribute being set
      * @return an AttrAssignNode
      */
     public Node attrset(Node receiver, String name) {
         checkExpression(receiver);
 
         return new_attrassign(receiver.getPosition(), receiver, name + "=", null);
     }
 
     public void backrefAssignError(Node node) {
         if (node instanceof NthRefNode) {
             String varName = "$" + ((NthRefNode) node).getMatchNumber();
             throw new SyntaxException(PID.INVALID_ASSIGNMENT, node.getPosition(), 
                     "Can't set variable " + varName + '.', varName);
         } else if (node instanceof BackRefNode) {
             String varName = "$" + ((BackRefNode) node).getType();
             throw new SyntaxException(PID.INVALID_ASSIGNMENT, node.getPosition(), "Can't set variable " + varName + '.', varName);
         }
     }
 
     public Node arg_add(ISourcePosition position, Node node1, Node node2) {
         if (node1 == null) {
             if (node2 == null) {
                 return new ArrayNode(position, NilImplicitNode.NIL);
             } else {
                 return new ArrayNode(node2.getPosition(), node2);
             }
         }
         if (node1 instanceof ArrayNode) return ((ArrayNode) node1).add(node2);
         
         return new ArgsPushNode(position, node1, node2);
     }
     
 	/**
 	 * @fixme position
 	 **/
     public Node node_assign(Node lhs, Node rhs) {
         if (lhs == null) return null;
 
         Node newNode = lhs;
 
         checkExpression(rhs);
         if (lhs instanceof AssignableNode) {
     	    ((AssignableNode) lhs).setValueNode(rhs);
         } else if (lhs instanceof IArgumentNode) {
             IArgumentNode invokableNode = (IArgumentNode) lhs;
             
             return invokableNode.setArgsNode(arg_add(lhs.getPosition(), invokableNode.getArgsNode(), rhs));
         }
         
         return newNode;
     }
     
     public Node ret_args(Node node, ISourcePosition position) {
         if (node != null) {
             if (node instanceof BlockPassNode) {
                 throw new SyntaxException(PID.BLOCK_ARG_UNEXPECTED, position,
                         lexer.getCurrentLine(), "block argument should not be given");
             } else if (node instanceof ArrayNode && ((ArrayNode)node).size() == 1) {
                 node = ((ArrayNode)node).get(0);
             } else if (node instanceof SplatNode) {
                 node = newSValueNode(position, node);
             }
         }
         
         return node;
     }
 
     /**
      * Is the supplied node a break/control statement?
      * 
      * @param node to be checked
      * @return true if a control node, false otherwise
      */
     public boolean isBreakStatement(Node node) {
         breakLoop: do {
             if (node == null) return false;
 
             switch (node.getNodeType()) {
             case NEWLINENODE:
                 node = ((NewlineNode) node).getNextNode();
                 continue breakLoop;
             case BREAKNODE: case NEXTNODE: case REDONODE:
             case RETRYNODE: case RETURNNODE:
                 return true;
             default:
                 return false;
             }
         } while (true);                    
     }
     
     public void warnUnlessEOption(ID id, Node node, String message) {
         if (!configuration.isInlineSource()) {
             warnings.warn(id, node.getPosition(), message);
         }
     }
 
     public void warningUnlessEOption(ID id, Node node, String message) {
         if (warnings.isVerbose() && !configuration.isInlineSource()) {
             warnings.warning(id, node.getPosition(), message);
         }
     }
 
     private Node compactNewlines(Node head) {
         while (head instanceof NewlineNode) {
             Node nextNode = ((NewlineNode) head).getNextNode();
 
             if (!(nextNode instanceof NewlineNode)) {
                 break;
             }
             head = nextNode;
         }
         return head;
     }
 
     // logical equivalent to value_expr in MRI
     public boolean checkExpression(Node node) {
         boolean conditional = false;
 
         while (node != null) {
             switch (node.getNodeType()) {
             case RETURNNODE: case BREAKNODE: case NEXTNODE: case REDONODE:
             case RETRYNODE:
                 if (!conditional) {
                     throw new SyntaxException(PID.VOID_VALUE_EXPRESSION,
                             node.getPosition(), lexer.getCurrentLine(),
                             "void value expression");
                 }
                 return false;
             case BLOCKNODE:
                 node = ((BlockNode) node).getLast();
                 break;
             case BEGINNODE:
                 node = ((BeginNode) node).getBodyNode();
                 break;
             case IFNODE:
                 if (!checkExpression(((IfNode) node).getThenBody())) return false;
                 node = ((IfNode) node).getElseBody();
                 break;
             case ANDNODE: case ORNODE:
                 conditional = true;
                 node = ((BinaryOperatorNode) node).getSecondNode();
                 break;
             case NEWLINENODE:
                 node = ((NewlineNode) node).getNextNode();
                 break;
             default: // Node
                 return true;
             }
         }
 
         return true;
     }
     
     /**
      * Is this a literal in the sense that MRI has a NODE_LIT for.  This is different than
      * ILiteralNode.  We should pick a different name since ILiteralNode is something we created
      * which is similiar but used for a slightly different condition (can I do singleton things).
      * 
      * @param node to be tested
      * @return true if it is a literal
      */
     public boolean isLiteral(Node node) {
         return node != null && (node instanceof FixnumNode || node instanceof BignumNode || 
                 node instanceof FloatNode || node instanceof SymbolNode || 
                 (node instanceof RegexpNode && ((RegexpNode) node).getOptions().toJoniOptions() == 0));
     }
 
     private void handleUselessWarn(Node node, String useless) {
         if (Options.PARSER_WARN_USELESSS_USE_OF.load()) {
             warnings.warn(ID.USELESS_EXPRESSION, node.getPosition(), "Useless use of " + useless + " in void context.");
         }
     }
 
     /**
      * Check to see if current node is an useless statement.  If useless a warning if printed.
      * 
      * @param node to be checked.
      */
     public void checkUselessStatement(Node node) {
         if (!warnings.isVerbose() || (!configuration.isInlineSource() && configuration.isEvalParse())) return;
         
         uselessLoop: do {
             if (node == null) return;
             
             switch (node.getNodeType()) {
             case NEWLINENODE:
                 node = ((NewlineNode) node).getNextNode();
                 continue uselessLoop;
             case CALLNODE: {
                 String name = ((CallNode) node).getName();
                 
                 if (name == "+" || name == "-" || name == "*" || name == "/" || name == "%" || 
                     name == "**" || name == "+@" || name == "-@" || name == "|" || name == "^" || 
                     name == "&" || name == "<=>" || name == ">" || name == ">=" || name == "<" || 
                     name == "<=" || name == "==" || name == "!=") {
                     handleUselessWarn(node, name);
                 }
                 return;
             }
             case BACKREFNODE: case DVARNODE: case GLOBALVARNODE:
             case LOCALVARNODE: case NTHREFNODE: case CLASSVARNODE:
             case INSTVARNODE:
                 handleUselessWarn(node, "a variable"); return;
             // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
             /*case CONSTNODE:
                 handleUselessWarn(node, "a constant"); return;*/
             case BIGNUMNODE: case DREGEXPNODE: case DSTRNODE: case DSYMBOLNODE:
             case FIXNUMNODE: case FLOATNODE: case REGEXPNODE:
             case STRNODE: case SYMBOLNODE:
                 handleUselessWarn(node, "a literal"); return;
             // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
             /*case CLASSNODE: case COLON2NODE:
                 handleUselessWarn(node, "::"); return;*/
             case DOTNODE:
                 handleUselessWarn(node, ((DotNode) node).isExclusive() ? "..." : ".."); return;
             case DEFINEDNODE:
                 handleUselessWarn(node, "defined?"); return;
             case FALSENODE:
                 handleUselessWarn(node, "false"); return;
             case NILNODE: 
                 handleUselessWarn(node, "nil"); return;
             // FIXME: Temporarily disabling because this fires way too much running Rails tests. JRUBY-518
             /*case SELFNODE:
                 handleUselessWarn(node, "self"); return;*/
             case TRUENODE:
                 handleUselessWarn(node, "true"); return;
             default: return;
             }
         } while (true);
     }
 
     /**
      * Check all nodes but the last one in a BlockNode for useless (void context) statements.
      * 
      * @param blockNode to be checked.
      */
     public void checkUselessStatements(BlockNode blockNode) {
         if (warnings.isVerbose()) {
             Node lastNode = blockNode.getLast();
 
             for (int i = 0; i < blockNode.size(); i++) {
                 Node currentNode = blockNode.get(i);
         		
                 if (lastNode != currentNode ) {
                     checkUselessStatement(currentNode);
                 }
             }
         }
     }
 
 	/**
      * assign_in_cond
 	 **/
     private boolean checkAssignmentInCondition(Node node) {
         if (node instanceof MultipleAsgnNode) {
             throw new SyntaxException(PID.MULTIPLE_ASSIGNMENT_IN_CONDITIONAL, node.getPosition(),
                     lexer.getCurrentLine(), "Multiple assignment in conditional.");
         } else if (node instanceof LocalAsgnNode || node instanceof DAsgnNode || node instanceof GlobalAsgnNode || node instanceof InstAsgnNode) {
             Node valueNode = ((AssignableNode) node).getValueNode();
             if (valueNode instanceof ILiteralNode || valueNode instanceof NilNode || valueNode instanceof TrueNode || valueNode instanceof FalseNode) {
                 warnings.warn(ID.ASSIGNMENT_IN_CONDITIONAL, node.getPosition(), "Found '=' in conditional, should be '=='.");
             }
             return true;
         } 
 
         return false;
     }
     
     protected Node makeNullNil(Node node) {
         return node == null ? NilImplicitNode.NIL : node;
     }
 
     private Node cond0(Node node) {
         checkAssignmentInCondition(node);
         
         Node leftNode = null;
         Node rightNode = null;
 
         // FIXME: DSTR,EVSTR,STR: warning "string literal in condition"
         switch(node.getNodeType()) {
         case DREGEXPNODE: {
             ISourcePosition position = node.getPosition();
 
             return new Match2Node(position, node, new GlobalVarNode(position, "$_"));
         }
         case ANDNODE:
             leftNode = cond0(((AndNode) node).getFirstNode());
             rightNode = cond0(((AndNode) node).getSecondNode());
             
             return new AndNode(node.getPosition(), makeNullNil(leftNode), makeNullNil(rightNode));
         case ORNODE:
             leftNode = cond0(((OrNode) node).getFirstNode());
             rightNode = cond0(((OrNode) node).getSecondNode());
             
             return new OrNode(node.getPosition(), makeNullNil(leftNode), makeNullNil(rightNode));
         case DOTNODE: {
             DotNode dotNode = (DotNode) node;
             if (dotNode.isLiteral()) return node; 
             
             String label = String.valueOf("FLIP" + node.hashCode());
             currentScope.getLocalScope().addVariable(label);
             int slot = currentScope.isDefined(label);
             
             return new FlipNode(node.getPosition(),
                     getFlipConditionNode(((DotNode) node).getBeginNode()),
                     getFlipConditionNode(((DotNode) node).getEndNode()),
                     dotNode.isExclusive(), slot);
         }
         case REGEXPNODE:
             if (Options.PARSER_WARN_REGEX_CONDITION.load()) {
                 warningUnlessEOption(ID.REGEXP_LITERAL_IN_CONDITION, node, "regex literal in condition");
             }
             
             return new MatchNode(node.getPosition(), node);
         }
 
         return node;
     }
 
     public Node getConditionNode(Node node) {
         if (node == null) return NilImplicitNode.NIL;
 
         if (node instanceof NewlineNode) {
             return new NewlineNode(node.getPosition(), cond0(((NewlineNode) node).getNextNode()));
         } 
 
         return cond0(node);
     }
 
     /* MRI: range_op */
     private Node getFlipConditionNode(Node node) {
         if (!configuration.isInlineSource()) return node;
         
         node = getConditionNode(node);
 
         if (node instanceof NewlineNode) return ((NewlineNode) node).getNextNode();
         
         if (node instanceof FixnumNode) {
             warnUnlessEOption(ID.LITERAL_IN_CONDITIONAL_RANGE, node, "integer literal in conditional range");
             return getOperatorCallNode(node, "==", new GlobalVarNode(node.getPosition(), "$."));
         } 
 
         return node;
     }
 
     public SValueNode newSValueNode(ISourcePosition position, Node node) {
         return new SValueNode(position, node);
     }
     
     public SplatNode newSplatNode(ISourcePosition position, Node node) {
         return new SplatNode(position, makeNullNil(node));
     }
     
     public ArrayNode newArrayNode(ISourcePosition position, Node firstNode) {
         return new ArrayNode(position, makeNullNil(firstNode));
     }
 
     public ISourcePosition position(ISourcePositionHolder one, ISourcePositionHolder two) {
         return one == null ? two.getPosition() : one.getPosition();
     }
 
     public AndNode newAndNode(ISourcePosition position, Node left, Node right) {
         checkExpression(left);
         
         if (left == null && right == null) return new AndNode(position, makeNullNil(left), makeNullNil(right));
         
         return new AndNode(position(left, right), makeNullNil(left), makeNullNil(right));
     }
 
     public OrNode newOrNode(ISourcePosition position, Node left, Node right) {
         checkExpression(left);
 
         if (left == null && right == null) return new OrNode(position, makeNullNil(left), makeNullNil(right));
         
         return new OrNode(position(left, right), makeNullNil(left), makeNullNil(right));
     }
 
     /**
      * Ok I admit that this is somewhat ugly.  We post-process a chain of when nodes and analyze
      * them to re-insert them back into our new CaseNode the way we want.  The grammar is being
      * difficult and until I go back into the depths of that this is where things are.
      *
      * @param expression of the case node (e.g. case foo)
      * @param firstWhenNode first when (which could also be the else)
      * @return a new case node
      */
     public CaseNode newCaseNode(ISourcePosition position, Node expression, Node firstWhenNode) {
         ArrayNode cases = new ArrayNode(firstWhenNode != null ? firstWhenNode.getPosition() : position);
         CaseNode caseNode = new CaseNode(position, expression, cases);
 
         for (Node current = firstWhenNode; current != null; current = ((WhenNode) current).getNextCase()) {
             if (current instanceof WhenOneArgNode) {
                 cases.add(current);
             } else if (current instanceof WhenNode) {
                 simplifyMultipleArgumentWhenNodes((WhenNode) current, cases);
             } else {
                 caseNode.setElseNode(current);
                 break;
             }
         }
 
         return caseNode;
     }
 
     /*
      * This method exists for us to break up multiple expression when nodes (e.g. when 1,2,3:)
      * into individual whenNodes.  The primary reason for this is to ensure lazy evaluation of
      * the arguments (when foo,bar,gar:) to prevent side-effects.  In the old code this was done
      * using nested when statements, which was awful for interpreter and compilation.
      *
      * Notes: This has semantic equivalence but will not be lexically equivalent.  Compiler
      * needs to detect same bodies to simplify bytecode generated.
      */
     private void simplifyMultipleArgumentWhenNodes(WhenNode sourceWhen, ArrayNode cases) {
         Node expressionNodes = sourceWhen.getExpressionNodes();
 
         if (expressionNodes instanceof SplatNode || expressionNodes instanceof ArgsCatNode) {
             cases.add(sourceWhen);
             return;
         }
 
         if (expressionNodes instanceof ListNode) {
             ListNode list = (ListNode) expressionNodes;
             ISourcePosition position = sourceWhen.getPosition();
             Node bodyNode = sourceWhen.getBodyNode();
 
             for (int i = 0; i < list.size(); i++) {
                 Node expression = list.get(i);
 
                 if (expression instanceof SplatNode || expression instanceof ArgsCatNode) {
                     cases.add(new WhenNode(position, expression, bodyNode, null));
                 } else {
                     cases.add(new WhenOneArgNode(position, expression, bodyNode, null));
                 }
             }
         } else {
             cases.add(sourceWhen);
         }
     }
     
     public WhenNode newWhenNode(ISourcePosition position, Node expressionNodes, Node bodyNode, Node nextCase) {
         if (bodyNode == null) bodyNode = NilImplicitNode.NIL;
 
         if (expressionNodes instanceof SplatNode || expressionNodes instanceof ArgsCatNode || expressionNodes instanceof ArgsPushNode) {
             return new WhenNode(position, expressionNodes, bodyNode, nextCase);
         }
 
         ListNode list = (ListNode) expressionNodes;
 
         if (list.size() == 1) {
             Node element = list.get(0);
             
             if (!(element instanceof SplatNode)) {
                 return new WhenOneArgNode(position, element, bodyNode, nextCase);
             }
         }
 
         return new WhenNode(position, expressionNodes, bodyNode, nextCase);
     }
 
     // FIXME: Currently this is passing in position of receiver
     public Node new_opElementAsgnNode(Node receiverNode, String operatorName, Node argsNode, Node valueNode) {
         ISourcePosition position = lexer.tokline;  // FIXME: ruby_sourceline in new lexer.
         Node newNode = null;
 
         if (argsNode instanceof ArrayNode) {
             ArrayNode array = (ArrayNode) argsNode;
 
             if (array.size() == 1) {
                 if (operatorName.equals("||")) {
                     newNode = new OpElementOneArgOrAsgnNode(position, receiverNode, operatorName, array, valueNode);
                 } else if (operatorName.equals("&&")) {
 
                     newNode = new OpElementOneArgAndAsgnNode(position, receiverNode, operatorName, array, valueNode);
                 } else {
                     newNode = new OpElementOneArgAsgnNode(position, receiverNode, operatorName, array, valueNode);
                 }
             }
         }
 
         if (newNode == null) {
             newNode = new OpElementAsgnNode(position, receiverNode, operatorName, argsNode, valueNode);
         }
 
         fixpos(newNode, receiverNode);
 
         return newNode;
     }
     
     public Node new_attrassign(ISourcePosition position, Node receiver, String name, Node args) {
         if (!(args instanceof ArrayNode)) return new AttrAssignNode(position, receiver, name, args);
         
         ArrayNode argsNode = (ArrayNode) args;
         
         switch (argsNode.size()) {
             case 1:
                 return new AttrAssignOneArgNode(position, receiver, name, argsNode);
             case 2:
                 return new AttrAssignTwoArgNode(position, receiver, name, argsNode);
             case 3:
                 return new AttrAssignThreeArgNode(position, receiver, name, argsNode);
             default:
                 return new AttrAssignNode(position, receiver, name, argsNode);
         }
     }
     
     private boolean isNumericOperator(String name) {
         if (name.length() == 1) {
             switch (name.charAt(0)) {
                 case '+': case '-': case '*': case '/': case '<': case '>':
                     return true;
             }
         } else if (name.length() == 2) {
             switch (name.charAt(0)) {
             case '<': case '>': case '=':
                 switch (name.charAt(1)) {
                 case '=': case '<':
                     return true;
                 }
             }
         }
         
         return false;
     }
 
     public Node new_call(Node receiver, String name, Node argsNode, Node iter) {
         if (argsNode instanceof BlockPassNode) {
             if (iter != null) {
                 throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, iter.getPosition(),
                         lexer.getCurrentLine(), "Both block arg and actual block given.");
             }
 
             BlockPassNode blockPass = (BlockPassNode) argsNode;
             return new CallNode(position(receiver, argsNode), receiver, name, blockPass.getArgsNode(), blockPass);
         }
 
         return new CallNode(position(receiver, argsNode), receiver, name, argsNode, iter);
     }
 
     public Colon2Node new_colon2(ISourcePosition position, Node leftNode, String name) {
         if (leftNode == null) return new Colon2ImplicitNode(position, name);
 
         return new Colon2ConstNode(position, leftNode, name);
     }
 
     public Colon3Node new_colon3(ISourcePosition position, String name) {
         return new Colon3Node(position, name);
     }
 
     public void frobnicate_fcall_args(FCallNode fcall, Node args, Node iter) {
         if (args instanceof BlockPassNode) {
             if (iter != null) {
                 throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, iter.getPosition(),
                         lexer.getCurrentLine(), "Both block arg and actual block given.");
             }
 
             BlockPassNode blockPass = (BlockPassNode) args;
             args = blockPass.getArgsNode();
             iter = blockPass;
         }
 
         fcall.setArgsNode(args);
         fcall.setIterNode(iter);
     }
 
     public void fixpos(Node node, Node orig) {
         if (node == null || orig == null) return;
 
         node.setPosition(orig.getPosition());
     }
 
     public Node new_fcall(String operation) {
         return new FCallNode(lexer.tokline, operation);
     }
 
     public Node new_super(ISourcePosition position, Node args) {
         if (args != null && args instanceof BlockPassNode) {
             return new SuperNode(position, ((BlockPassNode) args).getArgsNode(), args);
         }
         return new SuperNode(position, args);
     }
 
     /**
     *  Description of the RubyMethod
     */
     public void initTopLocalVariables() {
         DynamicScope scope = configuration.getScope(); 
         currentScope = scope.getStaticScope(); 
         
         result.setScope(scope);
     }
 
     /** Getter for property inSingle.
      * @return Value of property inSingle.
      */
     public boolean isInSingle() {
         return inSingleton != 0;
     }
 
     /** Setter for property inSingle.
      * @param inSingle New value of property inSingle.
      */
     public void setInSingle(int inSingle) {
         this.inSingleton = inSingle;
     }
 
     public boolean isInDef() {
         return inDefinition;
     }
 
     public void setInDef(boolean inDef) {
         this.inDefinition = inDef;
     }
 
     /** Getter for property inSingle.
      * @return Value of property inSingle.
      */
     public int getInSingle() {
         return inSingleton;
     }
 
     /**
      * Gets the result.
      * @return Returns a RubyParserResult
      */
     public RubyParserResult getResult() {
         return result;
     }
 
     /**
      * Sets the result.
      * @param result The result to set
      */
     public void setResult(RubyParserResult result) {
         this.result = result;
     }
 
     /**
      * Sets the configuration.
      * @param configuration The configuration to set
      */
     public void setConfiguration(ParserConfiguration configuration) {
         this.configuration = configuration;
     }
 
     public void setWarnings(IRubyWarnings warnings) {
         this.warnings = warnings;
     }
 
     public void setLexer(RubyLexer lexer) {
         this.lexer = lexer;
     }
 
     public DStrNode createDStrNode(ISourcePosition position) {
         return new DStrNode(position, lexer.getEncoding());
     }
         
     public Node asSymbol(ISourcePosition position, Node value) {
-        // FIXME: This might have an encoding issue since toString generally uses iso-8859-1
-        if (value instanceof StrNode) return new SymbolNode(position, ((StrNode) value).getValue().toString().intern());
-        
-        return new DSymbolNode(position, (DStrNode) value);
+        return value instanceof StrNode ? new SymbolNode(position, ((StrNode) value).getValue()) :
+                new DSymbolNode(position, (DStrNode) value);
     }
     
     public Node literal_concat(ISourcePosition position, Node head, Node tail) { 
         if (head == null) return tail;
         if (tail == null) return head;
         
         if (head instanceof EvStrNode) {
             head = createDStrNode(head.getPosition()).add(head);
         } 
 
         if (tail instanceof StrNode) {
             if (head instanceof StrNode) {
                 StrNode front = (StrNode) head;
                 // string_contents always makes an empty strnode...which is sometimes valid but
                 // never if it ever is in literal_concat.
                 if (front.getValue().getRealSize() > 0) {
                     return new StrNode(head.getPosition(), front, (StrNode) tail);
                 } else {
                     return tail;
                 }
             } 
             head.setPosition(head.getPosition());
             return ((ListNode) head).add(tail);
         	
         } else if (tail instanceof DStrNode) {
             if (head instanceof StrNode){
                 ((DStrNode)tail).prepend(head);
                 return tail;
             } 
 
             return ((ListNode) head).addAll(tail);
         } 
 
         // tail must be EvStrNode at this point 
         if (head instanceof StrNode) {
         	
             //Do not add an empty string node
             if(((StrNode) head).getValue().length() == 0) {
                 head = createDStrNode(head.getPosition());
             } else {
                 head = createDStrNode(head.getPosition()).add(head);
             }
         }
         return ((DStrNode) head).add(tail);
     }
     
     public Node newEvStrNode(ISourcePosition position, Node node) {
         Node head = node;
         while (true) {
             if (node == null) break;
             
             if (node instanceof StrNode || node instanceof DStrNode || node instanceof EvStrNode) {
                 return node;
             }
                 
             if (!(node instanceof NewlineNode)) break;
                 
             node = ((NewlineNode) node).getNextNode();
         }
         
         return new EvStrNode(position, head);
     }
     
     public Node new_yield(ISourcePosition position, Node node) {
         if (node != null && node instanceof BlockPassNode) {
             throw new SyntaxException(PID.BLOCK_ARG_UNEXPECTED, node.getPosition(),
                     lexer.getCurrentLine(), "Block argument should not be given.");
         }
 
         return new Yield19Node(position, node); 
     }
     
     public Node negateInteger(Node integerNode) {
         if (integerNode instanceof FixnumNode) {
             FixnumNode fixnumNode = (FixnumNode) integerNode;
             
             fixnumNode.setValue(-fixnumNode.getValue());
             return fixnumNode;
         } else if (integerNode instanceof BignumNode) {
             BignumNode bignumNode = (BignumNode) integerNode;
 
             BigInteger value = bignumNode.getValue().negate();
 
             // Negating a bignum will make the last negative value of our bignum
             if (value.compareTo(RubyBignum.LONG_MIN) >= 0) {
                 return new FixnumNode(bignumNode.getPosition(), value.longValue());
             }
             
             bignumNode.setValue(value);
         }
         
         return integerNode;
     }
     
     public FloatNode negateFloat(FloatNode floatNode) {
         floatNode.setValue(-floatNode.getValue());
         
         return floatNode;
     }
 
     public ComplexNode negateComplexNode(ComplexNode complexNode) {
         complexNode.setNumber(negateNumeric(complexNode.getPosition(), complexNode.getNumber()));
 
         return complexNode;
     }
 
     public RationalNode negateRational(RationalNode rationalNode) {
         return new RationalNode(rationalNode.getPosition(),
                                 -rationalNode.getNumerator(),
                                 rationalNode.getDenominator());
     }
     
     public Node unwrapNewlineNode(Node node) {
     	if(node instanceof NewlineNode) {
     		return ((NewlineNode) node).getNextNode();
     	}
     	return node;
     }
     
     private Node checkForNilNode(Node node, ISourcePosition defaultPosition) {
         return (node == null) ? new NilNode(defaultPosition) : node; 
     }
 
     public Node new_args(ISourcePosition position, ListNode pre, ListNode optional, RestArgNode rest,
             ListNode post, BlockArgNode block) {
         // Zero-Argument declaration
         if (optional == null && rest == null && post == null && block == null) {
             if (pre == null || pre.size() == 0) return new ArgsNoArgNode(position);
             if (pre.size() == 1 && !hasAssignableArgs(pre)) return new ArgsPreOneArgNode(position, pre);
             if (pre.size() == 2 && !hasAssignableArgs(pre)) return new ArgsPreTwoArgNode(position, pre);
         }
         return new ArgsNode(position, pre, optional, rest, post, block);
     }
     
     public Node new_args(ISourcePosition position, ListNode pre, ListNode optional, RestArgNode rest,
             ListNode post, ArgsTailHolder tail) {
         if (tail == null) return new_args(position, pre, optional, rest, post, (BlockArgNode) null);
         
         // Zero-Argument declaration
         if (optional == null && rest == null && post == null && !tail.hasKeywordArgs() && tail.getBlockArg() == null) {
             if (pre == null || pre.size() == 0) return new ArgsNoArgNode(position);
             if (pre.size() == 1 && !hasAssignableArgs(pre)) return new ArgsPreOneArgNode(position, pre);
             if (pre.size() == 2 && !hasAssignableArgs(pre)) return new ArgsPreTwoArgNode(position, pre);
         }
 
         return new ArgsNode(position, pre, optional, rest, post, 
                 tail.getKeywordArgs(), tail.getKeywordRestArgNode(), tail.getBlockArg());
     }
     
     public ArgsTailHolder new_args_tail(ISourcePosition position, ListNode keywordArg, 
             String keywordRestArgName, BlockArgNode blockArg) {
         if (keywordRestArgName == null) return new ArgsTailHolder(position, keywordArg, null, blockArg);
         
         String restKwargsName = keywordRestArgName;
 
         int slot = currentScope.exists(restKwargsName);
         if (slot == -1) slot = currentScope.addVariable(restKwargsName);
 
         KeywordRestArgNode keywordRestArg = new KeywordRestArgNode(position, restKwargsName, slot);
         
         return new ArgsTailHolder(position, keywordArg, keywordRestArg, blockArg);
     }    
 
     private boolean hasAssignableArgs(ListNode list) {
         for (int i = 0; i < list.size(); i++) {
             Node node = list.get(i);
             if (node instanceof AssignableNode) return true;
         }
         return false;
     }
 
     public Node newAlias(ISourcePosition position, Node newNode, Node oldNode) {
         return new AliasNode(position, newNode, oldNode);
     }
 
     public Node newUndef(ISourcePosition position, Node nameNode) {
         return new UndefNode(position, nameNode);
     }
 
     /**
      * generate parsing error
      */
     public void yyerror(String message) {
         throw new SyntaxException(PID.GRAMMAR_ERROR, lexer.getPosition(), lexer.getCurrentLine(), message);
     }
 
     /**
      * generate parsing error
      * @param message text to be displayed.
      * @param expected list of acceptable tokens, if available.
      */
     public void yyerror(String message, String[] expected, String found) {
         String text = message + ", unexpected " + found + "\n";
         throw new SyntaxException(PID.GRAMMAR_ERROR, lexer.getPosition(), lexer.getCurrentLine(), text, found);
     }
 
     public ISourcePosition getPosition(ISourcePositionHolder start) {
         return start != null ? lexer.getPosition(start.getPosition()) : lexer.getPosition();
     }
 
     public void warn(ID id, ISourcePosition position, String message, Object... data) {
         warnings.warn(id, position, message);
     }
 
     public void warning(ID id, ISourcePosition position, String message, Object... data) {
         if (warnings.isVerbose()) warnings.warning(id, position, message);
     }
 
     // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name) [1.9]
     public boolean is_local_id(String name) {
         return lexer.isIdentifierChar(name.charAt(0));
     }
 
     // 1.9
     public ListNode list_append(Node list, Node item) {
         if (list == null) return new ArrayNode(item.getPosition(), item);
         if (!(list instanceof ListNode)) return new ArrayNode(list.getPosition(), list).add(item);
 
         return ((ListNode) list).add(item);
     }
 
     // 1.9
     public Node new_bv(String identifier) {
         if (!is_local_id(identifier)) {
             getterIdentifierError(lexer.getPosition(), identifier);
         }
         shadowing_lvar(identifier);
         
         return arg_var(identifier);
     }
 
     // 1.9
     public ArgumentNode arg_var(String name) {
         StaticScope current = getCurrentScope();
 
         // Multiple _ arguments are allowed.  To not screw with tons of arity
         // issues in our runtime we will allocate unnamed bogus vars so things
         // still work. MRI does not use name as intern'd value so they don't
         // have this issue.
         if (name == "_") {
             int count = 0;
             while (current.exists(name) >= 0) {
                 name = "_$" + count++;
             }
         }
         
         return new ArgumentNode(lexer.getPosition(), name, current.addVariableThisScope(name));
     }
 
     public String formal_argument(String identifier) {
         if (!is_local_id(identifier)) yyerror("formal argument must be local variable");
 
         return shadowing_lvar(identifier);
     }
 
     // 1.9
     public String shadowing_lvar(String name) {
         if (name == "_") return name;
 
         StaticScope current = getCurrentScope();
         if (current.isBlockScope()) {
             if (current.exists(name) >= 0) yyerror("duplicated argument name");
 
             if (warnings.isVerbose() && current.isDefined(name) >= 0 && Options.PARSER_WARN_LOCAL_SHADOWING.load()) {
                 warnings.warning(ID.STATEMENT_NOT_REACHED, lexer.getPosition(),
                         "shadowing outer local variable - " + name);
             }
         } else if (current.exists(name) >= 0) {
             yyerror("duplicated argument name");
         }
 
         return name;
     }
 
     // 1.9
     public ListNode list_concat(Node first, Node second) {
         if (first instanceof ListNode) {
             if (second instanceof ListNode) {
                 return ((ListNode) first).addAll((ListNode) second);
             } else {
                 return ((ListNode) first).addAll(second);
             }
         }
 
         return new ArrayNode(first.getPosition(), first).add(second);
     }
 
     // 1.9
     /**
      * If node is a splat and it is splatting a literal array then return the literal array.
      * Otherwise return null.  This allows grammar to not splat into a Ruby Array if splatting
      * a literal array.
      */
     public Node splat_array(Node node) {
         if (node instanceof SplatNode) node = ((SplatNode) node).getValue();
         if (node instanceof ArrayNode) return node;
         return null;
     }
 
     // 1.9
     public Node arg_append(Node node1, Node node2) {
         if (node1 == null) return new ArrayNode(node2.getPosition(), node2);
         if (node1 instanceof ListNode) return ((ListNode) node1).add(node2);
         if (node1 instanceof BlockPassNode) return arg_append(((BlockPassNode) node1).getBodyNode(), node2);
         if (node1 instanceof ArgsPushNode) {
             ArgsPushNode pushNode = (ArgsPushNode) node1;
             Node body = pushNode.getSecondNode();
 
             return new ArgsCatNode(pushNode.getPosition(), pushNode.getFirstNode(),
                     new ArrayNode(body.getPosition(), body).add(node2));
         }
 
         return new ArgsPushNode(position(node1, node2), node1, node2);
     }
 
     // MRI: reg_fragment_check
     public void regexpFragmentCheck(RegexpNode end, ByteList value) {
         setRegexpEncoding(end, value);
         RubyRegexp.preprocessCheck(configuration.getRuntime(), value);
     }        // 1.9 mode overrides to do extra checking...
     private List<Integer> allocateNamedLocals(RegexpNode regexpNode) {
         RubyRegexp pattern = RubyRegexp.newRegexp(configuration.getRuntime(), regexpNode.getValue(), regexpNode.getOptions());
         pattern.setLiteral();
         String[] names = pattern.getNames();
         int length = names.length;
         List<Integer> locals = new ArrayList<Integer>();
         StaticScope scope = getCurrentScope();
 
         for (int i = 0; i < length; i++) {
             // TODO: Pass by non-local-varnamed things but make sure consistent with list we get from regexp
             
             if (RubyLexer.getKeyword(names[i]) == null) {
                 int slot = scope.isDefined(names[i]);
                 if (slot >= 0) {
                     locals.add(slot);
                 } else {
                     locals.add(getCurrentScope().addVariableThisScope(names[i]));
                 }
             }
         }
 
         return locals;
     }
 
     private boolean is7BitASCII(ByteList value) {
         return StringSupport.codeRangeScan(value.getEncoding(), value) == StringSupport.CR_7BIT;
     }
 
     // TODO: Put somewhere more consolidated (similiar
     private char optionsEncodingChar(Encoding optionEncoding) {
         if (optionEncoding == RubyLexer.USASCII_ENCODING) return 'n';
         if (optionEncoding == org.jcodings.specific.EUCJPEncoding.INSTANCE) return 'e';
         if (optionEncoding == org.jcodings.specific.SJISEncoding.INSTANCE) return 's';
         if (optionEncoding == RubyLexer.UTF8_ENCODING) return 'u';
 
         return ' ';
     }
 
     public void compile_error(String message) { // mri: rb_compile_error_with_enc
         String line = lexer.getCurrentLine();
         ISourcePosition position = lexer.getPosition();
         String errorMessage = position.getFile() + ":" + position.getLine() + ": ";
 
         if (line != null && line.length() > 5) {
             boolean addNewline = message != null && ! message.endsWith("\n");
 
             message += (addNewline ? "\n" : "") + line;
         }
 
         throw getConfiguration().getRuntime().newSyntaxError(errorMessage + message);
     }
 
     protected void compileError(Encoding optionEncoding, Encoding encoding) {
         throw new SyntaxException(PID.REGEXP_ENCODING_MISMATCH, lexer.getPosition(), lexer.getCurrentLine(),
                 "regexp encoding option '" + optionsEncodingChar(optionEncoding) +
                 "' differs from source encoding '" + encoding + "'");
     }
     
     // MRI: reg_fragment_setenc_gen
     public void setRegexpEncoding(RegexpNode end, ByteList value) {
         RegexpOptions options = end.getOptions();
         Encoding optionsEncoding = options.setup(configuration.getRuntime()) ;
 
         // Change encoding to one specified by regexp options as long as the string is compatible.
         if (optionsEncoding != null) {
             if (optionsEncoding != value.getEncoding() && !is7BitASCII(value)) {
                 compileError(optionsEncoding, value.getEncoding());
             }
 
             value.setEncoding(optionsEncoding);
         } else if (options.isEncodingNone()) {
             if (value.getEncoding() == RubyLexer.ASCII8BIT_ENCODING && !is7BitASCII(value)) {
                 compileError(optionsEncoding, value.getEncoding());
             }
             value.setEncoding(RubyLexer.ASCII8BIT_ENCODING);
         } else if (lexer.getEncoding() == RubyLexer.USASCII_ENCODING) {
             if (!is7BitASCII(value)) {
                 value.setEncoding(RubyLexer.USASCII_ENCODING); // This will raise later
             } else {
                 value.setEncoding(RubyLexer.ASCII8BIT_ENCODING);
             }
         }
     }    
 
     protected void checkRegexpSyntax(ByteList value, RegexpOptions options) {
         RubyRegexp.newRegexp(getConfiguration().getRuntime(), value, options);
     }
 
     public Node newRegexpNode(ISourcePosition position, Node contents, RegexpNode end) {
         RegexpOptions options = end.getOptions();
         Encoding encoding = lexer.getEncoding();
 
         if (contents == null) {
             ByteList newValue = ByteList.create("");
             if (encoding != null) {
                 newValue.setEncoding(encoding);
             }
 
             regexpFragmentCheck(end, newValue);
             return new RegexpNode(position, newValue, options.withoutOnce());
         } else if (contents instanceof StrNode) {
             ByteList meat = (ByteList) ((StrNode) contents).getValue().clone();
             regexpFragmentCheck(end, meat);
             checkRegexpSyntax(meat, options.withoutOnce());
             return new RegexpNode(contents.getPosition(), meat, options.withoutOnce());
         } else if (contents instanceof DStrNode) {
             DStrNode dStrNode = (DStrNode) contents;
             
             for (int i = 0; i < dStrNode.size(); i++) {
                 Node fragment = dStrNode.get(i);
                 if (fragment instanceof StrNode) {
                     ByteList frag = ((StrNode) fragment).getValue();
                     regexpFragmentCheck(end, frag);
 //                    if (!lexer.isOneEight()) encoding = frag.getEncoding();
                 }
             }
             
             dStrNode.prepend(new StrNode(contents.getPosition(), createMaster(options)));
 
             return new DRegexpNode(position, options, encoding).addAll(dStrNode);
         }
 
         // EvStrNode: #{val}: no fragment check, but at least set encoding
         ByteList master = createMaster(options);
         regexpFragmentCheck(end, master);
         encoding = master.getEncoding();
         DRegexpNode node = new DRegexpNode(position, options, encoding);
         node.add(new StrNode(contents.getPosition(), master));
         node.add(contents);
         return node;
     }
     
     // Create the magical empty 'master' string which will be encoded with
     // regexp options encoding so dregexps can end up starting with the
     // right encoding.
     private ByteList createMaster(RegexpOptions options) {
         Encoding encoding = options.setup(configuration.getRuntime());
 
         return new ByteList(new byte[] {}, encoding);
     }
     
     // FIXME:  This logic is used by many methods in MRI, but we are only using it in lexer
     // currently.  Consolidate this when we tackle a big encoding refactoring
     public static int associateEncoding(ByteList buffer, Encoding newEncoding, int codeRange) {
         Encoding bufferEncoding = buffer.getEncoding();
                 
         if (newEncoding == bufferEncoding) return codeRange;
         
         // TODO: Special const error
         
         buffer.setEncoding(newEncoding);
         
         if (codeRange != StringSupport.CR_7BIT || !newEncoding.isAsciiCompatible()) {
             return StringSupport.CR_UNKNOWN;
         }
         
         return codeRange;
     }
     
     public KeywordArgNode keyword_arg(ISourcePosition position, AssignableNode assignable) {
         return new KeywordArgNode(position, assignable);
     }
     
     public Node negateNumeric(ISourcePosition position, Node node) {
         switch (node.getNodeType()) {
             case FIXNUMNODE:
             case BIGNUMNODE:
                 return negateInteger(node);
             case COMPLEXNODE:
                 return negateComplexNode((ComplexNode) node);
             case FLOATNODE:
                 return negateFloat((FloatNode) node);
             case RATIONALNODE:
                 return negateRational((RationalNode) node);
         }
         
         yyerror("Invalid or unimplemented numeric to negate: " + node.toString());
         return null;
     }
     
     public Node new_defined(ISourcePosition position, Node something) {
         return new DefinedNode(position, something);
     }
     
     public String internalId() {
         return "";
     }
 }
diff --git a/core/src/main/java/org/jruby/parser/RubyParser.java b/core/src/main/java/org/jruby/parser/RubyParser.java
index afbe8b6195..40d081d3a9 100644
--- a/core/src/main/java/org/jruby/parser/RubyParser.java
+++ b/core/src/main/java/org/jruby/parser/RubyParser.java
@@ -3221,2002 +3221,2002 @@ states[280] = new ParserState() {
   }
 };
 states[281] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getCmdArgumentState().reset(((Long)yyVals[-1+yyTop]).longValue());
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[282] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BlockPassNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[283] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((BlockPassNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[285] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition pos = ((Node)yyVals[0+yyTop]) == null ? lexer.getPosition() : ((Node)yyVals[0+yyTop]).getPosition();
                     yyVal = support.newArrayNode(pos, ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[286] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newSplatNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[287] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node = support.splat_array(((Node)yyVals[-2+yyTop]));
 
                     if (node != null) {
                         yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                     } else {
                         yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                     }
     return yyVal;
   }
 };
 states[288] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node = null;
 
                     /* FIXME: lose syntactical elements here (and others like this)*/
                     if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                         (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                         yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                     } else {
                         yyVal = support.arg_concat(support.getPosition(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                     }
     return yyVal;
   }
 };
 states[289] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[290] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[291] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node = support.splat_array(((Node)yyVals[-2+yyTop]));
 
                     if (node != null) {
                         yyVal = support.list_append(node, ((Node)yyVals[0+yyTop]));
                     } else {
                         yyVal = support.arg_append(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
                     }
     return yyVal;
   }
 };
 states[292] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node = null;
 
                     if (((Node)yyVals[0+yyTop]) instanceof ArrayNode &&
                         (node = support.splat_array(((Node)yyVals[-3+yyTop]))) != null) {
                         yyVal = support.list_concat(node, ((Node)yyVals[0+yyTop]));
                     } else {
                         yyVal = support.arg_concat(((Node)yyVals[-3+yyTop]).getPosition(), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[0+yyTop]));
                     }
     return yyVal;
   }
 };
 states[293] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.newSplatNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[300] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((ListNode)yyVals[0+yyTop]); /* FIXME: Why complaining without $$ = $1;*/
     return yyVal;
   }
 };
 states[301] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((ListNode)yyVals[0+yyTop]); /* FIXME: Why complaining without $$ = $1;*/
     return yyVal;
   }
 };
 states[304] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_fcall(((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[305] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BeginNode(((ISourcePosition)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[306] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_ENDARG);
     return yyVal;
   }
 };
 states[307] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null; /*FIXME: Should be implicit nil?*/
     return yyVal;
   }
 };
 states[308] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_ENDARG); 
     return yyVal;
   }
 };
 states[309] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (Options.PARSER_WARN_GROUPED_EXPRESSIONS.load()) {
                       support.warning(ID.GROUPED_EXPRESSION, ((ISourcePosition)yyVals[-3+yyTop]), "(...) interpreted as grouped expression");
                     }
                     yyVal = ((Node)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
 states[310] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) != null) {
                         /* compstmt position includes both parens around it*/
                         ((ISourcePositionHolder) ((Node)yyVals[-1+yyTop])).setPosition(((ISourcePosition)yyVals[-2+yyTop]));
                         yyVal = ((Node)yyVals[-1+yyTop]);
                     } else {
                         yyVal = new NilNode(((ISourcePosition)yyVals[-2+yyTop]));
                     }
     return yyVal;
   }
 };
 states[311] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_colon2(support.getPosition(((Node)yyVals[-2+yyTop])), ((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[312] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_colon3(lexer.getPosition(), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[313] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition position = support.getPosition(((Node)yyVals[-1+yyTop]));
                     if (((Node)yyVals[-1+yyTop]) == null) {
                         yyVal = new ZArrayNode(position); /* zero length array */
                     } else {
                         yyVal = ((Node)yyVals[-1+yyTop]);
                     }
     return yyVal;
   }
 };
 states[314] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((HashNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[315] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ReturnNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[316] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_yield(((ISourcePosition)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[317] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZYieldNode(((ISourcePosition)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
 states[318] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZYieldNode(((ISourcePosition)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[319] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_defined(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[320] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(support.getConditionNode(((Node)yyVals[-1+yyTop])), "!");
     return yyVal;
   }
 };
 states[321] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
     return yyVal;
   }
 };
 states[322] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.frobnicate_fcall_args(((FCallNode)yyVals[-1+yyTop]), null, ((IterNode)yyVals[0+yyTop]));
                     yyVal = ((FCallNode)yyVals[-1+yyTop]);                    
     return yyVal;
   }
 };
 states[324] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) != null && 
                           ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     yyVal = ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                     ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[325] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((LambdaNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[326] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(((ISourcePosition)yyVals[-5+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[327] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(((ISourcePosition)yyVals[-5+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
 states[328] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getConditionState().begin();
     return yyVal;
   }
 };
 states[329] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getConditionState().end();
     return yyVal;
   }
 };
 states[330] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                     yyVal = new WhileNode(((ISourcePosition)yyVals[-6+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
     return yyVal;
   }
 };
 states[331] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                   lexer.getConditionState().begin();
     return yyVal;
   }
 };
 states[332] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                   lexer.getConditionState().end();
     return yyVal;
   }
 };
 states[333] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
                     yyVal = new UntilNode(((ISourcePosition)yyVals[-6+yyTop]), support.getConditionNode(((Node)yyVals[-4+yyTop])), body);
     return yyVal;
   }
 };
 states[334] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newCaseNode(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[335] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newCaseNode(((ISourcePosition)yyVals[-3+yyTop]), null, ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[336] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getConditionState().begin();
     return yyVal;
   }
 };
 states[337] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.getConditionState().end();
     return yyVal;
   }
 };
 states[338] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                       /* ENEBO: Lots of optz in 1.9 parser here*/
                     yyVal = new ForNode(((ISourcePosition)yyVals[-8+yyTop]), ((Node)yyVals[-7+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[-4+yyTop]), support.getCurrentScope());
     return yyVal;
   }
 };
 states[339] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("class definition in method body");
                     }
                     support.pushLocalScope();
     return yyVal;
   }
 };
 states[340] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
 
                     yyVal = new ClassNode(((ISourcePosition)yyVals[-5+yyTop]), ((Colon3Node)yyVals[-4+yyTop]), support.getCurrentScope(), body, ((Node)yyVals[-3+yyTop]));
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[341] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = Boolean.valueOf(support.isInDef());
                     support.setInDef(false);
     return yyVal;
   }
 };
 states[342] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = Integer.valueOf(support.getInSingle());
                     support.setInSingle(0);
                     support.pushLocalScope();
     return yyVal;
   }
 };
 states[343] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
 
                     yyVal = new SClassNode(((ISourcePosition)yyVals[-7+yyTop]), ((Node)yyVals[-5+yyTop]), support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInDef(((Boolean)yyVals[-4+yyTop]).booleanValue());
                     support.setInSingle(((Integer)yyVals[-2+yyTop]).intValue());
     return yyVal;
   }
 };
 states[344] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (support.isInDef() || support.isInSingle()) { 
                         support.yyerror("module definition in method body");
                     }
                     support.pushLocalScope();
     return yyVal;
   }
 };
 states[345] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]) == null ? NilImplicitNode.NIL : ((Node)yyVals[-1+yyTop]);
 
                     yyVal = new ModuleNode(((ISourcePosition)yyVals[-4+yyTop]), ((Colon3Node)yyVals[-3+yyTop]), support.getCurrentScope(), body);
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[346] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.setInDef(true);
                     support.pushLocalScope();
     return yyVal;
   }
 };
 states[347] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]);
                     if (body == null) body = NilImplicitNode.NIL;
 
                     yyVal = new DefnNode(((ISourcePosition)yyVals[-5+yyTop]), new ArgumentNode(((ISourcePosition)yyVals[-5+yyTop]), ((String)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInDef(false);
     return yyVal;
   }
 };
 states[348] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_FNAME);
     return yyVal;
   }
 };
 states[349] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.setInSingle(support.getInSingle() + 1);
                     support.pushLocalScope();
                     lexer.setState(LexState.EXPR_ENDFN); /* force for args */
     return yyVal;
   }
 };
 states[350] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node body = ((Node)yyVals[-1+yyTop]);
                     if (body == null) body = NilImplicitNode.NIL;
 
                     yyVal = new DefsNode(((ISourcePosition)yyVals[-8+yyTop]), ((Node)yyVals[-7+yyTop]), new ArgumentNode(((ISourcePosition)yyVals[-8+yyTop]), ((String)yyVals[-4+yyTop])), ((ArgsNode)yyVals[-2+yyTop]), support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInSingle(support.getInSingle() - 1);
     return yyVal;
   }
 };
 states[351] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BreakNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[352] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new NextNode(((ISourcePosition)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[353] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new RedoNode(((ISourcePosition)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[354] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new RetryNode(((ISourcePosition)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[355] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.checkExpression(((Node)yyVals[0+yyTop]));
                     yyVal = ((Node)yyVals[0+yyTop]);
                     if (yyVal == null) yyVal = NilImplicitNode.NIL;
     return yyVal;
   }
 };
 states[362] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IfNode(((ISourcePosition)yyVals[-4+yyTop]), support.getConditionNode(((Node)yyVals[-3+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[364] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[366] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
     return yyVal;
   }
 };
 states[367] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[368] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[369] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[370] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[371] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((ListNode)yyVals[0+yyTop]).getPosition(), ((ListNode)yyVals[0+yyTop]), null, null);
     return yyVal;
   }
 };
 states[372] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null), null);
     return yyVal;
   }
 };
 states[373] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[374] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-2+yyTop]).getPosition(), ((ListNode)yyVals[-2+yyTop]), new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
 states[375] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(((ListNode)yyVals[-4+yyTop]).getPosition(), ((ListNode)yyVals[-4+yyTop]), new StarNode(lexer.getPosition()), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[376] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null), null);
     return yyVal;
   }
 };
 states[377] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), null), ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[378] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(lexer.getPosition(), null, new StarNode(lexer.getPosition()), null);
     return yyVal;
   }
 };
 states[379] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new MultipleAsgn19Node(support.getPosition(((ListNode)yyVals[0+yyTop])), null, null, ((ListNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[380] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[381] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[382] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(lexer.getPosition(), null, ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[383] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[384] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsTailHolder)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[385] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(lexer.getPosition(), null, null, null);
     return yyVal;
   }
 };
 states[386] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[387] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[388] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[389] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[390] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[391] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     RestArgNode rest = new UnnamedRestArgNode(((ListNode)yyVals[-1+yyTop]).getPosition(), null, support.getCurrentScope().addVariable("*"));
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, rest, null, (ArgsTailHolder) null);
     return yyVal;
   }
 };
 states[392] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[393] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[394] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-3+yyTop])), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[395] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-5+yyTop])), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[396] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(support.getPosition(((ListNode)yyVals[-1+yyTop])), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[397] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[398] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[399] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[400] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ArgsTailHolder)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[401] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
     /* was $$ = null;*/
                     yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
     return yyVal;
   }
 };
 states[402] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.commandStart = true;
                     yyVal = ((ArgsNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[403] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
     return yyVal;
   }
 };
 states[404] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
     return yyVal;
   }
 };
 states[405] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsNode)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
 states[406] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[407] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[408] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[409] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[410] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.new_bv(((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[411] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[412] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.pushBlockScope();
                     yyVal = lexer.getLeftParenBegin();
                     lexer.setLeftParenBegin(lexer.incrementParenNest());
     return yyVal;
   }
 };
 states[413] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new LambdaNode(((ArgsNode)yyVals[-1+yyTop]).getPosition(), ((ArgsNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), support.getCurrentScope());
                     support.popCurrentScope();
                     lexer.setLeftParenBegin(((Integer)yyVals[-2+yyTop]));
     return yyVal;
   }
 };
 states[414] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsNode)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
 states[415] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[416] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[417] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[418] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.pushBlockScope();
     return yyVal;
   }
 };
 states[419] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[420] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
                     if (((Node)yyVals[-1+yyTop]) instanceof YieldNode) {
                         throw new SyntaxException(PID.BLOCK_GIVEN_TO_YIELD, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "block given to yield");
                     }
                     if (((Node)yyVals[-1+yyTop]) instanceof BlockAcceptingNode && ((BlockAcceptingNode)yyVals[-1+yyTop]).getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, ((Node)yyVals[-1+yyTop]).getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     if (((Node)yyVals[-1+yyTop]) instanceof NonLocalControlFlowNode) {
                         ((BlockAcceptingNode) ((NonLocalControlFlowNode)yyVals[-1+yyTop]).getValueNode()).setIterNode(((IterNode)yyVals[0+yyTop]));
                     } else {
                         ((BlockAcceptingNode)yyVals[-1+yyTop]).setIterNode(((IterNode)yyVals[0+yyTop]));
                     }
                     yyVal = ((Node)yyVals[-1+yyTop]);
                     ((Node)yyVal).setPosition(((Node)yyVals[-1+yyTop]).getPosition());
     return yyVal;
   }
 };
 states[421] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[422] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[423] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-4+yyTop]), ((String)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), ((IterNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[424] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.frobnicate_fcall_args(((FCallNode)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
                     yyVal = ((FCallNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[425] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[426] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[427] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), ((String)yyVals[0+yyTop]), null, null);
     return yyVal;
   }
 };
 states[428] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), "call", ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[429] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_call(((Node)yyVals[-2+yyTop]), "call", ((Node)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[430] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_super(((ISourcePosition)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[431] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZSuperNode(((ISourcePosition)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[432] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-3+yyTop]) instanceof SelfNode) {
                         yyVal = support.new_fcall("[]");
                         support.frobnicate_fcall_args(((FCallNode)yyVal), ((Node)yyVals[-1+yyTop]), null);
                     } else {
                         yyVal = support.new_call(((Node)yyVals[-3+yyTop]), "[]", ((Node)yyVals[-1+yyTop]), null);
                     }
     return yyVal;
   }
 };
 states[433] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.pushBlockScope();
     return yyVal;
   }
 };
 states[434] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[435] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.pushBlockScope();
     return yyVal;
   }
 };
 states[436] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new IterNode(((ISourcePosition)yyVals[-4+yyTop]), ((ArgsNode)yyVals[-2+yyTop]), ((Node)yyVals[-1+yyTop]), support.getCurrentScope());
                     support.popCurrentScope();
     return yyVal;
   }
 };
 states[437] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newWhenNode(((ISourcePosition)yyVals[-4+yyTop]), ((Node)yyVals[-3+yyTop]), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[440] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     Node node;
                     if (((Node)yyVals[-3+yyTop]) != null) {
                         node = support.appendToBlock(support.node_assign(((Node)yyVals[-3+yyTop]), new GlobalVarNode(((ISourcePosition)yyVals[-5+yyTop]), "$!")), ((Node)yyVals[-1+yyTop]));
                         if (((Node)yyVals[-1+yyTop]) != null) {
                             node.setPosition(((ISourcePosition)yyVals[-5+yyTop]));
                         }
                     } else {
                         node = ((Node)yyVals[-1+yyTop]);
                     }
                     Node body = node == null ? NilImplicitNode.NIL : node;
                     yyVal = new RescueBodyNode(((ISourcePosition)yyVals[-5+yyTop]), ((Node)yyVals[-4+yyTop]), body, ((RescueBodyNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[441] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null; 
     return yyVal;
   }
 };
 states[442] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[443] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.splat_array(((Node)yyVals[0+yyTop]));
                     if (yyVal == null) yyVal = ((Node)yyVals[0+yyTop]); /* ArgsCat or ArgsPush*/
     return yyVal;
   }
 };
 states[445] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[447] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[450] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    /* FIXME: We may be intern'ing more than once.*/
-                    yyVal = new SymbolNode(lexer.getPosition(), ((String)yyVals[0+yyTop]).intern());
+                    yyVal = new SymbolNode(lexer.getPosition(), new ByteList(((String)yyVals[0+yyTop]).getBytes(), lexer.getEncoding()));
     return yyVal;
   }
 };
 states[452] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]) instanceof EvStrNode ? new DStrNode(((Node)yyVals[0+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[0+yyTop])) : ((Node)yyVals[0+yyTop]);
                     /*
                     NODE *node = $1;
                     if (!node) {
                         node = NEW_STR(STR_NEW0());
                     } else {
                         node = evstr2dstr(node);
                     }
                     $$ = node;
                     */
     return yyVal;
   }
 };
 states[453] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ByteList aChar = ByteList.create(((String)yyVals[0+yyTop]));
                     aChar.setEncoding(lexer.getEncoding());
                     yyVal = lexer.createStrNode(lexer.getPosition(), aChar, 0);
     return yyVal;
   }
 };
 states[454] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[455] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[456] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[457] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ISourcePosition position = support.getPosition(((Node)yyVals[-1+yyTop]));
 
                     if (((Node)yyVals[-1+yyTop]) == null) {
                         yyVal = new XStrNode(position, null);
                     } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
                         yyVal = new XStrNode(position, (ByteList) ((StrNode)yyVals[-1+yyTop]).getValue().clone());
                     } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                         yyVal = new DXStrNode(position, ((DStrNode)yyVals[-1+yyTop]));
 
                         ((Node)yyVal).setPosition(position);
                     } else {
                         yyVal = new DXStrNode(position).add(((Node)yyVals[-1+yyTop]));
                     }
     return yyVal;
   }
 };
 states[458] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.newRegexpNode(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), (RegexpNode) ((RegexpNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[459] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[460] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[461] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[462] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DStrNode(((ListNode)yyVals[-2+yyTop]).getPosition(), lexer.getEncoding()).add(((Node)yyVals[-1+yyTop])) : ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[464] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[465] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[466] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[467] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[468] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]) instanceof EvStrNode ? new DSymbolNode(((ListNode)yyVals[-2+yyTop]).getPosition()).add(((Node)yyVals[-1+yyTop])) : support.asSymbol(((ListNode)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop])));
     return yyVal;
   }
 };
 states[469] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = new ZArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[470] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[471] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ZArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[472] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[473] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[474] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[475] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[476] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(support.asSymbol(((ListNode)yyVals[-2+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop])));
     return yyVal;
   }
 };
 states[477] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ByteList aChar = ByteList.create("");
                     aChar.setEncoding(lexer.getEncoding());
                     yyVal = lexer.createStrNode(lexer.getPosition(), aChar, 0);
     return yyVal;
   }
 };
 states[478] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.literal_concat(((Node)yyVals[-1+yyTop]).getPosition(), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[479] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[480] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[481] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[482] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
     /* FIXME: mri is different here.*/
                     yyVal = support.literal_concat(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[483] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[484] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = lexer.getStrTerm();
                     lexer.setStrTerm(null);
                     lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
 states[485] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setStrTerm(((StrTerm)yyVals[-1+yyTop]));
                     yyVal = new EvStrNode(support.getPosition(((Node)yyVals[0+yyTop])), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[486] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    yyVal = lexer.getStrTerm();
                    lexer.getConditionState().stop();
                    lexer.getCmdArgumentState().stop();
                    lexer.setStrTerm(null);
                    lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
 states[487] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    lexer.getConditionState().restart();
                    lexer.getCmdArgumentState().restart();
                    lexer.setStrTerm(((StrTerm)yyVals[-2+yyTop]));
 
                    yyVal = support.newEvStrNode(support.getPosition(((Node)yyVals[-1+yyTop])), ((Node)yyVals[-1+yyTop]));
     return yyVal;
   }
 };
 states[488] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = new GlobalVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[489] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = new InstVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[490] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = new ClassVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[492] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      lexer.setState(LexState.EXPR_END);
                      yyVal = ((String)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[497] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      lexer.setState(LexState.EXPR_END);
 
                      /* DStrNode: :"some text #{some expression}"*/
                      /* StrNode: :"some text"*/
                      /* EvStrNode :"#{some expression}"*/
                      /* Ruby 1.9 allows empty strings as symbols*/
                      if (((Node)yyVals[-1+yyTop]) == null) {
-                         yyVal = new SymbolNode(lexer.getPosition(), "");
+                         yyVal = new SymbolNode(lexer.getPosition(), new ByteList(new byte[0], lexer.getEncoding()));
                      } else if (((Node)yyVals[-1+yyTop]) instanceof DStrNode) {
                          yyVal = new DSymbolNode(((Node)yyVals[-1+yyTop]).getPosition(), ((DStrNode)yyVals[-1+yyTop]));
                      } else if (((Node)yyVals[-1+yyTop]) instanceof StrNode) {
-                         yyVal = new SymbolNode(((Node)yyVals[-1+yyTop]).getPosition(), ((StrNode)yyVals[-1+yyTop]).getValue().toString().intern());
+                         yyVal = new SymbolNode(((Node)yyVals[-1+yyTop]).getPosition(), ((StrNode)yyVals[-1+yyTop]).getValue());
                      } else {
                          yyVal = new DSymbolNode(((Node)yyVals[-1+yyTop]).getPosition());
                          ((DSymbolNode)yyVal).add(((Node)yyVals[-1+yyTop]));
                      }
     return yyVal;
   }
 };
 states[498] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);  
     return yyVal;
   }
 };
 states[499] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = support.negateNumeric(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[500] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[501] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((FloatNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[502] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((RationalNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[503] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                      yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[504] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.declareIdentifier(((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[505] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new InstVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[506] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new GlobalVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[507] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ConstNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[508] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ClassVarNode(lexer.getPosition(), ((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[509] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new NilNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[510] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new SelfNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[511] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new TrueNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[512] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new FalseNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[513] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new FileNode(lexer.getPosition(), new ByteList(lexer.getPosition().getFile().getBytes(),
                     support.getConfiguration().getRuntime().getEncodingService().getLocaleEncoding()));
     return yyVal;
   }
 };
 states[514] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new FixnumNode(lexer.getPosition(), lexer.tokline.getLine()+1);
     return yyVal;
   }
 };
 states[515] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new EncodingNode(lexer.getPosition(), lexer.getEncoding());
     return yyVal;
   }
 };
 states[516] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), null);
     return yyVal;
   }
 };
 states[517] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    yyVal = new InstAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[518] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    yyVal = new GlobalAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[519] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (support.isInDef() || support.isInSingle()) support.compile_error("dynamic constant assignment");
 
                     yyVal = new ConstDeclNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), null, NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[520] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ClassVarAsgnNode(lexer.getPosition(), ((String)yyVals[0+yyTop]), NilImplicitNode.NIL);
     return yyVal;
   }
 };
 states[521] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.compile_error("Can't assign to nil");
                     yyVal = null;
     return yyVal;
   }
 };
 states[522] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.compile_error("Can't change the value of self");
                     yyVal = null;
     return yyVal;
   }
 };
 states[523] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.compile_error("Can't assign to true");
                     yyVal = null;
     return yyVal;
   }
 };
 states[524] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.compile_error("Can't assign to false");
                     yyVal = null;
     return yyVal;
   }
 };
 states[525] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.compile_error("Can't assign to __FILE__");
                     yyVal = null;
     return yyVal;
   }
 };
 states[526] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.compile_error("Can't assign to __LINE__");
                     yyVal = null;
     return yyVal;
   }
 };
 states[527] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.compile_error("Can't assign to __ENCODING__");
                     yyVal = null;
     return yyVal;
   }
 };
 states[528] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[529] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[530] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[531] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
 states[532] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[533] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                    yyVal = null;
     return yyVal;
   }
 };
 states[534] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                     lexer.setState(LexState.EXPR_BEG);
                     lexer.commandStart = true;
     return yyVal;
   }
 };
 states[535] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsNode)yyVals[-1+yyTop]);
                     lexer.setState(LexState.EXPR_BEG);
                     lexer.commandStart = true;
     return yyVal;
   }
 };
 states[536] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[537] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[538] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(lexer.getPosition(), null, ((String)yyVals[-1+yyTop]), ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[539] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(((BlockArgNode)yyVals[0+yyTop]).getPosition(), null, null, ((BlockArgNode)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[540] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ArgsTailHolder)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[541] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args_tail(lexer.getPosition(), null, null, null);
     return yyVal;
   }
 };
 states[542] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[543] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-7+yyTop]).getPosition(), ((ListNode)yyVals[-7+yyTop]), ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[544] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[545] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[546] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), ((ListNode)yyVals[-3+yyTop]), null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[547] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), ((ListNode)yyVals[-5+yyTop]), null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[548] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), ((ListNode)yyVals[-1+yyTop]), null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[549] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[550] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-5+yyTop]).getPosition(), null, ((ListNode)yyVals[-5+yyTop]), ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[551] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-1+yyTop]).getPosition(), null, ((ListNode)yyVals[-1+yyTop]), null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[552] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ListNode)yyVals[-3+yyTop]).getPosition(), null, ((ListNode)yyVals[-3+yyTop]), null, ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[553] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-1+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-1+yyTop]), null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[554] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((RestArgNode)yyVals[-3+yyTop]).getPosition(), null, null, ((RestArgNode)yyVals[-3+yyTop]), ((ListNode)yyVals[-1+yyTop]), ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[555] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(((ArgsTailHolder)yyVals[0+yyTop]).getPosition(), null, null, null, null, ((ArgsTailHolder)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[556] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
     return yyVal;
   }
 };
 states[557] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a constant");
     return yyVal;
   }
 };
 states[558] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be an instance variable");
     return yyVal;
   }
 };
 states[559] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a global variable");
     return yyVal;
   }
 };
 states[560] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.yyerror("formal argument cannot be a class variable");
     return yyVal;
   }
 };
 states[562] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.formal_argument(((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[563] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.arg_var(((String)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[564] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((Node)yyVals[-1+yyTop]);
                     /*            {
             ID tid = internal_id();
             arg_var(tid);
             if (dyna_in_block()) {
                 $2->nd_value = NEW_DVAR(tid);
             }
             else {
                 $2->nd_value = NEW_LVAR(tid);
             }
             $$ = NEW_ARGS_AUX(tid, 1);
             $$->nd_next = $2;*/
     return yyVal;
   }
 };
 states[565] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(lexer.getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[566] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
                     yyVal = ((ListNode)yyVals[-2+yyTop]);
     return yyVal;
   }
 };
 states[567] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.arg_var(support.formal_argument(((String)yyVals[0+yyTop])));
                     yyVal = ((String)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[568] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.keyword_arg(((Node)yyVals[0+yyTop]).getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[569] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.keyword_arg(lexer.getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), new RequiredKeywordArgumentValueNode()));
     return yyVal;
   }
 };
 states[570] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.keyword_arg(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-1+yyTop]), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[571] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.keyword_arg(lexer.getPosition(), support.assignableLabelOrIdentifier(((String)yyVals[0+yyTop]), new RequiredKeywordArgumentValueNode()));
     return yyVal;
   }
 };
 states[572] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[573] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[574] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new ArrayNode(((Node)yyVals[0+yyTop]).getPosition(), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[575] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((ListNode)yyVals[-2+yyTop]).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[576] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((String)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[577] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((String)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[578] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.shadowing_lvar(((String)yyVals[0+yyTop]));
                     yyVal = ((String)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[579] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.internalId();
     return yyVal;
   }
 };
 states[580] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.arg_var(((String)yyVals[-2+yyTop]));
                     yyVal = new OptArgNode(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[581] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     support.arg_var(support.formal_argument(((String)yyVals[-2+yyTop])));
                     yyVal = new OptArgNode(support.getPosition(((Node)yyVals[0+yyTop])), support.assignableLabelOrIdentifier(((String)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop])));
     return yyVal;
   }
 };
 states[582] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[583] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[584] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new BlockNode(((Node)yyVals[0+yyTop]).getPosition()).add(((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[585] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = support.appendToBlock(((ListNode)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[588] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (!support.is_local_id(((String)yyVals[0+yyTop]))) {
                         support.yyerror("rest argument must be local variable");
                     }
                     
                     yyVal = new RestArgNode(support.arg_var(support.shadowing_lvar(((String)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
 states[589] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new UnnamedRestArgNode(lexer.getPosition(), "", support.getCurrentScope().addVariable("*"));
     return yyVal;
   }
 };
 states[592] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (!support.is_local_id(((String)yyVals[0+yyTop]))) {
                         support.yyerror("block argument must be local variable");
                     }
                     
                     yyVal = new BlockArgNode(support.arg_var(support.shadowing_lvar(((String)yyVals[0+yyTop]))));
     return yyVal;
   }
 };
 states[593] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((BlockArgNode)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[594] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = null;
     return yyVal;
   }
 };
 states[595] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (!(((Node)yyVals[0+yyTop]) instanceof SelfNode)) {
                         support.checkExpression(((Node)yyVals[0+yyTop]));
                     }
                     yyVal = ((Node)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[596] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     lexer.setState(LexState.EXPR_BEG);
     return yyVal;
   }
 };
 states[597] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     if (((Node)yyVals[-1+yyTop]) == null) {
                         support.yyerror("can't define single method for ().");
                     } else if (((Node)yyVals[-1+yyTop]) instanceof ILiteralNode) {
                         support.yyerror("can't define single method for literals.");
                     }
                     support.checkExpression(((Node)yyVals[-1+yyTop]));
                     yyVal = ((Node)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[598] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new HashNode(lexer.getPosition());
     return yyVal;
   }
 };
 states[599] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((HashNode)yyVals[-1+yyTop]);
     return yyVal;
   }
 };
 states[600] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new HashNode(lexer.getPosition(), ((KeyValuePair)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[601] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((HashNode)yyVals[-2+yyTop]).add(((KeyValuePair)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[602] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new KeyValuePair<Node,Node>(((Node)yyVals[-2+yyTop]), ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[603] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
-                    yyVal = new KeyValuePair<Node,Node>(new SymbolNode(support.getPosition(((Node)yyVals[0+yyTop])), ((String)yyVals[-1+yyTop])), ((Node)yyVals[0+yyTop]));
+                    SymbolNode label = new SymbolNode(support.getPosition(((Node)yyVals[0+yyTop])), new ByteList(((String)yyVals[-1+yyTop]).getBytes(), lexer.getEncoding()));
+                    yyVal = new KeyValuePair<Node,Node>(label, ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[604] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = new KeyValuePair<Node,Node>(null, ((Node)yyVals[0+yyTop]));
     return yyVal;
   }
 };
 states[621] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((String)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[622] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                     yyVal = ((String)yyVals[0+yyTop]);
     return yyVal;
   }
 };
 states[630] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                       yyVal = null;
     return yyVal;
   }
 };
 states[631] = new ParserState() {
   @Override public Object execute(ParserSupport support, RubyLexer lexer, Object yyVal, Object[] yyVals, int yyTop) {
                   yyVal = null;
     return yyVal;
   }
 };
 }
 					// line 2482 "RubyParser.y"
 
     /** The parse method use an lexer stream and parse it to an AST node 
      * structure
      */
     public RubyParserResult parse(ParserConfiguration configuration, LexerSource source) throws IOException {
         support.reset();
         support.setConfiguration(configuration);
         support.setResult(new RubyParserResult());
         
         lexer.reset();
         lexer.setSource(source);
         lexer.setEncoding(configuration.getDefaultEncoding());
 
         yyparse(lexer, configuration.isDebug() ? new YYDebug() : null);
         
         return support.getResult();
     }
 }
 					// line 9616 "-"
diff --git a/core/src/main/java/org/jruby/parser/RubyParser.y b/core/src/main/java/org/jruby/parser/RubyParser.y
index 622ae58a07..7769243fbe 100644
--- a/core/src/main/java/org/jruby/parser/RubyParser.y
+++ b/core/src/main/java/org/jruby/parser/RubyParser.y
@@ -844,1656 +844,1656 @@ fitem           : fsym {
                     $$ = $1;
                 }
 
 undef_list      : fitem {
                     $$ = support.newUndef($1.getPosition(), $1);
                 }
                 | undef_list ',' {
                     lexer.setState(LexState.EXPR_FNAME);
                 } fitem {
                     $$ = support.appendToBlock($1, support.newUndef($1.getPosition(), $4));
                 }
 
 // String:op
 op              : tPIPE | tCARET | tAMPER2 | tCMP | tEQ | tEQQ | tMATCH
                 | tNMATCH | tGT | tGEQ | tLT | tLEQ | tNEQ | tLSHFT | tRSHFT
                 | tDSTAR | tPLUS | tMINUS | tSTAR2 | tSTAR | tDIVIDE | tPERCENT 
                 | tPOW | tBANG | tTILDE | tUPLUS | tUMINUS | tAREF | tASET 
                 | tBACK_REF2
 
 // String:op
 reswords        : k__LINE__ {
                     $$ = "__LINE__";
                 }
                 | k__FILE__ {
                     $$ = "__FILE__";
                 }
                 | k__ENCODING__ {
                     $$ = "__ENCODING__";
                 }
                 | klBEGIN {
                     $$ = "BEGIN";
                 }
                 | klEND {
                     $$ = "END";
                 }
                 | kALIAS {
                     $$ = "alias";
                 }
                 | kAND {
                     $$ = "and";
                 }
                 | kBEGIN {
                     $$ = "begin";
                 }
                 | kBREAK {
                     $$ = "break";
                 }
                 | kCASE {
                     $$ = "case";
                 }
                 | kCLASS {
                     $$ = "class";
                 }
                 | kDEF {
                     $$ = "def";
                 }
                 | kDEFINED {
                     $$ = "defined?";
                 }
                 | kDO {
                     $$ = "do";
                 }
                 | kELSE {
                     $$ = "else";
                 }
                 | kELSIF {
                     $$ = "elsif";
                 }
                 | kEND {
                     $$ = "end";
                 }
                 | kENSURE {
                     $$ = "ensure";
                 }
                 | kFALSE {
                     $$ = "false";
                 }
                 | kFOR {
                     $$ = "for";
                 }
                 | kIN {
                     $$ = "in";
                 }
                 | kMODULE {
                     $$ = "module";
                 }
                 | kNEXT {
                     $$ = "next";
                 }
                 | kNIL {
                     $$ = "nil";
                 }
                 | kNOT {
                     $$ = "not";
                 }
                 | kOR {
                     $$ = "or";
                 }
                 | kREDO {
                     $$ = "redo";
                 }
                 | kRESCUE {
                     $$ = "rescue";
                 }
                 | kRETRY {
                     $$ = "retry";
                 }
                 | kRETURN {
                     $$ = "return";
                 }
                 | kSELF {
                     $$ = "self";
                 }
                 | kSUPER {
                     $$ = "super";
                 }
                 | kTHEN {
                     $$ = "then";
                 }
                 | kTRUE {
                     $$ = "true";
                 }
                 | kUNDEF {
                     $$ = "undef";
                 }
                 | kWHEN {
                     $$ = "when";
                 }
                 | kYIELD {
                     $$ = "yield";
                 }
                 | kIF_MOD {
                     $$ = "if";
                 }
                 | kUNLESS_MOD {
                     $$ = "unless";
                 }
                 | kWHILE_MOD {
                     $$ = "while";
                 }
                 | kUNTIL_MOD {
                     $$ = "until";
                 }
                 | kRESCUE_MOD {
                     $$ = "rescue";
                 }
 
 arg             : lhs '=' arg {
                     $$ = support.node_assign($1, $3);
                     // FIXME: Consider fixing node_assign itself rather than single case
                     $<Node>$.setPosition(support.getPosition($1));
                 }
                 | lhs '=' arg kRESCUE_MOD arg {
                     ISourcePosition position = support.getPosition($1);
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
                     $$ = support.node_assign($1, new RescueNode(position, $3, new RescueBodyNode(position, null, body, null), null));
                 }
                 | var_lhs tOP_ASGN arg {
                     support.checkExpression($3);
 
                     ISourcePosition pos = $1.getPosition();
                     String asgnOp = $2;
                     if (asgnOp.equals("||")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnOrNode(pos, support.gettable2($1), $1);
                     } else if (asgnOp.equals("&&")) {
                         $1.setValueNode($3);
                         $$ = new OpAsgnAndNode(pos, support.gettable2($1), $1);
                     } else {
                         $1.setValueNode(support.getOperatorCallNode(support.gettable2($1), asgnOp, $3));
                         $1.setPosition(pos);
                         $$ = $1;
                     }
                 }
                 | var_lhs tOP_ASGN arg kRESCUE_MOD arg {
                     support.checkExpression($3);
                     ISourcePosition pos = support.getPosition($5);
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
                     Node rescue = new RescueNode(pos, $3, new RescueBodyNode(support.getPosition($3), null, body, null), null);
 
                     pos = $1.getPosition();
                     String asgnOp = $2;
                     if (asgnOp.equals("||")) {
                         $1.setValueNode(rescue);
                         $$ = new OpAsgnOrNode(pos, support.gettable2($1), $1);
                     } else if (asgnOp.equals("&&")) {
                         $1.setValueNode(rescue);
                         $$ = new OpAsgnAndNode(pos, support.gettable2($1), $1);
                     } else {
                         $1.setValueNode(support.getOperatorCallNode(support.gettable2($1), asgnOp, rescue));
                         $1.setPosition(pos);
                         $$ = $1;
                     }
                 }
                 | primary_value '[' opt_call_args rbracket tOP_ASGN arg {
   // FIXME: arg_concat missing for opt_call_args
                     $$ = support.new_opElementAsgnNode($1, $5, $3, $6);
                 }
                 | primary_value tDOT tIDENTIFIER tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, $3, $4);
                 }
                 | primary_value tDOT tCONSTANT tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, $3, $4);
                 }
                 | primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg {
                     $$ = new OpAsgnNode(support.getPosition($1), $1, $5, $3, $4);
                 }
                 | primary_value tCOLON2 tCONSTANT tOP_ASGN arg {
                     support.yyerror("constant re-assignment");
                 }
                 | tCOLON3 tCONSTANT tOP_ASGN arg {
                     support.yyerror("constant re-assignment");
                 }
                 | backref tOP_ASGN arg {
                     support.backrefAssignError($1);
                 }
                 | arg tDOT2 arg {
                     support.checkExpression($1);
                     support.checkExpression($3);
     
                     boolean isLiteral = $1 instanceof FixnumNode && $3 instanceof FixnumNode;
                     $$ = new DotNode(support.getPosition($1), $1, $3, false, isLiteral);
                 }
                 | arg tDOT3 arg {
                     support.checkExpression($1);
                     support.checkExpression($3);
 
                     boolean isLiteral = $1 instanceof FixnumNode && $3 instanceof FixnumNode;
                     $$ = new DotNode(support.getPosition($1), $1, $3, true, isLiteral);
                 }
                 | arg tPLUS arg {
                     $$ = support.getOperatorCallNode($1, "+", $3, lexer.getPosition());
                 }
                 | arg tMINUS arg {
                     $$ = support.getOperatorCallNode($1, "-", $3, lexer.getPosition());
                 }
                 | arg tSTAR2 arg {
                     $$ = support.getOperatorCallNode($1, "*", $3, lexer.getPosition());
                 }
                 | arg tDIVIDE arg {
                     $$ = support.getOperatorCallNode($1, "/", $3, lexer.getPosition());
                 }
                 | arg tPERCENT arg {
                     $$ = support.getOperatorCallNode($1, "%", $3, lexer.getPosition());
                 }
                 | arg tPOW arg {
                     $$ = support.getOperatorCallNode($1, "**", $3, lexer.getPosition());
                 }
                 | tUMINUS_NUM simple_numeric tPOW arg {
                     $$ = support.getOperatorCallNode(support.getOperatorCallNode($2, "**", $4, lexer.getPosition()), "-@");
                 }
                 | tUPLUS arg {
                     $$ = support.getOperatorCallNode($2, "+@");
                 }
                 | tUMINUS arg {
                     $$ = support.getOperatorCallNode($2, "-@");
                 }
                 | arg tPIPE arg {
                     $$ = support.getOperatorCallNode($1, "|", $3, lexer.getPosition());
                 }
                 | arg tCARET arg {
                     $$ = support.getOperatorCallNode($1, "^", $3, lexer.getPosition());
                 }
                 | arg tAMPER2 arg {
                     $$ = support.getOperatorCallNode($1, "&", $3, lexer.getPosition());
                 }
                 | arg tCMP arg {
                     $$ = support.getOperatorCallNode($1, "<=>", $3, lexer.getPosition());
                 }
                 | arg tGT arg {
                     $$ = support.getOperatorCallNode($1, ">", $3, lexer.getPosition());
                 }
                 | arg tGEQ arg {
                     $$ = support.getOperatorCallNode($1, ">=", $3, lexer.getPosition());
                 }
                 | arg tLT arg {
                     $$ = support.getOperatorCallNode($1, "<", $3, lexer.getPosition());
                 }
                 | arg tLEQ arg {
                     $$ = support.getOperatorCallNode($1, "<=", $3, lexer.getPosition());
                 }
                 | arg tEQ arg {
                     $$ = support.getOperatorCallNode($1, "==", $3, lexer.getPosition());
                 }
                 | arg tEQQ arg {
                     $$ = support.getOperatorCallNode($1, "===", $3, lexer.getPosition());
                 }
                 | arg tNEQ arg {
                     $$ = support.getOperatorCallNode($1, "!=", $3, lexer.getPosition());
                 }
                 | arg tMATCH arg {
                     $$ = support.getMatchNode($1, $3);
                   /* ENEBO
                         $$ = match_op($1, $3);
                         if (nd_type($1) == NODE_LIT && TYPE($1->nd_lit) == T_REGEXP) {
                             $$ = reg_named_capture_assign($1->nd_lit, $$);
                         }
                   */
                 }
                 | arg tNMATCH arg {
                     $$ = support.getOperatorCallNode($1, "!~", $3, lexer.getPosition());
                 }
                 | tBANG arg {
                     $$ = support.getOperatorCallNode(support.getConditionNode($2), "!");
                 }
                 | tTILDE arg {
                     $$ = support.getOperatorCallNode($2, "~");
                 }
                 | arg tLSHFT arg {
                     $$ = support.getOperatorCallNode($1, "<<", $3, lexer.getPosition());
                 }
                 | arg tRSHFT arg {
                     $$ = support.getOperatorCallNode($1, ">>", $3, lexer.getPosition());
                 }
                 | arg tANDOP arg {
                     $$ = support.newAndNode($1.getPosition(), $1, $3);
                 }
                 | arg tOROP arg {
                     $$ = support.newOrNode($1.getPosition(), $1, $3);
                 }
                 | kDEFINED opt_nl arg {
                     $$ = support.new_defined($1, $3);
                 }
                 | arg '?' arg opt_nl ':' arg {
                     $$ = new IfNode(support.getPosition($1), support.getConditionNode($1), $3, $6);
                 }
                 | primary {
                     $$ = $1;
                 }
 
 arg_value       : arg {
                     support.checkExpression($1);
                     $$ = $1 != null ? $1 : NilImplicitNode.NIL;
                 }
 
 aref_args       : none
                 | args trailer {
                     $$ = $1;
                 }
                 | args ',' assocs trailer {
                     $$ = support.arg_append($1, $3);
                 }
                 | assocs trailer {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
 
 paren_args      : tLPAREN2 opt_call_args rparen {
                     $$ = $2;
                     if ($$ != null) $<Node>$.setPosition($1);
                 }
 
 opt_paren_args  : none | paren_args
 
 opt_call_args   : none 
                 | call_args
                 | args ',' {
                     $$ = $1;
                 }
                 | args ',' assocs ',' {
                     $$ = support.arg_append($1, $3);
                 }
                 | assocs ',' {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
    
 
 // [!null]
 call_args       : command {
                     $$ = support.newArrayNode(support.getPosition($1), $1);
                 }
                 | args opt_block_arg {
                     $$ = support.arg_blk_pass($1, $2);
                 }
                 | assocs opt_block_arg {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                     $$ = support.arg_blk_pass((Node)$$, $2);
                 }
                 | args ',' assocs opt_block_arg {
                     $$ = support.arg_append($1, $3);
                     $$ = support.arg_blk_pass((Node)$$, $4);
                 }
                 | block_arg {
                 }
 
 command_args    : /* none */ {
                     $$ = Long.valueOf(lexer.getCmdArgumentState().begin());
                 } call_args {
                     lexer.getCmdArgumentState().reset($<Long>1.longValue());
                     $$ = $2;
                 }
 
 block_arg       : tAMPER arg_value {
                     $$ = new BlockPassNode(support.getPosition($2), $2);
                 }
 
 opt_block_arg   : ',' block_arg {
                     $$ = $2;
                 }
                 | none_block_pass
 
 // [!null]
 args            : arg_value {
                     ISourcePosition pos = $1 == null ? lexer.getPosition() : $1.getPosition();
                     $$ = support.newArrayNode(pos, $1);
                 }
                 | tSTAR arg_value {
                     $$ = support.newSplatNode(support.getPosition($2), $2);
                 }
                 | args ',' arg_value {
                     Node node = support.splat_array($1);
 
                     if (node != null) {
                         $$ = support.list_append(node, $3);
                     } else {
                         $$ = support.arg_append($1, $3);
                     }
                 }
                 | args ',' tSTAR arg_value {
                     Node node = null;
 
                     // FIXME: lose syntactical elements here (and others like this)
                     if ($4 instanceof ArrayNode &&
                         (node = support.splat_array($1)) != null) {
                         $$ = support.list_concat(node, $4);
                     } else {
                         $$ = support.arg_concat(support.getPosition($1), $1, $4);
                     }
                 }
 
 mrhs_arg	: mrhs {
                     $$ = $1;
                 }
 		| arg_value {
                     $$ = $1;
                 }
 
 
 mrhs            : args ',' arg_value {
                     Node node = support.splat_array($1);
 
                     if (node != null) {
                         $$ = support.list_append(node, $3);
                     } else {
                         $$ = support.arg_append($1, $3);
                     }
                 }
                 | args ',' tSTAR arg_value {
                     Node node = null;
 
                     if ($4 instanceof ArrayNode &&
                         (node = support.splat_array($1)) != null) {
                         $$ = support.list_concat(node, $4);
                     } else {
                         $$ = support.arg_concat($1.getPosition(), $1, $4);
                     }
                 }
                 | tSTAR arg_value {
                      $$ = support.newSplatNode(support.getPosition($2), $2);
                 }
 
 primary         : literal
                 | strings
                 | xstring
                 | regexp
                 | words
                 | qwords
                 | symbols { 
                      $$ = $1; // FIXME: Why complaining without $$ = $1;
                 }
                 | qsymbols {
                      $$ = $1; // FIXME: Why complaining without $$ = $1;
                 }
                 | var_ref
                 | backref
                 | tFID {
                     $$ = support.new_fcall($1);
                 }
                 | kBEGIN bodystmt kEND {
                     $$ = new BeginNode($1, $2 == null ? NilImplicitNode.NIL : $2);
                 }
                 | tLPAREN_ARG {
                     lexer.setState(LexState.EXPR_ENDARG);
                 } rparen {
                     $$ = null; //FIXME: Should be implicit nil?
                 }
                 | tLPAREN_ARG expr {
                     lexer.setState(LexState.EXPR_ENDARG); 
                 } rparen {
                     if (Options.PARSER_WARN_GROUPED_EXPRESSIONS.load()) {
                       support.warning(ID.GROUPED_EXPRESSION, $1, "(...) interpreted as grouped expression");
                     }
                     $$ = $2;
                 }
                 | tLPAREN compstmt tRPAREN {
                     if ($2 != null) {
                         // compstmt position includes both parens around it
                         ((ISourcePositionHolder) $2).setPosition($1);
                         $$ = $2;
                     } else {
                         $$ = new NilNode($1);
                     }
                 }
                 | primary_value tCOLON2 tCONSTANT {
                     $$ = support.new_colon2(support.getPosition($1), $1, $3);
                 }
                 | tCOLON3 tCONSTANT {
                     $$ = support.new_colon3(lexer.getPosition(), $2);
                 }
                 | tLBRACK aref_args tRBRACK {
                     ISourcePosition position = support.getPosition($2);
                     if ($2 == null) {
                         $$ = new ZArrayNode(position); /* zero length array */
                     } else {
                         $$ = $2;
                     }
                 }
                 | tLBRACE assoc_list tRCURLY {
                     $$ = $2;
                 }
                 | kRETURN {
                     $$ = new ReturnNode($1, NilImplicitNode.NIL);
                 }
                 | kYIELD tLPAREN2 call_args rparen {
                     $$ = support.new_yield($1, $3);
                 }
                 | kYIELD tLPAREN2 rparen {
                     $$ = new ZYieldNode($1);
                 }
                 | kYIELD {
                     $$ = new ZYieldNode($1);
                 }
                 | kDEFINED opt_nl tLPAREN2 expr rparen {
                     $$ = support.new_defined($1, $4);
                 }
                 | kNOT tLPAREN2 expr rparen {
                     $$ = support.getOperatorCallNode(support.getConditionNode($3), "!");
                 }
                 | kNOT tLPAREN2 rparen {
                     $$ = support.getOperatorCallNode(NilImplicitNode.NIL, "!");
                 }
                 | fcall brace_block {
                     support.frobnicate_fcall_args($1, null, $2);
                     $$ = $1;                    
                 }
                 | method_call
                 | method_call brace_block {
                     if ($1 != null && 
                           $<BlockAcceptingNode>1.getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, $1.getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     $$ = $<BlockAcceptingNode>1.setIterNode($2);
                     $<Node>$.setPosition($1.getPosition());
                 }
                 | tLAMBDA lambda {
                     $$ = $2;
                 }
                 | kIF expr_value then compstmt if_tail kEND {
                     $$ = new IfNode($1, support.getConditionNode($2), $4, $5);
                 }
                 | kUNLESS expr_value then compstmt opt_else kEND {
                     $$ = new IfNode($1, support.getConditionNode($2), $5, $4);
                 }
                 | kWHILE {
                     lexer.getConditionState().begin();
                 } expr_value do {
                     lexer.getConditionState().end();
                 } compstmt kEND {
                     Node body = $6 == null ? NilImplicitNode.NIL : $6;
                     $$ = new WhileNode($1, support.getConditionNode($3), body);
                 }
                 | kUNTIL {
                   lexer.getConditionState().begin();
                 } expr_value do {
                   lexer.getConditionState().end();
                 } compstmt kEND {
                     Node body = $6 == null ? NilImplicitNode.NIL : $6;
                     $$ = new UntilNode($1, support.getConditionNode($3), body);
                 }
                 | kCASE expr_value opt_terms case_body kEND {
                     $$ = support.newCaseNode($1, $2, $4);
                 }
                 | kCASE opt_terms case_body kEND {
                     $$ = support.newCaseNode($1, null, $3);
                 }
                 | kFOR for_var kIN {
                     lexer.getConditionState().begin();
                 } expr_value do {
                     lexer.getConditionState().end();
                 } compstmt kEND {
                       // ENEBO: Lots of optz in 1.9 parser here
                     $$ = new ForNode($1, $2, $8, $5, support.getCurrentScope());
                 }
                 | kCLASS cpath superclass {
                     if (support.isInDef() || support.isInSingle()) {
                         support.yyerror("class definition in method body");
                     }
                     support.pushLocalScope();
                 } bodystmt kEND {
                     Node body = $5 == null ? NilImplicitNode.NIL : $5;
 
                     $$ = new ClassNode($1, $<Colon3Node>2, support.getCurrentScope(), body, $3);
                     support.popCurrentScope();
                 }
                 | kCLASS tLSHFT expr {
                     $$ = Boolean.valueOf(support.isInDef());
                     support.setInDef(false);
                 } term {
                     $$ = Integer.valueOf(support.getInSingle());
                     support.setInSingle(0);
                     support.pushLocalScope();
                 } bodystmt kEND {
                     Node body = $7 == null ? NilImplicitNode.NIL : $7;
 
                     $$ = new SClassNode($1, $3, support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInDef($<Boolean>4.booleanValue());
                     support.setInSingle($<Integer>6.intValue());
                 }
                 | kMODULE cpath {
                     if (support.isInDef() || support.isInSingle()) { 
                         support.yyerror("module definition in method body");
                     }
                     support.pushLocalScope();
                 } bodystmt kEND {
                     Node body = $4 == null ? NilImplicitNode.NIL : $4;
 
                     $$ = new ModuleNode($1, $<Colon3Node>2, support.getCurrentScope(), body);
                     support.popCurrentScope();
                 }
                 | kDEF fname {
                     support.setInDef(true);
                     support.pushLocalScope();
                 } f_arglist bodystmt kEND {
                     Node body = $5;
                     if (body == null) body = NilImplicitNode.NIL;
 
                     $$ = new DefnNode($1, new ArgumentNode($1, $2), $4, support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInDef(false);
                 }
                 | kDEF singleton dot_or_colon {
                     lexer.setState(LexState.EXPR_FNAME);
                 } fname {
                     support.setInSingle(support.getInSingle() + 1);
                     support.pushLocalScope();
                     lexer.setState(LexState.EXPR_ENDFN); /* force for args */
                 } f_arglist bodystmt kEND {
                     Node body = $8;
                     if (body == null) body = NilImplicitNode.NIL;
 
                     $$ = new DefsNode($1, $2, new ArgumentNode($1, $5), $7, support.getCurrentScope(), body);
                     support.popCurrentScope();
                     support.setInSingle(support.getInSingle() - 1);
                 }
                 | kBREAK {
                     $$ = new BreakNode($1, NilImplicitNode.NIL);
                 }
                 | kNEXT {
                     $$ = new NextNode($1, NilImplicitNode.NIL);
                 }
                 | kREDO {
                     $$ = new RedoNode($1);
                 }
                 | kRETRY {
                     $$ = new RetryNode($1);
                 }
 
 primary_value   : primary {
                     support.checkExpression($1);
                     $$ = $1;
                     if ($$ == null) $$ = NilImplicitNode.NIL;
                 }
 
 then            : term
                 | kTHEN
                 | term kTHEN
 
 do              : term
                 | kDO_COND
 
 if_tail         : opt_else
                 | kELSIF expr_value then compstmt if_tail {
                     $$ = new IfNode($1, support.getConditionNode($2), $4, $5);
                 }
 
 opt_else        : none
                 | kELSE compstmt {
                     $$ = $2;
                 }
 
 for_var         : lhs
                 | mlhs {
                 }
 
 f_marg          : f_norm_arg {
                      $$ = support.assignableLabelOrIdentifier($1, NilImplicitNode.NIL);
                 }
                 | tLPAREN f_margs rparen {
                     $$ = $2;
                 }
 
 // [!null]
 f_marg_list     : f_marg {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | f_marg_list ',' f_marg {
                     $$ = $1.add($3);
                 }
 
 f_margs         : f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, null, null);
                 }
                 | f_marg_list ',' tSTAR f_norm_arg {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, support.assignableLabelOrIdentifier($4, null), null);
                 }
                 | f_marg_list ',' tSTAR f_norm_arg ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, support.assignableLabelOrIdentifier($4, null), $6);
                 }
                 | f_marg_list ',' tSTAR {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), null);
                 }
                 | f_marg_list ',' tSTAR ',' f_marg_list {
                     $$ = new MultipleAsgn19Node($1.getPosition(), $1, new StarNode(lexer.getPosition()), $5);
                 }
                 | tSTAR f_norm_arg {
                     $$ = new MultipleAsgn19Node(lexer.getPosition(), null, support.assignableLabelOrIdentifier($2, null), null);
                 }
                 | tSTAR f_norm_arg ',' f_marg_list {
                     $$ = new MultipleAsgn19Node(lexer.getPosition(), null, support.assignableLabelOrIdentifier($2, null), $4);
                 }
                 | tSTAR {
                     $$ = new MultipleAsgn19Node(lexer.getPosition(), null, new StarNode(lexer.getPosition()), null);
                 }
                 | tSTAR ',' f_marg_list {
                     $$ = new MultipleAsgn19Node(support.getPosition($3), null, null, $3);
                 }
 
 block_args_tail : f_block_kwarg ',' f_kwrest opt_f_block_arg {
                     $$ = support.new_args_tail($1.getPosition(), $1, $3, $4);
                 }
                 | f_block_kwarg opt_f_block_arg {
                     $$ = support.new_args_tail($1.getPosition(), $1, null, $2);
                 }
                 | f_kwrest opt_f_block_arg {
                     $$ = support.new_args_tail(lexer.getPosition(), null, $1, $2);
                 }
                 | f_block_arg {
                     $$ = support.new_args_tail($1.getPosition(), null, null, $1);
                 }
 
 opt_block_args_tail : ',' block_args_tail {
                     $$ = $2;
                 }
                 | /* none */ {
                     $$ = support.new_args_tail(lexer.getPosition(), null, null, null);
                 }
 
 // [!null]
 block_param     : f_arg ',' f_block_optarg ',' f_rest_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, null, $6);
                 }
                 | f_arg ',' f_block_optarg ',' f_rest_arg ',' f_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, $7, $8);
                 }
                 | f_arg ',' f_block_optarg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, null, $4);
                 }
                 | f_arg ',' f_block_optarg ',' f_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, $5, $6);
                 }
                 | f_arg ',' f_rest_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, null, $4);
                 }
                 | f_arg ',' {
                     RestArgNode rest = new UnnamedRestArgNode($1.getPosition(), null, support.getCurrentScope().addVariable("*"));
                     $$ = support.new_args($1.getPosition(), $1, null, rest, null, (ArgsTailHolder) null);
                 }
                 | f_arg ',' f_rest_arg ',' f_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, $5, $6);
                 }
                 | f_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, null, null, null, $2);
                 }
                 | f_block_optarg ',' f_rest_arg opt_block_args_tail {
                     $$ = support.new_args(support.getPosition($1), null, $1, $3, null, $4);
                 }
                 | f_block_optarg ',' f_rest_arg ',' f_arg opt_block_args_tail {
                     $$ = support.new_args(support.getPosition($1), null, $1, $3, $5, $6);
                 }
                 | f_block_optarg opt_block_args_tail {
                     $$ = support.new_args(support.getPosition($1), null, $1, null, null, $2);
                 }
                 | f_block_optarg ',' f_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), null, $1, null, $3, $4);
                 }
                 | f_rest_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), null, null, $1, null, $2);
                 }
                 | f_rest_arg ',' f_arg opt_block_args_tail {
                     $$ = support.new_args($1.getPosition(), null, null, $1, $3, $4);
                 }
                 | block_args_tail {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, $1);
                 }
 
 opt_block_param : none {
     // was $$ = null;
                     $$ = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                 }
                 | block_param_def {
                     lexer.commandStart = true;
                     $$ = $1;
                 }
 
 block_param_def : tPIPE opt_bv_decl tPIPE {
                     $$ = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                 }
                 | tOROP {
                     $$ = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                 }
                 | tPIPE block_param opt_bv_decl tPIPE {
                     $$ = $2;
                 }
 
 // shadowed block variables....
 opt_bv_decl     : opt_nl {
                     $$ = null;
                 }
                 | opt_nl ';' bv_decls opt_nl {
                     $$ = null;
                 }
 
 // ENEBO: This is confusing...
 bv_decls        : bvar {
                     $$ = null;
                 }
                 | bv_decls ',' bvar {
                     $$ = null;
                 }
 
 bvar            : tIDENTIFIER {
                     support.new_bv($1);
                 }
                 | f_bad_arg {
                     $$ = null;
                 }
 
 lambda          : /* none */  {
                     support.pushBlockScope();
                     $$ = lexer.getLeftParenBegin();
                     lexer.setLeftParenBegin(lexer.incrementParenNest());
                 } f_larglist lambda_body {
                     $$ = new LambdaNode($2.getPosition(), $2, $3, support.getCurrentScope());
                     support.popCurrentScope();
                     lexer.setLeftParenBegin($<Integer>1);
                 }
 
 f_larglist      : tLPAREN2 f_args opt_bv_decl tRPAREN {
                     $$ = $2;
                 }
                 | f_args {
                     $$ = $1;
                 }
 
 lambda_body     : tLAMBEG compstmt tRCURLY {
                     $$ = $2;
                 }
                 | kDO_LAMBDA compstmt kEND {
                     $$ = $2;
                 }
 
 do_block        : kDO_BLOCK {
                     support.pushBlockScope();
                 } opt_block_param compstmt kEND {
                     $$ = new IterNode($1, $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
 
   // JRUBY-2326 and GH #305 both end up hitting this production whereas in
   // MRI these do not.  I have never isolated the cause but I can work around
   // the individual reported problems with a few extra conditionals in this
   // first production
 block_call      : command do_block {
                     // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
                     if ($1 instanceof YieldNode) {
                         throw new SyntaxException(PID.BLOCK_GIVEN_TO_YIELD, $1.getPosition(), lexer.getCurrentLine(), "block given to yield");
                     }
                     if ($1 instanceof BlockAcceptingNode && $<BlockAcceptingNode>1.getIterNode() instanceof BlockPassNode) {
                         throw new SyntaxException(PID.BLOCK_ARG_AND_BLOCK_GIVEN, $1.getPosition(), lexer.getCurrentLine(), "Both block arg and actual block given.");
                     }
                     if ($1 instanceof NonLocalControlFlowNode) {
                         ((BlockAcceptingNode) $<NonLocalControlFlowNode>1.getValueNode()).setIterNode($2);
                     } else {
                         $<BlockAcceptingNode>1.setIterNode($2);
                     }
                     $$ = $1;
                     $<Node>$.setPosition($1.getPosition());
                 }
                 | block_call dot_or_colon operation2 opt_paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | block_call dot_or_colon operation2 opt_paren_args brace_block {
                     $$ = support.new_call($1, $3, $4, $5);
                 }
                 | block_call dot_or_colon operation2 command_args do_block {
                     $$ = support.new_call($1, $3, $4, $5);
                 }
 
 // [!null]
 method_call     : fcall paren_args {
                     support.frobnicate_fcall_args($1, $2, null);
                     $$ = $1;
                 }
                 | primary_value tDOT operation2 opt_paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tCOLON2 operation2 paren_args {
                     $$ = support.new_call($1, $3, $4, null);
                 }
                 | primary_value tCOLON2 operation3 {
                     $$ = support.new_call($1, $3, null, null);
                 }
                 | primary_value tDOT paren_args {
                     $$ = support.new_call($1, "call", $3, null);
                 }
                 | primary_value tCOLON2 paren_args {
                     $$ = support.new_call($1, "call", $3, null);
                 }
                 | kSUPER paren_args {
                     $$ = support.new_super($1, $2);
                 }
                 | kSUPER {
                     $$ = new ZSuperNode($1);
                 }
                 | primary_value '[' opt_call_args rbracket {
                     if ($1 instanceof SelfNode) {
                         $$ = support.new_fcall("[]");
                         support.frobnicate_fcall_args($<FCallNode>$, $3, null);
                     } else {
                         $$ = support.new_call($1, "[]", $3, null);
                     }
                 }
 
 brace_block     : tLCURLY {
                     support.pushBlockScope();
                 } opt_block_param compstmt tRCURLY {
                     $$ = new IterNode($1, $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
                 | kDO {
                     support.pushBlockScope();
                 } opt_block_param compstmt kEND {
                     $$ = new IterNode($1, $3, $4, support.getCurrentScope());
                     support.popCurrentScope();
                 }
 
 case_body       : kWHEN args then compstmt cases {
                     $$ = support.newWhenNode($1, $2, $4, $5);
                 }
 
 cases           : opt_else | case_body
 
 opt_rescue      : kRESCUE exc_list exc_var then compstmt opt_rescue {
                     Node node;
                     if ($3 != null) {
                         node = support.appendToBlock(support.node_assign($3, new GlobalVarNode($1, "$!")), $5);
                         if ($5 != null) {
                             node.setPosition($1);
                         }
                     } else {
                         node = $5;
                     }
                     Node body = node == null ? NilImplicitNode.NIL : node;
                     $$ = new RescueBodyNode($1, $2, body, $6);
                 }
                 | { 
                     $$ = null; 
                 }
 
 exc_list        : arg_value {
                     $$ = support.newArrayNode($1.getPosition(), $1);
                 }
                 | mrhs {
                     $$ = support.splat_array($1);
                     if ($$ == null) $$ = $1; // ArgsCat or ArgsPush
                 }
                 | none
 
 exc_var         : tASSOC lhs {
                     $$ = $2;
                 }
                 | none
 
 opt_ensure      : kENSURE compstmt {
                     $$ = $2;
                 }
                 | none
 
 literal         : numeric
                 | symbol {
-                    // FIXME: We may be intern'ing more than once.
-                    $$ = new SymbolNode(lexer.getPosition(), $1.intern());
+                    $$ = new SymbolNode(lexer.getPosition(), new ByteList($1.getBytes(), lexer.getEncoding()));
                 }
                 | dsym
 
 strings         : string {
                     $$ = $1 instanceof EvStrNode ? new DStrNode($1.getPosition(), lexer.getEncoding()).add($1) : $1;
                     /*
                     NODE *node = $1;
                     if (!node) {
                         node = NEW_STR(STR_NEW0());
                     } else {
                         node = evstr2dstr(node);
                     }
                     $$ = node;
                     */
                 }
 
 // [!null]
 string          : tCHAR {
                     ByteList aChar = ByteList.create($1);
                     aChar.setEncoding(lexer.getEncoding());
                     $$ = lexer.createStrNode(lexer.getPosition(), aChar, 0);
                 }
                 | string1 {
                     $$ = $1;
                 }
                 | string string1 {
                     $$ = support.literal_concat($1.getPosition(), $1, $2);
                 }
 
 string1         : tSTRING_BEG string_contents tSTRING_END {
                     $$ = $2;
                 }
 
 xstring         : tXSTRING_BEG xstring_contents tSTRING_END {
                     ISourcePosition position = support.getPosition($2);
 
                     if ($2 == null) {
                         $$ = new XStrNode(position, null);
                     } else if ($2 instanceof StrNode) {
                         $$ = new XStrNode(position, (ByteList) $<StrNode>2.getValue().clone());
                     } else if ($2 instanceof DStrNode) {
                         $$ = new DXStrNode(position, $<DStrNode>2);
 
                         $<Node>$.setPosition(position);
                     } else {
                         $$ = new DXStrNode(position).add($2);
                     }
                 }
 
 regexp          : tREGEXP_BEG regexp_contents tREGEXP_END {
                     $$ = support.newRegexpNode(support.getPosition($2), $2, (RegexpNode) $3);
                 }
 
 words           : tWORDS_BEG ' ' tSTRING_END {
                     $$ = new ZArrayNode(lexer.getPosition());
                 }
                 | tWORDS_BEG word_list tSTRING_END {
                     $$ = $2;
                 }
 
 word_list       : /* none */ {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | word_list word ' ' {
                      $$ = $1.add($2 instanceof EvStrNode ? new DStrNode($1.getPosition(), lexer.getEncoding()).add($2) : $2);
                 }
 
 word            : string_content
                 | word string_content {
                      $$ = support.literal_concat(support.getPosition($1), $1, $2);
                 }
 
 symbols         : tSYMBOLS_BEG ' ' tSTRING_END {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | tSYMBOLS_BEG symbol_list tSTRING_END {
                     $$ = $2;
                 }
 
 symbol_list     : /* none */ {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | symbol_list word ' ' {
                     $$ = $1.add($2 instanceof EvStrNode ? new DSymbolNode($1.getPosition()).add($2) : support.asSymbol($1.getPosition(), $2));
                 }
 
 qwords          : tQWORDS_BEG ' ' tSTRING_END {
                      $$ = new ZArrayNode(lexer.getPosition());
                 }
                 | tQWORDS_BEG qword_list tSTRING_END {
                     $$ = $2;
                 }
 
 qsymbols        : tQSYMBOLS_BEG ' ' tSTRING_END {
                     $$ = new ZArrayNode(lexer.getPosition());
                 }
                 | tQSYMBOLS_BEG qsym_list tSTRING_END {
                     $$ = $2;
                 }
 
 
 qword_list      : /* none */ {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | qword_list tSTRING_CONTENT ' ' {
                     $$ = $1.add($2);
                 }
 
 qsym_list      : /* none */ {
                     $$ = new ArrayNode(lexer.getPosition());
                 }
                 | qsym_list tSTRING_CONTENT ' ' {
                     $$ = $1.add(support.asSymbol($1.getPosition(), $2));
                 }
 
 string_contents : /* none */ {
                     ByteList aChar = ByteList.create("");
                     aChar.setEncoding(lexer.getEncoding());
                     $$ = lexer.createStrNode(lexer.getPosition(), aChar, 0);
                 }
                 | string_contents string_content {
                     $$ = support.literal_concat($1.getPosition(), $1, $2);
                 }
 
 xstring_contents: /* none */ {
                     $$ = null;
                 }
                 | xstring_contents string_content {
                     $$ = support.literal_concat(support.getPosition($1), $1, $2);
                 }
 
 regexp_contents :  /* none */ {
                     $$ = null;
                 }
                 | regexp_contents string_content {
     // FIXME: mri is different here.
                     $$ = support.literal_concat(support.getPosition($1), $1, $2);
                 }
 
 string_content  : tSTRING_CONTENT {
                     $$ = $1;
                 }
                 | tSTRING_DVAR {
                     $$ = lexer.getStrTerm();
                     lexer.setStrTerm(null);
                     lexer.setState(LexState.EXPR_BEG);
                 } string_dvar {
                     lexer.setStrTerm($<StrTerm>2);
                     $$ = new EvStrNode(support.getPosition($3), $3);
                 }
                 | tSTRING_DBEG {
                    $$ = lexer.getStrTerm();
                    lexer.getConditionState().stop();
                    lexer.getCmdArgumentState().stop();
                    lexer.setStrTerm(null);
                    lexer.setState(LexState.EXPR_BEG);
                 } compstmt tRCURLY {
                    lexer.getConditionState().restart();
                    lexer.getCmdArgumentState().restart();
                    lexer.setStrTerm($<StrTerm>2);
 
                    $$ = support.newEvStrNode(support.getPosition($3), $3);
                 }
 
 string_dvar     : tGVAR {
                      $$ = new GlobalVarNode(lexer.getPosition(), $1);
                 }
                 | tIVAR {
                      $$ = new InstVarNode(lexer.getPosition(), $1);
                 }
                 | tCVAR {
                      $$ = new ClassVarNode(lexer.getPosition(), $1);
                 }
                 | backref
 
 // String:symbol
 symbol          : tSYMBEG sym {
                      lexer.setState(LexState.EXPR_END);
                      $$ = $2;
                 }
 
 // String:symbol
 sym             : fname | tIVAR | tGVAR | tCVAR
 
 dsym            : tSYMBEG xstring_contents tSTRING_END {
                      lexer.setState(LexState.EXPR_END);
 
                      // DStrNode: :"some text #{some expression}"
                      // StrNode: :"some text"
                      // EvStrNode :"#{some expression}"
                      // Ruby 1.9 allows empty strings as symbols
                      if ($2 == null) {
-                         $$ = new SymbolNode(lexer.getPosition(), "");
+                         $$ = new SymbolNode(lexer.getPosition(), new ByteList(new byte[0], lexer.getEncoding()));
                      } else if ($2 instanceof DStrNode) {
                          $$ = new DSymbolNode($2.getPosition(), $<DStrNode>2);
                      } else if ($2 instanceof StrNode) {
-                         $$ = new SymbolNode($2.getPosition(), $<StrNode>2.getValue().toString().intern());
+                         $$ = new SymbolNode($2.getPosition(), $<StrNode>2.getValue());
                      } else {
                          $$ = new DSymbolNode($2.getPosition());
                          $<DSymbolNode>$.add($2);
                      }
                 }
 
  numeric        : simple_numeric {
                     $$ = $1;  
                 }
                 | tUMINUS_NUM simple_numeric %prec tLOWEST {
                      $$ = support.negateNumeric($2.getPosition(), $2);
                 }
 
 simple_numeric  : tINTEGER {
                     $$ = $1;
                 }
                 | tFLOAT {
                      $$ = $1;
                 }
                 | tRATIONAL {
                      $$ = $1;
                 }
                 | tIMAGINARY {
                      $$ = $1;
                 }
 
 // [!null]
 var_ref         : /*mri:user_variable*/ tIDENTIFIER {
                     $$ = support.declareIdentifier($1);
                 }
                 | tIVAR {
                     $$ = new InstVarNode(lexer.getPosition(), $1);
                 }
                 | tGVAR {
                     $$ = new GlobalVarNode(lexer.getPosition(), $1);
                 }
                 | tCONSTANT {
                     $$ = new ConstNode(lexer.getPosition(), $1);
                 }
                 | tCVAR {
                     $$ = new ClassVarNode(lexer.getPosition(), $1);
                 } /*mri:user_variable*/
                 | /*mri:keyword_variable*/ kNIL { 
                     $$ = new NilNode(lexer.getPosition());
                 }
                 | kSELF {
                     $$ = new SelfNode(lexer.getPosition());
                 }
                 | kTRUE { 
                     $$ = new TrueNode(lexer.getPosition());
                 }
                 | kFALSE {
                     $$ = new FalseNode(lexer.getPosition());
                 }
                 | k__FILE__ {
                     $$ = new FileNode(lexer.getPosition(), new ByteList(lexer.getPosition().getFile().getBytes(),
                     support.getConfiguration().getRuntime().getEncodingService().getLocaleEncoding()));
                 }
                 | k__LINE__ {
                     $$ = new FixnumNode(lexer.getPosition(), lexer.tokline.getLine()+1);
                 }
                 | k__ENCODING__ {
                     $$ = new EncodingNode(lexer.getPosition(), lexer.getEncoding());
                 } /*mri:keyword_variable*/
 
 // [!null]
 var_lhs         : /*mri:user_variable*/ tIDENTIFIER {
                     $$ = support.assignableLabelOrIdentifier($1, null);
                 }
                 | tIVAR {
                    $$ = new InstAsgnNode(lexer.getPosition(), $1, NilImplicitNode.NIL);
                 }
                 | tGVAR {
                    $$ = new GlobalAsgnNode(lexer.getPosition(), $1, NilImplicitNode.NIL);
                 }
                 | tCONSTANT {
                     if (support.isInDef() || support.isInSingle()) support.compile_error("dynamic constant assignment");
 
                     $$ = new ConstDeclNode(lexer.getPosition(), $1, null, NilImplicitNode.NIL);
                 }
                 | tCVAR {
                     $$ = new ClassVarAsgnNode(lexer.getPosition(), $1, NilImplicitNode.NIL);
                 } /*mri:user_variable*/
                 | /*mri:keyword_variable*/ kNIL {
                     support.compile_error("Can't assign to nil");
                     $$ = null;
                 }
                 | kSELF {
                     support.compile_error("Can't change the value of self");
                     $$ = null;
                 }
                 | kTRUE {
                     support.compile_error("Can't assign to true");
                     $$ = null;
                 }
                 | kFALSE {
                     support.compile_error("Can't assign to false");
                     $$ = null;
                 }
                 | k__FILE__ {
                     support.compile_error("Can't assign to __FILE__");
                     $$ = null;
                 }
                 | k__LINE__ {
                     support.compile_error("Can't assign to __LINE__");
                     $$ = null;
                 }
                 | k__ENCODING__ {
                     support.compile_error("Can't assign to __ENCODING__");
                     $$ = null;
                 } /*mri:keyword_variable*/
 
 // [!null]
 backref         : tNTH_REF {
                     $$ = $1;
                 }
                 | tBACK_REF {
                     $$ = $1;
                 }
 
 superclass      : term {
                     $$ = null;
                 }
                 | tLT {
                    lexer.setState(LexState.EXPR_BEG);
                 } expr_value term {
                     $$ = $3;
                 }
                 | error term {
                    $$ = null;
                 }
 
 // [!null]
 // ENEBO: Look at command_start stuff I am ripping out
 f_arglist       : tLPAREN2 f_args rparen {
                     $$ = $2;
                     lexer.setState(LexState.EXPR_BEG);
                     lexer.commandStart = true;
                 }
                 | f_args term {
                     $$ = $1;
                     lexer.setState(LexState.EXPR_BEG);
                     lexer.commandStart = true;
                 }
 
 
 args_tail       : f_kwarg ',' f_kwrest opt_f_block_arg {
                     $$ = support.new_args_tail($1.getPosition(), $1, $3, $4);
                 }
                 | f_kwarg opt_f_block_arg {
                     $$ = support.new_args_tail($1.getPosition(), $1, null, $2);
                 }
                 | f_kwrest opt_f_block_arg {
                     $$ = support.new_args_tail(lexer.getPosition(), null, $1, $2);
                 }
                 | f_block_arg {
                     $$ = support.new_args_tail($1.getPosition(), null, null, $1);
                 }
 
 opt_args_tail   : ',' args_tail {
                     $$ = $2;
                 }
                 | /* none */ {
                     $$ = support.new_args_tail(lexer.getPosition(), null, null, null);
                 }
 
 // [!null]
 f_args          : f_arg ',' f_optarg ',' f_rest_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, null, $6);
                 }
                 | f_arg ',' f_optarg ',' f_rest_arg ',' f_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, $3, $5, $7, $8);
                 }
                 | f_arg ',' f_optarg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, null, $4);
                 }
                 | f_arg ',' f_optarg ',' f_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, $3, null, $5, $6);
                 }
                 | f_arg ',' f_rest_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, null, $4);
                 }
                 | f_arg ',' f_rest_arg ',' f_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, null, $3, $5, $6);
                 }
                 | f_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), $1, null, null, null, $2);
                 }
                 | f_optarg ',' f_rest_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), null, $1, $3, null, $4);
                 }
                 | f_optarg ',' f_rest_arg ',' f_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), null, $1, $3, $5, $6);
                 }
                 | f_optarg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), null, $1, null, null, $2);
                 }
                 | f_optarg ',' f_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), null, $1, null, $3, $4);
                 }
                 | f_rest_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), null, null, $1, null, $2);
                 }
                 | f_rest_arg ',' f_arg opt_args_tail {
                     $$ = support.new_args($1.getPosition(), null, null, $1, $3, $4);
                 }
                 | args_tail {
                     $$ = support.new_args($1.getPosition(), null, null, null, null, $1);
                 }
                 | /* none */ {
                     $$ = support.new_args(lexer.getPosition(), null, null, null, null, (ArgsTailHolder) null);
                 }
 
 f_bad_arg       : tCONSTANT {
                     support.yyerror("formal argument cannot be a constant");
                 }
                 | tIVAR {
                     support.yyerror("formal argument cannot be an instance variable");
                 }
                 | tGVAR {
                     support.yyerror("formal argument cannot be a global variable");
                 }
                 | tCVAR {
                     support.yyerror("formal argument cannot be a class variable");
                 }
 
 // String:f_norm_arg [!null]
 f_norm_arg      : f_bad_arg
                 | tIDENTIFIER {
                     $$ = support.formal_argument($1);
                 }
 
 f_arg_item      : f_norm_arg {
                     $$ = support.arg_var($1);
                 }
                 | tLPAREN f_margs rparen {
                     $$ = $2;
                     /*            {
             ID tid = internal_id();
             arg_var(tid);
             if (dyna_in_block()) {
                 $2->nd_value = NEW_DVAR(tid);
             }
             else {
                 $2->nd_value = NEW_LVAR(tid);
             }
             $$ = NEW_ARGS_AUX(tid, 1);
             $$->nd_next = $2;*/
                 }
 
 // [!null]
 f_arg           : f_arg_item {
                     $$ = new ArrayNode(lexer.getPosition(), $1);
                 }
                 | f_arg ',' f_arg_item {
                     $1.add($3);
                     $$ = $1;
                 }
 
 f_label 	: tLABEL {
                     support.arg_var(support.formal_argument($1));
                     $$ = $1;
                 }
 
 f_kw            : f_label arg_value {
                     $$ = support.keyword_arg($2.getPosition(), support.assignableLabelOrIdentifier($1, $2));
                 }
                 | f_label {
                     $$ = support.keyword_arg(lexer.getPosition(), support.assignableLabelOrIdentifier($1, new RequiredKeywordArgumentValueNode()));
                 }
 
 f_block_kw      : f_label primary_value {
                     $$ = support.keyword_arg(support.getPosition($2), support.assignableLabelOrIdentifier($1, $2));
                 }
                 | f_label {
                     $$ = support.keyword_arg(lexer.getPosition(), support.assignableLabelOrIdentifier($1, new RequiredKeywordArgumentValueNode()));
                 }
              
 
 f_block_kwarg   : f_block_kw {
                     $$ = new ArrayNode($1.getPosition(), $1);
                 }
                 | f_block_kwarg ',' f_block_kw {
                     $$ = $1.add($3);
                 }
 
 f_kwarg         : f_kw {
                     $$ = new ArrayNode($1.getPosition(), $1);
                 }
                 | f_kwarg ',' f_kw {
                     $$ = $1.add($3);
                 }
 
 kwrest_mark     : tPOW {
                     $$ = $1;
                 }
                 | tDSTAR {
                     $$ = $1;
                 }
 
 f_kwrest        : kwrest_mark tIDENTIFIER {
                     support.shadowing_lvar($2);
                     $$ = $2;
                 }
                 | kwrest_mark {
                     $$ = support.internalId();
                 }
 
 f_opt           : f_norm_arg '=' arg_value {
                     support.arg_var($1);
                     $$ = new OptArgNode(support.getPosition($3), support.assignableLabelOrIdentifier($1, $3));
                 }
 
 f_block_opt     : tIDENTIFIER '=' primary_value {
                     support.arg_var(support.formal_argument($1));
                     $$ = new OptArgNode(support.getPosition($3), support.assignableLabelOrIdentifier($1, $3));
                 }
 
 f_block_optarg  : f_block_opt {
                     $$ = new BlockNode($1.getPosition()).add($1);
                 }
                 | f_block_optarg ',' f_block_opt {
                     $$ = support.appendToBlock($1, $3);
                 }
 
 f_optarg        : f_opt {
                     $$ = new BlockNode($1.getPosition()).add($1);
                 }
                 | f_optarg ',' f_opt {
                     $$ = support.appendToBlock($1, $3);
                 }
 
 restarg_mark    : tSTAR2 | tSTAR
 
 // [!null]
 f_rest_arg      : restarg_mark tIDENTIFIER {
                     if (!support.is_local_id($2)) {
                         support.yyerror("rest argument must be local variable");
                     }
                     
                     $$ = new RestArgNode(support.arg_var(support.shadowing_lvar($2)));
                 }
                 | restarg_mark {
                     $$ = new UnnamedRestArgNode(lexer.getPosition(), "", support.getCurrentScope().addVariable("*"));
                 }
 
 // [!null]
 blkarg_mark     : tAMPER2 | tAMPER
 
 // f_block_arg - Block argument def for function (foo(&block)) [!null]
 f_block_arg     : blkarg_mark tIDENTIFIER {
                     if (!support.is_local_id($2)) {
                         support.yyerror("block argument must be local variable");
                     }
                     
                     $$ = new BlockArgNode(support.arg_var(support.shadowing_lvar($2)));
                 }
 
 opt_f_block_arg : ',' f_block_arg {
                     $$ = $2;
                 }
                 | /* none */ {
                     $$ = null;
                 }
 
 singleton       : var_ref {
                     if (!($1 instanceof SelfNode)) {
                         support.checkExpression($1);
                     }
                     $$ = $1;
                 }
                 | tLPAREN2 {
                     lexer.setState(LexState.EXPR_BEG);
                 } expr rparen {
                     if ($3 == null) {
                         support.yyerror("can't define single method for ().");
                     } else if ($3 instanceof ILiteralNode) {
                         support.yyerror("can't define single method for literals.");
                     }
                     support.checkExpression($3);
                     $$ = $3;
                 }
 
 // HashNode: [!null]
 assoc_list      : none {
                     $$ = new HashNode(lexer.getPosition());
                 }
                 | assocs trailer {
                     $$ = $1;
                 }
 
 // [!null]
 assocs          : assoc {
                     $$ = new HashNode(lexer.getPosition(), $1);
                 }
                 | assocs ',' assoc {
                     $$ = $1.add($3);
                 }
 
 // Cons: [!null]
 assoc           : arg_value tASSOC arg_value {
                     $$ = new KeyValuePair<Node,Node>($1, $3);
                 }
                 | tLABEL arg_value {
-                    $$ = new KeyValuePair<Node,Node>(new SymbolNode(support.getPosition($2), $1), $2);
+                    SymbolNode label = new SymbolNode(support.getPosition($2), new ByteList($1.getBytes(), lexer.getEncoding()));
+                    $$ = new KeyValuePair<Node,Node>(label, $2);
                 }
                 | tDSTAR arg_value {
                     $$ = new KeyValuePair<Node,Node>(null, $2);
                 }
 
 operation       : tIDENTIFIER | tCONSTANT | tFID
 operation2      : tIDENTIFIER | tCONSTANT | tFID | op
 operation3      : tIDENTIFIER | tFID | op
 dot_or_colon    : tDOT | tCOLON2
 opt_terms       : /* none */ | terms
 opt_nl          : /* none */ | '\n'
 rparen          : opt_nl tRPAREN {
                     $$ = $2;
                 }
 rbracket        : opt_nl tRBRACK {
                     $$ = $2;
                 }
 trailer         : /* none */ | '\n' | ','
 
 term            : ';'
                 | '\n'
 
 terms           : term
                 | terms ';'
 
 none            : /* none */ {
                       $$ = null;
                 }
 
 none_block_pass : /* none */ {  
                   $$ = null;
                 }
 
 %%
 
     /** The parse method use an lexer stream and parse it to an AST node 
      * structure
      */
     public RubyParserResult parse(ParserConfiguration configuration, LexerSource source) throws IOException {
         support.reset();
         support.setConfiguration(configuration);
         support.setResult(new RubyParserResult());
         
         lexer.reset();
         lexer.setSource(source);
         lexer.setEncoding(configuration.getDefaultEncoding());
 
         yyparse(lexer, configuration.isDebug() ? new YYDebug() : null);
         
         return support.getResult();
     }
 }
