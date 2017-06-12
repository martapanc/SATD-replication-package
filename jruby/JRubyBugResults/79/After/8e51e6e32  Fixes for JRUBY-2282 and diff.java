diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 2a0c5c8fca..68a24e6e57 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1775,2001 +1775,2001 @@ public final class Ruby {
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
 
     public RubyClass getEncodingError() {
         return encodingError;
     }
 
     public RubyClass getEncodingCompatibilityError() {
         return encodingCompatibilityError;
     }
 
     public RubyClass getConverterNotFoundError() {
         return converterNotFoundError;
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
         return parser.parse(file, bytes, scope,
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
 
         return parser.parse(file, bytes, scope,
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
         InputStream readStream = in;
         
         try {
             secure(4); /* should alter global state */
 
             context.setFile(filename);
             context.preNodeEval(objectClass, self, filename);
 
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
                 className = "ruby.jit.FILE_" + hash;
 
                 // FIXME: duplicated from ClassCache
                 Class contents;
                 try {
                     contents = jrubyClassLoader.loadClass(className);
                     if (JITCompiler.DEBUG) {
                         System.err.println("found jitted code in classloader: " + className);
                     }
                     script = (Script)contents.newInstance();
                     readStream = new ByteArrayInputStream(buffer);
                 } catch (ClassNotFoundException cnfe) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("no jitted code in classloader for file " + filename + " at class: " + className);
                     }
                 } catch (InstantiationException ie) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 } catch (IllegalAccessException iae) {
                     if (JITCompiler.DEBUG) {
                         System.err.println("jitted code could not be instantiated for file " + filename + " at class: " + className);
                     }
                 }
             } catch (IOException ioe) {
                 // TODO: log something?
             }
 
             // script was not found in cache above, so proceed to compile
             if (script == null) {
                 Node scriptNode = parseFile(readStream, filename, null);
 
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader));
             }
             
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
         if (!RubyInstanceConfig.FULL_TRACE_ENABLED) {
             // without full tracing, many events will not fire
             getWarnings().warn("tracing (e.g. set_trace_func) will not capture all events without --debug flag");
         }
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
         if (context.isEventHooksEnabled()) {
             for (EventHook eventHook : eventHooks) {
                 if (eventHook.isInterestedInEvent(event)) {
                     eventHook.event(context, event, file, line, name, type);
                 }
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
-            synchronized (finalizers) {
+            synchronized (finalizersMutex) {
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
 
     public RaiseException newErrnoEISCONNError() {
         return newRaiseException(getErrno().fastGetClass("EISCONN"), "Socket is already connected");
     }
 
     public RaiseException newErrnoEINPROGRESSError() {
         return newRaiseException(getErrno().fastGetClass("EINPROGRESS"), "Operation now in progress");
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
 
     public RaiseException newErrnoEADDRINUSEError(String message) {
         return newRaiseException(getErrno().fastGetClass("EADDRINUSE"), message);
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
 
     public RaiseException newErrnoEINPROGRESSError(String message) {
         return newRaiseException(getErrno().fastGetClass("EINPROGRESS"), message);
     }
 
     public RaiseException newErrnoEISCONNError(String message) {
         return newRaiseException(getErrno().fastGetClass("EISCONN"), message);
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
 
     public RaiseException newErrnoENOTCONNError(String message) {
         return newRaiseException(getErrno().fastGetClass("ENOTCONN"), message);
     }
 
     public RaiseException newErrnoENOTCONNError() {
         return newRaiseException(getErrno().fastGetClass("ENOTCONN"), "Socket is not connected");
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
 
     public RaiseException newErrnoEADDRNOTAVAILError(String message) {
         return newRaiseException(getErrno().fastGetClass("EADDRNOTAVAIL"), message);
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
 
     public RaiseException newErrnoFromInt(int errno, String message) {
         RubyClass errnoClass = getErrno(errno);
         if (errnoClass != null) {
             return newRaiseException(errnoClass, message);
         } else {
             return newSystemCallError("Unknown Error (" + errno + ") - " + message);
         }
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
     
     public RaiseException newIllegalSequence(String message) {
         return newRaiseException(fastGetClass("Iconv").fastGetClass("IllegalSequence"), message);
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
 
     public RaiseException newConverterNotFoundError(String message) {
         return newRaiseException(getConverterNotFoundError(), message);
     }
 
     public RaiseException newUndefinedConversionError(String message) {
         return newRaiseException(getUndefinedConversionError(), message);
     }
 
     public RaiseException newInvalidByteSequenceError(String message) {
         return newRaiseException(getInvalidByteSequenceError(), message);
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
 
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
         Integer filenoKey = descriptor.getFileno();
         retainedDescriptors.put(filenoKey, descriptor);
     }
 
     public void registerDescriptor(ChannelDescriptor descriptor) {
         registerDescriptor(descriptor,false); // default: don't retain
     }
 
     public void unregisterDescriptor(int aFileno) {
         Integer aFilenoKey = aFileno;
         retainedDescriptors.remove(aFilenoKey);
     }
 
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return retainedDescriptors.get(aFileno);
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
 
     public static interface RecursiveFunction {
         IRubyObject call(IRubyObject obj, boolean recur);
     }
 
     private static class RecursiveError extends Error {
         public RecursiveError(Object tag) {
             this.tag = tag;
         }
         public final Object tag;
         
         @Override
         public synchronized Throwable fillInStackTrace() {
             return this;
         }
     }
 
     private ThreadLocal<Map<String, RubyHash>> recursive = new ThreadLocal<Map<String, RubyHash>>();
     private IRubyObject recursiveListAccess() {
         Map<String, RubyHash> hash = recursive.get();
         String sym = getCurrentContext().getFrameName();
         IRubyObject list = getNil();
         if(hash == null) {
             hash = new HashMap<String, RubyHash>();
             recursive.set(hash);
         } else {
             list = hash.get(sym);
         }
         if(list == null || list.isNil()) {
             list = RubyHash.newHash(this);
             list.setUntrusted(true);
             hash.put(sym, (RubyHash)list);
         }
         return list;
     }
 
     private RubySymbol recursiveKey;
 
     private static class ExecRecursiveParams {
         public ExecRecursiveParams() {}
         public RecursiveFunction func;
         public IRubyObject list;
         public IRubyObject obj;
         public IRubyObject objid;
         public IRubyObject pairid;
     }
 
     private void recursivePush(IRubyObject list, IRubyObject obj, IRubyObject paired_obj) {
         IRubyObject pair_list;
         if(paired_obj == null) {
             ((RubyHash)list).op_aset(getCurrentContext(), obj, getTrue());
         } else if((pair_list = ((RubyHash)list).fastARef(obj)) == null) {
             ((RubyHash)list).op_aset(getCurrentContext(), obj, paired_obj);
         } else {
             if(!(pair_list instanceof RubyHash)) {
                 IRubyObject other_paired_obj = pair_list;
                 pair_list = RubyHash.newHash(this);
                 pair_list.setUntrusted(true);
                 ((RubyHash)pair_list).op_aset(getCurrentContext(), other_paired_obj, getTrue());
                 ((RubyHash)list).op_aset(getCurrentContext(), obj, pair_list);
             }
             ((RubyHash)pair_list).op_aset(getCurrentContext(), paired_obj, getTrue());
         }
     }
 
     private void recursivePop(IRubyObject list, IRubyObject obj, IRubyObject paired_obj) {
         if(paired_obj != null) {
             IRubyObject pair_list = ((RubyHash)list).fastARef(obj);
             if(pair_list == null) {
                 throw newTypeError("invalid inspect_tbl pair_list for " + getCurrentContext().getFrameName());
             }
             if(pair_list instanceof RubyHash) {
                 ((RubyHash)pair_list).delete(getCurrentContext(), paired_obj, Block.NULL_BLOCK);
                 if(!((RubyHash)pair_list).isEmpty()) {
                     return;
                 }
             }
         }
         ((RubyHash)list).delete(getCurrentContext(), obj, Block.NULL_BLOCK);
     }
 
     private boolean recursiveCheck(IRubyObject list, IRubyObject obj_id, IRubyObject paired_obj_id) {
         IRubyObject pair_list = ((RubyHash)list).fastARef(obj_id);
         if(pair_list == null) {
             return false;
         }
         if(paired_obj_id != null) {
             if(!(pair_list instanceof RubyHash)) {
                 if(pair_list != paired_obj_id) {
                     return false;
                 }
             } else {
                 IRubyObject paired_result = ((RubyHash)pair_list).fastARef(paired_obj_id);
                 if(paired_result == null || paired_result.isNil()) {
                     return false;
                 }
             }
         }
         return true;
     }
 
     // exec_recursive_i
     private IRubyObject execRecursiveI(ExecRecursiveParams p) {
         IRubyObject result = null;
         recursivePush(p.list, p.objid, p.pairid);
         try {
             result = p.func.call(p.obj, false);
         } finally {
             recursivePop(p.list, p.objid, p.pairid);
         }
         return result;
     }
 
     // exec_recursive
     private IRubyObject execRecursiveInternal(RecursiveFunction func, IRubyObject obj, IRubyObject pairid, boolean outer) {
         ExecRecursiveParams p = new ExecRecursiveParams();
         p.list = recursiveListAccess();
         p.objid = obj.id();
         boolean outermost = outer && !recursiveCheck(p.list, recursiveKey, null);
         if(recursiveCheck(p.list, p.objid, pairid)) {
             if(outer && !outermost) {
                 throw new RecursiveError(p.list);
             }
             return func.call(obj, true); 
         } else {
             IRubyObject result = null;
             p.func = func;
             p.obj = obj;
             p.pairid = pairid;
 
             if(outermost) {
                 recursivePush(p.list, recursiveKey, null);
                 try {
                     result = execRecursiveI(p);
                 } catch(RecursiveError e) {
                     if(e.tag != p.list) {
                         throw e;
                     } else {
                         result = p.list;
                     }
                 }
                 recursivePop(p.list, recursiveKey, null);
                 if(result == p.list) {
                     result = func.call(obj, true);
                 }
             } else {
                 result = execRecursiveI(p);
             }
 
             return result;
         }
     }
 
     // rb_exec_recursive
     public IRubyObject execRecursive(RecursiveFunction func, IRubyObject obj) {
         return execRecursiveInternal(func, obj, null, false);
     }
 
     // rb_exec_recursive_outer
     public IRubyObject execRecursiveOuter(RecursiveFunction func, IRubyObject obj) {
         return execRecursiveInternal(func, obj, null, true);
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
             complexClass, rationalClass, enumeratorClass, yielderClass,
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
             encodingError, encodingCompatibilityError, converterNotFoundError, undefinedConversionError,
             invalidByteSequenceError, randomClass;
 
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
 
     // The "current line" global variable
     private int currentLine = 0;
 
     private IRubyObject argsFile;
 
     private long startTime = System.currentTimeMillis();
 
     private final RubyInstanceConfig config;
     private final boolean is1_9;
 
     private final InputStream in;
     private final PrintStream out;
     private final PrintStream err;
 
     // Java support
     private JavaSupport javaSupport;
diff --git a/src/org/jruby/util/ReferenceReaper.java b/src/org/jruby/util/ReferenceReaper.java
index be5dcaaf29..78e3e54b69 100644
--- a/src/org/jruby/util/ReferenceReaper.java
+++ b/src/org/jruby/util/ReferenceReaper.java
@@ -1,62 +1,62 @@
 
 package org.jruby.util;
 
 import java.lang.ref.Reference;
 import java.lang.ref.ReferenceQueue;
 
 /**
  * A general purpose reference tracker & reaper utility class.
  */
 public final class ReferenceReaper {
     private final ReferenceQueue referenceQueue = new ReferenceQueue();
     private final Thread reaperThread;
     
     private ReferenceReaper() {
-        reaperThread = new Thread(reaper);
+        reaperThread = new Thread(reaper, "ReferenceReaper");
         reaperThread.setDaemon(true);
         reaperThread.start();
     }
 
     private static final class SingletonHolder {
         private static final ReferenceReaper INSTANCE = new ReferenceReaper();
     }
     public static final ReferenceReaper getInstance() {
         return SingletonHolder.INSTANCE;
     }
 
     public static abstract class Weak<T> extends java.lang.ref.WeakReference<T> implements Runnable {
         public Weak(T referent) {
             super(referent, ReferenceReaper.getInstance().referenceQueue);
         }
     }
 
     public static abstract class Soft<T> extends java.lang.ref.SoftReference<T> implements Runnable {
         public Soft(T referent) {
             super(referent, ReferenceReaper.getInstance().referenceQueue);
         }
     }
 
     public static abstract class Phantom<T> extends java.lang.ref.PhantomReference<T> implements Runnable {
         public Phantom(T referent) {
             super(referent, ReferenceReaper.getInstance().referenceQueue);
         }
     }
 
     private final Runnable reaper = new Runnable() {
 
         public void run() {
             for ( ; ; ) {
                 try {
                     Reference r = referenceQueue.remove();
                     if (r instanceof Runnable) {
                         ((Runnable) r).run();
                     }
                 } catch (InterruptedException ex) {
                     break;
                 } catch (Throwable t) {
                     continue;
                 }
             }
         }
     };
 }
diff --git a/src/org/jruby/util/io/ChannelStream.java b/src/org/jruby/util/io/ChannelStream.java
index c1791a4ec4..7f0f1df294 100644
--- a/src/org/jruby/util/io/ChannelStream.java
+++ b/src/org/jruby/util/io/ChannelStream.java
@@ -1,1600 +1,1608 @@
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
  * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2007 Damian Steer <pldms@mac.com>
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
 package org.jruby.util.io;
 
 import static java.util.logging.Logger.getLogger;
 
 import java.io.EOFException;
 import java.io.FileDescriptor;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.RandomAccessFile;
 import java.nio.ByteBuffer;
 import java.nio.channels.Channel;
 import java.nio.channels.FileChannel;
 import java.nio.channels.IllegalBlockingModeException;
 import java.nio.channels.ReadableByteChannel;
 import java.nio.channels.SelectableChannel;
 
 import org.jruby.Finalizable;
 import org.jruby.Ruby;
 import org.jruby.platform.Platform;
 import org.jruby.util.ByteList;
 import org.jruby.util.JRubyFile;
 
 /**
- * <p>This file implements a seekable IO file.</p>
+ * This file implements a seekable IO file.
  */
 public class ChannelStream implements Stream, Finalizable {
     private final static boolean DEBUG = false;
 
     /**
      * The size of the read/write buffer allocated for this stream.
      *
      * This size has been scaled back from its original 16k because although
      * the larger buffer size results in raw File.open times being rather slow
      * (due to the cost of instantiating a relatively large buffer). We should
      * try to find a happy medium, or potentially pool buffers, or perhaps even
      * choose a value based on platform(??), but for now I am reducing it along
      * with changes for the "large read" patch from JRUBY-2657.
      */
     final static int BUFSIZE = 4 * 1024;
 
     /**
      * The size at which a single read should turn into a chunkier bulk read.
      * Currently, this size is about 4x a normal buffer size.
      *
      * This size was not really arrived at experimentally, and could potentially
      * be increased. However, it seems like a "good size" and we should
      * probably only adjust it if it turns out we would perform better with a
      * larger buffer for large bulk reads.
      */
     private final static int BULK_READ_SIZE = 16 * 1024;
     private final static ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
 
-    private Ruby runtime;
+    private volatile Ruby runtime;
     protected ModeFlags modes;
     protected boolean sync = false;
 
     protected volatile ByteBuffer buffer; // r/w buffer
     protected boolean reading; // are we reading or writing?
     private ChannelDescriptor descriptor;
     private boolean blocking = true;
     protected int ungotc = -1;
     private volatile boolean closedExplicitly = false;
 
     private volatile boolean eof = false;
 
     private ChannelStream(Ruby runtime, ChannelDescriptor descriptor) {
         this.runtime = runtime;
         this.descriptor = descriptor;
         this.modes = descriptor.getOriginalModes();
         buffer = ByteBuffer.allocate(BUFSIZE);
         buffer.flip();
         this.reading = true;
+        runtime.addInternalFinalizer(this);
     }
 
     private ChannelStream(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         this(runtime, descriptor);
         descriptor.checkNewModes(modes);
         this.modes = modes;
     }
 
     public Ruby getRuntime() {
         return runtime;
     }
 
     public void checkReadable() throws IOException {
         if (!modes.isReadable()) throw new IOException("not opened for reading");
     }
 
     public void checkWritable() throws IOException {
         if (!modes.isWritable()) throw new IOException("not opened for writing");
     }
 
     public void checkPermissionsSubsetOf(ModeFlags subsetModes) {
         subsetModes.isSubsetOf(modes);
     }
 
     public ModeFlags getModes() {
     	return modes;
     }
 
     public boolean isSync() {
         return sync;
     }
 
     public void setSync(boolean sync) {
         this.sync = sync;
     }
 
     public void setBinmode() {
         // No-op here, no binmode handling needed.
     }
 
     /**
      * Implement IO#wait as per io/wait in MRI.
      * waits until input available or timed out and returns self, or nil when EOF reached.
      *
      * The default implementation loops while ready returns 0.
      */
     public void waitUntilReady() throws IOException, InterruptedException {
         while (ready() == 0) {
             Thread.sleep(10);
         }
     }
 
     public boolean readDataBuffered() {
         return reading && buffer.hasRemaining();
     }
 
     public boolean writeDataBuffered() {
         return !reading && buffer.position() > 0;
     }
     private final int refillBuffer() throws IOException {
         buffer.clear();
         int n = ((ReadableByteChannel) descriptor.getChannel()).read(buffer);
         buffer.flip();
         return n;
     }
     public synchronized ByteList fgets(ByteList separatorString) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         if (separatorString == null) {
             return readall();
         }
 
         final ByteList separator = (separatorString == PARAGRAPH_DELIMETER) ?
             PARAGRAPH_SEPARATOR : separatorString;
 
         descriptor.checkOpen();
 
         if (feof()) {
             return null;
         }
 
         int c = read();
 
         if (c == -1) {
             return null;
         }
 
         // unread back
         buffer.position(buffer.position() - 1);
 
         ByteList buf = new ByteList(40);
 
         byte first = separator.getUnsafeBytes()[separator.getBegin()];
 
         LineLoop : while (true) {
             ReadLoop: while (true) {
                 byte[] bytes = buffer.array();
                 int offset = buffer.position();
                 int max = buffer.limit();
 
                 // iterate over remainder of buffer until we find a match
                 for (int i = offset; i < max; i++) {
                     c = bytes[i];
                     if (c == first) {
                         // terminate and advance buffer when we find our char
                         buf.append(bytes, offset, i - offset);
                         if (i >= max) {
                             buffer.clear();
                         } else {
                             buffer.position(i + 1);
                         }
                         break ReadLoop;
                     }
                 }
 
                 // no match, append remainder of buffer and continue with next block
                 buf.append(bytes, offset, buffer.remaining());
                 int read = refillBuffer();
                 if (read == -1) break LineLoop;
             }
 
             // found a match above, check if remaining separator characters match, appending as we go
             for (int i = 0; i < separator.getRealSize(); i++) {
                 if (c == -1) {
                     break LineLoop;
                 } else if (c != separator.getUnsafeBytes()[separator.getBegin() + i]) {
                     buf.append(c);
                     continue LineLoop;
                 }
                 buf.append(c);
                 if (i < separator.getRealSize() - 1) {
                     c = read();
                 }
             }
             break;
         }
 
         if (separatorString == PARAGRAPH_DELIMETER) {
             while (c == separator.getUnsafeBytes()[separator.getBegin()]) {
                 c = read();
             }
             ungetc(c);
         }
 
         return buf;
     }
 
     public synchronized int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         descriptor.checkOpen();
 
         int totalRead = 0;
         boolean found = false;
         if (ungotc != -1) {
             dst.append((byte) ungotc);
             found = ungotc == terminator;
             ungotc = -1;
             ++totalRead;
         }
         while (!found) {
             final byte[] bytes = buffer.array();
             final int begin = buffer.arrayOffset() + buffer.position();
             final int end = begin + buffer.remaining();
             int len = 0;
             for (int i = begin; i < end && !found; ++i) {
                 found = bytes[i] == terminator;
                 ++len;
             }
             if (len > 0) {
                 dst.append(buffer, len);
                 totalRead += len;
             }
             if (!found) {
                 int n = refillBuffer();
                 if (n <= 0) {
                     if (n < 0 && totalRead < 1) {
                         return -1;
                     }
                     break;
                 }
             }
         }
         return totalRead;
     }
 
     public synchronized int getline(ByteList dst, byte terminator, long limit) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
         descriptor.checkOpen();
 
         int totalRead = 0;
         boolean found = false;
         if (ungotc != -1) {
             dst.append((byte) ungotc);
             found = ungotc == terminator;
             ungotc = -1;
             limit--;
             ++totalRead;
         }
         while (!found) {
             final byte[] bytes = buffer.array();
             final int begin = buffer.arrayOffset() + buffer.position();
             final int end = begin + buffer.remaining();
             int len = 0;
             for (int i = begin; i < end && limit-- > 0 && !found; ++i) {
                 found = bytes[i] == terminator;
                 ++len;
             }
             if (limit < 1) found = true;
 
             if (len > 0) {
                 dst.append(buffer, len);
                 totalRead += len;
             }
             if (!found) {
                 int n = refillBuffer();
                 if (n <= 0) {
                     if (n < 0 && totalRead < 1) {
                         return -1;
                     }
                     break;
                 }
             }
         }
         return totalRead;
     }
 
     public synchronized ByteList readall() throws IOException, BadDescriptorException {
         final long fileSize = descriptor.isSeekable() && descriptor.getChannel() instanceof FileChannel
                 ? ((FileChannel) descriptor.getChannel()).size() : 0;
         //
         // Check file size - special files in /proc have zero size and need to be
         // handled by the generic read path.
         //
         if (fileSize > 0) {
             ensureRead();
 
             FileChannel channel = (FileChannel)descriptor.getChannel();
             final long left = fileSize - channel.position() + bufferedInputBytesRemaining();
             if (left <= 0) {
                 eof = true;
                 return null;
             }
 
             if (left > Integer.MAX_VALUE) {
                 if (getRuntime() != null) {
                     throw getRuntime().newIOError("File too large");
                 } else {
                     throw new IOException("File too large");
                 }
             }
 
             ByteList result = new ByteList((int) left);
             ByteBuffer buf = ByteBuffer.wrap(result.getUnsafeBytes(),
                     result.begin(), (int) left);
 
             //
             // Copy any buffered data (including ungetc byte)
             //
             copyBufferedBytes(buf);
 
             //
             // Now read unbuffered directly from the file
             //
             while (buf.hasRemaining()) {
                 final int MAX_READ_CHUNK = 1 * 1024 * 1024;
                 //
                 // When reading into a heap buffer, the jvm allocates a temporary
                 // direct ByteBuffer of the requested size.  To avoid allocating
                 // a huge direct buffer when doing ludicrous reads (e.g. 1G or more)
                 // we split the read up into chunks of no more than 1M
                 //
                 ByteBuffer tmp = buf.duplicate();
                 if (tmp.remaining() > MAX_READ_CHUNK) {
                     tmp.limit(tmp.position() + MAX_READ_CHUNK);
                 }
                 int n = channel.read(tmp);
                 if (n <= 0) {
                     break;
                 }
                 buf.position(tmp.position());
             }
             eof = true;
             result.length(buf.position());
             return result;
         } else if (descriptor.isNull()) {
             return new ByteList(0);
         } else {
             checkReadable();
 
             ByteList byteList = new ByteList();
             ByteList read = fread(BUFSIZE);
 
             if (read == null) {
                 eof = true;
                 return byteList;
             }
 
             while (read != null) {
                 byteList.append(read);
                 read = fread(BUFSIZE);
             }
 
             return byteList;
         }
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteBuffer</tt> to place the data in.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(ByteBuffer dst) {
         final int bytesToCopy = dst.remaining();
 
         if (ungotc != -1 && dst.hasRemaining()) {
             dst.put((byte) ungotc);
             ungotc = -1;
         }
 
         if (buffer.hasRemaining() && dst.hasRemaining()) {
 
             if (dst.remaining() >= buffer.remaining()) {
                 //
                 // Copy out any buffered bytes
                 //
                 dst.put(buffer);
 
             } else {
                 //
                 // Need to clamp source (buffer) size to avoid overrun
                 //
                 ByteBuffer tmp = buffer.duplicate();
                 tmp.limit(tmp.position() + dst.remaining());
                 dst.put(tmp);
                 buffer.position(tmp.position());
             }
         }
 
         return bytesToCopy - dst.remaining();
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteBuffer</tt> to place the data in.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(byte[] dst, int off, int len) {
         int bytesCopied = 0;
 
         if (ungotc != -1 && len > 0) {
             dst[off++] = (byte) ungotc;
             ungotc = -1;
             ++bytesCopied;
         }
 
         final int n = Math.min(len - bytesCopied, buffer.remaining());
         buffer.get(dst, off, n);
         bytesCopied += n;
 
         return bytesCopied;
     }
 
     /**
      * Copies bytes from the channel buffer into a destination <tt>ByteBuffer</tt>
      *
      * @param dst A <tt>ByteList</tt> to place the data in.
      * @param len The maximum number of bytes to copy.
      * @return The number of bytes copied.
      */
     private final int copyBufferedBytes(ByteList dst, int len) {
         int bytesCopied = 0;
 
         dst.ensure(Math.min(len, bufferedInputBytesRemaining()));
 
         if (bytesCopied < len && ungotc != -1) {
             ++bytesCopied;
             dst.append((byte) ungotc);
             ungotc = -1;
         }
 
         //
         // Copy out any buffered bytes
         //
         if (bytesCopied < len && buffer.hasRemaining()) {
             int n = Math.min(buffer.remaining(), len - bytesCopied);
             dst.append(buffer, n);
             bytesCopied += n;
         }
 
         return bytesCopied;
     }
 
     /**
      * Returns a count of how many bytes are available in the read buffer
      *
      * @return The number of bytes that can be read without reading the underlying stream.
      */
     private final int bufferedInputBytesRemaining() {
         return reading ? (buffer.remaining() + (ungotc != -1 ? 1 : 0)) : 0;
     }
 
     /**
      * Tests if there are bytes remaining in the read buffer.
      *
      * @return <tt>true</tt> if there are bytes available in the read buffer.
      */
     private final boolean hasBufferedInputBytes() {
         return reading && (buffer.hasRemaining() || ungotc != -1);
     }
 
     /**
      * Returns a count of how many bytes of space is available in the write buffer.
      *
      * @return The number of bytes that can be written to the buffer without flushing
      * to the underlying stream.
      */
     private final int bufferedOutputSpaceRemaining() {
         return !reading ? buffer.remaining() : 0;
     }
 
     /**
      * Tests if there is space available in the write buffer.
      *
      * @return <tt>true</tt> if there are bytes available in the write buffer.
      */
     private final boolean hasBufferedOutputSpace() {
         return !reading && buffer.hasRemaining();
     }
 
-
     /**
-     * <p>Close IO handler resources.</p>
-     * @throws IOException
-     * @throws BadDescriptorException
+     * Closes IO handler resources.
      *
-     * @see org.jruby.util.IOHandler#close()
-     */
-    public synchronized void fclose() throws IOException, BadDescriptorException {
-        closedExplicitly = true;
-        close(false); // not closing from finalize
-    }
-
-    /**
-     * Internal close, to safely work for finalizing.
-     * @param finalizing true if this is in a finalizing context
      * @throws IOException
      * @throws BadDescriptorException
      */
-    private void close(boolean finalizing) throws IOException, BadDescriptorException {
+    public void fclose() throws IOException, BadDescriptorException {
         try {
-            flushWrite();
-
-            descriptor.close();
-            buffer = EMPTY_BUFFER;
-
-            if (DEBUG) getLogger("ChannelStream").info("Descriptor for fileno "
-                    + descriptor.getFileno() + " closed by stream");
+            synchronized (this) {
+                closedExplicitly = true;
+                close(); // not closing from finalize
+            }
         } finally {
             Ruby localRuntime = getRuntime();
-            if (!finalizing && localRuntime != null) localRuntime.removeInternalFinalizer(this);
+
+            // Make sure we remove finalizers while not holding self lock,
+            // otherwise there is a possibility for a deadlock!
+            if (localRuntime != null) localRuntime.removeInternalFinalizer(this);
 
             // clear runtime so it doesn't get stuck in memory (JRUBY-2933)
             runtime = null;
         }
     }
 
     /**
-     * Internal close, to safely work for finalizing.
-     * @param finalizing true if this is in a finalizing context
+     * Internal close.
+     *
      * @throws IOException
      * @throws BadDescriptorException
      */
+    private void close() throws IOException, BadDescriptorException {
+        flushWrite();
+
+        descriptor.close();
+        buffer = EMPTY_BUFFER;
+
+        if (DEBUG) getLogger("ChannelStream").info("Descriptor for fileno "
+                + descriptor.getFileno() + " closed by stream");
+    }
+
+    /**
+     * Internal close, to safely work for finalizing.
+     * Silences possible exceptions.
+     */
     private void closeForFinalize() {
         try {
-            close(true);
+            close();
         } catch (BadDescriptorException ex) {
             // silence
         } catch (IOException ex) {
             // silence
+        } finally {
+            // clear runtime so it doesn't get stuck in memory (JRUBY-2933)
+            runtime = null;
         }
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
-     * @see org.jruby.util.IOHandler#flush()
      */
     public synchronized int fflush() throws IOException, BadDescriptorException {
         checkWritable();
         try {
             flushWrite();
         } catch (EOFException eof) {
             return -1;
         }
         return 0;
     }
 
     /**
      * Flush the write buffer to the channel (if needed)
      * @throws IOException
      */
     private void flushWrite() throws IOException, BadDescriptorException {
         if (reading || !modes.isWritable() || buffer.position() == 0) return; // Don't bother
 
         int len = buffer.position();
         buffer.flip();
         int n = descriptor.write(buffer);
 
         if(n != len) {
             // TODO: check the return value here
         }
         buffer.clear();
     }
 
     /**
      * Flush the write buffer to the channel (if needed)
      * @throws IOException
      */
     private boolean flushWrite(final boolean block) throws IOException, BadDescriptorException {
         if (reading || !modes.isWritable() || buffer.position() == 0) return false; // Don't bother
         int len = buffer.position();
         int nWritten = 0;
         buffer.flip();
 
         // For Sockets, only write as much as will fit.
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     if (oldBlocking != block) {
                         selectableChannel.configureBlocking(block);
                     }
                     nWritten = descriptor.write(buffer);
                 } finally {
                     if (oldBlocking != block) {
                         selectableChannel.configureBlocking(oldBlocking);
                     }
                 }
             }
         } else {
             nWritten = descriptor.write(buffer);
         }
         if (nWritten != len) {
             buffer.compact();
             return false;
         }
         buffer.clear();
         return true;
     }
 
     /**
      * @see org.jruby.util.IOHandler#getInputStream()
      */
     public InputStream newInputStream() {
         InputStream in = descriptor.getBaseInputStream();
         return in == null ? new InputStreamAdapter(this) : in;
     }
 
     /**
      * @see org.jruby.util.IOHandler#getOutputStream()
      */
     public OutputStream newOutputStream() {
         return new OutputStreamAdapter(this);
     }
 
     public void clearerr() {
         eof = false;
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#isEOF()
      */
     public boolean feof() throws IOException, BadDescriptorException {
         checkReadable();
 
         if (eof) {
             return true;
         } else {
             return false;
         }
     }
 
     /**
      * @throws IOException
      * @see org.jruby.util.IOHandler#pos()
      */
     public synchronized long fgetpos() throws IOException, PipeException, InvalidValueException, BadDescriptorException {
         // Correct position for read / write buffering (we could invalidate, but expensive)
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             long pos = fileChannel.position();
             // Adjust for buffered data
             if (reading) {
                 pos -= buffer.remaining();
                 return pos - (pos > 0 && ungotc != -1 ? 1 : 0);
             } else {
                 return pos + buffer.position();
             }
         } else if (descriptor.isNull()) {
             return 0;
         } else {
             throw new PipeException();
         }
     }
 
     /**
      * Implementation of libc "lseek", which seeks on seekable streams, raises
      * EPIPE if the fd is assocated with a pipe, socket, or FIFO, and doesn't
      * do anything for other cases (like stdio).
      *
      * @throws IOException
      * @throws InvalidValueException
      * @see org.jruby.util.IOHandler#seek(long, int)
      */
     public synchronized void lseek(long offset, int type) throws IOException, InvalidValueException, PipeException, BadDescriptorException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             ungotc = -1;
             int adj = 0;
             if (reading) {
                 // for SEEK_CUR, need to adjust for buffered data
                 adj = buffer.remaining();
                 buffer.clear();
                 buffer.flip();
             } else {
                 flushWrite();
             }
             try {
                 switch (type) {
                 case SEEK_SET:
                     fileChannel.position(offset);
                     break;
                 case SEEK_CUR:
                     fileChannel.position(fileChannel.position() - adj + offset);
                     break;
                 case SEEK_END:
                     fileChannel.position(fileChannel.size() + offset);
                     break;
                 }
             } catch (IllegalArgumentException e) {
                 throw new InvalidValueException();
             } catch (IOException ioe) {
                 ioe.printStackTrace();
                 throw ioe;
             }
         } else if (descriptor.getChannel() instanceof SelectableChannel) {
             // TODO: It's perhaps just a coincidence that all the channels for
             // which we should raise are instanceof SelectableChannel, since
             // stdio is not...so this bothers me slightly. -CON
             throw new PipeException();
         } else {
         }
     }
 
     /**
      * @see org.jruby.util.IOHandler#sync()
      */
     public synchronized void sync() throws IOException, BadDescriptorException {
         flushWrite();
     }
 
     /**
      * Ensure buffer is ready for reading, flushing remaining writes if required
      * @throws IOException
      */
     private void ensureRead() throws IOException, BadDescriptorException {
         if (reading) return;
         flushWrite();
         buffer.clear();
         buffer.flip();
         reading = true;
     }
 
     /**
      * Ensure buffer is ready for reading, flushing remaining writes if required
      * @throws IOException
      */
     private void ensureReadNonBuffered() throws IOException, BadDescriptorException {
         if (reading) {
             if (buffer.hasRemaining()) {
                 Ruby localRuntime = getRuntime();
                 if (localRuntime != null) {
                     throw localRuntime.newIOError("sysread for buffered IO");
                 } else {
                     throw new IOException("sysread for buffered IO");
                 }
             }
         } else {
             // libc flushes writes on any read from the actual file, so we flush here
             flushWrite();
             buffer.clear();
             buffer.flip();
             reading = true;
         }
     }
 
     private void resetForWrite() throws IOException {
         if (descriptor.isSeekable()) {
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (buffer.hasRemaining()) { // we have read ahead, and need to back up
                 fileChannel.position(fileChannel.position() - buffer.remaining());
             }
         }
         // FIXME: Clearing read buffer here...is this appropriate?
         buffer.clear();
         reading = false;
     }
 
     /**
      * Ensure buffer is ready for writing.
      * @throws IOException
      */
     private void ensureWrite() throws IOException {
         if (!reading) return;
         resetForWrite();
     }
 
     public synchronized ByteList read(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureReadNonBuffered();
 
         ByteList byteList = new ByteList(number);
 
         // TODO this should entry into error handling somewhere
         int bytesRead = descriptor.read(number, byteList);
 
         if (bytesRead == -1) {
             eof = true;
         }
 
         return byteList;
     }
 
     private ByteList bufferedRead(int number) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         int resultSize = 0;
 
         // 128K seems to be the minimum at which the stat+seek is faster than reallocation
         final int BULK_THRESHOLD = 128 * 1024;
         if (number >= BULK_THRESHOLD && descriptor.isSeekable() && descriptor.getChannel() instanceof FileChannel) {
             //
             // If it is a file channel, then we can pre-allocate the output buffer
             // to the total size of buffered + remaining bytes in file
             //
             FileChannel fileChannel = (FileChannel) descriptor.getChannel();
             resultSize = (int) Math.min(fileChannel.size() - fileChannel.position() + bufferedInputBytesRemaining(), number);
         } else {
             //
             // Cannot discern the total read length - allocate at least enough for the buffered data
             //
             resultSize = Math.min(bufferedInputBytesRemaining(), number);
         }
 
         ByteList result = new ByteList(resultSize);
         bufferedRead(result, number);
         return result;
     }
 
     private int bufferedRead(ByteList dst, int number) throws IOException, BadDescriptorException {
 
         int bytesRead = 0;
 
         //
         // Copy what is in the buffer, if there is some buffered data
         //
         bytesRead += copyBufferedBytes(dst, number);
 
         boolean done = false;
         //
         // Avoid double-copying for reads that are larger than the buffer size
         //
         while ((number - bytesRead) >= BUFSIZE) {
             //
             // limit each iteration to a max of BULK_READ_SIZE to avoid over-size allocations
             //
             final int bytesToRead = Math.min(BULK_READ_SIZE, number - bytesRead);
             final int n = descriptor.read(bytesToRead, dst);
             if (n == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (n == 0) {
                 done = true;
                 break;
             }
             bytesRead += n;
         }
 
         //
         // Complete the request by filling the read buffer first
         //
         while (!done && bytesRead < number) {
             int read = refillBuffer();
 
             if (read == -1) {
                 eof = true;
                 break;
             } else if (read == 0) {
                 break;
             }
 
             // append what we read into our buffer and allow the loop to continue
             final int len = Math.min(buffer.remaining(), number - bytesRead);
             dst.append(buffer, len);
             bytesRead += len;
         }
 
         if (bytesRead == 0 && number != 0) {
             if (eof) {
                 throw new EOFException();
             }
         }
 
         return bytesRead;
     }
 
     private int bufferedRead(ByteBuffer dst, boolean partial) throws IOException, BadDescriptorException {
         checkReadable();
         ensureRead();
 
         boolean done = false;
         int bytesRead = 0;
 
         //
         // Copy what is in the buffer, if there is some buffered data
         //
         bytesRead += copyBufferedBytes(dst);
 
         //
         // Avoid double-copying for reads that are larger than the buffer size, or
         // the destination is a direct buffer.
         //
         while ((bytesRead < 1 || !partial) && (dst.remaining() >= BUFSIZE || dst.isDirect())) {
             ByteBuffer tmpDst = dst;
             if (!dst.isDirect()) {
                 //
                 // We limit reads to BULK_READ_SIZED chunks to avoid NIO allocating
                 // a huge temporary native buffer, when doing reads into a heap buffer
                 // If the dst buffer is direct, then no need to limit.
                 //
                 int bytesToRead = Math.min(BULK_READ_SIZE, dst.remaining());
                 if (bytesToRead < dst.remaining()) {
                     tmpDst = dst.duplicate();
                     tmpDst.limit(bytesToRead);
                 }
             }
             int n = descriptor.read(tmpDst);
             if (n == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (n == 0) {
                 done = true;
                 break;
             } else {
                 bytesRead += n;
             }
         }
 
         //
         // Complete the request by filling the read buffer first
         //
         while (!done && dst.hasRemaining() && (bytesRead < 1 || !partial)) {
             int read = refillBuffer();
 
             if (read == -1) {
                 eof = true;
                 done = true;
                 break;
             } else if (read == 0) {
                 done = true;
                 break;
             } else {
                 // append what we read into our buffer and allow the loop to continue
                 bytesRead += copyBufferedBytes(dst);
             }
         }
 
         if (eof && bytesRead == 0 && dst.remaining() != 0) {
             throw new EOFException();
         }
 
         return bytesRead;
     }
 
     private int bufferedRead() throws IOException, BadDescriptorException {
         ensureRead();
 
         if (!buffer.hasRemaining()) {
             int len = refillBuffer();
             if (len == -1) {
                 eof = true;
                 return -1;
             } else if (len == 0) {
                 return -1;
             }
         }
         return buffer.get() & 0xFF;
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(ByteList buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
 
         if (buf.length() > buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
 
 
             int n = descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
             if(n != buf.length()) {
                 // TODO: check the return value here
             }
         } else {
             if (buf.length() > buffer.remaining()) flushWrite();
 
             buffer.put(buf.getUnsafeBytes(), buf.begin(), buf.length());
         }
 
         if (isSync()) flushWrite();
 
         return buf.getRealSize();
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(ByteBuffer buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || !buf.hasRemaining()) return 0;
 
         final int nbytes = buf.remaining();
         if (nbytes >= buffer.capacity()) { // Doesn't fit in buffer. Write immediately.
             flushWrite(); // ensure nothing left to write
 
             descriptor.write(buf);
             // TODO: check the return value here
         } else {
             if (nbytes > buffer.remaining()) flushWrite();
 
             buffer.put(buf);
         }
 
         if (isSync()) flushWrite();
 
         return nbytes - buf.remaining();
     }
 
     /**
      * @throws IOException
      * @throws BadDescriptorException
      * @see org.jruby.util.IOHandler#syswrite(String buf)
      */
     private int bufferedWrite(int c) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         if (!buffer.hasRemaining()) flushWrite();
 
         buffer.put((byte) c);
 
         if (isSync()) flushWrite();
 
         return 1;
     }
 
     public synchronized void ftruncate(long newLength) throws IOException,
             BadDescriptorException, InvalidValueException {
         Channel ch = descriptor.getChannel();
         if (!(ch instanceof FileChannel)) {
             throw new InvalidValueException();
         }
         invalidateBuffer();
         FileChannel fileChannel = (FileChannel)ch;
         if (newLength > fileChannel.size()) {
             // truncate can't lengthen files, so we save position, seek/write, and go back
             long position = fileChannel.position();
             int difference = (int)(newLength - fileChannel.size());
 
             fileChannel.position(fileChannel.size());
             // FIXME: This worries me a bit, since it could allocate a lot with a large newLength
             fileChannel.write(ByteBuffer.allocate(difference));
             fileChannel.position(position);
         } else {
             fileChannel.truncate(newLength);
         }
     }
 
     /**
      * Invalidate buffer before a position change has occurred (e.g. seek),
      * flushing writes if required, and correcting file position if reading
      * @throws IOException
      */
     private void invalidateBuffer() throws IOException, BadDescriptorException {
         if (!reading) flushWrite();
         int posOverrun = buffer.remaining(); // how far ahead we are when reading
         buffer.clear();
         if (reading) {
             buffer.flip();
             // if the read buffer is ahead, back up
             FileChannel fileChannel = (FileChannel)descriptor.getChannel();
             if (posOverrun != 0) fileChannel.position(fileChannel.position() - posOverrun);
         }
     }
 
     /**
-     * Ensure close (especially flush) when we're finished with
+     * Ensure close (especially flush) when we're finished with.
      */
     @Override
     public synchronized void finalize() {
         if (closedExplicitly) return;
 
+        if (DEBUG) {
+            getLogger("ChannelStream").info("finalize() for not explicitly closed stream");
+        }
+
         // FIXME: I got a bunch of NPEs when I didn't check for nulls here...HOW?!
         if (descriptor != null && descriptor.isSeekable() && descriptor.isOpen()) {
             closeForFinalize(); // close without removing from finalizers
         }
     }
 
     public int ready() throws IOException {
         if (descriptor.getChannel() instanceof SelectableChannel) {
             int ready_stat = 0;
             java.nio.channels.Selector sel = java.nio.channels.Selector.open();
             SelectableChannel selchan = (SelectableChannel)descriptor.getChannel();
             synchronized (selchan.blockingLock()) {
                 boolean is_block = selchan.isBlocking();
                 try {
                     selchan.configureBlocking(false);
                     selchan.register(sel, java.nio.channels.SelectionKey.OP_READ);
                     ready_stat = sel.selectNow();
                     sel.close();
                 } catch (Throwable ex) {
                     ex.printStackTrace();
                 } finally {
                     if (sel != null) {
                         try {
                             sel.close();
                         } catch (Exception e) {
                         }
                     }
                     selchan.configureBlocking(is_block);
                 }
             }
             return ready_stat;
         } else {
             return newInputStream().available();
         }
     }
 
     public synchronized void fputc(int c) throws IOException, BadDescriptorException {
         bufferedWrite(c);
     }
 
     public int ungetc(int c) {
         if (c == -1) {
             return -1;
         }
 
         // putting a bit back, so we're not at EOF anymore
         eof = false;
 
         // save the ungot
         ungotc = c;
 
         return c;
     }
 
     public synchronized int fgetc() throws IOException, BadDescriptorException {
         if (eof) {
             return -1;
         }
 
         checkReadable();
 
         int c = read();
 
         if (c == -1) {
             eof = true;
             return c;
         }
 
         return c & 0xff;
     }
 
     public synchronized int fwrite(ByteList string) throws IOException, BadDescriptorException {
         return bufferedWrite(string);
     }
 
     public synchronized int write(ByteBuffer buf) throws IOException, BadDescriptorException {
         return bufferedWrite(buf);
     }
 
     public synchronized int writenonblock(ByteList buf) throws IOException, BadDescriptorException {
         checkWritable();
         ensureWrite();
 
         // Ruby ignores empty syswrites
         if (buf == null || buf.length() == 0) return 0;
 
         if (buffer.position() != 0 && !flushWrite(false)) return 0;
 
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     if (oldBlocking) {
                         selectableChannel.configureBlocking(false);
                     }
                     return descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
                 } finally {
                     if (oldBlocking) {
                         selectableChannel.configureBlocking(oldBlocking);
                     }
                 }
             }
         } else {
             return descriptor.write(ByteBuffer.wrap(buf.getUnsafeBytes(), buf.begin(), buf.length()));
         }
     }
 
     public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {
         try {
             if (number == 0) {
                 if (eof) {
                     return null;
                 } else {
                     return new ByteList(0);
                 }
             }
 
             return bufferedRead(number);
         } catch (EOFException e) {
             eof = true;
             return null;
         }
     }
 
     public synchronized ByteList readnonblock(int number) throws IOException, BadDescriptorException, EOFException {
         assert number >= 0;
 
         if (number == 0) {
             return null;
         }
 
         if (descriptor.getChannel() instanceof SelectableChannel) {
             SelectableChannel selectableChannel = (SelectableChannel)descriptor.getChannel();
             synchronized (selectableChannel.blockingLock()) {
                 boolean oldBlocking = selectableChannel.isBlocking();
                 try {
                     selectableChannel.configureBlocking(false);
                     return readpartial(number);
                 } finally {
                     selectableChannel.configureBlocking(oldBlocking);
                 }
             }
         } else if (descriptor.getChannel() instanceof FileChannel) {
             return fread(number);
         } else {
             return null;
         }
     }
 
     public synchronized ByteList readpartial(int number) throws IOException, BadDescriptorException, EOFException {
         assert number >= 0;
 
         if (number == 0) {
             return null;
         }
         if (descriptor.getChannel() instanceof FileChannel) {
             return fread(number);
         }
 
         if (hasBufferedInputBytes()) {
             // already have some bytes buffered, just return those
             return bufferedRead(Math.min(bufferedInputBytesRemaining(), number));
         } else {
             // otherwise, we try an unbuffered read to get whatever's available
             return read(number);
         }
     }
 
     public synchronized int read(ByteBuffer dst) throws IOException, BadDescriptorException, EOFException {
         return read(dst, !(descriptor.getChannel() instanceof FileChannel));
     }
 
     public synchronized int read(ByteBuffer dst, boolean partial) throws IOException, BadDescriptorException, EOFException {
         assert dst.hasRemaining();
 
         return bufferedRead(dst, partial);
     }
 
     public synchronized int read() throws IOException, BadDescriptorException {
         try {
             descriptor.checkOpen();
 
             if (ungotc >= 0) {
                 int c = ungotc;
                 ungotc = -1;
                 return c;
             }
 
             return bufferedRead();
         } catch (EOFException e) {
             eof = true;
             return -1;
         }
     }
 
     public ChannelDescriptor getDescriptor() {
         return descriptor;
     }
 
     public void setBlocking(boolean block) throws IOException {
         if (!(descriptor.getChannel() instanceof SelectableChannel)) {
             return;
         }
         synchronized (((SelectableChannel) descriptor.getChannel()).blockingLock()) {
             blocking = block;
             try {
                 ((SelectableChannel) descriptor.getChannel()).configureBlocking(block);
             } catch (IllegalBlockingModeException e) {
                 // ignore this; select() will set the correct mode when it is finished
             }
         }
     }
 
     public boolean isBlocking() {
         return blocking;
     }
 
     public synchronized void freopen(Ruby runtime, String path, ModeFlags modes) throws DirectoryAsFileException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         // flush first
         flushWrite();
 
         // reset buffer
         buffer.clear();
         if (reading) {
             buffer.flip();
         }
 
         this.modes = modes;
 
         if (descriptor.isOpen()) {
             descriptor.close();
         }
 
         if (path.equals("/dev/null") || path.equalsIgnoreCase("nul:") || path.equalsIgnoreCase("nul")) {
             descriptor = new ChannelDescriptor(new NullChannel(), descriptor.getFileno(), modes, new FileDescriptor());
         } else {
             String cwd = runtime.getCurrentDirectory();
             JRubyFile theFile = JRubyFile.create(cwd,path);
 
             if (theFile.isDirectory() && modes.isWritable()) throw new DirectoryAsFileException();
 
             if (modes.isCreate()) {
                 if (theFile.exists() && modes.isExclusive()) {
                     throw runtime.newErrnoEEXISTError("File exists - " + path);
                 }
                 theFile.createNewFile();
             } else {
                 if (!theFile.exists()) {
                     throw runtime.newErrnoENOENTError("file not found - " + path);
                 }
             }
 
             // We always open this rw since we can only open it r or rw.
             RandomAccessFile file = new RandomAccessFile(theFile, modes.toJavaModeString());
 
             if (modes.isTruncate()) file.setLength(0L);
 
             descriptor = new ChannelDescriptor(file.getChannel(), descriptor.getFileno(), modes, file.getFD());
 
             if (modes.isAppendable()) lseek(0, SEEK_END);
         }
     }
 
     public static final Stream open(Ruby runtime, ChannelDescriptor descriptor) {
         return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor), descriptor.getOriginalModes());
     }
 
     public static Stream fdopen(Ruby runtime, ChannelDescriptor descriptor, ModeFlags modes) throws InvalidValueException {
         return maybeWrapWithLineEndingWrapper(new ChannelStream(runtime, descriptor, modes), modes);
     }
 
     private static Stream maybeWrapWithLineEndingWrapper(Stream stream, ModeFlags modes) {
         if (Platform.IS_WINDOWS && stream.getDescriptor().getChannel() instanceof FileChannel && !modes.isBinary()) {
             return new CRLFStreamWrapper(stream);
         }
         return stream;
     }
 
     public static Stream fopen(Ruby runtime, String path, ModeFlags modes) throws FileNotFoundException, DirectoryAsFileException, FileExistsException, IOException, InvalidValueException, PipeException, BadDescriptorException {
         ChannelDescriptor descriptor = ChannelDescriptor.open(runtime.getCurrentDirectory(), path, modes);
         Stream stream = fdopen(runtime, descriptor, modes);
 
         if (modes.isAppendable()) stream.lseek(0, Stream.SEEK_END);
 
         return stream;
     }
 
     public Channel getChannel() {
         return getDescriptor().getChannel();
     }
 
     private static final class InputStreamAdapter extends java.io.InputStream {
         private final ChannelStream stream;
 
         public InputStreamAdapter(ChannelStream stream) {
             this.stream = stream;
         }
 
         @Override
         public int read() throws IOException {
             synchronized (stream) {
                 // If it can be pulled direct from the buffer, don't go via the slow path
                 if (stream.hasBufferedInputBytes()) {
                     try {
                         return stream.read();
                     } catch (BadDescriptorException ex) {
                         throw new IOException(ex.getMessage());
                     }
                 }
             }
 
             byte[] b = new byte[1];
             return read(b, 0, 1) == 1 ? b[0] : -1;
         }
 
         @Override
         public int read(byte[] bytes, int off, int len) throws IOException {
             if (bytes == null) {
                 throw new NullPointerException("null destination buffer");
             }
             if ((len | off | (off + len) | (bytes.length - (off + len))) < 0) {
                 throw new IndexOutOfBoundsException();
             }
             if (len == 0) {
                 return 0;
             }
 
             try {
                 synchronized(stream) {
                     final int available = stream.bufferedInputBytesRemaining();
                      if (available >= len) {
                         return stream.copyBufferedBytes(bytes, off, len);
                     } else if (stream.getDescriptor().getChannel() instanceof SelectableChannel) {
                         SelectableChannel ch = (SelectableChannel) stream.getDescriptor().getChannel();
                         synchronized (ch.blockingLock()) {
                             boolean oldBlocking = ch.isBlocking();
                             try {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(true);
                                 }
                                 return stream.bufferedRead(ByteBuffer.wrap(bytes, off, len), true);
                             } finally {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(oldBlocking);
                                 }
                             }
                         }
                     } else {
                         return stream.bufferedRead(ByteBuffer.wrap(bytes, off, len), true);
                     }
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             } catch (EOFException ex) {
                 return -1;
             }
         }
 
         @Override
         public int available() throws IOException {
             synchronized (stream) {
                 return !stream.eof ? stream.bufferedInputBytesRemaining() : 0;
             }
         }
 
         @Override
         public void close() throws IOException {
             try {
                 synchronized (stream) {
-                    stream.close(false);
+                    stream.fclose();
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
     }
 
     private static final class OutputStreamAdapter extends java.io.OutputStream {
         private final ChannelStream stream;
 
         public OutputStreamAdapter(ChannelStream stream) {
             this.stream = stream;
         }
 
         @Override
         public void write(int i) throws IOException {
             synchronized (stream) {
                 if (!stream.isSync() && stream.hasBufferedOutputSpace()) {
                     stream.buffer.put((byte) i);
                     return;
                 }
             }
             byte[] b = { (byte) i };
             write(b, 0, 1);
         }
 
         @Override
         public void write(byte[] bytes, int off, int len) throws IOException {
             if (bytes == null) {
                 throw new NullPointerException("null source buffer");
             }
             if ((len | off | (off + len) | (bytes.length - (off + len))) < 0) {
                 throw new IndexOutOfBoundsException();
             }
 
             try {
                 synchronized(stream) {
                     if (!stream.isSync() && stream.bufferedOutputSpaceRemaining() >= len) {
                         stream.buffer.put(bytes, off, len);
 
                     } else if (stream.getDescriptor().getChannel() instanceof SelectableChannel) {
                         SelectableChannel ch = (SelectableChannel) stream.getDescriptor().getChannel();
                         synchronized (ch.blockingLock()) {
                             boolean oldBlocking = ch.isBlocking();
                             try {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(true);
                                 }
                                 stream.bufferedWrite(ByteBuffer.wrap(bytes, off, len));
                             } finally {
                                 if (!oldBlocking) {
                                     ch.configureBlocking(oldBlocking);
                                 }
                             }
                         }
                     } else {
                         stream.bufferedWrite(ByteBuffer.wrap(bytes, off, len));
                     }
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
 
 
         @Override
         public void close() throws IOException {
             try {
                 synchronized (stream) {
-                    stream.close(false);
+                    stream.fclose();
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
 
         @Override
         public void flush() throws IOException {
             try {
                 synchronized (stream) {
                     stream.flushWrite(true);
                 }
             } catch (BadDescriptorException ex) {
                 throw new IOException(ex.getMessage());
             }
         }
     }
 }
