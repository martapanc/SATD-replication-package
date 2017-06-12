diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 036bc60381..dc30464d53 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1344,1695 +1344,1700 @@ public final class Ruby {
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
 
     public RubyModule getEnumerator() {
         return enumeratorClass;
     }
     void setEnumerator(RubyClass enumeratorClass) {
         this.enumeratorClass = enumeratorClass;
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
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, false, true));
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         return parser.parse(file, in, scope, new ParserConfiguration(0, false, true));
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
         
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         byte[] bytes;
         
         try {
             bytes = content.getBytes(KCode.NONE.getKCode());
         } catch (UnsupportedEncodingException e) {
             bytes = content.getBytes();
         }
 
         return parser.parse(file, new ByteArrayInputStream(bytes), scope, 
                 new ParserConfiguration(lineNumber, extraPositionInformation, false));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         return parser.parse(file, content, scope, new ParserConfiguration(lineNumber, false));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content, scope, 
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
         IRubyObject self = null;
         if (wrap) {
             self = TopSelfFactory.createTopSelf(this);
         } else {
             self = getTopSelf();
         }
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
                     traceFunc.call(context, new IRubyObject[] {
                         newString(EVENT_NAMES[event]), // event name
                         newString(file), // filename
                         newFixnum(line + (event == RUBY_EVENT_RETURN ? 2 : 1)), // line numbers should be 1-based
                         name != null ? newSymbol(name) : getNil(),
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
                     finalIter.next().finalize();
                     finalIter.remove();
                 }
             }
         }
 
         synchronized (internalFinalizersMutex) {
             if (internalFinalizers != null) {
                 for (Iterator<Finalizable> finalIter = new ArrayList<Finalizable>(
                         internalFinalizers.keySet()).iterator(); finalIter.hasNext();) {
                     finalIter.next().finalize();
                     finalIter.remove();
                 }
             }
         }
 
         getThreadService().disposeCurrentThread();
 
+        getBeanManager().unregisterCompiler();
+        getBeanManager().unregisterConfig();
+        getBeanManager().unregisterClassCache();
+        getBeanManager().unregisterMethodCache();
+
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
 
     public RubyArray newArray(IRubyObject[] objects) {
         return RubyArray.newArray(this, objects);
     }
     
     public RubyArray newArrayNoCopy(IRubyObject[] objects) {
         return RubyArray.newArrayNoCopy(this, objects);
     }
     
     public RubyArray newArrayNoCopyLight(IRubyObject[] objects) {
         return RubyArray.newArrayNoCopyLight(this, objects);
     }
     
     public RubyArray newArray(List<IRubyObject> list) {
         return RubyArray.newArray(this, list);
     }
 
     public RubyArray newArray(int size) {
         return RubyArray.newArray(this, size);
     }
 
     public RubyBoolean newBoolean(boolean value) {
         return RubyBoolean.newBoolean(this, value);
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
         return RubyBinding.newBinding(this);
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
         assert internedName == internedName.intern() : internedName + " is not interned";
 
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
 
     public RaiseException newErrnoEISDirError() {
         return newRaiseException(getErrno().fastGetClass("EISDIR"), "Is a directory");
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
         if (printWhenVerbose && origException != null && this.getVerbose().isTrue()) {
             origException.printStackTrace(getErrorStream());
         }
         return new RaiseException(new RubyNameError(
                 this, getNameError(), message, name), true);
     }
 
     public RaiseException newLocalJumpError(String reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
     }
 
     public RaiseException newRedoLocalJumpError() {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), "unexpected redo", "redo", getNil()), true);
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getLoadError(), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(getTypeError(), "can't modify frozen " + objectType);
     }
 
     public RaiseException newSystemStackError(String message) {
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
 
     /**
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         RaiseException re = new RaiseException(this, exceptionClass, message, true);
         return re;
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
 
     public Map<Integer, WeakReference<ChannelDescriptor>> getDescriptors() {
         return descriptors;
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
 
     private final CacheMap cacheMap;
     private final ThreadService threadService;
     private Hashtable<Object, Object> runtimeInformation;
     
     private POSIX posix;
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
     private Map<Integer, WeakReference<ChannelDescriptor>> descriptors = new ConcurrentHashMap<Integer, WeakReference<ChannelDescriptor>>();
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
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     public final RubyFixnum[] fixnumCache = new RubyFixnum[256];
 
     private IRubyObject verbose;
     private IRubyObject debug;
     
     private RubyThreadGroup defaultThreadGroup;
 
     /**
      * All the core classes we keep hard references to. These are here largely
      * so that if someone redefines String or Array we won't start blowing up
      * creating strings and arrays internally. They also provide much faster
      * access than going through normal hash lookup on the Object class.
      */
     private RubyClass
             objectClass, moduleClass, classClass, nilClass, trueClass,
             falseClass, numericClass, floatClass, integerClass, fixnumClass,
             complexClass, rationalClass, enumeratorClass,
             arrayClass, hashClass, rangeClass, stringClass, symbolClass,
             procClass, bindingClass, methodClass, unboundMethodClass,
             matchDataClass, regexpClass, timeClass, bignumClass, dirClass,
             fileClass, fileStatClass, ioClass, threadClass, threadGroupClass,
             continuationClass, structClass, tmsStruct, passwdStruct,
             groupStruct, procStatusClass, exceptionClass, runtimeError, ioError,
             scriptError, nameError, nameErrorMessage, noMethodError, signalException,
             rangeError, dummyClass, systemExit, localJumpError, nativeException,
             systemCallError, fatal, interrupt, typeError, argumentError, indexError,
             syntaxError, standardError, loadError, notImplementedError, securityError, noMemoryError,
             regexpError, eofError, threadError, concurrencyError, systemStackError, zeroDivisionError, floatDomainError;
 
     /**
      * All the core modules we keep direct references to, for quick access and
      * to ensure they remain available.
      */
     private RubyModule
             kernelModule, comparableModule, enumerableModule, mathModule,
             marshalModule, etcModule, fileTestModule, gcModule,
             objectSpaceModule, processModule, procUIDModule, procGIDModule,
             procSysModule, precisionModule, errnoModule;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
     // former java.lang.System concepts now internalized for MVM
     private String currentDirectory;
 
     private long startTime = System.currentTimeMillis();
 
     private RubyInstanceConfig config;
 
     private InputStream in;
     private PrintStream out;
     private PrintStream err;
 
     // Java support
     private JavaSupport javaSupport;
     private JRubyClassLoader jrubyClassLoader;
     
     // Management/monitoring
     private BeanManager beanManager;
     
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
     private Object objectToYamlMethod;
 
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
 }
diff --git a/src/org/jruby/compiler/JITCompiler.java b/src/org/jruby/compiler/JITCompiler.java
index 6bfcd883ad..7078b0e181 100644
--- a/src/org/jruby/compiler/JITCompiler.java
+++ b/src/org/jruby/compiler/JITCompiler.java
@@ -1,334 +1,336 @@
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
  * Copyright (C) 2006-2008 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2008 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.compiler;
 
+import java.lang.ref.SoftReference;
+
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicLong;
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.util.SexpMaker;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.util.ClassCache;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JavaNameMangler;
 
 public class JITCompiler implements JITCompilerMBean {
     public static final boolean USE_CACHE = true;
     
-    private Ruby ruby;
+    private SoftReference<Ruby> ruby;
     
     private AtomicLong compiledCount = new AtomicLong(0);
     private AtomicLong successCount = new AtomicLong(0);
     private AtomicLong failCount = new AtomicLong(0);
     private AtomicLong abandonCount = new AtomicLong(0);
     private AtomicLong compileTime = new AtomicLong(0);
     private AtomicLong averageCompileTime = new AtomicLong(0);
     private AtomicLong codeSize = new AtomicLong(0);
     private AtomicLong averageCodeSize = new AtomicLong(0);
     private AtomicLong largestCodeSize = new AtomicLong(0);
     
     public JITCompiler(Ruby ruby) {
-        this.ruby = ruby;
+        this.ruby = new SoftReference<Ruby>(ruby);
         
         ruby.getBeanManager().register(this);
     }
     
     public void tryJIT(final DefaultMethod method, final ThreadContext context, final String name) {
         if (context.getRuntime().getInstanceConfig().getCompileMode().shouldJIT()) {
             jitIsEnabled(method, context, name);
         }
     }
     
     @Deprecated
     public void runJIT(final DefaultMethod method, final ThreadContext context, final String name) {
         // This method has JITed already or has been abandoned. Bail out.
         if (method.getCallCount() < 0) {
             return;
         } else {
             jitIsEnabled(method, context, name);
         }
     }
     
     private void jitIsEnabled(final DefaultMethod method, final ThreadContext context, final String name) {
-        RubyInstanceConfig instanceConfig = ruby.getInstanceConfig();
+        RubyInstanceConfig instanceConfig = ruby.get().getInstanceConfig();
         int callCount = method.incrementCallCount();
         
         if (callCount >= instanceConfig.getJitThreshold()) {
             jitThresholdReached(method, instanceConfig, context, name);
         }
     }
     
     private void jitThresholdReached(final DefaultMethod method, RubyInstanceConfig instanceConfig, final ThreadContext context, final String name) {
         try {
             // The cache is full. Abandon JIT for this method and bail out.
             ClassCache classCache = instanceConfig.getClassCache();
             if (classCache.isFull()) {
                 abandonCount.incrementAndGet();
                 method.setCallCount(-1);
                 return;
             }
 
             // Check if the method has been explicitly excluded
             String moduleName = method.getImplementationClass().getName();
             if (instanceConfig.getExcludedMethods().size() > 0 &&
                     (instanceConfig.getExcludedMethods().contains(moduleName) ||
                     instanceConfig.getExcludedMethods().contains(moduleName+"#"+name) ||
                     instanceConfig.getExcludedMethods().contains(name))) {
                 method.setCallCount(-1);
                 return;
             }
 
             JITClassGenerator generator = new JITClassGenerator(name, method, context);
 
             String key = SexpMaker.create(name, method.getArgsNode(), method.getBodyNode());
 
             Class<Script> sourceClass = (Class<Script>)instanceConfig.getClassCache().cacheClassByKey(key, generator);
 
             if (sourceClass == null) {
                 // class could not be found nor generated; give up on JIT and bail out
                 failCount.incrementAndGet();
                 method.setCallCount(-1);
                 return;
             }
 
             // successfully got back a jitted method
             successCount.incrementAndGet();
 
             // finally, grab the script
             Script jitCompiledScript = sourceClass.newInstance();
 
             // add to the jitted methods set
-            Set<Script> jittedMethods = ruby.getJittedMethods();
+            Set<Script> jittedMethods = ruby.get().getJittedMethods();
             jittedMethods.add(jitCompiledScript);
 
             // logEvery n methods based on configuration
             if (instanceConfig.getJitLogEvery() > 0) {
                 int methodCount = jittedMethods.size();
                 if (methodCount % instanceConfig.getJitLogEvery() == 0) {
                     log(method, name, "live compiled methods: " + methodCount);
                 }
             }
 
             if (instanceConfig.isJitLogging()) log(method, name, "done jitting");
 
             method.setJITCallConfig(generator.callConfig());
             method.setJITCompiledScript(jitCompiledScript);
             method.setCallCount(-1);
         } catch (Throwable t) {
             if (instanceConfig.isJitLoggingVerbose()) log(method, name, "could not compile", t.getMessage());
 
             failCount.incrementAndGet();
             method.setCallCount(-1);
         }
     }
     
     public class JITClassGenerator implements ClassCache.ClassGenerator {
         private StandardASMCompiler asmCompiler;
         private DefaultMethod method;
         private StaticScope staticScope;
         private Node bodyNode;
         private ArgsNode argsNode;
         private CallConfiguration jitCallConfig;
         
         private byte[] bytecode;
         private String name;
         
         public JITClassGenerator(String name, DefaultMethod method, ThreadContext context) {
             this.method = method;
             String packageName = "ruby/jit/" + JavaNameMangler.mangleFilenameForClasspath(method.getPosition().getFile());
             String cleanName = packageName + "/" + JavaNameMangler.mangleStringForCleanJavaIdentifier(name);
             this.bodyNode = method.getBodyNode();
             this.argsNode = method.getArgsNode();
             final String filename = calculateFilename(argsNode, bodyNode);
             staticScope = method.getStaticScope();
             asmCompiler = new StandardASMCompiler(cleanName + 
                     method.hashCode() + "_" + context.hashCode(), filename);
         }
         
         @SuppressWarnings("unchecked")
         protected void compile() {
             if (bytecode != null) return;
             
             // Time the compilation
             long start = System.nanoTime();
             
             asmCompiler.startScript(staticScope);
             final ASTCompiler compiler = new ASTCompiler();
 
             CompilerCallback args = new CompilerCallback() {
                 public void call(MethodCompiler context) {
                     compiler.compileArgs(argsNode, context);
                 }
             };
 
             ASTInspector inspector = new ASTInspector();
             // check args first, since body inspection can depend on args
             inspector.inspect(argsNode);
             inspector.inspect(bodyNode);
 
             MethodCompiler methodCompiler;
             if (bodyNode != null) {
                 // we have a body, do a full-on method
                 methodCompiler = asmCompiler.startMethod("__file__", args, staticScope, inspector);
                 compiler.compile(bodyNode, methodCompiler);
             } else {
                 // If we don't have a body, check for required or opt args
                 // if opt args, they could have side effects
                 // if required args, need to raise errors if too few args passed
                 // otherwise, method does nothing, make it a nop
                 if (argsNode != null && (argsNode.getRequiredArgsCount() > 0 || argsNode.getOptionalArgsCount() > 0)) {
                     methodCompiler = asmCompiler.startMethod("__file__", args, staticScope, inspector);
                     methodCompiler.loadNil();
                 } else {
                     methodCompiler = asmCompiler.startMethod("__file__", null, staticScope, inspector);
                     methodCompiler.loadNil();
                     jitCallConfig = CallConfiguration.NO_FRAME_NO_SCOPE;
                 }
             }
             methodCompiler.endMethod();
             asmCompiler.endScript(false, false);
             
             // if we haven't already decided on a do-nothing call
             if (jitCallConfig == null) {
                 // if we're not doing any of the operations that still need
                 // a scope, use the scopeless config
                 if (inspector.hasClosure() || inspector.hasScopeAwareMethods()) {
                     jitCallConfig = CallConfiguration.FRAME_AND_SCOPE;
                 } else {
                     // switch to a slightly faster call config
                     jitCallConfig = CallConfiguration.FRAME_ONLY;
                 }
             }
             
             bytecode = asmCompiler.getClassByteArray();
             name = CodegenUtils.c(asmCompiler.getClassname());
             
-            if (bytecode.length > ruby.getInstanceConfig().getJitMaxSize()) {
+            if (bytecode.length > ruby.get().getInstanceConfig().getJitMaxSize()) {
                 bytecode = null;
                 throw new NotCompilableException(
                         "JITed method size exceeds configured max of " +
-                        ruby.getInstanceConfig().getJitMaxSize());
+                        ruby.get().getInstanceConfig().getJitMaxSize());
             }
             
             compiledCount.incrementAndGet();
             compileTime.addAndGet(System.nanoTime() - start);
             codeSize.addAndGet(bytecode.length);
             averageCompileTime.set(compileTime.get() / compiledCount.get());
             averageCodeSize.set(codeSize.get() / compiledCount.get());
             synchronized (largestCodeSize) {
                 if (largestCodeSize.get() < bytecode.length) {
                     largestCodeSize.set(bytecode.length);
                 }
             }
         }
         
         public byte[] bytecode() {
             compile();
             return bytecode;
         }
 
         public String name() {
             compile();
             return name;
         }
         
         public CallConfiguration callConfig() {
             compile();
             return jitCallConfig;
         }
     }
     
     private static String calculateFilename(ArgsNode argsNode, Node bodyNode) {
         if (bodyNode != null) return bodyNode.getPosition().getFile();
         if (argsNode != null) return argsNode.getPosition().getFile();
         
         return "__eval__";
     }
 
     static void log(DefaultMethod method, String name, String message, String... reason) {
         String className = method.getImplementationClass().getBaseName();
         
         if (className == null) className = "<anon class>";
 
         System.err.print(message + ":" + className + "." + name);
         
         if (reason.length > 0) {
             System.err.print(" because of: \"");
             for (int i = 0; i < reason.length; i++) {
                 System.err.print(reason[i]);
             }
             System.err.print('"');
         }
         
         System.err.println("");
     }
 
     public long getSuccessCount() {
         return successCount.get();
     }
 
     public long getCompileCount() {
         return compiledCount.get();
     }
 
     public long getFailCount() {
         return failCount.get();
     }
 
     public long getCompileTime() {
         return compileTime.get() / 1000;
     }
 
     public long getAbandonCount() {
         return abandonCount.get();
     }
     
     public long getCodeSize() {
         return codeSize.get();
     }
     
     public long getAverageCodeSize() {
         return averageCodeSize.get();
     }
     
     public long getAverageCompileTime() {
         return averageCompileTime.get() / 1000;
     }
     
     public long getLargestCodeSize() {
         return largestCodeSize.get();
     }
 }
diff --git a/src/org/jruby/management/BeanManager.java b/src/org/jruby/management/BeanManager.java
index 6f9571dfaa..de9da59832 100644
--- a/src/org/jruby/management/BeanManager.java
+++ b/src/org/jruby/management/BeanManager.java
@@ -1,61 +1,91 @@
 package org.jruby.management;
 
 import java.lang.management.ManagementFactory;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import javax.management.InstanceAlreadyExistsException;
+import javax.management.InstanceNotFoundException;
 import javax.management.MBeanRegistrationException;
 import javax.management.MBeanServer;
 import javax.management.MalformedObjectNameException;
 import javax.management.NotCompliantMBeanException;
 import javax.management.ObjectName;
 import org.jruby.Ruby;
 import org.jruby.compiler.JITCompilerMBean;
 
 public class BeanManager {
     public final String base;
     
     private final Ruby ruby;
     private final boolean managementEnabled;
     
     public BeanManager(Ruby ruby, boolean managementEnabled) {
         this.ruby = ruby;
         this.managementEnabled = managementEnabled;
         this.base = "org.jruby:type=Runtime,name=" + ruby.hashCode() + ",";
     }
     
     public void register(JITCompilerMBean jitCompiler) {
         if (managementEnabled) register(base + "service=JITCompiler", jitCompiler);
     }
     
     public void register(ConfigMBean config) {
         if (managementEnabled) register(base + "service=Config", config);
     }
     
     public void register(MethodCacheMBean methodCache) {
         if (managementEnabled) register(base + "service=MethodCache", methodCache);
     }
     
     public void register(ClassCacheMBean classCache) {
         if (managementEnabled) register(base + "service=ClassCache", classCache);
     }
-    
+
+    public void unregisterCompiler() {
+        if (managementEnabled) unregister(base + "service=JITCompiler");
+    }
+    public void unregisterConfig() {
+        if (managementEnabled) unregister(base + "service=Config");
+    }
+    public void unregisterClassCache() {
+        if (managementEnabled) unregister(base + "service=ClassCache");
+    }
+    public void unregisterMethodCache() {
+        if (managementEnabled) unregister(base + "service=MethodCache");
+    }
+
     private void register(String name, Object bean) {
         try {
             MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
             
             ObjectName beanName = new ObjectName(name);
             mbs.registerMBean(bean, beanName);
         } catch (InstanceAlreadyExistsException ex) {
             Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
         } catch (MBeanRegistrationException ex) {
             Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
         } catch (NotCompliantMBeanException ex) {
             Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
         } catch (MalformedObjectNameException ex) {
             Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
         } catch (NullPointerException ex) {
             Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
         }
     }
+
+    private void unregister(String name) {
+        try {
+            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
+            
+            ObjectName beanName = new ObjectName(name);
+            mbs.unregisterMBean(beanName);
+        } catch (InstanceNotFoundException ex) {
+        } catch (MBeanRegistrationException ex) {
+            Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
+        } catch (MalformedObjectNameException ex) {
+            Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
+        } catch (NullPointerException ex) {
+            Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
+        }
+    }
 }
diff --git a/src/org/jruby/management/ClassCache.java b/src/org/jruby/management/ClassCache.java
index c0e2aaad90..abe6ff47de 100644
--- a/src/org/jruby/management/ClassCache.java
+++ b/src/org/jruby/management/ClassCache.java
@@ -1,31 +1,33 @@
 package org.jruby.management;
 
+import java.lang.ref.SoftReference;
+
 import org.jruby.Ruby;
 
 public class ClassCache implements ClassCacheMBean {
-    private final Ruby ruby;
+    private final SoftReference<Ruby> ruby;
     
     public ClassCache(Ruby ruby) {
-        this.ruby = ruby;
+        this.ruby = new SoftReference<Ruby>(ruby);
     }
 
     public boolean isFull() {
-        return ruby.getInstanceConfig().getClassCache().isFull();
+        return ruby.get().getInstanceConfig().getClassCache().isFull();
     }
 
     public int getClassLoadCount() {
-        return ruby.getInstanceConfig().getClassCache().getClassLoadCount();
+        return ruby.get().getInstanceConfig().getClassCache().getClassLoadCount();
     }
 
     public int getLiveClassCount() {
-        return ruby.getInstanceConfig().getClassCache().getLiveClassCount();
+        return ruby.get().getInstanceConfig().getClassCache().getLiveClassCount();
     }
 
     public int getClassReuseCount() {
-        return ruby.getInstanceConfig().getClassCache().getClassReuseCount();
+        return ruby.get().getInstanceConfig().getClassCache().getClassReuseCount();
     }
 
     public void flush() {
-        ruby.getInstanceConfig().getClassCache().flush();
+        ruby.get().getInstanceConfig().getClassCache().flush();
     }
 }
diff --git a/src/org/jruby/management/Config.java b/src/org/jruby/management/Config.java
index d54c71d54c..a823d8e988 100644
--- a/src/org/jruby/management/Config.java
+++ b/src/org/jruby/management/Config.java
@@ -1,178 +1,180 @@
 package org.jruby.management;
 
+import java.lang.ref.SoftReference;
+
 import java.util.Arrays;
 
 import org.jruby.Ruby;
 import org.jruby.RubyInstanceConfig;
 
 public class Config implements ConfigMBean {
-    private final Ruby ruby;
+    private final SoftReference<Ruby> ruby;
     
     public Config(Ruby ruby) {
-        this.ruby = ruby;
+        this.ruby = new SoftReference<Ruby>(ruby);
     }
     
     public String getVersionString() {
-        return ruby.getInstanceConfig().getVersionString();
+        return ruby.get().getInstanceConfig().getVersionString();
     }
 
     public String getCopyrightString() {
-        return ruby.getInstanceConfig().getCopyrightString();
+        return ruby.get().getInstanceConfig().getCopyrightString();
     }
 
     public String getCompileMode() {
-        return ruby.getInstanceConfig().getCompileMode().name();
+        return ruby.get().getInstanceConfig().getCompileMode().name();
     }
 
     public boolean isJitLogging() {
-        return ruby.getInstanceConfig().isJitLogging();
+        return ruby.get().getInstanceConfig().isJitLogging();
     }
 
     public boolean isJitLoggingVerbose() {
-        return ruby.getInstanceConfig().isJitLoggingVerbose();
+        return ruby.get().getInstanceConfig().isJitLoggingVerbose();
     }
 
     public int getJitLogEvery() {
-        return ruby.getInstanceConfig().getJitLogEvery();
+        return ruby.get().getInstanceConfig().getJitLogEvery();
     }
 
     public boolean isSamplingEnabled() {
-        return ruby.getInstanceConfig().isSamplingEnabled();
+        return ruby.get().getInstanceConfig().isSamplingEnabled();
     }
 
     public int getJitThreshold() {
-        return ruby.getInstanceConfig().getJitThreshold();
+        return ruby.get().getInstanceConfig().getJitThreshold();
     }
 
     public int getJitMax() {
-        return ruby.getInstanceConfig().getJitMax();
+        return ruby.get().getInstanceConfig().getJitMax();
     }
 
     public int getJitMaxSize() {
-        return ruby.getInstanceConfig().getJitMaxSize();
+        return ruby.get().getInstanceConfig().getJitMaxSize();
     }
 
     public boolean isRunRubyInProcess() {
-        return ruby.getInstanceConfig().isRunRubyInProcess();
+        return ruby.get().getInstanceConfig().isRunRubyInProcess();
     }
 
     public String getCompatVersion() {
-        return ruby.getInstanceConfig().getCompatVersion().name();
+        return ruby.get().getInstanceConfig().getCompatVersion().name();
     }
 
     public String getCurrentDirectory() {
-        return ruby.getInstanceConfig().getCurrentDirectory();
+        return ruby.get().getInstanceConfig().getCurrentDirectory();
     }
 
     public boolean isObjectSpaceEnabled() {
-        return ruby.getInstanceConfig().isObjectSpaceEnabled();
+        return ruby.get().getInstanceConfig().isObjectSpaceEnabled();
     }
 
     public String getEnvironment() {
-        return ruby.getInstanceConfig().getEnvironment().toString();
+        return ruby.get().getInstanceConfig().getEnvironment().toString();
     }
 
     public String getArgv() {
-        return Arrays.deepToString(ruby.getInstanceConfig().getArgv());
+        return Arrays.deepToString(ruby.get().getInstanceConfig().getArgv());
     }
 
     public String getJRubyHome() {
-        return ruby.getInstanceConfig().getJRubyHome();
+        return ruby.get().getInstanceConfig().getJRubyHome();
     }
 
     public String getRequiredLibraries() {
-        return ruby.getInstanceConfig().requiredLibraries().toString();
+        return ruby.get().getInstanceConfig().requiredLibraries().toString();
     }
 
     public String getLoadPaths() {
-        return ruby.getInstanceConfig().loadPaths().toString();
+        return ruby.get().getInstanceConfig().loadPaths().toString();
     }
 
     public String getDisplayedFileName() {
-        return ruby.getInstanceConfig().displayedFileName();
+        return ruby.get().getInstanceConfig().displayedFileName();
     }
 
     public String getScriptFileName() {
-        return ruby.getInstanceConfig().getScriptFileName();
+        return ruby.get().getInstanceConfig().getScriptFileName();
     }
 
     public boolean isBenchmarking() {
-        return ruby.getInstanceConfig().isBenchmarking();
+        return ruby.get().getInstanceConfig().isBenchmarking();
     }
 
     public boolean isAssumeLoop() {
-        return ruby.getInstanceConfig().isAssumeLoop();
+        return ruby.get().getInstanceConfig().isAssumeLoop();
     }
 
     public boolean isAssumePrinting() {
-        return ruby.getInstanceConfig().isAssumePrinting();
+        return ruby.get().getInstanceConfig().isAssumePrinting();
     }
 
     public boolean isProcessLineEnds() {
-        return ruby.getInstanceConfig().isProcessLineEnds();
+        return ruby.get().getInstanceConfig().isProcessLineEnds();
     }
 
     public boolean isSplit() {
-        return ruby.getInstanceConfig().isSplit();
+        return ruby.get().getInstanceConfig().isSplit();
     }
 
     public boolean isVerbose() {
-        return ruby.getInstanceConfig().isVerbose();
+        return ruby.get().getInstanceConfig().isVerbose();
     }
 
     public boolean isDebug() {
-        return ruby.getInstanceConfig().isDebug();
+        return ruby.get().getInstanceConfig().isDebug();
     }
 
     public boolean isYARVEnabled() {
-        return ruby.getInstanceConfig().isYARVEnabled();
+        return ruby.get().getInstanceConfig().isYARVEnabled();
     }
 
     public String getInputFieldSeparator() {
-        return ruby.getInstanceConfig().getInputFieldSeparator();
+        return ruby.get().getInstanceConfig().getInputFieldSeparator();
     }
 
     public boolean isRubiniusEnabled() {
-        return ruby.getInstanceConfig().isRubiniusEnabled();
+        return ruby.get().getInstanceConfig().isRubiniusEnabled();
     }
 
     public boolean isYARVCompileEnabled() {
-        return ruby.getInstanceConfig().isYARVCompileEnabled();
+        return ruby.get().getInstanceConfig().isYARVCompileEnabled();
     }
 
     public String getKCode() {
-        return ruby.getInstanceConfig().getKCode().name();
+        return ruby.get().getInstanceConfig().getKCode().name();
     }
 
     public String getRecordSeparator() {
-        return ruby.getInstanceConfig().getRecordSeparator();
+        return ruby.get().getInstanceConfig().getRecordSeparator();
     }
 
     public int getSafeLevel() {
-        return ruby.getInstanceConfig().getSafeLevel();
+        return ruby.get().getInstanceConfig().getSafeLevel();
     }
 
     public String getOptionGlobals() {
-        return ruby.getInstanceConfig().getOptionGlobals().toString();
+        return ruby.get().getInstanceConfig().getOptionGlobals().toString();
     }
     
     public boolean isManagementEnabled() {
-        return ruby.getInstanceConfig().isManagementEnabled();
+        return ruby.get().getInstanceConfig().isManagementEnabled();
     }
     
     public boolean isFullTraceEnabled() {
         return RubyInstanceConfig.FULL_TRACE_ENABLED;
     }
     
     public boolean isLazyHandlesEnabled() {
         return RubyInstanceConfig.LAZYHANDLES_COMPILE;
     }
     
     public boolean isShowBytecode() {
-        return ruby.getInstanceConfig().isShowBytecode();
+        return ruby.get().getInstanceConfig().isShowBytecode();
     }
     
     public String getExcludedMethods() {
-        return ruby.getInstanceConfig().getExcludedMethods().toString();
+        return ruby.get().getInstanceConfig().getExcludedMethods().toString();
     }
 }
diff --git a/src/org/jruby/management/MethodCache.java b/src/org/jruby/management/MethodCache.java
index 4505aad20a..68303070f0 100644
--- a/src/org/jruby/management/MethodCache.java
+++ b/src/org/jruby/management/MethodCache.java
@@ -1,44 +1,46 @@
 package org.jruby.management;
 
+import java.lang.ref.SoftReference;
+
 import org.jruby.runtime.CacheMap;
 import org.jruby.runtime.CallSite;
 
 public class MethodCache implements MethodCacheMBean {
-    private final CacheMap cacheMap;
+    private final SoftReference<CacheMap> cacheMap;
     
     public MethodCache(CacheMap cacheMap) {
-        this.cacheMap = cacheMap;
+        this.cacheMap = new SoftReference<CacheMap>(cacheMap);
     }
     
     public int getAddCount() {
-        return cacheMap.getAddCount();
+        return cacheMap.get().getAddCount();
     }
     
     public int getRemoveCount() {
-        return cacheMap.getRemoveCount();
+        return cacheMap.get().getRemoveCount();
     }
     
     public int getModuleIncludeCount() {
-        return cacheMap.getModuleIncludeCount();
+        return cacheMap.get().getModuleIncludeCount();
     }
     
     public int getModuleTriggeredRemoveCount() {
-        return cacheMap.getModuleTriggeredRemoveCount();
+        return cacheMap.get().getModuleTriggeredRemoveCount();
     }
     
     public int getFlushCount() {
-        return cacheMap.getFlushCount();
+        return cacheMap.get().getFlushCount();
     }
     
     public int getCallSiteCount() {
         return CallSite.InlineCachingCallSite.totalCallSites;
     }
     
     public int getFailedCallSiteCount() {
         return CallSite.InlineCachingCallSite.failedCallSites;
     }
     
     public void flush() {
-        cacheMap.flush();
+        cacheMap.get().flush();
     }
 }
