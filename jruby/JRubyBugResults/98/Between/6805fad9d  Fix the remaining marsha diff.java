diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index 340eba22f4..02407a501d 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,406 +1,410 @@
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
 
 import java.io.IOException;
 import java.util.Map;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
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
 	
     private final Ruby runtime;
     
     // the default allocator
     private final ObjectAllocator allocator;
     
     private ObjectMarshal marshal;
     
     private static final ObjectMarshal DEFAULT_OBJECT_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             IRubyObject object = (IRubyObject)obj;
             
             Map iVars = object.getInstanceVariablesSnapshot();
             
             marshalStream.dumpInstanceVars(iVars);
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             IRubyObject result = type.allocate();
             
             unmarshalStream.registerLinkTarget(result);
 
             unmarshalStream.defaultInstanceVarsUnmarshal(result);
 
             return result;
         }
     };
 
     /**
      * @mri rb_boot_class
      */
     
     /**
      * @mri rb_boot_class
      */
     
     /**
      * @mri rb_boot_class
      */
     
     /**
      * @mri rb_boot_class
      */
     protected RubyClass(RubyClass superClass, ObjectAllocator allocator) {
         this(superClass.getRuntime(), superClass.getRuntime().getClass("Class"), superClass, allocator, null, null);
 
         infectBy(superClass);
     }
 
     protected RubyClass(Ruby runtime, RubyClass superClass, ObjectAllocator allocator) {
         this(runtime, null, superClass, allocator, null, null);
     }
 
     protected RubyClass(Ruby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator) {
         this(runtime, metaClass, superClass, allocator, null, null);
     }
     
     protected RubyClass(Ruby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass, superClass, parentCRef, name);
         this.allocator = allocator;
         this.runtime = runtime;
         
         // use parent's marshal, or default object marshal by default
         if (superClass != null) {
             this.marshal = superClass.getMarshal();
         } else {
             this.marshal = DEFAULT_OBJECT_MARSHAL;
         }
     }
     
     /**
      * Create an initial Object meta class before Module and Kernel dependencies have
      * squirreled themselves together.
      * 
      * @param runtime we need it
      * @return a half-baked meta class for object
      */
     public static RubyClass createBootstrapMetaClass(Ruby runtime, String className, 
             RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList cref) {
         RubyClass objectClass = new RubyClass(runtime, null, superClass, allocator, cref, className);
         
         return objectClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.CLASS;
     }
 
     public static final byte EQQ_SWITCHVALUE = 1;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         // If tracing is on, don't do STI dispatch
         if (context.getRuntime().getTraceFunction() != null) return super.callMethod(context, rubyclass, name, args, callType, block);
         
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case EQQ_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_eqq(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
     
     public final IRubyObject allocate() {
         return getAllocator().allocate(getRuntime(), this);
     }
     
     public final ObjectMarshal getMarshal() {
         return marshal;
     }
     
     public final void setMarshal(ObjectMarshal marshal) {
         this.marshal = marshal;
     }
     
     public final void marshal(Object obj, MarshalStream marshalStream) throws IOException {
         getMarshal().marshalTo(getRuntime(), obj, this, marshalStream);
     }
     
     public final Object unmarshal(UnmarshalStream unmarshalStream) throws IOException {
         return getMarshal().unmarshalFrom(getRuntime(), this, unmarshalStream);
     }
     
     public static RubyClass newClassClass(Ruby runtime, RubyClass moduleClass) {
         ObjectAllocator defaultAllocator = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 IRubyObject instance = new RubyObject(runtime, klass);
                 instance.setMetaClass(klass);
 
                 return instance;
             }
         };
         
         RubyClass classClass = new RubyClass(
                 runtime,
                 null /* FIXME: should be something else? */,
                 moduleClass,
                 defaultAllocator,
                 null,
                 "Class");
         
         classClass.index = ClassIndex.CLASS;
         
         return classClass;
     }
     
     /* (non-Javadoc)
 	 * @see org.jruby.RubyObject#getRuntime()
 	 */
 	public Ruby getRuntime() {
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
         classClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newClass"));
         classClass.defineFastMethod("allocate", callbackFactory.getFastMethod("allocate"));
         classClass.defineMethod("new", callbackFactory.getOptMethod("newInstance"));
         classClass.defineMethod("superclass", callbackFactory.getMethod("superclass"));
         classClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy", RubyKernel.IRUBY_OBJECT));
         classClass.defineMethod("inherited", callbackFactory.getSingletonMethod("inherited", RubyKernel.IRUBY_OBJECT));
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
 
     public boolean isSingleton() {
         return false;
     }
 
 //    public RubyClass getMetaClass() {
 //        RubyClass type = super.getMetaClass();
 //
 //        return type != null ? type : getRuntime().getClass("Class");
 //    }
 
     public RubyClass getRealClass() {
         return this;
     }
 
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
         return new RubyClass(runtime, runtime.getClass("Class"), superClass, superClass.getAllocator(), parentCRef, name);
     }
 
     public static RubyClass cloneClass(Ruby runtime, RubyClass metaClass, RubyClass superClass, ObjectAllocator allocator, SinglyLinkedList parentCRef, String name) {
         return new RubyClass(runtime, metaClass, superClass, allocator, parentCRef, name);
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
         obj.callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
         return obj;
     }
     
     public ObjectAllocator getAllocator() {
         return allocator;
     }
 
     /** rb_class_s_new
      *
      */
     public static RubyClass newClass(IRubyObject recv, IRubyObject[] args, Block block, boolean invokeInherited) {
         final Ruby runtime = recv.getRuntime();
 
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
         RubyClass newClass = superClass.newSubClass(null, superClass.getAllocator(),tc.peekCRef(),invokeInherited);
 
         // call "initialize" method
         newClass.callInit(args, block);
 
         // FIXME: inheritedBy called in superClass.newSubClass, so I
         // assume this second call is a bug...?
         // call "inherited" method of the superclass
         //newClass.inheritedBy(superClass);
 
 		if (block.isGiven()) block.yield(tc, null, newClass, newClass, false);
 
 		return newClass;
     }
     public static RubyClass newClass(IRubyObject recv, IRubyObject[] args, Block block) {
         return newClass(recv,args,block,true);
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
 
     public static void marshalTo(RubyClass clazz, MarshalStream output) throws java.io.IOException {
-        output.writeString(clazz.getName());
+        String name = clazz.getName();
+        if(name.length() > 0 && name.charAt(0) == '#') {
+            throw clazz.getRuntime().newTypeError("can't dump anonymous " + (clazz instanceof RubyClass ? "class" : "module") + " " + name);
+        }
+        output.writeString(name);
     }
 
     public static RubyModule unmarshalFrom(UnmarshalStream output) throws java.io.IOException {
         return (RubyClass) RubyModule.unmarshalFrom(output);
     }
 
     public RubyClass newSubClass(String name, ObjectAllocator allocator,
             SinglyLinkedList parentCRef, boolean invokeInherited) {
         RubyClass classClass = runtime.getClass("Class");
         
         // Cannot subclass 'Class' or metaclasses
         if (this == classClass) {
             throw runtime.newTypeError("can't make subclass of Class");
         } else if (this instanceof MetaClass) {
             throw runtime.newTypeError("can't make subclass of virtual class");
         }
 
         RubyClass newClass = new RubyClass(runtime, classClass, this, allocator, parentCRef, name);
 
         newClass.makeMetaClass(getMetaClass(), newClass.getCRef());
         
         if (invokeInherited) {
             newClass.inheritedBy(this);
         }
 
         if(null != name) {
             ((RubyModule)parentCRef.getValue()).setConstant(name, newClass);
         }
 
         return newClass;
     }
     public RubyClass newSubClass(String name, ObjectAllocator allocator, SinglyLinkedList parentCRef) {
         return newSubClass(name,allocator,parentCRef,true);
     }
     
     
     protected IRubyObject doClone() {
     	return RubyClass.cloneClass(getRuntime(), getMetaClass(), getSuperClass(), getAllocator(), null/*FIXME*/, null);
     }
     
     /** rb_class_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original){
 
         if (((RubyClass) original).isSingleton()){
             throw getRuntime().newTypeError("can't copy singleton class");
         }
         
         super.initialize_copy(original);
         
         return this;        
     }    
 }
diff --git a/src/org/jruby/RubyGlobal.java b/src/org/jruby/RubyGlobal.java
index d898bace22..1dda7a9da6 100644
--- a/src/org/jruby/RubyGlobal.java
+++ b/src/org/jruby/RubyGlobal.java
@@ -1,375 +1,379 @@
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
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
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
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.environment.OSEnvironmentReaderExcepton;
 import org.jruby.environment.OSEnvironment;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ReadonlyGlobalVariable;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.KCode;
 
 /** This class initializes global variables and constants.
  * 
  * @author jpetersen
  */
 public class RubyGlobal {
     
     /**
      * Obligate string-keyed and string-valued hash, used for ENV and ENV_JAVA
      * 
      */
     private static class StringOnlyRubyHash extends RubyHash {
         
         public StringOnlyRubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
             super(runtime, valueMap, defaultValue);
         }
-        
+
         public IRubyObject aref(IRubyObject key) {
             if (!key.respondsTo("to_str")) {
                 throw getRuntime().newTypeError("can't convert " + key.getMetaClass() + " into String");
             }
 
             return super.aref(key.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY));
         }
 
         public IRubyObject aset(IRubyObject key, IRubyObject value) {
             if (!key.respondsTo("to_str")) {
                 throw getRuntime().newTypeError("can't convert " + key.getMetaClass() + " into String");
             }
             if (!value.respondsTo("to_str") && !value.isNil()) {
                 throw getRuntime().newTypeError("can't convert " + value.getMetaClass() + " into String");
             }
 
             ThreadContext context = getRuntime().getCurrentContext();
             //return super.aset(getRuntime().newString("sadfasdF"), getRuntime().newString("sadfasdF"));
             return super.aset(key.callMethod(context, MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY),
                     value.isNil() ? getRuntime().getNil() : value.callMethod(context, MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY));
         }
         
         public IRubyObject to_s(){
             return getRuntime().newString("ENV");
     }
     }
     
     public static void createGlobals(Ruby runtime) {
 
         // Version information:
         IRubyObject version = runtime.newString(Constants.RUBY_VERSION).freeze();
         IRubyObject release = runtime.newString(Constants.COMPILE_DATE).freeze();
         IRubyObject platform = runtime.newString(Constants.PLATFORM).freeze();
 
         runtime.defineGlobalConstant("RUBY_VERSION", version);
         runtime.defineGlobalConstant("RUBY_RELEASE_DATE", release);
         runtime.defineGlobalConstant("RUBY_PLATFORM", platform);
 
         runtime.defineGlobalConstant("VERSION", version);
         runtime.defineGlobalConstant("RELEASE_DATE", release);
         runtime.defineGlobalConstant("PLATFORM", platform);
         
         IRubyObject jrubyVersion = runtime.newString(Constants.VERSION).freeze();
         runtime.defineGlobalConstant("JRUBY_VERSION", jrubyVersion);
 		
         GlobalVariable kcodeGV = new KCodeGlobalVariable(runtime, "$KCODE", runtime.newString("NONE"));
         runtime.defineVariable(kcodeGV);
         runtime.defineVariable(new GlobalVariable.Copy(runtime, "$-K", kcodeGV));
         runtime.defineVariable(new StringGlobalVariable(runtime, "$/", runtime.newString("\n").freeze()));
         runtime.defineVariable(new StringGlobalVariable(runtime, "$\\", runtime.getNil()));
         runtime.defineVariable(new StringGlobalVariable(runtime, "$,", runtime.getNil()));
 
         runtime.defineVariable(new LineNumberGlobalVariable(runtime, "$.", RubyFixnum.one(runtime)));
         runtime.defineVariable(new LastlineGlobalVariable(runtime, "$_"));
 
         runtime.defineVariable(new ErrorInfoGlobalVariable(runtime, "$!", runtime.getNil()));
 
         runtime.defineVariable(new SafeGlobalVariable(runtime, "$SAFE"));
 
         runtime.defineVariable(new BacktraceGlobalVariable(runtime, "$@"));
 
         IRubyObject stdin = RubyIO.fdOpen(runtime, RubyIO.STDIN);
         IRubyObject stdout = RubyIO.fdOpen(runtime, RubyIO.STDOUT);
         IRubyObject stderr = RubyIO.fdOpen(runtime, RubyIO.STDERR);
 
         runtime.defineVariable(new InputGlobalVariable(runtime, "$stdin", stdin));
 
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stdout", stdout));
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stderr", stderr));
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$>", stdout));
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$defout", stdout));
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$deferr", stderr));
 
         runtime.defineGlobalConstant("STDIN", stdin);
         runtime.defineGlobalConstant("STDOUT", stdout);
         runtime.defineGlobalConstant("STDERR", stderr);
 
         runtime.defineVariable(new LoadedFeatures(runtime, "$\""));
         runtime.defineVariable(new LoadedFeatures(runtime, "$LOADED_FEATURES"));
 
         runtime.defineVariable(new LoadPath(runtime, "$:"));
         runtime.defineVariable(new LoadPath(runtime, "$-I"));
         runtime.defineVariable(new LoadPath(runtime, "$LOAD_PATH"));
 
         // after defn of $stderr as the call may produce warnings
         defineGlobalEnvConstants(runtime);
         
         // Fixme: Do we need the check or does Main.java not call this...they should consolidate 
         if (runtime.getGlobalVariables().get("$*").isNil()) {
             runtime.getGlobalVariables().defineReadonly("$*", new ValueAccessor(runtime.newArray()));
         }
 
         // ARGF, $< object
         new RubyArgsFile(runtime).initArgsFile();
     }
 
     private static void defineGlobalEnvConstants(Ruby runtime) {
 
     	Map environmentVariableMap = null;
     	OSEnvironment environment = new OSEnvironment();
     	try {
     		environmentVariableMap = environment.getEnvironmentVariableMap(runtime);
     	} catch (OSEnvironmentReaderExcepton e) {
     		// If the environment variables are not accessible shouldn't terminate 
     		runtime.getWarnings().warn(e.getMessage());
     	}
 		
     	if (environmentVariableMap == null) {
             // if the environment variables can't be obtained, define an empty ENV
     		environmentVariableMap = new HashMap();
     	}
-        runtime.defineGlobalConstant("ENV", new StringOnlyRubyHash(runtime,
-                environmentVariableMap, runtime.getNil()));
+
+        StringOnlyRubyHash h1 = new StringOnlyRubyHash(runtime,
+                                                       environmentVariableMap, runtime.getNil());
+        org.jruby.runtime.CallbackFactory cf = org.jruby.runtime.CallbackFactory.createFactory(runtime, StringOnlyRubyHash.class);
+        h1.getSingletonClass().defineFastMethod("to_s", cf.getFastMethod("to_s"));
+        runtime.defineGlobalConstant("ENV", h1);
 
         // Define System.getProperties() in ENV_JAVA
         Map systemProps = environment.getSystemPropertiesMap(runtime);
         runtime.defineGlobalConstant("ENV_JAVA", new StringOnlyRubyHash(
                 runtime, systemProps, runtime.getNil()));
         
     }
 
 
 
     // Accessor methods.
 
     private static class LineNumberGlobalVariable extends GlobalVariable {
         public LineNumberGlobalVariable(Ruby runtime, String name, RubyFixnum value) {
             super(runtime, name, value);
         }
 
         public IRubyObject set(IRubyObject value) {
             ((RubyArgsFile) runtime.getGlobalVariables().get("$<")).setCurrentLineNumber(RubyNumeric.fix2int(value));
             return super.set(value);
         }
     }
 
     private static class ErrorInfoGlobalVariable extends GlobalVariable {
         public ErrorInfoGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() && ! value.isKindOf(runtime.getClass("Exception"))) {
                 throw runtime.newTypeError("assigning non-exception to $!");
             }
             return super.set(value);
         }
     }
 
     // FIXME: move out of this class!
     public static class StringGlobalVariable extends GlobalVariable {
         public StringGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() && ! (value instanceof RubyString)) {
                 throw runtime.newTypeError("value of " + name() + " must be a String");
             }
             return super.set(value);
         }
     }
 
     public static class KCodeGlobalVariable extends GlobalVariable {
         public KCodeGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         public IRubyObject get() {
             return runtime.getKCode().kcode(runtime);
         }
 
         public IRubyObject set(IRubyObject value) {
             if (!value.isNil() && ! (value instanceof RubyString)) {
                 throw runtime.newTypeError("value of " + name() + " must be a String");
             }
             runtime.setKCode(KCode.create(runtime, value.toString()));
             return value;
         }
     }
 
     private static class SafeGlobalVariable extends GlobalVariable {
         public SafeGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         public IRubyObject get() {
             return runtime.newFixnum(runtime.getSafeLevel());
         }
 
         public IRubyObject set(IRubyObject value) {
             int level = RubyNumeric.fix2int(value);
             if (level < runtime.getSafeLevel()) {
             	throw runtime.newSecurityError("tried to downgrade safe level from " + 
             			runtime.getSafeLevel() + " to " + level);
             }
             runtime.setSafeLevel(level);
             // thread.setSafeLevel(level);
             return value;
         }
     }
 
     private static class BacktraceGlobalVariable extends GlobalVariable {
         public BacktraceGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         public IRubyObject get() {
             IRubyObject errorInfo = runtime.getGlobalVariables().get("$!");
             IRubyObject backtrace = errorInfo.isNil() ? runtime.getNil() : errorInfo.callMethod(errorInfo.getRuntime().getCurrentContext(), "backtrace");
             //$@ returns nil if $!.backtrace is not an array
             if (!(backtrace instanceof RubyArray)) {
                 backtrace = runtime.getNil();
             }
             return backtrace;
         }
 
         public IRubyObject set(IRubyObject value) {
             if (runtime.getGlobalVariables().get("$!").isNil()) {
                 throw runtime.newArgumentError("$! not set.");
             }
             runtime.getGlobalVariables().get("$!").callMethod(value.getRuntime().getCurrentContext(), "set_backtrace", value);
             return value;
         }
     }
 
     private static class LastlineGlobalVariable extends GlobalVariable {
         public LastlineGlobalVariable(Ruby runtime, String name) {
             super(runtime, name, null);
         }
 
         public IRubyObject get() {
             return runtime.getCurrentContext().getLastline();
         }
 
         public IRubyObject set(IRubyObject value) {
             runtime.getCurrentContext().setLastline(value);
             return value;
         }
     }
 
     private static class InputGlobalVariable extends GlobalVariable {
         public InputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         public IRubyObject set(IRubyObject value) {
             if (value == get()) {
                 return value;
             }
             if (value instanceof RubyIO) {
                 ((RubyIO) value).checkReadable();
             }
             return super.set(value);
         }
     }
 
     private static class OutputGlobalVariable extends GlobalVariable {
         public OutputGlobalVariable(Ruby runtime, String name, IRubyObject value) {
             super(runtime, name, value);
         }
 
         public IRubyObject set(IRubyObject value) {
             if (value == get()) {
                 return value;
             }
             if (value instanceof RubyIO) {
                 ((RubyIO) value).checkWriteable();
             }
             if (! value.respondsTo("write")) {
                 throw runtime.newTypeError(name() + " must have write method, " +
                                     value.getType().getName() + " given");
             }
             
             if ("$stdout".equals(name())) {
                 runtime.defineVariable(new OutputGlobalVariable(runtime, "$>", value));
             }
 
             return super.set(value);
         }
     }
     
     private static class LoadPath extends ReadonlyGlobalVariable {
         public LoadPath(Ruby runtime, String name) {
             super(runtime, name, null);
         }
         
         /**
          * @see org.jruby.runtime.GlobalVariable#get()
          */
         public IRubyObject get() {
             return runtime.getLoadService().getLoadPath();
         }
     }
 
     private static class LoadedFeatures extends ReadonlyGlobalVariable {
         public LoadedFeatures(Ruby runtime, String name) {
             super(runtime, name, null);
         }
         
         /**
          * @see org.jruby.runtime.GlobalVariable#get()
          */
         public IRubyObject get() {
             return runtime.getLoadService().getLoadedFeatures();
         }
     }
 }
diff --git a/src/org/jruby/RubyModule.java b/src/org/jruby/RubyModule.java
index af9585f685..9d572941cb 100644
--- a/src/org/jruby/RubyModule.java
+++ b/src/org/jruby/RubyModule.java
@@ -1,1298 +1,1307 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006-2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.internal.runtime.methods.AliasMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.FullFunctionCallbackMethod;
 import org.jruby.internal.runtime.methods.SimpleCallbackMethod;
 import org.jruby.internal.runtime.methods.MethodMethod;
 import org.jruby.internal.runtime.methods.ProcMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.internal.runtime.methods.WrapperMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.util.collections.SinglyLinkedList;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyModule extends RubyObject {
     private static final String CVAR_TAINT_ERROR =
         "Insecure: can't modify class variable";
     private static final String CVAR_FREEZE_ERROR = "class/module";
 
     // superClass may be null.
     private RubyClass superClass;
 
     public int index;
 
     public final int id;
 
     // Containing class...The parent of Object is null. Object should always be last in chain.
     //public RubyModule parentModule;
 
     // CRef...to eventually replace parentModule
     public SinglyLinkedList cref;
 
     // ClassId is the name of the class/module sans where it is located.
     // If it is null, then it an anonymous class.
     private String classId;
 
     // All methods and all CACHED methods for the module.  The cached methods will be removed
     // when appropriate (e.g. when method is removed by source class or a new method is added
     // with same name by one of its subclasses).
     private Map methods = new HashMap();
 
     protected RubyModule(Ruby runtime, RubyClass metaClass, RubyClass superClass, SinglyLinkedList parentCRef, String name) {
         super(runtime, metaClass);
 
         this.superClass = superClass;
 
         setBaseName(name);
 
         // If no parent is passed in, it is safe to assume Object.
         if (parentCRef == null) {
             if (runtime.getObject() != null) {
                 parentCRef = runtime.getObject().getCRef();
             }
         }
         this.cref = new SinglyLinkedList(this, parentCRef);
 
         runtime.moduleLastId++;
         this.id = runtime.moduleLastId;
     }
 
     public static RubyClass createModuleClass(Ruby runtime, RubyClass moduleClass) {
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyModule.class);   
         RubyClass moduleMetaClass = moduleClass.getMetaClass();
         moduleClass.index = ClassIndex.MODULE;
 
         moduleClass.defineFastMethod("===", callbackFactory.getFastMethod("op_eqq", IRubyObject.class));
         moduleClass.defineFastMethod("<=>", callbackFactory.getFastMethod("op_cmp", IRubyObject.class));
         moduleClass.defineFastMethod("<", callbackFactory.getFastMethod("op_lt", IRubyObject.class));
         moduleClass.defineFastMethod("<=", callbackFactory.getFastMethod("op_le", IRubyObject.class));
         moduleClass.defineFastMethod(">", callbackFactory.getFastMethod("op_gt", IRubyObject.class));
         moduleClass.defineFastMethod(">=", callbackFactory.getFastMethod("op_ge", IRubyObject.class));
         moduleClass.defineFastMethod("ancestors", callbackFactory.getFastMethod("ancestors"));
         moduleClass.defineFastMethod("class_variables", callbackFactory.getFastMethod("class_variables"));
         moduleClass.defineFastMethod("const_defined?", callbackFactory.getFastMethod("const_defined", IRubyObject.class));
         moduleClass.defineFastMethod("const_get", callbackFactory.getFastMethod("const_get", IRubyObject.class));
         moduleClass.defineMethod("const_missing", callbackFactory.getMethod("const_missing", IRubyObject.class));
         moduleClass.defineFastMethod("const_set", callbackFactory.getFastMethod("const_set", IRubyObject.class, IRubyObject.class));
         moduleClass.defineFastMethod("constants", callbackFactory.getFastMethod("constants"));
         moduleClass.defineMethod("extended", callbackFactory.getMethod("extended", IRubyObject.class));
         moduleClass.defineFastMethod("included", callbackFactory.getFastMethod("included", IRubyObject.class));
         moduleClass.defineFastMethod("included_modules", callbackFactory.getFastMethod("included_modules"));
         moduleClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         moduleClass.defineFastMethod("initialize_copy", callbackFactory.getFastMethod("initialize_copy", IRubyObject.class));
         moduleClass.defineFastMethod("instance_method", callbackFactory.getFastMethod("instance_method", IRubyObject.class));
         moduleClass.defineFastMethod("instance_methods",callbackFactory.getFastOptMethod("instance_methods"));
         moduleClass.defineFastMethod("method_defined?", callbackFactory.getFastMethod("method_defined", IRubyObject.class));
         moduleClass.defineMethod("module_eval", callbackFactory.getOptMethod("module_eval"));
         moduleClass.defineFastMethod("name", callbackFactory.getFastMethod("name"));
         moduleClass.defineFastMethod("private_class_method", callbackFactory.getFastOptMethod("private_class_method"));
         moduleClass.defineFastMethod("private_instance_methods", callbackFactory.getFastOptMethod("private_instance_methods"));
         moduleClass.defineFastMethod("protected_instance_methods", callbackFactory.getFastOptMethod("protected_instance_methods"));
         moduleClass.defineFastMethod("public_class_method", callbackFactory.getFastOptMethod("public_class_method"));
         moduleClass.defineFastMethod("public_instance_methods", callbackFactory.getFastOptMethod("public_instance_methods"));
         moduleClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         
         moduleClass.defineAlias("class_eval", "module_eval");
         
         moduleClass.defineFastPrivateMethod("alias_method", callbackFactory.getFastMethod("alias_method", IRubyObject.class, IRubyObject.class));
         moduleClass.defineFastPrivateMethod("append_features", callbackFactory.getFastMethod("append_features", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("attr", callbackFactory.getFastOptMethod("attr"));
         moduleClass.defineFastPrivateMethod("attr_reader", callbackFactory.getFastOptMethod("attr_reader"));
         moduleClass.defineFastPrivateMethod("attr_writer", callbackFactory.getFastOptMethod("attr_writer"));
         moduleClass.defineFastPrivateMethod("attr_accessor", callbackFactory.getFastOptMethod("attr_accessor"));
         moduleClass.definePrivateMethod("define_method", callbackFactory.getOptMethod("define_method"));
         moduleClass.defineFastPrivateMethod("extend_object", callbackFactory.getFastMethod("extend_object", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("include", callbackFactory.getFastOptMethod("include"));
         moduleClass.definePrivateMethod("method_added", callbackFactory.getMethod("method_added", IRubyObject.class));
         moduleClass.definePrivateMethod("method_removed", callbackFactory.getMethod("method_removed", IRubyObject.class));
         moduleClass.definePrivateMethod("method_undefined", callbackFactory.getMethod("method_undefined", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("module_function", callbackFactory.getFastOptMethod("module_function"));
         moduleClass.defineFastPrivateMethod("public", callbackFactory.getFastOptMethod("rbPublic"));
         moduleClass.defineFastPrivateMethod("protected", callbackFactory.getFastOptMethod("rbProtected"));
         moduleClass.defineFastPrivateMethod("private", callbackFactory.getFastOptMethod("rbPrivate"));
         moduleClass.defineFastPrivateMethod("remove_class_variable", callbackFactory.getFastMethod("remove_class_variable", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("remove_const", callbackFactory.getFastMethod("remove_const", IRubyObject.class));
         moduleClass.defineFastPrivateMethod("remove_method", callbackFactory.getFastOptMethod("remove_method"));
         moduleClass.defineFastPrivateMethod("undef_method", callbackFactory.getFastMethod("undef_method", IRubyObject.class));
         
         moduleMetaClass.defineMethod("nesting", callbackFactory.getSingletonMethod("nesting"));
 
         callbackFactory = runtime.callbackFactory(RubyKernel.class);
         moduleClass.defineFastMethod("autoload", callbackFactory.getFastSingletonMethod("autoload", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         moduleClass.defineFastMethod("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", RubyKernel.IRUBY_OBJECT));
 
         return moduleClass;
     }    
     
     static ObjectAllocator MODULE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return RubyModule.newModule(runtime, klass, null);
         }
     };
     
     public int getNativeTypeIndex() {
         return ClassIndex.MODULE;
     }
 
     public static final byte EQQ_SWITCHVALUE = 1;
 
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, int methodIndex, String name,
             IRubyObject[] args, CallType callType, Block block) {
         // If tracing is on, don't do STI dispatch
         if (context.getRuntime().getTraceFunction() != null) return callMethod(context, rubyclass, name, args, callType, block);
         
         switch (getRuntime().getSelectorTable().table[rubyclass.index][methodIndex]) {
         case EQQ_SWITCHVALUE:
             if (args.length != 1) throw context.getRuntime().newArgumentError("wrong number of arguments(" + args.length + " for " + 1 + ")");
             return op_eqq(args[0]);
         case 0:
         default:
             return super.callMethod(context, rubyclass, name, args, callType, block);
         }
     }
 
     /** Getter for property superClass.
      * @return Value of property superClass.
      */
     public RubyClass getSuperClass() {
         return superClass;
     }
 
     protected void setSuperClass(RubyClass superClass) {
         this.superClass = superClass;
     }
 
     public RubyModule getParent() {
         if (cref.getNext() == null) {
             return null;
         }
 
         return (RubyModule)cref.getNext().getValue();
     }
 
     public void setParent(RubyModule p) {
         cref.setNext(p.getCRef());
     }
 
     public Map getMethods() {
         return methods;
     }
     
     public void putMethod(Object name, DynamicMethod method) {
         // FIXME: kinda hacky...flush STI here
         getRuntime().getSelectorTable().table[index][MethodIndex.getIndex((String)name)] = 0;
         getMethods().put(name, method);
     }
 
     public boolean isModule() {
         return true;
     }
 
     public boolean isClass() {
         return false;
     }
 
     public boolean isSingleton() {
         return false;
     }
 
     /**
      * Is this module one that in an included one (e.g. an IncludedModuleWrapper). 
      */
     public boolean isIncluded() {
         return false;
     }
 
     public RubyModule getNonIncludedClass() {
         return this;
     }
 
     public String getBaseName() {
         return classId;
     }
 
     public void setBaseName(String name) {
         classId = name;
     }
 
     /**
      * Generate a fully-qualified class name or a #-style name for anonymous and singleton classes.
      * 
      * Ruby C equivalent = "classname"
      * 
      * @return The generated class name
      */
     public String getName() {
         if (getBaseName() == null) {
             if (isClass()) {
                 return "#<" + "Class" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             } else {
                 return "#<" + "Module" + ":01x" + Integer.toHexString(System.identityHashCode(this)) + ">";
             }
         }
 
         StringBuffer result = new StringBuffer(getBaseName());
         RubyClass objectClass = getRuntime().getObject();
 
         for (RubyModule p = this.getParent(); p != null && p != objectClass; p = p.getParent()) {
-            result.insert(0, "::").insert(0, p.getBaseName());
+            String pName = p.getBaseName();
+            // This is needed when the enclosing class or module is a singleton.
+            // In that case, we generated a name such as null::Foo, which broke 
+            // Marshalling, among others. The correct thing to do in this situation 
+            // is to insert the generate the name of form #<Class:01xasdfasd> if 
+            // it's a singleton module/class, which this code accomplishes.
+            if(pName == null) {
+                pName = p.getName();
+            }
+            result.insert(0, "::").insert(0, pName);
         }
 
         return result.toString();
     }
 
     /**
      * Create a wrapper to use for including the specified module into this one.
      * 
      * Ruby C equivalent = "include_class_new"
      * 
      * @return The module wrapper
      */
     public IncludedModuleWrapper newIncludeClass(RubyClass superClazz) {
         IncludedModuleWrapper includedModule = new IncludedModuleWrapper(getRuntime(), superClazz, this);
 
         // include its parent (and in turn that module's parents)
         if (getSuperClass() != null) {
             includedModule.includeModule(getSuperClass());
         }
 
         return includedModule;
     }
 
     /**
      * Search this and parent modules for the named variable.
      * 
      * @param name The variable to search for
      * @return The module in which that variable is found, or null if not found
      */
     private RubyModule getModuleWithInstanceVar(String name) {
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getInstanceVariable(name) != null) {
                 return p;
             }
         }
         return null;
     }
 
     /**
      * Set the named class variable to the given value, provided taint and freeze allow setting it.
      * 
      * Ruby C equivalent = "rb_cvar_set"
      * 
      * @param name The variable name to set
      * @param value The value to set it to
      */
     public IRubyObject setClassVar(String name, IRubyObject value) {
         RubyModule module = getModuleWithInstanceVar(name);
 
         if (module == null) {
             module = this;
         }
 
         return module.setInstanceVariable(name, value, CVAR_TAINT_ERROR, CVAR_FREEZE_ERROR);
     }
 
     /**
      * Retrieve the specified class variable, searching through this module, included modules, and supermodules.
      * 
      * Ruby C equivalent = "rb_cvar_get"
      * 
      * @param name The name of the variable to retrieve
      * @return The variable's value, or throws NameError if not found
      */
     public IRubyObject getClassVar(String name) {
         RubyModule module = getModuleWithInstanceVar(name);
 
         if (module != null) {
             IRubyObject variable = module.getInstanceVariable(name);
 
             return variable == null ? getRuntime().getNil() : variable;
         }
 
         throw getRuntime().newNameError("uninitialized class variable " + name + " in " + getName(), name);
     }
 
     /**
      * Is class var defined?
      * 
      * Ruby C equivalent = "rb_cvar_defined"
      * 
      * @param name The class var to determine "is defined?"
      * @return true if true, false if false
      */
     public boolean isClassVarDefined(String name) {
         return getModuleWithInstanceVar(name) != null;
     }
 
     /**
      * Set the named constant on this module. Also, if the value provided is another Module and
      * that module has not yet been named, assign it the specified name.
      * 
      * @param name The name to assign
      * @param value The value to assign to it; if an unnamed Module, also set its basename to name
      * @return The result of setting the variable.
      * @see RubyObject#setInstanceVariable(String, IRubyObject, String, String)
      */
     public IRubyObject setConstant(String name, IRubyObject value) {
         if(getConstantAt(name) != null) {
             getRuntime().getWarnings().warn("already initialized constant " + name);
         }
 
         IRubyObject result = setInstanceVariable(name, value, "Insecure: can't set constant",
                 "class/module");
 
         // if adding a module under a constant name, set that module's basename to the constant name
         if (value instanceof RubyModule) {
             RubyModule module = (RubyModule)value;
             if (module.getBaseName() == null) {
                 module.setBaseName(name);
                 module.setParent(this);
             }
             /*
             module.setParent(this);
             */
         }
         return result;
     }
 
     /**
      * Finds a class that is within the current module (or class).
      * 
      * @param name to be found in this module (or class)
      * @return the class or null if no such class
      */
     public RubyClass getClass(String name) {
         IRubyObject module = getConstantAt(name);
 
         return  (module instanceof RubyClass) ? (RubyClass) module : null;
     }
 
     /**
      * Base implementation of Module#const_missing, throws NameError for specific missing constant.
      * 
      * @param name The constant name which was found to be missing
      * @return Nothing! Absolutely nothing! (though subclasses might choose to return something)
      */
     public IRubyObject const_missing(IRubyObject name, Block block) {
         /* Uninitialized constant */
         if (this != getRuntime().getObject()) {
             throw getRuntime().newNameError("uninitialized constant " + getName() + "::" + name.asSymbol(), "" + getName() + "::" + name.asSymbol());
         }
 
         throw getRuntime().newNameError("uninitialized constant " + name.asSymbol(), name.asSymbol());
     }
 
     /**
      * Include a new module in this module or class.
      * 
      * @param arg The module to include
      */
     public synchronized void includeModule(IRubyObject arg) {
         assert arg != null;
 
         testFrozen("module");
         if (!isTaint()) {
             getRuntime().secure(4);
         }
 
         if (!(arg instanceof RubyModule)) {
             throw getRuntime().newTypeError("Wrong argument type " + arg.getMetaClass().getName() +
                     " (expected Module).");
         }
 
         RubyModule module = (RubyModule) arg;
 
         // Make sure the module we include does not already exist
         if (isSame(module)) {
             return;
         }
 
         infectBy(module);
 
         RubyModule p, c;
         boolean changed = false;
         boolean skip = false;
 
         c = this;
         while (module != null) {
             if (getNonIncludedClass() == module.getNonIncludedClass()) {
                 throw getRuntime().newArgumentError("cyclic include detected");
             }
 
             boolean superclassSeen = false;
             for (p = getSuperClass(); p != null; p = p.getSuperClass()) {
                 if (p instanceof IncludedModuleWrapper) {
                     if (p.getNonIncludedClass() == module.getNonIncludedClass()) {
                         if (!superclassSeen) {
                             c = p;
                         }
                         skip = true;
                         break;
                     }
                 } else {
                     superclassSeen = true;
                 }
             }
             if (!skip) {
                 // In the current logic, if we get here we know that module is not an 
                 // IncludedModuleWrapper, so there's no need to fish out the delegate. But just 
                 // in case the logic should change later, let's do it anyway:
                 c.setSuperClass(new IncludedModuleWrapper(getRuntime(), c.getSuperClass(),
                         module.getNonIncludedClass()));
                 c = c.getSuperClass();
                 changed = true;
             }
 
             module = module.getSuperClass();
             skip = false;
         }
 
         if (changed) {
             // MRI seems to blow away its cache completely after an include; is
             // what we're doing here really safe?
             List methodNames = new ArrayList(((RubyModule) arg).getMethods().keySet());
             for (Iterator iter = methodNames.iterator();
                  iter.hasNext();) {
                 String methodName = (String) iter.next();
                 getRuntime().getCacheMap().remove(methodName, searchMethod(methodName));
             }
         }
 
     }
 
     public void defineMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new FullFunctionCallbackMethod(this, method, visibility));
     }
 
     public void defineFastMethod(String name, Callback method) {
         Visibility visibility = name.equals("initialize") ?
                 Visibility.PRIVATE : Visibility.PUBLIC;
         addMethod(name, new SimpleCallbackMethod(this, method, visibility));
     }
 
     public void definePrivateMethod(String name, Callback method) {
         addMethod(name, new FullFunctionCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void defineFastPrivateMethod(String name, Callback method) {
         addMethod(name, new SimpleCallbackMethod(this, method, Visibility.PRIVATE));
     }
 
     public void undefineMethod(String name) {
         addMethod(name, UndefinedMethod.getInstance());
     }
 
     /** rb_undef
      *
      */
     public void undef(String name) {
         Ruby runtime = getRuntime();
         if (this == runtime.getObject()) {
             runtime.secure(4);
         }
         if (runtime.getSafeLevel() >= 4 && !isTaint()) {
             throw new SecurityException("Insecure: can't undef");
         }
         testFrozen("module");
         if (name.equals("__id__") || name.equals("__send__")) {
             getRuntime().getWarnings().warn("undefining `"+ name +"' may cause serious problem");
         }
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             String s0 = " class";
             RubyModule c = this;
 
             if (c.isSingleton()) {
                 IRubyObject obj = getInstanceVariable("__attached__");
 
                 if (obj != null && obj instanceof RubyModule) {
                     c = (RubyModule) obj;
                     s0 = "";
                 }
             } else if (c.isModule()) {
                 s0 = " module";
             }
 
             throw getRuntime().newNameError("Undefined method " + name + " for" + s0 + " '" + c.getName() + "'", name);
         }
         addMethod(name, UndefinedMethod.getInstance());
         
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(runtime.getCurrentContext(), "singleton_method_undefined", getRuntime().newSymbol(name));
         }else{
             callMethod(runtime.getCurrentContext(), "method_undefined", getRuntime().newSymbol(name));
     }
     }
 
     private void addCachedMethod(String name, DynamicMethod method) {
         // Included modules modify the original 'included' modules class.  Since multiple
         // classes can include the same module, we cannot cache in the original included module.
         if (!isIncluded()) {
             putMethod(name, method);
             getRuntime().getCacheMap().add(method, this);
         }
     }
 
     // TODO: Consider a better way of synchronizing 
     public void addMethod(String name, DynamicMethod method) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't define method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             // If we add a method which already is cached in this class, then we should update the 
             // cachemap so it stays up to date.
             DynamicMethod existingMethod = (DynamicMethod) getMethods().remove(name);
             if (existingMethod != null) {
                 getRuntime().getCacheMap().remove(name, existingMethod);
             }
             putMethod(name, method);
         }
     }
 
     public void removeCachedMethod(String name) {
         getMethods().remove(name);
     }
 
     public void removeMethod(String name) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't remove method");
         }
         testFrozen("class/module");
 
         // We can safely reference methods here instead of doing getMethods() since if we
         // are adding we are not using a IncludedModuleWrapper.
         synchronized(getMethods()) {
             DynamicMethod method = (DynamicMethod) getMethods().remove(name);
             if (method == null) {
                 throw getRuntime().newNameError("method '" + name + "' not defined in " + getName(), name);
             }
 
             getRuntime().getCacheMap().remove(name, method);
         }
         
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(getRuntime().getCurrentContext(), "singleton_method_removed", getRuntime().newSymbol(name));
         }else{
             callMethod(getRuntime().getCurrentContext(), "method_removed", getRuntime().newSymbol(name));
     }
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod searchMethod(String name) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             // included modules use delegates methods for we need to synchronize on result of getMethods
             synchronized(searchModule.getMethods()) {
                 // See if current class has method or if it has been cached here already
                 DynamicMethod method = (DynamicMethod) searchModule.getMethods().get(name);
                 if (method != null) {
                     if (searchModule != this) {
                         addCachedMethod(name, method);
                     }
 
                     return method;
                 }
             }
         }
 
         return UndefinedMethod.getInstance();
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public DynamicMethod retrieveMethod(String name) {
         return (DynamicMethod)getMethods().get(name);
     }
 
     /**
      * Search through this module and supermodules for method definitions. Cache superclass definitions in this class.
      * 
      * @param name The name of the method to search for
      * @return The method, or UndefinedMethod if not found
      */
     public RubyModule findImplementer(RubyModule clazz) {
         for (RubyModule searchModule = this; searchModule != null; searchModule = searchModule.getSuperClass()) {
             if (searchModule.isSame(clazz)) {
                 return searchModule;
             }
         }
 
         return null;
     }
 
     public void addModuleFunction(String name, DynamicMethod method) {
         addMethod(name, method);
         getSingletonClass().addMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineModuleFunction(String name, Callback method) {
         definePrivateMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void definePublicModuleFunction(String name, Callback method) {
         defineMethod(name, method);
         getSingletonClass().defineMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastModuleFunction(String name, Callback method) {
         defineFastPrivateMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     /** rb_define_module_function
      *
      */
     public void defineFastPublicModuleFunction(String name, Callback method) {
         defineFastMethod(name, method);
         getSingletonClass().defineFastMethod(name, method);
     }
 
     private IRubyObject getConstantInner(String name, boolean exclude) {
         IRubyObject objectClass = getRuntime().getObject();
         boolean retryForModule = false;
         RubyModule p = this;
 
         retry: while (true) {
             while (p != null) {
                 IRubyObject constant = p.getConstantAt(name);
 
                 if (constant == null) {
                     if (getRuntime().getLoadService().autoload(p.getName() + "::" + name) != null) {
                         continue;
                     }
                 }
                 if (constant != null) {
                     if (exclude && p == objectClass && this != objectClass) {
                         getRuntime().getWarnings().warn("toplevel constant " + name +
                                 " referenced by " + getName() + "::" + name);
                     }
 
                     return constant;
                 }
                 p = p.getSuperClass();
             }
 
             if (!exclude && !retryForModule && getClass().equals(RubyModule.class)) {
                 retryForModule = true;
                 p = getRuntime().getObject();
                 continue retry;
             }
 
             break;
         }
 
         return callMethod(getRuntime().getCurrentContext(), "const_missing", RubySymbol.newSymbol(getRuntime(), name));
     }
 
     /**
      * Retrieve the named constant, invoking 'const_missing' should that be appropriate.
      * 
      * @param name The constant to retrieve
      * @return The value for the constant, or null if not found
      */
     public IRubyObject getConstant(String name) {
         return getConstantInner(name, false);
     }
 
     public IRubyObject getConstantFrom(String name) {
         return getConstantInner(name, true);
     }
 
     public IRubyObject getConstantAt(String name) {
         return getInstanceVariable(name);
     }
 
     /** rb_alias
      *
      */
     public synchronized void defineAlias(String name, String oldName) {
         testFrozen("module");
         if (oldName.equals(name)) {
             return;
         }
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
         DynamicMethod method = searchMethod(oldName);
         if (method.isUndefined()) {
             if (isModule()) {
                 method = getRuntime().getObject().searchMethod(oldName);
             }
 
             if (method.isUndefined()) {
                 throw getRuntime().newNameError("undefined method `" + oldName + "' for " +
                         (isModule() ? "module" : "class") + " `" + getName() + "'", oldName);
             }
         }
         getRuntime().getCacheMap().remove(name, searchMethod(name));
         putMethod(name, new AliasMethod(method, oldName));
     }
 
     public RubyClass defineOrGetClassUnder(String name, RubyClass superClazz) {
         // This method is intended only for defining new classes in Ruby code,
         // so it uses the allocator of the specified superclass or default to
         // the Object allocator. It should NOT be used to define classes that require a native allocator.
         IRubyObject type = getConstantAt(name);
         ObjectAllocator allocator = superClazz == null ? getRuntime().getObject().getAllocator() : superClazz.getAllocator();
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         } 
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (superClazz != null && ((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newTypeError("superclass mismatch for class " + name);
         }
 
         return (RubyClass) type;
     }
 
     /** rb_define_class_under
      *
      */
     public RubyClass defineClassUnder(String name, RubyClass superClazz, ObjectAllocator allocator) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineClassUnder(name, superClazz, allocator, cref);
         }
 
         if (!(type instanceof RubyClass)) {
             throw getRuntime().newTypeError(name + " is not a class.");
         } else if (((RubyClass) type).getSuperClass().getRealClass() != superClazz) {
             throw getRuntime().newNameError(name + " is already defined.", name);
         }
 
         return (RubyClass) type;
     }
 
     public RubyModule defineModuleUnder(String name) {
         IRubyObject type = getConstantAt(name);
 
         if (type == null) {
             return getRuntime().defineModuleUnder(name, cref);
         }
 
         if (!(type instanceof RubyModule)) {
             throw getRuntime().newTypeError(name + " is not a module.");
         }
 
         return (RubyModule) type;
     }
 
     /** rb_define_const
      *
      */
     public void defineConstant(String name, IRubyObject value) {
         assert value != null;
 
         if (this == getRuntime().getClass("Class")) {
             getRuntime().secure(4);
         }
 
         if (!IdUtil.isConstant(name)) {
             throw getRuntime().newNameError("bad constant name " + name, name);
         }
 
         setConstant(name, value);
     }
 
     /** rb_mod_remove_cvar
      *
      */
     public IRubyObject removeCvar(IRubyObject name) { // Wrong Parameter ?
         if (!IdUtil.isClassVariable(name.asSymbol())) {
             throw getRuntime().newNameError("wrong class variable name " + name.asSymbol(), name.asSymbol());
         }
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't remove class variable");
         }
         testFrozen("class/module");
 
         IRubyObject value = removeInstanceVariable(name.asSymbol());
 
         if (value != null) {
             return value;
         }
 
         if (isClassVarDefined(name.asSymbol())) {
             throw cannotRemoveError(name.asSymbol());
         }
 
         throw getRuntime().newNameError("class variable " + name.asSymbol() + " not defined for " + getName(), name.asSymbol());
     }
 
     private void addAccessor(String name, boolean readable, boolean writeable) {
         ThreadContext tc = getRuntime().getCurrentContext();
 
         // Check the visibility of the previous frame, which will be the frame in which the class is being eval'ed
         Visibility attributeScope = tc.getCurrentVisibility();
         if (attributeScope.isPrivate()) {
             //FIXME warning
         } else if (attributeScope.isModuleFunction()) {
             attributeScope = Visibility.PRIVATE;
             // FIXME warning
         }
         final String variableName = "@" + name;
         final Ruby runtime = getRuntime();
         ThreadContext context = getRuntime().getCurrentContext();
         if (readable) {
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     Arity.checkArgumentCount(getRuntime(), args, 0, 0);
 
                     IRubyObject variable = self.getInstanceVariable(variableName);
 
                     return variable == null ? runtime.getNil() : variable;
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
         if (writeable) {
             name = name + "=";
             defineMethod(name, new Callback() {
                 public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                     // ENEBO: Can anyone get args to be anything but length 1?
                     Arity.checkArgumentCount(getRuntime(), args, 1, 1);
 
                     return self.setInstanceVariable(variableName, args[0]);
                 }
 
                 public Arity getArity() {
                     return Arity.singleArgument();
                 }
             });
             callMethod(context, "method_added", RubySymbol.newSymbol(getRuntime(), name));
         }
     }
 
     /** set_method_visibility
      *
      */
     public void setMethodVisibility(IRubyObject[] methods, Visibility visibility) {
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't change method visibility");
         }
 
         for (int i = 0; i < methods.length; i++) {
             exportMethod(methods[i].asSymbol(), visibility);
         }
     }
 
     /** rb_export_method
      *
      */
     public void exportMethod(String name, Visibility visibility) {
         if (this == getRuntime().getObject()) {
             getRuntime().secure(4);
         }
 
         DynamicMethod method = searchMethod(name);
 
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method '" + name + "' for " +
                                 (isModule() ? "module" : "class") + " '" + getName() + "'", name);
         }
 
         if (method.getVisibility() != visibility) {
             if (this == method.getImplementationClass()) {
                 method.setVisibility(visibility);
             } else {
                 addMethod(name, new FullFunctionCallbackMethod(this, new Callback() {
                     public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                         ThreadContext tc = self.getRuntime().getCurrentContext();
                         return self.callSuper(tc, tc.getFrameArgs(), block);
                     }
 
                     public Arity getArity() {
                         return Arity.optional();
                     }
                 }, visibility));
             }
         }
     }
 
     /**
      * MRI: rb_method_boundp
      *
      */
     public boolean isMethodBound(String name, boolean checkVisibility) {
         DynamicMethod method = searchMethod(name);
         if (!method.isUndefined()) {
             return !(checkVisibility && method.getVisibility().isPrivate());
         }
         return false;
     }
 
     public IRubyObject newMethod(IRubyObject receiver, String name, boolean bound) {
         DynamicMethod method = searchMethod(name);
         if (method.isUndefined()) {
             throw getRuntime().newNameError("undefined method `" + name +
                 "' for class `" + this.getName() + "'", name);
         }
 
         RubyMethod newMethod = null;
         if (bound) {
             newMethod = RubyMethod.newMethod(method.getImplementationClass(), name, this, name, method, receiver);
         } else {
             newMethod = RubyUnboundMethod.newUnboundMethod(method.getImplementationClass(), name, this, name, method);
         }
         newMethod.infectBy(this);
 
         return newMethod;
     }
 
     // What is argument 1 for in this method? A Method or Proc object /OB
     public IRubyObject define_method(IRubyObject[] args, Block block) {
         if (args.length < 1 || args.length > 2) {
             throw getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         IRubyObject body;
         String name = args[0].asSymbol();
         DynamicMethod newMethod = null;
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility visibility = tc.getCurrentVisibility();
 
         if (visibility.isModuleFunction()) visibility = Visibility.PRIVATE;
 
         if (args.length == 1 || args[1].isKindOf(getRuntime().getClass("Proc"))) {
             // double-testing args.length here, but it avoids duplicating the proc-setup code in two places
             RubyProc proc = (args.length == 1) ? getRuntime().newProc(false, block) : (RubyProc)args[1];
             body = proc;
 
             proc.getBlock().isLambda = true;
             proc.getBlock().getFrame().setKlazz(this);
             proc.getBlock().getFrame().setName(name);
 
             newMethod = new ProcMethod(this, proc, visibility);
         } else if (args[1].isKindOf(getRuntime().getClass("Method"))) {
             RubyMethod method = (RubyMethod)args[1];
             body = method;
 
             newMethod = new MethodMethod(this, method.unbind(null), visibility);
         } else {
             throw getRuntime().newTypeError("wrong argument type " + args[0].getType().getName() + " (expected Proc/Method)");
         }
 
         addMethod(name, newMethod);
 
         RubySymbol symbol = RubySymbol.newSymbol(getRuntime(), name);
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (tc.getPreviousVisibility().isModuleFunction()) {
             getSingletonClass().addMethod(name, new WrapperMethod(getSingletonClass(), newMethod, Visibility.PUBLIC));
         }
 
         if(isSingleton()){
             IRubyObject singleton = getInstanceVariable("__attached__"); 
             singleton.callMethod(context, "singleton_method_added", symbol);
         }else{
         callMethod(context, "method_added", symbol);
         }
 
         return body;
     }
 
     public IRubyObject executeUnder(Callback method, IRubyObject[] args, Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         context.preExecuteUnder(this, block);
 
         try {
             return method.execute(this, args, block);
         } finally {
             context.postExecuteUnder();
         }
     }
 
     // Methods of the Module Class (rb_mod_*):
 
     public static RubyModule newModule(Ruby runtime, String name) {
         return newModule(runtime, runtime.getClass("Module"), name, null);
     }
 
     public static RubyModule newModule(Ruby runtime, RubyClass type, String name) {
         return newModule(runtime, type, name, null);
     }
 
     public static RubyModule newModule(Ruby runtime, String name, SinglyLinkedList parentCRef) {
         return newModule(runtime, runtime.getClass("Module"), name, parentCRef);
     }
 
     public static RubyModule newModule(Ruby runtime, RubyClass type, String name, SinglyLinkedList parentCRef) {
         RubyModule module = new RubyModule(runtime, type, null, parentCRef, name);
         
         return module;
     }
 
     public RubyString name() {
         return getRuntime().newString(getBaseName() == null ? "" : getName());
     }
 
     /** rb_mod_class_variables
      *
      */
     public RubyArray class_variables() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             for (Iterator iter = p.instanceVariableNames(); iter.hasNext();) {
                 String id = (String) iter.next();
                 if (IdUtil.isClassVariable(id)) {
                     RubyString kval = getRuntime().newString(id);
                     if (!ary.includes(kval)) {
                         ary.append(kval);
                     }
                 }
             }
         }
         return ary;
     }
 
     protected IRubyObject cloneMethods(RubyModule clone) {
         RubyModule realType = this.getNonIncludedClass();
         for (Iterator iter = getMethods().entrySet().iterator(); iter.hasNext(); ) {
             Map.Entry entry = (Map.Entry) iter.next();
             DynamicMethod method = (DynamicMethod) entry.getValue();
 
             // Do not clone cached methods
             if (method.getImplementationClass() == realType) {
                 // A cloned method now belongs to a new class.  Set it.
                 // TODO: Make DynamicMethod immutable
                 DynamicMethod clonedMethod = method.dup();
                 clonedMethod.setImplementationClass(clone);
                 clone.putMethod(entry.getKey(), clonedMethod);
             }
         }
 
         return clone;
     }
 
     protected IRubyObject doClone() {
         return RubyModule.newModule(getRuntime(), null, cref.getNext());
     }
 
     /** rb_mod_init_copy
      * 
      */
     public IRubyObject initialize_copy(IRubyObject original) {
         assert original instanceof RubyModule;
         
         RubyModule originalModule = (RubyModule)original;
         
         super.initialize_copy(originalModule);
         
         if (!getMetaClass().isSingleton()) {
             setMetaClass(originalModule.getSingletonClassClone());
         }
 
         setSuperClass(originalModule.getSuperClass());
         
         if (originalModule.instanceVariables != null){
             setInstanceVariables(new HashMap(originalModule.getInstanceVariables()));
         }
         
         // no __classpath__ and __classid__ stuff in JRuby here (yet?)        
 
         originalModule.cloneMethods(this);
         
         return this;        
     }
 
     /** rb_mod_included_modules
      *
      */
     public RubyArray included_modules() {
         RubyArray ary = getRuntime().newArray();
 
         for (RubyModule p = getSuperClass(); p != null; p = p.getSuperClass()) {
             if (p.isIncluded()) {
                 ary.append(p.getNonIncludedClass());
             }
         }
 
         return ary;
     }
 
     /** rb_mod_ancestors
      *
      */
     public RubyArray ancestors() {
         RubyArray ary = getRuntime().newArray(getAncestorList());
 
         return ary;
     }
 
     public List getAncestorList() {
         ArrayList list = new ArrayList();
 
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if(!p.isSingleton()) {
                 list.add(p.getNonIncludedClass());
             }
         }
 
         return list;
     }
 
     public boolean hasModuleInHierarchy(RubyModule type) {
         // XXX: This check previously used callMethod("==") to check for equality between classes
         // when scanning the hierarchy. However the == check may be safe; we should only ever have
         // one instance bound to a given type/constant. If it's found to be unsafe, examine ways
         // to avoid the == call.
         for (RubyModule p = this; p != null; p = p.getSuperClass()) {
             if (p.getNonIncludedClass() == type) return true;
         }
 
         return false;
     }
 
     public int hashCode() {
         return id;
     }
 
     public RubyFixnum hash() {
         return getRuntime().newFixnum(id);
     }
 
     /** rb_mod_to_s
      *
      */
     public IRubyObject to_s() {
         if(isSingleton()){            
             IRubyObject attached = getInstanceVariable("__attached__");
             StringBuffer buffer = new StringBuffer("#<Class:");
             if(attached instanceof RubyClass || attached instanceof RubyModule){
                 buffer.append(attached.inspect());
             }else{
                 buffer.append(attached.anyToString());
             }
             buffer.append(">");
             return getRuntime().newString(buffer.toString());
         }
         return getRuntime().newString(getName());
     }
 
     /** rb_mod_eqq
      *
      */
     public RubyBoolean op_eqq(IRubyObject obj) {
         return getRuntime().newBoolean(obj.isKindOf(this));
     }
 
     /** rb_mod_le
     *
     */
    public IRubyObject op_le(IRubyObject obj) {
        if (!(obj instanceof RubyModule)) {
            throw getRuntime().newTypeError("compared with non class/module");
        }
 
        if (isKindOfModule((RubyModule)obj)) {
            return getRuntime().getTrue();
        } else if (((RubyModule)obj).isKindOfModule(this)) {
            return getRuntime().getFalse();
        }
 
        return getRuntime().getNil();
    }
diff --git a/src/org/jruby/RubyStruct.java b/src/org/jruby/RubyStruct.java
index fd46f0a798..0d975bdcbb 100644
--- a/src/org/jruby/RubyStruct.java
+++ b/src/org/jruby/RubyStruct.java
@@ -1,484 +1,483 @@
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
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
 
 import java.util.List;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.IdUtil;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.runtime.ClassIndex;
 
 /**
  * @author  jpetersen
  */
 public class RubyStruct extends RubyObject {
     private IRubyObject[] values;
 
     /**
      * Constructor for RubyStruct.
      * @param runtime
      * @param rubyClass
      */
     public RubyStruct(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
 
     public static RubyClass createStructClass(Ruby runtime) {
         // TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here, but it's unclear how Structs
         // work with marshalling. Confirm behavior and ensure we're doing this correctly. JRUBY-415
         RubyClass structClass = runtime.defineClass("Struct", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         structClass.index = ClassIndex.STRUCT;
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyStruct.class);
         structClass.includeModule(runtime.getModule("Enumerable"));
 
         structClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
 
         structClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
         structClass.defineMethod("clone", callbackFactory.getMethod("rbClone"));
 
         structClass.defineFastMethod("==", callbackFactory.getFastMethod("equal", RubyKernel.IRUBY_OBJECT));
 
         structClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
         structClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         structClass.defineFastMethod("to_a", callbackFactory.getFastMethod("to_a"));
         structClass.defineFastMethod("values", callbackFactory.getFastMethod("to_a"));
         structClass.defineFastMethod("size", callbackFactory.getFastMethod("size"));
         structClass.defineFastMethod("length", callbackFactory.getFastMethod("size"));
 
         structClass.defineMethod("each", callbackFactory.getMethod("each"));
         structClass.defineMethod("each_pair", callbackFactory.getMethod("each_pair"));
         structClass.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         structClass.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
 
         structClass.defineFastMethod("members", callbackFactory.getFastMethod("members"));
 
         return structClass;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.STRUCT;
     }
 
     private static IRubyObject getInstanceVariable(RubyClass type, String name) {
         RubyClass structClass = type.getRuntime().getClass("Struct");
 
         while (type != null && type != structClass) {
             IRubyObject variable = type.getInstanceVariable(name);
             if (variable != null) {
                 return variable;
             }
 
             type = type.getSuperClass();
         }
 
         return type.getRuntime().getNil();
     }
 
     private RubyClass classOf() {
         return getMetaClass() instanceof MetaClass ? getMetaClass().getSuperClass() : getMetaClass();
     }
 
     private void modify() {
         testFrozen("Struct is frozen");
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify struct");
         }
     }
 
     private IRubyObject setByName(String name, IRubyObject value) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private IRubyObject getByName(String name) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i];
             }
         }
 
         throw notStructMemberError(name);
     }
 
     // Struct methods
 
     /** Create new Struct class.
      *
      * MRI: rb_struct_s_def / make_struct
      *
      */
     public static RubyClass newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = null;
         Ruby runtime = recv.getRuntime();
 
         if (args.length > 0 && args[0] instanceof RubyString) {
             name = args[0].toString();
         }
 
         RubyArray member = recv.getRuntime().newArray();
 
         for (int i = name == null ? 0 : 1; i < args.length; i++) {
             member.append(RubySymbol.newSymbol(recv.getRuntime(), args[i].asSymbol()));
         }
 
         RubyClass newStruct;
         RubyClass superClass = (RubyClass)recv;
 
         if (name == null) {
             newStruct = new RubyClass(superClass, superClass.getAllocator());
         } else {
             if (!IdUtil.isConstant(name)) {
                 throw runtime.newNameError("identifier " + name + " needs to be constant", name);
             }
 
             IRubyObject type = superClass.getConstantAt(name);
 
             if (type != null) {
                 runtime.getWarnings().warn(runtime.getCurrentContext().getFramePosition(), "redefining constant Struct::" + name);
             }
             newStruct = superClass.newSubClass(name, superClass.getAllocator(), superClass.getCRef());
         }
 
         newStruct.index = ClassIndex.STRUCT;
         
         newStruct.setInstanceVariable("__size__", member.length());
         newStruct.setInstanceVariable("__member__", member);
 
         CallbackFactory callbackFactory = recv.getRuntime().callbackFactory(RubyStruct.class);
         newStruct.getSingletonClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.getSingletonClass().defineMethod("[]", callbackFactory.getOptSingletonMethod("newStruct"));
         newStruct.getSingletonClass().defineMethod("members", callbackFactory.getSingletonMethod("members"));
 
         // define access methods.
         for (int i = name == null ? 0 : 1; i < args.length; i++) {
             String memberName = args[i].asSymbol();
             newStruct.defineMethod(memberName, callbackFactory.getMethod("get"));
             newStruct.defineMethod(memberName + "=", callbackFactory.getMethod("set", RubyKernel.IRUBY_OBJECT));
         }
         
         if (block.isGiven()) {
             block.yield(recv.getRuntime().getCurrentContext(), null, newStruct, newStruct, false);
         }
 
         return newStruct;
     }
 
     /** Create new Structure.
      *
      * MRI: struct_alloc
      *
      */
     public static RubyStruct newStruct(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyStruct struct = new RubyStruct(recv.getRuntime(), (RubyClass) recv);
 
         int size = RubyNumeric.fix2int(getInstanceVariable((RubyClass) recv, "__size__"));
 
         struct.values = new IRubyObject[size];
 
         struct.callInit(args, block);
 
         return struct;
     }
 
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         modify();
 
         int size = RubyNumeric.fix2int(getInstanceVariable(getMetaClass(), "__size__"));
 
         if (args.length > size) {
             throw getRuntime().newArgumentError("struct size differs (" + args.length +" for " + size + ")");
         }
 
         for (int i = 0; i < args.length; i++) {
             values[i] = args[i];
         }
 
         for (int i = args.length; i < size; i++) {
             values[i] = getRuntime().getNil();
         }
 
         return getRuntime().getNil();
     }
     
     public static RubyArray members(IRubyObject recv, Block block) {
         RubyArray member = (RubyArray) getInstanceVariable((RubyClass) recv, "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         RubyArray result = recv.getRuntime().newArray(member.getLength());
         for (int i = 0,k=member.getLength(); i < k; i++) {
             result.append(recv.getRuntime().newString(member.eltInternal(i).asSymbol()));
         }
 
         return result;
     }
 
     public RubyArray members() {
         return members(classOf(), Block.NULL_BLOCK);
     }
 
     public IRubyObject set(IRubyObject value, Block block) {
         String name = getRuntime().getCurrentContext().getFrameName();
         if (name.endsWith("=")) {
             name = name.substring(0, name.length() - 1);
         }
 
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         modify();
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i] = value;
             }
         }
 
         throw notStructMemberError(name);
     }
 
     private RaiseException notStructMemberError(String name) {
         return getRuntime().newNameError(name + " is not struct member", name);
     }
 
     public IRubyObject get(Block block) {
         String name = getRuntime().getCurrentContext().getFrameName();
 
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (member.eltInternal(i).asSymbol().equals(name)) {
                 return values[i];
             }
         }
 
         throw notStructMemberError(name);
     }
 
     public IRubyObject rbClone(Block block) {
         RubyStruct clone = new RubyStruct(getRuntime(), getMetaClass());
 
         clone.values = new IRubyObject[values.length];
         System.arraycopy(values, 0, clone.values, 0, values.length);
 
         clone.setFrozen(this.isFrozen());
         clone.setTaint(this.isTaint());
 
         return clone;
     }
 
     public IRubyObject equal(IRubyObject other) {
         if (this == other) {
             return getRuntime().getTrue();
         } else if (!(other instanceof RubyStruct)) {
             return getRuntime().getFalse();
         } else if (getMetaClass() != other.getMetaClass()) {
-            System.err.println("differing metaclass");
             return getRuntime().getFalse();
         } else {
             for (int i = 0; i < values.length; i++) {
                 if (!values[i].equals(((RubyStruct) other).values[i])) {
                     return getRuntime().getFalse();
                 }
             }
             return getRuntime().getTrue();
         }
     }
 
     public IRubyObject to_s() {
         return inspect();
     }
 
     public IRubyObject inspect() {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         StringBuffer sb = new StringBuffer(100);
 
         sb.append("#<struct ").append(getMetaClass().getName()).append(' ');
 
         for (int i = 0,k=member.getLength(); i < k; i++) {
             if (i > 0) {
                 sb.append(", ");
             }
 
             sb.append(member.eltInternal(i).asSymbol()).append("=");
             sb.append(values[i].callMethod(getRuntime().getCurrentContext(), "inspect"));
         }
 
         sb.append('>');
 
         return getRuntime().newString(sb.toString()); // OBJ_INFECT
     }
 
     public RubyArray to_a() {
         return getRuntime().newArray(values);
     }
 
     public RubyFixnum size() {
         return getRuntime().newFixnum(values.length);
     }
 
     public IRubyObject each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < values.length; i++) {
             block.yield(context, values[i]);
         }
 
         return this;
     }
 
     public IRubyObject each_pair(Block block) {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0; i < values.length; i++) {
             block.yield(context, getRuntime().newArrayNoCopy(new IRubyObject[]{member.eltInternal(i), values[i]}));
         }
 
         return this;
     }
 
     public IRubyObject aref(IRubyObject key) {
         if (key instanceof RubyString || key instanceof RubySymbol) {
             return getByName(key.asSymbol());
         }
 
         int idx = RubyNumeric.fix2int(key);
 
         idx = idx < 0 ? values.length + idx : idx;
 
         if (idx < 0) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         } else if (idx >= values.length) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         }
 
         return values[idx];
     }
 
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         if (key instanceof RubyString || key instanceof RubySymbol) {
             return setByName(key.asSymbol(), value);
         }
 
         int idx = RubyNumeric.fix2int(key);
 
         idx = idx < 0 ? values.length + idx : idx;
 
         if (idx < 0) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         } else if (idx >= values.length) {
             throw getRuntime().newIndexError("offset " + idx + " too large for struct (size:" + values.length + ")");
         }
 
         modify();
         return values[idx] = value;
     }
 
     public static void marshalTo(RubyStruct struct, MarshalStream output) throws java.io.IOException {
         output.dumpDefaultObjectHeader('S', struct.getMetaClass());
 
         List members = ((RubyArray) getInstanceVariable(struct.classOf(), "__member__")).getList();
         output.writeInt(members.size());
 
         for (int i = 0; i < members.size(); i++) {
             RubySymbol name = (RubySymbol) members.get(i);
             output.dumpObject(name);
             output.dumpObject(struct.values[i]);
         }
     }
 
     public static RubyStruct unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         Ruby runtime = input.getRuntime();
 
         RubySymbol className = (RubySymbol) input.unmarshalObject();
         RubyClass rbClass = pathToClass(runtime, className.asSymbol());
         if (rbClass == null) {
             throw runtime.newNameError("uninitialized constant " + className, className.asSymbol());
         }
 
         RubyArray mem = members(rbClass, Block.NULL_BLOCK);
 
         int len = input.unmarshalInt();
         IRubyObject[] values = new IRubyObject[len];
         for(int i = 0; i < len; i++) {
             values[i] = runtime.getNil();
         }
         RubyStruct result = newStruct(rbClass, values, Block.NULL_BLOCK);
         input.registerLinkTarget(result);
         for(int i = 0; i < len; i++) {
             IRubyObject slot = input.unmarshalObject();
             if(!mem.eltInternal(i).toString().equals(slot.toString())) {
                 throw runtime.newTypeError("struct " + rbClass.getName() + " not compatible (:" + slot + " for :" + mem.eltInternal(i) + ")");
             }
             result.aset(runtime.newFixnum(i), input.unmarshalObject());
         }
         return result;
     }
 
     private static RubyClass pathToClass(Ruby runtime, String path) {
         // FIXME: Throw the right ArgumentError's if the class is missing
         // or if it's a module.
         return (RubyClass) runtime.getClassFromPath(path);
     }
 }
