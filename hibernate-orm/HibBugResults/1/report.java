File path: cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
Comment: ME Per the RegionFactory class Javadoc
Initial commit id: c49ef2e2
Final commit id: d2c88d55
   Bugs between [       1]:
d2c88d55df HHH-5647 - Develop release process using Gradle
   Bugs after [       0]:


Start block index: 42
End block index: 49
     * FIXME Per the RegionFactory class Javadoc, this constructor version
     * should not be necessary.
     * 
     * @param props The configuration properties
     */
    public JBossCacheRegionFactory(Properties props) {
        super(props);
    }
