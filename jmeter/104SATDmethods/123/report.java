File path: src/protocol/ldap/org/apache/jmeter/protocol/ldap/sampler/LdapClient.java
Comment: ODO perhaps return this?
Initial commit id: 6675e0d87
Final commit id: eb69c5eb8
   Bugs between [       1]:
dd30d6171 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
   Bugs after [       1]:
66cc6bc13 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution

Start block index: 183
End block index: 188
    public void createTest(BasicAttributes basicattributes, String string)
        throws NamingException
    {
    	//DirContext dc = //TODO perhaps return this?
        dirContext.createSubcontext(string, basicattributes);
    }

*********************** Method when SATD was removed **************************

public void createTest(BasicAttributes basicattributes, String string) throws NamingException {
    dirContext.createSubcontext(string, basicattributes);
}
