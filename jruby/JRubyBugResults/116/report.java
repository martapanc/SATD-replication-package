File path: src/org/jruby/javasupport/proxy/JavaProxyClassFactory.java
Comment: / TODO: we should be able to optimize this quite a bit post-1.0. JavaClass already
Initial commit id: 0b9733a0
Final commit id: 165ec891
   Bugs between [       9]:
a40de0ef64 Store the JavaProxyClass cache in JavaSupport (fixes #164)
1d3e1adaf5 Fix and spec for JRUBY-4451: error constructing new Font instance from Ruby class inheriting from Java::java.awt.Font in Java 1.5
68ed5ba440 Fix for JRUBY-3011: java classes with non-public constructors are incorrectly instantiated
6f5700d766 Fix for JRUBY-2865, extending a default package class in Ruby generates a bogus proxy package name.
1855c7f12a Fix for JRUBY-2886.
b6cf6a1475 Add a prefix to all Ruby subclasses of other libraries' classes, to work around security restrictions about creating new classes in possibly secured packages. JRUBY-2439.
6af2c16188 * Fix for JRUBY-2551, proxy-subclass-thingies not using getDeclaredConstructors and therefore no ability to override protected constructors from parents. * Also twiddled some MiniJava code that didn't make use of the fact that getConstructors only returns public, and still checked public for each.
5aca3fa621 Fix for JRUBY-1226 to allow JRuby to work inside a signed webstart app. Patch by Andrew McDowell
71b7b41bdd Committing a slightly modified fix for JRUBY-874, to allow overriding any superclass methods that aren't final or private.
   Bugs after [       0]:


comment was not found
