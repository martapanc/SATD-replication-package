diff --git a/src/org/jruby/Main.java b/src/org/jruby/Main.java
index 2281b52e11..f49179fdb7 100644
--- a/src/org/jruby/Main.java
+++ b/src/org/jruby/Main.java
@@ -1,399 +1,413 @@
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
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2006 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2005 Jason Voegele <jason@jvoegele.com>
  * Copyright (C) 2005 Tim Azzopardi <tim@tigerfive.com>
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
 
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.io.File;
 
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.exceptions.ThreadKill;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.SafePropertyAccessor;
 import org.jruby.util.SimpleSampler;
 
 /**
  * Class used to launch the interpreter.
  * This is the main class as defined in the jruby.mf manifest.
  * It is very basic and does not support yet the same array of switches
  * as the C interpreter.
  *       Usage: java -jar jruby.jar [switches] [rubyfile.rb] [arguments]
  *           -e 'command'    one line of script. Several -e's allowed. Omit [programfile]
  * @author  jpetersen
  */
 public class Main {
     public Main(RubyInstanceConfig config) {
         this(config, false);
     }
 
     public Main(final InputStream in, final PrintStream out, final PrintStream err) {
         this(new RubyInstanceConfig(){{
             setInput(in);
             setOutput(out);
             setError(err);
         }});
     }
 
     public Main() {
         this(new RubyInstanceConfig());
     }
 
     private Main(RubyInstanceConfig config, boolean hardExit) {
         this.config = config;
         config.setHardExit(hardExit);
     }
 
     private Main(boolean hardExit) {
         this(new RubyInstanceConfig(), hardExit);
     }
 
     public static class Status {
         private boolean isExit = false;
         private int status = 0;
 
         /**
          * Creates a status object with the specified value and with explicit
          * exit flag. An exit flag means that Kernel.exit() has been explicitly
          * invoked during the run.
          *
          * @param staus The status value.
          */
         Status(int status) {
             this.isExit = true;
             this.status = status;
         }
 
         /**
          * Creates a status object with 0 value and no explicit exit flag.
          */
         Status() {}
 
         public boolean isExit() { return isExit; }
         public int getStatus() { return status; }
     }
 
     /**
      * This is the command-line entry point for JRuby, and should ONLY be used by
      * Java when starting up JRuby from a command-line. Use other mechanisms when
      * embedding JRuby into another application.
      *
      * @param args command-line args, provided by the JVM.
      */
     public static void main(String[] args) {
         doGCJCheck();
         
         Main main = new Main(true);
         
         try {
             Status status = main.run(args);
             if (status.isExit()) {
                 System.exit(status.getStatus());
             }
         } catch (RaiseException rj) {
             System.exit(handleRaiseException(rj));
         } catch (Throwable t) {
             // print out as a nice Ruby backtrace
             System.err.println(ThreadContext.createRawBacktraceStringFromThrowable(t));
             while ((t = t.getCause()) != null) {
                 System.err.println("Caused by:");
                 System.err.println(ThreadContext.createRawBacktraceStringFromThrowable(t));
             }
             System.exit(1);
         }
     }
 
     public Status run(String[] args) {
         try {
             config.processArguments(args);
             return run();
         } catch (MainExitException mee) {
             return handleMainExit(mee);
         } catch (OutOfMemoryError oome) {
             return handleOutOfMemory(oome);
         } catch (StackOverflowError soe) {
             return handleStackOverflow(soe);
         } catch (UnsupportedClassVersionError ucve) {
             return handleUnsupportedClassVersion(ucve);
         } catch (ThreadKill kill) {
             return new Status();
         }
     }
 
     @Deprecated
     public Status run() {
         return internalRun();
     }
 
     private Status internalRun() {
         doShowVersion();
         doShowCopyright();
 
         if (!config.shouldRunInterpreter() ) {
             doPrintUsage(false);
             doPrintProperties();
             return new Status();
         }
 
         InputStream in   = config.getScriptSource();
         String filename  = config.displayedFileName();
         
         doProcessArguments(in);
         
         Ruby runtime     = Ruby.newInstance(config);
 
         try {
             doSetContextClassLoader(runtime);
 
             if (in == null) {
                 // no script to run, return success
                 return new Status();
             } else if (config.isxFlag() && !config.hasShebangLine()) {
                 // no shebang was found and x option is set
                 throw new MainExitException(1, "jruby: no Ruby script found in input (LoadError)");
             } else if (config.isShouldCheckSyntax()) {
                 // check syntax only and exit
                 return doCheckSyntax(runtime, in, filename);
             } else {
                 // proceed to run the script
                 return doRunFromMain(runtime, in, filename);
             }
         } finally {
             runtime.tearDown();
         }
     }
 
     private Status handleUnsupportedClassVersion(UnsupportedClassVersionError ucve) {
         config.getError().println("Error: Some library (perhaps JRuby) was built with a later JVM version.");
         config.getError().println("Please use libraries built with the version you intend to use or an earlier one.");
         if (config.isVerbose()) {
             config.getError().println("Exception trace follows:");
             ucve.printStackTrace();
         } else {
             config.getError().println("Specify -w for full UnsupportedClassVersionError stack trace");
         }
         return new Status(1);
     }
 
     private Status handleStackOverflow(StackOverflowError soe) {
         // produce a nicer error since Rubyists aren't used to seeing this
         System.gc();
         String stackMax = SafePropertyAccessor.getProperty("jruby.stack.max");
         String message = "";
         if (stackMax != null) {
             message = " of " + stackMax;
         }
         config.getError().println("Error: Your application used more stack memory than the safety cap" + message + ".");
         config.getError().println("Specify -J-Xss####k to increase it (#### = cap size in KB).");
         if (config.isVerbose()) {
             config.getError().println("Exception trace follows:");
             soe.printStackTrace();
         } else {
             config.getError().println("Specify -w for full StackOverflowError stack trace");
         }
         return new Status(1);
     }
 
     private Status handleOutOfMemory(OutOfMemoryError oome) {
         // produce a nicer error since Rubyists aren't used to seeing this
         System.gc();
         String memoryMax = SafePropertyAccessor.getProperty("jruby.memory.max");
         String message = "";
         if (memoryMax != null) {
             message = " of " + memoryMax;
         }
         config.getError().println("Error: Your application used more memory than the safety cap" + message + ".");
         config.getError().println("Specify -J-Xmx####m to increase it (#### = cap size in MB).");
         if (config.isVerbose()) {
             config.getError().println("Exception trace follows:");
             oome.printStackTrace();
         } else {
             config.getError().println("Specify -w for full OutOfMemoryError stack trace");
         }
         return new Status(1);
     }
 
     private Status handleMainExit(MainExitException mee) {
         if (!mee.isAborted()) {
             config.getOutput().println(mee.getMessage());
             if (mee.isUsageError()) {
                 doPrintUsage(true);
             }
         }
         return new Status(mee.getStatus());
     }
 
     private Status doRunFromMain(Ruby runtime, InputStream in, String filename) {
         long now = -1;
         try {
             if (config.isBenchmarking()) {
                 now = System.currentTimeMillis();
             }
             if (config.isSamplingEnabled()) {
                 SimpleSampler.startSampleThread();
             }
+
+            doCheckSecurityManager();
+
             try {
                 runtime.runFromMain(in, filename);
             } finally {
                 if (config.isBenchmarking()) {
                     config.getOutput().println("Runtime: " + (System.currentTimeMillis() - now) + " ms");
                 }
                 if (config.isSamplingEnabled()) {
                     org.jruby.util.SimpleSampler.report();
                 }
             }
         } catch (RaiseException rj) {
             return new Status(handleRaiseException(rj));
         }
         return new Status();
     }
 
     private Status doCheckSyntax(Ruby runtime, InputStream in, String filename) throws RaiseException {
         int status = 0;
         try {
             runtime.parseFromMain(in, filename);
             config.getOutput().println("Syntax OK for " + filename);
         } catch (RaiseException re) {
             status = -1;
             if (re.getException().getMetaClass().getBaseName().equals("SyntaxError")) {
                 config.getOutput().println("SyntaxError in " + re.getException().message(runtime.getCurrentContext()));
             } else {
                 throw re;
             }
         }
         if (config.getArgv().length > 0) {
             for (String arg : config.getArgv()) {
                 File argFile = new File(arg);
                 if (argFile.exists()) {
                     try {
                         runtime.parseFromMain(new FileInputStream(argFile), arg);
                         config.getOutput().println("Syntax OK for " + arg);
                     } catch (FileNotFoundException fnfe) {
                         status = -1;
                         config.getOutput().println("File not found: " + arg);
                     } catch (RaiseException re) {
                         status = -1;
                         if (re.getException().getMetaClass().getBaseName().equals("SyntaxError")) {
                             config.getOutput().println("SyntaxError in " + re.getException().message(runtime.getCurrentContext()));
                         } else {
                             throw re;
                         }
                     }
                 } else {
                     status = -1;
                     config.getOutput().println("File not found: " + arg);
                 }
             }
         }
         return new Status(status);
     }
 
     private void doSetContextClassLoader(Ruby runtime) {
         // set thread context JRuby classloader here, for the main thread
         try {
             Thread.currentThread().setContextClassLoader(runtime.getJRubyClassLoader());
         } catch (SecurityException se) {
             // can't set TC classloader
             if (runtime.getInstanceConfig().isVerbose()) {
                 config.getError().println("WARNING: Security restrictions disallowed setting context classloader for main thread.");
             }
         }
     }
 
     private void doProcessArguments(InputStream in) {
         String[] args = config.parseShebangOptions(in);
         if (args.length > 0) {
             config.processArguments(args);
         }
     }
 
     private void doPrintProperties() {
         if (config.shouldPrintProperties()) {
             config.getOutput().print(config.getPropertyHelp());
         }
     }
 
     private void doPrintUsage(boolean force) {
         if (config.shouldPrintUsage() || force) {
             config.getOutput().print(config.getBasicUsageHelp());
         }
     }
 
     private void doShowCopyright() {
         if (config.isShowCopyright()) {
             config.getOutput().println(config.getCopyrightString());
         }
     }
 
     private void doShowVersion() {
         if (config.isShowVersion()) {
             config.getOutput().println(config.getVersionString());
         }
     }
 
     private static void doGCJCheck() {
         // Ensure we're not running on GCJ, since it's not supported and leads to weird errors
         if (Platform.IS_GCJ) {
             System.err.println("Fatal: GCJ (GNU Compiler for Java) is not supported by JRuby.");
             System.exit(1);
         }
     }
 
+    private void doCheckSecurityManager() {
+        if (Main.class.getClassLoader() == null && System.getSecurityManager() != null) {
+            System.err.println("Warning: security manager and JRuby running from boot classpath.\n" +
+                    "Run from jruby.jar or set env VERIFY_JRUBY=true to enable security.");
+        }
+        
+        if (config.isShowVersion()) {
+            config.getOutput().println(config.getVersionString());
+        }
+    }
+
     private static int handleRaiseException(RaiseException rj) {
         RubyException raisedException = rj.getException();
         Ruby runtime = raisedException.getRuntime();
         if (runtime.getSystemExit().isInstance(raisedException)) {
             IRubyObject status = raisedException.callMethod(runtime.getCurrentContext(), "status");
             if (status != null && !status.isNil()) {
                 return RubyNumeric.fix2int(status);
             } else {
                 return 0;
             }
         } else {
             System.err.print(RubyInstanceConfig.TRACE_TYPE.printBacktrace(raisedException));
             return 1;
         }
     }
 
     private final RubyInstanceConfig config;
 }
 
diff --git a/src/org/jruby/Ruby.java b/src/org/jruby/Ruby.java
index e01ae31671..5e9be8b7e4 100644
--- a/src/org/jruby/Ruby.java
+++ b/src/org/jruby/Ruby.java
@@ -1752,2001 +1752,2003 @@ public final class Ruby {
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
         return parser.parse(file, in, scope, new ParserConfiguration(this,
                 lineNumber, false, false, true, config));
     }
     
     public Node parseFile(InputStream in, String file, DynamicScope scope) {
         return parseFile(in, file, scope, 0);
     }
 
     public Node parseInline(InputStream in, String file, DynamicScope scope) {
         if (parserStats != null) parserStats.addEvalParse();
         ParserConfiguration parserConfig =
                 new ParserConfiguration(this, 0, false, true, false, config);
         if (is1_9) parserConfig.setDefaultEncoding(getEncodingService().getLocaleEncoding());
         return parser.parse(file, in, scope, parserConfig);
     }
 
     public Node parseEval(String content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     @Deprecated
     public Node parse(String content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         return parser.parse(file, content.getBytes(), scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
     }
     
     public Node parseEval(ByteList content, String file, DynamicScope scope, int lineNumber) {
         if (parserStats != null) parserStats.addEvalParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, false, false, false, config));
     }
 
     public Node parse(ByteList content, String file, DynamicScope scope, int lineNumber, 
             boolean extraPositionInformation) {
         if (parserStats != null) parserStats.addJRubyModuleParse();
         return parser.parse(file, content, scope, new ParserConfiguration(this,
                 lineNumber, extraPositionInformation, false, true, config));
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
 
         PrintStream errorStream = getErrorStream();
         errorStream.print(RubyInstanceConfig.TRACE_TYPE.printBacktrace(excp));
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
                 className = JITCompiler.RUBY_JIT_PREFIX + ".FILE_" + hash;
 
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
             Node scriptNode = parseFile(readStream, filename, null);
             if (script == null) {
                 script = tryCompile(scriptNode, className, new JRubyClassLoader(jrubyClassLoader), false);
             }
 
             if (script == null) {
                 failForcedCompile(scriptNode);
 
                 runInterpreter(scriptNode);
             } else {
                 runScript(script);
             }
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
 
-        getJRubyClassLoader().tearDown(isDebug());
+        if (getJRubyClassLoader() != null) {
+            getJRubyClassLoader().tearDown(isDebug());
+        }
 
         if (config.isProfilingEntireRun()) {
             System.err.println("\nmain thread profile results:");
             IProfileData profileData = (IProfileData) threadService.getMainThread().getContext().getProfileData();
             config.makeDefaultProfilePrinter(profileData).printProfile(System.err);
         }
 
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
 
         RubyProc proc =  RubyProc.newProc(this, block, type);
 
         return proc;
     }
 
     public RubyProc newBlockPassProc(Block.Type type, Block block) {
         if (type != Block.Type.LAMBDA && block.getProcObject() != null) return block.getProcObject();
 
         RubyProc proc =  RubyProc.newProc(this, block, type);
 
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
         return new RaiseException(this, exceptionClass, message, true);
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
      * Get the list of method names being profiled
      */
     public String[] getProfiledNames() {
         return profiledNames;
     }
 
     /**
      * Get the list of method objects for methods being profiled
      */
     public DynamicMethod[] getProfiledMethods() {
         return profiledMethods;
     }
 
     /**
      * Add a method and its name to the profiling arrays, so it can be printed out
      * later.
      *
      * @param name the name of the method
      * @param method
      */
     public void addProfiledMethod(String name, DynamicMethod method) {
         if (!config.isProfiling()) return;
         if (method.isUndefined()) return;
         if (method.getSerialNumber() > MAX_PROFILE_METHODS) return;
 
         int index = (int)method.getSerialNumber();
         if (profiledMethods.length <= index) {
             int newSize = Math.min((int)index * 2 + 1, MAX_PROFILE_METHODS);
             String[] newProfiledNames = new String[newSize];
             System.arraycopy(profiledNames, 0, newProfiledNames, 0, profiledNames.length);
             profiledNames = newProfiledNames;
             DynamicMethod[] newProfiledMethods = new DynamicMethod[newSize];
             System.arraycopy(profiledMethods, 0, newProfiledMethods, 0, profiledMethods.length);
             profiledMethods = newProfiledMethods;
         }
 
         // only add the first one we encounter, since others will probably share the original
         if (profiledNames[index] == null) {
             profiledNames[index] = name;
             profiledMethods[index] = method;
         }
     }
 
     private volatile int constantGeneration = 1;
     private final ThreadService threadService;
     
     private POSIX posix;
 
     private final ObjectSpace objectSpace = new ObjectSpace();
 
     private final RubySymbol.SymbolTable symbolTable = new RubySymbol.SymbolTable(this);
 
     private long randomSeed = 0;
     private long randomSeedSequence = 0;
     private Random random = new Random();
 
     private final List<EventHook> eventHooks = new Vector<EventHook>();
     private boolean hasEventHooks;  
     private boolean globalAbortOnExceptionEnabled = false;
     private boolean doNotReverseLookupEnabled = false;
     private volatile boolean objectSpaceEnabled;
     
diff --git a/src/org/jruby/runtime/load/LoadService.java b/src/org/jruby/runtime/load/LoadService.java
index 2f60fc15d9..dd8df89208 100644
--- a/src/org/jruby/runtime/load/LoadService.java
+++ b/src/org/jruby/runtime/load/LoadService.java
@@ -1,1229 +1,1229 @@
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
  * Copyright (C) 2002-2010 JRuby Community
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 package org.jruby.runtime.load;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.jar.JarFile;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.zip.ZipException;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyFile;
 import org.jruby.RubyHash;
 import org.jruby.RubyInstanceConfig;
 import org.jruby.RubyString;
 import org.jruby.ast.executable.Script;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.platform.Platform;
 import org.jruby.runtime.Constants;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.JRubyFile;
 
 /**
  * <b>How require works in JRuby</b>
  * When requiring a name from Ruby, JRuby will first remove any file extension it knows about,
  * thereby making it possible to use this string to see if JRuby has already loaded
  * the name in question. If a .rb extension is specified, JRuby will only try
  * those extensions when searching. If a .so, .o, .dll, or .jar extension is specified, JRuby
  * will only try .so or .jar when searching. Otherwise, JRuby goes through the known suffixes
  * (.rb, .rb.ast.ser, .so, and .jar) and tries to find a library with this name. The process for finding a library follows this order
  * for all searchable extensions:
  * <ol>
  * <li>First, check if the name starts with 'jar:', then the path points to a jar-file resource which is returned.</li>
  * <li>Second, try searching for the file in the current dir</li>
  * <li>Then JRuby looks through the load path trying these variants:
  *   <ol>
  *     <li>See if the current load path entry starts with 'jar:', if so check if this jar-file contains the name</li>
  *     <li>Otherwise JRuby tries to construct a path by combining the entry and the current working directy, and then see if 
  *         a file with the correct name can be reached from this point.</li>
  *   </ol>
  * </li>
  * <li>If all these fail, try to load the name as a resource from classloader resources, using the bare name as
  *     well as the load path entries</li>
  * <li>When we get to this state, the normal JRuby loading has failed. At this stage JRuby tries to load 
  *     Java native extensions, by following this process:
  *   <ol>
  *     <li>First it checks that we haven't already found a library. If we found a library of type JarredScript, the method continues.</li>
  *     <li>The first step is translating the name given into a valid Java Extension class name. First it splits the string into 
  *     each path segment, and then makes all but the last downcased. After this it takes the last entry, removes all underscores
  *     and capitalizes each part separated by underscores. It then joins everything together and tacks on a 'Service' at the end.
  *     Lastly, it removes all leading dots, to make it a valid Java FWCN.</li>
  *     <li>If the previous library was of type JarredScript, we try to add the jar-file to the classpath</li>
  *     <li>Now JRuby tries to instantiate the class with the name constructed. If this works, we return a ClassExtensionLibrary. Otherwise,
  *     the old library is put back in place, if there was one.
  *   </ol>
  * </li>
  * <li>When all separate methods have been tried and there was no result, a LoadError will be raised.</li>
  * <li>Otherwise, the name will be added to the loaded features, and the library loaded</li>
  * </ol>
  *
  * <b>How to make a class that can get required by JRuby</b>
  * <p>First, decide on what name should be used to require the extension.
  * In this purely hypothetical example, this name will be 'active_record/connection_adapters/jdbc_adapter'.
  * Then create the class name for this require-name, by looking at the guidelines above. Our class should
  * be named active_record.connection_adapters.JdbcAdapterService, and implement one of the library-interfaces.
  * The easiest one is BasicLibraryService, where you define the basicLoad-method, which will get called
  * when your library should be loaded.</p>
  * <p>The next step is to either put your compiled class on JRuby's classpath, or package the class/es inside a
  * jar-file. To package into a jar-file, we first create the file, then rename it to jdbc_adapter.jar. Then 
  * we put this jar-file in the directory active_record/connection_adapters somewhere in JRuby's load path. For
  * example, copying jdbc_adapter.jar into JRUBY_HOME/lib/ruby/site_ruby/1.8/active_record/connection_adapters
  * will make everything work. If you've packaged your extension inside a RubyGem, write a setub.rb-script that 
  * copies the jar-file to this place.</p>
  * <p>If you don't want to have the name of your extension-class to be prescribed, you can also put a file called
  * jruby-ext.properties in your jar-files META-INF directory, where you can use the key <full-extension-name>.impl
  * to make the extension library load the correct class. An example for the above would have a jruby-ext.properties
  * that contained a ruby like: "active_record/connection_adapters/jdbc_adapter=org.jruby.ar.JdbcAdapter". (NOTE: THIS
  * FEATURE IS NOT IMPLEMENTED YET.)</p>
  *
  * @author jpetersen
  */
 public class LoadService {
     private final LoadTimer loadTimer;
 
     public enum SuffixType {
         Source, Extension, Both, Neither;
         
         public static final String[] sourceSuffixes = { ".class", ".rb" };
         public static final String[] extensionSuffixes = { ".jar", ".so", ".bundle", ".dll" };
         private static final String[] allSuffixes = { ".class", ".rb", ".jar", ".so", ".bundle", ".dll" };
         private static final String[] emptySuffixes = { "" };
         
         public String[] getSuffixes() {
             switch (this) {
             case Source:
                 return sourceSuffixes;
             case Extension:
                 return extensionSuffixes;
             case Both:
                 return allSuffixes;
             case Neither:
                 return emptySuffixes;
             }
             throw new RuntimeException("Unknown SuffixType: " + this);
         }
     }
     protected static final Pattern sourcePattern = Pattern.compile("\\.(?:rb)$");
     protected static final Pattern extensionPattern = Pattern.compile("\\.(?:so|o|dll|bundle|jar)$");
 
     protected RubyArray loadPath;
     protected RubyArray loadedFeatures;
     protected List loadedFeaturesInternal;
     protected final Map<String, Library> builtinLibraries = new HashMap<String, Library>();
 
     protected final Map<String, JarFile> jarFiles = new HashMap<String, JarFile>();
 
     protected final Map<String, IAutoloadMethod> autoloadMap = new HashMap<String, IAutoloadMethod>();
 
     protected final Ruby runtime;
 
     protected boolean caseInsensitiveFS = false;
     
     public LoadService(Ruby runtime) {
         this.runtime = runtime;
         if (RubyInstanceConfig.DEBUG_LOAD_TIMINGS) {
             loadTimer = new TracingLoadTimer();
         } else {
             loadTimer = new LoadTimer();
         }
     }
 
     public void init(List additionalDirectories) {
         loadPath = RubyArray.newArray(runtime);
         loadedFeatures = RubyArray.newArray(runtime);
         loadedFeaturesInternal = Collections.synchronizedList(loadedFeatures);
         
         // add all startup load paths to the list first
         for (Iterator iter = additionalDirectories.iterator(); iter.hasNext();) {
             addPath((String) iter.next());
         }
 
         // add $RUBYLIB paths
         RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         RubyString env_rubylib = runtime.newString("RUBYLIB");
         if (env.has_key_p(env_rubylib).isTrue()) {
             String rubylib = env.op_aref(runtime.getCurrentContext(), env_rubylib).toString();
             String[] paths = rubylib.split(File.pathSeparator);
             for (int i = 0; i < paths.length; i++) {
                 addPath(paths[i]);
             }
         }
 
         // wrap in try/catch for security exceptions in an applet
         try {
             String jrubyHome = runtime.getJRubyHome();
             if (jrubyHome != null) {
                 char sep = '/';
                 String rubyDir = jrubyHome + sep + "lib" + sep + "ruby" + sep;
 
                 // If we're running in 1.9 compat mode, add Ruby 1.9 libs to path before 1.8 libs
                 if (runtime.is1_9()) {
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY1_9_MAJOR_VERSION);
                     addPath(rubyDir + "site_ruby" + sep + "shared");
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
                     addPath(rubyDir + Constants.RUBY1_9_MAJOR_VERSION);
                 } else {
                     // Add 1.8 libs
                     addPath(rubyDir + "site_ruby" + sep + Constants.RUBY_MAJOR_VERSION);
                     addPath(rubyDir + "site_ruby" + sep + "shared");
                     addPath(rubyDir + Constants.RUBY_MAJOR_VERSION);
                 }
-            }
 
-            String lowerCaseJRubyHome = jrubyHome.toLowerCase();
-            String upperCaseJRubyHome = lowerCaseJRubyHome.toUpperCase();
+                String lowerCaseJRubyHome = jrubyHome.toLowerCase();
+                String upperCaseJRubyHome = lowerCaseJRubyHome.toUpperCase();
 
-            try {
-                String canonNormal = new File(jrubyHome).getCanonicalPath();
-                String canonLower = new File(lowerCaseJRubyHome).getCanonicalPath();
-                String canonUpper = new File(upperCaseJRubyHome).getCanonicalPath();
-                if (canonNormal.equals(canonLower) && canonLower.equals(canonUpper)) {
-                    caseInsensitiveFS = true;
-                }
-            } catch (Exception e) {}
+                try {
+                    String canonNormal = new File(jrubyHome).getCanonicalPath();
+                    String canonLower = new File(lowerCaseJRubyHome).getCanonicalPath();
+                    String canonUpper = new File(upperCaseJRubyHome).getCanonicalPath();
+                    if (canonNormal.equals(canonLower) && canonLower.equals(canonUpper)) {
+                        caseInsensitiveFS = true;
+                    }
+                } catch (Exception e) {}
+            }
 
         } catch(SecurityException ignore) {}
         
         // "." dir is used for relative path loads from a given file, as in require '../foo/bar'
         if (!runtime.is1_9() && runtime.getSafeLevel() == 0) {
             addPath(".");
         }
     }
 
     protected void addLoadedFeature(RubyString loadNameRubyString) {
         loadedFeaturesInternal.add(loadNameRubyString);
     }
 
     protected void addPath(String path) {
         // Empty paths do not need to be added
         if (path == null || path.length() == 0) return;
         
         synchronized(loadPath) {
             loadPath.append(runtime.newString(path.replace('\\', '/')));
         }
     }
 
     public void load(String file, boolean wrap) {
         if(!runtime.getProfile().allowLoad(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         SearchState state = new SearchState(file);
         state.prepareLoadSearch(file);
         
         Library library = findBuiltinLibrary(state, state.searchFile, state.suffixType);
         if (library == null) library = findLibraryWithoutCWD(state, state.searchFile, state.suffixType);
 
         if (library == null) {
             library = findLibraryWithClassloaders(state, state.searchFile, state.suffixType);
             if (library == null) {
                 throw runtime.newLoadError("No such file to load -- " + file);
             }
         }
         try {
             library.load(runtime, wrap);
         } catch (IOException e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
             throw newLoadErrorFromThrowable(runtime, file, e);
         }
     }
 
     public SearchState findFileForLoad(String file) throws AlreadyLoaded {
         SearchState state = new SearchState(file);
         state.prepareRequireSearch(file);
 
         for (LoadSearcher searcher : searchers) {
             if (searcher.shouldTrySearch(state)) {
                 searcher.trySearch(state);
             } else {
                 continue;
             }
         }
 
         return state;
     }
 
     public boolean lockAndRequire(String requireName) {
         Object requireLock;
         try {
             synchronized (requireLocks) {
                 requireLock = requireLocks.get(requireName);
                 if (requireLock == null) {
                     requireLock = new Object();
                     requireLocks.put(requireName, requireLock);
                 }
             }
 
             synchronized (requireLock) {
                 return require(requireName);
             }
         } finally {
             synchronized (requireLocks) {
                 requireLocks.remove(requireName);
             }
         }
     }
 
     protected Map requireLocks = new Hashtable();
 
     public boolean smartLoad(String file) {
         checkEmptyLoad(file);
         if (Platform.IS_WINDOWS) {
             file = file.replace('\\', '/');
         }
 
         SearchState state;
 
         try {
             // Even if we don't support .so, some stdlib require .so directly.
             // Replace it with .jar to look for a java extension
             // JRUBY-5033: The ExtensionSearcher will locate C exts, too, this way.
             if (file.endsWith(".so")) {
                 file = file.replaceAll(".so$", ".jar");
             }
             state = findFileForLoad(file);
 
             return tryLoadingLibraryOrScript(runtime, state);
         } catch (AlreadyLoaded al) {
             // Library has already been loaded in some form, bail out
             return false;
         }
     }
 
     private static class LoadTimer {
         public long startLoad(String file) { return 0L; }
         public void endLoad(String file, long startTime) {}
     }
 
     private static class TracingLoadTimer extends LoadTimer {
         private final AtomicInteger indent = new AtomicInteger(0);
         private String getIndentString() {
             StringBuilder buf = new StringBuilder();
             int i = indent.get();
             for (int j = 0; j < i; j++) {
                 buf.append("  ");
             }
             return buf.toString();
         }
         @Override
         public long startLoad(String file) {
             indent.incrementAndGet();
             System.err.println(getIndentString() + "-> " + file);
             return System.currentTimeMillis();
         }
         @Override
         public void endLoad(String file, long startTime) {
             System.err.println(getIndentString() + "<- " + file + " - "
                     + (System.currentTimeMillis() - startTime) + "ms");
             indent.decrementAndGet();
         }
     }
 
     public boolean require(String file) {
         if(!runtime.getProfile().allowRequire(file)) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
 
         if (featureAlreadyLoaded(RubyString.newString(runtime, file))) {
             return false;
         }
 
         long startTime = loadTimer.startLoad(file);
         try {
             return smartLoad(file);
         } finally {
             loadTimer.endLoad(file, startTime);
         }
 
     }
 
     /**
      * Load the org.jruby.runtime.load.Library implementation specified by
      * className. The purpose of using this method is to avoid having static
      * references to the given library class, thereby avoiding the additional
      * classloading when the library is not in use.
      * 
      * @param runtime The runtime in which to load
      * @param libraryName The name of the library, to use for error messages
      * @param className The class of the library
      * @param classLoader The classloader to use to load it
      * @param wrap Whether to wrap top-level in an anonymous module
      */
     public static void reflectedLoad(Ruby runtime, String libraryName, String className, ClassLoader classLoader, boolean wrap) {
         try {
             if (classLoader == null && Ruby.isSecurityRestricted()) {
                 classLoader = runtime.getInstanceConfig().getLoader();
             }
 
             Object libObject = classLoader.loadClass(className).newInstance();
             if (libObject instanceof Library) {
                 Library library = (Library)libObject;
                 library.load(runtime, false);
             } else if (libObject instanceof BasicLibraryService) {
                 BasicLibraryService service = (BasicLibraryService)libObject;
                 service.basicLoad(runtime);
             }
         } catch (RaiseException re) {
             throw re;
         } catch (Throwable e) {
             if (runtime.getDebug().isTrue()) e.printStackTrace();
             throw runtime.newLoadError("library `" + libraryName + "' could not be loaded: " + e);
         }
     }
 
     public IRubyObject getLoadPath() {
         return loadPath;
     }
 
     public IRubyObject getLoadedFeatures() {
         return loadedFeatures;
     }
 
     public IAutoloadMethod autoloadFor(String name) {
         return autoloadMap.get(name);
     }
 
     public void removeAutoLoadFor(String name) {
         autoloadMap.remove(name);
     }
 
     public IRubyObject autoload(String name) {
         IAutoloadMethod loadMethod = autoloadMap.remove(name);
         if (loadMethod != null) {
             return loadMethod.load(runtime, name);
         }
         return null;
     }
 
     public void addAutoload(String name, IAutoloadMethod loadMethod) {
         autoloadMap.put(name, loadMethod);
     }
 
     public void addBuiltinLibrary(String name, Library library) {
         builtinLibraries.put(name, library);
     }
 
     public void removeBuiltinLibrary(String name) {
         builtinLibraries.remove(name);
     }
 
     public void removeInternalLoadedFeature(String name) {
         if (caseInsensitiveFS) {
             // on a case-insensitive filesystem, we need to search case-insensitively
             // to remove the loaded feature
             for (Iterator iter = loadedFeaturesInternal.iterator(); iter.hasNext();) {
                 Object feature = iter.next();
                 if (feature.toString().equalsIgnoreCase(name)) {
                     iter.remove();
                 }
             }
         } else {
             loadedFeaturesInternal.remove(name);
         }
     }
 
     protected boolean featureAlreadyLoaded(RubyString loadNameRubyString) {
         if (caseInsensitiveFS) {
             String name = loadNameRubyString.toString();
             // on a case-insensitive filesystem, we need to search case-insensitively
             // to find the loaded feature
             for (Iterator iter = loadedFeaturesInternal.iterator(); iter.hasNext();) {
                 Object feature = iter.next();
                 if (feature.toString().equalsIgnoreCase(name)) {
                     return true;
                 }
             }
             return false;
         } else {
             return loadedFeaturesInternal.contains(loadNameRubyString);
         }
     }
 
     protected boolean isJarfileLibrary(SearchState state, final String file) {
         return state.library instanceof JarredScript && file.endsWith(".jar");
     }
 
     protected void removeLoadedFeature(RubyString loadNameRubyString) {
 
         loadedFeaturesInternal.remove(loadNameRubyString);
     }
 
     protected void reraiseRaiseExceptions(Throwable e) throws RaiseException {
         if (e instanceof RaiseException) {
             throw (RaiseException) e;
         }
     }
     
     public interface LoadSearcher {
         public boolean shouldTrySearch(SearchState state);
         public void trySearch(SearchState state) throws AlreadyLoaded;
     }
     
     public static class AlreadyLoaded extends Exception {
         private RubyString searchNameString;
         
         public AlreadyLoaded(RubyString searchNameString) {
             this.searchNameString = searchNameString;
         }
         
         public RubyString getSearchNameString() {
             return searchNameString;
         }
     }
 
     public class BailoutSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
 
         protected void trySearch(String file, SuffixType suffixType) throws AlreadyLoaded {
             for (String suffix : suffixType.getSuffixes()) {
                 String searchName = file + suffix;
                 RubyString searchNameString = RubyString.newString(runtime, searchName);
                 if (featureAlreadyLoaded(searchNameString)) {
                     throw new AlreadyLoaded(searchNameString);
                 }
             }
         }
 
         public void trySearch(SearchState state) throws AlreadyLoaded {
             trySearch(state.searchFile, state.suffixType);
         }
     }
 
     public class SourceBailoutSearcher extends BailoutSearcher {
         public boolean shouldTrySearch(SearchState state) {
             // JRUBY-5032: Load extension files if they are required
             // explicitely, and even if an rb file of the same name
             // has already been loaded (effectively skipping the search for a source file).
             return !extensionPattern.matcher(state.loadName).find();
         }
 
         // According to Rubyspec, source files should be loaded even if an equally named
         // extension is loaded already. So we use the bailout search twice, once only
         // for source files and once for whatever suffix type the state determines
         public void trySearch(SearchState state) throws AlreadyLoaded {
             super.trySearch(state.searchFile, SuffixType.Source);
         }
     }
 
     public class NormalSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) {
             state.library = findLibraryWithoutCWD(state, state.searchFile, state.suffixType);
         }
     }
 
     public class ClassLoaderSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) {
             state.library = findLibraryWithClassloaders(state, state.searchFile, state.suffixType);
         }
     }
 
     public class ExtensionSearcher implements LoadSearcher {
         public boolean shouldTrySearch(SearchState state) {
             return (state.library == null || state.library instanceof JarredScript) && !state.searchFile.equalsIgnoreCase("");
         }
         
         public void trySearch(SearchState state) {
             // This code exploits the fact that all .jar files will be found for the JarredScript feature.
             // This is where the basic extension mechanism gets fixed
             Library oldLibrary = state.library;
 
             // Create package name, by splitting on / and joining all but the last elements with a ".", and downcasing them.
             String[] all = state.searchFile.split("/");
 
             StringBuilder finName = new StringBuilder();
             for(int i=0, j=(all.length-1); i<j; i++) {
                 finName.append(all[i].toLowerCase()).append(".");
             }
 
             try {
                 // Make the class name look nice, by splitting on _ and capitalize each segment, then joining
                 // the, together without anything separating them, and last put on "Service" at the end.
                 String[] last = all[all.length-1].split("_");
                 for(int i=0, j=last.length; i<j; i++) {
                     finName.append(Character.toUpperCase(last[i].charAt(0))).append(last[i].substring(1));
                 }
                 finName.append("Service");
 
                 // We don't want a package name beginning with dots, so we remove them
                 String className = finName.toString().replaceAll("^\\.*","");
 
                 // If there is a jar-file with the required name, we add this to the class path.
                 if(state.library instanceof JarredScript) {
                     // It's _really_ expensive to check that the class actually exists in the Jar, so
                     // we don't do that now.
                     runtime.getJRubyClassLoader().addURL(((JarredScript)state.library).getResource().getURL());
                 }
 
                 // quietly try to load the class
                 Class theClass = runtime.getJavaSupport().loadJavaClassQuiet(className);
                 state.library = new ClassExtensionLibrary(theClass);
             } catch (Exception ee) {
                 state.library = null;
                 runtime.getGlobalVariables().clear("$!");
             }
 
             // If there was a good library before, we go back to that
             if(state.library == null && oldLibrary != null) {
                 state.library = oldLibrary;
             }
         }
     }
 
     public class ScriptClassSearcher implements LoadSearcher {
         public class ScriptClassLibrary implements Library {
             private Script script;
 
             public ScriptClassLibrary(Script script) {
                 this.script = script;
             }
 
             public void load(Ruby runtime, boolean wrap) {
                 runtime.loadScript(script);
             }
         }
         
         public boolean shouldTrySearch(SearchState state) {
             return state.library == null;
         }
         
         public void trySearch(SearchState state) throws RaiseException {
             // no library or extension found, try to load directly as a class
             Script script;
             String className = buildClassName(state.searchFile);
             int lastSlashIndex = className.lastIndexOf('/');
             if (lastSlashIndex > -1 && lastSlashIndex < className.length() - 1 && !Character.isJavaIdentifierStart(className.charAt(lastSlashIndex + 1))) {
                 if (lastSlashIndex == -1) {
                     className = "_" + className;
                 } else {
                     className = className.substring(0, lastSlashIndex + 1) + "_" + className.substring(lastSlashIndex + 1);
                 }
             }
             className = className.replace('/', '.');
             try {
                 Class scriptClass = Class.forName(className);
                 script = (Script) scriptClass.newInstance();
             } catch (Exception cnfe) {
                 throw runtime.newLoadError("no such file to load -- " + state.searchFile);
             }
             state.library = new ScriptClassLibrary(script);
         }
     }
 
     public static class SearchState {
         public Library library;
         public String loadName;
         public SuffixType suffixType;
         public String searchFile;
         
         public SearchState(String file) {
             loadName = file;
         }
 
         public void prepareRequireSearch(final String file) {
             // if an extension is specified, try more targetted searches
             if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
                 Matcher matcher = null;
                 if ((matcher = sourcePattern.matcher(file)).find()) {
                     // source extensions
                     suffixType = SuffixType.Source;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else if ((matcher = extensionPattern.matcher(file)).find()) {
                     // extension extensions
                     suffixType = SuffixType.Extension;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else {
                     // unknown extension, fall back to search with extensions
                     suffixType = SuffixType.Both;
                     searchFile = file;
                 }
             } else {
                 // try all extensions
                 suffixType = SuffixType.Both;
                 searchFile = file;
             }
         }
 
         public void prepareLoadSearch(final String file) {
             // if a source extension is specified, try all source extensions
             if (file.lastIndexOf('.') > file.lastIndexOf('/')) {
                 Matcher matcher = null;
                 if ((matcher = sourcePattern.matcher(file)).find()) {
                     // source extensions
                     suffixType = SuffixType.Source;
 
                     // trim extension to try other options
                     searchFile = file.substring(0, matcher.start());
                 } else {
                     // unknown extension, fall back to exact search
                     suffixType = SuffixType.Neither;
                     searchFile = file;
                 }
             } else {
                 // try only literal search
                 suffixType = SuffixType.Neither;
                 searchFile = file;
             }
         }
         
         @Override
         public String toString() {
             StringBuilder sb = new StringBuilder();
             sb.append(this.getClass().getName()).append(": ");
             sb.append("library=").append(library.toString());
             sb.append(", loadName=").append(loadName);
             sb.append(", suffixType=").append(suffixType.toString());
             sb.append(", searchFile=").append(searchFile);
             return sb.toString();
         }
     }
     
     protected boolean tryLoadingLibraryOrScript(Ruby runtime, SearchState state) {
         // attempt to load the found library
         RubyString loadNameRubyString = RubyString.newString(runtime, state.loadName);
         try {
             synchronized (loadedFeaturesInternal) {
                 if (featureAlreadyLoaded(loadNameRubyString)) {
                     return false;
                 } else {
                     addLoadedFeature(loadNameRubyString);
                 }
             }
             
             // otherwise load the library we've found
             state.library.load(runtime, false);
             return true;
         } catch (MainExitException mee) {
             // allow MainExitException to propagate out for exec and friends
             throw mee;
         } catch (Throwable e) {
             if(isJarfileLibrary(state, state.searchFile)) {
                 return true;
             }
 
             removeLoadedFeature(loadNameRubyString);
             reraiseRaiseExceptions(e);
 
             if(runtime.getDebug().isTrue()) e.printStackTrace(runtime.getErr());
             
             RaiseException re = newLoadErrorFromThrowable(runtime, state.searchFile, e);
             re.initCause(e);
             throw re;
         }
     }
 
     private static RaiseException newLoadErrorFromThrowable(Ruby runtime, String file, Throwable t) {
         return runtime.newLoadError(String.format("load error: %s -- %s: %s", file, t.getClass().getName(), t.getMessage()));
     }
 
     // Using the BailoutSearch twice, once only for source files and once for state suffixes,
     // in order to adhere to Rubyspec
     protected final List<LoadSearcher> searchers = new ArrayList<LoadSearcher>();
     {
         searchers.add(new SourceBailoutSearcher());
         searchers.add(new NormalSearcher());
         searchers.add(new ClassLoaderSearcher());
         searchers.add(new BailoutSearcher());
         searchers.add(new ExtensionSearcher());
         searchers.add(new ScriptClassSearcher());
     }
 
     protected String buildClassName(String className) {
         // Remove any relative prefix, e.g. "./foo/bar" becomes "foo/bar".
         className = className.replaceFirst("^\\.\\/", "");
         if (className.lastIndexOf(".") != -1) {
             className = className.substring(0, className.lastIndexOf("."));
         }
         className = className.replace("-", "_minus_").replace('.', '_');
         return className;
     }
 
     protected void checkEmptyLoad(String file) throws RaiseException {
         if (file.equals("")) {
             throw runtime.newLoadError("No such file to load -- " + file);
         }
     }
 
     protected void debugLogTry(String what, String msg) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             runtime.getErr().println( "LoadService: trying " + what + ": " + msg );
         }
     }
 
     protected void debugLogFound(String what, String msg) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             runtime.getErr().println( "LoadService: found " + what + ": " + msg );
         }
     }
 
     protected void debugLogFound( LoadServiceResource resource ) {
         if (RubyInstanceConfig.DEBUG_LOAD_SERVICE) {
             String resourceUrl;
             try {
                 resourceUrl = resource.getURL().toString();
             } catch (IOException e) {
                 resourceUrl = e.getMessage();
             }
             runtime.getErr().println( "LoadService: found: " + resourceUrl );
         }
     }
 
     protected Library findBuiltinLibrary(SearchState state, String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             debugLogTry( "builtinLib",  namePlusSuffix );
             if (builtinLibraries.containsKey(namePlusSuffix)) {
                 state.loadName = namePlusSuffix;
                 Library lib = builtinLibraries.get(namePlusSuffix);
                 debugLogFound( "builtinLib", namePlusSuffix );
                 return lib;
             }
         }
         return null;
     }
 
     protected Library findLibraryWithoutCWD(SearchState state, String baseName, SuffixType suffixType) {
         Library library = null;
         
         switch (suffixType) {
         case Both:
             library = findBuiltinLibrary(state, baseName, SuffixType.Source);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Source));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Source));
             // If we fail to find as a normal Ruby script, we try to find as an extension,
             // checking for a builtin first.
             if (library == null) library = findBuiltinLibrary(state, baseName, SuffixType.Extension);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Extension));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Extension));
             break;
         case Source:
         case Extension:
             // Check for a builtin first.
             library = findBuiltinLibrary(state, baseName, suffixType);
             if (library == null) library = createLibrary(state, tryResourceFromJarURL(state, baseName, suffixType));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, suffixType));
             break;
         case Neither:
             library = createLibrary(state, tryResourceFromJarURL(state, baseName, SuffixType.Neither));
             if (library == null) library = createLibrary(state, tryResourceFromLoadPathOrURL(state, baseName, SuffixType.Neither));
             break;
         }
 
         return library;
     }
 
     protected Library findLibraryWithClassloaders(SearchState state, String baseName, SuffixType suffixType) {
         for (String suffix : suffixType.getSuffixes()) {
             String file = baseName + suffix;
             LoadServiceResource resource = findFileInClasspath(file);
             if (resource != null) {
                 state.loadName = resolveLoadName(resource, file);
                 return createLibrary(state, resource);
             }
         }
         return null;
     }
 
     protected Library createLibrary(SearchState state, LoadServiceResource resource) {
         if (resource == null) {
             return null;
         }
         String file = state.loadName;
         if (file.endsWith(".so") || file.endsWith(".dll") || file.endsWith(".bundle")) {
             return new CExtension(resource);
         } else if (file.endsWith(".jar")) {
             return new JarredScript(resource);
         } else if (file.endsWith(".class")) {
             return new JavaCompiledScript(resource);
         } else {
             return new ExternalScript(resource, file);
         }
     }
 
     protected LoadServiceResource tryResourceFromCWD(SearchState state, String baseName,SuffixType suffixType) throws RaiseException {
         LoadServiceResource foundResource = null;
         
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = baseName + suffix;
             // check current directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 debugLogTry("resourceFromCWD", file.toString());
                 if (file.isFile() && file.isAbsolute() && file.canRead()) {
                     boolean absolute = true;
                     foundResource = new LoadServiceResource(file, getFileName(file, namePlusSuffix), absolute);
                     debugLogFound(foundResource);
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break;
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
         
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceFromHome(SearchState state, String baseName, SuffixType suffixType) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         RubyHash env = (RubyHash) runtime.getObject().fastGetConstant("ENV");
         RubyString env_home = runtime.newString("HOME");
         if (env.has_key_p(env_home).isFalse()) {
             return null;
         }
         String home = env.op_aref(runtime.getCurrentContext(), env_home).toString();
         String path = baseName.substring(2);
 
         for (String suffix : suffixType.getSuffixes()) {
             String namePlusSuffix = path + suffix;
             // check home directory; if file exists, retrieve URL and return resource
             try {
                 JRubyFile file = JRubyFile.create(home, RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 debugLogTry("resourceFromHome", file.toString());
                 if (file.isFile() && file.isAbsolute() && file.canRead()) {
                     boolean absolute = true;
 
                     state.loadName = file.getPath();
                     foundResource = new LoadServiceResource(file, state.loadName, absolute);
                     debugLogFound(foundResource);
                     break;
                 }
             } catch (IllegalArgumentException illArgEx) {
             } catch (SecurityException secEx) {
             }
         }
 
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromJarURL(SearchState state, String baseName, SuffixType suffixType) {
         // if a jar or file URL, return load service resource directly without further searching
         LoadServiceResource foundResource = null;
         if (baseName.startsWith("jar:")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     URL url = new URL(namePlusSuffix);
                     debugLogTry("resourceFromJarURL", url.toString());
                     if (url.openStream() != null) {
                         foundResource = new LoadServiceResource(url, namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch (FileNotFoundException e) {
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 } catch (IOException e) {
                     throw runtime.newIOErrorFromException(e);
                 }
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break; // end suffix iteration
                 }
             }
         } else if(baseName.startsWith("file:") && baseName.indexOf("!/") != -1) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 try {
                     String jarFile = namePlusSuffix.substring(5, namePlusSuffix.indexOf("!/"));
                     JarFile file = new JarFile(jarFile);
                     String expandedFilename = expandRelativeJarPath(namePlusSuffix.substring(namePlusSuffix.indexOf("!/") + 2));
 
                     debugLogTry("resourceFromJarURL", expandedFilename.toString());
                     if(file.getJarEntry(expandedFilename) != null) {
                         foundResource = new LoadServiceResource(new URL("jar:file:" + jarFile + "!/" + expandedFilename), namePlusSuffix);
                         debugLogFound(foundResource);
                     }
                 } catch(Exception e) {}
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     break; // end suffix iteration
                 }
             }    
         }
         
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromLoadPathOrURL(SearchState state, String baseName, SuffixType suffixType) {
         LoadServiceResource foundResource = null;
 
         // if it's a ./ baseName, use CWD logic
         if (baseName.startsWith("./")) {
             foundResource = tryResourceFromCWD(state, baseName, suffixType);
 
             if (foundResource != null) {
                 state.loadName = resolveLoadName(foundResource, foundResource.getName());
             }
 
             // not found, don't bother with load path
             return foundResource;
         }
 
         // if it's a ~/ baseName use HOME logic
         if (baseName.startsWith("~/")) {
             foundResource = tryResourceFromHome(state, baseName, suffixType);
 
             if (foundResource != null) {
                 state.loadName = resolveLoadName(foundResource, foundResource.getName());
             }
 
             // not found, don't bother with load path
             return foundResource;
         }
 
         // if given path is absolute, just try it as-is (with extensions) and no load path
         if (new File(baseName).isAbsolute() || baseName.startsWith("../")) {
             for (String suffix : suffixType.getSuffixes()) {
                 String namePlusSuffix = baseName + suffix;
                 foundResource = tryResourceAsIs(namePlusSuffix);
 
                 if (foundResource != null) {
                     state.loadName = resolveLoadName(foundResource, namePlusSuffix);
                     return foundResource;
                 }
             }
 
             return null;
         }
         
         Outer: for (int i = 0; i < loadPath.size(); i++) {
             // TODO this is really inefficient, and potentially a problem everytime anyone require's something.
             // we should try to make LoadPath a special array object.
             RubyString entryString = loadPath.eltInternal(i).convertToString();
             String loadPathEntry = entryString.asJavaString();
 
             if (loadPathEntry.equals(".") || loadPathEntry.equals("")) {
                 foundResource = tryResourceFromCWD(state, baseName, suffixType);
 
                 if (foundResource != null) {
                     String ss = foundResource.getName();
                     if(ss.startsWith("./")) {
                         ss = ss.substring(2);
                     }
                     state.loadName = resolveLoadName(foundResource, ss);
                     break Outer;
                 }
             } else {
                 boolean looksLikeJarURL = loadPathLooksLikeJarURL(loadPathEntry);
                 for (String suffix : suffixType.getSuffixes()) {
                     String namePlusSuffix = baseName + suffix;
 
                     if (looksLikeJarURL) {
                         foundResource = tryResourceFromJarURLWithLoadPath(namePlusSuffix, loadPathEntry);
                     } else {
                         foundResource = tryResourceFromLoadPath(namePlusSuffix, loadPathEntry);
                     }
 
                     if (foundResource != null) {
                         String ss = namePlusSuffix;
                         if(ss.startsWith("./")) {
                             ss = ss.substring(2);
                         }
                         state.loadName = resolveLoadName(foundResource, ss);
                         break Outer; // end suffix iteration
                     }
                 }
             }
         }
         
         return foundResource;
     }
     
     protected LoadServiceResource tryResourceFromJarURLWithLoadPath(String namePlusSuffix, String loadPathEntry) {
         LoadServiceResource foundResource = null;
         
         JarFile current = jarFiles.get(loadPathEntry);
         boolean isFileJarUrl = loadPathEntry.startsWith("file:") && loadPathEntry.indexOf("!/") != -1;
         String after = isFileJarUrl ? loadPathEntry.substring(loadPathEntry.indexOf("!/") + 2) + "/" : "";
         String before = isFileJarUrl ? loadPathEntry.substring(0, loadPathEntry.indexOf("!/")) : loadPathEntry;
 
         if(null == current) {
             try {
                 if(loadPathEntry.startsWith("jar:")) {
                     current = new JarFile(loadPathEntry.substring(4));
                 } else if (loadPathEntry.endsWith(".jar")) {
                     current = new JarFile(loadPathEntry);
                 } else {
                     current = new JarFile(loadPathEntry.substring(5,loadPathEntry.indexOf("!/")));
                 }
                 jarFiles.put(loadPathEntry,current);
             } catch (ZipException ignored) {
                 if (runtime.getInstanceConfig().isDebug()) {
                     runtime.getErr().println("ZipException trying to access " + loadPathEntry + ", stack trace follows:");
                     ignored.printStackTrace(runtime.getErr());
                 }
             } catch (FileNotFoundException ignored) {
             } catch (IOException e) {
                 throw runtime.newIOErrorFromException(e);
             }
         }
         String canonicalEntry = after+namePlusSuffix;
         if (current != null ) {
             debugLogTry("resourceFromJarURLWithLoadPath", current.getName() + "!/" + canonicalEntry);
             if (current.getJarEntry(canonicalEntry) != null) {
                 try {
                     if (loadPathEntry.endsWith(".jar")) {
                         foundResource = new LoadServiceResource(new URL("jar:file:" + loadPathEntry + "!/" + canonicalEntry), "/" + namePlusSuffix);
                     } else if (loadPathEntry.startsWith("file:")) {
                         foundResource = new LoadServiceResource(new URL("jar:" + before + "!/" + canonicalEntry), loadPathEntry + "/" + namePlusSuffix);
                     } else {
                         foundResource =  new LoadServiceResource(new URL("jar:file:" + loadPathEntry.substring(4) + "!/" + namePlusSuffix), loadPathEntry + namePlusSuffix);
                     }
                     debugLogFound(foundResource);
                 } catch (MalformedURLException e) {
                     throw runtime.newIOErrorFromException(e);
                 }
             }
         }
         
         return foundResource;
     }
 
     protected boolean loadPathLooksLikeJarURL(String loadPathEntry) {
         return loadPathEntry.startsWith("jar:") || loadPathEntry.endsWith(".jar") || (loadPathEntry.startsWith("file:") && loadPathEntry.indexOf("!/") != -1);
     }
 
     protected LoadServiceResource tryResourceFromLoadPath( String namePlusSuffix,String loadPathEntry) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         try {
             if (!Ruby.isSecurityRestricted()) {
                 String reportedPath = loadPathEntry + "/" + namePlusSuffix;
                 JRubyFile actualPath;
                 boolean absolute = false;
                 // we check length == 0 for 'load', which does not use load path
                 if (new File(reportedPath).isAbsolute()) {
                     absolute = true;
                     // it's an absolute path, use it as-is
                     actualPath = JRubyFile.create(loadPathEntry, RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 } else {
                     absolute = false;
                     // prepend ./ if . is not already there, since we're loading based on CWD
                     if (reportedPath.charAt(0) != '.') {
                         reportedPath = "./" + reportedPath;
                     }
                     actualPath = JRubyFile.create(JRubyFile.create(runtime.getCurrentDirectory(), loadPathEntry).getAbsolutePath(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 }
                 debugLogTry("resourceFromLoadPath", "'" + actualPath.toString() + "' " + actualPath.isFile() + " " + actualPath.canRead());
                 if (actualPath.isFile() && actualPath.canRead()) {
                     foundResource = new LoadServiceResource(actualPath, reportedPath, absolute);
                     debugLogFound(foundResource);
                 }
             }
         } catch (SecurityException secEx) {
         }
 
         return foundResource;
     }
 
     protected LoadServiceResource tryResourceAsIs(String namePlusSuffix) throws RaiseException {
         LoadServiceResource foundResource = null;
 
         try {
             if (!Ruby.isSecurityRestricted()) {
                 String reportedPath = namePlusSuffix;
                 File actualPath;
                 // we check length == 0 for 'load', which does not use load path
                 if (new File(reportedPath).isAbsolute()) {
                     // it's an absolute path, use it as-is
                     actualPath = new File(RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 } else {
                     // prepend ./ if . is not already there, since we're loading based on CWD
                     if (reportedPath.charAt(0) == '.' && reportedPath.charAt(1) == '/') {
                         reportedPath = reportedPath.replaceFirst("\\./", runtime.getCurrentDirectory());
                     }
 
                     actualPath = JRubyFile.create(runtime.getCurrentDirectory(), RubyFile.expandUserPath(runtime.getCurrentContext(), namePlusSuffix));
                 }
                 debugLogTry("resourceAsIs", actualPath.toString());
                 if (actualPath.isFile() && actualPath.canRead()) {
                     foundResource = new LoadServiceResource(actualPath, reportedPath);
                     debugLogFound(foundResource);
                 }
             }
         } catch (SecurityException secEx) {
         }
 
         return foundResource;
     }
 
     /**
      * this method uses the appropriate lookup strategy to find a file.
      * It is used by Kernel#require.
      *
      * @mri rb_find_file
      * @param name the file to find, this is a path name
      * @return the correct file
      */
     protected LoadServiceResource findFileInClasspath(String name) {
         // Look in classpath next (we do not use File as a test since UNC names will match)
         // Note: Jar resources must NEVER begin with an '/'. (previous code said "always begin with a /")
         ClassLoader classLoader = runtime.getJRubyClassLoader();
 
         // handle security-sensitive case
         if (Ruby.isSecurityRestricted() && classLoader == null) {
             classLoader = runtime.getInstanceConfig().getLoader();
         }
 
         // absolute classpath URI, no need to iterate over loadpaths
         if (name.startsWith("classpath:/")) {
