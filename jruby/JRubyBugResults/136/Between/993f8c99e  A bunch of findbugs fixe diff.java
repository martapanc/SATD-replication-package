diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index e637f79d0a..9d2f7324da 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1139,1297 +1139,1297 @@ public class RubyArray extends RubyObject implements List {
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
     @JRubyMethod(name = "each", frame = true)
     public IRubyObject each(ThreadContext context, Block block) {
         for (int i = 0; i < realLength; i++) {
             block.yield(context, values[begin + i]);
         }
         return this;
     }
 
     /** rb_ary_each_index
      *
      */
     @JRubyMethod(name = "each_index", frame = true)
     public IRubyObject each_index(ThreadContext context, Block block) {
         Ruby runtime = getRuntime();
         for (int i = 0; i < realLength; i++) {
             block.yield(context, runtime.newFixnum(i));
         }
         return this;
     }
 
     /** rb_ary_reverse_each
      *
      */
     @JRubyMethod(name = "reverse_each", frame = true)
     public IRubyObject reverse_each(ThreadContext context, Block block) {
         int len = realLength;
         
         while(len-- > 0) {
             block.yield(context, values[begin + len]);
             
             if (realLength < len) len = realLength;
         }
         
         return this;
     }
 
     private IRubyObject inspectJoin(ThreadContext context, RubyArray tmp, IRubyObject sep) {
         Ruby runtime = getRuntime();
 
         // If already inspecting, there is no need to register/unregister again.
         if (runtime.isInspecting(this)) {
             return tmp.join(context, sep);
         }
 
         try {
             runtime.registerInspecting(this);
             return tmp.join(context, sep);
         } finally {
             runtime.unregisterInspecting(this);
         }
     }
 
     /** rb_ary_join
      *
      */
     public RubyString join(ThreadContext context, IRubyObject sep) {
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
                     tmp = inspectJoin(context, (RubyArray)tmp, sep);
                 }
             } else {
                 tmp = RubyString.objAsString(context, tmp);
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
     public RubyString join_m(ThreadContext context, IRubyObject[] args) {
         int argc = args.length;
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
         
         return join(context, sep);
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
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
 
         if (!(obj instanceof RubyArray)) {
             if (!obj.respondsTo("to_ary")) {
                 return getRuntime().getFalse();
             } else {
                 return equalInternal(context, obj.callMethod(context, "to_ary"), this);
             }
         }
 
         RubyArray ary = (RubyArray) obj;
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         for (long i = 0; i < realLength; i++) {
             if (!equalInternal(context, elt(i), ary.elt(i)).isTrue()) return runtime.getFalse();            
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     @JRubyMethod(name = "eql?", required = 1)
     public RubyBoolean eql_p(ThreadContext context, IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
         if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
 
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
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
     public IRubyObject fill(ThreadContext context, IRubyObject[] args, Block block) {
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
     public IRubyObject index(ThreadContext context, IRubyObject obj) {
         Ruby runtime = getRuntime();
         for (int i = begin; i < begin + realLength; i++) {
             if (equalInternal(context, values[i], obj).isTrue()) return runtime.newFixnum(i - begin);            
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     @JRubyMethod(name = "rindex", required = 1)
     public IRubyObject rindex(ThreadContext context, IRubyObject obj) {
         Ruby runtime = getRuntime();
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
     public RubyArray collect(ThreadContext context, Block block) {
         Ruby runtime = getRuntime();
         
         if (!block.isGiven()) return new RubyArray(getRuntime(), runtime.getArray(), this);
         
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
     public RubyArray collect_bang(ThreadContext context, Block block) {
         modify();
         for (int i = 0, len = realLength; i < len; i++) {
             store(i, block.yield(context, values[begin + i]));
         }
         return this;
     }
 
     /** rb_ary_select
      *
      */
     @JRubyMethod(name = "select", frame = true)
     public RubyArray select(ThreadContext context, Block block) {
         Ruby runtime = getRuntime();
         RubyArray result = new RubyArray(runtime, realLength);
 
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
     public IRubyObject delete(ThreadContext context, IRubyObject item, Block block) {
         int i2 = 0;
 
         Ruby runtime = getRuntime();
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
     public IRubyObject reject(ThreadContext context, Block block) {
         RubyArray ary = aryDup();
         ary.reject_bang(context, block);
         return ary;
     }
 
     /** rb_ary_reject_bang
      *
      */
     @JRubyMethod(name = "reject!", frame = true)
     public IRubyObject reject_bang(ThreadContext context, Block block) {
         int i2 = 0;
         modify();
 
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
     public IRubyObject delete_if(ThreadContext context, Block block) {
         reject_bang(context, block);
         return this;
     }
 
     /** rb_ary_zip
      * 
      */
     @JRubyMethod(name = "zip", optional = 1, rest = true, frame = true)
     public IRubyObject zip(ThreadContext context, IRubyObject[] args, Block block) {
         for (int i = 0; i < args.length; i++) {
             args[i] = args[i].convertToArray();
         }
 
         Ruby runtime = getRuntime();
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
     public IRubyObject op_cmp(ThreadContext context, IRubyObject obj) {
         RubyArray ary2 = obj.convertToArray();
 
         int len = realLength;
 
         if (len > ary2.realLength) len = ary2.realLength;
 
         Ruby runtime = getRuntime();
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
     public IRubyObject assoc(ThreadContext context, IRubyObject key) {
         Ruby runtime = getRuntime();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray) {
                 RubyArray arr = (RubyArray)v;
                 if (arr.realLength > 0 && equalInternal(context, arr.values[arr.begin], key).isTrue()) return arr;
             }
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
     @JRubyMethod(name = "rassoc", required = 1)
     public IRubyObject rassoc(ThreadContext context, IRubyObject value) {
         Ruby runtime = getRuntime();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray) {
                 RubyArray arr = (RubyArray)v;
                 if (arr.realLength > 1 && equalInternal(context, arr.values[arr.begin + 1], value).isTrue()) return arr;
             }
         }
 
         return runtime.getNil();
     }
 
     /** flatten
      * 
      */
     private final int flatten(ThreadContext context, int index, RubyArray ary2, RubyArray memo) {
         int i = index;
         int n;
         int lim = index + ary2.realLength;
 
         IRubyObject id = ary2.id();
 
         if (memo.includes(context, id)) throw getRuntime().newArgumentError("tried to flatten recursive array");
 
         memo.append(id);
         splice(index, 1, ary2);
         while (i < lim) {
             IRubyObject tmp = elt(i).checkArrayType();
             if (!tmp.isNil()) {
                 n = flatten(context, i, (RubyArray) tmp, memo);
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
     public IRubyObject flatten_bang(ThreadContext context) {
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
 
                 i += flatten(context, i, (RubyArray) tmp, memo);
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
     public IRubyObject flatten(ThreadContext context) {
         RubyArray ary = aryDup();
         ary.flatten_bang(context);
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
     public IRubyObject op_times(ThreadContext context, IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join(context, tmp);
 
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
             int n = RubyComparable.cmpint(context, ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
-    final class DefaultComparator implements Comparator {
+    static final class DefaultComparator implements Comparator {
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
 
             ThreadContext context = obj1.getRuntime().getCurrentContext();
             IRubyObject ret = obj1.callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", obj2);
             int n = RubyComparable.cmpint(context, ret, obj1, obj2);
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
     public RubyString pack(ThreadContext context, IRubyObject obj) {
         RubyString iFmt = RubyString.objAsString(context, obj);
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
         IRubyObject deleted = delete(getRuntime().getCurrentContext(), JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
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
diff --git a/src/org/jruby/RubyDigest.java b/src/org/jruby/RubyDigest.java
index 6aa9f78019..ec0272da3c 100644
--- a/src/org/jruby/RubyDigest.java
+++ b/src/org/jruby/RubyDigest.java
@@ -1,328 +1,328 @@
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
  * Copyright (C) 2006, 2007 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  * Copyright (C) 2008 Vladimir Sizikov <vsizikov@gmail.com>
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
 
 import java.security.Provider;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.ByteList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class RubyDigest {
     private static Provider provider = null;
 
     public static void createDigest(Ruby runtime) {
         try {
             provider = (Provider) Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider").newInstance();
         } catch(Exception e) {
             // provider is not available
         }
 
         RubyModule mDigest = runtime.defineModule("Digest");
         RubyClass cDigestBase = mDigest.defineClassUnder("Base",runtime.getObject(), Base.BASE_ALLOCATOR);
 
         cDigestBase.defineAnnotatedMethods(Base.class);
     }
 
     private static MessageDigest createMessageDigest(Ruby runtime, String providerName) throws NoSuchAlgorithmException {
         if(provider != null) {
             try {
                 return MessageDigest.getInstance(providerName, provider);
             } catch(NoSuchAlgorithmException e) {
                 // bouncy castle doesn't support algorithm
             }
         }
         // fall back to system JCA providers
         return MessageDigest.getInstance(providerName);
     }
 
     public static void createDigestMD5(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         RubyModule mDigest = runtime.fastGetModule("Digest");
         RubyClass cDigestBase = mDigest.fastGetClass("Base");
         RubyClass cDigest_MD5 = mDigest.defineClassUnder("MD5",cDigestBase,cDigestBase.getAllocator());
         cDigest_MD5.defineFastMethod("block_length", new Callback() {
             public Arity getArity() {
                 return Arity.NO_ARGUMENTS;
             }
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                 return RubyFixnum.newFixnum(recv.getRuntime(), 64);
             }
         });
         cDigest_MD5.setInternalModuleVariable("metadata",runtime.newString("MD5"));
     }
 
     public static void createDigestRMD160(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         if(provider == null) {
             throw runtime.newLoadError("RMD160 not supported without BouncyCastle");
         }
 
         RubyModule mDigest = runtime.fastGetModule("Digest");
         RubyClass cDigestBase = mDigest.fastGetClass("Base");
         RubyClass cDigest_RMD160 = mDigest.defineClassUnder("RMD160",cDigestBase,cDigestBase.getAllocator());
         cDigest_RMD160.setInternalModuleVariable("metadata",runtime.newString("RIPEMD160"));
     }
 
     public static void createDigestSHA1(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         RubyModule mDigest = runtime.fastGetModule("Digest");
         RubyClass cDigestBase = mDigest.fastGetClass("Base");
         RubyClass cDigest_SHA1 = mDigest.defineClassUnder("SHA1",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA1.setInternalModuleVariable("metadata",runtime.newString("SHA1"));
     }
 
     public static void createDigestSHA2(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         try {
             createMessageDigest(runtime, "SHA-256");
         } catch(NoSuchAlgorithmException e) {
             throw runtime.newLoadError("SHA2 not supported");
         }
 
         RubyModule mDigest = runtime.fastGetModule("Digest");
         RubyClass cDigestBase = mDigest.fastGetClass("Base");
         RubyClass cDigest_SHA2_256 = mDigest.defineClassUnder("SHA256",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_256.setInternalModuleVariable("metadata",runtime.newString("SHA-256"));
         RubyClass cDigest_SHA2_384 = mDigest.defineClassUnder("SHA384",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_384.setInternalModuleVariable("metadata",runtime.newString("SHA-384"));
         RubyClass cDigest_SHA2_512 = mDigest.defineClassUnder("SHA512",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_512.setInternalModuleVariable("metadata",runtime.newString("SHA-512"));
     }
 
     public static class Base extends RubyObject {
-        protected static ObjectAllocator BASE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator BASE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new Base(runtime, klass);
             }
         };
         
         @JRubyMethod(name = "digest", required = 1, meta = true)
         public static IRubyObject s_digest(IRubyObject recv, IRubyObject str) {
             Ruby runtime = recv.getRuntime();
             String name = ((RubyClass)recv).searchInternalModuleVariable("metadata").toString();
             try {
                 MessageDigest md = createMessageDigest(runtime, name);
                 return RubyString.newString(runtime, md.digest(str.convertToString().getBytes()));
             } catch(NoSuchAlgorithmException e) {
                 throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
             }
         }
         
         @JRubyMethod(name = "hexdigest", required = 1, meta = true)
         public static IRubyObject s_hexdigest(IRubyObject recv, IRubyObject str) {
             Ruby runtime = recv.getRuntime();
             String name = ((RubyClass)recv).searchInternalModuleVariable("metadata").toString();
             try {
                 MessageDigest md = createMessageDigest(runtime, name);
                 return RubyString.newString(runtime, ByteList.plain(toHex(md.digest(str.convertToString().getBytes()))));
             } catch(NoSuchAlgorithmException e) {
                 throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
             }
         }
 
         private MessageDigest algo;
         private StringBuffer data;
 
         public Base(Ruby runtime, RubyClass type) {
             super(runtime,type);
             data = new StringBuffer();
 
             if(type == runtime.fastGetModule("Digest").fastGetClass("Base")) {
                 throw runtime.newNotImplementedError("Digest::Base is an abstract class");
             }
             if(!type.hasInternalModuleVariable("metadata")) {
                 throw runtime.newNotImplementedError("the " + type + "() function is unimplemented on this machine");
             }
             try {
                 setAlgorithm(type.searchInternalModuleVariable("metadata"));
             } catch(NoSuchAlgorithmException e) {
                 throw runtime.newNotImplementedError("the " + type + "() function is unimplemented on this machine");
             }
 
         }
         
         @JRubyMethod(name = "initialize", optional = 1, frame = true)
         public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
             if(args.length > 0 && !args[0].isNil()) {
                 update(args[0]);
             }
             return this;
         }
 
         @JRubyMethod(name = "initialize_copy", required = 1)
         public IRubyObject initialize_copy(IRubyObject obj) {
             if(this == obj) {
                 return this;
             }
             ((RubyObject)obj).checkFrozen();
 
             data = new StringBuffer(((Base)obj).data.toString());
             String name = ((Base)obj).algo.getAlgorithm();
             try {
                 algo = createMessageDigest(getRuntime(), name);
             } catch(NoSuchAlgorithmException e) {
                 throw getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
             }
             return this;
         }
 
         @JRubyMethod(name = {"update", "<<"}, required = 1)
         public IRubyObject update(IRubyObject obj) {
             data.append(obj);
             return this;
         }
 
         @JRubyMethod(name = "digest", optional = 1)
         public IRubyObject digest(IRubyObject[] args) {
             if (args.length == 1) {
                 reset();
                 data.append(args[0]);
             }
 
             IRubyObject digest = getDigest();
 
             if (args.length == 1) {
                 reset();
             }
             return digest;
         }
         
         private IRubyObject getDigest() {
             algo.reset();
             return RubyString.newString(getRuntime(), algo.digest(ByteList.plain(data)));
         }
         
         @JRubyMethod(name = "digest!")
         public IRubyObject digest_bang() {
             algo.reset();            
             byte[] digest = algo.digest(ByteList.plain(data));
             reset();
             return RubyString.newString(getRuntime(), digest);
         }
 
         @JRubyMethod(name = {"hexdigest"}, optional = 1)
         public IRubyObject hexdigest(IRubyObject[] args) {
             algo.reset();
             if (args.length == 1) {
                 reset();
                 data.append(args[0]);
             }
 
             byte[] digest = ByteList.plain(toHex(algo.digest(ByteList.plain(data)))); 
 
             if (args.length == 1) {
                 reset();
             }
             return RubyString.newString(getRuntime(), digest);
         }
         
         @JRubyMethod(name = {"to_s"})
         public IRubyObject to_s() {
             algo.reset();
             return RubyString.newString(getRuntime(), ByteList.plain(toHex(algo.digest(ByteList.plain(data)))));
         }
 
         @JRubyMethod(name = {"hexdigest!"})
         public IRubyObject hexdigest_bang() {
             algo.reset();
             byte[] digest = ByteList.plain(toHex(algo.digest(ByteList.plain(data))));
             reset();
             return RubyString.newString(getRuntime(), digest);
         }
         
         @JRubyMethod(name = "inspect")
         public IRubyObject inspect() {
             algo.reset();
             return RubyString.newString(getRuntime(), ByteList.plain("#<" + getMetaClass().getRealClass().getName() + ": " + toHex(algo.digest(ByteList.plain(data))) + ">"));
         }
 
         @JRubyMethod(name = "==", required = 1)
         public IRubyObject op_equal(IRubyObject oth) {
             boolean ret = this == oth;
             if(!ret) {
                 if (oth instanceof Base) {
                     Base b = (Base)oth;
                     ret = this.algo.getAlgorithm().equals(b.algo.getAlgorithm()) &&
                             this.getDigest().equals(b.getDigest());
                 } else {
                     IRubyObject str = oth.convertToString();
                     ret = this.to_s().equals(str);
                 }
             }
 
             return ret ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         
         @JRubyMethod(name = {"length", "size", "digest_length"})
         public IRubyObject length() {
             return RubyFixnum.newFixnum(getRuntime(), algo.getDigestLength());
         }
 
         @JRubyMethod(name = {"block_length"})
         public IRubyObject block_length() {
             throw getRuntime().newRuntimeError(
                     this.getMetaClass() + " doesn't implement block_length()");
         }
 
         @JRubyMethod(name = {"reset"})
         public IRubyObject reset() {
             algo.reset();
             data = new StringBuffer();
             return getRuntime().getNil();
         }
 
        private void setAlgorithm(IRubyObject algo) throws NoSuchAlgorithmException {
            this.algo = createMessageDigest(getRuntime(), algo.toString());
         }
 
         private static String toHex(byte[] val) {
             StringBuffer out = new StringBuffer();
             for(int i=0,j=val.length;i<j;i++) {
                 String ve = Integer.toString((((int)((char)val[i])) & 0xFF),16);
                 if(ve.length() == 1) {
                     ve = "0" + ve;
                 }
                 out.append(ve);
             }
             return out.toString();
         }
     }
 }// RubyDigest
diff --git a/src/org/jruby/RubyDir.java b/src/org/jruby/RubyDir.java
index dfde0a9f2d..47890898f7 100644
--- a/src/org/jruby/RubyDir.java
+++ b/src/org/jruby/RubyDir.java
@@ -1,576 +1,577 @@
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.ext.posix.util.Platform;
 
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.Dir;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.ByteList;
 
 /**
  * .The Ruby built-in class Dir.
  *
  * @author  jvoegele
  */
 public class RubyDir extends RubyObject {
 	// What we passed to the constructor for method 'path'
     private RubyString    path;
     protected JRubyFile      dir;
     private   String[]  snapshot;   // snapshot of contents of directory
     private   int       pos;        // current position in directory
     private boolean isOpen = true;
 
     public RubyDir(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
-    private static ObjectAllocator DIR_ALLOCATOR = new ObjectAllocator() {
+    private static final ObjectAllocator DIR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyDir(runtime, klass);
         }
     };
 
     public static RubyClass createDirClass(Ruby runtime) {
         RubyClass dirClass = runtime.defineClass("Dir", runtime.getObject(), DIR_ALLOCATOR);
         runtime.setDir(dirClass);
 
         dirClass.includeModule(runtime.getEnumerable());
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyDir.class);
         
         dirClass.defineAnnotatedMethods(RubyDir.class);
         dirClass.dispatcher = callbackFactory.createDispatcher(dirClass);
 
         return dirClass;
     }
     
     private final void checkDir() {
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) throw getRuntime().newSecurityError("Insecure: operation on untainted Dir");
      
         testFrozen("");
         
         if (!isOpen) throw getRuntime().newIOError("closed directory");
     }    
 
     /**
      * Creates a new <code>Dir</code>.  This method takes a snapshot of the
      * contents of the directory at creation time, so changes to the contents
      * of the directory will not be reflected during the lifetime of the
      * <code>Dir</code> object returned, so a new <code>Dir</code> instance
      * must be created to reflect changes to the underlying file system.
      */
     @JRubyMethod(name = "initialize", required = 1, frame = true)
     public IRubyObject initialize(IRubyObject _newPath, Block unusedBlock) {
         RubyString newPath = _newPath.convertToString();
         getRuntime().checkSafeString(newPath);
 
         String adjustedPath = RubyFile.adjustRootPathOnWindows(getRuntime(), newPath.toString(), null);
         checkDirIsTwoSlashesOnWindows(getRuntime(), adjustedPath);
 
         dir = JRubyFile.create(getRuntime().getCurrentDirectory(), adjustedPath);
         if (!dir.isDirectory()) {
             dir = null;
             throw getRuntime().newErrnoENOENTError(newPath.toString() + " is not a directory");
         }
         path = newPath;
 		List<String> snapshotList = new ArrayList<String>();
 		snapshotList.add(".");
 		snapshotList.add("..");
 		snapshotList.addAll(getContents(dir));
 		snapshot = (String[]) snapshotList.toArray(new String[snapshotList.size()]);
 		pos = 0;
 
         return this;
     }
 
 // ----- Ruby Class Methods ----------------------------------------------------
     
     private static List<ByteList> dirGlobs(String cwd, IRubyObject[] args, int flags) {
         List<ByteList> dirs = new ArrayList<ByteList>();
         
         for (int i = 0; i < args.length; i++) {
             ByteList globPattern = args[i].convertToString().getByteList();
             dirs.addAll(Dir.push_glob(cwd, globPattern, flags));
         }
         
         return dirs;
     }
     
     private static IRubyObject asRubyStringList(Ruby runtime, List<ByteList> dirs) {
         List<RubyString> allFiles = new ArrayList<RubyString>();
 
         for (ByteList dir: dirs) {
             allFiles.add(RubyString.newString(runtime, dir));
         }            
 
         IRubyObject[] tempFileList = new IRubyObject[allFiles.size()];
         allFiles.toArray(tempFileList);
          
         return runtime.newArrayNoCopy(tempFileList);
     }
     
     private static String getCWD(Ruby runtime) {
         try {
             return new org.jruby.util.NormalizedFile(runtime.getCurrentDirectory()).getCanonicalPath();
         } catch(Exception e) {
             return runtime.getCurrentDirectory();
         }
     }
 
     @JRubyMethod(name = "[]", required = 1, optional = 1, meta = true)
     public static IRubyObject aref(IRubyObject recv, IRubyObject[] args) {
         List<ByteList> dirs;
         if (args.length == 1) {
             ByteList globPattern = args[0].convertToString().getByteList();
             dirs = Dir.push_glob(getCWD(recv.getRuntime()), globPattern, 0);
         } else {
             dirs = dirGlobs(getCWD(recv.getRuntime()), args, 0);
         }
 
         return asRubyStringList(recv.getRuntime(), dirs);
     }
     
     /**
      * Returns an array of filenames matching the specified wildcard pattern
      * <code>pat</code>. If a block is given, the array is iterated internally
      * with each filename is passed to the block in turn. In this case, Nil is
      * returned.  
      */
     @JRubyMethod(name = "glob", required = 1, optional = 1, frame = true, meta = true)
     public static IRubyObject glob(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         int flags = args.length == 2 ?  RubyNumeric.num2int(args[1]) : 0;
 
         List<ByteList> dirs;
         IRubyObject tmp = args[0].checkArrayType();
         if (tmp.isNil()) {
             ByteList globPattern = args[0].convertToString().getByteList();
             dirs = Dir.push_glob(recv.getRuntime().getCurrentDirectory(), globPattern, flags);
         } else {
             dirs = dirGlobs(getCWD(runtime), ((RubyArray) tmp).toJavaArray(), flags);
         }
         
         if (block.isGiven()) {
             for (int i = 0; i < dirs.size(); i++) {
                 block.yield(context, RubyString.newString(runtime, dirs.get(i)));
             }
         
             return recv.getRuntime().getNil();
         }
 
         return asRubyStringList(recv.getRuntime(), dirs);
     }
 
     /**
      * @return all entries for this Dir
      */
     @JRubyMethod(name = "entries")
     public RubyArray entries() {
         return getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(getRuntime(), snapshot));
     }
     
     /**
      * Returns an array containing all of the filenames in the given directory.
      */
     @JRubyMethod(name = "entries", required = 1, meta = true)
     public static RubyArray entries(IRubyObject recv, IRubyObject path) {
         Ruby runtime = recv.getRuntime();
 
         String adjustedPath = RubyFile.adjustRootPathOnWindows(
                 runtime, path.convertToString().toString(), null);
         checkDirIsTwoSlashesOnWindows(runtime, adjustedPath);
 
         final JRubyFile directory = JRubyFile.create(
                 recv.getRuntime().getCurrentDirectory(), adjustedPath);
 
         if (!directory.isDirectory()) {
             throw recv.getRuntime().newErrnoENOENTError("No such directory");
         }
         List<String> fileList = getContents(directory);
 		fileList.add(0, ".");
 		fileList.add(1, "..");
         Object[] files = fileList.toArray();
         return recv.getRuntime().newArrayNoCopy(JavaUtil.convertJavaArrayToRuby(recv.getRuntime(), files));
     }
     
     // MRI behavior: just plain '//' or '\\\\' are considered illegal on Windows.
     private static void checkDirIsTwoSlashesOnWindows(Ruby runtime, String path) {
         if (Platform.IS_WINDOWS && ("//".equals(path) || "\\\\".equals(path))) {
             throw runtime.newErrnoEINVALError("Invalid argument - " + path);
         }
     }
 
     /** Changes the current directory to <code>path</code> */
     @JRubyMethod(name = "chdir", optional = 1, frame = true, meta = true)
     public static IRubyObject chdir(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString path = args.length == 1 ? 
             (RubyString) args[0].convertToString() : getHomeDirectoryPath(context, recv);
         String adjustedPath = RubyFile.adjustRootPathOnWindows(
                 recv.getRuntime(), path.toString(), null);
         checkDirIsTwoSlashesOnWindows(recv.getRuntime(), adjustedPath);
         JRubyFile dir = getDir(recv.getRuntime(), adjustedPath, true);
         String realPath = null;
         String oldCwd = recv.getRuntime().getCurrentDirectory();
         
         // We get canonical path to try and flatten the path out.
         // a dir '/subdir/..' should return as '/'
         // cnutter: Do we want to flatten path out?
         try {
             realPath = dir.getCanonicalPath();
         } catch (IOException e) {
             realPath = dir.getAbsolutePath();
         }
         
         IRubyObject result = null;
         if (block.isGiven()) {
         	// FIXME: Don't allow multiple threads to do this at once
             recv.getRuntime().setCurrentDirectory(realPath);
             try {
                 result = block.yield(context, path);
             } finally {
                 dir = getDir(recv.getRuntime(), oldCwd, true);
                 recv.getRuntime().setCurrentDirectory(oldCwd);
             }
         } else {
         	recv.getRuntime().setCurrentDirectory(realPath);
         	result = recv.getRuntime().newFixnum(0);
         }
         
         return result;
     }
 
     /**
      * Changes the root directory (only allowed by super user).  Not available
      * on all platforms.
      */
     @JRubyMethod(name = "chroot", required = 1, meta = true)
     public static IRubyObject chroot(IRubyObject recv, IRubyObject path) {
         throw recv.getRuntime().newNotImplementedError("chroot not implemented: chroot is non-portable and is not supported.");
     }
 
     /**
      * Deletes the directory specified by <code>path</code>.  The directory must
      * be empty.
      */
     @JRubyMethod(name = {"rmdir", "unlink", "delete"}, required = 1, meta = true)
     public static IRubyObject rmdir(IRubyObject recv, IRubyObject path) {
         JRubyFile directory = getDir(recv.getRuntime(), path.convertToString().toString(), true);
         
         if (!directory.delete()) {
             throw recv.getRuntime().newSystemCallError("No such directory");
         }
         
         return recv.getRuntime().newFixnum(0);
     }
 
     /**
      * Executes the block once for each file in the directory specified by
      * <code>path</code>.
      */
     @JRubyMethod(name = "foreach", required = 1, frame = true, meta = true)
     public static IRubyObject foreach(ThreadContext context, IRubyObject recv, IRubyObject _path, Block block) {
         RubyString path = _path.convertToString();
         recv.getRuntime().checkSafeString(path);
 
         RubyClass dirClass = recv.getRuntime().getDir();
         RubyDir dir = (RubyDir) dirClass.newInstance(context, new IRubyObject[] { path }, block);
         
         dir.each(context, block);
         return recv.getRuntime().getNil();
     }
 
     /** Returns the current directory. */
     @JRubyMethod(name = {"getwd", "pwd"}, meta = true)
     public static RubyString getwd(IRubyObject recv) {
         return recv.getRuntime().newString(recv.getRuntime().getCurrentDirectory());
     }
 
     /**
      * Creates the directory specified by <code>path</code>.  Note that the
      * <code>mode</code> parameter is provided only to support existing Ruby
      * code, and is ignored.
      */
     @JRubyMethod(name = "mkdir", required = 1, optional = 1, meta = true)
     public static IRubyObject mkdir(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         runtime.checkSafeString(args[0]);
         String path = args[0].toString();
 
         File newDir = getDir(runtime, path, false);
         if (File.separatorChar == '\\') {
             newDir = new File(newDir.getPath());
         }
         
         int mode = args.length == 2 ? ((int) args[1].convertToInteger().getLongValue()) : 0777;
 
         if (runtime.getPosix().mkdir(newDir.getAbsolutePath(), mode) < 0) {
             // FIXME: This is a system error based on errno
             throw recv.getRuntime().newSystemCallError("mkdir failed");
         }
         
         return RubyFixnum.zero(recv.getRuntime());
     }
 
     /**
      * Returns a new directory object for <code>path</code>.  If a block is
      * provided, a new directory object is passed to the block, which closes the
      * directory object before terminating.
      */
     @JRubyMethod(name = "open", required = 1, frame = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject path, Block block) {
         RubyDir directory = 
             (RubyDir) recv.getRuntime().getDir().newInstance(context,
                     new IRubyObject[] { path }, Block.NULL_BLOCK);
 
         if (!block.isGiven()) return directory;
         
         try {
             return block.yield(context, directory);
         } finally {
             directory.close();
         }
     }
 
 // ----- Ruby Instance Methods -------------------------------------------------
 
     /**
      * Closes the directory stream.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         // Make sure any read()s after close fail.
         checkDir();
         
         isOpen = false;
 
         return getRuntime().getNil();
     }
 
     /**
      * Executes the block once for each entry in the directory.
      */
     @JRubyMethod(name = "each", frame = true)
     public IRubyObject each(ThreadContext context, Block block) {
         checkDir();
         
         String[] contents = snapshot;
         for (int i=0; i<contents.length; i++) {
             block.yield(context, getRuntime().newString(contents[i]));
         }
         return this;
     }
 
     /**
      * Returns the current position in the directory.
      */
     @JRubyMethod(name = {"tell", "pos"})
     public RubyInteger tell() {
         checkDir();
         return getRuntime().newFixnum(pos);
     }
 
     /**
      * Moves to a position <code>d</code>.  <code>pos</code> must be a value
      * returned by <code>tell</code> or 0.
      */
     @JRubyMethod(name = "seek", required = 1)
     public IRubyObject seek(IRubyObject newPos) {
         checkDir();
         
         set_pos(newPos);
         return this;
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public IRubyObject set_pos(IRubyObject newPos) {
         this.pos = RubyNumeric.fix2int(newPos);
         return newPos;
     }
 
     @JRubyMethod(name = "path")
     public IRubyObject path() {
         checkDir();
         
         return path.strDup();
     }
 
     /** Returns the next entry from this directory. */
     @JRubyMethod(name = "read")
     public IRubyObject read() {
         checkDir();
 
         if (pos >= snapshot.length) {
             return getRuntime().getNil();
         }
         RubyString result = getRuntime().newString(snapshot[pos]);
         pos++;
         return result;
     }
 
     /** Moves position in this directory to the first entry. */
     @JRubyMethod(name = "rewind")
     public IRubyObject rewind() {
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) throw getRuntime().newSecurityError("Insecure: can't close");
         checkDir();
 
         pos = 0;
         return this;
     }
 
 // ----- Helper Methods --------------------------------------------------------
 
     /** Returns a Java <code>File</code> object for the specified path.  If
      * <code>path</code> is not a directory, throws <code>IOError</code>.
      *
      * @param   path path for which to return the <code>File</code> object.
      * @param   mustExist is true the directory must exist.  If false it must not.
      * @throws  IOError if <code>path</code> is not a directory.
      */
     protected static JRubyFile getDir(final Ruby runtime, final String path, final boolean mustExist) {
         JRubyFile result = JRubyFile.create(runtime.getCurrentDirectory(),path);
         if (mustExist && !result.exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + path);
         }
         boolean isDirectory = result.isDirectory();
         
         if (mustExist && !isDirectory) {
             throw runtime.newErrnoENOTDIRError(path + " is not a directory");
         } else if (!mustExist && isDirectory) {
             throw runtime.newErrnoEEXISTError("File exists - " + path); 
         }
 
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Java Strings.
      */
     protected static List<String> getContents(File directory) {
         String[] contents = directory.list();
         List<String> result = new ArrayList<String>();
 
         // If an IO exception occurs (something odd, but possible)
         // A directory may return null.
         if (contents != null) {
             for (int i=0; i<contents.length; i++) {
                 result.add(contents[i]);
             }
         }
         return result;
     }
 
     /**
      * Returns the contents of the specified <code>directory</code> as an
      * <code>ArrayList</code> containing the names of the files as Ruby Strings.
      */
     protected static List<RubyString> getContents(File directory, Ruby runtime) {
         List<RubyString> result = new ArrayList<RubyString>();
         String[] contents = directory.list();
         
         for (int i = 0; i < contents.length; i++) {
             result.add(runtime.newString(contents[i]));
         }
         return result;
     }
 	
     /**
      * Returns the home directory of the specified <code>user</code> on the
      * system. If the home directory of the specified user cannot be found,
      * an <code>ArgumentError it thrown</code>.
      */
     public static IRubyObject getHomeDirectoryPath(IRubyObject recv, String user) {
         /*
          * TODO: This version is better than the hackish previous one. Windows
          *       behavior needs to be defined though. I suppose this version
          *       could be improved more too.
          * TODO: /etc/passwd is also inadequate for MacOSX since it does not
          *       use /etc/passwd for regular user accounts
          */
 
         String passwd = null;
         try {
             FileInputStream stream = new FileInputStream("/etc/passwd");
             int totalBytes = stream.available();
             byte[] bytes = new byte[totalBytes];
             stream.read(bytes);
+            stream.close();
             passwd = new String(bytes);
         } catch (IOException e) {
             return recv.getRuntime().getNil();
         }
 
         String[] rows = passwd.split("\n");
         int rowCount = rows.length;
         for (int i = 0; i < rowCount; i++) {
             String[] fields = rows[i].split(":");
             if (fields[0].equals(user)) {
                 return recv.getRuntime().newString(fields[5]);
             }
         }
 
         throw recv.getRuntime().newArgumentError("user " + user + " doesn't exist");
     }
 
     public static RubyString getHomeDirectoryPath(ThreadContext context, IRubyObject recv) {
         Ruby runtime = recv.getRuntime();
         RubyHash systemHash = (RubyHash) runtime.getObject().fastGetConstant("ENV_JAVA");
         RubyHash envHash = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         IRubyObject home = envHash.op_aref(context, runtime.newString("HOME"));
 
         if (home == null || home.isNil()) {
             home = systemHash.op_aref(context, runtime.newString("user.home"));
         }
 
         if (home == null || home.isNil()) {
             home = envHash.op_aref(context, runtime.newString("LOGDIR"));
         }
 
         if (home == null || home.isNil()) {
             throw runtime.newArgumentError("user.home/LOGDIR not set");
         }
 
         return (RubyString) home;
     }
 }
diff --git a/src/org/jruby/RubyIconv.java b/src/org/jruby/RubyIconv.java
index 08316e98dd..64c9261f94 100644
--- a/src/org/jruby/RubyIconv.java
+++ b/src/org/jruby/RubyIconv.java
@@ -1,332 +1,332 @@
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
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Koichiro Ohba <koichiro@meadowy.org>
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
 
 import java.nio.ByteBuffer;
 import java.nio.CharBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetDecoder;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.CodingErrorAction;
 import java.nio.charset.IllegalCharsetNameException;
 import java.nio.charset.MalformedInputException;
 import java.nio.charset.UnmappableCharacterException;
 import java.nio.charset.UnsupportedCharsetException;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Arity;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.ByteList;
 
 public class RubyIconv extends RubyObject {
     //static private final String TRANSLIT = "//translit";
     static private final String IGNORE = "//ignore";
 
     private CharsetDecoder fromEncoding;
     private CharsetEncoder toEncoding;
 
     public RubyIconv(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
-    private static ObjectAllocator ICONV_ALLOCATOR = new ObjectAllocator() {
+    private static final ObjectAllocator ICONV_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIconv(runtime, klass);
         }
     };
 
     public static void createIconv(Ruby runtime) {
         RubyClass iconvClass = runtime.defineClass("Iconv", runtime.getObject(), ICONV_ALLOCATOR);
         
         iconvClass.defineAnnotatedMethods(RubyIconv.class);
 
         RubyModule failure = iconvClass.defineModuleUnder("Failure");
         RubyClass argumentError = runtime.fastGetClass("ArgumentError");
 
         String[] iconvErrors = {"IllegalSequence", "InvalidCharacter", "InvalidEncoding", 
                 "OutOfRange", "BrokenLibrary"};
         
         for (int i = 0; i < iconvErrors.length; i++) {
             RubyClass subClass = iconvClass.defineClassUnder(iconvErrors[i], argumentError, RubyFailure.ICONV_FAILURE_ALLOCATOR);
             subClass.defineAnnotatedMethods(RubyFailure.class);
             subClass.includeModule(failure);
         }    
     }
     
     public static class RubyFailure extends RubyException {
         private IRubyObject success;
         private IRubyObject failed;
 
         public static RubyFailure newInstance(Ruby runtime, RubyClass excptnClass, String msg) {
             return new RubyFailure(runtime, excptnClass, msg);
         }
 
-        protected static ObjectAllocator ICONV_FAILURE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator ICONV_FAILURE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new RubyFailure(runtime, klass);
             }
         };
 
         protected RubyFailure(Ruby runtime, RubyClass rubyClass) {
             this(runtime, rubyClass, null);
         }
 
         public RubyFailure(Ruby runtime, RubyClass rubyClass, String message) {
             super(runtime, rubyClass, message);
         }
 
         @JRubyMethod(name = "initialize", required = 1, optional = 2, frame = true)
         public IRubyObject initialize(IRubyObject[] args, Block block) {
             super.initialize(args, block);
             success = args.length >= 2 ? args[1] : getRuntime().getNil();
             failed = args.length == 3 ? args[2] : getRuntime().getNil();
 
             return this;
         }
 
         @JRubyMethod(name = "success")
         public IRubyObject success() {
             return success;
         }
 
         @JRubyMethod(name = "failed")
         public IRubyObject failed() {
             return failed;
         }
 
         @JRubyMethod(name = "inspect")
         public IRubyObject inspect() {
             RubyModule rubyClass = getMetaClass();
             StringBuffer buffer = new StringBuffer("#<");
             buffer.append(rubyClass.getName()).append(": ").append(success.inspect().toString());
             buffer.append(", ").append(failed.inspect().toString()).append(">");
 
             return getRuntime().newString(buffer.toString());
         }
     }
 
     private static String getCharset(String encoding) {
         int index = encoding.indexOf("//");
         if (index == -1) return encoding;
         return encoding.substring(0, index);
     }
     
     /* Currently dead code, but useful when we figure out how to actually perform translit.
     private static boolean isTranslit(String encoding) {
         return encoding.toLowerCase().indexOf(TRANSLIT) != -1 ? true : false;
     }*/
     
     private static boolean isIgnore(String encoding) {
         return encoding.toLowerCase().indexOf(IGNORE) != -1 ? true : false;
     }
 
     @JRubyMethod(name = "open", required = 2, frame = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject to, IRubyObject from, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyIconv iconv =
             (RubyIconv) runtime.fastGetClass("Iconv").newInstance(context,
                     new IRubyObject[] { to, from }, Block.NULL_BLOCK);
         if (!block.isGiven()) return iconv;
 
         IRubyObject result = runtime.getNil();
         try {
             result = block.yield(context, iconv);
         } finally {
             iconv.close();
         }
 
         return result;
     }
     
     @JRubyMethod(name = "initialize", required = 2, frame = true)
     public IRubyObject initialize(IRubyObject arg1, IRubyObject arg2, Block unusedBlock) {
         Ruby runtime = getRuntime();
         if (!arg1.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + arg1.getMetaClass() + " into String");
         }
         if (!arg2.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + arg2.getMetaClass() + " into String");
         }
 
         String to = arg1.convertToString().toString();
         String from = arg2.convertToString().toString();
 
         try {
 
             fromEncoding = Charset.forName(getCharset(from)).newDecoder();
             toEncoding = Charset.forName(getCharset(to)).newEncoder();
 
             if (!isIgnore(from)) fromEncoding.onUnmappableCharacter(CodingErrorAction.REPORT);
             if (!isIgnore(to)) toEncoding.onUnmappableCharacter(CodingErrorAction.REPORT);
         } catch (IllegalCharsetNameException e) {
             throw runtime.newArgumentError("invalid encoding");
         } catch (UnsupportedCharsetException e) {
             throw runtime.newArgumentError("invalid encoding");
         } catch (Exception e) {
             throw runtime.newSystemCallError(e.toString());
         }
 
         return this;
     }
 
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         toEncoding = null;
         fromEncoding = null;
         return getRuntime().newString("");
     }
 
     @JRubyMethod(name = "iconv", required = 1, optional = 2)
     public IRubyObject iconv(IRubyObject[] args) {
         Ruby runtime = getRuntime();
         args = Arity.scanArgs(runtime, args, 1, 2);
         int start = 0;
         int end = -1;
 
         if (args[0].isNil()) {
             fromEncoding.reset();
             toEncoding.reset();
             return runtime.newString("");
         }
         if (!args[0].respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + args[0].getMetaClass() + " into String");
         }
         if (!args[1].isNil()) start = RubyNumeric.fix2int(args[1]);
         if (!args[2].isNil()) end = RubyNumeric.fix2int(args[2]);
         
         IRubyObject result = _iconv(args[0].convertToString(), start, end);
         return result;
     }
 
     // FIXME: We are assuming that original string will be raw bytes.  If -Ku is provided
     // this will not be true, but that is ok for now.  Deal with that when someone needs it.
     private IRubyObject _iconv(RubyString str, int start, int end) {
         ByteList bytes = str.getByteList();
         
         // treat start and end as start...end for end >= 0, start..end for end < 0
         if (start < 0) {
             start += bytes.length();
         }
         
         if (end < 0) {
             end += 1 + bytes.length();
         } else if (end > bytes.length()) {
             end = bytes.length();
         }
         
         if (start < 0 || end < start) { // invalid ranges result in an empty string
             return getRuntime().newString();
         }
         
         ByteBuffer buf = ByteBuffer.wrap(bytes.unsafeBytes(), bytes.begin() + start, end - start);
         
         try {
             CharBuffer cbuf = fromEncoding.decode(buf);
             buf = toEncoding.encode(cbuf);
         } catch (MalformedInputException e) {
         } catch (UnmappableCharacterException e) {
         } catch (CharacterCodingException e) {
             throw getRuntime().newInvalidEncoding("invalid sequence");
         } catch (IllegalStateException e) {
         }
         byte[] arr = buf.array();
         
         return getRuntime().newString(new ByteList(arr, 0, buf.limit()));
     }
 
     @JRubyMethod(name = "iconv", required = 2, optional = 1, meta = true)
     public static IRubyObject iconv(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         return convertWithArgs(recv, args, "iconv");
     }
     
     @JRubyMethod(name = "conv", required = 3, rest = true, meta = true)
     public static IRubyObject conv(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         return convertWithArgs(recv, args, "conv").join(context, recv.getRuntime().newString(""));
     }
     
     public static RubyArray convertWithArgs(IRubyObject recv, IRubyObject[] args, String function) {
         String fromEncoding = args[1].convertToString().toString();
         String toEncoding = args[0].convertToString().toString();
         RubyArray array = recv.getRuntime().newArray();
         
         for (int i = 2; i < args.length; i++) {
             array.append(convert2(fromEncoding, toEncoding, args[i].convertToString()));
         }
 
         return array;
     }
     
     /*
     private static IRubyObject convert(String fromEncoding, String toEncoding, RubyString original) 
         throws UnsupportedEncodingException {
         // Get all bytes from PLAIN string pretend they are not encoded in any way.
         byte[] string = original.getBytes();
         // Now create a string pretending it is from fromEncoding
         string = new String(string, fromEncoding).getBytes(toEncoding);
         // Finally recode back to PLAIN
         return RubyString.newString(original.getRuntime(), string);
     }
     */
 
     // FIXME: We are assuming that original string will be raw bytes.  If -Ku is provided
     // this will not be true, but that is ok for now.  Deal with that when someone needs it.
     private static IRubyObject convert2(String fromEncoding, String toEncoding, RubyString original) {
         // Don't bother to convert if it is already in toEncoding
         if (fromEncoding.equals(toEncoding)) return original;
         
         try {
             // Get all bytes from string and pretend they are not encoded in any way.
             ByteList bytes = original.getByteList();
             ByteBuffer buf = ByteBuffer.wrap(bytes.unsafeBytes(), bytes.begin(), bytes.length());
 
             CharsetDecoder decoder = Charset.forName(getCharset(fromEncoding)).newDecoder();
             
             if (!isIgnore(fromEncoding)) decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
 
             CharBuffer cbuf = decoder.decode(buf);
             CharsetEncoder encoder = Charset.forName(getCharset(toEncoding)).newEncoder();
             
             if (!isIgnore(toEncoding)) encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
 
             buf = encoder.encode(cbuf);
             byte[] arr = buf.array();
             return RubyString.newString(original.getRuntime(), new ByteList(arr,0,buf.limit()));
         } catch (UnsupportedCharsetException e) {
             throw original.getRuntime().newInvalidEncoding("invalid encoding");
         } catch (UnmappableCharacterException e) {
         } catch (CharacterCodingException e) {
         }
         return original.getRuntime().getNil();
     }
 }
diff --git a/src/org/jruby/RubyNameError.java b/src/org/jruby/RubyNameError.java
index 1897de4b1f..9e88bc7eb0 100644
--- a/src/org/jruby/RubyNameError.java
+++ b/src/org/jruby/RubyNameError.java
@@ -1,110 +1,112 @@
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
  * Copyright (C) 2006 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author Anders Bengtsson
  */
 public class RubyNameError extends RubyException {
     private IRubyObject name;
     
     private static ObjectAllocator NAMEERROR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyNameError(runtime, klass);
         }
     };
 
     public static RubyClass createNameErrorClass(Ruby runtime, RubyClass standardErrorClass) {
         RubyClass nameErrorClass = runtime.defineClass("NameError", standardErrorClass, NAMEERROR_ALLOCATOR);
 
         nameErrorClass.defineAnnotatedMethods(RubyNameError.class);
 
         return nameErrorClass;
     }
 
     protected RubyNameError(Ruby runtime, RubyClass exceptionClass) {
-        this(runtime, exceptionClass, exceptionClass.getName().toString());
+        this(runtime, exceptionClass, exceptionClass.getName());
     }
 
     public RubyNameError(Ruby runtime, RubyClass exceptionClass, String message) {
         this(runtime, exceptionClass, message, null);
     }
 
     public RubyNameError(Ruby runtime, RubyClass exceptionClass, String message, String name) {
         super(runtime, exceptionClass, message);
         this.name = name == null ? runtime.getNil() : runtime.newString(name);
     }
     
     @JRubyMethod(name = "exception", rest = true, meta = true)
     public static RubyException newRubyNameError(IRubyObject recv, IRubyObject[] args) {
         RubyClass klass = (RubyClass)recv;
         
         RubyException newError = (RubyException) klass.allocate();
         
         newError.callInit(args, Block.NULL_BLOCK);
         
         return newError;
     }
 
     @JRubyMethod(name = "initialize", optional = 2, frame = true)
+    @Override
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length > 1) {
             name = args[args.length - 1];
             int newLength = args.length > 2 ? args.length - 2 : args.length - 1;
 
             IRubyObject []tmpArgs = new IRubyObject[newLength];
             System.arraycopy(args, 0, tmpArgs, 0, newLength);
             args = tmpArgs;
         } else {
             name = getRuntime().getNil();
         }
 
         super.initialize(args, block);
         return this;
     }
 
     @JRubyMethod(name = "to_s")
+    @Override
     public IRubyObject to_s() {
         if (message.isNil()) return getRuntime().newString(message.getMetaClass().getName());
         RubyString str = message.convertToString();
         if (str != message) message = str;
         if (isTaint()) message.setTaint(true);
         return message;
     }
 
     @JRubyMethod(name = "name")
     public IRubyObject name() {
         return name;
     }
 }
diff --git a/src/org/jruby/RubyNil.java b/src/org/jruby/RubyNil.java
index 620c5b9840..c23ffcae29 100644
--- a/src/org/jruby/RubyNil.java
+++ b/src/org/jruby/RubyNil.java
@@ -1,178 +1,178 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyNil extends RubyObject {
     public RubyNil(Ruby runtime) {
         super(runtime, runtime.getNilClass(), false);
         flags |= NIL_F | FALSE_F;
     }
     
-    public static ObjectAllocator NIL_ALLOCATOR = new ObjectAllocator() {
+    public static final ObjectAllocator NIL_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return runtime.getNil();
         }
     };
     
     public static RubyClass createNilClass(Ruby runtime) {
         RubyClass nilClass = runtime.defineClass("NilClass", runtime.getObject(), NIL_ALLOCATOR);
         runtime.setNilClass(nilClass);
         nilClass.index = ClassIndex.NIL;
         
         nilClass.defineAnnotatedMethods(RubyNil.class);
         
         nilClass.getMetaClass().undefineMethod("new");
         
         // FIXME: This is causing a verification error for some reason
         //nilClass.dispatcher = callbackFactory.createDispatcher(nilClass);
         
         return nilClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.NIL;
     }
     
 //    public RubyClass getMetaClass() {
 //        return runtime.getNilClass();
 //    }
 
     public boolean isImmediate() {
         return true;
     }
 
     public RubyClass getSingletonClass() {
         return metaClass;
     }
     
     // Methods of the Nil Class (nil_*):
     
     /** nil_to_i
      *
      */
     @JRubyMethod(name = "to_i")
     public static RubyFixnum to_i(IRubyObject recv) {
         return RubyFixnum.zero(recv.getRuntime());
     }
     
     /**
      * nil_to_f
      *
      */
     @JRubyMethod(name = "to_f")
     public static RubyFloat to_f(IRubyObject recv) {
         return RubyFloat.newFloat(recv.getRuntime(), 0.0D);
     }
     
     /** nil_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     public static RubyString to_s(IRubyObject recv) {
         return recv.getRuntime().newString("");
     }
     
     /** nil_to_a
      *
      */
     @JRubyMethod(name = "to_a")
     public static RubyArray to_a(IRubyObject recv) {
         return recv.getRuntime().newEmptyArray();
     }
     
     /** nil_inspect
      *
      */
     @JRubyMethod(name = "inspect")
     public static RubyString inspect(IRubyObject recv) {
         return recv.getRuntime().newString("nil");
     }
     
     /** nil_type
      *
      */
     @JRubyMethod(name = "type")
     public static RubyClass type(IRubyObject recv) {
         return recv.getRuntime().getNilClass();
     }
     
     /** nil_and
      *
      */
     @JRubyMethod(name = "&", required = 1)
     public static RubyBoolean op_and(IRubyObject recv, IRubyObject obj) {
         return recv.getRuntime().getFalse();
     }
     
     /** nil_or
      *
      */
     @JRubyMethod(name = "|", required = 1)
     public static RubyBoolean op_or(IRubyObject recv, IRubyObject obj) {
         return recv.getRuntime().newBoolean(obj.isTrue());
     }
     
     /** nil_xor
      *
      */
     @JRubyMethod(name = "^", required = 1)
     public static RubyBoolean op_xor(IRubyObject recv, IRubyObject obj) {
         return recv.getRuntime().newBoolean(obj.isTrue());
     }
 
     @JRubyMethod(name = "nil?")
     public IRubyObject nil_p() {
         return getRuntime().getTrue();
     }
     
     public RubyFixnum id() {
         return getRuntime().newFixnum(4);
     }
     
     public IRubyObject taint() {
         return this;
     }
 
     public IRubyObject freeze() {
         return this;
     }
 }
diff --git a/src/org/jruby/RubyNoMethodError.java b/src/org/jruby/RubyNoMethodError.java
index 65ec3c9b08..5918d88ad2 100644
--- a/src/org/jruby/RubyNoMethodError.java
+++ b/src/org/jruby/RubyNoMethodError.java
@@ -1,81 +1,81 @@
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
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class RubyNoMethodError extends RubyNameError {
     private IRubyObject args;
 
-    private static ObjectAllocator NOMETHODERROR_ALLOCATOR = new ObjectAllocator() {
+    private static final ObjectAllocator NOMETHODERROR_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyNoMethodError(runtime, klass);
         }
     };
 
     public static RubyClass createNoMethodErrorClass(Ruby runtime, RubyClass nameErrorClass) {
         RubyClass noMethodErrorClass = runtime.defineClass("NoMethodError", nameErrorClass, NOMETHODERROR_ALLOCATOR);
         
         noMethodErrorClass.defineAnnotatedMethods(RubyNoMethodError.class);
 
         return noMethodErrorClass;
     }
 
     protected RubyNoMethodError(Ruby runtime, RubyClass exceptionClass) {
-        super(runtime, exceptionClass, exceptionClass.getName().toString());
+        super(runtime, exceptionClass, exceptionClass.getName());
         this.args = runtime.getNil();
     }
     
     public RubyNoMethodError(Ruby runtime, RubyClass exceptionClass, String message, String name, IRubyObject args) {
         super(runtime, exceptionClass, message,  name);
         this.args = args;
     }    
 
     @JRubyMethod(name = "initialize", optional = 3, frame = true)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length > 2) {
             this.args = args[args.length - 1];
             IRubyObject []tmpArgs = new IRubyObject[args.length - 1];
             System.arraycopy(args, 0, tmpArgs, 0, tmpArgs.length);
             args = tmpArgs;
         } else {
             this.args = getRuntime().getNil();
         }
 
         super.initialize(args, block);
         return this;
     }
     
     @JRubyMethod(name = "args")
     public IRubyObject args() {
         return args;
     }
 
 }
diff --git a/src/org/jruby/RubyNumeric.java b/src/org/jruby/RubyNumeric.java
index 64092bbcf0..29ca4dba5e 100644
--- a/src/org/jruby/RubyNumeric.java
+++ b/src/org/jruby/RubyNumeric.java
@@ -1,780 +1,780 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Antti Karanta <Antti.Karanta@napa.fi>
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
 
 import java.math.BigInteger;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.Convert;
 
 /**
  * Base class for all numerical types in ruby.
  */
 // TODO: Numeric.new works in Ruby and it does here too.  However trying to use
 //   that instance in a numeric operation should generate an ArgumentError. Doing
 //   this seems so pathological I do not see the need to fix this now.
 public class RubyNumeric extends RubyObject {
     
     public static RubyClass createNumericClass(Ruby runtime) {
         RubyClass numeric = runtime.defineClass("Numeric", runtime.getObject(), NUMERIC_ALLOCATOR);
         runtime.setNumeric(numeric);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyNumeric.class);
 
         numeric.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyNumeric;
             }
         };
 
         numeric.includeModule(runtime.getComparable());
         numeric.defineAnnotatedMethods(RubyNumeric.class);
         numeric.dispatcher = callbackFactory.createDispatcher(numeric);
 
         return numeric;
     }
 
-    protected static ObjectAllocator NUMERIC_ALLOCATOR = new ObjectAllocator() {
+    protected static final ObjectAllocator NUMERIC_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyNumeric(runtime, klass);
         }
     };
 
     public static double DBL_EPSILON=2.2204460492503131e-16;
     
     public RubyNumeric(Ruby runtime, RubyClass metaClass) {
         super(runtime, metaClass);
     }
 
     public RubyNumeric(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         super(runtime, metaClass, useObjectSpace);
     }    
 
     // The implementations of these are all bonus (see TODO above)  I was going
     // to throw an error from these, but it appears to be the wrong place to
     // do it.
     public double getDoubleValue() {
         return 0;
     }
 
     public long getLongValue() {
         return 0;
     }
     
     public static RubyNumeric newNumeric(Ruby runtime) {
     	return new RubyNumeric(runtime, runtime.getNumeric());
     }
 
     /*  ================
      *  Utility Methods
      *  ================ 
      */
 
     /** rb_num2int, NUM2INT
      * 
      */
     public static int num2int(IRubyObject arg) {
         long num = num2long(arg);
 
         checkInt(arg, num);
         return (int)num;
     }
     
     /** check_int
      * 
      */
     public static void checkInt(IRubyObject arg, long num){
         String s;
         if (num < Integer.MIN_VALUE) {
             s = "small";
         } else if (num > Integer.MAX_VALUE) {
             s = "big";
         } else {
             return;
         }
         throw arg.getRuntime().newRangeError("integer " + num + " too " + s + " to convert to `int'");
     }
 
     /**
      * NUM2CHR
      */
     public static byte num2chr(IRubyObject arg) {
         if (arg instanceof RubyString) {
             String value = ((RubyString) arg).toString();
 
             if (value != null && value.length() > 0) return (byte) value.charAt(0);
         } 
 
         return (byte) num2int(arg);
     }
 
     /** rb_num2long and FIX2LONG (numeric.c)
      * 
      */
     public static long num2long(IRubyObject arg) {
         if (arg instanceof RubyFixnum) {
             return ((RubyFixnum) arg).getLongValue();
         }
         if (arg.isNil()) {
             throw arg.getRuntime().newTypeError("no implicit conversion from nil to integer");
     }
 
         if (arg instanceof RubyFloat) {
             double aFloat = ((RubyFloat) arg).getDoubleValue();
             if (aFloat <= (double) Long.MAX_VALUE && aFloat >= (double) Long.MIN_VALUE) {
                 return (long) aFloat;
             } else {
                 // TODO: number formatting here, MRI uses "%-.10g", 1.4 API is a must?
                 throw arg.getRuntime().newRangeError("float " + aFloat + " out of range of integer");
             }
         } else if (arg instanceof RubyBignum) {
             return RubyBignum.big2long((RubyBignum) arg);
         }
         return arg.convertToInteger().getLongValue();
     }
 
     /** rb_dbl2big + LONG2FIX at once (numeric.c)
      * 
      */
     public static IRubyObject dbl2num(Ruby runtime, double val) {
         if (Double.isInfinite(val)) {
             throw runtime.newFloatDomainError(val < 0 ? "-Infinity" : "Infinity");
         }
         if (Double.isNaN(val)) {
             throw runtime.newFloatDomainError("NaN");
         }
 
         if (val >= (double) RubyFixnum.MAX || val < (double) RubyFixnum.MIN) {
             return RubyBignum.newBignum(runtime, val);
         }
         return RubyFixnum.newFixnum(runtime, (long) val);
     }
 
     /** rb_num2dbl and NUM2DBL
      * 
      */
     public static double num2dbl(IRubyObject arg) {
         if (arg instanceof RubyFloat) {
             return ((RubyFloat) arg).getDoubleValue();
         } else if (arg instanceof RubyString) {
             throw arg.getRuntime().newTypeError("no implicit conversion to float from string");
         } else if (arg == arg.getRuntime().getNil()) {
             throw arg.getRuntime().newTypeError("no implicit conversion to float from nil");
         }
         return arg.convertToFloat().getDoubleValue();
     }
 
     /** rb_dbl_cmp (numeric.c)
      * 
      */
     public static IRubyObject dbl_cmp(Ruby runtime, double a, double b) {
         if (Double.isNaN(a) || Double.isNaN(b)) {
             return runtime.getNil();
         }
         if (a > b) {
             return RubyFixnum.one(runtime);
         }
         if (a < b) {
             return RubyFixnum.minus_one(runtime);
         }
         return RubyFixnum.zero(runtime);
     }
 
     public static long fix2long(IRubyObject arg) {
         return ((RubyFixnum) arg).getLongValue();
     }
 
     public static int fix2int(IRubyObject arg) {
         long num = arg instanceof RubyFixnum ? fix2long(arg) : num2long(arg);
 
         checkInt(arg, num);
         return (int) num;
         }
 
     public static RubyInteger str2inum(Ruby runtime, RubyString str, int base) {
         return str2inum(runtime,str,base,false);
     }
 
     public static RubyNumeric int2fix(Ruby runtime, long val) {
         return RubyFixnum.newFixnum(runtime,val);
     }
 
     /** rb_num2fix
      * 
      */
     public static IRubyObject num2fix(IRubyObject val) {
         if (val instanceof RubyFixnum) {
             return val;
         }
         if (val instanceof RubyBignum) {
             // any BigInteger is bigger than Fixnum and we don't have FIXABLE
             throw val.getRuntime().newRangeError("integer " + val + " out of range of fixnum");
         }
         return RubyFixnum.newFixnum(val.getRuntime(), num2long(val));
     }
 
     /**
      * Converts a string representation of an integer to the integer value. 
      * Parsing starts at the beginning of the string (after leading and 
      * trailing whitespace have been removed), and stops at the end or at the 
      * first character that can't be part of an integer.  Leading signs are
      * allowed. If <code>base</code> is zero, strings that begin with '0[xX]',
      * '0[bB]', or '0' (optionally preceded by a sign) will be treated as hex, 
      * binary, or octal numbers, respectively.  If a non-zero base is given, 
      * only the prefix (if any) that is appropriate to that base will be 
      * parsed correctly.  For example, if the base is zero or 16, the string
      * "0xff" will be converted to 256, but if the base is 10, it will come out 
      * as zero, since 'x' is not a valid decimal digit.  If the string fails 
      * to parse as a number, zero is returned.
      * 
      * @param runtime  the ruby runtime
      * @param str   the string to be converted
      * @param base  the expected base of the number (for example, 2, 8, 10, 16),
      *              or 0 if the method should determine the base automatically 
      *              (defaults to 10). Values 0 and 2-36 are permitted. Any other
      *              value will result in an ArgumentError.
      * @param strict if true, enforce the strict criteria for String encoding of
      *               numeric values, as required by Integer('n'), and raise an
      *               exception when those criteria are not met. Otherwise, allow
      *               lax expression of values, as permitted by String#to_i, and
      *               return a value in almost all cases (excepting illegal radix).
      *               TODO: describe the rules/criteria
      * @return  a RubyFixnum or (if necessary) a RubyBignum representing 
      *          the result of the conversion, which will be zero if the 
      *          conversion failed.
      */
     public static RubyInteger str2inum(Ruby runtime, RubyString str, int base, boolean strict) {
         if (base != 0 && (base < 2 || base > 36)) {
             throw runtime.newArgumentError("illegal radix " + base);
             }
         ByteList bytes = str.getByteList();
         try {
             return runtime.newFixnum(Convert.byteListToLong(bytes,base,strict));
 
         } catch (InvalidIntegerException e) {
             if (strict) {
                 throw runtime.newArgumentError("invalid value for Integer: "
                         + str.callMethod(runtime.getCurrentContext(), "inspect").toString());
             }
             return RubyFixnum.zero(runtime);
         } catch (NumberTooLargeException e) {
         try {
                 BigInteger bi = Convert.byteListToBigInteger(bytes,base,strict);
                 return new RubyBignum(runtime,bi);
             } catch (InvalidIntegerException e2) {
                 if(strict) {
                     throw runtime.newArgumentError("invalid value for Integer: "
                             + str.callMethod(runtime.getCurrentContext(), "inspect").toString());
                 }
                 return RubyFixnum.zero(runtime);
             }
         }
     }
 
     public static RubyFloat str2fnum(Ruby runtime, RubyString arg) {
         return str2fnum(runtime,arg,false);
     }
 
     /**
      * Converts a string representation of a floating-point number to the 
      * numeric value.  Parsing starts at the beginning of the string (after 
      * leading and trailing whitespace have been removed), and stops at the 
      * end or at the first character that can't be part of a number.  If 
      * the string fails to parse as a number, 0.0 is returned.
      * 
      * @param runtime  the ruby runtime
      * @param arg   the string to be converted
      * @param strict if true, enforce the strict criteria for String encoding of
      *               numeric values, as required by Float('n'), and raise an
      *               exception when those criteria are not met. Otherwise, allow
      *               lax expression of values, as permitted by String#to_f, and
      *               return a value in all cases.
      *               TODO: describe the rules/criteria
      * @return  a RubyFloat representing the result of the conversion, which
      *          will be 0.0 if the conversion failed.
      */
     public static RubyFloat str2fnum(Ruby runtime, RubyString arg, boolean strict) {
         final double ZERO = 0.0;
         
         try {
             return new RubyFloat(runtime,Convert.byteListToDouble(arg.getByteList(),strict));
         } catch (NumberFormatException e) {
             if (strict) {
                 throw runtime.newArgumentError("invalid value for Float(): "
                         + arg.callMethod(runtime.getCurrentContext(), "inspect").toString());
             }
             return new RubyFloat(runtime,ZERO);
         }
     }
     
     /** Numeric methods. (num_*)
      *
      */
     
     protected IRubyObject[] getCoerced(ThreadContext context, IRubyObject other, boolean error) {
         IRubyObject result;
         
         try {
             result = other.callMethod(context, "coerce", this);
         } catch (RaiseException e) {
             if (error) {
                 throw getRuntime().newTypeError(
                         other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
             }
              
             return null;
         }
         
         if (!(result instanceof RubyArray) || ((RubyArray)result).getLength() != 2) {
             throw getRuntime().newTypeError("coerce must return [x, y]");
         }
         
         return ((RubyArray)result).toJavaArray();
     }
 
     protected IRubyObject callCoerced(ThreadContext context, String method, IRubyObject other, boolean err) {
         IRubyObject[] args = getCoerced(context, other, err);
         if(args == null) {
             return getRuntime().getNil();
         }
         return args[0].callMethod(context, method, args[1]);
     }
 
     protected IRubyObject callCoerced(ThreadContext context, String method, IRubyObject other) {
         IRubyObject[] args = getCoerced(context, other, false);
         if(args == null) {
             return getRuntime().getNil();
         }
         return args[0].callMethod(context, method, args[1]);
     }
     
     // beneath are rewritten coercions that reflect MRI logic, the aboves are used only by RubyBigDecimal
 
     /** coerce_body
      *
      */
     protected final IRubyObject coerceBody(ThreadContext context, IRubyObject other) {
         return other.callMethod(context, "coerce", this);
     }
 
     /** do_coerce
      * 
      */
     protected final RubyArray doCoerce(ThreadContext context, IRubyObject other, boolean err) {
         IRubyObject result;
         try {
             result = coerceBody(context, other);
         } catch (RaiseException e) {
             if (err) {
                 throw getRuntime().newTypeError(
                         other.getMetaClass().getName() + " can't be coerced into " + getMetaClass().getName());
             }
             return null;
         }
     
         if (!(result instanceof RubyArray) || ((RubyArray) result).getLength() != 2) {
             throw getRuntime().newTypeError("coerce must return [x, y]");
         }
         return (RubyArray) result;
     }
 
     /** rb_num_coerce_bin
      *  coercion taking two arguments
      */
     protected final IRubyObject coerceBin(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, true);
         return (ary.eltInternal(0)).callMethod(context, method, ary.eltInternal(1));
     }
     
     /** rb_num_coerce_cmp
      *  coercion used for comparisons
      */
     protected final IRubyObject coerceCmp(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, false);
         if (ary == null) {
             return getRuntime().getNil(); // MRI does it!
         } 
         return (ary.eltInternal(0)).callMethod(context, method, ary.eltInternal(1));
     }
         
     /** rb_num_coerce_relop
      *  coercion used for relative operators
      */
     protected final IRubyObject coerceRelOp(ThreadContext context, String method, IRubyObject other) {
         RubyArray ary = doCoerce(context, other, false);
         if (ary != null) {
             IRubyObject result = (ary.eltInternal(0)).callMethod(context, method,ary.eltInternal(1));
             if (!result.isNil()) {
                 return result;
             }
         }
     
         RubyComparable.cmperr(this, other); // MRI also does it that way       
         return null; // not reachd as in MRI
     } 
         
     public RubyNumeric asNumeric() {
         return this;
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** num_sadded
      *
      */
     @JRubyMethod(name = "singleton_method_added", required = 1)
     public IRubyObject sadded(IRubyObject name) {
         throw getRuntime().newTypeError("can't define singleton method " + name + " for " + getType().getName());
     } 
         
     /** num_init_copy
      *
      */
     @Override
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject initialize_copy(IRubyObject arg) {
         throw getRuntime().newTypeError("can't copy " + getType().getName());
     }
     
     /** num_coerce
      *
      */
     @JRubyMethod(name = "coerce", required = 1)
     public IRubyObject coerce(IRubyObject other) {
         if (getMetaClass() == other.getMetaClass()) {
             return getRuntime().newArray(other, this);
         } 
 
         return getRuntime().newArray(RubyKernel.new_float(this, other), RubyKernel.new_float(this, this));
     }
 
     /** num_uplus
      *
      */
     @JRubyMethod(name = "+@")
     public IRubyObject op_uplus() {
         return this;
     }
 
     /** num_uminus
      *
      */
     @JRubyMethod(name = "-@")
     public IRubyObject op_uminus(ThreadContext context) {
         RubyFixnum zero = RubyFixnum.zero(getRuntime());
         RubyArray ary = zero.doCoerce(context, this, true);
         return ary.eltInternal(0).callMethod(context, MethodIndex.OP_MINUS, "-", ary.eltInternal(1));
     }
     
     /** num_cmp
      *
      */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(IRubyObject other) {
         if (this == other) { // won't hurt fixnums
             return RubyFixnum.zero(getRuntime());
         }
         return getRuntime().getNil();
     }
 
     /** num_eql
      *
      */
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(ThreadContext context, IRubyObject other) {
         if (getMetaClass() != other.getMetaClass()) {
             return getRuntime().getFalse();
         }
         return op_equal(context, other);
     }
             
     /** num_quo
      *
      */
     @JRubyMethod(name = "quo", required = 1)
     public IRubyObject quo(ThreadContext context, IRubyObject other) {
         return callMethod(context, "/", other);
     }
 
     /** num_div
      * 
      */
     @JRubyMethod(name = "div", required = 1)
     public IRubyObject div(ThreadContext context, IRubyObject other) {
         return callMethod(context, "/", other).convertToFloat().floor();
     }
 
     /** num_divmod
      * 
      */
     @JRubyMethod(name = "divmod", required = 1)
     public IRubyObject divmod(ThreadContext context, IRubyObject other) {
         return RubyArray.newArray(getRuntime(), div(context, other), modulo(context, other));
     }
 
     /** num_modulo
      *
      */
     @JRubyMethod(name = "modulo", required = 1)
     public IRubyObject modulo(ThreadContext context, IRubyObject other) {
         return callMethod(context, "%", other);
     }
 
     /** num_remainder
      *
      */
     @JRubyMethod(name = "remainder", required = 1)
     public IRubyObject remainder(ThreadContext context, IRubyObject dividend) {
         IRubyObject z = callMethod(context, "%", dividend);
         IRubyObject x = this;
         RubyFixnum zero = RubyFixnum.zero(getRuntime());
 
         if (z.op_equal(context, zero).isTrue()) {
             return z;
         } else if (x.callMethod(context, MethodIndex.OP_LT, "<", zero).isTrue()
                         && dividend.callMethod(context, MethodIndex.OP_GT, ">", zero).isTrue()
                    || x.callMethod(context, MethodIndex.OP_GT, ">", zero).isTrue()
                         && (dividend.callMethod(context, MethodIndex.OP_LT, "<", zero)).isTrue()) {
             return z.callMethod(context, MethodIndex.OP_MINUS, "-",dividend);
         } else {
             return z;
         }
     }
 
     /** num_abs
      *
      */
     @JRubyMethod(name = "abs")
     public IRubyObject abs(ThreadContext context) {
         if (callMethod(context, MethodIndex.OP_LT, "<", RubyFixnum.zero(getRuntime())).isTrue()) {
             return (RubyNumeric) callMethod(context, "-@");
         }
         return this;
     }
     
     /** num_to_int
      * 
      */
     @JRubyMethod(name = "to_int")
     public IRubyObject to_int(ThreadContext context) {
         return RuntimeHelpers.invoke(context, this, MethodIndex.TO_I, "to_i", IRubyObject.NULL_ARRAY);
     }
 
     /** num_int_p
      *
      */
     @JRubyMethod(name = "integer?")
     public IRubyObject integer_p() {
         return getRuntime().getFalse();
     }
     
     /** num_zero_p
      *
      */
     @JRubyMethod(name = "zero?")
     public IRubyObject zero_p(ThreadContext context) {
         // Will always return a boolean
         return op_equal(context, RubyFixnum.zero(getRuntime())).isTrue() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
     
     /** num_nonzero_p
      *
      */
     @JRubyMethod(name = "nonzero?")
     public IRubyObject nonzero_p(ThreadContext context) {
         if (callMethod(context, "zero?").isTrue()) {
             return getRuntime().getNil();
         }
         return this;
     }
 
     /** num_floor
      *
      */
     @JRubyMethod(name = "floor")
     public IRubyObject floor() {
         return convertToFloat().floor();
     }
         
     /** num_ceil
      *
      */
     @JRubyMethod(name = "ceil")
     public IRubyObject ceil() {
         return convertToFloat().ceil();
     }
 
     /** num_round
      *
      */
     @JRubyMethod(name = "round")
     public IRubyObject round() {
         return convertToFloat().round();
     }
 
     /** num_truncate
      *
      */
     @JRubyMethod(name = "truncate")
     public IRubyObject truncate() {
         return convertToFloat().truncate();
     }
     
     @JRubyMethod(name = "step", required = 1, optional = 1, frame = true)
     public IRubyObject step(ThreadContext context, IRubyObject[] args, Block block) {
         IRubyObject to;
         IRubyObject step;
         
         if(args.length == 1){ 
             to = args[0];
             step = RubyFixnum.one(getRuntime());
         } else if (args.length == 2) {
             to = args[0];
             step = args[1];
         }else{
             throw getRuntime().newTypeError("wrong number of arguments");
         }
         
         if (this instanceof RubyFixnum && to instanceof RubyFixnum && step instanceof RubyFixnum) {
             long value = getLongValue();
             long end = ((RubyFixnum) to).getLongValue();
             long diff = ((RubyFixnum) step).getLongValue();
 
             if (diff == 0) {
                 throw getRuntime().newArgumentError("step cannot be 0");
             }
             if (diff > 0) {
                 for (long i = value; i <= end; i += diff) {
                     block.yield(context, RubyFixnum.newFixnum(getRuntime(), i));
                 }
             } else {
                 for (long i = value; i >= end; i += diff) {
                     block.yield(context, RubyFixnum.newFixnum(getRuntime(), i));
                 }
             }
         } else if (this instanceof RubyFloat || to instanceof RubyFloat || step instanceof RubyFloat) {
             double beg = num2dbl(this);
             double end = num2dbl(to);
             double unit = num2dbl(step);
 
             if (unit == 0) {
                 throw getRuntime().newArgumentError("step cannot be 0");
             }           
             
             double n = (end - beg)/unit;
             double err = (Math.abs(beg) + Math.abs(end) + Math.abs(end - beg)) / Math.abs(unit) * DBL_EPSILON;
             
             if (err>0.5) {
                 err=0.5;            
             }
             n = Math.floor(n + err) + 1;
             
             for(double i = 0; i < n; i++){
                 block.yield(context, RubyFloat.newFloat(getRuntime(), i * unit + beg));
             }
 
         } else {
             RubyNumeric i = this;
             
             int cmp;
             String cmpString;
             if (((RubyBoolean) step.callMethod(context, MethodIndex.OP_GT, ">", RubyFixnum.zero(getRuntime()))).isTrue()) {
                 cmp = MethodIndex.OP_GT;
             } else {
                 cmp = MethodIndex.OP_LT;
             }
             cmpString = MethodIndex.NAMES.get(cmp);
 
             while (true) {
                 if (i.callMethod(context, cmp, cmpString, to).isTrue()) {
                     break;
                 }
                 block.yield(context, i);
                 i = (RubyNumeric) i.callMethod(context, MethodIndex.OP_PLUS, "+", step);
             }
         }
         return this;
     }
 
     /** num_equal, doesn't override RubyObject.op_equal
      *
      */
     protected final IRubyObject op_num_equal(ThreadContext context, IRubyObject other) {
         // it won't hurt fixnums
         if (this == other)  return getRuntime().getTrue();
 
         return other.callMethod(context, MethodIndex.EQUALEQUAL, "==", this);
     }
 
     public static class InvalidIntegerException extends NumberFormatException {
         private static final long serialVersionUID = 55019452543252148L;
         
         public InvalidIntegerException() {
             super();
         }
         public InvalidIntegerException(String message) {
             super(message);
         }
         public Throwable fillInStackTrace() {
             return this;
         }
     }
     
     public static class NumberTooLargeException extends NumberFormatException {
         private static final long serialVersionUID = -1835120694982699449L;
         public NumberTooLargeException() {
             super();
         }
         public NumberTooLargeException(String message) {
             super(message);
         }
         public Throwable fillInStackTrace() {
             return this;
         }
     }
     
 }
diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index 7e7b01b0f3..05c12d1bd8 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,822 +1,822 @@
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
 import java.nio.channels.Channel;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.HashMap;
 import java.util.Map;
 
 import java.util.Set;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.internal.runtime.FutureThread;
 import org.jruby.internal.runtime.NativeThread;
 import org.jruby.internal.runtime.RubyNativeThread;
 import org.jruby.internal.runtime.RubyRunnable;
 import org.jruby.internal.runtime.ThreadLike;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.TimeoutException;
 import java.util.concurrent.locks.ReentrantLock;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.Visibility;
 
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
 public class RubyThread extends RubyObject {
     private ThreadLike threadImpl;
     private RubyFixnum priority;
     private final Map<IRubyObject, IRubyObject> threadLocalVariables = new HashMap<IRubyObject, IRubyObject>();
     private boolean abortOnException;
     private IRubyObject finalResult;
     private RaiseException exitingException;
     private IRubyObject receivedException;
     private RubyThreadGroup threadGroup;
 
     private final ThreadService threadService;
     private volatile boolean isStopped = false;
     public Object stopLock = new Object();
     
     private volatile boolean killed = false;
     public Object killLock = new Object();
     
     public final ReentrantLock lock = new ReentrantLock();
     
     private static final boolean DEBUG = false;
 
     protected RubyThread(Ruby runtime, RubyClass type) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         // set to default thread group
         RubyThreadGroup defaultThreadGroup = (RubyThreadGroup)runtime.getThreadGroup().fastGetConstant("Default");
         defaultThreadGroup.add(this, Block.NULL_BLOCK);
         finalResult = runtime.getNil();
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
 
         threadClass.defineAnnotatedMethods(RubyThread.class);
 
         RubyThread rubyThread = new RubyThread(runtime, threadClass);
         // TODO: need to isolate the "current" thread from class creation
         rubyThread.threadImpl = new NativeThread(rubyThread, Thread.currentThread());
         runtime.getThreadService().setMainThread(rubyThread);
         
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
         
         context.preAdoptThread();
         
         return rubyThread;
     }
     
     @JRubyMethod(name = "initialize", rest = true, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (!block.isGiven()) throw getRuntime().newThreadError("must be called with a block");
 
         if (RubyInstanceConfig.POOLING_ENABLED) {
             threadImpl = new FutureThread(this, new RubyRunnable(this, args, block));
         } else {
             threadImpl = new NativeThread(this, new RubyNativeThread(this, args, block));
         }
         threadImpl.start();
         
         return this;
     }
     
     private static RubyThread startThread(final IRubyObject recv, final IRubyObject[] args, boolean callInit, Block block) {
         RubyThread rubyThread = new RubyThread(recv.getRuntime(), (RubyClass) recv);
         
         if (callInit) {
             rubyThread.callInit(args, block);
         } else {
             // for Thread::start, which does not call the subclass's initialize
             rubyThread.initialize(args, block);
         }
         
         return rubyThread;
     }
     
     private void ensureCurrent(ThreadContext context) {
         if (this != context.getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     private void ensureNotCurrent() {
         if (this == getRuntime().getCurrentContext().getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
-    public void cleanTerminate(IRubyObject result) {
+    public synchronized void cleanTerminate(IRubyObject result) {
         finalResult = result;
         isStopped = true;
     }
 
     public void pollThreadEvents() {
         pollThreadEvents(getRuntime().getCurrentContext());
     }
     
     public void pollThreadEvents(ThreadContext context) {
         // check for criticalization *before* locking ourselves
         threadService.waitForCritical();
 
         ensureCurrent(context);
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before");
         if (killed) throw new ThreadKill();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " after");
         if (receivedException != null) {
             // clear this so we don't keep re-throwing
             IRubyObject raiseException = receivedException;
             receivedException = null;
             RubyModule kernelModule = getRuntime().getKernel();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before propagating exception: " + killed);
             kernelModule.callMethod(context, "raise", raiseException);
         }
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
 
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject op_aref(IRubyObject key) {
         IRubyObject value;
         if ((value = threadLocalVariables.get(getSymbolKey(key))) != null) {
             return value;
         }
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "[]=", required = 2)
     public IRubyObject op_aset(IRubyObject key, IRubyObject value) {
         key = getSymbolKey(key);
         
         threadLocalVariables.put(key, value);
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
         return threadImpl.isAlive() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "join", optional = 1)
     public IRubyObject join(IRubyObject[] args) {
         long timeoutMillis = 0;
         if (args.length > 0) {
             if (args.length > 1) {
                 throw getRuntime().newArgumentError(args.length,1);
             }
             // MRI behavior: value given in seconds; converted to Float; less
             // than or equal to zero returns immediately; returns nil
             timeoutMillis = (long)(1000.0D * args[0].convertToFloat().getValue());
             if (timeoutMillis <= 0) {
 	        // TODO: not sure that we should skip caling join() altogether.
 		// Thread.join() has some implications for Java Memory Model, etc.
 	        if (threadImpl.isAlive()) {
 		   return getRuntime().getNil();
 		} else {   
                    return this;
 		}
             }
         }
         if (isCurrent()) {
             throw getRuntime().newThreadError("thread tried to join itself");
         }
         try {
             if (threadService.getCritical()) {
                 // If the target thread is sleeping or stopped, wake it
                 synchronized (stopLock) {
                     stopLock.notify();
                 }
                 
                 // interrupt the target thread in case it's blocking or waiting
                 // WARNING: We no longer interrupt the target thread, since this usually means
                 // interrupting IO and with NIO that means the channel is no longer usable.
                 // We either need a new way to handle waking a target thread that's waiting
                 // on IO, or we need to accept that we can't wake such threads and must wait
                 // for them to complete their operation.
                 //threadImpl.interrupt();
             }
             threadImpl.join(timeoutMillis);
         } catch (InterruptedException ie) {
             ie.printStackTrace();
             assert false : ie;
         } catch (TimeoutException ie) {
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
 
     @JRubyMethod(name = "value")
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
     public IRubyObject inspect() {
         // FIXME: There's some code duplication here with RubyObject#inspect
         StringBuffer part = new StringBuffer();
         String cname = getMetaClass().getRealClass().getName();
         part.append("#<").append(cname).append(":0x");
         part.append(Integer.toHexString(System.identityHashCode(this)));
         
         if (threadImpl.isAlive()) {
             if (isStopped) {
                 part.append(getRuntime().newString(" sleep"));
             } else if (killed) {
                 part.append(getRuntime().newString(" aborting"));
             } else {
                 part.append(getRuntime().newString(" run"));
             }
         } else {
             part.append(" dead");
         }
         
         part.append(">");
         return getRuntime().newString(part.toString());
     }
 
     @JRubyMethod(name = "key?", required = 1)
     public RubyBoolean key_p(IRubyObject key) {
         key = getSymbolKey(key);
         
         return getRuntime().newBoolean(threadLocalVariables.containsKey(key));
     }
 
     @JRubyMethod(name = "keys")
     public RubyArray keys() {
         IRubyObject[] keys = new IRubyObject[threadLocalVariables.size()];
         
         return RubyArray.newArrayNoCopy(getRuntime(), (IRubyObject[])threadLocalVariables.keySet().toArray(keys));
     }
     
     @JRubyMethod(name = "critical=", required = 1, meta = true)
     public static IRubyObject critical_set(IRubyObject receiver, IRubyObject value) {
     	receiver.getRuntime().getThreadService().setCritical(value.isTrue());
     	
     	return value;
     }
 
     @JRubyMethod(name = "critical", meta = true)
     public static IRubyObject critical(IRubyObject receiver) {
     	return receiver.getRuntime().newBoolean(receiver.getRuntime().getThreadService().getCritical());
     }
     
     @JRubyMethod(name = "stop", meta = true)
     public static IRubyObject stop(IRubyObject receiver) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
         Object stopLock = rubyThread.stopLock;
         
         synchronized (stopLock) {
             rubyThread.pollThreadEvents();
             try {
                 rubyThread.isStopped = true;
                 // attempt to decriticalize all if we're the critical thread
                 receiver.getRuntime().getThreadService().setCritical(false);
                 
                 stopLock.wait();
             } catch (InterruptedException ie) {
                 rubyThread.pollThreadEvents();
             }
             rubyThread.isStopped = false;
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
         
         rubyThread.killed = true;
         // attempt to decriticalize all if we're the critical thread
         receiver.getRuntime().getThreadService().setCritical(false);
         
         throw new ThreadKill();
     }
 
     @JRubyMethod(name = "stop?")
     public RubyBoolean stop_p() {
     	// not valid for "dead" state
     	return getRuntime().newBoolean(isStopped);
     }
     
     @JRubyMethod(name = "wakeup")
     public RubyThread wakeup() {
     	synchronized (stopLock) {
     		stopLock.notifyAll();
     	}
     	
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
 
     @JRubyMethod(name = "raise", optional = 2, frame = true)
     public IRubyObject raise(IRubyObject[] args, Block block) {
         ensureNotCurrent();
         Ruby runtime = getRuntime();
         
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before raising");
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         try {
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " raising");
             receivedException = prepareRaiseException(runtime, args, block);
             
             // If the target thread is sleeping or stopped, wake it
             synchronized (stopLock) {
                 stopLock.notify();
             }
 
             // interrupt the target thread in case it's blocking or waiting
             // WARNING: We no longer interrupt the target thread, since this usually means
             // interrupting IO and with NIO that means the channel is no longer usable.
             // We either need a new way to handle waking a target thread that's waiting
             // on IO, or we need to accept that we can't wake such threads and must wait
             // for them to complete their operation.
             //threadImpl.interrupt();
             
             // new interrupt, to hopefully wake it out of any blocking IO
             this.interrupt();
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
         }
 
         return this;
     }
 
     private IRubyObject prepareRaiseException(Ruby runtime, IRubyObject[] args, Block block) {
         if(args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if(lastException.isNil()) {
                 return new RaiseException(runtime, runtime.fastGetClass("RuntimeError"), "", false).getException();
             } 
             return lastException;
         }
 
         IRubyObject exception;
         ThreadContext context = getRuntime().getCurrentContext();
         
         if(args.length == 1) {
             if(args[0] instanceof RubyString) {
                 return runtime.fastGetClass("RuntimeError").newInstance(context, args, block);
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
     public IRubyObject run() {
         // if stopped, unstop
         synchronized (stopLock) {
             if (isStopped) {
                 isStopped = false;
                 stopLock.notifyAll();
             }
         }
     	
     	return this;
     }
     
     public void sleep(long millis) throws InterruptedException {
         ensureCurrent(getRuntime().getCurrentContext());
         synchronized (stopLock) {
             pollThreadEvents();
             try {
                 isStopped = true;
                 stopLock.wait(millis);
             } finally {
                 isStopped = false;
                 pollThreadEvents();
             }
         }
     }
 
     @JRubyMethod(name = "status")
     public IRubyObject status() {
         if (threadImpl.isAlive()) {
             if (isStopped || currentSelector != null && currentSelector.isOpen()) {
             	return getRuntime().newString("sleep");
             } else if (killed) {
                 return getRuntime().newString("aborting");
             }
         	
             return getRuntime().newString("run");
         } else if (exitingException != null) {
             return getRuntime().getNil();
         } else {
             return getRuntime().newBoolean(false);
         }
     }
 
     @JRubyMethod(name = {"kill", "exit", "terminate"})
     public IRubyObject kill() {
     	// need to reexamine this
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         
         try {
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " trying to kill");
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
 
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " succeeded with kill");
             killed = true;
             
             // If the target thread is sleeping or stopped, wake it
             synchronized (stopLock) {
                 stopLock.notify();
             }
 
             // interrupt the target thread in case it's blocking or waiting
             // WARNING: We no longer interrupt the target thread, since this usually means
             // interrupting IO and with NIO that means the channel is no longer usable.
             // We either need a new way to handle waking a target thread that's waiting
             // on IO, or we need to accept that we can't wake such threads and must wait
             // for them to complete their operation.
             //threadImpl.interrupt();
             
             // new interrupt, to hopefully wake it out of any blocking IO
             this.interrupt();
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
         }
         
         try {
             threadImpl.join();
         } catch (InterruptedException ie) {
             // we were interrupted, check thread events again
             currentThread.pollThreadEvents();
         } catch (ExecutionException ie) {
             // we were interrupted, check thread events again
             currentThread.pollThreadEvents();
         }
         
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
 
     private boolean isCurrent() {
         return threadImpl.isCurrent();
     }
 
     public void exceptionRaised(RaiseException exception) {
         assert isCurrent();
 
         RubyException rubyException = exception.getException();
         Ruby runtime = rubyException.getRuntime();
         if (runtime.fastGetClass("SystemExit").isInstance(rubyException)) {
             threadService.getMainThread().raise(new IRubyObject[] {rubyException}, Block.NULL_BLOCK);
         } else if (abortOnException(runtime)) {
             // FIXME: printError explodes on some nullpointer
             //getRuntime().getRuntime().printError(exception.getException());
             RubyException systemExit = RubySystemExit.newInstance(runtime, 1);
             systemExit.message = rubyException.message;
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
     
     private Selector currentSelector;
     
     public boolean selectForAccept(RubyIO io) {
         Channel channel = io.getChannel();
         
         if (channel instanceof SelectableChannel) {
             SelectableChannel selectable = (SelectableChannel)channel;
             
             try {
                 currentSelector = selectable.provider().openSelector();
             
                 SelectionKey key = selectable.register(currentSelector, SelectionKey.OP_ACCEPT);
 
                 int result = currentSelector.select();
 
                 if (result == 1) {
                     Set<SelectionKey> keySet = currentSelector.selectedKeys();
 
                     if (keySet.iterator().next() == key) {
                         return true;
                     }
                 }
 
                 return false;
             } catch (IOException ioe) {
                 throw io.getRuntime().newRuntimeError("Error with selector: " + ioe);
             } finally {
                 if (currentSelector != null) {
                     try {
                         currentSelector.close();
                     } catch (IOException ioe) {
                         throw io.getRuntime().newRuntimeError("Could not close selector");
                     }
                 }
                 currentSelector = null;
             }
         } else {
             // can't select, just have to do a blocking call
             return true;
         }
     }
     
     public void interrupt() {
         if (currentSelector != null) {
             currentSelector.wakeup();
         }
     }
     
     public void beforeBlockingCall() {
         isStopped = true;
     }
     
     public void afterBlockingCall() {
         isStopped = false;
     }
 }
diff --git a/src/org/jruby/RubyZlib.java b/src/org/jruby/RubyZlib.java
index e282e7e5e1..27cb36eec2 100644
--- a/src/org/jruby/RubyZlib.java
+++ b/src/org/jruby/RubyZlib.java
@@ -1,1072 +1,1072 @@
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
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Arity;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.CRC32Ext;
 import org.jruby.util.Adler32Ext;
 import org.jruby.util.ZlibInflate;
 import org.jruby.util.ZlibDeflate;
 
 import org.jruby.util.ByteList;
 
 public class RubyZlib {
     /** Create the Zlib module and add it to the Ruby runtime.
      * 
      */
     public static RubyModule createZlibModule(Ruby runtime) {
         RubyModule result = runtime.defineModule("Zlib");
 
         RubyClass gzfile = result.defineClassUnder("GzipFile", runtime.getObject(), RubyGzipFile.GZIPFILE_ALLOCATOR);
         gzfile.defineAnnotatedMethods(RubyGzipFile.class);
         
         RubyClass gzreader = result.defineClassUnder("GzipReader", gzfile, RubyGzipReader.GZIPREADER_ALLOCATOR);
         gzreader.includeModule(runtime.getEnumerable());
         gzreader.defineAnnotatedMethods(RubyGzipReader.class);
         
         RubyClass standardError = runtime.fastGetClass("StandardError");
         RubyClass zlibError = result.defineClassUnder("Error", standardError, standardError.getAllocator());
         gzreader.defineClassUnder("Error", zlibError, zlibError.getAllocator());
 
         RubyClass gzwriter = result.defineClassUnder("GzipWriter", gzfile, RubyGzipWriter.GZIPWRITER_ALLOCATOR);
         gzwriter.defineAnnotatedMethods(RubyGzipWriter.class);
 
         result.defineConstant("ZLIB_VERSION",runtime.newString("1.2.1"));
         result.defineConstant("VERSION",runtime.newString("0.6.0"));
 
         result.defineConstant("BINARY",runtime.newFixnum(0));
         result.defineConstant("ASCII",runtime.newFixnum(1));
         result.defineConstant("UNKNOWN",runtime.newFixnum(2));
 
         result.defineConstant("DEF_MEM_LEVEL",runtime.newFixnum(8));
         result.defineConstant("MAX_MEM_LEVEL",runtime.newFixnum(9));
 
         result.defineConstant("OS_UNIX",runtime.newFixnum(3));
         result.defineConstant("OS_UNKNOWN",runtime.newFixnum(255));
         result.defineConstant("OS_CODE",runtime.newFixnum(11));
         result.defineConstant("OS_ZSYSTEM",runtime.newFixnum(8));
         result.defineConstant("OS_VMCMS",runtime.newFixnum(4));
         result.defineConstant("OS_VMS",runtime.newFixnum(2));
         result.defineConstant("OS_RISCOS",runtime.newFixnum(13));
         result.defineConstant("OS_MACOS",runtime.newFixnum(7));
         result.defineConstant("OS_OS2",runtime.newFixnum(6));
         result.defineConstant("OS_AMIGA",runtime.newFixnum(1));
         result.defineConstant("OS_QDOS",runtime.newFixnum(12));
         result.defineConstant("OS_WIN32",runtime.newFixnum(11));
         result.defineConstant("OS_ATARI",runtime.newFixnum(5));
         result.defineConstant("OS_MSDOS",runtime.newFixnum(0));
         result.defineConstant("OS_CPM",runtime.newFixnum(9));
         result.defineConstant("OS_TOPS20",runtime.newFixnum(10));
 
         result.defineConstant("DEFAULT_STRATEGY",runtime.newFixnum(0));
         result.defineConstant("FILTERED",runtime.newFixnum(1));
         result.defineConstant("HUFFMAN_ONLY",runtime.newFixnum(2));
 
         result.defineConstant("NO_FLUSH",runtime.newFixnum(0));
         result.defineConstant("SYNC_FLUSH",runtime.newFixnum(2));
         result.defineConstant("FULL_FLUSH",runtime.newFixnum(3));
         result.defineConstant("FINISH",runtime.newFixnum(4));
 
         result.defineConstant("NO_COMPRESSION",runtime.newFixnum(0));
         result.defineConstant("BEST_SPEED",runtime.newFixnum(1));
         result.defineConstant("DEFAULT_COMPRESSION",runtime.newFixnum(-1));
         result.defineConstant("BEST_COMPRESSION",runtime.newFixnum(9));
 
         result.defineConstant("MAX_WBITS",runtime.newFixnum(15));
 
         result.defineAnnotatedMethods(RubyZlib.class);
 
         result.defineClassUnder("StreamEnd",zlibError, zlibError.getAllocator());
         result.defineClassUnder("StreamError",zlibError, zlibError.getAllocator());
         result.defineClassUnder("BufError",zlibError, zlibError.getAllocator());
         result.defineClassUnder("NeedDict",zlibError, zlibError.getAllocator());
         result.defineClassUnder("MemError",zlibError, zlibError.getAllocator());
         result.defineClassUnder("VersionError",zlibError, zlibError.getAllocator());
         result.defineClassUnder("DataError",zlibError, zlibError.getAllocator());
 
         RubyClass gzError = gzfile.defineClassUnder("Error",zlibError, zlibError.getAllocator());
         gzfile.defineClassUnder("CRCError",gzError, gzError.getAllocator());
         gzfile.defineClassUnder("NoFooter",gzError, gzError.getAllocator());
         gzfile.defineClassUnder("LengthError",gzError, gzError.getAllocator());
 
         // ZStream actually *isn't* allocatable
         RubyClass zstream = result.defineClassUnder("ZStream", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         zstream.defineAnnotatedMethods(ZStream.class);
         zstream.undefineMethod("new");
 
         RubyClass infl = result.defineClassUnder("Inflate", zstream, Inflate.INFLATE_ALLOCATOR);
         infl.defineAnnotatedMethods(Inflate.class);
 
         RubyClass defl = result.defineClassUnder("Deflate", zstream, Deflate.DEFLATE_ALLOCATOR);
         defl.defineAnnotatedMethods(Deflate.class);
 
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require",runtime.newString("stringio"));
 
         return result;
     }
 
     @JRubyMethod(name = "zlib_version", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject zlib_version(IRubyObject recv) {
         return ((RubyModule)recv).fastGetConstant("ZLIB_VERSION");
     }
 
     @JRubyMethod(name = "version", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject version(IRubyObject recv) {
         return ((RubyModule)recv).fastGetConstant("VERSION");
     }
 
     @JRubyMethod(name = "crc32", optional = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject crc32(IRubyObject recv, IRubyObject[] args) throws Exception {
         args = Arity.scanArgs(recv.getRuntime(),args,0,2);
         long crc = 0;
         ByteList bytes = null;
         
         if (!args[0].isNil()) bytes = args[0].convertToString().getByteList();
         if (!args[1].isNil()) crc = RubyNumeric.num2long(args[1]);
 
         CRC32Ext ext = new CRC32Ext((int)crc);
         if (bytes != null) {
             ext.update(bytes.unsafeBytes(), bytes.begin(), bytes.length());
         }
         
         return recv.getRuntime().newFixnum(ext.getValue());
     }
 
     @JRubyMethod(name = "adler32", optional = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject adler32(IRubyObject recv, IRubyObject[] args) throws Exception {
         args = Arity.scanArgs(recv.getRuntime(),args,0,2);
         int adler = 1;
         ByteList bytes = null;
         if (!args[0].isNil()) bytes = args[0].convertToString().getByteList();
         if (!args[1].isNil()) adler = RubyNumeric.fix2int(args[1]);
 
         Adler32Ext ext = new Adler32Ext(adler);
         if (bytes != null) {
             ext.update(bytes.unsafeBytes(), bytes.begin(), bytes.length()); // it's safe since adler.update doesn't modify the array
         }
         return recv.getRuntime().newFixnum(ext.getValue());
     }
 
     private final static long[] crctab = new long[]{
         0L, 1996959894L, 3993919788L, 2567524794L, 124634137L, 1886057615L, 3915621685L, 2657392035L, 249268274L, 2044508324L, 3772115230L, 2547177864L, 162941995L, 
         2125561021L, 3887607047L, 2428444049L, 498536548L, 1789927666L, 4089016648L, 2227061214L, 450548861L, 1843258603L, 4107580753L, 2211677639L, 325883990L, 
         1684777152L, 4251122042L, 2321926636L, 335633487L, 1661365465L, 4195302755L, 2366115317L, 997073096L, 1281953886L, 3579855332L, 2724688242L, 1006888145L, 
         1258607687L, 3524101629L, 2768942443L, 901097722L, 1119000684L, 3686517206L, 2898065728L, 853044451L, 1172266101L, 3705015759L, 2882616665L, 651767980L, 
         1373503546L, 3369554304L, 3218104598L, 565507253L, 1454621731L, 3485111705L, 3099436303L, 671266974L, 1594198024L, 3322730930L, 2970347812L, 795835527L, 
         1483230225L, 3244367275L, 3060149565L, 1994146192L, 31158534L, 2563907772L, 4023717930L, 1907459465L, 112637215L, 2680153253L, 3904427059L, 2013776290L, 
         251722036L, 2517215374L, 3775830040L, 2137656763L, 141376813L, 2439277719L, 3865271297L, 1802195444L, 476864866L, 2238001368L, 4066508878L, 1812370925L, 
         453092731L, 2181625025L, 4111451223L, 1706088902L, 314042704L, 2344532202L, 4240017532L, 1658658271L, 366619977L, 2362670323L, 4224994405L, 1303535960L, 
         984961486L, 2747007092L, 3569037538L, 1256170817L, 1037604311L, 2765210733L, 3554079995L, 1131014506L, 879679996L, 2909243462L, 3663771856L, 1141124467L, 
         855842277L, 2852801631L, 3708648649L, 1342533948L, 654459306L, 3188396048L, 3373015174L, 1466479909L, 544179635L, 3110523913L, 3462522015L, 1591671054L, 
         702138776L, 2966460450L, 3352799412L, 1504918807L, 783551873L, 3082640443L, 3233442989L, 3988292384L, 2596254646L, 62317068L, 1957810842L, 3939845945L, 
         2647816111L, 81470997L, 1943803523L, 3814918930L, 2489596804L, 225274430L, 2053790376L, 3826175755L, 2466906013L, 167816743L, 2097651377L, 4027552580L, 
         2265490386L, 503444072L, 1762050814L, 4150417245L, 2154129355L, 426522225L, 1852507879L, 4275313526L, 2312317920L, 282753626L, 1742555852L, 4189708143L, 
         2394877945L, 397917763L, 1622183637L, 3604390888L, 2714866558L, 953729732L, 1340076626L, 3518719985L, 2797360999L, 1068828381L, 1219638859L, 3624741850L, 
         2936675148L, 906185462L, 1090812512L, 3747672003L, 2825379669L, 829329135L, 1181335161L, 3412177804L, 3160834842L, 628085408L, 1382605366L, 3423369109L, 
         3138078467L, 570562233L, 1426400815L, 3317316542L, 2998733608L, 733239954L, 1555261956L, 3268935591L, 3050360625L, 752459403L, 1541320221L, 2607071920L, 
         3965973030L, 1969922972L, 40735498L, 2617837225L, 3943577151L, 1913087877L, 83908371L, 2512341634L, 3803740692L, 2075208622L, 213261112L, 2463272603L, 
         3855990285L, 2094854071L, 198958881L, 2262029012L, 4057260610L, 1759359992L, 534414190L, 2176718541L, 4139329115L, 1873836001L, 414664567L, 2282248934L, 
         4279200368L, 1711684554L, 285281116L, 2405801727L, 4167216745L, 1634467795L, 376229701L, 2685067896L, 3608007406L, 1308918612L, 956543938L, 2808555105L, 
         3495958263L, 1231636301L, 1047427035L, 2932959818L, 3654703836L, 1088359270L, 936918000L, 2847714899L, 3736837829L, 1202900863L, 817233897L, 3183342108L, 
         3401237130L, 1404277552L, 615818150L, 3134207493L, 3453421203L, 1423857449L, 601450431L, 3009837614L, 3294710456L, 1567103746L, 711928724L, 3020668471L, 
         3272380065L, 1510334235L, 755167117};
 
     @JRubyMethod(name = "crc_table", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject crc_table(IRubyObject recv) {
         List<IRubyObject> ll = new ArrayList<IRubyObject>(crctab.length);
         for(int i=0;i<crctab.length;i++) {
             ll.add(recv.getRuntime().newFixnum(crctab[i]));
         }
         return recv.getRuntime().newArray(ll);
     }
 
 
     public static abstract class ZStream extends RubyObject {
         protected boolean closed = false;
         protected boolean ended = false;
         protected boolean finished = false;
 
         protected abstract int internalTotalOut();
         protected abstract boolean internalStreamEndP();
         protected abstract void internalEnd();
         protected abstract void internalReset();
         protected abstract int internalAdler();
         protected abstract IRubyObject internalFinish() throws Exception;
         protected abstract int internalTotalIn();
         protected abstract void internalClose();
 
         public ZStream(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
         public IRubyObject initialize(Block unusedBlock) {
             return this;
         }
 
         @JRubyMethod(name = "flust_next_out")
         public IRubyObject flush_next_out() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "total_out")
         public IRubyObject total_out() {
             return getRuntime().newFixnum(internalTotalOut());
         }
 
         @JRubyMethod(name = "stream_end?")
         public IRubyObject stream_end_p() {
             return internalStreamEndP() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "data_type")
         public IRubyObject data_type() {
             return getRuntime().fastGetModule("Zlib").fastGetConstant("UNKNOWN");
         }
 
         @JRubyMethod(name = "closed?")
         public IRubyObject closed_p() {
             return closed ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "ended?")
         public IRubyObject ended_p() {
             return ended ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "end")
         public IRubyObject end() {
             if(!ended) {
                 internalEnd();
                 ended = true;
             }
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "reset")
         public IRubyObject reset() {
             internalReset();
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "avail_out")
         public IRubyObject avail_out() {
             return RubyFixnum.zero(getRuntime());
         }
 
         @JRubyMethod(name = "avail_out=", required = 1)
         public IRubyObject set_avail_out(IRubyObject p1) {
             return p1;
         }
 
         @JRubyMethod(name = "adler")
         public IRubyObject adler() {
             return getRuntime().newFixnum(internalAdler());
         }
 
         @JRubyMethod(name = "finish")
         public IRubyObject finish() throws Exception {
             if(!finished) {
                 finished = true;
                 return internalFinish();
             }
             return getRuntime().newString("");
         }
 
         @JRubyMethod(name = "avail_in")
         public IRubyObject avail_in() {
             return RubyFixnum.zero(getRuntime());
         }
 
         @JRubyMethod(name = "flush_next_in")
         public IRubyObject flush_next_in() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "total_in")
         public IRubyObject total_in() {
             return getRuntime().newFixnum(internalTotalIn());
         }
 
         @JRubyMethod(name = "finished?")
         public IRubyObject finished_p() {
             return finished ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "close")
         public IRubyObject close() {
             if(!closed) {
                 internalClose();
                 closed = true;
             }
             return getRuntime().getNil();
         }
     }
 
     public static class Inflate extends ZStream {
-        protected static ObjectAllocator INFLATE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator INFLATE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new Inflate(runtime, klass);
             }
         };
 
         @JRubyMethod(name = "inflate", required = 1, meta = true)
         public static IRubyObject s_inflate(IRubyObject recv, IRubyObject string) throws Exception {
             return ZlibInflate.s_inflate(recv,string.convertToString().getByteList());
         }
 
         public Inflate(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         private ZlibInflate infl;
 
         @JRubyMethod(name = "initialize", rest = true, visibility = Visibility.PRIVATE)
         public IRubyObject _initialize(IRubyObject[] args) throws Exception {
             infl = new ZlibInflate(this);
             return this;
         }
 
         @JRubyMethod(name = "<<", required = 1)
         public IRubyObject append(IRubyObject arg) {
             infl.append(arg);
             return this;
         }
 
         @JRubyMethod(name = "sync_point?")
         public IRubyObject sync_point_p() {
             return infl.sync_point();
         }
 
         @JRubyMethod(name = "set_dictionary", required = 1)
         public IRubyObject set_dictionary(IRubyObject arg) throws Exception {
             return infl.set_dictionary(arg);
         }
 
         @JRubyMethod(name = "inflate", required = 1)
         public IRubyObject inflate(IRubyObject string) throws Exception {
             return infl.inflate(string.convertToString().getByteList());
         }
 
         @JRubyMethod(name = "sync", required = 1)
         public IRubyObject sync(IRubyObject string) {
             return infl.sync(string);
         }
 
         protected int internalTotalOut() {
             return infl.getInflater().getTotalOut();
         }
 
         protected boolean internalStreamEndP() {
             return infl.getInflater().finished();
         }
 
         protected void internalEnd() {
             infl.getInflater().end();
         }
 
         protected void internalReset() {
             infl.getInflater().reset();
         }
 
         protected int internalAdler() {
             return infl.getInflater().getAdler();
         }
 
         protected IRubyObject internalFinish() throws Exception {
             infl.finish();
             return getRuntime().getNil();
         }
 
         public IRubyObject finished_p() {
             return infl.getInflater().finished() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         protected int internalTotalIn() {
             return infl.getInflater().getTotalIn();
         }
 
         protected void internalClose() {
             infl.close();
         }
     }
 
     public static class Deflate extends ZStream {
-        protected static ObjectAllocator DEFLATE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator DEFLATE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new Deflate(runtime, klass);
             }
         };
 
         @JRubyMethod(name = "deflate", required = 1, optional = 1, meta = true)
         public static IRubyObject s_deflate(IRubyObject recv, IRubyObject[] args) throws Exception {
             args = Arity.scanArgs(recv.getRuntime(),args,1,1);
             int level = -1;
             if(!args[1].isNil()) {
                 level = RubyNumeric.fix2int(args[1]);
             }
             return ZlibDeflate.s_deflate(recv,args[0].convertToString().getByteList(),level);
         }
 
         public Deflate(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         private ZlibDeflate defl;
 
         @JRubyMethod(name = "initialize", optional = 4, visibility = Visibility.PRIVATE)
         public IRubyObject _initialize(IRubyObject[] args) throws Exception {
             args = Arity.scanArgs(getRuntime(),args,0,4);
             int level = -1;
             int window_bits = 15;
             int memlevel = 8;
             int strategy = 0;
             if(!args[0].isNil()) {
                 level = RubyNumeric.fix2int(args[0]);
             }
             if(!args[1].isNil()) {
                 window_bits = RubyNumeric.fix2int(args[1]);
             }
             if(!args[2].isNil()) {
                 memlevel = RubyNumeric.fix2int(args[2]);
             }
             if(!args[3].isNil()) {
                 strategy = RubyNumeric.fix2int(args[3]);
             }
             defl = new ZlibDeflate(this,level,window_bits,memlevel,strategy);
             return this;
         }
 
         @JRubyMethod(name = "<<", required = 1)
         public IRubyObject append(IRubyObject arg) throws Exception {
             defl.append(arg);
             return this;
         }
 
         @JRubyMethod(name = "params", required = 2)
         public IRubyObject params(IRubyObject level, IRubyObject strategy) {
             defl.params(RubyNumeric.fix2int(level),RubyNumeric.fix2int(strategy));
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "set_dictionary", required = 1)
         public IRubyObject set_dictionary(IRubyObject arg) throws Exception {
             return defl.set_dictionary(arg);
         }
         
         @JRubyMethod(name = "flush", optional = 1)
         public IRubyObject flush(IRubyObject[] args) throws Exception {
             int flush = 2; // SYNC_FLUSH
             if(args.length == 1) {
                 if(!args[0].isNil()) {
                     flush = RubyNumeric.fix2int(args[0]);
                 }
             }
             return defl.flush(flush);
         }
 
         @JRubyMethod(name = "deflate", required = 1, optional = 1)
         public IRubyObject deflate(IRubyObject[] args) throws Exception {
             args = Arity.scanArgs(getRuntime(),args,1,1);
             int flush = 0; // NO_FLUSH
             if(!args[1].isNil()) {
                 flush = RubyNumeric.fix2int(args[1]);
             }
             return defl.deflate(args[0].convertToString().getByteList(),flush);
         }
 
         protected int internalTotalOut() {
             return defl.getDeflater().getTotalOut();
         }
 
         protected boolean internalStreamEndP() {
             return defl.getDeflater().finished();
         }
 
         protected void internalEnd() {
             defl.getDeflater().end();
         }
 
         protected void internalReset() {
             defl.getDeflater().reset();
         }
 
         protected int internalAdler() {
             return defl.getDeflater().getAdler();
         }
 
         protected IRubyObject internalFinish() throws Exception {
             return defl.finish();
         }
 
         protected int internalTotalIn() {
             return defl.getDeflater().getTotalIn();
         }
 
         protected void internalClose() {
             defl.close();
         }
     }
 
     public static class RubyGzipFile extends RubyObject {
         @JRubyMethod(name = "wrap", required = 2, frame = true, meta = true)
         public static IRubyObject wrap(ThreadContext context, IRubyObject recv, IRubyObject io, IRubyObject proc, Block unusedBlock) throws IOException {
             if (!(io instanceof RubyGzipFile)) throw recv.getRuntime().newTypeError(io, (RubyClass)recv);
             if (!proc.isNil()) {
                 try {
                     ((RubyProc)proc).call(context, new IRubyObject[]{io});
                 } finally {
                     RubyGzipFile zipIO = (RubyGzipFile)io;
                     if (!zipIO.isClosed()) {
                         zipIO.close();
                     }
                 }
                 return recv.getRuntime().getNil();
             }
 
             return io;
         }
         
-        protected static ObjectAllocator GZIPFILE_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator GZIPFILE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new RubyGzipFile(runtime, klass);
             }
         };
 
         @JRubyMethod(name = "new", frame = true, meta = true)
         public static RubyGzipFile newInstance(IRubyObject recv, Block block) {
             RubyClass klass = (RubyClass)recv;
             
             RubyGzipFile result = (RubyGzipFile) klass.allocate();
             
             result.callInit(new IRubyObject[0], block);
             
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
 
         public RubyGzipFile(Ruby runtime, RubyClass type) {
             super(runtime, type);
             mtime = runtime.getNil();
         }
         
         @JRubyMethod(name = "os_code")
         public IRubyObject os_code() {
             return getRuntime().newFixnum(os_code);
         }
         
         @JRubyMethod(name = "closed?")
         public IRubyObject closed_p() {
             return closed ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         
         protected boolean isClosed() {
             return closed;
         }
         
         @JRubyMethod(name = "orig_name")
         public IRubyObject orig_name() {
             return orig_name == null ? getRuntime().getNil() : getRuntime().newString(orig_name);
         }
         
         @JRubyMethod(name = "to_io")
         public IRubyObject to_io() {
             return realIo;
         }
         
         @JRubyMethod(name = "comment")
         public IRubyObject comment() {
             return comment == null ? getRuntime().getNil() : getRuntime().newString(comment);
         }
         
         @JRubyMethod(name = "crc")
         public IRubyObject crc() {
             return RubyFixnum.zero(getRuntime());
         }
         
         @JRubyMethod(name = "mtime")
         public IRubyObject mtime() {
             return mtime;
         }
         
         @JRubyMethod(name = "sync")
         public IRubyObject sync() {
             return getRuntime().getNil();
         }
         
         @JRubyMethod(name = "finish")
         public IRubyObject finish() throws IOException {
             if (!finished) {
                 //io.finish();
             }
             finished = true;
             return realIo;
         }
 
         @JRubyMethod(name = "close")
         public IRubyObject close() throws IOException {
             return null;
         }
         
         @JRubyMethod(name = "level")
         public IRubyObject level() {
             return getRuntime().newFixnum(level);
         }
         
         @JRubyMethod(name = "sync=", required = 1)
         public IRubyObject set_sync(IRubyObject ignored) {
             return getRuntime().getNil();
         }
     }
 
     public static class RubyGzipReader extends RubyGzipFile {
-        protected static ObjectAllocator GZIPREADER_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator GZIPREADER_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new RubyGzipReader(runtime, klass);
             }
         };
         
         @JRubyMethod(name = "new", rest = true, frame = true, meta = true)
         public static RubyGzipReader newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
             RubyClass klass = (RubyClass)recv;
             RubyGzipReader result = (RubyGzipReader)klass.allocate();
             result.callInit(args, block);
             return result;
         }
 
         @JRubyMethod(name = "open", required = 1, frame = true, meta = true)
         public static IRubyObject open(final ThreadContext context, IRubyObject recv, IRubyObject filename, Block block) throws IOException {
             Ruby runtime = recv.getRuntime();
             IRubyObject proc = block.isGiven() ? runtime.newProc(Block.Type.PROC, block) : runtime.getNil();
             RubyGzipReader io = newInstance(recv,
                     new IRubyObject[]{ runtime.getFile().callMethod(context, "open",
                             new IRubyObject[]{filename, runtime.newString("rb")})}, block);
             
             return RubyGzipFile.wrap(context, recv, io, proc, null);
         }
 
         public RubyGzipReader(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
         
         private int line;
         private InputStream io;
         
         @JRubyMethod(name = "initialize", required = 1, frame = true, visibility = Visibility.PRIVATE)
         public IRubyObject initialize(IRubyObject io, Block unusedBlock) {
             realIo = io;
             try {
                 this.io = new GZIPInputStream(new IOInputStream(io));
             } catch (IOException e) {
                 Ruby runtime = io.getRuntime();
                 RubyClass errorClass = runtime.fastGetModule("Zlib").fastGetClass("GzipReader").fastGetClass("Error");
                 throw new RaiseException(RubyException.newException(runtime, errorClass, e.getMessage()));
             }
 
             line = 1;
             
             return this;
         }
         
         @JRubyMethod(name = "rewind")
         public IRubyObject rewind() {
             return getRuntime().getNil();
         }
         
         @JRubyMethod(name = "lineno")
         public IRubyObject lineno() {
             return getRuntime().newFixnum(line);
         }
 
         @JRubyMethod(name = "readline")
         public IRubyObject readline(ThreadContext context) throws IOException {
             IRubyObject dst = gets(context, new IRubyObject[0]);
             if (dst.isNil()) {
                 throw getRuntime().newEOFError();
             }
             return dst;
         }
 
         public IRubyObject internalGets(IRubyObject[] args) throws IOException {
             ByteList sep = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getByteList();
             if (args.length > 0) {
                 sep = args[0].convertToString().getByteList();
             }
             return internalSepGets(sep);
         }
 
         private IRubyObject internalSepGets(ByteList sep) throws IOException {
             ByteList result = new ByteList();
             int ce = io.read();
             while (ce != -1 && sep.indexOf(ce) == -1) {
                 result.append((byte)ce);
                 ce = io.read();
             }
             line++;
             result.append(sep);
             return RubyString.newString(getRuntime(),result);
         }
 
         @JRubyMethod(name = "gets", optional = 1)
         public IRubyObject gets(ThreadContext context, IRubyObject[] args) throws IOException {
             IRubyObject result = internalGets(args);
             if (!result.isNil()) {
                 context.getCurrentFrame().setLastLine(result);
             }
             return result;
         }
 
         private final static int BUFF_SIZE = 4096;
         
         @JRubyMethod(name = "read", optional = 1)
         public IRubyObject read(IRubyObject[] args) throws IOException {
             if (args.length == 0 || args[0].isNil()) {
                 ByteList val = new ByteList(10);
                 byte[] buffer = new byte[BUFF_SIZE];
                 int read = io.read(buffer);
                 while (read != -1) {
                     val.append(buffer,0,read);
                     read = io.read(buffer);
                 }
                 return RubyString.newString(getRuntime(),val);
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
             	} // hmm...
             	return RubyString.newString(getRuntime(),new ByteList(buffer,0,len-toRead,false));
             }
                 
             return getRuntime().newString("");
         }
 
         @JRubyMethod(name = "lineno=", required = 1)
         public IRubyObject set_lineno(IRubyObject lineArg) {
             line = RubyNumeric.fix2int(lineArg);
             return lineArg;
         }
 
         @JRubyMethod(name = "pos")
         public IRubyObject pos() {
             return RubyFixnum.zero(getRuntime());
         }
         
         @JRubyMethod(name = "readchar")
         public IRubyObject readchar() throws IOException {
             int value = io.read();
             if (value == -1) {
                 throw getRuntime().newEOFError();
             }
             return getRuntime().newFixnum(value);
         }
 
         @JRubyMethod(name = "getc")
         public IRubyObject getc() throws IOException {
             int value = io.read();
             return value == -1 ? getRuntime().getNil() : getRuntime().newFixnum(value);
         }
 
         private boolean isEof() throws IOException {
             return ((GZIPInputStream)io).available() != 1;
         }
 
         @JRubyMethod(name = "close")
         public IRubyObject close() throws IOException {
             if (!closed) {
                 io.close();
             }
             this.closed = true;
             return getRuntime().getNil();
         }
         
         @JRubyMethod(name = "eof")
         public IRubyObject eof() throws IOException {
             return isEof() ? getRuntime().getTrue() : getRuntime().getFalse();
         }
 
         @JRubyMethod(name = "eof?")
         public IRubyObject eof_p() throws IOException {
             return eof();
         }
 
         @JRubyMethod(name = "unused")
         public IRubyObject unused() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "tell")
         public IRubyObject tell() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "each", optional = 1, frame = true)
         public IRubyObject each(ThreadContext context, IRubyObject[] args, Block block) throws IOException {
             ByteList sep = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getByteList();
             
             if (args.length > 0 && !args[0].isNil()) {
                 sep = args[0].convertToString().getByteList();
             }
 
             while (!isEof()) {
                 block.yield(context, internalSepGets(sep));
             }
             
             return getRuntime().getNil();
         }
     
         @JRubyMethod(name = "ungetc", required = 1)
         public IRubyObject ungetc(IRubyObject arg) {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "readlines", optional = 1)
         public IRubyObject readlines(IRubyObject[] args) throws IOException {
             List<IRubyObject> array = new ArrayList<IRubyObject>();
             
             if (args.length != 0 && args[0].isNil()) {
                 array.add(read(new IRubyObject[0]));
             } else {
                 ByteList seperator = ((RubyString)getRuntime().getGlobalVariables().get("$/")).getByteList();
                 if (args.length > 0) {
                     seperator = args[0].convertToString().getByteList();
                 }
                 while (!isEof()) {
                     array.add(internalSepGets(seperator));
                 }
             }
             return getRuntime().newArray(array);
         }
 
         @JRubyMethod(name = "each_byte", frame = true)
         public IRubyObject each_byte(ThreadContext context, Block block) throws IOException {
             int value = io.read();
 
             while (value != -1) {
                 block.yield(context, getRuntime().newFixnum(value));
                 value = io.read();
             }
             
             return getRuntime().getNil();
         }
     }
 
     public static class RubyGzipWriter extends RubyGzipFile {
-        protected static ObjectAllocator GZIPWRITER_ALLOCATOR = new ObjectAllocator() {
+        protected static final ObjectAllocator GZIPWRITER_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new RubyGzipWriter(runtime, klass);
             }
         };
         
         @JRubyMethod(name = "new", rest = true, frame = true, meta = true)
         public static RubyGzipWriter newGzipWriter(IRubyObject recv, IRubyObject[] args, Block block) {
             RubyClass klass = (RubyClass)recv;
             
             RubyGzipWriter result = (RubyGzipWriter)klass.allocate();
             result.callInit(args, block);
             return result;
         }
 
         @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, meta = true)
         public static IRubyObject open(final ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) throws IOException {
             Ruby runtime = recv.getRuntime();
             IRubyObject level = runtime.getNil();
             IRubyObject strategy = runtime.getNil();
 
             if (args.length > 1) {
                 level = args[1];
                 if (args.length > 2) strategy = args[2];
             }
 
             IRubyObject proc = block.isGiven() ? runtime.newProc(Block.Type.PROC, block) : runtime.getNil();
             RubyGzipWriter io = newGzipWriter(
                     recv,
                     new IRubyObject[]{ runtime.getFile().callMethod(
                             context,
                             "open",
                             new IRubyObject[]{args[0],runtime.newString("wb")}),level,strategy},block);
             
             return RubyGzipFile.wrap(context, recv, io, proc, null);
         }
 
         public RubyGzipWriter(Ruby runtime, RubyClass type) {
             super(runtime, type);
         }
 
         private GZIPOutputStream io;
         
         @JRubyMethod(name = "initialize", required = 1, rest = true, frame = true, visibility = Visibility.PRIVATE)
         public IRubyObject initialize2(IRubyObject[] args, Block unusedBlock) throws IOException {
             realIo = (RubyObject)args[0];
             this.io = new GZIPOutputStream(new IOOutputStream(args[0]));
             
             return this;
         }
 
         @JRubyMethod(name = "close")
         public IRubyObject close() throws IOException {
             if (!closed) {
                 io.close();
             }
             this.closed = true;
             
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "append", required = 1)
         public IRubyObject append(IRubyObject p1) throws IOException {
             this.write(p1);
             return this;
         }
 
         @JRubyMethod(name = "printf", required = 1, rest = true)
         public IRubyObject printf(IRubyObject[] args) throws IOException {
             write(RubyKernel.sprintf(this, args));
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "print", rest = true)
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
 
         @JRubyMethod(name = "pos")
         public IRubyObject pos() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "orig_name=", required = 1)
         public IRubyObject set_orig_name(IRubyObject ignored) {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "comment=", required = 1)
         public IRubyObject set_comment(IRubyObject ignored) {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "putc", required = 1)
         public IRubyObject putc(IRubyObject p1) throws IOException {
             io.write(RubyNumeric.fix2int(p1));
             return p1;
         }
         
         @JRubyMethod(name = "puts", rest = true)
         public IRubyObject puts(ThreadContext context, IRubyObject[] args) throws IOException {
             RubyStringIO sio = (RubyStringIO)getRuntime().fastGetClass("StringIO").newInstance(context, new IRubyObject[0], Block.NULL_BLOCK);
             sio.puts(context, args);
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
 
         @JRubyMethod(name = "flush", optional = 1)
         public IRubyObject flush(IRubyObject[] args) throws IOException {
             if (args.length == 0 || args[0].isNil() || RubyNumeric.fix2int(args[0]) != 0) { // Zlib::NO_FLUSH
                 io.flush();
             }
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "mtime=", required = 1)
         public IRubyObject set_mtime(IRubyObject ignored) {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "tell")
         public IRubyObject tell() {
             return getRuntime().getNil();
         }
 
         @JRubyMethod(name = "write", required = 1)
         public IRubyObject write(IRubyObject p1) throws IOException {
             ByteList bytes = p1.convertToString().getByteList();
             io.write(bytes.unsafeBytes(), bytes.begin(), bytes.length());
             return getRuntime().newFixnum(bytes.length());
         }
     }
 }
diff --git a/src/org/jruby/compiler/impl/StandardASMCompiler.java b/src/org/jruby/compiler/impl/StandardASMCompiler.java
index 1f2d2e7ed9..6cecc3bdda 100644
--- a/src/org/jruby/compiler/impl/StandardASMCompiler.java
+++ b/src/org/jruby/compiler/impl/StandardASMCompiler.java
@@ -1,1744 +1,1745 @@
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
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
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
 
 package org.jruby.compiler.impl;
 
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.io.UnsupportedEncodingException;
 import java.math.BigInteger;
 import java.util.Arrays;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyRange;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.executable.AbstractScript;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ArrayCallback;
 import org.jruby.compiler.BranchCallback;
 import org.jruby.compiler.CacheCompiler;
 import org.jruby.compiler.CompilerCallback;
 import org.jruby.compiler.InvocationCompiler;
 import org.jruby.compiler.MethodCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.ScriptCompiler;
 import org.jruby.compiler.VariableCompiler;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.ReOptions;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.BlockBody;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.InstanceVariables;
 import org.jruby.util.ByteList;
 import static org.jruby.util.CodegenUtils.*;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.JavaNameMangler;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.ClassVisitor;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.Opcodes;
 import org.objectweb.asm.util.CheckClassAdapter;
 
 /**
  *
  * @author headius
  */
 public class StandardASMCompiler implements ScriptCompiler, Opcodes {
     private static final String THREADCONTEXT = p(ThreadContext.class);
     private static final String RUBY = p(Ruby.class);
     private static final String IRUBYOBJECT = p(IRubyObject.class);
 
     private static final String METHOD_SIGNATURE = sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class});
     private static final String CLOSURE_SIGNATURE = sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class});
 
     public static final int THIS = 0;
     public static final int THREADCONTEXT_INDEX = 1;
     public static final int SELF_INDEX = 2;
     public static final int ARGS_INDEX = 3;
     public static final int CLOSURE_INDEX = 4;
     public static final int DYNAMIC_SCOPE_INDEX = 5;
     public static final int RUNTIME_INDEX = 6;
     public static final int VARS_ARRAY_INDEX = 7;
     public static final int NIL_INDEX = 8;
     public static final int EXCEPTION_INDEX = 9;
     public static final int PREVIOUS_EXCEPTION_INDEX = 10;
     public static final int FIRST_TEMP_INDEX = 11;
     
     private String classname;
     private String sourcename;
 
     private ClassWriter classWriter;
     private SkinnyMethodAdapter initMethod;
     private SkinnyMethodAdapter clinitMethod;
     int methodIndex = -1;
     int innerIndex = -1;
     int fieldIndex = 0;
     int rescueNumber = 1;
     int ensureNumber = 1;
     StaticScope topLevelScope;
     
     CacheCompiler cacheCompiler;
     
     /** Creates a new instance of StandardCompilerContext */
     public StandardASMCompiler(String classname, String sourcename) {
         this.classname = classname;
         this.sourcename = sourcename;
     }
 
     public byte[] getClassByteArray() {
         return classWriter.toByteArray();
     }
 
     public Class<?> loadClass(JRubyClassLoader classLoader) throws ClassNotFoundException {
         classLoader.defineClass(c(classname), classWriter.toByteArray());
         return classLoader.loadClass(c(classname));
     }
 
     public void writeClass(File destination) throws IOException {
         writeClass(classname, destination, classWriter);
     }
 
     private void writeClass(String classname, File destination, ClassWriter writer) throws IOException {
         String fullname = classname + ".class";
         String filename = null;
         String path = null;
         
         // verify the class
         byte[] bytecode = writer.toByteArray();
         CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(System.err));
         
         if (fullname.lastIndexOf("/") == -1) {
             filename = fullname;
             path = "";
         } else {
             filename = fullname.substring(fullname.lastIndexOf("/") + 1);
             path = fullname.substring(0, fullname.lastIndexOf("/"));
         }
         // create dir if necessary
         File pathfile = new File(destination, path);
         pathfile.mkdirs();
 
         FileOutputStream out = new FileOutputStream(new File(pathfile, filename));
 
         out.write(bytecode);
+        out.close();
     }
 
     public String getClassname() {
         return classname;
     }
 
     public String getSourcename() {
         return sourcename;
     }
 
     public ClassVisitor getClassVisitor() {
         return classWriter;
     }
     
     static boolean USE_INHERITED_CACHE_FIELDS = true;
 
     public void startScript(StaticScope scope) {
         classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
 
         // Create the class with the appropriate class name and source file
         classWriter.visit(V1_4, ACC_PUBLIC + ACC_SUPER, classname, null, p(AbstractScript.class), null);
         classWriter.visitSource(sourcename, null);
         
         topLevelScope = scope;
 
         beginInit();
         beginClassInit();
         
         cacheCompiler = new InheritedCacheCompiler(this);
     }
 
     public void endScript(boolean generateRun, boolean generateLoad, boolean generateMain) {
         // add Script#run impl, used for running this script with a specified threadcontext and self
         // root method of a script is always in __file__ method
         String methodName = "__file__";
         
         if (generateRun) {
             SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, "run", METHOD_SIGNATURE, null, null));
             method.start();
 
             // invoke __file__ with threadcontext, self, args (null), and block (null)
             method.aload(THIS);
             method.aload(THREADCONTEXT_INDEX);
             method.aload(SELF_INDEX);
             method.aload(ARGS_INDEX);
             method.aload(CLOSURE_INDEX);
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
             method.areturn();
 
             method.end();
         }
         
         if (generateLoad || generateMain) {
             // the load method is used for loading as a top-level script, and prepares appropriate scoping around the code
             SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, "load", METHOD_SIGNATURE, null, null));
             method.start();
 
             // invoke __file__ with threadcontext, self, args (null), and block (null)
             Label tryBegin = new Label();
             Label tryFinally = new Label();
 
             method.label(tryBegin);
             method.aload(THREADCONTEXT_INDEX);
             buildStaticScopeNames(method, topLevelScope);
             method.invokestatic(p(RuntimeHelpers.class), "preLoad", sig(void.class, ThreadContext.class, String[].class));
 
             method.aload(THIS);
             method.aload(THREADCONTEXT_INDEX);
             method.aload(SELF_INDEX);
             method.aload(ARGS_INDEX);
             method.aload(CLOSURE_INDEX);
 
             method.invokevirtual(classname, methodName, METHOD_SIGNATURE);
             method.aload(THREADCONTEXT_INDEX);
             method.invokestatic(p(RuntimeHelpers.class), "postLoad", sig(void.class, ThreadContext.class));
             method.areturn();
 
             method.label(tryFinally);
             method.aload(THREADCONTEXT_INDEX);
             method.invokestatic(p(RuntimeHelpers.class), "postLoad", sig(void.class, ThreadContext.class));
             method.athrow();
 
             method.trycatch(tryBegin, tryFinally, tryFinally, null);
 
             method.end();
         }
         
         if (generateMain) {
             // add main impl, used for detached or command-line execution of this script with a new runtime
             // root method of a script is always in stub0, method0
             SkinnyMethodAdapter method = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_STATIC, "main", sig(Void.TYPE, params(String[].class)), null, null));
             method.start();
 
             // new instance to invoke run against
             method.newobj(classname);
             method.dup();
             method.invokespecial(classname, "<init>", sig(Void.TYPE));
 
             // instance config for the script run
             method.newobj(p(RubyInstanceConfig.class));
             method.dup();
             method.invokespecial(p(RubyInstanceConfig.class), "<init>", "()V");
 
             // set argv from main's args
             method.dup();
             method.aload(0);
             method.invokevirtual(p(RubyInstanceConfig.class), "setArgv", sig(void.class, String[].class));
 
             // invoke run with threadcontext and topself
             method.invokestatic(p(Ruby.class), "newInstance", sig(Ruby.class, RubyInstanceConfig.class));
             method.dup();
 
             method.invokevirtual(RUBY, "getCurrentContext", sig(ThreadContext.class));
             method.swap();
             method.invokevirtual(RUBY, "getTopSelf", sig(IRubyObject.class));
             method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             method.getstatic(p(Block.class), "NULL_BLOCK", ci(Block.class));
 
             method.invokevirtual(classname, "load", METHOD_SIGNATURE);
             method.voidreturn();
             method.end();
         }
         
         endInit();
         endClassInit();
     }
 
     public void buildStaticScopeNames(SkinnyMethodAdapter method, StaticScope scope) {
         // construct static scope list of names
         method.ldc(new Integer(scope.getNumberOfVariables()));
         method.anewarray(p(String.class));
         for (int i = 0; i < scope.getNumberOfVariables(); i++) {
             method.dup();
             method.ldc(new Integer(i));
             method.ldc(scope.getVariables()[i]);
             method.arraystore();
         }
     }
 
     private void beginInit() {
         ClassVisitor cv = getClassVisitor();
 
         initMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<init>", sig(Void.TYPE), null, null));
         initMethod.start();
         initMethod.aload(THIS);
         if (USE_INHERITED_CACHE_FIELDS) {
             initMethod.invokespecial(p(AbstractScript.class), "<init>", sig(Void.TYPE));
         } else {
             initMethod.invokespecial(p(Object.class), "<init>", sig(Void.TYPE));
         }
         
         cv.visitField(ACC_PRIVATE | ACC_FINAL, "$class", ci(Class.class), null, null);
         
         // FIXME: this really ought to be in clinit, but it doesn't matter much
         initMethod.aload(THIS);
         initMethod.ldc(c(classname));
         initMethod.invokestatic(p(Class.class), "forName", sig(Class.class, params(String.class)));
         initMethod.putfield(classname, "$class", ci(Class.class));
     }
 
     private void endInit() {
         initMethod.voidreturn();
         initMethod.end();
     }
 
     private void beginClassInit() {
         ClassVisitor cv = getClassVisitor();
 
         clinitMethod = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "<clinit>", sig(Void.TYPE), null, null));
         clinitMethod.start();
     }
 
     private void endClassInit() {
         clinitMethod.voidreturn();
         clinitMethod.end();
     }
     
     public SkinnyMethodAdapter getInitMethod() {
         return initMethod;
     }
     
     public SkinnyMethodAdapter getClassInitMethod() {
         return clinitMethod;
     }
     
     public CacheCompiler getCacheCompiler() {
         return cacheCompiler;
     }
     
     public MethodCompiler startMethod(String friendlyName, CompilerCallback args, StaticScope scope, ASTInspector inspector) {
         ASMMethodCompiler methodCompiler = new ASMMethodCompiler(friendlyName, inspector);
         
         methodCompiler.beginMethod(args, scope);
         
         return methodCompiler;
     }
 
     public abstract class AbstractMethodCompiler implements MethodCompiler {
         protected SkinnyMethodAdapter method;
         protected VariableCompiler variableCompiler;
         protected InvocationCompiler invocationCompiler;
         
         protected Label[] currentLoopLabels;
         protected Label scopeStart;
         protected Label scopeEnd;
         protected Label redoJump;
         protected boolean withinProtection = false;
         private int lastLine = -1;
 
         public abstract void beginMethod(CompilerCallback args, StaticScope scope);
 
         public abstract void endMethod();
         
         public MethodCompiler chainToMethod(String methodName, ASTInspector inspector) {
             // chain to the next segment of this giant method
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, methodName, sig(IRubyObject.class, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
             endMethod();
 
             ASMMethodCompiler methodCompiler = new ASMMethodCompiler(methodName, inspector);
 
             methodCompiler.beginChainedMethod();
 
             return methodCompiler;
         }
         
         public StandardASMCompiler getScriptCompiler() {
             return StandardASMCompiler.this;
         }
 
         public void lineNumber(ISourcePosition position) {
             int thisLine = position.getStartLine();
             
             // No point in updating number if last number was same value.
             if (thisLine != lastLine) {
                 lastLine = thisLine;
             } else {
                 return;
             }
             
             Label line = new Label();
             method.label(line);
             method.visitLineNumber(thisLine + 1, line);
         }
 
         public void loadThreadContext() {
             method.aload(THREADCONTEXT_INDEX);
         }
 
         public void loadSelf() {
             method.aload(SELF_INDEX);
         }
 
         public void loadRuntime() {
             method.aload(RUNTIME_INDEX);
         }
 
         public void loadBlock() {
             method.aload(CLOSURE_INDEX);
         }
 
         public void loadNil() {
             method.aload(NIL_INDEX);
         }
         
         public void loadNull() {
             method.aconst_null();
         }
 
         public void loadSymbol(String symbol) {
             loadRuntime();
 
             method.ldc(symbol);
 
             invokeIRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
         }
 
         public void loadObject() {
             loadRuntime();
 
             invokeIRuby("getObject", sig(RubyClass.class, params()));
         }
 
         /**
          * This is for utility methods used by the compiler, to reduce the amount of code generation
          * necessary.  All of these live in CompilerHelpers.
          */
         public void invokeUtilityMethod(String methodName, String signature) {
             method.invokestatic(p(RuntimeHelpers.class), methodName, signature);
         }
 
         public void invokeThreadContext(String methodName, String signature) {
             method.invokevirtual(THREADCONTEXT, methodName, signature);
         }
 
         public void invokeIRuby(String methodName, String signature) {
             method.invokevirtual(RUBY, methodName, signature);
         }
 
         public void invokeIRubyObject(String methodName, String signature) {
             method.invokeinterface(IRUBYOBJECT, methodName, signature);
         }
 
         public void consumeCurrentValue() {
             method.pop();
         }
 
         public void duplicateCurrentValue() {
             method.dup();
         }
 
         public void swapValues() {
             method.swap();
         }
 
         public void retrieveSelf() {
             loadSelf();
         }
 
         public void retrieveSelfClass() {
             loadSelf();
             metaclass();
         }
         
         public VariableCompiler getVariableCompiler() {
             return variableCompiler;
         }
         
         public InvocationCompiler getInvocationCompiler() {
             return invocationCompiler;
         }
 
         public void assignConstantInCurrent(String name) {
             loadThreadContext();
             method.ldc(name);
             method.dup2_x1();
             method.pop2();
             invokeThreadContext("setConstantInCurrent", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
         }
 
         public void assignConstantInModule(String name) {
             method.ldc(name);
             loadThreadContext();
             invokeUtilityMethod("setConstantInModule", sig(IRubyObject.class, IRubyObject.class, IRubyObject.class, String.class, ThreadContext.class));
         }
 
         public void assignConstantInObject(String name) {
             // load Object under value
             loadRuntime();
             invokeIRuby("getObject", sig(RubyClass.class, params()));
             method.swap();
 
             assignConstantInModule(name);
         }
 
         public void retrieveConstant(String name) {
             loadThreadContext();
             method.ldc(name);
             invokeThreadContext("getConstant", sig(IRubyObject.class, params(String.class)));
         }
 
         public void retrieveConstantFromModule(String name) {
             method.visitTypeInsn(CHECKCAST, p(RubyModule.class));
             method.ldc(name);
             method.invokevirtual(p(RubyModule.class), "fastGetConstantFrom", sig(IRubyObject.class, params(String.class)));
         }
 
         public void retrieveClassVariable(String name) {
             loadThreadContext();
             loadRuntime();
             loadSelf();
             method.ldc(name);
 
             invokeUtilityMethod("fastFetchClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
         }
 
         public void assignClassVariable(String name) {
             loadThreadContext();
             method.swap();
             loadRuntime();
             method.swap();
             loadSelf();
             method.swap();
             method.ldc(name);
             method.swap();
 
             invokeUtilityMethod("fastSetClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
         }
 
         public void declareClassVariable(String name) {
             loadThreadContext();
             method.swap();
             loadRuntime();
             method.swap();
             loadSelf();
             method.swap();
             method.ldc(name);
             method.swap();
 
             invokeUtilityMethod("fastDeclareClassVariable", sig(IRubyObject.class, params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
         }
 
         public void createNewFloat(double value) {
             loadRuntime();
             method.ldc(new Double(value));
 
             invokeIRuby("newFloat", sig(RubyFloat.class, params(Double.TYPE)));
         }
 
         public void createNewFixnum(long value) {
             loadRuntime();
             method.ldc(new Long(value));
 
             invokeIRuby("newFixnum", sig(RubyFixnum.class, params(Long.TYPE)));
         }
 
         public void createNewBignum(BigInteger value) {
             loadRuntime();
             getCacheCompiler().cacheBigInteger(method, value);
             method.invokestatic(p(RubyBignum.class), "newBignum", sig(RubyBignum.class, params(Ruby.class, BigInteger.class)));
         }
 
         public void createNewString(ArrayCallback callback, int count) {
             loadRuntime();
             invokeIRuby("newString", sig(RubyString.class, params()));
             for (int i = 0; i < count; i++) {
                 callback.nextValue(this, null, i);
                 method.invokevirtual(p(RubyString.class), "append", sig(RubyString.class, params(IRubyObject.class)));
             }
         }
 
         public void createNewSymbol(ArrayCallback callback, int count) {
             loadRuntime();
             invokeIRuby("newString", sig(RubyString.class, params()));
             for (int i = 0; i < count; i++) {
                 callback.nextValue(this, null, i);
                 method.invokevirtual(p(RubyString.class), "append", sig(RubyString.class, params(IRubyObject.class)));
             }
             toJavaString();
             loadRuntime();
             method.swap();
             invokeIRuby("newSymbol", sig(RubySymbol.class, params(String.class)));
         }
 
         public void createNewString(ByteList value) {
             // FIXME: this is sub-optimal, storing string value in a java.lang.String again
             loadRuntime();
             getCacheCompiler().cacheByteList(method, value.toString());
 
             invokeIRuby("newStringShared", sig(RubyString.class, params(ByteList.class)));
         }
 
         public void createNewSymbol(String name) {
             getCacheCompiler().cacheSymbol(method, name);
         }
 
         public void createNewArray(boolean lightweight) {
             loadRuntime();
             // put under object array already present
             method.swap();
 
             if (lightweight) {
                 invokeIRuby("newArrayNoCopyLight", sig(RubyArray.class, params(IRubyObject[].class)));
             } else {
                 invokeIRuby("newArrayNoCopy", sig(RubyArray.class, params(IRubyObject[].class)));
             }
         }
 
         public void createEmptyArray() {
             loadRuntime();
 
             invokeIRuby("newArray", sig(RubyArray.class, params()));
         }
 
         public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
             buildObjectArray(IRUBYOBJECT, sourceArray, callback);
         }
 
         public void createObjectArray(int elementCount) {
             // if element count is less than 6, use helper methods
             if (elementCount < 6) {
                 Class[] params = new Class[elementCount];
                 Arrays.fill(params, IRubyObject.class);
                 invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params));
             } else {
                 // This is pretty inefficient for building an array, so just raise an error if someone's using it for a lot of elements
                 throw new NotCompilableException("Don't use createObjectArray(int) for more than 5 elements");
             }
         }
 
         private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
             if (sourceArray.length == 0) {
                 method.getstatic(p(IRubyObject.class), "NULL_ARRAY", ci(IRubyObject[].class));
             } else if (sourceArray.length <= RuntimeHelpers.MAX_SPECIFIC_ARITY_OBJECT_ARRAY) {
                 // if we have a specific-arity helper to construct an array for us, use that
                 for (int i = 0; i < sourceArray.length; i++) {
                     callback.nextValue(this, sourceArray, i);
                 }
                 invokeUtilityMethod("constructObjectArray", sig(IRubyObject[].class, params(IRubyObject.class, sourceArray.length)));
             } else {
                 // brute force construction inline
                 method.ldc(new Integer(sourceArray.length));
                 method.anewarray(type);
 
                 for (int i = 0; i < sourceArray.length; i++) {
                     method.dup();
                     method.ldc(new Integer(i));
 
                     callback.nextValue(this, sourceArray, i);
 
                     method.arraystore();
                 }
             }
         }
 
         public void createEmptyHash() {
             loadRuntime();
 
             method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, params(Ruby.class)));
         }
 
         public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
             loadRuntime();
             
             if (keyCount <= RuntimeHelpers.MAX_SPECIFIC_ARITY_HASH) {
                 // we have a specific-arity method we can use to construct, so use that
                 for (int i = 0; i < keyCount; i++) {
                     callback.nextValue(this, elements, i);
                 }
                 
                 invokeUtilityMethod("constructHash", sig(RubyHash.class, params(Ruby.class, IRubyObject.class, keyCount * 2)));
             } else {
                 method.invokestatic(p(RubyHash.class), "newHash", sig(RubyHash.class, params(Ruby.class)));
 
                 for (int i = 0; i < keyCount; i++) {
                     method.dup();
                     callback.nextValue(this, elements, i);
                     method.invokevirtual(p(RubyHash.class), "fastASet", sig(void.class, params(IRubyObject.class, IRubyObject.class)));
                 }
             }
         }
 
         public void createNewRange(boolean isExclusive) {
             loadRuntime();
             loadThreadContext();
 
             // could be more efficient with a callback
             method.dup2_x2();
             method.pop2();
 
-            method.ldc(new Boolean(isExclusive));
+            method.ldc(Boolean.valueOf(isExclusive));
 
             method.invokestatic(p(RubyRange.class), "newRange", sig(RubyRange.class, params(Ruby.class, ThreadContext.class, IRubyObject.class, IRubyObject.class, Boolean.TYPE)));
         }
 
         /**
          * Invoke IRubyObject.isTrue
          */
         private void isTrue() {
             invokeIRubyObject("isTrue", sig(Boolean.TYPE));
         }
 
         public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
             Label afterJmp = new Label();
             Label falseJmp = new Label();
 
             // call isTrue on the result
             isTrue();
 
             method.ifeq(falseJmp); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
             method.go_to(afterJmp);
 
             // FIXME: optimize for cases where we have no false branch
             method.label(falseJmp);
             falseBranch.branch(this);
 
             method.label(afterJmp);
         }
 
         public void performLogicalAnd(BranchCallback longBranch) {
             Label falseJmp = new Label();
 
             // dup it since we need to return appropriately if it's false
             method.dup();
 
             // call isTrue on the result
             isTrue();
 
             method.ifeq(falseJmp); // EQ == 0 (i.e. false)
             // pop the extra result and replace with the send part of the AND
             method.pop();
             longBranch.branch(this);
             method.label(falseJmp);
         }
 
         public void performLogicalOr(BranchCallback longBranch) {
             // FIXME: after jump is not in here.  Will if ever be?
             //Label afterJmp = new Label();
             Label falseJmp = new Label();
 
             // dup it since we need to return appropriately if it's false
             method.dup();
 
             // call isTrue on the result
             isTrue();
 
             method.ifne(falseJmp); // EQ == 0 (i.e. false)
             // pop the extra result and replace with the send part of the AND
             method.pop();
             longBranch.branch(this);
             method.label(falseJmp);
         }
 
         public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst) {
             // FIXME: handle next/continue, break, etc
             Label tryBegin = new Label();
             Label tryEnd = new Label();
             Label catchRedo = new Label();
             Label catchNext = new Label();
             Label catchBreak = new Label();
             Label catchRaised = new Label();
             Label endOfBody = new Label();
             Label conditionCheck = new Label();
             Label topOfBody = new Label();
             Label done = new Label();
             Label normalLoopEnd = new Label();
             method.trycatch(tryBegin, tryEnd, catchRedo, p(JumpException.RedoJump.class));
             method.trycatch(tryBegin, tryEnd, catchNext, p(JumpException.NextJump.class));
             method.trycatch(tryBegin, tryEnd, catchBreak, p(JumpException.BreakJump.class));
             if (checkFirst) {
                 // only while loops seem to have this RaiseException magic
                 method.trycatch(tryBegin, tryEnd, catchRaised, p(RaiseException.class));
             }
             
             method.label(tryBegin);
             {
                 
                 Label[] oldLoopLabels = currentLoopLabels;
                 
                 currentLoopLabels = new Label[] {endOfBody, topOfBody, done};
                 
                 // FIXME: if we terminate immediately, this appears to break while in method arguments
                 // we need to push a nil for the cases where we will never enter the body
                 if (checkFirst) {
                     method.go_to(conditionCheck);
                 }
 
                 method.label(topOfBody);
 
                 body.branch(this);
                 
                 method.label(endOfBody);
 
                 // clear body or next result after each successful loop
                 method.pop();
                 
                 method.label(conditionCheck);
                 
                 // check the condition
                 condition.branch(this);
                 isTrue();
                 method.ifne(topOfBody); // NE == nonzero (i.e. true)
                 
                 currentLoopLabels = oldLoopLabels;
             }
 
             method.label(tryEnd);
             // skip catch block
             method.go_to(normalLoopEnd);
 
             // catch logic for flow-control exceptions
             {
                 // redo jump
                 {
                     method.label(catchRedo);
                     method.pop();
                     method.go_to(topOfBody);
                 }
 
                 // next jump
                 {
                     method.label(catchNext);
                     method.pop();
                     // exceptionNext target is for a next that doesn't push a new value, like this one
                     method.go_to(conditionCheck);
                 }
 
                 // break jump
                 {
                     method.label(catchBreak);
                     loadBlock();
                     invokeUtilityMethod("breakJumpInWhile", sig(IRubyObject.class, JumpException.BreakJump.class, Block.class));
                     method.go_to(done);
                 }
 
                 // FIXME: This generates a crapload of extra code that is frequently *never* needed
                 // raised exception
                 if (checkFirst) {
                     // only while loops seem to have this RaiseException magic
                     method.label(catchRaised);
                     Label raiseNext = new Label();
                     Label raiseRedo = new Label();
                     Label raiseRethrow = new Label();
                     method.dup();
                     invokeUtilityMethod("getLocalJumpTypeOrRethrow", sig(String.class, params(RaiseException.class)));
                     // if we get here we have a RaiseException we know is a local jump error and an error type
 
                     // is it break?
                     method.dup(); // dup string
                     method.ldc("break");
                     method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
                     method.ifeq(raiseNext);
                     // pop the extra string, get the break value, and end the loop
                     method.pop();
                     invokeUtilityMethod("unwrapLocalJumpErrorValue", sig(IRubyObject.class, params(RaiseException.class)));
                     method.go_to(done);
 
                     // is it next?
                     method.label(raiseNext);
                     method.dup();
                     method.ldc("next");
                     method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
                     method.ifeq(raiseRedo);
                     // pop the extra string and the exception, jump to the condition
                     method.pop2();
                     method.go_to(conditionCheck);
 
                     // is it redo?
                     method.label(raiseRedo);
                     method.dup();
                     method.ldc("redo");
                     method.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
                     method.ifeq(raiseRethrow);
                     // pop the extra string and the exception, jump to the condition
                     method.pop2();
                     method.go_to(topOfBody);
 
                     // just rethrow it
                     method.label(raiseRethrow);
                     method.pop(); // pop extra string
                     method.athrow();
                 }
             }
             
             method.label(normalLoopEnd);
             loadNil();
             method.label(done);
         }
 
         public void createNewClosure(
                 int line,
                 StaticScope scope,
                 int arity,
                 CompilerCallback body,
                 CompilerCallback args,
                 boolean hasMultipleArgsHead,
                 NodeType argsNodeId,
                 ASTInspector inspector) {
             String closureMethodName = "closure_" + line + "_" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, inspector);
             
             closureCompiler.beginMethod(args, scope);
             
             body.call(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(p(CallbackFactory.class), "getBlockCallback", sig(CompiledBlockCallback.class, params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(arity));
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             method.ldc(Boolean.valueOf(hasMultipleArgsHead));
             method.ldc(BlockBody.asArgumentType(argsNodeId));
             // if there's a sub-closure or there's scope-aware methods, it can't be "light"
             method.ldc(!(inspector.hasClosure() || inspector.hasScopeAwareMethods()));
 
             invokeUtilityMethod("createBlock", sig(Block.class,
                     params(ThreadContext.class, IRubyObject.class, Integer.TYPE, String[].class, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE, boolean.class)));
         }
 
         public void runBeginBlock(StaticScope scope, CompilerCallback body) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(null, scope);
             
             body.call(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(p(CallbackFactory.class), "getBlockCallback", sig(CompiledBlockCallback.class, params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
 
             buildStaticScopeNames(method, scope);
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             invokeUtilityMethod("runBeginBlock", sig(IRubyObject.class,
                     params(ThreadContext.class, IRubyObject.class, String[].class, CompiledBlockCallback.class)));
         }
 
         public void createNewForLoop(int arity, CompilerCallback body, CompilerCallback args, boolean hasMultipleArgsHead, NodeType argsNodeId) {
             String closureMethodName = "closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(args, null);
             
             body.call(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(p(CallbackFactory.class), "getBlockCallback", sig(CompiledBlockCallback.class, params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(arity));
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             method.ldc(Boolean.valueOf(hasMultipleArgsHead));
             method.ldc(BlockBody.asArgumentType(argsNodeId));
 
             invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                     params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
         }
 
         public void createNewEndBlock(CompilerCallback body) {
             String closureMethodName = "END_closure" + ++innerIndex;
             String closureFieldName = "_" + closureMethodName;
             
             ASMClosureCompiler closureCompiler = new ASMClosureCompiler(closureMethodName, closureFieldName, null);
             
             closureCompiler.beginMethod(null, null);
             
             body.call(closureCompiler);
             
             closureCompiler.endMethod();
 
             // Done with closure compilation
             /////////////////////////////////////////////////////////////////////////////
             // Now, store a compiled block object somewhere we can access it in the future
             // in current method, load the field to see if we've created a BlockCallback yet
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated);
 
             // no callback, construct and cache it
             method.aload(THIS);
             getCallbackFactory();
 
             method.ldc(closureMethodName);
             method.aload(THIS);
             method.invokevirtual(p(CallbackFactory.class), "getBlockCallback", sig(CompiledBlockCallback.class, params(String.class, Object.class)));
             method.putfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
 
             method.label(alreadyCreated);
 
             // Construct the block for passing to the target method
             loadThreadContext();
             loadSelf();
             method.ldc(new Integer(0));
 
             method.aload(THIS);
             method.getfield(classname, closureFieldName, ci(CompiledBlockCallback.class));
             method.ldc(false);
             method.ldc(Block.ZERO_ARGS);
 
             invokeUtilityMethod("createSharedScopeBlock", sig(Block.class,
                     params(ThreadContext.class, IRubyObject.class, Integer.TYPE, CompiledBlockCallback.class, Boolean.TYPE, Integer.TYPE)));
             
             loadRuntime();
             invokeUtilityMethod("registerEndBlock", sig(void.class, Block.class, Ruby.class));
             loadNil();
         }
 
         private void getCallbackFactory() {
             // FIXME: Perhaps a bit extra code, but only for defn/s; examine
             loadRuntime();
             getCompiledClass();
             method.dup();
             method.invokevirtual(p(Class.class), "getClassLoader", sig(ClassLoader.class));
             method.invokestatic(p(CallbackFactory.class), "createFactory", sig(CallbackFactory.class, params(Ruby.class, Class.class, ClassLoader.class)));
         }
 
         public void getCompiledClass() {
             method.aload(THIS);
             method.getfield(classname, "$class", ci(Class.class));
         }
 
         private void getRubyClass() {
             loadThreadContext();
             invokeThreadContext("getRubyClass", sig(RubyModule.class));
         }
 
         public void println() {
             method.dup();
             method.getstatic(p(System.class), "out", ci(PrintStream.class));
             method.swap();
 
             method.invokevirtual(p(PrintStream.class), "println", sig(Void.TYPE, params(Object.class)));
         }
 
         public void defineAlias(String newName, String oldName) {
             loadThreadContext();
             method.ldc(newName);
             method.ldc(oldName);
             invokeUtilityMethod("defineAlias", sig(IRubyObject.class, ThreadContext.class, String.class, String.class));
         }
 
         public void loadFalse() {
             // TODO: cache?
             loadRuntime();
             invokeIRuby("getFalse", sig(RubyBoolean.class));
         }
 
         public void loadTrue() {
             // TODO: cache?
             loadRuntime();
             invokeIRuby("getTrue", sig(RubyBoolean.class));
         }
 
         public void loadCurrentModule() {
             loadThreadContext();
             invokeThreadContext("getCurrentScope", sig(DynamicScope.class));
             method.invokevirtual(p(DynamicScope.class), "getStaticScope", sig(StaticScope.class));
             method.invokevirtual(p(StaticScope.class), "getModule", sig(RubyModule.class));
         }
 
         public void retrieveInstanceVariable(String name) {
             loadRuntime();
             loadSelf();
             method.ldc(name);
             invokeUtilityMethod("fastGetInstanceVariable", sig(IRubyObject.class, Ruby.class, IRubyObject.class, String.class));
         }
 
         public void assignInstanceVariable(String name) {
             // FIXME: more efficient with a callback
             loadSelf();
             invokeIRubyObject("getInstanceVariables", sig(InstanceVariables.class));
             method.swap();
 
             method.ldc(name);
             method.swap();
 
             method.invokeinterface(p(InstanceVariables.class), "fastSetInstanceVariable", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
         }
 
         public void retrieveGlobalVariable(String name) {
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
             method.ldc(name);
             method.invokevirtual(p(GlobalVariables.class), "get", sig(IRubyObject.class, params(String.class)));
         }
 
         public void assignGlobalVariable(String name) {
             // FIXME: more efficient with a callback
             loadRuntime();
 
             invokeIRuby("getGlobalVariables", sig(GlobalVariables.class));
             method.swap();
             method.ldc(name);
             method.swap();
             method.invokevirtual(p(GlobalVariables.class), "set", sig(IRubyObject.class, params(String.class, IRubyObject.class)));
         }
 
         public void negateCurrentValue() {
             loadRuntime();
             invokeUtilityMethod("negate", sig(IRubyObject.class, IRubyObject.class, Ruby.class));
         }
 
         public void splatCurrentValue() {
             loadRuntime();
             method.invokestatic(p(ASTInterpreter.class), "splatValue", sig(RubyArray.class, params(IRubyObject.class, Ruby.class)));
         }
 
         public void singlifySplattedValue() {
             loadRuntime();
             method.invokestatic(p(ASTInterpreter.class), "aValueSplat", sig(IRubyObject.class, params(IRubyObject.class, Ruby.class)));
         }
 
         public void aryToAry() {
             loadRuntime();
             method.invokestatic(p(ASTInterpreter.class), "aryToAry", sig(IRubyObject.class, params(IRubyObject.class, Ruby.class)));
         }
 
         public void ensureRubyArray() {
             invokeUtilityMethod("ensureRubyArray", sig(RubyArray.class, params(IRubyObject.class)));
         }
 
         public void ensureMultipleAssignableRubyArray(boolean masgnHasHead) {
             loadRuntime();
             method.swap();
             method.ldc(new Boolean(masgnHasHead));
             invokeUtilityMethod("ensureMultipleAssignableRubyArray", sig(RubyArray.class, params(Ruby.class, IRubyObject.class, boolean.class)));
         }
 
         public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback, ArrayCallback nilCallback, CompilerCallback argsCallback) {
             // FIXME: This could probably be made more efficient
             for (; start < count; start++) {
                 Label noMoreArrayElements = new Label();
                 Label doneWithElement = new Label();
                 
                 // confirm we're not past the end of the array
                 method.dup(); // dup the original array object
                 method.invokevirtual(p(RubyArray.class), "getLength", sig(Integer.TYPE));
                 method.ldc(new Integer(start));
                 method.if_icmple(noMoreArrayElements); // if length <= start, end loop
                 
                 // extract item from array
                 method.dup(); // dup the original array object
                 method.ldc(new Integer(start)); // index for the item
                 method.invokevirtual(p(RubyArray.class), "entry", sig(IRubyObject.class, params(Integer.TYPE))); // extract item
                 callback.nextValue(this, source, start);
                 method.go_to(doneWithElement);
                 
                 // otherwise no items left available, use the code from nilCallback
                 method.label(noMoreArrayElements);
                 nilCallback.nextValue(this, source, start);
                 
                 // end of this element
                 method.label(doneWithElement);
                 // normal assignment leaves the value; pop it.
                 method.pop();
             }
             
             if (argsCallback != null) {
                 Label emptyArray = new Label();
                 Label readyForArgs = new Label();
                 // confirm we're not past the end of the array
                 method.dup(); // dup the original array object
                 method.invokevirtual(p(RubyArray.class), "getLength", sig(Integer.TYPE));
                 method.ldc(new Integer(start));
                 method.if_icmple(emptyArray); // if length <= start, end loop
                 
                 // assign remaining elements as an array for rest args
                 method.dup(); // dup the original array object
                 method.ldc(start);
                 invokeUtilityMethod("createSubarray", sig(RubyArray.class, RubyArray.class, int.class));
                 method.go_to(readyForArgs);
                 
                 // create empty array
                 method.label(emptyArray);
                 createEmptyArray();
                 
                 // assign rest args
                 method.label(readyForArgs);
                 argsCallback.call(this);
                 //consume leftover assigned value
                 method.pop();
             }
         }
 
         public void asString() {
             method.invokeinterface(p(IRubyObject.class), "asString", sig(RubyString.class, params()));
         }
         
         public void toJavaString() {
             method.invokevirtual(p(Object.class), "toString", sig(String.class));
         }
 
         public void nthRef(int match) {
             method.ldc(new Integer(match));
             backref();
             method.invokestatic(p(RubyRegexp.class), "nth_match", sig(IRubyObject.class, params(Integer.TYPE, IRubyObject.class)));
         }
 
         public void match() {
             loadThreadContext();
             method.invokevirtual(p(RubyRegexp.class), "op_match2", sig(IRubyObject.class, params(ThreadContext.class)));
         }
 
         public void match2() {
             loadThreadContext();
             method.swap();
             method.invokevirtual(p(RubyRegexp.class), "op_match", sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class)));
         }
 
         public void match3() {
             loadThreadContext();
             invokeUtilityMethod("match3", sig(IRubyObject.class, RubyRegexp.class, IRubyObject.class, ThreadContext.class));
         }
 
         public void createNewRegexp(final ByteList value, final int options) {
             String regexpField = getNewConstant(ci(RubyRegexp.class), "lit_reg_");
 
             // in current method, load the field to see if we've created a Pattern yet
             method.aload(THIS);
             method.getfield(classname, regexpField, ci(RubyRegexp.class));
 
             Label alreadyCreated = new Label();
             method.ifnonnull(alreadyCreated); //[]
 
             // load string, for Regexp#source and Regexp#inspect
             String regexpString = value.toString();
 
             loadRuntime(); //[R]
             method.ldc(regexpString); //[R, rS]
             method.ldc(new Integer(options)); //[R, rS, opts]
 
             method.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, params(Ruby.class, String.class, Integer.TYPE))); //[reg]
 
             method.aload(THIS); //[reg, T]
             method.swap(); //[T, reg]
             method.putfield(classname, regexpField, ci(RubyRegexp.class)); //[]
             method.label(alreadyCreated);
             method.aload(THIS); //[T]
             method.getfield(classname, regexpField, ci(RubyRegexp.class)); 
         }
 
         public void createNewRegexp(CompilerCallback createStringCallback, final int options) {
             boolean onceOnly = (options & ReOptions.RE_OPTION_ONCE) != 0;   // for regular expressions with the /o flag
             Label alreadyCreated = null;
             String regexpField = null;
 
             // only alter the code if the /o flag was present
             if (onceOnly) {
                 regexpField = getNewConstant(ci(RubyRegexp.class), "lit_reg_");
     
                 // in current method, load the field to see if we've created a Pattern yet
                 method.aload(THIS);
                 method.getfield(classname, regexpField, ci(RubyRegexp.class));
     
                 alreadyCreated = new Label();
                 method.ifnonnull(alreadyCreated);
             }
 
             loadRuntime();
 
             createStringCallback.call(this);
             method.invokevirtual(p(RubyString.class), "getByteList", sig(ByteList.class));
             method.ldc(new Integer(options));
 
             method.invokestatic(p(RubyRegexp.class), "newRegexp", sig(RubyRegexp.class, params(Ruby.class, ByteList.class, Integer.TYPE))); //[reg]
 
             // only alter the code if the /o flag was present
             if (onceOnly) {
                 method.aload(THIS);
                 method.swap();
                 method.putfield(classname, regexpField, ci(RubyRegexp.class));
                 method.label(alreadyCreated);
                 method.aload(THIS);
                 method.getfield(classname, regexpField, ci(RubyRegexp.class));
             }
         }
 
         public void pollThreadEvents() {
             if (!RubyInstanceConfig.THREADLESS_COMPILE_ENABLED) {
                 loadThreadContext();
                 invokeThreadContext("pollThreadEvents", sig(Void.TYPE));
             }
         }
 
         public void nullToNil() {
             Label notNull = new Label();
             method.dup();
             method.ifnonnull(notNull);
             method.pop();
             method.aload(NIL_INDEX);
             method.label(notNull);
         }
 
         public void isInstanceOf(Class clazz, BranchCallback trueBranch, BranchCallback falseBranch) {
             method.instance_of(p(clazz));
 
             Label falseJmp = new Label();
             Label afterJmp = new Label();
 
             method.ifeq(falseJmp); // EQ == 0 (i.e. false)
             trueBranch.branch(this);
 
             method.go_to(afterJmp);
             method.label(falseJmp);
 
             falseBranch.branch(this);
 
             method.label(afterJmp);
         }
 
         public void isCaptured(final int number, final BranchCallback trueBranch, final BranchCallback falseBranch) {
             backref();
             method.dup();
             isInstanceOf(RubyMatchData.class, new BranchCallback() {
 
                 public void branch(MethodCompiler context) {
                     method.visitTypeInsn(CHECKCAST, p(RubyMatchData.class));
                     method.dup();
                     method.invokevirtual(p(RubyMatchData.class), "use", sig(void.class));
                     method.ldc(new Long(number));
                     method.invokevirtual(p(RubyMatchData.class), "group", sig(IRubyObject.class, params(long.class)));
                     method.invokeinterface(p(IRubyObject.class), "isNil", sig(boolean.class));
                     Label isNil = new Label();
                     Label after = new Label();
 
                     method.ifne(isNil);
                     trueBranch.branch(context);
                     method.go_to(after);
 
                     method.label(isNil);
                     falseBranch.branch(context);
                     method.label(after);
                 }
             }, new BranchCallback() {
 
                 public void branch(MethodCompiler context) {
                     method.pop();
                     falseBranch.branch(context);
                 }
             });
         }
 
         public void branchIfModule(CompilerCallback receiverCallback, BranchCallback moduleCallback, BranchCallback notModuleCallback) {
             receiverCallback.call(this);
             isInstanceOf(RubyModule.class, moduleCallback, notModuleCallback);
         }
 
         public void backref() {
             loadThreadContext();
             invokeThreadContext("getCurrentFrame", sig(Frame.class));
             method.invokevirtual(p(Frame.class), "getBackRef", sig(IRubyObject.class));
         }
 
         public void backrefMethod(String methodName) {
             backref();
             method.invokestatic(p(RubyRegexp.class), methodName, sig(IRubyObject.class, params(IRubyObject.class)));
         }
         
         public void issueLoopBreak() {
             // inside a loop, break out of it
             // go to end of loop, leaving break value on stack
             method.go_to(currentLoopLabels[2]);
         }
         
         public void issueLoopNext() {
             // inside a loop, jump to conditional
             method.go_to(currentLoopLabels[0]);
         }
         
         public void issueLoopRedo() {
             // inside a loop, jump to body
             method.go_to(currentLoopLabels[1]);
         }
 
         protected String getNewEnsureName() {
             return "__ensure_" + (ensureNumber++);
         }
 
         public void protect(BranchCallback regularCode, BranchCallback protectedCode, Class ret) {
 
             String mname = getNewEnsureName();
             SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}), null, null));
             SkinnyMethodAdapter old_method = null;
             SkinnyMethodAdapter var_old_method = null;
             SkinnyMethodAdapter inv_old_method = null;
             boolean oldWithinProtection = withinProtection;
             withinProtection = true;
             try {
                 old_method = this.method;
                 var_old_method = getVariableCompiler().getMethodAdapter();
                 inv_old_method = getInvocationCompiler().getMethodAdapter();
                 this.method = mv;
                 getVariableCompiler().setMethodAdapter(mv);
                 getInvocationCompiler().setMethodAdapter(mv);
 
                 mv.visitCode();
                 // set up a local IRuby variable
 
                 mv.aload(THREADCONTEXT_INDEX);
                 mv.dup();
                 mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
             
                 // grab nil for local variables
                 mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
                 mv.astore(NIL_INDEX);
             
                 mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
                 mv.dup();
                 mv.astore(DYNAMIC_SCOPE_INDEX);
                 mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
                 mv.astore(VARS_ARRAY_INDEX);
 
                 Label codeBegin = new Label();
                 Label codeEnd = new Label();
                 Label ensureBegin = new Label();
                 Label ensureEnd = new Label();
                 method.label(codeBegin);
 
                 regularCode.branch(this);
 
                 method.label(codeEnd);
 
                 protectedCode.branch(this);
                 mv.areturn();
 
                 method.label(ensureBegin);
                 method.astore(EXCEPTION_INDEX);
                 method.label(ensureEnd);
 
                 protectedCode.branch(this);
 
                 method.aload(EXCEPTION_INDEX);
                 method.athrow();
                 
                 method.trycatch(codeBegin, codeEnd, ensureBegin, null);
                 method.trycatch(ensureBegin, ensureEnd, ensureBegin, null);
 
                 mv.visitMaxs(1, 1);
                 mv.visitEnd();
             } finally {
                 this.method = old_method;
                 getVariableCompiler().setMethodAdapter(var_old_method);
                 getInvocationCompiler().setMethodAdapter(inv_old_method);
                 withinProtection = oldWithinProtection;
             }
 
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
         }
 
         protected String getNewRescueName() {
             return "__rescue_" + (rescueNumber++);
         }
 
         public void rescue(BranchCallback regularCode, Class exception, BranchCallback catchCode, Class ret) {
             String mname = getNewRescueName();
             SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_SYNTHETIC, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}), null, null));
             SkinnyMethodAdapter old_method = null;
             SkinnyMethodAdapter var_old_method = null;
             SkinnyMethodAdapter inv_old_method = null;
             Label afterMethodBody = new Label();
             Label catchRetry = new Label();
             Label catchRaised = new Label();
             Label catchJumps = new Label();
             Label exitRescue = new Label();
             boolean oldWithinProtection = withinProtection;
             withinProtection = true;
             try {
                 old_method = this.method;
                 var_old_method = getVariableCompiler().getMethodAdapter();
                 inv_old_method = getInvocationCompiler().getMethodAdapter();
                 this.method = mv;
                 getVariableCompiler().setMethodAdapter(mv);
                 getInvocationCompiler().setMethodAdapter(mv);
 
                 mv.visitCode();
 
                 // set up a local IRuby variable
                 mv.aload(THREADCONTEXT_INDEX);
                 mv.dup();
                 mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                 mv.dup();
                 mv.astore(RUNTIME_INDEX);
                 
                 // store previous exception for restoration if we rescue something
                 loadRuntime();
                 invokeUtilityMethod("getErrorInfo", sig(IRubyObject.class, Ruby.class));
                 mv.astore(PREVIOUS_EXCEPTION_INDEX);
             
                 // grab nil for local variables
                 mv.invokevirtual(p(Ruby.class), "getNil", sig(IRubyObject.class));
                 mv.astore(NIL_INDEX);
             
                 mv.invokevirtual(p(ThreadContext.class), "getCurrentScope", sig(DynamicScope.class));
                 mv.dup();
                 mv.astore(DYNAMIC_SCOPE_INDEX);
                 mv.invokevirtual(p(DynamicScope.class), "getValues", sig(IRubyObject[].class));
                 mv.astore(VARS_ARRAY_INDEX);
 
                 Label beforeBody = new Label();
                 Label afterBody = new Label();
                 Label catchBlock = new Label();
                 mv.visitTryCatchBlock(beforeBody, afterBody, catchBlock, p(exception));
                 mv.visitLabel(beforeBody);
 
                 regularCode.branch(this);
 
                 mv.label(afterBody);
                 mv.go_to(exitRescue);
                 mv.label(catchBlock);
                 mv.astore(EXCEPTION_INDEX);
 
                 catchCode.branch(this);
                 
                 mv.label(afterMethodBody);
                 mv.go_to(exitRescue);
                 
                 // retry handling in the rescue block
                 mv.trycatch(catchBlock, afterMethodBody, catchRetry, p(JumpException.RetryJump.class));
                 mv.label(catchRetry);
                 mv.pop();
                 mv.go_to(beforeBody);
                 
                 // any exceptions raised must continue to be raised, skipping $! restoration
                 mv.trycatch(beforeBody, afterMethodBody, catchRaised, p(RaiseException.class));
                 mv.label(catchRaised);
                 mv.athrow();
                 
                 // and remaining jump exceptions should restore $!
                 mv.trycatch(beforeBody, afterMethodBody, catchJumps, p(JumpException.class));
                 mv.label(catchJumps);
                 loadRuntime();
                 mv.aload(PREVIOUS_EXCEPTION_INDEX);
                 invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
                 mv.athrow();
                 
                 mv.label(exitRescue);
                 
                 // restore the original exception
                 loadRuntime();
                 mv.aload(PREVIOUS_EXCEPTION_INDEX);
                 invokeUtilityMethod("setErrorInfo", sig(void.class, Ruby.class, IRubyObject.class));
                 
                 mv.areturn();
                 mv.visitMaxs(1, 1);
                 mv.visitEnd();
             } finally {
                 withinProtection = oldWithinProtection;
                 this.method = old_method;
                 getVariableCompiler().setMethodAdapter(var_old_method);
                 getInvocationCompiler().setMethodAdapter(inv_old_method);
             }
             
             method.aload(THIS);
             loadThreadContext();
             loadSelf();
             method.aload(ARGS_INDEX);
             if(this instanceof ASMClosureCompiler) {
                 pushNull();
             } else {
                 loadBlock();
             }
             method.invokevirtual(classname, mname, sig(ret, new Class[]{ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class}));
         }
 
         public void inDefined() {
             method.aload(THREADCONTEXT_INDEX);
             method.iconst_1();
             invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
         }
 
         public void outDefined() {
             method.aload(THREADCONTEXT_INDEX);
             method.iconst_0();
             invokeThreadContext("setWithinDefined", sig(void.class, params(boolean.class)));
         }
 
         public void stringOrNil() {
             loadRuntime();
             loadNil();
             invokeUtilityMethod("stringOrNil", sig(IRubyObject.class, String.class, Ruby.class, IRubyObject.class));
         }
 
         public void pushNull() {
             method.aconst_null();
         }
 
         public void pushString(String str) {
             method.ldc(str);
         }
 
         public void isMethodBound(String name, BranchCallback trueBranch, BranchCallback falseBranch) {
             metaclass();
             method.ldc(name);
             method.iconst_0(); // push false
             method.invokevirtual(p(RubyClass.class), "isMethodBound", sig(boolean.class, params(String.class, boolean.class)));
             Label falseLabel = new Label();
             Label exitLabel = new Label();
diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index a0eb60b749..dfcdceb283 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1,905 +1,905 @@
 package org.jruby.javasupport.util;
 
 import org.jruby.MetaClass;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyException;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyHash;
 import org.jruby.RubyKernel;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyMatchData;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CompiledBlock;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.CompiledBlockLight;
 import org.jruby.runtime.CompiledSharedScopeBlock;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.MethodFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.TypeConverter;
 
 /**
  * Helper methods which are called by the compiler.  Note: These will show no consumers, but
  * generated code does call these so don't remove them thinking they are dead code. 
  *
  */
 public class RuntimeHelpers {
     public static Block createBlock(ThreadContext context, IRubyObject self, int arity, 
             String[] staticScopeNames, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType, boolean light) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         if (light) {
             return CompiledBlockLight.newCompiledClosureLight(
                     context,
                     self,
                     Arity.createArity(arity),
                     staticScope,
                     callback,
                     hasMultipleArgsHead,
                     argsNodeType);
         } else {
             return CompiledBlock.newCompiledClosure(
                     context,
                     self,
                     Arity.createArity(arity),
                     staticScope,
                     callback,
                     hasMultipleArgsHead,
                     argsNodeType);
         }
     }
     
     public static IRubyObject runBeginBlock(ThreadContext context, IRubyObject self, String[] staticScopeNames, CompiledBlockCallback callback) {
         StaticScope staticScope = 
             new BlockStaticScope(context.getCurrentScope().getStaticScope(), staticScopeNames);
         staticScope.determineModule();
         
         context.preScopedBody(DynamicScope.newDynamicScope(staticScope, context.getCurrentScope()));
         
         Block block = CompiledBlock.newCompiledClosure(context, self, Arity.createArity(0), staticScope, callback, false, Block.ZERO_ARGS);
         
         block.yield(context, null);
         
         context.postScopedBody();
         
         return context.getRuntime().getNil();
     }
     
     public static Block createSharedScopeBlock(ThreadContext context, IRubyObject self, int arity, 
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argsNodeType) {
         
         return CompiledSharedScopeBlock.newCompiledSharedScopeClosure(context, self, Arity.createArity(arity), 
                 context.getCurrentScope(), callback, hasMultipleArgsHead, argsNodeType);
     }
     
     public static IRubyObject def(ThreadContext context, IRubyObject self, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         RubyModule containingClass = context.getRubyClass();
         Visibility visibility = context.getCurrentVisibility();
         
         if (containingClass == null) {
             throw runtime.newTypeError("No class to add method.");
         }
         
-        if (containingClass == runtime.getObject() && name == "initialize") {
+        if (containingClass == runtime.getObject() && name.equals("initialize")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop", "Object#initialize");
         }
 
-        if (name == "__id__" || name == "__send__") {
+        if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem", name); 
         }
 
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
-        if (name == "initialize" || visibility == Visibility.MODULE_FUNCTION) {
+        if (name.equals("initialize") || visibility == Visibility.MODULE_FUNCTION) {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), Visibility.PRIVATE, scope, scriptObject, callConfig);
         } else {
             method = factory.getCompiledMethod(containingClass, javaName, 
                     Arity.createArity(arity), visibility, scope, scriptObject, callConfig);
         }
         
         containingClass.addMethod(name, method);
         
         if (visibility == Visibility.MODULE_FUNCTION) {
             containingClass.getSingletonClass().addMethod(name,
                     new WrapperMethod(containingClass.getSingletonClass(), method,
                     Visibility.PUBLIC));
             containingClass.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
         }
         
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             ((MetaClass) containingClass).getAttached().callMethod(
                     context, "singleton_method_added", runtime.fastNewSymbol(name));
         } else {
             containingClass.callMethod(context, "method_added", runtime.fastNewSymbol(name));
         }
         
         return runtime.getNil();
     }
     
     public static IRubyObject defs(ThreadContext context, IRubyObject self, IRubyObject receiver, Object scriptObject, String name, String javaName, String[] scopeNames,
             int arity, int required, int optional, int rest, CallConfiguration callConfig) {
         Class compiledClass = scriptObject.getClass();
         Ruby runtime = context.getRuntime();
         
         if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
             throw runtime.newSecurityError("Insecure; can't define singleton method.");
         }
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
           throw runtime.newTypeError("can't define singleton method \"" + name
           + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) throw runtime.newFrozenError("object");
 
         RubyClass rubyClass = receiver.getSingletonClass();
 
         if (runtime.getSafeLevel() >= 4 && rubyClass.getMethods().get(name) != null) {
             throw runtime.newSecurityError("redefining method prohibited.");
         }
         
         StaticScope scope = new LocalStaticScope(context.getCurrentScope().getStaticScope(), scopeNames);
         scope.determineModule();
         scope.setArities(required, optional, rest);
         
         MethodFactory factory = MethodFactory.createFactory(compiledClass.getClassLoader());
         DynamicMethod method;
         
         method = factory.getCompiledMethod(rubyClass, javaName, 
                 Arity.createArity(arity), Visibility.PUBLIC, scope, scriptObject, callConfig);
         
         rubyClass.addMethod(name, method);
         receiver.callMethod(context, "singleton_method_added", runtime.fastNewSymbol(name));
         
         return runtime.getNil();
     }
     
     public static RubyClass getSingletonClass(Ruby runtime, IRubyObject receiver) {
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("no virtual class for " + receiver.getMetaClass().getBaseName());
         } else {
             if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
                 throw runtime.newSecurityError("Insecure: can't extend object.");
             }
 
             return receiver.getSingletonClass();
         }
     }
 
     public static IRubyObject doAttrAssign(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         return compilerCallMethod(context, receiver, name, args, caller, callType, block);
     }
     
     public static IRubyObject doAttrAssignIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         if (receiver == caller) callType = CallType.VARIABLE;
         
         return compilerCallMethodWithIndex(context, receiver, methodIndex, name, args, caller, 
                 callType, block);
     }
     
     public static IRubyObject doInvokeDynamic(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, String name, IRubyObject caller, CallType callType, Block block) {
         return compilerCallMethod(context, receiver, name, args, caller, callType, block);
     }
     
     public static IRubyObject doInvokeDynamicIndexed(IRubyObject receiver, IRubyObject[] args, 
             ThreadContext context, byte methodIndex, String name, IRubyObject caller, 
             CallType callType, Block block) {
         return compilerCallMethodWithIndex(context, receiver, methodIndex, name, args, caller, 
                 callType, block);
     }
 
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public static IRubyObject compilerCallMethodWithIndex(ThreadContext context, IRubyObject receiver, int methodIndex, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block) {
         RubyClass clazz = receiver.getMetaClass();
         
         if (clazz.index != 0) {
             return clazz.invoke(context, receiver, methodIndex, name, args, callType, block);
         }
         
         return compilerCallMethod(context, receiver, name, args, caller, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public static IRubyObject compilerCallMethod(ThreadContext context, IRubyObject receiver, String name,
             IRubyObject[] args, IRubyObject caller, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = receiver.getMetaClass();
         method = rubyclass.searchMethod(name);
         
         if (method.isUndefined() || (!name.equals("method_missing") && !method.isCallableFrom(caller, callType))) {
             return callMethodMissing(context, receiver, method, name, args, caller, callType, block);
         }
 
         return method.call(context, receiver, rubyclass, name, args, block);
     }
     
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, int methodIndex,
                                                 IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         // store call information so method_missing impl can use it            
         context.setLastCallStatus(callType);            
         context.setLastVisibility(method.getVisibility());
 
         if (methodIndex == MethodIndex.METHOD_MISSING) {
             return RubyKernel.method_missing(context, self, args, block);
         }
 
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = context.getRuntime().newSymbol(name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
     }
 
     public static IRubyObject callMethodMissing(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name, 
                                                 IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         // store call information so method_missing impl can use it            
         context.setLastCallStatus(callType);            
         context.setLastVisibility(method.getVisibility());
 
         if (name.equals("method_missing")) {
             return RubyKernel.method_missing(context, self, args, block);
         }
 
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 1, args.length);
         newArgs[0] = context.getRuntime().newSymbol(name);
 
         return receiver.callMethod(context, "method_missing", newArgs, block);
     }
 
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name) {
         return RuntimeHelpers.invoke(context, self, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, self, name, arg, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, self, name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, Block block) {
         return RuntimeHelpers.invoke(context, self, name, args, CallType.FUNCTIONAL, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject[] args, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, args, callType, block);
     }
     
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, String name, IRubyObject arg, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, name, arg, callType, block);
     }
     
     public static IRubyObject invokeAs(ThreadContext context, RubyClass asClass, IRubyObject self, String name, IRubyObject[] args, CallType callType, Block block) {
         return asClass.invoke(context, self, name, args, callType, block);
     }
     
     // Indexed versions are for STI, which has become more and more irrelevant
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args) {
         return RuntimeHelpers.invoke(context, self, methodIndex,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
     public static IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block) {
         return self.getMetaClass().invoke(context, self, methodIndex, name, args, callType, block);
     }
 
     public static RubyArray ensureRubyArray(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             value = RubyArray.newArray(value.getRuntime(), value);
         }
         return (RubyArray) value;
     }
 
     public static RubyArray ensureMultipleAssignableRubyArray(Ruby runtime, IRubyObject value, boolean masgnHasHead) {
         if (!(value instanceof RubyArray)) {
             value = ArgsUtil.convertToRubyArray(runtime, value, masgnHasHead);
         }
         return (RubyArray) value;
     }
     
     public static IRubyObject fetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.getClassVar(name);
     }
     
     public static IRubyObject fastFetchClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         return rubyClass.fastGetClassVar(internedName);
     }
     
     public static IRubyObject nullToNil(IRubyObject value, Ruby runtime) {
         return value != null ? value : runtime.getNil();
     }
     
     public static RubyClass prepareSuperClass(Ruby runtime, IRubyObject rubyClass) {
         RubyClass.checkInheritable(rubyClass); // use the same logic as in EvaluationState
         return (RubyClass)rubyClass;
     }
     
     public static RubyModule prepareClassNamespace(ThreadContext context, IRubyObject rubyModule) {
         if (rubyModule == null || rubyModule.isNil()) {
             rubyModule = context.getCurrentScope().getStaticScope().getModule();
             
             if (rubyModule == null) {
                 throw context.getRuntime().newTypeError("no outer class/module");
             }
         }
         
         return (RubyModule)rubyModule;
     }
     
     public static IRubyObject setClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String name, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject fastSetClassVariable(ThreadContext context, Ruby runtime, 
             IRubyObject self, String internedName, IRubyObject value) {
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) rubyClass = self.getMetaClass();
 
         rubyClass.fastSetClassVar(internedName, value);
    
         return value;
     }
     
     public static IRubyObject declareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String name, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.setClassVar(name, value);
    
         return value;
     }
     
     public static IRubyObject fastDeclareClassVariable(ThreadContext context, Ruby runtime, IRubyObject self, String internedName, IRubyObject value) {
         // FIXME: This isn't quite right; it shouldn't evaluate the value if it's going to throw the error
         RubyModule rubyClass = ASTInterpreter.getClassVariableBase(context, runtime);
    
         if (rubyClass == null) throw runtime.newTypeError("no class/module to define class variable");
         
         rubyClass.fastSetClassVar(internedName, value);
    
         return value;
     }
     
     public static void handleArgumentSizes(ThreadContext context, Ruby runtime, int given, int required, int opt, int rest) {
         if (opt == 0) {
             if (rest < 0) {
                 // no opt, no rest, exact match
                 if (given != required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             } else {
                 // only rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             }
         } else {
             if (rest < 0) {
                 // opt but no rest, must be at least required and no more than required + opt
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 } else if (given > (required + opt)) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + (required + opt) + ")");
                 }
             } else {
                 // opt and rest, must be at least required
                 if (given < required) {
                     throw runtime.newArgumentError("wrong # of arguments(" + given + " for " + required + ")");
                 }
             }
         }
     }
     
     public static String getLocalJumpTypeOrRethrow(RaiseException re) {
         RubyException exception = re.getException();
         Ruby runtime = exception.getRuntime();
         if (runtime.fastGetClass("LocalJumpError").isInstance(exception)) {
             RubyLocalJumpError jumpError = (RubyLocalJumpError)re.getException();
 
             IRubyObject reason = jumpError.reason();
 
             return reason.asJavaString();
         }
 
         throw re;
     }
     
     public static IRubyObject unwrapLocalJumpErrorValue(RaiseException re) {
         return ((RubyLocalJumpError)re.getException()).exit_value();
     }
     
     public static IRubyObject processBlockArgument(Ruby runtime, Block block) {
         if (!block.isGiven()) {
             return runtime.getNil();
         }
         
         RubyProc blockArg;
         
         if (block.getProcObject() != null) {
             blockArg = block.getProcObject();
         } else {
             blockArg = runtime.newProc(Block.Type.PROC, block);
             blockArg.getBlock().type = Block.Type.PROC;
         }
         
         return blockArg;
     }
     
     public static Block getBlockFromBlockPassBody(IRubyObject proc, Block currentBlock) {
         Ruby runtime = proc.getRuntime();
 
         // No block from a nil proc
         if (proc.isNil()) return Block.NULL_BLOCK;
 
         // If not already a proc then we should try and make it one.
         if (!(proc instanceof RubyProc)) {
             proc = TypeConverter.convertToType(proc, runtime.getProc(), 0, "to_proc", false);
 
             if (!(proc instanceof RubyProc)) {
                 throw runtime.newTypeError("wrong argument type "
                         + proc.getMetaClass().getName() + " (expected Proc)");
             }
         }
 
         // TODO: Add safety check for taintedness
         if (currentBlock != null && currentBlock.isGiven()) {
             RubyProc procObject = currentBlock.getProcObject();
             // The current block is already associated with proc.  No need to create a new one
             if (procObject != null && procObject == proc) return currentBlock;
         }
 
         return ((RubyProc) proc).getBlock();
     }
     
     public static IRubyObject backref(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         if(backref instanceof RubyMatchData) {
             ((RubyMatchData)backref).use();
         }
         return backref;
     }
     
     public static IRubyObject backrefLastMatch(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.last_match(backref);
     }
     
     public static IRubyObject backrefMatchPre(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_pre(backref);
     }
     
     public static IRubyObject backrefMatchPost(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_post(backref);
     }
     
     public static IRubyObject backrefMatchLast(ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         
         return RubyRegexp.match_last(backref);
     }
     
     public static IRubyObject callZSuper(Ruby runtime, ThreadContext context, Block block, IRubyObject self) {
         if (context.getFrameKlazz() == null) {
             String name = context.getFrameName();
             throw runtime.newNameError("superclass method '" + name
                     + "' disabled", name);
         }
         
         // Has the method that is calling super received a block argument
         if (!block.isGiven()) block = context.getCurrentFrame().getBlock(); 
         
         return self.callSuper(context, context.getCurrentScope().getArgValues(), block);
     }
     
     public static IRubyObject[] appendToObjectArray(IRubyObject[] array, IRubyObject add) {
         IRubyObject[] newArray = new IRubyObject[array.length + 1];
         System.arraycopy(array, 0, newArray, 0, array.length);
         newArray[array.length] = add;
         return newArray;
     }
     
     public static IRubyObject returnJump(IRubyObject result, ThreadContext context) {
         throw new JumpException.ReturnJump(context.getFrameJumpTarget(), result);
     }
     
     public static IRubyObject breakJumpInWhile(JumpException.BreakJump bj, Block aBlock) {
         // JRUBY-530, while case
         if (bj.getTarget() == aBlock.getBody()) {
             bj.setTarget(null);
             
             throw bj;
         }
 
         return (IRubyObject) bj.getValue();
     }
     
     public static IRubyObject breakJump(IRubyObject value) {
         throw new JumpException.BreakJump(null, value);
     }
     
     public static IRubyObject breakLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError("break", value, "unexpected break");
     }
     
     public static IRubyObject[] concatObjectArrays(IRubyObject[] array, IRubyObject[] add) {
         IRubyObject[] newArray = new IRubyObject[array.length + add.length];
         System.arraycopy(array, 0, newArray, 0, array.length);
         System.arraycopy(add, 0, newArray, array.length, add.length);
         return newArray;
     }
     
     public static IRubyObject isExceptionHandled(RubyException currentException, IRubyObject[] exceptions, Ruby runtime, ThreadContext context, IRubyObject self) {
         for (int i = 0; i < exceptions.length; i++) {
             if (!runtime.getModule().isInstance(exceptions[i])) {
                 throw runtime.newTypeError("class or module required for rescue clause");
             }
             IRubyObject result = exceptions[i].callMethod(context, "===", currentException);
             if (result.isTrue()) return result;
         }
         return runtime.getFalse();
     }
     
     public static void checkSuperDisabled(ThreadContext context) {
         RubyModule klazz = context.getFrameKlazz();
         
         if (klazz == null) {
             String name = context.getFrameName();
             throw context.getRuntime().newNameError("Superclass method '" + name
                     + "' disabled.", name);
         }
     }
     
     public static Block ensureSuperBlock(Block given, Block parent) {
         if (!given.isGiven()) {
             return parent;
         }
         return given;
     }
     
     public static RubyModule findImplementerIfNecessary(RubyModule clazz, RubyModule implementationClass) {
         if (implementationClass != null && implementationClass.needsImplementer()) {
             // modules are included with a shim class; we must find that shim to handle super() appropriately
             return clazz.findImplementer(implementationClass);
         } else {
             // classes are directly in the hierarchy, so no special logic is necessary for implementer
             return implementationClass;
         }
     }
     
     public static RubyArray createSubarray(RubyArray input, int start) {
         return (RubyArray)input.subseqLight(start, input.size() - start);
     }
     
     public static RubyArray createSubarray(IRubyObject[] input, Ruby runtime, int start) {
         return RubyArray.newArrayNoCopy(runtime, input, start);
     }
     
     public static RubyBoolean isWhenTriggered(IRubyObject expression, IRubyObject expressionsObject, ThreadContext context) {
         RubyArray expressions = ASTInterpreter.splatValue(context.getRuntime(), expressionsObject);
         for (int j = 0,k = expressions.getLength(); j < k; j++) {
             IRubyObject condition = expressions.eltInternal(j);
 
             if ((expression != null && condition.callMethod(context, MethodIndex.OP_EQQ, "===", expression)
                     .isTrue())
                     || (expression == null && condition.isTrue())) {
                 return context.getRuntime().getTrue();
             }
         }
         
         return context.getRuntime().getFalse();
     }
     
     public static IRubyObject setConstantInModule(IRubyObject module, IRubyObject value, String name, ThreadContext context) {
         return context.setConstantInModule(name, module, value);
     }
     
     public static IRubyObject retryJump() {
         throw JumpException.RETRY_JUMP;
     }
     
     public static IRubyObject redoJump() {
         throw JumpException.REDO_JUMP;
     }
     
     public static IRubyObject redoLocalJumpError(Ruby runtime) {
         throw runtime.newLocalJumpError("redo", runtime.getNil(), "unexpected redo");
     }
     
     public static IRubyObject nextJump(IRubyObject value) {
         throw new JumpException.NextJump(value);
     }
     
     public static IRubyObject nextLocalJumpError(Ruby runtime, IRubyObject value) {
         throw runtime.newLocalJumpError("next", value, "unexpected next");
     }
     
     public static final int MAX_SPECIFIC_ARITY_OBJECT_ARRAY = 5;
     
     public static IRubyObject[] constructObjectArray(IRubyObject one) {
         return new IRubyObject[] {one};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two) {
         return new IRubyObject[] {one, two};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three) {
         return new IRubyObject[] {one, two, three};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four) {
         return new IRubyObject[] {one, two, three, four};
     }
     
     public static IRubyObject[] constructObjectArray(IRubyObject one, IRubyObject two, IRubyObject three, IRubyObject four, IRubyObject five) {
         return new IRubyObject[] {one, two, three, four, five};
     }
     
     public static final int MAX_SPECIFIC_ARITY_HASH = 3;
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASet(key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASet(key1, value1);
         hash.fastASet(key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASet(key1, value1);
         hash.fastASet(key2, value2);
         hash.fastASet(key3, value3);
         return hash;
     }
     
     public static IRubyObject defineAlias(ThreadContext context, String newName, String oldName) {
         Ruby runtime = context.getRuntime();
         RubyModule module = context.getRubyClass();
    
         if (module == null) throw runtime.newTypeError("no class to make alias");
    
         module.defineAlias(newName, oldName);
         module.callMethod(context, "method_added", runtime.newSymbol(newName));
    
         return runtime.getNil();
     }
     
     public static IRubyObject getInstanceVariable(Ruby runtime, IRubyObject self, String name) {
         IRubyObject result = self.getInstanceVariables().getInstanceVariable(name);
         
         if (result != null) return result;
         
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + name + " not initialized");
         
         return runtime.getNil();
     }
     
     public static IRubyObject fastGetInstanceVariable(Ruby runtime, IRubyObject self, String internedName) {
         IRubyObject result;
         if ((result = self.getInstanceVariables().fastGetInstanceVariable(internedName)) != null) return result;
         
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
         
         return runtime.getNil();
     }
     
     public static IRubyObject negate(IRubyObject value, Ruby runtime) {
         if (value.isTrue()) return runtime.getFalse();
         return runtime.getTrue();
     }
     
     public static IRubyObject stringOrNil(String value, Ruby runtime, IRubyObject nil) {
         if (value == null) return nil;
         return RubyString.newString(runtime, value);
     }
     
     public static void preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = new LocalStaticScope(null, varNames);
         staticScope.setModule(context.getRuntime().getObject());
         DynamicScope scope = DynamicScope.newDynamicScope(staticScope);
         
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postScopedBody();
     }
     
     public static void registerEndBlock(Block block, Ruby runtime) {
         runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
     }
     
     public static IRubyObject match3(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match(context, value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
     
     public static IRubyObject getErrorInfo(Ruby runtime) {
         return runtime.getGlobalVariables().get("$!");
     }
     
     public static void setErrorInfo(Ruby runtime, IRubyObject error) {
         runtime.getGlobalVariables().set("$!", error);
     }
 
     public static void setLastLine(Ruby runtime, ThreadContext context, IRubyObject value) {
         context.getCurrentFrame().setLastLine(value);
     }
 
     public static IRubyObject getLastLine(Ruby runtime, ThreadContext context) {
         return context.getCurrentFrame().getLastLine();
     }
 
     public static void setBackref(Ruby runtime, ThreadContext context, IRubyObject value) {
         if (!value.isNil() && !(value instanceof RubyMatchData)) throw runtime.newTypeError(value, runtime.getMatchData());
         context.getCurrentFrame().setBackRef(value);
     }
 
     public static IRubyObject getBackref(Ruby runtime, ThreadContext context) {
         IRubyObject backref = context.getCurrentFrame().getBackRef();
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
         return backref;
     }
     
     public static IRubyObject preOpAsgnWithOrAnd(IRubyObject receiver, ThreadContext context, CallSite varSite) {
         return varSite.call(context, receiver);
     }
     
     public static IRubyObject postOpAsgnWithOrAnd(IRubyObject receiver, IRubyObject value, ThreadContext context, CallSite varAsgnSite) {
         return varAsgnSite.call(context, receiver, value);
     }
     
     public static IRubyObject opAsgnWithMethod(ThreadContext context, IRubyObject receiver, IRubyObject arg, CallSite varSite, CallSite opSite, CallSite opAsgnSite) {
         IRubyObject var = varSite.call(context, receiver);
         IRubyObject result = opSite.call(context, var, arg);
         opAsgnSite.call(context, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject receiver, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, receiver);
         IRubyObject result = opSite.call(context, var, value);
         elementAsgnSite.call(context, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, receiver, arg);
         IRubyObject result = opSite.call(context, var, value);
         elementAsgnSite.call(context, receiver, arg, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, receiver, arg1, arg2);
         IRubyObject result = opSite.call(context, var, value);
         elementAsgnSite.call(context, receiver, arg1, arg2, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, receiver, arg1, arg2, arg3);
         IRubyObject result = opSite.call(context, var, value);
         elementAsgnSite.call(context, receiver, new IRubyObject[] {arg1, arg2, arg3, result});
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, receiver);
         IRubyObject result = opSite.call(context, var, value);
         elementAsgnSite.call(context, receiver, appendToObjectArray(args, result));
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoOneArg(ThreadContext context, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, receiver, arg, value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoTwoArgs(ThreadContext context, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, receiver, args[0], args[1], value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoThreeArgs(ThreadContext context, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, receiver, new IRubyObject[] {args[0], args[1], args[2], value});
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoNArgs(ThreadContext context, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         asetSite.call(context, receiver, newArgs);
         return value;
     }
 }
diff --git a/src/org/jruby/lexer/yacc/RubyYaccLexer.java b/src/org/jruby/lexer/yacc/RubyYaccLexer.java
index bf054b954c..e298c8587d 100644
--- a/src/org/jruby/lexer/yacc/RubyYaccLexer.java
+++ b/src/org/jruby/lexer/yacc/RubyYaccLexer.java
@@ -1,1219 +1,1219 @@
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004-2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Zach Dennis <zdennis@mktec.com>
  * Copyright (C) 2006 Thomas Corbat <tcorbat@hsr.ch>
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
 package org.jruby.lexer.yacc;
 
 import java.io.IOException;
 
 import java.math.BigInteger;
 import java.util.HashMap;
 
 import org.jruby.ast.BackRefNode;
 import org.jruby.ast.BignumNode;
 import org.jruby.ast.CommentNode;
 import org.jruby.ast.FixnumNode;
 import org.jruby.ast.FloatNode;
 import org.jruby.ast.NthRefNode;
 import org.jruby.common.IRubyWarnings;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.lexer.yacc.SyntaxException.PID;
 import org.jruby.parser.ParserSupport;
 import org.jruby.parser.Tokens;
 import org.jruby.util.ByteList;
 
 
 /** This is a port of the MRI lexer to Java it is compatible to Ruby 1.8.1.
  */
 public class RubyYaccLexer {
     private static ByteList END_MARKER = new ByteList(new byte[] {'_', 'E', 'N', 'D', '_', '_'});
     private static ByteList BEGIN_DOC_MARKER = new ByteList(new byte[] {'b', 'e', 'g', 'i', 'n'});
     private static ByteList END_DOC_MARKER = new ByteList(new byte[] {'e', 'n', 'd'});
     private static HashMap<String, Keyword> map;
     
     static {
         map = new HashMap<String, Keyword>();
         
         map.put("end", Keyword.END);
         map.put("else", Keyword.ELSE);
         map.put("case", Keyword.CASE);
         map.put("ensure", Keyword.ENSURE);
         map.put("module", Keyword.MODULE);
         map.put("elsif", Keyword.ELSIF);
         map.put("def", Keyword.DEF);
         map.put("rescue", Keyword.RESCUE);
         map.put("not", Keyword.NOT);
         map.put("then", Keyword.THEN);
         map.put("yield", Keyword.YIELD);
         map.put("for", Keyword.FOR);
         map.put("self", Keyword.SELF);
         map.put("false", Keyword.FALSE);
         map.put("retry", Keyword.RETRY);
         map.put("return", Keyword.RETURN);
         map.put("true", Keyword.TRUE);
         map.put("if", Keyword.IF);
         map.put("defined?", Keyword.DEFINED_P);
         map.put("super", Keyword.SUPER);
         map.put("undef", Keyword.UNDEF);
         map.put("break", Keyword.BREAK);
         map.put("in", Keyword.IN);
         map.put("do", Keyword.DO);
         map.put("nil", Keyword.NIL);
         map.put("until", Keyword.UNTIL);
         map.put("unless", Keyword.UNLESS);
         map.put("or", Keyword.OR);
         map.put("next", Keyword.NEXT);
         map.put("when", Keyword.WHEN);
         map.put("redo", Keyword.REDO);
         map.put("and", Keyword.AND);
         map.put("begin", Keyword.BEGIN);
         map.put("__LINE__", Keyword.__LINE__);
         map.put("class", Keyword.CLASS);
         map.put("__FILE__", Keyword.__FILE__);
         map.put("END", Keyword.LEND);
         map.put("BEGIN", Keyword.LBEGIN);
         map.put("while", Keyword.WHILE);
         map.put("alias", Keyword.ALIAS);
     }
     
     public enum Keyword {
         END ("end", Tokens.kEND, Tokens.kEND, LexState.EXPR_END),
         ELSE ("else", Tokens.kELSE, Tokens.kELSE, LexState.EXPR_BEG),
         CASE ("case", Tokens.kCASE, Tokens.kCASE, LexState.EXPR_BEG),
         ENSURE ("ensure", Tokens.kENSURE, Tokens.kENSURE, LexState.EXPR_BEG),
         MODULE ("module", Tokens.kMODULE, Tokens.kMODULE, LexState.EXPR_BEG),
         ELSIF ("elsif", Tokens.kELSIF, Tokens.kELSIF, LexState.EXPR_BEG),
         DEF ("def", Tokens.kDEF, Tokens.kDEF, LexState.EXPR_FNAME),
         RESCUE ("rescue", Tokens.kRESCUE, Tokens.kRESCUE_MOD, LexState.EXPR_MID),
         NOT ("not", Tokens.kNOT, Tokens.kNOT, LexState.EXPR_BEG),
         THEN ("then", Tokens.kTHEN, Tokens.kTHEN, LexState.EXPR_BEG),
         YIELD ("yield", Tokens.kYIELD, Tokens.kYIELD, LexState.EXPR_ARG),
         FOR ("for", Tokens.kFOR, Tokens.kFOR, LexState.EXPR_BEG),
         SELF ("self", Tokens.kSELF, Tokens.kSELF, LexState.EXPR_END),
         FALSE ("false", Tokens.kFALSE, Tokens.kFALSE, LexState.EXPR_END),
         RETRY ("retry", Tokens.kRETRY, Tokens.kRETRY, LexState.EXPR_END),
         RETURN ("return", Tokens.kRETURN, Tokens.kRETURN, LexState.EXPR_MID),
         TRUE ("true", Tokens.kTRUE, Tokens.kTRUE, LexState.EXPR_END),
         IF ("if", Tokens.kIF, Tokens.kIF_MOD, LexState.EXPR_BEG),
         DEFINED_P ("defined?", Tokens.kDEFINED, Tokens.kDEFINED, LexState.EXPR_ARG),
         SUPER ("super", Tokens.kSUPER, Tokens.kSUPER, LexState.EXPR_ARG),
         UNDEF ("undef", Tokens.kUNDEF, Tokens.kUNDEF, LexState.EXPR_FNAME),
         BREAK ("break", Tokens.kBREAK, Tokens.kBREAK, LexState.EXPR_MID),
         IN ("in", Tokens.kIN, Tokens.kIN, LexState.EXPR_BEG),
         DO ("do", Tokens.kDO, Tokens.kDO, LexState.EXPR_BEG),
         NIL ("nil", Tokens.kNIL, Tokens.kNIL, LexState.EXPR_END),
         UNTIL ("until", Tokens.kUNTIL, Tokens.kUNTIL_MOD, LexState.EXPR_BEG),
         UNLESS ("unless", Tokens.kUNLESS, Tokens.kUNLESS_MOD, LexState.EXPR_BEG),
         OR ("or", Tokens.kOR, Tokens.kOR, LexState.EXPR_BEG),
         NEXT ("next", Tokens.kNEXT, Tokens.kNEXT, LexState.EXPR_MID),
         WHEN ("when", Tokens.kWHEN, Tokens.kWHEN, LexState.EXPR_BEG),
         REDO ("redo", Tokens.kREDO, Tokens.kREDO, LexState.EXPR_END),
         AND ("and", Tokens.kAND, Tokens.kAND, LexState.EXPR_BEG),
         BEGIN ("begin", Tokens.kBEGIN, Tokens.kBEGIN, LexState.EXPR_BEG),
         __LINE__ ("__LINE__", Tokens.k__LINE__, Tokens.k__LINE__, LexState.EXPR_END),
         CLASS ("class", Tokens.kCLASS, Tokens.kCLASS, LexState.EXPR_CLASS),
         __FILE__("__FILE__", Tokens.k__FILE__, Tokens.k__FILE__, LexState.EXPR_END),
         LEND ("END", Tokens.klEND, Tokens.klEND, LexState.EXPR_END),
         LBEGIN ("BEGIN", Tokens.klBEGIN, Tokens.klBEGIN, LexState.EXPR_END),
         WHILE ("while", Tokens.kWHILE, Tokens.kWHILE_MOD, LexState.EXPR_BEG),
         ALIAS ("alias", Tokens.kALIAS, Tokens.kALIAS, LexState.EXPR_FNAME);
         
         public final String name;
         public final int id0;
         public final int id1;
         public final LexState state;
         
         Keyword(String name, int id0, int id1, LexState state) {
             this.name = name;
             this.id0 = id0;
             this.id1 = id1;
             this.state = state;
         }
     }
     
     public enum LexState {
         EXPR_BEG, EXPR_END, EXPR_ARG, EXPR_CMDARG, EXPR_ENDARG, EXPR_MID,
         EXPR_FNAME, EXPR_DOT, EXPR_CLASS
     }
     
     public static Keyword getKeyword(String str) {
         return (Keyword) map.get(str);
     }
 
     // Last token read via yylex().
     private int token;
     
     // Value of last token which had a value associated with it.
     Object yaccValue;
 
     // Stream of data that yylex() examines.
     private LexerSource src;
     
     // Used for tiny smidgen of grammar in lexer (see setParserSupport())
     private ParserSupport parserSupport = null;
 
     // What handles warnings
     private IRubyWarnings warnings;
 
     // Additional context surrounding tokens that both the lexer and
     // grammar use.
     private LexState lex_state;
     
     // Tempory buffer to build up a potential token.  Consumer takes responsibility to reset 
     // this before use.
     private StringBuilder tokenBuffer = new StringBuilder(60);
 
     private StackState conditionState = new StackState();
     private StackState cmdArgumentState = new StackState();
     private StrTerm lex_strterm;
     private boolean commandStart;
 
     // Give a name to a value.  Enebo: This should be used more.
     static final int EOF = -1;
 
     // ruby constants for strings (should this be moved somewhere else?)
     static final int STR_FUNC_ESCAPE=0x01;
     static final int STR_FUNC_EXPAND=0x02;
     static final int STR_FUNC_REGEXP=0x04;
     static final int STR_FUNC_QWORDS=0x08;
     static final int STR_FUNC_SYMBOL=0x10;
     // When the heredoc identifier specifies <<-EOF that indents before ident. are ok (the '-').
     static final int STR_FUNC_INDENT=0x20;
 
-    private final int str_squote = 0;
-    private final int str_dquote = STR_FUNC_EXPAND;
-    private final int str_xquote = STR_FUNC_EXPAND;
-    private final int str_regexp = STR_FUNC_REGEXP | STR_FUNC_ESCAPE | STR_FUNC_EXPAND;
-    private final int str_ssym   = STR_FUNC_SYMBOL;
-    private final int str_dsym   = STR_FUNC_SYMBOL | STR_FUNC_EXPAND;
+    private static final int str_squote = 0;
+    private static final int str_dquote = STR_FUNC_EXPAND;
+    private static final int str_xquote = STR_FUNC_EXPAND;
+    private static final int str_regexp = STR_FUNC_REGEXP | STR_FUNC_ESCAPE | STR_FUNC_EXPAND;
+    private static final int str_ssym   = STR_FUNC_SYMBOL;
+    private static final int str_dsym   = STR_FUNC_SYMBOL | STR_FUNC_EXPAND;
     
     public RubyYaccLexer() {
     	reset();
     }
     
     public void reset() {
     	token = 0;
     	yaccValue = null;
     	src = null;
         lex_state = null;
         resetStacks();
         lex_strterm = null;
         commandStart = true;
     }
     
     /**
      * How the parser advances to the next token.
      * 
      * @return true if not at end of file (EOF).
      */
     public boolean advance() throws IOException {
         return (token = yylex()) != EOF;
     }
     
     /**
      * Last token read from the lexer at the end of a call to yylex()
      * 
      * @return last token read
      */
     public int token() {
         return token;
     }
 
     public StringBuilder getTokenBuffer() {
         return tokenBuffer;
     }
     
     /**
      * Value of last token (if it is a token which has a value).
      * 
      * @return value of last value-laden token
      */
     public Object value() {
         return yaccValue;
     }
 
     public ISourcePositionFactory getPositionFactory() {
         return src.getPositionFactory();
     }
     
     /**
      * Get position information for Token/Node that follows node represented by startPosition 
      * and current lexer location.
      * 
      * @param startPosition previous node/token
      * @param inclusive include previous node into position information of current node
      * @return a new position
      */
     public ISourcePosition getPosition(ISourcePosition startPosition, boolean inclusive) {
     	return src.getPosition(startPosition, inclusive); 
     }
     
     public ISourcePosition getPosition() {
         return src.getPosition(null, false);
     }
 
     /**
      * Parse must pass its support object for some check at bottom of
      * yylex().  Ruby does it this way as well (i.e. a little parsing
      * logic in the lexer).
      * 
      * @param parserSupport
      */
     public void setParserSupport(ParserSupport parserSupport) {
         this.parserSupport = parserSupport;
     }
 
     /**
      * Allow the parser to set the source for its lexer.
      * 
      * @param source where the lexer gets raw data
      */
     public void setSource(LexerSource source) {
         this.src = source;
     }
 
     public StrTerm getStrTerm() {
         return lex_strterm;
     }
     
     public void setStrTerm(StrTerm strterm) {
         this.lex_strterm = strterm;
     }
 
     public void resetStacks() {
         conditionState.reset();
         cmdArgumentState.reset();
     }
     
     public void setWarnings(IRubyWarnings warnings) {
         this.warnings = warnings;
     }
 
 
     public void setState(LexState state) {
         this.lex_state = state;
     }
 
     public StackState getCmdArgumentState() {
         return cmdArgumentState;
     }
 
     public StackState getConditionState() {
         return conditionState;
     }
     
     public void setValue(Object yaccValue) {
         this.yaccValue = yaccValue;
     }
 
     private boolean isNext_identchar() throws IOException {
         int c = src.read();
         src.unread(c);
 
         return c != EOF && (Character.isLetterOrDigit(c) || c == '_');
     }
 
     private void determineExpressionState() {
         switch (lex_state) {
         case EXPR_FNAME: case EXPR_DOT:
             lex_state = LexState.EXPR_ARG;
             break;
         default:
             lex_state = LexState.EXPR_BEG;
             break;
         }
     }
 
     private Object getInteger(String value, int radix) {
         try {
             return new FixnumNode(getPosition(), Long.parseLong(value, radix));
         } catch (NumberFormatException e) {
             return new BignumNode(getPosition(), new BigInteger(value, radix));
         }
     }
 
 	/**
 	 * @param c the character to test
 	 * @return true if character is a hex value (0-9a-f)
 	 */
     static final boolean isHexChar(int c) {
         return Character.isDigit(c) || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
     }
 
     /**
 	 * @param c the character to test
      * @return true if character is an octal value (0-7)
 	 */
     static final boolean isOctChar(int c) {
         return '0' <= c && c <= '7';
     }
     
     /**
      * @param c is character to be compared
      * @return whether c is an identifier or not
      */
     public static final boolean isIdentifierChar(int c) {
         return Character.isLetterOrDigit(c) || c == '_';
     }
     
     /**
      * What type/kind of quote are we dealing with?
      * 
      * @param c first character the the quote construct
      * @return a token that specifies the quote type
      */
     private int parseQuote(int c) throws IOException {
         int begin, end;
         boolean shortHand;
         
         // Short-hand (e.g. %{,%.,%!,... versus %Q{).
         if (!Character.isLetterOrDigit(c)) {
             begin = c;
             c = 'Q';
             shortHand = true;
         // Long-hand (e.g. %Q{}).
         } else {
             shortHand = false;
             begin = src.read();
             if (Character.isLetterOrDigit(begin) /* no mb || ismbchar(term)*/) {
                 throw new SyntaxException(PID.STRING_UNKNOWN_TYPE, getPosition(), "unknown type of %string");
             }
         }
         if (c == EOF || begin == EOF) {
             throw new SyntaxException(PID.STRING_HITS_EOF, getPosition(), "unterminated quoted string meets end of file");
         }
         
         // Figure end-char.  '\0' is special to indicate begin=end and that no nesting?
         switch(begin) {
         case '(': end = ')'; break;
         case '[': end = ']'; break;
         case '{': end = '}'; break;
         case '<': end = '>'; break;
         default: 
             end = begin; 
             begin = '\0';
         }
 
         switch (c) {
         case 'Q':
             lex_strterm = new StringTerm(str_dquote, begin ,end);
             yaccValue = new Token("%"+ (shortHand ? (""+end) : ("" + c + begin)), getPosition());
             return Tokens.tSTRING_BEG;
 
         case 'q':
             lex_strterm = new StringTerm(str_squote, begin, end);
             yaccValue = new Token("%"+c+begin, getPosition());
             return Tokens.tSTRING_BEG;
 
         case 'W':
             lex_strterm = new StringTerm(str_dquote | STR_FUNC_QWORDS, begin, end);
             do {c = src.read();} while (Character.isWhitespace(c));
             src.unread(c);
             yaccValue = new Token("%"+c+begin, getPosition());
             return Tokens.tWORDS_BEG;
 
         case 'w':
             lex_strterm = new StringTerm(str_squote | STR_FUNC_QWORDS, begin, end);
             do {c = src.read();} while (Character.isWhitespace(c));
             src.unread(c);
             yaccValue = new Token("%"+c+begin, getPosition());
             return Tokens.tQWORDS_BEG;
 
         case 'x':
             lex_strterm = new StringTerm(str_xquote, begin, end);
             yaccValue = new Token("%"+c+begin, getPosition());
             return Tokens.tXSTRING_BEG;
 
         case 'r':
             lex_strterm = new StringTerm(str_regexp, begin, end);
             yaccValue = new Token("%"+c+begin, getPosition());
             return Tokens.tREGEXP_BEG;
 
         case 's':
             lex_strterm = new StringTerm(str_ssym, begin, end);
             lex_state = LexState.EXPR_FNAME;
             yaccValue = new Token("%"+c+begin, getPosition());
             return Tokens.tSYMBEG;
 
         default:
             throw new SyntaxException(PID.STRING_UNKNOWN_TYPE, getPosition(), 
                     "Unknown type of %string. Expected 'Q', 'q', 'w', 'x', 'r' or any non letter character, but found '" + c + "'.");
         }
     }
     
     private int hereDocumentIdentifier() throws IOException {
         int c = src.read(); 
         int term;
 
         int func = 0;
         if (c == '-') {
             c = src.read();
             func = STR_FUNC_INDENT;
         }
         
         ByteList markerValue;
         if (c == '\'' || c == '"' || c == '`') {
             if (c == '\'') {
                 func |= str_squote;
             } else if (c == '"') {
                 func |= str_dquote;
             } else {
                 func |= str_xquote; 
             }
 
             markerValue = new ByteList();
             term = c;
             while ((c = src.read()) != EOF && c != term) {
                 markerValue.append(c);
             }
             if (c == EOF) {
                 throw new SyntaxException(PID.STRING_MARKER_MISSING, getPosition(), "unterminated here document identifier");
             }	
         } else {
             if (!isIdentifierChar(c)) {
                 src.unread(c);
                 if ((func & STR_FUNC_INDENT) != 0) {
                     src.unread('-');
                 }
                 return 0;
             }
             markerValue = new ByteList();
             term = '"';
             func |= str_dquote;
             do {
                 markerValue.append(c);
             } while ((c = src.read()) != EOF && isIdentifierChar(c));
 
             src.unread(c);
         }
 
         ByteList lastLine = src.readLineBytes();
         lastLine.append('\n');
         lex_strterm = new HeredocTerm(markerValue, func, lastLine);
 
         if (term == '`') {
             yaccValue = new Token("`", getPosition());
             return Tokens.tXSTRING_BEG;
         }
         
         yaccValue = new Token("\"", getPosition());
         // Hacky: Advance position to eat newline here....
         getPosition();
         return Tokens.tSTRING_BEG;
     }
     
     private void arg_ambiguous() {
         warnings.warning(ID.AMBIGUOUS_ARGUMENT, getPosition(), "Ambiguous first argument; make sure.");
     }
 
     /**
      * Read a comment up to end of line.  When found each comment will get stored away into
      * the parser result so that any interested party can use them as they seem fit.  One idea
      * is that IDE authors can do distance based heuristics to associate these comments to the
      * AST node they think they belong to.
      * 
      * @param c last character read from lexer source
      * @return newline or eof value 
      */
     protected int readComment(int c) throws IOException {
         if (!parserSupport.getConfiguration().hasExtraPositionInformation()) {
             return src.skipUntil('\n');
         }
         
         ISourcePosition startPosition = src.getPosition();
         tokenBuffer.setLength(0);
         tokenBuffer.append((char) c);
 
         // FIXME: Consider making a better LexerSource.readLine
         while ((c = src.read()) != '\n') {
             if (c == EOF) break;
 
             tokenBuffer.append((char) c);
         }
         src.unread(c);
         
         // Store away each comment to parser result so IDEs can do whatever they want with them.
         ISourcePosition position = startPosition.union(getPosition());
         
         parserSupport.getResult().addComment(new CommentNode(position, tokenBuffer.toString()));
         
         return c;
     }
     
     /*
      * Not normally used, but is left in here since it can be useful in debugging
      * grammar and lexing problems.
      *
      *
     private void printToken(int token) {
         //System.out.print("LOC: " + support.getPosition() + " ~ ");
         
         switch (token) {
         	case Tokens.yyErrorCode: System.err.print("yyErrorCode,"); break;
         	case Tokens.kCLASS: System.err.print("kClass,"); break;
         	case Tokens.kMODULE: System.err.print("kModule,"); break;
         	case Tokens.kDEF: System.err.print("kDEF,"); break;
         	case Tokens.kUNDEF: System.err.print("kUNDEF,"); break;
         	case Tokens.kBEGIN: System.err.print("kBEGIN,"); break;
         	case Tokens.kRESCUE: System.err.print("kRESCUE,"); break;
         	case Tokens.kENSURE: System.err.print("kENSURE,"); break;
         	case Tokens.kEND: System.err.print("kEND,"); break;
         	case Tokens.kIF: System.err.print("kIF,"); break;
         	case Tokens.kUNLESS: System.err.print("kUNLESS,"); break;
         	case Tokens.kTHEN: System.err.print("kTHEN,"); break;
         	case Tokens.kELSIF: System.err.print("kELSIF,"); break;
         	case Tokens.kELSE: System.err.print("kELSE,"); break;
         	case Tokens.kCASE: System.err.print("kCASE,"); break;
         	case Tokens.kWHEN: System.err.print("kWHEN,"); break;
         	case Tokens.kWHILE: System.err.print("kWHILE,"); break;
         	case Tokens.kUNTIL: System.err.print("kUNTIL,"); break;
         	case Tokens.kFOR: System.err.print("kFOR,"); break;
         	case Tokens.kBREAK: System.err.print("kBREAK,"); break;
         	case Tokens.kNEXT: System.err.print("kNEXT,"); break;
         	case Tokens.kREDO: System.err.print("kREDO,"); break;
         	case Tokens.kRETRY: System.err.print("kRETRY,"); break;
         	case Tokens.kIN: System.err.print("kIN,"); break;
         	case Tokens.kDO: System.err.print("kDO,"); break;
         	case Tokens.kDO_COND: System.err.print("kDO_COND,"); break;
         	case Tokens.kDO_BLOCK: System.err.print("kDO_BLOCK,"); break;
         	case Tokens.kRETURN: System.err.print("kRETURN,"); break;
         	case Tokens.kYIELD: System.err.print("kYIELD,"); break;
         	case Tokens.kSUPER: System.err.print("kSUPER,"); break;
         	case Tokens.kSELF: System.err.print("kSELF,"); break;
         	case Tokens.kNIL: System.err.print("kNIL,"); break;
         	case Tokens.kTRUE: System.err.print("kTRUE,"); break;
         	case Tokens.kFALSE: System.err.print("kFALSE,"); break;
         	case Tokens.kAND: System.err.print("kAND,"); break;
         	case Tokens.kOR: System.err.print("kOR,"); break;
         	case Tokens.kNOT: System.err.print("kNOT,"); break;
         	case Tokens.kIF_MOD: System.err.print("kIF_MOD,"); break;
         	case Tokens.kUNLESS_MOD: System.err.print("kUNLESS_MOD,"); break;
         	case Tokens.kWHILE_MOD: System.err.print("kWHILE_MOD,"); break;
         	case Tokens.kUNTIL_MOD: System.err.print("kUNTIL_MOD,"); break;
         	case Tokens.kRESCUE_MOD: System.err.print("kRESCUE_MOD,"); break;
         	case Tokens.kALIAS: System.err.print("kALIAS,"); break;
         	case Tokens.kDEFINED: System.err.print("kDEFINED,"); break;
         	case Tokens.klBEGIN: System.err.print("klBEGIN,"); break;
         	case Tokens.klEND: System.err.print("klEND,"); break;
         	case Tokens.k__LINE__: System.err.print("k__LINE__,"); break;
         	case Tokens.k__FILE__: System.err.print("k__FILE__,"); break;
         	case Tokens.tIDENTIFIER: System.err.print("tIDENTIFIER["+ value() + "],"); break;
         	case Tokens.tFID: System.err.print("tFID[" + value() + "],"); break;
         	case Tokens.tGVAR: System.err.print("tGVAR[" + value() + "],"); break;
         	case Tokens.tIVAR: System.err.print("tIVAR[" + value() +"],"); break;
         	case Tokens.tCONSTANT: System.err.print("tCONSTANT["+ value() +"],"); break;
         	case Tokens.tCVAR: System.err.print("tCVAR,"); break;
         	case Tokens.tINTEGER: System.err.print("tINTEGER,"); break;
         	case Tokens.tFLOAT: System.err.print("tFLOAT,"); break;
             case Tokens.tSTRING_CONTENT: System.err.print("tSTRING_CONTENT[" + ((StrNode) value()).getValue().toString() + "],"); break;
             case Tokens.tSTRING_BEG: System.err.print("tSTRING_BEG,"); break;
             case Tokens.tSTRING_END: System.err.print("tSTRING_END,"); break;
             case Tokens.tSTRING_DBEG: System.err.print("STRING_DBEG,"); break;
             case Tokens.tSTRING_DVAR: System.err.print("tSTRING_DVAR,"); break;
             case Tokens.tXSTRING_BEG: System.err.print("tXSTRING_BEG,"); break;
             case Tokens.tREGEXP_BEG: System.err.print("tREGEXP_BEG,"); break;
             case Tokens.tREGEXP_END: System.err.print("tREGEXP_END,"); break;
             case Tokens.tWORDS_BEG: System.err.print("tWORDS_BEG,"); break;
             case Tokens.tQWORDS_BEG: System.err.print("tQWORDS_BEG,"); break;
         	case Tokens.tBACK_REF: System.err.print("tBACK_REF,"); break;
         	case Tokens.tNTH_REF: System.err.print("tNTH_REF,"); break;
         	case Tokens.tUPLUS: System.err.print("tUPLUS"); break;
         	case Tokens.tUMINUS: System.err.print("tUMINUS,"); break;
         	case Tokens.tPOW: System.err.print("tPOW,"); break;
         	case Tokens.tCMP: System.err.print("tCMP,"); break;
         	case Tokens.tEQ: System.err.print("tEQ,"); break;
         	case Tokens.tEQQ: System.err.print("tEQQ,"); break;
         	case Tokens.tNEQ: System.err.print("tNEQ,"); break;
         	case Tokens.tGEQ: System.err.print("tGEQ,"); break;
         	case Tokens.tLEQ: System.err.print("tLEQ,"); break;
         	case Tokens.tANDOP: System.err.print("tANDOP,"); break;
         	case Tokens.tOROP: System.err.print("tOROP,"); break;
         	case Tokens.tMATCH: System.err.print("tMATCH,"); break;
         	case Tokens.tNMATCH: System.err.print("tNMATCH,"); break;
         	case Tokens.tDOT2: System.err.print("tDOT2,"); break;
         	case Tokens.tDOT3: System.err.print("tDOT3,"); break;
         	case Tokens.tAREF: System.err.print("tAREF,"); break;
         	case Tokens.tASET: System.err.print("tASET,"); break;
         	case Tokens.tLSHFT: System.err.print("tLSHFT,"); break;
         	case Tokens.tRSHFT: System.err.print("tRSHFT,"); break;
         	case Tokens.tCOLON2: System.err.print("tCOLON2,"); break;
         	case Tokens.tCOLON3: System.err.print("tCOLON3,"); break;
         	case Tokens.tOP_ASGN: System.err.print("tOP_ASGN,"); break;
         	case Tokens.tASSOC: System.err.print("tASSOC,"); break;
         	case Tokens.tLPAREN: System.err.print("tLPAREN,"); break;
         	case Tokens.tLPAREN_ARG: System.err.print("tLPAREN_ARG,"); break;
         	case Tokens.tLBRACK: System.err.print("tLBRACK,"); break;
         	case Tokens.tLBRACE: System.err.print("tLBRACE,"); break;
             case Tokens.tSTAR: System.err.print("tSTAR,"); break;
             case Tokens.tSTAR2: System.err.print("tSTAR2,"); break;
         	case Tokens.tAMPER: System.err.print("tAMPER,"); break;
         	case Tokens.tSYMBEG: System.err.print("tSYMBEG,"); break;
         	case '\n': System.err.println("NL"); break;
         	default: System.err.print("'" + (char)token + "',"); break;
         }
     }
 
     // DEBUGGING HELP 
     private int yylex2() throws IOException {
         int token = yylex();
         
         printToken(token);
         
         return token;
     }*/
 
     /**
      *  Returns the next token. Also sets yyVal is needed.
      *
      *@return    Description of the Returned Value
      */
     private int yylex() throws IOException {
         int c;
         boolean spaceSeen = false;
         boolean commandState;
         
         if (lex_strterm != null) {
 			int tok = lex_strterm.parseString(this, src);
 			if (tok == Tokens.tSTRING_END || tok == Tokens.tREGEXP_END) {
 			    lex_strterm = null;
 			    lex_state = LexState.EXPR_END;
 			}
 			return tok;
         }
 
         commandState = commandStart;
         commandStart = false;
 
         loop: for(;;) {
             c = src.read();            
             switch(c) {
             case '\004':		/* ^D */
             case '\032':		/* ^Z */
             case EOF:			/* end of script. */
                 return EOF;
            
                 /* white spaces */
             case ' ': case '\t': case '\f': case '\r':
             case '\13': /* '\v' */
                 getPosition();
                 spaceSeen = true;
                 continue;
             case '#':		/* it's a comment */
                 if (readComment(c) == EOF) return EOF;
                     
                 /* fall through */
             case '\n':
             	// Replace a string of newlines with a single one
                 while((c = src.read()) == '\n');
                 src.unread(c);
                 getPosition();
 
                 switch (lex_state) {
                 case EXPR_BEG: case EXPR_FNAME: case EXPR_DOT: case EXPR_CLASS:
                     continue loop;
                 }
 
                 commandStart = true;
                 lex_state = LexState.EXPR_BEG;
                 return '\n';
             case '*':
                 return star(spaceSeen);
             case '!':
                 return bang();
             case '=':
                 // documentation nodes
                 if (src.wasBeginOfLine()) {
                     boolean doComments = parserSupport.getConfiguration().hasExtraPositionInformation();
                     if (src.matchMarker(BEGIN_DOC_MARKER, false, false)) {
                         if (doComments) {
                             tokenBuffer.setLength(0);
                             tokenBuffer.append(BEGIN_DOC_MARKER);
                         }
                         c = src.read();
                         
                         if (Character.isWhitespace(c)) {
                             // In case last next was the newline.
                             src.unread(c);
                             for (;;) {
                                 c = src.read();
                                 if (doComments) tokenBuffer.append((char) c);
 
                                 // If a line is followed by a blank line put
                                 // it back.
                                 while (c == '\n') {
                                     c = src.read();
                                     if (doComments) tokenBuffer.append((char) c);
                                 }
                                 if (c == EOF) {
                                     throw new SyntaxException(PID.STRING_HITS_EOF, getPosition(), "embedded document meets end of file");
                                 }
                                 if (c != '=') continue;
                                 if (src.wasBeginOfLine() && src.matchMarker(END_DOC_MARKER, false, false)) {
                                     if (doComments) tokenBuffer.append(END_DOC_MARKER);
                                     ByteList list = src.readLineBytes();
                                     if (doComments) tokenBuffer.append(list);
                                     src.unread('\n');
                                     break;
                                 }
                             }
 
                             if (doComments) {
                                 parserSupport.getResult().addComment(new CommentNode(getPosition(), tokenBuffer.toString()));
                             }
                             continue;
                         }
 						src.unread(c);
                     }
                 }
 
                 determineExpressionState();
 
                 c = src.read();
                 if (c == '=') {
                     c = src.read();
                     if (c == '=') {
                         yaccValue = new Token("===", getPosition());
                         return Tokens.tEQQ;
                     }
                     src.unread(c);
                     yaccValue = new Token("==", getPosition());
                     return Tokens.tEQ;
                 }
                 if (c == '~') {
                     yaccValue = new Token("=~", getPosition());
                     return Tokens.tMATCH;
                 } else if (c == '>') {
                     yaccValue = new Token("=>", getPosition());
                     return Tokens.tASSOC;
                 }
                 src.unread(c);
                 yaccValue = new Token("=", getPosition());
                 return '=';
                 
             case '<':
                 return lessThan(spaceSeen);
             case '>':
                 return greaterThan();
             case '"':
                 return doubleQuote();
             case '`':
                 return backtick(commandState);
             case '\'':
                 return singleQuote();
             case '?':
                 return questionMark();
             case '&':
                 return ampersand(spaceSeen);
             case '|':
                 return pipe();
             case '+':
                 return plus(spaceSeen);
             case '-':
                 return minus(spaceSeen);
             case '.':
                 return dot();
             case '0' : case '1' : case '2' : case '3' : case '4' :
             case '5' : case '6' : case '7' : case '8' : case '9' :
                 return parseNumber(c);
             case ')':
                 return rightParen();
             case ']':
                 return rightBracket();
             case '}':
                 return rightCurly();
             case ':':
                 return colon(spaceSeen);
             case '/':
                 return slash(spaceSeen);
             case '^':
                 return caret();
             case ';':
                 commandStart = true;
             case ',':
                 return comma(c);
             case '~':
                 return tilde();
             case '(':
                 return leftParen(spaceSeen);
             case '[':
                 return leftBracket(spaceSeen);
             case '{':
             	return leftCurly();
             case '\\':
                 c = src.read();
                 if (c == '\n') {
                     spaceSeen = true;
                     continue;
                 }
                 src.unread(c);
                 yaccValue = new Token("\\", getPosition());
                 return '\\';
             case '%':
                 return percent(spaceSeen);
             case '$':
                 return dollar();
             case '@':
                 return at();
             case '_':
                 if (src.wasBeginOfLine() && src.matchMarker(END_MARKER, false, true)) {
                 	parserSupport.getResult().setEndOffset(src.getOffset());
                     return EOF;
                 }
                 return identifier(c, commandState);
             default:
                 return identifier(c, commandState);
             }
         }
     }
 
     private int identifierToken(LexState last_state, int result, String value) {
 
         if (result == Tokens.tIDENTIFIER && last_state != LexState.EXPR_DOT &&
                 parserSupport.getCurrentScope().isDefined(value) >= 0) {
             lex_state = LexState.EXPR_END;
         }
 
         yaccValue = new Token(value, result, getPosition());
         return result;
     }
 
     private int getIdentifier(int c) throws IOException {
         do {
             tokenBuffer.append((char) c);
             /* no special multibyte character handling is needed in Java
              * if (ismbchar(c)) {
                 int i, len = mbclen(c)-1;
 
                 for (i = 0; i < len; i++) {
                     c = src.read();
                     tokenBuffer.append(c);
                 }
             }*/
             c = src.read();
         } while (isIdentifierChar(c));
         
         return c;
     }
     
     private int ampersand(boolean spaceSeen) throws IOException {
         int c = src.read();
         
         switch (c) {
         case '&':
             lex_state = LexState.EXPR_BEG;
             if ((c = src.read()) == '=') {
                 yaccValue = new Token("&&", getPosition());
                 lex_state = LexState.EXPR_BEG;
                 return Tokens.tOP_ASGN;
             }
             src.unread(c);
             yaccValue = new Token("&&", getPosition());
             return Tokens.tANDOP;
         case '=':
             yaccValue = new Token("&", getPosition());
             lex_state = LexState.EXPR_BEG;
             return Tokens.tOP_ASGN;
         }
         src.unread(c);
         
         //tmpPosition is required because getPosition()'s side effects.
         //if the warning is generated, the getPosition() on line 954 (this line + 18) will create
         //a wrong position if the "inclusive" flag is not set.
         ISourcePosition tmpPosition = getPosition();
         if ((lex_state == LexState.EXPR_ARG || lex_state == LexState.EXPR_CMDARG) && 
                 spaceSeen && !Character.isWhitespace(c)) {
             warnings.warning(ID.ARGUMENT_AS_PREFIX, tmpPosition, "`&' interpreted as argument prefix", "&");
             c = Tokens.tAMPER;
         } else if (lex_state == LexState.EXPR_BEG || 
                 lex_state == LexState.EXPR_MID) {
             c = Tokens.tAMPER;
         } else {
             c = Tokens.tAMPER2;
         }
         
         determineExpressionState();
         
         yaccValue = new Token("&", tmpPosition);
         return c;
     }
     
     private int at() throws IOException {
         int c = src.read();
         int result;
         tokenBuffer.setLength(0);
         tokenBuffer.append('@');
         if (c == '@') {
             tokenBuffer.append('@');
             c = src.read();
             result = Tokens.tCVAR;
         } else {
             result = Tokens.tIVAR;                    
         }
         
         if (Character.isDigit(c)) {
             if (tokenBuffer.length() == 1) {
                 throw new SyntaxException(PID.IVAR_BAD_NAME, getPosition(), "`@" + c + "' is not allowed as an instance variable name");
             }
             throw new SyntaxException(PID.CVAR_BAD_NAME, getPosition(), "`@@" + c + "' is not allowed as a class variable name");
         }
         
         if (!isIdentifierChar(c)) {
             src.unread(c);
             yaccValue = new Token("@", getPosition());
             return '@';
         }
 
         c = getIdentifier(c);
         src.unread(c);
 
         LexState last_state = lex_state;
         lex_state = LexState.EXPR_END;
 
         return identifierToken(last_state, result, tokenBuffer.toString().intern());        
     }
     
     private int backtick(boolean commandState) throws IOException {
         yaccValue = new Token("`", getPosition());
 
         switch (lex_state) {
         case EXPR_FNAME:
             lex_state = LexState.EXPR_END;
             
             return Tokens.tBACK_REF2;
         case EXPR_DOT:
             lex_state = commandState ? LexState.EXPR_CMDARG : LexState.EXPR_ARG;
 
             return Tokens.tBACK_REF2;
         default:
             lex_strterm = new StringTerm(str_xquote, '\0', '`');
         
             return Tokens.tXSTRING_BEG;
         }
     }
     
     private int bang() throws IOException {
         int c = src.read();
         lex_state = LexState.EXPR_BEG;
         
         switch (c) {
         case '=':
             yaccValue = new Token("!=",getPosition());
             
             return Tokens.tNEQ;
         case '~':
             yaccValue = new Token("!~",getPosition());
             
             return Tokens.tNMATCH;
         default: // Just a plain bang
             src.unread(c);
             yaccValue = new Token("!",getPosition());
             
             return Tokens.tBANG;
         }
     }
     
     private int caret() throws IOException {
         int c = src.read();
         if (c == '=') {
             lex_state = LexState.EXPR_BEG;
             yaccValue = new Token("^", getPosition());
             return Tokens.tOP_ASGN;
         }
         
         determineExpressionState();
         
         src.unread(c);
         yaccValue = new Token("^", getPosition());
         return Tokens.tCARET;
     }
 
     private int colon(boolean spaceSeen) throws IOException {
         int c = src.read();
         
         if (c == ':') {
             if (lex_state == LexState.EXPR_BEG ||
                 lex_state == LexState.EXPR_MID ||
                 lex_state == LexState.EXPR_CLASS || 
                 ((lex_state == LexState.EXPR_ARG || lex_state == LexState.EXPR_CMDARG) && spaceSeen)) {
                 lex_state = LexState.EXPR_BEG;
                 yaccValue = new Token("::", getPosition());
                 return Tokens.tCOLON3;
             }
             lex_state = LexState.EXPR_DOT;
             yaccValue = new Token(":",getPosition());
             return Tokens.tCOLON2;
         }
         
         if (lex_state == LexState.EXPR_END || 
             lex_state == LexState.EXPR_ENDARG || Character.isWhitespace(c)) {
             src.unread(c);
             lex_state = LexState.EXPR_BEG;
             yaccValue = new Token(":",getPosition());
             return ':';
         }
         
         switch (c) {
         case '\'':
             lex_strterm = new StringTerm(str_ssym, '\0', c);
             break;
         case '"':
             lex_strterm = new StringTerm(str_dsym, '\0', c);
             break;
         default:
             src.unread(c);
             break;
         }
         
         lex_state = LexState.EXPR_FNAME;
         yaccValue = new Token(":", getPosition());
         return Tokens.tSYMBEG;
     }
 
     private int comma(int c) throws IOException {
         lex_state = LexState.EXPR_BEG;
         yaccValue = new Token(",", getPosition());
         
         return c;
     }
     
     private int dollar() throws IOException {
         LexState last_state = lex_state;
         lex_state = LexState.EXPR_END;
         int c = src.read();
         
         switch (c) {
         case '_':       /* $_: last read line string */
             c = src.read();
             if (isIdentifierChar(c)) {
                 tokenBuffer.setLength(0);
                 tokenBuffer.append("$_");
                 c = getIdentifier(c);
                 src.unread(c);
                 last_state = lex_state;
                 lex_state = LexState.EXPR_END;
 
                 return identifierToken(last_state, Tokens.tGVAR, tokenBuffer.toString().intern());
             }
             src.unread(c);
             c = '_';
             
             // fall through
         case '~':       /* $~: match-data */
         case '*':       /* $*: argv */
         case '$':       /* $$: pid */
         case '?':       /* $?: last status */
         case '!':       /* $!: error string */
         case '@':       /* $@: error position */
         case '/':       /* $/: input record separator */
         case '\\':      /* $\: output record separator */
         case ';':       /* $;: field separator */
         case ',':       /* $,: output field separator */
         case '.':       /* $.: last read line number */
         case '=':       /* $=: ignorecase */
         case ':':       /* $:: load path */
         case '<':       /* $<: reading filename */
         case '>':       /* $>: default output handle */
         case '\"':      /* $": already loaded files */
             yaccValue = new Token("$" + (char) c, Tokens.tGVAR, getPosition());
             return Tokens.tGVAR;
 
         case '-':
             tokenBuffer.setLength(0);
             tokenBuffer.append('$');
             tokenBuffer.append((char) c);
             c = src.read();
             if (isIdentifierChar(c)) {
                 tokenBuffer.append((char) c);
             } else {
                 src.unread(c);
             }
             yaccValue = new Token(tokenBuffer.toString(), Tokens.tGVAR, getPosition());
             /* xxx shouldn't check if valid option variable */
             return Tokens.tGVAR;
 
         case '&':       /* $&: last match */
         case '`':       /* $`: string before last match */
         case '\'':      /* $': string after last match */
         case '+':       /* $+: string matches last paren. */
             // Explicit reference to these vars as symbols...
             if (last_state == LexState.EXPR_FNAME) {
                 yaccValue = new Token("$" + (char) c, Tokens.tGVAR, getPosition());
                 return Tokens.tGVAR;
             }
             
             yaccValue = new BackRefNode(getPosition(), c);
             return Tokens.tBACK_REF;
 
         case '1': case '2': case '3': case '4': case '5': case '6':
         case '7': case '8': case '9':
             tokenBuffer.setLength(0);
             tokenBuffer.append('$');
             do {
                 tokenBuffer.append((char) c);
                 c = src.read();
             } while (Character.isDigit(c));
             src.unread(c);
             if (last_state == LexState.EXPR_FNAME) {
                 yaccValue = new Token(tokenBuffer.toString(), Tokens.tGVAR, getPosition());
                 return Tokens.tGVAR;
             }
             
             yaccValue = new NthRefNode(getPosition(), Integer.parseInt(tokenBuffer.substring(1)));
             return Tokens.tNTH_REF;
         case '0':
             lex_state = LexState.EXPR_END;
 
             return identifierToken(last_state, Tokens.tGVAR, ("$" + (char) c).intern());
         default:
             if (!isIdentifierChar(c)) {
                 src.unread(c);
                 yaccValue = new Token("$", getPosition());
                 return '$';
             }
         
             // $blah
             tokenBuffer.setLength(0);
             tokenBuffer.append('$');
             int d = getIdentifier(c);
             src.unread(d);
             last_state = lex_state;
             lex_state = LexState.EXPR_END;
 
             return identifierToken(last_state, Tokens.tGVAR, tokenBuffer.toString().intern());
         }
     }
     
     private int dot() throws IOException {
         int c;
         
diff --git a/src/org/jruby/runtime/MethodFactory.java b/src/org/jruby/runtime/MethodFactory.java
index 00b6717108..d010aff463 100644
--- a/src/org/jruby/runtime/MethodFactory.java
+++ b/src/org/jruby/runtime/MethodFactory.java
@@ -1,158 +1,158 @@
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
  * Copyright (C) 2008 The JRuby Community <www.jruby.org>
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
 package org.jruby.runtime;
 
 import java.lang.reflect.Method;
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.ReflectionMethodFactory;
 import org.jruby.internal.runtime.methods.InvocationMethodFactory;
 import org.jruby.internal.runtime.methods.DumpingInvocationMethodFactory;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.SafePropertyAccessor;
 
 /**
  * MethodFactory is used to generate "invokers" or "method handles" given a target
  * class, method name, and other characteristics. In order to bind methods into
  * Ruby's reified class hierarchy, we need a way to treat individual methods as
  * objects. Implementers of this class provide that functionality.
  */
 public abstract class MethodFactory {
     /**
      * A Class[] representing the signature of compiled Ruby method.
      */
     public final static Class[] COMPILED_METHOD_PARAMS = new Class[] {ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class};
     
     /**
      * For batched method construction, the logic necessary to bind resulting
      * method objects into a target module/class must be provided as a callback.
      * This interface should be implemented by code calling any batched methods
      * on this MethodFactory.
      */
     public interface MethodDefiningCallback {
         public void define(RubyModule targetMetaClass, Method method, DynamicMethod dynamicMethod);
     }
 
     /**
      * Based on optional properties, create a new MethodFactory. By default,
      * this will create a code-generation-based InvocationMethodFactory. If
      * security restricts code generation, ReflectionMethodFactory will be used.
      * If we are dumping class definitions, DumpingInvocationMethodFactory will
      * be used. See MethodFactory's static initializer for more details.
      * 
      * @param classLoader The classloader to use for searching for and
      * dynamically loading code.
      * @return A new MethodFactory.
      */
     public static MethodFactory createFactory(ClassLoader classLoader) {
         if (reflection) return new ReflectionMethodFactory();
         if (dumping) return new DumpingInvocationMethodFactory(dumpingPath, classLoader);
 
         return new InvocationMethodFactory(classLoader);
     }
     
     /**
      * Get a new method handle based on the target JRuby-compiled method.
      * Because compiled Ruby methods have additional requirements and
      * characteristics not typically found in Java-based methods, this is
      * provided as a separate way to define such method handles.
      * 
      * @param implementationClass The class to which the method will be bound.
      * @param method The name of the method
      * @param arity The Arity of the method
      * @param visibility The method's visibility on the target type.
      * @param scope The methods static scoping information.
      * @param scriptObject An instace of the target compiled method class.
      * @param callConfig The call configuration to use for this method.
      * @return A new method handle for the target compiled method.
      */
     public abstract DynamicMethod getCompiledMethod(
             RubyModule implementationClass, String method, 
             Arity arity, Visibility visibility, StaticScope scope, 
             Object scriptObject, CallConfiguration callConfig);
     
     /**
      * Based on an annotated Java method object, generate a method handle using
      * the annotation and the target signature. The annotation and signature
      * will be used to dynamically generate the appropriate call logic for the
      * handle.
      * 
      * @param implementationClass The target class or module on which the method
      * will be bound.
      * @param method The java.lang.Method object for the target method.
      * @return A method handle for the target object.
      */
     public abstract DynamicMethod getAnnotatedMethod(RubyModule implementationClass, Method method);
     
     /**
      * Add all annotated methods on the target Java class to the specified
      * Ruby class using the semantics of getAnnotatedMethod, calling back to
      * the specified callback for each method to allow the caller to bind
      * each method.
      * 
      * @param implementationClass The target class or module on which the method
      * will be bound.
      * @param containingClass The Java class containined annotated methods to
      * be bound.
      * @param callback A callback provided by the caller which handles binding
      * each method.
      */
     public abstract void defineIndexedAnnotatedMethods(RubyModule implementationClass, Class containingClass, MethodDefiningCallback callback);
 
     /**
      * Use the reflection-based factory.
      */
     private static boolean reflection = false;
     /**
      * User the dumping-based factory, which generates .class files as it runs.
      */
     private static boolean dumping = false;
     /**
      * The target path for the dumping factory to save the .class files.
      */
     private static String dumpingPath = null;
     
     static {
         // initialize the static settings to determine which factory to use
         if (Ruby.isSecurityRestricted()) {
             reflection = true;
         } else {
             if (SafePropertyAccessor.getProperty("jruby.reflection") != null && SafePropertyAccessor.getBoolean("jruby.reflection")) {
                 reflection = true;
             }
             if (SafePropertyAccessor.getProperty("jruby.dump_invocations") != null) {
                 dumping = true;
-                dumpingPath = SafePropertyAccessor.getProperty("jruby.dump_invocations").toString();
+                dumpingPath = SafePropertyAccessor.getProperty("jruby.dump_invocations");
             }
         }
     }
 }
diff --git a/src/org/jruby/runtime/callback/InvocationCallbackFactory.java b/src/org/jruby/runtime/callback/InvocationCallbackFactory.java
index fe327876a1..4975306bc2 100644
--- a/src/org/jruby/runtime/callback/InvocationCallbackFactory.java
+++ b/src/org/jruby/runtime/callback/InvocationCallbackFactory.java
@@ -1,1325 +1,1325 @@
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
 package org.jruby.runtime.callback;
 
 import java.security.AccessController;
 import java.security.PrivilegedAction;
 import java.security.ProtectionDomain;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyClass;
 import org.jruby.RubyKernel;
 import org.jruby.RubyModule;
 import org.objectweb.asm.ClassWriter;
 import org.objectweb.asm.Label;
 import org.objectweb.asm.MethodVisitor;
 import org.objectweb.asm.Opcodes;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CompiledBlockCallback;
 import org.jruby.runtime.Dispatcher;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import static org.jruby.util.CodegenUtils.* ;
 import org.jruby.util.JRubyClassLoader;
 
 public class InvocationCallbackFactory extends CallbackFactory implements Opcodes {
     private final Class type;
     final ProtectionDomain protectionDomain;
     protected final JRubyClassLoader classLoader;
     private final String typePath;
     protected final Ruby runtime;
 
     private final static String SUPER_CLASS = p(InvocationCallback.class);
     private final static String FAST_SUPER_CLASS = p(FastInvocationCallback.class);
     private final static String CALL_SIG = sig(RubyKernel.IRUBY_OBJECT, params(Object.class,
             Object[].class, Block.class));
     private final static String FAST_CALL_SIG = sig(RubyKernel.IRUBY_OBJECT, params(
             Object.class, Object[].class));
     private final static String BLOCK_CALL_SIG = sig(RubyKernel.IRUBY_OBJECT, params(
             ThreadContext.class, RubyKernel.IRUBY_OBJECT, IRubyObject[].class));
     private final static String IRUB = p(RubyKernel.IRUBY_OBJECT);
     
     
     public static final int DISPATCHER_THREADCONTEXT_INDEX = 1;
     public static final int DISPATCHER_SELF_INDEX = 2;
     public static final int DISPATCHER_RUBYMODULE_INDEX = 3;
     public static final int DISPATCHER_METHOD_INDEX = 4;
     public static final int DISPATCHER_NAME_INDEX = 5;
     public static final int DISPATCHER_ARGS_INDEX = 6;
     public static final int DISPATCHER_CALLTYPE_INDEX = 7;
     public static final int DISPATCHER_BLOCK_INDEX = 8;
     public static final int DISPATCHER_RUNTIME_INDEX = 9;
 
     private static final int METHOD_ARGS_INDEX = 2;
 
     public InvocationCallbackFactory(Ruby runtime, final Class type, ClassLoader classLoader) {
         this.type = type;
         if (classLoader instanceof JRubyClassLoader) {
             this.classLoader = (JRubyClassLoader)classLoader;
         } else {
            this.classLoader = new JRubyClassLoader(classLoader);
         }
         this.typePath = p(type);
         this.runtime = runtime;
         
         SecurityManager sm = System.getSecurityManager();
         if (sm == null) {
             this.protectionDomain = type.getProtectionDomain();
         } else {
             this.protectionDomain = AccessController.doPrivileged(
                     new PrivilegedAction<ProtectionDomain>() {
                         public ProtectionDomain run() {
                             return type.getProtectionDomain();
                         }
                     });
         }
     }
 
     private Class getReturnClass(String method, Class[] args) throws Exception {
         return type.getMethod(method, args).getReturnType();
     }
 
     private ClassWriter createCtor(String namePath) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         cw.visit(V1_4, ACC_PUBLIC + ACC_SUPER, namePath, null, SUPER_CLASS, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitMethodInsn(INVOKESPECIAL, SUPER_CLASS, "<init>", "()V");
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitInsn(RETURN);
         mv.visitMaxs(1, 1);
         mv.visitEnd();
         return cw;
     }
 
     private ClassWriter createCtorDispatcher(String namePath, Map switchMap) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         cw.visit(V1_4, ACC_PUBLIC + ACC_SUPER, namePath, null, p(Dispatcher.class), null);
         SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cw.visitMethod(ACC_PUBLIC, "<init>", sig(Void.TYPE, params(Ruby.class)), null, null));
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitMethodInsn(INVOKESPECIAL, p(Dispatcher.class), "<init>", "()V");
         Label line = new Label();
         mv.visitLineNumber(0, line);
         
         // create our array
         mv.aload(0);
         mv.ldc(new Integer(MethodIndex.NAMES.size()));
         mv.newarray(T_BYTE);
         mv.putfield(p(Dispatcher.class), "switchTable", ci(byte[].class));
         
         // for each switch value, set it into the table
         mv.aload(0);
         mv.getfield(p(Dispatcher.class), "switchTable", ci(byte[].class));
         
         for (Iterator switchIter = switchMap.keySet().iterator(); switchIter.hasNext();) {
             Integer switchValue = (Integer)switchIter.next();
             mv.dup();
             
             // method index
             mv.ldc(new Integer(MethodIndex.getIndex((String)switchMap.get(switchValue))));
             // switch value is one-based, add one
             mv.ldc(switchValue);
             
             // store
             mv.barraystore();
         }
         
         // clear the extra table on stack
         mv.pop();
         
         mv.visitInsn(RETURN);
         mv.visitMaxs(1, 1);
         mv.visitEnd();
         return cw;
     }
 
     private ClassWriter createCtorFast(String namePath) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         cw.visit(V1_4, ACC_PUBLIC + ACC_SUPER, namePath, null, FAST_SUPER_CLASS, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitMethodInsn(INVOKESPECIAL, FAST_SUPER_CLASS, "<init>", "()V");
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitInsn(RETURN);
         mv.visitMaxs(1, 1);
         mv.visitEnd();
         return cw;
     }
 
     private ClassWriter createBlockCtor(String namePath) throws Exception {
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         cw.visit(V1_4, ACC_PUBLIC + ACC_SUPER, namePath, null, p(Object.class),
                 new String[] { p(CompiledBlockCallback.class) });
         cw.visitField(ACC_PRIVATE | ACC_FINAL, "$scriptObject", ci(Object.class), null, null);
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", sig(Void.TYPE, params(Object.class)), null, null);
         mv.visitCode();
         mv.visitVarInsn(ALOAD, 0);
         mv.visitMethodInsn(INVOKESPECIAL, p(Object.class), "<init>", "()V");
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitVarInsn(ALOAD, 0);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitFieldInsn(PUTFIELD, namePath, "$scriptObject", ci(Object.class));
         mv.visitInsn(RETURN);
         mv.visitMaxs(1, 1);
         mv.visitEnd();
         return cw;
     }
 
     private Class tryClass(String name) {
         try {
             return classLoader.loadClass(name);
         } catch (Exception e) {
             return null;
         }
     }
 
     private MethodVisitor startCall(ClassWriter cw) {
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "call", CALL_SIG, null, null);
         
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitTypeInsn(CHECKCAST, typePath);
         return mv;
     }
 
     private MethodVisitor startCallS(ClassWriter cw) {
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "call", CALL_SIG, null, null);
         
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitVarInsn(ALOAD, 1);
         checkCast(mv, IRubyObject.class);
         return mv;
     }
 
     private MethodVisitor startCallFast(ClassWriter cw) {
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "call", FAST_CALL_SIG, null, null);
         
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitTypeInsn(CHECKCAST, typePath);
         return mv;
     }
 
     private MethodVisitor startDispatcher(ClassWriter cw) {
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "callMethod", sig(IRubyObject.class, params(ThreadContext.class, IRubyObject.class, RubyClass.class, Integer.TYPE, String.class,
                 IRubyObject[].class, CallType.class, Block.class)), null, null);
         
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitVarInsn(ALOAD, 2);
         mv.visitTypeInsn(CHECKCAST, typePath);
         return mv;
     }
 
     private MethodVisitor startCallSFast(ClassWriter cw) {
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "call", FAST_CALL_SIG, null, null);
         
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitTypeInsn(CHECKCAST, IRUB);
         return mv;
     }
 
     private MethodVisitor startBlockCall(ClassWriter cw) {
         MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "call", BLOCK_CALL_SIG, null, null);
         
         mv.visitCode();
         Label line = new Label();
         mv.visitLineNumber(0, line);
         return mv;
     }
 
     protected Class endCall(ClassWriter cw, MethodVisitor mv, String name) {
         mv.visitEnd();
         cw.visitEnd();
         byte[] code = cw.toByteArray();
         return classLoader.defineClass(name, code, protectionDomain);
     }
 
     public Callback getMethod(String method) {
-        String mname = type.getName() + "Invoker$" + method + "_0";
-        String mnamePath = typePath + "Invoker$" + method + "_0";
+        String mname = type.getName() + "Callback$" + method + "_0";
+        String mnamePath = typePath + "Callback$" + method + "_0";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     Class[] signature = new Class[] { Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCall(cw);
                     
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(1, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.noArguments());
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getMethod(String method, Class arg1) {
-        String mname = type.getName() + "Invoker$" + method + "_1";
-        String mnamePath = typePath + "Invoker$" + method + "_1";
+        String mname = type.getName() + "Callback$" + method + "_1";
+        String mnamePath = typePath + "Callback$" + method + "_1";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] {arg1};
                 if (c == null) {
                     Class[] signature = new Class[] { arg1, Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCall(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 1, descriptor);
 
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(3, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.singleArgument());
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getMethod(String method, Class arg1, Class arg2) {
-        String mname = type.getName() + "Invoker$" + method + "_2";
-        String mnamePath = typePath + "Invoker$" + method + "_2";
+        String mname = type.getName() + "Callback$" + method + "_2";
+        String mnamePath = typePath + "Callback$" + method + "_2";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] { arg1, arg2 };
                 if (c == null) {
                     Class[] signature = new Class[] { arg1, arg2, Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCall(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 2, descriptor);
 
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(4, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.twoArguments());
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
     
     public Callback getMethod(String method, Class arg1, Class arg2, Class arg3) {
-        String mname = type.getName() + "Invoker$" + method + "_3";
-        String mnamePath = typePath + "Invoker$" + method + "_3";
+        String mname = type.getName() + "Callback$" + method + "_3";
+        String mnamePath = typePath + "Callback$" + method + "_3";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] { arg1, arg2, arg3 }; 
                 if (c == null) {
                     Class[] signature = new Class[] { arg1, arg2, arg3, Block.class }; 
                     Class ret = getReturnClass(method,
                             descriptor);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCall(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 3, descriptor);
                     
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(5, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.fixed(3));
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getSingletonMethod(String method) {
-        String mname = type.getName() + "Invoker$" + method + "S0";
-        String mnamePath = typePath + "Invoker$" + method + "S0";
+        String mname = type.getName() + "Callback$" + method + "S0";
+        String mnamePath = typePath + "Callback$" + method + "S0";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCallS(cw);
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(1, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.noArguments());
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getSingletonMethod(String method, Class arg1) {
-        String mname = type.getName() + "Invoker$" + method + "_S1";
-        String mnamePath = typePath + "Invoker$" + method + "_S1";
+        String mname = type.getName() + "Callback$" + method + "_S1";
+        String mnamePath = typePath + "Callback$" + method + "_S1";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] {arg1};
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, arg1, Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCallS(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 1, descriptor);
 
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(3, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.singleArgument());
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getSingletonMethod(String method, Class arg1, Class arg2) {
-        String mname = type.getName() + "Invoker$" + method + "_S2";
-        String mnamePath = typePath + "Invoker$" + method + "_S2";
+        String mname = type.getName() + "Callback$" + method + "_S2";
+        String mnamePath = typePath + "Callback$" + method + "_S2";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] {arg1, arg2};
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, arg1, arg2, Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCallS(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 2, descriptor);
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(4, 4);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.twoArguments());
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getSingletonMethod(String method, Class arg1, Class arg2, Class arg3) {
-        String mname = type.getName() + "Invoker$" + method + "_S3";
-        String mnamePath = typePath + "Invoker$" + method + "_S3";
+        String mname = type.getName() + "Callback$" + method + "_S3";
+        String mnamePath = typePath + "Callback$" + method + "_S3";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] {arg1, arg2, arg3};
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, arg1, arg2, arg3, Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCallS(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 3, descriptor);
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(5, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.fixed(3));
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getBlockMethod(String method) {
         // TODO: This is probably BAD...
         return new ReflectionCallback(type, method, new Class[] { RubyKernel.IRUBY_OBJECT,
                 RubyKernel.IRUBY_OBJECT }, false, true, Arity.fixed(2), false);
     }
 
     public CompiledBlockCallback getBlockCallback(String method, Object scriptObject) {
         Class typeClass = scriptObject.getClass();
         String typePathString = p(typeClass);
-        String mname = typeClass.getName() + "Block" + method + "xx1";
-        String mnamePath = typePathString + "Block" + method + "xx1";
+        String mname = typeClass.getName() + "BlockCallback$" + method + "xx1";
+        String mnamePath = typePathString + "BlockCallback$" + method + "xx1";
         synchronized (classLoader) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     ClassWriter cw = createBlockCtor(mnamePath);
                     MethodVisitor mv = startBlockCall(cw);
                     mv.visitVarInsn(ALOAD, 0);
                     mv.visitFieldInsn(GETFIELD, mnamePath, "$scriptObject", ci(Object.class));
                     mv.visitTypeInsn(CHECKCAST, p(typeClass));
                     mv.visitVarInsn(ALOAD, 1);
                     mv.visitVarInsn(ALOAD, 2);
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePathString, method, sig(
                             RubyKernel.IRUBY_OBJECT, params(ThreadContext.class,
                                     RubyKernel.IRUBY_OBJECT, IRubyObject[].class)));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(2, 3);
                     c = endCall(cw, mv, mname);
                 }
                 CompiledBlockCallback ic = (CompiledBlockCallback) c.getConstructor(Object.class).newInstance(scriptObject);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 e.printStackTrace();
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getOptSingletonMethod(String method) {
-        String mname = type.getName() + "Invoker$" + method + "_Sopt";
-        String mnamePath = typePath + "Invoker$" + method + "_Sopt";
+        String mname = type.getName() + "Callback$" + method + "_Sopt";
+        String mnamePath = typePath + "Callback$" + method + "_Sopt";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, IRubyObject[].class, Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCallS(cw);
                     
                     mv.visitVarInsn(ALOAD, METHOD_ARGS_INDEX);
                     checkCast(mv, IRubyObject[].class);
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(2, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.optional());
                 ic.setArgumentTypes(InvocationCallback.OPTIONAL_ARGS);
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getOptMethod(String method) {
-        String mname = type.getName() + "Invoker$" + method + "_opt";
-        String mnamePath = typePath + "Invoker$" + method + "_opt";
+        String mname = type.getName() + "Callback$" + method + "_opt";
+        String mnamePath = typePath + "Callback$" + method + "_opt";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     Class[] signature = new Class[] { IRubyObject[].class, Block.class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtor(mnamePath);
                     MethodVisitor mv = startCall(cw);
                     
                     mv.visitVarInsn(ALOAD, METHOD_ARGS_INDEX);
                     checkCast(mv, IRubyObject[].class);
                     mv.visitVarInsn(ALOAD, 3);
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(2, 3);
                     c = endCall(cw, mv, mname);
                 }
                 InvocationCallback ic = (InvocationCallback) c.newInstance();
                 ic.setArity(Arity.optional());
                 ic.setArgumentTypes(InvocationCallback.OPTIONAL_ARGS);
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastMethod(String method) {
-        String mname = type.getName() + "Invoker$" + method + "_F0";
-        String mnamePath = typePath + "Invoker$" + method + "_F0";
+        String mname = type.getName() + "Callback$" + method + "_F0";
+        String mnamePath = typePath + "Callback$" + method + "_F0";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     Class ret = getReturnClass(method, new Class[0]);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallFast(cw);
 
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(1, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.noArguments());
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastMethod(String method, Class arg1) {
-        String mname = type.getName() + "Invoker$" + method + "_F1";
-        String mnamePath = typePath + "Invoker$" + method + "_F1";
+        String mname = type.getName() + "Callback$" + method + "_F1";
+        String mnamePath = typePath + "Callback$" + method + "_F1";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] { arg1 };
                 if (c == null) {
                     Class[] signature = descriptor;
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallFast(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 1, descriptor);
 
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(3, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.singleArgument());
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastMethod(String method, Class arg1, Class arg2) {
-        String mname = type.getName() + "Invoker$" + method + "_F2";
-        String mnamePath = typePath + "Invoker$" + method + "_F2";
+        String mname = type.getName() + "Callback$" + method + "_F2";
+        String mnamePath = typePath + "Callback$" + method + "_F2";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] { arg1, arg2 };
                 if (c == null) {
                     Class[] signature = descriptor;
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallFast(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 2, descriptor);
 
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(4, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.twoArguments());
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastMethod(String method, Class arg1, Class arg2, Class arg3) {
-        String mname = type.getName() + "Invoker$" + method + "_F3";
-        String mnamePath = typePath + "Invoker$" + method + "_F3";
+        String mname = type.getName() + "Callback$" + method + "_F3";
+        String mnamePath = typePath + "Callback$" + method + "_F3";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] { arg1, arg2, arg3 };
                 if (c == null) {
                     Class[] signature = descriptor;
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallFast(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 3, descriptor);
 
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(5, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.fixed(3));
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastSingletonMethod(String method) {
-        String mname = type.getName() + "Invoker$" + method + "_FS0";
-        String mnamePath = typePath + "Invoker$" + method + "_FS0";
+        String mname = type.getName() + "Callback$" + method + "_FS0";
+        String mnamePath = typePath + "Callback$" + method + "_FS0";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallSFast(cw);
 
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(1, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.noArguments());
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastSingletonMethod(String method, Class arg1) {
-        String mname = type.getName() + "Invoker$" + method + "_FS1";
-        String mnamePath = typePath + "Invoker$" + method + "_FS1";
+        String mname = type.getName() + "Callback$" + method + "_FS1";
+        String mnamePath = typePath + "Callback$" + method + "_FS1";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] {arg1};
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, arg1 };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallSFast(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 1, descriptor);
 
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(3, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.singleArgument());
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastSingletonMethod(String method, Class arg1, Class arg2) {
-        String mname = type.getName() + "Invoker$" + method + "_FS2";
-        String mnamePath = typePath + "Invoker$" + method + "_FS2";
+        String mname = type.getName() + "Callback$" + method + "_FS2";
+        String mnamePath = typePath + "Callback$" + method + "_FS2";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] {arg1, arg2};
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, arg1, arg2 };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallSFast(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 2, descriptor);
 
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(4, 4);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.twoArguments());
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastSingletonMethod(String method, Class arg1, Class arg2, Class arg3) {
-        String mname = type.getName() + "Invoker$" + method + "_FS3";
-        String mnamePath = typePath + "Invoker$" + method + "_FS3";
+        String mname = type.getName() + "Callback$" + method + "_FS3";
+        String mnamePath = typePath + "Callback$" + method + "_FS3";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 Class[] descriptor = new Class[] {arg1, arg2, arg3};
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, arg1, arg2, arg3 };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallSFast(cw);
                     
                     loadArguments(mv, METHOD_ARGS_INDEX, 3, descriptor);
 
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(5, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.fixed(3));
                 ic.setArgumentTypes(descriptor);
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastOptMethod(String method) {
-        String mname = type.getName() + "Invoker$" + method + "_Fopt";
-        String mnamePath = typePath + "Invoker$" + method + "_Fopt";
+        String mname = type.getName() + "Callback$" + method + "_Fopt";
+        String mnamePath = typePath + "Callback$" + method + "_Fopt";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     Class[] signature = new Class[] { IRubyObject[].class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallFast(cw);
                     
                     mv.visitVarInsn(ALOAD, METHOD_ARGS_INDEX);
                     checkCast(mv, IRubyObject[].class);
                     mv.visitMethodInsn(INVOKEVIRTUAL, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(2, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.optional());
                 ic.setArgumentTypes(InvocationCallback.OPTIONAL_ARGS);
                 ic.setJavaName(method);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
 
     public Callback getFastOptSingletonMethod(String method) {
-        String mname = type.getName() + "Invoker$" + method + "_FSopt";
-        String mnamePath = typePath + "Invoker$" + method + "_FSopt";
+        String mname = type.getName() + "Callback$" + method + "_FSopt";
+        String mnamePath = typePath + "Callback$" + method + "_FSopt";
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(mname);
             try {
                 if (c == null) {
                     Class[] signature = new Class[] { RubyKernel.IRUBY_OBJECT, IRubyObject[].class };
                     Class ret = getReturnClass(method, signature);
                     ClassWriter cw = createCtorFast(mnamePath);
                     MethodVisitor mv = startCallSFast(cw);
                     
                     mv.visitVarInsn(ALOAD, METHOD_ARGS_INDEX);
                     checkCast(mv, IRubyObject[].class);
                     mv.visitMethodInsn(INVOKESTATIC, typePath, method, sig(ret, signature));
                     mv.visitInsn(ARETURN);
                     mv.visitMaxs(2, 3);
                     c = endCall(cw, mv, mname);
                 }
                 FastInvocationCallback ic = (FastInvocationCallback) c.newInstance();
                 ic.setArity(Arity.optional());
                 ic.setArgumentTypes(InvocationCallback.OPTIONAL_ARGS);
                 ic.setJavaName(method);
                 ic.setSingleton(true);
                 return ic;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
     
     public Dispatcher createDispatcher(RubyClass metaClass) {
-        String className = type.getName() + "Dispatcher_for_" + metaClass.getBaseName();
-        String classPath = typePath + "Dispatcher_for_" + metaClass.getBaseName();
+        String className = type.getName() + "Dispatcher$" + metaClass.getBaseName();
+        String classPath = typePath + "Dispatcher$" + metaClass.getBaseName();
         
         synchronized (runtime.getJRubyClassLoader()) {
             Class c = tryClass(className);
             try {
                 if (c == null) {
                     // build a map of all methods from the module and all its parents
                     Map allMethods = new HashMap();
                     RubyModule current = metaClass;
                     
                     while (current != null) {
                         for (Iterator methodIter = current.getMethods().entrySet().iterator(); methodIter.hasNext();) {
                             Map.Entry entry = (Map.Entry)methodIter.next();
                             
                             if (allMethods.containsKey(entry.getKey())) continue;
                             
                             DynamicMethod dynamicMethod = (DynamicMethod)entry.getValue();
                             if (!(dynamicMethod instanceof JavaMethod)) {
                                 // method is not a simple/fast method, don't add to our big switch
                                 // FIXME: eventually, we'll probably want to add it to the switch for fast non-hash dispatching
                                 continue;
                             } else {
                                 // TODO: skip singleton methods for now; we'll figure out fast dispatching for them in a future patch
                                 JavaMethod javaMethod = (JavaMethod)dynamicMethod;
                                         
                                 // singleton methods require doing a static invocation, etc...disabling again for now
                                 if (javaMethod.isSingleton() || javaMethod.getCallConfig() != CallConfiguration.NO_FRAME_NO_SCOPE) continue;
                                 
                                 // skipping non-public methods for now, to avoid visibility checks in STI
                                 if (dynamicMethod.getVisibility() != Visibility.PUBLIC) continue;
                             }
                             
                             allMethods.put(entry.getKey(), entry.getValue());
                         }
                         current = current.getSuperClass();
                         while (current != null && current.isIncluded()) current = current.getSuperClass();
                     }
                     
                     // switches are 1-based, so add one
                     Label[] labels = new Label[allMethods.size()];
                     Label defaultLabel = new Label();
 
                     int switchValue = 0;
                     Map switchMap = new HashMap();
                     
                     // NOTE: We sort the list of keys here to ensure they're encountered in the same order from run to run
                     // This will aid AOT compilation, since a given revision of JRuby should always generate the same
                     // sequence of method indices, and code compiled for that revision should continue to work.
                     // FIXME: This will not aid compiling once and running across JRuby versions, since method indices
                     // could be generated in a different order on a different revision (adds, removes, etc over time)
                     List methodKeys = new ArrayList(allMethods.keySet());
                     Collections.sort(methodKeys);
                     for (Iterator methodIter = methodKeys.iterator(); methodIter.hasNext();) {
                         String indexKey = (String)methodIter.next();
                         switchValue++;
                         
                         switchMap.put(new Integer(switchValue), indexKey);
                         // switches are one-based, so subtract one
                         labels[switchValue - 1] = new Label();
                     }
 
                     ClassWriter cw = createCtorDispatcher(classPath, switchMap);
                     SkinnyMethodAdapter mv = new SkinnyMethodAdapter(startDispatcher(cw));
                     
                     // store runtime
                     mv.aload(DISPATCHER_THREADCONTEXT_INDEX);
                     mv.invokevirtual(p(ThreadContext.class), "getRuntime", sig(Ruby.class));
                     mv.astore(DISPATCHER_RUNTIME_INDEX);
                     
                     Label tryBegin = new Label();
                     Label tryEnd = new Label();
                     Label tryCatch = new Label();
                     mv.trycatch(tryBegin, tryEnd, tryCatch, p(StackOverflowError.class));
                     mv.label(tryBegin);
                     
                     // invoke directly
 
                     // receiver is already loaded by startDispatcher
                     
                     // check if tracing is on
                     mv.aload(DISPATCHER_RUNTIME_INDEX);
                     mv.invokevirtual(p(Ruby.class), "hasEventHooks", sig(boolean.class));
                     mv.ifne(defaultLabel);
                     
                     // if no switch values, go straight to default
                     if (switchValue == 0) {
                         mv.go_to(defaultLabel);
                     } else {
                         // load switch value
                         mv.aload(0);
                         mv.getfield(p(Dispatcher.class), "switchTable", ci(byte[].class));
                         
                         // ensure size isn't too large
                         mv.dup();
                         mv.arraylength();
                         mv.iload(DISPATCHER_METHOD_INDEX);
                         Label ok = new Label();
                         mv.if_icmpgt(ok);
                         
                         // size is too large, remove extra table and go to default
                         mv.pop();
                         mv.go_to(defaultLabel);
                         
                         // size is ok, retrieve from table and switch on the result
                         mv.label(ok);
                         mv.iload(DISPATCHER_METHOD_INDEX);
                         mv.barrayload();
                         
                         // perform switch
                         mv.tableswitch(1, switchValue, defaultLabel, labels);
                         
                         for (int i = 0; i < labels.length; i++) {
                             String rubyName = (String)switchMap.get(new Integer(i + 1));
                             DynamicMethod dynamicMethod = (DynamicMethod)allMethods.get(rubyName);
                             
                             mv.label(labels[i]);
     
                             // based on the check above, it's a fast method, we can fast dispatch
                             JavaMethod javaMethod = (JavaMethod)dynamicMethod;
                             String method = javaMethod.getJavaName();
                             Arity arity = javaMethod.getArity();
                             Class[] descriptor = javaMethod.getArgumentTypes();
                             
                             // arity check
                             checkArity(mv, arity);
                             
                             // if singleton load self/recv
                             if (javaMethod.isSingleton()) {
                                 mv.aload(DISPATCHER_SELF_INDEX);
                             }
                             
                             boolean contextProvided = descriptor.length > 0 && descriptor[0] == ThreadContext.class;
 
                             if (contextProvided) {
                                 mv.aload(DISPATCHER_THREADCONTEXT_INDEX);
                             }
 
                             int argCount = arity.getValue();
                             switch (argCount) {
                             case 3: case 2: case 1:
                                 loadArguments(mv, DISPATCHER_ARGS_INDEX, argCount, descriptor, contextProvided);
                                 break;
                             case 0:
                                 break;
                             default: // this should catch all opt/rest cases
                                 mv.aload(DISPATCHER_ARGS_INDEX);
                                 checkCast(mv, IRubyObject[].class);
                                 break;
                             }
                             
                             Class ret = getReturnClass(method, descriptor);
                             String callSig = sig(ret, descriptor);
 //                            if (method.equals("op_equal")) System.out.println("NAME: " + type.getName() + "METHOD: " + sig(ret,descriptor) + ", ARITY: " + arity.getValue());
                             // if block, pass it
                             if (descriptor.length > 0 && descriptor[descriptor.length - 1] == Block.class) {
                                 mv.aload(DISPATCHER_BLOCK_INDEX);
                             }
                             
                             mv.invokevirtual(typePath, method, callSig);
                             mv.areturn();
                         }
                     }
                     
                     // done with cases, handle default case by getting method object and invoking it
                     mv.label(defaultLabel);
                     Label afterCall = new Label();
                     
                     dispatchWithoutSTI(mv, afterCall);
 
                     mv.label(tryEnd);
                     mv.go_to(afterCall);
 
                     mv.label(tryCatch);
                     mv.aload(DISPATCHER_RUNTIME_INDEX);
                     mv.ldc("stack level too deep");
                     mv.invokevirtual(p(Ruby.class), "newSystemStackError", sig(RaiseException.class, params(String.class)));
                     mv.athrow();
 
                     // calls done, return
                     mv.label(afterCall);
                     
                     mv.areturn();
                     mv.visitMaxs(1, 1);
                     c = endCall(cw, mv, className);
                 }
                 Dispatcher dispatcher = (Dispatcher)c.getConstructor(new Class[] {Ruby.class}).newInstance(new Object[] {runtime});
                 return dispatcher;
             } catch (IllegalArgumentException e) {
                 throw e;
             } catch (Exception e) {
                 e.printStackTrace();
                 throw new IllegalArgumentException(e.getMessage());
             }
         }
     }
     
     private void dispatchWithoutSTI(SkinnyMethodAdapter mv, Label afterCall) {
         // retrieve method
         mv.aload(DISPATCHER_RUBYMODULE_INDEX); // module
         mv.aload(DISPATCHER_NAME_INDEX); // name
         mv.invokevirtual(p(RubyModule.class), "searchMethod", sig(DynamicMethod.class, params(String.class)));
 
         Label okCall = new Label();
         
         callMethodMissingIfNecessary(mv, afterCall, okCall);
 
         // call is ok, punch it!
         mv.label(okCall);
 
         // method object already present, push various args
         mv.aload(DISPATCHER_THREADCONTEXT_INDEX); // tc
         mv.aload(DISPATCHER_SELF_INDEX); // self
         mv.aload(DISPATCHER_RUBYMODULE_INDEX); // klazz
         mv.aload(DISPATCHER_NAME_INDEX); // name
         mv.aload(DISPATCHER_ARGS_INDEX);
         mv.aload(DISPATCHER_BLOCK_INDEX);
         mv.invokevirtual(p(DynamicMethod.class), "call",
                 sig(IRubyObject.class, 
                 params(ThreadContext.class, IRubyObject.class, RubyModule.class, String.class, IRubyObject[].class, Block.class)));
     }
     
     public void callMethodMissingIfNecessary(SkinnyMethodAdapter mv, Label afterCall, Label okCall) {
         Label methodMissing = new Label();
 
         // if undefined, branch to method_missing
         mv.dup();
         mv.invokevirtual(p(DynamicMethod.class), "isUndefined", sig(boolean.class));
         mv.ifne(methodMissing);
 
         // if we're not attempting to invoke method_missing and method is not visible, branch to method_missing
         mv.aload(DISPATCHER_NAME_INDEX);
         mv.ldc("method_missing");
         // if it's method_missing, just invoke it
         mv.invokevirtual(p(String.class), "equals", sig(boolean.class, params(Object.class)));
         mv.ifne(okCall);
         // check visibility
         mv.dup(); // dup method
         mv.aload(DISPATCHER_THREADCONTEXT_INDEX);
         mv.invokevirtual(p(ThreadContext.class), "getFrameSelf", sig(IRubyObject.class));
         mv.aload(DISPATCHER_CALLTYPE_INDEX);
         mv.invokevirtual(p(DynamicMethod.class), "isCallableFrom", sig(boolean.class, params(IRubyObject.class, CallType.class)));
         mv.ifne(okCall);
 
         // invoke callMethodMissing
         mv.label(methodMissing);
 
         mv.aload(DISPATCHER_THREADCONTEXT_INDEX); // tc
         mv.swap(); // under method
         mv.aload(DISPATCHER_SELF_INDEX); // self
         mv.swap(); // under method
         mv.aload(DISPATCHER_NAME_INDEX); // name
         mv.aload(DISPATCHER_ARGS_INDEX); // args
 
         // caller
         mv.aload(DISPATCHER_THREADCONTEXT_INDEX);
         mv.invokevirtual(p(ThreadContext.class), "getFrameSelf", sig(IRubyObject.class));
 
         mv.aload(DISPATCHER_CALLTYPE_INDEX); // calltype
         mv.aload(DISPATCHER_BLOCK_INDEX); // block
 
         // invoke callMethodMissing method directly
         // TODO: this could be further optimized, since some DSLs hit method_missing pretty hard...
         mv.invokestatic(p(RuntimeHelpers.class), "callMethodMissing", sig(IRubyObject.class, 
                 params(ThreadContext.class, IRubyObject.class, DynamicMethod.class, String.class, 
                                     IRubyObject[].class, IRubyObject.class, CallType.class, Block.class)));
         // if no exception raised, jump to end to leave result on stack for return
         mv.go_to(afterCall);
     }
     
     private void loadArguments(MethodVisitor mv, int argsIndex, int count, Class[] types) {
         loadArguments(mv, argsIndex, count, types, false);
     }
     
     private void loadArguments(MethodVisitor mv, int argsIndex, int count, Class[] types, boolean contextProvided) {
         for (int i = 0; i < count; i++) {
             loadArgument(mv, argsIndex, i, types[i + (contextProvided ? 1 : 0)]);
         }
     }
     
     private void loadArgument(MethodVisitor mv, int argsIndex, int argIndex, Class type1) {
         mv.visitVarInsn(ALOAD, argsIndex);
         mv.visitLdcInsn(new Integer(argIndex));
         mv.visitInsn(AALOAD);
         checkCast(mv, type1);
     }
 
     private void checkCast(MethodVisitor mv, Class clazz) {
         mv.visitTypeInsn(CHECKCAST, p(clazz));
     }
 
     private void checkArity(SkinnyMethodAdapter mv, Arity arity) {
         if (arity.getValue() >= 0) {
             Label arityOk = new Label();
             // check arity
             mv.aload(6);
             mv.arraylength();
             
             // load arity for check
             switch (arity.getValue()) {
             case 3: mv.iconst_3(); break;
             case 2: mv.iconst_2(); break;
             case 1: mv.iconst_1(); break;
             case 0: mv.iconst_0(); break;
             default: mv.ldc(new Integer(arity.getValue()));
             }
    
             mv.if_icmpeq(arityOk);
             
             // throw
             mv.aload(9);
             mv.aload(6);
             mv.arraylength();
 
             // load arity for error
             switch (arity.getValue()) {
             case 3: mv.iconst_3(); break;
             case 2: mv.iconst_2(); break;
             case 1: mv.iconst_1(); break;
             case 0: mv.iconst_0(); break;
             default: mv.ldc(new Integer(arity.getValue()));
             }
 
             mv.invokevirtual(p(Ruby.class), "newArgumentError", sig(RaiseException.class, params(int.class, int.class)));
             mv.athrow();
 
             // arity ok, continue
             mv.label(arityOk);
         }
     }
 } //InvocationCallbackFactory
