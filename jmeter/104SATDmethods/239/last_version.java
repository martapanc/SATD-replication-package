/*
 * Copyright 2004-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
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
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;

/**
 * @author mstover
 * 
 */
public class SampleSaveConfiguration implements Cloneable, Serializable {
	static final long serialVersionUID = 2;

	// ---------------------------------------------------------------------
	// PROPERTY FILE CONSTANTS
	// ---------------------------------------------------------------------

	/** Indicates that the results file should be in XML format. * */
	private static final String XML = "xml"; // $NON_NLS-1$

	/** Indicates that the results file should be in CSV format. * */
	private static final String CSV = "csv"; // $NON_NLS-1$

	/** Indicates that the results should be stored in a database. * */
	private static final String DATABASE = "db"; // $NON_NLS-1$

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

	/***************************************************************************
	 * The name of the property indicating whether the time should be saved.
	 **************************************************************************/
	private static final String SAVE_TIME_PROP = "jmeter.save.saveservice.time"; // $NON_NLS-1$

	/***************************************************************************
	 * The name of the property indicating whether the time stamp should be
	 * saved.
	 **************************************************************************/
	private static final String TIME_STAMP_FORMAT_PROP = "jmeter.save.saveservice.timestamp_format"; // $NON_NLS-1$

    private static final String SUBRESULTS_PROP      = "jmeter.save.saveservice.subresults"; // $NON_NLS-1$
    private static final String ASSERTIONS_PROP      = "jmeter.save.saveservice.assertions"; // $NON_NLS-1$
    private static final String LATENCY_PROP         = "jmeter.save.saveservice.latency"; // $NON_NLS-1$
    private static final String SAMPLERDATA_PROP     = "jmeter.save.saveservice.samplerData"; // $NON_NLS-1$
    private static final String RESPONSEHEADERS_PROP = "jmeter.save.saveservice.responseHeaders"; // $NON_NLS-1$
    private static final String REQUESTHEADERS_PROP  = "jmeter.save.saveservice.requestHeaders"; // $NON_NLS-1$
    private static final String ENCODING_PROP        = "jmeter.save.saveservice.encoding"; // $NON_NLS-1$
    
	// ---------------------------------------------------------------------
	// XML RESULT FILE CONSTANTS AND FIELD NAME CONSTANTS
	// ---------------------------------------------------------------------

    //TODO - these do not appear to be used
	public final static String PRESERVE = "preserve"; // $NON_NLS-1$

	public final static String XML_SPACE = "xml:space"; // $NON_NLS-1$

	public static final String ASSERTION_RESULT_TAG_NAME = "assertionResult"; // $NON_NLS-1$

	public static final String BINARY = "binary"; // $NON_NLS-1$

	public static final String DATA_TYPE = "dataType"; // $NON_NLS-1$

	public static final String ERROR = "error"; // $NON_NLS-1$

	public static final String FAILURE = "failure"; // $NON_NLS-1$

	public static final String FAILURE_MESSAGE = "failureMessage"; // $NON_NLS-1$

	public static final String LABEL = "label"; // $NON_NLS-1$

	public static final String RESPONSE_CODE = "responseCode"; // $NON_NLS-1$

	public static final String RESPONSE_MESSAGE = "responseMessage"; // $NON_NLS-1$

	public static final String SAMPLE_RESULT_TAG_NAME = "sampleResult"; // $NON_NLS-1$

	public static final String SUCCESSFUL = "success"; // $NON_NLS-1$

	public static final String THREAD_NAME = "threadName"; // $NON_NLS-1$

	public static final String TIME = "time"; // $NON_NLS-1$

	public static final String TIME_STAMP = "timeStamp"; // $NON_NLS-1$

	// Initialise values from properties
	private boolean time = _time, latency = _latency, timestamp = _timestamp, success = _success, label = _label,
			code = _code, message = _message, threadName = _threadName, dataType = _dataType, encoding = _encoding,
			assertions = _assertions, subresults = _subresults, responseData = _responseData,
			samplerData = _samplerData, xml = _xml, fieldNames = _fieldNames, responseHeaders = _responseHeaders,
			requestHeaders = _requestHeaders, responseDataOnError = _responseDataOnError;

	private boolean saveAssertionResultsFailureMessage = _saveAssertionResultsFailureMessage;

	private int assertionsResultsToSave = _assertionsResultsToSave;

	private String delimiter = _delimiter;

	private boolean printMilliseconds = _printMilliseconds;

	/** A formatter for the time stamp. */
	private transient SimpleDateFormat formatter = _formatter;
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

	private static int _assertionsResultsToSave;

	// TODO turn into method?
	public static final int SAVE_NO_ASSERTIONS = 0;

	public static final int SAVE_FIRST_ASSERTION = SAVE_NO_ASSERTIONS + 1;

	public static final int SAVE_ALL_ASSERTIONS = SAVE_FIRST_ASSERTION + 1;

	private static final boolean _printMilliseconds;

	private static final SimpleDateFormat _formatter;

	/**
	 * The string used to separate fields when stored to disk, for example, the
	 * comma for CSV files.
	 */
	private static final String _delimiter;

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

		_delimiter = props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER);

		_fieldNames = TRUE.equalsIgnoreCase(props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));

		_dataType = TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));

		_label = TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));

		_code = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));

		_responseData = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));

		_responseDataOnError = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_DATA_ON_ERROR_PROP, FALSE));

		_message = TRUE.equalsIgnoreCase(props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));

		_success = TRUE.equalsIgnoreCase(props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));

		_threadName = TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));

		_time = TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));

		_timeStampFormat = props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);

		_printMilliseconds = MILLISECONDS.equalsIgnoreCase(_timeStampFormat);

		// Prepare for a pretty date
		if (!_printMilliseconds && !NONE.equalsIgnoreCase(_timeStampFormat) && (_timeStampFormat != null)) {
			_formatter = new SimpleDateFormat(_timeStampFormat);
		} else {
			_formatter = null;
		}

		_timestamp = !_timeStampFormat.equalsIgnoreCase(NONE);

		_saveAssertionResultsFailureMessage = TRUE.equalsIgnoreCase(props.getProperty(
				ASSERTION_RESULTS_FAILURE_MESSAGE_PROP, FALSE));

		String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
		if (NONE.equals(whichAssertionResults)) {
			_assertionsResultsToSave = SAVE_NO_ASSERTIONS;
		} else if (FIRST.equals(whichAssertionResults)) {
			_assertionsResultsToSave = SAVE_FIRST_ASSERTION;
		} else if (ALL.equals(whichAssertionResults)) {
			_assertionsResultsToSave = SAVE_ALL_ASSERTIONS;
		}

		String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, XML);

		if (XML.equals(howToSave)) {
			_xml = true;
		} else {
			_xml = false;
		}

	}

	private static final SampleSaveConfiguration _static = new SampleSaveConfiguration();

	// Give access to initial configuration
	public static SampleSaveConfiguration staticConfig() {
		return _static;
	}

	public SampleSaveConfiguration() {
	}

	public Object clone() {
		SampleSaveConfiguration s = new SampleSaveConfiguration();
		s.time = time;
		s.latency = latency;
		s.timestamp = timestamp;
		s.success = success;
		s.label = label;
		s.code = code;
		s.message = message;
		s.threadName = threadName;
		s.dataType = dataType;
		s.encoding = encoding;
		s.assertions = assertions;
		s.subresults = subresults;
		s.responseData = responseData;
		s.samplerData = samplerData;
		s.xml = xml;
		s.fieldNames = fieldNames;
		s.responseHeaders = responseHeaders;
		s.requestHeaders = requestHeaders;
		s.formatter = formatter;
		s.assertionsResultsToSave = assertionsResultsToSave;
		s.saveAssertionResultsFailureMessage = saveAssertionResultsFailureMessage;
		s.delimiter = delimiter;
		s.printMilliseconds = printMilliseconds;
		s.responseDataOnError = responseDataOnError;
		return s;
	}

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

	/**
	 * @return Returns the assertions.
	 */
	public boolean saveAssertions() {
		return assertions;
	}

	/**
	 * @param assertions
	 *            The assertions to set.
	 */
	public void setAssertions(boolean assertions) {
		this.assertions = assertions;
	}

	/**
	 * @return Returns the code.
	 */
	public boolean saveCode() {
		return code;
	}

	/**
	 * @param code
	 *            The code to set.
	 */
	public void setCode(boolean code) {
		this.code = code;
	}

	/**
	 * @return Returns the dataType.
	 */
	public boolean saveDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            The dataType to set.
	 */
	public void setDataType(boolean dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return Returns the encoding.
	 */
	public boolean saveEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            The encoding to set.
	 */
	public void setEncoding(boolean encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return Returns the label.
	 */
	public boolean saveLabel() {
		return label;
	}

	/**
	 * @param label
	 *            The label to set.
	 */
	public void setLabel(boolean label) {
		this.label = label;
	}

	/**
	 * @return Returns the latency.
	 */
	public boolean saveLatency() {
		return latency;
	}

	/**
	 * @param latency
	 *            The latency to set.
	 */
	public void setLatency(boolean latency) {
		this.latency = latency;
	}

	/**
	 * @return Returns the message.
	 */
	public boolean saveMessage() {
		return message;
	}

	/**
	 * @param message
	 *            The message to set.
	 */
	public void setMessage(boolean message) {
		this.message = message;
	}

	/**
	 * Should samplerData be saved for the current result?
	 * 
	 * @return Returns whether to save the samplerData.
	 */
	public boolean saveResponseData(SampleResult res) {
		return responseData || TestPlan.getFunctionalMode() || (responseDataOnError && !res.isSuccessful());
	}
    
    public boolean saveResponseData()
    {
        return responseData;
    }

	/**
	 * @param responseData
	 *            The responseData to set.
	 */
	public void setResponseData(boolean responseData) {
		this.responseData = responseData;
	}

	/**
	 * @return Returns whether to save the samplerData.
	 */
	public boolean saveSamplerData(SampleResult res) {
		return samplerData || TestPlan.getFunctionalMode() // as per 2.0 branch
				|| (responseDataOnError && !res.isSuccessful());
	}
    
    public boolean saveSamplerData()
    {
        return samplerData;
    }

	/**
	 * @param samplerData
	 *            The samplerData to set.
	 */
	public void setSamplerData(boolean samplerData) {
		this.samplerData = samplerData;
	}

	/**
	 * @return Returns the subresults.
	 */
	public boolean saveSubresults() {
		return subresults;
	}

	/**
	 * @param subresults
	 *            The subresults to set.
	 */
	public void setSubresults(boolean subresults) {
		this.subresults = subresults;
	}

	/**
	 * @return Returns the success.
	 */
	public boolean saveSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            The success to set.
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return Returns the threadName.
	 */
	public boolean saveThreadName() {
		return threadName;
	}

	/**
	 * @param threadName
	 *            The threadName to set.
	 */
	public void setThreadName(boolean threadName) {
		this.threadName = threadName;
	}

	/**
	 * @return Returns the time.
	 */
	public boolean saveTime() {
		return time;
	}

	/**
	 * @param time
	 *            The time to set.
	 */
	public void setTime(boolean time) {
		this.time = time;
	}

	/**
	 * @return Returns the timestamp.
	 */
	public boolean saveTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            The timestamp to set.
	 */
	public void setTimestamp(boolean timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return Returns the xml.
	 */
	public boolean saveAsXml() {
		return xml;
	}

	/**
	 * @param xml
	 *            The xml to set.
	 */
	public void setAsXml(boolean xml) {
		this.xml = xml;
	}

	/**
	 * @return Returns the printFieldNames.
	 */
	public boolean saveFieldNames() {
		return fieldNames;
	}

	/**
	 * @param printFieldNames -
	 *            should field names be printed?
	 */
	public void setFieldNames(boolean printFieldNames) {
		this.fieldNames = printFieldNames;
	}

	public boolean printMilliseconds() {
		return printMilliseconds;
	}

	public SimpleDateFormat formatter() {
		return formatter;
	}

	public boolean saveAssertionResultsFailureMessage() {
		return saveAssertionResultsFailureMessage;
	}

	public void setAssertionResultsFailureMessage(boolean b) {
		saveAssertionResultsFailureMessage = b;
	}

	public int assertionsResultsToSave() {
		return assertionsResultsToSave;
	}

	public String getDelimiter() {
		return delimiter;
	}
}