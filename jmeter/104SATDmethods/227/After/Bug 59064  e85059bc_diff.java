diff --git a/src/core/org/apache/jmeter/reporters/ResultCollector.java b/src/core/org/apache/jmeter/reporters/ResultCollector.java
index 79011cf02..f37e777ec 100644
--- a/src/core/org/apache/jmeter/reporters/ResultCollector.java
+++ b/src/core/org/apache/jmeter/reporters/ResultCollector.java
@@ -1,692 +1,679 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.reporters;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.io.RandomAccessFile;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 
-import org.apache.avalon.framework.configuration.ConfigurationException;
 import org.apache.jmeter.engine.util.NoThreadClone;
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.samplers.Clearable;
 import org.apache.jmeter.samplers.Remoteable;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleListener;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.save.CSVSaveService;
-import org.apache.jmeter.save.OldSaveService;
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.services.FileServer;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestStateListener;
 import org.apache.jmeter.testelement.property.BooleanProperty;
 import org.apache.jmeter.testelement.property.ObjectProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
-import org.xml.sax.SAXException;
 
 import com.thoughtworks.xstream.converters.ConversionException;
 
 /**
  * This class handles all saving of samples.
  * The class must be thread-safe because it is shared between threads (NoThreadClone).
  */
 public class ResultCollector extends AbstractListenerElement implements SampleListener, Clearable, Serializable,
         TestStateListener, Remoteable, NoThreadClone {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     private static final long serialVersionUID = 233L;
 
     // This string is used to identify local test runs, so must not be a valid host name
     private static final String TEST_IS_LOCAL = "*local*"; // $NON-NLS-1$
 
     private static final String TESTRESULTS_START = "<testResults>"; // $NON-NLS-1$
 
     private static final String TESTRESULTS_START_V1_1_PREVER = "<testResults version=\"";  // $NON-NLS-1$
 
     private static final String TESTRESULTS_START_V1_1_POSTVER="\">"; // $NON-NLS-1$
 
     private static final String TESTRESULTS_END = "</testResults>"; // $NON-NLS-1$
 
     private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; // $NON-NLS-1$
 
     private static final int MIN_XML_FILE_LEN = XML_HEADER.length() + TESTRESULTS_START.length()
             + TESTRESULTS_END.length();
 
     public static final String FILENAME = "filename"; // $NON-NLS-1$
 
     private static final String SAVE_CONFIG = "saveConfig"; // $NON-NLS-1$
 
     private static final String ERROR_LOGGING = "ResultCollector.error_logging"; // $NON-NLS-1$
 
     private static final String SUCCESS_ONLY_LOGGING = "ResultCollector.success_only_logging"; // $NON-NLS-1$
 
     /** AutoFlush on each line */
     private static final boolean SAVING_AUTOFLUSH = JMeterUtils.getPropDefault("jmeter.save.saveservice.autoflush", false); //$NON-NLS-1$
 
     // Static variables
 
     // Lock used to guard static mutable variables
     private static final Object LOCK = new Object();
 
     //@GuardedBy("LOCK")
     private static final Map<String, FileEntry> files = new HashMap<>();
 
     /**
      * Shutdown Hook that ensures PrintWriter is flushed is CTRL+C or kill is called during a test
      */
     //@GuardedBy("LOCK")
     private static Thread shutdownHook;
 
     /*
      * Keep track of the file writer and the configuration,
      * as the instance used to close them is not the same as the instance that creates
      * them. This means one cannot use the saved PrintWriter or use getSaveConfig()
      */
     private static class FileEntry{
         final PrintWriter pw;
         final SampleSaveConfiguration config;
         FileEntry(PrintWriter _pw, SampleSaveConfiguration _config){
             pw =_pw;
             config = _config;
         }
     }
 
     /**
      * The instance count is used to keep track of whether any tests are currently running.
      * It's not possible to use the constructor or threadStarted etc as tests may overlap
      * e.g. a remote test may be started,
      * and then a local test started whilst the remote test is still running.
      */
     //@GuardedBy("LOCK")
     private static int instanceCount; // Keep track of how many instances are active
 
     // Instance variables (guarded by volatile)
 
     private transient volatile PrintWriter out;
 
     private volatile boolean inTest = false;
 
     private volatile boolean isStats = false;
 
     /** the summarizer to which this result collector will forward the samples */
     private volatile Summariser summariser;
 
     private static final class ShutdownHook implements Runnable {
 
         @Override
         public void run() {
             log.info("Shutdown hook started");
             synchronized (LOCK) {
                 flushFileOutput();                    
             }
             log.info("Shutdown hook ended");
         }     
     }
     
     /**
      * No-arg constructor.
      */
     public ResultCollector() {
         this(null);
     }
 
     /**
      * Constructor which sets the used {@link Summariser}
      * @param summer The {@link Summariser} to use
      */
     public ResultCollector(Summariser summer) {
         setErrorLogging(false);
         setSuccessOnlyLogging(false);
         setProperty(new ObjectProperty(SAVE_CONFIG, new SampleSaveConfiguration()));
         summariser = summer;
     }
 
     // Ensure that the sample save config is not shared between copied nodes
     // N.B. clone only seems to be used for client-server tests
     @Override
     public Object clone(){
         ResultCollector clone = (ResultCollector) super.clone();
         clone.setSaveConfig((SampleSaveConfiguration)clone.getSaveConfig().clone());
         // Unfortunately AbstractTestElement does not call super.clone()
         clone.summariser = this.summariser;
         return clone;
     }
 
     private void setFilenameProperty(String f) {
         setProperty(FILENAME, f);
     }
 
     /**
      * Get the filename of the file this collector uses
      * 
      * @return The name of the file
      */
     public String getFilename() {
         return getPropertyAsString(FILENAME);
     }
 
     /**
      * Get the state of error logging
      * 
      * @return Flag whether errors should be logged
      */
     public boolean isErrorLogging() {
         return getPropertyAsBoolean(ERROR_LOGGING);
     }
 
     /**
      * Sets error logging flag
      * 
      * @param errorLogging
      *            The flag whether errors should be logged
      */
     public final void setErrorLogging(boolean errorLogging) {
         setProperty(new BooleanProperty(ERROR_LOGGING, errorLogging));
     }
 
     /**
      * Sets the flag whether only successful samples should be logged
      * 
      * @param value
      *            The flag whether only successful samples should be logged
      */
     public final void setSuccessOnlyLogging(boolean value) {
         if (value) {
             setProperty(new BooleanProperty(SUCCESS_ONLY_LOGGING, true));
         } else {
             removeProperty(SUCCESS_ONLY_LOGGING);
         }
     }
 
     /**
      * Get the state of successful only logging
      * 
      * @return Flag whether only successful samples should be logged
      */
     public boolean isSuccessOnlyLogging() {
         return getPropertyAsBoolean(SUCCESS_ONLY_LOGGING,false);
     }
 
     /**
      * Decides whether or not to a sample is wanted based on:
      * <ul>
      * <li>errorOnly</li>
      * <li>successOnly</li>
      * <li>sample success</li>
      * </ul>
      * Should only be called for single samples.
      *
      * @param success is sample successful
      * @return whether to log/display the sample
      */
     public boolean isSampleWanted(boolean success){
         boolean errorOnly = isErrorLogging();
         boolean successOnly = isSuccessOnlyLogging();
         return isSampleWanted(success, errorOnly, successOnly);
     }
 
     /**
      * Decides whether or not to a sample is wanted based on:
      * <ul>
      * <li>errorOnly</li>
      * <li>successOnly</li>
      * <li>sample success</li>
      * </ul>
      * This version is intended to be called by code that loops over many samples;
      * it is cheaper than fetching the settings each time.
      * @param success status of sample
      * @param errorOnly if errors only wanted
      * @param successOnly if success only wanted
      * @return whether to log/display the sample
      */
     public static boolean isSampleWanted(boolean success, boolean errorOnly,
             boolean successOnly) {
         return (!errorOnly && !successOnly) ||
                (success && successOnly) ||
                (!success && errorOnly);
         // successOnly and errorOnly cannot both be set
     }
     /**
      * Sets the filename attribute of the ResultCollector object.
      *
      * @param f
      *            the new filename value
      */
     public void setFilename(String f) {
         if (inTest) {
             return;
         }
         setFilenameProperty(f);
     }
 
     @Override
     public void testEnded(String host) {
         synchronized(LOCK){
             instanceCount--;
             if (instanceCount <= 0) {
                 // No need for the hook now
                 // Bug 57088 - prevent (im?)possible NPE
                 if (shutdownHook != null) {
                     Runtime.getRuntime().removeShutdownHook(shutdownHook);
                 } else {
                     log.warn("Should not happen: shutdownHook==null, instanceCount=" + instanceCount);
                 }
                 finalizeFileOutput();
                 inTest = false;
             }
         }
 
         if(summariser != null) {
             summariser.testEnded(host);
         }
     }
 
     @Override
     public void testStarted(String host) {
         synchronized(LOCK){
             if (instanceCount == 0) { // Only add the hook once
                 shutdownHook = new Thread(new ShutdownHook());
                 Runtime.getRuntime().addShutdownHook(shutdownHook);
             }
             instanceCount++;
             try {
                 initializeFileOutput();
                 if (getVisualizer() != null) {
                     this.isStats = getVisualizer().isStats();
                 }
             } catch (Exception e) {
                 log.error("", e);
             }
         }
         inTest = true;
 
         if(summariser != null) {
             summariser.testStarted(host);
         }
     }
 
     @Override
     public void testEnded() {
         testEnded(TEST_IS_LOCAL);
     }
 
     @Override
     public void testStarted() {
         testStarted(TEST_IS_LOCAL);
     }
 
     /**
      * Loads an existing sample data (JTL) file.
      * This can be one of:
      * <ul>
      *   <li>XStream format</li>
-     *   <li>Avalon format</li>
      *   <li>CSV format</li>
      * </ul>
      *
      */
     public void loadExistingFile() {
         final Visualizer visualizer = getVisualizer();
         if (visualizer == null) {
             return; // No point reading the file if there's no visualiser
         }
         boolean parsedOK = false;
         String filename = getFilename();
         File file = new File(filename);
         if (file.exists()) {
             BufferedReader dataReader = null;
             BufferedInputStream bufferedInputStream = null;
             try {
                 dataReader = new BufferedReader(new FileReader(file)); // TODO Charset ?
                 // Get the first line, and see if it is XML
                 String line = dataReader.readLine();
                 dataReader.close();
                 dataReader = null;
                 if (line == null) {
                     log.warn(filename+" is empty");
                 } else {
                     if (!line.startsWith("<?xml ")){// No, must be CSV //$NON-NLS-1$
                         CSVSaveService.processSamples(filename, visualizer, this);
                         parsedOK = true;
                     } else { // We are processing XML
                         try { // Assume XStream
                             bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                             SaveService.loadTestResults(bufferedInputStream,
                                     new ResultCollectorHelper(this, visualizer));
                             parsedOK = true;
                         } catch (ConversionException e) {
-                            final String message = e.getShortMessage();
-                            if (message.startsWith("sampleResult")) { // probably Avalon format
-                                log.info("Failed to load "+filename+" using XStream. Assuming Avalon format, as message was: "+message);
-                                OldSaveService.processSamples(filename, visualizer, this);
-                                parsedOK = true;
-                            } else {
-                                log.warn("Failed to load "+filename+" using XStream. Error was: "+e);
-                            }
+                            log.warn("Failed to load "+filename+" using XStream. Error was: "+e);
                         } catch (Exception e) {
                             log.warn("Failed to load "+filename+" using XStream. Error was: "+e);
                         }
                     }
                 }
             } catch (IOException | JMeterError | RuntimeException | OutOfMemoryError e) {
                 // FIXME Why do we catch OOM ?
                 log.warn("Problem reading JTL file: "+file);
-            } catch (ConfigurationException | SAXException e) { // Avalon only
-                log.warn("Problem reading Avalon JTL file: "+file,e);
             } finally {
                 JOrphanUtils.closeQuietly(dataReader);
                 JOrphanUtils.closeQuietly(bufferedInputStream);
                 if (!parsedOK) {
                     GuiPackage.showErrorMessage(
                                 "Error loading results file - see log file",
                                 "Result file loader");
                 }
             }
         } else {
             GuiPackage.showErrorMessage(
                     "Error loading results file - could not open file",
                     "Result file loader");
         }
     }
 
     private static void writeFileStart(PrintWriter writer, SampleSaveConfiguration saveConfig) {
         if (saveConfig.saveAsXml()) {
             writer.print(XML_HEADER);
             // Write the EOL separately so we generate LF line ends on Unix and Windows
             writer.print("\n"); // $NON-NLS-1$
             String pi=saveConfig.getXmlPi();
             if (pi.length() > 0) {
                 writer.println(pi);
             }
             // Can't do it as a static initialisation, because SaveService
             // is being constructed when this is called
             writer.print(TESTRESULTS_START_V1_1_PREVER);
             writer.print(SaveService.getVERSION());
             writer.print(TESTRESULTS_START_V1_1_POSTVER);
             // Write the EOL separately so we generate LF line ends on Unix and Windows
             writer.print("\n"); // $NON-NLS-1$
         } else if (saveConfig.saveFieldNames()) {
             writer.println(CSVSaveService.printableFieldNamesToString(saveConfig));
         }
     }
 
     private static void writeFileEnd(PrintWriter pw, SampleSaveConfiguration saveConfig) {
         if (saveConfig.saveAsXml()) {
             pw.print("\n"); // $NON-NLS-1$
             pw.print(TESTRESULTS_END);
             pw.print("\n");// Added in version 1.1 // $NON-NLS-1$
         }
     }
 
     private static PrintWriter getFileWriter(String filename, SampleSaveConfiguration saveConfig)
             throws IOException {
         if (filename == null || filename.length() == 0) {
             return null;
         }
         filename = FileServer.resolveBaseRelativeName(filename);
         FileEntry fe = files.get(filename);
         PrintWriter writer = null;
         boolean trimmed = true;
 
         if (fe == null) {
             if (saveConfig.saveAsXml()) {
                 trimmed = trimLastLine(filename);
             } else {
                 trimmed = new File(filename).exists();
             }
             // Find the name of the directory containing the file
             // and create it - if there is one
             File pdir = new File(filename).getParentFile();
             if (pdir != null) {
                 // returns false if directory already exists, so need to check again
                 if(pdir.mkdirs()){
                     log.info("Folder "+pdir.getAbsolutePath()+" was created");
                 } // else if might have been created by another process so not a problem
                 if (!pdir.exists()){
                     log.warn("Error creating directories for "+pdir.toString());
                 }
             }
             writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(filename,
                     trimmed)), SaveService.getFileEncoding("UTF-8")), SAVING_AUTOFLUSH); // $NON-NLS-1$
             log.debug("Opened file: "+filename);
             files.put(filename, new FileEntry(writer, saveConfig));
         } else {
             writer = fe.pw;
         }
         if (!trimmed) {
             writeFileStart(writer, saveConfig);
         }
         return writer;
     }
 
     // returns false if the file did not contain the terminator
     private static boolean trimLastLine(String filename) {
         RandomAccessFile raf = null;
         try {
             raf = new RandomAccessFile(filename, "rw"); // $NON-NLS-1$
             long len = raf.length();
             if (len < MIN_XML_FILE_LEN) {
                 return false;
             }
             raf.seek(len - TESTRESULTS_END.length() - 10);// TODO: may not work on all OSes?
             String line;
             long pos = raf.getFilePointer();
             int end = 0;
             while ((line = raf.readLine()) != null)// reads to end of line OR end of file
             {
                 end = line.indexOf(TESTRESULTS_END);
                 if (end >= 0) // found the string
                 {
                     break;
                 }
                 pos = raf.getFilePointer();
             }
             if (line == null) {
                 log.warn("Unexpected EOF trying to find XML end marker in " + filename);
                 raf.close();
                 return false;
             }
             raf.setLength(pos + end);// Truncate the file
             raf.close();
             raf = null;
         } catch (FileNotFoundException e) {
             return false;
         } catch (IOException e) {
             log.warn("Error trying to find XML terminator " + e.toString());
             return false;
         } finally {
             try {
                 if (raf != null) {
                     raf.close();
                 }
             } catch (IOException e1) {
                 log.info("Could not close " + filename + " " + e1.getLocalizedMessage());
             }
         }
         return true;
     }
 
     @Override
     public void sampleStarted(SampleEvent e) {
     }
 
     @Override
     public void sampleStopped(SampleEvent e) {
     }
 
     /**
      * When a test result is received, display it and save it.
      *
      * @param event
      *            the sample event that was received
      */
     @Override
     public void sampleOccurred(SampleEvent event) {
         SampleResult result = event.getResult();
 
         if (isSampleWanted(result.isSuccessful())) {
             sendToVisualizer(result);
             if (out != null && !isResultMarked(result) && !this.isStats) {
                 SampleSaveConfiguration config = getSaveConfig();
                 result.setSaveConfig(config);
                 try {
                     if (config.saveAsXml()) {
                         SaveService.saveSampleResult(event, out);
                     } else { // !saveAsXml
                         String savee = CSVSaveService.resultToDelimitedString(event);
                         out.println(savee);
                     }
                 } catch (Exception err) {
                     log.error("Error trying to record a sample", err); // should throw exception back to caller
                 }
             }
         }
 
         if(summariser != null) {
             summariser.sampleOccurred(event);
         }
     }
 
     protected final void sendToVisualizer(SampleResult r) {
         if (getVisualizer() != null) {
             getVisualizer().add(r);
         }
     }
 
     /**
      * recordStats is used to save statistics generated by visualizers
      *
      * @param e The data to save
      * @throws IOException when data writing fails
      */
     // Used by: MonitorHealthVisualizer.add(SampleResult res)
     public void recordStats(TestElement e) throws IOException {
         if (out != null) {
             SaveService.saveTestElement(e, out);
         }
     }
 
     /**
      * Checks if the sample result is marked or not, and marks it
      * @param res - the sample result to check
      * @return <code>true</code> if the result was marked
      */
     private boolean isResultMarked(SampleResult res) {
         String filename = getFilename();
         return res.markFile(filename);
     }
 
     private void initializeFileOutput() throws IOException {
 
         String filename = getFilename();
         if (filename != null) {
             if (out == null) {
                 try {
                     out = getFileWriter(filename, getSaveConfig());
                 } catch (FileNotFoundException e) {
                     out = null;
                 }
             }
         }
     }
 
     /**
      * Flush PrintWriter to synchronize file contents
      */
     public void flushFile() {
         if (out != null) {
             log.info("forced flush through ResultCollector#flushFile");
             out.flush();
         }
     }
 
     /**
      * Flush PrintWriter, called by Shutdown Hook to ensure no data is lost
      */
     private static void flushFileOutput() {
         for(Map.Entry<String,ResultCollector.FileEntry> me : files.entrySet()){
             log.debug("Flushing: "+me.getKey());
             FileEntry fe = me.getValue();
             fe.pw.flush();
             if (fe.pw.checkError()){
                 log.warn("Problem detected during use of "+me.getKey());
             }
         }
     }
     
     private void finalizeFileOutput() {
         for(Map.Entry<String,ResultCollector.FileEntry> me : files.entrySet()){
             log.debug("Closing: "+me.getKey());
             FileEntry fe = me.getValue();
             writeFileEnd(fe.pw, fe.config);
             fe.pw.close();
             if (fe.pw.checkError()){
                 log.warn("Problem detected during use of "+me.getKey());
             }
         }
         files.clear();
     }
 
     /**
      * @return Returns the saveConfig.
      */
     public SampleSaveConfiguration getSaveConfig() {
         try {
             return (SampleSaveConfiguration) getProperty(SAVE_CONFIG).getObjectValue();
         } catch (ClassCastException e) {
             setSaveConfig(new SampleSaveConfiguration());
             return getSaveConfig();
         }
     }
 
     /**
      * @param saveConfig
      *            The saveConfig to set.
      */
     public void setSaveConfig(SampleSaveConfiguration saveConfig) {
         getProperty(SAVE_CONFIG).setObjectValue(saveConfig);
     }
 
     // This is required so that
     // @see org.apache.jmeter.gui.tree.JMeterTreeModel.getNodesOfType()
     // can find the Clearable nodes - the userObject has to implement the interface.
     @Override
     public void clearData() {
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/SampleResult.java b/src/core/org/apache/jmeter/samplers/SampleResult.java
index e875323f9..7be00e445 100644
--- a/src/core/org/apache/jmeter/samplers/SampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/SampleResult.java
@@ -1,1403 +1,1403 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.io.UnsupportedEncodingException;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.nio.charset.Charset;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 // For unit tests, @see TestSampleResult
 
 /**
  * This is a nice packaging for the various information returned from taking a
  * sample of an entry.
  *
  */
 public class SampleResult implements Serializable, Cloneable {
 
     private static final long serialVersionUID = 241L;
 
     // Needs to be accessible from Test code
     static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * The default encoding to be used if not overridden.
      * The value is ISO-8859-1.
      */
     public static final String DEFAULT_HTTP_ENCODING = "ISO-8859-1";  // $NON-NLS-1$
 
     // Bug 33196 - encoding ISO-8859-1 is only suitable for Western countries
     // However the suggested System.getProperty("file.encoding") is Cp1252 on
     // Windows
     // So use a new property with the original value as default
     // needs to be accessible from test code
     /**
      * The default encoding to be used to decode the responseData byte array.
      * The value is defined by the property "sampleresult.default.encoding"
      * with a default of DEFAULT_HTTP_ENCODING if that is not defined.
      */
     protected static final String DEFAULT_ENCODING
             = JMeterUtils.getPropDefault("sampleresult.default.encoding", // $NON-NLS-1$
             DEFAULT_HTTP_ENCODING);
 
     /* The default used by {@link #setResponseData(String, String)} */
     private static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
 
     /**
      * Data type value ({@value}) indicating that the response data is text.
      *
      * @see #getDataType
      * @see #setDataType(java.lang.String)
      */
     public static final String TEXT = "text"; // $NON-NLS-1$
 
     /**
      * Data type value ({@value}) indicating that the response data is binary.
      *
      * @see #getDataType
      * @see #setDataType(java.lang.String)
      */
     public static final String BINARY = "bin"; // $NON-NLS-1$
 
     /** empty array which can be returned instead of null */
     public static final byte[] EMPTY_BA = new byte[0];
 
     private static final SampleResult[] EMPTY_SR = new SampleResult[0];
 
     private static final AssertionResult[] EMPTY_AR = new AssertionResult[0];
     
     private static final boolean GETBYTES_BODY_REALSIZE = 
         JMeterUtils.getPropDefault("sampleresult.getbytes.body_real_size", true); // $NON-NLS-1$
 
     private static final boolean GETBYTES_HEADERS_SIZE = 
         JMeterUtils.getPropDefault("sampleresult.getbytes.headers_size", true); // $NON-NLS-1$
     
     private static final boolean GETBYTES_NETWORK_SIZE =
             GETBYTES_HEADERS_SIZE && GETBYTES_BODY_REALSIZE;
 
     private SampleSaveConfiguration saveConfig;
 
     private SampleResult parent = null;
 
     /**
      * @param propertiesToSave
      *            The propertiesToSave to set.
      */
     public void setSaveConfig(SampleSaveConfiguration propertiesToSave) {
         this.saveConfig = propertiesToSave;
     }
 
     public SampleSaveConfiguration getSaveConfig() {
         return saveConfig;
     }
 
     private byte[] responseData = EMPTY_BA;
 
     private String responseCode = "";// Never return null
 
     private String label = "";// Never return null
 
     /** Filename used by ResultSaver */
     private String resultFileName = "";
 
     /** The data used by the sampler */
     private String samplerData;
 
     private String threadName = ""; // Never return null
 
     private String responseMessage = "";
 
     private String responseHeaders = ""; // Never return null
 
     private String contentType = ""; // e.g. text/html; charset=utf-8
 
     private String requestHeaders = "";
 
     // TODO timeStamp == 0 means either not yet initialised or no stamp available (e.g. when loading a results file)
     /** the time stamp - can be start or end */
     private long timeStamp = 0;
 
     private long startTime = 0;
 
     private long endTime = 0;
 
     private long idleTime = 0;// Allow for non-sample time
 
     /** Start of pause (if any) */
     private long pauseTime = 0;
 
     private List<AssertionResult> assertionResults;
 
     private List<SampleResult> subResults;
 
     /**
      * The data type of the sample
      * @see #getDataType()
      * @see #setDataType(String)
      * @see #TEXT
      * @see #BINARY
      */
     private String dataType=""; // Don't return null if not set
 
     private boolean success;
 
     //@GuardedBy("this"")
     /** files that this sample has been saved in */
     /** In Non GUI mode and when best config is used, size never exceeds 1, 
      * but as a compromise set it to 3 
      */
     private final Set<String> files = new HashSet<>(3);
 
     private String dataEncoding;// (is this really the character set?) e.g.
                                 // ISO-8895-1, UTF-8
 
     /** elapsed time */
     private long elapsedTime = 0;
 
     /** time to first response */
     private long latency = 0;
 
     /**
      * time to end connecting
      */
     private long connectTime = 0;
 
     /** Should thread start next iteration ? */
     private boolean startNextThreadLoop = false;
 
     /** Should thread terminate? */
     private boolean stopThread = false;
 
     /** Should test terminate? */
     private boolean stopTest = false;
 
     /** Should test terminate abruptly? */
     private boolean stopTestNow = false;
 
     /** Is the sampler acting as a monitor? */
     private boolean isMonitor = false;
 
     private int sampleCount = 1;
 
     private int bytes = 0; // Allows override of sample size in case sampler does not want to store all the data
     
     private int headersSize = 0;
     
     private int bodySize = 0;
 
     /** Currently active threads in this thread group */
     private volatile int groupThreads = 0;
 
     /** Currently active threads in all thread groups */
     private volatile int allThreads = 0;
 
     // TODO do contentType and/or dataEncoding belong in HTTPSampleResult instead?
 
     private static final boolean startTimeStamp
         = JMeterUtils.getPropDefault("sampleresult.timestamp.start", false);  // $NON-NLS-1$
 
     // Allow read-only access from test code
     static final boolean USENANOTIME
     = JMeterUtils.getPropDefault("sampleresult.useNanoTime", true);  // $NON-NLS-1$
 
     // How long between checks of nanotime; default 5000ms; set to <=0 to disable the thread
     private static final long NANOTHREAD_SLEEP = 
             JMeterUtils.getPropDefault("sampleresult.nanoThreadSleep", 5000);  // $NON-NLS-1$;
 
     static {
         if (startTimeStamp) {
             log.info("Note: Sample TimeStamps are START times");
         } else {
             log.info("Note: Sample TimeStamps are END times");
         }
         log.info("sampleresult.default.encoding is set to " + DEFAULT_ENCODING);
         log.info("sampleresult.useNanoTime="+USENANOTIME);
         log.info("sampleresult.nanoThreadSleep="+NANOTHREAD_SLEEP);
 
         if (USENANOTIME && NANOTHREAD_SLEEP > 0) {
             // Make sure we start with a reasonable value
             NanoOffset.nanoOffset = System.currentTimeMillis() - SampleResult.sampleNsClockInMs();
             NanoOffset nanoOffset = new NanoOffset();
             nanoOffset.setDaemon(true);
             nanoOffset.setName("NanoOffset");
             nanoOffset.start();
         }
     }
 
 
     private final long nanoTimeOffset;
 
     // Allow testcode access to the settings
     final boolean useNanoTime;
     
     final long nanoThreadSleep;
     
     /**
      * Cache for responseData as string to avoid multiple computations
      */
     private volatile transient String responseDataAsString;
     
     private long initOffset(){
         if (useNanoTime){
             return nanoThreadSleep > 0 ? NanoOffset.getNanoOffset() : System.currentTimeMillis() - sampleNsClockInMs();
         } else {
             return Long.MIN_VALUE;
         }
     }
 
     public SampleResult() {
         this(USENANOTIME, NANOTHREAD_SLEEP);
     }
 
     // Allow test code to change the default useNanoTime setting
     SampleResult(boolean nanoTime) {
         this(nanoTime, NANOTHREAD_SLEEP);
     }
 
     // Allow test code to change the default useNanoTime and nanoThreadSleep settings
     SampleResult(boolean nanoTime, long nanoThreadSleep) {
         this.elapsedTime = 0;
         this.useNanoTime = nanoTime;
         this.nanoThreadSleep = nanoThreadSleep;
         this.nanoTimeOffset = initOffset();
     }
 
     /**
      * Copy constructor.
      * 
      * @param res existing sample result
      */
     public SampleResult(SampleResult res) {
         this();
         allThreads = res.allThreads;//OK
         assertionResults = res.assertionResults;// TODO ??
         bytes = res.bytes;
         headersSize = res.headersSize;
         bodySize = res.bodySize;
         contentType = res.contentType;//OK
         dataEncoding = res.dataEncoding;//OK
         dataType = res.dataType;//OK
         endTime = res.endTime;//OK
         // files is created automatically, and applies per instance
         groupThreads = res.groupThreads;//OK
         idleTime = res.idleTime;
         isMonitor = res.isMonitor;
         label = res.label;//OK
         latency = res.latency;
         connectTime = res.connectTime;
         location = res.location;//OK
         parent = res.parent; // TODO ??
         pauseTime = res.pauseTime;
         requestHeaders = res.requestHeaders;//OK
         responseCode = res.responseCode;//OK
         responseData = res.responseData;//OK
         responseDataAsString = null;
         responseHeaders = res.responseHeaders;//OK
         responseMessage = res.responseMessage;//OK
         // Don't copy this; it is per instance resultFileName = res.resultFileName;
         sampleCount = res.sampleCount;
         samplerData = res.samplerData;
         saveConfig = res.saveConfig;
         startTime = res.startTime;//OK
         stopTest = res.stopTest;
         stopTestNow = res.stopTestNow;
         stopThread = res.stopThread;
         startNextThreadLoop = res.startNextThreadLoop;
         subResults = res.subResults; // TODO ??
         success = res.success;//OK
         threadName = res.threadName;//OK
         elapsedTime = res.elapsedTime;
         timeStamp = res.timeStamp;
     }
 
     public boolean isStampedAtStart() {
         return startTimeStamp;
     }
 
     /**
      * Create a sample with a specific elapsed time but don't allow the times to
      * be changed later
      *
      * (only used by HTTPSampleResult)
      *
      * @param elapsed
      *            time
      * @param atend
      *            create the sample finishing now, else starting now
      */
     protected SampleResult(long elapsed, boolean atend) {
         this();
         long now = currentTimeInMillis();
         if (atend) {
             setTimes(now - elapsed, now);
         } else {
             setTimes(now, now + elapsed);
         }
     }
 
     /**
      * Create a sample with specific start and end times for test purposes, but
      * don't allow the times to be changed later
      *
      * (used by StatVisualizerModel.Test)
      *
      * @param start
      *            start time in milliseconds since unix epoch
      * @param end
      *            end time in milliseconds since unix epoch
      * @return sample with given start and end time
      */
     public static SampleResult createTestSample(long start, long end) {
         SampleResult res = new SampleResult();
         res.setStartTime(start);
         res.setEndTime(end);
         return res;
     }
 
     /**
      * Create a sample with a specific elapsed time for test purposes, but don't
      * allow the times to be changed later
      *
      * @param elapsed
      *            - desired elapsed time in milliseconds
      * @return sample that starts 'now' and ends <code>elapsed</code> milliseconds later
      */
     public static SampleResult createTestSample(long elapsed) {
         long now = System.currentTimeMillis();
         return createTestSample(now, now + elapsed);
     }
 
     /**
      * Allow users to create a sample with specific timestamp and elapsed times
      * for cloning purposes, but don't allow the times to be changed later
      *
-     * Currently used by OldSaveService, CSVSaveService and
+     * Currently used by CSVSaveService and
      * StatisticalSampleResult
      *
      * @param stamp
      *            this may be a start time or an end time (both in
      *            milliseconds)
      * @param elapsed
      *            time in milliseconds
      */
     public SampleResult(long stamp, long elapsed) {
         this();
         stampAndTime(stamp, elapsed);
     }
 
     private static long sampleNsClockInMs() {
         return System.nanoTime() / 1000000;
     }
 
     /**
      * Helper method to get 1 ms resolution timing.
      * 
      * @return the current time in milliseconds
      * @throws RuntimeException
      *             when <code>useNanoTime</code> is <code>true</code> but
      *             <code>nanoTimeOffset</code> is not set
      */
     public long currentTimeInMillis() {
         if (useNanoTime){
             if (nanoTimeOffset == Long.MIN_VALUE){
                 throw new RuntimeException("Invalid call; nanoTimeOffset as not been set");
             }
             return sampleNsClockInMs() + nanoTimeOffset;            
         }
         return System.currentTimeMillis();
     }
 
     // Helper method to maintain timestamp relationships
     private void stampAndTime(long stamp, long elapsed) {
         if (startTimeStamp) {
             startTime = stamp;
             endTime = stamp + elapsed;
         } else {
             startTime = stamp - elapsed;
             endTime = stamp;
         }
         timeStamp = stamp;
         elapsedTime = elapsed;
     }
 
     /**
      * For use by SaveService only.
      * 
      * @param stamp
      *            this may be a start time or an end time (both in milliseconds)
      * @param elapsed
      *            time in milliseconds
      * @throws RuntimeException
      *             when <code>startTime</code> or <code>endTime</code> has been
      *             set already
      */
     public void setStampAndTime(long stamp, long elapsed) {
         if (startTime != 0 || endTime != 0){
             throw new RuntimeException("Calling setStampAndTime() after start/end times have been set");
         }
         stampAndTime(stamp, elapsed);
     }
 
     /**
      * Set the "marked" flag to show that the result has been written to the file.
      *
      * @param filename the name of the file
      * @return <code>true</code> if the result was previously marked
      */
     public synchronized boolean markFile(String filename) {
         return !files.add(filename);
     }
 
     public String getResponseCode() {
         return responseCode;
     }
 
     private static final String OK_CODE = Integer.toString(HttpURLConnection.HTTP_OK);
     private static final String OK_MSG = "OK"; // $NON-NLS-1$
 
     /**
      * Set response code to OK, i.e. "200"
      *
      */
     public void setResponseCodeOK(){
         responseCode=OK_CODE;
     }
 
     public void setResponseCode(String code) {
         responseCode = code;
     }
 
     public boolean isResponseCodeOK(){
         return responseCode.equals(OK_CODE);
     }
     public String getResponseMessage() {
         return responseMessage;
     }
 
     public void setResponseMessage(String msg) {
         responseMessage = msg;
     }
 
     public void setResponseMessageOK() {
         responseMessage = OK_MSG;
     }
 
     /**
      * Set result statuses OK - shorthand method to set:
      * <ul>
      * <li>ResponseCode</li>
      * <li>ResponseMessage</li>
      * <li>Successful status</li>
      * </ul>
      */
     public void setResponseOK(){
         setResponseCodeOK();
         setResponseMessageOK();
         setSuccessful(true);
     }
 
     public String getThreadName() {
         return threadName;
     }
 
     public void setThreadName(String threadName) {
         this.threadName = threadName;
     }
 
     /**
      * Get the sample timestamp, which may be either the start time or the end time.
      *
      * @see #getStartTime()
      * @see #getEndTime()
      *
      * @return timeStamp in milliseconds
      */
     public long getTimeStamp() {
         return timeStamp;
     }
 
     public String getSampleLabel() {
         return label;
     }
 
     /**
      * Get the sample label for use in summary reports etc.
      *
      * @param includeGroup whether to include the thread group name
      * @return the label
      */
     public String getSampleLabel(boolean includeGroup) {
         if (includeGroup) {
             StringBuilder sb = new StringBuilder(threadName.substring(0,threadName.lastIndexOf(' '))); //$NON-NLS-1$
             return sb.append(":").append(label).toString(); //$NON-NLS-1$
         }
         return label;
     }
 
     public void setSampleLabel(String label) {
         this.label = label;
     }
 
     public void addAssertionResult(AssertionResult assertResult) {
         if (assertionResults == null) {
             assertionResults = new ArrayList<>();
         }
         assertionResults.add(assertResult);
     }
 
     /**
      * Gets the assertion results associated with this sample.
      *
      * @return an array containing the assertion results for this sample.
      *         Returns empty array if there are no assertion results.
      */
     public AssertionResult[] getAssertionResults() {
         if (assertionResults == null) {
             return EMPTY_AR;
         }
         return assertionResults.toArray(new AssertionResult[assertionResults.size()]);
     }
 
     /**
      * Add a subresult and adjust the parent byte count and end-time.
      * 
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void addSubResult(SampleResult subResult) {
         if(subResult == null) {
             // see https://bz.apache.org/bugzilla/show_bug.cgi?id=54778
             return;
         }
         String tn = getThreadName();
         if (tn.length()==0) {
             tn=Thread.currentThread().getName();//TODO do this more efficiently
             this.setThreadName(tn);
         }
         subResult.setThreadName(tn); // TODO is this really necessary?
 
         // Extend the time to the end of the added sample
         setEndTime(Math.max(getEndTime(), subResult.getEndTime() + nanoTimeOffset - subResult.nanoTimeOffset)); // Bug 51855
         // Include the byte count for the added sample
         setBytes(getBytes() + subResult.getBytes());
         setHeadersSize(getHeadersSize() + subResult.getHeadersSize());
         setBodySize(getBodySize() + subResult.getBodySize());
         addRawSubResult(subResult);
     }
     
     /**
      * Add a subresult to the collection without updating any parent fields.
      * 
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void addRawSubResult(SampleResult subResult){
         storeSubResult(subResult);
     }
 
     /**
      * Add a subresult read from a results file.
      * <p>
      * As for {@link SampleResult#addSubResult(SampleResult)
      * addSubResult(SampleResult)}, except that the fields don't need to be
      * accumulated
      *
      * @param subResult
      *            the {@link SampleResult} to be added
      */
     public void storeSubResult(SampleResult subResult) {
         if (subResults == null) {
             subResults = new ArrayList<>();
         }
         subResults.add(subResult);
         subResult.setParent(this);
     }
 
     /**
      * Gets the subresults associated with this sample.
      *
      * @return an array containing the subresults for this sample. Returns an
      *         empty array if there are no subresults.
      */
     public SampleResult[] getSubResults() {
         if (subResults == null) {
             return EMPTY_SR;
         }
         return subResults.toArray(new SampleResult[subResults.size()]);
     }
 
     /**
      * Sets the responseData attribute of the SampleResult object.
      *
      * If the parameter is null, then the responseData is set to an empty byte array.
      * This ensures that getResponseData() can never be null.
      *
      * @param response
      *            the new responseData value
      */
     public void setResponseData(byte[] response) {
         responseDataAsString = null;
         responseData = response == null ? EMPTY_BA : response;
     }
 
     /**
      * Sets the responseData attribute of the SampleResult object.
      * Should only be called after setting the dataEncoding (if necessary)
      *
      * @param response
      *            the new responseData value (String)
      *
      * @deprecated - only intended for use from BeanShell code
      */
     @Deprecated
     public void setResponseData(String response) {
         responseDataAsString = null;
         try {
             responseData = response.getBytes(getDataEncodingWithDefault());
         } catch (UnsupportedEncodingException e) {
             log.warn("Could not convert string, using default encoding. "+e.getLocalizedMessage());
             responseData = response.getBytes(); // N.B. default charset is used deliberately here
         }
     }
 
     /**
      * Sets the encoding and responseData attributes of the SampleResult object.
      *
      * @param response the new responseData value (String)
      * @param encoding the encoding to set and then use (if null, use platform default)
      *
      */
     public void setResponseData(final String response, final String encoding) {
         responseDataAsString = null;
         String encodeUsing = encoding != null? encoding : DEFAULT_CHARSET;
         try {
             responseData = response.getBytes(encodeUsing);
             setDataEncoding(encodeUsing);
         } catch (UnsupportedEncodingException e) {
             log.warn("Could not convert string using '"+encodeUsing+
                     "', using default encoding: "+DEFAULT_CHARSET,e);
             responseData = response.getBytes(); // N.B. default charset is used deliberately here
             setDataEncoding(DEFAULT_CHARSET);
         }
     }
 
     /**
      * Gets the responseData attribute of the SampleResult object.
      * <p>
      * Note that some samplers may not store all the data, in which case
      * getResponseData().length will be incorrect.
      *
      * Instead, always use {@link #getBytes()} to obtain the sample result byte count.
      * </p>
      * @return the responseData value (cannot be null)
      */
     public byte[] getResponseData() {
         return responseData;
     }
 
     /**
      * Gets the responseData of the SampleResult object as a String
      *
      * @return the responseData value as a String, converted according to the encoding
      */
     public String getResponseDataAsString() {
         try {
             if(responseDataAsString == null) {
                 responseDataAsString= new String(responseData,getDataEncodingWithDefault());
             }
             return responseDataAsString;
         } catch (UnsupportedEncodingException e) {
             log.warn("Using platform default as "+getDataEncodingWithDefault()+" caused "+e);
             return new String(responseData); // N.B. default charset is used deliberately here
         }
     }
 
     public void setSamplerData(String s) {
         samplerData = s;
     }
 
     public String getSamplerData() {
         return samplerData;
     }
 
     /**
      * Get the time it took this sample to occur.
      *
      * @return elapsed time in milliseonds
      *
      */
     public long getTime() {
         return elapsedTime;
     }
 
     public boolean isSuccessful() {
         return success;
     }
 
     /**
      * Sets the data type of the sample.
      * @param dataType String containing {@link #BINARY} or {@link #TEXT}
      * @see #BINARY
      * @see #TEXT
      */
     public void setDataType(String dataType) {
         this.dataType = dataType;
     }
 
     /**
      * Returns the data type of the sample.
      * 
      * @return String containing {@link #BINARY} or {@link #TEXT} or the empty string
      * @see #BINARY
      * @see #TEXT
      */
     public String getDataType() {
         return dataType;
     }
 
     /**
      * Extract and save the DataEncoding and DataType from the parameter provided.
      * Does not save the full content Type.
      * @see #setContentType(String) which should be used to save the full content-type string
      *
      * @param ct - content type (may be null)
      */
     public void setEncodingAndType(String ct){
         if (ct != null) {
             // Extract charset and store as DataEncoding
             // N.B. The meta tag:
             // <META http-equiv="content-type" content="text/html; charset=foobar">
             // is now processed by HTTPSampleResult#getDataEncodingWithDefault
             final String CS_PFX = "charset="; // $NON-NLS-1$
             int cset = ct.toLowerCase(java.util.Locale.ENGLISH).indexOf(CS_PFX);
             if (cset >= 0) {
                 String charSet = ct.substring(cset + CS_PFX.length());
                 // handle: ContentType: text/plain; charset=ISO-8859-1; format=flowed
                 int semiColon = charSet.indexOf(';');
                 if (semiColon >= 0) {
                     charSet=charSet.substring(0, semiColon);
                 }
                 // Check for quoted string
                 if (charSet.startsWith("\"")||charSet.startsWith("\'")){ // $NON-NLS-1$
                     setDataEncoding(charSet.substring(1, charSet.length()-1)); // remove quotes
                 } else {
                     setDataEncoding(charSet);
                 }
             }
             if (isBinaryType(ct)) {
                 setDataType(BINARY);
             } else {
                 setDataType(TEXT);
             }
         }
     }
 
     // List of types that are known to be binary
     private static final String[] BINARY_TYPES = {
         "image/",       //$NON-NLS-1$
         "audio/",       //$NON-NLS-1$
         "video/",       //$NON-NLS-1$
         };
 
     // List of types that are known to be ascii, although they may appear to be binary
     private static final String[] NON_BINARY_TYPES = {
         "audio/x-mpegurl",  //$NON-NLS-1$ (HLS Media Manifest)
         "video/f4m"         //$NON-NLS-1$ (Flash Media Manifest)
         };
 
     /*
      * Determine if content-type is known to be binary, i.e. not displayable as text.
      *
      * @param ct content type
      * @return true if content-type is of type binary.
      */
     private static boolean isBinaryType(String ct){
         for (String entry : NON_BINARY_TYPES){
             if (ct.startsWith(entry)){
                 return false;
             }
         }
         for (String binaryType : BINARY_TYPES) {
             if (ct.startsWith(binaryType)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Sets the successful attribute of the SampleResult object.
      *
      * @param success
      *            the new successful value
      */
     public void setSuccessful(boolean success) {
         this.success = success;
     }
 
     /**
      * Returns the display name.
      *
      * @return display name of this sample result
      */
     @Override
     public String toString() {
         return getSampleLabel();
     }
 
     /**
      * Returns the dataEncoding or the default if no dataEncoding was provided.
      * 
      * @return the value of the dataEncoding or DEFAULT_ENCODING
      */
     public String getDataEncodingWithDefault() {
         return getDataEncodingWithDefault(DEFAULT_ENCODING);
     }
 
     /**
      * Returns the dataEncoding or the default if no dataEncoding was provided.
      * 
      * @param defaultEncoding the default to be applied
      * @return the value of the dataEncoding or the provided default
      */
     protected String getDataEncodingWithDefault(String defaultEncoding) {
         if (dataEncoding != null && dataEncoding.length() > 0) {
             return dataEncoding;
         }
         return defaultEncoding;
     }
 
     /**
      * Returns the dataEncoding. May be null or the empty String.
      * @return the value of the dataEncoding
      */
     public String getDataEncodingNoDefault() {
         return dataEncoding;
     }
 
     /**
      * Sets the dataEncoding.
      *
      * @param dataEncoding
      *            the dataEncoding to set, e.g. ISO-8895-1, UTF-8
      */
     public void setDataEncoding(String dataEncoding) {
         this.dataEncoding = dataEncoding;
     }
 
     /**
      * @return whether to stop the test
      */
     public boolean isStopTest() {
         return stopTest;
     }
 
     /**
      * @return whether to stop the test now
      */
     public boolean isStopTestNow() {
         return stopTestNow;
     }
 
     /**
      * @return whether to stop this thread
      */
     public boolean isStopThread() {
         return stopThread;
     }
 
     public void setStopTest(boolean b) {
         stopTest = b;
     }
 
     public void setStopTestNow(boolean b) {
         stopTestNow = b;
     }
 
     public void setStopThread(boolean b) {
         stopThread = b;
     }
 
     /**
      * @return the request headers
      */
     public String getRequestHeaders() {
         return requestHeaders;
     }
 
     /**
      * @return the response headers
      */
     public String getResponseHeaders() {
         return responseHeaders;
     }
 
     /**
      * @param string -
      *            request headers
      */
     public void setRequestHeaders(String string) {
         requestHeaders = string;
     }
 
     /**
      * @param string -
      *            response headers
      */
     public void setResponseHeaders(String string) {
         responseHeaders = string;
     }
 
     /**
      * @return the full content type - e.g. text/html [;charset=utf-8 ]
      */
     public String getContentType() {
         return contentType;
     }
 
     /**
      * Get the media type from the Content Type
      * @return the media type - e.g. text/html (without charset, if any)
      */
     public String getMediaType() {
         return JOrphanUtils.trim(contentType," ;").toLowerCase(java.util.Locale.ENGLISH);
     }
 
     /**
      * Stores the content-type string, e.g. <code>text/xml; charset=utf-8</code>
      * @see #setEncodingAndType(String) which can be used to extract the charset.
      *
      * @param string the content-type to be set
      */
     public void setContentType(String string) {
         contentType = string;
     }
 
     /**
      * @return idleTime
      */
     public long getIdleTime() {
         return idleTime;
     }
 
     /**
      * @return the end time
      */
     public long getEndTime() {
         return endTime;
     }
 
     /**
      * @return the start time
      */
     public long getStartTime() {
         return startTime;
     }
 
     /*
      * Helper methods N.B. setStartTime must be called before setEndTime
      *
      * setStartTime is used by HTTPSampleResult to clone the parent sampler and
      * allow the original start time to be kept
      */
     protected final void setStartTime(long start) {
         startTime = start;
         if (startTimeStamp) {
             timeStamp = startTime;
         }
     }
 
     public void setEndTime(long end) {
         endTime = end;
         if (!startTimeStamp) {
             timeStamp = endTime;
         }
         if (startTime == 0) {
             log.error("setEndTime must be called after setStartTime", new Throwable("Invalid call sequence"));
             // TODO should this throw an error?
         } else {
             elapsedTime = endTime - startTime - idleTime;
         }
     }
 
     /**
      * Set idle time pause.
      * For use by SampleResultConverter/CSVSaveService.
      * @param idle long
      */
     public void setIdleTime(long idle) {
         idleTime = idle;
     }
 
     private void setTimes(long start, long end) {
         setStartTime(start);
         setEndTime(end);
     }
 
     /**
      * Record the start time of a sample
      *
      */
     public void sampleStart() {
         if (startTime == 0) {
             setStartTime(currentTimeInMillis());
         } else {
             log.error("sampleStart called twice", new Throwable("Invalid call sequence"));
         }
     }
 
     /**
      * Record the end time of a sample and calculate the elapsed time
      *
      */
     public void sampleEnd() {
         if (endTime == 0) {
             setEndTime(currentTimeInMillis());
         } else {
             log.error("sampleEnd called twice", new Throwable("Invalid call sequence"));
         }
     }
 
     /**
      * Pause a sample
      *
      */
     public void samplePause() {
         if (pauseTime != 0) {
             log.error("samplePause called twice", new Throwable("Invalid call sequence"));
         }
         pauseTime = currentTimeInMillis();
     }
 
     /**
      * Resume a sample
      *
      */
     public void sampleResume() {
         if (pauseTime == 0) {
             log.error("sampleResume without samplePause", new Throwable("Invalid call sequence"));
         }
         idleTime += currentTimeInMillis() - pauseTime;
         pauseTime = 0;
     }
 
     /**
      * When a Sampler is working as a monitor
      *
      * @param monitor
      *            flag whether this sampler is working as a monitor
      */
     public void setMonitor(boolean monitor) {
         isMonitor = monitor;
     }
 
     /**
      * If the sampler is a monitor, method will return true.
      *
      * @return true if the sampler is a monitor
      */
     public boolean isMonitor() {
         return isMonitor;
     }
 
     /**
      * The statistical sample sender aggregates several samples to save on
      * transmission costs.
      * 
      * @param count number of samples represented by this instance
      */
     public void setSampleCount(int count) {
         sampleCount = count;
     }
 
     /**
      * return the sample count. by default, the value is 1.
      *
      * @return the sample count
      */
     public int getSampleCount() {
         return sampleCount;
     }
 
     /**
      * Returns the count of errors.
      *
      * @return 0 - or 1 if the sample failed
      * 
      * TODO do we need allow for nested samples?
      */
     public int getErrorCount(){
         return success ? 0 : 1;
     }
 
     public void setErrorCount(int i){// for reading from CSV files
         // ignored currently
     }
 
     /*
      * TODO: error counting needs to be sorted out.
      *
      * At present the Statistical Sampler tracks errors separately
      * It would make sense to move the error count here, but this would
      * mean lots of changes.
      * It's also tricky maintaining the count - it can't just be incremented/decremented
      * when the success flag is set as this may be done multiple times.
      * The work-round for now is to do the work in the StatisticalSampleResult,
      * which overrides this method.
      * Note that some JMS samplers also create samples with > 1 sample count
      * Also the Transaction Controller probably needs to be changed to do
      * proper sample and error accounting.
      * The purpose of this work-round is to allow at least minimal support for
      * errors in remote statistical batch mode.
      *
      */
     /**
      * In the event the sampler does want to pass back the actual contents, we
      * still want to calculate the throughput. The bytes are the bytes of the
      * response data.
      *
      * @param length
      *            the number of bytes of the response data for this sample
      */
     public void setBytes(int length) {
         bytes = length;
     }
 
     /**
      * return the bytes returned by the response.
      *
      * @return byte count
      */
     public int getBytes() {
         if (GETBYTES_NETWORK_SIZE) {
             int tmpSum = this.getHeadersSize() + this.getBodySize();
             return tmpSum == 0 ? bytes : tmpSum;
         } else if (GETBYTES_HEADERS_SIZE) {
             return this.getHeadersSize();
         } else if (GETBYTES_BODY_REALSIZE) {
             return this.getBodySize();
         }
         return bytes == 0 ? responseData.length : bytes;
     }
 
     /**
      * @return Returns the latency.
      */
     public long getLatency() {
         return latency;
     }
 
     /**
      * Set the time to the first response
      *
      */
     public void latencyEnd() {
         latency = currentTimeInMillis() - startTime - idleTime;
     }
 
     /**
      * This is only intended for use by SampleResultConverter!
      *
      * @param latency
      *            The latency to set.
      */
     public void setLatency(long latency) {
         this.latency = latency;
     }
 
     /**
      * @return Returns the connect time.
      */
     public long getConnectTime() {
         return connectTime;
     }
 
     /**
      * Set the time to the end of connecting
      */
     public void connectEnd() {
         connectTime = currentTimeInMillis() - startTime - idleTime;
     }
 
     /**
      * This is only intended for use by SampleResultConverter!
      *
      * @param time The connect time to set.
      */
     public void setConnectTime(long time) {
         this.connectTime = time;
     }
 
     /**
      * This is only intended for use by SampleResultConverter!
      *
      * @param timeStamp
      *            The timeStamp to set.
      */
     public void setTimeStamp(long timeStamp) {
         this.timeStamp = timeStamp;
     }
 
     private URL location;
 
     public void setURL(URL location) {
         this.location = location;
     }
 
     public URL getURL() {
         return location;
     }
 
     /**
      * Get a String representation of the URL (if defined).
      *
      * @return ExternalForm of URL, or empty string if url is null
      */
     public String getUrlAsString() {
         return location == null ? "" : location.toExternalForm();
     }
 
     /**
      * @return Returns the parent.
      */
     public SampleResult getParent() {
         return parent;
     }
 
     /**
      * @param parent
      *            The parent to set.
      */
     public void setParent(SampleResult parent) {
         this.parent = parent;
     }
 
     public String getResultFileName() {
         return resultFileName;
     }
 
     public void setResultFileName(String resultFileName) {
         this.resultFileName = resultFileName;
     }
 
     public int getGroupThreads() {
         return groupThreads;
     }
 
     public void setGroupThreads(int n) {
         this.groupThreads = n;
     }
 
     public int getAllThreads() {
         return allThreads;
     }
 
     public void setAllThreads(int n) {
         this.allThreads = n;
     }
 
     // Bug 47394
     /**
      * Allow custom SampleSenders to drop unwanted assertionResults
      */
     public void removeAssertionResults() {
         this.assertionResults = null;
     }
 
     /**
      * Allow custom SampleSenders to drop unwanted subResults
      */
     public void removeSubResults() {
         this.subResults = null;
     }
     
     /**
      * Set the headers size in bytes
      * 
      * @param size
      *            the number of bytes of the header
      */
     public void setHeadersSize(int size) {
         this.headersSize = size;
     }
     
     /**
      * Get the headers size in bytes
      * 
      * @return the headers size
      */
     public int getHeadersSize() {
         return headersSize;
     }
 
     /**
      * @return the body size in bytes
      */
     public int getBodySize() {
         return bodySize == 0 ? responseData.length : bodySize;
     }
 
     /**
      * @param bodySize the body size to set
      */
     public void setBodySize(int bodySize) {
         this.bodySize = bodySize;
     }
 
     private static class NanoOffset extends Thread {
 
         private static volatile long nanoOffset; 
 
         static long getNanoOffset() {
             return nanoOffset;
         }
 
         @Override
         public void run() {
             // Wait longer than a clock pulse (generally 10-15ms)
             getOffset(30L); // Catch an early clock pulse to reduce slop.
             while(true) {
                 getOffset(NANOTHREAD_SLEEP); // Can now afford to wait a bit longer between checks
             }
             
         }
 
         private void getOffset(long wait) {
             try {
                 TimeUnit.MILLISECONDS.sleep(wait);
                 long clock = System.currentTimeMillis();
                 long nano = SampleResult.sampleNsClockInMs();
                 nanoOffset = clock - nano;
             } catch (InterruptedException ignore) {
                 // ignored
             }
         }
diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index 38e517d67..e98d1277b 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,855 +1,854 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  */
 
 /*
  * Created on Sep 7, 2004
  */
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Properties;
 
 import org.apache.commons.lang3.CharUtils;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.log.Logger;
 
 /*
  * N.B. to add a new field, remember the following
  * - static _xyz
  * - instance xyz=_xyz
  * - clone s.xyz = xyz (perhaps)
  * - setXyz(boolean)
  * - saveXyz()
  * - update SampleSaveConfigurationConverter to add new fields to marshall() and shouldSerialiseMember()
  * - update SampleResultConverter and/or HTTPSampleConverter
  * - update CSVSaveService: CSV_XXXX, makeResultFromDelimitedString, printableFieldNamesToString, static{}
  * - update messages.properties to add save_xyz entry
  * - update jmeter.properties to add new property
  * - update listeners.xml to add new property, CSV and XML names etc.
  * - take screenshot sample_result_config.png
  * - update listeners.xml and component_reference.xml with new dimensions (might not change)
  *
  */
 /**
  * Holds details of which sample attributes to save.
  *
  * The pop-up dialogue for this is created by the class SavePropertyDialog, which assumes:
  * <p>
  * For each field <em>XXX</em>
  * <ul>
  *  <li>methods have the signature "boolean save<em>XXX</em>()"</li>
  *  <li>a corresponding "void set<em>XXX</em>(boolean)" method</li>
  *  <li>messages.properties contains the key save_<em>XXX</em></li>
  * </ul>
  */
 public class SampleSaveConfiguration implements Cloneable, Serializable {
     private static final long serialVersionUID = 7L;
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // ---------------------------------------------------------------------
     // PROPERTY FILE CONSTANTS
     // ---------------------------------------------------------------------
 
     /** Indicates that the results file should be in XML format. * */
     private static final String XML = "xml"; // $NON_NLS-1$
 
     /** Indicates that the results file should be in CSV format. * */
     private static final String CSV = "csv"; // $NON_NLS-1$
 
     /** Indicates that the results should be stored in a database. * */
     //NOTUSED private static final String DATABASE = "db"; // $NON_NLS-1$
 
     /** A properties file indicator for true. * */
     private static final String TRUE = "true"; // $NON_NLS-1$
 
     /** A properties file indicator for false. * */
     private static final String FALSE = "false"; // $NON_NLS-1$
 
     /** A properties file indicator for milliseconds. * */
     private static final String MILLISECONDS = "ms"; // $NON_NLS-1$
 
     /** A properties file indicator for none. * */
     private static final String NONE = "none"; // $NON_NLS-1$
 
     /** A properties file indicator for the first of a series. * */
     private static final String FIRST = "first"; // $NON_NLS-1$
 
     /** A properties file indicator for all of a series. * */
     private static final String ALL = "all"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which assertion results should be
      * saved.
      **************************************************************************/
     public static final String ASSERTION_RESULTS_FAILURE_MESSAGE_PROP =
         "jmeter.save.saveservice.assertion_results_failure_message";  // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which assertion results should be
      * saved.
      **************************************************************************/
     private static final String ASSERTION_RESULTS_PROP = "jmeter.save.saveservice.assertion_results"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which delimiter should be used when
      * saving in a delimited values format.
      **************************************************************************/
     private static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating which format should be used when
      * saving the results, e.g., xml or csv.
      **************************************************************************/
     private static final String OUTPUT_FORMAT_PROP = "jmeter.save.saveservice.output_format"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether field names should be printed
      * to a delimited file.
      **************************************************************************/
     private static final String PRINT_FIELD_NAMES_PROP = "jmeter.save.saveservice.print_field_names"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the data type should be
      * saved.
      **************************************************************************/
     private static final String SAVE_DATA_TYPE_PROP = "jmeter.save.saveservice.data_type"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the label should be saved.
      **************************************************************************/
     private static final String SAVE_LABEL_PROP = "jmeter.save.saveservice.label"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response code should be
      * saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_CODE_PROP = "jmeter.save.saveservice.response_code"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response data should be
      * saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_DATA_PROP = "jmeter.save.saveservice.response_data"; // $NON_NLS-1$
 
     private static final String SAVE_RESPONSE_DATA_ON_ERROR_PROP = "jmeter.save.saveservice.response_data.on_error"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the response message should
      * be saved.
      **************************************************************************/
     private static final String SAVE_RESPONSE_MESSAGE_PROP = "jmeter.save.saveservice.response_message"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the success indicator should
      * be saved.
      **************************************************************************/
     private static final String SAVE_SUCCESSFUL_PROP = "jmeter.save.saveservice.successful"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the thread name should be
      * saved.
      **************************************************************************/
     private static final String SAVE_THREAD_NAME_PROP = "jmeter.save.saveservice.thread_name"; // $NON_NLS-1$
 
     // Save bytes read
     private static final String SAVE_BYTES_PROP = "jmeter.save.saveservice.bytes"; // $NON_NLS-1$
 
     // Save URL
     private static final String SAVE_URL_PROP = "jmeter.save.saveservice.url"; // $NON_NLS-1$
 
     // Save fileName for ResultSaver
     private static final String SAVE_FILENAME_PROP = "jmeter.save.saveservice.filename"; // $NON_NLS-1$
 
     // Save hostname for ResultSaver
     private static final String SAVE_HOSTNAME_PROP = "jmeter.save.saveservice.hostname"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property indicating whether the time should be saved.
      **************************************************************************/
     private static final String SAVE_TIME_PROP = "jmeter.save.saveservice.time"; // $NON_NLS-1$
 
     /***************************************************************************
      * The name of the property giving the format of the time stamp
      **************************************************************************/
     private static final String TIME_STAMP_FORMAT_PROP = "jmeter.save.saveservice.timestamp_format"; // $NON_NLS-1$
 
     private static final String SUBRESULTS_PROP      = "jmeter.save.saveservice.subresults"; // $NON_NLS-1$
     private static final String ASSERTIONS_PROP      = "jmeter.save.saveservice.assertions"; // $NON_NLS-1$
     private static final String LATENCY_PROP         = "jmeter.save.saveservice.latency"; // $NON_NLS-1$
     private static final String CONNECT_TIME_PROP    = "jmeter.save.saveservice.connect_time"; // $NON_NLS-1$
     private static final String SAMPLERDATA_PROP     = "jmeter.save.saveservice.samplerData"; // $NON_NLS-1$
     private static final String RESPONSEHEADERS_PROP = "jmeter.save.saveservice.responseHeaders"; // $NON_NLS-1$
     private static final String REQUESTHEADERS_PROP  = "jmeter.save.saveservice.requestHeaders"; // $NON_NLS-1$
     private static final String ENCODING_PROP        = "jmeter.save.saveservice.encoding"; // $NON_NLS-1$
 
 
     // optional processing instruction for line 2; e.g.
     // <?xml-stylesheet type="text/xsl" href="../extras/jmeter-results-detail-report_21.xsl"?>
     private static final String XML_PI               = "jmeter.save.saveservice.xml_pi"; // $NON_NLS-1$
 
     private static final String SAVE_THREAD_COUNTS   = "jmeter.save.saveservice.thread_counts"; // $NON_NLS-1$
 
     private static final String SAVE_SAMPLE_COUNT    = "jmeter.save.saveservice.sample_count"; // $NON_NLS-1$
 
     private static final String SAVE_IDLE_TIME       = "jmeter.save.saveservice.idle_time"; // $NON_NLS-1$
     // N.B. Remember to update the equals and hashCode methods when adding new variables.
 
     // Initialise values from properties
     private boolean time = _time, latency = _latency, connectTime=_connectTime, timestamp = _timestamp, success = _success, label = _label,
             code = _code, message = _message, threadName = _threadName, dataType = _dataType, encoding = _encoding,
             assertions = _assertions, subresults = _subresults, responseData = _responseData,
             samplerData = _samplerData, xml = _xml, fieldNames = _fieldNames, responseHeaders = _responseHeaders,
             requestHeaders = _requestHeaders, responseDataOnError = _responseDataOnError;
 
     private boolean saveAssertionResultsFailureMessage = _saveAssertionResultsFailureMessage;
 
     private boolean url = _url, bytes = _bytes , fileName = _fileName;
 
     private boolean hostname = _hostname;
 
     private boolean threadCounts = _threadCounts;
 
     private boolean sampleCount = _sampleCount;
 
     private boolean idleTime = _idleTime;
 
     // Does not appear to be used (yet)
     private int assertionsResultsToSave = _assertionsResultsToSave;
 
 
     // Don't save this, as it is derived from the time format
     private boolean printMilliseconds = _printMilliseconds;
 
     /** A formatter for the time stamp. */
     private transient DateFormat formatter = _formatter;
     /* Make transient as we don't want to save the SimpleDataFormat class
      * Also, there's currently no way to change the value via the GUI, so changing it
      * later means editting the JMX, or recreating the Listener.
      */
 
     // Defaults from properties:
     private static final boolean _time, _timestamp, _success, _label, _code, _message, _threadName, _xml,
             _responseData, _dataType, _encoding, _assertions, _latency, _connectTime, _subresults, _samplerData, _fieldNames,
             _responseHeaders, _requestHeaders;
 
     private static final boolean _responseDataOnError;
 
     private static final boolean _saveAssertionResultsFailureMessage;
 
     private static final String _timeStampFormat;
 
     private static final int _assertionsResultsToSave;
 
     // TODO turn into method?
     public static final int SAVE_NO_ASSERTIONS = 0;
 
     public static final int SAVE_FIRST_ASSERTION = SAVE_NO_ASSERTIONS + 1;
 
     public static final int SAVE_ALL_ASSERTIONS = SAVE_FIRST_ASSERTION + 1;
 
     private static final boolean _printMilliseconds;
 
     private static final boolean _bytes;
 
     private static final boolean _url;
 
     private static final boolean _fileName;
 
     private static final boolean _hostname;
 
     private static final boolean _threadCounts;
 
     private static final boolean _sampleCount;
 
     private static final DateFormat _formatter;
 
     /**
      * The string used to separate fields when stored to disk, for example, the
      * comma for CSV files.
      */
     private static final String _delimiter;
 
     private static final boolean _idleTime;
 
     private static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
 
     /**
      * Read in the properties having to do with saving from a properties file.
      */
     static {
         Properties props = JMeterUtils.getJMeterProperties();
 
         _subresults      = TRUE.equalsIgnoreCase(props.getProperty(SUBRESULTS_PROP, TRUE));
         _assertions      = TRUE.equalsIgnoreCase(props.getProperty(ASSERTIONS_PROP, TRUE));
         _latency         = TRUE.equalsIgnoreCase(props.getProperty(LATENCY_PROP, TRUE));
         _connectTime     = TRUE.equalsIgnoreCase(props.getProperty(CONNECT_TIME_PROP, FALSE));
         _samplerData     = TRUE.equalsIgnoreCase(props.getProperty(SAMPLERDATA_PROP, FALSE));
         _responseHeaders = TRUE.equalsIgnoreCase(props.getProperty(RESPONSEHEADERS_PROP, FALSE));
         _requestHeaders  = TRUE.equalsIgnoreCase(props.getProperty(REQUESTHEADERS_PROP, FALSE));
         _encoding        = TRUE.equalsIgnoreCase(props.getProperty(ENCODING_PROP, FALSE));
 
         String dlm = props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER);
         if (dlm.equals("\\t")) {// Make it easier to enter a tab (can use \<tab> but that is awkward)
             dlm="\t";
         }
 
         if (dlm.length() != 1){
             throw new JMeterError("Delimiter '"+dlm+"' must be of length 1.");
         }
         char ch = dlm.charAt(0);
 
         if (CharUtils.isAsciiAlphanumeric(ch) || ch == CSVSaveService.QUOTING_CHAR){
             throw new JMeterError("Delimiter '"+ch+"' must not be alphanumeric or "+CSVSaveService.QUOTING_CHAR+".");
         }
 
         if (ch != '\t' && !CharUtils.isAsciiPrintable(ch)){
             throw new JMeterError("Delimiter (code "+(int)ch+") must be printable.");
         }
 
         _delimiter = dlm;
 
         _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, TRUE));
 
         _dataType = TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));
 
         _label = TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));
 
         _code = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));
 
         _responseData = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));
 
         _responseDataOnError = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_ON_ERROR_PROP, FALSE));
 
         _message = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));
 
         _success = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));
 
         _threadName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));
 
         _bytes = TRUE.equalsIgnoreCase(props.getProperty(SAVE_BYTES_PROP, TRUE));
 
         _url = TRUE.equalsIgnoreCase(props.getProperty(SAVE_URL_PROP, FALSE));
 
         _fileName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_FILENAME_PROP, FALSE));
 
         _hostname = TRUE.equalsIgnoreCase(props.getProperty(SAVE_HOSTNAME_PROP, FALSE));
 
         _time = TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));
 
         _timeStampFormat = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);
 
         _printMilliseconds = MILLISECONDS.equalsIgnoreCase(_timeStampFormat);
 
         // Prepare for a pretty date
         if (!_printMilliseconds && !NONE.equalsIgnoreCase(_timeStampFormat) && (_timeStampFormat != null)) {
             _formatter = new SimpleDateFormat(_timeStampFormat);
         } else {
             _formatter = null;
         }
 
         _timestamp = !NONE.equalsIgnoreCase(_timeStampFormat);// reversed compare allows for null
 
         _saveAssertionResultsFailureMessage = TRUE.equalsIgnoreCase(props.getProperty(
                 ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, TRUE));
 
         String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
         if (NONE.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_NO_ASSERTIONS;
         } else if (FIRST.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_FIRST_ASSERTION;
         } else if (ALL.equals(whichAssertionResults)) {
             _assertionsResultsToSave = SAVE_ALL_ASSERTIONS;
         } else {
             _assertionsResultsToSave = 0;
         }
 
         String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, CSV);
 
         if (XML.equals(howToSave)) {
             _xml = true;
         } else {
             if (!CSV.equals(howToSave)) {
                 log.warn(OUTPUT_FORMAT_PROP + " has unexepected value: '" + howToSave + "' - assuming 'csv' format");
             }
             _xml = false;
         }
 
         _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, TRUE));
 
         _sampleCount=TRUE.equalsIgnoreCase(props.getProperty(SAVE_SAMPLE_COUNT, FALSE));
 
         _idleTime=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, FALSE));
     }
 
     // Don't save this, as not settable via GUI
     private String delimiter = _delimiter;
 
     // Don't save this - only needed for processing CSV headers currently
     private transient int varCount = 0;
 
     private static final SampleSaveConfiguration _static = new SampleSaveConfiguration();
 
     public int getVarCount() { // Only for use by CSVSaveService
         return varCount;
     }
 
     public void setVarCount(int varCount) { // Only for use by CSVSaveService
         this.varCount = varCount;
     }
 
     // Give access to initial configuration
     public static SampleSaveConfiguration staticConfig() {
         return _static;
     }
 
     public SampleSaveConfiguration() {
     }
 
     /**
-     * Alternate constructor for use by OldSaveService
+     * Alternate constructor for use by CsvSaveService
      *
      * @param value initial setting for boolean fields used in Config dialogue
      */
     public SampleSaveConfiguration(boolean value) {
         assertions = value;
         bytes = value;
         code = value;
         dataType = value;
         encoding = value;
         fieldNames = value;
         fileName = value;
         hostname = value;
         label = value;
         latency = value;
         connectTime = value;
         message = value;
         printMilliseconds = _printMilliseconds;//is derived from properties only
         requestHeaders = value;
         responseData = value;
         responseDataOnError = value;
         responseHeaders = value;
         samplerData = value;
         saveAssertionResultsFailureMessage = value;
         subresults = value;
         success = value;
         threadCounts = value;
         sampleCount = value;
         threadName = value;
         time = value;
         timestamp = value;
         url = value;
         xml = value;
     }
 
     private Object readResolve(){
        formatter = _formatter;
        return this;
     }
 
     @Override
     public Object clone() {
         try {
             SampleSaveConfiguration clone = (SampleSaveConfiguration)super.clone();
             if(this.formatter != null) {
                 clone.formatter = (SimpleDateFormat)this.formatter.clone();
             }
             return clone;
         }
         catch(CloneNotSupportedException e) {
             throw new RuntimeException("Should not happen",e);
         }
     }
 
     @Override
     public boolean equals(Object obj) {
         if(this == obj) {
             return true;
         }
         if((obj == null) || (obj.getClass() != this.getClass())) {
             return false;
         }
         // We know we are comparing to another SampleSaveConfiguration
         SampleSaveConfiguration s = (SampleSaveConfiguration)obj;
         boolean primitiveValues = s.time == time &&
             s.latency == latency &&
             s.connectTime == connectTime &&
             s.timestamp == timestamp &&
             s.success == success &&
             s.label == label &&
             s.code == code &&
             s.message == message &&
             s.threadName == threadName &&
             s.dataType == dataType &&
             s.encoding == encoding &&
             s.assertions == assertions &&
             s.subresults == subresults &&
             s.responseData == responseData &&
             s.samplerData == samplerData &&
             s.xml == xml &&
             s.fieldNames == fieldNames &&
             s.responseHeaders == responseHeaders &&
             s.requestHeaders == requestHeaders &&
             s.assertionsResultsToSave == assertionsResultsToSave &&
             s.saveAssertionResultsFailureMessage == saveAssertionResultsFailureMessage &&
             s.printMilliseconds == printMilliseconds &&
             s.responseDataOnError == responseDataOnError &&
             s.url == url &&
             s.bytes == bytes &&
             s.fileName == fileName &&
             s.hostname == hostname &&
             s.sampleCount == sampleCount &&
             s.idleTime == idleTime &&
             s.threadCounts == threadCounts;
 
         boolean stringValues = false;
         if(primitiveValues) {
             stringValues = s.delimiter == delimiter || (delimiter != null && delimiter.equals(s.delimiter));
         }
         boolean complexValues = false;
         if(primitiveValues && stringValues) {
             complexValues = s.formatter == formatter || (formatter != null && formatter.equals(s.formatter));
         }
 
         return primitiveValues && stringValues && complexValues;
     }
 
     @Override
     public int hashCode() {
         int hash = 7;
         hash = 31 * hash + (time ? 1 : 0);
         hash = 31 * hash + (latency ? 1 : 0);
         hash = 31 * hash + (connectTime ? 1 : 0);
         hash = 31 * hash + (timestamp ? 1 : 0);
         hash = 31 * hash + (success ? 1 : 0);
         hash = 31 * hash + (label ? 1 : 0);
         hash = 31 * hash + (code ? 1 : 0);
         hash = 31 * hash + (message ? 1 : 0);
         hash = 31 * hash + (threadName ? 1 : 0);
         hash = 31 * hash + (dataType ? 1 : 0);
         hash = 31 * hash + (encoding ? 1 : 0);
         hash = 31 * hash + (assertions ? 1 : 0);
         hash = 31 * hash + (subresults ? 1 : 0);
         hash = 31 * hash + (responseData ? 1 : 0);
         hash = 31 * hash + (samplerData ? 1 : 0);
         hash = 31 * hash + (xml ? 1 : 0);
         hash = 31 * hash + (fieldNames ? 1 : 0);
         hash = 31 * hash + (responseHeaders ? 1 : 0);
         hash = 31 * hash + (requestHeaders ? 1 : 0);
         hash = 31 * hash + assertionsResultsToSave;
         hash = 31 * hash + (saveAssertionResultsFailureMessage ? 1 : 0);
         hash = 31 * hash + (printMilliseconds ? 1 : 0);
         hash = 31 * hash + (responseDataOnError ? 1 : 0);
         hash = 31 * hash + (url ? 1 : 0);
         hash = 31 * hash + (bytes ? 1 : 0);
         hash = 31 * hash + (fileName ? 1 : 0);
         hash = 31 * hash + (hostname ? 1 : 0);
         hash = 31 * hash + (threadCounts ? 1 : 0);
         hash = 31 * hash + (delimiter != null  ? delimiter.hashCode() : 0);
         hash = 31 * hash + (formatter != null  ? formatter.hashCode() : 0);
         hash = 31 * hash + (sampleCount ? 1 : 0);
         hash = 31 * hash + (idleTime ? 1 : 0);
 
         return hash;
     }
 
     ///////////////////// Start of standard save/set access methods /////////////////////
 
     public boolean saveResponseHeaders() {
         return responseHeaders;
     }
 
     public void setResponseHeaders(boolean r) {
         responseHeaders = r;
     }
 
     public boolean saveRequestHeaders() {
         return requestHeaders;
     }
 
     public void setRequestHeaders(boolean r) {
         requestHeaders = r;
     }
 
     public boolean saveAssertions() {
         return assertions;
     }
 
     public void setAssertions(boolean assertions) {
         this.assertions = assertions;
     }
 
     public boolean saveCode() {
         return code;
     }
 
     public void setCode(boolean code) {
         this.code = code;
     }
 
     public boolean saveDataType() {
         return dataType;
     }
 
     public void setDataType(boolean dataType) {
         this.dataType = dataType;
     }
 
     public boolean saveEncoding() {
         return encoding;
     }
 
     public void setEncoding(boolean encoding) {
         this.encoding = encoding;
     }
 
     public boolean saveLabel() {
         return label;
     }
 
     public void setLabel(boolean label) {
         this.label = label;
     }
 
     public boolean saveLatency() {
         return latency;
     }
 
     public void setLatency(boolean latency) {
         this.latency = latency;
     }
 
     public boolean saveConnectTime() {
         return connectTime;
     }
 
     public void setConnectTime(boolean connectTime) {
         this.connectTime = connectTime;
     }
 
     public boolean saveMessage() {
         return message;
     }
 
     public void setMessage(boolean message) {
         this.message = message;
     }
 
     public boolean saveResponseData(SampleResult res) {
         return responseData || TestPlan.getFunctionalMode() || (responseDataOnError && !res.isSuccessful());
     }
 
     public boolean saveResponseData()
     {
         return responseData;
     }
 
     public void setResponseData(boolean responseData) {
         this.responseData = responseData;
     }
 
     public boolean saveSamplerData(SampleResult res) {
         return samplerData || TestPlan.getFunctionalMode() // as per 2.0 branch
                 || (responseDataOnError && !res.isSuccessful());
     }
 
     public boolean saveSamplerData()
     {
         return samplerData;
     }
 
     public void setSamplerData(boolean samplerData) {
         this.samplerData = samplerData;
     }
 
     public boolean saveSubresults() {
         return subresults;
     }
 
     public void setSubresults(boolean subresults) {
         this.subresults = subresults;
     }
 
     public boolean saveSuccess() {
         return success;
     }
 
     public void setSuccess(boolean success) {
         this.success = success;
     }
 
     public boolean saveThreadName() {
         return threadName;
     }
 
     public void setThreadName(boolean threadName) {
         this.threadName = threadName;
     }
 
     public boolean saveTime() {
         return time;
     }
 
     public void setTime(boolean time) {
         this.time = time;
     }
 
     public boolean saveTimestamp() {
         return timestamp;
     }
 
     public void setTimestamp(boolean timestamp) {
         this.timestamp = timestamp;
     }
 
     public boolean saveAsXml() {
         return xml;
     }
 
     public void setAsXml(boolean xml) {
         this.xml = xml;
     }
 
     public boolean saveFieldNames() {
         return fieldNames;
     }
 
     public void setFieldNames(boolean printFieldNames) {
         this.fieldNames = printFieldNames;
     }
 
     public boolean saveUrl() {
         return url;
     }
 
     public void setUrl(boolean save) {
         this.url = save;
     }
 
     public boolean saveBytes() {
         return bytes;
     }
 
     public void setBytes(boolean save) {
         this.bytes = save;
     }
 
     public boolean saveFileName() {
         return fileName;
     }
 
     public void setFileName(boolean save) {
         this.fileName = save;
     }
 
     public boolean saveAssertionResultsFailureMessage() {
         return saveAssertionResultsFailureMessage;
     }
 
     public void setAssertionResultsFailureMessage(boolean b) {
         saveAssertionResultsFailureMessage = b;
     }
 
     public boolean saveThreadCounts() {
         return threadCounts;
     }
 
     public void setThreadCounts(boolean save) {
         this.threadCounts = save;
     }
 
     public boolean saveSampleCount() {
         return sampleCount;
     }
 
     public void setSampleCount(boolean save) {
         this.sampleCount = save;
     }
 
     ///////////////// End of standard field accessors /////////////////////
 
     /**
-     * Only intended for use by OldSaveService (and test cases)
-     * 
+     * Intended for use by CsvSaveService (and test cases)
      * @param fmt
      *            format of the date to be saved. If <code>null</code>
      *            milliseconds since epoch will be printed
      */
     public void setFormatter(DateFormat fmt){
         printMilliseconds = (fmt == null); // maintain relationship
         formatter = fmt;
     }
 
     public boolean printMilliseconds() {
         return printMilliseconds;
     }
 
     public DateFormat formatter() {
         return formatter;
     }
 
     public int assertionsResultsToSave() {
         return assertionsResultsToSave;
     }
 
     public String getDelimiter() {
         return delimiter;
     }
 
     public String getXmlPi() {
         return JMeterUtils.getJMeterProperties().getProperty(XML_PI, ""); // Defaults to empty;
     }
 
     // Used by old Save service
     public void setDelimiter(String delim) {
         delimiter=delim;
     }
 
     // Used by SampleSaveConfigurationConverter.unmarshall()
     public void setDefaultDelimiter() {
         delimiter=_delimiter;
     }
 
     // Used by SampleSaveConfigurationConverter.unmarshall()
     public void setDefaultTimeStampFormat() {
         printMilliseconds=_printMilliseconds;
         formatter=_formatter;
     }
 
     public boolean saveHostname(){
         return hostname;
     }
 
     public void setHostname(boolean save){
         hostname = save;
     }
 
     public boolean saveIdleTime() {
         return idleTime;
     }
 
     public void setIdleTime(boolean save) {
         idleTime = save;
     }
 }
diff --git a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
index dc7da6b3a..d966725a6 100644
--- a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
@@ -1,149 +1,149 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.samplers;
 
 import java.io.Serializable;
 
 /**
  * Aggregates sample results for use by the Statistical remote batch mode.
  * Samples are aggregated by the key defined by getKey().
  * TODO: merge error count into parent class?
  */
 public class StatisticalSampleResult extends SampleResult implements
         Serializable {
 
     private static final long serialVersionUID = 240L;
 
     private int errorCount;
 
     // Need to maintain our own elapsed timer to ensure more accurate aggregation
     private long elapsed;
 
     public StatisticalSampleResult(){// May be called by XStream
     }
 
     /**
-     * Allow OldSaveService to generate a suitable result when sample/error counts have been saved.
+     * Allow CsvSaveService to generate a suitable result when sample/error counts have been saved.
      *
      * @deprecated Needs to be replaced when multiple sample results are sorted out
      *
      * @param stamp this may be a start time or an end time (both in milliseconds)
      * @param elapsed time in milliseconds
      */
     @Deprecated
     public StatisticalSampleResult(long stamp, long elapsed) {
         super(stamp, elapsed);
         this.elapsed = elapsed;
     }
 
     /**
      * Create a statistical sample result from an ordinary sample result.
      * 
      * @param res the sample result 
      */
     public StatisticalSampleResult(SampleResult res) {
         // Copy data that is shared between samples (i.e. the key items):
         setSampleLabel(res.getSampleLabel());
         
         setThreadName(res.getThreadName());
 
         setSuccessful(true); // Assume result is OK
         setSampleCount(0); // because we add the sample count in later
         elapsed = 0;
     }
 
     /**
      * Create a statistical sample result from an ordinary sample result.
      * 
      * @param res the sample result 
      * @param unused no longer used
      * @deprecated no longer necessary; use {@link #StatisticalSampleResult(SampleResult)} instead
      */
     @Deprecated
     public StatisticalSampleResult(SampleResult res, boolean unused) {
         this(res);
     }
 
     public void add(SampleResult res) {
         // Add Sample Counter
         setSampleCount(getSampleCount() + res.getSampleCount());
 
         setBytes(getBytes() + res.getBytes());
 
         // Add Error Counter
         if (!res.isSuccessful()) {
             errorCount++;
             this.setSuccessful(false);
         }
 
         // Set start/end times
         if (getStartTime()==0){ // Bug 40954 - ensure start time gets started!
             this.setStartTime(res.getStartTime());
         } else {
             this.setStartTime(Math.min(getStartTime(), res.getStartTime()));
         }
         this.setEndTime(Math.max(getEndTime(), res.getEndTime()));
 
         setLatency(getLatency()+ res.getLatency());
         setConnectTime(getConnectTime()+ res.getConnectTime());
 
         elapsed += res.getTime();
     }
 
     @Override
     public long getTime() {
         return elapsed;
     }
 
     @Override
     public long getTimeStamp() {
         return getEndTime();
     }
 
     @Override
     public int getErrorCount() {// Overrides SampleResult
         return errorCount;
     }
 
     @Override
     public void setErrorCount(int e) {// for reading CSV files
         errorCount = e;
     }
 
     /**
      * Generates the key to be used for aggregating samples as follows:<br>
      * <code>sampleLabel</code> "-" <code>[threadName|threadGroup]</code>
      * <p>
      * N.B. the key should agree with the fixed items that are saved in the sample.
      *
      * @param event sample event whose key is to be calculated
      * @param keyOnThreadName true if key should use thread name, otherwise use thread group
      * @return the key to use for aggregating samples
      */
     public static String getKey(SampleEvent event, boolean keyOnThreadName) {
         StringBuilder sb = new StringBuilder(80);
         sb.append(event.getResult().getSampleLabel());
         if (keyOnThreadName){
             sb.append('-').append(event.getResult().getThreadName());
         } else {
             sb.append('-').append(event.getThreadGroup());
         }
         return sb.toString();
     }
 }
diff --git a/src/core/org/apache/jmeter/save/OldSaveService.java b/src/core/org/apache/jmeter/save/OldSaveService.java
deleted file mode 100644
index e856a720c..000000000
--- a/src/core/org/apache/jmeter/save/OldSaveService.java
+++ /dev/null
@@ -1,530 +0,0 @@
-/*
- * Licensed to the Apache Software Foundation (ASF) under one or more
- * contributor license agreements.  See the NOTICE file distributed with
- * this work for additional information regarding copyright ownership.
- * The ASF licenses this file to You under the Apache License, Version 2.0
- * (the "License"); you may not use this file except in compliance with
- * the License.  You may obtain a copy of the License at
- *
- *   http://www.apache.org/licenses/LICENSE-2.0
- *
- * Unless required by applicable law or agreed to in writing, software
- * distributed under the License is distributed on an "AS IS" BASIS,
- * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- * See the License for the specific language governing permissions and
- * limitations under the License.
- *
- */
-
-package org.apache.jmeter.save;
-
-//import java.io.ByteArrayOutputStream;
-import java.io.IOException;
-import java.io.InputStream;
-//import java.io.OutputStream;
-import java.io.UnsupportedEncodingException;
-import java.util.Collection;
-//import java.util.Date;
-//import java.util.Iterator;
-//import java.util.LinkedList;
-//import java.util.List;
-import java.util.Map;
-
-import org.apache.avalon.framework.configuration.Configuration;
-import org.apache.avalon.framework.configuration.ConfigurationException;
-//import org.apache.avalon.framework.configuration.DefaultConfiguration;
-import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
-//import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
-import org.apache.jmeter.assertions.AssertionResult;
-import org.apache.jmeter.reporters.ResultCollector;
-import org.apache.jmeter.samplers.SampleResult;
-//import org.apache.jmeter.samplers.SampleSaveConfiguration;
-import org.apache.jmeter.testelement.TestElement;
-import org.apache.jmeter.testelement.property.CollectionProperty;
-import org.apache.jmeter.testelement.property.JMeterProperty;
-import org.apache.jmeter.testelement.property.MapProperty;
-import org.apache.jmeter.testelement.property.StringProperty;
-import org.apache.jmeter.testelement.property.TestElementProperty;
-import org.apache.jmeter.util.NameUpdater;
-import org.apache.jmeter.visualizers.Visualizer;
-import org.apache.jorphan.collections.HashTree;
-import org.apache.jorphan.collections.ListedHashTree;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
-import org.xml.sax.SAXException;
-
-/**
- * This class restores the original Avalon XML format (not used by default).
- *
- * This may be removed in a future release.
- */
-public final class OldSaveService {
-    private static final Logger log = LoggingManager.getLoggerForClass();
-
-    // ---------------------------------------------------------------------
-    // XML RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
-    // ---------------------------------------------------------------------
-
-    // Shared with TestElementSaver
-    static final String PRESERVE = "preserve"; // $NON-NLS-1$
-    static final String XML_SPACE = "xml:space"; // $NON-NLS-1$
-
-    private static final String ASSERTION_RESULT_TAG_NAME = "assertionResult"; // $NON-NLS-1$
-    private static final String BINARY = "binary"; // $NON-NLS-1$
-    private static final String DATA_TYPE = "dataType"; // $NON-NLS-1$
-    private static final String ERROR = "error"; // $NON-NLS-1$
-    private static final String FAILURE = "failure"; // $NON-NLS-1$
-    private static final String FAILURE_MESSAGE = "failureMessage"; // $NON-NLS-1$
-    private static final String LABEL = "label"; // $NON-NLS-1$
-    private static final String RESPONSE_CODE = "responseCode"; // $NON-NLS-1$
-    private static final String RESPONSE_MESSAGE = "responseMessage"; // $NON-NLS-1$
-    private static final String SAMPLE_RESULT_TAG_NAME = "sampleResult"; // $NON-NLS-1$
-    private static final String SUCCESSFUL = "success"; // $NON-NLS-1$
-    private static final String THREAD_NAME = "threadName"; // $NON-NLS-1$
-    private static final String TIME = "time"; // $NON-NLS-1$
-    private static final String TIME_STAMP = "timeStamp"; // $NON-NLS-1$
-
-    private static final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
-
-    /**
-     * Private constructor to prevent instantiation.
-     */
-    private OldSaveService() {
-    }
-
-
-//    public static void saveSubTree(HashTree subTree, OutputStream writer) throws IOException {
-//        Configuration config = getConfigsFromTree(subTree).get(0);
-//        DefaultConfigurationSerializer saver = new DefaultConfigurationSerializer();
-//
-//        saver.setIndent(true);
-//        try {
-//            saver.serialize(writer, config);
-//        } catch (SAXException e) {
-//            throw new IOException("SAX implementation problem");
-//        } catch (ConfigurationException e) {
-//            throw new IOException("Problem using Avalon Configuration tools");
-//        }
-//    }
-
-    /**
-     * Read sampleResult from Avalon XML file.
-     *
-     * @param config Avalon configuration
-     * @return sample result
-     */
-    // Probably no point in converting this to return a SampleEvent
-    private static SampleResult getSampleResult(Configuration config) {
-        SampleResult result = new SampleResult(config.getAttributeAsLong(TIME_STAMP, 0L), config.getAttributeAsLong(
-                TIME, 0L));
-
-        result.setThreadName(config.getAttribute(THREAD_NAME, "")); // $NON-NLS-1$
-        result.setDataType(config.getAttribute(DATA_TYPE, ""));
-        result.setResponseCode(config.getAttribute(RESPONSE_CODE, "")); // $NON-NLS-1$
-        result.setResponseMessage(config.getAttribute(RESPONSE_MESSAGE, "")); // $NON-NLS-1$
-        result.setSuccessful(config.getAttributeAsBoolean(SUCCESSFUL, false));
-        result.setSampleLabel(config.getAttribute(LABEL, "")); // $NON-NLS-1$
-        result.setResponseData(getBinaryData(config.getChild(BINARY)));
-        Configuration[] subResults = config.getChildren(SAMPLE_RESULT_TAG_NAME);
-
-        for (int i = 0; i < subResults.length; i++) {
-            result.storeSubResult(getSampleResult(subResults[i]));
-        }
-        Configuration[] assResults = config.getChildren(ASSERTION_RESULT_TAG_NAME);
-
-        for (int i = 0; i < assResults.length; i++) {
-            result.addAssertionResult(getAssertionResult(assResults[i]));
-        }
-
-        Configuration[] samplerData = config.getChildren("property"); // $NON-NLS-1$
-        for (int i = 0; i < samplerData.length; i++) {
-            result.setSamplerData(samplerData[i].getValue("")); // $NON-NLS-1$
-        }
-        return result;
-    }
-
-//    private static List<Configuration> getConfigsFromTree(HashTree subTree) {
-//        Iterator<TestElement> iter = subTree.list().iterator();
-//        List<Configuration> configs = new LinkedList<Configuration>();
-//
-//        while (iter.hasNext()) {
-//            TestElement item = iter.next();
-//            DefaultConfiguration config = new DefaultConfiguration("node", "node"); // $NON-NLS-1$ // $NON-NLS-2$
-//
-//            config.addChild(getConfigForTestElement(null, item));
-//            List<Configuration> configList = getConfigsFromTree(subTree.getTree(item));
-//            Iterator<Configuration> iter2 = configList.iterator();
-//
-//            while (iter2.hasNext()) {
-//                config.addChild(iter2.next());
-//            }
-//            configs.add(config);
-//        }
-//        return configs;
-//    }
-
-//    private static Configuration getConfiguration(byte[] bin) {
-//        DefaultConfiguration config = new DefaultConfiguration(BINARY, "JMeter Save Service"); // $NON-NLS-1$
-//
-//        try {
-//            config.setValue(new String(bin, "UTF-8")); // $NON-NLS-1$
-//        } catch (UnsupportedEncodingException e) {
-//            log.error("", e); // $NON-NLS-1$
-//        }
-//        return config;
-//    }
-
-    private static byte[] getBinaryData(Configuration config) {
-        if (config == null) {
-            return new byte[0];
-        }
-        try {
-            return config.getValue("").getBytes("UTF-8"); // $NON-NLS-1$
-        } catch (UnsupportedEncodingException e) {
-            return new byte[0];
-        }
-    }
-
-    private static AssertionResult getAssertionResult(Configuration config) {
-        AssertionResult result = new AssertionResult(""); //TODO provide proper name?
-        result.setError(config.getAttributeAsBoolean(ERROR, false));
-        result.setFailure(config.getAttributeAsBoolean(FAILURE, false));
-        result.setFailureMessage(config.getAttribute(FAILURE_MESSAGE, ""));
-        return result;
-    }
-
-//    private static Configuration getConfiguration(AssertionResult assResult) {
-//        DefaultConfiguration config = new DefaultConfiguration(ASSERTION_RESULT_TAG_NAME, "JMeter Save Service");
-//
-//        config.setAttribute(FAILURE_MESSAGE, assResult.getFailureMessage());
-//        config.setAttribute(ERROR, "" + assResult.isError());
-//        config.setAttribute(FAILURE, "" + assResult.isFailure());
-//        return config;
-//    }
-
-//    /**
-//     * This method determines the content of the result data that will be
-//     * stored for the Avalon XML format.
-//     *
-//     * @param result
-//     *            the object containing all of the data that has been collected.
-//     * @param saveConfig
-//     *            the configuration giving the data items to be saved.
-//     * N.B. It is rather out of date, as many fields are not saved.
-//     * However it is probably not worth updating, as no-one should be using the format.
-//     */
-//    public static Configuration getConfiguration(SampleResult result, SampleSaveConfiguration saveConfig) {
-//        DefaultConfiguration config = new DefaultConfiguration(SAMPLE_RESULT_TAG_NAME, "JMeter Save Service"); // $NON-NLS-1$
-//
-//        if (saveConfig.saveTime()) {
-//            config.setAttribute(TIME, String.valueOf(result.getTime()));
-//        }
-//        if (saveConfig.saveLabel()) {
-//            config.setAttribute(LABEL, result.getSampleLabel());
-//        }
-//        if (saveConfig.saveCode()) {
-//            config.setAttribute(RESPONSE_CODE, result.getResponseCode());
-//        }
-//        if (saveConfig.saveMessage()) {
-//            config.setAttribute(RESPONSE_MESSAGE, result.getResponseMessage());
-//        }
-//        if (saveConfig.saveThreadName()) {
-//            config.setAttribute(THREAD_NAME, result.getThreadName());
-//        }
-//        if (saveConfig.saveDataType()) {
-//            config.setAttribute(DATA_TYPE, result.getDataType());
-//        }
-//
-//        if (saveConfig.printMilliseconds()) {
-//            config.setAttribute(TIME_STAMP, String.valueOf(result.getTimeStamp()));
-//        } else if (saveConfig.formatter() != null) {
-//            String stamp = saveConfig.formatter().format(new Date(result.getTimeStamp()));
-//
-//            config.setAttribute(TIME_STAMP, stamp);
-//        }
-//
-//        if (saveConfig.saveSuccess()) {
-//            config.setAttribute(SUCCESSFUL, Boolean.toString(result.isSuccessful()));
-//        }
-//
-//        SampleResult[] subResults = result.getSubResults();
-//
-//        if (subResults != null) {
-//            for (int i = 0; i < subResults.length; i++) {
-//                config.addChild(getConfiguration(subResults[i], saveConfig));
-//            }
-//        }
-//
-//        AssertionResult[] assResults = result.getAssertionResults();
-//
-//        if (saveConfig.saveSamplerData(result)) {
-//            config.addChild(createConfigForString("samplerData", result.getSamplerData())); // $NON-NLS-1$
-//        }
-//        if (saveConfig.saveAssertions() && assResults != null) {
-//            for (int i = 0; i < assResults.length; i++) {
-//                config.addChild(getConfiguration(assResults[i]));
-//            }
-//        }
-//        if (saveConfig.saveResponseData(result)) {
-//            config.addChild(getConfiguration(result.getResponseData()));
-//        }
-//        return config;
-//    }
-
-//    private static Configuration getConfigForTestElement(String named, TestElement item) {
-//        TestElementSaver saver = new TestElementSaver(named);
-//        item.traverse(saver);
-//        Configuration config = saver.getConfiguration();
-//        /*
-//         * DefaultConfiguration config = new DefaultConfiguration("testelement",
-//         * "testelement");
-//         *
-//         * if (named != null) { config.setAttribute("name", named); } if
-//         * (item.getProperty(TestElement.TEST_CLASS) != null) {
-//         * config.setAttribute("class", (String)
-//         * item.getProperty(TestElement.TEST_CLASS)); } else {
-//         * config.setAttribute("class", item.getClass().getName()); } Iterator
-//         * iter = item.getPropertyNames().iterator();
-//         *
-//         * while (iter.hasNext()) { String name = (String) iter.next(); Object
-//         * value = item.getProperty(name);
-//         *
-//         * if (value instanceof TestElement) {
-//         * config.addChild(getConfigForTestElement(name, (TestElement) value)); }
-//         * else if (value instanceof Collection) {
-//         * config.addChild(createConfigForCollection(name, (Collection) value)); }
-//         * else if (value != null) { config.addChild(createConfigForString(name,
-//         * value.toString())); } }
-//         */
-//        return config;
-//    }
-
-
-//    private static Configuration createConfigForString(String name, String value) {
-//        if (value == null) {
-//            value = "";
-//        }
-//        DefaultConfiguration config = new DefaultConfiguration("property", "property");
-//
-//        config.setAttribute("name", name);
-//        config.setValue(value);
-//        config.setAttribute(XML_SPACE, PRESERVE);
-//        return config;
-//    }
-
-    // Called by SaveService.loadTree(InputStream reader) if XStream loading fails
-    public synchronized static HashTree loadSubTree(InputStream in) throws IOException {
-        try {
-            Configuration config = builder.build(in);
-            HashTree loadedTree = generateNode(config);
-
-            return loadedTree;
-        } catch (ConfigurationException e) {
-            String message = "Problem loading using Avalon Configuration tools";
-            log.error(message, e);
-            throw new IOException(message);
-        } catch (SAXException e) {
-            String message = "Problem with SAX implementation";
-            log.error(message, e);
-            throw new IOException(message);
-        }
-    }
-
-    private static TestElement createTestElement(Configuration config) throws ConfigurationException,
-            ClassNotFoundException, IllegalAccessException, InstantiationException {
-        TestElement element = null;
-
-        String testClass = config.getAttribute("class"); // $NON-NLS-1$
-
-        String guiClass=""; // $NON-NLS-1$
-        Configuration[] children = config.getChildren();
-        for (int i = 0; i < children.length; i++) {
-            if (children[i].getName().equals("property")) { // $NON-NLS-1$
-                if (children[i].getAttribute("name").equals(TestElement.GUI_CLASS)){ // $NON-NLS-1$
-                    guiClass=children[i].getValue();
-                }
-            }
-        }
-
-        String newClass = NameUpdater.getCurrentTestName(testClass,guiClass);
-
-        element = (TestElement) Class.forName(newClass).newInstance();
-
-        for (int i = 0; i < children.length; i++) {
-            if (children[i].getName().equals("property")) { // $NON-NLS-1$
-                try {
-                    JMeterProperty prop = createProperty(children[i], newClass);
-                    if (prop!=null) {
-                        element.setProperty(prop);
-                    }
-                } catch (Exception ex) {
-                    log.error("Problem loading property", ex);
-                    element.setProperty(children[i].getAttribute("name"), ""); // $NON-NLS-1$ // $NON-NLS-2$
-                }
-            } else if (children[i].getName().equals("testelement")) { // $NON-NLS-1$
-                element.setProperty(new TestElementProperty(children[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
-                        createTestElement(children[i])));
-            } else if (children[i].getName().equals("collection")) { // $NON-NLS-1$
-                element.setProperty(new CollectionProperty(children[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
-                        createCollection(children[i], newClass)));
-            } else if (children[i].getName().equals("map")) { // $NON-NLS-1$
-                element.setProperty(new MapProperty(children[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
-                        createMap(children[i],newClass)));
-            }
-        }
-        return element;
-    }
-
-    private static Collection<JMeterProperty> createCollection(Configuration config, String testClass) throws ConfigurationException,
-            ClassNotFoundException, IllegalAccessException, InstantiationException {
-        @SuppressWarnings("unchecked") // OK
-        Collection<JMeterProperty> coll = (Collection<JMeterProperty>) Class.forName(config.getAttribute("class")).newInstance(); // $NON-NLS-1$
-        Configuration[] items = config.getChildren();
-
-        for (int i = 0; i < items.length; i++) {
-            if (items[i].getName().equals("property")) { // $NON-NLS-1$
-                JMeterProperty prop = createProperty(items[i], testClass);
-                if (prop!=null) {
-                    coll.add(prop);
-                }
-            } else if (items[i].getName().equals("testelement")) { // $NON-NLS-1$
-                coll.add(new TestElementProperty(items[i].getAttribute("name", ""), createTestElement(items[i]))); // $NON-NLS-1$ // $NON-NLS-2$
-            } else if (items[i].getName().equals("collection")) { // $NON-NLS-1$
-                coll.add(new CollectionProperty(items[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
-                        createCollection(items[i], testClass)));
-            } else if (items[i].getName().equals("string")) { // $NON-NLS-1$
-                JMeterProperty prop = createProperty(items[i], testClass);
-                if (prop!=null) {
-                    coll.add(prop);
-                }
-            } else if (items[i].getName().equals("map")) { // $NON-NLS-1$
-                coll.add(new MapProperty(items[i].getAttribute("name", ""), createMap(items[i], testClass))); // $NON-NLS-1$ // $NON-NLS-2$
-            }
-        }
-        return coll;
-    }
-
-    private static JMeterProperty createProperty(Configuration config, String testClass) throws IllegalAccessException,
-            ClassNotFoundException, InstantiationException {
-        String value = config.getValue(""); // $NON-NLS-1$
-        String name = config.getAttribute("name", value); // $NON-NLS-1$
-        String oname = name;
-        String type = config.getAttribute("propType", StringProperty.class.getName()); // $NON-NLS-1$
-
-        // Do upgrade translation:
-        name = NameUpdater.getCurrentName(name, testClass);
-        if (TestElement.GUI_CLASS.equals(name)) {
-            value = NameUpdater.getCurrentName(value);
-        } else if (TestElement.TEST_CLASS.equals(name)) {
-            value=testClass; // must always agree
-        } else {
-            value = NameUpdater.getCurrentName(value, name, testClass);
-        }
-
-        // Delete any properties whose name converts to the empty string
-        if (oname.length() != 0 && name.length()==0) {
-            return null;
-        }
-
-        // Create the property:
-        JMeterProperty prop = (JMeterProperty) Class.forName(type).newInstance();
-        prop.setName(name);
-        prop.setObjectValue(value);
-
-        return prop;
-    }
-
-    private static Map<String, JMeterProperty> createMap(Configuration config, String testClass) throws ConfigurationException,
-            ClassNotFoundException, IllegalAccessException, InstantiationException {
-        @SuppressWarnings("unchecked") // OK
-        Map<String, JMeterProperty> map = (Map<String, JMeterProperty>) Class.forName(config.getAttribute("class")).newInstance();
-        Configuration[] items = config.getChildren();
-
-        for (int i = 0; i < items.length; i++) {
-            if (items[i].getName().equals("property")) { // $NON-NLS-1$
-                JMeterProperty prop = createProperty(items[i], testClass);
-                if (prop!=null) {
-                    map.put(prop.getName(), prop);
-                }
-            } else if (items[i].getName().equals("testelement")) { // $NON-NLS-1$
-                map.put(items[i].getAttribute("name", ""), new TestElementProperty(items[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
-                        createTestElement(items[i])));
-            } else if (items[i].getName().equals("collection")) { // $NON-NLS-1$
-                map.put(items[i].getAttribute("name"),  // $NON-NLS-1$
-                        new CollectionProperty(items[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
-                        createCollection(items[i], testClass)));
-            } else if (items[i].getName().equals("map")) { // $NON-NLS-1$
-                map.put(items[i].getAttribute("name", ""),  // $NON-NLS-1$ // $NON-NLS-2$
-                        new MapProperty(items[i].getAttribute("name", ""), // $NON-NLS-1$ // $NON-NLS-2$
-                        createMap(items[i], testClass)));
-            }
-        }
-        return map;
-    }
-
-    private static HashTree generateNode(Configuration config) {
-        TestElement element = null;
-
-        try {
-            element = createTestElement(config.getChild("testelement")); // $NON-NLS-1$
-        } catch (Exception e) {
-            log.error("Problem loading part of file", e);
-            return null;
-        }
-        HashTree subTree = new ListedHashTree(element);
-        Configuration[] subNodes = config.getChildren("node"); // $NON-NLS-1$
-
-        for (int i = 0; i < subNodes.length; i++) {
-            HashTree t = generateNode(subNodes[i]);
-
-            if (t != null) {
-                subTree.add(element, t);
-            }
-        }
-        return subTree;
-    }
-
-    // Called by ResultCollector#loadExistingFile() if XStream loading fails
-    public static void processSamples(String filename, Visualizer visualizer, ResultCollector rc)
-    throws SAXException, IOException, ConfigurationException
-    {
-        DefaultConfigurationBuilder cfgbuilder = new DefaultConfigurationBuilder();
-        Configuration savedSamples = cfgbuilder.buildFromFile(filename);
-        Configuration[] samples = savedSamples.getChildren();
-        final boolean errorsOnly = rc.isErrorLogging();
-        final boolean successOnly = rc.isSuccessOnlyLogging();
-        for (int i = 0; i < samples.length; i++) {
-            SampleResult result = OldSaveService.getSampleResult(samples[i]);
-            if (ResultCollector.isSampleWanted(result.isSuccessful(), errorsOnly, successOnly)) {
-                visualizer.add(result);
-            }
-        }
-    }
-
-    // Called by ResultCollector#recordResult()
-//    public static String getSerializedSampleResult(
-//            SampleResult result, DefaultConfigurationSerializer slzr, SampleSaveConfiguration cfg)
-//        throws SAXException, IOException,
-//            ConfigurationException {
-//        ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
-//
-//        slzr.serialize(tempOut, OldSaveService.getConfiguration(result, cfg));
-//        String serVer = tempOut.toString();
-//        String lineSep=System.getProperty("line.separator"); // $NON-NLS-1$
-//        /*
-//         * Remove the <?xml ... ?> prefix.
-//         * When using the x-jars (xakan etc) or Java 1.4, the serialised output has a
-//         * newline after the prefix. However, when using Java 1.5 without the x-jars, the output
-//         * has no newline at all.
-//         */
-//        int index = serVer.indexOf(lineSep); // Is there a new-line?
-//        if (index > -1) {// Yes, assume it follows the prefix
-//            return serVer.substring(index);
-//        }
-//        if (serVer.startsWith("<?xml")){ // $NON-NLS-1$
-//            index=serVer.indexOf("?>");// must exist // $NON-NLS-1$
-//            return lineSep + serVer.substring(index+2);// +2 for ?>
-//        }
-//        return serVer;
-//    }
-}
diff --git a/src/core/org/apache/jmeter/save/SaveService.java b/src/core/org/apache/jmeter/save/SaveService.java
index 5c792163e..81a506557 100644
--- a/src/core/org/apache/jmeter/save/SaveService.java
+++ b/src/core/org/apache/jmeter/save/SaveService.java
@@ -1,683 +1,678 @@
 /*
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */
 
 package org.apache.jmeter.save;
 
 import java.io.BufferedInputStream;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.OutputStreamWriter;
 import java.io.Writer;
 import java.lang.reflect.InvocationTargetException;
 import java.nio.charset.Charset;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.apache.commons.lang3.builder.ToStringBuilder;
 import org.apache.jmeter.reporters.ResultCollectorHelper;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.NameUpdater;
 import org.apache.jorphan.collections.HashTree;
 import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
 import org.apache.log.Logger;
 
 import com.thoughtworks.xstream.XStream;
 import com.thoughtworks.xstream.converters.ConversionException;
 import com.thoughtworks.xstream.converters.Converter;
 import com.thoughtworks.xstream.converters.DataHolder;
 import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
 import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
 import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
 import com.thoughtworks.xstream.io.xml.StaxDriver;
 import com.thoughtworks.xstream.mapper.CannotResolveClassException;
 import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.mapper.MapperWrapper;
 
 /**
  * Handles setting up XStream serialisation.
  * The class reads alias definitions from saveservice.properties.
  *
  */
 public class SaveService {
 
     private static final Logger log = LoggingManager.getLoggerForClass();
 
     // Names of DataHolder entries for JTL processing
     public static final String SAMPLE_EVENT_OBJECT = "SampleEvent"; // $NON-NLS-1$
     public static final String RESULTCOLLECTOR_HELPER_OBJECT = "ResultCollectorHelper"; // $NON-NLS-1$
 
     // Names of DataHolder entries for JMX processing
     public static final String TEST_CLASS_NAME = "TestClassName"; // $NON-NLS-1$
 
     private static final class XStreamWrapper extends XStream {
         private XStreamWrapper(ReflectionProvider reflectionProvider) {
             super(reflectionProvider, new StaxDriver());
         }
 
         // Override wrapMapper in order to insert the Wrapper in the chain
         @Override
         protected MapperWrapper wrapMapper(MapperWrapper next) {
             // Provide our own aliasing using strings rather than classes
             return new MapperWrapper(next){
             // Translate alias to classname and then delegate to wrapped class
             @Override
             public Class<?> realClass(String alias) {
                 String fullName = aliasToClass(alias);
                 if (fullName != null) {
                     fullName = NameUpdater.getCurrentName(fullName);
                 }
                 return super.realClass(fullName == null ? alias : fullName);
             }
             // Translate to alias and then delegate to wrapped class
             @Override
             public String serializedClass(@SuppressWarnings("rawtypes") // superclass does not use types 
                     Class type) {
                 if (type == null) {
                     return super.serializedClass(null); // was type, but that caused FindBugs warning
                 }
                 String alias = classToAlias(type.getName());
                 return alias == null ? super.serializedClass(type) : alias ;
                 }
             };
         }
     }
 
     private static final XStream JMXSAVER = new XStreamWrapper(new PureJavaReflectionProvider());
     private static final XStream JTLSAVER = new XStreamWrapper(new PureJavaReflectionProvider());
     static {
         JTLSAVER.setMode(XStream.NO_REFERENCES); // This is needed to stop XStream keeping copies of each class
     }
 
     // The XML header, with placeholder for encoding, since that is controlled by property
     private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"<ph>\"?>"; // $NON-NLS-1$
 
     // Default file name
     private static final String SAVESERVICE_PROPERTIES_FILE = "/bin/saveservice.properties"; // $NON-NLS-1$
 
     // Property name used to define file name
     private static final String SAVESERVICE_PROPERTIES = "saveservice_properties"; // $NON-NLS-1$
 
     // Define file format property names
     private static final String FILE_FORMAT = "file_format"; // $NON-NLS-1$
     private static final String FILE_FORMAT_TESTPLAN = "file_format.testplan"; // $NON-NLS-1$
     private static final String FILE_FORMAT_TESTLOG = "file_format.testlog"; // $NON-NLS-1$
 
     // Define file format versions
     private static final String VERSION_2_2 = "2.2";  // $NON-NLS-1$
 
     // Default to overall format, and then to version 2.2
     public static final String TESTPLAN_FORMAT
         = JMeterUtils.getPropDefault(FILE_FORMAT_TESTPLAN
         , JMeterUtils.getPropDefault(FILE_FORMAT, VERSION_2_2));
 
     public static final String TESTLOG_FORMAT
         = JMeterUtils.getPropDefault(FILE_FORMAT_TESTLOG
         , JMeterUtils.getPropDefault(FILE_FORMAT, VERSION_2_2));
 
     private static boolean validateFormat(String format){
         if ("2.2".equals(format)) {
             return true;
         }
         if ("2.1".equals(format)) {
             return true;
         }
         return false;
     }
 
     static{
         if (!validateFormat(TESTPLAN_FORMAT)){
             log.error("Invalid test plan format: "+TESTPLAN_FORMAT);
         }
         if (!validateFormat(TESTLOG_FORMAT)){
             log.error("Invalid test log format: "+TESTLOG_FORMAT);
         }
     }
 
     /** New XStream format - more compressed class names */
     public static final boolean IS_TESTPLAN_FORMAT_22
         = VERSION_2_2.equals(TESTPLAN_FORMAT);
 
     // Holds the mappings from the saveservice properties file
     // Key: alias Entry: full class name
     // There may be multiple aliases which map to the same class
     private static final Properties aliasToClass = new Properties();
 
     // Holds the reverse mappings
     // Key: full class name Entry: primary alias
     private static final Properties classToAlias = new Properties();
 
     // Version information for test plan header
     // This is written to JMX files by ScriptWrapperConverter
     // Also to JTL files by ResultCollector
     private static final String VERSION = "1.2"; // $NON-NLS-1$
 
     // This is written to JMX files by ScriptWrapperConverter
     private static String propertiesVersion = "";// read from properties file; written to JMX files
     
     // Must match _version property value in saveservice.properties
     // used to ensure saveservice.properties and SaveService are updated simultaneously
     static final String PROPVERSION = "2.9";// Expected version $NON-NLS-1$
 
     // Internal information only
     private static String fileVersion = ""; // computed from saveservice.properties file// $NON-NLS-1$
     // Must match the sha1 checksum of the file saveservice.properties (without newline character),
     // used to ensure saveservice.properties and SaveService are updated simultaneously
     static final String FILEVERSION = "2e0ec2b2360e52cd5de4e0f20fa51c1809f6895c"; // Expected value $NON-NLS-1$
 
     private static String fileEncoding = ""; // read from properties file// $NON-NLS-1$
 
     static {
         log.info("Testplan (JMX) version: "+TESTPLAN_FORMAT+". Testlog (JTL) version: "+TESTLOG_FORMAT);
         initProps();
         checkVersions();
     }
 
     // Helper method to simplify alias creation from properties
     private static void makeAlias(String aliasList, String clazz) {
         String[] aliases = aliasList.split(","); // Can have multiple aliases for same target classname
         String alias = aliases[0];
         for (String a : aliases){
             Object old = aliasToClass.setProperty(a,clazz);
             if (old != null){
                 log.error("Duplicate class detected for "+alias+": "+clazz+" & "+old);                
             }
         }
         Object oldval=classToAlias.setProperty(clazz,alias);
         if (oldval != null) {
             log.error("Duplicate alias detected for "+clazz+": "+alias+" & "+oldval);
         }
     }
 
     public static Properties loadProperties() throws IOException{
         Properties nameMap = new Properties();
         try (FileInputStream fis = new FileInputStream(JMeterUtils.getJMeterHome()
                 + JMeterUtils.getPropDefault(SAVESERVICE_PROPERTIES, SAVESERVICE_PROPERTIES_FILE))){
             nameMap.load(fis);
         }
         return nameMap;
     }
 
     private static String getChecksumForPropertiesFile()
             throws NoSuchAlgorithmException, IOException {
         MessageDigest md = MessageDigest.getInstance("SHA1");
         try (FileReader fileReader = new FileReader(
                     JMeterUtils.getJMeterHome()
                     + JMeterUtils.getPropDefault(SAVESERVICE_PROPERTIES,
                     SAVESERVICE_PROPERTIES_FILE));
                 BufferedReader reader = new BufferedReader(fileReader)) {
             String line = null;
             while ((line = reader.readLine()) != null) {
                 md.update(line.getBytes());
             }
         }
         return JOrphanUtils.baToHexString(md.digest());
     }
     private static void initProps() {
         // Load the alias properties
         try {
             fileVersion = getChecksumForPropertiesFile();
         } catch (IOException | NoSuchAlgorithmException e) {
             log.fatalError("Can't compute checksum for saveservice properties file", e);
             throw new JMeterError("JMeter requires the checksum of saveservice properties file to continue", e);
         }
         try {
             Properties nameMap = loadProperties();
             // now create the aliases
             for (Map.Entry<Object, Object> me : nameMap.entrySet()) {
                 String key = (String) me.getKey();
                 String val = (String) me.getValue();
                 if (!key.startsWith("_")) { // $NON-NLS-1$
                     makeAlias(key, val);
                 } else {
                     // process special keys
                     if (key.equalsIgnoreCase("_version")) { // $NON-NLS-1$
                         propertiesVersion = val;
                         log.info("Using SaveService properties version " + propertiesVersion);
                     } else if (key.equalsIgnoreCase("_file_version")) { // $NON-NLS-1$
                         log.info("SaveService properties file version is now computed by a checksum,"
                                 + "the property _file_version is not used anymore and can be removed.");
                     } else if (key.equalsIgnoreCase("_file_encoding")) { // $NON-NLS-1$
                         fileEncoding = val;
                         log.info("Using SaveService properties file encoding " + fileEncoding);
                     } else {
                         key = key.substring(1);// Remove the leading "_"
                         try {
                             final String trimmedValue = val.trim();
                             if (trimmedValue.equals("collection") // $NON-NLS-1$
                              || trimmedValue.equals("mapping")) { // $NON-NLS-1$
                                 registerConverter(key, JMXSAVER, true);
                                 registerConverter(key, JTLSAVER, true);
                             } else {
                                 registerConverter(key, JMXSAVER, false);
                                 registerConverter(key, JTLSAVER, false);
                             }
                         } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | IllegalArgumentException|
                                 SecurityException | InvocationTargetException | NoSuchMethodException e1) {
                             log.warn("Can't register a converter: " + key, e1);
                         }
                     }
                 }
             }
         } catch (IOException e) {
             log.fatalError("Bad saveservice properties file", e);
             throw new JMeterError("JMeter requires the saveservice properties file to continue");
         }
     }
 
     /**
      * Register converter.
      * @param key
      * @param jmxsaver
      * @param useMapper
      *
      * @throws InstantiationException
      * @throws IllegalAccessException
      * @throws InvocationTargetException
      * @throws NoSuchMethodException
      * @throws ClassNotFoundException
      */
     private static void registerConverter(String key, XStream jmxsaver, boolean useMapper)
             throws InstantiationException, IllegalAccessException,
             InvocationTargetException, NoSuchMethodException,
             ClassNotFoundException {
         if (useMapper){
             jmxsaver.registerConverter((Converter) Class.forName(key).getConstructor(
                     new Class[] { Mapper.class }).newInstance(
                             new Object[] { jmxsaver.getMapper() }));
         } else {
             jmxsaver.registerConverter((Converter) Class.forName(key).newInstance());
         }
     }
 
     // For converters to use
     public static String aliasToClass(String s){
         String r = aliasToClass.getProperty(s);
         return r == null ? s : r;
     }
 
     // For converters to use
     public static String classToAlias(String s){
         String r = classToAlias.getProperty(s);
         return r == null ? s : r;
     }
 
     // Called by Save function
     public static void saveTree(HashTree tree, OutputStream out) throws IOException {
         // Get the OutputWriter to use
         OutputStreamWriter outputStreamWriter = getOutputStreamWriter(out);
         writeXmlHeader(outputStreamWriter);
         // Use deprecated method, to avoid duplicating code
         ScriptWrapper wrapper = new ScriptWrapper();
         wrapper.testPlan = tree;
         JMXSAVER.marshal(wrapper, new PrettyPrintWriter(outputStreamWriter));
         outputStreamWriter.write('\n');// Ensure terminated properly
         outputStreamWriter.close();
     }
 
     // Used by Test code
     public static void saveElement(Object el, OutputStream out) throws IOException {
         // Get the OutputWriter to use
         OutputStreamWriter outputStreamWriter = getOutputStreamWriter(out);
         writeXmlHeader(outputStreamWriter);
         // Use deprecated method, to avoid duplicating code
         JMXSAVER.marshal(el, new PrettyPrintWriter(outputStreamWriter));
         outputStreamWriter.close();
     }
 
     // Used by Test code
     public static Object loadElement(InputStream in) throws IOException {
         // Get the InputReader to use
         InputStreamReader inputStreamReader = getInputStreamReader(in);
         // Use deprecated method, to avoid duplicating code
         Object element = JMXSAVER.fromXML(inputStreamReader);
         inputStreamReader.close();
         return element;
     }
 
     /**
      * Save a sampleResult to an XML output file using XStream.
      *
      * @param evt sampleResult wrapped in a sampleEvent
      * @param writer output stream which must be created using {@link #getFileEncoding(String)}
      * @throws IOException when writing data to output fails
      */
     // Used by ResultCollector.sampleOccurred(SampleEvent event)
     public synchronized static void saveSampleResult(SampleEvent evt, Writer writer) throws IOException {
         DataHolder dh = JTLSAVER.newDataHolder();
         dh.put(SAMPLE_EVENT_OBJECT, evt);
         // This is effectively the same as saver.toXML(Object, Writer) except we get to provide the DataHolder
         // Don't know why there is no method for this in the XStream class
         try {
             JTLSAVER.marshal(evt.getResult(), new PrettyPrintWriter(writer), dh);
         } catch(RuntimeException e) {
             throw new IllegalArgumentException("Failed marshalling:"+(evt.getResult() != null ? showDebuggingInfo(evt.getResult()) : "null"), e);
         }
         writer.write('\n');
     }
 
     /**
      * 
      * @param result SampleResult
      * @return String debugging information
      */
     private static String showDebuggingInfo(SampleResult result) {
         try {
             return "class:"+result.getClass()+",content:"+ToStringBuilder.reflectionToString(result);
         } catch(Exception e) {
             return "Exception occured creating debug from event, message:"+e.getMessage();
         }
     }
 
     /**
      * @param elem test element
      * @param writer output stream which must be created using {@link #getFileEncoding(String)}
      * @throws IOException when writing data to output fails
      */
     // Used by ResultCollector#recordStats()
     public synchronized static void saveTestElement(TestElement elem, Writer writer) throws IOException {
         JMXSAVER.toXML(elem, writer); // TODO should this be JTLSAVER? Only seems to be called by MonitorHealthVisualzer
         writer.write('\n');
     }
 
     private static boolean versionsOK = true;
 
 //  private static void checkVersion(Class clazz, String expected) {
 //
 //      String actual = "*NONE*"; // $NON-NLS-1$
 //      try {
 //          actual = (String) clazz.getMethod("getVersion", null).invoke(null, null);
 //          actual = extractVersion(actual);
 //      } catch (Exception ignored) {
 //          // Not needed
 //      }
 //      if (0 != actual.compareTo(expected)) {
 //          versionsOK = false;
 //          log.warn("Version mismatch: expected '" + expected + "' found '" + actual + "' in " + clazz.getName());
 //      }
 //  }
 
     // Routines for TestSaveService
     static String getPropertyVersion(){
         return SaveService.propertiesVersion;
     }
 
     static String getFileVersion(){
         return SaveService.fileVersion;
     }
 
     // Allow test code to check for spurious class references
     static List<String> checkClasses(){
         final ClassLoader classLoader = SaveService.class.getClassLoader();
         List<String> missingClasses = new ArrayList<>();
         //boolean OK = true;
         for (Object clazz : classToAlias.keySet()) {
             String name = (String) clazz;
             if (!NameUpdater.isMapped(name)) {// don't bother checking class is present if it is to be updated
                 try {
                     Class.forName(name, false, classLoader);
                 } catch (ClassNotFoundException e) {
                         log.error("Unexpected entry in saveservice.properties; class does not exist and is not upgraded: "+name);              
                         missingClasses.add(name);
                 }
             }
         }
         return missingClasses;
     }
 
     static boolean checkVersions() {
         versionsOK = true;
         // Disable converter version checks as they are more of a nuisance than helpful
 //      checkVersion(BooleanPropertyConverter.class, "493779"); // $NON-NLS-1$
 //      checkVersion(HashTreeConverter.class, "514283"); // $NON-NLS-1$
 //      checkVersion(IntegerPropertyConverter.class, "493779"); // $NON-NLS-1$
 //      checkVersion(LongPropertyConverter.class, "493779"); // $NON-NLS-1$
 //      checkVersion(MultiPropertyConverter.class, "514283"); // $NON-NLS-1$
 //      checkVersion(SampleResultConverter.class, "571992"); // $NON-NLS-1$
 //
 //        // Not built until later, so need to use this method:
 //        try {
 //            checkVersion(
 //                    Class.forName("org.apache.jmeter.protocol.http.util.HTTPResultConverter"), // $NON-NLS-1$
 //                    "514283"); // $NON-NLS-1$
 //        } catch (ClassNotFoundException e) {
 //            versionsOK = false;
 //            log.warn(e.getLocalizedMessage());
 //        }
 //      checkVersion(StringPropertyConverter.class, "493779"); // $NON-NLS-1$
 //      checkVersion(TestElementConverter.class, "549987"); // $NON-NLS-1$
 //      checkVersion(TestElementPropertyConverter.class, "549987"); // $NON-NLS-1$
 //      checkVersion(ScriptWrapperConverter.class, "514283"); // $NON-NLS-1$
 //      checkVersion(TestResultWrapperConverter.class, "514283"); // $NON-NLS-1$
 //        checkVersion(SampleSaveConfigurationConverter.class,"549936"); // $NON-NLS-1$
 
         if (!PROPVERSION.equalsIgnoreCase(propertiesVersion)) {
             log.warn("Bad _version - expected " + PROPVERSION + ", found " + propertiesVersion + ".");
         }
 //        if (!FILEVERSION.equalsIgnoreCase(fileVersion)) {
 //            log.warn("Bad _file_version - expected " + FILEVERSION + ", found " + fileVersion +".");
 //        }
         if (versionsOK) {
             log.info("All converter versions present and correct");
         }
         return versionsOK;
     }
 
     /**
      * Read results from JTL file.
      *
      * @param reader of the file
      * @param resultCollectorHelper helper class to enable TestResultWrapperConverter to deliver the samples
      * @throws IOException if an I/O error occurs
      */
     public static void loadTestResults(InputStream reader, ResultCollectorHelper resultCollectorHelper) throws IOException {
         // Get the InputReader to use
         InputStreamReader inputStreamReader = getInputStreamReader(reader);
         DataHolder dh = JTLSAVER.newDataHolder();
         dh.put(RESULTCOLLECTOR_HELPER_OBJECT, resultCollectorHelper); // Allow TestResultWrapper to feed back the samples
         // This is effectively the same as saver.fromXML(InputStream) except we get to provide the DataHolder
         // Don't know why there is no method for this in the XStream class
         JTLSAVER.unmarshal(new StaxDriver().createReader(reader), null, dh);
         inputStreamReader.close();
     }
 
     /**
      * Load a Test tree (JMX file)
      * @param reader the JMX file as an {@link InputStream}
      * @return the loaded tree or null if an error occurs
      * @throws IOException if there is a problem reading the file or processing it
      * @deprecated use {@link SaveService}{@link #loadTree(File)}
      */
     @Deprecated
     public static HashTree loadTree(InputStream reader) throws IOException {
         try {
             return readTree(reader, null);
         } catch(IllegalArgumentException e) {
             log.error("Problem loading XML, message:"+e.getMessage(), e);
             return null;
         } finally {
             JOrphanUtils.closeQuietly(reader);
         }
     }
     
     /**
      * Load a Test tree (JMX file)
      * @param file the JMX file
      * @return the loaded tree
      * @throws IOException if there is a problem reading the file or processing it
      */
     public static HashTree loadTree(File file) throws IOException {
         log.info("Loading file: " + file);
         try (InputStream reader = new FileInputStream(file)){
             return readTree(reader, file);
         }
     }
 
     /**
      * 
      * @param reader {@link InputStream} 
      * @param file the JMX file used only for debug, can be null
      * @return the loaded tree
      * @throws IOException if there is a problem reading the file or processing it
      */
     private static HashTree readTree(InputStream reader, File file)
             throws IOException {
         if (!reader.markSupported()) {
             reader = new BufferedInputStream(reader);
         }
         reader.mark(Integer.MAX_VALUE);
         ScriptWrapper wrapper = null;
         try {
             // Get the InputReader to use
             InputStreamReader inputStreamReader = getInputStreamReader(reader);
             wrapper = (ScriptWrapper) JMXSAVER.fromXML(inputStreamReader);
             inputStreamReader.close();
             if (wrapper == null){
                 log.error("Problem loading XML: see above.");
                 return null;
             }
             return wrapper.testPlan;
         } catch (CannotResolveClassException e) {
-            if (e.getMessage().startsWith("node")) {
-                log.info("Problem loading XML, trying Avalon format");
-                reader.reset();
-                return OldSaveService.loadSubTree(reader);                
-            }
             if(file != null) {
                 throw new IllegalArgumentException("Problem loading XML from:'"+file.getAbsolutePath()+"', cannot determine class for element: " + e, e);
             } else {
                 throw new IllegalArgumentException("Problem loading XML, cannot determine class for element: " + e, e);
             }
         } catch (ConversionException | NoClassDefFoundError e) {
             if(file != null) {
                 throw new IllegalArgumentException("Problem loading XML from:'"+file.getAbsolutePath()+"', missing class "+e , e);
             } else {
                 throw new IllegalArgumentException("Problem loading XML, missing class "+e , e);
             }
         }
 
     }
     private static InputStreamReader getInputStreamReader(InputStream inStream) {
         // Check if we have a encoding to use from properties
         Charset charset = getFileEncodingCharset();
         if(charset != null) {
             return new InputStreamReader(inStream, charset);
         }
         else {
             // We use the default character set encoding of the JRE
             return new InputStreamReader(inStream);
         }
     }
 
     private static OutputStreamWriter getOutputStreamWriter(OutputStream outStream) {
         // Check if we have a encoding to use from properties
         Charset charset = getFileEncodingCharset();
         if(charset != null) {
             return new OutputStreamWriter(outStream, charset);
         }
         else {
             // We use the default character set encoding of the JRE
             return new OutputStreamWriter(outStream);
         }
     }
 
     /**
      * Returns the file Encoding specified in saveservice.properties or the default
      * @param dflt value to return if file encoding was not provided
      *
      * @return file encoding or default
      */
     // Used by ResultCollector when creating output files
     public static String getFileEncoding(String dflt){
         if(fileEncoding != null && fileEncoding.length() > 0) {
             return fileEncoding;
         }
         else {
             return dflt;
         }
     }
 
     private static Charset getFileEncodingCharset() {
         // Check if we have a encoding to use from properties
         if(fileEncoding != null && fileEncoding.length() > 0) {
             return Charset.forName(fileEncoding);
         }
         else {
             // We use the default character set encoding of the JRE
             return null;
         }
     }
 
     private static void writeXmlHeader(OutputStreamWriter writer) throws IOException {
         // Write XML header if we have the charset to use for encoding
         Charset charset = getFileEncodingCharset();
         if(charset != null) {
             // We do not use getEncoding method of Writer, since that returns
             // the historical name
             String header = XML_HEADER.replaceAll("<ph>", charset.name());
             writer.write(header);
             writer.write('\n');
         }
     }
 
 //  Normal output
 //  ---- Debugging information ----
 //  required-type       : org.apache.jorphan.collections.ListedHashTree
 //  cause-message       : WebServiceSampler : WebServiceSampler
 //  class               : org.apache.jmeter.save.ScriptWrapper
 //  message             : WebServiceSampler : WebServiceSampler
 //  line number         : 929
 //  path                : /jmeterTestPlan/hashTree/hashTree/hashTree[4]/hashTree[5]/WebServiceSampler
 //  cause-exception     : com.thoughtworks.xstream.alias.CannotResolveClassException
 //  -------------------------------
 
     /**
      * Simplify getMessage() output from XStream ConversionException
      * @param ce - ConversionException to analyse
      * @return string with details of error
      */
     public static String CEtoString(ConversionException ce){
         String msg =
             "XStream ConversionException at line: " + ce.get("line number")
             + "\n" + ce.get("message")
             + "\nPerhaps a missing jar? See log file.";
         return msg;
     }
 
     public static String getPropertiesVersion() {
         return propertiesVersion;
     }
 
     public static String getVERSION() {
         return VERSION;
     }
 }
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index 92a95c23f..928dc0523 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,442 +1,444 @@
 <?xml version="1.0"?> 
 <!--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 -->
 <!DOCTYPE document
 [
 <!ENTITY hellip   "&#x02026;" >
 ]>
 <document>   
 <properties>     
     <author email="dev AT jmeter.apache.org">JMeter developers</author>     
     <title>Changes</title>   
 </properties> 
 <body> 
 <section name="Changes"> 
 <style type="text/css"><!--
 h2 { color: #960000; }
 h3 { color: #960000; }
 --></style>
 <note>
 <b>This page details the changes made in the current version only.</b>
 <br></br>
 Earlier changes are detailed in the <a href="changes_history.html">History of Previous Changes</a>.
 </note>
 
 
 <!--  =================== 3.0 =================== -->
 
 <h1>Version 3.0</h1>
 
 Summary
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Known bugs">Known bugs</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <!-- <ch_category>Improvements</ch_category> -->
 <!-- <ch_title>Sample title</ch_title>
 <p>
 <ul>
 <li>Sample text</li>
 </ul>
 </p>
 
 <ch_title>Sample title</ch_title>
 <p>Sample text</p>
 <figure width="691" height="215" image="changes/2.10/18_https_test_script_recorder.png"></figure>
  -->
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>Since version 3.0, <code>jmeter.save.saveservice.assertion_results_failure_message</code> property value is true, meaning CSV file for results will contain an additional column containing assertion result response message, see <bugzilla>58978</bugzilla></li>
     <li>Since version 3.0, <code>jmeter.save.saveservice.print_field_names</code> property value is true, meaning CSV file for results will contain field names as first line in CSV, see <bugzilla>58991</bugzilla></li>
     <li>In RandomTimer class, protected instance timer has been replaced by getTimer() protected method, this is related to <bugzilla>58100</bugzilla>. This may impact 3rd party plugins.</li>
     <li>Since version 3.0, you can use Nashorn Engine (default javascript engine is Rhino) under Java8 for Elements that use Javascript Engine (__javaScript, IfController). If you want to use it, use property <code>javascript.use_rhino=false</code>, see <bugzilla>58406</bugzilla>.
     Note in future versions, we will switch to Nashorn by default, so users are encouraged to report any issue related to broken code when using Nashorn instead of Rhino.
     </li>
     <li>Since version 3.0, JMS Publisher will reload contents of file if Message source is "From File" and the ""Filename" field changes (through variables usage for example)</li>
     <li>org.apache.jmeter.gui.util.ButtonPanel has been removed, if you use it in your 3rd party plugin or custom development ensure you update your code. See <bugzilla>58687</bugzilla></li>
     <li>Property <code>jmeterthread.startearlier</code> has been removed. See <bugzilla>58726</bugzilla></li>   
     <li>Property <code>jmeterengine.startlistenerslater</code> has been removed. See <bugzilla>58728</bugzilla></li>   
     <li>Property <code>jmeterthread.reversePostProcessors</code> has been removed. See <bugzilla>58728</bugzilla></li>  
     <li>MongoDB elements (MongoDB Source Config, MongoDB Script) have been deprecated and will be removed in next version of jmeter. They do not appear anymore in the menu, if you need them modify <code>not_in_menu</code> property. JMeter team advises not to use them anymore. See <bugzilla>58772</bugzilla></li>
     <li>Summariser listener now outputs a formated duration in HH:mm:ss (Hour:Minute:Second), it previously outputed seconds. See <bugzilla>58776</bugzilla></li>
     <li>WebService(SOAP) Request and HTML Parameter Mask which were deprecated in 2.13 version, have now been removed following our <a href="./usermanual/best-practices.html#deprecation">deprecation strategy</a></li>
     <li>org.apache.jmeter.protocol.http.visualizers.RequestViewHTTP.getQueryMap signature has changed, if you use it ensure you update your code. See <bugzilla>58845</bugzilla></li>
     <li><code>__jexl</code> function has been deprecated and will be removed in next version. See <bugzilla>58903</bugzilla></li>
     <li>JMS Subscriber will consider sample in error if number of received messages is not equals to expected number of messages. It previously considerer sample OK if only 1 message was received. See <bugzilla>58980</bugzilla></li>
     <li>Since version 3.0, HTTP(S) Test Script recorder uses default port 8888 as configured when using Recording Template. See <bugzilla>59006</bugzilla></li>
-    <li>Since version 3.0, the parser for embedded ressources (replaced since 2.10 by Lagarto based implementation) relying on htmlparser library (HtmlParserHTMLParser) has been dropped as long as its dependencies.</li>     
+    <li>Since version 3.0, the parser for embedded ressources (replaced since 2.10 by Lagarto based implementation) relying on htmlparser library (HtmlParserHTMLParser) has been dropped as long as its dependencies.</li>
+    <li>Since version 3.0, the support for reading old Avalon format JTL (result) files has been removed, see <bugzilla>59064</bugzilla></li>     
 </ul>
 
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57696</bug>HTTP Request : Improve responseMessage when resource download fails. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>57995</bug>Use FileServer for HTTP Request files. Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
     <li><bug>58811</bug>When pasting arguments between http samplers the column "Encode" and "Include Equals" are lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58843</bug>Improve the usable space in the HTTP sampler GUI. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58852</bug>Use less memory for <code>PUT</code> requests. The uploaded data will no longer be stored in the Sampler.
         This is the same behaviour as with <code>POST</code> requests.</li>
     <li><bug>58860</bug>HTTP Request : Add automatic variable generation in HTTP parameters table by right click. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58923</bug>normalize URIs when downloading embedded resources.</li>
     <li><bug>59005</bug>HTTP Sampler : Added WebDAV verb (SEARCH).</li>
     <li><bug>59006</bug>Change Default proxy recording port to 8888 to align it with Recording Template. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
     <li><bug>58099</bug>Performance : Lazily initialize HttpClient SSL Context to avoid its initialization even for HTTP only scenarios</li>
     <li><bug>57577</bug>HttpSampler : Retrieve All Embedded Resources, add property "httpsampler.embedded_resources_use_md5" to only compute md5 and not keep response data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59023</bug>HttpSampler UI : rework the embedded resources labels and change default number of parallel downloads to 6. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59028</bug>Use SystemDefaultDnsResolver singleton. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59036</bug>FormCharSetFinder : Use JSoup instead of deprecated HTMLParser</li>
     <li><bug>59034</bug>Parallel downloads connection management is not realistic. Contributed by Benoit Wiart (benoit dot wiart at gmail.com) and Philippe Mouawad</li>
     <li><bug>59060</bug>HTTP Request GUI : Move File Upload to a new Tab to have more space for parameters and prevent incoherent configuration. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>57928</bug>Add ability to define protocol (http/https) to AccessLogSampler GUI. Contributed by Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
     <li><bug>58300</bug> Make existing Java Samplers implement Interruptible</li>
     <li><bug>58160</bug>JMS Publisher : reload file content if file name changes. Based partly on a patch contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
     <li><bug>58786</bug>JDBC Sampler : Replace Excalibur DataSource by more up to date library commons-dbcp2</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58406</bug>IfController : Allow use of Nashorn Engine if available for JavaScript evaluation</li>
     <li><bug>58281</bug>RandomOrderController : Improve randomization algorithm performance. Contributed by Graham Russell (jmeter at ham1.co.uk)</li> 
     <li><bug>58675</bug>Module controller : error message can easily be missed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58673</bug>Module controller : when the target element is disabled the default jtree icons are displayed. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58674</bug>Module controller : it should not be possible to select more than one node in the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58680</bug>Module Controller : ui enhancement. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58989</bug>Record controller gui : add a button to clear all the recorded samples. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58041</bug>Tree View Listener should show sample data type</li>
 <li><bug>58122</bug>GraphiteBackendListener : Add Server Hits metric. Partly based on a patch from Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li><bug>58681</bug>GraphiteBackendListener : Don't send data if no sampling occured</li>
 <li><bug>58776</bug>Summariser should display a more readable duration</li>
 <li><bug>58791</bug>Deprecate listeners:Distribution Graph (alpha) and Spline Visualizer</li>
 <li><bug>58849</bug>View Results Tree : Add a search panel to the request http view to be able to search in the parameters table. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58857</bug>View Results Tree : the request view http does not allow to resize the parameters table first column. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58955</bug>Request view http does not correctly display http parameters in multipart/form-data. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
   <li><bug>58303</bug>Change usage of bouncycastle api in SMIMEAssertion to get rid of deprecation warnings.</li>
   <li><bug>58515</bug>New JSON related components : JSON-PATH Extractor and JSON-PATH Renderer in View Results Tree. Donated by Ubik Load Pack (support at ubikloadpack.com).</li>
   <li><bug>58698</bug>Correct parsing of auth-files in HTTP Authorization Manager.</li>
   <li><bug>58756</bug>CookieManager : Cookie Policy select box content must depend on Cookie implementation.</li>
   <li><bug>56358</bug>Cookie manager supports cross port cookies and RFC6265.</li>
   <li><bug>58773</bug>TestCacheManager : Add tests for CacheManager that use HttpClient 4</li>
   <li><bug>58742</bug>CompareAssertion : Reset data in TableEditor when switching between different CompareAssertions in gui.
       Based on a patch by Vincent Herilier (vherilier at gmail.com)</li>
   <li><bug>58848</bug>Argument Panel : when adding an argument (add button or from clipboard) scroll the table to the new line. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
   <li><bug>58865</bug>Allow empty default value in the Regular Expression Extractor. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>58477</bug> __javaScript function : Allow use of Nashorn engine for Java8 and later versions</li>
     <li><bug>58903</bug>Provide __jexl3 function that uses commons-jexl3 and deprecated __jexl (1.1) function</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
 <li><bug>58736</bug>Add Sample Timeout support</li>
 <li><bug>57913</bug>Automated backups of last saved JMX files. Contributed by Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li><bug>57988</bug>Shortcuts (<keycombo><keysym>Ctrl</keysym><keysym>1</keysym></keycombo> &hellip;
     <keycombo><keysym>Ctrl</keysym><keysym>9</keysym></keycombo>) to quick add elements into test plan.
     Implemented by Andrey Pokhilko (andrey at blazemeter.com) and contributed by BlazeMeter Ltd.</li>
 <li><bug>58100</bug>Performance enhancements : Replace Random by ThreadLocalRandom.</li>
 <li><bug>58465</bug>JMS Read response field is badly named and documented</li>
 <li><bug>58601</bug>Change check for modification of <code>saveservice.properties</code> from <code>SVN Revision ID</code> to sha1 sum of the file itself.</li>
 <li><bug>58677</bug>TestSaveService#testLoadAndSave use the wrong set of files. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58679</bug>Replace the xpp pull parser in xstream with a java6+ standard solution. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58689</bug>Add shortcuts to expand / collapse a part of the tree. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58696</bug>Create Ant task to setup Eclipse project</li>
 <li><bug>58653</bug>New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results. Developed by Ubik-Ingenierie and contributed by Decathlon S.A. and Ubik-Ingenierie / UbikLoadPack</li>
 <li><bug>58699</bug>Workbench changes neither saved nor prompted for saving upon close. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58726</bug>Remove the <code>jmeterthread.startearlier</code> parameter. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58728</bug>Drop old behavioural properties</li>
 <li><bug>57319</bug>Upgrade to HttpClient 4.5.X. With the big help from Oleg Kalnichevski.</li>
 <li><bug>58772</bug>Deprecate MongoDB related elements</li>
 <li><bug>58782</bug>ThreadGroup : Improve ergonomy</li>
 <li><bug>58165</bug>Show the time elapsed since the start of the load test in GUI mode. Partly based on a contribution from Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li><bug>58784</bug>Make JMeterUtils#runSafe sync/async awt invocation configurable and change the visualizers to use the async version.</li>
 <li><bug>58790</bug>Issue in CheckDirty and its relation to ActionRouter</li>
 <li><bug>58814</bug>JVM don't recognize option MaxLiveObjectEvacuationRatio; remove from comments</li>
 <li><bug>58810</bug>Config Element Counter (and others): Check Boxes Toggle Area Too Big</li>
 <li><bug>56554</bug>JSR223 Test Element : Generate compilation cache key automatically. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58911</bug>Header Manager : it should be possible to copy/paste between Header Managers. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58864</bug>Arguments Panel : when moving parameter with up / down, ensure that the selection remains visible. Based on a contribution by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58924</bug>Dashboard / report : It should be possible to export the generated graph as image (PNG). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58884</bug>JMeter report generator : need better error message. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58957</bug>Report/Dashboard: HTML Exporter does not create parent directories for output directory. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
 <li><bug>58968</bug>Add a new template to allow to record script with think time included. Contributed by Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li><bug>58978</bug>Settings defaults : Switch "jmeter.save.saveservice.assertion_results_failure_message" to true (after 2.13)</li>
 <li><bug>58991</bug>Settings defaults : Switch "jmeter.save.saveservice.print_field_names" to true (after 2.13)</li>
 <li><bug>58987</bug>Report/Dashboard: Improve error reporting.</li>
 <li><bug>58870</bug>TableEditor: minimum size is too small. Contributed by Vincent Herilier (vherilier at gmail.com)</li>
 <li><bug>59037</bug>Drop HtmlParserHTMLParser and dependencies on htmlparser and htmllexer</li>
 <li><bug>58933</bug>JSyntaxTextArea : Ability to set font.  Contributed by Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 <li><bug>58793</bug>Create developers page explaining how to build and contribute</li>
 <li><bug>59046</bug>JMeter Gui Replace controller should keep the name and the selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to tika-core and tika-parsers 1.12 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.2 (from 2.3)</li>
 <li>Updated to commons-lang 3.4 (from 3.3.2)</li>
 <li>Updated to rhino-1.7.7.1 (from 1.7R5)</li>
 <li>Updated to jodd-3.6.7.jar (from 3.6.4)</li>
 <li>Updated to jsoup-1.8.3 (from 1.8.1)</li>
 <li>Updated to rsyntaxtextarea-2.5.8 (from 2.5.6)</li>
 <li>Updated to slf4j-1.7.12 (from 1.7.10)</li>
 <li>Updated to xmlgraphics-commons-2.0.1 (from 1.5)</li>
 <li>Updated to commons-collections-3.2.2 (from 3.2.1)</li>
 <li>Updated to commons-net 3.4 (from 3.3)</li>
 <li>Updated to slf4j 1.7.13 (from 1.7.12)</li>
 <li><bug>57981</bug>Require a minimum of Java 7. Partly contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li><bug>58684</bug>JMeterColor does not need to extend java.awt.Color. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58687</bug>ButtonPanel should die. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58705</bug>Make org.apache.jmeter.testelement.property.MultiProperty iterable. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58729</bug>Cleanup extras folder for maintainability</li>
 <li><bug>57110</bug>Fixed spelling+grammar, formatting, removed commented out code etc. Contributed by Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Correct instructions on running jmeter in help.txt. Contributed by Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li><bug>58704</bug>Non regression testing : Ant task batchtest fails if tests and run in a non en_EN locale and use a JMX file that uses a Csv DataSet</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58949</bug>Cleanup of ldap code. Based on a patch by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58897</bug>Improve JUnit Test code. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58967</bug>Use junit categories to exclude tests that need a gui. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>59003</bug>ClutilTestCase testSingleArg8 and testSingleArg9 are identical</li>
+<li><bug>59064</bug>Remove OldSaveService which supported very old Avalon format JTL (result) files</li>
 </ul>
  
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>57806</bug>"audio/x-mpegurl" mime type is erroneously considered as binary by ViewResultsTree. Contributed by Ubik Load Pack (support at ubikloadpack.com).</li>
     <li><bug>57858</bug>Don't call sampleEnd twice in HTTPHC4Impl when a RuntimeException or an IOException occurs in the sample method.</li>
     <li><bug>57921</bug>HTTP/1.1 without keep-alive "Connection" response header no longer uses infinite keep-alive.</li>
     <li><bug>57956</bug>The hc.parameters reference in jmeter.properties doesn't work when JMeter is not started in bin.</li>
     <li><bug>58137</bug>JMeter fails to download embedded URLS that contain illegal characters in URL (it does not escape them).</li>
     <li><bug>58201</bug>Make usage of port in the host header more consistent across the different http samplers.</li>
     <li><bug>58453</bug>HTTP Test Script Recorder : NullPointerException when disabling Capture HTTP Headers </li>
     <li><bug>57804</bug>HTTP Request doesn't reuse cached SSL context when using Client Certificates in HTTPS (only fixed for HttpClient4 implementation)</li>
     <li><bug>58800</bug>proxy.pause default value , fix documentation</li>
     <li><bug>58844</bug>Buttons enable / disable is broken in the arguments panel. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58861</bug>When clicking on up, down or detail while in a cell of the argument panel, newly added content is lost. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>57935</bug>SSL SNI extension not supported by HttpClient 4.2.6</li>
     <li><bug>59044</bug>Http Sampler : It should not be possible to select the multipart encoding if the method is not POST. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59008</bug>Http Sampler: Infinite recursion SampleResult on frame depth limit reached</li>
     <li><bug>59069</bug>CookieManager : Selected Cookie Policy is always reset to default when saving or switching to another TestElement (nightly build 25th feb 2016)</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
     <li><bug>58209</bug>JMeter hang when testing javasampler because HashMap.put() is called from multiple threads without sync.</li>
     <li><bug>58301</bug>Use typed methods such as setInt, setDouble, setDate ... for prepared statement #27</li>
     <li><bug>58851</bug>Add a dependency to hamcrest-core to allow JUnit tests with annotations to work</li>
     <li><bug>58947</bug>Connect metric is wrong when ConnectException occurs</li>
     <li><bug>58980</bug>JMS Subscriber will return successful as long as 1 message is received. Contributed by Harrison Termotto (harrison dot termotto at stonybrook.edu)</li>
     <li><bug>59051</bug>JDBC Request : Connection is closed by pool if it exceeds the configured lifetime (affects nightly build as of 23 fev 2016).</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58600</bug>Display correct filenames, when they are searched by IncludeController</li>
     <li><bug>58678</bug>Module Controller : limit target element selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58714</bug>Module controller : it should not be possible to add a timer as child. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>59067</bug>JMeter fails to iterate over Controllers that are children of a TransactionController having "Generate parent sample" checked after an assertion error occurs on a Thread Group with "Start Next Thread Loop". Contributed by Benoit Wiart(benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
 <li><bug>58033</bug>SampleResultConverter should note that it cannot record non-TEXT data</li>
 <li><bug>58845</bug>Request http view doesn't display all the parameters. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 <li><bug>58413</bug>ViewResultsTree : Request HTTP Renderer does not show correctly parameters that contain ampersand (&amp;). Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
 <li><bug>58079</bug>Do not cache HTTP samples that have a Vary header when using a HTTP CacheManager.</li>
 <li><bug>58912</bug>Response assertion gui : Deleting more than 1 selected row deletes only one row. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 <li><bug>57825</bug>__Random function fails if min value is equal to max value (regression related to <bugzilla>54453</bugzilla>)</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>54826</bug>Don't fail on long strings in JSON responses when displaying them as JSON in View Results Tree.</li>
     <li><bug>57734</bug>Maven transient dependencies are incorrect for 2.13 (Fixed group ids for Commons Pool and Math)</li>
     <li><bug>57821</bug>Command-line option "-X --remoteexit" doesn't work since 2.13 (regression related to <bugzilla>57500</bugzilla>)</li>
     <li><bug>57731</bug>TESTSTART.MS has always the value of the first Test started in Server mode in NON GUI Distributed testing</li>
     <li><bug>58016</bug> Error type casting using external SSL Provider. Contributed by Kirill Yankov (myworkpostbox at gmail.com)</li>
     <li><bug>58293</bug>SOAP/XML-RPC Sampler file browser generates NullPointerException</li>
     <li><bug>58685</bug>JDatefield : Make the modification of the date with up/down arrow work. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58693</bug>Fix "Cannot nest output folder 'jmeter/build/components' inside output folder 'jmeter/build' when setting up eclipse</li>
     <li><bug>58781</bug>Command line option "-?" shows Unknown option</li>
     <li><bug>58795</bug>NPE may occur in GuiPackage#getTestElementCheckSum with some 3rd party plugins</li>
     <li><bug>58913</bug>When closing jmeter should not interpret cancel as "destroy my test plan". Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58952</bug>Report/Dashboard: Generation of aggregated series in graphs does not work. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>58931</bug>New Report/Dashboard : Getting font errors under Firefox and Chrome (not Safari)</li>
     <li><bug>58932</bug>Report / Dashboard: Document clearly and log what report are not generated when saveservice options are not correct. Developed by Florent Sabbe (f dot sabbe at ubik-ingenierie.com) and contributed by Ubik-Ingenierie</li>
     <li><bug>59055</bug>JMeter report generator : When generation is not launched from jmeter/bin folder report-template is not found</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 <ul>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Benoit Vatan (benoit.vatan at gmail.com)</li>
 <li>Jérémie Lesage (jeremie.lesage at jeci.fr)</li>
 <li>Kirill Yankov (myworkpostbox at gmail.com)</li>
 <li>Amol Moye (amol.moye at thomsonreuters.com)</li>
 <li>Samoht-fr (https://github.com/Samoht-fr)</li>
 <li>Graham Russell (jmeter at ham1.co.uk)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>Benoit Wiart (benoit.wiart at gmail.com)</li>
 <li><a href="http://www.decathlon.com">Decathlon S.A.</a></li>
 <li><a href="http://www.ubik-ingenierie.com">Ubik-Ingenierie S.A.S.</a></li>
 <li>Oleg Kalnichevski (olegk at apache.org)</li>
 <li>Pascal Schumacher (pascalschumacher at gmx.net)</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Florent Sabbe (f dot sabbe at ubik-ingenierie.com)</li>
 <li>Antonio Gomes Rodrigues (ra0077 at gmail.com)</li>
 <li>Harrison Termotto (harrison dot termotto at stonybrook.edu</li>
 <li>Vincent Herilier (vherilier at gmail.com)</li>
 <li>Denis Kirpichenkov (denis.kirpichenkov at gmail.com)</li>
 </ul>
 
 <br/>
 We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:
 <ul>
 </ul>
 
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
  
 <ch_section>Known problems and workarounds</ch_section>
 
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads, 
 the total number of threads only applies to a locally run test, otherwise it will show 0 (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that there is a <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6396599 ">bug in Java</a>
 on some Linux systems that manifests itself as the following error when running the test cases or JMeter itself:
 <pre>
  [java] WARNING: Couldn't flush user prefs:
  java.util.prefs.BackingStoreException:
  java.lang.IllegalArgumentException: Not supported: indent-number
 </pre>
 This does not affect JMeter operation. This issue is fixed since Java 7b05.
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <pre>
 java.util.prefs.WindowsPreferences 
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </pre>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 With Oracle Java 7 and Mac Book Pro Retina Display, the JMeter GUI may look blurry. 
 This is a known Java bug, see Bug <a href="http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8000629" >JDK-8000629</a>.
 A workaround is to use a Java 7 update 40 runtime which fixes this issue.
 </li>
 
 <li>
 You may encounter the following error: <i>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</i>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing 
 the Java <b>jdk.certpath.disabledAlgorithms</b> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <pre>JAVA_HOME/jre/lib/security/java.security</pre>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a> 
 The fix is to use JDK7_u79, JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "px" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a> 
 The fix is to use JDK9 b65 or later.
 </li>
 
 <li>
 JTable selection with keyboard (SHIFT + up/down) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a> 
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
  
 </section> 
 </body> 
 </document>
