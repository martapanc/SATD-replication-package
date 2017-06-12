File path: src/org/jruby/ext/Readline.java
Comment: / This is for JRUBY-2988
Initial commit id: 0f9880c5
Final commit id: a00e2780
   Bugs between [       3]:
3487bba2fa Fixed JRUBY-4116 and JRUBY-3950 (IRB in Applet and WebStart environments).
703b764c7f Fix for JRUBY-3420: Added basic_word_break_characters method to Readline
a020c04888 Fix up a problem with my re-initializing logic for JRUBY-2988; only reinitialize if we have the bad IOException.
   Bugs after [       2]:
4e6078d711 Fix JRUBY-6623: In IRB, backslashes are gobbled in the eval loop Prevent jline2 from expanding \\ into commands. This may prevent some useful expansions, but if it does, we will deal with it later.
db5cdb6964 Fix JRUBY-6605. jline.console.history.MemoryHistory$EntryImpl now overrides toString().

Start block index: 204
End block index: 242
    public static IRubyObject s_readline(ThreadContext context, IRubyObject recv, IRubyObject prompt, IRubyObject add_to_hist) throws IOException {
        Ruby runtime = context.getRuntime();
        ConsoleHolder holder = getHolder(runtime);
        if (holder.readline == null) {
            initReadline(runtime, holder); // not overridden, let's go
        }
        
        IRubyObject line = runtime.getNil();
        String v = null;
        while (true) {
            try {
                holder.readline.getTerminal().disableEcho();
                v = holder.readline.readLine(prompt.toString());
                break;
            } catch (IOException ioe) {
                if (RubyIO.restartSystemCall(ioe)) {
                    continue;
                }
                throw runtime.newIOErrorFromException(ioe);
            } finally {
                // This is for JRUBY-2988, since after a suspend the terminal seems
                // to need to be reinitialized. Since we can't easily detect suspension,
                // initialize after every readline. Probably not fast, but this is for
                // interactive terminals anyway...so who cares?
                try {holder.readline.getTerminal().initializeTerminal();} catch (Exception e) {}
                holder.readline.getTerminal().enableEcho();
            }
        }
        
        if (null != v) {
            if (add_to_hist.isTrue()) {
                holder.readline.getHistory().addToHistory(v);
            }

            /* Explicitly use UTF-8 here. c.f. history.addToHistory using line.asUTF8() */
            line = RubyString.newUnicodeString(recv.getRuntime(), v);
        }
        return line;
    }
