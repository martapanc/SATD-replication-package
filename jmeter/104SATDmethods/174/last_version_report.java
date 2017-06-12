        public void warning(SAXParseException exception) throws SAXParseException {
            String msg = "warning: " + errorDetails(exception);
            log.debug(msg);
            result.setFailureMessage(msg);
        }
