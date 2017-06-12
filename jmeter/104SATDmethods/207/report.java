File path: src/protocol/mail/org/apache/jmeter/protocol/smtp/sampler/SmtpSampler.java
Comment: TODO this should really be request data
Initial commit id: fe02a3a9
Final commit id: aa7d5dba
   Bugs between [       8]:
a44c6efe5 Bug 53039 - HTTP Request : Be able to handle responses which size exceeds 2147483647 bytes Fix deprecated calls to setBytes(int) Bugzilla Id: 53039
74885f034 Bug 53042 - Introduce a new Interface to be implemented by AbstractSampler to allow Sampler to decide wether a config element applies to Sampler
fed4b33a8 Bug 53027 - Jmeter starts throwing exceptions while using SMTP Sample in a test plan with HTTP Cookie Mngr or HTTP Request Defaults
67fc58bb9 Bug 49862 - Improve SMTPSampler Request output.
a3d623b63 Bug 49775 - Allow sending messages without a body
91e79e3ed Bug 49603 - Allow accepting expired certificates on Mail Reader Sampler
270ae7482 Bug 49622 - Allow sending messages without a subject (SMTP Sampler)
cf33f2724 Bug 49552 - Add Message Headers on SMTPSampler
   Bugs after [       1]:
72cb7ea99 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution

Start block index: 98
End block index: 345
    public SampleResult sample(Entry e) {
        Message message = null;
        SampleResult res = new SampleResult();
        res.setSampleLabel(getName());
        boolean isOK = false; // Did sample succeed?
        SendMailCommand instance = new SendMailCommand();
        instance.setSmtpServer(getPropertyAsString(SmtpSampler.SERVER));
        instance.setSmtpPort(getPropertyAsString(SmtpSampler.SERVER_PORT));

        instance.setUseSSL(getPropertyAsBoolean(USE_SSL));
        instance.setUseStartTLS(getPropertyAsBoolean(USE_STARTTLS));
        instance.setTrustAllCerts(getPropertyAsBoolean(SSL_TRUST_ALL_CERTS));
        instance.setEnforceStartTLS(getPropertyAsBoolean(ENFORCE_STARTTLS));

        instance.setUseAuthentication(getPropertyAsBoolean(USE_AUTH));
        instance.setUsername(getPropertyAsString(USERNAME));
        instance.setPassword(getPropertyAsString(PASSWORD));

        instance.setUseLocalTrustStore(getPropertyAsBoolean(USE_LOCAL_TRUSTSTORE));
        instance.setTrustStoreToUse(getPropertyAsString(TRUSTSTORE_TO_USE));
        instance.setEmlMessage(getPropertyAsString(EML_MESSAGE_TO_SEND));
        instance.setUseEmlMessage(getPropertyAsBoolean(USE_EML));

        if (getMailFrom().matches(".*@.*")) {
            instance.setSender(getMailFrom());
        }

        try {
            if (!getPropertyAsBoolean(USE_EML)) { // part is only needed if we
                // don't send an .eml-file

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                List<InternetAddress> receiversTo = new Vector<InternetAddress>();
                if (getPropertyAsString(SmtpSampler.RECEIVER_TO).matches(".*@.*")) {
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_TO))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversTo.add(new InternetAddress(strReceivers[i].trim()));
                    }
                } else {
                    receiversTo.add(new InternetAddress(getMailFrom()));
                }

                instance.setReceiverTo(receiversTo);

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_CC).matches(".*@.*")) {
                    List<InternetAddress> receiversCC = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_CC))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversCC.add(new InternetAddress(strReceivers[i].trim()));
                    }
                    instance.setReceiverCC(receiversCC);
                }

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_BCC).matches(".*@.*")) {
                    List<InternetAddress> receiversBCC = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_BCC))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversBCC.add(new InternetAddress(strReceivers[i].trim()));
                    }
                    instance.setReceiverBCC(receiversBCC);
                }

                MailBodyProvider mb = new MailBodyProvider();
                if (getPropertyAsString(MESSAGE) != null
                        && !getPropertyAsString(MESSAGE).equals(""))
                    mb.setBody(getPropertyAsString(MESSAGE));
                instance.setMbProvider(mb);

                if (!getAttachments().equals("")) {
                    String[] attachments = getAttachments().split(FILENAME_SEPARATOR);
                    for (String attachment : attachments) {
                        instance.addAttachment(new File(attachment));
                    }
                }

                instance.setSubject(getPropertyAsString(SUBJECT)
                                + (getPropertyAsBoolean(INCLUDE_TIMESTAMP) ? " <<< current timestamp: "
                                        + new Date().getTime() + " >>>"
                                        : ""));
            } else {

                // send an .eml-file

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_TO).matches(".*@.*")) {
                    List<InternetAddress> receiversTo = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_TO))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversTo.add(new InternetAddress(strReceivers[i].trim()));
                    }
                    instance.setReceiverTo(receiversTo);
                }

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_CC).matches(".*@.*")) {
                    List<InternetAddress> receiversCC = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_CC))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversCC.add(new InternetAddress(strReceivers[i].trim()));
                    }
                    instance.setReceiverCC(receiversCC);
                }

                // check if there are really mail-addresses in the fields and if
                // there are multiple ones
                if (getPropertyAsString(SmtpSampler.RECEIVER_BCC).matches(
                        ".*@.*")) {
                    List<InternetAddress> receiversBCC = new Vector<InternetAddress>();
                    String[] strReceivers = (getPropertyAsString(SmtpSampler.RECEIVER_BCC))
                            .split(";");
                    for (int i = 0; i < strReceivers.length; i++) {
                        receiversBCC.add(new InternetAddress(strReceivers[i]
                                .trim()));
                    }
                    instance.setReceiverBCC(receiversBCC);
                }

                String subj = getPropertyAsString(SUBJECT);
                if (subj.trim().length() > 0) {
                    instance.setSubject(subj
                            + (getPropertyAsBoolean(INCLUDE_TIMESTAMP) ? " <<< current timestamp: "
                                    + new Date().getTime() + " >>>"
                                    : ""));
                }
            }
            // needed for measuring sending time
            instance.setSynchronousMode(true);

            message = instance.prepareMessage();

            if (getPropertyAsBoolean(MESSAGE_SIZE_STATS)) {
                // calculate message size
                CounterOutputStream cs = new CounterOutputStream();
                message.writeTo(cs);
                res.setBytes(cs.getCount());
            } else {
                res.setBytes(-1);
            }

        } catch (AddressException ex) {
            log.warn("Error while preparing message", ex);
            return res;
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        } catch (MessagingException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        // Perform the sampling
        res.sampleStart();

        try {
            instance.execute(message);

            // Set up the sample result details
            res.setSamplerData("To: "
                    + getPropertyAsString(SmtpSampler.RECEIVER_TO) + "\nCC: "
                    + getPropertyAsString(SmtpSampler.RECEIVER_CC) + "\nBCC: "
                    + getPropertyAsString(SmtpSampler.RECEIVER_BCC));
            res.setDataType(SampleResult.TEXT);
            res.setResponseCodeOK();
            /*
             * TODO if(instance.getSMTPStatusCode == 250)
             * res.setResponseMessage("Message successfully sent!"); else
             * res.setResponseMessage(instance.getSMTPStatusCodeIncludingMessage);
             */
            res.setResponseMessage("Message successfully sent!\n"
                    + instance.getServerResponse());
            isOK = true;
        }
        // username / password incorrect
        catch (AuthenticationFailedException afex) {
            log.warn("", afex);
            res.setResponseCode("500");
            res.setResponseMessage("AuthenticationFailedException: authentication failed - wrong username / password!\n"
                            + afex);
        }
        // SSL not supported, startTLS not supported, other messagingException
        catch (MessagingException mex) {
            log.warn("",mex);
            res.setResponseCode("500");
            if (mex.getMessage().matches(
                    ".*Could not connect to SMTP host.*465.*")
                    && mex.getCause().getMessage().matches(
                            ".*Connection timed out.*")) {
                res.setResponseMessage("MessagingException: Probably, SSL is not supported by the SMTP-Server!\n"
                                + mex);
            } else if (mex.getMessage().matches(".*StartTLS failed.*")) {
                res.setResponseMessage("MessagingException: StartTLS not supported by server or initializing failed!\n"
                                + mex);
            } else if (mex.getMessage().matches(".*send command to.*")
                    && mex.getCause().getMessage().matches(
                                    ".*unable to find valid certification path to requested target.*")) {
                res.setResponseMessage("MessagingException: Server certificate not trusted - perhaps you have to restart JMeter!\n"
                                + mex);
            } else {
                res.setResponseMessage("Other MessagingException: "
                        + mex.toString());
            }
        }
        // general exception
        catch (Exception ex) {
            res.setResponseCode("500");
            if (null != ex.getMessage()
                    && ex.getMessage().matches("Failed to build truststore")) {
                res.setResponseMessage("Failed to build truststore - did not try to send mail!");
            } else {
                res.setResponseMessage("Other Exception: " + ex.toString());
            }
        }

        res.sampleEnd();

        try {
            // process the sampler result
            InputStream is = message.getInputStream();
            StringBuffer sb = new StringBuffer();
            byte[] buf = new byte[1024];
            int read = is.read(buf);
            while (read > 0) {
                sb.append(new String(buf, 0, read));
                read = is.read(buf);
            }
            res.setResponseData(sb.toString().getBytes()); // TODO this should really be request data, but there is none
        } catch (IOException ex) {
            log.warn("",ex);
        } catch (MessagingException ex) {
            log.warn("",ex);
        }

        res.setSuccessful(isOK);

        return res;
    }

*********************** Method when SATD was removed **************************

    @Override
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
