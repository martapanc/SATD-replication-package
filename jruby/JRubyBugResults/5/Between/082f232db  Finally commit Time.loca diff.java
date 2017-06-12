diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index 717f578a16..96eea24cc2 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -2082,1184 +2082,1184 @@ public final class Ruby {
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
                 
                 RubyBinding binding = RubyBinding.newBinding(Ruby.this);
 
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
 
-    public Map<String, DateTimeZone> getLocalTimezoneCache() {
-        return localTimeZoneCache;
+    public Map<String, DateTimeZone> getTimezoneCache() {
+        return timeZoneCache;
     }
 
     public int getConstantGeneration() {
         return constantGeneration;
     }
 
     public synchronized void incrementConstantGeneration() {
         constantGeneration++;
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
             complexClass, rationalClass, enumeratorClass,
             arrayClass, hashClass, rangeClass, stringClass, encodingClass, converterClass, symbolClass,
             procClass, bindingClass, methodClass, unboundMethodClass,
             matchDataClass, regexpClass, timeClass, bignumClass, dirClass,
             fileClass, fileStatClass, ioClass, threadClass, threadGroupClass,
             continuationClass, structClass, tmsStruct, passwdStruct,
             groupStruct, procStatusClass, exceptionClass, runtimeError, ioError,
             scriptError, nameError, nameErrorMessage, noMethodError, signalException,
             rangeError, dummyClass, systemExit, localJumpError, nativeException,
             systemCallError, fatal, interrupt, typeError, argumentError, indexError,
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
 
-    private Map<String, DateTimeZone> localTimeZoneCache = new HashMap<String,DateTimeZone>();
+    private Map<String, DateTimeZone> timeZoneCache = new HashMap<String,DateTimeZone>();
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
diff --git a/src/org/jruby/RubyTime.java b/src/org/jruby/RubyTime.java
index 4541f4dad8..fe92868a9c 100644
--- a/src/org/jruby/RubyTime.java
+++ b/src/org/jruby/RubyTime.java
@@ -1,850 +1,884 @@
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
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
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
 package org.jruby;
 
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Locale;
 import java.util.Map;
 import java.util.TimeZone;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.RubyDateFormat;
 
 /** The Time class.
  * 
  * @author chadfowler, jpetersen
  */
 @JRubyClass(name="Time", include="Comparable")
 public class RubyTime extends RubyObject {
     public static final String UTC = "UTC";
     private DateTime dt;
     private long usec;
     
     private final static DateTimeFormatter ONE_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM  d HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TWO_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
 
     private final static DateTimeFormatter TO_S_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TO_S_UTC_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy").withLocale(Locale.ENGLISH);
 
     // There are two different popular TZ formats: legacy (AST+3:00:00, GMT-3), and
     // newer one (US/Pacific, America/Los_Angeles). This pattern is to detect
     // the legacy TZ format in order to convert it to the newer format
     // understood by Java API.
     private static final Pattern TZ_PATTERN
             = Pattern.compile("(\\D+?)([\\+-]?)(\\d+)(:\\d+)?(:\\d+)?");
     
     private static final ByteList TZ_STRING = ByteList.create("TZ");
-     
+
     public static DateTimeZone getLocalTimeZone(Ruby runtime) {
         RubyString tzVar = runtime.newString(TZ_STRING);
         RubyHash h = ((RubyHash)runtime.getObject().fastGetConstant("ENV"));
         IRubyObject tz = h.op_aref(runtime.getCurrentContext(), tzVar);
+
         if (tz == null || ! (tz instanceof RubyString)) {
             return DateTimeZone.getDefault();
         } else {
-            String zone = tz.toString();
-            DateTimeZone cachedZone = runtime.getLocalTimezoneCache().get(zone);
+            return getTimeZone(runtime, tz.toString());
+        }
+    }
+     
+    public static DateTimeZone getTimeZone(Ruby runtime, String zone) {
+        DateTimeZone cachedZone = runtime.getTimezoneCache().get(zone);
 
-            if (cachedZone != null) return cachedZone;
+        if (cachedZone != null) return cachedZone;
 
-            String originalZone = zone;
+        String originalZone = zone;
 
-            // Value of "TZ" property is of a bit different format,
-            // which confuses the Java's TimeZone.getTimeZone(id) method,
-            // and so, we need to convert it.
+        // Value of "TZ" property is of a bit different format,
+        // which confuses the Java's TimeZone.getTimeZone(id) method,
+        // and so, we need to convert it.
 
-            Matcher tzMatcher = TZ_PATTERN.matcher(zone);
-            if (tzMatcher.matches()) {                    
-                String sign = tzMatcher.group(2);
-                String hours = tzMatcher.group(3);
-                String minutes = tzMatcher.group(4);
+        Matcher tzMatcher = TZ_PATTERN.matcher(zone);
+        if (tzMatcher.matches()) {                    
+            String sign = tzMatcher.group(2);
+            String hours = tzMatcher.group(3);
+            String minutes = tzMatcher.group(4);
                 
-                // GMT+00:00 --> Etc/GMT, see "MRI behavior"
-                // comment below.
-                if (("00".equals(hours) || "0".equals(hours))
-                        && (minutes == null || ":00".equals(minutes) || ":0".equals(minutes))) {
-                    zone = "Etc/GMT";
-                } else {
-                    // Invert the sign, since TZ format and Java format
-                    // use opposite signs, sigh... Also, Java API requires
-                    // the sign to be always present, be it "+" or "-".
-                    sign = ("-".equals(sign)? "+" : "-");
-
-                    // Always use "GMT" since that's required by Java API.
-                    zone = "GMT" + sign + hours;
-
-                    if (minutes != null) {
-                        zone += minutes;
-                    }
+            // GMT+00:00 --> Etc/GMT, see "MRI behavior"
+            // comment below.
+            if (("00".equals(hours) || "0".equals(hours))
+                    && (minutes == null || ":00".equals(minutes) || ":0".equals(minutes))) {
+                zone = "Etc/GMT";
+            } else {
+                // Invert the sign, since TZ format and Java format
+                // use opposite signs, sigh... Also, Java API requires
+                // the sign to be always present, be it "+" or "-".
+                sign = ("-".equals(sign)? "+" : "-");
+
+                // Always use "GMT" since that's required by Java API.
+                zone = "GMT" + sign + hours;
+
+                if (minutes != null) {
+                    zone += minutes;
                 }
             }
+        }
 
-            // MRI behavior: With TZ equal to "GMT" or "UTC", Time.now
-            // is *NOT* considered as a proper GMT/UTC time:
-            //   ENV['TZ']="GMT"
-            //   Time.now.gmt? ==> false
-            //   ENV['TZ']="UTC"
-            //   Time.now.utc? ==> false
-            // Hence, we need to adjust for that.
-            if ("GMT".equalsIgnoreCase(zone) || "UTC".equalsIgnoreCase(zone)) {
-                zone = "Etc/" + zone;
-            }
-
-            DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
-            runtime.getLocalTimezoneCache().put(originalZone, dtz);
-            return dtz;
+        // MRI behavior: With TZ equal to "GMT" or "UTC", Time.now
+        // is *NOT* considered as a proper GMT/UTC time:
+        //   ENV['TZ']="GMT"
+        //   Time.now.gmt? ==> false
+        //   ENV['TZ']="UTC"
+        //   Time.now.utc? ==> false
+        // Hence, we need to adjust for that.
+        if ("GMT".equalsIgnoreCase(zone) || "UTC".equalsIgnoreCase(zone)) {
+            zone = "Etc/" + zone;
         }
+
+        DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
+        runtime.getTimezoneCache().put(originalZone, dtz);
+        return dtz;
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass, DateTime dt) {
         super(runtime, rubyClass);
         this.dt = dt;
     }
 
     // We assume that these two time instances
     // occurred at the same time.
     private static final long BASE_TIME_MILLIS = System.currentTimeMillis();
     private static final long BASE_TIME_NANOS = System.nanoTime();
 
     private static ObjectAllocator TIME_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             long usecsPassed = (System.nanoTime() - BASE_TIME_NANOS) / 1000L;
             long millisTime = BASE_TIME_MILLIS + usecsPassed / 1000L;
             long usecs = usecsPassed % 1000L;
 
             DateTimeZone dtz = getLocalTimeZone(runtime);
             DateTime dt = new DateTime(millisTime, dtz);
             RubyTime rt =  new RubyTime(runtime, klass, dt);
             rt.setUSec(usecs);
 
             return rt;
         }
     };
 
     public static RubyClass createTimeClass(Ruby runtime) {
         RubyClass timeClass = runtime.defineClass("Time", runtime.getObject(), TIME_ALLOCATOR);
         timeClass.index = ClassIndex.TIME;
         runtime.setTime(timeClass);
         
         timeClass.includeModule(runtime.getComparable());
         
         timeClass.defineAnnotatedMethods(RubyTime.class);
         
         return timeClass;
     }
     
     public void setUSec(long usec) {
         this.usec = usec;
     }
     
     public long getUSec() {
         return usec;
     }
     
     public void updateCal(DateTime dt) {
         this.dt = dt;
     }
     
     protected long getTimeInMillis() {
         return dt.getMillis();  // For JDK 1.4 we can use "cal.getTimeInMillis()"
     }
     
     public static RubyTime newTime(Ruby runtime, long milliseconds) {
         return newTime(runtime, new DateTime(milliseconds));
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt) {
         return new RubyTime(runtime, runtime.getTime(), dt);
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt, long usec) {
         RubyTime t = new RubyTime(runtime, runtime.getTime(), dt);
         t.setUSec(usec);
         return t;
     }
     
     @Override
     public Class<?> getJavaClass() {
         return Date.class;
     }
 
     @JRubyMethod(name = "initialize_copy", required = 1)
     @Override
     public IRubyObject initialize_copy(IRubyObject original) {
         if (!(original instanceof RubyTime)) {
             throw getRuntime().newTypeError("Expecting an instance of class Time");
         }
         
         RubyTime originalTime = (RubyTime) original;
         
         // We can just use dt, since it is immutable
         dt = originalTime.dt;
         usec = originalTime.usec;
         
         return this;
     }
 
     @JRubyMethod(name = "succ")
     public RubyTime succ() {
         return newTime(getRuntime(),dt.plusSeconds(1));
     }
 
     @JRubyMethod(name = {"gmtime", "utc"})
     public RubyTime gmtime() {
         dt = dt.withZone(DateTimeZone.UTC);
         return this;
     }
 
     @JRubyMethod(name = "localtime")
     public RubyTime localtime() {
         dt = dt.withZone(getLocalTimeZone(getRuntime()));
         return this;
     }
     
     @JRubyMethod(name = {"gmt?", "utc?", "gmtime?"})
     public RubyBoolean gmt() {
         return getRuntime().newBoolean(dt.getZone().getID().equals("UTC"));
     }
     
     @JRubyMethod(name = {"getgm", "getutc"})
     public RubyTime getgm() {
         return newTime(getRuntime(), dt.withZone(DateTimeZone.UTC), getUSec());
     }
 
     @JRubyMethod(name = "getlocal")
     public RubyTime getlocal() {
         return newTime(getRuntime(), dt.withZone(getLocalTimeZone(getRuntime())), getUSec());
     }
 
     @JRubyMethod(name = "strftime", required = 1)
     public RubyString strftime(IRubyObject format) {
         final RubyDateFormat rubyDateFormat = new RubyDateFormat("-", Locale.US);
         rubyDateFormat.applyPattern(format.toString());
         rubyDateFormat.setDateTime(dt);
         String result = rubyDateFormat.format(null);
         return getRuntime().newString(result);
     }
     
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) >= 0);
         }
         
         return RubyComparable.op_ge(context, this, other);
     }
     
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) > 0);
         }
         
         return RubyComparable.op_gt(context, this, other);
     }
     
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) <= 0);
         }
         
         return RubyComparable.op_le(context, this, other);
     }
     
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) < 0);
         }
         
         return RubyComparable.op_lt(context, this, other);
     }
     
     private int cmp(RubyTime other) {
         long millis = getTimeInMillis();
 		long millis_other = other.getTimeInMillis();
         long usec_other = other.usec;
         
 		if (millis > millis_other || (millis == millis_other && usec > usec_other)) {
 		    return 1;
 		} else if (millis < millis_other || (millis == millis_other && usec < usec_other)) {
 		    return -1;
 		} 
 
         return 0;
     }
     
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject other) {
         long time = getTimeInMillis();
 
         if (other instanceof RubyTime) {
             throw getRuntime().newTypeError("time + time ?");
         }
         long adjustment = (long) (RubyNumeric.num2dbl(other) * 1000000);
         int micro = (int) (adjustment % 1000);
         adjustment = adjustment / 1000;
 
         time += adjustment;
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
         newTime.setUSec(micro);
 
         return newTime;
     }
     
     private IRubyObject opMinus(RubyTime other) {
         long time = getTimeInMillis() * 1000 + getUSec();
 
         time -= other.getTimeInMillis() * 1000 + other.getUSec();
         
         return RubyFloat.newFloat(getRuntime(), time / 1000000.0); // float number of seconds
     }
 
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_minus(IRubyObject other) {
         if (other instanceof RubyTime) return opMinus((RubyTime) other);
         
         long time = getTimeInMillis();
         long adjustment = (long) (RubyNumeric.num2dbl(other) * 1000000);
         int micro = (int) (adjustment % 1000);
         adjustment = adjustment / 1000;
 
         time -= adjustment;
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
         newTime.setUSec(micro);
 
         return newTime;
     }
 
     @JRubyMethod(name = "===", required = 1)
     @Override
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return (RubyNumeric.fix2int(callMethod(context, "<=>", other)) == 0) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return context.getRuntime().newFixnum(cmp((RubyTime) other));
         }
 
         return context.getRuntime().getNil();
     }
     
     @JRubyMethod(name = "eql?", required = 1)
     @Override
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyTime) {
             RubyTime otherTime = (RubyTime)other; 
             return (usec == otherTime.usec && getTimeInMillis() == otherTime.getTimeInMillis()) ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = {"asctime", "ctime"})
     public RubyString asctime() {
         DateTimeFormatter simpleDateFormat;
 
         if (dt.getDayOfMonth() < 10) {
             simpleDateFormat = ONE_DAY_CTIME_FORMATTER;
         } else {
             simpleDateFormat = TWO_DAY_CTIME_FORMATTER;
         }
         String result = simpleDateFormat.print(dt);
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = {"to_s", "inspect"})
     @Override
     public IRubyObject to_s() {
         DateTimeFormatter simpleDateFormat;
         if (dt.getZone() == DateTimeZone.UTC) {
             simpleDateFormat = TO_S_UTC_FORMATTER;
         } else {
             simpleDateFormat = TO_S_FORMATTER;
         }
 
         String result = simpleDateFormat.print(dt);
 
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = "to_a")
     @Override
     public RubyArray to_a() {
         return getRuntime().newArrayNoCopy(new IRubyObject[] { sec(), min(), hour(), mday(), month(), 
                 year(), wday(), yday(), isdst(), zone() });
     }
 
     @JRubyMethod(name = "to_f")
     public RubyFloat to_f() {
         long time = getTimeInMillis();
         time = time * 1000 + usec;
         return RubyFloat.newFloat(getRuntime(), time / 1000000.0);
     }
 
     @JRubyMethod(name = {"to_i", "tv_sec"})
     public RubyInteger to_i() {
         return getRuntime().newFixnum(getTimeInMillis() / 1000);
     }
 
     @JRubyMethod(name = {"usec", "tv_usec"})
     public RubyInteger usec() {
         return getRuntime().newFixnum(dt.getMillisOfSecond() * 1000 + getUSec());
     }
 
     public void setMicroseconds(long mic) {
         long millis = getTimeInMillis() % 1000;
         long withoutMillis = getTimeInMillis() - millis;
         withoutMillis += (mic / 1000);
         dt = dt.withMillis(withoutMillis);
         usec = mic % 1000;
     }
     
     public long microseconds() {
     	return getTimeInMillis() % 1000 * 1000 + usec;
     }
 
     @JRubyMethod(name = "sec")
     public RubyInteger sec() {
         return getRuntime().newFixnum(dt.getSecondOfMinute());
     }
 
     @JRubyMethod(name = "min")
     public RubyInteger min() {
         return getRuntime().newFixnum(dt.getMinuteOfHour());
     }
 
     @JRubyMethod(name = "hour")
     public RubyInteger hour() {
         return getRuntime().newFixnum(dt.getHourOfDay());
     }
 
     @JRubyMethod(name = {"mday", "day"})
     public RubyInteger mday() {
         return getRuntime().newFixnum(dt.getDayOfMonth());
     }
 
     @JRubyMethod(name = {"month", "mon"})
     public RubyInteger month() {
         return getRuntime().newFixnum(dt.getMonthOfYear());
     }
 
     @JRubyMethod(name = "year")
     public RubyInteger year() {
         return getRuntime().newFixnum(dt.getYear());
     }
 
     @JRubyMethod(name = "wday")
     public RubyInteger wday() {
         return getRuntime().newFixnum((dt.getDayOfWeek()%7));
     }
 
     @JRubyMethod(name = "yday")
     public RubyInteger yday() {
         return getRuntime().newFixnum(dt.getDayOfYear());
     }
 
     @JRubyMethod(name = {"gmt_offset", "gmtoff", "utc_offset"})
     public RubyInteger gmt_offset() {
         int offset = dt.getZone().getOffsetFromLocal(dt.getMillis());
         
         return getRuntime().newFixnum((int)(offset/1000));
     }
 
     @JRubyMethod(name = {"isdst", "dst?"})
     public RubyBoolean isdst() {
         return getRuntime().newBoolean(!dt.getZone().isStandardOffset(dt.getMillis()));
     }
 
     @JRubyMethod(name = "zone")
     public RubyString zone() {
         String zone = dt.getZone().getShortName(dt.getMillis());
         if(zone.equals("+00:00")) {
             zone = "GMT";
         }
         return getRuntime().newString(zone);
     }
 
     public void setDateTime(DateTime dt) {
         this.dt = dt;
     }
 
     public DateTime getDateTime() {
         return this.dt;
     }
 
     public Date getJavaDate() {
         return this.dt.toDate();
     }
 
     @JRubyMethod(name = "hash")
     @Override
     public RubyFixnum hash() {
     	// modified to match how hash is calculated in 1.8.2
         return getRuntime().newFixnum((int)(((dt.getMillis() / 1000) ^ microseconds()) << 1) >> 1);
     }    
 
     @JRubyMethod(name = "_dump", optional = 1, frame = true)
     public RubyString dump(IRubyObject[] args, Block unusedBlock) {
         RubyString str = (RubyString) mdump(new IRubyObject[] { this });
         str.syncVariables(this.getVariableList());
         return str;
     }    
 
     public RubyObject mdump(final IRubyObject[] args) {
         RubyTime obj = (RubyTime)args[0];
         DateTime dateTime = obj.dt.withZone(DateTimeZone.UTC);
         byte dumpValue[] = new byte[8];
         int pe = 
             0x1                                 << 31 |
             (dateTime.getYear()-1900)           << 14 |
             (dateTime.getMonthOfYear()-1)       << 10 |
             dateTime.getDayOfMonth()            << 5  |
             dateTime.getHourOfDay();
         int se =
             dateTime.getMinuteOfHour()          << 26 |
             dateTime.getSecondOfMinute()        << 20 |
             (dateTime.getMillisOfSecond() * 1000 + (int)usec); // dump usec, not msec
 
         for(int i = 0; i < 4; i++) {
             dumpValue[i] = (byte)(pe & 0xFF);
             pe >>>= 8;
         }
         for(int i = 4; i < 8 ;i++) {
             dumpValue[i] = (byte)(se & 0xFF);
             se >>>= 8;
         }
         return RubyString.newString(obj.getRuntime(), new ByteList(dumpValue,false));
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(Block block) {
         return this;
     }
     
     /* Time class methods */
     
     public static IRubyObject s_new(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, new DateTime(getLocalTimeZone(runtime)));
         time.callInit(args,block);
         return time;
     }
 
     /**
      * @deprecated Use {@link #newInstance(ThreadContext, IRubyObject)}
      */
     @Deprecated
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         return newInstance(context, recv);
     }
 
     @JRubyMethod(name = "now", backtrace = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv) {
         IRubyObject obj = ((RubyClass) recv).allocate();
         obj.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, obj);
         return obj;
     }
 
     @JRubyMethod(name = "at",  meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject arg) {
         Ruby runtime = context.getRuntime();
         final RubyTime time;
 
         if (arg instanceof RubyTime) {
             RubyTime other = (RubyTime) arg;
             time = new RubyTime(runtime, (RubyClass) recv, other.dt);
             time.setUSec(other.getUSec());
         } else {
             time = new RubyTime(runtime, (RubyClass) recv,
                     new DateTime(0L, getLocalTimeZone(runtime)));
 
             long seconds = RubyNumeric.num2long(arg);
             long millisecs = 0;
             long microsecs = 0;
 
             // In the case of two arguments, MRI will discard the portion of
             // the first argument after a decimal point (i.e., "floor").
             // However in the case of a single argument, any portion after
             // the decimal point is honored.
             if (arg instanceof RubyFloat) {
                 double dbl = ((RubyFloat) arg).getDoubleValue();
                 long micro = (long) ((dbl - seconds) * 1000000);
                 millisecs = micro / 1000;
                 microsecs = micro % 1000;
             }
             time.setUSec(microsecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
         }
 
         time.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, time);
 
         return time;
     }
 
     @JRubyMethod(name = "at", meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = context.getRuntime();
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv,
                 new DateTime(0L, getLocalTimeZone(runtime)));
 
             long seconds = RubyNumeric.num2long(arg1);
             long millisecs = 0;
             long microsecs = 0;
 
             long tmp = RubyNumeric.num2long(arg2);
             millisecs = tmp / 1000;
             microsecs = tmp % 1000;
 
             time.setUSec(microsecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
 
             time.getMetaClass().getBaseCallSites()[RubyClass.CS_IDX_INITIALIZE].call(context, recv, time);
 
         return time;
     }
 
     @JRubyMethod(name = {"local", "mktime"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_local(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, false);
     }
 
     @JRubyMethod(name = {"utc", "gm"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_utc(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, true);
     }
 
     @JRubyMethod(name = "_load", required = 1, frame = true, meta = true)
     public static RubyTime load(IRubyObject recv, IRubyObject from, Block block) {
         return s_mload(recv, (RubyTime)(((RubyClass)recv).allocate()), from);
     }
 
     protected static RubyTime s_mload(IRubyObject recv, RubyTime time, IRubyObject from) {
         Ruby runtime = recv.getRuntime();
 
         DateTime dt = new DateTime(DateTimeZone.UTC);
 
         byte[] fromAsBytes = null;
         fromAsBytes = from.convertToString().getBytes();
         if(fromAsBytes.length != 8) {
             throw runtime.newTypeError("marshaled time format differ");
         }
         int p=0;
         int s=0;
         for (int i = 0; i < 4; i++) {
             p |= ((int)fromAsBytes[i] & 0xFF) << (8 * i);
         }
         for (int i = 4; i < 8; i++) {
             s |= ((int)fromAsBytes[i] & 0xFF) << (8 * (i - 4));
         }
         if ((p & (1<<31)) == 0) {
             dt = dt.withMillis(p * 1000L + s);
         } else {
             p &= ~(1<<31);
             dt = dt.withYear(((p >>> 14) & 0xFFFF) + 1900);
             dt = dt.withMonthOfYear(((p >>> 10) & 0xF) + 1);
             dt = dt.withDayOfMonth(((p >>> 5)  & 0x1F));
             dt = dt.withHourOfDay((p & 0x1F));
             dt = dt.withMinuteOfHour(((s >>> 26) & 0x3F));
             dt = dt.withSecondOfMinute(((s >>> 20) & 0x3F));
             // marsaling dumps usec, not msec
             dt = dt.withMillisOfSecond((s & 0xFFFFF) / 1000);
             dt = dt.withZone(getLocalTimeZone(runtime));
             time.setUSec((s & 0xFFFFF) % 1000);
         }
         time.setDateTime(dt);
 
         from.getInstanceVariables().copyInstanceVariablesInto(time);
         return time;
     }
 
     private static final String[] MONTHS = {"jan", "feb", "mar", "apr", "may", "jun",
                                             "jul", "aug", "sep", "oct", "nov", "dec"};
 
     private static final Map<String, Integer> MONTHS_MAP = new HashMap<String, Integer>();
     static {
         for (int i = 0; i < MONTHS.length; i++) {
             MONTHS_MAP.put(MONTHS[i], i + 1);
         }
     }
 
     private static final int[] time_min = {1, 0, 0, 0, Integer.MIN_VALUE};
     private static final int[] time_max = {31, 23, 59, 60, Integer.MAX_VALUE};
 
     private static final int ARG_SIZE = 7;
 
     private static RubyTime createTime(IRubyObject recv, IRubyObject[] args, boolean gmt) {
         Ruby runtime = recv.getRuntime();
         int len = ARG_SIZE;
+        Boolean isDst = null;
 
+        DateTimeZone dtz;
+        if (gmt) {
+            dtz = DateTimeZone.UTC;
+        } else if (args.length == 10 && args[9] instanceof RubyString) {
+            dtz = getTimeZone(runtime, ((RubyString) args[9]).toString());
+        } else {
+            dtz = getLocalTimeZone(runtime);
+        }
+ 
         if (args.length == 10) {
+	    if(args[8] instanceof RubyBoolean) {
+	        isDst = ((RubyBoolean)args[8]).isTrue();
+	    }
             args = new IRubyObject[] { args[5], args[4], args[3], args[2], args[1], args[0], runtime.getNil() };
         } else {
             // MRI accepts additional wday argument which appears to be ignored.
             len = args.length;
 
             if (len < ARG_SIZE) {
                 IRubyObject[] newArgs = new IRubyObject[ARG_SIZE];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
                 for (int i = len; i < ARG_SIZE; i++) {
                     newArgs[i] = runtime.getNil();
                 }
                 args = newArgs;
                 len = ARG_SIZE;
             }
         }
 
         if (args[0] instanceof RubyString) {
             args[0] = RubyNumeric.str2inum(runtime, (RubyString) args[0], 10, false);
         }
 
         int year = (int) RubyNumeric.num2long(args[0]);
         int month = 1;
 
         if (len > 1) {
             if (!args[1].isNil()) {
                 IRubyObject tmp = args[1].checkStringType();
                 if (!tmp.isNil()) {
                     String monthString = tmp.toString().toLowerCase();
                     Integer monthInt = MONTHS_MAP.get(monthString);
 
                     if (monthInt != null) {
                         month = monthInt;
                     } else {
                         try {
                             month = Integer.parseInt(monthString);
                         } catch (NumberFormatException nfExcptn) {
                             throw runtime.newArgumentError("Argument out of range.");
                         }
                     }
                 } else {
                     month = (int) RubyNumeric.num2long(args[1]);
                 }
             }
             if (1 > month || month > 12) {
                 throw runtime.newArgumentError("Argument out of range: for month: " + month);
             }
         }
 
         int[] int_args = { 1, 0, 0, 0, 0, 0 };
 
         for (int i = 0; int_args.length >= i + 2; i++) {
             if (!args[i + 2].isNil()) {
                 if (!(args[i + 2] instanceof RubyNumeric)) {
                     args[i + 2] = args[i + 2].callMethod(
                             runtime.getCurrentContext(), "to_i");
                 }
 
                 long value = RubyNumeric.num2long(args[i + 2]);
                 if (time_min[i] > value || value > time_max[i]) {
                     throw runtime.newArgumentError("argument out of range.");
                 }
                 int_args[i] = (int) value;
             }
         }
 
         if (0 <= year && year < 39) {
             year += 2000;
         } else if (69 <= year && year < 139) {
             year += 1900;
         }
 
-        DateTimeZone dtz;
-        if (gmt) {
-            dtz = DateTimeZone.UTC;
-        } else {
-            dtz = getLocalTimeZone(runtime);
-        }
-
         DateTime dt;
         // set up with min values and then add to allow rolling over
         try {
-            dt = new DateTime(year, 1, 1, 0, 0 , 0, 0, dtz);
+            dt = new DateTime(year, 1, 1, 0, 0 , 0, 0, DateTimeZone.UTC);
 
             dt = dt.plusMonths(month - 1)
                     .plusDays(int_args[0] - 1)
                     .plusHours(int_args[1])
                     .plusMinutes(int_args[2])
                     .plusSeconds(int_args[3]);
+
+	    dt = dt.withZoneRetainFields(dtz);
+
+	    // we might need to perform a DST correction
+	    if(isDst != null) {
+                // the instant at which we will ask dtz what the difference between DST and
+                // standard time is
+                long offsetCalculationInstant = dt.getMillis();
+
+                // if we might be moving this time from !DST -> DST, the offset is assumed
+                // to be the same as it was just before we last moved from DST -> !DST
+                if(dtz.isStandardOffset(dt.getMillis()))
+                    offsetCalculationInstant = dtz.previousTransition(offsetCalculationInstant);
+
+                int offset = dtz.getStandardOffset(offsetCalculationInstant) -
+                             dtz.getOffset(offsetCalculationInstant);
+
+                if (!isDst && !dtz.isStandardOffset(dt.getMillis())) {
+                    dt = dt.minusMillis(offset);
+                }
+                if (isDst &&  dtz.isStandardOffset(dt.getMillis())) {
+                    dt = dt.plusMillis(offset);
+                }
+	    }
         } catch (org.joda.time.IllegalFieldValueException e) {
             throw runtime.newArgumentError("time out of range");
         }
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, dt);
         // Ignores usec if 8 args (for compatibility with parsedate) or if not supplied.
         if (args.length != 8 && !args[6].isNil()) {
             int usec = int_args[4] % 1000;
             int msec = int_args[4] / 1000;
 
             if (int_args[4] < 0) {
                 msec -= 1;
                 usec += 1000;
             }
             time.dt = dt.withMillis(dt.getMillis() + msec);
             time.setUSec(usec);
         }
 
         time.callInit(IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         return time;
     }
 }
