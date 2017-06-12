File path: src/main/org/apache/tools/ant/util/regexp/MatcherWrappedAsRegexp.java
Comment: XXX - should throw an exception instead?
Initial commit id: be53ecfd
Final commit id: b3154423
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 134
End block index: 158
    public String substitute(String input, String argument, int options)
        throws BuildException {
        Vector v = matcher.getGroups(input, options);
        
        StringBuffer result = new StringBuffer();
        char[] to = argument.toCharArray();
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
