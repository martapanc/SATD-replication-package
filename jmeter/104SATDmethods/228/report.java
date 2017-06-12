File path: src/core/org/apache/jmeter/save/OldSaveService.java
Comment: ODO provide proper name?
Initial commit id: 07a261e3
Final commit id: 3885b642
   Bugs between [       4]:
6f9771e84 Bug 43450 - add save/restore of error count; fix Calculator to use error count
be023bb62 Bug 43450 (partial fix) - Allow SampleCount to be saved/restored from CSV files
9a3d4075a Bug 43430 - Count of active threads is incorrect for remote samples
1b221e055 Bug 42919 - Failure Message blank in CSV output [now records first non-blank message]
   Bugs after [       1]:
e85059bca Bug 59064 - Remove OldSaveService which supported very old Avalon format JTL (result) files Bugzilla Id: 59064

Start block index: 603
End block index: 609
	public static AssertionResult getAssertionResult(Configuration config) {
		AssertionResult result = new AssertionResult(""); //TODO provide proper name?
		result.setError(config.getAttributeAsBoolean(ERROR, false));
		result.setFailure(config.getAttributeAsBoolean(FAILURE, false));
		result.setFailureMessage(config.getAttribute(FAILURE_MESSAGE, ""));
		return result;
	}
