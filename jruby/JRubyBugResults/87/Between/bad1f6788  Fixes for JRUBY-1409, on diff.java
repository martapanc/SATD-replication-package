diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 68b3da2f04..e71815af65 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1,1916 +1,1924 @@
 /*
  **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 
 import com.sun.jna.Native;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.Reader;
 import java.io.StringReader;
 import java.lang.ref.WeakReference;
 import java.util.ArrayList;
 import java.util.Hashtable;
 import java.util.HashMap;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.Stack;
 import java.util.WeakHashMap;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.RubiniusRunner;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.executable.YARVCompiledRunner;
 import org.jruby.common.RubyWarnings;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.NotCompilableException;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.compiler.yarv.StandardYARVCompiler;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.GlobalVariables;
 import org.jruby.internal.runtime.ThreadService;
 import org.jruby.internal.runtime.ValueAccessor;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaSupport;
 import org.jruby.libraries.IConvLibrary;
 import org.jruby.libraries.JRubyLibrary;
 import org.jruby.libraries.NKFLibrary;
 import org.jruby.libraries.RbConfigLibrary;
 import org.jruby.libraries.StringIOLibrary;
 import org.jruby.libraries.StringScannerLibrary;
 import org.jruby.libraries.ZlibLibrary;
 import org.jruby.libraries.YamlLibrary;
 import org.jruby.libraries.EnumeratorLibrary;
 import org.jruby.libraries.BigDecimalLibrary;
 import org.jruby.libraries.DigestLibrary;
 import org.jruby.libraries.ThreadLibrary;
 import org.jruby.libraries.IOWaitLibrary;
 import org.jruby.ext.socket.RubySocket;
 import org.jruby.ext.Generator;
 import org.jruby.ext.JavaBasedPOSIX;
 import org.jruby.ext.POSIX;
 import org.jruby.ext.POSIXFunctionMapper;
 import org.jruby.ext.Readline;
 import org.jruby.libraries.FiberLibrary;
 import org.jruby.parser.Parser;
 import org.jruby.parser.ParserConfiguration;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.EventHook;
 import org.jruby.runtime.GlobalVariable;
 import org.jruby.runtime.IAccessor;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectSpace;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.BuiltinScript;
 import org.jruby.util.ByteList;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOInputStream;
 import org.jruby.util.IOOutputStream;
 import org.jruby.util.JRubyClassLoader;
 import org.jruby.util.KCode;
 import org.jruby.util.MethodCache;
 import org.jruby.util.NormalizedFile;
 import org.jruby.regexp.RegexpFactory;
 import org.jruby.util.CommandlineParser;
 
 /**
  * The jruby runtime.
  */
 public final class Ruby {
     private static String[] BUILTIN_LIBRARIES = {"fcntl", "yaml", "yaml/syck", "jsignal" };
 
     private CacheMap cacheMap = new CacheMap();
     private MethodCache methodCache = new MethodCache();
     private ThreadService threadService;
     private Hashtable<Object, Object> runtimeInformation;
     
     private static POSIX posix = loadPosix();
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     public final RubyFixnum[] fixnumCache = new RubyFixnum[256];
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable();
     private Hashtable<Integer, WeakReference<IOHandler>> ioHandlers = new Hashtable<Integer, WeakReference<IOHandler>>();
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private ArrayList<EventHook> eventHooks = new ArrayList<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private final boolean objectSpaceEnabled;
 
     private long globalState = 1;
 
     /** safe-level:
             0 - strings from streams/environment/ARGV are tainted (default)
             1 - no dangerous operation by tainted value
             2 - process/file operations prohibited
             3 - all generated objects are tainted
             4 - no global (non-tainted) variable modification/no direct output
     */
     private int safeLevel = 0;
 
     // Default classes/objects
     private IRubyObject undef;
 
     private RubyClass objectClass;
     private RubyClass moduleClass;
     private RubyClass classClass;
 
     private RubyModule kernelModule;
     
     private RubyClass nilClass;
     private RubyClass trueClass;
     private RubyClass falseClass;
     
     private RubyModule comparableModule;
     
     private RubyClass numericClass;
     private RubyClass floatClass;
     private RubyClass integerClass;
     private RubyClass fixnumClass;
 
     private RubyModule enumerableModule;    
     private RubyClass arrayClass;
     private RubyClass hashClass;
     private RubyClass rangeClass;
     
     private RubyClass stringClass;
     private RubyClass symbolClass;
     
     private RubyNil nilObject;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     
     private RubyClass procClass;
     private RubyClass bindingClass;
     private RubyClass methodClass;
     private RubyClass unboundMethodClass;
 
     private RubyClass matchDataClass;
     private RubyClass regexpClass;
 
     private RubyClass timeClass;
 
     private RubyModule mathModule;
 
     private RubyModule marshalModule;
 
     private RubyClass bignumClass;
 
     private RubyClass dirClass;
 
     private RubyClass fileClass;
     private RubyClass fileStatClass;
     private RubyModule fileTestModule;
     private RubyClass statClass;
 
     private RubyClass ioClass;
 
     private RubyClass threadClass;
     private RubyClass threadGroupClass;
 
     private RubyClass continuationClass;
 
     private RubyClass structClass;
     private IRubyObject tmsStruct;
 
     private RubyModule gcModule;
     private RubyModule objectSpaceModule;
 
     private RubyModule processModule;
     private RubyClass procStatusClass;
     private RubyModule procUIDModule;
+    private RubyModule procGIDModule;
 
     private RubyModule precisionModule;
 
     private RubyClass exceptionClass;
 
     private RubyClass systemCallError = null;
     private RubyModule errnoModule = null;
     private IRubyObject topSelf;
 
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
 
     private long startTime = System.currentTimeMillis();
 
     private RubyInstanceConfig config;
 
     private InputStream in;
     private PrintStream out;
     private PrintStream err;
 
     private IRubyObject verbose;
     private IRubyObject debug;
 
     // Java support
     private JavaSupport javaSupport;
     private static JRubyClassLoader jrubyClassLoader;
 
     private static boolean securityRestricted = false;
 
     private Parser parser = new Parser(this);
 
     private LoadService loadService;
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private String jrubyHome;
 
     private KCode kcode = KCode.NONE;
 
     public int symbolLastId = 0;
     public int moduleLastId = 0;
 
     private Object respondToMethod;
 
     private RegexpFactory regexpFactory;
     
     /**
      * A list of finalizers, weakly referenced, to be executed on tearDown
      */
     private Map<Finalizable, Object> finalizers;
 
     /**
      * Create and initialize a new JRuby Runtime.
      */
     private Ruby(RubyInstanceConfig config) {
         this.config             = config;
         this.threadService      = new ThreadService(this);
         if(config.isSamplingEnabled()) {
             org.jruby.util.SimpleSampler.registerThreadContext(threadService.getCurrentContext());
         }
 
         this.in                 = config.getInput();
         this.out                = config.getOutput();
         this.err                = config.getError();
         this.objectSpaceEnabled = config.isObjectSpaceEnabled();
         this.profile            = config.getProfile();
         this.currentDirectory   = config.getCurrentDirectory();;
     }
 
     /**
      * Returns a default instance of the JRuby runtime.
      *
      * @return the JRuby runtime
      */
     public static Ruby getDefaultInstance() {
         return newInstance(new RubyInstanceConfig());
     }
 
     /**
      * Returns a default instance of the JRuby runtime configured as provided.
      *
      * @param config the instance configuration
      * @return the JRuby runtime
      */
     public static Ruby newInstance(RubyInstanceConfig config) {
         Ruby ruby = new Ruby(config);
         ruby.init();
         return ruby;
     }
 
     /**
      * Returns a default instance of the JRuby runtime configured with the given input, output and error streams.
      *
      * @param in the custom input stream
      * @param out the custom output stream
      * @param err the custom error stream
      * @return the JRuby runtime
      */
     public static Ruby newInstance(InputStream in, PrintStream out, PrintStream err) {
         RubyInstanceConfig config = new RubyInstanceConfig();
         config.setInput(in);
         config.setOutput(out);
         config.setError(err);
         return newInstance(config);
     }
 
     public IRubyObject evalFile(Reader reader, String name) {
         return eval(parseFile(reader, name, getCurrentContext().getCurrentScope()));
     }
     
     /**
      * Evaluates a script and returns a RubyObject.
      */
     public IRubyObject evalScriptlet(String script) {
         return eval(parseEval(script, "<script>", getCurrentContext().getCurrentScope(), 0));
     }
 
     public IRubyObject eval(Node node) {
         try {
             ThreadContext tc = getCurrentContext();
             return ASTInterpreter.eval(this, tc, node, tc.getFrameSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             throw newLocalJumpError("return", (IRubyObject)rj.getValue(), "unexpected return");
         } catch (JumpException.BreakJump bj) {
             throw newLocalJumpError("break", (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw newLocalJumpError("redo", (IRubyObject)rj.getValue(), "unexpected redo");
         }
     }
     
     /**
      * This differs from the other methods in that it accepts a string-based script and
      * parses and runs it as though it were loaded at a command-line. This is the preferred
      * way to start up a new script when calling directly into the Ruby object (which is
      * generally *dis*couraged.
      * 
      * @param script The contents of the script to run as a normal, root script
      * @return The last value of the script
      */
     public IRubyObject executeScript(String script, String filename) {
         Reader reader = new StringReader(script);
         Node node = parseInline(reader, filename, null);
         
         getCurrentContext().getCurrentFrame().setPosition(node.getPosition());
         return runNormally(node, false, false);
     }
     
     public void runFromMain(Reader sourceReader, String filename, CommandlineParser commandLine) {
         if(commandLine.isYARVEnabled()) {
             new YARVCompiledRunner(this,sourceReader,filename).run();
         } else if(commandLine.isRubiniusEnabled()) {
             new RubiniusRunner(this,sourceReader,filename).run();
         } else {
             Node scriptNode;
             if (commandLine.isInlineScript()) {
                 scriptNode = parseInline(sourceReader, filename, getCurrentContext().getCurrentScope());
             } else {
                 scriptNode = parseFile(sourceReader, filename, getCurrentContext().getCurrentScope());
             }
             
             getCurrentContext().getCurrentFrame().setPosition(scriptNode.getPosition());
 
             if (commandLine.isAssumePrinting() || commandLine.isAssumeLoop()) {
                 runWithGetsLoop(scriptNode, commandLine.isAssumePrinting(), commandLine.isProcessLineEnds(),
                         commandLine.isSplit(), commandLine.isCompilerEnabled(), commandLine.isYARVCompileEnabled());
             } else {
                 runNormally(scriptNode, commandLine.isCompilerEnabled(), commandLine.isYARVCompileEnabled());
             }
         }
     }
     
     public IRubyObject runWithGetsLoop(Node scriptNode, boolean printing, boolean processLineEnds, boolean split, boolean compile, boolean yarvCompile) {
         ThreadContext context = getCurrentContext();
         
         Script script = null;
         YARVCompiledRunner runner = null;
         if (compile || !yarvCompile) {
             script = tryCompile(scriptNode);
             if (compile && script == null) {
                 // terminate; tryCompile will have printed out an error and we're done
                 return getNil();
             }
         } else if (yarvCompile) {
             runner = tryCompileYarv(scriptNode);
         }
         
         if (processLineEnds) {
             getGlobalVariables().set("$\\", getGlobalVariables().get("$/"));
         }
         
         IRubyObject result = null;
         outerLoop: while (RubyKernel.gets(getTopSelf(), IRubyObject.NULL_ARRAY).isTrue()) {
             loop: while (true) { // Used for the 'redo' command
                 try {
                     if (processLineEnds) {
                         getGlobalVariables().get("$_").callMethod(context, "chop!");
                     }
                     
                     if (split) {
                         getGlobalVariables().set("$F", getGlobalVariables().get("$_").callMethod(context, "split"));
                     }
                     
                     if (script != null) {
                         result = runScript(script);
                     } else if (runner != null) {
                         result = runYarv(runner);
                     } else {
                         result = runInterpreter(scriptNode);
                     }
                     
                     if (printing) RubyKernel.print(getKernel(), new IRubyObject[] {getGlobalVariables().get("$_")});
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
         
         return getNil();
     }
     
     public IRubyObject runNormally(Node scriptNode, boolean compile, boolean yarvCompile) {
         Script script = null;
         YARVCompiledRunner runner = null;
         if (compile || (config.isJitEnabled() && !yarvCompile)) {
             script = tryCompile(scriptNode);
             if (compile && script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
                 return getNil();
             }
         } else if (yarvCompile) {
             runner = tryCompileYarv(scriptNode);
         }
         
         if (script != null) {
             return runScript(script);
         } else if (runner != null) {
             return runYarv(runner);
         } else {
             return runInterpreter(scriptNode);
         }
     }
     
     private Script tryCompile(Node node) {
         Script script = null;
         try {
             String filename = node.getPosition().getFile();
             String classname;
             if (filename.equals("-e")) {
                 classname = "__dash_e__";
             } else {
                 classname = filename.replace('\\', '/').replaceAll(".rb", "");
             }
             // remove leading / or ./ from classname, since it will muck up the dotted name
             if (classname.startsWith("/")) classname = classname.substring(1);
             if (classname.startsWith("./")) classname = classname.substring(2);
 
             ASTInspector inspector = new ASTInspector();
             inspector.inspect(node);
 
             StandardASMCompiler compiler = new StandardASMCompiler(classname, filename);
             ASTCompiler.compileRoot(node, compiler, inspector);
             script = (Script)compiler.loadClass(this.getJRubyClassLoader()).newInstance();
 
             if (config.isJitLogging()) {
                 System.err.println("compiled: " + node.getPosition().getFile());
             }
         } catch (NotCompilableException nce) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + nce.getMessage());
             }
         } catch (ClassNotFoundException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
             }
         } catch (InstantiationException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
             }
         } catch (IllegalAccessException e) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("Error -- Not compileable: " + e.getMessage());
             }
         } catch (Throwable t) {
             if (config.isJitLoggingVerbose()) {
                 System.err.println("coult not compile: " + node.getPosition().getFile() + " because of: \"" + t.getMessage() + "\"");
             }
         }
         
         return script;
     }
     
     private YARVCompiledRunner tryCompileYarv(Node node) {
         try {
             StandardYARVCompiler compiler = new StandardYARVCompiler(this);
             ASTCompiler.getYARVCompiler().compile(node, compiler);
             org.jruby.lexer.yacc.ISourcePosition p = node.getPosition();
             if(p == null && node instanceof org.jruby.ast.RootNode) {
                 p = ((org.jruby.ast.RootNode)node).getBodyNode().getPosition();
             }
             return new YARVCompiledRunner(this,compiler.getInstructionSequence("<main>",p.getFile(),"toplevel"));
         } catch (NotCompilableException nce) {
             System.err.println("Error -- Not compileable: " + nce.getMessage());
             return null;
         } catch (JumpException.ReturnJump rj) {
             return null;
         }
     }
     
     private IRubyObject runScript(Script script) {
         ThreadContext context = getCurrentContext();
         
         try {
             return script.load(getCurrentContext(), context.getFrameSelf(), IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     private IRubyObject runYarv(YARVCompiledRunner runner) {
         try {
             return runner.run();
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
     
     private IRubyObject runInterpreter(Node scriptNode) {
         ThreadContext context = getCurrentContext();
         
         try {
             return ASTInterpreter.eval(this, context, scriptNode, getTopSelf(), Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return (IRubyObject) rj.getValue();
         }
     }
 
     Object getRespondToMethod() {
         return respondToMethod;
     }
 
     void setRespondToMethod(Object rtm) {
         this.respondToMethod = rtm;
     }
     
     public IRubyObject getUndef() {
         return undef;
     }
 
     public RubyClass getObject() {
         return objectClass;
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
 
     public RubyModule getEnumerable() {
         return enumerableModule;
     }
     void setEnumerable(RubyModule enumerableModule) {
         this.enumerableModule = enumerableModule;
     }      
 
     public RubyClass getString() {
         return stringClass;
     }    
     void setString(RubyClass stringClass) {
         this.stringClass = stringClass;
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
 
     public RubyModule getMarshal() {;
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
 
     public RubyClass getStat() {
         return statClass;
     }
     void setStat(RubyClass statClass) {
         this.statClass = statClass;
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
+    
+    public RubyModule getProcGID() {
+        return procGIDModule;
+    }
+    void setProcGID(RubyModule procGIDModule) {
+        this.procGIDModule = procGIDModule;
+    }
 
     public RubyModule getPrecision() {
         return precisionModule;
     }
     void setPrecision(RubyModule precisionModule) {
         this.precisionModule = precisionModule;
     }    
 
     public RubyClass getException() {
         return exceptionClass;
     }
     void setException(RubyClass exceptionClass) {
         this.exceptionClass = exceptionClass;
     }    
 
     public RubyModule getModule(String name) {
         return (RubyModule) objectClass.getConstantAt(name);
     }
 
     /**
      * 
      * @param internedName the name of the module; <em>must</em> be an interned String!
      * @return
      */
     public RubyModule fastGetModule(String internedName) {
         return (RubyModule) objectClass.fastGetConstantAt(internedName);
     }
 
     /** Returns a class from the instance pool.
      *
      * @param name The name of the class.
      * @return The class.
      */
     public RubyClass getClass(String name) {
         return objectClass.getClass(name);
     }
 
     /**
      * 
      * @param internedName the name of the class; <em>must</em> be an interned String!
      * @return
      */
     public RubyClass fastGetClass(String internedName) {
         return objectClass.fastGetClass(internedName);
     }
 
     /** rb_define_class
      *
      */
     public RubyClass defineClass(String name, RubyClass superClass, ObjectAllocator allocator) {
         IRubyObject classObj = objectClass.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) throw newNameError(name + " is already defined", name);
             return klazz;
         }
 
         if (superClass == null) {
             warnings.warn("no super class for `" + name + "', Object assumed");
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, objectClass, false);
     }
 
     /** rb_define_class_under
     *
     */
     public RubyClass defineClassUnder(String name, RubyClass superClass, ObjectAllocator allocator, RubyModule parent) {
         IRubyObject classObj = parent.getConstantAt(name);
 
         if (classObj != null) {
             if (!(classObj instanceof RubyClass)) throw newTypeError(name + " is not a class");
             RubyClass klazz = (RubyClass)classObj;
             if (klazz.getSuperClass().getRealClass() != superClass) throw newNameError(name + " is already defined", name);
             return klazz;
         }
 
         if (superClass == null) {
             warnings.warn("no super class for `" + parent.getName() + "::" + name + "', Object assumed");
             superClass = objectClass;
         }
 
         return RubyClass.newClass(this, superClass, name, allocator, parent, true);
     }
 
     /** rb_define_module
      *
      */
     public RubyModule defineModule(String name) {
         IRubyObject moduleObj = objectClass.getConstantAt(name);
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             throw newTypeError(moduleObj.getMetaClass().getName() + " is not a module");
         }
 
         return RubyModule.newModule(this, name, objectClass, false);
     }
 
     /** rb_define_module_under
     *
     */
     public RubyModule defineModuleUnder(String name, RubyModule parent) {
         IRubyObject moduleObj = parent.getConstantAt(name);
 
         if (moduleObj != null ) {
             if (moduleObj.isModule()) return (RubyModule)moduleObj;
             throw newTypeError(parent.getName() + "::" + moduleObj.getMetaClass().getName() + " is not a module");
         }
 
         return RubyModule.newModule(this, name, parent, true);
     }
 
     /**
      * In the current context, get the named module. If it doesn't exist a
      * new module is created.
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
 
 
     /** Getter for property securityLevel.
      * @return Value of property securityLevel.
      */
     public int getSafeLevel() {
         return this.safeLevel;
     }
 
     /** Setter for property securityLevel.
      * @param safeLevel New value of property securityLevel.
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
 
     // FIXME moved this hear to get what's obviously a utility method out of IRubyObject.
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
 
     /**
      * Retrieve mappings of cached methods to where they have been cached.  When a cached
      * method needs to be invalidated this map can be used to remove all places it has been
      * cached.
      *
      * @return the mappings of where cached methods have been stored
      */
     public CacheMap getCacheMap() {
         return cacheMap;
     }
     
     /**
      * Retrieve method cache.
      * 
      * @return method cache where cached methods have been stored
      */
     public MethodCache getMethodCache() {
         return methodCache;
     }
 
     /**
      * @see org.jruby.Ruby#getRuntimeInformation
      */
     public Map<Object, Object> getRuntimeInformation() {
         return runtimeInformation == null ? runtimeInformation = new Hashtable<Object, Object>() : runtimeInformation;
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
 
     /** ruby_init
      *
      */
     // TODO: Figure out real dependencies between vars and reorder/refactor into better methods
     private void init() {
         ThreadContext tc = getCurrentContext();
 
         this.regexpFactory = RegexpFactory.getFactory(this.config.getDefaultRegexpEngine());
 
         javaSupport = new JavaSupport(this);
 
         tc.preInitCoreClasses();
 
         initCoreClasses();
         
         // core classes are initialized, ensure top scope has Object as cref
         tc.getCurrentScope().getStaticScope().setModule(objectClass);
 
         verbose = falseObject;
         debug = falseObject;
 
         initLibraries();
 
         topSelf = TopSelfFactory.createTopSelf(this);
 
         tc.preInitBuiltinClasses(objectClass, topSelf);
 
         RubyGlobal.createGlobals(this);
         
         defineGlobalConstant("TRUE", trueObject);
         defineGlobalConstant("FALSE", falseObject);
         defineGlobalConstant("NIL", nilObject);
 
         getObject().defineConstant("TOPLEVEL_BINDING", newBinding());
 
         RubyKernel.autoload(topSelf, newSymbol("Java"), newString("java"));
         
         methodCache.initialized();
     }
 
     private void initLibraries() {
         loadService = config.createLoadService(this);
         registerBuiltin("java.rb", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     Java.createJavaModule(runtime);
                     new BuiltinScript("javasupport").load(runtime);
                     RubyClassPathVariable.createClassPathVariable(runtime);
                 }
             });
         
         registerBuiltin("socket.rb", new RubySocket.Service());
         registerBuiltin("rbconfig.rb", new RbConfigLibrary());
 
         for (int i=0; i<BUILTIN_LIBRARIES.length; i++) {
             if(profile.allowBuiltin(BUILTIN_LIBRARIES[i])) {
                 loadService.registerRubyBuiltin(BUILTIN_LIBRARIES[i]);
             }
         }
 
         final Library NO_OP_LIBRARY = new Library() {
                 public void load(Ruby runtime) throws IOException {
                 }
             };
 
         registerBuiltin("jruby.rb", new JRubyLibrary());
         registerBuiltin("jruby/ext.rb", new RubyJRuby.ExtLibrary());
         registerBuiltin("iconv.rb", new IConvLibrary());
         registerBuiltin("nkf.rb", new NKFLibrary());
         registerBuiltin("stringio.rb", new StringIOLibrary());
         registerBuiltin("strscan.rb", new StringScannerLibrary());
         registerBuiltin("zlib.rb", new ZlibLibrary());
         registerBuiltin("yaml_internal.rb", new YamlLibrary());
         registerBuiltin("enumerator.rb", new EnumeratorLibrary());
         registerBuiltin("generator_internal.rb", new Generator.Service());
         registerBuiltin("readline.rb", new Readline.Service());
         registerBuiltin("thread.so", new ThreadLibrary());
         registerBuiltin("openssl.so", new Library() {
                 public void load(Ruby runtime) throws IOException {
                     runtime.getKernel().callMethod(runtime.getCurrentContext(),"require",runtime.newString("rubygems"));
                     runtime.getTopSelf().callMethod(runtime.getCurrentContext(),"gem",runtime.newString("jruby-openssl"));
                     runtime.getKernel().callMethod(runtime.getCurrentContext(),"require",runtime.newString("openssl.rb"));
                 }
             });
         registerBuiltin("digest.so", new DigestLibrary());
         registerBuiltin("digest.rb", new DigestLibrary());
         registerBuiltin("digest/md5.rb", new DigestLibrary.MD5());
         registerBuiltin("digest/rmd160.rb", new DigestLibrary.RMD160());
         registerBuiltin("digest/sha1.rb", new DigestLibrary.SHA1());
         registerBuiltin("digest/sha2.rb", new DigestLibrary.SHA2());
         registerBuiltin("bigdecimal.rb", new BigDecimalLibrary());
         registerBuiltin("io/wait.so", new IOWaitLibrary());
         registerBuiltin("etc.so", NO_OP_LIBRARY);
         registerBuiltin("fiber.so", new FiberLibrary());
     }
 
     private void registerBuiltin(String nm, Library lib) {
         if(profile.allowBuiltin(nm)) {
             loadService.registerBuiltin(nm,lib);
         }
     }
 
     private void initCoreClasses() {
         undef = new RubyUndef();
 
         objectClass = RubyClass.createBootstrapClass(this, "Object", null, RubyObject.OBJECT_ALLOCATOR);
         moduleClass = RubyClass.createBootstrapClass(this, "Module", objectClass, RubyModule.MODULE_ALLOCATOR);
         classClass = RubyClass.createBootstrapClass(this, "Class", moduleClass, RubyClass.CLASS_ALLOCATOR);
 
         objectClass.setMetaClass(classClass);
         moduleClass.setMetaClass(classClass);
         classClass.setMetaClass(classClass);
 
         RubyClass metaClass; 
         metaClass = objectClass.makeMetaClass(classClass);
         metaClass = moduleClass.makeMetaClass(metaClass);
         metaClass = classClass.makeMetaClass(metaClass);
         
         RubyObject.createObjectClass(this, objectClass);
         RubyModule.createModuleClass(this, moduleClass);
         RubyClass.createClassClass(this, classClass);
 
         RubyKernel.createKernelModule(this);
         objectClass.includeModule(kernelModule);
 
         // Pre-create the core classes we know we will get referenced by starting up the runtime.
         RubyNil.createNilClass(this);
         RubyBoolean.createFalseClass(this);
         RubyBoolean.createTrueClass(this);
         
         nilObject = new RubyNil(this);
         falseObject = new RubyBoolean(this, false);
         trueObject = new RubyBoolean(this, true);
 
         RubyComparable.createComparable(this);
         RubyEnumerable.createEnumerableModule(this);
         RubyString.createStringClass(this);
         RubySymbol.createSymbolClass(this);
         
         if (profile.allowClass("ThreadGroup")) RubyThreadGroup.createThreadGroupClass(this);
         if (profile.allowClass("Thread")) RubyThread.createThreadClass(this);
         if (profile.allowClass("Exception")) RubyException.createExceptionClass(this);
         if (profile.allowModule("Precision")) RubyPrecision.createPrecisionModule(this);
         if (profile.allowClass("Numeric")) RubyNumeric.createNumericClass(this);
         if (profile.allowClass("Integer")) RubyInteger.createIntegerClass(this);
         if (profile.allowClass("Fixnum")) RubyFixnum.createFixnumClass(this);
         if (profile.allowClass("Hash")) RubyHash.createHashClass(this);
         if (profile.allowClass("Array")) RubyArray.createArrayClass(this);
         if (profile.allowClass("Float")) RubyFloat.createFloatClass(this);
         if (profile.allowClass("Bignum")) RubyBignum.createBignumClass(this);
 
         ioClass = RubyIO.createIOClass(this);        
         
         if (profile.allowClass("Struct")) RubyStruct.createStructClass(this);
 
         if (profile.allowClass("Tms")) {
             tmsStruct = RubyStruct.newInstance(structClass,
                     new IRubyObject[] { newString("Tms"), newSymbol("utime"), newSymbol("stime"),
                         newSymbol("cutime"), newSymbol("cstime")}, Block.NULL_BLOCK);
         }
 
         if (profile.allowClass("Binding")) RubyBinding.createBindingClass(this);
         // Math depends on all numeric types
         if (profile.allowModule("Math")) RubyMath.createMathModule(this); 
         if (profile.allowClass("Regexp")) RubyRegexp.createRegexpClass(this);
         if (profile.allowClass("Range")) RubyRange.createRangeClass(this);
         if (profile.allowModule("ObjectSpace")) RubyObjectSpace.createObjectSpaceModule(this);
         if (profile.allowModule("GC")) RubyGC.createGCModule(this);
         if (profile.allowClass("Proc")) RubyProc.createProcClass(this);
         if (profile.allowClass("Method")) RubyMethod.createMethodClass(this);
         if (profile.allowClass("MatchData")) RubyMatchData.createMatchDataClass(this);
         if (profile.allowModule("Marshal")) RubyMarshal.createMarshalModule(this);
         if (profile.allowClass("Dir")) RubyDir.createDirClass(this);
         if (profile.allowModule("FileTest")) RubyFileTest.createFileTestModule(this);
         // depends on IO, FileTest
         if (profile.allowClass("File")) RubyFile.createFileClass(this);
         if (profile.allowClass("File::Stat")) RubyFileStat.createFileStatClass(this);
         if (profile.allowModule("Process")) RubyProcess.createProcessModule(this);
         if (profile.allowClass("Time")) RubyTime.createTimeClass(this);
         if (profile.allowClass("UnboundMethod")) RubyUnboundMethod.defineUnboundMethodClass(this);
 
         RubyClass exceptionClass = fastGetClass("Exception");
         RubyClass standardError = null;
         RubyClass runtimeError = null;
         RubyClass ioError = null;
         RubyClass scriptError = null;
         RubyClass nameError = null;
         RubyClass signalException = null;
         
         RubyClass rangeError = null;
         if (profile.allowClass("StandardError")) {
             standardError = defineClass("StandardError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("RuntimeError")) {
             runtimeError = defineClass("RuntimeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("IOError")) {
             ioError = defineClass("IOError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("ScriptError")) {
             scriptError = defineClass("ScriptError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("NameError")) {
             nameError = RubyNameError.createNameErrorClass(this, standardError);
         }
         if (profile.allowClass("NoMethodError")) {
             RubyNoMethodError.createNoMethodErrorClass(this, nameError);
         }        
         if (profile.allowClass("RangeError")) {
             rangeError = defineClass("RangeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SystemExit")) {
             RubySystemExit.createSystemExitClass(this, exceptionClass);
         }
         if (profile.allowClass("Fatal")) {
             defineClass("Fatal", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("SignalException")) {
             signalException = defineClass("SignalException", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("Interrupt")) {
             defineClass("Interrupt", signalException, signalException.getAllocator());
         }
         if (profile.allowClass("TypeError")) {
             defineClass("TypeError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("ArgumentError")) {
             defineClass("ArgumentError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("IndexError")) {
             defineClass("IndexError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SyntaxError")) {
             defineClass("SyntaxError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("LoadError")) {
             defineClass("LoadError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("NotImplementedError")) {
             defineClass("NotImplementedError", scriptError, scriptError.getAllocator());
         }
         if (profile.allowClass("SecurityError")) {
             defineClass("SecurityError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("NoMemoryError")) {
             defineClass("NoMemoryError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("RegexpError")) {
             defineClass("RegexpError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("EOFError")) {
             defineClass("EOFError", ioError, ioError.getAllocator());
         }
         if (profile.allowClass("LocalJumpError")) {
             RubyLocalJumpError.createLocalJumpErrorClass(this, standardError);
         }
         if (profile.allowClass("ThreadError")) {
             defineClass("ThreadError", standardError, standardError.getAllocator());
         }
         if (profile.allowClass("SystemStackError")) {
             defineClass("SystemStackError", exceptionClass, exceptionClass.getAllocator());
         }
         if (profile.allowClass("ZeroDivisionError")) {
             defineClass("ZeroDivisionError", standardError, standardError.getAllocator());
         }
         // FIXME: Actually this somewhere <- fixed
         if (profile.allowClass("FloatDomainError")) {
             defineClass("FloatDomainError", rangeError, rangeError.getAllocator());
         }
         if (profile.allowClass("NativeException")) NativeException.createClass(this, runtimeError);
         if (profile.allowClass("SystemCallError")) {
             systemCallError = defineClass("SystemCallError", standardError, standardError.getAllocator());
         }
         if (profile.allowModule("Errno")) errnoModule = defineModule("Errno");
 
         initErrnoErrors();
 
         if (profile.allowClass("Data")) defineClass("Data", objectClass, objectClass.getAllocator());
         if (profile.allowModule("Signal")) RubySignal.createSignal(this);
         if (profile.allowClass("Continuation")) RubyContinuation.createContinuation(this);
     }
 
     /**
      * Create module Errno's Variables.  We have this method since Errno does not have it's
      * own java class.
      */
     private void initErrnoErrors() {
         createSysErr(IErrno.ENOTEMPTY, "ENOTEMPTY");
         createSysErr(IErrno.ERANGE, "ERANGE");
         createSysErr(IErrno.ESPIPE, "ESPIPE");
         createSysErr(IErrno.ENFILE, "ENFILE");
         createSysErr(IErrno.EXDEV, "EXDEV");
         createSysErr(IErrno.ENOMEM, "ENOMEM");
         createSysErr(IErrno.E2BIG, "E2BIG");
         createSysErr(IErrno.ENOENT, "ENOENT");
         createSysErr(IErrno.ENOSYS, "ENOSYS");
         createSysErr(IErrno.EDOM, "EDOM");
         createSysErr(IErrno.ENOSPC, "ENOSPC");
         createSysErr(IErrno.EINVAL, "EINVAL");
         createSysErr(IErrno.EEXIST, "EEXIST");
         createSysErr(IErrno.EAGAIN, "EAGAIN");
         createSysErr(IErrno.ENXIO, "ENXIO");
         createSysErr(IErrno.EILSEQ, "EILSEQ");
         createSysErr(IErrno.ENOLCK, "ENOLCK");
         createSysErr(IErrno.EPIPE, "EPIPE");
         createSysErr(IErrno.EFBIG, "EFBIG");
         createSysErr(IErrno.EISDIR, "EISDIR");
         createSysErr(IErrno.EBUSY, "EBUSY");
         createSysErr(IErrno.ECHILD, "ECHILD");
         createSysErr(IErrno.EIO, "EIO");
         createSysErr(IErrno.EPERM, "EPERM");
         createSysErr(IErrno.EDEADLOCK, "EDEADLOCK");
         createSysErr(IErrno.ENAMETOOLONG, "ENAMETOOLONG");
         createSysErr(IErrno.EMLINK, "EMLINK");
         createSysErr(IErrno.ENOTTY, "ENOTTY");
         createSysErr(IErrno.ENOTDIR, "ENOTDIR");
         createSysErr(IErrno.EFAULT, "EFAULT");
         createSysErr(IErrno.EBADF, "EBADF");
         createSysErr(IErrno.EINTR, "EINTR");
         createSysErr(IErrno.EWOULDBLOCK, "EWOULDBLOCK");
         createSysErr(IErrno.EDEADLK, "EDEADLK");
         createSysErr(IErrno.EROFS, "EROFS");
         createSysErr(IErrno.EMFILE, "EMFILE");
         createSysErr(IErrno.ENODEV, "ENODEV");
         createSysErr(IErrno.EACCES, "EACCES");
         createSysErr(IErrno.ENOEXEC, "ENOEXEC");
         createSysErr(IErrno.ESRCH, "ESRCH");
         createSysErr(IErrno.ECONNREFUSED, "ECONNREFUSED");
         createSysErr(IErrno.ECONNRESET, "ECONNRESET");
         createSysErr(IErrno.EADDRINUSE, "EADDRINUSE");
         createSysErr(IErrno.ECONNABORTED, "ECONNABORTED");
     }
 
     /**
      * Creates a system error.
      * @param i the error code (will probably use a java exception instead)
      * @param name of the error to define.
      **/
     private void createSysErr(int i, String name) {
         if(profile.allowClass(name)) {
             errnoModule.defineClassUnder(name, systemCallError, systemCallError.getAllocator()).defineConstant("Errno", newFixnum(i));
         }
     }
 
     /** Getter for property isVerbose.
      * @return Value of property isVerbose.
      */
     public IRubyObject getVerbose() {
         return verbose;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose;
     }
 
     /** Getter for property isDebug.
      * @return Value of property isDebug.
      */
     public IRubyObject getDebug() {
         return debug;
     }
 
     /** Setter for property isDebug.
      * @param debug New value of property isDebug.
      */
     public void setDebug(IRubyObject debug) {
         this.debug = debug;
     }
 
     public JavaSupport getJavaSupport() {
         return javaSupport;
     }
 
     public JRubyClassLoader getJRubyClassLoader() {
         if (!Ruby.isSecurityRestricted() && jrubyClassLoader == null)
             jrubyClassLoader = new JRubyClassLoader(Thread.currentThread().getContextClassLoader());
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
     
     public Node parseFile(Reader content, String file, DynamicScope scope) {
         return parser.parse(file, content, scope, new ParserConfiguration(0, false, false, true));
     }
 
     public Node parseInline(Reader content, String file, DynamicScope scope) {
         return parser.parse(file, content, scope, new ParserConfiguration(0, false, true));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, new StringReader(content), scope, new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, new StringReader(content), scope, 
                 new ParserConfiguration(lineNumber, extraPositionInformation, false));
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
 
     public RubyWarnings getWarnings() {
         return warnings;
     }
 
     public RegexpFactory getRegexpFactory() {
         return this.regexpFactory;
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
 
         ThreadContext tc = getCurrentContext();
         IRubyObject backtrace = excp.callMethod(tc, "backtrace");
 
         PrintStream errorStream = getErrorStream();
         if (backtrace.isNil() || !(backtrace instanceof RubyArray)) {
             if (tc.getSourceFile() != null) {
                 errorStream.print(tc.getPosition());
             } else {
                 errorStream.print(tc.getSourceLine());
             }
         } else if (((RubyArray) backtrace).getLength() == 0) {
             printErrorPos(errorStream);
         } else {
             IRubyObject mesg = ((RubyArray) backtrace).first(IRubyObject.NULL_ARRAY);
 
             if (mesg.isNil()) {
                 printErrorPos(errorStream);
             } else {
                 errorStream.print(mesg);
             }
         }
 
         RubyClass type = excp.getMetaClass();
         String info = excp.toString();
 
         if (type == fastGetClass("RuntimeError") && (info == null || info.length() == 0)) {
             errorStream.print(": unhandled exception\n");
         } else {
             String path = type.getName();
 
             if (info.length() == 0) {
                 errorStream.print(": " + path + '\n');
             } else {
                 if (path.startsWith("#")) {
                     path = null;
                 }
 
                 String tail = null;
                 if (info.indexOf("\n") != -1) {
                     tail = info.substring(info.indexOf("\n") + 1);
                     info = info.substring(0, info.indexOf("\n"));
                 }
 
                 errorStream.print(": " + info);
 
                 if (path != null) {
                     errorStream.print(" (" + path + ")\n");
                 }
 
                 if (tail != null) {
                     errorStream.print(tail + '\n');
                 }
             }
         }
 
         excp.printBacktrace(errorStream);
     }
 
     private void printErrorPos(PrintStream errorStream) {
         ThreadContext tc = getCurrentContext();
         if (tc.getSourceFile() != null) {
             if (tc.getFrameName() != null) {
                 errorStream.print(tc.getPosition());
                 errorStream.print(":in '" + tc.getFrameName() + '\'');
             } else if (tc.getSourceLine() != 0) {
                 errorStream.print(tc.getPosition());
             } else {
                 errorStream.print(tc.getSourceFile());
             }
         }
     }
 
     /** This method compiles and interprets a Ruby script.
      *
      *  It can be used if you want to use JRuby as a Macro language.
      *
      */
     public void loadFile(String scriptName, Reader source) {
         if (!Ruby.isSecurityRestricted()) {
             File f = new File(scriptName);
             if(f.exists() && !f.isAbsolute() && !scriptName.startsWith("./")) {
                 scriptName = "./" + scriptName;
             };
         }
 
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
 
             Node node = parseFile(source, scriptName, null);
             ASTInterpreter.eval(this, context, node, self, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
         }
     }
 
     public void loadScript(Script script) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
 
             context.preNodeEval(objectClass, self);
             
             script.load(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
         }
     }
 
     public class CallTraceFuncHook implements EventHook {
         private RubyProc traceFunc;
         
         public void setTraceFunc(RubyProc traceFunc) {
             this.traceFunc = traceFunc;
         }
         
         public void event(ThreadContext context, int event, String file, int line, String name, IRubyObject type) {
             if (!context.isWithinTrace()) {
                 if (file == null) file = "(ruby)";
                 if (type == null) type = getFalse();
                 
                 RubyBinding binding = RubyBinding.newBinding(Ruby.this);
 
                 context.preTrace();
                 try {
                     traceFunc.call(new IRubyObject[] {
                         newString(EVENT_NAMES[event]), // event name
                         newString(file), // filename
                         newFixnum(line + 1), // line numbers should be 1-based
                         name != null ? RubySymbol.newSymbol(Ruby.this, name) : getNil(),
                         binding,
                         type
                     });
                 } finally {
                     context.postTrace();
                 }
             }
         }
 
         public boolean isInterestedInEvent(int event) {
             return true;
         }
     };
     
     private final CallTraceFuncHook callTraceFuncHook = new CallTraceFuncHook();
     
     public void addEventHook(EventHook hook) {
         eventHooks.add(hook);
         hasEventHooks = true;
     }
     
     public void removeEventHook(EventHook hook) {
         eventHooks.remove(hook);
         hasEventHooks = !eventHooks.isEmpty();
     }
 
     public void setTraceFunction(RubyProc traceFunction) {
         removeEventHook(callTraceFuncHook);
         
         if (traceFunction == null) {
             return;
         }
         
         callTraceFuncHook.setTraceFunc(traceFunction);
         addEventHook(callTraceFuncHook);
     }
     
     public void callEventHooks(ThreadContext context, int event, String file, int line, String name, IRubyObject type) {
         for (EventHook eventHook : eventHooks) {
             if (eventHook.isInterestedInEvent(event)) {
                 eventHook.event(context, event, file, line, name, type);
             }
         }
     }
     
     public boolean hasEventHooks() {
         return hasEventHooks;
     }
     
     public GlobalVariables getGlobalVariables() {
         return globalVariables;
     }
 
     // For JSR 223 support: see http://scripting.java.net/
     public void setGlobalVariables(GlobalVariables globalVariables) {
         this.globalVariables = globalVariables;
     }
 
     public CallbackFactory callbackFactory(Class<?> type) {
         return CallbackFactory.createFactory(this, type);
     }
 
     /**
      * Push block onto exit stack.  When runtime environment exits
      * these blocks will be evaluated.
      *
      * @return the element that was pushed onto stack
      */
     public IRubyObject pushExitBlock(RubyProc proc) {
         atExitBlocks.push(proc);
         return proc;
     }
     
     public void addFinalizer(Finalizable finalizer) {
         synchronized (this) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap<Finalizable, Object>();
             }
         }
         
         synchronized (finalizers) {
             finalizers.put(finalizer, null);
         }
     }
     
     public void removeFinalizer(Finalizable finalizer) {
         if (finalizers != null) {
             synchronized (finalizers) {
                 finalizers.remove(finalizer);
             }
         }
     }
 
     /**
      * Make sure Kernel#at_exit procs get invoked on runtime shutdown.
      * This method needs to be explicitly called to work properly.
      * I thought about using finalize(), but that did not work and I
      * am not sure the runtime will be at a state to run procs by the
      * time Ruby is going away.  This method can contain any other
      * things that need to be cleaned up at shutdown.
      */
     public void tearDown() {
         while (!atExitBlocks.empty()) {
             RubyProc proc = atExitBlocks.pop();
 
             proc.call(IRubyObject.NULL_ARRAY);
         }
         if (finalizers != null) {
             synchronized (finalizers) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(finalizers.keySet()).iterator(); finalIter.hasNext();) {
                     finalIter.next().finalize();
                     finalIter.remove();
                 }
             }
         }
     }
 
     // new factory methods ------------------------------------------------------------------------
 
     public RubyArray newArray() {
         return RubyArray.newArray(this);
     }
 
     public RubyArray newArrayLight() {
         return RubyArray.newArrayLight(this);
     }
diff --git a/src/org/jruby/RubyArray.java b/src/org/jruby/RubyArray.java
index 1914930c5d..8203338bab 100644
--- a/src/org/jruby/RubyArray.java
+++ b/src/org/jruby/RubyArray.java
@@ -1,1001 +1,1002 @@
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
  * Copyright (C) 2006 Daniel Steer <damian.steer@hp.com>
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
 
 import java.lang.reflect.Array;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Comparator;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.Pack;
 
 /**
  * The implementation of the built-in class Array in Ruby.
  */
 public class RubyArray extends RubyObject implements List {
 
     public static RubyClass createArrayClass(Ruby runtime) {
         RubyClass arrayc = runtime.defineClass("Array", runtime.getObject(), ARRAY_ALLOCATOR);
         runtime.setArray(arrayc);
         arrayc.index = ClassIndex.ARRAY;
         arrayc.kindOf = new RubyModule.KindOf() {
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyArray;
             }
         };
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyArray.class);
 
         arrayc.includeModule(runtime.getEnumerable());
         arrayc.defineAnnotatedMethods(RubyArray.class);
         arrayc.dispatcher = callbackFactory.createDispatcher(arrayc);
 
         return arrayc;
     }
 
     private static ObjectAllocator ARRAY_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyArray(runtime, klass);
         }
     };
 
     public int getNativeTypeIndex() {
         return ClassIndex.ARRAY;
     }
 
     /** rb_ary_s_create
      * 
      */
     @JRubyMethod(name = "[]", rest = true, frame = true, meta = true)
     public static IRubyObject create(IRubyObject klass, IRubyObject[] args, Block block) {
         RubyArray arr = (RubyArray) ((RubyClass) klass).allocate();
         arr.callInit(IRubyObject.NULL_ARRAY, block);
     
         if (args.length > 0) {
             arr.alloc(args.length);
             System.arraycopy(args, 0, arr.values, 0, args.length);
             arr.realLength = args.length;
         }
         return arr;
     }
 
     /** rb_ary_new2
      *
      */
     public static final RubyArray newArray(final Ruby runtime, final long len) {
         return new RubyArray(runtime, len);
     }
     public static final RubyArray newArrayLight(final Ruby runtime, final long len) {
         return new RubyArray(runtime, len, false);
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArray(final Ruby runtime) {
         return new RubyArray(runtime, ARRAY_DEFAULT_SIZE);
     }
 
     /** rb_ary_new
      *
      */
     public static final RubyArray newArrayLight(final Ruby runtime) {
         /* Ruby arrays default to holding 16 elements, so we create an
          * ArrayList of the same size if we're not told otherwise
          */
         RubyArray arr = new RubyArray(runtime, false);
         arr.alloc(ARRAY_DEFAULT_SIZE);
         return arr;
     }
 
     public static RubyArray newArray(Ruby runtime, IRubyObject obj) {
         return new RubyArray(runtime, new IRubyObject[] { obj });
     }
 
     /** rb_assoc_new
      *
      */
     public static RubyArray newArray(Ruby runtime, IRubyObject car, IRubyObject cdr) {
         return new RubyArray(runtime, new IRubyObject[] { car, cdr });
     }
 
     /** rb_ary_new4, rb_ary_new3
      *   
      */
     public static RubyArray newArray(Ruby runtime, IRubyObject[] args) {
         RubyArray arr = new RubyArray(runtime, args.length);
         System.arraycopy(args, 0, arr.values, 0, args.length);
         arr.realLength = args.length;
         return arr;
     }
     
     public static RubyArray newArrayNoCopy(Ruby runtime, IRubyObject[] args) {
         return new RubyArray(runtime, args);
     }
     
     public static RubyArray newArrayNoCopy(Ruby runtime, IRubyObject[] args, int begin) {
         return new RubyArray(runtime, args, begin);
     }
     
     public static RubyArray newArrayNoCopyLight(Ruby runtime, IRubyObject[] args) {
         RubyArray arr = new RubyArray(runtime, false);
         arr.values = args;
         arr.realLength = args.length;
         return arr;
     }
 
     public static RubyArray newArray(Ruby runtime, Collection collection) {
         RubyArray arr = new RubyArray(runtime, collection.size());
         collection.toArray(arr.values);
         arr.realLength = arr.values.length;
         return arr;
     }
 
     public static final int ARRAY_DEFAULT_SIZE = 16;    
 
     private IRubyObject[] values;
 
     private static final int TMPLOCK_ARR_F = 1 << 9;
     private static final int SHARED_ARR_F = 1 << 10;
 
     private int begin = 0;
     private int realLength = 0;
 
     /* 
      * plain internal array assignment
      */
     private RubyArray(Ruby runtime, IRubyObject[] vals) {
         super(runtime, runtime.getArray());
         values = vals;
         realLength = vals.length;
     }
 
     /* 
      * plain internal array assignment
      */
     private RubyArray(Ruby runtime, IRubyObject[] vals, int begin) {
         super(runtime, runtime.getArray());
         this.values = vals;
         this.begin = begin;
         this.realLength = vals.length - begin;
         flags |= SHARED_ARR_F;
     }
     
     /* rb_ary_new2
      * just allocates the internal array
      */
     private RubyArray(Ruby runtime, long length) {
         super(runtime, runtime.getArray());
         checkLength(length);
         alloc((int) length);
     }
 
     private RubyArray(Ruby runtime, long length, boolean objectspace) {
         super(runtime, runtime.getArray(), objectspace);
         checkLength(length);
         alloc((int)length);
     }     
 
     /* rb_ary_new3, rb_ary_new4
      * allocates the internal array of size length and copies the 'length' elements
      */
     public RubyArray(Ruby runtime, long length, IRubyObject[] vals) {
         super(runtime, runtime.getArray());
         checkLength(length);
         int ilength = (int) length;
         alloc(ilength);
         if (ilength > 0 && vals.length > 0) System.arraycopy(vals, 0, values, 0, ilength);
 
         realLength = ilength;
     }
 
     /* NEWOBJ and OBJSETUP equivalent
      * fastest one, for shared arrays, optional objectspace
      */
     private RubyArray(Ruby runtime, boolean objectSpace) {
         super(runtime, runtime.getArray(), objectSpace);
     }
 
     private RubyArray(Ruby runtime) {
         super(runtime, runtime.getArray());
         alloc(ARRAY_DEFAULT_SIZE);
     }
 
     public RubyArray(Ruby runtime, RubyClass klass) {
         super(runtime, klass);
         alloc(ARRAY_DEFAULT_SIZE);
     }
     
     /* Array constructors taking the MetaClass to fulfil MRI Array subclass behaviour
      * 
      */
     private RubyArray(Ruby runtime, RubyClass klass, int length) {
         super(runtime, klass);
         alloc(length);
     }
     
     private RubyArray(Ruby runtime, RubyClass klass, long length) {
         super(runtime, klass);
         checkLength(length);
         alloc((int)length);
     }
 
     private RubyArray(Ruby runtime, RubyClass klass, long length, boolean objectspace) {
         super(runtime, klass, objectspace);
         checkLength(length);
         alloc((int)length);
     }    
 
     private RubyArray(Ruby runtime, RubyClass klass, boolean objectSpace) {
         super(runtime, klass, objectSpace);
     }
     
     private RubyArray(Ruby runtime, RubyClass klass, RubyArray original) {
         super(runtime, klass);
         realLength = original.realLength;
         alloc(realLength);
         System.arraycopy(original.values, original.begin, values, 0, realLength);
     }
     
     private final IRubyObject[] reserve(int length) {
         return new IRubyObject[length];
     }
 
     private final void alloc(int length) {
         values = new IRubyObject[length];
     }
 
     private final void realloc(int newLength) {
         IRubyObject[] reallocated = new IRubyObject[newLength];
         System.arraycopy(values, 0, reallocated, 0, newLength > realLength ? realLength : newLength);
         values = reallocated;
     }
 
     private final void checkLength(long length) {
         if (length < 0) {
             throw getRuntime().newArgumentError("negative array size (or size too big)");
         }
 
         if (length >= Integer.MAX_VALUE) {
             throw getRuntime().newArgumentError("array size too big");
         }
     }
 
     /** Getter for property list.
      * @return Value of property list.
      */
     public List getList() {
         return Arrays.asList(toJavaArray()); 
     }
 
     public int getLength() {
         return realLength;
     }
 
     public IRubyObject[] toJavaArray() {
         IRubyObject[] copy = reserve(realLength);
         System.arraycopy(values, begin, copy, 0, realLength);
         return copy;
     }
     
     public IRubyObject[] toJavaArrayUnsafe() {
         return (flags & SHARED_ARR_F) == 0 ? values : toJavaArray();
     }    
 
     public IRubyObject[] toJavaArrayMaybeUnsafe() {
         return ((flags & SHARED_ARR_F) == 0 && begin == 0 && values.length == realLength) ? values : toJavaArray();
     }    
 
     /** rb_ary_make_shared
     *
     */
     private final RubyArray makeShared(int beg, int len, RubyClass klass) {
         return makeShared(beg, len, klass, klass.getRuntime().isObjectSpaceEnabled());
     }    
     
     /** rb_ary_make_shared
      *
      */
     private final RubyArray makeShared(int beg, int len, RubyClass klass, boolean objectSpace) {
         RubyArray sharedArray = new RubyArray(getRuntime(), klass, objectSpace);
         flags |= SHARED_ARR_F;
         sharedArray.values = values;
         sharedArray.flags |= SHARED_ARR_F;
         sharedArray.begin = beg;
         sharedArray.realLength = len;
         return sharedArray;
     }
 
     /** rb_ary_modify_check
      *
      */
     private final void modifyCheck() {
         testFrozen("array");
 
         if ((flags & TMPLOCK_ARR_F) != 0) {
             throw getRuntime().newTypeError("can't modify array during iteration");
         }
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify array");
         }
     }
 
     /** rb_ary_modify
      *
      */
     private final void modify() {
         modifyCheck();
         if ((flags & SHARED_ARR_F) != 0) {
             IRubyObject[] vals = reserve(realLength);
             flags &= ~SHARED_ARR_F;
             System.arraycopy(values, begin, vals, 0, realLength);
             begin = 0;            
             values = vals;
         }
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** rb_ary_initialize
      * 
      */
     @JRubyMethod(name = "initialize", required = 0, optional = 2, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 0, 2);
         Ruby runtime = getRuntime();
 
         if (argc == 0) {
             realLength = 0;
             if (block.isGiven()) runtime.getWarnings().warn("given block not used");
 
     	    return this;
     	}
 
         if (argc == 1 && !(args[0] instanceof RubyFixnum)) {
             IRubyObject val = args[0].checkArrayType();
             if (!val.isNil()) {
                 replace(val);
                 return this;
             }
         }
 
         long len = RubyNumeric.num2long(args[0]);
 
         if (len < 0) throw runtime.newArgumentError("negative array size");
 
         if (len >= Integer.MAX_VALUE) throw runtime.newArgumentError("array size too big");
 
         int ilen = (int) len;
 
         modify();
 
         if (ilen > values.length) values = reserve(ilen);
 
         if (block.isGiven()) {
             if (argc == 2) {
                 runtime.getWarnings().warn("block supersedes default value argument");
             }
 
             ThreadContext context = runtime.getCurrentContext();
             for (int i = 0; i < ilen; i++) {
                 store(i, block.yield(context, new RubyFixnum(runtime, i)));
                 realLength = i + 1;
             }
         } else {
             Arrays.fill(values, 0, ilen, (argc == 2) ? args[1] : runtime.getNil());
             realLength = ilen;
         }
     	return this;
     }
 
     /** rb_ary_replace
      *
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1)
     public IRubyObject replace(IRubyObject orig) {
         modifyCheck();
 
         RubyArray origArr = orig.convertToArray();
 
         if (this == orig) return this;
 
         origArr.flags |= SHARED_ARR_F;
         flags |= SHARED_ARR_F;        
         values = origArr.values;
         realLength = origArr.realLength;
         begin = origArr.begin;
 
 
         return this;
     }
 
     /** rb_ary_to_s
      *
      */
     @JRubyMethod(name = "to_s")
     public IRubyObject to_s() {
         if (realLength == 0) return getRuntime().newString("");
 
         return join(getRuntime().getGlobalVariables().get("$,"));
     }
 
     
     public boolean includes(IRubyObject item) {
         final ThreadContext context = getRuntime().getCurrentContext();
         int begin = this.begin;
         
         for (int i = begin; i < begin + realLength; i++) {
             if (equalInternal(context, values[i], item).isTrue()) return true;
     	}
         
         return false;
     }
 
     /** rb_ary_hash
      * 
      */
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         int h = realLength;
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             h = (h << 1) | (h < 0 ? 1 : 0);
             h ^= RubyNumeric.num2long(values[i].callMethod(context, MethodIndex.HASH, "hash"));
         }
 
         return runtime.newFixnum(h);
     }
 
     /** rb_ary_store
      *
      */
     public final IRubyObject store(long index, IRubyObject value) {
         if (index < 0) {
             index += realLength;
             if (index < 0) {
                 throw getRuntime().newIndexError("index " + (index - realLength) + " out of array");
             }
         }
 
         modify();
 
         if (index >= realLength) {
         if (index >= values.length) {
                 long newLength = values.length >> 1;
 
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += index;
             if (newLength >= Integer.MAX_VALUE) {
                 throw getRuntime().newArgumentError("index too big");
             }
             realloc((int) newLength);
         }
             if(index != realLength) Arrays.fill(values, realLength, (int) index + 1, getRuntime().getNil());
             
             realLength = (int) index + 1;
         }
 
         values[(int) index] = value;
         return value;
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt(long offset) {
         if (realLength == 0 || offset < 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + (int) offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt(int offset) {
         if (realLength == 0 || offset < 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt_f(long offset) {
         if (realLength == 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + (int) offset];
     }
 
     /** rb_ary_elt - faster
      *
      */
     private final IRubyObject elt_f(int offset) {
         if (realLength == 0 || offset >= realLength) return getRuntime().getNil();
 
         return values[begin + offset];
     }
 
     /** rb_ary_entry
      *
      */
     public final IRubyObject entry(long offset) {
         return (offset < 0 ) ? elt(offset + realLength) : elt_f(offset);
     }
 
 
     /** rb_ary_entry
      *
      */
     public final IRubyObject entry(int offset) {
         return (offset < 0 ) ? elt(offset + realLength) : elt_f(offset);
     }
 
     public final IRubyObject eltInternal(int offset) {
         return values[begin + offset];
     }
     
     public final IRubyObject eltInternalSet(int offset, IRubyObject item) {
         return values[begin + offset] = item;
     }
     
     /** rb_ary_fetch
      *
      */
     @JRubyMethod(name = "fetch", required = 1, optional = 1, frame = true)
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2 && block.isGiven()) {
             getRuntime().getWarnings().warn("block supersedes default value argument");
         }
 
         long index = RubyNumeric.num2long(args[0]);
 
         if (index < 0) index += realLength;
 
         if (index < 0 || index >= realLength) {
             if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), args[0]);
 
             if (args.length == 1) {
                 throw getRuntime().newIndexError("index " + index + " out of array");
             }
             
             return args[1];
         }
         
         return values[begin + (int) index];
     }
 
     /** rb_ary_to_ary
      * 
      */
     private static RubyArray aryToAry(IRubyObject obj) {
         if (obj instanceof RubyArray) return (RubyArray) obj;
 
         if (obj.respondsTo("to_ary")) return obj.convertToArray();
 
         RubyArray arr = new RubyArray(obj.getRuntime(), false); // possibly should not in object space
         arr.alloc(1);
         arr.values[0] = obj;
         arr.realLength = 1;
         return arr;
     }
 
     /** rb_ary_splice
      * 
      */
     private final void splice(long beg, long len, IRubyObject rpl) {
         int rlen;
 
         if (len < 0) throw getRuntime().newIndexError("negative length (" + len + ")");
 
         if (beg < 0) {
             beg += realLength;
             if (beg < 0) {
                 beg -= realLength;
                 throw getRuntime().newIndexError("index " + beg + " out of array");
             }
         }
         
         if (beg + len > realLength) len = realLength - beg;
 
         RubyArray rplArr;
         if (rpl == null || rpl.isNil()) {
             rplArr = null;
             rlen = 0;
         } else {
             rplArr = aryToAry(rpl);
             rlen = rplArr.realLength;
         }
 
         modify();
 
         if (beg >= realLength) {
             len = beg + rlen;
 
             if (len >= values.length) {
                 int tryNewLength = values.length + (values.length >> 1);
                 
                 realloc(len > tryNewLength ? (int)len : tryNewLength);
             }
 
             Arrays.fill(values, realLength, (int) beg, getRuntime().getNil());
             if (rlen > 0) {
                 System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, rlen);
             }
             realLength = (int) len;
         } else {
             long alen;
 
             if (beg + len > realLength) len = realLength - beg;
 
             alen = realLength + rlen - len;
             if (alen >= values.length) {
                 int tryNewLength = values.length + (values.length >> 1);
                 
                 realloc(alen > tryNewLength ? (int)alen : tryNewLength);
             }
 
             if (len != rlen) {
                 System.arraycopy(values, (int) (beg + len), values, (int) beg + rlen, realLength - (int) (beg + len));
                 realLength = (int) alen;
             }
 
             if (rlen > 0) {
                 System.arraycopy(rplArr.values, rplArr.begin, values, (int) beg, rlen);
             }
         }
     }
 
     /** rb_ary_insert
      * 
      */
     @JRubyMethod(name = "insert", required = 1, rest = true)
     public IRubyObject insert(IRubyObject[] args) {
         if (args.length == 1) return this;
 
         if (args.length < 1) {
             throw getRuntime().newArgumentError("wrong number of arguments (at least 1)");
         }
 
         long pos = RubyNumeric.num2long(args[0]);
 
         if (pos == -1) pos = realLength;
         if (pos < 0) pos++;
 
         RubyArray inserted = new RubyArray(getRuntime(), false);
         inserted.values = args;
         inserted.begin = 1;
         inserted.realLength = args.length - 1;
         
         splice(pos, 0, inserted); // rb_ary_new4
         
         return this;
     }
 
     /** rb_ary_dup
      * 
      */
     public final RubyArray aryDup() {
         RubyArray dup = new RubyArray(getRuntime(), getMetaClass(), this);
         dup.flags |= flags & TAINTED_F; // from DUP_SETUP
         // rb_copy_generic_ivar from DUP_SETUP here ...unlikely..
         return dup;
     }
 
     /** rb_ary_transpose
      * 
      */
     @JRubyMethod(name = "transpose")
     public RubyArray transpose() {
         RubyArray tmp, result = null;
 
         int alen = realLength;
         if (alen == 0) return aryDup();
     
         Ruby runtime = getRuntime();
         int elen = -1;
         int end = begin + alen;
         for (int i = begin; i < end; i++) {
             tmp = elt(i).convertToArray();
             if (elen < 0) {
                 elen = tmp.realLength;
                 result = new RubyArray(runtime, elen);
                 for (int j = 0; j < elen; j++) {
                     result.store(j, new RubyArray(runtime, alen));
                 }
             } else if (elen != tmp.realLength) {
                 throw runtime.newIndexError("element size differs (" + tmp.realLength
                         + " should be " + elen + ")");
             }
             for (int j = 0; j < elen; j++) {
                 ((RubyArray) result.elt(j)).store(i - begin, tmp.elt(j));
             }
         }
         return result;
     }
 
     /** rb_values_at (internal)
      * 
      */
     private final IRubyObject values_at(long olen, IRubyObject[] args) {
         RubyArray result = new RubyArray(getRuntime(), args.length);
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] instanceof RubyFixnum) {
                 result.append(entry(((RubyFixnum)args[i]).getLongValue()));
                 continue;
             }
 
             long beglen[];
             if (!(args[i] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[i]).begLen(olen, 0)) == null) {
                 continue;
             } else {
                 int beg = (int) beglen[0];
                 int len = (int) beglen[1];
                 int end = begin + len;
                 for (int j = begin; j < end; j++) {
                     result.append(entry(j + beg));
                 }
                 continue;
             }
             result.append(entry(RubyNumeric.num2long(args[i])));
         }
 
         return result;
     }
 
     /** rb_values_at
      * 
      */
     @JRubyMethod(name = "values_at", rest = true)
     public IRubyObject values_at(IRubyObject[] args) {
         return values_at(realLength, args);
     }
 
     /** rb_ary_subseq
      *
      */
     public IRubyObject subseq(long beg, long len) {
         if (beg > realLength || beg < 0 || len < 0) return getRuntime().getNil();
 
         if (beg + len > realLength) {
             len = realLength - beg;
             
             if (len < 0) len = 0;
         }
         
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0);
 
         return makeShared(begin + (int) beg, (int) len, getMetaClass());
     }
 
     /** rb_ary_subseq
      *
      */
     public IRubyObject subseqLight(long beg, long len) {
         if (beg > realLength || beg < 0 || len < 0) return getRuntime().getNil();
 
         if (beg + len > realLength) {
             len = realLength - beg;
             
             if (len < 0) len = 0;
         }
         
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0, false);
 
         return makeShared(begin + (int) beg, (int) len, getMetaClass(), false);
     }
 
     /** rb_ary_length
      *
      */
     @JRubyMethod(name = "length", alias = "size")
     public RubyFixnum length() {
         return getRuntime().newFixnum(realLength);
     }
 
     /** rb_ary_push - specialized rb_ary_store 
      *
      */
     @JRubyMethod(name = "<<", required = 1)
     public RubyArray append(IRubyObject item) {
         modify();
         
         if (realLength == values.length) {
         if (realLength == Integer.MAX_VALUE) throw getRuntime().newArgumentError("index too big");
             
             long newLength = values.length + (values.length >> 1);
             if ( newLength > Integer.MAX_VALUE ) {
                 newLength = Integer.MAX_VALUE;
             }else if ( newLength < ARRAY_DEFAULT_SIZE ) {
                 newLength = ARRAY_DEFAULT_SIZE;
             }
 
             realloc((int) newLength);
         }
         
         values[realLength++] = item;
         return this;
     }
 
     /** rb_ary_push_m
      * FIXME: Whis is this named "push_m"?
      */
     @JRubyMethod(name = "push", rest = true)
     public RubyArray push_m(IRubyObject[] items) {
         for (int i = 0; i < items.length; i++) {
             append(items[i]);
         }
         
         return this;
     }
 
     /** rb_ary_pop
      *
      */
     @JRubyMethod(name = "pop")
     public IRubyObject pop() {
         modifyCheck();
         
         if (realLength == 0) return getRuntime().getNil();
 
         if ((flags & SHARED_ARR_F) == 0) {
             int index = begin + --realLength;
             IRubyObject obj = values[index];
             values[index] = null;
             return obj;
         } 
 
         return values[begin + --realLength];
     }
 
     /** rb_ary_shift
      *
      */
     @JRubyMethod(name = "shift")
     public IRubyObject shift() {
         modifyCheck();
 
         if (realLength == 0) return getRuntime().getNil();
 
         IRubyObject obj = values[begin];
 
         flags |= SHARED_ARR_F;
 
         begin++;
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_unshift
      *
      */
     public RubyArray unshift(IRubyObject item) {
         modify();
 
         if (realLength == values.length) {
             int newLength = values.length >> 1;
             if (newLength < ARRAY_DEFAULT_SIZE) newLength = ARRAY_DEFAULT_SIZE;
 
             newLength += values.length;
             realloc(newLength);
         }
         System.arraycopy(values, 0, values, 1, realLength);
 
         realLength++;
         values[0] = item;
 
         return this;
     }
 
     /** rb_ary_unshift_m
      *
      */
     @JRubyMethod(name = "unshift", rest = true)
     public RubyArray unshift_m(IRubyObject[] items) {
         long len = realLength;
 
         if (items.length == 0) return this;
 
         store(len + items.length - 1, getRuntime().getNil());
 
         // it's safe to use zeroes here since modified by store()
         System.arraycopy(values, 0, values, items.length, (int) len);
         System.arraycopy(items, 0, values, 0, items.length);
         
         return this;
     }
 
     /** rb_ary_includes
      * 
      */
     @JRubyMethod(name = "include?", required = 1)
     public RubyBoolean include_p(IRubyObject item) {
         return getRuntime().newBoolean(includes(item));
     }
 
     /** rb_ary_frozen_p
      *
      */
     @JRubyMethod(name = "frozen?")
     public RubyBoolean frozen_p() {
         return getRuntime().newBoolean(isFrozen() || (flags & TMPLOCK_ARR_F) != 0);
     }
 
     /** rb_ary_aref
      */
     @JRubyMethod(name = {"[]", "slice"}, required = 1, optional = 1)
     public IRubyObject aref(IRubyObject[] args) {
         long beg, len;
 
         if(args.length == 1) {
             if (args[0] instanceof RubyFixnum) return entry(((RubyFixnum)args[0]).getLongValue());
             if (args[0] instanceof RubySymbol) throw getRuntime().newTypeError("Symbol as array index");
             
             long[] beglen;
             if (!(args[0] instanceof RubyRange)) {
             } else if ((beglen = ((RubyRange) args[0]).begLen(realLength, 0)) == null) {
                 return getRuntime().getNil();
             } else {
                 beg = beglen[0];
                 len = beglen[1];
                 return subseq(beg, len);
             }
@@ -1190,1241 +1191,1241 @@ public class RubyArray extends RubyObject implements List {
 
     /** rb_ary_reverse_each
      *
      */
     @JRubyMethod(name = "reverse_each", frame = true)
     public IRubyObject reverse_each(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         
         int len = realLength;
         
         while(len-- > 0) {
             block.yield(context, values[begin + len]);
             
             if (realLength < len) len = realLength;
         }
         
         return this;
     }
 
     private IRubyObject inspectJoin(RubyArray tmp, IRubyObject sep) {
         try {
             getRuntime().registerInspecting(this);
             return tmp.join(sep);
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     /** rb_ary_join
      *
      */
     public RubyString join(IRubyObject sep) {
         if (realLength == 0) return getRuntime().newString("");
 
         boolean taint = isTaint() || sep.isTaint();
 
         long len = 1;
         for (int i = begin; i < begin + realLength; i++) {            
             IRubyObject tmp = values[i].checkStringType();
             len += tmp.isNil() ? 10 : ((RubyString) tmp).getByteList().length();
         }
 
         RubyString strSep = null;
         if (!sep.isNil()) {
             sep = strSep = sep.convertToString();
             len += strSep.getByteList().length() * (realLength - 1);
         }
 
         ByteList buf = new ByteList((int)len);
         Ruby runtime = getRuntime();
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject tmp = values[i];
             if (tmp instanceof RubyString) {
                 // do nothing
             } else if (tmp instanceof RubyArray) {
                 if (runtime.isInspecting(tmp)) {
                     tmp = runtime.newString("[...]");
                 } else {
                     tmp = inspectJoin((RubyArray)tmp, sep);
                 }
             } else {
                 tmp = RubyString.objAsString(tmp);
             }
 
             if (i > begin && !sep.isNil()) buf.append(strSep.getByteList());
 
             buf.append(tmp.asString().getByteList());
             if (tmp.isTaint()) taint = true;
         }
 
         RubyString result = runtime.newString(buf); 
 
         if (taint) result.setTaint(true);
 
         return result;
     }
 
     /** rb_ary_join_m
      *
      */
     @JRubyMethod(name = "join", optional = 1)
     public RubyString join_m(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 0, 1);
         IRubyObject sep = (argc == 1) ? args[0] : getRuntime().getGlobalVariables().get("$,");
         
         return join(sep);
     }
 
     /** rb_ary_to_a
      *
      */
     @JRubyMethod(name = "to_a")
     public RubyArray to_a() {
         if(getMetaClass() != getRuntime().getArray()) {
             RubyArray dup = new RubyArray(getRuntime(), getRuntime().isObjectSpaceEnabled());
 
             flags |= SHARED_ARR_F;
             dup.flags |= SHARED_ARR_F;
             dup.values = values;
             dup.realLength = realLength; 
             dup.begin = begin;
             
             return dup;
         }        
         return this;
     }
 
     @JRubyMethod(name = "to_ary")
     public IRubyObject to_ary() {
     	return this;
     }
 
     public RubyArray convertToArray() {
         return this;
     }
     
     public IRubyObject checkArrayType(){
         return this;
     }
 
     /** rb_ary_equal
      *
      */
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
 
         if (!(obj instanceof RubyArray)) {
             if (!obj.respondsTo("to_ary")) {
                 return getRuntime().getFalse();
             } else {
                 return equalInternal(getRuntime().getCurrentContext(), obj, this);
             }
         }
 
         RubyArray ary = (RubyArray) obj;
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (long i = 0; i < realLength; i++) {
             if (!equalInternal(context, elt(i), ary.elt(i)).isTrue()) return runtime.getFalse();            
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_eql
      *
      */
     @JRubyMethod(name = "eql?", required = 1)
     public RubyBoolean eql_p(IRubyObject obj) {
         if (this == obj) return getRuntime().getTrue();
         if (!(obj instanceof RubyArray)) return getRuntime().getFalse();
 
         RubyArray ary = (RubyArray) obj;
 
         if (realLength != ary.realLength) return getRuntime().getFalse();
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < realLength; i++) {
             if (!eqlInternal(context, elt(i), ary.elt(i))) return runtime.getFalse();
         }
         return runtime.getTrue();
     }
 
     /** rb_ary_compact_bang
      *
      */
     @JRubyMethod(name = "compact!")
     public IRubyObject compact_bang() {
         modify();
 
         int p = 0;
         int t = 0;
         int end = p + realLength;
 
         while (t < end) {
             if (values[t].isNil()) {
                 t++;
             } else {
                 values[p++] = values[t++];
             }
         }
 
         if (realLength == p) return getRuntime().getNil();
 
         realloc(p);
         realLength = p;
         return this;
     }
 
     /** rb_ary_compact
      *
      */
     @JRubyMethod(name = "compact")
     public IRubyObject compact() {
         RubyArray ary = aryDup();
         ary.compact_bang();
         return ary;
     }
 
     /** rb_ary_empty_p
      *
      */
     @JRubyMethod(name = "empty?")
     public IRubyObject empty_p() {
         return realLength == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_ary_clear
      *
      */
     @JRubyMethod(name = "clear")
     public IRubyObject rb_clear() {
         modifyCheck();
 
         if((flags & SHARED_ARR_F) != 0){
             alloc(ARRAY_DEFAULT_SIZE);
             flags |= SHARED_ARR_F;
         } else if (values.length > ARRAY_DEFAULT_SIZE << 1){
             alloc(ARRAY_DEFAULT_SIZE << 1);
         }
 
         begin = 0;
         realLength = 0;
         return this;
     }
 
     /** rb_ary_fill
      *
      */
     @JRubyMethod(name = "fill", optional = 3, frame = true)
     public IRubyObject fill(IRubyObject[] args, Block block) {
         IRubyObject item = null;
         IRubyObject begObj = null;
         IRubyObject lenObj = null;
         int argc = args.length;
 
         if (block.isGiven()) {
             Arity.checkArgumentCount(getRuntime(), args, 0, 2);
             item = null;
         	begObj = argc > 0 ? args[0] : null;
         	lenObj = argc > 1 ? args[1] : null;
         	argc++;
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 1, 3);
             item = args[0];
         	begObj = argc > 1 ? args[1] : null;
         	lenObj = argc > 2 ? args[2] : null;
         }
 
         long beg = 0, end = 0, len = 0;
         switch (argc) {
         case 1:
             beg = 0;
             len = realLength;
             break;
         case 2:
             if (begObj instanceof RubyRange) {
                 long[] beglen = ((RubyRange) begObj).begLen(realLength, 1);
                 beg = (int) beglen[0];
                 len = (int) beglen[1];
                 break;
             }
             /* fall through */
         case 3:
             beg = begObj.isNil() ? 0 : RubyNumeric.num2long(begObj);
             if (beg < 0) {
                 beg = realLength + beg;
                 if (beg < 0) beg = 0;
             }
             len = (lenObj == null || lenObj.isNil()) ? realLength - beg : RubyNumeric.num2long(lenObj);
             break;
         }
 
         modify();
 
         end = beg + len;
         if (end > realLength) {
             if (end >= values.length) realloc((int) end);
 
             Arrays.fill(values, realLength, (int) end, getRuntime().getNil());
             realLength = (int) end;
         }
 
         if (block.isGiven()) {
             Ruby runtime = getRuntime();
             ThreadContext context = runtime.getCurrentContext();
             for (int i = (int) beg; i < (int) end; i++) {
                 IRubyObject v = block.yield(context, runtime.newFixnum(i));
                 if (i >= realLength) break;
 
                 values[i] = v;
             }
         } else {
             if(len > 0) Arrays.fill(values, (int) beg, (int) (beg + len), item);
         }
         
         return this;
     }
 
     /** rb_ary_index
      *
      */
     @JRubyMethod(name = "index", required = 1)
     public IRubyObject index(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i = begin; i < begin + realLength; i++) {
             if (equalInternal(context, values[i], obj).isTrue()) return runtime.newFixnum(i - begin);            
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_rindex
      *
      */
     @JRubyMethod(name = "rindex", required = 1)
     public IRubyObject rindex(IRubyObject obj) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         int i = realLength;
 
         while (i-- > 0) {
             if (i > realLength) {
                 i = realLength;
                 continue;
             }
             if (equalInternal(context, values[begin + i], obj).isTrue()) return getRuntime().newFixnum(i);
         }
 
         return runtime.getNil();
     }
 
     /** rb_ary_indexes
      * 
      */
     @JRubyMethod(name = {"indexes", "indices"}, required = 1, rest = true)
     public IRubyObject indexes(IRubyObject[] args) {
         getRuntime().getWarnings().warn("Array#indexes is deprecated; use Array#values_at");
 
         RubyArray ary = new RubyArray(getRuntime(), args.length);
 
         IRubyObject[] arefArgs = new IRubyObject[1];
         for (int i = 0; i < args.length; i++) {
             arefArgs[0] = args[i];
             ary.append(aref(arefArgs));
         }
 
         return ary;
     }
 
     /** rb_ary_reverse_bang
      *
      */
     @JRubyMethod(name = "reverse!")
     public IRubyObject reverse_bang() {
         modify();
 
         IRubyObject tmp;
         if (realLength > 1) {
             int p1 = 0;
             int p2 = p1 + realLength - 1;
 
             while (p1 < p2) {
                 tmp = values[p1];
                 values[p1++] = values[p2];
                 values[p2--] = tmp;
             }
         }
         return this;
     }
 
     /** rb_ary_reverse_m
      *
      */
     @JRubyMethod(name = "reverse")
     public IRubyObject reverse() {
         return aryDup().reverse_bang();
     }
 
     /** rb_ary_collect
      *
      */
     @JRubyMethod(name = {"collect", "map"}, frame = true)
     public RubyArray collect(Block block) {
         Ruby runtime = getRuntime();
         
         if (!block.isGiven()) return new RubyArray(getRuntime(), runtime.getArray(), this);
         
         ThreadContext context = runtime.getCurrentContext();
         RubyArray collect = new RubyArray(runtime, realLength);
         
         for (int i = begin; i < begin + realLength; i++) {
             collect.append(block.yield(context, values[i]));
         }
         
         return collect;
     }
 
     /** rb_ary_collect_bang
      *
      */
     @JRubyMethod(name = {"collect!", "map!"}, frame = true)
     public RubyArray collect_bang(Block block) {
         modify();
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i = 0, len = realLength; i < len; i++) {
             store(i, block.yield(context, values[begin + i]));
         }
         return this;
     }
 
     /** rb_ary_select
      *
      */
     @JRubyMethod(name = "select", frame = true)
     public RubyArray select(Block block) {
         Ruby runtime = getRuntime();
         RubyArray result = new RubyArray(runtime, realLength);
 
         ThreadContext context = runtime.getCurrentContext();
         if ((flags & SHARED_ARR_F) != 0) {
             for (int i = begin; i < begin + realLength; i++) {
                 if (block.yield(context, values[i]).isTrue()) result.append(elt(i - begin));
             }
         } else {
             for (int i = 0; i < realLength; i++) {
                 if (block.yield(context, values[i]).isTrue()) result.append(elt(i));
             }
         }
         return result;
     }
 
     /** rb_ary_delete
      *
      */
     @JRubyMethod(name = "delete", required = 1, frame = true)
     public IRubyObject delete(IRubyObject item, Block block) {
         int i2 = 0;
 
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject e = values[begin + i1];
             if (equalInternal(context, e, item).isTrue()) continue;
             if (i1 != i2) store(i2, e);
             i2++;
         }
         
         if (realLength == i2) {
             if (block.isGiven()) return block.yield(context, item);
 
             return runtime.getNil();
         }
 
         modify();
 
         if (realLength > i2) {
             realLength = i2;
             if (i2 << 1 < values.length && values.length > ARRAY_DEFAULT_SIZE) realloc(i2 << 1);
         }
         return item;
     }
 
     /** rb_ary_delete_at
      *
      */
     private final IRubyObject delete_at(int pos) {
         int len = realLength;
 
         if (pos >= len) return getRuntime().getNil();
 
         if (pos < 0) pos += len;
 
         if (pos < 0) return getRuntime().getNil();
 
         modify();
 
         IRubyObject obj = values[pos];
         System.arraycopy(values, pos + 1, values, pos, len - (pos + 1));
 
         realLength--;
 
         return obj;
     }
 
     /** rb_ary_delete_at_m
      * 
      */
     @JRubyMethod(name = "delete_at", required = 1)
     public IRubyObject delete_at(IRubyObject obj) {
         return delete_at((int) RubyNumeric.num2long(obj));
     }
 
     /** rb_ary_reject_bang
      * 
      */
     @JRubyMethod(name = "reject", frame = true)
     public IRubyObject reject(Block block) {
         RubyArray ary = aryDup();
         ary.reject_bang(block);
         return ary;
     }
 
     /** rb_ary_reject_bang
      *
      */
     @JRubyMethod(name = "reject!", frame = true)
     public IRubyObject reject_bang(Block block) {
         int i2 = 0;
         modify();
 
         ThreadContext context = getRuntime().getCurrentContext();
         for (int i1 = 0; i1 < realLength; i1++) {
             IRubyObject v = values[i1];
             if (block.yield(context, v).isTrue()) continue;
 
             if (i1 != i2) store(i2, v);
             i2++;
         }
         if (realLength == i2) return getRuntime().getNil();
 
         if (i2 < realLength) realLength = i2;
 
         return this;
     }
 
     /** rb_ary_delete_if
      *
      */
     @JRubyMethod(name = "delete_if", frame = true)
     public IRubyObject delete_if(Block block) {
         reject_bang(block);
         return this;
     }
 
     /** rb_ary_zip
      * 
      */
     @JRubyMethod(name = "zip", optional = 1, rest = true, frame = true)
     public IRubyObject zip(IRubyObject[] args, Block block) {
         for (int i = 0; i < args.length; i++) {
             args[i] = args[i].convertToArray();
         }
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         if (block.isGiven()) {
             for (int i = 0; i < realLength; i++) {
                 RubyArray tmp = new RubyArray(runtime, args.length + 1);
                 tmp.append(elt(i));
                 for (int j = 0; j < args.length; j++) {
                     tmp.append(((RubyArray) args[j]).elt(i));
                 }
                 block.yield(context, tmp);
             }
             return runtime.getNil();
         }
         
         int len = realLength;
         RubyArray result = new RubyArray(runtime, len);
         for (int i = 0; i < len; i++) {
             RubyArray tmp = new RubyArray(runtime, args.length + 1);
             tmp.append(elt(i));
             for (int j = 0; j < args.length; j++) {
                 tmp.append(((RubyArray) args[j]).elt(i));
             }
             result.append(tmp);
         }
         return result;
     }
 
     /** rb_ary_cmp
      *
      */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(IRubyObject obj) {
         RubyArray ary2 = obj.convertToArray();
 
         int len = realLength;
 
         if (len > ary2.realLength) len = ary2.realLength;
 
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < len; i++) {
             IRubyObject v = elt(i).callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", ary2.elt(i));
             if (!(v instanceof RubyFixnum) || ((RubyFixnum) v).getLongValue() != 0) return v;
         }
         len = realLength - ary2.realLength;
 
         if (len == 0) return RubyFixnum.zero(runtime);
         if (len > 0) return RubyFixnum.one(runtime);
 
         return RubyFixnum.minus_one(runtime);
     }
 
     /** rb_ary_slice_bang
      *
      */
     @JRubyMethod(name = "slice!", required = 1, optional = 2)
     public IRubyObject slice_bang(IRubyObject[] args) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
             long pos = RubyNumeric.num2long(args[0]);
             long len = RubyNumeric.num2long(args[1]);
             
             if (pos < 0) pos = realLength + pos;
 
             args[1] = subseq(pos, len);
             splice(pos, len, null);
             
             return args[1];
         }
         
         IRubyObject arg = args[0];
         if (arg instanceof RubyRange) {
             long[] beglen = ((RubyRange) arg).begLen(realLength, 1);
             long pos = beglen[0];
             long len = beglen[1];
 
             if (pos < 0) {
                 pos = realLength + pos;
             }
             arg = subseq(pos, len);
             splice(pos, len, null);
             return arg;
         }
 
         return delete_at((int) RubyNumeric.num2long(args[0]));
     }
 
     /** rb_ary_assoc
      *
      */
     @JRubyMethod(name = "assoc", required = 1)
     public IRubyObject assoc(IRubyObject key) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray && ((RubyArray) v).realLength > 0
                 && equalInternal(context, ((RubyArray) v).values[0], key).isTrue()) {
                 return v;
             }
         }
         return runtime.getNil();
     }
 
     /** rb_ary_rassoc
      *
      */
     @JRubyMethod(name = "rassoc", required = 1)
     public IRubyObject rassoc(IRubyObject value) {
         Ruby runtime = getRuntime();
         final ThreadContext context = runtime.getCurrentContext();
 
         for (int i = begin; i < begin + realLength; i++) {
             IRubyObject v = values[i];
             if (v instanceof RubyArray && ((RubyArray) v).realLength > 1
                     && equalInternal(context, ((RubyArray) v).values[1], value).isTrue()) {
                 return v;
             }
         }
 
         return runtime.getNil();
     }
 
     /** flatten
      * 
      */
     private final int flatten(int index, RubyArray ary2, RubyArray memo) {
         int i = index;
         int n;
         int lim = index + ary2.realLength;
 
         IRubyObject id = ary2.id();
 
         if (memo.includes(id)) throw getRuntime().newArgumentError("tried to flatten recursive array");
 
         memo.append(id);
         splice(index, 1, ary2);
         while (i < lim) {
             IRubyObject tmp = elt(i).checkArrayType();
             if (!tmp.isNil()) {
                 n = flatten(i, (RubyArray) tmp, memo);
                 i += n;
                 lim += n;
             }
             i++;
         }
         memo.pop();
         return lim - index - 1; /* returns number of increased items */
     }
 
     /** rb_ary_flatten_bang
      *
      */
     @JRubyMethod(name = "flatten!")
     public IRubyObject flatten_bang() {
         int i = 0;
         RubyArray memo = null;
 
         while (i < realLength) {
             IRubyObject ary2 = values[begin + i];
             IRubyObject tmp = ary2.checkArrayType();
             if (!tmp.isNil()) {
                 if (memo == null) {
                     memo = new RubyArray(getRuntime(), false);
                     memo.values = reserve(ARRAY_DEFAULT_SIZE);
                 }
 
                 i += flatten(i, (RubyArray) tmp, memo);
             }
             i++;
         }
         if (memo == null) return getRuntime().getNil();
 
         return this;
     }
 
     /** rb_ary_flatten
      *
      */
     @JRubyMethod(name = "flatten")
     public IRubyObject flatten() {
         RubyArray ary = aryDup();
         ary.flatten_bang();
         return ary;
     }
 
     /** rb_ary_nitems
      *
      */
     @JRubyMethod(name = "nitems")
     public IRubyObject nitems() {
         int n = 0;
 
         for (int i = begin; i < begin + realLength; i++) {
             if (!values[i].isNil()) n++;
         }
         
         return getRuntime().newFixnum(n);
     }
 
     /** rb_ary_plus
      *
      */
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject obj) {
         RubyArray y = obj.convertToArray();
         int len = realLength + y.realLength;
         RubyArray z = new RubyArray(getRuntime(), len);
         System.arraycopy(values, begin, z.values, 0, realLength);
         System.arraycopy(y.values, y.begin, z.values, realLength, y.realLength);
         z.realLength = len;
         return z;
     }
 
     /** rb_ary_times
      *
      */
     @JRubyMethod(name = "*", required = 1)
     public IRubyObject op_times(IRubyObject times) {
         IRubyObject tmp = times.checkStringType();
 
         if (!tmp.isNil()) return join(tmp);
 
         long len = RubyNumeric.num2long(times);
         if (len == 0) return new RubyArray(getRuntime(), getMetaClass(), 0);
         if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
         if (Long.MAX_VALUE / len < realLength) {
             throw getRuntime().newArgumentError("argument too big");
         }
 
         len *= realLength;
 
         RubyArray ary2 = new RubyArray(getRuntime(), getMetaClass(), len);
         ary2.realLength = (int) len;
 
         for (int i = 0; i < len; i += realLength) {
             System.arraycopy(values, begin, ary2.values, i, realLength);
         }
 
         ary2.infectBy(this);
 
         return ary2;
     }
 
     /** ary_make_hash
      * 
      */
     private final RubyHash makeHash(RubyArray ary2) {
         RubyHash hash = new RubyHash(getRuntime(), false);
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             hash.fastASet(values[i], NEVER);
         }
 
         if (ary2 != null) {
             begin = ary2.begin;            
             for (int i = begin; i < begin + ary2.realLength; i++) {
                 hash.fastASet(ary2.values[i], NEVER);
             }
         }
         return hash;
     }
 
     /** rb_ary_uniq_bang
      *
      */
     @JRubyMethod(name = "uniq!")
     public IRubyObject uniq_bang() {
         RubyHash hash = makeHash(null);
 
         if (realLength == hash.size()) return getRuntime().getNil();
 
         int j = 0;
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (hash.fastDelete(v) != null) store(j++, v);
         }
         realLength = j;
         return this;
     }
 
     /** rb_ary_uniq
      *
      */
     @JRubyMethod(name = "uniq")
     public IRubyObject uniq() {
         RubyArray ary = aryDup();
         ary.uniq_bang();
         return ary;
     }
 
     /** rb_ary_diff
      *
      */
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_diff(IRubyObject other) {
         RubyHash hash = other.convertToArray().makeHash(null);
         RubyArray ary3 = new RubyArray(getRuntime());
 
         int begin = this.begin;
         for (int i = begin; i < begin + realLength; i++) {
             if (hash.fastARef(values[i]) != null) continue;
             ary3.append(elt(i - begin));
         }
 
         return ary3;
     }
 
     /** rb_ary_and
      *
      */
     @JRubyMethod(name = "&", required = 1)
     public IRubyObject op_and(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         RubyHash hash = ary2.makeHash(null);
         RubyArray ary3 = new RubyArray(getRuntime(), 
                 realLength < ary2.realLength ? realLength : ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (hash.fastDelete(v) != null) ary3.append(v);
         }
 
         return ary3;
     }
 
     /** rb_ary_or
      *
      */
     @JRubyMethod(name = "|", required = 1)
     public IRubyObject op_or(IRubyObject other) {
         RubyArray ary2 = other.convertToArray();
         RubyHash set = makeHash(ary2);
 
         RubyArray ary3 = new RubyArray(getRuntime(), realLength + ary2.realLength);
 
         for (int i = 0; i < realLength; i++) {
             IRubyObject v = elt(i);
             if (set.fastDelete(v) != null) ary3.append(v);
         }
         for (int i = 0; i < ary2.realLength; i++) {
             IRubyObject v = ary2.elt(i);
             if (set.fastDelete(v) != null) ary3.append(v);
         }
         return ary3;
     }
 
     /** rb_ary_sort
      *
      */
     @JRubyMethod(name = "sort", frame = true)
     public RubyArray sort(Block block) {
         RubyArray ary = aryDup();
         ary.sort_bang(block);
         return ary;
     }
 
     /** rb_ary_sort_bang
      *
      */
     @JRubyMethod(name = "sort!", frame = true)
     public RubyArray sort_bang(Block block) {
         modify();
         if (realLength > 1) {
             flags |= TMPLOCK_ARR_F;
             try {
                 if (block.isGiven()) {
                     Arrays.sort(values, 0, realLength, new BlockComparator(block));
                 } else {
                     Arrays.sort(values, 0, realLength, new DefaultComparator());
                 }
             } finally {
                 flags &= ~TMPLOCK_ARR_F;
             }
         }
         return this;
     }
 
     final class BlockComparator implements Comparator {
         private Block block;
 
         public BlockComparator(Block block) {
             this.block = block;
         }
 
         public int compare(Object o1, Object o2) {
             ThreadContext context = getRuntime().getCurrentContext();
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
             IRubyObject ret = block.yield(context, getRuntime().newArray(obj1, obj2), null, null, true);
             int n = RubyComparable.cmpint(ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
     final class DefaultComparator implements Comparator {
         public int compare(Object o1, Object o2) {
             if (o1 instanceof RubyFixnum && o2 instanceof RubyFixnum) {
                 long a = ((RubyFixnum) o1).getLongValue();
                 long b = ((RubyFixnum) o2).getLongValue();
                 if (a > b) return 1;
                 if (a < b) return -1;
                 return 0;
             }
             if (o1 instanceof RubyString && o2 instanceof RubyString) {
                 return ((RubyString) o1).op_cmp((RubyString) o2);
             }
 
             IRubyObject obj1 = (IRubyObject) o1;
             IRubyObject obj2 = (IRubyObject) o2;
 
             IRubyObject ret = obj1.callMethod(obj1.getRuntime().getCurrentContext(), MethodIndex.OP_SPACESHIP, "<=>", obj2);
             int n = RubyComparable.cmpint(ret, obj1, obj2);
             //TODO: ary_sort_check should be done here
             return n;
         }
     }
 
     public static void marshalTo(RubyArray array, MarshalStream output) throws IOException {
         output.writeInt(array.getList().size());
         for (Iterator iter = array.getList().iterator(); iter.hasNext();) {
             output.dumpObject((IRubyObject) iter.next());
         }
     }
 
     public static RubyArray unmarshalFrom(UnmarshalStream input) throws IOException {
         RubyArray result = input.getRuntime().newArray();
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             result.append(input.unmarshalObject());
         }
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#pack
      */
     @JRubyMethod(name = "pack", required = 1)
     public RubyString pack(IRubyObject obj) {
         RubyString iFmt = RubyString.objAsString(obj);
         return Pack.pack(getRuntime(), this, iFmt.getByteList());
     }
 
     public Class getJavaClass() {
         return List.class;
     }
 
     // Satisfy java.util.List interface (for Java integration)
-	public int size() {
+    public int size() {
         return realLength;
     }
 
     public boolean isEmpty() {
         return realLength == 0;
     }
 
     public boolean contains(Object element) {
         return indexOf(element) != -1;
     }
 
     public Object[] toArray() {
         Object[] array = new Object[realLength];
         for (int i = begin; i < realLength; i++) {
             array[i - begin] = JavaUtil.convertRubyToJava(values[i]);
         }
         return array;
     }
 
     public Object[] toArray(final Object[] arg) {
         Object[] array = arg;
         if (array.length < realLength) {
             Class type = array.getClass().getComponentType();
             array = (Object[]) Array.newInstance(type, realLength);
         }
         int length = realLength - begin;
 
         for (int i = 0; i < length; i++) {
             array[i] = JavaUtil.convertRubyToJava(values[i + begin]);
         }
         return array;
     }
 
     public boolean add(Object element) {
         append(JavaUtil.convertJavaToRuby(getRuntime(), element));
         return true;
     }
 
     public boolean remove(Object element) {
         IRubyObject deleted = delete(JavaUtil.convertJavaToRuby(getRuntime(), element), Block.NULL_BLOCK);
         return deleted.isNil() ? false : true; // TODO: is this correct ?
     }
 
     public boolean containsAll(Collection c) {
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             if (indexOf(iter.next()) == -1) {
                 return false;
             }
         }
 
         return true;
     }
 
     public boolean addAll(Collection c) {
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             add(iter.next());
         }
         return !c.isEmpty();
     }
 
     public boolean addAll(int index, Collection c) {
         Iterator iter = c.iterator();
         for (int i = index; iter.hasNext(); i++) {
             add(i, iter.next());
         }
         return !c.isEmpty();
     }
 
     public boolean removeAll(Collection c) {
         boolean listChanged = false;
         for (Iterator iter = c.iterator(); iter.hasNext();) {
             if (remove(iter.next())) {
                 listChanged = true;
             }
         }
         return listChanged;
     }
 
     public boolean retainAll(Collection c) {
         boolean listChanged = false;
 
         for (Iterator iter = iterator(); iter.hasNext();) {
             Object element = iter.next();
             if (!c.contains(element)) {
                 remove(element);
                 listChanged = true;
             }
         }
         return listChanged;
     }
 
     public Object get(int index) {
         return JavaUtil.convertRubyToJava((IRubyObject) elt(index), Object.class);
     }
 
     public Object set(int index, Object element) {
         return store(index, JavaUtil.convertJavaToRuby(getRuntime(), element));
     }
 
     // TODO: make more efficient by not creating IRubyArray[]
     public void add(int index, Object element) {
         insert(new IRubyObject[]{RubyFixnum.newFixnum(getRuntime(), index), JavaUtil.convertJavaToRuby(getRuntime(), element)});
     }
 
     public Object remove(int index) {
         return JavaUtil.convertRubyToJava(delete_at(index), Object.class);
     }
 
     public int indexOf(Object element) {
         int begin = this.begin;
 
         if (element == null) {
             for (int i = begin; i < begin + realLength; i++) {
                 if (values[i] == null) {
                     return i;
                 }
             }
         } else {
             IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
 
             for (int i = begin; i < begin + realLength; i++) {
                 if (convertedElement.equals(values[i])) {
                     return i;
                 }
             }
         }
         return -1;
     }
 
     public int lastIndexOf(Object element) {
         int begin = this.begin;
 
         if (element == null) {
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (values[i] == null) {
                     return i;
                 }
             }
         } else {
             IRubyObject convertedElement = JavaUtil.convertJavaToRuby(getRuntime(), element);
 
             for (int i = begin + realLength - 1; i >= begin; i--) {
                 if (convertedElement.equals(values[i])) {
                     return i;
                 }
             }
         }
 
         return -1;
     }
 
     public class RubyArrayConversionIterator implements Iterator {
         protected int index = 0;
         protected int last = -1;
 
         public boolean hasNext() {
             return index < realLength;
         }
 
         public Object next() {
             IRubyObject element = elt(index);
             last = index++;
             return JavaUtil.convertRubyToJava(element, Object.class);
         }
 
         public void remove() {
             if (last == -1) throw new IllegalStateException();
 
             delete_at(last);
             if (last < index) index--;
 
             last = -1;
 	
         }
     }
 
     public Iterator iterator() {
         return new RubyArrayConversionIterator();
     }
 
     final class RubyArrayConversionListIterator extends RubyArrayConversionIterator implements ListIterator {
         public RubyArrayConversionListIterator() {
         }
 
         public RubyArrayConversionListIterator(int index) {
             this.index = index;
 		}
 
 		public boolean hasPrevious() {
             return index >= 0;
 		}
 
 		public Object previous() {
             return JavaUtil.convertRubyToJava((IRubyObject) elt(last = --index), Object.class);
 		}
 
 		public int nextIndex() {
             return index;
 		}
 
 		public int previousIndex() {
             return index - 1;
 		}
 
         public void set(Object obj) {
             if (last == -1) throw new IllegalStateException();
 
             store(last, JavaUtil.convertJavaToRuby(getRuntime(), obj));
         }
 
         public void add(Object obj) {
             insert(new IRubyObject[] { RubyFixnum.newFixnum(getRuntime(), index++), JavaUtil.convertJavaToRuby(getRuntime(), obj) });
             last = -1;
 		}
     }
 
     public ListIterator listIterator() {
         return new RubyArrayConversionListIterator();
     }
 
     public ListIterator listIterator(int index) {
         return new RubyArrayConversionListIterator(index);
 	}
 
     // TODO: list.subList(from, to).clear() is supposed to clear the sublist from the list.
     // How can we support this operation?
     public List subList(int fromIndex, int toIndex) {
         if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
             throw new IndexOutOfBoundsException();
         }
         
         IRubyObject subList = subseq(fromIndex, toIndex - fromIndex + 1);
 
         return subList.isNil() ? null : (List) subList;
     }
 
     public void clear() {
         rb_clear();
     }
 }
diff --git a/src/org/jruby/RubyFixnum.java b/src/org/jruby/RubyFixnum.java
index bc8fb10ef7..80da8f013d 100644
--- a/src/org/jruby/RubyFixnum.java
+++ b/src/org/jruby/RubyFixnum.java
@@ -1,701 +1,701 @@
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2006 Antti Karanta <antti.karanta@napa.fi>
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
 package org.jruby;
 
 import java.math.BigInteger;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.Convert;
 
 /** 
  * Implementation of the Fixnum class.
  */
 public class RubyFixnum extends RubyInteger {
     
     public static RubyClass createFixnumClass(Ruby runtime) {
         RubyClass fixnum = runtime.defineClass("Fixnum", runtime.getInteger(),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setFixnum(fixnum);
         fixnum.index = ClassIndex.FIXNUM;
         fixnum.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyFixnum;
                 }
             };
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyFixnum.class);
 
         fixnum.includeModule(runtime.getPrecision());
         
         fixnum.defineAnnotatedMethods(RubyFixnum.class);
         
         fixnum.dispatcher = callbackFactory.createDispatcher(fixnum);
         
         for (int i = 0; i < runtime.fixnumCache.length; i++) {
             runtime.fixnumCache[i] = new RubyFixnum(runtime, fixnum, i - 128);
         }
 
         return fixnum;
     }    
     
     private final long value;
     private static final int BIT_SIZE = 64;
     public static final long SIGN_BIT = (1L << (BIT_SIZE - 1));
     public static final long MAX = (1L<<(BIT_SIZE - 1)) - 1;
     public static final long MIN = -1 * MAX - 1;
     public static final long MAX_MARSHAL_FIXNUM = (1L << 30) - 1; // 0x3fff_ffff
     public static final long MIN_MARSHAL_FIXNUM = - (1L << 30);   // -0x4000_0000
 
     public RubyFixnum(Ruby runtime) {
         this(runtime, 0);
     }
 
     public RubyFixnum(Ruby runtime, long value) {
         super(runtime, runtime.getFixnum(), false);
         this.value = value;
     }
     
     private RubyFixnum(Ruby runtime, RubyClass klazz, long value) {
         super(runtime, klazz, false);
         this.value = value;
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.FIXNUM;
     }
     
     /** 
      * short circuit for Fixnum key comparison
      */
     public final boolean eql(IRubyObject other) {
         return other instanceof RubyFixnum && value == ((RubyFixnum)other).value;
     }
     
     public boolean isImmediate() {
     	return true;
     }
     
     public RubyClass getSingletonClass() {
         throw getRuntime().newTypeError("can't define singleton");
     }
 
     public Class<?> getJavaClass() {
         return Long.TYPE;
     }
 
     public double getDoubleValue() {
         return value;
     }
 
     public long getLongValue() {
         return value;
     }
 
     public static RubyFixnum newFixnum(Ruby runtime, long value) {
         final int offset = 128;
         if (value <= 127 && value >= -128) {
             return runtime.fixnumCache[(int) value + offset];
         }
         return new RubyFixnum(runtime, value);
     }
 
     public RubyFixnum newFixnum(long newValue) {
         return newFixnum(getRuntime(), newValue);
     }
 
     public static RubyFixnum zero(Ruby runtime) {
         return newFixnum(runtime, 0);
     }
 
     public static RubyFixnum one(Ruby runtime) {
         return newFixnum(runtime, 1);
     }
 
     public static RubyFixnum minus_one(Ruby runtime) {
         return newFixnum(runtime, -1);
     }
 
     public RubyFixnum hash() {
         return newFixnum(hashCode());
     }
 
     public final int hashCode() {
         return (int)(value ^ value >>> 32);
     }
 
     public boolean equals(Object other) {
         if (other == this) {
             return true;
         }
         
         if (other instanceof RubyFixnum) { 
             RubyFixnum num = (RubyFixnum)other;
             
             if (num.value == value) {
                 return true;
             }
         }
         
         return false;
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** fix_to_s
      * 
      */
     @JRubyMethod(name = "to_s", optional = 1)
     public RubyString to_s(IRubyObject[] args) {
         int base = args.length == 0 ? 10 : num2int(args[0]);
         if (base < 2 || base > 36) {
             throw getRuntime().newArgumentError("illegal radix " + base);
             }
         return getRuntime().newString(Convert.longToByteList(value, base));
         }
 
     /** fix_id2name
      * 
      */
     @JRubyMethod(name = "id2name")
     public IRubyObject id2name() {
         RubySymbol symbol = RubySymbol.getSymbolLong(getRuntime(), value);
         
         if (symbol != null) return getRuntime().newString(symbol.asSymbol());
 
         return getRuntime().getNil();
     }
 
     /** fix_to_sym
      * 
      */
     @JRubyMethod(name = "to_sym")
     public IRubyObject to_sym() {
         RubySymbol symbol = RubySymbol.getSymbolLong(getRuntime(), value);
         
         return symbol != null ? symbol : getRuntime().getNil(); 
     }
 
     /** fix_uminus
      * 
      */
     @JRubyMethod(name = "-@")
     public IRubyObject op_uminus() {
         if (value == MIN) { // a gotcha
             return RubyBignum.newBignum(getRuntime(), BigInteger.valueOf(value).negate());
         }
         return RubyFixnum.newFixnum(getRuntime(), -value);
         }
 
     /** fix_plus
      * 
      */
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             long result = value + otherValue;
             if ((~(value ^ otherValue) & (value ^ result) & SIGN_BIT) != 0) {
                 return RubyBignum.newBignum(getRuntime(), value).op_plus(other);
             }
 		return newFixnum(result);
     }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_plus(this);
     }
         if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value + ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("+", other);
     }
 
     /** fix_minus
      * 
      */
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_minus(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             long result = value - otherValue;
             if ((~(value ^ ~otherValue) & (value ^ result) & SIGN_BIT) != 0) {
                 return RubyBignum.newBignum(getRuntime(), value).op_minus(other);
     }
             return newFixnum(result);
         } else if (other instanceof RubyBignum) {
             return RubyBignum.newBignum(getRuntime(), value).op_minus(other);
         } else if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value - ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("-", other);
     }
 
     /** fix_mul
      * 
      */
     @JRubyMethod(name = "*", required = 1)
     public IRubyObject op_mul(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             if (value == 0) {
                 return RubyFixnum.zero(getRuntime());
             }
             long result = value * otherValue;
             IRubyObject r = newFixnum(getRuntime(),result);
             if(RubyNumeric.fix2long(r) != result || result/value != otherValue) {
                 return (RubyNumeric) RubyBignum.newBignum(getRuntime(), value).op_mul(other);
             }
             return r;
         } else if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_mul(this);
         } else if (other instanceof RubyFloat) {
             return getRuntime().newFloat((double) value * ((RubyFloat) other).getDoubleValue());
         }
         return coerceBin("*", other);
     }
     
     /** fix_div
      * here is terrible MRI gotcha:
      * 1.div 3.0 -> 0
      * 1 / 3.0   -> 0.3333333333333333
      * 
      * MRI is also able to do it in one place by looking at current frame in rb_num_coerce_bin:
      * rb_funcall(x, ruby_frame->orig_func, 1, y);
      * 
      * also note that RubyFloat doesn't override Numeric.div
      */
     @JRubyMethod(name = "div", required = 1)
     public IRubyObject div_div(IRubyObject other) {
         return idiv(other, "div");
     }
     	
     @JRubyMethod(name = "/", required = 1)
     public IRubyObject op_div(IRubyObject other) {
         return idiv(other, "/");
     }
 
     public IRubyObject idiv(IRubyObject other, String method) {
         if (other instanceof RubyFixnum) {
             long x = value;
             long y = ((RubyFixnum) other).value;
             
             if (y == 0) {
                 throw getRuntime().newZeroDivisionError();
             }
             
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
             }
 
             return getRuntime().newFixnum(div);
         } 
         return coerceBin(method, other);
     }
         
     /** fix_mod
      * 
      */
-    @JRubyMethod(name = "%", required = 1, alias = "modulo")
+    @JRubyMethod(name = {"%", "modulo"}, required = 1)
     public IRubyObject op_mod(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             // Java / and % are not the same as ruby
             long x = value;
             long y = ((RubyFixnum) other).value;
 
             if (y == 0) {
             	throw getRuntime().newZeroDivisionError();
     }
 
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 mod += y;
             }
                 
             return getRuntime().newFixnum(mod);
 	            }
         return coerceBin("%", other);
     }
                 
     /** fix_divmod
      * 
      */
     @JRubyMethod(name = "divmod", required = 1)
     public IRubyObject divmod(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long x = value;
             long y = ((RubyFixnum) other).value;
             final Ruby runtime = getRuntime();
 
             if (y == 0) {
                 throw runtime.newZeroDivisionError();
                 }
 
             long div = x / y;
             long mod = x % y;
 
             if (mod < 0 && y > 0 || mod > 0 && y < 0) {
                 div -= 1;
                 mod += y;
             }
 
             IRubyObject fixDiv = RubyFixnum.newFixnum(getRuntime(), div);
             IRubyObject fixMod = RubyFixnum.newFixnum(getRuntime(), mod);
 
             return RubyArray.newArray(runtime, fixDiv, fixMod);
 
     	}
         return coerceBin("divmod", other);
     }
     	
     /** fix_quo
      * 
      */
     @JRubyMethod(name = "quo", required = 1)
     public IRubyObject quo(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyFloat.newFloat(getRuntime(), (double) value
                     / (double) ((RubyFixnum) other).value);
     }
         return coerceBin("quo", other);
 	            }
 
     /** fix_pow 
      * 
      */
     @JRubyMethod(name = "**", required = 1)
     public IRubyObject op_pow(IRubyObject other) {
         if(other instanceof RubyFixnum) {
             long b = ((RubyFixnum) other).value;
             if (b == 0) {
                 return RubyFixnum.one(getRuntime());
             }
             if (b == 1) {
                 return this;
             }
             if (b > 0) {
                 return RubyBignum.newBignum(getRuntime(), value).op_pow(other);
             }
             return RubyFloat.newFloat(getRuntime(), Math.pow(value, b));
         } else if (other instanceof RubyFloat) {
             return RubyFloat.newFloat(getRuntime(), Math.pow(value, ((RubyFloat) other)
                     .getDoubleValue()));
         }
         return coerceBin("**", other);
     }
             
     /** fix_abs
      * 
      */
     @JRubyMethod(name = "abs")
     public IRubyObject abs() {
         if (value < 0) {
             return RubyFixnum.newFixnum(getRuntime(), -value);
         }
         return this;
     }
             
     /** fix_equal
      * 
      */
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value == ((RubyFixnum) other).value);
         }
         return super.op_equal(other);
             }
 
     /** fix_cmp
      * 
      */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long otherValue = ((RubyFixnum) other).value;
             if (value == otherValue) {
                 return RubyFixnum.zero(getRuntime());
     }
             if (value > otherValue) {
                 return RubyFixnum.one(getRuntime());
             }
             return RubyFixnum.minus_one(getRuntime());
         }
         return coerceCmp("<=>", other);
     }
 
     /** fix_gt
      * 
      */
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value > ((RubyFixnum) other).value);
     }
         return coerceRelOp(">", other);
     }
 
     /** fix_ge
      * 
      */
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value >= ((RubyFixnum) other).value);
             }
         return coerceRelOp(">=", other);
     }
 
     /** fix_lt
      * 
      */
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value < ((RubyFixnum) other).value);
         }
         return coerceRelOp("<", other);
     }
         
     /** fix_le
      * 
      */
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return RubyBoolean.newBoolean(getRuntime(), value <= ((RubyFixnum) other).value);
     }
         return coerceRelOp("<=", other);
     }
 
     /** fix_rev
      * 
      */
     @JRubyMethod(name = "~")
     public IRubyObject op_neg() {
         return newFixnum(~value);
     	}
     	
     /** fix_and
      * 
      */
     @JRubyMethod(name = "&", required = 1)
     public IRubyObject op_and(IRubyObject other) {
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_and(this);
     }
         return RubyFixnum.newFixnum(getRuntime(), value & num2long(other));
     }
 
     /** fix_or 
      * 
      */
     @JRubyMethod(name = "|", required = 1)
     public IRubyObject op_or(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             return newFixnum(value | ((RubyFixnum) other).value);
         }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_or(this);
         }
         if (other instanceof RubyNumeric) {
             return newFixnum(value | ((RubyNumeric) other).getLongValue());
         }
         
         return op_or(RubyFixnum.newFixnum(getRuntime(), num2long(other)));
     }
 
     /** fix_xor 
      * 
      */
     @JRubyMethod(name = "^", required = 1)
     public IRubyObject op_xor(IRubyObject other) {
         if(other instanceof RubyFixnum) {
             return newFixnum(value ^ ((RubyFixnum) other).value);
             }
         if (other instanceof RubyBignum) {
             return ((RubyBignum) other).op_xor(this);
         }
         if (other instanceof RubyNumeric) {
             return newFixnum(value ^ ((RubyNumeric) other).getLongValue());
         }
 
         return op_xor(RubyFixnum.newFixnum(getRuntime(), num2long(other)));
         }
 
     /** fix_aref 
      * 
      */
     @JRubyMethod(name = "[]", required = 1)
     public IRubyObject op_aref(IRubyObject other) {
         if(other instanceof RubyBignum) {
             RubyBignum big = (RubyBignum) other;
             RubyObject tryFix = RubyBignum.bignorm(getRuntime(), big.getValue());
             if (!(tryFix instanceof RubyFixnum)) {
                 if (big.getValue().signum() == 0 || value >= 0) {
                     return RubyFixnum.zero(getRuntime());
         }
                 return RubyFixnum.one(getRuntime());
         }
     }
 
         long otherValue = num2long(other);
             
         if (otherValue < 0) {
             return RubyFixnum.zero(getRuntime());
 		    } 
 		      
         if (BIT_SIZE - 1 < otherValue) {
             if (value < 0) {
                 return RubyFixnum.one(getRuntime());
         }
             return RubyFixnum.zero(getRuntime());
         }
         
         return (value & (1L << otherValue)) == 0 ? RubyFixnum.zero(getRuntime()) : RubyFixnum.one(getRuntime());
     }
 
     /** fix_lshift 
      * 
      */
     @JRubyMethod(name = "<<", required = 1)
     public IRubyObject op_lshift(IRubyObject other) {
         long width = num2long(other);
 
             if (width < 0) {
             return op_rshift(RubyFixnum.newFixnum(getRuntime(), -width));
 		    }
     	
         if (width == 0) {
             return this;
     }
 
         if (width > BIT_SIZE - 1 || ((~0L << BIT_SIZE - width - 1) & value) != 0) {
             return RubyBignum.newBignum(getRuntime(), value).op_lshift(other);
         }
         
         return newFixnum(value << width);
     }
 
     /** fix_rshift 
      * 
      */
     @JRubyMethod(name = ">>", required = 1)
     public IRubyObject op_rshift(IRubyObject other) {
         long width = num2long(other);
 
         if (width < 0) {
             return op_lshift(RubyFixnum.newFixnum(getRuntime(), -width));
     }
     
         if (width == 0) {
             return this;
     }
 
         if (width >= BIT_SIZE - 1) {
             if (value < 0) {
                 return RubyFixnum.minus_one(getRuntime());
     }
             return RubyFixnum.zero(getRuntime());
         }
 
         return newFixnum(value >> width);
         }
 
     /** fix_to_f 
      * 
      */
     @JRubyMethod(name = "to_f")
     public IRubyObject to_f() {
         return RubyFloat.newFloat(getRuntime(), (double) value);
     }
 
     /** fix_size 
      * 
      */
     @JRubyMethod(name = "size")
     public IRubyObject size() {
         return newFixnum((long) ((BIT_SIZE + 7) / 8));
         }
 
     /** fix_zero_p 
      * 
      */
     @JRubyMethod(name = "zero?")
     public IRubyObject zero_p() {
         return RubyBoolean.newBoolean(getRuntime(), value == 0);
     }
 
     @JRubyMethod(name = "id")
     public IRubyObject id() {
         if (value <= Long.MAX_VALUE / 2 && value >= Long.MIN_VALUE / 2)
             return newFixnum(2 * value + 1);
         return super.id();
     }
 
     public IRubyObject taint() {
         return this;
     }
 
     public IRubyObject freeze() {
         return this;
     }
 
     public static RubyFixnum unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         return input.getRuntime().newFixnum(input.unmarshalInt());
     }
 
     /*  ================
      *  Singleton Methods
      *  ================ 
      */
 
     /** rb_fix_induced_from
      * 
      */
     @JRubyMethod(name = "induced_from", required = 1, meta = true)
     public static IRubyObject induced_from(IRubyObject recv, IRubyObject other) {
         return RubyNumeric.num2fix(other);
     }
 }
diff --git a/src/org/jruby/RubyIO.java b/src/org/jruby/RubyIO.java
index 2e80498fc2..e5a61a2189 100644
--- a/src/org/jruby/RubyIO.java
+++ b/src/org/jruby/RubyIO.java
@@ -1,1578 +1,1579 @@
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
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
 package org.jruby;
 
 import java.io.EOFException;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.lang.ref.WeakReference;
 import java.nio.channels.Channel;
 import java.nio.channels.Pipe;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.SelectionKey;
 import java.nio.channels.Selector;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOHandlerJavaIO;
 import org.jruby.util.IOHandlerNio;
 import org.jruby.util.IOHandlerNull;
 import org.jruby.util.IOHandlerProcess;
 import org.jruby.util.IOHandlerSeekable;
 import org.jruby.util.IOHandlerUnseekable;
 import org.jruby.util.IOModes;
 import org.jruby.util.ShellLauncher;
 
 /**
  * 
  * @author jpetersen
  */
 public class RubyIO extends RubyObject {
     public static final int STDIN = 0;
     public static final int STDOUT = 1;
     public static final int STDERR = 2;
     
     protected IOHandler handler;
     protected IOModes modes = null;
     protected int lineNumber = 0;
     
     // Does THIS IO object think it is still open
     // as opposed to the IO Handler which knows the
     // actual truth.  If two IO objects share the
     // same IO Handler, then it is possible for
     // one object to think that the handler is open
     // when it really isn't.  Keeping track of this yields
     // the right errors.
     protected boolean isOpen = true;
     private boolean atEOF = false;
 
     /*
      * Random notes:
      *  
      * 1. When a second IO object is created with the same fileno odd
      * concurrency issues happen when the underlying implementation
      * commits data.   So:
      * 
      * f = File.new("some file", "w")
      * f.puts("heh")
      * g = IO.new(f.fileno)
      * g.puts("hoh")
      * ... more operations of g and f ...
      * 
      * Will generate a mess in "some file".  The problem is that most
      * operations are buffered.  When those buffers flush and get
      * written to the physical file depends on the implementation
      * (semantically I would think that it should be last op wins -- but 
      * it isn't).  I doubt java could mimic ruby in this way.  I also 
      * doubt many people are taking advantage of this.  How about 
      * syswrite/sysread though?  I think the fact that sysread/syswrite 
      * are defined to be a low-level system calls, allows implementations 
      * to be somewhat different?
      * 
      * 2. In the case of:
      * f = File.new("some file", "w")
      * f.puts("heh")
      * print f.pos
      * g = IO.new(f.fileno)
      * print g.pos
      * Both printed positions will be the same.  But:
      * f = File.new("some file", "w")
      * f.puts("heh")
      * g = IO.new(f.fileno)
      * print f.pos, g.pos
      * won't be the same position.  Seem peculiar enough not to touch
      * (this involves pos() actually causing a seek?)
      * 
      * 3. All IO objects reference a IOHandler.  If multiple IO objects
      * have the same fileno, then they also share the same IOHandler.
      * It is possible that some IO objects may share the same IOHandler
      * but not have the same permissions.  However, all subsequent IO
      * objects created after the first must be a subset of the original
      * IO Object (see below for an example). 
      *
      * The idea that two or more IO objects can have different access
      * modes means that IO objects must keep track of their own
      * permissions.  In addition the IOHandler itself must know what
      * access modes it has.
      * 
      * The above sharing situation only occurs in a situation like:
      * f = File.new("some file", "r+")
      * g = IO.new(f.fileno, "r")
      * Where g has reduced (subset) permissions.
      * 
      * On reopen, the fileno's IOHandler gets replaced by a new handler. 
      */
     
     /*
      * I considered making all callers of this be moved into IOHandlers
      * constructors (since it would be less error prone to forget there).
      * However, reopen() makes doing this a little funky. 
      */
     public void registerIOHandler(IOHandler newHandler) {
         getRuntime().getIoHandlers().put(new Integer(newHandler.getFileno()), new WeakReference(newHandler));
     }
     
     public void unregisterIOHandler(int aFileno) {
         getRuntime().getIoHandlers().remove(new Integer(aFileno));
     }
     
     public IOHandler getIOHandlerByFileno(int aFileno) {
         return (IOHandler) ((WeakReference) getRuntime().getIoHandlers().get(new Integer(aFileno))).get();
     }
     
     // FIXME can't use static; would interfere with other runtimes in the same JVM
     protected static int fileno = 2;
     
     public static int getNewFileno() {
         fileno++;
         
         return fileno;
     }
 
     // This should only be called by this and RubyFile.
     // It allows this object to be created without a IOHandler.
     public RubyIO(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
 
     public RubyIO(Ruby runtime, OutputStream outputStream) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (outputStream == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         try {
             handler = new IOHandlerUnseekable(runtime, null, outputStream);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
         modes = handler.getModes();
         
         registerIOHandler(handler);
     }
     
     public RubyIO(Ruby runtime, InputStream inputStream) {
         super(runtime, runtime.getIO());
         
         if (inputStream == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         try {
             handler = new IOHandlerUnseekable(runtime, inputStream, null);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
         
         modes = handler.getModes();
         
         registerIOHandler(handler);
     }
     
     public RubyIO(Ruby runtime, Channel channel) {
         super(runtime, runtime.getIO());
         
         // We only want IO objects with valid streams (better to error now). 
         if (channel == null) {
             throw runtime.newIOError("Opening invalid stream");
         }
         
         try {
             handler = new IOHandlerNio(runtime, channel);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
         modes = handler.getModes();
         
         registerIOHandler(handler);
     }
 
     public RubyIO(Ruby runtime, Process process) {
     	super(runtime, runtime.getIO());
 
         modes = new IOModes(runtime, "w+");
 
         try {
     	    handler = new IOHandlerProcess(runtime, process, modes);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
     	modes = handler.getModes();
     	
     	registerIOHandler(handler);
     }
     
     public RubyIO(Ruby runtime, int descriptor) {
         super(runtime, runtime.getIO());
 
         try {
             handler = new IOHandlerUnseekable(runtime, descriptor);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
         modes = handler.getModes();
         
         registerIOHandler(handler);
     }
     
     private static ObjectAllocator IO_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIO(runtime, klass);
         }
     };
 
     public static RubyClass createIOClass(Ruby runtime) {
         RubyClass ioClass = runtime.defineClass("IO", runtime.getObject(), IO_ALLOCATOR);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyIO.class);   
         RubyClass ioMetaClass = ioClass.getMetaClass();
         ioClass.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyIO;
                 }
             };
 
         ioClass.includeModule(runtime.getEnumerable());
         
         // TODO: Implement tty? and isatty.  We have no real capability to
         // determine this from java, but if we could set tty status, then
         // we could invoke jruby differently to allow stdin to return true
         // on this.  This would allow things like cgi.rb to work properly.
         
         ioClass.defineAnnotatedMethods(RubyIO.class);
 
         // Constants for seek
         ioClass.fastSetConstant("SEEK_SET", runtime.newFixnum(IOHandler.SEEK_SET));
         ioClass.fastSetConstant("SEEK_CUR", runtime.newFixnum(IOHandler.SEEK_CUR));
         ioClass.fastSetConstant("SEEK_END", runtime.newFixnum(IOHandler.SEEK_END));
         
         ioClass.dispatcher = callbackFactory.createDispatcher(ioClass);
 
         return ioClass;
     }    
     
     /**
      * <p>Open a file descriptor, unless it is already open, then return
      * it.</p> 
      */
     public static IRubyObject fdOpen(Ruby runtime, int descriptor) {
         return new RubyIO(runtime, descriptor);
     }
 
     /*
      * See checkReadable for commentary.
      */
     protected void checkWriteable() {
         if (!isOpen() || !modes.isWritable()) {
             throw getRuntime().newIOError("not opened for writing");
         }
     }
 
     /*
      * What the IO object "thinks" it can do.  If two IO objects share
      * the same fileno (IOHandler), then it is possible for one to pull
      * the rug out from the other.  This will make the second object still
      * "think" that the file is open.  Secondly, if two IO objects share
      * the same fileno, but the second one only has a subset of the access
      * permissions, then it will "think" that it cannot do certain 
      * operations.
      */
     protected void checkReadable() {
         if (!isOpen() || !modes.isReadable()) {
             throw getRuntime().newIOError("not opened for reading");            
         }
     }
     
     public boolean isOpen() {
         return isOpen;
     }
 
     public OutputStream getOutStream() {
         if(handler instanceof IOHandlerJavaIO) {
             return ((IOHandlerJavaIO) handler).getOutputStream();
         } else {
             return null;
         }
     }
 
     public InputStream getInStream() {
         if (handler instanceof IOHandlerJavaIO) {
             return ((IOHandlerJavaIO) handler).getInputStream();
         } else {
             return null;
         }
     }
 
     public Channel getChannel() {
         if (handler instanceof IOHandlerNio) {
             return ((IOHandlerNio) handler).getChannel();
         } else {
             return null;
         }
     }
 
     @JRubyMethod(name = "reopen", required = 1, optional = 1)
     public IRubyObject reopen(IRubyObject[] args) {
     	if (args.length < 1) {
             throw getRuntime().newArgumentError("wrong number of arguments");
     	}
     	
         if (args[0].isKindOf(getRuntime().getIO())) {
             RubyIO ios = (RubyIO) args[0];
 
             int keepFileno = handler.getFileno();
             
             // close the old handler before it gets overwritten
             if (handler.isOpen()) {
                 try {
                     handler.close();
                 } catch (IOHandler.BadDescriptorException e) {
                     throw getRuntime().newErrnoEBADFError();
                 } catch (EOFException e) {
                     return getRuntime().getNil();
                 } catch (IOException e) {
                     throw getRuntime().newIOError(e.getMessage());
                 }
             }
 
             // When we reopen, we want our fileno to be preserved even
             // though we have a new IOHandler.
             // Note: When we clone we get a new fileno...then we replace it.
             // This ends up incrementing our fileno index up, which makes the
             // fileno we choose different from ruby.  Since this seems a bit
             // too implementation specific, I did not bother trying to get
             // these to agree (what scary code would depend on fileno generating
             // a particular way?)
             try {
                 handler = ios.handler.cloneIOHandler();
             } catch (IOHandler.InvalidValueException e) {
             	throw getRuntime().newErrnoEINVALError();
             } catch (IOHandler.PipeException e) {
             	throw getRuntime().newErrnoESPIPEError();
             } catch (FileNotFoundException e) {
             	throw getRuntime().newErrnoENOENTError();
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
             handler.setFileno(keepFileno);
 
             // Update fileno list with our new handler
             registerIOHandler(handler);
         } else if (args[0].isKindOf(getRuntime().getString())) {
             String path = ((RubyString) args[0]).toString();
             IOModes newModes = null;
 
             if (args.length > 1) {
                 if (!args[1].isKindOf(getRuntime().getString())) {
                     throw getRuntime().newTypeError(args[1], getRuntime().getString());
                 }
                     
                 newModes = new IOModes(getRuntime(), ((RubyString) args[1]).toString());
             }
 
             try {
                 if (handler != null) {
                     close();
                 }
 
                 if (newModes != null) {
                 	modes = newModes;
                 }
                 if ("/dev/null".equals(path)) {
                 	handler = new IOHandlerNull(getRuntime(), modes);
                 } else {
                 	handler = new IOHandlerSeekable(getRuntime(), path, modes);
                 }
                 
                 registerIOHandler(handler);
             } catch (IOHandler.InvalidValueException e) {
             	throw getRuntime().newErrnoEINVALError();
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.toString());
             }
         }
         
         // A potentially previously close IO is being 'reopened'.
         isOpen = true;
         return this;
     }
     /** Read a line.
      * 
      */
     // TODO: Most things loop over this and always pass it the same arguments
     // meaning they are an invariant of the loop.  Think about fixing this.
     public IRubyObject internalGets(IRubyObject[] args) {
         checkReadable();
 
         IRubyObject sepVal;
 
         if (args.length > 0) {
             sepVal = args[0];
         } else {
             sepVal = getRuntime().getGlobalVariables().get("$/");
         }
         
         ByteList separator = sepVal.isNil() ? null : ((RubyString) sepVal).getByteList();
 
         if (separator != null && separator.realSize == 0) {
             separator = IOHandler.PARAGRAPH_DELIMETER;
         }
 
         try {
 						
             ByteList newLine = handler.gets(separator);
 
 		    if (newLine != null) {
 		        lineNumber++;
 		        getRuntime().getGlobalVariables().set("$.", getRuntime().newFixnum(lineNumber));
 		        RubyString result = RubyString.newString(getRuntime(), newLine);
 		        result.taint();
    
 		        return result;
 		    }
 		    
             return getRuntime().getNil();
         } catch (EOFException e) {
             return getRuntime().getNil();
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     // IO class methods.
 
     @JRubyMethod(name = "initialize", required = 1, optional = 1, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         int count = Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         int newFileno = RubyNumeric.fix2int(args[0]);
         String mode = null;
         
         if (count > 1) {
             mode = args[1].convertToString().toString();
         }
 
         // See if we already have this descriptor open.
         // If so then we can mostly share the handler (keep open
         // file, but possibly change the mode).
         IOHandler existingIOHandler = getIOHandlerByFileno(newFileno);
         
         if (existingIOHandler == null) {
             if (mode == null) {
                 mode = "r";
             }
             
             try {
                 handler = new IOHandlerUnseekable(getRuntime(), newFileno, mode);
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
             modes = new IOModes(getRuntime(), mode);
             
             registerIOHandler(handler);
         } else {
             // We are creating a new IO object that shares the same
             // IOHandler (and fileno).  
             handler = existingIOHandler;
             
             // Inherit if no mode specified otherwise create new one
             modes = mode == null ? handler.getModes() :
             	new IOModes(getRuntime(), mode);
 
             // Reset file based on modes.
             try {
                 handler.reset(modes);
             } catch (IOHandler.InvalidValueException e) {
             	throw getRuntime().newErrnoEINVALError();
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         }
         
         return this;
     }
 
     // This appears to be some windows-only mode.  On a java platform this is a no-op
     @JRubyMethod(name = "binmode")
     public IRubyObject binmode() {
             return this;
     }
 
     @JRubyMethod(name = "syswrite", required = 1)
     public IRubyObject syswrite(IRubyObject obj) {
         try {
             if (obj instanceof RubyString) {
                 return getRuntime().newFixnum(handler.syswrite(((RubyString)obj).getByteList()));
             } else {
                 // FIXME: unlikely to be efficient, but probably correct
                 return getRuntime().newFixnum(
                         handler.syswrite(
                         ((RubyString)obj.callMethod(
                             obj.getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s")).getByteList()));
             }
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newSystemCallError(e.getMessage());
         }
     }
     
     /** io_write
      * 
      */
     @JRubyMethod(name = "write", required = 1)
     public IRubyObject write(IRubyObject obj) {
         checkWriteable();
 
         try {
             if (obj instanceof RubyString) {
                 return getRuntime().newFixnum(handler.write(((RubyString)obj).getByteList()));
             } else {
                 // FIXME: unlikely to be efficient, but probably correct
                 return getRuntime().newFixnum(
                         handler.write(
                         ((RubyString)obj.callMethod(
                             obj.getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s")).getByteList()));
             }
         } catch (IOHandler.BadDescriptorException e) {
             return RubyFixnum.zero(getRuntime());
         } catch (IOException e) {
             return RubyFixnum.zero(getRuntime());
         }
     }
 
     /** rb_io_addstr
      * 
      */
     @JRubyMethod(name = "<<", required = 1)
     public IRubyObject op_concat(IRubyObject anObject) {
         // Claims conversion is done via 'to_s' in docs.
         IRubyObject strObject = anObject.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s");
 
         write(strObject);
         
         return this; 
     }
 
-    @JRubyMethod(name = {"fileno", "to_i"})
+    @JRubyMethod(name = "fileno", alias = "to_i")
     public RubyFixnum fileno() {
         return getRuntime().newFixnum(handler.getFileno());
     }
     
     /** Returns the current line number.
      * 
      * @return the current line number.
      */
     @JRubyMethod(name = "lineno")
     public RubyFixnum lineno() {
         return getRuntime().newFixnum(lineNumber);
     }
 
     /** Sets the current line number.
      * 
      * @param newLineNumber The new line number.
      */
     @JRubyMethod(name = "lineno=", required = 1)
     public RubyFixnum lineno_set(IRubyObject newLineNumber) {
         lineNumber = RubyNumeric.fix2int(newLineNumber);
 
         return (RubyFixnum) newLineNumber;
     }
 
     /** Returns the current sync mode.
      * 
      * @return the current sync mode.
      */
     @JRubyMethod(name = "sync")
     public RubyBoolean sync() {
         return getRuntime().newBoolean(handler.isSync());
     }
     
     /**
      * <p>Return the process id (pid) of the process this IO object
      * spawned.  If no process exists (popen was not called), then
      * nil is returned.  This is not how it appears to be defined
      * but ruby 1.8 works this way.</p>
      * 
      * @return the pid or nil
      */
     @JRubyMethod(name = "pid")
     public IRubyObject pid() {
         int pid = handler.pid();
         
         return pid == -1 ? getRuntime().getNil() : getRuntime().newFixnum(pid); 
     }
     
     public boolean hasPendingBuffered() {
         return handler.hasPendingBuffered();
     }
     
     @JRubyMethod(name = {"pos", "tell"})
     public RubyFixnum pos() {
         try {
             return getRuntime().newFixnum(handler.pos());
         } catch (IOHandler.PipeException e) {
         	throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
     
     @JRubyMethod(name = "pos=", required = 1)
     public RubyFixnum pos_set(IRubyObject newPosition) {
         long offset = RubyNumeric.fix2long(newPosition);
 
         if (offset < 0) {
             throw getRuntime().newSystemCallError("Negative seek offset");
         }
         
         try {
             handler.seek(offset, IOHandler.SEEK_SET);
         } catch (IOHandler.InvalidValueException e) {
         	throw getRuntime().newErrnoEINVALError();
         } catch (IOHandler.PipeException e) {
         	throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         return (RubyFixnum) newPosition;
     }
     
     /** Print some objects to the stream.
      * 
      */
     @JRubyMethod(name = "print", rest = true)
     public IRubyObject print(IRubyObject[] args) {
         if (args.length == 0) {
             args = new IRubyObject[] { getRuntime().getCurrentContext().getCurrentFrame().getLastLine() };
         }
 
         IRubyObject fs = getRuntime().getGlobalVariables().get("$,");
         IRubyObject rs = getRuntime().getGlobalVariables().get("$\\");
         ThreadContext context = getRuntime().getCurrentContext();
         
         for (int i = 0; i < args.length; i++) {
             if (i > 0 && !fs.isNil()) {
                 callMethod(context, "write", fs);
             }
             if (args[i].isNil()) {
                 callMethod(context, "write", getRuntime().newString("nil"));
             } else {
                 callMethod(context, "write", args[i]);
             }
         }
         if (!rs.isNil()) {
             callMethod(context, "write", rs);
         }
 
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", required = 1, rest = true)
     public IRubyObject printf(IRubyObject[] args) {
     	Arity.checkArgumentCount(getRuntime(), args, 1, -1);
         callMethod(getRuntime().getCurrentContext(), "write", RubyKernel.sprintf(this, args));
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "putc", required = 1)
     public IRubyObject putc(IRubyObject object) {
         int c;
         
         if (object.isKindOf(getRuntime().getString())) {
             String value = ((RubyString) object).toString();
             
             if (value.length() > 0) {
                 c = value.charAt(0);
             } else {
                 throw getRuntime().newTypeError("Cannot convert String to Integer");
             }
         } else if (object.isKindOf(getRuntime().getFixnum())){
             c = RubyNumeric.fix2int(object);
         } else { // What case will this work for?
             c = RubyNumeric.fix2int(object.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_I, "to_i"));
         }
 
         try {
             handler.putc(c);
         } catch (IOHandler.BadDescriptorException e) {
             return RubyFixnum.zero(getRuntime());
         } catch (IOException e) {
             return RubyFixnum.zero(getRuntime());
         }
         
         return object;
     }
     
     // This was a getOpt with one mandatory arg, but it did not work
     // so I am parsing it for now.
     @JRubyMethod(name = "seek", required = 1, optional = 1)
     public RubyFixnum seek(IRubyObject[] args) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("wrong number of arguments");
         }
         
         long offset = RubyNumeric.fix2long(args[0]);
         int type = IOHandler.SEEK_SET;
         
         if (args.length > 1) {
             type = RubyNumeric.fix2int(args[1].convertToInteger());
         }
         
         try {
             handler.seek(offset, type);
         } catch (IOHandler.InvalidValueException e) {
         	throw getRuntime().newErrnoEINVALError();
         } catch (IOHandler.PipeException e) {
         	throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         return RubyFixnum.zero(getRuntime());
     }
 
     @JRubyMethod(name = "rewind")
     public RubyFixnum rewind() {
         try {
 		    handler.rewind();
         } catch (IOHandler.InvalidValueException e) {
         	throw getRuntime().newErrnoEINVALError();
         } catch (IOHandler.PipeException e) {
         	throw getRuntime().newErrnoESPIPEError();
 	    } catch (IOException e) {
 	        throw getRuntime().newIOError(e.getMessage());
 	    }
 
         // Must be back on first line on rewind.
         lineNumber = 0;
         
         return RubyFixnum.zero(getRuntime());
     }
     
     @JRubyMethod(name = "fsync")
     public RubyFixnum fsync() {
         checkWriteable();
 
         try {
             handler.sync();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         }
 
         return RubyFixnum.zero(getRuntime());
     }
 
     /** Sets the current sync mode.
      * 
      * @param newSync The new sync mode.
      */
     @JRubyMethod(name = "sync=", required = 1)
     public IRubyObject sync_set(IRubyObject newSync) {
         handler.setIsSync(newSync.isTrue());
 
         return this;
     }
 
     @JRubyMethod(name = {"eof?", "eof"})
     public RubyBoolean eof_p() {
         try {
             boolean isEOF = handler.isEOF(); 
             return isEOF ? getRuntime().getTrue() : getRuntime().getFalse();
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     @JRubyMethod(name = {"tty?", "isatty?"})
     public RubyBoolean tty_p() {
         // TODO: this is less than ideal but might be as close as we'll get
         int fileno = handler.getFileno();
         if (fileno == STDOUT || fileno == STDIN || fileno == STDERR) {
             return getRuntime().getTrue();
         } else {
             return getRuntime().getFalse();
         }
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1)
     public IRubyObject initialize_copy(IRubyObject original){
         if (this == original) return this;
 
         RubyIO originalIO = (RubyIO) original;
         
         // Two pos pointers?  
         // http://blade.nagaokaut.ac.jp/ruby/ruby-talk/81513
         // So if I understand this correctly, the descriptor level stuff
         // shares things like position, but the higher level stuff uses
         // a different set of libc functions (FILE*), which does not share
         // position.  Our current implementation implements our higher 
         // level operations on top of our 'sys' versions.  So we could in
         // fact share everything.  Unfortunately, we want to clone ruby's
         // behavior (i.e. show how this interface bleeds their 
         // implementation). So our best bet, is to just create a yet another
         // copy of the handler.  In fact, ruby 1.8 must do this as the cloned
         // resource is in fact a different fileno.  What is clone for again?        
         
         handler = originalIO.handler;
         modes = (IOModes) originalIO.modes.clone();
         
         return this;
     }
     
     /** Closes the IO.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "closed?")
     public RubyBoolean closed_p() {
         return isOpen() ? getRuntime().getFalse() : getRuntime().getTrue();
     }
 
     /** 
      * <p>Closes all open resources for the IO.  It also removes
      * it from our magical all open file descriptor pool.</p>
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         isOpen = false;
         
         try {
             handler.close();
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
         
         unregisterIOHandler(handler.getFileno());
         
         return this;
     }
 
     @JRubyMethod(name = "close_write")
     public IRubyObject close_write() {
         return this;
     }
 
     /** Flushes the IO output stream.
      * 
      * @return The IO.
      */
     @JRubyMethod(name = "flush")
     public RubyIO flush() {
         try { 
             handler.flush();
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
 
         return this;
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "gets", optional = 1)
     public IRubyObject gets(IRubyObject[] args) {
         IRubyObject result = internalGets(args);
 
         if (!result.isNil()) getRuntime().getCurrentContext().getCurrentFrame().setLastLine(result);
 
         return result;
     }
 
     public boolean getBlocking() {
         if (!(handler instanceof IOHandlerNio)) return true;
 
         return ((IOHandlerNio) handler).getBlocking();
      }
 
     @JRubyMethod(name = "fcntl", required = 2)
     public IRubyObject fcntl(IRubyObject cmd, IRubyObject arg) throws IOException {
         long realCmd = cmd.convertToInteger().getLongValue();
         
         // FIXME: Arg may also be true, false, and nil and still be valid.  Strangely enough, 
         // protocol conversion is not happening in Ruby on this arg?
         if (!(arg instanceof RubyNumeric)) return getRuntime().newFixnum(0);
         
         long realArg = ((RubyNumeric)arg).getLongValue();
 
         // Fixme: Only F_SETFL is current supported
         if (realCmd == 1L) {  // cmd is F_SETFL
             boolean block = true;
             
             if ((realArg & IOModes.NONBLOCK) == IOModes.NONBLOCK) {
                 block = false;
             }
             
             if (!(handler instanceof IOHandlerNio)) {
                 // cryptic for the uninitiated...
                 throw getRuntime().newNotImplementedError("FCNTL only works with Nio based handlers");
             }
 
             try {
                 ((IOHandlerNio) handler).setBlocking(block);
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         }
         
         return getRuntime().newFixnum(0);
     }
 
     @JRubyMethod(name = "puts", rest = true)
     public IRubyObject puts(IRubyObject[] args) {
     	Arity.checkArgumentCount(getRuntime(), args, 0, -1);
         
     	ThreadContext context = getRuntime().getCurrentContext();
         
         if (args.length == 0) {
             callMethod(context, "write", getRuntime().newString("\n"));
             return getRuntime().getNil();
         }
 
         for (int i = 0; i < args.length; i++) {
             String line = null;
             if (args[i].isNil()) {
                 line = "nil";
             } else if(getRuntime().isInspecting(args[i])) {
                 line = "[...]";
             }
             else if (args[i] instanceof RubyArray) {
                 inspectPuts((RubyArray) args[i]);
                 continue;
             } else {
                 line = args[i].toString();
             }
             callMethod(getRuntime().getCurrentContext(), "write", getRuntime().newString(line+
             		(line.endsWith("\n") ? "" : "\n")));
         }
         return getRuntime().getNil();
     }
     
     private IRubyObject inspectPuts(RubyArray array) {
         try {
             getRuntime().registerInspecting(array);
             return puts(array.toJavaArray());
         } finally {
             getRuntime().unregisterInspecting(array);
         }
     }
 
     /** Read a line.
      * 
      */
     @JRubyMethod(name = "readline", optional = 1)
     public IRubyObject readline(IRubyObject[] args) {
         IRubyObject line = gets(args);
 
         if (line.isNil()) {
             throw getRuntime().newEOFError();
         }
         
         return line;
     }
 
     /** Read a byte. On EOF returns nil.
      * 
      */
     @JRubyMethod(name = "getc")
     public IRubyObject getc() {
         checkReadable();
         
         try {
             int c = handler.getc();
         
             return c == -1 ? getRuntime().getNil() : getRuntime().newFixnum(c);
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
     
     /** 
      * <p>Pushes char represented by int back onto IOS.</p>
      * 
      * @param number to push back
      */
     @JRubyMethod(name = "ungetc", required = 1)
     public IRubyObject ungetc(IRubyObject number) {
         handler.ungetc(RubyNumeric.fix2int(number));
 
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "readpartial", required = 1, optional = 1)
     public IRubyObject readpartial(IRubyObject[] args) {
         if(!(handler instanceof IOHandlerNio)) {
             // cryptic for the uninitiated...
             throw getRuntime().newNotImplementedError("readpartial only works with Nio based handlers");
         }
     	try {
             ByteList buf = ((IOHandlerNio)handler).readpartial(RubyNumeric.fix2int(args[0]));
             IRubyObject strbuf = RubyString.newString(getRuntime(), buf == null ? new ByteList(ByteList.NULL_ARRAY) : buf);
             if(args.length > 1) {
                 args[1].callMethod(getRuntime().getCurrentContext(),MethodIndex.OP_LSHIFT, "<<", strbuf);
                 return args[1];
             } 
 
             return strbuf;
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             return getRuntime().getNil();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     // FIXME: according to MRI's RI, sysread only takes one arg
     @JRubyMethod(name = "sysread", required = 1, optional = 1)
     public IRubyObject sysread(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
 
         int len = (int)RubyNumeric.num2long(args[0]);
         if (len < 0) throw getRuntime().newArgumentError("Negative size");
 
         try {
             RubyString str;
             if (args.length == 1 || args[1].isNil()) {
                 if (len == 0) return RubyString.newString(getRuntime(), "");
                 str = RubyString.newString(getRuntime(), handler.sysread(len));
             } else {
                 str = args[1].convertToString();
                 if (len == 0) {
                     str.setValue(new ByteList());
                     return str;
                 }
                 str.setValue(handler.sysread(len)); // should preserve same instance
             }
             str.setTaint(true);
             return str;
             
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
     	} catch (IOException e) {
             // All errors to sysread should be SystemCallErrors, but on a closed stream
             // Ruby returns an IOError.  Java throws same exception for all errors so
             // we resort to this hack...
             if ("File not open".equals(e.getMessage())) {
                     throw getRuntime().newIOError(e.getMessage());
             }
     	    throw getRuntime().newSystemCallError(e.getMessage());
     	}
     }
     
     @JRubyMethod(name = "read", rest = true)
     public IRubyObject read(IRubyObject[] args) {
                
         int argCount = Arity.checkArgumentCount(getRuntime(), args, 0, 2);
         RubyString callerBuffer = null;
         boolean readEntireStream = (argCount == 0 || args[0].isNil());
 
         try {
             // Reads when already at EOF keep us at EOF
             // We do retain the possibility of un-EOFing if the handler
             // gets new data
             if (atEOF && handler.isEOF()) throw new EOFException();
 
             if (argCount == 2) {
                 callerBuffer = args[1].convertToString(); 
             }
 
             ByteList buf;
             if (readEntireStream) {
                 buf = handler.getsEntireStream();
             } else {
                 long len = RubyNumeric.num2long(args[0]);
                 if (len < 0) throw getRuntime().newArgumentError("negative length " + len + " given");
                 if (len == 0) return getRuntime().newString("");
                 buf = handler.read((int)len);
             }
 
             if (buf == null) throw new EOFException();
 
             // If we get here then no EOFException was thrown in the handler.  We
             // might still need to set our atEOF flag back to true depending on
             // whether we were reading the entire stream (see the finally block below)
             atEOF = false;
             if (callerBuffer != null) {
                 callerBuffer.setValue(buf);
                 return callerBuffer;
             }
             
             return RubyString.newString(getRuntime(), buf);
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             // on EOF, IO#read():
             // with no args or a nil first arg will return an empty string
             // with a non-nil first arg will return nil
             atEOF = true;
             if (callerBuffer != null) {
                 callerBuffer.setValue("");
                 return readEntireStream ? callerBuffer : getRuntime().getNil();
             }
 
             return readEntireStream ? getRuntime().newString("") : getRuntime().getNil();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         } finally {
             // reading the entire stream always puts us at EOF
             if (readEntireStream) {
                 atEOF = true;
             }
         }
     }
 
     /** Read a byte. On EOF throw EOFError.
      * 
      */
     @JRubyMethod(name = "readchar")
     public IRubyObject readchar() {
         checkReadable();
         
         try {
             int c = handler.getc();
         
             if (c == -1) throw getRuntime().newEOFError();
         
             return getRuntime().newFixnum(c);
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             throw getRuntime().newEOFError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     /** 
      * <p>Invoke a block for each byte.</p>
      */
     @JRubyMethod(name = "each_byte", frame = true)
     public IRubyObject each_byte(Block block) {
     	try {
             ThreadContext context = getRuntime().getCurrentContext();
             for (int c = handler.getc(); c != -1; c = handler.getc()) {
                 assert c < 256;
                 block.yield(context, getRuntime().newFixnum(c));
             }
 
             return getRuntime().getNil();
         } catch (IOHandler.BadDescriptorException e) {
             throw getRuntime().newErrnoEBADFError();
         } catch (EOFException e) {
             return getRuntime().getNil();
     	} catch (IOException e) {
     	    throw getRuntime().newIOError(e.getMessage());
         }
     }
 
     /** 
      * <p>Invoke a block for each line.</p>
      */
     @JRubyMethod(name = {"each_line", "each"}, optional = 1, frame = true)
     public RubyIO each_line(IRubyObject[] args, Block block) {
         IRubyObject rs;
         
         if (args.length == 0) {
             rs = getRuntime().getGlobalVariables().get("$/");
         } else {
             Arity.checkArgumentCount(getRuntime(), args, 1, 1);
             rs = args[0];
             if (!rs.isNil()) rs = rs.convertToString();
         }
         
         ThreadContext context = getRuntime().getCurrentContext();        
         for (IRubyObject line = internalGets(args); !line.isNil(); 
         	line = internalGets(args)) {
             block.yield(context, line);
         }
         
         return this;
     }
 
 
     @JRubyMethod(name = "readlines", optional = 1)
     public RubyArray readlines(IRubyObject[] args) {
         IRubyObject[] separatorArgument;
         if (args.length > 0) {
             if (!args[0].isKindOf(getRuntime().getNilClass()) &&
                 !args[0].isKindOf(getRuntime().getString())) {
                 throw getRuntime().newTypeError(args[0], 
                         getRuntime().getString());
             } 
             separatorArgument = new IRubyObject[] { args[0] };
         } else {
             separatorArgument = IRubyObject.NULL_ARRAY;
         }
 
         RubyArray result = getRuntime().newArray();
         IRubyObject line;
         while (! (line = internalGets(separatorArgument)).isNil()) {
             result.append(line);
         }
         return result;
     }
     
     @JRubyMethod(name = "to_io")
     public RubyIO to_io() {
     	return this;
     }
 
     public String toString() {
         return "RubyIO(" + modes + ", " + fileno + ")";
     }
     
     /* class methods for IO */
     
     /** rb_io_s_foreach
     *
     */
     @JRubyMethod(name = "foreach", required = 1, rest = true, frame = true, meta = true)
     public static IRubyObject foreach(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         int count = Arity.checkArgumentCount(runtime, args, 1, -1);
         IRubyObject filename = args[0].convertToString();
         runtime.checkSafeString(filename);
         RubyIO io = (RubyIO) RubyFile.open(recv, new IRubyObject[] { filename }, false, block);
        
         if (!io.isNil() && io.isOpen()) {
             try {
                 IRubyObject[] newArgs = new IRubyObject[count - 1];
                 System.arraycopy(args, 1, newArgs, 0, count - 1);
                
                 IRubyObject nextLine = io.internalGets(newArgs);
                 while (!nextLine.isNil()) {
                     block.yield(runtime.getCurrentContext(), nextLine);
                     nextLine = io.internalGets(newArgs);
                 }
             } finally {
                 io.close();
             }
         }
        
         return runtime.getNil();
     }
    
     private static RubyIO registerSelect(Selector selector, IRubyObject obj, int ops) throws IOException {
        RubyIO ioObj;
        
        if (!(obj instanceof RubyIO)) {
            // invoke to_io
            if (!obj.respondsTo("to_io")) return null;
 
            ioObj = (RubyIO) obj.callMethod(obj.getRuntime().getCurrentContext(), "to_io");
        } else {
            ioObj = (RubyIO) obj;
        }
        
        Channel channel = ioObj.getChannel();
        if (channel == null || !(channel instanceof SelectableChannel)) {
            return null;
        }
        
        ((SelectableChannel) channel).configureBlocking(false);
        int real_ops = ((SelectableChannel) channel).validOps() & ops;
        SelectionKey key = ((SelectableChannel) channel).keyFor(selector);
        
        if (key == null) {
            ((SelectableChannel) channel).register(selector, real_ops, obj);
        } else {
            key.interestOps(key.interestOps()|real_ops);
        }
        
        return ioObj;
    }
    
     @JRubyMethod(name = "select", required = 1, optional = 3, meta = true)
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return select_static(recv.getRuntime(), args);
     }
    
     public static IRubyObject select_static(Ruby runtime, IRubyObject[] args) {
        try {
            boolean atLeastOneDescriptor = false;
            
            Set pending = new HashSet();
            Selector selector = Selector.open();
            if (!args[0].isNil()) {
                atLeastOneDescriptor = true;
                
                // read
                for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                    IRubyObject obj = (IRubyObject) i.next();
                    RubyIO ioObj = registerSelect(selector, obj, 
                            SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
                    
                    if (ioObj!=null && ioObj.hasPendingBuffered()) pending.add(obj);
                }
            }
            if (args.length > 1 && !args[1].isNil()) {
                atLeastOneDescriptor = true;
                // write
                for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                    IRubyObject obj = (IRubyObject) i.next();
                    registerSelect(selector, obj, SelectionKey.OP_WRITE);
                }
            }
            if (args.length > 2 && !args[2].isNil()) {
                atLeastOneDescriptor = true;
                // Java's select doesn't do anything about this, so we leave it be.
            }
            
            long timeout = 0;
            if(args.length > 3 && !args[3].isNil()) {
                if (args[3] instanceof RubyFloat) {
                    timeout = Math.round(((RubyFloat) args[3]).getDoubleValue() * 1000);
                } else {
                    timeout = Math.round(((RubyFixnum) args[3]).getDoubleValue() * 1000);
                }
                
                if (timeout < 0) {
                    throw runtime.newArgumentError("negative timeout given");
                }
            }
            
            if (!atLeastOneDescriptor) {
                return runtime.getNil();
            }
            
            if (pending.isEmpty()) {
                if (args.length > 3) {
                    if (timeout==0) {
                        selector.selectNow();
                    } else {
                        selector.select(timeout);                       
                    }
                } else {
                    selector.select();
                }
            } else {
                selector.selectNow();               
            }
            
            List r = new ArrayList();
            List w = new ArrayList();
            List e = new ArrayList();
            for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                SelectionKey key = (SelectionKey) i.next();
                if ((key.interestOps() & key.readyOps()
                        & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
                    r.add(key.attachment());
                    pending.remove(key.attachment());
                }
                if ((key.interestOps() & key.readyOps() & (SelectionKey.OP_WRITE)) != 0) {
                    w.add(key.attachment());
                }
            }
            r.addAll(pending);
            
            // make all sockets blocking as configured again
            for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
                SelectionKey key = (SelectionKey) i.next();
                SelectableChannel channel = key.channel();
                synchronized(channel.blockingLock()) {
                    boolean blocking = ((RubyIO) key.attachment()).getBlocking();
                    key.cancel();
                    channel.configureBlocking(blocking);
                }
            }
            selector.close();
            
            if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
                return runtime.getNil();
            }
            
            List ret = new ArrayList();
            
            ret.add(RubyArray.newArray(runtime, r));
            ret.add(RubyArray.newArray(runtime, w));
            ret.add(RubyArray.newArray(runtime, e));
            
            return RubyArray.newArray(runtime, ret);
        } catch(IOException e) {
            throw runtime.newIOError(e.getMessage());
        }
    }
    
     @JRubyMethod(name = "read", required = 1, optional = 2, meta = true)
     public static IRubyObject read(IRubyObject recv, IRubyObject[] args, Block block) {
        Ruby runtime = recv.getRuntime();
        Arity.checkArgumentCount(runtime, args, 1, 3);
        IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
        RubyIO file = (RubyIO) RubyKernel.open(recv, fileArguments, block);
        IRubyObject[] readArguments;
        
        if (args.length >= 2) {
            readArguments = new IRubyObject[] {args[1].convertToInteger()};
        } else {
            readArguments = new IRubyObject[] {};
        }
        
        try {
            
            if (args.length == 3) {
                file.seek(new IRubyObject[] {args[2].convertToInteger()});
            }
            
            return file.read(readArguments);
        } finally {
            file.close();
        }
    }
    
     @JRubyMethod(name = "readlines", required = 1, optional = 1, meta = true)
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args, Block block) {
        int count = Arity.checkArgumentCount(recv.getRuntime(), args, 1, 2);
        
        IRubyObject[] fileArguments = new IRubyObject[] {args[0]};
        IRubyObject[] separatorArguments = count >= 2 ? new IRubyObject[]{args[1]} : IRubyObject.NULL_ARRAY;
        RubyIO file = (RubyIO) RubyKernel.open(recv, fileArguments, block);
        try {
            return file.readlines(separatorArguments);
        } finally {
            file.close();
        }
    }
    
     //XXX Hacked incomplete popen implementation to make
     @JRubyMethod(name = "popen", required = 1, optional = 1, meta = true)
     public static IRubyObject popen(IRubyObject recv, IRubyObject[] args, Block block) {
        Ruby runtime = recv.getRuntime();
        Arity.checkArgumentCount(runtime, args, 1, 2);
        IRubyObject cmdObj = args[0].convertToString();
        runtime.checkSafeString(cmdObj);
        
        try {
            Process process = new ShellLauncher(runtime).run(cmdObj);            
            RubyIO io = new RubyIO(runtime, process);
            
            if (block.isGiven()) {
                try {
                    block.yield(runtime.getCurrentContext(), io);
                    return runtime.getNil();
                } finally {
                    io.close();
                    runtime.getGlobalVariables().set("$?",  RubyProcess.RubyStatus.newProcessStatus(runtime, (process.waitFor() * 256)));
                }
            }
            return io;
        } catch (IOException e) {
            throw runtime.newIOErrorFromException(e);
        } catch (InterruptedException e) {
            throw runtime.newThreadError("unexpected interrupt");
        }
    }
    
     // NIO based pipe
     @JRubyMethod(name = "pipe", meta = true)
     public static IRubyObject pipe(IRubyObject recv) throws Exception {
        Ruby runtime = recv.getRuntime();
        Pipe pipe = Pipe.open();
        return runtime.newArrayNoCopy(new IRubyObject[]{
            new RubyIO(runtime, pipe.source()),
            new RubyIO(runtime, pipe.sink())
        });
    }
    
     /**
      * returns non-nil if input available without blocking, false if EOF or not open/readable, otherwise nil.
      */
     public IRubyObject ready() {
        try {
            if (!handler.isOpen() || !handler.isReadable() || handler.isEOF()) {
                return getRuntime().getFalse();
            }
 
            int avail = handler.ready();
            if (avail > 0) {
                return getRuntime().newFixnum(avail);
            } 
        } catch (Exception anyEx) {
            return getRuntime().getFalse();
        }
        return getRuntime().getNil();
    }
    
     /**
      * waits until input available or timed out and returns self, or nil when EOF reached.
      */
     public IRubyObject io_wait() {
        try {
            if (handler.isEOF()) {
                return getRuntime().getNil();
            }
            handler.waitUntilReady();
        } catch (Exception anyEx) {
            return getRuntime().getNil();
        }
        return this;
    }
 }
diff --git a/src/org/jruby/RubyInteger.java b/src/org/jruby/RubyInteger.java
index 4bf6518783..30dcd396dd 100644
--- a/src/org/jruby/RubyInteger.java
+++ b/src/org/jruby/RubyInteger.java
@@ -1,233 +1,234 @@
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /** Implementation of the Integer class.
  *
  * @author  jpetersen
  */
 public abstract class RubyInteger extends RubyNumeric { 
 
     public static RubyClass createIntegerClass(Ruby runtime) {
         RubyClass integer = runtime.defineClass("Integer", runtime.getNumeric(),
                 ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setInteger(integer);
         integer.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyInteger;
                 }
             };
 
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyInteger.class);
         integer.getSingletonClass().undefineMethod("new");
 
         integer.includeModule(runtime.getPrecision());
         
         integer.defineAnnotatedMethods(RubyInteger.class);
         integer.dispatcher = callbackFactory.createDispatcher(integer);
         
         return integer;
     }
 
     public RubyInteger(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
     
     public RubyInteger(Ruby runtime, RubyClass rubyClass, boolean useObjectSpace) {
         super(runtime, rubyClass, useObjectSpace);
     }     
 
     public RubyInteger convertToInteger() {
     	return this;
     }
 
     // conversion
     protected RubyFloat toFloat() {
         return RubyFloat.newFloat(getRuntime(), getDoubleValue());
     }
 
     /*  ================
      *  Instance Methods
      *  ================ 
      */
 
     /** int_int_p
      * 
      */
     @JRubyMethod(name = "integer?")
     public IRubyObject integer_p() {
         return getRuntime().getTrue();
     }
 
     /** int_upto
      * 
      */
     @JRubyMethod(name = "upto", required = 1, frame = true)
     public IRubyObject upto(IRubyObject to, Block block) {
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         if (this instanceof RubyFixnum && to instanceof RubyFixnum) {
 
             RubyFixnum toFixnum = (RubyFixnum) to;
             long toValue = toFixnum.getLongValue();
             long fromValue = getLongValue();
             for (long i = fromValue; i <= toValue; i++) {
                 block.yield(context, RubyFixnum.newFixnum(runtime, i));
             }
         } else {
             RubyNumeric i = this;
 
             while (true) {
                 if (i.callMethod(context, MethodIndex.OP_GT, ">", to).isTrue()) {
                     break;
                 }
                 block.yield(context, i);
                 i = (RubyNumeric) i.callMethod(context, MethodIndex.OP_PLUS, "+", RubyFixnum.one(runtime));
             }
         }
         return this;
     }
 
     /** int_downto
      * 
      */
     // TODO: Make callCoerced work in block context...then fix downto, step, and upto.
     @JRubyMethod(name = "downto", required = 1, frame = true)
     public IRubyObject downto(IRubyObject to, Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (this instanceof RubyFixnum && to instanceof RubyFixnum) {
             RubyFixnum toFixnum = (RubyFixnum) to;
             long toValue = toFixnum.getLongValue();
             for (long i = getLongValue(); i >= toValue; i--) {
                 block.yield(context, RubyFixnum.newFixnum(getRuntime(), i));
             }
         } else {
             RubyNumeric i = this;
 
             while (true) {
                 if (i.callMethod(context, MethodIndex.OP_LT, "<", to).isTrue()) {
                     break;
                 }
                 block.yield(context, i);
                 i = (RubyNumeric) i.callMethod(context, MethodIndex.OP_MINUS, "-", RubyFixnum.one(getRuntime()));
             }
         }
         return this;
     }
 
     @JRubyMethod(name = "times", frame = true)
     public IRubyObject times(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 
         if (this instanceof RubyFixnum) {
 
             long value = getLongValue();
             for (long i = 0; i < value; i++) {
                 block.yield(context, RubyFixnum.newFixnum(getRuntime(), i));
             }
         } else {
             RubyNumeric i = RubyFixnum.zero(getRuntime());
             while (true) {
                 if (!i.callMethod(context, MethodIndex.OP_LT, "<", this).isTrue()) {
                     break;
                 }
                 block.yield(context, i);
                 i = (RubyNumeric) i.callMethod(context, MethodIndex.OP_PLUS, "+", RubyFixnum.one(getRuntime()));
             }
         }
 
         return this;
     }
 
     /** int_succ
      * 
      */
-    @JRubyMethod(name = "succ", alias = "next")
+    @JRubyMethod(name = {"succ", "next"})
     public IRubyObject succ() {
         if (this instanceof RubyFixnum) {
             return RubyFixnum.newFixnum(getRuntime(), getLongValue() + 1L);
         } else {
             return callMethod(getRuntime().getCurrentContext(), MethodIndex.OP_PLUS, "+", RubyFixnum.one(getRuntime()));
         }
     }
 
     /** int_chr
      * 
      */
     @JRubyMethod(name = "chr")
     public RubyString chr() {
         if (getLongValue() < 0 || getLongValue() > 0xff) {
             throw getRuntime().newRangeError(this.toString() + " out of char range");
         }
         return getRuntime().newString(new String(new char[] { (char) getLongValue() }));
     }
 
     /** int_to_i
      * 
      */
     @JRubyMethod(name = {"to_i", "to_int", "floor", "ceil", "round", "truncate"})
     public RubyInteger to_i() {
         return this;
     }
 
     /*  ================
      *  Singleton Methods
      *  ================ 
      */
 
     /** rb_int_induced_from
      * 
      */
     @JRubyMethod(name = "induced_from", meta = true, required = 1)
     public static IRubyObject induced_from(IRubyObject recv, IRubyObject other) {
         if (other instanceof RubyFixnum || other instanceof RubyBignum) {
             return other;
         } else if (other instanceof RubyFloat) {
             return other.callMethod(recv.getRuntime().getCurrentContext(), MethodIndex.TO_I, "to_i");
         } else {
             throw recv.getRuntime().newTypeError(
                     "failed to convert " + other.getMetaClass().getName() + " into Integer");
         }
     }
 }
diff --git a/src/org/jruby/RubyJRuby.java b/src/org/jruby/RubyJRuby.java
index 3932dfe101..b1344cd971 100644
--- a/src/org/jruby/RubyJRuby.java
+++ b/src/org/jruby/RubyJRuby.java
@@ -1,223 +1,224 @@
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
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
 package org.jruby;
 
 import java.io.IOException;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.JavaObject;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 
 import org.jruby.ast.Node;
 import org.jruby.compiler.ASTInspector;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.objectweb.asm.ClassReader;
 
 /**
  * Module which defines JRuby-specific methods for use. 
  */
 public class RubyJRuby {
     public static RubyModule createJRuby(Ruby runtime) {
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require", runtime.newString("java"));
         RubyModule jrubyModule = runtime.defineModule("JRuby");
         
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyJRuby.class);
         jrubyModule.defineAnnotatedMethods(RubyJRuby.class);
 
         RubyClass compiledScriptClass = jrubyModule.defineClassUnder("CompiledScript",runtime.getObject(), runtime.getObject().getAllocator());
         CallbackFactory compiledScriptCallbackFactory = runtime.callbackFactory(JRubyCompiledScript.class);
 
         compiledScriptClass.attr_accessor(new IRubyObject[]{runtime.newSymbol("name"), runtime.newSymbol("class_name"), runtime.newSymbol("original_script"), runtime.newSymbol("code")});
         compiledScriptClass.defineAnnotatedMethods(JRubyCompiledScript.class);
 
         return jrubyModule;
     }
 
     public static RubyModule createJRubyExt(Ruby runtime) {
         runtime.getKernel().callMethod(runtime.getCurrentContext(),"require", runtime.newString("java"));
         RubyModule mJRubyExt = runtime.getOrCreateModule("JRuby").defineModuleUnder("Extensions");
         CallbackFactory cf = runtime.callbackFactory(JRubyExtensions.class);
         
         mJRubyExt.defineAnnotatedMethods(JRubyExtensions.class);
 
         runtime.getObject().includeModule(mJRubyExt);
 
         return mJRubyExt;
     }
 
     public static class ExtLibrary implements Library {
         public void load(Ruby runtime) throws IOException {
             RubyJRuby.createJRubyExt(runtime);
         }
     }
     
     @JRubyMethod(name = "runtime", frame = true, module = true)
     public static IRubyObject runtime(IRubyObject recv, Block unusedBlock) {
         return Java.java_to_ruby(recv, JavaObject.wrap(recv.getRuntime(), recv.getRuntime()), Block.NULL_BLOCK);
     }
     
-    @JRubyMethod(name = "parse", alias = "ast_for", optional = 3, frame = true, module = true)
+    @JRubyMethod(name = {"parse", "ast_for"}, optional = 3, frame = true, module = true)
     public static IRubyObject parse(IRubyObject recv, IRubyObject[] args, Block block) {
         if(block.isGiven()) {
             Arity.checkArgumentCount(recv.getRuntime(),args,0,0);
             return Java.java_to_ruby(recv, JavaObject.wrap(recv.getRuntime(), block.getIterNode().getBodyNode()), Block.NULL_BLOCK);
         } else {
             Arity.checkArgumentCount(recv.getRuntime(),args,1,3);
             String filename = "-";
             boolean extraPositionInformation = false;
             RubyString content = args[0].convertToString();
             if(args.length>1) {
                 filename = args[1].convertToString().toString();
                 if(args.length>2) {
                     extraPositionInformation = args[2].isTrue();
                 }
             }
             return Java.java_to_ruby(recv, JavaObject.wrap(recv.getRuntime(), 
                recv.getRuntime().parse(content.toString(), filename, null, 0, extraPositionInformation)), Block.NULL_BLOCK);
         }
     }
 
     @JRubyMethod(name = "compile", optional = 3, frame = true, module = true)
     public static IRubyObject compile(IRubyObject recv, IRubyObject[] args, Block block) {
         Node node;
         String filename;
         RubyString content = recv.getRuntime().newString("");
         if(block.isGiven()) {
             Arity.checkArgumentCount(recv.getRuntime(),args,0,0);
             if(block instanceof org.jruby.runtime.CompiledBlock) {
                 throw new RuntimeException("Cannot compile an already compiled block. Use -J-Djruby.jit.enabled=false to avoid this problem.");
             }
             Node bnode = block.getIterNode().getBodyNode();
             node = new org.jruby.ast.RootNode(bnode.getPosition(), block.getDynamicScope(), bnode);
             filename = "__block_" + node.getPosition().getFile();
         } else {
             Arity.checkArgumentCount(recv.getRuntime(),args,1,3);
             filename = "-";
             boolean extraPositionInformation = false;
             content = args[0].convertToString();
             if(args.length>1) {
                 filename = args[1].convertToString().toString();
                 if(args.length>2) {
                     extraPositionInformation = args[2].isTrue();
                 }
             }
 
             node = recv.getRuntime().parse(content.toString(), filename, null, 0, extraPositionInformation);
         }
 
         String classname;
         if (filename.equals("-e")) {
             classname = "__dash_e__";
         } else {
             classname = filename.replace('\\', '/').replaceAll(".rb", "").replaceAll("-","_dash_");
         }
 
         ASTInspector inspector = new ASTInspector();
         inspector.inspect(node);
             
         StandardASMCompiler compiler = new StandardASMCompiler(classname, filename);
         ASTCompiler.compileRoot(node, compiler, inspector);
         byte[] bts = compiler.getClassByteArray();
 
         IRubyObject compiledScript = ((RubyModule)recv).fastGetConstant("CompiledScript").callMethod(recv.getRuntime().getCurrentContext(),"new");
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "name=", recv.getRuntime().newString(filename));
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "class_name=", recv.getRuntime().newString(classname));
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "original_script=", content);
         compiledScript.callMethod(recv.getRuntime().getCurrentContext(), "code=", Java.java_to_ruby(recv, JavaObject.wrap(recv.getRuntime(), bts), Block.NULL_BLOCK));
 
         return compiledScript;
     }
 
     @JRubyMethod(name = "reference", required = 1, module = true)
     public static IRubyObject reference(IRubyObject recv, IRubyObject obj) {
         return Java.wrap(recv.getRuntime().getJavaSupport().getJavaUtilitiesModule(),
                 JavaObject.wrap(recv.getRuntime(), obj));
     }
 
     public static class JRubyCompiledScript {
         @JRubyMethod(name = "to_s")
         public static IRubyObject compiled_script_to_s(IRubyObject recv) {
             return recv.fastGetInstanceVariable("@original_script");
         }
 
         @JRubyMethod(name = "inspect")
         public static IRubyObject compiled_script_inspect(IRubyObject recv) {
             return recv.getRuntime().newString("#<JRuby::CompiledScript " + recv.fastGetInstanceVariable("@name") + ">");
         }
 
         @JRubyMethod(name = "inspect_bytecode")
         public static IRubyObject compiled_script_inspect_bytecode(IRubyObject recv) {
             java.io.StringWriter sw = new java.io.StringWriter();
             org.objectweb.asm.ClassReader cr = new org.objectweb.asm.ClassReader((byte[])org.jruby.javasupport.JavaUtil.convertRubyToJava(recv.fastGetInstanceVariable("@code"),byte[].class));
             org.objectweb.asm.util.TraceClassVisitor cv = new org.objectweb.asm.util.TraceClassVisitor(new java.io.PrintWriter(sw));
             cr.accept(cv, ClassReader.SKIP_DEBUG);
             return recv.getRuntime().newString(sw.toString());
         }
     }
 
     public static class JRubyExtensions {
         @JRubyMethod(name = "steal_method", required = 2, module = true)
         public static IRubyObject steal_method(IRubyObject recv, IRubyObject type, IRubyObject methodName) {
             RubyModule to_add = null;
             if(recv instanceof RubyModule) {
                 to_add = (RubyModule)recv;
             } else {
                 to_add = recv.getSingletonClass();
             }
             String name = methodName.toString();
             if(!(type instanceof RubyModule)) {
                 throw recv.getRuntime().newArgumentError("First argument must be a module/class");
             }
 
             DynamicMethod method = ((RubyModule)type).searchMethod(name);
             if(method == null || method.isUndefined()) {
                 throw recv.getRuntime().newArgumentError("No such method " + name + " on " + type);
             }
 
             to_add.addMethod(name, method);
             return recv.getRuntime().getNil();
         }
 
         @JRubyMethod(name = "steal_methods", required = 1, rest = true, module = true)
         public static IRubyObject steal_methods(IRubyObject recv, IRubyObject[] args) {
             Arity.checkArgumentCount(recv.getRuntime(), args, 1, -1);
             IRubyObject type = args[0];
             for(int i=1;i<args.length;i++) {
                 steal_method(recv, type, args[i]);
             }
             return recv.getRuntime().getNil();
         }
     }
 }
diff --git a/src/org/jruby/RubyProcess.java b/src/org/jruby/RubyProcess.java
index c391207a39..6ca12f7b10 100644
--- a/src/org/jruby/RubyProcess.java
+++ b/src/org/jruby/RubyProcess.java
@@ -1,363 +1,366 @@
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 
 
 /**
  *
  * @author  enebo
  */
 public class RubyProcess {
 
     public static RubyModule createProcessModule(Ruby runtime) {
         RubyModule process = runtime.defineModule("Process");
         runtime.setProcess(process);
         
         // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
         RubyClass process_status = process.defineClassUnder("Status", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
         runtime.setProcStatus(process_status);
         
         RubyModule process_uid = process.defineModuleUnder("UID");
         runtime.setProcUID(process_uid);
+        
+        RubyModule process_gid = process.defineModuleUnder("GID");
+        runtime.setProcGID(process_gid);
 
-        CallbackFactory processCallbackFactory = runtime.callbackFactory(RubyProcess.class);
         CallbackFactory process_statusCallbackFactory = runtime.callbackFactory(RubyProcess.RubyStatus.class);
-        CallbackFactory processUIDCallbackFactory = runtime.callbackFactory(RubyProcess.UID.class);
         
         process.defineAnnotatedMethods(RubyProcess.class);
         process_status.defineAnnotatedMethods(RubyStatus.class);
-        process_uid.defineAnnotatedMethods(UID.class);
+        process_uid.defineAnnotatedMethods(UserAndGroupID.class);
+        process_gid.defineAnnotatedMethods(UserAndGroupID.class);
 
 //    #ifdef HAVE_GETPRIORITY
 //        rb_define_const(rb_mProcess, "PRIO_PROCESS", INT2FIX(PRIO_PROCESS));
 //        rb_define_const(rb_mProcess, "PRIO_PGRP", INT2FIX(PRIO_PGRP));
 //        rb_define_const(rb_mProcess, "PRIO_USER", INT2FIX(PRIO_USER));
 //    #endif
         
         // Process::Status methods  
         Callback notImplemented = process_statusCallbackFactory.getFastMethod("not_implemented");
         process_status.defineMethod("&", process_statusCallbackFactory.getFastMethod("not_implemented1", IRubyObject.class));
         process_status.defineMethod("to_int", notImplemented);
         process_status.defineMethod("pid", notImplemented);
         process_status.defineMethod("stopped?", notImplemented);
         process_status.defineMethod("stopsig", notImplemented);
         process_status.defineMethod("signaled?", notImplemented);
         process_status.defineMethod("termsig", notImplemented);
         process_status.defineMethod("exited?", notImplemented);
         process_status.defineMethod("coredump?", notImplemented);
         
         return process;
     }
     
     public static class RubyStatus extends RubyObject {
         private long status = 0L;
         
         private static final long EXIT_SUCCESS = 0L;
         public RubyStatus(Ruby runtime, RubyClass metaClass, long status) {
             super(runtime, metaClass);
             this.status = status;
         }
         
         public static RubyStatus newProcessStatus(Ruby runtime, long status) {
             return new RubyStatus(runtime, runtime.getProcStatus(), status);
         }
         
         public IRubyObject not_implemented() {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         public IRubyObject not_implemented1(IRubyObject arg) {
             String error = "Process::Status#" + getRuntime().getCurrentContext().getFrameName() + " not implemented";
             throw getRuntime().newNotImplementedError(error);
         }
         
         @JRubyMethod(name = "exitstatus")
         public IRubyObject exitstatus() {
             return getRuntime().newFixnum(status);
         }
         
         @JRubyMethod(name = ">>", required = 1)
         public IRubyObject op_rshift(IRubyObject other) {
             long shiftValue = other.convertToInteger().getLongValue();
             return getRuntime().newFixnum(status >> shiftValue);
         }
         
         @JRubyMethod(name = "==", required = 1)
         public IRubyObject op_equal(IRubyObject other) {
             return other.callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==", this.to_i());
         }
 
         @JRubyMethod(name = "to_i")
         public IRubyObject to_i() {
             return getRuntime().newFixnum(shiftedValue());
         }
         
         @JRubyMethod(name = "to_s")
         public IRubyObject to_s() {
             return getRuntime().newString(String.valueOf(shiftedValue()));
         }
         
         @JRubyMethod(name = "inspect")
         public IRubyObject inspect() {
             return getRuntime().newString("#<Process::Status: pid=????,exited(" + String.valueOf(status) + ")>");
         }
         
         @JRubyMethod(name = "success?")
         public IRubyObject success_p() {
             return getRuntime().newBoolean(status == EXIT_SUCCESS);
         }
         
         private long shiftedValue() {
             return status << 8;
         }
     }
     
-    public static class UID {
+    public static class UserAndGroupID {
         @JRubyMethod(name = "change_privilege", required = 1, module = true)
         public static IRubyObject change_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::change_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "eid", module = true)
         public static IRubyObject eid(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::eid not implemented yet");
         }
         
         @JRubyMethod(name = "eid=", required = 1, module = true)
         public static IRubyObject eid(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::eid= not implemented yet");
         }
         
         @JRubyMethod(name = "grant_privilege", required = 1, module = true)
         public static IRubyObject grant_privilege(IRubyObject self, IRubyObject arg) {
             throw self.getRuntime().newNotImplementedError("Process::UID::grant_privilege not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchange", module = true)
         public static IRubyObject re_exchange(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::re_exchange not implemented yet");
         }
         
         @JRubyMethod(name = "re_exchangeable?", module = true)
         public static IRubyObject re_exchangeable_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::re_exchangeable? not implemented yet");
         }
         
         @JRubyMethod(name = "rid", module = true)
         public static IRubyObject rid(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::rid not implemented yet");
         }
         
         @JRubyMethod(name = "sid_available?", module = true)
         public static IRubyObject sid_available_p(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::sid_available not implemented yet");
         }
         
         @JRubyMethod(name = "switch", module = true)
         public static IRubyObject switch_rb(IRubyObject self) {
             throw self.getRuntime().newNotImplementedError("Process::UID::switch not implemented yet");
         }
     }
 
     @JRubyMethod(name = "groups", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject groups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @JRubyMethod(name = "setrlimit", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject setrlimit(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#setrlimit not yet implemented");
     }
 
     @JRubyMethod(name = "getpgrp", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject getpgrp(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#getpgrp not yet implemented");
     }
 
     @JRubyMethod(name = "groups=", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject groups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#groups not yet implemented");
     }
 
     @JRubyMethod(name = "waitpid", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject waitpid(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#waitpid not yet implemented");
     }
 
     @JRubyMethod(name = "wait", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject wait(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#wait not yet implemented");
     }
 
     @JRubyMethod(name = "waitall", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject waitall(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#waitall not yet implemented");
     }
 
     @JRubyMethod(name = "setsid", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject setsid(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#setsid not yet implemented");
     }
 
     @JRubyMethod(name = "setpgrp", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject setpgrp(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#setpgrp not yet implemented");
     }
 
     @JRubyMethod(name = "egid=", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject egid_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#egid= not yet implemented");
     }
 
     @JRubyMethod(name = "euid", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject euid(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#euid not yet implemented");
     }
 
     @JRubyMethod(name = "uid=", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject uid_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#uid= not yet implemented");
     }
 
     @JRubyMethod(name = "gid", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject gid(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#gid not yet implemented");
     }
 
     @JRubyMethod(name = "maxgroups", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject maxgroups(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups not yet implemented");
     }
 
     @JRubyMethod(name = "getpriority", required = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject getpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         throw recv.getRuntime().newNotImplementedError("Process#getpriority not yet implemented");
     }
 
     @JRubyMethod(name = "uid", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject uid(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#uid not yet implemented");
     }
 
     @JRubyMethod(name = "waitpid2", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject waitpid2(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#waitpid2 not yet implemented");
     }
 
     @JRubyMethod(name = "initgroups", required = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject initgroups(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         throw recv.getRuntime().newNotImplementedError("Process#initgroups not yet implemented");
     }
 
     @JRubyMethod(name = "maxgroups=", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject maxgroups_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#maxgroups_set not yet implemented");
     }
 
     @JRubyMethod(name = "ppid", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject ppid(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#ppid not yet implemented");
     }
 
     @JRubyMethod(name = "gid=", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject gid_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#gid= not yet implemented");
     }
 
     @JRubyMethod(name = "wait2", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject wait2(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#wait2 not yet implemented");
     }
 
     @JRubyMethod(name = "euid=", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject euid_set(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#euid= not yet implemented");
     }
 
     @JRubyMethod(name = "setpriority", required = 3, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject setpriority(IRubyObject recv, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3) {
         throw recv.getRuntime().newNotImplementedError("Process#setpriority not yet implemented");
     }
 
     @JRubyMethod(name = "setpgid", required = 2, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject setpgid(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         throw recv.getRuntime().newNotImplementedError("Process#setpgid not yet implemented");
     }
 
     @JRubyMethod(name = "getpgid", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject getpgid(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#getpgid not yet implemented");
     }
 
     @JRubyMethod(name = "getrlimit", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject getrlimit(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#getrlimit not yet implemented");
     }
 
     @JRubyMethod(name = "egid", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject egid(IRubyObject recv) {
         throw recv.getRuntime().newNotImplementedError("Process#egid not yet implemented");
     }
 
     @JRubyMethod(name = "kill", rest = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject kill(IRubyObject recv, IRubyObject[] args) {
         throw recv.getRuntime().newNotImplementedError("Process#kill not yet implemented");
     }
 
     @JRubyMethod(name = "detach", required = 1, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject detach(IRubyObject recv, IRubyObject arg) {
         throw recv.getRuntime().newNotImplementedError("Process#detach not yet implemented");
     }
     
     @JRubyMethod(name = "times", frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
         Ruby runtime = recv.getRuntime();
         double currentTime = System.currentTimeMillis() / 1000.0;
         double startTime = runtime.getStartTime() / 1000.0;
         RubyFloat zero = runtime.newFloat(0.0);
         return RubyStruct.newStruct(runtime.getTmsStruct(), 
                 new IRubyObject[] { runtime.newFloat(currentTime - startTime), zero, zero, zero }, 
                 Block.NULL_BLOCK);
     }
 
     @JRubyMethod(name = "pid", module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject pid(IRubyObject recv) {
         return recv.getRuntime().newFixnum(System.identityHashCode(recv.getRuntime()));
     }
 }
diff --git a/src/org/jruby/RubyString.java b/src/org/jruby/RubyString.java
index 07eadbe36e..81878fd0a8 100644
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
@@ -1,3393 +1,3394 @@
-/***** BEGIN LICENSE BLOCK *****
+/*
+ **** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2004 David Corbin <dcorbin@users.sourceforge.net>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 
 import java.io.UnsupportedEncodingException;
 import java.nio.ByteBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetDecoder;
 import java.nio.charset.CodingErrorAction;
 import java.util.Locale;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.regexp.RegexpMatcher;
 import org.jruby.regexp.RegexpPattern;
 import org.jruby.runtime.Arity;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ByteList;
 import org.jruby.util.KCode;
 import org.jruby.util.Pack;
 import org.jruby.util.Sprintf;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyString extends RubyObject {
     // string doesn't have it's own ByteList (values) 
     private static final int SHARED_BUFFER_STR_F = 1 << 9;
     // string has it's own ByteList, but it's pointing to a shared buffer (byte[])
     private static final int SHARED_BYTELIST_STR_F = 1 << 10;
     // string has either ByteList or buffer shared  
     private static final int SHARED_STR_F = SHARED_BUFFER_STR_F | SHARED_BYTELIST_STR_F;  
 
     private ByteList value;
 
     private static ObjectAllocator STRING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyString newString = runtime.newString("");
             
             newString.setMetaClass(klass);
             
             return newString;
         }
     };
     
     public static RubyClass createStringClass(Ruby runtime) {
         RubyClass stringClass = runtime.defineClass("String", runtime.getObject(), STRING_ALLOCATOR);
         runtime.setString(stringClass);
         stringClass.index = ClassIndex.STRING;
         stringClass.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyString;
                 }
             };
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyString.class);
         
         stringClass.includeModule(runtime.getComparable());
         stringClass.includeModule(runtime.getEnumerable());
         
         stringClass.defineAnnotatedMethods(RubyString.class);
         stringClass.dispatcher = callbackFactory.createDispatcher(stringClass);
         
         return stringClass;
     }
 
     /** short circuit for String key comparison
      * 
      */
     public final boolean eql(IRubyObject other) {
         return other instanceof RubyString && value.equal(((RubyString)other).value);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, CharSequence value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(ByteList.plain(value),false);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, byte[] value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = new ByteList(value);
     }
 
     private RubyString(Ruby runtime, RubyClass rubyClass, ByteList value) {
         super(runtime, rubyClass);
         assert value != null;
         this.value = value;
     }
     
     private RubyString(Ruby runtime, RubyClass rubyClass, ByteList value, boolean objectSpace) {
         super(runtime, rubyClass, objectSpace);
         assert value != null;
         this.value = value;
     }    
 
     public int getNativeTypeIndex() {
         return ClassIndex.STRING;
     }
 
     public Class getJavaClass() {
         return String.class;
     }
 
     public RubyString convertToString() {
         return this;
     }
 
     public String toString() {
         return value.toString();
     }
 
     /** rb_str_dup
      * 
      */
     public final RubyString strDup() {
         return strDup(getMetaClass());
     }
 
     private final RubyString strDup(RubyClass clazz) {
         flags |= SHARED_BYTELIST_STR_F;
         RubyString dup = new RubyString(getRuntime(), clazz, value);
         dup.flags |= SHARED_BYTELIST_STR_F;
 
         dup.infectBy(this);
         return dup;
     }    
 
     public final RubyString makeShared(int index, int len) {
         if (len == 0) return newEmptyString(getRuntime(), getMetaClass());
         
         if ((flags & SHARED_STR_F) == 0) flags |= SHARED_BUFFER_STR_F;
         RubyString shared = new RubyString(getRuntime(), getMetaClass(), value.makeShared(index, len));
         shared.flags |= SHARED_BUFFER_STR_F;
 
         shared.infectBy(this);
         return shared;
     }
 
     private final void modifyCheck() {
         // TODO: tmp lock here!
         testFrozen("string");
 
         if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify string");
         }
     }
     
     private final void modifyCheck(byte[] b, int len) {
         if (value.bytes != b || value.realSize != len) throw getRuntime().newRuntimeError("string modified");
     }
     
     private final void frozenCheck() {
         if (isFrozen()) throw getRuntime().newRuntimeError("string frozen");
     }
 
     /** rb_str_modify
      * 
      */
 
     public final void modify() {
         modifyCheck();
 
         if ((flags & SHARED_STR_F) != 0) {
             if ((flags & SHARED_BYTELIST_STR_F) != 0) {
                 value = value.dup();
             } else if ((flags & SHARED_BUFFER_STR_F) != 0) {
                 value.unshare();
             }
             flags &= ~SHARED_STR_F;
         }
 
         value.invalidate();
     }
     
     public final void modify(int length) {
         modifyCheck();
 
         if ((flags & SHARED_STR_F) != 0) {
             if ((flags & SHARED_BYTELIST_STR_F) != 0) {
                 value = value.dup(length);
             } else if ((flags & SHARED_BUFFER_STR_F) != 0) {
                 value.unshare(length);
             }
             flags &= ~SHARED_STR_F;
         } else {
             value = value.dup(length);
         }
         value.invalidate();
     }        
     
     private final void view(ByteList bytes) {
         modifyCheck();
 
         value = bytes;
         flags &= ~SHARED_STR_F; 
     }
 
     private final void view(byte[]bytes) {
         modifyCheck();        
 
         value.replace(bytes);
         flags &= ~SHARED_STR_F;
 
         value.invalidate();        
     }
 
     private final void view(int index, int len) {
         modifyCheck();
 
         if ((flags & SHARED_STR_F) != 0) {
             if ((flags & SHARED_BYTELIST_STR_F) != 0) {
                 // if len == 0 then shared empty
                 value = value.makeShared(index, len);
                 flags &= ~SHARED_BYTELIST_STR_F;
                 flags |= SHARED_BUFFER_STR_F; 
             } else if ((flags & SHARED_BUFFER_STR_F) != 0) { 
                 value.view(index, len);
             }
         } else {        
             value.view(index, len);
             // FIXME this below is temporary, but its much safer for COW (it prevents not shared Strings with begin != 0)
             // this allows now e.g.: ByteList#set not to be begin aware
             flags |= SHARED_BUFFER_STR_F;
         }
 
         value.invalidate();
     }
 
     public static String bytesToString(byte[] bytes, int beg, int len) {
         return new String(ByteList.plain(bytes, beg, len));
     }
 
     public static String byteListToString(ByteList bytes) {
         return bytesToString(bytes.unsafeBytes(), bytes.begin(), bytes.length());
     }
 
     public static String bytesToString(byte[] bytes) {
         return bytesToString(bytes, 0, bytes.length);
     }
 
     public static byte[] stringToBytes(String string) {
         return ByteList.plain(string);
     }
 
     public static boolean isDigit(int c) {
         return c >= '0' && c <= '9';
     }
 
     public static boolean isUpper(int c) {
         return c >= 'A' && c <= 'Z';
     }
 
     public static boolean isLower(int c) {
         return c >= 'a' && c <= 'z';
     }
 
     public static boolean isLetter(int c) {
         return isUpper(c) || isLower(c);
     }
 
     public static boolean isAlnum(int c) {
         return isUpper(c) || isLower(c) || isDigit(c);
     }
 
     public static boolean isPrint(int c) {
         return c >= 0x20 && c <= 0x7E;
     }
 
     public RubyString asString() {
         return this;
     }
 
     public IRubyObject checkStringType() {
         return this;
     }
 
     @JRubyMethod(name = {"to_s", "to_str"})
     public IRubyObject to_s() {
         if (getMetaClass().getRealClass() != getRuntime().getString()) {
             return strDup(getRuntime().getString());
         }
         return this;
     }
 
     /* rb_str_cmp_m */
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newFixnum(op_cmp((RubyString)other));
         }
 
         return getRuntime().getNil();
     }
         
     /**
      * 
      */
     @JRubyMethod(name = "==", required = 1)
     public IRubyObject op_equal(IRubyObject other) {
         if (this == other) return getRuntime().getTrue();
         if (!(other instanceof RubyString)) {
             if (!other.respondsTo("to_str")) return getRuntime().getFalse();
             Ruby runtime = getRuntime();
             return other.callMethod(runtime.getCurrentContext(), MethodIndex.EQUALEQUAL, "==", this).isTrue() ? runtime.getTrue() : runtime.getFalse();
         }
         return value.equal(((RubyString)other).value) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject other) {
         RubyString str = RubyString.stringValue(other);
 
         ByteList newValue = new ByteList(value.length() + str.value.length());
         newValue.append(value);
         newValue.append(str.value);
         return newString(getRuntime(), newValue).infectBy(other).infectBy(this);
     }
 
     @JRubyMethod(name = "*", required = 1)
     public IRubyObject op_mul(IRubyObject other) {
         RubyInteger otherInteger = (RubyInteger) other.convertToInteger();
         long len = otherInteger.getLongValue();
 
         if (len < 0) throw getRuntime().newArgumentError("negative argument");
 
         // we limit to int because ByteBuffer can only allocate int sizes
         if (len > 0 && Integer.MAX_VALUE / len < value.length()) {
             throw getRuntime().newArgumentError("argument too big");
         }
         ByteList newBytes = new ByteList(value.length() * (int)len);
 
         for (int i = 0; i < len; i++) {
             newBytes.append(value);
         }
 
         RubyString newString = newString(getRuntime(), newBytes);
         newString.setTaint(isTaint());
         return newString;
     }
 
     @JRubyMethod(name = "%", required = 1)
     public IRubyObject op_format(IRubyObject arg) {
         // FIXME: Should we make this work with platform's locale, or continue hardcoding US?
         return getRuntime().newString((ByteList)Sprintf.sprintf(Locale.US, value, arg));
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
         return getRuntime().newFixnum(value.hashCode());
     }
 
     public int hashCode() {
         return value.hashCode();
     }
 
     public boolean equals(Object other) {
         if (this == other) return true;
 
         if (other instanceof RubyString) {
             RubyString string = (RubyString) other;
 
             if (string.value.equal(value)) return true;
         }
 
         return false;
     }
 
     /** rb_obj_as_string
      *
      */
     public static RubyString objAsString(IRubyObject obj) {
         if (obj instanceof RubyString) return (RubyString) obj;
 
         IRubyObject str = obj.callMethod(obj.getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s");
 
         if (!(str instanceof RubyString)) return (RubyString) obj.anyToString();
 
         if (obj.isTaint()) str.setTaint(true);
 
         return (RubyString) str;
     }
 
     /** rb_str_cmp
      *
      */
     public int op_cmp(RubyString other) {
         return value.cmp(other.value);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         // TODO: callers that don't need interned string should be modified
         // to call toString, there are just a handful of places this happens.
 
         // this must be interned here - call #toString for non-interned value
         return toString().intern();
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *
      */
     public RubyString newString(CharSequence s) {
         return new RubyString(getRuntime(), getType(), s);
     }
 
     /** Create a new String which uses the same Ruby runtime and the same
      *  class like this String.
      *
      *  This method should be used to satisfy RCR #38.
      *
      */
     public RubyString newString(ByteList s) {
         return new RubyString(getRuntime(), getMetaClass(), s);
     }
 
     // Methods of the String class (rb_str_*):
 
     /** rb_str_new2
      *
      */
     public static RubyString newString(Ruby runtime, CharSequence str) {
         return new RubyString(runtime, runtime.getString(), str);
     }
     
     private static RubyString newEmptyString(Ruby runtime, RubyClass metaClass) {
         RubyString empty = new RubyString(runtime, metaClass, ByteList.EMPTY_BYTELIST);
         empty.flags |= SHARED_BYTELIST_STR_F;
         return empty;
     }
 
     public static RubyString newUnicodeString(Ruby runtime, String str) {
         try {
             return new RubyString(runtime, runtime.getString(), new ByteList(str.getBytes("UTF8"), false));
         } catch (UnsupportedEncodingException uee) {
             return new RubyString(runtime, runtime.getString(), str);
         }
     }
 
     public static RubyString newString(Ruby runtime, RubyClass clazz, CharSequence str) {
         return new RubyString(runtime, clazz, str);
     }
 
     public static RubyString newString(Ruby runtime, byte[] bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newString(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes);
     }
 
     public static RubyString newStringLight(Ruby runtime, ByteList bytes) {
         return new RubyString(runtime, runtime.getString(), bytes, false);
     }
 
     public static RubyString newStringShared(Ruby runtime, RubyString orig) {
         orig.flags |= SHARED_BYTELIST_STR_F;
         RubyString str = new RubyString(runtime, runtime.getString(), orig.value);
         str.flags |= SHARED_BYTELIST_STR_F;
         return str;
     }       
     
     public static RubyString newStringShared(Ruby runtime, ByteList bytes) {
         return newStringShared(runtime, runtime.getString(), bytes);
     }    
 
     public static RubyString newStringShared(Ruby runtime, RubyClass clazz, ByteList bytes) {
         RubyString str = new RubyString(runtime, clazz, bytes);
         str.flags |= SHARED_BYTELIST_STR_F;
         return str;
     }    
 
     public static RubyString newString(Ruby runtime, byte[] bytes, int start, int length) {
         byte[] bytes2 = new byte[length];
         System.arraycopy(bytes, start, bytes2, 0, length);
         return new RubyString(runtime, runtime.getString(), new ByteList(bytes2, false));
     }
 
     public IRubyObject doClone(){
         return newString(getRuntime(), value.dup());
     }
 
     // FIXME: cat methods should be more aware of sharing to prevent unnecessary reallocations in certain situations 
     public RubyString cat(byte[] str) {
         modify();
         value.append(str);
         return this;
     }
 
     public RubyString cat(byte[] str, int beg, int len) {
         modify();        
         value.append(str, beg, len);
         return this;
     }
 
     public RubyString cat(ByteList str) {
         modify();        
         value.append(str);
         return this;
     }
 
     public RubyString cat(byte ch) {
         modify();        
         value.append(ch);
         return this;
     }
 
     /** rb_str_replace_m
      *
      */
     @JRubyMethod(name = {"replace", "initialize_copy"}, required = 1)
     public RubyString replace(IRubyObject other) {
         modifyCheck();
 
         if (this == other) return this;
          
         RubyString otherStr =  stringValue(other);
 
         flags |= SHARED_BYTELIST_STR_F;
         otherStr.flags |= SHARED_BYTELIST_STR_F;
         
         value = otherStr.value;
 
         infectBy(other);
         return this;
     }
 
     @JRubyMethod(name = "reverse")
     public RubyString reverse() {
         if (value.length() <= 1) return strDup();
 
         ByteList buf = new ByteList(value.length()+2);
         buf.realSize = value.length();
         int src = value.length() - 1;
         int dst = 0;
         
         while (src >= 0) buf.set(dst++, value.get(src--));
 
         RubyString rev = new RubyString(getRuntime(), getMetaClass(), buf);
         rev.infectBy(this);
         return rev;
     }
 
     @JRubyMethod(name = "reverse!")
     public RubyString reverse_bang() {
         if (value.length() > 1) {
             modify();
             for (int i = 0; i < (value.length() / 2); i++) {
                 byte b = (byte) value.get(i);
                 
                 value.set(i, value.get(value.length() - i - 1));
                 value.set(value.length() - i - 1, b);
             }
         }
         
         return this;
     }
 
     /** rb_str_s_new
      *
      */
     public static RubyString newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString newString = newString(recv.getRuntime(), "");
         newString.setMetaClass((RubyClass) recv);
         newString.callInit(args, block);
         return newString;
     }
 
     @JRubyMethod(name = "initialize", optional = 1, frame = true)
     public IRubyObject initialize(IRubyObject[] args, Block unusedBlock) {
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 1) replace(args[0]);
 
         return this;
     }
 
     @JRubyMethod(name = "casecmp", required = 1)
     public IRubyObject casecmp(IRubyObject other) {
         int compare = toString().compareToIgnoreCase(stringValue(other).toString());
         
         return RubyFixnum.newFixnum(getRuntime(), compare == 0 ? 0 : (compare < 0 ? -1 : 1));
     }
 
     /** rb_str_match
      *
      */
     @JRubyMethod(name = "=~", required = 1)
     public IRubyObject op_match(IRubyObject other) {
         if (other instanceof RubyRegexp) return ((RubyRegexp) other).op_match(this);
         if (other instanceof RubyString) {
             throw getRuntime().newTypeError("type mismatch: String given");
         }
         return other.callMethod(getRuntime().getCurrentContext(), "=~", this);
     }
 
     /** rb_str_match2
      *
      */
     @JRubyMethod(name = "~")
     public IRubyObject op_match2() {
         return RubyRegexp.newRegexp(this, 0, null).op_match2();
     }
 
     /**
      * String#match(pattern)
      *
      * @param pattern Regexp or String
      */
     @JRubyMethod(name = "match", required = 1)
     public IRubyObject match(IRubyObject pattern) {
         IRubyObject res = null;
         if (pattern instanceof RubyRegexp) {
             res = ((RubyRegexp)pattern).search2(toString(), this);
         } else if (pattern instanceof RubyString) {
             RubyRegexp regexp = RubyRegexp.newRegexp((RubyString) pattern, 0, null);
             res = regexp.search2(toString(), this);
         } else if (pattern.respondsTo("to_str")) {
             // FIXME: is this cast safe?
             RubyRegexp regexp = RubyRegexp.newRegexp((RubyString) pattern.callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_STR, "to_str", IRubyObject.NULL_ARRAY), 0, null);
             res = regexp.search2(toString(), this);
         } else {
             // not regexp and not string, can't convert
             throw getRuntime().newTypeError("wrong argument type " + pattern.getMetaClass().getBaseName() + " (expected Regexp)");
         }
         
         if(res instanceof RubyMatchData) {
             ((RubyMatchData)res).use();
         }
         return res;
     }
 
     /** rb_str_capitalize
      *
      */
     @JRubyMethod(name = "capitalize")
     public IRubyObject capitalize() {
         RubyString str = strDup();
         str.capitalize_bang();
         return str;
     }
 
     /** rb_str_capitalize_bang
      *
      */
     @JRubyMethod(name = "capitalize!")
     public IRubyObject capitalize_bang() {
         if (value.realSize == 0) return getRuntime().getNil();
         
         modify();
         
         int s = value.begin;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         
         
         
         boolean modify = false;
         
         char c = (char)(buf[s] & 0xff);
         if (Character.isLetter(c) && Character.isLowerCase(c)) {
             buf[s] = (byte)Character.toUpperCase(c);
             modify = true;
         }
         
         while (++s < send) {
             c = (char)(buf[s] & 0xff);
             if (Character.isLetter(c) && Character.isUpperCase(c)) {
                 buf[s] = (byte)Character.toLowerCase(c);
                 modify = true;
             }
         }
         
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(op_cmp((RubyString) other) >= 0);
         }
 
         return RubyComparable.op_ge(this, other);
     }
 
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(op_cmp((RubyString) other) > 0);
         }
 
         return RubyComparable.op_gt(this, other);
     }
 
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(op_cmp((RubyString) other) <= 0);
         }
 
         return RubyComparable.op_le(this, other);
     }
 
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(IRubyObject other) {
         if (other instanceof RubyString) {
             return getRuntime().newBoolean(op_cmp((RubyString) other) < 0);
         }
 
         return RubyComparable.op_lt(this, other);
     }
 
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(IRubyObject other) {
         if (!(other instanceof RubyString)) return getRuntime().getFalse();
         RubyString otherString = (RubyString)other;
         return value.equal(otherString.value) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     /** rb_str_upcase
      *
      */
     @JRubyMethod(name = "upcase")
     public RubyString upcase() {
         RubyString str = strDup();
         str.upcase_bang();
         return str;
     }
 
     /** rb_str_upcase_bang
      *
      */
     @JRubyMethod(name = "upcase!")
     public IRubyObject upcase_bang() {
         if (value.realSize == 0)  return getRuntime().getNil();
 
         modify();
 
         int s = value.begin;
         int send = s + value.realSize;
         byte []buf = value.bytes;
 
         boolean modify = false;
         while (s < send) {
             char c = (char)(buf[s] & 0xff);
             if (c >= 'a' && c<= 'z') {
                 buf[s] = (byte)(c-32);
                 modify = true;
             }
             s++;
         }
 
         if (modify) return this;
         return getRuntime().getNil();        
     }
 
     /** rb_str_downcase
      *
      */
     @JRubyMethod(name = "downcase")
     public RubyString downcase() {
         RubyString str = strDup();
         str.downcase_bang();
         return str;
     }
 
     /** rb_str_downcase_bang
      *
      */
     @JRubyMethod(name = "downcase!")
     public IRubyObject downcase_bang() {
         if (value.realSize == 0)  return getRuntime().getNil();
         
         modify();
         
         int s = value.begin;
         int send = s + value.realSize;
         byte []buf = value.bytes;
         
         boolean modify = false;
         while (s < send) {
             char c = (char)(buf[s] & 0xff);
             if (c >= 'A' && c <= 'Z') {
                 buf[s] = (byte)(c+32);
                 modify = true;
             }
             s++;
         }
         
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     /** rb_str_swapcase
      *
      */
     @JRubyMethod(name = "swapcase")
     public RubyString swapcase() {
         RubyString str = strDup();
         str.swapcase_bang();
         return str;
     }
 
     /** rb_str_swapcase_bang
      *
      */
     @JRubyMethod(name = "swapcase!")
     public IRubyObject swapcase_bang() {
         if (value.realSize == 0)  return getRuntime().getNil();        
         
         modify();
         
         int s = value.begin;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         
         boolean modify = false;
         while (s < send) {
             char c = (char)(buf[s] & 0xff);
             if (Character.isLetter(c)) {
                 if (Character.isUpperCase(c)) {
                     buf[s] = (byte)Character.toLowerCase(c);
                     modify = true;
                 } else {
                     buf[s] = (byte)Character.toUpperCase(c);
                     modify = true;
                 }
             }
             s++;
         }
 
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     /** rb_str_dump
      *
      */
     @JRubyMethod(name = "dump")
     public IRubyObject dump() {
         return inspect();
     }
 
     @JRubyMethod(name = "insert", required = 2)
     public IRubyObject insert(IRubyObject indexArg, IRubyObject stringArg) {
         int index = (int) indexArg.convertToInteger().getLongValue();
         if (index < 0) index += value.length() + 1;
 
         if (index < 0 || index > value.length()) {
             throw getRuntime().newIndexError("index " + index + " out of range");
         }
 
         modify();
         
         ByteList insert = ((RubyString)stringArg.convertToString()).value;
         value.unsafeReplace(index, 0, insert);
         return this;
     }
 
     /** rb_str_inspect
      *
      */
     @JRubyMethod(name = "inspect")
     public IRubyObject inspect() {
         final int length = value.length();
         Ruby runtime = getRuntime();
         ByteList sb = new ByteList(length + 2 + length / 100);
 
         sb.append('\"');
 
         // FIXME: This may not be unicode-safe
         for (int i = 0; i < length; i++) {
             int c = value.get(i) & 0xFF;
             if (isAlnum(c)) {
                 sb.append((char)c);
             } else if (runtime.getKCode() == KCode.UTF8 && c == 0xEF) {
                 // don't escape encoded UTF8 characters, leave them as bytes
                 // append byte order mark plus two character bytes
                 sb.append((char)c);
                 sb.append((char)(value.get(++i) & 0xFF));
                 sb.append((char)(value.get(++i) & 0xFF));
             } else if (c == '\"' || c == '\\') {
                 sb.append('\\').append((char)c);
             } else if (c == '#' && isEVStr(i, length)) {
                 sb.append('\\').append((char)c);
             } else if (isPrint(c)) {
                 sb.append((char)c);
             } else if (c == '\n') {
                 sb.append('\\').append('n');
             } else if (c == '\r') {
                 sb.append('\\').append('r');
             } else if (c == '\t') {
                 sb.append('\\').append('t');
             } else if (c == '\f') {
                 sb.append('\\').append('f');
             } else if (c == '\u000B') {
                 sb.append('\\').append('v');
             } else if (c == '\u0007') {
                 sb.append('\\').append('a');
             } else if (c == '\u001B') {
                 sb.append('\\').append('e');
             } else {
                 sb.append(ByteList.plain(Sprintf.sprintf(runtime,"\\%.3o",c)));
             }
         }
 
         sb.append('\"');
         return getRuntime().newString(sb);
     }
     
     private boolean isEVStr(int i, int length) {
         if (i+1 >= length) return false;
         int c = value.get(i+1) & 0xFF;
         
         return c == '$' || c == '@' || c == '{';
     }
 
     /** rb_str_length
      *
      */
-    @JRubyMethod(name = "length", alias = "size")
+    @JRubyMethod(name = {"length", "size"})
     public RubyFixnum length() {
         return getRuntime().newFixnum(value.length());
     }
 
     /** rb_str_empty
      *
      */
     @JRubyMethod(name = "empty?")
     public RubyBoolean empty_p() {
         return isEmpty() ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     private boolean isEmpty() {
         return value.length() == 0;
     }
 
     /** rb_str_append
      *
      */
     public RubyString append(IRubyObject other) {
         infectBy(other);
         return cat(stringValue(other).value);
     }
 
     /** rb_str_concat
      *
      */
-    @JRubyMethod(name = "concat", alias = "<<", required = 1)
+    @JRubyMethod(name = {"concat", "<<"}, required = 1)
     public RubyString concat(IRubyObject other) {
         if (other instanceof RubyFixnum) {
             long value = ((RubyFixnum) other).getLongValue();
             if (value >= 0 && value < 256) return cat((byte) value);
         }
         return append(other);
     }
 
     /** rb_str_crypt
      *
      */
     @JRubyMethod(name = "crypt", required = 1)
     public RubyString crypt(IRubyObject other) {
         String salt = stringValue(other).getValue().toString();
         if (salt.length() < 2) {
             throw getRuntime().newArgumentError("salt too short(need >=2 bytes)");
         }
 
         salt = salt.substring(0, 2);
         return getRuntime().newString(JavaCrypt.crypt(salt, this.toString()));
     }
 
 
     public static class JavaCrypt {
         private static java.util.Random r_gen = new java.util.Random();
 
         private static final char theBaseSalts[] = {
             'a','b','c','d','e','f','g','h','i','j','k','l','m',
             'n','o','p','q','r','s','t','u','v','w','x','y','z',
             'A','B','C','D','E','F','G','H','I','J','K','L','M',
             'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
             '0','1','2','3','4','5','6','7','8','9','/','.'};
 
         private static final int ITERATIONS = 16;
 
         private static final int con_salt[] = {
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
             0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
             0x0A, 0x0B, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A,
             0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12,
             0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A,
             0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22,
             0x23, 0x24, 0x25, 0x20, 0x21, 0x22, 0x23, 0x24,
             0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C,
             0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34,
             0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C,
             0x3D, 0x3E, 0x3F, 0x00, 0x00, 0x00, 0x00, 0x00,
         };
 
         private static final boolean shifts2[] = {
             false, false, true, true, true, true, true, true,
             false, true,  true, true, true, true, true, false };
 
         private static final int skb[][] = {
             {
                 /* for C bits (numbered as per FIPS 46) 1 2 3 4 5 6 */
                 0x00000000, 0x00000010, 0x20000000, 0x20000010,
                 0x00010000, 0x00010010, 0x20010000, 0x20010010,
                 0x00000800, 0x00000810, 0x20000800, 0x20000810,
                 0x00010800, 0x00010810, 0x20010800, 0x20010810,
                 0x00000020, 0x00000030, 0x20000020, 0x20000030,
                 0x00010020, 0x00010030, 0x20010020, 0x20010030,
                 0x00000820, 0x00000830, 0x20000820, 0x20000830,
                 0x00010820, 0x00010830, 0x20010820, 0x20010830,
                 0x00080000, 0x00080010, 0x20080000, 0x20080010,
                 0x00090000, 0x00090010, 0x20090000, 0x20090010,
                 0x00080800, 0x00080810, 0x20080800, 0x20080810,
                 0x00090800, 0x00090810, 0x20090800, 0x20090810,
                 0x00080020, 0x00080030, 0x20080020, 0x20080030,
                 0x00090020, 0x00090030, 0x20090020, 0x20090030,
                 0x00080820, 0x00080830, 0x20080820, 0x20080830,
                 0x00090820, 0x00090830, 0x20090820, 0x20090830,
             },{
                 /* for C bits (numbered as per FIPS 46) 7 8 10 11 12 13 */
                 0x00000000, 0x02000000, 0x00002000, 0x02002000,
                 0x00200000, 0x02200000, 0x00202000, 0x02202000,
                 0x00000004, 0x02000004, 0x00002004, 0x02002004,
                 0x00200004, 0x02200004, 0x00202004, 0x02202004,
                 0x00000400, 0x02000400, 0x00002400, 0x02002400,
                 0x00200400, 0x02200400, 0x00202400, 0x02202400,
                 0x00000404, 0x02000404, 0x00002404, 0x02002404,
                 0x00200404, 0x02200404, 0x00202404, 0x02202404,
                 0x10000000, 0x12000000, 0x10002000, 0x12002000,
                 0x10200000, 0x12200000, 0x10202000, 0x12202000,
                 0x10000004, 0x12000004, 0x10002004, 0x12002004,
                 0x10200004, 0x12200004, 0x10202004, 0x12202004,
                 0x10000400, 0x12000400, 0x10002400, 0x12002400,
                 0x10200400, 0x12200400, 0x10202400, 0x12202400,
                 0x10000404, 0x12000404, 0x10002404, 0x12002404,
                 0x10200404, 0x12200404, 0x10202404, 0x12202404,
             },{
                 /* for C bits (numbered as per FIPS 46) 14 15 16 17 19 20 */
                 0x00000000, 0x00000001, 0x00040000, 0x00040001,
                 0x01000000, 0x01000001, 0x01040000, 0x01040001,
                 0x00000002, 0x00000003, 0x00040002, 0x00040003,
                 0x01000002, 0x01000003, 0x01040002, 0x01040003,
                 0x00000200, 0x00000201, 0x00040200, 0x00040201,
                 0x01000200, 0x01000201, 0x01040200, 0x01040201,
                 0x00000202, 0x00000203, 0x00040202, 0x00040203,
                 0x01000202, 0x01000203, 0x01040202, 0x01040203,
                 0x08000000, 0x08000001, 0x08040000, 0x08040001,
                 0x09000000, 0x09000001, 0x09040000, 0x09040001,
                 0x08000002, 0x08000003, 0x08040002, 0x08040003,
                 0x09000002, 0x09000003, 0x09040002, 0x09040003,
                 0x08000200, 0x08000201, 0x08040200, 0x08040201,
                 0x09000200, 0x09000201, 0x09040200, 0x09040201,
                 0x08000202, 0x08000203, 0x08040202, 0x08040203,
                 0x09000202, 0x09000203, 0x09040202, 0x09040203,
             },{
                 /* for C bits (numbered as per FIPS 46) 21 23 24 26 27 28 */
                 0x00000000, 0x00100000, 0x00000100, 0x00100100,
                 0x00000008, 0x00100008, 0x00000108, 0x00100108,
                 0x00001000, 0x00101000, 0x00001100, 0x00101100,
                 0x00001008, 0x00101008, 0x00001108, 0x00101108,
                 0x04000000, 0x04100000, 0x04000100, 0x04100100,
                 0x04000008, 0x04100008, 0x04000108, 0x04100108,
                 0x04001000, 0x04101000, 0x04001100, 0x04101100,
                 0x04001008, 0x04101008, 0x04001108, 0x04101108,
                 0x00020000, 0x00120000, 0x00020100, 0x00120100,
                 0x00020008, 0x00120008, 0x00020108, 0x00120108,
                 0x00021000, 0x00121000, 0x00021100, 0x00121100,
                 0x00021008, 0x00121008, 0x00021108, 0x00121108,
                 0x04020000, 0x04120000, 0x04020100, 0x04120100,
                 0x04020008, 0x04120008, 0x04020108, 0x04120108,
                 0x04021000, 0x04121000, 0x04021100, 0x04121100,
                 0x04021008, 0x04121008, 0x04021108, 0x04121108,
             },{
                 /* for D bits (numbered as per FIPS 46) 1 2 3 4 5 6 */
                 0x00000000, 0x10000000, 0x00010000, 0x10010000,
                 0x00000004, 0x10000004, 0x00010004, 0x10010004,
                 0x20000000, 0x30000000, 0x20010000, 0x30010000,
                 0x20000004, 0x30000004, 0x20010004, 0x30010004,
                 0x00100000, 0x10100000, 0x00110000, 0x10110000,
                 0x00100004, 0x10100004, 0x00110004, 0x10110004,
                 0x20100000, 0x30100000, 0x20110000, 0x30110000,
                 0x20100004, 0x30100004, 0x20110004, 0x30110004,
                 0x00001000, 0x10001000, 0x00011000, 0x10011000,
                 0x00001004, 0x10001004, 0x00011004, 0x10011004,
                 0x20001000, 0x30001000, 0x20011000, 0x30011000,
                 0x20001004, 0x30001004, 0x20011004, 0x30011004,
                 0x00101000, 0x10101000, 0x00111000, 0x10111000,
                 0x00101004, 0x10101004, 0x00111004, 0x10111004,
                 0x20101000, 0x30101000, 0x20111000, 0x30111000,
                 0x20101004, 0x30101004, 0x20111004, 0x30111004,
             },{
                 /* for D bits (numbered as per FIPS 46) 8 9 11 12 13 14 */
                 0x00000000, 0x08000000, 0x00000008, 0x08000008,
                 0x00000400, 0x08000400, 0x00000408, 0x08000408,
                 0x00020000, 0x08020000, 0x00020008, 0x08020008,
                 0x00020400, 0x08020400, 0x00020408, 0x08020408,
                 0x00000001, 0x08000001, 0x00000009, 0x08000009,
                 0x00000401, 0x08000401, 0x00000409, 0x08000409,
                 0x00020001, 0x08020001, 0x00020009, 0x08020009,
                 0x00020401, 0x08020401, 0x00020409, 0x08020409,
                 0x02000000, 0x0A000000, 0x02000008, 0x0A000008,
                 0x02000400, 0x0A000400, 0x02000408, 0x0A000408,
                 0x02020000, 0x0A020000, 0x02020008, 0x0A020008,
                 0x02020400, 0x0A020400, 0x02020408, 0x0A020408,
                 0x02000001, 0x0A000001, 0x02000009, 0x0A000009,
                 0x02000401, 0x0A000401, 0x02000409, 0x0A000409,
                 0x02020001, 0x0A020001, 0x02020009, 0x0A020009,
                 0x02020401, 0x0A020401, 0x02020409, 0x0A020409,
             },{
                 /* for D bits (numbered as per FIPS 46) 16 17 18 19 20 21 */
                 0x00000000, 0x00000100, 0x00080000, 0x00080100,
                 0x01000000, 0x01000100, 0x01080000, 0x01080100,
                 0x00000010, 0x00000110, 0x00080010, 0x00080110,
                 0x01000010, 0x01000110, 0x01080010, 0x01080110,
                 0x00200000, 0x00200100, 0x00280000, 0x00280100,
                 0x01200000, 0x01200100, 0x01280000, 0x01280100,
                 0x00200010, 0x00200110, 0x00280010, 0x00280110,
                 0x01200010, 0x01200110, 0x01280010, 0x01280110,
                 0x00000200, 0x00000300, 0x00080200, 0x00080300,
                 0x01000200, 0x01000300, 0x01080200, 0x01080300,
                 0x00000210, 0x00000310, 0x00080210, 0x00080310,
                 0x01000210, 0x01000310, 0x01080210, 0x01080310,
                 0x00200200, 0x00200300, 0x00280200, 0x00280300,
                 0x01200200, 0x01200300, 0x01280200, 0x01280300,
                 0x00200210, 0x00200310, 0x00280210, 0x00280310,
                 0x01200210, 0x01200310, 0x01280210, 0x01280310,
             },{
                 /* for D bits (numbered as per FIPS 46) 22 23 24 25 27 28 */
                 0x00000000, 0x04000000, 0x00040000, 0x04040000,
                 0x00000002, 0x04000002, 0x00040002, 0x04040002,
                 0x00002000, 0x04002000, 0x00042000, 0x04042000,
                 0x00002002, 0x04002002, 0x00042002, 0x04042002,
                 0x00000020, 0x04000020, 0x00040020, 0x04040020,
                 0x00000022, 0x04000022, 0x00040022, 0x04040022,
                 0x00002020, 0x04002020, 0x00042020, 0x04042020,
                 0x00002022, 0x04002022, 0x00042022, 0x04042022,
                 0x00000800, 0x04000800, 0x00040800, 0x04040800,
                 0x00000802, 0x04000802, 0x00040802, 0x04040802,
                 0x00002800, 0x04002800, 0x00042800, 0x04042800,
                 0x00002802, 0x04002802, 0x00042802, 0x04042802,
                 0x00000820, 0x04000820, 0x00040820, 0x04040820,
                 0x00000822, 0x04000822, 0x00040822, 0x04040822,
                 0x00002820, 0x04002820, 0x00042820, 0x04042820,
                 0x00002822, 0x04002822, 0x00042822, 0x04042822,
             }
         };
 
         private static final int SPtrans[][] = {
             {
                 /* nibble 0 */
                 0x00820200, 0x00020000, 0x80800000, 0x80820200,
                 0x00800000, 0x80020200, 0x80020000, 0x80800000,
                 0x80020200, 0x00820200, 0x00820000, 0x80000200,
                 0x80800200, 0x00800000, 0x00000000, 0x80020000,
                 0x00020000, 0x80000000, 0x00800200, 0x00020200,
                 0x80820200, 0x00820000, 0x80000200, 0x00800200,
                 0x80000000, 0x00000200, 0x00020200, 0x80820000,
                 0x00000200, 0x80800200, 0x80820000, 0x00000000,
                 0x00000000, 0x80820200, 0x00800200, 0x80020000,
                 0x00820200, 0x00020000, 0x80000200, 0x00800200,
                 0x80820000, 0x00000200, 0x00020200, 0x80800000,
                 0x80020200, 0x80000000, 0x80800000, 0x00820000,
                 0x80820200, 0x00020200, 0x00820000, 0x80800200,
                 0x00800000, 0x80000200, 0x80020000, 0x00000000,
                 0x00020000, 0x00800000, 0x80800200, 0x00820200,
                 0x80000000, 0x80820000, 0x00000200, 0x80020200,
             },{
                 /* nibble 1 */
                 0x10042004, 0x00000000, 0x00042000, 0x10040000,
                 0x10000004, 0x00002004, 0x10002000, 0x00042000,
                 0x00002000, 0x10040004, 0x00000004, 0x10002000,
                 0x00040004, 0x10042000, 0x10040000, 0x00000004,
                 0x00040000, 0x10002004, 0x10040004, 0x00002000,
                 0x00042004, 0x10000000, 0x00000000, 0x00040004,
                 0x10002004, 0x00042004, 0x10042000, 0x10000004,
                 0x10000000, 0x00040000, 0x00002004, 0x10042004,
                 0x00040004, 0x10042000, 0x10002000, 0x00042004,
                 0x10042004, 0x00040004, 0x10000004, 0x00000000,
                 0x10000000, 0x00002004, 0x00040000, 0x10040004,
                 0x00002000, 0x10000000, 0x00042004, 0x10002004,
                 0x10042000, 0x00002000, 0x00000000, 0x10000004,
                 0x00000004, 0x10042004, 0x00042000, 0x10040000,
                 0x10040004, 0x00040000, 0x00002004, 0x10002000,
                 0x10002004, 0x00000004, 0x10040000, 0x00042000,
             },{
                 /* nibble 2 */
                 0x41000000, 0x01010040, 0x00000040, 0x41000040,
                 0x40010000, 0x01000000, 0x41000040, 0x00010040,
                 0x01000040, 0x00010000, 0x01010000, 0x40000000,
                 0x41010040, 0x40000040, 0x40000000, 0x41010000,
                 0x00000000, 0x40010000, 0x01010040, 0x00000040,
                 0x40000040, 0x41010040, 0x00010000, 0x41000000,
                 0x41010000, 0x01000040, 0x40010040, 0x01010000,
                 0x00010040, 0x00000000, 0x01000000, 0x40010040,
                 0x01010040, 0x00000040, 0x40000000, 0x00010000,
                 0x40000040, 0x40010000, 0x01010000, 0x41000040,
                 0x00000000, 0x01010040, 0x00010040, 0x41010000,
                 0x40010000, 0x01000000, 0x41010040, 0x40000000,
                 0x40010040, 0x41000000, 0x01000000, 0x41010040,
                 0x00010000, 0x01000040, 0x41000040, 0x00010040,
                 0x01000040, 0x00000000, 0x41010000, 0x40000040,
                 0x41000000, 0x40010040, 0x00000040, 0x01010000,
             },{
                 /* nibble 3 */
                 0x00100402, 0x04000400, 0x00000002, 0x04100402,
                 0x00000000, 0x04100000, 0x04000402, 0x00100002,
                 0x04100400, 0x04000002, 0x04000000, 0x00000402,
                 0x04000002, 0x00100402, 0x00100000, 0x04000000,
                 0x04100002, 0x00100400, 0x00000400, 0x00000002,
                 0x00100400, 0x04000402, 0x04100000, 0x00000400,
                 0x00000402, 0x00000000, 0x00100002, 0x04100400,
                 0x04000400, 0x04100002, 0x04100402, 0x00100000,
                 0x04100002, 0x00000402, 0x00100000, 0x04000002,
                 0x00100400, 0x04000400, 0x00000002, 0x04100000,
                 0x04000402, 0x00000000, 0x00000400, 0x00100002,
                 0x00000000, 0x04100002, 0x04100400, 0x00000400,
                 0x04000000, 0x04100402, 0x00100402, 0x00100000,
                 0x04100402, 0x00000002, 0x04000400, 0x00100402,
                 0x00100002, 0x00100400, 0x04100000, 0x04000402,
                 0x00000402, 0x04000000, 0x04000002, 0x04100400,
             },{
                 /* nibble 4 */
                 0x02000000, 0x00004000, 0x00000100, 0x02004108,
                 0x02004008, 0x02000100, 0x00004108, 0x02004000,
                 0x00004000, 0x00000008, 0x02000008, 0x00004100,
                 0x02000108, 0x02004008, 0x02004100, 0x00000000,
                 0x00004100, 0x02000000, 0x00004008, 0x00000108,
                 0x02000100, 0x00004108, 0x00000000, 0x02000008,
                 0x00000008, 0x02000108, 0x02004108, 0x00004008,
                 0x02004000, 0x00000100, 0x00000108, 0x02004100,
                 0x02004100, 0x02000108, 0x00004008, 0x02004000,
                 0x00004000, 0x00000008, 0x02000008, 0x02000100,
                 0x02000000, 0x00004100, 0x02004108, 0x00000000,
                 0x00004108, 0x02000000, 0x00000100, 0x00004008,
                 0x02000108, 0x00000100, 0x00000000, 0x02004108,
                 0x02004008, 0x02004100, 0x00000108, 0x00004000,
                 0x00004100, 0x02004008, 0x02000100, 0x00000108,
                 0x00000008, 0x00004108, 0x02004000, 0x02000008,
             },{
                 /* nibble 5 */
                 0x20000010, 0x00080010, 0x00000000, 0x20080800,
                 0x00080010, 0x00000800, 0x20000810, 0x00080000,
                 0x00000810, 0x20080810, 0x00080800, 0x20000000,
                 0x20000800, 0x20000010, 0x20080000, 0x00080810,
                 0x00080000, 0x20000810, 0x20080010, 0x00000000,
                 0x00000800, 0x00000010, 0x20080800, 0x20080010,
                 0x20080810, 0x20080000, 0x20000000, 0x00000810,
                 0x00000010, 0x00080800, 0x00080810, 0x20000800,
                 0x00000810, 0x20000000, 0x20000800, 0x00080810,
                 0x20080800, 0x00080010, 0x00000000, 0x20000800,
                 0x20000000, 0x00000800, 0x20080010, 0x00080000,
                 0x00080010, 0x20080810, 0x00080800, 0x00000010,
                 0x20080810, 0x00080800, 0x00080000, 0x20000810,
                 0x20000010, 0x20080000, 0x00080810, 0x00000000,
                 0x00000800, 0x20000010, 0x20000810, 0x20080800,
                 0x20080000, 0x00000810, 0x00000010, 0x20080010,
             },{
                 /* nibble 6 */
                 0x00001000, 0x00000080, 0x00400080, 0x00400001,
                 0x00401081, 0x00001001, 0x00001080, 0x00000000,
                 0x00400000, 0x00400081, 0x00000081, 0x00401000,
                 0x00000001, 0x00401080, 0x00401000, 0x00000081,
                 0x00400081, 0x00001000, 0x00001001, 0x00401081,
                 0x00000000, 0x00400080, 0x00400001, 0x00001080,
                 0x00401001, 0x00001081, 0x00401080, 0x00000001,
                 0x00001081, 0x00401001, 0x00000080, 0x00400000,
                 0x00001081, 0x00401000, 0x00401001, 0x00000081,
                 0x00001000, 0x00000080, 0x00400000, 0x00401001,
                 0x00400081, 0x00001081, 0x00001080, 0x00000000,
                 0x00000080, 0x00400001, 0x00000001, 0x00400080,
                 0x00000000, 0x00400081, 0x00400080, 0x00001080,
                 0x00000081, 0x00001000, 0x00401081, 0x00400000,
                 0x00401080, 0x00000001, 0x00001001, 0x00401081,
                 0x00400001, 0x00401080, 0x00401000, 0x00001001,
             },{
                 /* nibble 7 */
                 0x08200020, 0x08208000, 0x00008020, 0x00000000,
                 0x08008000, 0x00200020, 0x08200000, 0x08208020,
                 0x00000020, 0x08000000, 0x00208000, 0x00008020,
                 0x00208020, 0x08008020, 0x08000020, 0x08200000,
                 0x00008000, 0x00208020, 0x00200020, 0x08008000,
                 0x08208020, 0x08000020, 0x00000000, 0x00208000,
                 0x08000000, 0x00200000, 0x08008020, 0x08200020,
                 0x00200000, 0x00008000, 0x08208000, 0x00000020,
                 0x00200000, 0x00008000, 0x08000020, 0x08208020,
                 0x00008020, 0x08000000, 0x00000000, 0x00208000,
                 0x08200020, 0x08008020, 0x08008000, 0x00200020,
                 0x08208000, 0x00000020, 0x00200020, 0x08008000,
                 0x08208020, 0x00200000, 0x08200000, 0x08000020,
                 0x00208000, 0x00008020, 0x08008020, 0x08200000,
                 0x00000020, 0x08208000, 0x00208020, 0x00000000,
                 0x08000000, 0x08200020, 0x00008000, 0x00208020
             }
         };
 
         private static final int cov_2char[] = {
             0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35,
             0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43, 0x44,
             0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C,
             0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0x53, 0x54,
             0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x61, 0x62,
             0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A,
             0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70, 0x71, 0x72,
             0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
         };
 
         private static final int byteToUnsigned(byte b) {
             return b & 0xFF;
         }
 
         private static int fourBytesToInt(byte b[], int offset) {
             int value;
             value  =  byteToUnsigned(b[offset++]);
             value |= (byteToUnsigned(b[offset++]) <<  8);
             value |= (byteToUnsigned(b[offset++]) << 16);
             value |= (byteToUnsigned(b[offset++]) << 24);
             return(value);
         }
 
         private static final void intToFourBytes(int iValue, byte b[], int offset) {
             b[offset++] = (byte)((iValue)        & 0xff);
             b[offset++] = (byte)((iValue >>> 8 ) & 0xff);
             b[offset++] = (byte)((iValue >>> 16) & 0xff);
             b[offset++] = (byte)((iValue >>> 24) & 0xff);
         }
 
         private static final void PERM_OP(int a, int b, int n, int m, int results[]) {
             int t;
 
             t = ((a >>> n) ^ b) & m;
             a ^= t << n;
             b ^= t;
 
             results[0] = a;
             results[1] = b;
         }
 
         private static final int HPERM_OP(int a, int n, int m) {
             int t;
 
             t = ((a << (16 - n)) ^ a) & m;
             a = a ^ t ^ (t >>> (16 - n));
 
             return a;
         }
 
         private static int [] des_set_key(byte key[]) {
             int schedule[] = new int[ITERATIONS * 2];
 
             int c = fourBytesToInt(key, 0);
             int d = fourBytesToInt(key, 4);
 
             int results[] = new int[2];
 
             PERM_OP(d, c, 4, 0x0f0f0f0f, results);
             d = results[0]; c = results[1];
 
             c = HPERM_OP(c, -2, 0xcccc0000);
             d = HPERM_OP(d, -2, 0xcccc0000);
 
             PERM_OP(d, c, 1, 0x55555555, results);
             d = results[0]; c = results[1];
 
             PERM_OP(c, d, 8, 0x00ff00ff, results);
             c = results[0]; d = results[1];
 
             PERM_OP(d, c, 1, 0x55555555, results);
             d = results[0]; c = results[1];
 
             d = (((d & 0x000000ff) <<  16) |  (d & 0x0000ff00)     |
                  ((d & 0x00ff0000) >>> 16) | ((c & 0xf0000000) >>> 4));
             c &= 0x0fffffff;
 
             int s, t;
             int j = 0;
 
             for(int i = 0; i < ITERATIONS; i ++) {
                 if(shifts2[i]) {
                     c = (c >>> 2) | (c << 26);
                     d = (d >>> 2) | (d << 26);
                 } else {
                     c = (c >>> 1) | (c << 27);
                     d = (d >>> 1) | (d << 27);
                 }
 
                 c &= 0x0fffffff;
                 d &= 0x0fffffff;
 
                 s = skb[0][ (c       ) & 0x3f                       ]|
                     skb[1][((c >>>  6) & 0x03) | ((c >>>  7) & 0x3c)]|
                     skb[2][((c >>> 13) & 0x0f) | ((c >>> 14) & 0x30)]|
                     skb[3][((c >>> 20) & 0x01) | ((c >>> 21) & 0x06) |
                            ((c >>> 22) & 0x38)];
 
                 t = skb[4][ (d     )  & 0x3f                       ]|
                     skb[5][((d >>> 7) & 0x03) | ((d >>>  8) & 0x3c)]|
                     skb[6][ (d >>>15) & 0x3f                       ]|
                     skb[7][((d >>>21) & 0x0f) | ((d >>> 22) & 0x30)];
 
                 schedule[j++] = ((t <<  16) | (s & 0x0000ffff)) & 0xffffffff;
                 s             = ((s >>> 16) | (t & 0xffff0000));
 
                 s             = (s << 4) | (s >>> 28);
                 schedule[j++] = s & 0xffffffff;
             }
             return(schedule);
         }
 
         private static final int D_ENCRYPT(int L, int R, int S, int E0, int E1, int s[]) {
             int t, u, v;
 
             v = R ^ (R >>> 16);
             u = v & E0;
             v = v & E1;
             u = (u ^ (u << 16)) ^ R ^ s[S];
             t = (v ^ (v << 16)) ^ R ^ s[S + 1];
             t = (t >>> 4) | (t << 28);
 
             L ^= SPtrans[1][(t       ) & 0x3f] |
                 SPtrans[3][(t >>>  8) & 0x3f] |
                 SPtrans[5][(t >>> 16) & 0x3f] |
                 SPtrans[7][(t >>> 24) & 0x3f] |
                 SPtrans[0][(u       ) & 0x3f] |
                 SPtrans[2][(u >>>  8) & 0x3f] |
                 SPtrans[4][(u >>> 16) & 0x3f] |
                 SPtrans[6][(u >>> 24) & 0x3f];
 
             return(L);
         }
 
         private static final int [] body(int schedule[], int Eswap0, int Eswap1) {
             int left = 0;
             int right = 0;
             int t     = 0;
 
             for(int j = 0; j < 25; j ++) {
                 for(int i = 0; i < ITERATIONS * 2; i += 4) {
                     left  = D_ENCRYPT(left,  right, i,     Eswap0, Eswap1, schedule);
                     right = D_ENCRYPT(right, left,  i + 2, Eswap0, Eswap1, schedule);
                 }
                 t     = left;
                 left  = right;
                 right = t;
             }
 
             t = right;
 
             right = (left >>> 1) | (left << 31);
             left  = (t    >>> 1) | (t    << 31);
 
             left  &= 0xffffffff;
             right &= 0xffffffff;
 
             int results[] = new int[2];
 
             PERM_OP(right, left, 1, 0x55555555, results);
             right = results[0]; left = results[1];
 
             PERM_OP(left, right, 8, 0x00ff00ff, results);
             left = results[0]; right = results[1];
 
             PERM_OP(right, left, 2, 0x33333333, results);
             right = results[0]; left = results[1];
 
             PERM_OP(left, right, 16, 0x0000ffff, results);
             left = results[0]; right = results[1];
 
             PERM_OP(right, left, 4, 0x0f0f0f0f, results);
             right = results[0]; left = results[1];
 
             int out[] = new int[2];
 
             out[0] = left; out[1] = right;
 
             return(out);
         }
 
         public static final String crypt(String salt, String original) {
             while(salt.length() < 2)
                 salt += getSaltChar();
 
             StringBuffer buffer = new StringBuffer("             ");
 
             char charZero = salt.charAt(0);
             char charOne  = salt.charAt(1);
 
             buffer.setCharAt(0, charZero);
             buffer.setCharAt(1, charOne);
 
             int Eswap0 = con_salt[(int)charZero];
             int Eswap1 = con_salt[(int)charOne] << 4;
 
             byte key[] = new byte[8];
 
             for(int i = 0; i < key.length; i ++) {
                 key[i] = (byte)0;
             }
 
             for(int i = 0; i < key.length && i < original.length(); i ++) {
                 int iChar = (int)original.charAt(i);
 
                 key[i] = (byte)(iChar << 1);
             }
 
             int schedule[] = des_set_key(key);
             int out[]      = body(schedule, Eswap0, Eswap1);
 
             byte b[] = new byte[9];
 
             intToFourBytes(out[0], b, 0);
             intToFourBytes(out[1], b, 4);
             b[8] = 0;
 
             for(int i = 2, y = 0, u = 0x80; i < 13; i ++) {
                 for(int j = 0, c = 0; j < 6; j ++) {
                     c <<= 1;
 
                     if(((int)b[y] & u) != 0)
                         c |= 1;
 
                     u >>>= 1;
 
                     if(u == 0) {
                         y++;
                         u = 0x80;
                     }
                     buffer.setCharAt(i, (char)cov_2char[c]);
                 }
             }
             return(buffer.toString());
         }
 
         private static String getSaltChar() {
             return JavaCrypt.getSaltChar(1);
         }
 
         private static String getSaltChar(int amount) {
             StringBuffer sb = new StringBuffer();
             for(int i=amount;i>0;i--) {
                 sb.append(theBaseSalts[(Math.abs(r_gen.nextInt())%64)]);
             }
             return sb.toString();
         }
 
         public static boolean check(String theClear,String theCrypt) {
             String theTest = JavaCrypt.crypt(theCrypt.substring(0,2),theClear);
             return theTest.equals(theCrypt);
         }
 
         public static String crypt(String theClear) {
             return JavaCrypt.crypt(getSaltChar(2),theClear);
         }
     }
 
     /* RubyString aka rb_string_value */
     public static RubyString stringValue(IRubyObject object) {
         return (RubyString) (object instanceof RubyString ? object :
             object.convertToString());
     }
 
     /** rb_str_sub
      *
      */
     @JRubyMethod(name = "sub", required = 1, optional = 1)
     public IRubyObject sub(IRubyObject[] args, Block block) {
         RubyString str = strDup();
         str.sub_bang(args, block);
         return str;
     }
 
     /** rb_str_sub_bang
      *
      */
     @JRubyMethod(name = "sub!", required = 1, optional = 1)
     public IRubyObject sub_bang(IRubyObject[] args, Block block) {
         boolean iter = false;
         IRubyObject repl;
         Ruby runtime = getRuntime();
         boolean tainted = false;
         
         if (args.length == 1 && block.isGiven()) {
             iter = true;
             repl = runtime.getNil();
         } else if (args.length == 2) {
             repl = args[1].convertToString();
             tainted = repl.isTaint(); 
         } else {
             throw runtime.newArgumentError("wrong number of arguments (" + args.length + "for 2)");
         }
         
         RubyRegexp pattern = getPat(args[0], true);
         boolean utf8 = pattern.getCode() == KCode.UTF8; 
         
         RegexpPattern pat = pattern.getPattern();
         String str = toString(utf8); 
         RegexpMatcher mat = pat.matcher(str);
         
         if (mat.find()) {
             ThreadContext context = runtime.getCurrentContext();
             RubyMatchData md = matchdata(runtime, str, mat, utf8);
             context.getCurrentFrame().setBackRef(md);
             if (iter) {
                 // FIXME: I don't like this setting into the frame directly, but it's necessary since blocks dupe the frame
                 block.getFrame().setBackRef(md);
                 int len = value.realSize;
                 byte[]b = value.bytes;
                 repl = objAsString(block.yield(context, substr(runtime, str, mat.start(0), mat.length(0), utf8)));
                 modifyCheck(b, len);
                 frozenCheck();
             } else {
                 repl = pattern.regsub(repl, this, md);
             }
 
             if (repl.isTaint()) tainted = true;
             int startZ = mat.start(0);
             if(utf8) {
                 try {
                     startZ = str.substring(0, startZ).getBytes("UTF8").length;
                 } catch (UnsupportedEncodingException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
             }
 
             int plen = mat.end(0) - startZ;
 
             if (utf8) {
                 try {
                     plen = mat.group(0).getBytes("UTF8").length;
                 } catch (UnsupportedEncodingException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
             }
             ByteList replValue = ((RubyString)repl).value;
             
             if (replValue.realSize > plen) { // this might be smarter by being real bytes length aware
                 modify(value.realSize + replValue.realSize - plen);
             } else {
                 modify();
             }
             if (replValue.realSize != plen && (value.realSize - startZ - plen) > 0) {
                 int valueOldStart = value.begin + startZ + plen;
                 int valueNewStart = value.begin + startZ + replValue.realSize;
                 int valueLength = value.realSize - startZ - plen;
                 
                 System.arraycopy(value.bytes, valueOldStart, value.bytes, valueNewStart, valueLength);
             }
             System.arraycopy(replValue.bytes, replValue.begin, value.bytes, value.begin + startZ, replValue.realSize);
             value.realSize += replValue.realSize - plen;
             if (tainted) setTaint(true);
             return this;
             
         }
 
         return runtime.getNil();
     }
 
     /** rb_str_gsub
      *
      */
     @JRubyMethod(name = "gsub", required = 1, optional = 1, frame = true)
     public IRubyObject gsub(IRubyObject[] args, Block block) {
         return gsub(args, block, false);
     }
 
     /** rb_str_gsub_bang
      *
      */
     @JRubyMethod(name = "gsub!", required = 1, optional = 1, frame = true)
     public IRubyObject gsub_bang(IRubyObject[] args, Block block) {
         return gsub(args, block, true);
     }
 
     private final IRubyObject gsub(IRubyObject[] args, Block block, boolean bang) {
         boolean iter = false;
         IRubyObject repl;
         Ruby runtime = getRuntime();
         boolean tainted = false;
         
         if (args.length == 1 && block.isGiven()) {
             iter = true;
             repl = runtime.getNil();
         } else if (args.length == 2) {
             repl = args[1].convertToString();
             tainted = repl.isTaint(); 
         } else {
             throw runtime.newArgumentError("wrong number of arguments (" + args.length + "for 2)");
         }
         
         RubyRegexp pattern = getPat(args[0], true);
         boolean utf8 = pattern.getCode() == KCode.UTF8; 
         
         RegexpPattern pat = pattern.getPattern();
         String str = toString(utf8); 
         RegexpMatcher mat = pat.matcher(str);
 
         boolean found = mat.find();
         
         if (!found) {
             if (bang) return runtime.getNil();
             return strDup();
         }
         
         int blen = value.realSize + 30;
         ByteList dest = new ByteList(blen);
         dest.realSize = blen;
         int buf = 0, bp = 0;
         int cp = value.begin;
 
         // for modifyCheck
         byte [] sb = value.bytes; 
         int slen = value.realSize;
         
         RubyString val; 
         ThreadContext context = runtime.getCurrentContext();
         
         int offset = 0;
         // tmp lock
         RubyMatchData md = null;
         while (found) {
             
             if (iter) {
                 md = matchdata(runtime, str, mat, utf8);
                 context.getPreviousFrame().setBackRef(md);
                 // FIXME: I don't like this setting into the frame directly, but it's necessary since blocks dupe the
                 block.getFrame().setBackRef(md);
                 val = objAsString(block.yield(context, substr(runtime, str, mat.start(0), mat.length(0), utf8)));
                 modifyCheck(sb, slen);
                 if (bang) frozenCheck();
                 // rb_raise(rb_eRuntimeError, "block should not cheat");
             } else {
                 if (md == null) {
                     md = matchdata(runtime, str, mat, utf8);
                     context.getPreviousFrame().setBackRef(md);
                 } else {
                     md.invalidateRegs();
                 }
                 val = pattern.regsub(repl, this, md);
             }
             if (val.isTaint()) tainted = true;
             ByteList vbuf = val.value;
             int beg = mat.start(); 
             int len = (bp - buf) + (beg - offset) + vbuf.realSize + 3;
 
             if (blen < len) {
                 while (blen < len) blen <<= 1;
                 len = bp - buf;
                 dest.realloc(blen);                
                 dest.realSize = blen;
                 bp = buf + len;
             }
             len = beg - offset;
             System.arraycopy(value.bytes, cp, dest.bytes, bp, len);
             bp += len;
             System.arraycopy(vbuf.bytes, vbuf.begin, dest.bytes, bp, vbuf.realSize);
             bp += vbuf.realSize;
             int endZ = mat.end(0);
             offset = endZ;
             
             if (mat.length(0) == 0) {
                 if (value.realSize <= endZ) break;
                 len = 1;
                 System.arraycopy(value.bytes, value.begin + endZ, dest.bytes, bp, len);
                 bp += len;
                 offset = endZ + len;
             }
             cp = value.begin + offset;
             if (offset > value.realSize) break;
             mat.setPosition(offset);
             found = mat.find();
         }
         
         if (value.realSize > offset) {
             int len = bp - buf;
             if (blen - len < value.realSize - offset) {
                 blen = len + value.realSize - offset;
                 dest.realloc(blen);
                 bp = buf + len;
             }
             System.arraycopy(value.bytes, cp, dest.bytes, bp, value.realSize - offset);
             bp += value.realSize - offset;
         }
         // match unlock
         // tmp unlock;
         
         dest.realSize = bp - buf;
         
         if (bang) {
             view(dest);
             if (tainted) setTaint(true);
             return this;
         } else {
             RubyString destStr = new RubyString(runtime, getMetaClass(), dest);
             destStr.infectBy(this);
             if (tainted) destStr.setTaint(true);
             return destStr;
         }
     }
 
     /** rb_str_index_m
      *
      */
     @JRubyMethod(name = "index", required = 1, optional = 1)
     public IRubyObject index(IRubyObject[] args) {
         return index(args, false);
     }
 
     /** rb_str_rindex_m
      *
      */
     @JRubyMethod(name = "rindex", required = 1, optional = 1)
     public IRubyObject rindex(IRubyObject[] args) {
         return index(args, true);
     }
 
     /**
      *	@fixme may be a problem with pos when doing reverse searches
      */
     private IRubyObject index(IRubyObject[] args, boolean reverse) {
         //FIXME may be a problem with pos when doing reverse searches
         int pos;
         boolean offset = false;
         
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
             pos = RubyNumeric.fix2int(args[1]);
             if (pos > value.length()) {
                 if (reverse) {
                     pos = value.length();
                 } else {
                     return getRuntime().getNil();
                 }
             } else {
                 if (pos < 0) {
                     pos += value.length();
                     if (pos < 0) return getRuntime().getNil();
                 }                 
             }
             offset = true;           
         } else {
             pos = !reverse ? 0 : value.length();
         }
         
         if (args[0] instanceof RubyRegexp) {
             // save position we shouldn't look past
             int doNotLookPastIfReverse = pos;
 
             // RubyRegexp doesn't (yet?) support reverse searches, so we
             // find all matches and use the last one--very inefficient.
             // FIXME: - find a better way
             pos = ((RubyRegexp) args[0]).search(toString(), this, reverse ? 0 : pos);
             
             if (reverse) {
                 if (pos == -1) return getRuntime().getNil();
                 
                 int dummy = pos;
                 if (offset) {
                     pos = doNotLookPastIfReverse;
                     if (dummy > pos) {
                         pos = -1;
                         dummy = -1;
                     }
                 }
                 while (reverse && dummy > -1 && dummy <= doNotLookPastIfReverse) {
                     pos = dummy;
                     dummy = ((RubyRegexp) args[0]).search(toString(), this, pos + 1);
                 }
             }
         } else if (args[0] instanceof RubyFixnum) {
             char c = (char) ((RubyFixnum) args[0]).getLongValue();
             pos = reverse ? value.lastIndexOf(c, pos) : value.indexOf(c, pos);
         } else {
             IRubyObject tmp = args[0].checkStringType();
 
             if (tmp.isNil()) throw getRuntime().newTypeError("type mismatch: " + args[0].getMetaClass().getName() + " given");
 
             ByteList sub = ((RubyString) tmp).value;
 
             if (sub.length() > value.length()) return getRuntime().getNil();
             // the empty string is always found at the beginning of a string (or at the end when rindex)
             if (sub.realSize == 0) return getRuntime().newFixnum(pos);
 
             pos = reverse ? value.lastIndexOf(sub, pos) : value.indexOf(sub, pos);
         }
 
         return pos == -1 ? getRuntime().getNil() : getRuntime().newFixnum(pos);
     }
 
     /* rb_str_substr */
     public IRubyObject substr(int beg, int len) {
         int length = value.length();
         if (len < 0 || beg > length) return getRuntime().getNil();
 
         if (beg < 0) {
             beg += length;
             if (beg < 0) return getRuntime().getNil();
         }
         
         int end = Math.min(length, beg + len);
         return makeShared(beg, end - beg);
     }
 
     /* rb_str_replace */
     public IRubyObject replace(int beg, int len, RubyString replaceWith) {
         if (beg + len >= value.length()) len = value.length() - beg;
 
         modify();
         value.unsafeReplace(beg,len,replaceWith.value);
 
         return infectBy(replaceWith);
     }
 
     /** rb_str_aref, rb_str_aref_m
      *
      */
-    @JRubyMethod(name = "[]", alias = "slice", required = 1, optional = 1)
+    @JRubyMethod(name = {"[]", "slice"}, required = 1, optional = 1)
     public IRubyObject op_aref(IRubyObject[] args) {
         if (Arity.checkArgumentCount(getRuntime(), args, 1, 2) == 2) {
             if (args[0] instanceof RubyRegexp) {
                 IRubyObject match = RubyRegexp.regexpValue(args[0]).match(toString(), this, 0);
                 long idx = args[1].convertToInteger().getLongValue();
                 getRuntime().getCurrentContext().getCurrentFrame().setBackRef(match);
                 return RubyRegexp.nth_match((int) idx, match);
             }
             return substr(RubyNumeric.fix2int(args[0]), RubyNumeric.fix2int(args[1]));
         }
 
         if (args[0] instanceof RubyRegexp) {
             return RubyRegexp.regexpValue(args[0]).search(toString(), this, 0) >= 0 ?
                 RubyRegexp.last_match(getRuntime().getCurrentContext().getCurrentFrame().getBackRef()) :
                 getRuntime().getNil();
         } else if (args[0] instanceof RubyString) {
             return toString().indexOf(stringValue(args[0]).toString()) != -1 ?
                 args[0] : getRuntime().getNil();
         } else if (args[0] instanceof RubyRange) {
             long[] begLen = ((RubyRange) args[0]).begLen(value.length(), 0);
             return begLen == null ? getRuntime().getNil() :
                 substr((int) begLen[0], (int) begLen[1]);
         }
         int idx = (int) args[0].convertToInteger().getLongValue();
         
         if (idx < 0) idx += value.length();
         if (idx < 0 || idx >= value.length()) return getRuntime().getNil();
 
         return getRuntime().newFixnum(value.get(idx) & 0xFF);
     }
 
     /**
      * rb_str_subpat_set
      *
      */
     private void subpatSet(RubyRegexp regexp, int nth, IRubyObject repl) {
         int found = regexp.search(this.toString(), this, 0);
         if (found == -1) throw getRuntime().newIndexError("regexp not matched");
 
         RubyMatchData match = (RubyMatchData) getRuntime().getCurrentContext().getCurrentFrame().getBackRef();
         match.use();
 
         if (nth >= match.getSize()) {
             throw getRuntime().newIndexError("index " + nth + " out of regexp");
         }
         if (nth < 0) {
             if (-nth >= match.getSize()) {
                 throw getRuntime().newIndexError("index " + nth + " out of regexp");
             }
             nth += match.getSize();
         }
 
         IRubyObject group = match.group(nth);
         if (getRuntime().getNil().equals(group)) {
             throw getRuntime().newIndexError("regexp group " + nth + " not matched");
         }
 
         int beg = (int) match.begin(nth);
         int len = (int) (match.end(nth) - beg);
 
         replace(beg, len, stringValue(repl));
     }
 
     /** rb_str_aset, rb_str_aset_m
      *
      */
     @JRubyMethod(name = "[]=", required = 2, optional = 1)
     public IRubyObject op_aset(IRubyObject[] args) {
         int strLen = value.length();
         if (Arity.checkArgumentCount(getRuntime(), args, 2, 3) == 3) {
             if (args[0] instanceof RubyFixnum) {
                 RubyString repl = stringValue(args[2]);
                 int beg = RubyNumeric.fix2int(args[0]);
                 int len = RubyNumeric.fix2int(args[1]);
                 if (len < 0) throw getRuntime().newIndexError("negative length");
                 if (beg < 0) beg += strLen;
 
                 if (beg < 0 || (beg > 0 && beg >= strLen)) {
                     throw getRuntime().newIndexError("string index out of bounds");
                 }
                 if (beg + len > strLen) len = strLen - beg;
 
                 replace(beg, len, repl);
                 return repl;
             }
             if (args[0] instanceof RubyRegexp) {
                 RubyString repl = stringValue(args[2]);
                 int nth = RubyNumeric.fix2int(args[1]);
                 subpatSet((RubyRegexp) args[0], nth, repl);
                 return repl;
             }
         }
         if (args[0] instanceof RubyFixnum || args[0].respondsTo("to_int")) { // FIXME: RubyNumeric or RubyInteger instead?
             int idx = 0;
 
             // FIXME: second instanceof check adds overhead?
             if (!(args[0] instanceof RubyFixnum)) {
                 // FIXME: ok to cast?
                 idx = (int)args[0].convertToInteger().getLongValue();
             } else {
                 idx = RubyNumeric.fix2int(args[0]); // num2int?
             }
             
             if (idx < 0) idx += value.length();
 
             if (idx < 0 || idx >= value.length()) {
                 throw getRuntime().newIndexError("string index out of bounds");
             }
             if (args[1] instanceof RubyFixnum) {
                 modify();
                 value.set(idx, (byte) RubyNumeric.fix2int(args[1]));
             } else {
                 replace(idx, 1, stringValue(args[1]));
             }
             return args[1];
         }
         if (args[0] instanceof RubyRegexp) {
             sub_bang(args, null);
             return args[1];
         }
         if (args[0] instanceof RubyString) {
             RubyString orig = stringValue(args[0]);
             int beg = toString().indexOf(orig.toString());
             if (beg != -1) {
                 replace(beg, orig.value.length(), stringValue(args[1]));
             }
             return args[1];
         }
         if (args[0] instanceof RubyRange) {
             long[] idxs = ((RubyRange) args[0]).getBeginLength(value.length(), true, true);
             replace((int) idxs[0], (int) idxs[1], stringValue(args[1]));
             return args[1];
         }
         throw getRuntime().newTypeError("wrong argument type");
     }
 
     /** rb_str_slice_bang
      *
      */
     @JRubyMethod(name = "slice!", required = 1, optional = 1)
     public IRubyObject slice_bang(IRubyObject[] args) {
         int argc = Arity.checkArgumentCount(getRuntime(), args, 1, 2);
         IRubyObject[] newArgs = new IRubyObject[argc + 1];
         newArgs[0] = args[0];
         if (argc > 1) newArgs[1] = args[1];
 
         newArgs[argc] = newString("");
         IRubyObject result = op_aref(args);
         if (result.isNil()) return result;
 
         op_aset(newArgs);
         return result;
     }
 
-    @JRubyMethod(name = "succ", alias = "next")
+    @JRubyMethod(name = {"succ", "next"})
     public IRubyObject succ() {
         return strDup().succ_bang();
     }
 
-    @JRubyMethod(name = "succ!", alias = "next!")
+    @JRubyMethod(name = {"succ!", "next!"})
     public IRubyObject succ_bang() {
         if (value.length() == 0) return this;
 
         modify();
         
         boolean alnumSeen = false;
         int pos = -1;
         int c = 0;
         int n = 0;
         for (int i = value.length() - 1; i >= 0; i--) {
             c = value.get(i) & 0xFF;
             if (isAlnum(c)) {
                 alnumSeen = true;
                 if ((isDigit(c) && c < '9') || (isLower(c) && c < 'z') || (isUpper(c) && c < 'Z')) {
                     value.set(i, (byte)(c + 1));
                     pos = -1;
                     break;
                 }
                 pos = i;
                 n = isDigit(c) ? '1' : (isLower(c) ? 'a' : 'A');
                 value.set(i, (byte)(isDigit(c) ? '0' : (isLower(c) ? 'a' : 'A')));
             }
         }
         if (!alnumSeen) {
             for (int i = value.length() - 1; i >= 0; i--) {
                 c = value.get(i) & 0xFF;
                 if (c < 0xff) {
                     value.set(i, (byte)(c + 1));
                     pos = -1;
                     break;
                 }
                 pos = i;
                 n = '\u0001';
                 value.set(i, 0);
             }
         }
         if (pos > -1) {
             // This represents left most digit in a set of incremented
             // values?  Therefore leftmost numeric must be '1' and not '0'
             // 999 -> 1000, not 999 -> 0000.  whereas chars should be
             // zzz -> aaaa and non-alnum byte values should be "\377" -> "\001\000"
             value.prepend((byte) n);
         }
         return this;
     }
 
     /** rb_str_upto_m
      *
      */
     @JRubyMethod(name = "upto", required = 1, frame = true)
     public IRubyObject upto(IRubyObject str, Block block) {
         return upto(str, false, block);
     }
 
     /* rb_str_upto */
     public IRubyObject upto(IRubyObject str, boolean excl, Block block) {
         // alias 'this' to 'beg' for ease of comparison with MRI
         RubyString beg = this;
         RubyString end = stringValue(str);
 
         int n = beg.op_cmp(end);
         if (n > 0 || (excl && n == 0)) return beg;
 
         RubyString afterEnd = stringValue(end.succ());
         RubyString current = beg;
 
         ThreadContext context = getRuntime().getCurrentContext();
         while (!current.equals(afterEnd)) {
             block.yield(context, current);
             if (!excl && current.equals(end)) break;
 
             current = (RubyString) current.succ();
             
             if (excl && current.equals(end)) break;
 
             if (current.length().getLongValue() > end.length().getLongValue()) break;
         }
 
         return beg;
 
     }
 
 
     /** rb_str_include
      *
      */
     @JRubyMethod(name = "include?", required = 1)
     public RubyBoolean include_p(IRubyObject obj) {
         if (obj instanceof RubyFixnum) {
             int c = RubyNumeric.fix2int(obj);
             for (int i = 0; i < value.length(); i++) {
                 if (value.get(i) == (byte)c) {
                     return getRuntime().getTrue();
                 }
             }
             return getRuntime().getFalse();
         }
         ByteList str = stringValue(obj).value;
         return getRuntime().newBoolean(value.indexOf(str) != -1);
     }
 
     /** rb_str_to_i
      *
      */
     @JRubyMethod(name = "to_i", optional = 1)
     public IRubyObject to_i(IRubyObject[] args) {
         long base = Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 0 ? 10 : args[0].convertToInteger().getLongValue();
         return RubyNumeric.str2inum(getRuntime(), this, (int) base);
     }
 
     /** rb_str_oct
      *
      */
     @JRubyMethod(name = "oct")
     public IRubyObject oct() {
         if (isEmpty()) {
             return getRuntime().newFixnum(0);
         }
 
         int base = 8;
         String str = toString().trim();
         int pos = (str.charAt(0) == '-' || str.charAt(0) == '+') ? 1 : 0;
         if (str.indexOf("0x") == pos || str.indexOf("0X") == pos) {
             base = 16;
         } else if (str.indexOf("0b") == pos || str.indexOf("0B") == pos) {
             base = 2;
         }
         return RubyNumeric.str2inum(getRuntime(), this, base);
     }
 
     /** rb_str_hex
      *
      */
     @JRubyMethod(name = "hex")
     public IRubyObject hex() {
         return RubyNumeric.str2inum(getRuntime(), this, 16);
     }
 
     /** rb_str_to_f
      *
      */
     @JRubyMethod(name = "to_f")
     public IRubyObject to_f() {
         return RubyNumeric.str2fnum(getRuntime(), this);
     }
 
     /** rb_str_split_m
      *
      */
     @JRubyMethod(name = "split", optional = 2)
     public RubyArray split(IRubyObject[] args) {
         int beg, end, i = 0;
         int lim = 0;
         boolean limit = false;
             
         Ruby runtime = getRuntime();        
         if (Arity.checkArgumentCount(runtime, args, 0, 2) == 2) {
             lim = RubyNumeric.fix2int(args[1]);
             if (lim == 1) { 
                 if (value.realSize == 0) return runtime.newArray();
                 return runtime.newArray(this);
             } else if (lim > 0) {
                 limit = true;
             }
             
             if (lim > 0) limit = true;
             i = 1;
         }
         
         IRubyObject spat = (args.length == 0 || args[0].isNil()) ? null : args[0];
         boolean awkSplit = false;
         
         if (spat == null && (spat = runtime.getGlobalVariables().get("$;")).isNil()) {
             awkSplit = true;
         } else {
             if (spat instanceof RubyString && ((RubyString)spat).value.realSize == 1) {
                 if (((RubyString)spat).value.get(0) == ' ') {
                     awkSplit = true;
                 }
             }
         }
             
         RubyArray result = runtime.newArray();
         
         if (awkSplit) { // awk split
             int ptr = value.begin; 
             int eptr = ptr + value.realSize;
             byte[]buff = value.bytes;            
             boolean skip = true;
             
             for (end = beg = 0; ptr < eptr; ptr++) {
                 if (skip) {
                     if (Character.isWhitespace((char)(buff[ptr] & 0xff))) {
                         beg++;
                     } else {
                         end = beg + 1;
                         skip = false;
                         if (limit && lim <= i) break;
                     }
                 } else {
                     if (Character.isWhitespace((char)(buff[ptr] & 0xff))) {
                         result.append(makeShared(beg, end - beg));
                         skip = true;
                         beg = end + 1;
                         if (limit) i++;
                     } else {
                         end++;
                     }
                 }
             }
             
             if (value.realSize > 0 && (limit || value.realSize > beg || lim < 0)) {
                 if (value.length() == beg) {
                     result.append(newEmptyString(runtime, getMetaClass()));
                 } else {
                     result.append(makeShared(beg, value.realSize - beg));
                 }
             }
         } else if(spat instanceof RubyString) { // string split
             ByteList rsep = ((RubyString)spat).value;
             int rslen = rsep.realSize;
 
             int ptr = value.begin; 
             int pend = ptr + value.realSize;
             byte[]buff = value.bytes;            
             int p = ptr;
             
             byte lastVal = rsep.bytes[rsep.begin+rslen-1];
 
             int s = p;
             p+=rslen;
 
             for(; p < pend; p++) {
                 if(ptr<p && buff[p-1] == lastVal &&
                    (rslen <= 1 || 
                     ByteList.memcmp(rsep.bytes, rsep.begin, rslen, buff, p-rslen, rslen) == 0)) {
 
                     result.append(makeShared(s-ptr, (p - s) - rslen));
                     s = p;
                     if(limit) {
                         i++;
                         if(lim<=i) {
                             p = pend;
                             break;
                         }
                     }
                 }
             }
             
             if(s != pend) {
                 if(p > pend) {
                     p = pend;
                 }
                 if(!(limit && lim<=i) && ByteList.memcmp(rsep.bytes, rsep.begin, rslen, buff, p-rslen, rslen) == 0) {
                     result.append(makeShared(s-ptr, (p - s)-rslen));
                     if(lim < 0) {
                         result.append(newEmptyString(runtime, getMetaClass()));
                     }
                 } else {
                     result.append(makeShared(s-ptr, p - s));
                 }
             }
         } else { // regexp split
             boolean utf8 = false; 
             String str;
             
             RubyRegexp rr =(RubyRegexp)getPat(spat,true);
             if (runtime.getKCode() == KCode.UTF8) {
                 // We're in UTF8 mode; try to convert the string to UTF8, but fall back on raw bytes if we can't decode
                 // TODO: all this decoder and charset stuff could be centralized...in KCode perhaps?
                 CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
                 decoder.onMalformedInput(CodingErrorAction.REPORT);
                 decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
 
                 try {
                     str = decoder.decode(ByteBuffer.wrap(value.unsafeBytes(), value.begin, value.realSize)).toString();
                     utf8 = true;
                 } catch (CharacterCodingException cce) {
                     // ignore, just use the unencoded string
                     str = toString();
                 }
             } else {
                 utf8 = rr.getCode() == KCode.UTF8; 
                 str = toString(utf8);
             }
 
             RegexpPattern pat = rr.getPattern();
             RegexpMatcher mat = pat.matcher(str);
             beg = 0;
             boolean lastNull = false;
             while (mat.find()) {
                 end = mat.start();
                 if (beg == end && mat.length(0) == 0) {
                     if (value.realSize == 0) {
                         result.append(newEmptyString(runtime, getMetaClass()));
                         break;
                     } else if (lastNull) {
                         beg = end;
                     } else {
                         lastNull = true;
                         continue;
                     }
                 } else {
                     result.append(substr(runtime, str, beg, end - beg, utf8));
                     beg = mat.end(0);
                 }
                 
                 lastNull = false;
 
                 for (int index = 1; index < mat.groupCount(); index++) {
                     if (mat.isCaptured(index)) {
                         int iLength = mat.length(index);
                         
                         if (iLength == 0) {
                             result.append(newEmptyString(runtime, getMetaClass()));
                         } else {
                             result.append(substr(runtime, str, mat.start(index), iLength, utf8));
                         }
                     }
                 }
                 
                 if (limit && lim <= ++i) break;
             }
 
             if (str.length() > 0 && (limit || str.length() > beg || lim < 0)) {
                 if (str.length() == beg) {
                     result.append(newEmptyString(runtime, getMetaClass()));
                 } else {
                     result.append(substr(runtime, str, beg, str.length() - beg, utf8));
                 }
             }
             
         } // regexp split
 
         if (!limit && lim == 0) {
             while (result.size() > 0 && ((RubyString)result.eltInternal(result.size() - 1)).value.realSize == 0)
                 result.pop();
         }
 
         return result;
     }
     
     /** get_pat
      * 
      */
     private final RubyRegexp getPat(IRubyObject pattern, boolean quote) {
         if (pattern instanceof RubyRegexp) {
             return (RubyRegexp)pattern;
         } else if (!(pattern instanceof RubyString)) {
             IRubyObject val = pattern.checkStringType();
             if(val.isNil()) throw getRuntime().newTypeError("wrong argument type " + pattern.getMetaClass() + " (expected Regexp)");
             pattern = val; 
         }
         
         RubyString strPattern = (RubyString)pattern;
         String str = strPattern.toString();
         if (quote) str = RubyRegexp.escapeSpecialChars(strPattern.toString());
         return RubyRegexp.newRegexp(getRuntime(), str, 0, null);
     }
 
     /** rb_str_scan
      *
      */
     @JRubyMethod(name = "scan", required = 1, frame = true)
     public IRubyObject scan(IRubyObject arg, Block block) {
         RubyRegexp pattern = getPat(arg, true);
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
 
         // Fix for JRUBY-97: Temporary fix pending
         // decision on UTF8-based string implementation.
         // Move toString() call outside loop.
         boolean utf8 = pattern.getCode() == KCode.UTF8;
         
         String str = toString(utf8);
 
         RegexpPattern pat = pattern.getPattern();
         RegexpMatcher mat = pat.matcher(str);
         
         if (!block.isGiven()) {
             RubyArray result = runtime.newArray();
             RubyMatchData md = matchdata(runtime, str, mat, utf8); 
             context.getPreviousFrame().setBackRef(md);
             while(mat.find()) {
                 int groups = mat.groupCount();
                 if (groups == 1) {
                     result.append(substr(runtime, str, mat.start(0), mat.length(0), utf8));
                 } else {
                     RubyArray sub = runtime.newArray(groups);
                     for (int i=1; i < groups; i++){
                         sub.append(mat.isCaptured(i) ? substr(runtime, str, mat.start(i), mat.length(i), utf8) : runtime.getNil());
                     }
                     result.append(sub);
                 }
                 md.invalidateRegs();
             }
             return result;
         } 
             
         while(mat.find()) {
             int groups = mat.groupCount();
 
             context.getPreviousFrame().setBackRef(matchdata(runtime, str, mat, utf8));
             
             if (groups == 1) {
                 block.yield(context, substr(runtime, str, mat.start(0), mat.length(0), utf8));
             } else {
                 RubyArray sub = runtime.newArray(groups);
                 for (int i=1; i < groups; i++){
                     sub.append(mat.isCaptured(i) ? substr(runtime, str, mat.start(i), mat.length(i), utf8) : runtime.getNil());                        
                 }
                 block.yield(context, sub);
             }
         }
         
         context.getPreviousFrame().setBackRef(matchdata(runtime, str, mat, utf8));
 
         return this;
     }
     
     private final RubyString substr(Ruby runtime, String str, int beg, int len, boolean utf8) {
         if (utf8) {
             if (len == 0) return newEmptyString(runtime, getMetaClass());
             return new RubyString(runtime, getMetaClass(), new ByteList(toUTF(str.substring(beg, beg + len)), false));
         } else {
             return makeShared(beg, len);
         }
     }
     
     private final String toString(boolean utf8) {
         String str = toString();
         if (utf8) {
             try {
                 str = new String(ByteList.plain(str), "UTF8");
             } catch(Exception e){}
         }   
         return str;
     }
     
     private final RubyMatchData matchdata(Ruby runtime, String str, RegexpMatcher mat, boolean utf8) { 
         return mat.createOrReplace(null, str, this, utf8);
     }
     
     private static final ByteList SPACE_BYTELIST = new ByteList(ByteList.plain(" "));
 
     
     private final IRubyObject justify(IRubyObject[]args, char jflag) {
         Ruby runtime = getRuntime();        
         Arity.scanArgs(runtime, args, 1, 1);
         
         int width = RubyFixnum.num2int(args[0]);
         
         int f, flen = 0;
         byte[]fbuf;
         
         IRubyObject pad;
 
         if (args.length == 2) {
             pad = args[1].convertToString();
             ByteList fList = ((RubyString)pad).value;
             f = fList.begin;
             flen = fList.realSize;
 
             if (flen == 0) throw getRuntime().newArgumentError("zero width padding");
             
             fbuf = fList.bytes;
         } else {
             f = SPACE_BYTELIST.begin;
             flen = SPACE_BYTELIST.realSize;
             fbuf = SPACE_BYTELIST.bytes;
             pad = runtime.getNil();
         }
         
         if (width < 0 || value.realSize >= width) return strDup();
         
         ByteList res = new ByteList(width);
         res.realSize = width;
         
         int p = res.begin;
         int pend;
         byte[] pbuf = res.bytes;
         
         if (jflag != 'l') {
             int n = width - value.realSize;
             pend = p + ((jflag == 'r') ? n : n / 2);
             if (flen <= 1) {
                 while (p < pend) pbuf[p++] = fbuf[f];
             } else {
                 int q = f;
                 while (p + flen <= pend) {
                     System.arraycopy(fbuf, f, pbuf, p, flen);
                     p += flen;
                 }
                 while (p < pend) pbuf[p++] = fbuf[q++];
             }
         }
         
         System.arraycopy(value.bytes, value.begin, pbuf, p, value.realSize);
         
         if (jflag != 'r') {
             p += value.realSize;
             pend = res.begin + width;
             if (flen <= 1) {
                 while (p < pend) pbuf[p++] = fbuf[f];
             } else {
                 while (p + flen <= pend) {
                     System.arraycopy(fbuf, f, pbuf, p, flen);
                     p += flen;
                 }
                 while (p < pend) pbuf[p++] = fbuf[f++];
             }
             
         }
         
         RubyString resStr = new RubyString(runtime, getMetaClass(), res);
         resStr.infectBy(this);
         if (flen > 0) resStr.infectBy(pad);
         return resStr;
         
     }
 
     /** rb_str_ljust
      *
      */
     @JRubyMethod(name = "ljust", required = 1, optional = 1)
     public IRubyObject ljust(IRubyObject [] args) {
         return justify(args, 'l');
     }
 
     /** rb_str_rjust
      *
      */
     @JRubyMethod(name = "rjust", required = 1, optional = 1)
     public IRubyObject rjust(IRubyObject [] args) {
         return justify(args, 'r');
     }
 
     @JRubyMethod(name = "center", required = 1, optional = 1)
     public IRubyObject center(IRubyObject[] args) {
         return justify(args, 'c');
     }
 
     @JRubyMethod(name = "chop")
     public IRubyObject chop() {
         RubyString str = strDup();
         str.chop_bang();
         return str;
     }
 
     /** rb_str_chop_bang
      * 
      */
     @JRubyMethod(name = "chop!")
     public IRubyObject chop_bang() {
         int end = value.realSize - 1;
         if (end < 0) return getRuntime().getNil(); 
 
         if ((value.bytes[value.begin + end]) == '\n') {
             if (end > 0 && (value.bytes[value.begin + end - 1]) == '\r') end--;
         }
 
         view(0, end);
         return this;
     }
 
     /** rb_str_chop
      * 
      */
     @JRubyMethod(name = "chomp", optional = 1)
     public RubyString chomp(IRubyObject[] args) {
         RubyString str = strDup();
         str.chomp_bang(args);
         return str;
     }
 
     /**
      * rb_str_chomp_bang
      *
      * In the common case, removes CR and LF characters in various ways depending on the value of
      *   the optional args[0].
      * If args.length==0 removes one instance of CR, CRLF or LF from the end of the string.
      * If args.length>0 and args[0] is "\n" then same behaviour as args.length==0 .
      * If args.length>0 and args[0] is "" then removes trailing multiple LF or CRLF (but no CRs at
      *   all(!)).
      * @param args See method description.
      */
     @JRubyMethod(name = "chomp!", optional = 1)
     public IRubyObject chomp_bang(IRubyObject[] args) {
         IRubyObject rsObj;
 
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 0) {
             int len = value.length();
             if (len == 0) return getRuntime().getNil();
             byte[]buff = value.bytes;
 
             rsObj = getRuntime().getGlobalVariables().get("$/");
 
             if (rsObj == getRuntime().getGlobalVariables().getDefaultSeparator()) {
                 int realSize = value.realSize;
                 if ((buff[len - 1] & 0xFF) == '\n') {
                     realSize--;
                     if (realSize > 0 && (buff[realSize - 1] & 0xFF) == '\r') realSize--;
                     view(0, realSize);
                 } else if ((buff[len - 1] & 0xFF) == '\r') {
                     realSize--;
                     view(0, realSize);
                 } else {
                     modifyCheck();
                     return getRuntime().getNil();
                 }
                 return this;                
             }
         } else {
             rsObj = args[0];
         }
 
         if (rsObj.isNil()) return getRuntime().getNil();
 
         RubyString rs = rsObj.convertToString();
         int len = value.realSize;
         if (len == 0) return getRuntime().getNil();
         byte[]buff = value.bytes;
         int rslen = rs.value.realSize;
 
         if (rslen == 0) {
             while (len > 0 && (buff[len - 1] & 0xFF) == '\n') {
                 len--;
                 if (len > 0 && (buff[len - 1] & 0xFF) == '\r') len--;
             }
             if (len < value.realSize) {
                 view(0, len);
                 return this;
             }
             return getRuntime().getNil();
         }
 
         if (rslen > len) return getRuntime().getNil();
         int newline = rs.value.bytes[rslen - 1] & 0xFF;
 
         if (rslen == 1 && newline == '\n') {
             buff = value.bytes;
             int realSize = value.realSize;
             if ((buff[len - 1] & 0xFF) == '\n') {
                 realSize--;
                 if (realSize > 0 && (buff[realSize - 1] & 0xFF) == '\r') realSize--;
                 view(0, realSize);
             } else if ((buff[len - 1] & 0xFF) == '\r') {
                 realSize--;
                 view(0, realSize);
             } else {
                 modifyCheck();
                 return getRuntime().getNil();
             }
             return this;                
         }
 
         if ((buff[len - 1] & 0xFF) == newline && rslen <= 1 || value.endsWith(rs.value)) {
             view(0, value.realSize - rslen);
             return this;            
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_str_lstrip
      * 
      */
     @JRubyMethod(name = "lstrip")
     public IRubyObject lstrip() {
         RubyString str = strDup();
         str.lstrip_bang();
         return str;
     }
 
     private final static boolean[] WHITESPACE = new boolean[256];
     static {
         WHITESPACE[((byte)' ')+128] = true;
         WHITESPACE[((byte)'\t')+128] = true;
         WHITESPACE[((byte)'\n')+128] = true;
         WHITESPACE[((byte)'\r')+128] = true;
         WHITESPACE[((byte)'\f')+128] = true;
     }
 
 
     /** rb_str_lstrip_bang
      */
     @JRubyMethod(name = "lstrip!")
     public IRubyObject lstrip_bang() {
         if (value.realSize == 0) return getRuntime().getNil();
         
         int i=0;
         while (i < value.realSize && WHITESPACE[value.bytes[value.begin+i]+128]) i++;
         
         if (i > 0) {
             view(i, value.realSize - i);
             return this;
         }
         
         return getRuntime().getNil();
     }
 
     /** rb_str_rstrip
      *  
      */
     @JRubyMethod(name = "rstrip")
     public IRubyObject rstrip() {
         RubyString str = strDup();
         str.rstrip_bang();
         return str;
     }
 
     /** rb_str_rstrip_bang
      */ 
     @JRubyMethod(name = "rstrip!")
     public IRubyObject rstrip_bang() {
         if (value.realSize == 0) return getRuntime().getNil();
         int i=value.realSize - 1;
 
         while (i >= 0 && value.bytes[value.begin+i] == 0) i--;
         while (i >= 0 && WHITESPACE[value.bytes[value.begin+i]+128]) i--;
 
         if (i < value.realSize - 1) {
             view(0, i + 1);
             return this;
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_str_strip
      *
      */
     @JRubyMethod(name = "strip")
     public IRubyObject strip() {
         RubyString str = strDup();
         str.strip_bang();
         return str;
         }
 
     /** rb_str_strip_bang
      */
     @JRubyMethod(name = "strip!")
     public IRubyObject strip_bang() {
         IRubyObject l = lstrip_bang();
         IRubyObject r = rstrip_bang();
 
         if(l.isNil() && r.isNil()) {
             return l;
         }
         return this;
     }
 
     /** rb_str_count
      *
      */
     @JRubyMethod(name = "count", required = 1, rest = true)
     public IRubyObject count(IRubyObject[] args) {
         if (args.length < 1) throw getRuntime().newArgumentError("wrong number of arguments");
         if (value.realSize == 0) return getRuntime().newFixnum(0);
 
         boolean[]table = new boolean[TRANS_SIZE];
         boolean init = true;
         for (int i=0; i<args.length; i++) {
             RubyString s = args[i].convertToString();
             s.setup_table(table, init);
             init = false;
         }
 
         int s = value.begin;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         int i = 0;
 
         while (s < send) if (table[buf[s++] & 0xff]) i++;
 
         return getRuntime().newFixnum(i);
     }
 
     /** rb_str_delete
      *
      */
     @JRubyMethod(name = "delete", required = 1, rest = true)
     public IRubyObject delete(IRubyObject[] args) {
         RubyString str = strDup();
         str.delete_bang(args);
         return str;
     }
 
     /** rb_str_delete_bang
      *
      */
     @JRubyMethod(name = "delete!", required = 1, rest = true)
     public IRubyObject delete_bang(IRubyObject[] args) {
         if (args.length < 1) throw getRuntime().newArgumentError("wrong number of arguments");
         
         boolean[]squeeze = new boolean[TRANS_SIZE];
 
         boolean init = true;
         for (int i=0; i<args.length; i++) {
             RubyString s = args[i].convertToString();
             s.setup_table(squeeze, init);
             init = false;
         }
         
         modify();
         
         if (value.realSize == 0) return getRuntime().getNil();
         int s = value.begin;
         int t = s;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         boolean modify = false;
         
         while (s < send) {
             if (squeeze[buf[s] & 0xff]) {
                 modify = true;
             } else {
                 buf[t++] = buf[s];
             }
             s++;
         }
         value.realSize = t - value.begin;
         
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     /** rb_str_squeeze
      *
      */
     @JRubyMethod(name = "squeeze", rest = true)
     public IRubyObject squeeze(IRubyObject[] args) {
         RubyString str = strDup();
         str.squeeze_bang(args);        
         return str;        
     }
 
     /** rb_str_squeeze_bang
      *
      */
     @JRubyMethod(name = "squeeze!", rest = true)
     public IRubyObject squeeze_bang(IRubyObject[] args) {
         if (value.realSize == 0) return getRuntime().getNil();
 
         final boolean squeeze[] = new boolean[TRANS_SIZE];
 
         if (args.length == 0) {
             for (int i=0; i<TRANS_SIZE; i++) squeeze[i] = true;
         } else {
             boolean init = true;
             for (int i=0; i<args.length; i++) {
                 RubyString s = args[i].convertToString();
                 s.setup_table(squeeze, init);
                 init = false;
             }
         }
 
         modify();
 
         int s = value.begin;
         int t = s;
         int send = s + value.realSize;
         byte[]buf = value.bytes;
         int save = -1;
 
         while (s < send) {
             int c = buf[s++] & 0xff;
             if (c != save || !squeeze[c]) buf[t++] = (byte)(save = c);
         }
 
         if (t - value.begin != value.realSize) { // modified
             value.realSize = t - value.begin; 
             return this;
         }
 
         return getRuntime().getNil();
     }
 
     /** rb_str_tr
      *
      */
     @JRubyMethod(name = "tr", required = 2)
     public IRubyObject tr(IRubyObject src, IRubyObject repl) {
         RubyString str = strDup();
         str.tr_trans(src, repl, false);        
         return str;        
     }
     
     /** rb_str_tr_bang
     *
     */
     @JRubyMethod(name = "tr!", required = 2)
     public IRubyObject tr_bang(IRubyObject src, IRubyObject repl) {
         return tr_trans(src, repl, false);
     }    
     
     private static final class TR {
         int gen, now, max;
         int p, pend;
         byte[]buf;
     }
 
     private static final int TRANS_SIZE = 256;
     
     /** tr_setup_table
      * 
      */
     private final void setup_table(boolean[]table, boolean init) {
         final boolean[]buf = new boolean[TRANS_SIZE];
         final TR tr = new TR();
         int c;
         
         boolean cflag = false;
         
         tr.p = value.begin;
         tr.pend = value.begin + value.realSize;
         tr.buf = value.bytes;
         tr.gen = tr.now = tr.max = 0;
         
         if (value.realSize > 1 && value.bytes[value.begin] == '^') {
             cflag = true;
             tr.p++;
         }
         
         if (init) for (int i=0; i<TRANS_SIZE; i++) table[i] = true;
         
         for (int i=0; i<TRANS_SIZE; i++) buf[i] = cflag;
         while ((c = trnext(tr)) >= 0) buf[c & 0xff] = !cflag;
         for (int i=0; i<TRANS_SIZE; i++) table[i] = table[i] && buf[i];
     }
     
     /** tr_trans
     *
     */    
     private final IRubyObject tr_trans(IRubyObject src, IRubyObject repl, boolean sflag) {
         if (value.realSize == 0) return getRuntime().getNil();
         
         ByteList replList = repl.convertToString().value;
         
         if (replList.realSize == 0) return delete_bang(new IRubyObject[]{src});
 
         ByteList srcList = src.convertToString().value;
         
         final TR trsrc = new TR();
         final TR trrepl = new TR();
         
         boolean cflag = false;
         boolean modify = false;
         
         trsrc.p = srcList.begin;
         trsrc.pend = srcList.begin + srcList.realSize;
         trsrc.buf = srcList.bytes;
         if (srcList.realSize >= 2 && srcList.bytes[srcList.begin] == '^') {
             cflag = true;
             trsrc.p++;
         }       
         
         trrepl.p = replList.begin;
         trrepl.pend = replList.begin + replList.realSize;
         trrepl.buf = replList.bytes;
         
         trsrc.gen = trrepl.gen = 0;
         trsrc.now = trrepl.now = 0;
         trsrc.max = trrepl.max = 0;
         
         int c;
         final int[]trans = new int[TRANS_SIZE];
         if (cflag) {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = 1;
             while ((c = trnext(trsrc)) >= 0) trans[c & 0xff] = -1;
             while ((c = trnext(trrepl)) >= 0); 
             for (int i=0; i<TRANS_SIZE; i++) {
                 if (trans[i] >= 0) trans[i] = trrepl.now;
             }
         } else {
             for (int i=0; i<TRANS_SIZE; i++) trans[i] = -1;
             while ((c = trnext(trsrc)) >= 0) {
                 int r = trnext(trrepl);
                 if (r == -1) r = trrepl.now;
                 trans[c & 0xff] = r;
             }
         }
         
         modify();
         
         int s = value.begin;
         int send = s + value.realSize;
         byte sbuf[] = value.bytes;
         
         if (sflag) {
             int t = s;
             int c0, last = -1;
             while (s < send) {
                 c0 = sbuf[s++];
                 if ((c = trans[c0 & 0xff]) >= 0) {
                     if (last == c) continue;
                     last = c;
                     sbuf[t++] = (byte)(c & 0xff);
                     modify = true;
                 } else {
                     last = -1;
                     sbuf[t++] = (byte)c0;
                 }
             }
             
             if (value.realSize > (t - value.begin)) {
                 value.realSize = t - value.begin;
                 modify = true;
             }
         } else {
             while (s < send) {
                 if ((c = trans[sbuf[s] & 0xff]) >= 0) {
                     sbuf[s] = (byte)(c & 0xff);
                     modify = true;
                 }
                 s++;
             }
         }
         
         if (modify) return this;
         return getRuntime().getNil();
     }
 
     /** trnext
     *
     */    
     private final int trnext(TR t) {
         byte [] buf = t.buf;
         
         for (;;) {
             if (t.gen == 0) {
                 if (t.p == t.pend) return -1;
                 if (t.p < t.pend -1 && buf[t.p] == '\\') t.p++;
                 t.now = buf[t.p++];
                 if (t.p < t.pend - 1 && buf[t.p] == '-') {
                     t.p++;
                     if (t.p < t.pend) {
                         if (t.now > buf[t.p]) {
                             t.p++;
                             continue;
                         }
                         t.gen = 1;
                         t.max = buf[t.p++];
                     }
                 }
                 return t.now & 0xff;
             } else if (++t.now < t.max) {
                 return t.now & 0xff;
             } else {
                 t.gen = 0;
                 return t.max & 0xff;
             }
         }
     }    
 
     /** rb_str_tr_s
      *
      */
     @JRubyMethod(name = "tr_s", required = 2)
     public IRubyObject tr_s(IRubyObject src, IRubyObject repl) {
         RubyString str = strDup();
         str.tr_trans(src, repl, true);        
         return str;        
     }
 
     /** rb_str_tr_s_bang
      *
      */
     @JRubyMethod(name = "tr_s!", required = 2)
     public IRubyObject tr_s_bang(IRubyObject src, IRubyObject repl) {
         return tr_trans(src, repl, true);
     }
 
     /** rb_str_each_line
      *
      */
-    @JRubyMethod(name = "each_line", required = 0, optional = 1, frame = true, alias = "each")
+    @JRubyMethod(name = {"each_line", "each"}, required = 0, optional = 1, frame = true)
     public IRubyObject each_line(IRubyObject[] args, Block block) {
         byte newline;
         int p = value.begin;
         int pend = p + value.realSize;
         int s;
         int ptr = p;
         int len = value.realSize;
         int rslen;
         IRubyObject line;
         
 
         IRubyObject _rsep;
         if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 0) {
             _rsep = getRuntime().getGlobalVariables().get("$/");
         } else {
             _rsep = args[0];
         }
 
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if(_rsep.isNil()) {
             block.yield(tc, this);
             return this;
         }
         
         RubyString rsep = stringValue(_rsep);
         ByteList rsepValue = rsep.value;
         byte[] strBytes = value.bytes;
 
         rslen = rsepValue.realSize;
         
         if(rslen == 0) {
             newline = '\n';
         } else {
             newline = rsepValue.bytes[rsepValue.begin + rslen-1];
         }
 
         s = p;
         p+=rslen;
 
         for(; p < pend; p++) {
             if(rslen == 0 && strBytes[p] == '\n') {
                 if(strBytes[++p] != '\n') {
                     continue;
                 }
                 while(strBytes[p] == '\n') {
                     p++;
                 }
             }
             if(ptr<p && strBytes[p-1] == newline &&
                (rslen <= 1 || 
                 ByteList.memcmp(rsepValue.bytes, rsepValue.begin, rslen, strBytes, p-rslen, rslen) == 0)) {
                 line = RubyString.newStringShared(getRuntime(), getMetaClass(), this.value.makeShared(s, p-s));
                 line.infectBy(this);
                 block.yield(tc, line);
                 modifyCheck(strBytes,len);
                 s = p;
             }
         }
 
         if(s != pend) {
             if(p > pend) {
                 p = pend;
             }
             line = RubyString.newStringShared(getRuntime(), getMetaClass(), this.value.makeShared(s, p-s));
             line.infectBy(this);
             block.yield(tc, line);
         }
 
         return this;
     }
 
     /**
      * rb_str_each_byte
      */
     @JRubyMethod(name = "each_byte", frame = true)
     public RubyString each_byte(Block block) {
         int lLength = value.length();
         Ruby runtime = getRuntime();
         ThreadContext context = runtime.getCurrentContext();
         for (int i = 0; i < lLength; i++) {
             block.yield(context, runtime.newFixnum(value.get(i) & 0xFF));
         }
         return this;
     }
 
     /** rb_str_intern
      *
      */
     public RubySymbol intern() {
         String s = toString();
         if (s.length() == 0) {
             throw getRuntime().newArgumentError("interning empty string");
         }
         if (s.indexOf('\0') >= 0) {
             throw getRuntime().newArgumentError("symbol string may not contain '\\0'");
         }
         return RubySymbol.newSymbol(getRuntime(), toString());
     }
 
-    @JRubyMethod(name = "to_sym", alias = "intern")
+    @JRubyMethod(name = {"to_sym", "intern"})
     public RubySymbol to_sym() {
         return intern();
     }
 
     @JRubyMethod(name = "sum", optional = 1)
     public RubyInteger sum(IRubyObject[] args) {
         if (args.length > 1) {
             throw getRuntime().newArgumentError("wrong number of arguments (" + args.length + " for 1)");
         }
         
         long bitSize = 16;
         if (args.length == 1) {
             long bitSizeArg = ((RubyInteger) args[0].convertToInteger()).getLongValue();
             if (bitSizeArg > 0) {
                 bitSize = bitSizeArg;
             }
         }
 
         long result = 0;
         for (int i = 0; i < value.length(); i++) {
             result += value.get(i) & 0xFF;
         }
         return getRuntime().newFixnum(bitSize == 0 ? result : result % (long) Math.pow(2, bitSize));
     }
 
     public static RubyString unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         RubyString result = newString(input.getRuntime(), input.unmarshalString());
         input.registerLinkTarget(result);
         return result;
     }
 
     /**
      * @see org.jruby.util.Pack#unpack
      */
     @JRubyMethod(name = "unpack", required = 1)
     public RubyArray unpack(IRubyObject obj) {
         return Pack.unpack(getRuntime(), this.value, stringValue(obj).value);
     }
 
     /**
      * Mutator for internal string representation.
      *
      * @param value The new java.lang.String this RubyString should encapsulate
      */
     public void setValue(CharSequence value) {
         view(ByteList.plain(value));
     }
 
     public void setValue(ByteList value) {
         view(value);
     }
 
     public CharSequence getValue() {
         return toString();
     }
 
     public String getUnicodeValue() {
         try {
             return new String(value.bytes,value.begin,value.realSize, "UTF8");
         } catch (Exception e) {
             throw new RuntimeException("Something's seriously broken with encodings", e);
         }
     }
 
     public static byte[] toUTF(String string) {
         try {
             return string.getBytes("UTF8");
         } catch (Exception e) {
             throw new RuntimeException("Something's seriously broken with encodings", e);
         }
     }
     
     public void setUnicodeValue(String newValue) {
         view(toUTF(newValue));
     }
 
     public byte[] getBytes() {
         return value.bytes();
     }
 
     public ByteList getByteList() {
         return value;
     }
 }
