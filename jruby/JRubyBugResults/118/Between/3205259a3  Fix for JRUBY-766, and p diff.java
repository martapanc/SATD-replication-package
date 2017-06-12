diff --git a/src/org/jruby/internal/runtime/methods/DefaultMethod.java b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
index 889c06e57a..3d589ee3b1 100644
--- a/src/org/jruby/internal/runtime/methods/DefaultMethod.java
+++ b/src/org/jruby/internal/runtime/methods/DefaultMethod.java
@@ -1,326 +1,323 @@
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
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.internal.runtime.methods;
 
 import java.util.ArrayList;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.ListNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.NodeCompilerFactory;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  */
 public final class DefaultMethod extends DynamicMethod {
     
     private StaticScope staticScope;
     private Node body;
     private ArgsNode argsNode;
     private SinglyLinkedList cref;
     private int callCount = 0;
     private Script jitCompiledScript;
 
     private static final boolean JIT_ENABLED;
     private static final boolean JIT_LOGGING;
     private static final boolean JIT_LOGGING_VERBOSE;
     private static final int JIT_THRESHOLD;
       
     static {
         if (Ruby.isSecurityRestricted()) {
             JIT_ENABLED = false;
             JIT_LOGGING = false;
             JIT_LOGGING_VERBOSE = false;
             JIT_THRESHOLD = 50;
         } else {
             JIT_ENABLED = Boolean.getBoolean("jruby.jit.enabled");
             JIT_LOGGING = Boolean.getBoolean("jruby.jit.logging");
             JIT_LOGGING_VERBOSE = Boolean.getBoolean("jruby.jit.logging.verbose");
             JIT_THRESHOLD = Integer.parseInt(System.getProperty("jruby.jit.threshold", "50"));
         }
     }
     public DefaultMethod(RubyModule implementationClass, StaticScope staticScope, Node body, 
             ArgsNode argsNode, Visibility visibility, SinglyLinkedList cref) {
         super(implementationClass, visibility);
         this.body = body;
         this.staticScope = staticScope;
         this.argsNode = argsNode;
 		this.cref = cref;
 		
 		assert argsNode != null;
     }
     
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, 
             IRubyObject[] args, boolean noSuper, Block block) {
-        context.preDefMethodInternalCall(clazz, name, self, args, getArity().required(), block, noSuper, cref, staticScope);
+        context.preDefMethodInternalCall(clazz, name, self, args, getArity().required(), block, noSuper, cref, staticScope, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postDefMethodInternalCall();
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject call(ThreadContext context, IRubyObject self, 
             RubyModule clazz, String name, IRubyObject[] args, boolean noSuper, Block block) {
         if (jitCompiledScript != null) {
             try {
                 context.preCompiledMethod(implementationClass, cref);
                 // FIXME: pass block when available
                 return jitCompiledScript.run(context, self, args, block);
             } finally {
                 context.postCompiledMethod();
             }
         } 
           
         return super.call(context, self, clazz, name, args, noSuper, block);
     }
 
     /**
      * @see AbstractCallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject internalCall(ThreadContext context, RubyModule clazz, 
             IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         assert args != null;
         
         Ruby runtime = context.getRuntime();
         
         if (JIT_ENABLED) runJIT(runtime, name);
         
         if (JIT_ENABLED && jitCompiledScript != null) {
             return jitCompiledScript.run(context, self, args, block);
         }
-        
-        // set jump target for returns that show up later, like from evals
-        context.setFrameJumpTarget(this);
 
         if (argsNode.getBlockArgNode() != null && block.isGiven()) {
             RubyProc blockArg;
             
             if (block.getProcObject() != null) {
                 blockArg = (RubyProc) block.getProcObject();
             } else {
                 blockArg = runtime.newProc(false, block);
                 blockArg.getBlock().isLambda = block.isLambda;
             }
             // We pass depth zero since we know this only applies to newly created local scope
             context.getCurrentScope().setValue(argsNode.getBlockArgNode().getCount(), blockArg, 0);
         }
 
         try {
             prepareArguments(context, runtime, args);
             
             getArity().checkArity(runtime, args);
 
             traceCall(context, runtime, self, name);
                     
             return EvaluationState.eval(runtime, context, body, self, block);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump && je.getTarget() == this) {
 	                return (IRubyObject) je.getValue();
         	}
             
        		throw je;
         } finally {
             traceReturn(context, runtime, self, name);
         }
     }
 
     private void runJIT(Ruby runtime, String name) {
         if (callCount >= 0 && getArity().isFixed() && argsNode.getBlockArgNode() == null && argsNode.getOptArgs() == null && argsNode.getRestArg() == -1) {
             callCount++;
             if (callCount >= JIT_THRESHOLD) {
                 String className = null;
                 if (JIT_LOGGING) {
                     className = getImplementationClass().getBaseName();
                     if (className == null) {
                         className = "<anon class>";
                     }
                 }
                 
                 try {
                     String cleanName = CodegenUtils.cleanJavaIdentifier(name);
                     StandardASMCompiler compiler = new StandardASMCompiler(cleanName + hashCode(), body.getPosition().getFile());
                     compiler.startScript();
                     Object methodToken = compiler.beginMethod("__file__", getArity().getValue(), staticScope.getNumberOfVariables());
                     NodeCompilerFactory.getCompiler(body).compile(body, compiler);
                     compiler.endMethod(methodToken);
                     compiler.endScript();
                     Class sourceClass = compiler.loadClass(runtime);
                     jitCompiledScript = (Script)sourceClass.newInstance();
                     
                     if (JIT_LOGGING) System.err.println("compiled: " + className + "." + name);
                 } catch (Exception e) {
                     if (JIT_LOGGING_VERBOSE) System.err.println("could not compile: " + className + "." + name + " because of: \"" + e.getMessage() + '"');
                 } finally {
                     callCount = -1;
                 }
             }
         }
     }
 
     private void prepareArguments(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         int expectedArgsCount = argsNode.getArgsCount();
 
         int restArg = argsNode.getRestArg();
         boolean hasOptArgs = argsNode.getOptArgs() != null;
 
         // FIXME: This seems redundant with the arity check in internalCall...is it actually different?
         if (expectedArgsCount > args.length) {
             throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
         }
 
         // Bind 'normal' parameter values to the local scope for this method.
         if (expectedArgsCount > 0) {
             context.getCurrentScope().setArgValues(args, expectedArgsCount);
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) {
             args = prepareOptOrRestArgs(context, runtime, args, expectedArgsCount, restArg, hasOptArgs);
         }
         
         context.setFrameArgs(args);
     }
 
     private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, Ruby runtime, IRubyObject[] args, int expectedArgsCount, int restArg, boolean hasOptArgs) {
         if (restArg == -1 && hasOptArgs) {
             int opt = expectedArgsCount + argsNode.getOptArgs().size();
 
             if (opt < args.length) {
                 throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for " + opt + ")");
             }
         }
         
         int count = expectedArgsCount;
         if (argsNode.getOptArgs() != null) {
             count += argsNode.getOptArgs().size();
         }
 
         ArrayList allArgs = new ArrayList();
         
         // Combine static and optional args into a single list allArgs
         for (int i = 0; i < count && i < args.length; i++) {
             allArgs.add(args[i]);
         }
         
         if (hasOptArgs) {
             ListNode optArgs = argsNode.getOptArgs();
    
             int j = 0;
             for (int i = expectedArgsCount; i < args.length && j < optArgs.size(); i++, j++) {
                 // in-frame EvalState should already have receiver set as self, continue to use it
                 AssignmentVisitor.assign(runtime, context, context.getFrameSelf(), optArgs.get(j), args[i], Block.NULL_BLOCK, true);
                 expectedArgsCount++;
             }
    
             // assign the default values, adding to the end of allArgs
             while (j < optArgs.size()) {
                 // in-frame EvalState should already have receiver set as self, continue to use it
                 allArgs.add(EvaluationState.eval(runtime, context, optArgs.get(j++), context.getFrameSelf(), Block.NULL_BLOCK));
             }
         }
         
         // build an array from *rest type args, also adding to allArgs
         
         // ENEBO: Does this next comment still need to be done since I killed hasLocalVars:
         // move this out of the scope.hasLocalVariables() condition to deal
         // with anonymous restargs (* versus *rest)
         
         
         // none present ==> -1
         // named restarg ==> >=0
         // anonymous restarg ==> -2
         if (restArg != -1) {
             for (int i = expectedArgsCount; i < args.length; i++) {
                 allArgs.add(args[i]);
             }
 
             // only set in scope if named
             if (restArg >= 0) {
                 RubyArray array = runtime.newArray(args.length - expectedArgsCount);
                 for (int i = expectedArgsCount; i < args.length; i++) {
                     array.append(args[i]);
                 }
 
                 context.getCurrentScope().setValue(restArg, array, 0);
             }
         }
         
         args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
         return args;
     }
 
     private void traceReturn(ThreadContext context, Ruby runtime, IRubyObject self, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
 
         ISourcePosition position = context.getPreviousFramePosition();
         runtime.callTraceFunction(context, "return", position, self, name, getImplementationClass());
     }
 
     private void traceCall(ThreadContext context, Ruby runtime, IRubyObject self, String name) {
         if (runtime.getTraceFunction() == null) return;
 
 		ISourcePosition position = body != null ? body.getPosition() : context.getPosition(); 
 
 		runtime.callTraceFunction(context, "call", position, self, name, getImplementationClass());
     }
 
     public Arity getArity() {
         return argsNode.getArity();
     }
     
     public DynamicMethod dup() {
         return new DefaultMethod(getImplementationClass(), staticScope, body, argsNode, getVisibility(), cref);
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/FullFunctionCallbackMethod.java b/src/org/jruby/internal/runtime/methods/FullFunctionCallbackMethod.java
index 86d39a6728..53280cc8f2 100644
--- a/src/org/jruby/internal/runtime/methods/FullFunctionCallbackMethod.java
+++ b/src/org/jruby/internal/runtime/methods/FullFunctionCallbackMethod.java
@@ -1,90 +1,102 @@
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
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
+import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 /**
  */
 public class FullFunctionCallbackMethod extends DynamicMethod {
     private Callback callback;
 
     public FullFunctionCallbackMethod(RubyModule implementationClass, Callback callback, Visibility visibility) {
         super(implementationClass, visibility);
         this.callback = callback;
     }
 
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
-        context.preReflectedMethodInternalCall(implementationClass, clazz, self, name, args, getArity().required(), noSuper, block);
+        context.preReflectedMethodInternalCall(implementationClass, clazz, self, name, args, getArity().required(), noSuper, block, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postReflectedMethodInternalCall();
     }
-
+    
     public IRubyObject internalCall(ThreadContext context, RubyModule clazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
-    	assert args != null;
+        assert args != null;
         Ruby runtime = context.getRuntime();
+        ISourcePosition position = null;
+        boolean isTrace = runtime.getTraceFunction() != null;
         
-        if (runtime.getTraceFunction() != null) {
-            ISourcePosition position = context.getPosition();
-
+        if (isTrace) {
+            position = context.getPosition();
+            
             runtime.callTraceFunction(context, "c-call", position, self, name, getImplementationClass());
-            try {
-                return callback.execute(self, args, block);
-            } finally {
+        }
+        
+        try {
+            return callback.execute(self, args, block);
+        } catch (JumpException je) {
+            switch (je.getJumpType().getTypeId()) {
+            case JumpException.JumpType.RETURN:
+                if (je.getTarget() == this) return (IRubyObject)je.getValue();
+            default:
+                throw je;
+            }
+        } finally {
+            if (isTrace) {
                 runtime.callTraceFunction(context, "c-return", position, self, name, getImplementationClass());
             }
         }
-		return callback.execute(self, args, block);
     }
-
+    
     public Callback getCallback() {
         return callback;
     }
 
     public Arity getArity() {
         return getCallback().getArity();
     }
     
     public DynamicMethod dup() {
         return new FullFunctionCallbackMethod(getImplementationClass(), callback, getVisibility());
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/FullFunctionReflectedMethod.java b/src/org/jruby/internal/runtime/methods/FullFunctionReflectedMethod.java
index 8c29243991..df26ce5712 100644
--- a/src/org/jruby/internal/runtime/methods/FullFunctionReflectedMethod.java
+++ b/src/org/jruby/internal/runtime/methods/FullFunctionReflectedMethod.java
@@ -1,174 +1,174 @@
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
  * Copyright (C) 2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.internal.runtime.methods;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.Arrays;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public class FullFunctionReflectedMethod extends DynamicMethod {
     private Method method;
     private Class type;
     private String methodName;
     private Arity arity;
     
     public FullFunctionReflectedMethod(RubyModule implementationClass, Class type, String methodName, 
         Arity arity, Visibility visibility) {
     	super(implementationClass, visibility);
     	this.type = type;
     	this.methodName = methodName;
     	this.arity = arity;
     	
         assert type != null;
         assert methodName != null;
         assert arity != null;
 
         Class[] parameterTypes;
         if (arity.isFixed()) {
             parameterTypes = new Class[arity.getValue()+1];
             Arrays.fill(parameterTypes, IRubyObject.class);
             parameterTypes[arity.getValue()] = Block.class;
         } else {
             parameterTypes = new Class[2];
             parameterTypes[0] = IRubyObject[].class;
             parameterTypes[1] = Block.class;
         }
         try {
             method = type.getMethod(methodName, parameterTypes);
         } catch (NoSuchMethodException e) {
             assert false : e;
         } catch (SecurityException e) {
             assert false : e;
         }
         
         assert method != null;
     }
 
     public void preMethod(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
-        context.preReflectedMethodInternalCall(implementationClass, klazz, self, name, args, arity.required(), noSuper, block);
+        context.preReflectedMethodInternalCall(implementationClass, klazz, self, name, args, arity.required(), noSuper, block, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postReflectedMethodInternalCall();
     }
     
 	public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         Ruby runtime = context.getRuntime();
         arity.checkArity(runtime, args);
         
         assert self != null;
         assert args != null;
         assert method != null;
         
         Object[] methodArgs;
         if (!arity.isFixed()) {
             methodArgs = new Object[]{args, block};
         } else {
             methodArgs = new Object[args.length + 1];
             System.arraycopy(args, 0, methodArgs, 0, args.length);
             methodArgs[args.length] = block;
         }
 
         try {
             return (IRubyObject) method.invoke(self, methodArgs);
         } catch (InvocationTargetException e) {
             if (e.getTargetException() instanceof RaiseException) {
                 throw (RaiseException) e.getTargetException();
             } else if (e.getTargetException() instanceof JumpException) {
                 throw (JumpException) e.getTargetException();
             } else if (e.getTargetException() instanceof ThreadKill) {
             	// allow it to bubble up
             	throw (ThreadKill) e.getTargetException();
             } else if (e.getTargetException() instanceof Exception) {
                 if(e.getTargetException() instanceof MainExitException) {
                     throw (RuntimeException)e.getTargetException();
                 }
                 runtime.getJavaSupport().handleNativeException(e.getTargetException());
                 return runtime.getNil();
             } else {
                 throw (Error) e.getTargetException();
             }
         } catch (IllegalAccessException e) {
             StringBuffer message = new StringBuffer();
             message.append(e.getMessage());
             message.append(':');
             message.append(" methodName=").append(methodName);
             message.append(" recv=").append(self.toString());
             message.append(" type=").append(type.getName());
             message.append(" methodArgs=[");
             for (int i = 0; i < methodArgs.length; i++) {
                 message.append(methodArgs[i]);
                 message.append(' ');
             }
             message.append(']');
             assert false : message.toString();
             return null;
         } catch (final IllegalArgumentException e) {
 /*            StringBuffer message = new StringBuffer();
             message.append(e.getMessage());
             message.append(':');
             message.append(" methodName=").append(methodName);
             message.append(" recv=").append(recv.toString());
             message.append(" type=").append(type.getName());
             message.append(" methodArgs=[");
             for (int i = 0; i < methodArgs.length; i++) {
                 message.append(methodArgs[i]);
                 message.append(' ');
             }
             message.append(']');*/
             assert false : e;
             return null;
         }
 	}
 
 	public DynamicMethod dup() {
 		FullFunctionReflectedMethod newMethod = 
 		    new FullFunctionReflectedMethod(getImplementationClass(), type, methodName, arity, getVisibility());
 		
 		newMethod.method = method;
 		
 		return newMethod;
 	}
 
 	// TODO:  Perhaps abstract method should contain this and all other Methods should pass in decent value
 	public Arity getArity() {
 		return arity;
 	}
 }
diff --git a/src/org/jruby/internal/runtime/methods/FullInvocationMethod.java b/src/org/jruby/internal/runtime/methods/FullInvocationMethod.java
index ad9a870578..e07a14e975 100644
--- a/src/org/jruby/internal/runtime/methods/FullInvocationMethod.java
+++ b/src/org/jruby/internal/runtime/methods/FullInvocationMethod.java
@@ -1,108 +1,108 @@
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.exceptions.MainExitException;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public abstract class FullInvocationMethod extends DynamicMethod implements Cloneable {
     private Arity arity;
     public FullInvocationMethod(RubyModule implementationClass, Arity arity, Visibility visibility) {
     	super(implementationClass, visibility);
         this.arity = arity;
     }
 
     public void preMethod(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
-        context.preReflectedMethodInternalCall(implementationClass, klazz, self, name, args, arity.required(), noSuper, block);
+        context.preReflectedMethodInternalCall(implementationClass, klazz, self, name, args, arity.required(), noSuper, block, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postReflectedMethodInternalCall();
     }
 
     private IRubyObject wrap(Ruby runtime, IRubyObject self, IRubyObject[] args, Block block) {
         try {
             return call(self,args,block);
         } catch(RaiseException e) {
             throw e;
         } catch(JumpException e) {
             throw e;
         } catch(ThreadKill e) {
             throw e;
         } catch(MainExitException e) {
             throw e;
         } catch(Exception e) {
             runtime.getJavaSupport().handleNativeException(e);
             return runtime.getNil();
         }        
     }
     
 	public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         Ruby runtime = context.getRuntime();
         arity.checkArity(runtime, args);
 
         if(runtime.getTraceFunction() != null) {
             ISourcePosition position = context.getPosition();
 
             runtime.callTraceFunction(context, "c-call", position, self, name, getImplementationClass());
             try {
                 return wrap(runtime,self,args,block);
             } finally {
                 runtime.callTraceFunction(context, "c-return", position, self, name, getImplementationClass());
             }
         }
         return wrap(runtime,self,args,block);
     }
 
     public abstract IRubyObject call(IRubyObject self, IRubyObject[] args, Block block);
     
 	public DynamicMethod dup() {
         try {
             return (FullInvocationMethod) clone();
         } catch (CloneNotSupportedException e) {
             return null;
         }
     }
 
 	public Arity getArity() {
 		return arity;
 	}
 }
diff --git a/src/org/jruby/internal/runtime/methods/MethodMethod.java b/src/org/jruby/internal/runtime/methods/MethodMethod.java
index 0303372c08..dda641e05b 100644
--- a/src/org/jruby/internal/runtime/methods/MethodMethod.java
+++ b/src/org/jruby/internal/runtime/methods/MethodMethod.java
@@ -1,74 +1,74 @@
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
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.RubyUnboundMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * 
  * @author jpetersen
  */
 public class MethodMethod extends DynamicMethod {
     private RubyUnboundMethod method;
 
     /**
      * Constructor for MethodMethod.
      * @param visibility
      */
     public MethodMethod(RubyModule implementationClass, RubyUnboundMethod method, Visibility visibility) {
         super(implementationClass, visibility);
         this.method = method;
     }
     
     public void preMethod(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
-        context.preMethodCall(implementationClass, klazz, self, name, args, 0, block, noSuper);
+        context.preMethodCall(implementationClass, klazz, self, name, args, 0, block, noSuper, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postMethodCall();
     }
 
     /**
      * @see org.jruby.runtime.ICallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         return method.bind(self, block).call(args, block);
     }
     
     public DynamicMethod dup() {
         return new MethodMethod(getImplementationClass(), method, getVisibility());
     }
 }
diff --git a/src/org/jruby/internal/runtime/methods/ProcMethod.java b/src/org/jruby/internal/runtime/methods/ProcMethod.java
index 83e78eb287..569c4bc8da 100644
--- a/src/org/jruby/internal/runtime/methods/ProcMethod.java
+++ b/src/org/jruby/internal/runtime/methods/ProcMethod.java
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
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby.internal.runtime.methods;
 
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * 
  * @author jpetersen
  */
 public class ProcMethod extends DynamicMethod {
     private RubyProc proc;
 
     /**
      * Constructor for ProcMethod.
      * @param visibility
      */
     public ProcMethod(RubyModule implementationClass, RubyProc proc, Visibility visibility) {
         super(implementationClass, visibility);
         this.proc = proc;
     }
     
     // ENEBO: I doubt this is right...it should be proc.block?
     public void preMethod(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
-        context.preMethodCall(implementationClass, klazz, self, name, args, getArity().required(), block, noSuper);
+        context.preMethodCall(implementationClass, klazz, self, name, args, getArity().required(), block, noSuper, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postMethodCall();
     }
 
     /**
      * @see org.jruby.runtime.ICallable#call(Ruby, IRubyObject, String, IRubyObject[], boolean)
      */
     public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
         return proc.call(args, self, Block.NULL_BLOCK);
     }
     
     public DynamicMethod dup() {
         return new ProcMethod(getImplementationClass(), proc, getVisibility());
     }
     
     public Arity getArity() {
         return proc.getBlock().arity();
     }    
 }
diff --git a/src/org/jruby/internal/runtime/methods/YARVMethod.java b/src/org/jruby/internal/runtime/methods/YARVMethod.java
index 836fecdc56..b542e71055 100644
--- a/src/org/jruby/internal/runtime/methods/YARVMethod.java
+++ b/src/org/jruby/internal/runtime/methods/YARVMethod.java
@@ -1,196 +1,196 @@
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
 package org.jruby.internal.runtime.methods;
 
 import java.util.ArrayList;
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 import org.jruby.ast.executable.YARVMachine;
 import org.jruby.ast.executable.ISeqPosition;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  * @version $Revision: 1.2 $
  */
 public class YARVMethod extends DynamicMethod {
     private SinglyLinkedList cref;
     private YARVMachine.InstructionSequence iseq;
     private StaticScope staticScope;
     private Arity arity;
 
     public YARVMethod(RubyModule implementationClass, YARVMachine.InstructionSequence iseq, StaticScope staticScope, Visibility visibility, SinglyLinkedList cref) {
         super(implementationClass, visibility);
         this.staticScope = staticScope;
         this.iseq = iseq;
 		this.cref = cref;
 
         boolean opts = iseq.args_arg_opts > 0 || iseq.args_rest > 0;
         boolean req = iseq.args_argc > 0;
         if(!req && !opts) {
             this.arity = Arity.noArguments();
         } else if(req && !opts) {
             this.arity = Arity.fixed(iseq.args_argc);
         } else if(opts && !req) {
             this.arity = Arity.optional();
         } else {
             this.arity = Arity.required(iseq.args_argc);
         }
     }
     
     public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, 
             IRubyObject[] args, boolean noSuper, Block block) {
-        context.preDefMethodInternalCall(clazz, name, self, args, arity.required(), block, noSuper, cref, staticScope);
+        context.preDefMethodInternalCall(clazz, name, self, args, arity.required(), block, noSuper, cref, staticScope, this);
     }
     
     public void postMethod(ThreadContext context) {
         context.postDefMethodInternalCall();
     }
 
     public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
     	assert args != null;
         
         Ruby runtime = context.getRuntime();
         
         try {
             prepareArguments(context, runtime, args);
             getArity().checkArity(runtime, args);
 
             traceCall(context, runtime, self, name);
 
             DynamicScope sc = new DynamicScope(staticScope,null);
             for(int i = 0; i<args.length; i++) {
                 sc.setValue(i,args[i],0);
             }
 
             return YARVMachine.INSTANCE.exec(context, self, sc, iseq.body);
         } catch (JumpException je) {
         	if (je.getJumpType() == JumpException.JumpType.ReturnJump && je.getTarget() == this) {
 	                return (IRubyObject) je.getValue();
         	}
             
        		throw je;
         } finally {
             traceReturn(context, runtime, self, name);
         }
     }
 
     private void prepareArguments(ThreadContext context, Ruby runtime, IRubyObject[] args) {
         context.setPosition(new ISeqPosition(iseq));
 
         int expectedArgsCount = iseq.args_argc;
         int restArg = iseq.args_rest;
         boolean hasOptArgs = iseq.args_arg_opts > 0;
 
         if (expectedArgsCount > args.length) {
             throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
         }
 
         // optArgs and restArgs require more work, so isolate them and ArrayList creation here
         if (hasOptArgs || restArg != -1) {
             args = prepareOptOrRestArgs(context, runtime, args, expectedArgsCount, restArg, hasOptArgs);
         }
         
         context.setFrameArgs(args);
     }
 
     private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, Ruby runtime, IRubyObject[] args, int expectedArgsCount, int restArg, boolean hasOptArgs) {
         if (restArg == 0 && hasOptArgs) {
             int opt = expectedArgsCount + iseq.args_arg_opts;
 
             if (opt < args.length) {
                 throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for " + opt + ")");
             }
         }
         
         int count = expectedArgsCount + iseq.args_arg_opts + iseq.args_rest;
 
         ArrayList allArgs = new ArrayList();
         
         // Combine static and optional args into a single list allArgs
         for (int i = 0; i < count && i < args.length; i++) {
             allArgs.add(args[i]);
         }
 
         if (restArg != 0) {
             for (int i = expectedArgsCount; i < args.length; i++) {
                 allArgs.add(args[i]);
             }
 
             // only set in scope if named
             if (restArg >= 0) {
                 RubyArray array = runtime.newArray(args.length - expectedArgsCount);
                 for (int i = expectedArgsCount; i < args.length; i++) {
                     array.append(args[i]);
                 }
 
                 context.getCurrentScope().setValue(restArg, array, 0);
             }
         }
         
         args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
         return args;
     }
 
     private void traceReturn(ThreadContext context, Ruby runtime, IRubyObject receiver, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
 
         ISourcePosition position = context.getPreviousFramePosition();
         runtime.callTraceFunction(context, "return", position, receiver, name, getImplementationClass());
     }
 
     private void traceCall(ThreadContext context, Ruby runtime, IRubyObject receiver, String name) {
         if (runtime.getTraceFunction() == null) {
             return;
         }
 
 		ISourcePosition position = context.getPosition(); 
 
 		runtime.callTraceFunction(context, "call", position, receiver, name, getImplementationClass());
     }
 
     public Arity getArity() {
         return this.arity;
     }
     
     public DynamicMethod dup() {
         return new YARVMethod(getImplementationClass(), iseq, staticScope, getVisibility(), cref);
     }	
 }// YARVMethod
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 62265e729c..ce53c7c90d 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,821 +1,824 @@
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
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyModule;
 import org.jruby.RubyThread;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.lexer.yacc.SourcePositionFactory;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  * @author jpetersen
  */
 public class ThreadContext {
     public static synchronized ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         
         return context;
     }
     
     private final static int INITIAL_SIZE = 50;
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     //private UnsynchronizedStack crefStack;
     private SinglyLinkedList[] crefStack = new SinglyLinkedList[INITIAL_SIZE];
     private int crefIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private String[] catchStack = new String[INITIAL_SIZE];
     private int catchIndex = -1;
     
     private ISourcePosition sourcePosition = new SourcePositionFactory(null).getDummyPosition();
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         pushScope(new DynamicScope(new LocalStaticScope(null), null));
     }
     
     CallType lastCallType;
     
     public Ruby getRuntime() {
         return runtime;
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
     
     public Visibility getLastVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public CallType getLastCallType() {
         return lastCallType;
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
     
     private void expandFramesIfNecessary() {
         if (frameIndex + 1 == frameStack.length) {
             int newSize = frameStack.length * 2;
             Frame[] newFrameStack = new Frame[newSize];
             
             System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
             
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
     
     private void expandCrefsIfNecessary() {
         if (crefIndex + 1 == crefStack.length) {
             int newSize = crefStack.length * 2;
             SinglyLinkedList[] newCrefStack = new SinglyLinkedList[newSize];
             
             System.arraycopy(crefStack, 0, newCrefStack, 0, crefStack.length);
             
             crefStack = newCrefStack;
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
     
     public IRubyObject getLastline() {
         return getCurrentScope().getLastLine();
     }
     
     public void setLastline(IRubyObject value) {
         getCurrentScope().setLastLine(value);
     }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         if (catchIndex + 1 == catchStack.length) {
             int newSize = catchStack.length * 2;
             String[] newCatchStack = new String[newSize];
             
             System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
             catchStack = newCatchStack;
         }
     }
     
     public void pushCatch(String catchSymbol) {
         catchStack[++catchIndex] = catchSymbol;
         expandCatchIfNecessary();
     }
     
     public void popCatch() {
         catchIndex--;
     }
     
     public String[] getActiveCatches() {
         if (catchIndex < 0) return new String[0];
         
         String[] activeCatches = new String[catchIndex + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, catchIndex + 1);
         return activeCatches;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         pushFrame(getCurrentFrame().duplicate());
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
-                               IRubyObject self, IRubyObject[] args, int req, Block block) {
-        pushFrame(new Frame(clazz, self, name, args, req, block, getPosition(), null));        
+                               IRubyObject self, IRubyObject[] args, int req, Block block, Object jumpTarget) {
+        pushFrame(new Frame(clazz, self, name, args, req, block, getPosition(), jumpTarget));        
     }
     
     private void pushFrame() {
         pushFrame(new Frame(getPosition()));
     }
     
     private void pushFrame(Frame frame) {
         frameStack[++frameIndex] = frame;
         expandFramesIfNecessary();
     }
     
     private void popFrame() {
         Frame frame = (Frame)frameStack[frameIndex];
         frameStack[frameIndex--] = null;
         
         setPosition(frame.getPosition());
     }
     
     public Frame getCurrentFrame() {
         return (Frame)frameStack[frameIndex];
     }
     
     public Frame getPreviousFrame() {
         int size = frameIndex + 1;
         return size <= 1 ? null : (Frame) frameStack[size - 2];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject[] getFrameArgs() {
         return getCurrentFrame().getArgs();
     }
     
     public void setFrameArgs(IRubyObject[] args) {
         getCurrentFrame().setArgs(args);
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public Object getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public void setFrameJumpTarget(Object target) {
         getCurrentFrame().setJumpTarget(target);
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public ISourcePosition getFramePosition() {
         return getCurrentFrame().getPosition();
     }
     
     public ISourcePosition getPreviousFramePosition() {
         return getPreviousFrame().getPosition();
     }
     
     public ISourcePosition getPosition() {
         return sourcePosition;
     }
     
     public String getSourceFile() {
         return sourcePosition.getFile();
     }
     
     public int getSourceLine() {
         return sourcePosition.getEndLine();
     }
     
     public void setPosition(ISourcePosition position) {
         sourcePosition = position;
     }
     
     public IRubyObject getBackref() {
         IRubyObject value = getCurrentScope().getBackRef();
         
         // DynamicScope does not preinitialize these values since they are virtually
         // never used.
         return value == null ? runtime.getNil() : value;
     }
     
     public void setBackref(IRubyObject backref) {
         getCurrentScope().setBackRef(backref);
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
     
     public SinglyLinkedList peekCRef() {
         return (SinglyLinkedList)crefStack[crefIndex];
     }
     
     public void setCRef(SinglyLinkedList newCRef) {
         crefStack[++crefIndex] = newCRef;
         expandCrefsIfNecessary();
     }
     
     public void unsetCRef() {
         crefStack[crefIndex--] = null;
     }
     
     public SinglyLinkedList pushCRef(RubyModule newModule) {
         if (crefIndex == -1) {
             crefStack[++crefIndex] = new SinglyLinkedList(newModule, null);
         } else {
             crefStack[crefIndex] = new SinglyLinkedList(newModule, (SinglyLinkedList)crefStack[crefIndex]);
         }
         
         return (SinglyLinkedList)peekCRef();
     }
     
     public RubyModule popCRef() {
         assert !(crefIndex == -1) : "Tried to pop from empty CRef stack";
         
         RubyModule module = (RubyModule)peekCRef().getValue();
         
         SinglyLinkedList next = ((SinglyLinkedList)crefStack[crefIndex--]).getNext();
         
         if (next != null) {
             crefStack[++crefIndex] = next;
         } else {
             crefStack[crefIndex+1] = null;
         }
         
         return module;
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         assert currentModule != null : "Can't push null RubyClass";
         
         parentStack[++parentIndex] = currentModule;
         expandParentsIfNecessary();
     }
     
     public RubyModule popRubyClass() {
         RubyModule ret = (RubyModule)parentStack[parentIndex];
         parentStack[parentIndex--] = null;
         return ret;
     }
     
     public RubyModule getRubyClass() {
         assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
         
         RubyModule parentModule = (RubyModule)parentStack[parentIndex];
         
         return parentModule.getNonIncludedClass();
     }
     
     public RubyModule getBindingRubyClass() {
         RubyModule parentModule = null;
         if(parentIndex == 0) {
             parentModule = (RubyModule)parentStack[parentIndex];
         } else {
             parentModule = (RubyModule)parentStack[parentIndex-1];
             
         }
         return parentModule.getNonIncludedClass();
     }
     
     public boolean isTopLevel() {
         return parentIndex == 0;
     }
     
     public boolean getConstantDefined(String name) {
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         for (SinglyLinkedList cbase = peekCRef(); cbase != null; cbase = cbase.getNext()) {
             result = ((RubyModule) cbase.getValue()).getConstantAt(name);
             if (result != null || runtime.getLoadService().autoload(name) != null) {
                 return true;
             }
         }
         
         return false;
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String name) {
         //RubyModule self = state.threadContext.getRubyClass();
         SinglyLinkedList cbase = peekCRef();
         RubyClass object = runtime.getObject();
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = (RubyModule) cbase.getValue();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             result = klass.getConstantAt(name);
             if (result == null) {
                 if (runtime.getLoadService().autoload(name) != null) {
                     continue;
                 }
             } else {
                 return result;
             }
             cbase = cbase.getNext();
         } while (cbase != null && cbase.getValue() != object);
         
         return ((RubyModule) peekCRef().getValue()).getConstant(name);
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String name, IRubyObject result) {
         RubyModule module;
 
         // FIXME: why do we check RubyClass and then use CRef?
         if (getRubyClass() == null) {
             // TODO: wire into new exception handling mechanism
             throw runtime.newTypeError("no class/module to define constant");
         }
         module = (RubyModule) peekCRef().getValue();
    
         setConstantInModule(name, module, result);
    
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInModule(String name, RubyModule module, IRubyObject result) {
         ((RubyModule) module).setConstant(name, result);
    
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String name, IRubyObject result) {
         setConstantInModule(name, runtime.getObject(), result);
    
         return result;
     }
     
     public IRubyObject getConstant(String name, RubyModule module) {
         //RubyModule self = state.threadContext.getRubyClass();
         SinglyLinkedList cbase = module.getCRef();
         IRubyObject result = null;
         
         // flipped from while to do to search current class first
         do {
             RubyModule klass = (RubyModule) cbase.getValue();
             
             // Not sure how this can happen
             //if (NIL_P(klass)) return rb_const_get(CLASS_OF(self), id);
             result = klass.getConstantAt(name);
             if (result == null) {
                 if (runtime.getLoadService().autoload(name) != null) {
                     continue;
                 }
             } else {
                 return result;
             }
             cbase = cbase.getNext();
         } while (cbase != null);
         
         //System.out.println("CREF is " + state.threadContext.getCRef().getValue());
         return ((RubyModule) peekCRef().getValue()).getConstant(name);
     }
     
     private void addBackTraceElement(RubyArray backtrace, Frame frame, Frame previousFrame) {
         StringBuffer sb = new StringBuffer(100);
         ISourcePosition position = frame.getPosition();
         
         if(previousFrame != null && frame.getName() != null && previousFrame.getName() != null &&
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getPosition().getFile().equals(previousFrame.getPosition().getFile()) &&
                 frame.getPosition().getEndLine() == previousFrame.getPosition().getEndLine()) {
             return;
         }
         
         sb.append(position.getFile()).append(':').append(position.getEndLine());
         
         if (previousFrame != null && previousFrame.getName() != null) {
             sb.append(":in `").append(previousFrame.getName()).append('\'');
         } else if (previousFrame == null && frame.getName() != null) {
             sb.append(":in `").append(frame.getName()).append('\'');
         }
         
         backtrace.append(backtrace.getRuntime().newString(sb.toString()));
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createBacktrace(int level, boolean nativeException) {
         RubyArray backtrace = runtime.newArray();
         int traceSize = frameIndex - level;
         
         if (traceSize <= 0) return backtrace;
         
         if (nativeException) {
             // assert level == 0;
             addBackTraceElement(backtrace, frameStack[frameIndex], null);
         }
         
         for (int i = traceSize; i > 0; i--) {
             Frame frame = frameStack[i];
             
             // We are in eval with binding break out early
             if (frame.isBindingFrame()) break;
 
             addBackTraceElement(backtrace, frame, (Frame) frameStack[i-1]);
         }
         
         return backtrace;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         pushCRef(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushCRef(type);
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         pushScope(new DynamicScope(staticScope, getCurrentScope()));
     }
     
     public void postClassEval() {
         popCRef();
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
 
     public void preMethodCall(RubyModule implementationClass, RubyModule clazz, 
-                              IRubyObject self, String name, IRubyObject[] args, int req, Block block, boolean noSuper) {
+                              IRubyObject self, String name, IRubyObject[] args, int req, Block block, boolean noSuper, Object jumpTarget) {
         pushRubyClass((RubyModule)implementationClass.getCRef().getValue());
-        pushCallFrame(noSuper ? null : clazz, name, self, args, req, block);
+        pushCallFrame(noSuper ? null : clazz, name, self, args, req, block, jumpTarget);
     }
     
     public void postMethodCall() {
         popFrame();
         popRubyClass();
     }
     
     public void preDefMethodInternalCall(RubyModule clazz, String name, 
                                          IRubyObject self, IRubyObject[] args, int req, Block block, boolean noSuper, 
-            SinglyLinkedList cref, StaticScope staticScope) {
+            SinglyLinkedList cref, StaticScope staticScope, Object jumpTarget) {
         RubyModule implementationClass = (RubyModule)cref.getValue();
         setCRef(cref);
-        pushCallFrame(noSuper ? null : clazz, name, self, args, req, block);
+        pushCallFrame(noSuper ? null : clazz, name, self, args, req, block, jumpTarget);
         pushScope(new DynamicScope(staticScope, getCurrentScope()));
         pushRubyClass(implementationClass);
     }
     
     public void postDefMethodInternalCall() {
         popRubyClass();
         popScope();
         popFrame();
         unsetCRef();
     }
     
     public void preCompiledMethod(RubyModule implementationClass, SinglyLinkedList cref) {
         pushRubyClass(implementationClass);
         setCRef(cref);
     }
     
     public void postCompiledMethod() {
         popRubyClass();
         unsetCRef();
     }
     
     // NEW! Push a scope into the frame, since this is now required to use it
     // XXX: This is screwy...apparently Ruby runs internally-implemented methods in their own frames but in the *caller's* scope
-    public void preReflectedMethodInternalCall(RubyModule implementationClass, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, int req, boolean noSuper, Block block) {
+    public void preReflectedMethodInternalCall(
+            RubyModule implementationClass, RubyModule klazz, IRubyObject self, 
+            String name, IRubyObject[] args, int req, boolean noSuper, 
+            Block block, Object jumpTarget) {
         pushRubyClass((RubyModule)implementationClass.getCRef().getValue());
-        pushCallFrame(noSuper ? null : klazz, name, self, args, req, block);
+        pushCallFrame(noSuper ? null : klazz, name, self, args, req, block, jumpTarget);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postReflectedMethodInternalCall() {
         popFrame();
         popRubyClass();
     }
     
     public void preInitCoreClasses() {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
     }
     
     public void preInitBuiltinClasses(RubyClass objectClass, IRubyObject topSelf) {
         pushRubyClass(objectClass);
         setCRef(objectClass.getCRef());
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
-        pushCallFrame(null, null, self, IRubyObject.NULL_ARRAY, 0, Block.NULL_BLOCK);
+        pushCallFrame(null, null, self, IRubyObject.NULL_ARRAY, 0, Block.NULL_BLOCK, null);
         setCRef(rubyClass.getCRef());
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
         unsetCRef();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         pushCRef(executeUnderClass);
-        pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), frame.getArgs(), frame.getRequiredArgCount(), block);
+        pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), frame.getArgs(), frame.getRequiredArgCount(), block, frame.getJumpTarget());
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popRubyClass();
         popCRef();
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
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
     }
     
     public void preForBlock(Block block, RubyModule klass) {
         pushFrame(block.getFrame());
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushScope(block.getDynamicScope());
         pushRubyClass((klass != null) ? klass : block.getKlass());
     }
     
     public void preYieldSpecificBlock(Block block, RubyModule klass) {
         //System.out.println("IN RESTORE BLOCK (" + block.getDynamicScope() + ")");
         pushFrame(block.getFrame());
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushScope(block.getDynamicScope().cloneScope());
         pushRubyClass((klass != null) ? klass : block.getKlass());
     }
     
     public void preEvalWithBinding(Block block) {
         Frame frame = block.getFrame();
         
         frame.setIsBindingFrame(true);
         pushFrame(block.getFrame());
         setCRef(block.getCRef());
         getCurrentFrame().setVisibility(block.getVisibility());
         pushRubyClass(block.getKlass());
     }
     
     public void postEvalWithBinding(Block block) {
         block.getFrame().setIsBindingFrame(false);
         popFrame();
         unsetCRef();
         popRubyClass();
     }
     
     public void postYield() {
         popScope();
         popFrame();
         unsetCRef();
         popRubyClass();
     }
     
     public void preRootNode(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postRootNode() {
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
diff --git a/test/testEval.rb b/test/testEval.rb
index f7161835cc..51e7ba977d 100644
--- a/test/testEval.rb
+++ b/test/testEval.rb
@@ -1,160 +1,160 @@
 require 'test/minirunit'
 
 # ensure binding is setting self correctly
 def x
   "foo"
 end
 
 Z = binding
 
 class A
   def x
     "bar"
   end
 
   def y
     eval("x", Z)
   end
 end
 
 old_self = self
 test_equal(A.new.y, "foo")
 test_equal(x, "foo")
 
 #### ensure self is back to pre bound eval
 test_equal(self, old_self)
 
 #### ensure returns within ensures that cross evalstates during an eval are handled properly (whew!)
 def inContext &proc 
    begin
      proc.call
    ensure
    end
 end
 
 def x2
   inContext do
      return "foo"
   end
 end
 
 test_equal(x2, "foo")
 
 # test that evaling a proc doesn't goof up the module nesting for a binding
 proc_binding = eval("proc{binding}.call", TOPLEVEL_BINDING)
 nesting = eval("$nesting = nil; class A; $nesting = Module.nesting; end; $nesting", TOPLEVEL_BINDING)
 test_equal("A", nesting.to_s)
 
 class Foo
   def initialize(p)
     @prefix = p
   end
 
   def result(val)
     redefine_result
     result val
   end
   
   def redefine_result
     method_decl = "def result(val); \"#{@prefix}: \#\{val\}\"; end"
     instance_eval method_decl, "generated code (#{__FILE__}:#{__LINE__})"
   end
 end
 
 f = Foo.new("foo")
 test_equal "foo: hi", f.result("hi")
 
 g = Foo.new("bar")
 test_equal "bar: hi", g.result("hi")
 
 test_equal "foo: bye", f.result("bye")
 test_equal "bar: bye", g.result("bye")
 
 # JRUBY-214 - eval should call to_str on arg 0
 class Bar
   def to_str
     "magic_number"
   end
 end
 magic_number = 1
 test_equal(magic_number, eval(Bar.new))
 
 test_exception(TypeError) { eval(Object.new) }
 
 # JRUBY-386 tests
 # need at least one arg
 test_exception(ArgumentError) { eval }
 test_exception(ArgumentError) {self.class.module_eval}
 test_exception(ArgumentError) {self.class.class_eval}
 test_exception(ArgumentError) {3.instance_eval}
 
 # args must respond to #to_str
 test_exception(TypeError) {eval 3}
 test_exception(TypeError) {self.class.module_eval 3}
 test_exception(TypeError) {self.class.class_eval 4}
 test_exception(TypeError) {3.instance_eval 4}
 
 begin
   eval 'return'
 rescue LocalJumpError => e
   test_ok(e.message =~ /unexpected return$/)
 end
 
 begin
   eval 'break'
 rescue LocalJumpError => e
   test_ok(e.message =~ /unexpected break$/)
 end
 
 begin
   "".instance_eval 'break'
 rescue LocalJumpError => e
   test_ok(e.message =~ /unexpected break$/)
 end
 
 begin
   "".instance_eval 'return'
 rescue LocalJumpError => e
   test_ok(e.message =~ /unexpected return$/)
 end
 
 # If getBindingRubyClass isn't used, this test case will fail,
 # since when eval gets called, Kernel will get pushed on the
 # parent-stack, and this will always become the RubyClass for
 # the evaled string, which is incorrect.
 class AbcTestFooAbc
   eval <<-ENDT
   def foofoo_foofoo
   end
 ENDT
 end
 
 test_equal ["foofoo_foofoo"], AbcTestFooAbc.instance_methods.grep(/foofoo_foofoo/)
 test_equal [], Object.instance_methods.grep(/foofoo_foofoo/)
 
 # test Binding.of_caller
 def foo
   x = 1
   bar
 end
 
 def bar
   eval "x + 1", Binding.of_caller
 end
 
 test_equal(2, foo)
 
 # test returns within an eval
 def foo
   eval 'return 1'
   return 2
 end
 def foo2
   x = "blah"
   x.instance_eval "return 1"
   return 2
 end
 
 test_equal(1, foo)
 # this case is still broken
-#test_equal(1, foo2)
\ No newline at end of file
+test_equal(1, foo2)
