File path: src/reports/org/apache/jmeter/testelement/JTLData.java
Comment: Method is broken anyway
Initial commit id: d5271a54
Final commit id: faf5bc05
   Bugs between [       2]:
f023972db Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
5a32848af Bug 54199 - Move to Java 6 add @Override Bugzilla Id: 54199
   Bugs after [       0]:


Start block index: 81
End block index: 92
    @SuppressWarnings("unchecked") // Method is broken anyway
    public List getStats(List urls) {
        ArrayList items = new ArrayList();
        Iterator itr = urls.iterator();
        if (itr.hasNext()) {
            SamplingStatCalculator row = (SamplingStatCalculator)itr.next();
            if (row != null) {
                items.add(row);
            }
        }
        return items;
    }
