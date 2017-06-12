File path: src/org/jruby/RubyIO.java
Comment: / TODO: is this the right thing to do?
Initial commit id: 043df600
Final commit id: fb60a5ba
   Bugs between [      39]:
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
   Bugs after [      28]:
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

Start block index: 2664
End block index: 2793
    public static IRubyObject select_static(ThreadContext context, Ruby runtime, IRubyObject[] args) {
       try {
           Set pending = new HashSet();
           Set unselectable_reads = new HashSet();
           Set unselectable_writes = new HashSet();
           Selector selector = Selector.open();
           if (!args[0].isNil()) {
               // read
               checkArrayType(runtime, args[0]);
               for (Iterator i = ((RubyArray) args[0]).getList().iterator(); i.hasNext(); ) {
                   IRubyObject obj = (IRubyObject) i.next();
                   RubyIO ioObj = convertToIO(context, obj);
                   if (registerSelect(context, selector, obj, ioObj, SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) {
                       if (ioObj.writeDataBuffered()) {
                           pending.add(obj);
                       }
                   } else {
                       if (( ioObj.openFile.getMode() & OpenFile.READABLE ) != 0) {
                           unselectable_reads.add(obj);
                       }
                   }
               }
           }

           if (args.length > 1 && !args[1].isNil()) {
               // write
               checkArrayType(runtime, args[1]);
               for (Iterator i = ((RubyArray) args[1]).getList().iterator(); i.hasNext(); ) {
                   IRubyObject obj = (IRubyObject) i.next();
                   RubyIO ioObj = convertToIO(context, obj);
                   if (!registerSelect(context, selector, obj, ioObj, SelectionKey.OP_WRITE)) {
                       if (( ioObj.openFile.getMode() & OpenFile.WRITABLE ) != 0) {
                           unselectable_writes.add(obj);
                       }
                   }
               }
           }

           if (args.length > 2 && !args[2].isNil()) {
               checkArrayType(runtime, args[2]);
               // Java's select doesn't do anything about this, so we leave it be.
           }

           final boolean has_timeout = ( args.length > 3 && !args[3].isNil() );
           long timeout = 0;
           if(has_timeout) {
               IRubyObject timeArg = args[3];
               if (timeArg instanceof RubyFloat) {
                   timeout = Math.round(((RubyFloat) timeArg).getDoubleValue() * 1000);
               } else if (timeArg instanceof RubyFixnum) {
                   timeout = Math.round(((RubyFixnum) timeArg).getDoubleValue() * 1000);
               } else { // TODO: MRI also can hadle Bignum here
                   throw runtime.newTypeError("can't convert "
                           + timeArg.getMetaClass().getName() + " into time interval");
               }

               if (timeout < 0) {
                   throw runtime.newArgumentError("negative timeout given");
               }
           }
           
           if (pending.isEmpty() && unselectable_reads.isEmpty() && unselectable_writes.isEmpty()) {
               if (has_timeout) {
                   if (timeout==0) {
                       selector.selectNow();
                   } else {
                       selector.select(timeout);                       
                   }
               } else {
                   selector.select();
               }
           } else {
               selector.selectNow();               
           }
           
           List r = new ArrayList();
           List w = new ArrayList();
           List e = new ArrayList();
           for (Iterator i = selector.selectedKeys().iterator(); i.hasNext(); ) {
               SelectionKey key = (SelectionKey) i.next();
               try {
                   int interestAndReady = key.interestOps() & key.readyOps();
                   if ((interestAndReady
                           & (SelectionKey.OP_READ|SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT)) != 0) {
                       r.add(key.attachment());
                       pending.remove(key.attachment());
                   }
                   if ((interestAndReady & (SelectionKey.OP_WRITE)) != 0) {
                       w.add(key.attachment());
                   }
               } catch (CancelledKeyException cke) {
                   // TODO: is this the right thing to do?
                   pending.remove(key.attachment());
                   e.add(key.attachment());
               }
           }
           r.addAll(pending);
           r.addAll(unselectable_reads);
           w.addAll(unselectable_writes);
           
           // make all sockets blocking as configured again
           for (Iterator i = selector.keys().iterator(); i.hasNext(); ) {
               SelectionKey key = (SelectionKey) i.next();
               SelectableChannel channel = key.channel();
               synchronized(channel.blockingLock()) {
                   RubyIO originalIO = (RubyIO) TypeConverter.convertToType(
                           (IRubyObject) key.attachment(), runtime.getIO(),
                           MethodIndex.TO_IO, "to_io");
                   boolean blocking = originalIO.getBlocking();
                   key.cancel();
                   channel.configureBlocking(blocking);
               }
           }
           selector.close();
           
           if (r.size() == 0 && w.size() == 0 && e.size() == 0) {
               return runtime.getNil();
           }
           
           List ret = new ArrayList();
           
           ret.add(RubyArray.newArray(runtime, r));
           ret.add(RubyArray.newArray(runtime, w));
           ret.add(RubyArray.newArray(runtime, e));
           
           return RubyArray.newArray(runtime, ret);
       } catch(IOException e) {
           throw runtime.newIOError(e.getMessage());
       }
   }
