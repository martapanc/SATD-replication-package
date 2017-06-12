diff --git a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
index 927e140f5..38e517d67 100644
--- a/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
+++ b/src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
@@ -1,855 +1,855 @@
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
 
-        _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));
+        _fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, TRUE));
 
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
      * Only intended for use by OldSaveService (and test cases)
      * 
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
diff --git a/xdocs/changes.xml b/xdocs/changes.xml
index bc030a118..853c802c7 100644
--- a/xdocs/changes.xml
+++ b/xdocs/changes.xml
@@ -1,413 +1,415 @@
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
-    <li>Since version 3.0, jmeter.save.saveservice.assertion_results_failure_message property value is true, meaning CSV file for results will contain an additional column containing assertion result response message, see <bugzilla>58978</bugzilla></li>
+    <li>Since version 3.0, <code>jmeter.save.saveservice.assertion_results_failure_message</code> property value is true, meaning CSV file for results will contain an additional column containing assertion result response message, see <bugzilla>58978</bugzilla></li>
+    <li>Since version 3.0, <code>jmeter.save.saveservice.print_field_names</code> property value is true, meaning CSV file for results will contain field names as first line in CSV, see <bugzilla>58991</bugzilla></li>
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
 <li><bug>58601</bug>Change check for modification of <code>saveservice.properties</code> from <code>$Revision$</code> to sha1 sum of the file itself.</li>
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
 <li><bug>58968</bug>Add a new template to allow to record script with think time included. Contributed by Antonio Gomes Rodrigues</li>
 <li><bug>58978</bug>Settings defaults : Switch "jmeter.save.saveservice.assertion_results_failure_message" to true (after 2.13)</li>
+<li><bug>58991</bug>Settings defaults : Switch "jmeter.save.saveservice.print_field_names" to true (after 2.13)</li>
 </ul>
 <ch_section>Non-functional changes</ch_section>
 <ul>
 <li>Updated to tika-core and tika-parsers 1.11 (from 1.7)</li>
 <li>Updated to commons-math3 3.5 (from 3.4.1)</li>
 <li>Updated to commons-pool2 2.4.2 (from 2.3)</li>
 <li>Updated to commons-lang 3.4 (from 3.3.2)</li>
 <li>Updated to rhino-1.7.7 (from 1.7R5)</li>
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
 </ul>
 
 <h3>Other Samplers</h3>
 <ul>
     <li><bug>58013</bug>Enable all protocols that are enabled on the default SSLContext for usage with the SMTP Sampler.</li>
     <li><bug>58209</bug>JMeter hang when testing javasampler because HashMap.put() is called from multiple threads without sync.</li>
     <li><bug>58301</bug>Use typed methods such as setInt, setDouble, setDate ... for prepared statement #27</li>
     <li><bug>58851</bug>Add a dependency to hamcrest-core to allow JUnit tests with annotations to work</li>
     <li><bug>58947</bug>Connect metric is wrong when ConnectException occurs</li>
     <li><bug>58980</bug>JMS Subscriber will return successful as long as 1 message is received. Contributed by Harrison Termotto (harrison dot termotto at stonybrook.edu)</li>
 </ul>
 
 <h3>Controllers</h3>
 <ul>
     <li><bug>58600</bug>Display correct filenames, when they are searched by IncludeController</li>
     <li><bug>58678</bug>Module Controller : limit target element selection. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
     <li><bug>58714</bug>Module controller : it should not be possible to add a timer as child. Contributed by Benoit Wiart (benoit dot wiart at gmail.com)</li>
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
