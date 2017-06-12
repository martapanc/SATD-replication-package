File path: src/protocol/jms/org/apache/jmeter/protocol/jms/sampler/SubscriberSampler.java
Comment: TODO set different status if not enough messages found?
Initial commit id: 57708e4d2
Final commit id: 3c61b5450
   Bugs between [      17]:
3c61b5450 Bug 58980 - JMS Subscriber will return successful as long as 1 message is received. #resolve #111 Bugzilla Id: 58980
dd30d6171 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
3f7c5f507 Bug 57193: Self-closing br element is not allowed in javadoc Bugzilla Id: 57193
d3e00524c Bug 52265 - Code:Transient fields not set by deserialization Bugzilla Id: 52265
b25b73c10 Bug 54182 - Support sending of ByteMessage for JMS Publisher. Bugzilla Id: 54182
472da1514 Bug 53765 - Switch to commons-lang3-3.1 Bugzilla Id: 53765
3da254b78 Bug 52052 - Using a delimiter to separate result-messages for JMS Subscriber ER1: the separator should not be placed after the last message; ER2: no separator for one message;
c3e1778ca Bug 52052 - Using a delimiter to separate result-messages for JMS Subscriber There was an issue when following options were checked: - stopBetweenSamples - Read Response
be0d75a92 Bug 52052 - Using a delimiter to separate result-messages for JMS Subscriber
25c941775 Bug 52052 - Using a delimiter to separate result-messages for JMS Subscriber Fix setting \t, \n , \r as separator
c28b77943 Bug 52052 - Using a delimiter to separate result-messages for JMS Subscriber
93a5ce84e Bug 52044 - JMS Subscriber used with many threads leads to javax.naming.NamingException: Something already bound with ActiveMQ
1c4a4e7b7 Bug 52044 - JMS Subscriber used with many threads leads to javax.naming.NamingException: Something already bound with ActiveMQ
98fac8545 Bug 51419 - JMS Subscriber: ability to use Selectors
b66c9a19a Bug 52036 - Durable Subscription fails with ActiveMQ due to missing clientId field
0f48431b5 Bug 51840 - JMS : Cache of InitialContext has some issues
921b365dc Bug 50666 - JMSSubscriber: support for durable subscriptions
   Bugs after [       3]:
567f75d50 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution Use slf4j parameterized messages Bugzilla Id: 60564
188d41207 Bug 60585 - JMS Publisher and JMS Subscriber : Allow reconnection on error and pause between errors Based on PR 240  from by Logan Mauzaize (logan.mauzaize at gmail.com) and Maxime Chassagneux (maxime.chassagneux at gmail.com).
a44c6efe5 Bug 53039 - HTTP Request : Be able to handle responses which size exceeds 2147483647 bytes Fix deprecated calls to setBytes(int) Bugzilla Id: 53039

Start block index: 183
End block index: 230
    private SampleResult sampleWithListener(SampleResult result) {
        StringBuilder buffer = new StringBuilder();
        StringBuilder propBuffer = new StringBuilder();

        int loop = getIterationCount();
        int read = 0;

        long until = 0L;
        long now = System.currentTimeMillis();
        if (timeout > 0) {
            until = timeout + now;
        }
        while (!interrupted
                && (until == 0 || now < until)
                && read < loop) {
            try {
                Message msg = queue.poll(calculateWait(until, now), TimeUnit.MILLISECONDS);
                if (msg != null) {
                    read++;
                    extractContent(buffer, propBuffer, msg);
                }
            } catch (InterruptedException e) {
                // Ignored
            }
            now = System.currentTimeMillis();
        }
        result.sampleEnd();

        if (getReadResponseAsBoolean()) {
            result.setResponseData(buffer.toString().getBytes());
        } else {
            result.setBytes(buffer.toString().getBytes().length);
        }
        result.setResponseHeaders(propBuffer.toString());
        result.setDataType(SampleResult.TEXT);
        if (read == 0) {
            result.setResponseCode("404"); // Not found
            result.setSuccessful(false);
        } else { // TODO set different status if not enough messages found?
            result.setResponseCodeOK();
            result.setSuccessful(true);
        }
        result.setResponseMessage(read + " messages received");
        result.setSamplerData(loop + " messages expected");
        result.setSampleCount(read);

        return result;
    }

*********************** Method when SATD was removed **************************

(deprecated, removed)
