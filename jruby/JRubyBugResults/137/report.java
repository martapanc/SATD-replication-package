File path: src/org/jruby/javasupport/Java.java
Comment: houldn't happen. TODO: might want to throw exception instead.
Initial commit id: 7e8f9604
Final commit id: 1dbbe1a3
   Bugs between [       2]:
cb9ebb7286 JI cleanup: remove old hack/workaround for long-since-fixed JRUBY-948; provide underscore __aliases__ for methods removed from blank-slated JavaPackageModuleTemplate, to facilitate workarounds for issues like JRUBY-1086/JRUBY-1275 (IRB tab completion). All this code eventually needs to move to Java, but this should help in the interim.
33d8b80e93 Fix for JRUBY-1197: handle digits in Java method names gracefully when turning into non-camel-cased.
   Bugs after [      31]:
60de9aa097 Fix #238.
f38b8e5a35 Kinda sorta fix JRUBY-6674
066c2f03a0 Fix JRUBY-6721
6c3ca3f9f9 Fix JRUBY-6337.
bb9cd07534 Fix JRUBY-6076: Mixed-case Java package name not resolved
2c6781ef4e Fix JRUBY-928: Java arrays don't inherit from java.lang.Object in Rubified Java class hierarchy
5dc6595a29 Fix for JRUBY-5365. java.util.Map interface also had interface module like other interfaces.
71ad726cdb Bug fix for MapJavaProxy. This resolves JRUBY-5353 and JRUBY-5354.
f0ee41f4c3 Fix JRUBY-5196: NPE in initializer
1599d42320 Fix JRUBY-4897: assertion error with jruby
26f5d63fd5 Fix for JRUBY-4531: java_send for static methods
af6bc7fe16 Fixes JRUBY-3889: Java dependency errors are hidden using include_package to import some classes into a module
5249dc344d Fix for JRUBY-3891: JavaSupport.handleNativeException doesn't seem handle superclasses properly
2870a2459c Fix for JRUBY-3218: Import is sometimes confused by unquoted class name
0a5a79d8f4 Fix for JRUBY-1000: Illegal attempt to subclass a final Java class should fail when the subclass is defined
18ff811229 Fixes for JRUBY-3015: use absolute value of hash for interface impl and fix a hash-ordering issue in yaml test.
6104b21119 Re-fix JRUBY-2867: make all method finders use the scoring version and make the scoring version count exactness as a virtue.
df9488f632 For automatic closure conversion, only generate a new class as needed for a new proc + [interface1, ... interfaceN] combination, rather than a new one each time (leak) or every time (inefficient, even if generated into their own classloaders. Fix for JRUBY-2943.
1552059f21 Fix for JRUBY-2938, from Jim Menard.
18d03923f1 Likely fix for JRUBY-2680 and potential fix for JRUBY-2803, by porting the entirety of JavaInterfaceTemplate from Ruby to Java. The bulk of this module was the append_features logic, which was gigantic, involved multiple instance_evals, many method definitions, and a lot of back-and-forth to the Java side of things. Writing up some benchmarks momentarily.
dd7847f26d Fix for JRUBY-2882. Handle error messages related to constructors better
e465a56305 Really real fix for JRUBY-2867.
3fcc8c6f47 Fix for JRUBY-2867, selecting wrong primitive Java method. Also some basic logic for selecting the correct target primitive method based on the required precision of the incoming value. For example, a Fixnum in byte range will try to call byte methods first.
d9d1d26182 Fix for JRUBY-2480, make sure Ruby objects that can't be coerced to a Java type are not being wrapped in JavaObject.
0f7386ed2f Partial fix for JRUBY-2169: Round out methods in JI wrappers, and add support for 1.5 methods (for annotations, etc.).  Still more to do for JavaProxyConstructor and JavaProxyMethod (i.e., JRuby-defined subclass methods and ctors).  Includes some unit tests, could use more.
b28e70f76f Fix for JRUBY-2171, too verbose logging in verbose mode for normal package searching.
7a9a8427af Fix for JRUBY-2171; normal JI package searching logs exceptions in verbose mode.
5fcc772271 Applying Vladimir's fix for JRUBY-2106 to a couple of other spots (top-level Java package references, as in Java::boom or Java::Boom).  Regression tests included.
7d9d9f3c9a Fix for JRUBY-199 (and related JRUBY-1513 and JRUBY-1735). Caches proxies in Weak/Concurrent Map-like cache, which replaces the ineffectual JavaObject cache.  TODO: unit tests, measure memory/performance impact.
47fc3bf1eb Fix for JRUBY-1468, fixes the original issue, adds a whole raft of tests, and fixes a few additional issues discovered.
9b1ae82293 Fix for JRUBY-1043, make parameter cache in Java proxy instance concurrent.

Start block index: 120
End block index: 133
        public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
            // shouldn't happen, but if a superclass is specified, it's not ours
            if (superClazz != null) {
                return null;
            }
            IRubyObject packageName = pkg.getInstanceVariable("@package_name");
            // again, shouldn't happen. TODO: might want to throw exception instead.
            if (packageName == null) return null;

            Ruby runtime = pkg.getRuntime();
            String className = packageName.asSymbol() + name;
            JavaClass javaClass = JavaClass.forName(runtime, className);
            return (RubyClass)get_proxy_class(runtime.getJavaSupport().getJavaUtilitiesModule(), javaClass);
        }
