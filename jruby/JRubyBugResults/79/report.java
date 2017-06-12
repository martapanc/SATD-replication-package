File path: src/org/jruby/util/io/ChannelStream.java
Comment: / TODO this should entry into error handling somewhere
Initial commit id: 66b024fe
Final commit id: aa4ffa1b
   Bugs between [       0]:

   Bugs after [      35]:
533b304286 Fix JRUBY-6780
cb777c23ff Partial fix for JRUBY-6442
01fa54f735 Fix JRUBY-6280: Fails to open fifo for writing.
ed560a8ca7 Fix JRUBY-6198: When calling dup on file open in binmode the new object does not respect binmode
d60304cf18 Fix for JRUBY-5193: a File named "classpath:/path" uses ByteList's classloaders
0b7aed72d4 Alternative fix for JRUBY-5114: provide an autoclose flag on all IO objects that will allow turning off the close-on-finalize behavior. Also modified the "to_io" impls for InputStream, OutputStream, and Channel to take an :autoclose option.
620caccb15 Fix for JRUBY-5114: Frequent internal server errors resulting from AJAX calls -- GlassFish v3 1.0.2 with JRuby 1.5.2 and newer
4aa30906c3 Fix for JRUBY-5002: Stuck when loading marshalled data > 32000 bytes from IO stream
d9f4b889d9 Add a fix for JRUBY-5076
30d7b6d8b7 Fix for JRUBY-5021: imap-over-ssl connections left to GC do not clean up completely
8e51e6e32c Fixes for JRUBY-2282 and JRUBY-2475.
edfa8a8b80 Fix JRUBY-4319
feea254223 Even betterer fix for JRUBY-4308 - add InputStream and OutputStream adapters to ChannelStream, so they can share the read/write buffer, and use them directly from IOInputStream and IOOutputStream.  Some benches are 6x faster than VVSiz's already improved version.
2c50d7f115 Fix JRUBY-3784 - More than 2G memory required for jruby -e 'buf = IO.read("/tmp/1GB.txt"); p buf.size'
6978b2a6f4 Fix JRUBY-3721 by reading buffered data in ChannelStream#readall(), instead of flushing the buffer then re-reading from the channel.
e92706582b Fix for JRUBY-3688 - only use the optimized read path for files with a size larger than zero.  Pointed out by Rick Ohnemus.
61a0d45a0a Maybe fix for JRUBY-3679 - make ChannelStream#sync() synchronized, and use flushWrite() internally.
fa28a14132 Fix for JRUBY-2506: Marshal/IO.eof bug
94392e1728 Fix all locations where we set channel blocking modes for select without proper re-setting protocol. Needs more cleanup, but fixes JRUBY-3017: DRb "premature header" error on JRuby client.
3b3a34120f Fix for JRUBY-2108: IOWaitLibrary ready? returns nil when ready
8956ef0bdd Fix for JRUBY-3155: TCPSocket#puts block when the socket is waiting in read
282588e765 Fix JRUBY-3008 by not flushing putc after every char.  This also allows BadDescriptorExceptions to be propagated from IO#putc - the failing tests were expecting putc to be buffered.
b7b26c9c4a Fix for JRUBY-2586
ae65e95de2 Fix for JRUBY-2625, pending a set of test cases or specs.
b1d615bc75 Fixes for EOF logic in ChannelStream for JRUBY-2386.
7d500602a4 Wayne Meissner's fixes/improvements for getline performance. JRUBY-2689.
2211f67b41 Re-fix for JRUBY-2657, with ungetc getting cleared appropriately during a readall.
afe466dba5 IO#readpartial fixes: - JRUBY-2632: IO#readpartial doesn't handle unget char               (Heavily reworked patch by Kevin Ballard)
a175d77cd8 Additional fix for regression caused by JRUBY-2314 fix: readpartial should only read what's available on the buffered and do a normal unbuffered read otherwise. Because readpartial originally called bufferedRead, the fix for 2314 broke it. The correct behavior is that readpartial freads only what's already buffered, rather than the number provided.
d990ee5af4 Fix for JRUBY-2314, allow buffered reads to keep reading as long as they're successfully getting bytes back and do not hit EOF. EOF or zero bytes read will terminate the read loop.
e85e442e2f Fix for JRUBY-2164. Add appropriate waitReadable and waitWritable to important places.
20b8cfaadb Fix for JRUBY-2125, trying to create /dev/null on a reopen.
6967d56b0e Various fixes to the "null channel" for JRUBY-2159. Tests coming from me or Vladimir.
e8a3ebe129 Fix for JRUBY-2071, reopen seek errors because of shared position in the underlying channel.
3c58a8a75d Fixed regression in JRUBY-1923: STDIN not working under Kernel.system, after IO reorg.

Start block index: 516
End block index: 526
    public synchronized ByteList read(int number) throws IOException, BadDescriptorException {
        checkReadable();
        ensureReadNonBuffered();
        
        ByteList byteList = new ByteList(number);
        
        // TODO this should entry into error handling somewhere
        int bytesRead = descriptor.read(number, byteList);
        
        return byteList;
    }
