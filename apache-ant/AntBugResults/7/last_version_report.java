    private static String getNamespaceURI(Node n) {
        String uri = n.getNamespaceURI();
        return uri == null ? "" : uri;
    }
