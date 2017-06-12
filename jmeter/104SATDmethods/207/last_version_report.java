    public SampleResult sample(Entry e) {
        SendMailCommand sendMailCmd;
        Message message;
        SampleResult result = createSampleResult();

        try {
            sendMailCmd = createSendMailCommandFromProperties();
            message = sendMailCmd.prepareMessage();
            result.setBytes(calculateMessageSize(message));
        } catch (Exception ex) {
            log.warn("Error while preparing message", ex);
            result.setResponseCode("500");
            result.setResponseMessage(ex.toString());
            return result;
        }

        // Set up the sample result details
        result.setDataType(SampleResult.TEXT);
        try {
            result.setRequestHeaders(getRequestHeaders(message));
            result.setSamplerData(getSamplerData(message));
        } catch (MessagingException | IOException ex) {
            result.setSamplerData("Error occurred trying to save request info: " + ex);
            log.warn("Error occurred trying to save request info", ex);
        }

        // Perform the sampling
        result.sampleStart();
        boolean isSuccessful = executeMessage(result, sendMailCmd, message);
        result.sampleEnd();

        try {
            result.setResponseData(processSampler(message));
        } catch (IOException | MessagingException ex) {
            log.warn("Failed to set result response data", ex);
        }

        result.setSuccessful(isSuccessful);

        return result;
    }
