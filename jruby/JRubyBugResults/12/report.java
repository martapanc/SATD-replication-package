File path: src/org/jruby/RubyIO.java
Comment: / FIXME: I don't like the null checks here
Initial commit id: 66b024fe
Final commit id: 83c50c04
   Bugs between [      43]:
c97a0fb3fa Fix for JRUBY-5124: Bundler doesn't work with 1.9 mode
d9f4b889d9 Add a fix for JRUBY-5076
7bc5d6b33b Fix JRUBY-4960: 1.9: puts nil should output "\n" not "nil\n". Two more passing 1.9 specs.
23c2ef7091 fix JRUBY-4932: IO.popen4 reading from the wrong socket results in wrong error message
713c1c7a6a Possible fix for JRUBY-4764: Memory leak
dc1096071d Fix JRUBY-3857: IO.fsync not flushing buffer before sync
71bcc5a480 fixes JRUBY-4652: [1.9] IO.try_convert is not implemented
96c1df8be4 Fix for JRUBY-4650 in 1.9 mode as well.
a1f900fc28 Fix for JRUBY-4504: Installing gems while in 1.9 mode fails
3487bba2fa Fixed JRUBY-4116 and JRUBY-3950 (IRB in Applet and WebStart environments).
b03c7b4786 fixes JRUBY-4011: IO.binmode should raise an IOError on closed stream
4579aa2546 Fix for JRUBY-3869: 'puts []' prints a newline
6ce486ad82 Fix for JRUBY-3799: Bug in native timeout module causes hang when waiting on IO
fdd28b5834 Fix for JRUBY-3778: clone of stderr and stdout not working
cbe056d421 Fix for JRUBY-3652: IO.popen drops first space between command and first argument
434b818193 Fix for JRUBY-3483: Redirecting $stdout to an object blows up the stack
0c8b72d572 Fix for JRUBY-3237: IO.popen dosn't support forward slashes in Windows (diffrent behaviour to MRI)
49f6aa5090 Fix for JRUBY-3405: popen breaks input channel for IRB
82c0b9b8bc Fix for JRUBY-3071: Illegal seek error trying to read() from pipe
60f3d6628f Fix for JRUBY-1079: IO.sysopen not defined
94392e1728 Fix all locations where we set channel blocking modes for select without proper re-setting protocol. Needs more cleanup, but fixes JRUBY-3017: DRb "premature header" error on JRuby client.
5b3ecffcc6 Partial fix for JRUBY-3384, only solves Readline.readline issues.
4d687865f7 Change ordering of selector closing and blocking-mode resetting to avoid channels still be registered when we try to change blocking. Should fix JRUBY-3329.
df57693641 Fix for JRUBY-3198: String#slice! not working correctly when used with string read from file. String#each was to blame as it should mark self as shared.
28cedab887 Fix JRUBY-3019 by resetting the length of the ByteList in the passed in string to 0.
282588e765 Fix JRUBY-3008 by not flushing putc after every char.  This also allows BadDescriptorExceptions to be propagated from IO#putc - the failing tests were expecting putc to be buffered.
31d26f76b6 Fix JRUBY-3009.  Convert IOException into IOError for IO#putc.  BadDescriptorException has to be ignored, since thats what is expected.
666dcb838f Fix JRUBY-2281 by making IO#write_nonblock similar to syswrite internally.
45ff75add0 Fix for JRUBY-2998.  Use OpenFile#getWriteStream for IO#putc.
24a1c73de4 fix JRUBY-2869, and partially address JRUBY-891
cdf5c76741 partially fix JRUBY-2869
9709fc2ea2 Fix for JRUBY-2615, interactive subprocess not acting interactive.
593f867f0b Fix for JRUBY-2644, TCPServer#close not waking threads waiting on TCPServer#accept.
ae65e95de2 Fix for JRUBY-2625, pending a set of test cases or specs.
b1d615bc75 Fixes for EOF logic in ChannelStream for JRUBY-2386.
b0693ba5ab Probable fix for JRUBY-2625, read_nonblock behaving like a blocking read.
7d500602a4 Wayne Meissner's fixes/improvements for getline performance. JRUBY-2689.
afe466dba5 IO#readpartial fixes: - JRUBY-2632: IO#readpartial doesn't handle unget char               (Heavily reworked patch by Kevin Ballard)
e85e442e2f Fix for JRUBY-2164. Add appropriate waitReadable and waitWritable to important places.
6967d56b0e Various fixes to the "null channel" for JRUBY-2159. Tests coming from me or Vladimir.
3c58a8a75d Fixed regression in JRUBY-1923: STDIN not working under Kernel.system, after IO reorg.
03f8a3762a Fix for JRUBY-1047, add IO#for_fd alias
4ad8f70a85 Improved fix for JRUBY-2073: IO#foreach failures and crashes with rubyspecs.
   Bugs after [      43]:
bcc26747f5 Use RubyModule.JavaClassKindOf fixes jruby/jruby#614
efe615a2a3 IO.copy_stream should handle objects that respond to read or write. Fixes #437
3f56e551e4 Fix JRUBY-7046 - always copy files in 128M chunks.  Might be slightly slower than the optimized version that tried to transfer the entire file with the fallback, but that one relied on parsing exception messages, and I think the fallback was broken anyway (always copied from the same file position).
6de4987a92 Rework the previous logic (calling #to_path) for #393. Also, fix #399.
ebbfbacfc1 Revert "Fix JRUBY-6974"
f5e2f5fd0b Fix JRUBY-6974
73ce313ae3 Revert "Fix JRUBY-6162"
daa36aa25f Partially fix JRUBY-6893: fcntl(Fcntl::F_GETFL) always return 0
2d497afa89 Fix JRUBY-6162
1b3665d0fc Fix JRUBY-6851: IO#set_encoding doesn't work with Encoding object
8fecee704c Fix JRUBY-6572 and unexclude a bunch of passing tests.
c725e7dae4 Also fix JRUBY-6180 on Windows
353fea102b Fix JRUBY-5876
30095bb645 Fix JRUBY-5222
cb777c23ff Partial fix for JRUBY-6442
f8408dac26 Fix JRUBY-5348
c0b57cdb87 Fix JRUBY-4913
0ae6d4570a Try fixing JRUBY-6180. Error message matching may not be correct, so we will need confirmation.
00d30e159b Fix JRUBY-6291: Closing One Stream From IO.popen4 Results in Stream Closed Error When Reading Other Streams
ed560a8ca7 Fix JRUBY-6198: When calling dup on file open in binmode the new object does not respect binmode
3adbb9e36d Fix JRUBY-6205: 'Bad file descriptor' when using IO.popen4 with OpenJDK 7
b7c3cc24bd Fix JRUBY-6212: IO#inspect in 1.9 could be prettier
c0615e746e Fix for JRUBY-6101.
ac992dfaa8 Fix JRUBY-5888: missing File#readbyte
7bca899059 Move org.jruby.ext.ffi.Util.checkStringSafety() to org.jruby.util.StringSupport.checkStringSafety(). This should fix JRUBY-5835: [ruboto] File#read fails with jruby-jars-1.6.2
0b55907500 Fix JRUBY-4828: Null-byte vulnerability via org.jruby.ext.ffi.Util.checkStringSafety()
dc2ff992f5 Fix JRUBY-5685: IO.popen4 does not work when arguments contain *
5d094dbf5c Fix JRUBY-5688: Process::Status#pid is missing
864087b8c8 Fix JRUBY-5532: IO.foreach: Can't convert Hash into String
15832d3b4d Fix JRUBY-5514: Errno::EBADF is sometimes raised instead of IOError when TCPSocket#readline is called after TCPSocket#close
26351a1be2 Fix JRUBY-5487: Kernel#select's read_array parameter is not respected
2e751d6699 Fix JRUBY-5503: Timeout::timeout makes IO#close block if there's a #read present
5c55540d73 fix JRUBY-5400: [1.9] IO.ungetc needs to accept multibyte char
20924a2ab8 fix JRUBY-5436: File.open mode doesnt accept "r:ENC:-"
891409e4b2 Improve and expand fix for JRUBY-5406 and centralize encoding lookup logic in EncodingService.
2270b58753 Fix JRUBY-5389: IO.popen doesn't support Array of String's arg in 1.9 mode
d60304cf18 Fix for JRUBY-5193: a File named "classpath:/path" uses ByteList's classloaders
b89d8a699f Fix JRUBY-4595: [1.9] File.binread is missing
99a5b31897 Additional fix for JRUBY-5122: fread read size calcuration error
2ae49952b1 Fix IO#autoclose= to take an argument and expose that and IO#autoclose in 1.8 mode. JRUBY-5114.
0b7aed72d4 Alternative fix for JRUBY-5114: provide an autoclose flag on all IO objects that will allow turning off the close-on-finalize behavior. Also modified the "to_io" impls for InputStream, OutputStream, and Channel to take an :autoclose option.
3871049378 Fix for JRUBY-4908: IO.popen4 returns the wrong pid in Linux
1df3506c0a Fix for JRUBY-4908: IO.popen4 returns the wrong pid in Linux

Start block index: 1935
End block index: 2573

@JRubyMethod(name = "read", optional = 2)
public IRubyObject read(IRubyObject[] args) {
    int argCount = args.length;

    OpenFile myOpenFile = getOpenFileChecked();

    if (argCount == 0 || args[0].isNil()) {
        try {
            myOpenFile.checkReadable(getRuntime());

            if (args.length == 2) {
                return readAll(args[1]);
            } else {
                return readAll(getRuntime().getNil());
            }
        } catch (PipeException ex) {
            throw getRuntime().newErrnoEPIPEError();
        } catch (InvalidValueException ex) {
            throw getRuntime().newErrnoEINVALError();
        } catch (EOFException ex) {
            throw getRuntime().newEOFError();
        } catch (IOException ex) {
            throw getRuntime().newIOErrorFromException(ex);
        } catch (BadDescriptorException ex) {
            throw getRuntime().newErrnoEBADFError();
        }
    }

    int length = RubyNumeric.num2int(args[0]);

    if (length < 0) {
        throw getRuntime().newArgumentError("negative length " + length + " given");
    }

    RubyString str = null;
//        ByteList buffer = null;
    if (args.length == 1 || args[1].isNil()) {
//            buffer = new ByteList(length);
//            str = RubyString.newString(getRuntime(), buffer);
    } else {
        str = args[1].convertToString();
        str.modify(length);

        if (length == 0) {
            return str;
        }

//            buffer = str.getByteList();
    }

    try {
        myOpenFile.checkReadable(getRuntime());

        if (myOpenFile.getMainStream().feof()) {
            return getRuntime().getNil();
        }

        // TODO: Ruby locks the string here

        // READ_CHECK from MRI io.c
        readCheck(myOpenFile.getMainStream());

        // TODO: check buffer length again?
//        if (RSTRING(str)->len != len) {
//            rb_raise(rb_eRuntimeError, "buffer string modified");
//        }

        // TODO: read into buffer using all the fread logic
//        int read = openFile.getMainStream().fread(buffer);
        ByteList newBuffer = myOpenFile.getMainStream().fread(length);

        // TODO: Ruby unlocks the string here

        // TODO: change this to check number read into buffer once that's working
//        if (read == 0) {

        if (newBuffer == null || newBuffer.length() == 0) {
            if (myOpenFile.getMainStream() == null) {
                return getRuntime().getNil();
            }

            if (myOpenFile.getMainStream().feof()) {
                // truncate buffer string to zero, if provided
                if (str != null) {
                    str.setValue(ByteList.EMPTY_BYTELIST.dup());
                }

                return getRuntime().getNil();
            }

            if (length > 0) {
                // I think this is only partly correct; sys fail based on errno in Ruby
                throw getRuntime().newEOFError();
            }
        }


        // TODO: Ruby truncates string to specific size here, but our bytelist should handle this already?

        // FIXME: I don't like the null checks here
        if (str == null) {
            if (newBuffer == null) {
                str = getRuntime().newString();
            } else {
                str = RubyString.newString(getRuntime(), newBuffer);
            }
        } else {
            if (newBuffer == null) {
                str.setValue(ByteList.EMPTY_BYTELIST.dup());
            } else {
                str.setValue(newBuffer);
            }
        }
        str.setTaint(true);

        return str;
    } catch (EOFException ex) {
        throw getRuntime().newEOFError();
    } catch (PipeException ex) {
        throw getRuntime().newErrnoEPIPEError();
    } catch (InvalidValueException ex) {
        throw getRuntime().newErrnoEINVALError();
    } catch (IOException ex) {
        throw getRuntime().newIOErrorFromException(ex);
    } catch (BadDescriptorException ex) {
        throw getRuntime().newErrnoEBADFError();
    }
}
