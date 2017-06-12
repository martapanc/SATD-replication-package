diff --git a/rubyspecs.revision b/rubyspecs.revision
index 7727ee0617..40626fb306 100644
--- a/rubyspecs.revision
+++ b/rubyspecs.revision
@@ -1,6 +1,6 @@
 # These are the pointers to the 'stable/frozen' versions of
 # mspec and rubyspecs, used to for our CI runs.
 
 mspec.revision=0faab128f7b7b9cebe71a48cce8f4b7502ab2995
 
-rubyspecs.revision=b3cc1076bd9f3781b116d5041bbc272674c4b6d4
+rubyspecs.revision=71a0fbeee14056b2896c2f69cd81b1423aa9705a
diff --git a/spec/tags/ruby/language/next_tags.txt b/spec/tags/ruby/language/next_tags.txt
index c44e394f81..3b5c1a7d4f 100644
--- a/spec/tags/ruby/language/next_tags.txt
+++ b/spec/tags/ruby/language/next_tags.txt
@@ -1,2 +1 @@
 fails:The next statement raises a LocalJumpError if used not within block or while/for loop
-fails(JRUBY-1872):The next statement from within the block returns the argument passed
diff --git a/src/org/jruby/runtime/CompiledBlock.java b/src/org/jruby/runtime/CompiledBlock.java
index 1edcbe1eb2..ee81e2afc3 100644
--- a/src/org/jruby/runtime/CompiledBlock.java
+++ b/src/org/jruby/runtime/CompiledBlock.java
@@ -1,238 +1,238 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlock extends BlockBody {
     protected final CompiledBlockCallback callback;
     protected final boolean hasMultipleArgsHead;
     protected final Arity arity;
     protected final StaticScope scope;
     
     public static Block newCompiledClosure(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
         BlockBody body = new CompiledBlock(arity, scope, callback, hasMultipleArgsHead, argumentType);
         
         return new Block(body, binding);
     }
     
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, Arity arity,
             StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         return newCompiledClosure(
                 self,
                 context.getCurrentFrame(),
                 Visibility.PUBLIC,
                 context.getRubyClass(),
                 context.getCurrentScope(),
                 arity,
                 scope,
                 callback,
                 hasMultipleArgsHead,
                 argumentType);
     }
     
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, BlockBody body) {
         Binding binding = new Binding(self, context.getCurrentFrame(), Visibility.PUBLIC, context.getRubyClass(), context.getCurrentScope());
         return new Block(body, binding);
     }
     
     public static BlockBody newCompiledBlock(Arity arity,
             StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         return new CompiledBlock(arity, scope, callback, hasMultipleArgsHead, argumentType);
     }
 
     protected CompiledBlock(Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         super(argumentType);
         this.arity = arity;
         this.scope = scope;
         this.callback = callback;
         this.hasMultipleArgsHead = hasMultipleArgsHead;
     }
 
     @Override
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yield(context, null, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yield(context, arg0, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1), null, null, true, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1, arg2), null, null, true, binding, type);
     }
 
     @Override
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
 
         IRubyObject realArg = setupBlockArg(context.getRuntime(), value, self); 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
         
         try {
             return callback.call(context, self, realArg);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = prepareSelf(binding);
         }
 
         IRubyObject realArg = aValue ? 
                 setupBlockArgs(context, args, self) : setupBlockArg(context.getRuntime(), args, self); 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, klass, binding);
         
         try {
             return callback.call(context, self, realArg);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
     
     private IRubyObject prepareSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
         
         return self;
     }
-    
+
     private IRubyObject handleNextJump(ThreadContext context, JumpException.NextJump nj, Block.Type type) {
-        return type == Block.Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
+        return nj.getValue() == null ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
     }
     
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldSpecificBlock(binding, scope, klass);
     }
     
     protected void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYield(binding, lastFrame);
     }
 
     protected IRubyObject setupBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return null;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return value;
         default:
             return defaultArgsLogic(context.getRuntime(), value);
         }
     }
     
     private IRubyObject defaultArgsLogic(Ruby ruby, IRubyObject value) {
         int length = ArgsUtil.arrayLength(value);
         switch (length) {
         case 0:
             return ruby.getNil();
         case 1:
             return ((RubyArray)value).eltInternal(0);
         default:
             blockArgWarning(ruby, length);
         }
         return value;
     }
     
     private void blockArgWarning(Ruby ruby, int length) {
         ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (" +
                     length + " for 1)");
     }
 
     protected IRubyObject setupBlockArg(Ruby ruby, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return null;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return ArgsUtil.convertToRubyArray(ruby, value, hasMultipleArgsHead);
         default:
             return defaultArgLogic(ruby, value);
         }
     }
     
     private IRubyObject defaultArgLogic(Ruby ruby, IRubyObject value) {
         if (value == null) {
             return warnMultiReturnNil(ruby);
         }
         return value;
     }
     
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Block cloneBlock(Binding binding) {
         binding = new Binding(binding.getSelf(),
                 binding.getFrame(),
                 binding.getVisibility(),
                 binding.getKlass(),
                 binding.getDynamicScope());
         
         return new Block(this, binding);
     }
 
     @Override
     public Arity arity() {
         return arity;
     }
 
     private IRubyObject warnMultiReturnNil(Ruby ruby) {
         ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (0 for 1)");
         return ruby.getNil();
     }
 }
diff --git a/src/org/jruby/runtime/CompiledBlock19.java b/src/org/jruby/runtime/CompiledBlock19.java
index a379c4ecc9..48728c480e 100644
--- a/src/org/jruby/runtime/CompiledBlock19.java
+++ b/src/org/jruby/runtime/CompiledBlock19.java
@@ -1,279 +1,279 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlock19 extends BlockBody {
     protected final CompiledBlockCallback19 callback;
     protected final boolean hasMultipleArgsHead;
     protected final Arity arity;
     protected final StaticScope scope;
     
     public static Block newCompiledClosure(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
         DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
         Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
         BlockBody body = new CompiledBlock19(arity, scope, callback, hasMultipleArgsHead, argumentType);
         
         return new Block(body, binding);
     }
     
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, Arity arity,
             StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
         return newCompiledClosure(
                 self,
                 context.getCurrentFrame(),
                 Visibility.PUBLIC,
                 context.getRubyClass(),
                 context.getCurrentScope(),
                 arity,
                 scope,
                 callback,
                 hasMultipleArgsHead,
                 argumentType);
     }
     
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, BlockBody body) {
         Binding binding = new Binding(self, context.getCurrentFrame(), Visibility.PUBLIC, context.getRubyClass(), context.getCurrentScope());
         return new Block(body, binding);
     }
     
     public static BlockBody newCompiledBlock(Arity arity,
             StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
         return new CompiledBlock19(arity, scope, callback, hasMultipleArgsHead, argumentType);
     }
 
     protected CompiledBlock19(Arity arity, StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
         super(argumentType);
         this.arity = arity;
         this.scope = scope;
         this.callback = callback;
         this.hasMultipleArgsHead = hasMultipleArgsHead;
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         IRubyObject value = args.length == 1 ? args[0] : context.getRuntime().newArrayNoCopy(args);
 
         return yield(context, value, null, null, true, binding, type, Block.NULL_BLOCK);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type, Block block) {
         IRubyObject value = args.length == 1 ? args[0] : context.getRuntime().newArrayNoCopy(args);
 
         return yield(context, value, null, null, true, binding, type, block);
     }
 
     @Override
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yieldSpecificInternal(context, IRubyObject.NULL_ARRAY, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yieldSpecificInternal(context, new IRubyObject[] {arg0}, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yieldSpecificInternal(context, new IRubyObject[] {arg0, arg1}, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yieldSpecificInternal(context, new IRubyObject[] {arg0, arg1, arg2}, binding, type);
     }
 
     private IRubyObject yieldSpecificInternal(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
 
         try {
             return callback.call(context, self, args, Block.NULL_BLOCK);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     @Override
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
 
         IRubyObject[] realArgs = setupBlockArg(context.getRuntime(), value, self);
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
         
         try {
             return callback.call(context, self, realArgs, Block.NULL_BLOCK);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         return yield(context, args, self, klass, aValue, binding, type, Block.NULL_BLOCK);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Block.Type type, Block block) {
         if (klass == null) {
             self = prepareSelf(binding);
         }
 
         IRubyObject[] realArgs = setupBlockArgs(args);
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, klass, binding);
         
         try {
             return callback.call(context, self, realArgs, block);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
     
     private IRubyObject prepareSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
         
         return self;
     }
-    
+
     private IRubyObject handleNextJump(ThreadContext context, JumpException.NextJump nj, Block.Type type) {
-        return type == Block.Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
+        return nj.getValue() == null ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
     }
     
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldSpecificBlock(binding, scope, klass);
     }
     
     protected void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYield(binding, lastFrame);
     }
 
     private IRubyObject[] setupBlockArgs(IRubyObject value) {
         IRubyObject[] parameters;
 
         if (value instanceof RubyArray) {// && args.getMaxArgumentsCount() != 1) {
             parameters = ((RubyArray) value).toJavaArray();
         } else {
             parameters = new IRubyObject[] { value };
         }
 
         return parameters;
 //        if (!(args instanceof ArgsNoArgNode)) {
 //            Ruby runtime = context.getRuntime();
 //
 //            // FIXME: This needs to happen for lambdas
 ////            args.checkArgCount(runtime, parameters.length);
 //            args.prepare(context, runtime, self, parameters, block);
 //        }
     }
     
     private IRubyObject defaultArgsLogic(Ruby ruby, IRubyObject value) {
         int length = ArgsUtil.arrayLength(value);
         switch (length) {
         case 0:
             return ruby.getNil();
         case 1:
             return ((RubyArray)value).eltInternal(0);
         default:
             blockArgWarning(ruby, length);
         }
         return value;
     }
     
     private void blockArgWarning(Ruby ruby, int length) {
         ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (" +
                     length + " for 1)");
     }
 
     protected IRubyObject[] setupBlockArg(Ruby ruby, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return null;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return ArgsUtil.convertToRubyArray(ruby, value, hasMultipleArgsHead).toJavaArray();
         default:
             return defaultArgLogic(ruby, value);
         }
     }
     
     private IRubyObject[] defaultArgLogic(Ruby ruby, IRubyObject value) {
         if (value == null) {
 //            return warnMultiReturnNil(ruby);
             return new IRubyObject[] {ruby.getNil()};
         }
         return new IRubyObject[] {value};
     }
 
     private IRubyObject[] warnMultiReturnNil(Ruby ruby) {
         ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (0 for 1)");
         return IRubyObject.NULL_ARRAY;
     }
     
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Block cloneBlock(Binding binding) {
         binding = new Binding(binding.getSelf(),
                 binding.getFrame(),
                 binding.getVisibility(),
                 binding.getKlass(),
                 binding.getDynamicScope());
         
         return new Block(this, binding);
     }
 
     @Override
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/Interpreted19Block.java b/src/org/jruby/runtime/Interpreted19Block.java
index fa057f6394..2377df11aa 100644
--- a/src/org/jruby/runtime/Interpreted19Block.java
+++ b/src/org/jruby/runtime/Interpreted19Block.java
@@ -1,259 +1,259 @@
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
  * Copyright (C) 2008 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.LambdaNode;
 import org.jruby.ast.NilImplicitNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.ArgsNoArgNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 /**
  *
  * @author enebo
  */
 public class Interpreted19Block  extends BlockBody {
     /** The argument list, pulled out of iterNode */
     private final ArgsNode args;
 
     /** The body of the block, pulled out of bodyNode */
     private final Node body;
 
     /** The static scope for the block body */
     private final StaticScope scope;
 
     /** The arity of the block */
     private final Arity arity;
 
     public static Block newInterpretedClosure(ThreadContext context, BlockBody body, IRubyObject self) {
         Frame frame = context.getCurrentFrame();
 
         Binding binding = new Binding(self,
                          frame,
                          frame.getVisibility(),
                          context.getRubyClass(),
                          context.getCurrentScope());
         return new Block(body, binding);
     }
 
     public Interpreted19Block(IterNode iter) {
         super(-1); // We override that the logic which uses this
 
         if (iter instanceof LambdaNode) {
             LambdaNode lambda = (LambdaNode)iter;
             
             this.arity = lambda.getArgs().getArity();
             this.args = lambda.getArgs();
             this.body = lambda.getBody() == null ? NilImplicitNode.NIL : lambda.getBody();
             this.scope = lambda.getScope();
         } else {
             ArgsNode argsNode = (ArgsNode) iter.getVarNode();
 
             if (argsNode == null) {
                 System.out.println("ITER HAS NO ARGS: " + iter);
             }
 
             this.arity = argsNode.getArity();
             this.args = argsNode;
             this.body = iter.getBodyNode() == null ? NilImplicitNode.NIL : iter.getBodyNode();
             this.scope = iter.getScope();
         }
     }
 
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldSpecificBlock(binding, scope, klass);
     }
 
     protected void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYield(binding, lastFrame);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         IRubyObject value = args.length == 1 ? args[0] : context.getRuntime().newArrayNoCopy(args);
 
         return yield(context, value, null, null, true, binding, type, Block.NULL_BLOCK);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type, Block block) {
         IRubyObject value = args.length == 1 ? args[0] : context.getRuntime().newArrayNoCopy(args);
 
         return yield(context, value, null, null, true, binding, type, block);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yield(context, null, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yield(context, arg0, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1), null, null, true, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1, arg2), null, null, true, binding, type);
     }
 
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
 
         try {
             setupBlockArgs(context, value, self, Block.NULL_BLOCK, type);
 
             return evalBlockBody(context, self);
         } catch (JumpException.NextJump nj) {
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      *
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self,
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         return yield(context, value, self, klass, aValue, binding, type, Block.NULL_BLOCK);
 
     }
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self,
             RubyModule klass, boolean aValue, Binding binding, Block.Type type, Block block) {
         if (klass == null) {
             self = prepareSelf(binding);
         }
 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, klass, binding);
 
         try {
             setupBlockArgs(context, value, self, block, type);
 
             // This while loop is for restarting the block call in case a 'redo' fires.
             return evalBlockBody(context, self);
         } catch (JumpException.NextJump nj) {
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     private IRubyObject evalBlockBody(ThreadContext context, IRubyObject self) {
         // This while loop is for restarting the block call in case a 'redo' fires.
         while (true) {
             try {
                 return body.interpret(context.getRuntime(), context, self, Block.NULL_BLOCK);
             } catch (JumpException.RedoJump rj) {
                 context.pollThreadEvents();
                 // do nothing, allow loop to redo
             } catch (StackOverflowError soe) {
                 throw context.getRuntime().newSystemStackError("stack level too deep", soe);
             }
         }
     }
 
     private IRubyObject prepareSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
 
         return self;
     }
 
     private IRubyObject handleNextJump(ThreadContext context, JumpException.NextJump nj, Block.Type type) {
-        return type == Block.Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
+        return nj.getValue() == null ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
     }
 
     private void setupBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self, Block block, Block.Type type) {
         IRubyObject[] parameters;
 
         if (value instanceof RubyArray && args.getMaxArgumentsCount() != 1) {
             parameters = ((RubyArray) value).toJavaArray();
         } else {
             parameters = new IRubyObject[] { value };
         }
 
         if (!(args instanceof ArgsNoArgNode)) {
             Ruby runtime = context.getRuntime();
 
             // FIXME: This needs to happen for lambdas
 //            args.checkArgCount(runtime, parameters.length);
             args.prepare(context, runtime, self, parameters, block);
         }
     }
 
     public Block cloneBlock(Binding binding) {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
         binding = new Binding(
                 binding.getSelf(),
                 binding.getFrame(),
                 binding.getVisibility(),
                 binding.getKlass(),
                 binding.getDynamicScope());
 
         return new Block(this, binding);
     }
 
     public ArgsNode getArgs() {
         return args;
     }
     
     public Node getBody() {
         return body;
     }
 
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/InterpretedBlock.java b/src/org/jruby/runtime/InterpretedBlock.java
index 1621fec453..22dcce969d 100644
--- a/src/org/jruby/runtime/InterpretedBlock.java
+++ b/src/org/jruby/runtime/InterpretedBlock.java
@@ -1,322 +1,322 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NilImplicitNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * This branch of the BlockBody hierarchy represents an interpreted block that
  * passes its AST nodes to the interpreter. It forms the top of the hierarchy
  * of interpreted blocks. In a typical application, it is the most heavily
  * consumed type of block.
  * 
  * @see SharedScopeBlock, CompiledBlock
  */
 public class InterpretedBlock extends BlockBody {
     /** The node wrapping the body and parameter list for this block */
     private final IterNode iterNode;
     
     /** Whether this block has an argument list or not */
     private final boolean hasVarNode;
     
     /** The argument list, pulled out of iterNode */
     private final Node varNode;
     
     /** The body of the block, pulled out of bodyNode */
     private final Node bodyNode;
     
     /** The static scope for the block body */
     private final StaticScope scope;
     
     /** The arity of the block */
     private final Arity arity;
 
     public static Block newInterpretedClosure(ThreadContext context, IterNode iterNode, IRubyObject self) {
         Frame f = context.getCurrentFrame();
 
         return newInterpretedClosure(iterNode,
                          self,
                          Arity.procArityOf(iterNode.getVarNode()),
                          f,
                          f.getVisibility(),
                          context.getRubyClass(),
                          context.getCurrentScope());
     }
 
     public static Block newInterpretedClosure(ThreadContext context, BlockBody body, IRubyObject self) {
         Frame f = context.getCurrentFrame();
 
         Binding binding = new Binding(self,
                          f,
                          f.getVisibility(),
                          context.getRubyClass(),
                          context.getCurrentScope());
         return new Block(body, binding);
     }
     
     public static Block newInterpretedClosure(IterNode iterNode, IRubyObject self, Arity arity, Frame frame,
             Visibility visibility, RubyModule klass, DynamicScope dynamicScope) {
         NodeType argsNodeId = getArgumentTypeWackyHack(iterNode);
         
         BlockBody body = new InterpretedBlock(
                 iterNode,
                 arity,
                 asArgumentType(argsNodeId));
         
         Binding binding = new Binding(
                 self, 
                 frame,
                 visibility,
                 klass,
                 dynamicScope);
         
         return new Block(body, binding);
     }
 
     public InterpretedBlock(IterNode iterNode, int argumentType) {
         this(iterNode, Arity.procArityOf(iterNode == null ? null : iterNode.getVarNode()), argumentType);
     }
     
     public InterpretedBlock(IterNode iterNode, Arity arity, int argumentType) {
         super(argumentType);
         this.iterNode = iterNode;
         this.arity = arity;
         this.hasVarNode = iterNode.getVarNode() != null;
         this.varNode = iterNode.getVarNode();
         this.bodyNode = iterNode.getBodyNode() == null ? NilImplicitNode.NIL : iterNode.getBodyNode();
         this.scope = iterNode.getScope();
     }
     
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldSpecificBlock(binding, iterNode.getScope(), klass);
     }
     
     protected void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYield(binding, lastFrame);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yield(context, null, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yield(context, arg0, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1), null, null, true, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1, arg2), null, null, true, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
         
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
 
         try {
             if (hasVarNode) {
                 setupBlockArg(context, varNode, value, self);
             }
             
             return evalBlockBody(context, self);
         } catch (JumpException.NextJump nj) {
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = prepareSelf(binding);
         }
         
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, klass, binding);
 
         try {
             if (hasVarNode) {
                 if (aValue) {
                     setupBlockArgs(context, varNode, value, self);
                 } else {
                     setupBlockArg(context, varNode, value, self);
                 }
             }
             
             // This while loop is for restarting the block call in case a 'redo' fires.
             return evalBlockBody(context, self);
         } catch (JumpException.NextJump nj) {
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
     
     private IRubyObject evalBlockBody(ThreadContext context, IRubyObject self) {
         // This while loop is for restarting the block call in case a 'redo' fires.
         while (true) {
             try {
                 return bodyNode.interpret(context.getRuntime(), context, self, Block.NULL_BLOCK);
             } catch (JumpException.RedoJump rj) {
                 context.pollThreadEvents();
                 // do nothing, allow loop to redo
             } catch (StackOverflowError soe) {
                 throw context.getRuntime().newSystemStackError("stack level too deep", soe);
             }
         }
     }
     
     private IRubyObject prepareSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
         
         return self;
     }
     
     private IRubyObject handleNextJump(ThreadContext context, JumpException.NextJump nj, Block.Type type) {
-        return type == Block.Type.LAMBDA ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
+        return nj.getValue() == null ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
     }
 
     private void setupBlockArgs(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = context.getRuntime();
         
         switch (varNode.getNodeType()) {
         case ZEROARGNODE:
             break;
         case MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode, (RubyArray)value, false);
             break;
         default:
             defaultArgsLogic(context, runtime, self, value);
         }
     }
 
     private void setupBlockArg(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = context.getRuntime();
         
         switch (varNode.getNodeType()) {
         case ZEROARGNODE:
             return;
         case MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode,
                     ArgsUtil.convertToRubyArray(runtime, value, ((MultipleAsgnNode)varNode).getHeadNode() != null), false);
             break;
         default:
             defaultArgLogic(context, runtime, self, value);
         }
     }
     
     private final void defaultArgsLogic(ThreadContext context, Ruby ruby, IRubyObject self, IRubyObject value) {
         int length = ArgsUtil.arrayLength(value);
         switch (length) {
         case 0:
             value = ruby.getNil();
             break;
         case 1:
             value = ((RubyArray)value).eltInternal(0);
             break;
         default:
             ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (" + length + " for 1)");
         }
         
         varNode.assign(ruby, context, self, value, Block.NULL_BLOCK, false);
     }
     
     private final void defaultArgLogic(ThreadContext context, Ruby ruby, IRubyObject self, IRubyObject value) {
         if (value == null) {
             ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (0 for 1)");
         }
         varNode.assign(ruby, context, self, value, Block.NULL_BLOCK, false);
     }
     
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Block cloneBlock(Binding binding) {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
         binding = new Binding(
                 binding.getSelf(),
                 binding.getFrame(),
                 binding.getVisibility(),
                 binding.getKlass(), 
                 binding.getDynamicScope());
         
         return new Block(this, binding);
     }
 
     public IterNode getIterNode() {
         return iterNode;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return arity;
     }
 }
