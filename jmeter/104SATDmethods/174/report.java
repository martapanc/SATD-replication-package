File path: src/components/org/apache/jmeter/assertions/XMLSchemaAssertion.java
Comment: TODO is this the correct strategy?
Initial commit id: 41436a6b6
Final commit id: 77b43dd77
   Bugs between [       3]:
bc41e092e Bug 57193: Put <a...> on one line, as it will confuse javadoc  otherwise. Bugzilla Id: 57193
e9bcada30 Bug 52519 - XMLSchemaAssertion uses JMeter JVM file.encoding instead of response encoding
0a717bbad Bug 41873 - Add name to AssertionResult and display AssertionResult in ViewResultsFullVisualizer
   Bugs after [       2]:
7ebfa083a Bug 60564 - Migrating LogKit to SLF4J - check log level in non-error logging if method invoked in params Contributed by Woonsan Ko This closes #267 Bugzilla Id: 60564
0af7ce0e4 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution Contributed by Woonsan Ko This closes #263 Bugzilla Id: 60564

Start block index: 227
End block index: 237
        public void warning(SAXParseException exception)
                throws SAXParseException
        {

			String msg="warning: "+errorDetails(exception);
			log.debug(msg);
			result.setFailureMessage(msg);
			//result.setError(true); // TODO is this the correct strategy?
            //throw exception; // allow assertion to pass

        }
*********************** Method when SATD was removed **************************

@Override
public void warning(SAXParseException exception) throws SAXParseException {
    String msg = "warning: " + errorDetails(exception);
    log.debug(msg);
    result.setFailureMessage(msg);
}
