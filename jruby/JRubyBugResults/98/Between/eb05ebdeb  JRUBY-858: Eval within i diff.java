diff --git a/src/org/jruby/RubyBinding.java b/src/org/jruby/RubyBinding.java
index 96bd185ba3..666bd1fc93 100644
--- a/src/org/jruby/RubyBinding.java
+++ b/src/org/jruby/RubyBinding.java
@@ -1,121 +1,136 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author  jpetersen
  */
 public class RubyBinding extends RubyObject {
     private Block block;
 
     public RubyBinding(Ruby runtime, RubyClass rubyClass, Block block) {
         super(runtime, rubyClass);
         
         this.block = block;
     }
     
     private static ObjectAllocator BINDING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyBinding instance = runtime.newBinding();
             
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
     
     public static RubyClass createBindingClass(Ruby runtime) {
         RubyClass bindingClass = runtime.defineClass("Binding", runtime.getObject(), BINDING_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyBinding.class);   
         
         bindingClass.getMetaClass().defineMethod("of_caller", callbackFactory.getSingletonMethod("of_caller"));
         
         return bindingClass;
     }
 
     public Block getBlock() {
         return block;
     }
 
     // Proc class
     
     public static RubyBinding newBinding(Ruby runtime, Block block) {
         return new RubyBinding(runtime, runtime.getClass("Binding"), block);
     }
 
     public static RubyBinding newBinding(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         
         // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
         Frame frame = context.getCurrentFrame();
         Block bindingBlock = Block.createBinding(frame, context.getCurrentScope());
         
         return new RubyBinding(runtime, runtime.getClass("Binding"), bindingBlock);
     }
 
     /**
      * Create a binding appropriate for a bare "eval", by using the previous (caller's) frame and current
      * scope.
      */
     public static RubyBinding newBindingForEval(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         
-        // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
-        Frame frame = context.getPreviousFrame();
-        Block bindingBlock = Block.createBinding(frame, context.getCurrentScope());
+        // This requires some explaining.  We use Frame values when executing blocks to fill in 
+        // various values in ThreadContext and EvalState.eval like rubyClass, cref, and self.
+        // Largely, for an eval that is using the logical binding at a place where the eval is 
+        // called we mostly want to use the current frames value for this.  Most importantly, 
+        // we need that self (JRUBY-858) at this point.  We also need to make sure that returns
+        // jump to the right place (which happens to be the previous frame).  Lastly, we do not
+        // want the current frames klazz since that will be the klazz represented of self.  We
+        // want the class right before the eval (well we could use cref class for this too I think).
+        // Once we end up having Frames created earlier I think the logic of stuff like this will
+        // be better since we won't be worried about setting Frame to setup other variables/stacks
+        // but just making sure Frame itself is correct...
+        
+        Frame previousFrame = context.getPreviousFrame();
+        Frame currentFrame = context.getCurrentFrame();
+        currentFrame.setKlazz(previousFrame.getKlazz());
+        currentFrame.setJumpTarget(previousFrame);
+        
+        Block bindingBlock = Block.createBinding(currentFrame, context.getCurrentScope());
         
         return new RubyBinding(runtime, runtime.getClass("Binding"), bindingBlock);
     }
 
     public static RubyBinding newBindingOfCaller(Ruby runtime) {
         ThreadContext context = runtime.getCurrentContext();
         
         // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
         Frame frame = context.getPreviousFrame();
         Block bindingBlock = Block.createBinding(frame, context.getPreviousScope());
         
         return new RubyBinding(runtime, runtime.getClass("Binding"), bindingBlock);
     }
     
     public static IRubyObject of_caller(IRubyObject recv, Block aBlock) {
         return RubyBinding.newBindingOfCaller(recv.getRuntime());
     }
 }
diff --git a/src/org/jruby/RubyDigest.java b/src/org/jruby/RubyDigest.java
index 5b6ca8ebfc..8101426770 100644
--- a/src/org/jruby/RubyDigest.java
+++ b/src/org/jruby/RubyDigest.java
@@ -1,245 +1,244 @@
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
  * Copyright (C) 2006, 2007 Ola Bini <ola@ologix.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
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
-import java.security.NoSuchProviderException;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class RubyDigest {
     private static Provider provider = null;
 
     public static void createDigest(Ruby runtime) {
         try {
             Class c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
             provider = (Provider)c.newInstance();
         } catch(Exception e) {
             // provider is not available
         }
 
         RubyModule mDigest = runtime.defineModule("Digest");
         RubyClass cDigestBase = mDigest.defineClassUnder("Base",runtime.getObject(), Base.BASE_ALLOCATOR);
 
         CallbackFactory basecb = runtime.callbackFactory(Base.class);
         
         cDigestBase.getMetaClass().defineFastMethod("digest",basecb.getFastSingletonMethod("s_digest",RubyKernel.IRUBY_OBJECT));
         cDigestBase.getMetaClass().defineFastMethod("hexdigest",basecb.getFastSingletonMethod("s_hexdigest",RubyKernel.IRUBY_OBJECT));
 
         cDigestBase.defineMethod("initialize",basecb.getOptMethod("initialize"));
         cDigestBase.defineFastMethod("initialize_copy",basecb.getFastMethod("initialize_copy",RubyKernel.IRUBY_OBJECT));
         cDigestBase.defineFastMethod("update",basecb.getFastMethod("update",RubyKernel.IRUBY_OBJECT));
         cDigestBase.defineFastMethod("<<",basecb.getFastMethod("update",RubyKernel.IRUBY_OBJECT));
         cDigestBase.defineFastMethod("digest",basecb.getFastMethod("digest"));
         cDigestBase.defineFastMethod("hexdigest",basecb.getFastMethod("hexdigest"));
         cDigestBase.defineFastMethod("to_s",basecb.getFastMethod("hexdigest"));
         cDigestBase.defineFastMethod("==",basecb.getFastMethod("eq",RubyKernel.IRUBY_OBJECT));
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
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_MD5 = mDigest.defineClassUnder("MD5",cDigestBase,cDigestBase.getAllocator());
         cDigest_MD5.setClassVar("metadata",runtime.newString("MD5"));
     }
 
     public static void createDigestRMD160(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         if(provider == null) {
             throw runtime.newLoadError("RMD160 not supported without BouncyCastle");
         }
 
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_RMD160 = mDigest.defineClassUnder("RMD160",cDigestBase,cDigestBase.getAllocator());
         cDigest_RMD160.setClassVar("metadata",runtime.newString("RIPEMD160"));
     }
 
     public static void createDigestSHA1(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_SHA1 = mDigest.defineClassUnder("SHA1",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA1.setClassVar("metadata",runtime.newString("SHA1"));
     }
 
     public static void createDigestSHA2(Ruby runtime) {
         runtime.getLoadService().require("digest.so");
         try {
             createMessageDigest(runtime, "SHA-256");
         } catch(NoSuchAlgorithmException e) {
             throw runtime.newLoadError("SHA2 not supported");
         }
 
         RubyModule mDigest = runtime.getModule("Digest");
         RubyClass cDigestBase = mDigest.getClass("Base");
         RubyClass cDigest_SHA2_256 = mDigest.defineClassUnder("SHA256",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_256.setClassVar("metadata",runtime.newString("SHA-256"));
         RubyClass cDigest_SHA2_384 = mDigest.defineClassUnder("SHA384",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_384.setClassVar("metadata",runtime.newString("SHA-384"));
         RubyClass cDigest_SHA2_512 = mDigest.defineClassUnder("SHA512",cDigestBase,cDigestBase.getAllocator());
         cDigest_SHA2_512.setClassVar("metadata",runtime.newString("SHA-512"));
     }
 
     public static class Base extends RubyObject {
         protected static ObjectAllocator BASE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new Base(runtime, klass);
             }
         };
         public static IRubyObject s_digest(IRubyObject recv, IRubyObject str) {
             Ruby runtime = recv.getRuntime();
             String name = ((RubyClass)recv).getClassVar("metadata").toString();
             try {
                 MessageDigest md = createMessageDigest(runtime, name);
                 return RubyString.newString(runtime, md.digest(str.convertToString().getBytes()));
             } catch(NoSuchAlgorithmException e) {
                 throw recv.getRuntime().newNotImplementedError("Unsupported digest algorithm (" + name + ")");
             }
         }
         public static IRubyObject s_hexdigest(IRubyObject recv, IRubyObject str) {
             Ruby runtime = recv.getRuntime();
             String name = ((RubyClass)recv).getClassVar("metadata").toString();
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
 
             if(type == runtime.getModule("Digest").getClass("Base")) {
                 throw runtime.newNotImplementedError("Digest::Base is an abstract class");
             }
             if(!type.isClassVarDefined("metadata")) {
                 throw runtime.newNotImplementedError("the " + type + "() function is unimplemented on this machine");
             }
             try {
                 setAlgorithm(type.getClassVar("metadata"));
             } catch(NoSuchAlgorithmException e) {
                 throw runtime.newNotImplementedError("the " + type + "() function is unimplemented on this machine");
             }
 
         }
         
         public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
             if(args.length > 0 && !args[0].isNil()) {
                 update(args[0]);
             }
             return this;
         }
 
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
 
         public IRubyObject update(IRubyObject obj) {
             data.append(obj);
             return this;
         }
 
         public IRubyObject digest() {
             algo.reset();
             return RubyString.newString(getRuntime(), algo.digest(ByteList.plain(data)));
         }
 
         public IRubyObject hexdigest() {
             algo.reset();
             return RubyString.newString(getRuntime(), ByteList.plain(toHex(algo.digest(ByteList.plain(data)))));
         }
 
         public IRubyObject eq(IRubyObject oth) {
             boolean ret = this == oth;
             if(!ret && oth instanceof Base) {
                 Base b = (Base)oth;
                 ret = this.algo.getAlgorithm().equals(b.algo.getAlgorithm()) &&
                     this.digest().equals(b.digest());
             }
 
             return ret ? getRuntime().getTrue() : getRuntime().getFalse();
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
diff --git a/src/org/jruby/RubyStruct.java b/src/org/jruby/RubyStruct.java
index cb969fc54c..72f6c43681 100644
--- a/src/org/jruby/RubyStruct.java
+++ b/src/org/jruby/RubyStruct.java
@@ -1,499 +1,499 @@
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
         structClass.defineFastMethod("eql?", callbackFactory.getFastMethod("eql_p", RubyKernel.IRUBY_OBJECT));
 
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
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyStruct)) return getRuntime().getFalse();
-        if (getMetaClass() != other.getMetaClass()) return getRuntime().getFalse();
+        if (getMetaClass().getRealClass() != other.getMetaClass().getRealClass()) return getRuntime().getFalse();
         
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         RubyStruct otherStruct = (RubyStruct)other;
             for (int i = 0; i < values.length; i++) {
             if (!values[i].equalInternal(context, otherStruct.values[i]).isTrue()) {
                 return runtime.getFalse();
                 }
             }
         return runtime.getTrue();
         }
     
     public IRubyObject eql_p(IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyStruct)) return getRuntime().getFalse();
         if (getMetaClass() != other.getMetaClass()) return getRuntime().getFalse();
         
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         RubyStruct otherStruct = (RubyStruct)other;
         for (int i = 0; i < values.length; i++) {
             if (!values[i].eqlInternal(context, otherStruct.values[i])) {
                 return runtime.getFalse();
     }
         }
         return runtime.getTrue();        
     }
 
     public IRubyObject to_s() {
         return inspect();
     }
 
     public IRubyObject inspect() {
         RubyArray member = (RubyArray) getInstanceVariable(classOf(), "__member__");
 
         assert !member.isNil() : "uninitialized struct";
 
         StringBuffer sb = new StringBuffer(100);
 
-        sb.append("#<struct ").append(getMetaClass().getName()).append(' ');
+        sb.append("#<struct ").append(getMetaClass().getRealClass().getName()).append(' ');
 
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
diff --git a/src/org/jruby/RubyThread.java b/src/org/jruby/RubyThread.java
index cf86e63898..6dd5d24d51 100644
--- a/src/org/jruby/RubyThread.java
+++ b/src/org/jruby/RubyThread.java
@@ -1,700 +1,683 @@
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
 
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.internal.runtime.FutureThread;
 import org.jruby.internal.runtime.NativeThread;
 import org.jruby.internal.runtime.RubyNativeThread;
 import org.jruby.internal.runtime.RubyRunnable;
 import org.jruby.internal.runtime.ThreadLike;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
 import edu.emory.mathcs.backport.java.util.concurrent.TimeoutException;
 import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
 import org.jruby.runtime.Arity;
 
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
  *
  * @author Jason Voegele (jason@jvoegele.com)
  */
 public class RubyThread extends RubyObject {
     private ThreadLike threadImpl;
     private Map threadLocalVariables = new HashMap();
     private boolean abortOnException;
     private IRubyObject finalResult;
     private RaiseException exitingException;
     private IRubyObject receivedException;
     private RubyThreadGroup threadGroup;
 
     private ThreadService threadService;
-    private boolean hasStarted = false;
     private volatile boolean isStopped = false;
     public Object stopLock = new Object();
     
     private volatile boolean killed = false;
     public Object killLock = new Object();
-    private RubyThread joinedByCriticalThread;
-    
-    private boolean checkLocks = false;
     
     public final ReentrantLock lock = new ReentrantLock();
-    private volatile boolean critical = false;
     
     private static boolean USE_POOLING;
     
     private static final boolean DEBUG = false;
     
    static {
        if (Ruby.isSecurityRestricted()) USE_POOLING = false;
        else USE_POOLING = Boolean.getBoolean("jruby.thread.pooling");
    }
    
     public static RubyClass createThreadClass(Ruby runtime) {
         // FIXME: In order for Thread to play well with the standard 'new' behavior,
         // it must provide an allocator that can create empty object instances which
         // initialize then fills with appropriate data.
         RubyClass threadClass = runtime.defineClass("Thread", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyThread.class);
 
         threadClass.defineFastMethod("[]", callbackFactory.getFastMethod("aref", RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("[]=", callbackFactory.getFastMethod("aset", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("abort_on_exception", callbackFactory.getFastMethod("abort_on_exception"));
         threadClass.defineFastMethod("abort_on_exception=", callbackFactory.getFastMethod("abort_on_exception_set", RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("alive?", callbackFactory.getFastMethod("is_alive"));
         threadClass.defineFastMethod("group", callbackFactory.getFastMethod("group"));
         threadClass.defineFastMethod("join", callbackFactory.getFastOptMethod("join"));
         threadClass.defineFastMethod("value", callbackFactory.getFastMethod("value"));
         threadClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
         threadClass.defineFastMethod("key?", callbackFactory.getFastMethod("has_key", RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("keys", callbackFactory.getFastMethod("keys"));
         threadClass.defineFastMethod("priority", callbackFactory.getFastMethod("priority"));
         threadClass.defineFastMethod("priority=", callbackFactory.getFastMethod("priority_set", RubyKernel.IRUBY_OBJECT));
         threadClass.defineMethod("raise", callbackFactory.getOptMethod("raise"));
         //threadClass.defineFastMethod("raise", callbackFactory.getFastMethod("raise", RubyKernel.IRUBY_OBJECT));
         threadClass.defineFastMethod("run", callbackFactory.getFastMethod("run"));
         threadClass.defineFastMethod("status", callbackFactory.getFastMethod("status"));
         threadClass.defineFastMethod("stop?", callbackFactory.getFastMethod("isStopped"));
         threadClass.defineFastMethod("wakeup", callbackFactory.getFastMethod("wakeup"));
         //        threadClass.defineMethod("value", 
         //                callbackFactory.getMethod("value"));
         threadClass.defineFastMethod("kill", callbackFactory.getFastMethod("kill"));
         threadClass.defineFastMethod("exit", callbackFactory.getFastMethod("exit"));
         
         threadClass.getMetaClass().defineFastMethod("current", callbackFactory.getFastSingletonMethod("current"));
         threadClass.getMetaClass().defineMethod("fork", callbackFactory.getOptSingletonMethod("newInstance"));
         threadClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
         threadClass.getMetaClass().defineFastMethod("list", callbackFactory.getFastSingletonMethod("list"));
         threadClass.getMetaClass().defineFastMethod("pass", callbackFactory.getFastSingletonMethod("pass"));
         threadClass.getMetaClass().defineMethod("start", callbackFactory.getOptSingletonMethod("start"));
         threadClass.getMetaClass().defineFastMethod("critical=", callbackFactory.getFastSingletonMethod("critical_set", RubyBoolean.class));
         threadClass.getMetaClass().defineFastMethod("critical", callbackFactory.getFastSingletonMethod("critical"));
         threadClass.getMetaClass().defineFastMethod("stop", callbackFactory.getFastSingletonMethod("stop"));
         threadClass.getMetaClass().defineMethod("kill", callbackFactory.getSingletonMethod("s_kill", RubyThread.class));
         threadClass.getMetaClass().defineMethod("exit", callbackFactory.getSingletonMethod("s_exit"));
         threadClass.getMetaClass().defineFastMethod("abort_on_exception", callbackFactory.getFastSingletonMethod("abort_on_exception"));
         threadClass.getMetaClass().defineFastMethod("abort_on_exception=", callbackFactory.getFastSingletonMethod("abort_on_exception_set", RubyKernel.IRUBY_OBJECT));
 
         RubyThread rubyThread = new RubyThread(runtime, threadClass);
-        // set hasStarted to true, otherwise Thread.main.status freezes
-        rubyThread.hasStarted = true;
         // TODO: need to isolate the "current" thread from class creation
         rubyThread.threadImpl = new NativeThread(rubyThread, Thread.currentThread());
         runtime.getThreadService().setMainThread(rubyThread);
         
         threadClass.getMetaClass().defineFastMethod("main", callbackFactory.getFastSingletonMethod("main"));
         
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
     public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         return startThread(recv, args, true, block);
     }
 
     /**
      * Basically the same as Thread.new . However, if class Thread is
      * subclassed, then calling start in that subclass will not invoke the
      * subclass's initialize method.
      */
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
         runtime.getThreadService().registerNewThread(rubyThread);
         
         runtime.getCurrentContext().preAdoptThread();
         
         rubyThread.callInit(IRubyObject.NULL_ARRAY, block);
-        rubyThread.hasStarted = true;
         
         return rubyThread;
     }
 
     private static RubyThread startThread(final IRubyObject recv, final IRubyObject[] args, boolean callInit, Block block) {
         if (!block.isGiven()) throw recv.getRuntime().newThreadError("must be called with a block");
 
         RubyThread rubyThread = new RubyThread(recv.getRuntime(), (RubyClass) recv);
         
         if (callInit) rubyThread.callInit(IRubyObject.NULL_ARRAY, block);
 
         if (USE_POOLING) {
             rubyThread.threadImpl = new FutureThread(rubyThread, new RubyRunnable(rubyThread, args, block));
         } else {
             rubyThread.threadImpl = new NativeThread(rubyThread, new RubyNativeThread(rubyThread, args, block));
         }
         rubyThread.threadImpl.start();
-        rubyThread.hasStarted = true;
         
         return rubyThread;
     }
     
     private void ensureCurrent() {
         if (this != getRuntime().getCurrentContext().getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     private void ensureNotCurrent() {
         if (this == getRuntime().getCurrentContext().getThread()) {
             throw new RuntimeException("internal thread method called from another thread");
         }
     }
     
     public void cleanTerminate(IRubyObject result) {
         finalResult = result;
         isStopped = true;
     }
 
     public void pollThreadEvents() {
         // check for criticalization *before* locking ourselves
         threadService.waitForCritical();
 
         ensureCurrent();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before");
         if (killed) throw new ThreadKill();
 
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " after");
         if (receivedException != null) {
             // clear this so we don't keep re-throwing
             IRubyObject raiseException = receivedException;
             receivedException = null;
             RubyModule kernelModule = getRuntime().getModule("Kernel");
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before propagating exception: " + killed);
             kernelModule.callMethod(getRuntime().getCurrentContext(), "raise", raiseException);
         }
     }
 
     private RubyThread(Ruby runtime, RubyClass type) {
         super(runtime, type);
         this.threadService = runtime.getThreadService();
         // set to default thread group
         RubyThreadGroup defaultThreadGroup = (RubyThreadGroup)runtime.getClass("ThreadGroup").getConstant("Default");
         defaultThreadGroup.add(this, Block.NULL_BLOCK);
         finalResult = runtime.getNil();
     }
 
     /**
      * Returns the status of the global ``abort on exception'' condition. The
      * default is false. When set to true, will cause all threads to abort (the
      * process will exit(0)) if an exception is raised in any thread. See also
      * Thread.abort_on_exception= .
      */
     public static RubyBoolean abort_on_exception(IRubyObject recv) {
     	Ruby runtime = recv.getRuntime();
         return runtime.isGlobalAbortOnExceptionEnabled() ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 
     public static IRubyObject abort_on_exception_set(IRubyObject recv, IRubyObject value) {
         recv.getRuntime().setGlobalAbortOnExceptionEnabled(value.isTrue());
         return value;
     }
 
     public static RubyThread current(IRubyObject recv) {
         return recv.getRuntime().getCurrentContext().getThread();
     }
 
     public static RubyThread main(IRubyObject recv) {
         return recv.getRuntime().getThreadService().getMainThread();
     }
 
     public static IRubyObject pass(IRubyObject recv) {
         Ruby runtime = recv.getRuntime();
         ThreadService ts = runtime.getThreadService();
         boolean critical = ts.getCritical();
-        RubyThread currentThread = ts.getCurrentContext().getThread();
         
         ts.setCritical(false);
         
         Thread.yield();
         
         ts.setCritical(critical);
         
         return recv.getRuntime().getNil();
     }
 
     public static RubyArray list(IRubyObject recv) {
     	RubyThread[] activeThreads = recv.getRuntime().getThreadService().getActiveRubyThreads();
         
         return recv.getRuntime().newArrayNoCopy(activeThreads);
     }
     
     private IRubyObject getSymbolKey(IRubyObject originalKey) {
         if (originalKey instanceof RubySymbol) {
             return originalKey;
         } else if (originalKey instanceof RubyString) {
             return RubySymbol.newSymbol(getRuntime(), originalKey.asSymbol());
         } else if (originalKey instanceof RubyFixnum) {
             getRuntime().getWarnings().warn("Do not use Fixnums as Symbols");
             throw getRuntime().newArgumentError(originalKey + " is not a symbol");
         } else {
             throw getRuntime().newArgumentError(originalKey + " is not a symbol");
         }
     }
 
     public IRubyObject aref(IRubyObject key) {
         key = getSymbolKey(key);
         
         if (!threadLocalVariables.containsKey(key)) {
             return getRuntime().getNil();
         }
         return (IRubyObject) threadLocalVariables.get(key);
     }
 
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         key = getSymbolKey(key);
         
         threadLocalVariables.put(key, value);
         return value;
     }
 
     public RubyBoolean abort_on_exception() {
         return abortOnException ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public IRubyObject abort_on_exception_set(IRubyObject val) {
         abortOnException = val.isTrue();
         return val;
     }
 
     public RubyBoolean is_alive() {
         return threadImpl.isAlive() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public RubyThread join(IRubyObject[] args) {
         long timeoutMillis = 0;
         if (args.length > 0) {
             if (args.length > 1) {
                 throw getRuntime().newArgumentError(args.length,1);
             }
             // MRI behavior: value given in seconds; converted to Float; less
             // than or equal to zero returns immediately; returns nil
             timeoutMillis = (long)(1000.0D * args[0].convertToFloat().getValue());
             if (timeoutMillis <= 0) {
                 return null;
             }
         }
         if (isCurrent()) {
             throw getRuntime().newThreadError("thread tried to join itself");
         }
         try {
             if (threadService.getCritical()) {
-                // set the target thread's joinedBy, so it knows it can execute during a critical section
-                joinedByCriticalThread = this;
                 threadImpl.interrupt(); // break target thread out of critical
             }
             threadImpl.join(timeoutMillis);
         } catch (InterruptedException iExcptn) {
             assert false : iExcptn;
         } catch (TimeoutException iExcptn) {
             assert false : iExcptn;
         } catch (ExecutionException iExcptn) {
             assert false : iExcptn;
         }
         if (exitingException != null) {
             throw exitingException;
         }
         return null;
     }
 
     public IRubyObject value() {
         join(new IRubyObject[0]);
         synchronized (this) {
             return finalResult;
         }
     }
 
     public IRubyObject group() {
         if (threadGroup == null) {
         	return getRuntime().getNil();
         }
         
         return threadGroup;
     }
     
     void setThreadGroup(RubyThreadGroup rubyThreadGroup) {
     	threadGroup = rubyThreadGroup;
     }
     
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
 
     public RubyBoolean has_key(IRubyObject key) {
         key = getSymbolKey(key);
         
         return getRuntime().newBoolean(threadLocalVariables.containsKey(key));
     }
 
     public RubyArray keys() {
         IRubyObject[] keys = new IRubyObject[threadLocalVariables.size()];
         
         return RubyArray.newArrayNoCopy(getRuntime(), (IRubyObject[])threadLocalVariables.keySet().toArray(keys));
     }
     
     public static IRubyObject critical_set(IRubyObject receiver, RubyBoolean value) {
     	receiver.getRuntime().getThreadService().setCritical(value.isTrue());
     	
     	return value;
     }
 
     public static IRubyObject critical(IRubyObject receiver) {
     	return receiver.getRuntime().newBoolean(receiver.getRuntime().getThreadService().getCritical());
     }
     
     public static IRubyObject stop(IRubyObject receiver) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
         Object stopLock = rubyThread.stopLock;
         
         synchronized (stopLock) {
             try {
                 rubyThread.isStopped = true;
                 // attempt to decriticalize all if we're the critical thread
                 receiver.getRuntime().getThreadService().setCritical(false);
                 
                 stopLock.wait();
             } catch (InterruptedException ie) {
                 // ignore, continue;
             }
             rubyThread.isStopped = false;
         }
         
         return receiver.getRuntime().getNil();
     }
     
     public static IRubyObject s_kill(IRubyObject receiver, RubyThread rubyThread, Block block) {
         return rubyThread.kill();
     }
     
     public static IRubyObject s_exit(IRubyObject receiver, Block block) {
         RubyThread rubyThread = receiver.getRuntime().getThreadService().getCurrentContext().getThread();
         
         rubyThread.killed = true;
         // attempt to decriticalize all if we're the critical thread
         receiver.getRuntime().getThreadService().setCritical(false);
         
         throw new ThreadKill();
     }
 
     public RubyBoolean isStopped() {
     	// not valid for "dead" state
     	return getRuntime().newBoolean(isStopped);
     }
     
     public RubyThread wakeup() {
     	synchronized (stopLock) {
     		stopLock.notifyAll();
     	}
     	
     	return this;
     }
     
     public RubyFixnum priority() {
         return getRuntime().newFixnum(threadImpl.getPriority());
     }
 
     public IRubyObject priority_set(IRubyObject priority) {
         // FIXME: This should probably do some translation from Ruby priority levels to Java priority levels (until we have green threads)
         int iPriority = RubyNumeric.fix2int(priority);
         
         if (iPriority < Thread.MIN_PRIORITY) {
             iPriority = Thread.MIN_PRIORITY;
         } else if (iPriority > Thread.MAX_PRIORITY) {
             iPriority = Thread.MAX_PRIORITY;
         }
         
         threadImpl.setPriority(iPriority);
         return priority;
     }
 
     public IRubyObject raise(IRubyObject[] args, Block block) {
         ensureNotCurrent();
         Ruby runtime = getRuntime();
         
         if (DEBUG) System.out.println("thread " + Thread.currentThread() + " before raising");
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         try {
-            this.checkLocks = true;
-            
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " raising");
             receivedException = prepareRaiseException(runtime, args, block);
 
             // interrupt the target thread in case it's blocking or waiting
             threadImpl.interrupt();
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
-            this.checkLocks = false;
         }
 
         return this;
     }
 
     private IRubyObject prepareRaiseException(Ruby runtime, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(getRuntime(), args, 0, 3); 
 
         if(args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if(lastException.isNil()) {
                 return new RaiseException(runtime, runtime.getClass("RuntimeError"), "", false).getException();
             } 
             return lastException;
         }
 
         IRubyObject exception;
         ThreadContext context = getRuntime().getCurrentContext();
         
         if(args.length == 1) {
             if(args[0] instanceof RubyString) {
                 return runtime.getClass("RuntimeError").newInstance(args, block);
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
         
         if (!exception.isKindOf(runtime.getClass("Exception"))) {
             return runtime.newTypeError("exception object expected").getException();
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         return exception;
     }
     
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
         ensureCurrent();
         synchronized (stopLock) {
             try {
                 isStopped = true;
                 stopLock.wait(millis);
             } finally {
                 isStopped = false;
                 pollThreadEvents();
             }
         }
     }
 
     public IRubyObject status() {
         if (threadImpl.isAlive()) {
         	if (isStopped) {
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
 
     public IRubyObject kill() {
     	// need to reexamine this
         RubyThread currentThread = getRuntime().getCurrentContext().getThread();
         
         try {
-            this.checkLocks = true;
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " trying to kill");
             while (!(currentThread.lock.tryLock() && this.lock.tryLock())) {
                 if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             }
 
             currentThread.pollThreadEvents();
 
             if (DEBUG) System.out.println("thread " + Thread.currentThread() + " succeeded with kill");
             killed = true;
 
             threadImpl.interrupt(); // break out of wait states and blocking IO
         } finally {
             if (currentThread.lock.isHeldByCurrentThread()) currentThread.lock.unlock();
             if (this.lock.isHeldByCurrentThread()) this.lock.unlock();
-            this.checkLocks = false;
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
     
     public IRubyObject exit() {
     	return kill();
     }
 
     private boolean isCurrent() {
         return threadImpl.isCurrent();
     }
 
     public void exceptionRaised(RaiseException exception) {
         assert isCurrent();
 
         Ruby runtime = exception.getException().getRuntime();
         if (abortOnException(runtime)) {
             // FIXME: printError explodes on some nullpointer
             //getRuntime().getRuntime().printError(exception.getException());
         	// TODO: Doesn't SystemExit have its own method to make this less wordy..
             RubyException re = RubyException.newException(getRuntime(), getRuntime().getClass("SystemExit"), exception.getMessage());
             re.setInstanceVariable("status", getRuntime().newFixnum(1));
             threadService.getMainThread().raise(new IRubyObject[]{re}, Block.NULL_BLOCK);
         } else {
             exitingException = exception;
         }
     }
 
     private boolean abortOnException(Ruby runtime) {
         return (runtime.isGlobalAbortOnExceptionEnabled() || abortOnException);
     }
 
     public static RubyThread mainThread(IRubyObject receiver) {
         return receiver.getRuntime().getThreadService().getMainThread();
     }
 }
diff --git a/src/org/jruby/internal/runtime/ThreadService.java b/src/org/jruby/internal/runtime/ThreadService.java
index a34be08772..6ed9482cf0 100644
--- a/src/org/jruby/internal/runtime/ThreadService.java
+++ b/src/org/jruby/internal/runtime/ThreadService.java
@@ -1,184 +1,180 @@
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
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
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
 package org.jruby.internal.runtime;
 
-import edu.emory.mathcs.backport.java.util.concurrent.locks.Lock;
 import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
-import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantReadWriteLock;
-import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
-import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
 import java.lang.ref.WeakReference;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.jruby.Ruby;
 import org.jruby.RubyThread;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.util.collections.WeakHashSet;
 
 public class ThreadService {
     private Ruby runtime;
     private ThreadContext mainContext;
     private ThreadLocal localContext;
     private ThreadGroup rubyThreadGroup;
     private Set rubyThreadList;
     private Thread mainThread;
     
     private ReentrantLock criticalLock = new ReentrantLock();
 
     public ThreadService(Ruby runtime) {
         this.runtime = runtime;
         this.mainContext = ThreadContext.newContext(runtime);
         this.localContext = new ThreadLocal();
         this.rubyThreadGroup = new ThreadGroup("Ruby Threads#" + runtime.hashCode());
         this.rubyThreadList = Collections.synchronizedSet(new WeakHashSet());
         
         // Must be called from main thread (it is currently, but this bothers me)
         mainThread = Thread.currentThread();
         localContext.set(new WeakReference(mainContext));
         rubyThreadList.add(mainThread);
     }
 
     public void disposeCurrentThread() {
         localContext.set(null);
     }
 
     public ThreadContext getCurrentContext() {
         WeakReference wr = (WeakReference) localContext.get();
         
         if (wr == null) {
             wr = adoptCurrentThread();
         } else if(wr.get() == null) {
             wr = adoptCurrentThread();
         }
 
         return (ThreadContext)wr.get();
     }
     
     private WeakReference adoptCurrentThread() {
         Thread current = Thread.currentThread();
         
         RubyThread.adopt(runtime.getClass("Thread"), current);
         
         return (WeakReference) localContext.get();
     }
 
     public RubyThread getMainThread() {
         return mainContext.getThread();
     }
 
     public void setMainThread(RubyThread thread) {
         mainContext.setThread(thread);
     }
     
     public synchronized RubyThread[] getActiveRubyThreads() {
     	// all threads in ruby thread group plus main thread
 
         synchronized(rubyThreadList) {
             List rtList = new ArrayList(rubyThreadList.size());
         
             for (Iterator iter = rubyThreadList.iterator(); iter.hasNext();) {
                 Thread t = (Thread)iter.next();
             
                 if (!t.isAlive()) continue;
             
                 RubyThread rt = getRubyThreadFromThread(t);
                 rtList.add(rt);
             }
         
             RubyThread[] rubyThreads = new RubyThread[rtList.size()];
             rtList.toArray(rubyThreads);
     	
             return rubyThreads;
         }
     }
     
     public ThreadGroup getRubyThreadGroup() {
     	return rubyThreadGroup;
     }
 
     public synchronized void registerNewThread(RubyThread thread) {
         localContext.set(new WeakReference(ThreadContext.newContext(runtime)));
         getCurrentContext().setThread(thread);
         // This requires register to be called from within the registree thread
         rubyThreadList.add(Thread.currentThread());
     }
     
     public synchronized void unregisterThread(RubyThread thread) {
         rubyThreadList.remove(Thread.currentThread());
         getCurrentContext().setThread(null);
         localContext.set(null);
     }
     
     private RubyThread getRubyThreadFromThread(Thread activeThread) {
         RubyThread rubyThread;
         if (activeThread instanceof RubyNativeThread) {
             RubyNativeThread rubyNativeThread = (RubyNativeThread)activeThread;
             rubyThread = rubyNativeThread.getRubyThread();
         } else {
             // main thread
             rubyThread = mainContext.getThread();
         }
         return rubyThread;
     }
     
     public synchronized void setCritical(boolean critical) {
         if (criticalLock.isHeldByCurrentThread()) {
             if (critical) {
                 // do nothing
             } else {
                 criticalLock.unlock();
             }
         } else {
             if (critical) {
                 criticalLock.lock();
             } else {
                 // do nothing
             }
         }
     }
     
     public synchronized boolean getCritical() {
         return criticalLock.isHeldByCurrentThread();
     }
     
     public void waitForCritical() {
         if (criticalLock.isLocked()) {
             criticalLock.lock();
             criticalLock.unlock();
         }
     }
 
 }
diff --git a/src/org/jruby/runtime/Frame.java b/src/org/jruby/runtime/Frame.java
index aa4b4a92fe..477ea6ea67 100644
--- a/src/org/jruby/runtime/Frame.java
+++ b/src/org/jruby/runtime/Frame.java
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.RubyModule;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * <p>Frame for a full (read: not 'fast') Ruby method invocation.  Any Ruby method which calls 
  * another Ruby method (or yields to a block) will get a Frame.  A fast method by contrast does 
  * not get a Frame because we know that we will not be calling/yielding.</p>  
  * 
  * A Frame is also needed for a few special cases:
  * <ul>
  * <li>Proc.new must check previous frame to get the block it is getting constructed for
  * <li>block_given? must check the previous frame to see if a block is active
  * </li>
  * 
  */
 public class Frame {
     /**
      * The class for the method we are invoking for this frame.  Note: This may not be the
      * class where the implementation of the method lives.
      */
     private RubyModule klazz;
     
     /**
      * The 'self' for this frame.
      */
     private IRubyObject self;
     
     /**
      * The name of the method being invoked in this frame.  Note: Blocks are backed by frames
      * and do not have a name.
      */
     private String name;
     
     /**
      * The arguments passed into the method of this frame.   The frame captures arguments
      * so that they can be reused for things like super/zsuper.
      */
     private IRubyObject[] args;
 
     private int requiredArgCount;
 
     /**
      * The block that was passed in for this frame (as either a block or a &amp;block argument).
      * The frame captures the block for super/zsuper, but also for Proc.new (with no arguments)
      * and also for block_given?.  Both of those methods needs access to the block of the 
      * previous frame to work.
      */ 
     private Block block;
     
     /**
      * Does this delimit a frame where an eval with binding occurred.  Used for stack traces.
      */
     private boolean isBindingFrame = false;
 
     /**
      * The current visibility for anything defined under this frame
      */
     private Visibility visibility = Visibility.PUBLIC;
     
     private Object jumpTarget;
 
     public Object getJumpTarget() {
         return jumpTarget;
     }
 
     public void setJumpTarget(Object jumpTarget) {
         this.jumpTarget = jumpTarget;
     }
 
     /**
      * The location in source where this block/method invocation is happening
      */
     private final ISourcePosition position;
 
     public Frame(ISourcePosition position) {
         this(null, null, null, IRubyObject.NULL_ARRAY, 0, Block.NULL_BLOCK, position, null); 
     }
 
     public Frame(RubyModule klazz, IRubyObject self, String name,
                  IRubyObject[] args, int requiredArgCount, Block block, ISourcePosition position, Object jumpTarget) {
         assert block != null : "Block uses null object pattern.  It should NEVER be null";
         
         this.self = self;
         this.args = args;
         this.requiredArgCount = requiredArgCount;
         this.name = name;
         this.klazz = klazz;
         this.position = position;
         this.block = block;
         this.jumpTarget = jumpTarget;
     }
 
     /** Getter for property args.
      * @return Value of property args.
      */
     IRubyObject[] getArgs() {
         return args;
     }
 
     /** Setter for property args.
      * @param args New value of property args.
      */
     void setArgs(IRubyObject[] args) {
         this.args = args;
     }
 
     public int getRequiredArgCount() {
         return requiredArgCount;
     }
 
     /**
      * @return the frames current position
      */
     ISourcePosition getPosition() {
         return position;
     }
 
     /** 
      * Return class that we are supposedly calling for this invocation
      * 
      * @return the current class
      */
-    RubyModule getKlazz() {
+    public RubyModule getKlazz() {
         return klazz;
     }
 
     /**
      * Set class that this method is supposedly calling on.  Note: This is different than
      * a native method's implementation class.
      * 
      * @param klazz the new class
      */
     public void setKlazz(RubyModule klazz) {
         this.klazz = klazz;
     }
 
     /**
      * Set the method name associated with this frame
      * 
      * @param name the new name
      */
     public void setName(String name) {
         this.name = name;
     }
 
     /** 
      * Get the method name associated with this frame
      * 
      * @return the method name
      */
     String getName() {
         return name;
     }
 
     /**
      * Get the self associated with this frame
      * 
      * @return the self
      */
     IRubyObject getSelf() {
         return self;
     }
 
     /** 
      * Set the self associated with this frame
      * 
      * @param self is the new value of self
      */
     void setSelf(IRubyObject self) {
         this.self = self;
     }
     
     /**
      * Get the visibility at the time of this frame
      * 
      * @return the visibility
      */
     public Visibility getVisibility() {
         return visibility;
     }
     
     /**
      * Change the visibility associated with this frame
      * 
      * @param visibility the new visibility
      */
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
     
     /**
      * Is this frame the frame which started a binding eval?
      * 
      * @return true if it is a binding frame
      */
     public boolean isBindingFrame() {
         return isBindingFrame;
     }
     
     /**
      * Set whether this is a binding frame or not
      * 
      * @param isBindingFrame true if it is
      */
     public void setIsBindingFrame(boolean isBindingFrame) {
         this.isBindingFrame = isBindingFrame;
     }
     
     /**
      * What block is associated with this frame?
      * 
      * @return the block of this frame or NULL_BLOCK if no block given
      */
     public Block getBlock() {
         return block;
     }
 
     public Frame duplicate() {
         IRubyObject[] newArgs;
         if (args.length != 0) {
             newArgs = new IRubyObject[args.length];
             System.arraycopy(args, 0, newArgs, 0, args.length);
         } else {
         	newArgs = args;
         }
 
         return new Frame(klazz, self, name, newArgs, requiredArgCount, block, position, jumpTarget);
     }
 
     /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
     public String toString() {
         StringBuffer sb = new StringBuffer(50);
         sb.append(position != null ? position.toString() : "-1");
         sb.append(':');
         sb.append(klazz + " " + name);
         if (name != null) {
             sb.append("in ");
             sb.append(name);
         }
         return sb.toString();
     }
 }
diff --git a/src/org/jruby/runtime/ObjectSpace.java b/src/org/jruby/runtime/ObjectSpace.java
index af12e770f6..6d88d2b0d8 100644
--- a/src/org/jruby/runtime/ObjectSpace.java
+++ b/src/org/jruby/runtime/ObjectSpace.java
@@ -1,203 +1,198 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
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
 
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.WeakIdentityHashMap;
 
 import java.lang.ref.ReferenceQueue;
 import java.lang.ref.WeakReference;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
-import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
-import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
-import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
-import org.jruby.RubyNumeric;
-
 /**
  * FIXME: This version is faster than the previous, but both suffer from a
  * crucial flaw: It is impossible to create an ObjectSpace with an iterator
  * that doesn't either: a. hold on to objects that might otherwise be collected
  * or b. have no way to guarantee that a call to hasNext() will be correct or
  * that a subsequent call to next() will produce an object. For our purposes,
  * for now, this may be acceptable.
  */
 public class ObjectSpace {
     private ReferenceQueue deadReferences = new ReferenceQueue();
     private WeakReferenceListNode top;
 
     private ReferenceQueue deadIdentityReferences = new ReferenceQueue();
     private final Map identities = new HashMap();
     private final Map identitiesByObject = new WeakIdentityHashMap();
 
     private long maxId = 4; // Highest reserved id
 
     public long idOf(IRubyObject rubyObject) {
         synchronized (identities) {
             Long longId = (Long) identitiesByObject.get(rubyObject);
             if (longId == null) {
                 longId = createId(rubyObject);
             }
             return longId.longValue();
         }
     }
 
     private Long createId(IRubyObject object) {
         cleanIdentities();
         maxId += 2; // id must always be even
         Long longMaxId = new Long(maxId);
         identities.put(longMaxId, new IdReference(object, maxId, deadIdentityReferences));
         identitiesByObject.put(object, longMaxId);
         return longMaxId;
     }
 
     public IRubyObject id2ref(long id) {
         synchronized (identities) {
             cleanIdentities();
             IdReference reference = (IdReference) identities.get(new Long(id));
             if (reference == null)
                 return null;
             return (IRubyObject) reference.get();
         }
     }
 
     private void cleanIdentities() {
         IdReference ref;
         while ((ref = (IdReference) deadIdentityReferences.poll()) != null)
             identities.remove(new Long(ref.id()));
     }
     
     public void addFinalizer(IRubyObject object, RubyProc proc) {
         object.addFinalizer(proc);
     }
     
     public void removeFinalizers(long id) {
         IRubyObject object = id2ref(id);
         if (object != null) {
             object.removeFinalizers();
         }
     }
     
     public synchronized void add(IRubyObject object) {
         cleanup();
         top = new WeakReferenceListNode(object, deadReferences, top);
     }
 
     public synchronized Iterator iterator(RubyModule rubyClass) {
         final List objList = new ArrayList();
         WeakReferenceListNode current = top;
         while (current != null) {
             IRubyObject obj = (IRubyObject)current.get();
             if (obj != null && obj.isKindOf(rubyClass)) {
                 objList.add(current);
             }
 
             current = current.nextNode;
         }
 
         return new Iterator() {
             private Iterator iter = objList.iterator();
 
             public boolean hasNext() {
                 throw new UnsupportedOperationException();
             }
 
             public Object next() {
                 Object obj = null;
                 while (iter.hasNext()) {
                     WeakReferenceListNode node = (WeakReferenceListNode)iter.next();
 
                     obj = node.get();
 
                     if (obj != null) break;
                 }
                 return obj;
             }
 
             public void remove() {
                 throw new UnsupportedOperationException();
             }
         };
     }
 
     private synchronized void cleanup() {
         WeakReferenceListNode reference;
         while ((reference = (WeakReferenceListNode)deadReferences.poll()) != null) {
             reference.remove();
         }
     }
 
     private class WeakReferenceListNode extends WeakReference {
         private WeakReferenceListNode prevNode;
         private WeakReferenceListNode nextNode;
 
         public WeakReferenceListNode(Object ref, ReferenceQueue queue, WeakReferenceListNode next) {
             super(ref, queue);
 
             this.nextNode = next;
             if (next != null) {
                 next.prevNode = this;
             }
         }
 
         public void remove() {
             synchronized (ObjectSpace.this) {
                 if (prevNode != null) {
                     prevNode.nextNode = nextNode;
                 } else {
                     top = nextNode;
                 }
                 if (nextNode != null) {
                     nextNode.prevNode = prevNode;
                 }
             }
         }
     }
 
     private static class IdReference extends WeakReference {
         private final long id;
 
         public IdReference(IRubyObject object, long id, ReferenceQueue queue) {
             super(object, queue);
             this.id = id;
         }
 
         public long id() {
             return id;
         }
     }
 }
diff --git a/src/org/jvyamlb/ResolverImpl.java b/src/org/jvyamlb/ResolverImpl.java
index d6b12adc61..9bd096d4f0 100644
--- a/src/org/jvyamlb/ResolverImpl.java
+++ b/src/org/jvyamlb/ResolverImpl.java
@@ -1,238 +1,236 @@
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
 package org.jvyamlb;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.LinkedList;
 import java.util.HashMap;
 import java.util.Map;
-import java.util.HashSet;
-import java.util.Set;
 
 import org.jvyamlb.nodes.MappingNode;
 import org.jvyamlb.nodes.Node;
 import org.jvyamlb.nodes.ScalarNode;
 import org.jvyamlb.nodes.SequenceNode;
 
 import org.jruby.util.ByteList;
 
 /**
  * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
  */
 public class ResolverImpl implements Resolver {
     private final static Map yamlPathResolvers = new HashMap();
 
     private final static ResolverScanner SCANNER = new ResolverScanner();
 
     private List resolverExactPaths = new LinkedList();
     private List resolverPrefixPaths = new LinkedList();
 
     public static void addPathResolver(final String tag, final List path, final Class kind) {
         final List newPath = new LinkedList();
         Object nodeCheck=null;
         Object indexCheck=null;
         for(final Iterator iter = path.iterator();iter.hasNext();) {
             final Object element = iter.next();
             if(element instanceof List) {
                 final List eList = (List)element;
                 if(eList.size() == 2) {
                     nodeCheck = eList.get(0);
                     indexCheck = eList.get(1);
                 } else if(eList.size() == 1) {
                     nodeCheck = eList.get(0);
                     indexCheck = Boolean.TRUE;
                 } else {
                     throw new ResolverException("Invalid path element: " + element);
                 }
             } else {
                 nodeCheck = null;
                 indexCheck = element;
             }
 
             if(nodeCheck instanceof String || nodeCheck instanceof ByteList) {
                 nodeCheck = ScalarNode.class;
             } else if(nodeCheck instanceof List) {
                 nodeCheck = SequenceNode.class;
             } else if(nodeCheck instanceof Map) {
                 nodeCheck = MappingNode.class;
             } else if(null != nodeCheck && !ScalarNode.class.equals(nodeCheck) && !SequenceNode.class.equals(nodeCheck) && !MappingNode.class.equals(nodeCheck)) {
                 throw new ResolverException("Invalid node checker: " + nodeCheck);
             }
             if(!(indexCheck instanceof String || nodeCheck instanceof ByteList || indexCheck instanceof Integer) && null != indexCheck) {
                 throw new ResolverException("Invalid index checker: " + indexCheck);
             }
             newPath.add(new Object[]{nodeCheck,indexCheck});
         }
         Class newKind = null;
         if(String.class.equals(kind) || ByteList.class.equals(kind)) {
             newKind = ScalarNode.class;
         } else if(List.class.equals(kind)) {
             newKind = SequenceNode.class;
         } else if(Map.class.equals(kind)) {
             newKind = MappingNode.class;
         } else if(kind != null && !ScalarNode.class.equals(kind) && !SequenceNode.class.equals(kind) && !MappingNode.class.equals(kind)) {
             throw new ResolverException("Invalid node kind: " + kind);
         } else {
             newKind = kind;
         }
         final List x = new ArrayList(1);
         x.add(newPath);
         final List y = new ArrayList(2);
         y.add(x);
         y.add(kind);
         yamlPathResolvers.put(y,tag);
     }
 
     public void descendResolver(final Node currentNode, final Object currentIndex) {
         final Map exactPaths = new HashMap();
         final List prefixPaths = new LinkedList();
         if(null != currentNode) {
             final int depth = resolverPrefixPaths.size();
             for(final Iterator iter = ((List)resolverPrefixPaths.get(0)).iterator();iter.hasNext();) {
                 final Object[] obj = (Object[])iter.next();
                 final List path = (List)obj[0];
                 if(checkResolverPrefix(depth,path,(Class)obj[1],currentNode,currentIndex)) {
                     if(path.size() > depth) {
                         prefixPaths.add(new Object[] {path,obj[1]});
                     } else {
                         final List resPath = new ArrayList(2);
                         resPath.add(path);
                         resPath.add(obj[1]);
                         exactPaths.put(obj[1],yamlPathResolvers.get(resPath));
                     }
                 }
             }
         } else {
             for(final Iterator iter = yamlPathResolvers.keySet().iterator();iter.hasNext();) {
                 final List key = (List)iter.next();
                 final List path = (List)key.get(0);
                 final Class kind = (Class)key.get(1);
                 if(null == path) {
                     exactPaths.put(kind,yamlPathResolvers.get(key));
                 } else {
                     prefixPaths.add(key);
                 }
             }
         }
         resolverExactPaths.add(0,exactPaths);
         resolverPrefixPaths.add(0,prefixPaths);
     }
 
     public void ascendResolver() {
         resolverExactPaths.remove(0);
         resolverPrefixPaths.remove(0);
     }
 
     public boolean checkResolverPrefix(final int depth, final List path, final Class kind, final Node currentNode, final Object currentIndex) {
         final Object[] check = (Object[])path.get(depth-1);
         final Object nodeCheck = check[0];
         final Object indexCheck = check[1];
         if(nodeCheck instanceof String) {
             if(!currentNode.getTag().equals(nodeCheck)) {
                 return false;
             }
         } else if(null != nodeCheck) {
             if(!((Class)nodeCheck).isInstance(currentNode)) {
                 return false;
             }
         }
         if(indexCheck == Boolean.TRUE && currentIndex != null) {
             return false;
         }
         if(indexCheck == Boolean.FALSE && currentIndex == null) {
             return false;
         }
         if(indexCheck instanceof String) {
             if(!(currentIndex instanceof ScalarNode && indexCheck.equals(((ScalarNode)currentIndex).getValue()))) {
                 return false;
             }
         } else if(indexCheck instanceof ByteList) {
             if(!(currentIndex instanceof ScalarNode && indexCheck.equals(((ScalarNode)currentIndex).getValue()))) {
                 return false;
             }
         } else if(indexCheck instanceof Integer) {
             if(!currentIndex.equals(indexCheck)) {
                 return false;
             }
         }
         return true;
     }
     
     public String resolve(final Class kind, final ByteList value, final boolean[] implicit) {
         List resolvers = null;
         if(kind.equals(ScalarNode.class) && implicit[0]) {
             String resolv = SCANNER.recognize(value);
             if(resolv != null) {
                 return resolv;
             }
         }
         final Map exactPaths = (Map)resolverExactPaths.get(0);
         if(exactPaths.containsKey(kind)) {
             return (String)exactPaths.get(kind);
         }
         if(exactPaths.containsKey(null)) {
             return (String)exactPaths.get(null);
         }
         if(kind.equals(ScalarNode.class)) {
             return YAML.DEFAULT_SCALAR_TAG;
         } else if(kind.equals(SequenceNode.class)) {
             return YAML.DEFAULT_SEQUENCE_TAG;
         } else if(kind.equals(MappingNode.class)) {
             return YAML.DEFAULT_MAPPING_TAG;
         }
         return null;
     } 
     
     private static ByteList s(String se){
         return new ByteList(se.getBytes());
     }
 
     public static void main(String[] args) {
         ByteList[] strings = {s("yes"), s("NO"), s("booooooooooooooooooooooooooooooooooooooooooooooool"), s("false"),s(""), s("~"),s("~a"),
                             s("<<"), s("10.1"), s("10000000000003435345.2324E+13"), s(".23"), s(".nan"), s("null"), s("124233333333333333"),
                             s("0b030323"), s("+0b0111111011010101"), s("0xaafffdf"), s("2005-05-03"), s("2005-05-03a"), s(".nana"),
                             s("2005-03-05T05:23:22"), s("="), s("= "), s("=a")};
         boolean[] implicit = new boolean[]{true,true};
         Resolver res = new ResolverImpl();
         res.descendResolver(null,null);
         Class s = ScalarNode.class;
         final long before = System.currentTimeMillis();
         final int NUM = 100000;
         for(int j=0;j<NUM;j++) {
             for(int i=0;i<strings.length;i++) {
                 res.resolve(s,strings[i%(strings.length)],implicit);
             }
         }
         final long after = System.currentTimeMillis();
         final long time = after-before;
         final double timeS = (after-before)/1000.0;
         System.out.println("Resolving " + NUM*strings.length + " nodes took " + time + "ms, or " + timeS + " seconds"); 
     }
 }// ResolverImpl
diff --git a/test/testEval.rb b/test/testEval.rb
index ed574822e2..4b2af79b4a 100644
--- a/test/testEval.rb
+++ b/test/testEval.rb
@@ -1,164 +1,178 @@
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
 test_equal(1, foo2)
 
 $a = 1
 eval 'BEGIN { $a = 2 }'
 test_equal(1, $a)
+
+$b = nil
+class Foo
+  $b = binding
+end
+
+a = Object.new
+main = self
+b = binding
+a.instance_eval { 
+  eval("test_equal(a, self)") 
+  eval("test_equal(main,self)", b)
+  eval("test_equal(Foo, self)", $b)
+}
diff --git a/test/testStruct.rb b/test/testStruct.rb
index 15685c88d7..7f9253e2e6 100644
--- a/test/testStruct.rb
+++ b/test/testStruct.rb
@@ -1,66 +1,87 @@
 require 'test/minirunit'
 test_check "Test Struct"
 
 s = Struct.new("MyStruct", :x, :y)
 
 test_equal(Struct::MyStruct, s)
 
 s1 = s.new(11, 22)
 test_equal(11, s1["x"])
 test_equal(22, s1["y"])
 
 s1 = s.new
 test_equal(nil, s1.x)
 test_equal(nil, s1.y)
 s1.x = 10
 s1.y = 20
 test_equal(10, s1.x)
 test_equal(20, s1.y)
 test_equal(10, s1[0])
 test_equal(20, s1[1])
 test_equal(20, s1[-1])
 
 test_equal(2, s1.length)
 test_equal(2, s1.size)
 
 test_equal([10, 20], s1.values)
 
 test_exception(NameError) { s1["froboz"] }
 test_exception(IndexError) { s1[10] }
 
 s2 = s.new(1, 2)
 test_equal(1, s2.x)
 test_equal(2, s2.y)
 
 test_exception(ArgumentError) { s.new(1,2,3,4,5,6,7) }
 
 # Anonymous Struct
 a = Struct.new(:x, :y)
 a1 = a.new(5, 7)
 test_equal(5, a1.x)
 
 # Struct::Tms
 tms = Struct::Tms.new(0, 0, 0, 0)
 test_ok(tms != nil)
 
 # Struct creation with a block
 a = Struct.new(:foo, :bar) {
   def hello
     "hello"
   end
 }
 
 test_equal("hello", a.new(0, 0).hello)
 
 # Redefining a named struct should produce a warning, but it should be a new class
 P1 = Struct.new("Post", :foo)
 P1.class_eval do
   def bar
     true
   end
 end
 P2 = Struct.new("Post", :foo)
 
 test_exception {
   P2.new.bar
-}
\ No newline at end of file
+}
+
+MyStruct = Struct.new("MyStruct", :a, :b)
+class MySubStruct < MyStruct
+  def initialize(v, *args) super(*args); @v = v; end 
+end
+
+b = MySubStruct.new(1, 2)
+inspect1 = b.inspect
+b.instance_eval {"EH"}
+# Instance_eval creates a metaclass and our inspect should not print that new metaclass out
+test_equal(inspect1, b.inspect)
+c = MySubStruct.new(1, 2)
+
+class << b
+  def foo
+  end
+end
+
+# Even though they have different metaclasses they are still equal in the eyes of Ruby
+test_equal(b, c)
+
diff --git a/test/test_unit_index b/test/test_unit_index
index 2bfeceec3c..43d8fa7b07 100644
--- a/test/test_unit_index
+++ b/test/test_unit_index
@@ -1,145 +1,145 @@
 #openssl/test_asn1
 #openssl/test_cipher
 #openssl/test_digest
 #openssl/test_hmac
 #openssl/test_ns_spki
 #openssl/test_pair
 #openssl/test_pkey_rsa
 #openssl/test_ssl
 #openssl/test_x509cert
 #openssl/test_x509crl
 #openssl/test_x509ext
 #openssl/test_x509name
 #openssl/test_x509req
 #openssl/test_x509store
 
 # Rubicon tests
 rubicon/test_access_control
 rubicon/test_array
 rubicon/test_assignment
 rubicon/test_basic_expressions
 rubicon/test_bignum
 rubicon/test_blocks_procs
 rubicon/test_boolean_expressions
 rubicon/test_case
 rubicon/test_catch_throw
 rubicon/test_class
 rubicon/test_comparable
 rubicon/test_constants
 rubicon/test_enumerable
 rubicon/test_eval
 rubicon/test_exception
 rubicon/test_exceptions
 rubicon/test_false_class
 rubicon/test_float
 rubicon/test_floats
 rubicon/test_hash
 rubicon/test_if_unless
 rubicon/test_integer
 rubicon/test_marshal
 rubicon/test_match_data
 rubicon/test_math
 rubicon/test_method
 rubicon/test_module
 rubicon/test_module_private
 rubicon/test_nil_class
 rubicon/test_numeric
 rubicon/test_object
 rubicon/test_object_space
 rubicon/test_pack
 rubicon/test_proc
 rubicon/test_regexp
 rubicon/test_string
 rubicon/test_struct
 rubicon/test_symbol
 rubicon/test_thread_group
 rubicon/test_true_class
 
 # Our own test/unit-based tests
 test_array
 test_backquote
 test_block
 test_case
 test_comparable
 test_core_arities
 #test_digest2
 test_dup_clone_taint_freeze
 test_env
 test_globals
 test_hash
 test_higher_javasupport
 test_java_accessible_object
 test_java_extension
 #test_openssl
 test_pack
 test_primitive_to_java
 test_parsing
 test_string_printf
 test_symbol
 test_thread
 test_trace_func
 test_zlib
 test_yaml
 
 # BFTS working tests
 externals/bfts/test_array
 externals/bfts/test_comparable
 externals/bfts/test_exception
 externals/bfts/test_false_class
 #externals/bfts/test_file_test
 externals/bfts/test_hash
 externals/bfts/test_nil_class
 externals/bfts/test_range
 #externals/bfts/test_string
 #externals/bfts/test_struct
 #externals/bfts/test_time
 externals/bfts/test_true_class
 
 # MRI's tests
 externals/mri/ruby/test_alias
 externals/mri/ruby/test_array
 externals/mri/ruby/test_assignment
 #externals/mri/ruby/test_beginendblock
 externals/mri/ruby/test_bignum
 externals/mri/ruby/test_call
 externals/mri/ruby/test_case
 externals/mri/ruby/test_clone
 externals/mri/ruby/test_condition
 externals/mri/ruby/test_const
 externals/mri/ruby/test_defined
 externals/mri/ruby/test_dir
 externals/mri/ruby/test_env
 externals/mri/ruby/test_eval
 externals/mri/ruby/test_exception
 #externals/mri/ruby/test_file
 externals/mri/ruby/test_float
 externals/mri/ruby/test_gc
 externals/mri/ruby/test_hash
 externals/mri/ruby/test_ifunless
 externals/mri/ruby/test_io
 #externals/mri/ruby/test_iterator
-#externals/mri/ruby/test_marshal
+externals/mri/ruby/test_marshal
 externals/mri/ruby/test_math
 externals/mri/ruby/test_method
 #externals/mri/ruby/test_objectspace
 #externals/mri/ruby/test_pack
 #externals/mri/ruby/test_path
 externals/mri/ruby/test_pipe
 externals/mri/ruby/test_proc
 #externals/mri/ruby/test_process
 #externals/mri/ruby/test_rand
 externals/mri/ruby/test_range
 #externals/mri/ruby/test_readpartial
 #externals/mri/ruby/test_settracefunc
 #externals/mri/ruby/test_signal
 externals/mri/ruby/test_string
 externals/mri/ruby/test_stringchar
 externals/mri/ruby/test_struct
 externals/mri/ruby/test_super
 externals/mri/ruby/test_symbol
 #externals/mri/ruby/test_system
 externals/mri/ruby/test_time
 #externals/mri/ruby/test_trace
 # For some reason test_variable only fails during ant test
 #externals/mri/ruby/test_variable
 externals/mri/ruby/test_whileuntil
