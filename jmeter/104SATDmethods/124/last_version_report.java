    public static final String UNSPECIFIED_PORT_AS_STRING = "0"; // $NON-NLS-1$

    /** If the port is not present in a URL, getPort() returns -1 */
    public static final int URL_UNSPECIFIED_PORT = -1;
    public static final String URL_UNSPECIFIED_PORT_AS_STRING = "-1"; // $NON-NLS-1$

    protected static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";

    protected static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";

    public static final String POST_BODY_RAW = "HTTPSampler.postBodyRaw";

    public static final boolean POST_BODY_RAW_DEFAULT = false;

    private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$

    private static final String QRY_SEP = "&"; // $NON-NLS-1$

    private static final String QRY_PFX = "?"; // $NON-NLS-1$

    protected static final int MAX_REDIRECTS = JMeterUtils.getPropDefault("httpsampler.max_redirects", 20); // $NON-NLS-1$

    protected static final int MAX_FRAME_DEPTH = JMeterUtils.getPropDefault("httpsampler.max_frame_depth", 5); // $NON-NLS-1$

    // Derive the mapping of content types to parsers
    private static final Map<String, String> PARSERS_FOR_CONTENT_TYPE = new HashMap<>();
    // Not synch, but it is not modified after creation

    private static final String RESPONSE_PARSERS = // list of parsers
            JMeterUtils.getProperty("HTTPResponse.parsers");//$NON-NLS-1$
    
    // Bug 49083
    /** Whether to remove '/pathsegment/..' from redirects; default true */
    private static final boolean REMOVESLASHDOTDOT =
            JMeterUtils.getPropDefault("httpsampler.redirect.removeslashdotdot", true);
    
    private static final String HTTP_PREFIX = HTTPConstants.PROTOCOL_HTTP+"://"; // $NON-NLS-1$
    private static final String HTTPS_PREFIX = HTTPConstants.PROTOCOL_HTTPS+"://"; // $NON-NLS-1$

    // Bug 51939
    private static final boolean SEPARATE_CONTAINER =
            JMeterUtils.getPropDefault("httpsampler.separate.container", true); // $NON-NLS-1$

    static {
        String[] parsers = JOrphanUtils.split(RESPONSE_PARSERS, " " , true);// returns empty array for null
        for (final String parser : parsers) {
            String classname = JMeterUtils.getProperty(parser + ".className");//$NON-NLS-1$
            if (classname == null) {
                log.error("Cannot find .className property for " + parser+", ensure you set property:'" + parser + ".className'");
                continue;
            }
            String typeList = JMeterUtils.getProperty(parser + ".types");//$NON-NLS-1$
            if (typeList != null) {
                String[] types = JOrphanUtils.split(typeList, " ", true);
                for (final String type : types) {
                    log.info("Parser for " + type + " is " + classname);
                    PARSERS_FOR_CONTENT_TYPE.put(type, classname);
                }
            } else {
                log.warn("Cannot find .types property for " + parser
                        + ", as a consequence parser will not be used, to make it usable, define property:'"
                        + parser + ".types'");
            }
        }
    }
