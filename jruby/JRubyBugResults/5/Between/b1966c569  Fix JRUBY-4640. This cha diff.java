diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index eba1e8398a..fcacbac828 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1735,2055 +1735,2063 @@ public final class Ruby {
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
         byte[] bytes = content.getBytes();
         
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, bytes, scope,
                 new ParserConfiguration(getKCode(), lineNumber, false, false, true, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         byte[] bytes = content.getBytes();
 
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
+        tearDown(true);
+    }
+
+    // tearDown(boolean) has been added for embedding API. When an error
+    // occurs in Ruby code, JRuby does system exit abruptly, no chance to
+    // catch exception. This makes debugging really hard. This is why
+    // tearDown(boolean) exists.
+    public void tearDown(boolean systemExit) {
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
             synchronized (finalizersMutex) {
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
 
-        if (status != 0) {
+        if (systemExit && status != 0) {
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
diff --git a/src/org/jruby/embed/ScriptingContainer.java b/src/org/jruby/embed/ScriptingContainer.java
index abf383de91..4d4128ce91 100644
--- a/src/org/jruby/embed/ScriptingContainer.java
+++ b/src/org/jruby/embed/ScriptingContainer.java
@@ -643,1003 +643,1003 @@ public class ScriptingContainer implements EmbedRubyInstanceConfigAdapter {
     public ClassLoader getClassLoader() {
         return provider.getRubyInstanceConfig().getLoader();
     }
 
     /**
      * Changes a class loader to a given loader.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param loader a new class loader to be set.
      */
     public void setClassLoader(ClassLoader loader) {
         provider.getRubyInstanceConfig().setLoader(loader);
     }
 
     /**
      * Returns a Profile currently used. The default value is Profile.DEFAULT,
      * which has the same behavior to Profile.ALL.
      * Profile allows you to define a restricted subset of code to be loaded during
      * the runtime initialization. When you use JRuby in restricted environment
      * such as Google App Engine, Profile is a helpful option.
      *
      * @since JRuby 1.5.0.
      *
      * @return a current profiler.
      */
     public Profile getProfile() {
         return provider.getRubyInstanceConfig().getProfile();
     }
 
     /**
      * Changes a Profile to a given one. The default value is Profile.DEFAULT,
      * which has the same behavior to Profile.ALL.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * Profile allows you to define a restricted subset of code to be loaded during
      * the runtime initialization. When you use JRuby in restricted environment
      * such as Google App Engine, Profile is a helpful option. For example,
      * Profile.NO_FILE_CLASS doesn't load File class.
      *
      * @since JRuby 1.5.0.
      *
      * @param profile a new profiler to be set.
      */
     public void setProfile(Profile profile) {
         provider.getRubyInstanceConfig().setProfile(profile);
     }
 
     /**
      * Returns a LoadServiceCreator currently used.
      *
      * @since JRuby 1.5.0.
      *
      * @return a current LoadServiceCreator.
      */
     public LoadServiceCreator getLoadServiceCreator() {
         return provider.getRubyInstanceConfig().getLoadServiceCreator();
     }
 
     /**
      * Changes a LoadServiceCreator to a given one.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param creator a new LoadServiceCreator
      */
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         provider.getRubyInstanceConfig().setLoadServiceCreator(creator);
     }
 
     /**
      * Returns an arguments' list.
      *
      * @since JRuby 1.5.0.
      *
      * @return an arguments' list.
      */
     public String[] getArgv() {
         return provider.getRubyInstanceConfig().getArgv();
     }
 
     /**
      * Changes values of the arguments' list.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param argv a new arguments' list.
      */
     public void setArgv(String[] argv) {
         provider.getRubyInstanceConfig().setArgv(argv);
     }
 
     /**
      * Returns a script filename to run. The default value is "<script>".
      *
      * @since JRuby 1.5.0.
      *
      * @return a script filename.
      */
     public String getScriptFilename() {
         return provider.getRubyInstanceConfig().getScriptFileName();
     }
 
     /**
      * Changes a script filename to run. The default value is "<script>".
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param filename a new script filename.
      */
     public void setScriptFilename(String filename) {
         provider.getRubyInstanceConfig().setScriptFileName(filename);
     }
 
     /**
      * Returns a record separator. The default value is "\n".
      *
      * @since JRuby 1.5.0.
      *
      * @return a record separator.
      */
     public String getRecordSeparator() {
         return provider.getRubyInstanceConfig().getRecordSeparator();
     }
 
     /**
      * Changes a record separator to a given value. If "0" is given, the record
      * separator goes to "\n\n", "777" goes to "\uFFFF", otherwise, an octal value
      * of the given number.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param separator a new record separator value, "0" or "777"
      */
     public void setRecordSeparator(String separator) {
         provider.getRubyInstanceConfig().setRecordSeparator(separator);
     }
 
     /**
      * Returns a value of KCode currently used. The default value is KCode.NONE.
      *
      * @since JRuby 1.5.0.
      *
      * @return a KCode value.
      */
     public KCode getKCode() {
         return provider.getRubyInstanceConfig().getKCode();
     }
 
     /**
      * Changes a value of KCode to a given value. The default value is KCode.NONE.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param kcode a new KCode value.
      */
     public void setKCode(KCode kcode) {
         provider.getRubyInstanceConfig().setKCode(kcode);
     }
 
     /**
      * Returns the value of n, which means that jitted methods are logged in
      * every n methods. The default value is 0.
      *
      * @since JRuby 1.5.0.
      *
      * @return a value that determines how often jitted methods are logged.
      */
     public int getJitLogEvery() {
         return provider.getRubyInstanceConfig().getJitLogEvery();
     }
 
     /**
      * Changes a value of n, so that jitted methods are logged in every n methods.
      * The default value is 0. This value can be set by the jruby.jit.logEvery System
      * property.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param logEvery a new number of methods.
      */
     public void setJitLogEvery(int logEvery) {
         provider.getRubyInstanceConfig().setJitLogEvery(logEvery);
     }
 
     /**
      * Returns a value of the threshold that determines whether jitted methods'
      * call reached to the limit or not. The default value is -1 when security
      * restriction is applied, or 50 when no security restriction exists.
      *
      * @since JRuby 1.5.0.
      *
      * @return a value of the threshold.
      */
     public int getJitThreshold() {
         return provider.getRubyInstanceConfig().getJitThreshold();
     }
 
     /**
      * Changes a value of the threshold that determines whether jitted methods'
      * call reached to the limit or not. The default value is -1 when security
      * restriction is applied, or 50 when no security restriction exists. This
      * value can be set by jruby.jit.threshold System property.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param threshold a new value of the threshold.
      */
     public void setJitThreshold(int threshold) {
         provider.getRubyInstanceConfig().setJitThreshold(threshold);
     }
 
     /**
      * Returns a value of a max class cache size. The default value is 0 when
      * security restriction is applied, or 4096 when no security restriction exists.
      *
      * @since JRuby 1.5.0.
      *
      * @return a value of a max class cache size.
      */
     public int getJitMax() {
         return provider.getRubyInstanceConfig().getJitMax();
     }
 
     /**
      * Changes a value of a max class cache size. The default value is 0 when
      * security restriction is applied, or 4096 when no security restriction exists.
      * This value can be set by jruby.jit.max System property.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param max a new value of a max class cache size.
      */
     public void setJitMax(int max) {
         provider.getRubyInstanceConfig().setJitMax(max);
     }
 
     /**
      * Returns a value of a max size of the bytecode generated by compiler. The
      * default value is -1 when security restriction is applied, or 10000 when
      * no security restriction exists.
      *
      * @since JRuby 1.5.0.
      *
      * @return a value of a max size of the bytecode.
      */
     public int getJitMaxSize() {
         return provider.getRubyInstanceConfig().getJitMaxSize();
     }
 
     /**
      * Changes a value of a max size of the bytecode generated by compiler. The
      * default value is -1 when security restriction is applied, or 10000 when
      * no security restriction exists. This value can be set by jruby.jit.maxsize
      * System property.
      * Call this before you use put/get, runScriptlet, and parse methods so that
      * initial configurations will work.
      *
      * @since JRuby 1.5.0.
      *
      * @param maxSize a new value of a max size of the bytecode.
      */
     public void setJitMaxSize(int maxSize) {
         provider.getRubyInstanceConfig().setJitMaxSize(maxSize);
     }
 
     /**
      * Returns version information about JRuby and Ruby supported by this platform.
      *
      * @return version information.
      */
     public String getSupportedRubyVersion() {
         return provider.getRubyInstanceConfig().getVersionString().trim();
     }
 
     /**
      * Returns an array of values associated to a key.
      *
      * @param key is a key in a property file
      * @return values associated to the key
      */
     public String[] getProperty(String key) {
         return propertyReader.getProperty(key);
     }
 
     /**
      * Returns a provider instance of {@link LocalContextProvider}. When users 
      * want to configure Ruby runtime, they can do by setting class loading paths,
      * {@link org.jruby.RubyInstanceConfig} or {@link org.jruby.util.ClassCache}
      * to the provider before they get Ruby runtime.
      * 
      * @return a provider of {@link LocalContextProvider}
      */
     public LocalContextProvider getProvider() {
         return provider;
     }
 
     /**
      * Returns a Ruby runtime in one of {@link LocalContextScope}.
      *
      * @deprecated As of JRuby 1.5.0. Use getProvider().getRuntime() method instead.
      *
      * @return Ruby runtime of a specified local context
      */
     @Deprecated
     public Ruby getRuntime() {
         return provider.getRuntime();
     }
 
     /**
      * Returns a variable map in one of {@link LocalContextScope}. Variables
      * in this map is used to share between Java and Ruby. Map keys are Ruby's
      * variable names, thus they must be valid Ruby names.
      * 
      * @return a variable map specific to the current thread
      */
     public BiVariableMap getVarMap() {
         return provider.getVarMap();
     }
 
     /**
      * Returns a attribute map in one of {@link LocalContextScope}. Attributes
      * in this map accept any key value pair, types of which are java.lang.Object.
      * Ruby scripts do not look up this map.
      * 
      * @return an attribute map specific to the current thread
      */
     public Map getAttributeMap() {
         return provider.getAttributeMap();
     }
 
     /**
      * Returns an attribute value associated with the specified key in
      * a attribute map. This is a short cut method of
      * ScriptingContainer#getAttributeMap().get(key).
      * 
      * @param key is the attribute key
      * @return value is a value associated to the specified key
      */
     public Object getAttribute(Object key) {
         return provider.getAttributeMap().get(key);
     }
 
     /**
      * Associates the specified value with the specified key in a
      * attribute map. If the map previously contained a mapping for the key,
      * the old value is replaced. This is a short cut method of
      * ScriptingContainer#getAttributeMap().put(key, value).
      * 
      * @param key is a key that the specified value is to be associated with
      * @param value is a value to be associated with the specified key
      * @return the previous value associated with key, or null if there was no mapping for key. 
      */
     public Object setAttribute(Object key, Object value) {
         return provider.getAttributeMap().put(key, value);
     }
 
     /**
      * Removes the specified value with the specified key in a
      * attribute map. If the map previously contained a mapping for the key,
      * the old value is returned. This is a short cut method of
      * ScriptingContainer#getAttributeMap().remove(key).
      *
      * @param key is a key that the specified value is to be removed from
      * @return the previous value associated with key, or null if there was no mapping for key.
      */
     public Object removeAttribute(Object key) {
         return provider.getAttributeMap().remove(key);
     }
 
     /**
      * Returns a value to which the specified key is mapped in a
      * variable map, or null if this map contains no mapping for the key. The key
      * must be a valid Ruby variable name. This is a short cut method of
      * ScriptingContainer#getVarMap().get(key).
      * 
      * @param key is a key whose associated value is to be returned
      * @return a value to which the specified key is mapped, or null if this
      *         map contains no mapping for the key
      */
     public Object get(String key) {
         return provider.getVarMap().get(key);
     }
 
     /**
      * Associates the specified value with the specified key in a
      * variable map. If the map previously contained a mapping for the key,
      * the old value is replaced. The key must be a valid Ruby variable name.
      * This is a short cut method of ScriptingContainer#getVarMap().put(key, value).
      * 
      * @param key is a key that the specified value is to be associated with
      * @param value is a value to be associated with the specified key
      * @param lines are line numbers to be parsed from. Only the first argument is used for parsing.
      *        This field is optional. When no line number is specified, 0 is applied to.
      * @return a previous value associated with a key, or null if there was
      *         no mapping for this key.
      */
     public Object put(String key, Object value) {
         return provider.getVarMap().put(key, value);
     }
 
     /**
      * Removes the specified Ruby variable with the specified variable name in a
      * variable map. If the map previously contained a mapping for the key,
      * the old value is returned. The key must be a valid Ruby variable name.
      * This is a short cut method of ScriptingContainer#getVarMap().remove(key).
      *
      * @param key is a key that the specified value is to be associated with
      * @return a previous value associated with a key, or null if there was
      *         no mapping for this key.
      */
     public Object remove(String key) {
         return provider.getVarMap().remove(key);
     }
 
     /**
      * Removes all of the mappings from this map.
      * The map will be empty after this call returns. Ruby variables are also
      * removed from Ruby instance. However, Ruby instance keep having global variable
      * names with null value.
      * This is a short cut method of ScriptingContainer#getVarMap().clear().
      */
     public void clear() {
         provider.getVarMap().clear();
     }
 
     /**
      * Parses a script and return an object which can be run(). This allows
      * the script to be parsed once and evaluated many times.
      * 
      * @param script is a Ruby script to be parsed
      * @param lines are linenumbers to display for parse errors and backtraces.
      *        This field is optional. Only the first argument is used for parsing.
      *        When no line number is specified, 0 is applied to.
      * @return an object which can be run
      */
     public EmbedEvalUnit parse(String script, int... lines) {
         return runtimeAdapter.parse(script, lines);
     }
 
     /**
      * Parses a script given by a reader and return an object which can be run().
      * This allows the script to be parsed once and evaluated many times.
      * 
      * @param reader is used to read a script from
      * @param filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @param lines are linenumbers to display for parse errors and backtraces.
      *        This field is optional. Only the first argument is used for parsing.
      *        When no line number is specified, 0 is applied to.
      * @return an object which can be run
      */
     public EmbedEvalUnit parse(Reader reader, String filename, int... lines) {
         return runtimeAdapter.parse(reader, filename, lines);
     }
 
     /**
      * Parses a script read from a specified path and return an object which can be run().
      * This allows the script to be parsed once and evaluated many times.
      * 
      * @param type is one of the types {@link PathType} defines
      * @param filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @param lines are linenumbers to display for parse errors and backtraces.
      *        This field is optional. Only the first argument is used for parsing.
      *        When no line number is specified, 0 is applied to.
      * @return an object which can be run
      */
     public EmbedEvalUnit parse(PathType type, String filename, int... lines) {
         return runtimeAdapter.parse(type, filename, lines);
     }
 
     /**
      * Parses a script given by a input stream and return an object which can be run().
      * This allows the script to be parsed once and evaluated many times.
      * 
      * @param istream is an input stream to get a script from
      * @param filename filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @param lines are linenumbers to display for parse errors and backtraces.
      *        This field is optional. Only the first argument is used for parsing.
      *        When no line number is specified, 0 is applied to.
      * @return an object which can be run
      */
     public EmbedEvalUnit parse(InputStream istream, String filename, int... lines) {
         return runtimeAdapter.parse(istream, filename, lines);
     }
 
     /**
      * Evaluates a script under the current scope (perhaps the top-level
      * scope) and returns a result only if a script returns a value.
      * Right after the parsing, the script is evaluated once.
      *
      * @param script is a Ruby script to get run
      * @return an evaluated result converted to a Java object
      */
     public Object runScriptlet(String script) {
         EmbedEvalUnit unit = parse(script);
         return runUnit(unit);
     }
 
     private Object runUnit(EmbedEvalUnit unit) {
         if (unit == null) {
             return null;
         }
         IRubyObject ret = unit.run();
         return JavaEmbedUtils.rubyToJava(ret);
     }
 
     /**
      * Evaluates a script read from a reader under the current scope
      * (perhaps the top-level scope) and returns a result only if a script
      * returns a value. Right after the parsing, the script is evaluated once.
      * 
      * @param reader is used to read a script from
      * @param filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @return an evaluated result converted to a Java object
      */
     public Object runScriptlet(Reader reader, String filename) {
         EmbedEvalUnit unit = parse(reader, filename);
         return runUnit(unit);
     }
 
     /**
      * Evaluates a script read from a input stream under the current scope
      * (perhaps the top-level scope) and returns a result only if a script
      * returns a value. Right after the parsing, the script is evaluated once.
      *
      * @param istream is used to input a script from
      * @param filename is used as in information, for example, appears in a stack trace
      *        of an exception
      * @return an evaluated result converted to a Java object
      */
     public Object runScriptlet(InputStream istream, String filename) {
         EmbedEvalUnit unit = parse(istream, filename);
         return runUnit(unit);
     }
 
     /**
      * Reads a script file from specified path and evaluates it under the current
      * scope (perhaps the top-level scope) and returns a result only if a script
      * returns a value. Right after the parsing, the script is evaluated once.
      * 
      * @param type is one of the types {@link PathType} defines
      * @param filename is used to read the script from and an information
      * @return an evaluated result converted to a Java object
      */
     public Object runScriptlet(PathType type, String filename) {
         EmbedEvalUnit unit = parse(type, filename);
         return runUnit(unit);
     }
 
     /**
      * Returns an instance of {@link EmbedRubyRuntimeAdapter} for embedders to parse
      * scripts.
      * 
      * @return an instance of {@link EmbedRubyRuntimeAdapter}.
      */
     public EmbedRubyRuntimeAdapter newRuntimeAdapter() {
         return runtimeAdapter;
     }
 
     /**
      * Returns an instance of {@link EmbedRubyObjectAdapter} for embedders to invoke
      * methods defined by Ruby. The script must be evaluated prior to a method call.
      * <pre>Example
      *         # calendar.rb
      *         require 'date'
      *         class Calendar
      *           def initialize;@today = DateTime.now;end
      *           def next_year;@today.year + 1;end
      *         end
      *         Calendar.new
      *
      *
      *         ScriptingContainer container = new ScriptingContainer();
      *         String filename =  "ruby/calendar.rb";
      *         Object receiver = instance.runScriptlet(PathType.CLASSPATH, filename);
      *         EmbedRubyObjectAdapter adapter = instance.newObjectAdapter();
      *         Integer result =
      *             (Integer) adapter.callMethod(receiver, "next_year", Integer.class);
      *         System.out.println("next year: " + result);
      *         System.out.println(instance.get("@today"));
      *
      * Outputs:
      *     next year: 2010
      *     2009-05-19T17:46:44-04:00</pre>
      * 
      * @return an instance of {@link EmbedRubyObjectAdapter}
      */
     public EmbedRubyObjectAdapter newObjectAdapter() {
         return objectAdapter;
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method does not have any argument.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public Object callMethod(Object receiver, String methodName, Object... args) {
         return objectAdapter.callMethod(receiver, methodName, args);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method does not have any argument.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public Object callMethod(Object receiver, String methodName, Block block, Object... args) {
         return objectAdapter.callMethod(receiver, methodName, block, args);
     }
     
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method does not have any argument.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Class<T> returnType) {
         return objectAdapter.callMethod(receiver, methodName, returnType);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have only one argument.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param singleArg is an method argument
      * @param returnType returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Object singleArg, Class<T> returnType) {
         return objectAdapter.callMethod(receiver, methodName, singleArg, returnType);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have multiple arguments.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments
      * @param returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Object[] args, Class<T> returnType) {
         return objectAdapter.callMethod(receiver, methodName, args, returnType);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have multiple arguments, one of which is a block.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments except a block
      * @param block is a block to be executed in this method
      * @param returnType is the type we want it to convert to
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Object[] args, Block block, Class<T> returnType) {
         return objectAdapter.callMethod(receiver, methodName, args, block, returnType);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method does not have any argument, and users want to inject Ruby's local
      * variables' values from Java.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param returnType is the type we want it to convert to
      * @param unit is parsed unit
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Class<T> returnType, EmbedEvalUnit unit) {
         return objectAdapter.callMethod(receiver, methodName, returnType, unit);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have multiple arguments, and users want to inject Ruby's local
      * variables' values from Java.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments
      * @param returnType is the type we want it to convert to
      * @param unit is parsed unit
      * @return an instance of requested Java type
      */
     public <T> T callMethod(Object receiver, String methodName, Object[] args, Class<T> returnType, EmbedEvalUnit unit) {
         return objectAdapter.callMethod(receiver, methodName, args, returnType, unit);
     }
 
     /**
      * Executes a method defined in Ruby script. This method is used when a Ruby
      * method have multiple arguments, one of which is a block, and users want to
      * inject Ruby's local variables' values from Java.
      *
      * @param receiver is an instance that will receive this method call
      * @param methodName is a method name to be called
      * @param args is an array of method arguments except a block
      * @param block is a block to be executed in this method
      * @param returnType is the type we want it to convert to
      * @param unit is parsed unit
      * @return is the type we want it to convert to
      */
     public <T> T callMethod(Object receiver, String methodName, Object[] args, Block block, Class<T> returnType, EmbedEvalUnit unit) {
         return objectAdapter.callMethod(receiver, methodName, args, block, returnType, unit);
     }
 
     /**
      *
      * @param receiver is an instance that will receive this method call
      * @param args is an array of method arguments
      * @param returnType is the type we want it to convert to
      * @return is the type we want it to convert to
      */
     public <T> T callSuper(Object receiver, Object[] args, Class<T> returnType) {
         return objectAdapter.callSuper(receiver, args, returnType);
     }
 
     /**
      *
      * @param receiver is an instance that will receive this method call
      * @param args is an array of method arguments except a block
      * @param block is a block to be executed in this method
      * @param returnType is the type we want it to convert to
      * @return is the type we want it to convert to
      */
     public <T> T callSuper(Object receiver, Object[] args, Block block, Class<T> returnType) {
         return objectAdapter.callSuper(receiver, args, block, returnType);
     }
 
     /**
      * Returns an instance of a requested interface type. An implementation of
      * the requested interface is done by a Ruby script, which has been evaluated
      * before getting the instance.
      * <pre>Example
      * Interface
      *     //QuadraticFormula.java
      *     package org.jruby.embed;
      *     import java.util.List;
      *     public interface QuadraticFormula {
      *         List solve(int a, int b, int c) throws Exception;
      *     }
      *
      * Implementation
      *     #quadratic_formula.rb
      *     def solve(a, b, c)
      *       v = b ** 2 - 4 * a * c
      *       if v < 0: raise RangeError end
      *       s0 = ((-1)*b - Math.sqrt(v))/(2*a)
      *       s1 = ((-1)*b + Math.sqrt(v))/(2*a)
      *       return s0, s1
      *     end
      *
      * Usage
      *     ScriptingcContainer container = new ScriptingContaier();
      *     String filename = "ruby/quadratic_formula_class.rb";
      *     Object receiver = container.runScriptlet(PathType.CLASSPATH, filename);
      *     QuadraticFormula qf = container.getInstance(receiver, QuadraticFormula.class);
      *     try {
      *          List<Double> solutions = qf.solve(1, -2, -13);
      *          printSolutions(solutions);
      *          solutions = qf.solve(1, -2, 13);
      *          for (double s : solutions) {
      *              System.out.print(s + ", ");
      *          }
      *     } catch (Exception e) {
      *          e.printStackTrace();
      *     }
      *
      * Output
      *     -2.7416573867739413, 4.741657386773941, 
      * </pre>
      *
      * 
      * @param receiver is an instance that implements the interface
      * @param clazz is a requested interface
      * @return an instance of a requested interface type
      */
     public <T> T getInstance(Object receiver, Class<T> clazz) {
         return interfaceAdapter.getInstance(receiver, clazz);
     }
 
     /**
      * Replaces a standard input by a specified reader
      * 
      * @param reader is a reader to be set
      */
     public void setReader(Reader reader) {
         if (reader == null) {
             return;
         }
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.READER)) {
             Reader old = (Reader) map.get(AttributeName.READER);
             if (old == reader) {
                 return;
             }
         }
         map.put(AttributeName.READER, reader);
         InputStream istream = new ReaderInputStream(reader);
         Ruby runtime = provider.getRuntime();
         RubyIO io = new RubyIO(runtime, istream);
         io.getOpenFile().getMainStream().setSync(true);
         runtime.defineVariable(new InputGlobalVariable(runtime, "$stdin", io));
         runtime.getObject().getConstantMapForWrite().put("STDIN", io);
     }
 
     /**
      * Returns a reader set in an attribute map.
      *
      * @return a reader in an attribute map
      */
     public Reader getReader() {
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.READER)) {
             return (Reader) getAttributeMap().get(AttributeName.READER);
         }
         return null;
     }
 
     /**
      * Returns an input stream that Ruby runtime has. The stream is set when
      * Ruby runtime is initialized.
      *
      * @deprecated As of JRuby 1.5.0, replaced by getInput().
      * 
      * @return an input stream that Ruby runtime has.
      */
     @Deprecated
     public InputStream getIn() {
         return getInput();
     }
 
     /**
      * Replaces a standard output by a specified writer.
      *
      * @param writer is a writer to be set
      */
     public void setWriter(Writer writer) {
         if (writer == null) {
             return;
         }
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.WRITER)) {
             Writer old = (Writer) map.get(AttributeName.WRITER);
             if (old == writer) {
                 return;
             }
         }
         map.put(AttributeName.WRITER, writer);
         PrintStream pstream = new PrintStream(new WriterOutputStream(writer));
         setOutputStream(pstream);
     }
 
     private void setOutputStream(PrintStream pstream) {
         if (pstream == null) {
             return;
         }
         Ruby runtime = provider.getRuntime();
         RubyIO io = new RubyIO(runtime, pstream);
         io.getOpenFile().getMainStream().setSync(true);
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stdout", io));
         runtime.getObject().getConstantMapForWrite().put("STDOUT", io);
         runtime.getGlobalVariables().alias("$>", "$stdout");
         runtime.getGlobalVariables().alias("$defout", "$stdout");
     }
 
     public void resetWriter() {
         PrintStream pstream = provider.getRubyInstanceConfig().getOutput();
         setOutputStream(pstream);
     }
 
     /**
      * Returns a writer set in an attribute map.
      * 
      * @return a writer in a attribute map
      */
     public Writer getWriter() {
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.WRITER)) {
             return (Writer) getAttributeMap().get(AttributeName.WRITER);
         }
         return null;
     }
 
     /**
      * Returns an output stream that Ruby runtime has. The stream is set when
      * Ruby runtime is initialized.
      *
      * @deprecated As of JRuby 1.5.0, replaced by getOutput().
      * 
      * @return an output stream that Ruby runtime has
      */
     @Deprecated
     public PrintStream getOut() {
         return getOutput();
     }
 
     /**
      * Replaces a standard error by a specified writer.
      * 
      * @param errorWriter is a writer to be set
      */
     public void setErrorWriter(Writer errorWriter) {
         if (errorWriter == null) {
             return;
         }
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.ERROR_WRITER)) {
             Writer old = (Writer) map.get(AttributeName.ERROR_WRITER);
             if (old == errorWriter) {
                 return;
             }
         }
         map.put(AttributeName.ERROR_WRITER, errorWriter);
         PrintStream pstream = new PrintStream(new WriterOutputStream(errorWriter));
         setErrorStream(pstream);
     }
 
     private void setErrorStream(PrintStream error) {
         if (error == null) {
             return;
         }
         Ruby runtime = provider.getRuntime();
         RubyIO io = new RubyIO(runtime, error);
         io.getOpenFile().getMainStream().setSync(true);
         runtime.defineVariable(new OutputGlobalVariable(runtime, "$stderr", io));
         runtime.getObject().getConstantMapForWrite().put("STDERR", io);
         runtime.getGlobalVariables().alias("$deferr", "$stderr");
     }
 
     public void resetErrorWriter() {
         PrintStream error = provider.getRubyInstanceConfig().getError();
         setErrorStream(error);
     }
 
     /**
      * Returns an error writer set in an attribute map.
      *
      * @return an error writer in a attribute map
      */
     public Writer getErrorWriter() {
         Map map = getAttributeMap();
         if (map.containsKey(AttributeName.ERROR_WRITER)) {
             return (Writer) getAttributeMap().get(AttributeName.ERROR_WRITER);
         }
         return null;
     }
 
     /**
      * Returns an error output stream that Ruby runtime has. The stream is set when
      * Ruby runtime is initialized.
      *
      * @deprecated As of JRuby 1.5.0, Replaced by getError()
      * 
      * @return an error output stream that Ruby runtime has
      */
     @Deprecated
     public PrintStream getErr() {
         return getError();
     }
 
     public void terminate() {
-        getProvider().getRuntime().tearDown();
+        getProvider().getRuntime().tearDown(false);
     }
 }
\ No newline at end of file
