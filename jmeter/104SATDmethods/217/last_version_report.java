public void warning(SAXParseException exception) throws SAXParseException {
	msg = "warning: " + errorDetails(exception);
	log.debug(msg);
	messageType = JOptionPane.WARNING_MESSAGE;
}
