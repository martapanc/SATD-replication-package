diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index c82f0c5914..c57d3e7455 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1967,1562 +1967,1566 @@ public final class Ruby {
 
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
 
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
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
         return verbose;
     }
 
     public boolean isVerbose() {
         return isVerbose;
     }
 
     public boolean warningsEnabled() {
         return warningsEnabled;
     }
 
     /** Setter for property isVerbose.
      * @param verbose New value of property isVerbose.
      */
     public void setVerbose(IRubyObject verbose) {
         this.verbose = verbose;
         isVerbose = verbose.isTrue();
         warningsEnabled = !verbose.isNil();
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
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), 0, false, true, false, config));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
         
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
 
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, 
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, true, config));
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
 
         if (RubyException.TRACE_TYPE == RubyException.RUBINIUS) {
             printRubiniusTrace(excp);
             return;
         }
 
         ThreadContext context = getCurrentContext();
         IRubyObject backtrace = excp.callMethod(context, "backtrace");
 
         PrintStream errorStream = getErrorStream();
         if (backtrace.isNil() || !(backtrace instanceof RubyArray)) {
             if (context.getFile() != null) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
             } else {
                 errorStream.print(context.getLine());
             }
         } else if (((RubyArray) backtrace).getLength() == 0) {
             printErrorPos(context, errorStream);
         } else {
             IRubyObject mesg = ((RubyArray) backtrace).first();
 
             if (mesg.isNil()) {
                 printErrorPos(context, errorStream);
             } else {
                 errorStream.print(mesg);
             }
         }
 
         RubyClass type = excp.getMetaClass();
         String info = excp.toString();
 
         if (type == getRuntimeError() && (info == null || info.length() == 0)) {
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
 
     private void printRubiniusTrace(RubyException exception) {
 
         ThreadContext.RubyStackTraceElement[] frames = exception.getBacktraceFrames();
 
         ArrayList firstParts = new ArrayList();
         int longestFirstPart = 0;
         for (ThreadContext.RubyStackTraceElement frame : frames) {
             String firstPart = frame.getClassName() + "#" + frame.getMethodName();
             if (firstPart.length() > longestFirstPart) longestFirstPart = firstPart.length();
             firstParts.add(firstPart);
         }
 
         // determine spacing
         int center = longestFirstPart
                 + 2 // initial spaces
                 + 1; // spaces before "at"
 
         StringBuffer buffer = new StringBuffer();
 
         buffer
                 .append("An exception has occurred:\n")
                 .append("    ");
 
         if (exception.getMetaClass() == getRuntimeError() && exception.message(getCurrentContext()).toString().length() == 0) {
             buffer.append("No current exception (RuntimeError)");
         } else {
             buffer.append(exception.message(getCurrentContext()).toString());
         }
 
         buffer
                 .append('\n')
                 .append('\n')
                 .append("Backtrace:\n");
 
         int i = 0;
         for (ThreadContext.RubyStackTraceElement frame : frames) {
             String firstPart = (String)firstParts.get(i);
             String secondPart = frame.getFileName() + ":" + frame.getLineNumber();
             
             buffer.append("  ");
             for (int j = 0; j < center - firstPart.length(); j++) {
                 buffer.append(' ');
             }
             buffer.append(firstPart);
             buffer.append(" at ");
             buffer.append(secondPart);
             buffer.append('\n');
             i++;
         }
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(buffer.toString());
     }
 
     private void printErrorPos(ThreadContext context, PrintStream errorStream) {
         if (context.getFile() != null) {
             if (context.getFrameName() != null) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
                 errorStream.print(":in '" + context.getFrameName() + '\'');
             } else if (context.getLine() != 0) {
                 errorStream.print(context.getFile() + ":" + context.getLine());
             } else {
                 errorStream.print(context.getFile());
             }
         }
     }
     
     public void loadFile(String scriptName, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(scriptName);
             context.preNodeEval(objectClass, self, scriptName);
 
             parseFile(in, scriptName, null).interpret(this, context, self, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
         }
     }
     
     public void compileAndLoadFile(String filename, InputStream in, boolean wrap) {
         IRubyObject self = wrap ? TopSelfFactory.createTopSelf(this) : getTopSelf();
         ThreadContext context = getCurrentContext();
         String file = context.getFile();
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
             context.preNodeEval(objectClass, self, filename);
             
             Node scriptNode = parseFile(in, filename, null);
             
             Script script = tryCompile(scriptNode, new JRubyClassLoader(jrubyClassLoader));
             if (script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
             }
 
             runScript(script);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.postNodeEval();
             context.setFile(file);
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
 
     public class CallTraceFuncHook extends EventHook {
         private RubyProc traceFunc;
         
         public void setTraceFunc(RubyProc traceFunc) {
             this.traceFunc = traceFunc;
         }
         
         public void eventHandler(ThreadContext context, String eventName, String file, int line, String name, IRubyObject type) {
             if (!context.isWithinTrace()) {
                 if (file == null) file = "(ruby)";
                 if (type == null) type = getFalse();
                 
                 RubyBinding binding = RubyBinding.newBinding(Ruby.this, context.currentBinding());
 
                 context.preTrace();
                 try {
                     traceFunc.call(context, new IRubyObject[] {
                         newString(eventName), // event name
                         newString(file), // filename
                         newFixnum(line), // line numbers should be 1-based
                         name != null ? newSymbol(name) : getNil(),
                         binding,
                         type
                     });
                 } finally {
                     context.postTrace();
                 }
             }
         }
 
         public boolean isInterestedInEvent(RubyEvent event) {
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
     
     public void callEventHooks(ThreadContext context, RubyEvent event, String file, int line, String name, IRubyObject type) {
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
 
     // use this for JRuby-internal finalizers
     public void addInternalFinalizer(Finalizable finalizer) {
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers == null) {
                 internalFinalizers = new WeakHashMap<Finalizable, Object>();
             }
             internalFinalizers.put(finalizer, null);
         }
     }
 
     // this method is for finalizers registered via ObjectSpace
     public void addFinalizer(Finalizable finalizer) {
         synchronized (finalizersMutex) {
             if (finalizers == null) {
                 finalizers = new WeakHashMap<Finalizable, Object>();
             }
             finalizers.put(finalizer, null);
         }
     }
     
     public void removeInternalFinalizer(Finalizable finalizer) {
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 internalFinalizers.remove(finalizer);
             }
         }
     }
 
     public void removeFinalizer(Finalizable finalizer) {
         synchronized (finalizersMutex) {
             if (finalizers != null) {
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
         int status = 0;
 
         while (!atExitBlocks.empty()) {
             RubyProc proc = atExitBlocks.pop();
             try {
                 proc.call(getCurrentContext(), IRubyObject.NULL_ARRAY);
             } catch (RaiseException rj) {
                 RubyException raisedException = rj.getException();
                 if (!getSystemExit().isInstance(raisedException)) {
                     status = 1;
                     printError(raisedException);
                 } else {
                     IRubyObject statusObj = raisedException.callMethod(
                             getCurrentContext(), "status");
                     if (statusObj != null && !statusObj.isNil()) {
                         status = RubyNumeric.fix2int(statusObj);
                     }
                 }
             }
         }
 
         if (finalizers != null) {
             synchronized (finalizers) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(finalizers.keySet()).iterator(); finalIter.hasNext();) {
                     Finalizable f = finalIter.next();
                     if (f != null) {
                         f.finalize();
                     }
                     finalIter.remove();
                 }
             }
         }
 
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(
                         internalFinalizers.keySet()).iterator(); finalIter.hasNext();) {
                     Finalizable f = finalIter.next();
                     if (f != null) {
                         f.finalize();
                     }
                     finalIter.remove();
                 }
             }
         }
 
         getThreadService().disposeCurrentThread();
 
         getBeanManager().unregisterCompiler();
         getBeanManager().unregisterConfig();
         getBeanManager().unregisterParserStats();
         getBeanManager().unregisterClassCache();
         getBeanManager().unregisterMethodCache();
 
         if (status != 0) {
             throw newSystemExit(status);
         }
     }
 
     // new factory methods ------------------------------------------------------------------------
 
     public RubyArray newEmptyArray() {
         return RubyArray.newEmptyArray(this);
     }
 
     public RubyArray newArray() {
         return RubyArray.newArray(this);
     }
 
     public RubyArray newArrayLight() {
         return RubyArray.newArrayLight(this);
     }
 
     public RubyArray newArray(IRubyObject object) {
         return RubyArray.newArray(this, object);
     }
 
     public RubyArray newArray(IRubyObject car, IRubyObject cdr) {
         return RubyArray.newArray(this, car, cdr);
     }
 
     public RubyArray newArray(IRubyObject... objects) {
         return RubyArray.newArray(this, objects);
     }
     
     public RubyArray newArrayNoCopy(IRubyObject... objects) {
         return RubyArray.newArrayNoCopy(this, objects);
     }
     
     public RubyArray newArrayNoCopyLight(IRubyObject... objects) {
         return RubyArray.newArrayNoCopyLight(this, objects);
     }
     
     public RubyArray newArray(List<IRubyObject> list) {
         return RubyArray.newArray(this, list);
     }
 
     public RubyArray newArray(int size) {
         return RubyArray.newArray(this, size);
     }
 
     public RubyBoolean newBoolean(boolean value) {
         return value ? trueObject : falseObject;
     }
 
     public RubyFileStat newFileStat(String filename, boolean lstat) {
         return RubyFileStat.newFileStat(this, filename, lstat);
     }
     
     public RubyFileStat newFileStat(FileDescriptor descriptor) {
         return RubyFileStat.newFileStat(this, descriptor);
     }
 
     public RubyFixnum newFixnum(long value) {
         return RubyFixnum.newFixnum(this, value);
     }
 
     public RubyFixnum newFixnum(int value) {
         return RubyFixnum.newFixnum(this, value);
     }
 
     public RubyFixnum newFixnum(Constant value) {
         return RubyFixnum.newFixnum(this, value.value());
     }
 
     public RubyFloat newFloat(double value) {
         return RubyFloat.newFloat(this, value);
     }
 
     public RubyNumeric newNumeric() {
         return RubyNumeric.newNumeric(this);
     }
 
     public RubyProc newProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, type);
 
         proc.callInit(IRubyObject.NULL_ARRAY, block);
 
         return proc;
     }
 
     public RubyProc newBlockPassProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, type);
         proc.initialize(getCurrentContext(), block);
 
         return proc;
     }
 
     public RubyBinding newBinding() {
         return RubyBinding.newBinding(this, getCurrentContext().currentBinding());
     }
 
     public RubyBinding newBinding(Binding binding) {
         return RubyBinding.newBinding(this, binding);
     }
 
     public RubyString newString() {
         return RubyString.newString(this, new ByteList());
     }
 
     public RubyString newString(String string) {
         return RubyString.newString(this, string);
     }
     
     public RubyString newString(ByteList byteList) {
         return RubyString.newString(this, byteList);
     }
 
     @Deprecated
     public RubyString newStringShared(ByteList byteList) {
         return RubyString.newStringShared(this, byteList);
     }    
 
     public RubySymbol newSymbol(String name) {
         return symbolTable.getSymbol(name);
     }
 
     /**
      * Faster than {@link #newSymbol(String)} if you already have an interned
      * name String. Don't intern your string just to call this version - the
      * overhead of interning will more than wipe out any benefit from the faster
      * lookup.
      *   
      * @param internedName the symbol name, <em>must</em> be interned! if in
      *                     doubt, call {@link #newSymbol(String)} instead.
      * @return the symbol for name
      */
     public RubySymbol fastNewSymbol(String internedName) {
         //        assert internedName == internedName.intern() : internedName + " is not interned";
 
         return symbolTable.fastGetSymbol(internedName);
     }
 
     public RubyTime newTime(long milliseconds) {
         return RubyTime.newTime(this, milliseconds);
     }
 
     public RaiseException newRuntimeError(String message) {
         return newRaiseException(getRuntimeError(), message);
     }    
     
     public RaiseException newArgumentError(String message) {
         return newRaiseException(getArgumentError(), message);
     }
 
     public RaiseException newArgumentError(int got, int expected) {
         return newRaiseException(getArgumentError(), "wrong # of arguments(" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(getErrno().fastGetClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoENOPROTOOPTError() {
         return newRaiseException(getErrno().fastGetClass("ENOPROTOOPT"), "Protocol not available");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(getErrno().fastGetClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(getErrno().fastGetClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoECONNRESETError() {
         return newRaiseException(getErrno().fastGetClass("ECONNRESET"), "Connection reset by peer");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(getErrno().fastGetClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(getErrno().fastGetClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getErrno().fastGetClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(getErrno().fastGetClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newRaiseException(getErrno().fastGetClass("EAGAIN"), message);
     }
 
     public RaiseException newErrnoEISDirError(String message) {
         return newRaiseException(getErrno().fastGetClass("EISDIR"), message);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newErrnoEISDirError("Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getErrno().fastGetClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getErrno().fastGetClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getErrno().fastGetClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTDIR"), message);
     }
 
     public RaiseException newErrnoENOTSOCKError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTSOCK"), message);
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(getErrno().fastGetClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(getErrno().fastGetClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(getErrno().fastGetClass("EDOM"), "Domain error - " + message);
     }   
     
     public RaiseException newErrnoECHILDError() {
         return newRaiseException(getErrno().fastGetClass("ECHILD"), "No child processes");
     }    
 
     public RaiseException newIndexError(String message) {
         return newRaiseException(getIndexError(), message);
     }
 
     public RaiseException newSecurityError(String message) {
         return newRaiseException(getSecurityError(), message);
     }
 
     public RaiseException newSystemCallError(String message) {
         return newRaiseException(getSystemCallError(), message);
     }
 
     public RaiseException newErrnoFromLastPOSIXErrno() {
         return newRaiseException(getErrno(getPosix().errno()), null);
     }
 
     public RaiseException newTypeError(String message) {
         return newRaiseException(getTypeError(), message);
     }
 
     public RaiseException newThreadError(String message) {
         return newRaiseException(getThreadError(), message);
     }
 
     public RaiseException newConcurrencyError(String message) {
         return newRaiseException(getConcurrencyError(), message);
     }
 
     public RaiseException newSyntaxError(String message) {
         return newRaiseException(getSyntaxError(), message);
     }
 
     public RaiseException newRegexpError(String message) {
         return newRaiseException(getRegexpError(), message);
     }
 
     public RaiseException newRangeError(String message) {
         return newRaiseException(getRangeError(), message);
     }
 
     public RaiseException newNotImplementedError(String message) {
         return newRaiseException(getNotImplementedError(), message);
     }
     
     public RaiseException newInvalidEncoding(String message) {
         return newRaiseException(fastGetClass("Iconv").fastGetClass("InvalidEncoding"), message);
     }
+    
+    public RaiseException newIllegalSequence(String message) {
+        return newRaiseException(fastGetClass("Iconv").fastGetClass("IllegalSequence"), message);
+    }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         return new RaiseException(new RubyNoMethodError(this, getNoMethodError(), message, name, args), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return newNameError(message, name, null);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException) {
         return newNameError(message, name, origException, true);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException, boolean printWhenVerbose) {
         if (printWhenVerbose && origException != null && this.isVerbose()) {
             origException.printStackTrace(getErrorStream());
         }
         return new RaiseException(new RubyNameError(
                 this, getNameError(), message, name), true);
     }
 
     public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
     }
 
     public RaiseException newLocalJumpErrorNoBlock() {
         return newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, getNil(), "no block given");
     }
 
     public RaiseException newRedoLocalJumpError() {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), "unexpected redo", RubyLocalJumpError.Reason.REDO, getNil()), true);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getLoadError(), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(is1_9() ? getRuntimeError() : getTypeError(), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemStackError(String message, StackOverflowError soe) {
         if (getDebug().isTrue()) {
             soe.printStackTrace(getInstanceConfig().getError());
         }
         return newRaiseException(getSystemStackError(), message);
     }
 
     public RaiseException newSystemExit(int status) {
         return new RaiseException(RubySystemExit.newInstance(this, status));
     }
 
     public RaiseException newIOError(String message) {
         return newRaiseException(getIOError(), message);
     }
 
     public RaiseException newStandardError(String message) {
         return newRaiseException(getStandardError(), message);
     }
 
     public RaiseException newIOErrorFromException(IOException ioe) {
         // TODO: this is kinda gross
         if(ioe.getMessage() != null) {
             if (ioe.getMessage().equals("Broken pipe")) {
                 throw newErrnoEPIPEError();
             } else if (ioe.getMessage().equals("Connection reset by peer")) {
                 throw newErrnoECONNRESETError();
             }
             return newRaiseException(getIOError(), ioe.getMessage());
         } else {
             return newRaiseException(getIOError(), "IO Error");
         }
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newRaiseException(getTypeError(), "wrong argument type " +
                 receivedObject.getMetaClass().getRealClass() + " (expected " + expectedType + ")");
     }
 
     public RaiseException newEOFError() {
         return newRaiseException(getEOFError(), "End of file reached");
     }
 
     public RaiseException newEOFError(String message) {
         return newRaiseException(getEOFError(), message);
     }
 
     public RaiseException newZeroDivisionError() {
         return newRaiseException(getZeroDivisionError(), "divided by 0");
     }
 
     public RaiseException newFloatDomainError(String message){
         return newRaiseException(getFloatDomainError(), message);
     }
 
     public RaiseException newEncodingError(String message){
         return newRaiseException(getEncodingError(), message);
     }
 
     public RaiseException newEncodingCompatibilityError(String message){
         return newRaiseException(getEncodingCompatibilityError(), message);
     }
 
     /**
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         RaiseException re = new RaiseException(this, exceptionClass, message, true);
         return re;
     }
 
     // Equivalent of Data_Wrap_Struct
     public RubyObject.Data newData(RubyClass objectClass, Object sval) {
         return new RubyObject.Data(this, objectClass, sval);
     }
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
     }
 
     public void setStackTraces(int stackTraces) {
         this.stackTraces = stackTraces;
     }
 
     public int getStackTraces() {
         return stackTraces;
     }
 
     public void setRandomSeed(long randomSeed) {
         this.randomSeed = randomSeed;
     }
 
     public long getRandomSeed() {
         return randomSeed;
     }
 
     public Random getRandom() {
         return random;
     }
 
     public ObjectSpace getObjectSpace() {
         return objectSpace;
     }
 
     private class WeakDescriptorReference extends WeakReference {
         private int fileno;
 
         public WeakDescriptorReference(ChannelDescriptor descriptor, ReferenceQueue queue) {
             super(descriptor, queue);
             this.fileno = descriptor.getFileno();
         }
 
         public int getFileno() {
             return fileno;
         }
     }
 
     public Map<Integer, WeakDescriptorReference> getDescriptors() {
         return descriptors;
     }
 
     private void cleanDescriptors() {
         Reference reference;
         while ((reference = descriptorQueue.poll()) != null) {
             int fileno = ((WeakDescriptorReference)reference).getFileno();
             descriptors.remove(fileno);
         }
     }
 
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
         cleanDescriptors();
         
         Integer filenoKey = descriptor.getFileno();
         descriptors.put(filenoKey, new WeakDescriptorReference(descriptor, descriptorQueue));
         if (isRetained) {
             retainedDescriptors.put(filenoKey, descriptor);
         }
     }
 
     public void registerDescriptor(ChannelDescriptor descriptor) {
         registerDescriptor(descriptor,false); // default: don't retain
     }
 
     public void unregisterDescriptor(int aFileno) {
         cleanDescriptors();
         
         Integer aFilenoKey = aFileno;
         descriptors.remove(aFilenoKey);
         retainedDescriptors.remove(aFilenoKey);
     }
 
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         cleanDescriptors();
         
         Reference reference = descriptors.get(aFileno);
         if (reference == null) {
             return null;
         }
         return (ChannelDescriptor)reference.get();
     }
 
     public long incrementRandomSeedSequence() {
         return randomSeedSequence++;
     }
 
     public InputStream getIn() {
         return in;
     }
 
     public PrintStream getOut() {
         return out;
     }
 
     public PrintStream getErr() {
         return err;
     }
 
     public boolean isGlobalAbortOnExceptionEnabled() {
         return globalAbortOnExceptionEnabled;
     }
 
     public void setGlobalAbortOnExceptionEnabled(boolean enable) {
         globalAbortOnExceptionEnabled = enable;
     }
 
     public boolean isDoNotReverseLookupEnabled() {
         return doNotReverseLookupEnabled;
     }
 
     public void setDoNotReverseLookupEnabled(boolean b) {
         doNotReverseLookupEnabled = b;
     }
 
     private ThreadLocal<Map<Object, Object>> inspect = new ThreadLocal<Map<Object, Object>>();
     public void registerInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         if (val == null) inspect.set(val = new IdentityHashMap<Object, Object>());
         val.put(obj, null);
     }
 
     public boolean isInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         return val == null ? false : val.containsKey(obj);
     }
 
     public void unregisterInspecting(Object obj) {
         Map<Object, Object> val = inspect.get();
         if (val != null ) val.remove(obj);
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     // The method is intentionally not public, since it typically should
     // not be used outside of the core.
     /* package-private */ void setObjectSpaceEnabled(boolean objectSpaceEnabled) {
         this.objectSpaceEnabled = objectSpaceEnabled;
     }
 
     public long getStartTime() {
         return startTime;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public String getJRubyHome() {
         return config.getJRubyHome();
     }
 
     public void setJRubyHome(String home) {
         config.setJRubyHome(home);
     }
 
     public RubyInstanceConfig getInstanceConfig() {
         return config;
     }
 
     public boolean is1_9() {
         return is1_9;
     }
 
     /** GET_VM_STATE_VERSION */
     public long getGlobalState() {
         synchronized(this) {
             return globalState;
         }
     }
 
     /** INC_VM_STATE_VERSION */
     public void incGlobalState() {
         synchronized(this) {
             globalState = (globalState+1) & 0x8fffffff;
         }
     }
 
     public static boolean isSecurityRestricted() {
         return securityRestricted;
     }
     
     public static void setSecurityRestricted(boolean restricted) {
         securityRestricted = restricted;
     }
     
     public POSIX getPosix() {
         return posix;
     }
     
     public void setRecordSeparatorVar(GlobalVariable recordSeparatorVar) {
         this.recordSeparatorVar = recordSeparatorVar;
     }
     
     public GlobalVariable getRecordSeparatorVar() {
         return recordSeparatorVar;
     }
     
     public Set<Script> getJittedMethods() {
         return jittedMethods;
     }
     
     public ExecutorService getExecutor() {
         return executor;
     }
 
     public Map<String, DateTimeZone> getTimezoneCache() {
         return timeZoneCache;
     }
 
     public int getConstantGeneration() {
         return constantGeneration;
     }
 
     public synchronized void incrementConstantGeneration() {
         constantGeneration++;
     }
 
     public <E extends Enum<E>> void loadConstantSet(RubyModule module, Class<E> enumClass) {
         for (E e : EnumSet.allOf(enumClass)) {
             Constant c = (Constant) e;
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.fastSetConstant(c.name(), newFixnum(c.value()));
             }
         }
     }
     public void loadConstantSet(RubyModule module, String constantSetName) {
         for (Constant c : ConstantSet.getConstantSet(constantSetName)) {
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.fastSetConstant(c.name(), newFixnum(c.value()));
             }
         }
     }
 
     /**
      * Get a new serial number for a new DynamicMethod instance
      * @return a new serial number
      */
     public long getNextDynamicMethodSerial() {
         return dynamicMethodSerial.getAndIncrement();
     }
 
     /**
      * Get the global object used to synchronize class-hierarchy modifications like
      * cache invalidation, subclass sets, and included hierarchy sets.
      *
      * @return The object to use for locking when modifying the hierarchy
      */
     public Object getHierarchyLock() {
         return hierarchyLock;
     }
 
     private volatile int constantGeneration = 1;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
     private Map<Integer, WeakDescriptorReference> descriptors = new ConcurrentHashMap<Integer, WeakDescriptorReference>();
     private ReferenceQueue<ChannelDescriptor> descriptorQueue = new ReferenceQueue<ChannelDescriptor>();
     // ChannelDescriptors opened by sysopen are cached to avoid collection
     private Map<Integer, ChannelDescriptor> retainedDescriptors = new ConcurrentHashMap<Integer, ChannelDescriptor>();
 
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private List<EventHook> eventHooks = new Vector<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private volatile boolean objectSpaceEnabled;
     
     private final Set<Script> jittedMethods = Collections.synchronizedSet(new WeakHashSet<Script>());
     
     private static ThreadLocal<Ruby> currentRuntime = new ThreadLocal<Ruby>();
     
     private long globalState = 1;
     
     private int safeLevel = -1;
 
     // Default objects
     private IRubyObject topSelf;
     private RubyNil nilObject;
     private IRubyObject[] singleNilArray;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     public final RubyFixnum[] fixnumCache = new RubyFixnum[2 * RubyFixnum.CACHE_OFFSET];
 
     private IRubyObject verbose;
     private boolean isVerbose, warningsEnabled;
     private IRubyObject debug;
     
     private RubyThreadGroup defaultThreadGroup;
 
     /**
      * All the core classes we keep hard references to. These are here largely
      * so that if someone redefines String or Array we won't start blowing up
      * creating strings and arrays internally. They also provide much faster
      * access than going through normal hash lookup on the Object class.
      */
     private RubyClass
            basicObjectClass, objectClass, moduleClass, classClass, nilClass, trueClass,
             falseClass, numericClass, floatClass, integerClass, fixnumClass,
             complexClass, rationalClass, enumeratorClass, generatorClass, yielderClass,
             arrayClass, hashClass, rangeClass, stringClass, encodingClass, converterClass, symbolClass,
             procClass, bindingClass, methodClass, unboundMethodClass,
             matchDataClass, regexpClass, timeClass, bignumClass, dirClass,
             fileClass, fileStatClass, ioClass, threadClass, threadGroupClass,
             continuationClass, structClass, tmsStruct, passwdStruct,
             groupStruct, procStatusClass, exceptionClass, runtimeError, ioError,
             scriptError, nameError, nameErrorMessage, noMethodError, signalException,
             rangeError, dummyClass, systemExit, localJumpError, nativeException,
             systemCallError, fatal, interrupt, typeError, argumentError, indexError, stopIteration,
             syntaxError, standardError, loadError, notImplementedError, securityError, noMemoryError,
             regexpError, eofError, threadError, concurrencyError, systemStackError, zeroDivisionError, floatDomainError,
             encodingError, encodingCompatibilityError;
 
     /**
      * All the core modules we keep direct references to, for quick access and
      * to ensure they remain available.
      */
     private RubyModule
             kernelModule, comparableModule, enumerableModule, mathModule,
             marshalModule, etcModule, fileTestModule, gcModule,
             objectSpaceModule, processModule, procUIDModule, procGIDModule,
             procSysModule, precisionModule, errnoModule;
 
     private DynamicMethod privateMethodMissing, protectedMethodMissing, variableMethodMissing,
             superMethodMissing, normalMethodMissing, defaultMethodMissing;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
 
     private long startTime = System.currentTimeMillis();
 
     private final RubyInstanceConfig config;
     private final boolean is1_9;
 
     private final InputStream in;
     private final PrintStream out;
     private final PrintStream err;
 
     // Java support
     private JavaSupport javaSupport;
     private JRubyClassLoader jrubyClassLoader;
     
     // Management/monitoring
     private BeanManager beanManager;
 
     // Parser stats
     private ParserStats parserStats;
     
     // Compilation
     private final JITCompiler jitCompiler;
 
     // Note: this field and the following static initializer
     // must be located be in this order!
     private volatile static boolean securityRestricted = false;
     static {
         if (SafePropertyAccessor.isSecurityProtected("jruby.reflection")) {
             // can't read non-standard properties
             securityRestricted = true;
         } else {
             SecurityManager sm = System.getSecurityManager();
             if (sm != null) {
                 try {
                     sm.checkCreateClassLoader();
                 } catch (SecurityException se) {
                     // can't create custom classloaders
                     securityRestricted = true;
                 }
             }
         }
     }
 
     private Parser parser = new Parser(this);
 
     private LoadService loadService;
 
     private Encoding defaultInternalEncoding, defaultExternalEncoding;
     private EncodingService encodingService;
 
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private KCode kcode = KCode.NONE;
 
     // Atomic integers for symbol and method IDs
     private AtomicInteger symbolLastId = new AtomicInteger(128);
     private AtomicInteger moduleLastId = new AtomicInteger(0);
 
     private Object respondToMethod;
 
     private Map<String, DateTimeZone> timeZoneCache = new HashMap<String,DateTimeZone>();
     /**
      * A list of "external" finalizers (the ones, registered via ObjectSpace),
      * weakly referenced, to be executed on tearDown.
      */
     private Map<Finalizable, Object> finalizers;
     
     /**
      * A list of JRuby-internal finalizers,  weakly referenced,
      * to be executed on tearDown.
      */
     private Map<Finalizable, Object> internalFinalizers;
 
     // mutex that controls modifications of user-defined finalizers
     private final Object finalizersMutex = new Object();
 
     // mutex that controls modifications of internal finalizers
     private final Object internalFinalizersMutex = new Object();
     
     // A thread pool to use for executing this runtime's Ruby threads
     private ExecutorService executor;
 
     // A global object lock for class hierarchy mutations
     private Object hierarchyLock = new Object();
 
     // An atomic long for generating DynamicMethod serial numbers
     private AtomicLong dynamicMethodSerial = new AtomicLong(0);
 }
diff --git a/src/org/jruby/RubyIconv.java b/src/org/jruby/RubyIconv.java
index 1a3ee40f1b..bf251df833 100644
--- a/src/org/jruby/RubyIconv.java
+++ b/src/org/jruby/RubyIconv.java
@@ -1,380 +1,392 @@
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
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Koichiro Ohba <koichiro@meadowy.org>
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
 
 import java.nio.ByteBuffer;
 import java.nio.CharBuffer;
 import java.nio.charset.CharacterCodingException;
 import java.nio.charset.Charset;
 import java.nio.charset.CharsetDecoder;
 import java.nio.charset.CharsetEncoder;
 import java.nio.charset.CodingErrorAction;
 import java.nio.charset.IllegalCharsetNameException;
 import java.nio.charset.MalformedInputException;
 import java.nio.charset.UnmappableCharacterException;
 import java.nio.charset.UnsupportedCharsetException;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.anno.JRubyClass;
 import org.jruby.runtime.Arity;
 
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.ByteList;
 
 @JRubyClass(name="Iconv")
 public class RubyIconv extends RubyObject {
     //static private final String TRANSLIT = "//translit";
     static private final String IGNORE = "//ignore";
 
     private CharsetDecoder fromEncoding;
     private CharsetEncoder toEncoding;
 
     public RubyIconv(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     private static final ObjectAllocator ICONV_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             return new RubyIconv(runtime, klass);
         }
     };
 
     @JRubyModule(name="Iconv::Failure")
     public static class Failure {}
     @JRubyClass(name="Iconv::IllegalSequence", parent="ArgumentError", include="Iconv::Failure")
     public static class IllegalSequence {}
     @JRubyClass(name="Iconv::InvalidCharacter", parent="ArgumentError", include="Iconv::Failure")
     public static class InvalidCharacter {}
     @JRubyClass(name="Iconv::InvalidEncoding", parent="ArgumentError", include="Iconv::Failure")
     public static class InvalidEncoding {}
     @JRubyClass(name="Iconv::OutOfRange", parent="ArgumentError", include="Iconv::Failure")
     public static class OutOfRange {}
     @JRubyClass(name="Iconv::BrokenLibrary", parent="ArgumentError", include="Iconv::Failure")
     public static class BrokenLibrary {}
 
     public static void createIconv(Ruby runtime) {
         RubyClass iconvClass = runtime.defineClass("Iconv", runtime.getObject(), ICONV_ALLOCATOR);
         
         iconvClass.defineAnnotatedMethods(RubyIconv.class);
 
         RubyModule failure = iconvClass.defineModuleUnder("Failure");
         RubyClass argumentError = runtime.getArgumentError();
 
         String[] iconvErrors = {"IllegalSequence", "InvalidCharacter", "InvalidEncoding", 
                 "OutOfRange", "BrokenLibrary"};
         
         for (int i = 0; i < iconvErrors.length; i++) {
             RubyClass subClass = iconvClass.defineClassUnder(iconvErrors[i], argumentError, RubyFailure.ICONV_FAILURE_ALLOCATOR);
             subClass.defineAnnotatedMethods(RubyFailure.class);
             subClass.includeModule(failure);
         }    
     }
     
     public static class RubyFailure extends RubyException {
         private IRubyObject success;
         private IRubyObject failed;
 
         public static RubyFailure newInstance(Ruby runtime, RubyClass excptnClass, String msg) {
             return new RubyFailure(runtime, excptnClass, msg);
         }
 
         protected static final ObjectAllocator ICONV_FAILURE_ALLOCATOR = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klass) {
                 return new RubyFailure(runtime, klass);
             }
         };
 
         protected RubyFailure(Ruby runtime, RubyClass rubyClass) {
             this(runtime, rubyClass, null);
         }
 
         public RubyFailure(Ruby runtime, RubyClass rubyClass, String message) {
             super(runtime, rubyClass, message);
         }
 
         @JRubyMethod(name = "initialize", required = 1, optional = 2, frame = true)
         public IRubyObject initialize(IRubyObject[] args, Block block) {
             super.initialize(args, block);
             success = args.length >= 2 ? args[1] : getRuntime().getNil();
             failed = args.length == 3 ? args[2] : getRuntime().getNil();
 
             return this;
         }
 
         @JRubyMethod(name = "success")
         public IRubyObject success() {
             return success;
         }
 
         @JRubyMethod(name = "failed")
         public IRubyObject failed() {
             return failed;
         }
 
         @JRubyMethod(name = "inspect")
         public IRubyObject inspect() {
             RubyModule rubyClass = getMetaClass();
             StringBuilder buffer = new StringBuilder("#<");
             buffer.append(rubyClass.getName()).append(": ").append(success.inspect().toString());
             buffer.append(", ").append(failed.inspect().toString()).append(">");
 
             return getRuntime().newString(buffer.toString());
         }
     }
 
     private static String getCharset(String encoding) {
         int index = encoding.indexOf("//");
         if (index == -1) return encoding;
         return encoding.substring(0, index);
     }
     
     /* Currently dead code, but useful when we figure out how to actually perform translit.
     private static boolean isTranslit(String encoding) {
         return encoding.toLowerCase().indexOf(TRANSLIT) != -1 ? true : false;
     }*/
     
     private static boolean isIgnore(String encoding) {
         return encoding.toLowerCase().indexOf(IGNORE) != -1 ? true : false;
     }
 
     @JRubyMethod(name = "open", required = 2, frame = true, meta = true)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject to, IRubyObject from, Block block) {
         Ruby runtime = context.getRuntime();
 
         RubyIconv iconv = newIconv(context, recv, to, from);
 
         if (!block.isGiven()) return iconv;
 
         IRubyObject result = runtime.getNil();
         try {
             result = block.yield(context, iconv);
         } finally {
             iconv.close();
         }
 
         return result;
     }
     
     private static RubyIconv newIconv(ThreadContext context, IRubyObject recv,
             IRubyObject to, IRubyObject from) {
         RubyClass klazz = (RubyClass)recv;
 
         return (RubyIconv) klazz.newInstance(
                 context, new IRubyObject[] {to, from}, Block.NULL_BLOCK);
     }
 
     @JRubyMethod(name = "initialize", required = 2, frame = true)
     public IRubyObject initialize(IRubyObject arg1, IRubyObject arg2, Block unusedBlock) {
         Ruby runtime = getRuntime();
         if (!arg1.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + arg1.getMetaClass() + " into String");
         }
         if (!arg2.respondsTo("to_str")) {
             throw runtime.newTypeError("can't convert " + arg2.getMetaClass() + " into String");
         }
 
         String to = arg1.convertToString().toString();
         String from = arg2.convertToString().toString();
 
         try {
 
             fromEncoding = Charset.forName(getCharset(from)).newDecoder();
             toEncoding = Charset.forName(getCharset(to)).newEncoder();
 
-            if (!isIgnore(from)) fromEncoding.onUnmappableCharacter(CodingErrorAction.REPORT);
-            if (!isIgnore(to)) toEncoding.onUnmappableCharacter(CodingErrorAction.REPORT);
+            if (isIgnore(to)) {
+                fromEncoding.onUnmappableCharacter(CodingErrorAction.IGNORE);
+                fromEncoding.onMalformedInput(CodingErrorAction.IGNORE);
+                toEncoding.onUnmappableCharacter(CodingErrorAction.IGNORE);
+                toEncoding.onMalformedInput(CodingErrorAction.IGNORE);
+            } else {
+                fromEncoding.onUnmappableCharacter(CodingErrorAction.REPORT);
+                fromEncoding.onMalformedInput(CodingErrorAction.REPORT);
+                toEncoding.onUnmappableCharacter(CodingErrorAction.REPORT);
+                toEncoding.onMalformedInput(CodingErrorAction.REPORT);
+            }
         } catch (IllegalCharsetNameException e) {
             throw runtime.newInvalidEncoding("invalid encoding");
         } catch (UnsupportedCharsetException e) {
             throw runtime.newInvalidEncoding("invalid encoding");
         } catch (Exception e) {
             throw runtime.newSystemCallError(e.toString());
         }
 
         return this;
     }
 
     @JRubyMethod(name = "close")
     public IRubyObject close() {
         toEncoding = null;
         fromEncoding = null;
         return RubyString.newEmptyString(getRuntime());
     }
 
     @JRubyMethod
     public IRubyObject iconv(IRubyObject str) {
         return iconv(str, 0, -1);
     }
 
     @JRubyMethod
     public IRubyObject iconv(IRubyObject str, IRubyObject startArg) {
         int start = 0;
         if (!startArg.isNil()) start = RubyNumeric.fix2int(startArg);
         return iconv(str, start, -1);
     }
 
     @JRubyMethod
     public IRubyObject iconv(IRubyObject str, IRubyObject startArg, IRubyObject endArg) {
         int start = 0;
         int end = -1;
 
         if (!startArg.isNil()) start = RubyNumeric.fix2int(startArg);
         if (!endArg.isNil()) end = RubyNumeric.fix2int(endArg);
 
         return iconv(str, start, end);
     }
     
     private IRubyObject iconv(IRubyObject str, int start, int end) {
         if (str.isNil()) {
             fromEncoding.reset();
             toEncoding.reset();
             return RubyString.newEmptyString(getRuntime());
         }
 
         return _iconv(str.convertToString(), start, end);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the versions with one, two or three arguments.
      */
     public IRubyObject iconv(IRubyObject[] args) {
         switch (args.length) {
         case 1:
             return iconv(args[0]);
         case 2:
             return iconv(args[0], args[1]);
         case 3:
             return iconv(args[0], args[1], args[2]);
         default:
             Arity.raiseArgumentError(getRuntime(), args.length, 1, 2);
             return null; // not reached
         }
     }
 
     // FIXME: We are assuming that original string will be raw bytes.  If -Ku is provided
     // this will not be true, but that is ok for now.  Deal with that when someone needs it.
     private IRubyObject _iconv(RubyString str, int start, int end) {
         if (fromEncoding == null) {
             throw getRuntime().newArgumentError("closed iconv");
         }
         
         ByteList bytes = str.getByteList();
         
         // treat start and end as start...end for end >= 0, start..end for end < 0
         if (start < 0) {
             start += bytes.length();
         }
         
         if (end < 0) {
             end += 1 + bytes.length();
         } else if (end > bytes.length()) {
             end = bytes.length();
         }
         
         if (start < 0 || end < start) { // invalid ranges result in an empty string
             return RubyString.newEmptyString(getRuntime());
         }
         
         ByteBuffer buf = ByteBuffer.wrap(bytes.unsafeBytes(), bytes.begin() + start, end - start);
         
         try {
             CharBuffer cbuf = fromEncoding.decode(buf);
             buf = toEncoding.encode(cbuf);
         } catch (MalformedInputException e) {
+            throw getRuntime().newIllegalSequence(str.toString());
         } catch (UnmappableCharacterException e) {
+            throw getRuntime().newIllegalSequence(str.toString());
         } catch (CharacterCodingException e) {
             throw getRuntime().newInvalidEncoding("invalid sequence");
         } catch (IllegalStateException e) {
+            throw getRuntime().newIllegalSequence(str.toString());
         }
         byte[] arr = buf.array();
         
         return getRuntime().newString(new ByteList(arr, 0, buf.limit()));
     }
 
     @JRubyMethod(name = "iconv", required = 2, rest = true, meta = true)
     public static IRubyObject iconv(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         return convertWithArgs(context, recv, args, "iconv");
     }
     
     @JRubyMethod(name = "conv", required = 3, rest = true, meta = true)
     public static IRubyObject conv(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
         return convertWithArgs(context, recv, args, "conv").join(context, RubyString.newEmptyString(recv.getRuntime()));
     }
 
     @JRubyMethod(name = "charset_map", meta= true)
     public static IRubyObject charset_map_get(IRubyObject recv) {
         return recv.getRuntime().getCharsetMap();
     }
 
     private static String mapCharset(ThreadContext context, IRubyObject val) {
         RubyHash charset = val.getRuntime().getCharsetMap();
         if (charset.size() > 0) {
             RubyString key = val.callMethod(context, "downcase").convertToString();
             IRubyObject tryVal = charset.fastARef(key);
             if (tryVal != null) val = tryVal;
         }
 
         return val.convertToString().toString();
     }
 
     public static RubyArray convertWithArgs(ThreadContext context, IRubyObject recv, IRubyObject[] args, String function) {
         assert args.length >= 2;
 
         RubyArray array = context.getRuntime().newArray(args.length - 2);
         RubyIconv iconv = newIconv(context, recv, args[0], args[1]);
 
         try {
             for (int i = 2; i < args.length; i++) {
                 array.append(iconv.iconv(args[i]));
             }
         } finally {
             iconv.close();
         }
 
         return array;
     }
     
     /*
     private static IRubyObject convert(String fromEncoding, String toEncoding, RubyString original) 
         throws UnsupportedEncodingException {
         // Get all bytes from PLAIN string pretend they are not encoded in any way.
         byte[] string = original.getBytes();
         // Now create a string pretending it is from fromEncoding
         string = new String(string, fromEncoding).getBytes(toEncoding);
         // Finally recode back to PLAIN
         return RubyString.newString(original.getRuntime(), string);
     }
     */
 }
diff --git a/test/testIconv.rb b/test/testIconv.rb
index f7ce4ca172..26d6bfdd28 100644
--- a/test/testIconv.rb
+++ b/test/testIconv.rb
@@ -1,27 +1,23 @@
 require 'test/minirunit'
 test_check "Iconv:"
 
 require 'iconv'
 
-# FIXME: Not implemented yet (though core work done to support it) [JRUBY-309]
-#test_exception(Iconv::IllegalSequence) { p Iconv.conv("ASCII", "UTF-8", "ol\303\251") }
+test_exception(Iconv::IllegalSequence) { p Iconv.conv("ASCII", "UTF-8", "ol\303\251") }
+test_exception(Iconv::IllegalSequence) { p Iconv.conv("UTF-8", "UTF-8", "\xa4") }
+test_exception(Iconv::IllegalSequence) { p Iconv.conv("UTF-8", "UTF-8//IGNORE", "\xa4") }
 
 # FIXME: Java does not support transliteration in core jdk?
 #Iconv.iconv("ASCII//TRANSLIT", "UTF-8", "ol\303\251")
 
 # JRUBY-867, make us ignore transliteration stuff since we can't support it
 
 result = Iconv.conv("ISO-8859-1//TRANSLIT", "UTF-8", "ol\303\251")
 test_equal("ol\351", result)
 result = Iconv.conv("ISO-8859-1//IGNORE", "UTF-8", "ol\303\251")
 test_equal("ol\351", result)
 result = Iconv.conv("ISO-8859-1//IGNORE//TRANSLIT", "UTF-8", "ol\303\251")
 test_equal("ol\351", result)
 
-result = Iconv.conv("ISO-8859-1", "UTF-8//TRANSLIT", "ol\303\251")
-test_equal("ol\351", result)
-result = Iconv.conv("ISO-8859-1", "UTF-8//IGNORE", "ol\303\251")
-test_equal("ol\351", result)
-result = Iconv.conv("ISO-8859-1", "UTF-8//IGNORE//TRANSLIT", "ol\303\251")
-test_equal("ol\351", result)
-
+result = Iconv.conv("UTF-8//IGNORE", "UTF-8", "\xa4")
+test_equal("", result)
\ No newline at end of file
