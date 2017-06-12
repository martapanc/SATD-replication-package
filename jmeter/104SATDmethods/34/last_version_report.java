    public AssertionResult getResult(SampleResult inResponse) {
        log.debug("HTMLAssertions.getResult() called");

        // no error as default
        AssertionResult result = new AssertionResult(getName());

        if (inResponse.getResponseData().length == 0) {
            return result.setResultForNull();
        }

        result.setFailure(false);

        // create parser
        Tidy tidy = null;
        try {
            if (log.isDebugEnabled()){
                log.debug("HTMLAssertions.getResult(): Setup tidy ...");
                log.debug("doctype: " + getDoctype());
                log.debug("errors only: " + isErrorsOnly());
                log.debug("error threshold: " + getErrorThreshold());
                log.debug("warning threshold: " + getWarningThreshold());
                log.debug("html mode: " + isHTML());
                log.debug("xhtml mode: " + isXHTML());
                log.debug("xml mode: " + isXML());
            }
            tidy = new Tidy();
            tidy.setInputEncoding(StandardCharsets.UTF_8.name());
            tidy.setOutputEncoding(StandardCharsets.UTF_8.name());
            tidy.setQuiet(false);
            tidy.setShowWarnings(true);
            tidy.setOnlyErrors(isErrorsOnly());
            tidy.setDocType(getDoctype());
            if (isXHTML()) {
                tidy.setXHTML(true);
            } else if (isXML()) {
                tidy.setXmlTags(true);
            }
            tidy.setErrfile(getFilename());

            if (log.isDebugEnabled()) {
                log.debug("err file: " + getFilename());
                log.debug("getParser : tidy parser created - " + tidy);
                log.debug("HTMLAssertions.getResult(): Tidy instance created!");
            }

        } catch (Exception e) {
            log.error("Unable to instantiate tidy parser", e);
            result.setFailure(true);
            result.setFailureMessage("Unable to instantiate tidy parser");
            // return with an error
            return result;
        }

        /*
         * Run tidy.
         */
        try {
            log.debug("HTMLAssertions.getResult(): start parsing with tidy ...");

            StringWriter errbuf = new StringWriter();
            tidy.setErrout(new PrintWriter(errbuf));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            log.debug("Start : parse");
            Node node = tidy.parse(new ByteArrayInputStream(inResponse.getResponseData()), os);
            if (log.isDebugEnabled()) {
                log.debug("node : " + node);
                log.debug("End   : parse");
                log.debug("HTMLAssertions.getResult(): parsing with tidy done!");
                log.debug("Output: " + os.toString());
            }

            // write output to file
            writeOutput(errbuf.toString());

            // evaluate result
            if ((tidy.getParseErrors() > getErrorThreshold())
                    || (!isErrorsOnly() && (tidy.getParseWarnings() > getWarningThreshold()))) {
                if (log.isDebugEnabled()) {
                    log.debug("HTMLAssertions.getResult(): errors/warnings detected:");
                    log.debug(errbuf.toString());
                }
                result.setFailure(true);
                result.setFailureMessage(MessageFormat.format("Tidy Parser errors:   " + tidy.getParseErrors()
                        + " (allowed " + getErrorThreshold() + ") " + "Tidy Parser warnings: "
                        + tidy.getParseWarnings() + " (allowed " + getWarningThreshold() + ")", new Object[0]));
                // return with an error

            } else if ((tidy.getParseErrors() > 0) || (tidy.getParseWarnings() > 0)) {
                // return with no error
                log.debug("HTMLAssertions.getResult(): there were errors/warnings but threshold to high");
                result.setFailure(false);
            } else {
                // return with no error
                log.debug("HTMLAssertions.getResult(): no errors/warnings detected:");
                result.setFailure(false);
            }

        } catch (Exception e) {
            // return with an error
            log.warn("Cannot parse result content", e);
            result.setFailure(true);
            result.setFailureMessage(e.getMessage());
        }
        return result;
    }
