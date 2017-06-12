1;// CON FIXME: I don't know how to make case be an expression...does that;src/org/jruby/compiler/ir/IRBuilder.java;f9c3ccc4a;214b11c51
2;// don't bother to check if final method;src/org/jruby/javasupport/JavaMethod.java;52343aa0f;fe8302dcd
3;// end hack;src/org/jruby/RubyObject.java;f16e305fc;da0b65ceb
4;// ENEBO: Totally weird naming (in MRI is not allocated and is a local var name);truffle/src/main/java/org/jruby/truffle/parser/parser/ParserSupport.java;fa938c4c7;8b6eec17d
5;// FIXME moved this here to get what's obviously a utility method out of IRubyObject;src/org/jruby/Ruby.java;0aa32bd5b;47fd4acb7
6;// FIXME: Big fat hack here;src/org/jruby/ast/executable/AbstractScript.java;0b01fd39c;04602b8b0
7;// FIXME: bit of a kludge here (non-interface classes assigned to both;core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java;be3134444;55927e21e
8;// FIXME: Can get optimized for IEqlNode;src/org/jruby/ast/WhenOneArgNode.java;d7361b88d;75f48785f
9;// FIXME: Clearing read buffer here...is this appropriate?;src/org/jruby/util/io/ChannelStream.java;66b024fed;aa4ffa1be
10;// FIXME: Determine if a real allocator is needed here. Do people want to extend;jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java;f9b28c47d;14226fded
11;// FIXME: do we really want 'declared' methods? includes private/protected;jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java;f9b28c47d;14226fded
12;// FIXME: I don't like the null checks here;src/org/jruby/RubyIO.java;66b024fed;83c50c049
13;// FIXME: I don't like this;core/src/main/java/org/jruby/ir/targets/JVMVisitor.java;2877eda42;389b7657b
14;// FIXME: If NativeException is expected to be used from Ruby code;src/org/jruby/NativeException.java;b390103c2;725be6439
15;// FIXME: If true array is common enough we should pre-allocate and stick somewhere;src/org/jruby/RubyObject.java;8868926c9;6eb8df5bd
16;// FIXME: legal here? may want UnsupportedOperationException;src/org/jruby/IncludedModuleWrapper.java;95c346df5;6f159fe5d
17;// FIXME: Maybe not best place;src/org/jruby/runtime/BlockBody.erb;a0ea820f4;45658ca9d
18;// FIXME: needs to be rethought;src/org/jruby/javasupport/JavaSupport.java;1dbbe1a37;41d1cea1e
19; I don't like it.;src/org/jruby/runtime/CallbackFactory.java;b14d1b95c;4a8330815
20;// FIXME: Not sure what the semantics of transfer are;src/org/jruby/libraries/FiberLibrary.java;c187d01e4;c2f126a48
21;// FIXME: only starting after required args;src/org/jruby/compiler/impl/StackBasedVariableCompiler.java;9b6d602f9;ff9baab4a
22;// FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode;src/org/jruby/compiler/ASTCompiler19.java;20225f17b;37355c58e
23;// FIXME: This determine module is in a strange location and should somehow be in block;core/src/main/java/org/jruby/ir/interpreter/Interpreter.java;a8c5cfe34;8e7062232
24;// FIXME: This is a gross way to figure it out;src/org/jruby/compiler/ir/IR_Builder.java;dfa3f8635;b6fc0556a
25;// FIXME: This is almost entirely duplicated from Main.java;src/org/jruby/util/NailMain.java;58d67c3af;20ff6278c
26;// FIXME: This is an ugly hack to resolve JRUBY-1381;src/org/jruby/RubyObject.java;f16e305fc;da0b65ceb
27;// FIXME: This is gross. Don't do this.;src/org/jruby/runtime/BlockBody.erb;a0ea820f4;45658ca9d
28;// FIXME: This is temporary since the variable compilers assume we want;core/src/main/java/org/jruby/compiler/ASTCompiler.java;4e87bc6e1;ed54aab18
29;// FIXME: This isn't right for within ensured/rescued code;src/org/jruby/compiler/impl/StandardASMCompiler.java;fc09ed420;148883daa
30;// FIXME: This isn't right for within ensured/rescued code;src/org/jruby/compiler/impl/StandardASMCompiler.java;fc09ed420;148883daa
31;// FIXME: this not being in a finally is a little worrisome;src/org/jruby/java/MiniJava.java;07e838b13;02b8e01b4
32;// FIXME: this really ought to be in clinit;src/org/jruby/compiler/impl/StandardASMCompiler.java;362877f03;3849128d6
33;// FIXME: this should go somewhere more generic -- maybe IdUtil;truffle/src/main/java/org/jruby/truffle/util/IdUtil.java;92c1ab5a7;cd313f38a
34;// FIXME: This table will get moved into POSIX library so we can get all actual supported;src/org/jruby/RubyProcess.java;c3af7f127;2f1445af0
35;// FIXME: This worries me a bit;src/org/jruby/util/io/ChannelStream.java;66b024fed;aa4ffa1be
36;// FIXME: we should also support orgs that use capitalized package;src/org/jruby/javasupport/Java.java;e705f6ca4;bb9cd0753
37;// FIXME: We should be getting this from the runtime rather than assume none?;src/org/jruby/ast/EncodingNode.java;898124198;52192272b
38;// FIXME: We should really not be creating the dynamic scope for the root;truffle/src/main/java/org/jruby/truffle/parser/parser/ParserConfiguration.java;fa938c4c7;8b6eec17d
39;// FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes;src/org/jruby/javasupport/util/RuntimeHelpers.java;7af96b98d;bb98e4b8d
76;// This is a dummy scope;src/org/jruby/runtime/MethodBlock.java;cdb1c7f5d;0e0ffe2e5
77;// This is for JRUBY-2988;src/org/jruby/ext/Readline.java;0f9880c5a;a00e27808
78;// TODO need to abstract this setup behind another compiler interface;src/org/jruby/compiler/impl/StandardASMCompiler.java;4934ed56b;4fa52b39b
79;// TODO this should entry into error handling somewhere;src/org/jruby/util/io/ChannelStream.java;66b024fed;aa4ffa1be
80;// TODO: catch exception if constant is already set by other;core/src/main/java/org/jruby/javasupport/binding/ConstantField.java;5755383a4;55927e21e
81;// TODO: choose narrowest method by continuing to search;src/org/jruby/javasupport/JavaMethod.java;05c37a91a;c4e6f18a4
82;// TODO: Determine whether we should perhaps store non-singleton class;src/org/jruby/internal/runtime/methods/DynamicMethod.erb;f9b01edcd;885e23521
83;// TODO: don't bother passing when fcall or vcall;core/src/main/java/org/jruby/ir/targets/JVMVisitor.java;25fdaa3bb;96034ed2a
84;// TODO: exec should replace the current process;src/org/jruby/RubyKernel.java;8548cc7b0;0bf233162
85;// TODO: Generalize this type-checking code into IRubyObject helper.;src/org/jruby/RubyObject.java;a679fc6a6;6eb8df5bd
86;// TODO: Implement tty? and isatty. We have no real capability to;src/org/jruby/runtime/builtin/meta/IOMetaClass.java;ae3178fbb;bac69eda7
87;// TODO: is this correct ?;src/org/jruby/RubyArray.java;bb20e69fc;105217d34
88;// TODO: is this right?;src/org/jruby/compiler/ir/IRBuilder.java;f9c3ccc4a;9071ce26f
89;// TODO: is this the right thing to do?;src/org/jruby/RubyIO.java;043df6006;fb60a5baf
90;// TODO: It's perhaps just a coincidence that all the channels for;core/src/main/java/org/jruby/util/io/PosixShim.java;ecc42a6c1;16c989010
91;// TODO: make this an array so it's not as much class metadata;src/org/jruby/java/MiniJava.java;ba40f9ccd;02b8e01b4
92;// TODO: make this do specific-arity calling;src/org/jruby/java/MiniJava.java;70cf58217;02b8e01b4
93;// TODO: Make this more intelligible value;src/org/jruby/util/io/ModeFlags.java;66b024fed;e04c88bdf
94;// TODO: need to get this back into the method signature...now is too late...;src/org/jruby/compiler/ir/targets/JVM.java;aa0f8552a;80603cf52
95;// TODO: newTypeError does not offer enough for ruby error string...;src/org/jruby/RubyObject.java;a679fc6a6;6eb8df5bd
96;// TODO: no java stringity;src/org/jruby/RubyThread.java;c4c035bee;ba93d1ae0
97;// TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here;jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java;f9b28c47d;14226fded
98;// TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here;src/org/jruby/RubyStruct.java;b390103c2;87404f0e1
99;// TODO: Only setAccessible to account for pattern found by;src/org/jruby/javasupport/JavaField.java;e40beaae6;beaaeb89b
100;// TODO: Only used by interface implementation;src/org/jruby/javasupport/util/RuntimeHelpers.java;132d0a520;bb98e4b8d
101;// TODO: protected methods. this is going to require a rework;core/src/main/java/org/jruby/javasupport/JavaClass.java;ef97a3045;be3134444
102;// TODO: protected methods. this is going to require a rework of some of the mechanism.;core/src/main/java/org/jruby/javasupport/JavaClass.java;5755383a4;be3134444
103;// TODO: public only?;src/org/jruby/javasupport/JavaClass.java;beaaeb89b;48d04419e
104;// TODO: remove;truffle/src/main/java/org/jruby/truffle/nodes/core/TruffleInteropNodes.java;9f228b9ac;5f14842c0
105;// TODO: Ruby does not seem to care about invalid numeric mode values;src/org/jruby/util/io/ModeFlags.java;66b024fed;aa4ffa1be
106;// TODO: Should frozen error have its own distinct class? If not should more share?;src/org/jruby/exceptions/FrozenError.java;61a7ba7ca;36ec84bb4
107;// TODO: These were missing;src/org/jruby/RubyFile.java;bac69eda7;00633ae67
108;// TODO: this could be further optimized;src/org/jruby/runtime/callback/InvocationCallbackFactory.java;9251dc201;ed0a495c6
109;// TODO: This filtering is kind of gross...it would be nice to get some parser help here;src/org/jruby/compiler/ir/IR_Builder.java;dfa3f8635;89eb209a5
110;// TODO: This is actually now returning the scope of whoever called Method#to_proc;src/org/jruby/runtime/MethodBlock.java;cdb1c7f5d;fd0fa789b
111;// TODO: This is almost RubyModule#instance_methods on the metaClass. Perhaps refactor.;src/org/jruby/RubyObject.java;31d6374bd;6eb8df5bd
112;// TODO: This is also defined in the MetaClass too...Consolidate somewhere.;src/org/jruby/RubyFile.java;0a56a8280;d055a0ea0
113;// TODO: this is kinda gross;truffle/src/main/java/org/jruby/truffle/parser/Helpers.java;fa938c4c7;277af88f8
114;// TODO: This is probably BAD...;src/org/jruby/runtime/callback/InvocationCallbackFactory.java;9251dc201;ed0a495c6
115;// TODO: This probably isn't the best hack;src/org/jruby/runtime/ThreadContext.java;d5917ab01;fd0fa789b
116;// TODO: we should be able to optimize this quite a bit post-1.0. JavaClass already;src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java;0b9733a01;165ec891f
117;// TODO: why are we duplicating the constants here;src/org/jruby/RubyFile.java;cd64ee383;00633ae67
118;// TODO: wire into new exception handling mechanism;src/org/jruby/runtime/ThreadContext.java;3fa144ebb;efd8a3df5
119;// TODO: WRONG - get interfaces from class;src/org/jruby/javasupport/JavaInterfaceTemplate.java;18d03923f;557c40cd6
120;// TODO? I think this ends up propagating from normal Java exceptions;src/org/jruby/RubyIO.java;66b024fed;b97e02246
121;// We clone dynamic scope because this will be a new instance of a block. Any previously;src/org/jruby/runtime/Interpreted19Block.java;bf8db0bc4;0e0ffe2e5
122;// We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it);src/org/jruby/util/io/Stream.java;66b024fed;9d57dbc3b
123;// we're depending on the side effect of the load;core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java;613673ae6;066aa2fef
124;// Workaround for a bug in Sun's JDK 1.5.x;src/org/jruby/ext/socket/RubySocket.java;eb1f44e4b;606bd2e15
125;// XXX: const lookup can trigger const_missing;src/org/jruby/compiler/ir/IR_Builder.java;dfa3f8635;a7b4c64e6
126;// Yow...this is still ugly;core/src/main/java/org/jruby/ext/stringio/RubyStringIO.java;e14ea63ba;95db8a8ba
127;ENEBO: Lots of optz in 1.9 parser here;truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java;fa938c4c7;8b6eec17d
128;FIXME convert to enum ?;src/org/jruby/runtime/Iter.java;1c1c85534;24befe5cf
129;FIXME: any good reason to have two identical methods? (same as remove_class_variable);src/org/jruby/RubyModule.java;95c346df5;632441f33
130;FIXME: Consider fixing node_assign itself rather than single case;truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java;fa938c4c7;8b6eec17d
131;FIXME: finalizer should be dupped here;src/org/jruby/RubyObject.java;fa021e635;fd0fa789b
132;FIXME: lose syntactical elements here (and others like this);truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java;fa938c4c7;8b6eec17d
133;FIXME: Resolve what the hell is going on;src/org/jruby/parser/Ruby19Parser.java;bf8db0bc4;84ac6052e
134;FIXME: Should this be renamed to match its ruby name?;src/org/jruby/RubyObject.java;7404d16eb;6eb8df5bd
135;Fixme: This does not have exact same semantics as RubyArray.join;src/org/jruby/runtime/builtin/meta/FileMetaClass.java;7574ed61f;bac69eda7
136;"FIXME: Whis is this named ""push_m""?";src/org/jruby/RubyArray.java;e1a1b4740;719bc20ab
137;shouldn't happen. TODO: might want to throw exception instead.;src/org/jruby/javasupport/Java.java;7e8f96040;1dbbe1a37
138;SSS FIXME: Do we need to check if l is same as whatever popped?;src/org/jruby/compiler/ir/IR_ExecutionScope.java;dc0b3a049;8dff5b2a5
139;SSS FIXME: Used anywhere? I don't see calls to this anywhere;src/org/jruby/compiler/ir/IRBuilder.java;f9c3ccc4a;69a019ddc
140;when MET choose CET timezone to work around Joda;src/org/jruby/RubyTime.java;55f03b637;0a93732b7