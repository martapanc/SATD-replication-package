    public void createTest(BasicAttributes basicattributes, String string) throws NamingException {
        dirContext.createSubcontext(string, basicattributes);
    }
