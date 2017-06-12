diff --git a/src/core/org/apache/jmeter/samplers/SampleListener.java b/src/core/org/apache/jmeter/samplers/SampleListener.java
index 4033d5588..01b263a8d 100644
--- a/src/core/org/apache/jmeter/samplers/SampleListener.java
+++ b/src/core/org/apache/jmeter/samplers/SampleListener.java
@@ -1,43 +1,52 @@
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
 
 /**
  * Allows notification on events occurring during the sampling process.
  * Specifically, when sampling is started, when a specific sample is obtained,
  * and when sampling is stopped.
  *
  * @version $Revision$
  */
 public interface SampleListener {
     /**
      * A sample has started and stopped.
+     * 
+     * @param e
+     *            the {@link SampleEvent} that has occurred
      */
     void sampleOccurred(SampleEvent e);
 
     /**
      * A sample has started.
+     * 
+     * @param e
+     *            the {@link SampleEvent} that has started
      */
     void sampleStarted(SampleEvent e);
 
     /**
      * A sample has stopped.
+     * 
+     * @param e
+     *            the {@link SampleEvent} that has stopped
      */
     void sampleStopped(SampleEvent e);
 }
diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index 24f5bf160..0d6ee22db 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,837 +1,842 @@
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
- * For each field XXX
- * - methods have the signature "boolean saveXXX()"
- * - a corresponding "void setXXX(boolean)" method
- * - messages.properties contains the key save_XXX
- *
- *
+ * <p>
+ * For each field <em>XXX</em>
+ * <ul>
+ *  <li>methods have the signature "boolean save<em>XXX</em>()"</li>
+ *  <li>a corresponding "void set<em>XXX</em>(boolean)" method</li>
+ *  <li>messages.properties contains the key save_<em>XXX</em></li>
+ * </ul>
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
     private static final String ASSERTION_RESULTS_FAILURE_MESSAGE_PROP =
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
     private boolean time = _time, latency = _latency, timestamp = _timestamp, success = _success, label = _label,
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
             _responseData, _dataType, _encoding, _assertions, _latency, _subresults, _samplerData, _fieldNames,
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
 
         _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));
 
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
                 ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, FALSE));
 
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
      * Alternate constructor for use by OldSaveService
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
      * Only intended for use by OldSaveService (and test cases)
+     * 
+     * @param fmt
+     *            format of the date to be saved. If <code>null</code>
+     *            milliseconds since epoch will be printed
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
diff --git a/src/core/org/apache/jmeter/samplers/Sampler.java b/src/core/org/apache/jmeter/samplers/Sampler.java
index 0b740ddcf..99a425d03 100644
--- a/src/core/org/apache/jmeter/samplers/Sampler.java
+++ b/src/core/org/apache/jmeter/samplers/Sampler.java
@@ -1,37 +1,41 @@
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
 
 import org.apache.jmeter.testelement.TestElement;
 
 /**
  * Classes which are able to generate information about an entry should
  * implement this interface.
  *
  * @version $Revision$
  */
 public interface Sampler extends Serializable, TestElement {
     /**
      * Obtains statistics about the given Entry, and packages the information
      * into a SampleResult.
+     * 
+     * @param e
+     *            the Entry (TODO seems to be unused)
+     * @return information about the sample
      */
     SampleResult sample(Entry e);
 }
diff --git a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
index 68ac3094a..29ee2bbf9 100644
--- a/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
+++ b/src/core/org/apache/jmeter/samplers/StatisticalSampleResult.java
@@ -1,148 +1,148 @@
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
      * Allow OldSaveService to generate a suitable result when sample/error counts have been saved.
      *
      * @deprecated Needs to be replaced when multiple sample results are sorted out
      *
-     * @param stamp
-     * @param elapsed
+     * @param stamp this may be a start time or an end time (both in milliseconds)
+     * @param elapsed time in milliseconds
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
diff --git a/src/core/org/apache/jmeter/save/SaveGraphicsService.java b/src/core/org/apache/jmeter/save/SaveGraphicsService.java
index ee4af1ed4..392f8c7d2 100644
--- a/src/core/org/apache/jmeter/save/SaveGraphicsService.java
+++ b/src/core/org/apache/jmeter/save/SaveGraphicsService.java
@@ -1,191 +1,199 @@
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
 
 import java.awt.Dimension;
 import java.awt.Graphics2D;
 import java.awt.image.BufferedImage;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.OutputStream;
 
 import javax.swing.JComponent;
 
 import org.apache.xmlgraphics.image.codec.png.PNGEncodeParam;
 import org.apache.xmlgraphics.image.codec.png.PNGImageEncoder;
 import org.apache.xmlgraphics.image.codec.tiff.TIFFEncodeParam;
 import org.apache.xmlgraphics.image.codec.tiff.TIFFImageEncoder;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.util.JOrphanUtils;
 
 /**
  * Class is responsible for taking a component and saving it as a JPEG, PNG or
  * TIFF. The class is very simple. Thanks to Batik and the developers who worked
  * so hard on it.
  */
 public class SaveGraphicsService {
 
     public static final int PNG = 0;
 
     public static final int TIFF = 1;
 
     public static final String PNG_EXTENSION = ".png"; //$NON-NLS-1$
 
     public static final String TIFF_EXTENSION = ".tif"; //$NON-NLS-1$
 
     public static final String JPEG_EXTENSION = ".jpg"; //$NON-NLS-1$
 
     /**
      *
      */
     public SaveGraphicsService() {
         super();
     }
 
 /*
  * This is not currently used by JMeter code.
  * As it uses Sun-specific code (the only such in JMeter), it has been commented out for now.
  */
 //  /**
 //   * If someone wants to save a JPEG, use this method. There is a limitation
 //   * though. It uses gray scale instead of color due to artifacts with color
 //   * encoding. For some reason, it does not translate pure red and orange
 //   * correctly. To make the text readable, gray scale is used.
 //   *
 //   * @param filename
 //   * @param component
 //   */
 //  public void saveUsingJPEGEncoder(String filename, JComponent component) {
 //      Dimension size = component.getSize();
 //      // We use Gray scale, since color produces poor quality
 //      // this is an unfortunate result of the default codec
 //      // implementation.
 //      BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_USHORT_GRAY);
 //      Graphics2D grp = image.createGraphics();
 //      component.paint(grp);
 //
 //      File outfile = new File(filename + JPEG_EXTENSION);
 //      FileOutputStream fos = createFile(outfile);
 //      JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
 //      Float q = new Float(1.0);
 //      param.setQuality(q.floatValue(), true);
 //      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos, param);
 //
 //      try {
 //          encoder.encode(image);
 //      } catch (Exception e) {
 //          log.warn(e.toString());
 //      } finally {
 //            JOrphanUtils.closeQuietly(fos);
 //      }
 //  }
 
     /**
      * Method will save the JComponent as an image. The formats are PNG, and
      * TIFF.
      *
      * @param filename
+     *            name of the file to store the image into
      * @param type
+     *            of the image to be stored. Can be one of {@value #PNG} for PNG
+     *            or {@value #TIFF} for TIFF
      * @param component
+     *            to draw the image on
      */
     public void saveJComponent(String filename, int type, JComponent component) {
         Dimension size = component.getSize();
         BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_BYTE_INDEXED);
         Graphics2D grp = image.createGraphics();
         component.paint(grp);
 
         if (type == PNG) {
             filename += PNG_EXTENSION;
             this.savePNGWithBatik(filename, image);
         } else if (type == TIFF) {
             filename = filename + TIFF_EXTENSION;
             this.saveTIFFWithBatik(filename, image);
         }
     }
 
     /**
      * Use Batik to save a PNG of the graph
      *
      * @param filename
+     *            name of the file to store the image into
      * @param image
+     *            to be stored
      */
     public void savePNGWithBatik(String filename, BufferedImage image) {
         File outfile = new File(filename);
         OutputStream fos = createFile(outfile);
         if (fos == null) {
             return;
         }
         PNGEncodeParam param = PNGEncodeParam.getDefaultEncodeParam(image);
         PNGImageEncoder encoder = new PNGImageEncoder(fos, param);
         try {
             encoder.encode(image);
         } catch (IOException e) {
             JMeterUtils.reportErrorToUser("PNGImageEncoder reported: "+e.getMessage(), "Problem creating image file");
         } finally {
             JOrphanUtils.closeQuietly(fos);
         }
     }
 
     /**
      * Use Batik to save a TIFF file of the graph
      *
      * @param filename
+     *            name of the file to store the image into
      * @param image
+     *            to be stored
      */
     public void saveTIFFWithBatik(String filename, BufferedImage image) {
         File outfile = new File(filename);
         OutputStream fos = createFile(outfile);
         if (fos == null) {
             return;
         }
         TIFFEncodeParam param = new TIFFEncodeParam();
         TIFFImageEncoder encoder = new TIFFImageEncoder(fos, param);
         try {
             encoder.encode(image);
         } catch (IOException e) {
             JMeterUtils.reportErrorToUser("TIFFImageEncoder reported: "+e.getMessage(), "Problem creating image file");
         // Yuck: TIFFImageEncoder uses Error to report runtime problems
         } catch (Error e) {
             JMeterUtils.reportErrorToUser("TIFFImageEncoder reported: "+e.getMessage(), "Problem creating image file");
             if (e.getClass() != Error.class){// rethrow other errors
                 throw e;
             }
         } finally {
             JOrphanUtils.closeQuietly(fos);
         }
     }
 
     /**
      * Create a new file for the graphics. Since the method creates a new file,
      * we shouldn't get a FNFE.
      *
      * @param filename
      * @return output stream created from the filename
      */
     private FileOutputStream createFile(File filename) {
         try {
             return new FileOutputStream(filename);
         } catch (FileNotFoundException e) {
             JMeterUtils.reportErrorToUser("Could not create file: "+e.getMessage(), "Problem creating image file");
             return null;
         }
     }
 
 }
diff --git a/src/core/org/apache/jmeter/save/ScriptWrapperConverter.java b/src/core/org/apache/jmeter/save/ScriptWrapperConverter.java
index 4bd2e1bac..6a0061c72 100644
--- a/src/core/org/apache/jmeter/save/ScriptWrapperConverter.java
+++ b/src/core/org/apache/jmeter/save/ScriptWrapperConverter.java
@@ -1,127 +1,129 @@
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
 
 import org.apache.jmeter.save.converters.ConversionHelp;
 import org.apache.jmeter.util.JMeterUtils;
 import org.apache.jorphan.collections.HashTree;
 
 import com.thoughtworks.xstream.mapper.Mapper;
 import com.thoughtworks.xstream.converters.ConversionException;
 import com.thoughtworks.xstream.converters.Converter;
 import com.thoughtworks.xstream.converters.MarshallingContext;
 import com.thoughtworks.xstream.converters.UnmarshallingContext;
 import com.thoughtworks.xstream.io.HierarchicalStreamReader;
 import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 
 /**
  * Handles XStream conversion of Test Scripts
  *
  */
 public class ScriptWrapperConverter implements Converter {
 
     private static final String ATT_PROPERTIES = "properties"; // $NON-NLS-1$
     private static final String ATT_VERSION = "version"; // $NON-NLS-1$
     private static final String ATT_JMETER = "jmeter"; // $NON-NLS-1$
 
     /**
      * Returns the converter version; used to check for possible
      * incompatibilities
+     * 
+     * @return the version of the converter
      */
     public static String getVersion() {
         return "$Revision$"; // $NON-NLS-1$
     }
 
     private final Mapper classMapper;
 
     public ScriptWrapperConverter(Mapper classMapper) {
         this.classMapper = classMapper;
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public boolean canConvert(@SuppressWarnings("rawtypes") Class arg0) { // superclass is not typed
         return arg0.equals(ScriptWrapper.class);
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext context) {
         ScriptWrapper wrap = (ScriptWrapper) arg0;
         String version = SaveService.getVERSION();
         ConversionHelp.setOutVersion(version);// Ensure output follows version
         writer.addAttribute(ATT_VERSION, version);
         writer.addAttribute(ATT_PROPERTIES, SaveService.getPropertiesVersion());
         writer.addAttribute(ATT_JMETER, JMeterUtils.getJMeterVersion());
         writer.startNode(classMapper.serializedClass(wrap.testPlan.getClass()));
         context.convertAnother(wrap.testPlan);
         writer.endNode();
     }
 
     /**
      * {@inheritDoc}
      */
     @Override
     public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
         ScriptWrapper wrap = new ScriptWrapper();
         wrap.version = reader.getAttribute(ATT_VERSION);
         ConversionHelp.setInVersion(wrap.version);// Make sure decoding
                                                     // follows input file
         reader.moveDown();
         // Catch errors and rethrow as ConversionException so we get location details
         try {
             wrap.testPlan = (HashTree) context.convertAnother(wrap, getNextType(reader));
         } catch (NoClassDefFoundError e) {
             throw createConversionException(e);
         } catch (Exception e) {
             throw createConversionException(e);
         }
         return wrap;
     }
 
     private ConversionException createConversionException(Throwable e) {
         final ConversionException conversionException = new ConversionException(e);
         StackTraceElement[] ste = e.getStackTrace();
         if (ste!=null){
             for(StackTraceElement top : ste){
                 String className=top.getClassName();
                 if (className.startsWith("org.apache.jmeter.")){
                     conversionException.add("first-jmeter-class", top.toString());
                     break;
                 }
             }
         }
         return conversionException;
     }
 
     protected Class<?> getNextType(HierarchicalStreamReader reader) {
         String classAttribute = reader.getAttribute(ConversionHelp.ATT_CLASS);
         Class<?> type;
         if (classAttribute == null) {
             type = classMapper.realClass(reader.getNodeName());
         } else {
             type = classMapper.realClass(classAttribute);
         }
         return type;
     }
 }
