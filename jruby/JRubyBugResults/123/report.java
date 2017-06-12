File path: core/src/main/java/org/jruby/runtime/load/LibrarySearcher.java
Comment: / we're depending on the side effect of the load
Initial commit id: 613673ae
Final commit id: 066aa2fe
   Bugs between [       3]:
e422b1f86f Fixes #1940
8c31952769 Fixes #1936
a10942ea38 JRubyClassloader seems to have a problem with file urls pointing to jar within a jar. converting them into jar:file: do work - fixes #1850
   Bugs after [       4]:
ba74846a70 classpath: URLs are always absolute. Fixes #4188.
d4cbfd2d8c Add classpath:/ as an absolute path prefix. See #4000
1494436ea6 Get AOT-compiled scripts loading properly again. Fixes #3018
de65120771 Fixes #3055. Cannot delete file after requiring it on Windows in 9.0.0.0.rc1

Start block index: 206
End block index: 215
    private void loadClass(Ruby runtime, boolean wrap) {
      Script script = CompiledScriptLoader.loadScriptFromFile(runtime, is, searchName);
      if (script == null) {
        // we're depending on the side effect of the load, which loads the class but does not turn it into a script
        // I don't like it, but until we restructure the code a bit more, we'll need to quietly let it by here.
        return;
      }
      script.setFilename(scriptName);
      runtime.loadScript(script, wrap);
    }
