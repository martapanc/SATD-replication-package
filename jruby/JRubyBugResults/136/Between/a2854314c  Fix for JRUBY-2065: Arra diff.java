diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 16d698d359..0e6a75c29b 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -862,1597 +862,1598 @@ public class RubyArray extends RubyObject implements List {
         return makeShared(begin + (int) beg, (int) len, getMetaClass(), false);
     }
 
     /** rb_ary_length
      *
      */
     @JRubyMethod(name = "length", alias = "size")
     public RubyFixnum length() {
         return getRuntime().newFixnum(realLength);
     }
 
     /** rb_ary_push - specialized rb_ary_store 
      *
      */
     @JRubyMethod(name = "<<", required = 1)
     public RubyArray append(IRubyObject item) {
         modify();
         
         if (realLength == values.length) {
         if (realLength == Integer.MAX_VALUE) throw getRuntime().newArgumentError("index too big");
             
             long newLength = values.length + (values.length >> 1);
             if ( newLength > Integer.MAX_VALUE ) {
                 newLength = Integer.MAX_VALUE;
             }else if ( newLength < ARRAY_DEFAULT_SIZE ) {
                 newLength = ARRAY_DEFAULT_SIZE;
             }
 
             realloc((int) newLength);
         }
         
         values[realLength++] = item;
         return this;
     }
 
     /** rb_ary_push_m
      * FIXME: Whis is this named "push_m"?
      */
     @JRubyMethod(name = "push", rest = true)
     public RubyArray push_m(IRubyObject[] items) {
         for (int i = 0; i < items.length; i++) {
             append(items[i]);
         }
         
         return this;
     }
 
     /** rb_ary_pop
      *
      */
     @JRubyMethod(name = "pop")
     public IRubyObject pop() {
         modifyCheck();
         
         if (realLength == 0) return getRuntime().getNil();
 
         if (!isShared) {
             int index = begin + --realLength;
             IRubyObject obj = values[index];
             values[index] = null;
             return obj;
         } 
 
         return values[begin + --realLength];
     }
 
     /** rb_ary_shift
      *
      */
     @JRubyMethod(name = "shift")
     public IRubyObject shift() {
         modify();
 
         if (realLength == 0) return getRuntime().getNil();
 
         IRubyObject obj = values[begin];
         values[begin] = null;
 
         isShared = true;
 
         begin++;
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_unshift
      *
      */
     public RubyArray unshift(IRubyObject item) {
         modify();
 
         if (realLength == values.length) {
             int newLength = values.length >> 1;
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += values.length;
             realloc(newLength);
         }
         System.arraycopy(values, 0, values, 1, realLength);
 
         realLength++;
         values[0] = item;
 
         return this;
     }
 
     /** rb_ary_unshift_m
      *
      */
     @JRubyMethod(name = "unshift", rest = true)
     public RubyArray unshift_m(IRubyObject[] items) {
         long len = realLength;
 
         if (items.length == 0) return this;
 
         store(len + items.length - 1, getRuntime().getNil());
 
         // it's safe to use zeroes here since modified by store()
         System.arraycopy(values, 0, values, items.length, (int) len);
         System.arraycopy(items, 0, values, 0, items.length);
         
         return this;
     }
 
     /** rb_ary_includes
      * 
      */
     @JRubyMethod(name = "include?", required = 1)
     public RubyBoolean include_p(IRubyObject item) {
         return getRuntime().newBoolean(includes(item));
     }
 
     /** rb_ary_frozen_p
      *
      */
     @JRubyMethod(name = "frozen?")
     public RubyBoolean frozen_p() {
         return getRuntime().newBoolean(isFrozen() || (flags & TMPLOCK_ARR_F) != 0);
     }
 
     /** rb_ary_aref
      */
     @JRubyMethod(name = {"[]", "slice"}, required = 1, optional = 1)
     public IRubyObject aref(IRubyObject[] args) {
         long beg, len;
 
         if(args.length == 1) {
             if (args[0] instanceof RubyFixnum) return entry(((RubyFixnum)args[0]).getLongValue());
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
             
             long[] beglen;
             if (!(args[0] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[0]).begLen(realLength, 0)) == null) {
                 return getRuntime().getNil();
             } else {
                 beg = beglen[0];
                 len = beglen[1];
                 return subseq(beg, len);
             }
 
             return entry(RubyNumeric.num2long(args[0]));            
         }        
 
         if (args.length == 2) {
             if (args[0] instanceof RubySymbol) {
                 throw getRuntime().newTypeError("Symbol as array index");
             }
             beg = RubyNumeric.num2long(args[0]);
             len = RubyNumeric.num2long(args[1]);
 
             if (beg < 0) beg += realLength;
 
             return subseq(beg, len);
         }
         
         return null;
     }
 
     /** rb_ary_aset
      *
      */
     @JRubyMethod(name = "[]=", required = 2, optional = 1)
     public IRubyObject aset(IRubyObject[] args) {
         if (args.length == 2) {
         if (args[0] instanceof RubyFixnum) {
                 store(((RubyFixnum)args[0]).getLongValue(), args[1]);
             return args[1];
         }
         if (args[0] instanceof RubyRange) {
             long[] beglen = ((RubyRange) args[0]).begLen(realLength, 1);
             splice(beglen[0], beglen[1], args[1]);
             return args[1];
         }
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
 
         store(RubyNumeric.num2long(args[0]), args[1]);
         return args[1];
     }
 
         if (args.length == 3) {
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
             if (args[1] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as subarray length");
 
             splice(RubyNumeric.num2long(args[0]), RubyNumeric.num2long(args[1]), args[2]);
             return args[2];
         }
 
         throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 2)");
     }
 
     /** rb_ary_at
      *
      */
     @JRubyMethod(name = "at", required = 1)
     public IRubyObject at(IRubyObject pos) {
         return entry(RubyNumeric.num2long(pos));
     }
 
 	/** rb_ary_concat
      *
      */
     @JRubyMethod(name = "concat", required = 1)
     public RubyArray concat(IRubyObject obj) {
         RubyArray ary = obj.convertToArray();
         
         if (ary.realLength > 0) splice(realLength, 0, ary);
 
         return this;
     }
 
     /** inspect_ary
      * 
      */
     private IRubyObject inspectAry() {
         StringBuffer buffer = new StringBuffer("[");
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         boolean tainted = isTaint();
 
         for (int i = 0; i < realLength; i++) {
             RubyString s = RubyString.objAsString(values[begin + i].callMethod(context, "inspect"));
 
             if (s.isTaint()) tainted = true;
 
             if (i > 0) buffer.append(", ");
 
             buffer.append(s.toString());
         }
         buffer.append("]");
 
         RubyString str = runtime.newString(buffer.toString());
         if (tainted) str.setTaint(true);
 
         return str;
     }
 
     /** rb_ary_inspect
     *
     */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         if (realLength == 0) return getRuntime().newString("[]");
         if (getRuntime().isInspecting(this)) return  getRuntime().newString("[...]");
 
         try {
             getRuntime().registerInspecting(this);
             return inspectAry();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_ary_first
      *
      */
     @JRubyMethod(name = "first", optional = 1)
     public IRubyObject first(IRubyObject[] args) {
     	if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin];
         } 
             
         long n = RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
     	}
     	
         return makeShared(begin, (int) n, getRuntime().getArray());
     }
 
     /** rb_ary_last
      *
      */
     @JRubyMethod(name = "last", optional = 1)
     public IRubyObject last(IRubyObject[] args) {
         if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin + realLength - 1];
         } 
             
         long n = RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         return makeShared(begin + realLength - (int) n, (int) n, getRuntime().getArray());
     }
 
     /** rb_ary_each
      *
      */
     @JRubyMethod(name = "each", backtrace = true)
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             block.yield(context, values[begin + i]);
         }
         return this;
     }
 
     /** rb_ary_each_index
      *
      */
     @JRubyMethod(name = "each_index", backtrace = true)
     public IRubyObject each_index(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             block.yield(context, runtime.newFixnum(i));
         }
         return this;
     }
 
     /** rb_ary_reverse_each
      *
      */
     @JRubyMethod(name = "reverse_each", backtrace = true)
     public IRubyObject reverse_each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         int len = realLength;
         
         while(len-- > 0) {
             block.yield(context, values[begin + len]);
             
             if (realLength < len) len = realLength;
         }
         
         return this;
     }
 
     private IRubyObject inspectJoin(RubyArray tmp, IRubyObject sep) {
         Ruby runtime = getRuntime();
 
         // If already inspecting, there is no need to register/unregister again.
         if (runtime.isInspecting(this)) {
             return tmp.join(sep);
         }
 
         try {
             runtime.registerInspecting(this);
             return tmp.join(sep);
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     /** rb_ary_join
      *
      */
     public RubyString join(IRubyObject sep) {
         if (realLength == 0) return getRuntime().newString("");
 
         boolean taint = isTaint() || sep.isTaint();
 
         long len = 1;
         for (int i = begin; i < begin + realLength; i++) {            
             IRubyObject tmp = values[i].checkStringType();
             len += tmp.isNil() ? 10 : ((RubyString) tmp).getByteList().length();
         }
 
         RubyString strSep = null;
         if (!sep.isNil()) {
             sep = strSep = sep.convertToString();
             len += strSep.getByteList().length() * (realLength - 1);
         }
 
         ByteList buf = new ByteList((int)len);
         Ruby runtime = getRuntime();
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject tmp = values[i];
             if (tmp instanceof RubyString) {
                 // do nothing
             } else if (tmp instanceof RubyArray) {
                 if (runtime.isInspecting(tmp)) {
                     tmp = runtime.newString("[...]");
                 } else {
                     tmp = inspectJoin((RubyArray)tmp, sep);
                 }
             } else {
                 tmp = RubyString.objAsString(tmp);
             }
 
             if (i > begin && !sep.isNil()) buf.append(strSep.getByteList());
 
             buf.append(tmp.asString().getByteList());
             if (tmp.isTaint()) taint = true;
         }
 
         RubyString result = runtime.newString(buf); 
 
         if (taint) result.setTaint(true);
 
         return result;
     }
 
     /** rb_ary_join_m
      *
      */
     @JRubyMethod(name = "join", optional = 1)
     public RubyString join_m(IRubyObject[] args) {
         int argc = args.length;
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
         
         return join(sep);
     }
 
     /** rb_ary_to_a
      *
      */
     @JRubyMethod(name = "to_a")
     public RubyArray to_a() {
         if(getMetaClass() != getRuntime().getArray()) {
             RubyArray dup = new RubyArray(getRuntime(), getRuntime().isObjectSpaceEnabled());
 
             isShared = true;
             dup.isShared = true;
             dup.values = values;
             dup.realLength = realLength; 
             dup.begin = begin;
             
             return dup;
         }        
         return this;
     }
 
     @JRubyMethod(name = "to_ary")
     public IRubyObject to_ary() {
     	return this;
     }
 
     public RubyArray convertToArray() {
         return this;
     }
     
     public IRubyObject checkArrayType(){
         return this;
     }
 
     /** rb_ary_equal
      *
      */
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
 
         if (!(obj instanceof RubyArray)) {
             if (!obj.respondsTo("to_ary")) {
                 return getRuntime().getFalse();
             } else {
                 ThreadContext context = getRuntime().getCurrentContext();
                 
                 return equalInternal(context, obj.callMethod(context, "to_ary"), this);
             }
         }
 
         RubyArray ary = (RubyArray) obj;
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (long i = 0; i < realLength; i++) {
             if (!equalInternal(context, elt(i), ary.elt(i)).isTrue()) return runtime.getFalse();            
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     @JRubyMethod(name = "eql?", required = 1)
     public RubyBoolean eql_p(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
         if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
 
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             if (!eqlInternal(context, elt(i), ary.elt(i))) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_compact_bang
      *
      */
     @JRubyMethod(name = "compact!")
     public IRubyObject compact_bang() {
         modify();
 
         int p = 0;
         int t = 0;
         int end = p + realLength;
 
         while (t < end) {
             if (values[t].isNil()) {
                 t++;
             } else {
                 values[p++] = values[t++];
             }
         }
 
         if (realLength == p) return getRuntime().getNil();
 
         realloc(p);
         realLength = p;
         return this;
     }
 
     /** rb_ary_compact
      *
      */
     @JRubyMethod(name = "compact")
     public IRubyObject compact() {
         RubyArray ary = aryDup();
         ary.compact_bang();
         return ary;
     }
 
     /** rb_ary_empty_p
      *
      */
     @JRubyMethod(name = "empty?")
     public IRubyObject empty_p() {
         return realLength == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_ary_clear
      *
      */
     @JRubyMethod(name = "clear")
     public IRubyObject rb_clear() {
         modifyCheck();
 
         if(isShared) {
             alloc(ARRAY_DEFAULT_SIZE);
             isShared = true;
         } else if (values.length > ARRAY_DEFAULT_SIZE << 1){
             alloc(ARRAY_DEFAULT_SIZE << 1);
         }
 
         begin = 0;
         realLength = 0;
         return this;
     }
 
     /** rb_ary_fill
      *
      */
     @JRubyMethod(name = "fill", optional = 3, frame = true)
     public IRubyObject fill(IRubyObject[] args, Block block) {
         IRubyObject item = null;
         IRubyObject begObj = null;
         IRubyObject lenObj = null;
         int argc = args.length;
 
         if (block.isGiven()) {
             Arity.checkArgumentCount(getRuntime(), args, 0, 2);
             item = null;
         	begObj = argc > 0 ? args[0] : null;
         	lenObj = argc > 1 ? args[1] : null;
         	argc++;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 1, 3);
             item = args[0];
         	begObj = argc > 1 ? args[1] : null;
         	lenObj = argc > 2 ? args[2] : null;
         }
 
         int beg = 0, end = 0, len = 0;
         switch (argc) {
         case 1:
             beg = 0;
             len = realLength;
             break;
         case 2:
             if (begObj instanceof RubyRange) {
                 long[] beglen = ((RubyRange) begObj).begLen(realLength, 1);
                 beg = (int) beglen[0];
                 len = (int) beglen[1];
                 break;
             }
             /* fall through */
         case 3:
             beg = begObj.isNil() ? 0 : RubyNumeric.num2int(begObj);
             if (beg < 0) {
                 beg = realLength + beg;
                 if (beg < 0) beg = 0;
             }
             len = (lenObj == null || lenObj.isNil()) ? realLength - beg : RubyNumeric.num2int(lenObj);
             // TODO: In MRI 1.9, an explicit check for negative length is
             // added here. IndexError is raised when length is negative.
             // See [ruby-core:12953] for more details.
             break;
         }
 
         modify();
 
         end = beg + len;
         // apparently, that's how MRI does overflow check
         if (end < 0) {
             throw getRuntime().newArgumentError("argument too big");
         }
         if (end > realLength) {
             if (end >= values.length) realloc(end);
 
             Arrays.fill(values, realLength, end, getRuntime().getNil());
             realLength = end;
         }
 
         if (block.isGiven()) {
             Ruby runtime = getRuntime();
             ThreadContext context = runtime.getCurrentContext();
             for (int i = beg; i < end; i++) {
                 IRubyObject v = block.yield(context, runtime.newFixnum(i));
                 if (i >= realLength) break;
 
                 values[i] = v;
             }
         } else {
             if(len > 0) Arrays.fill(values, beg, beg + len, item);
         }
         
         return this;
     }
 
     /** rb_ary_index
      *
      */
     @JRubyMethod(name = "index", required = 1)
     public IRubyObject index(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = begin; i < begin + realLength; i++) {
             if (equalInternal(context, values[i], obj).isTrue()) return runtime.newFixnum(i - begin);            
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     @JRubyMethod(name = "rindex", required = 1)
     public IRubyObject rindex(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         int i = realLength;
 
         while (i-- > 0) {
             if (i > realLength) {
                 i = realLength;
                 continue;
             }
             if (equalInternal(context, values[begin + i], obj).isTrue()) return getRuntime().newFixnum(i);
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_indexes
      * 
      */
     @JRubyMethod(name = {"indexes", "indices"}, required = 1, rest = true)
     public IRubyObject indexes(IRubyObject[] args) {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Array#indexes is deprecated; use Array#values_at", "Array#indexes", "Array#values_at");
 
         RubyArray ary = new RubyArray(getRuntime(), args.length);
 
         IRubyObject[] arefArgs = new IRubyObject[1];
         for (int i = 0; i < args.length; i++) {
             arefArgs[0] = args[i];
             ary.append(aref(arefArgs));
         }
 
         return ary;
     }
 
     /** rb_ary_reverse_bang
      *
      */
     @JRubyMethod(name = "reverse!")
     public IRubyObject reverse_bang() {
         modify();
 
         IRubyObject tmp;
         if (realLength > 1) {
             int p1 = 0;
             int p2 = p1 + realLength - 1;
 
             while (p1 < p2) {
                 tmp = values[p1];
                 values[p1++] = values[p2];
                 values[p2--] = tmp;
             }
         }
         return this;
     }
 
     /** rb_ary_reverse_m
      *
      */
     @JRubyMethod(name = "reverse")
     public IRubyObject reverse() {
         return aryDup().reverse_bang();
     }
 
     /** rb_ary_collect
      *
      */
     @JRubyMethod(name = {"collect", "map"}, frame = true)
     public RubyArray collect(Block block) {
         Ruby runtime = getRuntime();
         
         if (!block.isGiven()) return new RubyArray(getRuntime(), runtime.getArray(), this);
         
         ThreadContext context = runtime.getCurrentContext();
         RubyArray collect = new RubyArray(runtime, realLength);
         
         for (int i = begin; i < begin + realLength; i++) {
             collect.append(block.yield(context, values[i]));
         }
         
         return collect;
     }
 
     /** rb_ary_collect_bang
      *
      */
     @JRubyMethod(name = {"collect!", "map!"}, frame = true)
     public RubyArray collect_bang(Block block) {
         modify();
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0, len = realLength; i < len; i++) {
             store(i, block.yield(context, values[begin + i]));
         }
         return this;
     }
 
     /** rb_ary_select
      *
      */
     @JRubyMethod(name = "select", frame = true)
     public RubyArray select(Block block) {
         Ruby runtime = getRuntime();
         RubyArray result = new RubyArray(runtime, realLength);
 
         ThreadContext context = runtime.getCurrentContext();
         if (isShared) {
             for (int i = begin; i < begin + realLength; i++) {
                 if (block.yield(context, values[i]).isTrue()) result.append(elt(i - begin));
             }
         } else {
             for (int i = 0; i < realLength; i++) {
                 if (block.yield(context, values[i]).isTrue()) result.append(elt(i));
             }
         }
         return result;
     }
 
     /** rb_ary_delete
      *
      */
     @JRubyMethod(name = "delete", required = 1, frame = true)
     public IRubyObject delete(IRubyObject item, Block block) {
         int i2 = 0;
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject e = values[begin + i1];
             if (equalInternal(context, e, item).isTrue()) continue;
             if (i1 != i2) store(i2, e);
             i2++;
         }
         
         if (realLength == i2) {
             if (block.isGiven()) return block.yield(context, item);
 
             return runtime.getNil();
         }
 
         modify();
 
         if (realLength > i2) {
             realLength = i2;
             if (i2 << 1 < values.length && values.length > ARRAY_DEFAULT_SIZE) realloc(i2 << 1);
         }
         return item;
     }
 
     /** rb_ary_delete_at
      *
      */
     private final IRubyObject delete_at(int pos) {
         int len = realLength;
 
         if (pos >= len) return getRuntime().getNil();
 
         if (pos < 0) pos += len;
 
         if (pos < 0) return getRuntime().getNil();
 
         modify();
 
         IRubyObject obj = values[pos];
         System.arraycopy(values, pos + 1, values, pos, len - (pos + 1));
 
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_delete_at_m
      * 
      */
     @JRubyMethod(name = "delete_at", required = 1)
     public IRubyObject delete_at(IRubyObject obj) {
         return delete_at((int) RubyNumeric.num2long(obj));
     }
 
     /** rb_ary_reject_bang
      * 
      */
     @JRubyMethod(name = "reject", frame = true)
     public IRubyObject reject(Block block) {
         RubyArray ary = aryDup();
         ary.reject_bang(block);
         return ary;
     }
 
     /** rb_ary_reject_bang
      *
      */
     @JRubyMethod(name = "reject!", frame = true)
     public IRubyObject reject_bang(Block block) {
         int i2 = 0;
         modify();
 
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject v = values[i1];
             if (block.yield(context, v).isTrue()) continue;
 
             if (i1 != i2) store(i2, v);
             i2++;
         }
         if (realLength == i2) return getRuntime().getNil();
 
         if (i2 < realLength) realLength = i2;
 
         return this;
     }
 
     /** rb_ary_delete_if
      *
      */
     @JRubyMethod(name = "delete_if", frame = true)
     public IRubyObject delete_if(Block block) {
         reject_bang(block);
         return this;
     }
 
     /** rb_ary_zip
      * 
      */
     @JRubyMethod(name = "zip", optional = 1, rest = true, frame = true)
     public IRubyObject zip(IRubyObject[] args, Block block) {
         for (int i = 0; i < args.length; i++) {
             args[i] = args[i].convertToArray();
         }
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         if (block.isGiven()) {
             for (int i = 0; i < realLength; i++) {
                 RubyArray tmp = new RubyArray(runtime, args.length + 1);
                 tmp.append(elt(i));
                 for (int j = 0; j < args.length; j++) {
                     tmp.append(((RubyArray) args[j]).elt(i));
                 }
                 block.yield(context, tmp);
             }
             return runtime.getNil();
         }
         
         int len = realLength;
         RubyArray result = new RubyArray(runtime, len);
         for (int i = 0; i < len; i++) {
             RubyArray tmp = new RubyArray(runtime, args.length + 1);
             tmp.append(elt(i));
             for (int j = 0; j < args.length; j++) {
                 tmp.append(((RubyArray) args[j]).elt(i));
             }
             result.append(tmp);
         }
         return result;
     }
 
     /** rb_ary_cmp
      *
      */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(IRubyObject obj) {
         RubyArray ary2 = obj.convertToArray();
 
         int len = realLength;
 
         if (len > ary2.realLength) len = ary2.realLength;
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < len; i++) {
             IRubyObject v = elt(i).callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", ary2.elt(i));
             if (!(v instanceof RubyFixnum) || ((RubyFixnum) v).getLongValue() != 0) return v;
         }
         len = realLength - ary2.realLength;
 
         if (len == 0) return RubyFixnum.zero(runtime);
         if (len > 0) return RubyFixnum.one(runtime);
 
         return RubyFixnum.minus_one(runtime);
     }
 
     /** rb_ary_slice_bang
      *
      */
     @JRubyMethod(name = "slice!", required = 1, optional = 2)
     public IRubyObject slice_bang(IRubyObject[] args) {
         if (args.length == 2) {
             long pos = RubyNumeric.num2long(args[0]);
             long len = RubyNumeric.num2long(args[1]);
             
             if (pos < 0) pos = realLength + pos;
 
             args[1] = subseq(pos, len);
             splice(pos, len, null);
             
             return args[1];
         }
         
         IRubyObject arg = args[0];
         if (arg instanceof RubyRange) {
             long[] beglen = ((RubyRange) arg).begLen(realLength, 1);
             long pos = beglen[0];
             long len = beglen[1];
 
             if (pos < 0) {
                 pos = realLength + pos;
             }
             arg = subseq(pos, len);
             splice(pos, len, null);
             return arg;
         }
 
         return delete_at((int) RubyNumeric.num2long(args[0]));
     }
 
     /** rb_ary_assoc
      *
      */
     @JRubyMethod(name = "assoc", required = 1)
     public IRubyObject assoc(IRubyObject key) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
-            if (v instanceof RubyArray && ((RubyArray) v).realLength > 0
-                && equalInternal(context, ((RubyArray) v).values[0], key).isTrue()) {
-                return v;
+            if (v instanceof RubyArray) {
+                RubyArray arr = (RubyArray)v;
+                if (arr.realLength > 0 && equalInternal(context, arr.values[arr.begin], key).isTrue()) return arr;
             }
         }
+
         return runtime.getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
     @JRubyMethod(name = "rassoc", required = 1)
     public IRubyObject rassoc(IRubyObject value) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
-            if (v instanceof RubyArray && ((RubyArray) v).realLength > 1
-                    && equalInternal(context, ((RubyArray) v).values[1], value).isTrue()) {
-                return v;
+            if (v instanceof RubyArray) {
+                RubyArray arr = (RubyArray)v;
+                if (arr.realLength > 1 && equalInternal(context, arr.values[arr.begin + 1], value).isTrue()) return arr;
             }
         }
 
         return runtime.getNil();
     }
 
     /** flatten
      * 
      */
     private final int flatten(int index, RubyArray ary2, RubyArray memo) {
         int i = index;
         int n;
         int lim = index + ary2.realLength;
 
         IRubyObject id = ary2.id();
 
         if (memo.includes(id)) throw getRuntime().newArgumentError("tried to flatten recursive array");
 
         memo.append(id);
         splice(index, 1, ary2);
         while (i < lim) {
             IRubyObject tmp = elt(i).checkArrayType();
             if (!tmp.isNil()) {
                 n = flatten(i, (RubyArray) tmp, memo);
                 i += n;
                 lim += n;
             }
             i++;
         }
         memo.pop();
         return lim - index - 1; /* returns number of increased items */
     }
 
     /** rb_ary_flatten_bang
      *
      */
     @JRubyMethod(name = "flatten!")
     public IRubyObject flatten_bang() {
         int i = 0;
         RubyArray memo = null;
 
         while (i < realLength) {
             IRubyObject ary2 = values[begin + i];
             IRubyObject tmp = ary2.checkArrayType();
             if (!tmp.isNil()) {
                 if (memo == null) {
                     memo = new RubyArray(getRuntime(), false);
                     memo.values = reserve(ARRAY_DEFAULT_SIZE);
                 }
 
                 i += flatten(i, (RubyArray) tmp, memo);
             }
             i++;
         }
         if (memo == null) return getRuntime().getNil();
 
         return this;
     }
 
     /** rb_ary_flatten
      *
      */
     @JRubyMethod(name = "flatten")
     public IRubyObject flatten() {
         RubyArray ary = aryDup();
         ary.flatten_bang();
         return ary;
     }
 
     /** rb_ary_nitems
      *
      */
     @JRubyMethod(name = "nitems")
     public IRubyObject nitems() {
         int n = 0;
 
         for (int i = begin; i < begin + realLength; i++) {
             if (!values[i].isNil()) n++;
         }
         
         return getRuntime().newFixnum(n);
     }
 
     /** rb_ary_plus
      *
      */
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject obj) {
         RubyArray y = obj.convertToArray();
         int len = realLength + y.realLength;
         RubyArray z = new RubyArray(getRuntime(), len);
         System.arraycopy(values, begin, z.values, 0, realLength);
         System.arraycopy(y.values, y.begin, z.values, realLength, y.realLength);
         z.realLength = len;
         return z;
     }
 
     /** rb_ary_times
      *
      */
     @JRubyMethod(name = "*", required = 1)
     public IRubyObject op_times(IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join(tmp);
 
         long len = RubyNumeric.num2long(times);
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0);
         if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
         if (Long.MAX_VALUE / len < realLength) {
             throw getRuntime().newArgumentError("argument too big");
         }
 
         len *= realLength;
 
         RubyArray ary2 = new RubyArray(getRuntime(), getMetaClass(), len);
         ary2.realLength = (int) len;
 
         for (int i = 0; i < len; i += realLength) {
             System.arraycopy(values, begin, ary2.values, i, realLength);
         }
 
         ary2.infectBy(this);
 
         return ary2;
     }
 
     /** ary_make_hash
      * 
      */
     private final RubyHash makeHash(RubyArray ary2) {
         RubyHash hash = new RubyHash(getRuntime(), false);
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             hash.fastASet(values[i], NEVER);
         }
 
         if (ary2 != null) {
             begin = ary2.begin;            
             for (int i = begin; i < begin + ary2.realLength; i++) {
                 hash.fastASet(ary2.values[i], NEVER);
             }
         }
         return hash;
     }
 
     /** rb_ary_uniq_bang
      *
      */
     @JRubyMethod(name = "uniq!")
     public IRubyObject uniq_bang() {
         RubyHash hash = makeHash(null);
 
         if (realLength == hash.size()) return getRuntime().getNil();
 
         int j = 0;
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (hash.fastDelete(v)) store(j++, v);
         }
         realLength = j;
         return this;
     }
 
     /** rb_ary_uniq
      *
      */
     @JRubyMethod(name = "uniq")
     public IRubyObject uniq() {
         RubyArray ary = aryDup();
         ary.uniq_bang();
         return ary;
     }
 
     /** rb_ary_diff
      *
      */
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_diff(IRubyObject other) {
         RubyHash hash = other.convertToArray().makeHash(null);
         RubyArray ary3 = new RubyArray(getRuntime());
 
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             if (hash.fastARef(values[i]) != null) continue;
             ary3.append(elt(i - begin));
         }
 
         return ary3;
     }
 
     /** rb_ary_and
      *
      */
     @JRubyMethod(name = "&", required = 1)
     public IRubyObject op_and(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         RubyHash hash = ary2.makeHash(null);
         RubyArray ary3 = new RubyArray(getRuntime(), 
                 realLength < ary2.realLength ? realLength : ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (hash.fastDelete(v)) ary3.append(v);
         }
 
         return ary3;
     }
 
     /** rb_ary_or
      *
      */
     @JRubyMethod(name = "|", required = 1)
     public IRubyObject op_or(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         RubyHash set = makeHash(ary2);
 
         RubyArray ary3 = new RubyArray(getRuntime(), realLength + ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.fastDelete(v)) ary3.append(v);
         }
         for (int i = 0; i < ary2.realLength; i++) {
             IRubyObject v = ary2.elt(i);
             if (set.fastDelete(v)) ary3.append(v);
         }
         return ary3;
     }
 
     /** rb_ary_sort
      *
      */
     @JRubyMethod(name = "sort", frame = true)
     public RubyArray sort(Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang(block);
         return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
     @JRubyMethod(name = "sort!", frame = true)
     public RubyArray sort_bang(Block block) {
         modify();
         if (realLength > 1) {
             flags |= TMPLOCK_ARR_F;
             try {
                 if (block.isGiven()) {
                     Arrays.sort(values, 0, realLength, new BlockComparator(block));
                 } else {
                     Arrays.sort(values, 0, realLength, new DefaultComparator());
                 }
             } finally {
                 flags &= ~TMPLOCK_ARR_F;
             }
         }
         return this;
     }
 
     final class BlockComparator implements Comparator {
         private Block block;
 
         public BlockComparator(Block block) {
             this.block = block;
         }
 
         public int compare(Object o1, Object o2) {
             ThreadContext context = getRuntime().getCurrentContext();
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
             IRubyObject ret = block.yield(context, getRuntime().newArray(obj1, obj2), null, null, true);
             int n = RubyComparable.cmpint(ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
     final class DefaultComparator implements Comparator {
         public int compare(Object o1, Object o2) {
             if (o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
                 long a = ((RubyFixnum) o1).getLongValue();
                 long b = ((RubyFixnum) o2).getLongValue();
                 if (a > b) return 1;
                 if (a < b) return -1;
                 return 0;
             }
             if (o1 instanceof RubyString && o2 instanceof RubyString) {
                 return ((RubyString) o1).op_cmp((RubyString) o2);
             }
 
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
 
             IRubyObject ret = obj1.callMethod(obj1.getRuntime().getCurrentContext(), MethodIndex.OP_SPACESHIP, "<=>", obj2);
             int n = RubyComparable.cmpint(ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
     public static void marshalTo(RubyArray array, MarshalStream output) throws IOException {
         output.registerLinkTarget(array);
         output.writeInt(array.getList().size());
         for (Iterator iter = array.getList().iterator(); iter.hasNext();) {
             output.dumpObject((IRubyObject) iter.next());
         }
     }
 
     public static RubyArray unmarshalFrom(UnmarshalStream input) throws IOException {
         RubyArray result = input.getRuntime().newArray();
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             result.append(input.unmarshalObject());
         }
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#pack
      */
     @JRubyMethod(name = "pack", required = 1)
     public RubyString pack(IRubyObject obj) {
         RubyString iFmt = RubyString.objAsString(obj);
         return Pack.pack(getRuntime(), this, iFmt.getByteList());
     }
 
     public Class getJavaClass() {
         return List.class;
     }
 
     // Satisfy java.util.List interface (for Java integration)
     public int size() {
         return realLength;
     }
 
     public boolean isEmpty() {
         return realLength == 0;
     }
 
     public boolean contains(Object element) {
         return indexOf(element) != -1;
     }
 
     public Object[] toArray() {
         Object[] array = new Object[realLength];
         for (int i = begin; i < realLength; i++) {
             array[i - begin] = JavaUtil.convertRubyToJava(values[i]);
         }
         return array;
     }
 
     public Object[] toArray(final Object[] arg) {
         Object[] array = arg;
         if (array.length < realLength) {
             Class type = array.getClass().getComponentType();
             array = (Object[]) Array.newInstance(type, realLength);
         }
         int length = realLength - begin;
 
         for (int i = 0; i < length; i++) {
             array[i] = JavaUtil.convertRubyToJava(values[i + begin]);
         }
         return array;
     }
 
     public boolean add(Object element) {
         append(JavaUtil.convertJavaToRuby(getRuntime(), element));
         return true;
     }
 
     public boolean remove(Object element) {
         IRubyObject deleted = delete(JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
         return deleted.isNil() ? false : true; // TODO: is this correct ?
     }
 
     public boolean containsAll(Collection c) {
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             if (indexOf(iter.next()) == -1) {
                 return false;
             }
         }
 
         return true;
     }
 
     public boolean addAll(Collection c) {
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             add(iter.next());
         }
         return !c.isEmpty();
     }
 
     public boolean addAll(int index, Collection c) {
         Iterator iter = c.iterator();
         for (int i = index; iter.hasNext(); i++) {
             add(i, iter.next());
         }
         return !c.isEmpty();
     }
 
     public boolean removeAll(Collection c) {
         boolean listChanged = false;
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             if (remove(iter.next())) {
                 listChanged = true;
             }
         }
         return listChanged;
     }
 
     public boolean retainAll(Collection c) {
         boolean listChanged = false;
 
         for (Iterator iter = iterator(); iter.hasNext();) {
             Object element = iter.next();
             if (!c.contains(element)) {
                 remove(element);
                 listChanged = true;
             }
         }
         return listChanged;
     }
 
     public Object get(int index) {
         return JavaUtil.convertRubyToJava((IRubyObject) elt(index), Object.class);
     }
 
     public Object set(int index, Object element) {
         return store(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
     }
 
     // TODO: make more efficient by not creating IRubyArray[]
     public void add(int index, Object element) {
         insert(new IRubyObject[]{RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToRuby(getRuntime(), element)});
     }
 
     public Object remove(int index) {
         return JavaUtil.convertRubyToJava(delete_at(index), Object.class);
     }
 
     public int indexOf(Object element) {
         int begin = this.begin;
 
         if (element == null) {
             for (int i = begin; i < begin + realLength; i++) {
                 if (values[i] == null) {
                     return i;
                 }
             }
         } else {
             IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
 
             for (int i = begin; i < begin + realLength; i++) {
                 if (convertedElement.equals(values[i])) {
                     return i;
                 }
             }
         }
         return -1;
     }
 
     public int lastIndexOf(Object element) {
         int begin = this.begin;
 
         if (element == null) {
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (values[i] == null) {
                     return i;
                 }
             }
         } else {
             IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
 
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (convertedElement.equals(values[i])) {
                     return i;
                 }
             }
         }
 
         return -1;
     }
 
     public class RubyArrayConversionIterator implements Iterator {
         protected int index = 0;
         protected int last = -1;
 
         public boolean hasNext() {
             return index < realLength;
         }
 
         public Object next() {
             IRubyObject element = elt(index);
             last = index++;
             return JavaUtil.convertRubyToJava(element, Object.class);
         }
 
         public void remove() {
             if (last == -1) throw new IllegalStateException();
 
             delete_at(last);
             if (last < index) index--;
 
             last = -1;
 	
         }
     }
 
     public Iterator iterator() {
         return new RubyArrayConversionIterator();
     }
 
     final class RubyArrayConversionListIterator extends RubyArrayConversionIterator implements ListIterator {
         public RubyArrayConversionListIterator() {
         }
 
         public RubyArrayConversionListIterator(int index) {
             this.index = index;
 		}
 
 		public boolean hasPrevious() {
             return index >= 0;
 		}
 
 		public Object previous() {
             return JavaUtil.convertRubyToJava((IRubyObject) elt(last = --index), Object.class);
 		}
 
 		public int nextIndex() {
             return index;
 		}
 
 		public int previousIndex() {
             return index - 1;
 		}
 
         public void set(Object obj) {
             if (last == -1) throw new IllegalStateException();
 
             store(last, JavaUtil.convertJavaToRuby(getRuntime(), obj));
         }
 
         public void add(Object obj) {
             insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index++), JavaUtil.convertJavaToRuby(getRuntime(), obj) });
             last = -1;
 		}
     }
 
     public ListIterator listIterator() {
         return new RubyArrayConversionListIterator();
     }
 
     public ListIterator listIterator(int index) {
         return new RubyArrayConversionListIterator(index);
 	}
 
     // TODO: list.subList(from, to).clear() is supposed to clear the sublist from the list.
     // How can we support this operation?
     public List subList(int fromIndex, int toIndex) {
         if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
             throw new IndexOutOfBoundsException();
         }
         
         IRubyObject subList = subseq(fromIndex, toIndex - fromIndex + 1);
 
         return subList.isNil() ? null : (List) subList;
     }
 
     public void clear() {
         rb_clear();
     }
 }
