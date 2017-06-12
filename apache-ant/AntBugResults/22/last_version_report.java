    public String substitute(String input, String argument, int options)
        throws BuildException {
        // translate \1 to $1 so that the Perl5Substitution will work
        StringBuffer subst = new StringBuffer();
        for (int i = 0; i < argument.length(); i++) {
            char c = argument.charAt(i);
            if (c == '$') {
                subst.append('\\');
                subst.append('$');
            } else if (c == '\\') {
                if (++i < argument.length()) {
                    c = argument.charAt(i);
                    int value = Character.digit(c, DECIMAL);
                    if (value > -1) {
                        subst.append("$").append(value);
                    } else {
                        subst.append(c);
                    }
                } else {
                    // TODO - should throw an exception instead?
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
