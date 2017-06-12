diff --git a/src/org/jruby/RubyEnumerable.java b/src/org/jruby/RubyEnumerable.java
index baa21241ff..32db6e132c 100644
--- a/src/org/jruby/RubyEnumerable.java
+++ b/src/org/jruby/RubyEnumerable.java
@@ -1,582 +1,584 @@
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
 
 import java.util.Comparator;
 import java.util.Arrays;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.exceptions.JumpException;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallBlock;
 import org.jruby.runtime.BlockCallback;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.TypeConverter;
 
 /**
  * The implementation of Ruby's Enumerable module.
  */
 
 @JRubyModule(name="Enumerable")
 public class RubyEnumerable {
 
     public static RubyModule createEnumerableModule(Ruby runtime) {
         RubyModule enumModule = runtime.defineModule("Enumerable");
         runtime.setEnumerable(enumModule);
         
         enumModule.defineAnnotatedMethods(RubyEnumerable.class);
 
         return enumModule;
     }
 
     public static IRubyObject callEach(Ruby runtime, ThreadContext context, IRubyObject self,
             BlockCallback callback) {
         return self.callMethod(context, "each", IRubyObject.NULL_ARRAY, CallBlock.newCallClosure(self, runtime.getEnumerable(), 
                 Arity.noArguments(), callback, context));
     }
 
     private static class ExitIteration extends RuntimeException {
         public Throwable fillInStackTrace() {
             return this;
         }
     }
 
     @JRubyMethod(name = "first")
     public static IRubyObject first_0(ThreadContext context, IRubyObject self) {
         Ruby runtime = self.getRuntime();
         
         final IRubyObject[] holder = new IRubyObject[]{runtime.getNil()};
 
         try {
             callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         holder[0] = largs[0];
                         throw new ExitIteration();
                     }
                 });
         } catch(ExitIteration ei) {}
 
         return holder[0];
     }
 
     @JRubyMethod(name = "first")
     public static IRubyObject first_1(ThreadContext context, IRubyObject self, final IRubyObject num) {
         final Ruby runtime = self.getRuntime();
         final RubyArray result = runtime.newArray();
 
         if(RubyNumeric.fix2int(num) < 0) {
             throw runtime.newArgumentError("negative index");
         }
 
         try {
             callEach(runtime, context, self, new BlockCallback() {
                     private int iter = RubyNumeric.fix2int(num);
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if(iter-- == 0) {
                             throw new ExitIteration();
                         }
                         result.append(largs[0]);
                         return runtime.getNil();
                     }
                 });
         } catch(ExitIteration ei) {}
 
         return result;
     }
 
     @JRubyMethod(name = {"to_a", "entries"})
     public static IRubyObject to_a(ThreadContext context, IRubyObject self) {
         Ruby runtime = self.getRuntime();
         RubyArray result = runtime.newArray();
 
         callEach(runtime, context, self, new AppendBlockCallback(runtime, result));
 
         return result;
     }
 
     @JRubyMethod(name = "sort", frame = true)
     public static IRubyObject sort(ThreadContext context, IRubyObject self, final Block block) {
         Ruby runtime = self.getRuntime();
         RubyArray result = runtime.newArray();
 
         callEach(runtime, context, self, new AppendBlockCallback(runtime, result));
         result.sort_bang(block);
         
         return result;
     }
 
     @JRubyMethod(name = "sort_by", frame = true)
     public static IRubyObject sort_by(final ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
 
         if (self instanceof RubyArray) {
             RubyArray selfArray = (RubyArray) self;
             final IRubyObject[][] valuesAndCriteria = new IRubyObject[selfArray.size()][2];
 
             callEach(runtime, context, self, new BlockCallback() {
                 int i = 0;
 
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     valuesAndCriteria[i][0] = largs[0];
                     valuesAndCriteria[i++][1] = block.yield(context, largs[0]);
                     return runtime.getNil();
                 }
             });
             
             Arrays.sort(valuesAndCriteria, new Comparator<IRubyObject[]>() {
                 public int compare(IRubyObject[] o1, IRubyObject[] o2) {
                     return RubyFixnum.fix2int(o1[1].callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", o2[1]));
                 }
             });
             
             IRubyObject dstArray[] = new IRubyObject[selfArray.size()];
             for (int i = 0; i < dstArray.length; i++) {
                 dstArray[i] = valuesAndCriteria[i][0];
             }
 
             return runtime.newArrayNoCopy(dstArray);
         } else {
             final RubyArray result = runtime.newArray();
             callEach(runtime, context, self, new AppendBlockCallback(runtime, result));
             
             final IRubyObject[][] valuesAndCriteria = new IRubyObject[result.size()][2];
             for (int i = 0; i < valuesAndCriteria.length; i++) {
                 IRubyObject val = result.eltInternal(i);
                 valuesAndCriteria[i][0] = val;
                 valuesAndCriteria[i][1] = block.yield(context, val);
             }
             
             Arrays.sort(valuesAndCriteria, new Comparator<IRubyObject[]>() {
                 public int compare(IRubyObject[] o1, IRubyObject[] o2) {
                     return RubyFixnum.fix2int(o1[1].callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", o2[1]));
                 }
             });
             
             for (int i = 0; i < valuesAndCriteria.length; i++) {
                 result.eltInternalSet(i, valuesAndCriteria[i][0]);
             }
 
             return result;
         }
     }
 
     @JRubyMethod(name = "grep", required = 1, frame = true)
     public static IRubyObject grep(final ThreadContext context, IRubyObject self, final IRubyObject pattern, final Block block) {
         final Ruby runtime = self.getRuntime();
         final RubyArray result = runtime.newArray();
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
+                    context.setRubyFrameDelta(context.getRubyFrameDelta()+2);
                     if (pattern.callMethod(context, MethodIndex.OP_EQQ, "===", largs[0]).isTrue()) {
                         result.append(block.yield(context, largs[0]));
                     }
+                    context.setRubyFrameDelta(context.getRubyFrameDelta()-2);
                     return runtime.getNil();
                 }
             });
         } else {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (pattern.callMethod(context, MethodIndex.OP_EQQ, "===", largs[0]).isTrue()) {
                         result.append(largs[0]);
                     }
                     return runtime.getNil();
                 }
             });
         }
         
         return result;
     }
 
     @JRubyMethod(name = {"detect", "find"}, optional = 1, frame = true)
     public static IRubyObject detect(final ThreadContext context, IRubyObject self, IRubyObject[] args, final Block block) {
         final Ruby runtime = self.getRuntime();
         final IRubyObject result[] = new IRubyObject[] { null };
         IRubyObject ifnone = null;
 
         if (args.length == 1) ifnone = args[0];
 
         try {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (block.yield(context, largs[0]).isTrue()) {
                         result[0] = largs[0];
                         throw JumpException.SPECIAL_JUMP;
                     }
                     return runtime.getNil();
                 }
             });
         } catch (JumpException.SpecialJump sj) {
             return result[0];
         }
 
         return ifnone != null ? ifnone.callMethod(context, "call") : runtime.getNil();
     }
 
     @JRubyMethod(name = {"select", "find_all"}, frame = true)
     public static IRubyObject select(final ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final RubyArray result = runtime.newArray();
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 if (block.yield(context, largs[0]).isTrue()) result.append(largs[0]);
                 return runtime.getNil();
             }
         });
 
         return result;
     }
 
     @JRubyMethod(name = "reject", frame = true)
     public static IRubyObject reject(final ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final RubyArray result = runtime.newArray();
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 if (!block.yield(context, largs[0]).isTrue()) result.append(largs[0]);
                 return runtime.getNil();
             }
         });
 
         return result;
     }
 
     @JRubyMethod(name = {"collect", "map"}, frame = true)
     public static IRubyObject collect(final ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final RubyArray result = runtime.newArray();
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     result.append(block.yield(context, largs[0]));
                     return runtime.getNil();
                 }
             });
         } else {
             callEach(runtime, context, self, new AppendBlockCallback(runtime, result));
         }
         return result;
     }
 
     @JRubyMethod(name = "inject", optional = 1, frame = true)
     public static IRubyObject inject(final ThreadContext context, IRubyObject self, IRubyObject[] args, final Block block) {
         final Ruby runtime = self.getRuntime();
         final IRubyObject result[] = new IRubyObject[] { null };
 
         if (args.length == 1) result[0] = args[0];
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 result[0] = result[0] == null ? 
                         largs[0] : block.yield(context, runtime.newArray(result[0], largs[0]), null, null, true);
 
                 return runtime.getNil();
             }
         });
 
         return result[0] == null ? runtime.getNil() : result[0];
     }
 
     @JRubyMethod(name = "partition", frame = true)
     public static IRubyObject partition(final ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final RubyArray arr_true = runtime.newArray();
         final RubyArray arr_false = runtime.newArray();
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 if (block.yield(context, largs[0]).isTrue()) {
                     arr_true.append(largs[0]);
                 } else {
                     arr_false.append(largs[0]);
                 }
 
                 return runtime.getNil();
             }
         });
 
         return runtime.newArray(arr_true, arr_false);
     }
 
     private static class EachWithIndex implements BlockCallback {
         private int index = 0;
         private final Block block;
         private final Ruby runtime;
 
         public EachWithIndex(ThreadContext ctx, Block block) {
             this.block = block;
             this.runtime = ctx.getRuntime();
         }
 
         public IRubyObject call(ThreadContext context, IRubyObject[] iargs, Block block) {
             this.block.yield(context, runtime.newArray(iargs[0], runtime.newFixnum(index++)));
             return runtime.getNil();            
         }
     }
 
     @JRubyMethod(name = "each_with_index", frame = true)
     public static IRubyObject each_with_index(ThreadContext context, IRubyObject self, Block block) {
         self.callMethod(context, "each", IRubyObject.NULL_ARRAY, CallBlock.newCallClosure(self, self.getRuntime().getEnumerable(), 
                 Arity.noArguments(), new EachWithIndex(context, block), context));
         
         return self;
     }
 
     @JRubyMethod(name = {"include?", "member?"}, required = 1, frame = true)
     public static IRubyObject include_p(final ThreadContext context, IRubyObject self, final IRubyObject arg) {
         final Ruby runtime = self.getRuntime();
 
         try {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (RubyObject.equalInternal(context, arg, largs[0])) {
                         throw JumpException.SPECIAL_JUMP;
                     }
                     return runtime.getNil();
                 }
             });
         } catch (JumpException.SpecialJump sj) {
             return runtime.getTrue();
         }
         
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "max", frame = true)
     public static IRubyObject max(ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final IRubyObject result[] = new IRubyObject[] { null };
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result[0] == null || RubyComparable.cmpint(ctx, block.yield(ctx, 
                             runtime.newArray(largs[0], result[0])), largs[0], result[0]) > 0) {
                         result[0] = largs[0];
                     }
                     return runtime.getNil();
                 }
             });
         } else {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result[0] == null || RubyComparable.cmpint(ctx, largs[0].callMethod(ctx,
                             MethodIndex.OP_SPACESHIP, "<=>", result[0]), largs[0], result[0]) > 0) {
                         result[0] = largs[0];
                     }
                     return runtime.getNil();
                 }
             });
         }
         
         return result[0] == null ? runtime.getNil() : result[0];
     }
 
     @JRubyMethod(name = "min", frame = true)
     public static IRubyObject min(ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final IRubyObject result[] = new IRubyObject[] { null };
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result[0] == null || RubyComparable.cmpint(ctx, block.yield(ctx, 
                             runtime.newArray(largs[0], result[0])), largs[0], result[0]) < 0) {
                         result[0] = largs[0];
                     }
                     return runtime.getNil();
                 }
             });
         } else {
             callEach(runtime, context, self, new BlockCallback() {
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     if (result[0] == null || RubyComparable.cmpint(ctx, largs[0].callMethod(ctx,
                             MethodIndex.OP_SPACESHIP, "<=>", result[0]), largs[0], result[0]) < 0) {
                         result[0] = largs[0];
                     }
                     return runtime.getNil();
                 }
             });
         }
         
         return result[0] == null ? runtime.getNil() : result[0];
     }
 
     @JRubyMethod(name = "all?", frame = true)
     public static IRubyObject all_p(ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
 
         try {
             if (block.isGiven()) {
                 callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if (!block.yield(ctx, largs[0]).isTrue()) {
                             throw JumpException.SPECIAL_JUMP;
                         }
                         return runtime.getNil();
                     }
                 });
             } else {
                 callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if (!largs[0].isTrue()) {
                             throw JumpException.SPECIAL_JUMP;
                         }
                         return runtime.getNil();
                     }
                 });
             }
         } catch (JumpException.SpecialJump sj) {
             return runtime.getFalse();
         }
 
         return runtime.getTrue();
     }
 
     @JRubyMethod(name = "any?", frame = true)
     public static IRubyObject any_p(ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
 
         try {
             if (block.isGiven()) {
                 callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if (block.yield(ctx, largs[0]).isTrue()) {
                             throw JumpException.SPECIAL_JUMP;
                         }
                         return runtime.getNil();
                     }
                 });
             } else {
                 callEach(runtime, context, self, new BlockCallback() {
                     public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                         if (largs[0].isTrue()) {
                             throw JumpException.SPECIAL_JUMP;
                         }
                         return runtime.getNil();
                     }
                 });
             }
         } catch (JumpException.SpecialJump sj) {
             return runtime.getTrue();
         }
 
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "zip", rest = true, frame = true)
     public static IRubyObject zip(ThreadContext context, IRubyObject self, final IRubyObject[] args, final Block block) {
         final Ruby runtime = self.getRuntime();
 
         for (int i = 0; i < args.length; i++) {
             args[i] = TypeConverter.convertToType(args[i], runtime.getArray(), MethodIndex.TO_A, "to_a");
         }
         
         final int aLen = args.length + 1;
 
         if (block.isGiven()) {
             callEach(runtime, context, self, new BlockCallback() {
                 int ix = 0;
 
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     RubyArray array = runtime.newArray(aLen);
                     array.append(largs[0]);
                     for (int i = 0, j = args.length; i < j; i++) {
                         array.append(((RubyArray) args[i]).entry(ix));
                     }
                     block.yield(ctx, array);
                     ix++;
                     return runtime.getNil();
                 }
             });
             return runtime.getNil();
         } else {
             final RubyArray zip = runtime.newArray();
             callEach(runtime, context, self, new BlockCallback() {
                 int ix = 0;
 
                 public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                     RubyArray array = runtime.newArray(aLen);
                     array.append(largs[0]);
                     for (int i = 0, j = args.length; i < j; i++) {
                         array.append(((RubyArray) args[i]).entry(ix));
                     }
                     zip.append(array);
                     ix++;
                     return runtime.getNil();
                 }
             });
             return zip;
         }
     }
 
     @JRubyMethod(name = "group_by", frame = true)
     public static IRubyObject group_by(ThreadContext context, IRubyObject self, final Block block) {
         final Ruby runtime = self.getRuntime();
         final RubyHash result = new RubyHash(runtime);
 
         callEach(runtime, context, self, new BlockCallback() {
             public IRubyObject call(ThreadContext ctx, IRubyObject[] largs, Block blk) {
                 IRubyObject key = block.yield(ctx, largs[0]);
                 IRubyObject curr = result.fastARef(key);
 
                 if (curr == null) {
                     curr = runtime.newArray();
                     result.fastASet(key, curr);
                 }
                 curr.callMethod(ctx, MethodIndex.OP_LSHIFT, "<<", largs[0]);
                 return runtime.getNil();
             }
         });
 
         return result;
     }
     
     public static final class AppendBlockCallback implements BlockCallback {
         private Ruby runtime;
         private RubyArray result;
 
         public AppendBlockCallback(Ruby runtime, RubyArray result) {
             this.runtime = runtime;
             this.result = result;
         }
         
         public IRubyObject call(ThreadContext context, IRubyObject[] largs, Block blk) {
             result.append(largs[0]);
             
             return runtime.getNil();
         }
     }
 }
diff --git a/src/org/jruby/RubyRegexp.java b/src/org/jruby/RubyRegexp.java
index 9fe70953f7..9c87b53c27 100644
--- a/src/org/jruby/RubyRegexp.java
+++ b/src/org/jruby/RubyRegexp.java
@@ -1,1205 +1,1205 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Nick Sieger <nicksieger@gmail.com>
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
 
 import java.lang.ref.SoftReference;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.joni.Matcher;
 import org.joni.NameEntry;
 import org.joni.Option;
 import org.joni.Regex;
 import org.joni.Region;
 import org.joni.Syntax;
 import org.joni.WarnCallback;
 import org.joni.encoding.Encoding;
 import static org.jruby.anno.FrameField.*;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.parser.ReOptions;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.TypeConverter;
 
 /**
  *
  */
 @JRubyClass(name="Regexp")
 public class RubyRegexp extends RubyObject implements ReOptions, WarnCallback {
     private KCode kcode;
     private Regex pattern;
     private ByteList str;
 
     private final static byte[] PIPE = new byte[]{'|'};
     private final static byte[] DASH = new byte[]{'-'};
     private final static byte[] R_PAREN = new byte[]{')'};
     private final static byte[] COLON = new byte[]{':'};
     private final static byte[] M_CHAR = new byte[]{'m'};
     private final static byte[] I_CHAR = new byte[]{'i'};
     private final static byte[] X_CHAR = new byte[]{'x'};
 
     private static final int REGEXP_LITERAL_F =     1 << 11;
     private static final int REGEXP_KCODE_DEFAULT = 1 << 12;
 
     public void setLiteral() {
         flags |= REGEXP_LITERAL_F;
     }
     
     public void clearLiteral() {
         flags &= ~REGEXP_LITERAL_F;
     }
     
     public boolean isLiteral() {
         return (flags & REGEXP_LITERAL_F) != 0;
     }
 
     public void setKCodeDefault() {
         flags |= REGEXP_KCODE_DEFAULT;
     }
     
     public void clearKCodeDefault() {
         flags &= ~REGEXP_KCODE_DEFAULT;
     }
     
     public boolean isKCodeDefault() {
         return (flags & REGEXP_KCODE_DEFAULT) != 0;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     private static Map<ByteList, Regex> getPatternCache() {
         Map<ByteList, Regex> cache = patternCache.get();
         if (cache == null) {
             cache = new ConcurrentHashMap<ByteList, Regex>(5);
             patternCache = new SoftReference<Map<ByteList, Regex>>(cache);
         }
         return cache;
     }
 
     static volatile SoftReference<Map<ByteList, Regex>> patternCache = new SoftReference<Map<ByteList, Regex>>(null);    
 
     public static RubyClass createRegexpClass(Ruby runtime) {
         RubyClass regexpClass = runtime.defineClass("Regexp", runtime.getObject(), REGEXP_ALLOCATOR);
         runtime.setRegexp(regexpClass);
         regexpClass.index = ClassIndex.REGEXP;
         regexpClass.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyRegexp;
                 }
             };
         
         regexpClass.defineConstant("IGNORECASE", runtime.newFixnum(RE_OPTION_IGNORECASE));
         regexpClass.defineConstant("EXTENDED", runtime.newFixnum(RE_OPTION_EXTENDED));
         regexpClass.defineConstant("MULTILINE", runtime.newFixnum(RE_OPTION_MULTILINE));
         
         regexpClass.defineAnnotatedMethods(RubyRegexp.class);
 
         return regexpClass;
     }
     
     private static ObjectAllocator REGEXP_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyRegexp instance = new RubyRegexp(runtime, klass);
             return instance;
         }
     };
 
     /** used by allocator
      * 
      */
     private RubyRegexp(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
     }
 
     /** default constructor
      * 
      */
     private RubyRegexp(Ruby runtime) {
         super(runtime, runtime.getRegexp());
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newRegexp(Ruby runtime, String pattern, int options) {
         return newRegexp(runtime, ByteList.create(pattern), options);
     }
 
     // used only by the compiler/interpreter (will set the literal flag)
     public static RubyRegexp newRegexp(Ruby runtime, ByteList pattern, int options) {
         RubyRegexp regexp = newRegexp(runtime, pattern, options, false);
         regexp.setLiteral();
         return regexp;
     }
     
     public static RubyRegexp newRegexp(Ruby runtime, ByteList pattern, int options, boolean quote) {
         RubyRegexp regexp = new RubyRegexp(runtime);
         regexp.initialize(pattern, options, quote);
         return regexp;
     }
 
     public void warn(String message) {
         getRuntime().getWarnings().warn(ID.MISCELLANEOUS, message);
     }
 
     @JRubyMethod(name = "kcode")
     public IRubyObject kcode() {
         return (!isKCodeDefault() && kcode != null) ? getRuntime().newString(kcode.name()) : getRuntime().getNil();
     }
 
     public int getNativeTypeIndex() {
         return ClassIndex.REGEXP;
     }
     
     public Regex getPattern() {
         return pattern;
     }
 
     private void check() {
         if (pattern == null || str == null) throw getRuntime().newTypeError("uninitialized Regexp");
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         check();
         int hashval = (int)pattern.getOptions();
         int len = this.str.realSize;
         int p = this.str.begin;
         while (len-->0) {
             hashval = hashval * 33 + str.bytes[p++];
         }
         hashval = hashval + (hashval>>5);
         return getRuntime().newFixnum(hashval);
     }
 
     @JRubyMethod(name = {"==", "eql?"}, required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         if(this == other) return getRuntime().getTrue();
         if(!(other instanceof RubyRegexp)) return getRuntime().getFalse();
         RubyRegexp otherRegex = (RubyRegexp)other;
         
         check();
         otherRegex.check();
         
         if(str.equal(otherRegex.str) && kcode == otherRegex.kcode &&
                                       pattern.getOptions() == otherRegex.pattern.getOptions()) {
             return getRuntime().getTrue();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "~", reads = {LASTLINE, BACKREF}, writes = BACKREF)
     public IRubyObject op_match2(ThreadContext context) {
         IRubyObject line = context.getCurrentFrame().getLastLine();
         if(!(line instanceof RubyString)) {
             context.getCurrentFrame().setBackRef(getRuntime().getNil());
             return getRuntime().getNil();
         }
         int start = search(context, (RubyString)line, 0, false);
         if(start < 0) {
             return getRuntime().getNil();
         } else {
             return getRuntime().newFixnum(start);
         }
     }
 
     /** rb_reg_eqq
      * 
      */
     @JRubyMethod(name = "===", required = 1, writes = BACKREF)
     public IRubyObject eqq(ThreadContext context, IRubyObject str) {
         if(!(str instanceof RubyString)) str = str.checkStringType();
 
         if (str.isNil()) {
             context.getCurrentFrame().setBackRef(getRuntime().getNil());
             return getRuntime().getFalse();
         }
         int start = search(context, (RubyString)str, 0, false);
         return (start < 0) ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     private static final int REGEX_QUOTED = 1;
     private void initialize(ByteList bytes, int options, boolean quote) {
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) throw getRuntime().newSecurityError("Insecure: can't modify regexp");
         checkFrozen();
         if (isLiteral()) throw getRuntime().newSecurityError("can't modify literal regexp");
 
         setKCode(options);
 
         Map<ByteList, Regex> cache = getPatternCache();
         Regex pat = cache.get(bytes);
 
         if (pat != null &&
             pat.getEncoding() == kcode.getEncoding() &&
             pat.getOptions() == (options & 0xf) &&
             ((pat.getUserOptions() & REGEX_QUOTED) != 0) == quote) { // cache hit
             pattern = pat;
         } else {
             if (quote) bytes = quote(bytes, getRuntime().getKCode());
             makeRegexp(bytes, bytes.begin, bytes.realSize, options & 0xf, kcode.getEncoding());
             if (quote) pattern.setUserOptions(REGEX_QUOTED);
             cache.put(bytes, pattern);
         }
 
         str = bytes;
     }
 
     private void makeRegexp(ByteList bytes, int start, int len, int flags, Encoding enc) {
         try {
             pattern = new Regex(bytes.bytes, start, start + len, flags, enc, Syntax.DEFAULT, this);
         } catch(Exception e) {
             rb_reg_raise(bytes.bytes, start, len, e.getMessage(), flags);
         }
     }
 
     private final void rb_reg_raise(byte[] s, int start, int len, String err,int flags) {
         throw getRuntime().newRegexpError(err + ": " + rb_reg_desc(s,start, len,flags));
     }
 
     private final StringBuffer rb_reg_desc(byte[] s, int start, int len, int flags) {
         StringBuffer sb = new StringBuffer("/");
         rb_reg_expr_str(sb, s, start, len);
         sb.append("/");
 
         if((flags & ReOptions.RE_OPTION_MULTILINE) != 0) sb.append("m");
         if((flags & ReOptions.RE_OPTION_IGNORECASE) != 0) sb.append("i");
         if((flags & ReOptions.RE_OPTION_EXTENDED) != 0) sb.append("x");
 
         if (kcode != null && !isKCodeDefault()) {
             sb.append(kcode.name().charAt(0));
         }
         return sb;
     }
 
     private final void rb_reg_expr_str(StringBuffer sb, byte[] s, int start, int len) {
         int p,pend;
         boolean need_escape = false;
         p = start;
         pend = start+len;
         Encoding enc = kcode.getEncoding();
         while(p<pend) {
             if(s[p] == '/' || (!(' ' == s[p] || (!Character.isWhitespace(s[p]) && 
                                                  !Character.isISOControl(s[p]))) && 
                                enc.length(s[p])==1)) {
                 need_escape = true;
                 break;
             }
             p += enc.length(s[p]);
         }
         if(!need_escape) {
             sb.append(new ByteList(s,start,len,false).toString());
         } else {
             p = 0;
             while(p < pend) {
                 if(s[p] == '\\') {
                     int n = enc.length(s[p+1]) + 1;
                     sb.append(new ByteList(s,p,n,false).toString());
                     p += n;
                     continue;
                 } else if(s[p] == '/') {
                     sb.append("\\/");
                 } else if(enc.length(s[p])!=1) {
                     sb.append(new ByteList(s,p,enc.length(s[p]),false).toString());
                     p += enc.length(s[p]);
                     continue;
                 } else if((' ' == s[p] || (!Character.isWhitespace(s[p]) && 
                                            !Character.isISOControl(s[p])))) {
                     sb.append((char)(s[p]&0xFF));
                 } else if(!Character.isWhitespace((char)(s[p]&0xFF))) {
                     sb.append('\\');
                     sb.append(Integer.toString((int)(s[p]&0377),8));
                 } else {
                     sb.append((char)(s[p]&0xFF));
                 }
                 p++;
             }
         }
     }    
 
     /** rb_reg_init_copy
      */
     @JRubyMethod(name = "initialize_copy", required = 1)
     public IRubyObject initialize_copy(IRubyObject re) {
         if(this == re) return this;
 
         checkFrozen();
 
         if (getMetaClass().getRealClass() != re.getMetaClass().getRealClass()) {
             throw getRuntime().newTypeError("wrong argument type");
 	    }
 
         RubyRegexp regexp = (RubyRegexp)re;
         regexp.check();
 
         initialize(regexp.str, regexp.getOptions(), false);
 
         return this;
     }
 
     /** rb_set_kcode
      */
     private int getKcode() {
         if(kcode == KCode.NONE) {
             return 16;
         } else if(kcode == KCode.EUC) {
             return 32;
         } else if(kcode == KCode.SJIS) {
             return 48;
         } else if(kcode == KCode.UTF8) {
             return 64;
         }
         return 0;
     }
     
     /**
      */
     private void setKCode(int options) {
         clearKCodeDefault();
         switch(options & ~0xf) {
         case 0:
         default:
             setKCodeDefault();
             kcode = getRuntime().getKCode();
             break;
         case 16:
             kcode = KCode.NONE;
             break;
         case 32:
             kcode = KCode.EUC;
             break;
         case 48:
             kcode = KCode.SJIS;
             break;
         case 64:
             kcode = KCode.UTF8;
             break;
         }        
     }
 
     /** rb_reg_options
      */
     private int getOptions() {
         check();
         int options = (int)(pattern.getOptions() & (RE_OPTION_IGNORECASE|RE_OPTION_MULTILINE|RE_OPTION_EXTENDED));
         if(!isKCodeDefault()) {
             options |= getKcode();
         }
         return options;
     }
 
     /** rb_reg_initialize_m
      */
     @JRubyMethod(name = "initialize", optional = 3, visibility = Visibility.PRIVATE)
     public IRubyObject initialize_m(IRubyObject[] args) {
         ByteList bytes;
         int regexFlags = 0;
 
         if(args[0] instanceof RubyRegexp) {
             if(args.length > 1) {
                 getRuntime().getWarnings().warn(ID.REGEXP_IGNORED_FLAGS, "flags" + (args.length == 3 ? " and encoding" : "") + " ignored");
             }
             RubyRegexp regexp = (RubyRegexp)args[0];
             regexp.check();
 
             regexFlags = (int)regexp.pattern.getOptions() & 0xF;
             if (!regexp.isKCodeDefault() && regexp.kcode != null && regexp.kcode != KCode.NIL) {
                 if (regexp.kcode == KCode.NONE) {
                     regexFlags |= 16;
                 } else if (regexp.kcode == KCode.EUC) {
                     regexFlags |= 32;
                 } else if (regexp.kcode == KCode.SJIS) {
                     regexFlags |= 48;
                 } else if (regexp.kcode == KCode.UTF8) {
                     regexFlags |= 64;
                 }                
             }
             bytes = regexp.str;
         } else {
             if (args.length >= 2) {
                 if (args[1] instanceof RubyFixnum) {
                     regexFlags = RubyNumeric.fix2int(args[1]);
                 } else if (args[1].isTrue()) {
                     regexFlags = RE_OPTION_IGNORECASE;
                 }
             }
             if (args.length == 3 && !args[2].isNil()) {
                 ByteList kcodeBytes = args[2].convertToString().getByteList();
                 char first = kcodeBytes.length() > 0 ? kcodeBytes.charAt(0) : 0;
                 regexFlags &= ~0x70;
                 switch (first) {
                 case 'n': case 'N':
                     regexFlags |= 16;
                     break;
                 case 'e': case 'E':
                     regexFlags |= 32;
                     break;
                 case 's': case 'S':
                     regexFlags |= 48;
                     break;
                 case 'u': case 'U':
                     regexFlags |= 64;
                     break;
                 default:
                     break;
                 }
             }
             bytes = args[0].convertToString().getByteList();
         }
         initialize(bytes, regexFlags, false);
 
         return this;
     }
 
     @JRubyMethod(name = {"new", "compile"}, required = 1, optional = 2, meta = true)
     public static RubyRegexp newInstance(IRubyObject recv, IRubyObject[] args) {
         RubyClass klass = (RubyClass)recv;
         
         RubyRegexp re = (RubyRegexp) klass.allocate();
         re.callInit(args, Block.NULL_BLOCK);
         
         return re;
     }
 
     @JRubyMethod(name = "options")
     public IRubyObject options() {
         return getRuntime().newFixnum(getOptions());
     }
     
     /** rb_reg_search
      */
     public int search(ThreadContext context, RubyString str, int pos, boolean reverse) {
         Ruby runtime = getRuntime();
-        Frame frame = context.getCurrentFrame();
+        Frame frame = context.getCurrentRubyFrame();
 
         ByteList value = str.getByteList();
         if (pos > value.realSize || pos < 0) {
             frame.setBackRef(runtime.getNil());
             return -1;
         }
         
         check();
         
         int range = reverse ? -pos : value.realSize - pos;
         
         Matcher matcher = pattern.matcher(value.bytes, value.begin, value.begin + value.realSize);
         
         int result = matcher.search(value.begin + pos, 
                                     value.begin + pos + range,
                                     Option.NONE);
         
         if (result < 0) {
             frame.setBackRef(runtime.getNil());
             return result;
         }
         
         updateBackRef(str, frame, matcher);
 
         return result;
     }
     
     final RubyMatchData updateBackRef(RubyString str, Frame frame, Matcher matcher) {
         IRubyObject backref = frame.getBackRef();
         final RubyMatchData match;
         if (backref == null || backref.isNil() || ((RubyMatchData)backref).used()) {
             match = new RubyMatchData(getRuntime());
         } else {
             match = (RubyMatchData)backref;
             if(getRuntime().getSafeLevel() >= 3) {
                 match.setTaint(true);
             } else {
                 match.setTaint(false);
             }
         }
         
         match.regs = matcher.getRegion(); // lazy, null when no groups defined
         match.begin = matcher.getBegin();
         match.end = matcher.getEnd();
         
         match.str = (RubyString)str.strDup().freeze();
         match.pattern = pattern;
 
         frame.setBackRef(match);
         
         match.infectBy(this);
         match.infectBy(str);
         return match;
     }
 
     /** rb_reg_match
      * 
      */
     @JRubyMethod(name = "=~", required = 1, reads = BACKREF, writes = BACKREF)
     public IRubyObject op_match(ThreadContext context, IRubyObject str) {
         int start;
         if(str.isNil()) {
             context.getCurrentFrame().setBackRef(getRuntime().getNil());
             return str;
         }
         
         start = search(context, str.convertToString(), 0, false);
 
         if(start < 0) return getRuntime().getNil();
 
         return RubyFixnum.newFixnum(getRuntime(), start);
     }
 
     /** rb_reg_match_m
      * 
      */
     @JRubyMethod(name = "match", required = 1, reads = BACKREF)
     public IRubyObject match_m(ThreadContext context, IRubyObject str) {
         if(op_match(context, str).isNil()) {
             return getRuntime().getNil();
         }
         IRubyObject result =  context.getCurrentFrame().getBackRef();
         if(result instanceof RubyMatchData) {
             ((RubyMatchData)result).use();
         }
         return result;
     }
 
 
     public RubyString regsub(RubyString str, RubyString src, Matcher matcher) {
         Region regs = matcher.getRegion();
         int mbeg = matcher.getBegin();
         int mend = matcher.getEnd();
         
         int p,s,e;
         p = s = 0;
         int no = -1;
         ByteList bs = str.getByteList();
         ByteList srcbs = src.getByteList();
         e = bs.length();
         RubyString val = null;
         Encoding enc = kcode.getEncoding();
 
         int beg, end;
         while(s < e) {
             int ss = s;
             char c = bs.charAt(s++);
             if(enc.length((byte)c) != 1) {
                 s += enc.length((byte)c) - 1;
                 continue;
             }
             if (c != '\\' || s == e) continue;
             if (val == null) val = RubyString.newString(getRuntime(), new ByteList(ss - p));
 
             val.cat(bs.bytes, bs.begin + p, ss - p);
             c = bs.charAt(s++);
             p = s;
             
             switch(c) {
             case '0': case '1': case '2': case '3': case '4':
             case '5': case '6': case '7': case '8': case '9':
                 no = c - '0';
                 break;
             case '&':
                 no = 0;
                 break;
             case '`':
                 beg = regs == null ? mbeg : regs.beg[0];
                 val.cat(srcbs.bytes, srcbs.begin, beg);
                 continue;
 
             case '\'':
                 end = regs == null ? mend : regs.end[0];
                 val.cat(srcbs.bytes, srcbs.begin + end, src.getByteList().realSize - end);
                 continue;
 
             case '+':
                 if (regs == null) {
                     if (mbeg == -1) {
                         no = 0;
                         continue;
                     }
                 } else {
                     no = regs.numRegs-1;
                     while(regs.beg[no] == -1 && no > 0) no--;
                     if (no == 0) continue;
                 }
                 break;
             case '\\':
                 val.cat(bs.bytes, s - 1, 1);
                 continue;
             default:
                 val.cat(bs.bytes, s - 2, 2);
                 continue;
             }
 
             if (regs != null) {
                 if (no >= 0) {
                     if (no >= regs.numRegs || regs.beg[no] == -1) continue;
                     val.cat(srcbs.bytes, srcbs.begin + regs.beg[no], regs.end[no] - regs.beg[no]);
                 }
             } else {
                 if (no != 0 || mbeg == -1) continue;
                 val.cat(srcbs.bytes, srcbs.begin + mbeg, mend - mbeg);
             }
         }
 
         if(p < e) {
             if(val == null) {
                 val = RubyString.newString(getRuntime(), bs.makeShared(p, e-p));
             } else {
                 val.cat(bs.bytes, bs.begin + p, e - p);
             }
         }
         if (val == null) return str;
 
         return val;
     }
 
     final int adjustStartPos(RubyString str, int pos, boolean reverse) {
         check();
         ByteList value = str.getByteList();
         return pattern.adjustStartPosition(value.bytes, value.begin, value.realSize, pos, reverse);
     }
 
     @JRubyMethod(name = "casefold?")
     public IRubyObject casefold_p() {
         check();
         if ((pattern.getOptions() & RE_OPTION_IGNORECASE) != 0) {
             return getRuntime().getTrue();
         }
         return getRuntime().getFalse();
     }
 
     /** rb_reg_source
      * 
      */
     @JRubyMethod(name = "source")
     public IRubyObject source() {
         check();
         RubyString str = RubyString.newStringShared(getRuntime(), this.str);
         if(isTaint()) {
             str.taint();
         }
         return str;
     }
 
     final int length() {
         return str.realSize;
     }
 
     /** rb_reg_inspect
      *
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         check();
         return getRuntime().newString(ByteList.create(rb_reg_desc(str.bytes, str.begin, str.realSize, pattern.getOptions()).toString()));
     }
 
     private final static int EMBEDDABLE = RE_OPTION_MULTILINE|RE_OPTION_IGNORECASE|RE_OPTION_EXTENDED;
 
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         RubyString ss = getRuntime().newString("(?");
         check();
         int options = pattern.getOptions();
         int p = str.begin;
         int l = str.realSize;
         byte[] _str = str.bytes;
 
 
         again: do {
             if(l >= 4 && _str[p] == '(' && _str[p+1] == '?') {
                 boolean err = true;
                 p += 2;
                 if((l -= 2) > 0) {
                     do {
                         if(_str[p] == 'm') {
                             options |= RE_OPTION_MULTILINE;
                         } else if(_str[p] == 'i') {
                             options |= RE_OPTION_IGNORECASE;
                         } else if(_str[p] == 'x') {
                             options |= RE_OPTION_EXTENDED;
                         } else {
                             break;
                         }
                         p++;
                     } while(--l > 0);
                 }
                 if(l > 1 && _str[p] == '-') {
                     ++p;
                     --l;
                     do {
                         if(_str[p] == 'm') {
                             options &= ~RE_OPTION_MULTILINE;
                         } else if(_str[p] == 'i') {
                             options &= ~RE_OPTION_IGNORECASE;
                         } else if(_str[p] == 'x') {
                             options &= ~RE_OPTION_EXTENDED;
                         } else {
                             break;
                         }
                         p++;
                     } while(--l > 0);
                 }
                 if(_str[p] == ')') {
                     --l;
                     ++p;
                     continue again;
                 }
                 if(_str[p] == ':' && _str[p+l-1] == ')') {
                     try {
                         new Regex(_str,++p,l-=2,Option.DEFAULT,kcode.getEncoding(),Syntax.DEFAULT);
                         err = false;
                     } catch(Exception e) {
                         err = true;
                     }
                 }
                 if(err) {
                     options = (int)pattern.getOptions();
                     p = str.begin;
                     l = str.realSize;
                 }
             }
 
             if((options & RE_OPTION_MULTILINE)!=0) ss.cat(M_CHAR);
             if((options & RE_OPTION_IGNORECASE)!=0) ss.cat(I_CHAR);
             if((options & RE_OPTION_EXTENDED)!=0) ss.cat(X_CHAR);
 
             if((options&EMBEDDABLE) != EMBEDDABLE) {
                 ss.cat(DASH);
                 if((options & RE_OPTION_MULTILINE)==0) ss.cat(M_CHAR);
                 if((options & RE_OPTION_IGNORECASE)==0) ss.cat(I_CHAR);
                 if((options & RE_OPTION_EXTENDED)==0) ss.cat(X_CHAR);
             }
             ss.cat(COLON);
             rb_reg_expr_str(ss,p,l);
             ss.cat(R_PAREN);
             ss.infectBy(this);
             return ss;
         } while(true);
     }
 
     private final boolean ISPRINT(byte c) {
         return ISPRINT((char)(c&0xFF));
     }
 
     private final boolean ISPRINT(char c) {
         return (' ' == c || (!Character.isWhitespace(c) && !Character.isISOControl(c)));
     }
 
     private final void rb_reg_expr_str(RubyString ss, int s, int l) {
         int p = s;
         int pend = l;
         boolean need_escape = false;
         while(p<pend) {
             if(str.bytes[p] == '/' || (!ISPRINT(str.bytes[p]) && kcode.getEncoding().length(str.bytes[p]) == 1)) {
                 need_escape = true;
                 break;
             }
             p += kcode.getEncoding().length(str.bytes[p]);
         }
         if(!need_escape) {
             ss.cat(str.bytes,s,l);
         } else {
             p = s; 
             while(p<pend) {
                 if(str.bytes[p] == '\\') {
                     int n = kcode.getEncoding().length(str.bytes[p+1]) + 1;
                     ss.cat(str.bytes,p,n);
                     p += n;
                     continue;
                 } else if(str.bytes[p] == '/') {
                     char c = '\\';
                     ss.cat((byte)c);
                     ss.cat(str.bytes,p,1);
                 } else if(kcode.getEncoding().length(str.bytes[p]) != 1) {
                     ss.cat(str.bytes,p,kcode.getEncoding().length(str.bytes[p]));
                     p += kcode.getEncoding().length(str.bytes[p]);
                     continue;
                 } else if(ISPRINT(str.bytes[p])) {
                     ss.cat(str.bytes,p,1);
                 } else if(!Character.isWhitespace(str.bytes[p])) {
                     ss.cat(ByteList.create(Integer.toString(str.bytes[p]&0377,8)));
                 } else {
                     ss.cat(str.bytes,p,1);
                 }
                 p++;
             }
         }
     }
 
     /** rb_reg_s_quote
      * 
      */
     @JRubyMethod(name = {"quote", "escape"}, required = 1, optional = 1, meta = true)
     public static RubyString quote(IRubyObject recv, IRubyObject[] args) {
         IRubyObject kcode = args.length == 2 ? args[1] : null;
         IRubyObject str = args[0];
         KCode code = recv.getRuntime().getKCode();
 
         if (kcode != null && !kcode.isNil()) {
             code = KCode.create(recv.getRuntime(), kcode.toString());
         }
         
         RubyString src = str.convertToString();
         RubyString dst = RubyString.newString(recv.getRuntime(), quote(src.getByteList(), code));
         dst.infectBy(src);
         return dst;
     }
 
     /** rb_reg_quote
      *
      */
     public static ByteList quote(ByteList str, KCode kcode) {
         ByteList bs = str;
         int tix = 0;
         int s = bs.begin;
         char c;
         int send = s+bs.length();
         Encoding enc = kcode.getEncoding();
         meta_found: do {
             for(; s<send; s++) {
                 c = (char)(bs.bytes[s]&0xFF);
                 if(enc.length((byte)c) != 1) {
                     int n = enc.length((byte)c);
                     while(n-- > 0 && s < send) {
                         s++;
                     }
                     s--;
                     continue;
                 }
                 switch (c) {
                 case '[': case ']': case '{': case '}':
                 case '(': case ')': case '|': case '-':
                 case '*': case '.': case '\\':
                 case '?': case '+': case '^': case '$':
                 case ' ': case '#':
                 case '\t': case '\f': case '\n': case '\r':
                     break meta_found;
                 }
             }
             return bs;
         } while(false);
         ByteList b1 = new ByteList(send*2);
         System.arraycopy(bs.bytes,bs.begin,b1.bytes,b1.begin,s-bs.begin);
         tix += (s-bs.begin);
 
         for(; s<send; s++) {
             c = (char)(bs.bytes[s]&0xFF);
             if(enc.length((byte)c) != 1) {
                 int n = enc.length((byte)c);
                 while(n-- > 0 && s < send) {
                     b1.bytes[tix++] = bs.bytes[s++];
                 }
                 s--;
                 continue;
             }
 
             switch(c) {
             case '[': case ']': case '{': case '}':
             case '(': case ')': case '|': case '-':
             case '*': case '.': case '\\':
             case '?': case '+': case '^': case '$':
             case '#':
                 b1.bytes[tix++] = '\\';
                 break;
             case ' ':
                 b1.bytes[tix++] = '\\';
                 b1.bytes[tix++] = ' ';
                 continue;
             case '\t':
                 b1.bytes[tix++] = '\\';
                 b1.bytes[tix++] = 't';
                  continue;
             case '\n':
                 b1.bytes[tix++] = '\\';
                 b1.bytes[tix++] = 'n';
                 continue;
             case '\r':
                 b1.bytes[tix++] = '\\';
                 b1.bytes[tix++] = 'r';
                 continue;
             case '\f':
                 b1.bytes[tix++] = '\\';
                 b1.bytes[tix++] = 'f';
                 continue;
             }
             b1.bytes[tix++] = (byte)c;
         }
         b1.realSize = tix;
         return b1;
     }
 
 
     /** rb_reg_nth_match
      *
      */
     public static IRubyObject nth_match(int nth, IRubyObject match) {
         if (match.isNil()) return match;
         RubyMatchData m = (RubyMatchData)match;
 
         int start, end;
         
         if (m.regs == null) {
             if (nth >= 1) return match.getRuntime().getNil();
             if (nth < 0 && ++nth <= 0) return match.getRuntime().getNil();
             start = m.begin;
             end = m.end;
         } else {
             if (nth >= m.regs.numRegs) return match.getRuntime().getNil();
             if (nth < 0 && (nth+=m.regs.numRegs) <= 0) return match.getRuntime().getNil();
             start = m.regs.beg[nth];
             end = m.regs.end[nth];
         }
         
         if(start == -1) return match.getRuntime().getNil();
 
         RubyString str = m.str.makeShared(start, end - start);
         str.infectBy(match);
         return str;
     }
 
     /** rb_reg_last_match
      *
      */
     public static IRubyObject last_match(IRubyObject match) {
         return nth_match(0, match);
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * @deprecated Use the versions with zero, one, or two args.
      */
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         switch (args.length) {
         case 0:
             return last_match_s(context, recv);
         case 1:
             return last_match_s(context, recv, args[0]);
         default:
             Arity.raiseArgumentError(recv.getRuntime(), args.length, 0, 1);
             return null; // not reached
         }
     }
 
     /** rb_reg_s_last_match / match_getter
     *
     */
     @JRubyMethod(name = "last_match", meta = true, reads = BACKREF)
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv) {
         IRubyObject match = context.getCurrentFrame().getBackRef();
         if (match instanceof RubyMatchData) ((RubyMatchData)match).use();
         return match;
     }
 
     /** rb_reg_s_last_match
     *
     */
     @JRubyMethod(name = "last_match", meta = true, reads = BACKREF)
     public static IRubyObject last_match_s(ThreadContext context, IRubyObject recv, IRubyObject nth) {
         IRubyObject match = context.getCurrentFrame().getBackRef();
         if (match.isNil()) return match;
         return nth_match(((RubyMatchData)match).backrefNumber(nth), match);
     }
 
     /** rb_reg_match_pre
      *
      */
     public static IRubyObject match_pre(IRubyObject match) {
         if (match.isNil()) return match;
         RubyMatchData m = (RubyMatchData)match;
         
         int beg = m.regs == null ? m.begin : m.regs.beg[0];
         
         if(beg == -1) match.getRuntime().getNil(); 
 
         RubyString str = m.str.makeShared(0, beg);
         str.infectBy(match);
         return str;
     }
 
     /** rb_reg_match_post
      *
      */
     public static IRubyObject match_post(IRubyObject match) {
         if (match.isNil()) return match;
         RubyMatchData m = (RubyMatchData)match;
 
         int end;
         if (m.regs == null) {
             if (m.begin == -1) return match.getRuntime().getNil();
             end = m.end;
         } else {
             if (m.regs.beg[0] == -1) return match.getRuntime().getNil();
             end = m.regs.end[0];
         }
         
         RubyString str = m.str.makeShared(end, m.str.getByteList().realSize - end);
         str.infectBy(match);
         return str;
     }
 
     /** rb_reg_match_last
      *
      */
     public static IRubyObject match_last(IRubyObject match) {
         if (match.isNil()) return match;
         RubyMatchData m = (RubyMatchData)match;
 
         if (m.regs == null || m.regs.beg[0] == -1) return match.getRuntime().getNil();
 
         int i;
         for (i=m.regs.numRegs-1; m.regs.beg[i]==-1 && i>0; i--);
         if (i == 0) return match.getRuntime().getNil();
         
         return nth_match(i,match);
     }
 
     /** rb_reg_s_union
      *
      */
     @JRubyMethod(name = "union", rest = true, meta = true)
     public static IRubyObject union(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             return newRegexp(recv.getRuntime(), ByteList.create("(?!)"), 0, false);
         } else if (args.length == 1) {
             IRubyObject v = TypeConverter.convertToTypeWithCheck(args[0], recv.getRuntime().getRegexp(), 0, "to_regexp");
             if(!v.isNil()) {
                 return v;
             } else {
                 // newInstance here
                 return newRegexp(recv.getRuntime(), quote(recv,args).getByteList(), 0, false);
             }
         } else {
             KCode kcode = null;
             IRubyObject kcode_re = recv.getRuntime().getNil();
             RubyString source = recv.getRuntime().newString();
             IRubyObject[] _args = new IRubyObject[3];
 
             for(int i = 0; i < args.length; i++) {
                 if(0 < i) {
                     source.cat(PIPE);
                 }
                 IRubyObject v = TypeConverter.convertToTypeWithCheck(args[i], recv.getRuntime().getRegexp(), 0, "to_regexp");
                 if (!v.isNil()) {
                     if (!((RubyRegexp)v).isKCodeDefault()) {
                         if (kcode == null) {
                             kcode_re = v;
                             kcode = ((RubyRegexp)v).kcode;
                         } else if (((RubyRegexp)v).kcode != kcode) {
                             IRubyObject str1 = kcode_re.inspect();
                             IRubyObject str2 = v.inspect();
                             throw recv.getRuntime().newArgumentError("mixed kcode " + str1 + " and " + str2);
                         }
                     }
                     v = ((RubyRegexp)v).to_s();
                 } else {
                     v = quote(recv, new IRubyObject[]{args[i]});
                 }
                 source.append(v);
             }
 
             _args[0] = source;
             _args[1] = recv.getRuntime().getNil();
             if (kcode == null) {
                 _args[2] = recv.getRuntime().getNil();
             } else if (kcode == KCode.NONE) {
                 _args[2] = recv.getRuntime().newString("n");
             } else if (kcode == KCode.EUC) {
                 _args[2] = recv.getRuntime().newString("e");
             } else if (kcode == KCode.SJIS) {
                 _args[2] = recv.getRuntime().newString("s");
             } else if (kcode == KCode.UTF8) {
                 _args[2] = recv.getRuntime().newString("u");
             }
             return recv.callMethod(context, "new", _args);
         }
     }
 
     /** rb_reg_names
      * 
      */
     @JRubyMethod(name = "names", compat = CompatVersion.RUBY1_9)
     public IRubyObject names() {
         if (pattern.numberOfNames() == 0) return getRuntime().newEmptyArray();
 
         RubyArray ary = getRuntime().newArray(pattern.numberOfNames());
         for (Iterator<NameEntry> i = pattern.namedBackrefIterator(); i.hasNext();) {
             NameEntry e = i.next();
             ary.append(RubyString.newStringShared(getRuntime(), e.name, e.nameP, e.nameEnd - e.nameP));
         }
         return ary;
     }
 
     /** rb_reg_named_captures
      * 
      */
     @JRubyMethod(name = "named_captures", compat = CompatVersion.RUBY1_9)
     public IRubyObject named_captures() {
         RubyHash hash = RubyHash.newHash(getRuntime());
         if (pattern.numberOfNames() == 0) return hash;
 
         for (Iterator<NameEntry> i = pattern.namedBackrefIterator(); i.hasNext();) {
             NameEntry e = i.next();
             int[]backrefs = e.getBackRefs();
             RubyArray ary = getRuntime().newArray(backrefs.length);
 
             for (int backref : backrefs) ary.append(RubyFixnum.newFixnum(getRuntime(), backref));
             hash.fastASet(RubyString.newStringShared(getRuntime(), e.name, e.nameP, e.nameEnd - e.nameP).freeze(), ary);
         }
         return hash;
     }    
 
     public static RubyRegexp unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyRegexp result = newRegexp(input.getRuntime(), input.unmarshalString(), input.unmarshalInt(), false);
         input.registerLinkTarget(result);
         return result;
     }
 
     public static void marshalTo(RubyRegexp regexp, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(regexp);
         output.writeString(new String(regexp.str.bytes,regexp.str.begin,regexp.str.realSize));
         output.writeInt(regexp.pattern.getOptions() & EMBEDDABLE);
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 677b256984..3d3c32ce3b 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,924 +1,943 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
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
 
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyObject;
 import org.jruby.RubyKernel.CatchTarget;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public final class ThreadContext {
     public static synchronized ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         //        if(runtime.getInstanceConfig().isSamplingEnabled()) {
         //    org.jruby.util.SimpleSampler.registerThreadContext(context);
         //}
 
         return context;
     }
     
     private final static int INITIAL_SIZE = 50;
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     // Error info is per-thread
     private IRubyObject errorInfo;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private CatchTarget[] catchStack = new CatchTarget[INITIAL_SIZE];
     private int catchIndex = -1;
     
     // File where current executing unit is being evaluated
     private String file = "";
     
     // Line where current executing unit is being evaluated
     private int line = 0;
+
+    // In certain places, like grep, we don't use real frames for the
+    // call blocks. This has the effect of not setting the backref in
+    // the correct frame - this delta is activated to the place where
+    // the grep is running in so that the backref will be set in an
+    // appropriate place.
+    private int rubyFrameDelta = 0;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
         // init errorInfo to nil
         errorInfo = runtime.getNil();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
             
         for (int i = 0; i < frameStack.length; i++) {
             frameStack[i] = new Frame();
         }
     }
 
     @Override
     protected void finalize() throws Throwable {
         thread.dispose();
     }
     
     CallType lastCallType;
     
     Visibility lastVisibility;
     
     IRubyObject lastExitStatus;
     
     public final Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return errorInfo;
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         this.errorInfo = errorInfo;
         return errorInfo;
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
 
     public CallType getLastCallType() {
         return lastCallType;
     }
 
     public void setLastVisibility(Visibility visibility) {
         lastVisibility = visibility;
     }
 
     public Visibility getLastVisibility() {
         return lastVisibility;
     }
     
     public IRubyObject getLastExitStatus() {
         return lastExitStatus;
     }
     
     public void setLastExitStatus(IRubyObject lastExitStatus) {
         this.lastExitStatus = lastExitStatus;
     }
 
     public void printScope() {
         System.out.println("SCOPE STACK:");
         for (int i = 0; i <= scopeIndex; i++) {
             System.out.println(scopeStack[i]);
         }
     }
 
     public DynamicScope getCurrentScope() {
         return scopeStack[scopeIndex];
     }
     
     public DynamicScope getPreviousScope() {
         return scopeStack[scopeIndex - 1];
     }
     
     private void expandFramesIfNecessary(int newMax) {
         if (newMax == frameStack.length) {
             int newSize = frameStack.length * 2;
             Frame[] newFrameStack = new Frame[newSize];
             
             System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
             
             for (int i = frameStack.length; i < newSize; i++) {
                 newFrameStack[i] = new Frame();
             }
             
             frameStack = newFrameStack;
         }
     }
     
     private void expandParentsIfNecessary() {
         if (parentIndex + 1 == parentStack.length) {
             int newSize = parentStack.length * 2;
             RubyModule[] newParentStack = new RubyModule[newSize];
             
             System.arraycopy(parentStack, 0, newParentStack, 0, parentStack.length);
             
             parentStack = newParentStack;
         }
     }
     
     public void pushScope(DynamicScope scope) {
         scopeStack[++scopeIndex] = scope;
         expandScopesIfNecessary();
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
     
     private void expandScopesIfNecessary() {
         if (scopeIndex + 1 == scopeStack.length) {
             int newSize = scopeStack.length * 2;
             DynamicScope[] newScopeStack = new DynamicScope[newSize];
             
             System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
             
             scopeStack = newScopeStack;
         }
     }
     
     public RubyThread getThread() {
         return thread;
     }
     
     public void setThread(RubyThread thread) {
         this.thread = thread;
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
 //    public IRubyObject getLastline() {
 //        IRubyObject value = getCurrentScope().getLastLine();
 //        
 //        // DynamicScope does not preinitialize these values since they are virtually never used.
 //        return value == null ? runtime.getNil() : value;
 //    }
 //    
 //    public void setLastline(IRubyObject value) {
 //        getCurrentScope().setLastLine(value);
 //    }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         if (catchIndex + 1 == catchStack.length) {
             int newSize = catchStack.length * 2;
             CatchTarget[] newCatchStack = new CatchTarget[newSize];
             
             System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
             catchStack = newCatchStack;
         }
     }
     
     public void pushCatch(CatchTarget catchTarget) {
         catchStack[++catchIndex] = catchTarget;
         expandCatchIfNecessary();
     }
     
     public void popCatch() {
         catchIndex--;
     }
     
     public CatchTarget[] getActiveCatches() {
         if (catchIndex < 0) return new CatchTarget[0];
         
         CatchTarget[] activeCatches = new CatchTarget[catchIndex + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, catchIndex + 1);
         return activeCatches;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         Frame currentFrame = getCurrentFrame();
         frameStack[++frameIndex].updateFrame(currentFrame);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private Frame pushFrameCopy(Frame frame) {
         frameStack[++frameIndex].updateFrame(frame);
         expandFramesIfNecessary(frameIndex + 1);
         return frameStack[frameIndex];
     }
     
     private void pushFrame(Frame frame) {
         frameStack[++frameIndex] = frame;
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block, JumpTarget jumpTarget) {
         pushFrame(clazz, name, self, block, jumpTarget);        
     }
     
     private void pushBacktraceFrame(String name) {
         pushFrame(name);        
     }
     
     private void pushFrame(String name) {
         frameStack[++frameIndex].updateFrame(name, file, line);
         expandFramesIfNecessary(frameIndex + 1);
     }
 
     private void pushFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block, JumpTarget jumpTarget) {
         frameStack[++frameIndex].updateFrame(clazz, self, name, block, file, line, jumpTarget);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void pushFrame() {
         frameStack[++frameIndex].updateFrame(file, line);
         expandFramesIfNecessary(frameIndex + 1);
     }
     
     private void popFrame() {
         Frame frame = frameStack[frameIndex];
         frameIndex--;
         setFile(frame.getFile());
         setLine(frame.getLine());
     }
         
     private void popFrameReal() {
         Frame frame = frameStack[frameIndex];
         frameStack[frameIndex] = new Frame();
         frameIndex--;
         setFile(frame.getFile());
         setLine(frame.getLine());
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
+
+    public int getRubyFrameDelta() {
+        return this.rubyFrameDelta;
+    }
+    
+    public void setRubyFrameDelta(int newDelta) {
+        this.rubyFrameDelta = newDelta;
+    }
+
+    public Frame getCurrentRubyFrame() {
+        return frameStack[frameIndex-rubyFrameDelta];
+    }
     
     public Frame getNextFrame() {
         expandFramesIfNecessary(frameIndex + 1);
         return frameStack[frameIndex + 1];
     }
     
     public Frame getPreviousFrame() {
         int size = frameIndex + 1;
         return size <= 1 ? null : frameStack[size - 2];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public JumpTarget getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public void setFrameJumpTarget(JumpTarget target) {
         getCurrentFrame().setJumpTarget(target);
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return file;
     }
     
     public int getLine() {
         return line;
     }
     
     public void setFile(String file) {
         this.file = file;
     }
     
     public void setLine(int line) {
         this.line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         this.file = file;
         this.line = line;
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
     }
     
     public void pollThreadEvents() {
         getThread().pollThreadEvents(this);
     }
     
     int calls = 0;
     
     public void callThreadPoll() {
         if ((calls++ & 0xFF) == 0) pollThreadEvents();
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         
         parentStack[++parentIndex] = currentModule;
         expandParentsIfNecessary();
     }
     
     public RubyModule popRubyClass() {
         RubyModule ret = parentStack[parentIndex];
         parentStack[parentIndex--] = null;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = parentStack[parentIndex];
         
         return parentModule.getNonIncludedClass();
     }
     
     public RubyModule getBindingRubyClass() {
         RubyModule parentModule = null;
         if(parentIndex == 0) {
             parentModule = parentStack[parentIndex];
         } else {
             parentModule = parentStack[parentIndex-1];
             
         }
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject result;
         
         // flipped from while to do to search current class first
         for (StaticScope scope = getCurrentScope().getStaticScope(); scope != null; scope = scope.getPreviousCRefScope()) {
             RubyModule module = scope.getModule();
             if ((result = module.fastFetchConstant(internedName)) != null) {
                 if (result != RubyObject.UNDEF) return true;
                 return runtime.getLoadService().autoloadFor(module.getName() + "::" + internedName) != null;
             }
         }
         
         return getCurrentScope().getStaticScope().getModule().fastIsConstantDefined(internedName);
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         StaticScope scope = getCurrentScope().getStaticScope();
         RubyClass object = runtime.getObject();
         IRubyObject result;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = scope.getModule();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             if ((result = klass.fastFetchConstant(internedName)) != null) {
                 if (result != RubyObject.UNDEF) {
                     return result;
                 }
                 klass.deleteConstant(internedName);
                 if (runtime.getLoadService().autoload(klass.getName() + "::" + internedName) == null) break;
                 continue;
             }
             scope = scope.getPreviousCRefScope();
         } while (scope != null && scope.getModule() != object);
         
         return getCurrentScope().getStaticScope().getModule().fastGetConstant(internedName);
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String internedName, IRubyObject result) {
         RubyModule module;
 
         if ((module = getCurrentScope().getStaticScope().getModule()) != null) {
             module.fastSetConstant(internedName, result);
             return result;
         }
 
         // TODO: wire into new exception handling mechanism
         throw runtime.newTypeError("no class/module to define constant");
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInModule(String internedName, IRubyObject target, IRubyObject result) {
         if (!(target instanceof RubyModule)) {
             throw runtime.newTypeError(target.toString() + " is not a class/module");
         }
         RubyModule module = (RubyModule)target;
         module.fastSetConstant(internedName, result);
         
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String internedName, IRubyObject result) {
         runtime.getObject().fastSetConstant(internedName, result);
         
         return result;
     }
     
     private static void addBackTraceElement(RubyArray backtrace, Frame frame, Frame previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getName() != null && 
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getFile().equals(previousFrame.getFile()) &&
                 frame.getLine() == previousFrame.getLine()) {
             return;
         }
         
         StringBuffer buf = new StringBuffer(60);
         buf.append(frame.getFile()).append(':').append(frame.getLine() + 1);
         
         if (previousFrame.getName() != null) {
             buf.append(":in `").append(previousFrame.getName()).append('\'');
         }
         
         backtrace.append(backtrace.getRuntime().newString(buf.toString()));
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, Frame[] backtraceFrames) {
         RubyArray backtrace = runtime.newArray();
         
         if (backtraceFrames == null || backtraceFrames.length <= 0) return backtrace;
         
         int traceSize = backtraceFrames.length;
 
         for (int i = traceSize - 1; i > 0; i--) {
             Frame frame = backtraceFrames[i];
             // We are in eval with binding break out early
             if (frame.isBindingFrame()) break;
 
             addBackTraceElement(backtrace, frame, backtraceFrames[i - 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Frame[] createBacktrace(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         Frame[] traceFrames;
         
         if (traceSize <= 0) return null;
         
         if (nativeException) {
             // assert level == 0;
             traceFrames = new Frame[traceSize + 1];
             traceFrames[traceSize] = frameStack[frameIndex];
         } else {
             traceFrames = new Frame[traceSize];
         }
         
         System.arraycopy(frameStack, 0, traceFrames, 0, traceSize);
         
         return traceFrames;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type, String[] scopeNames) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         StaticScope staticScope = new LocalStaticScope(getCurrentScope().getStaticScope(), scopeNames);
         staticScope.setModule(type);
         pushScope(new ManyVarsDynamicScope(staticScope, null));
     }
     
     public void postCompiledClass() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preScopeNode(StaticScope staticScope) {
         pushScope(DynamicScope.newDynamicScope(staticScope, getCurrentScope()));
     }
 
     public void postScopeNode() {
         popScope();
     }
 
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         pushScope(DynamicScope.newDynamicScope(staticScope, null));
     }
     
     public void postClassEval() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         LocalStaticScope staticScope = new LocalStaticScope(null);
         staticScope.setVariables(names);
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope, JumpTarget jumpTarget) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block, jumpTarget);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, Block block,
             JumpTarget jumpTarget) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block, jumpTarget);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postMethodFrameOnly() {
         popFrame();
         popRubyClass();
     }
     
     public void preMethodScopeOnly(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodScopeOnly() {
         popRubyClass();
         popScope();
     }
     
     public void preMethodBacktraceAndScope(String name, RubyModule clazz, StaticScope staticScope) {
         preMethodScopeOnly(clazz, staticScope);
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
         popFrame();
     }
     
     public void preMethodBacktraceOnly(String name) {
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceOnly() {
         popFrame();
     }
     
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         
         pushRubyClass(objectClass);
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self, String name) {
         setFile(name);
         pushRubyClass(rubyClass);
         pushCallFrame(null, name, self, Block.NULL_BLOCK, null);
         // set visibility to private, since toplevel of scripts always started out private
         setCurrentVisibility(Visibility.PRIVATE);
     }
 
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushCallFrame(null, null, self, Block.NULL_BLOCK, null);
         // set visibility to private, since toplevel of scripts always started out private
         setCurrentVisibility(Visibility.PRIVATE);
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         DynamicScope scope = getCurrentScope();
         StaticScope sScope = new BlockStaticScope(scope.getStaticScope());
         sScope.setModule(executeUnderClass);
         pushScope(DynamicScope.newDynamicScope(sScope, scope));
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block, frame.getJumpTarget());
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popScope();
         popRubyClass();
     }
     
     public void preMproc() {
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
     }
     
     public void preRunThread(Frame currentFrame) {
         pushFrame(currentFrame);
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public void preForBlock(Binding binding, RubyModule klass) {
         Frame f = binding.getFrame();
         f.setFile(file);
         f.setLine(line);
         pushFrame(f);
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushScope(binding.getDynamicScope());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         Frame f = pushFrameCopy(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         Frame f = pushFrameCopy(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldNoScope(Binding binding, RubyModule klass) {
         Frame f = pushFrameCopy(binding.getFrame());
         f.setFile(file);
         f.setLine(line);
         f.setVisibility(binding.getVisibility());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preEvalWithBinding(Binding binding) {
         Frame frame = binding.getFrame();
         frame.setIsBindingFrame(true);
         pushFrame(frame);
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushRubyClass(binding.getKlass());
     }
     
     public void postEvalWithBinding(Binding binding) {
         binding.getFrame().setIsBindingFrame(false);
         popFrameReal();
         popRubyClass();
     }
     
     public void postYield(Binding binding) {
         popScope();
         popFrameReal();
         popRubyClass();
     }
     
     public void postYieldLight(Binding binding) {
         popScope();
         popFrameReal();
         popRubyClass();
     }
     
     public void postYieldNoScope() {
         popFrameReal();
         popRubyClass();
     }
     
     public void preScopedBody(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postScopedBody() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Is this thread actively in defined? at the moment.
      *
      * @return true if within defined?
      */
     public boolean isWithinDefined() {
         return isWithinDefined;
     }
     
     /**
      * Set whether we are actively within defined? or not.
      *
      * @param isWithinDefined true if so
      */
     public void setWithinDefined(boolean isWithinDefined) {
         this.isWithinDefined = isWithinDefined;
     }
 }
diff --git a/test/testRegexp.rb b/test/testRegexp.rb
index de7d2db457..21dfe42321 100644
--- a/test/testRegexp.rb
+++ b/test/testRegexp.rb
@@ -1,204 +1,219 @@
 require 'test/minirunit'
 test_check "Test regexp substitutions:"
 
 #########    test1   #################
 rgx1 = /[a-z]+/
 str1 = "redrum".sub(rgx1, "<\\&>")
 test_ok(str1=='<redrum>')
 
 #########    test2   #################
 str1.sub!(/\w+/) { |m| $` + m.upcase + $' }
 
 #########    test3   #################
 test_ok(str1=='<<REDRUM>>')
 
 #########    test4   #################
 test_ok('*R*U*B*Y*' ==  "ruby".upcase.gsub(/\d?/, '*'))
 
 #########    test5   #################
 str3 = "regnad kcin".reverse
 str3.gsub!(/\w+/) { |m| m.capitalize }
 test_ok('Nick Danger' == str3)
 
 #########    test6   #################
 str4 =  'B'
 test_ok(0 == (str4 =~ /^(?:(A):)?(B)/))
 test_ok(nil == $1)
 test_ok(str4 == $2)
 
 #########    test7   #################
 test_ok("(?-mix:pattern)" == /pattern/.to_s)
 test_ok("(?m-ix:pattern)" == /pattern/m.to_s)
 test_ok("(?mix:pattern)" == /pattern/mix.to_s)
 
 #########    test8   #################
 test_ok(/ab (?# comment )c/ =~ 'ab c')
 
 #########    test9   #################
 test_ok("\tZ"   =~ /\x9Z/)
 test_ok("\t"   =~ /\x9/)
 test_ok("\tZ\tQ"   =~ /\x9Z\x9Q/)
 test_ok("\x9cZ" =~ /\x9cZ/)
 
 #########    test10   #################
 'ABCDE' =~ /B(C)D/
 test_equal('BCD', $~.to_s)
 
 "ALBUM: Foo Bar".match(/ALBUM: [^\s]*\s(.+)/)
 test_equal('Bar', $1)
 
 ######## MatchData #############
 match_data = /(.)(.)(\d+)(\d)/.match("THX1138")
 
 test_equal(["HX1138", "H", "X", "113", "8"], match_data.to_a)
 test_equal(["H", "X", "113", "8"], match_data.captures)
 
 ##### === ######
 test_equal(false, /a/ === :a)
 test_equal(false, /aa/ === ['a' => 'a'])
 test_equal(false, :a =~ /a/)
 test_equal(false, ['a' => 'a'] =~ /a/)
 
 ##### inspect #####
 re = /^admin\/.+$/
 
 test_equal("^admin\\/.+$", re.source)
 test_equal("/^admin\\/.+$/", re.inspect)
 
 re = Regexp.new("/hi/")
 test_equal("/hi/", re.source)
 test_equal("/\\/hi\\//", re.inspect)
 
 ##### Posix sequences ######
 "a  b" =~ /([[:space:]]+)/
 test_equal("  ", $1)
 # We should only honor this as posix sequence inside [] (bug 1475096)
 #test_equal(0, "a  b" =~ /([:space:]+)/)
 
 ##### union #####
 test_equal(/(?!)/, Regexp.union)
 test_equal(/penzance/, Regexp.union("penzance"))
 test_equal(/skiing|sledding/, Regexp.union("skiing", "sledding"))
 test_equal(/(?-mix:dogs)|(?i-mx:cats)/, Regexp.union(/dogs/, /cats/i))
 
 # copied from MRI sample/test.rb
 a = []
 (0..255).each {|n|
   ch = [n].pack("C")                     
   a.push ch if /a#{Regexp.quote ch}b/x =~ "ab" 
 }
 test_ok(a.size == 0)
 
 # In Ruby 1.8.5, quote can take an optional encoding arg
 test_equal("hel\\\\l\\*o\317\200", Regexp.quote("hel\\l*o\317\200", "n"))
 test_equal("hel\\\\l\\*o\317\200", Regexp.quote("hel\\l*o\317\200", "u"))
 
 # test matching \r
 test_equal("\r", /./.match("\r")[0])
 
 #test_exception(SyntaxError) { r = /*/ }
 
 test_equal(/a/.hash, /a/.hash)
 
 test_equal("\\-", Regexp.quote('-'))
 
 # JRUBY-722
 /a((abc)|(foo))d/ =~ "afood"
 test_equal ["foo", nil, "foo"], $~.captures
 
 # JRUBY-741
 test_equal "foo", Regexp.last_match(1)
 
 # JRUBY-717
 test_equal nil, /./ =~ "\n"
 test_equal 0, /(?m:.)/ =~ "\n"
 
 
 NAME_STR= '[\w]*'
 TAG_MATCH = /^<#{NAME_STR}\s*>/u
 input = <<-EOL
 <path
   >
 EOL
 test_ok TAG_MATCH =~ input
 
 xy = /(.*).*\1\}/
 xy =~ "12}"
 
 /1{2}+/ =~ "1111"
 test_equal $&, "1111"
 /1{2}+/ =~ "111111111111"
 test_equal $&, "111111111111"
 /1{2}+/ =~ "111"
 test_equal $&, "11"
 
 # JRUBY-139: don't show result of internal JRuby translations
 test_equal("/[:alpha:]/", %r{[:alpha:]}.inspect)
 test_equal("[:alpha:]", %r{[:alpha:]}.source)
 
 # Why anyone would do this I have no idea, but it matches MRI
 test_equal(/x/, +/x/)
 
 
 def m(it = false)
   a = /a/
   a.instance_variable_set :@set, true if it
   a
 end
 
 test_equal nil, m().instance_variable_get(:@set)
 test_equal true, m(true).instance_variable_get(:@set)
 test_equal true, m().instance_variable_get(:@set)
 
 # JRUBY-1046: Support \G correctly:
 test_equal ["aa1 ", "aa2 "], "aa1 aa2 ba3 ".scan(/\Ga+\d\s*/)
 
 # JRUBY-1109: Octal literals eat next character...
 test_equal 0, "\034\015" =~ /^\034\015$/
 
 test_equal 0, /\0/ =~ "\0"
 test_equal 0, /\00/ =~ "\0"
 test_equal 0, /\00/ =~ "\0"
 
 # JRUBY-1372: Regexp.quoting
 old_kcode = $KCODE
 $KCODE = 'u'
 helpers_dir = "/my/happy/helpers"
 extract = /^#{Regexp.quote(helpers_dir)}\/?(.*)_helper.rb$/
 test_equal "/^\\/my\\/happy\\/helpers\\/?(.*)_helper.rb$/", extract.inspect
 $KCODE = old_kcode
 
 # JRUBY-1385: String.match shouldn't return the same object
 pattern = /\w/
 ext = ["foo", "bar"].map {|e| e.match(pattern) }
 test_ok ext[0] != ext[1]
 
 # MatchData#select behavior
 # FIXME: the select behavior below matches MRI rdoc, but not MRI behavior
 m = /(.)(.)(\d+)(\d)/.match("THX1138: The Movie")
 test_equal(["HX1138", "H", "X", "113", "8"], m.to_a)
 #test_equal(["HX1138", "H", "X", "113", "8"], m.select {})
 #test_equal(["HX1138", "X", "113"], m.select(0, 2, -2) {})
 
 # JRUBY-1236
 test_equal(0, "\n" =~ /\s/n)
 
 # JRUBY-1552
 test_equal(1, Array('a'..'z').map { |c| c.to_s[/#{c}/o] }.compact.size)
 test_equal(26, Array('a'..'z').map { |c| c.to_s[/#{c}/] }.compact.size)
 
 test_equal nil, /[^a]/i =~ "a"
 test_equal nil, /[^a]/i =~ "A"
 test_equal nil, /[^A]/i =~ "a"
 test_equal nil, /[^A]/i =~ "A"
 
 test_equal nil, /[^a-c]/i =~ "b"
 test_equal nil, /[^a-c]/i =~ "B"
 test_equal nil, /[^A-C]/i =~ "b"
 test_equal nil, /[^A-C]/i =~ "B"
 
 test_ok Regexp.new("foo").send(:initialize, "bar")
 test_ok Regexp.new("foo").send(:initialize_copy, Regexp.new("bar"))
 test_exception(SecurityError){/foo/.send(:initialize, "bar")}
 test_exception(SecurityError){/foo/.send(:initialize_copy, Regexp.new("bar"))}
 test_no_exception{Regexp.new("a", 0, "")}
+
+
+/c(.)t/ =~ 'cat'                    
+test_equal MatchData, Regexp.last_match.class
+test_equal 'cat', Regexp.last_match(0)
+test_equal 'a', Regexp.last_match(1)
+test_equal nil, Regexp.last_match(2)
+
+x = ["fb"]
+poo = /^f(.+)$/
+test_equal ['b'], x.grep(poo){|z| Regexp.last_match(1)}
+
+x = "fb".."fc"
+poo = /^f(.+)$/
+test_equal ['b','c'], x.grep(poo){|z| Regexp.last_match(1)}
