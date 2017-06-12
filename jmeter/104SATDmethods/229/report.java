File path: src/core/org/apache/jmeter/save/OldSaveService.java
Comment: ODO provide proper name?
Initial commit id: e96b8fbf
Final commit id: e85059bc
   Bugs between [       1]:
e85059bca Bug 59064 - Remove OldSaveService which supported very old Avalon format JTL (result) files Bugzilla Id: 59064
   Bugs after [       0]:


Start block index: 187
End block index: 193
    private static AssertionResult getAssertionResult(Configuration config) {
        AssertionResult result = new AssertionResult(""); //TODO provide proper name?
        result.setError(config.getAttributeAsBoolean(ERROR, false));
        result.setFailure(config.getAttributeAsBoolean(FAILURE, false));
        result.setFailureMessage(config.getAttribute(FAILURE_MESSAGE, ""));
        return result;
    }
