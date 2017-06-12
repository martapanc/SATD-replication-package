File path: truffle/src/main/java/org/jruby/truffle/util/IdUtil.java
Comment: / FIXME: this should go somewhere more generic -- maybe IdUtil
Initial commit id: 92c1ab5a
Final commit id: cd313f38
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 159
End block index: 163
    // FIXME: this should go somewhere more generic -- maybe IdUtil
    public static final boolean isRubyVariable(String name) {
        char c;
        return name.length() > 0 && ((c = name.charAt(0)) == '@' || (c <= 'Z' && c >= 'A'));
    }
