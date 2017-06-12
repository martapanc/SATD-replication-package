public static IRubyObject s_readline(ThreadContext context, IRubyObject recv, IRubyObject prompt, IRubyObject add_to_hist) {
    Ruby runtime = context.getRuntime();
    ConsoleHolder holder = getHolder(runtime);
    if (holder.readline == null) {
        initReadline(runtime, holder); // not overridden, let's go
    }

    IRubyObject line = runtime.getNil();
    String v = null;
    while (true) {
        try {
            holder.readline.getTerminal().setEchoEnabled(false);
            v = holder.readline.readLine(prompt.toString());
            break;
        } catch (IOException ioe) {
            throw runtime.newIOErrorFromException(ioe);
        } finally {
            holder.readline.getTerminal().setEchoEnabled(true);
        }
    }

    if (null != v) {
        if (add_to_hist.isTrue()) {
            holder.readline.getHistory().add(v);
        }

        // Enebo: This is a little weird and a little broken.  We just ask
        // for the bytes and hope they match default_external.  This will
        // work for common cases, but not ones in which the user explicitly
        // sets the default_external to something else.  The second problem
        // is that no al M17n encodings are valid encodings in java.lang.String.
        // We clearly need a byte[]-version of JLine since we cannot totally
        // behave properly using Java Strings.
        if (runtime.is1_9()) {
            ByteList list = new ByteList(v.getBytes(), runtime.getDefaultExternalEncoding());
            line = RubyString.newString(runtime, list);
        } else {
            /* Explicitly use UTF-8 here. c.f. history.addToHistory using line.asUTF8() */
            line = RubyString.newUnicodeString(recv.getRuntime(), v);
        }
    }
    return line;
}
