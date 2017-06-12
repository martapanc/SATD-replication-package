File path: src/org/jruby/runtime/CallbackFactory.java
Comment: I don't like it.
Initial commit id: b14d1b95
Final commit id: 4a833081
   Bugs between [       1]:
a78db1a702 Fix for JRUBY-1641 from Vladimir; safely check privileged system properties, since blowing up there kills JRuby.
   Bugs after [       0]:


Start block index: 167
End block index: 176
    public static CallbackFactory createFactory(Ruby runtime, Class type, ClassLoader classLoader) {
        if(reflection) {
            return new ReflectionCallbackFactory(type);
        } else if(dumping) {
            return new DumpingInvocationCallbackFactory(runtime, type, dumpingPath);
        } else {
            // FIXME: No, I don't like it.
            return new InvocationCallbackFactory(runtime, type, (JRubyClassLoader)classLoader);
        }
    }
