File path: src/main/org/apache/tools/ant/util/RegexpPatternMapper.java
Comment: XXX - should throw an exception instead?
Initial commit id: e4f0795f
Final commit id: 13000c1a
   Bugs between [       3]:
3f609f977 explicitly guard against nul values, mark to and from attributes as required.  PR 44790.
ae0dd4dca add casesensitive and handledirchar to globmapper and regexpmapper PR: 16686 and 32487
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 115
End block index: 137
    protected String replaceReferences(String source) {
        Vector v = reg.getGroups(source);
        
        result.setLength(0);
        for (int i=0; i<to.length; i++) {
            if (to[i] == '\\') {
                if (++i < to.length) {
                    int value = Character.digit(to[i], 10);
                    if (value > -1) {
                        result.append((String) v.elementAt(value));
                    } else {
                        result.append(to[i]);
                    }
                } else {
                    // XXX - should throw an exception instead?
                    result.append('\\');
                }
            } else {
                result.append(to[i]);
            }
        }
        return result.toString();
    }
