diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index 0ca63486f6..0a184de8ed 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,302 +1,352 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
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
 package org.jruby;
 
+import java.io.IOException;
 import java.util.HashMap;
-
+import java.util.Iterator;
+import java.util.Map;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
+import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.collections.SinglyLinkedList;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyClass extends RubyModule {
 	
 	private final IRuby runtime;
     
     // the default allocator
     private final ObjectAllocator allocator;
+    
+    private ObjectMarshal marshal;
+    
+    private static final ObjectMarshal DEFAULT_OBJECT_MARSHAL = new ObjectMarshal() {
+        public void marshalTo(IRuby runtime, Object obj, RubyClass type,
+                              MarshalStream marshalStream) throws IOException {
+            IRubyObject object = (IRubyObject)obj;
+            
+            marshalStream.dumpDefaultObjectHeader(type);
+            
+            Map iVars = object.getInstanceVariablesSnapshot();
+            
+            marshalStream.dumpInstanceVars(iVars);
+        }
+
+        public Object unmarshalFrom(IRuby runtime, RubyClass type,
+                                    UnmarshalStream unmarshalStream) throws IOException {
+            IRubyObject result = type.allocate();
+            
+            unmarshalStream.registerLinkTarget(result);
+
+            unmarshalStream.defaultInstanceVarsUnmarshal(result);
+
+            return result;
+        }
+    };
 
     /**
      * @mri rb_boot_class
      */
+    
+    /**
+     * @mri rb_boot_class
+     */
     protected RubyClass(RubyClass superClass, ObjectAllocator allocator) {
-        super(superClass.getRuntime(), superClass.getRuntime().getClass("Class"), superClass, null, null);
+        this(superClass.getRuntime(), superClass.getRuntime().getClass("Class"), superClass, allocator, null, null);
 
         infectBy(superClass);
-        this.runtime = superClass.getRuntime();
-        this.allocator = allocator;
     }
 
     protected RubyClass(IRuby runtime, RubyClass superClass, ObjectAllocator allocator) {
-        super(runtime, null, superClass, null, null);
-        this.allocator = allocator;
-        this.runtime = runtime;
+        this(runtime, null, superClass, allocator, null, null);
     }
 
     protected RubyClass(IRuby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator) {
-        super(runtime, metaClass, superClass, null, null);
-        this.allocator = allocator;
-        this.runtime = runtime;
+        this(runtime, metaClass, superClass, allocator, null, null);
     }
     
     protected RubyClass(IRuby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass, superClass, parentCRef, name);
         this.allocator = allocator;
         this.runtime = runtime;
+        
+        // use superclass's marshal by default, or the default marshal
+        if (superClass != null) {
+            this.marshal = superClass.getMarshal();
+        } else {
+            this.marshal = DEFAULT_OBJECT_MARSHAL;
+        }
     }
     
     public final IRubyObject allocate() {
         return getAllocator().allocate(getRuntime(), this);
     }
     
+    public final ObjectMarshal getMarshal() {
+        return marshal;
+    }
+    
+    public final void setMarshal(ObjectMarshal marshal) {
+        this.marshal = marshal;
+    }
+    
+    public final void marshal(Object obj, MarshalStream marshalStream) throws IOException {
+        getMarshal().marshalTo(getRuntime(), obj, this, marshalStream);
+    }
+    
+    public final Object unmarshal(UnmarshalStream unmarshalStream) throws IOException {
+        return getMarshal().unmarshalFrom(getRuntime(), this, unmarshalStream);
+    }
+    
     public static RubyClass newClassClass(IRuby runtime, RubyClass moduleClass) {
         ObjectAllocator defaultAllocator = new ObjectAllocator() {
             public IRubyObject allocate(IRuby runtime, RubyClass klass) {
                 IRubyObject instance = new RubyObject(runtime, klass);
                 instance.setMetaClass(klass);
 
                 return instance;
             }
         };
         
         return new RubyClass(
                 runtime,
                 null /* FIXME: should be something else? */,
                 moduleClass,
                 defaultAllocator,
                 null,
                 "Class");
     }
     
     /* (non-Javadoc)
 	 * @see org.jruby.RubyObject#getRuntime()
 	 */
 	public IRuby getRuntime() {
 		return runtime;
 	}
 
     public boolean isModule() {
         return false;
     }
 
     public boolean isClass() {
         return true;
     }
 
     public static void createClassClass(RubyClass classClass) {
         CallbackFactory callbackFactory = classClass.getRuntime().callbackFactory(RubyClass.class);
         classClass.defineSingletonMethod("new", callbackFactory.getOptSingletonMethod("newClass"));
         classClass.defineFastMethod("allocate", callbackFactory.getFastMethod("allocate"));
         classClass.defineMethod("new", callbackFactory.getOptMethod("newInstance"));
         classClass.defineMethod("superclass", callbackFactory.getMethod("superclass"));
         classClass.defineSingletonMethod("inherited", callbackFactory.getSingletonMethod("inherited", IRubyObject.class));
         classClass.undefineMethod("module_function");
     }
     
     public static IRubyObject inherited(IRubyObject recv, IRubyObject arg, Block block) {
         return recv.getRuntime().getNil();
     }
 
     /** Invokes if  a class is inherited from an other  class.
      * 
      * MRI: rb_class_inherited
      * 
      * @since Ruby 1.6.7
      * 
      */
     public void inheritedBy(RubyClass superType) {
         if (superType == null) {
             superType = getRuntime().getObject();
         }
         superType.callMethod(getRuntime().getCurrentContext(), "inherited", this);
     }
 
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
         if (!isSingleton()) {
             return this;
         }
 
         MetaClass clone = new MetaClass(getRuntime(), getMetaClass(), getMetaClass().getAllocator(), getSuperClass().getCRef());
         clone.initCopy(this);
         clone.setInstanceVariables(new HashMap(getInstanceVariables()));
 
         return (RubyClass) cloneMethods(clone);
     }
 
     public boolean isSingleton() {
         return false;
     }
 
     public RubyClass getMetaClass() {
         RubyClass type = super.getMetaClass();
 
         return type != null ? type : getRuntime().getClass("Class");
     }
 
     public RubyClass getRealClass() {
         return this;
     }
 
     public MetaClass newSingletonClass(SinglyLinkedList parentCRef) {
         MetaClass newClass = new MetaClass(getRuntime(), this, this.getAllocator(), parentCRef);
         newClass.infectBy(this);
         return newClass;
     }
 
     public static RubyClass newClass(IRuby runtime, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
         return new RubyClass(runtime, runtime.getClass("Class"), superClass, superClass.getAllocator(), parentCRef, name);
     }
 
     /** Create a new subclass of this class.
      * @return the new sublass
      * @throws TypeError if this is class `Class'
      * @mri rb_class_new
      */
     protected RubyClass subclass() {
         if (this == getRuntime().getClass("Class")) {
             throw getRuntime().newTypeError("can't make subclass of Class");
         }
         return new RubyClass(this, getAllocator());
     }
 
     /** rb_class_new_instance
      *
      */
     public IRubyObject newInstance(IRubyObject[] args, Block block) {
         IRubyObject obj = (IRubyObject) allocate();
         obj.callInit(args, block);
         return obj;
     }
     
     public ObjectAllocator getAllocator() {
         return allocator;
     }
 
     /** rb_class_s_new
      *
      */
     public static RubyClass newClass(IRubyObject recv, IRubyObject[] args, Block block) {
         final IRuby runtime = recv.getRuntime();
 
         RubyClass superClass;
         if (args.length > 0) {
             if (args[0] instanceof RubyClass) {
                 superClass = (RubyClass) args[0];
             } else {
                 throw runtime.newTypeError(
                     "wrong argument type " + args[0].getType().getName() + " (expected Class)");
             }
         } else {
             superClass = runtime.getObject();
         }
 
         ThreadContext tc = runtime.getCurrentContext();
         // use allocator of superclass, since this will be a pure Ruby class
         RubyClass newClass = superClass.newSubClass(null, superClass.getAllocator(),tc.peekCRef());
 
         // call "initialize" method
         newClass.callInit(args, block);
 
         // call "inherited" method of the superclass
         newClass.inheritedBy(superClass);
 
 		if (block != null) block.yield(tc, null, newClass, newClass, false);
 
 		return newClass;
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */
     public IRubyObject superclass(Block block) {
         RubyClass superClass = getSuperClass();
         while (superClass != null && superClass.isIncluded()) {
             superClass = superClass.getSuperClass();
         }
 
         return superClass != null ? superClass : getRuntime().getNil();
     }
 
     /** rb_class_s_inherited
      *
      */
     public static IRubyObject inherited(RubyClass recv) {
         throw recv.getRuntime().newTypeError("can't make subclass of Class");
     }
 
     public void marshalTo(MarshalStream output) throws java.io.IOException {
         output.write('c');
         output.dumpString(getName());
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream output) throws java.io.IOException {
         return (RubyClass) RubyModule.unmarshalFrom(output);
     }
 
     public RubyClass newSubClass(String name, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         RubyClass classClass = runtime.getClass("Class");
         
         // Cannot subclass 'Class' or metaclasses
         if (this == classClass) {
             throw runtime.newTypeError("can't make subclass of Class");
         } else if (this instanceof MetaClass) {
             throw runtime.newTypeError("can't make subclass of virtual class");
         }
 
         RubyClass newClass = new RubyClass(runtime, classClass, this, allocator, parentCRef, name);
 
         newClass.makeMetaClass(getMetaClass(), newClass.getCRef());
         newClass.inheritedBy(this);
 
         if(null != name) {
             ((RubyModule)parentCRef.getValue()).setConstant(name, newClass);
         }
 
         return newClass;
     }
     
     protected IRubyObject doClone() {
     	return RubyClass.newClass(getRuntime(), getSuperClass(), null/*FIXME*/, getBaseName());
     }
 }
diff --git a/src/org/jruby/RubyMarshal.java b/src/org/jruby/RubyMarshal.java
index 7f4a118c5d..7e0edb339b 100644
--- a/src/org/jruby/RubyMarshal.java
+++ b/src/org/jruby/RubyMarshal.java
@@ -1,151 +1,151 @@
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
  * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2003 Thomas E Enebo <enebo@acm.org>
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
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  * Marshal module
  *
  * @author Anders
  */
 public class RubyMarshal {
 
     public static RubyModule createMarshalModule(IRuby runtime) {
         RubyModule module = runtime.defineModule("Marshal");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyMarshal.class);
 
         module.defineSingletonMethod("dump", callbackFactory.getOptSingletonMethod("dump"));
         module.defineSingletonMethod("load", callbackFactory.getOptSingletonMethod("load"));
         module.defineSingletonMethod("restore", callbackFactory.getOptSingletonMethod("load"));
         module.defineConstant("MAJOR_VERSION", runtime.newFixnum(Constants.MARSHAL_MAJOR));
         module.defineConstant("MINOR_VERSION", runtime.newFixnum(Constants.MARSHAL_MINOR));
 
         return module;
     }
 
     public static IRubyObject dump(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         if (args.length < 1) {
             throw recv.getRuntime().newArgumentError("wrong # of arguments(at least 1)");
         }
         IRubyObject objectToDump = args[0];
 
         RubyIO io = null;
         int depthLimit = -1;
 
         if (args.length >= 2) {
             if (args[1] instanceof RubyIO) {
                 io = (RubyIO) args[1];
             } else if (args[1] instanceof RubyFixnum) {
                 depthLimit = (int) ((RubyFixnum) args[1]).getLongValue();
             }
             if (args.length == 3) {
                 depthLimit = (int) ((RubyFixnum) args[2]).getLongValue();
             }
         }
 
         try {
             if (io != null) {
                 dumpToStream(objectToDump, io.getOutStream(), depthLimit);
                 return io;
             }
 			ByteArrayOutputStream stringOutput = new ByteArrayOutputStream();
 			dumpToStream(objectToDump, stringOutput, depthLimit);
 			return RubyString.newString(recv.getRuntime(), stringOutput.toByteArray());
 
         } catch (IOException ioe) {
             throw recv.getRuntime().newIOErrorFromException(ioe);
         }
 
     }
 
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         try {
             if (args.length < 1) {
                 throw recv.getRuntime().newArgumentError("wrong number of arguments (0 for 1)");
             }
             
             if (args.length > 2) {
             	throw recv.getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 2)");
             }
             
             IRubyObject in = null;
             IRubyObject proc = null;
 
             switch (args.length) {
             case 2:
             	proc = args[1];
             case 1:
             	in = args[0];
             }
 
             InputStream rawInput;
             if (in instanceof RubyIO) {
                 rawInput = ((RubyIO) in).getInStream();
             } else if (in.respondsTo("to_str")) {
                 RubyString inString = (RubyString) in.callMethod(recv.getRuntime().getCurrentContext(), "to_str");
                 rawInput = new ByteArrayInputStream(inString.toByteArray());
             } else {
                 throw recv.getRuntime().newTypeError("instance of IO needed");
             }
             
-            UnmarshalStream input = new UnmarshalStream(recv.getRuntime(), rawInput);
+            UnmarshalStream input = new UnmarshalStream(recv.getRuntime(), rawInput, proc);
 
-            return input.unmarshalObject(proc);
+            return input.unmarshalObject();
 
         } catch (EOFException ee) {
             throw recv.getRuntime().newEOFError();
         } catch (IOException ioe) {
             throw recv.getRuntime().newIOErrorFromException(ioe);
         }
     }
 
     private static void dumpToStream(IRubyObject object, OutputStream rawOutput, int depthLimit)
         throws IOException
     {
         MarshalStream output = new MarshalStream(object.getRuntime(), rawOutput, depthLimit);
         output.dumpObject(object);
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 86cb4f2eb9..f22db28746 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -225,1063 +225,1052 @@ public class RubyObject implements Cloneable, IRubyObject {
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
 
     /** rb_define_singleton_method
      *
      */
     public void defineFastSingletonMethod(String name, Callback method) {
         getSingletonClass().defineFastMethod(name, method);
     }
 
     public void addSingletonMethod(String name, DynamicMethod method) {
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
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, null);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, null);
     }
     
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, getMetaClass(), name, args, callType, block);
     }
 
     /**
      * Used by the compiler to ease calling indexed methods
      */
     public IRubyObject callMethod(ThreadContext context, byte methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, getRuntime().getSelectorTable().table[module.index][methodIndex], name, args, callType, block);
         } 
             
         return callMethod(context, module, name, args, callType, block);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, name, args, callType, null);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, rubyclass, name, args, callType, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
 
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
                 return RubyKernel.method_missing(this, args, block);
             }
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(getRuntime(), name);
 
             return callMethod(context, "method_missing", newArgs, block);
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
 
         return method.call(context, this, implementer, name, args, false, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, null);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, block);
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
 
     	if (!IdUtil.isInstanceVariable(varName)) {
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
 
     	if (!IdUtil.isInstanceVariable(varName)) {
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
         return EvaluationState.eval(getRuntime().getCurrentContext(), n, this, null);
     }
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
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
 
     protected String trueFalseNil(String v) {
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
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block != null) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, block);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
 
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
         
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         args[0].convertToString();
         
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
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
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
         }, new IRubyObject[] { this, src, file, line }, null);
     }
 
     private IRubyObject yieldUnder(RubyModule under, Block block) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return block.yield(context, valueInYield, selfInYield, context.getRubyClass(), false);
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
         }, new IRubyObject[] { this }, block);
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
 
             result = EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
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
         try {
             return EvaluationState.eval(threadContext, getRuntime().parse(src.toString(), file, threadContext.getCurrentScope()), this, null);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             // restore position
             threadContext.setPosition(savedPosition);
         }
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject obj_equal(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
 //        if (isNil()) {
 //            return getRuntime().newBoolean(obj.isNil());
 //        }
 //        return getRuntime().newBoolean(this == obj);
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
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
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
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
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
         RubyClass realClass = getMetaClass().getRealClass();
     	return realClass.getAllocator().allocate(getRuntime(), realClass);
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
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     if(IdUtil.isInstanceVariable(name)) {
                         part.append(" ");
                         part.append(sep);
                         part.append(name);
                         part.append("=");
                         part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                         sep = ",";
                     }
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
             if (IdUtil.isInstanceVariable(name)) {
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
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(checkArgumentCount(args,0,1) == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && ((type instanceof MetaClass) || (all && type.isIncluded()));
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type && !(all && type.isIncluded())) {
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
 
     public IRubyObject instance_eval(IRubyObject[] args, Block block) {
         return specificEval(getSingletonClass(), args, block);
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
 
     public IRubyObject inherited(IRubyObject arg, Block block) {
     	return getRuntime().getNil();
     }
     public IRubyObject initialize(IRubyObject[] args, Block block) {
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args, Block block) {
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
     public IRubyObject send(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name, Block block) {
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
-        output.write('o');
-        RubySymbol classname = RubySymbol.newSymbol(getRuntime(), getMetaClass().getName());
-        output.dumpObject(classname);
-        Map iVars = getInstanceVariablesSnapshot();
-        output.dumpInt(iVars.size());
-        for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
-            String name = (String) iter.next();
-            IRubyObject value = (IRubyObject)iVars.get(name);
-            
-            output.dumpObject(RubySymbol.newSymbol(getRuntime(), name));
-            output.dumpObject(value);
-        }
+        getMetaClass().marshal(this, output);
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
     public IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional) {
         int total = required+optional;
         int real = checkArgumentCount(args,required,total);
         IRubyObject[] narr = new IRubyObject[total];
         System.arraycopy(args,0,narr,0,real);
         for(int i=real; i<total; i++) {
             narr[i] = getRuntime().getNil();
         }
         return narr;
     }
 
     private transient Object dataStruct;
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         this.dataStruct = obj;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return dataStruct;
     }
 
     /** rb_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if(this == other || callMethod(getRuntime().getCurrentContext(), "==",other).isTrue()){
             return getRuntime().getTrue();
         }
  
         return getRuntime().getFalse();
     }
 }
diff --git a/src/org/jruby/RubyRange.java b/src/org/jruby/RubyRange.java
index 30d1459795..2711075e11 100644
--- a/src/org/jruby/RubyRange.java
+++ b/src/org/jruby/RubyRange.java
@@ -1,384 +1,427 @@
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
  * Copyright (C) 2001 Ed Sinjiashvili <slorcim@users.sourceforge.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
+import java.io.IOException;
+import java.util.HashMap;
+import java.util.Map;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
+import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
+import org.jruby.runtime.marshal.MarshalStream;
+import org.jruby.runtime.marshal.UnmarshalStream;
 
 /**
  * @author jpetersen
  */
 public class RubyRange extends RubyObject {
 
     private IRubyObject begin;
     private IRubyObject end;
     private boolean isExclusive;
 
     public RubyRange(IRuby runtime, RubyClass impl) {
         super(runtime, impl);
     }
 
     public void init(IRubyObject aBegin, IRubyObject aEnd, RubyBoolean aIsExclusive) {
         if (!(aBegin instanceof RubyFixnum && aEnd instanceof RubyFixnum)) {
             try {
                 aBegin.callMethod(getRuntime().getCurrentContext(), "<=>", aEnd);
             } catch (RaiseException rExcptn) {
                 throw getRuntime().newArgumentError("bad value for range");
             }
         }
 
         this.begin = aBegin;
         this.end = aEnd;
         this.isExclusive = aIsExclusive.isTrue();
     }
     
     private static ObjectAllocator RANGE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(IRuby runtime, RubyClass klass) {
             return new RubyRange(runtime, klass);
         }
     };
+    
+    private static final ObjectMarshal RANGE_MARSHAL = new ObjectMarshal() {
+        public void marshalTo(IRuby runtime, Object obj, RubyClass type,
+                              MarshalStream marshalStream) throws IOException {
+            RubyRange range = (RubyRange)obj;
+            
+            marshalStream.dumpDefaultObjectHeader(type);
+            
+            // FIXME: This is a pretty inefficient way to do this, but we need child class
+            // ivars and begin/end together
+            Map iVars = new HashMap(range.getInstanceVariables());
+            
+            // add our "begin" and "end" instance vars to the collection
+            iVars.put("begin", range.begin);
+            iVars.put("end", range.end);
+            iVars.put("excl", range.isExclusive? runtime.getTrue() : runtime.getFalse());
+            
+            marshalStream.dumpInstanceVars(iVars);
+        }
+
+        public Object unmarshalFrom(IRuby runtime, RubyClass type,
+                                    UnmarshalStream unmarshalStream) throws IOException {
+            RubyRange range = (RubyRange)type.allocate();
+            
+            unmarshalStream.registerLinkTarget(range);
 
+            unmarshalStream.defaultInstanceVarsUnmarshal(range);
+            
+            range.begin = range.getInstanceVariable("begin");
+            range.end = range.getInstanceVariable("end");
+
+            return range;
+        }
+    };
+    
     public static RubyClass createRangeClass(IRuby runtime) {
         RubyClass result = runtime.defineClass("Range", runtime.getObject(), RANGE_ALLOCATOR);
+        
+        result.setMarshal(RANGE_MARSHAL);
+        
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyRange.class);
         
         result.includeModule(runtime.getModule("Enumerable"));
 
         result.defineMethod("==", callbackFactory.getMethod("equal", IRubyObject.class));
         result.defineFastMethod("begin", callbackFactory.getFastMethod("first"));
         result.defineMethod("each", callbackFactory.getMethod("each"));
         result.defineFastMethod("end", callbackFactory.getFastMethod("last"));
         result.defineFastMethod("exclude_end?", callbackFactory.getFastMethod("exclude_end_p"));
         result.defineFastMethod("first", callbackFactory.getFastMethod("first"));
         result.defineFastMethod("hash", callbackFactory.getFastMethod("hash"));
         result.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         result.defineMethod("inspect", callbackFactory.getMethod("inspect"));
         result.defineFastMethod("last", callbackFactory.getFastMethod("last"));
         result.defineMethod("length", callbackFactory.getMethod("length"));
         result.defineMethod("size", callbackFactory.getMethod("length"));
         result.defineMethod("step", callbackFactory.getOptMethod("step"));
         result.defineMethod("to_s", callbackFactory.getMethod("to_s"));
 
         result.defineMethod("to_a", callbackFactory.getMethod("to_a"));
         result.defineMethod("include?", callbackFactory.getMethod("include_p", IRubyObject.class));
-		// We override Enumerable#member? since ranges in 1.8.1 are continuous.
-		result.defineAlias("member?", "include?");
+        // We override Enumerable#member? since ranges in 1.8.1 are continuous.
+        result.defineAlias("member?", "include?");
         result.defineAlias("===", "include?");
         
-		CallbackFactory classCB = runtime.callbackFactory(RubyClass.class);
-		result.defineSingletonMethod("new", classCB.getOptMethod("newInstance"));
+        CallbackFactory classCB = runtime.callbackFactory(RubyClass.class);
+        result.defineSingletonMethod("new", classCB.getOptMethod("newInstance"));
         
         return result;
     }
 
     /**
      * Converts this Range to a pair of integers representing a start position 
      * and length.  If either of the range's endpoints is negative, it is added to 
      * the <code>limit</code> parameter in an attempt to arrive at a position 
      * <i>p</i> such that <i>0&nbsp;&lt;=&nbsp;p&nbsp;&lt;=&nbsp;limit</i>. If 
      * <code>truncate</code> is true, the result will be adjusted, if possible, so 
      * that <i>begin&nbsp;+&nbsp;length&nbsp;&lt;=&nbsp;limit</i>.  If <code>strict</code> 
      * is true, an exception will be raised if the range can't be converted as 
      * described above; otherwise it just returns <b>null</b>. 
      * 
      * @param limit    the size of the object (e.g., a String or Array) that 
      *                 this range is being evaluated against.
      * @param truncate if true, result must fit within the range <i>(0..limit)</i>.
      * @param isStrict   if true, raises an exception if the range can't be converted.
      * @return         a two-element array representing a start value and a length, 
      *                 or <b>null</b> if the conversion failed.
      */
     public long[] getBeginLength(long limit, boolean truncate, boolean isStrict) {
         long beginLong = RubyNumeric.num2long(begin);
         long endLong = RubyNumeric.num2long(end);
         
         // Apparent legend for MRI 'err' param to JRuby 'truncate' and 'isStrict':
         // 0 =>  truncate && !strict
         // 1 => !truncate &&  strict
         // 2 =>  truncate &&  strict
 
         if (! isExclusive) {
             endLong++;
         }
 
         if (beginLong < 0) {
             beginLong += limit;
             if (beginLong < 0) {
                 if (isStrict) {
                     throw getRuntime().newRangeError(inspect().toString() + " out of range.");
                 }
                 return null;
             }
         }
 
         if (truncate && beginLong > limit) {
             if (isStrict) {
                 throw getRuntime().newRangeError(inspect().toString() + " out of range.");
             }
             return null;
         }
 
         if (truncate && endLong > limit) {
             endLong = limit;
         }
 
 		if (endLong < 0  || (!isExclusive && endLong == 0)) {
 			endLong += limit;
 			if (endLong < 0) {
 				if (isStrict) {
 					throw getRuntime().newRangeError(inspect().toString() + " out of range.");
 				}
 				return null;
 			}
 		}
 
         return new long[] { beginLong, Math.max(endLong - beginLong, 0L) };
     }
 
     public static RubyRange newRange(IRuby runtime, IRubyObject begin, IRubyObject end, boolean isExclusive) {
         RubyRange range = new RubyRange(runtime, runtime.getClass("Range"));
         range.init(begin, end, isExclusive ? runtime.getTrue() : runtime.getFalse());
         return range;
     }
 
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         if (args.length == 3) {
             init(args[0], args[1], (RubyBoolean) args[2]);
         } else if (args.length == 2) {
             init(args[0], args[1], getRuntime().getFalse());
         } else {
             throw getRuntime().newArgumentError("Wrong arguments. (anObject, anObject, aBoolean = false) expected");
         }
         return getRuntime().getNil();
     }
 
     public IRubyObject first() {
         return begin;
     }
 
     public IRubyObject last() {
         return end;
     }
     
     public RubyFixnum hash() {
         ThreadContext context = getRuntime().getCurrentContext();
         long baseHash = (isExclusive ? 1 : 0);
         long beginHash = ((RubyFixnum) begin.callMethod(context, "hash")).getLongValue();
         long endHash = ((RubyFixnum) end.callMethod(context, "hash")).getLongValue();
         
         long hash = baseHash;
         hash = hash ^ (beginHash << 1);
         hash = hash ^ (endHash << 9);
         hash = hash ^ (baseHash << 24);
         
         return getRuntime().newFixnum(hash);
     }
 
     private IRubyObject asString(String stringMethod) {
         ThreadContext context = getRuntime().getCurrentContext();
         RubyString begStr = (RubyString) begin.callMethod(context, stringMethod);
         RubyString endStr = (RubyString) end.callMethod(context, stringMethod);
 
         return begStr.cat(isExclusive ? "..." : "..").concat(endStr);
     }
     
     public IRubyObject inspect(Block block) {
         return asString("inspect");
     }
     
     public IRubyObject to_s(Block block) {
         return asString("to_s");
     }
 
     public RubyBoolean exclude_end_p() {
         return getRuntime().newBoolean(isExclusive);
     }
 
     public RubyFixnum length(Block block) {
         long size = 0;
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (begin.callMethod(context, ">", end).isTrue()) {
             return getRuntime().newFixnum(0);
         }
 
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             size = ((RubyNumeric) end).getLongValue() - ((RubyNumeric) begin).getLongValue();
             if (!isExclusive) {
                 size++;
             }
         } else { // Support length for arbitrary classes
             IRubyObject currentObject = begin;
 	    String compareMethod = isExclusive ? "<" : "<=";
 
 	    while (currentObject.callMethod(context, compareMethod, end).isTrue()) {
 		size++;
 		if (currentObject.equals(end)) {
 		    break;
 		}
 		currentObject = currentObject.callMethod(context, "succ");
 	    }
 	}
         return getRuntime().newFixnum(size);
     }
 
     public IRubyObject equal(IRubyObject obj, Block block) {
         if (!(obj instanceof RubyRange)) {
             return getRuntime().getFalse();
         }
         RubyRange otherRange = (RubyRange) obj;
         boolean result =
             begin.equals(otherRange.begin) &&
             end.equals(otherRange.end) &&
             isExclusive == otherRange.isExclusive;
         return getRuntime().newBoolean(result);
     }
 
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         if (begin instanceof RubyFixnum && end instanceof RubyFixnum) {
             long endLong = ((RubyNumeric) end).getLongValue();
             long i = ((RubyNumeric) begin).getLongValue();
 
             if (!isExclusive) {
                 endLong += 1;
             }
 
             for (; i < endLong; i++) {
                 context.yield(getRuntime().newFixnum(i), block);
             }
         } else if (begin instanceof RubyString) {
             ((RubyString) begin).upto(end, isExclusive, block);
         } else if (begin.isKindOf(getRuntime().getClass("Numeric"))) {
             if (!isExclusive) {
                 end = end.callMethod(context, "+", RubyFixnum.one(getRuntime()));
             }
             while (begin.callMethod(context, "<", end).isTrue()) {
                 context.yield(begin, block);
                 begin = begin.callMethod(context, "+", RubyFixnum.one(getRuntime()));
             }
         } else {
             IRubyObject v = begin;
 
             if (isExclusive) {
                 while (v.callMethod(context, "<", end).isTrue()) {
                     if (v.equals(end)) {
                         break;
                     }
                     context.yield(v, block);
                     v = v.callMethod(context, "succ");
                 }
             } else {
                 while (v.callMethod(context, "<=", end).isTrue()) {
                     context.yield(v, block);
                     if (v.equals(end)) {
                         break;
                     }
                     v = v.callMethod(context, "succ");
                 }
             }
         }
 
         return this;
     }
     
     public IRubyObject step(IRubyObject[] args, Block block) {
         checkArgumentCount(args, 0, 1);
         
         IRubyObject currentObject = begin;
         String compareMethod = isExclusive ? "<" : "<=";
         int stepSize = (int) (args.length == 0 ? 1 : args[0].convertToInteger().getLongValue());
         
         if (stepSize <= 0) {
             throw getRuntime().newArgumentError("step can't be negative");
         }
 
         ThreadContext context = getRuntime().getCurrentContext();
         if (begin instanceof RubyNumeric && end instanceof RubyNumeric) {
             RubyFixnum stepNum = getRuntime().newFixnum(stepSize);
             while (currentObject.callMethod(context, compareMethod, end).isTrue()) {
                 context.yield(currentObject, block);
                 currentObject = currentObject.callMethod(context, "+", stepNum);
             }
         } else {
             while (currentObject.callMethod(context, compareMethod, end).isTrue()) {
                 context.yield(currentObject, block);
                 
                 for (int i = 0; i < stepSize; i++) {
                     currentObject = currentObject.callMethod(context, "succ");
                 }
             }
         }
         
         return this;
     }
     
     public RubyArray to_a(Block block) {
         IRubyObject currentObject = begin;
 	    String compareMethod = isExclusive ? "<" : "<=";
 	    RubyArray array = getRuntime().newArray();
         ThreadContext context = getRuntime().getCurrentContext();
         
 	    while (currentObject.callMethod(context, compareMethod, end).isTrue()) {
 	        array.append(currentObject);
 	        
 			if (currentObject.equals(end)) {
 			    break;
 			}
 			
 			currentObject = currentObject.callMethod(context, "succ");
 	    }
 	    
 	    return array;
     }
     
     // this could have been easily written in Ruby --sma
     public RubyBoolean include_p(IRubyObject obj, Block block) {
     	String compareMethod = isExclusive ? ">" : ">=";
     	IRubyObject[] arg = new IRubyObject[]{ obj };
     	IRubyObject f = obj.getRuntime().getFalse();
         ThreadContext context = getRuntime().getCurrentContext();
         
     	return f.getRuntime().newBoolean(
     			f != first().callMethod(context, "<=", arg) && f != last().callMethod(context, compareMethod, arg));
     }
 }
diff --git a/src/org/jruby/runtime/marshal/MarshalStream.java b/src/org/jruby/runtime/marshal/MarshalStream.java
index f2558febe0..f9ca7e4555 100644
--- a/src/org/jruby/runtime/marshal/MarshalStream.java
+++ b/src/org/jruby/runtime/marshal/MarshalStream.java
@@ -1,221 +1,238 @@
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
 package org.jruby.runtime.marshal;
 
 import java.io.FilterOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.IRuby;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyString;
 import org.jruby.RubySymbol;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Marshals objects into Ruby's binary marshal format.
  *
  * @author Anders
  */
 public class MarshalStream extends FilterOutputStream {
     private final IRuby runtime;
     private final int depthLimit;
     private int depth = 0;
     private MarshalCache cache;
 
     private final static char TYPE_IVAR = 'I';
     private final static char TYPE_USRMARSHAL = 'U';
     private final static char TYPE_USERDEF = 'u';
     
     protected static char TYPE_UCLASS = 'C';
 
     public MarshalStream(IRuby runtime, OutputStream out, int depthLimit) throws IOException {
         super(out);
 
         this.runtime = runtime;
         this.depthLimit = depthLimit >= 0 ? depthLimit : Integer.MAX_VALUE;
         this.cache = new MarshalCache();
 
         out.write(Constants.MARSHAL_MAJOR);
         out.write(Constants.MARSHAL_MINOR);
     }
 
     public void dumpObject(IRubyObject value) throws IOException {
         depth++;
         if (depth > depthLimit) {
             throw runtime.newArgumentError("exceed depth limit");
         }
         if (! shouldBeRegistered(value)) {
             writeDirectly(value);
         } else if (hasNewUserDefinedMarshaling(value)) {
             userNewMarshal(value);
         } else if (hasUserDefinedMarshaling(value)) {
             userMarshal(value);
         } else {
             writeAndRegister(value);
         }
         depth--;
         if (depth == 0) {
         	out.flush(); // flush afer whole dump is complete
         }
     }
     
     public void writeUserClass(IRubyObject obj, RubyClass baseClass, MarshalStream output) throws IOException {
     	// TODO: handle w_extended code here
     	
     	if (obj.getMetaClass().equals(baseClass)) {
     		// do nothing, simple builtin type marshalling
     		return;
     	}
     	
     	output.write(TYPE_UCLASS);
     	
     	// w_unique
     	if (obj.getMetaClass().getName().charAt(0) == '#') {
     		throw obj.getRuntime().newTypeError("Can't dump anonymous class");
     	}
     	
     	// w_symbol
     	// TODO: handle symlink?
     	output.dumpObject(RubySymbol.newSymbol(obj.getRuntime(), obj.getMetaClass().getName()));
     }
     
     public void writeInstanceVars(IRubyObject obj, MarshalStream output) throws IOException {
     	IRuby runtime = obj.getRuntime();
         Map iVars = obj.getInstanceVariablesSnapshot();
         output.dumpInt(iVars.size());
         for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
             String name = (String) iter.next();
             IRubyObject value = (IRubyObject)iVars.get(name);
             
             output.dumpObject(runtime.newSymbol(name));
             output.dumpObject(value);
         }
     }
 
     private void writeDirectly(IRubyObject value) throws IOException {
         if (value.isNil()) {
             out.write('0');
         } else {
             value.marshalTo(this);
         }
     }
 
     private boolean shouldBeRegistered(IRubyObject value) {
         if (value.isNil()) {
             return false;
         } else if (value instanceof RubyBoolean) {
             return false;
         } else if (value instanceof RubyFixnum) {
             return false;
         } else if (value instanceof RubyFloat) {
             return false;
         }
         return true;
     }
 
     private void writeAndRegister(IRubyObject value) throws IOException {
         if (cache.isRegistered(value)) {
             cache.writeLink(this, value);
         } else {
             cache.register(value);
             value.marshalTo(this);
         }
     }
 
     private boolean hasUserDefinedMarshaling(IRubyObject value) {
         return value.respondsTo("_dump");
     }
 
     private boolean hasNewUserDefinedMarshaling(IRubyObject value) {
         return value.respondsTo("marshal_dump");
     }
 
     private void userMarshal(IRubyObject value) throws IOException {
         out.write(TYPE_USERDEF);
         dumpObject(RubySymbol.newSymbol(runtime, value.getMetaClass().getName()));
 
         RubyString marshaled = (RubyString) value.callMethod(runtime.getCurrentContext(), "_dump", runtime.newFixnum(depthLimit)); 
         dumpString(marshaled.toString());
     }
 
     private void userNewMarshal(final IRubyObject value) throws IOException {
         out.write(TYPE_USRMARSHAL);
         dumpObject(RubySymbol.newSymbol(runtime, value.getMetaClass().getName()));
 
         IRubyObject marshaled =  value.callMethod(runtime.getCurrentContext(), "marshal_dump"); 
         dumpObject(marshaled);
     }
+    
+    public void dumpInstanceVars(Map instanceVars) throws IOException {
+        dumpInt(instanceVars.size());
+        for (Iterator iter = instanceVars.keySet().iterator(); iter.hasNext();) {
+            String name = (String) iter.next();
+            IRubyObject value = (IRubyObject)instanceVars.get(name);
+
+            dumpObject(RubySymbol.newSymbol(runtime, name));
+            dumpObject(value);
+        }
+    }
+    
+    public void dumpDefaultObjectHeader(RubyClass type) throws IOException {
+        write('o');
+        RubySymbol classname = RubySymbol.newSymbol(runtime, type.getName());
+        dumpObject(classname);
+    }
 
     public void dumpString(String value) throws IOException {
         dumpInt(value.length());
         out.write(RubyString.stringToBytes(value));
     }
 
     public void dumpInt(int value) throws IOException {
         if (value == 0) {
             out.write(0);
         } else if (0 < value && value < 123) {
             out.write(value + 5);
         } else if (-124 < value && value < 0) {
             out.write((value - 5) & 0xff);
         } else {
             int[] buf = new int[4];
             int i;
             for (i = 0; i < buf.length; i++) {
                 buf[i] = value & 0xff;
                 value = value >> 8;
                 if (value == 0 || value == -1) {
                     break;
                 }
             }
             int len = i + 1;
             out.write(value < 0 ? -len : len);
             for (i = 0; i < len; i++) {
                 out.write(buf[i]);
             }
         }
     }
 
 	public void writeIVar(IRubyObject obj, MarshalStream output) throws IOException {
 		if (obj.getInstanceVariables().size() > 0) {
 			out.write(TYPE_IVAR);
 		} 
 	}
 }
diff --git a/src/org/jruby/runtime/marshal/UnmarshalStream.java b/src/org/jruby/runtime/marshal/UnmarshalStream.java
index 3e4d5edf10..ae7e1cd184 100644
--- a/src/org/jruby/runtime/marshal/UnmarshalStream.java
+++ b/src/org/jruby/runtime/marshal/UnmarshalStream.java
@@ -1,292 +1,287 @@
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
 package org.jruby.runtime.marshal;
 
 import java.io.FilterInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 
 import org.jruby.IRuby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBignum;
 import org.jruby.RubyClass;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyHash;
 import org.jruby.RubyModule;
 import org.jruby.RubyObject;
 import org.jruby.RubyRegexp;
 import org.jruby.RubyString;
 import org.jruby.RubyStruct;
 import org.jruby.RubySymbol;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Unmarshals objects from strings or streams in Ruby's marsal format.
  *
  * @author Anders
  */
 public class UnmarshalStream extends FilterInputStream {
     protected final IRuby runtime;
     private UnmarshalCache cache;
+    private IRubyObject proc;
 
-    public UnmarshalStream(IRuby runtime, InputStream in) throws IOException {
+    public UnmarshalStream(IRuby runtime, InputStream in, IRubyObject proc) throws IOException {
         super(in);
         this.runtime = runtime;
         this.cache = new UnmarshalCache(runtime);
+        this.proc = proc;
 
         in.read(); // Major
         in.read(); // Minor
     }
 
     public IRubyObject unmarshalObject() throws IOException {
-        return unmarshalObject(null);
-    }
-
-    public IRubyObject unmarshalObject(IRubyObject proc) throws IOException {
         int type = readUnsignedByte();
         IRubyObject result;
         if (cache.isLinkType(type)) {
             result = cache.readLink(this, type);
         } else {
-        	result = unmarshalObjectDirectly(type, proc);
+        	result = unmarshalObjectDirectly(type);
         }
         return result;
     }
 
     public void registerLinkTarget(IRubyObject newObject) {
         cache.register(newObject);
     }
 
-    private IRubyObject unmarshalObjectDirectly(int type, IRubyObject proc) throws IOException {
+    private IRubyObject unmarshalObjectDirectly(int type) throws IOException {
     	IRubyObject rubyObj = null;
         switch (type) {
         	case 'I':
-                rubyObj = unmarshalObject(proc);
-                defaultInstanceVarsUnmarshal(rubyObj, proc);
+                rubyObj = unmarshalObject();
+                defaultInstanceVarsUnmarshal(rubyObj);
         		break;
             case '0' :
                 rubyObj = runtime.getNil();
                 break;
             case 'T' :
                 rubyObj = runtime.getTrue();
                 break;
             case 'F' :
                 rubyObj = runtime.getFalse();
                 break;
             case '"' :
                 rubyObj = RubyString.unmarshalFrom(this);
                 break;
             case 'i' :
                 rubyObj = RubyFixnum.unmarshalFrom(this);
                 break;
             case 'f' :
             	rubyObj = RubyFloat.unmarshalFrom(this);
             	break;
             case '/' :
                 rubyObj = RubyRegexp.unmarshalFrom(this);
                 break;
             case ':' :
                 rubyObj = RubySymbol.unmarshalFrom(this);
                 break;
             case '[' :
                 rubyObj = RubyArray.unmarshalFrom(this);
                 break;
             case '{' :
                 rubyObj = RubyHash.unmarshalFrom(this, false);
                 break;
             case '}' :
                 // "hashdef" object, a hash with a default
                 rubyObj = RubyHash.unmarshalFrom(this, true);
                 break;
             case 'c' :
                 rubyObj = RubyClass.unmarshalFrom(this);
                 break;
             case 'm' :
                 rubyObj = RubyModule.unmarshalFrom(this);
                 break;
             case 'l' :
                 rubyObj = RubyBignum.unmarshalFrom(this);
                 break;
             case 'S' :
                 rubyObj = RubyStruct.unmarshalFrom(this);
                 break;
             case 'o' :
-                rubyObj = defaultObjectUnmarshal(proc);
+                rubyObj = defaultObjectUnmarshal();
                 break;
             case 'u' :
                 rubyObj = userUnmarshal();
                 break;
             case 'U' :
                 rubyObj = userNewUnmarshal();
                 break;
             case 'C' :
             	rubyObj = uclassUnmarshall();
             	break;
             default :
                 throw getRuntime().newArgumentError("dump format error(" + (char)type + ")");
         }
         
         if (proc != null) {
 			proc.callMethod(getRuntime().getCurrentContext(), "call", new IRubyObject[] {rubyObj});
 		}
         return rubyObj;
     }
 
 
     public IRuby getRuntime() {
         return runtime;
     }
 
     public int readUnsignedByte() throws IOException {
         int result = read();
         if (result == -1) {
             throw new IOException("Unexpected end of stream");
         }
         return result;
     }
 
     public byte readSignedByte() throws IOException {
         int b = readUnsignedByte();
         if (b > 127) {
             return (byte) (b - 256);
         }
 		return (byte) b;
     }
 
     public String unmarshalString() throws IOException {
         int length = unmarshalInt();
         byte[] buffer = new byte[length];
         int bytesRead = read(buffer);
         if (bytesRead != length) {
             throw new IOException("Unexpected end of stream");
         }
         return RubyString.bytesToString(buffer);
     }
 
     public int unmarshalInt() throws IOException {
         int c = readSignedByte();
         if (c == 0) {
             return 0;
         } else if (4 < c && c < 128) {
             return c - 5;
         } else if (-129 < c && c < -4) {
             return c + 5;
         }
         long result;
         if (c > 0) {
             result = 0;
             for (int i = 0; i < c; i++) {
                 result |= (long) readUnsignedByte() << (8 * i);
             }
         } else {
             c = -c;
             result = -1;
             for (int i = 0; i < c; i++) {
                 result &= ~((long) 0xff << (8 * i));
                 result |= (long) readUnsignedByte() << (8 * i);
             }
         }
         return (int) result;
     }
 
-    private IRubyObject defaultObjectUnmarshal(IRubyObject proc) throws IOException {
+    private IRubyObject defaultObjectUnmarshal() throws IOException {
         RubySymbol className = (RubySymbol) unmarshalObject();
 
         // ... FIXME: handle if class doesn't exist ...
 
         RubyClass type = (RubyClass) runtime.getClassFromPath(className.asSymbol());
 
         assert type != null : "type shouldn't be null.";
-
-        IRubyObject result = new RubyObject(runtime, type);
-        registerLinkTarget(result);
-
-        defaultInstanceVarsUnmarshal(result, proc);
+        
+        IRubyObject result = (IRubyObject)type.unmarshal(this);
 
         return result;
     }
     
-    private void defaultInstanceVarsUnmarshal(IRubyObject object, IRubyObject proc) throws IOException {
+    public void defaultInstanceVarsUnmarshal(IRubyObject object) throws IOException {
     	int count = unmarshalInt();
     	
     	for (int i = 0; i < count; i++) {
-    		object.setInstanceVariable(unmarshalObject().asSymbol(), unmarshalObject(proc));
+    		object.setInstanceVariable(unmarshalObject().asSymbol(), unmarshalObject());
     	}
     }
     
     private IRubyObject uclassUnmarshall() throws IOException {
     	RubySymbol className = (RubySymbol)unmarshalObject();
     	
     	RubyClass type = (RubyClass)runtime.getClassFromPath(className.asSymbol());
     	
     	IRubyObject result = unmarshalObject();
     	
     	result.setMetaClass(type);
     	
     	return result;
     }
 
     private IRubyObject userUnmarshal() throws IOException {
         String className = unmarshalObject().asSymbol();
         String marshaled = unmarshalString();
         RubyModule classInstance;
         try {
             classInstance = runtime.getClassFromPath(className);
         } catch (RaiseException e) {
             if (e.getException().isKindOf(runtime.getModule("NameError"))) {
                 throw runtime.newArgumentError("undefined class/module " + className);
             } 
                 
             throw e;
         }
         if (!classInstance.respondsTo("_load")) {
             throw runtime.newTypeError("class " + classInstance.getName() + " needs to have method `_load'");
         }
         IRubyObject result = classInstance.callMethod(getRuntime().getCurrentContext(),
             "_load", runtime.newString(marshaled));
         registerLinkTarget(result);
         return result;
     }
 
     private IRubyObject userNewUnmarshal() throws IOException {
         String className = unmarshalObject().asSymbol();
         IRubyObject marshaled = unmarshalObject();
         RubyClass classInstance = runtime.getClass(className);
         IRubyObject result = classInstance.newInstance(new IRubyObject[0], null);
         result.callMethod(getRuntime().getCurrentContext(),"marshal_load", marshaled);
         registerLinkTarget(result);
         return result;
     }
 }
diff --git a/test/testMarshal.rb b/test/testMarshal.rb
index 6c4426ae79..548a080696 100644
--- a/test/testMarshal.rb
+++ b/test/testMarshal.rb
@@ -1,208 +1,241 @@
 require 'test/minirunit'
 test_check "Test Marshal:"
 
 MARSHAL_HEADER = Marshal.dump(nil).chop
 
 def test_marshal(expected, marshalee)
   test_equal(MARSHAL_HEADER + expected, Marshal.dump(marshalee))
 end
 
 test_marshal("0", nil)
 test_marshal("T", true)
 test_marshal("F", false)
 test_marshal("i\000", 0)
 test_marshal("i\006", 1)
 test_marshal("i\372", -1)
 test_marshal("i\002\320\a", 2000)
 test_marshal("i\3760\370", -2000)
 test_marshal("i\004\000\312\232;", 1000000000)
 test_marshal(":\017somesymbol", :somesymbol)
 test_marshal("f\n2.002", 2.002)
 test_marshal("f\013-2.002", -2.002)
 test_marshal("\"\nhello", "hello")
 test_marshal("[\010i\006i\ai\010", [1,2,3])
 test_marshal("{\006i\006i\a", {1=>2})
 test_marshal("c\013Object", Object)
 module Foo
   class Bar
   end
 end
 test_marshal("c\rFoo::Bar", Foo::Bar)
 test_marshal("m\017Enumerable", Enumerable)
 test_marshal("/\013regexp\000", /regexp/)
 test_marshal("l+\n\000\000\000\000\000\000\000\000@\000", 2 ** 70)
 #test_marshal("l+\f\313\220\263z\e\330p\260\200-\326\311\264\000",
 #             14323534664547457526224437612747)
 test_marshal("l+\n\001\000\001@\000\000\000\000@\000",
              1 + (2 ** 16) + (2 ** 30) + (2 ** 70))
 test_marshal("l+\n6\361\3100_/\205\177Iq",
              534983213684351312654646)
 test_marshal("l-\n6\361\3100_/\205\177Iq",
              -534983213684351312654646)
 test_marshal("l+\n\331\347\365%\200\342a\220\336\220",
              684126354563246654351321)
 #test_marshal("l+\vIZ\210*,u\006\025\304\016\207\001",
 #            472759725676945786624563785)
 
 test_marshal("c\023Struct::Froboz",
              Struct.new("Froboz", :x, :y))
 test_marshal("S:\023Struct::Froboz\a:\006xi\n:\006yi\f",
              Struct::Froboz.new(5, 7))
 
 # Can't dump anonymous class
 #test_exception(ArgumentError) { Marshal.dump(Struct.new(:x, :y).new(5, 7)) }
 
 
 # FIXME: Bignum marshaling is broken.
 
 # FIXME: IVAR, MODULE_OLD, 'U', ...
 
 test_marshal("o:\013Object\000", Object.new)
 class MarshalTestClass
   def initialize
     @foo = "bar"
   end
 end
 test_marshal("o:\025MarshalTestClass\006:\t@foo\"\010bar",
              MarshalTestClass.new)
 o = Object.new
 test_marshal("[\to:\013Object\000@\006@\006@\006",
              [o, o, o, o])
 class MarshalTestClass
   def initialize
     @foo = self
   end
 end
 test_marshal("o:\025MarshalTestClass\006:\t@foo@\000",
              MarshalTestClass.new)
 
 class UserMarshaled
   attr :foo
   def initialize(foo)
     @foo = foo
   end
   class << self
     def _load(str)
       return self.new(str.reverse.to_i)
     end
   end
   def _dump(depth)
     @foo.to_s.reverse
   end
   def ==(other)
     self.class == other.class && self.foo == other.foo
   end
 end
 um = UserMarshaled.new(123)
 test_marshal("u:\022UserMarshaled\010321", um)
 test_equal(um, Marshal.load(Marshal.dump(um)))
 
 test_marshal("[\a00", [nil, nil])
 test_marshal("[\aTT", [true, true])
 test_marshal("[\ai\006i\006", [1, 1])
 test_marshal("[\a:\ahi;\000", [:hi, :hi])
 o = Object.new
 test_marshal("[\ao:\013Object\000@\006", [o, o])
 
 test_exception(ArgumentError) {
   Marshal.load("\004\bu:\026SomeUnknownClassX\nhello")
 }
 
 module UM
   class UserMarshal
     def _dump(depth)
       "hello"
     end
   end
 end
 begin
   Marshal.load("\004\bu:\024UM::UserMarshal\nhello")
   test_fail
 rescue TypeError => e
   test_equal("class UM::UserMarshal needs to have method `_load'", e.message)
 end
 
 # Unmarshaling
 
 object = Marshal.load(MARSHAL_HEADER + "o:\025MarshalTestClass\006:\t@foo\"\010bar")
 test_equal(["@foo"], object.instance_variables)
 
 test_equal(true, Marshal.load(MARSHAL_HEADER + "T"))
 test_equal(false, Marshal.load(MARSHAL_HEADER + "F"))
 test_equal(nil, Marshal.load(MARSHAL_HEADER + "0"))
 test_equal("hello", Marshal.load(MARSHAL_HEADER + "\"\nhello"))
 test_equal(1, Marshal.load(MARSHAL_HEADER + "i\006"))
 test_equal(-1, Marshal.load(MARSHAL_HEADER + "i\372"))
 test_equal(-2, Marshal.load(MARSHAL_HEADER + "i\371"))
 test_equal(2000, Marshal.load(MARSHAL_HEADER + "i\002\320\a"))
 test_equal(-2000, Marshal.load(MARSHAL_HEADER + "i\3760\370"))
 test_equal(1000000000, Marshal.load(MARSHAL_HEADER + "i\004\000\312\232;"))
 test_equal([1, 2, 3], Marshal.load(MARSHAL_HEADER + "[\010i\006i\ai\010"))
 test_equal({1=>2}, Marshal.load(MARSHAL_HEADER + "{\006i\006i\a"))
 test_equal(String, Marshal.load(MARSHAL_HEADER + "c\013String"))
 #test_equal(Enumerable, Marshal.load(MARSHAL_HEADER + "m\017Enumerable"))
 test_equal(Foo::Bar, Marshal.load(MARSHAL_HEADER + "c\rFoo::Bar"))
 
 s = Marshal.load(MARSHAL_HEADER + "S:\023Struct::Froboz\a:\006xi\n:\006yi\f")
 test_equal(Struct::Froboz, s.class)
 test_equal(5, s.x)
 test_equal(7, s.y)
 
 test_equal(2 ** 70, Marshal.load(MARSHAL_HEADER + "l+\n\000\000\000\000\000\000\000\000@\000"))
 
 object = Marshal.load(MARSHAL_HEADER + "o:\013Object\000")
 test_equal(Object, object.class)
 
 Marshal.dump([1,2,3], 2)
 test_exception(ArgumentError) { Marshal.dump([1,2,3], 1) }
 
 o = Object.new
 a = Marshal.load(Marshal.dump([o, o, o, o]))
 test_ok(a[0] == a[1])
 a = Marshal.load(Marshal.dump([:hi, :hi, :hi, :hi]))
 test_ok(a[0] == :hi)
 test_ok(a[1] == :hi)
 
 # simple extensions of builtins should retain their types
 class MyHash < Hash
   attr_accessor :used, :me
   
   def initialize
   	super
     @used = {}
     @me = 'a'
   end
   
   def []=(k, v) #:nodoc:
     @used[k] = false
     super
   end
   
   def foo; end
 end
 
 x = MyHash.new
 
 test_equal(MyHash, Marshal.load(Marshal.dump(x)).class)
 test_equal(x, Marshal.load(Marshal.dump(x)))
 
 x['a'] = 'b'
 #test_equal(x, Marshal.load(Marshal.dump(x)))
 
 class F < Hash
   def initialize #:nodoc:
     super
     @val = { :notice=>true }
     @val2 = { :notice=>false }
   end
 end
 
 test_equal(F.new,Marshal.load(Marshal.dump(F.new)))
 
 test_equal(4, Marshal::MAJOR_VERSION)
 test_equal(8, Marshal::MINOR_VERSION)
 
 # Hashes with defaults serialize a bit differently; confirm the default is coming back correctly
 x = {}
 x.default = "foo"
-test_equal("foo", Marshal.load(Marshal.dump(x)).default)
\ No newline at end of file
+test_equal("foo", Marshal.load(Marshal.dump(x)).default)
+
+# Range tests
+x = 1..10
+y = Marshal.load(Marshal.dump(x))
+test_equal(x, y)
+test_equal(x.class, y.class)
+test_no_exception {
+  test_equal(10, y.max)
+  test_equal(1, y.min)
+  test_equal(false, y.exclude_end?)
+  y.each {}
+}
+z = Marshal.dump(x)
+test_ok(z.include?("excl"))
+test_ok(z.include?("begin"))
+test_ok(z.include?("end"))
+
+class MyRange < Range
+  attr_accessor :foo
+
+  def initialize(a,b)
+    super
+    @foo = "hello"
+  end
+end
+
+x = MyRange.new(1,10)
+y = Marshal.load(Marshal.dump(x))
+test_equal(MyRange, y.class)
+test_no_exception {
+  test_equal(10, y.max)
+  test_equal("hello", y.foo)
+}
