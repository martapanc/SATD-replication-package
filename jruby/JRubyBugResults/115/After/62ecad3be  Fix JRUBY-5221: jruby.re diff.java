diff --git a/spec/java_integration/reify/become_java_spec.rb b/spec/java_integration/reify/become_java_spec.rb
index 5826e9310d..6e05a9bba2 100644
--- a/spec/java_integration/reify/become_java_spec.rb
+++ b/spec/java_integration/reify/become_java_spec.rb
@@ -1,54 +1,54 @@
 require File.dirname(__FILE__) + "/../spec_helper"
 require 'jruby/core_ext'
 
 describe "JRuby class reification" do
   jclass = java.lang.Class
 
   class RubyRunnable
     include java.lang.Runnable
     def run
     end
   end
 
   it "should add the included Java interfaces to the reified class" do
     RubyRunnable.module_eval do
       add_method_signature("run", [java.lang.Void::TYPE])
     end
     java_class = RubyRunnable.become_java!
     java_class.interfaces.should include(java.lang.Runnable.java_class)
   end
 
   it "uses the nesting of the class for its package name" do
     class ReifyInterfacesClass1
       class ReifyInterfacesClass2
       end
     end
     ReifyInterfacesClass1.become_java!
     ReifyInterfacesClass1::ReifyInterfacesClass2.become_java!
 
-    ReifyInterfacesClass1.to_java(jclass).name.should == "ruby.ReifyInterfacesClass1"
-    ReifyInterfacesClass1::ReifyInterfacesClass2.to_java(jclass).name.should == "ruby.ReifyInterfacesClass1.ReifyInterfacesClass2"
+    ReifyInterfacesClass1.to_java(jclass).name.should == "rubyobj.ReifyInterfacesClass1"
+    ReifyInterfacesClass1::ReifyInterfacesClass2.to_java(jclass).name.should == "rubyobj.ReifyInterfacesClass1.ReifyInterfacesClass2"
 
     # checking that the packages are valid for Java's purposes
     lambda do
       ReifyInterfacesClass1.new
       ReifyInterfacesClass1::ReifyInterfacesClass2.new
     end.should_not raise_error
   end
   
   it "creates static methods for Ruby class methods" do
     cls = Class.new do
       class << self
         def blah
         end
       end
     end
     
     java_class = cls.become_java!
     
     method = java_class.declared_methods.select {|m| m.name == "blah"}[0]
     method.name.should == "blah"
     method.return_type.should == org.jruby.runtime.builtin.IRubyObject.java_class
     method.parameter_types.length.should == 0
   end
 end
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 4d087cb3c6..447941e9dc 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1615,2001 +1615,2001 @@ public final class Ruby {
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
                 new ParserConfiguration(getKCode(), lineNumber, false, false, false, config));
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
         return parser.parse(file, content, scope, new ParserConfiguration(getKCode(), lineNumber, false, false, false, config));
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
 
     private static final String FIRST_COLOR = "\033[0;31m";
     private static final String KERNEL_COLOR = "\033[0;36m";
     private static final String EVAL_COLOR = "\033[0;33m";
     private static final String CLEAR_COLOR = "\033[0m";
 
     private void printRubiniusTrace(RubyException exception) {
         ThreadContext.RubyStackTraceElement[] frames = exception.getBacktraceElements();
 
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
 
         StringBuilder buffer = new StringBuilder();
 
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
 
             if (i == 0) {
                 buffer.append(FIRST_COLOR);
             } else if (frame.isBinding() || frame.getFileName().equals("(eval)")) {
                 buffer.append(EVAL_COLOR);
             } else if (frame.getFileName().indexOf(".java") != -1) {
                 buffer.append(KERNEL_COLOR);
             }
             buffer.append("  ");
             for (int j = 0; j < center - firstPart.length(); j++) {
                 buffer.append(' ');
             }
             buffer.append(firstPart);
             buffer.append(" at ");
             buffer.append(secondPart);
             buffer.append(CLEAR_COLOR);
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
-                className = "ruby.jit.FILE_" + hash;
+                className = JITCompiler.RUBY_JIT_PREFIX + ".FILE_" + hash;
 
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
             if (script == null) {
                 Node scriptNode = parseFile(readStream, filename, null);
 
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader), false);
             }
             
             if (script == null) {
                 System.err.println("Error, could not compile; pass -J-Djruby.jit.logging.verbose=true for more details");
             }
 
             runScript(script);
         } catch (JumpException.ReturnJump rj) {
             return;
         } finally {
             context.setFile(file);
         }
     }
 
     public void loadScript(Script script) {
         IRubyObject self = getTopSelf();
         ThreadContext context = getCurrentContext();
 
         try {
             secure(4); /* should alter global state */
             
             script.load(context, self, IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         } catch (JumpException.ReturnJump rj) {
             return;
         }
     }
 
     public void addBoundMethod(String javaName, String rubyName) {
         boundMethods.put(javaName, rubyName);
     }
 
     public Map<String, String> getBoundMethods() {
         return boundMethods;
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
         tearDown(true);
     }
 
     // tearDown(boolean) has been added for embedding API. When an error
     // occurs in Ruby code, JRuby does system exit abruptly, no chance to
     // catch exception. This makes debugging really hard. This is why
     // tearDown(boolean) exists.
     public void tearDown(boolean systemExit) {
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
                         try {
                             f.finalize();
                         } catch (Throwable t) {
                             // ignore
                         }
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
                         try {
                             f.finalize();
                         } catch (Throwable t) {
                             // ignore
                         }
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
 
         if (systemExit && status != 0) {
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
 
     public RubyRational newRational(long num, long den) {
         return RubyRational.newRationalRaw(this, newFixnum(num), newFixnum(den));
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
         return newRaiseException(getArgumentError(), "wrong number of arguments (" + got + " for " + expected + ")");
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
 
     public RaiseException newErrnoECONNABORTEDError() {
         return newRaiseException(getErrno().fastGetClass("ECONNABORTED"),
                 "An established connection was aborted by the software in your host machine");
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
 
     public RaiseException newErrnoEHOSTUNREACHError(String message) {
         return newRaiseException(getErrno().fastGetClass("EHOSTUNREACH"), message);
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
 
     public RaiseException newErrnoEPERMError(String name) {
         return newRaiseException(getErrno().fastGetClass("EPERM"), "Operation not permitted - " + name);
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
 
     public RaiseException newErrnoESRCHError() {
         return newRaiseException(getErrno().fastGetClass("ESRCH"), null);
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
         RaiseException exception = new RaiseException(new RubyNoMethodError(this, getNoMethodError(), message, name, args), true);
         exception.preRaise(getCurrentContext());
         return exception;
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
         
         RaiseException exception = new RaiseException(new RubyNameError(
                 this, getNameError(), message, name), false);
         exception.preRaise(getCurrentContext());
 
         return exception;
     }
 
     public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         RaiseException exception = new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
         exception.preRaise(getCurrentContext());
         return exception;
     }
 
     public RaiseException newLocalJumpErrorNoBlock() {
         return newLocalJumpError(RubyLocalJumpError.Reason.NOREASON, getNil(), "no block given");
     }
 
     public RaiseException newRedoLocalJumpError() {
         return newLocalJumpError(RubyLocalJumpError.Reason.REDO, getNil(), "unexpected redo");
     }
 
     public RaiseException newLoadError(String message) {
         return newRaiseException(getLoadError(), message);
     }
 
     public RaiseException newFrozenError(String objectType) {
         return newFrozenError(objectType, false);
     }
 
     public RaiseException newFrozenError(String objectType, boolean runtimeError) {
         // TODO: Should frozen error have its own distinct class?  If not should more share?
         return newRaiseException(is1_9() || runtimeError ? getRuntimeError() : getTypeError(), "can't modify frozen " + objectType);
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
         RaiseException exception = new RaiseException(RubySystemExit.newInstance(this, status));
         exception.preRaise(getCurrentContext());
         return exception;
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
 
     public RaiseException newMathDomainError(String message) {
         return newRaiseException(getMathDomainError(), "Numerical argument is out of domain - \"" + message + "\"");
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
 
     public RaiseException newFiberError(String message) {
         return newRaiseException(getFiberError(), message);
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
         re.preRaise(getCurrentContext());
         return re;
     }
 
     // Equivalent of Data_Wrap_Struct
     public RubyObject.Data newData(RubyClass objectClass, Object sval) {
         return new RubyObject.Data(this, objectClass, sval);
     }
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
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
 
     private final Map<Integer, Integer> filenoExtIntMap = new HashMap<Integer, Integer>();
     private final Map<Integer, Integer> filenoIntExtMap = new HashMap<Integer, Integer>();
 
     public void putFilenoMap(int external, int internal) {
         filenoExtIntMap.put(external, internal);
         filenoIntExtMap.put(internal, external);
     }
 
     public int getFilenoExtMap(int external) {
         Integer internal = filenoExtIntMap.get(external);
         if (internal != null) return internal;
         return external;
     }
 
     public int getFilenoIntMap(int internal) {
         Integer external = filenoIntExtMap.get(internal);
         if (external != null) return external;
         return internal;
     }
 
     /**
      * Get the "external" fileno for a given ChannelDescriptor. Primarily for
      * the shared 0, 1, and 2 filenos, which we can't actually share across
      * JRuby runtimes.
      *
      * @param descriptor The descriptor for which to get the fileno
      * @return The external fileno for the descriptor
      */
     public int getFileno(ChannelDescriptor descriptor) {
         return getFilenoIntMap(descriptor.getFileno());
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor, boolean isRetained) {
     }
 
     @Deprecated
     public void registerDescriptor(ChannelDescriptor descriptor) {
     }
 
     @Deprecated
     public void unregisterDescriptor(int aFileno) {
     }
 
     @Deprecated
     public ChannelDescriptor getDescriptorByFileno(int aFileno) {
         return ChannelDescriptor.getDescriptorByFileno(aFileno);
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
 
     private static class RecursiveError extends Error implements Unrescuable {
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
diff --git a/src/org/jruby/RubyClass.java b/src/org/jruby/RubyClass.java
index 6218ddaefe..49a733028f 100644
--- a/src/org/jruby/RubyClass.java
+++ b/src/org/jruby/RubyClass.java
@@ -1,1746 +1,1772 @@
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
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Thomas E Enebo <enebo@acm.org>
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
 package org.jruby;
 
 import static org.jruby.util.CodegenUtils.ci;
 import static org.jruby.util.CodegenUtils.p;
 import static org.jruby.util.CodegenUtils.sig;
 import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
 import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
 import static org.objectweb.asm.Opcodes.ACC_STATIC;
 import static org.objectweb.asm.Opcodes.ACC_SUPER;
 import static org.objectweb.asm.Opcodes.ACC_VARARGS;
 
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
+import java.util.HashSet;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.compiler.impl.SkinnyMethodAdapter;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.internal.runtime.methods.JavaMethod;
 import org.jruby.internal.runtime.methods.UndefinedMethod;
 import org.jruby.java.codegen.RealClassGenerator;
 import org.jruby.javasupport.Java;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallSite;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ObjectMarshal;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import static org.jruby.runtime.Visibility.*;
 import static org.jruby.CompatVersion.*;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callsite.CacheEntry;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 import org.jruby.util.ClassCache.OneShotClassLoader;
 import org.jruby.util.CodegenUtils;
 import org.jruby.util.JavaNameMangler;
 import org.jruby.util.collections.WeakHashSet;
 import org.objectweb.asm.AnnotationVisitor;
 import org.objectweb.asm.ClassWriter;
 
 /**
  *
  * @author  jpetersen
  */
 @JRubyClass(name="Class", parent="Module")
 public class RubyClass extends RubyModule {
     public static void createClassClass(Ruby runtime, RubyClass classClass) {
         classClass.index = ClassIndex.CLASS;
         classClass.setReifiedClass(RubyClass.class);
         classClass.kindOf = new RubyModule.KindOf() {
             @Override
             public boolean isKindOf(IRubyObject obj, RubyModule type) {
                 return obj instanceof RubyClass;
             }
         };
         
         classClass.undefineMethod("module_function");
         classClass.undefineMethod("append_features");
         classClass.undefineMethod("extend_object");
         
         classClass.defineAnnotatedMethods(RubyClass.class);
         
         classClass.addMethod("new", new SpecificArityNew(classClass, PUBLIC));
     }
     
     public static final ObjectAllocator CLASS_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyClass clazz = new RubyClass(runtime);
             clazz.allocator = ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR; // Class.allocate object is not allocatable before it is initialized
             return clazz;
         }
     };
 
     public ObjectAllocator getAllocator() {
         return allocator;
     }
 
     public void setAllocator(ObjectAllocator allocator) {
         this.allocator = allocator;
     }
 
     /**
      * Set a reflective allocator that calls a no-arg constructor on the given
      * class.
      *
      * @param cls The class on which to call the default constructor to allocate
      */
     public void setClassAllocator(final Class cls) {
         this.allocator = new ObjectAllocator() {
             public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                 try {
                     RubyBasicObject object = (RubyBasicObject)cls.newInstance();
                     object.setMetaClass(klazz);
                     return object;
                 } catch (InstantiationException ie) {
                     throw runtime.newTypeError("could not allocate " + cls + " with default constructor:\n" + ie);
                 } catch (IllegalAccessException iae) {
                     throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible default constructor:\n" + iae);
                 }
             }
         };
         
         this.reifiedClass = cls;
     }
 
     /**
      * Set a reflective allocator that calls the "standard" Ruby object
      * constructor (Ruby, RubyClass) on the given class.
      *
      * @param cls The class from which to grab a standard Ruby constructor
      */
     public void setRubyClassAllocator(final Class cls) {
         try {
             final Constructor constructor = cls.getConstructor(Ruby.class, RubyClass.class);
             
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)constructor.newInstance(runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (InstantiationException ie) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ie);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
 
             this.reifiedClass = cls;
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     /**
      * Set a reflective allocator that calls the "standard" Ruby object
      * constructor (Ruby, RubyClass) on the given class via a static
      * __allocate__ method intermediate.
      *
      * @param cls The class from which to grab a standard Ruby __allocate__
      *            method.
      */
     public void setRubyStaticAllocator(final Class cls) {
         try {
             final Method method = cls.getDeclaredMethod("__allocate__", Ruby.class, RubyClass.class);
 
             this.allocator = new ObjectAllocator() {
                 public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                     try {
                         return (IRubyObject)method.invoke(null, runtime, klazz);
                     } catch (InvocationTargetException ite) {
                         throw runtime.newTypeError("could not allocate " + cls + " with (Ruby, RubyClass) constructor:\n" + ite);
                     } catch (IllegalAccessException iae) {
                         throw runtime.newSecurityError("could not allocate " + cls + " due to inaccessible (Ruby, RubyClass) constructor:\n" + iae);
                     }
                 }
             };
 
             this.reifiedClass = cls;
         } catch (NoSuchMethodException nsme) {
             throw new RuntimeException(nsme);
         }
     }
 
     @JRubyMethod(name = "allocate")
     public IRubyObject allocate() {
         if (superClass == null) {
             if(!(runtime.is1_9() && this == runtime.getBasicObject())) {
                 throw runtime.newTypeError("can't instantiate uninitialized class");
             }
         }
         IRubyObject obj = allocator.allocate(runtime, this);
         if (obj.getMetaClass().getRealClass() != getRealClass()) {
             throw runtime.newTypeError("wrong instance allocation");
         }
         return obj;
     }
 
     public CallSite[] getBaseCallSites() {
         return baseCallSites;
     }
     
     public CallSite[] getExtraCallSites() {
         return extraCallSites;
     }
 
     public static class VariableAccessor {
         private String name;
         private int index;
         private final int classId;
         public VariableAccessor(String name, int index, int classId) {
             this.index = index;
             this.classId = classId;
             this.name = name;
         }
         public int getClassId() {
             return classId;
         }
         public int getIndex() {
             return index;
         }
         public String getName() {
             return name;
         }
         public Object get(Object object) {
             return ((IRubyObject)object).getVariable(index);
         }
         public void set(Object object, Object value) {
             ((IRubyObject)object).setVariable(index, value);
         }
         public static final VariableAccessor DUMMY_ACCESSOR = new VariableAccessor(null, -1, -1);
     }
 
     public Map<String, VariableAccessor> getVariableAccessorsForRead() {
         return variableAccessors;
     }
     
     private volatile VariableAccessor objectIdAccessor = VariableAccessor.DUMMY_ACCESSOR;
 
     private synchronized final VariableAccessor allocateVariableAccessor(String name) {
         String[] myVariableNames = variableNames;
         int newIndex = myVariableNames.length;
         String[] newVariableNames = new String[newIndex + 1];
         VariableAccessor newVariableAccessor = new VariableAccessor(name, newIndex, this.id);
         System.arraycopy(myVariableNames, 0, newVariableNames, 0, newIndex);
         newVariableNames[newIndex] = name;
         variableNames = newVariableNames;
         return newVariableAccessor;
     }
 
     public VariableAccessor getVariableAccessorForWrite(String name) {
         VariableAccessor ivarAccessor = variableAccessors.get(name);
         if (ivarAccessor == null) {
             synchronized (this) {
                 Map<String, VariableAccessor> myVariableAccessors = variableAccessors;
                 ivarAccessor = myVariableAccessors.get(name);
 
                 if (ivarAccessor == null) {
                     // allocate a new accessor and populate a new table
                     ivarAccessor = allocateVariableAccessor(name);
                     Map<String, VariableAccessor> newVariableAccessors = new HashMap<String, VariableAccessor>(myVariableAccessors.size() + 1);
                     newVariableAccessors.putAll(myVariableAccessors);
                     newVariableAccessors.put(name, ivarAccessor);
                     variableAccessors = newVariableAccessors;
                 }
             }
         }
         return ivarAccessor;
     }
 
     public VariableAccessor getVariableAccessorForRead(String name) {
         VariableAccessor accessor = getVariableAccessorsForRead().get(name);
         if (accessor == null) accessor = VariableAccessor.DUMMY_ACCESSOR;
         return accessor;
     }
 
     public synchronized VariableAccessor getObjectIdAccessorForWrite() {
         if (objectIdAccessor == VariableAccessor.DUMMY_ACCESSOR) objectIdAccessor = allocateVariableAccessor("object_id");
         return objectIdAccessor;
     }
 
     public VariableAccessor getObjectIdAccessorForRead() {
         return objectIdAccessor;
     }
 
     public int getVariableTableSize() {
         return variableAccessors.size();
     }
 
     public int getVariableTableSizeWithObjectId() {
         return variableAccessors.size() + (objectIdAccessor == VariableAccessor.DUMMY_ACCESSOR ? 0 : 1);
     }
 
     public Map<String, VariableAccessor> getVariableTableCopy() {
         return new HashMap<String, VariableAccessor>(getVariableAccessorsForRead());
     }
 
     /**
      * Get an array of all the known instance variable names. The offset into
      * the array indicates the offset of the variable's value in the per-object
      * variable array.
      *
      * @return a copy of the array of known instance variable names
      */
     public String[] getVariableNames() {
         String[] original = variableNames;
         String[] copy = new String[original.length];
         System.arraycopy(original, 0, copy, 0, original.length);
         return copy;
     }
 
     @Override
     public int getNativeTypeIndex() {
         return ClassIndex.CLASS;
     }
     
     @Override
     public boolean isModule() {
         return false;
     }
 
     @Override
     public boolean isClass() {
         return true;
     }
 
     @Override
     public boolean isSingleton() {
         return false;
     }
 
     /** boot_defclass
      * Create an initial Object meta class before Module and Kernel dependencies have
      * squirreled themselves together.
      * 
      * @param runtime we need it
      * @return a half-baked meta class for object
      */
     public static RubyClass createBootstrapClass(Ruby runtime, String name, RubyClass superClass, ObjectAllocator allocator) {
         RubyClass obj;
 
         if (superClass == null ) {  // boot the Object class 
             obj = new RubyClass(runtime);
             obj.marshal = DEFAULT_OBJECT_MARSHAL;
         } else {                    // boot the Module and Class classes
             obj = new RubyClass(runtime, superClass);
         }
         obj.setAllocator(allocator);
         obj.setBaseName(name);
         return obj;
     }
 
     /** separate path for MetaClass and IncludedModuleWrapper construction
      *  (rb_class_boot version for MetaClasses)
      *  no marshal, allocator initialization and addSubclass(this) here!
      */
     protected RubyClass(Ruby runtime, RubyClass superClass, boolean objectSpace) {
         super(runtime, runtime.getClassClass(), objectSpace);
         this.runtime = runtime;
         setSuperClass(superClass); // this is the only case it might be null here (in MetaClass construction)
     }
     
     /** used by CLASS_ALLOCATOR (any Class' class will be a Class!)
      *  also used to bootstrap Object class
      */
     protected RubyClass(Ruby runtime) {
         super(runtime, runtime.getClassClass());
         this.runtime = runtime;
         index = ClassIndex.CLASS;
     }
     
     /** rb_class_boot (for plain Classes)
      *  also used to bootstrap Module and Class classes 
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz) {
         this(runtime);
         setSuperClass(superClazz);
         marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         allocator = superClazz.allocator;
         
         infectBy(superClass);        
     }
     
     /** 
      * A constructor which allows passing in an array of supplementary call sites.
      */
     protected RubyClass(Ruby runtime, RubyClass superClazz, CallSite[] extraCallSites) {
         this(runtime);
         setSuperClass(superClazz);
         this.marshal = superClazz.marshal; // use parent's marshal
         superClazz.addSubclass(this);
         
         this.extraCallSites = extraCallSites;
         
         infectBy(superClass);        
     }
 
     /** 
      * Construct a new class with the given name scoped under Object (global)
      * and with Object as its immediate superclass.
      * Corresponds to rb_class_new in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass);        
     }
 
     /** 
      * A variation on newClass that allow passing in an array of supplementary
      * call sites to improve dynamic invocation.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, CallSite[] extraCallSites) {
         if (superClass == runtime.getClassClass()) throw runtime.newTypeError("can't make subclass of Class");
         if (superClass.isSingleton()) throw runtime.newTypeError("can't make subclass of virtual class");
         return new RubyClass(runtime, superClass, extraCallSites);        
     }
 
     /** 
      * Construct a new class with the given name, allocator, parent class,
      * and containing class. If setParent is true, the class's parent will be
      * explicitly set to the provided parent (rather than the new class just
      * being assigned to a constant in that parent).
      * Corresponds to rb_class_new/rb_define_class_id/rb_name_class/rb_set_class_path
      * in MRI.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent) {
         RubyClass clazz = newClass(runtime, superClass);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** 
      * A variation on newClass that allows passing in an array of supplementary
      * call sites to improve dynamic invocation performance.
      */
     public static RubyClass newClass(Ruby runtime, RubyClass superClass, String name, ObjectAllocator allocator, RubyModule parent, boolean setParent, CallSite[] extraCallSites) {
         RubyClass clazz = newClass(runtime, superClass, extraCallSites);
         clazz.setBaseName(name);
         clazz.setAllocator(allocator);
         clazz.makeMetaClass(superClass.getMetaClass());
         if (setParent) clazz.setParent(parent);
         parent.setConstant(name, clazz);
         clazz.inherit(superClass);
         return clazz;
     }
 
     /** rb_make_metaclass
      *
      */
     @Override
     public RubyClass makeMetaClass(RubyClass superClass) {
         if (isSingleton()) { // could be pulled down to RubyClass in future
             MetaClass klass = new MetaClass(runtime, superClass, this); // rb_class_boot
             setMetaClass(klass);
 
             klass.setMetaClass(klass);
             klass.setSuperClass(getSuperClass().getRealClass().getMetaClass());
             
             return klass;
         } else {
             return super.makeMetaClass(superClass);
         }
     }
     
     @Deprecated
     public IRubyObject invoke(ThreadContext context, IRubyObject self, int methodIndex, String name, IRubyObject[] args, CallType callType, Block block) {
         return invoke(context, self, name, args, callType, block);
     }
     
     public boolean notVisibleAndNotMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return !method.isCallableFrom(caller, callType) && !name.equals("method_missing");
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, block);
         }
         return method.call(context, self, this, name, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, Block block) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, block);
         }
         return method.call(context, self, this, name, args, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, block);
         }
         return method.call(context, self, this, name, arg, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, block);
         }
         return method.call(context, self, this, name, arg0, arg1, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType, Block block) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, block);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2, block);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name);
     }
 
     public IRubyObject finvokeChecked(ThreadContext context, IRubyObject self, String name) {
         DynamicMethod method = searchMethod(name);
         if(method.isUndefined()) {
             DynamicMethod methodMissing = searchMethod("method_missing");
             if(methodMissing.isUndefined() || methodMissing == context.getRuntime().getDefaultMethodMissing()) {
                 return null;
             }
 
             try {
                 return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, Block.NULL_BLOCK);
             } catch(RaiseException e) {
                 if(context.getRuntime().getNoMethodError().isInstance(e.getException())) {
                     if(self.respondsTo(name)) {
                         throw e;
                     } else {
                         return null;
                     }
                 } else {
                     throw e;
                 }
             }
         }
         return method.call(context, self, this, name);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args, CallType callType) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject[] args) {
         assert args != null;
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, args, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, args);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1);
     }
     
     public IRubyObject invoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, CallType callType) {
         DynamicMethod method = searchMethod(name);
         IRubyObject caller = context.getFrameSelf();
         if (shouldCallMethodMissing(method, name, caller, callType)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, callType, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
     
     public IRubyObject finvoke(ThreadContext context, IRubyObject self, String name,
             IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
         DynamicMethod method = searchMethod(name);
         if (shouldCallMethodMissing(method)) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), name, CallType.FUNCTIONAL, arg0, arg1, arg2, Block.NULL_BLOCK);
         }
         return method.call(context, self, this, name, arg0, arg1, arg2);
     }
 
     private void dumpReifiedClass(String dumpDir, String javaPath, byte[] classBytes) {
         if (dumpDir != null) {
             if (dumpDir.equals("")) {
                 dumpDir = ".";
             }
             java.io.FileOutputStream classStream = null;
             try {
                 java.io.File classFile = new java.io.File(dumpDir, javaPath + ".class");
                 classFile.getParentFile().mkdirs();
                 classStream = new java.io.FileOutputStream(classFile);
                 classStream.write(classBytes);
             } catch (IOException io) {
                 getRuntime().getWarnings().warn("unable to dump class file: " + io.getMessage());
             } finally {
                 if (classStream != null) {
                     try {
                         classStream.close();
                     } catch (IOException ignored) {
                     }
                 }
             }
         }
     }
 
     private void generateMethodAnnotations(Map<Class, Map<String, Object>> methodAnnos, SkinnyMethodAdapter m, List<Map<Class, Map<String, Object>>> parameterAnnos) {
         if (methodAnnos != null && methodAnnos.size() != 0) {
             for (Map.Entry<Class, Map<String, Object>> entry : methodAnnos.entrySet()) {
                 m.visitAnnotationWithFields(ci(entry.getKey()), true, entry.getValue());
             }
         }
         if (parameterAnnos != null && parameterAnnos.size() != 0) {
             for (int i = 0; i < parameterAnnos.size(); i++) {
                 Map<Class, Map<String, Object>> annos = parameterAnnos.get(i);
                 if (annos != null && annos.size() != 0) {
                     for (Iterator<Map.Entry<Class, Map<String, Object>>> it = annos.entrySet().iterator(); it.hasNext();) {
                         Map.Entry<Class, Map<String, Object>> entry = it.next();
                         m.visitParameterAnnotationWithFields(i, ci(entry.getKey()), true, entry.getValue());
                     }
                 }
             }
         }
     }
     
     private boolean shouldCallMethodMissing(DynamicMethod method) {
         return method.isUndefined();
     }
     private boolean shouldCallMethodMissing(DynamicMethod method, String name, IRubyObject caller, CallType callType) {
         return method.isUndefined() || notVisibleAndNotMethodMissing(method, name, caller, callType);
     }
     
     public IRubyObject invokeInherited(ThreadContext context, IRubyObject self, IRubyObject subclass) {
         DynamicMethod method = getMetaClass().searchMethod("inherited");
 
         if (method.isUndefined()) {
             return RuntimeHelpers.callMethodMissing(context, self, method.getVisibility(), "inherited", CallType.FUNCTIONAL, Block.NULL_BLOCK);
         }
 
         return method.call(context, self, getMetaClass(), "inherited", subclass, Block.NULL_BLOCK);
     }
 
     /** rb_class_new_instance
     *
     */
     public IRubyObject newInstance(ThreadContext context, IRubyObject[] args, Block block) {
         IRubyObject obj = allocate();
         baseCallSites[CS_IDX_INITIALIZE].call(context, this, obj, args, block);
         return obj;
     }
     
     public static class SpecificArityNew extends JavaMethod {
         public SpecificArityNew(RubyModule implClass, Visibility visibility) {
             super(implClass, visibility);
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, args, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
         public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2, Block block) {
             preBacktraceOnly(context, name);
             try {
                 RubyClass cls = (RubyClass)self;
                 IRubyObject obj = cls.allocate();
                 cls.baseCallSites[CS_IDX_INITIALIZE].call(context, self, obj, arg0, arg1, arg2, block);
                 return obj;
             } finally {
                 postBacktraceOnly(context);
             }
         }
     }
 
     /** rb_class_initialize
      * 
      */
     @JRubyMethod(compat = RUBY1_8, visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, false);
     }
         
     @JRubyMethod(compat = RUBY1_8, visibility = PRIVATE)
     public IRubyObject initialize(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, false);
     }
         
     @JRubyMethod(name = "initialize", compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, Block block) {
         checkNotInitialized();
         return initializeCommon(runtime.getObject(), block, true);
     }
         
     @JRubyMethod(name = "initialize", compat = RUBY1_9, visibility = PRIVATE)
     public IRubyObject initialize19(ThreadContext context, IRubyObject superObject, Block block) {
         checkNotInitialized();
         checkInheritable(superObject);
         return initializeCommon((RubyClass)superObject, block, true);
     }
 
     private IRubyObject initializeCommon(RubyClass superClazz, Block block, boolean callInheritBeforeSuper) {
         setSuperClass(superClazz);
         allocator = superClazz.allocator;
         makeMetaClass(superClazz.getMetaClass());
 
         marshal = superClazz.marshal;
 
         superClazz.addSubclass(this);
 
         if (callInheritBeforeSuper) {
             inherit(superClazz);
             super.initialize(block);
         } else {
             super.initialize(block);
             inherit(superClazz);
         }
 
         return this;
     }
 
     /** rb_class_init_copy
      * 
      */
     @JRubyMethod(name = "initialize_copy", required = 1, visibility = PRIVATE)
     @Override
     public IRubyObject initialize_copy(IRubyObject original){
         checkNotInitialized();
         if (original instanceof MetaClass) throw runtime.newTypeError("can't copy singleton class");        
         
         super.initialize_copy(original);
         allocator = ((RubyClass)original).allocator; 
         return this;        
     }
 
     protected void setModuleSuperClass(RubyClass superClass) {
         // remove us from old superclass's child classes
         if (this.superClass != null) this.superClass.removeSubclass(this);
         // add us to new superclass's child classes
         superClass.addSubclass(this);
         // update superclass reference
         setSuperClass(superClass);
     }
     
     public Collection<RubyClass> subclasses(boolean includeDescendants) {
         Set<RubyClass> mySubclasses = subclasses;
         if (mySubclasses != null) {
             Collection<RubyClass> mine = new ArrayList<RubyClass>(mySubclasses);
             if (includeDescendants) {
                 for (RubyClass i: mySubclasses) {
                     mine.addAll(i.subclasses(includeDescendants));
                 }
             }
 
             return mine;
         } else {
             return Collections.EMPTY_LIST;
         }
     }
 
     /**
      * Add a new subclass to the weak set of subclasses.
      *
      * This version always constructs a new set to avoid having to synchronize
      * against the set when iterating it for invalidation in
      * invalidateCacheDescendants.
      *
      * @param subclass The subclass to add
      */
     public synchronized void addSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) subclasses = oldSubclasses = new WeakHashSet<RubyClass>(4);
             oldSubclasses.add(subclass);
         }
     }
     
     /**
      * Remove a subclass from the weak set of subclasses.
      *
      * @param subclass The subclass to remove
      */
     public synchronized void removeSubclass(RubyClass subclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
         }
     }
 
     /**
      * Replace an existing subclass with a new one.
      *
      * @param subclass The subclass to remove
      * @param newSubclass The subclass to replace it with
      */
     public synchronized void replaceSubclass(RubyClass subclass, RubyClass newSubclass) {
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> oldSubclasses = subclasses;
             if (oldSubclasses == null) return;
 
             oldSubclasses.remove(subclass);
             oldSubclasses.add(newSubclass);
         }
     }
 
     public void becomeSynchronized() {
         // make this class and all subclasses sync
         synchronized (getRuntime().getHierarchyLock()) {
             super.becomeSynchronized();
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.becomeSynchronized();
             }
         }
     }
 
     /**
      * Invalidate all subclasses of this class by walking the set of all
      * subclasses and asking them to invalidate themselves.
      *
      * Note that this version works against a reference to the current set of
      * subclasses, which could be replaced by the time this iteration is
      * complete. In theory, there may be a path by which invalidation would
      * miss a class added during the invalidation process, but the exposure is
      * minimal if it exists at all. The only way to prevent it would be to
      * synchronize both invalidation and subclass set modification against a
      * global lock, which we would like to avoid.
      */
     @Override
     public void invalidateCacheDescendants() {
         super.invalidateCacheDescendants();
         // update all subclasses
         synchronized (runtime.getHierarchyLock()) {
             Set<RubyClass> mySubclasses = subclasses;
             if (mySubclasses != null) for (RubyClass subclass : mySubclasses) {
                 subclass.invalidateCacheDescendants();
             }
         }
     }
     
     public Ruby getClassRuntime() {
         return runtime;
     }
 
     public RubyClass getRealClass() {
         return this;
     }    
 
     @JRubyMethod(name = "inherited", required = 1, visibility = PRIVATE)
     public IRubyObject inherited(ThreadContext context, IRubyObject arg) {
         return runtime.getNil();
     }
 
     /** rb_class_inherited (reversed semantics!)
      * 
      */
     public void inherit(RubyClass superClazz) {
         if (superClazz == null) superClazz = runtime.getObject();
 
         if (getRuntime().getNil() != null) {
             superClazz.invokeInherited(runtime.getCurrentContext(), superClazz, this);
         }
     }
 
     /** Return the real super class of this class.
      * 
      * rb_class_superclass
      *
      */    
     @JRubyMethod(name = "superclass")
     public IRubyObject superclass(ThreadContext context) {
         RubyClass superClazz = superClass;
         
         if (superClazz == null) {
             if (metaClass == runtime.getBasicObject().getMetaClass()) return runtime.getNil();
             throw runtime.newTypeError("uninitialized class");
         }
 
         while (superClazz != null && superClazz.isIncluded()) superClazz = superClazz.superClass;
 
         return superClazz != null ? superClazz : runtime.getNil();
     }
 
     @JRubyMethod(optional = 1)
     public IRubyObject __subclasses__(ThreadContext context, IRubyObject[] args) {
         boolean recursive = false;
         if (args.length > 0) {
             recursive = args[0].isTrue();
         }
 
         return RubyArray.newArray(context.getRuntime(), subclasses(recursive)).freeze(context);
     }
 
 
     private void checkNotInitialized() {
         if (superClass != null || (runtime.is1_9() && this == runtime.getBasicObject())) {
             throw runtime.newTypeError("already initialized class");
         }
     }
     /** rb_check_inheritable
      * 
      */
     public static void checkInheritable(IRubyObject superClass) {
         if (!(superClass instanceof RubyClass)) {
             throw superClass.getRuntime().newTypeError("superclass must be a Class (" + superClass.getMetaClass() + " given)"); 
         }
         if (((RubyClass)superClass).isSingleton()) {
             throw superClass.getRuntime().newTypeError("can't make subclass of virtual class");
         }        
     }
 
     public final ObjectMarshal getMarshal() {
         return marshal;
     }
     
     public final void setMarshal(ObjectMarshal marshal) {
         this.marshal = marshal;
     }
     
     public final void marshal(Object obj, MarshalStream marshalStream) throws IOException {
         getMarshal().marshalTo(runtime, obj, this, marshalStream);
     }
     
     public final Object unmarshal(UnmarshalStream unmarshalStream) throws IOException {
         return getMarshal().unmarshalFrom(runtime, this, unmarshalStream);
     }
     
     public static void marshalTo(RubyClass clazz, MarshalStream output) throws java.io.IOException {
         output.registerLinkTarget(clazz);
         output.writeString(MarshalStream.getPathFromClass(clazz));
     }
 
     public static RubyClass unmarshalFrom(UnmarshalStream input) throws java.io.IOException {
         String name = RubyString.byteListToString(input.unmarshalString());
         RubyClass result = UnmarshalStream.getClassFromPath(input.getRuntime(), name);
         input.registerLinkTarget(result);
         return result;
     }
 
     protected static final ObjectMarshal DEFAULT_OBJECT_MARSHAL = new ObjectMarshal() {
         public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                               MarshalStream marshalStream) throws IOException {
             IRubyObject object = (IRubyObject)obj;
             
             marshalStream.registerLinkTarget(object);
             marshalStream.dumpVariables(object.getVariableList());
         }
 
         public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                     UnmarshalStream unmarshalStream) throws IOException {
             IRubyObject result = type.allocate();
             
             unmarshalStream.registerLinkTarget(result);
 
             unmarshalStream.defaultVariablesUnmarshal(result);
 
             return result;
         }
     };
 
     public synchronized void reify() {
         reify(null);
     }
 
+    private static final boolean DEBUG_REIFY = false;
+
     /**
      * Stand up a real Java class for the backing store of this object
      * @param classDumpDir Directory to save reified java class
      */
     public synchronized void reify(String classDumpDir) {
         Class reifiedParent = RubyObject.class;
 
         // calculate an appropriate name, using "Anonymous####" if none is present
         String name;
         if (getBaseName() == null) {
             name = "AnonymousRubyClass#" + id;
         } else {
             name = getName();
         }
         
-        String javaName = "ruby." + name.replaceAll("::", ".");
-        String javaPath = "ruby/" + name.replaceAll("::", "/");
+        String javaName = "rubyobj." + name.replaceAll("::", ".");
+        String javaPath = "rubyobj/" + name.replaceAll("::", "/");
         OneShotClassLoader parentCL;
         if (superClass.getRealClass().getReifiedClass().getClassLoader() instanceof OneShotClassLoader) {
             parentCL = (OneShotClassLoader)superClass.getRealClass().getReifiedClass().getClassLoader();
         } else {
             parentCL = new OneShotClassLoader(runtime.getJRubyClassLoader());
         }
 
         if (superClass.reifiedClass != null) {
             reifiedParent = superClass.reifiedClass;
         }
 
         Class[] interfaces = Java.getInterfacesFromRubyClass(this);
         String[] interfaceNames = new String[interfaces.length];
         for (int i = 0; i < interfaces.length; i++) {
             interfaceNames[i] = p(interfaces[i]);
         }
 
         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
         cw.visit(RubyInstanceConfig.JAVA_VERSION, ACC_PUBLIC + ACC_SUPER, javaPath, null, p(reifiedParent),
                 interfaceNames);
 
         if (classAnnotations != null && classAnnotations.size() != 0) {
             for (Map.Entry<Class,Map<String,Object>> entry : classAnnotations.entrySet()) {
                 Class annoType = entry.getKey();
                 Map<String,Object> fields = entry.getValue();
 
                 AnnotationVisitor av = cw.visitAnnotation(ci(annoType), true);
                 CodegenUtils.visitAnnotationFields(av, fields);
                 av.visitEnd();
             }
         }
 
         // fields to hold Ruby and RubyClass references
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "ruby", ci(Ruby.class), null, null);
         cw.visitField(ACC_STATIC | ACC_PRIVATE, "rubyClass", ci(RubyClass.class), null, null);
 
         // static initializing method
         SkinnyMethodAdapter m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_STATIC, "clinit", sig(void.class, Ruby.class, RubyClass.class), null, null);
         m.start();
         m.aload(0);
         m.putstatic(javaPath, "ruby", ci(Ruby.class));
         m.aload(1);
         m.putstatic(javaPath, "rubyClass", ci(RubyClass.class));
         m.voidreturn();
         m.end();
 
         // standard constructor that accepts Ruby, RubyClass
         m = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "<init>", sig(void.class, Ruby.class, RubyClass.class), null, null);
         m.aload(0);
         m.aload(1);
         m.aload(2);
         m.invokespecial(p(reifiedParent), "<init>", sig(void.class, Ruby.class, RubyClass.class));
         m.voidreturn();
         m.end();
 
         // no-arg constructor using static references to Ruby and RubyClass
         m = new SkinnyMethodAdapter(cw, ACC_PUBLIC, "<init>", CodegenUtils.sig(void.class), null, null);
         m.aload(0);
         m.getstatic(javaPath, "ruby", ci(Ruby.class));
         m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
         m.invokespecial(p(reifiedParent), "<init>", sig(void.class, Ruby.class, RubyClass.class));
         m.voidreturn();
         m.end();
 
+        // gather a list of instance methods, so we don't accidentally make static ones that conflict
+        Set<String> instanceMethods = new HashSet<String>();
+        
         for (Map.Entry<String,DynamicMethod> methodEntry : getMethods().entrySet()) {
             String methodName = methodEntry.getKey();
             String javaMethodName = JavaNameMangler.mangleStringForCleanJavaIdentifier(methodName);
             Map<Class,Map<String,Object>> methodAnnos = getMethodAnnotations().get(methodName);
             List<Map<Class,Map<String,Object>>> parameterAnnos = getParameterAnnotations().get(methodName);
             Class[] methodSignature = getMethodSignatures().get(methodName);
 
+            String signature;
             if (methodSignature == null) {
                 // non-signature signature with just IRubyObject
                 switch (methodEntry.getValue().getArity().getValue()) {
                 case 0:
-                    m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(IRubyObject.class), null, null);
+                    signature = sig(IRubyObject.class);
+                    m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, signature, null, null);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.aload(0);
                     m.ldc(methodName);
                     m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class));
                     break;
                 default:
-                    m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(IRubyObject.class, IRubyObject[].class), null, null);
+                    signature = sig(IRubyObject.class, IRubyObject[].class);
+                    m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, signature, null, null);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.aload(0);
                     m.ldc(methodName);
                     m.aload(1);
                     m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
                 }
                 m.areturn();
             } else {
                 // generate a real method signature for the method, with to/from coercions
 
                 // indices for temp values
                 Class[] params = new Class[methodSignature.length - 1];
                 System.arraycopy(methodSignature, 1, params, 0, params.length);
                 int baseIndex = 1;
                 for (Class paramType : params) {
                     if (paramType == double.class || paramType == long.class) {
                         baseIndex += 2;
                     } else {
                         baseIndex += 1;
                     }
                 }
                 int rubyIndex = baseIndex;
 
-                m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, sig(methodSignature[0], params), null, null);
+                signature = sig(methodSignature[0], params);
+                m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS, javaMethodName, signature, null, null);
                 generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                 m.getstatic(javaPath, "ruby", ci(Ruby.class));
                 m.astore(rubyIndex);
 
                 m.aload(0); // self
                 m.ldc(methodName); // method name
                 RealClassGenerator.coerceArgumentsToRuby(m, params, rubyIndex);
                 m.invokevirtual(javaPath, "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
 
                 RealClassGenerator.coerceResultAndReturn(m, methodSignature[0]);
             }
 
+            if (DEBUG_REIFY) System.out.println("defining " + getName() + "#" + methodName + " as " + javaName + "#" + javaMethodName + signature);
+
+            instanceMethods.add(javaMethodName + signature);
+
             m.end();
         }
         
         for (Map.Entry<String,DynamicMethod> methodEntry : getMetaClass().getMethods().entrySet()) {
             String methodName = methodEntry.getKey();
             String javaMethodName = JavaNameMangler.mangleStringForCleanJavaIdentifier(methodName);
             Map<Class,Map<String,Object>> methodAnnos = getMetaClass().getMethodAnnotations().get(methodName);
             List<Map<Class,Map<String,Object>>> parameterAnnos = getMetaClass().getParameterAnnotations().get(methodName);
             Class[] methodSignature = getMetaClass().getMethodSignatures().get(methodName);
 
+            String signature;
             if (methodSignature == null) {
                 // non-signature signature with just IRubyObject
                 switch (methodEntry.getValue().getArity().getValue()) {
                 case 0:
-                    m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, sig(IRubyObject.class), null, null);
+                    signature = sig(IRubyObject.class);
+                    if (instanceMethods.contains(javaMethodName + signature)) continue;
+                    m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, signature, null, null);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
                     //m.invokevirtual("org/jruby/RubyClass", "getMetaClass", sig(RubyClass.class) );
                     m.ldc(methodName); // Method name
                     m.invokevirtual("org/jruby/RubyClass", "callMethod", sig(IRubyObject.class, String.class) );
                     break;
                 default:
-                    m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, sig(IRubyObject.class, IRubyObject[].class), null, null);
+                    signature = sig(IRubyObject.class, IRubyObject[].class);
+                    if (instanceMethods.contains(javaMethodName + signature)) continue;
+                    m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, signature, null, null);
                     generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                     m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
                     m.ldc(methodName); // Method name
                     m.aload(0);
                     m.invokevirtual("org/jruby/RubyClass", "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class) );
                 }
                 m.areturn();
             } else {
                 // generate a real method signature for the method, with to/from coercions
 
                 // indices for temp values
                 Class[] params = new Class[methodSignature.length - 1];
                 System.arraycopy(methodSignature, 1, params, 0, params.length);
                 int baseIndex = 1;
                 for (Class paramType : params) {
                     if (paramType == double.class || paramType == long.class) {
                         baseIndex += 2;
                     } else {
                         baseIndex += 1;
                     }
                 }
                 int rubyIndex = baseIndex;
 
-                m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, sig(methodSignature[0], params), null, null);
+                signature = sig(methodSignature[0], params);
+                if (instanceMethods.contains(javaMethodName + signature)) continue;
+                m = new SkinnyMethodAdapter(cw, ACC_PUBLIC | ACC_VARARGS | ACC_STATIC, javaMethodName, signature, null, null);
                 generateMethodAnnotations(methodAnnos, m, parameterAnnos);
 
                 m.getstatic(javaPath, "ruby", ci(Ruby.class));
                 m.astore(rubyIndex);
 
                 m.getstatic(javaPath, "rubyClass", ci(RubyClass.class));
                 
                 m.ldc(methodName); // method name
                 RealClassGenerator.coerceArgumentsToRuby(m, params, rubyIndex);
                 m.invokevirtual("org/jruby/RubyClass", "callMethod", sig(IRubyObject.class, String.class, IRubyObject[].class));
 
                 RealClassGenerator.coerceResultAndReturn(m, methodSignature[0]);
             }
 
+            if (DEBUG_REIFY) System.out.println("defining " + getName() + "." + methodName + " as " + javaName + "." + javaMethodName + signature);
+
             m.end();
         }
 
 
         cw.visitEnd();
         byte[] classBytes = cw.toByteArray();
         dumpReifiedClass(classDumpDir, javaPath, classBytes);
         Class result = parentCL.defineClass(javaName, classBytes);
 
         try {
             java.lang.reflect.Method clinit = result.getDeclaredMethod("clinit", Ruby.class, RubyClass.class);
             clinit.invoke(null, runtime, this);
         } catch (Exception e) {
-            throw new RuntimeException(e);
+            if (RubyInstanceConfig.REIFY_LOG_ERRORS) {
+                System.err.println("failed to reify class " + getName() + " due to:\n");
+                e.printStackTrace(System.err);
+            }
         }
 
         setClassAllocator(result);
         reifiedClass = result;
     }
 
     public void setReifiedClass(Class newReifiedClass) {
         this.reifiedClass = newReifiedClass;
     }
 
     public Class getReifiedClass() {
         return reifiedClass;
     }
 
     public Map<String, List<Map<Class, Map<String,Object>>>> getParameterAnnotations() {
         if (parameterAnnotations == null) return Collections.EMPTY_MAP;
         return parameterAnnotations;
     }
 
     public synchronized void addParameterAnnotation(String method, int i, Class annoClass, Map<String,Object> value) {
         if (parameterAnnotations == null) parameterAnnotations = new Hashtable<String,List<Map<Class,Map<String,Object>>>>();
         List<Map<Class,Map<String,Object>>> paramList = parameterAnnotations.get(method);
         if (paramList == null) {
             paramList = new ArrayList<Map<Class,Map<String,Object>>>(i + 1);
             parameterAnnotations.put(method, paramList);
         }
         if (paramList.size() < i + 1) {
             for (int j = paramList.size(); j < i + 1; j++) {
                 paramList.add(null);
             }
         }
         if (annoClass != null && value != null) {
             Map<Class, Map<String, Object>> annos = paramList.get(i);
             if (annos == null) {
                 annos = new HashMap<Class, Map<String, Object>>();
                 paramList.set(i, annos);
             }
             annos.put(annoClass, value);
         } else {
             paramList.set(i, null);
         }
     }
 
     public Map<String,Map<Class,Map<String,Object>>> getMethodAnnotations() {
         if (methodAnnotations == null) return Collections.EMPTY_MAP;
 
         return methodAnnotations;
     }
 
     public synchronized void addMethodAnnotation(String methodName, Class annotation, Map fields) {
         if (methodAnnotations == null) methodAnnotations = new Hashtable<String,Map<Class,Map<String,Object>>>();
 
         Map<Class,Map<String,Object>> annos = methodAnnotations.get(methodName);
         if (annos == null) {
             annos = new Hashtable<Class,Map<String,Object>>();
             methodAnnotations.put(methodName, annos);
         }
 
         annos.put(annotation, fields);
     }
 
     public Map<String,Class[]> getMethodSignatures() {
         if (methodSignatures == null) return Collections.EMPTY_MAP;
 
         return methodSignatures;
     }
 
     public synchronized void addMethodSignature(String methodName, Class[] types) {
         if (methodSignatures == null) methodSignatures = new Hashtable<String,Class[]>();
 
         methodSignatures.put(methodName, types);
     }
 
     public Map<Class,Map<String,Object>> getClassAnnotations() {
         if (classAnnotations == null) return Collections.EMPTY_MAP;
 
         return classAnnotations;
     }
 
     public synchronized void addClassAnnotation(Class annotation, Map fields) {
         if (classAnnotations == null) classAnnotations = new Hashtable<Class,Map<String,Object>>();
 
         classAnnotations.put(annotation, fields);
     }
 
     @Override
     public Object toJava(Class klass) {
         Class returnClass = null;
 
         if (klass == Class.class) {
             // Class requested; try java_class or else return nearest reified class
             if (respondsTo("java_class")) {
                 return callMethod("java_class").toJava(klass);
             } else {
                 for (RubyClass current = this; current != null; current = current.getSuperClass()) {
                     returnClass = current.getReifiedClass();
                     if (returnClass != null) return returnClass;
                 }
             }
             // should never fall through, since RubyObject has a reified class
         }
 
         if (klass.isAssignableFrom(RubyClass.class)) {
             // they're asking for something RubyClass extends, give them that
             return this;
         }
 
         return super.toJava(klass);
     }
 
     /**
      * An enum defining the type of marshaling a given class's objects employ.
      */
     private static enum MarshalType {
         DEFAULT, NEW_USER, OLD_USER, DEFAULT_SLOW, NEW_USER_SLOW, USER_SLOW
     }
 
     /**
      * A tuple representing the mechanism by which objects should be marshaled.
      *
      * This tuple caches the type of marshaling to perform (from @MarshalType),
      * the method to be used for marshaling data (either marshal_load/dump or
      * _load/_dump), and the generation of the class at the time this tuple was
      * created. When "dump" or "load" are invoked, they either call the default
      * marshaling logic (@MarshalType.DEFAULT) or they further invoke the cached
      * marshal_dump/load or _dump/_load methods to marshal the data.
      *
      * It is expected that code outside MarshalTuple will validate that the
      * generation number still matches before invoking load or dump.
      */
     private static class MarshalTuple {
         /**
          * Construct a new MarshalTuple with the given values.
          *
          * @param method The method to invoke, or null in the case of default
          * marshaling.
          * @param type The type of marshaling to perform, from @MarshalType
          * @param generation The generation of the associated class at the time
          * of creation.
          */
         public MarshalTuple(DynamicMethod method, MarshalType type, int generation) {
             this.method = method;
             this.type = type;
             this.generation = generation;
         }
 
         /**
          * Dump the given object to the given stream, using the appropriate
          * marshaling method.
          *
          * @param stream The stream to which to dump
          * @param object The object to dump
          * @throws IOException If there is an IO error during dumping
          */
         public void dump(MarshalStream stream, IRubyObject object) throws IOException {
             switch (type) {
                 case DEFAULT:
                     stream.writeDirectly(object);
                     return;
                 case NEW_USER:
                     stream.userNewMarshal(object, method);
                     return;
                 case OLD_USER:
                     stream.userMarshal(object, method);
                     return;
                 case DEFAULT_SLOW:
                     if (object.respondsTo("marshal_dump")) {
                         stream.userNewMarshal(object);
                     } else if (object.respondsTo("_dump")) {
                         stream.userMarshal(object);
                     } else {
                         stream.writeDirectly(object);
                     }
                     return;
             }
         }
 
         /** A "null" tuple, used as the default value for caches. */
         public static final MarshalTuple NULL_TUPLE = new MarshalTuple(null, null, 0);
         /** The method associated with this tuple. */
         public final DynamicMethod method;
         /** The type of marshaling that will be performed */
         public final MarshalType type;
         /** The generation of the associated class at the time of creation */
         public final int generation;
     }
 
     /**
      * Marshal the given object to the marshaling stream, being "smart" and
      * caching how to do that marshaling.
      *
      * If the class defines a custom "respond_to?" method, then the behavior of
      * dumping could vary without our class structure knowing it. As a result,
      * we do only the slow-path classic behavior.
      *
      * If the class defines a real "marshal_dump" method, we cache and use that.
      *
      * If the class defines a real "_dump" method, we cache and use that.
      *
      * If the class neither defines none of the above methods, we use a fast
      * path directly to the default dumping logic.
      *
      * @param stream The stream to which to marshal the data
      * @param target The object whose data should be marshaled
      * @throws IOException If there is an IO exception while writing to the
      * stream.
      */
     public void smartDump(MarshalStream stream, IRubyObject target) throws IOException {
         MarshalTuple tuple;
         if ((tuple = cachedDumpMarshal).generation == generation) {
         } else {
             // recache
             DynamicMethod method = searchMethod("respond_to?");
             if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
 
                 // custom respond_to?, always do slow default marshaling
                 tuple = (cachedDumpMarshal = new MarshalTuple(null, MarshalType.DEFAULT_SLOW, generation));
 
             } else if (!(method = searchMethod("marshal_dump")).isUndefined()) {
 
                 // object really has 'marshal_dump', cache "new" user marshaling
                 tuple = (cachedDumpMarshal = new MarshalTuple(method, MarshalType.NEW_USER, generation));
 
             } else if (!(method = searchMethod("_dump")).isUndefined()) {
 
                 // object really has '_dump', cache "old" user marshaling
                 tuple = (cachedDumpMarshal = new MarshalTuple(method, MarshalType.OLD_USER, generation));
 
             } else {
 
                 // no respond_to?, marshal_dump, or _dump, so cache default marshaling
                 tuple = (cachedDumpMarshal = new MarshalTuple(null, MarshalType.DEFAULT, generation));
             }
         }
 
         tuple.dump(stream, target);
     }
 
     /**
      * Load marshaled data into a blank target object using marshal_load, being
      * "smart" and caching the mechanism for invoking marshal_load.
      *
      * If the class implements a custom respond_to?, cache nothing and go slow
      * path invocation of respond_to? and marshal_load every time. Raise error
      * if respond_to? :marshal_load returns true and no :marshal_load is
      * defined.
      *
      * If the class implements marshal_load, cache and use that.
      *
      * Otherwise, error, since marshal_load is not present.
      *
      * @param target The blank target object into which marshal_load will
      * deserialize the given data
      * @param data The marshaled data
      * @return The fully-populated target object
      */
     public IRubyObject smartLoadNewUser(IRubyObject target, IRubyObject data) {
         ThreadContext context = runtime.getCurrentContext();
         CacheEntry cache;
         if ((cache = cachedLoad).token == generation) {
             cache.method.call(context, target, this, "marshal_load", data);
             return target;
         } else {
             DynamicMethod method = searchMethod("respond_to?");
             if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
 
                 // custom respond_to?, cache nothing and use slow path
                 if (method.call(context, target, this, "respond_to?", runtime.newSymbol("marshal_load")).isTrue()) {
                     target.callMethod(context, "marshal_load", data);
                     return target;
                 } else {
                     throw runtime.newTypeError("class " + getName() + " needs to have method `marshal_load'");
                 }
 
             } else if (!(cache = searchWithCache("marshal_load")).method.isUndefined()) {
 
                 // real marshal_load defined, cache and call it
                 cachedLoad = cache;
                 cache.method.call(context, target, this, "marshal_load", data);
                 return target;
 
             } else {
 
                 // go ahead and call, method_missing might handle it
                 target.callMethod(context, "marshal_load", data);
                 return target;
                 
             }
         }
     }
 
 
     /**
      * Load marshaled data into a blank target object using _load, being
      * "smart" and caching the mechanism for invoking _load.
      *
      * If the metaclass implements custom respond_to?, cache nothing and go slow
      * path invocation of respond_to? and _load every time. Raise error if
      * respond_to? :_load returns true and no :_load is defined.
      *
      * If the metaclass implements _load, cache and use that.
      *
      * Otherwise, error, since _load is not present.
      *
      * @param data The marshaled data, to be reconstituted into an object by
      * _load
      * @return The fully-populated target object
      */
     public IRubyObject smartLoadOldUser(IRubyObject data) {
         ThreadContext context = runtime.getCurrentContext();
         CacheEntry cache;
         if ((cache = getSingletonClass().cachedLoad).token == getSingletonClass().generation) {
             return cache.method.call(context, this, getSingletonClass(), "_load", data);
         } else {
             DynamicMethod method = getSingletonClass().searchMethod("respond_to?");
             if (method != runtime.getRespondToMethod() && !method.isUndefined()) {
 
                 // custom respond_to?, cache nothing and use slow path
                 if (method.call(context, this, getSingletonClass(), "respond_to?", runtime.newSymbol("_load")).isTrue()) {
                     return callMethod(context, "_load", data);
                 } else {
                     throw runtime.newTypeError("class " + getName() + " needs to have method `_load'");
                 }
 
             } else if (!(cache = getSingletonClass().searchWithCache("_load")).method.isUndefined()) {
 
                 // real _load defined, cache and call it
                 getSingletonClass().cachedLoad = cache;
                 return cache.method.call(context, this, getSingletonClass(), "_load", data);
 
             } else {
 
                 // provide an error, since it doesn't exist
                 throw runtime.newTypeError("class " + getName() + " needs to have method `_load'");
 
             }
         }
     }
 
     protected final Ruby runtime;
     private ObjectAllocator allocator; // the default allocator
     protected ObjectMarshal marshal;
     private Set<RubyClass> subclasses;
     public static final int CS_IDX_INITIALIZE = 0;
     public static final String[] CS_NAMES = {
         "initialize"
     };
     private final CallSite[] baseCallSites = new CallSite[CS_NAMES.length];
     {
         for(int i = 0; i < CS_NAMES.length; i++) {
             baseCallSites[i] = MethodIndex.getFunctionalCallSite(CS_NAMES[i]);
         }
     }
 
     private CallSite[] extraCallSites;
 
     private Class reifiedClass;
 
     @SuppressWarnings("unchecked")
     private static String[] EMPTY_STRING_ARRAY = new String[0];
     private Map<String, VariableAccessor> variableAccessors = (Map<String, VariableAccessor>)Collections.EMPTY_MAP;
     private volatile String[] variableNames = EMPTY_STRING_ARRAY;
 
     private volatile boolean hasObjectID = false;
     public boolean hasObjectID() {
         return hasObjectID;
     }
 
     private Map<String, List<Map<Class, Map<String,Object>>>> parameterAnnotations;
 
     private Map<String, Map<Class, Map<String,Object>>> methodAnnotations;
 
     private Map<String, Class[]> methodSignatures;
 
     private Map<Class, Map<String,Object>> classAnnotations;
 
     /** A cached tuple of method, type, and generation for dumping */
     private MarshalTuple cachedDumpMarshal = MarshalTuple.NULL_TUPLE;
 
     /** A cached tuple of method and generation for marshal loading */
     private CacheEntry cachedLoad = CacheEntry.NULL_CACHE;
 }
diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index 46f7f922a1..18011c1749 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -1,1594 +1,1601 @@
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
  * Copyright (C) 2007-2010 Nick Sieger <nicksieger@gmail.com>
  * Copyright (C) 2009 Joseph LaFata <joe@quibb.org>
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
 
 import java.io.BufferedInputStream;
 import java.io.BufferedReader;
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.net.URI;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 
 import org.jruby.ast.executable.Script;
 import org.jruby.compiler.ASTCompiler;
 import org.jruby.compiler.ASTCompiler19;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.ext.posix.util.Platform;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.runtime.load.LoadService19;
 import org.jruby.util.ClassCache;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.KCode;
 import org.jruby.util.NormalizedFile;
 import org.jruby.util.SafePropertyAccessor;
 import org.objectweb.asm.Opcodes;
 
 public class RubyInstanceConfig {
 
     /**
      * The max count of active methods eligible for JIT-compilation.
      */
     public static final int JIT_MAX_METHODS_LIMIT = 4096;
 
     /**
      * The max size of JIT-compiled methods (full class size) allowed.
      */
     public static final int JIT_MAX_SIZE_LIMIT = 10000;
 
     /**
      * The JIT threshold to the specified method invocation count.
      */
     public static final int JIT_THRESHOLD = 50;
     
     /** The version to use for generated classes. Set to current JVM version by default */
     public static final int JAVA_VERSION;
     
     /**
      * Default size for chained compilation.
      */
     public static final int CHAINED_COMPILE_LINE_COUNT_DEFAULT = 500;
     
     /**
      * The number of lines at which a method, class, or block body is split into
      * chained methods (to dodge 64k method-size limit in JVM).
      */
     public static final int CHAINED_COMPILE_LINE_COUNT
             = SafePropertyAccessor.getInt("jruby.compile.chainsize", CHAINED_COMPILE_LINE_COUNT_DEFAULT);
 
     /**
      * Indicates whether the script must be extracted from script source
      */
     private boolean xFlag;
 
     public boolean hasShebangLine() {
         return hasShebangLine;
     }
 
     public void setHasShebangLine(boolean hasShebangLine) {
         this.hasShebangLine = hasShebangLine;
     }
 
     /**
      * Indicates whether the script has a shebang line or not
      */
     private boolean hasShebangLine;
 
     public boolean isxFlag() {
         return xFlag;
     }
 
     public enum CompileMode {
         JIT, FORCE, OFF, OFFIR;
 
         public boolean shouldPrecompileCLI() {
             switch (this) {
             case JIT: case FORCE:
                 if (DYNOPT_COMPILE_ENABLED) {
                     // don't precompile the CLI script in dynopt mode
                     return false;
                 }
                 return true;
             }
             return false;
         }
 
         public boolean shouldJIT() {
             switch (this) {
             case JIT: case FORCE:
                 return true;
             }
             return false;
         }
 
         public boolean shouldPrecompileAll() {
             return this == FORCE;
         }
     }
     private InputStream input          = System.in;
     private PrintStream output         = System.out;
     private PrintStream error          = System.err;
     private Profile profile            = Profile.DEFAULT;
     private boolean objectSpaceEnabled
             = SafePropertyAccessor.getBoolean("jruby.objectspace.enabled", false);
 
     private CompileMode compileMode = CompileMode.JIT;
     private boolean runRubyInProcess   = true;
     private String currentDirectory;
     private Map environment;
     private String[] argv = {};
 
     private final boolean jitLogging;
     private final boolean jitDumping;
     private final boolean jitLoggingVerbose;
     private int jitLogEvery;
     private int jitThreshold;
     private int jitMax;
     private int jitMaxSize;
     private final boolean samplingEnabled;
     private CompatVersion compatVersion;
     private boolean profiling;
 
     private ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
     private ClassLoader loader = contextLoader == null ? RubyInstanceConfig.class.getClassLoader() : contextLoader;
 
     private ClassCache<Script> classCache;
 
     // from CommandlineParser
     private List<String> loadPaths = new ArrayList<String>();
     private Set<String> excludedMethods = new HashSet<String>();
     private StringBuffer inlineScript = new StringBuffer();
     private boolean hasInlineScript = false;
     private String scriptFileName = null;
     private Collection<String> requiredLibraries = new LinkedHashSet<String>();
     private boolean benchmarking = false;
     private boolean argvGlobalsOn = false;
     private boolean assumeLoop = false;
     private boolean assumePrinting = false;
     private Map optionGlobals = new HashMap();
     private boolean processLineEnds = false;
     private boolean split = false;
     // This property is a Boolean, to allow three values, so it can match MRI's nil, false and true
     private Boolean verbose = Boolean.FALSE;
     private boolean debug = false;
     private boolean showVersion = false;
     private boolean showBytecode = false;
     private boolean showCopyright = false;
     private boolean endOfArguments = false;
     private boolean shouldRunInterpreter = true;
     private boolean shouldPrintUsage = false;
     private boolean shouldPrintProperties=false;
     private KCode kcode = KCode.NONE;
     private String recordSeparator = "\n";
     private boolean shouldCheckSyntax = false;
     private String inputFieldSeparator = null;
     private boolean managementEnabled = false;
     private String inPlaceBackupExtension = null;
     private boolean parserDebug = false;
     private String threadDumpSignal = null;
     private boolean hardExit = false;
 
     private int safeLevel = 0;
 
     private String jrubyHome;
 
     public static final boolean PEEPHOLE_OPTZ
             = SafePropertyAccessor.getBoolean("jruby.compile.peephole", true);
     public static boolean DYNOPT_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.dynopt", false);
     public static boolean NOGUARDS_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.noguards");
     public static boolean FASTEST_COMPILE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.compile.fastest");
     public static boolean FASTOPS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastops");
     public static boolean FRAMELESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.frameless");
     public static boolean POSITIONLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.positionless", true);
     public static boolean THREADLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.threadless");
     public static boolean FASTCASE_COMPILE_ENABLED =
             SafePropertyAccessor.getBoolean("jruby.compile.fastcase");
     public static boolean FASTSEND_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.fastsend");
     public static boolean LAZYHANDLES_COMPILE = SafePropertyAccessor.getBoolean("jruby.compile.lazyHandles", false);
     public static boolean INLINE_DYNCALL_ENABLED
             = FASTEST_COMPILE_ENABLED
             || SafePropertyAccessor.getBoolean("jruby.compile.inlineDyncalls");
     public static final boolean FORK_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.fork.enabled");
     public static final boolean POOLING_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.thread.pool.enabled");
     public static final int POOL_MAX
             = SafePropertyAccessor.getInt("jruby.thread.pool.max", Integer.MAX_VALUE);
     public static final int POOL_MIN
             = SafePropertyAccessor.getInt("jruby.thread.pool.min", 0);
     public static final int POOL_TTL
             = SafePropertyAccessor.getInt("jruby.thread.pool.ttl", 60);
 
     public static final boolean NATIVE_NET_PROTOCOL
             = SafePropertyAccessor.getBoolean("jruby.native.net.protocol", false);
 
     public static boolean FULL_TRACE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.debug.fullTrace", false);
 
     public static final String COMPILE_EXCLUDE
             = SafePropertyAccessor.getProperty("jruby.jit.exclude");
     public static boolean nativeEnabled = true;
 
     public static final boolean REIFY_RUBY_CLASSES
             = SafePropertyAccessor.getBoolean("jruby.reify.classes", false);
 
+    public static final boolean REIFY_LOG_ERRORS
+            = SafePropertyAccessor.getBoolean("jruby.reify.logErrors", false);
+
     public static final boolean USE_GENERATED_HANDLES
             = SafePropertyAccessor.getBoolean("jruby.java.handles", false);
 
     public static final boolean DEBUG_LOAD_SERVICE
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService", false);
 
     public static final boolean DEBUG_LOAD_TIMINGS
             = SafePropertyAccessor.getBoolean("jruby.debug.loadService.timing", false);
 
     public static final boolean DEBUG_LAUNCHING
             = SafePropertyAccessor.getBoolean("jruby.debug.launch", false);
 
     public static final boolean DEBUG_SCRIPT_RESOLUTION
             = SafePropertyAccessor.getBoolean("jruby.debug.scriptResolution", false);
 
     public static final boolean JUMPS_HAVE_BACKTRACE
             = SafePropertyAccessor.getBoolean("jruby.jump.backtrace", false);
 
     public static final boolean JIT_CACHE_ENABLED
             = SafePropertyAccessor.getBoolean("jruby.jit.cache", true);
 
     public static final String JIT_CODE_CACHE
             = SafePropertyAccessor.getProperty("jruby.jit.codeCache", null);
 
     public static final boolean REFLECTED_HANDLES
             = SafePropertyAccessor.getBoolean("jruby.reflected.handles", false)
             || SafePropertyAccessor.getBoolean("jruby.reflection", false);
 
     public static final boolean NO_UNWRAP_PROCESS_STREAMS
             = SafePropertyAccessor.getBoolean("jruby.process.noUnwrap", false);
 
     public static final boolean INTERFACES_USE_PROXY
             = SafePropertyAccessor.getBoolean("jruby.interfaces.useProxy");
 
     public static final boolean JIT_LOADING_DEBUG = SafePropertyAccessor.getBoolean("jruby.jit.debug", false);
 
     public static final boolean CAN_SET_ACCESSIBLE = SafePropertyAccessor.getBoolean("jruby.ji.setAccessible", true);
 
     public static interface LoadServiceCreator {
         LoadService create(Ruby runtime);
 
         LoadServiceCreator DEFAULT = new LoadServiceCreator() {
                 public LoadService create(Ruby runtime) {
                     if (runtime.is1_9()) {
                         return new LoadService19(runtime);
                     }
                     return new LoadService(runtime);
                 }
             };
     }
 
     private LoadServiceCreator creator = LoadServiceCreator.DEFAULT;
 
 
     static {
         String specVersion = null;
         try {
             specVersion = System.getProperty("jruby.bytecode.version");
             if (specVersion == null) {
                 specVersion = System.getProperty("java.specification.version");
             }
             if (System.getProperty("jruby.native.enabled") != null) {
                 nativeEnabled = Boolean.getBoolean("jruby.native.enabled");
             }
         } catch (SecurityException se) {
             nativeEnabled = false;
             specVersion = "1.5";
         }
         
         if (specVersion.equals("1.5")) {
             JAVA_VERSION = Opcodes.V1_5;
         } else {
             JAVA_VERSION = Opcodes.V1_6;
         }
     }
 
     public int characterIndex = 0;
 
     public RubyInstanceConfig(RubyInstanceConfig parentConfig) {
         setCurrentDirectory(parentConfig.getCurrentDirectory());
         samplingEnabled = parentConfig.samplingEnabled;
         compatVersion = parentConfig.compatVersion;
         compileMode = parentConfig.getCompileMode();
         jitLogging = parentConfig.jitLogging;
         jitDumping = parentConfig.jitDumping;
         jitLoggingVerbose = parentConfig.jitLoggingVerbose;
         jitLogEvery = parentConfig.jitLogEvery;
         jitThreshold = parentConfig.jitThreshold;
         jitMax = parentConfig.jitMax;
         jitMaxSize = parentConfig.jitMaxSize;
         managementEnabled = parentConfig.managementEnabled;
         runRubyInProcess = parentConfig.runRubyInProcess;
         excludedMethods = parentConfig.excludedMethods;
         threadDumpSignal = parentConfig.threadDumpSignal;
         
         classCache = new ClassCache<Script>(loader, jitMax);
     }
 
     public RubyInstanceConfig() {
         setCurrentDirectory(Ruby.isSecurityRestricted() ? "/" : JRubyFile.getFileProperty("user.dir"));
         samplingEnabled = SafePropertyAccessor.getBoolean("jruby.sampling.enabled", false);
 
         String compatString = SafePropertyAccessor.getProperty("jruby.compat.version", "RUBY1_8");
         if (compatString.equalsIgnoreCase("RUBY1_8")) {
             setCompatVersion(CompatVersion.RUBY1_8);
         } else if (compatString.equalsIgnoreCase("RUBY1_9")) {
             setCompatVersion(CompatVersion.RUBY1_9);
         } else {
             error.println("Compatibility version `" + compatString + "' invalid; use RUBY1_8 or RUBY1_9. Using RUBY1_8.");
             setCompatVersion(CompatVersion.RUBY1_8);
         }
 
         if (Ruby.isSecurityRestricted()) {
             compileMode = CompileMode.OFF;
             jitLogging = false;
             jitDumping = false;
             jitLoggingVerbose = false;
             jitLogEvery = 0;
             jitThreshold = -1;
             jitMax = 0;
             jitMaxSize = -1;
             managementEnabled = false;
         } else {
             String threshold = SafePropertyAccessor.getProperty("jruby.jit.threshold");
             String max = SafePropertyAccessor.getProperty("jruby.jit.max");
             String maxSize = SafePropertyAccessor.getProperty("jruby.jit.maxsize");
             
             if (COMPILE_EXCLUDE != null) {
                 String[] elements = COMPILE_EXCLUDE.split(",");
                 excludedMethods.addAll(Arrays.asList(elements));
             }
             
             managementEnabled = SafePropertyAccessor.getBoolean("jruby.management.enabled", false);
             runRubyInProcess = SafePropertyAccessor.getBoolean("jruby.launch.inproc", true);
             boolean jitProperty = SafePropertyAccessor.getProperty("jruby.jit.enabled") != null;
             if (jitProperty) {
                 error.print("jruby.jit.enabled property is deprecated; use jruby.compile.mode=(OFF|JIT|FORCE) for -C, default, and +C flags");
                 compileMode = SafePropertyAccessor.getBoolean("jruby.jit.enabled") ? CompileMode.JIT : CompileMode.OFF;
             } else {
                 String jitModeProperty = SafePropertyAccessor.getProperty("jruby.compile.mode", "JIT");
 
                 if (jitModeProperty.equals("OFF")) {
                     compileMode = CompileMode.OFF;
                 } else if (jitModeProperty.equals("JIT")) {
                     compileMode = CompileMode.JIT;
                 } else if (jitModeProperty.equals("FORCE")) {
                     compileMode = CompileMode.FORCE;
                 } else {
                     error.print("jruby.compile.mode property must be OFF, JIT, FORCE, or unset; defaulting to JIT");
                     compileMode = CompileMode.JIT;
                 }
             }
             jitLogging = SafePropertyAccessor.getBoolean("jruby.jit.logging");
             jitDumping = SafePropertyAccessor.getBoolean("jruby.jit.dumping");
             jitLoggingVerbose = SafePropertyAccessor.getBoolean("jruby.jit.logging.verbose");
             String logEvery = SafePropertyAccessor.getProperty("jruby.jit.logEvery");
             jitLogEvery = logEvery == null ? 0 : Integer.parseInt(logEvery);
             jitThreshold = threshold == null ?
                     JIT_THRESHOLD : Integer.parseInt(threshold);
             jitMax = max == null ?
                     JIT_MAX_METHODS_LIMIT : Integer.parseInt(max);
             jitMaxSize = maxSize == null ?
                     JIT_MAX_SIZE_LIMIT : Integer.parseInt(maxSize);
         }
 
         // default ClassCache using jitMax as a soft upper bound
         classCache = new ClassCache<Script>(loader, jitMax);
         threadDumpSignal = SafePropertyAccessor.getProperty("jruby.thread.dump.signal", "USR2");
 
         if (FORK_ENABLED) {
             error.print("WARNING: fork is highly unlikely to be safe or stable on the JVM. Have fun!\n");
         }
     }
 
     public LoadServiceCreator getLoadServiceCreator() {
         return creator;
     }
 
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         this.creator = creator;
     }
 
     public LoadService createLoadService(Ruby runtime) {
         return this.creator.create(runtime);
     }
 
     public String getBasicUsageHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("Usage: jruby [switches] [--] [programfile] [arguments]\n")
                 .append("  -0[octal]       specify record separator (\\0, if no argument)\n")
                 .append("  -a              autosplit mode with -n or -p (splits $_ into $F)\n")
                 .append("  -b              benchmark mode, times the script execution\n")
                 .append("  -c              check syntax only\n")
                 .append("  -Cdirectory     cd to directory, before executing your script\n")
                 .append("  -d              set debugging flags (set $DEBUG to true)\n")
                 .append("  -e 'command'    one line of script. Several -e's allowed. Omit [programfile]\n")
                 .append("  -Fpattern       split() pattern for autosplit (-a)\n")
                 .append("  -i[extension]   edit ARGV files in place (make backup if extension supplied)\n")
                 .append("  -Idirectory     specify $LOAD_PATH directory (may be used more than once)\n")
                 .append("  -J[java option] pass an option on to the JVM (e.g. -J-Xmx512m)\n")
                 .append("                    use --properties to list JRuby properties\n")
                 .append("                    run 'java -help' for a list of other Java options\n")
                 .append("  -Kkcode         specifies code-set (e.g. -Ku for Unicode, -Ke for EUC and -Ks for SJIS)\n")
                 .append("  -l              enable line ending processing\n")
                 .append("  -n              assume 'while gets(); ... end' loop around your script\n")
                 .append("  -p              assume loop like -n but print line also like sed\n")
                 .append("  -rlibrary       require the library, before executing your script\n")
                 .append("  -s              enable some switch parsing for switches after script name\n")
                 .append("  -S              look for the script in bin or using PATH environment variable\n")
                 .append("  -T[level]       turn on tainting checks\n")
                 .append("  -v              print version number, then turn on verbose mode\n")
                 .append("  -w              turn warnings on for your script\n")
                 .append("  -W[level]       set warning level; 0=silence, 1=medium, 2=verbose (default)\n")
                 .append("  -x[directory]   strip off text before #!ruby line and perhaps cd to directory\n")
                 .append("  -X[option]      enable extended option (omit option to list)\n")
                 .append("  -y              enable parsing debug output\n")
                 .append("  --copyright     print the copyright\n")
                 .append("  --debug         sets the execution mode most suitable for debugger functionality\n")
                 .append("  --jdb           runs JRuby process under JDB\n")
                 .append("  --properties    List all configuration Java properties (pass -J-Dproperty=value)\n")
                 .append("  --sample        run with profiling using the JVM's sampling profiler\n")
                 .append("  --profile       run with instrumented (timed) profiling\n")
                 .append("  --client        use the non-optimizing \"client\" JVM (improves startup; default)\n")
                 .append("  --server        use the optimizing \"server\" JVM (improves perf)\n")
                 .append("  --manage        enable remote JMX management and monitoring of the VM and JRuby\n")
                 .append("  --headless      do not launch a GUI window, no matter what\n")
                 .append("  --1.8           specify Ruby 1.8.x compatibility (default)\n")
                 .append("  --1.9           specify Ruby 1.9.x compatibility\n")
                 .append("  --bytecode      show the JVM bytecode produced by compiling specified code\n")
                 .append("  --version       print the version\n");
 
         return sb.toString();
     }
 
     public String getExtendedHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These flags are for extended JRuby options.\n")
                 .append("Specify them by passing -X<option>\n")
                 .append("  -O              run with ObjectSpace disabled (default; improves performance)\n")
                 .append("  +O              run with ObjectSpace enabled (reduces performance)\n")
                 .append("  -C              disable all compilation\n")
                 .append("  +C              force compilation of all scripts before they are run (except eval)\n");
 
         return sb.toString();
     }
 
     public String getPropertyHelp() {
         StringBuilder sb = new StringBuilder();
         sb
                 .append("These properties can be used to alter runtime behavior for perf or compatibility.\n")
                 .append("Specify them by passing -J-D<property>=<value>\n")
                 .append("\nCOMPILER SETTINGS:\n")
                 .append("    jruby.compile.mode=JIT|FORCE|OFF\n")
                 .append("       Set compilation mode. JIT is default; FORCE compiles all, OFF disables\n")
                 .append("    jruby.compile.fastest=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on all experimental compiler optimizations\n")
                 .append("    jruby.compile.frameless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on frameless compilation where possible\n")
                 .append("    jruby.compile.positionless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation that avoids updating Ruby position info. Default is false\n")
                 .append("    jruby.compile.threadless=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on compilation without polling for \"unsafe\" thread events. Default is false\n")
                 .append("    jruby.compile.fastops=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on fast operators for Fixnum. Default is false\n")
                 .append("    jruby.compile.fastcase=true|false\n")
                 .append("       (EXPERIMENTAL) Turn on fast case/when for all-Fixnum whens. Default is false\n")
                 .append("    jruby.compile.chainsize=<line count>\n")
                 .append("       Set the number of lines at which compiled bodies are \"chained\". Default is ").append(CHAINED_COMPILE_LINE_COUNT_DEFAULT).append("\n")
                 .append("    jruby.compile.lazyHandles=true|false\n")
                 .append("       Generate method bindings (handles) for compiled methods lazily. Default is false.\n")
                 .append("    jruby.compile.peephole=true|false\n")
                 .append("       Enable or disable peephole optimizations. Default is true (on).\n")
                 .append("\nJIT SETTINGS:\n")
                 .append("    jruby.jit.threshold=<invocation count>\n")
                 .append("       Set the JIT threshold to the specified method invocation count. Default is ").append(JIT_THRESHOLD).append(".\n")
                 .append("    jruby.jit.max=<method count>\n")
                 .append("       Set the max count of active methods eligible for JIT-compilation.\n")
                 .append("       Default is ").append(JIT_MAX_METHODS_LIMIT).append(" per runtime. A value of 0 disables JIT, -1 disables max.\n")
                 .append("    jruby.jit.maxsize=<jitted method size (full .class)>\n")
                 .append("       Set the maximum full-class byte size allowed for jitted methods. Default is ").append(JIT_MAX_SIZE_LIMIT).append(".\n")
                 .append("    jruby.jit.logging=true|false\n")
                 .append("       Enable JIT logging (reports successful compilation). Default is false\n")
                 .append("    jruby.jit.logging.verbose=true|false\n")
                 .append("       Enable verbose JIT logging (reports failed compilation). Default is false\n")
                 .append("    jruby.jit.logEvery=<method count>\n")
                 .append("       Log a message every n methods JIT compiled. Default is 0 (off).\n")
                 .append("    jruby.jit.exclude=<ClsOrMod,ClsOrMod::method_name,-::method_name>\n")
                 .append("       Exclude methods from JIT by class/module short name, c/m::method_name,\n")
                 .append("       or -::method_name for anon/singleton classes/modules. Comma-delimited.\n")
                 .append("    jruby.jit.cache=true|false\n")
                 .append("       Cache jitted method in-memory bodies across runtimes and loads. Default is true.\n")
                 .append("    jruby.jit.codeCache=<dir>\n")
                 .append("       Save jitted methods to <dir> as they're compiled, for future runs.\n")
                 .append("\nNATIVE SUPPORT:\n")
                 .append("    jruby.native.enabled=true|false\n")
                 .append("       Enable/disable native extensions (like JNA for non-Java APIs; Default is true\n")
                 .append("       (This affects all JRuby instances in a given JVM)\n")
                 .append("    jruby.native.verbose=true|false\n")
                 .append("       Enable verbose logging of native extension loading. Default is false.\n")
                 .append("\nTHREAD POOLING:\n")
                 .append("    jruby.thread.pool.enabled=true|false\n")
                 .append("       Enable reuse of native backing threads via a thread pool. Default is false.\n")
                 .append("    jruby.thread.pool.min=<min thread count>\n")
                 .append("       The minimum number of threads to keep alive in the pool. Default is 0.\n")
                 .append("    jruby.thread.pool.max=<max thread count>\n")
                 .append("       The maximum number of threads to allow in the pool. Default is unlimited.\n")
                 .append("    jruby.thread.pool.ttl=<time to live, in seconds>\n")
                 .append("       The maximum number of seconds to keep alive an idle thread. Default is 60.\n")
                 .append("\nMISCELLANY:\n")
                 .append("    jruby.compat.version=RUBY1_8|RUBY1_9\n")
                 .append("       Specify the major Ruby version to be compatible with; Default is RUBY1_8\n")
                 .append("    jruby.objectspace.enabled=true|false\n")
                 .append("       Enable or disable ObjectSpace.each_object (default is disabled)\n")
                 .append("    jruby.launch.inproc=true|false\n")
                 .append("       Set in-process launching of e.g. system('ruby ...'). Default is true\n")
                 .append("    jruby.bytecode.version=1.5|1.6\n")
                 .append("       Set bytecode version for JRuby to generate. Default is current JVM version.\n")
                 .append("    jruby.management.enabled=true|false\n")
                 .append("       Set whether JMX management is enabled. Default is false.\n")
                 .append("    jruby.jump.backtrace=true|false\n")
                 .append("       Make non-local flow jumps generate backtraces. Default is false.\n")
                 .append("    jruby.process.noUnwrap=true|false\n")
                 .append("       Do not unwrap process streams (IBM Java 6 issue). Default is false.\n")
+                .append("    jruby.reify.classes=true|false\n")
+                .append("       Before instantiation, stand up a real Java class for ever Ruby class. Default is false. \n")
+                .append("    jruby.reify.logErrors=true|false\n")
+                .append("       Log errors during reification (reify.classes=true). Default is false. \n")
                 .append("\nDEBUGGING/LOGGING:\n")
                 .append("    jruby.debug.loadService=true|false\n")
                 .append("       LoadService logging\n")
                 .append("    jruby.debug.loadService.timing=true|false\n")
                 .append("       Print load timings for each require'd library. Default is false.\n")
                 .append("    jruby.debug.launch=true|false\n")
                 .append("       ShellLauncher logging\n")
                 .append("    jruby.debug.fullTrace=true|false\n")
                 .append("       Set whether full traces are enabled (c-call/c-return). Default is false.\n")
                 .append("    jruby.debug.scriptResolution=true|false\n")
                 .append("       Print which script is executed by '-S' flag. Default is false.\n")
                 .append("    jruby.reflected.handles=true|false\n")
                 .append("       Use reflection for binding methods, not generated bytecode. Default is false.\n")
                 .append("\nJAVA INTEGRATION:\n")
                 .append("    jruby.ji.setAccessible=true|false\n")
                 .append("       Try to set inaccessible Java methods to be accessible. Default is true.\n")
                 .append("    jruby.interfaces.useProxy=true|false\n")
                 .append("       Use java.lang.reflect.Proxy for interface impl. Default is false.\n");
 
         return sb.toString();
     }
 
     public String getVersionString() {
         String ver = null;
         String patchDelimeter = null;
         int patchlevel = 0;
         switch (getCompatVersion()) {
         case RUBY1_8:
             ver = Constants.RUBY_VERSION;
             patchlevel = Constants.RUBY_PATCHLEVEL;
             patchDelimeter = " patchlevel ";
             break;
         case RUBY1_9:
             ver = Constants.RUBY1_9_VERSION;
             patchlevel = Constants.RUBY1_9_PATCHLEVEL;
             patchDelimeter = " trunk ";
             break;
         }
 
         String fullVersion = String.format(
                 "jruby %s (ruby %s%s%d) (%s %s) (%s %s) [%s-%s-java]",
                 Constants.VERSION, ver, patchDelimeter, patchlevel,
                 Constants.COMPILE_DATE, Constants.REVISION,
                 System.getProperty("java.vm.name"), System.getProperty("java.version"),
                 Platform.getOSName(),
                 SafePropertyAccessor.getProperty("os.arch", "unknown")
                 );
 
         return fullVersion;
     }
 
     public String getCopyrightString() {
         return "JRuby - Copyright (C) 2001-2010 The JRuby Community (and contribs)";
     }
 
     public void processArguments(String[] arguments) {
         new ArgumentProcessor(arguments).processArguments();
         tryProcessArgumentsWithRubyopts();
     }
 
     public void tryProcessArgumentsWithRubyopts() {
         try {
             String rubyopt = null;
             if (environment != null && environment.containsKey("RUBYOPT")) {
                 rubyopt = environment.get("RUBYOPT").toString();
             } else {
                 rubyopt = System.getenv("RUBYOPT");
             }
             if (rubyopt != null && rubyopt.split("\\s").length != 0) {
                 String[] rubyoptArgs = rubyopt.split("\\s+");
                 new ArgumentProcessor(rubyoptArgs, false, true).processArguments();
             }
         } catch (SecurityException se) {
             // ignore and do nothing
         }
     }
 
     public CompileMode getCompileMode() {
         return compileMode;
     }
 
     public void setCompileMode(CompileMode compileMode) {
         this.compileMode = compileMode;
     }
 
     public boolean isJitLogging() {
         return jitLogging;
     }
 
     public boolean isJitDumping() {
         return jitDumping;
     }
 
     public boolean isJitLoggingVerbose() {
         return jitLoggingVerbose;
     }
 
     public int getJitLogEvery() {
         return jitLogEvery;
     }
 
     public void setJitLogEvery(int jitLogEvery) {
         this.jitLogEvery = jitLogEvery;
     }
 
     public boolean isSamplingEnabled() {
         return samplingEnabled;
     }
 
     public int getJitThreshold() {
         return jitThreshold;
     }
 
     public void setJitThreshold(int jitThreshold) {
         this.jitThreshold = jitThreshold;
     }
 
     public int getJitMax() {
         return jitMax;
     }
 
     public void setJitMax(int jitMax) {
         this.jitMax = jitMax;
     }
 
     public int getJitMaxSize() {
         return jitMaxSize;
     }
 
     public void setJitMaxSize(int jitMaxSize) {
         this.jitMaxSize = jitMaxSize;
     }
 
     public boolean isRunRubyInProcess() {
         return runRubyInProcess;
     }
 
     public void setRunRubyInProcess(boolean flag) {
         this.runRubyInProcess = flag;
     }
 
     public void setInput(InputStream newInput) {
         input = newInput;
     }
 
     public InputStream getInput() {
         return input;
     }
 
     public CompatVersion getCompatVersion() {
         return compatVersion;
     }
 
     public void setCompatVersion(CompatVersion compatVersion) {
         // Until we get a little more solid on 1.9 support we will only run interpreted mode
         if (compatVersion == CompatVersion.RUBY1_9) compileMode = CompileMode.OFF;
         if (compatVersion == null) compatVersion = CompatVersion.RUBY1_8;
 
         this.compatVersion = compatVersion;
     }
 
     public void setOutput(PrintStream newOutput) {
         output = newOutput;
     }
 
     public PrintStream getOutput() {
         return output;
     }
 
     public void setError(PrintStream newError) {
         error = newError;
     }
 
     public PrintStream getError() {
         return error;
     }
 
     public void setCurrentDirectory(String newCurrentDirectory) {
         currentDirectory = newCurrentDirectory;
     }
 
     public String getCurrentDirectory() {
         return currentDirectory;
     }
 
     public void setProfile(Profile newProfile) {
         profile = newProfile;
     }
 
     public Profile getProfile() {
         return profile;
     }
 
     public void setObjectSpaceEnabled(boolean newObjectSpaceEnabled) {
         objectSpaceEnabled = newObjectSpaceEnabled;
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public void setEnvironment(Map newEnvironment) {
         environment = newEnvironment;
     }
 
     public Map getEnvironment() {
         return environment;
     }
 
     public ClassLoader getLoader() {
         return loader;
     }
 
     public void setLoader(ClassLoader loader) {
         // Setting the loader needs to reset the class cache
         if(this.loader != loader) {
             this.classCache = new ClassCache<Script>(loader, this.classCache.getMax());
         }
         this.loader = loader;
     }
 
     public String[] getArgv() {
         return argv;
     }
 
     public void setArgv(String[] argv) {
         this.argv = argv;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
             // try the normal property first
             if (!Ruby.isSecurityRestricted()) {
                 jrubyHome = SafePropertyAccessor.getProperty("jruby.home");
             }
 
             if (jrubyHome != null) {
                 // verify it if it's there
                 jrubyHome = verifyHome(jrubyHome);
             } else {
                 try {
                     // try loading from classloader resources
                     final String jrubyHomePath = "/META-INF/jruby.home";
                     URL jrubyHomeURL = getClass().getResource(jrubyHomePath);
                     // special case for jar:file (most typical case)
                     if (jrubyHomeURL.getProtocol().equals("jar")) {
                         jrubyHome = jrubyHomeURL.getPath();
                     } else {
                         jrubyHome = "classpath:" + jrubyHomePath;
                         return jrubyHome;
                     }
                 } catch (Exception e) {}
 
                 if (jrubyHome != null) {
                     // verify it if it's there
                     jrubyHome = verifyHome(jrubyHome);
                 } else {
                     // otherwise fall back on system temp location
                     jrubyHome = SafePropertyAccessor.getProperty("java.io.tmpdir");
                 }
             }
         }
         return jrubyHome;
     }
 
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home);
     }
 
     // We require the home directory to be absolute
     private String verifyHome(String home) {
         if (home.equals(".")) {
             home = SafePropertyAccessor.getProperty("user.dir");
         }
         if (home.startsWith("cp:")) {
             home = home.substring(3);
         } else if (!home.startsWith("file:") && !home.startsWith("classpath:")) {
             NormalizedFile f = new NormalizedFile(home);
             if (!f.isAbsolute()) {
                 home = f.getAbsolutePath();
             }
             if (!f.exists()) {
                 error.println("Warning: JRuby home \"" + f + "\" does not exist, using " + SafePropertyAccessor.getProperty("java.io.tmpdir"));
                 return System.getProperty("java.io.tmpdir");
             }
         }
         return home;
     }
 
     private final class Argument {
         public final String originalValue;
         public final String dashedValue;
         public Argument(String value, boolean dashed) {
             this.originalValue = value;
             this.dashedValue = dashed && !value.startsWith("-") ? "-" + value : value;
         }
     }
 
     private class ArgumentProcessor {
         private List<Argument> arguments;
         private int argumentIndex = 0;
         private boolean processArgv;
 
         public ArgumentProcessor(String[] arguments) {
             this(arguments, true, false);
         }
 
         public ArgumentProcessor(String[] arguments, boolean processArgv, boolean dashed) {
             this.arguments = new ArrayList<Argument>();
             if (arguments != null && arguments.length > 0) {
                 for (String argument : arguments) {
                     this.arguments.add(new Argument(argument, dashed));
                 }
             }
             this.processArgv = processArgv;
         }
 
         public void processArguments() {
             while (argumentIndex < arguments.size() && isInterpreterArgument(arguments.get(argumentIndex).originalValue)) {
                 processArgument();
                 argumentIndex++;
             }
 
             if (!hasInlineScript && scriptFileName == null) {
                 if (argumentIndex < arguments.size()) {
                     setScriptFileName(arguments.get(argumentIndex).originalValue); //consume the file name
                     argumentIndex++;
                 }
             }
 
             if (processArgv) processArgv();
         }
 
         private void processArgv() {
             List<String> arglist = new ArrayList<String>();
             for (; argumentIndex < arguments.size(); argumentIndex++) {
                 String arg = arguments.get(argumentIndex).originalValue;
                 if (argvGlobalsOn && arg.startsWith("-")) {
                     arg = arg.substring(1);
                     if (arg.indexOf('=') > 0) {
                         String[] keyvalue = arg.split("=", 2);
                         optionGlobals.put(keyvalue[0], keyvalue[1]);
                     } else {
                         optionGlobals.put(arg, null);
                     }
                 } else {
                     argvGlobalsOn = false;
                     arglist.add(arg);
                 }
             }
 
             // Remaining arguments are for the script itself
             arglist.addAll(Arrays.asList(argv));
             argv = arglist.toArray(new String[arglist.size()]);
         }
 
         private boolean isInterpreterArgument(String argument) {
             return argument.length() > 0 && (argument.charAt(0) == '-' || argument.charAt(0) == '+') && !endOfArguments;
         }
 
         private String getArgumentError(String additionalError) {
             return "jruby: invalid argument\n" + additionalError + "\n";
         }
 
         private void processArgument() {
             String argument = arguments.get(argumentIndex).dashedValue;
             FOR : for (characterIndex = 1; characterIndex < argument.length(); characterIndex++) {
                 switch (argument.charAt(characterIndex)) {
                 case '0': {
                     String temp = grabOptionalValue();
                     if (null == temp) {
                         recordSeparator = "\u0000";
                     } else if (temp.equals("0")) {
                         recordSeparator = "\n\n";
                     } else if (temp.equals("777")) {
                         recordSeparator = "\uFFFF"; // Specify something that can't separate
                     } else {
                         try {
                             int val = Integer.parseInt(temp, 8);
                             recordSeparator = "" + (char) val;
                         } catch (Exception e) {
                             MainExitException mee = new MainExitException(1, getArgumentError(" -0 must be followed by either 0, 777, or a valid octal value"));
                             mee.setUsageError(true);
                             throw mee;
                         }
                     }
                     break FOR;
                 }
                 case 'a':
                     split = true;
                     break;
                 case 'b':
                     benchmarking = true;
                     break;
                 case 'c':
                     shouldCheckSyntax = true;
                     break;
                 case 'C':
                     try {
                         String saved = grabValue(getArgumentError(" -C must be followed by a directory expression"));
                         File base = new File(currentDirectory);
                         File newDir = new File(saved);
                         if (newDir.isAbsolute()) {
                             currentDirectory = newDir.getCanonicalPath();
                         } else {
                             currentDirectory = new File(base, newDir.getPath()).getCanonicalPath();
                         }
                         if (!(new File(currentDirectory).isDirectory())) {
                             MainExitException mee = new MainExitException(1, "jruby: Can't chdir to " + saved + " (fatal)");
                             throw mee;
                         }
                     } catch (IOException e) {
                         MainExitException mee = new MainExitException(1, getArgumentError(" -C must be followed by a valid directory"));
                         throw mee;
                     }
                     break FOR;
                 case 'd':
                     debug = true;
                     verbose = Boolean.TRUE;
                     break;
                 case 'e':
                     inlineScript.append(grabValue(getArgumentError(" -e must be followed by an expression to evaluate")));
                     inlineScript.append('\n');
                     hasInlineScript = true;
                     break FOR;
                 case 'F':
                     inputFieldSeparator = grabValue(getArgumentError(" -F must be followed by a pattern for input field separation"));
                     break FOR;
                 case 'h':
                     shouldPrintUsage = true;
                     shouldRunInterpreter = false;
                     break;
                 case 'i' :
                     inPlaceBackupExtension = grabOptionalValue();
                     if(inPlaceBackupExtension == null) inPlaceBackupExtension = "";
                     break FOR;
                 case 'I':
                     String s = grabValue(getArgumentError("-I must be followed by a directory name to add to lib path"));
                     String[] ls = s.split(java.io.File.pathSeparator);
                     loadPaths.addAll(Arrays.asList(ls));
                     break FOR;
                 case 'J':
                     grabOptionalValue();
                     error.println("warning: "+argument+" argument ignored (launched in same VM?)");
                     break FOR;
                 case 'K':
                     // FIXME: No argument seems to work for -K in MRI plus this should not
                     // siphon off additional args 'jruby -K ~/scripts/foo'.  Also better error
                     // processing.
                     String eArg = grabValue(getArgumentError("provide a value for -K"));
                     kcode = KCode.create(null, eArg);
                     break;
                 case 'l':
                     processLineEnds = true;
                     break;
                 case 'n':
                     assumeLoop = true;
                     break;
                 case 'p':
                     assumePrinting = true;
                     assumeLoop = true;
                     break;
                 case 'r':
                     requiredLibraries.add(grabValue(getArgumentError("-r must be followed by a package to require")));
                     break FOR;
                 case 's' :
                     argvGlobalsOn = true;
                     break;
                 case 'S':
                     runBinScript();
                     break FOR;
                 case 'T' :{
                     String temp = grabOptionalValue();
                     int value = 1;
 
                     if(temp!=null) {
                         try {
                             value = Integer.parseInt(temp, 8);
                         } catch(Exception e) {
                             value = 1;
                         }
                     }
 
                     safeLevel = value;
 
                     break FOR;
                 }
                 case 'v':
                     verbose = Boolean.TRUE;
                     setShowVersion(true);
                     break;
                 case 'w':
                     verbose = Boolean.TRUE;
                     break;
                 case 'W': {
                     String temp = grabOptionalValue();
                     int value = 2;
                     if (null != temp) {
                         if (temp.equals("2")) {
                             value = 2;
                         } else if (temp.equals("1")) {
                             value = 1;
                         } else if (temp.equals("0")) {
                             value = 0;
                         } else {
                             MainExitException mee = new MainExitException(1, getArgumentError(" -W must be followed by either 0, 1, 2 or nothing"));
                             mee.setUsageError(true);
                             throw mee;
                         }
                     }
                     switch (value) {
                     case 0:
                         verbose = null;
                         break;
                     case 1:
                         verbose = Boolean.FALSE;
                         break;
                     case 2:
                         verbose = Boolean.TRUE;
                         break;
                     }
 
 
                     break FOR;
                 }
                case 'x':
                    try {
                        String saved = grabOptionalValue();
                        if (saved != null) {
                            File base = new File(currentDirectory);
                            File newDir = new File(saved);
                            if (newDir.isAbsolute()) {
                                currentDirectory = newDir.getCanonicalPath();
                            } else {
                                currentDirectory = new File(base, newDir.getPath()).getCanonicalPath();
                            }
                            if (!(new File(currentDirectory).isDirectory())) {
                                MainExitException mee = new MainExitException(1, "jruby: Can't chdir to " + saved + " (fatal)");
                                throw mee;
                            }
                        }
                        xFlag = true;
                    } catch (IOException e) {
                        MainExitException mee = new MainExitException(1, getArgumentError(" -x must be followed by a valid directory"));
                        throw mee;
                    }
                    break FOR;
                 case 'X':
                     String extendedOption = grabOptionalValue();
 
                     if (extendedOption == null) {
                         throw new MainExitException(0, "jruby: missing extended option, listing available options\n" + getExtendedHelp());
                     } else if (extendedOption.equals("-O")) {
                         objectSpaceEnabled = false;
                     } else if (extendedOption.equals("+O")) {
                         objectSpaceEnabled = true;
                     } else if (extendedOption.equals("-C")) {
                         compileMode = CompileMode.OFF;
                     } else if (extendedOption.equals("-CIR")) {
                         compileMode = CompileMode.OFFIR;
                     } else if (extendedOption.equals("+C")) {
                         compileMode = CompileMode.FORCE;
                     } else {
                         MainExitException mee =
                                 new MainExitException(1, "jruby: invalid extended option " + extendedOption + " (-X will list valid options)\n");
                         mee.setUsageError(true);
 
                         throw mee;
                     }
                     break FOR;
                 case 'y':
                     parserDebug = true;
                     break FOR;
                 case '-':
                     if (argument.equals("--command") || argument.equals("--bin")) {
                         characterIndex = argument.length();
                         runBinScript();
                         break;
                     } else if (argument.equals("--compat")) {
                         characterIndex = argument.length();
                         setCompatVersion(CompatVersion.getVersionFromString(grabValue(getArgumentError("--compat must be RUBY1_8 or RUBY1_9"))));
                         break FOR;
                     } else if (argument.equals("--copyright")) {
                         setShowCopyright(true);
                         shouldRunInterpreter = false;
                         break FOR;
                     } else if (argument.equals("--debug")) {
                         FULL_TRACE_ENABLED = true;
                         compileMode = CompileMode.OFF;
                         break FOR;
                     } else if (argument.equals("--jdb")) {
                         debug = true;
                         verbose = Boolean.TRUE;
                         break;
                     } else if (argument.equals("--help")) {
                         shouldPrintUsage = true;
                         shouldRunInterpreter = false;
                         break;
                     } else if (argument.equals("--properties")) {
                         shouldPrintProperties = true;
                         shouldRunInterpreter = false;
                         break;
                     } else if (argument.equals("--version")) {
                         setShowVersion(true);
                         shouldRunInterpreter = false;
                         break FOR;
                     } else if (argument.equals("--bytecode")) {
                         setShowBytecode(true);
                         break FOR;
                     } else if (argument.equals("--fast")) {
                         compileMode = CompileMode.FORCE;
                         FASTEST_COMPILE_ENABLED = true;
                         FASTOPS_COMPILE_ENABLED = true;
                         FRAMELESS_COMPILE_ENABLED = true;
                         POSITIONLESS_COMPILE_ENABLED = true;
                         FASTCASE_COMPILE_ENABLED = true;
                         FASTSEND_COMPILE_ENABLED = true;
                         INLINE_DYNCALL_ENABLED = true;
                         RubyException.TRACE_TYPE = RubyException.RUBY_COMPILED;
                         break FOR;
                     } else if (argument.equals("--profile")) {
                         profiling = true;
                         break FOR;
                     } else if (argument.equals("--1.9")) {
                         setCompatVersion(CompatVersion.RUBY1_9);
                         break FOR;
                     } else if (argument.equals("--1.8")) {
                         setCompatVersion(CompatVersion.RUBY1_8);
                         break FOR;
                     } else {
                         if (argument.equals("--")) {
                             // ruby interpreter compatibilty
                             // Usage: ruby [switches] [--] [programfile] [arguments])
                             endOfArguments = true;
                             break;
                         }
                     }
                 default:
                     throw new MainExitException(1, "jruby: unknown option " + argument);
                 }
             }
         }
 
         private void runBinScript() {
             String scriptName = grabValue("jruby: provide a bin script to execute");
             if (scriptName.equals("irb")) {
                 scriptName = "jirb";
             }
 
             scriptFileName = resolveScript(scriptName);
 
             // run as a command if we couldn't find a script
             if (scriptFileName == null) {
                 scriptFileName = scriptName;
                 requiredLibraries.add("jruby/commands");
                 inlineScript.append("JRuby::Commands.").append(scriptName);
                 inlineScript.append("\n");
                 hasInlineScript = true;
             }
 
             endOfArguments = true;
         }
 
         private String resolveScript(String scriptName) {
             // This try/catch is to allow failing over to the "commands" logic
             // when running from within a jruby-complete jar file, which has
             // jruby.home = a jar file URL that does not resolve correctly with
             // JRubyFile.create.
             try {
                 // try cwd first
                 File fullName = JRubyFile.create(currentDirectory, scriptName);
                 if (fullName.exists() && fullName.isFile()) {
                     if (DEBUG_SCRIPT_RESOLUTION) {
                         error.println("Found: " + fullName.getAbsolutePath());
                     }
                     return scriptName;
                 }
 
                 fullName = JRubyFile.create(getJRubyHome(), "bin/" + scriptName);
                 if (fullName.exists() && fullName.isFile()) {
                     if (DEBUG_SCRIPT_RESOLUTION) {
                         error.println("Found: " + fullName.getAbsolutePath());
                     }
                     return fullName.getAbsolutePath();
                 }
 
                 try {
                     String path = System.getenv("PATH");
                     if (path != null) {
                         String[] paths = path.split(System.getProperty("path.separator"));
                         for (int i = 0; i < paths.length; i++) {
                             fullName = JRubyFile.create(paths[i], scriptName);
                             if (fullName.exists() && fullName.isFile()) {
                                 if (DEBUG_SCRIPT_RESOLUTION) {
                                     error.println("Found: " + fullName.getAbsolutePath());
                                 }
                                 return fullName.getAbsolutePath();
                             }
                         }
                     }
                 } catch (SecurityException se) {
                     // ignore and do nothing
                 }
             } catch (IllegalArgumentException iae) {
                 if (debug) error.println("warning: could not resolve -S script on filesystem: " + scriptName);
             }
             return null;
         }
 
         private String grabValue(String errorMessage) {
             String optValue = grabOptionalValue();
             if (optValue != null) {
                 return optValue;
             }
             argumentIndex++;
             if (argumentIndex < arguments.size()) {
                 return arguments.get(argumentIndex).originalValue;
             }
 
             MainExitException mee = new MainExitException(1, errorMessage);
             mee.setUsageError(true);
 
             throw mee;
         }
 
         private String grabOptionalValue() {
             characterIndex++;
             String argValue = arguments.get(argumentIndex).originalValue;
             if (characterIndex < argValue.length()) {
                 return argValue.substring(characterIndex);
             }
             return null;
         }
     }
 
     public byte[] inlineScript() {
         return inlineScript.toString().getBytes();
     }
 
     public Collection<String> requiredLibraries() {
         return requiredLibraries;
     }
 
     public List<String> loadPaths() {
         return loadPaths;
     }
 
     public void setLoadPaths(List<String> loadPaths) {
         this.loadPaths = loadPaths;
     }
 
     public boolean shouldRunInterpreter() {
         return isShouldRunInterpreter();
     }
 
     public boolean shouldPrintUsage() {
         return shouldPrintUsage;
     }
 
     public boolean shouldPrintProperties() {
         return shouldPrintProperties;
     }
 
     private boolean isSourceFromStdin() {
         return getScriptFileName() == null;
     }
 
     public boolean isInlineScript() {
         return hasInlineScript;
     }
 
     public InputStream getScriptSource() {
         try {
             // KCode.NONE is used because KCODE does not affect parse in Ruby 1.8
             // if Ruby 2.0 encoding pragmas are implemented, this will need to change
             if (hasInlineScript) {
                 return new ByteArrayInputStream(inlineScript());
             } else if (isSourceFromStdin()) {
                 // can't use -v and stdin
                 if (isShowVersion()) {
                     return null;
                 }
                 return getInput();
             } else {
                 String script = getScriptFileName();
                 InputStream stream = null;
                 if (script.startsWith("file:") && script.indexOf(".jar!/") != -1) {
                     stream = new URL("jar:" + script).openStream();
                 } else {
                     File file = JRubyFile.create(getCurrentDirectory(), getScriptFileName());
                     if (isxFlag()) {
                         // search for a shebang line and
                         // return the script between shebang and __END__ or CTRL-Z (0x1A)
                         return findScript(file);
                     }
                     stream = new FileInputStream(file);
                 }
 
                 return new BufferedInputStream(stream, 8192);
             }
         } catch (IOException e) {
             // We haven't found any file directly on the file system,
             // now check for files inside the JARs.
             InputStream is = getJarScriptSource();
             if (is != null) {
                 return new BufferedInputStream(is, 8129);
             }
             throw new MainExitException(1, "Error opening script file: " + e.getMessage());
         }
     }
 
     private InputStream findScript(File file) throws IOException {
         StringBuffer buf = new StringBuffer();
         BufferedReader br = new BufferedReader(new FileReader(file));
         String currentLine = br.readLine();
         while (currentLine != null && !(currentLine.length() > 2 && currentLine.charAt(0) == '#' && currentLine.charAt(1) == '!')) {
             currentLine = br.readLine();
         }
 
         buf.append(currentLine);
         buf.append("\n");
 
         do {
             currentLine = br.readLine();
             if (currentLine != null) {
             buf.append(currentLine);
             buf.append("\n");
             }
         } while (!(currentLine == null || currentLine.contains("__END__") || currentLine.contains("\026")));
         return new BufferedInputStream(new ByteArrayInputStream(buf.toString().getBytes()), 8192);
     }
 
     private InputStream getJarScriptSource() {
         String name = getScriptFileName();
         boolean looksLikeJarURL = name.startsWith("file:") && name.indexOf("!/") != -1;
         if (!looksLikeJarURL) {
             return null;
         }
 
         String before = name.substring("file:".length(), name.indexOf("!/"));
         String after =  name.substring(name.indexOf("!/") + 2);
 
         try {
             JarFile jFile = new JarFile(before);
             JarEntry entry = jFile.getJarEntry(after);
 
             if (entry != null && !entry.isDirectory()) {
                 return jFile.getInputStream(entry);
             }
         } catch (IOException ignored) {
         }
         return null;
     }
 
     public String displayedFileName() {
         if (hasInlineScript) {
             if (scriptFileName != null) {
                 return scriptFileName;
             } else {
                 return "-e";
             }
         } else if (isSourceFromStdin()) {
             return "-";
         } else {
             return getScriptFileName();
         }
     }
 
     public void setScriptFileName(String scriptFileName) {
         this.scriptFileName = scriptFileName;
     }
 
     public String getScriptFileName() {
         return scriptFileName;
     }
 
     public boolean isBenchmarking() {
         return benchmarking;
     }
 
     public boolean isAssumeLoop() {
         return assumeLoop;
     }
 
     public boolean isAssumePrinting() {
         return assumePrinting;
     }
 
     public boolean isProcessLineEnds() {
         return processLineEnds;
     }
 
     public boolean isSplit() {
         return split;
     }
 
     public boolean isVerbose() {
         return verbose == Boolean.TRUE;
     }
 
     public Boolean getVerbose() {
         return verbose;
     }
 
     public boolean isDebug() {
         return debug;
     }
 
     public void setDebug(boolean debug) {
         this.debug = debug;
     }
 
     public boolean isParserDebug() {
         return parserDebug;
     }
 
     public boolean isShowVersion() {
         return showVersion;
     }
     
     public boolean isShowBytecode() {
         return showBytecode;
     }
 
     public boolean isShowCopyright() {
         return showCopyright;
     }
 
     protected void setShowVersion(boolean showVersion) {
         this.showVersion = showVersion;
     }
     
     protected void setShowBytecode(boolean showBytecode) {
         this.showBytecode = showBytecode;
     }
 
     protected void setShowCopyright(boolean showCopyright) {
         this.showCopyright = showCopyright;
     }
 
     public boolean isShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
 
     public boolean isShouldCheckSyntax() {
         return shouldCheckSyntax;
     }
 
     public String getInputFieldSeparator() {
         return inputFieldSeparator;
     }
 
     public KCode getKCode() {
         return kcode;
     }
 
     public void setKCode(KCode kcode) {
         this.kcode = kcode;
     }
 
     public String getRecordSeparator() {
         return recordSeparator;
     }
 
     public int getSafeLevel() {
         return safeLevel;
     }
 
     public void setRecordSeparator(String recordSeparator) {
         this.recordSeparator = recordSeparator;
     }
 
     public ClassCache getClassCache() {
         return classCache;
     }
 
     public String getInPlaceBackupExtention() {
         return inPlaceBackupExtension;
     }
 
     public void setClassCache(ClassCache classCache) {
         this.classCache = classCache;
     }
 
     public Map getOptionGlobals() {
         return optionGlobals;
     }
     
     public boolean isManagementEnabled() {
         return managementEnabled;
     }
     
     public Set getExcludedMethods() {
         return excludedMethods;
     }
 
     public ASTCompiler newCompiler() {
         if (getCompatVersion() == CompatVersion.RUBY1_8) {
             return new ASTCompiler();
         } else {
diff --git a/src/org/jruby/compiler/JITCompiler.java b/src/org/jruby/compiler/JITCompiler.java
index ce08e0b19a..19d678f3da 100644
--- a/src/org/jruby/compiler/JITCompiler.java
+++ b/src/org/jruby/compiler/JITCompiler.java
@@ -1,440 +1,441 @@
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
 
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.PrintWriter;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.Locale;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicLong;
 import org.jruby.Ruby;
 import org.jruby.RubyModule;
 import org.jruby.MetaClass;
 import org.jruby.RubyEncoding;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.ast.ArgsNode;
 import org.jruby.ast.Node;
 import org.jruby.ast.executable.Script;
 import org.jruby.ast.util.SexpMaker;
 import org.jruby.compiler.impl.StandardASMCompiler;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.internal.runtime.methods.CallConfiguration;
 import org.jruby.internal.runtime.methods.DefaultMethod;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.util.ClassCache;
 import org.jruby.util.JavaNameMangler;
 import org.objectweb.asm.ClassReader;
 import org.objectweb.asm.util.TraceClassVisitor;
 
 public class JITCompiler implements JITCompilerMBean {
     public static final boolean USE_CACHE = true;
+    public static final String RUBY_JIT_PREFIX = "rubyjit";
 
     public static class JITCounts {
         private final AtomicLong compiledCount = new AtomicLong(0);
         private final AtomicLong successCount = new AtomicLong(0);
         private final AtomicLong failCount = new AtomicLong(0);
         private final AtomicLong abandonCount = new AtomicLong(0);
         private final AtomicLong compileTime = new AtomicLong(0);
         private final AtomicLong averageCompileTime = new AtomicLong(0);
         private final AtomicLong codeSize = new AtomicLong(0);
         private final AtomicLong averageCodeSize = new AtomicLong(0);
         private final AtomicLong largestCodeSize = new AtomicLong(0);
     }
 
     private final JITCounts counts = new JITCounts();
     
     public JITCompiler(Ruby ruby) {
         ruby.getBeanManager().register(this);
     }
 
     public DynamicMethod tryJIT(final DefaultMethod method, final ThreadContext context, final String name) {
         if (context.getRuntime().getInstanceConfig().getCompileMode().shouldJIT()) {
             return jitIsEnabled(method, context, name);
         }
 
         return null;
     }
 
     private DynamicMethod jitIsEnabled(final DefaultMethod method, final ThreadContext context, final String name) {
         RubyInstanceConfig instanceConfig = context.getRuntime().getInstanceConfig();
         
         if (method.incrementCallCount() >= instanceConfig.getJitThreshold()) {
             return jitThresholdReached(method, instanceConfig, context, name);
         }
 
         return null;
     }
     
     private DynamicMethod jitThresholdReached(final DefaultMethod method, RubyInstanceConfig instanceConfig, final ThreadContext context, final String name) {
         try {
             // The cache is full. Abandon JIT for this method and bail out.
             ClassCache classCache = instanceConfig.getClassCache();
             if (classCache.isFull()) {
                 counts.abandonCount.incrementAndGet();
                 method.setCallCount(-1);
                 return null;
             }
 
             // Check if the method has been explicitly excluded
             String moduleName = method.getImplementationClass().getName();
             if(instanceConfig.getExcludedMethods().size() > 0) {
                 String excludeModuleName = moduleName;
                 if(method.getImplementationClass().isSingleton()) {
                     IRubyObject possibleRealClass = ((MetaClass)method.getImplementationClass()).getAttached();
                     if(possibleRealClass instanceof RubyModule) {
                         excludeModuleName = "Meta:" + ((RubyModule)possibleRealClass).getName();
                     }
                 }
 
                 if ((instanceConfig.getExcludedMethods().contains(excludeModuleName) ||
                      instanceConfig.getExcludedMethods().contains(excludeModuleName +"#"+name) ||
                      instanceConfig.getExcludedMethods().contains(name))) {
                     method.setCallCount(-1);
                     return null;
                 }
             }
 
             String key = SexpMaker.create(name, method.getArgsNode(), method.getBodyNode());
             JITClassGenerator generator = new JITClassGenerator(name, key, context.getRuntime(), method, context, counts);
 
             Class<Script> sourceClass = (Class<Script>)instanceConfig.getClassCache().cacheClassByKey(key, generator);
 
             if (sourceClass == null) {
                 // class could not be found nor generated; give up on JIT and bail out
                 counts.failCount.incrementAndGet();
                 method.setCallCount(-1);
                 return null;
             }
 
             // successfully got back a jitted method
             counts.successCount.incrementAndGet();
 
             // finally, grab the script
             Script jitCompiledScript = sourceClass.newInstance();
 
             // add to the jitted methods set
             Set<Script> jittedMethods = context.getRuntime().getJittedMethods();
             jittedMethods.add(jitCompiledScript);
 
             // logEvery n methods based on configuration
             if (instanceConfig.getJitLogEvery() > 0) {
                 int methodCount = jittedMethods.size();
                 if (methodCount % instanceConfig.getJitLogEvery() == 0) {
                     log(method, name, "live compiled methods: " + methodCount);
                 }
             }
 
             if (instanceConfig.isJitLogging()) log(method, name, "done jitting");
 
             method.switchToJitted(jitCompiledScript, generator.callConfig());
             return null;
         } catch (Throwable t) {
             if (context.getRuntime().getDebug().isTrue()) t.printStackTrace();
             if (instanceConfig.isJitLoggingVerbose()) log(method, name, "could not compile", t.getMessage());
 
             counts.failCount.incrementAndGet();
             method.setCallCount(-1);
             return null;
         }
     }
 
     public static String getHashForString(String str) {
         return getHashForBytes(RubyEncoding.encodeUTF8(str));
     }
 
     public static String getHashForBytes(byte[] bytes) {
         try {
             MessageDigest sha1 = MessageDigest.getInstance("SHA1");
             sha1.update(bytes);
             byte[] digest = sha1.digest();
             StringBuilder builder = new StringBuilder();
             for (int i = 0; i < digest.length; i++) {
                 builder.append(Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 ));
             }
             return builder.toString().toUpperCase(Locale.ENGLISH);
         } catch (NoSuchAlgorithmException nsae) {
             throw new RuntimeException(nsae);
         }
     }
 
     public static void saveToCodeCache(Ruby ruby, byte[] bytecode, String packageName, File cachedClassFile) {
         String codeCache = RubyInstanceConfig.JIT_CODE_CACHE;
         File codeCacheDir = new File(codeCache);
         if (!codeCacheDir.exists()) {
             ruby.getWarnings().warn("jruby.jit.codeCache directory " + codeCacheDir + " does not exist");
         } else if (!codeCacheDir.isDirectory()) {
             ruby.getWarnings().warn("jruby.jit.codeCache directory " + codeCacheDir + " is not a directory");
         } else if (!codeCacheDir.canWrite()) {
             ruby.getWarnings().warn("jruby.jit.codeCache directory " + codeCacheDir + " is not writable");
         } else {
             if (!new File(codeCache, packageName).isDirectory()) {
                 boolean createdDirs = new File(codeCache, packageName).mkdirs();
                 if (!createdDirs) {
                     ruby.getWarnings().warn("could not create JIT cache dir: " + new File(codeCache, packageName));
                 }
             }
             // write to code cache
             FileOutputStream fos = null;
             try {
                 if (RubyInstanceConfig.JIT_LOADING_DEBUG) System.err.println("writing jitted code to to " + cachedClassFile);
                 fos = new FileOutputStream(cachedClassFile);
                 fos.write(bytecode);
             } catch (Exception e) {
                 e.printStackTrace();
                 // ignore
             } finally {
                 try {fos.close();} catch (Exception e) {}
             }
         }
     }
     
     public static class JITClassGenerator implements ClassCache.ClassGenerator {
         public JITClassGenerator(String name, String key, Ruby ruby, DefaultMethod method, ThreadContext context, JITCounts counts) {
-            this.packageName = "ruby/jit";
+            this.packageName = JITCompiler.RUBY_JIT_PREFIX;
             this.digestString = getHashForString(key);
             this.className = packageName + "/" + JavaNameMangler.mangleMethodName(name) + "_" + digestString;
             this.name = className.replaceAll("/", ".");
             this.bodyNode = method.getBodyNode();
             this.argsNode = method.getArgsNode();
             this.methodName = name;
             filename = calculateFilename(argsNode, bodyNode);
             staticScope = method.getStaticScope();
             asmCompiler = new StandardASMCompiler(className, filename);
             this.ruby = ruby;
             this.counts = counts;
         }
         
         @SuppressWarnings("unchecked")
         protected void compile() {
             if (bytecode != null) return;
             
             // check if we have a cached compiled version on disk
             String codeCache = RubyInstanceConfig.JIT_CODE_CACHE;
             File cachedClassFile = new File(codeCache + "/" + className + ".class");
 
             if (codeCache != null &&
                     cachedClassFile.exists()) {
                 FileInputStream fis = null;
                 try {
                     if (RubyInstanceConfig.JIT_LOADING_DEBUG) System.err.println("loading cached code from: " + cachedClassFile);
                     fis = new FileInputStream(cachedClassFile);
                     bytecode = new byte[(int)fis.getChannel().size()];
                     fis.read(bytecode);
                     name = new ClassReader(bytecode).getClassName();
                     return;
                 } catch (Exception e) {
                     // ignore and proceed to compile
                 } finally {
                     try {fis.close();} catch (Exception e) {}
                 }
             }
             
             // Time the compilation
             long start = System.nanoTime();
 
             asmCompiler.startScript(staticScope);
             final ASTCompiler compiler = ruby.getInstanceConfig().newCompiler();
 
             CompilerCallback args = new CompilerCallback() {
                 public void call(BodyCompiler context) {
                     compiler.compileArgs(argsNode, context, true);
                 }
             };
 
             ASTInspector inspector = new ASTInspector();
             if (ruby.getInstanceConfig().isJitDumping()) {
                 inspector = new ASTInspector(className, true);
             }
             // check args first, since body inspection can depend on args
             inspector.inspect(argsNode);
             inspector.inspect(bodyNode);
 
             BodyCompiler methodCompiler;
             if (bodyNode != null) {
                 // we have a body, do a full-on method
                 methodCompiler = asmCompiler.startFileMethod(args, staticScope, inspector);
                 compiler.compileBody(bodyNode, methodCompiler,true);
             } else {
                 // If we don't have a body, check for required or opt args
                 // if opt args, they could have side effects
                 // if required args, need to raise errors if too few args passed
                 // otherwise, method does nothing, make it a nop
                 if (argsNode != null && (argsNode.getRequiredArgsCount() > 0 || argsNode.getOptionalArgsCount() > 0)) {
                     methodCompiler = asmCompiler.startFileMethod(args, staticScope, inspector);
                     methodCompiler.loadNil();
                 } else {
                     methodCompiler = asmCompiler.startFileMethod(null, staticScope, inspector);
                     methodCompiler.loadNil();
                     jitCallConfig = CallConfiguration.FrameNoneScopeNone;
                 }
             }
             methodCompiler.endBody();
             asmCompiler.endScript(false, false);
             
             // if we haven't already decided on a do-nothing call
             if (jitCallConfig == null) {
                 jitCallConfig = inspector.getCallConfig();
             }
             
             bytecode = asmCompiler.getClassByteArray();
             if (ruby.getInstanceConfig().isJitDumping()) {
                 TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.out));
                 new ClassReader(bytecode).accept(tcv, 0);
             }
             
             if (bytecode.length > ruby.getInstanceConfig().getJitMaxSize()) {
                 bytecode = null;
                 throw new NotCompilableException(
                         "JITed method size exceeds configured max of " +
                         ruby.getInstanceConfig().getJitMaxSize());
             }
 
             if (codeCache != null) {
                 JITCompiler.saveToCodeCache(ruby, bytecode, packageName, cachedClassFile);
             }
             
             counts.compiledCount.incrementAndGet();
             counts.compileTime.addAndGet(System.nanoTime() - start);
             counts.codeSize.addAndGet(bytecode.length);
             counts.averageCompileTime.set(counts.compileTime.get() / counts.compiledCount.get());
             counts.averageCodeSize.set(counts.codeSize.get() / counts.compiledCount.get());
             synchronized (counts) {
                 if (counts.largestCodeSize.get() < bytecode.length) {
                     counts.largestCodeSize.set(bytecode.length);
                 }
             }
         }
 
         public void generate() {
             compile();
         }
         
         public byte[] bytecode() {
             return bytecode;
         }
 
         public String name() {
             return name;
         }
         
         public CallConfiguration callConfig() {
             compile();
             return jitCallConfig;
         }
 
         @Override
         public String toString() {
             return methodName + "() at " + bodyNode.getPosition().getFile() + ":" + bodyNode.getPosition().getLine();
         }
         
         private final StandardASMCompiler asmCompiler;
         private final StaticScope staticScope;
         private final Node bodyNode;
         private final ArgsNode argsNode;
         private final Ruby ruby;
         private final String packageName;
         private final String className;
         private final String filename;
         private final String methodName;
         private final JITCounts counts;
         private final String digestString;
 
         private CallConfiguration jitCallConfig;
         private byte[] bytecode;
         private String name;
     }
     
     private static String calculateFilename(ArgsNode argsNode, Node bodyNode) {
         if (bodyNode != null) return bodyNode.getPosition().getFile();
         if (argsNode != null) return argsNode.getPosition().getFile();
         
         return "__eval__";
     }
 
     static void log(DefaultMethod method, String name, String message, String... reason) {
         String className = method.getImplementationClass().getBaseName();
         
         if (className == null) className = "<anon class>";
 
         System.err.print(message + ":" + className + "." + name + " at " + method.getPosition());
         
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
         return counts.successCount.get();
     }
 
     public long getCompileCount() {
         return counts.compiledCount.get();
     }
 
     public long getFailCount() {
         return counts.failCount.get();
     }
 
     public long getCompileTime() {
         return counts.compileTime.get() / 1000;
     }
 
     public long getAbandonCount() {
         return counts.abandonCount.get();
     }
     
     public long getCodeSize() {
         return counts.codeSize.get();
     }
     
     public long getAverageCodeSize() {
         return counts.averageCodeSize.get();
     }
     
     public long getAverageCompileTime() {
         return counts.averageCompileTime.get() / 1000;
     }
     
     public long getLargestCodeSize() {
         return counts.largestCodeSize.get();
     }
 }
diff --git a/src/org/jruby/runtime/ThreadContext.java b/src/org/jruby/runtime/ThreadContext.java
index 333e41b43a..3d2b81ea6d 100644
--- a/src/org/jruby/runtime/ThreadContext.java
+++ b/src/org/jruby/runtime/ThreadContext.java
@@ -1,1541 +1,1542 @@
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
 
 import java.util.ArrayList;
 import org.jruby.runtime.profile.ProfileData;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.jruby.runtime.scope.ManyVarsDynamicScope;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyContinuation.Continuation;
 import org.jruby.RubyModule;
 import org.jruby.RubyString;
 import org.jruby.RubyThread;
 import org.jruby.ast.executable.RuntimeCache;
+import org.jruby.compiler.JITCompiler;
 import org.jruby.evaluator.ASTInterpreter;
 import org.jruby.exceptions.JumpException.ReturnJump;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.libraries.FiberLibrary.Fiber;
 import org.jruby.parser.BlockStaticScope;
 import org.jruby.parser.LocalStaticScope;
 import org.jruby.parser.StaticScope;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JavaNameMangler;
 
 public final class ThreadContext {
     public static ThreadContext newContext(Ruby runtime) {
         ThreadContext context = new ThreadContext(runtime);
 
         return context;
     }
     
     private final static int INITIAL_SIZE = 10;
     private final static int INITIAL_FRAMES_SIZE = 10;
     
     /** The number of calls after which to do a thread event poll */
     private final static int CALL_POLL_COUNT = 0xFFF;
 
     // runtime, nil, and runtimeCache cached here for speed of access from any thread
     public final Ruby runtime;
     public final IRubyObject nil;
     public final RuntimeCache runtimeCache;
     
     // Is this thread currently with in a function trace?
     private boolean isWithinTrace;
     
     // Is this thread currently doing an defined? defined should set things like $!
     private boolean isWithinDefined;
     
     private RubyThread thread;
     private Fiber fiber;
     
     private RubyModule[] parentStack = new RubyModule[INITIAL_SIZE];
     private int parentIndex = -1;
     
     private Frame[] frameStack = new Frame[INITIAL_FRAMES_SIZE];
     private int frameIndex = -1;
 
     private Backtrace[] backtrace = new Backtrace[1000];
     private int backtraceIndex = -1;
 
     public static class Backtrace {
         public Backtrace() {
         }
         public Backtrace(String klass, String method, String filename, int line) {
             this.method = method;
             this.filename = filename;
             this.line = line;
             this.klass = klass;
         }
         @Override
         public String toString() {
             return klass + "#" + method + " at " + filename + ":" + line;
         }
         @Override
         public Backtrace clone() {
             return new Backtrace(klass, method, filename, line);
         }
         public void update(String klass, String method, ISourcePosition position) {
             this.method = method;
             if (position == ISourcePosition.INVALID_POSITION) {
                 // use dummy values; there's no need for a real position here anyway
                 this.filename = "dummy";
                 this.line = -1;
             } else {
                 this.filename = position.getFile();
                 this.line = position.getLine();
             }
             this.klass = klass;
         }
         public void update(String klass, String method, String file, int line) {
             this.method = method;
             this.filename = file;
             this.line = line;
             this.klass = klass;
         }
         
         public String getFilename() {
             return filename;
         }
 
         public void setFilename(String filename) {
             this.filename = filename;
         }
 
         public String getKlass() {
             return klass;
         }
 
         public void setKlass(String klass) {
             this.klass = klass;
         }
 
         public int getLine() {
             return line;
         }
 
         public void setLine(int line) {
             this.line = line;
         }
 
         public String getMethod() {
             return method;
         }
 
         public void setMethod(String method) {
             this.method = method;
         }
         public String klass;
         public String method;
         public String filename;
         public int line;
     }
     
     // List of active dynamic scopes.  Each of these may have captured other dynamic scopes
     // to implement closures.
     private DynamicScope[] scopeStack = new DynamicScope[INITIAL_SIZE];
     private int scopeIndex = -1;
 
     private static final Continuation[] EMPTY_CATCHTARGET_STACK = new Continuation[0];
     private Continuation[] catchStack = EMPTY_CATCHTARGET_STACK;
     private int catchIndex = -1;
     
     // File where current executing unit is being evaluated
     private String file = "";
     
     // Line where current executing unit is being evaluated
     private int line = 0;
 
     // The profile data for this thread (TODO: don't create if we're not profiling)
     private final ProfileData profileData = new ProfileData();
 
     // In certain places, like grep, we don't use real frames for the
     // call blocks. This has the effect of not setting the backref in
     // the correct frame - this delta is activated to the place where
     // the grep is running in so that the backref will be set in an
     // appropriate place.
     private int rubyFrameDelta = 0;
     private boolean eventHooksEnabled = true;
     
     /**
      * Constructor for Context.
      */
     private ThreadContext(Ruby runtime) {
         this.runtime = runtime;
         this.nil = runtime.getNil();
         this.runtimeCache = runtime.getRuntimeCache();
         
         // TOPLEVEL self and a few others want a top-level scope.  We create this one right
         // away and then pass it into top-level parse so it ends up being the top level.
         StaticScope topStaticScope = new LocalStaticScope(null);
         pushScope(new ManyVarsDynamicScope(topStaticScope, null));
 
         Frame[] stack = frameStack;
         int length = stack.length;
         for (int i = 0; i < length; i++) {
             stack[i] = new Frame();
         }
         Backtrace[] stack2 = backtrace;
         int length2 = stack2.length;
         for (int i = 0; i < length2; i++) {
             stack2[i] = new Backtrace();
         }
         pushBacktrace("", "", "", 0);
         pushBacktrace("", "", "", 0);
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
 
         // associate the thread with this context, unless we're clearing the reference
         if (thread != null) {
             thread.setContext(this);
         }
     }
     
     public Fiber getFiber() {
         return fiber;
     }
     
     public void setFiber(Fiber fiber) {
         this.fiber = fiber;
     }
     
     //////////////////// CATCH MANAGEMENT ////////////////////////
     private void expandCatchIfNecessary() {
         int newSize = catchStack.length * 2;
         if (newSize == 0) newSize = 1;
         Continuation[] newCatchStack = new Continuation[newSize];
 
         System.arraycopy(catchStack, 0, newCatchStack, 0, catchStack.length);
         catchStack = newCatchStack;
     }
     
     public void pushCatch(Continuation catchTarget) {
         int index = ++catchIndex;
         if (index == catchStack.length) {
             expandCatchIfNecessary();
         }
         catchStack[index] = catchTarget;
     }
     
     public void popCatch() {
         catchIndex--;
     }
 
     /**
      * Find the active Continuation for the given tag. Must be called with an
      * interned string.
      *
      * @param tag The interned string to search for
      * @return The continuation associated with this tag
      */
     public Continuation getActiveCatch(Object tag) {
         for (int i = catchIndex; i >= 0; i--) {
             Continuation c = catchStack[i];
             if (c.tag.equals(tag)) return c;
         }
         return null;
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
         stack[index].updateFrame(clazz, self, name, block, callNumber);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     private void pushEvalFrame(IRubyObject self) {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         stack[index].updateFrameForEval(self, callNumber);
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
         stack[index].updateFrame(name);
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     public void pushFrame() {
         int index = ++this.frameIndex;
         Frame[] stack = frameStack;
         if (index + 1 == stack.length) {
             expandFramesIfNecessary();
         }
     }
     
     public void popFrame() {
         Frame frame = frameStack[frameIndex--];
         
         frame.clear();
     }
         
     private void popFrameReal(Frame oldFrame) {
         int index = frameIndex;
         Frame frame = frameStack[index];
         frameStack[index] = oldFrame;
         frameIndex = index - 1;
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
 
     public Frame[] getFrames(int delta) {
         int top = frameIndex + delta;
         Frame[] frames = new Frame[top + 1];
         for (int i = 0; i <= top; i++) {
             frames[i] = frameStack[i].duplicateForBacktrace();
         }
         return frames;
     }
 
     /////////////////// BACKTRACE ////////////////////
 
     private void expandBacktraceIfNecessary() {
         int newSize = backtrace.length * 2;
         backtrace = fillNewBacktrace(new Backtrace[newSize], newSize);
     }
 
     private Backtrace[] fillNewBacktrace(Backtrace[] newBacktrace, int newSize) {
         System.arraycopy(backtrace, 0, newBacktrace, 0, backtrace.length);
 
         for (int i = backtrace.length; i < newSize; i++) {
             newBacktrace[i] = new Backtrace();
         }
 
         return newBacktrace;
     }
 
     public void pushBacktrace(String klass, String method, ISourcePosition position) {
         int index = ++this.backtraceIndex;
         Backtrace[] stack = backtrace;
         stack[index].update(klass, method, position);
         if (index + 1 == stack.length) {
             expandBacktraceIfNecessary();
         }
     }
 
     public void pushBacktrace(String klass, String method, String file, int line) {
         int index = ++this.backtraceIndex;
         Backtrace[] stack = backtrace;
         stack[index].update(klass, method, file, line);
         if (index + 1 == stack.length) {
             expandBacktraceIfNecessary();
         }
     }
 
     public void popBacktrace() {
         backtraceIndex--;
     }
 
     /**
      * Search the frame stack for the given JumpTarget. Return true if it is
      * found and false otherwise. Skip the given number of frames before
      * beginning the search.
      * 
      * @param target The JumpTarget to search for
      * @param skipFrames The number of frames to skip before searching
      * @return
      */
     public boolean isJumpTargetAlive(int target, int skipFrames) {
         for (int i = frameIndex - skipFrames; i >= 0; i--) {
             if (frameStack[i].getJumpTarget() == target) return true;
         }
         return false;
     }
     
     public String getFrameName() {
         return getCurrentFrame().getName();
     }
     
     public IRubyObject getFrameSelf() {
         return getCurrentFrame().getSelf();
     }
     
     public int getFrameJumpTarget() {
         return getCurrentFrame().getJumpTarget();
     }
     
     public RubyModule getFrameKlazz() {
         return getCurrentFrame().getKlazz();
     }
     
     public Block getFrameBlock() {
         return getCurrentFrame().getBlock();
     }
     
     public String getFile() {
         return backtrace[backtraceIndex].filename;
     }
     
     public int getLine() {
         return backtrace[backtraceIndex].line;
     }
     
     public void setFile(String file) {
         backtrace[backtraceIndex].filename = file;
     }
     
     public void setLine(int line) {
         backtrace[backtraceIndex].line = line;
     }
     
     public void setFileAndLine(String file, int line) {
         backtrace[backtraceIndex].filename = file;
         backtrace[backtraceIndex].line = line;
     }
 
     public void setFileAndLine(ISourcePosition position) {
         backtrace[backtraceIndex].filename = position.getFile();
         backtrace[backtraceIndex].line = position.getStartLine();
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
     
     public int callNumber = 0;
 
     public int getCurrentTarget() {
         return callNumber;
     }
     
     public void callThreadPoll() {
         if ((callNumber++ & CALL_POLL_COUNT) == 0) pollThreadEvents();
     }
 
     public static void callThreadPoll(ThreadContext context) {
         if ((context.callNumber++ & CALL_POLL_COUNT) == 0) context.pollThreadEvents();
     }
     
     public void trace(RubyEvent event, String name, RubyModule implClass) {
         trace(event, name, implClass, backtrace[backtraceIndex].filename, backtrace[backtraceIndex].line);
     }
 
     public void trace(RubyEvent event, String name, RubyModule implClass, String file, int line) {
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
         assert parentIndex != -1 : "Trying to get RubyClass from empty stack";
         RubyModule parentModule = parentStack[parentIndex];
         return parentModule.getNonIncludedClass();
     }
 
     public RubyModule getPreviousRubyClass() {
         assert parentIndex != 0 : "Trying to get RubyClass from too-shallow stack";
         RubyModule parentModule = parentStack[parentIndex - 1];
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
     
     private static void addBackTraceElement(Ruby runtime, RubyArray backtrace, RubyStackTraceElement element) {
         RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'");
         backtrace.append(str);
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public IRubyObject createCallerBacktrace(Ruby runtime, int level) {
         RubyStackTraceElement[] trace = gatherCallerBacktrace(level);
 
         // scrub out .java core class names and replace with Ruby equivalents
         OUTER: for (int i = 0; i < trace.length; i++) {
             RubyStackTraceElement element = trace[i];
             String classDotMethod = element.getClassName() + "." + element.getMethodName();
             if (runtime.getBoundMethods().containsKey(classDotMethod)) {
                 String rubyName = runtime.getBoundMethods().get(classDotMethod);
                 // find first Ruby file+line
                 RubyStackTraceElement rubyElement = null;
                 int rubyIndex;
                 for (int j = i; j < trace.length; j++) {
                     rubyElement = trace[j];
                     if (!rubyElement.getFileName().endsWith(".java")) {
                         trace[i] = new RubyStackTraceElement(
                                 element.getClassName(),
                                 rubyName,
                                 rubyElement.getFileName(),
                                 rubyElement.getLineNumber(),
                                 rubyElement.isBinding(),
                                 rubyElement.getFrameType());
                         continue OUTER;
                     }
                 }
                 // no match, leave it as is
 
             }
         }
         
         RubyArray backtrace = runtime.newArray(trace.length - level);
 
         for (int i = level; i < trace.length; i++) {
             addBackTraceElement(runtime, backtrace, trace[i]);
         }
         
         return backtrace;
     }
     
     public RubyStackTraceElement[] gatherCallerBacktrace(int level) {
         Backtrace[] copy = new Backtrace[backtraceIndex + 1];
         System.arraycopy(backtrace, 0, copy, 0, backtraceIndex + 1);
         RubyStackTraceElement[] trace = gatherHybridBacktrace(
                 runtime,
                 copy,
                 Thread.currentThread().getStackTrace(),
                 false);
 
         return trace;
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
 
     public boolean isEventHooksEnabled() {
         return eventHooksEnabled;
     }
 
     public void setEventHooksEnabled(boolean flag) {
         eventHooksEnabled = flag;
     }
 
     public static class RubyStackTraceElement {
         private final StackTraceElement element;
         private final boolean binding;
         private final FrameType frameType;
 
         public RubyStackTraceElement(StackTraceElement element) {
             this.element = element;
             this.binding = false;
             this.frameType = FrameType.METHOD;
         }
 
         public RubyStackTraceElement(String cls, String method, String file, int line, boolean binding) {
             this(cls, method, file, line, binding, FrameType.METHOD);
         }
 
         public RubyStackTraceElement(String cls, String method, String file, int line, boolean binding, FrameType frameType) {
             this.element = new StackTraceElement(cls, method, file, line);
             this.binding = binding;
             this.frameType = frameType;
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
 
         public FrameType getFrameType() {
             return frameType;
         }
 
         public String toString() {
             return element.toString();
         }
     }
     
     /**
      * Create an Array with backtrace information.
      * @param runtime
      * @param level
      * @param nativeException
      * @return an Array with the backtrace
      */
     public Backtrace[] createBacktrace2(int level, boolean nativeException) {
         Backtrace[] newTrace = new Backtrace[backtraceIndex + 1];
         for (int i = 0; i <= backtraceIndex; i++) {
             newTrace[i] = backtrace[i].clone();
         }
         return newTrace;
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
 
     public static RubyStackTraceElement[] gatherRawBacktrace(Ruby runtime, StackTraceElement[] stackTrace, boolean filter) {
         List trace = new ArrayList(stackTrace.length);
         
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
             if (filter) {
                 if (element.getClassName().startsWith("org.jruby") ||
                         element.getLineNumber() < 0) {
                     continue;
                 }
             }
             trace.add(new RubyStackTraceElement(element));
         }
 
         RubyStackTraceElement[] rubyStackTrace = new RubyStackTraceElement[trace.size()];
         return (RubyStackTraceElement[])trace.toArray(rubyStackTrace);
     }
 
     private Frame pushFrameForBlock(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setVisibility(binding.getVisibility());
         
         return lastFrame;
     }
 
     private Frame pushFrameForEval(Binding binding) {
         Frame lastFrame = getNextFrame();
         Frame f = pushFrame(binding.getFrame());
         f.setVisibility(binding.getVisibility());
         return lastFrame;
     }
     
     public enum FrameType { METHOD, BLOCK, EVAL, CLASS, ROOT }
     public static final Map<String, FrameType> INTERPRETED_FRAMES = new HashMap<String, FrameType>();
     
     static {
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_METHOD", FrameType.METHOD);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_EVAL", FrameType.EVAL);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_CLASS", FrameType.CLASS);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_BLOCK", FrameType.BLOCK);
         INTERPRETED_FRAMES.put(ASTInterpreter.class.getName() + ".INTERPRET_ROOT", FrameType.ROOT);
     }
 
     public static RubyStackTraceElement[] gatherHybridBacktrace(Ruby runtime, Backtrace[] backtraceFrames, StackTraceElement[] stackTrace, boolean fullTrace) {
         List trace = new ArrayList(stackTrace.length);
 
         // a running index into the Ruby backtrace stack, incremented for each
         // interpreter frame we encounter in the Java backtrace.
         int rubyFrameIndex = backtraceFrames == null ? -1 : backtraceFrames.length - 1;
 
         // no Java trace, can't generate hybrid trace
         // TODO: Perhaps just generate the interpreter trace? Is this path ever hit?
         if (stackTrace == null) return null;
 
         // walk the Java stack trace, peeling off Java elements or Ruby elements
         for (int i = 0; i < stackTrace.length; i++) {
             StackTraceElement element = stackTrace[i];
 
             if (element.getFileName() != null &&
                     (element.getFileName().endsWith(".rb") ||
                     element.getFileName().equals("-e") ||
                     // FIXME: Formalize jitted method structure so this isn't quite as hacky
-                    element.getClassName().startsWith("ruby.jit.") ||
+                    element.getClassName().startsWith(JITCompiler.RUBY_JIT_PREFIX) ||
                     element.getMethodName().contains("$RUBY$"))) {
                 if (element.getLineNumber() == -1) continue;
                 
                 String methodName = element.getMethodName();
                 String className = element.getClassName();
 
                 // FIXME: Formalize jitted method structure so this isn't quite as hacky
-                if (className.startsWith("ruby.jit.")) {
+                if (className.startsWith(JITCompiler.RUBY_JIT_PREFIX)) {
                     // pull out and demangle the method name
-                    methodName = className.substring("ruby.jit.".length(), className.lastIndexOf("_"));
+                    methodName = className.substring(JITCompiler.RUBY_JIT_PREFIX.length() + 1, className.lastIndexOf("_"));
                     methodName = JavaNameMangler.demangleMethodName(methodName);
                     
                     trace.add(new RubyStackTraceElement(className, methodName, element.getFileName(), element.getLineNumber(), false));
 
                     // if it's a synthetic call, use it but gobble up parent calls
                     // TODO: need to formalize this better
                     if (element.getMethodName().contains("$RUBY$SYNTHETIC")) {
                         // gobble up at least one parent, and keep going if there's more synthetic frames
                         while (element.getMethodName().indexOf("$RUBY$SYNTHETIC") != -1) {
                             i++;
                             element = stackTrace[i];
                         }
                     }
                     continue;
                 }
 
                 // AOT formatted method names, need to be cleaned up
                 int RUBYindex = methodName.indexOf("$RUBY$");
                 if (RUBYindex >= 0) {
                     methodName = methodName.substring(RUBYindex + "$RUBY$".length());
 
                     // if it's a synthetic call, use it but gobble up parent calls
                     // TODO: need to formalize this better
                     if (methodName.startsWith("SYNTHETIC")) {
                         methodName = methodName.substring("SYNTHETIC".length());
                         methodName = JavaNameMangler.demangleMethodName(methodName);
                         trace.add(new RubyStackTraceElement(className, methodName, element.getFileName(), element.getLineNumber(), false));
 
                         // gobble up at least one parent, and keep going if there's more synthetic frames
                         while (element.getMethodName().indexOf("$RUBY$SYNTHETIC") != -1) {
                             i++;
                             element = stackTrace[i];
                         }
                         continue;
                     }
 
                     methodName = JavaNameMangler.demangleMethodName(methodName);
                     trace.add(new RubyStackTraceElement(className, methodName, element.getFileName(), element.getLineNumber(), false));
                     continue;
                 }
             }
 
             String dotClassMethod = element.getClassName() + "." + element.getMethodName();
             if (fullTrace || runtime.getBoundMethods().containsKey(dotClassMethod)) {
                 String filename = element.getFileName();
                 int lastDot = element.getClassName().lastIndexOf('.');
                 if (lastDot != -1) {
                     filename = element.getClassName().substring(0, lastDot + 1).replaceAll("\\.", "/") + filename;
                 }
                 trace.add(new RubyStackTraceElement(element.getClassName(), element.getMethodName(), filename, element.getLineNumber(), false));
 
                 if (!fullTrace) {
                     // we want to fall through below to add Ruby trace info as well
                     continue;
                 }
             }
 
             // try to mine out a Ruby frame using our list of interpreter entry-point markers
             String classMethod = element.getClassName() + "." + element.getMethodName();
             FrameType frameType = INTERPRETED_FRAMES.get(classMethod);
             if (frameType != null && rubyFrameIndex >= 0) {
                 // Frame matches one of our markers for "interpreted" calls
                 Backtrace rubyElement = backtraceFrames[rubyFrameIndex];
                 trace.add(new RubyStackTraceElement(rubyElement.klass, rubyElement.method, rubyElement.filename, rubyElement.line + 1, false));
                 rubyFrameIndex--;
                 continue;
             } else {
                 // frames not being included...
 //                RubyString str = RubyString.newString(runtime, createRubyBacktraceString(element));
 //                traceArray.append(str);
                 continue;
             }
         }
 
         RubyStackTraceElement[] rubyStackTrace = new RubyStackTraceElement[trace.size()];
         return (RubyStackTraceElement[])trace.toArray(rubyStackTrace);
     }
 
     public static IRubyObject renderBacktraceMRI(Ruby runtime, RubyStackTraceElement[] trace) {
         if (trace == null) {
             return runtime.getNil();
         }
         
         RubyArray traceArray = RubyArray.newArray(runtime);
 
         for (int i = 0; i < trace.length; i++) {
             RubyStackTraceElement element = trace[i];
 
             RubyString str = RubyString.newString(runtime, element.getFileName() + ":" + element.getLineNumber() + ":in `" + element.getMethodName() + "'");
             traceArray.append(str);
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
     }
     
     public void postMethodBacktraceAndScope() {
         postMethodScopeOnly();
     }
     
     public void preMethodBacktraceOnly(String name) {
     }
 
     public void preMethodBacktraceDummyScope(RubyModule clazz, String name, StaticScope staticScope) {
         RubyModule implementationClass = staticScope.getModule();
         // FIXME: This is currently only here because of some problems with IOOutputStream writing to a "bare" runtime without a proper scope
         if (implementationClass == null) {
             implementationClass = clazz;
         }
         pushScope(staticScope.getDummyScope());
         pushRubyClass(implementationClass);
     }
     
     public void postMethodBacktraceOnly() {
     }
 
     public void postMethodBacktraceDummyScope() {
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
     
     public void preRunThread(Frame[] currentFrames) {
         for (Frame frame : currentFrames) {
             pushFrame(frame);
         }
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
         binding.getFrame().setIsBindingFrame(true);
         Frame lastFrame = pushFrameForEval(binding);
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
 
     /**
      * Return a binding representing the current call's state
      * @return the current binding
      */
     public Binding currentBinding() {
         Frame frame = getCurrentFrame();
         return new Binding(frame, getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding currentBinding(IRubyObject self) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility and self.
      * @param self the self object to use
      * @param visibility the visibility to use
      * @return the current binding using the specified self and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified scope and self.
      * @param self the self object to use
      * @param visibility the scope to use
      * @return the current binding using the specified self and scope
      */
     public Binding currentBinding(IRubyObject self, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, frame.getVisibility(), getRubyClass(), scope, backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the current call's state but with the
      * specified visibility, scope, and self. For shared-scope binding
      * consumers like for loops.
      * 
      * @param self the self object to use
      * @param visibility the visibility to use
      * @param scope the scope to use
      * @return the current binding using the specified self, scope, and visibility
      */
     public Binding currentBinding(IRubyObject self, Visibility visibility, DynamicScope scope) {
         Frame frame = getCurrentFrame();
         return new Binding(self, frame, visibility, getRubyClass(), scope, backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the previous call's state
      * @return the current binding
      */
     public Binding previousBinding() {
         Frame frame = getPreviousFrame();
         return new Binding(frame, getPreviousRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Return a binding representing the previous call's state but with a specified self
      * @param self the self object to use
      * @return the current binding, using the specified self
      */
     public Binding previousBinding(IRubyObject self) {
         Frame frame = getPreviousFrame();
         return new Binding(self, frame, frame.getVisibility(), getPreviousRubyClass(), getCurrentScope(), backtrace[backtraceIndex].clone());
     }
 
     /**
      * Get the profile data for this thread (ThreadContext).
      *
      * @return the thread's profile data
      */
     public ProfileData getProfileData() {
         return profileData;
     }
 
     public int profileEnter(int nextMethod) {
         return profileData.profileEnter(nextMethod);
     }
 
     public int profileExit(int nextMethod) {
         return profileData.profileExit(nextMethod);
     }
 }
