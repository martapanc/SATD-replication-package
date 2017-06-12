    public static XMLReader getXMLParser() {
        final String parserName = getPropDefault("xml.parser", // $NON-NLS-1$
                "org.apache.xerces.parsers.SAXParser");  // $NON-NLS-1$
        return (XMLReader) instantiate(parserName,
                "org.xml.sax.XMLReader"); // $NON-NLS-1$
    }
