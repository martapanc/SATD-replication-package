File path: proposal/myrmidon/src/todo/org/apache/tools/ant/taskdefs/JikesOutputParser.java
Comment: or will cause some trouble. The parser should definitely
Initial commit id: d1064dea
Final commit id: 58c82aeb
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 136
End block index: 172
    private void parseStandardOutput( BufferedReader reader )
        throws IOException
    {
        String line;
        String lower;
        // We assume, that every output, jike does, stands for an error/warning
        // XXX
        // Is this correct?

        // TODO:
        // A warning line, that shows code, which contains a variable
        // error will cause some trouble. The parser should definitely
        // be much better.

        while( ( line = reader.readLine() ) != null )
        {
            lower = line.toLowerCase();
            if( line.trim().equals( "" ) )
                continue;
            if( lower.indexOf( "error" ) != -1 )
                setError( true );
            else if( lower.indexOf( "warning" ) != -1 )
                setError( false );
            else
            {
                // If we don't know the type of the line
                // and we are in emacs mode, it will be
                // an error, because in this mode, jikes won't
                // always print "error", but sometimes other
                // keywords like "Syntax". We should look for
                // all those keywords.
                if( emacsMode )
                    setError( true );
            }
            log( line );
        }
    }
