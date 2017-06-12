diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index fd6025a29b..578805b80c 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1005,1428 +1005,1427 @@ public class RubyArray extends RubyObject implements List {
         realLength++;
         values[0] = item;
 
         return this;
     }
 
     /** rb_ary_unshift_m
      *
      */
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
     public RubyBoolean include_p(IRubyObject item) {
         return getRuntime().newBoolean(includes(item));
     }
 
     /** rb_ary_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen() || (flags & TMPLOCK_ARR_F) != 0);
     }
 
     /** rb_ary_aref
      */
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
 
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         return null;
         }
 
     /** rb_ary_aset
      *
      */
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
     public IRubyObject at(IRubyObject pos) {
         return entry(RubyNumeric.num2long(pos));
     }
 
 	/** rb_ary_concat
      *
      */
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
     public IRubyObject first(IRubyObject[] args) {
     	if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin];
         } 
             
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         long n = RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
     	}
     	
         return makeShared(begin, (int) n, getRuntime().getArray(), true);
     }
 
     /** rb_ary_last
      *
      */
     public IRubyObject last(IRubyObject[] args) {
         if (args.length == 0) {
             if (realLength == 0) return getRuntime().getNil();
 
             return values[begin + realLength - 1];
         } 
             
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         long n = RubyNumeric.num2long(args[0]);
         if (n > realLength) {
             n = realLength;
         } else if (n < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         return makeShared(begin + realLength - (int) n, (int) n, getRuntime().getArray(), true);
     }
 
     /** rb_ary_each
      *
      */
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         if ((flags & SHARED_ARR_F) != 0) {
             for (int i = begin; i < begin + realLength; i++) {
                 block.yield(context, values[i]);
             }
         } else {
             for (int i = 0; i < realLength; i++) {
                 block.yield(context, values[i]);
             }
         }
         return this;
     }
 
     /** rb_ary_each_index
      *
      */
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
         try {
             getRuntime().registerInspecting(this);
             return tmp.join(sep);
         } finally {
             getRuntime().unregisterInspecting(this);
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
     public RubyString join_m(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
         
         return join(sep);
     }
 
     /** rb_ary_to_a
      *
      */
     public RubyArray to_a() {
         if(getMetaClass() != getRuntime().getArray()) {
             RubyArray dup = new RubyArray(getRuntime(), true);
 
             flags |= SHARED_ARR_F;
             dup.flags |= SHARED_ARR_F;
             dup.values = values;
             dup.realLength = realLength; 
             dup.begin = begin;
             
             return dup;
         }        
         return this;
     }
 
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
     public IRubyObject op_equal(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
 
         if (!(obj instanceof RubyArray)) {
             if (!obj.respondsTo("to_ary")) {
                 return getRuntime().getFalse();
             } else {
                 return obj.equalInternal(getRuntime().getCurrentContext(), this);
             }
         }
 
         RubyArray ary = (RubyArray) obj;
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (long i = 0; i < realLength; i++) {
             if (!elt(i).equalInternal(context, ary.elt(i)).isTrue()) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     public RubyBoolean eql_p(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
         if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
 
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             if (!elt(i).eqlInternal(context, ary.elt(i))) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_compact_bang
      *
      */
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
     public IRubyObject compact() {
         RubyArray ary = aryDup();
         ary.compact_bang();
         return ary;
     }
 
     /** rb_ary_empty_p
      *
      */
     public IRubyObject empty_p() {
         return realLength == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_ary_clear
      *
      */
     public IRubyObject rb_clear() {
         modifyCheck();
 
         if((flags & SHARED_ARR_F) != 0){
             alloc(ARRAY_DEFAULT_SIZE);
             flags |= SHARED_ARR_F;
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
 
         long beg = 0, end = 0, len = 0;
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
             beg = begObj.isNil() ? 0 : RubyNumeric.num2long(begObj);
             if (beg < 0) {
                 beg = realLength + beg;
                 if (beg < 0) beg = 0;
             }
             len = (lenObj == null || lenObj.isNil()) ? realLength - beg : RubyNumeric.num2long(lenObj);
             break;
         }
 
         modify();
 
         end = beg + len;
         if (end > realLength) {
             if (end >= values.length) realloc((int) end);
 
             Arrays.fill(values, realLength, (int) end, getRuntime().getNil());
             realLength = (int) end;
         }
 
         if (block.isGiven()) {
             Ruby runtime = getRuntime();
             ThreadContext context = runtime.getCurrentContext();
             for (int i = (int) beg; i < (int) end; i++) {
                 IRubyObject v = block.yield(context, runtime.newFixnum(i));
                 if (i >= realLength) break;
 
                 values[i] = v;
             }
         } else {
             if(len > 0) Arrays.fill(values, (int) beg, (int) (beg + len), item);
         }
         
         return this;
     }
 
     /** rb_ary_index
      *
      */
     public IRubyObject index(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = begin; i < begin + realLength; i++) {
             if (values[i].equalInternal(context, obj).isTrue()) return runtime.newFixnum(i - begin);
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     public IRubyObject rindex(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         int i = realLength;
 
         while (i-- > 0) {
             if (i > realLength) {
                 i = realLength;
                 continue;
             }
             if (values[begin + i].equalInternal(context, obj).isTrue()) {
                 return getRuntime().newFixnum(i);
             }
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_indexes
      * 
      */
     public IRubyObject indexes(IRubyObject[] args) {
         getRuntime().getWarnings().warn("Array#indexes is deprecated; use Array#values_at");
 
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
     public IRubyObject reverse() {
         return aryDup().reverse_bang();
     }
 
     /** rb_ary_collect
      *
      */
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
     public RubyArray select(Block block) {
         Ruby runtime = getRuntime();
         RubyArray result = new RubyArray(runtime, realLength);
 
         ThreadContext context = runtime.getCurrentContext();
         if ((flags & SHARED_ARR_F) != 0) {
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
     public IRubyObject delete(IRubyObject item, Block block) {
         int i2 = 0;
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject e = values[begin + i1];
             if (e.equalInternal(context, item).isTrue()) continue;
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
     public IRubyObject delete_at(IRubyObject obj) {
         return delete_at((int) RubyNumeric.num2long(obj));
     }
 
     /** rb_ary_reject_bang
      * 
      */
     public IRubyObject reject(Block block) {
         RubyArray ary = aryDup();
         ary.reject_bang(block);
         return ary;
     }
 
     /** rb_ary_reject_bang
      *
      */
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
     public IRubyObject delete_if(Block block) {
         reject_bang(block);
         return this;
     }
 
     /** rb_ary_zip
      * 
      */
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
     public IRubyObject slice_bang(IRubyObject[] args) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
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
     public IRubyObject assoc(IRubyObject key) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray && ((RubyArray) v).realLength > 0
                     && ((RubyArray) v).values[0].equalInternal(context, key).isTrue()) {
                 return v;
             }
         }
         return runtime.getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
     public IRubyObject rassoc(IRubyObject value) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray && ((RubyArray) v).realLength > 1
                     && ((RubyArray) v).values[1].equalInternal(context, value).isTrue()) {
                 return v;
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
     public IRubyObject flatten() {
         RubyArray ary = aryDup();
         ary.flatten_bang();
         return ary;
     }
 
     /** rb_ary_nitems
      *
      */
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
-    private final Set makeSet(RubyArray ary2) {
-        final Set set = new HashSet();
+    private final RubyHash makeHash(RubyArray ary2) {
+        RubyHash hash = new RubyHash(getRuntime(), false);
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
-            set.add(values[i]);
+            hash.fastASet(values[i], NEVER);
         }
 
         if (ary2 != null) {
             begin = ary2.begin;            
             for (int i = begin; i < begin + ary2.realLength; i++) {
-                set.add(ary2.values[i]);
+                hash.fastASet(ary2.values[i], NEVER);
             }
         }
-        return set;
+        return hash;
     }
 
     /** rb_ary_uniq_bang
      *
      */
     public IRubyObject uniq_bang() {
-        Set set = makeSet(null);
+        RubyHash hash = makeHash(null);
 
-        if (realLength == set.size()) return getRuntime().getNil();
+        if (realLength == hash.size()) return getRuntime().getNil();
 
         int j = 0;
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
-            if (set.remove(v)) store(j++, v);
+            if (hash.fastDelete(v) != null) store(j++, v);
         }
         realLength = j;
         return this;
     }
 
     /** rb_ary_uniq
      *
      */
     public IRubyObject uniq() {
         RubyArray ary = aryDup();
         ary.uniq_bang();
         return ary;
     }
 
     /** rb_ary_diff
      *
      */
     public IRubyObject op_diff(IRubyObject other) {
-        Set set = other.convertToArray().makeSet(null);
+        RubyHash hash = other.convertToArray().makeHash(null);
         RubyArray ary3 = new RubyArray(getRuntime());
 
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
-            if (set.contains(values[i])) continue;
-
+            if (hash.fastARef(values[i]) != null) continue;
             ary3.append(elt(i - begin));
         }
 
         return ary3;
     }
 
     /** rb_ary_and
      *
      */
     public IRubyObject op_and(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
-        Set set = ary2.makeSet(null);
+        RubyHash hash = ary2.makeHash(null);
         RubyArray ary3 = new RubyArray(getRuntime(), 
                 realLength < ary2.realLength ? realLength : ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
-            if (set.remove(v)) ary3.append(v);
+            if (hash.fastDelete(v) != null) ary3.append(v);
         }
 
         return ary3;
     }
 
     /** rb_ary_or
      *
      */
     public IRubyObject op_or(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
-        Set set = makeSet(ary2);
+        RubyHash set = makeHash(ary2);
 
         RubyArray ary3 = new RubyArray(getRuntime(), realLength + ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
-            if (set.remove(v)) ary3.append(v);
+            if (set.fastDelete(v) != null) ary3.append(v);
         }
         for (int i = 0; i < ary2.realLength; i++) {
             IRubyObject v = ary2.elt(i);
-            if (set.remove(v)) ary3.append(v);
+            if (set.fastDelete(v) != null) ary3.append(v);
         }
         return ary3;
     }
 
     /** rb_ary_sort
      *
      */
     public RubyArray sort(Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang(block);
         return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
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
                 return ((RubyString) o1).cmp((RubyString) o2);
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
 			if (indexOf(iter.next()) == -1) return false;
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
         insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToRuby(getRuntime(), element) });
 	}
 
 	public Object remove(int index) {
         return JavaUtil.convertRubyToJava(delete_at(index), Object.class);
 	}
 
 	public int indexOf(Object element) {
         int begin = this.begin;
         
         if (element == null) {
             for (int i = begin; i < begin + realLength; i++) {
                 if (values[i] == null) return i;
             }
         } else {
             IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
             
             for (int i = begin; i < begin + realLength; i++) {
                 if (convertedElement.equals(values[i])) return i;
             }
         }
         return -1;
     }
 
 	public int lastIndexOf(Object element) {
         int begin = this.begin;
 
         if (element == null) {
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (values[i] == null) return i;
             }
         } else {
             IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
             
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (convertedElement.equals(values[i])) return i;
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
diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index 1ebfe7771a..1086fc1ad1 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,1648 +1,1661 @@
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
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 import java.util.AbstractCollection;
 import java.util.AbstractSet;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.NoSuchElementException;
 import java.util.Set;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /** Implementation of the Hash class.
  *
  * @author  jpetersen
  */
 public class RubyHash extends RubyObject implements Map {
     
     public static RubyClass createHashClass(Ruby runtime) {
         RubyClass hashc = runtime.defineClass("Hash", runtime.getObject(), HASH_ALLOCATOR);
         hashc.index = ClassIndex.HASH;
         hashc.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyHash;
                 }
             };
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyHash.class);
 
         hashc.includeModule(runtime.getModule("Enumerable"));
         hashc.getMetaClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("create"));
 
         hashc.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         hashc.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("rehash", callbackFactory.getFastMethod("rehash"));
 
         hashc.defineFastMethod("to_hash", callbackFactory.getFastMethod("to_hash"));        
         hashc.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         hashc.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));        
         hashc.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
 
         hashc.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("fetch", callbackFactory.getOptMethod("fetch"));
         hashc.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("store", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("default", callbackFactory.getFastOptMethod("default_value_get"));
         hashc.defineFastMethod("default=", callbackFactory.getFastMethod("default_value_set", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("default_proc", callbackFactory.getFastMethod("default_proc"));
         hashc.defineFastMethod("index", callbackFactory.getFastMethod("index", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("indexes", callbackFactory.getFastOptMethod("indices"));
         hashc.defineFastMethod("indices", callbackFactory.getFastOptMethod("indices"));
         hashc.defineFastMethod("size", callbackFactory.getFastMethod("rb_size"));
         hashc.defineFastMethod("length", callbackFactory.getFastMethod("rb_size"));        
         hashc.defineFastMethod("empty?", callbackFactory.getFastMethod("empty_p"));
 
         hashc.defineMethod("each", callbackFactory.getMethod("each"));
         hashc.defineMethod("each_value", callbackFactory.getMethod("each_value"));
         hashc.defineMethod("each_key", callbackFactory.getMethod("each_key"));
         hashc.defineMethod("each_pair", callbackFactory.getMethod("each_pair"));        
         hashc.defineMethod("sort", callbackFactory.getMethod("sort"));
 
         hashc.defineFastMethod("keys", callbackFactory.getFastMethod("keys"));
         hashc.defineFastMethod("values", callbackFactory.getFastMethod("rb_values"));
         hashc.defineFastMethod("values_at", callbackFactory.getFastOptMethod("values_at"));
 
         hashc.defineFastMethod("shift", callbackFactory.getFastMethod("shift"));
         hashc.defineMethod("delete", callbackFactory.getMethod("delete", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("delete_if", callbackFactory.getMethod("delete_if"));
         hashc.defineMethod("select", callbackFactory.getOptMethod("select"));
         hashc.defineMethod("reject", callbackFactory.getMethod("reject"));
         hashc.defineMethod("reject!", callbackFactory.getMethod("reject_bang"));
         hashc.defineFastMethod("clear", callbackFactory.getFastMethod("rb_clear"));
         hashc.defineFastMethod("invert", callbackFactory.getFastMethod("invert"));
         hashc.defineMethod("update", callbackFactory.getMethod("update", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("replace", callbackFactory.getFastMethod("replace", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("merge!", callbackFactory.getMethod("update", RubyKernel.IRUBY_OBJECT));
         hashc.defineMethod("merge", callbackFactory.getMethod("merge", RubyKernel.IRUBY_OBJECT));
 
         hashc.defineFastMethod("include?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("member?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("has_key?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("has_value?", callbackFactory.getFastMethod("has_value", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("key?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         hashc.defineFastMethod("value?", callbackFactory.getFastMethod("has_value", RubyKernel.IRUBY_OBJECT));
         
         hashc.dispatcher = callbackFactory.createDispatcher(hashc);
 
         return hashc;
     }
 
     private final static ObjectAllocator HASH_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyHash(runtime, klass);
         }
     };
 
     public int getNativeTypeIndex() {
         return ClassIndex.HASH;
     }
 
     /** rb_hash_s_create
      * 
      */
     public static IRubyObject create(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyClass klass = (RubyClass) recv;
         RubyHash hash;
 
         if (args.length == 1 && args[0] instanceof RubyHash) {
             RubyHash otherHash = (RubyHash)args[0];
             return new RubyHash(recv.getRuntime(), klass, otherHash.internalCopyTable(), otherHash.size); // hash_alloc0
         }
 
         if ((args.length & 1) != 0) throw recv.getRuntime().newArgumentError("odd number of args for Hash");
 
         hash = (RubyHash)klass.allocate();
         for (int i=0; i < args.length; i+=2) hash.aset(args[i], args[i+1]);
 
         return hash;
     }
 
     /** rb_hash_new
      * 
      */
     public static final RubyHash newHash(Ruby runtime) {
         return new RubyHash(runtime);
     }
 
     /** rb_hash_new
      * 
      */
     public static final RubyHash newHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         assert defaultValue != null;
     
         return new RubyHash(runtime, valueMap, defaultValue);
     }
 
     private RubyHashEntry[] table;
     private int size = 0;
     private int threshold;
 
     private int iterLevel = 0;
 
     private static final int DELETED_HASH_F = 1 << 9;    
     private static final int PROCDEFAULT_HASH_F = 1 << 10;
 
     private IRubyObject ifNone;
 
     private RubyHash(Ruby runtime, RubyClass klass, RubyHashEntry[]newTable, int newSize) {
         super(runtime, klass);
         this.ifNone = runtime.getNil();
         threshold = INITIAL_THRESHOLD;
         table = newTable;
         size = newSize;
     }    
 
     public RubyHash(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
         this.ifNone = runtime.getNil();
         alloc();
     }
 
     public RubyHash(Ruby runtime) {
         this(runtime, runtime.getNil());
     }
 
     public RubyHash(Ruby runtime, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = defaultValue;
         alloc();
     }
+    
+    /*
+     *  Constructor for internal usage (mainly for Array#|, Array#&, Array#- and Array#uniq)
+     *  it doesn't initialize ifNone field 
+     */
+    RubyHash(Ruby runtime, boolean objectSpace) {
+        super(runtime, runtime.getHash(), objectSpace);
+        alloc();
+    }
 
     // TODO should this be deprecated ? (to be efficient, internals should deal with RubyHash directly) 
     public RubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         super(runtime, runtime.getHash());
         this.ifNone = runtime.getNil();
         alloc();
 
         for (Iterator iter = valueMap.entrySet().iterator();iter.hasNext();) {
             Map.Entry e = (Map.Entry)iter.next();
             internalPut((IRubyObject)e.getKey(), (IRubyObject)e.getValue());
     }
     }
 
     private final void alloc() {
         threshold = INITIAL_THRESHOLD;
         table = new RubyHashEntry[MRI_HASH_RESIZE ? MRI_INITIAL_CAPACITY : JAVASOFT_INITIAL_CAPACITY];
-    }    
+    }
 
     /* ============================
      * Here are hash internals 
      * (This could be extracted to a separate class but it's not too large though)
      * ============================
      */    
 
     private static final int MRI_PRIMES[] = {
         8 + 3, 16 + 3, 32 + 5, 64 + 3, 128 + 3, 256 + 27, 512 + 9, 1024 + 9, 2048 + 5, 4096 + 3,
         8192 + 27, 16384 + 43, 32768 + 3, 65536 + 45, 131072 + 29, 262144 + 3, 524288 + 21, 1048576 + 7,
         2097152 + 17, 4194304 + 15, 8388608 + 9, 16777216 + 43, 33554432 + 35, 67108864 + 15,
         134217728 + 29, 268435456 + 3, 536870912 + 11, 1073741824 + 85, 0
     };    
 
     private static final int JAVASOFT_INITIAL_CAPACITY = 8; // 16 ?
     private static final int MRI_INITIAL_CAPACITY = MRI_PRIMES[0];
 
     private static final int INITIAL_THRESHOLD = JAVASOFT_INITIAL_CAPACITY - (JAVASOFT_INITIAL_CAPACITY >> 2);
     private static final int MAXIMUM_CAPACITY = 1 << 30;
 
     static final class RubyHashEntry implements Map.Entry {
         private IRubyObject key; 
         private IRubyObject value; 
         private RubyHashEntry next; 
         private int hash;
 
         RubyHashEntry(int h, IRubyObject k, IRubyObject v, RubyHashEntry e) {
             key = k; value = v; next = e; hash = h;
         }
         public Object getKey() {
             return key;
     }
         public Object getJavaifiedKey(){
             return JavaUtil.convertRubyToJava(key);
         }
         public Object getValue() {
             return value;            
         }
         public Object getJavaifiedValue() {
             return JavaUtil.convertRubyToJava(value);
         }
         public Object setValue(Object value) {            
             IRubyObject oldValue = this.value;
             if (value instanceof IRubyObject) {
                 this.value = (IRubyObject)value; 
         } else {
                 throw new UnsupportedOperationException("directEntrySet() doesn't support setValue for non IRubyObject instance entries, convert them manually or use entrySet() instead");                
                 }
             return oldValue;
         }
         public boolean equals(Object other){
             if(!(other instanceof RubyHashEntry)) return false;
             RubyHashEntry otherEntry = (RubyHashEntry)other;
             if(key == otherEntry.key && key != NEVER && key.eql(otherEntry.key)){
                 if(value == otherEntry.value || value.equals(otherEntry.value)) return true;
             }            
             return false;
         }
         public int hashCode(){
             return key.hashCode() ^ value.hashCode();
         }
     }
 
     private static int JavaSoftHashValue(int h) {
         h ^= (h >>> 20) ^ (h >>> 12);
         return h ^ (h >>> 7) ^ (h >>> 4);
                 }
 
     private static int JavaSoftBucketIndex(final int h, final int length) {
         return h & (length - 1);
         }
 
     private static int MRIHashValue(int h) {
         return h & HASH_SIGN_BIT_MASK;
     }
 
     private static final int HASH_SIGN_BIT_MASK = ~(1 << 31);
     private static int MRIBucketIndex(final int h, final int length) {
         return (h % length);
     }
 
     private final void resize(int newCapacity) {
         final RubyHashEntry[] oldTable = table;
         final RubyHashEntry[] newTable = new RubyHashEntry[newCapacity];
         for (int j = 0; j < oldTable.length; j++) {
             RubyHashEntry entry = oldTable[j];
             oldTable[j] = null;
             while (entry != null) {    
                 RubyHashEntry next = entry.next;
                 int i = bucketIndex(entry.hash, newCapacity);
                 entry.next = newTable[i];
                 newTable[i] = entry;
                 entry = next;
             }
         }
         table = newTable;
     }
 
     private final void JavaSoftCheckResize() {
         if (size > threshold) {
             int oldCapacity = table.length; 
             if (oldCapacity == MAXIMUM_CAPACITY) {
                 threshold = Integer.MAX_VALUE;
                 return;
             }
             int newCapacity = table.length << 1;
             resize(newCapacity);
             threshold = newCapacity - (newCapacity >> 2);
     }
     }
 
     private static final int MIN_CAPA = 8;
     private static final int ST_DEFAULT_MAX_DENSITY = 5;    
     private final void MRICheckResize() {
         if (size / table.length > ST_DEFAULT_MAX_DENSITY) {           
             int forSize = table.length + 1; // size + 1;         
             for (int i=0, newCapacity = MIN_CAPA; i < MRI_PRIMES.length; i++, newCapacity <<= 1) {
                 if (newCapacity > forSize) {                  
                     resize(MRI_PRIMES[i]);                  
                     return;                 
     }
             }
             return; // suboptimal for large hashes (> 1073741824 + 85 entries) not very likely to happen
         }
     }
     // ------------------------------   
     private static boolean MRI_HASH = true; 
     private static boolean MRI_HASH_RESIZE = true;
 
     private static int hashValue(final int h) {
         return MRI_HASH ? MRIHashValue(h) : JavaSoftHashValue(h);
     }
 
     private static int bucketIndex(final int h, final int length) {
         return MRI_HASH ? MRIBucketIndex(h, length) : JavaSoftBucketIndex(h, length); 
     }   
 
     private void checkResize() {
         if (MRI_HASH_RESIZE) MRICheckResize(); else JavaSoftCheckResize();
 	}
     // ------------------------------
     public static long collisions = 0;
 
     private final void internalPut(final IRubyObject key, final IRubyObject value) {
         checkResize();
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
 
         // if (table[i] != null) collisions++;
 
         for (RubyHashEntry entry = table[i]; entry != null; entry = entry.next) {
             IRubyObject k;
             if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
                 entry.value = value;
                 return;
 	}
         }
 
         table[i] = new RubyHashEntry(hash, key, value, table[i]);
         size++;
     }
 
     private final void internalPutDirect(final IRubyObject key, final IRubyObject value){
         checkResize();
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
         table[i] = new RubyHashEntry(hash, key, value, table[i]);
         size++;
 	}
 
     private final IRubyObject internalGet(IRubyObject key) { // specialized for value
         final int hash = hashValue(key.hashCode());
         for (RubyHashEntry entry = table[bucketIndex(hash, table.length)]; entry != null; entry = entry.next) {
             IRubyObject k;
             if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) return entry.value;
 	}
         return null;
     }
 
     private final RubyHashEntry internalGetEntry(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         for (RubyHashEntry entry = table[bucketIndex(hash, table.length)]; entry != null; entry = entry.next) {
             IRubyObject k;
             if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) return entry;
         }
         return null;
     }
 
     private final RubyHashEntry internalDelete(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         final int i = bucketIndex(hash, table.length);
         RubyHashEntry entry = table[i];
 
         if (entry == null) return null;
 
         IRubyObject k;
         if (entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
             table[i] = entry.next;
             size--;
             return entry;
     }
         for (; entry.next != null; entry = entry.next) {
             RubyHashEntry tmp = entry.next;
             if (tmp.hash == hash && ((k = tmp.key) == key || key.eql(k))) {
                 entry.next = entry.next.next;
                 size--;
                 return tmp;
             }
         }
         return null;
     }
 
     private final RubyHashEntry internalDeleteSafe(IRubyObject key) {
         final int hash = hashValue(key.hashCode());
         RubyHashEntry entry = table[bucketIndex(hash, table.length)];
 
         if (entry == null) return null;
         IRubyObject k;
 
         for (; entry != null; entry = entry.next) {           
             if (entry.key != NEVER && entry.hash == hash && ((k = entry.key) == key || key.eql(k))) {
                 entry.key = NEVER; // make it a skip node 
                 size--;             
                 return entry;
     }
         }
         return null;
     }   
 
     private final RubyHashEntry internalDeleteEntry(RubyHashEntry entry) {
         final int hash = hashValue(entry.key.hashCode());
         final int i = bucketIndex(hash, table.length);
         RubyHashEntry prev = table[i];
         RubyHashEntry e = prev;
         while (e != null){
             RubyHashEntry next = e.next;
             if (e.hash == hash && e.equals(entry)) {
                 size--;
                 if(iterLevel > 0){
                     if (prev == e) table[i] = next; else prev.next = next;
                 } else {
                     e.key = NEVER;
                 }
                 return e;
             }
             prev = e;
             e = next;
         }
         return e;
     }
 
     private final void internalCleanupSafe() { // synchronized ?
         for (int i=0; i < table.length; i++) {
             RubyHashEntry entry = table[i];
             while (entry != null && entry.key == NEVER) table[i] = entry = entry.next;
             if (entry != null) {
                 RubyHashEntry prev = entry;
                 entry = entry.next;
                 while (entry != null) {
                     if (entry.key == NEVER) { 
                         prev.next = entry.next;
                     } else {
                         prev = prev.next;
                     }
                     entry = prev.next;
                 }
             }
         }        
     }
 
     private final RubyHashEntry[] internalCopyTable() {
          RubyHashEntry[]newTable = new RubyHashEntry[table.length];
 
          for (int i=0; i < table.length; i++) {
              for (RubyHashEntry entry = table[i]; entry != null; entry = entry.next) {
                  if (entry.key != NEVER) newTable[i] = new RubyHashEntry(entry.hash, entry.key, entry.value, newTable[i]);
              }
          }
          return newTable;
     }
 
     // flags for callback based interation
     public static final int ST_CONTINUE = 0;    
     public static final int ST_STOP = 1;
     public static final int ST_DELETE = 2;
     public static final int ST_CHECK = 3;
 
     private void rehashOccured(){
         throw getRuntime().newRuntimeError("rehash occurred during iteration");
     }
 
     public static abstract class Callback { // a class to prevent invokeinterface
         public abstract int call(RubyHash hash, RubyHashEntry entry);
     }    
 
     private final int hashForEachEntry(final RubyHashEntry entry, final Callback callback) {
         if (entry.key == NEVER) return ST_CONTINUE;
         RubyHashEntry[]ltable = table;
 		
         int status = callback.call(this, entry);
         
         if (ltable != table) rehashOccured();
         
         switch (status) {
         case ST_DELETE:
             internalDeleteSafe(entry.key);
             flags |= DELETED_HASH_F;
         case ST_CONTINUE:
             break;
         case ST_STOP:
             return ST_STOP;
 	}
         return ST_CHECK;
     }    
 
     private final boolean internalForEach(final Callback callback) {
         RubyHashEntry entry, last, tmp;
         int length = table.length;
         for (int i = 0; i < length; i++) {
             last = null;
             for (entry = table[i]; entry != null;) {
                 switch (hashForEachEntry(entry, callback)) {
                 case ST_CHECK:
                     tmp = null;
                     if (i < length) for (tmp = table[i]; tmp != null && tmp != entry; tmp = tmp.next);
                     if (tmp == null) return true;
                 case ST_CONTINUE:
                     last = entry;
                     entry = entry.next;
                     break;
                 case ST_STOP:
                     return false;
                 case ST_DELETE:
                     tmp = entry;
                     if (last == null) table[i] = entry.next; else last.next = entry.next;
                     entry = entry.next;
                     size--;
                 }
             }
         }
         return false;
     }
 
     public final void forEach(final Callback callback) {
         try{
             preIter();
             if (internalForEach(callback)) rehashOccured();
         }finally{
             postIter();
         }
     }
 
     private final void preIter() {
         iterLevel++;
     }
 
     private final void postIter() {
         iterLevel--;
         if ((flags & DELETED_HASH_F) != 0) {
             internalCleanupSafe();
             flags &= ~DELETED_HASH_F;
         }
     }
 
     private final RubyHashEntry checkIter(RubyHashEntry[]ltable, RubyHashEntry node) {
         while (node != null && node.key == NEVER) node = node.next;
         if (ltable != table) rehashOccured();
         return node;
     }      
 
     /* ============================
      * End of hash internals
      * ============================
      */
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_hash_initialize
      * 
      */
     public IRubyObject initialize(IRubyObject[] args, final Block block) {
             modify();
 
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError("wrong number of arguments");
             ifNone = getRuntime().newProc(false, block);
             flags |= PROCDEFAULT_HASH_F;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 0, 1);
             if (args.length == 1) ifNone = args[0];
         }
         return this;
     }
 
     /** rb_hash_default
      * 
      */
     public IRubyObject default_value_get(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
         if ((flags & PROCDEFAULT_HASH_F) != 0) {
             if (args.length == 0) return getRuntime().getNil();
             return ifNone.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[]{this, args[0]});
         }
         return ifNone;
     }
 
     /** rb_hash_set_default
      * 
      */
     public IRubyObject default_value_set(final IRubyObject defaultValue) {
         modify();
 
         ifNone = defaultValue;
         flags &= ~PROCDEFAULT_HASH_F;
 
         return ifNone;
     }
 
     /** rb_hash_default_proc
      * 
      */    
     public IRubyObject default_proc() {
         return (flags & PROCDEFAULT_HASH_F) != 0 ? ifNone : getRuntime().getNil();
     }
 
     /** rb_hash_modify
      *
      */
     public void modify() {
     	testFrozen("hash");
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify hash");
         }
     }
 
     /** inspect_hash
      * 
      */
     public IRubyObject inspectHash() {
         try {
             Ruby runtime = getRuntime();
             final String sep = ", ";
             final String arrow = "=>";
             final StringBuffer sb = new StringBuffer("{");
             boolean firstEntry = true;
         
             ThreadContext context = runtime.getCurrentContext();
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (!firstEntry) sb.append(sep);
                     sb.append(entry.key.callMethod(context, "inspect")).append(arrow);
                     sb.append(entry.value.callMethod(context, "inspect"));
                     firstEntry = false;
                 }
             }
             sb.append("}");
             return runtime.newString(sb.toString());
         } finally {
             postIter();
         }         
     }
 
     /** rb_hash_inspect
      * 
      */
     public IRubyObject inspect() {
         if (size == 0) return getRuntime().newString("{}");
         if (getRuntime().isInspecting(this)) return getRuntime().newString("{...}");
         
         try {
             getRuntime().registerInspecting(this);
             return inspectHash();
         } finally {
             getRuntime().unregisterInspecting(this);
         }        
     }
 
     /** rb_hash_size
      * 
      */    
     public RubyFixnum rb_size() {
         return getRuntime().newFixnum(size);
     }
 
     /** rb_hash_empty_p
      * 
      */
     public RubyBoolean empty_p() {
         return size == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_hash_to_a
      * 
      */
     public RubyArray to_a() {
         Ruby runtime = getRuntime();
         RubyArray result = RubyArray.newArray(runtime, size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {                
                     result.append(RubyArray.newArray(runtime, entry.key, entry.value));
         }
             }
         } finally {postIter();}
 
         result.setTaint(isTaint());
         return result;
     }
 
     /** rb_hash_to_s & to_s_hash
      * 
      */
     public IRubyObject to_s() {
         if (getRuntime().isInspecting(this)) return getRuntime().newString("{...}");
         try {
             getRuntime().registerInspecting(this);
             return to_a().to_s();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_hash_rehash
      * 
      */
     public RubyHash rehash() {
         modify();
         final RubyHashEntry[] oldTable = table;
         final RubyHashEntry[] newTable = new RubyHashEntry[oldTable.length];
         for (int j = 0; j < oldTable.length; j++) {
             RubyHashEntry entry = oldTable[j];
             oldTable[j] = null;
             while (entry != null) {    
                 RubyHashEntry next = entry.next;
                 if (entry.key != NEVER) {
                     entry.hash = entry.key.hashCode(); // update the hash value
                     int i = bucketIndex(entry.hash, newTable.length);
                     entry.next = newTable[i];
                     newTable[i] = entry;
         }
                 entry = next;
             }
         }
         table = newTable;
         return this;
     }
 
     /** rb_hash_to_hash
      * 
      */
     public RubyHash to_hash() {
         return this;        
     }
 
     public RubyHash convertToHash() {    
         return this;
     }
 
     public final void fastASet(IRubyObject key, IRubyObject value) {
         internalPut(key, value);
     }
 
     /** rb_hash_aset
      * 
      */
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         modify();
 
         if (!(key instanceof RubyString)) {
             internalPut(key, value);
             return value;
         } 
 
         RubyHashEntry entry = null;        
         if ((entry = internalGetEntry(key)) != null) {
             entry.value = value;
         } else {
           IRubyObject realKey = ((RubyString)key).strDup();
             realKey.setFrozen(true);
           internalPutDirect(realKey, value);
         }
 
         return value;
     }
 
     public final IRubyObject fastARef(IRubyObject key) { // retuns null when not found to avoid unnecessary getRuntime().getNil() call
         return internalGet(key);
     }
 
     /** rb_hash_aref
      * 
      */
     public IRubyObject aref(IRubyObject key) {        
         IRubyObject value;
         return ((value = internalGet(key)) == null) ? callMethod(getRuntime().getCurrentContext(), MethodIndex.DEFAULT, "default", key) : value;
     }
 
     /** rb_hash_fetch
      * 
      */
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2 && block.isGiven()) {
             getRuntime().getWarnings().warn("block supersedes default value argument");
         }
 
         IRubyObject value;
         if ((value = internalGet(args[0])) == null) {
             if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), args[0]);
             if (args.length == 1) throw getRuntime().newIndexError("key not found");
             return args[1];
         }
         return value;
     }
 
     /** rb_hash_has_key
      * 
      */
     public RubyBoolean has_key(IRubyObject key) {
         return internalGetEntry(key) == null ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     /** rb_hash_has_value
      * 
      */
     public RubyBoolean has_value(IRubyObject value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, value).isTrue()) return runtime.getTrue();
     }
             }
         } finally {postIter();}
         return runtime.getFalse();
     }
 
     /** rb_hash_each
      * 
      */
 	public RubyHash each(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     // rb_assoc_new equivalent
                     block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, false);
 	}
             }
         } finally {postIter();}
 
         return this;
     }
 
     /** rb_hash_each_pair
      * 
      */
 	public RubyHash each_pair(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     // rb_yield_values(2,...) equivalent
                     block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, true);                    
         }
     }
         } finally {postIter();}
 
         return this;	
 	}
 
     /** rb_hash_each_value
      * 
      */
     public RubyHash each_value(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     block.yield(context, entry.value);
 		}
 	}
         } finally {postIter();}
 
         return this;        
 	}
 
     /** rb_hash_each_key
      * 
      */
 	public RubyHash each_key(Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     block.yield(context, entry.key);
 		}
 	}
         } finally {postIter();}
 
         return this;  
 	}
 
     /** rb_hash_sort
      * 
      */
 	public RubyArray sort(Block block) {
 		return to_a().sort_bang(block);
 	}
 
     /** rb_hash_index
      * 
      */
     public IRubyObject index(IRubyObject value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, value).isTrue()) return entry.key;
             }
         }
         } finally {postIter();}
 
         return getRuntime().getNil();        
     }
 
     /** rb_hash_indexes
      * 
      */
     public RubyArray indices(IRubyObject[] indices) {
         RubyArray values = RubyArray.newArray(getRuntime(), indices.length);
 
         for (int i = 0; i < indices.length; i++) {
             values.append(aref(indices[i]));
         }
 
         return values;
     }
 
     /** rb_hash_keys 
      * 
      */
     public RubyArray keys() {
         Ruby runtime = getRuntime();
         RubyArray keys = RubyArray.newArray(runtime, size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     keys.append(entry.key);
     }
             }
         } finally {postIter();}
 
         return keys;          
     }
 
     /** rb_hash_values
      * 
      */
     public RubyArray rb_values() {
         RubyArray values = RubyArray.newArray(getRuntime(), size);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     values.append(entry.value);
     }
             }
         } finally {postIter();}
 
         return values;
     }
 
     /** rb_hash_equal
      * 
      */
 
     private static final boolean EQUAL_CHECK_DEFAULT_VALUE = false; 
 
     public IRubyObject equal(IRubyObject other) {
         if (this == other ) return getRuntime().getTrue();
         if (!(other instanceof RubyHash)) {
             if (!other.respondsTo("to_hash")) return getRuntime().getFalse();
             return other.equalInternal(getRuntime().getCurrentContext(), this);
         }
 
         RubyHash otherHash = (RubyHash)other;
         if (size != otherHash.size) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();        
         ThreadContext context = runtime.getCurrentContext();
 
         if (EQUAL_CHECK_DEFAULT_VALUE) {
             if (!ifNone.equalInternal(context, otherHash.ifNone).isTrue() &&
                (flags & PROCDEFAULT_HASH_F) != (otherHash.flags & PROCDEFAULT_HASH_F)) return runtime.getFalse();
             }
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     IRubyObject value = otherHash.internalGet(entry.key);
                     if (value == null) return runtime.getFalse();
                     if (!entry.value.equalInternal(context, value).isTrue()) return runtime.getFalse();
         }
     }
         } finally {postIter();}        
 
         return runtime.getTrue();
     }
 
     /** rb_hash_shift
      * 
      */
     public IRubyObject shift() {
 		modify();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     RubyArray result = RubyArray.newArray(getRuntime(), entry.key, entry.value);
                     internalDeleteSafe(entry.key);
                     flags |= DELETED_HASH_F;
                     return result;
     }
             }
         } finally {postIter();}          
 
         if ((flags & PROCDEFAULT_HASH_F) != 0) return ifNone.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[]{this, getRuntime().getNil()});
         return ifNone;
     }
+    
+    public final RubyHashEntry fastDelete(IRubyObject key) {
+        return internalDelete(key);
+    }
 
     /** rb_hash_delete
      * 
      */
 	public IRubyObject delete(IRubyObject key, Block block) {
 		modify();
 
         RubyHashEntry entry;
         if (iterLevel > 0) {
             if ((entry = internalDeleteSafe(key)) != null) {
                 flags |= DELETED_HASH_F;
                 return entry.value;                
             }
         } else if ((entry = internalDelete(key)) != null) return entry.value;
 
 		if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), key);
         return getRuntime().getNil();
     }
 
     /** rb_hash_select
      * 
      */
     public IRubyObject select(IRubyObject[] args, Block block) {
         if (args.length > 0) throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 0)");
         RubyArray result = getRuntime().newArray();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {            
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (block.yield(context, runtime.newArray(entry.key, entry.value), null, null, true).isTrue()) {
                         result.append(runtime.newArray(entry.key, entry.value));
                     }
                 }
             }
         } finally {postIter();}
         return result;
     }
 
     /** rb_hash_delete_if
      * 
      */
 	public RubyHash delete_if(Block block) {
         modify();
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         try {            
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {            
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (block.yield(context, RubyArray.newArray(runtime, entry.key, entry.value), null, null, true).isTrue())
                         delete(entry.key, block);
                 }
             }
         } finally {postIter();}        
 
 		return this;
 	}
 
     /** rb_hash_reject
      * 
      */
 	public RubyHash reject(Block block) {
         return ((RubyHash)dup()).delete_if(block);
 	}
 
     /** rb_hash_reject_bang
      * 
      */
 	public IRubyObject reject_bang(Block block) {
         int n = size;
         delete_if(block);
         if (n == size) return getRuntime().getNil();
         return this;
 			}
 
     /** rb_hash_clear
      * 
      */
 	public RubyHash rb_clear() {
 		modify();
 
         if (size > 0) { 
             alloc();
             size = 0;
             flags &= ~DELETED_HASH_F;
 	}
 
 		return this;
 	}
 
     /** rb_hash_invert
      * 
      */
 	public RubyHash invert() {
 		RubyHash result = newHash(getRuntime());
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     result.aset(entry.value, entry.key);
 		}
 	}
         } finally {postIter();}        
 
         return result;        
 	}
 
     /** rb_hash_update
      * 
      */
     public RubyHash update(IRubyObject other, Block block) {
         modify();
 
         RubyHash otherHash = other.convertToHash();
 
         try {
              otherHash.preIter();
              RubyHashEntry[]ltable = otherHash.table;
         if (block.isGiven()) {
                  Ruby runtime = getRuntime();
                  ThreadContext context = runtime.getCurrentContext();
 
                  for (int i = 0; i < ltable.length; i++) {
                      for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                          IRubyObject value;
                          if (internalGet(entry.key) != null)
                              value = block.yield(context, RubyArray.newArrayNoCopy(runtime, new IRubyObject[]{entry.key, aref(entry.key), entry.value}));
                          else
                              value = entry.value;
                          aset(entry.key, value);
                 }
             }
             } else { 
                 for (int i = 0; i < ltable.length; i++) {
                     for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                         aset(entry.key, entry.value);
         }
                 }
             }  
         } finally {otherHash.postIter();}
 
         return this;
     }
 
     /** rb_hash_merge
      * 
      */
     public RubyHash merge(IRubyObject other, Block block) {
         return ((RubyHash)dup()).update(other, block);
     }
 
     /** rb_hash_replace
      * 
      */
     public RubyHash replace(IRubyObject other) {
         RubyHash otherHash = other.convertToHash();
 
         if (this == otherHash) return this;
 
         rb_clear();
 
         try {
             otherHash.preIter();
             RubyHashEntry[]ltable = otherHash.table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = otherHash.checkIter(ltable, entry)) != null; entry = entry.next) {
                     aset(entry.key, entry.value);
                 }
             }
         } finally {otherHash.postIter();}
 
         ifNone = otherHash.ifNone;
 
         if ((otherHash.flags & PROCDEFAULT_HASH_F) != 0) {
             flags |= PROCDEFAULT_HASH_F;
         } else {
             flags &= ~PROCDEFAULT_HASH_F;
         }
 
         return this;
     }
 
     /** rb_hash_values_at
      * 
      */
     public RubyArray values_at(IRubyObject[] args) {
         RubyArray result = RubyArray.newArray(getRuntime(), args.length);
         for (int i = 0; i < args.length; i++) {
             result.append(aref(args[i]));
         }
         return result;
     }
 
     public boolean hasDefaultProc() {
         return (flags & PROCDEFAULT_HASH_F) != 0;
     }
 
     public IRubyObject getIfNone(){
         return ifNone;
     }
 
     // FIXME:  Total hack to get flash in Rails marshalling/unmarshalling in session ok...We need
     // to totally change marshalling to work with overridden core classes.
     public static void marshalTo(RubyHash hash, MarshalStream output) throws IOException {
         output.writeInt(hash.size);
         try {
             hash.preIter();
             RubyHashEntry[]ltable = hash.table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = hash.checkIter(ltable, entry)) != null; entry = entry.next) {
                     output.dumpObject(entry.key);
                     output.dumpObject(entry.value);
         }
         }
         } finally {hash.postIter();}         
 
         if (!hash.ifNone.isNil()) output.dumpObject(hash.ifNone);
     }
 
     public static RubyHash unmarshalFrom(UnmarshalStream input, boolean defaultValue) throws IOException {
         RubyHash result = newHash(input.getRuntime());
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             result.aset(input.unmarshalObject(), input.unmarshalObject());
         }
         if (defaultValue) result.default_value_set(input.unmarshalObject());
         return result;
     }
 
     public Class getJavaClass() {
         return Map.class;
     }
 
     // Satisfy java.util.Set interface (for Java integration)
 
     public int size() {
         return size;
 	}
 
     public boolean isEmpty() {
         return size == 0;
     }    
 
 	public boolean containsKey(Object key) {
 		return internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)) != null;
 	}
 
 	public boolean containsValue(Object value) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 		IRubyObject element = JavaUtil.convertJavaToRuby(runtime, value);
 
         try {
             preIter();
             RubyHashEntry[]ltable = table;
             for (int i = 0; i < ltable.length; i++) {
                 for (RubyHashEntry entry = ltable[i]; entry != null && (entry = checkIter(ltable, entry)) != null; entry = entry.next) {
                     if (entry.value.equalInternal(context, element).isTrue()) return true;
 			}
 		}
         } finally {postIter();}        
 
 		return false;
 	}
 
 	public Object get(Object key) {
 		return JavaUtil.convertRubyToJava(internalGet(JavaUtil.convertJavaToRuby(getRuntime(), key)));
 	}
 
 	public Object put(Object key, Object value) {
 		internalPut(JavaUtil.convertJavaToRuby(getRuntime(), key), JavaUtil.convertJavaToRuby(getRuntime(), value));
         return value;
 	}
 
 	public Object remove(Object key) {
         IRubyObject rubyKey = JavaUtil.convertJavaToRuby(getRuntime(), key);
         RubyHashEntry entry;
         if (iterLevel > 0) {
             entry = internalDeleteSafe(rubyKey);
             flags |= DELETED_HASH_F;
         } else {
             entry = internalDelete(rubyKey);
 	}
 		 
         return entry != null ? entry.value : null;
 	}
 
 	public void putAll(Map map) {
         Ruby runtime = getRuntime();
 		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
 			Object key = iter.next();
 			internalPut(JavaUtil.convertJavaToRuby(runtime, key), JavaUtil.convertJavaToRuby(runtime, map.get(key))); 
 		}
 	}
 
 	public void clear() {
         rb_clear();
 	}
 
     private abstract class RubyHashIterator implements Iterator {
         RubyHashEntry entry, current;
         int index;
         RubyHashEntry[]iterTable;
         Ruby runtime = getRuntime();
         
         public RubyHashIterator(){
             iterTable = table;
             if(size > 0) seekNextValidEntry();
 	}
 
         private final void seekNextValidEntry(){
             do {
                 while (index < iterTable.length && (entry = iterTable[index++]) == null);
                 while (entry != null && entry.key == NEVER) entry = entry.next;
             } while (entry == null && index < iterTable.length);
 	}
 
         public boolean hasNext() {
             return entry != null;
 	}
 
         public final RubyHashEntry nextEntry() {
             if (entry == null) throw new NoSuchElementException();
             RubyHashEntry e = current = entry;
             if ((entry = checkIter(iterTable, entry.next)) == null) seekNextValidEntry(); 
             return e;
         }
 
         public void remove() {
             if (current == null) throw new IllegalStateException();
             internalDeleteSafe(current.key);
             flags |= DELETED_HASH_F;
         }
         
     }
 
     private final class KeyIterator extends RubyHashIterator {
 					public Object next() {
             return JavaUtil.convertRubyToJava(nextEntry().key);
 					}
 			}
 
     private class KeySet extends AbstractSet {
         public Iterator iterator() {
             return new KeyIterator();
         }
 			public int size() {
             return size;
 			}
         public boolean contains(Object o) {
             return containsKey(o);
 			}
         public boolean remove(Object o) {
             return RubyHash.this.remove(o) != null;
 	}
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
 	public Set keySet() {
         return new KeySet();
 					}
 
     private final class DirectKeyIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry().key;
 			}
 	}	
 
     private final class DirectKeySet extends KeySet {
         public Iterator iterator() {
             return new DirectKeyIterator();
         }        
 		}
 
     public Set directKeySet() {
         return new DirectKeySet();
 		}
 
     private final class ValueIterator extends RubyHashIterator {
 		public Object next() {
             return JavaUtil.convertRubyToJava(nextEntry().value);
 		}
 		}		
 
     private class Values extends AbstractCollection {
         public Iterator iterator() {
             return new ValueIterator();
         }
         public int size() {
             return size;
         }
         public boolean contains(Object o) {
             return containsValue(o);
             }
         public void clear() {
             RubyHash.this.clear();
         }
             }
 
     public Collection values() {
         return new Values();
         }
 
     private final class DirectValueIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry().value;
 		}
         }
 
     private final class DirectValues extends Values {
         public Iterator iterator() {
             return new DirectValueIterator();
         }        
     }
 
     public Collection directValues() {
         return new DirectValues();
     }    
 
     static final class ConversionMapEntry implements Map.Entry {
         private final RubyHashEntry entry;
         private final Ruby runtime;
 
         public ConversionMapEntry(Ruby runtime, RubyHashEntry entry) {
             this.entry = entry;
             this.runtime = runtime;
         }
 
 				public Object getKey() {
             return JavaUtil.convertRubyToJava(entry.key, Object.class); 
 				}
 
 				public Object getValue() {
             return JavaUtil.convertRubyToJava(entry.value, Object.class);
 				}
 
         public Object setValue(Object value) {
             return entry.value = JavaUtil.convertJavaToRuby(runtime, value);            
 				}
 
         public boolean equals(Object other){
             if(!(other instanceof RubyHashEntry)) return false;
             RubyHashEntry otherEntry = (RubyHashEntry)other;
             if(entry.key != NEVER && entry.key == otherEntry.key && entry.key.eql(otherEntry.key)){
                 if(entry.value == otherEntry.value || entry.value.equals(otherEntry.value)) return true;
             }            
             return false;
 		}
         public int hashCode(){
             return entry.hashCode();
         }
     }    
 
     private final class EntryIterator extends RubyHashIterator {
         public Object next() {
             return new ConversionMapEntry(runtime, nextEntry());
         }
     }
 
     private final class EntrySet extends AbstractSet {
         public Iterator iterator() {
             return new EntryIterator();
         }
         public boolean contains(Object o) {
             if (!(o instanceof ConversionMapEntry))
                 return false;
             ConversionMapEntry entry = (ConversionMapEntry)o;
             if (entry.entry.key == NEVER) return false;
             RubyHashEntry candidate = internalGetEntry(entry.entry.key);
             return candidate != null && candidate.equals(entry.entry);
         }
         public boolean remove(Object o) {
             if (!(o instanceof ConversionMapEntry)) return false;
             return internalDeleteEntry(((ConversionMapEntry)o).entry) != null;
         }
         public int size() {
             return size;
         }
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
     public Set entrySet() {
         return new EntrySet();
     }    
 
     private final class DirectEntryIterator extends RubyHashIterator {
         public Object next() {
             return nextEntry();
         }
     }    
 
     private final class DirectEntrySet extends AbstractSet {
         public Iterator iterator() {
             return new DirectEntryIterator();
         }
         public boolean contains(Object o) {
             if (!(o instanceof RubyHashEntry))
                 return false;
             RubyHashEntry entry = (RubyHashEntry)o;
             if (entry.key == NEVER) return false;
             RubyHashEntry candidate = internalGetEntry(entry.key);
             return candidate != null && candidate.equals(entry);
     }
         public boolean remove(Object o) {
             if (!(o instanceof RubyHashEntry)) return false;
             return internalDeleteEntry((RubyHashEntry)o) != null;
         }
         public int size() {
             return size;
         }
         public void clear() {
             RubyHash.this.clear();
         }
     }    
 
     /** return an entry set who's entries do not convert their values, faster
      * 
      */
     public Set directEntrySet() {
         return new DirectEntrySet();
     }       
 
     public boolean equals(Object other){
         if (!(other instanceof RubyHash)) return false;
         if (this == other) return true;
         return equal((RubyHash)other).isTrue() ? true : false;
         }
 }
diff --git a/test/test_array.rb b/test/test_array.rb
index b546e13599..66c30b6dde 100644
--- a/test/test_array.rb
+++ b/test/test_array.rb
@@ -1,224 +1,243 @@
 require 'test/unit'
 
 class TestArray < Test::Unit::TestCase
 
   def test_unshift_and_leftshift_op
     arr = ["zero", "first"]
     arr.unshift "second", "third"
     assert_equal(["second", "third", "zero", "first"], arr)
     assert_equal(["first"], arr[-1..-1])
     assert_equal(["first"], arr[3..3])
     assert_equal([], arr[3..2])
     assert_equal([], arr[3..1])
     assert(["third", "zero", "first"] == arr[1..4])
     assert('["third", "zero", "first"]' == arr[1..4].inspect)
   
     arr << "fourth"
 
     assert("fourth" == arr.pop());
     assert("second" == arr.shift());
   end
   
   class MyArray < Array
     def [](arg)
       arg
     end
   end
 
   def test_aref
     assert_equal(nil, [].slice(-1..1))
     # test that overriding in child works correctly
     assert_equal(2, MyArray.new[2])
   end
 
   def test_class
     assert(Array == ["zero", "first"].class)
     assert("Array" == Array.to_s)
   end
 
   def test_dup_and_reverse
     arr = [1, 2, 3]
     arr2 = arr.dup
     arr2.reverse!
     assert_equal([1,2,3], arr)
     assert_equal([3,2,1], arr2)
 
     assert_equal([1,2,3], [1,2,3,1,2,3,1,1,1,2,3,2,1].uniq)
 
     assert_equal([1,2,3,4], [[[1], 2], [3, [4]]].flatten)
     assert_equal(nil, [].flatten!)
   end
   
   def test_delete
     arr = [1, 2, 3]
     arr2 = []
     arr.each { |x|
       arr2 << x
       arr.delete(x) if x == 2
     }
     assert_equal([1,2], arr2)
   end
   
   def test_fill
     arr = [1,2,3,4]
     arr.fill(1,10)
     assert_equal([1,2,3,4], arr)
     arr.fill(1,0)
     assert_equal([1,1,1,1], arr)
   end
   
   def test_flatten
     arr = []
     arr << [[[arr]]]
     assert_raises(ArgumentError) {
       arr.flatten
     }
   end
 
   # To test int coersion for indicies
   class IntClass
     def initialize(num); @num = num; end
     def to_int; @num; end; 
   end
 
   def test_conversion
     arr = [1, 2, 3]
 
     index = IntClass.new(1)
     arr[index] = 4
     assert_equal(4, arr[index])
     eindex = IntClass.new(2)
     arr[index, eindex] = 5
     assert_equal([1,5], arr)
     arr.delete_at(index)
     assert_equal([1], arr)
     arr = arr * eindex
     assert_equal([1, 1], arr)
   end
 
   def test_unshift_nothing
     assert_nothing_raised { [].unshift(*[]) }
     assert_nothing_raised { [].unshift() }
   end
 
   ##### Array#[] #####
 
   def test_indexing
     assert_equal([1], Array[1])
     assert_equal([], Array[])
     assert_equal([1,2], Array[1,2])
   end
 
   ##### insert ####
 
   def test_insert
     a = [10, 11]
     a.insert(1, 12)
     assert_equal([10, 12, 11], a)
     a = []
     a.insert(-1, 10)
     assert_equal([10], a)
     a.insert(-2, 11)
     assert_equal([11, 10], a)
     a = [10]
     a.insert(-1, 11)
     assert_equal([10, 11], a)
   end
 
   ##### == #####
   
   def test_ary
     o = Object.new
     def o.to_ary; end
     def o.==(o); true; end
     assert_equal(true, [].==(o))
   end
   
   # test that extensions of the base classes are typed correctly
   class ArrayExt < Array
   end
 
   def test_array_extension
     assert_equal(ArrayExt, ArrayExt.new.class)
     assert_equal(ArrayExt, ArrayExt[:foo, :bar].class)
   end
 
   ##### flatten #####
   def test_flatten
     a = [2,[3,[4]]]
     assert_equal([1,2,3,4],[1,a].flatten)
     assert_equal([2,[3,[4]]],a)
     a = [[1,2,[3,[4],[5]],6,[7,[8]]],9]
     assert_equal([1,2,3,4,5,6,7,8,9],a.flatten)
     assert(a.flatten!,"We did flatten")
     assert(!a.flatten!,"We didn't flatten")
   end
 
   ##### splat test #####
   class ATest
     def to_a; 1; end
   end
 
   def test_splatting
     proc { |a| assert_equal(1, a) }.call(*1)
     assert_raises(TypeError) { proc { |a| }.call(*ATest.new) }
   end
 
   #### index test ####
   class AlwaysEqual
     def ==(arg)
       true
     end
   end
 
   def test_index
     array_of_alwaysequal = [AlwaysEqual.new]
     # this should pass because index should call AlwaysEqual#== when searching
     assert_equal(0, array_of_alwaysequal.index("foo"))
     assert_equal(0, array_of_alwaysequal.rindex("foo"))
   end
 
   def test_spaceship
     assert_equal(0, [] <=> [])
     assert_equal(0, [1] <=> [1])
     assert_equal(-1, [1] <=> [2])
     assert_equal(1, [2] <=> [1])
     assert_equal(1, [1] <=> [])
     assert_equal(-1, [] <=> [1])
 
     assert_equal(0, [1, 1] <=> [1, 1])
     assert_equal(-1, [1, 1] <=> [1, 2])
 
     assert_equal(1, [1,6,1] <=> [1,5,0,1])
     assert_equal(-1, [1,5,0,1] <=> [1,6,1])
   end
   
   class BadComparator
     def <=>(other)
       "hello"
     end
   end
 
   def test_bad_comparator
     assert_equal("hello", [BadComparator.new] <=> [BadComparator.new])
   end
 
   def test_raises_stack_exception
     assert_raises(SystemStackError) { a = []; a << a; a <=> a }
   end
   
   def test_multiline_array_not_really_add
     assert_raises(NoMethodError) do
   	  [1,2,3]
   	  +[2,3]
   	end
   end
 
   def test_recursive_join
     arr = []
     arr << [arr]
     arr << 1
     assert_equal("[...]1", arr.join)
   end
+  
+  class ArrayMock
+     def hash
+       0
+     end
+
+     def eql? a
+       true
+     end
+  end
+
+  def test_methods_requiring_hashing
+    a1 = ArrayMock.new
+    a2 = ArrayMock.new
+    assert_equal(1, [a1,a2].uniq.size)
+    assert_equal(1, ([a1] | [a2]).size)
+    assert_equal(1, ([a1] & [a2]).size)
+    assert_equal(0, ([a1] - [a2]).size)
+  end
 
 end
