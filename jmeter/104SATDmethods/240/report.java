File path: src/components/org/apache/jmeter/extractor/XPathExtractor.java
Comment: TODO is this correct?
Initial commit id: 3339b2e5
Final commit id: c3ba7772
   Bugs between [       8]:
c3ba77727 Bug 43294 - XPath Extractor namespace problems First part: Put in XPathUtil everything related to XPath computations
524e51555 Bug 51876 - Functionnality to search in Samplers TreeView Changed implementation to: - Add ability to search with regexp - Add ability to search in case sensitive and insentive modes - Plug additional search implementations
6572ccd24 Bug 51876 - Functionnality to search in Samplers TreeView
30860c40e Bug 51876 - Functionnality to search in Samplers TreeView
de4b08524 Bug 51885 - Allow a JMeter Variable as input to XPathExtractor part 2 of fix
2d9559c43 Bug 48331 - XpathExtractor does not return XML string representations for a Nodeset
a447f2272 Bug 47338 - XPath Extractor forces retrieval of document DTD
b436cd1b7 Bug 43382 - configure Tidy output (warnings, errors) for XPath Assertion and Post-Processor
   Bugs after [       6]:
7ebfa083a Bug 60564 - Migrating LogKit to SLF4J - check log level in non-error logging if method invoked in params Contributed by Woonsan Ko This closes #267 Bugzilla Id: 60564
9033e6208 Bug 60710 - XPath Extractor : When content on which assertion applies is not XML, in View Results Tree the extractor is marked in Red and named SAXParseException Bugzilla Id: 60710
b1cca8cc9 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution Contributed by Woonsan Ko This closes #264 Bugzilla Id: 60564
c53bafaee Bug 60602 - XPath Extractor : Add Match No. to allow extraction randomly, by index or all matches Bugzilla Id: 60602
4a087408e Bug 55694 - Assertions and Extractors : Avoid NullPointerException when scope is variable and variable is missing Bugzilla Id: 55694
e1c5c20a4 Bug 54129 - Search Feature does not find text although existing in elements Bugzilla Id: 54129

Start block index: 185
End block index: 222
    private void getValuesForXPath(Document d,String query, JMeterVariables vars, String refName)
     throws TransformerException
    {
        String val = null;
     	XObject xObject = XPathAPI.eval(d, query);
        if (xObject.getType() == XObject.CLASS_NODESET) {
	        NodeList matches = xObject.nodelist();
			int length = matches.getLength();
	        vars.put(concat(refName,MATCH_NR), String.valueOf(length));
	        for (int i = 0 ; i < length; i++) {
	            Node match = matches.item(i);
				if ( match instanceof Element){
				// elements have empty nodeValue, but we are usually interested in their content
				   final Node firstChild = match.getFirstChild();
				   if (firstChild != null) {
					   val = firstChild.getNodeValue();
				   } else {
					   val = match.getNodeValue(); // TODO is this correct?
				   }
				} else {
				   val = match.getNodeValue();
				}
	            if ( val!=null){
	                if (i==0) {// Treat 1st match specially
	                    vars.put(refName,val);
	                }
	                vars.put(concat(refName,String.valueOf(i+1)),val);
	            }
			}
	        vars.remove(concat(refName,String.valueOf(length+1)));
     	} else {
	        val = xObject.toString();
	        vars.put(concat(refName, MATCH_NR), "1");
	        vars.put(refName, val);
	        vars.put(concat(refName, "1"), val);
	        vars.remove(concat(refName, "2"));
	    }
    }

*********************** Method when SATD was removed **************************

    private void getValuesForXPath(Document d,String query, List<String> matchStrings)
        throws TransformerException {
    	XPathUtil.putValuesForXPathInList(d, query, matchStrings, getFragment());
    }
