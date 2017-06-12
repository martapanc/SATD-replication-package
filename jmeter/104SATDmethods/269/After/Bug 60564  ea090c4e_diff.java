diff --git a/src/core/org/apache/jmeter/save/CSVSaveService.java b/src/core/org/apache/jmeter/save/CSVSaveService.java
index ba4d61dc1..5ec47942e 100644
--- a/src/core/org/apache/jmeter/save/CSVSaveService.java
+++ b/src/core/org/apache/jmeter/save/CSVSaveService.java
@@ -1,1174 +1,1172 @@
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
 
 import java.io.BufferedReader;
 import java.io.CharArrayWriter;
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.StringReader;
 import java.io.Writer;
 import java.nio.charset.StandardCharsets;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import javax.swing.table.DefaultTableModel;
 
 import org.apache.commons.collections.map.LinkedMap;
 import org.apache.commons.lang3.CharUtils;
 import org.apache.commons.lang3.StringUtils;
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.reporters.ResultCollector;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.samplers.StatisticalSampleResult;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.visualizers.Visualizer;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This class provides a means for saving/reading test results as CSV files.
  */
 // For unit tests, @see TestCSVSaveService
 public final class CSVSaveService {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(CSVSaveService.class);
 
     // ---------------------------------------------------------------------
     // XML RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
     // ---------------------------------------------------------------------
 
     public static final String DATA_TYPE = "dataType"; // $NON-NLS-1$
     public static final String FAILURE_MESSAGE = "failureMessage"; // $NON-NLS-1$
     public static final String LABEL = "label"; // $NON-NLS-1$
     public static final String RESPONSE_CODE = "responseCode"; // $NON-NLS-1$
     public static final String RESPONSE_MESSAGE = "responseMessage"; // $NON-NLS-1$
     public static final String SUCCESSFUL = "success"; // $NON-NLS-1$
     public static final String THREAD_NAME = "threadName"; // $NON-NLS-1$
     public static final String TIME_STAMP = "timeStamp"; // $NON-NLS-1$
 
     // ---------------------------------------------------------------------
     // ADDITIONAL CSV RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
     // ---------------------------------------------------------------------
 
     public static final String CSV_ELAPSED = "elapsed"; // $NON-NLS-1$
     public static final String CSV_BYTES = "bytes"; // $NON-NLS-1$
     public static final String CSV_SENT_BYTES = "sentBytes"; // $NON-NLS-1$
     public static final String CSV_THREAD_COUNT1 = "grpThreads"; // $NON-NLS-1$
     public static final String CSV_THREAD_COUNT2 = "allThreads"; // $NON-NLS-1$
     public static final String CSV_SAMPLE_COUNT = "SampleCount"; // $NON-NLS-1$
     public static final String CSV_ERROR_COUNT = "ErrorCount"; // $NON-NLS-1$
     public static final String CSV_URL = "URL"; // $NON-NLS-1$
     public static final String CSV_FILENAME = "Filename"; // $NON-NLS-1$
     public static final String CSV_LATENCY = "Latency"; // $NON-NLS-1$
     public static final String CSV_CONNECT_TIME = "Connect"; // $NON-NLS-1$
     public static final String CSV_ENCODING = "Encoding"; // $NON-NLS-1$
     public static final String CSV_HOSTNAME = "Hostname"; // $NON-NLS-1$
     public static final String CSV_IDLETIME = "IdleTime"; // $NON-NLS-1$
 
     // Used to enclose variable name labels, to distinguish from any of the
     // above labels
     private static final String VARIABLE_NAME_QUOTE_CHAR = "\""; // $NON-NLS-1$
 
     // Initial config from properties
     static private final SampleSaveConfiguration _saveConfig = SampleSaveConfiguration
             .staticConfig();
 
     // Date formats to try if the time format does not parse as milliseconds
     private static final String[] DATE_FORMAT_STRINGS = {
         "yyyy/MM/dd HH:mm:ss.SSS",  // $NON-NLS-1$
         "yyyy/MM/dd HH:mm:ss",  // $NON-NLS-1$
         "yyyy-MM-dd HH:mm:ss.SSS",  // $NON-NLS-1$
         "yyyy-MM-dd HH:mm:ss",  // $NON-NLS-1$
 
         "MM/dd/yy HH:mm:ss"  // $NON-NLS-1$ (for compatibility, this is the original default)
         };
 
     private static final String LINE_SEP = System.getProperty("line.separator"); // $NON-NLS-1$
 
     /**
      * Private constructor to prevent instantiation.
      */
     private CSVSaveService() {
     }
 
     /**
      * Read Samples from a file; handles quoted strings.
      * 
      * @param filename
      *            input file
      * @param visualizer
      *            where to send the results
      * @param resultCollector
      *            the parent collector
      * @throws IOException
      *             when the file referenced by <code>filename</code> can't be
      *             read correctly
      */
     public static void processSamples(String filename, Visualizer visualizer,
             ResultCollector resultCollector) throws IOException {
         BufferedReader dataReader = null;
         final boolean errorsOnly = resultCollector.isErrorLogging();
         final boolean successOnly = resultCollector.isSuccessOnlyLogging();
         try {
             dataReader = new BufferedReader(new InputStreamReader(
                     new FileInputStream(filename), SaveService.getFileEncoding(StandardCharsets.UTF_8.name())));
             dataReader.mark(400);// Enough to read the header column names
             // Get the first line, and see if it is the header
             String line = dataReader.readLine();
             if (line == null) {
                 throw new IOException(filename + ": unable to read header line");
             }
             long lineNumber = 1;
             SampleSaveConfiguration saveConfig = CSVSaveService
                     .getSampleSaveConfiguration(line, filename);
             if (saveConfig == null) {// not a valid header
-                log.info(filename
-                        + " does not appear to have a valid header. Using default configuration.");
+                log.info("{} does not appear to have a valid header. Using default configuration.", filename);
                 saveConfig = (SampleSaveConfiguration) resultCollector
                         .getSaveConfig().clone(); // may change the format later
                 dataReader.reset(); // restart from beginning
                 lineNumber = 0;
             }
             String[] parts;
             final char delim = saveConfig.getDelimiter().charAt(0);
             // TODO: does it matter that an empty line will terminate the loop?
             // CSV output files should never contain empty lines, so probably
             // not
             // If so, then need to check whether the reader is at EOF
             while ((parts = csvReadFile(dataReader, delim)).length != 0) {
                 lineNumber++;
                 SampleEvent event = CSVSaveService.makeResultFromDelimitedString(parts, saveConfig, lineNumber);
                 if (event != null) {
                     final SampleResult result = event.getResult();
                     if (ResultCollector.isSampleWanted(result.isSuccessful(),
                             errorsOnly, successOnly)) {
                         visualizer.add(result);
                     }
                 }
             }
         } finally {
             JOrphanUtils.closeQuietly(dataReader);
         }
     }
 
     /**
      * Make a SampleResult given a set of tokens
      * 
      * @param parts
      *            tokens parsed from the input
      * @param saveConfig
      *            the save configuration (may be updated)
      * @param lineNumber the line number (for error reporting)
      * @return the sample result
      * 
      * @throws JMeterError
      */
     private static SampleEvent makeResultFromDelimitedString(
             final String[] parts, 
             final SampleSaveConfiguration saveConfig, // may be updated
             final long lineNumber) {
 
         SampleResult result = null;
         String hostname = "";// $NON-NLS-1$
         long timeStamp = 0;
         long elapsed = 0;
         String text = null;
         String field = null; // Save the name for error reporting
         int i = 0;
         try {
             if (saveConfig.saveTimestamp()) {
                 field = TIME_STAMP;
                 text = parts[i++];
                 if (saveConfig.printMilliseconds()) {
                     try {
                         timeStamp = Long.parseLong(text); // see if this works
                     } catch (NumberFormatException e) { // it did not, let's try some other formats
-                        log.warn(e.toString());
+                        log.warn("Cannot parse timestamp: '{}'", text);
                         boolean foundMatch = false;
                         for(String fmt : DATE_FORMAT_STRINGS) {
                             SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
                             dateFormat.setLenient(false);
                             try {
                                 Date stamp = dateFormat.parse(text);
                                 timeStamp = stamp.getTime();
                                 // method is only ever called from one thread at a time
                                 // so it's OK to use a static DateFormat
-                                log.warn("Setting date format to: " + fmt);
+                                log.warn("Setting date format to: {}", fmt);
                                 saveConfig.setFormatter(dateFormat);
                                 foundMatch = true;
                                 break;
-                            } catch (ParseException e1) {
-                                log.info(text+" did not match "+fmt);
+                            } catch (ParseException pe) {
+                                log.info("{} did not match {}", text, fmt);
                             }
                         }
                         if (!foundMatch) {
                             throw new ParseException("No date-time format found matching "+text,-1);
                         }
                     }
                 } else if (saveConfig.formatter() != null) {
                     Date stamp = saveConfig.formatter().parse(text);
                     timeStamp = stamp.getTime();
                 } else { // can this happen?
                     final String msg = "Unknown timestamp format";
                     log.warn(msg);
                     throw new JMeterError(msg);
                 }
             }
 
             if (saveConfig.saveTime()) {
                 field = CSV_ELAPSED;
                 text = parts[i++];
                 elapsed = Long.parseLong(text);
             }
 
             if (saveConfig.saveSampleCount()) {
                 result = new StatisticalSampleResult(timeStamp, elapsed);
             } else {
                 result = new SampleResult(timeStamp, elapsed);
             }
 
             if (saveConfig.saveLabel()) {
                 field = LABEL;
                 text = parts[i++];
                 result.setSampleLabel(text);
             }
             if (saveConfig.saveCode()) {
                 field = RESPONSE_CODE;
                 text = parts[i++];
                 result.setResponseCode(text);
             }
 
             if (saveConfig.saveMessage()) {
                 field = RESPONSE_MESSAGE;
                 text = parts[i++];
                 result.setResponseMessage(text);
             }
 
             if (saveConfig.saveThreadName()) {
                 field = THREAD_NAME;
                 text = parts[i++];
                 result.setThreadName(text);
             }
 
             if (saveConfig.saveDataType()) {
                 field = DATA_TYPE;
                 text = parts[i++];
                 result.setDataType(text);
             }
 
             if (saveConfig.saveSuccess()) {
                 field = SUCCESSFUL;
                 text = parts[i++];
                 result.setSuccessful(Boolean.valueOf(text).booleanValue());
             }
 
             if (saveConfig.saveAssertionResultsFailureMessage()) {
                 i++;
                 // TODO - should this be restored?
             }
 
             if (saveConfig.saveBytes()) {
                 field = CSV_BYTES;
                 text = parts[i++];
                 result.setBytes(Long.parseLong(text));
             }
 
             if (saveConfig.saveSentBytes()) {
                 field = CSV_SENT_BYTES;
                 text = parts[i++];
                 result.setSentBytes(Long.parseLong(text));
             }
 
             if (saveConfig.saveThreadCounts()) {
                 field = CSV_THREAD_COUNT1;
                 text = parts[i++];
                 result.setGroupThreads(Integer.parseInt(text));
 
                 field = CSV_THREAD_COUNT2;
                 text = parts[i++];
                 result.setAllThreads(Integer.parseInt(text));
             }
 
             if (saveConfig.saveUrl()) {
                 i++;
                 // TODO: should this be restored?
             }
 
             if (saveConfig.saveFileName()) {
                 field = CSV_FILENAME;
                 text = parts[i++];
                 result.setResultFileName(text);
             }
             if (saveConfig.saveLatency()) {
                 field = CSV_LATENCY;
                 text = parts[i++];
                 result.setLatency(Long.parseLong(text));
             }
 
             if (saveConfig.saveEncoding()) {
                 field = CSV_ENCODING;
                 text = parts[i++];
                 result.setEncodingAndType(text);
             }
 
             if (saveConfig.saveSampleCount()) {
                 field = CSV_SAMPLE_COUNT;
                 text = parts[i++];
                 result.setSampleCount(Integer.parseInt(text));
                 field = CSV_ERROR_COUNT;
                 text = parts[i++];
                 result.setErrorCount(Integer.parseInt(text));
             }
 
             if (saveConfig.saveHostname()) {
                 field = CSV_HOSTNAME;
                 hostname = parts[i++];
             }
 
             if (saveConfig.saveIdleTime()) {
                 field = CSV_IDLETIME;
                 text = parts[i++];
                 result.setIdleTime(Long.parseLong(text));
             }
             if (saveConfig.saveConnectTime()) {
                 field = CSV_CONNECT_TIME;
                 text = parts[i++];
                 result.setConnectTime(Long.parseLong(text));
             }
 
             if (i + saveConfig.getVarCount() < parts.length) {
-                log.warn("Line: " + lineNumber + ". Found " + parts.length
-                        + " fields, expected " + i
-                        + ". Extra fields have been ignored.");
+                log.warn("Line: {}. Found {} fields, expected {}. Extra fields have been ignored.", lineNumber,
+                        parts.length, i);
             }
 
         } catch (NumberFormatException | ParseException e) {
-            log.warn("Error parsing field '" + field + "' at line "
-                    + lineNumber + " " + e);
+            if (log.isWarnEnabled()) {
+                log.warn("Error parsing field '{}' at line {}. {}", field, lineNumber, e.toString());
+            }
             throw new JMeterError(e);
         } catch (ArrayIndexOutOfBoundsException e) {
-            log.warn("Insufficient columns to parse field '" + field
-                    + "' at line " + lineNumber);
+            log.warn("Insufficient columns to parse field '{}' at line {}", field, lineNumber);
             throw new JMeterError(e);
         }
         return new SampleEvent(result, "", hostname);
     }
 
     /**
      * Generates the field names for the output file
      * 
      * @return the field names as a string
      */
     public static String printableFieldNamesToString() {
         return printableFieldNamesToString(_saveConfig);
     }
 
     /**
      * Generates the field names for the output file
      * 
      * @param saveConfig
      *            the configuration of what is to be saved
      * @return the field names as a string
      */
     public static String printableFieldNamesToString(
             SampleSaveConfiguration saveConfig) {
         StringBuilder text = new StringBuilder();
         String delim = saveConfig.getDelimiter();
 
         if (saveConfig.saveTimestamp()) {
             text.append(TIME_STAMP);
             text.append(delim);
         }
 
         if (saveConfig.saveTime()) {
             text.append(CSV_ELAPSED);
             text.append(delim);
         }
 
         if (saveConfig.saveLabel()) {
             text.append(LABEL);
             text.append(delim);
         }
 
         if (saveConfig.saveCode()) {
             text.append(RESPONSE_CODE);
             text.append(delim);
         }
 
         if (saveConfig.saveMessage()) {
             text.append(RESPONSE_MESSAGE);
             text.append(delim);
         }
 
         if (saveConfig.saveThreadName()) {
             text.append(THREAD_NAME);
             text.append(delim);
         }
 
         if (saveConfig.saveDataType()) {
             text.append(DATA_TYPE);
             text.append(delim);
         }
 
         if (saveConfig.saveSuccess()) {
             text.append(SUCCESSFUL);
             text.append(delim);
         }
 
         if (saveConfig.saveAssertionResultsFailureMessage()) {
             text.append(FAILURE_MESSAGE);
             text.append(delim);
         }
 
         if (saveConfig.saveBytes()) {
             text.append(CSV_BYTES);
             text.append(delim);
         }
 
         if (saveConfig.saveSentBytes()) {
             text.append(CSV_SENT_BYTES);
             text.append(delim);
         }
 
         if (saveConfig.saveThreadCounts()) {
             text.append(CSV_THREAD_COUNT1);
             text.append(delim);
             text.append(CSV_THREAD_COUNT2);
             text.append(delim);
         }
 
         if (saveConfig.saveUrl()) {
             text.append(CSV_URL);
             text.append(delim);
         }
 
         if (saveConfig.saveFileName()) {
             text.append(CSV_FILENAME);
             text.append(delim);
         }
 
         if (saveConfig.saveLatency()) {
             text.append(CSV_LATENCY);
             text.append(delim);
         }
 
         if (saveConfig.saveEncoding()) {
             text.append(CSV_ENCODING);
             text.append(delim);
         }
 
         if (saveConfig.saveSampleCount()) {
             text.append(CSV_SAMPLE_COUNT);
             text.append(delim);
             text.append(CSV_ERROR_COUNT);
             text.append(delim);
         }
 
         if (saveConfig.saveHostname()) {
             text.append(CSV_HOSTNAME);
             text.append(delim);
         }
 
         if (saveConfig.saveIdleTime()) {
             text.append(CSV_IDLETIME);
             text.append(delim);
         }
 
         if (saveConfig.saveConnectTime()) {
             text.append(CSV_CONNECT_TIME);
             text.append(delim);
         }
 
         for (int i = 0; i < SampleEvent.getVarCount(); i++) {
             text.append(VARIABLE_NAME_QUOTE_CHAR);
             text.append(SampleEvent.getVarName(i));
             text.append(VARIABLE_NAME_QUOTE_CHAR);
             text.append(delim);
         }
 
         String resultString = null;
         int size = text.length();
         int delSize = delim.length();
 
         // Strip off the trailing delimiter
         if (size >= delSize) {
             resultString = text.substring(0, size - delSize);
         } else {
             resultString = text.toString();
         }
         return resultString;
     }
 
     // Map header names to set() methods
     private static final LinkedMap headerLabelMethods = new LinkedMap();
 
     // These entries must be in the same order as columns are saved/restored.
 
     static {
         headerLabelMethods.put(TIME_STAMP, new Functor("setTimestamp"));
         headerLabelMethods.put(CSV_ELAPSED, new Functor("setTime"));
         headerLabelMethods.put(LABEL, new Functor("setLabel"));
         headerLabelMethods.put(RESPONSE_CODE, new Functor("setCode"));
         headerLabelMethods.put(RESPONSE_MESSAGE, new Functor("setMessage"));
         headerLabelMethods.put(THREAD_NAME, new Functor("setThreadName"));
         headerLabelMethods.put(DATA_TYPE, new Functor("setDataType"));
         headerLabelMethods.put(SUCCESSFUL, new Functor("setSuccess"));
         headerLabelMethods.put(FAILURE_MESSAGE, new Functor(
                 "setAssertionResultsFailureMessage"));
         headerLabelMethods.put(CSV_BYTES, new Functor("setBytes"));
         headerLabelMethods.put(CSV_SENT_BYTES, new Functor("setSentBytes"));
         // Both these are needed in the list even though they set the same
         // variable
         headerLabelMethods.put(CSV_THREAD_COUNT1,
                 new Functor("setThreadCounts"));
         headerLabelMethods.put(CSV_THREAD_COUNT2,
                 new Functor("setThreadCounts"));
         headerLabelMethods.put(CSV_URL, new Functor("setUrl"));
         headerLabelMethods.put(CSV_FILENAME, new Functor("setFileName"));
         headerLabelMethods.put(CSV_LATENCY, new Functor("setLatency"));
         headerLabelMethods.put(CSV_ENCODING, new Functor("setEncoding"));
         // Both these are needed in the list even though they set the same
         // variable
         headerLabelMethods.put(CSV_SAMPLE_COUNT, new Functor("setSampleCount"));
         headerLabelMethods.put(CSV_ERROR_COUNT, new Functor("setSampleCount"));
         headerLabelMethods.put(CSV_HOSTNAME, new Functor("setHostname"));
         headerLabelMethods.put(CSV_IDLETIME, new Functor("setIdleTime"));
         headerLabelMethods.put(CSV_CONNECT_TIME, new Functor("setConnectTime"));
     }
 
     /**
      * Parse a CSV header line
      * 
      * @param headerLine
      *            from CSV file
      * @param filename
      *            name of file (for log message only)
      * @return config corresponding to the header items found or null if not a
      *         header line
      */
     public static SampleSaveConfiguration getSampleSaveConfiguration(
             String headerLine, String filename) {
         String[] parts = splitHeader(headerLine, _saveConfig.getDelimiter()); // Try
                                                                               // default
                                                                               // delimiter
 
         String delim = null;
 
         if (parts == null) {
             Perl5Matcher matcher = JMeterUtils.getMatcher();
             PatternMatcherInput input = new PatternMatcherInput(headerLine);
             Pattern pattern = JMeterUtils.getPatternCache()
             // This assumes the header names are all single words with no spaces
             // word followed by 0 or more repeats of (non-word char + word)
             // where the non-word char (\2) is the same
             // e.g. abc|def|ghi but not abd|def~ghi
                     .getPattern("\\w+((\\W)\\w+)?(\\2\\w+)*(\\2\"\\w+\")*", // $NON-NLS-1$
                             // last entries may be quoted strings
                             Perl5Compiler.READ_ONLY_MASK);
             if (matcher.matches(input, pattern)) {
                 delim = matcher.getMatch().group(2);
                 parts = splitHeader(headerLine, delim);// now validate the
                                                        // result
             }
         }
 
         if (parts == null) {
             return null; // failed to recognise the header
         }
 
         // We know the column names all exist, so create the config
         SampleSaveConfiguration saveConfig = new SampleSaveConfiguration(false);
 
         int varCount = 0;
         for (String label : parts) {
             if (isVariableName(label)) {
                 varCount++;
             } else {
                 Functor set = (Functor) headerLabelMethods.get(label);
                 set.invoke(saveConfig, new Boolean[]{Boolean.TRUE});
             }
         }
 
         if (delim != null) {
-            log.warn("Default delimiter '" + _saveConfig.getDelimiter()
-                    + "' did not work; using alternate '" + delim
-                    + "' for reading " + filename);
+            if (log.isWarnEnabled()) {
+                log.warn("Default delimiter '{}' did not work; using alternate '{}' for reading {}",
+                        _saveConfig.getDelimiter(), delim, filename);
+            }
             saveConfig.setDelimiter(delim);
         }
 
         saveConfig.setVarCount(varCount);
 
         return saveConfig;
     }
 
     private static String[] splitHeader(String headerLine, String delim) {
         String[] parts = headerLine.split("\\Q" + delim);// $NON-NLS-1$
         int previous = -1;
         // Check if the line is a header
         for (int i = 0; i < parts.length; i++) {
             final String label = parts[i];
             // Check for Quoted variable names
             if (isVariableName(label)) {
                 previous = Integer.MAX_VALUE; // they are always last
                 continue;
             }
             int current = headerLabelMethods.indexOf(label);
             if (current == -1) {
-                log.warn("Unknown column name " + label);
+                log.warn("Unknown column name {}", label);
                 return null; // unknown column name
             }
             if (current <= previous) {
-                log.warn("Column header number " + (i + 1) + " name " + label
-                        + " is out of order.");
+                log.warn("Column header number {} name {} is out of order.", (i + 1), label);
                 return null; // out of order
             }
             previous = current;
         }
         return parts;
     }
 
     /**
      * Check if the label is a variable name, i.e. is it enclosed in
      * double-quotes?
      * 
      * @param label
      *            column name from CSV file
      * @return if the label is enclosed in double-quotes
      */
     private static boolean isVariableName(final String label) {
         return label.length() > 2 && label.startsWith(VARIABLE_NAME_QUOTE_CHAR)
                 && label.endsWith(VARIABLE_NAME_QUOTE_CHAR);
     }
 
     /**
      * Method will save aggregate statistics as CSV. For now I put it here. Not
      * sure if it should go in the newer SaveService instead of here. if we ever
      * decide to get rid of this class, we'll need to move this method to the
      * new save service.
      * 
      * @param data
      *            List of data rows
      * @param writer
      *            output writer
      * @throws IOException
      *             when writing to <code>writer</code> fails
      */
     public static void saveCSVStats(List<?> data, Writer writer)
             throws IOException {
         saveCSVStats(data, writer, null);
     }
 
     /**
      * Method will save aggregate statistics as CSV. For now I put it here. Not
      * sure if it should go in the newer SaveService instead of here. if we ever
      * decide to get rid of this class, we'll need to move this method to the
      * new save service.
      * 
      * @param data
      *            List of data rows
      * @param writer
      *            output file
      * @param headers
      *            header names (if non-null)
      * @throws IOException
      *             when writing to <code>writer</code> fails
      */
     public static void saveCSVStats(List<?> data, Writer writer,
             String[] headers) throws IOException {
         final char DELIM = ',';
         final char[] SPECIALS = new char[] { DELIM, QUOTING_CHAR };
         if (headers != null) {
             for (int i = 0; i < headers.length; i++) {
                 if (i > 0) {
                     writer.write(DELIM);
                 }
                 writer.write(quoteDelimiters(headers[i], SPECIALS));
             }
             writer.write(LINE_SEP);
         }
         for (Object o : data) {
             List<?> row = (List<?>) o;
             for (int idy = 0; idy < row.size(); idy++) {
                 if (idy > 0) {
                     writer.write(DELIM);
                 }
                 Object item = row.get(idy);
                 writer.write(quoteDelimiters(String.valueOf(item), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
     }
 
     /**
      * Method saves aggregate statistics (with header names) as CSV from a table
      * model. Same as {@link #saveCSVStats(List, FileWriter, String[])} except
      * that there is no need to create a List containing the data.
      * 
      * @param model
      *            table model containing the data
      * @param writer
      *            output file
      * @throws IOException
      *             when writing to <code>writer</code> fails
      */
     public static void saveCSVStats(DefaultTableModel model, FileWriter writer)
             throws IOException {
         saveCSVStats(model, writer, true);
     }
 
     /**
      * Method saves aggregate statistics as CSV from a table model. Same as
      * {@link #saveCSVStats(List, FileWriter, String[])} except that there is no
      * need to create a List containing the data.
      * 
      * @param model
      *            table model containing the data
      * @param writer
      *            output file
      * @param saveHeaders
      *            whether or not to save headers
      * @throws IOException
      *             when writing to <code>writer</code> fails
      */
     public static void saveCSVStats(DefaultTableModel model, FileWriter writer,
             boolean saveHeaders) throws IOException {
         final char DELIM = ',';
         final char[] SPECIALS = new char[] { DELIM, QUOTING_CHAR };
         final int columns = model.getColumnCount();
         final int rows = model.getRowCount();
         if (saveHeaders) {
             for (int i = 0; i < columns; i++) {
                 if (i > 0) {
                     writer.write(DELIM);
                 }
                 writer.write(quoteDelimiters(model.getColumnName(i), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
         for (int row = 0; row < rows; row++) {
             for (int column = 0; column < columns; column++) {
                 if (column > 0) {
                     writer.write(DELIM);
                 }
                 Object item = model.getValueAt(row, column);
                 writer.write(quoteDelimiters(String.valueOf(item), SPECIALS));
             }
             writer.write(LINE_SEP);
         }
     }
 
     /**
      * Convert a result into a string, where the fields of the result are
      * separated by the default delimiter.
      * 
      * @param event
      *            the sample event to be converted
      * @return the separated value representation of the result
      */
     public static String resultToDelimitedString(SampleEvent event) {
         return resultToDelimitedString(event, event.getResult().getSaveConfig()
                 .getDelimiter());
     }
     
     /*
      * Class to handle generating the delimited string. - adds the delimiter
      * if not the first call - quotes any strings that require it
      */
     static final class StringQuoter {
         private final StringBuilder sb;
         private final char[] specials;
         private boolean addDelim;
 
         public StringQuoter(char delim) {
             sb = new StringBuilder(150);
             specials = new char[] { delim, QUOTING_CHAR, CharUtils.CR,
                     CharUtils.LF };
             addDelim = false; // Don't add delimiter first time round
         }
 
         private void addDelim() {
             if (addDelim) {
                 sb.append(specials[0]);
             } else {
                 addDelim = true;
             }
         }
 
         // These methods handle parameters that could contain delimiters or
         // quotes:
         public void append(String s) {
             addDelim();
             // if (s == null) return;
             sb.append(quoteDelimiters(s, specials));
         }
 
         public void append(Object obj) {
             append(String.valueOf(obj));
         }
 
         // These methods handle parameters that cannot contain delimiters or
         // quotes
         public void append(int i) {
             addDelim();
             sb.append(i);
         }
 
         public void append(long l) {
             addDelim();
             sb.append(l);
         }
 
         public void append(boolean b) {
             addDelim();
             sb.append(b);
         }
 
         @Override
         public String toString() {
             return sb.toString();
         }
     }
 
     /**
      * Convert a result into a string, where the fields of the result are
      * separated by a specified String.
      * 
      * @param event
      *            the sample event to be converted
      * @param delimiter
      *            the separation string
      * @return the separated value representation of the result
      */
     public static String resultToDelimitedString(SampleEvent event,
             final String delimiter) {
         StringQuoter text = new StringQuoter(delimiter.charAt(0));
 
         SampleResult sample = event.getResult();
         SampleSaveConfiguration saveConfig = sample.getSaveConfig();
 
         if (saveConfig.saveTimestamp()) {
             if (saveConfig.printMilliseconds()) {
                 text.append(sample.getTimeStamp());
             } else if (saveConfig.formatter() != null) {
                 String stamp = saveConfig.formatter().format(
                         new Date(sample.getTimeStamp()));
                 text.append(stamp);
             }
         }
 
         if (saveConfig.saveTime()) {
             text.append(sample.getTime());
         }
 
         if (saveConfig.saveLabel()) {
             text.append(sample.getSampleLabel());
         }
 
         if (saveConfig.saveCode()) {
             text.append(sample.getResponseCode());
         }
 
         if (saveConfig.saveMessage()) {
             text.append(sample.getResponseMessage());
         }
 
         if (saveConfig.saveThreadName()) {
             text.append(sample.getThreadName());
         }
 
         if (saveConfig.saveDataType()) {
             text.append(sample.getDataType());
         }
 
         if (saveConfig.saveSuccess()) {
             text.append(sample.isSuccessful());
         }
 
         if (saveConfig.saveAssertionResultsFailureMessage()) {
             String message = null;
             AssertionResult[] results = sample.getAssertionResults();
 
             if (results != null) {
                 // Find the first non-null message
                 for (AssertionResult result : results) {
                     message = result.getFailureMessage();
                     if (message != null) {
                         break;
                     }
                 }
             }
 
             if (message != null) {
                 text.append(message);
             } else {
                 text.append(""); // Need to append something so delimiter is
                                  // added
             }
         }
 
         if (saveConfig.saveBytes()) {
             text.append(sample.getBytesAsLong());
         }
 
         if (saveConfig.saveSentBytes()) {
             text.append(sample.getSentBytes());
         }
 
         if (saveConfig.saveThreadCounts()) {
             text.append(sample.getGroupThreads());
             text.append(sample.getAllThreads());
         }
         if (saveConfig.saveUrl()) {
             text.append(sample.getURL());
         }
 
         if (saveConfig.saveFileName()) {
             text.append(sample.getResultFileName());
         }
 
         if (saveConfig.saveLatency()) {
             text.append(sample.getLatency());
         }
 
         if (saveConfig.saveEncoding()) {
             text.append(sample.getDataEncodingWithDefault());
         }
 
         if (saveConfig.saveSampleCount()) {
             // Need both sample and error count to be any use
             text.append(sample.getSampleCount());
             text.append(sample.getErrorCount());
         }
 
         if (saveConfig.saveHostname()) {
             text.append(event.getHostname());
         }
 
         if (saveConfig.saveIdleTime()) {
             text.append(event.getResult().getIdleTime());
         }
 
         if (saveConfig.saveConnectTime()) {
             text.append(sample.getConnectTime());
         }
 
         for (int i = 0; i < SampleEvent.getVarCount(); i++) {
             text.append(event.getVarValue(i));
         }
 
         return text.toString();
     }
 
     // =================================== CSV quote/unquote handling
     // ==============================
 
     /*
      * Private versions of what might eventually be part of Commons-CSV or
      * Commons-Lang/Io...
      */
 
     /**
      * <p> Returns a <code>String</code> value for a character-delimited column
      * value enclosed in the quote character, if required. </p>
      * 
      * <p> If the value contains a special character, then the String value is
      * returned enclosed in the quote character. </p>
      * 
      * <p> Any quote characters in the value are doubled up. </p>
      * 
      * <p> If the value does not contain any special characters, then the String
      * value is returned unchanged. </p>
      * 
      * <p> N.B. The list of special characters includes the quote character.
      * </p>
      * 
      * @param input the input column String, may be null (without enclosing
      * delimiters)
      * 
      * @param specialChars special characters; second one must be the quote
      * character
      * 
      * @return the input String, enclosed in quote characters if the value
      * contains a special character, <code>null</code> for null string input
      */
     public static String quoteDelimiters(String input, char[] specialChars) {
         if (StringUtils.containsNone(input, specialChars)) {
             return input;
         }
         StringBuilder buffer = new StringBuilder(input.length() + 10);
         final char quote = specialChars[1];
         buffer.append(quote);
         for (int i = 0; i < input.length(); i++) {
             char c = input.charAt(i);
             if (c == quote) {
                 buffer.append(quote); // double the quote char
             }
             buffer.append(c);
         }
         buffer.append(quote);
         return buffer.toString();
     }
 
     // State of the parser
     private enum ParserState {INITIAL, PLAIN, QUOTED, EMBEDDEDQUOTE}
 
     public static final char QUOTING_CHAR = '"';
 
     /**
      * Reads from file and splits input into strings according to the delimiter,
      * taking note of quoted strings.
      * <p>
      * Handles DOS (CRLF), Unix (LF), and Mac (CR) line-endings equally.
      * <p>
      * A blank line - or a quoted blank line - both return an array containing
      * a single empty String.
      * @param infile
      *            input file - must support mark(1)
      * @param delim
      *            delimiter (e.g. comma)
      * @return array of strings, will be empty if there is no data, i.e. if the input is at EOF.
      * @throws IOException
      *             also for unexpected quote characters
      */
     public static String[] csvReadFile(BufferedReader infile, char delim)
             throws IOException {
         int ch;
         ParserState state = ParserState.INITIAL;
         List<String> list = new ArrayList<>();
         CharArrayWriter baos = new CharArrayWriter(200);
         boolean push = false;
         while (-1 != (ch = infile.read())) {
             push = false;
             switch (state) {
             case INITIAL:
                 if (ch == QUOTING_CHAR) {
                     state = ParserState.QUOTED;
                 } else if (isDelimOrEOL(delim, ch)) {
                     push = true;
                 } else {
                     baos.write(ch);
                     state = ParserState.PLAIN;
                 }
                 break;
             case PLAIN:
                 if (ch == QUOTING_CHAR) {
                     baos.write(ch);
                     throw new IOException(
                             "Cannot have quote-char in plain field:["
                                     + baos.toString() + "]");
                 } else if (isDelimOrEOL(delim, ch)) {
                     push = true;
                     state = ParserState.INITIAL;
                 } else {
                     baos.write(ch);
                 }
                 break;
             case QUOTED:
                 if (ch == QUOTING_CHAR) {
                     state = ParserState.EMBEDDEDQUOTE;
                 } else {
                     baos.write(ch);
                 }
                 break;
             case EMBEDDEDQUOTE:
                 if (ch == QUOTING_CHAR) {
                     baos.write(QUOTING_CHAR); // doubled quote => quote
                     state = ParserState.QUOTED;
                 } else if (isDelimOrEOL(delim, ch)) {
                     push = true;
                     state = ParserState.INITIAL;
                 } else {
                     baos.write(QUOTING_CHAR);
                     throw new IOException(
                             "Cannot have single quote-char in quoted field:["
                                     + baos.toString() + "]");
                 }
                 break;
             default:
                 throw new IllegalStateException("Unexpected state " + state);
             } // switch(state)
             if (push) {
                 if (ch == '\r') {// Remove following \n if present
                     infile.mark(1);
                     if (infile.read() != '\n') {
                         infile.reset(); // did not find \n, put the character
                                         // back
                     }
                 }
                 String s = baos.toString();
                 list.add(s);
                 baos.reset();
             }
             if ((ch == '\n' || ch == '\r') && state != ParserState.QUOTED) {
                 break;
             }
         } // while not EOF
         if (ch == -1) {// EOF (or end of string) so collect any remaining data
             if (state == ParserState.QUOTED) {
                 throw new IOException("Missing trailing quote-char in quoted field:[\""
                         + baos.toString() + "]");
             }
             // Do we have some data, or a trailing empty field?
             if (baos.size() > 0 // we have some data
                     || push // we've started a field
                     || state == ParserState.EMBEDDEDQUOTE // Just seen ""
             ) {
                 list.add(baos.toString());
             }
         }
         return list.toArray(new String[list.size()]);
     }
 
     private static boolean isDelimOrEOL(char delim, int ch) {
         return ch == delim || ch == '\n' || ch == '\r';
     }
 
     /**
      * Reads from String and splits into strings according to the delimiter,
      * taking note of quoted strings.
      * 
      * Handles DOS (CRLF), Unix (LF), and Mac (CR) line-endings equally.
      * 
      * @param line
      *            input line - not {@code null}
      * @param delim
      *            delimiter (e.g. comma)
      * @return array of strings
      * @throws IOException
      *             also for unexpected quote characters
      */
     public static String[] csvSplitString(String line, char delim)
             throws IOException {
         return csvReadFile(new BufferedReader(new StringReader(line)), delim);
     }
 }
diff --git a/src/core/org/apache/jmeter/save/SaveService.java b/src/core/org/apache/jmeter/save/SaveService.java
index ec9893a4b..d7d53e863 100644
--- a/src/core/org/apache/jmeter/save/SaveService.java
+++ b/src/core/org/apache/jmeter/save/SaveService.java
@@ -1,570 +1,570 @@
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
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.XStream;
 import com.thoughtworks.xstream.converters.ConversionException;
 import com.thoughtworks.xstream.converters.Converter;
 import com.thoughtworks.xstream.converters.DataHolder;
 import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
 import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
 import com.thoughtworks.xstream.io.xml.XppDriver;
 import com.thoughtworks.xstream.mapper.CannotResolveClassException;
 import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.mapper.MapperWrapper;
 
 /**
  * Handles setting up XStream serialisation.
  * The class reads alias definitions from saveservice.properties.
  *
  */
 public class SaveService {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SaveService.class);
 
     // Names of DataHolder entries for JTL processing
     public static final String SAMPLE_EVENT_OBJECT = "SampleEvent"; // $NON-NLS-1$
     public static final String RESULTCOLLECTOR_HELPER_OBJECT = "ResultCollectorHelper"; // $NON-NLS-1$
 
     // Names of DataHolder entries for JMX processing
     public static final String TEST_CLASS_NAME = "TestClassName"; // $NON-NLS-1$
 
     private static final class XStreamWrapper extends XStream {
         private XStreamWrapper(ReflectionProvider reflectionProvider) {
             super(reflectionProvider);
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
 
     // Define file format versions
     private static final String VERSION_2_2 = "2.2";  // $NON-NLS-1$
 
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
     static final String PROPVERSION = "3.2";// Expected version $NON-NLS-1$
 
     // Internal information only
     private static String fileVersion = ""; // computed from saveservice.properties file// $NON-NLS-1$
     // Must match the sha1 checksum of the file saveservice.properties (without newline character),
     // used to ensure saveservice.properties and SaveService are updated simultaneously
     static final String FILEVERSION = "2b8bbf6ee18f324d63d4c69981561fd9e125dd99"; // Expected value $NON-NLS-1$
 
     private static String fileEncoding = ""; // read from properties file// $NON-NLS-1$
 
     static {
-        log.info("Testplan (JMX) version: "+VERSION_2_2+". Testlog (JTL) version: "+VERSION_2_2);
+        log.info("Testplan (JMX) version: {}. Testlog (JTL) version: {}", VERSION_2_2, VERSION_2_2);
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
-                log.error("Duplicate class detected for "+alias+": "+clazz+" & "+old);                
+                log.error("Duplicate class detected for {}: {} & {}", alias, clazz, old);
             }
         }
         Object oldval=classToAlias.setProperty(clazz,alias);
         if (oldval != null) {
-            log.error("Duplicate alias detected for "+clazz+": "+alias+" & "+oldval);
+            log.error("Duplicate alias detected for {}: {} & {}", clazz, alias, oldval);
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
-            log.fatalError("Can't compute checksum for saveservice properties file", e);
+            log.error("Can't compute checksum for saveservice properties file", e);
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
-                        log.info("Using SaveService properties version " + propertiesVersion);
+                        log.info("Using SaveService properties version {}", propertiesVersion);
                     } else if (key.equalsIgnoreCase("_file_version")) { // $NON-NLS-1$
                         log.info("SaveService properties file version is now computed by a checksum,"
                                 + "the property _file_version is not used anymore and can be removed.");
                     } else if (key.equalsIgnoreCase("_file_encoding")) { // $NON-NLS-1$
                         fileEncoding = val;
-                        log.info("Using SaveService properties file encoding " + fileEncoding);
+                        log.info("Using SaveService properties file encoding {}", fileEncoding);
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
-                            log.warn("Can't register a converter: " + key, e1);
+                            log.warn("Can't register a converter: {}", key, e1);
                         }
                     }
                 }
             }
         } catch (IOException e) {
-            log.fatalError("Bad saveservice properties file", e);
+            log.error("Bad saveservice properties file", e);
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
         JMXSAVER.toXML(wrapper, outputStreamWriter);
         outputStreamWriter.write('\n');// Ensure terminated properly
         outputStreamWriter.close();
     }
 
     // Used by Test code
     public static void saveElement(Object el, OutputStream out) throws IOException {
         // Get the OutputWriter to use
         OutputStreamWriter outputStreamWriter = getOutputStreamWriter(out);
         writeXmlHeader(outputStreamWriter);
         // Use deprecated method, to avoid duplicating code
         JMXSAVER.toXML(el, outputStreamWriter);
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
             JTLSAVER.marshal(evt.getResult(), new XppDriver().createWriter(writer), dh);
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
-                        log.error("Unexpected entry in saveservice.properties; class does not exist and is not upgraded: "+name);              
+                        log.error("Unexpected entry in saveservice.properties; class does not exist and is not upgraded: {}", name);
                         missingClasses.add(name);
                 }
             }
         }
         return missingClasses;
     }
 
     private static void checkVersions() {
         if (!PROPVERSION.equalsIgnoreCase(propertiesVersion)) {
-            log.warn("Bad _version - expected " + PROPVERSION + ", found " + propertiesVersion + ".");
+            log.warn("Bad _version - expected {}, found {}.", PROPVERSION, propertiesVersion);
         }
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
         JTLSAVER.unmarshal(new XppDriver().createReader(reader), null, dh);
         inputStreamReader.close();
     }
     
     /**
      * Load a Test tree (JMX file)
      * @param file the JMX file
      * @return the loaded tree
      * @throws IOException if there is a problem reading the file or processing it
      */
     public static HashTree loadTree(File file) throws IOException {
-        log.info("Loading file: " + file);
+        log.info("Loading file: {}", file);
         try (InputStream inputStream = new FileInputStream(file);
                 BufferedInputStream bufferedInputStream = 
                     new BufferedInputStream(inputStream)){
             return readTree(bufferedInputStream, file);
         }
     }
 
     /**
      * 
      * @param inputStream {@link InputStream} 
      * @param file the JMX file used only for debug, can be null
      * @return the loaded tree
      * @throws IOException if there is a problem reading the file or processing it
      */
     private static HashTree readTree(InputStream inputStream, File file)
             throws IOException {
         ScriptWrapper wrapper = null;
         try {
             // Get the InputReader to use
             InputStreamReader inputStreamReader = getInputStreamReader(inputStream);
             wrapper = (ScriptWrapper) JMXSAVER.fromXML(inputStreamReader);
             inputStreamReader.close();
             if (wrapper == null){
                 log.error("Problem loading XML: see above.");
                 return null;
             }
             return wrapper.testPlan;
         } catch (CannotResolveClassException e) {
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
diff --git a/src/core/org/apache/jmeter/save/converters/ConversionHelp.java b/src/core/org/apache/jmeter/save/converters/ConversionHelp.java
index 1d54d2b86..5953fce3b 100644
--- a/src/core/org/apache/jmeter/save/converters/ConversionHelp.java
+++ b/src/core/org/apache/jmeter/save/converters/ConversionHelp.java
@@ -1,327 +1,327 @@
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
  * Created on Jul 27, 2004
  */
 package org.apache.jmeter.save.converters;
 
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.net.URLEncoder;
 import java.nio.charset.StandardCharsets;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.util.NameUpdater;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 
 /**
  * Utility conversion routines for use with XStream
  *
  */
 public class ConversionHelp {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(ConversionHelp.class);
 
     private static final String CHAR_SET = StandardCharsets.UTF_8.name();
 
     // Attributes for TestElement and TestElementProperty
     // Must all be unique
     public static final String ATT_CLASS         = "class"; //$NON-NLS-1$
     // Also used by PropertyConverter classes
     public static final String ATT_NAME          = "name"; // $NON-NLS-1$
     public static final String ATT_ELEMENT_TYPE  = "elementType"; // $NON-NLS-1$
 
     private static final String ATT_TE_ENABLED   = "enabled"; //$NON-NLS-1$
     private static final String ATT_TE_TESTCLASS = "testclass"; //$NON-NLS-1$
             static final String ATT_TE_GUICLASS  = "guiclass"; //$NON-NLS-1$
     private static final String ATT_TE_NAME      = "testname"; //$NON-NLS-1$
 
 
     /*
      * These must be set before reading/writing the XML. Rather a hack, but
      * saves changing all the method calls to include an extra variable.
      *
      * AFAIK the variables should only be accessed from one thread, so no need to synchronize.
      */
     private static String inVersion;
 
     private static String outVersion = "1.1"; // Default for writing//$NON-NLS-1$
 
     public static void setInVersion(String v) {
         inVersion = v;
     }
 
     public static void setOutVersion(String v) {
         outVersion = v;
     }
 
     /**
      * Encode a string (if necessary) for output to a JTL file.
      * Strings are only encoded if the output version is 1.0,
      * but nulls are always converted to the empty string.
      *
      * @param p string to encode
      * @return encoded string (will never be null)
      */
     public static String encode(String p) {
         if (p == null) {// Nulls cannot be written using PrettyPrintWriter - they cause an NPE
             return ""; // $NON-NLS-1$
         }
         // Only encode strings if outVersion = 1.0
         if (!"1.0".equals(outVersion)) {//$NON-NLS-1$
             return p;
         }
         try {
             String p1 = URLEncoder.encode(p, CHAR_SET);
             return p1;
         } catch (UnsupportedEncodingException e) {
-            log.warn("System doesn't support " + CHAR_SET, e);
+            log.warn("System doesn't support {}", CHAR_SET, e);
             return p;
         }
     }
 
     /**
      * Decode a string if {@link #inVersion} equals <code>1.0</code>
      * 
      * @param p
      *            the string to be decoded
      * @return the newly decoded string
      */
     public static String decode(String p) {
         if (!"1.0".equals(inVersion)) {//$NON-NLS-1$
             return p;
         }
         // Only decode strings if inVersion = 1.0
         if (p == null) {
             return null;
         }
         try {
             return URLDecoder.decode(p, CHAR_SET);
         } catch (UnsupportedEncodingException e) {
-            log.warn("System doesn't support " + CHAR_SET, e);
+            log.warn("System doesn't support {}", CHAR_SET, e);
             return p;
         }
     }
 
     /**
      * Embed an array of bytes as a string with <code>encoding</code> in a
      * xml-cdata section
      * 
      * @param chars
      *            bytes to be encoded and embedded
      * @param encoding
      *            the encoding to be used
      * @return the encoded string embedded in a xml-cdata section
      * @throws UnsupportedEncodingException
      *             when the bytes can not be encoded using <code>encoding</code>
      */
     public static String cdata(byte[] chars, String encoding) throws UnsupportedEncodingException {
         StringBuilder buf = new StringBuilder("<![CDATA[");
         buf.append(new String(chars, encoding));
         buf.append("]]>");
         return buf.toString();
     }
 
     /**
      *  Names of properties that are handled specially
      */
     private static final Map<String, String> propertyToAttribute = new HashMap<>();
 
     private static void mapentry(String prop, String att){
         propertyToAttribute.put(prop,att);
     }
 
     static{
         mapentry(TestElement.NAME,ATT_TE_NAME);
         mapentry(TestElement.GUI_CLASS,ATT_TE_GUICLASS);//$NON-NLS-1$
         mapentry(TestElement.TEST_CLASS,ATT_TE_TESTCLASS);//$NON-NLS-1$
         mapentry(TestElement.ENABLED,ATT_TE_ENABLED);
     }
 
     private static void saveClass(TestElement el, HierarchicalStreamWriter writer, String prop){
         String clazz=el.getPropertyAsString(prop);
         if (clazz.length()>0) {
             writer.addAttribute(propertyToAttribute.get(prop),SaveService.classToAlias(clazz));
         }
     }
 
     private static void restoreClass(TestElement el, HierarchicalStreamReader reader, String prop) {
         String att=propertyToAttribute.get(prop);
         String alias=reader.getAttribute(att);
         if (alias!=null){
             alias=SaveService.aliasToClass(alias);
             if (TestElement.GUI_CLASS.equals(prop)) { // mainly for TestElementConverter
                alias = NameUpdater.getCurrentName(alias);
             }
             el.setProperty(prop,alias);
         }
     }
 
     private static void saveItem(TestElement el, HierarchicalStreamWriter writer, String prop,
             boolean encode){
         String item=el.getPropertyAsString(prop);
         if (item.length() > 0) {
             if (encode) {
                 item=ConversionHelp.encode(item);
             }
             writer.addAttribute(propertyToAttribute.get(prop),item);
         }
     }
 
     private static void restoreItem(TestElement el, HierarchicalStreamReader reader, String prop,
             boolean decode) {
         String att=propertyToAttribute.get(prop);
         String value=reader.getAttribute(att);
         if (value!=null){
             if (decode) {
                 value=ConversionHelp.decode(value);
             }
             el.setProperty(prop,value);
         }
     }
 
     /**
      * Check whether <code>name</code> specifies a <em>special</em> property
      * 
      * @param name
      *            the name of the property to be checked
      * @return <code>true</code> if <code>name</code> is the name of a special
      *         property
      */
     public static boolean isSpecialProperty(String name) {
        return propertyToAttribute.containsKey(name);
     }
 
     /**
      * Get the property name, updating it if necessary using {@link NameUpdater}.
      * @param reader where to read the name attribute
      * @param context the unmarshalling context
      * 
      * @return the property name, may be null if the property has been deleted.
      * @see #getUpgradePropertyName(String, UnmarshallingContext)
      */
     public static String getPropertyName(HierarchicalStreamReader reader, UnmarshallingContext context) {
         String name = ConversionHelp.decode(reader.getAttribute(ATT_NAME));
         return getUpgradePropertyName(name, context);
         
     }
 
     /**
      * Get the property value, updating it if necessary using {@link NameUpdater}.
      * 
      * Do not use for GUI_CLASS or TEST_CLASS.
      * 
      * @param reader where to read the value
      * @param context the unmarshalling context
      * @param name the name of the property
      * 
      * @return the property value, updated if necessary.
      * @see #getUpgradePropertyValue(String, String, UnmarshallingContext)
      */
     public static String getPropertyValue(HierarchicalStreamReader reader, UnmarshallingContext context, String name) {
         String value = ConversionHelp.decode(reader.getValue());
         return getUpgradePropertyValue(name, value, context);
         
     }
 
     /**
      * Update a property name using {@link NameUpdater}.
      * @param name the original property name
      * @param context the unmarshalling context
      * 
      * @return the property name, may be null if the property has been deleted.
      */
     public static String getUpgradePropertyName(String name, UnmarshallingContext context) {
         String testClass = (String) context.get(SaveService.TEST_CLASS_NAME);
         final String newName = NameUpdater.getCurrentName(name, testClass);
         // Delete any properties whose name converts to the empty string
         if (name.length() != 0 && newName.length()==0) {
             return null;
         }
         return newName;
     }
 
     /**
      * Update a property value using {@link NameUpdater#getCurrentName(String, String, String)}.
      * 
      * Do not use for GUI_CLASS or TEST_CLASS.
      * 
      * @param name the original property name
      * @param value the original property value
      * @param context the unmarshalling context
      * 
      * @return the property value, updated if necessary
      */
     public static String getUpgradePropertyValue(String name, String value, UnmarshallingContext context) {
         String testClass = (String) context.get(SaveService.TEST_CLASS_NAME);
         return NameUpdater.getCurrentName(value, name, testClass);
     }
 
 
     /**
      * Save the special properties:
      * <ul>
      * <li>TestElement.GUI_CLASS</li>
      * <li>TestElement.TEST_CLASS</li>
      * <li>TestElement.NAME</li>
      * <li>TestElement.ENABLED</li>
      * </ul>
      * 
      * @param testElement
      *            element for which the special properties should be saved
      * @param writer
      *            {@link HierarchicalStreamWriter} in which the special
      *            properties should be saved
      */
     public static void saveSpecialProperties(TestElement testElement, HierarchicalStreamWriter writer) {
         saveClass(testElement,writer,TestElement.GUI_CLASS);
         saveClass(testElement,writer,TestElement.TEST_CLASS);
         saveItem(testElement,writer,TestElement.NAME,true);
         saveItem(testElement,writer,TestElement.ENABLED,false);
     }
 
     /**
      * Restore the special properties:
      * <ul>
      * <li>TestElement.GUI_CLASS</li>
      * <li>TestElement.TEST_CLASS</li>
      * <li>TestElement.NAME</li>
      * <li>TestElement.ENABLED</li>
      * </ul>
      * 
      * @param testElement
      *            in which the special properties should be restored
      * @param reader
      *            {@link HierarchicalStreamReader} from which the special
      *            properties should be restored
      */
     public static void restoreSpecialProperties(TestElement testElement, HierarchicalStreamReader reader) {
         restoreClass(testElement,reader,TestElement.GUI_CLASS);
         restoreClass(testElement,reader,TestElement.TEST_CLASS);
         restoreItem(testElement,reader,TestElement.NAME,true);
         restoreItem(testElement,reader,TestElement.ENABLED,false);
     }
 }
diff --git a/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java b/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
index 2c0ab2602..44501ed33 100644
--- a/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
+++ b/src/core/org/apache/jmeter/save/converters/SampleResultConverter.java
@@ -1,483 +1,483 @@
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
 
 package org.apache.jmeter.save.converters;
 
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 import java.net.URL;
 
 import org.apache.jmeter.assertions.AssertionResult;
 import org.apache.jmeter.samplers.SampleEvent;
 import org.apache.jmeter.samplers.SampleResult;
 import org.apache.jmeter.samplers.SampleSaveConfiguration;
 import org.apache.jmeter.save.SaveService;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.Converter;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 import com.thoughtworks.xstream.mapper.Mapper;
 
 /**
  * XStream Converter for the SampleResult class
  */
 public class SampleResultConverter extends AbstractCollectionConverter {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(SampleResultConverter.class);
 
     private static final String JAVA_LANG_STRING = "java.lang.String"; //$NON-NLS-1$
     private static final String ATT_CLASS = "class"; //$NON-NLS-1$
 
     // Element tags. Must be unique. Keep sorted.
     protected static final String TAG_COOKIES           = "cookies";          //$NON-NLS-1$
     protected static final String TAG_METHOD            = "method";           //$NON-NLS-1$
     protected static final String TAG_QUERY_STRING      = "queryString";      //$NON-NLS-1$
     protected static final String TAG_REDIRECT_LOCATION = "redirectLocation"; //$NON-NLS-1$
     protected static final String TAG_REQUEST_HEADER    = "requestHeader";    //$NON-NLS-1$
 
     //NOT USED protected   static final String TAG_URL               = "requestUrl";       //$NON-NLS-1$
 
     protected static final String TAG_RESPONSE_DATA     = "responseData";     //$NON-NLS-1$
     protected static final String TAG_RESPONSE_HEADER   = "responseHeader";   //$NON-NLS-1$
     protected static final String TAG_SAMPLER_DATA      = "samplerData";      //$NON-NLS-1$
     protected static final String TAG_RESPONSE_FILE     = "responseFile";     //$NON-NLS-1$
 
     // samplerData attributes. Must be unique. Keep sorted by string value.
     // Ensure the Listener documentation is updated when new attributes are added
     private static final String ATT_BYTES             = "by"; //$NON-NLS-1$
     private static final String ATT_SENT_BYTES        = "sby"; //$NON-NLS-1$
     private static final String ATT_DATA_ENCODING     = "de"; //$NON-NLS-1$
     private static final String ATT_DATA_TYPE         = "dt"; //$NON-NLS-1$
     private static final String ATT_ERROR_COUNT       = "ec"; //$NON-NLS-1$
     private static final String ATT_HOSTNAME          = "hn"; //$NON-NLS-1$
     private static final String ATT_LABEL             = "lb"; //$NON-NLS-1$
     private static final String ATT_LATENCY           = "lt"; //$NON-NLS-1$
     private static final String ATT_CONNECT_TIME      = "ct"; //$NON-NLS-1$
 
     private static final String ATT_ALL_THRDS         = "na"; //$NON-NLS-1$
     private static final String ATT_GRP_THRDS         = "ng"; //$NON-NLS-1$
 
     // N.B. Originally the response code was saved with the code "rs"
     // but retrieved with the code "rc". Changed to always use "rc", but
     // allow for "rs" when restoring values.
     private static final String ATT_RESPONSE_CODE     = "rc"; //$NON-NLS-1$
     private static final String ATT_RESPONSE_MESSAGE  = "rm"; //$NON-NLS-1$
     private static final String ATT_RESPONSE_CODE_OLD = "rs"; //$NON-NLS-1$
 
     private static final String ATT_SUCCESS           = "s";  //$NON-NLS-1$
     private static final String ATT_SAMPLE_COUNT      = "sc"; //$NON-NLS-1$
     private static final String ATT_TIME              = "t";  //$NON-NLS-1$
     private static final String ATT_IDLETIME          = "it"; //$NON-NLS-1$
     private static final String ATT_THREADNAME        = "tn"; //$NON-NLS-1$
     private static final String ATT_TIME_STAMP        = "ts"; //$NON-NLS-1$
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
      * 
      * @return the version of this converter
      */
     public static String getVersion() {
         return "$Revision$"; //$NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass does not use types
         return SampleResult.class.equals(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
         SampleResult res = (SampleResult) obj;
         SampleSaveConfiguration save = res.getSaveConfig();
         setAttributes(writer, context, res, save);
         saveAssertions(writer, context, res, save);
         saveSubResults(writer, context, res, save);
         saveResponseHeaders(writer, context, res, save);
         saveRequestHeaders(writer, context, res, save);
         saveResponseData(writer, context, res, save);
         saveSamplerData(writer, context, res, save);
     }
 
     /**
      * Save the data of the sample result to a stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveSamplerData(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveSamplerData(res)) {
             writeString(writer, TAG_SAMPLER_DATA, res.getSamplerData());
         }
         if (save.saveUrl()) {
             final URL url = res.getURL();
             if (url != null) {
                 writeItem(url, context, writer);
             }
         }
     }
 
     /**
      * Save the response from the sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveResponseData(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveResponseData(res)) {
             writer.startNode(TAG_RESPONSE_DATA);
             writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
             try {
                 if (SampleResult.TEXT.equals(res.getDataType())){
                     writer.setValue(new String(res.getResponseData(), res.getDataEncodingWithDefault()));
                 } else {
                     writer.setValue("Non-TEXT response data, cannot record: (" + res.getDataType() + ")");                    
                 }
                 // Otherwise don't save anything - no point
             } catch (UnsupportedEncodingException e) {
                 writer.setValue("Unsupported encoding in response data, cannot record: " + e);
             }
             writer.endNode();
         }
         if (save.saveFileName()){
             writer.startNode(TAG_RESPONSE_FILE);
             writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
             writer.setValue(res.getResultFileName());
             writer.endNode();
         }
     }
 
     /**
      * Save request headers from the sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveRequestHeaders(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveRequestHeaders()) {
             writeString(writer, TAG_REQUEST_HEADER, res.getRequestHeaders());
         }
     }
 
     /**
      * Save response headers from sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveResponseHeaders(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveResponseHeaders()) {
             writeString(writer, TAG_RESPONSE_HEADER, res.getResponseHeaders());
         }
     }
 
     /**
      * Save sub results from sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveSubResults(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveSubresults()) {
             SampleResult[] subResults = res.getSubResults();
             for (SampleResult subResult : subResults) {
                 subResult.setSaveConfig(save);
                 writeItem(subResult, context, writer);
             }
         }
     }
 
     /**
      * Save assertion results from the sample result into the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void saveAssertions(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveAssertions()) {
             AssertionResult[] assertionResults = res.getAssertionResults();
             for (AssertionResult assertionResult : assertionResults) {
                 writeItem(assertionResult, context, writer);
             }
         }
     }
 
     /**
      * Save attributes of the sample result to the stream
      *
      * @param writer
      *            stream to save objects into
      * @param context
      *            context for xstream to allow nested objects
      * @param res
      *            sample to be saved
      * @param save
      *            configuration telling us what to save
      */
     protected void setAttributes(HierarchicalStreamWriter writer, MarshallingContext context, SampleResult res,
             SampleSaveConfiguration save) {
         if (save.saveTime()) {
             writer.addAttribute(ATT_TIME, Long.toString(res.getTime()));
         }
         if (save.saveIdleTime()) {
             writer.addAttribute(ATT_IDLETIME, Long.toString(res.getIdleTime()));
         }
         if (save.saveLatency()) {
             writer.addAttribute(ATT_LATENCY, Long.toString(res.getLatency()));
         }
         if (save.saveConnectTime()) {
             writer.addAttribute(ATT_CONNECT_TIME, Long.toString(res.getConnectTime()));
         }
         if (save.saveTimestamp()) {
             writer.addAttribute(ATT_TIME_STAMP, Long.toString(res.getTimeStamp()));
         }
         if (save.saveSuccess()) {
             writer.addAttribute(ATT_SUCCESS, Boolean.toString(res.isSuccessful()));
         }
         if (save.saveLabel()) {
             writer.addAttribute(ATT_LABEL, ConversionHelp.encode(res.getSampleLabel()));
         }
         if (save.saveCode()) {
             writer.addAttribute(ATT_RESPONSE_CODE, ConversionHelp.encode(res.getResponseCode()));
         }
         if (save.saveMessage()) {
             writer.addAttribute(ATT_RESPONSE_MESSAGE, ConversionHelp.encode(res.getResponseMessage()));
         }
         if (save.saveThreadName()) {
             writer.addAttribute(ATT_THREADNAME, ConversionHelp.encode(res.getThreadName()));
         }
         if (save.saveDataType()) {
             writer.addAttribute(ATT_DATA_TYPE, ConversionHelp.encode(res.getDataType()));
         }
         if (save.saveEncoding()) {
             writer.addAttribute(ATT_DATA_ENCODING, ConversionHelp.encode(res.getDataEncodingNoDefault()));
         }
         if (save.saveBytes()) {
             writer.addAttribute(ATT_BYTES, String.valueOf(res.getBytesAsLong()));
         }
         if (save.saveSentBytes()) {
             writer.addAttribute(ATT_SENT_BYTES, String.valueOf(res.getSentBytes()));
         }
         if (save.saveSampleCount()){
             writer.addAttribute(ATT_SAMPLE_COUNT, String.valueOf(res.getSampleCount()));
             writer.addAttribute(ATT_ERROR_COUNT, String.valueOf(res.getErrorCount()));
         }
         if (save.saveThreadCounts()){
            writer.addAttribute(ATT_GRP_THRDS, String.valueOf(res.getGroupThreads()));
            writer.addAttribute(ATT_ALL_THRDS, String.valueOf(res.getAllThreads()));
         }
         SampleEvent event = (SampleEvent) context.get(SaveService.SAMPLE_EVENT_OBJECT);
         if (event != null) {
             if (save.saveHostname()){
                 writer.addAttribute(ATT_HOSTNAME, event.getHostname());
             }
             for (int i = 0; i < SampleEvent.getVarCount(); i++){
                writer.addAttribute(SampleEvent.getVarName(i), ConversionHelp.encode(event.getVarValue(i)));
             }
         }
     }
 
     /**
      * Write a tag with a content of <code>value</code> to the
      * <code>writer</code>
      * 
      * @param writer
      *            writer to write the tag into
      * @param tag
      *            name of the tag to use
      * @param value
      *            content for tag
      */
     protected void writeString(HierarchicalStreamWriter writer, String tag, String value) {
         if (value != null) {
             writer.startNode(tag);
             writer.addAttribute(ATT_CLASS, JAVA_LANG_STRING);
             writer.setValue(value);
             writer.endNode();
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         SampleResult res = (SampleResult) createCollection(context.getRequiredType());
         retrieveAttributes(reader, context, res);
         while (reader.hasMoreChildren()) {
             reader.moveDown();
             Object subItem = readItem(reader, context, res);
             retrieveItem(reader, context, res, subItem);
             reader.moveUp();
         }
 
         // If we have a file, but no data, then read the file
         String resultFileName = res.getResultFileName();
         if (resultFileName.length()>0
         &&  res.getResponseData().length == 0) {
             readFile(resultFileName,res);
         }
         return res;
     }
 
     /**
      *
      * @param reader stream from which the objects should be read
      * @param context context for xstream to allow nested objects
      * @param res sample result into which the information should be retrieved
      * @param subItem sub item which should be added into <code>res</code>
      * @return <code>true</code> if the item was processed (for HTTPResultConverter)
      */
     protected boolean retrieveItem(HierarchicalStreamReader reader, UnmarshallingContext context, SampleResult res,
             Object subItem) {
         String nodeName = reader.getNodeName();
         if (subItem instanceof AssertionResult) {
             res.addAssertionResult((AssertionResult) subItem);
         } else if (subItem instanceof SampleResult) {
             res.storeSubResult((SampleResult) subItem);
         } else if (nodeName.equals(TAG_RESPONSE_HEADER)) {
             res.setResponseHeaders((String) subItem);
         } else if (nodeName.equals(TAG_REQUEST_HEADER)) {
             res.setRequestHeaders((String) subItem);
         } else if (nodeName.equals(TAG_RESPONSE_DATA)) {
             final String responseData = (String) subItem;
             if (responseData.length() > 0) {
                 final String dataEncoding = res.getDataEncodingWithDefault();
                 try {
                     res.setResponseData(responseData.getBytes(dataEncoding));
                 } catch (UnsupportedEncodingException e) {
                     res.setResponseData("Can't support the char set: " + dataEncoding, null);
                     res.setDataType(SampleResult.TEXT);
                 }
             }
         } else if (nodeName.equals(TAG_SAMPLER_DATA)) {
             res.setSamplerData((String) subItem);
         } else if (nodeName.equals(TAG_RESPONSE_FILE)) {
             res.setResultFileName((String) subItem);
         // Don't try restoring the URL TODO: why not?
         } else {
             return false;
         }
         return true;
     }
 
     /**
      * @param reader stream to read objects from
      * @param context context for xstream to allow nested objects
      * @param res sample result on which the attributes should be set
      */
     protected void retrieveAttributes(HierarchicalStreamReader reader, UnmarshallingContext context, SampleResult res) {
         res.setSampleLabel(ConversionHelp.decode(reader.getAttribute(ATT_LABEL)));
         res.setDataEncoding(ConversionHelp.decode(reader.getAttribute(ATT_DATA_ENCODING)));
         res.setDataType(ConversionHelp.decode(reader.getAttribute(ATT_DATA_TYPE)));
         String oldrc=reader.getAttribute(ATT_RESPONSE_CODE_OLD);
         if (oldrc!=null) {
             res.setResponseCode(ConversionHelp.decode(oldrc));
         } else {
             res.setResponseCode(ConversionHelp.decode(reader.getAttribute(ATT_RESPONSE_CODE)));
         }
         res.setResponseMessage(ConversionHelp.decode(reader.getAttribute(ATT_RESPONSE_MESSAGE)));
         res.setSuccessful(Converter.getBoolean(reader.getAttribute(ATT_SUCCESS), true));
         res.setThreadName(ConversionHelp.decode(reader.getAttribute(ATT_THREADNAME)));
         res.setStampAndTime(Converter.getLong(reader.getAttribute(ATT_TIME_STAMP)),
                 Converter.getLong(reader.getAttribute(ATT_TIME)));
         res.setIdleTime(Converter.getLong(reader.getAttribute(ATT_IDLETIME)));
         res.setLatency(Converter.getLong(reader.getAttribute(ATT_LATENCY)));
         res.setConnectTime(Converter.getLong(reader.getAttribute(ATT_CONNECT_TIME)));
         res.setBytes(Converter.getLong(reader.getAttribute(ATT_BYTES)));
         res.setSentBytes(Converter.getLong(reader.getAttribute(ATT_SENT_BYTES)));
         res.setSampleCount(Converter.getInt(reader.getAttribute(ATT_SAMPLE_COUNT),1)); // default is 1
         res.setErrorCount(Converter.getInt(reader.getAttribute(ATT_ERROR_COUNT),0)); // default is 0
         res.setGroupThreads(Converter.getInt(reader.getAttribute(ATT_GRP_THRDS)));
         res.setAllThreads(Converter.getInt(reader.getAttribute(ATT_ALL_THRDS)));
     }
 
     protected void readFile(String resultFileName, SampleResult res) {
         File in = new File(resultFileName);
         try (FileInputStream fis = new FileInputStream(in);
                 BufferedInputStream bis = new BufferedInputStream(fis)){
             ByteArrayOutputStream outstream = new ByteArrayOutputStream(4096);
             byte[] buffer = new byte[4096];
             int len;
             while ((len = bis.read(buffer)) > 0) {
                 outstream.write(buffer, 0, len);
             }
             outstream.close();
             res.setResponseData(outstream.toByteArray());
         } catch (IOException e) {
-            log.warn(e.getLocalizedMessage());
+            log.warn("Failed to read result file.", e);
         } 
     }
 
     /**
      * @param arg0 the mapper
      */
     public SampleResultConverter(Mapper arg0) {
         super(arg0);
     }
 }
diff --git a/src/core/org/apache/jmeter/save/converters/TestElementConverter.java b/src/core/org/apache/jmeter/save/converters/TestElementConverter.java
index a5b97e0a3..b091a059a 100644
--- a/src/core/org/apache/jmeter/save/converters/TestElementConverter.java
+++ b/src/core/org/apache/jmeter/save/converters/TestElementConverter.java
@@ -1,126 +1,126 @@
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
 
 package org.apache.jmeter.save.converters;
 
 import org.apache.jmeter.save.SaveService;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.util.NameUpdater;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 import com.thoughtworks.xstream.mapper.Mapper;
 
 public class TestElementConverter extends AbstractCollectionConverter {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TestElementConverter.class);
 
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
      * 
      * @return the version of this converter
      */
     public static String getVersion() {
         return "$Revision$"; //$NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass does not use types
         return TestElement.class.isAssignableFrom(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext context) {
         TestElement el = (TestElement) arg0;
         ConversionHelp.saveSpecialProperties(el,writer);
         PropertyIterator iter = el.propertyIterator();
         while (iter.hasNext()) {
             JMeterProperty jmp=iter.next();
             // Skip special properties if required
             if (!ConversionHelp.isSpecialProperty(jmp.getName())) {
                 // Don't save empty comments - except for the TestPlan (to maintain compatibility)
                    if (!(
                            TestElement.COMMENTS.equals(jmp.getName())
                            && jmp.getStringValue().length()==0
                            && !el.getClass().equals(TestPlan.class)
                        ))
                    {
                     writeItem(jmp, context, writer);
                    }
             }
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         String classAttribute = reader.getAttribute(ConversionHelp.ATT_CLASS);
         Class<?> type;
         if (classAttribute == null) {
             type = mapper().realClass(reader.getNodeName());
         } else {
             type = mapper().realClass(classAttribute);
         }
         // Update the test class name if necessary (Bug 52466)
         String inputName = type.getName();
         String targetName = inputName;
         String guiClassName = SaveService.aliasToClass(reader.getAttribute(ConversionHelp.ATT_TE_GUICLASS));
         targetName = NameUpdater.getCurrentTestName(inputName, guiClassName);
         if (!targetName.equals(inputName)) { // remap the class name
             type = mapper().realClass(targetName);
         }
         context.put(SaveService.TEST_CLASS_NAME, targetName); // needed by property converters  (Bug 52466)
         try {
             TestElement el = (TestElement) type.newInstance();
             // No need to check version, just process the attributes if present
             ConversionHelp.restoreSpecialProperties(el, reader);
             // Slight hack - we need to ensure the TestClass is not reset by the previous call
             el.setProperty(TestElement.TEST_CLASS, targetName);
             while (reader.hasMoreChildren()) {
                 reader.moveDown();
                 JMeterProperty prop = (JMeterProperty) readItem(reader, context, el);
                 if (prop != null) { // could be null if it has been deleted via NameUpdater
                     el.setProperty(prop);
                 }
                 reader.moveUp();
             }
             return el;
         } catch (InstantiationException | IllegalAccessException e) {
-            log.error("TestElement not instantiable: " + type, e);
+            log.error("TestElement not instantiable: {}", type, e);
             return null;
         }
     }
 
     /**
      * @param arg0 the mapper
      */
     public TestElementConverter(Mapper arg0) {
         super(arg0);
     }
 }
diff --git a/src/core/org/apache/jmeter/save/converters/TestElementPropertyConverter.java b/src/core/org/apache/jmeter/save/converters/TestElementPropertyConverter.java
index e7b8ea83f..4f47b4406 100644
--- a/src/core/org/apache/jmeter/save/converters/TestElementPropertyConverter.java
+++ b/src/core/org/apache/jmeter/save/converters/TestElementPropertyConverter.java
@@ -1,137 +1,137 @@
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
 
 package org.apache.jmeter.save.converters;
 
 import org.apache.jmeter.config.ConfigTestElement;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 import com.thoughtworks.xstream.mapper.Mapper;
 
 public class TestElementPropertyConverter extends AbstractCollectionConverter {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TestElementPropertyConverter.class);
 
     private static final String HEADER_CLASSNAME
         = "org.apache.jmeter.protocol.http.control.Header"; // $NON-NLS-1$
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
      * 
      * @return the version of this converter
      */
     public static String getVersion() {
         return "$Revision$"; // $NON-NLS-1$
     }
 
     /** {@inheritDoc} */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass does not use types
         return TestElementProperty.class.equals(arg0);
     }
 
     /** {@inheritDoc} */
     @Override
     public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext context) {
         TestElementProperty prop = (TestElementProperty) arg0;
         writer.addAttribute(ConversionHelp.ATT_NAME, ConversionHelp.encode(prop.getName()));
         Class<?> clazz = prop.getObjectValue().getClass();
         writer.addAttribute(ConversionHelp.ATT_ELEMENT_TYPE,
                 mapper().serializedClass(clazz));
         TestElement te = (TestElement)prop.getObjectValue();
         ConversionHelp.saveSpecialProperties(te,writer);
         for (JMeterProperty jmp : prop) {
             // Skip special properties if required
             if (!ConversionHelp.isSpecialProperty(jmp.getName()))
             {
                 // Don't save empty comments
                 if (!(TestElement.COMMENTS.equals(jmp.getName())
                         && jmp.getStringValue().isEmpty()))
                 {
                     writeItem(jmp, context, writer);
                 }
             }
         }
         //TODO clazz is probably always the same as testclass
     }
 
     /*
      * TODO - convert to work more like upgrade.properties/NameUpdater.java
      *
      * Special processing is carried out for the Header Class The String
      * property TestElement.name is converted to Header.name for example:
      * <elementProp name="User-Agent"
      * elementType="org.apache.jmeter.protocol.http.control.Header"> <stringProp
      * name="Header.value">Mozilla%2F4.0+%28compatible%3B+MSIE+5.5%3B+Windows+98%29</stringProp>
      * <stringProp name="TestElement.name">User-Agent</stringProp>
      * </elementProp> becomes <elementProp name="User-Agent"
      * elementType="org.apache.jmeter.protocol.http.control.Header"> <stringProp
      * name="Header.value">Mozilla%2F4.0+%28compatible%3B+MSIE+5.5%3B+Windows+98%29</stringProp>
      * <stringProp name="Header.name">User-Agent</stringProp> </elementProp>
      */
     /** {@inheritDoc} */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         try {
             TestElementProperty prop = (TestElementProperty) createCollection(context.getRequiredType());
             prop.setName(ConversionHelp.decode(reader.getAttribute(ConversionHelp.ATT_NAME)));
             String element = reader.getAttribute(ConversionHelp.ATT_ELEMENT_TYPE);
             boolean isHeader = HEADER_CLASSNAME.equals(element);
             prop.setObjectValue(mapper().realClass(element).newInstance());// Always decode
             TestElement te = (TestElement)prop.getObjectValue();
             // No need to check version, just process the attributes if present
             ConversionHelp.restoreSpecialProperties(te, reader);
             while (reader.hasMoreChildren()) {
                 reader.moveDown();
                 JMeterProperty subProp = (JMeterProperty) readItem(reader, context, prop);
                 if (subProp != null) { // could be null if it has been deleted via NameUpdater
                     if (isHeader) {
                         String name = subProp.getName();
                         if (TestElement.NAME.equals(name)) {
                             subProp.setName("Header.name");// $NON-NLS-1$
                             // Must be same as Header.HNAME - but that is built
                             // later
                         }
                     }
                     prop.addProperty(subProp);
                 }
                 reader.moveUp();
             }
             return prop;
         } catch (InstantiationException | IllegalAccessException e) {
             log.error("Couldn't unmarshall TestElementProperty", e);
             return new TestElementProperty("ERROR", new ConfigTestElement());// $NON-NLS-1$
         }
     }
 
     /**
      * @param arg0 the mapper
      */
     public TestElementPropertyConverter(Mapper arg0) {
         super(arg0);
     }
 }
diff --git a/src/core/org/apache/jmeter/services/FileServer.java b/src/core/org/apache/jmeter/services/FileServer.java
index 2e3107c2b..57d81faa0 100644
--- a/src/core/org/apache/jmeter/services/FileServer.java
+++ b/src/core/org/apache/jmeter/services/FileServer.java
@@ -1,593 +1,593 @@
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
  * Created on Oct 19, 2004
  */
 package org.apache.jmeter.services;
 
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.Closeable;
 import java.io.EOFException;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.io.Reader;
 import java.io.Writer;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ThreadLocalRandom;
 
 import org.apache.commons.collections.ArrayStack;
 import org.apache.jmeter.gui.JMeterFileFilter;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  *
  * The point of this class is to provide thread-safe access to files, and to
  * provide some simplifying assumptions about where to find files and how to
  * name them. For instance, putting supporting files in the same directory as
  * the saved test plan file allows users to refer to the file with just it's
  * name - this FileServer class will find the file without a problem.
  * Eventually, I want all in-test file access to be done through here, with the
  * goal of packaging up entire test plans as a directory structure that can be
  * sent via rmi to remote servers (currently, one must make sure the remote
  * server has all support files in a relative-same location) and to package up
  * test plans to execute on unknown boxes that only have Java installed.
  */
 public class FileServer {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(FileServer.class);
 
     /**
      * The default base used for resolving relative files, i.e.<br/>
      * {@code System.getProperty("user.dir")}
      */
     private static final String DEFAULT_BASE = System.getProperty("user.dir");// $NON-NLS-1$
 
     /** Default base prefix: {@value} */
     private static final String BASE_PREFIX_DEFAULT = "~/"; // $NON-NLS-1$
 
     private static final String BASE_PREFIX = 
         JMeterUtils.getPropDefault("jmeter.save.saveservice.base_prefix", // $NON-NLS-1$
                 BASE_PREFIX_DEFAULT);
 
     //@GuardedBy("this")
     private File base;
 
     //@GuardedBy("this") NOTE this also guards against possible window in checkForOpenFiles()
     private final Map<String, FileEntry> files = new HashMap<>();
 
     private static final FileServer server = new FileServer();
 
     // volatile needed to ensure safe publication
     private volatile String scriptName;
 
     // Cannot be instantiated
     private FileServer() {
         base = new File(DEFAULT_BASE);
-        log.info("Default base='"+DEFAULT_BASE+"'");
+        log.info("Default base='{}'", DEFAULT_BASE);
     }
 
     /**
      * @return the singleton instance of the server.
      */
     public static FileServer getFileServer() {
         return server;
     }
 
     /**
      * Resets the current base to {@link #DEFAULT_BASE}.
      */
     public synchronized void resetBase() {
         checkForOpenFiles();
         base = new File(DEFAULT_BASE);
-        log.info("Reset base to'"+base+"'");
+        log.info("Reset base to '{}'", base);
     }
 
     /**
      * Sets the current base directory for relative file names from the provided path.
      * If the path does not refer to an existing directory, then its parent is used.
      * Normally the provided path is a file, so using the parent directory is appropriate.
      * 
      * @param basedir the path to set, or {@code null} if the GUI is being cleared
      * @throws IllegalStateException if files are still open
      */
     public synchronized void setBasedir(String basedir) {
         checkForOpenFiles(); // TODO should this be called if basedir == null?
         if (basedir != null) {
             File newBase = new File(basedir);
             if (!newBase.isDirectory()) {
                 newBase = newBase.getParentFile();
             }
             base = newBase;
-            log.info("Set new base='"+base+"'");
+            log.info("Set new base='{}'", base);
         }
     }
 
     /**
      * Sets the current base directory for relative file names from the provided script file.
      * The parameter is assumed to be the path to a JMX file, so the base directory is derived
      * from its parent.
      * 
      * @param scriptPath the path of the script file; must be not be {@code null}
      * @throws IllegalStateException if files are still open
      * @throws IllegalArgumentException if scriptPath parameter is null
      */
     public synchronized void setBaseForScript(File scriptPath) {
         if (scriptPath == null){
             throw new IllegalArgumentException("scriptPath must not be null");
         }
         setScriptName(scriptPath.getName());
         // getParentFile() may not work on relative paths
         setBase(scriptPath.getAbsoluteFile().getParentFile());
     }
 
     /**
      * Sets the current base directory for relative file names.
      * 
      * @param jmxBase the path of the script file base directory, cannot be null
      * @throws IllegalStateException if files are still open
      * @throws IllegalArgumentException if {@code basepath} is null
      */
     public synchronized void setBase(File jmxBase) {
         if (jmxBase == null) {
             throw new IllegalArgumentException("jmxBase must not be null");
         }
         checkForOpenFiles();
         base = jmxBase;
-        log.info("Set new base='"+base+"'");
+        log.info("Set new base='{}'", base);
     }
 
     /**
      * Check if there are entries in use.
      * <p>
      * Caller must ensure that access to the files map is single-threaded as
      * there is a window between checking the files Map and clearing it.
      * 
      * @throws IllegalStateException if there are any entries still in use
      */
     private void checkForOpenFiles() throws IllegalStateException {
         if (filesOpen()) { // checks for entries in use
             throw new IllegalStateException("Files are still open, cannot change base directory");
         }
         files.clear(); // tidy up any unused entries
     }
 
     public synchronized String getBaseDir() {
         return base.getAbsolutePath();
     }
 
     public static String getDefaultBase(){
         return DEFAULT_BASE;
     }
 
     /**
      * Calculates the relative path from {@link #DEFAULT_BASE} to the current base,
      * which must be the same as or a child of the default.
      * 
      * @return the relative path, or {@code "."} if the path cannot be determined
      */
     public synchronized File getBaseDirRelative() {
         // Must first convert to absolute path names to ensure parents are available
         File parent = new File(DEFAULT_BASE).getAbsoluteFile();
         File f = base.getAbsoluteFile();
         ArrayStack l = new ArrayStack();
         while (f != null) { 
             if (f.equals(parent)){
                 if (l.isEmpty()){
                     break;
                 }
                 File rel = new File((String) l.pop());
                 while(!l.isEmpty()) {
                     rel = new File(rel, (String) l.pop());
                 }
                 return rel;
             }
             l.push(f.getName());
             f = f.getParentFile(); 
         }
         return new File(".");
     }
 
     /**
      * Creates an association between a filename and a File inputOutputObject,
      * and stores it for later use - unless it is already stored.
      *
      * @param filename - relative (to base) or absolute file name (must not be null)
      */
     public void reserveFile(String filename) {
         reserveFile(filename,null);
     }
 
     /**
      * Creates an association between a filename and a File inputOutputObject,
      * and stores it for later use - unless it is already stored.
      *
      * @param filename - relative (to base) or absolute file name (must not be null)
      * @param charsetName - the character set encoding to use for the file (may be null)
      */
     public void reserveFile(String filename, String charsetName) {
         reserveFile(filename, charsetName, filename, false);
     }
 
     /**
      * Creates an association between a filename and a File inputOutputObject,
      * and stores it for later use - unless it is already stored.
      *
      * @param filename - relative (to base) or absolute file name (must not be null)
      * @param charsetName - the character set encoding to use for the file (may be null)
      * @param alias - the name to be used to access the object (must not be null)
      */
     public void reserveFile(String filename, String charsetName, String alias) {
         reserveFile(filename, charsetName, alias, false);
     }
 
     /**
      * Creates an association between a filename and a File inputOutputObject,
      * and stores it for later use - unless it is already stored.
      *
      * @param filename - relative (to base) or absolute file name (must not be null or empty)
      * @param charsetName - the character set encoding to use for the file (may be null)
      * @param alias - the name to be used to access the object (must not be null)
      * @param hasHeader true if the file has a header line describing the contents
      * @return the header line; may be null
      * @throws IllegalArgumentException if header could not be read or filename is null or empty
      */
     public synchronized String reserveFile(String filename, String charsetName, String alias, boolean hasHeader) {
         if (filename == null || filename.isEmpty()){
             throw new IllegalArgumentException("Filename must not be null or empty");
         }
         if (alias == null){
             throw new IllegalArgumentException("Alias must not be null");
         }
         FileEntry fileEntry = files.get(alias);
         if (fileEntry == null) {
             fileEntry = new FileEntry(resolveFileFromPath(filename), null, charsetName);
             if (filename.equals(alias)){
-                log.info("Stored: "+filename);
+                log.info("Stored: {}", filename);
             } else {
-                log.info("Stored: "+filename+" Alias: "+alias);
+                log.info("Stored: {} Alias: {}", filename, alias);
             }
             files.put(alias, fileEntry);
             if (hasHeader) {
                 try {
                     fileEntry.headerLine = readLine(alias, false);
                     if (fileEntry.headerLine == null) {
                         fileEntry.exception = new EOFException("File is empty: " + fileEntry.file);
                     }
                 } catch (IOException | IllegalArgumentException e) {
                     fileEntry.exception = e;
                 }
             }
         }
         if (hasHeader && fileEntry.headerLine == null) {
             throw new IllegalArgumentException("Could not read file header line for file " + filename,
                     fileEntry.exception);
         }
         return fileEntry.headerLine;
     }
 
     /**
      * Resolves file name into {@link File} instance.
      * When filename is not absolute and not found from current workind dir,
      * it tries to find it under current base directory
      * @param filename original file name
      * @return {@link File} instance
      */
     private File resolveFileFromPath(String filename) {
         File f = new File(filename);
         if (f.isAbsolute() || f.exists()) {
             return f;
         } else {
             return new File(base, filename);
         }
     }
 
     /**
      * Get the next line of the named file, recycle by default.
      *
      * @param filename the filename or alias that was used to reserve the file
      * @return String containing the next line in the file
      * @throws IOException when reading of the file fails, or the file was not reserved properly
      */
     public String readLine(String filename) throws IOException {
       return readLine(filename, true);
     }
 
     /**
      * Get the next line of the named file, first line is name to false
      *
      * @param filename the filename or alias that was used to reserve the file
      * @param recycle - should file be restarted at EOF?
      * @return String containing the next line in the file (null if EOF reached and not recycle)
      * @throws IOException when reading of the file fails, or the file was not reserved properly
      */
     public String readLine(String filename, boolean recycle) throws IOException {
         return readLine(filename, recycle, false);
     }
    /**
      * Get the next line of the named file
      *
      * @param filename the filename or alias that was used to reserve the file
      * @param recycle - should file be restarted at EOF?
      * @param ignoreFirstLine - Ignore first line
      * @return String containing the next line in the file (null if EOF reached and not recycle)
      * @throws IOException when reading of the file fails, or the file was not reserved properly
      */
     public synchronized String readLine(String filename, boolean recycle, 
             boolean ignoreFirstLine) throws IOException {
         FileEntry fileEntry = files.get(filename);
         if (fileEntry != null) {
             if (fileEntry.inputOutputObject == null) {
                 fileEntry.inputOutputObject = createBufferedReader(fileEntry);
             } else if (!(fileEntry.inputOutputObject instanceof Reader)) {
                 throw new IOException("File " + filename + " already in use");
             }
             BufferedReader reader = (BufferedReader) fileEntry.inputOutputObject;
             String line = reader.readLine();
             if (line == null && recycle) {
                 reader.close();
                 reader = createBufferedReader(fileEntry);
                 fileEntry.inputOutputObject = reader;
                 if (ignoreFirstLine) {
                     // read first line and forget
                     reader.readLine();//NOSONAR
                 }
                 line = reader.readLine();
             }
-            if (log.isDebugEnabled()) { log.debug("Read:"+line); }
+            log.debug("Read:{}", line);
             return line;
         }
         throw new IOException("File never reserved: "+filename);
     }
 
     /**
      * 
      * @param alias the file name or alias
      * @param recycle whether the file should be re-started on EOF
      * @param ignoreFirstLine whether the file contains a file header which will be ignored
      * @param delim the delimiter to use for parsing
      * @return the parsed line, will be empty if the file is at EOF
      * @throws IOException when reading of the aliased file fails, or the file was not reserved properly
      */
     public synchronized String[] getParsedLine(String alias, boolean recycle, boolean ignoreFirstLine, char delim) throws IOException {
         BufferedReader reader = getReader(alias, recycle, ignoreFirstLine);
         return CSVSaveService.csvReadFile(reader, delim);
     }
 
     /**
      * Return BufferedReader handling close if EOF reached and recycle is true 
      * and ignoring first line if ignoreFirstLine is true
      * @param alias String alias
      * @param recycle Recycle at eof
      * @param ignoreFirstLine Ignore first line
      * @return {@link BufferedReader}
      * @throws IOException
      */
     private BufferedReader getReader(String alias, boolean recycle, boolean ignoreFirstLine) throws IOException {
         FileEntry fileEntry = files.get(alias);
         if (fileEntry != null) {
             BufferedReader reader;
             if (fileEntry.inputOutputObject == null) {
                 reader = createBufferedReader(fileEntry);
                 fileEntry.inputOutputObject = reader;
                 if (ignoreFirstLine) {
                     // read first line and forget
                     reader.readLine(); //NOSONAR
                 }                
             } else if (!(fileEntry.inputOutputObject instanceof Reader)) {
                 throw new IOException("File " + alias + " already in use");
             } else {
                 reader = (BufferedReader) fileEntry.inputOutputObject;
                 if (recycle) { // need to check if we are at EOF already
                     reader.mark(1);
                     int peek = reader.read();
                     if (peek == -1) { // already at EOF
                         reader.close();
                         reader = createBufferedReader(fileEntry);
                         fileEntry.inputOutputObject = reader;
                         if (ignoreFirstLine) {
                             // read first line and forget
                             reader.readLine(); //NOSONAR
                         }                
                     } else { // OK, we still have some data, restore it
                         reader.reset();
                     }
                 }
             }
             return reader;
         } else {
             throw new IOException("File never reserved: "+alias);
         }
     }
 
     private BufferedReader createBufferedReader(FileEntry fileEntry) throws IOException {
         if (!fileEntry.file.canRead() || !fileEntry.file.isFile()) {
             throw new IllegalArgumentException("File "+ fileEntry.file.getName()+ " must exist and be readable");
         }
         FileInputStream fis = new FileInputStream(fileEntry.file);
         InputStreamReader isr = null;
         // If file encoding is specified, read using that encoding, otherwise use default platform encoding
         String charsetName = fileEntry.charSetEncoding;
         if(!JOrphanUtils.isBlank(charsetName)) {
             isr = new InputStreamReader(fis, charsetName);
         } else {
             isr = new InputStreamReader(fis);
         }
         return new BufferedReader(isr);
     }
 
     public synchronized void write(String filename, String value) throws IOException {
         FileEntry fileEntry = files.get(filename);
         if (fileEntry != null) {
             if (fileEntry.inputOutputObject == null) {
                 fileEntry.inputOutputObject = createBufferedWriter(fileEntry);
             } else if (!(fileEntry.inputOutputObject instanceof Writer)) {
                 throw new IOException("File " + filename + " already in use");
             }
             BufferedWriter writer = (BufferedWriter) fileEntry.inputOutputObject;
-            if (log.isDebugEnabled()) { log.debug("Write:"+value); }
+            log.debug("Write:{}", value);
             writer.write(value);
         } else {
             throw new IOException("File never reserved: "+filename);
         }
     }
 
     private BufferedWriter createBufferedWriter(FileEntry fileEntry) throws IOException {
         FileOutputStream fos = new FileOutputStream(fileEntry.file);
         OutputStreamWriter osw = null;
         // If file encoding is specified, write using that encoding, otherwise use default platform encoding
         String charsetName = fileEntry.charSetEncoding;
         if(!JOrphanUtils.isBlank(charsetName)) {
             osw = new OutputStreamWriter(fos, charsetName);
         } else {
             osw = new OutputStreamWriter(fos);
         }
         return new BufferedWriter(osw);
     }
 
     public synchronized void closeFiles() throws IOException {
         for (Map.Entry<String, FileEntry> me : files.entrySet()) {
             closeFile(me.getKey(),me.getValue() );
         }
         files.clear();
     }
 
     /**
      * @param name the name or alias of the file to be closed
      * @throws IOException when closing of the aliased file fails
      */
     public synchronized void closeFile(String name) throws IOException {
         FileEntry fileEntry = files.get(name);
         closeFile(name, fileEntry);
     }
 
     private void closeFile(String name, FileEntry fileEntry) throws IOException {
         if (fileEntry != null && fileEntry.inputOutputObject != null) {
-            log.info("Close: "+name);
+            log.info("Close: {}", name);
             fileEntry.inputOutputObject.close();
             fileEntry.inputOutputObject = null;
         }
     }
 
     boolean filesOpen() { // package access for test code only
         for (FileEntry fileEntry : files.values()) {
             if (fileEntry.inputOutputObject != null) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Method will get a random file in a base directory 
      * <p>
      * TODO hey, not sure this
      * method belongs here. FileServer is for threadsafe File access relative to
      * current test's base directory.
      *
      * @param basedir
      *            name of the directory in which the files can be found
      * @param extensions
      *            array of allowed extensions, if <code>null</code> is given,
      *            any file be allowed
      * @return a random File from the <code>basedir</code> that matches one of
      *         the extensions
      */
     public File getRandomFile(String basedir, String[] extensions) {
         File input = null;
         if (basedir != null) {
             File src = new File(basedir);
             File[] lfiles = src.listFiles(new JMeterFileFilter(extensions));
             if (lfiles != null) {
                 // lfiles cannot be null as it has been checked before
                 int count = lfiles.length;
                 input = lfiles[ThreadLocalRandom.current().nextInt(count)];
             }
         }
         return input;
     }
 
     /**
      * Get {@link File} instance for provided file path,
      * resolve file location relative to base dir or script dir when needed
      * @param path original path to file, maybe relative
      * @return {@link File} instance 
      */
     public File getResolvedFile(String path) {
         reserveFile(path);
         return files.get(path).file;
     }
 
     private static class FileEntry{
         private String headerLine;
         private Throwable exception;
         private final File file;
         private Closeable inputOutputObject; 
         private final String charSetEncoding;
         FileEntry(File f, Closeable o, String e){
             file=f;
             inputOutputObject=o;
             charSetEncoding=e;
         }
     }
     
     /**
      * Resolve a file name that may be relative to the base directory. If the
      * name begins with the value of the JMeter property
      * "jmeter.save.saveservice.base_prefix" - default "~/" - then the name is
      * assumed to be relative to the basename.
      * 
      * @param relativeName
      *            filename that should be checked for
      *            <code>jmeter.save.saveservice.base_prefix</code>
      * @return the updated filename
      */
     public static String resolveBaseRelativeName(String relativeName) {
         if (relativeName.startsWith(BASE_PREFIX)){
             String newName = relativeName.substring(BASE_PREFIX.length());
             return new File(getFileServer().getBaseDir(),newName).getAbsolutePath();
         }
         return relativeName;
     }
 
     /**
      * @return JMX Script name
      * @since 2.6
      */
     public String getScriptName() {
         return scriptName;
     }
 
     /**
      * @param scriptName Script name
      * @since 2.6
      */
     public void setScriptName(String scriptName) {
         this.scriptName = scriptName;
     }
 }
diff --git a/src/core/org/apache/jmeter/swing/HtmlPane.java b/src/core/org/apache/jmeter/swing/HtmlPane.java
index 303c56720..40ce65414 100644
--- a/src/core/org/apache/jmeter/swing/HtmlPane.java
+++ b/src/core/org/apache/jmeter/swing/HtmlPane.java
@@ -1,56 +1,56 @@
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
 
 package org.apache.jmeter.swing;
 
 import java.awt.Rectangle;
 
 import javax.swing.JTextPane;
 import javax.swing.event.HyperlinkEvent;
 import javax.swing.event.HyperlinkListener;
 
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Implements an HTML Pane with local hyperlinking enabled.
  */
 public class HtmlPane extends JTextPane {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(HtmlPane.class);
 
     public HtmlPane() {
         this.addHyperlinkListener(new HyperlinkListener() {
             @Override
             public void hyperlinkUpdate(HyperlinkEvent e) {
                 if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                     String ref = e.getURL().getRef();
                     if (ref != null) {
-                        log.debug("reference to scroll to = '" + ref + "'");
+                        log.debug("reference to scroll to = '{}'", ref);
                         if (ref.length() > 0) {
                             scrollToReference(ref);
                         } else { // href="#"
                             scrollRectToVisible(new Rectangle(1,1,1,1));
                         }
                     }
                 }
             }
         });
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/BeanInfoSupport.java b/src/core/org/apache/jmeter/testbeans/BeanInfoSupport.java
index 51f49487e..45c8d27f9 100644
--- a/src/core/org/apache/jmeter/testbeans/BeanInfoSupport.java
+++ b/src/core/org/apache/jmeter/testbeans/BeanInfoSupport.java
@@ -1,304 +1,306 @@
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
  */
 package org.apache.jmeter.testbeans;
 
 import java.awt.Image;
 import java.beans.BeanDescriptor;
 import java.beans.BeanInfo;
 import java.beans.EventSetDescriptor;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.MethodDescriptor;
 import java.beans.PropertyDescriptor;
 import java.beans.SimpleBeanInfo;
 import java.util.MissingResourceException;
 import java.util.ResourceBundle;
 
 import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
 import org.apache.jmeter.testbeans.gui.TypeEditor;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Support class for test bean beanInfo objects. It will help using the
  * introspector to get most of the information, to then modify it at will.
  * <p>
  * To use, subclass it, create a subclass with a parameter-less constructor
  * that:
  * <ol>
  * <li>Calls super(beanClass)
  * <li>Modifies the property descriptors, bean descriptor, etc. at will.
  * </ol>
  * <p>
  * Even before any such modifications, a resource bundle named xxxResources
  * (where xxx is the fully qualified bean class name) will be obtained if
  * available and used to localize the following:
  * <ul>
  * <li>Bean's display name -- from property <b>displayName</b>.
  * <li>Properties' display names -- from properties <b><i>propertyName</i>.displayName</b>.
  * <li>Properties' short descriptions -- from properties <b><i>propertyName</i>.shortDescription</b>.
  * </ul>
  * <p>
  * The resource bundle will be stored as the bean descriptor's "resourceBundle"
  * attribute, so that it can be used for further localization. TestBeanGUI, for
  * example, uses it to obtain the group's display names from properties <b><i>groupName</i>.displayName</b>.
  *
  * @version $Revision$
  */
 public abstract class BeanInfoSupport extends SimpleBeanInfo {
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(BeanInfoSupport.class);
 
     // Some known attribute names, just for convenience:
     public static final String TAGS = GenericTestBeanCustomizer.TAGS;
 
     /** Whether the field must be defined (i.e. is required);  Boolean, defaults to FALSE */
     public static final String NOT_UNDEFINED = GenericTestBeanCustomizer.NOT_UNDEFINED;
 
     /** Whether the field disallows JMeter expressions; Boolean, default FALSE */
     public static final String NOT_EXPRESSION = GenericTestBeanCustomizer.NOT_EXPRESSION;
 
     /** Whether the field disallows constant values different from the provided tags; Boolean, default FALSE */
     public static final String NOT_OTHER = GenericTestBeanCustomizer.NOT_OTHER;
 
     /** If specified, create a multi-line editor */
     public static final String MULTILINE = GenericTestBeanCustomizer.MULTILINE;
 
     /** Default value, must be provided if {@link #NOT_UNDEFINED} is TRUE */
     public static final String DEFAULT = GenericTestBeanCustomizer.DEFAULT;
 
     /** Default value is not saved; only non-defaults are saved */
     public static final String DEFAULT_NOT_SAVED = GenericTestBeanCustomizer.DEFAULT_NOT_SAVED;
 
     /** Pointer to the resource bundle, if any (will generally be null) */
     public static final String RESOURCE_BUNDLE = GenericTestBeanCustomizer.RESOURCE_BUNDLE;
 
     /** TextEditor property */
     public static final String TEXT_LANGUAGE = GenericTestBeanCustomizer.TEXT_LANGUAGE;
 
     /** The BeanInfo for our class as obtained by the introspector. */
     private final BeanInfo rootBeanInfo;
 
     /** The descriptor for our class */
     private final BeanDescriptor beanDescriptor;
 
     /** The icons for this bean. */
     private final Image[] icons = new Image[5];
 
     /** The class for which we're providing the bean info. */
     private final Class<?> beanClass;
 
     /**
      * Construct a BeanInfo for the given class.
      *
      * @param beanClass
      *            class for which to construct a BeanInfo
      */
     protected BeanInfoSupport(Class<? extends TestBean> beanClass) {
         this.beanClass= beanClass;
 
         try {
             rootBeanInfo = Introspector.getBeanInfo(beanClass, Introspector.IGNORE_IMMEDIATE_BEANINFO);
         } catch (IntrospectionException e) {
             throw new Error("Can't introspect "+beanClass, e); // Programming error: bail out.
         }
 
         // N.B. JVMs other than Sun may return different instances each time
         // so we cache the value here (and avoid having to fetch it every time)
         beanDescriptor = rootBeanInfo.getBeanDescriptor();
 
         try {
             ResourceBundle resourceBundle = ResourceBundle.getBundle(
                     beanClass.getName() + "Resources",  // $NON-NLS-1$
                     JMeterUtils.getLocale());
 
             // Store the resource bundle as an attribute of the BeanDescriptor:
             getBeanDescriptor().setValue(RESOURCE_BUNDLE, resourceBundle);
             final String dnKey = "displayName";
             // Localize the bean name
             if (resourceBundle.containsKey(dnKey)) { // $NON-NLS-1$
                 getBeanDescriptor().setDisplayName(resourceBundle.getString(dnKey)); // $NON-NLS-1$
             } else {
-                log.debug("Localized display name not available for bean " + beanClass);                    
+                log.debug("Localized display name not available for bean {}", beanClass);
             }
             // Localize the property names and descriptions:
             PropertyDescriptor[] properties = getPropertyDescriptors();
             for (PropertyDescriptor property : properties) {
                 String name = property.getName();
                 final String propDnKey = name + ".displayName";
                 if(resourceBundle.containsKey(propDnKey)) {
                     property.setDisplayName(resourceBundle.getString(propDnKey)); // $NON-NLS-1$
                 } else {
-                    log.debug("Localized display name not available for property " + name + " in " + beanClass);
+                    log.debug("Localized display name not available for property {} in {}", name, beanClass);
                 }
                 final String propSdKey = name + ".shortDescription";
                 if(resourceBundle.containsKey(propSdKey)) {
                     property.setShortDescription(resourceBundle.getString(propSdKey));
                 } else {
-                    log.debug("Localized short description not available for property " + name + " in " + beanClass);
+                    log.debug("Localized short description not available for property {} in {}", name, beanClass);
                 }
             }
         } catch (MissingResourceException e) {
-            log.warn("Localized strings not available for bean " + beanClass, e);
+            log.warn("Localized strings not available for bean {}", beanClass, e);
         } catch (Exception e) {
-            log.warn("Something bad happened when loading bean info for bean " + beanClass, e);
+            log.warn("Something bad happened when loading bean info for bean {}", beanClass, e);
         }
     }
 
     /**
      * Get the property descriptor for the property of the given name.
      *
      * @param name
      *            property name
      * @return descriptor for a property of that name, or null if there's none
      */
     protected PropertyDescriptor property(String name) {
         for (PropertyDescriptor propdesc : getPropertyDescriptors()) {
             if (propdesc.getName().equals(name)) {
                 return propdesc;
             }
         }
-        log.error("Cannot find property: " + name + " in class " + beanClass);
+        log.error("Cannot find property: {} in class {}", name, beanClass);
         return null;
     }
 
     /**
      * Get the property descriptor for the property of the given name.
      * Sets the GUITYPE to the provided editor.
      *
      * @param name
      *            property name
      * @param editor the TypeEditor enum that describes the property editor
      *
      * @return descriptor for a property of that name, or null if there's none
      */
     protected PropertyDescriptor property(String name, TypeEditor editor) {
         PropertyDescriptor property = property(name);
         if (property != null) {
             property.setValue(GenericTestBeanCustomizer.GUITYPE, editor);
         }
         return property;
     }
 
     /**
      * Get the property descriptor for the property of the given name.
      * Sets the GUITYPE to the provided enum.
      *
      * @param name
      *            property name
      * @param enumClass the enum class that is to be used by the editor
      * @return descriptor for a property of that name, or null if there's none
      */
     protected PropertyDescriptor property(final String name, 
             final Class<? extends Enum<?>> enumClass) {
         PropertyDescriptor property = property(name);
         if (property != null) {
             property.setValue(GenericTestBeanCustomizer.GUITYPE, enumClass);
             // we also provide the resource bundle
             property.setValue(GenericTestBeanCustomizer.RESOURCE_BUNDLE, getBeanDescriptor().getValue(RESOURCE_BUNDLE));
         }
         return property;
     }
 
     /**
      * Set the bean's 16x16 colour icon.
      *
      * @param resourceName
      *            A pathname relative to the directory holding the class file of
      *            the current class.
      */
     protected void setIcon(String resourceName) {
         icons[ICON_COLOR_16x16] = loadImage(resourceName);
     }
 
     /** Number of groups created so far by createPropertyGroup. */
     private int numCreatedGroups = 0;
 
     /**
      * Utility method to group and order properties.
      * <p>
      * It will assign the given group name to each of the named properties, and
      * set their order attribute so that they are shown in the given order.
      * <p>
      * The created groups will get order 1, 2, 3,... in the order in which they
      * are created.
      *
      * @param group
      *            name of the group
      * @param names
      *            property names in the desired order
      */
     protected void createPropertyGroup(String group, String[] names) {
+        String name;
         for (int i = 0; i < names.length; i++) { // i is used below
-            log.debug("Getting property for: " + names[i]);
-            PropertyDescriptor p = property(names[i]);
+            name = names[i];
+            log.debug("Getting property for: {}", name);
+            PropertyDescriptor p = property(name);
             p.setValue(GenericTestBeanCustomizer.GROUP, group);
             p.setValue(GenericTestBeanCustomizer.ORDER, Integer.valueOf(i));
         }
         numCreatedGroups++;
         getBeanDescriptor().setValue(GenericTestBeanCustomizer.ORDER(group), Integer.valueOf(numCreatedGroups));
     }
 
     /** {@inheritDoc} */
     @Override
     public BeanInfo[] getAdditionalBeanInfo() {
         return rootBeanInfo.getAdditionalBeanInfo();
     }
 
     /** {@inheritDoc} */
     @Override
     public BeanDescriptor getBeanDescriptor() {
         return beanDescriptor;
     }
 
     /** {@inheritDoc} */
     @Override
     public int getDefaultEventIndex() {
         return rootBeanInfo.getDefaultEventIndex();
     }
 
     /** {@inheritDoc} */
     @Override
     public int getDefaultPropertyIndex() {
         return rootBeanInfo.getDefaultPropertyIndex();
     }
 
     /** {@inheritDoc} */
     @Override
     public EventSetDescriptor[] getEventSetDescriptors() {
         return rootBeanInfo.getEventSetDescriptors();
     }
 
     /** {@inheritDoc} */
     @Override
     public Image getIcon(int iconKind) {
         return icons[iconKind];
     }
 
     /** {@inheritDoc} */
     @Override
     public MethodDescriptor[] getMethodDescriptors() {
         return rootBeanInfo.getMethodDescriptors();
     }
 
     /** {@inheritDoc} */
     @Override
     public PropertyDescriptor[] getPropertyDescriptors() {
         return rootBeanInfo.getPropertyDescriptors();
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/TestBeanHelper.java b/src/core/org/apache/jmeter/testbeans/TestBeanHelper.java
index a4b49b317..c6494fb04 100644
--- a/src/core/org/apache/jmeter/testbeans/TestBeanHelper.java
+++ b/src/core/org/apache/jmeter/testbeans/TestBeanHelper.java
@@ -1,207 +1,207 @@
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
  */
 package org.apache.jmeter.testbeans;
 
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.Collection;
 import java.util.LinkedList;
 
 import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
 import org.apache.jmeter.testbeans.gui.TableEditor;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.CollectionProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.MultiProperty;
 import org.apache.jmeter.testelement.property.NullProperty;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.Converter;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This is an experimental class. An attempt to address the complexity of
  * writing new JMeter components.
  * <p>
  * TestBean currently extends AbstractTestElement to support
  * backward-compatibility, but the property-value-map may later on be separated
  * from the test beans themselves. To ensure this will be doable with minimum
  * damage, all inherited methods are deprecated.
  *
  */
 public class TestBeanHelper {
-    protected static final Logger log = LoggingManager.getLoggerForClass();
+    protected static final Logger log = LoggerFactory.getLogger(TestBeanHelper.class);
 
     /**
      * Prepare the bean for work by populating the bean's properties from the
      * property value map.
      * <p>
      *
      * @param el the TestElement to be prepared
      */
     public static void prepare(TestElement el) {
         if (!(el instanceof TestBean)) {
             return;
         }
         try {
             BeanInfo beanInfo = Introspector.getBeanInfo(el.getClass());
             PropertyDescriptor[] descs = beanInfo.getPropertyDescriptors();
 
             if (log.isDebugEnabled()) {
-                log.debug("Preparing " + el.getClass());
+                log.debug("Preparing {}", el.getClass());
             }
 
             for (PropertyDescriptor desc : descs) {
                 if (isDescriptorIgnored(desc)) {
                     if (log.isDebugEnabled()) {
-                        log.debug("Ignoring property '" + desc.getName() + "' in " + el.getClass().getCanonicalName());
+                        log.debug("Ignoring property '{}' in {}", desc.getName(), el.getClass().getCanonicalName());
                     }
                     continue;
                 }
                 // Obtain a value of the appropriate type for this property.
                 JMeterProperty jprop = el.getProperty(desc.getName());
                 Class<?> type = desc.getPropertyType();
                 Object value = unwrapProperty(desc, jprop, type);
 
                 if (log.isDebugEnabled()) {
-                    log.debug("Setting " + jprop.getName() + "=" + value);
+                    log.debug("Setting {}={}", jprop.getName(), value);
                 }
 
                 // Set the bean's property to the value we just obtained:
                 if (value != null || !type.isPrimitive())
                 // We can't assign null to primitive types.
                 {
                     Method writeMethod = desc.getWriteMethod();
                     if (writeMethod!=null) {
                         invokeOrBailOut(el, writeMethod, new Object[] {value});
                     }
                 }
             }
         } catch (IntrospectionException e) {
-            log.error("Couldn't set properties for " + el.getClass().getName(), e);
+            log.error("Couldn't set properties for {}", el.getClass(), e);
         } catch (UnsatisfiedLinkError ule) { // Can occur running headless on Jenkins
-            log.error("Couldn't set properties for " + el.getClass().getName());
+            log.error("Couldn't set properties for {}", el.getClass());
             throw ule;
         }
     }
 
     private static Object unwrapProperty(PropertyDescriptor desc, JMeterProperty jprop, Class<?> type) {
         Object value;
         if(jprop instanceof TestElementProperty)
         {
             TestElement te = ((TestElementProperty)jprop).getElement();
             if(te instanceof TestBean)
             {
                 prepare(te);
             }
             value = te;
         }
         else if(jprop instanceof MultiProperty)
         {
             value = unwrapCollection((MultiProperty)jprop,(String)desc.getValue(TableEditor.CLASSNAME));
         }
         // value was not provided, and this is allowed
         else if (jprop instanceof NullProperty &&
                 // use negative condition so missing (null) value is treated as FALSE
                 ! Boolean.TRUE.equals(desc.getValue(GenericTestBeanCustomizer.NOT_UNDEFINED))) {    
             value=null;
         } else {
             value = Converter.convert(jprop.getStringValue(), type);
         }
         return value;
     }
 
     private static Object unwrapCollection(MultiProperty prop, String type)
     {
         if(prop instanceof CollectionProperty)
         {
             Collection<Object> values = new LinkedList<>();
             for (JMeterProperty jMeterProperty : prop) {
                 try {
                     values.add(unwrapProperty(null, jMeterProperty, Class.forName(type)));
                 }
                 catch(Exception e) {
-                    log.error("Couldn't convert object: " + prop.getObjectValue() + " to " + type,e);
+                    log.error("Couldn't convert object: {} to {}", prop.getObjectValue(), type, e);
                 }
             }
             return values;
         }
         return null;
     }
 
     /**
      * Utility method that invokes a method and does the error handling around
      * the invocation.
      *
      * @param invokee
      *            the object on which the method should be invoked
      * @param method
      *            the method which should be invoked
      * @param params
      *            the parameters for the method
      * @return the result of the method invocation.
      */
     private static Object invokeOrBailOut(Object invokee, Method method, Object[] params) {
         try {
             return method.invoke(invokee, params);
         } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
             throw new Error(createMessage(invokee, method, params), e);
         }
     }
 
     private static String createMessage(Object invokee, Method method, Object[] params){
         StringBuilder sb = new StringBuilder();
         sb.append("This should never happen. Tried to invoke:\n");
         sb.append(invokee.getClass().getName());
         sb.append("#");
         sb.append(method.getName());
         sb.append("(");
         for(Object o : params) {
             if (o != null) {
                 sb.append(o.getClass().getSimpleName());
                 sb.append(' ');
             }
             sb.append(o);
             sb.append(' ');
         }
         sb.append(")");
         return sb.toString();
     }
 
     /**
      * Checks whether the descriptor should be ignored, i.e.
      * <ul>
      * <li>isHidden</li>
      * <li>isExpert and JMeter not using expert mode</li>
      * <li>no read method</li>
      * <li>no write method</li>
      * </ul>
      * @param descriptor the {@link PropertyDescriptor} to be checked
      * @return <code>true</code> if the descriptor should be ignored
      */
     public static boolean isDescriptorIgnored(PropertyDescriptor descriptor) {
         return descriptor.isHidden() 
             || (descriptor.isExpert() && !JMeterUtils.isExpertMode())
             || descriptor.getReadMethod() == null 
             || descriptor.getWriteMethod() == null;
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/gui/FieldStringEditor.java b/src/core/org/apache/jmeter/testbeans/gui/FieldStringEditor.java
index cf6e7a6ef..ef003fd0b 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/FieldStringEditor.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/FieldStringEditor.java
@@ -1,135 +1,132 @@
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
  */
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.Component;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.FocusEvent;
 import java.awt.event.FocusListener;
 import java.beans.PropertyEditorSupport;
 
 import javax.swing.JTextField;
 
-//import org.apache.jorphan.logging.LoggingManager;
-//import org.apache.log.Logger;
 
 /**
  * This class implements a property editor for non-null String properties that
  * supports custom editing (i.e.: provides a GUI component) based on a text
  * field.
  * <p>
  * The provided GUI is a simple text field.
  *
  */
 class FieldStringEditor extends PropertyEditorSupport implements ActionListener, FocusListener {
-//  private static final Logger log = LoggingManager.getLoggerForClass();
 
     /**
      * This will hold the text editing component, either a plain JTextField (in
      * cases where the combo box would not have other options than 'Edit'), or
      * the text editing component in the combo box.
      */
     private final JTextField textField;
 
     /**
      * Value on which we started the editing. Used to avoid firing
      * PropertyChanged events when there's not been such change.
      */
     private String initialValue = "";
 
     protected FieldStringEditor() {
         super();
 
         textField = new JTextField();
         textField.addActionListener(this);
         textField.addFocusListener(this);
     }
 
     @Override
     public String getAsText() {
         return textField.getText();
     }
 
     @Override
     public void setAsText(String value) {
         initialValue = value;
         textField.setText(value);
     }
 
     @Override
     public Object getValue() {
         return getAsText();
     }
 
     @Override
     public void setValue(Object value) {
         if (value instanceof String) {
             setAsText((String) value);
         } else {
             throw new IllegalArgumentException();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Component getCustomEditor() {
         return textField;
     }
 
     // TODO should this implement supportsCustomEditor() ?
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void firePropertyChange() {
         String newValue = getAsText();
 
         if (initialValue.equals(newValue)) {
             return;
         }
         initialValue = newValue;
 
         super.firePropertyChange();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void actionPerformed(ActionEvent e) {
         firePropertyChange();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void focusGained(FocusEvent e) {
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void focusLost(FocusEvent e) {
         firePropertyChange();
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/gui/GenericTestBeanCustomizer.java b/src/core/org/apache/jmeter/testbeans/gui/GenericTestBeanCustomizer.java
index ff1ca00a0..61d269858 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/GenericTestBeanCustomizer.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/GenericTestBeanCustomizer.java
@@ -1,804 +1,805 @@
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
  */
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.Component;
 import java.awt.GridBagConstraints;
 import java.awt.GridBagLayout;
 import java.awt.Insets;
 import java.beans.BeanInfo;
 import java.beans.PropertyDescriptor;
 import java.beans.PropertyEditor;
 import java.beans.PropertyEditorManager;
 import java.io.Serializable;
 import java.text.MessageFormat;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.Map;
 import java.util.ResourceBundle;
 
 import javax.swing.BorderFactory;
 import javax.swing.Box;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.SwingConstants;
 
 import org.apache.commons.lang3.ClassUtils;
 import org.apache.jmeter.gui.ClearGui;
 import org.apache.jmeter.testbeans.TestBeanHelper;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * The GenericTestBeanCustomizer is designed to provide developers with a
  * mechanism to quickly implement GUIs for new components.
  * <p>
  * It allows editing each of the public exposed properties of the edited type 'a
  * la JavaBeans': as far as the types of those properties have an associated
  * editor, there's no GUI development required.
  * <p>
  * This class understands the following PropertyDescriptor attributes:
  * <dl>
  * <dt>group: String</dt>
  * <dd>Group under which the property should be shown in the GUI. The string is
  * also used as a group title (but see comment on resourceBundle below). The
  * default group is "".</dd>
  * <dt>order: Integer</dt>
  * <dd>Order in which the property will be shown in its group. A smaller
  * integer means higher up in the GUI. The default order is 0. Properties of
  * equal order are sorted alphabetically.</dd>
  * <dt>tags: String[]</dt>
  * <dd>List of values to be offered for the property in addition to those
  * offered by its property editor.</dd>
  * <dt>notUndefined: Boolean</dt>
  * <dd>If true, the property should not be left undefined. A <b>default</b>
  * attribute must be provided if this is set.</dd>
  * <dt>notExpression: Boolean</dt>
  * <dd>If true, the property content should always be constant: JMeter
  * 'expressions' (strings using ${var}, etc...) can't be used.</dd>
  * <dt>notOther: Boolean</dt>
  * <dd>If true, the property content must always be one of the tags values or
  * null.</dd>
  * <dt>default: Object</dt>
  * <dd>Initial value for the property's GUI. Must be provided and be non-null
  * if <b>notUndefined</b> is set. Must be one of the provided tags (or null) if
  * <b>notOther</b> is set.
  * </dl>
  * <p>
  * The following BeanDescriptor attributes are also understood:
  * <dl>
  * <dt>group.<i>group</i>.order: Integer</dt>
  * <dd>where <b><i>group</i></b> is a group name used in a <b>group</b>
  * attribute in one or more PropertyDescriptors. Defines the order in which the
  * group will be shown in the GUI. A smaller integer means higher up in the GUI.
  * The default order is 0. Groups of equal order are sorted alphabetically.</dd>
  * <dt>resourceBundle: ResourceBundle</dt>
  * <dd>A resource bundle to be used for GUI localization: group display names
  * will be obtained from property "<b><i>group</i>.displayName</b>" if
  * available (where <b><i>group</i></b> is the group name).
  * </dl>
  */
 public class GenericTestBeanCustomizer extends JPanel implements SharedCustomizer {
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(GenericTestBeanCustomizer.class);
 
     // Need to register Editors for Java classes because we cannot create them
     // in the same package, nor can we create them in the built-in search patch of packages,
     // as that is not part of the public API
     static {
         PropertyEditorManager.registerEditor(Long.class,    LongPropertyEditor.class);
         PropertyEditorManager.registerEditor(Integer.class, IntegerPropertyEditor.class);
         PropertyEditorManager.registerEditor(Boolean.class, BooleanPropertyEditor.class);
     }
 
     public static final String GROUP = "group"; //$NON-NLS-1$
 
     public static final String ORDER = "order"; //$NON-NLS-1$
 
     /**
      * Array of permissible values.
      * <p>
      * Must be provided if:
      * <ul>
      * <li>{@link #NOT_OTHER} is TRUE, and</li>
      * <li>{@link PropertyEditor#getTags()} is null</li>
      * </ul>
      */
     public static final String TAGS = "tags"; //$NON-NLS-1$
 
     /** 
      * Whether the field must be defined (i.e. is required); 
      * Boolean, defaults to FALSE
      */
     public static final String NOT_UNDEFINED = "notUndefined"; //$NON-NLS-1$
 
     /** Whether the field disallows JMeter expressions; Boolean, default FALSE */
     public static final String NOT_EXPRESSION = "notExpression"; //$NON-NLS-1$
 
     /** Whether the field disallows constant values different from the provided tags; Boolean, default FALSE */
     public static final String NOT_OTHER = "notOther"; //$NON-NLS-1$
 
     /** If specified, create a multi-line editor */
     public static final String MULTILINE = "multiline";
 
     /** Default value, must be provided if {@link #NOT_UNDEFINED} is TRUE */
     public static final String DEFAULT = "default"; //$NON-NLS-1$
 
     /** Default value is not saved; only non-defaults are saved */
     public static final String DEFAULT_NOT_SAVED = "defaultNoSave"; //$NON-NLS-1$
 
     /** Pointer to the resource bundle, if any (will generally be null) */
     public static final String RESOURCE_BUNDLE = "resourceBundle"; //$NON-NLS-1$
 
     /** Property editor override; must be an enum of type {@link TypeEditor} */
     public static final String GUITYPE = "guiType"; // $NON-NLS-$
 
     /** TextEditor property */
     public static final String TEXT_LANGUAGE = "textLanguage"; //$NON-NLS-1$
 
     public static String ORDER(String group) {
         return "group." + group + ".order";
     }
 
     public static final String DEFAULT_GROUP = "";
 
     @SuppressWarnings("unused") // TODO - use or remove
     private int scrollerCount = 0;
 
     /**
      * BeanInfo object for the class of the objects being edited.
      */
     private transient BeanInfo beanInfo;
 
     /**
      * Property descriptors from the beanInfo.
      */
     private transient PropertyDescriptor[] descriptors;
 
     /**
      * Property editors -- or null if the property can't be edited. Unused if
      * customizerClass==null.
      */
     private transient PropertyEditor[] editors;
 
     /**
      * Message format for property field labels:
      */
     private MessageFormat propertyFieldLabelMessage;
 
     /**
      * Message format for property tooltips:
      */
     private MessageFormat propertyToolTipMessage;
 
     /**
      * The Map we're currently customizing. Set by setObject().
      */
     private Map<String, Object> propertyMap;
 
     /**
      * @deprecated only for use by test code
      */
     @Deprecated
     public GenericTestBeanCustomizer(){
         log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
     }
     /**
      * Create a customizer for a given test bean type.
      *
      * @param testBeanClass
      *            a subclass of TestBean
      * @see org.apache.jmeter.testbeans.TestBean
      */
     GenericTestBeanCustomizer(BeanInfo beanInfo) {
         super();
 
         this.beanInfo = beanInfo;
 
         // Get and sort the property descriptors:
         descriptors = beanInfo.getPropertyDescriptors();
         Arrays.sort(descriptors, new PropertyComparator(beanInfo));
 
         // Obtain the propertyEditors:
         editors = new PropertyEditor[descriptors.length];
         int scriptLanguageIndex = 0;
         int textAreaEditorIndex = 0;
         for (int i = 0; i < descriptors.length; i++) { // Index is also used for accessing editors array
             PropertyDescriptor descriptor = descriptors[i];
             String name = descriptor.getName();
 
             // Don't get editors for hidden or non-read-write properties:
             if (TestBeanHelper.isDescriptorIgnored(descriptor)) {
-                log.debug("Skipping editor for property " + name);
+                log.debug("Skipping editor for property {}", name);
                 editors[i] = null;
                 continue;
             }
 
             PropertyEditor propertyEditor;
             Object guiType = descriptor.getValue(GUITYPE);
             if (guiType instanceof TypeEditor) {
                 propertyEditor = ((TypeEditor) guiType).getInstance(descriptor);
             } else if (guiType instanceof Class && Enum.class.isAssignableFrom((Class<?>) guiType)) {
                     @SuppressWarnings("unchecked") // we check the class type above
                     final Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) guiType;
                     propertyEditor = new EnumEditor(descriptor, enumClass, (ResourceBundle) descriptor.getValue(GenericTestBeanCustomizer.RESOURCE_BUNDLE));
             } else {
                 Class<?> editorClass = descriptor.getPropertyEditorClass();
-                if (log.isDebugEnabled()) {
-                    log.debug("Property " + name + " has editor class " + editorClass);
-                }
-    
+                log.debug("Property {} has editor class {}", name, editorClass);
+
                 if (editorClass != null) {
                     try {
                         propertyEditor = (PropertyEditor) editorClass.newInstance();
                     } catch (InstantiationException | IllegalAccessException e) {
                         log.error("Can't create property editor.", e);
                         throw new Error(e.toString());
                     }
                 } else {
                     Class<?> c = descriptor.getPropertyType();
                     propertyEditor = PropertyEditorManager.findEditor(c);
                 }
             }
             
             if (propertyEditor == null) {
-                log.warn("No editor for property: " + name 
-                        + " type: " + descriptor.getPropertyType()
-                        + " in bean: " + beanInfo.getBeanDescriptor().getDisplayName()
-                        );
+                if (log.isWarnEnabled()) {
+                    log.warn("No editor for property: {} type: {} in bean: {}", name, descriptor.getPropertyType(),
+                            beanInfo.getBeanDescriptor().getDisplayName());
+                }
                 editors[i] = null;
                 continue;
             }
 
-            if (log.isDebugEnabled()) {
-                log.debug("Property " + name + " has property editor " + propertyEditor);
-            }
+            log.debug("Property {} has property editor {}", name, propertyEditor);
 
             validateAttributes(descriptor, propertyEditor);
 
             if (!propertyEditor.supportsCustomEditor()) {
                 propertyEditor = createWrapperEditor(propertyEditor, descriptor);
-
-                if (log.isDebugEnabled()) {
-                    log.debug("Editor for property " + name + " is wrapped in " + propertyEditor);
-                }
+                log.debug("Editor for property {} is wrapped in {}", name, propertyEditor);
             }
             if(propertyEditor instanceof TestBeanPropertyEditor)
             {
                 ((TestBeanPropertyEditor)propertyEditor).setDescriptor(descriptor);
             }
 
             if (propertyEditor instanceof TextAreaEditor) {
                 textAreaEditorIndex = i;
             }
             if (propertyEditor.getCustomEditor() instanceof JScrollPane) {
                 scrollerCount++;
             }
 
             editors[i] = propertyEditor;
 
             // Initialize the editor with the provided default value or null:
             setEditorValue(i, descriptor.getValue(DEFAULT));
 
             if (name.equals("scriptLanguage")) {
                 scriptLanguageIndex = i;
             }
 
         }
         // In case of BSF and JSR elements i want to add textAreaEditor as a listener to scriptLanguage ComboBox.
         String beanName = this.beanInfo.getBeanDescriptor().getName();
         if (beanName.startsWith("BSF") || beanName.startsWith("JSR223")) { // $NON-NLS-1$ $NON-NLS-2$
             WrapperEditor we = (WrapperEditor) editors[scriptLanguageIndex];
             TextAreaEditor tae = (TextAreaEditor) editors[textAreaEditorIndex];
             we.addChangeListener(tae);
         }
 
         // Obtain message formats:
         propertyFieldLabelMessage = new MessageFormat(JMeterUtils.getResString("property_as_field_label")); //$NON-NLS-1$
         propertyToolTipMessage = new MessageFormat(JMeterUtils.getResString("property_tool_tip")); //$NON-NLS-1$
 
         // Initialize the GUI:
         init();
     }
 
     /**
      * Validate the descriptor attributes.
      * 
      * @param pd the descriptor
      * @param pe the propertyEditor
      */
     private static void validateAttributes(PropertyDescriptor pd, PropertyEditor pe) {
         final Object deflt = pd.getValue(DEFAULT);
         if (deflt == null) {
             if (notNull(pd)) {
-                log.warn(getDetails(pd) + " requires a value but does not provide a default.");
+                if (log.isWarnEnabled()) {
+                    log.warn("{} requires a value but does not provide a default.", getDetails(pd));
+                }
             }
             if (noSaveDefault(pd)) {
-                log.warn(getDetails(pd) + " specifies DEFAULT_NO_SAVE but does not provide a default.");                
+                if (log.isWarnEnabled()) {
+                    log.warn("{} specifies DEFAULT_NO_SAVE but does not provide a default.", getDetails(pd));
+                }
             }
         } else {
             final Class<?> defltClass = deflt.getClass(); // the DEFAULT class
             // Convert int to Integer etc:
             final Class<?> propClass = ClassUtils.primitiveToWrapper(pd.getPropertyType());
             if (!propClass.isAssignableFrom(defltClass) ){
-                log.warn(getDetails(pd) + " has a DEFAULT of class " + defltClass.getCanonicalName());
+                if (log.isWarnEnabled()) {
+                    log.warn("{} has a DEFAULT of class {}", getDetails(pd), defltClass.getCanonicalName());
+                }
             }            
         }
         if (notOther(pd) && pd.getValue(TAGS) == null && pe.getTags() == null) {
-            log.warn(getDetails(pd) + " does not have tags but other values are not allowed.");
+            if (log.isWarnEnabled()) {
+                log.warn("{} does not have tags but other values are not allowed.", getDetails(pd));
+            }
         }
         if (!notNull(pd)) {
             Class<?> propertyType = pd.getPropertyType();
             if (propertyType.isPrimitive()) {
-                log.warn(getDetails(pd) + " allows null but is a primitive type");
+                if (log.isWarnEnabled()) {
+                    log.warn("{} allows null but is a primitive type", getDetails(pd));
+                }
             }
         }
         if (!pd.attributeNames().hasMoreElements()) {
-            log.warn(getDetails(pd) + " does not appear to have been configured");            
+            if (log.isWarnEnabled()) {
+                log.warn("{} does not appear to have been configured", getDetails(pd));
+            }
         }
     }
 
     /**
      * Identify the property from the descriptor.
      * 
      * @param pd
      * @return the property details
      */
     private static String getDetails(PropertyDescriptor pd) {
         StringBuilder sb = new StringBuilder();
         sb.append(pd.getReadMethod().getDeclaringClass().getName());
         sb.append('#');
         sb.append(pd.getName());
         sb.append('(');
         sb.append(pd.getPropertyType().getCanonicalName());
         sb.append(')');
         return sb.toString();
     }
 
     /**
      * Find the default typeEditor and a suitable guiEditor for the given
      * property descriptor, and combine them in a WrapperEditor.
      *
      * @param typeEditor
      * @param descriptor
      * @return the wrapper editor
      */
     private WrapperEditor createWrapperEditor(PropertyEditor typeEditor, PropertyDescriptor descriptor) {
         String[] editorTags = typeEditor.getTags();
         String[] additionalTags = (String[]) descriptor.getValue(TAGS);
         String[] tags = null;
         if (editorTags == null) {
             tags = additionalTags;
         } else if (additionalTags == null) {
             tags = editorTags;
         } else {
             tags = new String[editorTags.length + additionalTags.length];
             int j = 0;
             for (String editorTag : editorTags) {
                 tags[j++] = editorTag;
             }
             for (String additionalTag : additionalTags) {
                 tags[j++] = additionalTag;
             }
         }
 
         boolean notNull = notNull(descriptor);
         boolean notExpression = notExpression(descriptor);
         boolean notOther = notOther(descriptor);
 
         PropertyEditor guiEditor;
         if (notNull && tags == null) {
             guiEditor = new FieldStringEditor();
         } else {
             guiEditor = new ComboStringEditor(tags, notExpression && notOther, notNull,
                     (ResourceBundle) descriptor.getValue(GenericTestBeanCustomizer.RESOURCE_BUNDLE));
         }
 
         WrapperEditor wrapper = new WrapperEditor(typeEditor, guiEditor,
                 !notNull, // acceptsNull
                 !notExpression, // acceptsExpressions
                 !notOther, // acceptsOther
                 descriptor.getValue(DEFAULT));
 
         return wrapper;
     }
 
     /**
      * Returns true if the property disallows constant values different from the provided tags.
      * 
      * @param descriptor the property descriptor
      * @return true if the attribute {@link #NOT_OTHER} is defined and equal to Boolean.TRUE;
      *  otherwise the default is false
      */
     static boolean notOther(PropertyDescriptor descriptor) {
         boolean notOther = Boolean.TRUE.equals(descriptor.getValue(NOT_OTHER));
         return notOther;
     }
 
     /**
      * Returns true if the property does not allow JMeter expressions.
      * 
      * @param descriptor the property descriptor
      * @return true if the attribute {@link #NOT_EXPRESSION} is defined and equal to Boolean.TRUE;
      *  otherwise the default is false
      */
     static boolean notExpression(PropertyDescriptor descriptor) {
         boolean notExpression = Boolean.TRUE.equals(descriptor.getValue(NOT_EXPRESSION));
         return notExpression;
     }
 
     /**
      * Returns true if the property must be defined (i.e. is required); 
      * 
      * @param descriptor the property descriptor
      * @return true if the attribute {@link #NOT_UNDEFINED} is defined and equal to Boolean.TRUE;
      *  otherwise the default is false
      */
     static boolean notNull(PropertyDescriptor descriptor) {
         boolean notNull = Boolean.TRUE.equals(descriptor.getValue(NOT_UNDEFINED));
         return notNull;
     }
 
     /**
      * Returns true if the property default value is not saved
      * 
      * @param descriptor the property descriptor
      * @return true if the attribute {@link #DEFAULT_NOT_SAVED} is defined and equal to Boolean.TRUE;
      *  otherwise the default is false
      */
     static boolean noSaveDefault(PropertyDescriptor descriptor) {
         return Boolean.TRUE.equals(descriptor.getValue(DEFAULT_NOT_SAVED));
     }
 
     /**
      * Set the value of the i-th property, properly reporting a possible
      * failure.
      *
      * @param i
      *            the index of the property in the descriptors and editors
      *            arrays
      * @param value
      *            the value to be stored in the editor
      *
      * @throws IllegalArgumentException
      *             if the editor refuses the value
      */
     private void setEditorValue(int i, Object value) throws IllegalArgumentException {
         editors[i].setValue(value);
     }
 
 
     /**
      * {@inheritDoc}
      * @param map must be an instance of Map&lt;String, Object&gt;
      */
     @SuppressWarnings("unchecked")
     @Override
     public void setObject(Object map) {
         propertyMap = (Map<String, Object>) map;
 
         if (propertyMap.size() == 0) {
             // Uninitialized -- set it to the defaults:
             for (PropertyDescriptor descriptor : descriptors) {
                 Object value = descriptor.getValue(DEFAULT);
                 String name = descriptor.getName();
                 if (value != null) {
                     propertyMap.put(name, value);
-                    log.debug("Set " + name + "= " + value);
+                    log.debug("Set {}={}", name, value);
                 }
                 firePropertyChange(name, null, value);
             }
         }
 
         // Now set the editors to the element's values:
         for (int i = 0; i < editors.length; i++) {
             if (editors[i] == null) {
                 continue;
             }
             try {
                 setEditorValue(i, propertyMap.get(descriptors[i].getName()));
             } catch (IllegalArgumentException e) {
                 // I guess this can happen as a result of a bad
                 // file read? In this case, it would be better to replace the
                 // incorrect value with anything valid, e.g. the default value
                 // for the property.
                 // But for the time being, I just prefer to be aware of any
                 // problems occuring here, most likely programming errors,
                 // so I'll bail out.
                 // (MS Note) Can't bail out - newly create elements have blank
                 // values and must get the defaults.
                 // Also, when loading previous versions of jmeter test scripts,
                 // some values
                 // may not be right, and should get default values - MS
                 // TODO: review this and possibly change to:
                 setEditorValue(i, descriptors[i].getValue(DEFAULT));
             }
         }
     }
 
 //  /**
 //   * Find the index of the property of the given name.
 //   *
 //   * @param name
 //   *            the name of the property
 //   * @return the index of that property in the descriptors array, or -1 if
 //   *         there's no property of this name.
 //   */
 //  private int descriptorIndex(String name) // NOTUSED
 //  {
 //      for (int i = 0; i < descriptors.length; i++) {
 //          if (descriptors[i].getName().equals(name)) {
 //              return i;
 //          }
 //      }
 //      return -1;
 //  }
 
     /**
      * Initialize the GUI.
      */
     private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
         setLayout(new GridBagLayout());
 
         GridBagConstraints cl = new GridBagConstraints(); // for labels
         cl.gridx = 0;
         cl.anchor = GridBagConstraints.EAST;
         cl.insets = new Insets(0, 1, 0, 1);
 
         GridBagConstraints ce = new GridBagConstraints(); // for editors
         ce.fill = GridBagConstraints.BOTH;
         ce.gridx = 1;
         ce.weightx = 1.0;
         ce.insets = new Insets(0, 1, 0, 1);
 
         GridBagConstraints cp = new GridBagConstraints(); // for panels
         cp.fill = GridBagConstraints.BOTH;
         cp.gridx = 1;
         cp.gridy = GridBagConstraints.RELATIVE;
         cp.gridwidth = 2;
         cp.weightx = 1.0;
 
         JPanel currentPanel = this;
         String currentGroup = DEFAULT_GROUP;
         int y = 0;
 
         for (int i = 0; i < editors.length; i++) {
             if (editors[i] == null) {
                 continue;
             }
 
             if (log.isDebugEnabled()) {
-                log.debug("Laying property " + descriptors[i].getName());
+                log.debug("Laying property {}", descriptors[i].getName());
             }
 
             String g = group(descriptors[i]);
             if (!currentGroup.equals(g)) {
                 if (currentPanel != this) {
                     add(currentPanel, cp);
                 }
                 currentGroup = g;
                 currentPanel = new JPanel(new GridBagLayout());
                 currentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                         groupDisplayName(g)));
                 cp.weighty = 0.0;
                 y = 0;
             }
 
             Component customEditor = editors[i].getCustomEditor();
 
             boolean multiLineEditor = false;
             if (customEditor.getPreferredSize().height > 50 || customEditor instanceof JScrollPane
                     || descriptors[i].getValue(MULTILINE) != null) {
                 // TODO: the above works in the current situation, but it's
                 // just a hack. How to get each editor to report whether it
                 // wants to grow bigger? Whether the property label should
                 // be at the left or at the top of the editor? ...?
                 multiLineEditor = true;
             }
 
             JLabel label = createLabel(descriptors[i]);
             label.setLabelFor(customEditor);
 
             cl.gridy = y;
             cl.gridwidth = multiLineEditor ? 2 : 1;
             cl.anchor = multiLineEditor ? GridBagConstraints.CENTER : GridBagConstraints.EAST;
             currentPanel.add(label, cl);
 
             ce.gridx = multiLineEditor ? 0 : 1;
             ce.gridy = multiLineEditor ? ++y : y;
             ce.gridwidth = multiLineEditor ? 2 : 1;
             ce.weighty = multiLineEditor ? 1.0 : 0.0;
 
             cp.weighty += ce.weighty;
 
             currentPanel.add(customEditor, ce);
 
             y++;
         }
         if (currentPanel != this) {
             add(currentPanel, cp);
         }
 
         // Add a 0-sized invisible component that will take all the vertical
         // space that nobody wants:
         cp.weighty = 0.0001;
         add(Box.createHorizontalStrut(0), cp);
     }
 
     private JLabel createLabel(PropertyDescriptor desc) {
         String text = desc.getDisplayName();
         if (!"".equals(text)) {
             text = propertyFieldLabelMessage.format(new Object[] { desc.getDisplayName() });
         }
         // if the displayName is the empty string, leave it like that.
         JLabel label = new JLabel(text);
         label.setHorizontalAlignment(SwingConstants.TRAILING);
         label.setToolTipText(propertyToolTipMessage.format(new Object[] { desc.getShortDescription() }));
 
         return label;
     }
 
     /**
      * Obtain a property descriptor's group.
      *
      * @param descriptor
      * @return the group String.
      */
     private static String group(PropertyDescriptor descriptor) {
         String group = (String) descriptor.getValue(GROUP);
         if (group == null){
             group = DEFAULT_GROUP;
         }
         return group;
     }
 
     /**
      * Obtain a group's display name
      */
     private String groupDisplayName(String group) {
         ResourceBundle b = (ResourceBundle) beanInfo.getBeanDescriptor().getValue(RESOURCE_BUNDLE);
         if (b == null) {
             return group;
         }
         String key = new StringBuilder(group).append(".displayName").toString();
         if (b.containsKey(key)) {
             return b.getString(key);
         } else {
             return group;
         }
     }
 
     /**
      * Comparator used to sort properties for presentation in the GUI.
      */
     private static class PropertyComparator implements Comparator<PropertyDescriptor>, Serializable {
         private static final long serialVersionUID = 240L;
 
         private final BeanInfo beanInfo;
         public PropertyComparator(BeanInfo beanInfo) {
             this.beanInfo = beanInfo;
         }
 
         @Override
         public int compare(PropertyDescriptor d1, PropertyDescriptor d2) {
             String g1 = group(d1);
             String g2 = group(d2);
             Integer go1 = groupOrder(g1), go2 = groupOrder(g2);
 
             int result = go1.compareTo(go2);
             if (result != 0) {
                 return result;
             }
 
             result = g1.compareTo(g2);
             if (result != 0) {
                 return result;
             }
 
             Integer po1 = propertyOrder(d1);
             Integer po2 = propertyOrder(d2);
             result = po1.compareTo(po2);
             if (result != 0) {
                 return result;
             }
 
             return d1.getName().compareTo(d2.getName());
         }
 
         /**
          * Obtain a group's order.
          *
          * @param group
          *            group name
          * @return the group's order (zero by default)
          */
         private Integer groupOrder(String group) {
             Integer order = (Integer) beanInfo.getBeanDescriptor().getValue(ORDER(group));
             if (order == null) {
                 order = Integer.valueOf(0);
             }
             return order;
         }
 
         /**
          * Obtain a property's order.
          *
          * @param d
          * @return the property's order attribute (zero by default)
          */
         private Integer propertyOrder(PropertyDescriptor d) {
             Integer order = (Integer) d.getValue(ORDER);
             if (order == null) {
                 order = Integer.valueOf(0);
             }
             return order;
         }
     }
 
     /**
      * Save values from the GUI fields into the property map
      */
     void saveGuiFields() {
         for (int i = 0; i < editors.length; i++) {
             PropertyEditor propertyEditor=editors[i]; // might be null (e.g. in testing)
             if (propertyEditor != null) {
                 Object value = propertyEditor.getValue();
                 String name = descriptors[i].getName();
                 if (value == null) {
                     propertyMap.remove(name);
-                    if (log.isDebugEnabled()) {
-                        log.debug("Unset " + name);
-                    }
+                    log.debug("Unset {}", name);
                 } else {
                     propertyMap.put(name, value);
-                    if (log.isDebugEnabled()) {
-                        log.debug("Set " + name + "= " + value);
-                    }
+                    log.debug("Set {}={}", name, value);
                 }
             }
         }
     }
 
     void clearGuiFields() {
         for (int i = 0; i < editors.length; i++) {
             PropertyEditor propertyEditor=editors[i]; // might be null (e.g. in testing)
             if (propertyEditor != null) {
                 try {
                 if (propertyEditor instanceof ClearGui) {
                     ((ClearGui) propertyEditor).clearGui();
                 } else if (propertyEditor instanceof WrapperEditor){
                     WrapperEditor we = (WrapperEditor) propertyEditor;
                     String[] tags = we.getTags();
                     if (tags != null && tags.length > 0) {
                         we.setAsText(tags[0]);
                     } else {
                         we.resetValue();
                     }
                 } else {
                     propertyEditor.setAsText("");
                 }
                 } catch (IllegalArgumentException ex){
-                    log.error("Failed to set field "+descriptors[i].getName(),ex);
+                    log.error("Failed to set field {}", descriptors[i].getName(), ex);
                 }
             }
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/testbeans/gui/TableEditor.java b/src/core/org/apache/jmeter/testbeans/gui/TableEditor.java
index 436245d1d..19098eeea 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/TableEditor.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/TableEditor.java
@@ -1,417 +1,417 @@
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
 
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.Dimension;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.FocusEvent;
 import java.awt.event.FocusListener;
 import java.beans.PropertyDescriptor;
 import java.beans.PropertyEditorSupport;
 import java.lang.reflect.Method;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 
 import javax.swing.CellEditor;
 import javax.swing.JButton;
 import javax.swing.JComponent;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTable;
 import javax.swing.ListSelectionModel;
 import javax.swing.event.TableModelEvent;
 import javax.swing.event.TableModelListener;
 
 import org.apache.jmeter.gui.ClearGui;
 import org.apache.jmeter.testelement.property.TestElementProperty;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.gui.GuiUtils;
 import org.apache.jorphan.gui.ObjectTableModel;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.reflect.Functor;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * Table editor for TestBean GUI properties.
  * Currently only works for:
  * <ul>
  * <li>property type Collection of {@link String}s, where there is a single header entry</li>
  * </ul>
  */
 public class TableEditor extends PropertyEditorSupport implements FocusListener,TestBeanPropertyEditor,TableModelListener, ClearGui {
-    private static final Logger LOG = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TableEditor.class);
 
     /** 
      * attribute name for class name of a table row;
      * value must be java.lang.String, or a class which supports set and get/is methods for the property name.
      */
     public static final String CLASSNAME = "tableObject.classname"; // $NON-NLS-1$
 
     /** 
      * attribute name for table headers, value must be a String array.
      * If {@link #CLASSNAME} is java.lang.String, there must be only a single entry.
      */
     public static final String HEADERS = "table.headers"; // $NON-NLS-1$
 
     /** attribute name for property names within the {@link #CLASSNAME}, value must be String array */
     public static final String OBJECT_PROPERTIES = "tableObject.properties"; // $NON-NLS-1$
 
     private JTable table;
     private ObjectTableModel model;
     private Class<?> clazz;
     private PropertyDescriptor descriptor;
     private final JButton addButton;
     private final JButton clipButton;
     private final JButton removeButton;
     private final JButton clearButton;
     private final JButton upButton;
     private final JButton downButton;
 
     public TableEditor() {
         addButton = new JButton(JMeterUtils.getResString("add")); // $NON-NLS-1$
         addButton.addActionListener(new AddListener());
         clipButton = new JButton(JMeterUtils.getResString("add_from_clipboard")); // $NON-NLS-1$
         clipButton.addActionListener(new ClipListener());
         removeButton = new JButton(JMeterUtils.getResString("remove")); // $NON-NLS-1$
         removeButton.addActionListener(new RemoveListener());
         clearButton = new JButton(JMeterUtils.getResString("clear")); // $NON-NLS-1$
         clearButton.addActionListener(new ClearListener());
         upButton = new JButton(JMeterUtils.getResString("up")); // $NON-NLS-1$
         upButton.addActionListener(new UpListener());
         downButton = new JButton(JMeterUtils.getResString("down")); // $NON-NLS-1$
         downButton.addActionListener(new DownListener());
     }
 
     @Override
     public String getAsText() {
         return null;
     }
 
     @Override
     public Component getCustomEditor() {
         JComponent pane = makePanel();
         pane.doLayout();
         pane.validate();
         return pane;
     }
 
     private JComponent makePanel() {
         JPanel p = new JPanel(new BorderLayout());
         JScrollPane scroller = new JScrollPane(table);
         table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         scroller.setMinimumSize(new Dimension(100, 70));
         scroller.setPreferredSize(scroller.getMinimumSize());
         p.add(scroller,BorderLayout.CENTER);
         JPanel south = new JPanel();
         south.add(addButton);
         south.add(clipButton);
         removeButton.setEnabled(false);
         south.add(removeButton);
         clearButton.setEnabled(false);
         south.add(clearButton);
         upButton.setEnabled(false);
         south.add(upButton);
         downButton.setEnabled(false);
         south.add(downButton);
         p.add(south,BorderLayout.SOUTH);
         return p;
     }
 
     @Override
     public Object getValue() {
         return model.getObjectList();
     }
 
     @Override
     public void setAsText(String text) throws IllegalArgumentException {
         //not interested in this method.
     }
 
     @Override
     public void setValue(Object value) {
         if(value != null) {
             model.setRows(convertCollection((Collection<?>)value));
         } else {
             model.clearData();
         }
         
         if(model.getRowCount()>0) {
             removeButton.setEnabled(true);
             clearButton.setEnabled(true);
         } else {
             removeButton.setEnabled(false);
             clearButton.setEnabled(false);
         }
         
         if(model.getRowCount()>1) {
             upButton.setEnabled(true);
             downButton.setEnabled(true);
         } else {
             upButton.setEnabled(false);
             downButton.setEnabled(false);
         }
         
         this.firePropertyChange();
     }
 
     private Collection<Object> convertCollection(Collection<?> values) {
         List<Object> l = new LinkedList<>();
         for(Object obj : values) {
             if(obj instanceof TestElementProperty) {
                 l.add(((TestElementProperty)obj).getElement());
             } else {
                 l.add(obj);
             }
         }
         return l;
     }
 
     @Override
     public boolean supportsCustomEditor() {
         return true;
     }
 
     /**
      * For the table editor, the CLASSNAME attribute must simply be the name of the class of object it will hold
      * where each row holds one object.
      */
     @Override
     public void setDescriptor(PropertyDescriptor descriptor) {
         this.descriptor = descriptor;
         String value = (String)descriptor.getValue(CLASSNAME);
         if (value == null) {
             throw new RuntimeException("The Table Editor requires the CLASSNAME atttribute be set - the name of the object to represent a row");
         }
         try {
             clazz = Class.forName(value);
             initializeModel();
         } catch (ClassNotFoundException e) {
             throw new RuntimeException("Could not find the CLASSNAME class "+ value, e);
         }
     }
 
     void initializeModel()
     {
         Object hdrs = descriptor.getValue(HEADERS);
         if (!(hdrs instanceof String[])) {
             throw new RuntimeException("attribute HEADERS must be a String array");            
         }
         if(clazz == String.class) {
             model = new ObjectTableModel((String[])hdrs,new Functor[0],new Functor[0],new Class[]{String.class});
         } else {
             Object value = descriptor.getValue(OBJECT_PROPERTIES);
             if (!(value instanceof String[])) {
                 throw new RuntimeException("attribute OBJECT_PROPERTIES must be a String array");
             }
             String[] props = (String[])value;
             Functor[] writers = new Functor[props.length];
             Functor[] readers = new Functor[props.length];
             Class<?>[] editors = new Class[props.length];
             int count = 0;
             for(String propName : props) {
                 propName = propName.substring(0,1).toUpperCase(Locale.ENGLISH) + propName.substring(1);
                 writers[count] = createWriter(clazz,propName);
                 readers[count] = createReader(clazz,propName);
                 editors[count] = getArgForWriter(clazz,propName);
                 count++;
             }
             model = new ObjectTableModel((String[])hdrs,readers,writers,editors);
         }
         model.addTableModelListener(this);
         table = new JTable(model);
         JMeterUtils.applyHiDPI(table);
         table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         table.addFocusListener(this);
     }
 
     Functor createWriter(Class<?> c,String propName) {
         String setter = "set" + propName; // $NON-NLS-1$
         return new Functor(setter);
     }
 
     Functor createReader(Class<?> c,String propName) {
         String getter = "get" + propName; // $NON-NLS-1$
         try {
             c.getMethod(getter,new Class[0]);
             return new Functor(getter);
         } catch(Exception e) {
             return new Functor("is" + propName);
         }
     }
 
     Class<?> getArgForWriter(Class<?> c,String propName) {
         String setter = "set" + propName; // $NON-NLS-1$
         for(Method m : c.getMethods()) {
             if(m.getName().equals(setter)) {
                 return m.getParameterTypes()[0];
             }
         }
         return null;
     }
 
     @Override
     public void tableChanged(TableModelEvent e) {
         this.firePropertyChange();
     }
 
     @Override
     public void focusGained(FocusEvent e) {
 
     }
 
     @Override
     public void focusLost(FocusEvent e) {
         final int editingRow = table.getEditingRow();
         final int editingColumn = table.getEditingColumn();
         CellEditor ce = null;
         if (editingRow != -1 && editingColumn != -1) {
             ce = table.getCellEditor(editingRow,editingColumn);
         }
         Component editor = table.getEditorComponent();
         if(ce != null && (editor == null || editor != e.getOppositeComponent())) {
             ce.stopCellEditing();
         } else if(editor != null) {
             editor.addFocusListener(this);
         }
         this.firePropertyChange();
     }
 
     private class AddListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             try {
                 model.addRow(clazz.newInstance());
                 
                 removeButton.setEnabled(true);
                 clearButton.setEnabled(true);
             } catch(Exception err) {
-                LOG.error("The class type given to TableEditor was not instantiable. ", err);
+                log.error("The class type given to TableEditor was not instantiable.", err);
             }
         }
     }
     
     private class ClipListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             try {
                 String clipboardContent = GuiUtils.getPastedText();
                 if(clipboardContent == null) {
                     return;
                 }
 
                 String[] clipboardLines = clipboardContent.split("\n"); // $NON-NLS-1$
                 for (String clipboardLine : clipboardLines) {
                     String[] columns = clipboardLine.split("\t"); // $NON-NLS-1$
 
                     model.addRow(clazz.newInstance());
                     
                     for (int i=0; i < columns.length; i++) {
                         model.setValueAt(columns[i], model.getRowCount() - 1, i);
                     }
                 }
 
                 if(model.getRowCount()>1) {
                     upButton.setEnabled(true);
                     downButton.setEnabled(true);
                 } else {
                     upButton.setEnabled(false);
                     downButton.setEnabled(false);
                 }
             } catch (Exception err) {
-                LOG.error("The class type given to TableEditor was not instantiable. ", err);
+                log.error("The class type given to TableEditor was not instantiable.", err);
             }
         }
     }
 
     private class RemoveListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             int[] rows = table.getSelectedRows();
             for(int i=0; i<rows.length; i++){
               model.removeRow(rows[i]-i);
             }
             
             if(model.getRowCount()>1) {
                 upButton.setEnabled(true);
                 downButton.setEnabled(true);
             } else {
                 upButton.setEnabled(false);
                 downButton.setEnabled(false);
             }
         }
     }
 
     private class ClearListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             model.clearData();
             
             upButton.setEnabled(false);
             downButton.setEnabled(false);
         }
     }
     
     private class UpListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             GuiUtils.cancelEditing(table);
 
             int[] rowsSelected = table.getSelectedRows();
             if (rowsSelected.length > 0 && rowsSelected[0] > 0) {
                 table.clearSelection();
                 for (int rowSelected : rowsSelected) {
                     model.moveRow(rowSelected, rowSelected + 1, rowSelected - 1);
                 }
                 for (int rowSelected : rowsSelected) {
                     table.addRowSelectionInterval(rowSelected - 1, rowSelected - 1);
                 }
             }            
         }
     }
     
     private class DownListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
             GuiUtils.cancelEditing(table);
             
             int[] rowsSelected = table.getSelectedRows();
             if (rowsSelected.length > 0 && rowsSelected[rowsSelected.length - 1] < table.getRowCount() - 1) {
                 table.clearSelection();
                 for (int i = rowsSelected.length - 1; i >= 0; i--) {
                     int rowSelected = rowsSelected[i];
                     model.moveRow(rowSelected, rowSelected + 1, rowSelected + 1);
                 }
                 for (int rowSelected : rowsSelected) {
                     table.addRowSelectionInterval(rowSelected + 1, rowSelected + 1);
                 }
             }
         }
     }
 
     @Override
     public void clearGui() {
         this.model.clearData();
     }
     
 }
diff --git a/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java b/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
index c5b307fa7..1ac55e5c5 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/TestBeanGUI.java
@@ -1,525 +1,525 @@
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
  */
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.beans.BeanDescriptor;
 import java.beans.BeanInfo;
 import java.beans.Customizer;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.beans.PropertyEditorManager;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.ResourceBundle;
 
 import javax.swing.JPopupMenu;
 
 import org.apache.commons.collections.map.LRUMap;
 import org.apache.jmeter.assertions.Assertion;
 import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
 import org.apache.jmeter.config.ConfigElement;
 import org.apache.jmeter.config.gui.AbstractConfigGui;
 import org.apache.jmeter.control.Controller;
 import org.apache.jmeter.control.gui.AbstractControllerGui;
 import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
 import org.apache.jmeter.gui.JMeterGUIComponent;
 import org.apache.jmeter.gui.util.MenuFactory;
 import org.apache.jmeter.processor.PostProcessor;
 import org.apache.jmeter.processor.PreProcessor;
 import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
 import org.apache.jmeter.processor.gui.AbstractPreProcessorGui;
 import org.apache.jmeter.samplers.Sampler;
 import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
 import org.apache.jmeter.testbeans.TestBean;
 import org.apache.jmeter.testelement.TestElement;
 import org.apache.jmeter.testelement.property.AbstractProperty;
 import org.apache.jmeter.testelement.property.JMeterProperty;
 import org.apache.jmeter.testelement.property.PropertyIterator;
 import org.apache.jmeter.timers.Timer;
 import org.apache.jmeter.timers.gui.AbstractTimerGui;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jmeter.util.LocaleChangeEvent;
 import org.apache.jmeter.util.LocaleChangeListener;
 import org.apache.jmeter.visualizers.Visualizer;
 import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
-import org.apache.jorphan.logging.LoggingManager;
 import org.apache.jorphan.util.JOrphanUtils;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * JMeter GUI element editing for TestBean elements.
  * <p>
  * The actual GUI is always a bean customizer: if the bean descriptor provides
  * one, it will be used; otherwise, a GenericTestBeanCustomizer will be created
  * for this purpose.
  * <p>
  * Those customizers deviate from the standards only in that, instead of a bean,
  * they will receive a Map in the setObject call. This will be a property name
  * to value Map. The customizer is also in charge of initializing empty Maps
  * with sensible initial values.
  * <p>
  * If the provided Customizer class implements the SharedCustomizer interface,
  * the same instance of the customizer will be reused for all beans of the type:
  * setObject(map) can then be called multiple times. Otherwise, one separate
  * instance will be used for each element. For efficiency reasons, most
  * customizers should implement SharedCustomizer.
  *
  */
 public class TestBeanGUI extends AbstractJMeterGuiComponent implements JMeterGUIComponent, LocaleChangeListener{
-    private static final long serialVersionUID = 240L;
+    private static final long serialVersionUID = 241L;
 
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(TestBeanGUI.class);
 
     private final Class<?> testBeanClass;
 
     private transient BeanInfo beanInfo;
 
     private final Class<?> customizerClass;
 
     /**
      * The single customizer if the customizer class implements
      * SharedCustomizer, null otherwise.
      */
     private Customizer customizer = null;
 
     /**
      * TestElement to Customizer map if customizer is null. This is necessary to
      * avoid the cost of creating a new customizer on each edit. The cache size
      * needs to be limited, though, to avoid memory issues when editing very
      * large test plans.
      */
     @SuppressWarnings("unchecked")
     private final Map<TestElement, Customizer> customizers = new LRUMap(20);
 
     /**
      * Index of the customizer in the JPanel's child component list:
      */
     private int customizerIndexInPanel;
 
     /**
      * The property name to value map that the active customizer edits:
      */
     private final Map<String, Object> propertyMap = new HashMap<>();
 
     /**
      * Whether the GUI components have been created.
      */
     private boolean initialized = false;
 
     static {
         List<String> paths = new LinkedList<>();
         paths.add("org.apache.jmeter.testbeans.gui");// $NON-NLS-1$
         paths.addAll(Arrays.asList(PropertyEditorManager.getEditorSearchPath()));
         String s = JMeterUtils.getPropDefault("propertyEditorSearchPath", null);// $NON-NLS-1$
         if (s != null) {
             paths.addAll(Arrays.asList(JOrphanUtils.split(s, ",", "")));// $NON-NLS-1$ // $NON-NLS-2$
         }
         PropertyEditorManager.setEditorSearchPath(paths.toArray(new String[paths.size()]));
     }
 
     /**
      * @deprecated Dummy for JUnit test purposes only
      */
     @Deprecated
     public TestBeanGUI() {
         log.warn("Constructor only for use in testing");// $NON-NLS-1$
         testBeanClass = null;
         customizerClass = null;
         beanInfo = null;
     }
 
     public TestBeanGUI(Class<?> testBeanClass) {
         super();
-        log.debug("testing class: " + testBeanClass.getName());
+        log.debug("testing class: {}", testBeanClass);
         // A quick verification, just in case:
         if (!TestBean.class.isAssignableFrom(testBeanClass)) {
             Error e = new Error();
             log.error("This should never happen!", e);
             throw e; // Programming error: bail out.
         }
 
         this.testBeanClass = testBeanClass;
 
         // Get the beanInfo:
         try {
             beanInfo = Introspector.getBeanInfo(testBeanClass);
         } catch (IntrospectionException e) {
-            log.error("Can't get beanInfo for " + testBeanClass.getName(), e);
+            log.error("Can't get beanInfo for {}", testBeanClass, e);
             throw new Error(e.toString()); // Programming error. Don't
                                             // continue.
         }
 
         customizerClass = beanInfo.getBeanDescriptor().getCustomizerClass();
 
         // Creation of the customizer and GUI initialization is delayed until
         // the
         // first
         // configure call. We don't need all that just to find out the static
         // label, menu
         // categories, etc!
         initialized = false;
         JMeterUtils.addLocaleChangeListener(this);
     }
 
     private Customizer createCustomizer() {
         try {
             return (Customizer) customizerClass.newInstance();
         } catch (InstantiationException | IllegalAccessException e) {
-            log.error("Could not instantiate customizer of class " + customizerClass, e);
+            log.error("Could not instantiate customizer of {}", customizerClass, e);
             throw new Error(e.toString());
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public String getStaticLabel() {
         if (beanInfo == null){
             return "null";// $NON-NLS-1$
         }
         return beanInfo.getBeanDescriptor().getDisplayName();
     }
 
     /**
      * {@inheritDoc}
      */
    @Override
 public TestElement createTestElement() {
         try {
             TestElement element = (TestElement) testBeanClass.newInstance();
             // In other GUI component, clearGUI resets the value to defaults one as there is one GUI per Element
             // With TestBeanGUI as it's shared, its default values are only known here, we must call setValues with 
             // element (as it holds default values)
             // otherwise we will get values as computed by customizer reset and not default ones
             if(initialized) {
                 setValues(element);
             }
             // configure(element);
             // super.clear(); // set name, enabled.
             modifyTestElement(element); // put the default values back into the
             // new element
             return element;
         } catch (InstantiationException | IllegalAccessException e) {
             log.error("Can't create test element", e);
             throw new Error(e.toString()); // Programming error. Don't
                                             // continue.
         }
     }
 
    /**
     * {@inheritDoc}
     */
     @Override
     public void modifyTestElement(TestElement element) {
         // Fetch data from screen fields
         if (customizer instanceof GenericTestBeanCustomizer) {
             GenericTestBeanCustomizer gtbc = (GenericTestBeanCustomizer) customizer;
             gtbc.saveGuiFields();
         }
         configureTestElement(element);
 
         // Copy all property values from the map into the element:
         for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {
             String name = desc.getName();
             Object value = propertyMap.get(name);
-            log.debug("Modify " + name + " to " + value);
+            log.debug("Modify {} to {}", name, value);
             if (value == null) {
                 if (GenericTestBeanCustomizer.notNull(desc)) { // cannot be null
                     if (GenericTestBeanCustomizer.noSaveDefault(desc)) {
-                        log.debug("Did not set DEFAULT for " + name);
+                        log.debug("Did not set DEFAULT for {}", name);
                         element.removeProperty(name);
                     } else {
                         setPropertyInElement(element, name, desc.getValue(GenericTestBeanCustomizer.DEFAULT));
                     }
                 } else {
                     element.removeProperty(name);
                 }
             } else {
                 if (GenericTestBeanCustomizer.noSaveDefault(desc) && value.equals(desc.getValue(GenericTestBeanCustomizer.DEFAULT))) {
-                    log.debug("Did not set " + name + " to the default: " + value);
+                    log.debug("Did not set {} to the default: {}", name, value);
                     element.removeProperty(name);
                 } else {
                     setPropertyInElement(element, name, value);
                 }
             }
         }
     }
 
     /**
      * @param element
      * @param name
      */
     private void setPropertyInElement(TestElement element, String name, Object value) {
         JMeterProperty jprop = AbstractProperty.createProperty(value);
         jprop.setName(name);
         element.setProperty(jprop);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public JPopupMenu createPopupMenu() {
         if (Timer.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultTimerMenu();
         }
         else if(Sampler.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultSamplerMenu();
         }
         else if(ConfigElement.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultConfigElementMenu();
         }
         else if(Assertion.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultAssertionMenu();
         }
         else if(PostProcessor.class.isAssignableFrom(testBeanClass) ||
                 PreProcessor.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultExtractorMenu();
         }
         else if(Visualizer.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultVisualizerMenu();
         }
         else if(Controller.class.isAssignableFrom(testBeanClass))
         {
             return MenuFactory.getDefaultControllerMenu();
         }
         else {
-            log.warn("Cannot determine PopupMenu for "+testBeanClass.getName());
+            log.warn("Cannot determine PopupMenu for {}", testBeanClass);
             return MenuFactory.getDefaultMenu();
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void configure(TestElement element) {
         if (!initialized){
             init();
         }
         clearGui();
 
         super.configure(element);
 
         setValues(element);
 
         initialized = true;
     }
     
     /**
      * Get values from element to fill propertyMap and setup customizer 
      * @param element TestElement
      */
     private void setValues(TestElement element) {
         // Copy all property values into the map:
         for (PropertyIterator jprops = element.propertyIterator(); jprops.hasNext();) {
             JMeterProperty jprop = jprops.next();
             propertyMap.put(jprop.getName(), jprop.getObjectValue());
         }
         
         if (customizer != null) {
             customizer.setObject(propertyMap);
         } else {
             if (initialized){
                 remove(customizerIndexInPanel);
             }
             Customizer c = customizers.get(element);
             if (c == null) {
                 c = createCustomizer();
                 c.setObject(propertyMap);
                 customizers.put(element, c);
             }
             add((Component) c, BorderLayout.CENTER);
         }
     }
 
     /** {@inheritDoc} */
     @Override
     public Collection<String> getMenuCategories() {
         List<String> menuCategories = new LinkedList<>();
         BeanDescriptor bd = beanInfo.getBeanDescriptor();
 
         // We don't want to show expert beans in the menus unless we're
         // in expert mode:
         if (bd.isExpert() && !JMeterUtils.isExpertMode()) {
             return null;
         }
 
         int matches = setupGuiClasses(menuCategories);
         if (matches == 0) {
-            log.error("Could not assign GUI class to " + testBeanClass.getName());
+            log.error("Could not assign GUI class to {}", testBeanClass);
         } else if (matches > 1) {// may be impossible, but no harm in
                                     // checking ...
-            log.error("More than 1 GUI class found for " + testBeanClass.getName());
+            log.error("More than 1 GUI class found for {}", testBeanClass);
         }
         return menuCategories;
     }
 
     /**
      * Setup GUI class
      * @return number of matches
      */
     public int setupGuiClasses() {
         return setupGuiClasses(new ArrayList<String>());
     }
     
     /**
      * Setup GUI class
      * @param menuCategories List<String> menu categories
      * @return number of matches
      */
     private int setupGuiClasses(List<String> menuCategories ) {
         int matches = 0;// How many classes can we assign from?
         // TODO: there must be a nicer way...
         BeanDescriptor bd = beanInfo.getBeanDescriptor();
         if (Assertion.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.ASSERTIONS);
             bd.setValue(TestElement.GUI_CLASS, AbstractAssertionGui.class.getName());
             matches++;
         }
         if (ConfigElement.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.CONFIG_ELEMENTS);
             bd.setValue(TestElement.GUI_CLASS, AbstractConfigGui.class.getName());
             matches++;
         }
         if (Controller.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.CONTROLLERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractControllerGui.class.getName());
             matches++;
         }
         if (Visualizer.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.LISTENERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractVisualizer.class.getName());
             matches++;
         }
         if (PostProcessor.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.POST_PROCESSORS);
             bd.setValue(TestElement.GUI_CLASS, AbstractPostProcessorGui.class.getName());
             matches++;
         }
         if (PreProcessor.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.PRE_PROCESSORS);
             bd.setValue(TestElement.GUI_CLASS, AbstractPreProcessorGui.class.getName());
             matches++;
         }
         if (Sampler.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.SAMPLERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractSamplerGui.class.getName());
             matches++;
         }
         if (Timer.class.isAssignableFrom(testBeanClass)) {
             menuCategories.add(MenuFactory.TIMERS);
             bd.setValue(TestElement.GUI_CLASS, AbstractTimerGui.class.getName());
             matches++;
         }
         return matches;
     }
 
     private void init() {
         setLayout(new BorderLayout(0, 5));
 
         setBorder(makeBorder());
         add(makeTitlePanel(), BorderLayout.NORTH);
 
         customizerIndexInPanel = getComponentCount();
 
         if (customizerClass == null) {
             customizer = new GenericTestBeanCustomizer(beanInfo);
         } else if (SharedCustomizer.class.isAssignableFrom(customizerClass)) {
             customizer = createCustomizer();
         }
 
         if (customizer != null){
             add((Component) customizer, BorderLayout.CENTER);
         }
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public String getLabelResource() {
         // @see getStaticLabel
         return null;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void clearGui() {
         super.clearGui();
         if (customizer instanceof GenericTestBeanCustomizer) {
             GenericTestBeanCustomizer gtbc = (GenericTestBeanCustomizer) customizer;
             gtbc.clearGuiFields();
         }
         propertyMap.clear();
     }
 
     public boolean isHidden() {
         return beanInfo.getBeanDescriptor().isHidden();
     }
 
     public boolean isExpert() {
         return beanInfo.getBeanDescriptor().isExpert();
     }
 
     /**
      * Handle Locale Change by reloading BeanInfo
      * @param event {@link LocaleChangeEvent}
      */
     @Override
     public void localeChanged(LocaleChangeEvent event) {
         try {
             beanInfo = Introspector.getBeanInfo(testBeanClass);
             setupGuiClasses();
         } catch (IntrospectionException e) {
-            log.error("Can't get beanInfo for " + testBeanClass.getName(), e);
+            log.error("Can't get beanInfo for {}", testBeanClass, e);
             JMeterUtils.reportErrorToUser("Can't get beanInfo for " + testBeanClass.getName());
         }
     }
 
     /**
      * {@inheritDoc}}
      * @see org.apache.jmeter.gui.AbstractJMeterGuiComponent#getDocAnchor()
      */
     @Override
     public String getDocAnchor() {
         ResourceBundle resourceBundle = ResourceBundle.getBundle(
                 testBeanClass.getName() + "Resources",  // $NON-NLS-1$
                 new Locale("",""));
 
         String name = resourceBundle.getString("displayName");
         return name.replace(' ', '_');
     }
 }
diff --git a/src/core/org/apache/jmeter/testbeans/gui/WrapperEditor.java b/src/core/org/apache/jmeter/testbeans/gui/WrapperEditor.java
index b725575df..e8e935eb7 100644
--- a/src/core/org/apache/jmeter/testbeans/gui/WrapperEditor.java
+++ b/src/core/org/apache/jmeter/testbeans/gui/WrapperEditor.java
@@ -1,446 +1,456 @@
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
  */
 package org.apache.jmeter.testbeans.gui;
 
 import java.awt.Component;
 import java.beans.PropertyChangeEvent;
 import java.beans.PropertyChangeListener;
 import java.beans.PropertyEditor;
 import java.beans.PropertyEditorSupport;
 import java.util.Arrays;
 
 import javax.swing.JOptionPane;
 
 import org.apache.jmeter.gui.GuiPackage;
 import org.apache.jmeter.util.JMeterUtils;
-import org.apache.jorphan.logging.LoggingManager;
-import org.apache.log.Logger;
+import org.slf4j.Logger;
+import org.slf4j.LoggerFactory;
 
 /**
  * This is an implementation of a full-fledged property editor, providing both
  * object-text transformation and an editor GUI (a custom editor component),
  * from two simpler property editors providing only one of these functions
  * each, namely:
  * <dl>
  * <dt>typeEditor
  * <dt>
  * <dd>Provides suitable object-to-string and string-to-object transformation
  * for the property's type. That is: it's a simple editor that only need to
  * support the set/getAsText and set/getValue methods.</dd>
  * <dt>guiEditor</dt>
  * <dd>Provides a suitable GUI for the property, but works on [possibly null]
  * String values. That is: it supportsCustomEditor, but get/setAsText and
  * get/setValue are identical.</dd>
  * </dl>
  * <p>
  * The resulting editor provides optional support for null values (you can
  * choose whether <strong>null</strong> is to be a valid property value). It also
  * provides optional support for JMeter 'expressions' (you can choose whether
  * they make valid property values).
  *
  */
 class WrapperEditor extends PropertyEditorSupport implements PropertyChangeListener {
-    private static final Logger log = LoggingManager.getLoggerForClass();
+    private static final Logger log = LoggerFactory.getLogger(WrapperEditor.class);
 
     /** The type's property editor. */
     private final PropertyEditor typeEditor;
 
     /** The gui property editor */
     private final PropertyEditor guiEditor;
 
     /** Whether to allow <b>null</b> as a property value. */
     private final boolean acceptsNull;
 
     /** Whether to allow JMeter 'expressions' as property values. */
     private final boolean acceptsExpressions;
 
     /** Whether to allow any constant values different from the provided tags. */
     private final boolean acceptsOther;
 
     /** Default value to be used to (re-)initialise the field */
     private final Object defaultValue;
 
     /**
      * Keep track of the last valid value in the editor, so that we can revert
      * to it if the user enters an invalid value.
      */
     private String lastValidValue = null;
 
     /**
      * Constructor for use when a PropertyEditor is delegating to us.
      */
     WrapperEditor(
             Object source, PropertyEditor typeEditor, PropertyEditor guiEditor,
             boolean acceptsNull, boolean acceptsExpressions,
             boolean acceptsOther, Object defaultValue) {
         super();
         if (source != null) {
             super.setSource(source);
         }
         this.typeEditor = typeEditor;
         this.guiEditor = guiEditor;
         this.acceptsNull = acceptsNull;
         this.acceptsExpressions = acceptsExpressions;
         this.acceptsOther = acceptsOther;
         this.defaultValue = defaultValue;
         initialize();
     }
 
     /**
      * Constructor for use for regular instantiation and by subclasses.
      */
     WrapperEditor(
             PropertyEditor typeEditor, PropertyEditor guiEditor,
             boolean acceptsNull, boolean acceptsExpressions,
             boolean acceptsOther, Object defaultValue) {
         this(null, typeEditor, guiEditor, acceptsNull, acceptsExpressions,  acceptsOther, defaultValue);
     }
 
     final void resetValue() {
         setValue(defaultValue);
         lastValidValue = getAsText();        
     }
 
     private void initialize() {
 
         resetValue();
 
         if (guiEditor instanceof ComboStringEditor) {
             String[] tags = guiEditor.getTags();
 
             // Provide an initial edit value if necessary -- this is a heuristic
             // that tries to provide the most convenient initial edit value:
 
             String v;
             if (!acceptsOther) {
                 v = "${}"; //$NON-NLS-1$
             } else if (isValidValue("")) { //$NON-NLS-1$
                 v = ""; //$NON-NLS-1$
             } else if (acceptsExpressions) {
                 v = "${}"; //$NON-NLS-1$
             } else if (tags != null && tags.length > 0) {
                 v = tags[0];
             } else {
                 v = getAsText();
             }
 
             ((ComboStringEditor) guiEditor).setInitialEditValue(v);
         }
 
         guiEditor.addPropertyChangeListener(this);
     }
 
     @Override
     public boolean supportsCustomEditor() {
         return true;
     }
 
     @Override
     public Component getCustomEditor() {
         return guiEditor.getCustomEditor();
     }
 
     @Override
     public String[] getTags() {
         return guiEditor.getTags();
     }
 
     /**
      * Determine whether a string is one of the known tags.
      *
      * @param text the value to be checked
      * @return true if text equals one of the getTags()
      */
     private boolean isATag(String text) {
         String[] tags = getTags();
         if (tags == null) {
             return false;
         }
         for (String tag : tags) {
             if (tag.equals(text)) {
                 return true;
             }
         }
         return false;
     }
 
     /**
      * Determine whether a string is a valid value for the property.
      *
      * @param text
      *            the value to be checked
      * @return true iif text is a valid value
      */
     private boolean isValidValue(String text) {
         if (text == null) {
             return acceptsNull;
         }
 
         if (acceptsExpressions && isExpression(text)) {
             return true;
         }
 
         // Not an expression (isn't or can't be), not null.
 
         // The known tags are assumed to be valid:
         if (isATag(text)) {
             return true;
         }
 
         // Was not a tag, so if we can't accept other values...
         if (!acceptsOther) {
             return false;
         }
 
         // Delegate the final check to the typeEditor:
         try {
             typeEditor.setAsText(text);
         } catch (IllegalArgumentException e1) {
             return false; // setAsText failed: not valid
         }
         // setAsText succeeded: valid
         return true;
     }
 
     /**
      * This method is used to do some low-cost defensive programming: it is
      * called when a condition that the program logic should prevent from
      * happening occurs. I hope this will help early detection of logical bugs
      * in property value handling.
      *
      * @throws Error
      *             always throws an error.
      */
     private void shouldNeverHappen(String msg) throws Error {
         throw new Error(msg); // Programming error: bail out.
     }
 
     /**
      * Same as shouldNeverHappen(), but provide a source exception.
      *
      * @param e
      *            the exception that helped identify the problem
      * @throws Error
      *             always throws one.
      */
     private void shouldNeverHappen(Exception e) throws Error {
         throw new Error(e.toString()); // Programming error: bail out.
     }
 
     /**
      * Check if a string is a valid JMeter 'expression'.
      * <p>
      * The current implementation is very basic: it just accepts any string
      * containing "${" as a valid expression.
      * TODO: improve, but keep returning true for "${}".
      */
     private boolean isExpression(String text) {
         return text.contains("${");//$NON-NLS-1$
     }
 
     /**
      * Same as isExpression(String).
      *
      * @param text
      * @return true if text is a String and isExpression(text).
      */
     private boolean isExpression(Object text) {
         return text instanceof String && isExpression((String) text);
     }
 
     /**
      * @see java.beans.PropertyEditor#getValue()
      * @see org.apache.jmeter.testelement.property.JMeterProperty
      */
     @Override
     public Object getValue() {
         String text = (String) guiEditor.getValue();
 
         Object value;
 
         if (text == null) {
             if (!acceptsNull) {
                 shouldNeverHappen("Text is null but null is not allowed");
             }
             value = null;
         } else {
             if (acceptsExpressions && isExpression(text)) {
                 value = text;
             } else {
                 // not an expression (isn't or can't be), not null.
 
                 // a check, just in case:
                 if (!acceptsOther && !isATag(text)) {
                     shouldNeverHappen("Text is not a tag but other entries are not allowed");
                 }
 
                 try {
                     // Bug 44314  Number field does not seem to accept ""
                     try {
                         typeEditor.setAsText(text);
                     } catch (NumberFormatException e) {
                         if (text.length() == 0){
                             text = "0";//$NON-NLS-1$
                             typeEditor.setAsText(text);
                         } else {
                             shouldNeverHappen(e);
                         }
                     }
                 } catch (IllegalArgumentException e) {
                     shouldNeverHappen(e);
                 }
                 value = typeEditor.getValue();
             }
         }
 
         if (log.isDebugEnabled()) {
-            log.debug("->" + (value != null ? value.getClass().getName() : "NULL") + ":" + value);
+            if (value == null) {
+                log.debug("->NULL:null");
+            } else {
+                log.debug("->{}:{}", value.getClass().getName(), value);
+            }
         }
         return value;
     }
 
     @Override
     public final void setValue(Object value) { /// final because called from ctor
         String text;
 
         if (log.isDebugEnabled()) {
-            log.debug("<-" + (value != null ? value.getClass().getName() : "NULL") + ":" + value);
+            if (value == null) {
+                log.debug("<-NULL:null");
+            } else {
+                log.debug("<-{}:{}", value.getClass().getName(), value);
+            }
         }
 
         if (value == null) {
             if (!acceptsNull) {
                 throw new IllegalArgumentException("Null is not allowed");
             }
             text = null;
         } else if (acceptsExpressions && isExpression(value)) {
             text = (String) value;
         } else {
             // Not an expression (isn't or can't be), not null.
             typeEditor.setValue(value); // may throw IllegalArgumentExc.
             text = fixGetAsTextBug(typeEditor.getAsText());
 
             if (!acceptsOther && !isATag(text)) {
                 throw new IllegalArgumentException("Value not allowed: '" + text + "' is not in " + Arrays.toString(getTags()));
             }
         }
 
         guiEditor.setValue(text);
     }
 
     /*
      * Fix bug in JVMs that return true/false rather than True/False
      * from the type editor getAsText() method
      */
     private String fixGetAsTextBug(String asText) {
         if (asText == null){
             return null;
         }
         if (asText.equals("true")){
             log.debug("true=>True");// so we can detect it
             return "True";
         }
         if (asText.equals("false")){
             log.debug("false=>False");// so we can detect it
             return "False";
         }
         return asText;
     }
 
     @Override
     public String getAsText() {
         String text = fixGetAsTextBug(guiEditor.getAsText());
 
         if (text == null) {
             if (!acceptsNull) {
                 shouldNeverHappen("Text is null, but null is not allowed");
             }
         } else if (!acceptsExpressions || !isExpression(text)) {
             // not an expression (can't be or isn't), not null.
             try {
                 typeEditor.setAsText(text); // ensure value is propagated to editor
             } catch (IllegalArgumentException e) {
                 shouldNeverHappen(e);
             }
             text = fixGetAsTextBug(typeEditor.getAsText());
 
             // a check, just in case:
             if (!acceptsOther && !isATag(text)) {
                 shouldNeverHappen("Text is not a tag, but other values are not allowed");
             }
         }
 
-        if (log.isDebugEnabled()) {
-            log.debug("->\"" + text + "\"");
-        }
+        log.debug("->\"{}\"", text);
         return text;
     }
 
     @Override
     public void setAsText(String text) throws IllegalArgumentException {
         if (log.isDebugEnabled()) {
-            log.debug(text == null ? "<-null" : "<-\"" + text + "\"");
+            if (text == null) {
+                log.debug("<-null");
+            } else {
+                log.debug("<-\"{}\"", text);
+            }
         }
 
         String value;
 
         if (text == null) {
             if (!acceptsNull) {
                 throw new IllegalArgumentException("Null parameter not allowed");
             }
             value = null;
         } else {
             if (acceptsExpressions && isExpression(text)) {
                 value = text;
             } else {
                 // Some editors do tiny transformations (e.g. "true" to
                 // "True",...):
                 typeEditor.setAsText(text); // may throw IllegalArgumentException
                 value = typeEditor.getAsText();
 
                 if (!acceptsOther && !isATag(text)) {
                     throw new IllegalArgumentException("Value not allowed: "+text);
                 }
             }
         }
 
         guiEditor.setValue(value);
     }
 
     @Override
     public void propertyChange(PropertyChangeEvent event) {
         String text = fixGetAsTextBug(guiEditor.getAsText());
         if (isValidValue(text)) {
             lastValidValue = text;
             firePropertyChange();
         } else {
             if (GuiPackage.getInstance() == null){
-                log.warn("Invalid value: "+text+" "+typeEditor);
+                log.warn("Invalid value: {} {}", text, typeEditor);
             } else {
                 JOptionPane.showMessageDialog(guiEditor.getCustomEditor().getParent(),
                    JMeterUtils.getResString("property_editor.value_is_invalid_message"),//$NON-NLS-1$
                     JMeterUtils.getResString("property_editor.value_is_invalid_title"),  //$NON-NLS-1$
                     JOptionPane.WARNING_MESSAGE);
             }
             // Revert to the previous value:
             guiEditor.setAsText(lastValidValue);
         }
     }
 
     public void addChangeListener(PropertyChangeListener listener) {
         guiEditor.addPropertyChangeListener(listener);
     }
 }
