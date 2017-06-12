File path: src/org/jruby/javasupport/JavaSupport.java
Comment: / FIXME: needs to be rethought
Initial commit id: 1dbbe1a3
Final commit id: 41d1cea1
   Bugs between [       8]:
47172e5d72 Fix JRUBY-5418: Getting NativeException: org.jruby.RubyContinuation$Continuation: null with RSpec 2 and EventMachine
406556bd3b Fix JRUBY-5417: Nil backtrace when overriding method_missing and calling super
c8d316738e Fix JRUBY-5351: Peculiar errors with failed java_import in different scenarios
5249dc344d Fix for JRUBY-3891: JavaSupport.handleNativeException doesn't seem handle superclasses properly
28a5d02dce Fix for JRUBY-1955: Use weak references instead of soft for ObjectProxyCache.  This reduces the usefulness of the interim fix for JRUBY-199 et al, which will await lightweights for the real solution.
0acbce99c1 Fix for JRUBY-1782 from Vladimir.
7d9d9f3c9a Fix for JRUBY-199 (and related JRUBY-1513 and JRUBY-1735). Caches proxies in Weak/Concurrent Map-like cache, which replaces the ineffectual JavaObject cache.  TODO: unit tests, measure memory/performance impact.
014ba11a30 More security fixes for applets, specifically to allow IRB to run again. For JRUBY-1762, from Vladimir.
   Bugs after [       1]:
a40de0ef64 Store the JavaProxyClass cache in JavaSupport (fixes #164)

Start block index: 67
End block index: 90
    private final ConcurrentHashMap<Class,JavaClass> javaClassCache =
        new ConcurrentHashMap<Class,JavaClass>(128);
    
    // FIXME: needs to be rethought
    private final Map matchCache = Collections.synchronizedMap(new HashMap(128));

    private Callback concreteProxyCallback;

    private RubyModule javaModule;
    private RubyModule javaUtilitiesModule;
    private RubyClass javaObjectClass;
    private RubyClass javaClassClass;
    private RubyClass javaArrayClass;
    private RubyClass javaProxyClass;
    private RubyModule javaInterfaceTemplate;
    private RubyModule packageModuleTemplate;
    private RubyClass arrayProxyClass;
    private RubyClass concreteProxyClass;
    
    
    public JavaSupport(Ruby ruby) {
        this.runtime = ruby;
        this.javaClassLoader = ruby.getJRubyClassLoader();
    }
