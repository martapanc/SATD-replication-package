diff --git a/spec/tags/ruby/core/kernel/eval_tags.txt b/spec/tags/ruby/core/kernel/eval_tags.txt
deleted file mode 100644
index e8f2f08138..0000000000
--- a/spec/tags/ruby/core/kernel/eval_tags.txt
+++ /dev/null
@@ -1 +0,0 @@
-fails(JRUBY-2278):Kernel#eval should include file and line information in syntax error
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index f3b7fef7f9..511ec8f0cf 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1323,1987 +1323,1987 @@ public final class Ruby {
         if (config.getCompatVersion() == CompatVersion.RUBY1_9) {
             getLoadService().require("builtin/prelude.rb");
             getLoadService().require("builtin/core_ext/symbol");
             getLoadService().require("enumerator");
         }
     }
 
     private void addLazyBuiltin(String name, String shortName, String className) {
         addBuiltinIfAllowed(name, new LateLoadingLibrary(shortName, className, getJRubyClassLoader()));
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
 
     public Object getObjectToYamlMethod() {
         return objectToYamlMethod;
     }
 
     void setObjectToYamlMethod(Object otym) {
         this.objectToYamlMethod = otym;
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
 
     public RubyClass getGenerator() {
         return generatorClass;
     }
     void setGenerator(RubyClass generatorClass) {
         this.generatorClass = generatorClass;
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
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, true, config.getCompatVersion()));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, in, scope, new ParserConfiguration(getKCode(), 0, false, true, config.getCompatVersion()));
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
                 new ParserConfiguration(getKCode(), lineNumber, false, config.getCompatVersion()));
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
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, config.getCompatVersion()));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(getKCode(), lineNumber, false, config.getCompatVersion()));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, 
                 new ParserConfiguration(getKCode(), lineNumber, extraPositionInformation, false, config.getCompatVersion()));
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
                 
-                RubyBinding binding = RubyBinding.newBinding(Ruby.this);
+                RubyBinding binding = RubyBinding.newBinding(Ruby.this, context.currentBinding());
 
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
-        return RubyBinding.newBinding(this);
+        return RubyBinding.newBinding(this, getCurrentContext().currentBinding());
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
         return newRaiseException(getTypeError(), "can't modify frozen " + objectType);
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
 
     public void registerDescriptor(ChannelDescriptor descriptor) {
         cleanDescriptors();
         
         int fileno = descriptor.getFileno();
         descriptors.put(fileno, new WeakDescriptorReference(descriptor, descriptorQueue));
     }
 
     public void unregisterDescriptor(int aFileno) {
         cleanDescriptors();
         
         descriptors.remove(new Integer(aFileno));
     }
 
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         cleanDescriptors();
         
         Reference reference = descriptors.get(new Integer(aFileno));
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
 
     private volatile int constantGeneration = 1;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private int stackTraces = 0;
 
     private ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
     private Map<Integer, WeakDescriptorReference> descriptors = new ConcurrentHashMap<Integer, WeakDescriptorReference>();
     private ReferenceQueue<ChannelDescriptor> descriptorQueue = new ReferenceQueue<ChannelDescriptor>();
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
     public final RubyFixnum[] fixnumCache = new RubyFixnum[256];
 
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
     private Object objectToYamlMethod;
 
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
 }
diff --git a/src/org/jruby/RubyBinding.java b/src/org/jruby/RubyBinding.java
index 98e0b83ef9..1c56804a55 100644
--- a/src/org/jruby/RubyBinding.java
+++ b/src/org/jruby/RubyBinding.java
@@ -1,157 +1,116 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
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
 
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * @author  jpetersen
  */
 @JRubyClass(name="Binding")
 public class RubyBinding extends RubyObject {
     private Binding binding;
 
     public RubyBinding(Ruby runtime, RubyClass rubyClass, Binding binding) {
         super(runtime, rubyClass);
         
         this.binding = binding;
     }
 
     private RubyBinding(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
     
     private static ObjectAllocator BINDING_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyBinding instance = new RubyBinding(runtime, klass);
             
             return instance;
         }
     };
     
     public static RubyClass createBindingClass(Ruby runtime) {
         RubyClass bindingClass = runtime.defineClass("Binding", runtime.getObject(), BINDING_ALLOCATOR);
         runtime.setBinding(bindingClass);
         
         bindingClass.defineAnnotatedMethods(RubyBinding.class);
+        bindingClass.getSingletonClass().undefineMethod("new");
         
         return bindingClass;
     }
 
     public Binding getBinding() {
         return binding;
     }
 
     // Proc class
-    
+
     public static RubyBinding newBinding(Ruby runtime, Binding binding) {
         return new RubyBinding(runtime, runtime.getBinding(), binding);
     }
 
+    @Deprecated
     public static RubyBinding newBinding(Ruby runtime) {
-        ThreadContext context = runtime.getCurrentContext();
-        
-        // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
-        Frame frame = context.getCurrentFrame();
-        Binding binding = new Binding(frame, context.getImmediateBindingRubyClass(), context.getCurrentScope());
-        
-        return new RubyBinding(runtime, runtime.getBinding(), binding);
+        return newBinding(runtime, runtime.getCurrentContext().currentBinding());
     }
 
-    public static RubyBinding newBinding(Ruby runtime, IRubyObject recv) {
-        ThreadContext context = runtime.getCurrentContext();
-
-        // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
-        Frame frame = context.getCurrentFrame();
-        Binding binding = new Binding(recv, frame, frame.getVisibility(), context.getImmediateBindingRubyClass(), context.getCurrentScope());
-
-        return new RubyBinding(runtime, runtime.getBinding(), binding);
-    }
-
-    /**
-     * Create a binding appropriate for a bare "eval", by using the previous (caller's) frame and current
-     * scope.
-     */
-    public static RubyBinding newBindingForEval(ThreadContext context) {
-        // This requires some explaining.  We use Frame values when executing blocks to fill in 
-        // various values in ThreadContext and EvalState.eval like rubyClass, cref, and self.
-        // Largely, for an eval that is using the logical binding at a place where the eval is 
-        // called we mostly want to use the current frames value for this.  Most importantly, 
-        // we need that self (JRUBY-858) at this point.  We also need to make sure that returns
-        // jump to the right place (which happens to be the previous frame).  Lastly, we do not
-        // want the current frames klazz since that will be the klazz represented of self.  We
-        // want the class right before the eval (well we could use cref class for this too I think).
-        // Once we end up having Frames created earlier I think the logic of stuff like this will
-        // be better since we won't be worried about setting Frame to setup other variables/stacks
-        // but just making sure Frame itself is correct...
-        
-        Frame previousFrame = context.getPreviousFrame();
-        Frame currentFrame = context.getCurrentFrame();
-        currentFrame.setKlazz(previousFrame.getKlazz());
-        
-        // Set jump target to whatever the previousTarget thinks is good.
-//        currentFrame.setJumpTarget(previousFrame.getJumpTarget() != null ? previousFrame.getJumpTarget() : previousFrame);
-        
-        Binding binding = new Binding(previousFrame, context.getEvalBindingRubyClass(), context.getCurrentScope());
-        Ruby runtime = context.getRuntime();
-        
-        return new RubyBinding(runtime, runtime.getBinding(), binding);
+    @Deprecated
+    public static RubyBinding newBinding(Ruby runtime, IRubyObject self) {
+       return newBinding(runtime, runtime.getCurrentContext().currentBinding(self));
     }
     
     @JRubyMethod(name = "initialize", visibility = Visibility.PRIVATE)
     public IRubyObject initialize(ThreadContext context) {
-        // FIXME: We should be cloning, not reusing: frame, scope, dynvars, and potentially iter/block info
-        Frame frame = context.getCurrentFrame();
-        binding = new Binding(frame, context.getImmediateBindingRubyClass(), context.getCurrentScope());
+        binding = context.currentBinding();
         
         return this;
     }
     
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = Visibility.PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject other) {
         RubyBinding otherBinding = (RubyBinding)other;
         
         binding = otherBinding.binding;
         
         return this;
     }
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 624fe51d4f..41ac64da4f 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,1344 +1,1360 @@
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
  * Copyright (C) 2008 Joseph LaFata <joe@quibb.org>
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
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Random;
 
 import static org.jruby.RubyEnumerator.enumeratorize;
 import static org.jruby.anno.FrameField.*;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.javasupport.util.RuntimeHelpers;
+import org.jruby.runtime.Binding;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.IdUtil;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.TypeConverter;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  */
 @JRubyModule(name="Kernel")
 public class RubyKernel {
     public final static Class<?> IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         runtime.setKernel(module);
 
         module.defineAnnotatedMethods(RubyKernel.class);
         module.defineAnnotatedMethods(RubyObject.class);
         
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
         
         module.setFlag(RubyObject.USER7_F, false); //Kernel is the only Module that doesn't need an implementor
 
         return module;
     }
 
     @JRubyMethod(name = "at_exit", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject at_exit(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().pushExitBlock(context.getRuntime().newProc(Block.Type.PROC, block));
     }
 
     @JRubyMethod(name = "autoload?", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject autoload_p(ThreadContext context, final IRubyObject recv, IRubyObject symbol) {
         Ruby runtime = context.getRuntime();
         RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         String name = module.getName() + "::" + symbol.asJavaString();
         
         IAutoloadMethod autoloadMethod = runtime.getLoadService().autoloadFor(name);
         if (autoloadMethod == null) return runtime.getNil();
 
         return runtime.newString(autoloadMethod.file());
     }
 
     @JRubyMethod(name = "autoload", required = 2, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         Ruby runtime = recv.getRuntime(); 
         final LoadService loadService = runtime.getLoadService();
         String nonInternedName = symbol.asJavaString();
         
         if (!IdUtil.isValidConstantName(nonInternedName)) {
             throw runtime.newNameError("autoload must be constant name", nonInternedName);
         }
         
         RubyString fileString = file.convertToString();
         
         if (fileString.isEmpty()) {
             throw runtime.newArgumentError("empty file name");
         }
         
         final String baseName = symbol.asJavaString().intern(); // interned, OK for "fast" methods
         final RubyModule module = recv instanceof RubyModule ? (RubyModule) recv : runtime.getObject();
         String nm = module.getName() + "::" + baseName;
         
         IRubyObject existingValue = module.fastFetchConstant(baseName); 
         if (existingValue != null && existingValue != RubyObject.UNDEF) return runtime.getNil();
         
         module.fastStoreConstant(baseName, RubyObject.UNDEF);
         
         loadService.addAutoload(nm, new IAutoloadMethod() {
             public String file() {
                 return file.toString();
             }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 boolean required = loadService.require(file());
                 
                 // File to be loaded by autoload has already been or is being loaded.
                 if (!required) return null;
                 
                 return module.fastGetConstant(baseName);
             }
         });
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "method_missing", rest = true, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject method_missing(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         if (args.length == 0 || !(args[0] instanceof RubySymbol)) throw runtime.newArgumentError("no id given");
 
         Visibility lastVis = context.getLastVisibility();
         CallType lastCallType = context.getLastCallType();
 
         // create a lightweight thunk
         IRubyObject msg = new RubyNameError.RubyNameErrorMessage(runtime, 
                                                                  recv,
                                                                  args[0],
                                                                  lastVis,
                                                                  lastCallType);
         final IRubyObject[]exArgs;
         final RubyClass exc;
         if (lastCallType != CallType.VARIABLE) {
             exc = runtime.getNoMethodError();
             exArgs = new IRubyObject[]{msg, args[0], RubyArray.newArrayNoCopy(runtime, args, 1)};
         } else {
             exc = runtime.getNameError();
             exArgs = new IRubyObject[]{msg, args[0]};
         }
         
         throw new RaiseException((RubyException)exc.newInstance(context, exArgs, Block.NULL_BLOCK));
     }
 
     @JRubyMethod(name = "open", required = 1, optional = 2, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         String arg = args[0].convertToString().toString();
         Ruby runtime = context.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             return RubyIO.popen(context, runtime.getIO(), new IRubyObject[] {runtime.newString(command)}, block);
         } 
 
         return RubyFile.open(context, runtime.getFile(), args, block);
     }
 
     @JRubyMethod(name = "getc", module = true, visibility = PRIVATE)
     public static IRubyObject getc(ThreadContext context, IRubyObject recv) {
         context.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "getc is obsolete; use STDIN.getc instead", "getc", "STDIN.getc");
         IRubyObject defin = context.getRuntime().getGlobalVariables().get("$stdin");
         return defin.callMethod(context, "getc");
     }
 
     @JRubyMethod(name = "gets", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject gets(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.gets(context, context.getRuntime().getGlobalVariables().get("$<"), args);
     }
 
     @JRubyMethod(name = "abort", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject abort(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if(args.length == 1) {
             context.getRuntime().getGlobalVariables().get("$stderr").callMethod(context,"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     @JRubyMethod(name = "Array", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_array(ThreadContext context, IRubyObject recv, IRubyObject object) {
         IRubyObject value = object.checkArrayType();
 
         if (value.isNil()) {
             if (object.getMetaClass().searchMethod("to_a").getImplementationClass() != context.getRuntime().getKernel()) {
                 value = object.callMethod(context, "to_a");
                 if (!(value instanceof RubyArray)) throw context.getRuntime().newTypeError("`to_a' did not return Array");
                 return value;
             } else {
                 return context.getRuntime().newArray(object);
             }
         }
         return value;
     }
 
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert");
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg);
     }
     @JRubyMethod(name = "Complex", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_complex(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getComplex(), "convert", arg0, arg1);
     }
     
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert");
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg);
     }
     @JRubyMethod(name = "Rational", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject new_rational(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return RuntimeHelpers.invoke(context, context.getRuntime().getRational(), "convert", arg0, arg1);
     }
 
     @JRubyMethod(name = "Float", module = true, visibility = PRIVATE)
     public static RubyFloat new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return (RubyFloat)object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getByteList().realSize == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = (RubyFloat)TypeConverter.convertToType(object, recv.getRuntime().getFloat(), "to_f");
             if (Double.isNaN(rFloat.getDoubleValue())) throw recv.getRuntime().newArgumentError("invalid value for Float()");
             return rFloat;
         }
     }
 
     @JRubyMethod(name = "Integer", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_integer(ThreadContext context, IRubyObject recv, IRubyObject object) {
         if (object instanceof RubyFloat) {
             double val = ((RubyFloat)object).getDoubleValue(); 
             if (val > (double) RubyFixnum.MAX && val < (double) RubyFixnum.MIN) {
                 return RubyNumeric.dbl2num(context.getRuntime(),((RubyFloat)object).getDoubleValue());
             }
         } else if (object instanceof RubyFixnum || object instanceof RubyBignum) {
             return object;
         } else if (object instanceof RubyString) {
             return RubyNumeric.str2inum(context.getRuntime(),(RubyString)object,0,true);
         }
         
         IRubyObject tmp = TypeConverter.convertToType(object, context.getRuntime().getInteger(), "to_int", false);
         if (tmp.isNil()) return object.convertToInteger("to_i");
         return tmp;
     }
 
     @JRubyMethod(name = "String", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject new_string(ThreadContext context, IRubyObject recv, IRubyObject object) {
         return TypeConverter.convertToType(object, context.getRuntime().getString(), "to_s");
     }
 
     @JRubyMethod(name = "p", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject p(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         IRubyObject defout = runtime.getGlobalVariables().get("$>");
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", RubyObject.inspect(context, args[i]));
                 defout.callMethod(context, "write", runtime.newString("\n"));
             }
         }
 
         IRubyObject result = runtime.getNil();
         if (runtime.getInstanceConfig().getCompatVersion() == CompatVersion.RUBY1_9) {
             if (args.length == 1) {
                 result = args[0];
             } else if (args.length > 1) {
                 result = runtime.newArray(args);
             }
         }
 
         if (defout instanceof RubyFile) {
             ((RubyFile)defout).flush();
         }
 
         return result;
     }
 
     /** rb_f_putc
      */
     @JRubyMethod(name = "putc", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject putc(ThreadContext context, IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(context, "putc", ch);
     }
 
     @JRubyMethod(name = "puts", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject puts(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
         
         defout.callMethod(context, "puts", args);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "print", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject print(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
         defout.callMethod(context, "print", args);
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "printf", rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject printf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = context.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "readline", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readline(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(context, recv, args);
 
         if (line.isNil()) throw context.getRuntime().newEOFError();
 
         return line;
     }
 
     @JRubyMethod(name = "readlines", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyArgsFile.readlines(context, context.getRuntime().getGlobalVariables().get("$<"), args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(ThreadContext context, Ruby runtime) {
         IRubyObject line = context.getPreviousFrame().getLastLine();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, args, block);
     }
 
     @JRubyMethod(name = "sub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "sub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE)
     public static IRubyObject sub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).sub_bang(context, arg0, arg1, block);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, args, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "sub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject sub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.sub_bang(context, arg0, arg1, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, args, block);
     }
 
     @JRubyMethod(name = "gsub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, block);
     }
 
     @JRubyMethod(name = "gsub!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         return getLastlineString(context, context.getRuntime()).gsub_bang(context, arg0, arg1, block);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the one or two-arg versions.
      */
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, args, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "gsub", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject gsub(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1, Block block) {
         RubyString str = (RubyString) getLastlineString(context, context.getRuntime()).dup();
 
         if (!str.gsub_bang(context, arg0, arg1, block).isNil()) {
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     @JRubyMethod(name = "chop!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chop_bang(ThreadContext context, IRubyObject recv, Block block) {
         return getLastlineString(context, context.getRuntime()).chop_bang(context);
     }
 
     @JRubyMethod(name = "chop", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chop(ThreadContext context, IRubyObject recv, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
 
         if (str.getByteList().realSize > 0) {
             str = (RubyString) str.dup();
             str.chop_bang(context);
             context.getPreviousFrame().setLastLine(str);
         }
 
         return str;
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the zero or one-arg versions.
      */
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(args);
     }
 
     @JRubyMethod(name = "chomp!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context);
     }
 
     @JRubyMethod(name = "chomp!", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp_bang(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).chomp_bang(context, arg0);
     }
 
     /**
      * Variable-arity version for compatibility. Not bound to Ruby.
      * @deprecated Use the zero or one-arg versions.
      */
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "chomp", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context).isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     @JRubyMethod(name = "chomp", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = LASTLINE)
     public static IRubyObject chomp(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         RubyString str = getLastlineString(context, context.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(context, arg0).isNil()) {
             return str;
         } 
 
         context.getPreviousFrame().setLastLine(dup);
         return dup;
     }
 
     /**
      * Variable arity version for compatibility. Not bound to a Ruby method.
      * 
      * @param context The thread context for the current thread
      * @param recv The receiver of the method (usually a class that has included Kernel)
      * @return
      * @deprecated Use the versions with zero, one, or two args.
      */
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(context, context.getRuntime()).split(context, args);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv) {
         return getLastlineString(context, context.getRuntime()).split(context);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0);
     }
 
     @JRubyMethod(name = "split", frame = true, module = true, visibility = PRIVATE, reads = LASTLINE, writes = {LASTLINE, BACKREF})
     public static IRubyObject split(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
         return getLastlineString(context, context.getRuntime()).split(context, arg0, arg1);
     }
 
     @JRubyMethod(name = "scan", required = 1, frame = true, module = true, visibility = PRIVATE, reads = {LASTLINE, BACKREF}, writes = {LASTLINE, BACKREF})
     public static IRubyObject scan(ThreadContext context, IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(context, context.getRuntime()).scan(context, pattern, block);
     }
 
     @JRubyMethod(name = "select", required = 1, optional = 3, module = true, visibility = PRIVATE)
     public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         return RubyIO.select_static(context, context.getRuntime(), args);
     }
 
     @JRubyMethod(name = "sleep", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject sleep(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         long milliseconds;
 
         if (args.length == 0) {
             // Zero sleeps forever
             milliseconds = 0;
         } else {
             if (!(args[0] instanceof RubyNumeric)) {
                 throw context.getRuntime().newTypeError("can't convert " + args[0].getMetaClass().getName() + "into time interval");
             }
             milliseconds = (long) (args[0].convertToFloat().getDoubleValue() * 1000);
             if (milliseconds < 0) {
                 throw context.getRuntime().newArgumentError("time interval must be positive");
             } else if (milliseconds == 0) {
                 // Explicit zero in MRI returns immediately
                 return context.getRuntime().newFixnum(0);
             }
         }
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = context.getThread();
 
         // Spurious wakeup-loop
         do {
             long loopStartTime = System.currentTimeMillis();
             try {
                 // We break if we know this sleep was explicitly woken up/interrupted
                 if (!rubyThread.sleep(milliseconds)) break;
             } catch (InterruptedException iExcptn) {
             }
             milliseconds -= (System.currentTimeMillis() - loopStartTime);
         } while (milliseconds > 0);
 
         return context.getRuntime().newFixnum(Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     @JRubyMethod(name = "exit", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, false);
         return recv.getRuntime().getNil(); // not reached
     }
 
     @JRubyMethod(name = "exit!", optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         exit(recv.getRuntime(), args, true);
         return recv.getRuntime().getNil(); // not reached
     }
     
     private static void exit(Ruby runtime, IRubyObject[] args, boolean hard) {
         runtime.secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         if (hard) {
             throw new MainExitException(status, true);
         } else {
             throw runtime.newSystemExit(status);
         }
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     @JRubyMethod(name = "global_variables", module = true, visibility = PRIVATE)
     public static RubyArray global_variables(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         RubyArray globalVariables = runtime.newArray();
 
         for (String globalVariableName : runtime.getGlobalVariables().getNames()) {
             globalVariables.append(runtime.newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     @JRubyMethod(name = "local_variables", module = true, visibility = PRIVATE)
     public static RubyArray local_variables(ThreadContext context, IRubyObject recv) {
         final Ruby runtime = context.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         for (String name: context.getCurrentScope().getAllNamesInScope()) {
             if (IdUtil.isLocal(name)) localVariables.append(runtime.newString(name));
         }
 
         return localVariables;
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyBinding binding(ThreadContext context, IRubyObject recv, Block block) {
-        return RubyBinding.newBinding(context.getRuntime(), recv);
+        return RubyBinding.newBinding(context.getRuntime(), context.currentBinding(recv));
     }
 
     @JRubyMethod(name = "binding", module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyBinding binding_1_9(ThreadContext context, IRubyObject recv, Block block) {
-        return RubyBinding.newBinding(context.getRuntime());
+        return RubyBinding.newBinding(context.getRuntime(), context.currentBinding());
     }
 
     @JRubyMethod(name = {"block_given?", "iterator?"}, frame = true, module = true, visibility = PRIVATE)
     public static RubyBoolean block_given_p(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newBoolean(context.getPreviousFrame().getBlock().isGiven());
     }
 
 
     @Deprecated
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         return sprintf(recv.getRuntime().getCurrentContext(), recv, args);
     }
 
     @JRubyMethod(name = {"sprintf", "format"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject sprintf(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw context.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = context.getRuntime().newArrayNoCopy(args);
         newArgs.shift(context);
 
         return str.op_format(context, newArgs);
     }
 
     @JRubyMethod(name = {"raise", "fail"}, optional = 3, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject raise(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Ruby runtime = context.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getRuntimeError(), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.getRuntimeError().newInstance(context, args, block));
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
         
         if (!runtime.fastGetClass("Exception").isInstance(exception)) {
             throw runtime.newTypeError("exception object expected");
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
 
         if (runtime.getDebug().isTrue()) {
             printExceptionSummary(context, runtime, (RubyException) exception);
         }
 
         throw new RaiseException((RubyException) exception);
     }
 
     private static void printExceptionSummary(ThreadContext context, Ruby runtime, RubyException rEx) {
         Frame currentFrame = context.getCurrentFrame();
 
         String msg = String.format("Exception `%s' at %s:%s - %s\n",
                 rEx.getMetaClass(),
                 currentFrame.getFile(), currentFrame.getLine() + 1,
                 rEx.to_s());
 
         IRubyObject errorStream = runtime.getGlobalVariables().get("$stderr");
         errorStream.callMethod(context, "write", runtime.newString(msg));
     }
 
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     @JRubyMethod(name = "require", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         Ruby runtime = recv.getRuntime();
         
         if (runtime.getLoadService().require(name.convertToString().toString())) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     @JRubyMethod(name = "load", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyString file = args[0].convertToString();
         boolean wrap = args.length == 2 ? args[1].isTrue() : false;
 
         runtime.getLoadService().load(file.getByteList().toString(), wrap);
         
         return runtime.getTrue();
     }
 
     @JRubyMethod(name = "eval", required = 1, optional = 3, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject eval(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
-            
         // string to eval
         RubyString src = args[0].convertToString();
         runtime.checkSafeString(src);
-        
-        IRubyObject scope = args.length > 1 && !args[1].isNil() ? args[1] : null;
-        String file;
+
+        boolean bindingGiven = args.length > 1 && !args[1].isNil();
+        Binding binding = bindingGiven ? convertToBinding(args[1]) : context.previousBinding();
         if (args.length > 2) {
-            file = args[2].convertToString().toString();
-        } else if (scope == null) {
-            file = "(eval)";
+            // file given, use it and force it into binding
+            binding.setFile(args[2].convertToString().toString());
         } else {
-            file = null;
+            // file not given
+            if (bindingGiven) {
+                // binding given, use binding's file
+            } else {
+                // no binding given, use (eval)
+                binding.setFile("(eval)");
+            }
         }
-        int line;
         if (args.length > 3) {
-            line = (int) args[3].convertToInteger().getLongValue();
-        } else if (scope == null) {
-            line = 0;
+            // file given, use it and force it into binding
+            binding.setLine((int) args[3].convertToInteger().getLongValue());
         } else {
-            line = -1;
+            // no binding given, use 0 for both
+            binding.setLine(0);
         }
-        if (scope == null) scope = RubyBinding.newBindingForEval(context);
         
-        return ASTInterpreter.evalWithBinding(context, src, scope, file, line);
+        return ASTInterpreter.evalWithBinding(context, src, binding);
+    }
+
+    private static Binding convertToBinding(IRubyObject scope) {
+        if (scope instanceof RubyBinding) {
+            return ((RubyBinding)scope).getBinding().clone();
+        } else {
+            if (scope instanceof RubyProc) {
+                return ((RubyProc) scope).getBlock().getBinding().clone();
+            } else {
+                // bomb out, it's not a binding or a proc
+                throw scope.getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
+            }
+        }
     }
 
     @JRubyMethod(name = "callcc", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject callcc(ThreadContext context, IRubyObject recv, Block block) {
         RubyContinuation continuation = new RubyContinuation(context.getRuntime());
         return continuation.enter(context, block);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static IRubyObject caller(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level(" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "caller", optional = 1, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static IRubyObject caller1_9(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw context.getRuntime().newArgumentError("negative level(" + level + ')');
         }
 
         return context.createCallerBacktrace(context.getRuntime(), level);
     }
 
     @JRubyMethod(name = "catch", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject rbCatch(ThreadContext context, IRubyObject recv, IRubyObject tag, Block block) {
         CatchTarget target = new CatchTarget(tag.asJavaString());
         try {
             context.pushCatch(target);
             return block.yield(context, tag);
         } catch (JumpException.ThrowJump tj) {
             if (tj.getTarget() == target) return (IRubyObject) tj.getValue();
             
             throw tj;
         } finally {
             context.popCatch();
         }
     }
     
     public static class CatchTarget implements JumpTarget {
         private final String tag;
         public CatchTarget(String tag) { this.tag = tag; }
         public String getTag() { return tag; }
     }
 
     @JRubyMethod(name = "throw", required = 1, frame = true, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject rbThrow(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = context.getRuntime();
 
         String tag = args[0].asJavaString();
         CatchTarget[] catches = context.getActiveCatches();
 
         String message = "uncaught throw `" + tag + "'";
 
         // Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i].getTag())) {
                 //Catch active, throw for catch to handle
                 throw new JumpException.ThrowJump(catches[i], args.length > 1 ? args[1] : runtime.getNil());
             }
         }
 
         // No catch active for this throw
         RubyThread currentThread = context.getThread();
         if (currentThread == runtime.getThreadService().getMainThread()) {
             throw runtime.newNameError(message, tag);
         } else {
             throw runtime.newThreadError(message + " in thread 0x" + Integer.toHexString(RubyInteger.fix2int(currentThread.id())));
         }
     }
 
     @JRubyMethod(name = "trap", required = 1, frame = true, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject trap(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         context.getRuntime().getLoadService().require("jsignal");
         return RuntimeHelpers.invoke(context, recv, "__jtrap", args, block);
     }
     
     @JRubyMethod(name = "warn", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject warn(ThreadContext context, IRubyObject recv, IRubyObject message) {
         Ruby runtime = context.getRuntime();
         
         if (runtime.warningsEnabled()) {
             IRubyObject out = runtime.getGlobalVariables().get("$stderr");
             RuntimeHelpers.invoke(context, out, "puts", message);
         }
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "set_trace_func", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject set_trace_func(ThreadContext context, IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             context.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw context.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             context.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     @JRubyMethod(name = "trace_var", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject trace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
         RubyProc proc = null;
         String var = args.length > 1 ? args[0].toString() : null;
         // ignore if it's not a global var
         if (var.charAt(0) != '$') return context.getRuntime().getNil();
         if (args.length == 1) proc = RubyProc.newProc(context.getRuntime(), block, Block.Type.PROC);
         if (args.length == 2) {
             proc = (RubyProc)TypeConverter.convertToType(args[1], context.getRuntime().getProc(), "to_proc", true);
         }
         
         context.getRuntime().getGlobalVariables().setTraceVar(var, proc);
         
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "untrace_var", required = 1, optional = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject untrace_var(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         if (args.length == 0) throw context.getRuntime().newArgumentError(0, 1);
         String var = args.length >= 1 ? args[0].toString() : null;
 
         // ignore if it's not a global var
         if (var.charAt(0) != '$') return context.getRuntime().getNil();
         
         if (args.length > 1) {
             ArrayList<IRubyObject> success = new ArrayList<IRubyObject>();
             for (int i = 1; i < args.length; i++) {
                 if (context.getRuntime().getGlobalVariables().untraceVar(var, args[i])) {
                     success.add(args[i]);
                 }
             }
             return RubyArray.newArray(context.getRuntime(), success);
         } else {
             context.getRuntime().getGlobalVariables().untraceVar(var);
         }
         
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_added", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_added(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_removed", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_removed(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
 
     @JRubyMethod(name = "singleton_method_undefined", required = 1, frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject singleton_method_undefined(ThreadContext context, IRubyObject recv, IRubyObject symbolId, Block block) {
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = {"proc", "lambda"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_8)
     public static RubyProc proc(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @Deprecated
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"lambda"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc lambda(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.LAMBDA, block);
     }
     
     @JRubyMethod(name = {"proc"}, frame = true, module = true, visibility = PRIVATE, compat = CompatVersion.RUBY1_9)
     public static RubyProc proc_1_9(ThreadContext context, IRubyObject recv, Block block) {
         return context.getRuntime().newProc(Block.Type.PROC, block);
     }
 
     @JRubyMethod(name = "loop", frame = true, module = true, visibility = PRIVATE)
     public static IRubyObject loop(ThreadContext context, IRubyObject recv, Block block) {
         IRubyObject nil = context.getRuntime().getNil();
         while (true) {
             block.yield(context, nil);
 
             context.pollThreadEvents();
         }
     }
     
     @JRubyMethod(name = "test", required = 2, optional = 1, module = true, visibility = PRIVATE)
     public static IRubyObject test(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) throw context.getRuntime().newArgumentError("wrong number of arguments");
 
         int cmd;
         if (args[0] instanceof RubyFixnum) {
             cmd = (int)((RubyFixnum) args[0]).getLongValue();
         } else if (args[0] instanceof RubyString &&
                 ((RubyString) args[0]).getByteList().length() > 0) {
             // MRI behavior: use first byte of string value if len > 0
             cmd = ((RubyString) args[0]).getByteList().charAt(0);
         } else {
             cmd = (int) args[0].convertToInteger().getLongValue();
         }
         
         // MRI behavior: raise ArgumentError for 'unknown command' before
         // checking number of args.
         switch(cmd) {
         case 'A': case 'b': case 'c': case 'C': case 'd': case 'e': case 'f': case 'g': case 'G': 
         case 'k': case 'M': case 'l': case 'o': case 'O': case 'p': case 'r': case 'R': case 's':
         case 'S': case 'u': case 'w': case 'W': case 'x': case 'X': case 'z': case '=': case '<':
         case '>': case '-':
             break;
         default:
             throw context.getRuntime().newArgumentError("unknown command ?" + (char) cmd);
         }
 
         // MRI behavior: now check arg count
 
         switch(cmd) {
         case '-': case '=': case '<': case '>':
             if (args.length != 3) throw context.getRuntime().newArgumentError(args.length, 3);
             break;
         default:
             if (args.length != 2) throw context.getRuntime().newArgumentError(args.length, 2);
             break;
         }
         
         switch (cmd) {
         case 'A': // ?A  | Time    | Last access time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).atime();
         case 'b': // ?b  | boolean | True if file1 is a block device
             return RubyFileTest.blockdev_p(recv, args[1]);
         case 'c': // ?c  | boolean | True if file1 is a character device
             return RubyFileTest.chardev_p(recv, args[1]);
         case 'C': // ?C  | Time    | Last change time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).ctime();
         case 'd': // ?d  | boolean | True if file1 exists and is a directory
             return RubyFileTest.directory_p(recv, args[1]);
         case 'e': // ?e  | boolean | True if file1 exists
             return RubyFileTest.exist_p(recv, args[1]);
         case 'f': // ?f  | boolean | True if file1 exists and is a regular file
             return RubyFileTest.file_p(recv, args[1]);
         case 'g': // ?g  | boolean | True if file1 has the \CF{setgid} bit
             return RubyFileTest.setgid_p(recv, args[1]);
         case 'G': // ?G  | boolean | True if file1 exists and has a group ownership equal to the caller's group
             return RubyFileTest.grpowned_p(recv, args[1]);
         case 'k': // ?k  | boolean | True if file1 exists and has the sticky bit set
             return RubyFileTest.sticky_p(recv, args[1]);
         case 'M': // ?M  | Time    | Last modification time for file1
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtime();
         case 'l': // ?l  | boolean | True if file1 exists and is a symbolic link
             return RubyFileTest.symlink_p(recv, args[1]);
         case 'o': // ?o  | boolean | True if file1 exists and is owned by the caller's effective uid
             return RubyFileTest.owned_p(recv, args[1]);
         case 'O': // ?O  | boolean | True if file1 exists and is owned by the caller's real uid 
             return RubyFileTest.rowned_p(recv, args[1]);
         case 'p': // ?p  | boolean | True if file1 exists and is a fifo
             return RubyFileTest.pipe_p(recv, args[1]);
         case 'r': // ?r  | boolean | True if file1 is readable by the effective uid/gid of the caller
             return RubyFileTest.readable_p(recv, args[1]);
         case 'R': // ?R  | boolean | True if file is readable by the real uid/gid of the caller
             // FIXME: Need to implement an readable_real_p in FileTest
             return RubyFileTest.readable_p(recv, args[1]);
         case 's': // ?s  | int/nil | If file1 has nonzero size, return the size, otherwise nil
             return RubyFileTest.size_p(recv, args[1]);
         case 'S': // ?S  | boolean | True if file1 exists and is a socket
             return RubyFileTest.socket_p(recv, args[1]);
         case 'u': // ?u  | boolean | True if file1 has the setuid bit set
             return RubyFileTest.setuid_p(recv, args[1]);
         case 'w': // ?w  | boolean | True if file1 exists and is writable by effective uid/gid
             return RubyFileTest.writable_p(recv, args[1]);
         case 'W': // ?W  | boolean | True if file1 exists and is writable by the real uid/gid
             // FIXME: Need to implement an writable_real_p in FileTest
             return RubyFileTest.writable_p(recv, args[1]);
         case 'x': // ?x  | boolean | True if file1 exists and is executable by the effective uid/gid
             return RubyFileTest.executable_p(recv, args[1]);
         case 'X': // ?X  | boolean | True if file1 exists and is executable by the real uid/gid
             return RubyFileTest.executable_real_p(recv, args[1]);
         case 'z': // ?z  | boolean | True if file1 exists and has a zero length
             return RubyFileTest.zero_p(recv, args[1]);
         case '=': // ?=  | boolean | True if the modification times of file1 and file2 are equal
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeEquals(args[2]);
         case '<': // ?<  | boolean | True if the modification time of file1 is prior to that of file2
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeLessThan(args[2]);
         case '>': // ?>  | boolean | True if the modification time of file1 is after that of file2
             return context.getRuntime().newFileStat(args[1].convertToString().toString(), false).mtimeGreaterThan(args[2]);
         case '-': // ?-  | boolean | True if file1 and file2 are identical
             return RubyFileTest.identical_p(recv, args[1], args[2]);
         default:
             throw new InternalError("unreachable code reached!");
         }
     }
 
     @JRubyMethod(name = "`", required = 1, module = true, visibility = PRIVATE)
     public static IRubyObject backquote(ThreadContext context, IRubyObject recv, IRubyObject aString) {
         Ruby runtime = context.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         RubyString string = aString.convertToString();
         int resultCode = ShellLauncher.runAndWait(runtime, new IRubyObject[] {string}, output);
         
         runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newStringNoCopy(runtime, output.toByteArray());
     }
     
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
 
         // Not sure how well this works, but it works much better than
         // just currentTimeMillis by itself.
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(System.currentTimeMillis() ^
                recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
                runtime.getRandom().nextInt(Math.max(1, Math.abs((int)runtime.getRandomSeed()))));
 
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
     
     @JRubyMethod(name = "srand", module = true, visibility = PRIVATE)
     public static RubyInteger srand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         RubyInteger integerSeed = arg.convertToInteger("to_int");
         Ruby runtime = context.getRuntime();
 
         long oldRandomSeed = runtime.getRandomSeed();
         runtime.setRandomSeed(integerSeed.getLongValue());
 
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv) {
         Ruby runtime = context.getRuntime();
         return RubyFloat.newFloat(runtime, runtime.getRandom().nextDouble());
     }
 
     @JRubyMethod(name = "rand", module = true, visibility = PRIVATE)
     public static RubyNumeric rand(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         Random random = runtime.getRandom();
 
         if (arg instanceof RubyBignum) {
             byte[] bigCeilBytes = ((RubyBignum) arg).getValue().toByteArray();
             BigInteger bigCeil = new BigInteger(bigCeilBytes).abs();
             byte[] randBytes = new byte[bigCeilBytes.length];
             random.nextBytes(randBytes);
             BigInteger result = new BigInteger(randBytes).abs().mod(bigCeil);
             return new RubyBignum(runtime, result); 
         }
 
         RubyInteger integerCeil = (RubyInteger)RubyKernel.new_integer(context, recv, arg); 
         long ceil = Math.abs(integerCeil.getLongValue());
         if (ceil == 0) return RubyFloat.newFloat(runtime, random.nextDouble());
         if (ceil > Integer.MAX_VALUE) return runtime.newFixnum(Math.abs(random.nextLong()) % ceil);
 
         return runtime.newFixnum(random.nextInt((int) ceil));
     }
 
     @JRubyMethod(name = "syscall", required = 1, optional = 9, module = true, visibility = PRIVATE)
     public static IRubyObject syscall(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         throw context.getRuntime().newNotImplementedError("Kernel#syscall is not implemented in JRuby");
     }
 
     @JRubyMethod(name = {"system"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static RubyBoolean system(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode;
         try {
             resultCode = ShellLauncher.runAndWait(runtime, args);
         } catch (Exception e) {
             resultCode = 127;
         }
         runtime.getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     @JRubyMethod(name = {"exec"}, required = 1, rest = true, module = true, visibility = PRIVATE)
     public static IRubyObject exec(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         int resultCode;
         try {
             // TODO: exec should replace the current process.
             // This could be possible with JNA. 
             resultCode = ShellLauncher.execAndWait(runtime, args);
         } catch (Exception e) {
             throw runtime.newErrnoENOENTError("cannot execute");
         }
         
         exit(runtime, new IRubyObject[] {runtime.newFixnum(resultCode)}, true);
 
         // not reached
         return runtime.getNil();
     }
 
     @JRubyMethod(name = "fork", module = true, visibility = PRIVATE)
     public static IRubyObject fork(ThreadContext context, IRubyObject recv, Block block) {
         Ruby runtime = context.getRuntime();
         
         if (!RubyInstanceConfig.FORK_ENABLED) {
             throw runtime.newNotImplementedError("fork is unsafe and disabled by default on JRuby");
         }
         
         if (block.isGiven()) {
             int pid = runtime.getPosix().fork();
             
             if (pid == 0) {
                 try {
                     block.yield(context, runtime.getNil());
                 } catch (RaiseException re) {
                     if (re.getException() instanceof RubySystemExit) {
                         throw re;
                     }
                     return exit_bang(recv, new IRubyObject[] {RubyFixnum.minus_one(runtime)});
                 } catch (Throwable t) {
                     return exit_bang(recv, new IRubyObject[] {RubyFixnum.minus_one(runtime)});
                 }
                 return exit_bang(recv, new IRubyObject[] {RubyFixnum.zero(runtime)});
             } else {
                 return runtime.newFixnum(pid);
             }
         } else {
             int result = runtime.getPosix().fork();
         
             if (result == -1) {
                 return runtime.getNil();
             }
 
             return runtime.newFixnum(result);
         }
     }
 
     @JRubyMethod(frame = true, module = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject tap(ThreadContext context, IRubyObject recv, Block block) {
         block.yield(context, recv);
         return recv;
     }
 
     @JRubyMethod(name = {"to_enum", "enum_for"}, rest = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject to_enum(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = context.getRuntime();
         switch (args.length) {
         case 0: return enumeratorize(runtime, recv, "each");
         case 1: return enumeratorize(runtime, recv, args[0].asJavaString());
         case 2: return enumeratorize(runtime, recv, args[0].asJavaString(), args[1]);
         default:
             IRubyObject enumArgs[] = new IRubyObject[args.length - 1];
             System.arraycopy(args, 1, enumArgs, 0, enumArgs.length);
             return enumeratorize(runtime, recv, args[0].asJavaString(), enumArgs);
         }
     }
 
     @JRubyMethod(name = { "__method__", "__callee__" }, module = true, compat = CompatVersion.RUBY1_9)
     public static IRubyObject __method__(ThreadContext context, IRubyObject recv) {
         Frame f = context.getCurrentFrame();
         String name = f != null ? f.getName() : null;
         return name != null ? context.getRuntime().newSymbol(name) : context.getRuntime().getNil();
     }
 }
diff --git a/src/org/jruby/evaluator/ASTInterpreter.java b/src/org/jruby/evaluator/ASTInterpreter.java
index d2589e3728..5aff8381c0 100644
--- a/src/org/jruby/evaluator/ASTInterpreter.java
+++ b/src/org/jruby/evaluator/ASTInterpreter.java
@@ -1,397 +1,371 @@
 /*
  ******************************************************************************
  * BEGIN LICENSE BLOCK *** Version: CPL 1.0/GPL 2.0/LGPL 2.1
  * 
  * The contents of this file are subject to the Common Public License Version
  * 1.0 (the "License"); you may not use this file except in compliance with the
  * License. You may obtain a copy of the License at
  * http://www.eclipse.org/legal/cpl-v10.html
  * 
  * Software distributed under the License is distributed on an "AS IS" basis,
  * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  * the specific language governing rights and limitations under the License.
  * 
  * Copyright (C) 2006 Charles Oliver Nutter <headius@headius.com>
  * Copytight (C) 2006-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Ola Bini <ola@ologix.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"), or
  * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
  * which case the provisions of the GPL or the LGPL are applicable instead of
  * those above. If you wish to allow use of your version of this file only under
  * the terms of either the GPL or the LGPL, and not to allow others to use your
  * version of this file under the terms of the CPL, indicate your decision by
  * deleting the provisions above and replace them with the notice and other
  * provisions required by the GPL or the LGPL. If you do not delete the
  * provisions above, a recipient may use your version of this file under the
  * terms of any one of the CPL, the GPL or the LGPL. END LICENSE BLOCK ****
  ******************************************************************************/
 
 package org.jruby.evaluator;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyBinding;
 import org.jruby.RubyLocalJumpError;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.ast.ArrayNode;
 import org.jruby.ast.BlockPassNode;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.JumpException;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.DynamicScope;
 import org.jruby.runtime.RubyEvent;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.Binding;
 import org.jruby.runtime.Frame;
 import org.jruby.runtime.InterpretedBlock;
 import org.jruby.util.TypeConverter;
 
 public class ASTInterpreter {
     @Deprecated
     public static IRubyObject eval(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block block) {
         assert self != null : "self during eval must never be null";
         
         // TODO: Make into an assert once I get things like blockbodynodes to be implicit nil
         if (node == null) return runtime.getNil();
         
         try {
             return node.interpret(runtime, context, self, block);
         } catch (StackOverflowError soe) {
             throw runtime.newSystemStackError("stack level too deep", soe);
         }
     }
     
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber is the line number to pretend we are starting from
      * @return An IRubyObject result from the evaluation
      */
-    public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, 
-            String file, int lineNumber) {
-        // both of these are ensured by the (very few) callers
-        assert !scope.isNil();
-        //assert file != null;
-
+    public static IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, Binding binding) {
         Ruby runtime = src.getRuntime();
-        String savedFile = context.getFile();
-        int savedLine = context.getLine();
-
-        if (!(scope instanceof RubyBinding)) {
-            if (scope instanceof RubyProc) {
-                scope = ((RubyProc) scope).binding();
-            } else {
-                // bomb out, it's not a binding or a proc
-                throw runtime.newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
-            }
-        }
-
-        Binding binding = ((RubyBinding)scope).getBinding();
         DynamicScope evalScope = binding.getDynamicScope().getEvalScope();
-
-        // If no explicit file passed in we will use the bindings location
-        if (file == null) file = binding.getFrame().getFile();
-        if (lineNumber == -1) lineNumber = binding.getFrame().getLine();
         
         // FIXME:  This determine module is in a strange location and should somehow be in block
         evalScope.getStaticScope().determineModule();
 
         Frame lastFrame = context.preEvalWithBinding(binding);
         try {
             // Binding provided for scope, use it
             IRubyObject newSelf = binding.getSelf();
             RubyString source = src.convertToString();
-            Node node = runtime.parseEval(source.getByteList(), file, evalScope, lineNumber);
+            Node node = runtime.parseEval(source.getByteList(), binding.getFile(), evalScope, binding.getLine());
 
             return node.interpret(runtime, context, newSelf, binding.getFrame().getBlock());
         } catch (JumpException.BreakJump bj) {
             throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
         } catch (JumpException.RedoJump rj) {
             throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.REDO, (IRubyObject)rj.getValue(), "unexpected redo");
         } catch (StackOverflowError soe) {
             throw runtime.newSystemStackError("stack level too deep", soe);
         } finally {
             context.postEvalWithBinding(binding, lastFrame);
-
-            // restore position
-            context.setFile(savedFile);
-            context.setLine(savedLine);
         }
     }
 
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber that the eval supposedly starts from
      * @return An IRubyObject result from the evaluation
      * @deprecated Call with a RubyString now.
      */
     public static IRubyObject evalSimple(ThreadContext context, IRubyObject self, IRubyObject src, String file, int lineNumber) {
         RubyString source = src.convertToString();
         return evalSimple(context, self, source, file, lineNumber);
     }
 
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @param lineNumber that the eval supposedly starts from
      * @return An IRubyObject result from the evaluation
      */
     public static IRubyObject evalSimple(ThreadContext context, IRubyObject self, RubyString src, String file, int lineNumber) {
         // this is ensured by the callers
         assert file != null;
 
         Ruby runtime = src.getRuntime();
         String savedFile = context.getFile();
         int savedLine = context.getLine();
 
         // no binding, just eval in "current" frame (caller's frame)
         RubyString source = src.convertToString();
         
         DynamicScope evalScope = context.getCurrentScope().getEvalScope();
         evalScope.getStaticScope().determineModule();
         
         try {
             Node node = runtime.parseEval(source.getByteList(), file, evalScope, lineNumber);
             
             return node.interpret(runtime, context, self, Block.NULL_BLOCK);
         } catch (JumpException.BreakJump bj) {
             throw runtime.newLocalJumpError(RubyLocalJumpError.Reason.BREAK, (IRubyObject)bj.getValue(), "unexpected break");
         } catch (StackOverflowError soe) {
             throw runtime.newSystemStackError("stack level too deep", soe);
         } finally {
             // restore position
             context.setFile(savedFile);
             context.setLine(savedLine);
         }
     }
 
 
     public static void callTraceFunction(Ruby runtime, ThreadContext context, RubyEvent event) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         runtime.callEventHooks(context, event, context.getFile(), context.getLine(), name, type);
     }
     
     public static IRubyObject pollAndReturn(ThreadContext context, IRubyObject result) {
         context.pollThreadEvents();
 
         return result;
     }
     
     public static IRubyObject multipleAsgnArrayNode(Ruby runtime, ThreadContext context, MultipleAsgnNode iVisited, ArrayNode node, IRubyObject self, Block aBlock) {
         IRubyObject[] array = new IRubyObject[node.size()];
 
         for (int i = 0; i < node.size(); i++) {
             array[i] = node.get(i).interpret(runtime,context, self, aBlock);
         }
         return AssignmentVisitor.multiAssign(runtime, context, self, iVisited, RubyArray.newArrayNoCopyLight(runtime, array), false);
     }
 
     /** Evaluates the body in a class or module definition statement.
      *
      */
     public static IRubyObject evalClassDefinitionBody(Ruby runtime, ThreadContext context, StaticScope scope, 
             Node bodyNode, RubyModule type, IRubyObject self, Block block) {
         context.preClassEval(scope, type);
 
         try {
             if (runtime.hasEventHooks()) {
                 callTraceFunction(runtime, context, RubyEvent.CLASS);
             }
 
             if (bodyNode == null) return runtime.getNil();
             return bodyNode.interpret(runtime, context, type, block);
         } finally {
             if (runtime.hasEventHooks()) {
                 callTraceFunction(runtime, context, RubyEvent.END);
             }
             
             context.postClassEval();
         }
     }
 
     public static String getArgumentDefinition(Ruby runtime, ThreadContext context, Node node, String type, IRubyObject self, Block block) {
         if (node == null) return type;
             
         if (node instanceof ArrayNode) {
             ArrayNode list = (ArrayNode) node;
             int size = list.size();
 
             for (int i = 0; i < size; i++) {
                 if (list.get(i).definition(runtime, context, self, block) == null) return null;
             }
         } else if (node.definition(runtime, context, self, block) == null) {
             return null;
         }
 
         return type;
     }
     
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Block currentBlock, Node blockNode) {
         if (blockNode == null) return Block.NULL_BLOCK;
         
         if (blockNode instanceof IterNode) {
             return getIterNodeBlock(blockNode, context,self);
         } else if (blockNode instanceof BlockPassNode) {
             return getBlockPassBlock(blockNode, runtime,context, self, currentBlock);
         }
          
         assert false: "Trying to get block from something which cannot deliver";
         return null;
     }
 
     private static Block getBlockPassBlock(Node blockNode, Ruby runtime, ThreadContext context, IRubyObject self, Block currentBlock) {
         Node bodyNode = ((BlockPassNode) blockNode).getBodyNode();
         IRubyObject proc;
         if (bodyNode == null) {
             proc = runtime.getNil();
         } else {
             proc = bodyNode.interpret(runtime, context, self, currentBlock);
         }
 
         return RuntimeHelpers.getBlockFromBlockPassBody(proc, currentBlock);
     }
 
     private static Block getIterNodeBlock(Node blockNode, ThreadContext context, IRubyObject self) {
         IterNode iterNode = (IterNode) blockNode;
 
         StaticScope scope = iterNode.getScope();
         scope.determineModule();
 
         // Create block for this iter node
         // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
         return InterpretedBlock.newInterpretedClosure(context, iterNode.getBlockBody(), self);
     }
 
     /* Something like cvar_cbase() from eval.c, factored out for the benefit
      * of all the classvar-related node evaluations */
     public static RubyModule getClassVariableBase(ThreadContext context, Ruby runtime) {
         StaticScope scope = context.getCurrentScope().getStaticScope();
         RubyModule rubyClass = scope.getModule();
         if (rubyClass.isSingleton() || rubyClass == runtime.getDummy()) {
             scope = scope.getPreviousCRefScope();
             rubyClass = scope.getModule();
             if (scope.getPreviousCRefScope() == null) {
                 runtime.getWarnings().warn(ID.CVAR_FROM_TOPLEVEL_SINGLETON_METHOD, "class variable access from toplevel singleton method");
             }            
         }
         return rubyClass;
     }
 
     @Deprecated
     public static String getDefinition(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         try {
             context.setWithinDefined(true);
             return node.definition(runtime, context, self, aBlock);
         } finally {
             context.setWithinDefined(false);
         }
     }
 
     public static IRubyObject[] setupArgs(Ruby runtime, ThreadContext context, Node node, IRubyObject self, Block aBlock) {
         if (node == null) return IRubyObject.NULL_ARRAY;
 
         if (node instanceof ArrayNode) {
             ArrayNode argsArrayNode = (ArrayNode) node;
             String savedFile = context.getFile();
             int savedLine = context.getLine();
             int size = argsArrayNode.size();
             IRubyObject[] argsArray = new IRubyObject[size];
 
             for (int i = 0; i < size; i++) {
                 argsArray[i] = argsArrayNode.get(i).interpret(runtime, context, self, aBlock);
             }
 
             context.setFile(savedFile);
             context.setLine(savedLine);
 
             return argsArray;
         }
 
         return ArgsUtil.convertToJavaArray(node.interpret(runtime,context, self, aBlock));
     }
 
     @Deprecated
     public static IRubyObject aValueSplat(Ruby runtime, IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return runtime.getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first(IRubyObject.NULL_ARRAY) : array;
     }
 
     @Deprecated
     public static RubyArray arrayValue(Ruby runtime, IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can 
             // remove this hack too.
             if (value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 value = value.callMethod(runtime.getCurrentContext(), "to_a");
                 if (!(value instanceof RubyArray)) throw runtime.newTypeError("`to_a' did not return Array");
                 return (RubyArray)value;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     @Deprecated
     public static IRubyObject aryToAry(Ruby runtime, IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, runtime.getArray(), "to_ary", false);
         }
 
         return runtime.newArray(value);
     }
 
     @Deprecated
     public static RubyArray splatValue(Ruby runtime, IRubyObject value) {
         if (value.isNil()) {
             return runtime.newArray(value);
         }
 
         return arrayValue(runtime, value);
     }
 
     // Used by the compiler to simplify arg processing
     @Deprecated
     public static RubyArray splatValue(IRubyObject value, Ruby runtime) {
         return splatValue(runtime, value);
     }
     @Deprecated
     public static IRubyObject aValueSplat(IRubyObject value, Ruby runtime) {
         return aValueSplat(runtime, value);
     }
     @Deprecated
     public static IRubyObject aryToAry(IRubyObject value, Ruby runtime) {
         return aryToAry(runtime, value);
     }
 }
diff --git a/src/org/jruby/runtime/Binding.java b/src/org/jruby/runtime/Binding.java
index 731d05cae6..03b8ac05d0 100644
--- a/src/org/jruby/runtime/Binding.java
+++ b/src/org/jruby/runtime/Binding.java
@@ -1,120 +1,150 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
-import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public class Binding {
     
     /**
      * frame of method which defined this block
      */
     private final Frame frame;
     private final RubyModule klass;
 
     private Visibility visibility;
     /**
      * 'self' at point when the block is defined
      */
     private IRubyObject self;
     
     /**
      * A reference to all variable values (and names) that are in-scope for this block.
      */
     private final DynamicScope dynamicScope;
+
+    private String file;
+    private int line;
     
     public Binding(IRubyObject self, Frame frame,
-            Visibility visibility, RubyModule klass, DynamicScope dynamicScope) {
+            Visibility visibility, RubyModule klass, DynamicScope dynamicScope, String file, int line) {
         this.self = self;
         this.frame = frame.duplicate();
         this.visibility = visibility;
         this.klass = klass;
         this.dynamicScope = dynamicScope;
+        this.file = file;
+        this.line = line;
     }
     
-    public Binding(Frame frame, RubyModule bindingClass, DynamicScope dynamicScope) {
+    public Binding(Frame frame, RubyModule bindingClass, DynamicScope dynamicScope, String file, int line) {
         this.self = frame.getSelf();
         this.frame = frame.duplicate();
         this.visibility = frame.getVisibility();
         this.klass = bindingClass;
         this.dynamicScope = dynamicScope;
+        this.file = file;
+        this.line = line;
+    }
+
+    public Binding clone() {
+        return new Binding(self, frame, visibility, klass, dynamicScope, file, line);
+    }
+
+    public Binding clone(Visibility visibility) {
+        return new Binding(self, frame, visibility, klass, dynamicScope, file, line);
     }
 
     public Visibility getVisibility() {
         return visibility;
     }
 
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
     
     public IRubyObject getSelf() {
         return self;
     }
     
     public void setSelf(IRubyObject self) {
         this.self = self;
     }
 
     /**
      * Gets the dynamicVariables that are local to this block.   Parent dynamic scopes are also
      * accessible via the current dynamic scope.
      * 
      * @return Returns all relevent variable scoping information
      */
     public DynamicScope getDynamicScope() {
         return dynamicScope;
     }
 
     /**
      * Gets the frame.
      * 
      * @return Returns a RubyFrame
      */
     public Frame getFrame() {
         return frame;
     }
 
     /**
      * Gets the klass.
      * @return Returns a RubyModule
      */
     public RubyModule getKlass() {
         return klass;
     }
+
+    public String getFile() {
+        return file;
+    }
+
+    public void setFile(String file) {
+        this.file = file;
+    }
+
+    public int getLine() {
+        return line;
+    }
+
+    public void setLine(int line) {
+        this.line = line;
+    }
 }
diff --git a/src/org/jruby/runtime/CallBlock.java b/src/org/jruby/runtime/CallBlock.java
index 537334eca1..1e74904a35 100644
--- a/src/org/jruby/runtime/CallBlock.java
+++ b/src/org/jruby/runtime/CallBlock.java
@@ -1,118 +1,111 @@
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
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation. For
  * lightweight block logic within Java code.
  */
 public class CallBlock extends BlockBody {
     private final Arity arity;
     private final BlockCallback callback;
     private final RubyModule imClass;
     private final ThreadContext context;
     
     public static Block newCallClosure(IRubyObject self, RubyModule imClass, Arity arity, BlockCallback callback, ThreadContext context) {
-        Binding binding = new Binding(self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope());
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
         BlockBody body = new CallBlock(imClass, arity, callback, context);
         
         return new Block(body, binding);
     }
 
     private CallBlock(RubyModule imClass, Arity arity, BlockCallback callback, ThreadContext context) {
         super(BlockBody.SINGLE_RESTARG);
         this.arity = arity;
         this.callback = callback;
         this.imClass = imClass;
         this.context = context;
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         return callback.call(context, args, Block.NULL_BLOCK);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         throw new RuntimeException("This path is not valid yet for CallBlock");
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return callback.call(context, new IRubyObject[] {arg0}, Block.NULL_BLOCK);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         throw new RuntimeException("This path is not valid yet for CallBlock");
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         throw new RuntimeException("This path is not valid yet for CallBlock");
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         return callback.call(context, new IRubyObject[] {value}, Block.NULL_BLOCK);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         return callback.call(context, new IRubyObject[] {value}, Block.NULL_BLOCK);
     }
     
     public StaticScope getStaticScope() {
         throw new RuntimeException("CallBlock does not have a static scope; this should not be called");
     }
 
     public Block cloneBlock(Binding binding) {
-        binding = new Binding(binding.getSelf(), binding.getFrame(),
-                Visibility.PUBLIC,
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone(Visibility.PUBLIC);
         return new Block(this, binding);
     }
 
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/CompiledBlock.java b/src/org/jruby/runtime/CompiledBlock.java
index ee81e2afc3..6f5d49b5fa 100644
--- a/src/org/jruby/runtime/CompiledBlock.java
+++ b/src/org/jruby/runtime/CompiledBlock.java
@@ -1,238 +1,219 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlock extends BlockBody {
     protected final CompiledBlockCallback callback;
     protected final boolean hasMultipleArgsHead;
     protected final Arity arity;
     protected final StaticScope scope;
     
-    public static Block newCompiledClosure(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
-        DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
-        BlockBody body = new CompiledBlock(arity, scope, callback, hasMultipleArgsHead, argumentType);
-        
-        return new Block(body, binding);
-    }
-    
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, Arity arity,
             StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
-        return newCompiledClosure(
-                self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope(),
-                arity,
-                scope,
-                callback,
-                hasMultipleArgsHead,
-                argumentType);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
+        BlockBody body = new CompiledBlock(arity, scope, callback, hasMultipleArgsHead, argumentType);
+
+        return new Block(body, binding);
     }
     
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, BlockBody body) {
-        Binding binding = new Binding(self, context.getCurrentFrame(), Visibility.PUBLIC, context.getRubyClass(), context.getCurrentScope());
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
         return new Block(body, binding);
     }
     
     public static BlockBody newCompiledBlock(Arity arity,
             StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         return new CompiledBlock(arity, scope, callback, hasMultipleArgsHead, argumentType);
     }
 
     protected CompiledBlock(Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         super(argumentType);
         this.arity = arity;
         this.scope = scope;
         this.callback = callback;
         this.hasMultipleArgsHead = hasMultipleArgsHead;
     }
 
     @Override
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yield(context, null, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yield(context, arg0, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1), null, null, true, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1, arg2), null, null, true, binding, type);
     }
 
     @Override
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
 
         IRubyObject realArg = setupBlockArg(context.getRuntime(), value, self); 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
         
         try {
             return callback.call(context, self, realArg);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = prepareSelf(binding);
         }
 
         IRubyObject realArg = aValue ? 
                 setupBlockArgs(context, args, self) : setupBlockArg(context.getRuntime(), args, self); 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, klass, binding);
         
         try {
             return callback.call(context, self, realArg);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
     
     private IRubyObject prepareSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
         
         return self;
     }
 
     private IRubyObject handleNextJump(ThreadContext context, JumpException.NextJump nj, Block.Type type) {
         return nj.getValue() == null ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
     }
     
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldSpecificBlock(binding, scope, klass);
     }
     
     protected void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYield(binding, lastFrame);
     }
 
     protected IRubyObject setupBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return null;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return value;
         default:
             return defaultArgsLogic(context.getRuntime(), value);
         }
     }
     
     private IRubyObject defaultArgsLogic(Ruby ruby, IRubyObject value) {
         int length = ArgsUtil.arrayLength(value);
         switch (length) {
         case 0:
             return ruby.getNil();
         case 1:
             return ((RubyArray)value).eltInternal(0);
         default:
             blockArgWarning(ruby, length);
         }
         return value;
     }
     
     private void blockArgWarning(Ruby ruby, int length) {
         ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (" +
                     length + " for 1)");
     }
 
     protected IRubyObject setupBlockArg(Ruby ruby, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return null;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return ArgsUtil.convertToRubyArray(ruby, value, hasMultipleArgsHead);
         default:
             return defaultArgLogic(ruby, value);
         }
     }
     
     private IRubyObject defaultArgLogic(Ruby ruby, IRubyObject value) {
         if (value == null) {
             return warnMultiReturnNil(ruby);
         }
         return value;
     }
     
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Block cloneBlock(Binding binding) {
-        binding = new Binding(binding.getSelf(),
-                binding.getFrame(),
-                binding.getVisibility(),
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone();
         
         return new Block(this, binding);
     }
 
     @Override
     public Arity arity() {
         return arity;
     }
 
     private IRubyObject warnMultiReturnNil(Ruby ruby) {
         ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (0 for 1)");
         return ruby.getNil();
     }
 }
diff --git a/src/org/jruby/runtime/CompiledBlock19.java b/src/org/jruby/runtime/CompiledBlock19.java
index 48728c480e..78d3525b40 100644
--- a/src/org/jruby/runtime/CompiledBlock19.java
+++ b/src/org/jruby/runtime/CompiledBlock19.java
@@ -1,279 +1,260 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlock19 extends BlockBody {
     protected final CompiledBlockCallback19 callback;
     protected final boolean hasMultipleArgsHead;
     protected final Arity arity;
     protected final StaticScope scope;
     
-    public static Block newCompiledClosure(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
-        DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
-        BlockBody body = new CompiledBlock19(arity, scope, callback, hasMultipleArgsHead, argumentType);
-        
-        return new Block(body, binding);
-    }
-    
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, Arity arity,
             StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
-        return newCompiledClosure(
-                self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope(),
-                arity,
-                scope,
-                callback,
-                hasMultipleArgsHead,
-                argumentType);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
+        BlockBody body = new CompiledBlock19(arity, scope, callback, hasMultipleArgsHead, argumentType);
+
+        return new Block(body, binding);
     }
     
     public static Block newCompiledClosure(ThreadContext context, IRubyObject self, BlockBody body) {
-        Binding binding = new Binding(self, context.getCurrentFrame(), Visibility.PUBLIC, context.getRubyClass(), context.getCurrentScope());
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
         return new Block(body, binding);
     }
     
     public static BlockBody newCompiledBlock(Arity arity,
             StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
         return new CompiledBlock19(arity, scope, callback, hasMultipleArgsHead, argumentType);
     }
 
     protected CompiledBlock19(Arity arity, StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
         super(argumentType);
         this.arity = arity;
         this.scope = scope;
         this.callback = callback;
         this.hasMultipleArgsHead = hasMultipleArgsHead;
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         IRubyObject value = args.length == 1 ? args[0] : context.getRuntime().newArrayNoCopy(args);
 
         return yield(context, value, null, null, true, binding, type, Block.NULL_BLOCK);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type, Block block) {
         IRubyObject value = args.length == 1 ? args[0] : context.getRuntime().newArrayNoCopy(args);
 
         return yield(context, value, null, null, true, binding, type, block);
     }
 
     @Override
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yieldSpecificInternal(context, IRubyObject.NULL_ARRAY, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yieldSpecificInternal(context, new IRubyObject[] {arg0}, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yieldSpecificInternal(context, new IRubyObject[] {arg0, arg1}, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yieldSpecificInternal(context, new IRubyObject[] {arg0, arg1, arg2}, binding, type);
     }
 
     private IRubyObject yieldSpecificInternal(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
 
         try {
             return callback.call(context, self, args, Block.NULL_BLOCK);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     @Override
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
 
         IRubyObject[] realArgs = setupBlockArg(context.getRuntime(), value, self);
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
         
         try {
             return callback.call(context, self, realArgs, Block.NULL_BLOCK);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         return yield(context, args, self, klass, aValue, binding, type, Block.NULL_BLOCK);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject args, IRubyObject self, RubyModule klass, boolean aValue, Binding binding, Block.Type type, Block block) {
         if (klass == null) {
             self = prepareSelf(binding);
         }
 
         IRubyObject[] realArgs = setupBlockArgs(args);
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, klass, binding);
         
         try {
             return callback.call(context, self, realArgs, block);
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
     
     private IRubyObject prepareSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
         
         return self;
     }
 
     private IRubyObject handleNextJump(ThreadContext context, JumpException.NextJump nj, Block.Type type) {
         return nj.getValue() == null ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
     }
     
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldSpecificBlock(binding, scope, klass);
     }
     
     protected void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYield(binding, lastFrame);
     }
 
     private IRubyObject[] setupBlockArgs(IRubyObject value) {
         IRubyObject[] parameters;
 
         if (value instanceof RubyArray) {// && args.getMaxArgumentsCount() != 1) {
             parameters = ((RubyArray) value).toJavaArray();
         } else {
             parameters = new IRubyObject[] { value };
         }
 
         return parameters;
 //        if (!(args instanceof ArgsNoArgNode)) {
 //            Ruby runtime = context.getRuntime();
 //
 //            // FIXME: This needs to happen for lambdas
 ////            args.checkArgCount(runtime, parameters.length);
 //            args.prepare(context, runtime, self, parameters, block);
 //        }
     }
     
     private IRubyObject defaultArgsLogic(Ruby ruby, IRubyObject value) {
         int length = ArgsUtil.arrayLength(value);
         switch (length) {
         case 0:
             return ruby.getNil();
         case 1:
             return ((RubyArray)value).eltInternal(0);
         default:
             blockArgWarning(ruby, length);
         }
         return value;
     }
     
     private void blockArgWarning(Ruby ruby, int length) {
         ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (" +
                     length + " for 1)");
     }
 
     protected IRubyObject[] setupBlockArg(Ruby ruby, IRubyObject value, IRubyObject self) {
         switch (argumentType) {
         case ZERO_ARGS:
             return null;
         case MULTIPLE_ASSIGNMENT:
         case SINGLE_RESTARG:
             return ArgsUtil.convertToRubyArray(ruby, value, hasMultipleArgsHead).toJavaArray();
         default:
             return defaultArgLogic(ruby, value);
         }
     }
     
     private IRubyObject[] defaultArgLogic(Ruby ruby, IRubyObject value) {
         if (value == null) {
 //            return warnMultiReturnNil(ruby);
             return new IRubyObject[] {ruby.getNil()};
         }
         return new IRubyObject[] {value};
     }
 
     private IRubyObject[] warnMultiReturnNil(Ruby ruby) {
         ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (0 for 1)");
         return IRubyObject.NULL_ARRAY;
     }
     
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Block cloneBlock(Binding binding) {
-        binding = new Binding(binding.getSelf(),
-                binding.getFrame(),
-                binding.getVisibility(),
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone();
         
         return new Block(this, binding);
     }
 
     @Override
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/CompiledBlockLight.java b/src/org/jruby/runtime/CompiledBlockLight.java
index 5efa32dfc0..54b6139890 100644
--- a/src/org/jruby/runtime/CompiledBlockLight.java
+++ b/src/org/jruby/runtime/CompiledBlockLight.java
@@ -1,82 +1,67 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlockLight extends CompiledBlock {
-    public static Block newCompiledClosureLight(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
-        DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
-        BlockBody body = new CompiledBlockLight(arity, scope, callback, hasMultipleArgsHead, argumentType);
-        
-        return new Block(body, binding);
-    }
-    
     public static Block newCompiledClosureLight(ThreadContext context, IRubyObject self, Arity arity,
             StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
-        return newCompiledClosureLight(
-                self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope(),
-                arity,
-                scope, 
-                callback,
-                hasMultipleArgsHead,
-                argumentType);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
+        BlockBody body = new CompiledBlockLight(arity, scope, callback, hasMultipleArgsHead, argumentType);
+
+        return new Block(body, binding);
     }
     
     public static BlockBody newCompiledBlockLight(Arity arity,
             StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         return new CompiledBlockLight(arity, scope, callback, hasMultipleArgsHead, argumentType);
     }
 
     protected CompiledBlockLight(Arity arity, StaticScope scope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         super(arity, scope, callback, hasMultipleArgsHead, argumentType);
     }
     
     @Override
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldLightBlock(binding, DynamicScope.newDummyScope(scope, binding.getDynamicScope()), klass);
     }
     
     @Override
     protected final void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYieldLight(binding, lastFrame);
     }
 }
diff --git a/src/org/jruby/runtime/CompiledBlockLight19.java b/src/org/jruby/runtime/CompiledBlockLight19.java
index cdde897f82..2b0cb50092 100644
--- a/src/org/jruby/runtime/CompiledBlockLight19.java
+++ b/src/org/jruby/runtime/CompiledBlockLight19.java
@@ -1,82 +1,67 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledBlockLight19 extends CompiledBlock19 {
-    public static Block newCompiledClosureLight(IRubyObject self, Frame frame, Visibility visibility, RubyModule klass,
-        DynamicScope dynamicScope, Arity arity, StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self, frame, visibility, klass, dynamicScope);
-        BlockBody body = new CompiledBlockLight19(arity, scope, callback, hasMultipleArgsHead, argumentType);
-        
-        return new Block(body, binding);
-    }
-    
     public static Block newCompiledClosureLight(ThreadContext context, IRubyObject self, Arity arity,
             StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
-        return newCompiledClosureLight(
-                self,
-                context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                context.getCurrentScope(),
-                arity,
-                scope, 
-                callback,
-                hasMultipleArgsHead,
-                argumentType);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC);
+        BlockBody body = new CompiledBlockLight19(arity, scope, callback, hasMultipleArgsHead, argumentType);
+
+        return new Block(body, binding);
     }
     
     public static BlockBody newCompiledBlockLight(Arity arity,
             StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
         return new CompiledBlockLight19(arity, scope, callback, hasMultipleArgsHead, argumentType);
     }
 
     protected CompiledBlockLight19(Arity arity, StaticScope scope, CompiledBlockCallback19 callback, boolean hasMultipleArgsHead, int argumentType) {
         super(arity, scope, callback, hasMultipleArgsHead, argumentType);
     }
     
     @Override
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldLightBlock(binding, DynamicScope.newDummyScope(scope, binding.getDynamicScope()), klass);
     }
     
     @Override
     protected final void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYieldLight(binding, lastFrame);
     }
 }
diff --git a/src/org/jruby/runtime/CompiledSharedScopeBlock.java b/src/org/jruby/runtime/CompiledSharedScopeBlock.java
index c52595957e..dbf50fbce6 100644
--- a/src/org/jruby/runtime/CompiledSharedScopeBlock.java
+++ b/src/org/jruby/runtime/CompiledSharedScopeBlock.java
@@ -1,64 +1,60 @@
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
  * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Block implemented using a Java-based BlockCallback implementation
  * rather than with an ICallable. For lightweight block logic within
  * Java code.
  */
 public class CompiledSharedScopeBlock extends CompiledBlockLight {
     public static Block newCompiledSharedScopeClosure(ThreadContext context, IRubyObject self, Arity arity, DynamicScope dynamicScope,
             CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
-        Binding binding = new Binding(self,
-             context.getCurrentFrame(),
-                Visibility.PUBLIC,
-                context.getRubyClass(),
-                dynamicScope);
+        Binding binding = context.currentBinding(self, Visibility.PUBLIC, dynamicScope);
         BlockBody body = new CompiledSharedScopeBlock(arity, dynamicScope, callback, hasMultipleArgsHead, argumentType);
         
         return new Block(body, binding);
     }
 
     private CompiledSharedScopeBlock(Arity arity, DynamicScope containingScope, CompiledBlockCallback callback, boolean hasMultipleArgsHead, int argumentType) {
         super(arity, containingScope.getStaticScope(), callback, hasMultipleArgsHead, argumentType);
     }
     
     @Override
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preForBlock(binding, klass);
     }
     
     @Override
     public Block cloneBlock(Binding binding) {
         return new Block(this, binding);
     }
 }
diff --git a/src/org/jruby/runtime/Frame.java b/src/org/jruby/runtime/Frame.java
index d3ee5c0a36..a4760e4436 100644
--- a/src/org/jruby/runtime/Frame.java
+++ b/src/org/jruby/runtime/Frame.java
@@ -1,484 +1,492 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * A Frame holds per-call information that needs to persist outside the
  * execution of a given method. Currently a frame holds the following:
  * <ul>
  * <li>The class against which this method is being invoked. This is usually
  * (always?) the class of "self" within this call.</li>
  * <li>The current "self" for the call.</li>
  * <li>The name of the method being invoked during this frame, used for
  * backtraces and "super" invocations.</li>
  * <li>The block passed to this invocation. If the given code body can't
  * accept a block, it will be Block.NULL_BLOCK.</li>
  * <li>Whether this is the frame used for a binding-related call like eval. This
  * is used to determine where to terminate evaled code's backtrace.</li>
  * <li>The current visibility for methods defined during this call. Starts out
  * as PUBLIC by default (in most cases) and can be modified by appropriate
  * Kernel.public/private/protected calls.</li>
  * <li>The jump target marker for non-local returns.</li>
  * </ul>
  * Frames are allocated for all Ruby methods (in compatibility mode, default)
  * and for some core methods. In general, a frame is required for a method to
  * show up in a backtrace, and so some methods only use frame for backtrace
  * information (so-called "backtrace frames").
  * 
  * @see ThreadContext
  */
 public final class Frame implements JumpTarget {
     /** The class against which this call is executing. */
     private RubyModule klazz;
     
     /** The 'self' for this frame. */
     private IRubyObject self;
     
     /** The name of the method being invoked in this frame. */
     private String name;
 
     /**
      * The block that was passed in for this frame (as either a block or a &amp;block argument).
      * The frame captures the block for super/zsuper, but also for Proc.new (with no arguments)
      * and also for block_given?.  Both of those methods needs access to the block of the 
      * previous frame to work.
      */ 
     private Block block = Block.NULL_BLOCK;
     
     /** Delimit a frame where an eval with binding occurred.  Used for stack traces. */
     private boolean isBindingFrame = false;
 
     /** The current visibility for anything defined under this frame */
     private Visibility visibility = Visibility.PUBLIC;
     
     /** The target for non-local jumps, like return from a block */
     private final JumpTarget jumpTarget;
     
     /** A tuple representing the $_ and $~ values for this frame */
     private static class BackrefAndLastline {
         public IRubyObject backref;
         public IRubyObject lastline;
     }
     
     /** The current backref/lastline tuple for this frame */
     private BackrefAndLastline backrefAndLastline;
     
     /** The filename where the calling method is located */
     private String fileName;
     
     /** The line number in the calling method where this call is made */
     private int line;
     
     /**
      * Empty constructor, since Frame objects are pre-allocated and updated
      * when needed.
      */
     public Frame() {
         jumpTarget = this;
     }
     
     /**
      * Copy constructor, since Frame objects are pre-allocated and updated
      * when needed.
      */
     private Frame(Frame frame) {
         assert frame.block != null : "Block uses null object pattern.  It should NEVER be null";
 
         this.self = frame.self;
         this.name = frame.name;
         this.klazz = frame.klazz;
         this.fileName = frame.fileName;
         this.line = frame.line;
         this.block = frame.block;
         this.visibility = frame.visibility;
         this.isBindingFrame = frame.isBindingFrame;
         this.jumpTarget = frame.jumpTarget;
         
         // we force the lazy allocation of backref/lastline here to allow
         // closures to update the original frame
         frame.lazyBackrefAndLastline();
         this.backrefAndLastline = frame.backrefAndLastline;
     }
 
     /**
      * Update the frame with just filename and line, used for top-level frames
      * and method.
      * 
      * @param fileName The file where the calling method is located
      * @param line The line number in the calling method where the call is made
      */
     public void updateFrame(String fileName, int line) {
         updateFrame(null, null, null, Block.NULL_BLOCK, fileName, line); 
     }
 
     /**
      * Update the frame with caller information and method name, so it will
      * show up correctly in call stacks.
      * 
      * @param name The name of the method being called
      * @param fileName The file of the calling method
      * @param line The line number of the call to this method
      */
     public void updateFrame(String name, String fileName, int line) {
         this.name = name;
         this.fileName = fileName;
         this.line = line;
     }
 
     /**
      * Update the frame based on information from another frame. Used for
      * cloning frames (for blocks, usually) and when entering class bodies.
      * 
      * @param frame The frame whose data to duplicate in this frame
      */
     public void updateFrame(Frame frame) {
         assert frame.block != null : "Block uses null object pattern.  It should NEVER be null";
 
         this.self = frame.self;
         this.name = frame.name;
         this.klazz = frame.klazz;
         this.fileName = frame.fileName;
         this.line = frame.line;
         this.block = frame.block;
         this.visibility = frame.visibility;
         this.isBindingFrame = frame.isBindingFrame;
         
         // we force the lazy allocation of backref/lastline here to allow
         // closures to update the original frame
         frame.lazyBackrefAndLastline();
         this.backrefAndLastline = frame.backrefAndLastline;
     }
 
     /**
      * Update the frame based on the given values.
      * 
      * @param klazz The class against which the method is being called
      * @param self The 'self' for the method
      * @param name The name under which the method is being invoked
      * @param block The block passed to the method
      * @param fileName The filename of the calling method
      * @param line The line number where the call is being made
      * @param jumpTarget The target for non-local jumps (return in block)
      */
     public void updateFrame(RubyModule klazz, IRubyObject self, String name,
                  Block block, String fileName, int line) {
         assert block != null : "Block uses null object pattern.  It should NEVER be null";
 
         this.self = self;
         this.name = name;
         this.klazz = klazz;
         this.fileName = fileName;
         this.line = line;
         this.block = block;
         this.visibility = Visibility.PUBLIC;
         this.isBindingFrame = false;
     }
 
     /**
      * Update the frame based on the given values.
      * 
      * @param klazz The class against which the method is being called
      * @param self The 'self' for the method
      * @param name The name under which the method is being invoked
      * @param block The block passed to the method
      * @param fileName The filename of the calling method
      * @param line The line number where the call is being made
      * @param jumpTarget The target for non-local jumps (return in block)
      */
     public void updateFrameForEval(IRubyObject self, String fileName, int line) {
         this.self = self;
         this.name = null;
         this.fileName = fileName;
         this.line = line;
         this.visibility = Visibility.PRIVATE;
         this.isBindingFrame = false;
     }
 
     /**
      * Clear the frame, as when the call completes. Clearing prevents cached
      * frames from holding references after the call is done.
      */
     public void clear() {
         this.self = null;
         this.klazz = null;
         this.block = Block.NULL_BLOCK;
         this.backrefAndLastline = null;
     }
     
     /**
      * Clone this frame.
      * 
      * @return A new frame with duplicate information to the target frame
      */
     public Frame duplicate() {
         return new Frame(this);
     }
 
     /**
      * Get the jump target for non-local returns in this frame.
      * 
      * @return The jump target for non-local returns
      */
     public JumpTarget getJumpTarget() {
         return jumpTarget;
     }
 
     /**
      * Set the jump target for non-local returns in this frame.
      * 
      * @param jumpTarget The new jump target for non-local returns
      */
     @Deprecated
     public void setJumpTarget(JumpTarget jumpTarget) {
     }
 
     /**
      * Get the backref for this frame.
      * 
      * @return The backref for this frame
      */
     public IRubyObject getBackRef() {
         if (hasBackref()) {
             return self.getRuntime().getNil();
         }
         return backrefAndLastline.backref;
     }
     
     /**
      * Whether a backref has been set for this frame.
      * 
      * @return True if a backref has been set; false otherwise
      */
     private boolean hasBackref() {
         return backrefAndLastline == null || backrefAndLastline.backref == null;
     }
 
     /**
      * Set the backref for this frame.
      * 
      * @param backref The new backref for this frame
      * @return The passed-in backref value
      */
     public IRubyObject setBackRef(IRubyObject backref) {
         lazyBackrefAndLastline();
         return this.backrefAndLastline.backref = backref;
     }
 
     /**
      * Get the lastline for this frame.
      * 
      * @return The lastline for this frame.
      */
     public IRubyObject getLastLine() {
         if (hasLastline()) {
             return self.getRuntime().getNil();
         }
         return backrefAndLastline.lastline;
     }
     
     /**
      * Whether a lastline has been set for this frame.
      * 
      * @return True if a lastline has been set; false otherwise
      */
     private boolean hasLastline() {
         return backrefAndLastline == null || backrefAndLastline.lastline == null;
     }
 
     /**
      * Set the lastline for this frame.
      * 
      * @param lastline The new lastline for this frame
      * @return The passed-in lastline value
      */
     public IRubyObject setLastLine(IRubyObject lastline) {
         lazyBackrefAndLastline();
         return this.backrefAndLastline.lastline = lastline;
     }
     
     /**
      * Initialize the backref/lastline tuple.
      */
     private void lazyBackrefAndLastline() {
          if (backrefAndLastline == null) backrefAndLastline = new BackrefAndLastline();
     }
 
     /**
      * Get the filename of the caller.
      * 
      * @return The filename of the caller
      */
     public String getFile() {
         return fileName;
     }
 
     /**
      * Set the filename of the caller.
      * @param fileName
      */
     public void setFile(String fileName) {
         this.fileName = fileName;
     }
     
     /**
      * Get the line number where this call is being made.
      * 
      * @return The line number where this call is being made
      */
     public int getLine() {
         return line;
     }
     
     /**
      * Set the line number where this call is being made
      * @param line The new line number where this call is being made
      */
     public void setLine(int line) {
         this.line = line;
     }
 
+    /**
+     * Set both the file and line
+     */
+    public void setFileAndLine(String file, int line) {
+        this.fileName = file;
+        this.line = line;
+    }
+
     /** 
      * Return class that we are calling against
      * 
      * @return The class we are calling against
      */
     public RubyModule getKlazz() {
         return klazz;
     }
 
     /**
      * Set the class we are calling against.
      * 
      * @param klazz the new class
      */
     public void setKlazz(RubyModule klazz) {
         this.klazz = klazz;
     }
 
     /**
      * Set the method name associated with this frame
      * 
      * @param name the new name
      */
     public void setName(String name) {
         this.name = name;
     }
 
     /** 
      * Get the method name associated with this frame
      * 
      * @return the method name
      */
     public String getName() {
         return name;
     }
 
     /**
      * Get the self associated with this frame
      * 
      * @return The self for the frame
      */
     IRubyObject getSelf() {
         return self;
     }
 
     /** 
      * Set the self associated with this frame
      * 
      * @param self The new value of self
      */
     public void setSelf(IRubyObject self) {
         this.self = self;
     }
     
     /**
      * Get the visibility at the time of this frame
      * 
      * @return The visibility
      */
     public Visibility getVisibility() {
         return visibility;
     }
     
     /**
      * Change the visibility associated with this frame
      * 
      * @param visibility The new visibility
      */
     public void setVisibility(Visibility visibility) {
         this.visibility = visibility;
     }
     
     /**
      * Is this frame the frame which started a binding eval?
      * 
      * @return Whether this is a binding frame
      */
     public boolean isBindingFrame() {
         return isBindingFrame;
     }
     
     /**
      * Set whether this is a binding frame or not
      * 
      * @param isBindingFrame Whether this is a binding frame
      */
     public void setIsBindingFrame(boolean isBindingFrame) {
         this.isBindingFrame = isBindingFrame;
     }
     
     /**
      * Retrieve the block associated with this frame.
      * 
      * @return The block of this frame or NULL_BLOCK if no block given
      */
     public Block getBlock() {
         return block;
     }
 
     /* (non-Javadoc)
      * @see java.lang.Object#toString()
      */
     public String toString() {
         StringBuilder sb = new StringBuilder(50);
         
         sb.append(fileName).append(':').append(line+1).append(':').append(klazz);
         if (name != null) sb.append(" in ").append(name);
 
         return sb.toString();
     }
 }
diff --git a/src/org/jruby/runtime/Interpreted19Block.java b/src/org/jruby/runtime/Interpreted19Block.java
index 2377df11aa..19a2f740a6 100644
--- a/src/org/jruby/runtime/Interpreted19Block.java
+++ b/src/org/jruby/runtime/Interpreted19Block.java
@@ -1,259 +1,248 @@
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.LambdaNode;
 import org.jruby.ast.NilImplicitNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.ArgsNoArgNode;
 import org.jruby.ast.ArgsNode;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 /**
  *
  * @author enebo
  */
 public class Interpreted19Block  extends BlockBody {
     /** The argument list, pulled out of iterNode */
     private final ArgsNode args;
 
     /** The body of the block, pulled out of bodyNode */
     private final Node body;
 
     /** The static scope for the block body */
     private final StaticScope scope;
 
     /** The arity of the block */
     private final Arity arity;
 
     public static Block newInterpretedClosure(ThreadContext context, BlockBody body, IRubyObject self) {
-        Frame frame = context.getCurrentFrame();
-
-        Binding binding = new Binding(self,
-                         frame,
-                         frame.getVisibility(),
-                         context.getRubyClass(),
-                         context.getCurrentScope());
+        Binding binding = context.currentBinding(self);
         return new Block(body, binding);
     }
 
     public Interpreted19Block(IterNode iter) {
         super(-1); // We override that the logic which uses this
 
         if (iter instanceof LambdaNode) {
             LambdaNode lambda = (LambdaNode)iter;
             
             this.arity = lambda.getArgs().getArity();
             this.args = lambda.getArgs();
             this.body = lambda.getBody() == null ? NilImplicitNode.NIL : lambda.getBody();
             this.scope = lambda.getScope();
         } else {
             ArgsNode argsNode = (ArgsNode) iter.getVarNode();
 
             if (argsNode == null) {
                 System.out.println("ITER HAS NO ARGS: " + iter);
             }
 
             this.arity = argsNode.getArity();
             this.args = argsNode;
             this.body = iter.getBodyNode() == null ? NilImplicitNode.NIL : iter.getBodyNode();
             this.scope = iter.getScope();
         }
     }
 
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldSpecificBlock(binding, scope, klass);
     }
 
     protected void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYield(binding, lastFrame);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         IRubyObject value = args.length == 1 ? args[0] : context.getRuntime().newArrayNoCopy(args);
 
         return yield(context, value, null, null, true, binding, type, Block.NULL_BLOCK);
     }
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type, Block block) {
         IRubyObject value = args.length == 1 ? args[0] : context.getRuntime().newArrayNoCopy(args);
 
         return yield(context, value, null, null, true, binding, type, block);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yield(context, null, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yield(context, arg0, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1), null, null, true, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1, arg2), null, null, true, binding, type);
     }
 
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
 
         try {
             setupBlockArgs(context, value, self, Block.NULL_BLOCK, type);
 
             return evalBlockBody(context, self);
         } catch (JumpException.NextJump nj) {
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      *
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self,
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         return yield(context, value, self, klass, aValue, binding, type, Block.NULL_BLOCK);
 
     }
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self,
             RubyModule klass, boolean aValue, Binding binding, Block.Type type, Block block) {
         if (klass == null) {
             self = prepareSelf(binding);
         }
 
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, klass, binding);
 
         try {
             setupBlockArgs(context, value, self, block, type);
 
             // This while loop is for restarting the block call in case a 'redo' fires.
             return evalBlockBody(context, self);
         } catch (JumpException.NextJump nj) {
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     private IRubyObject evalBlockBody(ThreadContext context, IRubyObject self) {
         // This while loop is for restarting the block call in case a 'redo' fires.
         while (true) {
             try {
                 return body.interpret(context.getRuntime(), context, self, Block.NULL_BLOCK);
             } catch (JumpException.RedoJump rj) {
                 context.pollThreadEvents();
                 // do nothing, allow loop to redo
             } catch (StackOverflowError soe) {
                 throw context.getRuntime().newSystemStackError("stack level too deep", soe);
             }
         }
     }
 
     private IRubyObject prepareSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
 
         return self;
     }
 
     private IRubyObject handleNextJump(ThreadContext context, JumpException.NextJump nj, Block.Type type) {
         return nj.getValue() == null ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
     }
 
     private void setupBlockArgs(ThreadContext context, IRubyObject value, IRubyObject self, Block block, Block.Type type) {
         IRubyObject[] parameters;
 
         if (value instanceof RubyArray && args.getMaxArgumentsCount() != 1) {
             parameters = ((RubyArray) value).toJavaArray();
         } else {
             parameters = new IRubyObject[] { value };
         }
 
         if (!(args instanceof ArgsNoArgNode)) {
             Ruby runtime = context.getRuntime();
 
             // FIXME: This needs to happen for lambdas
 //            args.checkArgCount(runtime, parameters.length);
             args.prepare(context, runtime, self, parameters, block);
         }
     }
 
     public Block cloneBlock(Binding binding) {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
-        binding = new Binding(
-                binding.getSelf(),
-                binding.getFrame(),
-                binding.getVisibility(),
-                binding.getKlass(),
-                binding.getDynamicScope());
+        binding = binding.clone();
 
         return new Block(this, binding);
     }
 
     public ArgsNode getArgs() {
         return args;
     }
     
     public Node getBody() {
         return body;
     }
 
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/InterpretedBlock.java b/src/org/jruby/runtime/InterpretedBlock.java
index 22dcce969d..b0804e89ca 100644
--- a/src/org/jruby/runtime/InterpretedBlock.java
+++ b/src/org/jruby/runtime/InterpretedBlock.java
@@ -1,322 +1,290 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.runtime;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyModule;
 import org.jruby.ast.IterNode;
 import org.jruby.ast.MultipleAsgnNode;
 import org.jruby.ast.NilImplicitNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.NodeType;
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.evaluator.AssignmentVisitor;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * This branch of the BlockBody hierarchy represents an interpreted block that
  * passes its AST nodes to the interpreter. It forms the top of the hierarchy
  * of interpreted blocks. In a typical application, it is the most heavily
  * consumed type of block.
  * 
  * @see SharedScopeBlock, CompiledBlock
  */
 public class InterpretedBlock extends BlockBody {
     /** The node wrapping the body and parameter list for this block */
     private final IterNode iterNode;
     
     /** Whether this block has an argument list or not */
     private final boolean hasVarNode;
     
     /** The argument list, pulled out of iterNode */
     private final Node varNode;
     
     /** The body of the block, pulled out of bodyNode */
     private final Node bodyNode;
     
     /** The static scope for the block body */
     private final StaticScope scope;
     
     /** The arity of the block */
     private final Arity arity;
 
     public static Block newInterpretedClosure(ThreadContext context, IterNode iterNode, IRubyObject self) {
-        Frame f = context.getCurrentFrame();
-
-        return newInterpretedClosure(iterNode,
-                         self,
-                         Arity.procArityOf(iterNode.getVarNode()),
-                         f,
-                         f.getVisibility(),
-                         context.getRubyClass(),
-                         context.getCurrentScope());
-    }
-
-    public static Block newInterpretedClosure(ThreadContext context, BlockBody body, IRubyObject self) {
-        Frame f = context.getCurrentFrame();
-
-        Binding binding = new Binding(self,
-                         f,
-                         f.getVisibility(),
-                         context.getRubyClass(),
-                         context.getCurrentScope());
-        return new Block(body, binding);
-    }
-    
-    public static Block newInterpretedClosure(IterNode iterNode, IRubyObject self, Arity arity, Frame frame,
-            Visibility visibility, RubyModule klass, DynamicScope dynamicScope) {
+        Binding binding = context.currentBinding(self);
         NodeType argsNodeId = getArgumentTypeWackyHack(iterNode);
-        
+
         BlockBody body = new InterpretedBlock(
                 iterNode,
-                arity,
+                Arity.procArityOf(iterNode.getVarNode()),
                 asArgumentType(argsNodeId));
-        
-        Binding binding = new Binding(
-                self, 
-                frame,
-                visibility,
-                klass,
-                dynamicScope);
-        
+        return new Block(body, binding);
+    }
+
+    public static Block newInterpretedClosure(ThreadContext context, BlockBody body, IRubyObject self) {
+        Binding binding = context.currentBinding(self);
         return new Block(body, binding);
     }
 
     public InterpretedBlock(IterNode iterNode, int argumentType) {
         this(iterNode, Arity.procArityOf(iterNode == null ? null : iterNode.getVarNode()), argumentType);
     }
     
     public InterpretedBlock(IterNode iterNode, Arity arity, int argumentType) {
         super(argumentType);
         this.iterNode = iterNode;
         this.arity = arity;
         this.hasVarNode = iterNode.getVarNode() != null;
         this.varNode = iterNode.getVarNode();
         this.bodyNode = iterNode.getBodyNode() == null ? NilImplicitNode.NIL : iterNode.getBodyNode();
         this.scope = iterNode.getScope();
     }
     
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldSpecificBlock(binding, iterNode.getScope(), klass);
     }
     
     protected void post(ThreadContext context, Binding binding, Visibility vis, Frame lastFrame) {
         binding.getFrame().setVisibility(vis);
         context.postYield(binding, lastFrame);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yield(context, null, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yield(context, arg0, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1), null, null, true, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1, arg2), null, null, true, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         IRubyObject self = prepareSelf(binding);
         
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, null, binding);
 
         try {
             if (hasVarNode) {
                 setupBlockArg(context, varNode, value, self);
             }
             
             return evalBlockBody(context, self);
         } catch (JumpException.NextJump nj) {
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = prepareSelf(binding);
         }
         
         Visibility oldVis = binding.getFrame().getVisibility();
         Frame lastFrame = pre(context, klass, binding);
 
         try {
             if (hasVarNode) {
                 if (aValue) {
                     setupBlockArgs(context, varNode, value, self);
                 } else {
                     setupBlockArg(context, varNode, value, self);
                 }
             }
             
             // This while loop is for restarting the block call in case a 'redo' fires.
             return evalBlockBody(context, self);
         } catch (JumpException.NextJump nj) {
             return handleNextJump(context, nj, type);
         } finally {
             post(context, binding, oldVis, lastFrame);
         }
     }
     
     private IRubyObject evalBlockBody(ThreadContext context, IRubyObject self) {
         // This while loop is for restarting the block call in case a 'redo' fires.
         while (true) {
             try {
                 return bodyNode.interpret(context.getRuntime(), context, self, Block.NULL_BLOCK);
             } catch (JumpException.RedoJump rj) {
                 context.pollThreadEvents();
                 // do nothing, allow loop to redo
             } catch (StackOverflowError soe) {
                 throw context.getRuntime().newSystemStackError("stack level too deep", soe);
             }
         }
     }
     
     private IRubyObject prepareSelf(Binding binding) {
         IRubyObject self = binding.getSelf();
         binding.getFrame().setSelf(self);
         
         return self;
     }
     
     private IRubyObject handleNextJump(ThreadContext context, JumpException.NextJump nj, Block.Type type) {
         return nj.getValue() == null ? context.getRuntime().getNil() : (IRubyObject)nj.getValue();
     }
 
     private void setupBlockArgs(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = context.getRuntime();
         
         switch (varNode.getNodeType()) {
         case ZEROARGNODE:
             break;
         case MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode, (RubyArray)value, false);
             break;
         default:
             defaultArgsLogic(context, runtime, self, value);
         }
     }
 
     private void setupBlockArg(ThreadContext context, Node varNode, IRubyObject value, IRubyObject self) {
         Ruby runtime = context.getRuntime();
         
         switch (varNode.getNodeType()) {
         case ZEROARGNODE:
             return;
         case MULTIPLEASGNNODE:
             value = AssignmentVisitor.multiAssign(runtime, context, self, (MultipleAsgnNode)varNode,
                     ArgsUtil.convertToRubyArray(runtime, value, ((MultipleAsgnNode)varNode).getHeadNode() != null), false);
             break;
         default:
             defaultArgLogic(context, runtime, self, value);
         }
     }
     
     private final void defaultArgsLogic(ThreadContext context, Ruby ruby, IRubyObject self, IRubyObject value) {
         int length = ArgsUtil.arrayLength(value);
         switch (length) {
         case 0:
             value = ruby.getNil();
             break;
         case 1:
             value = ((RubyArray)value).eltInternal(0);
             break;
         default:
             ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (" + length + " for 1)");
         }
         
         varNode.assign(ruby, context, self, value, Block.NULL_BLOCK, false);
     }
     
     private final void defaultArgLogic(ThreadContext context, Ruby ruby, IRubyObject self, IRubyObject value) {
         if (value == null) {
             ruby.getWarnings().warn(ID.MULTIPLE_VALUES_FOR_BLOCK, "multiple values for a block parameter (0 for 1)");
         }
         varNode.assign(ruby, context, self, value, Block.NULL_BLOCK, false);
     }
     
     public StaticScope getStaticScope() {
         return scope;
     }
 
     public Block cloneBlock(Binding binding) {
         // We clone dynamic scope because this will be a new instance of a block.  Any previously
         // captured instances of this block may still be around and we do not want to start
         // overwriting those values when we create a new one.
         // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
-        binding = new Binding(
-                binding.getSelf(),
-                binding.getFrame(),
-                binding.getVisibility(),
-                binding.getKlass(), 
-                binding.getDynamicScope());
-        
+        binding = binding.clone();
         return new Block(this, binding);
     }
 
     public IterNode getIterNode() {
         return iterNode;
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/MethodBlock.java b/src/org/jruby/runtime/MethodBlock.java
index 592f763926..9911cb99ab 100644
--- a/src/org/jruby/runtime/MethodBlock.java
+++ b/src/org/jruby/runtime/MethodBlock.java
@@ -1,171 +1,161 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyMethod;
 import org.jruby.RubyModule;
 import org.jruby.exceptions.JumpException;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  *  Internal live representation of a block ({...} or do ... end).
  */
 public abstract class MethodBlock extends BlockBody {
     private final RubyMethod method;
     
     private final Arity arity;
      
     // This is a dummy scope; we should find a way to make that more explicit
     private final StaticScope staticScope;
 
     public static Block createMethodBlock(ThreadContext context, IRubyObject self, DynamicScope dynamicScope, MethodBlock body) {
-        Binding binding = new Binding(self,
-                               context.getCurrentFrame().duplicate(),
-                         context.getCurrentFrame().getVisibility(),
-                         context.getRubyClass(),
-                         dynamicScope);
-        
+        Binding binding = context.currentBinding(self, dynamicScope);
         return new Block(body, binding);
     }
 
     public MethodBlock(RubyMethod method, StaticScope staticScope) {
         super(BlockBody.SINGLE_RESTARG);
         this.method = method;
         this.arity = Arity.createArity((int) method.arity().getLongValue());
         this.staticScope = staticScope;
     }
 
     public abstract IRubyObject callback(IRubyObject value, IRubyObject method, IRubyObject self, Block block);
 
     @Override
     public IRubyObject call(ThreadContext context, IRubyObject[] args, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type);
     }
     
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preYieldNoScope(binding, klass);
     }
     
     protected void post(ThreadContext context, Binding binding, Frame lastFrame) {
         context.postYieldNoScope(lastFrame);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, Binding binding, Block.Type type) {
         return yield(context, null, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, Binding binding, Block.Type type) {
         return yield(context, arg0, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1), null, null, true, binding, type);
     }
 
     public IRubyObject yieldSpecific(ThreadContext context, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopyLight(arg0, arg1, arg2), null, null, true, binding, type);
     }
     
     public IRubyObject yield(ThreadContext context, IRubyObject value, Binding binding, Block.Type type) {
         return yield(context, value, null, null, false, binding, type);
     }
 
     /**
      * Yield to this block, usually passed to the current call.
      * 
      * @param context represents the current thread-specific data
      * @param value The value to yield, either a single value or an array of values
      * @param self The current self
      * @param klass
      * @param aValue Should value be arrayified or not?
      * @return
      */
     public IRubyObject yield(ThreadContext context, IRubyObject value, IRubyObject self, 
             RubyModule klass, boolean aValue, Binding binding, Block.Type type) {
         if (klass == null) {
             self = binding.getSelf();
             binding.getFrame().setSelf(self);
         }
         
         Frame lastFrame = pre(context, klass, binding);
 
         try {
             // This while loop is for restarting the block call in case a 'redo' fires.
             while (true) {
                 try {
                     return callback(value, method, self, Block.NULL_BLOCK);
                 } catch (JumpException.RedoJump rj) {
                     context.pollThreadEvents();
                     // do nothing, allow loop to redo
                 } catch (JumpException.BreakJump bj) {
                     if (bj.getTarget() == null) {
                         bj.setTarget(this);                            
                     }                        
                     throw bj;
                 }
             }
         } catch (JumpException.NextJump nj) {
             // A 'next' is like a local return from the block, ending this call or yield.
             return (IRubyObject)nj.getValue();
         } finally {
             post(context, binding, lastFrame);
         }
     }
     
     public StaticScope getStaticScope() {
         // TODO: This is actually now returning the scope of whoever called Method#to_proc
         // which is obviously wrong; but there's no scope to provide for many methods.
         // It fixes JRUBY-2237, but needs a better solution.
         return staticScope;
     }
 
     public Block cloneBlock(Binding binding) {
-        // We clone dynamic scope because this will be a new instance of a block.  Any previously
-        // captured instances of this block may still be around and we do not want to start
-        // overwriting those values when we create a new one.
-        // ENEBO: Once we make self, lastClass, and lastMethod immutable we can remove duplicate
-        binding = new Binding(binding.getSelf(), binding.getFrame(), binding.getVisibility(), binding.getKlass(), 
-                binding.getDynamicScope().cloneScope());
+        binding = binding.clone();
 
         return new Block(this, binding);
     }
 
     /**
      * What is the arity of this block?
      * 
      * @return the arity
      */
     public Arity arity() {
         return arity;
     }
 }
diff --git a/src/org/jruby/runtime/SharedScopeBlock.java b/src/org/jruby/runtime/SharedScopeBlock.java
index 97a13f6961..8db9eb15e4 100644
--- a/src/org/jruby/runtime/SharedScopeBlock.java
+++ b/src/org/jruby/runtime/SharedScopeBlock.java
@@ -1,70 +1,66 @@
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
  * Copyright (C) 2007 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby.runtime;
 
 import org.jruby.RubyModule;
 import org.jruby.ast.IterNode;
 import org.jruby.runtime.builtin.IRubyObject;
 
 /**
  * Represents the live state of a for or END construct in Ruby.  This is different from an
  * ordinary block in that it does not have its own scoped variables.  It leeches those from
  * the next outer scope.  Because of this we do not set up, clone, nor tear down scope-related
  * stuff.  Also because of this we do not need to clone the block since it state does not change.
  * 
  */
 public class SharedScopeBlock extends InterpretedBlock {
     protected SharedScopeBlock(IterNode iterNode) {
         super(iterNode, asArgumentType(getArgumentTypeWackyHack(iterNode)));
     }
     
     public static Block newInterpretedSharedScopeClosure(ThreadContext context, IterNode iterNode, DynamicScope dynamicScope, IRubyObject self) {
-        Binding binding = new Binding(self,
-                context.getCurrentFrame(),
-                context.getCurrentFrame().getVisibility(),
-                context.getRubyClass(),
-                dynamicScope);
+        Binding binding = context.currentBinding(self, dynamicScope);
         BlockBody body = new SharedScopeBlock(iterNode);
 
         return new Block(body, binding);
     }
     
     @Override
     protected Frame pre(ThreadContext context, RubyModule klass, Binding binding) {
         return context.preForBlock(binding, klass);
     }
     
     public IRubyObject call(ThreadContext context, IRubyObject[] args, IRubyObject replacementSelf, Binding binding, Block.Type type) {
         return yield(context, context.getRuntime().newArrayNoCopy(args), null, null, true, binding, type);
     }
     
     @Override
     public Block cloneBlock(Binding binding) {
         return new Block(this, binding);
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 5222fed99c..9e8649ae79 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1366 +1,1440 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
 package org.jruby.runtime;
 
+import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
-import org.jruby.RubyException;
-import org.jruby.RubyObject;
 import org.jruby.RubyKernel.CatchTarget;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.RubyThread;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException.ReturnJump;
 import org.jruby.internal.runtime.JumpTarget;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 
 public final class ThreadContext {
     public static synchronized ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
         //        if(runtime.getInstanceConfig().isSamplingEnabled()) {
         //    org.jruby.util.SimpleSampler.registerThreadContext(context);
         //}
 
         return context;
     }
     
     private final static int INITIAL_SIZE = 10;
     private final static String UNKNOWN_NAME = "(unknown)";
     
     private final Ruby runtime;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     //private UnsynchronizedStack parentStack;
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     //private UnsynchronizedStack frameStack;
     private Frame[] frameStack = new Frame[INITIAL_SIZE];
     private int frameIndex = -1;
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
     
     private CatchTarget[] catchStack = new CatchTarget[INITIAL_SIZE];
     private int catchIndex = -1;
     
     // File where current executing unit is being evaluated
     private String file = "";
     
     // Line where current executing unit is being evaluated
     private int line = 0;
 
     // In certain places, like grep, we don't use real frames for the
     // call blocks. This has the effect of not setting the backref in
     // the correct frame - this delta is activated to the place where
     // the grep is running in so that the backref will be set in an
     // appropriate place.
     private int rubyFrameDelta = 0;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
             
         for (int i = 0; i < frameStack.length; i++) {
             frameStack[i] = new Frame();
         }
     }
 
     @Override
     protected void finalize() throws Throwable {
         thread.dispose();
     }
     
     CallType lastCallType;
     
     Visibility lastVisibility;
     
     IRubyObject lastExitStatus;
     
     public final Ruby getRuntime() {
         return runtime;
     }
     
     public IRubyObject getErrorInfo() {
         return thread.getErrorInfo();
     }
     
     public IRubyObject setErrorInfo(IRubyObject errorInfo) {
         thread.setErrorInfo(errorInfo);
         return errorInfo;
     }
     
     public ReturnJump returnJump(IRubyObject value) {
         return new ReturnJump(getFrameJumpTarget(), value);
     }
     
     /**
      * Returns the lastCallStatus.
      * @return LastCallStatus
      */
     public void setLastCallStatus(CallType callType) {
         lastCallType = callType;
     }
 
     public CallType getLastCallType() {
         return lastCallType;
     }
 
     public void setLastVisibility(Visibility visibility) {
         lastVisibility = visibility;
     }
 
     public Visibility getLastVisibility() {
         return lastVisibility;
     }
     
     public void setLastCallStatusAndVisibility(CallType callType, Visibility visibility) {
         lastCallType = callType;
         lastVisibility = visibility;
     }
     
     public IRubyObject getLastExitStatus() {
         return lastExitStatus;
     }
     
     public void setLastExitStatus(IRubyObject lastExitStatus) {
         this.lastExitStatus = lastExitStatus;
     }
 
     public void printScope() {
         System.out.println("SCOPE STACK:");
         for (int i = 0; i <= scopeIndex; i++) {
             System.out.println(scopeStack[i]);
         }
     }
 
     public DynamicScope getCurrentScope() {
         return scopeStack[scopeIndex];
     }
     
     public DynamicScope getPreviousScope() {
         return scopeStack[scopeIndex - 1];
     }
     
     private void expandFramesIfNecessary() {
         int newSize = frameStack.length * 2;
         frameStack = fillNewFrameStack(new Frame[newSize], newSize);
     }
 
     private Frame[] fillNewFrameStack(Frame[] newFrameStack, int newSize) {
         System.arraycopy(frameStack, 0, newFrameStack, 0, frameStack.length);
 
         for (int i = frameStack.length; i < newSize; i++) {
             newFrameStack[i] = new Frame();
         }
         
         return newFrameStack;
     }
     
     private void expandParentsIfNecessary() {
         int newSize = parentStack.length * 2;
         RubyModule[] newParentStack = new RubyModule[newSize];
 
         System.arraycopy(parentStack, 0, newParentStack, 0, parentStack.length);
 
         parentStack = newParentStack;
     }
     
     public void pushScope(DynamicScope scope) {
         int index = ++scopeIndex;
         DynamicScope[] stack = scopeStack;
         stack[index] = scope;
         if (index + 1 == stack.length) {
             expandScopesIfNecessary();
         }
     }
     
     public void popScope() {
         scopeStack[scopeIndex--] = null;
     }
     
     private void expandScopesIfNecessary() {
         int newSize = scopeStack.length * 2;
         DynamicScope[] newScopeStack = new DynamicScope[newSize];
 
         System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
 
         scopeStack = newScopeStack;
     }
     
     public RubyThread getThread() {
         return thread;
     }
     
     public void setThread(RubyThread thread) {
         this.thread = thread;
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
 //    public IRubyObject getLastline() {
 //        IRubyObject value = getCurrentScope().getLastLine();
 //        
 //        // DynamicScope does not preinitialize these values since they are virtually never used.
 //        return value == null ? runtime.getNil() : value;
 //    }
 //    
 //    public void setLastline(IRubyObject value) {
 //        getCurrentScope().setLastLine(value);
 //    }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         int newSize = catchStack.length * 2;
         CatchTarget[] newCatchStack = new CatchTarget[newSize];
 
         System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
         catchStack = newCatchStack;
     }
     
     public void pushCatch(CatchTarget catchTarget) {
         int index = ++catchIndex;
         CatchTarget[] stack = catchStack;
         stack[index] = catchTarget;
         if (index + 1 == stack.length) {
             expandCatchIfNecessary();
         }
     }
     
     public void popCatch() {
         catchIndex--;
     }
     
     public CatchTarget[] getActiveCatches() {
         int index = catchIndex;
         if (index < 0) return new CatchTarget[0];
         
         CatchTarget[] activeCatches = new CatchTarget[index + 1];
         System.arraycopy(catchStack, 0, activeCatches, 0, index + 1);
         return activeCatches;
     }
     
     //////////////////// FRAME MANAGEMENT ////////////////////////
     private void pushFrameCopy() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         Frame currentFrame = stack[index - 1];
         stack[index].updateFrame(currentFrame);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private Frame pushFrame(Frame frame) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index] = frame;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return frame;
     }
     
     private void pushCallFrame(RubyModule clazz, String name, 
                                IRubyObject self, Block block) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(clazz, self, name, block, file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushEvalFrame(IRubyObject self) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrameForEval(self, file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushBacktraceFrame(String name) {
         pushFrame(name);        
     }
     
     private void pushFrame(String name) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(name, file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushFrame() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrame(file, line);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void popFrame() {
         Frame frame = frameStack[frameIndex--];
         setFileAndLine(frame);
         
         frame.clear();
     }
         
     private void popFrameReal(Frame oldFrame) {
         int index = frameIndex;
         Frame frame = frameStack[index];
         frameStack[index] = oldFrame;
         frameIndex = index - 1;
         setFileAndLine(frame);
     }
     
     public Frame getCurrentFrame() {
         return frameStack[frameIndex];
     }
 
     public int getRubyFrameDelta() {
         return this.rubyFrameDelta;
     }
     
     public void setRubyFrameDelta(int newDelta) {
         this.rubyFrameDelta = newDelta;
     }
 
     public Frame getCurrentRubyFrame() {
         return frameStack[frameIndex-rubyFrameDelta];
     }
     
     public Frame getNextFrame() {
         int index = frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
         return stack[index + 1];
     }
     
     public Frame getPreviousFrame() {
         int index = frameIndex;
         return index < 1 ? null : frameStack[index - 1];
     }
     
     public int getFrameCount() {
         return frameIndex + 1;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public JumpTarget getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     @Deprecated
     public void setFrameJumpTarget(JumpTarget target) {
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return file;
     }
     
     public int getLine() {
         return line;
     }
     
     public void setFile(String file) {
         this.file = file;
     }
     
     public void setLine(int line) {
         this.line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         this.file = file;
         this.line = line;
     }
     
     public void setFileAndLine(Frame frame) {
         this.file = frame.getFile();
         this.line = frame.getLine();
     }
 
     public void setFileAndLine(ISourcePosition position) {
         this.file = position.getFile();
         this.line = position.getStartLine();
     }
     
     public Visibility getCurrentVisibility() {
         return getCurrentFrame().getVisibility();
     }
     
     public Visibility getPreviousVisibility() {
         return getPreviousFrame().getVisibility();
     }
     
     public void setCurrentVisibility(Visibility visibility) {
         getCurrentFrame().setVisibility(visibility);
     }
     
     public void pollThreadEvents() {
         thread.pollThreadEvents(this);
     }
     
     int calls = 0;
     
     public void callThreadPoll() {
         if ((calls++ & 0xFF) == 0) pollThreadEvents();
     }
     
     public void trace(RubyEvent event, String name, RubyModule implClass) {
         runtime.callEventHooks(this, event, file, line, name, implClass);
     }
     
     public void pushRubyClass(RubyModule currentModule) {
         // FIXME: this seems like a good assertion, but it breaks compiled code and the code seems
         // to run without it...
         //assert currentModule != null : "Can't push null RubyClass";
         
         int index = ++parentIndex;
         RubyModule[] stack = parentStack;
         stack[index] = currentModule;
         if (index + 1 == stack.length) {
             expandParentsIfNecessary();
         }
     }
     
     public RubyModule popRubyClass() {
         int index = parentIndex;
         RubyModule[] stack = parentStack;
         RubyModule ret = stack[index];
         stack[index] = null;
         parentIndex = index - 1;
         return ret;
     }
     
     public RubyModule getRubyClass() {
-        assert !(parentIndex == -1) : "Trying to get RubyClass from empty stack";
-        
+        assert parentIndex != -1 : "Trying to get RubyClass from empty stack";
         RubyModule parentModule = parentStack[parentIndex];
-        
-        return parentModule.getNonIncludedClass();
-    }
-    
-    public RubyModule getImmediateBindingRubyClass() {
-        int index = parentIndex;
-        RubyModule parentModule = null;
-        parentModule = parentStack[index];
         return parentModule.getNonIncludedClass();
     }
 
-    public RubyModule getEvalBindingRubyClass() {
-        int index = parentIndex;
-        RubyModule parentModule = null;
-        if(index == 0) {
-            parentModule = parentStack[index];
-        } else {
-            parentModule = parentStack[index-1];
-        }
+    public RubyModule getPreviousRubyClass() {
+        assert parentIndex != 0 : "Trying to get RubyClass from too-shallow stack";
+        RubyModule parentModule = parentStack[parentIndex - 1];
         return parentModule.getNonIncludedClass();
     }
     
     public boolean getConstantDefined(String internedName) {
         IRubyObject value = getConstant(internedName);
 
         return value != null;
     }
     
     /**
      * Used by the evaluator and the compiler to look up a constant by name
      */
     public IRubyObject getConstant(String internedName) {
         return getCurrentScope().getStaticScope().getConstant(runtime, internedName, runtime.getObject());
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a null const decl
      */
     public IRubyObject setConstantInCurrent(String internedName, IRubyObject result) {
         RubyModule module;
 
         if ((module = getCurrentScope().getStaticScope().getModule()) != null) {
             module.fastSetConstant(internedName, result);
             return result;
         }
 
         // TODO: wire into new exception handling mechanism
         throw runtime.newTypeError("no class/module to define constant");
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name.
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInModule(String internedName, IRubyObject target, IRubyObject result) {
         if (!(target instanceof RubyModule)) {
             throw runtime.newTypeError(target.toString() + " is not a class/module");
         }
         RubyModule module = (RubyModule)target;
         module.fastSetConstant(internedName, result);
         
         return result;
     }
     
     /**
      * Used by the evaluator and the compiler to set a constant by name
      * This is for a Colon2 const decl
      */
     public IRubyObject setConstantInObject(String internedName, IRubyObject result) {
         runtime.getObject().fastSetConstant(internedName, result);
         
         return result;
     }
     
     @Deprecated
     private static void addBackTraceElement(RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame) {
         addBackTraceElement(backtrace.getRuntime(), backtrace, frame, previousFrame);
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, Frame frame, Frame previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getLine() == previousFrame.getLine() &&
                 frame.getName() != null && 
                 frame.getName().equals(previousFrame.getName()) &&
                 frame.getFile().equals(previousFrame.getFile())) {
             return;
         }
         
         RubyString traceLine;
         if (previousFrame.getName() != null) {
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `" + previousFrame.getName() + '\'');
         } else if (runtime.is1_9()) {
             // TODO: This probably isn't the best hack, but it works until we can have different
             // root frame setup for 1.9 easily.
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1) + ":in `<main>'");
         } else {
             traceLine = RubyString.newString(runtime, frame.getFile() + ':' + (frame.getLine() + 1));
         }
         
         backtrace.append(traceLine);
     }
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getLineNumber() == previousFrame.getLineNumber() &&
                 frame.getMethodName() != null &&
                 frame.getMethodName().equals(previousFrame.getMethodName()) &&
                 frame.getFileName().equals(previousFrame.getFileName())) {
             return;
         }
         
         RubyString traceLine;
         if (previousFrame.getMethodName() == UNKNOWN_NAME) {
             traceLine = RubyString.newString(runtime, frame.getFileName() + ':' + (frame.getLineNumber()));
         } else {
             traceLine = RubyString.newString(runtime, frame.getFileName() + ':' + (frame.getLineNumber()) + ":in `" + previousFrame.getMethodName() + '\'');
         }
         
         backtrace.append(traceLine);
     }
     
     private static void addBackTraceElement(RubyArray backtrace, RubyStackTraceElement frame, RubyStackTraceElement previousFrame, FrameType frameType) {
         if (frame != previousFrame && // happens with native exceptions, should not filter those out
                 frame.getMethodName() != null && 
                 frame.getMethodName().equals(previousFrame.getMethodName()) &&
                 frame.getFileName().equals(previousFrame.getFileName()) &&
                 frame.getLineNumber() == previousFrame.getLineNumber()) {
             return;
         }
         
         StringBuilder buf = new StringBuilder(60);
         buf.append(frame.getFileName()).append(':').append(frame.getLineNumber());
         
         if (previousFrame.getMethodName() != null) {
             switch (frameType) {
             case METHOD:
                 buf.append(":in `");
                 buf.append(previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case BLOCK:
                 buf.append(":in `");
                 buf.append("block in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case EVAL:
                 buf.append(":in `");
                 buf.append("eval in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case CLASS:
                 buf.append(":in `");
                 buf.append("class in " + previousFrame.getMethodName());
                 buf.append('\'');
                 break;
             case ROOT:
                 buf.append(":in `<toplevel>'");
                 break;
             }
         }
         
         backtrace.append(backtrace.getRuntime().newString(buf.toString()));
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, RubyStackTraceElement[] backtraceFrames) {
         return createBacktraceFromFrames(runtime, backtraceFrames, true);
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createCallerBacktrace(Ruby runtime, int level) {
         int traceSize = frameIndex - level + 1;
         RubyArray backtrace = runtime.newArray(traceSize);
 
         for (int i = traceSize - 1; i > 0; i--) {
             addBackTraceElement(runtime, backtrace, frameStack[i], frameStack[i - 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public static IRubyObject createBacktraceFromFrames(Ruby runtime, RubyStackTraceElement[] backtraceFrames, boolean cropAtEval) {
         RubyArray backtrace = runtime.newArray();
         
         if (backtraceFrames == null || backtraceFrames.length <= 0) return backtrace;
         
         int traceSize = backtraceFrames.length;
 
         for (int i = 0; i < traceSize - 1; i++) {
             RubyStackTraceElement frame = backtraceFrames[i];
             // We are in eval with binding break out early
             // FIXME: This is broken with the new backtrace stuff
             if (cropAtEval && frame.isBinding()) break;
 
             addBackTraceElement(runtime, backtrace, frame, backtraceFrames[i + 1]);
         }
         
         return backtrace;
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Frame[] createBacktrace(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         Frame[] traceFrames;
         
         if (traceSize <= 0) return null;
         
         if (nativeException) {
             // assert level == 0;
             traceFrames = new Frame[traceSize + 1];
             traceFrames[traceSize] = frameStack[frameIndex];
         } else {
             traceFrames = new Frame[traceSize];
         }
         
         System.arraycopy(frameStack, 0, traceFrames, 0, traceSize);
         
         return traceFrames;
     }
 
     public static class RubyStackTraceElement {
         private StackTraceElement element;
         private boolean binding;
 
         public RubyStackTraceElement(String cls, String method, String file, int line, boolean binding) {
             element = new StackTraceElement(cls, method, file, line);
             this.binding = binding;
         }
 
         public StackTraceElement getElement() {
             return element;
         }
 
         public boolean isBinding() {
             return binding;
         }
 
         public String getClassName() {
             return element.getClassName();
         }
 
         public String getFileName() {
             return element.getFileName();
         }
 
         public int getLineNumber() {
             return element.getLineNumber();
         }
 
         public String getMethodName() {
             return element.getMethodName();
         }
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public RubyStackTraceElement[] createBacktrace2(int level, boolean nativeException) {
         int traceSize = frameIndex - level + 1;
         RubyStackTraceElement[] newTrace;
         
         if (traceSize <= 0) return null;
 
         int totalSize = traceSize;
         if (nativeException) {
             // assert level == 0;
             totalSize = traceSize + 1;
         }
         newTrace = new RubyStackTraceElement[totalSize];
 
         return buildTrace(newTrace);
     }
 
     private RubyStackTraceElement[] buildTrace(RubyStackTraceElement[] newTrace) {
         for (int i = 0; i < newTrace.length; i++) {
             Frame current = frameStack[i];
             String klazzName = getClassNameFromFrame(current);
             String methodName = getMethodNameFromFrame(current);
             newTrace[newTrace.length - 1 - i] = 
                     new RubyStackTraceElement(klazzName, methodName, current.getFile(), current.getLine() + 1, current.isBindingFrame());
         }
         
         return newTrace;
     }
 
     private String getClassNameFromFrame(Frame current) {
         String klazzName;
         if (current.getKlazz() == null) {
             klazzName = UNKNOWN_NAME;
         } else {
             klazzName = current.getKlazz().getName();
         }
         return klazzName;
     }
     
     private String getMethodNameFromFrame(Frame current) {
         String methodName = current.getName();
         if (current.getName() == null) {
             methodName = UNKNOWN_NAME;
         }
         return methodName;
     }
     
     private static String createRubyBacktraceString(StackTraceElement element) {
         return element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'";
     }
     
     public static String createRawBacktraceStringFromThrowable(Throwable t) {
         StackTraceElement[] javaStackTrace = t.getStackTrace();
         
         StringBuffer buffer = new StringBuffer();
         if (javaStackTrace != null && javaStackTrace.length > 0) {
             StackTraceElement element = javaStackTrace[0];
 
             buffer
                     .append(createRubyBacktraceString(element))
                     .append(": ")
                     .append(t.toString())
                     .append("\n");
             for (int i = 1; i < javaStackTrace.length; i++) {
                 element = javaStackTrace[i];
                 
                 buffer
                         .append("\tfrom ")
                         .append(createRubyBacktraceString(element));
                 if (i + 1 < javaStackTrace.length) buffer.append("\n");
             }
         }
         
         return buffer.toString();
     }
     
     public static IRubyObject createRawBacktrace(Ruby runtime, StackTraceElement[] stackTrace, boolean filter) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             
             if (filter) {
                 if (element.getClassName().startsWith("org.jruby") ||
                         element.getLineNumber() < 0) {
                     continue;
                 }
             }
             RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element));
             traceArray.append(str);
         }
         
         return traceArray;
     }
     
     public static IRubyObject createRubyCompiledBacktrace(Ruby runtime, StackTraceElement[] stackTrace) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         for (int i = 17; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             int index = element.getMethodName().indexOf("$RUBY$");
             if (index < 0) continue;
             String unmangledMethod = element.getMethodName().substring(index + 6);
             RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + unmangledMethod + "'");
             traceArray.append(str);
         }
         
         return traceArray;
     }
 
     private Frame pushFrameForBlock(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
-        f.setFile(file);
-        f.setLine(line);
+
+        // set the binding's frame's "previous" file and line to current, so
+        // trace will show who called the block
+        f.setFileAndLine(file, line);
+        
+        setFileAndLine(binding.getFile(), binding.getLine());
+        f.setVisibility(binding.getVisibility());
+        
+        return lastFrame;
+    }
+
+    private Frame pushFrameForEval(Binding binding) {
+        Frame lastFrame = getNextFrame();
+        Frame f = pushFrame(binding.getFrame());
+        setFileAndLine(binding.getFile(), binding.getLine());
         f.setVisibility(binding.getVisibility());
         return lastFrame;
     }
     
     public enum FrameType { METHOD, BLOCK, EVAL, CLASS, ROOT }
     public static final Map<String, FrameType> INTERPRETED_FRAMES = new HashMap<String, FrameType>();
     
     static {
         INTERPRETED_FRAMES.put(DefaultMethod.class.getName() + ".interpretedCall", FrameType.METHOD);
         
         INTERPRETED_FRAMES.put(InterpretedBlock.class.getName() + ".evalBlockBody", FrameType.BLOCK);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalWithBinding", FrameType.EVAL);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalSimple", FrameType.EVAL);
         
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".evalClassDefinitionBody", FrameType.CLASS);
         
         INTERPRETED_FRAMES.put(Ruby.class.getName() + ".runInterpreter", FrameType.ROOT);
     }
     
     public static IRubyObject createRubyHybridBacktrace(Ruby runtime, RubyStackTraceElement[] backtraceFrames, RubyStackTraceElement[] stackTrace, boolean debug) {
         RubyArray traceArray = RubyArray.newArray(runtime);
         ThreadContext context = runtime.getCurrentContext();
         
         int rubyFrameIndex = backtraceFrames.length - 1;
         for (int i = 0; i < stackTrace.length; i++) {
             RubyStackTraceElement element = stackTrace[i];
             
             // look for mangling markers for compiled Ruby in method name
             int index = element.getMethodName().indexOf("$RUBY$");
             if (index >= 0) {
                 String unmangledMethod = element.getMethodName().substring(index + 6);
                 RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + unmangledMethod + "'");
                 traceArray.append(str);
                 
                 // if it's not a rescue or ensure, there's a frame associated, so decrement
                 if (!(element.getMethodName().contains("__rescue__") || element.getMethodName().contains("__ensure__"))) {
                     rubyFrameIndex--;
                 }
                 continue;
             }
             
             // look for __file__ method name for compiled roots
             if (element.getMethodName().equals("__file__")) {
                 RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ": `<toplevel>'");
                 traceArray.append(str);
                 rubyFrameIndex--;
                 continue;
             }
             
             // look for mangling markers for bound, unframed methods in class name
             index = element.getClassName().indexOf("$RUBYINVOKER$");
             if (index >= 0) {
                 // unframed invokers have no Ruby frames, so pull from class name
                 // but use current frame as file and line
                 String unmangledMethod = element.getClassName().substring(index + 13);
                 Frame current = context.frameStack[rubyFrameIndex];
                 RubyString str = RubyString.newString(runtime, current.getFile() + ":" + (current.getLine() + 1) + ":in `" + unmangledMethod + "'");
                 traceArray.append(str);
                 continue;
             }
             
             // look for mangling markers for bound, framed methods in class name
             index = element.getClassName().indexOf("$RUBYFRAMEDINVOKER$");
             if (index >= 0) {
                 // framed invokers will have Ruby frames associated with them
                 addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex - 1], FrameType.METHOD);
                 rubyFrameIndex--;
                 continue;
             }
             
             // try to mine out a Ruby frame using our list of interpreter entry-point markers
             String classMethod = element.getClassName() + "." + element.getMethodName();
             FrameType frameType = INTERPRETED_FRAMES.get(classMethod);
             if (frameType != null) {
                 // Frame matches one of our markers for "interpreted" calls
                 if (rubyFrameIndex == 0) {
                     addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex], frameType);
                 } else {
                     addBackTraceElement(traceArray, backtraceFrames[rubyFrameIndex], backtraceFrames[rubyFrameIndex - 1], frameType);
                     rubyFrameIndex--;
                 }
                 continue;
             } else {
                 // Frame is extraneous runtime information, skip it unless debug
                 if (debug) {
                     RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element.getElement()));
                     traceArray.append(str);
                 }
                 continue;
             }
         }
         
         return traceArray;
     }
     
     public void preAdoptThread() {
         pushFrame();
         pushRubyClass(runtime.getObject());
         getCurrentFrame().setSelf(runtime.getTopSelf());
     }
     
     public void preCompiledClass(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(DynamicScope.newDynamicScope(staticScope));
     }
 
     public void preCompiledClassDummyScope(RubyModule type, StaticScope staticScope) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         staticScope.setModule(type);
         pushScope(staticScope.getDummyScope());
     }
 
     public void postCompiledClass() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preScopeNode(StaticScope staticScope) {
         pushScope(DynamicScope.newDynamicScope(staticScope, getCurrentScope()));
     }
 
     public void postScopeNode() {
         popScope();
     }
 
     public void preClassEval(StaticScope staticScope, RubyModule type) {
         pushRubyClass(type);
         pushFrameCopy();
         getCurrentFrame().setSelf(type);
         getCurrentFrame().setVisibility(Visibility.PUBLIC);
         pushScope(DynamicScope.newDynamicScope(staticScope, null));
     }
     
     public void postClassEval() {
         popScope();
         popRubyClass();
         popFrame();
     }
     
     public void preBsfApply(String[] names) {
         // FIXME: I think we need these pushed somewhere?
         LocalStaticScope staticScope = new LocalStaticScope(null);
         staticScope.setVariables(names);
         pushFrame();
     }
     
     public void postBsfApply() {
         popFrame();
     }
     
     public void preMethodFrameAndScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void preMethodFrameAndDummyScope(RubyModule clazz, String name, IRubyObject self, Block block, 
             StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushCallFrame(clazz, name, self, block);
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
 
     public void preMethodNoFrameAndDummyScope(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodFrameAndScope() {
         popRubyClass();
         popScope();
         popFrame();
     }
     
     public void preMethodFrameOnly(RubyModule clazz, String name, IRubyObject self, Block block) {
         pushRubyClass(clazz);
         pushCallFrame(clazz, name, self, block);
     }
     
     public void postMethodFrameOnly() {
         popFrame();
         popRubyClass();
     }
     
     public void preMethodScopeOnly(RubyModule clazz, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(DynamicScope.newDynamicScope(staticScope));
         pushRubyClass(implementationClass);
     }
     
     public void postMethodScopeOnly() {
         popRubyClass();
         popScope();
     }
     
     public void preMethodBacktraceAndScope(String name, RubyModule clazz, StaticScope staticScope) {
         preMethodScopeOnly(clazz, staticScope);
         pushBacktraceFrame(name);
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
         popFrame();
     }
     
     public void preMethodBacktraceOnly(String name) {
         pushBacktraceFrame(name);
     }
 
     public void preMethodBacktraceDummyScope(RubyModule clazz, String name, StaticScope staticScope) {
         pushBacktraceFrame(name);
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodBacktraceOnly() {
         popFrame();
     }
 
     public void postMethodBacktraceDummyScope() {
         popFrame();
         popRubyClass();
         popScope();
     }
     
     public void prepareTopLevel(RubyClass objectClass, IRubyObject topSelf) {
         pushFrame();
         setCurrentVisibility(Visibility.PRIVATE);
         
         pushRubyClass(objectClass);
         
         Frame frame = getCurrentFrame();
         frame.setSelf(topSelf);
         
         getCurrentScope().getStaticScope().setModule(objectClass);
     }
     
     public void preNodeEval(RubyModule rubyClass, IRubyObject self, String name) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
 
     public void preNodeEval(RubyModule rubyClass, IRubyObject self) {
         pushRubyClass(rubyClass);
         pushEvalFrame(self);
     }
     
     public void postNodeEval() {
         popFrame();
         popRubyClass();
     }
     
     // XXX: Again, screwy evaling under previous frame's scope
     public void preExecuteUnder(RubyModule executeUnderClass, Block block) {
         Frame frame = getCurrentFrame();
         
         pushRubyClass(executeUnderClass);
         DynamicScope scope = getCurrentScope();
         StaticScope sScope = new BlockStaticScope(scope.getStaticScope());
         sScope.setModule(executeUnderClass);
         pushScope(DynamicScope.newDynamicScope(sScope, scope));
         pushCallFrame(frame.getKlazz(), frame.getName(), frame.getSelf(), block);
         getCurrentFrame().setVisibility(getPreviousFrame().getVisibility());
     }
     
     public void postExecuteUnder() {
         popFrame();
         popScope();
         popRubyClass();
     }
     
     public void preMproc() {
         pushFrame();
     }
     
     public void postMproc() {
         popFrame();
     }
     
     public void preRunThread(Frame currentFrame) {
         pushFrame(currentFrame);
     }
     
     public void preTrace() {
         setWithinTrace(true);
         pushFrame();
     }
     
     public void postTrace() {
         popFrame();
         setWithinTrace(false);
     }
     
     public Frame preForBlock(Binding binding, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         pushScope(binding.getDynamicScope());
         return lastFrame;
     }
     
     public Frame preYieldSpecificBlock(Binding binding, StaticScope scope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // new scope for this invocation of the block, based on parent scope
         pushScope(DynamicScope.newDynamicScope(scope, binding.getDynamicScope()));
         return lastFrame;
     }
     
     public Frame preYieldLightBlock(Binding binding, DynamicScope emptyScope, RubyModule klass) {
         Frame lastFrame = preYieldNoScope(binding, klass);
         // just push the same empty scope, since we won't use one
         pushScope(emptyScope);
         return lastFrame;
     }
     
     public Frame preYieldNoScope(Binding binding, RubyModule klass) {
         pushRubyClass((klass != null) ? klass : binding.getKlass());
         return pushFrameForBlock(binding);
     }
     
     public void preEvalScriptlet(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postEvalScriptlet() {
         popScope();
     }
     
     public Frame preEvalWithBinding(Binding binding) {
-        Frame lastFrame = getNextFrame();
-        Frame frame = binding.getFrame();
-        frame.setIsBindingFrame(true);
-        pushFrame(frame);
-        getCurrentFrame().setVisibility(binding.getVisibility());
+        binding.getFrame().setIsBindingFrame(true);
+        Frame lastFrame = pushFrameForEval(binding);
         pushRubyClass(binding.getKlass());
         return lastFrame;
     }
     
     public void postEvalWithBinding(Binding binding, Frame lastFrame) {
         binding.getFrame().setIsBindingFrame(false);
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYield(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldLight(Binding binding, Frame lastFrame) {
         popScope();
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void postYieldNoScope(Frame lastFrame) {
         popFrameReal(lastFrame);
         popRubyClass();
     }
     
     public void preScopedBody(DynamicScope scope) {
         pushScope(scope);
     }
     
     public void postScopedBody() {
         popScope();
     }
     
     /**
      * Is this thread actively tracing at this moment.
      *
      * @return true if so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public boolean isWithinTrace() {
         return isWithinTrace;
     }
     
     /**
      * Set whether we are actively tracing or not on this thread.
      *
      * @param isWithinTrace true is so
      * @see org.jruby.Ruby#callTraceFunction(String, ISourcePosition, IRubyObject, String, IRubyObject)
      */
     public void setWithinTrace(boolean isWithinTrace) {
         this.isWithinTrace = isWithinTrace;
     }
     
     /**
      * Is this thread actively in defined? at the moment.
      *
      * @return true if within defined?
      */
     public boolean isWithinDefined() {
         return isWithinDefined;
     }
     
     /**
      * Set whether we are actively within defined? or not.
      *
      * @param isWithinDefined true if so
      */
     public void setWithinDefined(boolean isWithinDefined) {
         this.isWithinDefined = isWithinDefined;
     }
+
+    /**
+     * Return a binding representing the current call's state
+     * @return the current binding
+     */
+    public Binding currentBinding() {
+        Frame frame = getCurrentFrame();
+        return new Binding(frame, getRubyClass(), getCurrentScope(), file, line);
+    }
+
+    /**
+     * Return a binding representing the current call's state but with a specified self
+     * @param self the self object to use
+     * @return the current binding, using the specified self
+     */
+    public Binding currentBinding(IRubyObject self) {
+        Frame frame = getCurrentFrame();
+        return new Binding(self, frame, frame.getVisibility(), getRubyClass(), getCurrentScope(), file, line);
+    }
+
+    /**
+     * Return a binding representing the current call's state but with the
+     * specified visibility and self.
+     * @param self the self object to use
+     * @param visibility the visibility to use
+     * @return the current binding using the specified self and visibility
+     */
+    public Binding currentBinding(IRubyObject self, Visibility visibility) {
+        Frame frame = getCurrentFrame();
+        return new Binding(self, frame, visibility, getRubyClass(), getCurrentScope(), file, line);
+    }
+
+    /**
+     * Return a binding representing the current call's state but with the
+     * specified scope and self.
+     * @param self the self object to use
+     * @param visibility the scope to use
+     * @return the current binding using the specified self and scope
+     */
+    public Binding currentBinding(IRubyObject self, DynamicScope scope) {
+        Frame frame = getCurrentFrame();
+        return new Binding(self, frame, frame.getVisibility(), getRubyClass(), scope, file, line);
+    }
+
+    /**
+     * Return a binding representing the current call's state but with the
+     * specified visibility, scope, and self. For shared-scope binding
+     * consumers like for loops.
+     * 
+     * @param self the self object to use
+     * @param visibility the visibility to use
+     * @param scope the scope to use
+     * @return the current binding using the specified self, scope, and visibility
+     */
+    public Binding currentBinding(IRubyObject self, Visibility visibility, DynamicScope scope) {
+        Frame frame = getCurrentFrame();
+        return new Binding(self, frame, visibility, getRubyClass(), scope, file, line);
+    }
+
+    /**
+     * Return a binding representing the previous call's state
+     * @return the current binding
+     */
+    public Binding previousBinding() {
+        Frame frame = getPreviousFrame();
+        Frame current = getCurrentFrame();
+        return new Binding(frame, getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
+    }
+
+    /**
+     * Return a binding representing the previous call's state but with a specified self
+     * @param self the self object to use
+     * @return the current binding, using the specified self
+     */
+    public Binding previousBinding(IRubyObject self) {
+        Frame frame = getPreviousFrame();
+        Frame current = getCurrentFrame();
+        return new Binding(self, frame, frame.getVisibility(), getPreviousRubyClass(), getCurrentScope(), current.getFile(), current.getLine());
+    }
 }
