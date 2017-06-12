diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index ae67ef3e8..e23115164 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,972 +1,1018 @@
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
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 import java.util.Objects;
 import java.util.Properties;
 
 import org.apache.commons.lang3.CharUtils;
+import org.apache.commons.lang3.time.FastDateFormat;
 import org.apache.jmeter.save.CSVSaveService;
 import org.apache.jmeter.testelement.TestPlan;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.util.JMeterError;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /*
  * N.B. to add a new field, remember the following
  * - static _xyz
  * - instance xyz=_xyz
  * - clone s.xyz = xyz (perhaps)
  * - setXyz(boolean)
  * - saveXyz()
  * - add Xyz to SAVE_CONFIG_NAMES list
  * - update SampleSaveConfigurationConverter to add new fields to marshall() and shouldSerialiseMember()
  * - update ctor SampleSaveConfiguration(boolean value) to set the value if it is a boolean property
  * - update SampleResultConverter and/or HTTPSampleConverter
  * - update CSVSaveService: CSV_XXXX, makeResultFromDelimitedString, printableFieldNamesToString, static{}
  * - update messages.properties to add save_xyz entry
  * - update jmeter.properties to add new property
  * - update listeners.xml to add new property, CSV and XML names etc.
  * - take screenshot sample_result_config.png
  * - update listeners.xml and component_reference.xml with new dimensions (might not change)
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
     private static final long serialVersionUID = 8L;
 
     private static final Logger log = LoggerFactory.getLogger(SampleSaveConfiguration.class);
 
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
     public static final String MILLISECONDS = "ms"; // $NON_NLS-1$
 
     /** A properties file indicator for none. * */
     public static final String NONE = "none"; // $NON_NLS-1$
 
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
     public static final String DEFAULT_DELIMITER_PROP = "jmeter.save.saveservice.default_delimiter"; // $NON_NLS-1$
 
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
     
     // Save bytes written
     private static final String SAVE_SENT_BYTES_PROP = "jmeter.save.saveservice.sent_bytes"; // $NON_NLS-1$
 
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
     
     // Defaults from properties:
     private static final boolean TIME;
     private static final boolean TIMESTAMP;
     private static final boolean SUCCESS;
     private static final boolean LABEL;
     private static final boolean CODE;
     private static final boolean MESSAGE;
     private static final boolean THREAD_NAME;
     private static final boolean IS_XML;
     private static final boolean RESPONSE_DATA;
     private static final boolean DATATYPE;
     private static final boolean ENCODING;
     private static final boolean ASSERTIONS;
     private static final boolean LATENCY;
     private static final boolean CONNECT_TIME;
     private static final boolean SUB_RESULTS;
     private static final boolean SAMPLER_DATA;
     private static final boolean FIELD_NAMES;
     private static final boolean RESPONSE_HEADERS;
     private static final boolean REQUEST_HEADERS;
 
     private static final boolean RESPONSE_DATA_ON_ERROR;
 
     private static final boolean SAVE_ASSERTION_RESULTS_FAILURE_MESSAGE;
 
-    private static final String TIMESTAMP_FORMAT;
-
     private static final int ASSERTIONS_RESULT_TO_SAVE;
 
     // TODO turn into method?
     public static final int SAVE_NO_ASSERTIONS = 0;
 
     public static final int SAVE_FIRST_ASSERTION = SAVE_NO_ASSERTIONS + 1;
 
     public static final int SAVE_ALL_ASSERTIONS = SAVE_FIRST_ASSERTION + 1;
 
     private static final boolean PRINT_MILLISECONDS;
 
     private static final boolean BYTES;
     
     private static final boolean SENT_BYTES;
 
     private static final boolean URL;
 
     private static final boolean FILE_NAME;
 
     private static final boolean HOST_NAME;
 
     private static final boolean THREAD_COUNTS;
 
     private static final boolean SAMPLE_COUNT;
 
-    private static final DateFormat DATE_FORMATTER;
+    private static final String DATE_FORMAT;
 
     /**
      * The string used to separate fields when stored to disk, for example, the
      * comma for CSV files.
      */
     private static final String DELIMITER;
 
     private static final boolean IDLE_TIME;
 
     public static final String DEFAULT_DELIMITER = ","; // $NON_NLS-1$
 
     /**
      * Read in the properties having to do with saving from a properties file.
      */
     static {
         Properties props = JMeterUtils.getJMeterProperties();
 
         SUB_RESULTS      = TRUE.equalsIgnoreCase(props.getProperty(SUBRESULTS_PROP, TRUE));
         ASSERTIONS      = TRUE.equalsIgnoreCase(props.getProperty(ASSERTIONS_PROP, TRUE));
         LATENCY         = TRUE.equalsIgnoreCase(props.getProperty(LATENCY_PROP, TRUE));
         CONNECT_TIME     = TRUE.equalsIgnoreCase(props.getProperty(CONNECT_TIME_PROP, TRUE));
         SAMPLER_DATA     = TRUE.equalsIgnoreCase(props.getProperty(SAMPLERDATA_PROP, FALSE));
         RESPONSE_HEADERS = TRUE.equalsIgnoreCase(props.getProperty(RESPONSEHEADERS_PROP, FALSE));
         REQUEST_HEADERS  = TRUE.equalsIgnoreCase(props.getProperty(REQUESTHEADERS_PROP, FALSE));
         ENCODING        = TRUE.equalsIgnoreCase(props.getProperty(ENCODING_PROP, FALSE));
 
         String dlm = JMeterUtils.getDelimiter(props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER));
         char ch = dlm.charAt(0);
 
         if (CharUtils.isAsciiAlphanumeric(ch) || ch == CSVSaveService.QUOTING_CHAR){
             throw new JMeterError("Delimiter '"+ch+"' must not be alphanumeric or "+CSVSaveService.QUOTING_CHAR+".");
         }
 
         if (ch != '\t' && !CharUtils.isAsciiPrintable(ch)){
             throw new JMeterError("Delimiter (code "+(int)ch+") must be printable.");
         }
 
         DELIMITER = dlm;
 
         FIELD_NAMES = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, TRUE));
 
         DATATYPE = TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));
 
         LABEL = TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));
 
         CODE = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));
 
         RESPONSE_DATA = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));
 
         RESPONSE_DATA_ON_ERROR = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_ON_ERROR_PROP, FALSE));
 
         MESSAGE = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));
 
         SUCCESS = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));
 
         THREAD_NAME = TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));
 
         BYTES = TRUE.equalsIgnoreCase(props.getProperty(SAVE_BYTES_PROP, TRUE));
         
         SENT_BYTES = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SENT_BYTES_PROP, TRUE));
 
         URL = TRUE.equalsIgnoreCase(props.getProperty(SAVE_URL_PROP, FALSE));
 
         FILE_NAME = TRUE.equalsIgnoreCase(props.getProperty(SAVE_FILENAME_PROP, FALSE));
 
         HOST_NAME = TRUE.equalsIgnoreCase(props.getProperty(SAVE_HOSTNAME_PROP, FALSE));
 
         TIME = TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));
 
-        TIMESTAMP_FORMAT = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);
+        String temporaryTimestampFormat = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);
 
-        PRINT_MILLISECONDS = MILLISECONDS.equalsIgnoreCase(TIMESTAMP_FORMAT);
+        PRINT_MILLISECONDS = MILLISECONDS.equalsIgnoreCase(temporaryTimestampFormat);
 
-        // Prepare for a pretty date
-        // FIXME Can TIMESTAMP_FORMAT be null ? it does not appear to me .
-        if (!PRINT_MILLISECONDS && !NONE.equalsIgnoreCase(TIMESTAMP_FORMAT) && (TIMESTAMP_FORMAT != null)) {
-            DATE_FORMATTER = new SimpleDateFormat(TIMESTAMP_FORMAT);
+        if (!PRINT_MILLISECONDS && !NONE.equalsIgnoreCase(temporaryTimestampFormat)) {
+            DATE_FORMAT = validateFormat(temporaryTimestampFormat);
         } else {
-            DATE_FORMATTER = null;
+            DATE_FORMAT = null;
         }
 
-        TIMESTAMP = !NONE.equalsIgnoreCase(TIMESTAMP_FORMAT);// reversed compare allows for null
+        TIMESTAMP = !NONE.equalsIgnoreCase(temporaryTimestampFormat);// reversed compare allows for null
 
         SAVE_ASSERTION_RESULTS_FAILURE_MESSAGE = TRUE.equalsIgnoreCase(props.getProperty(
                 ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, TRUE));
 
         String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
         if (NONE.equals(whichAssertionResults)) {
             ASSERTIONS_RESULT_TO_SAVE = SAVE_NO_ASSERTIONS;
         } else if (FIRST.equals(whichAssertionResults)) {
             ASSERTIONS_RESULT_TO_SAVE = SAVE_FIRST_ASSERTION;
         } else if (ALL.equals(whichAssertionResults)) {
             ASSERTIONS_RESULT_TO_SAVE = SAVE_ALL_ASSERTIONS;
         } else {
             ASSERTIONS_RESULT_TO_SAVE = 0;
         }
 
         String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, CSV);
 
         if (XML.equals(howToSave)) {
             IS_XML = true;
         } else {
             if (!CSV.equals(howToSave)) {
                 log.warn("{} has unexepected value: '{}' - assuming 'csv' format", OUTPUT_FORMAT_PROP, howToSave);
             }
             IS_XML = false;
         }
 
         THREAD_COUNTS=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, TRUE));
 
         SAMPLE_COUNT=TRUE.equalsIgnoreCase(props.getProperty(SAVE_SAMPLE_COUNT, FALSE));
 
         IDLE_TIME=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, TRUE));
     }
 
     private static final SampleSaveConfiguration STATIC_SAVE_CONFIGURATION = new SampleSaveConfiguration();
 
+    // for test code only
+    static final String CONFIG_GETTER_PREFIX = "save";  // $NON-NLS-1$
+    
+    // for test code only
+    static final String CONFIG_SETTER_PREFIX = "set";  // $NON-NLS-1$
+
+    /**
+     * List of saveXXX/setXXX(boolean) methods which is used to build the Sample Result Save Configuration dialog.
+     * New method names should be added at the end so that existing layouts are not affected.
+     */
+    // The current order is derived from http://jmeter.apache.org/usermanual/listeners.html#csvlogformat
+    // TODO this may not be the ideal order; fix further and update the screenshot(s)
+    public static final List<String> SAVE_CONFIG_NAMES = Collections.unmodifiableList(Arrays.asList(new String[]{
+        "AsXml",
+        "FieldNames", // CSV
+        "Timestamp",
+        "Time", // elapsed
+        "Label",
+        "Code", // Response Code
+        "Message", // Response Message
+        "ThreadName",
+        "DataType",
+        "Success",
+        "AssertionResultsFailureMessage",
+        "Bytes",
+        "SentBytes",
+        "ThreadCounts", // grpThreads and allThreads
+        "Url",
+        "FileName",
+        "Latency",
+        "ConnectTime",
+        "Encoding",
+        "SampleCount", // Sample and Error Count
+        "Hostname",
+        "IdleTime",
+        "RequestHeaders", // XML
+        "SamplerData", // XML
+        "ResponseHeaders", // XML
+        "ResponseData", // XML
+        "Subresults", // XML
+        "Assertions", // XML
+    }));
     // N.B. Remember to update the equals and hashCode methods when adding new variables.
 
     // Initialise values from properties
     private boolean time = TIME;
     private boolean latency = LATENCY;
     private boolean connectTime=CONNECT_TIME;
     private boolean timestamp = TIMESTAMP;
     private boolean success = SUCCESS;
     private boolean label = LABEL;
     private boolean code = CODE;
     private boolean message = MESSAGE;
     private boolean threadName = THREAD_NAME;
     private boolean dataType = DATATYPE;
     private boolean encoding = ENCODING;
     private boolean assertions = ASSERTIONS;
     private boolean subresults = SUB_RESULTS;
     private boolean responseData = RESPONSE_DATA;
     private boolean samplerData = SAMPLER_DATA;
     private boolean xml = IS_XML;
     private boolean fieldNames = FIELD_NAMES;
     private boolean responseHeaders = RESPONSE_HEADERS;
     private boolean requestHeaders = REQUEST_HEADERS;
     private boolean responseDataOnError = RESPONSE_DATA_ON_ERROR;
 
     private boolean saveAssertionResultsFailureMessage = SAVE_ASSERTION_RESULTS_FAILURE_MESSAGE;
 
     private boolean url = URL;
     private boolean bytes = BYTES;
     private boolean sentBytes = SENT_BYTES;
     private boolean fileName = FILE_NAME;
 
     private boolean hostname = HOST_NAME;
 
     private boolean threadCounts = THREAD_COUNTS;
 
     private boolean sampleCount = SAMPLE_COUNT;
 
     private boolean idleTime = IDLE_TIME;
 
     // Does not appear to be used (yet)
     private int assertionsResultsToSave = ASSERTIONS_RESULT_TO_SAVE;
 
 
     // Don't save this, as it is derived from the time format
     private boolean printMilliseconds = PRINT_MILLISECONDS;
 
-    /** A formatter for the time stamp. */
-    private transient DateFormat formatter = DATE_FORMATTER;
-    /* Make transient as we don't want to save the SimpleDataFormat class
+    private String dateFormat = DATE_FORMAT;
+
+    /** A formatter for the time stamp. 
+     * Make transient as we don't want to save the FastDateFormat class
      * Also, there's currently no way to change the value via the GUI, so changing it
      * later means editting the JMX, or recreating the Listener.
      */
-
+    private transient FastDateFormat threadSafeLenientFormatter =
+        dateFormat != null ? FastDateFormat.getInstance(dateFormat) : null;
+    
     // Don't save this, as not settable via GUI
     private String delimiter = DELIMITER;
 
     // Don't save this - only needed for processing CSV headers currently
     private transient int varCount = 0;
 
-    
     public SampleSaveConfiguration() {
     }
 
     /**
      * Alternate constructor for use by CsvSaveService
      *
      * @param value initial setting for boolean fields used in Config dialogue
      */
     public SampleSaveConfiguration(boolean value) {
         assertions = value;
         bytes = value;
         code = value;
         connectTime = value;
         dataType = value;
         encoding = value;
         fieldNames = value;
         fileName = value;
         hostname = value;
         idleTime = value;
         label = value;
         latency = value;
         message = value;
         printMilliseconds = PRINT_MILLISECONDS;//is derived from properties only
         requestHeaders = value;
         responseData = value;
         responseDataOnError = value;
         responseHeaders = value;
         sampleCount = value;
         samplerData = value;
         saveAssertionResultsFailureMessage = value;
         sentBytes = value;
         subresults = value;
         success = value;
         threadCounts = value;
         threadName = value;
         time = value;
         timestamp = value;
         url = value;
         xml = value;
     }
     
     public int getVarCount() { // Only for use by CSVSaveService
         return varCount;
     }
 
     public void setVarCount(int varCount) { // Only for use by CSVSaveService
         this.varCount = varCount;
     }
 
     // Give access to initial configuration
     public static SampleSaveConfiguration staticConfig() {
         return STATIC_SAVE_CONFIGURATION;
     }
-
-    // for test code only
-    static final String CONFIG_GETTER_PREFIX = "save";  // $NON-NLS-1$
-
+    
     /**
      * Convert a config name to the method name of the getter.
      * The getter method returns a boolean.
      * @param configName the config name
      * @return the getter method name
      */
     public static final String getterName(String configName) {
         return CONFIG_GETTER_PREFIX + configName;
     }
 
-    // for test code only
-    static final String CONFIG_SETTER_PREFIX = "set";  // $NON-NLS-1$
-
     /**
      * Convert a config name to the method name of the setter
      * The setter method requires a boolean parameter.
      * @param configName the config name
      * @return the setter method name
      */
     public static final String setterName(String configName) {
         return CONFIG_SETTER_PREFIX + configName;
     }
 
     /**
-     * List of saveXXX/setXXX(boolean) methods which is used to build the Sample Result Save Configuration dialog.
-     * New method names should be added at the end so that existing layouts are not affected.
+     * Validate pattern
+     * @param temporaryTimestampFormat DateFormat pattern
+     * @return format if ok or null
      */
-    // The current order is derived from http://jmeter.apache.org/usermanual/listeners.html#csvlogformat
-    // TODO this may not be the ideal order; fix further and update the screenshot(s)
-    public static final List<String> SAVE_CONFIG_NAMES = Collections.unmodifiableList(Arrays.asList(new String[]{
-        "AsXml",
-        "FieldNames", // CSV
-        "Timestamp",
-        "Time", // elapsed
-        "Label",
-        "Code", // Response Code
-        "Message", // Response Message
-        "ThreadName",
-        "DataType",
-        "Success",
-        "AssertionResultsFailureMessage",
-        "Bytes",
-        "SentBytes",
-        "ThreadCounts", // grpThreads and allThreads
-        "Url",
-        "FileName",
-        "Latency",
-        "ConnectTime",
-        "Encoding",
-        "SampleCount", // Sample and Error Count
-        "Hostname",
-        "IdleTime",
-        "RequestHeaders", // XML
-        "SamplerData", // XML
-        "ResponseHeaders", // XML
-        "ResponseData", // XML
-        "Subresults", // XML
-        "Assertions", // XML
-    }));
+    private static String validateFormat(String temporaryTimestampFormat) {
+        try {
+            new SimpleDateFormat(temporaryTimestampFormat);
+            if(log.isDebugEnabled()) {
+                log.debug("Successfully validated pattern value {} for property {}", 
+                        temporaryTimestampFormat, TIME_STAMP_FORMAT_PROP);
+            }
+            return temporaryTimestampFormat;
+        } catch(IllegalArgumentException ex) {
+            log.error("Invalid pattern value {} for property {}", temporaryTimestampFormat, TIME_STAMP_FORMAT_PROP);
+            return null;
+        }
+    }
+
 
     private Object readResolve(){
-       formatter = DATE_FORMATTER;
-       return this;
+        setupDateFormat(DATE_FORMAT);
+        return this;
+    }
+
+    /**
+     * Initialize threadSafeLenientFormatter
+     * @param pDateFormat String date format
+     */
+    private void setupDateFormat(String pDateFormat) {
+        this.dateFormat = pDateFormat;
+        if(dateFormat != null) {
+            this.threadSafeLenientFormatter = FastDateFormat.getInstance(dateFormat);
+        } else {
+            this.threadSafeLenientFormatter = null;
+        }
     }
 
     @Override
     public Object clone() {
         try {
             SampleSaveConfiguration clone = (SampleSaveConfiguration)super.clone();
-            if(this.formatter != null) {
-                clone.formatter = (SimpleDateFormat)this.formatter.clone();
+            if(this.dateFormat != null) {
+                clone.threadSafeLenientFormatter = (FastDateFormat)this.threadSafeLenientFormatter.clone();
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
             s.sentBytes == sentBytes &&
             s.fileName == fileName &&
             s.hostname == hostname &&
             s.sampleCount == sampleCount &&
             s.idleTime == idleTime &&
             s.threadCounts == threadCounts;
 
         boolean stringValues = false;
         if(primitiveValues) {
             stringValues = Objects.equals(delimiter, s.delimiter);
         }
         boolean complexValues = false;
         if(primitiveValues && stringValues) {
-            complexValues = Objects.equals(formatter, s.formatter);
+            complexValues = Objects.equals(dateFormat, s.dateFormat);
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
         hash = 31 * hash + (sentBytes ? 1 : 0);
         hash = 31 * hash + (fileName ? 1 : 0);
         hash = 31 * hash + (hostname ? 1 : 0);
         hash = 31 * hash + (threadCounts ? 1 : 0);
         hash = 31 * hash + (delimiter != null  ? delimiter.hashCode() : 0);
-        hash = 31 * hash + (formatter != null  ? formatter.hashCode() : 0);
+        hash = 31 * hash + (dateFormat != null  ? dateFormat.hashCode() : 0);
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
     
     public boolean saveSentBytes() {
         return sentBytes;
     }
 
     public void setSentBytes(boolean save) {
         this.sentBytes = save;
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
 
+    
     /**
      * Intended for use by CsvSaveService (and test cases)
      * @param fmt
      *            format of the date to be saved. If <code>null</code>
      *            milliseconds since epoch will be printed
      */
-    public void setFormatter(DateFormat fmt){
+    public void setDateFormat(String fmt){
         printMilliseconds = fmt == null; // maintain relationship
-        formatter = fmt;
+        setupDateFormat(fmt);
     }
 
     public boolean printMilliseconds() {
         return printMilliseconds;
     }
 
-    public DateFormat formatter() {
-        return formatter;
+    /**
+     * @return {@link DateFormat} non lenient
+     */
+    public DateFormat strictDateFormatter() {
+        if(dateFormat != null) {
+            return new SimpleDateFormat(dateFormat);
+        } else {
+            return null;
+        }
+    }
+    
+    /**
+     * @return {@link FastDateFormat} Thread safe lenient formatter
+     */
+    public FastDateFormat threadSafeLenientFormatter() {
+        return threadSafeLenientFormatter;
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
         delimiter=DELIMITER;
     }
 
     // Used by SampleSaveConfigurationConverter.unmarshall()
     public void setDefaultTimeStampFormat() {
         printMilliseconds=PRINT_MILLISECONDS;
-        formatter=DATE_FORMATTER;
+        setupDateFormat(DATE_FORMAT);
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
diff --git a/src/core/org/apache/jmeter/save/CSVSaveService.java b/src/core/org/apache/jmeter/save/CSVSaveService.java
index 4208aca85..25cc3338e 100644
--- a/src/core/org/apache/jmeter/save/CSVSaveService.java
+++ b/src/core/org/apache/jmeter/save/CSVSaveService.java
@@ -1,1097 +1,1097 @@
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
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.io.StringReader;
 import java.io.Writer;
 import java.nio.charset.StandardCharsets;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
+import java.util.Arrays;
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
 import org.apache.jorphan.reflect.Functor;
 import org.apache.jorphan.util.JMeterError;
 import org.apache.oro.text.regex.Pattern;
 import org.apache.oro.text.regex.PatternMatcherInput;
 import org.apache.oro.text.regex.Perl5Compiler;
 import org.apache.oro.text.regex.Perl5Matcher;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * This class provides a means for saving/reading test results as CSV files.
  */
 // For unit tests, @see TestCSVSaveService
 public final class CSVSaveService {
     private static final Logger log = LoggerFactory.getLogger(CSVSaveService.class);
 
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
     private static final SampleSaveConfiguration _saveConfig = SampleSaveConfiguration
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
         final boolean errorsOnly = resultCollector.isErrorLogging();
         final boolean successOnly = resultCollector.isSuccessOnlyLogging();
         try (InputStream inStream = new FileInputStream(filename);
                 Reader inReader = new InputStreamReader(inStream,
                         SaveService.getFileEncoding(StandardCharsets.UTF_8.name()));
                 BufferedReader dataReader = new BufferedReader(inReader)) {
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
                 log.info("{} does not appear to have a valid header. Using default configuration.", filename);
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
-                        log.warn("Cannot parse timestamp: '{}'", text);
+                        log.warn("Cannot parse timestamp: '{}', will try following formats {}", text,
+                                Arrays.asList(DATE_FORMAT_STRINGS));
                         boolean foundMatch = false;
                         for(String fmt : DATE_FORMAT_STRINGS) {
                             SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
                             dateFormat.setLenient(false);
                             try {
                                 Date stamp = dateFormat.parse(text);
                                 timeStamp = stamp.getTime();
-                                // method is only ever called from one thread at a time
-                                // so it's OK to use a static DateFormat
                                 log.warn("Setting date format to: {}", fmt);
-                                saveConfig.setFormatter(dateFormat);
+                                saveConfig.setDateFormat(fmt);
                                 foundMatch = true;
                                 break;
                             } catch (ParseException pe) {
-                                log.info("{} did not match {}", text, fmt);
+                                log.info("{} did not match {}, trying next date format", text, fmt);
                             }
                         }
                         if (!foundMatch) {
                             throw new ParseException("No date-time format found matching "+text,-1);
                         }
                     }
-                } else if (saveConfig.formatter() != null) {
-                    Date stamp = saveConfig.formatter().parse(text);
+                } else if (saveConfig.strictDateFormatter() != null) {
+                    Date stamp = saveConfig.strictDateFormatter().parse(text);
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
                 log.warn("Line: {}. Found {} fields, expected {}. Extra fields have been ignored.", lineNumber,
                         parts.length, i);
             }
 
         } catch (NumberFormatException | ParseException e) {
             if (log.isWarnEnabled()) {
                 log.warn("Error parsing field '{}' at line {}. {}", field, lineNumber, e.toString());
             }
             throw new JMeterError(e);
         } catch (ArrayIndexOutOfBoundsException e) {
             log.warn("Insufficient columns to parse field '{}' at line {}", field, lineNumber);
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
 
         appendFields(saveConfig.saveTimestamp(), text, delim, TIME_STAMP);
         appendFields(saveConfig.saveTime(), text, delim, CSV_ELAPSED);
         appendFields(saveConfig.saveLabel(), text, delim, LABEL);
         appendFields(saveConfig.saveCode(), text, delim, RESPONSE_CODE);
         appendFields(saveConfig.saveMessage(), text, delim, RESPONSE_MESSAGE);
         appendFields(saveConfig.saveThreadName(), text, delim, THREAD_NAME);
         appendFields(saveConfig.saveDataType(), text, delim, DATA_TYPE);
         appendFields(saveConfig.saveSuccess(), text, delim, SUCCESSFUL);
         appendFields(saveConfig.saveAssertionResultsFailureMessage(), text, delim, FAILURE_MESSAGE);
         appendFields(saveConfig.saveBytes(), text, delim, CSV_BYTES);
         appendFields(saveConfig.saveSentBytes(), text, delim, CSV_SENT_BYTES);
         appendFields(saveConfig.saveThreadCounts(), text, delim, CSV_THREAD_COUNT1, CSV_THREAD_COUNT2);
         appendFields(saveConfig.saveUrl(), text, delim, CSV_URL);
         appendFields(saveConfig.saveFileName(), text, delim, CSV_FILENAME);
         appendFields(saveConfig.saveLatency(), text, delim, CSV_LATENCY);
         appendFields(saveConfig.saveEncoding(), text, delim, CSV_ENCODING);
         appendFields(saveConfig.saveSampleCount(), text, delim, CSV_SAMPLE_COUNT, CSV_ERROR_COUNT);
         appendFields(saveConfig.saveHostname(), text, delim, CSV_HOSTNAME);
         appendFields(saveConfig.saveIdleTime(), text, delim, CSV_IDLETIME);
         appendFields(saveConfig.saveConnectTime(), text, delim, CSV_CONNECT_TIME);
 
         for (int i = 0; i < SampleEvent.getVarCount(); i++) {
             text.append(VARIABLE_NAME_QUOTE_CHAR);
             text.append(SampleEvent.getVarName(i));
             text.append(VARIABLE_NAME_QUOTE_CHAR);
             text.append(delim);
         }
 
         String resultString;
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
 
     private static void appendFields(final boolean condition, StringBuilder textBuffer, String delim, String... fieldNames) {
         if (condition) {
             for (String name: fieldNames) {
                 textBuffer.append(name);
                 textBuffer.append(delim);
             }
         }
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
             if (log.isWarnEnabled()) {
                 log.warn("Default delimiter '{}' did not work; using alternate '{}' for reading {}",
                         _saveConfig.getDelimiter(), delim, filename);
             }
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
                 log.warn("Unknown column name {}", label);
                 return null; // unknown column name
             }
             if (current <= previous) {
                 log.warn("Column header number {} name {} is out of order.", (i + 1), label);
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
      * model. Same as {@link #saveCSVStats(List, Writer, String[])} except
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
      * {@link #saveCSVStats(List, Writer, String[])} except that there is no
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
-            } else if (saveConfig.formatter() != null) {
-                String stamp = saveConfig.formatter().format(
+            } else if (saveConfig.threadSafeLenientFormatter() != null) {
+                String stamp = saveConfig.threadSafeLenientFormatter().format(
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
diff --git a/test/src/org/apache/jmeter/samplers/TestSampleSaveConfiguration.java b/test/src/org/apache/jmeter/samplers/TestSampleSaveConfiguration.java
index 10663a247..00943d704 100644
--- a/test/src/org/apache/jmeter/samplers/TestSampleSaveConfiguration.java
+++ b/test/src/org/apache/jmeter/samplers/TestSampleSaveConfiguration.java
@@ -1,177 +1,186 @@
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
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNotSame;
 import static org.junit.Assert.assertTrue;
 
 import java.lang.reflect.Method;
-import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.apache.jmeter.junit.JMeterTestCase;
 import org.junit.Test;
 
 // Extends JMeterTest case because it needs access to JMeter properties
 public class TestSampleSaveConfiguration extends JMeterTestCase {    
 
     @Test
     public void testClone() throws Exception {
         SampleSaveConfiguration a = new SampleSaveConfiguration();
         a.setUrl(false);
         a.setAssertions(true);
         a.setDefaultDelimiter();
         a.setDefaultTimeStampFormat();
         a.setDataType(true);
         assertFalse(a.saveUrl());
         assertNotNull(a.getDelimiter());
         assertTrue(a.saveAssertions());
         assertTrue(a.saveDataType());
 
         // Original and clone should be equal
         SampleSaveConfiguration cloneA = (SampleSaveConfiguration) a.clone();
         assertNotSame(a, cloneA);
         assertEquals(a, cloneA);
         assertTrue(a.equals(cloneA));
         assertTrue(cloneA.equals(a));
         assertEquals(a.hashCode(), cloneA.hashCode());
         
         // Change the original
         a.setUrl(true);
         assertFalse(a.equals(cloneA));
         assertFalse(cloneA.equals(a));
         assertFalse(a.hashCode() == cloneA.hashCode());
         
         // Change the original back again
         a.setUrl(false);
         assertEquals(a, cloneA);
         assertTrue(a.equals(cloneA));
         assertTrue(cloneA.equals(a));
         assertEquals(a.hashCode(), cloneA.hashCode());        
     }
     
     @Test
     public void testEqualsAndHashCode() throws Exception {
         SampleSaveConfiguration a = new SampleSaveConfiguration();
         a.setUrl(false);
         a.setAssertions(true);
         a.setDefaultDelimiter();
         a.setDefaultTimeStampFormat();
         a.setDataType(true);
         SampleSaveConfiguration b = new SampleSaveConfiguration();
         b.setUrl(false);
         b.setAssertions(true);
         b.setDefaultDelimiter();
         b.setDefaultTimeStampFormat();
         b.setDataType(true);
         
         // a and b should be equal
         assertEquals(a, b);
         assertTrue(a.equals(b));
         assertTrue(b.equals(a));
         assertEquals(a.hashCode(), b.hashCode());
         assertPrimitiveEquals(a.saveUrl(), b.saveUrl());
         assertPrimitiveEquals(a.saveAssertions(), b.saveAssertions());
         assertEquals(a.getDelimiter(), b.getDelimiter());
         assertPrimitiveEquals(a.saveDataType(), b.saveDataType());
         
         a.setAssertions(false);
         // a and b should not be equal
         assertFalse(a.equals(b));
         assertFalse(b.equals(a));
         assertFalse(a.hashCode() == b.hashCode());
         assertFalse(a.saveAssertions() == b.saveAssertions());
     }
 
     @Test
     public void testFalse() throws Exception {
         SampleSaveConfiguration a = new SampleSaveConfiguration(false);
         SampleSaveConfiguration b = new SampleSaveConfiguration(false);
         assertEquals("Hash codes should be equal",a.hashCode(), b.hashCode());
         assertTrue("Objects should be equal",a.equals(b));
         assertTrue("Objects should be equal",b.equals(a));
     }
 
     @Test
     public void testTrue() throws Exception {
         SampleSaveConfiguration a = new SampleSaveConfiguration(true);
         SampleSaveConfiguration b = new SampleSaveConfiguration(true);
         assertEquals("Hash codes should be equal",a.hashCode(), b.hashCode());
         assertTrue("Objects should be equal",a.equals(b));
         assertTrue("Objects should be equal",b.equals(a));
     }
     @Test
     public void testFalseTrue() throws Exception {
         SampleSaveConfiguration a = new SampleSaveConfiguration(false);
         SampleSaveConfiguration b = new SampleSaveConfiguration(true);
         assertFalse("Hash codes should not be equal",a.hashCode() == b.hashCode());
         assertFalse("Objects should not be equal",a.equals(b));
         assertFalse("Objects should not be equal",b.equals(a));
     }
 
     @Test
     public void testFormatter() throws Exception {
         SampleSaveConfiguration a = new SampleSaveConfiguration(false);
         SampleSaveConfiguration b = new SampleSaveConfiguration(false);
         assertEquals("Hash codes should be equal",a.hashCode(), b.hashCode());
         assertTrue("Objects should be equal",a.equals(b));
         assertTrue("Objects should be equal",b.equals(a));
-        a.setFormatter(null);
-        b.setFormatter(null);
+        assertTrue(a.strictDateFormatter() == null);
+        assertTrue(b.strictDateFormatter() == null);
+        assertTrue(a.threadSafeLenientFormatter() == null);
+        assertTrue(b.threadSafeLenientFormatter() == null);
+        a.setDateFormat(null);
+        b.setDateFormat(null);
         assertEquals("Hash codes should be equal",a.hashCode(), b.hashCode());
         assertTrue("Objects should be equal",a.equals(b));
         assertTrue("Objects should be equal",b.equals(a));
-        a.setFormatter(new SimpleDateFormat());
-        b.setFormatter(new SimpleDateFormat());
+        assertTrue(a.strictDateFormatter() == null);
+        assertTrue(b.strictDateFormatter() == null);
+        assertTrue(a.threadSafeLenientFormatter() == null);
+        assertTrue(b.threadSafeLenientFormatter() == null);
+        a.setDateFormat("dd/MM/yyyy");
+        b.setDateFormat("dd/MM/yyyy");
         assertEquals("Hash codes should be equal",a.hashCode(), b.hashCode());
         assertTrue("Objects should be equal",a.equals(b));
         assertTrue("Objects should be equal",b.equals(a));
+        assertTrue("Objects should be equal",a.strictDateFormatter().equals(b.strictDateFormatter()));
+        assertTrue("Objects should be equal",a.threadSafeLenientFormatter().equals(b.threadSafeLenientFormatter()));
     }
 
     @Test
     // Checks that all the saveXX() and setXXX(boolean) methods are in the list
     public void testSaveConfigNames() throws Exception {
         List<String> getMethodNames = new ArrayList<>();
         List<String> setMethodNames = new ArrayList<>();
         Method[] methods = SampleSaveConfiguration.class.getMethods();
         for(Method method : methods) {
             String name = method.getName();
             if (name.startsWith(SampleSaveConfiguration.CONFIG_GETTER_PREFIX) && method.getParameterTypes().length == 0) {
                 name = name.substring(SampleSaveConfiguration.CONFIG_GETTER_PREFIX.length());
                 getMethodNames.add(name);
                 assertTrue("SAVE_CONFIG_NAMES should contain save" + name, SampleSaveConfiguration.SAVE_CONFIG_NAMES.contains(name));
             }
             if (name.startsWith(SampleSaveConfiguration.CONFIG_SETTER_PREFIX) && method.getParameterTypes().length == 1 && boolean.class.equals(method.getParameterTypes()[0])) {
                 name = name.substring(SampleSaveConfiguration.CONFIG_SETTER_PREFIX.length());
                 setMethodNames.add(name);
                 assertTrue("SAVE_CONFIG_NAMES should contain set" + name, SampleSaveConfiguration.SAVE_CONFIG_NAMES.contains(name));
             }
         }
         for (String name : SampleSaveConfiguration.SAVE_CONFIG_NAMES) {
             assertTrue("SAVE_CONFIG_NAMES should NOT contain save" + name, getMethodNames.contains(name));
             assertTrue("SAVE_CONFIG_NAMES should NOT contain set" + name, setMethodNames.contains(name));
         }
     }
 
  }
 
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index b46c04282..4ce15e0f1 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,514 +1,515 @@
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
 <!ENTITY rarr     "&#x02192;" >
 <!ENTITY vellip   "&#x022EE;" >
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
 
 
 <!--  =================== 3.2 =================== -->
 
 <h1>Version 3.2</h1>
 <p>
 Summary
 </p>
 <ul>
 <li><a href="#New and Noteworthy">New and Noteworthy</a></li>
 <li><a href="#Incompatible changes">Incompatible changes</a></li>
 <li><a href="#Bug fixes">Bug fixes</a></li>
 <li><a href="#Improvements">Improvements</a></li>
 <li><a href="#Non-functional changes">Non-functional changes</a></li>
 <li><a href="#Known problems and workarounds">Known problems and workarounds</a></li>
 <li><a href="#Thanks">Thanks</a></li>
 
 </ul>
 
 <ch_section>New and Noteworthy</ch_section>
 
 <!-- <ch_category>Sample category</ch_category> -->
 <!-- <ch_title>Sample title</ch_title> -->
 <!-- <figure width="846" height="613" image="changes/3.0/view_results_tree_search_feature.png"></figure> -->
 
 <ch_section>IMPORTANT CHANGES</ch_section>
 <p>
 JMeter now requires Java 8. Ensure you use the most up to date version.
 </p>
 <p>
 JMeter logging has been migrated to SLF4J and Log4j 2.
 This affects configuration and 3<sup>rd</sup> party plugins.
 </p>
 <ch_title>Core improvements</ch_title>
 <ul>
 <li>JMeter now provides a new BackendListener implementation that interfaces InfluxDB through its HTTP API using Asynchronous HTTP requests.
 <figure width="813" height="407" image="changes/3.2/backend_influxdb.png"></figure>
 </li>
 <li>DNS Cache Manager now has a table to allow static host resolution.
 <figure width="803" height="561" image="changes/3.2/dns_cache_manager_static_hosts.png"></figure>
 </li>
 <li>JMS Publisher and Subscriber now allow reconnection on error with pause.
 <figure width="852" height="738" image="changes/3.2/jms_publisher_reconnect.png"></figure>
 <figure width="716" height="538" image="changes/3.2/jms_subscriber_reconnect_pause.png"></figure>
 </li>
 <li>Variables in JMS Publisher are now supported for all types of messages. Add the encoding type of the file to parse his content</li>
 <figure width="750" height="743" image="changes/3.2/jms_subscriber_content_encoding.png"></figure>
 <li>XPath Extractor now allows extraction randomly, by index or for all matches.
 <figure width="823" height="348" image="changes/3.2/xpath_extractor.png"></figure>
 </li>
 <li>Response Assertion now allows to work on Request Header, provides a "OR" combination and has a better cell renderer
 <figure width="1053" height="329" image="changes/3.2/response_assertion.png"></figure>
 </li>
 <li>HTTP HC4 Implementation now allows preemptive Basic Auth</li>
 <li>Embedded resources download in CSS has been improved to avoid useless repetitive parsing to find the resources</li>
 <li>An important work on code quality and code coverage with tests has been done since Sonar has been setup on the project.
 You can see Sonar report <a href="https://builds.apache.org/analysis/overview?id=12927" >here</a>.
 </li>
 </ul>
 
 <ch_title>UX improvements</ch_title>
 <ul>
 <li>When running a Test, GUI is now more responsive and less impacting on memory usage thanks to a limitation on the number of Sample Results 
 listeners hold and a rework of the way GUI is updated</li>
 <li>HTTP Request GUI has been simplified and provides more place for parameters and body.
 <figure width="848" height="475" image="changes/3.2/http_request.png"></figure>
 </li>
 <li>A <code>replace</code> feature has been added to Search feature to allow replacement in some elements.
 <figure width="459" height="196" image="changes/3.2/search_replace.png"></figure>
 </li>
 <li>View Results Tree now provides a more up to date Browser renderer which requires JavaFX.</li>
 <li>You can now add through a contextual menu think times, this will add think times between samplers and Transaction Controllers
  of selected node.
  <figure width="326" height="430" image="changes/3.2/menu_add_think_times.png"></figure>
  </li>
 <li>You can now apply a naming policy to children of a Transaction Controller. A default policy exists but you can implement your own 
     through <code><a href="./api/org/apache/jmeter/gui/action/TreeNodeNamingPolicy.html" >org.apache.jmeter.gui.action.TreeNodeNamingPolicy</a></code>
     and configuring property <code>naming_policy.impl</code>
 <figure width="327" height="518" image="changes/3.2/menu_apply_naming_policy.png"></figure>    
 </li>
 <li>Sorting per column has been added to View Results in Table, Summary Report, Aggregate Report and Aggregate Graph elements.
 <figure width="1065" height="369" image="changes/3.2/sorting.png"></figure>
 </li>
 </ul>
 
 <ch_title>Documentation improvements</ch_title>
 <ul>
 <li>PDF Documentations have been migrated and updated to HTML user manual</li>
 </ul>
 
 <!-- =================== Incompatible changes =================== -->
 
 <ch_section>Incompatible changes</ch_section>
 
 <ul>
     <li>JMeter requires now at least a Java 8 version to run.</li>
     <li>JMeter logging has been migrated to SLF4J and Log4j 2, this involves changes in the way configuration is done. JMeter now relies on standard
     <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html">Log4j 2 configuration</a> in file <code>log4j2.xml</code>
     See <code>Logging changes</code> section below for further details.
     </li>
     <li>The following jars have been removed after migration from LogKit to SLF4J (see <bugzilla>60589</bugzilla>):
         <ul>
             <li>ApacheJMeter_slf4j_logkit.jar</li>
             <li>avalon-framework-4.1.4.jar</li>
             <li>avalon-framework-4.1.4.jar</li>
             <li>commons-logging-1.2.jar</li>
             <li>excalibur-logger-1.1.jar</li>
             <li>logkit-2.0.jar</li>
         </ul>
     </li>
     <li>The <code>commons-httpclient-3.1.jar</code> has been removed after drop of HC3.1 support(see <bugzilla>60727</bugzilla>)</li>
     <li>JMeter now sets through <code>-Djava.security.egd=file:/dev/urandom</code> the algorithm for secure random</li>
     <li>Process Sampler now returns error code 500 when an error occurs. It previously returned an empty value.</li>
     <li>In <code>org.apache.jmeter.protocol.http.sampler.HTTPHCAbstractImpl</code> two protected static fields (<code>localhost</code> and <code>nonProxyHostSuffixSize</code>) have been renamed to (<code>LOCALHOST</code> and <code>NON_PROXY_HOST_SUFFIX_SIZE</code>) 
         to follow static fields naming convention</li>
     <li>JMeter now uses by default Oracle Nashorn engine instead of Mozilla Rhino for better performances. This should not have an impact unless
     you use some advanced features. You can revert back to Rhino by settings property <code>javascript.use_rhino=true</code>. 
     You can read this <a href="https://wiki.openjdk.java.net/display/Nashorn/Rhino+Migration+Guide">migration guide</a> for more details on Nashorn. See <bugzilla>60672</bugzilla></li>
     <li><bug>60729</bug>The Random Variable Config Element now allows minimum==maximum. Previous versions logged an error when minimum==maximum and did not set the configured variable.</li>
     <li><bug>60730</bug>The JSON PostProcessor now sets the <code>_ALL</code> variable (assuming <code>Compute concatenation var</code> was checked)
     even if the JSON path matches only once. Previous versions did not set the <code>_ALL</code> variable in this case.</li>
 </ul>
 
 <h3>Removed elements or functions</h3>
 <ul>
     <li>SOAP/XML-RPC Request has been removed as part of <bugzilla>60727</bugzilla>. Use HTTP Request element as a replacement. 
     See <a href="./build-ws-test-plan.html" >Building a WebService Test Plan</a></li>
     <li><bug>60423</bug>Drop Monitor Results listener </li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.system.NativeCommand</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.config.gui.MultipartUrlConfigGui</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.testelement.TestListener</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.reporters.FileReporter</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.modifier.UserSequence</code></li>
     <li>Drop deprecated class <code>org.apache.jmeter.protocol.http.parser.HTMLParseError</code></li>
     <li>Drop unused methods <code>org.apache.jmeter.protocol.http.control.HeaderManager#getSOAPHeader</code>
     and <code>org.apache.jmeter.protocol.http.control.HeaderManager#setSOAPHeader(Object)</code>
     </li>
     <li><code>org.apache.jmeter.protocol.http.util.Base64Encode</code> has been deprecated, you can use <code>java.util.Base64</code> as a replacement</li>
 </ul>
 
 <h3>Logging changes</h3>
 <p>
     JMeter logging has been migrated to SLF4J and Log4j 2.
     This affects logging configuration and 3<sup>rd</sup> party plugins (if they use JMeter logging).
     The following sections describe what changes need to be made.
 </p>
 
 <h4>Setting the logging level and log file</h4>
 <p>
     The default logging level can be changed on the command-line using the <code>-L</code> parameter.
     Likewise the <code>-l</code> parameter can be used to change the name of the log file.
     However the <code>log_level</code> properties no longer work.
 </p>
 <p>
     The default logging levels and file name are defined in the <code>log4j2.xml</code> configuration file
     in the launch directory (usually <code>JMETER_HOME/bin</code>)
 </p>
 <p>
     <note>If you need to change the level programmatically from Groovy code or Beanshell, you need to do the following:
     <source>
     import org.apache.logging.log4j.core.config.Configurator;
     ...
     final String loggerName = te.getClass().getName(); // te being a JMeter class
     Configurator.setAllLevels(loggerName, Level.DEBUG); 
     </source>
     </note>
 </p>
 
 <h4>Changes to 3<sup>rd</sup> party plugin logging</h4>
 <p>
     <note>3rd party plugins should migrate their logging code from logkit to slf4j. This is fairly easy and can be done by replacing:
     <source>
         import org.apache.jorphan.logging.LoggingManager;
         import org.apache.log.Logger;
         ...
         private static final Logger log = LoggingManager.getLoggerForClass();
     </source>
     By:
     <source>
         import org.slf4j.Logger;
         import org.slf4j.LoggerFactory;
         ...
         private static final Logger log = LoggerFactory.getLogger(YourClassName.class);
     </source>
     </note>
 </p>
 <!-- =================== Improvements =================== -->
 
 <ch_section>Improvements</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>59934</bug>Fix race-conditions in CssParser. Based on a patch by Jerome Loisel (loisel.jerome at gmail.com)</li>
     <li><bug>60543</bug>HTTP Request / Http Request Defaults UX: Move to advanced panel Timeouts, Implementation, Proxy. Implemented by Philippe Mouawad (p.mouawad at ubik-ingenierie.com) and contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60548</bug>HTTP Request : Allow Upper Panel to be collapsed</li>
     <li><bug>57242</bug>HTTP Authorization is not pre-emptively set with HttpClient4</li>
     <li><bug>60727</bug>Drop commons-httpclient-3.1 and related elements. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60790</bug>HTTP(S) Test Script Recorder : Improve information on certificate expiration and have better UX for Start/Stop</li>
 </ul>
 
 <h3>Other samplers</h3>
 <ul>
     <li><bug>60740</bug>Support variable for all JMS messages (bytes, object, &hellip;) and sources (file, folder), based on <pr>241</pr>. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60585</bug>JMS Publisher and JMS Subscriber : Allow reconnection on error and pause between errors. Based on <pr>240</pr> from by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><pr>259</pr> - Refactored and reformatted SmtpSampler. Contributed by Graham Russell (graham at ham1.co.uk)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>60672</bug>JavaScript function / IfController : use Nashorn engine by default</li>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60144</bug>View Results Tree : Add a more up to date Browser Renderer to replace old Render</li>
     <li><bug>60542</bug>View Results Tree : Allow Upper Panel to be collapsed. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>52962</bug>Allow sorting by columns for View Results in Table, Summary Report, Aggregate Report and Aggregate Graph. Based on a <pr>245</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60590</bug>BackendListener : Add Influxdb BackendListenerClient implementation to JMeter. Partly based on <pr>246</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60591</bug>BackendListener : Add a time boxed sampling. Based on a <pr>237</pr> by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60678</bug>View Results Tree : Text renderer, search should not popup "Text Not Found"</li>
     <li><bug>60691</bug>View Results Tree : In Renderers (XPath, JSON Path Tester, RegExp Tester and CSS/JQuery Tester) lower panel is sometimes not visible as upper panel is too big and cannot be resized</li>
     <li><bug>60687</bug>Make GUI more responsive when it gets a lot of events.</li>
     <li><bug>60791</bug>View Results Tree: Trigger search on Enter key in Search Feature and display red background if no match</li>
     <li><bug>60822</bug>ResultCollector does not ensure unique file name entries in files HashMap</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60154</bug>User Parameters GUI: allow rows to be moved up &amp; down in the list. Contributed by Murdecai777 (https://github.com/Murdecai777).</li>
     <li><bug>60507</bug>Added '<code>Or</code>' Function into ResponseAssertion. Based on a contribution from 忻隆 (298015902 at qq.com)</li>
     <li><bug>58943</bug>Create a Better Think Time experience. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60602</bug>XPath Extractor : Add Match No. to allow extraction randomly, by index or all matches</li>
     <li><bug>60710</bug>XPath Extractor : When content on which assertion applies is not XML, in View Results Tree the extractor is marked in Red and named SAXParseException. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60712</bug>Response Assertion : Improve Renderer of Patterns</li>
     <li><bug>59174</bug>Add a table with static hosts to the DNS Cache Manager. This enables better virtual hosts testing with HttpClient4.</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
 </ul>
 
 <h3>I18N</h3>
 <ul>
     <li>Improve translation "<code>save_as</code>" in French. Based on a <pr>252</pr> by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60785</bug>Improvement of Japanese translation. Patch by Kimono (kimono.outfit.am at gmail.com).</li>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>60637</bug>Improve Statistics table design <figure image="dashboard/report_statistics.png" ></figure></li>
 </ul>
 
 <h3>General</h3>
 <ul>
     <li><bug>58164</bug>Check if file already exists on ResultCollector listener before starting the loadtest</li>
     <li><bug>54525</bug>Search Feature : Enhance it with ability to replace</li>
     <li><bug>60530</bug>Add API to create JMeter threads while test is running. Based on a contribution by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60514</bug>Ability to apply a naming convention on Children of a Transaction Controller. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60711</bug>Improve Delete button behaviour for Assertions / Header Manager / User Parameters GUIs / Exclude, Include in HTTP(S) Test Script Recorder</li>
     <li><bug>60593</bug>Switch to G1 GC algorithm</li>
     <li><bug>60595</bug>Add a SplashScreen at the start of JMeter GUI. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>55258</bug>Drop "Close" icon from toolbar and add "New" to menu. Partly based on contribution from Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
     <li><bug>59995</bug>Allow user to change font size with two new menu items and use <code>jmeter.hidpi.scale.factor</code> for scaling fonts. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60654</bug>Validation Feature : Be able to ignore BackendListener. Contributed by Maxime Chassagneux (maxime.chassagneux at gmail.com).</li>
     <li><bug>60646</bug>Workbench : Save it by default</li>
     <li><bug>60684</bug>Thread Group: Validate ended prematurely by Scheduler with 0 or very short duration. Contributed by Andrew Burton (andrewburtonatwh at gmail.com).</li>
     <li><bug>60589</bug>Migrate LogKit to SLF4J - Drop Avalon, LogKit and Excalibur with backward compatibility for 3<sup>rd</sup> party modules. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60565</bug>Migrate LogKit to SLF4J - Optimize logging statements. e.g, message format args, throwable args, unnecessary if-enabled-logging in simple ones, etc. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60564</bug>Migrate LogKit to SLF4J - Replace LogKit loggers with SLF4J ones and keep the current LogKit binding solution for backward compatibility with plugins. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><bug>60664</bug>Add a UI menu to set log level. Contributed by Woonsan Ko (woonsan at apache.org)</li>
     <li><pr>276</pr> - Added some translations for polish locale. Contributed by Bartosz Siewniak (barteksiewniak at gmail.com)</li>
     <li><bug>60792</bug>Create a new Help menu item to create a thread dump</li>
     <li><bug>60813</bug>JSR223 Test element : Take into account JMeterStopTestNowException, JMeterStopTestException and JMeterStopThreadException</li>
     <li><bug>60814</bug>Menu : Add <code>Open Recent</code> menu item to make recent files loading more obvious</li>
     <li><bug>60815</bug>Drop "Reset GUI" from menu</li>
 </ul>
 
 <ch_section>Non-functional changes</ch_section>
 <ul>
     <li><bug>60415</bug>Drop support for Java 7.</li>
     <li>Updated to dnsjava-2.1.8.jar (from 2.1.7)</li>
     <li>Updated to groovy 2.4.9 (from 2.4.7)</li>
     <li>Updated to httpcore 4.4.6 (from 4.4.5)</li>
     <li>Updated to httpclient 4.5.3 (from 4.5.2)</li>
     <li>Updated to jodd 3.8.1 (from 3.7.1.jar)</li>
     <li>Updated to jsoup-1.10.2 (from 1.10.1)</li>
     <li>Updated to ph-css 5.0.3 (from 4.1.6)</li>
     <li>Updated to ph-commons 8.6.0 (from 6.2.4)</li>
     <li>Updated to slf4j-api 1.7.24 (from 1.7.21)</li>
     <li>Updated to asm 5.2 (from 5.1)</li>
     <li>Updated to rsyntaxtextarea-2.6.1 (from 2.6.0)</li>
     <li>Updated to commons-net-3.6 (from 3.5)</li>
     <li>Converted the old pdf tutorials to xml.</li>
     <li><pr>255</pr> - Utilised Java 8 (and 7) features to tidy up code. Contributed by Graham Russell (graham at ham1.co.uk)</li>
     <li><bug>59435</bug>JMeterTestCase no longer supports JUnit3</li>
 </ul>
 
  <!-- =================== Bug fixes =================== -->
 
 <ch_section>Bug fixes</ch_section>
 
 <h3>HTTP Samplers and Test Script Recorder</h3>
 <ul>
     <li><bug>60531</bug>HTTP Cookie Manager : changing Implementation does not update Cookie Policy</li>
     <li><bug>60575</bug>HTTP GET Requests could have a content-type header without a body.</li>
     <li><bug>60682</bug>HTTP Request : Get method may fail on redirect due to Content-Length header being set</li>
     <li><bug>60643</bug>HTTP(S) Test Script Recorder doesn't correctly handle restart or start after stop. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60652</bug>HTTP PUT Requests might leak file descriptors.</li>
     <li><bug>60689</bug><code>httpclient4.validate_after_inactivity</code> has no impact leading to usage of potentially stale/closed connections</li>
     <li><bug>60690</bug>Default values for "httpclient4.validate_after_inactivity" and "httpclient4.time_to_live" which are equal to each other makes validation useless</li>
     <li><bug>60758</bug>HTTP(s) Test Script Recorder : Number request may generate duplicate numbers. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>56939</bug>Parameters are not passed with OPTIONS HTTP Request</li>
     <li><bug>60778</bug>Http Java Impl does not show Authorization header in SampleResult even if it is sent</li>
     <li><bug>60837</bug>GET with body, PUT are not retried even if <code>httpclient4.retrycount</code> is higher than 0</li>
     <li><bug>60842</bug>Trim extracted URLs when loading embedded resources using the Lagarto based HTML Parser.</li>
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>603982</bug>Guard Exception handler of the <code>JDBCSampler</code> against null messages</li>
     <li><bug>55652</bug>JavaSampler silently resets classname if class can not be found</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
 </ul>
 
 <h3>Listeners</h3>
 <ul>
     <li><bug>60648</bug>GraphiteBackendListener can lose some metrics at end of test if test is very short</li>
     <li><bug>60650</bug>AbstractBackendListenerClient does not reset UserMetric between runs</li>
     <li><bug>60759</bug>View Results Tree : Search feature does not search in URL. Contributed by Ubik Load Pack (support at ubikloadpack.com)</li>
     <li><bug>60859</bug>Save Responses to a file : 2 elements with different configuration will overlap</li>
 </ul>
 
 <h3>Timers, Assertions, Config, Pre- &amp; Post-Processors</h3>
 <ul>
     <li><bug>60438</bug><pr>235</pr> - Clear old variables before extracting new ones in JSON Extractor.
     Based on a patch by Qi Chen (qi.chensh at ele.me)</li>
     <li><bug>60607</bug>DNS Cache Manager configuration is ignored</li>
     <li><bug>60729</bug>The Random Variable Config Element should allow minimum==maximum</li>
     <li><bug>60730</bug>The JSON PostProcessor should set the <code>_ALL</code> variable even if the JSON path matches only once.</li>
     <li><bug>60747</bug>Response Assertion : Add Request Headers to <code>Field to Test</code></li>
     <li><bug>60763</bug>XMLAssertion should not leak errors to console</li>
     <li><bug>60797</bug>TestAction in pause mode can last beyond configured duration of test</li>
 </ul>
 
 <h3>Functions</h3>
 <ul>
     <li><bug>60819</bug>Function __fileToString does not honor the documentation contract when file is not found</li>
 </ul>
 
 <h3>I18N</h3>
 <ul>
 </ul>
 
 <h3>Report / Dashboard</h3>
 <ul>
     <li><bug>60726</bug>Report / Dashboard : Top 5 errors by samplers must not take into account the series filtering</li>
 </ul>
     
 <h3>General</h3>
 <ul>
     <li><bug>60775</bug>NamePanel ctor calls overrideable method</li>
     <li><bug>60428</bug>JMeter Graphite Backend Listener throws exception when test ends
     and <code>useRegexpForSamplersList</code> is set to <code>true</code>.
     Based on patch by Liu XP (liu_xp2003 at sina.com)</li>
     <li><bug>60442</bug>Fix a typo in <code>build.xml</code> (gavin at 16degrees.com.au)</li>
     <li><bug>60449</bug>JMeter Tree : Annoying behaviour when node name is empty</li>
     <li><bug>60494</bug>Add sonar analysis task to build</li>
     <li><bug>60501</bug>Search Feature : Performance issue when regexp is checked</li>
     <li><bug>60444</bug>Intermittent failure of <code>TestHTTPMirrorThread#testSleep()</code>. Contributed by Thomas Schapitz (ts-nospam12 at online.de)</li>
     <li><bug>60621</bug>The "<code>report-template</code>" folder is missing from <code>ApacheJMeter_config-3.1.jar</code> in maven central</li>
     <li><bug>60744</bug>GUI elements are not cleaned up when reused during load of Test Plan which can lead them to be partially initialized with a previous state for a new Test Element</li>
     <li><bug>60812</bug>JMeterThread does not honor contract of JMeterStopTestNowException</li>
     <li><bug>60857</bug>SaveService omits XML header if _file_encoding is not defined in saveservice.properties</li>
+    <li><bug>60830</bug>Timestamps in CSV file could be corrupted due to sharing a SimpleDateFormatter across threads</li>
 </ul>
 
  <!--  =================== Thanks =================== -->
 
 <ch_section>Thanks</ch_section>
 <p>We thank all contributors mentioned in bug and improvement sections above:
 </p>
 <ul>
 <li>Jerome Loisel (loisel.jerome at gmail.com)</li>
 <li>Liu XP (liu_xp2003 at sina.com)</li>
 <li>Qi Chen (qi.chensh at ele.me)</li>
 <li>(gavin at 16degrees.com.au)</li>
 <li>Thomas Schapitz (ts-nospam12 at online.de)</li>
 <li>Murdecai777 (https://github.com/Murdecai777)</li>
 <li>Logan Mauzaize (logan.mauzaize at gmail.com)</li>
 <li>Maxime Chassagneux (maxime.chassagneux at gmail.com)</li>
 <li>忻隆 (298015902 at qq.com)</li>
 <li><a href="http://ubikloadpack.com">Ubik Load Pack</a></li>
 <li>Graham Russell (graham at ham1.co.uk)</li>
 <li>Sanduni Kanishka (https://github.com/SanduniKanishka)</li>
 <li>Andrew Burton (andrewburtonatwh at gmail.com)</li>
 <li>Woonsan Ko (woonsan at apache.org)</li>
 <li>Bartosz Siewniak (barteksiewniak at gmail.com)</li>
 <li>Kimono (kimono.outfit.am at gmail.com)</li>
 </ul>
 <p>We also thank bug reporters who helped us improve JMeter. <br/>
 For this release we want to give special thanks to the following reporters for the clear reports and tests made after our fixes:</p>
 <ul>
 <li>Tuukka Mustonen (tuukka.mustonen at gmail.com) who gave us a lot of useful feedback which helped resolve <bugzilla>60689</bugzilla> and <bugzilla>60690</bugzilla></li>
 <li>Amar Darisa (amar.darisa at gmail.com) who helped us with his feedback on <bugzilla>60682</bugzilla></li>
 </ul>
 <p>
 Apologies if we have omitted anyone else.
  </p>
  <!--  =================== Known bugs or issues related to JAVA Bugs =================== -->
 
 <ch_section>Known problems and workarounds</ch_section>
 <ul>
 <li>View Results Tree may freeze rendering large response particularly if this response has no spaces, see <bugzilla>60816</bugzilla>.
 This is due to an identified Java Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8172336">UI stuck when calling JEditorPane.setText() or JTextArea.setText() with long text without space</a>.
 </li>
 </ul>
 <ul>
 <li>The Once Only controller behaves correctly under a Thread Group or Loop Controller,
 but otherwise its behaviour is not consistent (or clearly specified).</li>
 
 <li>
 The numbers that appear to the left of the green box are the number of active threads / total number of threads,
 the total number of threads only applies to a locally run test, otherwise it will show <code>0</code> (see <bugzilla>55510</bugzilla>).
 </li>
 
 <li>
 Note that under some windows systems you may have this WARNING:
 <source>
 java.util.prefs.WindowsPreferences
 WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0
 x80000002. Windows RegCreateKeyEx(&hellip;) returned error code 5.
 </source>
 The fix is to run JMeter as Administrator, it will create the registry key for you, then you can restart JMeter as a normal user and you won't have the warning anymore.
 </li>
 
 <li>
 You may encounter the following error:
 <source>java.security.cert.CertificateException: Certificates does not conform to algorithm constraints</source>
  if you run a HTTPS request on a web site with a SSL certificate (itself or one of SSL certificates in its chain of trust) with a signature
  algorithm using MD2 (like md2WithRSAEncryption) or with a SSL certificate with a size lower than 1024 bits.
 This error is related to increased security in Java 7 version u16 (MD2) and version u40 (Certificate size lower than 1024 bits), and Java 8 too.
 <br></br>
 To allow you to perform your HTTPS request, you can downgrade the security of your Java installation by editing
 the Java <code>jdk.certpath.disabledAlgorithms</code> property. Remove the MD2 value or the constraint on size, depending on your case.
 <br></br>
 This property is in this file:
 <source>JAVA_HOME/jre/lib/security/java.security</source>
 See  <bugzilla>56357</bugzilla> for details.
 </li>
 
 <li>
 Under Mac OSX Aggregate Graph will show wrong values due to mirroring effect on numbers.
 This is due to a known Java bug, see Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8065373" >JDK-8065373</a>
 The fix is to use JDK8_u45 or later.
 </li>
 
 <li>
 View Results Tree may fail to display some HTML code under HTML renderer, see <bugzilla>54586</bugzilla>.
 This is due to a known Java bug which fails to parse "<code>px</code>" units in row/col attributes.
 See Bug <a href="https://bugs.openjdk.java.net/browse/JDK-8031109" >JDK-8031109</a>
 The fix is to use JDK9 b65 or later (but be aware that Java 9 is not certified yet for JMeter).
 </li>
 
 <li>
 JTable selection with keyboard (<keycombo><keysym>SHIFT</keysym><keysym>up/down</keysym></keycombo>) is totally unusable with JAVA 7 on Mac OSX.
 This is due to a known Java bug <a href="https://bugs.openjdk.java.net/browse/JDK-8025126" >JDK-8025126</a>
 The fix is to use JDK 8 b132 or later.
 </li>
 </ul>
 
 </section>
 </body>
 </document>
