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
