    public SampleResult sample(Entry e) {
        SampleResult parent = new SampleResult();
        boolean isOK = false; // Did sample succeed?
        final boolean deleteMessages = getDeleteMessages();
        final String serverProtocol = getServerType();

        parent.setSampleLabel(getName());

        String samplerString = toString();
        parent.setSamplerData(samplerString);

        /*
         * Perform the sampling
         */
        parent.sampleStart(); // Start timing
        try {
            // Create empty properties
            Properties props = new Properties();

            if (isUseStartTLS()) {
                props.setProperty(mailProp(serverProtocol, "starttls.enable"), TRUE);  // $NON-NLS-1$
                if (isEnforceStartTLS()){
                    // Requires JavaMail 1.4.2+
                    props.setProperty(mailProp(serverProtocol, "starttls.require"), TRUE);  // $NON-NLS-1$
                }
            }

            if (isTrustAllCerts()) {
                if (isUseSSL()) {
                    props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.class"), TRUST_ALL_SOCKET_FACTORY);  // $NON-NLS-1$
                    props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                } else if (isUseStartTLS()) {
                    props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.class"), TRUST_ALL_SOCKET_FACTORY);  // $NON-NLS-1$
                    props.setProperty(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                }
            } else if (isUseLocalTrustStore()){
                File truststore = new File(getTrustStoreToUse());
                log.info("load local truststore - try to load truststore from: "+truststore.getAbsolutePath());
                if(!truststore.exists()){
                    log.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath());
                    truststore = new File(FileServer.getFileServer().getBaseDir(), getTrustStoreToUse());
                    log.info("load local truststore -Attempting to read truststore from:  "+truststore.getAbsolutePath());
                    if(!truststore.exists()){
                        log.info("load local truststore -Failed to load truststore from: "+truststore.getAbsolutePath() + ". Local truststore not available, aborting execution.");
                        throw new IOException("Local truststore file not found. Also not available under : " + truststore.getAbsolutePath());
                    }
                }
                if (isUseSSL()) {
                    // Requires JavaMail 1.4.2+
                    props.put(mailProp(serverProtocol, "ssl.socketFactory"),   // $NON-NLS-1$ 
                            new LocalTrustStoreSSLSocketFactory(truststore));
                    props.put(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                } else if (isUseStartTLS()) {
                    // Requires JavaMail 1.4.2+
                    props.put(mailProp(serverProtocol, "ssl.socketFactory"),  // $NON-NLS-1$
                            new LocalTrustStoreSSLSocketFactory(truststore));
                    props.put(mailProp(serverProtocol, "ssl.socketFactory.fallback"), FALSE);  // $NON-NLS-1$
                }
            }

            // Get session
            Session session = Session.getInstance(props, null);

            // Get the store
            Store store = session.getStore(serverProtocol);
            store.connect(getServer(), getPortAsInt(), getUserName(), getPassword());

            // Get folder
            Folder folder = store.getFolder(getFolder());
            if (deleteMessages) {
                folder.open(Folder.READ_WRITE);
            } else {
                folder.open(Folder.READ_ONLY);
            }

            final int messageTotal = folder.getMessageCount();
            int n = getNumMessages();
            if (n == ALL_MESSAGES || n > messageTotal) {
                n = messageTotal;
            }

            // Get directory
            Message messages[] = folder.getMessages(1,n);
            StringBuilder pdata = new StringBuilder();
            pdata.append(messages.length);
            pdata.append(" messages found\n");
            parent.setResponseData(pdata.toString(),null);
            parent.setDataType(SampleResult.TEXT);
            parent.setContentType("text/plain"); // $NON-NLS-1$

            final boolean headerOnly = getHeaderOnly();
            busy = true;
            for (Message message : messages) {
                StringBuilder cdata = new StringBuilder();
                SampleResult child = new SampleResult();
                child.sampleStart();

                cdata.append("Message "); // $NON-NLS-1$
                cdata.append(message.getMessageNumber());
                child.setSampleLabel(cdata.toString());
                child.setSamplerData(cdata.toString());
                cdata.setLength(0);

                final String contentType = message.getContentType();
                child.setContentType(contentType);// Store the content-type
                child.setDataEncoding(RFC_822_DEFAULT_ENCODING); // RFC 822 uses ascii per default
                child.setEncodingAndType(contentType);// Parse the content-type

                if (isStoreMimeMessage()) {
                    // Don't save headers - they are already in the raw message
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    message.writeTo(bout);
                    child.setResponseData(bout.toByteArray()); // Save raw message
                    child.setDataType(SampleResult.TEXT);
                } else {
                    @SuppressWarnings("unchecked") // Javadoc for the API says this is OK
                    Enumeration<Header> hdrs = message.getAllHeaders();
                    while(hdrs.hasMoreElements()){
                        Header hdr = hdrs.nextElement();
                        String value = hdr.getValue();
                        try {
                            value = MimeUtility.decodeText(value);
                        } catch (UnsupportedEncodingException uce) {
                            // ignored
                        }
                        cdata.append(hdr.getName()).append(": ").append(value).append("\n");
                    }
                    child.setResponseHeaders(cdata.toString());
                    cdata.setLength(0);
                    if (!headerOnly) {
                        appendMessageData(child, message);
                    }
                }

                if (deleteMessages) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }
                child.setResponseOK();
                if (child.getEndTime()==0){// Avoid double-call if addSubResult was called.
                    child.sampleEnd();
                }
                parent.addSubResult(child);
            }

            // Close connection
            folder.close(true);
            store.close();

            parent.setResponseCodeOK();
            parent.setResponseMessageOK();
            isOK = true;
        } catch (NoClassDefFoundError ex) {
            log.debug("",ex);// No need to log normally, as we set the status
            parent.setResponseCode("500"); // $NON-NLS-1$
            parent.setResponseMessage(ex.toString());
        } catch (MessagingException ex) {
            log.debug("", ex);// No need to log normally, as we set the status
            parent.setResponseCode("500"); // $NON-NLS-1$
            parent.setResponseMessage(ex.toString() + "\n" + samplerString); // $NON-NLS-1$
        } catch (IOException ex) {
            log.debug("", ex);// No need to log normally, as we set the status
            parent.setResponseCode("500"); // $NON-NLS-1$
            parent.setResponseMessage(ex.toString());
        } finally {
            busy = false;
        }

        if (parent.getEndTime()==0){// not been set by any child samples
            parent.sampleEnd();
        }
        parent.setSuccessful(isOK);
        return parent;
    }
