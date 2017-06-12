File path: src/components/org/apache/jmeter/assertions/ResponseAssertion.java
Comment: TODO strings should be resources
Initial commit id: eb9335c47
Final commit id: e69599f29
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

Start block index: 338
End block index: 370
	// TODO strings should be resources
	private String getFailText(String stringPattern) {
		String text;
		String what;
		if (ResponseAssertion.RESPONSE_DATA.equals(getTestField())) {
			what = "text";
		} else if (ResponseAssertion.RESPONSE_CODE.equals(getTestField())) {
			what = "code";
		} else if (ResponseAssertion.RESPONSE_MESSAGE.equals(getTestField())) {
			what = "message";
		} else // Assume it is the URL
		{
			what = "URL";
		}
		switch (getTestType()) {
		case CONTAINS:
			text = " expected to contain ";
			break;
		case NOT | CONTAINS:
			text = " expected not to contain ";
			break;
		case MATCH:
			text = " expected to match ";
			break;
		case NOT | MATCH:
			text = " expected not to match ";
			break;
		default:// should never happen...
			text = " expected something using ";
		}

		return "Test failed, " + what + text + "/" + stringPattern + "/";
	}

*********************** Method when SATD was removed **************************

private String getFailText(String stringPattern, String toCheck) {

    StringBuilder sb = new StringBuilder(200);
    sb.append("Test failed: ");

    if (isScopeVariable()){
        sb.append("variable(").append(getVariableName()).append(')');
    } else if (isTestFieldResponseData()) {
        sb.append("text");
    } else if (isTestFieldResponseCode()) {
        sb.append("code");
    } else if (isTestFieldResponseMessage()) {
        sb.append("message");
    } else if (isTestFieldResponseHeaders()) {
        sb.append("headers");
    } else if (isTestFieldResponseDataAsDocument()) {
        sb.append("document");
    } else // Assume it is the URL
    {
        sb.append("URL");
    }

    switch (getTestType()) {
    case CONTAINS:
    case SUBSTRING:
        sb.append(" expected to contain ");
        break;
    case NOT | CONTAINS:
    case NOT | SUBSTRING:
        sb.append(" expected not to contain ");
        break;
    case MATCH:
        sb.append(" expected to match ");
        break;
    case NOT | MATCH:
        sb.append(" expected not to match ");
        break;
    case EQUALS:
        sb.append(" expected to equal ");
        break;
    case NOT | EQUALS:
        sb.append(" expected not to equal ");
        break;
    default:// should never happen...
        sb.append(" expected something using ");
    }

    sb.append("/");

    if (isEqualsType()){
        sb.append(equalsComparisonText(toCheck, stringPattern));
    } else {
        sb.append(stringPattern);
    }

    sb.append("/");

    return sb.toString();
}
