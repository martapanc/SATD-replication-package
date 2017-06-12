diff --git a/src/builtin/javasupport.rb b/src/builtin/javasupport.rb
index f5bdb16c8a..51ae648c98 100644
--- a/src/builtin/javasupport.rb
+++ b/src/builtin/javasupport.rb
@@ -1,1167 +1,1167 @@
 ###### BEGIN LICENSE BLOCK ######
 # Version: CPL 1.0/GPL 2.0/LGPL 2.1
 #
 # The contents of this file are subject to the Common Public
 # License Version 1.0 (the "License"); you may not use this file
 # except in compliance with the License. You may obtain a copy of
 # the License at http://www.eclipse.org/legal/cpl-v10.html
 #
 # Software distributed under the License is distributed on an "AS
 # IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 # implied. See the License for the specific language governing
 # rights and limitations under the License.
 #
 # Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 # Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
 # Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
 # Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
 # Copyright (C) 2006 Michael Studman <me@michaelstudman.com>
 # Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
 # Copyright (C) 2007 William N Dortch <bill.dortch@gmail.com>
 # 
 # Alternatively, the contents of this file may be used under the terms of
 # either of the GNU General Public License Version 2 or later (the "GPL"),
 # or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 # in which case the provisions of the GPL or the LGPL are applicable instead
 # of those above. If you wish to allow use of your version of this file only
 # under the terms of either the GPL or the LGPL, and not to allow others to
 # use your version of this file under the terms of the CPL, indicate your
 # decision by deleting the provisions above and replace them with the notice
 # and other provisions required by the GPL or the LGPL. If you do not delete
 # the provisions above, a recipient may use your version of this file under
 # the terms of any one of the CPL, the GPL or the LGPL.
 ###### END LICENSE BLOCK ######
 
 # JavaProxy is a base class for all Java Proxies.  A Java proxy is a high-level abstraction
 # that wraps a low-level JavaObject with ruby methods capable of dispatching to the JavaObjects
 # native java methods.  
 class JavaProxy
   class << self
     attr :java_class, true
 
     # Allocate a new instance for the provided java_object.  This is like a second 'new' to
     # by-pass any 'initialize' methods we may have created for the proxy class (we already
     # have the instance for the proxy...We don't want to re-create it).
     def new_instance_for(java_object)
       new_instance = allocate
       new_instance.java_object = java_object
       new_instance
     end
     
     # Carry the Java class as a class variable on the derived classes too, otherwise 
     # JavaProxy.java_class won't work.
     def inherited(subclass)
       subclass.java_class = self.java_class unless subclass.java_class
       super
     end
     
     def singleton_class
       class << self; self; end 
     end
 
     # If the proxy class itself is passed as a parameter this will be called by Java#ruby_to_java    
     def to_java_object
       self.java_class
     end
 
     def [](*args)
       if args.length > 0
         # array creation should use this variant
         ArrayJavaProxyCreator.new(java_class,*args)      
       else
         # keep this variant for kind_of? testing
       JavaUtilities.get_proxy_class(java_class.array_class)
     end
     end
     
     def setup
       unless java_class.array?
         setup_attributes
         setup_class_methods
         setup_constants
         setup_inner_classes
         setup_instance_methods
       end
     end
     
     def setup_attributes
       instance_methods = java_class.java_instance_methods.collect! {|m| m.name}
       java_class.fields.select {|field| field.public? && !field.static? }.each do |attr|
         name = attr.name
       
         # Do not add any constants that have the same name as an existing method
         next if instance_methods.detect {|m| m == name }
 
         class_eval do
           define_method(name) do |*args| Java.java_to_ruby(attr.value(@java_object)); end
         end
 
         next if attr.final?
 
         class_eval do
           define_method("#{name}=") do |*args|
             Java.java_to_ruby(attr.set_value(@java_object, Java.ruby_to_java(args.first)))
           end
         end
       end
     end
 
     def setup_class_methods
       java_class.java_class_methods.select { |m| m.public? }.group_by { |m| m.name 
       }.each do |name, methods|
         if methods.length == 1
           method = methods.first
           singleton_class.send(:define_method, name) do |*args|
             args.collect! { |v| Java.ruby_to_java(v) }
             Java.java_to_ruby(method.invoke_static(*args))
           end
         else
           singleton_class.send(:define_method, name) do |*args|
             args.collect! { |v| Java.ruby_to_java(v) }
             Java.java_to_ruby(JavaUtilities.matching_method(methods, args).invoke_static(*args))
           end
         end
         singleton_class.instance_eval do
           alias_method name.gsub(/([a-z])([A-Z])/, '\1_\2').downcase, name
         end
       end
     end
     
     def setup_constants
       fields = java_class.fields
       class_methods = java_class.java_class_methods.collect! { |m| m.name } 
 
       fields.each do |field|
         if field.static? and field.final? and JavaUtilities.valid_constant_name?(field.name)
           const_set(field.name, Java.java_to_ruby(field.static_value))
         elsif (field.static? and field.final? and !JavaUtilities.valid_constant_name?(field.name)) or
             (field.public? and field.static? and !field.final? && !JavaUtilities.valid_constant_name?(field.name))
             
           next if class_methods.detect {|m| m == field.name } 
           class_eval do
             singleton_class.send(:define_method, field.name) do |*args|
               Java.java_to_ruby(java_class.field(field.name).static_value)
             end
           end
 
         end
       end
     end
 
     def setup_inner_classes
       def const_missing(constant)
         inner_class = nil
         begin
           inner_class = Java::JavaClass.for_name(java_class.name + '$' + constant.to_s)
         rescue NameError
           return super
         end
         JavaUtilities.create_proxy_class(constant, inner_class, self)
       end
     end
     
     def setup_instance_methods
       java_class.define_instance_methods_for_proxy(self)
     end
   end
   
   attr :java_object, true
 
   def java_class
     self.class.java_class
   end
 
   def ==(rhs)
     java_object == rhs
   end
   
   def to_s
     java_object.to_s
   end
 
   def eql?(rhs)
     self == rhs
   end
   
   def equal?(rhs)
     java_object.equal?(rhs)
   end
   
   def hash()
     java_object.hash()
   end
   
   def to_java_object
     java_object
   end
 
   def synchronized
     java_object.synchronized { yield }
   end
 end
 
 # This class enables the syntax arr = MyClass[n][n]...[n].new
 class ArrayJavaProxyCreator
   def initialize(java_class,*args)
     @java_class = java_class
     @dimensions = []
     extract_dimensions(args)
       end
   
   def [](*args)
     extract_dimensions(args)
     self
     end
   
   private
   def extract_dimensions(args)
     unless args.length > 0
       raise ArgumentError,"empty array dimensions specified"    
     end
     args.each do |arg|
       unless arg.kind_of?(Fixnum)
         raise ArgumentError,"array dimension length must be Fixnum"    
       end
       @dimensions << arg
     end  
     end
   public
   
   def new(fill_value=nil)
     array = @java_class.new_array(@dimensions)
     array_class = @java_class.array_class
     (@dimensions.length-1).times do
       array_class = array_class.array_class    
     end
     proxy_class = JavaUtilities.get_proxy_class(array_class)
     proxy = proxy_class.new(array)
     if fill_value
       converter = JavaArrayUtilities.get_converter(@java_class)
       JavaArrayUtilities.fill_array(proxy,@dimensions,converter.call(fill_value))
     end
     proxy  
   end
 end
 
 class ArrayJavaProxy < JavaProxy
   include Enumerable
 
   class << self  
     alias_method :new_proxy, :new
 
     # the 'size' variant should be phased out ASAP
     def new(java_array_or_size, fill_value=nil)
       proxy = new_proxy
       if java_array_or_size.kind_of?(Java::JavaArray)
         proxy.java_object = java_array_or_size
         return proxy      
       end
       puts " ** Warning: the 'ClassName[].new(size)' form is deprecated; use ClassName[size].new"
       # sort out the mess of the previous approach
       size = java_array_or_size
       component_type = proxy.java_class.component_type
       component_type = component_type.component_type while component_type.array?
       array = component_type.new_array(size)
       # get the right proxy class for the number of array dimensions
       array_class = component_type.array_class
       if size.kind_of?(Array)
         (size.length-1).times do
           array_class = array_class.array_class    
         end
       end
       proxy_class = JavaUtilities.get_proxy_class(array_class)
       proxy = proxy_class.new(array)
       if fill_value
         converter = JavaArrayUtilities.get_converter(component_type)
         JavaArrayUtilities.fill_array(proxy,size,converter.call(fill_value))
       end
       proxy
     end
     
   end
 
   def length()
     java_object.length
   end
   
   def [](index)
     Java.java_to_ruby(java_object[index])
   end
   
   def []=(index, value)
     #java_object[index] = Java.ruby_to_java(value)
 
     # I don't know if this (instance var) will cause any problems. If so,
     # I can call JavaArrayUtilities.convert_to_type on every call to []=, but
     # I'd rather keep the converter locally for better performance. -BD
     @converter ||= JavaArrayUtilities.get_converter(java_class.component_type)
     java_object[index] = @converter.call(value)
   end
   
   def each()
     block = Proc.new
     for index in 0...java_object.length
       block.call(self[index])
     end
   end
   
   def to_a
     JavaArrayUtilities.java_to_ruby(self)
   end
 
   alias_method :to_ary, :to_a
 
 end
 
 class InterfaceJavaProxy < JavaProxy
   class << self  
     alias_method :new_proxy, :new
 
     def new(*args, &block)
       proxy = new_proxy(*args, &block)
       proxy.java_object = Java.new_proxy_instance(proxy.class.java_class) { |proxy2, method, *args|
         args.collect! { |arg| Java.java_to_ruby(arg) }
         Java.ruby_to_java(proxy.send(method.name, *args))
       }
       proxy
     end
   end
     
   def self.impl(*meths, &block)
     block = lambda {|*args| send(:method_missing, *args) } unless block
 
     Class.new(self) do
       define_method(:method_missing) do |name, *args|
         return block.call(name, *args) if meths.empty? || meths.include?(name)
         super
       end
     end.new
   end
 end
 
 class ConcreteJavaProxy < JavaProxy
   class << self
     alias_method :new_proxy, :new
 
     def new(*args)
       proxy = new_proxy
       constructors = proxy.java_class.constructors.select {|c| c.arity == args.length }
       raise NameError.new("wrong # of arguments for constructor") if constructors.empty?
       args.collect! { |v| Java.ruby_to_java(v) }
       proxy.java_object = JavaUtilities.matching_method(constructors, args).new_instance(*args)
       proxy
     end
   end
 end
 
 module JavaUtilities
   @proxy_classes = {}
   @proxy_extenders = []
   
   def JavaUtilities.add_proxy_extender(extender)
     @proxy_extenders << extender
     # Already loaded proxies should be extended if they qualify
     @proxy_classes.values.each {|proxy_class| extender.extend_proxy(proxy_class) }
   end
   
   def JavaUtilities.extend_proxy(java_class_name, &block)
 	add_proxy_extender JavaInterfaceExtender.new(java_class_name, &block)
   end
 
   def JavaUtilities.valid_constant_name?(name)
     return false if name.empty?
     first_char = name[0..0]
     first_char == first_char.upcase && first_char != first_char.downcase
   end
 
   def JavaUtilities.get_proxy_class(java_class)
     java_class = Java::JavaClass.for_name(java_class) if java_class.kind_of?(String)
-    class_id = java_class.id
+    class_id = java_class.object_id
 
     java_class.synchronized do
       unless @proxy_classes[class_id]
         if java_class.interface?
           base_type = InterfaceJavaProxy
         elsif java_class.array?
           base_type = ArrayJavaProxy
         else
           base_type = ConcreteJavaProxy
         end
         
         proxy_class = Class.new(base_type) { 
           self.java_class = java_class 
           if base_type == ConcreteJavaProxy
             class << self
               def inherited(subclass)
                 super
                 JavaUtilities.setup_java_subclass(subclass, java_class)
               end
             end
           end
         }
         @proxy_classes[class_id] = proxy_class
         # We do not setup the proxy before we register it so that same-typed constants do
         # not try and create a fresh proxy class and go into an infinite loop
         proxy_class.setup
         @proxy_extenders.each {|e| e.extend_proxy(proxy_class)}
       end
     end
     @proxy_classes[class_id]
   end
 
   def JavaUtilities.setup_java_subclass(subclass, java_class)
   
   		# add new class-variable to hold the JavaProxyClass instance
   		subclass.class.send :attr, :java_proxy_class, true
 
     class << subclass
       def new(*args)
         new_proxy *args
       end
     end
 
         # override 
   		subclass.send(:define_method, "initialize") {|*args|
 		    constructors = self.class.java_proxy_class.constructors.select {|c| c.arity == args.length }
 		    raise NameError.new("wrong # of arguments for constructor") if constructors.empty?
 		    args.collect! { |v| Java.ruby_to_java(v) }
 			self.java_object = JavaUtilities.matching_method(constructors, args).new_instance(args) { |proxy, method, *args|
               args.collect! { |arg| Java.java_to_ruby(arg) } 
               result = send(method.name, *args)
 		      Java.ruby_to_java(result)
 		    } 
 		}
 		
 		subclass.send(:define_method, "setup_instance_methods") {
 			self.java_proxy_class.define_instance_methods_for_proxy(subclass)
 		}
 				  
   		subclass.java_proxy_class = Java::JavaProxyClass.get(java_class)
   end
 
   def JavaUtilities.get_java_class(name)
     begin
       return Java::JavaClass.for_name(name)
     rescue NameError
       return nil
     end
   end
   
   def JavaUtilities.create_proxy_class(constant, java_class, mod)
     mod.const_set(constant.to_s, get_proxy_class(java_class))
   end
   
   def JavaUtilities.print_class(java_type, indent="")
      while (!java_type.nil? && java_type.name != "java.lang.Class")
         puts "#{indent}Name:  #{java_type.name}, access: #{ JavaUtilities.access(java_type) }  Interfaces: "
         java_type.interfaces.each { |i| print_class(i, "  #{indent}") }
         puts "#{indent}SuperClass: "
         print_class(java_type.superclass, "  #{indent}")
         java_type = java_type.superclass
      end
   end
 
   def JavaUtilities.access(java_type)
     java_type.public? ? "public" : (java_type.protected? ? "protected" : "private")
   end
 
   # Wrap a low-level java_object with a high-level java proxy  
   def JavaUtilities.wrap(java_object)
     get_proxy_class(java_object.java_class).new_instance_for(java_object)
   end
 
   @@primitive_matches = {
     'int'     => ['java.lang.Integer','java.lang.Long','java.lang.Short','java.lang.Character'],
     'long'    => ['java.lang.Integer','java.lang.Long','java.lang.Short','java.lang.Character'],
     'short'   => ['java.lang.Integer','java.lang.Long','java.lang.Short','java.lang.Character'],
     'char'    => ['java.lang.Integer','java.lang.Long','java.lang.Short','java.lang.Character'],
     'float'   => ['java.lang.Float','java.lang.Double'],
     'double'  => ['java.lang.Float','java.lang.Double'],
     'boolean' => ['java.lang.Boolean'] }
 
   def JavaUtilities.primitive_match(t1,t2)
     if t1.primitive?
       return (matches = @@primitive_matches[t1.inspect]) && matches.include?(t2.inspect)
     end
     return true
   end
   
   def JavaUtilities.matching_method(methods, args)
     @match_cache ||= {}
 
     arg_types = args.collect {|a| a.java_class }
     
     @match_cache[methods] ||= {}
     method = @match_cache[methods][arg_types]
     return method if method
     
     notfirst = false
     2.times do
       methods.each do |method|
         types = method.argument_types
         # Exact match
         return @match_cache[methods][arg_types] = method if types == arg_types
         
         # Compatible (by inheritance)
         if (types.length == arg_types.length)
           match = true
           0.upto(types.length - 1) do |i|
             match = false unless types[i].assignable_from?(arg_types[i]) && (notfirst || primitive_match(types[i],arg_types[i]))
           end
           return @match_cache[methods][arg_types] = method if match
         end
       end
       notfirst = true
     end
 
     name = methods.first.kind_of?(Java::JavaConstructor) ? 
       "constructor" : "method '" + methods.first.name + "'"
     raise NameError.new("no " + name + " with arguments matching " + arg_types.inspect)
   end
 
   @primitives = {
     :boolean => true,
     :byte => true,
     :char => true,
     :short => true,
     :int => true,
     :long => true,
     :float => true,
     :double => true  
   }
   def JavaUtilities.is_primitive_type(sym)
     @primitives[sym]  
   end
 end
 
 module JavaArrayUtilities
   class ProxyRef
     def initialize(class_name)
       @class_name = class_name
     end
     def new(*args)
       proxy.new(*args).java_object
     end
     def proxy
       @proxy ||= JavaUtilities.get_proxy_class(@class_name)
     end
     def name
       @class_name    
     end
     def valueOf(*args)
       proxy.valueOf(*args).java_object
     end
   end
   Jboolean = ProxyRef.new('boolean')
   Jbyte = ProxyRef.new('byte')
   Jchar = ProxyRef.new('char')
   Jshort = ProxyRef.new('short')
   Jint = ProxyRef.new('int')
   Jlong = ProxyRef.new('long')
   Jfloat = ProxyRef.new('float')
   Jdouble = ProxyRef.new('double')
   JBoolean = ProxyRef.new('java.lang.Boolean')
   JByte = ProxyRef.new('java.lang.Byte')
   JCharacter = ProxyRef.new('java.lang.Character')
   JShort = ProxyRef.new('java.lang.Short')
   JInteger = ProxyRef.new('java.lang.Integer')
   JLong = ProxyRef.new('java.lang.Long')
   JFloat = ProxyRef.new('java.lang.Float')
   JDouble = ProxyRef.new('java.lang.Double')
   JBigDecimal = ProxyRef.new('java.math.BigDecimal')
   JBigInteger = ProxyRef.new('java.math.BigInteger')
   JObject = ProxyRef.new('java.lang.Object')
   JString = ProxyRef.new('java.lang.String')
   JDate = ProxyRef.new('java.util.Date')
   JOFalse = Java.ruby_to_java(false)
   JOTrue = Java.ruby_to_java(true)
   JIntegerMin = -2147483648
   JIntegerMax = 2147483647
   JLongMin = -9223372036854775808
   JLongMax = 9223372036854775807
   SBoolean = 'java.lang.Boolean'.to_sym
   SByte = 'java.lang.Byte'.to_sym
   SCharacter = 'java.lang.Character'.to_sym
   SShort = 'java.lang.Short'.to_sym
   SInteger = 'java.lang.Integer'.to_sym
   SLong = 'java.lang.Long'.to_sym
   SFloat = 'java.lang.Float'.to_sym
   SDouble = 'java.lang.Double'.to_sym
   SBigDecimal = 'java.math.BigDecimal'.to_sym
   SBigInteger = 'java.math.BigInteger'.to_sym
   SObject = 'java.lang.Object'.to_sym
   SString = 'java.lang.String'.to_sym
   SDate = 'java.util.Date'.to_sym
   # *very* loose/eager conversion rules in place here, can tighten them
   # up if need be. -BD
   # the order of evaluation in the converters is important, want to 
   # check the most probable first, then dispense with any unidentified 
   # non-Ruby classes before calling to_i/to_f/to_s. -BD
   @converters = {
     :boolean => Proc.new {|val| 
       if val == false || val.nil?
         JOFalse
       elsif val == true
         JOTrue
       elsif val.kind_of?(Numeric)
         val.to_i == 0 ? JOFalse : JOTrue
       elsif val.kind_of?(String)
         JBoolean.new(val)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_i)
         val.to_i == 0 ? JOFalse : JOTrue
       else
         Java.ruby_to_java(val)
       end
     },
     :byte => Proc.new {|val| 
       if val.kind_of?(Numeric)
         JByte.new(val.to_i)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_i)
         JByte.new(val.to_i)
       else
         Java.ruby_to_java(val)
       end
     },
     :char => Proc.new {|val| 
       if val.kind_of?(Numeric)
         JCharacter.new(val.to_i)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_i)
         JCharacter.new(val.to_i)
       else
         Java.ruby_to_java(val)
       end
     },
     :short => Proc.new {|val| 
       if val.kind_of?(Numeric)
         JShort.new(val.to_i)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_i)
         JShort.new(val.to_i)
       else
         Java.ruby_to_java(val)
       end
     },
     :int => Proc.new {|val| 
       if val.kind_of?(Numeric)
         JInteger.new(val.to_i)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_i)
         JInteger.new(val.to_i)
       else
         Java.ruby_to_java(val)
       end
     },
     :long => Proc.new {|val| 
       if val.kind_of?(Numeric)
         JLong.new(val.to_i)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_i)
         JLong.new(val.to_i)
       else
         Java.ruby_to_java(val)
       end
     },
     :float => Proc.new {|val| 
       if val.kind_of?(Numeric)
         JFloat.new(val.to_f)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_f)
         JFloat.new(val.to_f)
       else
         Java.ruby_to_java(val)
       end
     },
     :double => Proc.new {|val| 
       if val.kind_of?(Numeric)
         JDouble.new(val.to_f)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_f)
         JDouble.new(val.to_f)
       else
         Java.ruby_to_java(val)
       end
     },
     :decimal => Proc.new {|val| 
       if val.kind_of?(Numeric)
         JBigDecimal.valueOf(val)
       elsif val.kind_of?(String)
         JBigDecimal.new(val)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_f)
         JBigDecimal.valueOf(val.to_f)
       elsif val.respond_to?(:to_i)
         JBigDecimal.valueOf(val.to_i)
       else
         Java.ruby_to_java(val)
       end
     },
     :big_int => Proc.new {|val|
       if val.kind_of?(Integer)
         JBigInteger.new(val.to_s)
       elsif val.kind_of?(Numeric)
         JBigInteger.new(val.to_i.to_s)
       elsif val.kind_of?(String)
         JBigInteger.new(val)
       elsif val.kind_of?(JavaProxy)
         Java.ruby_to_java(val)
       elsif val.respond_to?(:to_i)
         JBigInteger.new(val.to_i.to_s)
       else
         Java.ruby_to_java(val)
       end
     },
     :object => Proc.new {|val|
       if val.kind_of?(Integer)
         if val >= JIntegerMin && val <= JIntegerMax
           JInteger.new(val)
         elsif val >= JLongMin && val <= JLongMax
           JLong.new(val)
         else
           JBigInteger.new(val.to_s)
         end
       elsif val.kind_of?(Float)
         JDouble.new(val)
       else
         Java.ruby_to_java(val)
       end
     },
     :string => Proc.new {|val|
       if val.kind_of?(String)
         JString.new(val)
       elsif val.kind_of?(JavaProxy)
         if val.respond_to?(:toString)
           JString.new(val.toString)
         else
           Java.ruby_to_java(val)
         end
       elsif val.respond_to?(:to_s)
         JString.new(val.to_s)
       else
         Java.ruby_to_java(val)
       end
     },
   }
   @converters['boolean'] = @converters['java.lang.Boolean'] = @converters[:Boolean] =
     @converters[SBoolean] = @converters[:boolean]
   @converters['byte'] = @converters['java.lang.Byte'] = @converters[:Byte] =
     @converters[SByte] = @converters[:byte]
   @converters['char'] = @converters['java.lang.Character'] = @converters[:Character] =
     @converters[SCharacter] = @converters[:Char] = @converters[:char]
   @converters['short'] = @converters['java.lang.Short'] = @converters[:Short] =
     @converters[SShort] = @converters[:short]
   @converters['int'] = @converters['java.lang.Integer'] = @converters[:Integer] =
     @converters[SInteger] = @converters[:Int] = @converters[:int]
   @converters['long'] = @converters['java.lang.Long'] = @converters[:Long] =
     @converters[SLong] = @converters[:long]
   @converters['float'] = @converters['java.lang.Float'] = @converters[:Float] =
     @converters[SFloat] = @converters[:float]
   @converters['double'] = @converters['java.lang.Double'] = @converters[:Double] =
     @converters[SDouble] = @converters[:double]
   @converters['java.math.BigDecimal'] = @converters[:BigDecimal] = @converters[:big_decimal]
     @converters[SBigDecimal] = @converters[:decimal]
   @converters['java.math.BigInteger'] = @converters[:BigInteger] = @converters[:big_integer]
     @converters[SBigInteger] = @converters[:big_int]
   @converters['java.lang.Object'] = @converters[:Object] =
     @converters[SObject] = @converters[:object]
   @converters['java.lang.String'] = @converters[:String] =
     @converters[SString] = @converters[:string]
 
   @default_converter = Proc.new {|val| Java.ruby_to_java(val) }
   
   @class_index = {
     :boolean => Jboolean,
     :byte => Jbyte,
     :char => Jchar,
     :short => Jshort,
     :int => Jint,
     :long => Jlong,
     :float => Jfloat,
     :double => Jdouble,
     :Boolean => JBoolean,
     SBoolean => JBoolean,
     :Byte => JByte,
     SByte => JByte,
     :Char => JCharacter,
     :Character => JCharacter,
     SCharacter => JCharacter,
     :Short => JShort,
     SShort => JShort,
     :Int => JInteger,
     :Integer => JInteger,
     SInteger => JInteger,
     :Long => JLong,
     SLong => JLong,
     :Float => JFloat,
     SFloat => JFloat,
     :Double => JDouble,
     SDouble => JDouble,
     :object => JObject,
     :Object => JObject,
     SObject => JObject,
     :string => JString,
     :String => JString,
     SString => JString,
     :decimal => JBigDecimal,
     :big_decimal => JBigDecimal,
     :BigDecimal => JBigDecimal,
     SBigDecimal => JBigDecimal,
     :big_int => JBigInteger,
     :big_integer => JBigInteger,
     :BigInteger => JBigInteger,
     SBigInteger => JBigInteger,
   }
  class << self
   def get_converter(component_type)
     converter = @converters[component_type.name]
     converter ? converter : @default_converter
   end
   
   def convert_to_type(component_type,value)
     get_converter(component_type).call(value)
   end
 
   # this can be expensive, as it must examine every element
   # of every 'dimension' of the Ruby array.  thinking about
   # moving this to Java.
   def dimensions(ruby_array,dims = [],index = 0)
     return [] unless ruby_array.kind_of?(::Array)
     dims << 0 while dims.length <= index 
     dims[index] = ruby_array.length if ruby_array.length > dims[index]
     ruby_array.each do |sub_array|
       next unless sub_array.kind_of?(::Array)
       dims = dimensions(sub_array,dims,index+1)
     end
     dims
   end
   
   def enable_extended_array_support
     return if [].respond_to?(:to_java)
     Array.module_eval {
       def to_java(*args,&block)
         JavaArrayUtilities.ruby_to_java(*(args.unshift(self)),&block)
       end
     }
   end
 
   def disable_extended_array_support
     return unless [].respond_to?(:to_java)
     Array.send(:remove_method,:to_java)
   end
  
   def fill_array(array,dimensions,fill_value)
     dims = dimensions.kind_of?(Array) ? dimensions : [dimensions]
     copy_ruby_to_java(dims,nil,array,nil,fill_value)
   end
   
 private
   def get_class(class_name)
     ref =  @class_index[class_name.to_sym]
     ref ? ref.proxy : JavaUtilities.get_proxy_class(class_name.to_s)
   end
 public
   
   def ruby_to_java(*args,&block)
     return JObject.proxy[].new(0) if args.length == 0
     ruby_array = args[0]
     unless ruby_array.kind_of?(::Array) || ruby_array.nil?
       raise ArgumentError,"invalid arg[0] passed to to_java (#{args[0]})"    
     end
     dims = nil
     fill_value = nil
     index = 1
     if index < args.length
       arg = args[index]
       # the (optional) first arg is class/name. if omitted,
       # defaults to java.lang.Object
       if arg.kind_of?(Class) && arg.respond_to?(:java_class)
         cls = arg
         cls_name = arg.java_class.name
         index += 1
       elsif arg.kind_of?(String) || arg.kind_of?(Symbol)
         cls = get_class(arg)
         unless cls
           raise ArgumentError,"invalid class name (#{arg}) specified for to_java"      
         end
         cls_name = arg
         index += 1
       else
         cls = JObject.proxy
         cls_name = SObject
       end
     else
       cls = JObject.proxy
       cls_name = SObject
     end
     if block
       converter = block
     elsif converter = @converters[cls_name]
     else
       converter = @default_converter
     end
     # the (optional) next arg(s) is dimensions. may be
     # specified as dim1,dim2,...,dimn, or [dim1,dim2,...,dimn]
     # the array version is required if you want to pass a
     # fill value after it
     if index < args.length
       arg = args[index]
       if arg.kind_of?(Fixnum)
         dims = [arg]
         index += 1
         while index < args.length && args[index].kind_of?(Fixnum)
           dims << args[index]
           index += 1        
         end
       elsif arg.kind_of?(::Array)
         dims = arg
         index += 1
         fill_value = converter.call(args[index]) if index < args.length
       elsif arg.nil?
         dims = dimensions(ruby_array) if ruby_array
         index += 1
         fill_value = converter.call(args[index]) if index < args.length
       end
     else
       dims = dimensions(ruby_array) if ruby_array
     end
     dims = [0] unless dims
     java_array = ArrayJavaProxyCreator.new(cls.java_class,*dims).new
     if ruby_array
       copy_ruby_to_java(dims,ruby_array,java_array,converter,fill_value)          
     elsif fill_value
       copy_ruby_to_java(dims,nil,java_array,converter,fill_value)
     end
     java_array
   end
 
 private
   def copy_ruby_to_java(dims,ruby_array,java_array,converter,fill_value)
     if dims.length > 1
       shift_dims = dims[1...dims.length]
       for i in 0...dims[0]
         if ruby_array.kind_of?(::Array)
           ruby_param = ruby_array[i]
         else
           ruby_param = ruby_array # fill with value when no array        
         end
         copy_ruby_to_java(shift_dims,ruby_param,java_array[i],converter,fill_value)
       end
     else
       copy_data(ruby_array,java_array,converter,fill_value)
     end
     java_array 
   end
   
 private
   def copy_data(ruby_array,java_array,converter,fill_value)
     if ruby_array.kind_of?(::Array)
       rlen = ruby_array.length
     else
       rlen = 0
       # in irregularly-formed Ruby arrays, values that appear where
       # a subarray is expected get propagated. not sure if this is
       # the best behavior, will see what users say
       fill_value = converter.call(ruby_array) if ruby_array    
     end
     java_object = java_array.java_object
     jlen = java_array.length
     i = 0
     while i < rlen && i < jlen
       java_object[i] = converter.call(ruby_array[i])
       i += 1
     end
     if i < jlen && fill_value
       java_object.fill(i,jlen,fill_value)
     end
     java_array
   end
 public
 
   def java_to_ruby(java_array)
     unless java_array.kind_of?(ArrayJavaProxy)
       raise ArgumentError,"not a Java array: #{java_array}"
     end
     length = java_array.length
     ruby_array = Array.new(length)
     if length > 0
       if java_array[0].kind_of?ArrayJavaProxy
         length.times do |i|
           ruby_array[i] = java_to_ruby(java_array[i])      
         end
       else
         length.times do |i|
           ruby_array[i] = java_array[i];      
         end
       end
     end
     ruby_array
   end
 
  end #self
 end #JavaArrayUtilities
 
 # enable :to_java for arrays
 class Array
   def to_java(*args,&block)
     JavaArrayUtilities.ruby_to_java(*(args.unshift(self)),&block)
   end
 end
 
 # Extensions to the standard Module package.
 
 class Module
   private
 
   ##
   # Includes a Java package into this class/module. The Java classes in the
   # package will become available in this class/module, unless a constant
   # with the same name as a Java class is already defined.
   #
   def include_package(package_name)
     if defined? @included_packages
       @included_packages << package_name      
       return
     end
     @included_packages = [package_name]
     @java_aliases = {} unless @java_aliases
 
     def self.const_missing(constant)
       real_name = @java_aliases[constant]
       real_name = constant unless real_name
 
       java_class = nil
       return super unless @included_packages.detect {|package|
           java_class = JavaUtilities.get_java_class(package + '.' + real_name.to_s)
       }
       
       JavaUtilities.create_proxy_class(constant, java_class, self)
     end
   end
 
   def java_alias(new_id, old_id)
     @java_aliases[new_id] = old_id
   end
 end
 
 class ConstantAlreadyExistsError < RuntimeError
 end
 
 class Object
   def include_class(include_class)
     class_names = include_class.to_a
 
     class_names.each do |full_class_name|
       package_name, class_name = full_class_name.match(/((.*)\.)?([^\.]*)/)[2,3]
 
       if block_given?
         constant = yield(package_name, class_name)
       else
         constant = class_name
       end
       
       cls = self.kind_of?(Module) ? self : self.class
 
 	  # Constant already exists...do not let proxy get created unless the collision is the proxy
 	  # you are trying to include.
       if (cls.const_defined?(constant) )
         proxy = JavaUtilities.get_proxy_class(full_class_name)
         existing_constant = cls.const_get(constant)
       	raise ConstantAlreadyExistsError.new, "Class #{constant} already exists" unless existing_constant == proxy
 	  end
 
       # FIXME: When I changed this user const_set instead of eval below Comparator got lost
       # which means I am missing something.
       if (respond_to?(:class_eval, true))
         class_eval("#{constant} = JavaUtilities.get_proxy_class(\"#{full_class_name}\")")
       else
         eval("#{constant} = JavaUtilities.get_proxy_class(\"#{full_class_name}\")")
       end
     end
   end
 
   # sneaking this in with the array support, getting
   # tired of having to define it in all my code  -BD
   def java_kind_of?(other)
     return true if self.kind_of?(other)
     return false unless self.respond_to?(:java_class) && other.respond_to?(:java_class) &&
       other.kind_of?(Module) && !self.kind_of?(Module) 
     return other.java_class.assignable_from?(self.java_class)
   end
 end
 
 class JavaInterfaceExtender
   def initialize(java_class_name, &block)
     @java_class = Java::JavaClass.for_name(java_class_name)
     @block = block
   end
   
   def extend_proxy(proxy_class)
     proxy_class.class_eval &@block if @java_class.assignable_from? proxy_class.java_class
   end
 end
 
 module Java
  class << self
    def const_missing(sym)
       JavaUtilities.get_proxy_class "#{sym}"
    end
 
    def method_missing(sym, *args)
      if JavaUtilities.is_primitive_type(sym) 
        JavaUtilities.get_proxy_class sym.to_s
      elsif sym.to_s.downcase[0] == sym.to_s[0]
        Package.create_package sym, sym, Java
      else
        JavaUtilities.get_proxy_class "#{sym}"
      end
    end
  end
 
  class Package
    # this class should be a blank slate
    
    def initialize(name)
      @name = name
    end
 
    def singleton; class << self; self; end; end 
 
    def method_missing(sym, *args)
      if sym.to_s.downcase[0] == sym.to_s[0]
        self.class.create_package sym, "#{@name}.#{sym}", singleton
      else
        JavaUtilities.get_proxy_class "#{@name}.#{sym}"
      end
    end
 
    class << self
      def create_package(sym, package_name, cls)
        package = Java::Package.new package_name
        cls.send(:define_method, sym) { package }
        package
      end
    end
  end
 end
 
 # Create convenience methods for top-level java packages so we do not need to prefix
 # with 'Java::'.  We undef these methods within Package in case we run into 'com.foo.com'.
 [:java, :javax, :com, :org].each do |meth|
  Java::Package.create_package(meth, meth.to_s, Kernel)
  Java::Package.send(:undef_method, meth)
 end
 
 require 'builtin/java/exceptions'
 require 'builtin/java/collections'
 require 'builtin/java/interfaces'
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 85614712b1..66443d36e2 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1171 +1,1171 @@
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
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.util.Iterator;
 import java.util.List;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.StringTokenizer;
 import java.util.regex.Pattern;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.UnsynchronizedStack;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  *
  * @author jpetersen
  */
 public class RubyKernel {
     public final static Class IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyKernel.class);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineFastModuleFunction("Array", callbackFactory.getFastSingletonMethod("new_array", IRUBY_OBJECT));
         module.defineFastModuleFunction("Float", callbackFactory.getFastSingletonMethod("new_float", IRUBY_OBJECT));
         module.defineFastModuleFunction("Integer", callbackFactory.getFastSingletonMethod("new_integer", IRUBY_OBJECT));
         module.defineFastModuleFunction("String", callbackFactory.getFastSingletonMethod("new_string", IRUBY_OBJECT));
         module.defineFastModuleFunction("`", callbackFactory.getFastSingletonMethod("backquote", IRUBY_OBJECT));
         module.defineFastModuleFunction("abort", callbackFactory.getFastOptSingletonMethod("abort"));
         module.defineModuleFunction("at_exit", callbackFactory.getSingletonMethod("at_exit"));
         module.defineFastModuleFunction("autoload", callbackFactory.getFastSingletonMethod("autoload", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", IRUBY_OBJECT));
         module.defineModuleFunction("binding", callbackFactory.getSingletonMethod("binding"));
         module.defineModuleFunction("block_given?", callbackFactory.getSingletonMethod("block_given"));
         // TODO: Implement Kernel#callcc
         module.defineModuleFunction("caller", callbackFactory.getOptSingletonMethod("caller"));
         module.defineModuleFunction("catch", callbackFactory.getSingletonMethod("rbCatch", IRUBY_OBJECT));
         module.defineFastModuleFunction("chomp", callbackFactory.getFastOptSingletonMethod("chomp"));
         module.defineFastModuleFunction("chomp!", callbackFactory.getFastOptSingletonMethod("chomp_bang"));
         module.defineFastModuleFunction("chop", callbackFactory.getFastSingletonMethod("chop"));
         module.defineFastModuleFunction("chop!", callbackFactory.getFastSingletonMethod("chop_bang"));
         module.defineModuleFunction("eval", callbackFactory.getOptSingletonMethod("eval"));
         module.defineFastModuleFunction("exit", callbackFactory.getFastOptSingletonMethod("exit"));
         module.defineFastModuleFunction("exit!", callbackFactory.getFastOptSingletonMethod("exit_bang"));
         module.defineModuleFunction("fail", callbackFactory.getOptSingletonMethod("raise"));
         // TODO: Implement Kernel#fork
         module.defineFastModuleFunction("format", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("gets", callbackFactory.getFastOptSingletonMethod("gets"));
         module.defineFastModuleFunction("global_variables", callbackFactory.getFastSingletonMethod("global_variables"));
         module.defineModuleFunction("gsub", callbackFactory.getOptSingletonMethod("gsub"));
         module.defineModuleFunction("gsub!", callbackFactory.getOptSingletonMethod("gsub_bang"));
         // TODO: Add deprecation to Kernel#iterator? (maybe formal deprecation mech.)
         module.defineModuleFunction("iterator?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("lambda", callbackFactory.getSingletonMethod("proc"));
         module.defineModuleFunction("load", callbackFactory.getOptSingletonMethod("load"));
         module.defineFastModuleFunction("local_variables", callbackFactory.getFastSingletonMethod("local_variables"));
         module.defineModuleFunction("loop", callbackFactory.getSingletonMethod("loop"));
         // Note: method_missing is documented as being in Object, but ruby appears to stick it in Kernel.
         module.defineModuleFunction("method_missing", callbackFactory.getOptSingletonMethod("method_missing"));
         module.defineModuleFunction("open", callbackFactory.getOptSingletonMethod("open"));
         module.defineFastModuleFunction("p", callbackFactory.getFastOptSingletonMethod("p"));
         module.defineFastModuleFunction("print", callbackFactory.getFastOptSingletonMethod("print"));
         module.defineFastModuleFunction("printf", callbackFactory.getFastOptSingletonMethod("printf"));
         module.defineModuleFunction("proc", callbackFactory.getSingletonMethod("proc"));
         // TODO: implement Kernel#putc
         module.defineFastModuleFunction("puts", callbackFactory.getFastOptSingletonMethod("puts"));
         module.defineModuleFunction("raise", callbackFactory.getOptSingletonMethod("raise"));
         module.defineFastModuleFunction("rand", callbackFactory.getFastOptSingletonMethod("rand"));
         module.defineFastModuleFunction("readline", callbackFactory.getFastOptSingletonMethod("readline"));
         module.defineFastModuleFunction("readlines", callbackFactory.getFastOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRUBY_OBJECT));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRUBY_OBJECT));
         module.defineFastModuleFunction("select", callbackFactory.getFastOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRUBY_OBJECT));
         module.defineFastModuleFunction("sleep", callbackFactory.getFastSingletonMethod("sleep", IRUBY_OBJECT));
         module.defineFastModuleFunction("split", callbackFactory.getFastOptSingletonMethod("split"));
         module.defineFastModuleFunction("sprintf", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("srand", callbackFactory.getFastOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineFastModuleFunction("system", callbackFactory.getFastOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineFastModuleFunction("exec", callbackFactory.getFastOptSingletonMethod("system"));
         // TODO: Implement Kernel#test (partial impl)
         module.defineModuleFunction("throw", callbackFactory.getOptSingletonMethod("rbThrow"));
         // TODO: Implement Kernel#trace_var
         module.defineModuleFunction("trap", callbackFactory.getOptSingletonMethod("trap"));
         // TODO: Implement Kernel#untrace_var
         module.defineFastModuleFunction("warn", callbackFactory.getFastSingletonMethod("warn", IRUBY_OBJECT));
         
         // Defined p411 Pickaxe 2nd ed.
         module.defineModuleFunction("singleton_method_added", callbackFactory.getSingletonMethod("singleton_method_added", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_removed", callbackFactory.getSingletonMethod("singleton_method_removed", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_undefined", callbackFactory.getSingletonMethod("singleton_method_undefined", IRUBY_OBJECT));
         
         // Object methods
         module.defineFastPublicModuleFunction("==", objectCallbackFactory.getFastMethod("obj_equal", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("===", objectCallbackFactory.getFastMethod("equal", IRUBY_OBJECT));
 
         module.defineAlias("eql?", "==");
         module.defineFastPublicModuleFunction("to_s", objectCallbackFactory.getFastMethod("to_s"));
         module.defineFastPublicModuleFunction("nil?", objectCallbackFactory.getFastMethod("nil_p"));
         module.defineFastPublicModuleFunction("to_a", callbackFactory.getFastSingletonMethod("to_a"));
         module.defineFastPublicModuleFunction("hash", objectCallbackFactory.getFastMethod("hash"));
-        module.defineFastPublicModuleFunction("id", objectCallbackFactory.getFastMethod("id"));
-        module.defineAlias("__id__", "id");
-        module.defineAlias("object_id", "id");
+        module.defineFastPublicModuleFunction("id", objectCallbackFactory.getFastMethod("id_deprecated"));
+        module.defineFastPublicModuleFunction("object_id", objectCallbackFactory.getFastMethod("id"));
+        module.defineAlias("__id__", "object_id");
         module.defineFastPublicModuleFunction("is_a?", objectCallbackFactory.getFastMethod("kind_of", IRUBY_OBJECT));
         module.defineAlias("kind_of?", "is_a?");
         module.defineFastPublicModuleFunction("dup", objectCallbackFactory.getFastMethod("dup"));
         module.defineFastPublicModuleFunction("equal?", objectCallbackFactory.getFastMethod("same", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("type", objectCallbackFactory.getFastMethod("type_deprecated"));
         module.defineFastPublicModuleFunction("class", objectCallbackFactory.getFastMethod("type"));
         module.defineFastPublicModuleFunction("inspect", objectCallbackFactory.getFastMethod("inspect"));
         module.defineFastPublicModuleFunction("=~", objectCallbackFactory.getFastMethod("match", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("clone", objectCallbackFactory.getFastMethod("rbClone"));
         module.defineFastPublicModuleFunction("display", objectCallbackFactory.getFastOptMethod("display"));
         module.defineFastPublicModuleFunction("extend", objectCallbackFactory.getFastOptMethod("extend"));
         module.defineFastPublicModuleFunction("freeze", objectCallbackFactory.getFastMethod("freeze"));
         module.defineFastPublicModuleFunction("frozen?", objectCallbackFactory.getFastMethod("frozen"));
         module.defineFastModuleFunction("initialize_copy", objectCallbackFactory.getFastMethod("initialize_copy", IRUBY_OBJECT));
         module.definePublicModuleFunction("instance_eval", objectCallbackFactory.getOptMethod("instance_eval"));
         module.defineFastPublicModuleFunction("instance_of?", objectCallbackFactory.getFastMethod("instance_of", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variables", objectCallbackFactory.getFastMethod("instance_variables"));
         module.defineFastPublicModuleFunction("instance_variable_get", objectCallbackFactory.getFastMethod("instance_variable_get", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variable_set", objectCallbackFactory.getFastMethod("instance_variable_set", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("method", objectCallbackFactory.getFastMethod("method", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("methods", objectCallbackFactory.getFastOptMethod("methods"));
         module.defineFastPublicModuleFunction("private_methods", objectCallbackFactory.getFastMethod("private_methods"));
         module.defineFastPublicModuleFunction("protected_methods", objectCallbackFactory.getFastMethod("protected_methods"));
         module.defineFastPublicModuleFunction("public_methods", objectCallbackFactory.getFastOptMethod("public_methods"));
         module.defineFastModuleFunction("remove_instance_variable", objectCallbackFactory.getMethod("remove_instance_variable", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("respond_to?", objectCallbackFactory.getFastOptMethod("respond_to"));
         module.definePublicModuleFunction("send", objectCallbackFactory.getOptMethod("send"));
         module.defineAlias("__send__", "send");
         module.defineFastPublicModuleFunction("singleton_methods", objectCallbackFactory.getFastOptMethod("singleton_methods"));
         module.defineFastPublicModuleFunction("taint", objectCallbackFactory.getFastMethod("taint"));
         module.defineFastPublicModuleFunction("tainted?", objectCallbackFactory.getFastMethod("tainted"));
         module.defineFastPublicModuleFunction("untaint", objectCallbackFactory.getFastMethod("untaint"));
 
         return module;
     }
 
     public static IRubyObject at_exit(IRubyObject recv, Block block) {
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(false, block));
     }
 
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         String name = symbol.asSymbol();
         if (recv instanceof RubyModule) {
             name = ((RubyModule)recv).getName() + "::" + name;
         }
         
         IAutoloadMethod autoloadMethod = recv.getRuntime().getLoadService().autoloadFor(name);
         if(autoloadMethod == null) return recv.getRuntime().getNil();
 
         return recv.getRuntime().newString(autoloadMethod.file());
     }
 
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         final LoadService loadService = recv.getRuntime().getLoadService();
         final String baseName = symbol.asSymbol();
         String nm = baseName;
         if(recv instanceof RubyModule) {
             nm = ((RubyModule)recv).getName() + "::" + nm;
         }
         loadService.addAutoload(nm, new IAutoloadMethod() {
                 public String file() {
                     return file.toString();
                 }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 loadService.require(file.toString());
                 if(recv instanceof RubyModule) {
                     return ((RubyModule)recv).getConstant(baseName);
                 }
                 return runtime.getObject().getConstant(baseName);
             }
         });
         return recv;
     }
 
     public static IRubyObject method_missing(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = recv.anyToString().toString();
         } else {
             description = recv.inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description, 
                                                                      noClass ? "" : ":", noClass ? "" : recv.getType().getName()}, null);
 
         throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg, name) : runtime.newNoMethodError(msg, name);
     }
 
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
 
         // Should this logic be pushed into RubyIO Somewhere?
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             try {
                 // TODO: may need to part cli parms out ourself?
                 Process p = Runtime.getRuntime().exec(command,getCurrentEnv(recv.getRuntime()));
                 RubyIO io = new RubyIO(recv.getRuntime(), p);
                 
                 if (block.isGiven()) {
                     try {
                         recv.getRuntime().getCurrentContext().yield(io, block);
                         
                         return recv.getRuntime().getNil();
                     } finally {
                         io.close();
                     }
                 }
 
                 return io;
             } catch (IOException ioe) {
                 throw recv.getRuntime().newIOErrorFromException(ioe);
             }
         } 
 
         return ((FileMetaClass) recv.getRuntime().getClass("File")).open(args, block);
     }
 
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
         RubyArgsFile argsFile = (RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<");
 
         IRubyObject line = argsFile.internalGets(args);
 
         recv.getRuntime().getCurrentContext().setLastline(line);
 
         return line;
     }
 
     public static IRubyObject abort(IRubyObject recv, IRubyObject[] args) {
         if(recv.checkArgumentCount(args,0,1) == 1) {
             recv.getRuntime().getGlobalVariables().get("$stderr").callMethod(recv.getRuntime().getCurrentContext(),"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.convertToTypeWithCheck("Array", "to_ary");
         
         if (value.isNil()) {
             DynamicMethod method = object.getMetaClass().searchMethod("to_a");
             
             if (method.getImplementationClass() == recv.getRuntime().getKernel()) {
                 return recv.getRuntime().newArray(object);
             }
             
             // Strange that Ruby has custom code here and not convertToTypeWithCheck equivalent.
             value = object.callMethod(recv.getRuntime().getCurrentContext(), "to_a");
             if (value.getMetaClass() != recv.getRuntime().getClass("Array")) {
                 throw recv.getRuntime().newTypeError("`to_a' did not return Array");
                
             }
         }
         
         return value;
     }
     
     public static IRubyObject new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getValue().length() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = object.convertToFloat();
             if(Double.isNaN(rFloat.getDoubleValue())){
                 recv.getRuntime().newArgumentError("invalid value for Float()");
         }
             return rFloat;
     }
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if(object instanceof RubyString) {
             String val = object.toString();
             if(val.length() > 0 && val.charAt(0) == '0') {
                 if(val.length() > 1) {
                     if(val.charAt(1) == 'x') {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(2)),16,true);
                     } else if(val.charAt(1) == 'b') {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(2)),2,true);
                     } else {
                         return RubyNumeric.str2inum(recv.getRuntime(),recv.getRuntime().newString(val.substring(1)),8,true);
                     }
                 }
             }
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,10,true);
         }
         return object.callMethod(context,"to_i");
     }
     
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return object.callMethod(recv.getRuntime().getCurrentContext(), "to_s");
     }
     
     
     public static IRubyObject p(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", args[i].callMethod(context, "inspect"));
                 defout.callMethod(context, "write", recv.getRuntime().newString("\n"));
             }
         }
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject puts(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         defout.callMethod(context, "puts", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject print(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         defout.callMethod(context, "print", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject printf(IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             ThreadContext context = recv.getRuntime().getCurrentContext();
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
             throw recv.getRuntime().newEOFError();
         }
 
         return line;
     }
 
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
         RubyArgsFile argsFile = (RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<");
 
         RubyArray lines = recv.getRuntime().newArray();
 
         IRubyObject line = argsFile.internalGets(args);
         while (!line.isNil()) {
             lines.append(line);
 
             line = argsFile.internalGets(args);
         }
 
         return lines;
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(Ruby runtime) {
         IRubyObject line = runtime.getCurrentContext().getLastline();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     public static IRubyObject sub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).sub_bang(args, block);
     }
 
     public static IRubyObject sub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.sub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject gsub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).gsub_bang(args, block);
     }
 
     public static IRubyObject gsub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.gsub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chop_bang(IRubyObject recv) {
         return getLastlineString(recv.getRuntime()).chop_bang();
     }
 
     public static IRubyObject chop(IRubyObject recv) {
         RubyString str = getLastlineString(recv.getRuntime());
 
         if (str.getValue().length() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang();
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chomp_bang(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).chomp_bang(args);
     }
 
     public static IRubyObject chomp(IRubyObject recv, IRubyObject[] args) {
         RubyString str = getLastlineString(recv.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         recv.getRuntime().getCurrentContext().setLastline(dup);
         return dup;
     }
 
     public static IRubyObject split(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).split(args);
     }
 
     public static IRubyObject scan(IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(recv.getRuntime()).scan(pattern, block);
     }
 
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return IOMetaClass.select_static(recv.getRuntime(), args);
     }
 
     public static IRubyObject sleep(IRubyObject recv, IRubyObject seconds) {
         long milliseconds = (long) (seconds.convertToFloat().getDoubleValue() * 1000);
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = recv.getRuntime().getThreadService().getCurrentContext().getThread();
         try {
             rubyThread.sleep(milliseconds);
         } catch (InterruptedException iExcptn) {
         }
 
         return recv.getRuntime().newFixnum(
                 Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         recv.getRuntime().secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         throw recv.getRuntime().newSystemExit(status);
     }
 
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return exit(recv, args);
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     public static RubyArray global_variables(IRubyObject recv) {
         RubyArray globalVariables = recv.getRuntime().newArray();
 
         Iterator iter = recv.getRuntime().getGlobalVariables().getNames();
         while (iter.hasNext()) {
             String globalVariableName = (String) iter.next();
 
             globalVariables.append(recv.getRuntime().newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     public static RubyArray local_variables(IRubyObject recv) {
         final Ruby runtime = recv.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         String[] names = runtime.getCurrentContext().getCurrentScope().getAllNamesInScope();
         for (int i = 0; i < names.length; i++) {
             localVariables.append(runtime.newString(names[i]));
         }
 
         return localVariables;
     }
 
     public static RubyBinding binding(IRubyObject recv, Block block) {
         // FIXME: Pass block into binding
         return recv.getRuntime().newBinding();
     }
 
     public static RubyBoolean block_given(IRubyObject recv, Block block) {
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().getPreviousFrame().getBlock().isGiven());
     }
 
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArrayNoCopy(args);
         newArgs.shift();
 
         return str.format(newArgs);
     }
 
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         recv.checkArgumentCount(args, 0, 3); 
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getClass("RuntimeError"), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.getClass("RuntimeError").newInstance(args, block));
             }
             
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!exception.isKindOf(runtime.getClass("Exception"))) {
             throw runtime.newTypeError("exception object expected");
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         throw new RaiseException((RubyException) exception);
     }
     
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         if (recv.getRuntime().getLoadService().require(name.toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString file = args[0].convertToString();
         recv.getRuntime().getLoadService().load(file.toString());
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args == null || args.length == 0) {
             throw recv.getRuntime().newArgumentError(args.length, 1);
         }
             
         RubyString src = args[0].convertToString();
         IRubyObject scope = null;
         String file = "(eval)";
         
         if (args.length > 1) {
             if (!args[1].isNil()) {
                 scope = args[1];
             }
             
             if (args.length > 2) {
                 file = args[2].toString();
             }
         }
         // FIXME: line number is not supported yet
         //int line = args.length > 3 ? RubyNumeric.fix2int(args[3]) : 1;
 
         src.checkSafeString();
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (scope == null) {
             scope = recv.getRuntime().newBinding();
         }
         
         return recv.evalWithBinding(context, src, scope, file);
     }
 
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return recv.getRuntime().getCurrentContext().createBacktrace(level, false);
     }
 
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         try {
             context.pushCatch(tag.asSymbol());
             return context.yield(tag, block);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ThrowJump &&
                 je.getTarget().equals(tag.asSymbol())) {
                     return (IRubyObject) je.getValue();
             }
             throw je;
         } finally {
             context.popCatch();
         }
     }
 
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         String tag = args[0].asSymbol();
         String[] catches = runtime.getCurrentContext().getActiveCatches();
 
         String message = "uncaught throw '" + tag + '\'';
 
         //Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i])) {
                 //Catch active, throw for catch to handle
                 JumpException je = new JumpException(JumpException.JumpType.ThrowJump);
 
                 je.setTarget(tag);
                 je.setValue(args.length > 1 ? args[1] : runtime.getNil());
                 throw je;
             }
         }
 
         //No catch active for this throw
         throw runtime.newNameError(message, tag);
     }
 
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: We can probably fake some basic signals, but obviously can't do everything. For now, stub.
         return recv.getRuntime().getNil();
     }
     
     public static IRubyObject warn(IRubyObject recv, IRubyObject message) {
         IRubyObject out = recv.getRuntime().getObject().getConstant("STDERR");
         RubyIO io = (RubyIO) out.convertToType("IO", "to_io", true); 
 
         io.puts(new IRubyObject[] { message });
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject set_trace_func(IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             recv.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw recv.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             recv.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     public static IRubyObject singleton_method_added(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_removed(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_undefined(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
     
     
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(true, block);
     }
 
     public static IRubyObject loop(IRubyObject recv, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             try {
                 context.yield(recv.getRuntime().getNil(), block);
 
                 Thread.yield();
             } catch (JumpException je) {
                 // JRUBY-530, specifically the Kernel#loop case:
                 // Kernel#loop always takes a block.  But what we're looking
                 // for here is breaking an iteration where the block is one 
                 // used inside loop's block, not loop's block itself.  Set the 
                 // appropriate flag on the JumpException if this is the case
                 // (the FCALLNODE case in EvaluationState will deal with it)
                 if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                     if (je.getTarget() != null && je.getTarget() != block) {
                         je.setBreakInKernelLoop(true);
                     }
                 }
                  
                 throw je;
             }
         }
     }
 
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         Ruby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         int resultCode = runInShell(runtime, new IRubyObject[] {aString}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return recv.getRuntime().newString(output.toString());
     }
     
     private static final Pattern PATH_SEPARATORS = Pattern.compile("[/\\\\]");
     
     /**
      * For the first full token on the command, most likely the actual executable to run, replace
      * all dir separators with that which is appropriate for the current platform. Return the new
      * with this executable string at the beginning.
      * 
      * @param command The all-forward-slashes command to be "fixed"
      * @return The "fixed" full command line
      */
     private static String repairDirSeps(String command) {
         String executable = "", remainder = "";
         command = command.trim();
         if (command.startsWith("'")) {
             String [] tokens = command.split("'", 3);
             executable = "'"+tokens[1]+"'";
             if (tokens.length > 2)
                 remainder = tokens[2];
         } else if (command.startsWith("\"")) {
             String [] tokens = command.split("\"", 3);
             executable = "\""+tokens[1]+"\"";
             if (tokens.length > 2)
                 remainder = tokens[2];
         } else {
             String [] tokens = command.split(" ", 2);
             executable = tokens[0];
             if (tokens.length > 1)
                 remainder = " "+tokens[1];
         }
         
         // Matcher.replaceAll treats backslashes in the replacement string as escaped characters
         String replacement = File.separator;
         if (File.separatorChar == '\\')
             replacement = "\\\\";
             
         return PATH_SEPARATORS.matcher(executable).replaceAll(replacement) + remainder;
                 }
 
     private static List parseCommandLine(IRubyObject[] rawArgs) {
         // first parse the first element of rawArgs since this may contain
         // the whole command line
         String command = rawArgs[0].toString();
         UnsynchronizedStack args = new UnsynchronizedStack();
         StringTokenizer st = new StringTokenizer(command, " ");
         String quoteChar = null;
 
         while (st.hasMoreTokens()) {
             String token = st.nextToken();
             if (quoteChar == null) {
                 // not currently in the middle of a quoted token
                 if (token.startsWith("'") || token.startsWith("\"")) {
                     // note quote char and remove from beginning of token
                     quoteChar = token.substring(0, 1);
                     token = token.substring(1);
                 }
                 if (quoteChar!=null && token.endsWith(quoteChar)) {
                     // quoted token self contained, remove from end of token
                     token = token.substring(0, token.length()-1);
                     quoteChar = null;
                 }
                 // add new token to list
                 args.push(token);
             } else {
                 // in the middle of quoted token
                 if (token.endsWith(quoteChar)) {
                     // end of quoted token
                     token = token.substring(0, token.length()-1);
                     quoteChar = null;
                 }
                 // update token at end of list
                 token = args.pop() + " " + token;
                 args.push(token);
             }
         }
         
         // now append the remaining raw args to the cooked arg list
         for (int i=1;i<rawArgs.length;i++) {
             args.push(rawArgs[i].toString());
         }
         
         return args;
     }
         
     /**
      * Only run an in-process script if the script name has "ruby", ".rb", or "irb" in the name
      */
     private static boolean shouldRunInProcess(Ruby runtime, String command) {
         command = command.trim();
         String [] spaceDelimitedTokens = command.split(" ", 2);
         String [] slashDelimitedTokens = spaceDelimitedTokens[0].split("/");
         String finalToken = slashDelimitedTokens[slashDelimitedTokens.length-1];
         return (finalToken.indexOf("ruby") != -1 || finalToken.endsWith(".rb") || finalToken.endsWith("irb"));
     }
     
     private static class InProcessScript extends Thread {
         private String[] argArray;
         private int result;
         private RubyInstanceConfig config;
         
         public InProcessScript(final String[] argArray, final InputStream in, 
                                final OutputStream out, final OutputStream err, final String[] env, final File dir) {
             this.argArray = argArray;
             this.config   = new RubyInstanceConfig() {{
                 setInput(in);
                 setOutput(new PrintStream(out));
                 setError(new PrintStream(err));
                 setEnvironment(environmentMap(env));
                 setCurrentDirectory(dir.toString());
             }};
         }
 
         public int getResult() {
             return result;
         }
 
         public void setResult(int result) {
             this.result = result;
         }
         
         public void run() {
             result = new Main(config).run(argArray);
         }
 
         private Map environmentMap(String[] env) {
             Map m = new HashMap();
             for (int i = 0; i < env.length; i++) {
                 String[] kv = env[i].split("=", 2);
                 m.put(kv[0], kv[1]);
             }
             return m;
         }
     }
 
     public static int runInShell(Ruby runtime, IRubyObject[] rawArgs) {
         return runInShell(runtime,rawArgs,runtime.getOutputStream());
     }
 
     private static String[] getCurrentEnv(Ruby runtime) {
         Map h = ((RubyHash)runtime.getObject().getConstant("ENV")).getValueMap();
         String[] ret = new String[h.size()];
         int i=0;
         for(Iterator iter = h.entrySet().iterator();iter.hasNext();i++) {
             Map.Entry e = (Map.Entry)iter.next();
             ret[i] = e.getKey().toString() + "=" + e.getValue().toString();
         }
         return ret;
     }
 
     public static int runInShell(Ruby runtime, IRubyObject[] rawArgs, OutputStream output) {
         OutputStream error = runtime.getErrorStream();
         InputStream input = runtime.getInputStream();
         try {
             String shell = runtime.evalScript("require 'rbconfig'; Config::CONFIG['SHELL']").toString();
             rawArgs[0] = runtime.newString(repairDirSeps(rawArgs[0].toString()));
             Process aProcess = null;
             InProcessScript ipScript = null;
             File pwd = new File(runtime.getCurrentDirectory());
             
             if (shouldRunInProcess(runtime, rawArgs[0].toString())) {
                 List args = parseCommandLine(rawArgs);
                 String command = (String)args.get(0);
 
                 // snip off ruby or jruby command from list of arguments
                 // leave alone if the command is the name of a script
                 int startIndex = command.endsWith(".rb") ? 0 : 1;
                 if(command.trim().endsWith("irb")) {
                     startIndex = 0;
                     args.set(0,runtime.getJRubyHome() + File.separator + "bin" + File.separator + "jirb");
                 }
                 String[] argArray = (String[])args.subList(startIndex,args.size()).toArray(new String[0]);
                 ipScript = new InProcessScript(argArray, input, output, error, getCurrentEnv(runtime), pwd);
                 
                 // execute ruby command in-process
                 ipScript.start();
                 ipScript.join();
             } else if (shell != null && rawArgs.length == 1) {
                 // execute command with sh -c or cmd.exe /c
                 // this does shell expansion of wildcards
                 String shellSwitch = shell.endsWith("sh") ? "-c" : "/c";
                 String[] argArray = new String[3];
                 argArray[0] = shell;
                 argArray[1] = shellSwitch;
                 argArray[2] = rawArgs[0].toString();
                 aProcess = Runtime.getRuntime().exec(argArray, getCurrentEnv(runtime), pwd);
             } else {
                 // execute command directly, no wildcard expansion
                 if (rawArgs.length > 1) {
                     String[] argArray = new String[rawArgs.length];
                     for (int i=0;i<rawArgs.length;i++) {
                         argArray[i] = rawArgs[i].toString();
                     }
                     aProcess = Runtime.getRuntime().exec(argArray,getCurrentEnv(runtime), pwd);
                 } else {
                     aProcess = Runtime.getRuntime().exec(rawArgs[0].toString(), getCurrentEnv(runtime), pwd);
                 }
             }
             
             if (aProcess != null) {
                 handleStreams(aProcess,input,output,error);
                 return aProcess.waitFor();
             } else if (ipScript != null) {
                 return ipScript.getResult();
             } else {
                 return 0;
             }
         } catch (IOException e) {
             throw runtime.newIOErrorFromException(e);
         } catch (InterruptedException e) {
             throw runtime.newThreadError("unexpected interrupt");
         }
     }
     
     private static void handleStreams(Process p, InputStream in, OutputStream out, OutputStream err) throws IOException {
         InputStream pOut = p.getInputStream();
         InputStream pErr = p.getErrorStream();
         OutputStream pIn = p.getOutputStream();
 
         boolean done = false;
         int b;
         boolean proc = false;
         while(!done) {
             if(pOut.available() > 0) {
                 byte[] input = new byte[pOut.available()];
                 if((b = pOut.read(input)) == -1) {
                     done = true;
                 } else {
                     out.write(input);
                 }
                 proc = true;
             }
             if(pErr.available() > 0) {
                 byte[] input = new byte[pErr.available()];
                 if((b = pErr.read(input)) != -1) {
                     err.write(input);
                 }
                 proc = true;
             }
             if(in.available() > 0) {
                 byte[] input = new byte[in.available()];
                 if((b = in.read(input)) != -1) {
                     pIn.write(input);
                 }
                 proc = true;
             }
             if(!proc) {
                 if((b = pOut.read()) == -1) {
                     if((b = pErr.read()) == -1) {
                         done = true;
                     } else {
                         err.write(b);
                     }
                 } else {
                     out.write(b);
                 }
             }
             proc = false;
         }
         pOut.close();
         pErr.close();
         pIn.close();
     }
 
     public static RubyInteger srand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long oldRandomSeed = runtime.getRandomSeed();
 
         if (args.length > 0) {
             RubyInteger integerSeed = 
                 (RubyInteger) args[0].convertToType("Integer", "to_i", true);
             runtime.setRandomSeed(integerSeed.getLongValue());
         } else {
             // Not sure how well this works, but it works much better than
             // just currentTimeMillis by itself.
             runtime.setRandomSeed(System.currentTimeMillis() ^
               recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
               runtime.getRandom().nextInt(Math.abs((int)runtime.getRandomSeed())));
         }
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     public static RubyNumeric rand(IRubyObject recv, IRubyObject[] args) {
         long ceil;
         if (args.length == 0) {
             ceil = 0;
         } else if (args.length == 1) {
             RubyInteger integerCeil = (RubyInteger) args[0].convertToType("Integer", "to_i", true);
             ceil = integerCeil.getLongValue();
             ceil = Math.abs(ceil);
             if (ceil > Integer.MAX_VALUE) {
                 throw recv.getRuntime().newNotImplementedError("Random values larger than Integer.MAX_VALUE not supported");
             }
         } else {
             throw recv.getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         if (ceil == 0) {
             double result = recv.getRuntime().getRandom().nextDouble();
             return RubyFloat.newFloat(recv.getRuntime(), result);
         }
         return recv.getRuntime().newFixnum(recv.getRuntime().getRandom().nextInt((int) ceil));
     }
 
     public static RubyBoolean system(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 0512af0847..1aa9a7f64e 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1379 +1,1384 @@
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
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 
 import org.jruby.ast.Node;
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.DynamicMethod;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.PrintfFormat;
 import org.jruby.util.collections.SinglyLinkedList;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.jruby.runtime.ClassIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
 	
     // The class of this object
     private RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
 
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     public RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
         this.frozen = false;
         this.taint = false;
 
         // Do not store any immediate objects into objectspace.
         if (useObjectSpace && !isImmediate()) {
             runtime.getObjectSpace().add(this);
         }
 
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         taint |= runtime.getSafeLevel() >= 3;
     }
 
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
     
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      */
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
     
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * Create a new meta class.
      *
      * @since Ruby 1.6.7
      */
     public RubyClass makeMetaClass(RubyClass superClass, SinglyLinkedList parentCRef) {
         RubyClass klass = new MetaClass(getRuntime(), superClass, getMetaClass().getAllocator(), parentCRef);
         setMetaClass(klass);
 		
         klass.setInstanceVariable("__attached__", this);
 
         if (this instanceof RubyClass && isSingleton()) { // could be pulled down to RubyClass in future
             klass.setMetaClass(klass);
             klass.setSuperClass(((RubyClass)this).getSuperClass().getRealClass().getMetaClass());
         } else {
             klass.setMetaClass(superClass.getRealClass().getMetaClass());
         }
         
         // use same ClassIndex as metaclass, since we're technically still of that type 
         klass.index = superClass.index;
         return klass;
     }
         
     public boolean isSingleton() {
         return false;
     }
 
     public boolean singletonMethodsAllowed() {
         return true;
     }
 
     public Class getJavaClass() {
         return IRubyObject.class;
     }
     
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     public boolean equals(Object other) {
         return other == this || other instanceof IRubyObject && callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), "to_s").toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public Ruby getRuntime() {
         return metaClass.getRuntime();
     }
     
     public boolean safeHasInstanceVariables() {
         return instanceVariables != null && instanceVariables.size() > 0;
     }
     
     public Map safeGetInstanceVariables() {
         return instanceVariables == null ? null : getInstanceVariablesSnapshot();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
     /**
      * Returns an unmodifiable snapshot of the current state of instance variables.
      * This method synchronizes access to avoid deadlocks.
      */
     public Map getInstanceVariablesSnapshot() {
         synchronized(getInstanceVariables()) {
             return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
         }
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
                             instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = Collections.synchronizedMap(instanceVariables);
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public RubyClass getMetaClass() {
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return frozen;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         this.frozen = frozen;
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message + getMetaClass().getName());
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen ");
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
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
     public RubyClass getSingletonClass() {
         RubyClass klass;
         
         if (getMetaClass().isSingleton() && getMetaClass().getInstanceVariable("__attached__") == this) {
             klass = getMetaClass();            
         } else {
             klass = makeMetaClass(getMetaClass(), getMetaClass().getCRef());
         }
         
         klass.setTaint(isTaint());
         klass.setFrozen(isFrozen());
         
         return klass;
     }
     
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
        RubyClass klass = getMetaClass();
 
        if (!klass.isSingleton()) {
            return klass;
 		}
        
        MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), getMetaClass().getAllocator(), getMetaClass().getCRef());
        clone.setFrozen(klass.isFrozen());
        clone.setTaint(klass.isTaint());
 
        if (this instanceof RubyClass) {
            clone.setMetaClass(clone);
        } else {
            clone.setMetaClass(klass.getSingletonClassClone());
        }
        
        if (klass.safeHasInstanceVariables()) {
            clone.setInstanceVariables(new HashMap(klass.getInstanceVariables()));
        }
 
        klass.cloneMethods(clone);
 
        clone.getMetaClass().setInstanceVariable("__attached__", clone);
 
        return clone;
     }    
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     /** init_copy
      * 
      */
     public void initCopy(IRubyObject original) {
         assert original != null;
         assert !isFrozen() : "frozen object (" + getMetaClass().getName() + ") allocated";
 
         setInstanceVariables(new HashMap(original.getInstanceVariables()));
         /* FIXME: finalizer should be dupped here */
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
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, Block.NULL_BLOCK);
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
         return callMethod(context, rubyclass, name, args, callType, Block.NULL_BLOCK);
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
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
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
         return EvaluationState.eval(getRuntime().getCurrentContext(), n, this, Block.NULL_BLOCK);
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
 
     public static String trueFalseNil(IRubyObject v) {
         return trueFalseNil(v.getMetaClass().getName());
     }
 
     public static String trueFalseNil(String v) {
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
 
     /** rb_obj_as_string
      */
     public RubyString objAsString() {
         if (this instanceof RubyString) return (RubyString) this;
         
         IRubyObject str = this.callMethod(getRuntime().getCurrentContext(), "to_s");
         
         if (!(str instanceof RubyString)) str = anyToString();
 
         return (RubyString) str;
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
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck("String","to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck("Array","to_ary");
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
         if (block.isGiven()) {
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
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
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
                 		return (IRubyObject) je.getValue();
                 	} 
 
                     throw je;
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
 
         ISourcePosition savedPosition = context.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
             return EvaluationState.eval(context, getRuntime().parse(src.toString(), file, context.getCurrentScope()), this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             // restore position
             context.setPosition(savedPosition);
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
 
     
     /** rb_obj_init_copy
      * 
      */
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this == original) return this;
 	    
 	    checkFrozen();
         
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
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
+
+    public synchronized RubyFixnum id_deprecated() {
+        getRuntime().getWarnings().warn("Object#id will be deprecated; use Object#object_id");
+        return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
+    }
     
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
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     public IRubyObject rbClone() {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getSingletonClassClone());
         clone.setTaint(isTaint());
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
      *  should be overriden only by: Proc
      */
     public IRubyObject dup() {
         if (isImmediate()) {
             throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
         }        
         
         IRubyObject dup = doClone();    
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         dup.setTaint(isTaint());
         
         dup.initCopy(this);
 
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
         if ((!isImmediate()) &&
                 // TYPE(obj) == T_OBJECT
                 !(this instanceof RubyClass) &&
                 this != getRuntime().getObject() &&
                 this != getRuntime().getClass("Module") &&
                 !(this instanceof RubyModule) &&
                 safeHasInstanceVariables()) {
 
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
 
     public IRubyObject anyToString() {
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
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = anyToString().toString();
         } else {
             description = inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = getRuntime().getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = new PrintfFormat(format).sprintf(new Object[] { name, description,
                                                                      noClass ? "" : ":", noClass ? "" : getType().getName()}, null);
 
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
diff --git a/test/testCompiler.rb b/test/testCompiler.rb
index 684858e508..3f83f6823e 100644
--- a/test/testCompiler.rb
+++ b/test/testCompiler.rb
@@ -1,95 +1,95 @@
 require 'jruby'
 require 'test/minirunit'
 
 StandardASMCompiler = org.jruby.compiler.impl.StandardASMCompiler
 NodeCompilerFactory = org.jruby.compiler.NodeCompilerFactory
 
 def compile_to_class(src)
-  node = JRuby.parse(src, "EVAL#{src.id}")
+  node = JRuby.parse(src, "EVAL#{src.object_id}")
   context = StandardASMCompiler.new(node)
   NodeCompilerFactory.getCompiler(node).compile(node, context)
 
   context.loadClass(JRuby.runtime)
 end
 
 def compile_and_run(src)
   cls = compile_to_class(src)
 
   cls.new_instance.run(JRuby.runtime.current_context, JRuby.runtime.top_self, nil, nil)
 end
 
 asgnFixnumCode = "a = 5; a"
 asgnStringCode = "a = 'hello'; a"
 arrayCode = "['hello', 5, ['foo', 6]]"
 fcallCode = "foo('bar')"
 callCode = "'bar'.capitalize"
 ifCode = "if 1 == 1; 2; else; 3; end"
 unlessCode = "unless 1 == 1; 2; else; 3; end"
 whileCode = "a = 0; while a < 5; a = a + 2; end; a"
 whileNoBody = "$foo = false; def flip; $foo = !$foo; $foo; end; while flip; end"
 andCode = "1 && 2"
 andShortCode = "nil && 3"
 beginCode = "begin; a = 4; end; a"
 
 iterBasic = "foo2('baz') { 4 }"
 
 defBasic = "def foo3(arg); arg + '2'; end"
 
 test_no_exception {
   compile_to_class(asgnFixnumCode);
 }
 
 test_equal(5, compile_and_run(asgnFixnumCode))
 test_equal('hello', compile_and_run(asgnStringCode))
 
 def foo(arg)
   arg + '2'
 end
 
 def foo2(arg)
   arg
 end
 
 test_equal(['hello', 5, ['foo', 6]], compile_and_run(arrayCode))
 test_equal('bar2', compile_and_run(fcallCode))
 test_equal('Bar', compile_and_run(callCode))
 test_equal(2, compile_and_run(ifCode))
 test_equal(3, compile_and_run(unlessCode))
 test_equal(6, compile_and_run(whileCode))
 #test_no_exception {
     test_equal(nil, compile_and_run(whileNoBody))
 #}
 #test_equal('baz', compile_and_run(iterBasic))
 compile_and_run(defBasic)
 test_equal('hello2', foo3('hello'))
 
 test_equal(2, compile_and_run(andCode))
 test_equal(nil, compile_and_run(andShortCode));
 test_equal(4, compile_and_run(beginCode));
 
 # Some complicated block var stuff
 blocksCode = <<-EOS
 def a
   yield 3
 end
 
 arr = []
 x = 1
 1.times { 
   y = 2
   arr << x
   x = 3
   a { 
     arr << y
     y = 4
     arr << x
     x = 5
   }
   arr << y
   arr << x
   x = 6
 }
 arr << x
 EOS
 
 test_equal([1,2,3,4,5,6], compile_and_run(blocksCode))
