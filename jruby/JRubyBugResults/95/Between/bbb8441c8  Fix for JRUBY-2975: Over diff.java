diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index ecf381ba44..f5476295f3 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1402,1596 +1402,1610 @@ public class RubyObject implements Cloneable, IRubyObject, Serializable, CoreObj
      *  
      *  Marks <i>obj</i> as tainted---if the <code>$SAFE</code> level is
      *  set appropriately, many method calls which might alter the running
      *  programs environment will refuse to accept tainted strings.
      */
     @JRubyMethod(name = "taint")
     public IRubyObject taint(ThreadContext context) {
         taint(context.getRuntime());
         return this;
     }
     
     private void taint(Ruby runtime) {
         runtime.secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
     }
 
     /** rb_obj_untaint
      *
      *  call-seq:
      *     obj.untaint    => obj
      *  
      *  Removes the taint from <i>obj</i>.
      * 
      *  Only callable in if more secure than 3.
      */
     @JRubyMethod(name = "untaint")
     public IRubyObject untaint(ThreadContext context) {
         context.getRuntime().secure(3);
         
         if (isTaint()) {
             testFrozen("object");
             setTaint(false);
         }
         
         return this;
     }
 
     /** rb_obj_freeze
      *
      *  call-seq:
      *     obj.freeze    => obj
      *  
      *  Prevents further modifications to <i>obj</i>. A
      *  <code>TypeError</code> will be raised if modification is attempted.
      *  There is no way to unfreeze a frozen object. See also
      *  <code>Object#frozen?</code>.
      *     
      *     a = [ "a", "b", "c" ]
      *     a.freeze
      *     a << "z"
      *     
      *  <em>produces:</em>
      *     
      *     prog.rb:3:in `<<': can't modify frozen array (TypeError)
      *     	from prog.rb:3
      */
     @JRubyMethod(name = "freeze")
     public IRubyObject freeze(ThreadContext context) {
         if ((flags & FROZEN_F) == 0) {
             if (context.getRuntime().getSafeLevel() >= 4 && isTaint()) {
                 throw context.getRuntime().newSecurityError("Insecure: can't freeze object");
             }
             flags |= FROZEN_F;
         }
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      *  call-seq:
      *     obj.frozen?    => true or false
      *  
      *  Returns the freeze status of <i>obj</i>.
      *     
      *     a = [ "a", "b", "c" ]
      *     a.freeze    #=> ["a", "b", "c"]
      *     a.frozen?   #=> true
      */    
     @JRubyMethod(name = "frozen?")
     public RubyBoolean frozen_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isFrozen());
     }
 
     /** inspect_obj
      * 
      * The internal helper method that takes care of the part of the
      * inspection that inspects instance variables.
      */
     private StringBuilder inspectObj(StringBuilder part) {
         ThreadContext context = getRuntime().getCurrentContext();
         String sep = "";
         
         for (Variable<IRubyObject> ivar : getInstanceVariableList()) {
             part.append(sep).append(" ").append(ivar.getName()).append("=");
             part.append(ivar.getValue().callMethod(context, "inspect"));
             sep = ",";
         }
         part.append(">");
         return part;
     }
 
     /** rb_inspect
      * 
      * The internal helper that ensures a RubyString instance is returned
      * so dangerous casting can be omitted
      * Prefered over callMethod(context, "inspect") 
      */
     static RubyString inspect(ThreadContext context, IRubyObject object) {
         return RubyString.objAsString(context, object.callMethod(context, "inspect"));
     }    
 
     /** rb_obj_inspect
      *
      *  call-seq:
      *     obj.inspect   => string
      *  
      *  Returns a string containing a human-readable representation of
      *  <i>obj</i>. If not overridden, uses the <code>to_s</code> method to
      *  generate the string.
      *     
      *     [ 1, 2, 3..4, 'five' ].inspect   #=> "[1, 2, 3..4, \"five\"]"
      *     Time.new.inspect                 #=> "Wed Apr 09 08:54:39 CDT 2003"
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         Ruby runtime = getRuntime();
         if ((!isImmediate()) && !(this instanceof RubyModule) && hasVariables()) {
             StringBuilder part = new StringBuilder();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
 
             if (runtime.isInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return runtime.newString(part.toString());
             }
             try {
                 runtime.registerInspecting(this);
                 return runtime.newString(inspectObj(part).toString());
             } finally {
                 runtime.unregisterInspecting(this);
             }
         }
 
         if (isNil()) return RubyNil.inspect(this);
         return RuntimeHelpers.invoke(runtime.getCurrentContext(), this, "to_s");
     }
 
     /** rb_obj_is_instance_of
      *
      *  call-seq:
      *     obj.instance_of?(class)    => true or false
      *  
      *  Returns <code>true</code> if <i>obj</i> is an instance of the given
      *  class. See also <code>Object#kind_of?</code>.
      */
     @JRubyMethod(name = "instance_of?", required = 1)
     public RubyBoolean instance_of_p(ThreadContext context, IRubyObject type) {
         if (type() == type) {
             return context.getRuntime().getTrue();
         } else if (!(type instanceof RubyModule)) {
             throw context.getRuntime().newTypeError("class or module required");
         } else {
             return context.getRuntime().getFalse();
         }
     }
 
 
     /** rb_obj_is_kind_of
      *
      *  call-seq:
      *     obj.is_a?(class)       => true or false
      *     obj.kind_of?(class)    => true or false
      *  
      *  Returns <code>true</code> if <i>class</i> is the class of
      *  <i>obj</i>, or if <i>class</i> is one of the superclasses of
      *  <i>obj</i> or modules included in <i>obj</i>.
      *     
      *     module M;    end
      *     class A
      *       include M
      *     end
      *     class B < A; end
      *     class C < B; end
      *     b = B.new
      *     b.instance_of? A   #=> false
      *     b.instance_of? B   #=> true
      *     b.instance_of? C   #=> false
      *     b.instance_of? M   #=> false
      *     b.kind_of? A       #=> true
      *     b.kind_of? B       #=> true
      *     b.kind_of? C       #=> false
      *     b.kind_of? M       #=> true
      */
     @JRubyMethod(name = {"kind_of?", "is_a?"}, required = 1)
     public RubyBoolean kind_of_p(ThreadContext context, IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!(type instanceof RubyModule)) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw context.getRuntime().newTypeError("class or module required");
         }
 
         return context.getRuntime().newBoolean(((RubyModule)type).isInstance(this));
     }
 
     @JRubyMethod(name = "tap", compat = CompatVersion.RUBY1_9)
     public IRubyObject tap(ThreadContext context, Block block) {
         block.yield(context, this);
         return this;
     }
 
     /** rb_obj_methods
      *
      *  call-seq:
      *     obj.methods    => array
      *  
      *  Returns a list of the names of methods publicly accessible in
      *  <i>obj</i>. This will include all the methods accessible in
      *  <i>obj</i>'s ancestors.
      *     
      *     class Klass
      *       def kMethod()
      *       end
      *     end
      *     k = Klass.new
      *     k.methods[0..9]    #=> ["kMethod", "freeze", "nil?", "is_a?", 
      *                             "class", "instance_variable_set",
      *                              "methods", "extend", "__send__", "instance_eval"]
      *     k.methods.length   #=> 42
      */
     @JRubyMethod(name = "methods", optional = 1)
     public IRubyObject methods(ThreadContext context, IRubyObject[] args) {
         boolean all = true;
         if (args.length == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray singletonMethods = null;
         if (getMetaClass().isSingleton()) {
             singletonMethods =
                 getMetaClass().instance_methods(new IRubyObject[] {context.getRuntime().getFalse()});
             if (all) {
                 singletonMethods.concat(getMetaClass().getSuperClass().instance_methods(new IRubyObject[] {context.getRuntime().getTrue()}));
             }
         } else {
             if (all) {
                 singletonMethods = getMetaClass().instance_methods(new IRubyObject[] {context.getRuntime().getTrue()});
             } else {
                 singletonMethods = context.getRuntime().newEmptyArray();
             }
         }
         
         return singletonMethods;
     }
 
     /** rb_obj_public_methods
      *
      *  call-seq:
      *     obj.public_methods(all=true)   => array
      *  
      *  Returns the list of public methods accessible to <i>obj</i>. If
      *  the <i>all</i> parameter is set to <code>false</code>, only those methods
      *  in the receiver will be listed.
      */
     @JRubyMethod(name = "public_methods", optional = 1)
     public IRubyObject public_methods(ThreadContext context, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getRuntime().getTrue() };
         }
 
         return getMetaClass().public_instance_methods(args);
     }
 
     /** rb_obj_protected_methods
      *
      *  call-seq:
      *     obj.protected_methods(all=true)   => array
      *  
      *  Returns the list of protected methods accessible to <i>obj</i>. If
      *  the <i>all</i> parameter is set to <code>false</code>, only those methods
      *  in the receiver will be listed.
      *
      *  Internally this implementation uses the 
      *  {@link RubyModule#protected_instance_methods} method.
      */
     @JRubyMethod(name = "protected_methods", optional = 1)
     public IRubyObject protected_methods(ThreadContext context, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getRuntime().getTrue() };
         }
 
         return getMetaClass().protected_instance_methods(args);
     }
 
     /** rb_obj_private_methods
      *
      *  call-seq:
      *     obj.private_methods(all=true)   => array
      *  
      *  Returns the list of private methods accessible to <i>obj</i>. If
      *  the <i>all</i> parameter is set to <code>false</code>, only those methods
      *  in the receiver will be listed.
      *
      *  Internally this implementation uses the 
      *  {@link RubyModule#private_instance_methods} method.
      */
     @JRubyMethod(name = "private_methods", optional = 1)
     public IRubyObject private_methods(ThreadContext context, IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { context.getRuntime().getTrue() };
         }
 
         return getMetaClass().private_instance_methods(args);
     }
 
     /** rb_obj_singleton_methods
      *
      *  call-seq:
      *     obj.singleton_methods(all=true)    => array
      *  
      *  Returns an array of the names of singleton methods for <i>obj</i>.
      *  If the optional <i>all</i> parameter is true, the list will include
      *  methods in modules included in <i>obj</i>.
      *     
      *     module Other
      *       def three() end
      *     end
      *     
      *     class Single
      *       def Single.four() end
      *     end
      *     
      *     a = Single.new
      *     
      *     def a.one()
      *     end
      *     
      *     class << a
      *       include Other
      *       def two()
      *       end
      *     end
      *     
      *     Single.singleton_methods    #=> ["four"]
      *     a.singleton_methods(false)  #=> ["two", "one"]
      *     a.singleton_methods         #=> ["two", "one", "three"]
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     @JRubyMethod(name = "singleton_methods", optional = 1)
     public RubyArray singleton_methods(ThreadContext context, IRubyObject[] args) {
         boolean all = true;
         if(args.length == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray singletonMethods;
         if (getMetaClass().isSingleton()) {
             singletonMethods = getMetaClass().instance_methods(new IRubyObject[] {context.getRuntime().getFalse()});
             if (all) {
                 RubyClass superClass = getMetaClass().getSuperClass();
                 while (superClass.isIncluded()) {
                     singletonMethods.concat(superClass.instance_methods(new IRubyObject[] {context.getRuntime().getFalse()}));
                     superClass = superClass.getSuperClass();
                 }
             }
         } else {
             singletonMethods = context.getRuntime().newEmptyArray();
         }
         
         return singletonMethods;
     }
 
     /** rb_obj_method
      *
      *  call-seq:
      *     obj.method(sym)    => method
      *  
      *  Looks up the named method as a receiver in <i>obj</i>, returning a
      *  <code>Method</code> object (or raising <code>NameError</code>). The
      *  <code>Method</code> object acts as a closure in <i>obj</i>'s object
      *  instance, so instance variables and the value of <code>self</code>
      *  remain available.
      *     
      *     class Demo
      *       def initialize(n)
      *         @iv = n
      *       end
      *       def hello()
      *         "Hello, @iv = #{@iv}"
      *       end
      *     end
      *     
      *     k = Demo.new(99)
      *     m = k.method(:hello)
      *     m.call   #=> "Hello, @iv = 99"
      *     
      *     l = Demo.new('Fred')
      *     m = l.method("hello")
      *     m.call   #=> "Hello, @iv = Fred"
      */
     @JRubyMethod(name = "method", required = 1)
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asJavaString(), true);
     }
 
     /**
      * Internal method that helps to convert any object into the
      * format of a class name and a hex string inside of #<>.
      */
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     /** rb_any_to_s
      *
      *  call-seq:
      *     obj.to_s    => string
      *  
      *  Returns a string representing <i>obj</i>. The default
      *  <code>to_s</code> prints the object's class and an encoding of the
      *  object id. As a special case, the top-level object that is the
      *  initial execution context of Ruby programs returns ``main.''
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     /** rb_any_to_a
      *
      *  call-seq:
      *     obj.to_a -> anArray
      *  
      *  Returns an array representation of <i>obj</i>. For objects of class
      *  <code>Object</code> and others that don't explicitly override the
      *  method, the return value is an array containing <code>self</code>. 
      *  However, this latter behavior will soon be obsolete.
      *     
      *     self.to_a       #=> -:1: warning: default `to_a' will be obsolete
      *     "hello".to_a    #=> ["hello"]
      *     Time.new.to_a   #=> [39, 54, 8, 9, 4, 2003, 3, 99, true, "CDT"]
      * 
      *  The default to_a method is deprecated.
      */    
     @JRubyMethod(name = "to_a", visibility = Visibility.PUBLIC)
     public RubyArray to_a() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "default 'to_a' will be obsolete", "to_a");
         return getRuntime().newArray(this);
     }
 
     /** rb_obj_instance_eval
      *
      *  call-seq:
      *     obj.instance_eval(string [, filename [, lineno]] )   => obj
      *     obj.instance_eval {| | block }                       => obj
      *  
      *  Evaluates a string containing Ruby source code, or the given block,
      *  within the context of the receiver (_obj_). In order to set the
      *  context, the variable +self+ is set to _obj_ while
      *  the code is executing, giving the code access to _obj_'s
      *  instance variables. In the version of <code>instance_eval</code>
      *  that takes a +String+, the optional second and third
      *  parameters supply a filename and starting line number that are used
      *  when reporting compilation errors.
      *     
      *     class Klass
      *       def initialize
      *         @secret = 99
      *       end
      *     end
      *     k = Klass.new
      *     k.instance_eval { @secret }   #=> 99
      */
     @JRubyMethod(name = "instance_eval", frame = true)
     public IRubyObject instance_eval(ThreadContext context, Block block) {
         return specificEval(context, getInstanceEvalClass(), block);
     }
     @JRubyMethod(name = "instance_eval", frame = true)
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, block);
     }
     @JRubyMethod(name = "instance_eval", frame = true)
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, block);
     }
     @JRubyMethod(name = "instance_eval", frame = true)
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, arg2, block);
     }
     @Deprecated
     public IRubyObject instance_eval(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz;
         
         if (isImmediate()) {
             // Ruby uses Qnil here, we use "dummy" because we need a class
             klazz = context.getRuntime().getDummy();
         } else {
             klazz = getSingletonClass();
         }
         
         return specificEval(context, klazz, args, block);
     }
     
     private RubyModule getInstanceEvalClass() {
         if (isImmediate()) {
             // Ruby uses Qnil here, we use "dummy" because we need a class
             return getRuntime().getDummy();
         } else {
             return getSingletonClass();
         }
     }
 
     /** rb_obj_instance_exec
      *
      *  call-seq:
      *     obj.instance_exec(arg...) {|var...| block }                       => obj
      *
      *  Executes the given block within the context of the receiver
      *  (_obj_). In order to set the context, the variable +self+ is set
      *  to _obj_ while the code is executing, giving the code access to
      *  _obj_'s instance variables.  Arguments are passed as block parameters.
      *
      *     class Klass
      *       def initialize
      *         @secret = 99
      *       end
      *     end
      *     k = Klass.new
      *     k.instance_exec(5) {|x| @secret+x }   #=> 104
      */
     @JRubyMethod(name = "instance_exec", optional = 3, frame = true)
     public IRubyObject instance_exec(ThreadContext context, IRubyObject[] args, Block block) {
         if (!block.isGiven()) throw context.getRuntime().newArgumentError("block not supplied");
 
         RubyModule klazz;
         if (isImmediate()) {
             // Ruby uses Qnil here, we use "dummy" because we need a class
             klazz = context.getRuntime().getDummy();          
         } else {
             klazz = getSingletonClass();
         }
 
         return yieldUnder(context, klazz, args, block);
     }
 
     /** rb_obj_extend
      *
      *  call-seq:
      *     obj.extend(module, ...)    => obj
      *  
      *  Adds to _obj_ the instance methods from each module given as a
      *  parameter.
      *     
      *     module Mod
      *       def hello
      *         "Hello from Mod.\n"
      *       end
      *     end
      *     
      *     class Klass
      *       def hello
      *         "Hello from Klass.\n"
      *       end
      *     end
      *     
      *     k = Klass.new
      *     k.hello         #=> "Hello from Klass.\n"
      *     k.extend(Mod)   #=> #<Klass:0x401b3bc8>
      *     k.hello         #=> "Hello from Mod.\n"
      */
     @JRubyMethod(name = "extend", required = 1, rest = true)
     public IRubyObject extend(IRubyObject[] args) {
         Ruby runtime = getRuntime();
         
         // Make sure all arguments are modules before calling the callbacks
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isModule()) throw runtime.newTypeError(args[i], runtime.getModule()); 
         }
 
         ThreadContext context = runtime.getCurrentContext();
                 
         // MRI extends in order from last to first
         for (int i = args.length - 1; i >= 0; i--) {
             args[i].callMethod(context, "extend_object", this);
             args[i].callMethod(context, "extended", this);
         }
         return this;
     }
 
     /** rb_obj_dummy
      *
      * Default initialize method. This one gets defined in some other
      * place as a Ruby method.
      */
     public IRubyObject initialize() {
         return getRuntime().getNil();
     }
 
     /** rb_f_send
      *
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
     @JRubyMethod(name = {"send", "__send__"})
     public IRubyObject send(ThreadContext context, Block block) {
         throw context.getRuntime().newArgumentError(0, 1);
     }
     @JRubyMethod(name = {"send", "__send__"})
     public IRubyObject send(ThreadContext context, IRubyObject arg0, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, block);
     }
     @JRubyMethod(name = {"send", "__send__"})
     public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, arg1, block);
     }
     @JRubyMethod(name = {"send", "__send__"})
     public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, arg1, arg2, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, rest = true)
     public IRubyObject send(ThreadContext context, IRubyObject[] args, Block block) {
         String name = args[0].asJavaString();
         int newArgsLength = args.length - 1;
 
         IRubyObject[] newArgs;
         if (newArgsLength == 0) {
             newArgs = IRubyObject.NULL_ARRAY;
         } else {
             newArgs = new IRubyObject[newArgsLength];
             System.arraycopy(args, 1, newArgs, 0, newArgs.length);
         }
 
         return getMetaClass().finvoke(context, this, name, newArgs, block);
     }
 
     /** rb_false
      *
      * call_seq:
      *   nil.nil?               => true
      *   <anything_else>.nil?   => false
      *
      * Only the object <i>nil</i> responds <code>true</code> to <code>nil?</code>.
      */
     @JRubyMethod(name = "nil?")
     public IRubyObject nil_p(ThreadContext context) {
     	return context.getRuntime().getFalse();
     }
 
     /** rb_obj_pattern_match
      *
      *  call-seq:
      *     obj =~ other  => false
      *  
      *  Pattern Match---Overridden by descendents (notably
      *  <code>Regexp</code> and <code>String</code>) to provide meaningful
      *  pattern-match semantics.
      */    
     @JRubyMethod(name = "=~", required = 1)
     public IRubyObject op_match(ThreadContext context, IRubyObject arg) {
     	return context.getRuntime().getFalse();
     }
     
     public IRubyObject to_java() {
         throw getRuntime().newTypeError(getMetaClass().getBaseName() + " cannot coerce to a Java type.");
     }
 
     public IRubyObject as(Class javaClass) {
         throw getRuntime().newTypeError(getMetaClass().getBaseName() + " cannot coerce to a Java type.");
     }
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
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
 
     /**
      * Adds the specified object as a finalizer for this object.
      */ 
     public void addFinalizer(IRubyObject finalizer) {
         if (this.finalizer == null) {
             this.finalizer = new Finalizer(getRuntime().getObjectSpace().idOf(this));
             getRuntime().addFinalizer(this.finalizer);
         }
         this.finalizer.addFinalizer(finalizer);
     }
 
     /**
      * Remove all the finalizers for this object.
      */
     public void removeFinalizers() {
         if (finalizer != null) {
             finalizer.removeFinalizers();
             finalizer = null;
             getRuntime().removeFinalizer(this.finalizer);
         }
     }
 
 
     //
     // INSTANCE VARIABLE RUBY METHODS
     //
     
     /** rb_obj_ivar_defined
      *
      *  call-seq:
      *     obj.instance_variable_defined?(symbol)    => true or false
      *
      *  Returns <code>true</code> if the given instance variable is
      *  defined in <i>obj</i>.
      *
      *     class Fred
      *       def initialize(p1, p2)
      *         @a, @b = p1, p2
      *       end
      *     end
      *     fred = Fred.new('cat', 99)
      *     fred.instance_variable_defined?(:@a)    #=> true
      *     fred.instance_variable_defined?("@b")   #=> true
      *     fred.instance_variable_defined?("@c")   #=> false
      */
     @JRubyMethod(name = "instance_variable_defined?", required = 1)
     public IRubyObject instance_variable_defined_p(ThreadContext context, IRubyObject name) {
         if (variableTableContains(validateInstanceVariable(name.asJavaString()))) {
             return context.getRuntime().getTrue();
         }
         return context.getRuntime().getFalse();
     }
 
     /** rb_obj_ivar_get
      *
      *  call-seq:
      *     obj.instance_variable_get(symbol)    => obj
      *
      *  Returns the value of the given instance variable, or nil if the
      *  instance variable is not set. The <code>@</code> part of the
      *  variable name should be included for regular instance
      *  variables. Throws a <code>NameError</code> exception if the
      *  supplied symbol is not valid as an instance variable name.
      *     
      *     class Fred
      *       def initialize(p1, p2)
      *         @a, @b = p1, p2
      *       end
      *     end
      *     fred = Fred.new('cat', 99)
      *     fred.instance_variable_get(:@a)    #=> "cat"
      *     fred.instance_variable_get("@b")   #=> 99
      */
     @JRubyMethod(name = "instance_variable_get", required = 1)
     public IRubyObject instance_variable_get(ThreadContext context, IRubyObject name) {
         IRubyObject value;
         if ((value = variableTableFetch(validateInstanceVariable(name.asJavaString()))) != null) {
             return value;
         }
         return context.getRuntime().getNil();
     }
 
     /** rb_obj_ivar_set
      *
      *  call-seq:
      *     obj.instance_variable_set(symbol, obj)    => obj
      *  
      *  Sets the instance variable names by <i>symbol</i> to
      *  <i>object</i>, thereby frustrating the efforts of the class's
      *  author to attempt to provide proper encapsulation. The variable
      *  did not have to exist prior to this call.
      *     
      *     class Fred
      *       def initialize(p1, p2)
      *         @a, @b = p1, p2
      *       end
      *     end
      *     fred = Fred.new('cat', 99)
      *     fred.instance_variable_set(:@a, 'dog')   #=> "dog"
      *     fred.instance_variable_set(:@c, 'cat')   #=> "cat"
      *     fred.inspect                             #=> "#<Fred:0x401b3da8 @a=\"dog\", @b=99, @c=\"cat\">"
      */
     @JRubyMethod(name = "instance_variable_set", required = 2)
     public IRubyObject instance_variable_set(IRubyObject name, IRubyObject value) {
         ensureInstanceVariablesSettable();
         return variableTableStore(validateInstanceVariable(name.asJavaString()), value);
     }
 
     /** rb_obj_remove_instance_variable
      *
      *  call-seq:
      *     obj.remove_instance_variable(symbol)    => obj
      *  
      *  Removes the named instance variable from <i>obj</i>, returning that
      *  variable's value.
      *     
      *     class Dummy
      *       attr_reader :var
      *       def initialize
      *         @var = 99
      *       end
      *       def remove
      *         remove_instance_variable(:@var)
      *       end
      *     end
      *     d = Dummy.new
      *     d.var      #=> 99
      *     d.remove   #=> 99
      *     d.var      #=> nil
      */
     @JRubyMethod(name = "remove_instance_variable", required = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject remove_instance_variable(ThreadContext context, IRubyObject name, Block block) {
         ensureInstanceVariablesSettable();
         IRubyObject value;
         if ((value = variableTableRemove(validateInstanceVariable(name.asJavaString()))) != null) {
             return value;
         }
         throw context.getRuntime().newNameError("instance variable " + name.asJavaString() + " not defined", name.asJavaString());
     }
 
     /** rb_obj_instance_variables
      *
      *  call-seq:
      *     obj.instance_variables    => array
      *  
      *  Returns an array of instance variable names for the receiver. Note
      *  that simply defining an accessor does not create the corresponding
      *  instance variable.
      *     
      *     class Fred
      *       attr_accessor :a1
      *       def initialize
      *         @iv = 3
      *       end
      *     end
      *     Fred.new.instance_variables   #=> ["@iv"]
      */    
     @JRubyMethod(name = "instance_variables")
     public RubyArray instance_variables(ThreadContext context) {
         Ruby runtime = context.getRuntime();
         List<String> nameList = getInstanceVariableNameList();
 
         RubyArray array = runtime.newArray(nameList.size());
 
         for (String name : nameList) {
             array.append(runtime.newString(name));
         }
 
         return array;
     }
 
     //
     // INSTANCE VARIABLE API METHODS
     //
 
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */    
     public InstanceVariables getInstanceVariables() {
         return this;
     }
     
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#hasInstanceVariable
      */
     public boolean hasInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return variableTableContains(name);
     }
     
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastHasInstanceVariable
      */
     public boolean fastHasInstanceVariable(String internedName) {
         assert IdUtil.isInstanceVariable(internedName);
         return variableTableFastContains(internedName);
     }
     
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariable
      */
     public IRubyObject getInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         return variableTableFetch(name);
     }
     
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastGetInstanceVariable
      */
     public IRubyObject fastGetInstanceVariable(String internedName) {
         assert IdUtil.isInstanceVariable(internedName);
         return variableTableFastFetch(internedName);
     }
 
     /** rb_iv_set / rb_ivar_set
     *
     * @see org.jruby.runtime.builtin.InstanceVariables#setInstanceVariable
     */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         assert IdUtil.isInstanceVariable(name) && value != null;
         ensureInstanceVariablesSettable();
         return variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#fastSetInstanceVariable
      */    
     public IRubyObject fastSetInstanceVariable(String internedName, IRubyObject value) {
         assert IdUtil.isInstanceVariable(internedName) && value != null;
         ensureInstanceVariablesSettable();
         return variableTableFastStore(internedName, value);
      }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#removeInstanceVariable
      */    
     public IRubyObject removeInstanceVariable(String name) {
         assert IdUtil.isInstanceVariable(name);
         ensureInstanceVariablesSettable();
         return variableTableRemove(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariableList
      */    
     public List<Variable<IRubyObject>> getInstanceVariableList() {
         VariableTableEntry[] table = variableTableGetTable();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         IRubyObject readValue;
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if (IdUtil.isInstanceVariable(e.name)) {
                     if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                     list.add(new VariableEntry<IRubyObject>(e.name, readValue));
                 }
             }
         }
         return list;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariableNameList
      */    
     public List<String> getInstanceVariableNameList() {
         VariableTableEntry[] table = variableTableGetTable();
         ArrayList<String> list = new ArrayList<String>();
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if (IdUtil.isInstanceVariable(e.name)) {
                     list.add(e.name);
                 }
             }
         }
         return list;
     }
 
     /**
+     * @see org.jruby.runtime.builtin.InstanceVariables#getInstanceVariableNameList
+     */
+    public void copyInstanceVariablesInto(InstanceVariables other) {
+        VariableTableEntry[] table = variableTableGetTable();
+        for (int i = table.length; --i >= 0; ) {
+            for (VariableTableEntry e = table[i]; e != null; e = e.next) {
+                if (IdUtil.isInstanceVariable(e.name)) {
+                    other.setInstanceVariable(e.name, e.value);
+                }
+            }
+        }
+    }
+
+    /**
      * The error message used when some one tries to modify an
      * instance variable in a high security setting.
      */
     protected static final String ERR_INSECURE_SET_INST_VAR  = "Insecure: can't modify instance variable";
 
     /**
      * Checks if the name parameter represents a legal instance variable name, and otherwise throws a Ruby NameError
      */
     protected String validateInstanceVariable(String name) {
         if (IdUtil.isValidInstanceVariableName(name)) return name;
 
         throw getRuntime().newNameError("`" + name + "' is not allowable as an instance variable name", name);
     }
 
     /**
      * Makes sure that instance variables can be set on this object,
      * including information about whether this object is frozen, or
      * tainted. Will throw a suitable exception in that case.
      */
     protected void ensureInstanceVariablesSettable() {
         if (!isFrozen() && (getRuntime().getSafeLevel() < 4 || isTaint())) {
             return;
         }
         
         if (getRuntime().getSafeLevel() >= 4 && !isTaint()) {
             throw getRuntime().newSecurityError(ERR_INSECURE_SET_INST_VAR);
         }
         if (isFrozen()) {
             if (this instanceof RubyModule) {
                 throw getRuntime().newFrozenError("class/module ");
             } else {
                 throw getRuntime().newFrozenError("");
             }
         }
     }
 
     //
     // INTERNAL VARIABLE METHODS
     //
     
     /**
      * Dummy method to avoid a cast, and to avoid polluting the
      * IRubyObject interface with all the instance variable management
      * methods.
      */    
     public InternalVariables getInternalVariables() {
         return this;
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#hasInternalVariable
      */    
     public boolean hasInternalVariable(String name) {
         assert !isRubyVariable(name);
         return variableTableContains(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastHasInternalVariable
      */    
     public boolean fastHasInternalVariable(String internedName) {
         assert !isRubyVariable(internedName);
         return variableTableFastContains(internedName);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#getInternalVariable
      */    
     public IRubyObject getInternalVariable(String name) {
         assert !isRubyVariable(name);
         return variableTableFetch(name);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastGetInternalVariable
      */    
     public IRubyObject fastGetInternalVariable(String internedName) {
         assert !isRubyVariable(internedName);
         return variableTableFastFetch(internedName);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#setInternalVariable
      */    
     public void setInternalVariable(String name, IRubyObject value) {
         assert !isRubyVariable(name);
         variableTableStore(name, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#fastSetInternalVariable
      */    
     public void fastSetInternalVariable(String internedName, IRubyObject value) {
         assert !isRubyVariable(internedName);
         variableTableFastStore(internedName, value);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#removeInternalVariable
      */    
     public IRubyObject removeInternalVariable(String name) {
         assert !isRubyVariable(name);
         return variableTableRemove(name);
     }
 
     /**
      * Sync one variable table with another - this is used to make
      * rbClone work correctly.
      */
     public void syncVariables(List<Variable<IRubyObject>> variables) {
         variableTableSync(variables);
     }
 
     /**
      * @see org.jruby.runtime.builtin.InternalVariables#getInternalVariableList
      */    
     public List<Variable<IRubyObject>> getInternalVariableList() {
         VariableTableEntry[] table = variableTableGetTable();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         IRubyObject readValue;
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if (!isRubyVariable(e.name)) {
                     if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                     list.add(new VariableEntry<IRubyObject>(e.name, readValue));
                 }
             }
         }
         return list;
     }
 
     
     //
     // COMMON VARIABLE METHODS
     //
 
     /**
      * Returns true if object has any variables, defined as:
      * <ul>
      * <li> instance variables
      * <li> class variables
      * <li> constants
      * <li> internal variables, such as those used when marshaling Ranges and Exceptions
      * </ul>
      * @return true if object has any variables, else false
      */
     public boolean hasVariables() {
         return variableTableGetSize() > 0;
     }
 
     /**
      * Returns the amount of instance variables, class variables,
      * constants and internal variables this object has.
      */
     public int getVariableCount() {
         return variableTableGetSize();
     }
     
     /**
      * Gets a list of all variables in this object.
      */
     // TODO: must override in RubyModule to pick up constants
     public List<Variable<IRubyObject>> getVariableList() {
         VariableTableEntry[] table = variableTableGetTable();
         ArrayList<Variable<IRubyObject>> list = new ArrayList<Variable<IRubyObject>>();
         IRubyObject readValue;
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                 list.add(new VariableEntry<IRubyObject>(e.name, readValue));
             }
         }
         return list;
     }
 
     /**
      * Gets a name list of all variables in this object.
      */
    // TODO: must override in RubyModule to pick up constants
    public List<String> getVariableNameList() {
         VariableTableEntry[] table = variableTableGetTable();
         ArrayList<String> list = new ArrayList<String>();
         for (int i = table.length; --i >= 0; ) {
             for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                 list.add(e.name);
             }
         }
         return list;
     }
     
     /**
      * Gets internal access to the getmap for variables.
      */
     @SuppressWarnings("unchecked")
     @Deprecated // born deprecated
     public Map getVariableMap() {
         return variableTableGetMap();
     }
 
     /**
      * Check the syntax of a Ruby variable, including that it's longer
      * than zero characters, and starts with either an @ or a capital
      * letter.
      */
     // FIXME: this should go somewhere more generic -- maybe IdUtil
     protected static final boolean isRubyVariable(String name) {
         char c;
         return name.length() > 0 && ((c = name.charAt(0)) == '@' || (c <= 'Z' && c >= 'A'));
     }
     
     //
     // VARIABLE TABLE METHODS, ETC.
     //
     
     protected static final int VARIABLE_TABLE_DEFAULT_CAPACITY = 8; // MUST be power of 2!
     protected static final int VARIABLE_TABLE_MAXIMUM_CAPACITY = 1 << 30;
     protected static final float VARIABLE_TABLE_LOAD_FACTOR = 0.75f;
     protected static final VariableTableEntry[] VARIABLE_TABLE_EMPTY_TABLE = new VariableTableEntry[0];
 
     /**
      * Every entry in the variable map is represented by an instance
      * of this class.
      */
     protected static final class VariableTableEntry {
         final int hash;
         final String name;
         volatile IRubyObject value;
         final VariableTableEntry next;
         
         VariableTableEntry(int hash, String name, IRubyObject value, VariableTableEntry next) {
             assert name == name.intern() : name + " is not interned";
             this.hash = hash;
             this.name = name;
             this.value = value;
             this.next = next;
         }
     }
 
     /**
      * Reads the value of the specified entry, locked on the current
      * object.
      */    
     protected synchronized IRubyObject variableTableReadLocked(VariableTableEntry entry) {
         return entry.value;
     }
 
     /**
      * Checks if the variable table contains a variable of the
      * specified name.
      */
     protected boolean variableTableContains(String name) {
         VariableTableEntry[] table;
         if ((table = variableTable) != null) {
             int hash = name.hashCode();
             for (VariableTableEntry e = table[hash & (table.length - 1)]; e != null; e = e.next) {
                 if (hash == e.hash && name.equals(e.name)) {
                     return true;
                 }
             }
         }
         return false;
     }
 
     /**
      * Checks if the variable table contains the the variable of the
      * specified name, where the precondition is that the name must be
      * an interned Java String.
      */
     protected boolean variableTableFastContains(String internedName) {
         assert internedName == internedName.intern() : internedName + " not interned";
         VariableTableEntry[] table;
         if ((table = variableTable) != null) {
             for (VariableTableEntry e = table[internedName.hashCode() & (table.length - 1)]; e != null; e = e.next) {
                 if (internedName == e.name) {
                     return true;
                 }
             }
         }
         return false;
     }
 
     /**
      * Fetch an object from the variable table based on the name.
      *
      * @return the object or null if not found
      */
     protected IRubyObject variableTableFetch(String name) {
         VariableTableEntry[] table;
         IRubyObject readValue;
         if ((table = variableTable) != null) {
             int hash = name.hashCode();
             for (VariableTableEntry e = table[hash & (table.length - 1)]; e != null; e = e.next) {
                 if (hash == e.hash && name.equals(e.name)) {
                     if ((readValue = e.value) != null) return readValue;
                     return variableTableReadLocked(e);
                 }
             }
         }
         return null;
     }
 
     /**
      * Fetch an object from the variable table based on the name,
      * where the name must be an interned Java String.
      *
      * @return the object or null if not found
      */
     protected IRubyObject variableTableFastFetch(String internedName) {
         VariableTableEntry[] table;
         IRubyObject readValue;
         if ((table = variableTable) != null) {
             for (VariableTableEntry e = table[internedName.hashCode() & (table.length - 1)]; e != null; e = e.next) {
                 if (internedName == e.name) {
                     if ((readValue = e.value) != null) return readValue;
                     return variableTableReadLocked(e);
                 }
             }
         }
         return null;
     }
 
     /**
      * Store a value in the variable store under the specific name.
      */
     protected IRubyObject variableTableStore(String name, IRubyObject value) {
         int hash = name.hashCode();
         synchronized(this) {
             VariableTableEntry[] table;
             VariableTableEntry e;
             if ((table = variableTable) == null) {
                 table =  new VariableTableEntry[VARIABLE_TABLE_DEFAULT_CAPACITY];
                 e = new VariableTableEntry(hash, name.intern(), value, null);
                 table[hash & (VARIABLE_TABLE_DEFAULT_CAPACITY - 1)] = e;
                 variableTableThreshold = (int)(VARIABLE_TABLE_DEFAULT_CAPACITY * VARIABLE_TABLE_LOAD_FACTOR);
                 variableTableSize = 1;
                 variableTable = table;
                 return value;
             }
             int potentialNewSize;
             if ((potentialNewSize = variableTableSize + 1) > variableTableThreshold) {
                 table = variableTableRehash();
             }
             int index;
             for (e = table[index = hash & (table.length - 1)]; e != null; e = e.next) {
                 if (hash == e.hash && name.equals(e.name)) {
                     e.value = value;
                     return value;
                 }
             }
             e = new VariableTableEntry(hash, name.intern(), value, table[index]);
             table[index] = e;
             variableTableSize = potentialNewSize;
             variableTable = table; // write-volatile
         }
         return value;
     }
 
     /**
      * Will store the value under the specified name, where the name
      * needs to be an interned Java String.
      */
     protected IRubyObject variableTableFastStore(String internedName, IRubyObject value) {
         if (IdUtil.isConstant(internedName)) new Exception().printStackTrace();
 
         assert internedName == internedName.intern() : internedName + " not interned";
         int hash = internedName.hashCode();
         synchronized(this) {
             VariableTableEntry[] table;
             VariableTableEntry e;
             if ((table = variableTable) == null) {
                 table =  new VariableTableEntry[VARIABLE_TABLE_DEFAULT_CAPACITY];
                 e = new VariableTableEntry(hash, internedName, value, null);
                 table[hash & (VARIABLE_TABLE_DEFAULT_CAPACITY - 1)] = e;
                 variableTableThreshold = (int)(VARIABLE_TABLE_DEFAULT_CAPACITY * VARIABLE_TABLE_LOAD_FACTOR);
                 variableTableSize = 1;
                 variableTable = table;
                 return value;
             }
             int potentialNewSize;
             if ((potentialNewSize = variableTableSize + 1) > variableTableThreshold) {
                 table = variableTableRehash();
             }
             int index;
             for (e = table[index = hash & (table.length - 1)]; e != null; e = e.next) {
                 if (internedName == e.name) {
                     e.value = value;
                     return value;
                 }
             }
             e = new VariableTableEntry(hash, internedName, value, table[index]);
             table[index] = e;
             variableTableSize = potentialNewSize;
             variableTable = table; // write-volatile
         }
         return value;
     }
 
     /**
      * Removes the entry with the specified name from the variable
      * table, and returning the removed value.
      */
     protected IRubyObject variableTableRemove(String name) {
         synchronized(this) {
             VariableTableEntry[] table;
             if ((table = variableTable) != null) {
                 int hash = name.hashCode();
                 int index = hash & (table.length - 1);
                 VariableTableEntry first = table[index];
                 VariableTableEntry e;
                 for (e = first; e != null; e = e.next) {
                     if (hash == e.hash && name.equals(e.name)) {
                         IRubyObject oldValue = e.value;
                         // All entries following removed node can stay
                         // in list, but all preceding ones need to be
                         // cloned.
                         VariableTableEntry newFirst = e.next;
                         for (VariableTableEntry p = first; p != e; p = p.next) {
                             newFirst = new VariableTableEntry(p.hash, p.name, p.value, newFirst);
                         }
                         table[index] = newFirst;
                         variableTableSize--;
                         variableTable = table; // write-volatile 
                         return oldValue;
                     }
                 }
             }
         }
         return null;
     }
 
     /**
      * Get the actual table used to save variable entries.
      */
     protected VariableTableEntry[] variableTableGetTable() {
         VariableTableEntry[] table;
         if ((table = variableTable) != null) {
             return table;
         }
         return VARIABLE_TABLE_EMPTY_TABLE;
     }
 
     /**
      * Get the size of the variable table.
      */
     protected int variableTableGetSize() {
         if (variableTable != null) {
             return variableTableSize;
         }
         return 0;
     }
 
     /**
      * Synchronize the variable table with the argument. In real terms
      * this means copy all entries into a newly allocated table.
      */
     protected void variableTableSync(List<Variable<IRubyObject>> vars) {
         synchronized(this) {
             variableTableSize = 0;
             variableTableThreshold = (int)(VARIABLE_TABLE_DEFAULT_CAPACITY * VARIABLE_TABLE_LOAD_FACTOR);
             variableTable =  new VariableTableEntry[VARIABLE_TABLE_DEFAULT_CAPACITY];
             for (Variable<IRubyObject> var : vars) {
                 variableTableStore(var.getName(), var.getValue());
             }
         }
     }
 
     /**
      * Rehashes the variable table. Must be called from a synchronized
      * block.
      */
     // MUST be called from synchronized/locked block!
     // should only be called by variableTableStore/variableTableFastStore
     protected final VariableTableEntry[] variableTableRehash() {
         VariableTableEntry[] oldTable = variableTable;
         int oldCapacity;
         if ((oldCapacity = oldTable.length) >= VARIABLE_TABLE_MAXIMUM_CAPACITY) {
             return oldTable;
         }
 
         int newCapacity = oldCapacity << 1;
         VariableTableEntry[] newTable = new VariableTableEntry[newCapacity];
         variableTableThreshold = (int)(newCapacity * VARIABLE_TABLE_LOAD_FACTOR);
         int sizeMask = newCapacity - 1;
         VariableTableEntry e;
         for (int i = oldCapacity; --i >= 0; ) {
             // We need to guarantee that any existing reads of old Map can
             //  proceed. So we cannot yet null out each bin.
             e = oldTable[i];
 
             if (e != null) {
                 VariableTableEntry next = e.next;
                 int idx = e.hash & sizeMask;
 
                 //  Single node on list
                 if (next == null)
                     newTable[idx] = e;
 
                 else {
                     // Reuse trailing consecutive sequence at same slot
                     VariableTableEntry lastRun = e;
                     int lastIdx = idx;
                     for (VariableTableEntry last = next;
                          last != null;
                          last = last.next) {
                         int k = last.hash & sizeMask;
                         if (k != lastIdx) {
                             lastIdx = k;
                             lastRun = last;
                         }
                     }
                     newTable[lastIdx] = lastRun;
 
                     // Clone all remaining nodes
                     for (VariableTableEntry p = e; p != lastRun; p = p.next) {
                         int k = p.hash & sizeMask;
                         VariableTableEntry m = new VariableTableEntry(p.hash, p.name, p.value, newTable[k]);
                         newTable[k] = m;
                     }
                 }
             }
         }
         variableTable = newTable;
         return newTable;
     }
 
     /**
      * Method to help ease transition to new variables implementation.
      * Will likely be deprecated in the near future.
      */
     @SuppressWarnings("unchecked")
     protected Map variableTableGetMap() {
         HashMap map = new HashMap();
         VariableTableEntry[] table;
         IRubyObject readValue;
         if ((table = variableTable) != null) {
             for (int i = table.length; --i >= 0; ) {
                 for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                     if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                     map.put(e.name, readValue);
                 }
             }
         }
         return map;
     }
 
     /**
      * Method to help ease transition to new variables implementation.
      * Will likely be deprecated in the near future.
      */
     @SuppressWarnings("unchecked")
     protected Map variableTableGetMap(Map map) {
         VariableTableEntry[] table;
         IRubyObject readValue;
         if ((table = variableTable) != null) {
             for (int i = table.length; --i >= 0; ) {
                 for (VariableTableEntry e = table[i]; e != null; e = e.next) {
                     if ((readValue = e.value) == null) readValue = variableTableReadLocked(e);
                     map.put(e.name, readValue);
                 }
             }
         }
         return map;
     }
 
     /**
      * Tries to support Java serialization of Ruby objects. This is
      * still experimental and might not work.
      */
     // NOTE: Serialization is primarily supported for testing purposes, and there is no general
     // guarantee that serialization will work correctly. Specifically, instance variables pointing
     // at symbols, threads, modules, classes, and other unserializable types are not detected.
     private void writeObject(ObjectOutputStream out) throws IOException {
         out.defaultWriteObject();
         // write out ivar count followed by name/value pairs
         List<String> names = getInstanceVariableNameList();
         out.writeInt(names.size());
         for (String name : names) {
             out.writeObject(name);
             out.writeObject(getInstanceVariables().getInstanceVariable(name));
         }
     }
 
     /**
      * Tries to support Java unserialization of Ruby objects. This is
      * still experimental and might not work.
      */
     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         // rest in ivar count followed by name/value pairs
         int ivarCount = in.readInt();
         for (int i = 0; i < ivarCount; i++) {
             setInstanceVariable((String)in.readObject(), (IRubyObject)in.readObject());
         }
     }
 
 }
diff --git a/src/org/jruby/RubyTime.java b/src/org/jruby/RubyTime.java
index 268985b5a7..2e752cab5c 100644
--- a/src/org/jruby/RubyTime.java
+++ b/src/org/jruby/RubyTime.java
@@ -1,850 +1,852 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Locale;
 import java.util.Map;
 import java.util.TimeZone;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.RubyDateFormat;
 
 /** The Time class.
  * 
  * @author chadfowler, jpetersen
  */
 @JRubyClass(name="Time", include="Comparable")
 public class RubyTime extends RubyObject {
     public static final String UTC = "UTC";
     private DateTime dt;
     private long usec;
     
     private final static DateTimeFormatter ONE_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM  d HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TWO_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
 
     private final static DateTimeFormatter TO_S_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TO_S_UTC_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy").withLocale(Locale.ENGLISH);
 
     // There are two different popular TZ formats: legacy (AST+3:00:00, GMT-3), and
     // newer one (US/Pacific, America/Los_Angeles). This pattern is to detect
     // the legacy TZ format in order to convert it to the newer format
     // understood by Java API.
     private static final Pattern TZ_PATTERN
             = Pattern.compile("(\\D+?)([\\+-]?)(\\d+)(:\\d+)?(:\\d+)?");
     
     private static final ByteList TZ_STRING = ByteList.create("TZ");
      
     public static DateTimeZone getLocalTimeZone(Ruby runtime) {
         RubyString tzVar = runtime.newString(TZ_STRING);
         RubyHash h = ((RubyHash)runtime.getObject().fastGetConstant("ENV"));
         IRubyObject tz = h.op_aref(runtime.getCurrentContext(), tzVar);
         if (tz == null || ! (tz instanceof RubyString)) {
             return DateTimeZone.getDefault();
         } else {
             String zone = tz.toString();
             DateTimeZone cachedZone = runtime.getLocalTimezoneCache().get(zone);
 
             if (cachedZone != null) return cachedZone;
 
             String originalZone = zone;
 
             // Value of "TZ" property is of a bit different format,
             // which confuses the Java's TimeZone.getTimeZone(id) method,
             // and so, we need to convert it.
 
             Matcher tzMatcher = TZ_PATTERN.matcher(zone);
             if (tzMatcher.matches()) {                    
                 String sign = tzMatcher.group(2);
                 String hours = tzMatcher.group(3);
                 String minutes = tzMatcher.group(4);
                 
                 // GMT+00:00 --> Etc/GMT, see "MRI behavior"
                 // comment below.
                 if (("00".equals(hours) || "0".equals(hours))
                         && (minutes == null || ":00".equals(minutes) || ":0".equals(minutes))) {
                     zone = "Etc/GMT";
                 } else {
                     // Invert the sign, since TZ format and Java format
                     // use opposite signs, sigh... Also, Java API requires
                     // the sign to be always present, be it "+" or "-".
                     sign = ("-".equals(sign)? "+" : "-");
 
                     // Always use "GMT" since that's required by Java API.
                     zone = "GMT" + sign + hours;
 
                     if (minutes != null) {
                         zone += minutes;
                     }
                 }
             }
 
             // MRI behavior: With TZ equal to "GMT" or "UTC", Time.now
             // is *NOT* considered as a proper GMT/UTC time:
             //   ENV['TZ']="GMT"
             //   Time.now.gmt? ==> false
             //   ENV['TZ']="UTC"
             //   Time.now.utc? ==> false
             // Hence, we need to adjust for that.
             if ("GMT".equalsIgnoreCase(zone) || "UTC".equalsIgnoreCase(zone)) {
                 zone = "Etc/" + zone;
             }
 
             DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
             runtime.getLocalTimezoneCache().put(originalZone, dtz);
             return dtz;
         }
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass, DateTime dt) {
         super(runtime, rubyClass);
         this.dt = dt;
     }
 
     // We assume that these two time instances
     // occurred at the same time.
     private static final long BASE_TIME_MILLIS = System.currentTimeMillis();
     private static final long BASE_TIME_NANOS = System.nanoTime();
 
     private static ObjectAllocator TIME_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             long usecsPassed = (System.nanoTime() - BASE_TIME_NANOS) / 1000L;
             long millisTime = BASE_TIME_MILLIS + usecsPassed / 1000L;
             long usecs = usecsPassed % 1000L;
 
             DateTimeZone dtz = getLocalTimeZone(runtime);
             DateTime dt = new DateTime(millisTime, dtz);
             RubyTime rt =  new RubyTime(runtime, klass, dt);
             rt.setUSec(usecs);
 
             return rt;
         }
     };
 
     public static RubyClass createTimeClass(Ruby runtime) {
         RubyClass timeClass = runtime.defineClass("Time", runtime.getObject(), TIME_ALLOCATOR);
         timeClass.index = ClassIndex.TIME;
         runtime.setTime(timeClass);
         
         timeClass.includeModule(runtime.getComparable());
         
         timeClass.defineAnnotatedMethods(RubyTime.class);
         
         return timeClass;
     }
     
     public void setUSec(long usec) {
         this.usec = usec;
     }
     
     public long getUSec() {
         return usec;
     }
     
     public void updateCal(DateTime dt) {
         this.dt = dt;
     }
     
     protected long getTimeInMillis() {
         return dt.getMillis();  // For JDK 1.4 we can use "cal.getTimeInMillis()"
     }
     
     public static RubyTime newTime(Ruby runtime, long milliseconds) {
         return newTime(runtime, new DateTime(milliseconds));
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt) {
         return new RubyTime(runtime, runtime.getTime(), dt);
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt, long usec) {
         RubyTime t = new RubyTime(runtime, runtime.getTime(), dt);
         t.setUSec(usec);
         return t;
     }
     
     @Override
     public Class<?> getJavaClass() {
         return Date.class;
     }
 
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         if (!(original instanceof RubyTime)) {
             throw getRuntime().newTypeError("Expecting an instance of class Time");
         }
         
         RubyTime originalTime = (RubyTime) original;
         
         // We can just use dt, since it is immutable
         dt = originalTime.dt;
         usec = originalTime.usec;
         
         return this;
     }
 
     @JRubyMethod(name = "succ")
     public RubyTime succ() {
         return newTime(getRuntime(),dt.plusSeconds(1));
     }
 
     @JRubyMethod(name = {"gmtime", "utc"})
     public RubyTime gmtime() {
         dt = dt.withZone(DateTimeZone.UTC);
         return this;
     }
 
     @JRubyMethod(name = "localtime")
     public RubyTime localtime() {
         dt = dt.withZone(getLocalTimeZone(getRuntime()));
         return this;
     }
     
     @JRubyMethod(name = {"gmt?", "utc?", "gmtime?"})
     public RubyBoolean gmt() {
         return getRuntime().newBoolean(dt.getZone().getID().equals("UTC"));
     }
     
     @JRubyMethod(name = {"getgm", "getutc"})
     public RubyTime getgm() {
         return newTime(getRuntime(), dt.withZone(DateTimeZone.UTC), getUSec());
     }
 
     @JRubyMethod(name = "getlocal")
     public RubyTime getlocal() {
         return newTime(getRuntime(), dt.withZone(getLocalTimeZone(getRuntime())), getUSec());
     }
 
     @JRubyMethod(name = "strftime", required = 1)
     public RubyString strftime(IRubyObject format) {
         final RubyDateFormat rubyDateFormat = new RubyDateFormat("-", Locale.US);
         rubyDateFormat.applyPattern(format.toString());
         rubyDateFormat.setDateTime(dt);
         String result = rubyDateFormat.format(null);
         return getRuntime().newString(result);
     }
     
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) >= 0);
         }
         
         return RubyComparable.op_ge(context, this, other);
     }
     
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) > 0);
         }
         
         return RubyComparable.op_gt(context, this, other);
     }
     
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) <= 0);
         }
         
         return RubyComparable.op_le(context, this, other);
     }
     
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) < 0);
         }
         
         return RubyComparable.op_lt(context, this, other);
     }
     
     private int cmp(RubyTime other) {
         long millis = getTimeInMillis();
 		long millis_other = other.getTimeInMillis();
         long usec_other = other.usec;
         
 		if (millis > millis_other || (millis == millis_other && usec > usec_other)) {
 		    return 1;
 		} else if (millis < millis_other || (millis == millis_other && usec < usec_other)) {
 		    return -1;
 		} 
 
         return 0;
     }
     
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject other) {
         long time = getTimeInMillis();
 
         if (other instanceof RubyTime) {
             throw getRuntime().newTypeError("time + time ?");
         }
         long adjustment = (long) (RubyNumeric.num2dbl(other) * 1000000);
         int micro = (int) (adjustment % 1000);
         adjustment = adjustment / 1000;
 
         time += adjustment;
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
         newTime.setUSec(micro);
 
         return newTime;
     }
     
     private IRubyObject opMinus(RubyTime other) {
         long time = getTimeInMillis() * 1000 + getUSec();
 
         time -= other.getTimeInMillis() * 1000 + other.getUSec();
         
         return RubyFloat.newFloat(getRuntime(), time / 1000000.0); // float number of seconds
     }
 
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_minus(IRubyObject other) {
         if (other instanceof RubyTime) return opMinus((RubyTime) other);
         
         long time = getTimeInMillis();
         long adjustment = (long) (RubyNumeric.num2dbl(other) * 1000000);
         int micro = (int) (adjustment % 1000);
         adjustment = adjustment / 1000;
 
         time -= adjustment;
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
         newTime.setUSec(micro);
 
         return newTime;
     }
 
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return (RubyNumeric.fix2int(callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", other)) == 0) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return context.getRuntime().newFixnum(cmp((RubyTime) other));
         }
 
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "eql?", required = 1)
     @Override
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyTime) {
             RubyTime otherTime = (RubyTime)other; 
             return (usec == otherTime.usec && getTimeInMillis() == otherTime.getTimeInMillis()) ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = {"asctime", "ctime"})
     public RubyString asctime() {
         DateTimeFormatter simpleDateFormat;
 
         if (dt.getDayOfMonth() < 10) {
             simpleDateFormat = ONE_DAY_CTIME_FORMATTER;
         } else {
             simpleDateFormat = TWO_DAY_CTIME_FORMATTER;
         }
         String result = simpleDateFormat.print(dt);
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = {"to_s", "inspect"})
     @Override
     public IRubyObject to_s() {
         DateTimeFormatter simpleDateFormat;
         if (dt.getZone() == DateTimeZone.UTC) {
             simpleDateFormat = TO_S_UTC_FORMATTER;
         } else {
             simpleDateFormat = TO_S_FORMATTER;
         }
 
         String result = simpleDateFormat.print(dt);
 
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = "to_a")
     @Override
     public RubyArray to_a() {
         return getRuntime().newArrayNoCopy(new IRubyObject[] { sec(), min(), hour(), mday(), month(), 
                 year(), wday(), yday(), isdst(), zone() });
     }
 
     @JRubyMethod(name = "to_f")
     public RubyFloat to_f() {
         long time = getTimeInMillis();
         time = time * 1000 + usec;
         return RubyFloat.newFloat(getRuntime(), time / 1000000.0);
     }
 
     @JRubyMethod(name = {"to_i", "tv_sec"})
     public RubyInteger to_i() {
         return getRuntime().newFixnum(getTimeInMillis() / 1000);
     }
 
     @JRubyMethod(name = {"usec", "tv_usec"})
     public RubyInteger usec() {
         return getRuntime().newFixnum(dt.getMillisOfSecond() * 1000 + getUSec());
     }
 
     public void setMicroseconds(long mic) {
         long millis = getTimeInMillis() % 1000;
         long withoutMillis = getTimeInMillis() - millis;
         withoutMillis += (mic / 1000);
         dt = dt.withMillis(withoutMillis);
         usec = mic % 1000;
     }
     
     public long microseconds() {
     	return getTimeInMillis() % 1000 * 1000 + usec;
     }
 
     @JRubyMethod(name = "sec")
     public RubyInteger sec() {
         return getRuntime().newFixnum(dt.getSecondOfMinute());
     }
 
     @JRubyMethod(name = "min")
     public RubyInteger min() {
         return getRuntime().newFixnum(dt.getMinuteOfHour());
     }
 
     @JRubyMethod(name = "hour")
     public RubyInteger hour() {
         return getRuntime().newFixnum(dt.getHourOfDay());
     }
 
     @JRubyMethod(name = {"mday", "day"})
     public RubyInteger mday() {
         return getRuntime().newFixnum(dt.getDayOfMonth());
     }
 
     @JRubyMethod(name = {"month", "mon"})
     public RubyInteger month() {
         return getRuntime().newFixnum(dt.getMonthOfYear());
     }
 
     @JRubyMethod(name = "year")
     public RubyInteger year() {
         return getRuntime().newFixnum(dt.getYear());
     }
 
     @JRubyMethod(name = "wday")
     public RubyInteger wday() {
         return getRuntime().newFixnum((dt.getDayOfWeek()%7));
     }
 
     @JRubyMethod(name = "yday")
     public RubyInteger yday() {
         return getRuntime().newFixnum(dt.getDayOfYear());
     }
 
     @JRubyMethod(name = {"gmt_offset", "gmtoff", "utc_offset"})
     public RubyInteger gmt_offset() {
         int offset = dt.getZone().getOffsetFromLocal(dt.getMillis());
         
         return getRuntime().newFixnum((int)(offset/1000));
     }
 
     @JRubyMethod(name = {"isdst", "dst?"})
     public RubyBoolean isdst() {
         return getRuntime().newBoolean(!dt.getZone().isStandardOffset(dt.getMillis()));
     }
 
     @JRubyMethod(name = "zone")
     public RubyString zone() {
         String zone = dt.getZone().getShortName(dt.getMillis());
         if(zone.equals("+00:00")) {
             zone = "GMT";
         }
         return getRuntime().newString(zone);
     }
 
     public void setDateTime(DateTime dt) {
         this.dt = dt;
     }
 
     public DateTime getDateTime() {
         return this.dt;
     }
 
     public Date getJavaDate() {
         return this.dt.toDate();
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
     	// modified to match how hash is calculated in 1.8.2
         return getRuntime().newFixnum((int)(((dt.getMillis() / 1000) ^ microseconds()) << 1) >> 1);
     }    
 
     @JRubyMethod(name = "_dump", optional = 1, frame = true)
     public RubyString dump(IRubyObject[] args, Block unusedBlock) {
         RubyString str = (RubyString) mdump(new IRubyObject[] { this });
         str.syncVariables(this.getVariableList());
         return str;
     }    
 
     public RubyObject mdump(final IRubyObject[] args) {
         RubyTime obj = (RubyTime)args[0];
         DateTime dateTime = obj.dt.withZone(DateTimeZone.UTC);
         byte dumpValue[] = new byte[8];
         int pe = 
             0x1                                 << 31 |
             (dateTime.getYear()-1900)           << 14 |
             (dateTime.getMonthOfYear()-1)       << 10 |
             dateTime.getDayOfMonth()            << 5  |
             dateTime.getHourOfDay();
         int se =
             dateTime.getMinuteOfHour()          << 26 |
             dateTime.getSecondOfMinute()        << 20 |
             (dateTime.getMillisOfSecond() * 1000 + (int)usec); // dump usec, not msec
 
         for(int i = 0; i < 4; i++) {
             dumpValue[i] = (byte)(pe & 0xFF);
             pe >>>= 8;
         }
         for(int i = 4; i < 8 ;i++) {
             dumpValue[i] = (byte)(se & 0xFF);
             se >>>= 8;
         }
         return RubyString.newString(obj.getRuntime(), new ByteList(dumpValue,false));
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(Block block) {
         return this;
     }
     
     /* Time class methods */
     
     public static IRubyObject s_new(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, new DateTime(getLocalTimeZone(runtime)));
         time.callInit(args,block);
         return time;
     }
 
     /**
      * @deprecated Use {@link #newInstance(ThreadContext, IRubyObject)}
      */
     @Deprecated
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return newInstance(context, recv);
     }
 
     @JRubyMethod(name = "now", backtrace = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv) {
         IRubyObject obj = ((RubyClass) recv).allocate();
         obj.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, obj);
         return obj;
     }
 
     @JRubyMethod(name = "at",  meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         final RubyTime time;
 
         if (arg instanceof RubyTime) {
             RubyTime other = (RubyTime) arg;
             time = new RubyTime(runtime, (RubyClass) recv, other.dt);
             time.setUSec(other.getUSec());
         } else {
             time = new RubyTime(runtime, (RubyClass) recv,
                     new DateTime(0L, getLocalTimeZone(runtime)));
 
             long seconds = RubyNumeric.num2long(arg);
             long millisecs = 0;
             long microsecs = 0;
 
             // In the case of two arguments, MRI will discard the portion of
             // the first argument after a decimal point (i.e., "floor").
             // However in the case of a single argument, any portion after
             // the decimal point is honored.
             if (arg instanceof RubyFloat) {
                 double dbl = ((RubyFloat) arg).getDoubleValue();
                 long micro = (long) ((dbl - seconds) * 1000000);
                 millisecs = micro / 1000;
                 microsecs = micro % 1000;
             }
             time.setUSec(microsecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
         }
 
         time.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, time);
 
         return time;
     }
 
     @JRubyMethod(name = "at", meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv,
                 new DateTime(0L, getLocalTimeZone(runtime)));
 
             long seconds = RubyNumeric.num2long(arg1);
             long millisecs = 0;
             long microsecs = 0;
 
             long tmp = RubyNumeric.num2long(arg2);
             millisecs = tmp / 1000;
             microsecs = tmp % 1000;
 
             time.setUSec(microsecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
 
             time.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, time);
 
         return time;
     }
 
     @JRubyMethod(name = {"local", "mktime"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_local(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, false);
     }
 
     @JRubyMethod(name = {"utc", "gm"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_utc(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, true);
     }
 
     @JRubyMethod(name = "_load", required = 1, frame = true, meta = true)
     public static RubyTime load(IRubyObject recv, IRubyObject from, Block block) {
         return s_mload(recv, (RubyTime)(((RubyClass)recv).allocate()), from);
     }
 
     protected static RubyTime s_mload(IRubyObject recv, RubyTime time, IRubyObject from) {
         Ruby runtime = recv.getRuntime();
 
         DateTime dt = new DateTime(DateTimeZone.UTC);
 
         byte[] fromAsBytes = null;
         fromAsBytes = from.convertToString().getBytes();
         if(fromAsBytes.length != 8) {
             throw runtime.newTypeError("marshaled time format differ");
         }
         int p=0;
         int s=0;
         for (int i = 0; i < 4; i++) {
             p |= ((int)fromAsBytes[i] & 0xFF) << (8 * i);
         }
         for (int i = 4; i < 8; i++) {
             s |= ((int)fromAsBytes[i] & 0xFF) << (8 * (i - 4));
         }
         if ((p & (1<<31)) == 0) {
             dt = dt.withMillis(p * 1000L + s);
         } else {
             p &= ~(1<<31);
             dt = dt.withYear(((p >>> 14) & 0xFFFF) + 1900);
             dt = dt.withMonthOfYear(((p >>> 10) & 0xF) + 1);
             dt = dt.withDayOfMonth(((p >>> 5)  & 0x1F));
             dt = dt.withHourOfDay((p & 0x1F));
             dt = dt.withMinuteOfHour(((s >>> 26) & 0x3F));
             dt = dt.withSecondOfMinute(((s >>> 20) & 0x3F));
             // marsaling dumps usec, not msec
             dt = dt.withMillisOfSecond((s & 0xFFFFF) / 1000);
             dt = dt.withZone(getLocalTimeZone(runtime));
             time.setUSec((s & 0xFFFFF) % 1000);
         }
         time.setDateTime(dt);
+
+        from.getInstanceVariables().copyInstanceVariablesInto(time);
         return time;
     }
 
     private static final String[] MONTHS = {"jan", "feb", "mar", "apr", "may", "jun",
                                             "jul", "aug", "sep", "oct", "nov", "dec"};
 
     private static final Map<String, Integer> MONTHS_MAP = new HashMap<String, Integer>();
     static {
         for (int i = 0; i < MONTHS.length; i++) {
             MONTHS_MAP.put(MONTHS[i], i + 1);
         }
     }
 
     private static final int[] time_min = {1, 0, 0, 0, Integer.MIN_VALUE};
     private static final int[] time_max = {31, 23, 59, 60, Integer.MAX_VALUE};
 
     private static final int ARG_SIZE = 7;
 
     private static RubyTime createTime(IRubyObject recv, IRubyObject[] args, boolean gmt) {
         Ruby runtime = recv.getRuntime();
         int len = ARG_SIZE;
 
         if (args.length == 10) {
             args = new IRubyObject[] { args[5], args[4], args[3], args[2], args[1], args[0], runtime.getNil() };
         } else {
             // MRI accepts additional wday argument which appears to be ignored.
             len = args.length;
 
             if (len < ARG_SIZE) {
                 IRubyObject[] newArgs = new IRubyObject[ARG_SIZE];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
                 for (int i = len; i < ARG_SIZE; i++) {
                     newArgs[i] = runtime.getNil();
                 }
                 args = newArgs;
                 len = ARG_SIZE;
             }
         }
 
         if (args[0] instanceof RubyString) {
             args[0] = RubyNumeric.str2inum(runtime, (RubyString) args[0], 10, false);
         }
 
         int year = (int) RubyNumeric.num2long(args[0]);
         int month = 1;
 
         if (len > 1) {
             if (!args[1].isNil()) {
                 IRubyObject tmp = args[1].checkStringType();
                 if (!tmp.isNil()) {
                     String monthString = tmp.toString().toLowerCase();
                     Integer monthInt = MONTHS_MAP.get(monthString);
 
                     if (monthInt != null) {
                         month = monthInt;
                     } else {
                         try {
                             month = Integer.parseInt(monthString);
                         } catch (NumberFormatException nfExcptn) {
                             throw runtime.newArgumentError("Argument out of range.");
                         }
                     }
                 } else {
                     month = (int) RubyNumeric.num2long(args[1]);
                 }
             }
             if (1 > month || month > 12) {
                 throw runtime.newArgumentError("Argument out of range: for month: " + month);
             }
         }
 
         int[] int_args = { 1, 0, 0, 0, 0, 0 };
 
         for (int i = 0; int_args.length >= i + 2; i++) {
             if (!args[i + 2].isNil()) {
                 if (!(args[i + 2] instanceof RubyNumeric)) {
                     args[i + 2] = args[i + 2].callMethod(
                             runtime.getCurrentContext(), "to_i");
                 }
 
                 long value = RubyNumeric.num2long(args[i + 2]);
                 if (time_min[i] > value || value > time_max[i]) {
                     throw runtime.newArgumentError("argument out of range.");
                 }
                 int_args[i] = (int) value;
             }
         }
 
         if (0 <= year && year < 39) {
             year += 2000;
         } else if (69 <= year && year < 139) {
             year += 1900;
         }
 
         DateTimeZone dtz;
         if (gmt) {
             dtz = DateTimeZone.UTC;
         } else {
             dtz = getLocalTimeZone(runtime);
         }
 
         DateTime dt;
         // set up with min values and then add to allow rolling over
         try {
             dt = new DateTime(year, 1, 1, 0, 0 , 0, 0, dtz);
 
             dt = dt.plusMonths(month - 1)
                     .plusDays(int_args[0] - 1)
                     .plusHours(int_args[1])
                     .plusMinutes(int_args[2])
                     .plusSeconds(int_args[3]);
         } catch (org.joda.time.IllegalFieldValueException e) {
             throw runtime.newArgumentError("time out of range");
         }
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, dt);
         // Ignores usec if 8 args (for compatibility with parsedate) or if not supplied.
         if (args.length != 8 && !args[6].isNil()) {
             int usec = int_args[4] % 1000;
             int msec = int_args[4] / 1000;
 
             if (int_args[4] < 0) {
                 msec -= 1;
                 usec += 1000;
             }
             time.dt = dt.withMillis(dt.getMillis() + msec);
             time.setUSec(usec);
         }
 
         time.callInit(IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         return time;
     }
 }
diff --git a/src/org/jruby/runtime/builtin/InstanceVariables.java b/src/org/jruby/runtime/builtin/InstanceVariables.java
index 086d3ec277..df20b1fcef 100644
--- a/src/org/jruby/runtime/builtin/InstanceVariables.java
+++ b/src/org/jruby/runtime/builtin/InstanceVariables.java
@@ -1,90 +1,95 @@
 /*
  * To change this template, choose Tools | Templates
  * and open the template in the editor.
  */
 
 package org.jruby.runtime.builtin;
 
 import java.util.List;
 
 /**
  * Interface that represents the instance variable aspect of Ruby
  * objects.
  *
  * @author headius
  */
 public interface InstanceVariables {
     //
     // INSTANCE VARIABLE METHODS
     //
 
     /**
      * Returns true if object has the named instance variable.
      * 
      * @param name the name of an instance variable
      * @return true if object has the named instance variable.
      */
     boolean hasInstanceVariable(String name);
 
     /**
      * Returns true if object has the named instance variable. The
      * supplied name <em>must</em> have been previously interned.
      * 
      * @param internedName the interned name of an instance variable
      * @return true if object has the named instance variable, else false
      */
     boolean fastHasInstanceVariable(String internedName);
     
     /**
      * Returns the named instance variable if present, else null. 
      * 
      * @param name the name of an instance variable
      * @return the named instance variable if present, else null
      */
     IRubyObject getInstanceVariable(String name);
 
     /**
      * Returns the named instance variable if present, else null. The
      * supplied name <em>must</em> have been previously interned.
      * 
      * @param internedName the interned name of an instance variable
      * @return he named instance variable if present, else null
      */
     IRubyObject fastGetInstanceVariable(String internedName);
 
     /**
      * Sets the named instance variable to the specified value.
      * 
      * @param name the name of an instance variable
      * @param value the value to be set
      */    
     IRubyObject setInstanceVariable(String name, IRubyObject value);
 
     /**
      * Sets the named instance variable to the specified value. The
      * supplied name <em>must</em> have been previously interned.
      * 
      * @param internedName the interned name of an instance variable
      * @param value the value to be set
      */
     IRubyObject fastSetInstanceVariable(String internedName, IRubyObject value);
 
     /**
      * Removes the named instance variable, if present, returning its
      * value.
      * 
      * @param name the name of the variable to remove
      * @return the value of the remove variable, if present; else null
      */
     IRubyObject removeInstanceVariable(String name);
 
     /**
      * @return instance variables
      */
     List<Variable<IRubyObject>> getInstanceVariableList();
 
     /**
      * @return instance variable names
      */
     List<String> getInstanceVariableNameList();
+
+    /**
+     * Copies all instance variables from the given object into the receiver
+     */
+    void copyInstanceVariablesInto(InstanceVariables other);
 }
diff --git a/src/org/jruby/runtime/marshal/UnmarshalStream.java b/src/org/jruby/runtime/marshal/UnmarshalStream.java
index bb2751d84e..0d971f9b7a 100644
--- a/src/org/jruby/runtime/marshal/UnmarshalStream.java
+++ b/src/org/jruby/runtime/marshal/UnmarshalStream.java
@@ -1,354 +1,364 @@
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
 
 import java.io.BufferedInputStream;
 import java.io.EOFException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jruby.Ruby;
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
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.Variable;
 import org.jruby.runtime.component.VariableEntry;
 import org.jruby.util.ByteList;
 
 /**
  * Unmarshals objects from strings or streams in Ruby's marsal format.
  *
  * @author Anders
  */
 public class UnmarshalStream extends BufferedInputStream {
     protected final Ruby runtime;
     private final UnmarshalCache cache;
     private final IRubyObject proc;
 
     public UnmarshalStream(Ruby runtime, InputStream in, IRubyObject proc) throws IOException {
         super(in);
         this.runtime = runtime;
         this.cache = new UnmarshalCache(runtime);
         this.proc = proc;
 
         in.read(); // Major
         in.read(); // Minor
     }
 
     public IRubyObject unmarshalObject() throws IOException {
         int type = readUnsignedByte();
         IRubyObject result;
         if (cache.isLinkType(type)) {
             result = cache.readLink(this, type);
         } else {
             result = unmarshalObjectDirectly(type);
         }
         return result;
     }
 
     public void registerLinkTarget(IRubyObject newObject) {
         if (MarshalStream.shouldBeRegistered(newObject)) {
             cache.register(newObject);
         }
     }
 
     public static RubyModule getModuleFromPath(Ruby runtime, String path) {
         RubyModule value = runtime.getClassFromPath(path);
         if (!value.isModule()) throw runtime.newArgumentError(path + " does not refer module");
         return value;
     }
 
     public static RubyClass getClassFromPath(Ruby runtime, String path) {
         RubyModule value = runtime.getClassFromPath(path);
         if (!value.isClass()) throw runtime.newArgumentError(path + " does not refer class");
         return (RubyClass)value;
     }
 
+    boolean ivarsWaiting = false;
+
     private IRubyObject unmarshalObjectDirectly(int type) throws IOException {
     	IRubyObject rubyObj = null;
         switch (type) {
             case 'I':
+                ivarsWaiting = true;
                 rubyObj = unmarshalObject();
-                defaultVariablesUnmarshal(rubyObj);
+                if (ivarsWaiting) {
+                    defaultVariablesUnmarshal(rubyObj);
+                }
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
             case 'e':
                 RubySymbol moduleName = (RubySymbol) unmarshalObject();
                 RubyModule tp = null;
                 try {
                     tp = runtime.getClassFromPath(moduleName.asJavaString());
                 } catch (RaiseException e) {
                     if (runtime.fastGetModule("NameError").isInstance(e.getException())) {
                         throw runtime.newArgumentError("undefined class/module " + moduleName.asJavaString());
                     } 
                     throw e;
                 }
 
                 rubyObj = unmarshalObject();
                 
                 tp.extend_object(rubyObj);
                 tp.callMethod(runtime.getCurrentContext(),"extended", rubyObj);
                 break;
             case 'l' :
                 rubyObj = RubyBignum.unmarshalFrom(this);
                 break;
             case 'S' :
                 rubyObj = RubyStruct.unmarshalFrom(this);
                 break;
             case 'o' :
                 rubyObj = defaultObjectUnmarshal();
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
         
         if (proc != null && type != ':') {
             // call the proc, but not for symbols
             RuntimeHelpers.invoke(getRuntime().getCurrentContext(), proc, "call", rubyObj);
         }
         return rubyObj;
     }
 
 
     public Ruby getRuntime() {
         return runtime;
     }
 
     public int readUnsignedByte() throws IOException {
         int result = read();
         if (result == -1) {
             throw new EOFException("Unexpected end of stream");
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
 
     public ByteList unmarshalString() throws IOException {
         int length = unmarshalInt();
         byte[] buffer = new byte[length];
         
         // FIXME: sooper inefficient, but it's working better...
         int b = 0;
         int i = 0;
         while (i < length && (b = read()) != -1) {
             buffer[i++] = (byte)b;
         }
         if (i < length) {
             throw getRuntime().newArgumentError("marshal data too short");
         }
         return new ByteList(buffer,false);
     }
 
     public int unmarshalInt() throws IOException {
         int c = readSignedByte();
         if (c == 0) {
             return 0;
         } else if (5 < c && c < 128) {
             return c - 5;
         } else if (-129 < c && c < -5) {
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
 
     private IRubyObject defaultObjectUnmarshal() throws IOException {
         RubySymbol className = (RubySymbol) unmarshalObject();
 
         RubyClass type = null;
         try {
             type = getClassFromPath(runtime, className.toString());
         } catch (RaiseException e) {
             if (runtime.fastGetModule("NameError").isInstance(e.getException())) {
                 throw runtime.newArgumentError("undefined class/module " + className.asJavaString());
             } 
                 
             throw e;
         }
 
         assert type != null : "type shouldn't be null.";
 
         IRubyObject result = (IRubyObject)type.unmarshal(this);
 
         return result;
     }
     
     public void defaultVariablesUnmarshal(IRubyObject object) throws IOException {
         int count = unmarshalInt();
-
+        
         List<Variable<IRubyObject>> attrs = new ArrayList<Variable<IRubyObject>>(count);
         
-        for (int i = count; --i >= 0; ) {            
+        for (int i = count; --i >= 0; ) {
             String name = unmarshalObject().asJavaString();
             IRubyObject value = unmarshalObject();
             attrs.add(new VariableEntry<IRubyObject>(name, value));
         }
         
         object.syncVariables(attrs);
     }
     
     
     private IRubyObject uclassUnmarshall() throws IOException {
     	RubySymbol className = (RubySymbol)unmarshalObject();
     	
     	RubyClass type = (RubyClass)runtime.getClassFromPath(className.asJavaString());
     	
         // All "C" marshalled objects descend from core classes, which are all RubyObject
     	RubyObject result = (RubyObject)unmarshalObject();
     	
     	result.setMetaClass(type);
     	
     	return result;
     }
 
     private IRubyObject userUnmarshal() throws IOException {
         String className = unmarshalObject().asJavaString();
         ByteList marshaled = unmarshalString();
         RubyModule classInstance = findClass(className);
         if (!classInstance.respondsTo("_load")) {
             throw runtime.newTypeError("class " + classInstance.getName() + " needs to have method `_load'");
         }
+        RubyString data = RubyString.newString(getRuntime(), marshaled);
+        if (ivarsWaiting) {
+            defaultVariablesUnmarshal(data);
+            ivarsWaiting = false;
+        }
         IRubyObject result = classInstance.callMethod(getRuntime().getCurrentContext(),
-            "_load", RubyString.newString(getRuntime(), marshaled));
+            "_load", data);
         registerLinkTarget(result);
         return result;
     }
 
     private IRubyObject userNewUnmarshal() throws IOException {
         String className = unmarshalObject().asJavaString();
         RubyClass classInstance = findClass(className);
         IRubyObject result = classInstance.allocate();
         registerLinkTarget(result);
         IRubyObject marshaled = unmarshalObject();
         result.callMethod(getRuntime().getCurrentContext(),"marshal_load", marshaled);
         return result;
     }
 
     private RubyClass findClass(String className) {
         RubyModule classInstance;
         try {
             classInstance = runtime.getClassFromPath(className);
         } catch (RaiseException e) {
             if (runtime.getModule("NameError").isInstance(e.getException())) {
                 throw runtime.newArgumentError("undefined class/module " + className);
             } 
             throw e;
         }
         if (! (classInstance instanceof RubyClass)) {
             throw runtime.newArgumentError(className + " does not refer class"); // sic
         }
         return (RubyClass) classInstance;
     }
 }
diff --git a/test/testMarshal.rb b/test/testMarshal.rb
index 1ed2a65311..24af33e6aa 100644
--- a/test/testMarshal.rb
+++ b/test/testMarshal.rb
@@ -1,453 +1,484 @@
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
 test_marshal("l+\f\313\220\263z\e\330p\260\200-\326\311\264\000",
              14323534664547457526224437612747)
 test_marshal("l+\n\001\000\001@\000\000\000\000@\000",
              1 + (2 ** 16) + (2 ** 30) + (2 ** 70))
 test_marshal("l+\n6\361\3100_/\205\177Iq",
              534983213684351312654646)
 test_marshal("l-\n6\361\3100_/\205\177Iq",
              -534983213684351312654646)
 test_marshal("l+\n\331\347\365%\200\342a\220\336\220",
              684126354563246654351321)
 test_marshal("l+\vIZ\210*,u\006\025\304\016\207\001",
             472759725676945786624563785)
 
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
 
 test_exception(ArgumentError) { Marshal.load("\004\010U:\eCompletelyUnknownClass\"\nboing") }
 
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
 test_equal(x, Marshal.load(Marshal.dump(x)))
 
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
 test_equal("foo", Marshal.load(Marshal.dump(x)).default)
 
 # Range tests
 x = 1..10
 y = Marshal.load(Marshal.dump(x))
 test_equal(x, y)
 test_equal(x.class, y.class)
 test_no_exception {
   test_equal(10, y.max)
   test_equal(1, y.min)
   test_equal(false, y.exclude_end?)
   y.each {}
 }
 z = Marshal.dump(x)
 test_ok(z.include?("excl"))
 test_ok(z.include?("begin"))
 test_ok(z.include?("end"))
 
 def test_core_subclass_marshalling(type, *init_args)
   my_type = nil
   eval <<-EOS
   class My#{type} < #{type}
     attr_accessor :foo
     def initialize(*args)
       super
       @foo = "hello"
     end
   end
   my_type = My#{type}
   EOS
 
   x = my_type.new(*init_args)
   y = Marshal.load(Marshal.dump(x))
   test_equal(my_type, y.class)
   test_no_exception {
     test_equal(x.to_s, y.to_s)
     test_equal("hello", y.foo)
   }
 end
 
 test_core_subclass_marshalling(Range, 1, 10)
 test_core_subclass_marshalling(Array, 0)
 test_core_subclass_marshalling(Hash, 5)
 test_core_subclass_marshalling(Regexp, //)
 
 # FIXME: this isn't working because we intercept system calls to "ruby" and run JRuby...
 =begin
 ruby_available = (`ruby -v`[0..3] == "ruby")
 
 if ruby_available
   def test_dump_against_ruby(eval_string)
     ruby_command = "ruby -e 'p Marshal.dump(eval\"#{eval_string}\")'"
     ruby_output = "Ruby output: " + system(ruby_command)
 
     test_equal(ruby_output, Marshal.dump(eval(eval_string)))
   end
 else
   def test_dump_against_ruby(eval_string)
     warn "ruby interpreter not available, skipping test
   end
 end
 
 test_dump_against_ruby("Object.new")
 =end
 
 # Time is user-marshalled, so ensure it's being marshalled correctly
 x = Time.now
 y = Marshal.dump([x,x])
 # symlink for second time object
 test_equal(6, y[-1])
 test_equal([x, x], Marshal.load(y))
 
 # User-marshalled classes should marshal singleton objects as the original class
 class Special  
   def initialize(valuable)
     @valuable = valuable
   end
 
   def _dump(depth)
     @valuable.to_str
   end
 
   def Special._load(str)
     result = Special.new(str);
   end
 end
 
 a = Special.new("Hello, World")
 class << a
   def newMeth
     puts "HELLO"
   end
 end
 data = Marshal.dump(a)
 test_equal("\004\bu:\fSpecial\021Hello, World", data)
 test_no_exception { obj = Marshal.load(data) }
 
 class Aaaa < Array
   attr_accessor :foo
 end
 a = Aaaa.new
 a.foo = :Aaaa
 test_marshal("IC:\tAaaa[\000\006:\t@foo;\000",a)
 
 # Check that exception message and backtrace are preserved
 class SomeException < Exception
   def initialize(message)
     super(message)
   end
   # Also check that subclass ivars are preserved
   attr_accessor :ivar
 end
 
 # Create an exception, set a fixed backtrace
 e1 = SomeException.new("a message")
 e1.set_backtrace ["line 1", "line 2"]
 e1.ivar = 42
 e2 = Marshal.load(Marshal.dump(e1))
 test_equal("a message", e2.message)
 test_equal(e1.backtrace, e2.backtrace)
 test_equal(42, e2.ivar)
 
 # The following dump is generated by MRI
 s = "\004\010o:\022SomeException\010:\tmesg\"\016a message:\abt[\a\"\13line 1\"\13line 2:\n@ivari/"
 e3 = Marshal.load(s)
 # Check that the MRI format loads correctly
 test_equal("a message", e3.message)
 test_equal(e1.backtrace, e3.backtrace)
 test_equal(42, e3.ivar)
 
 # Check that numbers of all sizes don't make link indexes break
 fixnum = 12345
 mri_bignum = 1234567890
 jruby_bignum = 12345678901234567890
 s = "should be cached"
 a = [fixnum,mri_bignum,jruby_bignum,s]
 a = a + a
 dumped_by_mri = "\004\b[\ri\00290l+\a\322\002\226Il+\t\322\n\037\353\214\251T\253\"\025should be cachedi\00290l+\a\322\002\226I@\a@\b"
 test_no_exception { test_equal(a, Marshal.load(Marshal.dump(a))) }
 test_no_exception { test_equal(a, Marshal.load(dumped_by_mri)) }
 test_no_exception { test_equal(dumped_by_mri, Marshal.dump(a)) }
 
 require 'stringio'
 class MTStream < StringIO
   attr :binmode_called
 
   def binmode
     @binmode_called = true
   end
 end
 
 class BinmodeLessMTStream < StringIO
   undef_method :binmode
 end
 
 # Checking for stream
 begin
   Marshal.dump("hi", :not_an_io)
 rescue TypeError
 else
   test_fail
 end
 
 # Writing to non-IO stream
 stream = MTStream.new
 Marshal.dump("hi", stream)
 test_ok(stream.size > 0)
 
 # Calling binmode if available
 stream = MTStream.new
 Marshal.dump("hi", stream)
 test_ok(stream.binmode_called)
 
 # Ignoring binmode if unavailable
 stream = BinmodeLessMTStream.new
 Marshal.dump("hi", stream)
 
 # Loading from non-IO stream
 stream = MTStream.new
 Marshal.dump("hi", stream)
 stream.rewind
 test_equal("hi", Marshal.load(stream))
 
 # Setting binmode on input
 stream = MTStream.new
 Marshal.dump("hi", stream)
 stream.rewind
 s = stream.read
 in_stream = MTStream.new
 in_stream.write(s)
 in_stream.rewind
 Marshal.load(in_stream)
 test_ok(in_stream.binmode_called)
 
 # thread isn't marshalable
 test_exception(TypeError) { Marshal.dump(Thread.new {}) }
 
 # time marshalling
 unpacked_marshaled_time = [4, 8, 117, 58, 9, 84, 105, 109, 101, 13, 247, 239, 26, 128, 57, 48, 112, 57]
 actual_time = Time.utc(2007, 12, 31, 23, 14, 23, 12345)
 test_equal(
   unpacked_marshaled_time,
   Marshal.dump(actual_time).unpack('C*'))
 test_equal(
   0,
   actual_time <=> Marshal.load(unpacked_marshaled_time.pack('C*')))
 
 # JRUBY-2392
 time = Time.now
 is_utc = time.to_s =~ /UTC/
 if (is_utc)
   test_ok(Marshal.load(Marshal.dump(time)).to_s =~ /UTC/)
 else
   test_ok(!(Marshal.load(Marshal.dump(time)).to_s =~ /UTC/))
 end
 
 # JRUBY-2345
 begin
   file_name = "test-file-tmp"
   f = File.new(file_name, "w+")
   test_exception(EOFError) { Marshal.load(f) }
 ensure
   f.close
   File.delete(file_name)
 end
 
 test_exception(ArgumentError) { Marshal.load("\004\b\"\b") }
 
 # borrowed from MRI 1.9 test:
 class C
   def initialize(str)
     @str = str
   end
   def _dump(limit)
     @str
   end
   def self._load(s)
     new(s)
   end
 end
 test_exception(ArgumentError) {
   (data = Marshal.dump(C.new("a")))[-2, 1] = "\003\377\377\377"
   Marshal.load(data)
 }
+
+# JRUBY-2975: Overriding Time._dump does not behave the same as MRI
+class Time
+  class << self
+    alias_method :_original_load, :_load
+    def _load(marshaled_time)
+      time = _original_load(marshaled_time)
+      utc = time.send(:remove_instance_variable, '@marshal_with_utc_coercion')
+      utc ? time.utc : time
+    end
+  end
+
+  alias_method :_original_dump, :_dump
+  def _dump(*args)
+    obj = self.frozen? ? self.dup : self
+    obj.instance_variable_set('@marshal_with_utc_coercion', utc?)
+    obj._original_dump(*args)
+  end
+end
+
+t = Time.local(2000).freeze
+t2 = Marshal.load(Marshal.dump(t))
+test_equal t, t2
+
+# reset _load and _dump
+class Time
+  class << self
+    alias_method :load, :_original_load
+  end
+  alias_method :dump, :_original_dump
+end
\ No newline at end of file
