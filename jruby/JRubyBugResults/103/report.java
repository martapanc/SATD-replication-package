File path: src/org/jruby/javasupport/JavaClass.java
Comment: / TODO: public only?
Initial commit id: beaaeb89
Final commit id: 48d04419
   Bugs between [      14]:
ae21cfc24b Move Java method alias creation into the initial proxy class creation, so it's not happening lazily and possibly causing concurrent errors. Also make those collections unmodifiable, to avoid this in the future. Fixes JRUBY-3332.
5301965491 Fix for JRUBY-3130: JRuby selects wrong static method on Java class
02b4c702a4 Fixes for JRUBY-3102: Java classes with non-visible constructors should not be constructible
380698d677 Fixes for JRUBY-2947, to reinstate the ability to coerce multidimensional arrays. No longer magic; specify the array type you want to convert to.
0828bab485 Fix for JRUBY-2903, allow interface methods to be implemented with underscore-cased names.
187e4f6bcf Fix for JRUBY-2850, make sure a wrapped Java object is registered in the cache on its way out to Java.
1d6cbee7a1 Fix for JRUBY-2093 and JRUBY-2270: eliminated the per-method matchingMethod cache used by InstanceMethodInvoker (and *not* used by StaticMethodInvoker), as it didn't take into account arity and would therefore sometimes return the wrong method (JRUBY-2093), was a pain to synchronize (JRUBY-2270), and added overhead while providing no benefit in the common case where there are not multiple methods with the same name and arity.  Also reworked synchronization so that only a relatively inexpensive volatile read is required for all but the first invocation of a method of a given name.
0f7386ed2f Partial fix for JRUBY-2169: Round out methods in JI wrappers, and add support for 1.5 methods (for annotations, etc.).  Still more to do for JavaProxyConstructor and JavaProxyMethod (i.e., JRuby-defined subclass methods and ctors).  Includes some unit tests, could use more.
7a9a8427af Fix for JRUBY-2171; normal JI package searching logs exceptions in verbose mode.
442afa575b Fix for JRUBY-2069.  Problems with equals() and hashCode() methods caused every instantiation of a Ruby-defined Java subclass to add a new entry to the method match cache (in the same bucket!), since returned hashCode was that of the class, new constructors were returned on every call, and equals() would never return true.  So if you created 1000 instances, then 1000 entries were searched for the 1001st instantiation, and so on.
7d9d9f3c9a Fix for JRUBY-199 (and related JRUBY-1513 and JRUBY-1735). Caches proxies in Weak/Concurrent Map-like cache, which replaces the ineffectual JavaObject cache.  TODO: unit tests, measure memory/performance impact.
47fc3bf1eb Fix for JRUBY-1468, fixes the original issue, adds a whole raft of tests, and fixes a few additional issues discovered.
33d8b80e93 Fix for JRUBY-1197: handle digits in Java method names gracefully when turning into non-camel-cased.
0a60ffec98 Bill's fixes for JRUBY-664 to allow final methods in base classes to be called from Ruby child classes.
   Bugs after [       8]:
a4bf4b8d4f Fix JRUBY-6383
3b00c39404 Correctly fix Scala-like method name $bslash. Includes spec. This should fix JRUBY-6327.
d2f2686e1b Fix for JRUBY-6043, JRUBY-6058.
a94f6c5fa0 Fix for JRUBY-5132: java.awt.Component.instance_of?() expects 2 args
ec8d280eb6 Fix for JRUBY-4799: Uncaught AccessibleObject.setAccessible fails on App Engine
aeef9422cb Fix JRUBY-4198: Cannot call a method called "public" on a Java object
4351ad0e48 Fixes for JRUBY-4724 and JRUBY-4725
7c4139b5eb Fix for JRUBY-4505: Restore __method versions of Java methods we don't bind directly

Start block index: 457
End block index: 465

private JavaClass(Ruby runtime, Class javaClass) {
    super(runtime, (RubyClass) runtime.getModule("Java").getClass("JavaClass"), javaClass);
    if (javaClass.isInterface()) {
        initializeInterface(javaClass);
    } else if (!(javaClass.isArray() || javaClass.isPrimitive())) {
        // TODO: public only?
        initializeClass(javaClass);
    }
}
