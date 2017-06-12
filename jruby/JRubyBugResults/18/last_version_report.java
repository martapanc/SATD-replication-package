    public JavaSupport(Ruby ruby) {
        this.runtime = ruby;
        
        try {
            this.proxyCache = PROXY_CACHE_CONSTRUCTOR.newInstance(ruby);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
