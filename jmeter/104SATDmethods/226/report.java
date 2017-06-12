File path: src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
Comment: Make it easier to enter a tab (can use \<tab> but that is awkward)
Initial commit id: c84db4ab
Final commit id: a7efa9ef
   Bugs between [      12]:
a7efa9efe Bug 60125 - Report / Dashboard : Dashboard cannot be generated if the default delimiter is \t Bugzilla Id: 60125
14d593aba Bug 59523 - Report Generator : NormalizerSampleConsumer uses a different default format for timestamp than JMeter default Bugzilla Id: 59523
f6b96cee9 Bug 57182 - Better defaults : Save idle time by default Bugzilla Id: 57182
e85059bca Bug 59064 - Remove OldSaveService which supported very old Avalon format JTL (result) files Bugzilla Id: 59064
e94825847 Bug 58991 - Settings defaults : Switch "jmeter.save.saveservice.print_field_names" to true (after 2.13) Bugzilla Id: 58991
5486169c9 Bug 58978 - Settings defaults : Switch "jmeter.save.saveservice.assertion_results_failure_message" to true (after 2.13) Bugzilla Id: 58978
27745b727 Bug 58653 - New JMeter Dashboard/Report with Dynamic Graphs, Tables to help analyzing load test results Bugzilla Id: 58653
5d6aec5db Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
b31d061c3 Bug 57025 - SaveService : Better defaults, save thread counts by default Bugzilla Id: 57025
6445156ab Bug 54412 - Changing JMeter defaults to ensure better performances by default Bugzilla Id: 54412
472da1514 Bug 53765 - Switch to commons-lang3-3.1 Bugzilla Id: 53765
55c15877e Bug 50203 Cannot set property "jmeter.save.saveservice.default_delimiter=\t"
   Bugs after [       5]:
1ee63ff4b Bug 60830 Timestamps in CSV file could be corrupted due to sharing a SimpleDateFormatter across threads Bugzilla Id: 60830
46234ac04 Bug 60830 - Timestamps in CSV file could be corrupted due to sharing a SimpleDateFormatter across threads Bugzilla Id: 60830
88a092424 Bug 60564 - Migrating LogKit to SLF4J - core/samplers,save,services,swing,testbeans (1/2) Contributed by Woonsan Ko This comments #271 Bugzilla Id: 60564
bac01a627 Bug 60229 - Add a new metric : sent_bytes Bugzilla Id: 60229
58c262ee3 Bug 60106 - Settings defaults : Switch "jmeter.save.saveservice.connect_time" to true (after 3.0) Bugzilla Id: 60106

Start block index: 277
End block index: 356
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
		}

		String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, XML);

		if (XML.equals(howToSave)) {
			_xml = true;
		} else {
			_xml = false;
		}

        _threadCounts=TRUE.equalsIgnoreCase(props.getProperty(SAVE_THREAD_COUNTS, FALSE));

        _sampleCount=TRUE.equalsIgnoreCase(props.getProperty(SAVE_SAMPLE_COUNT, FALSE));
	}


*********************** Method when SATD was removed **************************


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
