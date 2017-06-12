File path: src/protocol/http/org/apache/jmeter/protocol/http/parser/LagartoBasedHtmlParser.java
Comment: TODO is it the best way ? https://issues.apache.org/bugzilla/show_bug.cgi?id=55634
Initial commit id: 24a091fb
Final commit id: 57b724e1
   Bugs between [       3]:
ee0c987ff Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
06f9cbcd7 Bug 57078 - LagartoBasedHTMLParser fails to parse page that contains input with no type Bugzilla Id: 57078
be4d1fe65 Bug 56772 - Handle IE Conditional comments when parsing embedded resources Bugzilla Id: 56772
   Bugs after [       3]:
1e4a1ca55 Bug 60842 - jmeter chokes on newline Optimize by using static Pattern Factor our code in base class Use code in JSoup based implementation Add Junit tests for it Bugzilla Id: 60842
3ff0e6095 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution
b93b3328d Bug 59033 - Parallel Download : Rework Parser classes hierarchy to allow pluging parsers for different mime types Bugzilla Id: 59033

Start block index: 145
End block index: 162
    public Iterator<URL> getEmbeddedResourceURLs(byte[] html, URL baseUrl,
            URLCollection coll, String encoding) throws HTMLParseException {
        try {
            String contents = new String(html,encoding);
            LagartoParser lagartoParser = new LagartoParser(contents);
            JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll);
            lagartoParser.parse(tagVisitor);
            return coll.iterator();
        } catch (LagartoException e) {
            // TODO is it the best way ? https://issues.apache.org/bugzilla/show_bug.cgi?id=55634
            if(log.isDebugEnabled()) {
                log.debug("Error extracting embedded resource URLs from:'"+baseUrl+"', probably not text content, message:"+e.getMessage());
            }
            return Collections.<URL>emptyList().iterator();
        } catch (Exception e) {
            throw new HTMLParseException(e);
        }
    }

*********************** Method when SATD was removed **************************

    @Override
    public Iterator<URL> getEmbeddedResourceURLs(String userAgent, byte[] html, URL baseUrl,
            URLCollection coll, String encoding) throws HTMLParseException {
        try {
            Float ieVersion = extractIEVersion(userAgent);

            String contents = new String(html,encoding);
            // As per Jodd javadocs, emitStrings should be false for visitor for better performances
            LagartoParser lagartoParser = new LagartoParser(contents, false);
            LagartoParserConfig<LagartoParserConfig<?>> config = new LagartoParserConfig<LagartoParserConfig<?>>();
            config.setCaseSensitive(false);
            // Conditional comments only apply for IE < 10
            config.setEnableConditionalComments(isEnableConditionalComments(ieVersion));

            lagartoParser.setConfig(config);
            JMeterTagVisitor tagVisitor = new JMeterTagVisitor(new URLPointer(baseUrl), coll, ieVersion);
            lagartoParser.parse(tagVisitor);
            return coll.iterator();
        } catch (LagartoException e) {
            // TODO is it the best way ? https://bz.apache.org/bugzilla/show_bug.cgi?id=55634
            if(log.isDebugEnabled()) {
                log.debug("Error extracting embedded resource URLs from:'"+baseUrl+"', probably not text content, message:"+e.getMessage());
            }
            return Collections.<URL>emptyList().iterator();
        } catch (Exception e) {
            throw new HTMLParseException(e);
        }
    }
