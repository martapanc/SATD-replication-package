File path: proposal/embed/src/java/org/apache/tools/ant/PropertyHelper.java
Comment: This is used to support ant call and similar tasks. It should be
Initial commit id: 87912cbc
Final commit id: 6ecafdd4
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 469
End block index: 494
    // This is used to support ant call and similar tasks. It should be
    // deprecated, it is possible to use a better ( more efficient )
    // mechanism to preserve the context.

    // TODO: do we need to delegate ?

    /**
     * Returns a copy of the properties table.
     * @return a hashtable containing all properties
     *         (including user properties).
     */
    public Hashtable getProperties() {
        Hashtable propertiesCopy = new Hashtable();

        Enumeration e = properties.keys();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            Object value = properties.get(name);
            propertiesCopy.put(name, value);
        }

        // There is a better way to save the context. This shouldn't
        // delegate to next, it's for backward compat only.

        return propertiesCopy;
    }
