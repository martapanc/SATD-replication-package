File path: src/org/jruby/javasupport/JavaField.java
Comment: / TODO: Only setAccessible to account for pattern found by
Initial commit id: e40beaae
Final commit id: beaaeb89
   Bugs between [       2]:
b19d79dff7 Damian's fixes for applets in JRUBY-477
b390103c28 Damn the torpedos...full steam ahead! Committing fixes for JRUBY-408 to get them out in the wild. There are remaining fixes to be made, but ant test passes, gems install, rails starts, handles requests, and generates.
   Bugs after [       5]:
f38b8e5a35 Kinda sorta fix JRUBY-6674
1624d16b2f fixes: JRUBY-4351: static fields cannot be set via our JavaField implementation
d493ef889d Fix JRUBY-1976, make Java Fields coerce things that aren't JavaObjects correctly, such as JavaConstructor, JavaMethod and JavaField.
0f7386ed2f Partial fix for JRUBY-2169: Round out methods in JI wrappers, and add support for 1.5 methods (for annotations, etc.).  Still more to do for JavaProxyConstructor and JavaProxyMethod (i.e., JRuby-defined subclass methods and ctors).  Includes some unit tests, could use more.
442afa575b Fix for JRUBY-2069.  Problems with equals() and hashCode() methods caused every instantiation of a Ruby-defined Java subclass to add a new entry to the method match cache (in the same bucket!), since returned hashCode was that of the class, new constructors were returned on every call, and equals() would never return true.  So if you created 1000 instances, then 1000 entries were searched for the 1001st instantiation, and so on.

Start block index: 122
End block index: 134
    public JavaObject static_value() {
        try {
	    // TODO: Only setAccessible to account for pattern found by
	    // accessing constants included from a non-public interface.
	    // (aka java.util.zip.ZipConstants being implemented by many
	    // classes)
	    field.setAccessible(true);
            return JavaObject.wrap(getRuntime(), field.get(null));
        } catch (IllegalAccessException iae) {
	    throw new TypeError(getRuntime(),
				"illegal static value access: " + iae.getMessage());
        }
    }
