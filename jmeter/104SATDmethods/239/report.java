File path: src/core/org/apache/jmeter/samplers/SampleSaveConfiguration.java
Comment: TODO is this correct?
Initial commit id: fa25c32c
Final commit id: 9f551405
   Bugs between [       0]:

   Bugs after [      19]:
1ee63ff4b Bug 60830 Timestamps in CSV file could be corrupted due to sharing a SimpleDateFormatter across threads Bugzilla Id: 60830
46234ac04 Bug 60830 - Timestamps in CSV file could be corrupted due to sharing a SimpleDateFormatter across threads Bugzilla Id: 60830
88a092424 Bug 60564 - Migrating LogKit to SLF4J - core/samplers,save,services,swing,testbeans (1/2) Contributed by Woonsan Ko This comments #271 Bugzilla Id: 60564
bac01a627 Bug 60229 - Add a new metric : sent_bytes Bugzilla Id: 60229
58c262ee3 Bug 60106 - Settings defaults : Switch "jmeter.save.saveservice.connect_time" to true (after 3.0) Bugzilla Id: 60106
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
be023bb62 Bug 43450 (partial fix) - Allow SampleCount to be saved/restored from CSV files
986530c48 Bug 25441 - TestPlan changes sometimes detected incorrectly (isDirty)

Start block index: 164
End block index: 279
   private static final boolean _time,  _timestamp, _success,
   _label, _code, _message, _threadName, _xml, _responseData,
   _dataType, _encoding, _assertions, _latency,
   _subresults,  _samplerData, _fieldNames, _responseHeaders, _requestHeaders;
   
   private static final boolean _saveAssertionResultsFailureMessage;
   private static final String _timeStampFormat;
   private static int _assertionsResultsToSave;
   // TODO turn into method?
   public static final int SAVE_NO_ASSERTIONS = 0;
   public static final int SAVE_FIRST_ASSERTION = SAVE_NO_ASSERTIONS + 1;
   public static final int SAVE_ALL_ASSERTIONS = SAVE_FIRST_ASSERTION + 1;
   private static final boolean _printMilliseconds;
   private static final SimpleDateFormat _formatter;
   
   /** The string used to separate fields when stored to disk, for example,
   the comma for CSV files. */
   private static final String _delimiter;
   private static final String DEFAULT_DELIMITER = ",";

   /**
    * Read in the properties having to do with saving from a properties file.
    */
   static
   {
	   // TODO - get from properties?
	   _subresults = _encoding = _assertions= _latency = _samplerData = true;
	   _responseHeaders = _requestHeaders = true;
	   
	   
		Properties props = JMeterUtils.getJMeterProperties();

		_delimiter=props.getProperty(DEFAULT_DELIMITER_PROP,DEFAULT_DELIMITER);
		
       _fieldNames =
           TRUE.equalsIgnoreCase(
               props.getProperty(PRINT_FIELD_NAMES_PROP, FALSE));

       _dataType =
           TRUE.equalsIgnoreCase(props.getProperty(SAVE_DATA_TYPE_PROP, TRUE));

       _label =
           TRUE.equalsIgnoreCase(props.getProperty(SAVE_LABEL_PROP, TRUE));

       _code = // TODO is this correct?
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_RESPONSE_CODE_PROP, TRUE));

       _responseData =
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_RESPONSE_DATA_PROP, FALSE));

       _message =
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_RESPONSE_MESSAGE_PROP, TRUE));

       _success =
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_SUCCESSFUL_PROP, TRUE));

       _threadName =
           TRUE.equalsIgnoreCase(
               props.getProperty(SAVE_THREAD_NAME_PROP, TRUE));

       _time =
           TRUE.equalsIgnoreCase(props.getProperty(SAVE_TIME_PROP, TRUE));

       _timeStampFormat =
           props.getProperty(TIME_STAMP_FORMAT_PROP, MILLISECONDS);

       _printMilliseconds = MILLISECONDS.equalsIgnoreCase(_timeStampFormat);

       // Prepare for a pretty date
       if (!_printMilliseconds
           && !NONE.equalsIgnoreCase(_timeStampFormat)
           && (_timeStampFormat != null))
       {
           _formatter = new SimpleDateFormat(_timeStampFormat);
       } else {
		   _formatter = null;
       }

	   _timestamp = !_timeStampFormat.equalsIgnoreCase(NONE);
	   
       _saveAssertionResultsFailureMessage =
           TRUE.equalsIgnoreCase(
               props.getProperty(
                   ASSERTION_RESULTS_FAILURE_MESSAGE_PROP,
                   FALSE));

       String whichAssertionResults = props.getProperty(ASSERTION_RESULTS_PROP, NONE);
       if (NONE.equals(whichAssertionResults))
       {
           _assertionsResultsToSave = SAVE_NO_ASSERTIONS;
       }
       else if (FIRST.equals(whichAssertionResults))
       {
           _assertionsResultsToSave = SAVE_FIRST_ASSERTION;
       }
       else if (ALL.equals(whichAssertionResults))
       {
           _assertionsResultsToSave = SAVE_ALL_ASSERTIONS;
       }

       String howToSave = props.getProperty(OUTPUT_FORMAT_PROP, XML);

       if (XML.equals(howToSave))
       {
           _xml=true;
       }
       else
       {
           _xml=false;
       }

   }
