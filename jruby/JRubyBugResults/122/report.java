File path: src/org/jruby/util/io/Stream.java
Comment: / We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it)
Initial commit id: 66b024fe
Final commit id: 9d57dbc3
   Bugs between [       2]:
7d500602a4 Wayne Meissner's fixes/improvements for getline performance. JRUBY-2689.
e8a3ebe129 Fix for JRUBY-2071, reopen seek errors because of shared position in the underlying channel.
   Bugs after [       2]:
ed560a8ca7 Fix JRUBY-6198: When calling dup on file open in binmode the new object does not respect binmode
0b7aed72d4 Alternative fix for JRUBY-5114: provide an autoclose flag on all IO objects that will allow turning off the close-on-finalize behavior. Also modified the "to_io" impls for InputStream, OutputStream, and Channel to take an :autoclose option.

Start block index: 43
End block index: 44
    // We use a highly uncommon string to represent the paragraph delimiter (100% soln not worth it) 
    public static final ByteList PARAGRAPH_DELIMETER = ByteList.create("PARAGRPH_DELIM_MRK_ER");
