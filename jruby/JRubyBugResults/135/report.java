File path: src/org/jruby/runtime/builtin/meta/FileMetaClass.java
Comment: ixme: This does not have exact same semantics as RubyArray.join
Initial commit id: 7574ed61
Final commit id: bac69eda
   Bugs between [       9]:
1c02ca0e64 Fixes for JRUBY-734, along with a first run at cleaning up IRubyObject.
9fa4acdd46 Fix for JRUBY-461: trim off dir before searching for extension.
dbdef38090 Fix for JRUBY-429, and also make filenames more like MRI in backtraces.
8d7f2f4f78 Fix for JRUBY-412: Make File::Stat a good citizen of the new allocator world.
57504d74a1 Initial fixes for JRUBY-408. Added ObjectAllocator and code to consume it throughout the MetaClass hierarchy.
4825bbbd45 Fix for JRUBY-396: Adds File#ctime and File.ctime
542415f6bc Fix for JRUBY-301, make symlink? work by comparing canonical and absolute paths
59c5d204bf Fix for JRUBY-333, Rails 1.2 needs File#extname
aa567ca8c6 Fix for JRUBY-224, File.rename() should throw error when renaming to file in nonexistant directory
   Bugs after [       0]:

Start block index: 361
End block index: 393

/*
 * Fixme:  This does not have exact same semantics as RubyArray.join, but they
 * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
 */
public RubyString join(IRubyObject[] args) {
  boolean isTainted = false;
StringBuffer buffer = new StringBuffer();

for (int i = 0; i < args.length; i++) {
  if (args[i].isTaint()) {
    isTainted = true;
  }
  String element;
  if (args[i] instanceof RubyString) {
    element = args[i].toString();
  } else if (args[i] instanceof RubyArray) {
    // Fixme: Need infinite recursion check to put [...] and not go into a loop
    element = join(((RubyArray) args[i]).toJavaArray()).toString();
  } else {
    element = args[i].convertToString().toString();
  }

  chomp(buffer);
  if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
    buffer.append("/");
  }
  buffer.append(element);
}

    RubyString fixedStr = RubyString.newString(getRuntime(), buffer.toString());
    fixedStr.setTaint(isTainted);
    return fixedStr;
}
