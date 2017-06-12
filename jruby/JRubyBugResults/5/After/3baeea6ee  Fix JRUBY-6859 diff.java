diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index f79aa3e02d..789da30988 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -3069,1396 +3069,1398 @@ public final class Ruby {
         return RubyString.newStringShared(this, byteList);
     }    
 
     public RubySymbol newSymbol(String name) {
         return symbolTable.getSymbol(name);
     }
 
     public RubySymbol newSymbol(ByteList name) {
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
 
     public RaiseException newArgumentError(String name, int got, int expected) {
         return newRaiseException(getArgumentError(), "wrong number of arguments calling `" + name + "` (" + got + " for " + expected + ")");
     }
 
     public RaiseException newErrnoEBADFError() {
         return newRaiseException(getErrno().getClass("EBADF"), "Bad file descriptor");
     }
 
     public RaiseException newErrnoEISCONNError() {
         return newRaiseException(getErrno().getClass("EISCONN"), "Socket is already connected");
     }
 
     public RaiseException newErrnoEINPROGRESSError() {
         return newRaiseException(getErrno().getClass("EINPROGRESS"), "Operation now in progress");
     }
 
     public RaiseException newErrnoEINPROGRESSWritableError() {
         return newLightweightErrnoException(getModule("JRuby").getClass("EINPROGRESSWritable"), "");
     }
 
     public RaiseException newErrnoENOPROTOOPTError() {
         return newRaiseException(getErrno().getClass("ENOPROTOOPT"), "Protocol not available");
     }
 
     public RaiseException newErrnoEPIPEError() {
         return newRaiseException(getErrno().getClass("EPIPE"), "Broken pipe");
     }
 
     public RaiseException newErrnoECONNABORTEDError() {
         return newRaiseException(getErrno().getClass("ECONNABORTED"),
                 "An established connection was aborted by the software in your host machine");
     }
 
     public RaiseException newErrnoECONNREFUSEDError() {
         return newRaiseException(getErrno().getClass("ECONNREFUSED"), "Connection refused");
     }
 
     public RaiseException newErrnoECONNRESETError() {
         return newRaiseException(getErrno().getClass("ECONNRESET"), "Connection reset by peer");
     }
 
     public RaiseException newErrnoEADDRINUSEError() {
         return newRaiseException(getErrno().getClass("EADDRINUSE"), "Address in use");
     }
 
     public RaiseException newErrnoEADDRINUSEError(String message) {
         return newRaiseException(getErrno().getClass("EADDRINUSE"), message);
     }
 
     public RaiseException newErrnoEHOSTUNREACHError(String message) {
         return newRaiseException(getErrno().getClass("EHOSTUNREACH"), message);
     }
 
     public RaiseException newErrnoEINVALError() {
         return newRaiseException(getErrno().getClass("EINVAL"), "Invalid file");
     }
 
     public RaiseException newErrnoENOENTError() {
         return newRaiseException(getErrno().getClass("ENOENT"), "File not found");
     }
 
     public RaiseException newErrnoEACCESError(String message) {
         return newRaiseException(getErrno().getClass("EACCES"), message);
     }
 
     public RaiseException newErrnoEAGAINError(String message) {
         return newLightweightErrnoException(getErrno().getClass("EAGAIN"), message);
     }
 
     public RaiseException newErrnoEAGAINReadableError(String message) {
         return newLightweightErrnoException(getModule("JRuby").getClass("EAGAINReadable"), message);
     }
 
     public RaiseException newErrnoEAGAINWritableError(String message) {
         return newLightweightErrnoException(getModule("JRuby").getClass("EAGAINWritable"), message);
     }
 
     public RaiseException newErrnoEISDirError(String message) {
         return newRaiseException(getErrno().getClass("EISDIR"), message);
     }
 
     public RaiseException newErrnoEPERMError(String name) {
         return newRaiseException(getErrno().getClass("EPERM"), "Operation not permitted - " + name);
     }
 
     public RaiseException newErrnoEISDirError() {
         return newErrnoEISDirError("Is a directory");
     }
 
     public RaiseException newErrnoESPIPEError() {
         return newRaiseException(getErrno().getClass("ESPIPE"), "Illegal seek");
     }
 
     public RaiseException newErrnoEBADFError(String message) {
         return newRaiseException(getErrno().getClass("EBADF"), message);
     }
 
     public RaiseException newErrnoEINPROGRESSError(String message) {
         return newRaiseException(getErrno().getClass("EINPROGRESS"), message);
     }
 
     public RaiseException newErrnoEINPROGRESSWritableError(String message) {
         return newLightweightErrnoException(getModule("JRuby").getClass("EINPROGRESSWritable"), message);
     }
 
     public RaiseException newErrnoEISCONNError(String message) {
         return newRaiseException(getErrno().getClass("EISCONN"), message);
     }
 
     public RaiseException newErrnoEINVALError(String message) {
         return newRaiseException(getErrno().getClass("EINVAL"), message);
     }
 
     public RaiseException newErrnoENOTDIRError(String message) {
         return newRaiseException(getErrno().getClass("ENOTDIR"), message);
     }
 
     public RaiseException newErrnoENOTEMPTYError(String message) {
         return newRaiseException(getErrno().getClass("ENOTEMPTY"), message);
     }
 
     public RaiseException newErrnoENOTSOCKError(String message) {
         return newRaiseException(getErrno().getClass("ENOTSOCK"), message);
     }
 
     public RaiseException newErrnoENOTCONNError(String message) {
         return newRaiseException(getErrno().getClass("ENOTCONN"), message);
     }
 
     public RaiseException newErrnoENOTCONNError() {
         return newRaiseException(getErrno().getClass("ENOTCONN"), "Socket is not connected");
     }
 
     public RaiseException newErrnoENOENTError(String message) {
         return newRaiseException(getErrno().getClass("ENOENT"), message);
     }
 
     public RaiseException newErrnoESPIPEError(String message) {
         return newRaiseException(getErrno().getClass("ESPIPE"), message);
     }
 
     public RaiseException newErrnoEEXISTError(String message) {
         return newRaiseException(getErrno().getClass("EEXIST"), message);
     }
     
     public RaiseException newErrnoEDOMError(String message) {
         return newRaiseException(getErrno().getClass("EDOM"), "Domain error - " + message);
     }   
     
     public RaiseException newErrnoECHILDError() {
         return newRaiseException(getErrno().getClass("ECHILD"), "No child processes");
     }    
 
     public RaiseException newErrnoEADDRNOTAVAILError(String message) {
         return newRaiseException(getErrno().getClass("EADDRNOTAVAIL"), message);
     }
 
     public RaiseException newErrnoESRCHError() {
         return newRaiseException(getErrno().getClass("ESRCH"), null);
     }
 
     public RaiseException newErrnoEWOULDBLOCKError() {
         return newRaiseException(getErrno().getClass("EWOULDBLOCK"), null);
     }
 
     public RaiseException newErrnoEDESTADDRREQError(String func) {
         return newRaiseException(getErrno().getClass("EDESTADDRREQ"), func);
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
 
     public RaiseException newKeyError(String message) {
         return newRaiseException(getKeyError(), message);
     }
 
     public RaiseException newErrnoFromLastPOSIXErrno() {
         RubyClass errnoClass = getErrno(getPosix().errno());
         if (errnoClass == null) errnoClass = systemCallError;
 
         return newRaiseException(errnoClass, null);
     }
 
     public RaiseException newErrnoFromInt(int errno, String message) {
         RubyClass errnoClass = getErrno(errno);
         if (errnoClass != null) {
             return newRaiseException(errnoClass, message);
         } else {
             return newSystemCallError("Unknown Error (" + errno + ") - " + message);
         }
     }
 
     public RaiseException newErrnoFromInt(int errno) {
         Errno errnoObj = Errno.valueOf(errno);
         if (errnoObj == null) {
             return newSystemCallError("Unknown Error (" + errno + ")");
         }
         String message = errnoObj.description();
         return newErrnoFromInt(errno, message);
     }
 
     private final static Pattern ADDR_NOT_AVAIL_PATTERN = Pattern.compile("assign.*address");
 
     public RaiseException newErrnoEADDRFromBindException(BindException be) {
         String msg = be.getMessage();
         if (msg == null) {
             msg = "bind";
         } else {
             msg = "bind - " + msg;
         }
         // This is ugly, but what can we do, Java provides the same BindingException
         // for both EADDRNOTAVAIL and EADDRINUSE, so we differentiate the errors
         // based on BindException's message.
         if(ADDR_NOT_AVAIL_PATTERN.matcher(msg).find()) {
             return newErrnoEADDRNOTAVAILError(msg);
         } else {
             return newErrnoEADDRINUSEError(msg);
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
         return newRaiseException(fastGetClass("Iconv").getClass("InvalidEncoding"), message);
     }
     
     public RaiseException newIllegalSequence(String message) {
         return newRaiseException(fastGetClass("Iconv").getClass("IllegalSequence"), message);
     }
 
     public RaiseException newNoMethodError(String message, String name, IRubyObject args) {
         return new RaiseException(new RubyNoMethodError(this, getNoMethodError(), message, name, args), true);
     }
 
     public RaiseException newNameError(String message, String name) {
         return newNameError(message, name, null);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException) {
         return newNameError(message, name, origException, false);
     }
 
     public RaiseException newNameError(String message, String name, Throwable origException, boolean printWhenVerbose) {
         if (origException != null) {
             if (printWhenVerbose && isVerbose()) {
                 LOG.error(origException.getMessage(), origException);
             } else if (isDebug()) {
                 LOG.debug(origException.getMessage(), origException);
             }
         }
         
         return new RaiseException(new RubyNameError(
                 this, getNameError(), message, name), false);
     }
 
     public RaiseException newLocalJumpError(RubyLocalJumpError.Reason reason, IRubyObject exitValue, String message) {
         return new RaiseException(new RubyLocalJumpError(this, getLocalJumpError(), message, reason, exitValue), true);
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
             LOG.debug(soe.getMessage(), soe);
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
         if (ioe instanceof ClosedChannelException) {
             throw newIOError("closed stream");
         }
 
         // TODO: this is kinda gross
         if(ioe.getMessage() != null) {
             if (ioe.getMessage().equals("Broken pipe")) {
                 throw newErrnoEPIPEError();
             } else if (ioe.getMessage().equals("Connection reset by peer") ||
                     (Platform.IS_WINDOWS && ioe.getMessage().contains("connection was aborted"))) {
                 throw newErrnoECONNRESETError();
             }
             return newRaiseException(getIOError(), ioe.getMessage());
         } else {
             return newRaiseException(getIOError(), "IO Error");
         }
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyClass expectedType) {
         return newTypeError(receivedObject, expectedType.getName());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, RubyModule expectedType) {
         return newTypeError(receivedObject, expectedType.getName());
     }
 
     public RaiseException newTypeError(IRubyObject receivedObject, String expectedType) {
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
     public RaiseException newRaiseException(RubyClass exceptionClass, String message) {
         return new RaiseException(this, exceptionClass, message, true);
     }
 
     /**
      * Generate one of the ERRNO exceptions. This differs from the normal logic
      * by avoiding the generation of a backtrace. Many ERRNO values are expected,
      * such as EAGAIN, and JRuby pays a very high cost to generate backtraces that
      * are never used. The flags -Xerrno.backtrace=true or the property
      * jruby.errno.backtrace=true forces all errno exceptions to generate a backtrace.
      * 
      * @param exceptionClass
      * @param message
      * @return
      */
     private RaiseException newLightweightErrnoException(RubyClass exceptionClass, String message) {
         if (RubyInstanceConfig.ERRNO_BACKTRACE) {
             return new RaiseException(this, exceptionClass, message, true);
         } else {
             return new RaiseException(this, exceptionClass, ERRNO_BACKTRACE_MESSAGE, RubyArray.newEmptyArray(this), true);
         }
     }
 
     // Equivalent of Data_Wrap_Struct
     public RubyObject.Data newData(RubyClass objectClass, Object sval) {
         return new RubyObject.Data(this, objectClass, sval);
     }
 
     public RubySymbol.SymbolTable getSymbolTable() {
         return symbolTable;
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
 
     public int getFilenoIntMapSize() {
         return filenoIntExtMap.size();
     }
 
     public void removeFilenoIntMap(int internal) {
         filenoIntExtMap.remove(internal);
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
 
     private void recursiveListClear() {
         Map<String, RubyHash> hash = recursive.get();
         if(hash != null) {
             hash.clear();
         }
     }
 
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
 
     /**
      * Perform a recursive walk on the given object using the given function.
      *
      * Do not call this method directly unless you know you're within a call
      * to {@link Ruby#recursiveListOperation(java.util.concurrent.Callable) recursiveListOperation},
      * which will ensure the thread-local recursion tracking data structs are
      * cleared.
      *
      * MRI: rb_exec_recursive
      *
      * Calls func(obj, arg, recursive), where recursive is non-zero if the
      * current method is called recursively on obj
      *
      * @param func
      * @param obj
      * @return
      */
     public IRubyObject execRecursive(RecursiveFunction func, IRubyObject obj) {
         if (!inRecursiveListOperation.get()) {
             throw newThreadError("BUG: execRecursive called outside recursiveListOperation");
         }
         return execRecursiveInternal(func, obj, null, false);
     }
 
     /**
      * Perform a recursive walk on the given object using the given function.
      * Treat this as the outermost call, cleaning up recursive structures.
      *
      * MRI: rb_exec_recursive_outer
      *
      * If recursion is detected on the current method and obj, the outermost
      * func will be called with (obj, arg, Qtrue). All inner func will be
      * short-circuited using throw.
      *
      * @param func
      * @param obj
      * @return
      */
     public IRubyObject execRecursiveOuter(RecursiveFunction func, IRubyObject obj) {
         try {
             return execRecursiveInternal(func, obj, null, true);
         } finally {
             recursiveListClear();
         }
     }
 
     /**
      * Begin a recursive walk that may make one or more calls to
      * {@link Ruby#execRecursive(org.jruby.Ruby.RecursiveFunction, org.jruby.runtime.builtin.IRubyObject) execRecursive}.
      * Clean up recursive structures once complete.
      *
      * @param body
      * @param <T>
      * @return
      */
     public <T extends IRubyObject> T recursiveListOperation(Callable<T> body) {
         try {
             inRecursiveListOperation.set(true);
             return body.call();
         } catch (Exception e) {
             UnsafeFactory.getUnsafe().throwException(e);
             return null; // not reached
         } finally {
             recursiveListClear();
             inRecursiveListOperation.set(false);
         }
     }
 
     public boolean isObjectSpaceEnabled() {
         return objectSpaceEnabled;
     }
 
     public void setObjectSpaceEnabled(boolean objectSpaceEnabled) {
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
 
     public boolean is2_0() {
         return is2_0;
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
 
     @Deprecated
     public int getConstantGeneration() {
         return -1;
     }
 
     @Deprecated
     public synchronized void incrementConstantGeneration() {
         constantInvalidator.invalidate();
     }
     
     public Invalidator getConstantInvalidator() {
         return constantInvalidator;
     }
     
     public void invalidateConstants() {
         
     }
 
     public <E extends Enum<E>> void loadConstantSet(RubyModule module, Class<E> enumClass) {
         for (E e : EnumSet.allOf(enumClass)) {
             Constant c = (Constant) e;
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.setConstant(c.name(), newFixnum(c.intValue()));
             }
         }
     }
     public void loadConstantSet(RubyModule module, String constantSetName) {
         for (Constant c : ConstantSet.getConstantSet(constantSetName)) {
             if (Character.isUpperCase(c.name().charAt(0))) {
                 module.setConstant(c.name(), newFixnum(c.intValue()));
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
      * Get a new generation number for a module or class.
      *
      * @return a new generation number
      */
     public int getNextModuleGeneration() {
         return moduleGeneration.incrementAndGet();
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
 
     /**
      * Get the runtime-global selector pool
      *
      * @return a SelectorPool from which to get Selector instances
      */
     public SelectorPool getSelectorPool() {
         return selectorPool;
     }
 
     /**
      * Get the core class RuntimeCache instance, for doing dynamic calls from
      * core class methods.
      */
     public RuntimeCache getRuntimeCache() {
         return runtimeCache;
     }
 
     /**
      * Get the list of method holders for methods being profiled.
      */
     public ProfiledMethod[] getProfiledMethods() {
         return profiledMethods;
     }
     
     /**
      * Add a method, so it can be printed out later.
      *
      * @param name the name of the method
      * @param method
      */
     void addProfiledMethod(final String name, final DynamicMethod method) {
         if (!config.isProfiling()) return;
         if (method.isUndefined()) return;
-        if (method.getSerialNumber() > MAX_PROFILE_METHODS) return;
 
         final int index = (int) method.getSerialNumber();
+
+        if (index >= config.getProfileMaxMethods()) {
+            warnings.warnOnce(ID.PROFILE_MAX_METHODS_EXCEEDED, "method count exceeds max of " + config.getProfileMaxMethods() + "; no new methods will be profiled");
+            return;
+        }
+
         synchronized(this) {
             if (profiledMethods.length <= index) {
-                int newSize = Math.min((int) index * 2 + 1, MAX_PROFILE_METHODS);
+                int newSize = Math.min((int) index * 2 + 1, config.getProfileMaxMethods());
                 ProfiledMethod[] newProfiledMethods = new ProfiledMethod[newSize];
                 System.arraycopy(profiledMethods, 0, newProfiledMethods, 0, profiledMethods.length);
                 profiledMethods = newProfiledMethods;
             }
     
             // only add the first one we encounter, since others will probably share the original
             if (profiledMethods[index] == null) {
                 profiledMethods[index] = new ProfiledMethod(name, method);
             }
         }
     }
     
     /**
      * Increment the count of exceptions generated by code in this runtime.
      */
     public void incrementExceptionCount() {
         exceptionCount.incrementAndGet();
     }
     
     /**
      * Get the current exception count.
      * 
      * @return he current exception count
      */
     public int getExceptionCount() {
         return exceptionCount.get();
     }
     
     /**
      * Increment the count of backtraces generated by code in this runtime.
      */
     public void incrementBacktraceCount() {
         backtraceCount.incrementAndGet();
     }
     
     /**
      * Get the current backtrace count.
      * 
      * @return the current backtrace count
      */
     public int getBacktraceCount() {
         return backtraceCount.get();
     }
     
     /**
      * Increment the count of backtraces generated for warnings in this runtime.
      */
     public void incrementWarningCount() {
         warningCount.incrementAndGet();
     }
     
     /**
      * Get the current backtrace count.
      * 
      * @return the current backtrace count
      */
     public int getWarningCount() {
         return warningCount.get();
     }
 
     /**
      * Increment the count of backtraces generated by code in this runtime.
      */
     public void incrementCallerCount() {
         callerCount.incrementAndGet();
     }
 
     /**
      * Get the current backtrace count.
      *
      * @return the current backtrace count
      */
     public int getCallerCount() {
         return callerCount.get();
     }
     
     /**
      * Whether the Fixnum class has been reopened and modified
      */
     public boolean isFixnumReopened() {
         return fixnumReopened;
     }
     
     /**
      * Set whether the Fixnum class has been reopened and modified
      */
     public void setFixnumReopened(boolean fixnumReopened) {
         this.fixnumReopened = fixnumReopened;
     }
     
     /**
      * Whether the Float class has been reopened and modified
      */
     public boolean isFloatReopened() {
         return floatReopened;
     }
     
     /**
      * Set whether the Float class has been reopened and modified
      */
     public void setFloatReopened(boolean floatReopened) {
         this.floatReopened = floatReopened;
     }
     
     public boolean isBooting() {
         return booting;
     }
     
     public CoverageData getCoverageData() {
         return coverageData;
     }
     
     public Random getRandom() {
         return random;
     }
     
     public int getHashSeed() {
         return hashSeed;
     }
     
     public StaticScopeFactory getStaticScopeFactory() {
         return staticScopeFactory;
     }
 
     public FFI getFFI() {
         return ffi;
     }
 
     public void setFFI(FFI ffi) {
         this.ffi = ffi;
     }
 
     @Deprecated
     public int getSafeLevel() {
         return 0;
     }
 
     @Deprecated
     public void setSafeLevel(int safeLevel) {
     }
 
     @Deprecated
     public void checkSafeString(IRubyObject object) {
     }
 
     @Deprecated
     public void secure(int level) {
     }
 
     private final Invalidator constantInvalidator;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private final ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
 
     private final List<EventHook> eventHooks = new Vector<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private volatile boolean objectSpaceEnabled;
     
     private final Set<Script> jittedMethods = Collections.synchronizedSet(new WeakHashSet<Script>());
     
     private long globalState = 1;
 
     // Default objects
     private IRubyObject topSelf;
     private IRubyObject rootFiber;
     private RubyNil nilObject;
     private IRubyObject[] singleNilArray;
     private RubyBoolean trueObject;
     private RubyBoolean falseObject;
     public final RubyFixnum[] fixnumCache = new RubyFixnum[2 * RubyFixnum.CACHE_OFFSET];
 
     private boolean verbose, warningsEnabled, debug;
     private IRubyObject verboseValue;
     
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
             regexpError, eofError, threadError, concurrencyError, systemStackError, zeroDivisionError, floatDomainError, mathDomainError,
             encodingError, encodingCompatibilityError, converterNotFoundError, undefinedConversionError,
             invalidByteSequenceError, fiberError, randomClass, keyError;
 
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
             superMethodMissing, normalMethodMissing, defaultMethodMissing, respondTo;
     
     // record separator var, to speed up io ops that use it
     private GlobalVariable recordSeparatorVar;
 
     // former java.lang.System concepts now internalized for MVM
     private volatile String currentDirectory;
 
     // The "current line" global variable
     private volatile int currentLine = 0;
 
     private volatile IRubyObject argsFile;
 
     private final long startTime = System.currentTimeMillis();
 
     private final RubyInstanceConfig config;
     private final boolean is1_9;
     private final boolean is2_0;
 
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
         if (SafePropertyAccessor.isSecurityProtected("jruby.reflected.handles")) {
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
 
     private final Parser parser = new Parser(this);
 
     private LoadService loadService;
 
     private Encoding defaultInternalEncoding, defaultExternalEncoding;
     private EncodingService encodingService;
 
     private GlobalVariables globalVariables = new GlobalVariables(this);
     private final RubyWarnings warnings = new RubyWarnings(this);
 
     // Contains a list of all blocks (as Procs) that should be called when
     // the runtime environment exits.
     private final Stack<RubyProc> atExitBlocks = new Stack<RubyProc>();
 
     private Profile profile;
 
     private KCode kcode = KCode.NONE;
 
     // Atomic integers for symbol and method IDs
     private final AtomicInteger symbolLastId = new AtomicInteger(128);
     private final AtomicInteger moduleLastId = new AtomicInteger(0);
 
     // Weak map of all Modules in the system (and by extension, all Classes
     private final Set<RubyModule> allModules = new WeakHashSet<RubyModule>();
 
     private final Map<String, DateTimeZone> timeZoneCache = new HashMap<String,DateTimeZone>();
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
     private final Object hierarchyLock = new Object();
 
     // An atomic long for generating DynamicMethod serial numbers
     private final AtomicLong dynamicMethodSerial = new AtomicLong(1);
 
     // An atomic int for generating class generation numbers
     private final AtomicInteger moduleGeneration = new AtomicInteger(1);
 
     // A list of Java class+method names to include in backtraces
     private final Map<String, Map<String, String>> boundMethods = new HashMap();
 
     // A soft pool of selectors for blocking IO operations
     private final SelectorPool selectorPool = new SelectorPool();
 
     // A global cache for Java-to-Ruby calls
     private final RuntimeCache runtimeCache;
-
-    // The maximum number of methods we will track for profiling purposes
-    private static final int MAX_PROFILE_METHODS = 100000;
     
     // The method objects for serial numbers
     private ProfiledMethod[] profiledMethods = new ProfiledMethod[0];
     
     // Message for Errno exceptions that will not generate a backtrace
     public static final String ERRNO_BACKTRACE_MESSAGE = "errno backtraces disabled; run with -Xerrno.backtrace=true to enable";
     
     // Count of RaiseExceptions generated by code running in this runtime
     private final AtomicInteger exceptionCount = new AtomicInteger();
     
     // Count of exception backtraces generated by code running in this runtime
     private final AtomicInteger backtraceCount = new AtomicInteger();
     
     // Count of Kernel#caller backtraces generated by code running in this runtime
     private final AtomicInteger callerCount = new AtomicInteger();
 
     // Count of built-in warning backtraces generated by code running in this runtime
     private final AtomicInteger warningCount = new AtomicInteger();
     
     private boolean fixnumReopened, floatReopened;
     
     private volatile boolean booting = true;
     
     private RubyHash envObject;
     
     private final CoverageData coverageData = new CoverageData();
 
     /** The "global" runtime. Set to the first runtime created, normally. */
     private static Ruby globalRuntime;
     
     /** The "thread local" runtime. Set to the global runtime if unset. */
     private static ThreadLocal<Ruby> threadLocalRuntime = new ThreadLocal<Ruby>();
     
     /** The runtime-local random number generator. Uses SecureRandom if permissions allow. */
     private final Random random;
 
     /** The runtime-local seed for hash randomization */
     private int hashSeed;
     
     private final StaticScopeFactory staticScopeFactory;
     
     private IRManager irManager;
 
     // structures and such for recursive operations
     private ThreadLocal<Map<String, RubyHash>> recursive = new ThreadLocal<Map<String, RubyHash>>();
     private RubySymbol recursiveKey;
     private ThreadLocal<Boolean> inRecursiveListOperation = new ThreadLocal<Boolean>();
 
     private FFI ffi;
 }
diff --git a/src/org/jruby/RubyInstanceConfig.java b/src/org/jruby/RubyInstanceConfig.java
index fc4e64ac09..b3830bdb85 100644
--- a/src/org/jruby/RubyInstanceConfig.java
+++ b/src/org/jruby/RubyInstanceConfig.java
@@ -117,1509 +117,1525 @@ public class RubyInstanceConfig {
             } else if (jitModeProperty.equals("JIT")) {
                 compileMode = CompileMode.JIT;
             } else if (jitModeProperty.equals("FORCE")) {
                 compileMode = CompileMode.FORCE;
             } else {
                 error.print(Options.COMPILE_MODE + " property must be OFF, JIT, FORCE, or unset; defaulting to JIT");
                 compileMode = CompileMode.JIT;
             }
             
             jitLogging = Options.JIT_LOGGING.load();
             jitDumping = Options.JIT_DUMPING.load();
             jitLoggingVerbose = Options.JIT_LOGGING_VERBOSE.load();
             jitLogEvery = Options.JIT_LOGEVERY.load();
             jitThreshold = Options.JIT_THRESHOLD.load();
             jitMax = Options.JIT_MAX.load();
             jitMaxSize = Options.JIT_MAXSIZE.load();
         }
 
         // default ClassCache using jitMax as a soft upper bound
         classCache = new ClassCache<Script>(loader, jitMax);
         threadDumpSignal = Options.THREAD_DUMP_SIGNAL.load();
 
         try {
             environment = System.getenv();
         } catch (SecurityException se) {
             environment = new HashMap();
         }
     }
     
     public RubyInstanceConfig(RubyInstanceConfig parentConfig) {
         currentDirectory = parentConfig.getCurrentDirectory();
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
         updateNativeENVEnabled = parentConfig.updateNativeENVEnabled;
         
         classCache = new ClassCache<Script>(loader, jitMax);
 
         try {
             environment = System.getenv();
         } catch (SecurityException se) {
             environment = new HashMap();
         }
     }
 
     public LoadService createLoadService(Ruby runtime) {
         return creator.create(runtime);
     }
 
     @Deprecated
     public String getBasicUsageHelp() {
         return OutputStrings.getBasicUsageHelp();
     }
 
     @Deprecated
     public String getExtendedHelp() {
         return OutputStrings.getExtendedHelp();
     }
 
     @Deprecated
     public String getPropertyHelp() {
         return OutputStrings.getPropertyHelp();
     }
 
     @Deprecated
     public String getVersionString() {
         return OutputStrings.getVersionString(compatVersion);
     }
 
     @Deprecated
     public String getCopyrightString() {
         return OutputStrings.getCopyrightString();
     }
 
     public void processArguments(String[] arguments) {
         new ArgumentProcessor(arguments, this).processArguments();
         tryProcessArgumentsWithRubyopts();
     }
 
     public void tryProcessArgumentsWithRubyopts() {
         try {
             // environment defaults to System.getenv normally
             Object rubyoptObj = environment.get("RUBYOPT");
             String rubyopt = rubyoptObj == null ? null : rubyoptObj.toString();
             
             if (rubyopt == null || "".equals(rubyopt)) return;
 
             if (rubyopt.split("\\s").length != 0) {
                 String[] rubyoptArgs = rubyopt.split("\\s+");
                 new ArgumentProcessor(rubyoptArgs, false, true, this).processArguments();
             }
         } catch (SecurityException se) {
             // ignore and do nothing
         }
     }
 
     /**
      * The intent here is to gather up any options that might have
      * been specified in the shebang line and return them so they can
      * be merged into the ones specified on the commandline.  This is
      * kind of a hopeless task because it's impossible to figure out
      * where the command invocation stops and the parameters start.
      * We try to work with the common scenarios where /usr/bin/env is
      * used to invoke the jruby shell script, and skip any parameters
      * it might have.  Then we look for the interpreter invokation and
      * assume that the binary will have the word "ruby" in the name.
      * This is error prone but should cover more cases than the
      * previous code.
      */
     public String[] parseShebangOptions(InputStream in) {
         BufferedReader reader = null;
         String[] result = new String[0];
         if (in == null) return result;
         try {
             in.mark(1024);
             reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8192);
             String firstLine = reader.readLine();
 
             // Search for the shebang line in the given stream
             // if it wasn't found on the first line and the -x option
             // was specified
             if (isXFlag()) {
                 while (firstLine != null && !isRubyShebangLine(firstLine)) {
                     firstLine = reader.readLine();
                 }
             }
 
             boolean usesEnv = false;
             if (firstLine.length() > 2 && firstLine.charAt(0) == '#' && firstLine.charAt(1) == '!') {
                 String[] options = firstLine.substring(2).split("\\s+");
                 int i;
                 for (i = 0; i < options.length; i++) {
                     // Skip /usr/bin/env if it's first
                     if (i == 0 && options[i].endsWith("/env")) {
                         usesEnv = true;
                         continue;
                     }
                     // Skip any assignments if /usr/bin/env is in play
                     if (usesEnv && options[i].indexOf('=') > 0) {
                         continue;
                     }
                     // Skip any commandline args if /usr/bin/env is in play
                     if (usesEnv && options[i].startsWith("-")) {
                         continue;
                     }
                     String basename = (new File(options[i])).getName();
                     if (basename.indexOf("ruby") > 0) {
                         break;
                     }
                 }
                 setHasShebangLine(true);
                 System.arraycopy(options, i, result, 0, options.length - i);
             } else {
                 // No shebang line found
                 setHasShebangLine(false);
             }
         } catch (Exception ex) {
             // ignore error
         } finally {
             try {
                 in.reset();
             } catch (IOException ex) {}
         }
         return result;
     }
     
     private static final Pattern RUBY_SHEBANG = Pattern.compile("#!.*ruby.*");
 
     protected static boolean isRubyShebangLine(String line) {
         return RUBY_SHEBANG.matcher(line).matches();
     }
     
     private String calculateJRubyHome() {
         String newJRubyHome = null;
         
         // try the normal property first
         if (!Ruby.isSecurityRestricted()) {
             newJRubyHome = SafePropertyAccessor.getProperty("jruby.home");
         }
 
         if (newJRubyHome != null) {
             // verify it if it's there
             newJRubyHome = verifyHome(newJRubyHome, error);
         } else {
             try {
                 newJRubyHome = SystemPropertyCatcher.findFromJar(this);
             } catch (Exception e) {}
 
             if (newJRubyHome != null) {
                 // verify it if it's there
                 newJRubyHome = verifyHome(newJRubyHome, error);
             } else {
                 // otherwise fall back on system temp location
                 newJRubyHome = SafePropertyAccessor.getProperty("java.io.tmpdir");
             }
         }
         
         return newJRubyHome;
     }
 
     // We require the home directory to be absolute
     private static String verifyHome(String home, PrintStream error) {
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
 
     /** Indicates whether the JVM process' native environment will be updated when ENV[...] is set from Ruby. */
     public boolean isUpdateNativeENVEnabled() {
         return updateNativeENVEnabled;
     }
 
     /** Ensure that the JVM process' native environment will be updated when ENV is modified .*/
     public void setUpdateNativeENVEnabled(boolean updateNativeENVEnabled) {
         this.updateNativeENVEnabled = updateNativeENVEnabled;
     }
 
     public byte[] inlineScript() {
         return inlineScript.toString().getBytes();
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
                 } else if (script.startsWith("classpath:")) {
                     stream = Ruby.getClassLoader().getResourceAsStream(script.substring("classpath:".length()));
                 } else {
                     File file = JRubyFile.create(getCurrentDirectory(), getScriptFileName());
                     if (isXFlag()) {
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
             InputStream is = getJarScriptSource(scriptFileName);
             if (is != null) {
                 return new BufferedInputStream(is, 8129);
             }
             throw new MainExitException(1, "Error opening script file: " + e.getMessage());
         }
     }
 
     private static InputStream findScript(File file) throws IOException {
         StringBuffer buf = new StringBuffer();
         BufferedReader br = new BufferedReader(new FileReader(file));
         String currentLine = br.readLine();
         while (currentLine != null && !isRubyShebangLine(currentLine)) {
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
 
     private static InputStream getJarScriptSource(String scriptFileName) {
         boolean looksLikeJarURL = scriptFileName.startsWith("file:") && scriptFileName.indexOf("!/") != -1;
         if (!looksLikeJarURL) {
             return null;
         }
 
         String before = scriptFileName.substring("file:".length(), scriptFileName.indexOf("!/"));
         String after =  scriptFileName.substring(scriptFileName.indexOf("!/") + 2);
 
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
 
     public ASTCompiler newCompiler() {
         if (getCompatVersion() == CompatVersion.RUBY1_8) {
             return new ASTCompiler();
         } else {
             return new ASTCompiler19();
         }
     }
     
     ////////////////////////////////////////////////////////////////////////////
     // Static utilities and global state management methods.
     ////////////////////////////////////////////////////////////////////////////
     
     public static boolean hasLoadedNativeExtensions() {
         return loadedNativeExtensions;
     }
     
     public static void setLoadedNativeExtensions(boolean loadedNativeExtensions) {
         RubyInstanceConfig.loadedNativeExtensions = loadedNativeExtensions;
     }
     
     ////////////////////////////////////////////////////////////////////////////
     // Getters and setters for config settings.
     ////////////////////////////////////////////////////////////////////////////
 
     public LoadServiceCreator getLoadServiceCreator() {
         return creator;
     }
 
     public void setLoadServiceCreator(LoadServiceCreator creator) {
         this.creator = creator;
     }
 
     public String getJRubyHome() {
         if (jrubyHome == null) {
             jrubyHome = calculateJRubyHome();
         }
         return jrubyHome;
     }
 
     public void setJRubyHome(String home) {
         jrubyHome = verifyHome(home, error);
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
         if (newEnvironment == null) newEnvironment = new HashMap();
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
     
     public StringBuffer getInlineScript() {
         return inlineScript;
     }
     
     public void setHasInlineScript(boolean hasInlineScript) {
         this.hasInlineScript = hasInlineScript;
     }
     
     public boolean hasInlineScript() {
         return hasInlineScript;
     }
     
     public Collection<String> getRequiredLibraries() {
         return requiredLibraries;
     }
 
     @Deprecated
     public Collection<String> requiredLibraries() {
         return requiredLibraries;
     }
     
     public List<String> getLoadPaths() {
         return loadPaths;
     }
 
     @Deprecated
     public List<String> loadPaths() {
         return loadPaths;
     }
 
     public void setLoadPaths(List<String> loadPaths) {
         this.loadPaths = loadPaths;
     }
     
     public void setShouldPrintUsage(boolean shouldPrintUsage) {
         this.shouldPrintUsage = shouldPrintUsage;
     }
     
     public boolean getShouldPrintUsage() {
         return shouldPrintUsage;
     }
 
     @Deprecated
     public boolean shouldPrintUsage() {
         return shouldPrintUsage;
     }
     
     public void setShouldPrintProperties(boolean shouldPrintProperties) {
         this.shouldPrintProperties = shouldPrintProperties;
     }
     
     public boolean getShouldPrintProperties() {
         return shouldPrintProperties;
     }
 
     @Deprecated
     public boolean shouldPrintProperties() {
         return shouldPrintProperties;
     }
 
     public boolean isInlineScript() {
         return hasInlineScript;
     }
 
     private boolean isSourceFromStdin() {
         return getScriptFileName() == null;
     }
 
     public void setScriptFileName(String scriptFileName) {
         this.scriptFileName = scriptFileName;
     }
 
     public String getScriptFileName() {
         return scriptFileName;
     }
     
     public void setBenchmarking(boolean benchmarking) {
         this.benchmarking = benchmarking;
     }
 
     public boolean isBenchmarking() {
         return benchmarking;
     }
     
     public void setAssumeLoop(boolean assumeLoop) {
         this.assumeLoop = assumeLoop;
     }
 
     public boolean isAssumeLoop() {
         return assumeLoop;
     }
     
     public void setAssumePrinting(boolean assumePrinting) {
         this.assumePrinting = assumePrinting;
     }
 
     public boolean isAssumePrinting() {
         return assumePrinting;
     }
     
     public void setProcessLineEnds(boolean processLineEnds) {
         this.processLineEnds = processLineEnds;
     }
 
     public boolean isProcessLineEnds() {
         return processLineEnds;
     }
     
     public void setSplit(boolean split) {
         this.split = split;
     }
 
     public boolean isSplit() {
         return split;
     }
     
     public Verbosity getVerbosity() {
         return verbosity;
     }
     
     public void setVerbosity(Verbosity verbosity) {
         this.verbosity = verbosity;
     }
 
     public boolean isVerbose() {
         return verbosity == Verbosity.TRUE;
     }
 
     @Deprecated
     public Boolean getVerbose() {
         return isVerbose();
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
 
     public void setShowVersion(boolean showVersion) {
         this.showVersion = showVersion;
     }
     
     public void setShowBytecode(boolean showBytecode) {
         this.showBytecode = showBytecode;
     }
 
     public void setShowCopyright(boolean showCopyright) {
         this.showCopyright = showCopyright;
     }
     
     public void setShouldRunInterpreter(boolean shouldRunInterpreter) {
         this.shouldRunInterpreter = shouldRunInterpreter;
     }
     
     public boolean getShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
 
     @Deprecated
     public boolean shouldRunInterpreter() {
         return isShouldRunInterpreter();
     }
 
     @Deprecated
     public boolean isShouldRunInterpreter() {
         return shouldRunInterpreter;
     }
     
     public void setShouldCheckSyntax(boolean shouldSetSyntax) {
         this.shouldCheckSyntax = shouldSetSyntax;
     }
 
     public boolean getShouldCheckSyntax() {
         return shouldCheckSyntax;
     }
     
     public void setInputFieldSeparator(String inputFieldSeparator) {
         this.inputFieldSeparator = inputFieldSeparator;
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
     
     public void setInternalEncoding(String internalEncoding) {
         this.internalEncoding = internalEncoding;
     }
 
     public String getInternalEncoding() {
         return internalEncoding;
     }
     
     public void setExternalEncoding(String externalEncoding) {
         this.externalEncoding = externalEncoding;
     }
 
     public String getExternalEncoding() {
         return externalEncoding;
     }
     
     public void setRecordSeparator(String recordSeparator) {
         this.recordSeparator = recordSeparator;
     }
 
     public String getRecordSeparator() {
         return recordSeparator;
     }
 
     public int getSafeLevel() {
         return 0;
     }
 
     public ClassCache getClassCache() {
         return classCache;
     }
     
     public void setInPlaceBackupExtension(String inPlaceBackupExtension) {
         this.inPlaceBackupExtension = inPlaceBackupExtension;
     }
 
     public String getInPlaceBackupExtension() {
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
     
     public void setParserDebug(boolean parserDebug) {
         this.parserDebug = parserDebug;
     }
     
     public boolean getParserDebug() {
         return parserDebug;
     }
 
     public boolean isArgvGlobalsOn() {
         return argvGlobalsOn;
     }
 
     public void setArgvGlobalsOn(boolean argvGlobalsOn) {
         this.argvGlobalsOn = argvGlobalsOn;
     }
 
     public String getThreadDumpSignal() {
         return threadDumpSignal;
     }
 
     public boolean isHardExit() {
         return hardExit;
     }
 
     public void setHardExit(boolean hardExit) {
         this.hardExit = hardExit;
     }
 
     public boolean isProfiling() {
         return profilingMode != ProfilingMode.OFF;
     }
     
     public boolean isProfilingEntireRun() {
         return profilingMode != ProfilingMode.OFF && profilingMode != ProfilingMode.API;
     }
     
     public void setProfilingMode(ProfilingMode profilingMode) {
         this.profilingMode = profilingMode;
     }
 
     public ProfilingMode getProfilingMode() {
         return profilingMode;
     }
 
     public boolean hasShebangLine() {
         return hasShebangLine;
     }
 
     public void setHasShebangLine(boolean hasShebangLine) {
         this.hasShebangLine = hasShebangLine;
     }
 
     public boolean isDisableGems() {
         return disableGems;
     }
 
     public void setDisableGems(boolean dg) {
         this.disableGems = dg;
     }
 
     public TraceType getTraceType() {
         return traceType;
     }
 
     public void setTraceType(TraceType traceType) {
         this.traceType = traceType;
     }
 
     /**
      * Whether to mask .java lines in the Ruby backtrace, as MRI does for C calls.
      *
      * @return true if masking; false otherwise
      */
     public boolean getBacktraceMask() {
         return backtraceMask;
     }
 
     /**
      * Set whether to mask .java lines in the Ruby backtrace.
      *
      * @param backtraceMask true to mask; false otherwise
      */
     public void setBacktraceMask(boolean backtraceMask) {
         this.backtraceMask = backtraceMask;
     }
     
     /**
      * Set whether native code is enabled for this config. Disabling it also
      * disables C extensions (@see RubyInstanceConfig#setCextEnabled).
      * 
      * @param b new value indicating whether native code is enabled
      */
     public void setNativeEnabled(boolean b) {
         _nativeEnabled = false;
     }
     
     /**
      * Get whether native code is enabled for this config.
      * 
      * @return true if native code is enabled; false otherwise.
      */
     public boolean isNativeEnabled() {
         return _nativeEnabled;
     }
     
     /**
      * Set whether C extensions are enabled for this config.
      * 
      * @param b new value indicating whether native code is enabled
      */
     public void setCextEnabled(boolean b) {
         _cextEnabled = b;
     }
     
     /**
      * Get whether C extensions are enabled for this config.
      * 
      * @return true if C extensions are enabled; false otherwise.
      */
     public boolean isCextEnabled() {
         return _cextEnabled;
     }
     
     public void setXFlag(boolean xFlag) {
         this.xFlag = xFlag;
     }
 
     public boolean isXFlag() {
         return xFlag;
     }
     
     @Deprecated
     public boolean isxFlag() {
         return xFlag;
     }
     
     /**
      * True if colorized backtraces are enabled. False otherwise.
      */
     public boolean getBacktraceColor() {
         return backtraceColor;
     }
     
     /**
      * Set to true to enable colorized backtraces.
      */
     public void setBacktraceColor(boolean backtraceColor) {
         this.backtraceColor = backtraceColor;
     }
     
     /**
      * Whether to use a single global lock for requires.
      * @see Options.GLOBAL_REQUIRE_LOCK
      */
     public boolean isGlobalRequireLock() {
         return globalRequireLock;
     }
     
     /**
      * Set whether to use a single global lock for requires.
      */
     public void setGlobalRequireLock(boolean globalRequireLock) {
         this.globalRequireLock = globalRequireLock;
     }
 
     /**
      * Set whether the JIT compiler should run in a background thread (Executor-based).
      *
      * @param jitBackground whether to run the JIT compiler in a background thread
      */
     public void setJitBackground(boolean jitBackground) {
         this.jitBackground = jitBackground;
     }
 
     /**
      * Get whether the JIT compiler will run in a background thread.
      *
      * @return whether the JIT compiler will run in a background thread
      */
     public boolean getJitBackground() {
         return jitBackground;
     }
 
     /**
      * Set whether to load and setup bundler on startup.
      */
     public void setLoadGemfile(boolean loadGemfile) {
         this.loadGemfile = loadGemfile;
     }
 
     /**
      * Whether to load and setup bundler on startup.
      */
     public boolean getLoadGemfile() {
         return loadGemfile;
     }
+
+    /**
+     * Set the maximum number of methods to consider when profiling.
+     */
+    public void setProfileMaxMethods(int profileMaxMethods) {
+        this.profileMaxMethods = profileMaxMethods;
+    }
+
+    /**
+     * Get the maximum number of methods to consider when profiling.
+     */
+    public int getProfileMaxMethods() {
+        return profileMaxMethods;
+    }
     
     ////////////////////////////////////////////////////////////////////////////
     // Configuration fields.
     ////////////////////////////////////////////////////////////////////////////
     
     /**
      * Indicates whether the script must be extracted from script source
      */
     private boolean xFlag;
 
     /**
      * Indicates whether the script has a shebang line or not
      */
     private boolean hasShebangLine;
     private InputStream input          = System.in;
     private PrintStream output         = System.out;
     private PrintStream error          = System.err;
     private Profile profile            = Profile.DEFAULT;
     private boolean objectSpaceEnabled = Options.OBJECTSPACE_ENABLED.load();
 
     private CompileMode compileMode = CompileMode.JIT;
     private boolean runRubyInProcess   = true;
     private String currentDirectory;
 
     /** Environment variables; defaults to System.getenv() in constructor */
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
 
     private String internalEncoding = null;
     private String externalEncoding = null;
 		
     private ProfilingMode profilingMode = ProfilingMode.OFF;
     
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
     private Verbosity verbosity = Verbosity.FALSE;
     private boolean debug = false;
     private boolean showVersion = false;
     private boolean showBytecode = false;
     private boolean showCopyright = false;
     private boolean shouldRunInterpreter = true;
     private boolean shouldPrintUsage = false;
     private boolean shouldPrintProperties=false;
     private boolean dumpConfig=false;
     private KCode kcode = KCode.NONE;
     private String recordSeparator = "\n";
     private boolean shouldCheckSyntax = false;
     private String inputFieldSeparator = null;
     private boolean managementEnabled = false;
     private String inPlaceBackupExtension = null;
     private boolean parserDebug = false;
     private String threadDumpSignal = null;
     private boolean hardExit = false;
     private boolean disableGems = false;
     private boolean updateNativeENVEnabled = true;
 
     private String jrubyHome;
     
     /**
      * Whether native code is enabled for this configuration.
      */
     private boolean _nativeEnabled = NATIVE_ENABLED;
     
     /**
      * Whether C extensions are enabled for this configuration.
      */
     private boolean _cextEnabled = CEXT_ENABLED;
 
     private TraceType traceType =
             TraceType.traceTypeFor(Options.BACKTRACE_STYLE.load());
 
     private boolean backtraceMask = Options.BACKTRACE_MASK.load();
     
     private boolean backtraceColor = Options.BACKTRACE_COLOR.load();
 
     private LoadServiceCreator creator = LoadServiceCreator.DEFAULT;
     
     private boolean globalRequireLock = Options.GLOBAL_REQUIRE_LOCK.load();
 
     private boolean jitBackground = Options.JIT_BACKGROUND.load();
 
     private boolean loadGemfile = false;
+
+    private int profileMaxMethods = Options.PROFILE_MAX_METHODS.load();
     
     ////////////////////////////////////////////////////////////////////////////
     // Support classes, etc.
     ////////////////////////////////////////////////////////////////////////////
     
     public enum Verbosity { NIL, FALSE, TRUE }
 
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
 
     public enum ProfilingMode {
 		OFF, API, FLAT, GRAPH, HTML
 	}
 
     public enum CompileMode {
         JIT, FORCE, FORCEIR, OFF, OFFIR;
 
         public boolean shouldPrecompileCLI() {
             switch (this) {
             case JIT: case FORCE: case FORCEIR:
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
             case JIT: case FORCE: case FORCEIR:
                 return true;
             }
             return false;
         }
 
         public boolean shouldPrecompileAll() {
             return this == FORCE;
         }
     }
     
     ////////////////////////////////////////////////////////////////////////////
     // Static configuration fields, used as defaults for new JRuby instances.
     ////////////////////////////////////////////////////////////////////////////
     
     /**
      * The max count of active methods eligible for JIT-compilation.
      */
     @Deprecated
     public static final int JIT_MAX_METHODS_LIMIT = Constants.JIT_MAX_METHODS_LIMIT;
 
     /**
      * The max size of JIT-compiled methods (full class size) allowed.
      */
     @Deprecated
     public static final int JIT_MAX_SIZE_LIMIT = Constants.JIT_MAX_SIZE_LIMIT;
 
     /**
      * The JIT threshold to the specified method invocation count.
      */
     @Deprecated
     public static final int JIT_THRESHOLD = Constants.JIT_THRESHOLD;
     
     /**
      * The version to use for generated classes. Set to current JVM version by default
      */
     public static final int JAVA_VERSION = initGlobalJavaVersion();
     
     /**
      * Default size for chained compilation.
      */
     @Deprecated
     public static final int CHAINED_COMPILE_LINE_COUNT_DEFAULT = Constants.CHAINED_COMPILE_LINE_COUNT_DEFAULT;
     
     /**
      * The number of lines at which a method, class, or block body is split into
      * chained methods (to dodge 64k method-size limit in JVM).
      */
     public static final int CHAINED_COMPILE_LINE_COUNT = Options.COMPILE_CHAINSIZE.load();
 
     /**
      * Enable compiler peephole optimizations.
      *
      * Set with the <tt>jruby.compile.peephole</tt> system property.
      */
     public static final boolean PEEPHOLE_OPTZ = Options.COMPILE_PEEPHOLE.load();
     /**
      * Enable "dynopt" optimizations.
      *
      * Set with the <tt>jruby.compile.dynopt</tt> system property.
      */
     public static boolean DYNOPT_COMPILE_ENABLED = Options.COMPILE_DYNOPT.load();
 
     /**
      * Enable compiler "noguards" optimizations.
      *
      * Set with the <tt>jruby.compile.noguards</tt> system property.
      */
     public static boolean NOGUARDS_COMPILE_ENABLED = Options.COMPILE_NOGUARDS.load();
 
     /**
      * Enable compiler "fastest" set of optimizations.
      *
      * Set with the <tt>jruby.compile.fastest</tt> system property.
      */
     public static boolean FASTEST_COMPILE_ENABLED = Options.COMPILE_FASTEST.load();
 
     /**
      * Enable fast operator compiler optimizations.
      *
      * Set with the <tt>jruby.compile.fastops</tt> system property.
      */
     public static boolean FASTOPS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED || Options.COMPILE_FASTOPS.load();
 
     /**
      * Enable "threadless" compile.
      *
      * Set with the <tt>jruby.compile.threadless</tt> system property.
      */
     public static boolean THREADLESS_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED || Options.COMPILE_THREADLESS.load();
 
     /**
      * Enable "fast send" compiler optimizations.
      *
      * Set with the <tt>jruby.compile.fastsend</tt> system property.
      */
     public static boolean FASTSEND_COMPILE_ENABLED
             = FASTEST_COMPILE_ENABLED || Options.COMPILE_FASTSEND.load();
 
     /**
      * Enable lazy handles optimizations.
      *
      * Set with the <tt>jruby.compile.lazyHandles</tt> system property.
      */
     public static boolean LAZYHANDLES_COMPILE = Options.COMPILE_LAZYHANDLES.load();
 
     /**
      * Enable fast multiple assignment optimization.
      *
      * Set with the <tt>jruby.compile.fastMasgn</tt> system property.
      */
     public static boolean FAST_MULTIPLE_ASSIGNMENT = Options.COMPILE_FASTMASGN.load();
 
     /**
      * Enable a thread pool. Each Ruby thread will be mapped onto a thread from this pool.
      *
      * Set with the <tt>jruby.thread.pool.enabled</tt> system property.
      */
     public static final boolean POOLING_ENABLED = Options.THREADPOOL_ENABLED.load();
 
     /**
      * Maximum thread pool size (integer, default Integer.MAX_VALUE).
      *
      * Set with the <tt>jruby.thread.pool.max</tt> system property.
      */
     public static final int POOL_MAX = Options.THREADPOOL_MAX.load();
     /**
      * Minimum thread pool size (integer, default 0).
      *
      * Set with the <tt>jruby.thread.pool.min</tt> system property.
      */
     public static final int POOL_MIN = Options.THREADPOOL_MIN.load();
     /**
      * Thread pool time-to-live in seconds.
      *
      * Set with the <tt>jruby.thread.pool.max</tt> system property.
      */
     public static final int POOL_TTL = Options.THREADPOOL_TTL.load();
 
     /**
      * Enable use of the native Java version of the 'net/protocol' library.
      *
      * Set with the <tt>jruby.thread.pool.max</tt> system property.
      */
     public static final boolean NATIVE_NET_PROTOCOL = Options.NATIVE_NET_PROTOCOL.load();
 
     /**
      * Enable tracing of method calls.
      *
      * Set with the <tt>jruby.debug.fullTrace</tt> system property.
      */
     public static boolean FULL_TRACE_ENABLED = Options.DEBUG_FULLTRACE.load();
 
     /**
      * Comma-separated list of methods to exclude from JIT compilation.
      * Specify as "Module", "Module#method" or "method".
      *
      * Set with the <tt>jruby.jit.exclude</tt> system property.
      */
     public static final String COMPILE_EXCLUDE = Options.JIT_EXCLUDE.load();
 
     /**
      * Indicates the global default for whether native code is enabled. Default
      * is true. This value is used to default new runtime configurations.
      *
      * Set with the <tt>jruby.native.enabled</tt> system property.
      */
     public static final boolean NATIVE_ENABLED = Options.NATIVE_ENABLED.load();
 
     @Deprecated
     public static final boolean nativeEnabled = NATIVE_ENABLED;
 
     /**
      * Indicates the global default for whether C extensions are enabled.
      * Default is the value of RubyInstanceConfig.NATIVE_ENABLED. This value
      * is used to default new runtime configurations.
      *
      * Set with the <tt>jruby.cext.enabled</tt> system property.
      */
     public final static boolean CEXT_ENABLED = Options.CEXT_ENABLED.load();
 
     /**
      * Whether to reify (pre-compile and generate) a Java class per Ruby class.
      *
      * Set with the <tt>jruby.reify.classes</tt> system property.
      */
     public static final boolean REIFY_RUBY_CLASSES = Options.REIFY_CLASSES.load();
 
     /**
      * Log errors that occur during reification.
      *
      * Set with the <tt>jruby.reify.logErrors</tt> system property.
      */
     public static final boolean REIFY_LOG_ERRORS = Options.REIFY_LOGERRORS.load();
 
     /**
      * Whether to use a custom-generated handle for Java methods instead of
      * reflection.
      *
      * Set with the <tt>jruby.java.handles</tt> system property.
      */
     public static final boolean USE_GENERATED_HANDLES = Options.JAVA_HANDLES.load();
 
     /**
      * Turn on debugging of the load service (requires and loads).
      *
      * Set with the <tt>jruby.debug.loadService</tt> system property.
      */
     public static final boolean DEBUG_LOAD_SERVICE = Options.DEBUG_LOADSERVICE.load();
 
     /**
      * Turn on timings of the load service (requires and loads).
      *
      * Set with the <tt>jruby.debug.loadService.timing</tt> system property.
      */
     public static final boolean DEBUG_LOAD_TIMINGS = Options.DEBUG_LOADSERVICE_TIMING.load();
 
     /**
      * Turn on debugging of subprocess launching.
      *
      * Set with the <tt>jruby.debug.launch</tt> system property.
      */
     public static final boolean DEBUG_LAUNCHING = Options.DEBUG_LAUNCH.load();
 
     /**
      * Turn on debugging of script resolution with "-S".
      *
      * Set with the <tt>jruby.debug.scriptResolution</tt> system property.
      */
     public static final boolean DEBUG_SCRIPT_RESOLUTION = Options.DEBUG_SCRIPTRESOLUTION.load();
 
     public static final boolean JUMPS_HAVE_BACKTRACE = Options.JUMP_BACKTRACE.load();
 
     public static final boolean JIT_CACHE_ENABLED = Options.JIT_CACHE.load();
 
     public static final String JIT_CODE_CACHE = Options.JIT_CODECACHE.load();
 
     public static final boolean REFLECTED_HANDLES = Options.REFLECTED_HANDLES.load();
 
     public static final boolean NO_UNWRAP_PROCESS_STREAMS = Options.PROCESS_NOUNWRAP.load();
 
     public static final boolean INTERFACES_USE_PROXY = Options.INTERFACES_USEPROXY.load();
 
     public static final boolean JIT_LOADING_DEBUG = Options.JIT_DEBUG.load();
 
     public static final boolean CAN_SET_ACCESSIBLE = Options.JI_SETACCESSIBLE.load();
     /**
      * In Java integration, allow upper case name for a Java package;
      * e.g., com.example.UpperCase.Class
      */
     public static final boolean UPPER_CASE_PACKAGE_NAME_ALLOWED = Options.JI_UPPER_CASE_PACKAGE_NAME_ALLOWED.load();
     
     
     public static final boolean USE_INVOKEDYNAMIC;
     static {
         boolean isHotspot =
                 SafePropertyAccessor.getProperty("java.vm.name", "").toLowerCase().contains("hotspot") ||
                         SafePropertyAccessor.getProperty("java.vm.name", "").toLowerCase().contains("openjdk");
 
         String version = SafePropertyAccessor.getProperty("java.specification.version", "1.6");
         
         if (isHotspot && version.equals("1.7")) {
             // if on OpenJDK 7, on by default unless turned off
             // TODO: turned off temporarily due to the lack of 100% working OpenJDK7 indy support
             USE_INVOKEDYNAMIC = Options.COMPILE_INVOKEDYNAMIC.load() && Options.COMPILE_INVOKEDYNAMIC.isSpecified();
         } else if (isHotspot && version.equals("1.8")) {
             // OpenJDK 8 will have the new 100% working logic soon, so we enable by default
             USE_INVOKEDYNAMIC = Options.COMPILE_INVOKEDYNAMIC.load();
         } else {
             // if not on Java 7, on only if explicitly turned on
             USE_INVOKEDYNAMIC = Options.COMPILE_INVOKEDYNAMIC.load() && Options.COMPILE_INVOKEDYNAMIC.isSpecified();
         }
     }
     
     // max times an indy call site can fail before it goes to simple IC
     public static final int MAX_FAIL_COUNT = Options.INVOKEDYNAMIC_MAXFAIL.load();
     
     // max polymorphism at a call site to build a chained method handle PIC
     public static final int MAX_POLY_COUNT = Options.INVOKEDYNAMIC_MAXPOLY.load();
     
     // logging of various indy aspects
     public static final boolean LOG_INDY_BINDINGS = Options.INVOKEDYNAMIC_LOG_BINDING.load();
     public static final boolean LOG_INDY_CONSTANTS = Options.INVOKEDYNAMIC_LOG_CONSTANTS.load();
     
     // properties enabling or disabling certain uses of invokedynamic
     public static final boolean INVOKEDYNAMIC_ALL = USE_INVOKEDYNAMIC && Options.INVOKEDYNAMIC_ALL.load();
     public static final boolean INVOKEDYNAMIC_SAFE = USE_INVOKEDYNAMIC && Options.INVOKEDYNAMIC_SAFE.load();
     
     public static final boolean INVOKEDYNAMIC_INVOCATION = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && Options.INVOKEDYNAMIC_INVOCATION.load();
     public static final boolean INVOKEDYNAMIC_INVOCATION_SWITCHPOINT = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && Options.INVOKEDYNAMIC_INVOCATION_SWITCHPOINT.load();
     public static final boolean INVOKEDYNAMIC_INDIRECT = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_INVOCATION && Options.INVOKEDYNAMIC_INVOCATION_INDIRECT.load();
     public static final boolean INVOKEDYNAMIC_JAVA = INVOKEDYNAMIC_ALL ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_INVOCATION && Options.INVOKEDYNAMIC_INVOCATION_JAVA.load();
     public static final boolean INVOKEDYNAMIC_ATTR = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_INVOCATION && Options.INVOKEDYNAMIC_INVOCATION_ATTR.load();
     public static final boolean INVOKEDYNAMIC_FASTOPS = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_INVOCATION && Options.INVOKEDYNAMIC_INVOCATION_FASTOPS.load();
     
     public static final boolean INVOKEDYNAMIC_CACHE = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && Options.INVOKEDYNAMIC_CACHE.load();
     public static final boolean INVOKEDYNAMIC_CONSTANTS = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_CACHE && Options.INVOKEDYNAMIC_CACHE_CONSTANTS.load();
     public static final boolean INVOKEDYNAMIC_LITERALS = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_CACHE && Options.INVOKEDYNAMIC_CACHE_LITERALS.load();
     public static final boolean INVOKEDYNAMIC_IVARS = INVOKEDYNAMIC_ALL || INVOKEDYNAMIC_SAFE ||
             USE_INVOKEDYNAMIC && INVOKEDYNAMIC_CACHE && Options.INVOKEDYNAMIC_CACHE_IVARS.load();
     
     // properties for logging exceptions, backtraces, and caller invocations
     public static final boolean LOG_EXCEPTIONS = Options.LOG_EXCEPTIONS.load();
     public static final boolean LOG_BACKTRACES = Options.LOG_BACKTRACES.load();
     public static final boolean LOG_CALLERS = Options.LOG_CALLERS.load();
     public static final boolean LOG_WARNINGS = Options.LOG_WARNINGS.load();
     
     public static final boolean ERRNO_BACKTRACE = Options.ERRNO_BACKTRACE.load();
     
     public static boolean IR_DEBUG = Options.IR_DEBUG.load();
     public static boolean IR_PROFILE = Options.IR_PROFILE.load();
     public static boolean IR_COMPILER_DEBUG = Options.IR_COMPILER_DEBUG.load();
     public static String IR_COMPILER_PASSES = Options.IR_COMPILER_PASSES.load();
     public static String IR_INLINE_COMPILER_PASSES = Options.IR_INLINE_COMPILER_PASSES.load();
     
     public static final boolean COROUTINE_FIBERS = Options.FIBER_COROUTINES.load();
     
     private static volatile boolean loadedNativeExtensions = false;
     
     ////////////////////////////////////////////////////////////////////////////
     // Static initializers
     ////////////////////////////////////////////////////////////////////////////
     
     private static int initGlobalJavaVersion() {
         String specVersion = specVersion = Options.BYTECODE_VERSION.load();
         
         // stack map calculation is failing for some compilation scenarios, so
         // forcing both 1.5 and 1.6 to use 1.5 bytecode for the moment.
         if (specVersion.equals("1.5")) {// || specVersion.equals("1.6")) {
            return Opcodes.V1_5;
         } else if (specVersion.equals("1.6")) {
             return Opcodes.V1_6;
         } else if (specVersion.equals("1.7") || specVersion.equals("1.8")) {
             return Opcodes.V1_7;
         } else {
             throw new RuntimeException("unsupported Java version: " + specVersion);
         }
     }
 
     @Deprecated
     public void setSafeLevel(int safeLevel) {
     }
 
     @Deprecated
     public String getInPlaceBackupExtention() {
         return inPlaceBackupExtension;
     }
 }
diff --git a/src/org/jruby/common/IRubyWarnings.java b/src/org/jruby/common/IRubyWarnings.java
index 6f87b79d10..91d007c329 100644
--- a/src/org/jruby/common/IRubyWarnings.java
+++ b/src/org/jruby/common/IRubyWarnings.java
@@ -1,129 +1,130 @@
 /*
  * **** BEGIN LICENSE BLOCK *****
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
 package org.jruby.common;
 
 import org.jruby.Ruby;
 import org.jruby.lexer.yacc.ISourcePosition;
 
 // FIXME: Document difference between warn and warning (or rename one better)
 /**
  */
 public interface IRubyWarnings {
     public enum ID {
         AMBIGUOUS_ARGUMENT("AMBIGUOUS_ARGUMENT"),
         ACCESSOR_NOT_INITIALIZED("ACCESSOR_NOT_INITIALIZED"),
         ARGUMENT_AS_PREFIX("ARGUMENT_AS_PREFIX"),
         ARGUMENT_EXTRA_SPACE("ARGUMENT_EXTRA_SPACE"),
         ASSIGNMENT_IN_CONDITIONAL("ASSIGNMENT_IN_CONDITIONAL"),
         BIGNUM_FROM_FLOAT_RANGE("BIGNUM_FROM_FLOAT_RANGE"),
         BLOCK_BEATS_DEFAULT_VALUE("BLOCK_BEATS_DEFAULT_VALUE"),
         BLOCK_NOT_ACCEPTED("BLOCK_NOT_ACCEPTED"),
         BLOCK_UNUSED("BLOCK_UNUSED"),
         CONSTANT_ALREADY_INITIALIZED("CONSTANT_ALREADY_INITIALIZED"),
         CONSTANT_BAD_REFERENCE("CONSTANT_BAD_REFERENCE"),
         CVAR_FROM_TOPLEVEL_SINGLETON_METHOD("CVAR_FROM_TOPLEVEL_SINGLETON_METHOD"),
         DECLARING_SCLASS_VARIABLE("DECLARING_SCLASS_VARIABLE"),
         DEPRECATED_METHOD("DEPRECATED_METHOD"),
         DUMMY_VALUE_USED("DUMMY_VALUE_USED"),
         END_IN_METHOD("END_IN_METHOD"),
         ELSE_WITHOUT_RESCUE("ELSE_WITHOUT_RESCUE"),
         EMPTY_IMPLEMENTATION("EMPTY_IMPLEMENTATION"),
         ENV_VARS_FROM_CLI_METHOD("ENV_VARS_FROM_CLI_METHOD"),
         FIXNUMS_NOT_SYMBOLS("FIXNUMS_NOT_SYMBOLS"),
         FLOAT_OUT_OF_RANGE("FLOAT_OUT_OF_RANGE"),
         GLOBAL_NOT_INITIALIZED("GLOBAL_NOT_INITIALIZED"),
         GROUPED_EXPRESSION("GROUPED_EXPRESSION"),
         INEFFECTIVE_GLOBAL("INNEFFECTIVE_GLOBAL"),
         INVALID_CHAR_SEQUENCE("INVALID_CHAR_SEQUENCE"),
         IVAR_NOT_INITIALIZED("IVAR_NOT_INITIALIZED"),
         MAY_BE_TOO_BIG("MAY_BE_TOO_BIG"),
         MISCELLANEOUS("MISCELLANEOUS"),
         MULTIPLE_VALUES_FOR_BLOCK("MULTIPLE_VALUES_FOR_BLOCK"),
         NEGATIVE_NUMBER_FOR_U("NEGATIVE_NUMBER_FOR_U"),
         NO_SUPER_CLASS("NO_SUPER_CLASS"),
         NOT_IMPLEMENTED("NOT_IMPLEMENTED"),
         OBSOLETE_ARGUMENT("OBSOLETE_ARGUMENT"),
         PARENTHISE_ARGUMENTS("PARENTHISE_ARGUMENTS"),
         PROXY_EXTENDED_LATE("PROXY_EXTENDED_LATE"),
         STATEMENT_NOT_REACHED("STATEMENT_NOT_REACHED"), 
         LITERAL_IN_CONDITIONAL_RANGE("LITERAL_IN_CONDITIONAL_RANGE"),
         REDEFINING_DANGEROUS("REDEFINING_DANGEROUS"),
         REGEXP_IGNORED_FLAGS("REGEXP_IGNORED_FLAGS"),
         REGEXP_LITERAL_IN_CONDITION("REGEXP_LITERAL_IN_CONDITION"),
         REGEXP_MATCH_AGAINST_STRING("REGEXP_MATCH_AGAINST_STRING"),
         SAFE_NOT_SUPPORTED("SAFE_NOT_SUPPORTED"),
         STRUCT_CONSTANT_REDEFINED("STRUCT_CONSTANT_REDEFINED"),
         SYMBOL_AS_INTEGER("SYMBOL_AS_INTEGER"),
         SYSSEEK_BUFFERED_IO("SYSSEEK_BUFFERED_IO"),
         SYSWRITE_BUFFERED_IO("SYSWRITE_BUFFERED_IO"),
         SWALLOWED_IO_EXCEPTION("SWALLOWED_IO_EXCEPTION"),
         TOO_MANY_ARGUMENTS("TOO_MANY_ARGUMENTS"),
         UNDEFINING_BAD("UNDEFINING_BAD"),
         USELESS_EXPRESSION("USELESS_EXPRESSION"),
         VOID_VALUE_EXPRESSION("VOID_VALUE_EXPRESSION"),
         NAMED_CAPTURE_CONFLICT("NAMED_CAPTURE_CONFLICT"),
         NON_PERSISTENT_JAVA_PROXY("NON_PERSISTENT_JAVA_PROXY"),
-        LISTEN_SERVER_SOCKET("LISTEN_SERVER_SOCKET");
+        LISTEN_SERVER_SOCKET("LISTEN_SERVER_SOCKET"),
+        PROFILE_MAX_METHODS_EXCEEDED("MAX_PROFILE_METHODS_EXCEEDED");
         
         private final String id;
         
         ID(String id) {
             this.id = id;
         }
         
         public String getID() {
             return id;
         }
     }
 
     public abstract Ruby getRuntime();
     public abstract boolean isVerbose();
     
     public abstract void warn(ID id, ISourcePosition position, String message);
     public abstract void warn(ID id, String fileName, int lineNumber, String message);
     public abstract void warn(ID id, String message);
     public abstract void warning(ID id, String message);
     public abstract void warning(ID id, ISourcePosition position, String message);
     public abstract void warning(ID id, String fileName, int lineNumber, String message);
     
     @Deprecated
     public abstract void warn(ID id, String message, Object... data);
     @Deprecated
     public abstract void warning(ID id, String message, Object... data);
     @Deprecated
     public abstract void warn(ID id, ISourcePosition position, String message, Object... data);
     @Deprecated
     public abstract void warn(ID id, String fileName, int lineNumber, String message, Object... data);
     @Deprecated
     public abstract void warning(ID id, ISourcePosition position, String message, Object... data);
     @Deprecated
     public abstract void warning(ID id, String fileName, int lineNumber, String message, Object...data);
 }
diff --git a/src/org/jruby/util/cli/Category.java b/src/org/jruby/util/cli/Category.java
index d5e55b9236..fbb55b1a22 100644
--- a/src/org/jruby/util/cli/Category.java
+++ b/src/org/jruby/util/cli/Category.java
@@ -1,59 +1,60 @@
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
  * Copyright (C) 2001-2011 The JRuby Community (and contribs)
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
 package org.jruby.util.cli;
 
 /**
  * Representation of available option categories, with a short name to use
  * in printing descriptions.
  */
 public enum Category {
     COMPILER("compiler"),
     INVOKEDYNAMIC("invokedynamic"),
     JIT("jit"),
     IR("intermediate representation"),
     NATIVE("native"),
     THREADPOOL("thread pooling"),
     MISCELLANEOUS("miscellaneous"),
     DEBUG("debugging and logging"),
-    JAVA_INTEGRATION("java integration");
+    JAVA_INTEGRATION("java integration"),
+    PROFILING("profiling");
 
     Category(String desc) {
         this.desc = desc;
     }
 
     public String desc() {
         return desc;
     }
 
     public String toString() {
         return desc;
     }
     
     private final String desc;
 }
diff --git a/src/org/jruby/util/cli/Options.java b/src/org/jruby/util/cli/Options.java
index b3d16a099b..f5afe1f6d8 100644
--- a/src/org/jruby/util/cli/Options.java
+++ b/src/org/jruby/util/cli/Options.java
@@ -1,186 +1,188 @@
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
  * Copyright (C) 2001-2011 The JRuby Community (and contribs)
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
 package org.jruby.util.cli;
 
 import java.util.ArrayList;
 import java.util.List;
 import org.jruby.runtime.Constants;
 import org.jruby.util.SafePropertyAccessor;
 import static org.jruby.util.cli.Category.*;
 
 /**
  * Options defines all configuration settings for JRuby in a consistent form.
  * Loading of individual settings, printing documentation for settings and their
  * options and defaults, and categorizing properties by function are all part
  * of the built-in structure.
  */
 public class Options {
     public static String dump() {
         StringBuilder sb = new StringBuilder("# JRuby configuration options with current values\n");
         Category category = null;
         for (Option option : _loadedOptions) {
             if (category != option.category) {
                 category = option.category;
                 sb.append('\n').append(category.desc()).append('\n');
             }
             sb
                     .append(option.name)
                     .append('=')
                     .append(option.load())
                     .append('\n');
         }
         return sb.toString();
     }
 
     private static final List<Option> _loadedOptions = new ArrayList<Option>();
     
     // This section holds all Options for JRuby. They will be listed in the
     // --properties output in the order they are constructed here.
     public static final Option<String> COMPILE_MODE = string(COMPILER, "compile.mode", new String[]{"JIT", "FORCE", "OFF", "OFFIR"}, "JIT", "Set compilation mode. JIT = at runtime; FORCE = before execution.");
     public static final Option<Boolean> COMPILE_DUMP = bool(COMPILER, "compile.dump", false, "Dump to console all bytecode generated at runtime.");
     public static final Option<Boolean> COMPILE_THREADLESS = bool(COMPILER, "compile.threadless", false, "(EXPERIMENTAL) Turn on compilation without polling for \"unsafe\" thread events.");
     public static final Option<Boolean> COMPILE_DYNOPT = bool(COMPILER, "compile.dynopt", false, "(EXPERIMENTAL) Use interpreter to help compiler make direct calls.");
     public static final Option<Boolean> COMPILE_FASTOPS = bool(COMPILER, "compile.fastops", false, "Turn on fast operators for Fixnum and Float.");
     public static final Option<Integer> COMPILE_CHAINSIZE = integer(COMPILER, "compile.chainsize", Constants.CHAINED_COMPILE_LINE_COUNT_DEFAULT, "Set the number of lines at which compiled bodies are \"chained\".");
     public static final Option<Boolean> COMPILE_LAZYHANDLES = bool(COMPILER, "compile.lazyHandles", false, "Generate method bindings (handles) for compiled methods lazily.");
     public static final Option<Boolean> COMPILE_PEEPHOLE = bool(COMPILER, "compile.peephole", true, "Enable or disable peephole optimizations.");
     public static final Option<Boolean> COMPILE_NOGUARDS = bool(COMPILER, "compile.noguards", false, "Compile calls without guards, for experimentation.");
     public static final Option<Boolean> COMPILE_FASTEST = bool(COMPILER, "compile.fastest", false, "Compile with all \"mostly harmless\" compiler optimizations.");
     public static final Option<Boolean> COMPILE_FASTSEND = bool(COMPILER, "compile.fastsend", false, "Compile obj.__send__(<literal>, ...) as obj.<literal>(...).");
     public static final Option<Boolean> COMPILE_FASTMASGN = bool(COMPILER, "compile.fastMasgn", false, "Return true from multiple assignment instead of a new array.");
     public static final Option<Boolean> COMPILE_INVOKEDYNAMIC = bool(COMPILER, "compile.invokedynamic", true, "Use invokedynamic for optimizing Ruby code");
     
     public static final Option<Integer> INVOKEDYNAMIC_MAXFAIL = integer(INVOKEDYNAMIC, "invokedynamic.maxfail", 1000, "Maximum call site failures after which to inline cache.");
     public static final Option<Integer> INVOKEDYNAMIC_MAXPOLY = integer(INVOKEDYNAMIC, "invokedynamic.maxpoly", 2, "Maximum polymorphism of PIC binding.");
     public static final Option<Boolean> INVOKEDYNAMIC_LOG_BINDING = bool(INVOKEDYNAMIC, "invokedynamic.log.binding", false, "Log binding of invokedynamic call sites.");
     public static final Option<Boolean> INVOKEDYNAMIC_LOG_CONSTANTS = bool(INVOKEDYNAMIC, "invokedynamic.log.constants", false, "Log invokedynamic-based constant lookups.");
     public static final Option<Boolean> INVOKEDYNAMIC_ALL = bool(INVOKEDYNAMIC, "invokedynamic.all", false, "Enable all possible uses of invokedynamic.");
     public static final Option<Boolean> INVOKEDYNAMIC_SAFE = bool(INVOKEDYNAMIC, "invokedynamic.safe", false, "Enable all safe (but maybe not fast) uses of invokedynamic.");
     public static final Option<Boolean> INVOKEDYNAMIC_INVOCATION = bool(INVOKEDYNAMIC, "invokedynamic.invocation", true, "Enable invokedynamic for method invocations.");
     public static final Option<Boolean> INVOKEDYNAMIC_INVOCATION_SWITCHPOINT = bool(INVOKEDYNAMIC, "invokedynamic.invocation.switchpoint", true, "Use SwitchPoint for class modification guards on invocations.");
     public static final Option<Boolean> INVOKEDYNAMIC_INVOCATION_INDIRECT = bool(INVOKEDYNAMIC, "invokedynamic.invocation.indirect", true, "Also bind indirect method invokers to invokedynamic.");
     public static final Option<Boolean> INVOKEDYNAMIC_INVOCATION_JAVA = bool(INVOKEDYNAMIC, "invokedynamic.invocation.java", true, "Bind Ruby to Java invocations with invokedynamic.");
     public static final Option<Boolean> INVOKEDYNAMIC_INVOCATION_ATTR = bool(INVOKEDYNAMIC, "invokedynamic.invocation.attr", true, "Bind Ruby attribue invocations directly to invokedynamic.");
     public static final Option<Boolean> INVOKEDYNAMIC_INVOCATION_FASTOPS = bool(INVOKEDYNAMIC, "invokedynamic.invocation.fastops", true, "Bind Fixnum and Float math using optimized logic.");
     public static final Option<Boolean> INVOKEDYNAMIC_CACHE = bool(INVOKEDYNAMIC, "invokedynamic.cache", true, "Use invokedynamic to load cached values like literals and constants.");
     public static final Option<Boolean> INVOKEDYNAMIC_CACHE_CONSTANTS = bool(INVOKEDYNAMIC, "invokedynamic.cache.constants", true, "Use invokedynamic to load constants.");
     public static final Option<Boolean> INVOKEDYNAMIC_CACHE_LITERALS = bool(INVOKEDYNAMIC, "invokedynamic.cache.literals", true, "Use invokedynamic to load literals.");
     public static final Option<Boolean> INVOKEDYNAMIC_CACHE_IVARS = bool(INVOKEDYNAMIC, "invokedynamic.cache.ivars", true, "Use invokedynamic to get/set instance variables.");
     public static final Option<Boolean> INVOKEDYNAMIC_CLASS_VALUES = bool(INVOKEDYNAMIC, "invokedynamic.class.values", false, "Use ClassValue to store class-specific data.");
     
     public static final Option<Integer> JIT_THRESHOLD = integer(JIT, "jit.threshold", Constants.JIT_THRESHOLD, "Set the JIT threshold to the specified method invocation count.");
     public static final Option<Integer> JIT_MAX = integer(JIT, "jit.max", Constants.JIT_MAX_METHODS_LIMIT, "Set the max count of active methods eligible for JIT-compilation.");
     public static final Option<Integer> JIT_MAXSIZE = integer(JIT, "jit.maxsize", Constants.JIT_MAX_SIZE_LIMIT, "Set the maximum full-class byte size allowed for jitted methods.");
     public static final Option<Boolean> JIT_LOGGING = bool(JIT, "jit.logging", false, "Enable JIT logging (reports successful compilation).");
     public static final Option<Boolean> JIT_LOGGING_VERBOSE = bool(JIT, "jit.logging.verbose", false, "Enable verbose JIT logging (reports failed compilation).");
     public static final Option<Boolean> JIT_DUMPING = bool(JIT, "jit.dumping", false, "Enable stdout dumping of JITed bytecode.");
     public static final Option<Integer> JIT_LOGEVERY = integer(JIT, "jit.logEvery", 0, "Log a message every n methods JIT compiled.");
     public static final Option<String> JIT_EXCLUDE = string(JIT, "jit.exclude", new String[]{"ClsOrMod","ClsOrMod::method_name","-::method_name"}, "none", "Exclude methods from JIT. Comma delimited.");
     public static final Option<Boolean> JIT_CACHE = bool(JIT, "jit.cache", true, "Cache jitted method in-memory bodies across runtimes and loads.");
     public static final Option<String> JIT_CODECACHE = string(JIT, "jit.codeCache", new String[]{"dir"}, null, "Save jitted methods to <dir> as they're compiled, for future runs.");
     public static final Option<Boolean> JIT_DEBUG = bool(JIT, "jit.debug", false, "Log loading of JITed bytecode.");
     public static final Option<Boolean> JIT_BACKGROUND = bool(JIT, "jit.background", true, "Run the JIT compiler in a background thread.");
     
     public static final Option<Boolean> IR_DEBUG             = bool(IR, "ir.debug", false, "Debug generation of JRuby IR.");
     public static final Option<Boolean> IR_PROFILE           = bool(IR, "ir.profile", false, "[EXPT]: Profile IR code during interpretation.");
     public static final Option<Boolean> IR_COMPILER_DEBUG    = bool(IR, "ir.compiler.debug", false, "Debug compilation of JRuby IR.");
     public static final Option<String>  IR_COMPILER_PASSES = string(IR, "ir.passes", null, null, "Specify comma delimeted list of passes to run.");
     public static final Option<String>  IR_INLINE_COMPILER_PASSES = string(IR, "ir.inline_passes", null, null, "Specify comma delimeted list of passes to run after inlining a method.");
     
     public static final Option<Boolean> NATIVE_ENABLED = bool(NATIVE, "native.enabled", true, "Enable/disable native code, including POSIX features and C exts.");
     public static final Option<Boolean> NATIVE_VERBOSE = bool(NATIVE, "native.verbose", false, "Enable verbose logging of native extension loading.");
     public static final Option<Boolean> CEXT_ENABLED = bool(NATIVE, "cext.enabled", false, "Enable or disable C extension support.");
     public static final Option<Boolean> FFI_COMPILE_DUMP = bool(NATIVE, "ffi.compile.dump", false, "Dump bytecode-generated FFI stubs to console.");
     public static final Option<Integer> FFI_COMPILE_THRESHOLD = integer(NATIVE, "ffi.compile.threshold", 100, "Number of FFI invocations before generating a bytecode stub.");
     public static final Option<Boolean> FFI_COMPILE_INVOKEDYNAMIC = bool(NATIVE, "ffi.compile.invokedynamic", false, "Use invokedynamic to bind FFI invocations.");
     
     public static final Option<Boolean> THREADPOOL_ENABLED = bool(THREADPOOL, "thread.pool.enabled", false, "Enable reuse of native threads via a thread pool.");
     public static final Option<Integer> THREADPOOL_MIN = integer(THREADPOOL, "thread.pool.min", 0, "The minimum number of threads to keep alive in the pool.");
     public static final Option<Integer> THREADPOOL_MAX = integer(THREADPOOL, "thread.pool.max", Integer.MAX_VALUE, "The maximum number of threads to allow in the pool.");
     public static final Option<Integer> THREADPOOL_TTL = integer(THREADPOOL, "thread.pool.ttl", 60, "The maximum number of seconds to keep alive an idle thread.");
     
     public static final Option<String> COMPAT_VERSION = string(MISCELLANEOUS, "compat.version", new String[]{"1.8","1.9","2.0"}, Constants.DEFAULT_RUBY_VERSION, "Specify the major Ruby version to be compatible with.");
     public static final Option<Boolean> OBJECTSPACE_ENABLED = bool(MISCELLANEOUS, "objectspace.enabled", false, "Enable or disable ObjectSpace.each_object.");
     public static final Option<Boolean> LAUNCH_INPROC = bool(MISCELLANEOUS, "launch.inproc", false, "Set in-process launching of e.g. system('ruby ...').");
     public static final Option<String> BYTECODE_VERSION = string(MISCELLANEOUS, "bytecode.version", new String[]{"1.5","1.6","1.7"}, SafePropertyAccessor.getProperty("java.specification.version", "1.5"), "Specify the major Java bytecode version.");
     public static final Option<Boolean> MANAGEMENT_ENABLED = bool(MISCELLANEOUS, "management.enabled", false, "Set whether JMX management is enabled.");
     public static final Option<Boolean> JUMP_BACKTRACE = bool(MISCELLANEOUS, "jump.backtrace", false, "Make non-local flow jumps generate backtraces.");
     public static final Option<Boolean> PROCESS_NOUNWRAP = bool(MISCELLANEOUS, "process.noUnwrap", false, "Do not unwrap process streams (issue on some recent JVMs).");
     public static final Option<Boolean> REIFY_CLASSES = bool(MISCELLANEOUS, "reify.classes", false, "Before instantiation, stand up a real Java class for ever Ruby class.");
     public static final Option<Boolean> REIFY_LOGERRORS = bool(MISCELLANEOUS, "reify.logErrors", false, "Log errors during reification (reify.classes=true).");
     public static final Option<Boolean> REFLECTED_HANDLES = bool(MISCELLANEOUS, "reflected.handles", false, "Use reflection for binding methods, not generated bytecode.");
     public static final Option<Boolean> BACKTRACE_COLOR = bool(MISCELLANEOUS, "backtrace.color", false, "Enable colorized backtraces.");
     public static final Option<String> BACKTRACE_STYLE = string(MISCELLANEOUS, "backtrace.style", new String[]{"normal","raw","full","mri"}, "normal", "Set the style of exception backtraces.");
     public static final Option<Boolean> BACKTRACE_MASK = bool(MISCELLANEOUS, "backtrace.mask", false, "Mask .java lines in Ruby backtraces.");
     public static final Option<String> THREAD_DUMP_SIGNAL = string(MISCELLANEOUS, "thread.dump.signal", new String[]{"USR1", "USR2", "etc"}, "USR2", "Set the signal used for dumping thread stacks.");
     public static final Option<Boolean> NATIVE_NET_PROTOCOL = bool(MISCELLANEOUS, "native.net.protocol", false, "Use native impls for parts of net/protocol.");
     public static final Option<Boolean> FIBER_COROUTINES = bool(MISCELLANEOUS, "fiber.coroutines", false, "Use JVM coroutines for Fiber.");
     public static final Option<Boolean> GLOBAL_REQUIRE_LOCK = bool(MISCELLANEOUS, "global.require.lock", false, "Use a single global lock for requires.");
     public static final Option<Boolean> NATIVE_EXEC = bool(MISCELLANEOUS, "native.exec", true, "Do a true process-obliterating native exec for Kernel#exec.");
     
     public static final Option<Boolean> DEBUG_LOADSERVICE = bool(DEBUG, "debug.loadService", false, "Log require/load file searches.");
     public static final Option<Boolean> DEBUG_LOADSERVICE_TIMING = bool(DEBUG, "debug.loadService.timing", false, "Log require/load parse+evaluate times.");
     public static final Option<Boolean> DEBUG_LAUNCH = bool(DEBUG, "debug.launch", false, "Log externally-launched processes.");
     public static final Option<Boolean> DEBUG_FULLTRACE = bool(DEBUG, "debug.fullTrace", false, "Set whether full traces are enabled (c-call/c-return).");
     public static final Option<Boolean> DEBUG_SCRIPTRESOLUTION = bool(DEBUG, "debug.scriptResolution", false, "Print which script is executed by '-S' flag.");
     public static final Option<Boolean> ERRNO_BACKTRACE = bool(DEBUG, "errno.backtrace", false, "Generate backtraces for heavily-used Errno exceptions (EAGAIN).");
     public static final Option<Boolean> LOG_EXCEPTIONS = bool(DEBUG, "log.exceptions", false, "Log every time an exception is constructed.");
     public static final Option<Boolean> LOG_BACKTRACES = bool(DEBUG, "log.backtraces", false, "Log every time an exception backtrace is generated.");
     public static final Option<Boolean> LOG_CALLERS = bool(DEBUG, "log.callers", false, "Log every time a Kernel#caller backtrace is generated.");
     public static final Option<Boolean> LOG_WARNINGS = bool(DEBUG, "log.warnings", false, "Log every time a built-in warning backtrace is generated.");
     public static final Option<String> LOGGER_CLASS = string(DEBUG, "logger.class", new String[] {"class name"}, "org.jruby.util.log.JavaUtilLoggingLogger", "Use specified class for logging.");
     
     public static final Option<Boolean> JI_SETACCESSIBLE = bool(JAVA_INTEGRATION, "ji.setAccessible", true, "Try to set inaccessible Java methods to be accessible.");
     public static final Option<Boolean> JI_LOGCANSETACCESSIBLE = bool(JAVA_INTEGRATION, "ji.logCanSetAccessible", false, "Log whether setAccessible is working.");
     public static final Option<Boolean> JI_UPPER_CASE_PACKAGE_NAME_ALLOWED = bool(JAVA_INTEGRATION, "ji.upper.case.package.name.allowed", false, "Allow Capitalized Java pacakge names.");
     public static final Option<Boolean> INTERFACES_USEPROXY = bool(JAVA_INTEGRATION, "interfaces.useProxy", false, "Use java.lang.reflect.Proxy for interface impl.");
     public static final Option<Boolean> JAVA_HANDLES = bool(JAVA_INTEGRATION, "java.handles", false, "Use generated handles instead of reflection for calling Java.");
     public static final Option<Boolean> JI_NEWSTYLEEXTENSION = bool(JAVA_INTEGRATION, "ji.newStyleExtension", false, "Extend Java classes without using a proxy object.");
     public static final Option<Boolean> JI_OBJECTPROXYCACHE = bool(JAVA_INTEGRATION, "ji.objectProxyCache", true, "Cache Java object wrappers between calls.");
 
+    public static final Option<Integer> PROFILE_MAX_METHODS = integer(PROFILING, "profile.max.methods", 100000, "Maximum number of methods to consider for profiling.");
+
     public static final Option[] PROPERTIES = _loadedOptions.toArray(new Option[0]);
     
     private static Option<String> string(Category category, String name, String[] options, String defval, String description) {
         Option<String> option = new StringOption(category, name, options, defval, description);
         _loadedOptions.add(option);
         return option;
     }
     
     private static Option<Boolean> bool(Category category, String name, Boolean defval, String description) {
         Option<Boolean> option = new BooleanOption(category, name, defval, description);
         _loadedOptions.add(option);
         return option;
     }
     
     private static Option<Integer> integer(Category category, String name, Integer defval, String description) {
         Option<Integer> option = new IntegerOption(category, name, defval, description);
         _loadedOptions.add(option);
         return option;
     }
 }
