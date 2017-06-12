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
