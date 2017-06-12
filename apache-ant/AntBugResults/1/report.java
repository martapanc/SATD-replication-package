File path: src/main/org/apache/tools/ant/taskdefs/cvslib/ChangeLogParser.java
Comment: FIXME formatters are not thread-safe
Initial commit id: ce9af826
Final commit id: 8e56f57a
   Bugs between [       1]:
80d45d8db add remote option to use rlog instead of log to cvschangelog.  Submitted by Rob van Oostrum.  PR 27419.
   Bugs after [       0]:


Start block index: 37
End block index: 53

// FIXME formatters are not thread-safe

    /** input format for dates read in from cvs log */
    private static final SimpleDateFormat INPUT_DATE
        = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
    /**
     * New formatter used to parse CVS date/timestamp.
     */
    private static final SimpleDateFormat CVS1129_INPUT_DATE =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);

    static {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        INPUT_DATE.setTimeZone(utc);
        CVS1129_INPUT_DATE.setTimeZone(utc);
    }
