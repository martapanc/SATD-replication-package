diff --git a/lib/ruby/1.8/set.rb b/lib/ruby/1.8/set.rb
index 4a00e43f59..c661993e0d 100644
--- a/lib/ruby/1.8/set.rb
+++ b/lib/ruby/1.8/set.rb
@@ -1,1293 +1,1293 @@
 #!/usr/bin/env ruby
 #--
 # set.rb - defines the Set class
 #++
 # Copyright (c) 2002-2008 Akinori MUSHA <knu@iDaemons.org>
 #
 # Documentation by Akinori MUSHA and Gavin Sinclair. 
 #
 # All rights reserved.  You can redistribute and/or modify it under the same
 # terms as Ruby.
 #
 #   $Id$
 #
 # == Overview 
 # 
 # This library provides the Set class, which deals with a collection
 # of unordered values with no duplicates.  It is a hybrid of Array's
 # intuitive inter-operation facilities and Hash's fast lookup.  If you
 # need to keep values ordered, use the SortedSet class.
 #
 # The method +to_set+ is added to Enumerable for convenience.
 #
 # See the Set and SortedSet documentation for examples of usage.
 
 
 #
 # Set implements a collection of unordered values with no duplicates.
 # This is a hybrid of Array's intuitive inter-operation facilities and
 # Hash's fast lookup.
 #
 # Several methods accept any Enumerable object (implementing +each+)
 # for greater flexibility: new, replace, merge, subtract, |, &, -, ^.
 #
 # The equality of each couple of elements is determined according to
 # Object#eql? and Object#hash, since Set uses Hash as storage.
 #
 # Finally, if you are using class Set, you can also use Enumerable#to_set
 # for convenience.
 #
 # == Example
 #
 #   require 'set'
 #   s1 = Set.new [1, 2]                   # -> #<Set: {1, 2}>
 #   s2 = [1, 2].to_set                    # -> #<Set: {1, 2}>
 #   s1 == s2                              # -> true
 #   s1.add("foo")                         # -> #<Set: {1, 2, "foo"}>
 #   s1.merge([2, 6])                      # -> #<Set: {6, 1, 2, "foo"}>
 #   s1.subset? s2                         # -> false
 #   s2.subset? s1                         # -> true
 #
 # == Contact
 #
 #   - Akinori MUSHA <knu@iDaemons.org> (current maintainer)
 #
 class Set
   include Enumerable
 
   # Creates a new set containing the given objects.
   def self.[](*ary)
     new(ary)
   end
 
   # Creates a new set containing the elements of the given enumerable
   # object.
   #
   # If a block is given, the elements of enum are preprocessed by the
   # given block.
   def initialize(enum = nil, &block) # :yields: o
     @hash ||= Hash.new
 
     enum.nil? and return
 
     if block
       enum.each { |o| add(block[o]) }
     else
       merge(enum)
     end
   end
 
   # Copy internal hash.
   def initialize_copy(orig)
     @hash = orig.instance_eval{@hash}.dup
   end
 
   # Returns the number of elements.
   def size
     @hash.size
   end
   alias length size
 
   # Returns true if the set contains no elements.
   def empty?
     @hash.empty?
   end
 
   # Removes all elements and returns self.
   def clear
     @hash.clear
     self
   end
 
   # Replaces the contents of the set with the contents of the given
   # enumerable object and returns self.
   def replace(enum)
     if enum.class == self.class
       @hash.replace(enum.instance_eval { @hash })
     else
       enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
       clear
       enum.each { |o| add(o) }
     end
 
     self
   end
 
   # Converts the set to an array.  The order of elements is uncertain.
   def to_a
     @hash.keys
   end
 
   def flatten_merge(set, seen = Set.new)
     set.each { |e|
       if e.is_a?(Set)
 	if seen.include?(e_id = e.object_id)
 	  raise ArgumentError, "tried to flatten recursive Set"
 	end
 
 	seen.add(e_id)
 	flatten_merge(e, seen)
 	seen.delete(e_id)
       else
 	add(e)
       end
     }
 
     self
   end
   protected :flatten_merge
 
   # Returns a new set that is a copy of the set, flattening each
   # containing set recursively.
   def flatten
     self.class.new.flatten_merge(self)
   end
 
   # Equivalent to Set#flatten, but replaces the receiver with the
   # result in place.  Returns nil if no modifications were made.
   def flatten!
     if detect { |e| e.is_a?(Set) }
       replace(flatten())
     else
       nil
     end
   end
 
   # Returns true if the set contains the given object.
   def include?(o)
     @hash.include?(o)
   end
   alias member? include?
 
   # Returns true if the set is a superset of the given set.
   def superset?(set)
     set.is_a?(Set) or raise ArgumentError, "value must be a set"
     return false if size < set.size
     set.all? { |o| include?(o) }
   end
 
   # Returns true if the set is a proper superset of the given set.
   def proper_superset?(set)
     set.is_a?(Set) or raise ArgumentError, "value must be a set"
     return false if size <= set.size
     set.all? { |o| include?(o) }
   end
 
   # Returns true if the set is a subset of the given set.
   def subset?(set)
     set.is_a?(Set) or raise ArgumentError, "value must be a set"
     return false if set.size < size
     all? { |o| set.include?(o) }
   end
 
   # Returns true if the set is a proper subset of the given set.
   def proper_subset?(set)
     set.is_a?(Set) or raise ArgumentError, "value must be a set"
     return false if set.size <= size
     all? { |o| set.include?(o) }
   end
 
   # Calls the given block once for each element in the set, passing
   # the element as parameter.  Returns an enumerator if no block is
   # given.
   def each
     block_given? or return enum_for(__method__)
     @hash.each_key { |o| yield(o) }
     self
   end
 
   # Adds the given object to the set and returns self.  Use +merge+ to
   # add several elements at once.
   def add(o)
     @hash[o] = true
     self
   end
   alias << add
 
   # Adds the given object to the set and returns self.  If the
   # object is already in the set, returns nil.
   def add?(o)
     if include?(o)
       nil
     else
       add(o)
     end
   end
 
   # Deletes the given object from the set and returns self.  Use +subtract+ to
   # delete several items at once.
   def delete(o)
     @hash.delete(o)
     self
   end
 
   # Deletes the given object from the set and returns self.  If the
   # object is not in the set, returns nil.
   def delete?(o)
     if include?(o)
       delete(o)
     else
       nil
     end
   end
 
   # Deletes every element of the set for which block evaluates to
   # true, and returns self.
   def delete_if
     to_a.each { |o| @hash.delete(o) if yield(o) }
     self
   end
 
   # Do collect() destructively.
   def collect!
     set = self.class.new
     each { |o| set << yield(o) }
     replace(set)
   end
   alias map! collect!
 
   # Equivalent to Set#delete_if, but returns nil if no changes were
   # made.
   def reject!
     n = size
     delete_if { |o| yield(o) }
     size == n ? nil : self
   end
 
   # Merges the elements of the given enumerable object to the set and
   # returns self.
   def merge(enum)
     if enum.instance_of?(self.class)
       @hash.update(enum.instance_variable_get(:@hash))
     else
       enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
       enum.each { |o| add(o) }
     end
 
     self
   end
 
   # Deletes every element that appears in the given enumerable object
   # and returns self.
   def subtract(enum)
     enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
     enum.each { |o| delete(o) }
     self
   end
 
   # Returns a new set built by merging the set and the elements of the
   # given enumerable object.
   def |(enum)
     enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
     dup.merge(enum)
   end
   alias + |		##
   alias union |		##
 
   # Returns a new set built by duplicating the set, removing every
   # element that appears in the given enumerable object.
   def -(enum)
     enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
     dup.subtract(enum)
   end
   alias difference -	##
 
   # Returns a new set containing elements common to the set and the
   # given enumerable object.
   def &(enum)
     enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
     n = self.class.new
     enum.each { |o| n.add(o) if include?(o) }
     n
   end
   alias intersection &	##
 
   # Returns a new set containing elements exclusive between the set
   # and the given enumerable object.  (set ^ enum) is equivalent to
   # ((set | enum) - (set & enum)).
   def ^(enum)
     enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
     n = Set.new(enum)
     each { |o| if n.include?(o) then n.delete(o) else n.add(o) end }
     n
   end
 
   # Returns true if two sets are equal.  The equality of each couple
   # of elements is defined according to Object#eql?.
   def ==(set)
     equal?(set) and return true
 
     set.is_a?(Set) && size == set.size or return false
 
     hash = @hash.dup
     set.all? { |o| hash.include?(o) }
   end
 
   def hash	# :nodoc:
     @hash.hash
   end
 
   def eql?(o)	# :nodoc:
     return false unless o.is_a?(Set)
     @hash.eql?(o.instance_eval{@hash})
   end
 
   # Classifies the set by the return value of the given block and
   # returns a hash of {value => set of elements} pairs.  The block is
   # called once for each element of the set, passing the element as
   # parameter.
   #
   # e.g.:
   #
   #   require 'set'
   #   files = Set.new(Dir.glob("*.rb"))
   #   hash = files.classify { |f| File.mtime(f).year }
   #   p hash    # => {2000=>#<Set: {"a.rb", "b.rb"}>,
   #             #     2001=>#<Set: {"c.rb", "d.rb", "e.rb"}>,
   #             #     2002=>#<Set: {"f.rb"}>}
   def classify # :yields: o
     h = {}
 
     each { |i|
       x = yield(i)
       (h[x] ||= self.class.new).add(i)
     }
 
     h
   end
 
   # Divides the set into a set of subsets according to the commonality
   # defined by the given block.
   #
   # If the arity of the block is 2, elements o1 and o2 are in common
   # if block.call(o1, o2) is true.  Otherwise, elements o1 and o2 are
   # in common if block.call(o1) == block.call(o2).
   #
   # e.g.:
   #
   #   require 'set'
   #   numbers = Set[1, 3, 4, 6, 9, 10, 11]
   #   set = numbers.divide { |i,j| (i - j).abs == 1 }
   #   p set     # => #<Set: {#<Set: {1}>,
   #             #            #<Set: {11, 9, 10}>,
   #             #            #<Set: {3, 4}>,
   #             #            #<Set: {6}>}>
   def divide(&func)
     if func.arity == 2
       require 'tsort'
 
       class << dig = {}		# :nodoc:
 	include TSort
 
 	alias tsort_each_node each_key
 	def tsort_each_child(node, &block)
 	  fetch(node).each(&block)
 	end
       end
 
       each { |u|
 	dig[u] = a = []
 	each{ |v| func.call(u, v) and a << v }
       }
 
       set = Set.new()
       dig.each_strongly_connected_component { |css|
 	set.add(self.class.new(css))
       }
       set
     else
       Set.new(classify(&func).values)
     end
   end
 
   InspectKey = :__inspect_key__         # :nodoc:
 
   # Returns a string containing a human-readable representation of the
   # set. ("#<Set: {element1, element2, ...}>")
   def inspect
     ids = (Thread.current[InspectKey] ||= [])
 
     if ids.include?(object_id)
       return sprintf('#<%s: {...}>', self.class.name)
     end
 
     begin
       ids << object_id
       return sprintf('#<%s: {%s}>', self.class, to_a.inspect[1..-2])
     ensure
       ids.pop
     end
   end
 
   def pretty_print(pp)	# :nodoc:
     pp.text sprintf('#<%s: {', self.class.name)
     pp.nest(1) {
       pp.seplist(self) { |o|
 	pp.pp o
       }
     }
     pp.text "}>"
   end
 
   def pretty_print_cycle(pp)	# :nodoc:
     pp.text sprintf('#<%s: {%s}>', self.class.name, empty? ? '' : '...')
   end
 end
 
 # 
 # SortedSet implements a Set that guarantees that it's element are
 # yielded in sorted order (according to the return values of their
 # #<=> methods) when iterating over them.
 # 
-# All elements that are added to a SortedSet must include the
-# Comparable module.
+# All elements that are added to a SortedSet must respond to the <=>
+# method for comparison.
 # 
 # Also, all elements must be <em>mutually comparable</em>: <tt>el1 <=>
 # el2</tt> must not return <tt>nil</tt> for any elements <tt>el1</tt>
 # and <tt>el2</tt>, else an ArgumentError will be raised when
 # iterating over the SortedSet.
 #
 # == Example
 # 
 #   require "set"
 #   
 #   set = SortedSet.new(2, 1, 5, 6, 4, 5, 3, 3, 3)
 #   ary = []
 #   
 #   set.each do |obj|
 #     ary << obj
 #   end
 #   
 #   p ary # => [1, 2, 3, 4, 5, 6]
 #   
 #   set2 = SortedSet.new(1, 2, "3")
 #   set2.each { |obj| } # => raises ArgumentError: comparison of Fixnum with String failed
 #   
 class SortedSet < Set
   @@setup = false
 
   class << self
     def [](*ary)	# :nodoc:
       new(ary)
     end
 
     def setup	# :nodoc:
       @@setup and return
 
       module_eval {
         # a hack to shut up warning
         alias old_init initialize
         remove_method :old_init
       }
       begin
 	require 'rbtree'
 
 	module_eval %{
 	  def initialize(*args, &block)
 	    @hash = RBTree.new
 	    super
 	  end
 	  
 	  def add(o)
-	    o.is_a?(Comparable) or raise ArgumentError, "value must be comparable"
+	    o.respond_to?(:<=>) or raise ArgumentError, "value must repond to <=>"
 	    super
 	  end
 	  alias << add
 	}
       rescue LoadError
 	module_eval %{
 	  def initialize(*args, &block)
 	    @keys = nil
 	    super
 	  end
 
 	  def clear
 	    @keys = nil
 	    super
 	  end
 
 	  def replace(enum)
 	    @keys = nil
 	    super
 	  end
 
 	  def add(o)
-	    o.is_a?(Comparable) or raise ArgumentError, "value must be comparable"
+	    o.respond_to?(:<=>) or raise ArgumentError, "value must respond to <=>"
 	    @keys = nil
 	    super
 	  end
 	  alias << add
 
 	  def delete(o)
 	    @keys = nil
 	    @hash.delete(o)
 	    self
 	  end
 
 	  def delete_if
 	    n = @hash.size
 	    super
 	    @keys = nil if @hash.size != n
 	    self
 	  end
 
 	  def merge(enum)
 	    @keys = nil
 	    super
 	  end
 
 	  def each
 	    block_given? or return enum_for(__method__)
 	    to_a.each { |o| yield(o) }
 	    self
 	  end
 
 	  def to_a
 	    (@keys = @hash.keys).sort! unless @keys
 	    @keys
 	  end
 	}
       end
 
       @@setup = true
     end
   end
 
   def initialize(*args, &block)	# :nodoc:
     SortedSet.setup
     initialize(*args, &block)
   end
 end
 
 module Enumerable
   # Makes a set from the enumerable object with given arguments.
   # Needs to +require "set"+ to use this method.
   def to_set(klass = Set, *args, &block)
     klass.new(self, *args, &block)
   end
 end
 
 # =begin
 # == RestricedSet class
 # RestricedSet implements a set with restrictions defined by a given
 # block.
 # 
 # === Super class
 #     Set
 # 
 # === Class Methods
 # --- RestricedSet::new(enum = nil) { |o| ... }
 # --- RestricedSet::new(enum = nil) { |rset, o| ... }
 #     Creates a new restricted set containing the elements of the given
 #     enumerable object.  Restrictions are defined by the given block.
 # 
 #     If the block's arity is 2, it is called with the RestrictedSet
 #     itself and an object to see if the object is allowed to be put in
 #     the set.
 # 
 #     Otherwise, the block is called with an object to see if the object
 #     is allowed to be put in the set.
 # 
 # === Instance Methods
 # --- restriction_proc
 #     Returns the restriction procedure of the set.
 # 
 # =end
 # 
 # class RestricedSet < Set
 #   def initialize(*args, &block)
 #     @proc = block or raise ArgumentError, "missing a block"
 # 
 #     if @proc.arity == 2
 #       instance_eval %{
 # 	def add(o)
 # 	  @hash[o] = true if @proc.call(self, o)
 # 	  self
 # 	end
 # 	alias << add
 # 
 # 	def add?(o)
 # 	  if include?(o) || !@proc.call(self, o)
 # 	    nil
 # 	  else
 # 	    @hash[o] = true
 # 	    self
 # 	  end
 # 	end
 # 
 # 	def replace(enum)
 # 	  enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
 # 	  clear
 # 	  enum.each { |o| add(o) }
 # 
 # 	  self
 # 	end
 # 
 # 	def merge(enum)
 # 	  enum.is_a?(Enumerable) or raise ArgumentError, "value must be enumerable"
 # 	  enum.each { |o| add(o) }
 # 
 # 	  self
 # 	end
 #       }
 #     else
 #       instance_eval %{
 # 	def add(o)
 #         if @proc.call(o)
 # 	    @hash[o] = true 
 #         end
 # 	  self
 # 	end
 # 	alias << add
 # 
 # 	def add?(o)
 # 	  if include?(o) || !@proc.call(o)
 # 	    nil
 # 	  else
 # 	    @hash[o] = true
 # 	    self
 # 	  end
 # 	end
 #       }
 #     end
 # 
 #     super(*args)
 #   end
 # 
 #   def restriction_proc
 #     @proc
 #   end
 # end
 
 if $0 == __FILE__
   eval DATA.read, nil, $0, __LINE__+4
 end
 
 __END__
 
 require 'test/unit'
 
 class TC_Set < Test::Unit::TestCase
   def test_aref
     assert_nothing_raised {
       Set[]
       Set[nil]
       Set[1,2,3]
     }
 
     assert_equal(0, Set[].size)
     assert_equal(1, Set[nil].size)
     assert_equal(1, Set[[]].size)
     assert_equal(1, Set[[nil]].size)
 
     set = Set[2,4,6,4]
     assert_equal(Set.new([2,4,6]), set)
   end
 
   def test_s_new
     assert_nothing_raised {
       Set.new()
       Set.new(nil)
       Set.new([])
       Set.new([1,2])
       Set.new('a'..'c')
       Set.new('XYZ')
     }
     assert_raises(ArgumentError) {
       Set.new(false)
     }
     assert_raises(ArgumentError) {
       Set.new(1)
     }
     assert_raises(ArgumentError) {
       Set.new(1,2)
     }
 
     assert_equal(0, Set.new().size)
     assert_equal(0, Set.new(nil).size)
     assert_equal(0, Set.new([]).size)
     assert_equal(1, Set.new([nil]).size)
 
     ary = [2,4,6,4]
     set = Set.new(ary)
     ary.clear
     assert_equal(false, set.empty?)
     assert_equal(3, set.size)
 
     ary = [1,2,3]
 
     s = Set.new(ary) { |o| o * 2 }
     assert_equal([2,4,6], s.sort)
   end
 
   def test_clone
     set1 = Set.new
     set2 = set1.clone
     set1 << 'abc'
     assert_equal(Set.new, set2)
   end
 
   def test_dup
     set1 = Set[1,2]
     set2 = set1.dup
 
     assert_not_same(set1, set2)
 
     assert_equal(set1, set2)
 
     set1.add(3)
 
     assert_not_equal(set1, set2)
   end
 
   def test_size
     assert_equal(0, Set[].size)
     assert_equal(2, Set[1,2].size)
     assert_equal(2, Set[1,2,1].size)
   end
 
   def test_empty?
     assert_equal(true, Set[].empty?)
     assert_equal(false, Set[1, 2].empty?)
   end
 
   def test_clear
     set = Set[1,2]
     ret = set.clear
 
     assert_same(set, ret)
     assert_equal(true, set.empty?)
   end
 
   def test_replace
     set = Set[1,2]
     ret = set.replace('a'..'c')
 
     assert_same(set, ret)
     assert_equal(Set['a','b','c'], set)
   end
 
   def test_to_a
     set = Set[1,2,3,2]
     ary = set.to_a
 
     assert_equal([1,2,3], ary.sort)
   end
 
   def test_flatten
     # test1
     set1 = Set[
       1,
       Set[
 	5,
 	Set[7,
 	  Set[0]
 	],
 	Set[6,2],
 	1
       ],
       3,
       Set[3,4]
     ]
 
     set2 = set1.flatten
     set3 = Set.new(0..7)
 
     assert_not_same(set2, set1)
     assert_equal(set3, set2)
 
     # test2; destructive
     orig_set1 = set1
     set1.flatten!
 
     assert_same(orig_set1, set1)
     assert_equal(set3, set1)
 
     # test3; multiple occurrences of a set in an set
     set1 = Set[1, 2]
     set2 = Set[set1, Set[set1, 4], 3]
 
     assert_nothing_raised {
       set2.flatten!
     }
 
     assert_equal(Set.new(1..4), set2)
 
     # test4; recursion
     set2 = Set[]
     set1 = Set[1, set2]
     set2.add(set1)
 
     assert_raises(ArgumentError) {
       set1.flatten!
     }
 
     # test5; miscellaneous
     empty = Set[]
     set =  Set[Set[empty, "a"],Set[empty, "b"]]
 
     assert_nothing_raised {
       set.flatten
     }
 
     set1 = empty.merge(Set["no_more", set])
 
     assert_nil(Set.new(0..31).flatten!)
 
     x = Set[Set[],Set[1,2]].flatten!
     y = Set[1,2]
 
     assert_equal(x, y)
   end
 
   def test_include?
     set = Set[1,2,3]
 
     assert_equal(true, set.include?(1))
     assert_equal(true, set.include?(2))
     assert_equal(true, set.include?(3))
     assert_equal(false, set.include?(0))
     assert_equal(false, set.include?(nil))
 
     set = Set["1",nil,"2",nil,"0","1",false]
     assert_equal(true, set.include?(nil))
     assert_equal(true, set.include?(false))
     assert_equal(true, set.include?("1"))
     assert_equal(false, set.include?(0))
     assert_equal(false, set.include?(true))
   end
 
   def test_superset?
     set = Set[1,2,3]
 
     assert_raises(ArgumentError) {
       set.superset?()
     }
 
     assert_raises(ArgumentError) {
       set.superset?(2)
     }
 
     assert_raises(ArgumentError) {
       set.superset?([2])
     }
 
     assert_equal(true, set.superset?(Set[]))
     assert_equal(true, set.superset?(Set[1,2]))
     assert_equal(true, set.superset?(Set[1,2,3]))
     assert_equal(false, set.superset?(Set[1,2,3,4]))
     assert_equal(false, set.superset?(Set[1,4]))
 
     assert_equal(true, Set[].superset?(Set[]))
   end
 
   def test_proper_superset?
     set = Set[1,2,3]
 
     assert_raises(ArgumentError) {
       set.proper_superset?()
     }
 
     assert_raises(ArgumentError) {
       set.proper_superset?(2)
     }
 
     assert_raises(ArgumentError) {
       set.proper_superset?([2])
     }
 
     assert_equal(true, set.proper_superset?(Set[]))
     assert_equal(true, set.proper_superset?(Set[1,2]))
     assert_equal(false, set.proper_superset?(Set[1,2,3]))
     assert_equal(false, set.proper_superset?(Set[1,2,3,4]))
     assert_equal(false, set.proper_superset?(Set[1,4]))
 
     assert_equal(false, Set[].proper_superset?(Set[]))
   end
 
   def test_subset?
     set = Set[1,2,3]
 
     assert_raises(ArgumentError) {
       set.subset?()
     }
 
     assert_raises(ArgumentError) {
       set.subset?(2)
     }
 
     assert_raises(ArgumentError) {
       set.subset?([2])
     }
 
     assert_equal(true, set.subset?(Set[1,2,3,4]))
     assert_equal(true, set.subset?(Set[1,2,3]))
     assert_equal(false, set.subset?(Set[1,2]))
     assert_equal(false, set.subset?(Set[]))
 
     assert_equal(true, Set[].subset?(Set[1]))
     assert_equal(true, Set[].subset?(Set[]))
   end
 
   def test_proper_subset?
     set = Set[1,2,3]
 
     assert_raises(ArgumentError) {
       set.proper_subset?()
     }
 
     assert_raises(ArgumentError) {
       set.proper_subset?(2)
     }
 
     assert_raises(ArgumentError) {
       set.proper_subset?([2])
     }
 
     assert_equal(true, set.proper_subset?(Set[1,2,3,4]))
     assert_equal(false, set.proper_subset?(Set[1,2,3]))
     assert_equal(false, set.proper_subset?(Set[1,2]))
     assert_equal(false, set.proper_subset?(Set[]))
 
     assert_equal(false, Set[].proper_subset?(Set[]))
   end
 
   def test_each
     ary = [1,3,5,7,10,20]
     set = Set.new(ary)
 
     ret = set.each { |o| }
     assert_same(set, ret)
 
     e = set.each
     assert_instance_of(Enumerable::Enumerator, e)
 
     assert_nothing_raised {
       set.each { |o|
 	ary.delete(o) or raise "unexpected element: #{o}"
       }
 
       ary.empty? or raise "forgotten elements: #{ary.join(', ')}"
     }
   end
 
   def test_add
     set = Set[1,2,3]
 
     ret = set.add(2)
     assert_same(set, ret)
     assert_equal(Set[1,2,3], set)
 
     ret = set.add?(2)
     assert_nil(ret)
     assert_equal(Set[1,2,3], set)
 
     ret = set.add(4)
     assert_same(set, ret)
     assert_equal(Set[1,2,3,4], set)
 
     ret = set.add?(5)
     assert_same(set, ret)
     assert_equal(Set[1,2,3,4,5], set)
   end
 
   def test_delete
     set = Set[1,2,3]
 
     ret = set.delete(4)
     assert_same(set, ret)
     assert_equal(Set[1,2,3], set)
 
     ret = set.delete?(4)
     assert_nil(ret)
     assert_equal(Set[1,2,3], set)
 
     ret = set.delete(2)
     assert_equal(set, ret)
     assert_equal(Set[1,3], set)
 
     ret = set.delete?(1)
     assert_equal(set, ret)
     assert_equal(Set[3], set)
   end
 
   def test_delete_if
     set = Set.new(1..10)
     ret = set.delete_if { |i| i > 10 }
     assert_same(set, ret)
     assert_equal(Set.new(1..10), set)
 
     set = Set.new(1..10)
     ret = set.delete_if { |i| i % 3 == 0 }
     assert_same(set, ret)
     assert_equal(Set[1,2,4,5,7,8,10], set)
   end
 
   def test_collect!
     set = Set[1,2,3,'a','b','c',-1..1,2..4]
 
     ret = set.collect! { |i|
       case i
       when Numeric
 	i * 2
       when String
 	i.upcase
       else
 	nil
       end
     }
 
     assert_same(set, ret)
     assert_equal(Set[2,4,6,'A','B','C',nil], set)
   end
 
   def test_reject!
     set = Set.new(1..10)
 
     ret = set.reject! { |i| i > 10 }
     assert_nil(ret)
     assert_equal(Set.new(1..10), set)
 
     ret = set.reject! { |i| i % 3 == 0 }
     assert_same(set, ret)
     assert_equal(Set[1,2,4,5,7,8,10], set)
   end
 
   def test_merge
     set = Set[1,2,3]
 
     ret = set.merge([2,4,6])
     assert_same(set, ret)
     assert_equal(Set[1,2,3,4,6], set)
   end
 
   def test_subtract
     set = Set[1,2,3]
 
     ret = set.subtract([2,4,6])
     assert_same(set, ret)
     assert_equal(Set[1,3], set)
   end
 
   def test_plus
     set = Set[1,2,3]
 
     ret = set + [2,4,6]
     assert_not_same(set, ret)
     assert_equal(Set[1,2,3,4,6], ret)
   end
 
   def test_minus
     set = Set[1,2,3]
 
     ret = set - [2,4,6]
     assert_not_same(set, ret)
     assert_equal(Set[1,3], ret)
   end
 
   def test_and
     set = Set[1,2,3,4]
 
     ret = set & [2,4,6]
     assert_not_same(set, ret)
     assert_equal(Set[2,4], ret)
   end
 
   def test_xor
     set = Set[1,2,3,4]
     ret = set ^ [2,4,5,5]
     assert_not_same(set, ret)
     assert_equal(Set[1,3,5], ret)
   end
 
   def test_eq
     set1 = Set[2,3,1]
     set2 = Set[1,2,3]
 
     assert_equal(set1, set1)
     assert_equal(set1, set2)
     assert_not_equal(Set[1], [1])
 
     set1 = Class.new(Set)["a", "b"]
     set2 = Set["a", "b", set1]
     set1 = set1.add(set1.clone)
 
 #    assert_equal(set1, set2)
 #    assert_equal(set2, set1)
     assert_equal(set2, set2.clone)
     assert_equal(set1.clone, set1)
 
     assert_not_equal(Set[Exception.new,nil], Set[Exception.new,Exception.new], "[ruby-dev:26127]")
   end
 
   # def test_hash
   # end
 
   # def test_eql?
   # end
 
   def test_classify
     set = Set.new(1..10)
     ret = set.classify { |i| i % 3 }
 
     assert_equal(3, ret.size)
     assert_instance_of(Hash, ret)
     ret.each_value { |value| assert_instance_of(Set, value) }
     assert_equal(Set[3,6,9], ret[0])
     assert_equal(Set[1,4,7,10], ret[1])
     assert_equal(Set[2,5,8], ret[2])
   end
 
   def test_divide
     set = Set.new(1..10)
     ret = set.divide { |i| i % 3 }
 
     assert_equal(3, ret.size)
     n = 0
     ret.each { |s| n += s.size }
     assert_equal(set.size, n)
     assert_equal(set, ret.flatten)
 
     set = Set[7,10,5,11,1,3,4,9,0]
     ret = set.divide { |a,b| (a - b).abs == 1 }
 
     assert_equal(4, ret.size)
     n = 0
     ret.each { |s| n += s.size }
     assert_equal(set.size, n)
     assert_equal(set, ret.flatten)
     ret.each { |s|
       if s.include?(0)
 	assert_equal(Set[0,1], s)
       elsif s.include?(3)
 	assert_equal(Set[3,4,5], s)
       elsif s.include?(7)
 	assert_equal(Set[7], s)
       elsif s.include?(9)
 	assert_equal(Set[9,10,11], s)
       else
 	raise "unexpected group: #{s.inspect}"
       end
     }
   end
 
   def test_inspect
     set1 = Set[1]
 
     assert_equal('#<Set: {1}>', set1.inspect)
 
     set2 = Set[Set[0], 1, 2, set1]
     assert_equal(false, set2.inspect.include?('#<Set: {...}>'))
 
     set1.add(set2)
     assert_equal(true, set1.inspect.include?('#<Set: {...}>'))
   end
 
   # def test_pretty_print
   # end
 
   # def test_pretty_print_cycle
   # end
 end
 
 class TC_SortedSet < Test::Unit::TestCase
   def test_sortedset
     s = SortedSet[4,5,3,1,2]
 
     assert_equal([1,2,3,4,5], s.to_a)
 
     prev = nil
     s.each { |o| assert(prev < o) if prev; prev = o }
     assert_not_nil(prev)
 
     s.map! { |o| -2 * o }
 
     assert_equal([-10,-8,-6,-4,-2], s.to_a)
 
     prev = nil
     ret = s.each { |o| assert(prev < o) if prev; prev = o }
     assert_not_nil(prev)
     assert_same(s, ret)
 
     s = SortedSet.new([2,1,3]) { |o| o * -2 }
     assert_equal([-6,-4,-2], s.to_a)
 
     s = SortedSet.new(['one', 'two', 'three', 'four'])
     a = []
     ret = s.delete_if { |o| a << o; o.start_with?('t') }
     assert_same(s, ret)
     assert_equal(['four', 'one'], s.to_a)
     assert_equal(['four', 'one', 'three', 'two'], a)
 
     s = SortedSet.new(['one', 'two', 'three', 'four'])
     a = []
     ret = s.reject! { |o| a << o; o.start_with?('t') }
     assert_same(s, ret)
     assert_equal(['four', 'one'], s.to_a)
     assert_equal(['four', 'one', 'three', 'two'], a)
 
     s = SortedSet.new(['one', 'two', 'three', 'four'])
     a = []
     ret = s.reject! { |o| a << o; false }
     assert_same(nil, ret)
     assert_equal(['four', 'one', 'three', 'two'], s.to_a)
     assert_equal(['four', 'one', 'three', 'two'], a)
   end
 end
 
 class TC_Enumerable < Test::Unit::TestCase
   def test_to_set
     ary = [2,5,4,3,2,1,3]
 
     set = ary.to_set
     assert_instance_of(Set, set)
     assert_equal([1,2,3,4,5], set.sort)
 
     set = ary.to_set { |o| o * -2 }
     assert_instance_of(Set, set)
     assert_equal([-10,-8,-6,-4,-2], set.sort)
 
     set = ary.to_set(SortedSet)
     assert_instance_of(SortedSet, set)
     assert_equal([1,2,3,4,5], set.to_a)
 
     set = ary.to_set(SortedSet) { |o| o * -2 }
     assert_instance_of(SortedSet, set)
     assert_equal([-10,-8,-6,-4,-2], set.sort)
   end
 end
 
 # class TC_RestricedSet < Test::Unit::TestCase
 #   def test_s_new
 #     assert_raises(ArgumentError) { RestricedSet.new }
 # 
 #     s = RestricedSet.new([-1,2,3]) { |o| o > 0 }
 #     assert_equal([2,3], s.sort)
 #   end
 # 
 #   def test_restriction_proc
 #     s = RestricedSet.new([-1,2,3]) { |o| o > 0 }
 # 
 #     f = s.restriction_proc
 #     assert_instance_of(Proc, f)
 #     assert(f[1])
 #     assert(!f[0])
 #   end
 # 
 #   def test_replace
 #     s = RestricedSet.new(-3..3) { |o| o > 0 }
 #     assert_equal([1,2,3], s.sort)
 # 
 #     s.replace([-2,0,3,4,5])
 #     assert_equal([3,4,5], s.sort)
 #   end
 # 
 #   def test_merge
 #     s = RestricedSet.new { |o| o > 0 }
 #     s.merge(-5..5)
 #     assert_equal([1,2,3,4,5], s.sort)
 # 
 #     s.merge([10,-10,-8,8])
 #     assert_equal([1,2,3,4,5,8,10], s.sort)
 #   end
 # end
diff --git a/lib/ruby/1.9/fileutils.rb b/lib/ruby/1.9/fileutils.rb
index c65f007b8a..f2afdfbcf7 100644
--- a/lib/ruby/1.9/fileutils.rb
+++ b/lib/ruby/1.9/fileutils.rb
@@ -179,1410 +179,1412 @@ module FileUtils
   #
   # Options: mode noop verbose
   #
   # Creates a directory and all its parent directories.
   # For example,
   #
   #   FileUtils.mkdir_p '/usr/local/lib/ruby'
   #
   # causes to make following directories, if it does not exist.
   #     * /usr
   #     * /usr/local
   #     * /usr/local/lib
   #     * /usr/local/lib/ruby
   #
   # You can pass several directories at a time in a list.
   #
   def mkdir_p(list, options = {})
     fu_check_options options, OPT_TABLE['mkdir_p']
     list = fu_list(list)
     fu_output_message "mkdir -p #{options[:mode] ? ('-m %03o ' % options[:mode]) : ''}#{list.join ' '}" if options[:verbose]
     return *list if options[:noop]
 
     list.map {|path| path.sub(%r</\z>, '') }.each do |path|
       # optimize for the most common case
       begin
         fu_mkdir path, options[:mode]
         next
       rescue SystemCallError
         next if File.directory?(path)
       end
 
       stack = []
       until path == stack.last   # dirname("/")=="/", dirname("C:/")=="C:/"
         stack.push path
         path = File.dirname(path)
       end
       stack.reverse_each do |dir|
         begin
           fu_mkdir dir, options[:mode]
         rescue SystemCallError => err
           raise unless File.directory?(dir)
         end
       end
     end
 
     return *list
   end
   module_function :mkdir_p
 
   alias mkpath    mkdir_p
   alias makedirs  mkdir_p
   module_function :mkpath
   module_function :makedirs
 
   OPT_TABLE['mkdir_p']  =
   OPT_TABLE['mkpath']   =
   OPT_TABLE['makedirs'] = [:mode, :noop, :verbose]
 
   def fu_mkdir(path, mode)   #:nodoc:
     path = path.sub(%r</\z>, '')
     if mode
       Dir.mkdir path, mode
       File.chmod mode, path
     else
       Dir.mkdir path
     end
   end
   private_module_function :fu_mkdir
 
   #
   # Options: noop, verbose
   #
   # Removes one or more directories.
   #
   #   FileUtils.rmdir 'somedir'
   #   FileUtils.rmdir %w(somedir anydir otherdir)
   #   # Does not really remove directory; outputs message.
   #   FileUtils.rmdir 'somedir', :verbose => true, :noop => true
   #
   def rmdir(list, options = {})
     fu_check_options options, OPT_TABLE['rmdir']
     list = fu_list(list)
     parents = options[:parents]
     fu_output_message "rmdir #{parents ? '-p ' : ''}#{list.join ' '}" if options[:verbose]
     return if options[:noop]
     list.each do |dir|
       begin
         Dir.rmdir(dir = dir.sub(%r</\z>, ''))
         if parents
           until (parent = File.dirname(dir)) == '.' or parent == dir
             Dir.rmdir(dir)
           end
         end
       rescue Errno::ENOTEMPTY, Errno::ENOENT
       end
     end
   end
   module_function :rmdir
 
   OPT_TABLE['rmdir'] = [:parents, :noop, :verbose]
 
   #
   # Options: force noop verbose
   #
   # <b><tt>ln(old, new, options = {})</tt></b>
   #
   # Creates a hard link +new+ which points to +old+.
   # If +new+ already exists and it is a directory, creates a link +new/old+.
   # If +new+ already exists and it is not a directory, raises Errno::EEXIST.
   # But if :force option is set, overwrite +new+.
   #
   #   FileUtils.ln 'gcc', 'cc', :verbose => true
   #   FileUtils.ln '/usr/bin/emacs21', '/usr/bin/emacs'
   #
   # <b><tt>ln(list, destdir, options = {})</tt></b>
   #
   # Creates several hard links in a directory, with each one pointing to the
   # item in +list+.  If +destdir+ is not a directory, raises Errno::ENOTDIR.
   #
   #   include FileUtils
   #   cd '/sbin'
   #   FileUtils.ln %w(cp mv mkdir), '/bin'   # Now /sbin/cp and /bin/cp are linked.
   #
   def ln(src, dest, options = {})
     fu_check_options options, OPT_TABLE['ln']
     fu_output_message "ln#{options[:force] ? ' -f' : ''} #{[src,dest].flatten.join ' '}" if options[:verbose]
     return if options[:noop]
     fu_each_src_dest0(src, dest) do |s,d|
       remove_file d, true if options[:force]
       File.link s, d
     end
   end
   module_function :ln
 
   alias link ln
   module_function :link
 
   OPT_TABLE['ln']   =
   OPT_TABLE['link'] = [:force, :noop, :verbose]
 
   #
   # Options: force noop verbose
   #
   # <b><tt>ln_s(old, new, options = {})</tt></b>
   #
   # Creates a symbolic link +new+ which points to +old+.  If +new+ already
   # exists and it is a directory, creates a symbolic link +new/old+.  If +new+
   # already exists and it is not a directory, raises Errno::EEXIST.  But if
   # :force option is set, overwrite +new+.
   #
   #   FileUtils.ln_s '/usr/bin/ruby', '/usr/local/bin/ruby'
   #   FileUtils.ln_s 'verylongsourcefilename.c', 'c', :force => true
   #
   # <b><tt>ln_s(list, destdir, options = {})</tt></b>
   #
   # Creates several symbolic links in a directory, with each one pointing to the
   # item in +list+.  If +destdir+ is not a directory, raises Errno::ENOTDIR.
   #
   # If +destdir+ is not a directory, raises Errno::ENOTDIR.
   #
   #   FileUtils.ln_s Dir.glob('bin/*.rb'), '/home/aamine/bin'
   #
   def ln_s(src, dest, options = {})
     fu_check_options options, OPT_TABLE['ln_s']
     fu_output_message "ln -s#{options[:force] ? 'f' : ''} #{[src,dest].flatten.join ' '}" if options[:verbose]
     return if options[:noop]
     fu_each_src_dest0(src, dest) do |s,d|
       remove_file d, true if options[:force]
       File.symlink s, d
     end
   end
   module_function :ln_s
 
   alias symlink ln_s
   module_function :symlink
 
   OPT_TABLE['ln_s']    =
   OPT_TABLE['symlink'] = [:force, :noop, :verbose]
 
   #
   # Options: noop verbose
   #
   # Same as
   #   #ln_s(src, dest, :force)
   #
   def ln_sf(src, dest, options = {})
     fu_check_options options, OPT_TABLE['ln_sf']
     options = options.dup
     options[:force] = true
     ln_s src, dest, options
   end
   module_function :ln_sf
 
   OPT_TABLE['ln_sf'] = [:noop, :verbose]
 
   #
   # Options: preserve noop verbose
   #
   # Copies a file content +src+ to +dest+.  If +dest+ is a directory,
   # copies +src+ to +dest/src+.
   #
   # If +src+ is a list of files, then +dest+ must be a directory.
   #
   #   FileUtils.cp 'eval.c', 'eval.c.org'
   #   FileUtils.cp %w(cgi.rb complex.rb date.rb), '/usr/lib/ruby/1.6'
   #   FileUtils.cp %w(cgi.rb complex.rb date.rb), '/usr/lib/ruby/1.6', :verbose => true
   #   FileUtils.cp 'symlink', 'dest'   # copy content, "dest" is not a symlink
   #
   def cp(src, dest, options = {})
     fu_check_options options, OPT_TABLE['cp']
     fu_output_message "cp#{options[:preserve] ? ' -p' : ''} #{[src,dest].flatten.join ' '}" if options[:verbose]
     return if options[:noop]
     fu_each_src_dest(src, dest) do |s, d|
       copy_file s, d, options[:preserve]
     end
   end
   module_function :cp
 
   alias copy cp
   module_function :copy
 
   OPT_TABLE['cp']   =
   OPT_TABLE['copy'] = [:preserve, :noop, :verbose]
 
   #
   # Options: preserve noop verbose dereference_root remove_destination
   #
   # Copies +src+ to +dest+. If +src+ is a directory, this method copies
   # all its contents recursively. If +dest+ is a directory, copies
   # +src+ to +dest/src+.
   #
   # +src+ can be a list of files.
   #
   #   # Installing ruby library "mylib" under the site_ruby
   #   FileUtils.rm_r site_ruby + '/mylib', :force
   #   FileUtils.cp_r 'lib/', site_ruby + '/mylib'
   #
   #   # Examples of copying several files to target directory.
   #   FileUtils.cp_r %w(mail.rb field.rb debug/), site_ruby + '/tmail'
   #   FileUtils.cp_r Dir.glob('*.rb'), '/home/aamine/lib/ruby', :noop => true, :verbose => true
   #
   #   # If you want to copy all contents of a directory instead of the
   #   # directory itself, c.f. src/x -> dest/x, src/y -> dest/y,
   #   # use following code.
   #   FileUtils.cp_r 'src/.', 'dest'     # cp_r('src', 'dest') makes src/dest,
   #                                      # but this doesn't.
   #
   def cp_r(src, dest, options = {})
     fu_check_options options, OPT_TABLE['cp_r']
     fu_output_message "cp -r#{options[:preserve] ? 'p' : ''}#{options[:remove_destination] ? ' --remove-destination' : ''} #{[src,dest].flatten.join ' '}" if options[:verbose]
     return if options[:noop]
     options = options.dup
     options[:dereference_root] = true unless options.key?(:dereference_root)
     fu_each_src_dest(src, dest) do |s, d|
       copy_entry s, d, options[:preserve], options[:dereference_root], options[:remove_destination]
     end
   end
   module_function :cp_r
 
   OPT_TABLE['cp_r'] = [:preserve, :noop, :verbose,
                        :dereference_root, :remove_destination]
 
   #
   # Copies a file system entry +src+ to +dest+.
   # If +src+ is a directory, this method copies its contents recursively.
   # This method preserves file types, c.f. symlink, directory...
   # (FIFO, device files and etc. are not supported yet)
   #
   # Both of +src+ and +dest+ must be a path name.
   # +src+ must exist, +dest+ must not exist.
   #
   # If +preserve+ is true, this method preserves owner, group, permissions
   # and modified time.
   #
   # If +dereference_root+ is true, this method dereference tree root.
   #
   # If +remove_destination+ is true, this method removes each destination file before copy.
   #
   def copy_entry(src, dest, preserve = false, dereference_root = false, remove_destination = false)
     Entry_.new(src, nil, dereference_root).traverse do |ent|
       destent = Entry_.new(dest, ent.rel, false)
       File.unlink destent.path if remove_destination && File.file?(destent.path)
       ent.copy destent.path
       ent.copy_metadata destent.path if preserve
     end
   end
   module_function :copy_entry
 
   #
   # Copies file contents of +src+ to +dest+.
   # Both of +src+ and +dest+ must be a path name.
   #
   def copy_file(src, dest, preserve = false, dereference = true)
     ent = Entry_.new(src, nil, dereference)
     ent.copy_file dest
     ent.copy_metadata dest if preserve
   end
   module_function :copy_file
 
   #
   # Copies stream +src+ to +dest+.
   # +src+ must respond to #read(n) and
   # +dest+ must respond to #write(str).
   #
   def copy_stream(src, dest)
     IO.copy_stream(src, dest)
   end
   module_function :copy_stream
 
   #
   # Options: force noop verbose
   #
   # Moves file(s) +src+ to +dest+.  If +file+ and +dest+ exist on the different
   # disk partition, the file is copied then the original file is removed.
   #
   #   FileUtils.mv 'badname.rb', 'goodname.rb'
   #   FileUtils.mv 'stuff.rb', '/notexist/lib/ruby', :force => true  # no error
   #
   #   FileUtils.mv %w(junk.txt dust.txt), '/home/aamine/.trash/'
   #   FileUtils.mv Dir.glob('test*.rb'), 'test', :noop => true, :verbose => true
   #
   def mv(src, dest, options = {})
     fu_check_options options, OPT_TABLE['mv']
     fu_output_message "mv#{options[:force] ? ' -f' : ''} #{[src,dest].flatten.join ' '}" if options[:verbose]
     return if options[:noop]
     fu_each_src_dest(src, dest) do |s, d|
       destent = Entry_.new(d, nil, true)
       begin
         if destent.exist?
           if destent.directory?
             raise Errno::EEXIST, dest
           else
             destent.remove_file if rename_cannot_overwrite_file?
           end
         end
         begin
           File.rename s, d
         rescue Errno::EXDEV
           copy_entry s, d, true
           if options[:secure]
             remove_entry_secure s, options[:force]
           else
             remove_entry s, options[:force]
           end
         end
       rescue SystemCallError
         raise unless options[:force]
       end
     end
   end
   module_function :mv
 
   alias move mv
   module_function :move
 
   OPT_TABLE['mv']   =
   OPT_TABLE['move'] = [:force, :noop, :verbose, :secure]
 
   def rename_cannot_overwrite_file?   #:nodoc:
     /cygwin|mswin|mingw|bccwin|emx/ =~ RUBY_PLATFORM
   end
   private_module_function :rename_cannot_overwrite_file?
 
   #
   # Options: force noop verbose
   #
   # Remove file(s) specified in +list+.  This method cannot remove directories.
   # All StandardErrors are ignored when the :force option is set.
   #
   #   FileUtils.rm %w( junk.txt dust.txt )
   #   FileUtils.rm Dir.glob('*.so')
   #   FileUtils.rm 'NotExistFile', :force => true   # never raises exception
   #
   def rm(list, options = {})
     fu_check_options options, OPT_TABLE['rm']
     list = fu_list(list)
     fu_output_message "rm#{options[:force] ? ' -f' : ''} #{list.join ' '}" if options[:verbose]
     return if options[:noop]
 
     list.each do |path|
       remove_file path, options[:force]
     end
   end
   module_function :rm
 
   alias remove rm
   module_function :remove
 
   OPT_TABLE['rm']     =
   OPT_TABLE['remove'] = [:force, :noop, :verbose]
 
   #
   # Options: noop verbose
   #
   # Equivalent to
   #
   #   #rm(list, :force => true)
   #
   def rm_f(list, options = {})
     fu_check_options options, OPT_TABLE['rm_f']
     options = options.dup
     options[:force] = true
     rm list, options
   end
   module_function :rm_f
 
   alias safe_unlink rm_f
   module_function :safe_unlink
 
   OPT_TABLE['rm_f']        =
   OPT_TABLE['safe_unlink'] = [:noop, :verbose]
 
   #
   # Options: force noop verbose secure
   #
   # remove files +list+[0] +list+[1]... If +list+[n] is a directory,
   # removes its all contents recursively. This method ignores
   # StandardError when :force option is set.
   #
   #   FileUtils.rm_r Dir.glob('/tmp/*')
   #   FileUtils.rm_r '/', :force => true          #  :-)
   #
   # WARNING: This method causes local vulnerability
   # if one of parent directories or removing directory tree are world
   # writable (including /tmp, whose permission is 1777), and the current
   # process has strong privilege such as Unix super user (root), and the
   # system has symbolic link.  For secure removing, read the documentation
   # of #remove_entry_secure carefully, and set :secure option to true.
   # Default is :secure=>false.
   #
   # NOTE: This method calls #remove_entry_secure if :secure option is set.
   # See also #remove_entry_secure.
   #
   def rm_r(list, options = {})
     fu_check_options options, OPT_TABLE['rm_r']
     # options[:secure] = true unless options.key?(:secure)
     list = fu_list(list)
     fu_output_message "rm -r#{options[:force] ? 'f' : ''} #{list.join ' '}" if options[:verbose]
     return if options[:noop]
     list.each do |path|
       if options[:secure]
         remove_entry_secure path, options[:force]
       else
         remove_entry path, options[:force]
       end
     end
   end
   module_function :rm_r
 
   OPT_TABLE['rm_r'] = [:force, :noop, :verbose, :secure]
 
   #
   # Options: noop verbose secure
   #
   # Equivalent to
   #
   #   #rm_r(list, :force => true)
   #
   # WARNING: This method causes local vulnerability.
   # Read the documentation of #rm_r first.
   #
   def rm_rf(list, options = {})
     fu_check_options options, OPT_TABLE['rm_rf']
     options = options.dup
     options[:force] = true
     rm_r list, options
   end
   module_function :rm_rf
 
   alias rmtree rm_rf
   module_function :rmtree
 
   OPT_TABLE['rm_rf']  =
   OPT_TABLE['rmtree'] = [:noop, :verbose, :secure]
 
   #
   # This method removes a file system entry +path+.  +path+ shall be a
   # regular file, a directory, or something.  If +path+ is a directory,
   # remove it recursively.  This method is required to avoid TOCTTOU
   # (time-of-check-to-time-of-use) local security vulnerability of #rm_r.
   # #rm_r causes security hole when:
   #
   #   * Parent directory is world writable (including /tmp).
   #   * Removing directory tree includes world writable directory.
   #   * The system has symbolic link.
   #
   # To avoid this security hole, this method applies special preprocess.
   # If +path+ is a directory, this method chown(2) and chmod(2) all
   # removing directories.  This requires the current process is the
   # owner of the removing whole directory tree, or is the super user (root).
   #
   # WARNING: You must ensure that *ALL* parent directories are not
   # world writable.  Otherwise this method does not work.
   # Only exception is temporary directory like /tmp and /var/tmp,
   # whose permission is 1777.
   #
   # WARNING: Only the owner of the removing directory tree, or Unix super
   # user (root) should invoke this method.  Otherwise this method does not
   # work.
   #
   # For details of this security vulnerability, see Perl's case:
   #
   #   http://www.cve.mitre.org/cgi-bin/cvename.cgi?name=CAN-2005-0448
   #   http://www.cve.mitre.org/cgi-bin/cvename.cgi?name=CAN-2004-0452
   #
   # For fileutils.rb, this vulnerability is reported in [ruby-dev:26100].
   #
   def remove_entry_secure(path, force = false)
     unless fu_have_symlink?
       remove_entry path, force
       return
     end
     fullpath = File.expand_path(path)
     st = File.lstat(fullpath)
     unless st.directory?
       File.unlink fullpath
       return
     end
     # is a directory.
     parent_st = File.stat(File.dirname(fullpath))
     unless parent_st.world_writable?
       remove_entry path, force
       return
     end
     unless parent_st.sticky?
       raise ArgumentError, "parent directory is world writable, FileUtils#remove_entry_secure does not work; abort: #{path.inspect} (parent directory mode #{'%o' % parent_st.mode})"
     end
     # freeze tree root
     euid = Process.euid
     File.open(fullpath + '/.') {|f|
       unless fu_stat_identical_entry?(st, f.stat)
         # symlink (TOC-to-TOU attack?)
         File.unlink fullpath
         return
       end
       f.chown euid, -1
       f.chmod 0700
     }
     # ---- tree root is frozen ----
     root = Entry_.new(path)
     root.preorder_traverse do |ent|
       if ent.directory?
         ent.chown euid, -1
         ent.chmod 0700
       end
     end
     root.postorder_traverse do |ent|
       begin
         ent.remove
       rescue
         raise unless force
       end
     end
   rescue
     raise unless force
   end
   module_function :remove_entry_secure
 
   def fu_have_symlink?   #:nodoc
     File.symlink nil, nil
   rescue NotImplementedError
     return false
   rescue
     return true
   end
   private_module_function :fu_have_symlink?
 
   def fu_stat_identical_entry?(a, b)   #:nodoc:
     a.dev == b.dev and a.ino == b.ino
   end
   private_module_function :fu_stat_identical_entry?
 
   #
   # This method removes a file system entry +path+.
   # +path+ might be a regular file, a directory, or something.
   # If +path+ is a directory, remove it recursively.
   #
   # See also #remove_entry_secure.
   #
   def remove_entry(path, force = false)
     Entry_.new(path).postorder_traverse do |ent|
       begin
         ent.remove
       rescue
         raise unless force
       end
     end
   rescue
     raise unless force
   end
   module_function :remove_entry
 
   #
   # Removes a file +path+.
   # This method ignores StandardError if +force+ is true.
   #
   def remove_file(path, force = false)
     Entry_.new(path).remove_file
   rescue
     raise unless force
   end
   module_function :remove_file
 
   #
   # Removes a directory +dir+ and its contents recursively.
   # This method ignores StandardError if +force+ is true.
   #
   def remove_dir(path, force = false)
     remove_entry path, force   # FIXME?? check if it is a directory
   end
   module_function :remove_dir
 
   #
   # Returns true if the contents of a file A and a file B are identical.
   #
   #   FileUtils.compare_file('somefile', 'somefile')  #=> true
   #   FileUtils.compare_file('/bin/cp', '/bin/mv')    #=> maybe false
   #
   def compare_file(a, b)
     return false unless File.size(a) == File.size(b)
     File.open(a, 'rb') {|fa|
       File.open(b, 'rb') {|fb|
         return compare_stream(fa, fb)
       }
     }
   end
   module_function :compare_file
 
   alias identical? compare_file
   alias cmp compare_file
   module_function :identical?
   module_function :cmp
 
   #
   # Returns true if the contents of a stream +a+ and +b+ are identical.
   #
   def compare_stream(a, b)
     bsize = fu_stream_blksize(a, b)
     sa = sb = nil
     while sa == sb
       sa = a.read(bsize)
       sb = b.read(bsize)
       unless sa and sb
         if sa.nil? and sb.nil?
           return true
         end
       end
     end
     false
   end
   module_function :compare_stream
 
   #
   # Options: mode preserve noop verbose
   #
   # If +src+ is not same as +dest+, copies it and changes the permission
   # mode to +mode+.  If +dest+ is a directory, destination is +dest+/+src+.
   # This method removes destination before copy.
   #
   #   FileUtils.install 'ruby', '/usr/local/bin/ruby', :mode => 0755, :verbose => true
   #   FileUtils.install 'lib.rb', '/usr/local/lib/ruby/site_ruby', :verbose => true
   #
   def install(src, dest, options = {})
     fu_check_options options, OPT_TABLE['install']
     fu_output_message "install -c#{options[:preserve] && ' -p'}#{options[:mode] ? (' -m 0%o' % options[:mode]) : ''} #{[src,dest].flatten.join ' '}" if options[:verbose]
     return if options[:noop]
     fu_each_src_dest(src, dest) do |s, d, st|
       unless File.exist?(d) and compare_file(s, d)
         remove_file d, true
         copy_file s, d
         File.utime st.atime, st.mtime, d if options[:preserve]
         File.chmod options[:mode], d if options[:mode]
       end
     end
   end
   module_function :install
 
   OPT_TABLE['install'] = [:mode, :preserve, :noop, :verbose]
 
   #
   # Options: noop verbose
   #
   # Changes permission bits on the named files (in +list+) to the bit pattern
   # represented by +mode+.
   #
   #   FileUtils.chmod 0755, 'somecommand'
   #   FileUtils.chmod 0644, %w(my.rb your.rb his.rb her.rb)
   #   FileUtils.chmod 0755, '/usr/bin/ruby', :verbose => true
   #
   def chmod(mode, list, options = {})
     fu_check_options options, OPT_TABLE['chmod']
     list = fu_list(list)
     fu_output_message sprintf('chmod %o %s', mode, list.join(' ')) if options[:verbose]
     return if options[:noop]
     list.each do |path|
       Entry_.new(path).chmod mode
     end
   end
   module_function :chmod
 
   OPT_TABLE['chmod'] = [:noop, :verbose]
 
   #
   # Options: noop verbose force
   #
   # Changes permission bits on the named files (in +list+)
   # to the bit pattern represented by +mode+.
   #
   #   FileUtils.chmod_R 0700, "/tmp/app.#{$$}"
   #
   def chmod_R(mode, list, options = {})
     fu_check_options options, OPT_TABLE['chmod_R']
     list = fu_list(list)
     fu_output_message sprintf('chmod -R%s %o %s',
                               (options[:force] ? 'f' : ''),
                               mode, list.join(' ')) if options[:verbose]
     return if options[:noop]
     list.each do |root|
       Entry_.new(root).traverse do |ent|
         begin
           ent.chmod mode
         rescue
           raise unless options[:force]
         end
       end
     end
   end
   module_function :chmod_R
 
   OPT_TABLE['chmod_R'] = [:noop, :verbose, :force]
 
   #
   # Options: noop verbose
   #
   # Changes owner and group on the named files (in +list+)
   # to the user +user+ and the group +group+.  +user+ and +group+
   # may be an ID (Integer/String) or a name (String).
   # If +user+ or +group+ is nil, this method does not change
   # the attribute.
   #
   #   FileUtils.chown 'root', 'staff', '/usr/local/bin/ruby'
   #   FileUtils.chown nil, 'bin', Dir.glob('/usr/bin/*'), :verbose => true
   #
   def chown(user, group, list, options = {})
     fu_check_options options, OPT_TABLE['chown']
     list = fu_list(list)
     fu_output_message sprintf('chown %s%s',
                               [user,group].compact.join(':') + ' ',
                               list.join(' ')) if options[:verbose]
     return if options[:noop]
     uid = fu_get_uid(user)
     gid = fu_get_gid(group)
     list.each do |path|
       Entry_.new(path).chown uid, gid
     end
   end
   module_function :chown
 
   OPT_TABLE['chown'] = [:noop, :verbose]
 
   #
   # Options: noop verbose force
   #
   # Changes owner and group on the named files (in +list+)
   # to the user +user+ and the group +group+ recursively.
   # +user+ and +group+ may be an ID (Integer/String) or
   # a name (String).  If +user+ or +group+ is nil, this
   # method does not change the attribute.
   #
   #   FileUtils.chown_R 'www', 'www', '/var/www/htdocs'
   #   FileUtils.chown_R 'cvs', 'cvs', '/var/cvs', :verbose => true
   #
   def chown_R(user, group, list, options = {})
     fu_check_options options, OPT_TABLE['chown_R']
     list = fu_list(list)
     fu_output_message sprintf('chown -R%s %s%s',
                               (options[:force] ? 'f' : ''),
                               [user,group].compact.join(':') + ' ',
                               list.join(' ')) if options[:verbose]
     return if options[:noop]
     uid = fu_get_uid(user)
     gid = fu_get_gid(group)
     return unless uid or gid
     list.each do |root|
       Entry_.new(root).traverse do |ent|
         begin
           ent.chown uid, gid
         rescue
           raise unless options[:force]
         end
       end
     end
   end
   module_function :chown_R
 
   OPT_TABLE['chown_R'] = [:noop, :verbose, :force]
 
   begin
     require 'etc'
 
     def fu_get_uid(user)   #:nodoc:
       return nil unless user
       case user
       when Integer
         user
       when /\A\d+\z/
         user.to_i
       else
         Etc.getpwnam(user).uid
       end
     end
     private_module_function :fu_get_uid
 
     def fu_get_gid(group)   #:nodoc:
       return nil unless group
       case group
       when Integer
         group
       when /\A\d+\z/
         group.to_i
       else
         Etc.getgrnam(group).gid
       end
     end
     private_module_function :fu_get_gid
 
   rescue LoadError
     # need Win32 support???
 
     def fu_get_uid(user)   #:nodoc:
       user    # FIXME
     end
     private_module_function :fu_get_uid
 
     def fu_get_gid(group)   #:nodoc:
       group   # FIXME
     end
     private_module_function :fu_get_gid
   end
 
   #
   # Options: noop verbose
   #
   # Updates modification time (mtime) and access time (atime) of file(s) in
   # +list+.  Files are created if they don't exist.
   #
   #   FileUtils.touch 'timestamp'
   #   FileUtils.touch Dir.glob('*.c');  system 'make'
   #
   def touch(list, options = {})
     fu_check_options options, OPT_TABLE['touch']
     list = fu_list(list)
     created = nocreate = options[:nocreate]
     t = options[:mtime]
     if options[:verbose]
       fu_output_message "touch #{nocreate ? ' -c' : ''}#{t ? t.strftime(' -t %Y%m%d%H%M.%S') : ''}#{list.join ' '}"
     end
     return if options[:noop]
     list.each do |path|
       created = nocreate
       begin
         File.utime(t, t, path)
       rescue Errno::ENOENT
         raise if created
         File.open(path, 'a') {
           ;
         }
         created = true
         retry if t
       end
     end
   end
   module_function :touch
 
   OPT_TABLE['touch'] = [:noop, :verbose, :mtime, :nocreate]
 
   private
 
   module StreamUtils_
     private
 
     def fu_windows?
       /mswin|mingw|bccwin|emx/ =~ RUBY_PLATFORM
     end
 
     def fu_copy_stream0(src, dest, blksize = nil)   #:nodoc:
       IO.copy_stream(src, dest)
     end
 
     def fu_stream_blksize(*streams)
       streams.each do |s|
         next unless s.respond_to?(:stat)
         size = fu_blksize(s.stat)
         return size if size
       end
       fu_default_blksize()
     end
 
     def fu_blksize(st)
       s = st.blksize
       return nil unless s
       return nil if s == 0
       s
     end
 
     def fu_default_blksize
       1024
     end
   end
 
   include StreamUtils_
   extend StreamUtils_
 
   class Entry_   #:nodoc: internal use only
     include StreamUtils_
 
     def initialize(a, b = nil, deref = false)
       @prefix = @rel = @path = nil
       if b
         @prefix = a
         @rel = b
       else
         @path = a
       end
       @deref = deref
       @stat = nil
       @lstat = nil
     end
 
     def inspect
       "\#<#{self.class} #{path()}>"
     end
 
     def path
       if @path
         File.path(@path)
       else
         join(@prefix, @rel)
       end
     end
 
     def prefix
       @prefix || @path
     end
 
     def rel
       @rel
     end
 
     def dereference?
       @deref
     end
 
     def exist?
       lstat! ? true : false
     end
 
     def file?
       s = lstat!
       s and s.file?
     end
 
     def directory?
       s = lstat!
       s and s.directory?
     end
 
     def symlink?
       s = lstat!
       s and s.symlink?
     end
 
     def chardev?
       s = lstat!
       s and s.chardev?
     end
 
     def blockdev?
       s = lstat!
       s and s.blockdev?
     end
 
     def socket?
       s = lstat!
       s and s.socket?
     end
 
     def pipe?
       s = lstat!
       s and s.pipe?
     end
 
     S_IF_DOOR = 0xD000
 
     def door?
       s = lstat!
       s and (s.mode & 0xF000 == S_IF_DOOR)
     end
 
     def entries
-      Dir.entries(path())\
+      opts = {}
+      opts[:encoding] = "UTF-8" if /mswin|mignw/ =~ RUBY_PLATFORM
+      Dir.entries(path(), opts)\
           .reject {|n| n == '.' or n == '..' }\
           .map {|n| Entry_.new(prefix(), join(rel(), n.untaint)) }
     end
 
     def stat
       return @stat if @stat
       if lstat() and lstat().symlink?
         @stat = File.stat(path())
       else
         @stat = lstat()
       end
       @stat
     end
 
     def stat!
       return @stat if @stat
       if lstat! and lstat!.symlink?
         @stat = File.stat(path())
       else
         @stat = lstat!
       end
       @stat
     rescue SystemCallError
       nil
     end
 
     def lstat
       if dereference?
         @lstat ||= File.stat(path())
       else
         @lstat ||= File.lstat(path())
       end
     end
 
     def lstat!
       lstat()
     rescue SystemCallError
       nil
     end
 
     def chmod(mode)
       if symlink?
         File.lchmod mode, path() if have_lchmod?
       else
         File.chmod mode, path()
       end
     end
 
     def chown(uid, gid)
       if symlink?
         File.lchown uid, gid, path() if have_lchown?
       else
         File.chown uid, gid, path()
       end
     end
 
     def copy(dest)
       case
       when file?
         copy_file dest
       when directory?
         if !File.exist?(dest) and /^#{Regexp.quote(path)}/ =~ File.dirname(dest)
           raise ArgumentError, "cannot copy directory %s to itself %s" % [path, dest]
         end
         begin
           Dir.mkdir dest
         rescue
           raise unless File.directory?(dest)
         end
       when symlink?
         File.symlink File.readlink(path()), dest
       when chardev?
         raise "cannot handle device file" unless File.respond_to?(:mknod)
         mknod dest, ?c, 0666, lstat().rdev
       when blockdev?
         raise "cannot handle device file" unless File.respond_to?(:mknod)
         mknod dest, ?b, 0666, lstat().rdev
       when socket?
         raise "cannot handle socket" unless File.respond_to?(:mknod)
         mknod dest, nil, lstat().mode, 0
       when pipe?
         raise "cannot handle FIFO" unless File.respond_to?(:mkfifo)
         mkfifo dest, 0666
       when door?
         raise "cannot handle door: #{path()}"
       else
         raise "unknown file type: #{path()}"
       end
     end
 
     def copy_file(dest)
       File.open(path()) do |s|
         File.open(dest, 'wb') do |f|
           IO.copy_stream(s, f)
         end
       end
     end
 
     def copy_metadata(path)
       st = lstat()
       File.utime st.atime, st.mtime, path
       begin
         File.chown st.uid, st.gid, path
       rescue Errno::EPERM
         # clear setuid/setgid
         File.chmod st.mode & 01777, path
       else
         File.chmod st.mode, path
       end
     end
 
     def remove
       if directory?
         remove_dir1
       else
         remove_file
       end
     end
 
     def remove_dir1
       platform_support {
         Dir.rmdir path().sub(%r</\z>, '')
       }
     end
 
     def remove_file
       platform_support {
         File.unlink path
       }
     end
 
     def platform_support
       return yield unless fu_windows?
       first_time_p = true
       begin
         yield
       rescue Errno::ENOENT
         raise
       rescue => err
         if first_time_p
           first_time_p = false
           begin
             File.chmod 0700, path()   # Windows does not have symlink
             retry
           rescue SystemCallError
           end
         end
         raise err
       end
     end
 
     def preorder_traverse
       stack = [self]
       while ent = stack.pop
         yield ent
         stack.concat ent.entries.reverse if ent.directory?
       end
     end
 
     alias traverse preorder_traverse
 
     def postorder_traverse
       if directory?
         entries().each do |ent|
           ent.postorder_traverse do |e|
             yield e
           end
         end
       end
       yield self
     end
 
     private
 
     $fileutils_rb_have_lchmod = nil
 
     def have_lchmod?
       # This is not MT-safe, but it does not matter.
       if $fileutils_rb_have_lchmod == nil
         $fileutils_rb_have_lchmod = check_have_lchmod?
       end
       $fileutils_rb_have_lchmod
     end
 
     def check_have_lchmod?
       return false unless File.respond_to?(:lchmod)
       File.lchmod 0
       return true
     rescue NotImplementedError
       return false
     end
 
     $fileutils_rb_have_lchown = nil
 
     def have_lchown?
       # This is not MT-safe, but it does not matter.
       if $fileutils_rb_have_lchown == nil
         $fileutils_rb_have_lchown = check_have_lchown?
       end
       $fileutils_rb_have_lchown
     end
 
     def check_have_lchown?
       return false unless File.respond_to?(:lchown)
       File.lchown nil, nil
       return true
     rescue NotImplementedError
       return false
     end
 
     def join(dir, base)
       return File.path(dir) if not base or base == '.'
       return File.path(base) if not dir or dir == '.'
       File.join(dir, base)
     end
   end   # class Entry_
 
   def fu_list(arg)   #:nodoc:
     [arg].flatten.map {|path| File.path(path) }
   end
   private_module_function :fu_list
 
   def fu_each_src_dest(src, dest)   #:nodoc:
     fu_each_src_dest0(src, dest) do |s, d|
       raise ArgumentError, "same file: #{s} and #{d}" if fu_same?(s, d)
       yield s, d, File.stat(s)
     end
   end
   private_module_function :fu_each_src_dest
 
   def fu_each_src_dest0(src, dest)   #:nodoc:
     if tmp = Array.try_convert(src)
       tmp.each do |s|
         s = File.path(s)
         yield s, File.join(dest, File.basename(s))
       end
     else
       src = File.path(src)
       if File.directory?(dest)
         yield src, File.join(dest, File.basename(src))
       else
         yield src, File.path(dest)
       end
     end
   end
   private_module_function :fu_each_src_dest0
 
   def fu_same?(a, b)   #:nodoc:
     File.identical?(a, b)
   end
   private_module_function :fu_same?
 
   def fu_check_options(options, optdecl)   #:nodoc:
     h = options.dup
     optdecl.each do |opt|
       h.delete opt
     end
     raise ArgumentError, "no such option: #{h.keys.join(' ')}" unless h.empty?
   end
   private_module_function :fu_check_options
 
   def fu_update_option(args, new)   #:nodoc:
     if tmp = Hash.try_convert(args.last)
       args[-1] = tmp.dup.update(new)
     else
       args.push new
     end
     args
   end
   private_module_function :fu_update_option
 
   @fileutils_output = $stderr
   @fileutils_label  = ''
 
   def fu_output_message(msg)   #:nodoc:
     @fileutils_output ||= $stderr
     @fileutils_label  ||= ''
     @fileutils_output.puts @fileutils_label + msg
   end
   private_module_function :fu_output_message
 
   #
   # Returns an Array of method names which have any options.
   #
   #   p FileUtils.commands  #=> ["chmod", "cp", "cp_r", "install", ...]
   #
   def FileUtils.commands
     OPT_TABLE.keys
   end
 
   #
   # Returns an Array of option names.
   #
   #   p FileUtils.options  #=> ["noop", "force", "verbose", "preserve", "mode"]
   #
   def FileUtils.options
     OPT_TABLE.values.flatten.uniq.map {|sym| sym.to_s }
   end
 
   #
   # Returns true if the method +mid+ have an option +opt+.
   #
   #   p FileUtils.have_option?(:cp, :noop)     #=> true
   #   p FileUtils.have_option?(:rm, :force)    #=> true
   #   p FileUtils.have_option?(:rm, :perserve) #=> false
   #
   def FileUtils.have_option?(mid, opt)
     li = OPT_TABLE[mid.to_s] or raise ArgumentError, "no such method: #{mid}"
     li.include?(opt)
   end
 
   #
   # Returns an Array of option names of the method +mid+.
   #
   #   p FileUtils.options(:rm)  #=> ["noop", "verbose", "force"]
   #
   def FileUtils.options_of(mid)
     OPT_TABLE[mid.to_s].map {|sym| sym.to_s }
   end
 
   #
   # Returns an Array of method names which have the option +opt+.
   #
   #   p FileUtils.collect_method(:preserve) #=> ["cp", "cp_r", "copy", "install"]
   #
   def FileUtils.collect_method(opt)
     OPT_TABLE.keys.select {|m| OPT_TABLE[m].include?(opt) }
   end
 
   METHODS = singleton_methods() - [:private_module_function,
       :commands, :options, :have_option?, :options_of, :collect_method]
 
   #
   # This module has all methods of FileUtils module, but it outputs messages
   # before acting.  This equates to passing the <tt>:verbose</tt> flag to
   # methods in FileUtils.
   #
   module Verbose
     include FileUtils
     @fileutils_output  = $stderr
     @fileutils_label   = ''
     ::FileUtils.collect_method(:verbose).each do |name|
       module_eval(<<-EOS, __FILE__, __LINE__ + 1)
         def #{name}(*args)
           super(*fu_update_option(args, :verbose => true))
         end
         private :#{name}
       EOS
     end
     extend self
     class << self
       ::FileUtils::METHODS.each do |m|
         public m
       end
     end
   end
 
   #
   # This module has all methods of FileUtils module, but never changes
   # files/directories.  This equates to passing the <tt>:noop</tt> flag
   # to methods in FileUtils.
   #
   module NoWrite
     include FileUtils
     @fileutils_output  = $stderr
     @fileutils_label   = ''
     ::FileUtils.collect_method(:noop).each do |name|
       module_eval(<<-EOS, __FILE__, __LINE__ + 1)
         def #{name}(*args)
           super(*fu_update_option(args, :noop => true))
         end
         private :#{name}
       EOS
     end
     extend self
     class << self
       ::FileUtils::METHODS.each do |m|
         public m
       end
     end
   end
 
   #
   # This module has all methods of FileUtils module, but never changes
   # files/directories, with printing message before acting.
   # This equates to passing the <tt>:noop</tt> and <tt>:verbose</tt> flag
   # to methods in FileUtils.
   #
   module DryRun
     include FileUtils
     @fileutils_output  = $stderr
     @fileutils_label   = ''
     ::FileUtils.collect_method(:noop).each do |name|
       module_eval(<<-EOS, __FILE__, __LINE__ + 1)
         def #{name}(*args)
           super(*fu_update_option(args, :noop => true, :verbose => true))
         end
         private :#{name}
       EOS
     end
     extend self
     class << self
       ::FileUtils::METHODS.each do |m|
         public m
       end
     end
   end
 
 end
diff --git a/lib/ruby/1.9/forwardable.rb b/lib/ruby/1.9/forwardable.rb
index 39b35d9cee..06f157170d 100644
--- a/lib/ruby/1.9/forwardable.rb
+++ b/lib/ruby/1.9/forwardable.rb
@@ -1,270 +1,270 @@
 #
 #   forwardable.rb -
 #   	$Release Version: 1.1$
 #   	$Revision$
 #   	by Keiju ISHITSUKA(keiju@ishitsuka.com)
 #	original definition by delegator.rb
 #       Revised by Daniel J. Berger with suggestions from Florian Gross.
 #
 #       Documentation by James Edward Gray II and Gavin Sinclair
 #
 # == Introduction
 #
 # This library allows you delegate method calls to an object, on a method by
 # method basis.
 #
 # == Notes
 #
 # Be advised, RDoc will not detect delegated methods.
 #
 # <b>forwardable.rb provides single-method delegation via the
 # def_delegator() and def_delegators() methods.  For full-class
 # delegation via DelegateClass(), see delegate.rb.</b>
 #
 # == Examples
 #
 # === Forwardable
 #
 # Forwardable makes building a new class based on existing work, with a proper
 # interface, almost trivial.  We want to rely on what has come before obviously,
 # but with delegation we can take just the methods we need and even rename them
 # as appropriate.  In many cases this is preferable to inheritance, which gives
 # us the entire old interface, even if much of it isn't needed.
 #
 #   class Queue
 #     extend Forwardable
 #
 #     def initialize
 #       @q = [ ]    # prepare delegate object
 #     end
 #
 #     # setup preferred interface, enq() and deq()...
 #     def_delegator :@q, :push, :enq
 #     def_delegator :@q, :shift, :deq
 #
 #     # support some general Array methods that fit Queues well
 #     def_delegators :@q, :clear, :first, :push, :shift, :size
 #   end
 #
 #   q = Queue.new
 #   q.enq 1, 2, 3, 4, 5
 #   q.push 6
 #
 #   q.shift    # => 1
 #   while q.size > 0
 #     puts q.deq
 #   end
 #
 #   q.enq "Ruby", "Perl", "Python"
 #   puts q.first
 #   q.clear
 #   puts q.first
 #
 # <i>Prints:</i>
 #
 #   2
 #   3
 #   4
 #   5
 #   6
 #   Ruby
 #   nil
 #
 # SingleForwardable can be used to setup delegation at the object level as well.
 #
 #    printer = String.new
 #    printer.extend SingleForwardable        # prepare object for delegation
 #    printer.def_delegator "STDOUT", "puts"  # add delegation for STDOUT.puts()
 #    printer.puts "Howdy!"
 #
 # Also, SingleForwardable can be use to Class or Module.
 #
 #    module Facade
 #      extend SingleForwardable
 #      def_delegator :Implementation, :service
 #
 #      class Implementation
 #	  def service...
 #      end
 #    end
 #
 # If you want to use both Forwardable and SingleForwardable, you can
 # use methods def_instance_delegator and def_single_delegator, etc.
 #
 # If the object isn't a Module and Class, You can too extend
 # Forwardable module.
 #    printer = String.new
 #    printer.extend Forwardable              # prepare object for delegation
 #    printer.def_delegator "STDOUT", "puts"  # add delegation for STDOUT.puts()
 #    printer.puts "Howdy!"
 #
 # <i>Prints:</i>
 #
 #    Howdy!
 
 #
 # The Forwardable module provides delegation of specified
 # methods to a designated object, using the methods #def_delegator
 # and #def_delegators.
 #
 # For example, say you have a class RecordCollection which
 # contains an array <tt>@records</tt>.  You could provide the lookup method
 # #record_number(), which simply calls #[] on the <tt>@records</tt>
 # array, like this:
 #
 #   class RecordCollection
 #     extend Forwardable
 #     def_delegator :@records, :[], :record_number
 #   end
 #
 # Further, if you wish to provide the methods #size, #<<, and #map,
 # all of which delegate to @records, this is how you can do it:
 #
 #   class RecordCollection
 #     # extend Forwardable, but we did that above
 #     def_delegators :@records, :size, :<<, :map
 #   end
 #   f = Foo.new
 #   f.printf ...
 #   f.gets
 #   f.content_at(1)
 #
 # Also see the example at forwardable.rb.
 
 module Forwardable
   FORWARDABLE_VERSION = "1.1.0"
 
   @debug = nil
-  class<<self
+  class << self
     attr_accessor :debug
   end
 
   # Takes a hash as its argument.  The key is a symbol or an array of
   # symbols.  These symbols correspond to method names.  The value is
   # the accessor to which the methods will be delegated.
   #
   # :call-seq:
   #    delegate method => accessor
   #    delegate [method, method, ...] => accessor
   #
   def instance_delegate(hash)
     hash.each{ |methods, accessor|
       methods = methods.to_s unless methods.respond_to?(:each)
       methods.each{ |method|
         def_instance_delegator(accessor, method)
       }
     }
   end
 
   #
   # Shortcut for defining multiple delegator methods, but with no
   # provision for using a different name.  The following two code
   # samples have the same effect:
   #
   #   def_delegators :@records, :size, :<<, :map
   #
   #   def_delegator :@records, :size
   #   def_delegator :@records, :<<
   #   def_delegator :@records, :map
   #
   def def_instance_delegators(accessor, *methods)
     methods.delete("__send__")
     methods.delete("__id__")
     for method in methods
       def_instance_delegator(accessor, method)
     end
   end
 
   def def_instance_delegator(accessor, method, ali = method)
     line_no = __LINE__; str = %{
       def #{ali}(*args, &block)
 	begin
 	  #{accessor}.__send__(:#{method}, *args, &block)
 	rescue Exception
 	  $@.delete_if{|s| %r"#{Regexp.quote(__FILE__)}"o =~ s} unless Forwardable::debug
 	  ::Kernel::raise
 	end
       end
     }
     # If it's not a class or module, it's an instance
     begin
       module_eval(str, __FILE__, line_no)
     rescue
       instance_eval(str, __FILE__, line_no)
     end
 
   end
 
   alias delegate instance_delegate
   alias def_delegators def_instance_delegators
   alias def_delegator def_instance_delegator
 end
 
 #
 # Usage of The SingleForwardable is like Fowadable module.
 #
 module SingleForwardable
   # Takes a hash as its argument.  The key is a symbol or an array of
   # symbols.  These symbols correspond to method names.  The value is
   # the accessor to which the methods will be delegated.
   #
   # :call-seq:
   #    delegate method => accessor
   #    delegate [method, method, ...] => accessor
   #
   def single_delegate(hash)
     hash.each{ |methods, accessor|
       methods = methods.to_s unless methods.respond_to?(:each)
       methods.each{ |method|
         def_single_delegator(accessor, method)
       }
     }
   end
 
   #
   # Shortcut for defining multiple delegator methods, but with no
   # provision for using a different name.  The following two code
   # samples have the same effect:
   #
   #   def_delegators :@records, :size, :<<, :map
   #
   #   def_delegator :@records, :size
   #   def_delegator :@records, :<<
   #   def_delegator :@records, :map
   #
   def def_single_delegators(accessor, *methods)
     methods.delete("__send__")
     methods.delete("__id__")
     for method in methods
       def_single_delegator(accessor, method)
     end
   end
 
   #
   # Defines a method _method_ which delegates to _obj_ (i.e. it calls
   # the method of the same name in _obj_).  If _new_name_ is
   # provided, it is used as the name for the delegate method.
   #
   def def_single_delegator(accessor, method, ali = method)
     line_no = __LINE__; str = %{
       def #{ali}(*args, &block)
 	begin
 	  #{accessor}.__send__(:#{method}, *args, &block)
 	rescue Exception
 	  $@.delete_if{|s| %r"#{Regexp.quote(__FILE__)}"o =~ s} unless Forwardable::debug
 	  ::Kernel::raise
 	end
       end
     }
 
     instance_eval(str, __FILE__, __LINE__)
   end
 
   alias delegate single_delegate
   alias def_delegators def_single_delegators
   alias def_delegator def_single_delegator
 end
 
 
 
 
diff --git a/lib/ruby/1.9/irb/cmd/fork.rb b/lib/ruby/1.9/irb/cmd/fork.rb
index 45350cb1b7..c2664626ae 100644
--- a/lib/ruby/1.9/irb/cmd/fork.rb
+++ b/lib/ruby/1.9/irb/cmd/fork.rb
@@ -1,38 +1,38 @@
 #
 #   fork.rb -
 #   	$Release Version: 0.9.6 $
 #   	$Revision$
 #   	by Keiju ISHITSUKA(keiju@ruby-lang.org)
 #
 # --
 #
 #
 #
 
 @RCS_ID='-$Id$-'
 
 
 module IRB
   module ExtendCommand
     class Fork<Nop
       def execute(&block)
 	pid = send ExtendCommand.irb_original_method_name("fork")
 	unless pid
-	  class<<self
+	  class << self
 	    alias_method :exit, ExtendCommand.irb_original_method_name('exit')
 	  end
 	  if iterator?
 	    begin
 	      yield
 	    ensure
 	      exit
 	    end
 	  end
 	end
 	pid
       end
     end
   end
 end
 
 
diff --git a/lib/ruby/1.9/matrix.rb b/lib/ruby/1.9/matrix.rb
index ee26cb94d0..b98fc85839 100644
--- a/lib/ruby/1.9/matrix.rb
+++ b/lib/ruby/1.9/matrix.rb
@@ -1,1539 +1,1539 @@
 # encoding: utf-8
 #
 # = matrix.rb
 #
 # An implementation of Matrix and Vector classes.
 #
 # See classes Matrix and Vector for documentation.
 #
 # Current Maintainer:: Marc-André Lafortune
 # Original Author:: Keiju ISHITSUKA
 # Original Documentation:: Gavin Sinclair (sourced from <i>Ruby in a Nutshell</i> (Matsumoto, O'Reilly))
 ##
 
 require "e2mmap.rb"
 
 module ExceptionForMatrix # :nodoc:
   extend Exception2MessageMapper
   def_e2message(TypeError, "wrong argument type %s (expected %s)")
   def_e2message(ArgumentError, "Wrong # of arguments(%d for %d)")
 
   def_exception("ErrDimensionMismatch", "\#{self.name} dimension mismatch")
   def_exception("ErrNotRegular", "Not Regular Matrix")
   def_exception("ErrOperationNotDefined", "Operation(%s) can\\'t be defined: %s op %s")
   def_exception("ErrOperationNotImplemented", "Sorry, Operation(%s) not implemented: %s op %s")
 end
 
 #
 # The +Matrix+ class represents a mathematical matrix. It provides methods for creating
 # matrices, operating on them arithmetically and algebraically,
 # and determining their mathematical properties (trace, rank, inverse, determinant).
 #
 # == Method Catalogue
 #
 # To create a matrix:
 # * <tt> Matrix[*rows]                  </tt>
 # * <tt> Matrix.[](*rows)               </tt>
 # * <tt> Matrix.rows(rows, copy = true) </tt>
 # * <tt> Matrix.columns(columns)        </tt>
 # * <tt> Matrix.build(row_size, column_size, &block) </tt>
 # * <tt> Matrix.diagonal(*values)       </tt>
 # * <tt> Matrix.scalar(n, value)        </tt>
 # * <tt> Matrix.identity(n)             </tt>
 # * <tt> Matrix.unit(n)                 </tt>
 # * <tt> Matrix.I(n)                    </tt>
 # * <tt> Matrix.zero(n)                 </tt>
 # * <tt> Matrix.row_vector(row)         </tt>
 # * <tt> Matrix.column_vector(column)   </tt>
 #
 # To access Matrix elements/columns/rows/submatrices/properties:
 # * <tt>  [](i, j)                      </tt>
 # * <tt> #row_size                      </tt>
 # * <tt> #column_size                   </tt>
 # * <tt> #row(i)                        </tt>
 # * <tt> #column(j)                     </tt>
 # * <tt> #collect                       </tt>
 # * <tt> #map                           </tt>
 # * <tt> #each                          </tt>
 # * <tt> #each_with_index               </tt>
 # * <tt> #minor(*param)                 </tt>
 #
 # Properties of a matrix:
 # * <tt> #empty?                        </tt>
 # * <tt> #real?                         </tt>
 # * <tt> #regular?                      </tt>
 # * <tt> #singular?                     </tt>
 # * <tt> #square?                       </tt>
 #
 # Matrix arithmetic:
 # * <tt>  *(m)                          </tt>
 # * <tt>  +(m)                          </tt>
 # * <tt>  -(m)                          </tt>
 # * <tt> #/(m)                          </tt>
 # * <tt> #inverse                       </tt>
 # * <tt> #inv                           </tt>
 # * <tt>  **                            </tt>
 #
 # Matrix functions:
 # * <tt> #determinant                   </tt>
 # * <tt> #det                           </tt>
 # * <tt> #rank                          </tt>
 # * <tt> #trace                         </tt>
 # * <tt> #tr                            </tt>
 # * <tt> #transpose                     </tt>
 # * <tt> #t                             </tt>
 #
 # Complex arithmetic:
 # * <tt> conj                           </tt>
 # * <tt> conjugate                      </tt>
 # * <tt> imag                           </tt>
 # * <tt> imaginary                      </tt>
 # * <tt> real                           </tt>
 # * <tt> rect                           </tt>
 # * <tt> rectangular                    </tt>
 #
 # Conversion to other data types:
 # * <tt> #coerce(other)                 </tt>
 # * <tt> #row_vectors                   </tt>
 # * <tt> #column_vectors                </tt>
 # * <tt> #to_a                          </tt>
 #
 # String representations:
 # * <tt> #to_s                          </tt>
 # * <tt> #inspect                       </tt>
 #
 class Matrix
   include Enumerable
   include ExceptionForMatrix
 
   # instance creations
   private_class_method :new
   attr_reader :rows
   protected :rows
 
   #
   # Creates a matrix where each argument is a row.
   #   Matrix[ [25, 93], [-1, 66] ]
   #      =>  25 93
   #          -1 66
   #
   def Matrix.[](*rows)
     Matrix.rows(rows, false)
   end
 
   #
   # Creates a matrix where +rows+ is an array of arrays, each of which is a row
   # of the matrix.  If the optional argument +copy+ is false, use the given
   # arrays as the internal structure of the matrix without copying.
   #   Matrix.rows([[25, 93], [-1, 66]])
   #      =>  25 93
   #          -1 66
   #
   def Matrix.rows(rows, copy = true)
     rows = convert_to_array(rows)
     rows.map! do |row|
       convert_to_array(row, copy)
     end
     size = (rows[0] || []).size
     rows.each do |row|
       Matrix.Raise ErrDimensionMismatch, "row size differs (#{row.size} should be #{size})" unless row.size == size
     end
     new rows, size
   end
 
   #
   # Creates a matrix using +columns+ as an array of column vectors.
   #   Matrix.columns([[25, 93], [-1, 66]])
   #      =>  25 -1
   #          93 66
   #
   def Matrix.columns(columns)
     Matrix.rows(columns, false).transpose
   end
 
   #
   # Creates a matrix of size +row_size+ x +column_size+.
   # It fills the values by calling the given block,
   # passing the current row and column.
   # Returns an enumerator if no block is given.
   #
   #   m = Matrix.build(2, 4) {|row, col| col - row }
   #     => Matrix[[0, 1, 2, 3], [-1, 0, 1, 2]]
   #   m = Matrix.build(3) { rand }
   #     => a 3x3 matrix with random elements
   #
   def Matrix.build(row_size, column_size = row_size)
     row_size = CoercionHelper.coerce_to_int(row_size)
     column_size = CoercionHelper.coerce_to_int(column_size)
     raise ArgumentError if row_size < 0 || column_size < 0
     return to_enum :build, row_size, column_size unless block_given?
     rows = row_size.times.map do |i|
       column_size.times.map do |j|
         yield i, j
       end
     end
     new rows, column_size
   end
 
   #
   # Creates a matrix where the diagonal elements are composed of +values+.
   #   Matrix.diagonal(9, 5, -3)
   #     =>  9  0  0
   #         0  5  0
   #         0  0 -3
   #
   def Matrix.diagonal(*values)
     size = values.size
     rows = (0 ... size).collect {|j|
       row = Array.new(size, 0)
       row[j] = values[j]
       row
     }
     new rows
   end
 
   #
   # Creates an +n+ by +n+ diagonal matrix where each diagonal element is
   # +value+.
   #   Matrix.scalar(2, 5)
   #     => 5 0
   #        0 5
   #
   def Matrix.scalar(n, value)
     Matrix.diagonal(*Array.new(n, value))
   end
 
   #
   # Creates an +n+ by +n+ identity matrix.
   #   Matrix.identity(2)
   #     => 1 0
   #        0 1
   #
   def Matrix.identity(n)
     Matrix.scalar(n, 1)
   end
   class << Matrix
     alias unit identity
     alias I identity
   end
 
   #
   # Creates an +n+ by +n+ zero matrix.
   #   Matrix.zero(2)
   #     => 0 0
   #        0 0
   #
   def Matrix.zero(n)
     Matrix.scalar(n, 0)
   end
 
   #
   # Creates a single-row matrix where the values of that row are as given in
   # +row+.
   #   Matrix.row_vector([4,5,6])
   #     => 4 5 6
   #
   def Matrix.row_vector(row)
     row = convert_to_array(row)
     new [row]
   end
 
   #
   # Creates a single-column matrix where the values of that column are as given
   # in +column+.
   #   Matrix.column_vector([4,5,6])
   #     => 4
   #        5
   #        6
   #
   def Matrix.column_vector(column)
     column = convert_to_array(column)
     new [column].transpose, 1
   end
 
   #
   # Creates a empty matrix of +row_size+ x +column_size+.
   # At least one of +row_size+ or +column_size+ must be 0.
   #
   #   m = Matrix.empty(2, 0)
   #   m == Matrix[ [], [] ]
   #     => true
   #   n = Matrix.empty(0, 3)
   #   n == Matrix.columns([ [], [], [] ])
   #     => true
   #   m * n
   #     => Matrix[[0, 0, 0], [0, 0, 0]]
   #
   def Matrix.empty(row_size = 0, column_size = 0)
     Matrix.Raise ArgumentError, "One size must be 0" if column_size != 0 && row_size != 0
     Matrix.Raise ArgumentError, "Negative size" if column_size < 0 || row_size < 0
 
     new([[]]*row_size, column_size)
   end
 
   #
   # Matrix.new is private; use Matrix.rows, columns, [], etc... to create.
   #
   def initialize(rows, column_size = rows[0].size)
     # No checking is done at this point. rows must be an Array of Arrays.
     # column_size must be the size of the first row, if there is one,
     # otherwise it *must* be specified and can be any integer >= 0
     @rows = rows
     @column_size = column_size
   end
 
   def new_matrix(rows, column_size = rows[0].size) # :nodoc:
     Matrix.send(:new, rows, column_size) # bypass privacy of Matrix.new
   end
   private :new_matrix
 
   #
   # Returns element (+i+,+j+) of the matrix.  That is: row +i+, column +j+.
   #
   def [](i, j)
     @rows.fetch(i){return nil}[j]
   end
   alias element []
   alias component []
 
   def []=(i, j, v)
     @rows[i][j] = v
   end
   alias set_element []=
   alias set_component []=
   private :[]=, :set_element, :set_component
 
   #
   # Returns the number of rows.
   #
   def row_size
     @rows.size
   end
 
   #
   # Returns the number of columns.
   #
   attr_reader :column_size
 
   #
   # Returns row vector number +i+ of the matrix as a Vector (starting at 0 like
   # an array).  When a block is given, the elements of that vector are iterated.
   #
   def row(i, &block) # :yield: e
     if block_given?
       @rows.fetch(i){return self}.each(&block)
       self
     else
       Vector.elements(@rows.fetch(i){return nil})
     end
   end
 
   #
   # Returns column vector number +j+ of the matrix as a Vector (starting at 0
   # like an array).  When a block is given, the elements of that vector are
   # iterated.
   #
   def column(j) # :yield: e
     if block_given?
       return self if j >= column_size || j < -column_size
       row_size.times do |i|
         yield @rows[i][j]
       end
       self
     else
       return nil if j >= column_size || j < -column_size
       col = (0 ... row_size).collect {|i|
         @rows[i][j]
       }
       Vector.elements(col, false)
     end
   end
 
   #
   # Returns a matrix that is the result of iteration of the given block over all
   # elements of the matrix.
   #   Matrix[ [1,2], [3,4] ].collect { |e| e**2 }
   #     => 1  4
   #        9 16
   #
   def collect(&block) # :yield: e
     return to_enum(:collect) unless block_given?
     rows = @rows.collect{|row| row.collect(&block)}
     new_matrix rows, column_size
   end
   alias map collect
 
   #
   # Yields all elements of the matrix, starting with those of the first row,
   # or returns an Enumerator is no block given
   #   Matrix[ [1,2], [3,4] ].each { |e| puts e }
   #     # => prints the numbers 1 to 4
   #
   def each(&block) # :yield: e
     return to_enum(:each) unless block_given?
     @rows.each do |row|
       row.each(&block)
     end
     self
   end
 
   #
   # Yields all elements of the matrix, starting with those of the first row,
   # along with the row index and column index,
   # or returns an Enumerator is no block given
   #   Matrix[ [1,2], [3,4] ].each_with_index do |e, row, col|
   #     puts "#{e} at #{row}, #{col}"
   #   end
   #     # => 1 at 0, 0
   #     # => 2 at 0, 1
   #     # => 3 at 1, 0
   #     # => 4 at 1, 1
   #
   def each_with_index(&block) # :yield: e, row, column
     return to_enum(:each_with_index) unless block_given?
     @rows.each_with_index do |row, row_index|
       row.each_with_index do |e, col_index|
         yield e, row_index, col_index
       end
     end
     self
   end
 
   #
   # Returns a section of the matrix.  The parameters are either:
   # *  start_row, nrows, start_col, ncols; OR
   # *  row_range, col_range
   #
   #   Matrix.diagonal(9, 5, -3).minor(0..1, 0..2)
   #     => 9 0 0
   #        0 5 0
   #
   # Like Array#[], negative indices count backward from the end of the
   # row or column (-1 is the last element). Returns nil if the starting
   # row or column is greater than row_size or column_size respectively.
   #
   def minor(*param)
     case param.size
     when 2
       row_range, col_range = param
       from_row = row_range.first
       from_row += row_size if from_row < 0
       to_row = row_range.end
       to_row += row_size if to_row < 0
       to_row += 1 unless row_range.exclude_end?
       size_row = to_row - from_row
 
       from_col = col_range.first
       from_col += column_size if from_col < 0
       to_col = col_range.end
       to_col += column_size if to_col < 0
       to_col += 1 unless col_range.exclude_end?
       size_col = to_col - from_col
     when 4
       from_row, size_row, from_col, size_col = param
       return nil if size_row < 0 || size_col < 0
       from_row += row_size if from_row < 0
       from_col += column_size if from_col < 0
     else
       Matrix.Raise ArgumentError, param.inspect
     end
 
     return nil if from_row > row_size || from_col > column_size || from_row < 0 || from_col < 0
     rows = @rows[from_row, size_row].collect{|row|
       row[from_col, size_col]
     }
     new_matrix rows, column_size - from_col
   end
 
   #--
   # TESTING -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Returns +true+ if this is an empty matrix, i.e. if the number of rows
   # or the number of columns is 0.
   #
   def empty?
     column_size == 0 || row_size == 0
   end
 
   #
   # Returns +true+ if all entries of the matrix are real.
   #
   def real?
     all?(&:real?)
   end
 
   #
   # Returns +true+ if this is a regular (i.e. non-singular) matrix.
   #
   def regular?
     not singular?
   end
 
   #
   # Returns +true+ is this is a singular matrix.
   #
   def singular?
     determinant == 0
   end
 
   #
   # Returns +true+ is this is a square matrix.
   #
   def square?
     column_size == row_size
   end
 
   #--
   # OBJECT METHODS -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Returns +true+ if and only if the two matrices contain equal elements.
   #
   def ==(other)
     return false unless Matrix === other
     rows == other.rows
   end
 
   def eql?(other)
     return false unless Matrix === other
     rows.eql? other.rows
   end
 
   #
   # Returns a clone of the matrix, so that the contents of each do not reference
   # identical objects.
   # There should be no good reason to do this since Matrices are immutable.
   #
   def clone
     new_matrix @rows.map(&:dup), column_size
   end
 
   #
   # Returns a hash-code for the matrix.
   #
   def hash
     @rows.hash
   end
 
   #--
   # ARITHMETIC -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Matrix multiplication.
   #   Matrix[[2,4], [6,8]] * Matrix.identity(2)
   #     => 2 4
   #        6 8
   #
   def *(m) # m is matrix or vector or number
     case(m)
     when Numeric
       rows = @rows.collect {|row|
         row.collect {|e|
           e * m
         }
       }
       return new_matrix rows, column_size
     when Vector
       m = Matrix.column_vector(m)
       r = self * m
       return r.column(0)
     when Matrix
       Matrix.Raise ErrDimensionMismatch if column_size != m.row_size
 
       rows = (0 ... row_size).collect {|i|
         (0 ... m.column_size).collect {|j|
           (0 ... column_size).inject(0) do |vij, k|
             vij + self[i, k] * m[k, j]
           end
         }
       }
       return new_matrix rows, m.column_size
     else
       return apply_through_coercion(m, __method__)
     end
   end
 
   #
   # Matrix addition.
   #   Matrix.scalar(2,5) + Matrix[[1,0], [-4,7]]
   #     =>  6  0
   #        -4 12
   #
   def +(m)
     case m
     when Numeric
       Matrix.Raise ErrOperationNotDefined, "+", self.class, m.class
     when Vector
       m = Matrix.column_vector(m)
     when Matrix
     else
       return apply_through_coercion(m, __method__)
     end
 
     Matrix.Raise ErrDimensionMismatch unless row_size == m.row_size and column_size == m.column_size
 
     rows = (0 ... row_size).collect {|i|
       (0 ... column_size).collect {|j|
         self[i, j] + m[i, j]
       }
     }
     new_matrix rows, column_size
   end
 
   #
   # Matrix subtraction.
   #   Matrix[[1,5], [4,2]] - Matrix[[9,3], [-4,1]]
   #     => -8  2
   #         8  1
   #
   def -(m)
     case m
     when Numeric
       Matrix.Raise ErrOperationNotDefined, "-", self.class, m.class
     when Vector
       m = Matrix.column_vector(m)
     when Matrix
     else
       return apply_through_coercion(m, __method__)
     end
 
     Matrix.Raise ErrDimensionMismatch unless row_size == m.row_size and column_size == m.column_size
 
     rows = (0 ... row_size).collect {|i|
       (0 ... column_size).collect {|j|
         self[i, j] - m[i, j]
       }
     }
     new_matrix rows, column_size
   end
 
   #
   # Matrix division (multiplication by the inverse).
   #   Matrix[[7,6], [3,9]] / Matrix[[2,9], [3,1]]
   #     => -7  1
   #        -3 -6
   #
   def /(other)
     case other
     when Numeric
       rows = @rows.collect {|row|
         row.collect {|e|
           e / other
         }
       }
       return new_matrix rows, column_size
     when Matrix
       return self * other.inverse
     else
       return apply_through_coercion(other, __method__)
     end
   end
 
   #
   # Returns the inverse of the matrix.
   #   Matrix[[-1, -1], [0, -1]].inverse
   #     => -1  1
   #         0 -1
   #
   def inverse
     Matrix.Raise ErrDimensionMismatch unless square?
     Matrix.I(row_size).send(:inverse_from, self)
   end
   alias inv inverse
 
   def inverse_from(src) # :nodoc:
     last = row_size - 1
     a = src.to_a
 
     0.upto(last) do |k|
       i = k
       akk = a[k][k].abs
       (k+1).upto(last) do |j|
         v = a[j][k].abs
         if v > akk
           i = j
           akk = v
         end
       end
       Matrix.Raise ErrNotRegular if akk == 0
       if i != k
         a[i], a[k] = a[k], a[i]
         @rows[i], @rows[k] = @rows[k], @rows[i]
       end
       akk = a[k][k]
 
       0.upto(last) do |ii|
         next if ii == k
         q = a[ii][k].quo(akk)
         a[ii][k] = 0
 
         (k + 1).upto(last) do |j|
           a[ii][j] -= a[k][j] * q
         end
         0.upto(last) do |j|
           @rows[ii][j] -= @rows[k][j] * q
         end
       end
 
       (k+1).upto(last) do |j|
         a[k][j] = a[k][j].quo(akk)
       end
       0.upto(last) do |j|
         @rows[k][j] = @rows[k][j].quo(akk)
       end
     end
     self
   end
   private :inverse_from
 
   #
   # Matrix exponentiation.  Currently implemented for integer powers only.
   # Equivalent to multiplying the matrix by itself N times.
   #   Matrix[[7,6], [3,9]] ** 2
   #     => 67 96
   #        48 99
   #
   def ** (other)
     case other
     when Integer
       x = self
       if other <= 0
         x = self.inverse
         return Matrix.identity(self.column_size) if other == 0
         other = -other
       end
       z = nil
       loop do
         z = z ? z * x : x if other[0] == 1
         return z if (other >>= 1).zero?
         x *= x
       end
     when Float, Rational
       Matrix.Raise ErrOperationNotImplemented, "**", self.class, other.class
     else
       Matrix.Raise ErrOperationNotDefined, "**", self.class, other.class
     end
   end
 
   #--
   # MATRIX FUNCTIONS -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Returns the determinant of the matrix.
   #
   # Beware that using Float values can yield erroneous results
   # because of their lack of precision.
   # Consider using exact types like Rational or BigDecimal instead.
   #
   #   Matrix[[7,6], [3,9]].determinant
   #     => 45
   #
   def determinant
     Matrix.Raise ErrDimensionMismatch unless square?
     m = @rows
     case row_size
       # Up to 4x4, give result using Laplacian expansion by minors.
       # This will typically be faster, as well as giving good results
       # in case of Floats
     when 0
       +1
     when 1
       + m[0][0]
     when 2
       + m[0][0] * m[1][1] - m[0][1] * m[1][0]
     when 3
-      m0 = m[0]; m1 = m[1]; m2 = m[2]
+      m0, m1, m2 = m
       + m0[0] * m1[1] * m2[2] - m0[0] * m1[2] * m2[1] \
       - m0[1] * m1[0] * m2[2] + m0[1] * m1[2] * m2[0] \
       + m0[2] * m1[0] * m2[1] - m0[2] * m1[1] * m2[0]
     when 4
-      m0 = m[0]; m1 = m[1]; m2 = m[2]; m3 = m[3]
+      m0, m1, m2, m3 = m
       + m0[0] * m1[1] * m2[2] * m3[3] - m0[0] * m1[1] * m2[3] * m3[2] \
       - m0[0] * m1[2] * m2[1] * m3[3] + m0[0] * m1[2] * m2[3] * m3[1] \
       + m0[0] * m1[3] * m2[1] * m3[2] - m0[0] * m1[3] * m2[2] * m3[1] \
       - m0[1] * m1[0] * m2[2] * m3[3] + m0[1] * m1[0] * m2[3] * m3[2] \
       + m0[1] * m1[2] * m2[0] * m3[3] - m0[1] * m1[2] * m2[3] * m3[0] \
       - m0[1] * m1[3] * m2[0] * m3[2] + m0[1] * m1[3] * m2[2] * m3[0] \
       + m0[2] * m1[0] * m2[1] * m3[3] - m0[2] * m1[0] * m2[3] * m3[1] \
       - m0[2] * m1[1] * m2[0] * m3[3] + m0[2] * m1[1] * m2[3] * m3[0] \
       + m0[2] * m1[3] * m2[0] * m3[1] - m0[2] * m1[3] * m2[1] * m3[0] \
       - m0[3] * m1[0] * m2[1] * m3[2] + m0[3] * m1[0] * m2[2] * m3[1] \
       + m0[3] * m1[1] * m2[0] * m3[2] - m0[3] * m1[1] * m2[2] * m3[0] \
       - m0[3] * m1[2] * m2[0] * m3[1] + m0[3] * m1[2] * m2[1] * m3[0]
     else
       # For bigger matrices, use an efficient and general algorithm.
       # Currently, we use the Gauss-Bareiss algorithm
       determinant_bareiss
     end
   end
   alias_method :det, :determinant
 
   #
   # Private. Use Matrix#determinant
   #
   # Returns the determinant of the matrix, using
   # Bareiss' multistep integer-preserving gaussian elimination.
   # It has the same computational cost order O(n^3) as standard Gaussian elimination.
   # Intermediate results are fraction free and of lower complexity.
   # A matrix of Integers will have thus intermediate results that are also Integers,
   # with smaller bignums (if any), while a matrix of Float will usually have
   # intermediate results with better precision.
   #
   def determinant_bareiss
     size = row_size
     last = size - 1
     a = to_a
     no_pivot = Proc.new{ return 0 }
     sign = +1
     pivot = 1
     size.times do |k|
       previous_pivot = pivot
       if (pivot = a[k][k]) == 0
         switch = (k+1 ... size).find(no_pivot) {|row|
           a[row][k] != 0
         }
         a[switch], a[k] = a[k], a[switch]
         pivot = a[k][k]
         sign = -sign
       end
       (k+1).upto(last) do |i|
         ai = a[i]
         (k+1).upto(last) do |j|
           ai[j] =  (pivot * ai[j] - ai[k] * a[k][j]) / previous_pivot
         end
       end
     end
     sign * pivot
   end
   private :determinant_bareiss
 
   #
   # deprecated; use Matrix#determinant
   #
   def determinant_e
     warn "#{caller(1)[0]}: warning: Matrix#determinant_e is deprecated; use #determinant"
     rank
   end
   alias det_e determinant_e
 
   #
   # Returns the rank of the matrix.
   # Beware that using Float values can yield erroneous results
   # because of their lack of precision.
   # Consider using exact types like Rational or BigDecimal instead.
   #
   #   Matrix[[7,6], [3,9]].rank
   #     => 2
   #
   def rank
     # We currently use Bareiss' multistep integer-preserving gaussian elimination
     # (see comments on determinant)
     a = to_a
     last_column = column_size - 1
     last_row = row_size - 1
     rank = 0
     pivot_row = 0
     previous_pivot = 1
     0.upto(last_column) do |k|
       switch_row = (pivot_row .. last_row).find {|row|
         a[row][k] != 0
       }
       if switch_row
         a[switch_row], a[pivot_row] = a[pivot_row], a[switch_row] unless pivot_row == switch_row
         pivot = a[pivot_row][k]
         (pivot_row+1).upto(last_row) do |i|
            ai = a[i]
            (k+1).upto(last_column) do |j|
              ai[j] =  (pivot * ai[j] - ai[k] * a[pivot_row][j]) / previous_pivot
            end
          end
         pivot_row += 1
         previous_pivot = pivot
       end
     end
     pivot_row
   end
 
   #
   # deprecated; use Matrix#rank
   #
   def rank_e
     warn "#{caller(1)[0]}: warning: Matrix#rank_e is deprecated; use #rank"
     rank
   end
 
 
   #
   # Returns the trace (sum of diagonal elements) of the matrix.
   #   Matrix[[7,6], [3,9]].trace
   #     => 16
   #
   def trace
     Matrix.Raise ErrDimensionMismatch unless square?
     (0...column_size).inject(0) do |tr, i|
       tr + @rows[i][i]
     end
   end
   alias tr trace
 
   #
   # Returns the transpose of the matrix.
   #   Matrix[[1,2], [3,4], [5,6]]
   #     => 1 2
   #        3 4
   #        5 6
   #   Matrix[[1,2], [3,4], [5,6]].transpose
   #     => 1 3 5
   #        2 4 6
   #
   def transpose
     return Matrix.empty(column_size, 0) if row_size.zero?
     new_matrix @rows.transpose, row_size
   end
   alias t transpose
 
   #--
   # COMPLEX ARITHMETIC -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   #++
 
   #
   # Returns the conjugate of the matrix.
   #   Matrix[[Complex(1,2), Complex(0,1), 0], [1, 2, 3]]
   #     => 1+2i   i  0
   #           1   2  3
   #   Matrix[[Complex(1,2), Complex(0,1), 0], [1, 2, 3]].conjugate
   #     => 1-2i  -i  0
   #           1   2  3
   #
   def conjugate
     collect(&:conjugate)
   end
   alias conj conjugate
 
   #
   # Returns the imaginary part of the matrix.
   #   Matrix[[Complex(1,2), Complex(0,1), 0], [1, 2, 3]]
   #     => 1+2i  i  0
   #           1  2  3
   #   Matrix[[Complex(1,2), Complex(0,1), 0], [1, 2, 3]].imaginary
   #     =>   2i  i  0
   #           0  0  0
   #
   def imaginary
     collect(&:imaginary)
   end
   alias imag imaginary
 
   #
   # Returns the real part of the matrix.
   #   Matrix[[Complex(1,2), Complex(0,1), 0], [1, 2, 3]]
   #     => 1+2i  i  0
   #           1  2  3
   #   Matrix[[Complex(1,2), Complex(0,1), 0], [1, 2, 3]].real
   #     =>    1  0  0
   #           1  2  3
   #
   def real
     collect(&:real)
   end
 
   #
   # Returns an array containing matrices corresponding to the real and imaginary
   # parts of the matrix
   #
   # m.rect == [m.real, m.imag]  # ==> true for all matrices m
   #
   def rect
     [real, imag]
   end
   alias rectangular rect
 
   #--
   # CONVERTING -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # The coerce method provides support for Ruby type coercion.
   # This coercion mechanism is used by Ruby to handle mixed-type
   # numeric operations: it is intended to find a compatible common
   # type between the two operands of the operator.
   # See also Numeric#coerce.
   #
   def coerce(other)
     case other
     when Numeric
       return Scalar.new(other), self
     else
       raise TypeError, "#{self.class} can't be coerced into #{other.class}"
     end
   end
 
   #
   # Returns an array of the row vectors of the matrix.  See Vector.
   #
   def row_vectors
     (0 ... row_size).collect {|i|
       row(i)
     }
   end
 
   #
   # Returns an array of the column vectors of the matrix.  See Vector.
   #
   def column_vectors
     (0 ... column_size).collect {|i|
       column(i)
     }
   end
 
   #
   # Returns an array of arrays that describe the rows of the matrix.
   #
   def to_a
     @rows.collect{|row| row.dup}
   end
 
   def elements_to_f
     warn "#{caller(1)[0]}: warning: Matrix#elements_to_f is deprecated, use map(&:to_f)"
     map(&:to_f)
   end
 
   def elements_to_i
     warn "#{caller(1)[0]}: warning: Matrix#elements_to_i is deprecated, use map(&:to_i)"
     map(&:to_i)
   end
 
   def elements_to_r
     warn "#{caller(1)[0]}: warning: Matrix#elements_to_r is deprecated, use map(&:to_r)"
     map(&:to_r)
   end
 
   #--
   # PRINTING -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Overrides Object#to_s
   #
   def to_s
     if empty?
       "Matrix.empty(#{row_size}, #{column_size})"
     else
       "Matrix[" + @rows.collect{|row|
         "[" + row.collect{|e| e.to_s}.join(", ") + "]"
       }.join(", ")+"]"
     end
   end
 
   #
   # Overrides Object#inspect
   #
   def inspect
     if empty?
       "Matrix.empty(#{row_size}, #{column_size})"
     else
       "Matrix#{@rows.inspect}"
     end
   end
 
   # Private helper modules
 
   module ConversionHelper # :nodoc:
     #
     # Converts the obj to an Array. If copy is set to true
     # a copy of obj will be made if necessary.
     #
     def convert_to_array(obj, copy = false) # :nodoc:
       case obj
       when Array
         copy ? obj.dup : obj
       when Vector
         obj.to_a
       else
         begin
           converted = obj.to_ary
         rescue Exception => e
           raise TypeError, "can't convert #{obj.class} into an Array (#{e.message})"
         end
         raise TypeError, "#{obj.class}#to_ary should return an Array" unless converted.is_a? Array
         converted
       end
     end
     private :convert_to_array
   end
 
   extend ConversionHelper
 
   module CoercionHelper # :nodoc:
     #
     # Applies the operator +oper+ with argument +obj+
     # through coercion of +obj+
     #
     def apply_through_coercion(obj, oper)
       coercion = obj.coerce(self)
       raise TypeError unless coercion.is_a?(Array) && coercion.length == 2
       coercion[0].public_send(oper, coercion[1])
     rescue
       raise TypeError, "#{obj.inspect} can't be coerced into #{self.class}"
     end
     private :apply_through_coercion
 
     #
     # Helper method to coerce a value into a specific class.
     # Raises a TypeError if the coercion fails or the returned value
     # is not of the right class.
     # (from Rubinius)
     #
     def self.coerce_to(obj, cls, meth) # :nodoc:
       return obj if obj.kind_of?(cls)
 
       begin
         ret = obj.__send__(meth)
       rescue Exception => e
         raise TypeError, "Coercion error: #{obj.inspect}.#{meth} => #{cls} failed:\n" \
                          "(#{e.message})"
       end
       raise TypeError, "Coercion error: obj.#{meth} did NOT return a #{cls} (was #{ret.class})" unless ret.kind_of? cls
       ret
     end
 
     def self.coerce_to_int(obj)
       coerce_to(obj, Integer, :to_int)
     end
   end
 
   include CoercionHelper
 
   # Private CLASS
 
   class Scalar < Numeric # :nodoc:
     include ExceptionForMatrix
     include CoercionHelper
 
     def initialize(value)
       @value = value
     end
 
     # ARITHMETIC
     def +(other)
       case other
       when Numeric
         Scalar.new(@value + other)
       when Vector, Matrix
         Scalar.Raise ErrOperationNotDefined, "+", @value.class, other.class
       else
         apply_through_coercion(other, __method__)
       end
     end
 
     def -(other)
       case other
       when Numeric
         Scalar.new(@value - other)
       when Vector, Matrix
         Scalar.Raise ErrOperationNotDefined, "-", @value.class, other.class
       else
         apply_through_coercion(other, __method__)
       end
     end
 
     def *(other)
       case other
       when Numeric
         Scalar.new(@value * other)
       when Vector, Matrix
         other.collect{|e| @value * e}
       else
         apply_through_coercion(other, __method__)
       end
     end
 
     def / (other)
       case other
       when Numeric
         Scalar.new(@value / other)
       when Vector
         Scalar.Raise ErrOperationNotDefined, "/", @value.class, other.class
       when Matrix
         self * other.inverse
       else
         apply_through_coercion(other, __method__)
       end
     end
 
     def ** (other)
       case other
       when Numeric
         Scalar.new(@value ** other)
       when Vector
         Scalar.Raise ErrOperationNotDefined, "**", @value.class, other.class
       when Matrix
         #other.powered_by(self)
         Scalar.Raise ErrOperationNotImplemented, "**", @value.class, other.class
       else
         apply_through_coercion(other, __method__)
       end
     end
   end
 
 end
 
 
 #
 # The +Vector+ class represents a mathematical vector, which is useful in its own right, and
 # also constitutes a row or column of a Matrix.
 #
 # == Method Catalogue
 #
 # To create a Vector:
 # * <tt>  Vector.[](*array)                   </tt>
 # * <tt>  Vector.elements(array, copy = true) </tt>
 #
 # To access elements:
 # * <tt>  [](i)                               </tt>
 #
 # To enumerate the elements:
 # * <tt> #each2(v)                            </tt>
 # * <tt> #collect2(v)                         </tt>
 #
 # Vector arithmetic:
 # * <tt>  *(x) "is matrix or number"          </tt>
 # * <tt>  +(v)                                </tt>
 # * <tt>  -(v)                                </tt>
 #
 # Vector functions:
 # * <tt> #inner_product(v)                    </tt>
 # * <tt> #collect                             </tt>
 # * <tt> #map                                 </tt>
 # * <tt> #map2(v)                             </tt>
 # * <tt> #r                                   </tt>
 # * <tt> #size                                </tt>
 #
 # Conversion to other data types:
 # * <tt> #covector                            </tt>
 # * <tt> #to_a                                </tt>
 # * <tt> #coerce(other)                       </tt>
 #
 # String representations:
 # * <tt> #to_s                                </tt>
 # * <tt> #inspect                             </tt>
 #
 class Vector
   include ExceptionForMatrix
   include Enumerable
   include Matrix::CoercionHelper
   extend Matrix::ConversionHelper
   #INSTANCE CREATION
 
   private_class_method :new
   attr_reader :elements
   protected :elements
 
   #
   # Creates a Vector from a list of elements.
   #   Vector[7, 4, ...]
   #
   def Vector.[](*array)
     new convert_to_array(array, copy = false)
   end
 
   #
   # Creates a vector from an Array.  The optional second argument specifies
   # whether the array itself or a copy is used internally.
   #
   def Vector.elements(array, copy = true)
     new convert_to_array(array, copy)
   end
 
   #
   # Vector.new is private; use Vector[] or Vector.elements to create.
   #
   def initialize(array)
     # No checking is done at this point.
     @elements = array
   end
 
   # ACCESSING
 
   #
   # Returns element number +i+ (starting at zero) of the vector.
   #
   def [](i)
     @elements[i]
   end
   alias element []
   alias component []
 
   def []=(i, v)
     @elements[i]= v
   end
   alias set_element []=
   alias set_component []=
   private :[]=, :set_element, :set_component
 
   #
   # Returns the number of elements in the vector.
   #
   def size
     @elements.size
   end
 
   #--
   # ENUMERATIONS -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Iterate over the elements of this vector
   #
   def each(&block)
     return to_enum(:each) unless block_given?
     @elements.each(&block)
     self
   end
 
   #
   # Iterate over the elements of this vector and +v+ in conjunction.
   #
   def each2(v) # :yield: e1, e2
     raise TypeError, "Integer is not like Vector" if v.kind_of?(Integer)
     Vector.Raise ErrDimensionMismatch if size != v.size
     return to_enum(:each2, v) unless block_given?
     size.times do |i|
       yield @elements[i], v[i]
     end
     self
   end
 
   #
   # Collects (as in Enumerable#collect) over the elements of this vector and +v+
   # in conjunction.
   #
   def collect2(v) # :yield: e1, e2
     raise TypeError, "Integer is not like Vector" if v.kind_of?(Integer)
     Vector.Raise ErrDimensionMismatch if size != v.size
     return to_enum(:collect2, v) unless block_given?
     size.times.collect do |i|
       yield @elements[i], v[i]
     end
   end
 
   #--
   # COMPARING -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Returns +true+ iff the two vectors have the same elements in the same order.
   #
   def ==(other)
     return false unless Vector === other
     @elements == other.elements
   end
 
   def eql?(other)
     return false unless Vector === other
     @elements.eql? other.elements
   end
 
   #
   # Return a copy of the vector.
   #
   def clone
     Vector.elements(@elements)
   end
 
   #
   # Return a hash-code for the vector.
   #
   def hash
     @elements.hash
   end
 
   #--
   # ARITHMETIC -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Multiplies the vector by +x+, where +x+ is a number or another vector.
   #
   def *(x)
     case x
     when Numeric
       els = @elements.collect{|e| e * x}
       Vector.elements(els, false)
     when Matrix
       Matrix.column_vector(self) * x
     when Vector
       Vector.Raise ErrOperationNotDefined, "*", self.class, x.class
     else
       apply_through_coercion(x, __method__)
     end
   end
 
   #
   # Vector addition.
   #
   def +(v)
     case v
     when Vector
       Vector.Raise ErrDimensionMismatch if size != v.size
       els = collect2(v) {|v1, v2|
         v1 + v2
       }
       Vector.elements(els, false)
     when Matrix
       Matrix.column_vector(self) + v
     else
       apply_through_coercion(v, __method__)
     end
   end
 
   #
   # Vector subtraction.
   #
   def -(v)
     case v
     when Vector
       Vector.Raise ErrDimensionMismatch if size != v.size
       els = collect2(v) {|v1, v2|
         v1 - v2
       }
       Vector.elements(els, false)
     when Matrix
       Matrix.column_vector(self) - v
     else
       apply_through_coercion(v, __method__)
     end
   end
 
   #
   # Vector division.
   #
   def /(x)
     case x
     when Numeric
       els = @elements.collect{|e| e / x}
       Vector.elements(els, false)
     when Matrix, Vector
       Vector.Raise ErrOperationNotDefined, "/", self.class, x.class
     else
       apply_through_coercion(x, __method__)
     end
   end
 
   #--
   # VECTOR FUNCTIONS -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Returns the inner product of this vector with the other.
   #   Vector[4,7].inner_product Vector[10,1]  => 47
   #
   def inner_product(v)
     Vector.Raise ErrDimensionMismatch if size != v.size
 
     p = 0
     each2(v) {|v1, v2|
       p += v1 * v2
     }
     p
   end
 
   #
   # Like Array#collect.
   #
   def collect(&block) # :yield: e
     return to_enum(:collect) unless block_given?
     els = @elements.collect(&block)
     Vector.elements(els, false)
   end
   alias map collect
 
   #
   # Like Vector#collect2, but returns a Vector instead of an Array.
   #
   def map2(v, &block) # :yield: e1, e2
     return to_enum(:map2, v) unless block_given?
     els = collect2(v, &block)
     Vector.elements(els, false)
   end
 
   #
   # Returns the modulus (Pythagorean distance) of the vector.
   #   Vector[5,8,2].r => 9.643650761
   #
   def r
     Math.sqrt(@elements.inject(0) {|v, e| v + e*e})
   end
 
   #--
   # CONVERTING
   #++
 
   #
   # Creates a single-row matrix from this vector.
   #
   def covector
     Matrix.row_vector(self)
   end
 
   #
   # Returns the elements of the vector in an array.
   #
   def to_a
     @elements.dup
   end
 
   def elements_to_f
     warn "#{caller(1)[0]}: warning: Vector#elements_to_f is deprecated"
     map(&:to_f)
   end
 
   def elements_to_i
     warn "#{caller(1)[0]}: warning: Vector#elements_to_i is deprecated"
     map(&:to_i)
   end
 
   def elements_to_r
     warn "#{caller(1)[0]}: warning: Vector#elements_to_r is deprecated"
     map(&:to_r)
   end
 
   #
   # The coerce method provides support for Ruby type coercion.
   # This coercion mechanism is used by Ruby to handle mixed-type
   # numeric operations: it is intended to find a compatible common
   # type between the two operands of the operator.
   # See also Numeric#coerce.
   #
   def coerce(other)
     case other
     when Numeric
       return Matrix::Scalar.new(other), self
     else
       raise TypeError, "#{self.class} can't be coerced into #{other.class}"
     end
   end
 
   #--
   # PRINTING -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   #++
 
   #
   # Overrides Object#to_s
   #
   def to_s
     "Vector[" + @elements.join(", ") + "]"
   end
 
   #
   # Overrides Object#inspect
   #
   def inspect
     str = "Vector"+@elements.inspect
   end
 end
diff --git a/lib/ruby/1.9/mutex_m.rb b/lib/ruby/1.9/mutex_m.rb
index 21d828523e..081bb788e4 100644
--- a/lib/ruby/1.9/mutex_m.rb
+++ b/lib/ruby/1.9/mutex_m.rb
@@ -1,91 +1,91 @@
 #
 #   mutex_m.rb -
 #   	$Release Version: 3.0$
 #   	$Revision: 1.7 $
 #       Original from mutex.rb
 #   	by Keiju ISHITSUKA(keiju@ishitsuka.com)
 #       modified by matz
 #       patched by akira yamada
 #
 # --
 #   Usage:
 #	require "mutex_m.rb"
 #	obj = Object.new
 #	obj.extend Mutex_m
 #	...
 #	extended object can be handled like Mutex
 #       or
 #	class Foo
 #	  include Mutex_m
 #	  ...
 #	end
 #	obj = Foo.new
 #	this obj can be handled like Mutex
 #
 
 require 'thread'
 
 module Mutex_m
   def Mutex_m.define_aliases(cl)
     cl.module_eval %q{
       alias locked? mu_locked?
       alias lock mu_lock
       alias unlock mu_unlock
       alias try_lock mu_try_lock
       alias synchronize mu_synchronize
     }
   end
 
   def Mutex_m.append_features(cl)
     super
     define_aliases(cl) unless cl.instance_of?(Module)
   end
 
   def Mutex_m.extend_object(obj)
     super
     obj.mu_extended
   end
 
   def mu_extended
     unless (defined? locked? and
 	    defined? lock and
 	    defined? unlock and
 	    defined? try_lock and
 	    defined? synchronize)
-      Mutex_m.define_aliases(class<<self;self;end)
+      Mutex_m.define_aliases(singleton_class)
     end
     mu_initialize
   end
 
   # locking
   def mu_synchronize(&block)
     @_mutex.synchronize(&block)
   end
 
   def mu_locked?
     @_mutex.locked?
   end
 
   def mu_try_lock
     @_mutex.try_lock
   end
 
   def mu_lock
     @_mutex.lock
   end
 
   def mu_unlock
     @_mutex.unlock
   end
 
   private
 
   def mu_initialize
     @_mutex = Mutex.new
   end
 
   def initialize(*args)
     mu_initialize
     super
   end
 end
diff --git a/lib/ruby/1.9/net/imap.rb b/lib/ruby/1.9/net/imap.rb
index 46e8f201a1..3404700297 100644
--- a/lib/ruby/1.9/net/imap.rb
+++ b/lib/ruby/1.9/net/imap.rb
@@ -1,2015 +1,2025 @@
 #
 # = net/imap.rb
 #
 # Copyright (C) 2000  Shugo Maeda <shugo@ruby-lang.org>
 #
 # This library is distributed under the terms of the Ruby license.
 # You can freely distribute/modify this library.
 #
 # Documentation: Shugo Maeda, with RDoc conversion and overview by William
 # Webber.
 #
 # See Net::IMAP for documentation.
 #
 
 
 require "socket"
 require "monitor"
 require "digest/md5"
 require "strscan"
 begin
   require "openssl"
 rescue LoadError
 end
 
 module Net
 
   #
   # Net::IMAP implements Internet Message Access Protocol (IMAP) client
   # functionality.  The protocol is described in [IMAP].
   #
   # == IMAP Overview
   #
   # An IMAP client connects to a server, and then authenticates
   # itself using either #authenticate() or #login().  Having
   # authenticated itself, there is a range of commands
   # available to it.  Most work with mailboxes, which may be
   # arranged in an hierarchical namespace, and each of which
   # contains zero or more messages.  How this is implemented on
   # the server is implementation-dependent; on a UNIX server, it
   # will frequently be implemented as a files in mailbox format
   # within a hierarchy of directories.
   #
   # To work on the messages within a mailbox, the client must
   # first select that mailbox, using either #select() or (for
   # read-only access) #examine().  Once the client has successfully
   # selected a mailbox, they enter _selected_ state, and that
   # mailbox becomes the _current_ mailbox, on which mail-item
   # related commands implicitly operate.
   #
   # Messages have two sorts of identifiers: message sequence
   # numbers, and UIDs.
   #
   # Message sequence numbers number messages within a mail box
   # from 1 up to the number of items in the mail box.  If new
   # message arrives during a session, it receives a sequence
   # number equal to the new size of the mail box.  If messages
   # are expunged from the mailbox, remaining messages have their
   # sequence numbers "shuffled down" to fill the gaps.
   #
   # UIDs, on the other hand, are permanently guaranteed not to
   # identify another message within the same mailbox, even if
   # the existing message is deleted.  UIDs are required to
   # be assigned in ascending (but not necessarily sequential)
   # order within a mailbox; this means that if a non-IMAP client
   # rearranges the order of mailitems within a mailbox, the
   # UIDs have to be reassigned.  An IMAP client cannot thus
   # rearrange message orders.
   #
   # == Examples of Usage
   #
   # === List sender and subject of all recent messages in the default mailbox
   #
   #   imap = Net::IMAP.new('mail.example.com')
   #   imap.authenticate('LOGIN', 'joe_user', 'joes_password')
   #   imap.examine('INBOX')
   #   imap.search(["RECENT"]).each do |message_id|
   #     envelope = imap.fetch(message_id, "ENVELOPE")[0].attr["ENVELOPE"]
   #     puts "#{envelope.from[0].name}: \t#{envelope.subject}"
   #   end
   #
   # === Move all messages from April 2003 from "Mail/sent-mail" to "Mail/sent-apr03"
   #
   #   imap = Net::IMAP.new('mail.example.com')
   #   imap.authenticate('LOGIN', 'joe_user', 'joes_password')
   #   imap.select('Mail/sent-mail')
   #   if not imap.list('Mail/', 'sent-apr03')
   #     imap.create('Mail/sent-apr03')
   #   end
   #   imap.search(["BEFORE", "30-Apr-2003", "SINCE", "1-Apr-2003"]).each do |message_id|
   #     imap.copy(message_id, "Mail/sent-apr03")
   #     imap.store(message_id, "+FLAGS", [:Deleted])
   #   end
   #   imap.expunge
   #
   # == Thread Safety
   #
   # Net::IMAP supports concurrent threads. For example,
   #
   #   imap = Net::IMAP.new("imap.foo.net", "imap2")
   #   imap.authenticate("cram-md5", "bar", "password")
   #   imap.select("inbox")
   #   fetch_thread = Thread.start { imap.fetch(1..-1, "UID") }
   #   search_result = imap.search(["BODY", "hello"])
   #   fetch_result = fetch_thread.value
   #   imap.disconnect
   #
   # This script invokes the FETCH command and the SEARCH command concurrently.
   #
   # == Errors
   #
   # An IMAP server can send three different types of responses to indicate
   # failure:
   #
   # NO:: the attempted command could not be successfully completed.  For
   #      instance, the username/password used for logging in are incorrect;
   #      the selected mailbox does not exists; etc.
   #
   # BAD:: the request from the client does not follow the server's
   #       understanding of the IMAP protocol.  This includes attempting
   #       commands from the wrong client state; for instance, attempting
   #       to perform a SEARCH command without having SELECTed a current
   #       mailbox.  It can also signal an internal server
   #       failure (such as a disk crash) has occurred.
   #
   # BYE:: the server is saying goodbye.  This can be part of a normal
   #       logout sequence, and can be used as part of a login sequence
   #       to indicate that the server is (for some reason) unwilling
   #       to accept our connection.  As a response to any other command,
   #       it indicates either that the server is shutting down, or that
   #       the server is timing out the client connection due to inactivity.
   #
   # These three error response are represented by the errors
   # Net::IMAP::NoResponseError, Net::IMAP::BadResponseError, and
   # Net::IMAP::ByeResponseError, all of which are subclasses of
   # Net::IMAP::ResponseError.  Essentially, all methods that involve
   # sending a request to the server can generate one of these errors.
   # Only the most pertinent instances have been documented below.
   #
   # Because the IMAP class uses Sockets for communication, its methods
   # are also susceptible to the various errors that can occur when
   # working with sockets.  These are generally represented as
   # Errno errors.  For instance, any method that involves sending a
   # request to the server and/or receiving a response from it could
   # raise an Errno::EPIPE error if the network connection unexpectedly
   # goes down.  See the socket(7), ip(7), tcp(7), socket(2), connect(2),
   # and associated man pages.
   #
   # Finally, a Net::IMAP::DataFormatError is thrown if low-level data
   # is found to be in an incorrect format (for instance, when converting
   # between UTF-8 and UTF-16), and Net::IMAP::ResponseParseError is
   # thrown if a server response is non-parseable.
   #
   #
   # == References
   #
   # [[IMAP]]
   #    M. Crispin, "INTERNET MESSAGE ACCESS PROTOCOL - VERSION 4rev1",
   #    RFC 2060, December 1996.  (Note: since obsoleted by RFC 3501)
   #
   # [[LANGUAGE-TAGS]]
   #    Alvestrand, H., "Tags for the Identification of
   #    Languages", RFC 1766, March 1995.
   #
   # [[MD5]]
   #    Myers, J., and M. Rose, "The Content-MD5 Header Field", RFC
   #    1864, October 1995.
   #
   # [[MIME-IMB]]
   #    Freed, N., and N. Borenstein, "MIME (Multipurpose Internet
   #    Mail Extensions) Part One: Format of Internet Message Bodies", RFC
   #    2045, November 1996.
   #
   # [[RFC-822]]
   #    Crocker, D., "Standard for the Format of ARPA Internet Text
   #    Messages", STD 11, RFC 822, University of Delaware, August 1982.
   #
   # [[RFC-2087]]
   #    Myers, J., "IMAP4 QUOTA extension", RFC 2087, January 1997.
   #
   # [[RFC-2086]]
   #    Myers, J., "IMAP4 ACL extension", RFC 2086, January 1997.
   #
   # [[RFC-2195]]
   #    Klensin, J., Catoe, R., and Krumviede, P., "IMAP/POP AUTHorize Extension
   #    for Simple Challenge/Response", RFC 2195, September 1997.
   #
   # [[SORT-THREAD-EXT]]
   #    Crispin, M., "INTERNET MESSAGE ACCESS PROTOCOL - SORT and THREAD
   #    Extensions", draft-ietf-imapext-sort, May 2003.
   #
   # [[OSSL]]
   #    http://www.openssl.org
   #
   # [[RSSL]]
   #    http://savannah.gnu.org/projects/rubypki
   #
   # [[UTF7]]
   #    Goldsmith, D. and Davis, M., "UTF-7: A Mail-Safe Transformation Format of
   #    Unicode", RFC 2152, May 1997.
   #
   class IMAP
     include MonitorMixin
     if defined?(OpenSSL)
       include OpenSSL
       include SSL
     end
 
     #  Returns an initial greeting response from the server.
     attr_reader :greeting
 
     # Returns recorded untagged responses.  For example:
     #
     #   imap.select("inbox")
     #   p imap.responses["EXISTS"][-1]
     #   #=> 2
     #   p imap.responses["UIDVALIDITY"][-1]
     #   #=> 968263756
     attr_reader :responses
 
     # Returns all response handlers.
     attr_reader :response_handlers
 
     # The thread to receive exceptions.
     attr_accessor :client_thread
 
     # Flag indicating a message has been seen
     SEEN = :Seen
 
     # Flag indicating a message has been answered
     ANSWERED = :Answered
 
     # Flag indicating a message has been flagged for special or urgent
     # attention
     FLAGGED = :Flagged
 
     # Flag indicating a message has been marked for deletion.  This
     # will occur when the mailbox is closed or expunged.
     DELETED = :Deleted
 
     # Flag indicating a message is only a draft or work-in-progress version.
     DRAFT = :Draft
 
     # Flag indicating that the message is "recent", meaning that this
     # session is the first session in which the client has been notified
     # of this message.
     RECENT = :Recent
 
     # Flag indicating that a mailbox context name cannot contain
     # children.
     NOINFERIORS = :Noinferiors
 
     # Flag indicating that a mailbox is not selected.
     NOSELECT = :Noselect
 
     # Flag indicating that a mailbox has been marked "interesting" by
     # the server; this commonly indicates that the mailbox contains
     # new messages.
     MARKED = :Marked
 
     # Flag indicating that the mailbox does not contains new messages.
     UNMARKED = :Unmarked
 
     # Returns the debug mode.
     def self.debug
       return @@debug
     end
 
     # Sets the debug mode.
     def self.debug=(val)
       return @@debug = val
     end
 
     # Returns the max number of flags interned to symbols.
     def self.max_flag_count
       return @@max_flag_count
     end
 
     # Sets the max number of flags interned to symbols.
     def self.max_flag_count=(count)
       @@max_flag_count = count
     end
 
     # Adds an authenticator for Net::IMAP#authenticate.  +auth_type+
     # is the type of authentication this authenticator supports
     # (for instance, "LOGIN").  The +authenticator+ is an object
     # which defines a process() method to handle authentication with
     # the server.  See Net::IMAP::LoginAuthenticator,
     # Net::IMAP::CramMD5Authenticator, and Net::IMAP::DigestMD5Authenticator
     # for examples.
     #
     #
     # If +auth_type+ refers to an existing authenticator, it will be
     # replaced by the new one.
     def self.add_authenticator(auth_type, authenticator)
       @@authenticators[auth_type] = authenticator
     end
 
     # Disconnects from the server.
     def disconnect
       begin
         begin
           # try to call SSL::SSLSocket#io.
           @sock.io.shutdown
         rescue NoMethodError
           # @sock is not an SSL::SSLSocket.
           @sock.shutdown
         end
       rescue Errno::ENOTCONN
         # ignore `Errno::ENOTCONN: Socket is not connected' on some platforms.
+      rescue Exception => e
+        @receiver_thread.raise(e)
       end
       @receiver_thread.join
-      @sock.close
+      synchronize do
+        unless @sock.closed?
+          @sock.close 
+        end
+      end
+      raise e if e
     end
 
     # Returns true if disconnected from the server.
     def disconnected?
       return @sock.closed?
     end
 
     # Sends a CAPABILITY command, and returns an array of
     # capabilities that the server supports.  Each capability
     # is a string.  See [IMAP] for a list of possible
     # capabilities.
     #
     # Note that the Net::IMAP class does not modify its
     # behaviour according to the capabilities of the server;
     # it is up to the user of the class to ensure that
     # a certain capability is supported by a server before
     # using it.
     def capability
       synchronize do
         send_command("CAPABILITY")
         return @responses.delete("CAPABILITY")[-1]
       end
     end
 
     # Sends a NOOP command to the server. It does nothing.
     def noop
       send_command("NOOP")
     end
 
     # Sends a LOGOUT command to inform the server that the client is
     # done with the connection.
     def logout
       send_command("LOGOUT")
     end
 
     # Sends a STARTTLS command to start TLS session.
     def starttls(options = {}, verify = true)
       send_command("STARTTLS") do |resp|
         if resp.kind_of?(TaggedResponse) && resp.name == "OK"
           begin
             # for backward compatibility
             certs = options.to_str
             options = create_ssl_params(certs, verify)
           rescue NoMethodError
           end
           start_tls_session(options)
         end
       end
     end
 
     # Sends an AUTHENTICATE command to authenticate the client.
     # The +auth_type+ parameter is a string that represents
     # the authentication mechanism to be used. Currently Net::IMAP
     # supports authentication mechanisms:
     #
     #   LOGIN:: login using cleartext user and password.
     #   CRAM-MD5:: login with cleartext user and encrypted password
     #              (see [RFC-2195] for a full description).  This
     #              mechanism requires that the server have the user's
     #              password stored in clear-text password.
     #
     # For both these mechanisms, there should be two +args+: username
     # and (cleartext) password.  A server may not support one or other
     # of these mechanisms; check #capability() for a capability of
     # the form "AUTH=LOGIN" or "AUTH=CRAM-MD5".
     #
     # Authentication is done using the appropriate authenticator object:
     # see @@authenticators for more information on plugging in your own
     # authenticator.
     #
     # For example:
     #
     #    imap.authenticate('LOGIN', user, password)
     #
     # A Net::IMAP::NoResponseError is raised if authentication fails.
     def authenticate(auth_type, *args)
       auth_type = auth_type.upcase
       unless @@authenticators.has_key?(auth_type)
         raise ArgumentError,
           format('unknown auth type - "%s"', auth_type)
       end
       authenticator = @@authenticators[auth_type].new(*args)
       send_command("AUTHENTICATE", auth_type) do |resp|
         if resp.instance_of?(ContinuationRequest)
           data = authenticator.process(resp.data.text.unpack("m")[0])
           s = [data].pack("m").gsub(/\n/, "")
           send_string_data(s)
           put_string(CRLF)
         end
       end
     end
 
     # Sends a LOGIN command to identify the client and carries
     # the plaintext +password+ authenticating this +user+.  Note
     # that, unlike calling #authenticate() with an +auth_type+
     # of "LOGIN", #login() does *not* use the login authenticator.
     #
     # A Net::IMAP::NoResponseError is raised if authentication fails.
     def login(user, password)
       send_command("LOGIN", user, password)
     end
 
     # Sends a SELECT command to select a +mailbox+ so that messages
     # in the +mailbox+ can be accessed.
     #
     # After you have selected a mailbox, you may retrieve the
     # number of items in that mailbox from @responses["EXISTS"][-1],
     # and the number of recent messages from @responses["RECENT"][-1].
     # Note that these values can change if new messages arrive
     # during a session; see #add_response_handler() for a way of
     # detecting this event.
     #
     # A Net::IMAP::NoResponseError is raised if the mailbox does not
     # exist or is for some reason non-selectable.
     def select(mailbox)
       synchronize do
         @responses.clear
         send_command("SELECT", mailbox)
       end
     end
 
     # Sends a EXAMINE command to select a +mailbox+ so that messages
     # in the +mailbox+ can be accessed.  Behaves the same as #select(),
     # except that the selected +mailbox+ is identified as read-only.
     #
     # A Net::IMAP::NoResponseError is raised if the mailbox does not
     # exist or is for some reason non-examinable.
     def examine(mailbox)
       synchronize do
         @responses.clear
         send_command("EXAMINE", mailbox)
       end
     end
 
     # Sends a CREATE command to create a new +mailbox+.
     #
     # A Net::IMAP::NoResponseError is raised if a mailbox with that name
     # cannot be created.
     def create(mailbox)
       send_command("CREATE", mailbox)
     end
 
     # Sends a DELETE command to remove the +mailbox+.
     #
     # A Net::IMAP::NoResponseError is raised if a mailbox with that name
     # cannot be deleted, either because it does not exist or because the
     # client does not have permission to delete it.
     def delete(mailbox)
       send_command("DELETE", mailbox)
     end
 
     # Sends a RENAME command to change the name of the +mailbox+ to
     # +newname+.
     #
     # A Net::IMAP::NoResponseError is raised if a mailbox with the
     # name +mailbox+ cannot be renamed to +newname+ for whatever
     # reason; for instance, because +mailbox+ does not exist, or
     # because there is already a mailbox with the name +newname+.
     def rename(mailbox, newname)
       send_command("RENAME", mailbox, newname)
     end
 
     # Sends a SUBSCRIBE command to add the specified +mailbox+ name to
     # the server's set of "active" or "subscribed" mailboxes as returned
     # by #lsub().
     #
     # A Net::IMAP::NoResponseError is raised if +mailbox+ cannot be
     # subscribed to, for instance because it does not exist.
     def subscribe(mailbox)
       send_command("SUBSCRIBE", mailbox)
     end
 
     # Sends a UNSUBSCRIBE command to remove the specified +mailbox+ name
     # from the server's set of "active" or "subscribed" mailboxes.
     #
     # A Net::IMAP::NoResponseError is raised if +mailbox+ cannot be
     # unsubscribed from, for instance because the client is not currently
     # subscribed to it.
     def unsubscribe(mailbox)
       send_command("UNSUBSCRIBE", mailbox)
     end
 
     # Sends a LIST command, and returns a subset of names from
     # the complete set of all names available to the client.
     # +refname+ provides a context (for instance, a base directory
     # in a directory-based mailbox hierarchy).  +mailbox+ specifies
     # a mailbox or (via wildcards) mailboxes under that context.
     # Two wildcards may be used in +mailbox+: '*', which matches
     # all characters *including* the hierarchy delimiter (for instance,
     # '/' on a UNIX-hosted directory-based mailbox hierarchy); and '%',
     # which matches all characters *except* the hierarchy delimiter.
     #
     # If +refname+ is empty, +mailbox+ is used directly to determine
     # which mailboxes to match.  If +mailbox+ is empty, the root
     # name of +refname+ and the hierarchy delimiter are returned.
     #
     # The return value is an array of +Net::IMAP::MailboxList+. For example:
     #
     #   imap.create("foo/bar")
     #   imap.create("foo/baz")
     #   p imap.list("", "foo/%")
     #   #=> [#<Net::IMAP::MailboxList attr=[:Noselect], delim="/", name="foo/">, \\
     #        #<Net::IMAP::MailboxList attr=[:Noinferiors, :Marked], delim="/", name="foo/bar">, \\
     #        #<Net::IMAP::MailboxList attr=[:Noinferiors], delim="/", name="foo/baz">]
     def list(refname, mailbox)
       synchronize do
         send_command("LIST", refname, mailbox)
         return @responses.delete("LIST")
       end
     end
 
     # Sends the GETQUOTAROOT command along with specified +mailbox+.
     # This command is generally available to both admin and user.
     # If mailbox exists, returns an array containing objects of
     # Net::IMAP::MailboxQuotaRoot and Net::IMAP::MailboxQuota.
     def getquotaroot(mailbox)
       synchronize do
         send_command("GETQUOTAROOT", mailbox)
         result = []
         result.concat(@responses.delete("QUOTAROOT"))
         result.concat(@responses.delete("QUOTA"))
         return result
       end
     end
 
     # Sends the GETQUOTA command along with specified +mailbox+.
     # If this mailbox exists, then an array containing a
     # Net::IMAP::MailboxQuota object is returned.  This
     # command generally is only available to server admin.
     def getquota(mailbox)
       synchronize do
         send_command("GETQUOTA", mailbox)
         return @responses.delete("QUOTA")
       end
     end
 
     # Sends a SETQUOTA command along with the specified +mailbox+ and
     # +quota+.  If +quota+ is nil, then quota will be unset for that
     # mailbox.  Typically one needs to be logged in as server admin
     # for this to work.  The IMAP quota commands are described in
     # [RFC-2087].
     def setquota(mailbox, quota)
       if quota.nil?
         data = '()'
       else
         data = '(STORAGE ' + quota.to_s + ')'
       end
       send_command("SETQUOTA", mailbox, RawData.new(data))
     end
 
     # Sends the SETACL command along with +mailbox+, +user+ and the
     # +rights+ that user is to have on that mailbox.  If +rights+ is nil,
     # then that user will be stripped of any rights to that mailbox.
     # The IMAP ACL commands are described in [RFC-2086].
     def setacl(mailbox, user, rights)
       if rights.nil?
         send_command("SETACL", mailbox, user, "")
       else
         send_command("SETACL", mailbox, user, rights)
       end
     end
 
     # Send the GETACL command along with specified +mailbox+.
     # If this mailbox exists, an array containing objects of
     # Net::IMAP::MailboxACLItem will be returned.
     def getacl(mailbox)
       synchronize do
         send_command("GETACL", mailbox)
         return @responses.delete("ACL")[-1]
       end
     end
 
     # Sends a LSUB command, and returns a subset of names from the set
     # of names that the user has declared as being "active" or
     # "subscribed".  +refname+ and +mailbox+ are interpreted as
     # for #list().
     # The return value is an array of +Net::IMAP::MailboxList+.
     def lsub(refname, mailbox)
       synchronize do
         send_command("LSUB", refname, mailbox)
         return @responses.delete("LSUB")
       end
     end
 
     # Sends a STATUS command, and returns the status of the indicated
     # +mailbox+. +attr+ is a list of one or more attributes that
     # we are request the status of.  Supported attributes include:
     #
     #   MESSAGES:: the number of messages in the mailbox.
     #   RECENT:: the number of recent messages in the mailbox.
     #   UNSEEN:: the number of unseen messages in the mailbox.
     #
     # The return value is a hash of attributes. For example:
     #
     #   p imap.status("inbox", ["MESSAGES", "RECENT"])
     #   #=> {"RECENT"=>0, "MESSAGES"=>44}
     #
     # A Net::IMAP::NoResponseError is raised if status values
     # for +mailbox+ cannot be returned, for instance because it
     # does not exist.
     def status(mailbox, attr)
       synchronize do
         send_command("STATUS", mailbox, attr)
         return @responses.delete("STATUS")[-1].attr
       end
     end
 
     # Sends a APPEND command to append the +message+ to the end of
     # the +mailbox+. The optional +flags+ argument is an array of
     # flags to initially passing to the new message.  The optional
     # +date_time+ argument specifies the creation time to assign to the
     # new message; it defaults to the current time.
     # For example:
     #
     #   imap.append("inbox", <<EOF.gsub(/\n/, "\r\n"), [:Seen], Time.now)
     #   Subject: hello
     #   From: shugo@ruby-lang.org
     #   To: shugo@ruby-lang.org
     #
     #   hello world
     #   EOF
     #
     # A Net::IMAP::NoResponseError is raised if the mailbox does
     # not exist (it is not created automatically), or if the flags,
     # date_time, or message arguments contain errors.
     def append(mailbox, message, flags = nil, date_time = nil)
       args = []
       if flags
         args.push(flags)
       end
       args.push(date_time) if date_time
       args.push(Literal.new(message))
       send_command("APPEND", mailbox, *args)
     end
 
     # Sends a CHECK command to request a checkpoint of the currently
     # selected mailbox.  This performs implementation-specific
     # housekeeping, for instance, reconciling the mailbox's
     # in-memory and on-disk state.
     def check
       send_command("CHECK")
     end
 
     # Sends a CLOSE command to close the currently selected mailbox.
     # The CLOSE command permanently removes from the mailbox all
     # messages that have the \Deleted flag set.
     def close
       send_command("CLOSE")
     end
 
     # Sends a EXPUNGE command to permanently remove from the currently
     # selected mailbox all messages that have the \Deleted flag set.
     def expunge
       synchronize do
         send_command("EXPUNGE")
         return @responses.delete("EXPUNGE")
       end
     end
 
     # Sends a SEARCH command to search the mailbox for messages that
     # match the given searching criteria, and returns message sequence
     # numbers.  +keys+ can either be a string holding the entire
     # search string, or a single-dimension array of search keywords and
     # arguments.  The following are some common search criteria;
     # see [IMAP] section 6.4.4 for a full list.
     #
     # <message set>:: a set of message sequence numbers.  ',' indicates
     #                 an interval, ':' indicates a range.  For instance,
     #                 '2,10:12,15' means "2,10,11,12,15".
     #
     # BEFORE <date>:: messages with an internal date strictly before
     #                 <date>.  The date argument has a format similar
     #                 to 8-Aug-2002.
     #
     # BODY <string>:: messages that contain <string> within their body.
     #
     # CC <string>:: messages containing <string> in their CC field.
     #
     # FROM <string>:: messages that contain <string> in their FROM field.
     #
     # NEW:: messages with the \Recent, but not the \Seen, flag set.
     #
     # NOT <search-key>:: negate the following search key.
     #
     # OR <search-key> <search-key>:: "or" two search keys together.
     #
     # ON <date>:: messages with an internal date exactly equal to <date>,
     #             which has a format similar to 8-Aug-2002.
     #
     # SINCE <date>:: messages with an internal date on or after <date>.
     #
     # SUBJECT <string>:: messages with <string> in their subject.
     #
     # TO <string>:: messages with <string> in their TO field.
     #
     # For example:
     #
     #   p imap.search(["SUBJECT", "hello", "NOT", "NEW"])
     #   #=> [1, 6, 7, 8]
     def search(keys, charset = nil)
       return search_internal("SEARCH", keys, charset)
     end
 
     # As for #search(), but returns unique identifiers.
     def uid_search(keys, charset = nil)
       return search_internal("UID SEARCH", keys, charset)
     end
 
     # Sends a FETCH command to retrieve data associated with a message
     # in the mailbox. The +set+ parameter is a number or an array of
     # numbers or a Range object. The number is a message sequence
     # number.  +attr+ is a list of attributes to fetch; see the
     # documentation for Net::IMAP::FetchData for a list of valid
     # attributes.
     # The return value is an array of Net::IMAP::FetchData. For example:
     #
     #   p imap.fetch(6..8, "UID")
     #   #=> [#<Net::IMAP::FetchData seqno=6, attr={"UID"=>98}>, \\
     #        #<Net::IMAP::FetchData seqno=7, attr={"UID"=>99}>, \\
     #        #<Net::IMAP::FetchData seqno=8, attr={"UID"=>100}>]
     #   p imap.fetch(6, "BODY[HEADER.FIELDS (SUBJECT)]")
     #   #=> [#<Net::IMAP::FetchData seqno=6, attr={"BODY[HEADER.FIELDS (SUBJECT)]"=>"Subject: test\r\n\r\n"}>]
     #   data = imap.uid_fetch(98, ["RFC822.SIZE", "INTERNALDATE"])[0]
     #   p data.seqno
     #   #=> 6
     #   p data.attr["RFC822.SIZE"]
     #   #=> 611
     #   p data.attr["INTERNALDATE"]
     #   #=> "12-Oct-2000 22:40:59 +0900"
     #   p data.attr["UID"]
     #   #=> 98
     def fetch(set, attr)
       return fetch_internal("FETCH", set, attr)
     end
 
     # As for #fetch(), but +set+ contains unique identifiers.
     def uid_fetch(set, attr)
       return fetch_internal("UID FETCH", set, attr)
     end
 
     # Sends a STORE command to alter data associated with messages
     # in the mailbox, in particular their flags. The +set+ parameter
     # is a number or an array of numbers or a Range object. Each number
     # is a message sequence number.  +attr+ is the name of a data item
     # to store: 'FLAGS' means to replace the message's flag list
     # with the provided one; '+FLAGS' means to add the provided flags;
     # and '-FLAGS' means to remove them.  +flags+ is a list of flags.
     #
     # The return value is an array of Net::IMAP::FetchData. For example:
     #
     #   p imap.store(6..8, "+FLAGS", [:Deleted])
     #   #=> [#<Net::IMAP::FetchData seqno=6, attr={"FLAGS"=>[:Seen, :Deleted]}>, \\
     #        #<Net::IMAP::FetchData seqno=7, attr={"FLAGS"=>[:Seen, :Deleted]}>, \\
     #        #<Net::IMAP::FetchData seqno=8, attr={"FLAGS"=>[:Seen, :Deleted]}>]
     def store(set, attr, flags)
       return store_internal("STORE", set, attr, flags)
     end
 
     # As for #store(), but +set+ contains unique identifiers.
     def uid_store(set, attr, flags)
       return store_internal("UID STORE", set, attr, flags)
     end
 
     # Sends a COPY command to copy the specified message(s) to the end
     # of the specified destination +mailbox+. The +set+ parameter is
     # a number or an array of numbers or a Range object. The number is
     # a message sequence number.
     def copy(set, mailbox)
       copy_internal("COPY", set, mailbox)
     end
 
     # As for #copy(), but +set+ contains unique identifiers.
     def uid_copy(set, mailbox)
       copy_internal("UID COPY", set, mailbox)
     end
 
     # Sends a SORT command to sort messages in the mailbox.
     # Returns an array of message sequence numbers. For example:
     #
     #   p imap.sort(["FROM"], ["ALL"], "US-ASCII")
     #   #=> [1, 2, 3, 5, 6, 7, 8, 4, 9]
     #   p imap.sort(["DATE"], ["SUBJECT", "hello"], "US-ASCII")
     #   #=> [6, 7, 8, 1]
     #
     # See [SORT-THREAD-EXT] for more details.
     def sort(sort_keys, search_keys, charset)
       return sort_internal("SORT", sort_keys, search_keys, charset)
     end
 
     # As for #sort(), but returns an array of unique identifiers.
     def uid_sort(sort_keys, search_keys, charset)
       return sort_internal("UID SORT", sort_keys, search_keys, charset)
     end
 
     # Adds a response handler. For example, to detect when
     # the server sends us a new EXISTS response (which normally
     # indicates new messages being added to the mail box),
     # you could add the following handler after selecting the
     # mailbox.
     #
     #   imap.add_response_handler { |resp|
     #     if resp.kind_of?(Net::IMAP::UntaggedResponse) and resp.name == "EXISTS"
     #       puts "Mailbox now has #{resp.data} messages"
     #     end
     #   }
     #
     def add_response_handler(handler = Proc.new)
       @response_handlers.push(handler)
     end
 
     # Removes the response handler.
     def remove_response_handler(handler)
       @response_handlers.delete(handler)
     end
 
     # As for #search(), but returns message sequence numbers in threaded
     # format, as a Net::IMAP::ThreadMember tree.  The supported algorithms
     # are:
     #
     # ORDEREDSUBJECT:: split into single-level threads according to subject,
     #                  ordered by date.
     # REFERENCES:: split into threads by parent/child relationships determined
     #              by which message is a reply to which.
     #
     # Unlike #search(), +charset+ is a required argument.  US-ASCII
     # and UTF-8 are sample values.
     #
     # See [SORT-THREAD-EXT] for more details.
     def thread(algorithm, search_keys, charset)
       return thread_internal("THREAD", algorithm, search_keys, charset)
     end
 
     # As for #thread(), but returns unique identifiers instead of
     # message sequence numbers.
     def uid_thread(algorithm, search_keys, charset)
       return thread_internal("UID THREAD", algorithm, search_keys, charset)
     end
 
     # Sends an IDLE command that waits for notifications of new or expunged
     # messages.  Yields responses from the server during the IDLE.
     #
     # Use #idle_done() to leave IDLE.
     def idle(&response_handler)
       raise LocalJumpError, "no block given" unless response_handler
 
       response = nil
 
       synchronize do
         tag = Thread.current[:net_imap_tag] = generate_tag
         put_string("#{tag} IDLE#{CRLF}")
 
         begin
           add_response_handler(response_handler)
           @idle_done_cond = new_cond
           @idle_done_cond.wait
           @idle_done_cond = nil
         ensure
           remove_response_handler(response_handler)
           put_string("DONE#{CRLF}")
           response = get_tagged_response(tag, "IDLE")
         end
       end
 
       return response
     end
 
     # Leaves IDLE.
     def idle_done
       synchronize do
         if @idle_done_cond.nil?
           raise Net::IMAP::Error, "not during IDLE"
         end
         @idle_done_cond.signal
       end
     end
 
     # Decode a string from modified UTF-7 format to UTF-8.
     #
     # UTF-7 is a 7-bit encoding of Unicode [UTF7].  IMAP uses a
     # slightly modified version of this to encode mailbox names
     # containing non-ASCII characters; see [IMAP] section 5.1.3.
     #
     # Net::IMAP does _not_ automatically encode and decode
     # mailbox names to and from utf7.
     def self.decode_utf7(s)
       return s.gsub(/&(.*?)-/n) {
         if $1.empty?
           "&"
         else
           base64 = $1.tr(",", "/")
           x = base64.length % 4
           if x > 0
             base64.concat("=" * (4 - x))
           end
           base64.unpack("m")[0].unpack("n*").pack("U*")
         end
       }.force_encoding("UTF-8")
     end
 
     # Encode a string from UTF-8 format to modified UTF-7.
     def self.encode_utf7(s)
       return s.gsub(/(&)|([^\x20-\x7e]+)/u) {
         if $1
           "&-"
         else
           base64 = [$&.unpack("U*").pack("n*")].pack("m")
           "&" + base64.delete("=\n").tr("/", ",") + "-"
         end
       }.force_encoding("ASCII-8BIT")
     end
 
     # Formats +time+ as an IMAP-style date.
     def self.format_date(time)
       return time.strftime('%d-%b-%Y')
     end
 
     # Formats +time+ as an IMAP-style date-time.
     def self.format_datetime(time)
       return time.strftime('%d-%b-%Y %H:%M %z')
     end
 
     private
 
     CRLF = "\r\n"      # :nodoc:
     PORT = 143         # :nodoc:
     SSL_PORT = 993   # :nodoc:
 
     @@debug = false
     @@authenticators = {}
     @@max_flag_count = 10000
 
     # call-seq:
     #    Net::IMAP.new(host, options = {})
     #
     # Creates a new Net::IMAP object and connects it to the specified
     # +host+.
     #
     # +options+ is an option hash, each key of which is a symbol.
     #
     # The available options are:
     #
     # port::  port number (default value is 143 for imap, or 993 for imaps)
     # ssl::   if options[:ssl] is true, then an attempt will be made
     #         to use SSL (now TLS) to connect to the server.  For this to work
     #         OpenSSL [OSSL] and the Ruby OpenSSL [RSSL] extensions need to
     #         be installed.
     #         if options[:ssl] is a hash, it's passed to
     #         OpenSSL::SSL::SSLContext#set_params as parameters.
     #
     # The most common errors are:
     #
     # Errno::ECONNREFUSED:: connection refused by +host+ or an intervening
     #                       firewall.
     # Errno::ETIMEDOUT:: connection timed out (possibly due to packets
     #                    being dropped by an intervening firewall).
     # Errno::ENETUNREACH:: there is no route to that network.
     # SocketError:: hostname not known or other socket error.
     # Net::IMAP::ByeResponseError:: we connected to the host, but they
     #                               immediately said goodbye to us.
     def initialize(host, port_or_options = {},
                    usessl = false, certs = nil, verify = true)
       super()
       @host = host
       begin
         options = port_or_options.to_hash
       rescue NoMethodError
         # for backward compatibility
         options = {}
         options[:port] = port_or_options
         if usessl
           options[:ssl] = create_ssl_params(certs, verify)
         end
       end
       @port = options[:port] || (options[:ssl] ? SSL_PORT : PORT)
       @tag_prefix = "RUBY"
       @tagno = 0
       @parser = ResponseParser.new
       @sock = TCPSocket.open(@host, @port)
       if options[:ssl]
         start_tls_session(options[:ssl])
         @usessl = true
       else
         @usessl = false
       end
       @responses = Hash.new([].freeze)
       @tagged_responses = {}
       @response_handlers = []
       @tagged_response_arrival = new_cond
       @continuation_request_arrival = new_cond
       @idle_done_cond = nil
       @logout_command_tag = nil
       @debug_output_bol = true
       @exception = nil
 
       @greeting = get_response
       if @greeting.name == "BYE"
         @sock.close
         raise ByeResponseError, @greeting
       end
 
       @client_thread = Thread.current
       @receiver_thread = Thread.start {
-        receive_responses
+        begin
+          receive_responses
+        rescue Exception
+        end
       }
     end
 
     def receive_responses
       connection_closed = false
       until connection_closed
         synchronize do
           @exception = nil
         end
         begin
           resp = get_response
         rescue Exception => e
           synchronize do
             @sock.close
             @exception = e
           end
           break
         end
         unless resp
           synchronize do
             @exception = EOFError.new("end of file reached")
           end
           break
         end
         begin
           synchronize do
             case resp
             when TaggedResponse
               @tagged_responses[resp.tag] = resp
               @tagged_response_arrival.broadcast
               if resp.tag == @logout_command_tag
                 return
               end
             when UntaggedResponse
               record_response(resp.name, resp.data)
               if resp.data.instance_of?(ResponseText) &&
                   (code = resp.data.code)
                 record_response(code.name, code.data)
               end
               if resp.name == "BYE" && @logout_command_tag.nil?
                 @sock.close
                 @exception = ByeResponseError.new(resp)
                 connection_closed = true
               end
             when ContinuationRequest
               @continuation_request_arrival.signal
             end
             @response_handlers.each do |handler|
               handler.call(resp)
             end
           end
         rescue Exception => e
           @exception = e
           synchronize do
             @tagged_response_arrival.broadcast
             @continuation_request_arrival.broadcast
           end
         end
       end
       synchronize do
         @tagged_response_arrival.broadcast
         @continuation_request_arrival.broadcast
       end
     end
 
     def get_tagged_response(tag, cmd)
       until @tagged_responses.key?(tag)
         raise @exception if @exception
         @tagged_response_arrival.wait
       end
       resp = @tagged_responses.delete(tag)
       case resp.name
       when /\A(?:NO)\z/ni
         raise NoResponseError, resp
       when /\A(?:BAD)\z/ni
         raise BadResponseError, resp
       else
         return resp
       end
     end
 
     def get_response
       buff = ""
       while true
         s = @sock.gets(CRLF)
         break unless s
         buff.concat(s)
         if /\{(\d+)\}\r\n/n =~ s
           s = @sock.read($1.to_i)
           buff.concat(s)
         else
           break
         end
       end
       return nil if buff.length == 0
       if @@debug
         $stderr.print(buff.gsub(/^/n, "S: "))
       end
       return @parser.parse(buff)
     end
 
     def record_response(name, data)
       unless @responses.has_key?(name)
         @responses[name] = []
       end
       @responses[name].push(data)
     end
 
     def send_command(cmd, *args, &block)
       synchronize do
         args.each do |i|
           validate_data(i)
         end
         tag = generate_tag
         put_string(tag + " " + cmd)
         args.each do |i|
           put_string(" ")
           send_data(i)
         end
         put_string(CRLF)
         if cmd == "LOGOUT"
           @logout_command_tag = tag
         end
         if block
           add_response_handler(block)
         end
         begin
           return get_tagged_response(tag, cmd)
         ensure
           if block
             remove_response_handler(block)
           end
         end
       end
     end
 
     def generate_tag
       @tagno += 1
       return format("%s%04d", @tag_prefix, @tagno)
     end
 
     def put_string(str)
       @sock.print(str)
       if @@debug
         if @debug_output_bol
           $stderr.print("C: ")
         end
         $stderr.print(str.gsub(/\n(?!\z)/n, "\nC: "))
         if /\r\n\z/n.match(str)
           @debug_output_bol = true
         else
           @debug_output_bol = false
         end
       end
     end
 
     def validate_data(data)
       case data
       when nil
       when String
       when Integer
         if data < 0 || data >= 4294967296
           raise DataFormatError, num.to_s
         end
       when Array
         data.each do |i|
           validate_data(i)
         end
       when Time
       when Symbol
       else
         data.validate
       end
     end
 
     def send_data(data)
       case data
       when nil
         put_string("NIL")
       when String
         send_string_data(data)
       when Integer
         send_number_data(data)
       when Array
         send_list_data(data)
       when Time
         send_time_data(data)
       when Symbol
         send_symbol_data(data)
       else
         data.send_data(self)
       end
     end
 
     def send_string_data(str)
       case str
       when ""
         put_string('""')
       when /[\x80-\xff\r\n]/n
         # literal
         send_literal(str)
       when /[(){ \x00-\x1f\x7f%*"\\]/n
         # quoted string
         send_quoted_string(str)
       else
         put_string(str)
       end
     end
 
     def send_quoted_string(str)
       put_string('"' + str.gsub(/["\\]/n, "\\\\\\&") + '"')
     end
 
     def send_literal(str)
       put_string("{" + str.length.to_s + "}" + CRLF)
       @continuation_request_arrival.wait
       raise @exception if @exception
       put_string(str)
     end
 
     def send_number_data(num)
       put_string(num.to_s)
     end
 
     def send_list_data(list)
       put_string("(")
       first = true
       list.each do |i|
         if first
           first = false
         else
           put_string(" ")
         end
         send_data(i)
       end
       put_string(")")
     end
 
     DATE_MONTH = %w(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec)
 
     def send_time_data(time)
       t = time.dup.gmtime
       s = format('"%2d-%3s-%4d %02d:%02d:%02d +0000"',
                  t.day, DATE_MONTH[t.month - 1], t.year,
                  t.hour, t.min, t.sec)
       put_string(s)
     end
 
     def send_symbol_data(symbol)
       put_string("\\" + symbol.to_s)
     end
 
     def search_internal(cmd, keys, charset)
       if keys.instance_of?(String)
         keys = [RawData.new(keys)]
       else
         normalize_searching_criteria(keys)
       end
       synchronize do
         if charset
           send_command(cmd, "CHARSET", charset, *keys)
         else
           send_command(cmd, *keys)
         end
         return @responses.delete("SEARCH")[-1]
       end
     end
 
     def fetch_internal(cmd, set, attr)
       case attr
       when String then
         attr = RawData.new(attr)
       when Array then
         attr = attr.map { |arg|
           arg.is_a?(String) ? RawData.new(arg) : arg
         }
       end
 
       synchronize do
         @responses.delete("FETCH")
         send_command(cmd, MessageSet.new(set), attr)
         return @responses.delete("FETCH")
       end
     end
 
     def store_internal(cmd, set, attr, flags)
       if attr.instance_of?(String)
         attr = RawData.new(attr)
       end
       synchronize do
         @responses.delete("FETCH")
         send_command(cmd, MessageSet.new(set), attr, flags)
         return @responses.delete("FETCH")
       end
     end
 
     def copy_internal(cmd, set, mailbox)
       send_command(cmd, MessageSet.new(set), mailbox)
     end
 
     def sort_internal(cmd, sort_keys, search_keys, charset)
       if search_keys.instance_of?(String)
         search_keys = [RawData.new(search_keys)]
       else
         normalize_searching_criteria(search_keys)
       end
       normalize_searching_criteria(search_keys)
       synchronize do
         send_command(cmd, sort_keys, charset, *search_keys)
         return @responses.delete("SORT")[-1]
       end
     end
 
     def thread_internal(cmd, algorithm, search_keys, charset)
       if search_keys.instance_of?(String)
         search_keys = [RawData.new(search_keys)]
       else
         normalize_searching_criteria(search_keys)
       end
       normalize_searching_criteria(search_keys)
       send_command(cmd, algorithm, charset, *search_keys)
       return @responses.delete("THREAD")[-1]
     end
 
     def normalize_searching_criteria(keys)
       keys.collect! do |i|
         case i
         when -1, Range, Array
           MessageSet.new(i)
         else
           i
         end
       end
     end
 
     def create_ssl_params(certs = nil, verify = true)
       params = {}
       if certs
         if File.file?(certs)
           params[:ca_file] = certs
         elsif File.directory?(certs)
           params[:ca_path] = certs
         end
       end
       if verify
         params[:verify_mode] = VERIFY_PEER
       else
         params[:verify_mode] = VERIFY_NONE
       end
       return params
     end
 
     def start_tls_session(params = {})
       unless defined?(OpenSSL)
         raise "SSL extension not installed"
       end
       if @sock.kind_of?(OpenSSL::SSL::SSLSocket)
         raise RuntimeError, "already using SSL"
       end
       begin
         params = params.to_hash
       rescue NoMethodError
         params = {}
       end
       context = SSLContext.new
       context.set_params(params)
       if defined?(VerifyCallbackProc)
         context.verify_callback = VerifyCallbackProc
       end
       @sock = SSLSocket.new(@sock, context)
       @sock.sync_close = true
       @sock.connect
       if context.verify_mode != VERIFY_NONE
         @sock.post_connection_check(@host)
       end
     end
 
     class RawData # :nodoc:
       def send_data(imap)
         imap.send(:put_string, @data)
       end
 
       def validate
       end
 
       private
 
       def initialize(data)
         @data = data
       end
     end
 
     class Atom # :nodoc:
       def send_data(imap)
         imap.send(:put_string, @data)
       end
 
       def validate
       end
 
       private
 
       def initialize(data)
         @data = data
       end
     end
 
     class QuotedString # :nodoc:
       def send_data(imap)
         imap.send(:send_quoted_string, @data)
       end
 
       def validate
       end
 
       private
 
       def initialize(data)
         @data = data
       end
     end
 
     class Literal # :nodoc:
       def send_data(imap)
         imap.send(:send_literal, @data)
       end
 
       def validate
       end
 
       private
 
       def initialize(data)
         @data = data
       end
     end
 
     class MessageSet # :nodoc:
       def send_data(imap)
         imap.send(:put_string, format_internal(@data))
       end
 
       def validate
         validate_internal(@data)
       end
 
       private
 
       def initialize(data)
         @data = data
       end
 
       def format_internal(data)
         case data
         when "*"
           return data
         when Integer
           if data == -1
             return "*"
           else
             return data.to_s
           end
         when Range
           return format_internal(data.first) +
             ":" + format_internal(data.last)
         when Array
           return data.collect {|i| format_internal(i)}.join(",")
         when ThreadMember
           return data.seqno.to_s +
             ":" + data.children.collect {|i| format_internal(i).join(",")}
         end
       end
 
       def validate_internal(data)
         case data
         when "*"
         when Integer
           ensure_nz_number(data)
         when Range
         when Array
           data.each do |i|
             validate_internal(i)
           end
         when ThreadMember
           data.children.each do |i|
             validate_internal(i)
           end
         else
           raise DataFormatError, data.inspect
         end
       end
 
       def ensure_nz_number(num)
         if num < -1 || num == 0 || num >= 4294967296
           msg = "nz_number must be non-zero unsigned 32-bit integer: " +
                 num.inspect
           raise DataFormatError, msg
         end
       end
     end
 
     # Net::IMAP::ContinuationRequest represents command continuation requests.
     #
     # The command continuation request response is indicated by a "+" token
     # instead of a tag.  This form of response indicates that the server is
     # ready to accept the continuation of a command from the client.  The
     # remainder of this response is a line of text.
     #
     #   continue_req    ::= "+" SPACE (resp_text / base64)
     #
     # ==== Fields:
     #
     # data:: Returns the data (Net::IMAP::ResponseText).
     #
     # raw_data:: Returns the raw data string.
     ContinuationRequest = Struct.new(:data, :raw_data)
 
     # Net::IMAP::UntaggedResponse represents untagged responses.
     #
     # Data transmitted by the server to the client and status responses
     # that do not indicate command completion are prefixed with the token
     # "*", and are called untagged responses.
     #
     #   response_data   ::= "*" SPACE (resp_cond_state / resp_cond_bye /
     #                       mailbox_data / message_data / capability_data)
     #
     # ==== Fields:
     #
     # name:: Returns the name such as "FLAGS", "LIST", "FETCH"....
     #
     # data:: Returns the data such as an array of flag symbols,
     #         a ((<Net::IMAP::MailboxList>)) object....
     #
     # raw_data:: Returns the raw data string.
     UntaggedResponse = Struct.new(:name, :data, :raw_data)
 
     # Net::IMAP::TaggedResponse represents tagged responses.
     #
     # The server completion result response indicates the success or
     # failure of the operation.  It is tagged with the same tag as the
     # client command which began the operation.
     #
     #   response_tagged ::= tag SPACE resp_cond_state CRLF
     #
     #   tag             ::= 1*<any ATOM_CHAR except "+">
     #
     #   resp_cond_state ::= ("OK" / "NO" / "BAD") SPACE resp_text
     #
     # ==== Fields:
     #
     # tag:: Returns the tag.
     #
     # name:: Returns the name. the name is one of "OK", "NO", "BAD".
     #
     # data:: Returns the data. See ((<Net::IMAP::ResponseText>)).
     #
     # raw_data:: Returns the raw data string.
     #
     TaggedResponse = Struct.new(:tag, :name, :data, :raw_data)
 
     # Net::IMAP::ResponseText represents texts of responses.
     # The text may be prefixed by the response code.
     #
     #   resp_text       ::= ["[" resp_text_code "]" SPACE] (text_mime2 / text)
     #                       ;; text SHOULD NOT begin with "[" or "="
     #
     # ==== Fields:
     #
     # code:: Returns the response code. See ((<Net::IMAP::ResponseCode>)).
     #
     # text:: Returns the text.
     #
     ResponseText = Struct.new(:code, :text)
 
     #
     # Net::IMAP::ResponseCode represents response codes.
     #
     #   resp_text_code  ::= "ALERT" / "PARSE" /
     #                       "PERMANENTFLAGS" SPACE "(" #(flag / "\*") ")" /
     #                       "READ-ONLY" / "READ-WRITE" / "TRYCREATE" /
     #                       "UIDVALIDITY" SPACE nz_number /
     #                       "UNSEEN" SPACE nz_number /
     #                       atom [SPACE 1*<any TEXT_CHAR except "]">]
     #
     # ==== Fields:
     #
     # name:: Returns the name such as "ALERT", "PERMANENTFLAGS", "UIDVALIDITY"....
     #
     # data:: Returns the data if it exists.
     #
     ResponseCode = Struct.new(:name, :data)
 
     # Net::IMAP::MailboxList represents contents of the LIST response.
     #
     #   mailbox_list    ::= "(" #("\Marked" / "\Noinferiors" /
     #                       "\Noselect" / "\Unmarked" / flag_extension) ")"
     #                       SPACE (<"> QUOTED_CHAR <"> / nil) SPACE mailbox
     #
     # ==== Fields:
     #
     # attr:: Returns the name attributes. Each name attribute is a symbol
     #        capitalized by String#capitalize, such as :Noselect (not :NoSelect).
     #
     # delim:: Returns the hierarchy delimiter
     #
     # name:: Returns the mailbox name.
     #
     MailboxList = Struct.new(:attr, :delim, :name)
 
     # Net::IMAP::MailboxQuota represents contents of GETQUOTA response.
     # This object can also be a response to GETQUOTAROOT.  In the syntax
     # specification below, the delimiter used with the "#" construct is a
     # single space (SPACE).
     #
     #    quota_list      ::= "(" #quota_resource ")"
     #
     #    quota_resource  ::= atom SPACE number SPACE number
     #
     #    quota_response  ::= "QUOTA" SPACE astring SPACE quota_list
     #
     # ==== Fields:
     #
     # mailbox:: The mailbox with the associated quota.
     #
     # usage:: Current storage usage of mailbox.
     #
     # quota:: Quota limit imposed on mailbox.
     #
     MailboxQuota = Struct.new(:mailbox, :usage, :quota)
 
     # Net::IMAP::MailboxQuotaRoot represents part of the GETQUOTAROOT
     # response. (GETQUOTAROOT can also return Net::IMAP::MailboxQuota.)
     #
     #    quotaroot_response ::= "QUOTAROOT" SPACE astring *(SPACE astring)
     #
     # ==== Fields:
     #
     # mailbox:: The mailbox with the associated quota.
     #
     # quotaroots:: Zero or more quotaroots that effect the quota on the
     #              specified mailbox.
     #
     MailboxQuotaRoot = Struct.new(:mailbox, :quotaroots)
 
     # Net::IMAP::MailboxACLItem represents response from GETACL.
     #
     #    acl_data        ::= "ACL" SPACE mailbox *(SPACE identifier SPACE rights)
     #
     #    identifier      ::= astring
     #
     #    rights          ::= astring
     #
     # ==== Fields:
     #
     # user:: Login name that has certain rights to the mailbox
     #        that was specified with the getacl command.
     #
     # rights:: The access rights the indicated user has to the
     #          mailbox.
     #
     MailboxACLItem = Struct.new(:user, :rights)
 
     # Net::IMAP::StatusData represents contents of the STATUS response.
     #
     # ==== Fields:
     #
     # mailbox:: Returns the mailbox name.
     #
     # attr:: Returns a hash. Each key is one of "MESSAGES", "RECENT", "UIDNEXT",
     #        "UIDVALIDITY", "UNSEEN". Each value is a number.
     #
     StatusData = Struct.new(:mailbox, :attr)
 
     # Net::IMAP::FetchData represents contents of the FETCH response.
     #
     # ==== Fields:
     #
     # seqno:: Returns the message sequence number.
     #         (Note: not the unique identifier, even for the UID command response.)
     #
     # attr:: Returns a hash. Each key is a data item name, and each value is
     #        its value.
     #
     #        The current data items are:
     #
     #        [BODY]
     #           A form of BODYSTRUCTURE without extension data.
     #        [BODY[<section>]<<origin_octet>>]
     #           A string expressing the body contents of the specified section.
     #        [BODYSTRUCTURE]
     #           An object that describes the [MIME-IMB] body structure of a message.
     #           See Net::IMAP::BodyTypeBasic, Net::IMAP::BodyTypeText,
     #           Net::IMAP::BodyTypeMessage, Net::IMAP::BodyTypeMultipart.
     #        [ENVELOPE]
     #           A Net::IMAP::Envelope object that describes the envelope
     #           structure of a message.
     #        [FLAGS]
     #           A array of flag symbols that are set for this message. flag symbols
     #           are capitalized by String#capitalize.
     #        [INTERNALDATE]
     #           A string representing the internal date of the message.
     #        [RFC822]
     #           Equivalent to BODY[].
     #        [RFC822.HEADER]
     #           Equivalent to BODY.PEEK[HEADER].
     #        [RFC822.SIZE]
     #           A number expressing the [RFC-822] size of the message.
     #        [RFC822.TEXT]
     #           Equivalent to BODY[TEXT].
     #        [UID]
     #           A number expressing the unique identifier of the message.
     #
     FetchData = Struct.new(:seqno, :attr)
 
     # Net::IMAP::Envelope represents envelope structures of messages.
     #
     # ==== Fields:
     #
     # date:: Returns a string that represents the date.
     #
     # subject:: Returns a string that represents the subject.
     #
     # from:: Returns an array of Net::IMAP::Address that represents the from.
     #
     # sender:: Returns an array of Net::IMAP::Address that represents the sender.
     #
     # reply_to:: Returns an array of Net::IMAP::Address that represents the reply-to.
     #
     # to:: Returns an array of Net::IMAP::Address that represents the to.
     #
     # cc:: Returns an array of Net::IMAP::Address that represents the cc.
     #
     # bcc:: Returns an array of Net::IMAP::Address that represents the bcc.
     #
     # in_reply_to:: Returns a string that represents the in-reply-to.
     #
     # message_id:: Returns a string that represents the message-id.
     #
     Envelope = Struct.new(:date, :subject, :from, :sender, :reply_to,
                           :to, :cc, :bcc, :in_reply_to, :message_id)
 
     #
     # Net::IMAP::Address represents electronic mail addresses.
     #
     # ==== Fields:
     #
     # name:: Returns the phrase from [RFC-822] mailbox.
     #
     # route:: Returns the route from [RFC-822] route-addr.
     #
     # mailbox:: nil indicates end of [RFC-822] group.
     #           If non-nil and host is nil, returns [RFC-822] group name.
     #           Otherwise, returns [RFC-822] local-part
     #
     # host:: nil indicates [RFC-822] group syntax.
     #        Otherwise, returns [RFC-822] domain name.
     #
     Address = Struct.new(:name, :route, :mailbox, :host)
 
     #
     # Net::IMAP::ContentDisposition represents Content-Disposition fields.
     #
     # ==== Fields:
     #
     # dsp_type:: Returns the disposition type.
     #
     # param:: Returns a hash that represents parameters of the Content-Disposition
     #         field.
     #
     ContentDisposition = Struct.new(:dsp_type, :param)
 
     # Net::IMAP::ThreadMember represents a thread-node returned
     # by Net::IMAP#thread
     #
     # ==== Fields:
     #
     # seqno:: The sequence number of this message.
     #
     # children:: an array of Net::IMAP::ThreadMember objects for mail
     # items that are children of this in the thread.
     #
     ThreadMember = Struct.new(:seqno, :children)
 
     # Net::IMAP::BodyTypeBasic represents basic body structures of messages.
     #
     # ==== Fields:
     #
     # media_type:: Returns the content media type name as defined in [MIME-IMB].
     #
     # subtype:: Returns the content subtype name as defined in [MIME-IMB].
     #
     # param:: Returns a hash that represents parameters as defined in [MIME-IMB].
     #
     # content_id:: Returns a string giving the content id as defined in [MIME-IMB].
     #
     # description:: Returns a string giving the content description as defined in
     #               [MIME-IMB].
     #
     # encoding:: Returns a string giving the content transfer encoding as defined in
     #            [MIME-IMB].
     #
     # size:: Returns a number giving the size of the body in octets.
     #
     # md5:: Returns a string giving the body MD5 value as defined in [MD5].
     #
     # disposition:: Returns a Net::IMAP::ContentDisposition object giving
     #               the content disposition.
     #
     # language:: Returns a string or an array of strings giving the body
     #            language value as defined in [LANGUAGE-TAGS].
     #
     # extension:: Returns extension data.
     #
     # multipart?:: Returns false.
     #
     class BodyTypeBasic < Struct.new(:media_type, :subtype,
                                      :param, :content_id,
                                      :description, :encoding, :size,
                                      :md5, :disposition, :language,
                                      :extension)
       def multipart?
         return false
       end
 
       # Obsolete: use +subtype+ instead.  Calling this will
       # generate a warning message to +stderr+, then return
       # the value of +subtype+.
       def media_subtype
         $stderr.printf("warning: media_subtype is obsolete.\n")
         $stderr.printf("         use subtype instead.\n")
         return subtype
       end
     end
 
     # Net::IMAP::BodyTypeText represents TEXT body structures of messages.
     #
     # ==== Fields:
     #
     # lines:: Returns the size of the body in text lines.
     #
     # And Net::IMAP::BodyTypeText has all fields of Net::IMAP::BodyTypeBasic.
     #
     class BodyTypeText < Struct.new(:media_type, :subtype,
                                     :param, :content_id,
                                     :description, :encoding, :size,
                                     :lines,
                                     :md5, :disposition, :language,
                                     :extension)
       def multipart?
         return false
       end
 
       # Obsolete: use +subtype+ instead.  Calling this will
       # generate a warning message to +stderr+, then return
       # the value of +subtype+.
       def media_subtype
         $stderr.printf("warning: media_subtype is obsolete.\n")
         $stderr.printf("         use subtype instead.\n")
         return subtype
       end
     end
 
     # Net::IMAP::BodyTypeMessage represents MESSAGE/RFC822 body structures of messages.
     #
     # ==== Fields:
     #
     # envelope:: Returns a Net::IMAP::Envelope giving the envelope structure.
     #
     # body:: Returns an object giving the body structure.
     #
     # And Net::IMAP::BodyTypeMessage has all methods of Net::IMAP::BodyTypeText.
     #
     class BodyTypeMessage < Struct.new(:media_type, :subtype,
                                        :param, :content_id,
                                        :description, :encoding, :size,
                                        :envelope, :body, :lines,
                                        :md5, :disposition, :language,
                                        :extension)
       def multipart?
         return false
       end
 
       # Obsolete: use +subtype+ instead.  Calling this will
       # generate a warning message to +stderr+, then return
       # the value of +subtype+.
       def media_subtype
         $stderr.printf("warning: media_subtype is obsolete.\n")
         $stderr.printf("         use subtype instead.\n")
         return subtype
       end
     end
 
     # Net::IMAP::BodyTypeMultipart represents multipart body structures
     # of messages.
     #
     # ==== Fields:
     #
     # media_type:: Returns the content media type name as defined in [MIME-IMB].
     #
     # subtype:: Returns the content subtype name as defined in [MIME-IMB].
     #
     # parts:: Returns multiple parts.
     #
     # param:: Returns a hash that represents parameters as defined in [MIME-IMB].
     #
     # disposition:: Returns a Net::IMAP::ContentDisposition object giving
     #               the content disposition.
     #
     # language:: Returns a string or an array of strings giving the body
     #            language value as defined in [LANGUAGE-TAGS].
     #
     # extension:: Returns extension data.
     #
     # multipart?:: Returns true.
     #
     class BodyTypeMultipart < Struct.new(:media_type, :subtype,
                                          :parts,
                                          :param, :disposition, :language,
                                          :extension)
       def multipart?
         return true
       end
 
       # Obsolete: use +subtype+ instead.  Calling this will
       # generate a warning message to +stderr+, then return
       # the value of +subtype+.
       def media_subtype
         $stderr.printf("warning: media_subtype is obsolete.\n")
         $stderr.printf("         use subtype instead.\n")
         return subtype
       end
     end
 
     class ResponseParser # :nodoc:
       def initialize
         @str = nil
         @pos = nil
         @lex_state = nil
         @token = nil
         @flag_symbols = {}
       end
 
       def parse(str)
         @str = str
         @pos = 0
         @lex_state = EXPR_BEG
         @token = nil
         return response
       end
 
       private
 
       EXPR_BEG          = :EXPR_BEG
       EXPR_DATA         = :EXPR_DATA
       EXPR_TEXT         = :EXPR_TEXT
       EXPR_RTEXT        = :EXPR_RTEXT
       EXPR_CTEXT        = :EXPR_CTEXT
 
       T_SPACE   = :SPACE
       T_NIL     = :NIL
       T_NUMBER  = :NUMBER
       T_ATOM    = :ATOM
       T_QUOTED  = :QUOTED
       T_LPAR    = :LPAR
       T_RPAR    = :RPAR
       T_BSLASH  = :BSLASH
       T_STAR    = :STAR
       T_LBRA    = :LBRA
       T_RBRA    = :RBRA
       T_LITERAL = :LITERAL
       T_PLUS    = :PLUS
       T_PERCENT = :PERCENT
       T_CRLF    = :CRLF
       T_EOF     = :EOF
       T_TEXT    = :TEXT
 
       BEG_REGEXP = /\G(?:\
 (?# 1:  SPACE   )( +)|\
 (?# 2:  NIL     )(NIL)(?=[\x80-\xff(){ \x00-\x1f\x7f%*"\\\[\]+])|\
 (?# 3:  NUMBER  )(\d+)(?=[\x80-\xff(){ \x00-\x1f\x7f%*"\\\[\]+])|\
 (?# 4:  ATOM    )([^\x80-\xff(){ \x00-\x1f\x7f%*"\\\[\]+]+)|\
 (?# 5:  QUOTED  )"((?:[^\x00\r\n"\\]|\\["\\])*)"|\
 (?# 6:  LPAR    )(\()|\
 (?# 7:  RPAR    )(\))|\
 (?# 8:  BSLASH  )(\\)|\
 (?# 9:  STAR    )(\*)|\
 (?# 10: LBRA    )(\[)|\
 (?# 11: RBRA    )(\])|\
 (?# 12: LITERAL )\{(\d+)\}\r\n|\
 (?# 13: PLUS    )(\+)|\
 (?# 14: PERCENT )(%)|\
 (?# 15: CRLF    )(\r\n)|\
 (?# 16: EOF     )(\z))/ni
 
       DATA_REGEXP = /\G(?:\
 (?# 1:  SPACE   )( )|\
 (?# 2:  NIL     )(NIL)|\
 (?# 3:  NUMBER  )(\d+)|\
 (?# 4:  QUOTED  )"((?:[^\x00\r\n"\\]|\\["\\])*)"|\
 (?# 5:  LITERAL )\{(\d+)\}\r\n|\
diff --git a/lib/ruby/1.9/shell/process-controller.rb b/lib/ruby/1.9/shell/process-controller.rb
index ac7697dd5d..2198b88d51 100644
--- a/lib/ruby/1.9/shell/process-controller.rb
+++ b/lib/ruby/1.9/shell/process-controller.rb
@@ -1,319 +1,319 @@
 #
 #   shell/process-controller.rb -
 #   	$Release Version: 0.7 $
 #   	$Revision$
 #   	by Keiju ISHITSUKA(keiju@ruby-lang.org)
 #
 # --
 #
 #
 #
 require "forwardable"
 
 require "thread"
 require "sync"
 
 class Shell
   class ProcessController
 
     @ProcessControllers = {}
     @ProcessControllersMonitor = Mutex.new
     @ProcessControllersCV = ConditionVariable.new
 
     @BlockOutputMonitor = Mutex.new
     @BlockOutputCV = ConditionVariable.new
 
-    class<<self
+    class << self
       extend Forwardable
 
       def_delegator("@ProcessControllersMonitor",
 		    "synchronize", "process_controllers_exclusive")
 
       def active_process_controllers
 	process_controllers_exclusive do
 	  @ProcessControllers.dup
 	end
       end
 
       def activate(pc)
 	process_controllers_exclusive do
 	  @ProcessControllers[pc] ||= 0
 	  @ProcessControllers[pc] += 1
 	end
       end
 
       def inactivate(pc)
 	process_controllers_exclusive do
 	  if @ProcessControllers[pc]
 	    if (@ProcessControllers[pc] -= 1) == 0
 	      @ProcessControllers.delete(pc)
 	      @ProcessControllersCV.signal
 	    end
 	  end
 	end
       end
 
       def each_active_object
 	process_controllers_exclusive do
 	  for ref in @ProcessControllers.keys
 	    yield ref
 	  end
 	end
       end
 
       def block_output_synchronize(&b)
 	@BlockOutputMonitor.synchronize(&b)
       end
 
       def wait_to_finish_all_process_controllers
 	process_controllers_exclusive do
 	  while !@ProcessControllers.empty?
 	    Shell::notify("Process finishing, but active shell exists",
 			  "You can use Shell#transact or Shell#check_point for more safe execution.")
 	    if Shell.debug?
 	      for pc in @ProcessControllers.keys
 		Shell::notify(" Not finished jobs in "+pc.shell.to_s)
  		for com in pc.jobs
  		  com.notify("  Jobs: %id")
  		end
 	      end
 	    end
 	    @ProcessControllersCV.wait(@ProcessControllersMonitor)
 	  end
 	end
       end
     end
 
     # for shell-command complete finish at this process exit.
     USING_AT_EXIT_WHEN_PROCESS_EXIT = true
     at_exit do
       wait_to_finish_all_process_controllers unless $@
     end
 
     def initialize(shell)
       @shell = shell
       @waiting_jobs = []
       @active_jobs = []
       @jobs_sync = Sync.new
 
       @job_monitor = Mutex.new
       @job_condition = ConditionVariable.new
     end
 
     attr_reader :shell
 
     def jobs
       jobs = []
       @jobs_sync.synchronize(:SH) do
 	jobs.concat @waiting_jobs
 	jobs.concat @active_jobs
       end
       jobs
     end
 
     def active_jobs
       @active_jobs
     end
 
     def waiting_jobs
       @waiting_jobs
     end
 
     def jobs_exist?
       @jobs_sync.synchronize(:SH) do
 	@active_jobs.empty? or @waiting_jobs.empty?
       end
     end
 
     def active_jobs_exist?
       @jobs_sync.synchronize(:SH) do
 	@active_jobs.empty?
       end
     end
 
     def waiting_jobs_exist?
       @jobs_sync.synchronize(:SH) do
 	@waiting_jobs.empty?
       end
     end
 
     # schedule a command
     def add_schedule(command)
       @jobs_sync.synchronize(:EX) do
 	ProcessController.activate(self)
 	if @active_jobs.empty?
 	  start_job command
 	else
 	  @waiting_jobs.push(command)
 	end
       end
     end
 
     # start a job
     def start_job(command = nil)
       @jobs_sync.synchronize(:EX) do
 	if command
 	  return if command.active?
 	  @waiting_jobs.delete command
 	else
 	  command = @waiting_jobs.shift
 #	  command.notify "job(%id) pre-start.", @shell.debug?
 
 	  return unless command
 	end
 	@active_jobs.push command
 	command.start
 #	command.notify "job(%id) post-start.", @shell.debug?
 
 	# start all jobs that input from the job
 	for job in @waiting_jobs.dup
 	  start_job(job) if job.input == command
 	end
 #	command.notify "job(%id) post2-start.", @shell.debug?
       end
     end
 
     def waiting_job?(job)
       @jobs_sync.synchronize(:SH) do
 	@waiting_jobs.include?(job)
       end
     end
 
     def active_job?(job)
       @jobs_sync.synchronize(:SH) do
 	@active_jobs.include?(job)
       end
     end
 
     # terminate a job
     def terminate_job(command)
       @jobs_sync.synchronize(:EX) do
 	@active_jobs.delete command
 	ProcessController.inactivate(self)
 	if @active_jobs.empty?
 	  command.notify("start_job in terminate_job(%id)", Shell::debug?)
 	  start_job
 	end
       end
     end
 
     # kill a job
     def kill_job(sig, command)
       @jobs_sync.synchronize(:EX) do
 	if @waiting_jobs.delete command
 	  ProcessController.inactivate(self)
 	  return
 	elsif @active_jobs.include?(command)
 	  begin
 	    r = command.kill(sig)
 	    ProcessController.inactivate(self)
 	  rescue
 	    print "Shell: Warn: $!\n" if @shell.verbose?
 	    return nil
 	  end
 	  @active_jobs.delete command
 	  r
 	end
       end
     end
 
     # wait for all jobs to terminate
     def wait_all_jobs_execution
       @job_monitor.synchronize do
 	begin
 	  while !jobs.empty?
 	    @job_condition.wait(@job_monitor)
 	    for job in jobs
 	      job.notify("waiting job(%id)", Shell::debug?)
 	    end
 	  end
 	ensure
 	  redo unless jobs.empty?
 	end
       end
     end
 
     # simple fork
     def sfork(command, &block)
       pipe_me_in, pipe_peer_out = IO.pipe
       pipe_peer_in, pipe_me_out = IO.pipe
 
 
       pid = nil
       pid_mutex = Mutex.new
       pid_cv = ConditionVariable.new
 
       Thread.start do
 	ProcessController.block_output_synchronize do
 	  STDOUT.flush
 	  ProcessController.each_active_object do |pc|
 	    for jobs in pc.active_jobs
 	      jobs.flush
 	    end
 	  end
 
 	  pid = fork {
 	    Thread.list.each do |th|
 #	      th.kill unless [Thread.main, Thread.current].include?(th)
 	      th.kill unless Thread.current == th
 	    end
 
 	    STDIN.reopen(pipe_peer_in)
 	    STDOUT.reopen(pipe_peer_out)
 
 	    ObjectSpace.each_object(IO) do |io|
 	      if ![STDIN, STDOUT, STDERR].include?(io)
 		io.close unless io.closed?
 	      end
 	    end
 
 	    yield
 	  }
 	end
 	pid_cv.signal
 
 	pipe_peer_in.close
 	pipe_peer_out.close
 	command.notify "job(%name:##{pid}) start", @shell.debug?
 
 	begin
 	  _pid = nil
 	  command.notify("job(%id) start to waiting finish.", @shell.debug?)
 	  _pid = Process.waitpid(pid, nil)
 	rescue Errno::ECHILD
 	  command.notify "warn: job(%id) was done already waitpid."
 	  _pid = true
 	  #	rescue
 	  #	  STDERR.puts $!
 	ensure
 	  command.notify("Job(%id): Wait to finish when Process finished.", @shell.debug?)
 	  # when the process ends, wait until the command terminates
 	  if USING_AT_EXIT_WHEN_PROCESS_EXIT or _pid
 	  else
 	    command.notify("notice: Process finishing...",
 			   "wait for Job[%id] to finish.",
 			   "You can use Shell#transact or Shell#check_point for more safe execution.")
 	    redo
 	  end
 
 #	  command.notify "job(%id) pre-pre-finish.", @shell.debug?
 	  @job_monitor.synchronize do
 #	    command.notify "job(%id) pre-finish.", @shell.debug?
 	    terminate_job(command)
 #	    command.notify "job(%id) pre-finish2.", @shell.debug?
 	    @job_condition.signal
 	    command.notify "job(%id) finish.", @shell.debug?
 	  end
 	end
       end
 
       pid_mutex.synchronize do
 	while !pid
 	  pid_cv.wait(pid_mutex)
 	end
       end
 
       return pid, pipe_me_in, pipe_me_out
     end
   end
 end
diff --git a/lib/ruby/1.9/sync.rb b/lib/ruby/1.9/sync.rb
index 459bba732b..b97d36eb43 100644
--- a/lib/ruby/1.9/sync.rb
+++ b/lib/ruby/1.9/sync.rb
@@ -1,307 +1,307 @@
 #
 #   sync.rb - 2 phase lock with counter
 #   	$Release Version: 1.0$
 #   	$Revision$
 #   	by Keiju ISHITSUKA(keiju@ishitsuka.com)
 #
 # --
 #  Sync_m, Synchronizer_m
 #  Usage:
 #   obj.extend(Sync_m)
 #   or
 #   class Foo
 #	include Sync_m
 #	:
 #   end
 #
 #   Sync_m#sync_mode
 #   Sync_m#sync_locked?, locked?
 #   Sync_m#sync_shared?, shared?
 #   Sync_m#sync_exclusive?, sync_exclusive?
 #   Sync_m#sync_try_lock, try_lock
 #   Sync_m#sync_lock, lock
 #   Sync_m#sync_unlock, unlock
 #
 #  Sync, Synchronizer:
 #  Usage:
 #   sync = Sync.new
 #
 #   Sync#mode
 #   Sync#locked?
 #   Sync#shared?
 #   Sync#exclusive?
 #   Sync#try_lock(mode) -- mode = :EX, :SH, :UN
 #   Sync#lock(mode)     -- mode = :EX, :SH, :UN
 #   Sync#unlock
 #   Sync#synchronize(mode) {...}
 #
 #
 
 unless defined? Thread
   raise "Thread not available for this ruby interpreter"
 end
 
 module Sync_m
   RCS_ID='-$Id$-'
 
   # lock mode
   UN = :UN
   SH = :SH
   EX = :EX
 
   # exceptions
   class Err < StandardError
     def Err.Fail(*opt)
       fail self, sprintf(self::Message, *opt)
     end
 
     class UnknownLocker < Err
       Message = "Thread(%s) not locked."
       def UnknownLocker.Fail(th)
 	super(th.inspect)
       end
     end
 
     class LockModeFailer < Err
       Message = "Unknown lock mode(%s)"
       def LockModeFailer.Fail(mode)
 	if mode.id2name
 	  mode = id2name
 	end
 	super(mode)
       end
     end
   end
 
   def Sync_m.define_aliases(cl)
     cl.module_eval %q{
       alias locked? sync_locked?
       alias shared? sync_shared?
       alias exclusive? sync_exclusive?
       alias lock sync_lock
       alias unlock sync_unlock
       alias try_lock sync_try_lock
       alias synchronize sync_synchronize
     }
   end
 
   def Sync_m.append_features(cl)
     super
     # do nothing for Modules
     # make aliases for Classes.
     define_aliases(cl) unless cl.instance_of?(Module)
     self
   end
 
   def Sync_m.extend_object(obj)
     super
     obj.sync_extend
   end
 
   def sync_extend
     unless (defined? locked? and
 	    defined? shared? and
 	    defined? exclusive? and
 	    defined? lock and
 	    defined? unlock and
 	    defined? try_lock and
 	    defined? synchronize)
-      Sync_m.define_aliases(class<<self;self;end)
+      Sync_m.define_aliases(singleton_class)
     end
     sync_initialize
   end
 
   # accessing
   def sync_locked?
     sync_mode != UN
   end
 
   def sync_shared?
     sync_mode == SH
   end
 
   def sync_exclusive?
     sync_mode == EX
   end
 
   # locking methods.
   def sync_try_lock(mode = EX)
     return unlock if mode == UN
     @sync_mutex.synchronize do
       ret = sync_try_lock_sub(mode)
     end
     ret
   end
 
   def sync_lock(m = EX)
     return unlock if m == UN
 
     while true
       @sync_mutex.synchronize do
 	if sync_try_lock_sub(m)
 	  return self
 	else
 	  if sync_sh_locker[Thread.current]
 	    sync_upgrade_waiting.push [Thread.current, sync_sh_locker[Thread.current]]
 	    sync_sh_locker.delete(Thread.current)
 	  else
 	    sync_waiting.push Thread.current
 	  end
 	  @sync_mutex.sleep
 	end
       end
     end
     self
   end
 
   def sync_unlock(m = EX)
     wakeup_threads = []
     @sync_mutex.synchronize do
       if sync_mode == UN
 	Err::UnknownLocker.Fail(Thread.current)
       end
 
       m = sync_mode if m == EX and sync_mode == SH
 
       runnable = false
       case m
       when UN
 	Err::UnknownLocker.Fail(Thread.current)
 
       when EX
 	if sync_ex_locker == Thread.current
 	  if (self.sync_ex_count = sync_ex_count - 1) == 0
 	    self.sync_ex_locker = nil
 	    if sync_sh_locker.include?(Thread.current)
 	      self.sync_mode = SH
 	    else
 	      self.sync_mode = UN
 	    end
 	    runnable = true
 	  end
 	else
 	  Err::UnknownLocker.Fail(Thread.current)
 	end
 
       when SH
 	if (count = sync_sh_locker[Thread.current]).nil?
 	  Err::UnknownLocker.Fail(Thread.current)
 	else
 	  if (sync_sh_locker[Thread.current] = count - 1) == 0
 	    sync_sh_locker.delete(Thread.current)
 	    if sync_sh_locker.empty? and sync_ex_count == 0
 	      self.sync_mode = UN
 	      runnable = true
 	    end
 	  end
 	end
       end
 
       if runnable
 	if sync_upgrade_waiting.size > 0
 	  th, count = sync_upgrade_waiting.shift
 	  sync_sh_locker[th] = count
 	  th.wakeup
 	  wakeup_threads.push th
 	else
 	  wait = sync_waiting
 	  self.sync_waiting = []
 	  for th in wait
 	    th.wakeup
 	    wakeup_threads.push th
 	  end
 	end
       end
     end
     for th in wakeup_threads
       th.run
     end
     self
   end
 
   def sync_synchronize(mode = EX)
     sync_lock(mode)
     begin
       yield
     ensure
       sync_unlock
     end
   end
 
   attr_accessor :sync_mode
 
   attr_accessor :sync_waiting
   attr_accessor :sync_upgrade_waiting
   attr_accessor :sync_sh_locker
   attr_accessor :sync_ex_locker
   attr_accessor :sync_ex_count
 
   def sync_inspect
     sync_iv = instance_variables.select{|iv| /^@sync_/ =~ iv.id2name}.collect{|iv| iv.id2name + '=' + instance_eval(iv.id2name).inspect}.join(",")
     print "<#{self.class}.extend Sync_m: #{inspect}, <Sync_m: #{sync_iv}>"
   end
 
   private
 
   def sync_initialize
     @sync_mode = UN
     @sync_waiting = []
     @sync_upgrade_waiting = []
     @sync_sh_locker = Hash.new
     @sync_ex_locker = nil
     @sync_ex_count = 0
 
     @sync_mutex = Mutex.new
   end
 
   def initialize(*args)
     super
     sync_initialize
   end
 
   def sync_try_lock_sub(m)
     case m
     when SH
       case sync_mode
       when UN
 	self.sync_mode = m
 	sync_sh_locker[Thread.current] = 1
 	ret = true
       when SH
 	count = 0 unless count = sync_sh_locker[Thread.current]
 	sync_sh_locker[Thread.current] = count + 1
 	ret = true
       when EX
 	# in EX mode, lock will upgrade to EX lock
 	if sync_ex_locker == Thread.current
 	  self.sync_ex_count = sync_ex_count + 1
 	  ret = true
 	else
 	  ret = false
 	end
       end
     when EX
       if sync_mode == UN or
 	  sync_mode == SH && sync_sh_locker.size == 1 && sync_sh_locker.include?(Thread.current)
 	self.sync_mode = m
 	self.sync_ex_locker = Thread.current
 	self.sync_ex_count = 1
 	ret = true
       elsif sync_mode == EX && sync_ex_locker == Thread.current
 	self.sync_ex_count = sync_ex_count + 1
 	ret = true
       else
 	ret = false
       end
     else
       Err::LockModeFailer.Fail mode
     end
     return ret
   end
 end
 Synchronizer_m = Sync_m
 
 class Sync
   include Sync_m
 end
 Synchronizer = Sync
diff --git a/lib/ruby/1.9/uri/mailto.rb b/lib/ruby/1.9/uri/mailto.rb
index 240c849e9a..3edaac93f3 100644
--- a/lib/ruby/1.9/uri/mailto.rb
+++ b/lib/ruby/1.9/uri/mailto.rb
@@ -1,266 +1,266 @@
 #
 # = uri/mailto.rb
 #
 # Author:: Akira Yamada <akira@ruby-lang.org>
 # License:: You can redistribute it and/or modify it under the same term as Ruby.
 # Revision:: $Id$
 #
 
 require 'uri/generic'
 
 module URI
 
   #
   # RFC2368, The mailto URL scheme
   #
   class MailTo < Generic
     include REGEXP
 
     DEFAULT_PORT = nil
 
     COMPONENT = [ :scheme, :to, :headers ].freeze
 
     # :stopdoc:
     #  "hname" and "hvalue" are encodings of an RFC 822 header name and
     #  value, respectively. As with "to", all URL reserved characters must
     #  be encoded.
     #
     #  "#mailbox" is as specified in RFC 822 [RFC822]. This means that it
     #  consists of zero or more comma-separated mail addresses, possibly
     #  including "phrase" and "comment" components. Note that all URL
     #  reserved characters in "to" must be encoded: in particular,
     #  parentheses, commas, and the percent sign ("%"), which commonly occur
     #  in the "mailbox" syntax.
     #
     #  Within mailto URLs, the characters "?", "=", "&" are reserved.
 
     # hname      =  *urlc
     # hvalue     =  *urlc
     # header     =  hname "=" hvalue
     HEADER_PATTERN = "(?:[^?=&]*=[^?=&]*)".freeze
-    HEADER_REGEXP  = Regexp.new(HEADER_PATTERN, 'N').freeze
+    HEADER_REGEXP  = Regexp.new(HEADER_PATTERN).freeze
     # headers    =  "?" header *( "&" header )
     # to         =  #mailbox
     # mailtoURL  =  "mailto:" [ to ] [ headers ]
     MAILBOX_PATTERN = "(?:#{PATTERN::ESCAPED}|[^(),%?=&])".freeze
     MAILTO_REGEXP = Regexp.new(" # :nodoc:
       \\A
       (#{MAILBOX_PATTERN}*?)                          (?# 1: to)
       (?:
         \\?
         (#{HEADER_PATTERN}(?:\\&#{HEADER_PATTERN})*)  (?# 2: headers)
       )?
       (?:
         \\#
         (#{PATTERN::FRAGMENT})                        (?# 3: fragment)
       )?
       \\z
     ", Regexp::EXTENDED).freeze
     # :startdoc:
 
     #
     # == Description
     #
     # Creates a new URI::MailTo object from components, with syntax checking.
     #
     # Components can be provided as an Array or Hash. If an Array is used,
     # the components must be supplied as [to, headers].
     #
     # If a Hash is used, the keys are the component names preceded by colons.
     #
     # The headers can be supplied as a pre-encoded string, such as
     # "subject=subscribe&cc=address", or as an Array of Arrays like
     # [['subject', 'subscribe'], ['cc', 'address']]
     #
     # Examples:
     #
     #    require 'uri'
     #
     #    m1 = URI::MailTo.build(['joe@example.com', 'subject=Ruby'])
     #    puts m1.to_s  ->  mailto:joe@example.com?subject=Ruby
     #
     #    m2 = URI::MailTo.build(['john@example.com', [['Subject', 'Ruby'], ['Cc', 'jack@example.com']]])
     #    puts m2.to_s  ->  mailto:john@example.com?Subject=Ruby&Cc=jack@example.com
     #
     #    m3 = URI::MailTo.build({:to => 'listman@example.com', :headers => [['subject', 'subscribe']]})
     #    puts m3.to_s  ->  mailto:listman@example.com?subject=subscribe
     #
     def self.build(args)
       tmp = Util::make_components_hash(self, args)
 
       if tmp[:to]
         tmp[:opaque] = tmp[:to]
       else
         tmp[:opaque] = ''
       end
 
       if tmp[:headers]
         tmp[:opaque] << '?'
 
         if tmp[:headers].kind_of?(Array)
           tmp[:opaque] << tmp[:headers].collect { |x|
             if x.kind_of?(Array)
               x[0] + '=' + x[1..-1].to_s
             else
               x.to_s
             end
           }.join('&')
 
         elsif tmp[:headers].kind_of?(Hash)
           tmp[:opaque] << tmp[:headers].collect { |h,v|
             h + '=' + v
           }.join('&')
 
         else
           tmp[:opaque] << tmp[:headers].to_s
         end
       end
 
       return super(tmp)
     end
 
     #
     # == Description
     #
     # Creates a new URI::MailTo object from generic URL components with
     # no syntax checking.
     #
     # This method is usually called from URI::parse, which checks
     # the validity of each component.
     #
     def initialize(*arg)
       super(*arg)
 
       @to = nil
       @headers = []
 
       if MAILTO_REGEXP =~ @opaque
         if arg[-1]
           self.to = $1
           self.headers = $2
         else
           set_to($1)
           set_headers($2)
         end
 
       else
         raise InvalidComponentError,
           "unrecognised opaque part for mailtoURL: #{@opaque}"
       end
     end
 
     # The primary e-mail address of the URL, as a String
     attr_reader :to
 
     # E-mail headers set by the URL, as an Array of Arrays
     attr_reader :headers
 
     def check_to(v)
       return true unless v
       return true if v.size == 0
 
       if parser.regexp[:OPAQUE] !~ v || /\A#{MAILBOX_PATTERN}*\z/o !~ v
         raise InvalidComponentError,
           "bad component(expected opaque component): #{v}"
       end
 
       return true
     end
     private :check_to
 
     def set_to(v)
       @to = v
     end
     protected :set_to
 
     def to=(v)
       check_to(v)
       set_to(v)
       v
     end
 
     def check_headers(v)
       return true unless v
       return true if v.size == 0
 
       if parser.regexp[:OPAQUE] !~ v ||
           /\A(#{HEADER_PATTERN}(?:\&#{HEADER_PATTERN})*)\z/o !~ v
         raise InvalidComponentError,
           "bad component(expected opaque component): #{v}"
       end
 
       return true
     end
     private :check_headers
 
     def set_headers(v)
       @headers = []
       if v
         v.scan(HEADER_REGEXP) do |x|
           @headers << x.split(/=/o, 2)
         end
       end
     end
     protected :set_headers
 
     def headers=(v)
       check_headers(v)
       set_headers(v)
       v
     end
 
     def to_s
       @scheme + ':' +
         if @to
           @to
         else
           ''
         end +
         if @headers.size > 0
           '?' + @headers.collect{|x| x.join('=')}.join('&')
         else
           ''
         end +
         if @fragment
           '#' + @fragment
         else
           ''
         end
     end
 
     # Returns the RFC822 e-mail text equivalent of the URL, as a String.
     #
     # Example:
     #
     #   require 'uri'
     #
     #   uri = URI.parse("mailto:ruby-list@ruby-lang.org?Subject=subscribe&cc=myaddr")
     #   uri.to_mailtext
     #   # => "To: ruby-list@ruby-lang.org\nSubject: subscribe\nCc: myaddr\n\n\n"
     #
     def to_mailtext
       to = parser.unescape(@to)
       head = ''
       body = ''
       @headers.each do |x|
         case x[0]
         when 'body'
           body = parser.unescape(x[1])
         when 'to'
           to << ', ' + parser.unescape(x[1])
         else
           head << parser.unescape(x[0]).capitalize + ': ' +
             parser.unescape(x[1])  + "\n"
         end
       end
 
       return "To: #{to}
 #{head}
 #{body}
 "
     end
     alias to_rfc822text to_mailtext
   end
 
   @@schemes['MAILTO'] = MailTo
 end
diff --git a/spec/tags/1.9/ruby/core/kernel/comparison_tags.txt b/spec/tags/1.9/ruby/core/kernel/comparison_tags.txt
deleted file mode 100644
index 78b7c977a3..0000000000
--- a/spec/tags/1.9/ruby/core/kernel/comparison_tags.txt
+++ /dev/null
@@ -1,5 +0,0 @@
-fails:Kernel#<=> returns 0 if self
-fails:Kernel#<=> returns 0 if self is == to the argument
-fails:Kernel#<=> returns nil if self is eql? but not == to the argument
-fails:Kernel#<=> returns nil if self.==(arg) returns nil
-fails:Kernel#<=> returns nil if self is not == to the argument
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index 7615b3adef..15488a0718 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1364 +1,1373 @@
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
  * Copyright (C) 2007 MenTaLguY <mental@rydia.net>
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
 package org.jruby;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.util.HashSet;
 import java.util.List;
 
 import java.util.Set;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.IdUtil;
 import org.jruby.runtime.marshal.DataType;
 
 /**
  * RubyObject is the only implementation of the
  * {@link org.jruby.runtime.builtin.IRubyObject}. Every Ruby object in JRuby
  * is represented by something that is an instance of RubyObject. In
  * some of the core class implementations, this means doing a subclass
  * that extends RubyObject, in other cases it means using a simple
  * RubyObject instance and the data field to store specific
  * information about the Ruby object.
  *
  * Some care has been taken to make the implementation be as
  * monomorphic as possible, so that the Java Hotspot engine can
  * improve performance of it. That is the reason for several patterns
  * that might seem odd in this class.
  *
  * The IRubyObject interface used to have lots of methods for
  * different things, but these have now mostly been refactored into
  * several interfaces that gives access to that specific part of the
  * object. This gives us the possibility to switch out that subsystem
  * without changing interfaces again. For example, instance variable
  * and internal variables are handled this way, but the implementation
  * in RubyObject only returns "this" in {@link #getInstanceVariables()} and
  * {@link #getInternalVariables()}.
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Object", include="Kernel")
 public class RubyObject extends RubyBasicObject {
     // Equivalent of T_DATA
     public static class Data extends RubyObject implements DataType {
         public Data(Ruby runtime, RubyClass metaClass, Object data) {
             super(runtime, metaClass);
             dataWrapStruct(data);
         }
 
         public Data(RubyClass metaClass, Object data) {
             super(metaClass);
             dataWrapStruct(data);
         }
     }
 
     /**
      * Standard path for object creation. Objects are entered into ObjectSpace
      * only if ObjectSpace is enabled.
      */
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         super(runtime, metaClass);
     }
 
     /**
      * Path for objects that don't taint and don't enter objectspace.
      */
     public RubyObject(RubyClass metaClass) {
         super(metaClass);
     }
 
     /**
      * Path for objects who want to decide whether they don't want to be in
      * ObjectSpace even when it is on. (notably used by objects being
      * considered immediate, they'll always pass false here)
      */
     protected RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace, boolean canBeTainted) {
         super(runtime, metaClass, useObjectSpace, canBeTainted);
     }
 
     protected RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         super(runtime, metaClass, useObjectSpace);
     }
 
     /**
      * Will create the Ruby class Object in the runtime
      * specified. This method needs to take the actual class as an
      * argument because of the Object class' central part in runtime
      * initialization.
      */
     public static RubyClass createObjectClass(Ruby runtime, RubyClass objectClass) {
         objectClass.index = ClassIndex.OBJECT;
         objectClass.setReifiedClass(RubyObject.class);
 
         objectClass.defineAnnotatedMethods(ObjectMethods.class);
 
         return objectClass;
     }
 
     /**
      * Interestingly, the Object class doesn't really have that many
      * methods for itself. Instead almost all of the Object methods
      * are really defined on the Kernel module. This class is a holder
      * for all Object methods.
      *
      * @see RubyKernel
      */
     public static class ObjectMethods {
         @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, compat = CompatVersion.RUBY1_8)
         public static IRubyObject initialize(IRubyObject self) {
             return self.getRuntime().getNil();
         }
 
         @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE, rest = true, compat = CompatVersion.RUBY1_9)
         public static IRubyObject initialize19(IRubyObject self, IRubyObject[] args) {
             return self.getRuntime().getNil();
         }
     }
 
     /**
      * Default allocator instance for all Ruby objects. The only
      * reason to not use this allocator is if you actually need to
      * have all instances of something be a subclass of RubyObject.
      *
      * @see org.jruby.runtime.ObjectAllocator
      */
     public static final ObjectAllocator OBJECT_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyObject(runtime, klass);
         }
     };
 
     /**
      * Will make sure that this object is added to the current object
      * space.
      *
      * @see org.jruby.runtime.ObjectSpace
      */
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
 
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      *
      * Will generally return a value from org.jruby.runtime.ClassIndex
      *
      * @see org.jruby.runtime.ClassInde
      */
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
 
     /**
      * Simple helper to print any objects.
      */
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     @Override
     public boolean equals(Object other) {
         return other == this ||
                 other instanceof IRubyObject &&
                 callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     /**
      * The default toString method is just a wrapper that calls the
      * Ruby "to_s" method.
      */
     @Override
     public String toString() {
         RubyString rubyString = RuntimeHelpers.invoke(getRuntime().getCurrentContext(), this, "to_s").convertToString();
         return rubyString.getUnicodeValue();
     }
 
     /**
      * The actual method that checks frozen with the default frozen message from MRI.
      * If possible, call this instead of {@link #testFrozen}.
      */
    protected void checkFrozen() {
        testFrozen();
    }
 
     /** init_copy
      *
      * Initializes a copy with variable and special instance variable
      * information, and then call the initialize_copy Ruby method.
      */
     private static void initCopy(IRubyObject clone, RubyObject original) {
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         original.copySpecialInstanceVariables(clone);
 
         if (original.hasVariables()) clone.syncVariables(original.getVariableList());
         if (original instanceof RubyModule) ((RubyModule) clone).syncConstants((RubyModule) original);
 
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /**
      * Call the Ruby initialize method with the supplied arguments and block.
      */
     public final void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /**
      * Tries to convert this object to the specified Ruby type, using
      * a specific conversion method.
      */
     @Deprecated
     public final IRubyObject convertToType(RubyClass target, int convertMethodIndex) {
         throw new RuntimeException("Not supported; use the String versions");
     }
 
     /** specific_eval
      *
      * Evaluates the block or string inside of the context of this
      * object, using the supplied arguments. If a block is given, this
      * will be yielded in the specific context of this object. If no
      * block is given then a String-like object needs to be the first
      * argument, and this string will be evaluated. Second and third
      * arguments in the args-array is optional, but can contain the
      * filename and line of the string under evaluation.
      */
     @Deprecated
     public IRubyObject specificEval(ThreadContext context, RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(context, mod, block);
         }
 
         if (args.length == 0) {
             throw getRuntime().newArgumentError("block not supplied");
         } else if (args.length > 3) {
             String lastFuncName = context.getFrameName();
             throw getRuntime().newArgumentError(
                 "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
         }
 
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         RubyString evalStr;
         if (args[0] instanceof RubyString) {
             evalStr = (RubyString)args[0];
         } else {
             evalStr = args[0].convertToString();
         }
 
         String file;
         int line;
         if (args.length > 1) {
             file = args[1].convertToString().asJavaString();
             if (args.length > 2) {
                 line = (int)(args[2].convertToInteger().getLongValue() - 1);
             } else {
                 line = 0;
             }
         } else {
             file = "(eval)";
             line = 0;
         }
 
         return evalUnder(context, mod, evalStr, file, line);
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      * Will by default use identity equality to compare objects. This
      * follows the Ruby semantics.
      */
     @JRubyMethod(name = "==", required = 1, compat = CompatVersion.RUBY1_8)
     @Override
     public IRubyObject op_equal(ThreadContext context, IRubyObject obj) {
         return super.op_equal_19(context, obj);
     }
 
     /** rb_obj_equal
      *
      * Will use Java identity equality.
      */
     @JRubyMethod(name = "equal?", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject equal_p(ThreadContext context, IRubyObject obj) {
         return this == obj ? context.getRuntime().getTrue() : context.getRuntime().getFalse();
     }
 
     /** rb_obj_equal
      *
      * Just like "==" and "equal?", "eql?" will use identity equality for Object.
      */
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_equal
      *
      * The Ruby "===" method is used by default in case/when
      * statements. The Object implementation first checks Java identity
      * equality and then calls the "==" method too.
      */
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return context.getRuntime().newBoolean(equalInternal(context, this, other));
     }
 
+    @JRubyMethod(name = "<=>", required = 1, compat = CompatVersion.RUBY1_9)
+    public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
+        Ruby runtime = context.getRuntime();
+        if (this == other || this.callMethod(context, "==", other).isTrue()){
+            return RubyFixnum.zero(runtime);
+        }
+        return runtime.getNil();
+    }
+
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "==" method.
      */
     protected static boolean equalInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that == other || that.callMethod(context, "==", other).isTrue();
     }
 
     /**
      * Helper method for checking equality, first using Java identity
      * equality, and then calling the "eql?" method.
      */
     protected static boolean eqlInternal(final ThreadContext context, final IRubyObject that, final IRubyObject other){
         return that.callMethod(context, "eql?", other).isTrue();
     }
 
     /** rb_obj_init_copy
      *
      * Initializes this object as a copy of the original, that is the
      * parameter to this object. Will make sure that the argument
      * actually has the same real class as this object. It shouldn't
      * be possible to initialize an object with something totally
      * different.
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
     public IRubyObject initialize_copy(IRubyObject original) {
         if (this == original) {
             return this;
         }
         checkFrozen();
 
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
             throw getRuntime().newTypeError("initialize_copy should take same class object");
         }
 
         return this;
     }
 
     /** obj_respond_to
      *
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      *
      * !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
      *
      * Going back to splitting according to method arity. MRI is wrong
      * about most of these anyway, and since we have arity splitting
      * in both the compiler and the interpreter, the performance
      * benefit is important for this method.
      */
     @JRubyMethod(name = "respond_to?")
     public RubyBoolean respond_to_p(IRubyObject mname) {
         String name = mname.asJavaString();
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, true));
     }
 
     /** obj_respond_to
      *
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      *
      * !!! For some reason MRI shows the arity of respond_to? as -1, when it should be -2; that's why this is rest instead of required, optional = 1
      *
      * Going back to splitting according to method arity. MRI is wrong
      * about most of these anyway, and since we have arity splitting
      * in both the compiler and the interpreter, the performance
      * benefit is important for this method.
      */
     @JRubyMethod(name = "respond_to?")
     public RubyBoolean respond_to_p(IRubyObject mname, IRubyObject includePrivate) {
         String name = mname.asJavaString();
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate.isTrue()));
     }
 
     /** rb_obj_id
      *
      * Return the internal id of an object.
      *
      * FIXME: Should this be renamed to match its ruby name?
      */
      @JRubyMethod(name = {"object_id", "__id__"})
      @Override
      public IRubyObject id() {
         return super.id();
      }
 
     /** rb_obj_id_obsolete
      *
      * Old id version. This one is bound to the "id" name and will emit a deprecation warning.
      */
     @JRubyMethod(name = "id")
     public IRubyObject id_deprecated() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#id will be deprecated; use Object#object_id", "Object#id", "Object#object_id");
         return id();
     }
 
     /** rb_obj_id
      *
      * Will return the hash code of this object. In comparison to MRI,
      * this method will use the Java identity hash code instead of
      * using rb_obj_id, since the usage of id in JRuby will incur the
      * cost of some. ObjectSpace maintenance.
      */
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(super.hashCode());
     }
 
     /**
      * Override the Object#hashCode method to make sure that the Ruby
      * hash is actually used as the hashcode for Ruby objects. If the
      * Ruby "hash" method doesn't return a number, the Object#hashCode
      * implementation will be used instead.
      */
     @Override
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), "hash");
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue);
         return nonFixnumHashCode(hashValue);
     }
 
     private int nonFixnumHashCode(IRubyObject hashValue) {
         Ruby runtime = getRuntime();
         if (runtime.is1_9()) {
             return (int)hashValue.convertToInteger().getLongValue();
         } else {
             hashValue = hashValue.callMethod(runtime.getCurrentContext(), "%", RubyFixnum.newFixnum(runtime, 536870923L));
             if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue);
             return System.identityHashCode(hashValue);
         }
     }
 
     /** rb_obj_class
      *
      * Returns the real class of this object, excluding any
      * singleton/meta class in the inheritance chain.
      */
     @JRubyMethod(name = "class")
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     /** rb_obj_type
      *
      * The deprecated version of type, that emits a deprecation
      * warning.
      */
     @JRubyMethod(name = "type")
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "Object#type is deprecated; use Object#class", "Object#type", "Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *
      * This method should be overridden only by: Proc, Method,
      * UnboundedMethod, Binding. It will use the defined allocated of
      * the object, then clone the singleton class, taint the object,
      * call initCopy and then copy frozen state.
      */
     @JRubyMethod(name = "clone", frame = true)
     @Override
     public IRubyObject rbClone() {
         return super.rbClone();
     }
 
     /** rb_obj_dup
      *
      * This method should be overridden only by: Proc
      *
      * Will allocate a new instance of the real class of this object,
      * and then initialize that copy. It's different from {@link
      * #rbClone} in that it doesn't copy the singleton class.
      */
     @JRubyMethod(name = "dup")
     @Override
     public IRubyObject dup() {
         return super.dup();
     }
 
     /** rb_obj_display
      *
      *  call-seq:
      *     obj.display(port=$>)    => nil
      *
      *  Prints <i>obj</i> on the given port (default <code>$></code>).
      *  Equivalent to:
      *
      *     def display(port=$>)
      *       port.write self
      *     end
      *
      *  For example:
      *
      *     1.display
      *     "cat".display
      *     [ 4, 5, 6 ].display
      *     puts
      *
      *  <em>produces:</em>
      *
      *     1cat456
      *
      */
     @JRubyMethod(name = "display", optional = 1)
     public IRubyObject display(ThreadContext context, IRubyObject[] args) {
         IRubyObject port = args.length == 0 ? context.getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(context, "write", this);
 
         return context.getRuntime().getNil();
     }
 
     /** rb_obj_tainted
      *
      *  call-seq:
      *     obj.tainted?    => true or false
      *
      *  Returns <code>true</code> if the object is tainted.
      *
      */
     @JRubyMethod(name = "tainted?")
     public RubyBoolean tainted_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      *  call-seq:
      *     obj.taint -> obj
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
             testFrozen();
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
         Ruby runtime = context.getRuntime();
         if ((flags & FROZEN_F) == 0 && (runtime.is1_9() || !isImmediate())) {
             if (runtime.getSafeLevel() >= 4 && !isTaint()) throw runtime.newSecurityError("Insecure: can't freeze object");
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
 
     /** rb_obj_untrusted
      *  call-seq:
      *     obj.untrusted?    => true or false
      *
      *  Returns <code>true</code> if the object is untrusted.
      */
     @JRubyMethod(name = "untrusted?", compat = CompatVersion.RUBY1_9)
     public RubyBoolean untrusted_p(ThreadContext context) {
         return context.getRuntime().newBoolean(isUntrusted());
     }
 
     /** rb_obj_untrust
      *  call-seq:
      *     obj.untrust -> obj
      *
      *  Marks <i>obj</i> as untrusted.
      */
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject untrust(ThreadContext context) {
         if (!isUntrusted() && !isImmediate()) {
             checkFrozen();
             flags |= UNTRUSTED_F;
         }
         return this;
     }
 
     /** rb_obj_trust
      *  call-seq:
      *     obj.trust    => obj
      *
      *  Removes the untrusted mark from <i>obj</i>.
      */
     @JRubyMethod(compat = CompatVersion.RUBY1_9)
     public IRubyObject trust(ThreadContext context) {
         if (isUntrusted() && !isImmediate()) {
             checkFrozen();
             flags &= ~UNTRUSTED_F;
         }
         return this;
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
     @Override
     public IRubyObject inspect() {
         return super.inspect();
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
     @JRubyMethod(name = "methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject methods(ThreadContext context, IRubyObject[] args) {
         return methods(context, args, false);
     }
     @JRubyMethod(name = "methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject methods19(ThreadContext context, IRubyObject[] args) {
         return methods(context, args, true);
     }
 
     public IRubyObject methods(ThreadContext context, IRubyObject[] args, boolean useSymbols) {
         boolean all = args.length == 1 ? args[0].isTrue() : true;
         Ruby runtime = getRuntime();
         RubyArray methods = runtime.newArray();
         Set<String> seen = new HashSet<String>();
 
         if (getMetaClass().isSingleton()) {
             getMetaClass().populateInstanceMethodNames(seen, methods, Visibility.PRIVATE, true, useSymbols, false);
             if (all) {
                 getMetaClass().getSuperClass().populateInstanceMethodNames(seen, methods, Visibility.PRIVATE, true, useSymbols, true);
             }
         } else if (all) {
             getMetaClass().populateInstanceMethodNames(seen, methods, Visibility.PRIVATE, true, useSymbols, true);
         } else {
             // do nothing, leave empty
         }
 
         return methods;
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
     @JRubyMethod(name = "public_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject public_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().public_instance_methods(trueIfNoArgument(context, args));
     }
 
     @JRubyMethod(name = "public_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject public_methods19(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().public_instance_methods19(trueIfNoArgument(context, args));
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
     @JRubyMethod(name = "protected_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject protected_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().protected_instance_methods(trueIfNoArgument(context, args));
     }
 
     @JRubyMethod(name = "protected_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject protected_methods19(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().protected_instance_methods19(trueIfNoArgument(context, args));
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
     @JRubyMethod(name = "private_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject private_methods(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().private_instance_methods(trueIfNoArgument(context, args));
     }
 
     @JRubyMethod(name = "private_methods", optional = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject private_methods19(ThreadContext context, IRubyObject[] args) {
         return getMetaClass().private_instance_methods19(trueIfNoArgument(context, args));
     }
 
     // FIXME: If true array is common enough we should pre-allocate and stick somewhere
     private IRubyObject[] trueIfNoArgument(ThreadContext context, IRubyObject[] args) {
         return args.length == 0 ? new IRubyObject[] { context.getRuntime().getTrue() } : args;
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
     @JRubyMethod(name = "singleton_methods", optional = 1, compat = CompatVersion.RUBY1_8)
     public RubyArray singleton_methods(ThreadContext context, IRubyObject[] args) {
         return singletonMethods(context, args, false);
     }
 
     @JRubyMethod(name = "singleton_methods", optional = 1 , compat = CompatVersion.RUBY1_9)
     public RubyArray singleton_methods19(ThreadContext context, IRubyObject[] args) {
         return singletonMethods(context, args, true);
     }
 
     public RubyArray singletonMethods(ThreadContext context, IRubyObject[] args, boolean asSymbols) {
         boolean all = true;
         if(args.length == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray singletonMethods;
         if (getMetaClass().isSingleton()) {
             if (asSymbols) {
                 singletonMethods = getMetaClass().instance_methods19(new IRubyObject[]{context.getRuntime().getFalse()});
             } else {
                 singletonMethods = getMetaClass().instance_methods(new IRubyObject[]{context.getRuntime().getFalse()});
             }
             if (all) {
                 RubyClass superClass = getMetaClass().getSuperClass();
                 while (superClass.isSingleton() || superClass.isIncluded()) {
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
     @JRubyMethod(name = "to_a", visibility = Visibility.PUBLIC, compat = CompatVersion.RUBY1_8)
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
     @JRubyMethod(name = "instance_eval", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject instance_eval(ThreadContext context, Block block) {
         return specificEval(context, getInstanceEvalClass(), block);
     }
     @JRubyMethod(name = "instance_eval", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, block);
     }
     @JRubyMethod(name = "instance_eval", frame = true, compat = CompatVersion.RUBY1_8)
     public IRubyObject instance_eval(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return specificEval(context, getInstanceEvalClass(), arg0, arg1, block);
     }
     @JRubyMethod(name = "instance_eval", frame = true, compat = CompatVersion.RUBY1_8)
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
     @JRubyMethod(name = "instance_exec", optional = 3, rest = true, frame = true, compat = CompatVersion.RUBY1_8)
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
     @JRubyMethod(name = {"send", "__send__"}, compat = CompatVersion.RUBY1_8)
     public IRubyObject send(ThreadContext context, Block block) {
         throw context.getRuntime().newArgumentError(0, 1);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = CompatVersion.RUBY1_8)
     public IRubyObject send(ThreadContext context, IRubyObject arg0, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = CompatVersion.RUBY1_8)
     public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, arg1, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, compat = CompatVersion.RUBY1_8)
     public IRubyObject send(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         String name = arg0.asJavaString();
 
         return getMetaClass().finvoke(context, this, name, arg1, arg2, block);
     }
     @JRubyMethod(name = {"send", "__send__"}, rest = true, compat = CompatVersion.RUBY1_8)
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
 
     @JRubyMethod(name = {"send"}, compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject send19(ThreadContext context, Block block) {
         return super.send19(context, block);
     }
     @JRubyMethod(name = {"send"}, compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, Block block) {
         return super.send19(context, arg0, block);
     }
     @JRubyMethod(name = {"send"}, compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Block block) {
         return super.send19(context, arg0, arg1, block);
     }
     @JRubyMethod(name = {"send"}, compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject send19(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         return super.send19(context, arg0, arg1, arg2, block);
     }
     @JRubyMethod(name = {"send"}, rest = true, compat = CompatVersion.RUBY1_9)
     @Override
     public IRubyObject send19(ThreadContext context, IRubyObject[] args, Block block) {
         return super.send19(context, args, block);
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
     @JRubyMethod(name = "=~", required = 1, compat = CompatVersion.RUBY1_8)
     public IRubyObject op_match(ThreadContext context, IRubyObject arg) {
     	return context.getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "=~", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_match19(ThreadContext context, IRubyObject arg) {
     	return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "!~", required = 1, compat = CompatVersion.RUBY1_9)
     public IRubyObject op_not_match(ThreadContext context, IRubyObject arg) {
         return context.getRuntime().newBoolean(! callMethod(context, "=~", arg).isTrue());
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
diff --git a/test/externals/ruby_test/test/stdlib/Set/tc_sorted_set.rb b/test/externals/ruby_test/test/stdlib/Set/tc_sorted_set.rb
index 25e0eae141..c68bb33ed0 100644
--- a/test/externals/ruby_test/test/stdlib/Set/tc_sorted_set.rb
+++ b/test/externals/ruby_test/test/stdlib/Set/tc_sorted_set.rb
@@ -1,44 +1,46 @@
 ########################################################
 # tc_sorted_set.rb
 #
 # Test case for the SortedSet class.
 ########################################################
 require 'set'
 require 'test/unit'
 
 class TC_SortedSet_Stdlib < Test::Unit::TestCase
    def setup
       @set1 = SortedSet.new([7, 3, 5, 1, 9])
       @set2 = SortedSet.new([-7, -3, -5, -1, -9])
       @set3 = SortedSet.new(['1.8.2', '1.8.5', '1.8.3', '1.8.4'])
    end
 
    def test_new_aref_basic
       assert_nothing_raised{ SortedSet[] }
-      assert_nothing_raised{ SortedSet[nil] }
+      # See Ruby Bug #118
+      #assert_nothing_raised{ SortedSet[nil] }
       assert_nothing_raised{ SortedSet[[3,1,2]] }
    end
 
    def test_new_aref_values
       assert_equal(0, SortedSet[].size)
-      assert_equal(1, SortedSet[nil].size)
+      # See Ruby Bug #118
+      #assert_equal(1, SortedSet[nil].size)
       assert_equal(1, SortedSet[[]].size)
       assert_equal(1, SortedSet[[nil]].size)
    end
 
    def test_sorted_array
       assert_equal([1, 3, 5, 7, 9], @set1.to_a)
       assert_equal([-9, -7, -5, -3, -1], @set2.to_a)
    end
 
    def test_new_expected_errors
       assert_raises(ArgumentError){ SortedSet.new(false) }
       assert_raises(ArgumentError){ SortedSet.new(1) }
       assert_raises(ArgumentError){ SortedSet.new(1,2) }
    end
 
    def teardown
       @set1 = nil
       @set2 = nil
    end
 end
