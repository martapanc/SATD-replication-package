File path: src/org/jruby/RubyFile.java
Comment: / TODO: why are we duplicating the constants here
Initial commit id: cd64ee38
Final commit id: 00633ae6
   Bugs between [      31]:
55c374086d Fix JRUBY-6489
e4b8742829 Fix JRUBY-5960: Something in rails causes File#each to throw InvalidByteSequenceError in 1.9 mode
a3e44c8417 Fix JRUBY-5627: JRuby flock silently converts LOCK_EX to LOCK_SH on read-only files
3037c5e6f5 Fix JRUBY-5634: File.new (and related paths) unconditionally calling to_int on first arg
15832d3b4d Fix JRUBY-5514: Errno::EBADF is sometimes raised instead of IOError when TCPSocket#readline is called after TCPSocket#close
891409e4b2 Improve and expand fix for JRUBY-5406 and centralize encoding lookup logic in EncodingService.
da71cdc017 Fix JRUBY-5276: FileTest methods not showing up in File.singleton_methods
d60304cf18 Fix for JRUBY-5193: a File named "classpath:/path" uses ByteList's classloaders
2816396568 fix JRUBY-5286: taint failure in Dir.pwd and File#path
e2dbc57a01 fix JRUBY-5282: File#mtime should raise error if called on closed object
231a3b3d67 Fix for JRUBY-5167: __FILE__ set to symlink target during load or require
b11215e24f Fix by Aman Gupta for JRUBY-5144: Tempfile#path should return nil after #delete
5165bbb979 fix JRUBY-4983: FileUtils's cp_r and rm_r bugs out with directory containing non ascii characters
1e7d8dc241 fix JRUBY-4921: File.expand_path incorrectly resolves paths relative to an in-jar path
8ca33733dd fixes JRUBY-4899: unable to install gems in 1.9 mode
eaeafa4881 Fix for JRUBY-4879
71aa55d7c0 fixes JRUBY-4859: File.delete doesn't throw an error when deleting a directory
77c5115dbf fixes JRUBY-4770: File#size for 1.9.2
1a004d74f7 Fix for JRUBY-4760: File.open throws Errno::ENOENT when file inside jar is accessed using ".." (doubledot) in the path
431e8ce7db Fix for JRUBY-4537: File.open raises Errno::ENOENT instead of Errno::EACCES
678497f039 Fix for JRUBY-4536: Duplicated error message in SystemCallError#message
9c7afd4209 Fixes JRUBY-3922: File.basename doesn't cope with UTF8 strings
2571d5d5b4 Fix for JRUBY-3806: Encoding problems with File.expand_path after JRUBY-3576 fix for Macroman issue
bac8e8274b Fix by Hiro Asari for JRUBY-3388: RubySpec: File.open opens the file when passed mode, num and permissions
caef981504 Fix for JRUBY-3142 and JRUBY-3660: Dir.pwd correctly displays dirs with unicode characters in them
5c58a30248 Fix for JRUBY-1606: File.expand_path does not work as expected
9f7152a55f Fix for JRUBY-2542: DATA.flock causes ClassCastException
543651da13 Fix for JRUBY-1470: FileTest methods only work with string arguments
5b038df440 Fix or JRUBY-3050: File/IO broken with special characters in filenames
bc75f2d33b Fix for JRUBY-3025: File.truncate errors with "No such file or directory" when the file exists.
8ca2b03c28 Various fixes for JRUBY-2677, problems opening filenames or passing arguments with unicode characters.
   Bugs after [      13]:
bcc26747f5 Use RubyModule.JavaClassKindOf fixes jruby/jruby#614
78491e1c91 Fix JRUBY-7145 by checking for drive letter in Dir.mkdir.
37e33c389d My stab at fixing JRUBY-7122
15dd7233ea Potentially fix JRUBY-7002. Existing specs should pass, but the fix should also be tested on Windows.
6de4987a92 Rework the previous logic (calling #to_path) for #393. Also, fix #399.
e89ffda1a0 Throw correct Errno from File.symlink and File.link.  Fixes #397.
58111ccd41 Fix JRUBY-6998 and define File::NULL (which is included in IO).
ad609577a2 Merge pull request #178 from lukefx/fix_JRUBY-2724
89ed6b777f Fix JRUBY-6578: File.readlink with chdir
92fe8de00d Fix JRUBY-6774
533b304286 Fix JRUBY-6780
35604c2847 Fix JRUBY-6735: FileUtils.chmod broken for symlinks
34c4091481 Fix JRUBY-6702

Start block index: 175
End block index: 269
public static RubyClass createFileClass(Ruby runtime) {
    RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
    runtime.setFile(fileClass);
    RubyString separator = runtime.newString("/");

    fileClass.kindOf = new RubyModule.KindOf() {
            public boolean isKindOf(IRubyObject obj, RubyModule type) {
                return obj instanceof RubyFile;
            }
        };

    separator.freeze();
    fileClass.defineConstant("SEPARATOR", separator);
    fileClass.defineConstant("Separator", separator);

    if (File.separatorChar == '\\') {
        RubyString altSeparator = runtime.newString("\\");
        altSeparator.freeze();
        fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
    } else {
        fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
    }

    RubyString pathSeparator = runtime.newString(File.pathSeparator);
    pathSeparator.freeze();
    fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);

    // TODO: why are we duplicating the constants here, and then in
    // File::Constants below? File::Constants is included in IO.

    // TODO: These were missing, so we're not handling them elsewhere?
    // FIXME: The old value, 32786, didn't match what IOModes expected, so I reference
    // the constant here. THIS MAY NOT BE THE CORRECT VALUE.
    fileClass.fastSetConstant("BINARY", runtime.newFixnum(ModeFlags.BINARY));
    fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
    fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
    fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_CASEFOLD));
    fileClass.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
    fileClass.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));

    // Create constants for open flags
    fileClass.fastSetConstant("RDONLY", runtime.newFixnum(ModeFlags.RDONLY));
    fileClass.fastSetConstant("WRONLY", runtime.newFixnum(ModeFlags.WRONLY));
    fileClass.fastSetConstant("RDWR", runtime.newFixnum(ModeFlags.RDWR));
    fileClass.fastSetConstant("CREAT", runtime.newFixnum(ModeFlags.CREAT));
    fileClass.fastSetConstant("EXCL", runtime.newFixnum(ModeFlags.EXCL));
    fileClass.fastSetConstant("NOCTTY", runtime.newFixnum(ModeFlags.NOCTTY));
    fileClass.fastSetConstant("TRUNC", runtime.newFixnum(ModeFlags.TRUNC));
    fileClass.fastSetConstant("APPEND", runtime.newFixnum(ModeFlags.APPEND));
    fileClass.fastSetConstant("NONBLOCK", runtime.newFixnum(ModeFlags.NONBLOCK));

    // Create constants for flock
    fileClass.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
    fileClass.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
    fileClass.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
    fileClass.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));

    // Create Constants class
    RubyModule constants = fileClass.defineModuleUnder("Constants");

    // TODO: These were missing, so we're not handling them elsewhere?
    constants.fastSetConstant("BINARY", runtime.newFixnum(ModeFlags.BINARY));
    constants.fastSetConstant("SYNC", runtime.newFixnum(0x1000));
    constants.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
    constants.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
    constants.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_CASEFOLD));
    constants.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
    constants.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));

    // Create constants for open flags
    constants.fastSetConstant("RDONLY", runtime.newFixnum(ModeFlags.RDONLY));
    constants.fastSetConstant("WRONLY", runtime.newFixnum(ModeFlags.WRONLY));
    constants.fastSetConstant("RDWR", runtime.newFixnum(ModeFlags.RDWR));
    constants.fastSetConstant("CREAT", runtime.newFixnum(ModeFlags.CREAT));
    constants.fastSetConstant("EXCL", runtime.newFixnum(ModeFlags.EXCL));
    constants.fastSetConstant("NOCTTY", runtime.newFixnum(ModeFlags.NOCTTY));
    constants.fastSetConstant("TRUNC", runtime.newFixnum(ModeFlags.TRUNC));
    constants.fastSetConstant("APPEND", runtime.newFixnum(ModeFlags.APPEND));
    constants.fastSetConstant("NONBLOCK", runtime.newFixnum(ModeFlags.NONBLOCK));

    // Create constants for flock
    constants.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
    constants.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
    constants.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
    constants.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));

    // File::Constants module is included in IO.
    runtime.getIO().includeModule(constants);

    runtime.getFileTest().extend_object(fileClass);

    fileClass.defineAnnotatedMethods(RubyFile.class);

    return fileClass;
}
