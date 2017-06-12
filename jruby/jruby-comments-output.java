Hack in to replace usual readline with this
b2a2f49f1e remove readline and truffle from ext/
d38821551a Fix for JRUBY-336 and JRUBY-337: add support for applets by catching (and ignoring) access violation exceptions, and adds applet and standalone consoles
b2a2f49f1 ext/readline/src/main/java/org/jruby/demo/readline/TextAreaReadline.java
-        /* Hack in to replace usual readline with this */
d38821551 src/org/jruby/demo/TextAreaReadline.java
+        /* Hack in to replace usual readline with this */
*_*_*
SSS FIXME: Do we need to check if l is same as whatever popped?
0f45333751 [IR] Fixed bug with running ensure code when we encounter a break -- we only have to run those nested ensure blocks that are also nested within the loop that the break is nested in (old code was running all nested ensure blocks that the break was present in -- even those outside the loop).  This bug also affected next statements.  Fixed both.
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
dc0b3a049c - Cleaned up the scope setup some more; Integrated (not fully tested &   debugged) closures into live variable anaysis code; Added instructions   to allocate heap frame, load / store variables from frame (not   integrated yet);
955209fba7 - Added IR_Loop; implemented loop stack in IR_Scope; translated while and converted break & next to jumps for non-closure cases
0f4533375 src/org/jruby/compiler/ir/IRScope.java
-        loopStack.pop(); /* SSS FIXME: Do we need to check if l is same as whatever popped? */
8dff5b2a5 src/org/jruby/compiler/ir/IR_ExecutionScope.java
-        _loopStack.pop(); /* SSS FIXME: Do we need to check if l is same as whatever popped? */
f9c3ccc4a src/org/jruby/compiler/ir/IRExecutionScope.java
+        loopStack.pop(); /* SSS FIXME: Do we need to check if l is same as whatever popped? */
dc0b3a049 src/org/jruby/compiler/ir/IR_ExecutionScope.java
dc0b3a049 src/org/jruby/compiler/ir/IR_ScopeImpl.java
+    public void endLoop(IR_Loop l) { _loopStack.pop(); /* SSS FIXME: Do we need to check if l is same as whatever popped? */ }
-    public void endLoop(IR_Loop l) { _loopStack.pop(); /* SSS FIXME: Do we need to check if l is same as whatever popped? */ }
955209fba src/org/jruby/compiler/ir/IR_ScopeImpl.java
+    public endLoop(IR_Loop l) { _loopStack.pop(); /* SSS FIXME: Do we need to check if l is same as whatever popped? */ }
*_*_*
FIXME: Consider fixing node_assign itself rather than single case
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
5a0b41a2fb Updated 2.1 grammar with some non-working bits (like imaginary and rational literals)
bdd0b37b43 Minimum to remove 1.9 parser
db61864d6a minimal work to remove 1.8 parser
5397087cdf Whoops accidentally picked up emacs open file backup
7c6444fad4 Last MRI19 test regression fixed
b6d12c34d5 Moar production meat fixed
d5713e9388 Broken but saving ripper progress
5fae3aa735 Yay...generated 20 compiler will compile now
f00edcd9d5 First massive edit session complete
48e71445b6 Remove abortive Ruby Antlr grammar...
7a9828c859 Remove never used antlr skeleton of jruby parser
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
0e33acfafe Saving some scratch-pad ANTLR work for later, hopefully
e9bf476d31 Add some assignment position fixes (From Mirko Stocker) literal_concat changes:  - Multiple StrNodes coallesce  - Extra DStrNodes are not getting created
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
-                    // FIXME: Consider fixing node_assign itself rather than single case
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    // FIXME: Consider fixing node_assign itself rather than single case
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.java
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.y
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.java
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.y
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.java
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.y
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
-                    // FIXME: Consider fixing node_assign itself rather than single case
db61864d6 core/src/main/java/org/jruby/parser/DefaultRubyParser.java
db61864d6 core/src/main/java/org/jruby/parser/DefaultRubyParser.y
-		  /* FIXME: Consider fixing node_assign itself rather than single case*/
-		  // FIXME: Consider fixing node_assign itself rather than single case
5397087cd src/org/jruby/parser/#Ruby19Parser.y#
-                    // FIXME: Consider fixing node_assign itself rather than single case
7c6444fad src/org/jruby/parser/#Ruby19Parser.y#
+                    // FIXME: Consider fixing node_assign itself rather than single case
b6d12c34d src/org/jruby/parser/Ripper19Parser.java
b6d12c34d src/org/jruby/parser/Ripper19Parser.y
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
-                    // FIXME: Consider fixing node_assign itself rather than single case
d5713e938 src/org/jruby/parser/Ripper19Parser.java
d5713e938 src/org/jruby/parser/Ripper19Parser.y
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    // FIXME: Consider fixing node_assign itself rather than single case
5fae3aa73 src/org/jruby/parser/Ruby20Parser.java
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
f00edcd9d src/org/jruby/parser/Ruby20Parser.y
+                    // FIXME: Consider fixing node_assign itself rather than single case
48e71445b src/org/jruby/parser/DefaultRubyParser.initial.g
-                  // FIXME: Consider fixing node_assign itself rather than single case
7a9828c85 src/org/jruby/parser/DefaultRubyParser.g
-                  // FIXME: Consider fixing node_assign itself rather than single case
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.java
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.y
 		  /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    // FIXME: Consider fixing node_assign itself rather than single case
0e33acfaf src/org/jruby/parser/DefaultRubyParser.g
0e33acfaf src/org/jruby/parser/DefaultRubyParser.initial.g
+                  // FIXME: Consider fixing node_assign itself rather than single case
+                  // FIXME: Consider fixing node_assign itself rather than single case
e9bf476d3 src/org/jruby/parser/DefaultRubyParser.java
e9bf476d3 src/org/jruby/parser/DefaultRubyParser.y
+		    /* FIXME: Consider fixing node_assign itself rather than single case*/
+		    // FIXME: Consider fixing node_assign itself rather than single case
*_*_*
// FIXME: I think we need these pushed somewhere?
# shas =  1
*_*_*
// if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
52d2b6c7bc - More nodes translated; some nodes fixed up; added Regexp
86e7fc9281 - Cleaned up DefnNode, and translated DSymbolNode
dfa3f8635f - First commit: incomplete, will not compile, and some files are just outlines
57f9436a66 Get whole-body rescues to optimize the same way a non-rescued method body would.
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
52d2b6c7b src/org/jruby/compiler/ir/IR_Builder.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
86e7fc928 src/org/jruby/compiler/ir/IR_Builder.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
dfa3f8635 src/org/jruby/compiler/ir/IR_Builder.java
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
57f9436a6 src/org/jruby/compiler/ASTCompiler.java
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
*_*_*
// TODO: Make DynamicMethod immutable
# shas =  1
*_*_*
// Must be called from main thread (it is currently
# shas =  1
*_*_*
// TODO: Only used by interface implementation
7745f1060a deprecate no longer used Helpers.invokeMethodMissing method
bb98e4b8de Move RuntimeHelpers to org.jruby.runtime.Helpers.
132d0a520b More propagating the effects of having caller passed to visibility-checking logic; getFrameSelf() almost completely eliminated.
7745f1060 core/src/main/java/org/jruby/runtime/Helpers.java
-    // TODO: Only used by interface implementation; eliminate it
bb98e4b8d src/org/jruby/javasupport/util/RuntimeHelpers.java
bb98e4b8d src/org/jruby/runtime/Helpers.java
-    // TODO: Only used by interface implementation; eliminate it
+    // TODO: Only used by interface implementation; eliminate it
132d0a520 src/org/jruby/javasupport/util/RuntimeHelpers.java
+    // TODO: Only used by interface implementation; eliminate it
*_*_*
// SSS FIXME: Should I have a reference to the IR_loop that is being retried?
f9fc46efcf [IR] Got rid of unused instructions
07e7ef2ca0 - Added stubs for redo & retry instructions
f9fc46efc src/org/jruby/compiler/ir/instructions/RETRY_Instr.java
-// SSS FIXME: Should I have a reference to the IR_loop that is being retried?
07e7ef2ca src/org/jruby/compiler/ir/RETRY_Instr.java
+// SSS FIXME: Should I have a reference to the IR_loop that is being retried?
*_*_*
// FIXME: No, I don't like it.
4a8330815c Cleanup and centralize all property-based configuration settings.
b14d1b95cb Modify class-generation sites to accept a classloader, to allow for garbage collection of generated classes where appropriate. This allows the JIT to generate methods continously without leaking.
4a8330815 src/org/jruby/runtime/CallbackFactory.java
-            // FIXME: No, I don't like it.
b14d1b95c src/org/jruby/runtime/CallbackFactory.java
+            // FIXME: No, I don't like it.
*_*_*
// TODO: confirm expected args are IRubyObject (or similar)
cb8f537d74 Add an offline indy-based invoker generator.
e084deabce Add a separate type of JavaMethodDescriptor for compile time.
2f7cc79224 Get the annotation-based binding generator working again with multimethods and optional 'required' attrs.
a0ed4fd9d0 Abstract the idea of a "descriptor" given a method and its annotation into a separate structure we can pass around and store.
cb8f537d7 core/src/main/java/org/jruby/anno/IndyBinder.java
+                    args--;                        // TODO: confirm expected args are IRubyObject (or similar)
+                // TODO: confirm expected args are IRubyObject (or similar)
+                    args--;                        // TODO: confirm expected args are IRubyObject (or similar)
e084deabc core/src/main/java/org/jruby/anno/JavaMethodDescriptor.java
e084deabc core/src/main/java/org/jruby/anno/MethodDescriptor.java
-                // TODO: confirm expected args are IRubyObject (or similar)
-                // TODO: confirm expected args are IRubyObject (or similar)
+                // TODO: confirm expected args are IRubyObject (or similar)
+                // TODO: confirm expected args are IRubyObject (or similar)
2f7cc7922 src/org/jruby/anno/AnnotationBinder.java
+                        // TODO: confirm expected args are IRubyObject (or similar)
+                        // TODO: confirm expected args are IRubyObject (or similar)
a0ed4fd9d src/org/jruby/anno/JavaMethodDescriptor.java
+                // TODO: confirm expected args are IRubyObject (or similar)
+                // TODO: confirm expected args are IRubyObject (or similar)
*_*_*
// SSS FIXME: Can this return anything other than nil?
214b11c51e Fixed IR for alias nodes (right now, relying on ruby internal calls instruction -- probably should change to their own IR instructions at some point)
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
2b4a871b6d - More methods translated; several more FIXMEs recorded; more methods added to IR_Scope and its implementations.
214b11c51 src/org/jruby/compiler/ir/IRBuilder.java
-            // SSS FIXME: Can this return anything other than nil?
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-            // SSS FIXME: Can this return anything other than nil?
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+            // SSS FIXME: Can this return anything other than nil?
2b4a871b6 src/org/jruby/compiler/ir/IR_Builder.java
+            // SSS FIXME: Can this return anything other than nil?
*_*_*
FIXME: finalizer should be dupped here
fd0fa789b2 Merge "backtrace" branch to master, now that it runs clean on all our various test cases.
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
fa021e6350 JRUBY-488: Kernel#dup, Kernel#clone and initialize_copy should obey MRI protocol
fd0fa789b src/org/jruby/RubyObject.java
-        /* FIXME: finalizer should be dupped here */
bf8db0bc4 src/org/jruby/RubyBasicObject.java
+        /* FIXME: finalizer should be dupped here */
fa021e635 src/org/jruby/RubyObject.java
+        /* FIXME: finalizer should be dupped here */
*_*_*
// using IOOutputStream may not be the most performance way
# shas =  1
*_*_*
// FIXME: bit of a kludge here (non-interface classes assigned to both
55927e21ea Revert my refactoring in favor of version based on @kares work.
5d4a39d14b Remove JavaClass from participation in proxy binding.
be31344448 Pull all initialize logic up and out into initializer classes.
5755383a4b Begin refactoring JavaClass binding logic.
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
1dbbe1a37a More JI cleanup:  - Reworked the very expensive and thread-unsafe (and essentially obsolete) proxy extender mechanism. No longer is every new proxy checked against all proxy extenders; since the Rubification of the Java class hierarchy, it's really only necessary to extend the class/module specified in the extend_proxy method. I'd like to see this feature deprecated, as it's redundant now (see forthcoming note on jruby-dev).  - Moved JavaClass instances to a ConcurrentHashMap for faster access.  They had been in a weak hash map, but since almost all of them were linked to proxies that were in non-weak maps, there was really no benefit (and some performance penalties).  - Used concurrent techniques to speed proxy class lookup.  Proxies are now stored with their corresponding JavaClass instances, and can be retrieved with an unsynchronized access.  - Eliminated the ProxyData "dataStruct" that had been attached to the JavaUtilities module; most of the fields (maps) had been eliminated in the work noted above; the remaining fields have been moved to JavaSupport where they may be accessed without a JavaUtilities reference.  - Permanently disabled the the old-style (<) interface implementation syntax (there had been a method to enable it; no more).  - Fixed a bug wherein the colon2 syntax for opening modules was not working if the module had not previously been referenced.  Will open a JIRA and backport to the 1.0 branch.
55927e21e core/src/main/java/org/jruby/javasupport/JavaClass.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
+        // FIXME: bit of a kludge here (non-interface classes assigned to both
-        // FIXME: bit of a kludge here (non-interface classes assigned to both
5d4a39d14 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
-        // FIXME: bit of a kludge here (non-interface classes assigned to both
be3134444 core/src/main/java/org/jruby/javasupport/JavaClass.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
-            // FIXME: bit of a kludge here (non-interface classes assigned to both
+        // FIXME: bit of a kludge here (non-interface classes assigned to both
5755383a4 core/src/main/java/org/jruby/javasupport/JavaClass.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
-        // FIXME: bit of a kludge here (non-interface classes assigned to both
+        // FIXME: bit of a kludge here (non-interface classes assigned to both
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-        // FIXME: bit of a kludge here (non-interface classes assigned to both
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+        // FIXME: bit of a kludge here (non-interface classes assigned to both
1dbbe1a37 src/org/jruby/javasupport/JavaClass.java
+        // FIXME: bit of a kludge here (non-interface classes assigned to both
*_*_*
// This is a fairly specific hack for empty string
# shas =  1
*_*_*
// TODO: decompose getJavaPackageModule so we don't parse fullName
# shas =  1
*_*_*
// The following three methods must be defined fast
# shas =  1
*_*_*
// TODO: turn off the negative? will return -0.0 in lax mode
99a65891dc JRUBY-3709: The 'adapted' Sun code in util/Convert.java
bcd3301fcd The Bill Dortch sprintf special commit....
99a65891d src/org/jruby/util/ConvertDouble.java
-                        // TODO: turn off the negative? will return -0.0 in lax mode
bcd3301fc src/org/jruby/util/Convert.java
+                        // TODO: turn off the negative? will return -0.0 in lax mode
*_*_*
// TODO: why are we duplicating the constants here
00633ae67b Refactoring File: reordering, minor cleanup.
cd64ee3832 JRUBY-2571: some IO constants not defined
00633ae67 src/org/jruby/RubyFile.java
-        // TODO: why are we duplicating the constants here, and then in
cd64ee383 src/org/jruby/RubyFile.java
+        // TODO: why are we duplicating the constants here, and then in
*_*_*
// FIXME we want to eliminate these type casts when possible
3f44a103f8 Kill unused method invoker logic and related classes.
dc8c8156b2 Migrating compiled methods to be instance methods instead of static methods, to be a bit "safer" when sharing compiled classes across runtimes (no call-site caches colliding, etc).
3f44a103f core/src/main/java/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
-            // FIXME we want to eliminate these type casts when possible
dc8c8156b src/org/jruby/internal/runtime/methods/InvocationMethodFactory.java
+                // FIXME we want to eliminate these type casts when possible
*_*_*
// TODO: This probably isn't the best hack
fd0fa789b2 Merge "backtrace" branch to master, now that it runs clean on all our various test cases.
d5917ab017 Implement 1.9 Kernel#caller and require_relative (plus other 1.9 prelude stuff) in preparation for 1.9 tests.
fd0fa789b src/org/jruby/runtime/ThreadContext.java
-            // TODO: This probably isn't the best hack, but it works until we can have different
d5917ab01 src/org/jruby/runtime/ThreadContext.java
+            // TODO: This probably isn't the best hack, but it works until we can have different
*_*_*
// SSS FIXME: Is this correct?
cba0983f08 Remove irscope config flags since they are now dead
ce466f2853 First pass attempting to optimize startup time
78e968ef4d Save/restore $! properly in the JRuby runtime
1c09ad7a3d ISSUE 1601: save/restore $! properly in the runtime
64177eb12a [IR] Redid IR for ensure bodies
af99ede3fc [IR] Fixes to inlining instruction cloning code
6971c33ca8 [IR] Got rid of unnecessary top-level-binding hack.
377b26a442 [IR] Dealt with top-level-binding woes (with FIXME). More rails code seems to run.
07ab4fb0d0 [IR] Fixed bug with incorrect argument order in AttrAssignInstr
d3df20d503 [IR] Clean up pre/post call setup/cleanup code -- do different things for method calls and yield/block calls.  This fixes a number of spec failures (including super).  Updated IR mspec script to reflect updated state of crashers (and why they crash).
59691f706a IR: Make direct reference to RubyMatchData instead of Class.forName
e59f39070c [IR] Fixed bug in generated IR for defined? checks
19e699ae71 Setup self correctly when yielding in InterpretedIRBlock
ed65af13d1 Fix up ruby-array wrapping mismatches when passing arguments to blocks -- protocol is slightly different for IR code
9197a579fb Change colon3 to use Object scope and then revert SearchConst to only be created in terms of MetaObjects
8b602419cd Finish up AttrAssignNode bit of defined?
0a5e1726f0 One more piece of defined? IR in place
6e64905dd3 One more piece of defined? AST IR-ed + some code cleanup
69a019ddc9 Additional pieces of defined? IR-ed
e9b84ff65a Redid IR inheritance for Call & AttrAssign instructions
8bc8df0f87 Dump of current progress on using container for method def{n,s}
1d3fb85af7 Fixed bad arity checks in InterpretedIRBlockBody
b5d32a6b8d Added MethodHandle operand; Split calls into method lookup + method invoke; Added single-entry inline cache to methodhandle
5e5588fbe9 Make CallInstr have explicit receiver argument instead of being first argument in callArgs
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
022d5284f8 - Bug fixes in IR generation of yield instruction + more bug fixes in inlining of closures (seems to work better now).
411996dc4c - Got rid of a few fixmes, fixed some code, removed unused operation types, added some more comments.
15d7d44d7b - Few bug fixes + started changing coding style to conform with existing   coding style + translated SClassNode + added several additional fixmes   + added timing output.
d0d940ce45 - Some more compiler errors fixed.
3ac65ffe71 - Translated buildModule; Fixed code for some other nodes; Commented out about 16 AST nodes that aren't translated yet, and their corresponding code
cba0983f0 core/src/main/java/org/jruby/ir/IRScope.java
-        this.state = ScopeState.INIT; // SSS FIXME: Is this correct?
ce466f285 core/src/main/java/org/jruby/ir/IRScope.java
+        this.state = ScopeState.INIT; // SSS FIXME: Is this correct?
78e968ef4 core/src/main/java/org/jruby/RubyComparable.java
-                context.setErrorInfo(runtime.getNil()); // SSS FIXME: Is this correct?
1c09ad7a3 core/src/main/java/org/jruby/RubyComparable.java
+                context.setErrorInfo(runtime.getNil()); // SSS FIXME: Is this correct?
64177eb12 core/src/main/java/org/jruby/ir/instructions/JumpIndirectInstr.java
-        // SSS FIXME: Is this correct?  Are we guaranteed this returns a variable always?
af99ede3f src/org/jruby/compiler/ir/instructions/JumpIndirectInstr.java
+        // SSS FIXME: Is this correct?
6971c33ca src/org/jruby/internal/runtime/methods/InterpretedIRMethod.java
-        // SSS FIXME: Is this correct?
377b26a44 src/org/jruby/internal/runtime/methods/InterpretedIRMethod.java
+        // SSS FIXME: Is this correct?
07ab4fb0d src/org/jruby/compiler/ir/instructions/AttrAssignInstr.java
-        // SSS FIXME: Is this correct?  Is value the 1st arg (or the last arg??)
d3df20d50 src/org/jruby/runtime/InterpretedIRBlockBody.java
-        // SSS FIXME: Is this correct?
-        // SSS FIXME: Is this correct?
-        // SSS FIXME: Is this correct?
59691f706 src/org/jruby/compiler/ir/instructions/JRubyImplCallInstr.java
-                    rVal = runtime.newBoolean(Class.forName("RubyMatchData").isInstance(bRef)); // SSS FIXME: Is this correct?
e59f39070 src/org/jruby/compiler/ir/IRBuilder.java
-        // SSS FIXME: Is this correct?  Shoudln't we be copying v1 & v2 into an variable and returning that instead?
19e699ae7 src/org/jruby/runtime/InterpretedIRBlockBody.java
+        // SSS FIXME: Is this correct?
         // SSS FIXME: Is this correct?
-        // SSS FIXME: Is this correct? null self unlike the fixed up one above
-        // SSS FIXME: Is this correct? null self unlike the fixed up one above
-        // SSS FIXME: Is this correct? null self unlike the fixed up one above
-        // SSS FIXME: Is this correct? null self unlike the fixed up one above
-        // SSS FIXME: Is this correct? null self
-        // SSS FIXME: Is this correct? null self
-        // SSS FIXME: Is this correct? null self
         // SSS FIXME: Is this correct?
ed65af13d src/org/jruby/runtime/InterpretedIRBlockBody.java
-        // SSS FIXME: Is this correct?
         // SSS FIXME: Is this correct?
+        // SSS FIXME: Is this correct? null self unlike the fixed up one above
+        // SSS FIXME: Is this correct? null self unlike the fixed up one above
+        // SSS FIXME: Is this correct? null self unlike the fixed up one above
+        // SSS FIXME: Is this correct? null self unlike the fixed up one above
+        // SSS FIXME: Is this correct? null self
+        // SSS FIXME: Is this correct? null self
+        // SSS FIXME: Is this correct? null self
+        // SSS FIXME: Is this correct?
9197a579f src/org/jruby/compiler/ir/IRBuilder.java
-        // SSS FIXME: Is this correct?
8b602419c src/org/jruby/compiler/ir/IRBuilder.java
-                        // SSS FIXME: Is this correct?  Or, do we compare against a specific exception type?
0a5e1726f src/org/jruby/compiler/ir/instructions/JRubyImplCallInstr.java
+                rVal = rt.newBoolean(Class.forName("RubyMatchData").isInstance(bRef)); // SSS FIXME: Is this correct?
6e64905dd src/org/jruby/compiler/ir/IRBuilder.java
+                        // SSS FIXME: Is this correct?  Or, do we compare against a specific exception type?
69a019ddc src/org/jruby/compiler/ir/IRBuilder.java
+        // SSS FIXME: Is this correct?  Shoudln't we be copying v1 & v2 into an variable and returning that instead?
e9b84ff65 src/org/jruby/compiler/ir/instructions/AttrAssignInstr.java
+        // SSS FIXME: Is this correct?  Is value the 1st arg (or the last arg??)
8bc8df0f8 src/org/jruby/compiler/ir/IRBuilder.java
-        // SSS FIXME: Is this correct? This method belongs to 'container'
1d3fb85af src/org/jruby/runtime/InterpretedIRBlockBody.java
+        // SSS FIXME: Is this correct?
+        // SSS FIXME: Is this correct?
b5d32a6b8 src/org/jruby/compiler/ir/IRBuilder.java
+        // SSS FIXME: Is this correct? This method belongs to 'container'
5e5588fbe src/org/jruby/compiler/ir/IRBuilder.java
-        argsList.add(build(receiver, s)); // SSS FIXME: Is this correct?
-        argsList.add(s.getSelf()); // SSS FIXME: Is this correct?
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-        argsList.add(build(receiver, s)); // SSS FIXME: Is this correct?
-        argsList.add(s.getSelf()); // SSS FIXME: Is this correct?
-        // SSS FIXME: Is this correct?
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+        argsList.add(build(receiver, s)); // SSS FIXME: Is this correct?
+        argsList.add(s.getSelf()); // SSS FIXME: Is this correct?
+        // SSS FIXME: Is this correct?
022d5284f src/org/jruby/compiler/ir/IR_Builder.java
+        argsList.add(build(receiver, s)); // SSS FIXME: Is this correct?
+        argsList.add(s.getSelf()); // SSS FIXME: Is this correct?
411996dc4 src/org/jruby/compiler/ir/IR_Builder.java
-            // SSS FIXME: Is this correct?
-            container = new MetaObject(IR_Class.getCoreClass("Object")); // SSS FIXME: Is this correct?
15d7d44d7 src/org/jruby/compiler/ir/IR_Builder.java
+				// SSS FIXME: Is this correct?
d0d940ce4 src/org/jruby/compiler/ir/IR_Builder.java
+        // SSS FIXME: Is this correct?
3ac65ffe7 src/org/jruby/compiler/ir/IR_Builder.java
+            container = IR_Class.OBJECT; // SSS FIXME: Is this correct?
*_*_*
// FIXME: Determine if a real allocator is needed here. Do people want to extend
89cedd5a04 minor JavaClass house-keeping
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
89cedd5a0 core/src/main/java/org/jruby/javasupport/JavaClass.java
-        // FIXME: Determine if a real allocator is needed here. Do people want to extend
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-        // FIXME: Determine if a real allocator is needed here. Do people want to extend
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+        // FIXME: Determine if a real allocator is needed here. Do people want to extend
b390103c2 src/org/jruby/javasupport/JavaClass.java
+        // FIXME: Determine if a real allocator is needed here. Do people want to extend
*_*_*
// TODO: Determine whether we should perhaps store non-singleton class
885e23521e Remove some .erb source templates not used anymore.
f9b01edcd0 Start generating call-path logic using erb templates. Generated results will be checked in.
1afde653a8 Add a "jruby.compile.fastest" that sets all experimental settings and make frameless be really frameless. For demonstrating future performance potential.
885e23521 src/org/jruby/internal/runtime/methods/DynamicMethod.erb
-        // TODO: Determine whether we should perhaps store non-singleton class
f9b01edcd src/org/jruby/internal/runtime/methods/DynamicMethod.erb
+        // TODO: Determine whether we should perhaps store non-singleton class
1afde653a src/org/jruby/internal/runtime/methods/DynamicMethod.java
+            // TODO: Determine whether we should perhaps store non-singleton class instead
*_*_*
// TODO: get rid of this (lax returns 0.0
99a65891dc JRUBY-3709: The 'adapted' Sun code in util/Convert.java
bcd3301fcd The Bill Dortch sprintf special commit....
99a65891d src/org/jruby/util/ConvertDouble.java
-        // TODO: get rid of this (lax returns 0.0, strict will throw)
bcd3301fc src/org/jruby/util/Convert.java
+        // TODO: get rid of this (lax returns 0.0, strict will throw)
*_*_*
// SSS FIXME: Does int suffice
8434fe5bca Cleanup! Removed a bunch of stale FIXMEs.
0c38173857 - Fixed couple bugs with scope canonicalization; implemented ir output as a compiler pass; added code-version-token for IR_Method and several method stubs to start optimizing calls.
8434fe5bc core/src/main/java/org/jruby/ir/CodeVersion.java
-    // SSS FIXME: Does int suffice, or do we need long?
0c3817385 src/org/jruby/compiler/ir/CodeVersion.java
+    // SSS FIXME: Does int suffice, or do we need long?
*_*_*
// FIXME: This table will get moved into POSIX library so we can get all actual supported
2f1445af01 Use jnr-constants for signal values.  Fixes JRUBY-5696.
c3af7f1279 JRUBY-1645: Process.kill support through JNA
2f1445af0 src/org/jruby/RubyProcess.java
-        // FIXME: This table will get moved into POSIX library so we can get all actual supported
c3af7f127 src/org/jruby/RubyProcess.java
+        // FIXME: This table will get moved into POSIX library so we can get all actual supported
*_*_*
// hack to get right style for input
b2a2f49f1e remove readline and truffle from ext/
d38821551a Fix for JRUBY-336 and JRUBY-337: add support for applets by catching (and ignoring) access violation exceptions, and adds applet and standalone consoles
b2a2f49f1 ext/readline/src/main/java/org/jruby/demo/readline/TextAreaReadline.java
-               append(" ", inputStyle); // hack to get right style for input
d38821551 src/org/jruby/demo/TextAreaReadline.java
+                append(" ", inputStyle); // hack to get right style for input
*_*_*
// TODO: make this an array so it's not as much class metadata
d9656d2b20 Move all caching logic out of "real class" and use RuntimeCache (now top-level) for method caching.
02b8e01b46 Remove MiniJava and all the tendrils it extended through JRuby. Useful remainder moved to org.jruby.java.codegen.RealClassGenerator.
07e838b135 First commit of new JI interface-impl code that actually generates a real Java class to use for the Ruby objects. Does not support concrete Java superclasses yet, which still goes through the old style logic.
ba40f9ccdc Fix for JRUBY-3158: Wrong ruby methods called on object of same class from Java code.
d9656d2b2 src/org/jruby/java/codegen/RealClassGenerator.java
-            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
-            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
02b8e01b4 src/org/jruby/java/MiniJava.java
02b8e01b4 src/org/jruby/java/codegen/RealClassGenerator.java
-            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
-            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
+            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
+            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
07e838b13 src/org/jruby/java/MiniJava.java
+            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
ba40f9ccd src/org/jruby/java/MiniJava.java
+            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
*_*_*
// FIXME: shouldn't need @__java_ovrd_methods
# shas =  1
*_*_*
// FIXME: If true array is common enough we should pre-allocate and stick somewhere
d9ccc133c7 Re-refactor fixes for mocha and MRI suite back into place.
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
8868926c9d JRUBY-3702: The method methods
d9ccc133c core/src/main/java/org/jruby/RubyBasicObject.java
-    // FIXME: If true array is common enough we should pre-allocate and stick somewhere
6eb8df5bd src/org/jruby/RubyBasicObject.java
6eb8df5bd src/org/jruby/RubyObject.java
+    // FIXME: If true array is common enough we should pre-allocate and stick somewhere
-    // FIXME: If true array is common enough we should pre-allocate and stick somewhere
8868926c9 src/org/jruby/RubyObject.java
+    // FIXME: If true array is common enough we should pre-allocate and stick somewhere
*_*_*
// TODO: eliminate?
f021dd7ef6 Split zlib into multiple source files
ca96a445a9 Major Zlib::Inflate refactoring.
f021dd7ef src/org/jruby/ext/zlib/RubyZlib.java
f021dd7ef src/org/jruby/ext/zlib/ZStream.java
-        // TODO: eliminate?
+    // TODO: eliminate?
ca96a445a src/org/jruby/RubyZlib.java
+        // TODO: eliminate?
*_*_*
// Workaround for a bug in Sun's JDK 1.5.x
d9c8103481 Remove commented-out workaround for Java 1.5 bug.
606bd2e150 More of the epic IO/Socket refactoring.
21bbea842d Begin refactoring socket subsystem.
a8981decdb add connect and connent_nonblock to socket
eb1f44e4ba JRUBY-4232: Socket.bind() not available with jruby
56c73ee5eb Fix for JRUBY-2891, Java 5 throwing Error when a UDP socket is already bound.
d9c810348 core/src/main/java/org/jruby/ext/socket/RubyUDPSocket.java
-        //    // Workaround for a bug in Sun's JDK 1.5.x, see
606bd2e15 src/org/jruby/ext/socket/RubySocket.java
-            // Workaround for a bug in Sun's JDK 1.5.x, see
21bbea842 src/org/jruby/ext/socket/RubySocket.java
-            // Workaround for a bug in Sun's JDK 1.5.x, see
-            // Workaround for a bug in Sun's JDK 1.5.x, see
a8981decd src/org/jruby/ext/socket/RubySocket.java
+            // Workaround for a bug in Sun's JDK 1.5.x, see
+            // Workaround for a bug in Sun's JDK 1.5.x, see
eb1f44e4b src/org/jruby/ext/socket/RubySocket.java
+            // Workaround for a bug in Sun's JDK 1.5.x, see
56c73ee5e src/org/jruby/ext/socket/RubyUDPSocket.java
+            // Workaround for a bug in Sun's JDK 1.5.x, see
*_*_*
// Object#to_a is obsolete. We match Ruby's hack until to_a goes away. Then we can
277af88f8e [Truffle] Dead code in parser helpers.
fa938c4c71 [Truffle] Fork the JRuby parser.
62f4721cc5 Fixes #3007. splats with non-array like values.  @JRubyMethod will create two DynamicMethod instances if the method is marked as module=true.  So builtin check needs to check against both builtins
b92102d720 Fixed bad branch merge from f3293274
8d22538bf3 Fixes #1176. Array(array) returns a copy instead of array itself
8bf5c821a2 Fixes #1176. Array(array) returns a copy instead of array itself
bb98e4b8de Move RuntimeHelpers to org.jruby.runtime.Helpers.
9d263047d5 Improve perf of compiled simple argument splats of non-arrays.
fd0fa789b2 Merge "backtrace" branch to master, now that it runs clean on all our various test cases.
7be3e62c35 Multiple microoptz to compiled output: * all int loads now use smallest bytecode possible (biload, siload) * removed extraneous runtime params from several methods * split range instantiation into exclusive/inclusive versions * moved some array/splatting methods out of ASTInterpreter and into RuntimeHelpers * Modified Fixnum creation to use efficient int loading bytecode when possible
6e15491217 merging new interpreter plus a few minor fixes to trunk. JRUBY-185
a629a57098 merging headius branch to trunk
277af88f8 truffle/src/main/java/org/jruby/truffle/parser/Helpers.java
-        // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/Helpers.java
+        // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
62f4721cc core/src/main/java/org/jruby/runtime/Helpers.java
-            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
-            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
b92102d72 core/src/main/java/org/jruby/runtime/Helpers.java
-            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
8d22538bf core/src/main/java/org/jruby/runtime/Helpers.java
+            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
8bf5c821a core/src/main/java/org/jruby/runtime/Helpers.java
+            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
bb98e4b8d src/org/jruby/javasupport/util/RuntimeHelpers.java
bb98e4b8d src/org/jruby/runtime/Helpers.java
-            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
-        // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
+            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
+        // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
9d263047d src/org/jruby/javasupport/util/RuntimeHelpers.java
+        // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
fd0fa789b src/org/jruby/evaluator/ASTInterpreter.java
-            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
7be3e62c3 src/org/jruby/javasupport/util/RuntimeHelpers.java
-            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
+            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
+            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
6e1549121 src/org/jruby/evaluator/EvaluateVisitor.java
6e1549121 src/org/jruby/evaluator/EvaluationState.java
-            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
+            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
a629a5709 src/org/jruby/evaluator/EvaluateVisitor.java
+            // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
*_*_*
// TODO? I think this ends up propagating from normal Java exceptions
d8ef3bd722 IO#sysread and syswrite.
090e1ac962 Restore syswrite and write_nonblock I mercilessly deleted.
b97e022466 Add io/try_nonblock ext for nonblock without exceptions.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
d8ef3bd72 core/src/main/java/org/jruby/RubyIO.java
-                // TODO? I think this ends up propagating from normal Java exceptions
090e1ac96 src/org/jruby/RubyIO.java
+                // TODO? I think this ends up propagating from normal Java exceptions
b97e02246 src/org/jruby/RubyIO.java
-                // TODO? I think this ends up propagating from normal Java exceptions
66b024fed src/org/jruby/RubyIO.java
+                // TODO? I think this ends up propagating from normal Java exceptions
*_*_*
Process streams get Channel.newChannel()ed into FileChannel but are not actually
6884f63c4f Restore minimal part of old IO for jossl 0.9.5 to work.
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
23d786f79b Additional fixes to get reopen, popen, and seeking working correctly with various types of streams.
6884f63c4 core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
+     * Process streams get Channel.newChannel()ed into FileChannel but are not actually
a6f20b63a core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
-     * Process streams get Channel.newChannel()ed into FileChannel but are not actually
23d786f79 src/org/jruby/util/io/ChannelDescriptor.java
+     * Process streams get Channel.newChannel()ed into FileChannel but are not actually
*_*_*
// FIXME: Remove this from grammars.
c3e7c29ff2 Holy cannelloni.  Dead code removal in ParserSupport
9467258b58 IDE Position removal start (see JRubyParser project on Kenai if you need this)
c3e7c29ff core/src/main/java/org/jruby/parser/ParserSupport.java
-    // FIXME: Remove this from grammars.
9467258b5 src/org/jruby/parser/ParserSupport.java
+    // FIXME: Remove this from grammars.
*_*_*
FIXME: any good reason to have two identical methods? (same as remove_class_variable)
632441f330 deduplicating code and deprecating removeCvar
95c346df53 Vars have landed. Does not include concurrent SymbolTable (will follow in the next day or so). Also need multi-threaded unit tests, work in progress.
632441f33 src/org/jruby/RubyModule.java
-     * FIXME: any good reason to have two identical methods? (same as remove_class_variable)
95c346df5 src/org/jruby/RubyModule.java
+     * FIXME: any good reason to have two identical methods? (same as remove_class_variable)
*_*_*
// SSS FIXME: Are we guaranteed that we splats dont head to multiple-assignment nodes!
2ceae0f241 Fix incorrect IR for yield instructions
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
effd0cff49 - Fixed bug with not handling splats in arguments (method, block, multiple-assignment)
2ceae0f24 src/org/jruby/compiler/ir/IRBuilder.java
-                // SSS FIXME: Are we guaranteed that we splats dont head to multiple-assignment nodes!  i.e. |*(a,b)|?
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-                // SSS FIXME: Are we guaranteed that we splats dont head to multiple-assignment nodes!  i.e. |*(a,b)|?
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+                // SSS FIXME: Are we guaranteed that we splats dont head to multiple-assignment nodes!  i.e. |*(a,b)|?
effd0cff4 src/org/jruby/compiler/ir/IR_Builder.java
+                // SSS FIXME: Are we guaranteed that we splats dont head to multiple-assignment nodes!  i.e. |*(a,b)|?
*_*_*
// TODO: This isn't an exact port of MRI's pipe behavior, so revisit
dec952b476 De-version IO
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
dec952b47 core/src/main/java/org/jruby/RubyIO.java
-        // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
66b024fed src/org/jruby/RubyIO.java
+        // TODO: This isn't an exact port of MRI's pipe behavior, so revisit
*_*_*
// FIXME: these descriptions should probably be moved out
# shas =  1
*_*_*
// TODO: don't bother passing when fcall or vcall
0fb8adf327 Revert "Revert to re-green master"
7eac59dedc Revert "HEADIUS"
96034ed2a3 HEADIUS
13a08fa681 Revert to re-green master
25fdaa3bb8 Plug in specialized float RHS 1-arg calls, as in 1.7.
dcd9597d78 Plug in specialized fixnum RHS 1-arg calls, as in 1.7.
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
346b678e2e Allow varargs target methods to be called with [] call sites.
c689af6b26 Add direct boolean paths for Fixnum RHS boolean operations.
ea1cb9b078 Add float RHS operators to indy fast path logic.
53365d3f0c Fix JRUBY-5871: java.lang.NegativeArraySizeException from RubyEnumerator (after JITed)
06c26424d5 Improvements for attribute assignment.
1d1c6bad60 Initial unguarded Fixnum-RHS operator support through invokedynamic.
31a60fe36a Fix a number of arg mismatches and RHS return value for attr assignment with invokedynamic.
a5503a49df Move back to an atomic generation ID and install some simple type/modification guards for dynopt calls.
e26ff6b589 Basic plumbing to get invokedynamic wired into dispatch. Only supports single-arg calls atm, and isn't doing any of the wrapping logic for non-local jumps, etc.
cdfdc56984 Merge branch 'fastmath'
d531d8f51c Improvements to move closer to frameless execution. * Modify ASTInspector to have a more find-grained understanding of what frame bits are used. * Hook dynamic invocation via InvocationCompiler.invokeDynamic into caller-passing CallSite methods.
0fb8adf32 core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
7eac59ded core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
96034ed2a core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
13a08fa68 core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
25fdaa3bb core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
dcd9597d7 core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
ed54aab18 core/src/main/java/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
ed54aab18 core/src/main/java/org/jruby/compiler/impl/StandardInvocationCompiler.java
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
346b678e2 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
c689af6b2 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
ea1cb9b07 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
53365d3f0 src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
06c26424d src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
1d1c6bad6 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
31a60fe36 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
a5503a49d src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
e26ff6b58 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
cdfdc5698 src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
d531d8f51 src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
*_*_*
// TODO: Make callCoerced work in block context...then fix downto
# shas =  1
*_*_*
// TODO: just adding first one right now...add in signature-guessing logic
02b8e01b46 Remove MiniJava and all the tendrils it extended through JRuby. Useful remainder moved to org.jruby.java.codegen.RealClassGenerator.
3bb6b82f87 Early work on a new Java layer that's lighter-weight, more "raw", and more explicit.
02b8e01b4 src/org/jruby/java/MiniJava.java
-            // TODO: just adding first one right now...add in signature-guessing logic
-            // TODO: just adding first one right now...add in signature-guessing logic
3bb6b82f8 src/org/jruby/java/MiniJava.java
+            // TODO: just adding first one right now...add in signature-guessing logic
+            // TODO: just adding first one right now...add in signature-guessing logic
*_*_*
// TODO: not sure that we should skip calling join() altogether
# shas =  1
*_*_*
but its much safer for COW (it prevents not shared Strings with begin != 0)
# shas =  1
*_*_*
// TODO: proper algorithm to set the precision
b3342e7752 fix GH-2524 on jruby-1_7: improved algorithm to set the precision
8fec7f43fd fix GH-2524 on master: improved algorithm to set the precision
9ffe8821d5 De-version BigDecimal
693b9ef25a JRUBY-6556: BigDecimal divided by Rational gives nil in --1.9 mode
2f7d092e20 Fixed numerous rubyspec failures for BigDecimal's #quo #div and #/.
b3342e775 core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-        // TODO: proper algorithm to set the precision
8fec7f43f core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-        // TODO: proper algorithm to set the precision
9ffe8821d core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-        // TODO: proper algorithm to set the precision
693b9ef25 src/org/jruby/ext/bigdecimal/RubyBigDecimal.java
         // TODO: proper algorithm to set the precision
+        // TODO: proper algorithm to set the precision
2f7d092e2 src/org/jruby/RubyBigDecimal.java
+        // TODO: proper algorithm to set the precision
*_*_*
// Reset value map if this instruction is the start/end of a basic block
9250f254c9 Fix #2916: Run local opts pass before tmp var opts
de4bbdb33f - Cleaned up code to enable running compiler passes without having to write a lot of same boilerplate code all the time.  This will keep evolving and changing.  Just the first pass.
c32612f090 - Implemented an optimization interface to run different opts on a scope.
ddaf4c565c - Moved all operands to operands/ and instructions to instructions/ and fixed up imports.  Added some commented out code to start running peephole opts on the IR -- which will also serve as a basis for SCCP passes later on.
9250f254c core/src/main/java/org/jruby/ir/passes/LocalOptimizationPass.java
+        // Reset value map if this instruction is the start/end of a basic block
         // Reset value map if this instruction is the start/end of a basic block
de4bbdb33 src/org/jruby/compiler/ir/IR_ScopeImpl.java
-            // Reset value map if this instruction is the start/end of a basic block
c32612f09 src/org/jruby/compiler/ir/opts/PeepholeOpt.java
+            // Reset value map if this instruction is the start/end of a basic block
ddaf4c565 src/org/jruby/compiler/ir/IR_ScopeImpl.java
+            // Reset value map if this instruction is the start/end of a basic block
*_*_*
// FIXME: This is copied code from RubyArray. Both RE, Struct, and Array should share one imp
# shas =  1
*_*_*
// We always prepend an org.jruby.proxy package to the beginning
# shas =  1
*_*_*
// we should try to make LoadPath a special array object.
28663aa441 Add logic to LoadService for coercing load path entries to string.
fa0e97a126 Remove most toString dependencies, fixing a bug in ByteList#dup(length) and hopefully improve performance of StringIO#gets
28663aa44 src/org/jruby/runtime/load/LoadService.java
             // we should try to make LoadPath a special array object.
+            // we should try to make LoadPath a special array object.
fa0e97a12 src/org/jruby/runtime/load/LoadService.java
+            // we should try to make LoadPath a special array object.
*_*_*
// TODO: People extending GzipWriter/reader will break. Find better way here.
049de281a5 De-version zlib library
a0f78c7a2f GH #552: Zlib::GzipWriter.wrap in 1.9 mode accepts only one argument
f021dd7ef6 Split zlib into multiple source files
714843dd03 JRUBY-2405: Zlib::GzipFile.wrap has bad signature JRUBY-2406: StringIO.new "", "r+" (rb+) is not open for writing JRUBY-2407: StringIO.new does not accept MODE constants
049de281a core/src/main/java/org/jruby/ext/zlib/RubyGzipFile.java
-        // TODO: People extending GzipWriter/reader will break.  Find better way here.
a0f78c7a2 src/org/jruby/ext/zlib/RubyGzipFile.java
+        // TODO: People extending GzipWriter/reader will break.  Find better way here.
f021dd7ef src/org/jruby/ext/zlib/RubyGzipFile.java
f021dd7ef src/org/jruby/ext/zlib/RubyZlib.java
+        // TODO: People extending GzipWriter/reader will break.  Find better way here.
-            // TODO: People extending GzipWriter/reader will break.  Find better way here.
714843dd0 src/org/jruby/RubyZlib.java
+            // TODO: People extending GzipWriter/reader will break.  Find better way here.
*_*_*
// TODO: catch exception if constant is already set by other
55927e21ea Revert my refactoring in favor of version based on @kares work.
5755383a4b Begin refactoring JavaClass binding logic.
bb26fcb07e simplify (internal) installer classes + initializer happens under lock - no need to sync
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
1d47e4ba3f apply appropriate synchronization to MethodCallback
55927e21e core/src/main/java/org/jruby/javasupport/JavaClass.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/ConstantField.java
+                // TODO: catch exception if constant is already set by other
-            // TODO: catch exception if constant is already set by other
5755383a4 core/src/main/java/org/jruby/javasupport/JavaClass.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/ConstantField.java
-                // TODO: catch exception if constant is already set by other
+            // TODO: catch exception if constant is already set by other
bb26fcb07 core/src/main/java/org/jruby/javasupport/JavaClass.java
-                // TODO: catch exception if constant is already set by other
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-                // TODO: catch exception if constant is already set by other
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+                // TODO: catch exception if constant is already set by other
1d47e4ba3 src/org/jruby/javasupport/JavaClass.java
+                // TODO: catch exception if constant is already set by other
*_*_*
// FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
4e87bc6e15 Remove ASTCompiler19 and collapse logic into ASTCompiler.
37355c58ec Implement remaining 1.9 method args logic, add tests for it, and fix masgn19 test.
0b49ea917c Implement remaining 1.9 method args logic, add tests for it, and fix masgn19 test.
20225f17b2 Add support for post args in method arguments in 1.9 compiler.
d11f26175e Delegate all equivalent bits from ASTCompiler19 to ASTCompiler.
1d6c408a47 First round of twiddling to get 1.9 compilation work under way.
98064f1836 Many fixes to compiler to allow methods that previously did not compile to compile successfully. A total rewrite of method argument processing so all scenarios work (including def foo(a = (b = true; 1)) sorts of situations). Move common variable-compilation code to an abstract base. Fix DefaultMethod to jit methods without bodies appropriately, and make methods without bodies or opt args totally free of frame and scope costs. Fix the quicksort test so it doesn't take 15 minutes to parse.
4e87bc6e1 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-                            // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
37355c58e src/org/jruby/compiler/ASTCompiler19.java
-                    // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
0b49ea917 src/org/jruby/compiler/ASTCompiler19.java
-                    // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
20225f17b src/org/jruby/compiler/ASTCompiler19.java
+                            // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
d11f26175 src/org/jruby/compiler/ASTCompiler19.java
-                            // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
1d6c408a4 src/org/jruby/compiler/ASTCompiler19.java
+                            // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
98064f183 src/org/jruby/compiler/NodeCompilerFactory.java
+                    // FIXME: Somehow I'd feel better if this could get the appropriate var index from the ArgumentNode
*_*_*
// small hack to save a cast later on
55927e21ea Revert my refactoring in favor of version based on @kares work.
be31344448 Pull all initialize logic up and out into initializer classes.
5755383a4b Begin refactoring JavaClass binding logic.
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
beaaeb89b6 JRUBY-814: Multiple improvements to Java integration (was: Java method get lost.) [Bill Dortch]
55927e21e core/src/main/java/org/jruby/javasupport/JavaClass.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/NamedInstaller.java
+        // small hack to save a cast later on
-    // small hack to save a cast later on
be3134444 core/src/main/java/org/jruby/javasupport/JavaClass.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/NamedInstaller.java
-        // small hack to save a cast later on
+    // small hack to save a cast later on
5755383a4 core/src/main/java/org/jruby/javasupport/JavaClass.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/NamedInstaller.java
-        // small hack to save a cast later on
+    // small hack to save a cast later on
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-        // small hack to save a cast later on
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+        // small hack to save a cast later on
beaaeb89b src/org/jruby/javasupport/JavaClass.java
+        // small hack to save a cast later on
*_*_*
// FIXME: We should really not be creating the dynamic scope for the root
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
fe07751fb2 YARV updates (big machine-based frame; refactorings)
b317014af9 JRUBY-877: Sources not available during command-line debugging
174e3d0146 damn the torpedos! full speed ahead! landing enebo_lexical branch changes plus TC-passing optimizations and a few small compiler updates
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/ParserConfiguration.java
-        // FIXME: We should really not be creating the dynamic scope for the root
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/ParserConfiguration.java
+        // FIXME: We should really not be creating the dynamic scope for the root
fe07751fb src/org/jruby/parser/RubyParserConfiguration.java
-        // FIXME: We should really not be creating the dynamic scope for the root
b317014af src/org/jruby/parser/ParserConfiguration.java
+        // FIXME: We should really not be creating the dynamic scope for the root
174e3d014 src/org/jruby/parser/RubyParserConfiguration.java
+        // FIXME: We should really not be creating the dynamic scope for the root
*_*_*
// TODO: no java stringity
ba93d1ae03 Minor optimizations for Thread#status.
c4c035bee9 First rework of thread events, with ThreadService acting as arbitrator and a substantial reduction of complexity. Still needs more work, especially wrt blocking IO.
ba93d1ae0 src/org/jruby/RubyThread.java
-            // TODO: no java stringity
c4c035bee src/org/jruby/RubyThread.java
+            // TODO: no java stringity
*_*_*
SSS FIXME: Used anywhere? I don't see calls to this anywhere
69a019ddc9 Additional pieces of defined? IR-ed
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
85f7641591 - Built for node, iter node, and a few others
69a019ddc src/org/jruby/compiler/ir/IRBuilder.java
- * SSS FIXME: Used anywhere?  I don't see calls to this anywhere
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
- * SSS FIXME: Used anywhere?  I don't see calls to this anywhere
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+ * SSS FIXME: Used anywhere?  I don't see calls to this anywhere
85f764159 src/org/jruby/compiler/ir/IR_Builder.java
+ * SSS FIXME: Used anywhere?  I don't see calls to this anywhere
*_*_*
// FIXME: I use a for block to implement END node because we need a proc which captures
7621334c9b Start removing old interpreter
dbcdcdbac4 Tom Bomb! New Interpreter landing
94224312a6 For  some reason DefaultRubyParser.y and testEval did not commit from my commit yesterday for JRUBY-228 JRUBY-2: END syntax does not appear to fire correctly
7621334c9 core/src/main/java/org/jruby/ast/PreExeNode.java
-        // FIXME: I use a for block to implement END node because we need a proc which captures
dbcdcdbac src/org/jruby/ast/PreExeNode.java
dbcdcdbac src/org/jruby/evaluator/ASTInterpreter.java
+        // FIXME: I use a for block to implement END node because we need a proc which captures
-        // FIXME: I use a for block to implement END node because we need a proc which captures
94224312a src/org/jruby/evaluator/EvaluationState.java
+        // FIXME: I use a for block to implement END node because we need a proc which captures
*_*_*
// these extra arrays are really unfortunate
# shas =  1
*_*_*
// TODO: This is almost RubyModule#instance_methods on the metaClass. Perhaps refactor.
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
31d6374bdd New method caching system
6eb8df5bd src/org/jruby/RubyBasicObject.java
6eb8df5bd src/org/jruby/RubyObject.java
+    // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
-    // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
31d6374bd src/org/jruby/RubyObject.java
+    // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
*_*_*
// FIXME: We should be getting this from the runtime rather than assume none?
52192272b2 Initial stab at magic comments...bleh
8981241985 __ENCODING__ returns an encoding
52192272b src/org/jruby/ast/EncodingNode.java
-        // FIXME: We should be getting this from the runtime rather than assume none?
898124198 src/org/jruby/ast/EncodingNode.java
+        // FIXME: We should be getting this from the runtime rather than assume none?
*_*_*
// FIXME: This is pretty gross
71df91122e move and update match-data initialization into one place - make sure frozen str is set
72875b9011 Fix for JRUBY-3625: Multi-threading issue with RegExp
71df91122 core/src/main/java/org/jruby/RubyMatchData.java
71df91122 core/src/main/java/org/jruby/RubyRegexp.java
+        // FIXME: This is pretty gross; we should have a cleaner initialization
-        // FIXME: This is pretty gross; we should have a cleaner initialization
72875b901 src/org/jruby/RubyRegexp.java
+        // FIXME: This is pretty gross; we should have a cleaner initialization
*_*_*
// FIXME: this should go somewhere more generic -- maybe IdUtil
cd313f38a4 [Truffle] Finish draining the util swamp.
92c1ab5a70 [Truffle] Privatise code for AOT.
1a87714ed4 Initial round of work on new ivar table.
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
95c346df53 Vars have landed. Does not include concurrent SymbolTable (will follow in the next day or so). Also need multi-threaded unit tests, work in progress.
cd313f38a truffle/src/main/java/org/jruby/truffle/util/IdUtil.java
-    // FIXME: this should go somewhere more generic -- maybe IdUtil
92c1ab5a7 truffle/src/main/java/org/jruby/truffle/util/IdUtil.java
+    // FIXME: this should go somewhere more generic -- maybe IdUtil
1a87714ed src/org/jruby/RubyBasicObject.java
1a87714ed src/org/jruby/util/IdUtil.java
-    // FIXME: this should go somewhere more generic -- maybe IdUtil
+    // FIXME: this should go somewhere more generic -- maybe IdUtil
bf8db0bc4 src/org/jruby/RubyBasicObject.java
bf8db0bc4 src/org/jruby/RubyObject.java
+    // FIXME: this should go somewhere more generic -- maybe IdUtil
-    // FIXME: this should go somewhere more generic -- maybe IdUtil
95c346df5 src/org/jruby/RubyObject.java
+    // FIXME: this should go somewhere more generic -- maybe IdUtil
*_*_*
// temporary hack to handle non-Ruby values
# shas =  1
*_*_*
// SSS FIXME: Where is this set up? How is this diff from ClassVarDeclNode??
ef2e37f2fc Minor IR code reorg + comments + FIXMEs added
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
594441a0e8 - Converted BTRUE_Instr to BEQ_Instr with a comparison with BooleanLiteral.TRUE; Fixed earlier buggy translation of OpAsgnAnd node; Added GET_CVAR/PUT_CVAR instruction pair for class variables
ef2e37f2f src/org/jruby/compiler/ir/IRBuilder.java
-    // SSS FIXME: Where is this set up?  How is this diff from ClassVarDeclNode??
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-    // SSS FIXME: Where is this set up?  How is this diff from ClassVarDeclNode??
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+    // SSS FIXME: Where is this set up?  How is this diff from ClassVarDeclNode??
594441a0e src/org/jruby/compiler/ir/IR_Builder.java
+    // SSS FIXME: Where is this set up?  How is this diff from ClassVarDeclNode??
*_*_*
// FIXME: I don't like the null checks here
83c50c049e More fixes for IO + NONBLOCK behavior: read, read(n), readpartial and read_nonblock.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
83c50c049 src/org/jruby/RubyIO.java
-            // FIXME: I don't like the null checks here
66b024fed src/org/jruby/RubyIO.java
+            // FIXME: I don't like the null checks here
*_*_*
// modules are included with a shim class
277af88f8e [Truffle] Dead code in parser helpers.
fa938c4c71 [Truffle] Fork the JRuby parser.
e84352bd04 remove openssl since it has its own repo now - closes PR #1763
70727bf396 make sure code actually compiles against JRuby 1.7.x (as well as 1.6.8)
a62d31eea1 make sure code actually compiles against JRuby 1.7.x (as well as 1.6.8)
bb98e4b8de Move RuntimeHelpers to org.jruby.runtime.Helpers.
e605715e69 Adding super compilation, disabled until the whole findImplementers thing is tidied up in the generated code.
7b950a74d8 Abstracting compilation of local variable reads and writes; moving toward a pluggable compiler structure to support changing the mechanisms for compiling particular concepts on a per-method basis.
9c7e5974ff De-abstractifying (and thereby simplifying) DynamicMethod class hierarchy.
d86383002e Better encapsulating method_missing decision logic.
42ec5970fe Multiple updates to the compiler to allow JITting to work safely (I think!) again. Also some possible fixes for STI. JIT compiler is NOT yet enabled by default.
5b3a7565ea optz: only do callMethod() implementer search for non-classes
277af88f8 truffle/src/main/java/org/jruby/truffle/parser/Helpers.java
-            // modules are included with a shim class; we must find that shim to handle super() appropriately
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/Helpers.java
+            // modules are included with a shim class; we must find that shim to handle super() appropriately
e84352bd0 ext/openssl/src/main/java/org/jruby/ext/openssl/Utils.java
-            // modules are included with a shim class; we must find that shim to handle super() appropriately
70727bf39 ext/openssl/src/main/java/org/jruby/ext/openssl/Utils.java
+            // modules are included with a shim class; we must find that shim to handle super() appropriately
a62d31eea ext/openssl/src/main/java/org/jruby/ext/openssl/Utils.java
+            // modules are included with a shim class; we must find that shim to handle super() appropriately
bb98e4b8d src/org/jruby/javasupport/util/RuntimeHelpers.java
bb98e4b8d src/org/jruby/runtime/Helpers.java
-            // modules are included with a shim class; we must find that shim to handle super() appropriately
+            // modules are included with a shim class; we must find that shim to handle super() appropriately
e605715e6 src/org/jruby/internal/runtime/methods/DefaultMethod.java
e605715e6 src/org/jruby/javasupport/util/CompilerHelpers.java
-            // modules are included with a shim class; we must find that shim to handle super() appropriately
+            // modules are included with a shim class; we must find that shim to handle super() appropriately
7b950a74d src/org/jruby/internal/runtime/methods/CompiledMethod.java
-//            // modules are included with a shim class; we must find that shim to handle super() appropriately
9c7e5974f src/org/jruby/internal/runtime/methods/CompiledMethod.java
9c7e5974f src/org/jruby/internal/runtime/methods/DefaultMethod.java
9c7e5974f src/org/jruby/internal/runtime/methods/DynamicMethod.java
+//            // modules are included with a shim class; we must find that shim to handle super() appropriately
+            // modules are included with a shim class; we must find that shim to handle super() appropriately
-            // modules are included with a shim class; we must find that shim to handle super() appropriately
d86383002 src/org/jruby/RubyObject.java
d86383002 src/org/jruby/internal/runtime/methods/DynamicMethod.java
-            // modules are included with a shim class; we must find that shim to handle super() appropriately
-            // modules are included with a shim class; we must find that shim to handle super() appropriately
+            // modules are included with a shim class; we must find that shim to handle super() appropriately
42ec5970f src/org/jruby/RubyObject.java
+            // modules are included with a shim class; we must find that shim to handle super() appropriately
5b3a7565e src/org/jruby/RubyObject.java
+        	// modules are included with a shim class; we must find that shim to handle super() appropriately
*_*_*
// FIXME: Don't allow multiple threads to do this at once
# shas =  1
*_*_*
// FIXME: Clearing read buffer here...is this appropriate?
6884f63c4f Restore minimal part of old IO for jossl 0.9.5 to work.
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
aa4ffa1be5 Fix botched merge that doubled new file content.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
73f7a906d3 I have seen the promised land. Refactored all IO into a single IO handler based on the new "DescriptorLike" abstraction (name pending a highly likely change). There's still some branching in the IOHandler methods for files versus non-file streams to clean up, but it's at least all in one place. Had to disable one regressed test for a -1/255 byte in a pipe, but otherwise everything is passing. and Rails tarts up and even prints color logging to the console now, something it's never done before. go figure.
6884f63c4 core/src/main/java/org/jruby/util/io/ChannelStream.java
+        // FIXME: Clearing read buffer here...is this appropriate?
a6f20b63a core/src/main/java/org/jruby/util/io/ChannelStream.java
-        // FIXME: Clearing read buffer here...is this appropriate?
aa4ffa1be src/org/jruby/util/io/ChannelStream.java
-        // FIXME: Clearing read buffer here...is this appropriate?
66b024fed src/org/jruby/util/ChannelStream.java
66b024fed src/org/jruby/util/io/ChannelStream.java
-        // FIXME: Clearing read buffer here...is this appropriate?
+        // FIXME: Clearing read buffer here...is this appropriate?
+        // FIXME: Clearing read buffer here...is this appropriate?
73f7a906d src/org/jruby/util/IOHandlerNioBuffered.java
+        // FIXME: Clearing read buffer here...is this appropriate?
*_*_*
// SSS FIXME: Should this be Operand or CompoundString?
389b7657bd [IR] Convert some operands to instructions
34158207b4 [IR] Clean up operands
6a3bf2b586 - Some more nodes translated + added isConstant method to BacktickString
86e7fc9281 - Cleaned up DefnNode, and translated DSymbolNode
389b7657b core/src/main/java/org/jruby/ir/operands/DynamicSymbol.java
-    // SSS FIXME: Should this be Operand or CompoundString?
34158207b src/org/jruby/compiler/ir/operands/DynamicReference.java
34158207b src/org/jruby/compiler/ir/operands/DynamicSymbol.java
-        // SSS FIXME: Should this be Operand or CompoundString?
+    // SSS FIXME: Should this be Operand or CompoundString?
6a3bf2b58 src/org/jruby/compiler/ir/DynamicReference.java
6a3bf2b58 src/org/jruby/compiler/ir/DynamicSymbol.java
+        // SSS FIXME: Should this be Operand or CompoundString?
-      // SSS FIXME: Should this be Operand or CompoundString?
86e7fc928 src/org/jruby/compiler/ir/DynamicSymbol.java
+      // SSS FIXME: Should this be Operand or CompoundString?
*_*_*
// this covers the rare case of lower-case class names (and thus will
# shas =  1
*_*_*
// TODO: number formatting here
# shas =  1
*_*_*
// If variables were added then we may need to grow the dynamic scope to match the static
8b6eec17dd Remove TruffleRuby.
27da7b11f5 [Truffle] Parser was a pretty shallow abstraction - remove it.
fa938c4c71 [Truffle] Fork the JRuby parser.
f197e8b87f Remove commented out code (you're dead to me)
16e5550655 Lexer jumbo patch.  Speeds up general parsing 10-15%.  Cold parses are about 30% faster.   Code has been refactored to the point that additional optimizations can be considered (like bytelist identifiers for alloc-less identifiers; same for uninterpolated string nodes).
174e3d0146 damn the torpedos! full speed ahead! landing enebo_lexical branch changes plus TC-passing optimizations and a few small compiler updates
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/TranslatorDriver.java
-        // If variables were added then we may need to grow the dynamic scope to match the static
27da7b11f truffle/src/main/java/org/jruby/truffle/parser/TranslatorDriver.java
27da7b11f truffle/src/main/java/org/jruby/truffle/parser/parser/Parser.java
+        // If variables were added then we may need to grow the dynamic scope to match the static
-        // If variables were added then we may need to grow the dynamic scope to match the static
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/Parser.java
+        // If variables were added then we may need to grow the dynamic scope to match the static
f197e8b87 src/org/jruby/parser/Parser.java
-        // If variables were added then we may need to grow the dynamic scope to match the static
16e555065 src/org/jruby/parser/Parser.java
+        // If variables were added then we may need to grow the dynamic scope to match the static
174e3d014 src/org/jruby/parser/Parser.java
+        // If variables were added then we may need to grow the dynamic scope to match the static
*_*_*
// FIXME: Can get optimized for IEqlNode
7621334c9b Start removing old interpreter
2c813e9d06 Re-landing case/when AST refactoring
75f48785fa Revert "Change how we do case/when in AST.  Remove rewriter tests and position tests until they move to jruby-parser project on Kenai"
d7361b88d4 Change how we do case/when in AST.  Remove rewriter tests and position tests until they move to jruby-parser project on Kenai
7621334c9 core/src/main/java/org/jruby/ast/WhenOneArgNode.java
-    // FIXME: Can get optimized for IEqlNode
2c813e9d0 src/org/jruby/ast/WhenOneArgNode.java
+    // FIXME: Can get optimized for IEqlNode
75f48785f src/org/jruby/ast/WhenOneArgNode.java
-    // FIXME: Can get optimized for IEqlNode
d7361b88d src/org/jruby/ast/WhenOneArgNode.java
+    // FIXME: Can get optimized for IEqlNode
*_*_*
// This is a dummy scope
0e0ffe2e5c Create ContextAwareBlockBody to Dry up common blockbody code
cdb1c7f5d2 Fix for JRUBY-2237, provide a (currently bogus) staticscope for Method#to_proc procs.
0e0ffe2e5 src/org/jruby/runtime/MethodBlock.java
-    // This is a dummy scope; we should find a way to make that more explicit
cdb1c7f5d src/org/jruby/runtime/MethodBlock.java
+    // This is a dummy scope; we should find a way to make that more explicit
*_*_*
// TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here
87404f0e15 Fix JRUBY-6647
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
87404f0e1 src/org/jruby/RubyStruct.java
-        // TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here, but it's unclear how Structs
b390103c2 src/org/jruby/RubyStruct.java
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR may be ok here, but it's unclear how Structs
*_*_*
// SSS FIXME: Deprecated! Going forward
c445895b18 [IR] Some API cleanup in IRScope.java -- the original instruction list cannot be used after CFG has been build since the CFG now has the canonical copy of instructions.  Fixed JVM.java to use the CFG to compile code.
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
dc0b3a049c - Cleaned up the scope setup some more; Integrated (not fully tested &   debugged) closures into live variable anaysis code; Added instructions   to allocate heap frame, load / store variables from frame (not   integrated yet);
8d92c98c80 - Some more code cleanup + implemented dead code elimination pass
c445895b1 src/org/jruby/compiler/ir/IRScope.java
-    // SSS FIXME: Deprecated!  Going forward, all instructions should come from the CFG
8dff5b2a5 src/org/jruby/compiler/ir/IR_ExecutionScope.java
-    // SSS FIXME: Deprecated!  Going forward, all instructions should come from the CFG
f9c3ccc4a src/org/jruby/compiler/ir/IRExecutionScope.java
+    // SSS FIXME: Deprecated!  Going forward, all instructions should come from the CFG
dc0b3a049 src/org/jruby/compiler/ir/IR_ExecutionScope.java
dc0b3a049 src/org/jruby/compiler/ir/IR_ScopeImpl.java
+    // SSS FIXME: Deprecated!  Going forward, all instructions should come from the CFG
-    // SSS FIXME: Deprecated!  Going forward, all instructions should come from the CFG
8d92c98c8 src/org/jruby/compiler/ir/IR_ScopeImpl.java
+    // SSS FIXME: Deprecated!  Going forward, all instructions should come from the CFG
*_*_*
// TODO: we should be able to optimize this quite a bit post-1.0. JavaClass already
165ec891f5 Allow java proxy class to be overriden for Android support
0b9733a012 JRUBY-903: Java interface modules (by Bill Dortch) Update some samples to use newer syntax Remove miscellaneous unusied imports
165ec891f src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
-    // TODO: we should be able to optimize this quite a bit post-1.0.  JavaClass already
0b9733a01 src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
+    // TODO: we should be able to optimize this quite a bit post-1.0.  JavaClass already
*_*_*
// FIXME: set up a call configuration for this
# shas =  1
*_*_*
// TODO: It would be nice to throw a better error for this
6884f63c4f Restore minimal part of old IO for jossl 0.9.5 to work.
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
44444a4f0a Make reads/writes on a server socket not blow up horribly.
6884f63c4 core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
+        // TODO: It would be nice to throw a better error for this
+        // TODO: It would be nice to throw a better error for this
a6f20b63a core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
-        // TODO: It would be nice to throw a better error for this
-        // TODO: It would be nice to throw a better error for this
44444a4f0 src/org/jruby/util/io/ChannelDescriptor.java
+        // TODO: It would be nice to throw a better error for this
+        // TODO: It would be nice to throw a better error for this
*_*_*
// this is a rather ugly hack, but similar to MRI. See hash.c:ruby_setenv and similar in MRI
# shas =  1
*_*_*
// don't bother to check if final method
fe8302dcd3 Multiple cleanups, refactorings, improvements for standard Java invocation:
52343aa0f6 Move Java method invokers to specific-arity DynamicMethod.call logic and eliminate all that extra arg array boxing.
0a60ffec98 Bill's fixes for JRUBY-664 to allow final methods in base classes to be called from Ruby child classes.
fe8302dcd src/org/jruby/javasupport/JavaMethod.java
-                // don't bother to check if final method, it won't
52343aa0f src/org/jruby/javasupport/JavaMethod.java
+                // don't bother to check if final method, it won't
0a60ffec9 src/org/jruby/javasupport/JavaMethod.java
+                // don't bother to check if final method, it won't be there
*_*_*
// We clone dynamic scope because this will be a new instance of a block. Any previously
f568e2eb6d Clean up block cloning for instance_eval and friends.
35141a4aa4 Clean up block cloning for instance_eval and friends.
0e0ffe2e5c Create ContextAwareBlockBody to Dry up common blockbody code
99f983249e Rejigger how binding works with files and line numbers. It appears that we weren't tracking everything we needed in a binding. Instead of holding the current frame plus the current file+line, we held only the current frame. This led to us constantly setting and clearing the frame's line numbers in an attempt to get traces to line up. With binding carrying the original file+line, that juggling is no longer necessary. This fixes an eval spec and should improve backtraces across eval calls, which had numerous peculiarities before. This also fixes JRUBY-2328: Overriding require causes eval to give wrong __FILE__ in certain circumstances.
a88ab9427f Because Binding construction always duplicates the passed frame, any callers pre-duping were doing extra work. This logic may move back out of Binding at some point, to reduce the cost of constructing a new block, but for now it's simplest to centralize and de-duplicate it.
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
c2bbf517f6 More block cleanup, some minor doco.
fd7260c722 Block refactoring: force all subclass yield/call/clone methods to accept a binding parameter, isolating binding contents from execution.
94092a404b Ooops, missed adding new InterpretedBlock
43445efc2b More incremental Block refactoring: break into self-standing interpreted, method, and compiled subhierarchies
4bab7c8c05 Pulling binding contents up to a superclass of Block
7936b1a48b Wow, suddenly it's possible to eliminate ICallable and impls completely. This specializes one more Block subclass and should make it possible for blocks to properly report some positioning errors. It should also remove a stack level from eval'ed node-based block execution.
174e3d0146 damn the torpedos! full speed ahead! landing enebo_lexical branch changes plus TC-passing optimizations and a few small compiler updates
f568e2eb6 core/src/main/java/org/jruby/runtime/ContextAwareBlockBody.java
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
35141a4aa core/src/main/java/org/jruby/runtime/ContextAwareBlockBody.java
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
0e0ffe2e5 src/org/jruby/runtime/ContextAwareBlockBody.java
0e0ffe2e5 src/org/jruby/runtime/Interpreted19Block.java
0e0ffe2e5 src/org/jruby/runtime/InterpretedBlock.java
+        // We clone dynamic scope because this will be a new instance of a block.  Any previously
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
99f983249 src/org/jruby/runtime/MethodBlock.java
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
a88ab9427 src/org/jruby/runtime/Binding.java
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
bf8db0bc4 src/org/jruby/runtime/Interpreted19Block.java
+        // We clone dynamic scope because this will be a new instance of a block.  Any previously
c2bbf517f src/org/jruby/runtime/Block.java
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
fd7260c72 src/org/jruby/runtime/Block.java
+        // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
94092a404 src/org/jruby/runtime/InterpretedBlock.java
+        // We clone dynamic scope because this will be a new instance of a block.  Any previously
43445efc2 src/org/jruby/runtime/Block.java
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
4bab7c8c0 src/org/jruby/runtime/Binding.java
+        // We clone dynamic scope because this will be a new instance of a block.  Any previously
7936b1a48 src/org/jruby/runtime/MethodBlock.java
+        // We clone dynamic scope because this will be a new instance of a block.  Any previously
174e3d014 src/org/jruby/runtime/Block.java
+        // We clone dynamic scope because this will be a new instance of a block.  Any previously
*_*_*
// FIXME: ConstDecl could be two seperate classes (or done differently since constNode and name
7621334c9b Start removing old interpreter
df1ae61d89 A couple more callAdapter additions to nodes that can support them Remove annoying generics warnings that IDEs like to give for un typed collections Change boilerplate in ast so that netbeans can have them closed by default (and javadocs will not contain them) Remove last vestiges of serialization from AST MethodIndex moved into DefaultAdapter Smaller smattering and things I cannot remember
7621334c9 core/src/main/java/org/jruby/ast/ConstDeclNode.java
-// FIXME: ConstDecl could be two seperate classes (or done differently since constNode and name
df1ae61d8 src/org/jruby/ast/ConstDeclNode.java
+// FIXME: ConstDecl could be two seperate classes (or done differently since constNode and name
*_*_*
// TODO: what about n arg?
8c2b5f587d do coercion on * and / (same as MRI has been doing since 1.8) ... fixes #2538
0c3b435b77 Fixes for BigDecimal#mult to pass more rubyspecs.
8c2b5f587 core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
+        if (val == null) { // TODO: what about n arg?
-            // TODO: what about n arg?
+        if (val == null) { // TODO: what about n arg?
0c3b435b7 src/org/jruby/RubyBigDecimal.java
+            // TODO: what about n arg?
*_*_*
// FIXME: what should this really be? assert x instanceof RubyComplex
# shas =  1
*_*_*
// SSS FIXME: This should never get called for constant svalues
3be4b70e09 IR: Cleanup of fetchCompileTimeArrayElement
d7e29b3be4 - First pass of copy & constant propagation peephole opt. in place.  Compound values are not handled correcty yet.  I can only propagate the reference to the compound value, not the entire value.  Will fix this in next round of fixes.
3be4b70e0 src/org/jruby/compiler/ir/operands/SValue.java
-        // SSS FIXME: This should never get called for constant svalues
d7e29b3be src/org/jruby/compiler/ir/operands/SValue.java
+        // SSS FIXME: This should never get called for constant svalues
*_*_*
// FIXME: This doesn't actually support anything but String
# shas =  1
*_*_*
// FIXME: figure out why we get null sometimes
c58164f7c6 Cheryr-picked from master
86a75dce0e Change the profiler to make it possible to add custom profiler implementations
1afa9caaa9 Temporal fix for NPEs out of Module.to_s.
c58164f7c core/src/main/java/org/jruby/RubyModule.java
-            if (attached != null) { // FIXME: figure out why we get null sometimes
86a75dce0 core/src/main/java/org/jruby/RubyModule.java
-            if (attached != null) { // FIXME: figure out why we get null sometimes
1afa9caaa src/org/jruby/RubyModule.java
+            if (attached != null) { // FIXME: figure out why we get null sometimes
*_*_*
// TODO: Consider a better way of synchronizing
aaf16814d0 fix compilation error left-over from 7774be525ac71997cd2ed75ef641c47078a704bc
31d6374bdd New method caching system
aaf16814d core/src/main/java/org/jruby/RubyModule.java
-    // TODO: Consider a better way of synchronizing 
31d6374bd src/org/jruby/RubyModule.java
+    // TODO: Consider a better way of synchronizing 
*_*_*
// up to ten, stuff into tmp locals
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
ff7738a32b Expand expr optz for masgn up to 10 elements.
ed54aab18 core/src/main/java/org/jruby/compiler/impl/BaseBodyCompiler.java
-            // up to ten, stuff into tmp locals, load in reverse order, and assign
ff7738a32 src/org/jruby/compiler/impl/BaseBodyCompiler.java
+            // up to ten, stuff into tmp locals, load in reverse order, and assign
*_*_*
// FIXME: needs to be rethought
41d1cea1ed Remove some dead code from Java and JavaSupport.
1dbbe1a37a More JI cleanup:  - Reworked the very expensive and thread-unsafe (and essentially obsolete) proxy extender mechanism. No longer is every new proxy checked against all proxy extenders; since the Rubification of the Java class hierarchy, it's really only necessary to extend the class/module specified in the extend_proxy method. I'd like to see this feature deprecated, as it's redundant now (see forthcoming note on jruby-dev).  - Moved JavaClass instances to a ConcurrentHashMap for faster access.  They had been in a weak hash map, but since almost all of them were linked to proxies that were in non-weak maps, there was really no benefit (and some performance penalties).  - Used concurrent techniques to speed proxy class lookup.  Proxies are now stored with their corresponding JavaClass instances, and can be retrieved with an unsynchronized access.  - Eliminated the ProxyData "dataStruct" that had been attached to the JavaUtilities module; most of the fields (maps) had been eliminated in the work noted above; the remaining fields have been moved to JavaSupport where they may be accessed without a JavaUtilities reference.  - Permanently disabled the the old-style (<) interface implementation syntax (there had been a method to enable it; no more).  - Fixed a bug wherein the colon2 syntax for opening modules was not working if the module had not previously been referenced.  Will open a JIRA and backport to the 1.0 branch.
41d1cea1e src/org/jruby/javasupport/JavaSupport.java
-    // FIXME: needs to be rethought
1dbbe1a37 src/org/jruby/javasupport/JavaSupport.java
+    // FIXME: needs to be rethought
*_*_*
// FIXME: only starting after required args
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
ff9baab4ad Optz for class/sclass/method bodies, to get the usual variable optz and reduce per-entry cost due to recreating the scope.
9b6d602f95 Fix double-assignment of lvars in masgn and bad nil-initialization of root scope.
ed54aab18 core/src/main/java/org/jruby/compiler/impl/StackBasedVariableCompiler.java
-            // FIXME: only starting after required args, since opt args may access others
-            // FIXME: only starting after required args, since opt args may access others
ff9baab4a src/org/jruby/compiler/impl/StackBasedVariableCompiler.java
+            // FIXME: only starting after required args, since opt args may access others
9b6d602f9 src/org/jruby/compiler/impl/StackBasedVariableCompiler.java
+            // FIXME: only starting after required args, since opt args may access others
*_*_*
// TODO: do above but not below for additional newline nodes
7621334c9b Start removing old interpreter
dbcdcdbac4 Tom Bomb! New Interpreter landing
6e15491217 merging new interpreter plus a few minor fixes to trunk. JRUBY-185
7621334c9 core/src/main/java/org/jruby/ast/NewlineNode.java
-        // TODO: do above but not below for additional newline nodes
dbcdcdbac src/org/jruby/ast/NewlineNode.java
dbcdcdbac src/org/jruby/evaluator/ASTInterpreter.java
+        // TODO: do above but not below for additional newline nodes
-                // TODO: do above but not below for additional newline nodes
6e1549121 src/org/jruby/evaluator/EvaluationState.java
+                // TODO: do above but not below for additional newline nodes
*_*_*
// FIXME: Not sure what the semantics of transfer are
c2f126a48e Improve fiber library implementation relative to YRI.
c187d01e4b Adding basic Fiber support using native threads. Fairly primitive, but the non-brutal tests from YARV pass, as well as most demo code I've found online.
c2f126a48 src/org/jruby/libraries/FiberLibrary.java
-        // FIXME: Not sure what the semantics of transfer are
c187d01e4 src/org/jruby/libraries/FiberLibrary.java
+            // FIXME: Not sure what the semantics of transfer are
*_*_*
// using IOInputStream may not be the most performance way
# shas =  1
*_*_*
// SSS FIXME: Maybe this is not really a concern after all …
# shas =  0
*_*_*
// FIXME weakref.rb also does caller(2) here for the backtrace
# shas =  1
*_*_*
// TODO: Is this behavior really desirable? /mov
# shas =  1
*_*_*
// TODO: this method is not present in MRI!
b7bfb4e174 File#lchown and #lchmod do not exist in MRI.
e489cbd60a JRUBY-2397: File#chown and File.chown should allow nil as first argument
b7bfb4e17 core/src/main/java/org/jruby/RubyFile.java
-    // TODO: this method is not present in MRI!
e489cbd60 src/org/jruby/RubyFile.java
+    // TODO: this method is not present in MRI!
*_*_*
// Yow...this is still ugly
95db8a8ba4 More faithful port of strio_read.
e14ea63bad Numerous fixes for StringIO in 1.9 mode.
a5033ae141 Fix and spec update for JRUBY-3610: StringIO#read given a buffer raises ArrayIndex error
95db8a8ba core/src/main/java/org/jruby/ext/stringio/RubyStringIO.java
-            // Yow...this is still ugly
e14ea63ba core/src/main/java/org/jruby/ext/stringio/RubyStringIO.java
+            // Yow...this is still ugly
a5033ae14 src/org/jruby/RubyStringIO.java
+            // Yow...this is still ugly
*_*_*
// SSS FIXME: This method (at least in the context of multiple assignment) is a little weird
ffae988526 [IR] Some minor code cleanup
5fade4a7c1 More fixup of JRubyImplCallInstr
fa309a4a14 - Got rid of IS_DEFINED_Instr since it was erroneous; commented out is_defined code that I had previously written; implemented multiple-assignment node by adding GET_ARRAY_Instr
ffae98852 src/org/jruby/compiler/ir/instructions/JRubyImplCallInstr.java
-       // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
5fade4a7c src/org/jruby/compiler/ir/instructions/JRubyImplCallInstr.java
5fade4a7c src/org/jruby/compiler/ir/operands/MethAddr.java
+       // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
-    // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
fa309a4a1 src/org/jruby/compiler/ir/MethAddr.java
+    // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
*_*_*
// TODO: Generalize this type-checking code into IRubyObject helper.
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
a679fc6a62 Add Module.extended Type check without crash on Object.type_of? Type check Object.extend arguments
6eb8df5bd src/org/jruby/RubyBasicObject.java
6eb8df5bd src/org/jruby/RubyObject.java
+        // TODO: Generalize this type-checking code into IRubyObject helper.
-        // TODO: Generalize this type-checking code into IRubyObject helper.
a679fc6a6 src/org/jruby/RubyObject.java
+        // TODO: Generalize this type-checking code into IRubyObject helper.
*_*_*
// FIXME: Total hack to get flash in Rails marshalling/unmarshalling in session ok...We need
# shas =  1
*_*_*
// index for the item // this could probably be more efficient
# shas =  0
*_*_*
// SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
4bc398477a [IR] Code cleanup.
8cdcdb64ae Missing one
5a08b5476f Trivial IR changes littering my tree
b802028b86 - [ Hmm .. git newbie here .. somehow lost the commit .. trying again ] Fixed couple bugs with scope canonicalization; implemented ir output as a compiler pass; added code-version-token for IR_Method and added several method stubs to start optimizing calls
4bc398477 src/org/jruby/compiler/ir/IRMethod.java
-    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
8cdcdb64a src/org/jruby/compiler/ir/IR_Method.java
-    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
5a08b5476 src/org/jruby/compiler/ir/IRMethod.java
+    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
b802028b8 src/org/jruby/compiler/ir/IR_Method.java
+    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
*_*_*
// TODO: this is kinda gross
277af88f8e [Truffle] Dead code in parser helpers.
fa938c4c71 [Truffle] Fork the JRuby parser.
fe48f9887f refactor newIOErrorFromException's if - else if string equals into case (string)
78f196d805 Working through more logic... sysopen, seek, reopen, init_copy.
21e096f9ec IO#read_nonblock and readpartial.
fb75b640ab Add more smarts to "IOError from IOException" to produce appropriate errno exceptions for CONNRESET, PIPE. Probably others I'm missing. Damn you Java and your one-IOException-to-rule-them-all.
e382475a24 Various modifications, cleanups in the process of investigating JRUBY-2719, but still broken.
277af88f8 truffle/src/main/java/org/jruby/truffle/parser/Helpers.java
-        // TODO: this is kinda gross
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/Helpers.java
+        // TODO: this is kinda gross
fe48f9887 core/src/main/java/org/jruby/Ruby.java
-        // TODO: this is kinda gross
78f196d80 core/src/main/java/org/jruby/Ruby.java
78f196d80 core/src/main/java/org/jruby/runtime/Helpers.java
-        // TODO: this is kinda gross
+        // TODO: this is kinda gross
21e096f9e core/src/main/java/org/jruby/Ruby.java
+        // TODO: this is kinda gross
fb75b640a src/org/jruby/Ruby.java
fb75b640a src/org/jruby/RubyIO.java
+        // TODO: this is kinda gross
-            // TODO: this is kinda gross
e382475a2 src/org/jruby/RubyIO.java
+            // TODO: this is kinda gross
*_*_*
// TODO: need to get this back into the method signature...now is too late...
80603cf52e More work on bootstrapping JVM emitter for IR compiler.
aa0f8552ad Add RECV_ARG support and variable allocation/loading.
80603cf52 src/org/jruby/compiler/ir/targets/JVM.java
-        // TODO: need to get this back into the method signature...now is too late...
aa0f8552a src/org/jruby/compiler/ir/targets/JVM.java
+        // TODO: need to get this back into the method signature...now is too late...
*_*_*
// FIXME: This is a gross way to figure it out
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
b6fc0556ae case/when support in IR builder.
dfa3f8635f - First commit: incomplete, will not compile, and some files are just outlines
1b5b823dc7 Fix for JRUBY-3361: Compiled case/when blows up with arrays as when clauses
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-        // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
b6fc0556a src/org/jruby/compiler/ir/IR_Builder.java
-        // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
dfa3f8635 src/org/jruby/compiler/ir/IR_Builder.java
+        // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
1b5b823dc src/org/jruby/compiler/ASTCompiler.java
+        // FIXME: This is a gross way to figure it out; parser help similar to yield argument passing (expandArguments) would be better
*_*_*
// this could probably be more efficient
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
04602b8b0c 1.9-compatible argument processing for blocks.
47c53df4f6 Compiler reorg: break body compilation logic into abstract, method-specific, and closure-specific to allow specialized and common behaviors. Simplifies multiple compilation methods, and enabled fully compiling next/redo/break for methods and partially compiling them for blocks (when within loops).
14282e03f1 Compiler reorg: condense individual node compilers into a more evalstate-like visitor, for simplicity of working on the node-walking side of compilation and eventually to allow maintaining cross-node compile-time information.
cc895edd6a A fix for while loops without bodies and a few additional nodes to support that test.
ed54aab18 core/src/main/java/org/jruby/compiler/impl/AbstractVariableCompiler.java
-                    // this could probably be more efficient, bailing out on assigning args past the end?
-                    // this could probably be more efficient, bailing out on assigning args past the end?
04602b8b0 src/org/jruby/compiler/impl/AbstractVariableCompiler.java
+                    // this could probably be more efficient, bailing out on assigning args past the end?
+                    // this could probably be more efficient, bailing out on assigning args past the end?
47c53df4f src/org/jruby/compiler/NodeCompilerFactory.java
-                // this could probably be more efficient, and just avoid popping values for each loop
14282e03f src/org/jruby/compiler/NodeCompilerFactory.java
14282e03f src/org/jruby/compiler/WhileNodeCompiler.java
+                // this could probably be more efficient, and just avoid popping values for each loop
-                // this could probably be more efficient, and just avoid popping values for each loop
cc895edd6 src/org/jruby/compiler/WhileNodeCompiler.java
+                // this could probably be more efficient, and just avoid popping values for each loop
*_*_*
// TODO: is this correct ?
105217d348 RubyArray now obeys List.remove contract by removing only one element matching Object. Fix JRUBY-4661
bb20e69fca JRUBY-599: JRuby needs a COW, primitive array backed builtin Array JRUBY-604: Hash#sort raises exception
105217d34 src/org/jruby/RubyArray.java
-        return deleted.isNil() ? false : true; // TODO: is this correct ?
bb20e69fc src/org/jruby/RubyArray.java
+        return deleted.isNil() ? false : true; // TODO: is this correct ?
*_*_*
// TODO: // MRI behavior: Call "+" or "add"
# shas =  0
*_*_*
// end hack
da0b65ceb3 Fix remaining BasicObject specs and probably remove some obsolete tag files
8da55f45fc Last round of changes for JRUBY-1386, improve instance_eval. Added cases to the benchmark too.
f16e305fcf Not-so-pretty fix for JRUBY-1381: define_method with an instance_eval'ed block from the surrounding scope doesn't get the right self.
da0b65ceb src/org/jruby/RubyObject.java
-            // end hack
-            // end hack
8da55f45f src/org/jruby/RubyObject.java
+            // end hack
f16e305fc src/org/jruby/RubyObject.java
+                    // end hack
*_*_*
// This is for JRUBY-2988
a00e278089 Update to jline2.
0f9880c5a2 Fix for JRUBY-2988: jirb does not echo characters to the terminal after suspend and resume in the shell
a00e27808 src/org/jruby/ext/Readline.java
-                    // This is for JRUBY-2988, since after a suspend the terminal seems
0f9880c5a src/org/jruby/ext/Readline.java
+                // This is for JRUBY-2988, since after a suspend the terminal seems
*_*_*
// FIXME: I think this chunk is equivalent to MRI id2name (and not our public method
697e6895af Eliminate calls to Ruby.is1_8 and is1_9.
ec8033ab24 JRUBY-1473: newSymbol symbolid's make 1.to_sym, etc... give weird answers JRUBY-1066: Rubinius core/struct_spec failures
697e6895a core/src/main/java/org/jruby/RubyFixnum.java
-        // FIXME: I think this chunk is equivalent to MRI id2name (and not our public method 
ec8033ab2 src/org/jruby/RubyFixnum.java
+        // FIXME: I think this chunk is equivalent to MRI id2name (and not our public method 
*_*_*
// FIXME: Get rid of laziness and handle restricted access elsewhere
# shas =  1
*_*_*
// FIXME we should probably still be dyncalling 'write' here
36140802e3 Implement putc and clear out a bunch of unused RubyIO code.
434b818193 Fix for JRUBY-3483: Redirecting $stdout to an object blows up the stack
36140802e core/src/main/java/org/jruby/RubyIO.java
-            // FIXME we should probably still be dyncalling 'write' here
434b81819 src/org/jruby/RubyIO.java
+            // FIXME we should probably still be dyncalling 'write' here
*_*_*
FIXME: Should this be renamed to match its ruby name?
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
7ed527822b Disable id2ref and weak id association caching unless ObjectSpace is enabled. This speeds up __id__/object_id/id substantially. Also reimplemented a DRbIdConv to use a weak hash rather than id2ref.
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
7404d16eb8 Completed Kernel/Object migration to annotation binding
6eb8df5bd src/org/jruby/RubyObject.java
-     * FIXME: Should this be renamed to match its ruby name?
7ed527822 src/org/jruby/RubyBasicObject.java
-     * FIXME: Should this be renamed to match its ruby name?
bf8db0bc4 src/org/jruby/RubyBasicObject.java
+     * FIXME: Should this be renamed to match its ruby name?
      * FIXME: Should this be renamed to match its ruby name?
7404d16eb src/org/jruby/RubyObject.java
+     * FIXME: Should this be renamed to match its ruby name?
*_*_*
// TODO: protected methods. this is going to require a rework
55927e21ea Revert my refactoring in favor of version based on @kares work.
be31344448 Pull all initialize logic up and out into initializer classes.
5755383a4b Begin refactoring JavaClass binding logic.
14226fded5 Extra file committed to wrong place?
ef97a30455 Bind static interface methods (Java 8) to their proxy modules.
f9b28c47d3 Reverse comparison to avoid NPE.
da0dd06cc5 Eliminate old pure-Ruby __jcreate! for constructing the underlying object in normal cases. About 10x faster...more improvement coming.
0b9733a012 JRUBY-903: Java interface modules (by Bill Dortch) Update some samples to use newer syntax Remove miscellaneous unusied imports
55927e21e core/src/main/java/org/jruby/javasupport/JavaClass.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/Initializer.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
+        // TODO: protected methods.  this is going to require a rework
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
be3134444 core/src/main/java/org/jruby/javasupport/JavaClass.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
-            // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-            // TODO: protected methods.  this is going to require a rework
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
5755383a4 core/src/main/java/org/jruby/javasupport/JavaClass.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/Initializer.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
-        // TODO: protected methods.  this is going to require a rework
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-        // TODO: protected methods.  this is going to require a rework
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
ef97a3045 core/src/main/java/org/jruby/javasupport/JavaClass.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+        // TODO: protected methods.  this is going to require a rework
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
da0dd06cc src/org/jruby/javasupport/JavaClass.java
+        // TODO: protected methods.  this is going to require a rework 
0b9733a01 src/org/jruby/javasupport/JavaClass.java
+        // TODO: protected methods.  this is going to require a rework 
*_*_*
constructing a bignum just for dumping?
# shas =  1
*_*_*
// need to reexamine this
# shas =  1
*_*_*
// SSS FIXME: Is this correctly placed ... at the end of the loop iteration?
a78e818b9a [IR] Fix threadpoll instr generation -- place on backedges, and entry of closure and methods.
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
955209fba7 - Added IR_Loop; implemented loop stack in IR_Scope; translated while and converted break & next to jumps for non-closure cases
a78e818b9 src/org/jruby/compiler/ir/IRBuilder.java
-            // SSS FIXME: Is this correctly placed ... at the end of the loop iteration?
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-                // SSS FIXME: Is this correctly placed ... at the end of the loop iteration?
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+                // SSS FIXME: Is this correctly placed ... at the end of the loop iteration?
955209fba src/org/jruby/compiler/ir/IR_Builder.java
+                // SSS FIXME: Is this correctly placed ... at the end of the loop iteration?
*_*_*
// HACK: force clinit to be created
bf1632f6c8 Enable invokedynamic by default when running on a capable JVM.
e26ff6b589 Basic plumbing to get invokedynamic wired into dispatch. Only supports single-arg calls atm, and isn't doing any of the wrapping logic for non-local jumps, etc.
bf1632f6c src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
-        // HACK: force clinit to be created
e26ff6b58 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // HACK: force clinit to be created
*_*_*
// FIXME: this not being in a finally is a little worrisome
d9656d2b20 Move all caching logic out of "real class" and use RuntimeCache (now top-level) for method caching.
d8107a3199 Reduce size and complexity of new interface impl method stubs.
02b8e01b46 Remove MiniJava and all the tendrils it extended through JRuby. Useful remainder moved to org.jruby.java.codegen.RealClassGenerator.
07e838b135 First commit of new JI interface-impl code that actually generates a real Java class to use for the Ruby objects. Does not support concrete Java superclasses yet, which still goes through the old style logic.
98fa63591e Fixes for new flexible interface impl rules: * Predictable ordering for rubified names * Question-marked Java name was not being searched * Specs for ordering and all name variants
d9656d2b2 src/org/jruby/java/codegen/RealClassGenerator.java
-                    // FIXME: this not being in a finally is a little worrisome
d8107a319 src/org/jruby/java/codegen/RealClassGenerator.java
-                    // FIXME: this not being in a finally is a little worrisome
02b8e01b4 src/org/jruby/java/MiniJava.java
02b8e01b4 src/org/jruby/java/codegen/RealClassGenerator.java
-                    // FIXME: this not being in a finally is a little worrisome
-                    // FIXME: this not being in a finally is a little worrisome
+                    // FIXME: this not being in a finally is a little worrisome
+                    // FIXME: this not being in a finally is a little worrisome
07e838b13 src/org/jruby/java/MiniJava.java
+                    // FIXME: this not being in a finally is a little worrisome
98fa63591 src/org/jruby/java/MiniJava.java
+                // FIXME: this not being in a finally is a little worrisome
*_*_*
// SSS FIXME: What happens to the add class in this case??
15d7d44d7b - Few bug fixes + started changing coding style to conform with existing   coding style + translated SClassNode + added several additional fixmes   + added timing output.
fc6ff50620 - More cleanup; now, methods can only be added in class scopes!  Added def_class_method and def_instance_method instructions; cleaned up more toString output; now class definitions are handled properly; added return (for implicit return case) to methods.
15d7d44d7 src/org/jruby/compiler/ir/IR_Builder.java
-            // SSS FIXME: What happens to the add class in this case??
fc6ff5062 src/org/jruby/compiler/ir/IR_Builder.java
+            // SSS FIXME: What happens to the add class in this case??
*_*_*
// if we don't have opt args
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
ff9baab4ad Optz for class/sclass/method bodies, to get the usual variable optz and reduce per-entry cost due to recreating the scope.
55d5329d11 Large optimization/simplification for method argument processing: * Reduced bytecode for opt and rest args in half * Removed some unnecessary dup/pop * Simplified opt-arg flow to ease JVM framing overhead. * Reduced arity-checking to always do the static call; halved bytecode. * Reduced stack-based nil-filling to only fill uncertain local vars (those not guaranteed to be populated by arg processing)
ed54aab18 core/src/main/java/org/jruby/compiler/impl/StackBasedVariableCompiler.java
-            // if we don't have opt args, start after args (they will be assigned later)
-            // if we don't have opt args, start after args (they will be assigned later)
ff9baab4a src/org/jruby/compiler/impl/StackBasedVariableCompiler.java
+            // if we don't have opt args, start after args (they will be assigned later)
55d5329d1 src/org/jruby/compiler/impl/StackBasedVariableCompiler.java
+            // if we don't have opt args, start after args (they will be assigned later)
*_*_*
// FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
9902ebd3bf a slight Java integration (mostly bootstrap internals) cleanup
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
9902ebd3b core/src/main/java/org/jruby/javasupport/JavaArray.java
-        // FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
b390103c2 src/org/jruby/javasupport/JavaArray.java
+        // FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
*_*_*
// so we must do all this (the next 70 lines of code)
8b6eec17dd Remove TruffleRuby.
364473d04b [Truffle] Update sprintf float formatting
bcd3301fcd The Bill Dortch sprintf special commit....
8b6eec17d truffle/src/main/java/org/jruby/truffle/core/format/format/FormatFloatNode.java
-        // so we must do all this (the next 70 lines of code), which has already
364473d04 truffle/src/main/java/org/jruby/truffle/core/format/format/FormatFloatNode.java
+        // so we must do all this (the next 70 lines of code), which has already
bcd3301fc src/org/jruby/util/Sprintf.java
+                    // so we must do all this (the next 70 lines of code), which has already
*_*_*
// TODO: Ruby does not seem to care about invalid numeric mode values
aa4ffa1be5 Fix botched merge that doubled new file content.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
5205335b25 Truncate not happening for w+. Numeric modes to open() functions not working.
aa4ffa1be src/org/jruby/util/io/ModeFlags.java
-    	// TODO: Ruby does not seem to care about invalid numeric mode values
66b024fed src/org/jruby/util/IOModes.java
66b024fed src/org/jruby/util/io/ModeFlags.java
-    	// TODO: Ruby does not seem to care about invalid numeric mode values
+    	// TODO: Ruby does not seem to care about invalid numeric mode values
+    	// TODO: Ruby does not seem to care about invalid numeric mode values
5205335b2 src/org/jruby/util/IOModes.java
+    	// TODO: Ruby does not seem to care about invalid numeric mode values
*_*_*
// FIXME: the code below is a copy of RubyIO.puts
# shas =  1
*_*_*
// TODO should this be deprecated ? (to be efficient
# shas =  1
*_*_*
// FIXME: This is gross. Don't do this.
c3a18b3b18 More MAsgnNode removal
45658ca9dc Remove ill-fated erb generation of Block logic along with unused higher-arity call paths.
a0ea820f4b Generate the top layers of specific-arity block dispatch.
833606c98d Fix for JRUBY-1823, plus refactoring
47fc3bf1eb Fix for JRUBY-1468, fixes the original issue, adds a whole raft of tests, and fixes a few additional issues discovered.
c3a18b3b1 core/src/main/java/org/jruby/runtime/BlockBody.java
-                    // FIXME: This is gross. Don't do this.
45658ca9d src/org/jruby/runtime/BlockBody.erb
-                    // FIXME: This is gross. Don't do this.
a0ea820f4 src/org/jruby/runtime/BlockBody.erb
+                    // FIXME: This is gross. Don't do this.
833606c98 src/org/jruby/compiler/ASTCompiler.java
833606c98 src/org/jruby/runtime/BlockBody.java
-                    // FIXME: This is gross. Don't do this.
+                    // FIXME: This is gross. Don't do this.
47fc3bf1e src/org/jruby/compiler/ASTCompiler.java
+                    // FIXME: This is gross. Don't do this.
*_*_*
// this seems unlikely to happen unless it's a totally bogus fileno
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
64ff47702b Phase 2 of the big IO refactoring. Most IOHandler impls have been deleted. Only NIO-based handlers remain, using Channels.newChannel where only streams are available. Had to temporarily disable a test in higher_javasupport for unknown reasons.
a6f20b63a core/src/main/java/org/jruby/RubyIO.java
-            // this seems unlikely to happen unless it's a totally bogus fileno
64ff47702 src/org/jruby/RubyIO.java
+            // this seems unlikely to happen unless it's a totally bogus fileno
*_*_*
// TODO: protected methods. this is going to require a rework of some of the mechanism.
55927e21ea Revert my refactoring in favor of version based on @kares work.
be31344448 Pull all initialize logic up and out into initializer classes.
5755383a4b Begin refactoring JavaClass binding logic.
14226fded5 Extra file committed to wrong place?
ef97a30455 Bind static interface methods (Java 8) to their proxy modules.
f9b28c47d3 Reverse comparison to avoid NPE.
9bfb7b1f85 Minor edit
55927e21e core/src/main/java/org/jruby/javasupport/JavaClass.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/Initializer.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
be3134444 core/src/main/java/org/jruby/javasupport/JavaClass.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
-            // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
5755383a4 core/src/main/java/org/jruby/javasupport/JavaClass.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/Initializer.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
ef97a3045 core/src/main/java/org/jruby/javasupport/JavaClass.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
9bfb7b1f8 src/org/jruby/javasupport/JavaClass.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
*_*_*
// XXX This constructor is a hack to implement the __END__ syntax.
# shas =  1
*_*_*
// SSS FIXME: Where does this go?
43a2178dad [IR] Bug fixes with IR generated for args-cat/push + cleanup of CompoundArray operand and its use (requires renaming).
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
955209fba7 - Added IR_Loop; implemented loop stack in IR_Scope; translated while and converted break & next to jumps for non-closure cases
43a2178da src/org/jruby/compiler/ir/IRBuilder.java
-        // SSS FIXME: Where does this go?
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-        // SSS FIXME: Where does this go?
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+        // SSS FIXME: Where does this go?
955209fba src/org/jruby/compiler/ir/IR_Builder.java
+        // SSS FIXME: Where does this go?
*_*_*
// TODO: choose narrowest method by continuing to search
c4e6f18a4d Refactored the new declared_method_smart logic a bit, to get code into the right classes.  (See FIXMEs re: is 'declared' really what was intended?) Other misc. JI cleanup in advance of some changes.
b02be2c7f5 Imports working, primitives working better, constructors working, and added some default imports like :int, :string, and :object.
05c37a91a9 Add in another large bulk of the type-inferred compiler, along with some test updates and enhancements to bytecode lib and Java integration.
c4e6f18a4 src/org/jruby/javasupport/JavaConstructor.java
c4e6f18a4 src/org/jruby/javasupport/JavaMethod.java
+                    // TODO: choose narrowest method by continuing to search
-                        // TODO: choose narrowest method by continuing to search
-                        // TODO: choose narrowest method by continuing to search
+                    // TODO: choose narrowest method by continuing to search
b02be2c7f src/org/jruby/javasupport/JavaMethod.java
+                        // TODO: choose narrowest method by continuing to search
-                    // TODO: choose narrowest method by continuing to search
+                        // TODO: choose narrowest method by continuing to search
05c37a91a src/org/jruby/javasupport/JavaMethod.java
+                    // TODO: choose narrowest method by continuing to search
*_*_*
// TODO: At least ParserSupport.attrset passes argsNode as null. ImplicitNil is wrong magic for
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
dbcdcdbac4 Tom Bomb! New Interpreter landing
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/ast/AttrAssignParseNode.java
-        // TODO: At least ParserSupport.attrset passes argsNode as null.  ImplicitNil is wrong magic for
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/ast/AttrAssignNode.java
+        // TODO: At least ParserSupport.attrset passes argsNode as null.  ImplicitNil is wrong magic for
dbcdcdbac src/org/jruby/ast/AttrAssignNode.java
+        // TODO: At least ParserSupport.attrset passes argsNode as null.  ImplicitNil is wrong magic for 
*_*_*
// SSS FIXME: I could make IR_Loop a scope too ... Semantically
# shas =  0
*_*_*
// SSS FIXME: Receiver -- this is the class meta object basically?
a9819ccf07 - More cleanup of scoping and removing old crud from when I didn't understand some of the scoping semantics properly; Added a metaclass object to encapsulate construction of the metaclass and keep some code clean.  All of this will need couple more passes and some more fixup.
52d2b6c7bc - More nodes translated; some nodes fixed up; added Regexp
a9819ccf0 src/org/jruby/compiler/ir/IR_Builder.java
-            // SSS FIXME: Receiver -- this is the class meta object basically?
52d2b6c7b src/org/jruby/compiler/ir/IR_Builder.java
+            // SSS FIXME: Receiver -- this is the class meta object basically?
*_*_*
// TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
72234f9074 UnboundMethod does NOT extend Method and does NOT define to_proc.
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
72234f907 core/src/main/java/org/jruby/RubyUnboundMethod.java
-        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
b390103c2 src/org/jruby/RubyFileStat.java
b390103c2 src/org/jruby/RubyMethod.java
b390103c2 src/org/jruby/RubyProcess.java
b390103c2 src/org/jruby/RubyUnboundMethod.java
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
*_*_*
// TODO: This is actually now returning the scope of whoever called Method#to_proc
fd0fa789b2 Merge "backtrace" branch to master, now that it runs clean on all our various test cases.
cdb1c7f5d2 Fix for JRUBY-2237, provide a (currently bogus) staticscope for Method#to_proc procs.
fd0fa789b src/org/jruby/runtime/MethodBlock.java
-    // TODO: This is actually now returning the scope of whoever called Method#to_proc
cdb1c7f5d src/org/jruby/runtime/MethodBlock.java
+        // TODO: This is actually now returning the scope of whoever called Method#to_proc
*_*_*
// CON FIXME: I don't know how to make case be an expression...does that
214b11c51e Fixed IR for alias nodes (right now, relying on ruby internal calls instruction -- probably should change to their own IR instructions at some point)
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
b6fc0556ae case/when support in IR builder.
214b11c51 src/org/jruby/compiler/ir/IRBuilder.java
-        // CON FIXME: I don't know how to make case be an expression...does that
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-        // CON FIXME: I don't know how to make case be an expression...does that
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+        // CON FIXME: I don't know how to make case be an expression...does that
b6fc0556a src/org/jruby/compiler/ir/IR_Builder.java
+        // CON FIXME: I don't know how to make case be an expression...does that
*_*_*
// FIXME: Big fat hack here
55c3b8f755 Modify scope and block creation to decouple them.
20e70cd4d4 Move block descriptor building and parsing out of RuntimeCache.
d9656d2b20 Move all caching logic out of "real class" and use RuntimeCache (now top-level) for method caching.
04602b8b0c 1.9-compatible argument processing for blocks.
0b01fd39c3 Greatly reduce the amount of bytecodes emitted for closure bodies by moving most of the BlockBody construction logic to pre-defined methods. Saves all the bytecode for creating the BlockBody plus the synthetic method into which it was embedded.
55c3b8f75 src/org/jruby/javasupport/util/RuntimeHelpers.java
-            // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
20e70cd4d src/org/jruby/ast/executable/RuntimeCache.java
20e70cd4d src/org/jruby/javasupport/util/RuntimeHelpers.java
-            // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
-            // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
+            // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
d9656d2b2 src/org/jruby/ast/executable/AbstractScript.java
d9656d2b2 src/org/jruby/ast/executable/RuntimeCache.java
-                // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
-                // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
+            // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
+            // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
04602b8b0 src/org/jruby/ast/executable/AbstractScript.java
+            // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
0b01fd39c src/org/jruby/ast/executable/AbstractScript.java
+        // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
*_*_*
// There's not a compelling reason to keep JavaClass instances in a weak map
f5850a515a Add methods from Java* internal classes to java.lang equivs.
9d9271b8d9 Add support for using Java 7's ClassValue to retrieve JavaClass proxies.
1dbbe1a37a More JI cleanup:  - Reworked the very expensive and thread-unsafe (and essentially obsolete) proxy extender mechanism. No longer is every new proxy checked against all proxy extenders; since the Rubification of the Java class hierarchy, it's really only necessary to extend the class/module specified in the extend_proxy method. I'd like to see this feature deprecated, as it's redundant now (see forthcoming note on jruby-dev).  - Moved JavaClass instances to a ConcurrentHashMap for faster access.  They had been in a weak hash map, but since almost all of them were linked to proxies that were in non-weak maps, there was really no benefit (and some performance penalties).  - Used concurrent techniques to speed proxy class lookup.  Proxies are now stored with their corresponding JavaClass instances, and can be retrieved with an unsynchronized access.  - Eliminated the ProxyData "dataStruct" that had been attached to the JavaUtilities module; most of the fields (maps) had been eliminated in the work noted above; the remaining fields have been moved to JavaSupport where they may be accessed without a JavaUtilities reference.  - Permanently disabled the the old-style (<) interface implementation syntax (there had been a method to enable it; no more).  - Fixed a bug wherein the colon2 syntax for opening modules was not working if the module had not previously been referenced.  Will open a JIRA and backport to the 1.0 branch.
f5850a515 src/org/jruby/java/proxies/MapBasedProxyCache.java
f5850a515 src/org/jruby/util/collections/MapBasedClassValue.java
-    // There's not a compelling reason to keep JavaClass instances in a weak map
+    // There's not a compelling reason to keep JavaClass instances in a weak map
9d9271b8d src/org/jruby/java/proxies/MapBasedProxyCache.java
9d9271b8d src/org/jruby/javasupport/JavaSupport.java
+    // There's not a compelling reason to keep JavaClass instances in a weak map
-    // There's not a compelling reason to keep JavaClass instances in a weak map
1dbbe1a37 src/org/jruby/javasupport/JavaSupport.java
+    // There's not a compelling reason to keep JavaClass instances in a weak map
*_*_*
// TODO: This filtering is kind of gross...it would be nice to get some parser help here
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
89eb209a51 - More fixmes, translations, instructions.
dfa3f8635f - First commit: incomplete, will not compile, and some files are just outlines
4c35d54c6e Second phase of block arity-splitting: argument counts 1-3 now pass from compiler back to BlockBody unwrapped.
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-        // TODO: This filtering is kind of gross...it would be nice to get some parser help here
89eb209a5 src/org/jruby/compiler/ir/IR_Builder.java
-        // TODO: This filtering is kind of gross...it would be nice to get some parser help here
dfa3f8635 src/org/jruby/compiler/ir/IR_Builder.java
+        // TODO: This filtering is kind of gross...it would be nice to get some parser help here
4c35d54c6 src/org/jruby/compiler/ASTCompiler.java
+        // TODO: This filtering is kind of gross...it would be nice to get some parser help here
*_*_*
// TODO: set our metaclass to target's class (i.e. scary!)
78f196d805 Working through more logic... sysopen, seek, reopen, init_copy.
747826655f Fix for NullWritableChannel (used for /dev/null and friends): it wasn't advancing position while pretending to write.
78f196d80 core/src/main/java/org/jruby/RubyIO.java
-            // TODO: set our metaclass to target's class (i.e. scary!)
747826655 src/org/jruby/RubyIO.java
+                // TODO: set our metaclass to target's class (i.e. scary!)
*_*_*
// TODO: newTypeError does not offer enough for ruby error string...
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
a679fc6a62 Add Module.extended Type check without crash on Object.type_of? Type check Object.extend arguments
6eb8df5bd src/org/jruby/RubyBasicObject.java
6eb8df5bd src/org/jruby/RubyObject.java
+            // TODO: newTypeError does not offer enough for ruby error string...
-            // TODO: newTypeError does not offer enough for ruby error string...
a679fc6a6 src/org/jruby/RubyObject.java
+            // TODO: newTypeError does not offer enough for ruby error string...
*_*_*
shouldn't happen. TODO: might want to throw exception instead.
516fa6b39c introduce (native) JavaPackage replacement for JavaPackageModuleTemplate
1dbbe1a37a More JI cleanup:  - Reworked the very expensive and thread-unsafe (and essentially obsolete) proxy extender mechanism. No longer is every new proxy checked against all proxy extenders; since the Rubification of the Java class hierarchy, it's really only necessary to extend the class/module specified in the extend_proxy method. I'd like to see this feature deprecated, as it's redundant now (see forthcoming note on jruby-dev).  - Moved JavaClass instances to a ConcurrentHashMap for faster access.  They had been in a weak hash map, but since almost all of them were linked to proxies that were in non-weak maps, there was really no benefit (and some performance penalties).  - Used concurrent techniques to speed proxy class lookup.  Proxies are now stored with their corresponding JavaClass instances, and can be retrieved with an unsynchronized access.  - Eliminated the ProxyData "dataStruct" that had been attached to the JavaUtilities module; most of the fields (maps) had been eliminated in the work noted above; the remaining fields have been moved to JavaSupport where they may be accessed without a JavaUtilities reference.  - Permanently disabled the the old-style (<) interface implementation syntax (there had been a method to enable it; no more).  - Fixed a bug wherein the colon2 syntax for opening modules was not working if the module had not previously been referenced.  Will open a JIRA and backport to the 1.0 branch.
7e8f96040e Bill's changes for JRUBY-920, a longhand package-as-module syntax that uses actual package names and allows classes to be reopened.
516fa6b39 core/src/main/java/org/jruby/javasupport/Java.java
-            // again, shouldn't happen. TODO: might want to throw exception instead.
-            // again, shouldn't happen. TODO: might want to throw exception instead.
1dbbe1a37 src/org/jruby/javasupport/Java.java
             // again, shouldn't happen. TODO: might want to throw exception instead.
+            // again, shouldn't happen. TODO: might want to throw exception instead.
7e8f96040 src/org/jruby/javasupport/Java.java
+            // again, shouldn't happen. TODO: might want to throw exception instead.
*_*_*
// FIXME: Obvious issue that not all platforms can display all attributes. Ugly hacks
# shas =  1
*_*_*
// ruby constants for strings (should this be moved somewhere else?)
8b6eec17dd Remove TruffleRuby.
e37c0ad22f [Truffle] There is only one LexingCommon, so remove the abstraction.
fa938c4c71 [Truffle] Fork the JRuby parser.
fbaf038968 New lexer running plenty of stuff but missing some features like SCRIPT_LINES and with some bad line position info
3351729b98 Brutal insertion of regular lexer into ripper
38c9a31079 ruby 1.8 grammar landing
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/lexer/RubyLexer.java
-    // ruby constants for strings (should this be moved somewhere else?)
e37c0ad22 truffle/src/main/java/org/jruby/truffle/parser/lexer/LexingCommon.java
e37c0ad22 truffle/src/main/java/org/jruby/truffle/parser/lexer/RubyLexer.java
-    // ruby constants for strings (should this be moved somewhere else?)
+    // ruby constants for strings (should this be moved somewhere else?)
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/lexer/LexingCommon.java
+    // ruby constants for strings (should this be moved somewhere else?)
fbaf03896 core/src/main/java/org/jruby/ext/ripper/RipperLexer.java
fbaf03896 core/src/main/java/org/jruby/lexer/LexingCommon.java
fbaf03896 core/src/main/java/org/jruby/lexer/yacc/RubyLexer.java
-    // ruby constants for strings (should this be moved somewhere else?)
+    // ruby constants for strings (should this be moved somewhere else?)
-    // ruby constants for strings (should this be moved somewhere else?)
3351729b9 src/org/jruby/ext/ripper/RipperLexer.java
+    // ruby constants for strings (should this be moved somewhere else?)
38c9a3107 src/org/jruby/lexer/yacc/RubyYaccLexer.java
+    // ruby constants for strings (should this be moved somewhere else?)
*_*_*
// not intended to be called directly by users (private)
557c40cd6a Remove duplicate JavaInterfaceTemplate not being used.
ec7406706c Move JavaInterfaceTemplate to java.proxies package, add a closure conversion bench, add InterfaceJavaProxy native impl beginnings, but it's not enabled yet.
18d03923f1 Likely fix for JRUBY-2680 and potential fix for JRUBY-2803, by porting the entirety of JavaInterfaceTemplate from Ruby to Java. The bulk of this module was the append_features logic, which was gigantic, involved multiple instance_evals, many method definitions, and a lot of back-and-forth to the Java side of things. Writing up some benchmarks momentarily.
557c40cd6 src/org/jruby/javasupport/JavaInterfaceTemplate.java
-    // not intended to be called directly by users (private)
ec7406706 src/org/jruby/java/proxies/JavaInterfaceTemplate.java
+    // not intended to be called directly by users (private)
18d03923f src/org/jruby/javasupport/JavaInterfaceTemplate.java
+    // not intended to be called directly by users (private)
*_*_*
// TODO: Figure out how this can happen and possibly remove
7621334c9b Start removing old interpreter
dbcdcdbac4 Tom Bomb! New Interpreter landing
7621334c9 core/src/main/java/org/jruby/ast/ClassNode.java
-        // TODO: Figure out how this can happen and possibly remove
dbcdcdbac src/org/jruby/ast/ClassNode.java
+        // TODO: Figure out how this can happen and possibly remove
*_*_*
// see note below re: 2-field kludge
55927e21ea Revert my refactoring in favor of version based on @kares work.
5d4a39d14b Remove JavaClass from participation in proxy binding.
be31344448 Pull all initialize logic up and out into initializer classes.
5755383a4b Begin refactoring JavaClass binding logic.
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
1dbbe1a37a More JI cleanup:  - Reworked the very expensive and thread-unsafe (and essentially obsolete) proxy extender mechanism. No longer is every new proxy checked against all proxy extenders; since the Rubification of the Java class hierarchy, it's really only necessary to extend the class/module specified in the extend_proxy method. I'd like to see this feature deprecated, as it's redundant now (see forthcoming note on jruby-dev).  - Moved JavaClass instances to a ConcurrentHashMap for faster access.  They had been in a weak hash map, but since almost all of them were linked to proxies that were in non-weak maps, there was really no benefit (and some performance penalties).  - Used concurrent techniques to speed proxy class lookup.  Proxies are now stored with their corresponding JavaClass instances, and can be retrieved with an unsynchronized access.  - Eliminated the ProxyData "dataStruct" that had been attached to the JavaUtilities module; most of the fields (maps) had been eliminated in the work noted above; the remaining fields have been moved to JavaSupport where they may be accessed without a JavaUtilities reference.  - Permanently disabled the the old-style (<) interface implementation syntax (there had been a method to enable it; no more).  - Fixed a bug wherein the colon2 syntax for opening modules was not working if the module had not previously been referenced.  Will open a JIRA and backport to the 1.0 branch.
55927e21e core/src/main/java/org/jruby/javasupport/JavaClass.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
+            // see note below re: 2-field kludge
-            // see note below re: 2-field kludge
5d4a39d14 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
-            // see note below re: 2-field kludge
be3134444 core/src/main/java/org/jruby/javasupport/JavaClass.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
-                // see note below re: 2-field kludge
+            // see note below re: 2-field kludge
5755383a4 core/src/main/java/org/jruby/javasupport/JavaClass.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
-            // see note below re: 2-field kludge
+            // see note below re: 2-field kludge
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-            // see note below re: 2-field kludge
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+            // see note below re: 2-field kludge
1dbbe1a37 src/org/jruby/javasupport/JavaClass.java
+            // see note below re: 2-field kludge
*_*_*
// The implementations of these are all bonus (see TODO above) I was going
# shas =  1
*_*_*
// it would be nice (and logical!) if exponent form
8b6eec17dd Remove TruffleRuby.
364473d04b [Truffle] Update sprintf float formatting
bcd3301fcd The Bill Dortch sprintf special commit....
8b6eec17d truffle/src/main/java/org/jruby/truffle/core/format/format/FormatFloatNode.java
-                // it would be nice (and logical!) if exponent form
364473d04 truffle/src/main/java/org/jruby/truffle/core/format/format/FormatFloatNode.java
+                // it would be nice (and logical!) if exponent form
bcd3301fc src/org/jruby/util/Sprintf.java
+                        // it would be nice (and logical!) if exponent form 
*_*_*
// bit risky if someone changes completor
b2a2f49f1e remove readline and truffle from ext/
d38821551a Fix for JRUBY-336 and JRUBY-337: add support for applets by catching (and ignoring) access violation exceptions, and adds applet and standalone consoles
b2a2f49f1 ext/readline/src/main/java/org/jruby/demo/readline/TextAreaReadline.java
-        // bit risky if someone changes completor, but useful for method calls
d38821551 src/org/jruby/demo/TextAreaReadline.java
+        // bit risky if someone changes completor, but useful for method calls
*_*_*
// SSS FIXME: Correct? Where does closure arg come from?
2ceae0f241 Fix incorrect IR for yield instructions
b699909a0d Forgot to rm YIELD_Instr
4733ec035d Some more block specific IR stuff (mostly renaming)
89eb209a51 - More fixmes, translations, instructions.
2ceae0f24 src/org/jruby/compiler/ir/instructions/YieldInstr.java
-    // SSS FIXME: Correct?  Where does closure arg come from?
b699909a0 src/org/jruby/compiler/ir/instructions/YIELD_Instr.java
-    // SSS FIXME: Correct?  Where does closure arg come from?
4733ec035 src/org/jruby/compiler/ir/instructions/YieldInstr.java
+    // SSS FIXME: Correct?  Where does closure arg come from?
89eb209a5 src/org/jruby/compiler/ir/YIELD_Instr.java
+    // SSS FIXME: Correct?  Where does closure arg come from?
*_*_*
// TODO: Only setAccessible to account for pattern found by
1337fbecc3 align JavaField's value/static_value conversions and return values + test functionality
beaaeb89b6 JRUBY-814: Multiple improvements to Java integration (was: Java method get lost.) [Bill Dortch]
e40beaae6e Temporary hack for non-public interface classes to be accessible (see TODO comment next to hack for more info)
1337fbecc core/src/main/java/org/jruby/javasupport/JavaField.java
-            // TODO: Only setAccessible to account for pattern found by
-            // TODO: Only setAccessible to account for pattern found by
beaaeb89b src/org/jruby/javasupport/JavaField.java
+            // TODO: Only setAccessible to account for pattern found by
e40beaae6 src/org/jruby/javasupport/JavaField.java
+	    // TODO: Only setAccessible to account for pattern found by
*_*_*
// FIXME: null check is removable once we figure out how to assign to unset named block args
7621334c9b Start removing old interpreter
dbcdcdbac4 Tom Bomb! New Interpreter landing
174e3d0146 damn the torpedos! full speed ahead! landing enebo_lexical branch changes plus TC-passing optimizations and a few small compiler updates
7621334c9 core/src/main/java/org/jruby/ast/DVarNode.java
-        // FIXME: null check is removable once we figure out how to assign to unset named block args
dbcdcdbac src/org/jruby/ast/DVarNode.java
dbcdcdbac src/org/jruby/evaluator/ASTInterpreter.java
+        // FIXME: null check is removable once we figure out how to assign to unset named block args
-        // FIXME: null check is removable once we figure out how to assign to unset named block args
174e3d014 src/org/jruby/evaluator/EvaluationState.java
+                // FIXME: null check is removable once we figure out how to assign to unset named block args
*_*_*
// FIXME: not very efficient
367c2bcd7d - DynamicScope+children removed no longer used {get,set}ArgValues methods since they are dead. - ArgsNode getRest() replaced by hasRest() or retrieving getRestArgNode directly. - Slight rearrangement internally of fields in ArgsNode. - Remove accidental println from last commit.
33b1f99f1c Use a shared binding dynamic scope to take advantage of frame ir instructions (should actually be called dynamic scope instrs or some such thing)
34c5b29cd1 Add four-var scope and enable it globally.
aafd3613a2 Add three-var scope, not enabled yet.
d237a30fee Added no-var and two-var versions of DynamicScope.
ec4f90b7e0 Based on discoveries by Marcin, confirmed by Ola, adding capability for separate DynamicScope implementations that use fields instead of an array for scopes with small numbers of variables. This primarily improves compiled performance, since the compiler can statically dispatch to methods that amount to little more than field access. I will add additional DynamicScope impls for 0, 2, and 3 variable scopes.
d1589c3ece Total refactoring of zsuper argument processing, and zsuper is now enabled in the compiler. We still need more/better tests and specs for zsuper, unfortunately.
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/FourVarDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/ManyVarsDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/OneVarDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/SharedBindingDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/ThreeVarDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/TwoVarDynamicScope.java
-            // FIXME: not very efficient
-            // FIXME: not very efficient
-            // FIXME: not very efficient
-            // FIXME: not very efficient
-            // FIXME: not very efficient
-            // FIXME: not very efficient
33b1f99f1 src/org/jruby/runtime/scope/SharedBindingDynamicScope.java
+            // FIXME: not very efficient
34c5b29cd src/org/jruby/runtime/scope/FourVarDynamicScope.java
+            // FIXME: not very efficient
aafd3613a src/org/jruby/runtime/scope/ThreeVarDynamicScope.java
+            // FIXME: not very efficient
d237a30fe src/org/jruby/runtime/ManyVarsDynamicScope.java
d237a30fe src/org/jruby/runtime/OneVarDynamicScope.java
d237a30fe src/org/jruby/runtime/scope/ManyVarsDynamicScope.java
d237a30fe src/org/jruby/runtime/scope/OneVarDynamicScope.java
d237a30fe src/org/jruby/runtime/scope/TwoVarDynamicScope.java
+            // FIXME: not very efficient
ec4f90b7e src/org/jruby/runtime/DynamicScope.java
ec4f90b7e src/org/jruby/runtime/ManyVarsDynamicScope.java
ec4f90b7e src/org/jruby/runtime/OneVarDynamicScope.java
-            // FIXME: not very efficient
+            // FIXME: not very efficient
+            // FIXME: not very efficient
d1589c3ec src/org/jruby/runtime/DynamicScope.java
+            // FIXME: not very efficient
*_*_*
// using IOChannel may not be the most performant way, but it's easy.
# shas =  1
*_*_*
// TODO: check for Java reserved names and raise exception if encountered
6b96e53d75 review getProxyOrPackageUnderPackage and (slightly) unify with top-level's method
e705f6ca4b JI: Moved more JI code into Java to improve performance (and partly to offset the cost of supporting lower-case class names).  Would still like to get instantiation code (__jcreate!) logic moved into Java before 1.1, but won't have time before RC2.
6b96e53d7 core/src/main/java/org/jruby/javasupport/Java.java
-            // TODO: check for Java reserved names and raise exception if encountered
e705f6ca4 src/org/jruby/javasupport/Java.java
+            // TODO: check for Java reserved names and raise exception if encountered
*_*_*
// FIXME: this probably belongs in a different package.
# shas =  1
*_*_*
// from DUP_SETUP // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
# shas =  0
*_*_*
// TODO: cache?
0b2c4be442 Cache booleans with indy, so they can fold away.
148883daa1 Split compiler-related inner classes into top-level classes, in preparation for more refactoring.
fc09ed420d Multiple additional cleanups, fixes, to the compiler; expand stack-based methods to include those with opt/rest/block args, fix a problem with redo/next in ensure/rescue; fix an issue in the ASTInspector not inspecting opt arg values; shrink the generated bytecode by offloading to CompilerHelpers in a few places. Ruby stdlib now compiles completely. Yay!
0b2c4be44 src/org/jruby/compiler/impl/BaseBodyCompiler.java
-        // TODO: cache?
-        // TODO: cache?
148883daa src/org/jruby/compiler/impl/AbstractMethodCompiler.java
148883daa src/org/jruby/compiler/impl/StandardASMCompiler.java
+        // TODO: cache?
+        // TODO: cache?
-            // TODO: cache?
-            // TODO: cache?
fc09ed420 src/org/jruby/compiler/impl/StandardASMCompiler.java
+            // TODO: cache?
+            // TODO: cache?
*_*_*
// TODO: list.subList(from, to).clear() is supposed to clear the sublist from the list
# shas =  1
*_*_*
// TODO: public only?
48d04419e9 Defer *all* JavaClass initialization until a proxy class has been created. Improves large "import" times and memory use a bit.
beaaeb89b6 JRUBY-814: Multiple improvements to Java integration (was: Java method get lost.) [Bill Dortch]
48d04419e src/org/jruby/javasupport/JavaClass.java
-            // TODO: public only?
beaaeb89b src/org/jruby/javasupport/JavaClass.java
+            // TODO: public only?
*_*_*
// TODO: wire into new exception handling mechanism
905afa0b20 Make setIRScope and setScopeType simpler.  This change makes me wonder how safe we are since not all constructed IRScope paths setIRScope.  In the case of evals it appears we only set IRScopeType.
7621334c9b Start removing old interpreter
efd8a3df54 More decoupling of constant lookup from ThreadContext.
27438e0e61 [interp] Unboxed attrassign node (2-3x speedup) assign is now based on same mechanism as interpret FCallOneArgNode has slower ASTInterpreter.getBlock instead of this.getBlock converted consumers of ASTInterpreter.eval to Node.interpret converted all consumers of ASTAssignment.assign to Node.assign
3fa144ebbc Added three more nodes to the compiler, added a couple extra STI methods the shootout uses, fixed a bug with dstr node compilation, enabled const nodes as "safe" since they appear to be working right in the contexts I've tried.
972877610a JRUBY-202: ::A = 1 should work
6e15491217 merging new interpreter plus a few minor fixes to trunk. JRUBY-185
f5643d27aa merging cnutter_work2 to HEAD...damn the torpedos! full speed ahead!
905afa0b2 core/src/main/java/org/jruby/parser/StaticScope.java
-        // TODO: wire into new exception handling mechanism
7621334c9 core/src/main/java/org/jruby/ast/ConstDeclNode.java
-                // TODO: wire into new exception handling mechanism
efd8a3df5 src/org/jruby/parser/StaticScope.java
efd8a3df5 src/org/jruby/runtime/ThreadContext.java
                 // TODO: wire into new exception handling mechanism
+        // TODO: wire into new exception handling mechanism
-        // TODO: wire into new exception handling mechanism
27438e0e6 src/org/jruby/ast/ConstDeclNode.java
27438e0e6 src/org/jruby/evaluator/AssignmentVisitor.java
+                // TODO: wire into new exception handling mechanism
-                // TODO: wire into new exception handling mechanism
3fa144ebb src/org/jruby/evaluator/EvaluationState.java
3fa144ebb src/org/jruby/runtime/ThreadContext.java
-                // TODO: wire into new exception handling mechanism
+            // TODO: wire into new exception handling mechanism
972877610 src/org/jruby/evaluator/AssignmentVisitor.java
+                    // TODO: wire into new exception handling mechanism
                         // TODO: wire into new exception handling mechanism
6e1549121 src/org/jruby/evaluator/EvaluateVisitor.java
6e1549121 src/org/jruby/evaluator/EvaluationState.java
-                    // TODO: wire into new exception handling mechanism
+                        // TODO: wire into new exception handling mechanism
f5643d27a src/org/jruby/evaluator/EvaluateVisitor.java
+            	// TODO: wire into new exception handling mechanism
*_*_*
//TODO: ary_sort_check should be done here
793815e9e0 Implement two-object specialized arrays, plus misc improvements.
eba2c9089d Array#sort should never do ThreadContext lookup.
9496f57e1b Splitting up some methods to shrink them for e.g. hotspot inlining purposes.
bb20e69fca JRUBY-599: JRuby needs a COW, primitive array backed builtin Array JRUBY-604: Hash#sort raises exception
793815e9e core/src/main/java/org/jruby/specialized/RubyArrayTwoObject.java
         //TODO: ary_sort_check should be done here
+        //TODO: ary_sort_check should be done here
eba2c9089 src/org/jruby/RubyArray.java
+        //TODO: ary_sort_check should be done here
+                //TODO: ary_sort_check should be done here
-            //TODO: ary_sort_check should be done here
-            //TODO: ary_sort_check should be done here
-            //TODO: ary_sort_check should be done here
9496f57e1 src/org/jruby/RubyArray.java
+            //TODO: ary_sort_check should be done here
             //TODO: ary_sort_check should be done here
bb20e69fc src/org/jruby/RubyArray.java
+            //TODO: ary_sort_check should be done here
+            //TODO: ary_sort_check should be done here
*_*_*
// we might need to perform a DST correction
480beb745c Fix handling of Time offsets at DST boundaries.
082f232db7 Finally commit Time.local fixes for Rails' many timezone-related failures. Patch by Stephen Lewis for JRUBY-3254: Make JRuby's Time#local behave more like MRI
480beb745 core/src/main/java/org/jruby/RubyTime.java
-            // we might need to perform a DST correction
082f232db src/org/jruby/RubyTime.java
+	    // we might need to perform a DST correction
*_*_*
// TODO: rounding mode should not be hard-coded. See #mode.
1a34a5e4a2 Wow...I went too far but I am not turning back.  Refactoring of Bigdecimal + De-versioning
0c3b435b77 Fixes for BigDecimal#mult to pass more rubyspecs.
1a34a5e4a core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-            // TODO: rounding mode should not be hard-coded. See #mode.
0c3b435b7 src/org/jruby/RubyBigDecimal.java
+            // TODO: rounding mode should not be hard-coded. See #mode.
*_*_*
but Java API's aren't ISO 8601 compliant at all
8b6eec17dd Remove TruffleRuby.
e2b70266a0 [Truffle] Fork date and time formatters.
55e0d93f6d Restore RubyDateFormat and TimeOutputFormatter. Closes #1081
67f18433ab Use Joda for RubyTime, instead of stupid Java Calendar
8b6eec17d truffle/src/main/java/org/jruby/truffle/core/time/RubyDateFormatter.java
-                    // This is GROSS, but Java API's aren't ISO 8601 compliant at all
-                    // This is GROSS, but Java API's aren't ISO 8601 compliant at all
e2b70266a truffle/src/main/java/org/jruby/truffle/core/time/RubyDateFormatter.java
+                    // This is GROSS, but Java API's aren't ISO 8601 compliant at all
+                    // This is GROSS, but Java API's aren't ISO 8601 compliant at all
55e0d93f6 core/src/main/java/org/jruby/util/RubyDateFormatter.java
                     // This is GROSS, but Java API's aren't ISO 8601 compliant at all
+                    // This is GROSS, but Java API's aren't ISO 8601 compliant at all
+                    // This is GROSS, but Java API's aren't ISO 8601 compliant at all
67f18433a src/org/jruby/util/RubyDateFormat.java
+                    // This is GROSS, but Java API's aren't ISO 8601 compliant at all
+                    // This is GROSS, but Java API's aren't ISO 8601 compliant at all
*_*_*
Fixme: This does not have exact same semantics as RubyArray.join
bac69eda71 Remove remaining metaclass definitions...
7574ed61fd Process::Status File.join fixes
bac69eda7 src/org/jruby/RubyFile.java
bac69eda7 src/org/jruby/runtime/builtin/meta/FileMetaClass.java
+     * Fixme:  This does not have exact same semantics as RubyArray.join, but they
-     * Fixme:  This does not have exact same semantics as RubyArray.join, but they
7574ed61f src/org/jruby/runtime/builtin/meta/FileMetaClass.java
+     * Fixme:  This does not have exact same semantics as RubyArray.join, but they
*_*_*
// arguments compilers always create IRubyObject[]
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
4e87bc6e15 Remove ASTCompiler19 and collapse logic into ASTCompiler.
b0de876f10 Reinstate 1.9 logic in compiler for splatting nil.
d11f26175e Delegate all equivalent bits from ASTCompiler19 to ASTCompiler.
1d6c408a47 First round of twiddling to get 1.9 compilation work under way.
0f4a04f02c Compile ArgsCat, for foo(1, *[2, 3]) and equivalent
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
4e87bc6e1 core/src/main/java/org/jruby/compiler/ASTCompiler19.java
b0de876f1 src/org/jruby/compiler/ASTCompiler19.java
d11f26175 src/org/jruby/compiler/ASTCompiler19.java
1d6c408a4 src/org/jruby/compiler/ASTCompiler19.java
0f4a04f02 src/org/jruby/compiler/NodeCompilerFactory.java
*_*_*
// FIXME: set up a CallConfiguration for this
# shas =  1
*_*_*
// TODO: remove
5f14842c00 [Truffle] Handle argumentsAsArray as a rest argument in @CoreMethod.
9fd9078e6d [Truffle] Fix the execute foreign node.
9f228b9ac5 [Truffle] Add support for cross language interop and interface to C extensions.
7ef5314475 Implement Backtick and SValue.
f767b9cc6f Use an actual Ruby instance variable in Pathname
dec3a74d08 Use an actual Ruby instance variable in Pathname
633f9350a0 Implement Numeric#step.size
10d22da945 add Pathname as a hybrid native/ruby library (following MRI)
f021dd7ef6 Split zlib into multiple source files
908cd08186 Squashed commit of the following:
3fc5ce1617 Ugh. 1.9 Range#step.
5f14842c0 truffle/src/main/java/org/jruby/truffle/nodes/core/TruffleInteropNodes.java
-    // TODO: remove maxArgs - hits an assertion if maxArgs is removed - trying argumentsAsArray = true (CS)
9fd9078e6 truffle/src/main/java/org/jruby/truffle/nodes/core/TruffleInteropNodes.java
-    // TODO: remove maxArgs - hits an assertion if maxArgs is removed - trying argumentsAsArray = true (CS)
9f228b9ac truffle/src/main/java/org/jruby/truffle/nodes/core/TruffleInteropNodes.java
+    // TODO: remove maxArgs - hits an assertion if maxArgs is removed - trying argumentsAsArray = true (CS)
7ef531447 core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        jvmMethod().loadSelf(); // TODO: remove caller
f767b9cc6 core/src/main/java/org/jruby/ext/pathname/RubyPathname.java
-        // TODO: remove (either direct bridge to field or all native)
dec3a74d0 core/src/main/java/org/jruby/ext/pathname/RubyPathname.java
-        // TODO: remove (either direct bridge to field or all native)
633f9350a core/src/main/java/org/jruby/RubyNumeric.java
-        // TODO: remove
10d22da94 src/org/jruby/ext/pathname/RubyPathname.java
+        // TODO: remove (either direct bridge to field or all native)
f021dd7ef src/org/jruby/ext/zlib/RubyZlib.java
f021dd7ef src/org/jruby/ext/zlib/ZStream.java
-        // TODO: remove when JZlib checks the given level
-        // TODO: remove when JZlib checks the given windowBits
-        // TODO: remove when JZlib checks the given strategy
+    // TODO: remove when JZlib checks the given level
+    // TODO: remove when JZlib checks the given windowBits
+    // TODO: remove when JZlib checks the given strategy
908cd0818 src/org/jruby/ext/zlib/RubyZlib.java
+        // TODO: remove when JZlib checks the given level
+        // TODO: remove when JZlib checks the given windowBits
+        // TODO: remove when JZlib checks the given strategy
3fc5ce161 src/org/jruby/RubyNumeric.java
+        // TODO: remove
*_*_*
// FIXME: legal here? may want UnsupportedOperationException
02efc4da30 gently extend RubyModule's API to support setting hidden constants for consumers
6f159fe5dd Deprecation of fast constant access paths.
0be23d6124 JRUBY-3052: Cache constants at their call site (const_node)
95c346df53 Vars have landed. Does not include concurrent SymbolTable (will follow in the next day or so). Also need multi-threaded unit tests, work in progress.
02efc4da3 core/src/main/java/org/jruby/IncludedModuleWrapper.java
+        // FIXME: legal here? may want UnsupportedOperationException
6f159fe5d src/org/jruby/IncludedModuleWrapper.java
         // FIXME: legal here? may want UnsupportedOperationException
-        // FIXME: legal here? may want UnsupportedOperationException
0be23d612 src/org/jruby/IncludedModuleWrapper.java
-        // FIXME: legal here? may want UnsupportedOperationException
95c346df5 src/org/jruby/IncludedModuleWrapper.java
+        // FIXME: legal here? may want UnsupportedOperationException
+        // FIXME: legal here? may want UnsupportedOperationException
+        // FIXME: legal here? may want UnsupportedOperationException
*_*_*
// TODO: specifying soft refs
a8522d937e Move rather than rewrite JavaSupport so we can merge from 1.7.
b4b70cd7ca Abstract JavaSupport into an interface so it can be mocked.
7d9d9f3c9a Fix for JRUBY-199 (and related JRUBY-1513 and JRUBY-1735). Caches proxies in Weak/Concurrent Map-like cache, which replaces the ineffectual JavaObject cache.  TODO: unit tests, measure memory/performance impact.
a8522d937 core/src/main/java/org/jruby/javasupport/JavaSupport.java
a8522d937 core/src/main/java/org/jruby/javasupport/JavaSupportImpl.java
-        // TODO: specifying soft refs, may want to compare memory consumption,
+        // TODO: specifying soft refs, may want to compare memory consumption,
b4b70cd7c core/src/main/java/org/jruby/javasupport/JavaSupport.java
b4b70cd7c core/src/main/java/org/jruby/javasupport/JavaSupportImpl.java
-        // TODO: specifying soft refs, may want to compare memory consumption,
+        // TODO: specifying soft refs, may want to compare memory consumption,
7d9d9f3c9 src/org/jruby/javasupport/JavaSupport.java
+        // TODO: specifying soft refs, may want to compare memory consumption,
*_*_*
// FIXME: when the only autoconversions are primitives
# shas =  1
*_*_*
// TODO: reads/writes from frame
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
026f246748 Beginnings of annotations on compiled methods, to move toward unifying method binding logic and eliminating extra method generated for [] versions.
ed54aab18 core/src/main/java/org/jruby/compiler/impl/MethodBodyCompiler.java
-        // TODO: reads/writes from frame
026f24674 src/org/jruby/compiler/impl/MethodBodyCompiler.java
+        // TODO: reads/writes from frame
*_*_*
// ignore...bean doesn't get registered
28bf75a927 Minor tweaks to get JRuby running unmodified on Android:
ae39a9e4dd Add a bit more exception handling for bean registration, to quietly fail to register for security exceptions. Should help fix JRUBY-3055: permission javax.management.MBeanServerPermission "createMBeanServer";
762ade82c1 Eliminate dependency on ruby (MRI) for generating updated SCM revision info in -v output.
28bf75a92 src/org/jruby/management/BeanManager.java
28bf75a92 src/org/jruby/management/BeanManagerImpl.java
-            // ignore...bean doesn't get registered
-            // ignore...bean doesn't get registered
-            // ignore...bean doesn't get registered
-            // ignore...bean doesn't get registered
+            // ignore...bean doesn't get registered
+            // ignore...bean doesn't get registered
+            // ignore...bean doesn't get registered
+            // ignore...bean doesn't get registered
ae39a9e4d src/org/jruby/management/BeanManager.java
             // ignore...bean doesn't get registered
+            // ignore...bean doesn't get registered
+            // ignore...bean doesn't get registered
+            // ignore...bean doesn't get registered
762ade82c src/org/jruby/management/BeanManager.java
+            // ignore...bean doesn't get registered
*_*_*
// we're depending on the side effect of the load, which loads the class but does not turn it into a script
066aa2fefc Switch .rb resource loads back to loadresourceIS.  Split jar/class/normal loads into their own classes.  Do not double load .jar resources
613673ae6e Introduce LibrarySearcher and make LoadService use it
3809576cfe Enhancement for JRUBY-3248: jruby can't load file produced with jrubyc
066aa2fef core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
-                // we're depending on the side effect of the load, which loads the class but does not turn it into a script
613673ae6 core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
+        // we're depending on the side effect of the load, which loads the class but does not turn it into a script
3809576cf src/org/jruby/runtime/load/JavaCompiledScript.java
+                // we're depending on the side effect of the load, which loads the class but does not turn it into a script
*_*_*
// TODO: Split this into two sub-classes so that name and constNode can be specified seperately.
1bbd1a4207 Copy-editing: misspellings
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
dbcdcdbac4 Tom Bomb! New Interpreter landing
1bbd1a420 core/src/main/java/org/jruby/ast/ConstDeclNode.java
-    // TODO: Split this into two sub-classes so that name and constNode can be specified seperately.
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/ast/ConstDeclParseNode.java
-    // TODO: Split this into two sub-classes so that name and constNode can be specified seperately.
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/ast/ConstDeclNode.java
+    // TODO: Split this into two sub-classes so that name and constNode can be specified seperately.
dbcdcdbac src/org/jruby/ast/ConstDeclNode.java
+    // TODO: Split this into two sub-classes so that name and constNode can be specified seperately.
*_*_*
// This is perhaps innefficient timewise? Optimal spacewise
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
f7831f79f1 [1.9] Make sure block parms do not capture from enclosing scope but make new local
174e3d0146 damn the torpedos! full speed ahead! landing enebo_lexical branch changes plus TC-passing optimizations and a few small compiler updates
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/scope/StaticScope.java
-        // This is perhaps innefficient timewise?  Optimal spacewise
-        // This is perhaps innefficient timewise?  Optimal spacewise
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/scope/StaticScope.java
+        // This is perhaps innefficient timewise?  Optimal spacewise
+        // This is perhaps innefficient timewise?  Optimal spacewise
f7831f79f src/org/jruby/parser/StaticScope.java
+        // This is perhaps innefficient timewise?  Optimal spacewise
174e3d014 src/org/jruby/parser/StaticScope.java
+        // This is perhaps innefficient timewise?  Optimal spacewise
*_*_*
we should probably raise an error
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
a6f20b63a core/src/main/java/org/jruby/RubyIO.java
-            // IN FACT, we should probably raise an error, yes?
66b024fed src/org/jruby/RubyIO.java
+            // IN FACT, we should probably raise an error, yes?
*_*_*
// FIXME: This is temporary since the variable compilers assume we want
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
4e87bc6e15 Remove ASTCompiler19 and collapse logic into ASTCompiler.
09274cdb1f Add logic for jitting blocks. Not wired up yet.
8acf6f54a9 More fixes to get masgn, chained methods, and rest args working correctly in compiled 1.9 methods and blocks.
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
ed54aab18 core/src/main/java/org/jruby/compiler/JITCompiler.java
-                // FIXME: This is temporary since the variable compilers assume we want
-                // FIXME: This is temporary since the variable compilers assume we want
4e87bc6e1 core/src/main/java/org/jruby/compiler/ASTCompiler.java
4e87bc6e1 core/src/main/java/org/jruby/compiler/ASTCompiler19.java
+                // FIXME: This is temporary since the variable compilers assume we want
-                // FIXME: This is temporary since the variable compilers assume we want
09274cdb1 src/org/jruby/compiler/JITCompiler.java
+                // FIXME: This is temporary since the variable compilers assume we want
8acf6f54a src/org/jruby/compiler/ASTCompiler19.java
+                // FIXME: This is temporary since the variable compilers assume we want
*_*_*
// FIXME: Added this because marshal_spec seemed to reconstitute objects without calling dataWrapStruct
5daddb5dcd use internal asJavaObject helper + marhaling hack for Java not needed
fe8302dcd3 Multiple cleanups, refactorings, improvements for standard Java invocation:
5daddb5dc core/src/main/java/org/jruby/java/proxies/JavaProxy.java
-        // FIXME: Added this because marshal_spec seemed to reconstitute objects without calling dataWrapStruct
fe8302dcd src/org/jruby/java/proxies/JavaProxy.java
+        // FIXME: Added this because marshal_spec seemed to reconstitute objects without calling dataWrapStruct
*_*_*
TODO: This version is better than the hackish previous one. Windows
# shas =  1
*_*_*
// FIXME: potentially could just use ByteList here?
# shas =  1
*_*_*
// TODO: This version differs from ioctl by checking whether fcntl exists
8e517f833a JRUBY-3686: io.fcntl can also accept one-arg version
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
8e517f833 src/org/jruby/RubyIO.java
+        // TODO: This version differs from ioctl by checking whether fcntl exists
         // TODO: This version differs from ioctl by checking whether fcntl exists
66b024fed src/org/jruby/RubyIO.java
+        // TODO: This version differs from ioctl by checking whether fcntl exists
*_*_*
// TODO: top-level upper-case package was supported in the previous (Ruby-based)
# shas =  1
*_*_*
// ENEBO: Totally weird naming (in MRI is not allocated and is a local var name)
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
47d0614e3a Yeah static outlined methods and some getPosition simplification
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/ParserSupport.java
-    // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name) [1.9]
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/ParserSupport.java
+    // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name) [1.9]
47d0614e3 src/org/jruby/parser/ParserSupport.java
47d0614e3 src/org/jruby/parser/ParserSupport19.java
+    // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name) [1.9]
-    // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name)
bf8db0bc4 src/org/jruby/parser/ParserSupport19.java
+    // ENEBO: Totally weird naming (in MRI is not allocated and is a local var name)
*_*_*
// SSS FIXME: Verify that this is correct
6740d62fb2 [IR] [1.9] For-loops messed up my nice clean code for arg setup for Ruby 1.9 mode.  Had to add conditionals and fall back on 1.8 mode code to handle for-loop arguments.
668e937e77 [IR] [1.9] Fixed up interpretation for 1.9 receive arg instructions and fixed some bugs in generated IR.
6c44e444f5 - Resolved several fixmes around rescue-ensure block IR generation;   Nested ensure blocks in the presence of explicit returns are handled   properly;  Next step: fixup cfg generation to deal with rescue and   ensure blocks.
5d57a44aba - Added copy instruction; built the LoclAsgnNode + minor fixes in fcall and call and buildArgs methods
6740d62fb src/org/jruby/compiler/ir/IRBuilder19.java
-            // SSS FIXME: Verify that this is correct
668e937e7 src/org/jruby/compiler/ir/IRBuilder19.java
+            // SSS FIXME: Verify that this is correct
6c44e444f src/org/jruby/compiler/ir/IR_Builder.java
-            // SSS FIXME: Verify that this is correct
5d57a44ab src/org/jruby/compiler/ir/IR_Builder.java
+            // SSS FIXME: Verify that this is correct
*_*_*
// XXX: This check previously used callMethod("==") to check for equality between classes
# shas =  1
*_*_*
// TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
0fb8adf327 Revert "Revert to re-green master"
7eac59dedc Revert "HEADIUS"
96034ed2a3 HEADIUS
13a08fa681 Revert to re-green master
25fdaa3bb8 Plug in specialized float RHS 1-arg calls, as in 1.7.
dcd9597d78 Plug in specialized fixnum RHS 1-arg calls, as in 1.7.
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
346b678e2e Allow varargs target methods to be called with [] call sites.
c689af6b26 Add direct boolean paths for Fixnum RHS boolean operations.
ea1cb9b078 Add float RHS operators to indy fast path logic.
53365d3f0c Fix JRUBY-5871: java.lang.NegativeArraySizeException from RubyEnumerator (after JITed)
06c26424d5 Improvements for attribute assignment.
1d1c6bad60 Initial unguarded Fixnum-RHS operator support through invokedynamic.
31a60fe36a Fix a number of arg mismatches and RHS return value for attr assignment with invokedynamic.
a5503a49df Move back to an atomic generation ID and install some simple type/modification guards for dynopt calls.
e26ff6b589 Basic plumbing to get invokedynamic wired into dispatch. Only supports single-arg calls atm, and isn't doing any of the wrapping logic for non-local jumps, etc.
cdfdc56984 Merge branch 'fastmath'
d531d8f51c Improvements to move closer to frameless execution. * Modify ASTInspector to have a more find-grained understanding of what frame bits are used. * Hook dynamic invocation via InvocationCompiler.invokeDynamic into caller-passing CallSite methods.
0fb8adf32 core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
7eac59ded core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
96034ed2a core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
13a08fa68 core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
25fdaa3bb core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
dcd9597d7 core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
ed54aab18 core/src/main/java/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
ed54aab18 core/src/main/java/org/jruby/compiler/impl/StandardInvocationCompiler.java
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
-        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
346b678e2 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
c689af6b2 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
ea1cb9b07 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
53365d3f0 src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
06c26424d src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
1d1c6bad6 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
31a60fe36 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
a5503a49d src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
e26ff6b58 src/org/jruby/compiler/impl/InvokeDynamicInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
cdfdc5698 src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
d531d8f51 src/org/jruby/compiler/impl/StandardInvocationCompiler.java
+        // TODO: don't bother passing when fcall or vcall, and adjust callsite appropriately
*_*_*
proxy doesn't match. Replace
# shas =  0
*_*_*
// XXX: Again, screwy evaling under previous frame's scope
eed14f7490 Grr...failed to commit with merge for some reason.
7774be525a Use target obj as self for instance_eval. Fixes #2301.
1d43870a42 merging cnutter_work1 to HEAD
eed14f749 core/src/main/java/org/jruby/runtime/ThreadContext.java
-    // XXX: Again, screwy evaling under previous frame's scope
7774be525 core/src/main/java/org/jruby/runtime/ThreadContext.java
-    // XXX: Again, screwy evaling under previous frame's scope
1d43870a4 src/org/jruby/runtime/ThreadContext.java
+    // XXX: Again, screwy evaling under previous frame's scope
*_*_*
// Workaround for JRUBY-4149
# shas =  1
*_*_*
// FIXME: How much more obtuse can this be?
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
3351729b98 Brutal insertion of regular lexer into ripper
16e5550655 Lexer jumbo patch.  Speeds up general parsing 10-15%.  Cold parses are about 30% faster.   Code has been refactored to the point that additional optimizations can be considered (like bytelist identifiers for alloc-less identifiers; same for uninterpolated string nodes).
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/lexer/StringTerm.java
-        // FIXME: How much more obtuse can this be?
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/lexer/yacc/StringTerm.java
+        // FIXME: How much more obtuse can this be?
3351729b9 src/org/jruby/ext/ripper/StringTerm.java
+        // FIXME: How much more obtuse can this be?
16e555065 src/org/jruby/lexer/yacc/StringTerm.java
+        // FIXME: How much more obtuse can this be?
*_*_*
// We're not setting the provider or anything, but it seems that BouncyCastle does some internal things in its
564c53fe61 do not attempt running priviledged due BC (fixed since 1.46 ~ 5 years ago)
383206396f Add workaround for JRUBY-3919: Creation of BouncyCastle security provider results in security errors when run in an unprivileged context
564c53fe6 core/src/main/java/org/jruby/ext/digest/RubyDigest.java
-        // We're not setting the provider or anything, but it seems that BouncyCastle does some internal things in its
383206396 src/org/jruby/RubyDigest.java
+        // We're not setting the provider or anything, but it seems that BouncyCastle does some internal things in its
*_*_*
// FIXME: do we really want 'declared' methods? includes private/protected, and does _not_
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
c4e6f18a4d Refactored the new declared_method_smart logic a bit, to get code into the right classes.  (See FIXMEs re: is 'declared' really what was intended?) Other misc. JI cleanup in advance of some changes.
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
c4e6f18a4 src/org/jruby/javasupport/JavaClass.java
c4e6f18a4 src/org/jruby/javasupport/JavaMethod.java
+            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
+        // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
*_*_*
// FIXME: we should also support orgs that use capitalized package
bb9cd07534 Fix JRUBY-6076: Mixed-case Java package name not resolved
e705f6ca4b JI: Moved more JI code into Java to improve performance (and partly to offset the cost of supporting lower-case class names).  Would still like to get instantiation code (__jcreate!) logic moved into Java before 1.1, but won't have time before RC2.
bb9cd0753 src/org/jruby/javasupport/Java.java
-        // FIXME: we should also support orgs that use capitalized package
e705f6ca4 src/org/jruby/javasupport/Java.java
+            // FIXME: we should also support orgs that use capitalized package
*_*_*
// TODO: callback for value would be more efficient
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
3eb979fe14 - Several fixes to get this thing to start compiling ... lots more to go!
a7b4c64e64 - added get_const & put_const instructions and translated colon2 nodes
dfa3f8635f - First commit: incomplete, will not compile, and some files are just outlines
d11f26175e Delegate all equivalent bits from ASTCompiler19 to ASTCompiler.
1d6c408a47 First round of twiddling to get 1.9 compilation work under way.
379fc8d0aa More microoptz: * Use ICONST_M1 through ICONST_5 for small integer values * Use a callback for class var assignment, to avoid a bunch of stack juggling
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
3eb979fe1 src/org/jruby/compiler/ir/IR_Builder.java
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
a7b4c64e6 src/org/jruby/compiler/ir/IR_Builder.java
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
dfa3f8635 src/org/jruby/compiler/ir/IR_Builder.java
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
d11f26175 src/org/jruby/compiler/ASTCompiler19.java
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
1d6c408a4 src/org/jruby/compiler/ASTCompiler19.java
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
379fc8d0a src/org/jruby/compiler/ASTCompiler.java
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
*_*_*
// SSS FIXME: Move this code to some utils area .. or probably there is already a method for this in some jruby utils class
0cfab1256a [IR] Converted CompoundArray to BuildCompoundArrayInstr
dfd8f3550c - Added CompoundArray and fixed code that was buggy because of my incorrect understanding of ArgsCatNode and splats; parallel assigment works well and gets simplified.  Even though this is premature optimization (better to have a peephole optimization pass that simplifies parallel assignment alongwith several other peephole opts), I used this as an example to understand Array, Splat, ArgsCat, SValue.
0cfab1256 core/src/main/java/org/jruby/ir/operands/CompoundArray.java
-            // SSS FIXME: Move this code to some utils area .. or probably there is already a method for this in some jruby utils class
dfd8f3550 src/org/jruby/compiler/ir/CompoundArray.java
+            // SSS FIXME: Move this code to some utils area .. or probably there is already a method for this in some jruby utils class
*_*_*
// FIXME: what should these really be? Numeric?
# shas =  1
*_*_*
// Simplify the variables too -- to keep these variables in sync with what is actually used in the when clauses
18919b8fa6 [IR] Removed unused CaseInstr
dd945066f2 - Cleaned up code some more and added operand simplification methods for use in peephole opts. and later sccp passes.
18919b8fa src/org/jruby/compiler/ir/instructions/CaseInstr.java
-        // Simplify the variables too -- to keep these variables in sync with what is actually used in the when clauses
dd945066f src/org/jruby/compiler/ir/instructions/CASE_Instr.java
+        // Simplify the variables too -- to keep these variables in sync with what is actually used in the when clauses
*_*_*
// FIXME: This should probably do some translation from Ruby priority levels to Java priority levels (until we have green threads)
b048978b6e Map Ruby thread priorities (-3..3) to Java thread priorities.
d2c051a9b9 committing threadgroup changes; they're necessary in the long run and any new failures are just because the threading stuff is flaky to begin with.
b048978b6 core/src/main/java/org/jruby/RubyThread.java
-        // FIXME: This should probably do some translation from Ruby priority levels to Java priority levels (until we have green threads)
d2c051a9b src/org/jruby/RubyThread.java
+        // FIXME: This should probably do some translation from Ruby priority levels to Java priority levels (until we have green threads)
*_*_*
// SSS FIXME: 1. Is the ordering correct? (poll before next)
2ccee8d80d [IR] Bug fixes running ensures on encountering break or next; next now throws local jump error on encountering an out-of-place next
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
955209fba7 - Added IR_Loop; implemented loop stack in IR_Scope; translated while and converted break & next to jumps for non-closure cases
2ccee8d80 src/org/jruby/compiler/ir/IRBuilder.java
-        // SSS FIXME: 1. Is the ordering correct? (poll before next)
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-        // SSS FIXME: 1. Is the ordering correct? (poll before next)
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+        // SSS FIXME: 1. Is the ordering correct? (poll before next)
955209fba src/org/jruby/compiler/ir/IR_Builder.java
+        // SSS FIXME: 1. Is the ordering correct? (poll before next)
*_*_*
// SSS FIXME: These should get normally compiled or initialized some other way …
# shas =  0
*_*_*
// TODO: for now
# shas =  1
*_*_*
// method has more than 500 lines
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
87342fb6ef Fix for JRUBY-2246, chained methods require heap-based scopes.
ed54aab18 core/src/main/java/org/jruby/compiler/ASTInspector.java
-                    // method has more than 500 lines; we'll need to split it
87342fb6e src/org/jruby/compiler/ASTInspector.java
+                    // method has more than 500 lines; we'll need to split it
*_*_*
// if body is a rescue node
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
52d2b6c7bc - More nodes translated; some nodes fixed up; added Regexp
86e7fc9281 - Cleaned up DefnNode, and translated DSymbolNode
dfa3f8635f - First commit: incomplete, will not compile, and some files are just outlines
57f9436a66 Get whole-body rescues to optimize the same way a non-rescued method body would.
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
52d2b6c7b src/org/jruby/compiler/ir/IR_Builder.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
86e7fc928 src/org/jruby/compiler/ir/IR_Builder.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
dfa3f8635 src/org/jruby/compiler/ir/IR_Builder.java
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
57f9436a6 src/org/jruby/compiler/ASTCompiler.java
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
*_*_*
// TODO: Implement tty? and isatty. We have no real capability to
c12dd41569 Fix IO/File.open to return the block value rather than the IO.
bac69eda71 Remove remaining metaclass definitions...
ae3178fbb3 Created IO meta class. Fixed bugs in IO#initialize, IO#foreach and IO#readlines. Fixed Fixnum#[] bug (it doesn't accept classes with a "to_int" method yet!). Fixed multiline regexp bug.
4c689a812e IO work (phase1) 1. Make IO allow random access behavior (seek,rewind,pos) 2. Allow most IO duplication (reopen, clone) 3. Support almost all other previously missing IO method (sans pipe/popen related ones -- popen will be phase2) 4. Fix RubyArgsFile bugs in testing kernel 5. Made all rubicon tests for IO run correctly versus ruby 1.8 (exception: pipe/popen related ones)
c12dd4156 core/src/main/java/org/jruby/RubyIO.java
-        // TODO: Implement tty? and isatty.  We have no real capability to
bac69eda7 src/org/jruby/RubyIO.java
bac69eda7 src/org/jruby/runtime/builtin/meta/IOMetaClass.java
+        // TODO: Implement tty? and isatty.  We have no real capability to
-            // TODO: Implement tty? and isatty.  We have no real capability to
ae3178fbb src/org/jruby/RubyIO.java
ae3178fbb src/org/jruby/runtime/builtin/meta/IOMetaClass.java
-        // TODO: Implement tty? and isatty.  We have no real capability to 
+        // TODO: Implement tty? and isatty.  We have no real capability to 
4c689a812 src/org/jruby/RubyIO.java
+        // TODO: Implement tty? and isatty.  We have no real capability to 
*_*_*
// TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
89cedd5a04 minor JavaClass house-keeping
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
89cedd5a0 core/src/main/java/org/jruby/javasupport/JavaClass.java
-        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
b390103c2 src/org/jruby/javasupport/JavaClass.java
b390103c2 src/org/jruby/javasupport/JavaConstructor.java
b390103c2 src/org/jruby/javasupport/JavaField.java
b390103c2 src/org/jruby/javasupport/JavaMethod.java
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
*_*_*
// FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
7621334c9b Start removing old interpreter
bb98e4b8de Move RuntimeHelpers to org.jruby.runtime.Helpers.
7af96b98dd Modify interpreter to always order block/blockpass strictly left-to-right wrt receiver and arguments. Update to latest RubySpecs which tests this behavior.
45b120ff37 Interp: Add specialized getBlock(iter) to CallNode (to match FCallNode). assert on null iter (and remove if check) in getBlock(iter) since call paths calling getBlock should never be null
dbcdcdbac4 Tom Bomb! New Interpreter landing
8fb40110ad JRUBY-796 Fix for a very big leak in finalizer definition and dynamic scope creation
7621334c9 core/src/main/java/org/jruby/evaluator/ASTInterpreter.java
7621334c9 core/src/main/java/org/jruby/runtime/Helpers.java
-        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
-        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
bb98e4b8d src/org/jruby/javasupport/util/RuntimeHelpers.java
bb98e4b8d src/org/jruby/runtime/Helpers.java
-        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
+        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
7af96b98d src/org/jruby/ast/CallNode.java
7af96b98d src/org/jruby/ast/FCallNode.java
7af96b98d src/org/jruby/javasupport/util/RuntimeHelpers.java
-        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
-        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
+        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
45b120ff3 src/org/jruby/ast/CallNode.java
+        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
dbcdcdbac src/org/jruby/ast/FCallNode.java
+        // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
             // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
8fb40110a src/org/jruby/evaluator/EvaluationState.java
+            // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
*_*_*
// TODO: is this right?
9071ce26f2 [IR] Fix IR for OpAsgnOrNode to generate better-structured control-flow + remove spurious RecvSelf + fix formatting
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
aa0f8552ad Add RECV_ARG support and variable allocation/loading.
9071ce26f src/org/jruby/compiler/ir/IRBuilder.java
-        // TODO: is this right?
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-        // TODO: is this right?
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+        // TODO: is this right?
aa0f8552a src/org/jruby/compiler/ir/IR_Builder.java
+        // TODO: is this right?
*_*_*
// TODO: WRONG - get interfaces from class
1f21e82fc6 [ji] remove re-def of === method on Ruby class implementing an iface
557c40cd6a Remove duplicate JavaInterfaceTemplate not being used.
ec7406706c Move JavaInterfaceTemplate to java.proxies package, add a closure conversion bench, add InterfaceJavaProxy native impl beginnings, but it's not enabled yet.
18d03923f1 Likely fix for JRUBY-2680 and potential fix for JRUBY-2803, by porting the entirety of JavaInterfaceTemplate from Ruby to Java. The bulk of this module was the append_features logic, which was gigantic, involved multiple instance_evals, many method definitions, and a lot of back-and-forth to the Java side of things. Writing up some benchmarks momentarily.
1f21e82fc core/src/main/java/org/jruby/java/proxies/JavaInterfaceTemplate.java
-                        // TODO: WRONG - get interfaces from class
557c40cd6 src/org/jruby/javasupport/JavaInterfaceTemplate.java
-                        // TODO: WRONG - get interfaces from class
ec7406706 src/org/jruby/java/proxies/JavaInterfaceTemplate.java
+                        // TODO: WRONG - get interfaces from class
18d03923f src/org/jruby/javasupport/JavaInterfaceTemplate.java
+                        // TODO: WRONG - get interfaces from class
*_*_*
// TODO: better algorithm to set precision needed
b3342e7752 fix GH-2524 on jruby-1_7: improved algorithm to set the precision
8fec7f43fd fix GH-2524 on master: improved algorithm to set the precision
2f7d092e20 Fixed numerous rubyspec failures for BigDecimal's #quo #div and #/.
b3342e775 core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-            // TODO: better algorithm to set precision needed
8fec7f43f core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-        // TODO: better algorithm to set precision needed
2f7d092e2 src/org/jruby/RubyBigDecimal.java
+            // TODO: better algorithm to set precision needed
*_*_*
// TODO: Should frozen error have its own distinct class? If not should more share?
36ec84bb4b merging exception-removal changes to HEAD; EvaluateVisitor changes left on branch for now
61a7ba7ca1 Eliminate RubyExceptions and its second references to every exception (now initialized in Ruby) Remove all eclipse warnings in projects except for DefaultRubyParser Move TestHelper into test source hierarchy to get it out of jruby.jar
36ec84bb4 src/org/jruby/Ruby.java
36ec84bb4 src/org/jruby/exceptions/FrozenError.java
+		// TODO: Should frozen error have its own distinct class?  If not should more share?
-		// TODO: Should frozen error have its own distinct class?  If not should more share?
61a7ba7ca src/org/jruby/exceptions/FrozenError.java
+		// TODO: Should frozen error have its own distinct class?  If not should more share?
*_*_*
// TODO: handle writing into original buffer better
78b66c2c80 Wire up static IO.read logic, eliminate old read logic, cleanup.
1158df5a7a Large port of MRI IO transcoding logic, to fill in our gaps.
8eb76d0cc4 Reduce the use of null as a flag for buffer use in readAll and readNotAll. Also clean up spec for write_nonblock that seems incorrect.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
78b66c2c8 core/src/main/java/org/jruby/RubyIO.java
-        // TODO: handle writing into original buffer better
-            // TODO: handle writing into original buffer better
-        // TODO: handle writing into original buffer better
1158df5a7 core/src/main/java/org/jruby/RubyIO.java
-        // TODO: handle writing into original buffer better
+            // TODO: handle writing into original buffer better
+        // TODO: handle writing into original buffer better
8eb76d0cc src/org/jruby/RubyIO.java
         // TODO: handle writing into original buffer better
+        // TODO: handle writing into original buffer better
66b024fed src/org/jruby/RubyIO.java
+        // TODO: handle writing into original buffer better
*_*_*
// TODO: better error handling
78f196d805 Working through more logic... sysopen, seek, reopen, init_copy.
892787ae3c Based on some studying of C source for IO#initialize_copy and IO#reopen (for an fd) this commit tries to modify our logic to more closely map to Ruby's. Major missing pieces include: - Ruby's separate handling of an OpenFile struct, FILE* struct, and file descriptor, which map roughly to RubyIO.OpenFile (new in this commit), IOHandler, and Channel - Flushing logic for incoming and current streams - Construction of new IOHandlers to wrap existing channels What's largely missing from all this logic, and what would fill in the remaining pieces, would be a way for us to clone Channels in the same way Ruby can dup or dup2 file descriptors. This missing piece means we can't easily avoid closing channels still in use and can't manage channel position in two separate places.
78f196d80 core/src/main/java/org/jruby/RubyIO.java
-        } catch (IOException ex) { // TODO: better error handling
892787ae3 src/org/jruby/RubyIO.java
+            } catch (IOException ex) { // TODO: better error handling
*_*_*
Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
5a0b41a2fb Updated 2.1 grammar with some non-working bits (like imaginary and rational literals)
bdd0b37b43 Minimum to remove 1.9 parser
db61864d6a minimal work to remove 1.8 parser
5397087cdf Whoops accidentally picked up emacs open file backup
7c6444fad4 Last MRI19 test regression fixed
c586e919b2 Much closer to being done with grammar productions before debugging them more
7a01985b84 Fixed generate issue and rearranged tokens
464c04b3b2 Something is not generating right anymore...syncing
d5713e9388 Broken but saving ripper progress
5fae3aa735 Yay...generated 20 compiler will compile now
f00edcd9d5 First massive edit session complete
ae2975a6ec JRUBY-2326: Invalid cast during parsing of recursive.rb in facelets-2.3.0 (org.jruby.ast.YieldNode cannot be cast to org.jruby.ast.BlockAcceptingNode)
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.java
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.y
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.java
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.y
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.java
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.y
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
db61864d6 core/src/main/java/org/jruby/parser/DefaultRubyParser.java
db61864d6 core/src/main/java/org/jruby/parser/DefaultRubyParser.y
-                  /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                  // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
5397087cd src/org/jruby/parser/#Ruby19Parser.y#
-                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
7c6444fad src/org/jruby/parser/#Ruby19Parser.y#
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
c586e919b src/org/jruby/parser/Ripper19Parser.java
c586e919b src/org/jruby/parser/Ripper19Parser.y
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
7a01985b8 src/org/jruby/parser/Ripper19Parser.java
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
464c04b3b src/org/jruby/parser/Ripper19Parser.java
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
d5713e938 src/org/jruby/parser/Ripper19Parser.java
d5713e938 src/org/jruby/parser/Ripper19Parser.y
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
5fae3aa73 src/org/jruby/parser/Ruby20Parser.java
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
f00edcd9d src/org/jruby/parser/Ruby20Parser.y
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
ae2975a6e src/org/jruby/parser/DefaultRubyParser.java
ae2975a6e src/org/jruby/parser/DefaultRubyParser.y
ae2975a6e src/org/jruby/parser/Ruby19Parser.java
ae2975a6e src/org/jruby/parser/Ruby19Parser.y
+                  /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                  // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
*_*_*
FIXME: lose syntactical elements here (and others like this)
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
5a0b41a2fb Updated 2.1 grammar with some non-working bits (like imaginary and rational literals)
bdd0b37b43 Minimum to remove 1.9 parser
5397087cdf Whoops accidentally picked up emacs open file backup
7c6444fad4 Last MRI19 test regression fixed
412b71b7e5 saved editing up to this point
7a01985b84 Fixed generate issue and rearranged tokens
464c04b3b2 Something is not generating right anymore...syncing
d5713e9388 Broken but saving ripper progress
5fae3aa735 Yay...generated 20 compiler will compile now
f00edcd9d5 First massive edit session complete
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
-                    /* FIXME: lose syntactical elements here (and others like this)*/
-                    // FIXME: lose syntactical elements here (and others like this)
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
+                    /* FIXME: lose syntactical elements here (and others like this)*/
+                    // FIXME: lose syntactical elements here (and others like this)
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.java
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.y
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.java
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.y
-                    /* FIXME: lose syntactical elements here (and others like this)*/
+                    /* FIXME: lose syntactical elements here (and others like this)*/
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.java
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.y
-                    /* FIXME: lose syntactical elements here (and others like this)*/
-                    // FIXME: lose syntactical elements here (and others like this)
5397087cd src/org/jruby/parser/#Ruby19Parser.y#
-                    // FIXME: lose syntactical elements here (and others like this)
7c6444fad src/org/jruby/parser/#Ruby19Parser.y#
+                    // FIXME: lose syntactical elements here (and others like this)
412b71b7e src/org/jruby/parser/Ripper19Parser.java
412b71b7e src/org/jruby/parser/Ripper19Parser.y
-                    /* FIXME: lose syntactical elements here (and others like this)*/
-                    // FIXME: lose syntactical elements here (and others like this)
7a01985b8 src/org/jruby/parser/Ripper19Parser.java
+                    /* FIXME: lose syntactical elements here (and others like this)*/
464c04b3b src/org/jruby/parser/Ripper19Parser.java
-                    /* FIXME: lose syntactical elements here (and others like this)*/
d5713e938 src/org/jruby/parser/Ripper19Parser.java
d5713e938 src/org/jruby/parser/Ripper19Parser.y
+                    /* FIXME: lose syntactical elements here (and others like this)*/
+                    // FIXME: lose syntactical elements here (and others like this)
5fae3aa73 src/org/jruby/parser/Ruby20Parser.java
+                    /* FIXME: lose syntactical elements here (and others like this)*/
f00edcd9d src/org/jruby/parser/Ruby20Parser.y
+                    // FIXME: lose syntactical elements here (and others like this)
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.java
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.y
+                    /* FIXME: lose syntactical elements here (and others like this)*/
+                    // FIXME: lose syntactical elements here (and others like this)
*_*_*
// FIXME: possible to make handles do the superclass call?
# shas =  1
*_*_*
// we basically ignore protocol. let someone report it...
09d1654721 First pass at compatibility work on socket subsystem.
2f20704b03 Fix for JRUBY-2481, sorta. I added one optional argument to UDPSocket.new, but I couldn't figure out what it's actually supposed to do, so it's ignored. But it fixes the primary issue in the bug, new's arity.
09d165472 core/src/main/java/org/jruby/ext/socket/RubyUDPSocket.java
-        // we basically ignore protocol. let someone report it...
2f20704b0 src/org/jruby/ext/socket/RubyUDPSocket.java
+        // we basically ignore protocol. let someone report it...
*_*_*
// TODO: Make this more intelligible value
e04c88bdf4 Add better toString for debugging for ModeFlags
4a09d081fa First large-scale refactoring to better support IO encodings.
5f58e39a27 Remove local files that snuck into previous commit.
d3b828d6df Fix JRUBY-6382
aa4ffa1be5 Fix botched merge that doubled new file content.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
3b52a27ae1 Update IOModes to better support File::XXX open constants
e04c88bdf src/org/jruby/util/io/ModeFlags.java
-        // TODO: Make this more intelligible value
4a09d081f src/org/jruby/util/io/IOOptions.java
+        // TODO: Make this more intelligible value
5f58e39a2 src/org/jruby/util/io/IOOptions.java
-        // TODO: Make this more intelligible value
d3b828d6d src/org/jruby/util/io/IOOptions.java
+        // TODO: Make this more intelligible value
aa4ffa1be src/org/jruby/util/io/ModeFlags.java
-        // TODO: Make this more intelligible value
66b024fed src/org/jruby/util/IOModes.java
66b024fed src/org/jruby/util/io/ModeFlags.java
-    // TODO: Make this more intelligible value
+        // TODO: Make this more intelligible value
+        // TODO: Make this more intelligible value
3b52a27ae src/org/jruby/util/IOModes.java
+    // TODO: Make this more intelligible value
*_*_*
// FIXME: wtf is this? Why would these use the class?
074e6611bd slightly update newInterfaceImpl + move iface proxy invocation handler to inner class
3bb7b8c192 Temporary workaround for JRUBY-3100, property jruby.interfaces.useProxy=true modifies JI to use java.lang.reflect.Proxy instead of hand-generating an impl, which appears to work better in OSGi environment.
70cf58217e Second phase of interface impl refactoring, reuse minijava code to provide a dynamic-dispatch-free path from the implemented methods (now all generated into a real class) directly to the DynamicMethod they go with. Also eliminated the ruby-based method_missing used for Proc coercion to interfaces. Other than cleaning up code, reducing bytecode generated, and improving coercion, it's mostly done.
48a55e65c1 First step to make Ruby interface implementations faster: eliminated intermediate JavaObject coercion products, eliminated intermediate Proc invocation, eliminated use of __send__ call.
074e6611b core/src/main/java/org/jruby/javasupport/Java.java
-                // FIXME: wtf is this? Why would these use the class?
3bb7b8c19 src/org/jruby/javasupport/Java.java
+                    // FIXME: wtf is this? Why would these use the class?
70cf58217 src/org/jruby/javasupport/Java.java
-                // FIXME: wtf is this? Why would these use the class?
48a55e65c src/org/jruby/javasupport/Java.java
+                // FIXME: wtf is this? Why would these use the class?
*_*_*
// TODO: Bleeding runtime into parser. Arity may be should be in parser (to keep bleeding oneway)
428e058219 Remove IArityNode and all consumers (this was dead code)
a36ff3729d More moving towards java.util.Stack contract for remaining Abstract Stack users args are never null (we no longer null check with a few strategic asserts) getPosition() always returns something from Nodes (we no longer null check) Remove AttrSetNode (fake parser node) in favor of CallbackMethod we were pushing/popping an extra dynamic vars set every yield
428e05821 core/src/main/java/org/jruby/ast/types/IArityNode.java
-// TODO: Bleeding runtime into parser.  Arity may be should be in parser (to keep bleeding oneway)
a36ff3729 src/org/jruby/ast/types/IArityNode.java
+// TODO: Bleeding runtime into parser.  Arity may be should be in parser (to keep bleeding oneway)
*_*_*
ENEBO: Lots of optz in 1.9 parser here
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
5a0b41a2fb Updated 2.1 grammar with some non-working bits (like imaginary and rational literals)
bdd0b37b43 Minimum to remove 1.9 parser
5397087cdf Whoops accidentally picked up emacs open file backup
7c6444fad4 Last MRI19 test regression fixed
412b71b7e5 saved editing up to this point
7a01985b84 Fixed generate issue and rearranged tokens
464c04b3b2 Something is not generating right anymore...syncing
d5713e9388 Broken but saving ripper progress
5fae3aa735 Yay...generated 20 compiler will compile now
f00edcd9d5 First massive edit session complete
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
-                      /* ENEBO: Lots of optz in 1.9 parser here*/
-                      // ENEBO: Lots of optz in 1.9 parser here
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
+                      /* ENEBO: Lots of optz in 1.9 parser here*/
+                      // ENEBO: Lots of optz in 1.9 parser here
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.java
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.y
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.java
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.y
-                      /* ENEBO: Lots of optz in 1.9 parser here*/
+                      /* ENEBO: Lots of optz in 1.9 parser here*/
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.java
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.y
-                      /* ENEBO: Lots of optz in 1.9 parser here*/
-                      // ENEBO: Lots of optz in 1.9 parser here
5397087cd src/org/jruby/parser/#Ruby19Parser.y#
-                      // ENEBO: Lots of optz in 1.9 parser here
7c6444fad src/org/jruby/parser/#Ruby19Parser.y#
+                      // ENEBO: Lots of optz in 1.9 parser here
412b71b7e src/org/jruby/parser/Ripper19Parser.java
412b71b7e src/org/jruby/parser/Ripper19Parser.y
-                      /* ENEBO: Lots of optz in 1.9 parser here*/
-                      // ENEBO: Lots of optz in 1.9 parser here
7a01985b8 src/org/jruby/parser/Ripper19Parser.java
+                      /* ENEBO: Lots of optz in 1.9 parser here*/
464c04b3b src/org/jruby/parser/Ripper19Parser.java
-                      /* ENEBO: Lots of optz in 1.9 parser here*/
d5713e938 src/org/jruby/parser/Ripper19Parser.java
d5713e938 src/org/jruby/parser/Ripper19Parser.y
+                      /* ENEBO: Lots of optz in 1.9 parser here*/
+                      // ENEBO: Lots of optz in 1.9 parser here
5fae3aa73 src/org/jruby/parser/Ruby20Parser.java
+                      /* ENEBO: Lots of optz in 1.9 parser here*/
f00edcd9d src/org/jruby/parser/Ruby20Parser.y
+                      // ENEBO: Lots of optz in 1.9 parser here
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.java
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.y
+                      /* ENEBO: Lots of optz in 1.9 parser here*/
+                      // ENEBO: Lots of optz in 1.9 parser here
*_*_*
// FIXME: This is almost entirely duplicated from Main.java
20ff6278cc Fix JRUBY-5322: NPE forcing to compile a script
58d67c3af3 Clean up Main to use the config's error output and NailMain to handle raised exceptions properly (eliminates "MainExitException logging on NG server).
20ff6278c src/org/jruby/util/NailMain.java
-        // FIXME: This is almost entirely duplicated from Main.java
58d67c3af src/org/jruby/util/NailMain.java
+        // FIXME: This is almost entirely duplicated from Main.java
*_*_*
// TODO: This is also defined in the MetaClass too...Consolidate somewhere.
d055a0ea0c More encoding-related IO cleanup.
0a56a82804 IO and File now have full fledged meta classes Many methods in IO and File now do conversions of arguments to the types they expect
d055a0ea0 src/org/jruby/RubyFile.java
-    // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
0a56a8280 src/org/jruby/RubyFile.java
+    // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
*_*_*
FIXME: Resolve what the hell is going on
84ac6052e0 Add/fix syntax errors for duplicate variables
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
84ac6052e src/org/jruby/parser/Ruby19Parser.java
84ac6052e src/org/jruby/parser/Ruby19Parser.y
-    /* FIXME: Resolve what the hell is going on*/
-    // FIXME: Resolve what the hell is going on
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.java
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.y
+    /* FIXME: Resolve what the hell is going on*/
+    // FIXME: Resolve what the hell is going on
*_*_*
// FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
3e74923964 Deleted a bunch of dead code
57de8506bd Pass impl class into methods and clean up arg offset logic.
16b2963505 [IR] First pass implementing an explicit call protocol via IR instructions -- for now, only bindings are explicitly managed for non-closure and non-eval scopes. Frames, etc. will come later.
91799eaaaf Large refactoring of CallConfiguration and compiler logic pertaining to it:
3fc75aa4e0 Eliminate constant and class var dependency on heap-based scopes.
171b97463f Added a "frameless" compilation mode that will compile code without framing when possible. Specify -Djruby.compile.frameless=true
94809836f2 - Enabled assertions during all test runs, and made fixes and modifications as appropriate to get those assertions passing again. - Removed -da from jruby startup script to allow specifying -J-da on command line - A few optimizations to IOOutputStream/IOInputStream to use CallAdapter and lightweight strings - Cleaned up the gaggle of private RubyString constructors to four core versions, and added lightweight versions of a few public construction methods
3e7492396 core/src/main/java/org/jruby/runtime/ThreadContext.java
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
57de8506b core/src/main/java/org/jruby/runtime/ThreadContext.java
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
16b296350 src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
91799eaaa src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
3fc75aa4e src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
171b97463 src/org/jruby/runtime/ThreadContext.java
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
94809836f src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
*_*_*
// HACK: in order to have stdout/err act like ttys and flush always
94207aff25 Loosen restriction on $stderr to just #write.
9e049bca88 Make IO#read buffered for all other cases (File was done yesterday). Mild hack: set any stream that becomes stdout/stderr to be sync, since ttys are not fully buffered in C/MRI, but we can't (currently) determine if a given stream corresponds to a tty (perhaps out instanceof PrintWriter could be an indication?
94207aff2 core/src/main/java/org/jruby/RubyGlobal.java
-//            // HACK: in order to have stdout/err act like ttys and flush always,
9e049bca8 src/org/jruby/RubyGlobal.java
+                // HACK: in order to have stdout/err act like ttys and flush always,
*_*_*
FIXME: This version is faster than the previous
# shas =  1
*_*_*
// TODO: no need for this to be a full
02b8e01b46 Remove MiniJava and all the tendrils it extended through JRuby. Useful remainder moved to org.jruby.java.codegen.RealClassGenerator.
3bb6b82f87 Early work on a new Java layer that's lighter-weight, more "raw", and more explicit.
02b8e01b4 src/org/jruby/java/MiniJava.java
-            // TODO: no need for this to be a full, formal JVM signature
-            // TODO: no need for this to be a full, formal JVM signature
3bb6b82f8 src/org/jruby/java/MiniJava.java
+            // TODO: no need for this to be a full, formal JVM signature
+            // TODO: no need for this to be a full, formal JVM signature
*_*_*
// optional args
cb8f537d74 Add an offline indy-based invoker generator.
e084deabce Add a separate type of JavaMethodDescriptor for compile time.
2f7cc79224 Get the annotation-based binding generator working again with multimethods and optional 'required' attrs.
a0ed4fd9d0 Abstract the idea of a "descriptor" given a method and its annotation into a separate structure we can pass around and store.
cb8f537d7 core/src/main/java/org/jruby/anno/IndyBinder.java
+            // optional args, so we have IRubyObject[]
e084deabc core/src/main/java/org/jruby/anno/JavaMethodDescriptor.java
e084deabc core/src/main/java/org/jruby/anno/MethodDescriptor.java
-            // optional args, so we have IRubyObject[]
+            // optional args, so we have IRubyObject[]
2f7cc7922 src/org/jruby/anno/AnnotationBinder.java
+                    // optional args, so we have IRubyObject[]
a0ed4fd9d src/org/jruby/anno/JavaMethodDescriptor.java
+            // optional args, so we have IRubyObject[]
*_*_*
// TODO: Why does that bother me?
28bf75a927 Minor tweaks to get JRuby running unmodified on Android:
ae39a9e4dd Add a bit more exception handling for bean registration, to quietly fail to register for security exceptions. Should help fix JRUBY-3055: permission javax.management.MBeanServerPermission "createMBeanServer";
762ade82c1 Eliminate dependency on ruby (MRI) for generating updated SCM revision info in -v output.
28bf75a92 src/org/jruby/management/BeanManager.java
28bf75a92 src/org/jruby/management/BeanManagerImpl.java
-            // TODO: Why does that bother me?
-            // TODO: Why does that bother me?
-            // TODO: Why does that bother me?
-            // TODO: Why does that bother me?
+            // TODO: Why does that bother me?
+            // TODO: Why does that bother me?
+            // TODO: Why does that bother me?
+            // TODO: Why does that bother me?
ae39a9e4d src/org/jruby/management/BeanManager.java
             // TODO: Why does that bother me?
+            // TODO: Why does that bother me?
+            // TODO: Why does that bother me?
+            // TODO: Why does that bother me?
762ade82c src/org/jruby/management/BeanManager.java
+            // TODO: Why does that bother me?
*_*_*
// FIXME: This is probably not very efficient
# shas =  1
*_*_*
// FIXME: do we really want 'declared' methods? includes private/protected
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
c4e6f18a4d Refactored the new declared_method_smart logic a bit, to get code into the right classes.  (See FIXMEs re: is 'declared' really what was intended?) Other misc. JI cleanup in advance of some changes.
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
c4e6f18a4 src/org/jruby/javasupport/JavaClass.java
c4e6f18a4 src/org/jruby/javasupport/JavaMethod.java
+            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
+        // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
*_*_*
FIXME: Whis is this named "push_m"?
719bc20aba Remove FIXME asking why push_m is named push_m.
e1a1b4740e Migrate RubyArray to annotation-based binding
719bc20ab src/org/jruby/RubyArray.java
-     * FIXME: Whis is this named "push_m"?
e1a1b4740 src/org/jruby/RubyArray.java
+     * FIXME: Whis is this named "push_m"?
*_*_*
FIXME convert to enum ?
d1f89e004f Make ClassIndex into an enum, to prevent issues like #1004.
24befe5cf0 House cleaning! Delete a bunch of unused and/or deprecated classes of strange and wonderful origins, eliminate all deprecated Callback-related code, reduce environment lookup to a simple System.getenv call, kill off the last of the old stackless interpreter's "Instruction" interface, and other miscellaneous cleanup.
1c1c855342 Cleanup more low-hanging-fruit, this time in org.jruby.runtime - final fields where possible - deprecate unused classes - tag a few enum-like classes with FIXME to see if we want to convert to an enum
d1f89e004 core/src/main/java/org/jruby/runtime/ClassIndex.java
- * FIXME convert to enum ?
24befe5cf src/org/jruby/runtime/Iter.java
- * FIXME convert to enum ?
1c1c85534 src/org/jruby/runtime/ClassIndex.java
1c1c85534 src/org/jruby/runtime/Iter.java
+ * FIXME convert to enum ?
+ * FIXME convert to enum ?
*_*_*
// Not sure how well this works
de43a3b79d [1.8] Adopt Mersenne Twister PRNG.
fe62bbf1e0 Kernel#srand was not generating a different seed if called within the same millisecond
de43a3b79 src/org/jruby/RubyKernel.java
-        // Not sure how well this works, but it works much better than
fe62bbf1e src/org/jruby/KernelModule.java
+        	// Not sure how well this works, but it works much better than
*_*_*
// optional args, so we have IRubyObject[]
cb8f537d74 Add an offline indy-based invoker generator.
e084deabce Add a separate type of JavaMethodDescriptor for compile time.
2f7cc79224 Get the annotation-based binding generator working again with multimethods and optional 'required' attrs.
a0ed4fd9d0 Abstract the idea of a "descriptor" given a method and its annotation into a separate structure we can pass around and store.
cb8f537d7 core/src/main/java/org/jruby/anno/IndyBinder.java
e084deabc core/src/main/java/org/jruby/anno/JavaMethodDescriptor.java
e084deabc core/src/main/java/org/jruby/anno/MethodDescriptor.java
2f7cc7922 src/org/jruby/anno/AnnotationBinder.java
a0ed4fd9d src/org/jruby/anno/JavaMethodDescriptor.java
*_*_*
// FIXME: Is this ok?
0d4f883468 Remove rarely used and never quite right thread-pool mode.
2e04166f04 Multiple fixes for threadpool-based execution, and enable threadpool tests in test-all.
0d4f88346 core/src/main/java/org/jruby/internal/runtime/FutureThread.java
-                // FIXME: Is this ok?
2e04166f0 src/org/jruby/internal/runtime/FutureThread.java
+                // FIXME: Is this ok?
*_*_*
// FIXME: If NativeException is expected to be used from Ruby code
725be6439d Fix JRUBY-6103: allocator undefined for NativeException by providing a simple allocator for NativeException.
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
725be6439 src/org/jruby/NativeException.java
-        // FIXME: If NativeException is expected to be used from Ruby code, it should provide
b390103c2 src/org/jruby/NativeException.java
+        // FIXME: If NativeException is expected to be used from Ruby code, it should provide
*_*_*
// TODO this should entry into error handling somewhere
6884f63c4f Restore minimal part of old IO for jossl 0.9.5 to work.
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
aa4ffa1be5 Fix botched merge that doubled new file content.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
6884f63c4 core/src/main/java/org/jruby/util/io/ChannelStream.java
+        // TODO this should entry into error handling somewhere
a6f20b63a core/src/main/java/org/jruby/util/io/ChannelStream.java
-        // TODO this should entry into error handling somewhere
aa4ffa1be src/org/jruby/util/io/ChannelStream.java
-        // TODO this should entry into error handling somewhere
66b024fed src/org/jruby/util/io/ChannelStream.java
+        // TODO this should entry into error handling somewhere
+        // TODO this should entry into error handling somewhere
*_*_*
// TODO: make this do specific-arity calling
02b8e01b46 Remove MiniJava and all the tendrils it extended through JRuby. Useful remainder moved to org.jruby.java.codegen.RealClassGenerator.
70cf58217e Second phase of interface impl refactoring, reuse minijava code to provide a dynamic-dispatch-free path from the implemented methods (now all generated into a real class) directly to the DynamicMethod they go with. Also eliminated the ruby-based method_missing used for Proc coercion to interfaces. Other than cleaning up code, reducing bytecode generated, and improving coercion, it's mostly done.
02b8e01b4 src/org/jruby/java/MiniJava.java
02b8e01b4 src/org/jruby/java/codegen/RealClassGenerator.java
-            // TODO: make this do specific-arity calling
+            // TODO: make this do specific-arity calling
70cf58217 src/org/jruby/java/MiniJava.java
+            // TODO: make this do specific-arity calling
*_*_*
// TODO: It's perhaps just a coincidence that all the channels for
a4cc5eeffd Only EPIPE for unseekable streams that are selectable.
6884f63c4f Restore minimal part of old IO for jossl 0.9.5 to work.
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
16c9890106 Fix seek logic for various channels and fill in a few small blanks
ecc42a6c13 IO work continues.
f38e7fd38a Fix most seek logic, cleanup old gets remnants, minor cleanup.
23d786f79b Additional fixes to get reopen, popen, and seeking working correctly with various types of streams.
a4cc5eeff core/src/main/java/org/jruby/util/io/PosixShim.java
+            // TODO: It's perhaps just a coincidence that all the channels for
6884f63c4 core/src/main/java/org/jruby/util/io/ChannelStream.java
+            // TODO: It's perhaps just a coincidence that all the channels for
a6f20b63a core/src/main/java/org/jruby/util/io/ChannelStream.java
-            // TODO: It's perhaps just a coincidence that all the channels for
16c989010 core/src/main/java/org/jruby/util/io/PosixShim.java
-            // TODO: It's perhaps just a coincidence that all the channels for
ecc42a6c1 core/src/main/java/org/jruby/util/io/OpenFile.java
ecc42a6c1 core/src/main/java/org/jruby/util/io/PosixShim.java
-            // TODO: It's perhaps just a coincidence that all the channels for
+            // TODO: It's perhaps just a coincidence that all the channels for
f38e7fd38 core/src/main/java/org/jruby/util/io/OpenFile.java
+            // TODO: It's perhaps just a coincidence that all the channels for
23d786f79 src/org/jruby/util/io/ChannelStream.java
+            // TODO: It's perhaps just a coincidence that all the channels for
*_*_*
// FIXME: There's some code duplication here with RubyObject#inspect
# shas =  1
*_*_*
// TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
3eb979fe14 - Several fixes to get this thing to start compiling ... lots more to go!
a7b4c64e64 - added get_const & put_const instructions and translated colon2 nodes
dfa3f8635f - First commit: incomplete, will not compile, and some files are just outlines
d11f26175e Delegate all equivalent bits from ASTCompiler19 to ASTCompiler.
1d6c408a47 First round of twiddling to get 1.9 compilation work under way.
379fc8d0aa More microoptz: * Use ICONST_M1 through ICONST_5 for small integer values * Use a callback for class var assignment, to avoid a bunch of stack juggling
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
3eb979fe1 src/org/jruby/compiler/ir/IR_Builder.java
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
a7b4c64e6 src/org/jruby/compiler/ir/IR_Builder.java
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
dfa3f8635 src/org/jruby/compiler/ir/IR_Builder.java
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
d11f26175 src/org/jruby/compiler/ASTCompiler19.java
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
-        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
1d6c408a4 src/org/jruby/compiler/ASTCompiler19.java
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
379fc8d0a src/org/jruby/compiler/ASTCompiler.java
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
+        // TODO: callback for value would be more efficient, but unlikely to be a big cost (constants are rarely assigned)
*_*_*
// TODO: the rest of this...seeking to same position is unnecessary since we share a channel
78f196d805 Working through more logic... sysopen, seek, reopen, init_copy.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
78f196d80 core/src/main/java/org/jruby/RubyIO.java
-            // TODO: the rest of this...seeking to same position is unnecessary since we share a channel
66b024fed src/org/jruby/RubyIO.java
+            // TODO: the rest of this...seeking to same position is unnecessary since we share a channel
*_*_*
// lame generics issues: making Segment class static and manually
# shas =  1
*_*_*
// TODO: should probably apply the null object pattern for a
# shas =  1
*_*_*
// FIXME This whole thing could probably be implemented as a module and
# shas =  1
*_*_*
// TODO: exec should replace the current process
0bf233162c Native exec turned on (only windows currently supported in jnr-posix). Fix some permissions
8548cc7b0a Fix for JRUBY-1873 from Vladimir.
0bf233162 src/org/jruby/RubyKernel.java
-            // TODO: exec should replace the current process.
8548cc7b0 src/org/jruby/RubyKernel.java
+            // TODO: exec should replace the current process.
*_*_*
when MET choose CET timezone to work around Joda
0a93732b75 Fix JRUBY-3560: Time.local ignores provided time zone
55f03b6376 Fix for JRUBY-2759: Time Zone "MET" maps to Asia/Tehran
0a93732b7 src/org/jruby/RubyTime.java
-        // For JRUBY-2759, when MET choose CET timezone to work around Joda
55f03b637 src/org/jruby/RubyTime.java
+        // For JRUBY-2759, when MET choose CET timezone to work around Joda
*_*_*
//TODO: Should next and return also catch here?
bc9a5bea27 GH #2416 (partial). Kill last vestiges of breakjump
da0b65ceb3 Fix remaining BasicObject specs and probably remove some obsolete tag files
8da55f45fc Last round of changes for JRUBY-1386, improve instance_eval. Added cases to the benchmark too.
50d6342dd5 Creating extra block when we shouldn't (fixes Roberts builder example from mailing list) Java List objects can now use collect without throwing exception Remove 'construct' from Enumerable Smaller block/proc fixes
bc9a5bea2 core/src/main/java/org/jruby/RubyBasicObject.java
-            //TODO: Should next and return also catch here?
-            //TODO: Should next and return also catch here?
da0b65ceb src/org/jruby/RubyBasicObject.java
da0b65ceb src/org/jruby/RubyObject.java
+            //TODO: Should next and return also catch here?
+            //TODO: Should next and return also catch here?
-            //TODO: Should next and return also catch here?
-            //TODO: Should next and return also catch here?
8da55f45f src/org/jruby/RubyObject.java
+            //TODO: Should next and return also catch here?
50d6342dd src/org/jruby/RubyObject.java
+                    //TODO: Should next and return also catch here?
*_*_*
// We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it)
9d57dbc3b7 JRUBY-61, JRUBY-2140, JRUBY-4253: Merry Christmas JRuby windows users!
aa4ffa1be5 Fix botched merge that doubled new file content.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
e8803f4c43 Minor cleanup
9ca8281855 Eliminate IOHandlerJavaIO through copy/paste duplication of its common code. Divide and conquer; the children will now be refactored independently, and commonality can be pulled out as part of that process.
11130fcb71 Simple IO Refactoring: - ByteList.append(InputStream, length) for encapsulation (also one impl for all inputstream consumers) - Fix old writeable -> writable typo - Remove some dead code
9d57dbc3b src/org/jruby/util/io/Stream.java
-    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
aa4ffa1be src/org/jruby/util/io/Stream.java
-    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
66b024fed src/org/jruby/util/Stream.java
66b024fed src/org/jruby/util/io/Stream.java
-    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
+    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
+    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
e8803f4c4 src/org/jruby/util/AbstractIOHandler.java
-    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
9ca828185 src/org/jruby/util/AbstractIOHandler.java
+    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
     // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
11130fcb7 src/org/jruby/util/IOHandler.java
+    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
*_*_*
// SSS FIXME: Should this be a string literal or a string?
e41ed6a4c8 All IRScopes have names.  Push name up
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
9f9a147f39 - More nodes translated + added compound string + updated builder context code
e41ed6a4c src/org/jruby/compiler/ir/IRScript.java
-    public final String fileName;    // SSS FIXME: Should this be a string literal or a string?
8dff5b2a5 src/org/jruby/compiler/ir/IR_Script.java
-    public final String _fileName;    // SSS FIXME: Should this be a string literal or a string?
f9c3ccc4a src/org/jruby/compiler/ir/IRScript.java
+    public final String fileName;    // SSS FIXME: Should this be a string literal or a string?
9f9a147f3 src/org/jruby/compiler/ir/IR_Script.java
+      // SSS FIXME: Should this be a string literal or a string?
*_*_*
// TODO: this could be further optimized
ed0a495c61 Remove support for and uses of deprecated Callback methods.
9251dc2018 Grr...I suppose I should have checked this before, but Mongrel still uses CallbackFactory. We need to get them off it so we can delete it some day. Damn you, crufty old dependencies!
24befe5cf0 House cleaning! Delete a bunch of unused and/or deprecated classes of strange and wonderful origins, eliminate all deprecated Callback-related code, reduce environment lookup to a simple System.getenv call, kill off the last of the old stackless interpreter's "Instruction" interface, and other miscellaneous cleanup.
3c6108a399 Fix for -C and optimizations to the compiler and dispatcher to shorten the call path and give HotSpot better targets to JIT.
ed0a495c6 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
-        // TODO: this could be further optimized, since some DSLs hit method_missing pretty hard...
9251dc201 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
+        // TODO: this could be further optimized, since some DSLs hit method_missing pretty hard...
24befe5cf src/org/jruby/runtime/callback/InvocationCallbackFactory.java
-        // TODO: this could be further optimized, since some DSLs hit method_missing pretty hard...
3c6108a39 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
+                    // TODO: this could be further optimized, since some DSLs hit method_missing pretty hard...
*_*_*
// FIXME: This is an ugly hack to resolve JRUBY-1381
da0b65ceb3 Fix remaining BasicObject specs and probably remove some obsolete tag files
8da55f45fc Last round of changes for JRUBY-1386, improve instance_eval. Added cases to the benchmark too.
f16e305fcf Not-so-pretty fix for JRUBY-1381: define_method with an instance_eval'ed block from the surrounding scope doesn't get the right self.
da0b65ceb src/org/jruby/RubyBasicObject.java
da0b65ceb src/org/jruby/RubyObject.java
+        // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
-            // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
-            // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
8da55f45f src/org/jruby/RubyObject.java
+            // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
f16e305fc src/org/jruby/RubyObject.java
+                    // FIXME: This is an ugly hack to resolve JRUBY-1381; I'm not proud of it
*_*_*
// FIXME: This isn't right for within ensured/rescued code
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
148883daa1 Split compiler-related inner classes into top-level classes, in preparation for more refactoring.
fc09ed420d Multiple additional cleanups, fixes, to the compiler; expand stack-based methods to include those with opt/rest/block args, fix a problem with redo/next in ensure/rescue; fix an issue in the ASTInspector not inspecting opt arg values; shrink the generated bytecode by offloading to CompilerHelpers in a few places. Ruby stdlib now compiles completely. Yay!
ed54aab18 core/src/main/java/org/jruby/compiler/impl/ChildScopedBodyCompiler.java
-        // FIXME: This isn't right for within ensured/rescued code
148883daa src/org/jruby/compiler/impl/ASMClosureCompiler.java
148883daa src/org/jruby/compiler/impl/StandardASMCompiler.java
+        // FIXME: This isn't right for within ensured/rescued code
-            // FIXME: This isn't right for within ensured/rescued code
fc09ed420 src/org/jruby/compiler/impl/StandardASMCompiler.java
+            // FIXME: This isn't right for within ensured/rescued code
*_*_*
// TODO: These were missing, so we're not handling them elsewhere?
00633ae67b Refactoring File: reordering, minor cleanup.
1cd554c7c6 Use constantine for O_* constants in File
bac69eda71 Remove remaining metaclass definitions...
1d43870a42 merging cnutter_work1 to HEAD
00633ae67 src/org/jruby/RubyFile.java
-        // TODO: These were missing, so we're not handling them elsewhere?
1cd554c7c src/org/jruby/RubyFile.java
         // TODO: These were missing, so we're not handling them elsewhere?
-        // TODO: These were missing, so we're not handling them elsewhere?
bac69eda7 src/org/jruby/RubyFile.java
bac69eda7 src/org/jruby/runtime/builtin/meta/FileMetaClass.java
+        // TODO: These were missing, so we're not handling them elsewhere?
+        // TODO: These were missing, so we're not handling them elsewhere?
-            // TODO: These were missing, so we're not handling them elsewhere?
-            // TODO: These were missing, so we're not handling them elsewhere?
1d43870a4 src/org/jruby/runtime/builtin/meta/FileMetaClass.java
+            // TODO: These were missing, so we're not handling them elsewhere?
+            // TODO: These were missing, so we're not handling them elsewhere?
*_*_*
// FIXME: Ideally JavaObject instances should be marshallable
# shas =  1
*_*_*
wipe out the SoftReference (this could be done with a reference queue)
# shas =  1
*_*_*
// FIXME: move out of this class!
# shas =  1
*_*_*
// Let's only generate methods for those the user may actually
c4a44b8f7e allow for (potential) JavaProxyClassFactory overrides ... as intented
fcb53b843a add generix to Set in JavaProxyClass.getProxyClass's parameters
0b9733a012 JRUBY-903: Java interface modules (by Bill Dortch) Update some samples to use newer syntax Remove miscellaneous unusied imports
c4a44b8f7 core/src/main/java/org/jruby/javasupport/proxy/JavaProxyClass.java
+        // Let's only generate methods for those the user may actually intend to override.
fcb53b843 core/src/main/java/org/jruby/javasupport/proxy/JavaProxyClass.java
-        // Let's only generate methods for those the user may actually 
0b9733a01 src/org/jruby/javasupport/proxy/JavaProxyClass.java
+        // Let's only generate methods for those the user may actually 
*_*_*
// FIXME: JRUBY-3188 ends up with condition returning null...quick fix until I can dig into it
7621334c9b Start removing old interpreter
f703abd97f JRUBY-3188: Quick fix until I can figure out which node it returning null
7621334c9 core/src/main/java/org/jruby/ast/IfNode.java
-        // FIXME: JRUBY-3188 ends up with condition returning null...quick fix until I can dig into it
f703abd97 src/org/jruby/ast/IfNode.java
+        // FIXME: JRUBY-3188 ends up with condition returning null...quick fix until I can dig into it
*_*_*
since conditional calling #inherited doesn't fit standard ruby semantics
2bdfb5aab5 Eliminate proxy leak by adding pre/post to unfinished proxy check.
3e88cfab0d - Identified and extracted construction paths for class/module/singleton/IncludedModuleWrapper, now there's no unnecessary null checks or assignments in their constructors (they take very few arguments so even asserts are not needed). Each of those constructors have a comment what is it the responsible for, also, two RubyClass.newClass() and two RubyModule.newModule() methods have been extracted. - Fixed logic that sets the parent/constant for Modules, now matches MRI (only setConstant/getConstantAt are used here). - RubyClass rewritten, special attention has been put on allocation logic, now Class.new(...) doesn't fire a separate allocator class. (btw, logic of "class B < A;end" and "Class.new(A)" must be split since in second case superclass and allocator are not known until initialize is called). Class/Object allocation implementation mimics MRI's weirdest quirks in behavior, is _much_ simpler that it has been and much faster. This also allowed creation of generalized clone/dup implementations matching MRI. (only our special ivars need a care here). - Rewritten bootstrapping of Object/Class/Module. - Core classes (except Exception hierarchy, they should also be in future) are now cached in runtime (MRI caches them in static VALUE's like rb_cObject) Ruby.getClass(String), Ruby.getModule(String) and Ruby.getClassFromPath(String) should be used only by either interpreter or compiler and when the class name is not known at compile time. - Fixed and simplified nodes: classNode, moduleNode, sclassNode, defsNode. Compiler has been synchronized here as well. - No more __attached__ as ivar, it is now MetaClass field (which means lazy ivars for singletons). Compiler also updated. - added and dosumented isModule() to IRubyObject (isClass(), isSintleton() documented). - Fixed test_autoload test (Object.class_eval do;autoload :SomeClass, 'somefile';class SomeClass; end;end) should raise LoadError, now matches MRI.
2bdfb5aab core/src/main/java/org/jruby/javasupport/Java.java
-        // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
3e88cfab0 src/org/jruby/javasupport/Java.java
+        // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
*_*_*
// Sometimes the value can be retrieved at "compile time". If we succeed, nothing like it!
d8c290d988 Moved modules, classes, constants from IRScopeImpl to IRModule to move away from recording lexical information to recording semantic information.  Moved away from 'compile-time' resolution of Ruby constants because Module.remove_const and set_const makes such resolutions potentially incorrect.
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
a7b4c64e64 - added get_const & put_const instructions and translated colon2 nodes
86df31ace2 - Renamed RUBY_IMPL_CALL_Instr to RUBY_INTERNALS_CALL_Instr .. translated a few more nodes and added more fixmes!
d8c290d98 src/org/jruby/compiler/ir/IRScopeImpl.java
-    // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!
8dff5b2a5 src/org/jruby/compiler/ir/IR_ScopeImpl.java
-    // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!
f9c3ccc4a src/org/jruby/compiler/ir/IRScopeImpl.java
+    // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!
a7b4c64e6 src/org/jruby/compiler/ir/IR_Builder.java
a7b4c64e6 src/org/jruby/compiler/ir/IR_ScopeImpl.java
-            // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!  
+        // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!  
86df31ace src/org/jruby/compiler/ir/IR_Builder.java
+            // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!  
*_*_*
// FIXME: This worries me a bit
6884f63c4f Restore minimal part of old IO for jossl 0.9.5 to work.
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
aa4ffa1be5 Fix botched merge that doubled new file content.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
fa98e5022c Remove all references held to the RandomAccessFile used to get the FileChannel; they're not needed.
6884f63c4 core/src/main/java/org/jruby/util/io/ChannelStream.java
+            // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
a6f20b63a core/src/main/java/org/jruby/util/io/ChannelStream.java
-            // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
aa4ffa1be src/org/jruby/util/io/ChannelStream.java
-            // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
66b024fed src/org/jruby/util/ChannelStream.java
66b024fed src/org/jruby/util/io/ChannelStream.java
-            // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
+            // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
+            // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
fa98e5022 src/org/jruby/util/IOHandlerSeekable.java
+            // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
*_*_*
// TODO: is this the right thing to do?
a38a622204 Handle CancelledKeyException in IO.select
fb60a5baf3 Reimplement IO.select/Kernel.select to reduce object churn.
043df60067 More tags to get ant spec running on Mac Java 5. Also turned on specdoc output to track where the server freezes. Also a minor modification to RubyIO.select_static to hopefully make it more robust if keys get cancelled as a result of something closing immediately before the select happens.
a38a62220 core/src/main/java/org/jruby/util/io/SelectBlob.java
-                    // TODO: is this the right thing to do?
fb60a5baf src/org/jruby/RubyIO.java
fb60a5baf src/org/jruby/util/io/SelectBlob.java
-                    // TODO: is this the right thing to do?
+                    // TODO: is this the right thing to do?
043df6006 src/org/jruby/RubyIO.java
+                   // TODO: is this the right thing to do?
*_*_*
// FIXME moved this here to get what's obviously a utility method out of IRubyObject
47fd4acb72 Remove all safelevel checks throughout JRuby (plus misc cleanup).
0aa32bd5b6 Fixed typos in Ruby and RubyObject.
47fd4acb7 src/org/jruby/Ruby.java
-    // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
0aa32bd5b src/org/jruby/Ruby.java
+    // FIXME moved this here to get what's obviously a utility method out of IRubyObject.
*_*_*
// SSS FIXME: I added this in. Is this correct?
022d5284f8 - Bug fixes in IR generation of yield instruction + more bug fixes in inlining of closures (seems to work better now).
5d57a44aba - Added copy instruction; built the LoclAsgnNode + minor fixes in fcall and call and buildArgs methods
022d5284f src/org/jruby/compiler/ir/IR_Builder.java
-        argsList.add(build(receiver, s)); // SSS FIXME: I added this in.  Is this correct?
5d57a44ab src/org/jruby/compiler/ir/IR_Builder.java
+            // SSS FIXME: I added this in.  Is this correct?
*_*_*
// TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
1d491712e4 Start port of write logic plus fixing flush and more cleanup.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
1d491712e core/src/main/java/org/jruby/RubyIO.java
-        // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
66b024fed src/org/jruby/RubyIO.java
+        // TODO: Ruby reuses this logic for other "write" behavior by checking if it's an IO and calling write again
*_*_*
// XXX: const lookup can trigger const_missing
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
a7b4c64e64 - added get_const & put_const instructions and translated colon2 nodes
dfa3f8635f - First commit: incomplete, will not compile, and some files are just outlines
0165b69a75 Big reduction in bytecode emitted for Colon2, using Enebo's split AST nodes as a guide.
9fd65ed738 Finally some peephole optz! Propagate an 'expr' flag through compilation to know whether the product of a given subexpression is actually needed. Allows trivial removal of useless code like unused immediates, lvar, dvar, ivar, and gvar lookups. Also will allow optimizing away the array return value from an masgn, which should improve its performance substantially.
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-        // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
a7b4c64e6 src/org/jruby/compiler/ir/IR_Builder.java
-            // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
dfa3f8635 src/org/jruby/compiler/ir/IR_Builder.java
+        // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
0165b69a7 src/org/jruby/compiler/ASTCompiler.java
-        // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
9fd65ed73 src/org/jruby/compiler/ASTCompiler.java
+        // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
+        // XXX: const lookup can trigger const_missing; is that enough to warrant it always being executed?
*_*_*
// SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
ff7d060754 [IR] Fixed all output sites of PutClassVarInstr in IRBuilder to ask classVarDefinitionContainer to give it the container for the classvar.
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
fa309a4a14 - Got rid of IS_DEFINED_Instr since it was erroneous; commented out is_defined code that I had previously written; implemented multiple-assignment node by adding GET_ARRAY_Instr
3eb979fe14 - Several fixes to get this thing to start compiling ... lots more to go!
ff7d06075 src/org/jruby/compiler/ir/IRBuilder.java
-            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
-            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
8dff5b2a5 src/org/jruby/compiler/ir/IR_Builder.java
-            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
-            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
f9c3ccc4a src/org/jruby/compiler/ir/IRBuilder.java
+            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
+            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
fa309a4a1 src/org/jruby/compiler/ir/IR_Builder.java
+            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
3eb979fe1 src/org/jruby/compiler/ir/IR_Builder.java
+            // SSS FIXME: What is the difference between ClassVarAsgnNode & ClassVarDeclNode
*_*_*
// FIXME: This determine module is in a strange location and should somehow be in block
8e7062232f End of eval changes for now.  Later this will be revisted once we switch to only using StaticScope for lvars
a8c5cfe344 More old interpreter removal
a5bedbf63e More method removal/relocation out of IRubyObject. About as much as I can do this evening without making some really popular operations (conversion, instance vars) really cumbersome.
00a5649392 relanding... JRUBY-1200: commit #3984 breaks Webrick startup: active_support/dependencies.rb:266:in `load_missing_constant'
8e7062232 core/src/main/java/org/jruby/ir/interpreter/Interpreter.java
-        // FIXME:  This determine module is in a strange location and should somehow be in block
a8c5cfe34 core/src/main/java/org/jruby/evaluator/ASTInterpreter.java
a8c5cfe34 core/src/main/java/org/jruby/ir/interpreter/Interpreter.java
-        // FIXME:  This determine module is in a strange location and should somehow be in block
+        // FIXME:  This determine module is in a strange location and should somehow be in block
a5bedbf63 src/org/jruby/RubyObject.java
a5bedbf63 src/org/jruby/evaluator/ASTInterpreter.java
-        // FIXME:  This determine module is in a strange location and should somehow be in block
+        // FIXME:  This determine module is in a strange location and should somehow be in block
00a564939 src/org/jruby/RubyObject.java
+        // FIXME:  This determine module is in a strange location and should somehow be in block
*_*_*
// Hacky: Advance position to eat newline here....
fbaf038968 New lexer running plenty of stuff but missing some features like SCRIPT_LINES and with some bad line position info
3b519ff756 More heredoc support for ripper but much closer to done
3351729b98 Brutal insertion of regular lexer into ripper
e38dc21bbf Landing 'enebo' branch.  Most notable: - Merge in fixes from original refactoring patch form Mirko and friends. - Fixes many many positioning issues. - Simplify and refactor portions of lexing and parsing. - Reduce gratuitious object creation (like making a token which immediately    becomes a node). - Comments are now added to parser result as a list.  IDE people can   do a second pass to associate these nodes to the appropriate AST nodes. - offsets in ISourcePosition now represent a position 'between' characters.
fbaf03896 core/src/main/java/org/jruby/lexer/yacc/RubyLexer.java
-        // Hacky: Advance position to eat newline here....
3b519ff75 src/org/jruby/ext/ripper/RipperLexer.java
-        // Hacky: Advance position to eat newline here....
3351729b9 src/org/jruby/ext/ripper/RipperLexer.java
+        // Hacky: Advance position to eat newline here....
e38dc21bb src/org/jruby/lexer/yacc/RubyYaccLexer.java
+        // Hacky: Advance position to eat newline here....
*_*_*
// TODO need to abstract this setup behind another compiler interface
4fa52b39b1 Update to java.lang.invoke, ASM 4 (prerelease), JSR292-mock 1.6, and fix compile failure due to bytecode version.
4934ed56bf Initial support for invokedynamic-based dispatch in the compiler.
4fa52b39b src/org/jruby/compiler/impl/StandardASMCompiler.java
-            // TODO need to abstract this setup behind another compiler interface
4934ed56b src/org/jruby/compiler/impl/StandardASMCompiler.java
+            // TODO need to abstract this setup behind another compiler interface
*_*_*
// TODO: make more efficient by not creating IRubyArray[]
# shas =  1
*_*_*
// FIXME: Maybe not best place
55376a0017 Kill off argumentType from BlockBody
45658ca9dc Remove ill-fated erb generation of Block logic along with unused higher-arity call paths.
a0ea820f4b Generate the top layers of specific-arity block dispatch.
8798fdbd4b Implement Vladimir's silly Integer#times perf hack, avoiding instantiating fixnums when the block takes no args. About 25% faster for a simple .times { 1 } loop benchmark.
e4611dc96e Missed BlockBody
47f0d75fbf NodeTypes now and enum called NodeType Various warning removal Minor Block refactoring More boilerplate fixing
55376a001 core/src/main/java/org/jruby/runtime/BlockBody.java
-    // FIXME: Maybe not best place, but move it to a good home
45658ca9d src/org/jruby/runtime/BlockBody.erb
-    // FIXME: Maybe not best place, but move it to a good home
a0ea820f4 src/org/jruby/runtime/BlockBody.erb
+    // FIXME: Maybe not best place, but move it to a good home
8798fdbd4 src/org/jruby/runtime/Block.java
-    // FIXME: Maybe not best place, but move it to a good home
e4611dc96 src/org/jruby/runtime/BlockBody.java
+    // FIXME: Maybe not best place, but move it to a good home
47f0d75fb src/org/jruby/runtime/Block.java
+    // FIXME: Maybe not best place, but move it to a good home
*_*_*
// FIXME: this really ought to be in clinit, but it doesn't matter much
3849128d6a Reduce bytecode size by packing all basic cache initialization into a descriptor string that's parsed on load.
362877f032 Removing the overhead of constructing ISourcePosition objects for every line in the compiler; I moved construction to be a one-time <clinit> cost and perf numbers went back to where they were before.
3849128d6 src/org/jruby/compiler/impl/StandardASMCompiler.java
-        // FIXME: this really ought to be in clinit, but it doesn't matter much
362877f03 src/org/jruby/compiler/impl/StandardASMCompiler.java
+        // FIXME: this really ought to be in clinit, but it doesn't matter much
*_*_*
// Fixme: Do we need the check or does Main.java not call this...they should consolidate
9a784f0f9a use EMPTY_MAP instead of new (temporary) HashMap instance
4412cc7fc4 - StringIO.gets was returning nil when $/ was not matched versus rest of string - Kernel#gets would not accept a $stdin set to a StringIO
9a784f0f9 core/src/main/java/org/jruby/RubyGlobal.java
-        // Fixme: Do we need the check or does Main.java not call this...they should consolidate 
4412cc7fc src/org/jruby/RubyGlobal.java
+        // Fixme: Do we need the check or does Main.java not call this...they should consolidate 
*_*_*
// TODO: factor this chunk as in MRI/YARV GETASTER
86a1058566 Fixed #2161. Hash inspection not working as in MRI for sprintf %p.
bcd3301fcd The Bill Dortch sprintf special commit....
86a105856 core/src/main/java/org/jruby/util/Sprintf.java
                     // TODO: factor this chunk as in MRI/YARV GETASTER
-                        // TODO: factor this chunk as in MRI/YARV GETASTER
bcd3301fc src/org/jruby/util/Sprintf.java
+                    // TODO: factor this chunk as in MRI/YARV GETASTER
+                        // TODO: factor this chunk as in MRI/YARV GETASTER
*_*_*
// FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
f2f6c54152 Reinstate WrapperMethod for visibility changes. Fixes #4272.
d1589c3ece Total refactoring of zsuper argument processing, and zsuper is now enabled in the compiler. We still need more/better tests and specs for zsuper, unfortunately.
f2f6c5415 core/src/main/java/org/jruby/RubyModule.java
-                // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
d1589c3ec src/org/jruby/RubyModule.java
+                // FIXME: Why was this using a FullFunctionCallbackMethod before that did callSuper?
*_*_*
// FIXME: In order for Thread to play well with the standard 'new' behavior
# shas =  1
*_*_*
// FIXME: We should not be regenerating this over and over
85e49085c8 Fix parameter passing to procified method. JRUBY-6763.
5c0789044d JRUBY-484: Reverse Iter Call relationship in AST and also pass block via Java frame versus maintaining blocks in TC
85e49085c core/src/main/java/org/jruby/RubyMethod.java
-                // FIXME: We should not be regenerating this over and over
5c0789044 src/org/jruby/RubyMethod.java
+                // FIXME: We should not be regenerating this over and over
*_*_*
// rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
5c9cdfe905 Start porting IO open/initialize logic.
18e2a21b77 This commit generalizes the previous commit to certain array duplicate operations (Array#compact, Array#sort, Array#shuffle).
dc11f4b247 Perf improvement for Array#reverse, avoids doing a full arraycopy as well as a reversal.
bb20e69fca JRUBY-599: JRuby needs a COW, primitive array backed builtin Array JRUBY-604: Hash#sort raises exception
5c9cdfe90 core/src/main/java/org/jruby/RubyArray.java
-        // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
18e2a21b7 src/org/jruby/RubyArray.java
         // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
+        // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
dc11f4b24 src/org/jruby/RubyArray.java
+        // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
bb20e69fc src/org/jruby/RubyArray.java
+        // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
*_*_*
// FIXME: I don't like this
389b7657bd [IR] Convert some operands to instructions
f2e5e9a5ba Fix mode checking during IO.new/for_fd/open.
504738a430 Remove all references to getMainStream[Safe].
2877eda429 [IR] Implement Regexp (literal).
aa4ffa1be5 Fix botched merge that doubled new file content.
66b024fedb Merging the new IO implementation to trunk to give it appropriate burn-in time. Give it a go, friends!
3ebdff689d First phase of Joni related String/Regexp optimizations: - Major RubyRegexp and MatchData cleanup (Compiler/ASTInterpreter synchronized). - Enabled lazy Region instance creation (only when groups defined). - Heavy lifting for String#scan, String#sub, String#gsub, String#split by direct joni Matcher access and smarter backref management. - Enabled joni Matcher reusing for aforementioned methods. - Enabled thread local Regexp cache for String arguments that are converted to regexps. - Global variable ($=) made ineffective. In most cases the modified methods are now 2x - 5x faster (depending on string size)
2bb2dff23d Merge Joni as new regular expression engine
fc17861832 Move backref and lastline into Frame, since there's only ever one copy of them in a given frame. This also will make it possible to turn on stack-based variable AST inspection very soon.
47c53df4f6 Compiler reorg: break body compilation logic into abstract, method-specific, and closure-specific to allow specialized and common behaviors. Simplifies multiple compilation methods, and enabled fully compiling next/redo/break for methods and partially compiling them for blocks (when within loops).
3fa144ebbc Added three more nodes to the compiler, added a couple extra STI methods the shootout uses, fixed a bug with dstr node compilation, enabled const nodes as "safe" since they appear to be working right in the contexts I've tried.
0320b51ff9 Merging non-specific things from my sandbox to get diff smaller:
f00dfc9fcf Committing changes for JRUBY-405. I've accomplished what I set out to do, which was moving all the yield logic into Block and other appropriate classes. Other block refactoring, including adding a block argument to the call pipline, will come in other JIRAs.
389b7657b core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
-                // FIXME: I don't like this custom logic for building CompoundString bits a different way :-\
f2e5e9a5b core/src/main/java/org/jruby/util/io/ModeFlags.java
+                // FIXME: I don't like this
504738a43 core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
504738a43 core/src/main/java/org/jruby/util/io/ModeFlags.java
-            // FIXME: I don't like this
+                // FIXME: I don't like this
2877eda42 core/src/main/java/org/jruby/ir/targets/JVMVisitor.java
+                // FIXME: I don't like this custom logic for building CompoundString bits a different way :-\
aa4ffa1be src/org/jruby/util/io/ChannelDescriptor.java
-            // FIXME: I don't like this
66b024fed src/org/jruby/util/io/ChannelDescriptor.java
+            // FIXME: I don't like this
+            // FIXME: I don't like this
3ebdff689 src/org/jruby/RubyString.java
-                // FIXME: I don't like this setting into the frame directly, but it's necessary since blocks dupe the
2bb2dff23 src/org/jruby/RubyString.java
-                // FIXME: I don't like this setting into the frame directly, but it's necessary since blocks dupe the frame
                 // FIXME: I don't like this setting into the frame directly, but it's necessary since blocks dupe the
fc1786183 src/org/jruby/RubyString.java
+                // FIXME: I don't like this setting into the frame directly, but it's necessary since blocks dupe the frame
+                // FIXME: I don't like this setting into the frame directly, but it's necessary since blocks dupe the
47c53df4f src/org/jruby/compiler/impl/StandardASMCompiler.java
-            // FIXME: I don't like this pre/post state management.
-            // FIXME: I don't like this pre/post state management.
3fa144ebb src/org/jruby/compiler/impl/StandardASMCompiler.java
+        // FIXME: I don't like this pre/post state management.
+        // FIXME: I don't like this pre/post state management.
0320b51ff src/org/jruby/ast/util/ArgsUtil.java
-        // FIXME: I don't like this, but all consumers of this method do the same cast.
f00dfc9fc src/org/jruby/ast/util/ArgsUtil.java
+        // FIXME: I don't like this, but all consumers of this method do the same cast.
*_*_*
// TODO: This is probably BAD...
ed0a495c61 Remove support for and uses of deprecated Callback methods.
9251dc2018 Grr...I suppose I should have checked this before, but Mongrel still uses CallbackFactory. We need to get them off it so we can delete it some day. Damn you, crufty old dependencies!
24befe5cf0 House cleaning! Delete a bunch of unused and/or deprecated classes of strange and wonderful origins, eliminate all deprecated Callback-related code, reduce environment lookup to a simple System.getenv call, kill off the last of the old stackless interpreter's "Instruction" interface, and other miscellaneous cleanup.
08ba6cea4e Refactoring and cleanup of callback factories
4bf7730033 Dump generated classes to disk during the build process
5c0789044d JRUBY-484: Reverse Iter Call relationship in AST and also pass block via Java frame versus maintaining blocks in TC
ed0a495c6 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
-        // TODO: This is probably BAD...
9251dc201 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
+        // TODO: This is probably BAD...
24befe5cf src/org/jruby/runtime/callback/InvocationCallbackFactory.java
-        // TODO: This is probably BAD...
08ba6cea4 src/org/jruby/runtime/callback/DumpingInvocationCallbackFactory.java
-        // TODO: This is probably BAD...
4bf773003 src/org/jruby/runtime/callback/DumpingInvocationCallbackFactory.java
+        // TODO: This is probably BAD...
5c0789044 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
+        // TODO: This is probably BAD...
*_*_*
// TODO: protected methods. this is going to require a rework of some of the mechanism.
55927e21ea Revert my refactoring in favor of version based on @kares work.
be31344448 Pull all initialize logic up and out into initializer classes.
5755383a4b Begin refactoring JavaClass binding logic.
14226fded5 Extra file committed to wrong place?
ef97a30455 Bind static interface methods (Java 8) to their proxy modules.
f9b28c47d3 Reverse comparison to avoid NPE.
9bfb7b1f85 Minor edit
55927e21e core/src/main/java/org/jruby/javasupport/JavaClass.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/Initializer.java
55927e21e core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
be3134444 core/src/main/java/org/jruby/javasupport/JavaClass.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/ClassInitializer.java
be3134444 core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
-            // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
5755383a4 core/src/main/java/org/jruby/javasupport/JavaClass.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/Initializer.java
5755383a4 core/src/main/java/org/jruby/javasupport/binding/InterfaceInitializer.java
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
ef97a3045 core/src/main/java/org/jruby/javasupport/JavaClass.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
9bfb7b1f8 src/org/jruby/javasupport/JavaClass.java
+        // TODO: protected methods.  this is going to require a rework of some of the mechanism.
*_*_*
// FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
3e74923964 Deleted a bunch of dead code
57de8506bd Pass impl class into methods and clean up arg offset logic.
16b2963505 [IR] First pass implementing an explicit call protocol via IR instructions -- for now, only bindings are explicitly managed for non-closure and non-eval scopes. Frames, etc. will come later.
91799eaaaf Large refactoring of CallConfiguration and compiler logic pertaining to it:
3fc75aa4e0 Eliminate constant and class var dependency on heap-based scopes.
171b97463f Added a "frameless" compilation mode that will compile code without framing when possible. Specify -Djruby.compile.frameless=true
94809836f2 - Enabled assertions during all test runs, and made fixes and modifications as appropriate to get those assertions passing again. - Removed -da from jruby startup script to allow specifying -J-da on command line - A few optimizations to IOOutputStream/IOInputStream to use CallAdapter and lightweight strings - Cleaned up the gaggle of private RubyString constructors to four core versions, and added lightweight versions of a few public construction methods
3e7492396 core/src/main/java/org/jruby/runtime/ThreadContext.java
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
57de8506b core/src/main/java/org/jruby/runtime/ThreadContext.java
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
16b296350 src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
91799eaaa src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
3fc75aa4e src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
171b97463 src/org/jruby/runtime/ThreadContext.java
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
94809836f src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
*_*_*
// TODO: factor this chunk as in MRI/YARV GETASTER
86a1058566 Fixed #2161. Hash inspection not working as in MRI for sprintf %p.
bcd3301fcd The Bill Dortch sprintf special commit....
86a105856 core/src/main/java/org/jruby/util/Sprintf.java
                     // TODO: factor this chunk as in MRI/YARV GETASTER
-                        // TODO: factor this chunk as in MRI/YARV GETASTER
bcd3301fc src/org/jruby/util/Sprintf.java
+                    // TODO: factor this chunk as in MRI/YARV GETASTER
+                        // TODO: factor this chunk as in MRI/YARV GETASTER
*_*_*
// FIXME: what should this really be? assert x instanceof RubyComplex
# shas =  1
*_*_*
// FIXME: This isn't right for within ensured/rescued code
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
148883daa1 Split compiler-related inner classes into top-level classes, in preparation for more refactoring.
fc09ed420d Multiple additional cleanups, fixes, to the compiler; expand stack-based methods to include those with opt/rest/block args, fix a problem with redo/next in ensure/rescue; fix an issue in the ASTInspector not inspecting opt arg values; shrink the generated bytecode by offloading to CompilerHelpers in a few places. Ruby stdlib now compiles completely. Yay!
ed54aab18 core/src/main/java/org/jruby/compiler/impl/ChildScopedBodyCompiler.java
-        // FIXME: This isn't right for within ensured/rescued code
148883daa src/org/jruby/compiler/impl/ASMClosureCompiler.java
148883daa src/org/jruby/compiler/impl/StandardASMCompiler.java
+        // FIXME: This isn't right for within ensured/rescued code
-            // FIXME: This isn't right for within ensured/rescued code
fc09ed420 src/org/jruby/compiler/impl/StandardASMCompiler.java
+            // FIXME: This isn't right for within ensured/rescued code
*_*_*
// SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
4bc398477a [IR] Code cleanup.
8cdcdb64ae Missing one
5a08b5476f Trivial IR changes littering my tree
b802028b86 - [ Hmm .. git newbie here .. somehow lost the commit .. trying again ] Fixed couple bugs with scope canonicalization; implemented ir output as a compiler pass; added code-version-token for IR_Method and added several method stubs to start optimizing calls
4bc398477 src/org/jruby/compiler/ir/IRMethod.java
-    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
8cdcdb64a src/org/jruby/compiler/ir/IR_Method.java
-    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
5a08b5476 src/org/jruby/compiler/ir/IRMethod.java
+    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
b802028b8 src/org/jruby/compiler/ir/IR_Method.java
+    // SSS FIXME: Token can be final for a method -- implying that the token is only for this particular implementation of the method
*_*_*
// TODO: It would be nice to throw a better error for this
6884f63c4f Restore minimal part of old IO for jossl 0.9.5 to work.
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
44444a4f0a Make reads/writes on a server socket not blow up horribly.
6884f63c4 core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
+        // TODO: It would be nice to throw a better error for this
+        // TODO: It would be nice to throw a better error for this
a6f20b63a core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
-        // TODO: It would be nice to throw a better error for this
-        // TODO: It would be nice to throw a better error for this
44444a4f0 src/org/jruby/util/io/ChannelDescriptor.java
+        // TODO: It would be nice to throw a better error for this
+        // TODO: It would be nice to throw a better error for this
*_*_*
// TODO: make this an array so it's not as much class metadata
d9656d2b20 Move all caching logic out of "real class" and use RuntimeCache (now top-level) for method caching.
02b8e01b46 Remove MiniJava and all the tendrils it extended through JRuby. Useful remainder moved to org.jruby.java.codegen.RealClassGenerator.
07e838b135 First commit of new JI interface-impl code that actually generates a real Java class to use for the Ruby objects. Does not support concrete Java superclasses yet, which still goes through the old style logic.
ba40f9ccdc Fix for JRUBY-3158: Wrong ruby methods called on object of same class from Java code.
d9656d2b2 src/org/jruby/java/codegen/RealClassGenerator.java
-            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
-            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
02b8e01b4 src/org/jruby/java/MiniJava.java
02b8e01b4 src/org/jruby/java/codegen/RealClassGenerator.java
-            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
-            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
+            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
+            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
07e838b13 src/org/jruby/java/MiniJava.java
+            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
ba40f9ccd src/org/jruby/java/MiniJava.java
+            // TODO: make this an array so it's not as much class metadata; similar to AbstractScript stuff
*_*_*
// TODO: It would be nice to throw a better error for this
6884f63c4f Restore minimal part of old IO for jossl 0.9.5 to work.
a6f20b63a5 Eliminate remnants of old IO backend and localize fileno map.
44444a4f0a Make reads/writes on a server socket not blow up horribly.
6884f63c4 core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
+        // TODO: It would be nice to throw a better error for this
+        // TODO: It would be nice to throw a better error for this
a6f20b63a core/src/main/java/org/jruby/util/io/ChannelDescriptor.java
-        // TODO: It would be nice to throw a better error for this
-        // TODO: It would be nice to throw a better error for this
44444a4f0 src/org/jruby/util/io/ChannelDescriptor.java
+        // TODO: It would be nice to throw a better error for this
+        // TODO: It would be nice to throw a better error for this
*_*_*
// TODO: newTypeError does not offer enough for ruby error string...
6eb8df5bd5 Fix JRUBY-4871: [1.9] Attempt to invoke any method on Delegator leads to ClassCastException
a679fc6a62 Add Module.extended Type check without crash on Object.type_of? Type check Object.extend arguments
6eb8df5bd src/org/jruby/RubyBasicObject.java
6eb8df5bd src/org/jruby/RubyObject.java
+            // TODO: newTypeError does not offer enough for ruby error string...
-            // TODO: newTypeError does not offer enough for ruby error string...
a679fc6a6 src/org/jruby/RubyObject.java
+            // TODO: newTypeError does not offer enough for ruby error string...
*_*_*
// SSS FIXME: I added this in. Is this correct?
022d5284f8 - Bug fixes in IR generation of yield instruction + more bug fixes in inlining of closures (seems to work better now).
5d57a44aba - Added copy instruction; built the LoclAsgnNode + minor fixes in fcall and call and buildArgs methods
022d5284f src/org/jruby/compiler/ir/IR_Builder.java
-        argsList.add(build(receiver, s)); // SSS FIXME: I added this in.  Is this correct?
5d57a44ab src/org/jruby/compiler/ir/IR_Builder.java
+            // SSS FIXME: I added this in.  Is this correct?
*_*_*
// TODO: top-level upper-case package was supported in the previous (Ruby-based)
# shas =  1
*_*_*
// TODO: wire into new exception handling mechanism
905afa0b20 Make setIRScope and setScopeType simpler.  This change makes me wonder how safe we are since not all constructed IRScope paths setIRScope.  In the case of evals it appears we only set IRScopeType.
7621334c9b Start removing old interpreter
efd8a3df54 More decoupling of constant lookup from ThreadContext.
27438e0e61 [interp] Unboxed attrassign node (2-3x speedup) assign is now based on same mechanism as interpret FCallOneArgNode has slower ASTInterpreter.getBlock instead of this.getBlock converted consumers of ASTInterpreter.eval to Node.interpret converted all consumers of ASTAssignment.assign to Node.assign
3fa144ebbc Added three more nodes to the compiler, added a couple extra STI methods the shootout uses, fixed a bug with dstr node compilation, enabled const nodes as "safe" since they appear to be working right in the contexts I've tried.
972877610a JRUBY-202: ::A = 1 should work
6e15491217 merging new interpreter plus a few minor fixes to trunk. JRUBY-185
f5643d27aa merging cnutter_work2 to HEAD...damn the torpedos! full speed ahead!
905afa0b2 core/src/main/java/org/jruby/parser/StaticScope.java
-        // TODO: wire into new exception handling mechanism
7621334c9 core/src/main/java/org/jruby/ast/ConstDeclNode.java
-                // TODO: wire into new exception handling mechanism
efd8a3df5 src/org/jruby/parser/StaticScope.java
efd8a3df5 src/org/jruby/runtime/ThreadContext.java
                 // TODO: wire into new exception handling mechanism
+        // TODO: wire into new exception handling mechanism
-        // TODO: wire into new exception handling mechanism
27438e0e6 src/org/jruby/ast/ConstDeclNode.java
27438e0e6 src/org/jruby/evaluator/AssignmentVisitor.java
+                // TODO: wire into new exception handling mechanism
-                // TODO: wire into new exception handling mechanism
3fa144ebb src/org/jruby/evaluator/EvaluationState.java
3fa144ebb src/org/jruby/runtime/ThreadContext.java
-                // TODO: wire into new exception handling mechanism
+            // TODO: wire into new exception handling mechanism
972877610 src/org/jruby/evaluator/AssignmentVisitor.java
+                    // TODO: wire into new exception handling mechanism
                         // TODO: wire into new exception handling mechanism
6e1549121 src/org/jruby/evaluator/EvaluateVisitor.java
6e1549121 src/org/jruby/evaluator/EvaluationState.java
-                    // TODO: wire into new exception handling mechanism
+                        // TODO: wire into new exception handling mechanism
f5643d27a src/org/jruby/evaluator/EvaluateVisitor.java
+            	// TODO: wire into new exception handling mechanism
*_*_*
// TODO: This is also defined in the MetaClass too...Consolidate somewhere.
d055a0ea0c More encoding-related IO cleanup.
0a56a82804 IO and File now have full fledged meta classes Many methods in IO and File now do conversions of arguments to the types they expect
d055a0ea0 src/org/jruby/RubyFile.java
-    // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
0a56a8280 src/org/jruby/RubyFile.java
+    // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
*_*_*
// FIXME: ConstDecl could be two seperate classes (or done differently since constNode and name
7621334c9b Start removing old interpreter
df1ae61d89 A couple more callAdapter additions to nodes that can support them Remove annoying generics warnings that IDEs like to give for un typed collections Change boilerplate in ast so that netbeans can have them closed by default (and javadocs will not contain them) Remove last vestiges of serialization from AST MethodIndex moved into DefaultAdapter Smaller smattering and things I cannot remember
7621334c9 core/src/main/java/org/jruby/ast/ConstDeclNode.java
-// FIXME: ConstDecl could be two seperate classes (or done differently since constNode and name
df1ae61d8 src/org/jruby/ast/ConstDeclNode.java
+// FIXME: ConstDecl could be two seperate classes (or done differently since constNode and name
*_*_*
// TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
89cedd5a04 minor JavaClass house-keeping
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
89cedd5a0 core/src/main/java/org/jruby/javasupport/JavaClass.java
-        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
b390103c2 src/org/jruby/javasupport/JavaClass.java
b390103c2 src/org/jruby/javasupport/JavaConstructor.java
b390103c2 src/org/jruby/javasupport/JavaField.java
b390103c2 src/org/jruby/javasupport/JavaMethod.java
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
+        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here, since we don't intend for people to monkey with
*_*_*
FIXME: finalizer should be dupped here
fd0fa789b2 Merge "backtrace" branch to master, now that it runs clean on all our various test cases.
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
fa021e6350 JRUBY-488: Kernel#dup, Kernel#clone and initialize_copy should obey MRI protocol
fd0fa789b src/org/jruby/RubyObject.java
-        /* FIXME: finalizer should be dupped here */
bf8db0bc4 src/org/jruby/RubyBasicObject.java
+        /* FIXME: finalizer should be dupped here */
fa021e635 src/org/jruby/RubyObject.java
+        /* FIXME: finalizer should be dupped here */
*_*_*
// TODO: number formatting here
# shas =  1
*_*_*
// TODO: need to get this back into the method signature...now is too late...
80603cf52e More work on bootstrapping JVM emitter for IR compiler.
aa0f8552ad Add RECV_ARG support and variable allocation/loading.
80603cf52 src/org/jruby/compiler/ir/targets/JVM.java
-        // TODO: need to get this back into the method signature...now is too late...
aa0f8552a src/org/jruby/compiler/ir/targets/JVM.java
+        // TODO: need to get this back into the method signature...now is too late...
*_*_*
// FIXME: possible to make handles do the superclass call?
# shas =  1
*_*_*
// optional args, so we have IRubyObject[]
cb8f537d74 Add an offline indy-based invoker generator.
e084deabce Add a separate type of JavaMethodDescriptor for compile time.
2f7cc79224 Get the annotation-based binding generator working again with multimethods and optional 'required' attrs.
a0ed4fd9d0 Abstract the idea of a "descriptor" given a method and its annotation into a separate structure we can pass around and store.
cb8f537d7 core/src/main/java/org/jruby/anno/IndyBinder.java
e084deabc core/src/main/java/org/jruby/anno/JavaMethodDescriptor.java
e084deabc core/src/main/java/org/jruby/anno/MethodDescriptor.java
2f7cc7922 src/org/jruby/anno/AnnotationBinder.java
a0ed4fd9d src/org/jruby/anno/JavaMethodDescriptor.java
*_*_*
// TODO: better error handling
78f196d805 Working through more logic... sysopen, seek, reopen, init_copy.
892787ae3c Based on some studying of C source for IO#initialize_copy and IO#reopen (for an fd) this commit tries to modify our logic to more closely map to Ruby's. Major missing pieces include: - Ruby's separate handling of an OpenFile struct, FILE* struct, and file descriptor, which map roughly to RubyIO.OpenFile (new in this commit), IOHandler, and Channel - Flushing logic for incoming and current streams - Construction of new IOHandlers to wrap existing channels What's largely missing from all this logic, and what would fill in the remaining pieces, would be a way for us to clone Channels in the same way Ruby can dup or dup2 file descriptors. This missing piece means we can't easily avoid closing channels still in use and can't manage channel position in two separate places.
78f196d80 core/src/main/java/org/jruby/RubyIO.java
-        } catch (IOException ex) { // TODO: better error handling
892787ae3 src/org/jruby/RubyIO.java
+            } catch (IOException ex) { // TODO: better error handling
*_*_*
// FIXME: not very efficient
367c2bcd7d - DynamicScope+children removed no longer used {get,set}ArgValues methods since they are dead. - ArgsNode getRest() replaced by hasRest() or retrieving getRestArgNode directly. - Slight rearrangement internally of fields in ArgsNode. - Remove accidental println from last commit.
33b1f99f1c Use a shared binding dynamic scope to take advantage of frame ir instructions (should actually be called dynamic scope instrs or some such thing)
34c5b29cd1 Add four-var scope and enable it globally.
aafd3613a2 Add three-var scope, not enabled yet.
d237a30fee Added no-var and two-var versions of DynamicScope.
ec4f90b7e0 Based on discoveries by Marcin, confirmed by Ola, adding capability for separate DynamicScope implementations that use fields instead of an array for scopes with small numbers of variables. This primarily improves compiled performance, since the compiler can statically dispatch to methods that amount to little more than field access. I will add additional DynamicScope impls for 0, 2, and 3 variable scopes.
d1589c3ece Total refactoring of zsuper argument processing, and zsuper is now enabled in the compiler. We still need more/better tests and specs for zsuper, unfortunately.
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/FourVarDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/ManyVarsDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/OneVarDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/SharedBindingDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/ThreeVarDynamicScope.java
367c2bcd7 core/src/main/java/org/jruby/runtime/scope/TwoVarDynamicScope.java
-            // FIXME: not very efficient
-            // FIXME: not very efficient
-            // FIXME: not very efficient
-            // FIXME: not very efficient
-            // FIXME: not very efficient
-            // FIXME: not very efficient
33b1f99f1 src/org/jruby/runtime/scope/SharedBindingDynamicScope.java
+            // FIXME: not very efficient
34c5b29cd src/org/jruby/runtime/scope/FourVarDynamicScope.java
+            // FIXME: not very efficient
aafd3613a src/org/jruby/runtime/scope/ThreeVarDynamicScope.java
+            // FIXME: not very efficient
d237a30fe src/org/jruby/runtime/ManyVarsDynamicScope.java
d237a30fe src/org/jruby/runtime/OneVarDynamicScope.java
d237a30fe src/org/jruby/runtime/scope/ManyVarsDynamicScope.java
d237a30fe src/org/jruby/runtime/scope/OneVarDynamicScope.java
d237a30fe src/org/jruby/runtime/scope/TwoVarDynamicScope.java
+            // FIXME: not very efficient
ec4f90b7e src/org/jruby/runtime/DynamicScope.java
ec4f90b7e src/org/jruby/runtime/ManyVarsDynamicScope.java
ec4f90b7e src/org/jruby/runtime/OneVarDynamicScope.java
-            // FIXME: not very efficient
+            // FIXME: not very efficient
+            // FIXME: not very efficient
d1589c3ec src/org/jruby/runtime/DynamicScope.java
+            // FIXME: not very efficient
*_*_*
// SSS FIXME: Should this be Operand or CompoundString?
389b7657bd [IR] Convert some operands to instructions
34158207b4 [IR] Clean up operands
6a3bf2b586 - Some more nodes translated + added isConstant method to BacktickString
86e7fc9281 - Cleaned up DefnNode, and translated DSymbolNode
389b7657b core/src/main/java/org/jruby/ir/operands/DynamicSymbol.java
-    // SSS FIXME: Should this be Operand or CompoundString?
34158207b src/org/jruby/compiler/ir/operands/DynamicReference.java
34158207b src/org/jruby/compiler/ir/operands/DynamicSymbol.java
-        // SSS FIXME: Should this be Operand or CompoundString?
+    // SSS FIXME: Should this be Operand or CompoundString?
6a3bf2b58 src/org/jruby/compiler/ir/DynamicReference.java
6a3bf2b58 src/org/jruby/compiler/ir/DynamicSymbol.java
+        // SSS FIXME: Should this be Operand or CompoundString?
-      // SSS FIXME: Should this be Operand or CompoundString?
86e7fc928 src/org/jruby/compiler/ir/DynamicSymbol.java
+      // SSS FIXME: Should this be Operand or CompoundString?
*_*_*
// FIXME: We should really not be creating the dynamic scope for the root
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
fe07751fb2 YARV updates (big machine-based frame; refactorings)
b317014af9 JRUBY-877: Sources not available during command-line debugging
174e3d0146 damn the torpedos! full speed ahead! landing enebo_lexical branch changes plus TC-passing optimizations and a few small compiler updates
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/ParserConfiguration.java
-        // FIXME: We should really not be creating the dynamic scope for the root
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/ParserConfiguration.java
+        // FIXME: We should really not be creating the dynamic scope for the root
fe07751fb src/org/jruby/parser/RubyParserConfiguration.java
-        // FIXME: We should really not be creating the dynamic scope for the root
b317014af src/org/jruby/parser/ParserConfiguration.java
+        // FIXME: We should really not be creating the dynamic scope for the root
174e3d014 src/org/jruby/parser/RubyParserConfiguration.java
+        // FIXME: We should really not be creating the dynamic scope for the root
*_*_*
// TODO: wire into new exception handling mechanism
905afa0b20 Make setIRScope and setScopeType simpler.  This change makes me wonder how safe we are since not all constructed IRScope paths setIRScope.  In the case of evals it appears we only set IRScopeType.
7621334c9b Start removing old interpreter
efd8a3df54 More decoupling of constant lookup from ThreadContext.
27438e0e61 [interp] Unboxed attrassign node (2-3x speedup) assign is now based on same mechanism as interpret FCallOneArgNode has slower ASTInterpreter.getBlock instead of this.getBlock converted consumers of ASTInterpreter.eval to Node.interpret converted all consumers of ASTAssignment.assign to Node.assign
3fa144ebbc Added three more nodes to the compiler, added a couple extra STI methods the shootout uses, fixed a bug with dstr node compilation, enabled const nodes as "safe" since they appear to be working right in the contexts I've tried.
972877610a JRUBY-202: ::A = 1 should work
6e15491217 merging new interpreter plus a few minor fixes to trunk. JRUBY-185
f5643d27aa merging cnutter_work2 to HEAD...damn the torpedos! full speed ahead!
905afa0b2 core/src/main/java/org/jruby/parser/StaticScope.java
-        // TODO: wire into new exception handling mechanism
7621334c9 core/src/main/java/org/jruby/ast/ConstDeclNode.java
-                // TODO: wire into new exception handling mechanism
efd8a3df5 src/org/jruby/parser/StaticScope.java
efd8a3df5 src/org/jruby/runtime/ThreadContext.java
                 // TODO: wire into new exception handling mechanism
+        // TODO: wire into new exception handling mechanism
-        // TODO: wire into new exception handling mechanism
27438e0e6 src/org/jruby/ast/ConstDeclNode.java
27438e0e6 src/org/jruby/evaluator/AssignmentVisitor.java
+                // TODO: wire into new exception handling mechanism
-                // TODO: wire into new exception handling mechanism
3fa144ebb src/org/jruby/evaluator/EvaluationState.java
3fa144ebb src/org/jruby/runtime/ThreadContext.java
-                // TODO: wire into new exception handling mechanism
+            // TODO: wire into new exception handling mechanism
972877610 src/org/jruby/evaluator/AssignmentVisitor.java
+                    // TODO: wire into new exception handling mechanism
                         // TODO: wire into new exception handling mechanism
6e1549121 src/org/jruby/evaluator/EvaluateVisitor.java
6e1549121 src/org/jruby/evaluator/EvaluationState.java
-                    // TODO: wire into new exception handling mechanism
+                        // TODO: wire into new exception handling mechanism
f5643d27a src/org/jruby/evaluator/EvaluateVisitor.java
+            	// TODO: wire into new exception handling mechanism
*_*_*
// TODO: not sure that we should skip calling join() altogether
# shas =  1
*_*_*
// hack to get right style for input
b2a2f49f1e remove readline and truffle from ext/
d38821551a Fix for JRUBY-336 and JRUBY-337: add support for applets by catching (and ignoring) access violation exceptions, and adds applet and standalone consoles
b2a2f49f1 ext/readline/src/main/java/org/jruby/demo/readline/TextAreaReadline.java
-               append(" ", inputStyle); // hack to get right style for input
d38821551 src/org/jruby/demo/TextAreaReadline.java
+                append(" ", inputStyle); // hack to get right style for input
*_*_*
// If variables were added then we may need to grow the dynamic scope to match the static
8b6eec17dd Remove TruffleRuby.
27da7b11f5 [Truffle] Parser was a pretty shallow abstraction - remove it.
fa938c4c71 [Truffle] Fork the JRuby parser.
f197e8b87f Remove commented out code (you're dead to me)
16e5550655 Lexer jumbo patch.  Speeds up general parsing 10-15%.  Cold parses are about 30% faster.   Code has been refactored to the point that additional optimizations can be considered (like bytelist identifiers for alloc-less identifiers; same for uninterpolated string nodes).
174e3d0146 damn the torpedos! full speed ahead! landing enebo_lexical branch changes plus TC-passing optimizations and a few small compiler updates
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/TranslatorDriver.java
-        // If variables were added then we may need to grow the dynamic scope to match the static
27da7b11f truffle/src/main/java/org/jruby/truffle/parser/TranslatorDriver.java
27da7b11f truffle/src/main/java/org/jruby/truffle/parser/parser/Parser.java
+        // If variables were added then we may need to grow the dynamic scope to match the static
-        // If variables were added then we may need to grow the dynamic scope to match the static
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/Parser.java
+        // If variables were added then we may need to grow the dynamic scope to match the static
f197e8b87 src/org/jruby/parser/Parser.java
-        // If variables were added then we may need to grow the dynamic scope to match the static
16e555065 src/org/jruby/parser/Parser.java
+        // If variables were added then we may need to grow the dynamic scope to match the static
174e3d014 src/org/jruby/parser/Parser.java
+        // If variables were added then we may need to grow the dynamic scope to match the static
*_*_*
// SSS FIXME: This method (at least in the context of multiple assignment) is a little weird
ffae988526 [IR] Some minor code cleanup
5fade4a7c1 More fixup of JRubyImplCallInstr
fa309a4a14 - Got rid of IS_DEFINED_Instr since it was erroneous; commented out is_defined code that I had previously written; implemented multiple-assignment node by adding GET_ARRAY_Instr
ffae98852 src/org/jruby/compiler/ir/instructions/JRubyImplCallInstr.java
-       // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
5fade4a7c src/org/jruby/compiler/ir/instructions/JRubyImplCallInstr.java
5fade4a7c src/org/jruby/compiler/ir/operands/MethAddr.java
+       // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
-    // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
fa309a4a1 src/org/jruby/compiler/ir/MethAddr.java
+    // SSS FIXME: This method (at least in the context of multiple assignment) is a little weird.
*_*_*
// FIXME: potentially could just use ByteList here?
# shas =  1
*_*_*
// FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
3e74923964 Deleted a bunch of dead code
57de8506bd Pass impl class into methods and clean up arg offset logic.
16b2963505 [IR] First pass implementing an explicit call protocol via IR instructions -- for now, only bindings are explicitly managed for non-closure and non-eval scopes. Frames, etc. will come later.
91799eaaaf Large refactoring of CallConfiguration and compiler logic pertaining to it:
3fc75aa4e0 Eliminate constant and class var dependency on heap-based scopes.
171b97463f Added a "frameless" compilation mode that will compile code without framing when possible. Specify -Djruby.compile.frameless=true
94809836f2 - Enabled assertions during all test runs, and made fixes and modifications as appropriate to get those assertions passing again. - Removed -da from jruby startup script to allow specifying -J-da on command line - A few optimizations to IOOutputStream/IOInputStream to use CallAdapter and lightweight strings - Cleaned up the gaggle of private RubyString constructors to four core versions, and added lightweight versions of a few public construction methods
3e7492396 core/src/main/java/org/jruby/runtime/ThreadContext.java
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
57de8506b core/src/main/java/org/jruby/runtime/ThreadContext.java
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
-        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
16b296350 src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
91799eaaa src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
3fc75aa4e src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
171b97463 src/org/jruby/runtime/ThreadContext.java
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
94809836f src/org/jruby/runtime/ThreadContext.java
+        // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
*_*_*
FIXME: Consider fixing node_assign itself rather than single case
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
5a0b41a2fb Updated 2.1 grammar with some non-working bits (like imaginary and rational literals)
bdd0b37b43 Minimum to remove 1.9 parser
db61864d6a minimal work to remove 1.8 parser
5397087cdf Whoops accidentally picked up emacs open file backup
7c6444fad4 Last MRI19 test regression fixed
b6d12c34d5 Moar production meat fixed
d5713e9388 Broken but saving ripper progress
5fae3aa735 Yay...generated 20 compiler will compile now
f00edcd9d5 First massive edit session complete
48e71445b6 Remove abortive Ruby Antlr grammar...
7a9828c859 Remove never used antlr skeleton of jruby parser
bf8db0bc4a Biggest commit eva (1.9 parser+1.9-runtime-related-changes landing)
0e33acfafe Saving some scratch-pad ANTLR work for later, hopefully
e9bf476d31 Add some assignment position fixes (From Mirko Stocker) literal_concat changes:  - Multiple StrNodes coallesce  - Extra DStrNodes are not getting created
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
-                    // FIXME: Consider fixing node_assign itself rather than single case
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    // FIXME: Consider fixing node_assign itself rather than single case
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.java
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.y
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.java
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.y
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.java
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.y
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
-                    // FIXME: Consider fixing node_assign itself rather than single case
db61864d6 core/src/main/java/org/jruby/parser/DefaultRubyParser.java
db61864d6 core/src/main/java/org/jruby/parser/DefaultRubyParser.y
-		  /* FIXME: Consider fixing node_assign itself rather than single case*/
-		  // FIXME: Consider fixing node_assign itself rather than single case
5397087cd src/org/jruby/parser/#Ruby19Parser.y#
-                    // FIXME: Consider fixing node_assign itself rather than single case
7c6444fad src/org/jruby/parser/#Ruby19Parser.y#
+                    // FIXME: Consider fixing node_assign itself rather than single case
b6d12c34d src/org/jruby/parser/Ripper19Parser.java
b6d12c34d src/org/jruby/parser/Ripper19Parser.y
-                    /* FIXME: Consider fixing node_assign itself rather than single case*/
-                    // FIXME: Consider fixing node_assign itself rather than single case
d5713e938 src/org/jruby/parser/Ripper19Parser.java
d5713e938 src/org/jruby/parser/Ripper19Parser.y
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    // FIXME: Consider fixing node_assign itself rather than single case
5fae3aa73 src/org/jruby/parser/Ruby20Parser.java
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
f00edcd9d src/org/jruby/parser/Ruby20Parser.y
+                    // FIXME: Consider fixing node_assign itself rather than single case
48e71445b src/org/jruby/parser/DefaultRubyParser.initial.g
-                  // FIXME: Consider fixing node_assign itself rather than single case
7a9828c85 src/org/jruby/parser/DefaultRubyParser.g
-                  // FIXME: Consider fixing node_assign itself rather than single case
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.java
bf8db0bc4 src/org/jruby/parser/Ruby19Parser.y
 		  /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    /* FIXME: Consider fixing node_assign itself rather than single case*/
+                    // FIXME: Consider fixing node_assign itself rather than single case
0e33acfaf src/org/jruby/parser/DefaultRubyParser.g
0e33acfaf src/org/jruby/parser/DefaultRubyParser.initial.g
+                  // FIXME: Consider fixing node_assign itself rather than single case
+                  // FIXME: Consider fixing node_assign itself rather than single case
e9bf476d3 src/org/jruby/parser/DefaultRubyParser.java
e9bf476d3 src/org/jruby/parser/DefaultRubyParser.y
+		    /* FIXME: Consider fixing node_assign itself rather than single case*/
+		    // FIXME: Consider fixing node_assign itself rather than single case
*_*_*
// This is perhaps innefficient timewise? Optimal spacewise
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
f7831f79f1 [1.9] Make sure block parms do not capture from enclosing scope but make new local
174e3d0146 damn the torpedos! full speed ahead! landing enebo_lexical branch changes plus TC-passing optimizations and a few small compiler updates
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/scope/StaticScope.java
-        // This is perhaps innefficient timewise?  Optimal spacewise
-        // This is perhaps innefficient timewise?  Optimal spacewise
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/scope/StaticScope.java
+        // This is perhaps innefficient timewise?  Optimal spacewise
+        // This is perhaps innefficient timewise?  Optimal spacewise
f7831f79f src/org/jruby/parser/StaticScope.java
+        // This is perhaps innefficient timewise?  Optimal spacewise
174e3d014 src/org/jruby/parser/StaticScope.java
+        // This is perhaps innefficient timewise?  Optimal spacewise
*_*_*
// end hack
da0b65ceb3 Fix remaining BasicObject specs and probably remove some obsolete tag files
8da55f45fc Last round of changes for JRUBY-1386, improve instance_eval. Added cases to the benchmark too.
f16e305fcf Not-so-pretty fix for JRUBY-1381: define_method with an instance_eval'ed block from the surrounding scope doesn't get the right self.
da0b65ceb src/org/jruby/RubyObject.java
-            // end hack
-            // end hack
8da55f45f src/org/jruby/RubyObject.java
+            // end hack
f16e305fc src/org/jruby/RubyObject.java
+                    // end hack
*_*_*
// FIXME: Is this ok?
0d4f883468 Remove rarely used and never quite right thread-pool mode.
2e04166f04 Multiple fixes for threadpool-based execution, and enable threadpool tests in test-all.
0d4f88346 core/src/main/java/org/jruby/internal/runtime/FutureThread.java
-                // FIXME: Is this ok?
2e04166f0 src/org/jruby/internal/runtime/FutureThread.java
+                // FIXME: Is this ok?
*_*_*
// TODO: proper algorithm to set the precision
b3342e7752 fix GH-2524 on jruby-1_7: improved algorithm to set the precision
8fec7f43fd fix GH-2524 on master: improved algorithm to set the precision
9ffe8821d5 De-version BigDecimal
693b9ef25a JRUBY-6556: BigDecimal divided by Rational gives nil in --1.9 mode
2f7d092e20 Fixed numerous rubyspec failures for BigDecimal's #quo #div and #/.
b3342e775 core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-        // TODO: proper algorithm to set the precision
8fec7f43f core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-        // TODO: proper algorithm to set the precision
9ffe8821d core/src/main/java/org/jruby/ext/bigdecimal/RubyBigDecimal.java
-        // TODO: proper algorithm to set the precision
693b9ef25 src/org/jruby/ext/bigdecimal/RubyBigDecimal.java
         // TODO: proper algorithm to set the precision
+        // TODO: proper algorithm to set the precision
2f7d092e2 src/org/jruby/RubyBigDecimal.java
+        // TODO: proper algorithm to set the precision
*_*_*
// TODO: exec should replace the current process
0bf233162c Native exec turned on (only windows currently supported in jnr-posix). Fix some permissions
8548cc7b0a Fix for JRUBY-1873 from Vladimir.
0bf233162 src/org/jruby/RubyKernel.java
-            // TODO: exec should replace the current process.
8548cc7b0 src/org/jruby/RubyKernel.java
+            // TODO: exec should replace the current process.
*_*_*
// Sometimes the value can be retrieved at "compile time". If we succeed, nothing like it!
d8c290d988 Moved modules, classes, constants from IRScopeImpl to IRModule to move away from recording lexical information to recording semantic information.  Moved away from 'compile-time' resolution of Ruby constants because Module.remove_const and set_const makes such resolutions potentially incorrect.
8dff5b2a57 Damn...I keep removing this on a merge?
f9c3ccc4ac Simple renaming in ir package
a7b4c64e64 - added get_const & put_const instructions and translated colon2 nodes
86df31ace2 - Renamed RUBY_IMPL_CALL_Instr to RUBY_INTERNALS_CALL_Instr .. translated a few more nodes and added more fixmes!
d8c290d98 src/org/jruby/compiler/ir/IRScopeImpl.java
-    // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!
8dff5b2a5 src/org/jruby/compiler/ir/IR_ScopeImpl.java
-    // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!
f9c3ccc4a src/org/jruby/compiler/ir/IRScopeImpl.java
+    // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!
a7b4c64e6 src/org/jruby/compiler/ir/IR_Builder.java
a7b4c64e6 src/org/jruby/compiler/ir/IR_ScopeImpl.java
-            // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!  
+        // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!  
86df31ace src/org/jruby/compiler/ir/IR_Builder.java
+            // Sometimes the value can be retrieved at "compile time".  If we succeed, nothing like it!  
*_*_*
// FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
9902ebd3bf a slight Java integration (mostly bootstrap internals) cleanup
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
9902ebd3b core/src/main/java/org/jruby/javasupport/JavaArray.java
-        // FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
b390103c2 src/org/jruby/javasupport/JavaArray.java
+        // FIXME: NOT_ALLOCATABLE_ALLOCATOR is probably not right here, since we might
*_*_*
Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
8b6eec17dd Remove TruffleRuby.
fa938c4c71 [Truffle] Fork the JRuby parser.
5a0b41a2fb Updated 2.1 grammar with some non-working bits (like imaginary and rational literals)
bdd0b37b43 Minimum to remove 1.9 parser
db61864d6a minimal work to remove 1.8 parser
5397087cdf Whoops accidentally picked up emacs open file backup
7c6444fad4 Last MRI19 test regression fixed
c586e919b2 Much closer to being done with grammar productions before debugging them more
7a01985b84 Fixed generate issue and rearranged tokens
464c04b3b2 Something is not generating right anymore...syncing
d5713e9388 Broken but saving ripper progress
5fae3aa735 Yay...generated 20 compiler will compile now
f00edcd9d5 First massive edit session complete
ae2975a6ec JRUBY-2326: Invalid cast during parsing of recursive.rb in facelets-2.3.0 (org.jruby.ast.YieldNode cannot be cast to org.jruby.ast.BlockAcceptingNode)
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
8b6eec17d truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.java
fa938c4c7 truffle/src/main/java/org/jruby/truffle/parser/parser/RubyParser.y
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.java
5a0b41a2f core/src/main/java/org/jruby/parser/Ruby20Parser.y
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.java
5a0b41a2f core/src/main/java/org/jruby/parser/RubyParser.y
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.java
bdd0b37b4 core/src/main/java/org/jruby/parser/Ruby19Parser.y
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
db61864d6 core/src/main/java/org/jruby/parser/DefaultRubyParser.java
db61864d6 core/src/main/java/org/jruby/parser/DefaultRubyParser.y
-                  /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                  // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
5397087cd src/org/jruby/parser/#Ruby19Parser.y#
-                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
7c6444fad src/org/jruby/parser/#Ruby19Parser.y#
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
c586e919b src/org/jruby/parser/Ripper19Parser.java
c586e919b src/org/jruby/parser/Ripper19Parser.y
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
-                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
7a01985b8 src/org/jruby/parser/Ripper19Parser.java
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
464c04b3b src/org/jruby/parser/Ripper19Parser.java
-                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
d5713e938 src/org/jruby/parser/Ripper19Parser.java
d5713e938 src/org/jruby/parser/Ripper19Parser.y
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
5fae3aa73 src/org/jruby/parser/Ruby20Parser.java
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
f00edcd9d src/org/jruby/parser/Ruby20Parser.y
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
ae2975a6e src/org/jruby/parser/DefaultRubyParser.java
ae2975a6e src/org/jruby/parser/DefaultRubyParser.y
ae2975a6e src/org/jruby/parser/Ruby19Parser.java
ae2975a6e src/org/jruby/parser/Ruby19Parser.y
+                  /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                  // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
+                    /* Workaround for JRUBY-2326 (MRI does not enter this production for some reason)*/
+                    // Workaround for JRUBY-2326 (MRI does not enter this production for some reason)
*_*_*
// if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
ed54aab184 Rip out the guts! Removing many non-9k runtime classes.
52d2b6c7bc - More nodes translated; some nodes fixed up; added Regexp
86e7fc9281 - Cleaned up DefnNode, and translated DSymbolNode
dfa3f8635f - First commit: incomplete, will not compile, and some files are just outlines
57f9436a66 Get whole-body rescues to optimize the same way a non-rescued method body would.
ed54aab18 core/src/main/java/org/jruby/compiler/ASTCompiler.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
52d2b6c7b src/org/jruby/compiler/ir/IR_Builder.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
86e7fc928 src/org/jruby/compiler/ir/IR_Builder.java
-        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
dfa3f8635 src/org/jruby/compiler/ir/IR_Builder.java
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
57f9436a6 src/org/jruby/compiler/ASTCompiler.java
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
+        // if body is a rescue node, inspect its pieces separately to avoid it disabling all optz
*_*_*
// TODO: This is probably BAD...
ed0a495c61 Remove support for and uses of deprecated Callback methods.
9251dc2018 Grr...I suppose I should have checked this before, but Mongrel still uses CallbackFactory. We need to get them off it so we can delete it some day. Damn you, crufty old dependencies!
24befe5cf0 House cleaning! Delete a bunch of unused and/or deprecated classes of strange and wonderful origins, eliminate all deprecated Callback-related code, reduce environment lookup to a simple System.getenv call, kill off the last of the old stackless interpreter's "Instruction" interface, and other miscellaneous cleanup.
08ba6cea4e Refactoring and cleanup of callback factories
4bf7730033 Dump generated classes to disk during the build process
5c0789044d JRUBY-484: Reverse Iter Call relationship in AST and also pass block via Java frame versus maintaining blocks in TC
ed0a495c6 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
-        // TODO: This is probably BAD...
9251dc201 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
+        // TODO: This is probably BAD...
24befe5cf src/org/jruby/runtime/callback/InvocationCallbackFactory.java
-        // TODO: This is probably BAD...
08ba6cea4 src/org/jruby/runtime/callback/DumpingInvocationCallbackFactory.java
-        // TODO: This is probably BAD...
4bf773003 src/org/jruby/runtime/callback/DumpingInvocationCallbackFactory.java
+        // TODO: This is probably BAD...
5c0789044 src/org/jruby/runtime/callback/InvocationCallbackFactory.java
+        // TODO: This is probably BAD...
*_*_*
// FIXME: do we really want 'declared' methods? includes private/protected, and does _not_
14226fded5 Extra file committed to wrong place?
f9b28c47d3 Reverse comparison to avoid NPE.
c4e6f18a4d Refactored the new declared_method_smart logic a bit, to get code into the right classes.  (See FIXMEs re: is 'declared' really what was intended?) Other misc. JI cleanup in advance of some changes.
14226fded jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
-            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
f9b28c47d jruby/core/src/main/java/org/jruby/java/addons/ClassJavaAddons.java
+            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
c4e6f18a4 src/org/jruby/javasupport/JavaClass.java
c4e6f18a4 src/org/jruby/javasupport/JavaMethod.java
+            // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
+        // FIXME: do we really want 'declared' methods?  includes private/protected, and does _not_
*_*_*