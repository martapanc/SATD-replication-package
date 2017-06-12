File path: src/main/org/apache/tools/ant/taskdefs/AntStructure.java
Comment: XXX - we are committing CombiningChar and Extender here
Initial commit id: 74f58bf0
Final commit id: 13000c1a
   Bugs between [       4]:
bd52e7b9b allow targets to deal with missing extension points.  PR 49473.  Submitted by Danny Yates.
11b928d06 Avoid ConcurrentModificationException when iteratong over life-maps.  PR 48310
32f2e37a9 JDK 1.2 is EOL and documentation is no more available. Point to JDK 5 API PR: 37203
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 364
End block index: 375
    protected boolean isNmtoken(String s) {
        final int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            // XXX - we are committing CombiningChar and Extender here
            if (!Character.isLetterOrDigit(c)
                && c != '.' && c != '-' && c != '_' && c != ':') {
                return false;
            }
        }
        return true;
    }
