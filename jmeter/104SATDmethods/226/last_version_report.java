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

        String dlm = JMeterUtils.getDelimiter(props.getProperty(DEFAULT_DELIMITER_PROP, DEFAULT_DELIMITER));
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
        // FIXME Can _timeStampFormat be null ? it does not appear to me .
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

        _idleTime=TRUE.equalsIgnoreCase(props.getProperty(SAVE_IDLE_TIME, TRUE));
    }
