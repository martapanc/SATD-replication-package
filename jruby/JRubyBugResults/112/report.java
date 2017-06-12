File path: src/org/jruby/RubyFile.java
Comment: / TODO: This is also defined in the MetaClass too...Consolidate somewhere.
Initial commit id: 0a56a828
Final commit id: d055a0ea
   Bugs between [      53]:
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
9d126d060f Fix for JRUBY-2524, from Steen Lehman. Thanks!
6967d56b0e Various fixes to the "null channel" for JRUBY-2159. Tests coming from me or Vladimir.
74e78ff6fe JRUBY-1983: File.utime and File.mtime (patch by Wirianto Djunaidi) Also fixed ENOENT problem listed in comment by Assaf (which is JRUBY-1982 in cause) JRUBY-1982: FileUtils.touch / File.utime failing if file exists.
769666d708 Fix for JRUBY-1986, turn the len parameter of fnmatch into an end parameter instead.
cd45d2952b Fix for JRUBY-1990, patch by Wirianto Djunaidi
491987d6ad Fix for JRUBY-1926.
9a0360cc83 Fix for JRUBY-1920: File#delete can't delete broken symlinks (patch by Vladimir).
c54b9d5e2c Fix for JRUBY-1843, native and Java impls of File::readlink
4f1d891bb0 Fix for JRUBY-1785, make tests pass on windows too
014ba11a30 More security fixes for applets, specifically to allow IRB to run again. For JRUBY-1762, from Vladimir.
c8a60c62db JRUBY-1732: String#rindex works incorrectly with FixNum parameters (patch by Vladimir Sizikov) JRUBY-1730: String#slice! and String#[]= with negative ranges behave differently than Ruby (patch by Vladimir Sizikov) JRUBY-1726: String#inpect and String#dump behavior is different from Ruby (patch by Vladimir Sizikov) JRUBY-1672: JRuby File.rename() behavior different from Ruby, causes log rotation issue (patch by Vladimir Sizikov)
3465b43bd7 Fix for JRUBY-1612.
bf7af297ac Fix for JRUBY-1623, by Vladimir Sizikov.
b01a575665 Fix the AR-JDBC mess I created by pruning to much, and fix JRUBY-1624, patch by Vladimir Sizikov
4a7d0a1b4f Fix for JRUBY-1499, caused by mtime not reporting change when it should have
50006585ea Fix for JRUBY-1023, support file descriptor form of File.new
80f6487aa4 Fix for JRUBY-1025, raise EINVAL for negative truncate values.
92563647b8 Fix for JRUBY-1018.
fed6c8bef4 Fix for JRUBY-696 and some cleanup of atime and ctime. We have no way to retrieve either, so make both just be mtime.
1c02ca0e64 Fixes for JRUBY-734, along with a first run at cleaning up IRubyObject.
8d7f2f4f78 Fix for JRUBY-412: Make File::Stat a good citizen of the new allocator world.
4825bbbd45 Fix for JRUBY-396: Adds File#ctime and File.ctime
8ba215b45e Next version of inspect. Makes instanceVariables synchronized, and adds better inspect for RubyFile and RubyModule. Fixes JRUBY-170
   Bugs after [      14]:
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
55c374086d Fix JRUBY-6489

Start block index: 143
End block index: 152
    // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
	private IOModes getModes(IRubyObject object) {
		if (object instanceof RubyString) {
			return new IOModes(getRuntime(), ((RubyString)object).getValue());
		} else if (object instanceof RubyFixnum) {
			return new IOModes(getRuntime(), ((RubyFixnum)object).getLongValue());
		}

		throw getRuntime().newTypeError("Invalid type for modes");
	}
