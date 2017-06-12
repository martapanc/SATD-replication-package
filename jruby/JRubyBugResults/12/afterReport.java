12/report.java
Satd-method: public IRubyObject read(IRubyObject[] args) {
********************************************
********************************************
12/After/00d30e159  Fix JRUBY-6291: Closing  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/0ae6d4570  Try fixing JRUBY-6180. E diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/0b5590750  Fix JRUBY-4828: Null-byt diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/0b7aed72d  Alternative fix for JRUB diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ByteList fread(int number) throws IOException, BadDescriptorException, EOFException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ByteList readall() throws IOException, BadDescriptorException, EOFException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOpen() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Stream getMainStream() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkClosed(Ruby runtime) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Ruby getRuntime() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean readDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor getDescriptor() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int fgetc() throws IOException, BadDescriptorException, EOFException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/15832d3b4  Fix JRUBY-5514: Errno::E diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
-            int bytesRead = myOpenFile.getMainStream().getDescriptor().read(len, str.getByteList());
+            int bytesRead = myOpenFile.getMainStreamSafe().getDescriptor().read(len, str.getByteList());
-            return RubyString.newString(context.getRuntime(), openFile.getMainStream().read(RubyNumeric.fix2int(args[0])));
+            return RubyString.newString(context.getRuntime(), openFile.getMainStreamSafe().read(RubyNumeric.fix2int(args[0])));

Lines added containing method: 2. Lines removed containing method: 2. Tot = 4
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newStringShared(ByteList byteList) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {
-        return getHandler().getChannel();
+        try {
+            return getOpenFileChecked().getMainStreamSafe().getChannel();
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }

Lines added: 5. Lines removed: 1. Tot = 6
—————————
Method found in diff:	-    public void checkReadable(Ruby runtime) throws IOException, BadDescriptorException, PipeException, InvalidValueException {
-    public void checkReadable(Ruby runtime) throws IOException, BadDescriptorException, PipeException, InvalidValueException {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public int getFileno(ChannelDescriptor descriptor) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {
-        Stream stream = openFile.getMainStream();
+        Stream stream = openFile.getMainStreamSafe();

Lines added: 1. Lines removed: 1. Tot = 2
—————————
Method found in diff:	public boolean isOpen() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkSafeString(IRubyObject object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newIOError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ThreadContext getCurrentContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newEOFError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Stream getMainStream() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void checkClosed(ThreadContext context) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {
-        return ((ChannelStream) openFile.getMainStream()).isBlocking();
+        try {
+            return ((ChannelStream) openFile.getMainStreamSafe()).isBlocking();
+        } catch (BadDescriptorException e) {
+            throw getRuntime().newErrnoEBADFError();
+        }

Lines added: 5. Lines removed: 1. Tot = 6
—————————
Method found in diff:	public int getMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFileStat newFileStat(String filename, boolean lstat) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum newFixnum(long value) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newErrnoEPIPEError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newArgumentError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newErrnoEINVALError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newIOErrorFromException(IOException ioe) {
+        if (ioe instanceof ClosedChannelException) {
+            throw newIOError("closed stream");
+        }
+

Lines added: 4. Lines removed: 0. Tot = 4
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public GlobalVariables getGlobalVariables() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray newArray() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newErrnoEBADFError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject setValue(IRubyObject newValue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newThreadError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/1b3665d0f  Fix JRUBY-6851: IO#set_e diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/1df3506c0  Fix for JRUBY-4908: IO.p diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	//    protected int fread(int len, ByteList buffer) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/20924a2ab  fix JRUBY-5436: File.ope diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/2270b5875  Fix JRUBY-5389: IO.popen diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/26351a1be  Fix JRUBY-5487: Kernel#s diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/2ae49952b  Fix IO#autoclose= to tak diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/2d497afa8  Fix JRUBY-6162 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/2e751d669  Fix JRUBY-5503: Timeout: diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
+    start_read()
-            context.getThread().beforeBlockingCall();
-            ByteList newBuffer = fread(length);
+            ByteList newBuffer = fread(context.getThread(), length);
-            context.getThread().afterBlockingCall();
-                        ByteList read = fread(ChannelStream.BUFSIZE);
+                        ByteList read = fread(thread, ChannelStream.BUFSIZE);
-    private ByteList fread(int length) throws IOException, BadDescriptorException {
+    private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {
-        ByteList buf = stream.fread(length);
-            ByteList newBuffer = stream.fread(rest);
+            return stream.fread(length);

Lines added containing method: 5. Lines removed containing method: 7. Tot = 12
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-    private ByteList fread(int length) throws IOException, BadDescriptorException {
-    private ByteList fread(int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/30095bb64  Fix JRUBY-5222 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/353fea102  Fix JRUBY-5876 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/387104937  Fix for JRUBY-4908: IO.p diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/3adbb9e36  Fix JRUBY-6205: 'Bad fil diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkReadable(Ruby runtime) throws IOException, BadDescriptorException, InvalidValueException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOpen() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void seek(long offset, int whence) throws IOException, InvalidValueException, PipeException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Stream getMainStream() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkClosed(Ruby runtime) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/3f56e551e  Fix JRUBY-7046 - always  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/5c55540d7  fix JRUBY-5400: [1.9] IO diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/5d094dbf5  Fix JRUBY-5688: Process: diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	-        public static RubyStatus newProcessStatus(Ruby runtime, long status) {
-        public static RubyStatus newProcessStatus(Ruby runtime, long status) {

Lines added: 0. Lines removed: 1. Tot = 1
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject taint(ThreadContext context, IRubyObject self) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject dup(IRubyObject self) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/6de4987a9  Rework the previous logi diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void checkClosed(ThreadContext context) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/73ce313ae  Revert "Fix JRUBY-6162" diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/7bca89905  Move org.jruby.ext.ffi.U diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/864087b8c  Fix JRUBY-5532: IO.forea diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/891409e4b  Improve and expand fix f diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static RubyString newStringShared(Ruby runtime, RubyString orig) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString convertToString() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private void checkClosed(ThreadContext context) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString(CharSequence s) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ByteList getByteList() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void modify() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setValue(CharSequence value) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/8fecee704  Fix JRUBY-6572 and unexc diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static RubyString newStringShared(Ruby runtime, RubyString orig) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString convertToString() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {
--- a/src/org/jruby/RubyString.java
+++ b/src/org/jruby/RubyString.java
+        if (enc == null) enc = ASCIIEncoding.INSTANCE;
+        if (enc == null) enc = ASCIIEncoding.INSTANCE;
--- a/src/org/jruby/util/io/EncodingOption.java
+++ b/src/org/jruby/util/io/EncodingOption.java
+    // c: rb_io_ext_int_to_encs
+        boolean defaultExt = false;
+            defaultExt = true;
-        if (intEncoding == null) {
+        if (intEncoding == null && extEncoding != ASCIIEncoding.INSTANCE) {
+            /* If external is ASCII-8BIT, no default transcoding */
-        // NOTE: This logic used to do checks for int == ext, etc, like in rb_io_ext_int_to_encs,
-        // but that logic seems specific to how MRI's IO sets up "enc" and "enc2". We explicitly separate
-        // external and internal, so consumers should decide how to deal with int == ext.
-        return new EncodingOption(extEncoding, intEncoding, isBom);
+        if (intEncoding == null || intEncoding == extEncoding) {
+            /* No internal encoding => use external + no transcoding */
+            return new EncodingOption(
+                    null,
+                    (defaultExt && intEncoding != extEncoding) ? null : extEncoding,
+                    isBom);
+        } else {
+            return new EncodingOption(
+                    extEncoding,
+                    intEncoding,
+                    isBom);
+        }
-                    runtime.getDefaultInternalEncoding(), false);
+                    null, false);
+
+    public String toString() {
+        return "EncodingOption(int:" + internalEncoding + ", ext:" + externalEncoding + ", bom:" + bom + ")";
+    }
--- a/test/externals/ruby1.9/excludes/TestIO_M17N.rb
+++ b/test/externals/ruby1.9/excludes/TestIO_M17N.rb
-exclude :test_binary, "needs investigation"
-exclude :test_cbuf_select, "needs investigation"
-exclude :test_file_foreach, "needs investigation"
-exclude :test_getc_ascii_only, "needs investigation"
+exclude :test_error_nonascii, "needs investigation"
-exclude :test_getc_newlineconv, "needs investigation"
-exclude :test_gets_nil, "needs investigation"
+exclude :test_inspect_nonascii, "needs investigation"
-exclude :test_io_new_enc, "needs investigation"
-exclude :test_marshal, "needs investigation"
-exclude :test_open_ascii, "needs investigation"
-exclude :test_open_nonascii, "needs investigation"
-exclude :test_open_r, "needs investigation"
-exclude :test_open_r_enc, "needs investigation"
-exclude :test_open_r_enc_enc, "needs investigation"
-exclude :test_open_r_enc_enc_in_opt, "needs investigation"
-exclude :test_open_r_enc_in_opt, "needs investigation"
-exclude :test_open_r_encname_encname, "needs investigation"
-exclude :test_open_r_encname_encname_in_opt, "needs investigation"
-exclude :test_open_r_externalencname_internalencname_in_opt, "needs investigation"
-exclude :test_open_rb, "needs investigation"
-exclude :test_open_w_enc, "needs investigation"
-exclude :test_open_w_enc_enc, "needs investigation"
-exclude :test_open_w_enc_enc_in_opt, "needs investigation"
-exclude :test_open_w_enc_enc_in_opt2, "needs investigation"
-exclude :test_open_w_enc_enc_perm, "needs investigation"
-exclude :test_open_w_enc_in_opt, "needs investigation"
-exclude :test_open_wb, "needs investigation"
-exclude :test_pipe_conversion, "needs investigation"
-exclude :test_pipe_convert_partial_read, "needs investigation"
-exclude :test_pipe_terminator_conversion, "needs investigation"
-exclude :test_popen_r_enc, "needs investigation"
-exclude :test_read_encoding, "needs investigation"
-exclude :test_read_newline_conversion_without_encoding_conversion, "needs investigation"
-exclude :test_s_foreach_enc, "needs investigation"
-exclude :test_s_foreach_enc_enc, "needs investigation"
-exclude :test_s_foreach_enc_enc_in_opt, "needs investigation"
-exclude :test_s_foreach_enc_enc_in_opt2, "needs investigation"
-exclude :test_s_foreach_enc_in_opt, "needs investigation"
-exclude :test_set_encoding, "needs investigation"
-exclude :test_set_encoding2, "needs investigation"
-exclude :test_set_encoding_nil, "needs investigation"
-exclude :test_strip_bom, "needs investigation"
-exclude :test_terminator_conversion, "needs investigation"
-exclude :test_textmode_encode_newline, "needs investigation"
-exclude :test_write_ascii_incompat, "needs investigation"
-exclude :test_write_mode_fail, "needs investigation"
-exclude :test_write_noenc, "needs investigation"
-exclude :test_error_nonascii, "needs investigation"
-exclude :test_inspect_nonascii, "needs investigation"

Lines added: 29. Lines removed: 57. Tot = 86
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString(CharSequence s) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public final void modify() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject dup() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/99a5b3189  Additional fix for JRUBY diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
+  it "ZOMG: should read 4 bytes for read(4)" do
+        value = sock.read(4)

Lines added containing method: 2. Lines removed containing method: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(int length) throws IOException, BadDescriptorException {
+        int rest = length;
-        if (buf != null && buf.length() == length) {
-            return buf;
+        if (buf != null) {
+            rest -= buf.length();
-        int rest = length;

Lines added: 3. Lines removed: 3. Tot = 6
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/ac992dfaa  Fix JRUBY-5888: missing  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/b7c3cc24b  Fix JRUBY-6212: IO#inspe diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/b89d8a699  Fix JRUBY-4595: [1.9] Fi diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
+    public static IRubyObject binread(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
+            return !length.isNil() ? file.read(context, length) : file.read(context);

Lines added containing method: 2. Lines removed containing method: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/bcc26747f  Use RubyModule.JavaClass diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public IRubyObject select(ThreadContext context, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public double getDoubleValue() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static RubyString newStringShared(Ruby runtime, RubyString orig) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isInstance(IRubyObject object) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString convertToString() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public double yield(RubyString arg, boolean strict);

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public String getFile() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public List getList() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static RubyFixnum newFixnum(Ruby runtime, long value) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyInteger convertToInteger() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static int num2int(IRubyObject arg) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString(CharSequence s) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static final RubyArray newArray(final Ruby runtime, final long len) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private final void modify() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject taint(ThreadContext context) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject round() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object setValue(Object value) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject dup() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/c0615e746  Fix for JRUBY-6101. diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object callMethod(Object receiver, String methodName, Object... args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
--- a/src/org/jruby/embed/ScriptingContainer.java
+++ b/src/org/jruby/embed/ScriptingContainer.java
-        RubyIO io = new RubyIO(runtime, pstream);
+        RubyIO io = new RubyIO(runtime, pstream, false);
-        RubyIO io = new RubyIO(runtime, error);
+        RubyIO io = new RubyIO(runtime, error, false);
--- a/src/org/jruby/embed/jsr223/Utils.java
+++ b/src/org/jruby/embed/jsr223/Utils.java
+
-        
+
-        
+
-        
+

Lines added: 8. Lines removed: 7. Tot = 15
—————————
Method found in diff:	public Ruby getRuntime() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Object remove(String key) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/c0b57cdb8  Fix JRUBY-4913 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/c725e7dae  Also fix JRUBY-6180 on W diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/cb777c23f  Partial fix for JRUBY-64 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public RubyString newStringShared(ByteList byteList) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getFileno(ChannelDescriptor descriptor) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList readall() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newIOError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ThreadContext getCurrentContext() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newEOFError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFileStat newFileStat(String filename, boolean lstat) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum newFixnum(long value) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newErrnoEPIPEError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newArgumentError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyString newString() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor getDescriptor() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newErrnoEINVALError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newIOErrorFromException(IOException ioe) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public GlobalVariables getGlobalVariables() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray newArray() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newErrnoEBADFError() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public IRubyObject setValue(IRubyObject newValue) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RaiseException newThreadError(String message) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/d60304cf1  Fix for JRUBY-5193: a Fi diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public FileDescriptor getFileDescriptor() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getFileno() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized ByteList fread(int number) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOpen() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor getDescriptor() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor dup() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public synchronized int fgetc() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/daa36aa25  Partially fix JRUBY-6893 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/dc2ff992f  Fix JRUBY-5685: IO.popen diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private ByteList fread(RubyThread thread, int length) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void run() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int waitFor() throws InterruptedException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/ebbfbacfc  Revert "Fix JRUBY-6974" diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/ed560a8ca  Fix JRUBY-6198: When cal diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkReadable() throws IOException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ByteList fread(int number) throws IOException, BadDescriptorException, EOFException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ByteList readall() throws IOException, BadDescriptorException, EOFException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean isOpen() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void setSync(boolean sync) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Stream getMainStream() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public void checkClosed(Ruby runtime) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getMode() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Ruby getRuntime() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean readDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public ChannelDescriptor getDescriptor() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int getline(ByteList dst, byte terminator) throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean feof() throws IOException, BadDescriptorException {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public int fgetc() throws IOException, BadDescriptorException, EOFException {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/efe615a2a  IO.copy_stream should ha diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(
+                        read = io1.read(context, runtime.getNil()).convertToString();
+                        read = io1.read(context, length).convertToString();

Lines added containing method: 2. Lines removed containing method: 0. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public RubyArray readlines(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/f5e2f5fd0  Fix JRUBY-6974 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public RubyFixnum seek(ThreadContext context, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject open(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean getBlocking() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
12/After/f8408dac2  Fix JRUBY-5348 diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
read(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* cancel
* select
* getDoubleValue
* channel
* newStringShared
* isInstance
* iterator
* getChannel
* getFileDescriptor
* checkReadable
* convertToString
* getFileno
* fread
* readall
* isOpen
* newProcessStatus
* callMethod
* selectedKeys
* seek
* setSync
* checkSafeString
* getNil
* newIOError
* readyOps
* yield
* validOps
* writeDataBuffered
* getCurrentContext
* getMessage
* getNilClass
* newEOFError
* addAll
* getMainStream
* isNil
* readlines
* open
* configureBlocking
* register
* selectNow
* checkClosed
* run
* source
* getFile
* getList
* getBlocking
* getMode
* setTaint
* getRuntime
* waitFor
* newFileStat
* respondsTo
* newFixnum
* sink
* convertToInteger
* newErrnoEPIPEError
* newArgumentError
* num2int
* newString
* readDataBuffered
* isGiven
* keyFor
* interestOps
* getByteList
* getDescriptor
* keys
* remove
* attachment
* newErrnoEINVALError
* newIOErrorFromException
* getline
* blockingLock
* getGlobalVariables
* newArray
* getString
* modify
* newErrnoEBADFError
* taint
* round
* setValue
* feof
* newThreadError
* dup
* fgetc
* append
—————————
Method found in diff:	public static IRubyObject select(ThreadContext context, IRubyObject recv, IRubyObject[] args) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public Channel getChannel() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public boolean writeDataBuffered() {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static RubyArray readlines(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block unusedBlock) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	private IRubyObject getline(Ruby runtime, ByteList separator, ByteListCache cache) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject taint(ThreadContext context, IRubyObject self) {

Lines added: 0. Lines removed: 0. Tot = 0
—————————
Method found in diff:	public static IRubyObject dup(IRubyObject self) {

Lines added: 0. Lines removed: 0. Tot = 0
********************************************
********************************************
