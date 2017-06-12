76/report.java
Satd-method: 
********************************************
********************************************
76/Between/99f983249  Rejigger how binding wor diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***

--- a/spec/tags/ruby/core/kernel/eval_tags.txt
+++ /dev/null
-fails(JRUBY-2278):Kernel#eval should include file and line information in syntax error
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
-                RubyBinding binding = RubyBinding.newBinding(Ruby.this);
+                RubyBinding binding = RubyBinding.newBinding(Ruby.this, context.currentBinding());
-        return RubyBinding.newBinding(this);
+        return RubyBinding.newBinding(this, getCurrentContext().currentBinding());
--- a/src/org/jruby/RubyBinding.java
+++ b/src/org/jruby/RubyBinding.java
+        bindingClass.getSingletonClass().undefineMethod("new");
-    
+
+    @Deprecated
-        ThreadContext context = runtime.getCurrentContext();
-        
-        // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
-        Frame frame = context.getCurrentFrame();
-        Binding binding = new Binding(frame, context.getImmediateBindingRubyClass(), context.getCurrentScope());
-        
-        return new RubyBinding(runtime, runtime.getBinding(), binding);
+        return newBinding(runtime, runtime.getCurrentContext().currentBinding());
-    public static RubyBinding newBinding(Ruby runtime, IRubyObject recv) {
-        ThreadContext context = runtime.getCurrentContext();
-
-        // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
-        Frame frame = context.getCurrentFrame();
-        Binding binding = new Binding(recv, frame, frame.getVisibility(), context.getImmediateBindingRubyClass(), context.getCurrentScope());
-
-        return new RubyBinding(runtime, runtime.getBinding(), binding);
-    }
-
-    /**
-     * Create a binding appropriate for a bare "eval", by using the previous (caller's) frame and current
-     * scope.
-     */
-    public static RubyBinding newBindingForEval(ThreadContext context) {
-        // This requires some explaining.  We use Frame values when executing blocks to fill in 
-        // various values in ThreadContext and EvalState.eval like rubyClass, cref, and self.
-        // Largely, for an eval that is using the logical binding at a place where the eval is 
-        // called we mostly want to use the current frames value for this.  Most importantly, 
-        // we need that self (JRUBY-858) at this point.  We also need to make sure that returns
-        // jump to the right place (which happens to be the previous frame).  Lastly, we do not
-        // want the current frames klazz since that will be the klazz represented of self.  We
-        // want the class right before the eval (well we could use cref class for this too I think).
-        // Once we end up having Frames created earlier I think the logic of stuff like this will
-        // be better since we won't be worried about setting Frame to setup other variables/stacks
-        // but just making sure Frame itself is correct...
-        
-        Frame previousFrame = context.getPreviousFrame();
-        Frame currentFrame = context.getCurrentFrame();
-        currentFrame.setKlazz(previousFrame.getKlazz());
-        
-        // Set jump target to whatever the previousTarget thinks is good.
-//        currentFrame.setJumpTarget(previousFrame.getJumpTarget() != null ? previousFrame.getJumpTarget() : previousFrame);
-        
-        Binding binding = new Binding(previousFrame, context.getEvalBindingRubyClass(), context.getCurrentScope());
-        Ruby runtime = context.getRuntime();
-        
-        return new RubyBinding(runtime, runtime.getBinding(), binding);
+    @Deprecated
+    public static RubyBinding newBinding(Ruby runtime, IRubyObject self) {
+       return newBinding(runtime, runtime.getCurrentContext().currentBinding(self));
-        // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
-        Frame frame = context.getCurrentFrame();
-        binding = new Binding(frame, context.getImmediateBindingRubyClass(), context.getCurrentScope());
+        binding = context.currentBinding();
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
+import org.jruby.runtime.Binding;
-        return RubyBinding.newBinding(context.getRuntime(), recv);
+        return RubyBinding.newBinding(context.getRuntime(), context.currentBinding(recv));
-        return RubyBinding.newBinding(context.getRuntime());
+        return RubyBinding.newBinding(context.getRuntime(), context.currentBinding());
-            
-        
-        IRubyObject scope = args.length > 1 && !args[1].isNil() ? args[1] : null;
-        String file;
+
+        boolean bindingGiven = args.length > 1 && !args[1].isNil();
+        Binding binding = bindingGiven ? convertToBinding(args[1]) : context.previousBinding();
-            file = args[2].convertToString().toString();
-        } else if (scope == null) {
-            file = "(eval)";
+            // file given, use it and force it into binding
+            binding.setFile(args[2].convertToString().toString());
-            file = null;
+            // file not given
+            if (bindingGiven) {
+                // binding given, use binding's file
+            } else {
+                // no binding given, use (eval)
+                binding.setFile("(eval)");
+            }
-        int line;
-            line = (int) args[3].convertToInteger().getLongValue();
-        } else if (scope == null) {
-            line = 0;
+            // file given, use it and force it into binding
+            binding.setLine((int) args[3].convertToInteger().getLongValue());
-            line = -1;
+            // no binding given, use 0 for both
+            binding.setLine(0);
-        if (scope == null) scope = RubyBinding.newBindingForEval(context);
-        return ASTInterpreter.evalWithBinding(context, src, scope, file, line);
+        return ASTInterpreter.evalWithBinding(context, src, binding);
+    }
+
+    private static Binding convertToBinding(IRubyObject scope) {
+        if (scope instanceof RubyBinding) {
+            return ((RubyBinding)scope).getBinding().clone();
+        } else {
+            if (scope instanceof RubyProc) {
+                return ((RubyProc) scope).getBlock().getBinding().clone();
+            } else {
+                // bomb out, it's not a binding or a proc
+                throw scope.getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
+            }
+        }
--- a/src/org/jruby/evaluator/ASTInterpreter.java
+++ b/src/org/jruby/evaluator/ASTInterpreter.java
-    public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, 
-            String file, int lineNumber) {
-        // both of these are ensured by the (very few) callers
-        assert !scope.isNil();
-        //assert file != null;
-
+    public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, Binding binding) {
-        String savedFile = context.getFile();
-        int savedLine = context.getLine();
-
-        if (!(scope instanceof RubyBinding)) {
-            if (scope instanceof RubyProc) {
-                scope = ((RubyProc) scope).binding();
-            } else {
-                // bomb out, it's not a binding or a proc
-                throw runtime.newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
-            }
-        }
-
-        Binding binding = ((RubyBinding)scope).getBinding();
-
-        // If no explicit file passed in we will use the bindings location
-        if (file == null) file = binding.getFrame().getFile();
-        if (lineNumber == -1) lineNumber = binding.getFrame().getLine();
-            Node node = runtime.parseEval(source.getByteList(), file, evalScope, lineNumber);
+            Node node = runtime.parseEval(source.getByteList(), binding.getFile(), evalScope, binding.getLine());
-
-            // restore position
-            context.setFile(savedFile);
-            context.setLine(savedLine);
--- a/src/org/jruby/runtime/Binding.java
+++ b/src/org/jruby/runtime/Binding.java
-import org.jruby.parser.StaticScope;
+
+    private String file;
+    private int line;
-            Visibility visibility, RubyModule klass, DynamicScope dynamicScope) {
+            Visibility visibility, RubyModule klass, DynamicScope dynamicScope, String file, int line) {
+        this.file = file;
+        this.line = line;
-    public Binding(Frame frame, RubyModule bindingClass, DynamicScope dynamicScope) {
+    public Binding(Frame frame, RubyModule bindingClass, DynamicScope dynamicScope, String file, int line) {
+        this.file = file;
+        this.line = line;
+    }
+
+    public Binding clone() {
+        return new Binding(self, frame, visibility, klass, dynamicScope, file, line);
+    }
+
+    public Binding clone(Visibility visibility) {
+        return new Binding(self, frame, visibility, klass, dynamicScope, file, line);
+
+    public String getFile() {
+        return file;
+    }
+
+    public void setFile(String file) {
+        this.file = file;
+    }
+
+    public int getLine() {
+        return line;
+    }
+
+    public void setLine(int line) {
+        this.line = line;
+    }
--- a/src/org/jruby/runtime/CallBlock.java
+++ b/src/org/jruby/runtime/CallBlock.java
-        Binding binding = new Binding(self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope());
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
-        binding = new Binding(binding.getSelf(), binding.getFrame(),
-                Visibility.PUBLIC,
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone(Visibility.PUBLIC);
--- a/src/org/jruby/runtime/CompiledBlock.java
+++ b/src/org/jruby/runtime/CompiledBlock.java
-    public static Block newCompiledClosure(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
-        DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
-        BlockBody body = new CompiledBlock(arity, scope, callback, hasMultipleArgsHead, argumentType);
-        
-        return new Block(body, binding);
-    }
-    
-        return newCompiledClosure(
-                self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope(),
-                arity,
-                scope,
-                callback,
-                hasMultipleArgsHead,
-                argumentType);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
+        BlockBody body = new CompiledBlock(arity, scope, callback, hasMultipleArgsHead, argumentType);
+
+        return new Block(body, binding);
-        Binding binding = new Binding(self, context.getCurrentFrame(), Visibility.PUBLIC, context.getRubyClass(), context.getCurrentScope());
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
-        binding = new Binding(binding.getSelf(),
-                binding.getFrame(),
-                binding.getVisibility(),
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone();
--- a/src/org/jruby/runtime/CompiledBlock19.java
+++ b/src/org/jruby/runtime/CompiledBlock19.java
-    public static Block newCompiledClosure(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
-        DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
-        BlockBody body = new CompiledBlock19(arity, scope, callback, hasMultipleArgsHead, argumentType);
-        
-        return new Block(body, binding);
-    }
-    
-        return newCompiledClosure(
-                self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope(),
-                arity,
-                scope,
-                callback,
-                hasMultipleArgsHead,
-                argumentType);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
+        BlockBody body = new CompiledBlock19(arity, scope, callback, hasMultipleArgsHead, argumentType);
+
+        return new Block(body, binding);
-        Binding binding = new Binding(self, context.getCurrentFrame(), Visibility.PUBLIC, context.getRubyClass(), context.getCurrentScope());
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
-        binding = new Binding(binding.getSelf(),
-                binding.getFrame(),
-                binding.getVisibility(),
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone();
--- a/src/org/jruby/runtime/CompiledBlockLight.java
+++ b/src/org/jruby/runtime/CompiledBlockLight.java
-    public static Block newCompiledClosureLight(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
-        DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
-        BlockBody body = new CompiledBlockLight(arity, scope, callback, hasMultipleArgsHead, argumentType);
-        
-        return new Block(body, binding);
-    }
-    
-        return newCompiledClosureLight(
-                self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope(),
-                arity,
-                scope, 
-                callback,
-                hasMultipleArgsHead,
-                argumentType);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
+        BlockBody body = new CompiledBlockLight(arity, scope, callback, hasMultipleArgsHead, argumentType);
+
+        return new Block(body, binding);
--- a/src/org/jruby/runtime/CompiledBlockLight19.java
+++ b/src/org/jruby/runtime/CompiledBlockLight19.java
-    public static Block newCompiledClosureLight(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
-        DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
-        BlockBody body = new CompiledBlockLight19(arity, scope, callback, hasMultipleArgsHead, argumentType);
-        
-        return new Block(body, binding);
-    }
-    
-        return newCompiledClosureLight(
-                self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope(),
-                arity,
-                scope, 
-                callback,
-                hasMultipleArgsHead,
-                argumentType);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
+        BlockBody body = new CompiledBlockLight19(arity, scope, callback, hasMultipleArgsHead, argumentType);
+
+        return new Block(body, binding);
--- a/src/org/jruby/runtime/CompiledSharedScopeBlock.java
+++ b/src/org/jruby/runtime/CompiledSharedScopeBlock.java
-        Binding binding = new Binding(self,
-             context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                dynamicScope);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC, dynamicScope);
--- a/src/org/jruby/runtime/Frame.java
+++ b/src/org/jruby/runtime/Frame.java
+    /**
+     * Set both the file and line
+     */
+    public void setFileAndLine(String file, int line) {
+        this.fileName = file;
+        this.line = line;
+    }
+
--- a/src/org/jruby/runtime/Interpreted19Block.java
+++ b/src/org/jruby/runtime/Interpreted19Block.java
-        Frame frame = context.getCurrentFrame();
-
-        Binding binding = new Binding(self,
-                         frame,
-                         frame.getVisibility(),
-                         context.getRubyClass(),
-                         context.getCurrentScope());
+        Binding binding = context.currentBinding(self);
-        binding = new Binding(
-                binding.getSelf(),
-                binding.getFrame(),
-                binding.getVisibility(),
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone();
--- a/src/org/jruby/runtime/InterpretedBlock.java
+++ b/src/org/jruby/runtime/InterpretedBlock.java
-        Frame f = context.getCurrentFrame();
-
-        return newInterpretedClosure(iterNode,
-                         self,
-                         Arity.procArityOf(iterNode.getVarNode()),
-                         f,
-                         f.getVisibility(),
-                         context.getRubyClass(),
-                         context.getCurrentScope());
-    }
-
-    public static Block newInterpretedClosure(ThreadContext context, BlockBody body, IRubyObject self) {
-        Frame f = context.getCurrentFrame();
-
-        Binding binding = new Binding(self,
-                         f,
-                         f.getVisibility(),
-                         context.getRubyClass(),
-                         context.getCurrentScope());
-        return new Block(body, binding);
-    }
-    
-    public static Block newInterpretedClosure(IterNode iterNode, IRubyObject self, Arity arity, Frame frame,
-            Visibility visibility, RubyModule klass, DynamicScope dynamicScope) {
+        Binding binding = context.currentBinding(self);
-        
+
-                arity,
+                Arity.procArityOf(iterNode.getVarNode()),
-        
-        Binding binding = new Binding(
-                self, 
-                frame,
-                visibility,
-                klass,
-                dynamicScope);
-        
+        return new Block(body, binding);
+    }
+
+    public static Block newInterpretedClosure(ThreadContext context, BlockBody body, IRubyObject self) {
+        Binding binding = context.currentBinding(self);
-        binding = new Binding(
-                binding.getSelf(),
-                binding.getFrame(),
-                binding.getVisibility(),
-                binding.getKlass(), 
-                binding.getDynamicScope());
-        
+        binding = binding.clone();
--- a/src/org/jruby/runtime/MethodBlock.java
+++ b/src/org/jruby/runtime/MethodBlock.java
-        Binding binding = new Binding(self,
-                               context.getCurrentFrame().duplicate(),
-                         context.getCurrentFrame().getVisibility(),
-                         context.getRubyClass(),
-                         dynamicScope);
-        
+        Binding binding = context.currentBinding(self, dynamicScope);
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
-        // captured instances of this block may still be around and we do not want to start
-        // overwriting those values when we create a new one.
-        // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
-        binding = new Binding(binding.getSelf(), binding.getFrame(), binding.getVisibility(), binding.getKlass(), 
-                binding.getDynamicScope().cloneScope());
+        binding = binding.clone();
--- a/src/org/jruby/runtime/SharedScopeBlock.java
+++ b/src/org/jruby/runtime/SharedScopeBlock.java
-        Binding binding = new Binding(self,
-                context.getCurrentFrame(),
-                context.getCurrentFrame().getVisibility(),
-                context.getRubyClass(),
-                dynamicScope);
+        Binding binding = context.currentBinding(self, dynamicScope);
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
+import java.util.Arrays;
-import org.jruby.RubyException;
-import org.jruby.RubyObject;
-        assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
-        
+        assert parentIndex != -1 : "Trying to get RubyClass from empty stack";
-        
-        return parentModule.getNonIncludedClass();
-    }
-    
-    public RubyModule getImmediateBindingRubyClass() {
-        int index = parentIndex;
-        RubyModule parentModule = null;
-        parentModule = parentStack[index];
-    public RubyModule getEvalBindingRubyClass() {
-        int index = parentIndex;
-        RubyModule parentModule = null;
-        if(index == 0) {
-            parentModule = parentStack[index];
-        } else {
-            parentModule = parentStack[index-1];
-        }
+    public RubyModule getPreviousRubyClass() {
+        assert parentIndex != 0 : "Trying to get RubyClass from too-shallow stack";
+        RubyModule parentModule = parentStack[parentIndex - 1];
-        f.setFile(file);
-        f.setLine(line);
+
+        // set the binding's frame's "previous" file and line to current, so
+        // trace will show who called the block
+        f.setFileAndLine(file, line);
+        
+        setFileAndLine(binding.getFile(), binding.getLine());
+        f.setVisibility(binding.getVisibility());
+        
+        return lastFrame;
+    }
+
+    private Frame pushFrameForEval(Binding binding) {
+        Frame lastFrame = getNextFrame();
+        Frame f = pushFrame(binding.getFrame());
+        setFileAndLine(binding.getFile(), binding.getLine());
-        Frame lastFrame = getNextFrame();
-        Frame frame = binding.getFrame();
-        frame.setIsBindingFrame(true);
-        pushFrame(frame);
-        getCurrentFrame().setVisibility(binding.getVisibility());
+        binding.getFrame().setIsBindingFrame(true);
+        Frame lastFrame = pushFrameForEval(binding);
+
+    /**
+     * Return a binding representing the current call's state
+     * @return the current binding
+     */
+    public Binding currentBinding() {
+        Frame frame = getCurrentFrame();
+        return new Binding(frame, getRubyClass(), getCurrentScope(), file, line);
+    }
+
+    /**
+     * Return a binding representing the current call's state but with a specified self
+     * @param self the self object to use
+     * @return the current binding, using the specified self
+     */
+    public Binding currentBinding(IRubyObject self) {
+        Frame frame = getCurrentFrame();
+        return new Binding(self, frame, frame.getVisibility(), getRubyClass(), getCurrentScope(), file, line);
+    }
+
+    /**
+     * Return a binding representing the current call's state but with the
+     * specified visibility and self.
+     * @param self the self object to use
+     * @param visibility the visibility to use
+     * @return the current binding using the specified self and visibility
+     */
+    public Binding currentBinding(IRubyObject self, Visibility visibility) {
+        Frame frame = getCurrentFrame();
+        return new Binding(self, frame, visibility, getRubyClass(), getCurrentScope(), file, line);
+    }
+
+    /**
+     * Return a binding representing the current call's state but with the
+     * specified scope and self.
+     * @param self the self object to use
+     * @param visibility the scope to use
+     * @return the current binding using the specified self and scope
+     */
+    public Binding currentBinding(IRubyObject self, DynamicScope scope) {
+        Frame frame = getCurrentFrame();
+        return new Binding(self, frame, frame.getVisibility(), getRubyClass(), scope, file, line);
+    }
+
+    /**
+     * Return a binding representing the current call's state but with the
+     * specified visibility, scope, and self. For shared-scope binding
+     * consumers like for loops.
+     * 
+     * @param self the self object to use
+     * @param visibility the visibility to use
+     * @param scope the scope to use
+     * @return the current binding using the specified self, scope, and visibility
+     */
+    public Binding currentBinding(IRubyObject self, Visibility visibility, DynamicScope scope) {
+        Frame frame = getCurrentFrame();
+        return new Binding(self, frame, visibility, getRubyClass(), scope, file, line);
+    }
+
+    /**
+     * Return a binding representing the previous call's state
+     * @return the current binding
+     */
+    public Binding previousBinding() {
+        Frame frame = getPreviousFrame();
+        Frame current = getCurrentFrame();
+        return new Binding(frame, getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
+    }
+
+    /**
+     * Return a binding representing the previous call's state but with a specified self
+     * @param self the self object to use
+     * @return the current binding, using the specified self
+     */
+    public Binding previousBinding(IRubyObject self) {
+        Frame frame = getPreviousFrame();
+        Frame current = getCurrentFrame();
+        return new Binding(self, frame, frame.getVisibility(), getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
+    }

Lines added containing method: 242. Lines removed containing method: 318. Tot = 560
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
********************************************
********************************************
