File path: src/protocol/http/org/apache/jmeter/protocol/http/sampler/WebServiceSampler.java
Comment: $NON-NLS-1$ TODO should this be a variable?
Initial commit id: 692859a8
Final commit id: 33d71e87
   Bugs between [       5]:
ebb9c4e45 Bug 58100 - Performance enhancements : Replace Random by ThreadLocalRandom Bugzilla Id: 58100
ee0c987ff Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
ec8d230cd Bug 54004 - Webservice Sampler : Allow adding headers to request with Header Manager Bugzilla Id: 54004
74834bae0 Bug 52939 - Webservice Sampler : Make MaintainSession configurable
3126f8c56 Bug 52937 - Webservice Sampler : Clear Soap Documents Cache at end of Test
   Bugs after [       0]:


Start block index: 96
End block index: 113
    private static final String PROXY_PASS =
        JMeterUtils.getPropDefault(JMeter.HTTP_PROXY_PASS,""); // $NON-NLS-1$

    private static final String ENCODING = "UTF-8"; // $NON-NLS-1$ TODO should this be a variable?

    /*
     * Random class for generating random numbers.
     */
    private final Random RANDOM = new Random();

    private String fileContents = null;

    /**
     * Set the path where XML messages are stored for random selection.
     */
    public void setXmlPathLoc(String path) {
        setProperty(XML_PATH_LOC, path);
    }
