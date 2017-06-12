diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index cdfdda42fc..22b276a0a6 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,889 +1,890 @@
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
 import org.jruby.RubyKernel.CatchTarget;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author jpetersen
  */
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
     
     CallType lastCallType;
     
     Visibility lastVisibility;
     
     IRubyObject lastExitStatus;
     
     public Ruby getRuntime() {
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
         getThread().pollThreadEvents();
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
         IRubyObject undef = runtime.getUndef();
         
         // flipped from while to do to search current class first
         for (StaticScope scope = getCurrentScope().getStaticScope(); scope != null; scope = scope.getPreviousCRefScope()) {
             RubyModule module = scope.getModule();
             if ((result = module.fastFetchConstant(internedName)) != null) {
                 if (result != undef) return true;
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
         IRubyObject undef = runtime.getUndef();
         IRubyObject result;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = scope.getModule();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             if ((result = klass.fastFetchConstant(internedName)) != null) {
                 if (result != undef) {
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
-        if (frame.getName() != null && 
+        if (frame != previousFrame && // happens with native exceptions, should not filter those out
+                frame.getName() != null && 
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
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, IRubyObject[] args, int req, Block block, 
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
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, IRubyObject[] args, int req, Block block,
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
         pushFrame(binding.getFrame());
         getCurrentFrame().setVisibility(binding.getVisibility());
         pushScope(binding.getDynamicScope());
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         pushFrame(binding.getFrame());
         getCurrentFrame().setVisibility(binding.getVisibility());
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         pushFrame(binding.getFrame());
         getCurrentFrame().setVisibility(binding.getVisibility());
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         pushRubyClass((klass != null) ? klass : binding.getKlass());
     }
     
     public void preYieldNoScope(Binding binding, RubyModule klass) {
         pushFrame(binding.getFrame());
         getCurrentFrame().setVisibility(binding.getVisibility());
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
diff --git a/test/test_backtraces.rb b/test/test_backtraces.rb
index af3a9d7ec8..c08cc9ee5f 100644
--- a/test/test_backtraces.rb
+++ b/test/test_backtraces.rb
@@ -1,213 +1,248 @@
 require 'test/unit'
 
 # Unit and regression tests for backtraces.
 # Note: The tests follow MRI 1.8.6 behavior.
 # Behavior of MRI 1.9 is different:
 # http://blade.nagaokaut.ac.jp/cgi-bin/scat.rb/ruby/ruby-core/15589
 class TestBacktraces < Test::Unit::TestCase
   def setup
     @offset = nil
   end
 
   # Convenience method to obtain the exception,
   # and to print the stack trace, if needed.
   def get_exception(verbose=false)
     begin
       @get_exception_yield_line = __LINE__ + 1
       yield
     rescue Exception => ex
       puts ex.backtrace.join("\n") if verbose
       ex
     end
   end
 
   # Main verification method that performs actual checks
   # on the stacktraces.
   def check(expectations, exception)
     backtrace = []
     expectations.strip.split("\n").each { |line|
       line.strip!
 
       # if line starts with +nnn, we prepend
       # the current file and offset
       md = line.match(/^\+(\d+)(:.*)/)
       if (md)
         flunk("@offset is not defined in the test case") unless @offset
         line = "#{__FILE__}:#{$1.to_i + @offset}#{$2}"
       end
 
       backtrace << line.strip
     }
     backtrace.each_with_index { |expected, idx|
       assert_equal(expected, exception.backtrace[idx])
     }
   end
 
   def test_simple_exception
     @offset = __LINE__
     raise RuntimeError.new("Test")
   rescue Exception => ex
     expectation = "+1:in `test_simple_exception'"
     check(expectation, ex)
   end
 
+  def test_simple_exception_recursive
+    @offset = __LINE__
+    def meth(n)
+      raise RuntimeError.new("Test") if n == 10
+      n += 1
+      meth(n)
+    end
+    meth(0)
+  rescue Exception => ex
+    expectation = %q{
+      +2:in `meth'
+      +4:in `meth'
+      +6:in `test_simple_exception_recursive'
+    }
+    check(expectation, ex)
+  end
+
+  def test_native_exception_recursive
+    @offset = __LINE__
+    def meth(n)
+      raise "hello".sub(/l/, 5) if n == 10
+      n += 1
+      meth(n)
+    end
+    meth(0)
+  rescue Exception => ex
+    expectation = %q{
+      +2:in `sub'
+      +2:in `meth'
+      +4:in `meth'
+      +6:in `test_native_exception_recursive'
+    }
+    check(expectation, ex)
+  end
+
   def test_exception_from_block
     @offset = __LINE__
     def foo
       yield
     end
     def bar
       yield
     end
     foo { bar { raise TypeError.new("HEH") } }
   rescue Exception => ex
     expectation = %q{
       +7:in `test_exception_from_block'
       +5:in `bar'
       +7:in `test_exception_from_block'
       +2:in `foo'
       +7:in `test_exception_from_block'
     }
     check(expectation, ex)
   end
   
   def test_exception_from_for
     array = [1,2,3,4,5]
     @offset = __LINE__
     for element in array
       raise RuntimeError
     end
   rescue Exception => ex
     expectation = %q{
       +2:in `test_exception_from_for'
       +1:in `each'
       +1:in `test_exception_from_for'
     }
     check(expectation, ex)
   end
 
   def test_exception_from_proc
     p = Proc.new {
       @offset = __LINE__
       raise StandardError.new
     }
     p.call
   rescue Exception => ex
     expectation = %q{
       +1:in `test_exception_from_proc'
       +3:in `call'
       +3:in `test_exception_from_proc'
     }
     check(expectation, ex)
   end
 
   def test_exception_from_lambda
     l = lambda {
       @offset = __LINE__
       raise StandardError.new
     }
     l.call
   rescue Exception => ex
     expectation = %q{
       +1:in `test_exception_from_lambda'
       +3:in `call'
       +3:in `test_exception_from_lambda'
     }
     check(expectation, ex)
   end
 
   def test_exception_from_array_plus
     @offset = __LINE__
     [1,2,3] + 5
   rescue Exception => ex
     expectation = %q{
       +1:in `+'
       +1:in `test_exception_from_array_plus'
     }
     check(expectation, ex)
   end
   
   # JRUBY-2138
   def test_exception_from_string_plus
     @offset = __LINE__
     "hello" + nil
   rescue Exception => ex
     expectation = %q{
       +1:in `+'
       +1:in `test_exception_from_string_plus'
     }
     check(expectation, ex)
   end
 
   def test_exception_from_string_sub
     @offset = __LINE__
     "hello".sub(/l/, 5)
   rescue Exception => ex
     expectation = %q{
       +1:in `sub'
       +1:in `test_exception_from_string_sub'
     }
     check(expectation, ex)
   end
 
   def test_zero_devision_exception
     @offset = __LINE__
     1/0
   rescue Exception => ex
     expectation = %q{
       +1:in `/'
       +1:in `test_zero_devision_exception'
     }
     check(expectation, ex)
   end
 
   def test_exeption_from_object_send
     @offset = __LINE__
     "hello".__send__(:sub, /l/, 5)
   rescue Exception => ex
     expectation = %q{
       +1:in `sub'
       +1:in `__send__'
       +1:in `test_exeption_from_object_send'
     }
     check(expectation, ex)
   end
 
   def test_arity_exception
     @offset = __LINE__
     "hello".sub
   rescue Exception => ex
     expectation = "+1:in `sub'"
     check(expectation, ex)
   end
 
   def test_exception_from_eval
     ex = get_exception {
       @offset = __LINE__
       eval("raise RuntimeError.new")
     }
     expectation = %Q{
       +1:in `test_exception_from_eval'
       #{__FILE__}:#{@get_exception_yield_line}:in `eval'
       +1:in `test_exception_from_eval'
       #{__FILE__}:#{@get_exception_yield_line}:in `get_exception'
     }
     check(expectation, ex)
   end
 
   def test_exception_from_block_inside_eval
     ex = get_exception {
       @offset = __LINE__
       eval("def foo; yield; end; foo { raise RuntimeError.new }")
     }
     expectation = %Q{
       +1:in `test_exception_from_block_inside_eval'
       (eval):1:in `foo'
       (eval):1:in `test_exception_from_block_inside_eval'
       #{__FILE__}:#{@get_exception_yield_line}:in `eval'
       +1:in `test_exception_from_block_inside_eval'
       #{__FILE__}:#{@get_exception_yield_line}:in `get_exception'
     }
     check(expectation, ex)
   end
 end
