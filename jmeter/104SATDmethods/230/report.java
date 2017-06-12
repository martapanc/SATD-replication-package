File path: src/core/org/apache/jmeter/save/OldSaveService.java
Comment: TODO: should this be restored?
Initial commit id: 2c179593
Final commit id: f246b924
   Bugs between [       7]:
6f9771e84 Bug 43450 - add save/restore of error count; fix Calculator to use error count
be023bb62 Bug 43450 (partial fix) - Allow SampleCount to be saved/restored from CSV files
9a3d4075a Bug 43430 - Count of active threads is incorrect for remote samples
1b221e055 Bug 42919 - Failure Message blank in CSV output [now records first non-blank message]
fc0611c06 Bug 41277 - add Latency and Encoding to CSV output
1f186f591 Bug 40772 - correctly parse missing fields
5c126dc98 Bug 29481 - fix reloading sample results so subresults not counted twice
   Bugs after [       1]:
e85059bca Bug 59064 - Remove OldSaveService which supported very old Avalon format JTL (result) files Bugzilla Id: 59064

Start block index: 137
End block index: 227
    public static SampleResult makeResultFromDelimitedString(String inputLine, SampleSaveConfiguration saveConfig) {
        // Check for a header line
        if (inputLine.startsWith(TIME_STAMP) || inputLine.startsWith(CSV_TIME)){
            return null;
        }
		SampleResult result = null;
		long timeStamp = 0;
		long elapsed = 0;
		StringTokenizer splitter = new StringTokenizer(inputLine, _saveConfig.getDelimiter());
		String text = null;

		try {
			if (saveConfig.printMilliseconds()) {
				text = splitter.nextToken();
				timeStamp = Long.parseLong(text);
			} else if (saveConfig.formatter() != null) {
				text = splitter.nextToken();
				Date stamp = saveConfig.formatter().parse(text);
				timeStamp = stamp.getTime();
			}

			if (saveConfig.saveTime()) {
				text = splitter.nextToken();
				elapsed = Long.parseLong(text);
			}

			result = new SampleResult(timeStamp, elapsed);

			if (saveConfig.saveLabel()) {
				text = splitter.nextToken();
				result.setSampleLabel(text);
			}
			if (saveConfig.saveCode()) {
				text = splitter.nextToken();
				result.setResponseCode(text);
			}

			if (saveConfig.saveMessage()) {
				text = splitter.nextToken();
				result.setResponseMessage(text);
			}

			if (saveConfig.saveThreadName()) {
				text = splitter.nextToken();
				result.setThreadName(text);
			}

			if (saveConfig.saveDataType()) {
				text = splitter.nextToken();
				result.setDataType(text);
			}

			if (saveConfig.saveSuccess()) {
				text = splitter.nextToken();
				result.setSuccessful(Boolean.valueOf(text).booleanValue());
			}

			if (saveConfig.saveAssertionResultsFailureMessage()) {
				text = splitter.nextToken();
                // TODO - should this be restored?
			}
            
            if (saveConfig.saveBytes()) {
                text = splitter.nextToken();
                result.setBytes(Integer.parseInt(text));
            }
        
            if (saveConfig.saveThreadCounts()) {
                text = splitter.nextToken();
                // Not saved, as not part of a result
            }

            if (saveConfig.saveUrl()) {
                text = splitter.nextToken();
                // TODO: should this be restored?
            }
        
            if (saveConfig.saveFileName()) {
                text = splitter.nextToken();
                result.setResultFileName(text);
            }            
            
		} catch (NumberFormatException e) {
			log.warn("Error parsing number " + e);
			throw e;
		} catch (ParseException e) {
			log.warn("Error parsing line " + e);
			throw new RuntimeException(e.toString());
		}
		return result;
	}
