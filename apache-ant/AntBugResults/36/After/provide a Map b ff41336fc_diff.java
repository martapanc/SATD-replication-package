diff --git a/src/main/org/apache/tools/ant/taskdefs/ExecTask.java b/src/main/org/apache/tools/ant/taskdefs/ExecTask.java
index af7671c78..ac62b3d95 100644
--- a/src/main/org/apache/tools/ant/taskdefs/ExecTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/ExecTask.java
@@ -1,727 +1,728 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import java.io.IOException;
 import java.util.Enumeration;
-import java.util.Vector;
 import java.util.Locale;
+import java.util.Map;
+import java.util.Vector;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Environment;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.RedirectorElement;
 import org.apache.tools.ant.util.FileUtils;
 
 /**
  * Executes a given command if the os platform is appropriate.
  *
  * @since Ant 1.2
  *
  * @ant.task category="control"
  */
 public class ExecTask extends Task {
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     private String os;
     private String osFamily;
 
     private File dir;
     // CheckStyle:VisibilityModifier OFF - bc
     protected boolean failOnError = false;
     protected boolean newEnvironment = false;
     private Long timeout = null;
     private Environment env = new Environment();
     protected Commandline cmdl = new Commandline();
     private String resultProperty;
     private boolean failIfExecFails = true;
     private String executable;
     private boolean resolveExecutable = false;
     private boolean searchPath = false;
     private boolean spawn = false;
     private boolean incompatibleWithSpawn = false;
 
     //include locally for screening purposes
     private String inputString;
     private File input;
     private File output;
     private File error;
 
     protected Redirector redirector = new Redirector(this);
     protected RedirectorElement redirectorElement;
     // CheckStyle:VisibilityModifier ON
 
     /**
      * Controls whether the VM (1.3 and above) is used to execute the
      * command
      */
     private boolean vmLauncher = true;
 
 
     /**
      * Create an instance.
      * Needs to be configured by binding to a project.
      */
     public ExecTask() {
     }
 
     /**
      * create an instance that is helping another task.
      * Project, OwningTarget, TaskName and description are all
      * pulled out
      * @param owner task that we belong to
      */
     public ExecTask(Task owner) {
         bindToOwner(owner);
     }
 
     /**
      * Set whether or not you want the process to be spawned.
      * Default is false.
      * @param spawn if true you do not want Ant to wait for the end of the process.
      * @since Ant 1.6
      */
     public void setSpawn(boolean spawn) {
         this.spawn = spawn;
     }
 
     /**
      * Set the timeout in milliseconds after which the process will be killed.
      *
      * @param value timeout in milliseconds.
      *
      * @since Ant 1.5
      */
     public void setTimeout(Long value) {
         timeout = value;
         incompatibleWithSpawn = true;
     }
 
     /**
      * Set the timeout in milliseconds after which the process will be killed.
      *
      * @param value timeout in milliseconds.
      */
     public void setTimeout(Integer value) {
         setTimeout(
             (Long) ((value == null) ? null : new Long(value.intValue())));
     }
 
     /**
      * Set the name of the executable program.
      * @param value the name of the executable program.
      */
     public void setExecutable(String value) {
         this.executable = value;
         cmdl.setExecutable(value);
     }
 
     /**
      * Set the working directory of the process.
      * @param d the working directory of the process.
      */
     public void setDir(File d) {
         this.dir = d;
     }
 
     /**
      * List of operating systems on which the command may be executed.
      * @param os list of operating systems on which the command may be executed.
      */
     public void setOs(String os) {
         this.os = os;
     }
 
     /**
      * List of operating systems on which the command may be executed.
      * @since Ant 1.8.0
      */
     public final String getOs() {
         return os;
     }
 
     /**
      * Sets a command line.
      * @param cmdl command line.
      * @ant.attribute ignore="true"
      */
     public void setCommand(Commandline cmdl) {
         log("The command attribute is deprecated.\n"
             + "Please use the executable attribute and nested arg elements.",
             Project.MSG_WARN);
         this.cmdl = cmdl;
     }
 
     /**
      * File the output of the process is redirected to. If error is not
      * redirected, it too will appear in the output.
      *
      * @param out name of a file to which output should be sent.
      */
     public void setOutput(File out) {
         this.output = out;
         incompatibleWithSpawn = true;
     }
 
     /**
      * Set the input file to use for the task.
      *
      * @param input name of a file from which to get input.
      */
     public void setInput(File input) {
         if (inputString != null) {
             throw new BuildException("The \"input\" and \"inputstring\" "
                 + "attributes cannot both be specified");
         }
         this.input = input;
         incompatibleWithSpawn = true;
     }
 
     /**
      * Set the string to use as input.
      *
      * @param inputString the string which is used as the input source.
      */
     public void setInputString(String inputString) {
         if (input != null) {
             throw new BuildException("The \"input\" and \"inputstring\" "
                 + "attributes cannot both be specified");
         }
         this.inputString = inputString;
         incompatibleWithSpawn = true;
     }
 
     /**
      * Controls whether error output of exec is logged. This is only useful when
      * output is being redirected and error output is desired in the Ant log.
      *
      * @param logError set to true to log error output in the normal ant log.
      */
     public void setLogError(boolean logError) {
         redirector.setLogError(logError);
         incompatibleWithSpawn |= logError;
     }
 
     /**
      * Set the File to which the error stream of the process should be redirected.
      *
      * @param error a file to which stderr should be sent.
      *
      * @since Ant 1.6
      */
     public void setError(File error) {
         this.error = error;
         incompatibleWithSpawn = true;
     }
 
     /**
      * Sets the property name whose value should be set to the output of
      * the process.
      *
      * @param outputProp name of property.
      */
     public void setOutputproperty(String outputProp) {
         redirector.setOutputProperty(outputProp);
         incompatibleWithSpawn = true;
     }
 
     /**
      * Sets the name of the property whose value should be set to the error of
      * the process.
      *
      * @param errorProperty name of property.
      *
      * @since Ant 1.6
      */
     public void setErrorProperty(String errorProperty) {
         redirector.setErrorProperty(errorProperty);
         incompatibleWithSpawn = true;
     }
 
     /**
      * Fail if the command exits with a non-zero return code.
      *
      * @param fail if true fail the command on non-zero return code.
      */
     public void setFailonerror(boolean fail) {
         failOnError = fail;
         incompatibleWithSpawn |= fail;
     }
 
     /**
      * Do not propagate old environment when new environment variables are specified.
      *
      * @param newenv if true, do not propagate old environment
      * when new environment variables are specified.
      */
     public void setNewenvironment(boolean newenv) {
         newEnvironment = newenv;
     }
 
     /**
      * Set whether to attempt to resolve the executable to a file.
      *
      * @param resolveExecutable if true, attempt to resolve the
      * path of the executable.
      */
     public void setResolveExecutable(boolean resolveExecutable) {
         this.resolveExecutable = resolveExecutable;
     }
 
     /**
      * Set whether to search nested, then
      * system PATH environment variables for the executable.
      *
      * @param searchPath if true, search PATHs.
      */
     public void setSearchPath(boolean searchPath) {
         this.searchPath = searchPath;
     }
 
     /**
      * Indicates whether to attempt to resolve the executable to a
      * file.
      * @return the resolveExecutable flag
      *
      * @since Ant 1.6
      */
     public boolean getResolveExecutable() {
         return resolveExecutable;
     }
 
     /**
      * Add an environment variable to the launched process.
      *
      * @param var new environment variable.
      */
     public void addEnv(Environment.Variable var) {
         env.addVariable(var);
     }
 
     /**
      * Adds a command-line argument.
      *
      * @return new command line argument created.
      */
     public Commandline.Argument createArg() {
         return cmdl.createArgument();
     }
 
     /**
      * Sets the name of a property in which the return code of the
      * command should be stored. Only of interest if failonerror=false.
      *
      * @since Ant 1.5
      *
      * @param resultProperty name of property.
      */
     public void setResultProperty(String resultProperty) {
         this.resultProperty = resultProperty;
         incompatibleWithSpawn = true;
     }
 
     /**
      * Helper method to set result property to the
      * passed in value if appropriate.
      *
      * @param result value desired for the result property value.
      */
     protected void maybeSetResultPropertyValue(int result) {
         if (resultProperty != null) {
             String res = Integer.toString(result);
             getProject().setNewProperty(resultProperty, res);
         }
     }
 
     /**
      * Set whether to stop the build if program cannot be started.
      * Defaults to true.
      *
      * @param flag stop the build if program cannot be started.
      *
      * @since Ant 1.5
      */
     public void setFailIfExecutionFails(boolean flag) {
         failIfExecFails = flag;
         incompatibleWithSpawn = true;
     }
 
     /**
      * Set whether output should be appended to or overwrite an existing file.
      * Defaults to false.
      *
      * @param append if true append is desired.
      *
      * @since 1.30, Ant 1.5
      */
     public void setAppend(boolean append) {
         redirector.setAppend(append);
         incompatibleWithSpawn = true;
     }
 
     /**
      * Add a <code>RedirectorElement</code> to this task.
      *
      * @param redirectorElement   <code>RedirectorElement</code>.
      * @since Ant 1.6.2
      */
     public void addConfiguredRedirector(RedirectorElement redirectorElement) {
         if (this.redirectorElement != null) {
             throw new BuildException("cannot have > 1 nested <redirector>s");
         }
         this.redirectorElement = redirectorElement;
         incompatibleWithSpawn = true;
     }
 
 
     /**
      * Restrict this execution to a single OS Family
      * @param osFamily the family to restrict to.
      */
     public void setOsFamily(String osFamily) {
         this.osFamily = osFamily.toLowerCase(Locale.ENGLISH);
     }
 
     /**
      * Restrict this execution to a single OS Family
      * @since Ant 1.8.0
      */
     public final String getOsFamily() {
         return osFamily;
     }
 
     /**
      * The method attempts to figure out where the executable is so that we can feed
      * the full path. We first try basedir, then the exec dir, and then
      * fallback to the straight executable name (i.e. on the path).
      *
      * @param exec the name of the executable.
      * @param mustSearchPath if true, the executable will be looked up in
      * the PATH environment and the absolute path is returned.
      *
      * @return the executable as a full path if it can be determined.
      *
      * @since Ant 1.6
      */
     protected String resolveExecutable(String exec, boolean mustSearchPath) {
         if (!resolveExecutable) {
             return exec;
         }
         // try to find the executable
         File executableFile = getProject().resolveFile(exec);
         if (executableFile.exists()) {
             return executableFile.getAbsolutePath();
         }
         // now try to resolve against the dir if given
         if (dir != null) {
             executableFile = FILE_UTILS.resolveFile(dir, exec);
             if (executableFile.exists()) {
                 return executableFile.getAbsolutePath();
             }
         }
         // couldn't find it - must be on path
         if (mustSearchPath) {
             Path p = null;
             String[] environment = env.getVariables();
             if (environment != null) {
                 for (int i = 0; i < environment.length; i++) {
                     if (isPath(environment[i])) {
                         p = new Path(getProject(), getPath(environment[i]));
                         break;
                     }
                 }
             }
             if (p == null) {
-                Vector envVars = Execute.getProcEnvironment();
-                Enumeration e = envVars.elements();
-                while (e.hasMoreElements()) {
-                    String line = (String) e.nextElement();
-                    if (isPath(line)) {
-                        p = new Path(getProject(), getPath(line));
-                        break;
-                    }
+                String path = getPath(Execute.getEnvironmentVariables());
+                if (path != null) {
+                    p = new Path(getProject(), path);
                 }
             }
             if (p != null) {
                 String[] dirs = p.list();
                 for (int i = 0; i < dirs.length; i++) {
                     executableFile
                         = FILE_UTILS.resolveFile(new File(dirs[i]), exec);
                     if (executableFile.exists()) {
                         return executableFile.getAbsolutePath();
                     }
                 }
             }
         }
         // mustSearchPath is false, or no PATH or not found - keep our
         // fingers crossed.
         return exec;
     }
 
     /**
      * Do the work.
      *
      * @throws BuildException in a number of circumstances:
      * <ul>
      * <li>if failIfExecFails is set to true and the process cannot be started</li>
      * <li>the java13command launcher can send build exceptions</li>
      * <li>this list is not exhaustive or limitative</li>
      * </ul>
      */
     public void execute() throws BuildException {
         // Quick fail if this is not a valid OS for the command
         if (!isValidOs()) {
             return;
         }
         File savedDir = dir; // possibly altered in prepareExec
         cmdl.setExecutable(resolveExecutable(executable, searchPath));
         checkConfiguration();
         try {
             runExec(prepareExec());
         } finally {
             dir = savedDir;
         }
     }
 
     /**
      * Has the user set all necessary attributes?
      * @throws BuildException if there are missing required parameters.
      */
     protected void checkConfiguration() throws BuildException {
         if (cmdl.getExecutable() == null) {
             throw new BuildException("no executable specified", getLocation());
         }
         if (dir != null && !dir.exists()) {
             throw new BuildException("The directory " + dir + " does not exist");
         }
         if (dir != null && !dir.isDirectory()) {
             throw new BuildException(dir + " is not a directory");
         }
         if (spawn && incompatibleWithSpawn) {
             getProject().log("spawn does not allow attributes related to input, "
             + "output, error, result", Project.MSG_ERR);
             getProject().log("spawn also does not allow timeout", Project.MSG_ERR);
             getProject().log("finally, spawn is not compatible "
                 + "with a nested I/O <redirector>", Project.MSG_ERR);
             throw new BuildException("You have used an attribute "
                 + "or nested element which is not compatible with spawn");
         }
         setupRedirector();
     }
 
     /**
      * Set up properties on the redirector that we needed to store locally.
      */
     protected void setupRedirector() {
         redirector.setInput(input);
         redirector.setInputString(inputString);
         redirector.setOutput(output);
         redirector.setError(error);
     }
 
     /**
      * Is this the OS the user wanted?
      * @return boolean.
      * <ul>
      * <li>
      * <li><code>true</code> if the os and osfamily attributes are null.</li>
      * <li><code>true</code> if osfamily is set, and the os family and must match
      * that of the current OS, according to the logic of
      * {@link Os#isOs(String, String, String, String)}, and the result of the
      * <code>os</code> attribute must also evaluate true.
      * </li>
      * <li>
      * <code>true</code> if os is set, and the system.property os.name
      * is found in the os attribute,</li>
      * <li><code>false</code> otherwise.</li>
      * </ul>
      */
     protected boolean isValidOs() {
         //hand osfamily off to Os class, if set
         if (osFamily != null && !Os.isFamily(osFamily)) {
             return false;
         }
         //the Exec OS check is different from Os.isOs(), which
         //probes for a specific OS. Instead it searches the os field
         //for the current os.name
         String myos = System.getProperty("os.name");
         log("Current OS is " + myos, Project.MSG_VERBOSE);
         if ((os != null) && (os.indexOf(myos) < 0)) {
             // this command will be executed only on the specified OS
             log("This OS, " + myos
                     + " was not found in the specified list of valid OSes: " + os,
                     Project.MSG_VERBOSE);
             return false;
         }
         return true;
     }
 
     /**
      * Set whether to launch new process with VM, otherwise use the OS's shell.
      * Default value is true.
      * @param vmLauncher true if we want to launch new process with VM,
      * false if we want to use the OS's shell.
      */
     public void setVMLauncher(boolean vmLauncher) {
         this.vmLauncher = vmLauncher;
     }
 
     /**
      * Create an Execute instance with the correct working directory set.
      *
      * @return an instance of the Execute class.
      *
      * @throws BuildException under unknown circumstances.
      */
     protected Execute prepareExec() throws BuildException {
         // default directory to the project's base directory
         if (dir == null) {
             dir = getProject().getBaseDir();
         }
         if (redirectorElement != null) {
             redirectorElement.configure(redirector);
         }
         Execute exe = new Execute(createHandler(), createWatchdog());
         exe.setAntRun(getProject());
         exe.setWorkingDirectory(dir);
         exe.setVMLauncher(vmLauncher);
         String[] environment = env.getVariables();
         if (environment != null) {
             for (int i = 0; i < environment.length; i++) {
                 log("Setting environment variable: " + environment[i],
                     Project.MSG_VERBOSE);
             }
         }
         exe.setNewenvironment(newEnvironment);
         exe.setEnvironment(environment);
         return exe;
     }
 
     /**
      * A Utility method for this classes and subclasses to run an
      * Execute instance (an external command).
      *
      * @param exe instance of the execute class.
      *
      * @throws IOException in case of problem to attach to the stdin/stdout/stderr
      * streams of the process.
      */
     protected final void runExecute(Execute exe) throws IOException {
         int returnCode = -1; // assume the worst
 
         if (!spawn) {
             returnCode = exe.execute();
 
             //test for and handle a forced process death
             if (exe.killedProcess()) {
                 String msg = "Timeout: killed the sub-process";
                 if (failOnError) {
                     throw new BuildException(msg);
                 } else {
                     log(msg, Project.MSG_WARN);
                 }
             }
             maybeSetResultPropertyValue(returnCode);
             redirector.complete();
             if (Execute.isFailure(returnCode)) {
                 if (failOnError) {
                     throw new BuildException(getTaskType() + " returned: "
                         + returnCode, getLocation());
                 } else {
                     log("Result: " + returnCode, Project.MSG_ERR);
                 }
             }
         } else {
             exe.spawn();
         }
     }
 
     /**
      * Run the command using the given Execute instance. This may be
      * overridden by subclasses.
      *
      * @param exe instance of Execute to run.
      *
      * @throws BuildException if the new process could not be started
      * only if failIfExecFails is set to true (the default).
      */
     protected void runExec(Execute exe) throws BuildException {
         // show the command
         log(cmdl.describeCommand(), Project.MSG_VERBOSE);
 
         exe.setCommandline(cmdl.getCommandline());
         try {
             runExecute(exe);
         } catch (IOException e) {
             if (failIfExecFails) {
                 throw new BuildException("Execute failed: " + e.toString(), e,
                                          getLocation());
             } else {
                 log("Execute failed: " + e.toString(), Project.MSG_ERR);
             }
         } finally {
             // close the output file if required
             logFlush();
         }
     }
 
     /**
      * Create the StreamHandler to use with our Execute instance.
      *
      * @return instance of ExecuteStreamHandler.
      *
      * @throws BuildException under unknown circumstances.
      */
     protected ExecuteStreamHandler createHandler() throws BuildException {
         return redirector.createHandler();
     }
 
     /**
      * Create the Watchdog to kill a runaway process.
      *
      * @return instance of ExecuteWatchdog.
      *
      * @throws BuildException under unknown circumstances.
      */
     protected ExecuteWatchdog createWatchdog() throws BuildException {
         return (timeout == null)
             ? null : new ExecuteWatchdog(timeout.longValue());
     }
 
     /**
      * Flush the output stream - if there is one.
      */
     protected void logFlush() {
     }
 
     private boolean isPath(String line) {
         return line.startsWith("PATH=")
             || line.startsWith("Path=");
     }
 
     private String getPath(String line) {
         return line.substring("PATH=".length());
     }
+
+    private String getPath(Map/*<String, String>*/ map) {
+        String p = (String) map.get("PATH");
+        return p != null ? p : (String) map.get("Path");
+    }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Execute.java b/src/main/org/apache/tools/ant/taskdefs/Execute.java
index bb4b66cf0..ebc1d9b32 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Execute.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Execute.java
@@ -1,1240 +1,1262 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant.taskdefs;
 
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.StringReader;
+import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
+import java.util.LinkedHashMap;
 import java.util.Map;
 import java.util.Vector;
 
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.MagicNames;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.JavaEnvUtils;
 import org.apache.tools.ant.util.StringUtils;
 
 /**
  * Runs an external program.
  *
  * @since Ant 1.2
  *
  */
 public class Execute {
 
     private static final int ONE_SECOND = 1000;
 
     /** Invalid exit code.
      * set to {@link Integer#MAX_VALUE}
      */
     public static final int INVALID = Integer.MAX_VALUE;
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     private String[] cmdl = null;
     private String[] env = null;
     private int exitValue = INVALID;
     private ExecuteStreamHandler streamHandler;
     private ExecuteWatchdog watchdog;
     private File workingDirectory = null;
     private Project project = null;
     private boolean newEnvironment = false;
 
     /** Controls whether the VM is used to launch commands, where possible. */
     private boolean useVMLauncher = true;
 
     private static String antWorkingDirectory = System.getProperty("user.dir");
     private static CommandLauncher vmLauncher = null;
     private static CommandLauncher shellLauncher = null;
-    private static Vector procEnvironment = null;
+    private static Map/*<String, String>*/ procEnvironment = null;
 
     /** Used to destroy processes when the VM exits. */
     private static ProcessDestroyer processDestroyer = new ProcessDestroyer();
 
     /** Used for replacing env variables */
     private static boolean environmentCaseInSensitive = false;
 
     /*
      * Builds a command launcher for the OS and JVM we are running under.
      */
     static {
         // Try using a JDK 1.3 launcher
         try {
             if (!Os.isFamily("os/2")) {
                 vmLauncher = new Java13CommandLauncher();
             }
         } catch (NoSuchMethodException exc) {
             // Ignore and keep trying
         }
         if (Os.isFamily("mac") && !Os.isFamily("unix")) {
             // Mac
             shellLauncher = new MacCommandLauncher(new CommandLauncher());
         } else if (Os.isFamily("os/2")) {
             // OS/2
             shellLauncher = new OS2CommandLauncher(new CommandLauncher());
         } else if (Os.isFamily("windows")) {
             environmentCaseInSensitive = true;
             CommandLauncher baseLauncher = new CommandLauncher();
 
             if (!Os.isFamily("win9x")) {
                 // Windows XP/2000/NT
                 shellLauncher = new WinNTCommandLauncher(baseLauncher);
             } else {
                 // Windows 98/95 - need to use an auxiliary script
                 shellLauncher
                     = new ScriptCommandLauncher("bin/antRun.bat", baseLauncher);
             }
         } else if (Os.isFamily("netware")) {
 
             CommandLauncher baseLauncher = new CommandLauncher();
 
             shellLauncher
                 = new PerlScriptCommandLauncher("bin/antRun.pl", baseLauncher);
         } else if (Os.isFamily("openvms")) {
             // OpenVMS
             try {
                 shellLauncher = new VmsCommandLauncher();
             } catch (NoSuchMethodException exc) {
             // Ignore and keep trying
             }
         } else {
             // Generic
             shellLauncher = new ScriptCommandLauncher("bin/antRun",
                 new CommandLauncher());
         }
     }
 
     /**
      * Set whether or not you want the process to be spawned.
      * Default is not spawned.
      *
      * @param spawn if true you do not want Ant
      *              to wait for the end of the process.
      *              Has no influence in here, the calling task contains
      *              and acts accordingly
      *
      * @since Ant 1.6
      * @deprecated
      */
     public void setSpawn(boolean spawn) {
         // Method did not do anything to begin with
     }
 
     /**
      * Find the list of environment variables for this process.
      *
-     * @return a vector containing the environment variables.
-     * The vector elements are strings formatted like variable = value.
+     * @return a map containing the environment variables.
+     * @since Ant 1.8.2
      */
-    public static synchronized Vector getProcEnvironment() {
+    public static synchronized Map/*<String,String>*/ getEnvironmentVariables() {
         if (procEnvironment != null) {
             return procEnvironment;
         }
-        procEnvironment = new Vector();
-        if (JavaEnvUtils.isAtLeastJavaVersion(JavaEnvUtils.JAVA_1_5)) {
+        if (JavaEnvUtils.isAtLeastJavaVersion(JavaEnvUtils.JAVA_1_5)
+            && !Os.isFamily("openvms")) {
             try {
-                Map/*<String,String>*/ env = (Map) System.class.getMethod("getenv", new Class[0]).invoke(null, new Object[0]);
-                Iterator it = env.entrySet().iterator();
-                while (it.hasNext()) {
-                    Map.Entry entry = (Map.Entry) it.next();
-                    procEnvironment.add(entry.getKey() + "=" + entry.getValue());
-                }
+                procEnvironment = (Map) System.class
+                    .getMethod("getenv", new Class[0])
+                    .invoke(null, new Object[0]);
                 return procEnvironment;
             } catch (Exception x) {
                 x.printStackTrace();
             }
         }
+
+        procEnvironment = new LinkedHashMap();
         try {
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             Execute exe = new Execute(new PumpStreamHandler(out));
             exe.setCommandline(getProcEnvCommand());
             // Make sure we do not recurse forever
             exe.setNewenvironment(true);
             int retval = exe.execute();
             if (retval != 0) {
                 // Just try to use what we got
             }
             BufferedReader in =
                 new BufferedReader(new StringReader(toString(out)));
 
             if (Os.isFamily("openvms")) {
-                procEnvironment = addVMSLogicals(procEnvironment, in);
+                procEnvironment = getVMSLogicals(in);
                 return procEnvironment;
             }
             String var = null;
             String line, lineSep = StringUtils.LINE_SEP;
             while ((line = in.readLine()) != null) {
                 if (line.indexOf('=') == -1) {
                     // Chunk part of previous env var (UNIX env vars can
                     // contain embedded new lines).
                     if (var == null) {
                         var = lineSep + line;
                     } else {
                         var += lineSep + line;
                     }
                 } else {
                     // New env var...append the previous one if we have it.
                     if (var != null) {
-                        procEnvironment.addElement(var);
+                        int eq = var.indexOf("=");
+                        procEnvironment.put(var.substring(0, eq),
+                                            var.substring(eq + 1));
                     }
                     var = line;
                 }
             }
             // Since we "look ahead" before adding, there's one last env var.
             if (var != null) {
-                procEnvironment.addElement(var);
+                int eq = var.indexOf("=");
+                procEnvironment.put(var.substring(0, eq), var.substring(eq + 1));
             }
         } catch (java.io.IOException exc) {
             exc.printStackTrace();
             // Just try to see how much we got
         }
         return procEnvironment;
     }
 
     /**
+     * Find the list of environment variables for this process.
+     *
+     * @return a vector containing the environment variables.
+     * The vector elements are strings formatted like variable = value.
+     * @deprecated use #getEnvironmentVariables instead
+     */
+    public static synchronized Vector getProcEnvironment() {
+        Vector v = new Vector();
+        Iterator it = getEnvironmentVariables().entrySet().iterator();
+        while (it.hasNext()) {
+            Map.Entry entry = (Map.Entry) it.next();
+            v.add(entry.getKey() + "=" + entry.getValue());
+        }
+        return v;
+    }
+
+    /**
      * This is the operation to get our environment.
      * It is a notorious troublespot pre-Java1.5, and should be approached
      * with extreme caution.
      * @return
      */
     private static String[] getProcEnvCommand() {
         if (Os.isFamily("os/2")) {
             // OS/2 - use same mechanism as Windows 2000
             return new String[] {"cmd", "/c", "set" };
         } else if (Os.isFamily("windows")) {
             // Determine if we're running under XP/2000/NT or 98/95
             if (Os.isFamily("win9x")) {
                 // Windows 98/95
                 return new String[] {"command.com", "/c", "set" };
             } else {
                 // Windows XP/2000/NT/2003
                 return new String[] {"cmd", "/c", "set" };
             }
         } else if (Os.isFamily("z/os") || Os.isFamily("unix")) {
             // On most systems one could use: /bin/sh -c env
 
             // Some systems have /bin/env, others /usr/bin/env, just try
             String[] cmd = new String[1];
             if (new File("/bin/env").canRead()) {
                 cmd[0] = "/bin/env";
             } else if (new File("/usr/bin/env").canRead()) {
                 cmd[0] = "/usr/bin/env";
             } else {
                 // rely on PATH
                 cmd[0] = "env";
             }
             return cmd;
         } else if (Os.isFamily("netware") || Os.isFamily("os/400")) {
             // rely on PATH
             return new String[] {"env"};
         } else if (Os.isFamily("openvms")) {
             return new String[] {"show", "logical"};
         } else {
             // MAC OS 9 and previous
             //TODO: I have no idea how to get it, someone must fix it
             return null;
         }
     }
 
     /**
      * ByteArrayOutputStream#toString doesn't seem to work reliably on
      * OS/390, at least not the way we use it in the execution
      * context.
      *
      * @param bos the output stream that one wants to read.
      * @return the output stream as a string, read with
      * special encodings in the case of z/os and os/400.
      *
      * @since Ant 1.5
      */
     public static String toString(ByteArrayOutputStream bos) {
         if (Os.isFamily("z/os")) {
             try {
                 return bos.toString("Cp1047");
             } catch (java.io.UnsupportedEncodingException e) {
                 //noop default encoding used
             }
         } else if (Os.isFamily("os/400")) {
             try {
                 return bos.toString("Cp500");
             } catch (java.io.UnsupportedEncodingException e) {
                 //noop default encoding used
             }
         }
         return bos.toString();
     }
 
     /**
      * Creates a new execute object using <code>PumpStreamHandler</code> for
      * stream handling.
      */
     public Execute() {
         this(new PumpStreamHandler(), null);
     }
 
     /**
      * Creates a new execute object.
      *
      * @param streamHandler the stream handler used to handle the input and
      *        output streams of the subprocess.
      */
     public Execute(ExecuteStreamHandler streamHandler) {
         this(streamHandler, null);
     }
 
     /**
      * Creates a new execute object.
      *
      * @param streamHandler the stream handler used to handle the input and
      *        output streams of the subprocess.
      * @param watchdog a watchdog for the subprocess or <code>null</code> to
      *        to disable a timeout for the subprocess.
      */
     public Execute(ExecuteStreamHandler streamHandler,
                    ExecuteWatchdog watchdog) {
         setStreamHandler(streamHandler);
         this.watchdog = watchdog;
         //By default, use the shell launcher for VMS
         //
         if (Os.isFamily("openvms")) {
             useVMLauncher = false;
         }
     }
 
     /**
      * Set the stream handler to use.
      * @param streamHandler ExecuteStreamHandler.
      * @since Ant 1.6
      */
     public void setStreamHandler(ExecuteStreamHandler streamHandler) {
         this.streamHandler = streamHandler;
     }
 
     /**
      * Returns the commandline used to create a subprocess.
      *
      * @return the commandline used to create a subprocess.
      */
     public String[] getCommandline() {
         return cmdl;
     }
 
     /**
      * Sets the commandline of the subprocess to launch.
      *
      * @param commandline the commandline of the subprocess to launch.
      */
     public void setCommandline(String[] commandline) {
         cmdl = commandline;
     }
 
     /**
      * Set whether to propagate the default environment or not.
      *
      * @param newenv whether to propagate the process environment.
      */
     public void setNewenvironment(boolean newenv) {
         newEnvironment = newenv;
     }
 
     /**
      * Returns the environment used to create a subprocess.
      *
      * @return the environment used to create a subprocess.
      */
     public String[] getEnvironment() {
         return (env == null || newEnvironment)
             ? env : patchEnvironment();
     }
 
     /**
      * Sets the environment variables for the subprocess to launch.
      *
      * @param env array of Strings, each element of which has
      * an environment variable settings in format <em>key=value</em>.
      */
     public void setEnvironment(String[] env) {
         this.env = env;
     }
 
     /**
      * Sets the working directory of the process to execute.
      *
      * <p>This is emulated using the antRun scripts unless the OS is
      * Windows NT in which case a cmd.exe is spawned,
      * or MRJ and setting user.dir works, or JDK 1.3 and there is
      * official support in java.lang.Runtime.
      *
      * @param wd the working directory of the process.
      */
     public void setWorkingDirectory(File wd) {
         workingDirectory =
             (wd == null || wd.getAbsolutePath().equals(antWorkingDirectory))
             ? null : wd;
     }
 
     /**
      * Return the working directory.
      * @return the directory as a File.
      * @since Ant 1.7
      */
     public File getWorkingDirectory() {
         return workingDirectory == null ? new File(antWorkingDirectory)
                                         : workingDirectory;
     }
 
     /**
      * Set the name of the antRun script using the project's value.
      *
      * @param project the current project.
      *
      * @throws BuildException not clear when it is going to throw an exception, but
      * it is the method's signature.
      */
     public void setAntRun(Project project) throws BuildException {
         this.project = project;
     }
 
     /**
      * Launch this execution through the VM, where possible, rather than through
      * the OS's shell. In some cases and operating systems using the shell will
      * allow the shell to perform additional processing such as associating an
      * executable with a script, etc.
      *
      * @param useVMLauncher true if exec should launch through the VM,
      *                   false if the shell should be used to launch the
      *                   command.
      */
     public void setVMLauncher(boolean useVMLauncher) {
         this.useVMLauncher = useVMLauncher;
     }
 
     /**
      * Creates a process that runs a command.
      *
      * @param project the Project, only used for logging purposes, may be null.
      * @param command the command to run.
      * @param env the environment for the command.
      * @param dir the working directory for the command.
      * @param useVM use the built-in exec command for JDK 1.3 if available.
      * @return the process started.
      * @throws IOException forwarded from the particular launcher used.
      *
      * @since Ant 1.5
      */
     public static Process launch(Project project, String[] command,
                                  String[] env, File dir, boolean useVM)
         throws IOException {
         if (dir != null && !dir.exists()) {
             throw new BuildException(dir + " doesn't exist.");
         }
         CommandLauncher launcher
             = ((useVM && vmLauncher != null) ? vmLauncher : shellLauncher);
         return launcher.exec(project, command, env, dir);
     }
 
     /**
      * Runs a process defined by the command line and returns its exit status.
      *
      * @return the exit status of the subprocess or <code>INVALID</code>.
      * @exception java.io.IOException The exception is thrown, if launching
      *            of the subprocess failed.
      */
     public int execute() throws IOException {
         if (workingDirectory != null && !workingDirectory.exists()) {
             throw new BuildException(workingDirectory + " doesn't exist.");
         }
         final Process process = launch(project, getCommandline(),
                                        getEnvironment(), workingDirectory,
                                        useVMLauncher);
         try {
             streamHandler.setProcessInputStream(process.getOutputStream());
             streamHandler.setProcessOutputStream(process.getInputStream());
             streamHandler.setProcessErrorStream(process.getErrorStream());
         } catch (IOException e) {
             process.destroy();
             throw e;
         }
         streamHandler.start();
 
         try {
             // add the process to the list of those to destroy if the VM exits
             //
             processDestroyer.add(process);
 
             if (watchdog != null) {
                 watchdog.start(process);
             }
             waitFor(process);
 
             if (watchdog != null) {
                 watchdog.stop();
             }
             streamHandler.stop();
             closeStreams(process);
 
             if (watchdog != null) {
                 watchdog.checkException();
             }
             return getExitValue();
         } catch (ThreadDeath t) {
             // #31928: forcibly kill it before continuing.
             process.destroy();
             throw t;
         } finally {
             // remove the process to the list of those to destroy if
             // the VM exits
             //
             processDestroyer.remove(process);
         }
     }
 
     /**
      * Starts a process defined by the command line.
      * Ant will not wait for this process, nor log its output.
      *
      * @throws java.io.IOException The exception is thrown, if launching
      *            of the subprocess failed.
      * @since Ant 1.6
      */
     public void spawn() throws IOException {
         if (workingDirectory != null && !workingDirectory.exists()) {
             throw new BuildException(workingDirectory + " doesn't exist.");
         }
         final Process process = launch(project, getCommandline(),
                                        getEnvironment(), workingDirectory,
                                        useVMLauncher);
         if (Os.isFamily("windows")) {
             try {
                 Thread.sleep(ONE_SECOND);
             } catch (InterruptedException e) {
                 project.log("interruption in the sleep after having spawned a"
                             + " process", Project.MSG_VERBOSE);
             }
         }
         OutputStream dummyOut = new OutputStream() {
             public void write(int b) throws IOException {
                 // Method intended to swallow whatever comes at it
             }
         };
 
         ExecuteStreamHandler handler = new PumpStreamHandler(dummyOut);
         handler.setProcessErrorStream(process.getErrorStream());
         handler.setProcessOutputStream(process.getInputStream());
         handler.start();
         process.getOutputStream().close();
 
         project.log("spawned process " + process.toString(),
                     Project.MSG_VERBOSE);
     }
 
     /**
      * Wait for a given process.
      *
      * @param process the process one wants to wait for.
      */
     protected void waitFor(Process process) {
         try {
             process.waitFor();
             setExitValue(process.exitValue());
         } catch (InterruptedException e) {
             process.destroy();
         }
     }
 
     /**
      * Set the exit value.
      *
      * @param value exit value of the process.
      */
     protected void setExitValue(int value) {
         exitValue = value;
     }
 
     /**
      * Query the exit value of the process.
      * @return the exit value or Execute.INVALID if no exit value has
      * been received.
      */
     public int getExitValue() {
         return exitValue;
     }
 
     /**
      * Checks whether <code>exitValue</code> signals a failure on the current
      * system (OS specific).
      *
      * <p><b>Note</b> that this method relies on the conventions of
      * the OS, it will return false results if the application you are
      * running doesn't follow these conventions.  One notable
      * exception is the Java VM provided by HP for OpenVMS - it will
      * return 0 if successful (like on any other platform), but this
      * signals a failure on OpenVMS.  So if you execute a new Java VM
      * on OpenVMS, you cannot trust this method.</p>
      *
      * @param exitValue the exit value (return code) to be checked.
      * @return <code>true</code> if <code>exitValue</code> signals a failure.
      */
     public static boolean isFailure(int exitValue) {
         //on openvms even exit value signals failure;
         // for other platforms nonzero exit value signals failure
         return Os.isFamily("openvms")
             ? (exitValue % 2 == 0) : (exitValue != 0);
     }
 
     /**
      * Did this execute return in a failure.
      * @see #isFailure(int)
      * @return true if and only if the exit code is interpreted as a failure
      * @since Ant1.7
      */
     public boolean isFailure() {
         return isFailure(getExitValue());
     }
 
     /**
      * Test for an untimely death of the process.
      * @return true if a watchdog had to kill the process.
      * @since Ant 1.5
      */
     public boolean killedProcess() {
         return watchdog != null && watchdog.killedProcess();
     }
 
     /**
      * Patch the current environment with the new values from the user.
      * @return the patched environment.
      */
     private String[] patchEnvironment() {
         // On OpenVMS Runtime#exec() doesn't support the environment array,
         // so we only return the new values which then will be set in
         // the generated DCL script, inheriting the parent process environment
         if (Os.isFamily("openvms")) {
             return env;
         }
-        Vector osEnv = (Vector) getProcEnvironment().clone();
+        Map/*<String, String>*/ osEnv =
+            new LinkedHashMap(getEnvironmentVariables());
         for (int i = 0; i < env.length; i++) {
             String keyValue = env[i];
-            // Get key including "="
-            String key = keyValue.substring(0, keyValue.indexOf('=') + 1);
-            if (environmentCaseInSensitive) {
-                // Nb: using default locale as key is a env name
-                key = key.toLowerCase();
-            }
-            int size = osEnv.size();
+            String key = keyValue.substring(0, keyValue.indexOf('='));
             // Find the key in the current enviroment copy
             // and remove it.
-            for (int j = 0; j < size; j++) {
-                String osEnvItem = (String) osEnv.elementAt(j);
-                String convertedItem = environmentCaseInSensitive
-                    ? osEnvItem.toLowerCase() : osEnvItem;
-                if (convertedItem.startsWith(key)) {
-                    osEnv.removeElementAt(j);
-                    if (environmentCaseInSensitive) {
+
+            // Try without changing case first
+            if (osEnv.remove(key) == null && environmentCaseInSensitive) {
+                // not found, maybe perform a case insensitive search
+
+                // Nb: using default locale as key is a env name
+                key = key.toLowerCase();
+
+                for (Iterator it = osEnv.keySet().iterator(); it.hasNext(); ) {
+                    String osEnvItem = (String) it.next();
+                    if (osEnvItem.toLowerCase().equals(key)) {
                         // Use the original casiness of the key
-                        keyValue = osEnvItem.substring(0, key.length())
-                            + keyValue.substring(key.length());
+                        key = osEnvItem;
+                        break;
                     }
-                    break;
                 }
             }
+
             // Add the key to the enviromnent copy
-            osEnv.addElement(keyValue);
+            osEnv.put(key, keyValue.substring(key.length() + 1));
         }
-        return (String[]) (osEnv.toArray(new String[osEnv.size()]));
+
+        ArrayList l = new ArrayList();
+        for (Iterator it = osEnv.entrySet().iterator(); it.hasNext(); ) {
+            Map.Entry entry = (Map.Entry) it.next();
+            l.add(entry.getKey() + "=" + entry.getValue());
+        }
+        return (String[]) (l.toArray(new String[osEnv.size()]));
     }
 
     /**
      * A utility method that runs an external command.  Writes the output and
      * error streams of the command to the project log.
      *
      * @param task      The task that the command is part of.  Used for logging
      * @param cmdline   The command to execute.
      *
      * @throws BuildException if the command does not exit successfully.
      */
     public static void runCommand(Task task, String[] cmdline)
         throws BuildException {
         try {
             task.log(Commandline.describeCommand(cmdline),
                      Project.MSG_VERBOSE);
             Execute exe = new Execute(
                 new LogStreamHandler(task, Project.MSG_INFO, Project.MSG_ERR));
             exe.setAntRun(task.getProject());
             exe.setCommandline(cmdline);
             int retval = exe.execute();
             if (isFailure(retval)) {
                 throw new BuildException(cmdline[0]
                     + " failed with return code " + retval, task.getLocation());
             }
         } catch (java.io.IOException exc) {
             throw new BuildException("Could not launch " + cmdline[0] + ": "
                 + exc, task.getLocation());
         }
     }
 
     /**
      * Close the streams belonging to the given Process.
      * @param process   the <code>Process</code>.
      */
     public static void closeStreams(Process process) {
         FileUtils.close(process.getInputStream());
         FileUtils.close(process.getOutputStream());
         FileUtils.close(process.getErrorStream());
     }
 
     /**
-     * This method is VMS specific and used by getProcEnvironment().
+     * This method is VMS specific and used by getEnvironmentVariables().
      *
-     * Parses VMS logicals from <code>in</code> and adds them to
-     * <code>environment</code>.  <code>in</code> is expected to be the
+     * Parses VMS logicals from <code>in</code> and returns them as a Map.
+     * <code>in</code> is expected to be the
      * output of "SHOW LOGICAL".  The method takes care of parsing the output
      * correctly as well as making sure that a logical defined in multiple
      * tables only gets added from the highest order table.  Logicals with
      * multiple equivalence names are mapped to a variable with multiple
      * values separated by a comma (,).
      */
-    private static Vector addVMSLogicals(Vector environment, BufferedReader in)
+    private static Map getVMSLogicals(BufferedReader in)
         throws IOException {
         HashMap logicals = new HashMap();
         String logName = null, logValue = null, newLogName;
         String line = null;
         // CheckStyle:MagicNumber OFF
         while ((line = in.readLine()) != null) {
             // parse the VMS logicals into required format ("VAR=VAL[,VAL2]")
             if (line.startsWith("\t=")) {
                 // further equivalence name of previous logical
                 if (logName != null) {
                     logValue += "," + line.substring(4, line.length() - 1);
                 }
             } else if (line.startsWith("  \"")) {
                 // new logical?
                 if (logName != null) {
                     logicals.put(logName, logValue);
                 }
                 int eqIndex = line.indexOf('=');
                 newLogName = line.substring(3, eqIndex - 2);
                 if (logicals.containsKey(newLogName)) {
                     // already got this logical from a higher order table
                     logName = null;
                 } else {
                     logName = newLogName;
                     logValue = line.substring(eqIndex + 3, line.length() - 1);
                 }
             }
         }
         // CheckStyle:MagicNumber ON
         // Since we "look ahead" before adding, there's one last env var.
         if (logName != null) {
             logicals.put(logName, logValue);
         }
-        for (Iterator i = logicals.keySet().iterator(); i.hasNext();) {
-            String logical = (String) i.next();
-            environment.add(logical + "=" + logicals.get(logical));
-        }
-        return environment;
+        return logicals;
     }
 
     /**
      * A command launcher for a particular JVM/OS platform.  This class is
      * a general purpose command launcher which can only launch commands in
      * the current working directory.
      */
     private static class CommandLauncher {
         /**
          * Launches the given command in a new process.
          *
          * @param project       The project that the command is part of.
          * @param cmd           The command to execute.
          * @param env           The environment for the new process.  If null,
          *                      the environment of the current process is used.
          * @return the created Process.
          * @throws IOException if attempting to run a command in a
          * specific directory.
          */
         public Process exec(Project project, String[] cmd, String[] env)
              throws IOException {
             if (project != null) {
                 project.log("Execute:CommandLauncher: "
                     + Commandline.describeCommand(cmd), Project.MSG_DEBUG);
             }
             return Runtime.getRuntime().exec(cmd, env);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          *
          * @param project       The project that the command is part of.
          * @param cmd           The command to execute.
          * @param env           The environment for the new process.  If null,
          *                      the environment of the current process is used.
          * @param workingDir    The directory to start the command in.  If null,
          *                      the current directory is used.
          * @return the created Process.
          * @throws IOException  if trying to change directory.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             if (workingDir == null) {
                 return exec(project, cmd, env);
             }
             throw new IOException("Cannot execute a process in different "
                 + "directory under this JVM");
         }
     }
 
     /**
      * A command launcher for JDK/JRE 1.3 (and higher).  Uses the built-in
      * Runtime.exec() command.
      */
     private static class Java13CommandLauncher extends CommandLauncher {
 
         public Java13CommandLauncher() throws NoSuchMethodException {
             // Used to verify if Java13 is available, is prerequisite in ant 1.8
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @param workingDir the working directory where the command
          * should run.
          * @return the created Process.
          * @throws IOException probably forwarded from Runtime#exec.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             try {
                 if (project != null) {
                     project.log("Execute:Java13CommandLauncher: "
                         + Commandline.describeCommand(cmd), Project.MSG_DEBUG);
                 }
                 return Runtime.getRuntime().exec(cmd, env, workingDir);
             } catch (IOException ioex) {
                 throw ioex;
             } catch (Exception exc) {
                 // IllegalAccess, IllegalArgument, ClassCast
                 throw new BuildException("Unable to execute command", exc);
             }
         }
     }
 
     /**
      * A command launcher that proxies another command launcher.
      *
      * Sub-classes override exec(args, env, workdir).
      */
     private static class CommandLauncherProxy extends CommandLauncher {
         private CommandLauncher myLauncher;
 
         CommandLauncherProxy(CommandLauncher launcher) {
             myLauncher = launcher;
         }
 
         /**
          * Launches the given command in a new process.  Delegates this
          * method to the proxied launcher.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @return the created Process.
          * @throws IOException forwarded from the exec method of the
          * command launcher.
          */
         public Process exec(Project project, String[] cmd, String[] env)
             throws IOException {
             return myLauncher.exec(project, cmd, env);
         }
     }
 
     /**
      * A command launcher for OS/2 that uses 'cmd.exe' when launching
      * commands in directories other than the current working
      * directory.
      *
      * <p>Unlike Windows NT and friends, OS/2's cd doesn't support the
      * /d switch to change drives and directories in one go.</p>
      */
     private static class OS2CommandLauncher extends CommandLauncherProxy {
         OS2CommandLauncher(CommandLauncher launcher) {
             super(launcher);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @param workingDir working directory where the command should run.
          * @return the created Process.
          * @throws IOException forwarded from the exec method of the
          * command launcher.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             File commandDir = workingDir;
             if (workingDir == null) {
                 if (project != null) {
                     commandDir = project.getBaseDir();
                 } else {
                     return exec(project, cmd, env);
                 }
             }
             // Use cmd.exe to change to the specified drive and
             // directory before running the command
             final int preCmdLength = 7;
             final String cmdDir = commandDir.getAbsolutePath();
             String[] newcmd = new String[cmd.length + preCmdLength];
             // CheckStyle:MagicNumber OFF - do not bother
             newcmd[0] = "cmd";
             newcmd[1] = "/c";
             newcmd[2] = cmdDir.substring(0, 2);
             newcmd[3] = "&&";
             newcmd[4] = "cd";
             newcmd[5] = cmdDir.substring(2);
             newcmd[6] = "&&";
             // CheckStyle:MagicNumber ON
             System.arraycopy(cmd, 0, newcmd, preCmdLength, cmd.length);
 
             return exec(project, newcmd, env);
         }
     }
 
     /**
      * A command launcher for Windows XP/2000/NT that uses 'cmd.exe' when
      * launching commands in directories other than the current working
      * directory.
      */
     private static class WinNTCommandLauncher extends CommandLauncherProxy {
         WinNTCommandLauncher(CommandLauncher launcher) {
             super(launcher);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @param workingDir working directory where the command should run.
          * @return the created Process.
          * @throws IOException forwarded from the exec method of the
          * command launcher.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             File commandDir = workingDir;
             if (workingDir == null) {
                 if (project != null) {
                     commandDir = project.getBaseDir();
                 } else {
                     return exec(project, cmd, env);
                 }
             }
             // Use cmd.exe to change to the specified directory before running
             // the command
             final int preCmdLength = 6;
             String[] newcmd = new String[cmd.length + preCmdLength];
             // CheckStyle:MagicNumber OFF - do not bother
             newcmd[0] = "cmd";
             newcmd[1] = "/c";
             newcmd[2] = "cd";
             newcmd[3] = "/d";
             newcmd[4] = commandDir.getAbsolutePath();
             newcmd[5] = "&&";
             // CheckStyle:MagicNumber ON
             System.arraycopy(cmd, 0, newcmd, preCmdLength, cmd.length);
 
             return exec(project, newcmd, env);
         }
     }
 
     /**
      * A command launcher for Mac that uses a dodgy mechanism to change
      * working directory before launching commands.
      */
     private static class MacCommandLauncher extends CommandLauncherProxy {
         MacCommandLauncher(CommandLauncher launcher) {
             super(launcher);
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @param workingDir working directory where the command should run.
          * @return the created Process.
          * @throws IOException forwarded from the exec method of the
          * command launcher.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             if (workingDir == null) {
                 return exec(project, cmd, env);
             }
             System.getProperties().put("user.dir", workingDir.getAbsolutePath());
             try {
                 return exec(project, cmd, env);
             } finally {
                 System.getProperties().put("user.dir", antWorkingDirectory);
             }
         }
     }
 
     /**
      * A command launcher that uses an auxiliary script to launch commands
      * in directories other than the current working directory.
      */
     private static class ScriptCommandLauncher extends CommandLauncherProxy {
         ScriptCommandLauncher(String script, CommandLauncher launcher) {
             super(launcher);
             myScript = script;
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @param workingDir working directory where the command should run.
          * @return the created Process.
          * @throws IOException forwarded from the exec method of the
          * command launcher.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             if (project == null) {
                 if (workingDir == null) {
                     return exec(project, cmd, env);
                 }
                 throw new IOException("Cannot locate antRun script: "
                     + "No project provided");
             }
             // Locate the auxiliary script
             String antHome = project.getProperty(MagicNames.ANT_HOME);
             if (antHome == null) {
                 throw new IOException("Cannot locate antRun script: "
                     + "Property '" + MagicNames.ANT_HOME + "' not found");
             }
             String antRun =
                 FILE_UTILS.resolveFile(project.getBaseDir(),
                         antHome + File.separator + myScript).toString();
 
             // Build the command
             File commandDir = workingDir;
             if (workingDir == null) {
                 commandDir = project.getBaseDir();
             }
             String[] newcmd = new String[cmd.length + 2];
             newcmd[0] = antRun;
             newcmd[1] = commandDir.getAbsolutePath();
             System.arraycopy(cmd, 0, newcmd, 2, cmd.length);
 
             return exec(project, newcmd, env);
         }
 
         private String myScript;
     }
 
     /**
      * A command launcher that uses an auxiliary perl script to launch commands
      * in directories other than the current working directory.
      */
     private static class PerlScriptCommandLauncher
         extends CommandLauncherProxy {
         private String myScript;
 
         PerlScriptCommandLauncher(String script, CommandLauncher launcher) {
             super(launcher);
             myScript = script;
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @param workingDir working directory where the command should run.
          * @return the created Process.
          * @throws IOException forwarded from the exec method of the
          * command launcher.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             if (project == null) {
                 if (workingDir == null) {
                     return exec(project, cmd, env);
                 }
                 throw new IOException("Cannot locate antRun script: "
                     + "No project provided");
             }
             // Locate the auxiliary script
             String antHome = project.getProperty(MagicNames.ANT_HOME);
             if (antHome == null) {
                 throw new IOException("Cannot locate antRun script: "
                     + "Property '" + MagicNames.ANT_HOME + "' not found");
             }
             String antRun =
                 FILE_UTILS.resolveFile(project.getBaseDir(),
                         antHome + File.separator + myScript).toString();
 
             // Build the command
             File commandDir = workingDir;
             if (workingDir == null) {
                 commandDir = project.getBaseDir();
             }
             // CheckStyle:MagicNumber OFF
             String[] newcmd = new String[cmd.length + 3];
             newcmd[0] = "perl";
             newcmd[1] = antRun;
             newcmd[2] = commandDir.getAbsolutePath();
             System.arraycopy(cmd, 0, newcmd, 3, cmd.length);
             // CheckStyle:MagicNumber ON
 
             return exec(project, newcmd, env);
         }
     }
 
     /**
      * A command launcher for VMS that writes the command to a temporary DCL
      * script before launching commands.  This is due to limitations of both
      * the DCL interpreter and the Java VM implementation.
      */
     private static class VmsCommandLauncher extends Java13CommandLauncher {
 
         public VmsCommandLauncher() throws NoSuchMethodException {
             super();
         }
 
         /**
          * Launches the given command in a new process.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @return the created Process.
          * @throws IOException forwarded from the exec method of the
          * command launcher.
          */
         public Process exec(Project project, String[] cmd, String[] env)
             throws IOException {
             File cmdFile = createCommandFile(cmd, env);
             Process p
                 = super.exec(project, new String[] {cmdFile.getPath()}, env);
             deleteAfter(cmdFile, p);
             return p;
         }
 
         /**
          * Launches the given command in a new process, in the given working
          * directory.  Note that under Java 1.4.0 and 1.4.1 on VMS this
          * method only works if <code>workingDir</code> is null or the logical
          * JAVA$FORK_SUPPORT_CHDIR needs to be set to TRUE.
          * @param project the Ant project.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @param workingDir working directory where the command should run.
          * @return the created Process.
          * @throws IOException forwarded from the exec method of the
          * command launcher.
          */
         public Process exec(Project project, String[] cmd, String[] env,
                             File workingDir) throws IOException {
             File cmdFile = createCommandFile(cmd, env);
             Process p = super.exec(project, new String[] {cmdFile.getPath()},
                                    env, workingDir);
             deleteAfter(cmdFile, p);
             return p;
         }
 
         /*
          * Writes the command into a temporary DCL script and returns the
          * corresponding File object.  The script will be deleted on exit.
          * @param cmd the command line to execute as an array of strings.
          * @param env the environment to set as an array of strings.
          * @return the command File.
          * @throws IOException if errors are encountered creating the file.
          */
         private File createCommandFile(String[] cmd, String[] env)
             throws IOException {
             File script = FILE_UTILS.createTempFile("ANT", ".COM", null, true, true);
             BufferedWriter out = null;
             try {
                 out = new BufferedWriter(new FileWriter(script));
 
                 // add the environment as logicals to the DCL script
                 if (env != null) {
                     int eqIndex;
                     for (int i = 0; i < env.length; i++) {
                         eqIndex = env[i].indexOf('=');
                         if (eqIndex != -1) {
                             out.write("$ DEFINE/NOLOG ");
                             out.write(env[i].substring(0, eqIndex));
                             out.write(" \"");
                             out.write(env[i].substring(eqIndex + 1));
                             out.write('\"');
                             out.newLine();
                         }
                     }
                 }
                 out.write("$ " + cmd[0]);
                 for (int i = 1; i < cmd.length; i++) {
                     out.write(" -");
                     out.newLine();
                     out.write(cmd[i]);
                 }
             } finally {
                 FileUtils.close(out);
             }
             return script;
         }
 
         private void deleteAfter(final File f, final Process p) {
             new Thread() {
                 public void run() {
                     try {
                         p.waitFor();
                     } catch (InterruptedException e) {
                         //ignore
                     }
                     FileUtils.delete(f);
                 }
             }
             .start();
         }
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/Property.java b/src/main/org/apache/tools/ant/taskdefs/Property.java
index d7494c3a1..22f926c9a 100644
--- a/src/main/org/apache/tools/ant/taskdefs/Property.java
+++ b/src/main/org/apache/tools/ant/taskdefs/Property.java
@@ -1,743 +1,737 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 package org.apache.tools.ant.taskdefs;
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.Method;
 import java.net.URL;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Vector;
 
 import org.apache.tools.ant.AntClassLoader;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.PropertyHelper;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.property.ResolvePropertyMap;
 
 /**
  * Sets a property by name, or set of properties (from file or
  * resource) in the project.  </p>
  * Properties are immutable: whoever sets a property first freezes it for the
  * rest of the build; they are most definitely not variable.
  * <p>There are seven ways to set properties:</p>
  * <ul>
  *   <li>By supplying both the <i>name</i> and <i>value</i> attribute.</li>
  *   <li>By supplying the <i>name</i> and nested text.</li>
  *   <li>By supplying both the <i>name</i> and <i>refid</i> attribute.</li>
  *   <li>By setting the <i>file</i> attribute with the filename of the property
  *     file to load. This property file has the format as defined by the file used
  *     in the class java.util.Properties.</li>
  *   <li>By setting the <i>url</i> attribute with the url from which to load the
  *     properties. This url must be directed to a file that has the format as defined
  *     by the file used in the class java.util.Properties.</li>
  *   <li>By setting the <i>resource</i> attribute with the resource name of the
  *     property file to load. This property file has the format as defined by the
  *     file used in the class java.util.Properties.</li>
  *   <li>By setting the <i>environment</i> attribute with a prefix to use.
  *     Properties will be defined for every environment variable by
  *     prefixing the supplied name and a period to the name of the variable.</li>
  * </ul>
  * <p>Although combinations of these ways are possible, only one should be used
  * at a time. Problems might occur with the order in which properties are set, for
  * instance.</p>
  * <p>The value part of the properties being set, might contain references to other
  * properties. These references are resolved at the time these properties are set.
  * This also holds for properties loaded from a property file.</p>
  * Properties are case sensitive.
  *
  * @since Ant 1.1
  *
  * @ant.attribute.group name="name" description="One of these, when using the name attribute"
  * @ant.attribute.group name="noname" description="One of these, when not using the name attribute"
  * @ant.task category="property"
  */
 public class Property extends Task {
 
     // CheckStyle:VisibilityModifier OFF - bc
     protected String name;
     protected String value;
     protected File file;
     protected URL url;
     protected String resource;
     protected Path classpath;
     protected String env;
     protected Reference ref;
     protected String prefix;
     private Project fallback;
     private Object untypedValue;
     private boolean valueAttributeUsed = false;
     private boolean relative = false;
     private File basedir;
     private boolean prefixValues = false;
 
     protected boolean userProperty; // set read-only properties
     // CheckStyle:VisibilityModifier ON
 
     /**
      * Constructor for Property.
      */
     public Property() {
         this(false);
     }
 
     /**
      * Constructor for Property.
      * @param userProperty if true this is a user property
      * @since Ant 1.5
      */
     protected Property(boolean userProperty) {
         this(userProperty, null);
     }
 
     /**
      * Constructor for Property.
      * @param userProperty if true this is a user property
      * @param fallback a project to use to look for references if the reference is
      *                 not in the current project
      * @since Ant 1.5
      */
     protected Property(boolean userProperty, Project fallback) {
         this.userProperty = userProperty;
         this.fallback = fallback;
     }
 
     /**
      * Sets 'relative' attribute.
      * @param relative new value
      * @since Ant 1.8.0
      */
     public void setRelative(boolean relative) {
         this.relative = relative;
     }
 
     /**
      * Sets 'basedir' attribute.
      * @param basedir new value
      * @since Ant 1.8.0
      */
     public void setBasedir(File basedir) {
         this.basedir = basedir;
     }
 
     /**
      * The name of the property to set.
      * @param name property name
      */
     public void setName(String name) {
         this.name = name;
     }
 
     /**
      * Get the property name.
      * @return the property name
      */
     public String getName() {
         return name;
     }
 
     /**
      * Sets the property to the absolute filename of the
      * given file. If the value of this attribute is an absolute path, it
      * is left unchanged (with / and \ characters converted to the
      * current platforms conventions). Otherwise it is taken as a path
      * relative to the project's basedir and expanded.
      * @param location path to set
      *
      * @ant.attribute group="name"
      */
     public void setLocation(File location) {
         if (relative) {
             internalSetValue(location);
         } else {
             setValue(location.getAbsolutePath());
         }
     }
 
     /* the following method is first in source so IH will pick it up first:
      * Hopefully we'll never get any classes compiled by wise-guy compilers that behave otherwise...
      */
 
     /**
      * Set the value of the property.
      * @param value the value to use.
      */
     public void setValue(Object value) {
         valueAttributeUsed = true;
         internalSetValue(value);
     }
 
     private void internalSetValue(Object value) {
         this.untypedValue = value;
         //preserve protected string value for subclasses :(
         this.value = value == null ? null : value.toString();
     }
 
     /**
      * Set the value of the property as a String.
      * @param value value to assign
      *
      * @ant.attribute group="name"
      */
     public void setValue(String value) {
         setValue((Object) value);
     }
 
     /**
      * Set a (multiline) property as nested text.
      * @param msg the text to append to the output text
      * @since Ant 1.8.0
      */
     public void addText(String msg) {
         if (!valueAttributeUsed) {
             msg = getProject().replaceProperties(msg);
             String currentValue = getValue();
             if (currentValue != null) {
                 msg = currentValue + msg;
             }
             internalSetValue(msg);
         } else if (msg.trim().length() > 0) {
             throw new BuildException("can't combine nested text with value"
                                      + " attribute");
         }
     }
 
     /**
      * Get the property value.
      * @return the property value
      */
     public String getValue() {
         return value;
     }
 
     /**
      * Filename of a property file to load.
      * @param file filename
      *
      * @ant.attribute group="noname"
      */
     public void setFile(File file) {
         this.file = file;
     }
 
     /**
      * Get the file attribute.
      * @return the file attribute
      */
     public File getFile() {
         return file;
     }
 
     /**
      * The url from which to load properties.
      * @param url url string
      *
      * @ant.attribute group="noname"
      */
     public void setUrl(URL url) {
         this.url = url;
     }
 
     /**
      * Get the url attribute.
      * @return the url attribute
      */
     public URL getUrl() {
         return url;
     }
 
     /**
      * Prefix to apply to properties loaded using <code>file</code>
      * or <code>resource</code>.
      * A "." is appended to the prefix if not specified.
      * @param prefix prefix string
      * @since Ant 1.5
      */
     public void setPrefix(String prefix) {
         this.prefix = prefix;
         if (prefix != null && !prefix.endsWith(".")) {
             this.prefix += ".";
         }
     }
 
     /**
      * Get the prefix attribute.
      * @return the prefix attribute
      * @since Ant 1.5
      */
     public String getPrefix() {
         return prefix;
     }
 
     /**
      * Whether to apply the prefix when expanding properties on the
      * right hand side of a properties file as well.
      *
      * @since Ant 1.8.2
      */
     public void setPrefixValues(boolean b) {
         prefixValues = b;
     }
 
     /**
      * Whether to apply the prefix when expanding properties on the
      * right hand side of a properties file as well.
      *
      * @since Ant 1.8.2
      */
     public boolean getPrefixValues() {
         return prefixValues;
     }
 
     /**
      * Sets a reference to an Ant datatype
      * declared elsewhere.
      * Only yields reasonable results for references
      * PATH like structures or properties.
      * @param ref reference
      *
      * @ant.attribute group="name"
      */
     public void setRefid(Reference ref) {
         this.ref = ref;
     }
 
     /**
      * Get the refid attribute.
      * @return the refid attribute
      */
     public Reference getRefid() {
         return ref;
     }
 
     /**
      * The resource name of a property file to load
      * @param resource resource on classpath
      *
      * @ant.attribute group="noname"
      */
     public void setResource(String resource) {
         this.resource = resource;
     }
 
     /**
      * Get the resource attribute.
      * @return the resource attribute
      */
     public String getResource() {
         return resource;
     }
 
     /**
      * Prefix to use when retrieving environment variables.
      * Thus if you specify environment=&quot;myenv&quot;
      * you will be able to access OS-specific
      * environment variables via property names &quot;myenv.PATH&quot; or
      * &quot;myenv.TERM&quot;.
      * <p>
      * Note that if you supply a property name with a final
      * &quot;.&quot; it will not be doubled. ie environment=&quot;myenv.&quot; will still
      * allow access of environment variables through &quot;myenv.PATH&quot; and
      * &quot;myenv.TERM&quot;. This functionality is currently only implemented
      * on select platforms. Feel free to send patches to increase the number of platforms
      * this functionality is supported on ;).<br>
      * Note also that properties are case sensitive, even if the
      * environment variables on your operating system are not, e.g. it
      * will be ${env.Path} not ${env.PATH} on Windows 2000.
      * @param env prefix
      *
      * @ant.attribute group="noname"
      */
     public void setEnvironment(String env) {
         this.env = env;
     }
 
     /**
      * Get the environment attribute.
      * @return the environment attribute
      * @since Ant 1.5
      */
     public String getEnvironment() {
         return env;
     }
 
     /**
      * The classpath to use when looking up a resource.
      * @param classpath to add to any existing classpath
      */
     public void setClasspath(Path classpath) {
         if (this.classpath == null) {
             this.classpath = classpath;
         } else {
             this.classpath.append(classpath);
         }
     }
 
     /**
      * The classpath to use when looking up a resource.
      * @return a path to be configured
      */
     public Path createClasspath() {
         if (this.classpath == null) {
             this.classpath = new Path(getProject());
         }
         return this.classpath.createPath();
     }
 
     /**
      * the classpath to use when looking up a resource,
      * given as reference to a &lt;path&gt; defined elsewhere
      * @param r a reference to a classpath
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * Get the classpath used when looking up a resource.
      * @return the classpath
      * @since Ant 1.5
      */
     public Path getClasspath() {
         return classpath;
     }
 
     /**
      * @param userProperty ignored
      * @deprecated since 1.5.x.
      *             This was never a supported feature and has been
      *             deprecated without replacement.
      * @ant.attribute ignore="true"
      */
     public void setUserProperty(boolean userProperty) {
         log("DEPRECATED: Ignoring request to set user property in Property"
             + " task.", Project.MSG_WARN);
     }
 
     /**
      * get the value of this property
      * @return the current value or the empty string
      */
     public String toString() {
         return value == null ? "" : value;
     }
 
     /**
      * set the property in the project to the value.
      * if the task was give a file, resource or env attribute
      * here is where it is loaded
      * @throws BuildException on error
      */
     public void execute() throws BuildException {
         if (getProject() == null) {
             throw new IllegalStateException("project has not been set");
         }
 
         if (name != null) {
             if (untypedValue == null && ref == null) {
                 throw new BuildException("You must specify value, location or "
                                          + "refid with the name attribute",
                                          getLocation());
             }
         } else {
             if (url == null && file == null && resource == null && env == null) {
                 throw new BuildException("You must specify url, file, resource or "
                                          + "environment when not using the "
                                          + "name attribute", getLocation());
             }
         }
 
         if (url == null && file == null && resource == null && prefix != null) {
             throw new BuildException("Prefix is only valid when loading from "
                                      + "a url, file or resource", getLocation());
         }
 
         if (name != null && untypedValue != null) {
             if (relative) {
                 try {
                     File from = untypedValue instanceof File ? (File)untypedValue : new File(untypedValue.toString());
                     File to = basedir != null ? basedir : getProject().getBaseDir();
                     String relPath = FileUtils.getRelativePath(to, from);
                     relPath = relPath.replace('/', File.separatorChar);
                     addProperty(name, relPath);
                 } catch (Exception e) {
                     throw new BuildException(e, getLocation());
                 }
             } else {
                 addProperty(name, untypedValue);
             }
         }
 
         if (file != null) {
             loadFile(file);
         }
 
         if (url != null) {
             loadUrl(url);
         }
 
         if (resource != null) {
             loadResource(resource);
         }
 
         if (env != null) {
             loadEnvironment(env);
         }
 
         if ((name != null) && (ref != null)) {
             try {
                 addProperty(name,
                             ref.getReferencedObject(getProject()).toString());
             } catch (BuildException be) {
                 if (fallback != null) {
                     addProperty(name,
                                 ref.getReferencedObject(fallback).toString());
                 } else {
                     throw be;
                 }
             }
         }
     }
     
     /**
      * load properties from a url
      * @param url url to load from
      * @throws BuildException on error
      */
     protected void loadUrl(URL url) throws BuildException {
         Properties props = new Properties();
         log("Loading " + url, Project.MSG_VERBOSE);
         try {
             InputStream is = url.openStream();
             try {
                 loadProperties(props, is, url.getFile().endsWith(".xml"));
             } finally {
                 if (is != null) {
                     is.close();
                 }
             }
             addProperties(props);
         } catch (IOException ex) {
             throw new BuildException(ex, getLocation());
         }
     }
 
     /**
      * Loads the properties defined in the InputStream into the given
      * property. On Java5+ it supports reading from XML based property
      * definition.
      * @param props The property object to load into
      * @param is    The input stream from where to load
      * @param isXml <tt>true</tt> if we should try to load from xml
      * @throws IOException if something goes wrong
      * @since 1.7.1
      * @see http://java.sun.com/dtd/properties.dtd
      * @see java.util.Properties#loadFromXML(InputStream)
      */
     private void loadProperties(
                                 Properties props, InputStream is, boolean isXml) throws IOException {
         if (isXml) {
             // load the xml based property definition
             // use reflection because of bwc to Java 1.4
             try {
                 Method loadXmlMethod = props.getClass().getMethod("loadFromXML",
                                                                   new Class[] {InputStream.class});
                 loadXmlMethod.invoke(props, new Object[] {is});
             } catch (NoSuchMethodException e) {
                 e.printStackTrace();
                 log("Can not load xml based property definition on Java < 5");
             } catch (Exception e) {
                 // no-op
                 e.printStackTrace();
             }
         } else {
             // load ".properties" format
             props.load(is);
         }
     }
 
     /**
      * load properties from a file
      * @param file file to load
      * @throws BuildException on error
      */
     protected void loadFile(File file) throws BuildException {
         Properties props = new Properties();
         log("Loading " + file.getAbsolutePath(), Project.MSG_VERBOSE);
         try {
             if (file.exists()) {
                 FileInputStream  fis = null;
                 try {
                     fis = new FileInputStream(file);
                     loadProperties(props, fis, file.getName().endsWith(".xml"));
                 } finally {
                     FileUtils.close(fis);
                 }
                 addProperties(props);
             } else {
                 log("Unable to find property file: " + file.getAbsolutePath(),
                     Project.MSG_VERBOSE);
             }
         } catch (IOException ex) {
             throw new BuildException(ex, getLocation());
         }
     }
 
     /**
      * load properties from a resource in the current classpath
      * @param name name of resource to load
      */
     protected void loadResource(String name) {
         Properties props = new Properties();
         log("Resource Loading " + name, Project.MSG_VERBOSE);
         InputStream is = null;
         ClassLoader cL = null;
         boolean cleanup = false;
         try {
             if (classpath != null) {
                 cleanup = true;
                 cL = getProject().createClassLoader(classpath);
             } else {
                 cL = this.getClass().getClassLoader();
             }
 
             if (cL == null) {
                 is = ClassLoader.getSystemResourceAsStream(name);
             } else {
                 is = cL.getResourceAsStream(name);
             }
 
             if (is != null) {
                 loadProperties(props, is, name.endsWith(".xml"));
                 addProperties(props);
             } else {
                 log("Unable to find resource " + name, Project.MSG_WARN);
             }
         } catch (IOException ex) {
             throw new BuildException(ex, getLocation());
         } finally {
             if (is != null) {
                 try {
                     is.close();
                 } catch (IOException e) {
                     // ignore
                 }
             }
             if (cleanup && cL != null) {
                 ((AntClassLoader) cL).cleanup();
             }
         }
     }
 
     /**
      * load the environment values
      * @param prefix prefix to place before them
      */
     protected void loadEnvironment(String prefix) {
         Properties props = new Properties();
         if (!prefix.endsWith(".")) {
             prefix += ".";
         }
         log("Loading Environment " + prefix, Project.MSG_VERBOSE);
-        Vector osEnv = Execute.getProcEnvironment();
-        for (Enumeration e = osEnv.elements(); e.hasMoreElements();) {
-            String entry = (String) e.nextElement();
-            int pos = entry.indexOf('=');
-            if (pos == -1) {
-                log("Ignoring: " + entry, Project.MSG_WARN);
-            } else {
-                props.put(prefix + entry.substring(0, pos),
-                          entry.substring(pos + 1));
-            }
+        Map osEnv = Execute.getEnvironmentVariables();
+        for (Iterator e = osEnv.entrySet().iterator(); e.hasNext(); ) {
+            Map.Entry entry = (Map.Entry) e.next();
+            props.put(prefix + entry.getKey(), entry.getValue());
         }
         addProperties(props);
     }
 
     /**
      * iterate through a set of properties,
      * resolve them then assign them
      * @param props the properties to iterate over
      */
     protected void addProperties(Properties props) {
         HashMap m = new HashMap(props);
         resolveAllProperties(m);
         for (Iterator it = m.keySet().iterator(); it.hasNext();) {
             Object k = it.next();
             if (k instanceof String) {
                 String propertyName = (String) k;
                 if (prefix != null) {
                     propertyName = prefix + propertyName;
                 }
                 addProperty(propertyName, m.get(k));
             }
         }
     }
 
     /**
      * add a name value pair to the project property set
      * @param n name of property
      * @param v value to set
      */
     protected void addProperty(String n, String v) {
         addProperty(n, (Object) v);
     }
 
     /**
      * add a name value pair to the project property set
      * @param n name of property
      * @param v value to set
      * @since Ant 1.8
      */
     protected void addProperty(String n, Object v) {
         PropertyHelper ph = PropertyHelper.getPropertyHelper(getProject());
         if (userProperty) {
             if (ph.getUserProperty(n) == null) {
                 ph.setInheritedProperty(n, v);
             } else {
                 log("Override ignored for " + n, Project.MSG_VERBOSE);
             }
         } else {
             ph.setNewProperty(n, v);
         }
     }
 
     /**
      * resolve properties inside a properties hashtable
      * @param props properties object to resolve
      */
     private void resolveAllProperties(Map props) throws BuildException {
         PropertyHelper propertyHelper
             = (PropertyHelper) PropertyHelper.getPropertyHelper(getProject());
         new ResolvePropertyMap(
                                getProject(),
                                propertyHelper,
                                propertyHelper.getExpanders())
             .resolveAllProperties(props, getPrefix(), getPrefixValues());
     }
 
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/Rpm.java b/src/main/org/apache/tools/ant/taskdefs/optional/Rpm.java
index d0d3d1cf0..1c6064e02 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/Rpm.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/Rpm.java
@@ -1,366 +1,365 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 package org.apache.tools.ant.taskdefs.optional;
 
 import java.io.BufferedOutputStream;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.util.Enumeration;
+import java.util.Map;
 import java.util.Vector;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
 import org.apache.tools.ant.taskdefs.LogOutputStream;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.taskdefs.PumpStreamHandler;
 import org.apache.tools.ant.taskdefs.condition.Os;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.Path;
 
 /**
  * Invokes the rpm tool to build a Linux installation file.
  *
  */
 public class Rpm extends Task {
 
-    private static final String PATH1 = "PATH=";
-    private static final String PATH2 = "Path=";
-    private static final String PATH3 = "path=";
-    private static final int PATH_LEN = PATH1.length();
+    private static final String PATH1 = "PATH";
+    private static final String PATH2 = "Path";
+    private static final String PATH3 = "path";
 
     /**
      * the spec file
      */
     private String specFile;
 
     /**
      * the rpm top dir
      */
     private File topDir;
 
     /**
      * the rpm command to use
      */
     private String command = "-bb";
 
     /**
      * The executable to use for building the packages.
      * @since Ant 1.6
      */
     private String rpmBuildCommand = null;
 
     /**
      * clean BUILD directory
      */
     private boolean cleanBuildDir = false;
 
     /**
      * remove spec file
      */
     private boolean removeSpec = false;
 
     /**
      * remove sources
      */
     private boolean removeSource = false;
 
     /**
      * the file to direct standard output from the command
      */
     private File output;
 
     /**
      * the file to direct standard error from the command
      */
     private File error;
 
     /**
      * Halt on error return value from rpm build.
      */
     private boolean failOnError = false;
 
     /**
      * Don't show output of RPM build command on console. This does not affect
      * the printing of output and error messages to files.
      */
     private boolean quiet = false;
 
     /**
      * Execute the task
      *
      * @throws BuildException is there is a problem in the task execution.
      */
     public void execute() throws BuildException {
 
         Commandline toExecute = new Commandline();
 
         toExecute.setExecutable(rpmBuildCommand == null
                                 ? guessRpmBuildCommand()
                                 : rpmBuildCommand);
         if (topDir != null) {
             toExecute.createArgument().setValue("--define");
             toExecute.createArgument().setValue("_topdir " + topDir);
         }
 
         toExecute.createArgument().setLine(command);
 
         if (cleanBuildDir) {
             toExecute.createArgument().setValue("--clean");
         }
         if (removeSpec) {
             toExecute.createArgument().setValue("--rmspec");
         }
         if (removeSource) {
             toExecute.createArgument().setValue("--rmsource");
         }
 
         toExecute.createArgument().setValue("SPECS/" + specFile);
 
         ExecuteStreamHandler streamhandler = null;
         OutputStream outputstream = null;
         OutputStream errorstream = null;
         if (error == null && output == null) {
             if (!quiet) {
                 streamhandler = new LogStreamHandler(this, Project.MSG_INFO,
                                                      Project.MSG_WARN);
             } else {
                 streamhandler = new LogStreamHandler(this, Project.MSG_DEBUG,
                                                      Project.MSG_DEBUG);
             }
         } else {
             if (output != null) {
                 try {
                     BufferedOutputStream bos
                         = new BufferedOutputStream(new FileOutputStream(output));
                     outputstream = new PrintStream(bos);
                 } catch (IOException e) {
                     throw new BuildException(e, getLocation());
                 }
             } else if (!quiet) {
                 outputstream = new LogOutputStream(this, Project.MSG_INFO);
             } else {
                 outputstream = new LogOutputStream(this, Project.MSG_DEBUG);
             }
             if (error != null) {
                 try {
                     BufferedOutputStream bos
                         = new BufferedOutputStream(new FileOutputStream(error));
                     errorstream = new PrintStream(bos);
                 }  catch (IOException e) {
                     throw new BuildException(e, getLocation());
                 }
             } else if (!quiet) {
                 errorstream = new LogOutputStream(this, Project.MSG_WARN);
             } else {
                 errorstream = new LogOutputStream(this, Project.MSG_DEBUG);
             }
             streamhandler = new PumpStreamHandler(outputstream, errorstream);
         }
 
         Execute exe = getExecute(toExecute, streamhandler);
         try {
             log("Building the RPM based on the " + specFile + " file");
             int returncode = exe.execute();
             if (Execute.isFailure(returncode)) {
                 String msg = "'" + toExecute.getExecutable()
                     + "' failed with exit code " + returncode;
                 if (failOnError) {
                     throw new BuildException(msg);
                 }
                 log(msg, Project.MSG_ERR);
             }
         } catch (IOException e) {
             throw new BuildException(e, getLocation());
         } finally {
             FileUtils.close(outputstream);
             FileUtils.close(errorstream);
         }
     }
 
     /**
      * The directory which will have the expected
      * subdirectories, SPECS, SOURCES, BUILD, SRPMS ; optional.
      * If this isn't specified,
      * the <tt>baseDir</tt> value is used
      *
      * @param td the directory containing the normal RPM directories.
      */
     public void setTopDir(File td) {
         this.topDir = td;
     }
 
     /**
      * What command to issue to the rpm build tool; optional.
      * The default is "-bb"
      * @param c the command to use.
      */
     public void setCommand(String c) {
         this.command = c;
     }
 
     /**
      * The name of the spec File to use; required.
      * @param sf the spec file name to use.
      */
     public void setSpecFile(String sf) {
         if ((sf == null) || (sf.trim().length() == 0)) {
             throw new BuildException("You must specify a spec file", getLocation());
         }
         this.specFile = sf;
     }
 
     /**
      * Flag (optional, default=false) to remove
      * the generated files in the BUILD directory
      * @param cbd a <code>boolean</code> value.
      */
     public void setCleanBuildDir(boolean cbd) {
         cleanBuildDir = cbd;
     }
 
     /**
      * Flag (optional, default=false) to remove the spec file from SPECS
      * @param rs a <code>boolean</code> value.
      */
     public void setRemoveSpec(boolean rs) {
         removeSpec = rs;
     }
 
     /**
      * Flag (optional, default=false)
      * to remove the sources after the build.
      * See the <tt>--rmsource</tt>  option of rpmbuild.
      * @param rs a <code>boolean</code> value.
      */
     public void setRemoveSource(boolean rs) {
         removeSource = rs;
     }
 
     /**
      * Optional file to save stdout to.
      * @param output the file to save stdout to.
      */
     public void setOutput(File output) {
         this.output = output;
     }
 
     /**
      * Optional file to save stderr to
      * @param error the file to save error output to.
      */
     public void setError(File error) {
         this.error = error;
     }
 
     /**
      * The executable to run when building; optional.
      * The default is <code>rpmbuild</code>.
      *
      * @since Ant 1.6
      * @param c the rpm build executable
      */
     public void setRpmBuildCommand(String c) {
         this.rpmBuildCommand = c;
     }
 
     /**
      * If <code>true</code>, stop the build process when the rpmbuild command
      * exits with an error status.
      * @param value <code>true</code> if it should halt, otherwise
      * <code>false</code>. The default is <code>false</code>.
      *
      * @since Ant 1.6.3
      */
     public void setFailOnError(boolean value) {
         failOnError = value;
     }
 
     /**
      * If true, output from the RPM build command will only be logged to DEBUG.
      * @param value <code>false</code> if output should be logged, otherwise
      * <code>true</code>. The default is <code>false</code>.
      *
      * @since Ant 1.6.3
      */
     public void setQuiet(boolean value) {
         quiet = value;
     }
 
     /**
      * Checks whether <code>rpmbuild</code> is on the PATH and returns
      * the absolute path to it - falls back to <code>rpm</code>
      * otherwise.
      *
      * @return the command used to build RPM's
      *
      * @since 1.6
      */
     protected String guessRpmBuildCommand() {
-        Vector env = Execute.getProcEnvironment();
-        String path = null;
-        for (Enumeration e = env.elements(); e.hasMoreElements();) {
-            String var = (String) e.nextElement();
-            if (var.startsWith(PATH1) || var.startsWith(PATH2) || var.startsWith(PATH3)) {
-                path = var.substring(PATH_LEN);
-                break;
+        Map/*<String, String>*/ env = Execute.getEnvironmentVariables();
+        String path = (String) env.get(PATH1);
+        if (path == null) {
+            path = (String) env.get(PATH2);
+            if (path == null) {
+                path = (String) env.get(PATH3);
             }
         }
 
         if (path != null) {
             Path p = new Path(getProject(), path);
             String[] pElements = p.list();
             for (int i = 0; i < pElements.length; i++) {
                 File f = new File(pElements[i],
                                   "rpmbuild"
                                   + (Os.isFamily("dos") ? ".exe" : ""));
                 if (f.canRead()) {
                     return f.getAbsolutePath();
                 }
             }
         }
 
         return "rpm";
     }
 
     /**
      * Get the execute object.
      * @param toExecute the command line to use.
      * @param streamhandler the stream handler to use.
      * @return the execute object.
      * @since Ant 1.6.3
      */
     protected Execute getExecute(Commandline toExecute,
                                  ExecuteStreamHandler streamhandler) {
         Execute exe = new Execute(streamhandler, null);
 
         exe.setAntRun(getProject());
         if (topDir == null) {
             topDir = getProject().getBaseDir();
         }
         exe.setWorkingDirectory(topDir);
 
         exe.setCommandline(toExecute.getCommandline());
         return exe;
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/jdepend/JDependTask.java b/src/main/org/apache/tools/ant/taskdefs/optional/jdepend/JDependTask.java
index a5c3b27ef..d790792ba 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/jdepend/JDependTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/jdepend/JDependTask.java
@@ -1,688 +1,683 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant.taskdefs.optional.jdepend;
 
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.PrintWriter;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Method;
+import java.util.Map;
 import java.util.Vector;
-import java.util.Enumeration;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
 import org.apache.tools.ant.taskdefs.LogStreamHandler;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.CommandlineJava;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.PatternSet;
 import org.apache.tools.ant.types.Reference;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.LoaderUtils;
 
 /**
  * Runs JDepend tests.
  *
  * <p>JDepend is a tool to generate design quality metrics for each Java package.
  * It has been initially created by Mike Clark. JDepend can be found at <a
  * href="http://www.clarkware.com/software/JDepend.html">http://www.clarkware.com/software/JDepend.html</a>.
  *
  * The current implementation spawn a new Java VM.
  *
  */
 public class JDependTask extends Task {
     //private CommandlineJava commandline = new CommandlineJava();
 
     // required attributes
     private Path sourcesPath; // Deprecated!
     private Path classesPath; // Use this going forward
 
     // optional attributes
     private File outputFile;
     private File dir;
     private Path compileClasspath;
     private boolean haltonerror = false;
     private boolean fork = false;
     private Long timeout = null;
 
     private String jvm = null;
     private String format = "text";
     private PatternSet defaultPatterns = new PatternSet();
 
     private static Constructor packageFilterC;
     private static Method setFilter;
 
     private boolean includeRuntime = false;
     private Path runtimeClasses = null;
 
     static {
         try {
             Class packageFilter =
                 Class.forName("jdepend.framework.PackageFilter");
             packageFilterC =
                 packageFilter.getConstructor(new Class[] {java.util.Collection.class});
             setFilter =
                 jdepend.textui.JDepend.class.getDeclaredMethod("setFilter",
                                                                new Class[] {packageFilter});
         } catch (Throwable t) {
             if (setFilter == null) {
                 packageFilterC = null;
             }
         }
     }
 
     /**
      * If true,
      *  include jdepend.jar in the forked VM.
      *
      * @param b include ant run time yes or no
      * @since Ant 1.6
      */
     public void setIncluderuntime(boolean b) {
         includeRuntime = b;
     }
 
     /**
      * Set the timeout value (in milliseconds).
      *
      * <p>If the operation is running for more than this value, the jdepend
      * will be canceled. (works only when in 'fork' mode).</p>
      * @param value the maximum time (in milliseconds) allowed before
      * declaring the test as 'timed-out'
      * @see #setFork(boolean)
      */
     public void setTimeout(Long value) {
         timeout = value;
     }
 
     /**
      * @return the timeout value
      */
     public Long getTimeout() {
         return timeout;
     }
 
     /**
      * The output file name.
      *
      * @param outputFile the output file name
      */
     public void setOutputFile(File outputFile) {
         this.outputFile = outputFile;
     }
 
     /**
      * @return the output file name
      */
     public File getOutputFile() {
         return outputFile;
     }
 
     /**
      * Whether or not to halt on failure. Default: false.
      * @param haltonerror the value to set
      */
     public void setHaltonerror(boolean haltonerror) {
         this.haltonerror = haltonerror;
     }
 
     /**
      * @return the value of the haltonerror attribute
      */
     public boolean getHaltonerror() {
         return haltonerror;
     }
 
     /**
      * If true, forks into a new JVM. Default: false.
      *
      * @param   value   <tt>true</tt> if a JVM should be forked,
      *                  otherwise <tt>false<tt>
      */
     public void setFork(boolean value) {
         fork = value;
     }
 
     /**
      * @return the value of the fork attribute
      */
     public boolean getFork() {
         return fork;
     }
 
     /**
      * The command used to invoke a forked Java Virtual Machine.
      *
      * Default is <tt>java</tt>. Ignored if no JVM is forked.
      * @param   value   the new VM to use instead of <tt>java</tt>
      * @see #setFork(boolean)
      */
     public void setJvm(String value) {
         jvm = value;
 
     }
 
     /**
      * Adds a path to source code to analyze.
      * @return a source path
      * @deprecated since 1.6.x.
      */
     public Path createSourcespath() {
         if (sourcesPath == null) {
             sourcesPath = new Path(getProject());
         }
         return sourcesPath.createPath();
     }
 
     /**
      * Gets the sourcepath.
      * @return the sources path
      * @deprecated since 1.6.x.
      */
     public Path getSourcespath() {
         return sourcesPath;
     }
 
     /**
      * Adds a path to class code to analyze.
      * @return a classes path
      */
     public Path createClassespath() {
         if (classesPath == null) {
             classesPath = new Path(getProject());
         }
         return classesPath.createPath();
     }
 
     /**
      * Gets the classespath.
      * @return the classes path
      */
     public Path getClassespath() {
         return classesPath;
     }
 
     /**
      * The directory to invoke the VM in. Ignored if no JVM is forked.
      * @param   dir     the directory to invoke the JVM from.
      * @see #setFork(boolean)
      */
     public void setDir(File dir) {
         this.dir = dir;
     }
 
     /**
      * @return the dir attribute
      */
     public File getDir() {
         return dir;
     }
 
     /**
      * Set the classpath to be used for this compilation.
      * @param classpath a class path to be used
      */
     public void setClasspath(Path classpath) {
         if (compileClasspath == null) {
             compileClasspath = classpath;
         } else {
             compileClasspath.append(classpath);
         }
     }
 
     /**
      * Gets the classpath to be used for this compilation.
      * @return the class path used for compilation
      */
     public Path getClasspath() {
         return compileClasspath;
     }
 
     /**
      * Adds a path to the classpath.
      * @return a classpath
      */
     public Path createClasspath() {
         if (compileClasspath == null) {
             compileClasspath = new Path(getProject());
         }
         return compileClasspath.createPath();
     }
 
     /**
      * Create a new JVM argument. Ignored if no JVM is forked.
      * @param commandline the commandline to create the argument on
      * @return  create a new JVM argument so that any argument can
      *          be passed to the JVM.
      * @see #setFork(boolean)
      */
     public Commandline.Argument createJvmarg(CommandlineJava commandline) {
         return commandline.createVmArgument();
     }
 
     /**
      * Adds a reference to a classpath defined elsewhere.
      * @param r a classpath reference
      */
     public void setClasspathRef(Reference r) {
         createClasspath().setRefid(r);
     }
 
     /**
      * add a name entry on the exclude list
      * @return a pattern for the excludes
      */
     public PatternSet.NameEntry createExclude() {
         return defaultPatterns.createExclude();
     }
 
     /**
      * @return the excludes patterns
      */
     public PatternSet getExcludes() {
         return defaultPatterns;
     }
 
     /**
      * The format to write the output in, "xml" or "text".
      *
      * @param ea xml or text
      */
     public void setFormat(FormatAttribute ea) {
         format = ea.getValue();
     }
 
     /**
      * A class for the enumerated attribute format,
      * values are xml and text.
      * @see EnumeratedAttribute
      */
     public static class FormatAttribute extends EnumeratedAttribute {
         private String [] formats = new String[]{"xml", "text"};
 
         /**
          * @return the enumerated values
          */
         public String[] getValues() {
             return formats;
         }
     }
 
     /**
      * No problems with this test.
      */
     private static final int SUCCESS = 0;
     /**
      * An error occurred.
      */
     private static final int ERRORS = 1;
 
     /**
      * Search for the given resource and add the directory or archive
      * that contains it to the classpath.
      *
      * <p>Doesn't work for archives in JDK 1.1 as the URL returned by
      * getResource doesn't contain the name of the archive.</p>
      *
      * @param resource resource that one wants to lookup
      * @since Ant 1.6
      */
     private void addClasspathEntry(String resource) {
         /*
          * pre Ant 1.6 this method used to call getClass().getResource
          * while Ant 1.6 will call ClassLoader.getResource().
          *
          * The difference is that Class.getResource expects a leading
          * slash for "absolute" resources and will strip it before
          * delegating to ClassLoader.getResource - so we now have to
          * emulate Class's behavior.
          */
         if (resource.startsWith("/")) {
             resource = resource.substring(1);
         } else {
             resource = "org/apache/tools/ant/taskdefs/optional/jdepend/"
                 + resource;
         }
 
         File f = LoaderUtils.getResourceSource(getClass().getClassLoader(),
                                                resource);
         if (f != null) {
             log("Found " + f.getAbsolutePath(), Project.MSG_DEBUG);
             runtimeClasses.createPath().setLocation(f);
         } else {
             log("Couldn\'t find " + resource, Project.MSG_DEBUG);
         }
     }
 
     /**
      * execute the task
      *
      * @exception BuildException if an error occurs
      */
     public void execute() throws BuildException {
 
         CommandlineJava commandline = new CommandlineJava();
 
         if ("text".equals(format)) {
             commandline.setClassname("jdepend.textui.JDepend");
         } else
             if ("xml".equals(format)) {
                 commandline.setClassname("jdepend.xmlui.JDepend");
             }
 
         if (jvm != null) {
             commandline.setVm(jvm);
         }
         if (getSourcespath() == null && getClassespath() == null) {
             throw new BuildException("Missing classespath required argument");
         } else if (getClassespath() == null) {
             String msg =
                 "sourcespath is deprecated in JDepend >= 2.5 "
                 + "- please convert to classespath";
             log(msg);
         }
 
         // execute the test and get the return code
         int exitValue = JDependTask.ERRORS;
         boolean wasKilled = false;
         if (!getFork()) {
             exitValue = executeInVM(commandline);
         } else {
             ExecuteWatchdog watchdog = createWatchdog();
             exitValue = executeAsForked(commandline, watchdog);
             // null watchdog means no timeout, you'd better not check with null
             if (watchdog != null) {
                 wasKilled = watchdog.killedProcess();
             }
         }
 
         // if there is an error/failure and that it should halt, stop
         // everything otherwise just log a statement
         boolean errorOccurred = exitValue == JDependTask.ERRORS || wasKilled;
 
         if (errorOccurred) {
             String errorMessage = "JDepend FAILED"
                 + (wasKilled ? " - Timed out" : "");
 
             if  (getHaltonerror()) {
                 throw new BuildException(errorMessage, getLocation());
             } else {
                 log(errorMessage, Project.MSG_ERR);
             }
         }
     }
 
     // this comment extract from JUnit Task may also apply here
     // "in VM is not very nice since it could probably hang the
     // whole build. IMHO this method should be avoided and it would be best
     // to remove it in future versions. TBD. (SBa)"
 
     /**
      * Execute inside VM.
      *
      * @param commandline the command line
      * @return the return value of the mvm
      * @exception BuildException if an error occurs
      */
     public int executeInVM(CommandlineJava commandline) throws BuildException {
         jdepend.textui.JDepend jdepend;
 
         if ("xml".equals(format)) {
             jdepend = new jdepend.xmlui.JDepend();
         } else {
             jdepend = new jdepend.textui.JDepend();
         }
 
         FileWriter fw = null;
         PrintWriter pw = null;
         if (getOutputFile() != null) {
             try {
                 fw = new FileWriter(getOutputFile().getPath());
             } catch (IOException e) {
                 String msg = "JDepend Failed when creating the output file: "
                     + e.getMessage();
                 log(msg);
                 throw new BuildException(msg);
             }
             pw = new PrintWriter(fw);
             jdepend.setWriter(pw);
             log("Output to be stored in " + getOutputFile().getPath());
         }
 
 
         try {
             if (getClassespath() != null) {
                 // This is the new, better way - use classespath instead
                 // of sourcespath.  The code is currently the same - you
                 // need class files in a directory to use this or jar files.
                 String[] cP = getClassespath().list();
                 for (int i = 0; i < cP.length; i++) {
                     File f = new File(cP[i]);
                     // not necessary as JDepend would fail, but why loose
                     // some time?
                     if (!f.exists()) {
                         String msg = "\""
                             + f.getPath()
                             + "\" does not represent a valid"
                             + " file or directory. JDepend would fail.";
                         log(msg);
                         throw new BuildException(msg);
                     }
                     try {
                         jdepend.addDirectory(f.getPath());
                     } catch (IOException e) {
                         String msg =
                             "JDepend Failed when adding a class directory: "
                             + e.getMessage();
                         log(msg);
                         throw new BuildException(msg);
                     }
                 }
 
             } else if (getSourcespath() != null) {
 
                 // This is the old way and is deprecated - classespath is
                 // the right way to do this and is above
                 String[] sP = getSourcespath().list();
                 for (int i = 0; i < sP.length; i++) {
                     File f = new File(sP[i]);
 
                     // not necessary as JDepend would fail, but why loose
                     // some time?
                     if (!f.exists() || !f.isDirectory()) {
                         String msg = "\""
                             + f.getPath()
                             + "\" does not represent a valid"
                             + " directory. JDepend would fail.";
                         log(msg);
                         throw new BuildException(msg);
                     }
                     try {
                         jdepend.addDirectory(f.getPath());
                     } catch (IOException e) {
                         String msg =
                             "JDepend Failed when adding a source directory: "
                             + e.getMessage();
                         log(msg);
                         throw new BuildException(msg);
                     }
                 }
             }
 
             // This bit turns <exclude> child tags into patters to ignore
             String[] patterns = defaultPatterns.getExcludePatterns(getProject());
             if (patterns != null && patterns.length > 0) {
                 if (setFilter != null) {
                     Vector v = new Vector();
                     for (int i = 0; i < patterns.length; i++) {
                         v.addElement(patterns[i]);
                     }
                     try {
                         Object o = packageFilterC.newInstance(new Object[] {v});
                         setFilter.invoke(jdepend, new Object[] {o});
                     } catch (Throwable e) {
                         log("excludes will be ignored as JDepend doesn't like me: "
                             + e.getMessage(), Project.MSG_WARN);
                     }
                 } else {
                     log("Sorry, your version of JDepend doesn't support excludes",
                         Project.MSG_WARN);
                 }
             }
 
             jdepend.analyze();
             if (pw.checkError()) {
                 throw new IOException("Encountered an error writing JDepend"
                                       + " output");
             }
         } catch (IOException ex) {
             throw new BuildException(ex);
         } finally {
             FileUtils.close(pw);
             FileUtils.close(fw);
         }
         return SUCCESS;
     }
 
 
     /**
      * Execute the task by forking a new JVM. The command will block until
      * it finishes. To know if the process was destroyed or not, use the
      * <tt>killedProcess()</tt> method of the watchdog class.
      * @param commandline the commandline for forked jvm
      * @param  watchdog   the watchdog in charge of cancelling the test if it
      * exceeds a certain amount of time. Can be <tt>null</tt>.
      * @return the result of running the jdepend
      * @throws BuildException in case of error
      */
     // JL: comment extracted from JUnitTask (and slightly modified)
     public int executeAsForked(CommandlineJava commandline,
                                ExecuteWatchdog watchdog) throws BuildException {
         runtimeClasses = new Path(getProject());
         addClasspathEntry("/jdepend/textui/JDepend.class");
 
         // if not set, auto-create the ClassPath from the project
         createClasspath();
 
         // not sure whether this test is needed but cost nothing to put.
         // hope it will be reviewed by anybody competent
         if (getClasspath().toString().length() > 0) {
             createJvmarg(commandline).setValue("-classpath");
             createJvmarg(commandline).setValue(getClasspath().toString());
         }
 
         if (includeRuntime) {
-            Vector v = Execute.getProcEnvironment();
-            Enumeration e = v.elements();
-            while (e.hasMoreElements()) {
-                String s = (String) e.nextElement();
-                if (s.startsWith("CLASSPATH=")) {
-                    commandline.createClasspath(getProject()).createPath()
-                        .append(new Path(getProject(),
-                                         s.substring("CLASSPATH=".length()
-                                                     )));
-                }
+            Map/*<String, String>*/ env = Execute.getEnvironmentVariables();
+            String cp = (String) env.get("CLASSPATH");
+            if (cp != null) {
+                commandline.createClasspath(getProject()).createPath()
+                    .append(new Path(getProject(), cp));
             }
             log("Implicitly adding " + runtimeClasses + " to CLASSPATH",
                 Project.MSG_VERBOSE);
             commandline.createClasspath(getProject()).createPath()
                 .append(runtimeClasses);
         }
 
         if (getOutputFile() != null) {
             // having a space between the file and its path causes commandline
             // to add quotes around the argument thus making JDepend not taking
             // it into account. Thus we split it in two
             commandline.createArgument().setValue("-file");
             commandline.createArgument().setValue(outputFile.getPath());
             // we have to find a cleaner way to put this output
         }
 
         if (getSourcespath() != null) {
             // This is deprecated - use classespath in the future
             String[] sP = getSourcespath().list();
             for (int i = 0; i < sP.length; i++) {
                 File f = new File(sP[i]);
 
                 // not necessary as JDepend would fail, but why loose
                 // some time?
                 if (!f.exists() || !f.isDirectory()) {
                     throw new BuildException("\"" + f.getPath()
                                              + "\" does not represent a valid"
                                              + " directory. JDepend would"
                                              + " fail.");
                 }
                 commandline.createArgument().setValue(f.getPath());
             }
         }
 
         if (getClassespath() != null) {
             // This is the new way - use classespath - code is the
             // same for now
             String[] cP = getClassespath().list();
             for (int i = 0; i < cP.length; i++) {
                 File f = new File(cP[i]);
                 // not necessary as JDepend would fail, but why loose
                 // some time?
                 if (!f.exists()) {
                     throw new BuildException("\"" + f.getPath()
                                              + "\" does not represent a valid"
                                              + " file or directory. JDepend would"
                                              + " fail.");
                 }
                 commandline.createArgument().setValue(f.getPath());
             }
         }
 
         Execute execute = new Execute(new LogStreamHandler(this,
             Project.MSG_INFO, Project.MSG_WARN), watchdog);
         execute.setCommandline(commandline.getCommandline());
         if (getDir() != null) {
             execute.setWorkingDirectory(getDir());
             execute.setAntRun(getProject());
         }
 
         if (getOutputFile() != null) {
             log("Output to be stored in " + getOutputFile().getPath());
         }
         log(commandline.describeCommand(), Project.MSG_VERBOSE);
         try {
             return execute.execute();
         } catch (IOException e) {
             throw new BuildException("Process fork failed.", e, getLocation());
         }
     }
 
     /**
      * @return <tt>null</tt> if there is a timeout value, otherwise the
      * watchdog instance.
      * @throws BuildException in case of error
      */
     protected ExecuteWatchdog createWatchdog() throws BuildException {
         if (getTimeout() == null) {
             return null;
         }
         return new ExecuteWatchdog(getTimeout().longValue());
     }
 }
diff --git a/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java b/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
index 01aa0053b..585806564 100644
--- a/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
+++ b/src/main/org/apache/tools/ant/taskdefs/optional/junit/JUnitTask.java
@@ -1,2080 +1,2075 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.ant.taskdefs.optional.junit;
 
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.lang.reflect.Constructor;
 import java.net.URL;
 import java.security.AccessController;
 import java.security.PrivilegedAction;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Vector;
 
 import org.apache.tools.ant.AntClassLoader;
 import org.apache.tools.ant.BuildException;
 import org.apache.tools.ant.Project;
 import org.apache.tools.ant.Task;
 import org.apache.tools.ant.taskdefs.Execute;
 import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
 import org.apache.tools.ant.taskdefs.LogOutputStream;
 import org.apache.tools.ant.taskdefs.PumpStreamHandler;
 import org.apache.tools.ant.types.Assertions;
 import org.apache.tools.ant.types.Commandline;
 import org.apache.tools.ant.types.CommandlineJava;
 import org.apache.tools.ant.types.EnumeratedAttribute;
 import org.apache.tools.ant.types.Environment;
 import org.apache.tools.ant.types.Path;
 import org.apache.tools.ant.types.Permissions;
 import org.apache.tools.ant.types.PropertySet;
 import org.apache.tools.ant.util.FileUtils;
 import org.apache.tools.ant.util.LoaderUtils;
 import org.apache.tools.ant.util.SplitClassLoader;
 
 /**
  * Runs JUnit tests.
  *
  * <p> JUnit is a framework to create unit tests. It has been initially
  * created by Erich Gamma and Kent Beck.  JUnit can be found at <a
  * href="http://www.junit.org">http://www.junit.org</a>.
  *
  * <p> <code>JUnitTask</code> can run a single specific
  * <code>JUnitTest</code> using the <code>test</code> element.</p>
  * For example, the following target <code><pre>
  *   &lt;target name="test-int-chars" depends="jar-test"&gt;
  *       &lt;echo message="testing international characters"/&gt;
  *       &lt;junit printsummary="no" haltonfailure="yes" fork="false"&gt;
  *           &lt;classpath refid="classpath"/&gt;
  *           &lt;formatter type="plain" usefile="false" /&gt;
  *           &lt;test name="org.apache.ecs.InternationalCharTest" /&gt;
  *       &lt;/junit&gt;
  *   &lt;/target&gt;
  * </pre></code>
  * <p>runs a single junit test
  * (<code>org.apache.ecs.InternationalCharTest</code>) in the current
  * VM using the path with id <code>classpath</code> as classpath and
  * presents the results formatted using the standard
  * <code>plain</code> formatter on the command line.</p>
  *
  * <p> This task can also run batches of tests.  The
  * <code>batchtest</code> element creates a <code>BatchTest</code>
  * based on a fileset.  This allows, for example, all classes found in
  * directory to be run as testcases.</p>
  *
  * <p>For example,</p><code><pre>
  * &lt;target name="run-tests" depends="dump-info,compile-tests" if="junit.present"&gt;
  *   &lt;junit printsummary="no" haltonfailure="yes" fork="${junit.fork}"&gt;
  *     &lt;jvmarg value="-classic"/&gt;
  *     &lt;classpath refid="tests-classpath"/&gt;
  *     &lt;sysproperty key="build.tests" value="${build.tests}"/&gt;
  *     &lt;formatter type="brief" usefile="false" /&gt;
  *     &lt;batchtest&gt;
  *       &lt;fileset dir="${tests.dir}"&gt;
  *         &lt;include name="**&#047;*Test*" /&gt;
  *       &lt;/fileset&gt;
  *     &lt;/batchtest&gt;
  *   &lt;/junit&gt;
  * &lt;/target&gt;
  * </pre></code>
  * <p>this target finds any classes with a <code>test</code> directory
  * anywhere in their path (under the top <code>${tests.dir}</code>, of
  * course) and creates <code>JUnitTest</code>'s for each one.</p>
  *
  * <p> Of course, <code>&lt;junit&gt;</code> and
  * <code>&lt;batch&gt;</code> elements can be combined for more
  * complex tests. For an example, see the ant <code>build.xml</code>
  * target <code>run-tests</code> (the second example is an edited
  * version).</p>
  *
  * <p> To spawn a new Java VM to prevent interferences between
  * different testcases, you need to enable <code>fork</code>.  A
  * number of attributes and elements allow you to set up how this JVM
  * runs.
  *
  *
  * @since Ant 1.2
  *
  * @see JUnitTest
  * @see BatchTest
  */
 public class JUnitTask extends Task {
 
     private static final String LINE_SEP
         = System.getProperty("line.separator");
-    private static final String CLASSPATH = "CLASSPATH=";
+    private static final String CLASSPATH = "CLASSPATH";
     private CommandlineJava commandline;
     private Vector tests = new Vector();
     private Vector batchTests = new Vector();
     private Vector formatters = new Vector();
     private File dir = null;
 
     private Integer timeout = null;
     private boolean summary = false;
     private boolean reloading = true;
     private String summaryValue = "";
     private JUnitTaskMirror.JUnitTestRunnerMirror runner = null;
 
     private boolean newEnvironment = false;
     private Environment env = new Environment();
 
     private boolean includeAntRuntime = true;
     private Path antRuntimeClasses = null;
 
     // Do we send output to System.out/.err in addition to the formatters?
     private boolean showOutput = false;
 
     // Do we send output to the formatters ?
     private boolean outputToFormatters = true;
 
     private boolean logFailedTests = true;
 
     private File tmpDir;
     private AntClassLoader classLoader = null;
     private Permissions perm = null;
     private ForkMode forkMode = new ForkMode("perTest");
 
     private boolean splitJunit = false;
     private boolean enableTestListenerEvents = false;
     private JUnitTaskMirror delegate;
     private ClassLoader mirrorLoader;
 
     /** A boolean on whether to get the forked path for ant classes */
     private boolean forkedPathChecked = false;
 
     //   Attributes for basetest
     private boolean haltOnError = false;
     private boolean haltOnFail  = false;
     private boolean filterTrace = true;
     private boolean fork        = false;
     private String  failureProperty;
     private String  errorProperty;
 
     private static final int STRING_BUFFER_SIZE = 128;
     /**
      * @since Ant 1.7
      */
     public static final String TESTLISTENER_PREFIX =
         "junit.framework.TestListener: ";
 
     /**
      * Name of magic property that enables test listener events.
      */
     public static final String ENABLE_TESTLISTENER_EVENTS =
         "ant.junit.enabletestlistenerevents";
 
     private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
 
     /**
      * If true, force ant to re-classload all classes for each JUnit TestCase
      *
      * @param value force class reloading for each test case
      */
     public void setReloading(boolean value) {
         reloading = value;
     }
 
     /**
      * If true, smartly filter the stack frames of
      * JUnit errors and failures before reporting them.
      *
      * <p>This property is applied on all BatchTest (batchtest) and
      * JUnitTest (test) however it can possibly be overridden by their
      * own properties.</p>
      * @param value <tt>false</tt> if it should not filter, otherwise
      * <tt>true<tt>
      *
      * @since Ant 1.5
      */
     public void setFiltertrace(boolean value) {
         this.filterTrace = value;
     }
 
     /**
      * If true, stop the build process when there is an error in a test.
      * This property is applied on all BatchTest (batchtest) and JUnitTest
      * (test) however it can possibly be overridden by their own
      * properties.
      * @param value <tt>true</tt> if it should halt, otherwise
      * <tt>false</tt>
      *
      * @since Ant 1.2
      */
     public void setHaltonerror(boolean value) {
         this.haltOnError = value;
     }
 
     /**
      * Property to set to "true" if there is a error in a test.
      *
      * <p>This property is applied on all BatchTest (batchtest) and
      * JUnitTest (test), however, it can possibly be overriden by
      * their own properties.</p>
      * @param propertyName the name of the property to set in the
      * event of an error.
      *
      * @since Ant 1.4
      */
     public void setErrorProperty(String propertyName) {
         this.errorProperty = propertyName;
     }
 
     /**
      * If true, stop the build process if a test fails
      * (errors are considered failures as well).
      * This property is applied on all BatchTest (batchtest) and
      * JUnitTest (test) however it can possibly be overridden by their
      * own properties.
      * @param value <tt>true</tt> if it should halt, otherwise
      * <tt>false</tt>
      *
      * @since Ant 1.2
      */
     public void setHaltonfailure(boolean value) {
         this.haltOnFail = value;
     }
 
     /**
      * Property to set to "true" if there is a failure in a test.
      *
      * <p>This property is applied on all BatchTest (batchtest) and
      * JUnitTest (test), however, it can possibly be overriden by
      * their own properties.</p>
      * @param propertyName the name of the property to set in the
      * event of an failure.
      *
      * @since Ant 1.4
      */
     public void setFailureProperty(String propertyName) {
         this.failureProperty = propertyName;
     }
 
     /**
      * If true, JVM should be forked for each test.
      *
      * <p>It avoids interference between testcases and possibly avoids
      * hanging the build.  this property is applied on all BatchTest
      * (batchtest) and JUnitTest (test) however it can possibly be
      * overridden by their own properties.</p>
      * @param value <tt>true</tt> if a JVM should be forked, otherwise
      * <tt>false</tt>
      * @see #setTimeout
      *
      * @since Ant 1.2
      */
     public void setFork(boolean value) {
         this.fork = value;
     }
 
     /**
      * Set the behavior when {@link #setFork fork} fork has been enabled.
      *
      * <p>Possible values are "once", "perTest" and "perBatch".  If
      * set to "once", only a single Java VM will be forked for all
      * tests, with "perTest" (the default) each test will run in a
      * fresh Java VM and "perBatch" will run all tests from the same
      * &lt;batchtest&gt; in the same Java VM.</p>
      *
      * <p>This attribute will be ignored if tests run in the same VM
      * as Ant.</p>
      *
      * <p>Only tests with the same configuration of haltonerror,
      * haltonfailure, errorproperty, failureproperty and filtertrace
      * can share a forked Java VM, so even if you set the value to
      * "once", Ant may need to fork mutliple VMs.</p>
      * @param mode the mode to use.
      * @since Ant 1.6.2
      */
     public void setForkMode(ForkMode mode) {
         this.forkMode = mode;
     }
 
     /**
      * If true, print one-line statistics for each test, or "withOutAndErr"
      * to also show standard output and error.
      *
      * Can take the values on, off, and withOutAndErr.
      * @param value <tt>true</tt> to print a summary,
      * <tt>withOutAndErr</tt> to include the test&apos;s output as
      * well, <tt>false</tt> otherwise.
      * @see SummaryJUnitResultFormatter
      *
      * @since Ant 1.2
      */
     public void setPrintsummary(SummaryAttribute value) {
         summaryValue = value.getValue();
         summary = value.asBoolean();
     }
 
     /**
      * Print summary enumeration values.
      */
     public static class SummaryAttribute extends EnumeratedAttribute {
         /**
          * list the possible values
          * @return  array of allowed values
          */
         public String[] getValues() {
             return new String[] {"true", "yes", "false", "no",
                                  "on", "off", "withOutAndErr"};
         }
 
         /**
          * gives the boolean equivalent of the authorized values
          * @return boolean equivalent of the value
          */
         public boolean asBoolean() {
             String v = getValue();
             return "true".equals(v)
                 || "on".equals(v)
                 || "yes".equals(v)
                 || "withOutAndErr".equals(v);
         }
     }
 
     /**
      * Set the timeout value (in milliseconds).
      *
      * <p>If the test is running for more than this value, the test
      * will be canceled. (works only when in 'fork' mode).</p>
      * @param value the maximum time (in milliseconds) allowed before
      * declaring the test as 'timed-out'
      * @see #setFork(boolean)
      *
      * @since Ant 1.2
      */
     public void setTimeout(Integer value) {
         timeout = value;
     }
 
     /**
      * Set the maximum memory to be used by all forked JVMs.
      * @param   max     the value as defined by <tt>-mx</tt> or <tt>-Xmx</tt>
      *                  in the java command line options.
      *
      * @since Ant 1.2
      */
     public void setMaxmemory(String max) {
         getCommandline().setMaxmemory(max);
     }
 
     /**
      * The command used to invoke the Java Virtual Machine,
      * default is 'java'. The command is resolved by
      * java.lang.Runtime.exec(). Ignored if fork is disabled.
      *
      * @param   value   the new VM to use instead of <tt>java</tt>
      * @see #setFork(boolean)
      *
      * @since Ant 1.2
      */
     public void setJvm(String value) {
         getCommandline().setVm(value);
     }
 
     /**
      * Adds a JVM argument; ignored if not forking.
      *
      * @return create a new JVM argument so that any argument can be
      * passed to the JVM.
      * @see #setFork(boolean)
      *
      * @since Ant 1.2
      */
     public Commandline.Argument createJvmarg() {
         return getCommandline().createVmArgument();
     }
 
     /**
      * The directory to invoke the VM in. Ignored if no JVM is forked.
      * @param   dir     the directory to invoke the JVM from.
      * @see #setFork(boolean)
      *
      * @since Ant 1.2
      */
     public void setDir(File dir) {
         this.dir = dir;
     }
 
     /**
      * Adds a system property that tests can access.
      * This might be useful to tranfer Ant properties to the
      * testcases when JVM forking is not enabled.
      *
      * @since Ant 1.3
      * @deprecated since ant 1.6
      * @param sysp environment variable to add
      */
     public void addSysproperty(Environment.Variable sysp) {
 
         getCommandline().addSysproperty(sysp);
     }
 
     /**
      * Adds a system property that tests can access.
      * This might be useful to tranfer Ant properties to the
      * testcases when JVM forking is not enabled.
      * @param sysp new environment variable to add
      * @since Ant 1.6
      */
     public void addConfiguredSysproperty(Environment.Variable sysp) {
         // get a build exception if there is a missing key or value
         // see bugzilla report 21684
         String testString = sysp.getContent();
         getProject().log("sysproperty added : " + testString, Project.MSG_DEBUG);
         getCommandline().addSysproperty(sysp);
     }
 
     /**
      * Adds a set of properties that will be used as system properties
      * that tests can access.
      *
      * This might be useful to tranfer Ant properties to the
      * testcases when JVM forking is not enabled.
      *
      * @param sysp set of properties to be added
      * @since Ant 1.6
      */
     public void addSyspropertyset(PropertySet sysp) {
         getCommandline().addSyspropertyset(sysp);
     }
 
     /**
      * Adds path to classpath used for tests.
      *
      * @return reference to the classpath in the embedded java command line
      * @since Ant 1.2
      */
     public Path createClasspath() {
         return getCommandline().createClasspath(getProject()).createPath();
     }
 
     /**
      * Adds a path to the bootclasspath.
      * @return reference to the bootclasspath in the embedded java command line
      * @since Ant 1.6
      */
     public Path createBootclasspath() {
         return getCommandline().createBootclasspath(getProject()).createPath();
     }
 
     /**
      * Adds an environment variable; used when forking.
      *
      * <p>Will be ignored if we are not forking a new VM.</p>
      * @param var environment variable to be added
      * @since Ant 1.5
      */
     public void addEnv(Environment.Variable var) {
         env.addVariable(var);
     }
 
     /**
      * If true, use a new environment when forked.
      *
      * <p>Will be ignored if we are not forking a new VM.</p>
      *
      * @param newenv boolean indicating if setting a new environment is wished
      * @since Ant 1.5
      */
     public void setNewenvironment(boolean newenv) {
         newEnvironment = newenv;
     }
 
     /**
      * Preset the attributes of the test
      * before configuration in the build
      * script.
      * This allows attributes in the <junit> task
      * be be defaults for the tests, but allows
      * individual tests to override the defaults.
      */
     private void preConfigure(BaseTest test) {
         test.setFiltertrace(filterTrace);
         test.setHaltonerror(haltOnError);
         if (errorProperty != null) {
             test.setErrorProperty(errorProperty);
         }
         test.setHaltonfailure(haltOnFail);
         if (failureProperty != null) {
             test.setFailureProperty(failureProperty);
         }
         test.setFork(fork);
     }
 
     /**
      * Add a new single testcase.
      * @param   test    a new single testcase
      * @see JUnitTest
      *
      * @since Ant 1.2
      */
     public void addTest(JUnitTest test) {
         tests.addElement(test);
         preConfigure(test);
     }
 
     /**
      * Adds a set of tests based on pattern matching.
      *
      * @return  a new instance of a batch test.
      * @see BatchTest
      *
      * @since Ant 1.2
      */
     public BatchTest createBatchTest() {
         BatchTest test = new BatchTest(getProject());
         batchTests.addElement(test);
         preConfigure(test);
         return test;
     }
 
     /**
      * Add a new formatter to all tests of this task.
      *
      * @param fe formatter element
      * @since Ant 1.2
      */
     public void addFormatter(FormatterElement fe) {
         formatters.addElement(fe);
     }
 
     /**
      * If true, include ant.jar, optional.jar and junit.jar in the forked VM.
      *
      * @param b include ant run time yes or no
      * @since Ant 1.5
      */
     public void setIncludeantruntime(boolean b) {
         includeAntRuntime = b;
     }
 
     /**
      * If true, send any output generated by tests to Ant's logging system
      * as well as to the formatters.
      * By default only the formatters receive the output.
      *
      * <p>Output will always be passed to the formatters and not by
      * shown by default.  This option should for example be set for
      * tests that are interactive and prompt the user to do
      * something.</p>
      *
      * @param showOutput if true, send output to Ant's logging system too
      * @since Ant 1.5
      */
     public void setShowOutput(boolean showOutput) {
         this.showOutput = showOutput;
     }
 
     /**
      * If true, send any output generated by tests to the formatters.
      *
      * @param outputToFormatters if true, send output to formatters (Default
      *                           is true).
      * @since Ant 1.7.0
      */
     public void setOutputToFormatters(boolean outputToFormatters) {
         this.outputToFormatters = outputToFormatters;
     }
 
     /**
      * If true, write a single "FAILED" line for failed tests to Ant's
      * log system.
      *
      * @since Ant 1.8.0
      */
     public void setLogFailedTests(boolean logFailedTests) {
         this.logFailedTests = logFailedTests;
     }
 
     /**
      * Assertions to enable in this program (if fork=true)
      * @since Ant 1.6
      * @param asserts assertion set
      */
     public void addAssertions(Assertions asserts) {
         if (getCommandline().getAssertions() != null) {
             throw new BuildException("Only one assertion declaration is allowed");
         }
         getCommandline().setAssertions(asserts);
     }
 
     /**
      * Sets the permissions for the application run inside the same JVM.
      * @since Ant 1.6
      * @return .
      */
     public Permissions createPermissions() {
         if (perm == null) {
             perm = new Permissions();
         }
         return perm;
     }
 
     /**
      * If set, system properties will be copied to the cloned VM - as
      * well as the bootclasspath unless you have explicitly specified
      * a bootclaspath.
      *
      * <p>Doesn't have any effect unless fork is true.</p>
      * @param cloneVm a <code>boolean</code> value.
      * @since Ant 1.7
      */
     public void setCloneVm(boolean cloneVm) {
         getCommandline().setCloneVm(cloneVm);
     }
 
     /**
      * Creates a new JUnitRunner and enables fork of a new Java VM.
      *
      * @throws Exception under ??? circumstances
      * @since Ant 1.2
      */
     public JUnitTask() throws Exception {
     }
 
     /**
      * Where Ant should place temporary files.
      *
      * @param tmpDir location where temporary files should go to
      * @since Ant 1.6
      */
     public void setTempdir(File tmpDir) {
         if (tmpDir != null) {
             if (!tmpDir.exists() || !tmpDir.isDirectory()) {
                 throw new BuildException(tmpDir.toString()
                                          + " is not a valid temp directory");
             }
         }
         this.tmpDir = tmpDir;
     }
 
     /**
      * Whether test listener events shall be generated.
      *
      * <p>Defaults to false.</p>
      * 
      * <p>This value will be overridden by the magic property
      * ant.junit.enabletestlistenerevents if it has been set.</p>
      *
      * @since Ant 1.8.2
      */
     public void setEnableTestListenerEvents(boolean b) {
         enableTestListenerEvents = b;
     }
 
     /**
      * Whether test listener events shall be generated.
      * @since Ant 1.8.2
      */
     public boolean getEnableTestListenerEvents() {
         String e = getProject().getProperty(ENABLE_TESTLISTENER_EVENTS);
         if (e != null) {
             return Project.toBoolean(e);
         }
         return enableTestListenerEvents;
     }
 
     /**
      * Adds the jars or directories containing Ant, this task and
      * JUnit to the classpath - this should make the forked JVM work
      * without having to specify them directly.
      *
      * @since Ant 1.4
      */
     public void init() {
         antRuntimeClasses = new Path(getProject());
         splitJunit = !addClasspathResource("/junit/framework/TestCase.class");
         addClasspathEntry("/org/apache/tools/ant/launch/AntMain.class");
         addClasspathEntry("/org/apache/tools/ant/Task.class");
         addClasspathEntry("/org/apache/tools/ant/taskdefs/optional/junit/JUnitTestRunner.class");
     }
 
     private static JUnitTaskMirror createMirror(JUnitTask task, ClassLoader loader) {
         try {
             loader.loadClass("junit.framework.Test"); // sanity check
         } catch (ClassNotFoundException e) {
             throw new BuildException(
                     "The <classpath> for <junit> must include junit.jar "
                     + "if not in Ant's own classpath",
                     e, task.getLocation());
         }
         try {
             Class c = loader.loadClass(JUnitTaskMirror.class.getName() + "Impl");
             if (c.getClassLoader() != loader) {
                 throw new BuildException("Overdelegating loader", task.getLocation());
             }
             Constructor cons = c.getConstructor(new Class[] {JUnitTask.class});
             return (JUnitTaskMirror) cons.newInstance(new Object[] {task});
         } catch (Exception e) {
             throw new BuildException(e, task.getLocation());
         }
     }
 
     /**
      * Sets up the delegate that will actually run the tests.
      *
      * <p>Will be invoked implicitly once the delegate is needed.</p>
      *
      * @since Ant 1.7.1
      */
     protected void setupJUnitDelegate() {
         final ClassLoader myLoader = JUnitTask.class.getClassLoader();
         if (splitJunit) {
             final Path path = new Path(getProject());
             path.add(antRuntimeClasses);
             Path extra = getCommandline().getClasspath();
             if (extra != null) {
                 path.add(extra);
             }
             mirrorLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
                 public Object run() {
                     return new SplitClassLoader(myLoader, path, getProject(),
                                      new String[] {
                                          "BriefJUnitResultFormatter",
                                          "JUnit4TestMethodAdapter",
                                          "JUnitResultFormatter",
                                          "JUnitTaskMirrorImpl",
                                          "JUnitTestRunner",
                                          "JUnitVersionHelper",
                                          "OutErrSummaryJUnitResultFormatter",
                                          "PlainJUnitResultFormatter",
                                          "SummaryJUnitResultFormatter",
                                          "TearDownOnVmCrash",
                                          "XMLJUnitResultFormatter",
                                      });
                 }
             });
         } else {
             mirrorLoader = myLoader;
         }
         delegate = createMirror(this, mirrorLoader);
     }
 
     /**
      * Runs the testcase.
      *
      * @throws BuildException in case of test failures or errors
      * @since Ant 1.2
      */
     public void execute() throws BuildException {
         checkMethodLists();
 
         setupJUnitDelegate();
 
         List testLists = new ArrayList();
 
         boolean forkPerTest = forkMode.getValue().equals(ForkMode.PER_TEST);
         if (forkPerTest || forkMode.getValue().equals(ForkMode.ONCE)) {
             testLists.addAll(executeOrQueue(getIndividualTests(),
                                             forkPerTest));
         } else { /* forkMode.getValue().equals(ForkMode.PER_BATCH) */
             final int count = batchTests.size();
             for (int i = 0; i < count; i++) {
                 BatchTest batchtest = (BatchTest) batchTests.elementAt(i);
                 testLists.addAll(executeOrQueue(batchtest.elements(), false));
             }
             testLists.addAll(executeOrQueue(tests.elements(), forkPerTest));
         }
 
         try {
             Iterator iter = testLists.iterator();
             while (iter.hasNext()) {
                 List l = (List) iter.next();
                 if (l.size() == 1) {
                     execute((JUnitTest) l.get(0));
                 } else {
                     execute(l);
                 }
             }
         } finally {
             cleanup();
         }
     }
 
     /**
      * Run the tests.
      * @param arg one JunitTest
      * @throws BuildException in case of test failures or errors
      */
     protected void execute(JUnitTest arg) throws BuildException {
         validateTestName(arg.getName());
 
         JUnitTest test = (JUnitTest) arg.clone();
         // set the default values if not specified
         //@todo should be moved to the test class instead.
         if (test.getTodir() == null) {
             test.setTodir(getProject().resolveFile("."));
         }
 
         if (test.getOutfile() == null) {
             test.setOutfile("TEST-" + test.getName());
         }
 
         // execute the test and get the return code
         TestResultHolder result = null;
         if (!test.getFork()) {
             result = executeInVM(test);
         } else {
             ExecuteWatchdog watchdog = createWatchdog();
             result = executeAsForked(test, watchdog, null);
             // null watchdog means no timeout, you'd better not check with null
         }
         actOnTestResult(result, test, "Test " + test.getName());
     }
 
     /**
      * Throws a <code>BuildException</code> if the given test name is invalid.
      * Validity is defined as not <code>null</code>, not empty, and not the
      * string &quot;null&quot;.
      * @param testName the test name to be validated
      * @throws BuildException if <code>testName</code> is not a valid test name
      */
     private void validateTestName(String testName) throws BuildException {
         if (testName == null || testName.length() == 0
             || testName.equals("null")) {
             throw new BuildException("test name must be specified");
         }
     }
 
     /**
      * Execute a list of tests in a single forked Java VM.
      * @param testList the list of tests to execute.
      * @throws BuildException on error.
      */
     protected void execute(List testList) throws BuildException {
         JUnitTest test = null;
         // Create a temporary file to pass the test cases to run to
         // the runner (one test case per line)
         File casesFile = createTempPropertiesFile("junittestcases");
         BufferedWriter writer = null;
         try {
             writer = new BufferedWriter(new FileWriter(casesFile));
 
             log("Creating casesfile '" + casesFile.getAbsolutePath()
                 + "' with content: ", Project.MSG_VERBOSE);
             PrintStream logWriter =
                 new PrintStream(new LogOutputStream(this, Project.MSG_VERBOSE));
 
             Iterator iter = testList.iterator();
             while (iter.hasNext()) {
                 test = (JUnitTest) iter.next();
                 printDual(writer, logWriter, test.getName());
                 if (test.getMethods() != null) {
                     printDual(writer, logWriter, ":" + test.getMethodsString().replace(',', '+'));
                 }
                 if (test.getTodir() == null) {
                     printDual(writer, logWriter,
                               "," + getProject().resolveFile("."));
                 } else {
                     printDual(writer, logWriter, "," + test.getTodir());
                 }
 
                 if (test.getOutfile() == null) {
                     printlnDual(writer, logWriter,
                                 "," + "TEST-" + test.getName());
                 } else {
                     printlnDual(writer, logWriter, "," + test.getOutfile());
                 }
             }
             writer.flush();
             writer.close();
             writer = null;
 
             // execute the test and get the return code
             ExecuteWatchdog watchdog = createWatchdog();
             TestResultHolder result =
                 executeAsForked(test, watchdog, casesFile);
             actOnTestResult(result, test, "Tests");
         } catch (IOException e) {
             log(e.toString(), Project.MSG_ERR);
             throw new BuildException(e);
         } finally {
             FileUtils.close(writer);
 
             try {
                 FILE_UTILS.tryHardToDelete(casesFile);
             } catch (Exception e) {
                 log(e.toString(), Project.MSG_ERR);
             }
         }
     }
 
     /**
      * Execute a testcase by forking a new JVM. The command will block
      * until it finishes. To know if the process was destroyed or not
      * or whether the forked Java VM exited abnormally, use the
      * attributes of the returned holder object.
      * @param  test       the testcase to execute.
      * @param  watchdog   the watchdog in charge of cancelling the test if it
      * exceeds a certain amount of time. Can be <tt>null</tt>, in this case
      * the test could probably hang forever.
      * @param casesFile list of test cases to execute. Can be <tt>null</tt>,
      * in this case only one test is executed.
      * @return the test results from the JVM itself.
      * @throws BuildException in case of error creating a temporary property file,
      * or if the junit process can not be forked
      */
     private TestResultHolder executeAsForked(JUnitTest test,
                                              ExecuteWatchdog watchdog,
                                              File casesFile)
         throws BuildException {
 
         if (perm != null) {
             log("Permissions ignored when running in forked mode!",
                 Project.MSG_WARN);
         }
 
         CommandlineJava cmd;
         try {
             cmd = (CommandlineJava) (getCommandline().clone());
         } catch (CloneNotSupportedException e) {
             throw new BuildException("This shouldn't happen", e, getLocation());
         }
         if (casesFile == null) {
             cmd.createArgument().setValue(test.getName());
             if (test.getMethods() != null) {
                 cmd.createArgument().setValue(Constants.METHOD_NAMES + test.getMethodsString());
             }
         } else {
             log("Running multiple tests in the same VM", Project.MSG_VERBOSE);
             cmd.createArgument().setValue(Constants.TESTSFILE + casesFile);
         }
 
         cmd.createArgument().setValue(Constants.FILTERTRACE + test.getFiltertrace());
         cmd.createArgument().setValue(Constants.HALT_ON_ERROR + test.getHaltonerror());
         cmd.createArgument().setValue(Constants.HALT_ON_FAILURE
                                       + test.getHaltonfailure());
         checkIncludeAntRuntime(cmd);
 
         checkIncludeSummary(cmd);
 
         cmd.createArgument().setValue(Constants.SHOWOUTPUT
                                       + String.valueOf(showOutput));
         cmd.createArgument().setValue(Constants.OUTPUT_TO_FORMATTERS
                                       + String.valueOf(outputToFormatters));
         cmd.createArgument().setValue(Constants.LOG_FAILED_TESTS
                                       + String.valueOf(logFailedTests));
 
         // #31885
         cmd.createArgument().setValue(Constants.LOGTESTLISTENEREVENTS
                                       + String.valueOf(getEnableTestListenerEvents()));
 
         StringBuffer formatterArg = new StringBuffer(STRING_BUFFER_SIZE);
         final FormatterElement[] feArray = mergeFormatters(test);
         for (int i = 0; i < feArray.length; i++) {
             FormatterElement fe = feArray[i];
             if (fe.shouldUse(this)) {
                 formatterArg.append(Constants.FORMATTER);
                 formatterArg.append(fe.getClassname());
                 File outFile = getOutput(fe, test);
                 if (outFile != null) {
                     formatterArg.append(",");
                     formatterArg.append(outFile);
                 }
                 cmd.createArgument().setValue(formatterArg.toString());
                 formatterArg = new StringBuffer();
             }
         }
 
         File vmWatcher = createTempPropertiesFile("junitvmwatcher");
         cmd.createArgument().setValue(Constants.CRASHFILE
                                       + vmWatcher.getAbsolutePath());
         File propsFile = createTempPropertiesFile("junit");
         cmd.createArgument().setValue(Constants.PROPSFILE
                                       + propsFile.getAbsolutePath());
         Hashtable p = getProject().getProperties();
         Properties props = new Properties();
         for (Enumeration e = p.keys(); e.hasMoreElements();) {
             Object key = e.nextElement();
             props.put(key, p.get(key));
         }
         try {
             FileOutputStream outstream = new FileOutputStream(propsFile);
             props.store(outstream, "Ant JUnitTask generated properties file");
             outstream.close();
         } catch (java.io.IOException e) {
             FILE_UTILS.tryHardToDelete(propsFile);
             throw new BuildException("Error creating temporary properties "
                                      + "file.", e, getLocation());
         }
 
         Execute execute = new Execute(
             new JUnitLogStreamHandler(
                 this,
                 Project.MSG_INFO,
                 Project.MSG_WARN),
             watchdog);
         execute.setCommandline(cmd.getCommandline());
         execute.setAntRun(getProject());
         if (dir != null) {
             execute.setWorkingDirectory(dir);
         }
 
         String[] environment = env.getVariables();
         if (environment != null) {
             for (int i = 0; i < environment.length; i++) {
                 log("Setting environment variable: " + environment[i],
                     Project.MSG_VERBOSE);
             }
         }
         execute.setNewenvironment(newEnvironment);
         execute.setEnvironment(environment);
 
         log(cmd.describeCommand(), Project.MSG_VERBOSE);
 
         checkForkedPath(cmd);
 
         TestResultHolder result = new TestResultHolder();
         try {
             result.exitCode = execute.execute();
         } catch (IOException e) {
             throw new BuildException("Process fork failed.", e, getLocation());
         } finally {
             String vmCrashString = "unknown";
             BufferedReader br = null;
             try {
                 if (vmWatcher.exists()) {
                     br = new BufferedReader(new FileReader(vmWatcher));
                     vmCrashString = br.readLine();
                 } else {
                     vmCrashString = "Monitor file ("
                             + vmWatcher.getAbsolutePath()
                             + ") missing, location not writable,"
                             + " testcase not started or mixing ant versions?";
                 }
             } catch (Exception e) {
                 e.printStackTrace();
                 // ignored.
             } finally {
                 FileUtils.close(br);
                 if (vmWatcher.exists()) {
                     FILE_UTILS.tryHardToDelete(vmWatcher);
                 }
             }
 
             boolean crash = (watchdog != null && watchdog.killedProcess())
                 || !Constants.TERMINATED_SUCCESSFULLY.equals(vmCrashString);
 
             if (casesFile != null && crash) {
                 test = createDummyTestForBatchTest(test);
             }
 
             if (watchdog != null && watchdog.killedProcess()) {
                 result.timedOut = true;
                 logTimeout(feArray, test, vmCrashString);
             } else if (crash) {
                 result.crashed = true;
                 logVmCrash(feArray, test, vmCrashString);
             }
 
             if (!FILE_UTILS.tryHardToDelete(propsFile)) {
                 throw new BuildException("Could not delete temporary "
                                          + "properties file '"
                                          + propsFile.getAbsolutePath() + "'.");
             }
         }
 
         return result;
     }
 
     /**
      * Adding ant runtime.
      * @param cmd command to run
      */
     private void checkIncludeAntRuntime(CommandlineJava cmd) {
         if (includeAntRuntime) {
-            Vector v = Execute.getProcEnvironment();
-            Enumeration e = v.elements();
-            while (e.hasMoreElements()) {
-                String s = (String) e.nextElement();
-                if (s.startsWith(CLASSPATH)) {
-                    cmd.createClasspath(getProject()).createPath()
-                        .append(new Path(getProject(),
-                                         s.substring(CLASSPATH.length()
-                                                     )));
-                }
+            Map/*<String, String>*/ env = Execute.getEnvironmentVariables();
+            String cp = (String) env.get(CLASSPATH);
+            if (cp != null) {
+                cmd.createClasspath(getProject()).createPath()
+                    .append(new Path(getProject(), cp));
             }
             log("Implicitly adding " + antRuntimeClasses + " to CLASSPATH",
                 Project.MSG_VERBOSE);
             cmd.createClasspath(getProject()).createPath()
                 .append(antRuntimeClasses);
         }
     }
 
 
     /**
      * check for the parameter being "withoutanderr" in a locale-independent way.
      * @param summaryOption the summary option -can be null
      * @return true if the run should be withoutput and error
      */
     private boolean equalsWithOutAndErr(String summaryOption) {
         return "withoutanderr".equalsIgnoreCase(summaryOption);
     }
 
     private void checkIncludeSummary(CommandlineJava cmd) {
         if (summary) {
             String prefix = "";
             if (equalsWithOutAndErr(summaryValue)) {
                 prefix = "OutErr";
             }
             cmd.createArgument()
                 .setValue(Constants.FORMATTER
                           + "org.apache.tools.ant.taskdefs.optional.junit."
                           + prefix + "SummaryJUnitResultFormatter");
         }
     }
 
     /**
      * Check the path for multiple different versions of
      * ant.
      * @param cmd command to execute
      */
     private void checkForkedPath(CommandlineJava cmd) {
         if (forkedPathChecked) {
             return;
         }
         forkedPathChecked = true;
         if (!cmd.haveClasspath()) {
             return;
         }
         AntClassLoader loader = null;
         try {
             loader =
                 AntClassLoader.newAntClassLoader(null, getProject(),
                                                  cmd.createClasspath(getProject()),
                                                  true);
             String projectResourceName =
                 LoaderUtils.classNameToResource(Project.class.getName());
             URL previous = null;
             try {
                 for (Enumeration e = loader.getResources(projectResourceName);
                      e.hasMoreElements();) {
                     URL current = (URL) e.nextElement();
                     if (previous != null && !urlEquals(current, previous)) {
                         log("WARNING: multiple versions of ant detected "
                             + "in path for junit "
                             + LINE_SEP + "         " + previous
                             + LINE_SEP + "     and " + current,
                             Project.MSG_WARN);
                         return;
                     }
                     previous = current;
                 }
             } catch (Exception ex) {
                 // Ignore exception
             }
         } finally {
             if (loader != null) {
                 loader.cleanup();
             }
         }
     }
 
     /**
      * Compares URLs for equality but takes case-sensitivity into
      * account when comparing file URLs and ignores the jar specific
      * part of the URL if present.
      */
     private static boolean urlEquals(URL u1, URL u2) {
         String url1 = maybeStripJarAndClass(u1);
         String url2 = maybeStripJarAndClass(u2);
         if (url1.startsWith("file:") && url2.startsWith("file:")) {
             return new File(FILE_UTILS.fromURI(url1))
                 .equals(new File(FILE_UTILS.fromURI(url2)));
         }
         return url1.equals(url2);
     }
 
     private static String maybeStripJarAndClass(URL u) {
         String s = u.toString();
         if (s.startsWith("jar:")) {
             int pling = s.indexOf('!');
             s = s.substring(4, pling == -1 ? s.length() : pling);
         }
         return s;
     }
 
     /**
      * Create a temporary file to pass the properties to a new process.
      * Will auto-delete on (graceful) exit.
      * The file will be in the project basedir unless tmpDir declares
      * something else.
      * @param prefix
      * @return created file
      */
     private File createTempPropertiesFile(String prefix) {
         File propsFile =
             FILE_UTILS.createTempFile(prefix, ".properties",
                 tmpDir != null ? tmpDir : getProject().getBaseDir(), true, true);
         return propsFile;
     }
 
 
     /**
      * Pass output sent to System.out to the TestRunner so it can
      * collect it for the formatters.
      *
      * @param output output coming from System.out
      * @since Ant 1.5
      */
     protected void handleOutput(String output) {
         if (output.startsWith(TESTLISTENER_PREFIX)) {
             log(output, Project.MSG_VERBOSE);
         } else if (runner != null) {
             if (outputToFormatters) {
                 runner.handleOutput(output);
             }
             if (showOutput) {
                 super.handleOutput(output);
             }
         } else {
             super.handleOutput(output);
         }
     }
 
     /**
      * Handle an input request by this task.
      * @see Task#handleInput(byte[], int, int)
      * This implementation delegates to a runner if it
      * present.
      * @param buffer the buffer into which data is to be read.
      * @param offset the offset into the buffer at which data is stored.
      * @param length the amount of data to read.
      *
      * @return the number of bytes read.
      * @exception IOException if the data cannot be read.
      *
      * @since Ant 1.6
      */
     protected int handleInput(byte[] buffer, int offset, int length)
         throws IOException {
         if (runner != null) {
             return runner.handleInput(buffer, offset, length);
         } else {
             return super.handleInput(buffer, offset, length);
         }
     }
 
 
     /**
      * Pass output sent to System.out to the TestRunner so it can
      * collect ot for the formatters.
      *
      * @param output output coming from System.out
      * @since Ant 1.5.2
      */
     protected void handleFlush(String output) {
         if (runner != null) {
             runner.handleFlush(output);
             if (showOutput) {
                 super.handleFlush(output);
             }
         } else {
             super.handleFlush(output);
         }
     }
 
     /**
      * Pass output sent to System.err to the TestRunner so it can
      * collect it for the formatters.
      *
      * @param output output coming from System.err
      * @since Ant 1.5
      */
     public void handleErrorOutput(String output) {
         if (runner != null) {
             runner.handleErrorOutput(output);
             if (showOutput) {
                 super.handleErrorOutput(output);
             }
         } else {
             super.handleErrorOutput(output);
         }
     }
 
 
     /**
      * Pass output sent to System.err to the TestRunner so it can
      * collect it for the formatters.
      *
      * @param output coming from System.err
      * @since Ant 1.5.2
      */
     public void handleErrorFlush(String output) {
         if (runner != null) {
             runner.handleErrorFlush(output);
             if (showOutput) {
                 super.handleErrorFlush(output);
             }
         } else {
             super.handleErrorFlush(output);
         }
     }
 
     // in VM is not very nice since it could probably hang the
     // whole build. IMHO this method should be avoided and it would be best
     // to remove it in future versions. TBD. (SBa)
 
     /**
      * Execute inside VM.
      * @param arg one JUnitTest
      * @throws BuildException under unspecified circumstances
      * @return the results
      */
     private TestResultHolder executeInVM(JUnitTest arg) throws BuildException {
         if (delegate == null) {
             setupJUnitDelegate();
         }
 
         JUnitTest test = (JUnitTest) arg.clone();
         test.setProperties(getProject().getProperties());
         if (dir != null) {
             log("dir attribute ignored if running in the same VM",
                 Project.MSG_WARN);
         }
 
         if (newEnvironment || null != env.getVariables()) {
             log("Changes to environment variables are ignored if running in "
                 + "the same VM.", Project.MSG_WARN);
         }
 
         if (getCommandline().getBootclasspath() != null) {
             log("bootclasspath is ignored if running in the same VM.",
                 Project.MSG_WARN);
         }
 
         CommandlineJava.SysProperties sysProperties =
                 getCommandline().getSystemProperties();
         if (sysProperties != null) {
             sysProperties.setSystem();
         }
 
         try {
             log("Using System properties " + System.getProperties(),
                 Project.MSG_VERBOSE);
             if (splitJunit) {
                 classLoader = (AntClassLoader) delegate.getClass().getClassLoader();
             } else {
                 createClassLoader();
             }
             if (classLoader != null) {
                 classLoader.setThreadContextLoader();
             }
             runner = delegate.newJUnitTestRunner(test, test.getMethods(), test.getHaltonerror(),
                                          test.getFiltertrace(),
                                          test.getHaltonfailure(), false,
                                          getEnableTestListenerEvents(),
                                          classLoader);
             if (summary) {
 
                 JUnitTaskMirror.SummaryJUnitResultFormatterMirror f =
                     delegate.newSummaryJUnitResultFormatter();
                 f.setWithOutAndErr(equalsWithOutAndErr(summaryValue));
                 f.setOutput(getDefaultOutput());
                 runner.addFormatter(f);
             }
 
             runner.setPermissions(perm);
 
             final FormatterElement[] feArray = mergeFormatters(test);
             for (int i = 0; i < feArray.length; i++) {
                 FormatterElement fe = feArray[i];
                 if (fe.shouldUse(this)) {
                     File outFile = getOutput(fe, test);
                     if (outFile != null) {
                         fe.setOutfile(outFile);
                     } else {
                         fe.setOutput(getDefaultOutput());
                     }
                     runner.addFormatter(fe.createFormatter(classLoader));
                 }
             }
 
             runner.run();
             TestResultHolder result = new TestResultHolder();
             result.exitCode = runner.getRetCode();
             return result;
         } finally {
             if (sysProperties != null) {
                 sysProperties.restoreSystem();
             }
             if (classLoader != null) {
                 classLoader.resetThreadContextLoader();
             }
         }
     }
 
     /**
      * @return <tt>null</tt> if there is a timeout value, otherwise the
      * watchdog instance.
      *
      * @throws BuildException under unspecified circumstances
      * @since Ant 1.2
      */
     protected ExecuteWatchdog createWatchdog() throws BuildException {
         if (timeout == null) {
             return null;
         }
         return new ExecuteWatchdog((long) timeout.intValue());
     }
 
     /**
      * Get the default output for a formatter.
      *
      * @return default output stream for a formatter
      * @since Ant 1.3
      */
     protected OutputStream getDefaultOutput() {
         return new LogOutputStream(this, Project.MSG_INFO);
     }
 
     /**
      * Merge all individual tests from the batchtest with all individual tests
      * and return an enumeration over all <tt>JUnitTest</tt>.
      *
      * @return enumeration over individual tests
      * @since Ant 1.3
      */
     protected Enumeration getIndividualTests() {
         final int count = batchTests.size();
         final Enumeration[] enums = new Enumeration[ count + 1];
         for (int i = 0; i < count; i++) {
             BatchTest batchtest = (BatchTest) batchTests.elementAt(i);
             enums[i] = batchtest.elements();
         }
         enums[enums.length - 1] = tests.elements();
         return Enumerations.fromCompound(enums);
     }
 
     /**
      * Verifies all <code>test</code> elements having the <code>methods</code>
      * attribute specified and having the <code>if</code>-condition resolved
      * to true, that the value of the <code>methods</code> attribute is valid.
      * @exception BuildException if some of the tests matching the described
      *                           conditions has invalid value of the
      *                           <code>methods</code> attribute
      * @since 1.8.2
      */
     private void checkMethodLists() throws BuildException {
         if (tests.isEmpty()) {
             return;
         }
 
         Enumeration testsEnum = tests.elements();
         while (testsEnum.hasMoreElements()) {
             JUnitTest test = (JUnitTest) testsEnum.nextElement();
             if (test.hasMethodsSpecified() && test.shouldRun(getProject())) {
                 test.resolveMethods();
             }
         }
     }
 
     /**
      * return an enumeration listing each test, then each batchtest
      * @return enumeration
      * @since Ant 1.3
      */
     protected Enumeration allTests() {
         Enumeration[] enums = {tests.elements(), batchTests.elements()};
         return Enumerations.fromCompound(enums);
     }
 
     /**
      * @param test junit test
      * @return array of FormatterElement
      * @since Ant 1.3
      */
     private FormatterElement[] mergeFormatters(JUnitTest test) {
         Vector feVector = (Vector) formatters.clone();
         test.addFormattersTo(feVector);
         FormatterElement[] feArray = new FormatterElement[feVector.size()];
         feVector.copyInto(feArray);
         return feArray;
     }
 
     /**
      * If the formatter sends output to a file, return that file.
      * null otherwise.
      * @param fe  formatter element
      * @param test one JUnit test
      * @return file reference
      * @since Ant 1.3
      */
     protected File getOutput(FormatterElement fe, JUnitTest test) {
         if (fe.getUseFile()) {
             String base = test.getOutfile();
             if (base == null) {
                 base = JUnitTaskMirror.JUnitTestRunnerMirror.IGNORED_FILE_NAME;
             }
             String filename = base + fe.getExtension();
             File destFile = new File(test.getTodir(), filename);
             String absFilename = destFile.getAbsolutePath();
             return getProject().resolveFile(absFilename);
         }
         return null;
     }
 
     /**
      * Search for the given resource and add the directory or archive
      * that contains it to the classpath.
      *
      * <p>Doesn't work for archives in JDK 1.1 as the URL returned by
      * getResource doesn't contain the name of the archive.</p>
      *
      * @param resource resource that one wants to lookup
      * @since Ant 1.4
      */
     protected void addClasspathEntry(String resource) {
         addClasspathResource(resource);
     }
 
     /**
      * Implementation of addClasspathEntry.
      *
      * @param resource resource that one wants to lookup
      * @return true if something was in fact added
      * @since Ant 1.7.1
      */
     private boolean addClasspathResource(String resource) {
         /*
          * pre Ant 1.6 this method used to call getClass().getResource
          * while Ant 1.6 will call ClassLoader.getResource().
          *
          * The difference is that Class.getResource expects a leading
          * slash for "absolute" resources and will strip it before
          * delegating to ClassLoader.getResource - so we now have to
          * emulate Class's behavior.
          */
         if (resource.startsWith("/")) {
             resource = resource.substring(1);
         } else {
             resource = "org/apache/tools/ant/taskdefs/optional/junit/"
                 + resource;
         }
 
         File f = LoaderUtils.getResourceSource(getClass().getClassLoader(),
                                                resource);
         if (f != null) {
             log("Found " + f.getAbsolutePath(), Project.MSG_DEBUG);
             antRuntimeClasses.createPath().setLocation(f);
             return true;
         } else {
             log("Couldn\'t find " + resource, Project.MSG_DEBUG);
             return false;
         }
     }
 
     static final String TIMEOUT_MESSAGE = 
         "Timeout occurred. Please note the time in the report does"
         + " not reflect the time until the timeout.";
 
     /**
      * Take care that some output is produced in report files if the
      * watchdog kills the test.
      *
      * @since Ant 1.5.2
      */
     private void logTimeout(FormatterElement[] feArray, JUnitTest test,
                             String testCase) {
         logVmExit(feArray, test, TIMEOUT_MESSAGE, testCase);
     }
 
     /**
      * Take care that some output is produced in report files if the
      * forked machine exited before the test suite finished but the
      * reason is not a timeout.
      *
      * @since Ant 1.7
      */
     private void logVmCrash(FormatterElement[] feArray, JUnitTest test, String testCase) {
         logVmExit(
             feArray, test,
             "Forked Java VM exited abnormally. Please note the time in the report"
             + " does not reflect the time until the VM exit.",
             testCase);
     }
 
     /**
      * Take care that some output is produced in report files if the
      * forked machine terminated before the test suite finished
      *
      * @since Ant 1.7
      */
     private void logVmExit(FormatterElement[] feArray, JUnitTest test,
                            String message, String testCase) {
         if (delegate == null) {
             setupJUnitDelegate();
         }
 
         try {
             log("Using System properties " + System.getProperties(),
                 Project.MSG_VERBOSE);
             if (splitJunit) {
                 classLoader = (AntClassLoader) delegate.getClass().getClassLoader();
             } else {
                 createClassLoader();
             }
             if (classLoader != null) {
                 classLoader.setThreadContextLoader();
             }
 
             test.setCounts(1, 0, 1);
             test.setProperties(getProject().getProperties());
             for (int i = 0; i < feArray.length; i++) {
                 FormatterElement fe = feArray[i];
                 if (fe.shouldUse(this)) {
                     JUnitTaskMirror.JUnitResultFormatterMirror formatter =
                         fe.createFormatter(classLoader);
                     if (formatter != null) {
                         OutputStream out = null;
                         File outFile = getOutput(fe, test);
                         if (outFile != null) {
                             try {
                                 out = new FileOutputStream(outFile);
                             } catch (IOException e) {
                                 // ignore
                             }
                         }
                         if (out == null) {
                             out = getDefaultOutput();
                         }
                         delegate.addVmExit(test, formatter, out, message,
                                            testCase);
                     }
                 }
             }
             if (summary) {
                 JUnitTaskMirror.SummaryJUnitResultFormatterMirror f =
                     delegate.newSummaryJUnitResultFormatter();
                 f.setWithOutAndErr(equalsWithOutAndErr(summaryValue));
                 delegate.addVmExit(test, f, getDefaultOutput(), message, testCase);
             }
         } finally {
             if (classLoader != null) {
                 classLoader.resetThreadContextLoader();
             }
         }
     }
 
     /**
      * Creates and configures an AntClassLoader instance from the
      * nested classpath element.
      *
      * @since Ant 1.6
      */
     private void createClassLoader() {
         Path userClasspath = getCommandline().getClasspath();
         if (userClasspath != null) {
             if (reloading || classLoader == null) {
                 deleteClassLoader();
                 Path classpath = (Path) userClasspath.clone();
                 if (includeAntRuntime) {
                     log("Implicitly adding " + antRuntimeClasses
                         + " to CLASSPATH", Project.MSG_VERBOSE);
                     classpath.append(antRuntimeClasses);
                 }
                 classLoader = getProject().createClassLoader(classpath);
                 if (getClass().getClassLoader() != null
                     && getClass().getClassLoader() != Project.class.getClassLoader()) {
                     classLoader.setParent(getClass().getClassLoader());
                 }
                 classLoader.setParentFirst(false);
                 classLoader.addJavaLibraries();
                 log("Using CLASSPATH " + classLoader.getClasspath(),
                     Project.MSG_VERBOSE);
                 // make sure the test will be accepted as a TestCase
                 classLoader.addSystemPackageRoot("junit");
                 // make sure the test annotation are accepted
                 classLoader.addSystemPackageRoot("org.junit");
                 // will cause trouble in JDK 1.1 if omitted
                 classLoader.addSystemPackageRoot("org.apache.tools.ant");
             }
         }
     }
 
     /**
      * Removes resources.
      *
      * <p>Is invoked in {@link #execute execute}.  Subclasses that
      * don't invoke execute should invoke this method in a finally
      * block.</p>
      *
      * @since Ant 1.7.1
      */
     protected void cleanup() {
         deleteClassLoader();
         delegate = null;
     }
 
     /**
      * Removes a classloader if needed.
      * @since Ant 1.7
      */
     private void deleteClassLoader() {
         if (classLoader != null) {
             classLoader.cleanup();
             classLoader = null;
         }
         if (mirrorLoader instanceof SplitClassLoader) {
             ((SplitClassLoader) mirrorLoader).cleanup();
         }
         mirrorLoader = null;
     }
 
     /**
      * Get the command line used to run the tests.
      * @return the command line.
      * @since Ant 1.6.2
      */
     protected CommandlineJava getCommandline() {
         if (commandline == null) {
             commandline = new CommandlineJava();
             commandline.setClassname("org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner");
         }
         return commandline;
     }
 
     /**
      * Forked test support
      * @since Ant 1.6.2
      */
     private static final class ForkedTestConfiguration {
         private boolean filterTrace;
         private boolean haltOnError;
         private boolean haltOnFailure;
         private String errorProperty;
         private String failureProperty;
 
         /**
          * constructor for forked test configuration
          * @param filterTrace
          * @param haltOnError
          * @param haltOnFailure
          * @param errorProperty
          * @param failureProperty
          */
         ForkedTestConfiguration(boolean filterTrace, boolean haltOnError,
                                 boolean haltOnFailure, String errorProperty,
                                 String failureProperty) {
             this.filterTrace = filterTrace;
             this.haltOnError = haltOnError;
             this.haltOnFailure = haltOnFailure;
             this.errorProperty = errorProperty;
             this.failureProperty = failureProperty;
         }
 
         /**
          * configure from a test; sets member variables to attributes of the test
          * @param test
          */
         ForkedTestConfiguration(JUnitTest test) {
             this(test.getFiltertrace(),
                     test.getHaltonerror(),
                     test.getHaltonfailure(),
                     test.getErrorProperty(),
                     test.getFailureProperty());
         }
 
         /**
          * equality test checks all the member variables
          * @param other
          * @return true if everything is equal
          */
         public boolean equals(Object other) {
             if (other == null
                 || other.getClass() != ForkedTestConfiguration.class) {
                 return false;
             }
             ForkedTestConfiguration o = (ForkedTestConfiguration) other;
             return filterTrace == o.filterTrace
                 && haltOnError == o.haltOnError
                 && haltOnFailure == o.haltOnFailure
                 && ((errorProperty == null && o.errorProperty == null)
                     ||
                     (errorProperty != null
                      && errorProperty.equals(o.errorProperty)))
                 && ((failureProperty == null && o.failureProperty == null)
                     ||
                     (failureProperty != null
                      && failureProperty.equals(o.failureProperty)));
         }
 
         /**
          * hashcode is based only on the boolean members, and returns a value
          * in the range 0-7.
          * @return hash code value
          */
         public int hashCode() {
             // CheckStyle:MagicNumber OFF
             return (filterTrace ? 1 : 0)
                 + (haltOnError ? 2 : 0)
                 + (haltOnFailure ? 4 : 0);
             // CheckStyle:MagicNumber ON
         }
     }
 
     /**
      * These are the different forking options
      * @since 1.6.2
      */
     public static final class ForkMode extends EnumeratedAttribute {
 
         /**
          * fork once only
          */
         public static final String ONCE = "once";
         /**
          * fork once per test class
          */
         public static final String PER_TEST = "perTest";
         /**
          * fork once per batch of tests
          */
         public static final String PER_BATCH = "perBatch";
 
         /** No arg constructor. */
         public ForkMode() {
             super();
         }
 
         /**
          * Constructor using a value.
          * @param value the value to use - once, perTest or perBatch.
          */
         public ForkMode(String value) {
             super();
             setValue(value);
         }
 
         /** {@inheritDoc}. */
         public String[] getValues() {
             return new String[] {ONCE, PER_TEST, PER_BATCH};
         }
     }
 
     /**
      * Executes all tests that don't need to be forked (or all tests
      * if the runIndividual argument is true.  Returns a collection of
      * lists of tests that share the same VM configuration and haven't
      * been executed yet.
      * @param testList the list of tests to be executed or queued.
      * @param runIndividual if true execute each test individually.
      * @return a list of tasks to be executed.
      * @since 1.6.2
      */
     protected Collection executeOrQueue(Enumeration testList,
                                         boolean runIndividual) {
         Map testConfigurations = new HashMap();
         while (testList.hasMoreElements()) {
             JUnitTest test = (JUnitTest) testList.nextElement();
             if (test.shouldRun(getProject())) {
                 if (runIndividual || !test.getFork()) {
                     execute(test);
                 } else {
                     ForkedTestConfiguration c =
                         new ForkedTestConfiguration(test);
                     List l = (List) testConfigurations.get(c);
                     if (l == null) {
                         l = new ArrayList();
                         testConfigurations.put(c, l);
                     }
                     l.add(test);
                 }
             }
         }
         return testConfigurations.values();
     }
 
     /**
      * Logs information about failed tests, potentially stops
      * processing (by throwing a BuildException) if a failure/error
      * occurred or sets a property.
      * @param exitValue the exitValue of the test.
      * @param wasKilled if true, the test had been killed.
      * @param test      the test in question.
      * @param name      the name of the test.
      * @since Ant 1.6.2
      */
     protected void actOnTestResult(int exitValue, boolean wasKilled,
                                    JUnitTest test, String name) {
         TestResultHolder t = new TestResultHolder();
         t.exitCode = exitValue;
         t.timedOut = wasKilled;
         actOnTestResult(t, test, name);
     }
 
     /**
      * Logs information about failed tests, potentially stops
      * processing (by throwing a BuildException) if a failure/error
      * occurred or sets a property.
      * @param result    the result of the test.
      * @param test      the test in question.
      * @param name      the name of the test.
      * @since Ant 1.7
      */
     protected void actOnTestResult(TestResultHolder result, JUnitTest test,
                                    String name) {
         // if there is an error/failure and that it should halt, stop
         // everything otherwise just log a statement
         boolean fatal = result.timedOut || result.crashed;
         boolean errorOccurredHere =
             result.exitCode == JUnitTaskMirror.JUnitTestRunnerMirror.ERRORS || fatal;
         boolean failureOccurredHere =
             result.exitCode != JUnitTaskMirror.JUnitTestRunnerMirror.SUCCESS || fatal;
         if (errorOccurredHere || failureOccurredHere) {
             if ((errorOccurredHere && test.getHaltonerror())
                 || (failureOccurredHere && test.getHaltonfailure())) {
                 throw new BuildException(name + " failed"
                     + (result.timedOut ? " (timeout)" : "")
                     + (result.crashed ? " (crashed)" : ""), getLocation());
             } else {
                 if (logFailedTests) {
                     log(name + " FAILED"
                         + (result.timedOut ? " (timeout)" : "")
                         + (result.crashed ? " (crashed)" : ""),
                         Project.MSG_ERR);
                 }
                 if (errorOccurredHere && test.getErrorProperty() != null) {
                     getProject().setNewProperty(test.getErrorProperty(), "true");
                 }
                 if (failureOccurredHere && test.getFailureProperty() != null) {
                     getProject().setNewProperty(test.getFailureProperty(), "true");
                 }
             }
         }
     }
 
     /**
      * A value class that contains the result of a test.
      */
     protected static class TestResultHolder {
         // CheckStyle:VisibilityModifier OFF - bc
         /** the exit code of the test. */
         public int exitCode = JUnitTaskMirror.JUnitTestRunnerMirror.ERRORS;
         /** true if the test timed out */
         public boolean timedOut = false;
         /** true if the test crashed */
         public boolean crashed = false;
         // CheckStyle:VisibilityModifier ON
     }
 
     /**
      * A stream handler for handling the junit task.
      * @since Ant 1.7
      */
     protected static class JUnitLogOutputStream extends LogOutputStream {
         private Task task; // local copy since LogOutputStream.task is private
 
         /**
          * Constructor.
          * @param task the task being logged.
          * @param level the log level used to log data written to this stream.
          */
         public JUnitLogOutputStream(Task task, int level) {
             super(task, level);
             this.task = task;
         }
 
         /**
          * Logs a line.
          * If the line starts with junit.framework.TestListener: set the level
          * to MSG_VERBOSE.
          * @param line the line to log.
          * @param level the logging level to use.
          */
         protected void processLine(String line, int level) {
             if (line.startsWith(TESTLISTENER_PREFIX)) {
                 task.log(line, Project.MSG_VERBOSE);
             } else {
                 super.processLine(line, level);
             }
         }
     }
 
     /**
      * A log stream handler for junit.
      * @since Ant 1.7
      */
     protected static class JUnitLogStreamHandler extends PumpStreamHandler {
         /**
          * Constructor.
          * @param task the task to log.
          * @param outlevel the level to use for standard output.
          * @param errlevel the level to use for error output.
          */
         public JUnitLogStreamHandler(Task task, int outlevel, int errlevel) {
             super(new JUnitLogOutputStream(task, outlevel),
                   new LogOutputStream(task, errlevel));
         }
     }
 
     static final String NAME_OF_DUMMY_TEST = "Batch-With-Multiple-Tests";
 
     /**
      * Creates a JUnitTest instance that shares all flags with the
      * passed in instance but has a more meaningful name.
      *
      * <p>If a VM running multiple tests crashes, we don't know which
      * test failed.  Prior to Ant 1.8.0 Ant would log the error with
      * the last test of the batch test, which caused some confusion
      * since the log might look as if a test had been executed last
      * that was never started.  With Ant 1.8.0 the test's name will
      * indicate that something went wrong with a test inside the batch
      * without giving it a real name.</p>
      *
      * @see https://issues.apache.org/bugzilla/show_bug.cgi?id=45227
      */
     private static JUnitTest createDummyTestForBatchTest(JUnitTest test) {
         JUnitTest t = (JUnitTest) test.clone();
         int index = test.getName().lastIndexOf('.');
         // make sure test looks as if it was in the same "package" as
         // the last test of the batch
         String pack = index > 0 ? test.getName().substring(0, index + 1) : "";
         t.setName(pack + NAME_OF_DUMMY_TEST);
         return t;
     }
 
     private static void printDual(BufferedWriter w, PrintStream s, String text)
         throws IOException {
         w.write(String.valueOf(text));
         s.print(text);
     }
 
     private static void printlnDual(BufferedWriter w, PrintStream s, String text)
         throws IOException {
         w.write(String.valueOf(text));
         w.newLine();
         s.println(text);
     }
 }
