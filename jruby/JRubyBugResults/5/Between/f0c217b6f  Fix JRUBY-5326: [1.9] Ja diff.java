diff --git a/src/builtin/yaml.rb b/lib/ruby/1.8/yaml.rb
similarity index 100%
rename from src/builtin/yaml.rb
rename to lib/ruby/1.8/yaml.rb
diff --git a/src/builtin/yaml/baseemitter.rb b/lib/ruby/1.8/yaml/baseemitter.rb
similarity index 100%
rename from src/builtin/yaml/baseemitter.rb
rename to lib/ruby/1.8/yaml/baseemitter.rb
diff --git a/lib/ruby/1.9/yaml/basenode.rb b/lib/ruby/1.8/yaml/basenode.rb
similarity index 100%
rename from lib/ruby/1.9/yaml/basenode.rb
rename to lib/ruby/1.8/yaml/basenode.rb
diff --git a/src/builtin/yaml/compat.rb b/lib/ruby/1.8/yaml/compat.rb
similarity index 100%
rename from src/builtin/yaml/compat.rb
rename to lib/ruby/1.8/yaml/compat.rb
diff --git a/src/builtin/yaml/constants.rb b/lib/ruby/1.8/yaml/constants.rb
similarity index 100%
rename from src/builtin/yaml/constants.rb
rename to lib/ruby/1.8/yaml/constants.rb
diff --git a/src/builtin/yaml/dbm.rb b/lib/ruby/1.8/yaml/dbm.rb
similarity index 100%
rename from src/builtin/yaml/dbm.rb
rename to lib/ruby/1.8/yaml/dbm.rb
diff --git a/src/builtin/yaml/emitter.rb b/lib/ruby/1.8/yaml/emitter.rb
similarity index 100%
rename from src/builtin/yaml/emitter.rb
rename to lib/ruby/1.8/yaml/emitter.rb
diff --git a/src/builtin/yaml/encoding.rb b/lib/ruby/1.8/yaml/encoding.rb
similarity index 100%
rename from src/builtin/yaml/encoding.rb
rename to lib/ruby/1.8/yaml/encoding.rb
diff --git a/src/builtin/yaml/error.rb b/lib/ruby/1.8/yaml/error.rb
similarity index 100%
rename from src/builtin/yaml/error.rb
rename to lib/ruby/1.8/yaml/error.rb
diff --git a/src/builtin/yaml/rubytypes.rb b/lib/ruby/1.8/yaml/rubytypes.rb
similarity index 100%
rename from src/builtin/yaml/rubytypes.rb
rename to lib/ruby/1.8/yaml/rubytypes.rb
diff --git a/src/builtin/yaml/store.rb b/lib/ruby/1.8/yaml/store.rb
similarity index 100%
rename from src/builtin/yaml/store.rb
rename to lib/ruby/1.8/yaml/store.rb
diff --git a/src/builtin/yaml/stream.rb b/lib/ruby/1.8/yaml/stream.rb
similarity index 100%
rename from src/builtin/yaml/stream.rb
rename to lib/ruby/1.8/yaml/stream.rb
diff --git a/src/builtin/yaml/stringio.rb b/lib/ruby/1.8/yaml/stringio.rb
similarity index 100%
rename from src/builtin/yaml/stringio.rb
rename to lib/ruby/1.8/yaml/stringio.rb
diff --git a/src/builtin/yaml/tag.rb b/lib/ruby/1.8/yaml/tag.rb
similarity index 100%
rename from src/builtin/yaml/tag.rb
rename to lib/ruby/1.8/yaml/tag.rb
diff --git a/src/builtin/yaml/types.rb b/lib/ruby/1.8/yaml/types.rb
similarity index 100%
rename from src/builtin/yaml/types.rb
rename to lib/ruby/1.8/yaml/types.rb
diff --git a/src/builtin/yaml/yamlnode.rb b/lib/ruby/1.8/yaml/yamlnode.rb
similarity index 100%
rename from src/builtin/yaml/yamlnode.rb
rename to lib/ruby/1.8/yaml/yamlnode.rb
diff --git a/src/builtin/yaml/yecht.rb b/lib/ruby/1.8/yaml/yecht.rb
similarity index 100%
rename from src/builtin/yaml/yecht.rb
rename to lib/ruby/1.8/yaml/yecht.rb
diff --git a/lib/ruby/1.9/yaml/ypath.rb b/lib/ruby/1.8/yaml/ypath.rb
similarity index 100%
rename from lib/ruby/1.9/yaml/ypath.rb
rename to lib/ruby/1.8/yaml/ypath.rb
diff --git a/lib/ruby/1.9/yaml.rb b/lib/ruby/1.9/yaml.rb
index f452356b96..a45a5f565d 100644
--- a/lib/ruby/1.9/yaml.rb
+++ b/lib/ruby/1.9/yaml.rb
@@ -1,436 +1,45 @@
-# -*- mode: ruby; ruby-indent-level: 4; tab-width: 4 -*- vim: sw=4 ts=4
-# $Id: yaml.rb 24625 2009-08-22 04:52:09Z akr $
-#
-# = yaml.rb: top-level module with methods for loading and parsing YAML documents
-#
-# Author:: why the lucky stiff
-#
-
-require 'stringio'
-require 'yaml/error'
-require 'yaml/syck'
-require 'yaml/tag'
-require 'yaml/stream'
-require 'yaml/constants'
-
-# == YAML
-#
-# YAML(tm) (rhymes with 'camel') is a
-# straightforward machine parsable data serialization format designed for
-# human readability and interaction with scripting languages such as Perl
-# and Python. YAML is optimized for data serialization, formatted
-# dumping, configuration files, log files, Internet messaging and
-# filtering. This specification describes the YAML information model and
-# serialization format. Together with the Unicode standard for characters, it
-# provides all the information necessary to understand YAML Version 1.0
-# and construct computer programs to process it.
-#
-# See http://yaml.org/ for more information.  For a quick tutorial, please
-# visit YAML In Five Minutes (http://yaml.kwiki.org/?YamlInFiveMinutes).
-#
-# == About This Library
-#
-# The YAML 1.0 specification outlines four stages of YAML loading and dumping.
-# This library honors all four of those stages, although data is really only
-# available to you in three stages.
-#
-# The four stages are: native, representation, serialization, and presentation.
-#
-# The native stage refers to data which has been loaded completely into Ruby's
-# own types. (See +YAML::load+.)
-#
-# The representation stage means data which has been composed into
-# +YAML::BaseNode+ objects.  In this stage, the document is available as a
-# tree of node objects.  You can perform YPath queries and transformations
-# at this level.  (See +YAML::parse+.)
-#
-# The serialization stage happens inside the parser.  The YAML parser used in
-# Ruby is called Syck.  Serialized nodes are available in the extension as
-# SyckNode structs.
-#
-# The presentation stage is the YAML document itself.  This is accessible
-# to you as a string.  (See +YAML::dump+.)
-#
-# For more information about the various information models, see Chapter
-# 3 of the YAML 1.0 Specification (http://yaml.org/spec/#id2491269).
-#
-# The YAML module provides quick access to the most common loading (YAML::load)
-# and dumping (YAML::dump) tasks.  This module also provides an API for registering
-# global types (YAML::add_domain_type).
-#
-# == Example
-#
-# A simple round-trip (load and dump) of an object.
-#
-#     require "yaml"
-#
-#     test_obj = ["dogs", "cats", "badgers"]
-#
-#     yaml_obj = YAML::dump( test_obj )
-#                         # -> ---
-#                              - dogs
-#                              - cats
-#                              - badgers
-#     ruby_obj = YAML::load( yaml_obj )
-#                         # => ["dogs", "cats", "badgers"]
-#     ruby_obj == test_obj
-#                         # => true
-#
-# To register your custom types with the global resolver, use +add_domain_type+.
-#
-#     YAML::add_domain_type( "your-site.com,2004", "widget" ) do |type, val|
-#         Widget.new( val )
-#     end
-#
 module YAML
+  class EngineManager # :nodoc:
+    attr_reader :yamler
 
-    Resolver = YAML::Syck::Resolver
-    DefaultResolver = YAML::Syck::DefaultResolver
-    DefaultResolver.use_types_at( @@tagged_classes )
-    GenericResolver = YAML::Syck::GenericResolver
-    Parser = YAML::Syck::Parser
-    Emitter = YAML::Syck::Emitter
-
-    # Returns a new default parser
-    def YAML.parser; Parser.new.set_resolver( YAML.resolver ); end
-    # Returns a new generic parser
-    def YAML.generic_parser; Parser.new.set_resolver( GenericResolver ); end
-    # Returns the default resolver
-    def YAML.resolver; DefaultResolver; end
-    # Returns a new default emitter
-    def YAML.emitter; Emitter.new.set_resolver( YAML.resolver ); end
-
-	#
-	# Converts _obj_ to YAML and writes the YAML result to _io_.
-    #
-    #   File.open( 'animals.yaml', 'w' ) do |out|
-    #     YAML.dump( ['badger', 'elephant', 'tiger'], out )
-    #   end
-    #
-    # If no _io_ is provided, a string containing the dumped YAML
-    # is returned.
-	#
-    #   YAML.dump( :locked )
-    #      #=> "--- :locked"
-    #
-	def YAML.dump( obj, io = nil )
-        obj.to_yaml( io || io2 = StringIO.new )
-        io || ( io2.rewind; io2.read )
-	end
-
-	#
-	# Load a document from the current _io_ stream.
-	#
-    #   File.open( 'animals.yaml' ) { |yf| YAML::load( yf ) }
-    #      #=> ['badger', 'elephant', 'tiger']
-    #
-    # Can also load from a string.
-    #
-    #   YAML.load( "--- :locked" )
-    #      #=> :locked
-    #
-	def YAML.load( io )
-		yp = parser.load( io )
-	end
-
-    #
-    # Load a document from the file located at _filepath_.
-    #
-    #   YAML.load_file( 'animals.yaml' )
-    #      #=> ['badger', 'elephant', 'tiger']
-    #
-    def YAML.load_file( filepath )
-        File.open( filepath ) do |f|
-            load( f )
-        end
+    def initialize
+      @yamler = nil
     end
 
-	#
-	# Parse the first document from the current _io_ stream
-	#
-    #   File.open( 'animals.yaml' ) { |yf| YAML::load( yf ) }
-    #      #=> #<YAML::Syck::Node:0x82ccce0
-    #           @kind=:seq,
-    #           @value=
-    #            [#<YAML::Syck::Node:0x82ccd94
-    #              @kind=:scalar,
-    #              @type_id="str",
-    #              @value="badger">,
-    #             #<YAML::Syck::Node:0x82ccd58
-    #              @kind=:scalar,
-    #              @type_id="str",
-    #              @value="elephant">,
-    #             #<YAML::Syck::Node:0x82ccd1c
-    #              @kind=:scalar,
-    #              @type_id="str",
-    #              @value="tiger">]>
-    #
-    # Can also load from a string.
-    #
-    #   YAML.parse( "--- :locked" )
-    #      #=> #<YAML::Syck::Node:0x82edddc
-    #            @type_id="tag:ruby.yaml.org,2002:sym",
-    #            @value=":locked", @kind=:scalar>
-    #
-	def YAML.parse( io )
-		yp = generic_parser.load( io )
-	end
-
-    #
-    # Parse a document from the file located at _filepath_.
-    #
-    #   YAML.parse_file( 'animals.yaml' )
-    #      #=> #<YAML::Syck::Node:0x82ccce0
-    #           @kind=:seq,
-    #           @value=
-    #            [#<YAML::Syck::Node:0x82ccd94
-    #              @kind=:scalar,
-    #              @type_id="str",
-    #              @value="badger">,
-    #             #<YAML::Syck::Node:0x82ccd58
-    #              @kind=:scalar,
-    #              @type_id="str",
-    #              @value="elephant">,
-    #             #<YAML::Syck::Node:0x82ccd1c
-    #              @kind=:scalar,
-    #              @type_id="str",
-    #              @value="tiger">]>
-    #
-    def YAML.parse_file( filepath )
-        File.open( filepath ) do |f|
-            parse( f )
-        end
+    def syck?
+      'syck' == @yamler
     end
 
-	#
-	# Calls _block_ with each consecutive document in the YAML
-    # stream contained in _io_.
-    #
-    #   File.open( 'many-docs.yaml' ) do |yf|
-    #     YAML.each_document( yf ) do |ydoc|
-    #       ## ydoc contains the single object
-    #       ## from the YAML document
-    #     end
-    #   end
-	#
-    def YAML.each_document( io, &block )
-		yp = parser.load_documents( io, &block )
-    end
+    def yamler= engine
+      raise(ArgumentError, "bad engine") unless %w{syck psych}.include?(engine)
 
-	#
-	# Calls _block_ with each consecutive document in the YAML
-    # stream contained in _io_.
-    #
-    #   File.open( 'many-docs.yaml' ) do |yf|
-    #     YAML.load_documents( yf ) do |ydoc|
-    #       ## ydoc contains the single object
-    #       ## from the YAML document
-    #     end
-    #   end
-	#
-    def YAML.load_documents( io, &doc_proc )
-		YAML.each_document( io, &doc_proc )
-    end
+      require engine
 
-	#
-	# Calls _block_ with a tree of +YAML::BaseNodes+, one tree for
-    # each consecutive document in the YAML stream contained in _io_.
-    #
-    #   File.open( 'many-docs.yaml' ) do |yf|
-    #     YAML.each_node( yf ) do |ydoc|
-    #       ## ydoc contains a tree of nodes
-    #       ## from the YAML document
-    #     end
-    #   end
-	#
-    def YAML.each_node( io, &doc_proc )
-		yp = generic_parser.load_documents( io, &doc_proc )
-    end
+      Object.class_eval <<-eorb, __FILE__, __LINE__ + 1
+        remove_const 'YAML'
+        YAML = #{engine.capitalize}
+        remove_method :to_yaml
+        alias :to_yaml :#{engine}_to_yaml
+      eorb
 
-	#
-	# Calls _block_ with a tree of +YAML::BaseNodes+, one tree for
-    # each consecutive document in the YAML stream contained in _io_.
-    #
-    #   File.open( 'many-docs.yaml' ) do |yf|
-    #     YAML.parse_documents( yf ) do |ydoc|
-    #       ## ydoc contains a tree of nodes
-    #       ## from the YAML document
-    #     end
-    #   end
-	#
-    def YAML.parse_documents( io, &doc_proc )
-		YAML.each_node( io, &doc_proc )
+      @yamler = engine
+      engine
     end
+  end
 
-	#
-	# Loads all documents from the current _io_ stream,
-    # returning a +YAML::Stream+ object containing all
-    # loaded documents.
-	#
-	def YAML.load_stream( io )
-		d = nil
-		parser.load_documents( io ) do |doc|
-			d = YAML::Stream.new if not d
-			d.add( doc )
-        end
-		return d
-	end
-
-	#
-    # Returns a YAML stream containing each of the items in +objs+,
-    # each having their own document.
-    #
-    #   YAML.dump_stream( 0, [], {} )
-    #     #=> --- 0
-    #         --- []
-    #         --- {}
-    #
-	def YAML.dump_stream( *objs )
-		d = YAML::Stream.new
-        objs.each do |doc|
-			d.add( doc )
-        end
-        d.emit
-	end
-
-	#
-	# Add a global handler for a YAML domain type.
-	#
-	def YAML.add_domain_type( domain, type_tag, &transfer_proc )
-        resolver.add_type( "tag:#{ domain }:#{ type_tag }", transfer_proc )
-	end
-
-	#
-	# Add a transfer method for a builtin type
-	#
-	def YAML.add_builtin_type( type_tag, &transfer_proc )
-	    resolver.add_type( "tag:yaml.org,2002:#{ type_tag }", transfer_proc )
-	end
-
-	#
-	# Add a transfer method for a builtin type
-	#
-	def YAML.add_ruby_type( type_tag, &transfer_proc )
-	    resolver.add_type( "tag:ruby.yaml.org,2002:#{ type_tag }", transfer_proc )
-	end
-
-	#
-	# Add a private document type
-	#
-	def YAML.add_private_type( type_re, &transfer_proc )
-	    resolver.add_type( "x-private:" + type_re, transfer_proc )
-	end
-
-    #
-    # Detect typing of a string
-    #
-    def YAML.detect_implicit( val )
-        resolver.detect_implicit( val )
-    end
-
-    #
-    # Convert a type_id to a taguri
-    #
-    def YAML.tagurize( val )
-        resolver.tagurize( val )
-    end
-
-    #
-    # Apply a transfer method to a Ruby object
-    #
-    def YAML.transfer( type_id, obj )
-        resolver.transfer( YAML.tagurize( type_id ), obj )
-    end
-
-	#
-	# Apply any implicit a node may qualify for
-	#
-	def YAML.try_implicit( obj )
-		YAML.transfer( YAML.detect_implicit( obj ), obj )
-	end
-
-    #
-    # Method to extract colon-seperated type and class, returning
-    # the type and the constant of the class
-    #
-    def YAML.read_type_class( type, obj_class )
-        scheme, domain, type, tclass = type.split( ':', 4 )
-        tclass.split( "::" ).each { |c| obj_class = obj_class.const_get( c ) } if tclass
-        return [ type, obj_class ]
-    end
-
-    #
-    # Allocate blank object
-    #
-    def YAML.object_maker( obj_class, val )
-        if Hash === val
-            o = obj_class.allocate
-            val.each_pair { |k,v|
-                o.instance_variable_set("@#{k}", v)
-            }
-            o
-        else
-            raise YAML::Error, "Invalid object explicitly tagged !ruby/Object: " + val.inspect
-        end
-    end
-
-	#
-	# Allocate an Emitter if needed
-	#
-	def YAML.quick_emit( oid, opts = {}, &e )
-        out =
-            if opts.is_a? YAML::Emitter
-                opts
-            else
-                emitter.reset( opts )
-            end
-        out.emit( oid, &e )
-	end
-
+  ENGINE = YAML::EngineManager.new
 end
 
-require 'yaml/rubytypes'
-require 'yaml/types'
+# JRuby defaults to Psych, to avoid having to use Yecht in 1.9 mode
+engine = 'psych'
+# engine = (!defined?(Yecht) && defined?(Psych) ? 'psych' : 'syck')
 
-module Kernel
-    #
-    # ryan:: You know how Kernel.p is a really convenient way to dump ruby
-    #        structures?  The only downside is that it's not as legible as
-    #        YAML.
-    #
-    # _why:: (listening)
-    #
-    # ryan:: I know you don't want to urinate all over your users' namespaces.
-    #        But, on the other hand, convenience of dumping for debugging is,
-    #        IMO, a big YAML use case.
-    #
-    # _why:: Go nuts!  Have a pony parade!
-    #
-    # ryan:: Either way, I certainly will have a pony parade.
-    #
-
-    # Prints any supplied _objects_ out in YAML.  Intended as
-    # a variation on +Kernel::p+.
-    #
-    #   S = Struct.new(:name, :state)
-    #   s = S['dave', 'TX']
-    #   y s
-    #
-    # _produces:_
-    #
-    #   --- !ruby/struct:S
-    #   name: dave
-    #   state: TX
-    #
-    def y( object, *objects )
-        objects.unshift object
-        puts( if objects.length == 1
-                  YAML::dump( *objects )
-              else
-                  YAML::dump_stream( *objects )
-              end )
-    end
-    private :y
+module Syck
+  ENGINE = YAML::ENGINE
 end
 
+module Psych
+  ENGINE = YAML::ENGINE
+end
 
+YAML::ENGINE.yamler = engine
diff --git a/lib/ruby/1.9/yaml/baseemitter.rb b/lib/ruby/1.9/yaml/baseemitter.rb
deleted file mode 100644
index 59d9eddc76..0000000000
--- a/lib/ruby/1.9/yaml/baseemitter.rb
+++ /dev/null
@@ -1,242 +0,0 @@
-#
-# BaseEmitter
-#
-
-require 'yaml/constants'
-require 'yaml/encoding'
-require 'yaml/error'
-
-module YAML
-  module BaseEmitter
-    def options( opt = nil )
-      if opt
-        @options[opt] || YAML::DEFAULTS[opt]
-      else
-        @options
-      end
-    end
-
-    def options=( opt )
-      @options = opt
-    end
-
-    #
-    # Emit binary data
-    #
-    def binary_base64( value )
-      self << "!binary "
-      self.node_text( [value].pack("m"), '|' )
-    end
-
-    #
-    # Emit plain, normal flowing text
-    #
-    def node_text( value, block = nil )
-      @seq_map = false
-      valx = value.dup
-      unless block
-        block =
-          if options(:UseBlock)
-            '|'
-          elsif not options(:UseFold) and valx =~ /\n[ \t]/ and not valx =~ /#{YAML::ESCAPE_CHAR}/
-            '|'
-          else
-            '>'
-          end
-        indt = $&.to_i if block =~ /\d+/
-        if valx =~ /(\A\n*[ \t#]|^---\s+)/
-          indt = options(:Indent) unless indt.to_i > 0
-          block += indt.to_s
-        end
-
-        block +=
-          if valx =~ /\n\Z\n/
-            "+"
-          elsif valx =~ /\Z\n/
-            ""
-          else
-            "-"
-          end
-      end
-      block += "\n"
-      if block[0] == ?"
-        esc_skip = ( "\t\n" unless valx =~ /^[ \t]/ ) || ""
-        valx = fold( YAML::escape( valx, esc_skip ) + "\"" ).chomp
-        self << '"' + indent_text( valx, indt, false )
-      else
-        if block[0] == ?>
-          valx = fold( valx )
-        end
-        #p [block, indt]
-        self << block + indent_text( valx, indt )
-      end
-    end
-
-    #
-    # Emit a simple, unqouted string
-    #
-    def simple( value )
-      @seq_map = false
-      self << value.to_s
-    end
-
-    #
-    # Emit double-quoted string
-    #
-    def double( value )
-      "\"#{YAML.escape( value )}\""
-    end
-
-    #
-    # Emit single-quoted string
-    #
-    def single( value )
-      "'#{value}'"
-    end
-
-    #
-    # Write a text block with the current indent
-    #
-    def indent_text( text, mod, first_line = true )
-      return "" if text.to_s.empty?
-      spacing = indent( mod )
-      text = text.gsub( /\A([^\n])/, "#{ spacing }\\1" ) if first_line
-      return text.gsub( /\n^([^\n])/, "\n#{spacing}\\1" )
-    end
-
-    #
-    # Write a current indent
-    #
-    def indent( mod = nil )
-      #p [ self.id, level, mod, :INDENT ]
-      if level <= 0
-        mod ||= 0
-      else
-        mod ||= options(:Indent)
-        mod += ( level - 1 ) * options(:Indent)
-      end
-      return " " * mod
-    end
-
-    #
-    # Add indent to the buffer
-    #
-    def indent!
-      self << indent
-    end
-
-    #
-    # Folding paragraphs within a column
-    #
-    def fold( value )
-      value.gsub( /(^[ \t]+.*$)|(\S.{0,#{options(:BestWidth) - 1}})(?:[ \t]+|(\n+(?=[ \t]|\Z))|$)/ ) do
-        $1 || $2 + ( $3 || "\n" )
-      end
-    end
-
-    #
-    # Quick mapping
-    #
-    def map( type, &e )
-      val = Mapping.new
-      e.call( val )
-      self << "#{type} " if type.length.nonzero?
-
-      #
-      # Empty hashes
-      #
-      if val.length.zero?
-        self << "{}"
-        @seq_map = false
-      else
-        # FIXME
-        # if @buffer.length == 1 and options(:UseHeader) == false and type.length.zero?
-        #     @headless = 1
-        # end
-
-        defkey = @options.delete( :DefaultKey )
-        if defkey
-          seq_map_shortcut
-          self << "= : "
-          defkey.to_yaml( :Emitter => self )
-        end
-
-        #
-        # Emit the key and value
-        #
-        val.each { |v|
-          seq_map_shortcut
-          if v[0].is_complex_yaml?
-            self << "? "
-          end
-          v[0].to_yaml( :Emitter => self )
-          if v[0].is_complex_yaml?
-            self << "\n"
-            indent!
-          end
-          self << ": "
-          v[1].to_yaml( :Emitter => self )
-        }
-      end
-    end
-
-    def seq_map_shortcut
-      # FIXME: seq_map needs to work with the new anchoring system
-      # if @seq_map
-      #     @anchor_extras[@buffer.length - 1] = "\n" + indent
-      #     @seq_map = false
-      # else
-      self << "\n"
-      indent!
-      # end
-    end
-
-    #
-    # Quick sequence
-    #
-    def seq( type, &e )
-      @seq_map = false
-      val = Sequence.new
-      e.call( val )
-      self << "#{type} " if type.length.nonzero?
-
-      #
-      # Empty arrays
-      #
-      if val.length.zero?
-        self << "[]"
-      else
-        # FIXME
-        # if @buffer.length == 1 and options(:UseHeader) == false and type.length.zero?
-        #     @headless = 1
-        # end
-
-        #
-        # Emit the key and value
-        #
-        val.each { |v|
-          self << "\n"
-          indent!
-          self << "- "
-          @seq_map = true if v.class == Hash
-          v.to_yaml( :Emitter => self )
-        }
-      end
-    end
-  end
-
-  #
-  # Emitter helper classes
-  #
-  class Mapping < Array
-    def add( k, v )
-      push [k, v]
-    end
-  end
-
-  class Sequence < Array
-    def add( v )
-      push v
-    end
-  end
-end
diff --git a/lib/ruby/1.9/yaml/constants.rb b/lib/ruby/1.9/yaml/constants.rb
deleted file mode 100644
index 728d3b7932..0000000000
--- a/lib/ruby/1.9/yaml/constants.rb
+++ /dev/null
@@ -1,45 +0,0 @@
-#
-# Constants used throughout the library
-#
-module YAML
-
-	#
-	# Constants
-	#
-	VERSION = '0.60'
-	SUPPORTED_YAML_VERSIONS = ['1.0']
-
-	#
-	# Parser tokens
-	#
-	WORD_CHAR = 'A-Za-z0-9'
-	PRINTABLE_CHAR = '-_A-Za-z0-9!?/()$\'". '
-	NOT_PLAIN_CHAR = '\x7f\x0-\x1f\x80-\x9f'
-	ESCAPE_CHAR = '[\\x00-\\x09\\x0b-\\x1f]'
-	INDICATOR_CHAR = '*&!|\\\\^@%{}[]='
-	SPACE_INDICATORS = '-#:,?'
-	RESTRICTED_INDICATORS = '#:,}]'
-	DNS_COMP_RE = "\\w(?:[-\\w]*\\w)?"
-	DNS_NAME_RE = "(?:(?:#{DNS_COMP_RE}\\.)+#{DNS_COMP_RE}|#{DNS_COMP_RE})"
-	ESCAPES = %w{\x00   \x01	\x02	\x03	\x04	\x05	\x06	\a
-			     \x08	\t		\n		\v		\f		\r		\x0e	\x0f
-				 \x10	\x11	\x12	\x13	\x14	\x15	\x16	\x17
-				 \x18	\x19	\x1a	\e		\x1c	\x1d	\x1e	\x1f
-			    }
-	UNESCAPES = {
-				'a' => "\x07", 'b' => "\x08", 't' => "\x09",
-				'n' => "\x0a", 'v' => "\x0b", 'f' => "\x0c",
-				'r' => "\x0d", 'e' => "\x1b", '\\' => '\\',
-			    }
-
-	#
-	# Default settings
-	#
-	DEFAULTS = {
-		:Indent => 2, :UseHeader => false, :UseVersion => false, :Version => '1.0',
-		:SortKeys => false, :AnchorFormat => 'id%03d', :ExplicitTypes => false,
-		:WidthType => 'absolute', :BestWidth => 80,
-		:UseBlock => false, :UseFold => false, :Encoding => :None
-	}
-
-end
diff --git a/lib/ruby/1.9/yaml/dbm.rb b/lib/ruby/1.9/yaml/dbm.rb
deleted file mode 100644
index a28fd04f19..0000000000
--- a/lib/ruby/1.9/yaml/dbm.rb
+++ /dev/null
@@ -1,111 +0,0 @@
-require 'yaml'
-require 'dbm'
-#
-# YAML + DBM = YDBM
-# - Same interface as DBM class
-#
-module YAML
-
-class DBM < ::DBM
-    VERSION = "0.1"
-    def []( key )
-        fetch( key )
-    end
-    def []=( key, val )
-        store( key, val )
-    end
-    def fetch( keystr, ifnone = nil )
-        begin
-            val = super( keystr )
-            return YAML::load( val ) if String === val
-        rescue IndexError
-        end
-        if block_given?
-            yield keystr
-        else
-            ifnone
-        end
-    end
-    def index( keystr )
-        super( keystr.to_yaml )
-    end
-    def values_at( *keys )
-        keys.collect { |k| fetch( k ) }
-    end
-    def delete( key )
-        v = super( key )
-        if String === v
-            v = YAML::load( v )
-        end
-        v
-    end
-    def delete_if
-        del_keys = keys.dup
-        del_keys.delete_if { |k| yield( k, fetch( k ) ) == false }
-        del_keys.each { |k| delete( k ) }
-        self
-    end
-    def reject
-        hsh = self.to_hash
-        hsh.reject { |k,v| yield k, v }
-    end
-    def each_pair
-        keys.each { |k| yield k, fetch( k ) }
-        self
-    end
-    def each_value
-        super { |v| yield YAML::load( v ) }
-        self
-    end
-    def values
-        super.collect { |v| YAML::load( v ) }
-    end
-    def has_value?( val )
-        each_value { |v| return true if v == val }
-        return false
-    end
-    def invert
-        h = {}
-        keys.each { |k| h[ self.fetch( k ) ] = k }
-        h
-    end
-    def replace( hsh )
-        clear
-        update( hsh )
-    end
-    def shift
-        a = super
-        a[1] = YAML::load( a[1] ) if a
-        a
-    end
-    def select( *keys )
-        if block_given?
-            self.keys.collect { |k| v = self[k]; [k, v] if yield k, v }.compact
-        else
-            values_at( *keys )
-        end
-    end
-    def store( key, val )
-        super( key, val.to_yaml )
-        val
-    end
-    def update( hsh )
-        hsh.keys.each do |k|
-            self.store( k, hsh.fetch( k ) )
-        end
-        self
-    end
-    def to_a
-        a = []
-        keys.each { |k| a.push [ k, self.fetch( k ) ] }
-        a
-    end
-    def to_hash
-        h = {}
-        keys.each { |k| h[ k ] = self.fetch( k ) }
-        h
-    end
-    alias :each :each_pair
-end
-
-end
diff --git a/lib/ruby/1.9/yaml/encoding.rb b/lib/ruby/1.9/yaml/encoding.rb
deleted file mode 100644
index 98e83c3853..0000000000
--- a/lib/ruby/1.9/yaml/encoding.rb
+++ /dev/null
@@ -1,33 +0,0 @@
-#
-# Handle Unicode-to-Internal conversion
-#
-
-module YAML
-
-	#
-	# Escape the string, condensing common escapes
-	#
-	def YAML.escape( value, skip = "" )
-		value.gsub( /\\/, "\\\\\\" ).
-              gsub( /"/, "\\\"" ).
-              gsub( /([\x00-\x1f])/ ) do
-                 skip[$&] || ESCAPES[ $&.unpack("C")[0] ]
-             end
-	end
-
-	#
-	# Unescape the condenses escapes
-	#
-	def YAML.unescape( value )
-		value.gsub( /\\(?:([nevfbart\\])|0?x([0-9a-fA-F]{2})|u([0-9a-fA-F]{4}))/ ) {
-			if $3
-				["#$3".hex ].pack('U*')
-			elsif $2
-				[$2].pack( "H2" )
-			else
-				UNESCAPES[$1]
-			end
-		}
-	end
-
-end
diff --git a/lib/ruby/1.9/yaml/error.rb b/lib/ruby/1.9/yaml/error.rb
deleted file mode 100644
index 75de0ec18a..0000000000
--- a/lib/ruby/1.9/yaml/error.rb
+++ /dev/null
@@ -1,34 +0,0 @@
-#
-# Error messages and exception class
-#
-
-module YAML
-
-	#
-	# Error messages
-	#
-
-	ERROR_NO_HEADER_NODE = "With UseHeader=false, the node Array or Hash must have elements"
-	ERROR_NEED_HEADER = "With UseHeader=false, the node must be an Array or Hash"
-	ERROR_BAD_EXPLICIT = "Unsupported explicit transfer: '%s'"
-    ERROR_MANY_EXPLICIT = "More than one explicit transfer"
-	ERROR_MANY_IMPLICIT = "More than one implicit request"
-	ERROR_NO_ANCHOR = "No anchor for alias '%s'"
-	ERROR_BAD_ANCHOR = "Invalid anchor: %s"
-	ERROR_MANY_ANCHOR = "More than one anchor"
-	ERROR_ANCHOR_ALIAS = "Can't define both an anchor and an alias"
-	ERROR_BAD_ALIAS = "Invalid alias: %s"
-	ERROR_MANY_ALIAS = "More than one alias"
-	ERROR_ZERO_INDENT = "Can't use zero as an indentation width"
-	ERROR_UNSUPPORTED_VERSION = "This release of YAML.rb does not support YAML version %s"
-	ERROR_UNSUPPORTED_ENCODING = "Attempt to use unsupported encoding: %s"
-
-	#
-	# YAML Error classes
-	#
-
-	class Error < StandardError; end
-	class ParseError < Error; end
-    class TypeError < StandardError; end
-
-end
diff --git a/lib/ruby/1.9/yaml/loader.rb b/lib/ruby/1.9/yaml/loader.rb
deleted file mode 100644
index eb0709e103..0000000000
--- a/lib/ruby/1.9/yaml/loader.rb
+++ /dev/null
@@ -1,14 +0,0 @@
-#
-# YAML::Loader class
-# .. type handling ..
-#
-module YAML
-    class Loader
-        TRANSFER_DOMAINS = {
-            'yaml.org,2002' => {},
-            'ruby.yaml.org,2002' => {}
-        }
-        PRIVATE_TYPES = {}
-        IMPLICIT_TYPES = [ 'null', 'bool', 'time', 'int', 'float' ]
-    end
-end
diff --git a/lib/ruby/1.9/yaml/rubytypes.rb b/lib/ruby/1.9/yaml/rubytypes.rb
deleted file mode 100644
index e8c0c89f2c..0000000000
--- a/lib/ruby/1.9/yaml/rubytypes.rb
+++ /dev/null
@@ -1,446 +0,0 @@
-# -*- mode: ruby; ruby-indent-level: 4; tab-width: 4 -*- vim: sw=4 ts=4
-require 'date'
-
-class Class
-	def to_yaml( opts = {} )
-		raise TypeError, "can't dump anonymous class %s" % self.class
-	end
-end
-
-class Object
-    yaml_as "tag:ruby.yaml.org,2002:object"
-    def to_yaml_style; end
-    def to_yaml_properties; instance_variables.sort; end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-            out.map( taguri, to_yaml_style ) do |map|
-				to_yaml_properties.each do |m|
-                    map.add( m[1..-1], instance_variable_get( m ) )
-                end
-            end
-        end
-	end
-end
-
-class Hash
-    yaml_as "tag:ruby.yaml.org,2002:hash"
-    yaml_as "tag:yaml.org,2002:map"
-    def yaml_initialize( tag, val )
-        if Array === val
-            update Hash.[]( *val )		# Convert the map to a sequence
-        elsif Hash === val
-            update val
-        else
-            raise YAML::TypeError, "Invalid map explicitly tagged #{ tag }: " + val.inspect
-        end
-    end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-            out.map( taguri, to_yaml_style ) do |map|
-                each do |k, v|
-                    map.add( k, v )
-                end
-            end
-        end
-	end
-end
-
-class Struct
-    yaml_as "tag:ruby.yaml.org,2002:struct"
-    def self.yaml_tag_class_name; self.name.gsub( "Struct::", "" ); end
-    def self.yaml_tag_read_class( name ); "Struct::#{ name }"; end
-    def self.yaml_new( klass, tag, val )
-        if Hash === val
-            struct_type = nil
-
-            #
-            # Use existing Struct if it exists
-            #
-            props = {}
-            val.delete_if { |k,v| props[k] = v if k =~ /^@/ }
-            begin
-                struct_name, struct_type = YAML.read_type_class( tag, Struct )
-            rescue NameError
-            end
-            if not struct_type
-                struct_def = [ tag.split( ':', 4 ).last ]
-                struct_type = Struct.new( *struct_def.concat( val.keys.collect { |k| k.intern } ) )
-            end
-
-            #
-            # Set the Struct properties
-            #
-            st = YAML::object_maker( struct_type, {} )
-            st.members.each do |m|
-                st.send( "#{m}=", val[m] )
-            end
-            props.each do |k,v|
-                st.instance_variable_set(k, v)
-            end
-            st
-        else
-            raise YAML::TypeError, "Invalid Ruby Struct: " + val.inspect
-        end
-    end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-			#
-			# Basic struct is passed as a YAML map
-			#
-            out.map( taguri, to_yaml_style ) do |map|
-				self.members.each do |m|
-                    map.add( m, self[m] )
-                end
-				self.to_yaml_properties.each do |m|
-                    map.add( m, instance_variable_get( m ) )
-                end
-            end
-        end
-	end
-end
-
-class Array
-    yaml_as "tag:ruby.yaml.org,2002:array"
-    yaml_as "tag:yaml.org,2002:seq"
-    def yaml_initialize( tag, val ); concat( val.to_a ); end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-            out.seq( taguri, to_yaml_style ) do |seq|
-                each do |x|
-                    seq.add( x )
-                end
-            end
-        end
-	end
-end
-
-class Exception
-    yaml_as "tag:ruby.yaml.org,2002:exception"
-    def Exception.yaml_new( klass, tag, val )
-        o = YAML.object_maker( klass, { 'mesg' => val.delete( 'message' ) } )
-        val.each_pair do |k,v|
-            o.instance_variable_set("@#{k}", v)
-        end
-        o
-    end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-            out.map( taguri, to_yaml_style ) do |map|
-                map.add( 'message', message )
-				to_yaml_properties.each do |m|
-                    map.add( m[1..-1], instance_variable_get( m ) )
-                end
-            end
-        end
-	end
-end
-
-class String
-    yaml_as "tag:ruby.yaml.org,2002:string"
-    yaml_as "tag:yaml.org,2002:binary"
-    yaml_as "tag:yaml.org,2002:str"
-    def is_complex_yaml?
-        to_yaml_style or not to_yaml_properties.empty? or self =~ /\n.+/
-    end
-    def is_binary_data?
-        self.count("^ -~\t\r\n").fdiv(self.size) > 0.3 || self.index("\x00") unless self.empty?
-    end
-    def String.yaml_new( klass, tag, val )
-        val = val.unpack("m")[0] if tag == "tag:yaml.org,2002:binary"
-        val = { 'str' => val } if String === val
-        if Hash === val
-            s = klass.allocate
-            # Thank you, NaHi
-            String.instance_method(:initialize).
-                  bind(s).
-                  call( val.delete( 'str' ) )
-            val.each { |k,v| s.instance_variable_set( k, v ) }
-            s
-        else
-            raise YAML::TypeError, "Invalid String: " + val.inspect
-        end
-    end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( is_complex_yaml? ? self : nil, opts ) do |out|
-            if is_binary_data?
-                out.scalar( "tag:yaml.org,2002:binary", [self].pack("m"), :literal )
-            elsif to_yaml_properties.empty?
-                out.scalar( taguri, self, self =~ /^:/ ? :quote2 : to_yaml_style )
-            else
-                out.map( taguri, to_yaml_style ) do |map|
-                    map.add( 'str', "#{self}" )
-                    to_yaml_properties.each do |m|
-                        map.add( m, instance_variable_get( m ) )
-                    end
-                end
-            end
-        end
-	end
-end
-
-class Symbol
-    yaml_as "tag:ruby.yaml.org,2002:symbol"
-    yaml_as "tag:ruby.yaml.org,2002:sym"
-    def Symbol.yaml_new( klass, tag, val )
-        if String === val
-            val = YAML::load( val ) if val =~ /\A(["']).*\1\z/
-            val.intern
-        else
-            raise YAML::TypeError, "Invalid Symbol: " + val.inspect
-        end
-    end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( nil, opts ) do |out|
-            out.scalar( "tag:yaml.org,2002:str", self.inspect, :plain )
-        end
-	end
-end
-
-class Range
-    yaml_as "tag:ruby.yaml.org,2002:range"
-    def Range.yaml_new( klass, tag, val )
-        inr = %r'(\w+|[+-]?\d+(?:\.\d+)?(?:e[+-]\d+)?|"(?:[^\\"]|\\.)*")'
-        opts = {}
-        if String === val and val =~ /^#{inr}(\.{2,3})#{inr}$/o
-            r1, rdots, r2 = $1, $2, $3
-            opts = {
-                'begin' => YAML.load( "--- #{r1}" ),
-                'end' => YAML.load( "--- #{r2}" ),
-                'excl' => rdots.length == 3
-            }
-            val = {}
-        elsif Hash === val
-            opts['begin'] = val.delete('begin')
-            opts['end'] = val.delete('end')
-            opts['excl'] = val.delete('excl')
-        end
-        if Hash === opts
-            r = YAML::object_maker( klass, {} )
-            # Thank you, NaHi
-            Range.instance_method(:initialize).
-                  bind(r).
-                  call( opts['begin'], opts['end'], opts['excl'] )
-            val.each { |k,v| r.instance_variable_set( k, v ) }
-            r
-        else
-            raise YAML::TypeError, "Invalid Range: " + val.inspect
-        end
-    end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-            # if self.begin.is_complex_yaml? or self.begin.respond_to? :to_str or
-            #   self.end.is_complex_yaml? or self.end.respond_to? :to_str or
-            #   not to_yaml_properties.empty?
-                out.map( taguri, to_yaml_style ) do |map|
-                    map.add( 'begin', self.begin )
-                    map.add( 'end', self.end )
-                    map.add( 'excl', self.exclude_end? )
-                    to_yaml_properties.each do |m|
-                        map.add( m, instance_variable_get( m ) )
-                    end
-                end
-            # else
-            #     out.scalar( taguri ) do |sc|
-            #         sc.embed( self.begin )
-            #         sc.concat( self.exclude_end? ? "..." : ".." )
-            #         sc.embed( self.end )
-            #     end
-            # end
-        end
-	end
-end
-
-class Regexp
-    yaml_as "tag:ruby.yaml.org,2002:regexp"
-    def Regexp.yaml_new( klass, tag, val )
-        if String === val and val =~ /^\/(.*)\/([mix]*)$/
-            val = { 'regexp' => $1, 'mods' => $2 }
-        end
-        if Hash === val
-            mods = nil
-            unless val['mods'].to_s.empty?
-                mods = 0x00
-                mods |= Regexp::EXTENDED if val['mods'].include?( 'x' )
-                mods |= Regexp::IGNORECASE if val['mods'].include?( 'i' )
-                mods |= Regexp::MULTILINE if val['mods'].include?( 'm' )
-            end
-            val.delete( 'mods' )
-            r = YAML::object_maker( klass, {} )
-            Regexp.instance_method(:initialize).
-                  bind(r).
-                  call( val.delete( 'regexp' ), mods )
-            val.each { |k,v| r.instance_variable_set( k, v ) }
-            r
-        else
-            raise YAML::TypeError, "Invalid Regular expression: " + val.inspect
-        end
-    end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( nil, opts ) do |out|
-            if to_yaml_properties.empty?
-                out.scalar( taguri, self.inspect, :plain )
-            else
-                out.map( taguri, to_yaml_style ) do |map|
-                    src = self.inspect
-                    if src =~ /\A\/(.*)\/([a-z]*)\Z/
-                        map.add( 'regexp', $1 )
-                        map.add( 'mods', $2 )
-                    else
-		                raise YAML::TypeError, "Invalid Regular expression: " + src
-                    end
-                    to_yaml_properties.each do |m|
-                        map.add( m, instance_variable_get( m ) )
-                    end
-                end
-            end
-        end
-	end
-end
-
-class Time
-    yaml_as "tag:ruby.yaml.org,2002:time"
-    yaml_as "tag:yaml.org,2002:timestamp"
-    def Time.yaml_new( klass, tag, val )
-        if Hash === val
-            t = val.delete( 'at' )
-            val.each { |k,v| t.instance_variable_set( k, v ) }
-            t
-        else
-            raise YAML::TypeError, "Invalid Time: " + val.inspect
-        end
-    end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-            tz = "Z"
-            # from the tidy Tobias Peters <t-peters@gmx.de> Thanks!
-            unless self.utc?
-                utc_same_instant = self.dup.utc
-                utc_same_writing = Time.utc(year,month,day,hour,min,sec,usec)
-                difference_to_utc = utc_same_writing - utc_same_instant
-                if (difference_to_utc < 0)
-                    difference_sign = '-'
-                    absolute_difference = -difference_to_utc
-                else
-                    difference_sign = '+'
-                    absolute_difference = difference_to_utc
-                end
-                difference_minutes = (absolute_difference/60).round
-                tz = "%s%02d:%02d" % [ difference_sign, difference_minutes / 60, difference_minutes % 60]
-            end
-            standard = self.strftime( "%Y-%m-%d %H:%M:%S" )
-            standard += ".%06d" % [usec] if usec.nonzero?
-            standard += " %s" % [tz]
-            if to_yaml_properties.empty?
-                out.scalar( taguri, standard, :plain )
-            else
-                out.map( taguri, to_yaml_style ) do |map|
-                    map.add( 'at', standard )
-                    to_yaml_properties.each do |m|
-                        map.add( m, instance_variable_get( m ) )
-                    end
-                end
-            end
-        end
-	end
-end
-
-class Date
-    yaml_as "tag:yaml.org,2002:timestamp#ymd"
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-            out.scalar( "tag:yaml.org,2002:timestamp", self.to_s, :plain )
-        end
-	end
-end
-
-class Integer
-    yaml_as "tag:yaml.org,2002:int"
-	def to_yaml( opts = {} )
-		YAML::quick_emit( nil, opts ) do |out|
-            out.scalar( "tag:yaml.org,2002:int", self.to_s, :plain )
-        end
-	end
-end
-
-class Float
-    yaml_as "tag:yaml.org,2002:float"
-	def to_yaml( opts = {} )
-		YAML::quick_emit( nil, opts ) do |out|
-            str = self.to_s
-            if str == "Infinity"
-                str = ".Inf"
-            elsif str == "-Infinity"
-                str = "-.Inf"
-            elsif str == "NaN"
-                str = ".NaN"
-            end
-            out.scalar( "tag:yaml.org,2002:float", str, :plain )
-        end
-	end
-end
-
-class Rational
-	yaml_as "tag:ruby.yaml.org,2002:object:Rational"
-	def Rational.yaml_new( klass, tag, val )
-		if val.is_a? String
-			Rational( val )
-		else
-			Rational( val['numerator'], val['denominator'] )
-		end
-	end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-			out.map( taguri, nil ) do |map|
-				map.add( 'denominator', denominator )
-				map.add( 'numerator', numerator )
-			end
-		end
-	end
-end
-
-class Complex
-	yaml_as "tag:ruby.yaml.org,2002:object:Complex"
-	def Complex.yaml_new( klass, tag, val )
-		if val.is_a? String
-			Complex( val )
-		else
-			Complex( val['real'], val['image'] )
-		end
-	end
-	def to_yaml( opts = {} )
-		YAML::quick_emit( self, opts ) do |out|
-			out.map( taguri, nil ) do |map|
-				map.add( 'image', imaginary )
-				map.add( 'real', real )
-			end
-		end
-	end
-end
-
-class TrueClass
-    yaml_as "tag:yaml.org,2002:bool#yes"
-	def to_yaml( opts = {} )
-		YAML::quick_emit( nil, opts ) do |out|
-            out.scalar( taguri, "true", :plain )
-        end
-	end
-end
-
-class FalseClass
-    yaml_as "tag:yaml.org,2002:bool#no"
-	def to_yaml( opts = {} )
-		YAML::quick_emit( nil, opts ) do |out|
-            out.scalar( taguri, "false", :plain )
-        end
-	end
-end
-
-class NilClass
-    yaml_as "tag:yaml.org,2002:null"
-	def to_yaml( opts = {} )
-		YAML::quick_emit( nil, opts ) do |out|
-            out.scalar( taguri, "", :plain )
-        end
-	end
-end
-
diff --git a/lib/ruby/1.9/yaml/store.rb b/lib/ruby/1.9/yaml/store.rb
deleted file mode 100644
index e3a8e9fcdd..0000000000
--- a/lib/ruby/1.9/yaml/store.rb
+++ /dev/null
@@ -1,43 +0,0 @@
-#
-# YAML::Store
-#
-require 'yaml'
-require 'pstore'
-
-class YAML::Store < PStore
-  def initialize( *o )
-    @opt = YAML::DEFAULTS.dup
-    if String === o.first
-      super(o.shift)
-    end
-    if o.last.is_a? Hash
-      @opt.update(o.pop)
-    end
-  end
-
-  def dump(table)
-    @table.to_yaml(@opt)
-  end
-
-  def load(content)
-    table = YAML::load(content)
-    if table == false
-      {}
-    else
-      table
-    end
-  end
-
-  def marshal_dump_supports_canonical_option?
-    false
-  end
-
-  EMPTY_MARSHAL_DATA = {}.to_yaml
-  EMPTY_MARSHAL_CHECKSUM = Digest::MD5.digest(EMPTY_MARSHAL_DATA)
-  def empty_marshal_data
-    EMPTY_MARSHAL_DATA
-  end
-  def empty_marshal_checksum
-    EMPTY_MARSHAL_CHECKSUM
-  end
-end
diff --git a/lib/ruby/1.9/yaml/stream.rb b/lib/ruby/1.9/yaml/stream.rb
deleted file mode 100644
index 651a1bbbef..0000000000
--- a/lib/ruby/1.9/yaml/stream.rb
+++ /dev/null
@@ -1,40 +0,0 @@
-module YAML
-
-	#
-	# YAML::Stream -- for emitting many documents
-	#
-	class Stream
-
-		attr_accessor :documents, :options
-
-		def initialize( opts = {} )
-			@options = opts
-			@documents = []
-		end
-
-        def []( i )
-            @documents[ i ]
-        end
-
-		def add( doc )
-			@documents << doc
-		end
-
-		def edit( doc_num, doc )
-			@documents[ doc_num ] = doc
-		end
-
-		def emit( io = nil )
-            # opts = @options.dup
-			# opts[:UseHeader] = true if @documents.length > 1
-            out = YAML.emitter
-            out.reset( io || io2 = StringIO.new )
-            @documents.each { |v|
-                v.to_yaml( out )
-            }
-            io || ( io2.rewind; io2.read )
-		end
-
-	end
-
-end
diff --git a/lib/ruby/1.9/yaml/stringio.rb b/lib/ruby/1.9/yaml/stringio.rb
deleted file mode 100644
index b0fda19e28..0000000000
--- a/lib/ruby/1.9/yaml/stringio.rb
+++ /dev/null
@@ -1,83 +0,0 @@
-#
-# Limited StringIO if no core lib is available
-#
-begin
-require 'stringio'
-rescue LoadError
-    # StringIO based on code by MoonWolf
-    class StringIO
-        def initialize(string="")
-            @string=string
-            @pos=0
-            @eof=(string.size==0)
-        end
-        def pos
-            @pos
-        end
-        def eof
-            @eof
-        end
-        alias eof? eof
-        def readline(rs=$/)
-            if @eof
-                raise EOFError
-            else
-                if p = @string[@pos..-1]=~rs
-                    line = @string[@pos,p+1]
-                else
-                    line = @string[@pos..-1]
-                end
-                @pos+=line.size
-                @eof =true if @pos==@string.size
-                $_ = line
-            end
-        end
-        def rewind
-            seek(0,0)
-        end
-        def seek(offset,whence)
-            case whence
-            when 0
-                @pos=offset
-            when 1
-                @pos+=offset
-            when 2
-                @pos=@string.size+offset
-            end
-            @eof=(@pos>=@string.size)
-            0
-        end
-    end
-
-	#
-	# Class method for creating streams
-	#
-	def YAML.make_stream( io )
-        if String === io
-            io = StringIO.new( io )
-        elsif not IO === io
-            raise YAML::Error, "YAML stream must be an IO or String object."
-        end
-        if YAML::unicode
-            def io.readline
-                YAML.utf_to_internal( readline( @ln_sep ), @utf_encoding )
-            end
-            def io.check_unicode
-                @utf_encoding = YAML.sniff_encoding( read( 4 ) )
-                @ln_sep = YAML.enc_separator( @utf_encoding )
-                seek( -4, IO::SEEK_CUR )
-            end
-		    def io.utf_encoding
-		    	@utf_encoding
-		    end
-            io.check_unicode
-        else
-            def io.utf_encoding
-                :None
-            end
-        end
-        io
-	end
-
-end
-
diff --git a/lib/ruby/1.9/yaml/syck.rb b/lib/ruby/1.9/yaml/syck.rb
deleted file mode 100644
index faf57e8036..0000000000
--- a/lib/ruby/1.9/yaml/syck.rb
+++ /dev/null
@@ -1,19 +0,0 @@
-#
-# YAML::Syck module
-# .. glues syck and yaml.rb together ..
-#
-require 'syck'
-require 'yaml/basenode'
-
-module YAML
-    module Syck
-
-        #
-        # Mixin BaseNode functionality
-        #
-        class Node
-            include YAML::BaseNode
-        end
-
-    end
-end
diff --git a/lib/ruby/1.9/yaml/tag.rb b/lib/ruby/1.9/yaml/tag.rb
deleted file mode 100644
index 5c96dc99e5..0000000000
--- a/lib/ruby/1.9/yaml/tag.rb
+++ /dev/null
@@ -1,91 +0,0 @@
-# -*- mode: ruby; ruby-indent-level: 4; tab-width: 4 -*- vim: sw=4 ts=4
-# $Id: tag.rb 22784 2009-03-06 03:56:38Z nobu $
-#
-# = yaml/tag.rb: methods for associating a taguri to a class.
-#
-# Author:: why the lucky stiff
-#
-module YAML
-    # A dictionary of taguris which map to
-    # Ruby classes.
-    @@tagged_classes = {}
-
-    #
-    # Associates a taguri _tag_ with a Ruby class _cls_.  The taguri is used to give types
-    # to classes when loading YAML.  Taguris are of the form:
-    #
-    #   tag:authorityName,date:specific
-    #
-    # The +authorityName+ is a domain name or email address.  The +date+ is the date the type
-    # was issued in YYYY or YYYY-MM or YYYY-MM-DD format.  The +specific+ is a name for
-    # the type being added.
-    #
-    # For example, built-in YAML types have 'yaml.org' as the +authorityName+ and '2002' as the
-    # +date+.  The +specific+ is simply the name of the type:
-    #
-    #  tag:yaml.org,2002:int
-    #  tag:yaml.org,2002:float
-    #  tag:yaml.org,2002:timestamp
-    #
-    # The domain must be owned by you on the +date+ declared.  If you don't own any domains on the
-    # date you declare the type, you can simply use an e-mail address.
-    #
-    #  tag:why@ruby-lang.org,2004:notes/personal
-    #
-    def YAML.tag_class( tag, cls )
-        if @@tagged_classes.has_key? tag
-            warn "class #{ @@tagged_classes[tag] } held ownership of the #{ tag } tag"
-        end
-        @@tagged_classes[tag] = cls
-    end
-
-    # Returns the complete dictionary of taguris, paired with classes.  The key for
-    # the dictionary is the full taguri.  The value for each key is the class constant
-    # associated to that taguri.
-    #
-    #  YAML.tagged_classes["tag:yaml.org,2002:int"] => Integer
-    #
-    def YAML.tagged_classes
-        @@tagged_classes
-    end
-end
-
-class Module
-    # :stopdoc:
-
-    # Adds a taguri _tag_ to a class, used when dumping or loading the class
-    # in YAML.  See YAML::tag_class for detailed information on typing and
-    # taguris.
-    def yaml_as( tag, sc = true )
-        verbose, $VERBOSE = $VERBOSE, nil
-        class_eval <<-"end;", __FILE__, __LINE__+1
-            attr_writer :taguri
-            def taguri
-                if respond_to? :to_yaml_type
-                    YAML::tagurize( to_yaml_type[1..-1] )
-                else
-                    return @taguri if defined?(@taguri) and @taguri
-                    tag = #{ tag.dump }
-                    if self.class.yaml_tag_subclasses? and self.class != YAML::tagged_classes[tag]
-                        tag = "\#{ tag }:\#{ self.class.yaml_tag_class_name }"
-                    end
-                    tag
-                end
-            end
-            def self.yaml_tag_subclasses?; #{ sc ? 'true' : 'false' }; end
-        end;
-        YAML::tag_class tag, self
-    ensure
-        $VERBOSE = verbose
-    end
-    # Transforms the subclass name into a name suitable for display
-    # in a subclassed tag.
-    def yaml_tag_class_name
-        self.name
-    end
-    # Transforms the subclass name found in the tag into a Ruby
-    # constant name.
-    def yaml_tag_read_class( name )
-        name
-    end
-end
diff --git a/lib/ruby/1.9/yaml/types.rb b/lib/ruby/1.9/yaml/types.rb
deleted file mode 100644
index 60aebc0481..0000000000
--- a/lib/ruby/1.9/yaml/types.rb
+++ /dev/null
@@ -1,192 +0,0 @@
-# -*- mode: ruby; ruby-indent-level: 4 -*- vim: sw=4
-#
-# Classes required by the full core typeset
-#
-
-module YAML
-
-    #
-    # Default private type
-    #
-    class PrivateType
-        def self.tag_subclasses?; false; end
-        verbose, $VERBOSE = $VERBOSE, nil
-        def initialize( type, val )
-            @type_id = type; @value = val
-            @value.taguri = "x-private:#{ @type_id }"
-        end
-        def to_yaml( opts = {} )
-            @value.to_yaml( opts )
-        end
-    ensure
-        $VERBOSE = verbose
-    end
-
-    #
-    # Default domain type
-    #
-    class DomainType
-        def self.tag_subclasses?; false; end
-        verbose, $VERBOSE = $VERBOSE, nil
-        def initialize( domain, type, val )
-            @domain = domain; @type_id = type; @value = val
-            @value.taguri = "tag:#{ @domain }:#{ @type_id }"
-        end
-        def to_yaml( opts = {} )
-            @value.to_yaml( opts )
-        end
-    ensure
-        $VERBOSE = verbose
-    end
-
-    #
-    # Unresolved objects
-    #
-    class Object
-        def self.tag_subclasses?; false; end
-        def to_yaml( opts = {} )
-            YAML::quick_emit( self, opts ) do |out|
-                out.map( "tag:ruby.yaml.org,2002:object:#{ @class }", to_yaml_style ) do |map|
-                    @ivars.each do |k,v|
-                        map.add( k, v )
-                    end
-                end
-            end
-        end
-    end
-
-    #
-    # YAML Hash class to support comments and defaults
-    #
-    class SpecialHash < ::Hash
-        attr_accessor :default
-        def inspect
-            self.default.to_s
-        end
-        def to_s
-            self.default.to_s
-        end
-        def update( h )
-            if YAML::SpecialHash === h
-                @default = h.default if h.default
-            end
-            super( h )
-        end
-        def to_yaml( opts = {} )
-            opts[:DefaultKey] = self.default
-            super( opts )
-        end
-    end
-
-    #
-    # Builtin collection: !omap
-    #
-    class Omap < ::Array
-        yaml_as "tag:yaml.org,2002:omap"
-        def yaml_initialize( tag, val )
-            if Array === val
-                val.each do |v|
-                    if Hash === v
-                        concat( v.to_a )		# Convert the map to a sequence
-                    else
-                        raise YAML::Error, "Invalid !omap entry: " + val.inspect
-                    end
-                end
-            else
-                raise YAML::Error, "Invalid !omap: " + val.inspect
-            end
-            self
-        end
-        def self.[]( *vals )
-            o = Omap.new
-            0.step( vals.length - 1, 2 ) do |i|
-                o[vals[i]] = vals[i+1]
-            end
-            o
-        end
-        def []( k )
-            self.assoc( k ).to_a[1]
-        end
-        def []=( k, *rest )
-            val, set = rest.reverse
-            if ( tmp = self.assoc( k ) ) and not set
-                tmp[1] = val
-            else
-                self << [ k, val ]
-            end
-            val
-        end
-        def has_key?( k )
-            self.assoc( k ) ? true : false
-        end
-        def is_complex_yaml?
-            true
-        end
-        def to_yaml( opts = {} )
-            YAML::quick_emit( self, opts ) do |out|
-                out.seq( taguri, to_yaml_style ) do |seq|
-                    self.each do |v|
-                        seq.add( Hash[ *v ] )
-                    end
-                end
-            end
-        end
-    end
-
-    #
-    # Builtin collection: !pairs
-    #
-    class Pairs < ::Array
-        yaml_as "tag:yaml.org,2002:pairs"
-        def yaml_initialize( tag, val )
-            if Array === val
-                val.each do |v|
-                    if Hash === v
-                        concat( v.to_a )		# Convert the map to a sequence
-                    else
-                        raise YAML::Error, "Invalid !pairs entry: " + val.inspect
-                    end
-                end
-            else
-                raise YAML::Error, "Invalid !pairs: " + val.inspect
-            end
-            self
-        end
-        def self.[]( *vals )
-            p = Pairs.new
-            0.step( vals.length - 1, 2 ) { |i|
-                p[vals[i]] = vals[i+1]
-            }
-            p
-        end
-        def []( k )
-            self.assoc( k ).to_a
-        end
-        def []=( k, val )
-            self << [ k, val ]
-            val
-        end
-        def has_key?( k )
-            self.assoc( k ) ? true : false
-        end
-        def is_complex_yaml?
-            true
-        end
-        def to_yaml( opts = {} )
-            YAML::quick_emit( self, opts ) do |out|
-                out.seq( taguri, to_yaml_style ) do |seq|
-                    self.each do |v|
-                        seq.add( Hash[ *v ] )
-                    end
-                end
-            end
-        end
-    end
-
-    #
-    # Builtin collection: !set
-    #
-    class Set < ::Hash
-        yaml_as "tag:yaml.org,2002:set"
-    end
-end
diff --git a/lib/ruby/1.9/yaml/yamlnode.rb b/lib/ruby/1.9/yaml/yamlnode.rb
deleted file mode 100644
index 8afa142669..0000000000
--- a/lib/ruby/1.9/yaml/yamlnode.rb
+++ /dev/null
@@ -1,54 +0,0 @@
-#
-# YAML::YamlNode class
-#
-require 'yaml/basenode'
-
-module YAML
-
-    #
-    # YAML Generic Model container
-    #
-    class YamlNode
-        include BaseNode
-        attr_accessor :kind, :type_id, :value, :anchor
-        def initialize(t, v)
-            @type_id = t
-            if Hash === v
-                @kind = 'map'
-                @value = {}
-                v.each {|key,val|
-                    @value[key.transform] = [key, val]
-                }
-            elsif Array === v
-                @kind = 'seq'
-                @value = v
-            elsif String === v
-                @kind = 'scalar'
-                @value = v
-            end
-        end
-
-        #
-        # Transform this node fully into a native type
-        #
-        def transform
-            t = nil
-            if @value.is_a? Hash
-                t = {}
-                @value.each { |k,v|
-                    t[ k ] = v[1].transform
-                }
-            elsif @value.is_a? Array
-                t = []
-                @value.each { |v|
-                    t.push v.transform
-                }
-            else
-                t = @value
-            end
-            YAML.transfer_method( @type_id, t )
-        end
-
-    end
-
-end
diff --git a/src/builtin/yaml/basenode.rb b/src/builtin/yaml/basenode.rb
deleted file mode 100644
index 5439903f42..0000000000
--- a/src/builtin/yaml/basenode.rb
+++ /dev/null
@@ -1,216 +0,0 @@
-#
-# YAML::BaseNode class
-#
-require 'yaml/ypath'
-
-module YAML
-
-    #
-    # YAML Generic Model container
-    #
-    module BaseNode
-
-        #
-        # Search for YPath entry and return
-        # qualified nodes.
-        #
-        def select( ypath_str )
-            matches = match_path( ypath_str )
-
-            #
-            # Create a new generic view of the elements selected
-            #
-            if matches
-                result = []
-                matches.each { |m|
-                    result.push m.last
-                }
-                YAML.transfer( 'seq', result )
-            end
-        end
-
-        #
-        # Search for YPath entry and return
-        # transformed nodes.
-        #
-        def select!( ypath_str )
-            matches = match_path( ypath_str )
-
-            #
-            # Create a new generic view of the elements selected
-            #
-            if matches
-                result = []
-                matches.each { |m|
-                    result.push m.last.transform
-                }
-                result
-            end
-        end
-
-        #
-        # Search for YPath entry and return a list of
-        # qualified paths.
-        #
-        def search( ypath_str )
-            matches = match_path( ypath_str )
-
-            if matches
-                matches.collect { |m|
-                    path = []
-                    m.each_index { |i|
-                        path.push m[i] if ( i % 2 ).zero?
-                    }
-                    "/" + path.compact.join( "/" )
-                }
-            end
-        end
-
-        def at( seg )
-            if Hash === @value
-                self[seg]
-            elsif Array === @value and seg =~ /\A\d+\Z/ and @value[seg.to_i]
-                @value[seg.to_i]
-            end
-        end
-
-        #
-        # YPath search returning a complete depth array
-        #
-        def match_path( ypath_str )
-            depth = 0
-            matches = []
-            YPath.each_path( ypath_str ) do |ypath|
-                seg = match_segment( ypath, 0 )
-                matches += seg if seg
-            end
-            matches.uniq
-        end
-
-        #
-        # Search a node for a single YPath segment
-        #
-        def match_segment( ypath, depth )
-            deep_nodes = []
-            seg = ypath.segments[ depth ]
-            if seg == "/"
-                unless String === @value
-                    idx = -1
-                    @value.collect { |v|
-                        idx += 1
-                        if Hash === @value
-                            match_init = [v[0].transform, v[1]]
-                            match_deep = v[1].match_segment( ypath, depth )
-                        else
-                            match_init = [idx, v]
-                            match_deep = v.match_segment( ypath, depth )
-                        end
-                        if match_deep
-                            match_deep.each { |m|
-                                deep_nodes.push( match_init + m )
-                            }
-                        end
-                    }
-                end
-                depth += 1
-                seg = ypath.segments[ depth ]
-            end
-            match_nodes =
-                case seg
-                when "."
-                    [[nil, self]]
-                when ".."
-                    [["..", nil]]
-                when "*"
-                    if @value.is_a? Enumerable
-                        idx = -1
-                        @value.collect { |h|
-                            idx += 1
-                            if Hash === @value
-                                [h[0].transform, h[1]]
-                            else
-                                [idx, h]
-                            end
-                        }
-                    end
-                else
-                    if seg =~ /^"(.*)"$/
-                        seg = $1
-                    elsif seg =~ /^'(.*)'$/
-                        seg = $1
-                    end
-                    if ( v = at( seg ) )
-                        [[ seg, v ]]
-                    end
-                end
-            return deep_nodes unless match_nodes
-            pred = ypath.predicates[ depth ]
-            if pred
-                case pred
-                when /^\.=/
-                    pred = $'   # '
-                    match_nodes.reject! { |n|
-                        n.last.value != pred
-                    }
-                else
-                    match_nodes.reject! { |n|
-                        n.last.at( pred ).nil?
-                    }
-                end
-            end
-            return match_nodes + deep_nodes unless ypath.segments.length > depth + 1
-
-            #puts "DEPTH: #{depth + 1}"
-            deep_nodes = []
-            match_nodes.each { |n|
-                if n[1].is_a? BaseNode
-                    match_deep = n[1].match_segment( ypath, depth + 1 )
-                    if match_deep
-                        match_deep.each { |m|
-                            deep_nodes.push( n + m )
-                        }
-                    end
-                else
-                    deep_nodes = []
-                end
-            }
-            deep_nodes = nil if deep_nodes.length == 0
-            deep_nodes
-        end
-
-        #
-        # We want the node to act like as Hash
-        # if it is.
-        #
-        def []( *key )
-            if Hash === @value
-                v = @value.detect { |k,| k.transform == key.first }
-                v[1] if v
-            elsif Array === @value
-                @value.[]( *key )
-            end
-        end
-
-        def children
-            if Hash === @value
-                @value.values.collect { |c| c[1] }
-            elsif Array === @value
-                @value
-            end
-        end
-
-        def children_with_index
-            if Hash === @value
-                @value.keys.collect { |i| [self[i], i] }
-            elsif Array === @value
-                i = -1; @value.collect { |v| i += 1; [v, i] }
-            end
-        end
-
-        def emit
-            transform.to_yaml
-        end
-    end
-
-end
-
diff --git a/src/builtin/yaml/ypath.rb b/src/builtin/yaml/ypath.rb
deleted file mode 100644
index 81348ca043..0000000000
--- a/src/builtin/yaml/ypath.rb
+++ /dev/null
@@ -1,52 +0,0 @@
-#
-# YAML::YPath
-#
-
-module YAML
-
-    class YPath
-        attr_accessor :segments, :predicates, :flags
-        def initialize( str )
-            @segments = []
-            @predicates = []
-            @flags = nil
-            while str =~ /^\/?(\/|[^\/\[]+)(?:\[([^\]]+)\])?/
-                @segments.push $1
-                @predicates.push $2
-                str = $'
-            end
-            unless str.to_s.empty?
-                @segments += str.split( "/" )
-            end
-            if @segments.length == 0
-                @segments.push "."
-            end
-        end
-        def YPath.each_path( str )
-            #
-            # Find choices
-            #
-            paths = []
-            str = "(#{ str })"
-            while str.sub!( /\(([^()]+)\)/, "\n#{ paths.length }\n" )
-                paths.push $1.split( '|' )
-            end
-
-            #
-            # Construct all possible paths
-            #
-            all = [ str ]
-            ( paths.length - 1 ).downto( 0 ) do |i|
-                all = all.collect do |a|
-                    paths[i].collect do |p|
-                        a.gsub( /\n#{ i }\n/, p )
-                    end
-                end.flatten.uniq
-            end
-            all.collect do |path|
-                yield YPath.new( path )
-            end
-        end
-    end
-
-end
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 7cba07513c..75d8709fe3 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -494,2008 +494,2001 @@ public final class Ruby {
         }
         
         if (processLineEnds) {
             getGlobalVariables().set("$\\", getGlobalVariables().get("$/"));
         }
 
         // we do preand post load outside the "body" versions to pre-prepare
         // and pre-push the dynamic scope we need for lastline
         RuntimeHelpers.preLoad(context, ((RootNode)scriptNode).getStaticScope().getVariables());
 
         try {
             while (RubyKernel.gets(context, getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
                 loop: while (true) { // Used for the 'redo' command
                     try {
                         if (processLineEnds) {
                             getGlobalVariables().get("$_").callMethod(context, "chop!");
                         }
 
                         if (split) {
                             getGlobalVariables().set("$F", getGlobalVariables().get("$_").callMethod(context, "split"));
                         }
 
                         if (script != null) {
                             runScriptBody(script);
                         } else {
                             runInterpreterBody(scriptNode);
                         }
 
                         if (printing) RubyKernel.print(context, getKernel(), new IRubyObject[] {getGlobalVariables().get("$_")});
                         break loop;
                     } catch (JumpException.RedoJump rj) {
                         // do nothing, this iteration restarts
                     } catch (JumpException.NextJump nj) {
                         // recheck condition
                         break loop;
                     } catch (JumpException.BreakJump bj) {
                         // end loop
                         return (IRubyObject) bj.getValue();
                     }
                 }
             }
         } finally {
             RuntimeHelpers.postLoad(context);
         }
         
         return getNil();
     }
 
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      *
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     @Deprecated
     public IRubyObject runNormally(Node scriptNode, boolean unused) {
         return runNormally(scriptNode);
     }
     
     /**
      * Run the specified script without any of the loop-processing wrapper
      * code.
      * 
      * @param scriptNode The root node of the script to be executed
      * bytecode before execution
      * @return The result of executing the script
      */
     public IRubyObject runNormally(Node scriptNode) {
         Script script = null;
         boolean compile = getInstanceConfig().getCompileMode().shouldPrecompileCLI();
         if (compile || config.isShowBytecode()) {
             script = tryCompile(scriptNode, null, new JRubyClassLoader(getJRubyClassLoader()), config.isShowBytecode());
         }
 
         if (script != null) {
             if (config.isShowBytecode()) {
                 return getNil();
             }
             
             return runScript(script);
         } else {
             failForcedCompile(scriptNode);
             
             return runInterpreter(scriptNode);
         }
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled.
      *
      * @param node The node to attempt to compiled
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), false);
     }
 
     /**
      * Try to compile the code associated with the given Node, returning an
      * instance of the successfully-compiled Script or null if the script could
      * not be compiled. This version accepts an ASTInspector instance assumed to
      * have appropriate flags set for compile optimizations, such as to turn
      * on heap-based local variables to share an existing scope.
      *
      * @param node The node to attempt to compiled
      * @param inspector The ASTInspector to use for making optimization decisions
      * @return an instance of the successfully-compiled Script, or null.
      */
     public Script tryCompile(Node node, ASTInspector inspector) {
         return tryCompile(node, null, new JRubyClassLoader(getJRubyClassLoader()), inspector, false);
     }
 
     private void failForcedCompile(Node scriptNode) throws RaiseException {
         if (config.getCompileMode().shouldPrecompileAll()) {
             throw newRuntimeError("could not compile and compile mode is 'force': " + scriptNode.getPosition().getFile());
         }
     }
 
     private void handeCompileError(Node node, Throwable t) {
         if (config.isJitLoggingVerbose() || config.isDebug()) {
             System.err.println("warning: could not compile: " + node.getPosition().getFile() + "; full trace follows");
             t.printStackTrace();
         }
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, boolean dump) {
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
 
         return tryCompile(node, cachedClassName, classLoader, inspector, dump);
     }
 
     private Script tryCompile(Node node, String cachedClassName, JRubyClassLoader classLoader, ASTInspector inspector, boolean dump) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname = JavaNameMangler.mangledFilenameForStartupClasspath(filename);
 
             StandardASMCompiler asmCompiler = null;
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 asmCompiler = new StandardASMCompiler(cachedClassName.replace('.', '/'), filename);
             } else {
                 asmCompiler = new StandardASMCompiler(classname, filename);
             }
             ASTCompiler compiler = config.newCompiler();
             if (dump) {
                 compiler.compileRoot(node, asmCompiler, inspector, false, false);
                 asmCompiler.dumpClass(System.out);
             } else {
                 compiler.compileRoot(node, asmCompiler, inspector, true, false);
             }
 
             if (RubyInstanceConfig.JIT_CODE_CACHE != null && cachedClassName != null) {
                 // save script off to disk
                 String pathName = cachedClassName.replace('.', '/');
                 JITCompiler.saveToCodeCache(this, asmCompiler.getClassByteArray(), "ruby/jit", new File(RubyInstanceConfig.JIT_CODE_CACHE, pathName + ".class"));
             }
             script = (Script)asmCompiler.loadClass(classLoader).newInstance();
 
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (Throwable t) {
             handeCompileError(node, t);
         }
         
         return script;
     }
     
     public IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(context, getTopSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runScriptBody(Script script) {
         ThreadContext context = getCurrentContext();
 
         try {
             return script.__file__(context, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     public IRubyObject runInterpreter(ThreadContext context, Node rootNode, IRubyObject self) {
         assert rootNode != null : "scriptNode is not null";
 
         try {
             if (getInstanceConfig().getCompileMode() == CompileMode.OFFIR) {
                 return Interpreter.interpret(this, rootNode, self);
             } else {
                 return ASTInterpreter.INTERPRET_ROOT(this, context, rootNode, getTopSelf(), Block.NULL_BLOCK);
             }
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     public IRubyObject runInterpreter(Node scriptNode) {
         return runInterpreter(getCurrentContext(), scriptNode, getTopSelf());
     }
 
     /**
      * This is used for the "gets" loop, and we bypass 'load' to use an
      * already-prepared, already-pushed scope for the script body.
      */
     public IRubyObject runInterpreterBody(Node scriptNode) {
         assert scriptNode != null : "scriptNode is not null";
         assert scriptNode instanceof RootNode : "scriptNode is not a RootNode";
 
         return runInterpreter(((RootNode) scriptNode).getBodyNode());
     }
 
     public Parser getParser() {
         return parser;
     }
     
     public BeanManager getBeanManager() {
         return beanManager;
     }
     
     public JITCompiler getJITCompiler() {
         return jitCompiler;
     }
 
     /**
      * @deprecated use #newInstance()
      */
     public static Ruby getDefaultInstance() {
         return newInstance();
     }
     
     @Deprecated
     public static Ruby getCurrentInstance() {
         return null;
     }
     
     @Deprecated
     public static void setCurrentInstance(Ruby runtime) {
     }
     
     public int allocSymbolId() {
         return symbolLastId.incrementAndGet();
     }
     public int allocModuleId() {
         return moduleLastId.incrementAndGet();
     }
     public void addModule(RubyModule module) {
         synchronized (allModules) {
             allModules.add(module);
         }
     }
     public void eachModule(Function1<Object, IRubyObject> func) {
         synchronized (allModules) {
             for (RubyModule module : allModules) {
                 func.apply(module);
             }
         }
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace.
      * 
      * @param name The name of the module
      * @return The module or null if not found
      */
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /**
      * Retrieve the module with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName The name of the module; <em>must</em> be an interned String
      * @return The module or null if not found
      */
     public RubyModule fastGetModule(String internedName) {
         return (RubyModule) objectClass.fastGetConstantAt(internedName);
     }
 
     /** 
      * Retrieve the class with the given name from the Object namespace.
      *
      * @param name The name of the class
      * @return The class
      */
     public RubyClass getClass(String name) {
         return objectClass.getClass(name);
     }
 
     /**
      * Retrieve the class with the given name from the Object namespace. The
      * module name must be an interned string, but this method will be faster
      * than the non-interned version.
      * 
      * @param internedName the name of the class; <em>must</em> be an interned String!
      * @return
      */
     public RubyClass fastGetClass(String internedName) {
         return objectClass.fastGetClass(internedName);
     }
 
     /** 
      * Define a new class under the Object namespace. Roughly equivalent to
      * rb_define_class in MRI.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         return defineClassUnder(name, superClass, allocator, objectClass);
     }
 
     /** 
      * A variation of defineClass that allows passing in an array of subplementary
      * call sites for improving dynamic invocation performance.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @return The new class
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator, CallSite[] callSites) {
         return defineClassUnder(name, superClass, allocator, objectClass, callSites);
     }
 
     /**
      * Define a new class with the given name under the given module or class
      * namespace. Roughly equivalent to rb_define_class_under in MRI.
      * 
      * If the name specified is already bound, its value will be returned if:
      * * It is a class
      * * No new superclass is being defined
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent) {
         return defineClassUnder(name, superClass, allocator, parent, null);
     }
 
     /**
      * A variation of defineClassUnder that allows passing in an array of
      * supplementary call sites to improve dynamic invocation.
      *
      * @param name The name for the new class
      * @param superClass The super class for the new class
      * @param allocator An ObjectAllocator instance that can construct
      * instances of the new class.
      * @param parent The namespace under which to define the new class
      * @param callSites The array of call sites to add
      * @return The new class
      */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent, CallSite[] callSites) {
         IRubyObject classObj = parent.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) {
                 throw newNameError(name + " is already defined", name);
             }
             // If we define a class in Ruby, but later want to allow it to be defined in Java,
             // the allocator needs to be updated
             if (klazz.getAllocator() != allocator) {
                 klazz.setAllocator(allocator);
             }
             return klazz;
         }
         
         boolean parentIsObject = parent == objectClass;
 
         if (superClass == null) {
             String className = parentIsObject ? name : parent.getName() + "::" + name;  
             warnings.warn(ID.NO_SUPER_CLASS, "no super class for `" + className + "', Object assumed", className);
             
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, parent, !parentIsObject, callSites);
     }
 
     /** 
      * Define a new module under the Object namespace. Roughly equivalent to
      * rb_define_module in MRI.
      * 
      * @param name The name of the new module
      * @returns The new module
      */
     public RubyModule defineModule(String name) {
         return defineModuleUnder(name, objectClass);
     }
 
     /**
      * Define a new module with the given name under the given module or
      * class namespace. Roughly equivalent to rb_define_module_under in MRI.
      * 
      * @param name The name of the new module
      * @param parent The class or module namespace under which to define the
      * module
      * @returns The new module
      */
     public RubyModule defineModuleUnder(String name, RubyModule parent) {
         IRubyObject moduleObj = parent.getConstantAt(name);
         
         boolean parentIsObject = parent == objectClass;
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             
             if (parentIsObject) {
                 throw newTypeError(moduleObj.getMetaClass().getName() + " is not a module");
             } else {
                 throw newTypeError(parent.getName() + "::" + moduleObj.getMetaClass().getName() + " is not a module");
             }
         }
 
         return RubyModule.newModule(this, name, parent, !parentIsObject);
     }
 
     /**
      * From Object, retrieve the named module. If it doesn't exist a
      * new module is created.
      * 
      * @param name The name of the module
      * @returns The existing or new module
      */
     public RubyModule getOrCreateModule(String name) {
         IRubyObject module = objectClass.getConstantAt(name);
         if (module == null) {
             module = defineModule(name);
         } else if (getSafeLevel() >= 4) {
             throw newSecurityError("Extending module prohibited.");
         } else if (!module.isModule()) {
             throw newTypeError(name + " is not a Module");
         }
 
         return (RubyModule) module;
     }
 
 
     /** 
      * Retrieve the current safe level.
      * 
      * @see org.jruby.Ruby#setSaveLevel
      */
     public int getSafeLevel() {
         return this.safeLevel;
     }
 
 
     /** 
      * Set the current safe level:
      * 
      * 0 - strings from streams/environment/ARGV are tainted (default)
      * 1 - no dangerous operation by tainted value
      * 2 - process/file operations prohibited
      * 3 - all generated objects are tainted
      * 4 - no global (non-tainted) variable modification/no direct output
      * 
      * The safe level is set using $SAFE in Ruby code. It is not particularly
      * well supported in JRuby.
     */
     public void setSafeLevel(int safeLevel) {
         this.safeLevel = safeLevel;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public void setKCode(KCode kcode) {
         this.kcode = kcode;
     }
 
     public void secure(int level) {
         if (level <= safeLevel) {
             throw newSecurityError("Insecure operation '" + getCurrentContext().getFrameName() + "' at level " + safeLevel);
         }
     }
 
     // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
     // perhaps security methods should find their own centralized home at some point.
     public void checkSafeString(IRubyObject object) {
         if (getSafeLevel() > 0 && object.isTaint()) {
             ThreadContext tc = getCurrentContext();
             if (tc.getFrameName() != null) {
                 throw newSecurityError("Insecure operation - " + tc.getFrameName());
             }
             throw newSecurityError("Insecure operation: -r");
         }
         secure(4);
         if (!(object instanceof RubyString)) {
             throw newTypeError(
                 "wrong argument type " + object.getMetaClass().getName() + " (expected String)");
         }
     }
 
     /** rb_define_global_const
      *
      */
     public void defineGlobalConstant(String name, IRubyObject value) {
         objectClass.defineConstant(name, value);
     }
 
     public boolean isClassDefined(String name) {
         return getModule(name) != null;
     }
 
     /** 
      * This method is called immediately after constructing the Ruby instance.
      * The main thread is prepared for execution, all core classes and libraries
      * are initialized, and any libraries required on the command line are
      * loaded.
      */
     private void init() {
         safeLevel = config.getSafeLevel();
         
         // Construct key services
         loadService = config.createLoadService(this);
         posix = POSIXFactory.getPOSIX(new JRubyPOSIXHandler(this), RubyInstanceConfig.nativeEnabled);
         javaSupport = new JavaSupport(this);
         
         if (RubyInstanceConfig.POOLING_ENABLED) {
             executor = new ThreadPoolExecutor(
                     RubyInstanceConfig.POOL_MIN,
                     RubyInstanceConfig.POOL_MAX,
                     RubyInstanceConfig.POOL_TTL,
                     TimeUnit.SECONDS,
                     new SynchronousQueue<Runnable>(),
                     new DaemonThreadFactory());
         }
         
         // initialize the root of the class hierarchy completely
         initRoot();
 
         // Set up the main thread in thread service
         threadService.initMainThread();
 
         // Get the main threadcontext (gets constructed for us)
         ThreadContext tc = getCurrentContext();
 
         // Construct the top-level execution frame and scope for the main thread
         tc.prepareTopLevel(objectClass, topSelf);
 
         // Initialize all the core classes
         bootstrap();
         
         // Initialize the "dummy" class used as a marker
         dummyClass = new RubyClass(this, classClass);
         dummyClass.freeze(tc);
         
         // Create global constants and variables
         RubyGlobal.createGlobals(tc, this);
 
         // Prepare LoadService and load path
         getLoadService().init(config.loadPaths());
 
         // initialize builtin libraries
         initBuiltins();
         
         // Require in all libraries specified on command line
         for (String scriptName : config.requiredLibraries()) {
             loadService.smartLoad(scriptName);
         }
     }
 
     private void bootstrap() {
         initCore();
         initExceptions();
     }
 
     private void initRoot() {
         boolean oneNine = is1_9();
         // Bootstrap the top of the hierarchy
         if (oneNine) {
             basicObjectClass = RubyClass.createBootstrapClass(this, "BasicObject", null, RubyBasicObject.BASICOBJECT_ALLOCATOR);
             objectClass = RubyClass.createBootstrapClass(this, "Object", basicObjectClass, RubyObject.OBJECT_ALLOCATOR);
         } else {
             objectClass = RubyClass.createBootstrapClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR);
         }
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         if (oneNine) basicObjectClass.setMetaClass(classClass);
         objectClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass;
         if (oneNine) metaClass = basicObjectClass.makeMetaClass(classClass);
         metaClass = objectClass.makeMetaClass(classClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
 
         if (oneNine) RubyBasicObject.createBasicObjectClass(this, basicObjectClass);
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
         
         // set constants now that they're initialized
         if (oneNine) objectClass.setConstant("BasicObject", basicObjectClass);
         objectClass.setConstant("Object", objectClass);
         objectClass.setConstant("Class", classClass);
         objectClass.setConstant("Module", moduleClass);
 
         // Initialize Kernel and include into Object
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // Object is ready, create top self
         topSelf = TopSelfFactory.createTopSelf(this);
         
         // Pre-create all the core classes potentially referenced during startup
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
 
         nilObject = new RubyNil(this);
         for (int i=0; i<NIL_PREFILLED_ARRAY_SIZE; i++) nilPrefilledArray[i] = nilObject;
         singleNilArray = new IRubyObject[] {nilObject};
 
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
     }
 
     private void initEncodings() {
         RubyEncoding.createEncodingClass(this);
         RubyConverter.createConverterClass(this);
         encodingService = new EncodingService(this);
 
         // External should always have a value, but Encoding.external_encoding{,=} will lazily setup
         String encoding = config.getExternalEncoding();
         if (encoding != null && !encoding.equals("")) {
             Encoding loadedEncoding = encodingService.loadEncoding(ByteList.create(encoding));
             if (loadedEncoding == null) throw new MainExitException(1, "unknown encoding name - " + encoding);
             setDefaultExternalEncoding(loadedEncoding);
         } else {
             setDefaultExternalEncoding(USASCIIEncoding.INSTANCE);
         }
         
         encoding = config.getInternalEncoding();
         if (encoding != null && !encoding.equals("")) {
             Encoding loadedEncoding = encodingService.loadEncoding(ByteList.create(encoding));
             if (loadedEncoding == null) throw new MainExitException(1, "unknown encoding name - " + encoding);
             setDefaultInternalEncoding(loadedEncoding);
         }
     }
 
     private void initCore() {
         if (profile.allowClass("Data")) {
             defineClass("Data", objectClass, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         }
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
 
         if (is1_9()) {
             initEncodings();
             RubyRandom.createRandomClass(this);
         }
 
         RubySymbol.createSymbolClass(this);
 
         if (profile.allowClass("ThreadGroup")) {
             RubyThreadGroup.createThreadGroupClass(this);
         }
         if (profile.allowClass("Thread")) {
             RubyThread.createThreadClass(this);
         }
         if (profile.allowClass("Exception")) {
             RubyException.createExceptionClass(this);
         }
 
         if (!is1_9()) {
             if (profile.allowModule("Precision")) {
                 RubyPrecision.createPrecisionModule(this);
             }
         }
 
         if (profile.allowClass("Numeric")) {
             RubyNumeric.createNumericClass(this);
         }
         if (profile.allowClass("Integer")) {
             RubyInteger.createIntegerClass(this);
         }
         if (profile.allowClass("Fixnum")) {
             RubyFixnum.createFixnumClass(this);
         }
 
         if (is1_9()) {
             if (profile.allowClass("Complex")) {
                 RubyComplex.createComplexClass(this);
             }
             if (profile.allowClass("Rational")) {
                 RubyRational.createRationalClass(this);
             }
         }
 
         if (profile.allowClass("Hash")) {
             RubyHash.createHashClass(this);
         }
         if (profile.allowClass("Array")) {
             RubyArray.createArrayClass(this);
         }
         if (profile.allowClass("Float")) {
             RubyFloat.createFloatClass(this);
         }
         if (profile.allowClass("Bignum")) {
             RubyBignum.createBignumClass(this);
         }
         ioClass = RubyIO.createIOClass(this);
 
         if (profile.allowClass("Struct")) {
             RubyStruct.createStructClass(this);
         }
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass, new IRubyObject[]{newString("Tms"), newSymbol("utime"), newSymbol("stime"), newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Binding")) {
             RubyBinding.createBindingClass(this);
         }
         // Math depends on all numeric types
         if (profile.allowModule("Math")) {
             RubyMath.createMathModule(this);
         }
         if (profile.allowClass("Regexp")) {
             RubyRegexp.createRegexpClass(this);
         }
         if (profile.allowClass("Range")) {
             RubyRange.createRangeClass(this);
         }
         if (profile.allowModule("ObjectSpace")) {
             RubyObjectSpace.createObjectSpaceModule(this);
         }
         if (profile.allowModule("GC")) {
             RubyGC.createGCModule(this);
         }
         if (profile.allowClass("Proc")) {
             RubyProc.createProcClass(this);
         }
         if (profile.allowClass("Method")) {
             RubyMethod.createMethodClass(this);
         }
         if (profile.allowClass("MatchData")) {
             RubyMatchData.createMatchDataClass(this);
         }
         if (profile.allowModule("Marshal")) {
             RubyMarshal.createMarshalModule(this);
         }
         if (profile.allowClass("Dir")) {
             RubyDir.createDirClass(this);
         }
         if (profile.allowModule("FileTest")) {
             RubyFileTest.createFileTestModule(this);
         }
         // depends on IO, FileTest
         if (profile.allowClass("File")) {
             RubyFile.createFileClass(this);
         }
         if (profile.allowClass("File::Stat")) {
             RubyFileStat.createFileStatClass(this);
         }
         if (profile.allowModule("Process")) {
             RubyProcess.createProcessModule(this);
         }
         if (profile.allowClass("Time")) {
             RubyTime.createTimeClass(this);
         }
         if (profile.allowClass("UnboundMethod")) {
             RubyUnboundMethod.defineUnboundMethodClass(this);
         }
         if (profile.allowModule("Signal")) {
             RubySignal.createSignal(this);
         }
         if (profile.allowClass("Continuation")) {
             RubyContinuation.createContinuation(this);
         }
     }
 
     public static final int NIL_PREFILLED_ARRAY_SIZE = RubyArray.ARRAY_DEFAULT_SIZE * 8;
     private final IRubyObject nilPrefilledArray[] = new IRubyObject[NIL_PREFILLED_ARRAY_SIZE];
     public IRubyObject[] getNilPrefilledArray() {
         return nilPrefilledArray;
     }
 
     private void initExceptions() {
         standardError = defineClassIfAllowed("StandardError", exceptionClass);
         runtimeError = defineClassIfAllowed("RuntimeError", standardError);
         ioError = defineClassIfAllowed("IOError", standardError);
         scriptError = defineClassIfAllowed("ScriptError", exceptionClass);
         rangeError = defineClassIfAllowed("RangeError", standardError);
         signalException = defineClassIfAllowed("SignalException", exceptionClass);
         
         if (profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
             nameErrorMessage = RubyNameError.createNameErrorMessageClass(this, nameError);            
         }
         if (profile.allowClass("NoMethodError")) {
             noMethodError = RubyNoMethodError.createNoMethodErrorClass(this, nameError);
         }
         if (profile.allowClass("SystemExit")) {
             systemExit = RubySystemExit.createSystemExitClass(this, exceptionClass);
         }
         if (profile.allowClass("LocalJumpError")) {
             localJumpError = RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
         }
         if (profile.allowClass("NativeException")) {
             nativeException = NativeException.createClass(this, runtimeError);
         }
         if (profile.allowClass("SystemCallError")) {
             systemCallError = RubySystemCallError.createSystemCallErrorClass(this, standardError);
         }
 
         fatal = defineClassIfAllowed("Fatal", exceptionClass);
         interrupt = defineClassIfAllowed("Interrupt", signalException);
         typeError = defineClassIfAllowed("TypeError", standardError);
         argumentError = defineClassIfAllowed("ArgumentError", standardError);
         indexError = defineClassIfAllowed("IndexError", standardError);
         stopIteration = defineClassIfAllowed("StopIteration", indexError);
         syntaxError = defineClassIfAllowed("SyntaxError", scriptError);
         loadError = defineClassIfAllowed("LoadError", scriptError);
         notImplementedError = defineClassIfAllowed("NotImplementedError", scriptError);
         securityError = defineClassIfAllowed("SecurityError", standardError);
         noMemoryError = defineClassIfAllowed("NoMemoryError", exceptionClass);
         regexpError = defineClassIfAllowed("RegexpError", standardError);
         eofError = defineClassIfAllowed("EOFError", ioError);
         threadError = defineClassIfAllowed("ThreadError", standardError);
         concurrencyError = defineClassIfAllowed("ConcurrencyError", threadError);
         systemStackError = defineClassIfAllowed("SystemStackError", standardError);
         zeroDivisionError = defineClassIfAllowed("ZeroDivisionError", standardError);
         floatDomainError  = defineClassIfAllowed("FloatDomainError", rangeError);
 
         if (is1_9()) {
             if (profile.allowClass("EncodingError")) {
                 encodingError = defineClass("EncodingError", standardError, standardError.getAllocator());
                 encodingCompatibilityError = defineClassUnder("CompatibilityError", encodingError, encodingError.getAllocator(), encodingClass);
                 invalidByteSequenceError = defineClassUnder("InvalidByteSequenceError", encodingError, encodingError.getAllocator(), encodingClass);
                 undefinedConversionError = defineClassUnder("UndefinedConversionError", encodingError, encodingError.getAllocator(), encodingClass);
                 converterNotFoundError = defineClassUnder("ConverterNotFoundError", encodingError, encodingError.getAllocator(), encodingClass);
                 fiberError = defineClass("FiberError", standardError, standardError.getAllocator());
             }
 
             mathDomainError = defineClassUnder("DomainError", argumentError, argumentError.getAllocator(), mathModule);
             recursiveKey = newSymbol("__recursive_key__");
         }
 
         initErrno();
     }
     
     private RubyClass defineClassIfAllowed(String name, RubyClass superClass) {
 	// TODO: should probably apply the null object pattern for a
 	// non-allowed class, rather than null
         if (superClass != null && profile.allowClass(name)) {
             return defineClass(name, superClass, superClass.getAllocator());
         }
         return null;
     }
 
     private Map<Integer, RubyClass> errnos = new HashMap<Integer, RubyClass>();
 
     public RubyClass getErrno(int n) {
         return errnos.get(n);
     }
 
     /**
      * Create module Errno's Variables.  We have this method since Errno does not have it's
      * own java class.
      */
     private void initErrno() {
         if (profile.allowModule("Errno")) {
             errnoModule = defineModule("Errno");
             try {
                 // define EAGAIN now, so that future EWOULDBLOCK will alias to it
                 // see MRI's error.c and its explicit ordering of Errno definitions.
                 createSysErr(Errno.EAGAIN.value(), Errno.EAGAIN.name());
                 
                 for (Errno e : Errno.values()) {
                     Constant c = (Constant) e;
                     if (Character.isUpperCase(c.name().charAt(0))) {
                         createSysErr(c.value(), c.name());
                     }
                 }
             } catch (Exception e) {
                 // dump the trace and continue
                 // this is currently only here for Android, which seems to have
                 // bugs in its enumeration logic
                 // http://code.google.com/p/android/issues/detail?id=2812
                 e.printStackTrace();
             }
         }
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         if(profile.allowClass(name)) {
             if (errnos.get(i) == null) {
                 RubyClass errno = getErrno().defineClassUnder(name, systemCallError, systemCallError.getAllocator());
                 errnos.put(i, errno);
                 errno.defineConstant("Errno", newFixnum(i));
             } else {
                 // already defined a class for this errno, reuse it (JRUBY-4747)
                 getErrno().setConstant(name, errnos.get(i));
             }
         }
     }
 
     private void initBuiltins() {
         addLazyBuiltin("java.rb", "java", "org.jruby.javasupport.Java");
         addLazyBuiltin("jruby.rb", "jruby", "org.jruby.libraries.JRubyLibrary");
         addLazyBuiltin("jruby/ext.rb", "jruby/ext", "org.jruby.ext.jruby.JRubyExtLibrary");
         addLazyBuiltin("jruby/util.rb", "jruby/util", "org.jruby.ext.jruby.JRubyUtilLibrary");
         addLazyBuiltin("jruby/core_ext.rb", "jruby/core_ext", "org.jruby.ext.jruby.JRubyCoreExtLibrary");
         addLazyBuiltin("jruby/type.rb", "jruby/type", "org.jruby.ext.jruby.JRubyTypeLibrary");
         addLazyBuiltin("jruby/synchronized.rb", "jruby/synchronized", "org.jruby.ext.jruby.JRubySynchronizedLibrary");
         addLazyBuiltin("iconv.jar", "iconv", "org.jruby.libraries.IConvLibrary");
         addLazyBuiltin("nkf.jar", "nkf", "org.jruby.libraries.NKFLibrary");
         addLazyBuiltin("stringio.jar", "stringio", "org.jruby.libraries.StringIOLibrary");
         addLazyBuiltin("strscan.jar", "strscan", "org.jruby.libraries.StringScannerLibrary");
         addLazyBuiltin("zlib.jar", "zlib", "org.jruby.libraries.ZlibLibrary");
         addLazyBuiltin("enumerator.jar", "enumerator", "org.jruby.libraries.EnumeratorLibrary");
         addLazyBuiltin("readline.jar", "readline", "org.jruby.ext.ReadlineService");
         addLazyBuiltin("thread.jar", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("thread.rb", "thread", "org.jruby.libraries.ThreadLibrary");
         addLazyBuiltin("digest.jar", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest.rb", "digest", "org.jruby.libraries.DigestLibrary");
         addLazyBuiltin("digest/md5.jar", "digest/md5", "org.jruby.libraries.MD5");
         addLazyBuiltin("digest/rmd160.jar", "digest/rmd160", "org.jruby.libraries.RMD160");
         addLazyBuiltin("digest/sha1.jar", "digest/sha1", "org.jruby.libraries.SHA1");
         addLazyBuiltin("digest/sha2.jar", "digest/sha2", "org.jruby.libraries.SHA2");
         addLazyBuiltin("bigdecimal.jar", "bigdecimal", "org.jruby.libraries.BigDecimalLibrary");
         addLazyBuiltin("io/wait.jar", "io/wait", "org.jruby.libraries.IOWaitLibrary");
         addLazyBuiltin("etc.jar", "etc", "org.jruby.libraries.EtcLibrary");
         addLazyBuiltin("weakref.rb", "weakref", "org.jruby.ext.WeakRefLibrary");
         addLazyBuiltin("delegate_internal.jar", "delegate_internal", "org.jruby.ext.DelegateLibrary");
         addLazyBuiltin("timeout.rb", "timeout", "org.jruby.ext.Timeout");
         addLazyBuiltin("socket.jar", "socket", "org.jruby.ext.socket.SocketLibrary");
         addLazyBuiltin("rbconfig.rb", "rbconfig", "org.jruby.libraries.RbConfigLibrary");
         addLazyBuiltin("jruby/serialization.rb", "serialization", "org.jruby.libraries.JRubySerializationLibrary");
         addLazyBuiltin("ffi-internal.jar", "ffi-internal", "org.jruby.ext.ffi.FFIService");
         addLazyBuiltin("tempfile.rb", "tempfile", "org.jruby.libraries.TempfileLibrary");
         addLazyBuiltin("fcntl.rb", "fcntl", "org.jruby.libraries.FcntlLibrary");
         addLazyBuiltin("rubinius.jar", "rubinius", "org.jruby.ext.rubinius.RubiniusLibrary");
 
         if (is1_9()) {
             addLazyBuiltin("mathn/complex.jar", "mathn/complex", "org.jruby.ext.mathn.Complex");
             addLazyBuiltin("mathn/rational.jar", "mathn/rational", "org.jruby.ext.mathn.Rational");
             addLazyBuiltin("fiber.rb", "fiber", "org.jruby.libraries.FiberExtLibrary");
             addLazyBuiltin("psych.jar", "psych", "org.jruby.ext.psych.PsychLibrary");
         }
 
         if(RubyInstanceConfig.NATIVE_NET_PROTOCOL) {
             addLazyBuiltin("net/protocol.rb", "net/protocol", "org.jruby.libraries.NetProtocolBufferedIOLibrary");
         }
         
         if (is1_9()) {
             LoadService.reflectedLoad(this, "fiber", "org.jruby.libraries.FiberLibrary", getJRubyClassLoader(), false);
         }
         
         addBuiltinIfAllowed("openssl.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/openssl/stub");
             }
         });
 
         addBuiltinIfAllowed("win32ole.jar", new Library() {
             public void load(Ruby runtime, boolean wrap) throws IOException {
                 runtime.getLoadService().require("jruby/win32ole/stub");
             }
         });
         
-        String[] builtins = {"yaml", 
-                             "yaml/yecht", "yaml/baseemitter", "yaml/basenode", 
-                             "yaml/compat", "yaml/constants", "yaml/dbm", 
-                             "yaml/emitter", "yaml/encoding", "yaml/error", 
-                             "yaml/rubytypes", "yaml/store", "yaml/stream", 
-                             "yaml/stringio", "yaml/tag", "yaml/types", 
-                             "yaml/yamlnode", "yaml/ypath", 
-                             "jsignal_internal", "generator_internal"};
+        String[] builtins = {"jsignal_internal", "generator_internal"};
         for (String library : builtins) {
             addBuiltinIfAllowed(library + ".rb", new BuiltinScript(library));
         }
         
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
 
         if(is1_9()) {
             // see ruby.c's ruby_init_gems function
             loadFile("builtin/prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/prelude.rb"), false);
             if (!config.isDisableGems()) {
                 // NOTE: This has been disabled because gem_prelude is terribly broken.
                 //       We just require 'rubygems' in gem_prelude, at least for now.
                 //defineModule("Gem"); // dummy Gem module for prelude
                 loadFile("builtin/gem_prelude.rb", getJRubyClassLoader().getResourceAsStream("builtin/gem_prelude.rb"), false);
             }
         }
 
         getLoadService().require("enumerator");
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getClassLoader()));
     }
 
     private void addBuiltinIfAllowed(String name, Library lib) {
         if(profile.allowBuiltin(name)) {
             loadService.addBuiltinLibrary(name,lib);
         }
     }
 
     public Object getRespondToMethod() {
         return respondToMethod;
     }
 
     public void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
 
     /** Getter for property rubyTopSelf.
      * @return Value of property rubyTopSelf.
      */
     public IRubyObject getTopSelf() {
         return topSelf;
     }
 
     public void setCurrentDirectory(String dir) {
         currentDirectory = dir;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setCurrentLine(int line) {
         currentLine = line;
     }
 
     public int getCurrentLine() {
         return currentLine;
     }
 
     public void setArgsFile(IRubyObject argsFile) {
         this.argsFile = argsFile;
     }
 
     public IRubyObject getArgsFile() {
         return argsFile;
     }
     
     public RubyModule getEtc() {
         return etcModule;
     }
     
     public void setEtc(RubyModule etcModule) {
         this.etcModule = etcModule;
     }
 
     public RubyClass getObject() {
         return objectClass;
     }
 
     public RubyClass getBasicObject() {
         return basicObjectClass;
     }
 
     public RubyClass getModule() {
         return moduleClass;
     }
 
     public RubyClass getClassClass() {
         return classClass;
     }
     
     public RubyModule getKernel() {
         return kernelModule;
     }
     void setKernel(RubyModule kernelModule) {
         this.kernelModule = kernelModule;
     }
 
     public DynamicMethod getPrivateMethodMissing() {
         return privateMethodMissing;
     }
     public void setPrivateMethodMissing(DynamicMethod method) {
         privateMethodMissing = method;
     }
     public DynamicMethod getProtectedMethodMissing() {
         return protectedMethodMissing;
     }
     public void setProtectedMethodMissing(DynamicMethod method) {
         protectedMethodMissing = method;
     }
     public DynamicMethod getVariableMethodMissing() {
         return variableMethodMissing;
     }
     public void setVariableMethodMissing(DynamicMethod method) {
         variableMethodMissing = method;
     }
     public DynamicMethod getSuperMethodMissing() {
         return superMethodMissing;
     }
     public void setSuperMethodMissing(DynamicMethod method) {
         superMethodMissing = method;
     }
     public DynamicMethod getNormalMethodMissing() {
         return normalMethodMissing;
     }
     public void setNormalMethodMissing(DynamicMethod method) {
         normalMethodMissing = method;
     }
     public DynamicMethod getDefaultMethodMissing() {
         return defaultMethodMissing;
     }
     public void setDefaultMethodMissing(DynamicMethod method) {
         defaultMethodMissing = method;
     }
     
     public RubyClass getDummy() {
         return dummyClass;
     }
 
     public RubyModule getComparable() {
         return comparableModule;
     }
     void setComparable(RubyModule comparableModule) {
         this.comparableModule = comparableModule;
     }    
 
     public RubyClass getNumeric() {
         return numericClass;
     }
     void setNumeric(RubyClass numericClass) {
         this.numericClass = numericClass;
     }    
 
     public RubyClass getFloat() {
         return floatClass;
     }
     void setFloat(RubyClass floatClass) {
         this.floatClass = floatClass;
     }
     
     public RubyClass getInteger() {
         return integerClass;
     }
     void setInteger(RubyClass integerClass) {
         this.integerClass = integerClass;
     }    
     
     public RubyClass getFixnum() {
         return fixnumClass;
     }
     void setFixnum(RubyClass fixnumClass) {
         this.fixnumClass = fixnumClass;
     }
 
     public RubyClass getComplex() {
         return complexClass;
     }
     void setComplex(RubyClass complexClass) {
         this.complexClass = complexClass;
     }
 
     public RubyClass getRational() {
         return rationalClass;
     }
     void setRational(RubyClass rationalClass) {
         this.rationalClass = rationalClass;
     }
 
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     void setEnumerable(RubyModule enumerableModule) {
         this.enumerableModule = enumerableModule;
     }
 
     public RubyClass getEnumerator() {
         return enumeratorClass;
     }
     void setEnumerator(RubyClass enumeratorClass) {
         this.enumeratorClass = enumeratorClass;
     }
 
     public RubyClass getYielder() {
         return yielderClass;
     }
     void setYielder(RubyClass yielderClass) {
         this.yielderClass = yielderClass;
     }
 
     public RubyClass getString() {
         return stringClass;
     }
     void setString(RubyClass stringClass) {
         this.stringClass = stringClass;
     }
 
     public RubyClass getEncoding() {
         return encodingClass;
     }
     void setEncoding(RubyClass encodingClass) {
         this.encodingClass = encodingClass;
     }
 
     public RubyClass getConverter() {
         return converterClass;
     }
     void setConverter(RubyClass converterClass) {
         this.converterClass = converterClass;
     }
 
     public RubyClass getSymbol() {
         return symbolClass;
     }
     void setSymbol(RubyClass symbolClass) {
         this.symbolClass = symbolClass;
     }
 
     public RubyClass getArray() {
         return arrayClass;
     }    
     void setArray(RubyClass arrayClass) {
         this.arrayClass = arrayClass;
     }
 
     public RubyClass getHash() {
         return hashClass;
     }
     void setHash(RubyClass hashClass) {
         this.hashClass = hashClass;
     }
 
     public RubyClass getRange() {
         return rangeClass;
     }
     void setRange(RubyClass rangeClass) {
         this.rangeClass = rangeClass;
     }
 
     /** Returns the "true" instance from the instance pool.
      * @return The "true" instance.
      */
     public RubyBoolean getTrue() {
         return trueObject;
     }
 
     /** Returns the "false" instance from the instance pool.
      * @return The "false" instance.
      */
     public RubyBoolean getFalse() {
         return falseObject;
     }
 
     /** Returns the "nil" singleton instance.
      * @return "nil"
      */
     public IRubyObject getNil() {
         return nilObject;
     }
 
     public IRubyObject[] getSingleNilArray() {
         return singleNilArray;
     }
 
     public RubyClass getNilClass() {
         return nilClass;
     }
     void setNilClass(RubyClass nilClass) {
         this.nilClass = nilClass;
     }
 
     public RubyClass getTrueClass() {
         return trueClass;
     }
     void setTrueClass(RubyClass trueClass) {
         this.trueClass = trueClass;
     }
 
     public RubyClass getFalseClass() {
         return falseClass;
     }
     void setFalseClass(RubyClass falseClass) {
         this.falseClass = falseClass;
     }
 
     public RubyClass getProc() {
         return procClass;
     }
     void setProc(RubyClass procClass) {
         this.procClass = procClass;
     }
 
     public RubyClass getBinding() {
         return bindingClass;
     }
     void setBinding(RubyClass bindingClass) {
         this.bindingClass = bindingClass;
     }
 
     public RubyClass getMethod() {
         return methodClass;
     }
     void setMethod(RubyClass methodClass) {
         this.methodClass = methodClass;
     }    
 
     public RubyClass getUnboundMethod() {
         return unboundMethodClass;
     }
     void setUnboundMethod(RubyClass unboundMethodClass) {
         this.unboundMethodClass = unboundMethodClass;
     }    
 
     public RubyClass getMatchData() {
         return matchDataClass;
     }
     void setMatchData(RubyClass matchDataClass) {
         this.matchDataClass = matchDataClass;
     }    
 
     public RubyClass getRegexp() {
         return regexpClass;
     }
     void setRegexp(RubyClass regexpClass) {
         this.regexpClass = regexpClass;
     }    
 
     public RubyClass getTime() {
         return timeClass;
     }
     void setTime(RubyClass timeClass) {
         this.timeClass = timeClass;
     }    
 
     public RubyModule getMath() {
         return mathModule;
     }
     void setMath(RubyModule mathModule) {
         this.mathModule = mathModule;
     }    
 
     public RubyModule getMarshal() {
         return marshalModule;
     }
     void setMarshal(RubyModule marshalModule) {
         this.marshalModule = marshalModule;
     }    
 
     public RubyClass getBignum() {
         return bignumClass;
     }
     void setBignum(RubyClass bignumClass) {
         this.bignumClass = bignumClass;
     }    
 
     public RubyClass getDir() {
         return dirClass;
     }
     void setDir(RubyClass dirClass) {
         this.dirClass = dirClass;
     }    
 
     public RubyClass getFile() {
         return fileClass;
     }
     void setFile(RubyClass fileClass) {
         this.fileClass = fileClass;
     }    
 
     public RubyClass getFileStat() {
         return fileStatClass;
     }
     void setFileStat(RubyClass fileStatClass) {
         this.fileStatClass = fileStatClass;
     }    
 
     public RubyModule getFileTest() {
         return fileTestModule;
     }
     void setFileTest(RubyModule fileTestModule) {
         this.fileTestModule = fileTestModule;
     }
     
     public RubyClass getIO() {
         return ioClass;
     }
     void setIO(RubyClass ioClass) {
         this.ioClass = ioClass;
     }    
 
     public RubyClass getThread() {
         return threadClass;
     }
     void setThread(RubyClass threadClass) {
         this.threadClass = threadClass;
     }    
 
     public RubyClass getThreadGroup() {
         return threadGroupClass;
     }
     void setThreadGroup(RubyClass threadGroupClass) {
         this.threadGroupClass = threadGroupClass;
     }
     
     public RubyThreadGroup getDefaultThreadGroup() {
         return defaultThreadGroup;
     }
     void setDefaultThreadGroup(RubyThreadGroup defaultThreadGroup) {
         this.defaultThreadGroup = defaultThreadGroup;
     }
 
     public RubyClass getContinuation() {
         return continuationClass;
     }
     void setContinuation(RubyClass continuationClass) {
         this.continuationClass = continuationClass;
     }    
 
     public RubyClass getStructClass() {
         return structClass;
     }
     void setStructClass(RubyClass structClass) {
         this.structClass = structClass;
     }    
 
     public IRubyObject getTmsStruct() {
         return tmsStruct;
     }
     void setTmsStruct(RubyClass tmsStruct) {
         this.tmsStruct = tmsStruct;
     }
     
     public IRubyObject getPasswdStruct() {
         return passwdStruct;
     }
     void setPasswdStruct(RubyClass passwdStruct) {
         this.passwdStruct = passwdStruct;
     }
 
     public IRubyObject getGroupStruct() {
         return groupStruct;
     }
     void setGroupStruct(RubyClass groupStruct) {
         this.groupStruct = groupStruct;
     }
 
     public RubyModule getGC() {
         return gcModule;
     }
     void setGC(RubyModule gcModule) {
         this.gcModule = gcModule;
     }    
 
     public RubyModule getObjectSpaceModule() {
         return objectSpaceModule;
     }
     void setObjectSpaceModule(RubyModule objectSpaceModule) {
         this.objectSpaceModule = objectSpaceModule;
     }    
 
     public RubyModule getProcess() {
         return processModule;
     }
     void setProcess(RubyModule processModule) {
         this.processModule = processModule;
     }    
 
     public RubyClass getProcStatus() {
         return procStatusClass; 
     }
     void setProcStatus(RubyClass procStatusClass) {
         this.procStatusClass = procStatusClass;
     }
     
     public RubyModule getProcUID() {
         return procUIDModule;
     }
     void setProcUID(RubyModule procUIDModule) {
         this.procUIDModule = procUIDModule;
     }
     
     public RubyModule getProcGID() {
         return procGIDModule;
     }
     void setProcGID(RubyModule procGIDModule) {
         this.procGIDModule = procGIDModule;
     }
     
     public RubyModule getProcSysModule() {
         return procSysModule;
     }
     void setProcSys(RubyModule procSysModule) {
         this.procSysModule = procSysModule;
     }
 
     public RubyModule getPrecision() {
         return precisionModule;
     }
     void setPrecision(RubyModule precisionModule) {
         this.precisionModule = precisionModule;
     }
 
     public RubyModule getErrno() {
         return errnoModule;
     }
 
     public RubyClass getException() {
         return exceptionClass;
     }
     void setException(RubyClass exceptionClass) {
         this.exceptionClass = exceptionClass;
     }
 
     public RubyClass getNameError() {
         return nameError;
     }
 
     public RubyClass getNameErrorMessage() {
         return nameErrorMessage;
     }
 
     public RubyClass getNoMethodError() {
         return noMethodError;
     }
 
     public RubyClass getSignalException() {
         return signalException;
     }
 
     public RubyClass getRangeError() {
         return rangeError;
     }
 
     public RubyClass getSystemExit() {
         return systemExit;
     }
 
     public RubyClass getLocalJumpError() {
         return localJumpError;
     }
 
     public RubyClass getNativeException() {
         return nativeException;
     }
 
     public RubyClass getSystemCallError() {
         return systemCallError;
     }
 
     public RubyClass getFatal() {
         return fatal;
     }
     
     public RubyClass getInterrupt() {
         return interrupt;
     }
     
     public RubyClass getTypeError() {
         return typeError;
     }
 
     public RubyClass getArgumentError() {
         return argumentError;
     }
 
     public RubyClass getIndexError() {
         return indexError;
     }
 
     public RubyClass getStopIteration() {
         return stopIteration;
     }
 
     public RubyClass getSyntaxError() {
         return syntaxError;
     }
 
     public RubyClass getStandardError() {
         return standardError;
     }
     
     public RubyClass getRuntimeError() {
         return runtimeError;
     }
     
     public RubyClass getIOError() {
         return ioError;
     }
 
     public RubyClass getLoadError() {
         return loadError;
     }
 
     public RubyClass getNotImplementedError() {
         return notImplementedError;
     }
 
     public RubyClass getSecurityError() {
         return securityError;
     }
 
     public RubyClass getNoMemoryError() {
         return noMemoryError;
     }
 
     public RubyClass getRegexpError() {
         return regexpError;
     }
 
     public RubyClass getEOFError() {
         return eofError;
     }
 
     public RubyClass getThreadError() {
         return threadError;
     }
 
     public RubyClass getConcurrencyError() {
         return concurrencyError;
     }
 
     public RubyClass getSystemStackError() {
         return systemStackError;
     }
 
     public RubyClass getZeroDivisionError() {
         return zeroDivisionError;
     }
 
     public RubyClass getFloatDomainError() {
         return floatDomainError;
     }
 
     public RubyClass getMathDomainError() {
         return mathDomainError;
     }
 
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
     }
 
     public RubyClass getConverterNotFoundError() {
         return converterNotFoundError;
     }
 
     public RubyClass getFiberError() {
         return fiberError;
     }
 
     public RubyClass getUndefinedConversionError() {
         return undefinedConversionError;
     }
 
     public RubyClass getInvalidByteSequenceError() {
         return invalidByteSequenceError;
     }
 
     public RubyClass getRandomClass() {
         return randomClass;
     }
 
     public void setRandomClass(RubyClass randomClass) {
         this.randomClass = randomClass;
     }
 
     private RubyHash charsetMap;
     public RubyHash getCharsetMap() {
         if (charsetMap == null) charsetMap = new RubyHash(this);
         return charsetMap;
     }
 
     /** Getter for property isVerbose.
      * @return Value of property isVerbose.
      */
     public IRubyObject getVerbose() {
         return verboseValue;
     }
 
     public boolean isVerbose() {
         return verbose;
     }
 
     public boolean warningsEnabled() {
         return warningsEnabled;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose.isTrue();
         this.verboseValue = verbose;
         warningsEnabled = !verbose.isNil();
     }
 
     /** Getter for property isDebug.
      * @return Value of property isDebug.
      */
     public IRubyObject getDebug() {
         return debug ? trueObject : falseObject;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     /** Setter for property isDebug.
      * @param debug New value of property isDebug.
      */
     public void setDebug(IRubyObject debug) {
         this.debug = debug.isTrue();
     }
 
     public JavaSupport getJavaSupport() {
         return javaSupport;
     }
 
     public static ClassLoader getClassLoader() {
         // we try to get the classloader that loaded JRuby, falling back on System
         ClassLoader loader = Ruby.class.getClassLoader();
         if (loader == null) {
             loader = ClassLoader.getSystemClassLoader();
         }
         
         return loader;
     }
 
     public synchronized JRubyClassLoader getJRubyClassLoader() {
         // FIXME: Get rid of laziness and handle restricted access elsewhere
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null) {
             jrubyClassLoader = new JRubyClassLoader(config.getLoader());
         }
         
         return jrubyClassLoader;
     }
 
     /** Defines a global variable
      */
     public void defineVariable(final GlobalVariable variable) {
         globalVariables.define(variable.name(), new IAccessor() {
             public IRubyObject getValue() {
                 return variable.get();
             }
 
             public IRubyObject setValue(IRubyObject newValue) {
                 return variable.set(newValue);
             }
         });
     }
 
     /** defines a readonly global variable
      *
      */
     public void defineReadonlyVariable(String name, IRubyObject value) {
         globalVariables.defineReadonly(name, new ValueAccessor(value));
     }
 
     public Node parseFile(InputStream in, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addLoadParse();
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 0, false, true, false, config));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
     }
 
 
     public ThreadService getThreadService() {
         return threadService;
     }
 
     public ThreadContext getCurrentContext() {
         return threadService.getCurrentContext();
     }
 
     /**
      * Returns the loadService.
      * @return ILoadService
      */
     public LoadService getLoadService() {
         return loadService;
     }
 
     public Encoding getDefaultInternalEncoding() {
         return defaultInternalEncoding;
     }
 
     public void setDefaultInternalEncoding(Encoding defaultInternalEncoding) {
         this.defaultInternalEncoding = defaultInternalEncoding;
     }
 
     public Encoding getDefaultExternalEncoding() {
         return defaultExternalEncoding;
     }
 
     public void setDefaultExternalEncoding(Encoding defaultExternalEncoding) {
         this.defaultExternalEncoding = defaultExternalEncoding;
     }
 
     public EncodingService getEncodingService() {
         return encodingService;
     }
 
     public RubyWarnings getWarnings() {
         return warnings;
     }
 
     public PrintStream getErrorStream() {
         // FIXME: We can't guarantee this will always be a RubyIO...so the old code here is not safe
         /*java.io.OutputStream os = ((RubyIO) getGlobalVariables().get("$stderr")).getOutStream();
         if(null != os) {
             return new PrintStream(os);
         } else {
             return new PrintStream(new org.jruby.util.SwallowingOutputStream());
         }*/
         return new PrintStream(new IOOutputStream(getGlobalVariables().get("$stderr")));
     }
 
     public InputStream getInputStream() {
         return new IOInputStream(getGlobalVariables().get("$stdin"));
     }
 
     public PrintStream getOutputStream() {
         return new PrintStream(new IOOutputStream(getGlobalVariables().get("$stdout")));
     }
 
     public RubyModule getClassFromPath(String path) {
         RubyModule c = getObject();
         if (path.length() == 0 || path.charAt(0) == '#') {
             throw newTypeError("can't retrieve anonymous class " + path);
         }
         int pbeg = 0, p = 0;
         for(int l=path.length(); p<l; ) {
             while(p<l && path.charAt(p) != ':') {
                 p++;
             }
             String str = path.substring(pbeg, p);
 
             if(p<l && path.charAt(p) == ':') {
                 if(p+1 < l && path.charAt(p+1) != ':') {
                     throw newTypeError("undefined class/module " + path.substring(pbeg,p));
                 }
                 p += 2;
                 pbeg = p;
             }
 
             IRubyObject cc = c.getConstant(str);
             if(!(cc instanceof RubyModule)) {
                 throw newTypeError("" + path + " does not refer to class/module");
             }
             c = (RubyModule)cc;
         }
         return c;
     }
 
     /** Prints an error with backtrace to the error stream.
      *
      * MRI: eval.c - error_print()
      *
      */
     public void printError(RubyException excp) {
         if (excp == null || excp.isNil()) {
             return;
         }
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(RubyInstanceConfig.TRACE_TYPE.printBacktrace(excp));
     }
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(scriptName);
             context.preNodeEval(objectClass, self, scriptName);
 
             runInterpreter(context, parseFile(in, scriptName, null), self);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
 
             Script script = null;
             String className = null;
 
             try {
                 // read full contents of file, hash it, and try to load that class first
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 byte[] buffer = new byte[1024];
                 int num;
                 while ((num = in.read(buffer)) > -1) {
                     baos.write(buffer, 0, num);
                 }
                 buffer = baos.toByteArray();
                 String hash = JITCompiler.getHashForBytes(buffer);
                 className = JITCompiler.RUBY_JIT_PREFIX + ".FILE_" + hash;
 
                 // FIXME: duplicated from ClassCache
                 Class contents;
                 try {
                     contents = jrubyClassLoader.loadClass(className);
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("found jitted code for " + filename + " at class: " + className);
                     }
                     script = (Script)contents.newInstance();
                     readStream = new ByteArrayInputStream(buffer);
                 } catch (ClassNotFoundException cnfe) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("no jitted code in classloader for file " + filename + " at class: " + className);
                     }
                 } catch (InstantiationException ie) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 } catch (IllegalAccessException iae) {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 }
             } catch (IOException ioe) {
                 // TODO: log something?
             }
 
             // script was not found in cache above, so proceed to compile
             Node scriptNode = parseFile(readStream, filename, null);
diff --git a/src/org/jruby/util/IOOutputStream.java b/src/org/jruby/util/IOOutputStream.java
index 766cd7cd7d..2ed6f2faff 100644
--- a/src/org/jruby/util/IOOutputStream.java
+++ b/src/org/jruby/util/IOOutputStream.java
@@ -1,117 +1,116 @@
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
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
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
 package org.jruby.util;
 
 import java.io.OutputStream;
 import java.io.IOException;
 import org.jruby.RubyIO;
 import org.jruby.RubyString;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.MethodIndex;
 
 /**
  * This class wraps a IRubyObject in an OutputStream. Depending on which messages
  * the IRubyObject answers to, it will have different functionality.
  * 
  * The point is that the IRubyObject could exhibit duck typing, in the style of IO versus StringIO, for example.
  *
  * At the moment, the only functionality supported is writing, and the only requirement on the io-object is
  * that it responds to write() and close() like IO.
  * 
  * @author <a href="mailto:Ola.Bini@ki.se">Ola Bini</a>
  */
 public class IOOutputStream extends OutputStream {
     private final IRubyObject io;
     private final OutputStream out;
     private final CallSite writeAdapter;
     private static final CallSite closeAdapter = MethodIndex.getFunctionalCallSite("close");
 
     /**
      * Creates a new OutputStream with the object provided.
      *
      * @param io the ruby object
      */
     public IOOutputStream(final IRubyObject io, boolean checkAppend, boolean verifyCanWrite) {
         this.io = io;
         CallSite writeSite = MethodIndex.getFunctionalCallSite("write");
         if (io.respondsTo("write")) {
             writeAdapter = writeSite;
         } else if (checkAppend && io.respondsTo("<<")) {
             writeAdapter = MethodIndex.getFunctionalCallSite("<<");
         } else if (verifyCanWrite) {
-            throw new IllegalArgumentException(
-                    "Object: " + io + " is not a legal argument to this wrapper, " +
+            throw io.getRuntime().newArgumentError("Object: " + io + " is not a legal argument to this wrapper, " +
                         "cause it doesn't respond to \"write\".");
         } else {
             writeAdapter = writeSite;
         }
         this.out = io instanceof RubyIO && !((RubyIO)io).isClosed() && ((RubyIO)io).isBuiltin("write") ? ((RubyIO) io).getOutStream() : null;
     }
 
     /**
      * Creates a new OutputStream with the object provided.
      *
      * @param io the ruby object
      */
     public IOOutputStream(final IRubyObject io) {
         this(io, true, true);
     }
 
     public void write(final int bite) throws IOException {
         if (out != null) {
             out.write(bite);
         } else {
             writeAdapter.call(io.getRuntime().getCurrentContext(), io, io,
                     RubyString.newStringLight(io.getRuntime(), new ByteList(new byte[]{(byte)bite},false)));
         }
     }
 
     @Override
     public void write(final byte[] b) throws IOException {
         write(b,0,b.length);
     }
 
     @Override
     public void write(final byte[] b,final int off, final int len) throws IOException {
         if (out != null) {
             out.write(b, off, len);
         } else {
             writeAdapter.call(io.getRuntime().getCurrentContext(), io, io, RubyString.newStringLight(io.getRuntime(), new ByteList(b, off, len, false)));
         }
     }
     
     @Override
     public void close() throws IOException {
         if (out != null) {
             out.close();
         } else if (io.respondsTo("close")) {
             closeAdapter.call(io.getRuntime().getCurrentContext(), io, io);
         }
     }
 }
diff --git a/tool/globals_1_8_7.rb b/tool/globals_1_8_7.rb
index fdbab2f0a3..207f8a498b 100644
--- a/tool/globals_1_8_7.rb
+++ b/tool/globals_1_8_7.rb
@@ -1,115 +1,111 @@
 STDLIB_FILES = %w[
   English.rb
   Env.rb
   README
   abbrev.rb
   base64.rb
   benchmark.rb
   cgi
   cgi-lib.rb
   cgi.rb
   complex.rb
   csv.rb
   date
   date.rb
   date2.rb
   debug.rb
   delegate.rb
   dl.rb
   drb
   drb.rb
   e2mmap.rb
   erb.rb
   eregex.rb
   fileutils.rb
   finalize.rb
   find.rb
   forwardable.rb
   ftools.rb
   generator.rb
   getoptlong.rb
   getopts.rb
   gserver.rb
   importenv.rb
   ipaddr.rb
   irb
   irb.rb
   jcode.rb
   logger.rb
   mailread.rb
   mathn.rb
   matrix.rb
   mkmf.rb
   monitor.rb
   mutex_m.rb
   net
   observer.rb
   open-uri.rb
   open3.rb
   optparse
   optparse.rb
   ostruct.rb
   parsearg.rb
   parsedate.rb
   pathname.rb
   ping.rb
   pp.rb
   prettyprint.rb
   profile.rb
   profiler.rb
   pstore.rb
   racc
   rational.rb
   rdoc
   readbytes.rb
   resolv-replace.rb
   resolv.rb
   rexml
   rinda
   rss
   rss.rb
   rubyunit.rb
   runit
   scanf.rb
   securerandom.rb
   set.rb
   shell
   shell.rb
   shellwords.rb
   singleton.rb
   soap
   sync.rb
   test
   thread.rb
   thwait.rb
   time.rb
   tmpdir.rb
   tracer.rb
   tsort.rb
   un.rb
   uri
   uri.rb
   webrick
   webrick.rb
   wsdl
   xmlrpc
   xsd
+  yaml.rb
+  yaml
 ]
 
 EXT_FILES = {
   'ext/bigdecimal/lib/bigdecimal' => 'bigdecimal',
   'ext/dl/lib/dl' => 'dl',
   'ext/pty/lib/expect.rb' => 'expect.rb',
   'ext/io/wait/lib/nonblock.rb' => 'io/nonblock.rb',
   'ext/nkf/lib/kconv.rb' => 'kconv.rb',
   'ext/digest/lib/md5.rb' => 'md5.rb',
   'ext/digest/lib/sha1.rb' => 'sha1.rb',
   'ext/digest/sha2/lib/sha2.rb' => 'digest/sha2.rb',
   'ext/Win32API/lib/win32' => 'win32'
 }
-
-# yaml files go into src/builtin for jruby
-YAML_FILES = %w[
-  yaml.rb
-  yaml
-]
diff --git a/tool/globals_1_9_2.rb b/tool/globals_1_9_2.rb
index fb8046f15a..16a7243c8e 100644
--- a/tool/globals_1_9_2.rb
+++ b/tool/globals_1_9_2.rb
@@ -1,106 +1,101 @@
 STDLIB_FILES = %w[
   English.rb
   README
   abbrev.rb
   base64.rb
   benchmark.rb
   cgi
   cgi.rb
   cmath.rb
   complex.rb
   csv.rb
   date
   date.rb
   debug.rb
   delegate.rb
   dl.rb
   drb
   drb.rb
   e2mmap.rb
   erb.rb
   fileutils.rb
   find.rb
   forwardable.rb
   getoptlong.rb
   gserver.rb
   ipaddr.rb
   irb
   irb.rb
   logger.rb
   mathn.rb
   matrix.rb
   minitest
   mkmf.rb
   monitor.rb
   mutex_m.rb
   net
   observer.rb
   open-uri.rb
   open3.rb
   optparse
   optparse.rb
   ostruct.rb
   pathname.rb
   pp.rb
   prettyprint.rb
   prime.rb
   profile.rb
   profiler.rb
   pstore.rb
   racc
   rake
   rake.rb
   rational.rb
   rbconfig
   rdoc
   rdoc.rb
   resolv-replace.rb
   resolv.rb
   rexml
   rinda
   rss
   rss.rb
   scanf.rb
   securerandom.rb
   set.rb
   shell
   shell.rb
   shellwords.rb
   singleton.rb
   sync.rb
   test
   thread.rb
   thwait.rb
   time.rb
   tmpdir.rb
   tracer.rb
   tsort.rb
   un.rb
   uri
   uri.rb
   webrick
   webrick.rb
   xmlrpc
+  yaml.rb
+  yaml
 ]
 
 EXT_FILES = {
   'ext/bigdecimal/lib/bigdecimal' => 'bigdecimal',
   'ext/dl/lib/dl' => 'dl',
   'ext/pty/lib/expect.rb' => 'expect.rb',
   'ext/nkf/lib/kconv.rb' => 'kconv.rb',
   'ext/digest/lib/digest' => 'digest',
   'ext/digest/lib/digest.rb' => 'digest.rb',
   'ext/digest/sha2/lib/sha2.rb' => 'sha2.rb',
   'ext/dl/win32/lib/win32' => 'win32',
   'ext/psych/lib/psych.rb' => 'psych.rb',
   'ext/psych/lib/psych' => 'psych',
   'ext/ripper/lib/ripper.rb' => 'ripper.rb',
   'ext/ripper/lib/ripper' => 'ripper'
 }
-
-# yaml files go into src/builtin for jruby
-# disabled; need to reconcile 1.8 and 1.9 yaml
-#YAML_FILES = %w[
-#  yaml.rb
-#  yaml
-#]
diff --git a/tool/sync_ruby b/tool/sync_ruby
index ac28c4951e..6772038542 100755
--- a/tool/sync_ruby
+++ b/tool/sync_ruby
@@ -1,140 +1,134 @@
 #!/usr/bin/env jruby
 # -*- coding: utf-8 -*-
 
 # This script is for use with JRuby, to copy the (patched) stdlib and external test files from
 # various locations in MRI's layout to JRuby's layout. It should be used
 # against the jruby-specific fork of MRI's repository at
 # github.com/jruby/ruby. 
 #
 # This script selects the branch to use against with the version number, i.e: jruby-ruby_1_8_7 or jruby-ruby_1_9_2.
 #
 # usage: sync_ruby <tests|stdlib|all> <version(1_8_7|1_9_2)> <jruby ruby fork clone> <jruby dir>
 #
 # Example:
 # 
 # The JRuby ruby fork is in ../jruby-ruby, and jruby is in the current directory.
 # We want to sync both 1.8.7 standard libraries.
 # 
 # $ jruby tool/sync_ruby stdlib 1_8_7 ../jruby-ruby .
 # ~/projects/jruby ➔ jruby tool/sync_ruby stdlib 1_8_7 ../jruby-ruby .
 # Already on 'jruby-ruby_1_8_7'
 # cp -r ../jruby-ruby/lib/English.rb ./lib/ruby/1.8
 # cp -r ../jruby-ruby/lib/Env.rb ./lib/ruby/1.8
 # ...
 
 require 'fileutils'
 
 class Sync
   include FileUtils
 
   def initialize(type, version, source, target)
     @type = type
     @named_version = version
     @version = format_version(version)
     @source = source
     @target = target
 
     checkout
   end
 
   def sync_tests
     Dir.glob("#{@source}/test/*") do |file|
       cp_r file, "#{@target}/test/externals/ruby#{@version}", :verbose => true
     end
   end
 
   def sync_stdlib
     load File.dirname(__FILE__) + "/globals_#{@named_version}.rb"
 
     for file in STDLIB_FILES
       cp_r "#{@source}/lib/#{file}", "#{@target}/lib/ruby/#{@version}", :verbose => true
     end
 
     for file, target in EXT_FILES
       if File.directory? "#{@source}/#{file}"
         cp_r "#{@source}/#{file}", "#{@target}/lib/ruby/#{@version}/", :verbose => true
       else
         cp_r "#{@source}/#{file}", "#{@target}/lib/ruby/#{@version}/#{target}", :verbose => true
       end
     end
-
-    if defined? YAML_FILES
-      for file in YAML_FILES
-        cp_r "#{@source}/lib/#{file}", "#{@target}/src/builtin", :verbose => true
-      end
-    end
   end
 
   def sync_rubygems
     if Dir.pwd != File.expand_path('../..', __FILE__) || @target != '.'
       $stderr.puts "Rubygems is sync'd into the jruby where this script was launched."
       $stderr.puts "To acknowledge this, run in the top level of that jruby tree and use a target of `.'."
       raise ArgumentError, "Sync Rubygems target mismatch"
     end
     (Dir["#@target/lib/ruby/site_ruby/1.8/*ubygems"] + ["#@target/lib/ruby/site_ruby/1.8/rbconfig"]).each do |f|
       rm_rf f
     end
     cd(@source) do
       system "ruby setup.rb --no-format-executable --no-ri --no-rdoc"
     end
     gem_script = File.join(@target, "bin", "gem")
 
     # Fix up shebang so it doesn't have abs path
     lines = IO.readlines gem_script
     lines[0] = "#!/usr/bin/env jruby\n"
     File.open(gem_script, "wb") {|f| lines.each {|l| f << l } }
     cp File.join(@target, "bin", "gem"), File.join(@target, "bin", "jgem")
   end
 
   private
   def format_version(version)
     version.gsub(/_\d+$/, '').gsub(/_/, '.')
   end
 
   def checkout
     cd(@source) do
       branch = "jruby-ruby#{@type == 'rubygems' ? 'gems' : ''}_#{@named_version}"
 
       if (branches = `git branch | sed 's/[\*\s]*//'`).split("\n").include? branch
         `git checkout #{branch}`
       else
         `git checkout -t origin/#{branch}`
       end
     end
   end
 end
 
 if $0 == __FILE__
   if ARGV.size != 4
     puts "usage: sync_ruby <tests|stdlib|rubygems|all> <version(1_8_7|1_9_2)> <jruby ruby(gems) fork clone> <jruby dir>"
     exit 1
   end
 
   if !%w{tests stdlib rubygems all}.include? ARGV[0]
     puts "invalid source to sync: #{ARGV[0]}"
     exit 1
   end
 
   if !(ARGV[1] =~ /\d+_\d+_\d+/)
     puts "invalid version number: #{ARGV[1]}"
     exit 1
   end
 
   if !File.exist?(ARGV[2]) || !File.directory?(ARGV[2])
     puts "invalid source dir: #{ARGV[2]}"
     exit 1
   end
 
   if !File.exist?(ARGV[3]) || !File.directory?(ARGV[2])
     puts "invalid target dir: #{ARGV[2]}"
     exit 1
   end
 
   sync = Sync.new(*ARGV)
   if ARGV[0] == 'all'
     sync.sync_tests
     sync.sync_stdlib
   else
     sync.send(:"sync_#{ARGV[0]}")
   end
 end
