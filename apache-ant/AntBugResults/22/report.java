File path: src/main/org/apache/tools/ant/util/regexp/JakartaOroRegexp.java
Comment: XXX - should throw an exception instead?
Initial commit id: be53ecfd
Final commit id: 13000c1a
   Bugs between [       1]:
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
   Bugs after [       0]:


Start block index: 74
End block index: 109
    public String substitute(String input, String argument, int options)
        throws BuildException
    {
        // translate \1 to $1 so that the Perl5Substitution will work
        StringBuffer subst = new StringBuffer();
        for (int i=0; i<argument.length(); i++) {
            char c = argument.charAt(i);
            if (c == '\\') {
                if (++i < argument.length()) {
                    c = argument.charAt(i);
                    int value = Character.digit(c, 10);
                    if (value > -1) {
                        subst.append("$").append(value);
                    } else {
                        subst.append(c);
                    }
                } else {
                    // XXX - should throw an exception instead?
                    subst.append('\\');
                }
            } else {
                subst.append(c);
            }
        }
        

        // Do the substitution
        Substitution s = 
            new Perl5Substitution(subst.toString(), 
                                  Perl5Substitution.INTERPOLATE_ALL);
        return Util.substitute(matcher,
                               getCompiledPattern(options),
                               s,
                               input,
                               getSubsOptions(options));
    }
