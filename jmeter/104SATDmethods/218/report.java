File path: src/components/org/apache/jmeter/assertions/ResponseAssertion.java
Comment: k values for TEST_TYPE TODO: remove either MATCH or CONTAINS - they
Initial commit id: 76159a5b
Final commit id: e69599f2
   Bugs between [       9]:
3b7e03d0f Bug 58705 - Make org.apache.jmeter.testelement.property.MultiProperty iterable #resolve #48 Bugzilla Id: 58705
4a087408e Bug 55694 - Assertions and Extractors : Avoid NullPointerException when scope is variable and variable is missing Bugzilla Id: 55694
ef65b97b8 Bug 45903 - allow Assertions to apply to sub-samples Implemented for Duration, Response and Size
2ab1c97a1 Bug 45904 - Allow 'Not' Response Assertion to succeed with null sample
6e4d7a932 Bug 45749 - Response Assertion does not work with a substring that is not a valid RE
d3e1f36e4 Bug 43584 - Replace comma to avoid problem with CSV files
0709a6744 Bug 40991 - Allow Assertions to check Headers
0a717bbad Bug 41873 - Add name to AssertionResult and display AssertionResult in ViewResultsFullVisualizer
cfd28b1fb Bug 40369 (partial) add equals matching to Response Assertion Also tidied up the interfaces somewhat
   Bugs after [       3]:
13fd1465f Bug 60747 - Response Assertion : Add Request Headers to "Field to Test" Bugzilla Id: 60747
0af7ce0e4 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution Contributed by Woonsan Ko This closes #263 Bugzilla Id: 60564
3d13ac879 Bug 60507 - Added 'Or' Function into ResponseAssertion Based on contribution by  忻隆 Bugzilla Id: 60507

Start block index: 66
End block index: 69
	 * Mask values for TEST_TYPE TODO: remove either MATCH or CONTAINS - they
	 * are mutually exckusive
	 */
	private final static int MATCH = 1 << 0;
